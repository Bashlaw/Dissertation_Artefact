package com.staffs.backend.billingSetup.dto;

import com.staffs.backend.paymentIntegration.dto.PaymentRequestDTO;
import lombok.Builder;
import lombok.Data;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

@Data
@Builder
public class BillingSetupRequestDTO {

    @NotNull
    @NotEmpty(message = "Please provide account ID")
    private String accountId;

    @NotNull(message = "Please provide billing method ID")
    private Long billingMethodId;

    private final String meansOfPayment = "Online";

    @NotNull
    @NotEmpty(message = "Please provide package name")
    private String packageName;

    private final String email = null;

    private final String phone = null;

    @NotNull
    @NotEmpty(message = "Please provide user firstname")
    private final String firstName;

    private PaymentRequestDTO paymentRequest;

}
