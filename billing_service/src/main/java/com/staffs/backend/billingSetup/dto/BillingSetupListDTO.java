package com.staffs.backend.billingSetup.dto;

import com.staffs.backend.general.dto.PageableResponseDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class BillingSetupListDTO extends PageableResponseDTO {

    private List<BillingSetupDTO> billingSetupDTOs;

}
