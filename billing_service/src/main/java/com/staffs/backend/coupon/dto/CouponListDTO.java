package com.staffs.backend.coupon.dto;

import com.staffs.backend.general.dto.PageableResponseDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class CouponListDTO extends PageableResponseDTO {

    private List<CouponDTO> couponDTOs;

}
