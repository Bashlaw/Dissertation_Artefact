package com.staffs.backend.coupon.dto;

import com.staffs.backend.billLog.dto.BillLogDTO;
import lombok.Data;

@Data
public class CouponDTO {

    private String code;

    private BillLogDTO billLog;

}
