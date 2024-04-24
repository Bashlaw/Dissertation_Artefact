package com.staffs.backend.coupon.service.implementation;

import com.staffs.backend.coupon.dto.CouponDTO;
import com.staffs.backend.coupon.dto.CouponListDTO;
import com.staffs.backend.coupon.service.CouponService;
import com.staffs.backend.entity.billLog.BillLog;
import com.staffs.backend.entity.coupon.Coupon;
import com.staffs.backend.exceptions.GeneralException;
import com.staffs.backend.general.dto.MessageConstant;
import com.staffs.backend.general.dto.PageableRequestDTO;
import com.staffs.backend.general.enums.ResponseCodeAndMessage;
import com.staffs.backend.general.service.GeneralService;
import com.staffs.backend.repository.billLog.BillLogRepository;
import com.staffs.backend.repository.coupon.CouponRepository;
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
public class CouponServiceImpl implements CouponService {

    private final GeneralService generalService;
    private final CouponRepository couponRepository;
    private final BillLogRepository billLogRepository;

    @Override
    public void logCoupon(String billLogId) {
        log.info("generating coupon...");

        Coupon coupon = new Coupon();

        //set values
        coupon.setCouponId(UUID.randomUUID().toString());
        coupon.setCode(generateCouponCode());
        coupon.setBillLog(this.getSingleBillLog(billLogId));
        coupon.setUsed(false);

        //save to DB
        couponRepository.save(coupon);

    }

    @Override
    public CouponDTO getCouponForBillByItem(String billId , Long itemId) {
        Coupon coupon = getCoupon(UUID.fromString(billId) , itemId);
        updateUsedCoupon(UUID.fromString(billId) , itemId);
        return getCouponDTO(coupon);
    }

    @Override
    public CouponListDTO getUnusedCouponForBIll(PageableRequestDTO requestDTO , String billId) {

        Pageable pageable = generalService.getPageableObject(requestDTO.getSize() , requestDTO.getPage());
        List<Coupon> coupons = getUnUsedCoupon(UUID.fromString(billId));
        Page<Coupon> couponPage = new PageImpl<>(coupons , pageable , coupons.size());

        return getCouponListDTO(couponPage);
    }

    @Override
    public CouponListDTO getUsedCouponForBIll(PageableRequestDTO requestDTO , String billId) {

        Pageable pageable = generalService.getPageableObject(requestDTO.getSize() , requestDTO.getPage());
        List<Coupon> coupons = getUsedCoupon(UUID.fromString(billId));
        Page<Coupon> couponPage = new PageImpl<>(coupons , pageable , coupons.size());

        return getCouponListDTO(couponPage);
    }

    @Override
    public CouponListDTO getUsedUnusedCouponForBIllAndItem(PageableRequestDTO requestDTO , String billId , Long itemId , boolean isUsed) {

        Pageable pageable = generalService.getPageableObject(requestDTO.getSize() , requestDTO.getPage());
        List<Coupon> coupons = couponRepository.findByBillLog_BillingSetup_BillIdAndBillLog_Item_ItemIdAndUsedOrderByCreatedAtDesc(
                UUID.fromString(billId) , itemId , isUsed);
        Page<Coupon> couponPage = new PageImpl<>(coupons , pageable , coupons.size());

        return getCouponListDTO(couponPage);
    }

    private Coupon getCoupon(UUID billId , Long itemId) {
        return couponRepository.findFirstByBillLog_BillingSetup_BillIdAndBillLog_Item_ItemIdAndUsedOrderByCreatedAtDesc(
                billId , itemId , false).orElseThrow(() -> new GeneralException(ResponseCodeAndMessage.RECORD_NOT_FOUND.responseCode , MessageConstant.NO_COUPON_LEFT_FOR_THIS_SUBSCRIPTION_ITEM));
    }

    private List<Coupon> getUsedCoupon(UUID billId) {
        return couponRepository.findByBillLog_BillingSetup_BillIdAndUsed(billId , true);
    }

    private List<Coupon> getUnUsedCoupon(UUID billId) {
        return couponRepository.findByBillLog_BillingSetup_BillIdAndUsed(billId , false);
    }

    private CouponDTO getCouponDTO(Coupon coupon) {
        log.info("converting coupon to couponDTO");

        CouponDTO couponDTO = new CouponDTO();
        BeanUtils.copyProperties(coupon , couponDTO);

        //get and set bill log dto
        //couponDTO.setBillLog(this.getSingleDTOBillLog(coupon.getBillLog().getBillLogId()));

        return couponDTO;

    }

    private CouponListDTO getCouponListDTO(Page<Coupon> couponPage) {
        log.info("Converting coupon page to coupon list dto");

        CouponListDTO couponListDTO = new CouponListDTO();

        List<Coupon> coupons = couponPage.getContent();
        if (!coupons.isEmpty()) {
            couponListDTO.setHasNextRecord(couponPage.hasNext());
            couponListDTO.setTotalCount((int) couponPage.getTotalElements());
        }

        List<CouponDTO> couponDTOs = coupons.stream().map(this::getCouponDTO).collect(Collectors.toList());
        couponListDTO.setCouponDTOs(couponDTOs);

        return couponListDTO;

    }

    private String generateCouponCode() {
        String code = GeneralUtil.generateRandomChar(12);
        return code.substring(0 , 4) + "-" + code.substring(4 , 8) + "-" + code.substring(8 , 12);
    }

    private void updateUsedCoupon(UUID billId , Long itemId) {
        log.info("updating used coupon...");

        Coupon coupon = getCoupon(billId , itemId);

        if (!coupon.isUsed()) {
            coupon.setUsed(true);
            coupon.setUpdatedAt(LocalDateTime.now());
            couponRepository.save(coupon);

            //update bill log
            this.updateBillLogStatus(coupon.getBillLog().getBillLogId());
        }

    }

    private void updateBillLogStatus(String billLogId) {
        log.info("update bill log used status");

        BillLog billLog = this.getSingleBillLog(billLogId);
        Long left = billLog.getItemQuantityLeft();

        if (Objects.isNull(left)) {
            left = 0L;
        }

        if (!billLog.isUsed()) {
            billLog.setUsed(true);
            billLog.setUpdatedAt(LocalDateTime.now());
            billLog.setItemQuantityLeft(left + 1);
            billLogRepository.save(billLog);
        }

    }

    private BillLog getSingleBillLog(String billLogId) {
        return billLogRepository.findByBillLogId(billLogId).orElseThrow(()-> new GeneralException(ResponseCodeAndMessage.RECORD_NOT_FOUND.responseCode, MessageConstant.RECORD_NOT_FOUND));
    }

}
