package com.staffs.backend.billLog.service.implementation;

import com.staffs.backend.billLog.dto.BillLogDTO;
import com.staffs.backend.billLog.dto.BillLogListDTO;
import com.staffs.backend.billLog.dto.BillLogRequestDTO;
import com.staffs.backend.billLog.service.BillLogService;
import com.staffs.backend.entity.billLog.BillLog;
import com.staffs.backend.entity.billingSetup.model.BillingSetup;
import com.staffs.backend.entity.item.Item;
import com.staffs.backend.entity.packages.Packages;
import com.staffs.backend.entity.paymentSource.PaymentSource;
import com.staffs.backend.entity.regionRate.RegionRate;
import com.staffs.backend.exceptions.GeneralException;
import com.staffs.backend.general.dto.MessageConstant;
import com.staffs.backend.general.dto.PageableRequestDTO;
import com.staffs.backend.general.enums.ResponseCodeAndMessage;
import com.staffs.backend.general.service.GeneralService;
import com.staffs.backend.item.dto.ItemDTO;
import com.staffs.backend.item.service.ItemService;
import com.staffs.backend.packages.dto.PackageDTO;
import com.staffs.backend.packages.service.PackageService;
import com.staffs.backend.paymentIntegration.dto.PaymentResponseDTO;
import com.staffs.backend.paymentIntegration.service.PaymentService;
import com.staffs.backend.paymentSource.service.PaymentSourceService;
import com.staffs.backend.regionRate.service.RegionRateService;
import com.staffs.backend.repository.billLog.BillLogRepository;
import com.staffs.backend.repository.billingSetup.BillingSetupRepository;
import com.staffs.backend.utils.GeneralUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BillLogServiceImpl implements BillLogService {

    private final ItemService itemService;
    private final GeneralService generalService;
    private final PackageService packageService;
    private final PaymentService paymentService;
    private final RegionRateService regionRateService;
    private final BillLogRepository billLogRepository;
    private final PaymentSourceService paymentSourceService;
    private final BillingSetupRepository billingSetupRepository;

    @Override
    public BillLogDTO logBillingDetail(BillLogRequestDTO dto) {
        log.info("saving bill log info!");

        //get item
        Item item = itemService.getItemById(dto.getItemId());

        //check is item is standalone
        if (!item.isStandalone()) {
            throw new GeneralException(ResponseCodeAndMessage.BAD_REQUEST.responseCode , MessageConstant.ITEM_IS_NOT_STANDALONE);
        }

        //get bill setup
        BillingSetup billingSetup = billingSetupRepository.findByAccountIdAndValidateAndPackages_packageName(dto.getAccountId() , true , dto.getPackageName());

        if (Objects.nonNull(billingSetup)) {
            log.info("Saving new bill log => {} {}" , dto.getAccountId() , item.getItemName());

            //check item quantity
            Long quantity = dto.getItemQuantity();
            if (quantity <= 0) {
                throw new GeneralException(ResponseCodeAndMessage.BAD_REQUEST.responseCode , MessageConstant.ITEM_QUANTITY_CAN_NOT_BE_LESS_THAN_1);
            }

            //check item cap price
            double capPrice = item.getItemPrice();
            double minPrice = item.getItemMinPrice();
            double amountPay = dto.getChargeAmount();
            double billPrice = amountPay / quantity;

            if (billPrice > capPrice || billPrice < minPrice) {
                throw new GeneralException(ResponseCodeAndMessage.BAD_REQUEST.responseCode , MessageConstant.BILL_PRICE_CANNOT_BE_GREATER_LESS_THAN_CAP_MIN_PRICE);
            }

            String currency = GeneralUtil.getCurrency("default");

            //get bill PackageDTO
            PackageDTO packageDTO = packageService.getBillPackage(billingSetup.getBillId());

            //check source region rate for mpesa
            if (dto.getPaymentRequest().getPaymentSource().equals("MPESA") && regionRateService.isRegionRate("Kenya" , packageDTO.getCurrentBillRates().getVersionNo()) && amountPay > 0) {
                throw new GeneralException(ResponseCodeAndMessage.BAD_REQUEST.responseCode , MessageConstant.PACKAGE_REGION_RATE_FOR_COUNTRY_KENYA_IS_NEEDED_TO_BE_ABLE_TO_PAY_THROUGH_THIS_SOURCE);
            }

            //if there is region rate
            if (!dto.getPaymentRequest().getPaymentSource().equals("MPESA") && regionRateService.isRegionRate(dto.getPaymentRequest().getCountry() , packageDTO.getCurrentBillRates().getVersionNo())) {
                if (Objects.nonNull(regionRateService.getSingleRegionRate(dto.getPaymentRequest().getCountry() , packageDTO.getCurrentBillRates().getVersionNo())) && amountPay > 0) {
                    RegionRate region = (regionRateService.getSingleRegionRate(dto.getPaymentRequest().getCountry() , packageDTO.getCurrentBillRates().getVersionNo()));
                    double regionRate = region.getRate();

                    if (regionRate > 0) {
                        amountPay = amountPay * regionRate;

                        //set region currency
                        currency = GeneralUtil.getCurrency(dto.getPaymentRequest().getCountry());
                    }
                }
            } else if (dto.getPaymentRequest().getPaymentSource().equals("MPESA")) {
                double regionRate = regionRateService.getSingleRegionRate("Kenya" , packageDTO.getCurrentBillRates().getVersionNo()).getRate();
                if (regionRate > 0) {
                    amountPay = amountPay * regionRate;

                    //set region currency
                    currency = GeneralUtil.getCurrency("Kenya");
                }
            }

            BillLog billLog = new BillLog();
            BeanUtils.copyProperties(dto , billLog);
            billLog.setBillLogId(UUID.randomUUID().toString());
            billLog.setItem(item);
            billLog.setBillingSetup(billingSetup);
            billLog.setCreatedAt(LocalDateTime.now());
            billLog.setPaymentOrigin(paymentSourceService.getSingleSource(dto.getPaymentRequest().getPaymentSource()));
            billLog.setChargeAmount(amountPay);
            billLog.setCurrency(currency);
            billLog.setPaymentOrigin(paymentSourceService.getSingleSource(dto.getPaymentRequest().getPaymentSource()));

            BillLogDTO billLogDTO = getBillLogDTO(billLog);

            //check if service is payable or covered by bill subscription
            int res = checkAvailableService(item , billingSetup , dto.getItemQuantity());

            // make payment if there is charge amount
            if (amountPay > 0 && res < 0) {

                Packages packages = packageService.getPackageByName(dto.getPackageName());

                //set payment params
                dto.getPaymentRequest().setAccountId(dto.getAccountId());
                if (dto.getPaymentRequest().getPaymentSource().equals("MPESA")) {
                    dto.getPaymentRequest().setAmount(String.valueOf(Math.round(amountPay)));
                } else {
                    dto.getPaymentRequest().setAmount(String.valueOf(amountPay));
                }
                dto.getPaymentRequest().setServiceDate(LocalDateTime.now().toString());
                dto.getPaymentRequest().setDescription(dto.getPackageName());
                dto.getPaymentRequest().setTransRef(UUID.randomUUID().toString());
                dto.getPaymentRequest().setClient(packages.getPackageType().getLicenseType().getClient().getClientName());
                dto.getPaymentRequest().setPaymentType("");
                dto.getPaymentRequest().setMethod("");
                dto.getPaymentRequest().setEmail(billingSetup.getEmail());
                dto.getPaymentRequest().setCurrency(currency);

                //check a source
                if (dto.getPaymentRequest().getPaymentSource().equals("MPESA") && Objects.nonNull(dto.getPaymentRequest().getPhoneNumber())) {
                    if (!dto.getPaymentRequest().getPhoneNumber().contains("254")) {
                        throw new GeneralException(ResponseCodeAndMessage.BAD_REQUEST.responseCode , MessageConstant.PHONE_NUMBER_NEED_TO_BE_KENYA_WITH_254_COUNTRY_CODE);
                    }
                } else if (dto.getPaymentRequest().getPaymentSource().equals("MPESA")) {
                    throw new GeneralException(ResponseCodeAndMessage.BAD_REQUEST.responseCode , MessageConstant.PHONE_NUMBER_NEED_TO_BE_PROVIDED_FOR_MPESA);
                }

                //make payment
                PaymentResponseDTO paymentResponse = paymentService.pushPayment(dto.getPaymentRequest());

                if (Objects.nonNull(paymentResponse.getPaymentUrl())) {
                    billLogDTO.setPaymentURL(paymentResponse.getPaymentUrl());
                    billLogDTO.setTransRef(paymentResponse.getTransactionRef());

                    billLog.setTransRef(paymentResponse.getTransactionRef());
                    billLog.setPaymentStatus("Payment checkout url sent");
                    billLog.setPaymentSuccess(false);

                    //save to DB
                    billLogRepository.save(billLog);

                    billLogDTO.setPaymentStatus("Payment checkout url sent");

                    //save to DB
                    billLogRepository.save(billLog);

                } else {

                    throw new GeneralException(ResponseCodeAndMessage.BAD_REQUEST.responseCode , MessageConstant.PAYMENT_FAILED);

                }

            } else {

                billLog.setPaymentStatus("Payment not required");
                billLog.setPaymentSuccess(true);

                //save to DB
                billLogRepository.save(billLog);

                billLogDTO.setPaymentStatus("Payment not required");

                //save to DB
                billLogRepository.save(billLog);

            }

            return billLogDTO;

        } else {
            throw new GeneralException(ResponseCodeAndMessage.BAD_REQUEST.responseCode , MessageConstant.INVALID_REQUEST_PARAMS);
        }

    }

    @Override
    public BillLogDTO getSingleDTOBillLog(String billLogId) {
        return getBillLogDTO(getSingleBillLogById(billLogId));
    }

    @Override
    public BillLogListDTO getBillLogByAccountId(PageableRequestDTO requestDTO , String accountId) {
        log.info("getting BillLog DTOs info by account ID");

        Pageable pageable = generalService.getPageableObject(requestDTO.getSize() , requestDTO.getPage());
        List<BillLog> billLogs = getBillLogsByAccountId(accountId);
        Page<BillLog> billLogPage = new PageImpl<>(billLogs , pageable , billLogs.size());

        return getBillLogListDTO(billLogPage);

    }

    @Override
    public List<BillLogDTO> getBillLogByAccountIdAndBillSetup(String accountId , String packageName) {
        log.info("getting BillLog DTOs info by bill setup");

        //get bill setup
        BillingSetup billingSetup = billingSetupRepository.findByAccountIdAndValidateAndPackages_packageName(accountId , true , packageName);

        List<BillLog> billLogs = getBillLogsByAccountIdAndBillSetup(accountId , billingSetup);

        return billLogs.stream().map(this::getBillLogDTO).collect(Collectors.toList());

    }

    @Override
    public List<BillLogDTO> getBillLogsByAccountId4Bill(String accountId) {
        log.info("getting BillLog DTOs info by account for bill");

        List<BillLog> billLogs = billLogRepository.findByAccountIdOrderByCreatedAtDesc(accountId);

        return billLogs.stream().map(this::getBillLogDTO).collect(Collectors.toList());
    }

    @Override
    public BillLogListDTO getBillLogByAccountIdAndItem(PageableRequestDTO requestDTO , String accountId , Long itemId) {
        log.info("getting BillLog DTOs info by Item");

        //get item
        Item item = itemService.getItemById(itemId);

        Pageable pageable = generalService.getPageableObject(requestDTO.getSize() , requestDTO.getPage());
        List<BillLog> billLogs = getBillLogsByAccountIdAndItem(accountId , item);
        Page<BillLog> billLogPage = new PageImpl<>(billLogs , pageable , billLogs.size());

        return getBillLogListDTO(billLogPage);

    }

    private BillLog getSingleBillLogById(String billLogId) {
        return billLogRepository.findByBillLogId(billLogId).orElseThrow(() -> new GeneralException(ResponseCodeAndMessage.RECORD_NOT_FOUND.responseCode , MessageConstant.RECORD_NOT_FOUND));
    }

    private List<BillLog> getBillLogsByAccountIdAndBillSetup(String accountId , BillingSetup billingSetup) {
        return billLogRepository.findByAccountIdAndBillingSetupOrderByCreatedAtDesc(accountId , billingSetup);
    }

    private List<BillLog> getBillLogsByAccountIdAndItem(String accountId , Item item) {
        return billLogRepository.findByAccountIdAndItemOrderByCreatedAtDesc(accountId , item);
    }

    private List<BillLog> getBillLogsByAccountId(String accountId) {
        return billLogRepository.findByAccountIdOrderByCreatedAtDesc(accountId);
    }

    private BillLogDTO getBillLogDTO(BillLog billLog) {
        log.info("converting billLog to billLogDTO");

        BillLogDTO billLogDTO = new BillLogDTO();
        BeanUtils.copyProperties(billLog , billLogDTO);

        //get item info
        billLogDTO.setItemDTO(itemService.getItemDTOById(billLog.getItem().getItemId()));

        //get payment source
        PaymentSource paymentSource = billLog.getPaymentOrigin();
        billLogDTO.setPaymentSource(paymentSourceService.getPaymentDTO(paymentSource));

        return billLogDTO;
    }

    private int checkAvailableService(Item item , BillingSetup billingSetup , Long quantity) {
        log.info("checking if item is available within the subscription");

        int left = 0;

        if (item.isStandalone()) {
            PackageDTO packages = packageService.getBillPackage(billingSetup.getBillId());

            //getting bill item
            List<ItemDTO> billItems = packages.getItemList();
            ItemDTO billItemDTO = new ItemDTO();
            //get bill item
            for (ItemDTO billItem : billItems) {
                if (billItem.getItemId().equals(item.getItemId())) {
                    billItemDTO = itemService.getItemDTOByNameAndPackageName(item.getItemName() , packages.getPackageName());
                }
            }

            if (Objects.nonNull(billItemDTO.getQuantity())) {
                //get used item quantity
                Long usedItemQuantity = billLogRepository.getUsedItemQuantity(billingSetup.getBillId().toString().replace("-" , "") , item.getItemId());
                log.info("here please {} {} {}" , billItemDTO.getQuantity() , usedItemQuantity , billItemDTO);

                if (Objects.isNull(usedItemQuantity)) {
                    usedItemQuantity = 0L;
                }

                //subtract log item from bill item
                left = Math.toIntExact(billItemDTO.getQuantity() - usedItemQuantity - quantity);
            }
        }

        return left;
    }

    private BillLogListDTO getBillLogListDTO(Page<BillLog> billLogPage) {
        log.info("Converting BillLog page to BillLog list dto");

        BillLogListDTO billLogListDTO = new BillLogListDTO();

        List<BillLog> billLogs = billLogPage.getContent();
        if (!billLogs.isEmpty()) {
            billLogListDTO.setHasNextRecord(billLogPage.hasNext());
            billLogListDTO.setTotalCount((int) billLogPage.getTotalElements());
        }

        List<BillLogDTO> billLogDTOs = billLogs.stream().map(this::getBillLogDTO).collect(Collectors.toList());
        billLogListDTO.setBillLogDTOs(billLogDTOs);

        return billLogListDTO;
    }

}
