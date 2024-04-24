package com.staffs.backend.coupon.service;

import com.staffs.backend.coupon.dto.CouponDTO;
import com.staffs.backend.coupon.dto.CouponListDTO;
import com.staffs.backend.general.dto.PageableRequestDTO;

public interface CouponService {

    void logCoupon(String billLogId);

    CouponDTO getCouponForBillByItem(String billId, Long itemId);

    CouponListDTO getUnusedCouponForBIll(PageableRequestDTO requestDTO, String billId);

    CouponListDTO getUsedCouponForBIll(PageableRequestDTO requestDTO, String billId);

    CouponListDTO getUsedUnusedCouponForBIllAndItem(PageableRequestDTO requestDTO, String billId, Long itemId, boolean isUsed);

}
