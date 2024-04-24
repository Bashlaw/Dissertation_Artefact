package com.staffs.backend.licenseUpgrade.dto;

import com.staffs.backend.paymentIntegration.dto.PaymentRequestDTO;
import lombok.Builder;
import lombok.Data;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

@Data
@Builder
public class LicenseUpgradeRequestDTO {

    @NotNull
    @NotEmpty(message = "Please provide package name upgrading from")
    private String packageUpgradedFrom;

    @NotNull
    @NotEmpty(message = "Please provide package name upgrading to")
    private String packageUpgradedTo;

    @NotNull
    @NotEmpty(message = "Please provide account ID")
    private String accountId;

    @NotNull
    @NotEmpty(message = "Please provide billing method")
    private Long billingMethodID;

    private PaymentRequestDTO paymentRequest;

}
