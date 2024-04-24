package com.staffs.backend.paymentIntegration.dto;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class PaymentResponseDTO {

    @SerializedName("status")
    private int status;

    @SerializedName("message")
    private String message;

    @SerializedName("error")
    private String error;

    @SerializedName("paymentUrl")
    private String paymentUrl;

    @SerializedName("transactionRef")
    private String transactionRef;

}
