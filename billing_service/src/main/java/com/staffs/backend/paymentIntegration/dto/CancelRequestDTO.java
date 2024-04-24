package com.staffs.backend.paymentIntegration.dto;

import com.google.gson.annotations.SerializedName;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CancelRequestDTO {

    @SerializedName("paymentSource")
    private String paymentSource;

    @SerializedName("transaction_reference")
    private final String transactionReference;

}
