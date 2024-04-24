package com.staffs.backend.coupon.controller;

import com.staffs.backend.coupon.dto.CouponDTO;
import com.staffs.backend.coupon.dto.CouponListDTO;
import com.staffs.backend.coupon.service.CouponService;
import com.staffs.backend.general.dto.PageableRequestDTO;
import com.staffs.backend.general.dto.Response;
import com.staffs.backend.general.service.GeneralService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping("/billing/api/v1/coupon")
public class CouponController {

    private final CouponService couponService;
    private final GeneralService generalService;

    @GetMapping("/getCoupon/{billId}/{itemId}")
    public Response getCoupon(@PathVariable Long itemId , @PathVariable String billId) {

        CouponDTO couponDTO = couponService.getCouponForBillByItem(billId , itemId);

        return generalService.prepareSuccessResponse(couponDTO);
    }

    @GetMapping("/all/getUnUsedCoupon/{billId}/{page}/{size}")
    public Response getAllUnUsedCoupon(@PathVariable String billId , @PathVariable int page , @PathVariable int size) {

        PageableRequestDTO requestDTO = new PageableRequestDTO();
        requestDTO.setPage(page);
        requestDTO.setSize(size);

        CouponListDTO couponListDTO = couponService.getUnusedCouponForBIll(requestDTO , billId);

        return generalService.prepareSuccessResponse(couponListDTO);
    }

    @GetMapping("/all/getUsedCoupon/{billId}/{page}/{size}")
    public Response getAllUsedCoupon(@PathVariable String billId , @PathVariable int page , @PathVariable int size) {

        PageableRequestDTO requestDTO = new PageableRequestDTO();
        requestDTO.setPage(page);
        requestDTO.setSize(size);

        CouponListDTO couponListDTO = couponService.getUsedCouponForBIll(requestDTO , billId);

        return generalService.prepareSuccessResponse(couponListDTO);
    }

    @GetMapping("/all/getUsedUnusedCouponByItem/{billId}/{itemId}/{isUsed}/{page}/{size}")
    public Response getAllUsedUnusedCouponByItem(@PathVariable String billId , @PathVariable int page , @PathVariable int size ,
                                                 @PathVariable Long itemId , @PathVariable boolean isUsed) {

        PageableRequestDTO requestDTO = new PageableRequestDTO();
        requestDTO.setPage(page);
        requestDTO.setSize(size);

        CouponListDTO couponListDTO = couponService.getUsedUnusedCouponForBIllAndItem(requestDTO , billId , itemId , isUsed);

        return generalService.prepareSuccessResponse(couponListDTO);
    }

}
