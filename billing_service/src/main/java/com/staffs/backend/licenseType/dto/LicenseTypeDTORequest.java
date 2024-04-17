package com.staffs.backend.licenseType.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class LicenseTypeDTORequest {

    @NotEmpty(message = "license type name cannot be empty")
    private String licenseTypeName;

    private String Description;

    private Long userCount;

    @NotEmpty(message = "client name cannot be empty")
    private String clientName;

}
