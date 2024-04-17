package com.staffs.backend.packageType.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class PackageTypeDTORequest {

    @NotEmpty(message = "package type name cannot be empty")
    private String packageTypeName;

    private String description;

    @NotEmpty(message = "license type name cannot be empty")
    private String licenseTypeName;

    private boolean isVisit;

}
