package com.staffs.backend.changeLog.dto;

import com.staffs.backend.general.dto.PageableResponseDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class ChangeLogListDTO extends PageableResponseDTO {

    private List<ChangeLogDTO> changeLogDTOS;

}
