package com.staffs.backend.paymentIntegration.dto;

import com.google.gson.annotations.SerializedName;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ConfirmRequestDTO {

    @SerializedName("platform")
    private final String platform;

    @SerializedName("customerId")
    private final String accountId;

    @SerializedName("companyRef")
    private final String transRef;

    @SerializedName("transactionRef")
    private final String transactionRef;

    @SerializedName("paymentSource")
    private String paymentSource;

    @SerializedName("transaction_reference")
    private final String transactionReference;

}
