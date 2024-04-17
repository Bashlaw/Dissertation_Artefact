package com.staffs.backend.packageType.dto;

import com.staffs.backend.licenseType.dto.LicenseTypeDTO;
import lombok.Data;

@Data
public class PackageTypeDTO {

    private Long packageTypeId;

    private String packageTypeName;

    private String description;

    private LicenseTypeDTO licenseTypeDTO;

    private boolean isVisit;

}
