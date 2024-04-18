package com.staffs.backend.regionRate.dto;

import lombok.Builder;
import lombok.Data;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

@Data
@Builder
public class RegionRateRequestDTO {

    @NotNull
    @NotEmpty(message = "Please provide rate")
    private double rate;

    @NotNull
    @NotEmpty(message = "Please provide country")
    private String countryShortCode;

    @NotNull
    @NotEmpty(message = "Please provide package rate version number")
    private Long packageRateVersionId;

}
