package com.staffs.backend.billLog.dto;

import com.staffs.backend.paymentIntegration.dto.PaymentRequestDTO;
import lombok.Builder;
import lombok.Data;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

@Data
@Builder
public class BillLogRequestDTO {

    @NotNull
    @NotEmpty(message = "Please provide account ID")
    private String accountId;

    @NotNull
    @NotEmpty(message = "Please provide item ID")
    private Long itemId;

    @NotNull
    @NotEmpty(message = "Please provide item quantity")
    private Long itemQuantity;

    private String itemRef;

    @NotNull
    @NotEmpty(message = "Please provide charge amount")
    private double chargeAmount;

    @NotNull
    @NotEmpty(message = "Please provide package name")
    private String packageName;

    private PaymentRequestDTO paymentRequest;

}
