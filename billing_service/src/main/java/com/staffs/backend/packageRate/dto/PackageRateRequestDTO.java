package com.staffs.backend.packageRate.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PackageRateRequestDTO {

    @NotNull(message = "Version number must not be null")
    private Long versionNo;

    @NotNull(message = "Effect date must not be null")
    @FutureOrPresent(message = "Effect date must be in the present or future")
    private LocalDateTime effectDate;

    @PositiveOrZero(message = "Rate must be a positive or zero value")
    private double rate;

    private boolean validate;

    @NotEmpty(message = "Package name cannot be empty")
    private String packageName;

}
