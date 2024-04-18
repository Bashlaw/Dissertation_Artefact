package com.staffs.backend.packages.dto;

import com.staffs.backend.general.dto.PageableResponseDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class PackageListDTO extends PageableResponseDTO {

    private List<PackageDTO> packageDTOs;

}
