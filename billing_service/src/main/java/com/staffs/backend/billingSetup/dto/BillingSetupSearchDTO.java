package com.staffs.backend.billingSetup.dto;

import lombok.Data;

import jakarta.validation.constraints.NotNull;

@Data
public class BillingSetupSearchDTO {

    @NotNull(message = "Size must be provided, maximum is 100")
    private int size;

    @NotNull(message = "Page must be provided, minimum is 0")
    private int page = 0;

    private String fromDate;

    private String toDate;

    private String validate;

}
