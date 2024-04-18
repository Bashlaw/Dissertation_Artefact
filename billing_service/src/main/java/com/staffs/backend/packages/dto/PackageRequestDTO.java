package com.staffs.backend.packages.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PackageRequestDTO {

    @NotEmpty(message = "Package name cannot be empty")
    private String packageName;

    private String description;

    @NotNull(message = "Duration cannot be null")
    private Long duration;

    private boolean activation;

    @NotNull(message = "Package type id cannot be null")
    private Long packageTypeId;

    private boolean recurring;

}
