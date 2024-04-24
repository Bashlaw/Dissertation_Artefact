package com.staffs.backend.billLog.dto;

import com.staffs.backend.general.dto.PageableResponseDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class BillLogListDTO extends PageableResponseDTO {

    private List<BillLogDTO> billLogDTOs;

}
