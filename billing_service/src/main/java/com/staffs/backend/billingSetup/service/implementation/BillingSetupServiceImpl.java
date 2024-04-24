package com.staffs.backend.billingSetup.service.implementation;

import com.staffs.backend.billLog.dto.BillLogDTO;
import com.staffs.backend.billLog.dto.BillLogRequestDTO;
import com.staffs.backend.billLog.service.BillLogService;
import com.staffs.backend.billingMethod.service.BillingMethodService;
import com.staffs.backend.billingSetup.dto.BillingSetupDTO;
import com.staffs.backend.billingSetup.dto.BillingSetupListDTO;
import com.staffs.backend.billingSetup.dto.BillingSetupRequestDTO;
import com.staffs.backend.billingSetup.service.BillingSetupService;
import com.staffs.backend.coupon.service.CouponService;
import com.staffs.backend.entity.billingMethod.BillingMethod;
import com.staffs.backend.entity.billingSetup.iModel.BillingSetupBasicInfoI;
import com.staffs.backend.entity.billingSetup.model.BillingSetup;
import com.staffs.backend.entity.licenseType.LicenseType;
import com.staffs.backend.entity.licenseUpgrade.LicenseUpgrade;
import com.staffs.backend.entity.packages.Packages;
import com.staffs.backend.entity.paymentSource.PaymentSource;
import com.staffs.backend.entity.regionRate.RegionRate;
import com.staffs.backend.exceptions.GeneralException;
import com.staffs.backend.general.dto.BillChargeDTO;
import com.staffs.backend.general.dto.MessageConstant;
import com.staffs.backend.general.enums.ResponseCodeAndMessage;
import com.staffs.backend.item.dto.ItemDTO;
import com.staffs.backend.item.service.ItemService;
import com.staffs.backend.licenseUpgrade.dto.LicenseUpgradeRequestDTO;
import com.staffs.backend.packageRate.dto.PackageRateDTO;
import com.staffs.backend.packages.dto.PackageDTO;
import com.staffs.backend.packages.service.PackageService;
import com.staffs.backend.paymentIntegration.dto.PaymentRequestDTO;
import com.staffs.backend.paymentIntegration.dto.PaymentResponseDTO;
import com.staffs.backend.paymentIntegration.service.PaymentService;
import com.staffs.backend.paymentSource.service.PaymentSourceService;
import com.staffs.backend.regionRate.service.RegionRateService;
import com.staffs.backend.repository.billingSetup.BillingSetupRepository;
import com.staffs.backend.repository.licenseUpgrade.LicenseUpgradeRepository;
import com.staffs.backend.utils.GeneralUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BillingSetupServiceImpl implements BillingSetupService {

    private final ItemService itemService;
    private final CouponService couponService;
    private final BillLogService billLogService;
    private final PackageService packageService;
    private final PaymentService paymentService;
    private final RegionRateService regionRateService;
    private final BillingMethodService billingMethodService;
    private final PaymentSourceService paymentSourceService;
    private final BillingSetupRepository billingSetupRepository;
    private final LicenseUpgradeRepository licenseUpgradeRepository;

    @Override
    public BillingSetupDTO setupAccountBill(BillingSetupRequestDTO dto) {
        log.info("saving billing setup info!");

        if (Objects.isNull(dto.getFirstName()) || dto.getFirstName().isEmpty()) {
            throw new GeneralException(ResponseCodeAndMessage.BAD_REQUEST.responseCode , MessageConstant.FIRSTNAME_CAN_NOT_BE_NULL_NOR_EMPTY);
        }

        if (Objects.nonNull(dto.getPhone()) && Objects.nonNull(dto.getEmail())) {
            if (dto.getPhone().isEmpty() || dto.getEmail().isEmpty()) {
                throw new GeneralException(ResponseCodeAndMessage.BAD_REQUEST.responseCode , MessageConstant.EMAIL_OR_PHONE_NUMBER_MUST_BE_PROVIDED);
            }
        }

        //account id
        String accountId = dto.getAccountId();

        //get package
        Packages packages = packageService.getPackageByName(dto.getPackageName());

        log.info("getting params!");
        //get package name from package
        String packageName = packages.getPackageName();
        //get duration from package
        Long duration = packages.getDuration();

        //get package DTO
        PackageDTO packageDTO = packageService.getPackageDTOByName(packageName);

        //get bill rate
        PackageRateDTO packageRateDTO = packageDTO.getCurrentBillRates();

        //check if there is a valid bill rate
        if (Objects.isNull(packageRateDTO)) {
            throw new GeneralException(ResponseCodeAndMessage.BAD_REQUEST.responseCode , MessageConstant.BILL_RATE_NOT_FOUND_FOR_PACKAGE);
        }

        //get rate from package rate
        double rate = packageRateDTO.getRate();

        //charge amount
        double charge = duration * rate;
        String currency = GeneralUtil.getCurrency("default");

        //check source region rate for mpesa
        if (dto.getPaymentRequest().getPaymentSource().equals("MPESA") && !regionRateService.isRegionRate("Kenya" , packageRateDTO.getVersionNo()) && charge > 0) {
            throw new GeneralException(ResponseCodeAndMessage.BAD_REQUEST.responseCode , MessageConstant.PACKAGE_REGION_RATE_FOR_COUNTRY_KENYA_IS_NEEDED_TO_BE_ABLE_TO_PAY_THROUGH_THIS_SOURCE);
        }

        //if there is region rate
        if (!dto.getPaymentRequest().getPaymentSource().equals("MPESA") && regionRateService.isRegionRate(dto.getPaymentRequest().getCountry() , packageRateDTO.getVersionNo())) {
            if (Objects.nonNull(regionRateService.getSingleRegionRate(dto.getPaymentRequest().getCountry() , packageRateDTO.getVersionNo())) && charge > 0) {
                RegionRate region = (regionRateService.getSingleRegionRate(dto.getPaymentRequest().getCountry() , packageRateDTO.getVersionNo()));
                double regionRate = region.getRate();

                if (regionRate > 0) {
                    charge = duration * regionRate;

                    //set region currency
                    currency = GeneralUtil.getCurrency(dto.getPaymentRequest().getCountry());
                }
            }
        } else if (dto.getPaymentRequest().getPaymentSource().equals("MPESA")) {
            double regionRate = regionRateService.getSingleRegionRate("Kenya" , packageRateDTO.getVersionNo()).getRate();
            if (regionRate > 0) {
                charge = duration * regionRate;

                //set region currency
                currency = GeneralUtil.getCurrency("Kenya");
            }
        }

        //get a license type of package
        LicenseType licenseType = packages.getPackageType().getLicenseType();

        //get billing method
        BillingMethod billingMethod = billingMethodService.getBillingMethodById(dto.getBillingMethodId());
        //billing method id
        Long billingMethodId = dto.getBillingMethodId();

        if (Objects.nonNull(billingMethod)) {

            if (!billingSetupRepository.existsByAccountIdAndPackages_packageNameAndPackages_packageType_licenseTypeAndPaymentSuccess(accountId , packageName , licenseType , false)) {

                if (!billingSetupRepository.existsByAccountIdAndPackages_packageType_licenseTypeAndValidate(accountId , licenseType , true)) {
                    log.info("saving new billing setup => {} {} {}" , accountId , packageName , billingMethodId);

                    BillingSetup billingSetup = new BillingSetup();
                    BeanUtils.copyProperties(dto , billingSetup);
                    billingSetup.setBillingMethod(billingMethod);
                    billingSetup.setValidate(false);
                    billingSetup.setLicenseUpgrade(false);
                    billingSetup.setValidFrom(LocalDateTime.now());
                    billingSetup.setPackages(packages);
                    billingSetup.setPaymentSuccess(false);

                    billingSetup.setValidTill(LocalDateTime.now().plusDays(duration));
                    billingSetup.setCreatedAt(LocalDateTime.now());
                    billingSetup.setChargeAmount(charge);
                    billingSetup.setCurrency(currency);
                    billingSetup.setBillId(UUID.randomUUID());
                    billingSetup.setCountry(dto.getPaymentRequest().getCountry());

                    //get payment source
                    PaymentSource paymentSource = paymentSourceService.getSingleSource(dto.getPaymentRequest().getPaymentSource());

                    billingSetup.setPaymentSource(paymentSource);

                    BillingSetupDTO billingSetupDTO = getBillingSetupDTO(billingSetup);

                    if (charge > 0) {

                        //set payment params
                        if (dto.getPaymentRequest().getPaymentSource().equals("MPESA")) {
                            dto.getPaymentRequest().setAmount(String.valueOf(Math.round(charge)));
                        } else {
                            dto.getPaymentRequest().setAmount(String.valueOf(charge));
                        }
                        dto.getPaymentRequest().setServiceDate(LocalDateTime.now().toString());
                        dto.getPaymentRequest().setAccountId(accountId);
                        dto.getPaymentRequest().setDescription(packageName);
                        dto.getPaymentRequest().setTransRef(UUID.randomUUID().toString());
                        dto.getPaymentRequest().setClient(packages.getPackageType().getLicenseType().getClient().getClientName());
                        dto.getPaymentRequest().setPaymentType("");
                        dto.getPaymentRequest().setMethod("");
                        dto.getPaymentRequest().setCurrency(currency);
                        dto.getPaymentRequest().setEmail(dto.getEmail());

                        //check a source
                        if (dto.getPaymentRequest().getPaymentSource().equals("MPESA") && Objects.nonNull(dto.getPaymentRequest().getPhoneNumber())) {
                            if (!dto.getPaymentRequest().getPhoneNumber().contains("254")) {
                                throw new GeneralException(ResponseCodeAndMessage.BAD_REQUEST.responseCode , MessageConstant.PHONE_NUMBER_NEED_TO_BE_KENYA_WITH_254_COUNTRY_CODE);
                            }
                        } else if (dto.getPaymentRequest().getPaymentSource().equals("MPESA")) {
                            throw new GeneralException(ResponseCodeAndMessage.BAD_REQUEST.responseCode , MessageConstant.PHONE_NUMBER_NEED_TO_BE_PROVIDED_FOR_MPESA);
                        }

                        //make payment
                        PaymentResponseDTO paymentResponse = callPushPaymentService(dto.getPaymentRequest());

                        if (Objects.nonNull(paymentResponse.getPaymentUrl()) || (paymentResponse.getStatus() == 200 && dto.getPaymentRequest().getPaymentSource().equals("MPESA"))) {
                            billingSetupDTO.setPaymentUrl(paymentResponse.getPaymentUrl());
                            billingSetupDTO.setTransactionRef(paymentResponse.getTransactionRef());

                            if (dto.getPaymentRequest().getPaymentSource().equals("MPESA")) {
                                billingSetupDTO.setTransactionRef(dto.getPaymentRequest().getTransRef());
                                paymentResponse.setPaymentUrl("https://mpesa.com");
                            }

                            billingSetup.setPaymentRef(paymentResponse.getTransactionRef());

                            billingSetup = billingSetupRepository.save(billingSetup);

                            //save to DB
                            billingSetupRepository.save(billingSetup);

                        } else {

                            billingSetup.setPaymentSuccess(false);

                            //save to DB
                            billingSetupRepository.save(billingSetup);
                            throw new GeneralException(ResponseCodeAndMessage.BAD_REQUEST.responseCode , MessageConstant.PAYMENT_FAILED);
                        }
                    } else {
                        billingSetup.setValidate(true);
                        billingSetup.setPaymentSuccess(true);

                        //save to DB
                        billingSetup = billingSetupRepository.save(billingSetup);

                        //generate coupons
                        logBillCoupon(billingSetup , packageDTO);

                        //save to DB
                        billingSetupRepository.save(billingSetup);

                    }

                    return billingSetupDTO;

                } else {
                    throw new GeneralException(ResponseCodeAndMessage.BAD_REQUEST.responseCode , MessageConstant.ACCOUNT_ALREADY_HAS_A_VALID_BILL_SETUP_ON_THIS_LICENSE_TYPE_AND_PACKAGE);
                }

            } else {
                log.info("updating billing setup for setup without/failed payment => {} {} {}" , accountId , packageName , billingMethodId);

                if (billingSetupRepository.existsByAccountIdAndPackages_packageType_licenseTypeAndValidate(accountId , licenseType , true)) {
                    throw new GeneralException(ResponseCodeAndMessage.BAD_REQUEST.responseCode , MessageConstant.ACCOUNT_ALREADY_HAS_A_VALID_BILL_SETUP_ON_THIS_LICENSE_TYPE_AND_PACKAGE);
                }

                BillingSetup billingSetup = billingSetupRepository.findFirstByAccountIdAndPackages_packageNameAndPackages_packageType_licenseTypeAndPaymentSuccessOrderByCreatedAtDesc(accountId , packageName , licenseType , false);

                //set payment params
                if (dto.getPaymentRequest().getPaymentSource().equals("MPESA")) {
                    dto.getPaymentRequest().setAmount(String.valueOf(Math.round(charge)));
                } else {
                    dto.getPaymentRequest().setAmount(String.valueOf(charge));
                }
                dto.getPaymentRequest().setAccountId(accountId);
                dto.getPaymentRequest().setServiceDate(LocalDateTime.now().toString());
                dto.getPaymentRequest().setDescription(packageName);
                dto.getPaymentRequest().setTransRef(UUID.randomUUID().toString());
                dto.getPaymentRequest().setClient(packages.getPackageType().getLicenseType().getClient().getClientName());
                dto.getPaymentRequest().setPaymentType("");
                dto.getPaymentRequest().setMethod("");
                dto.getPaymentRequest().setCurrency(currency);
                dto.getPaymentRequest().setEmail(dto.getEmail());

                //check the source
                if (dto.getPaymentRequest().getPaymentSource().equals("MPESA") && Objects.nonNull(dto.getPaymentRequest().getPhoneNumber())) {
                    if (!dto.getPaymentRequest().getPhoneNumber().contains("254")) {
                        throw new GeneralException(ResponseCodeAndMessage.BAD_REQUEST.responseCode , MessageConstant.PHONE_NUMBER_NEED_TO_BE_KENYA_WITH_254_COUNTRY_CODE);
                    }
                } else if (dto.getPaymentRequest().getPaymentSource().equals("MPESA")) {
                    throw new GeneralException(ResponseCodeAndMessage.BAD_REQUEST.responseCode , MessageConstant.PHONE_NUMBER_NEED_TO_BE_PROVIDED_FOR_MPESA);
                }

                //make payment
                PaymentResponseDTO paymentResponse = callPushPaymentService(dto.getPaymentRequest());

                BillingSetupDTO billingSetupDTO = getBillingSetupDTO(billingSetup);

                if (Objects.nonNull(paymentResponse.getPaymentUrl()) || (paymentResponse.getStatus() == 200 && dto.getPaymentRequest().getPaymentSource().equals("MPESA"))) {
                    billingSetupDTO.setPaymentUrl(paymentResponse.getPaymentUrl());
                    billingSetupDTO.setTransactionRef(paymentResponse.getTransactionRef());
                    billingSetupDTO.setCurrency(currency);
                    billingSetupDTO.setChargeAmount(charge);

                    if (dto.getPaymentRequest().getPaymentSource().equals("MPESA")) {
                        billingSetupDTO.setTransactionRef(dto.getPaymentRequest().getTransRef());
                        paymentResponse.setPaymentUrl("https://mpesa.com");
                    }

                    billingSetup.setPaymentRef(paymentResponse.getTransactionRef());
                    billingSetup.setUpdatedAt(LocalDateTime.now());
                    billingSetup.setValidFrom(LocalDateTime.now());
                    billingSetup.setValidTill(LocalDateTime.now().plusDays(duration));
                    billingSetup.setChargeAmount(charge);
                    billingSetup.setCurrency(currency);

                    billingSetup = billingSetupRepository.save(billingSetup);

                    //save to DB
                    billingSetupRepository.save(billingSetup);

                } else {
                    throw new GeneralException(ResponseCodeAndMessage.BAD_REQUEST.responseCode , MessageConstant.PAYMENT_FAILED);
                }

                return billingSetupDTO;

            }

        } else {
            throw new GeneralException(ResponseCodeAndMessage.BAD_REQUEST.responseCode , MessageConstant.INVALID_BILLING_METHOD);
        }

    }

    @Override
    public BillingSetupDTO getAccountBillDTOInfo(String accountId , String packageName) {
        log.info("getting single billingSetup DTO info");

        return getBillingSetupDTO(getCurrentAccountBillInfo(accountId , packageName));
    }

    @Override
    public List<BillingSetupDTO> getBillInfoByPackageName(String packageName) {
        log.info("getting billingSetup DTOs info");

        List<BillingSetup> billingSetups = getBillingSetups(packageName);

        return billingSetups.stream().map(this::getBillingSetupDTO).collect(Collectors.toList());

    }

    @Override
    public List<BillingSetupDTO> getAccountBillInfo(String accountId) {
        return billingSetupRepository.findByAccountId(accountId).stream().map(this::getBillingSetupDTO).collect(Collectors.toList());
    }

    @Override
    public BillingSetup getCurrentAccountBillInfo(String accountId , String packageName) {
        return billingSetupRepository.findByAccountIdAndValidateAndPackages_packageName(accountId , true , packageName);
    }

    @Override
    public void invalidateBill(UUID billId) {

        BillingSetup billingSetup = getBillByBillId(billId);

        billingSetup.setValidate(false);
        billingSetup.setUpdatedAt(LocalDateTime.now());

        billingSetupRepository.save(billingSetup);

    }

    @Override
    public BillingSetupDTO upgradeBill(LicenseUpgradeRequestDTO requestDTO , double charge , String currency) {
        log.info("upgrading billing setup info!");

        //account id
        String accountId = requestDTO.getAccountId();

        //get package from
        Packages packageFrom = packageService.getPackageByName(requestDTO.getPackageUpgradedFrom());

        log.info("getting params for package from!");
        //get package name from package from
        String packageNameFrom = packageFrom.getPackageName();
        //get a license type of package from
        LicenseType licenseTypeFrom = packageFrom.getPackageType().getLicenseType();

        //get package to
        Packages packageTo = packageService.getPackageByName(requestDTO.getPackageUpgradedTo());
        //get package name from package to
        String packageNameTo = packageTo.getPackageName();
        //get a license type of package to
        LicenseType licenseTypeTo = packageTo.getPackageType().getLicenseType();

        //get billing method
        BillingMethod billMethod = billingMethodService.getBillingMethodById(requestDTO.getBillingMethodID());
        //billing method id
        Long billingMethodId = requestDTO.getBillingMethodID();

        //check if they are of the same licenses type
        if (!Objects.equals(licenseTypeFrom.getLicenseTypeId() , licenseTypeTo.getLicenseTypeId())) {
            throw new GeneralException(ResponseCodeAndMessage.BAD_REQUEST.responseCode , MessageConstant.UPGRADE_PACKAGE_TO_HAS_TO_BE_OF_SAME_LICENSE_WITH_UPGRADE_PACKAGE_FROM);
        }

        if (Objects.nonNull(billMethod)) {

            if (billingSetupRepository.existsByAccountIdAndPackages_packageType_licenseTypeAndValidate(accountId , licenseTypeFrom , true)) {
                log.info("upgrading billing setup => {} {} {}" , accountId , billingMethodId , packageNameFrom);

                //get an initial bill
                BillingSetup initialBill = billingSetupRepository.findByAccountIdAndPackages_packageNameAndPackages_packageType_licenseTypeAndValidate(accountId , packageNameFrom , licenseTypeFrom , true);

                BillingSetup billSetup = new BillingSetup();
                BeanUtils.copyProperties(requestDTO , billSetup);
                billSetup.setBillingMethod(billMethod);
                billSetup.setValidate(false);
                billSetup.setLicenseUpgrade(true);
                billSetup.setValidFrom(LocalDateTime.now());
                billSetup.setPackages(packageTo);
                billSetup.setPaymentSuccess(false);

                billSetup.setValidTill(initialBill.getValidTill());
                billSetup.setCreatedAt(LocalDateTime.now());
                billSetup.setChargeAmount(charge);
                billSetup.setCurrency(currency);
                billSetup.setBillId(UUID.randomUUID());
                billSetup.setEmail(initialBill.getEmail());
                billSetup.setPhone(initialBill.getPhone());
                billSetup.setCountry((initialBill.getCountry()));
                billSetup.setFirstName(initialBill.getFirstName());

                //get payment source
                PaymentSource paymentSource = paymentSourceService.getSingleSource(requestDTO.getPaymentRequest().getPaymentSource());

                billSetup.setPaymentSource(paymentSource);

                //set payment params
                requestDTO.getPaymentRequest().setAccountId(accountId);
                if (requestDTO.getPaymentRequest().getPaymentSource().equals("MPESA")) {
                    requestDTO.getPaymentRequest().setAmount(String.valueOf(Math.round(charge)));
                } else {
                    requestDTO.getPaymentRequest().setAmount(String.valueOf(charge));
                }
                requestDTO.getPaymentRequest().setServiceDate(LocalDateTime.now().toString());
                requestDTO.getPaymentRequest().setDescription(packageNameTo);
                requestDTO.getPaymentRequest().setTransRef(UUID.randomUUID().toString());
                requestDTO.getPaymentRequest().setClient(packageTo.getPackageType().getLicenseType().getClient().getClientName());
                requestDTO.getPaymentRequest().setPaymentType("");
                requestDTO.getPaymentRequest().setMethod("");
                requestDTO.getPaymentRequest().setCurrency(currency);
                requestDTO.getPaymentRequest().setEmail(initialBill.getEmail());

                //check the source
                if (requestDTO.getPaymentRequest().getPaymentSource().equals("MPESA") && Objects.nonNull(requestDTO.getPaymentRequest().getPhoneNumber())) {
                    if (!requestDTO.getPaymentRequest().getPhoneNumber().contains("254")) {
                        throw new GeneralException(ResponseCodeAndMessage.BAD_REQUEST.responseCode , MessageConstant.PHONE_NUMBER_NEED_TO_BE_KENYA_WITH_254_COUNTRY_CODE);
                    }
                } else if (requestDTO.getPaymentRequest().getPaymentSource().equals("MPESA")) {
                    throw new GeneralException(ResponseCodeAndMessage.BAD_REQUEST.responseCode , MessageConstant.PHONE_NUMBER_NEED_TO_BE_PROVIDED_FOR_MPESA);
                }

                //make payment
                PaymentResponseDTO paymentResponse = callPushPaymentService(requestDTO.getPaymentRequest());

                //initiate DTO
                BillingSetupDTO billingSetupDTO = getBillingSetupDTO(billSetup);

                if (Objects.nonNull(paymentResponse.getPaymentUrl()) || (paymentResponse.getStatus() == 200 && requestDTO.getPaymentRequest().getPaymentSource().equals("MPESA"))) {
                    billingSetupDTO.setPaymentUrl(paymentResponse.getPaymentUrl());
                    billingSetupDTO.setTransactionRef(paymentResponse.getTransactionRef());

                    if (requestDTO.getPaymentRequest().getPaymentSource().equals("MPESA")) {
                        billingSetupDTO.setTransactionRef(requestDTO.getPaymentRequest().getTransRef());
                        billSetup.setPaymentRef(requestDTO.getPaymentRequest().getTransRef());
                        paymentResponse.setPaymentUrl("https://mpesa.com");
                    } else {
                        billSetup.setPaymentRef(paymentResponse.getTransactionRef());
                    }

                    billSetup = billingSetupRepository.save(billSetup);

                    //save to DB
                    billingSetupRepository.save(billSetup);

                }

                return billingSetupDTO;

            } else {
                throw new GeneralException(ResponseCodeAndMessage.BAD_REQUEST.responseCode , MessageConstant.NO_ACTIVE_BILL_SETUP_FOUND);
            }

        } else {
            throw new GeneralException(ResponseCodeAndMessage.BAD_REQUEST.responseCode , MessageConstant.INVALID_BILLING_METHOD);
        }
    }

    @Override
    public BillChargeDTO getChargeAmount(String packageName , String country) {
        log.info("getting charge amount for package => {}" , packageName);

        if (Objects.nonNull(packageName) && !Objects.equals(packageName , "")) {

            //get package
            Packages packages = packageService.getPackageByName(packageName);

            log.info("getting the params!");
            //get duration from package
            Long duration = packages.getDuration();

            //get package DTO
            PackageDTO packageDTO = packageService.getPackageDTOByName(packageName);

            //get bill rate
            PackageRateDTO packageRateDTO = packageDTO.getCurrentBillRates();

            //check if there is a valid bill rate
            if (Objects.isNull(packageRateDTO)) {
                throw new GeneralException(ResponseCodeAndMessage.BAD_REQUEST.responseCode , MessageConstant.BILL_RATE_NOT_FOUND_FOR_PACKAGE);
            }

            //get rate from package rate
            double rate = packageRateDTO.getRate();

            double charge = duration * rate;
            String currency = GeneralUtil.getCurrency("default");

            //if there is region rate
            if (regionRateService.isRegionRate(country , packageRateDTO.getVersionNo())) {
                if (Objects.nonNull(regionRateService.getSingleRegionRate(country , packageRateDTO.getVersionNo())) && charge > 0) {
                    RegionRate region = (regionRateService.getSingleRegionRate(country , packageRateDTO.getVersionNo()));
                    double regionRate = region.getRate();

                    if (regionRate > 0) {
                        charge = duration * regionRate;

                        //set region currency
                        currency = GeneralUtil.getCurrency(country);
                    }
                }
            }

            //get other params
            String clientName = packages.getPackageType().getLicenseType().getClient().getClientName();

            BillChargeDTO billChargeDTO = new BillChargeDTO();

            //set billCharge values
            billChargeDTO.setTotalChargeAmount(charge);
            billChargeDTO.setClientName(clientName);
            billChargeDTO.setPackageName(packageName);
            billChargeDTO.setPaymentSourceDTO(paymentSourceService.getAllSources());
            billChargeDTO.setCurrency(currency);
            billChargeDTO.setPeriod(duration + " days");

            return billChargeDTO;

        } else {
            throw new GeneralException(ResponseCodeAndMessage.BAD_REQUEST.responseCode , MessageConstant.INVALID_PACKAGE_NAME);
        }
    }

    @Override
    public List<BillingSetupDTO> getActiveSubscriptions(String accountId) {
        log.info("getting active subscriptions for account {}" , accountId);

        List<BillingSetup> billingSetups = billingSetupRepository.findByAccountIdAndValidateAndPaymentSuccess(accountId , true , true);

        return billingSetups.stream().map(this::getBillingSetupDTO).collect(Collectors.toList());

    }

    @Override
    public BillingSetup getNewBillInfo(String trans_Ref) {
        return billingSetupRepository.findByPaymentRef(trans_Ref);
    }

    @Override
    public BillingSetupDTO getNewBillByBillId(UUID billId) {
        return getBillingSetupDTO(billingSetupRepository.findByBillId(billId));
    }

    private BillingSetup getBillByBillId(UUID billId) {
        return billingSetupRepository.findByBillIdAndValidate(billId , true);
    }

    private List<BillingSetup> getBillingSetups(String packageName) {
        return billingSetupRepository.findByPackages_packageNameAndValidate(packageName , true);
    }

    private PaymentResponseDTO callPushPaymentService(PaymentRequestDTO paymentRequest) {
        log.info("calling push payment service!");

        return paymentService.pushPayment(paymentRequest);
    }

    private BillingSetupDTO getBillingSetupDTO(BillingSetup billingSetup) {
        log.info("converting billingSetup to billingSetup DTO");

        BillingSetupDTO billingSetupDTO = new BillingSetupDTO();
        BeanUtils.copyProperties(billingSetup , billingSetupDTO);

        //get billing method info
        billingSetupDTO.setBillingMethod(billingMethodService.getBillingMethodDTOById(billingSetup.getBillingMethod().getBillingMethodID()));

        //get packages
        List<PackageDTO> packageDTOs = new ArrayList<>();

        if (!billingSetup.getPackages().isEmpty()) {
            int size = billingSetup.getPackages().size();

            for (int i = 0; i < size; i++) {

                PackageDTO packages = packageService.getPackageDTOByName(billingSetup.getPackages().get(i).getPackageName());
                packageDTOs.add(packages);
            }
        }

        billingSetupDTO.setPackageList(packageDTOs);

        //get bill log
        List<BillLogDTO> billLogDTOs = billLogService.getBillLogsByAccountId4Bill(billingSetup.getAccountId());

        if (!billLogDTOs.contains(null)) {
            billingSetupDTO.setBillLogDTOs(billLogDTOs);
        } else {
            billingSetupDTO.setBillLogDTOs(null);
        }

        return billingSetupDTO;
    }

    @Override
    public List<BillingSetupBasicInfoI> getOutdatedBills() {
        return billingSetupRepository.getOutdatedBills();
    }

    @Override
    public List<String> getOutdatedBilling() {
        return billingSetupRepository.getOutdatedBilling();
    }

    @Override
    public BillingSetup getOutdatedBill(String billId) {
        return billingSetupRepository.getOutdatedBill(billId);
    }

    @Override
    public void updateOutdatedBills(List<BillingSetup> billingSetups) {
        log.info("Update outdated bills");

        billingSetups = billingSetups.stream().peek(billingSetup -> billingSetup.setValidate(false)).peek(billingSetup -> billingSetup.setUpdatedAt(LocalDateTime.now())).collect(Collectors.toList());

        billingSetupRepository.saveAll(billingSetups);

    }

    @Override
    public List<BillingSetupDTO> renewBills(List<BillingSetup> billingSetups) {
        log.info("scheduler recurrent bill renewal implementation");

        List<BillingSetupDTO> billingSetupDTOs = new ArrayList<>();

        for (BillingSetup billingSetup : billingSetups) {

            //get bill package dto
            PackageDTO packageDTO = packageService.getBillPackage(billingSetup.getBillId());
            //get duration
            Long duration = packageDTO.getDuration();
            double charge = duration * packageDTO.getCurrentBillRates().getRate();

            //set values
            billingSetup.setValidate(false);
            billingSetup.setLicenseUpgrade(false);
            billingSetup.setValidFrom(LocalDateTime.now());
            billingSetup.setPaymentSuccess(false);
            billingSetup.setChargeAmount(charge);
            billingSetup.setPackages(packageService.getPackageByName(packageDTO.getPackageName()));

            billingSetup.setValidTill(LocalDateTime.now().plusDays(duration));
            billingSetup.setCreatedAt(LocalDateTime.now());
            billingSetup.setBillId(UUID.randomUUID());

            BillingSetupDTO billingSetupDTO = getBillingSetupDTO(billingSetup);

            String source = "DPO";
            if (Objects.nonNull(billingSetup.getPaymentSource().getSourceCode()) && !billingSetup.getPaymentSource().getSourceCode().equals("MPESA")) {
                source = billingSetup.getPaymentSource().getSourceCode();
            }

            if (packageDTO.isRecurring()) {
                if (charge > 0) {

                    //set payment params
                    PaymentRequestDTO dto = PaymentRequestDTO.builder().accountId(billingSetup.getAccountId()).amount(String.valueOf(charge)).serviceDate(LocalDateTime.now().toString()).description(packageDTO.getPackageName()).transRef(UUID.randomUUID().toString()).client(packageDTO.getPackageType().getLicenseTypeDTO().getClientDTO().getClientName()).paymentType("").method("").currency(billingSetup.getCurrency()).backUrl("https://beta.myrekod.com/payment-cancelled").redirectUrl("https://beta.myrekod.com/payment-processed").country(billingSetup.getCountry()).paymentSource(source).email(billingSetup.getEmail()).build();

                    //make payment
                    PaymentResponseDTO paymentResponse = callPushPaymentService(dto);

                    if (Objects.nonNull(paymentResponse.getPaymentUrl())) {
                        billingSetupDTO.setPaymentUrl(paymentResponse.getPaymentUrl());
                        billingSetupDTO.setTransactionRef(paymentResponse.getTransactionRef());

                        billingSetup.setPaymentRef(paymentResponse.getTransactionRef());

                        billingSetup = billingSetupRepository.save(billingSetup);

                        //Save to DB
                        billingSetupRepository.save(billingSetup);

                    }

                } else {

                    billingSetup.setValidate(true);
                    billingSetup.setPaymentSuccess(true);

                    //save to DB
                    billingSetup = billingSetupRepository.save(billingSetup);

                    //generate coupons
                    logBillCoupon(billingSetup , packageDTO);

                    //save to DB
                    billingSetupRepository.save(billingSetup);

                }
            }

            billingSetupDTOs.add(billingSetupDTO);

        }

        return billingSetupDTOs;

    }

    @Override
    public BillingSetup getBillSetupByBillLogId(String billLogId) {
        return billingSetupRepository.findByBillLogs_billLogId(billLogId).orElseThrow(() -> new GeneralException(ResponseCodeAndMessage.RECORD_NOT_FOUND.responseCode , MessageConstant.RECORD_NOT_FOUND));
    }

    private void logBillCoupon(BillingSetup billingSetup , PackageDTO packages) {
        log.info("generating bill log for bill setup standalone service...");

        //string array for bill log id
        List<BillLogDTO> billLogDTOs = new ArrayList<>();

        //get all items
        List<ItemDTO> items = packages.getItemList();

        for (ItemDTO item : items) {
            //check if item is standalone
            if (item.isStandalone()) {
                //get item quantity
                Long quantity = itemService.getItemPackageQuantity(packages.getPackageName() , item.getItemId()).getQuantity();

                if (billingSetup.isLicenseUpgrade()) {
                    LicenseUpgrade licenseUpgrade = licenseUpgradeRepository.findByBillingSetup_BillId(billingSetup.getBillId()).orElseThrow(() -> new GeneralException(ResponseCodeAndMessage.RECORD_NOT_FOUND.responseCode , MessageConstant.RECORD_NOT_FOUND));
                    if (Objects.nonNull(licenseUpgrade)) {
                        //get initial package
                        PackageDTO initialPackage = packageService.getBillPackage(licenseUpgrade.getInitialBillId());
                        if (Objects.nonNull(initialPackage)) {
                            //get all initial items
                            List<ItemDTO> initialItems = initialPackage.getItemList();
                            for (ItemDTO initialItem : initialItems) {
                                if (initialItem.getItemId().equals(item.getItemId())) {
                                    Long initialQuantity = itemService.getItemPackageQuantity(initialPackage.getPackageName() , initialItem.getItemId()).getQuantity();

                                    quantity = quantity - initialQuantity;
                                }
                            }
                        }
                    }
                }

                if (quantity > 0) {
                    //Prepare payment request
                    PaymentRequestDTO paymentRequestDTO = PaymentRequestDTO.builder().paymentSource("DPO").backUrl("https://beta.myrekod.com/payment-cancelled").redirectUrl("https://beta.myrekod.com/payment-processed").build();

                    //create bill log
                    BillLogRequestDTO dto = BillLogRequestDTO.builder().accountId(billingSetup.getAccountId()).chargeAmount(item.getItemMinPrice() * quantity).itemId(item.getItemId()).itemQuantity(quantity).itemRef(item.getItemRef()).packageName(packages.getPackageName()).paymentRequest(paymentRequestDTO).build();

                    BillLogDTO billLogDTO = billLogService.logBillingDetail(dto);

                    billLogDTOs.add(billLogDTO);
                }

            }
        }

        if (!billLogDTOs.isEmpty()) {
            for (BillLogDTO billLogDTO : billLogDTOs) {
                for (int i = 0; i < billLogDTO.getItemQuantity(); i++) {
                    couponService.logCoupon(billLogDTO.getBillLogId());
                }
            }
        }

    }

    private BillingSetupListDTO getBillingSetupListDTO(Page<BillingSetup> billingSetupPage) {
        log.info("Converting BillingSetup page to BillingSetup list dto");

        BillingSetupListDTO billingSetupListDTO = new BillingSetupListDTO();

        List<BillingSetup> billingSetups = billingSetupPage.getContent();
        if (!billingSetups.isEmpty()) {
            billingSetupListDTO.setHasNextRecord(billingSetupPage.hasNext());
            billingSetupListDTO.setTotalCount((int) billingSetupPage.getTotalElements());
        }

        List<BillingSetupDTO> billingSetupDTOs = billingSetups.stream().map(this::getBillingSetupDTO).collect(Collectors.toList());
        billingSetupListDTO.setBillingSetupDTOs(billingSetupDTOs);

        return billingSetupListDTO;

    }

}
