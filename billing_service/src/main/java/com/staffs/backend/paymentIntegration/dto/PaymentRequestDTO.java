package com.staffs.backend.paymentIntegration.dto;

import com.google.gson.annotations.SerializedName;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PaymentRequestDTO {

    @SerializedName("platform")
    private String client;

    @SerializedName("customerId")
    private String accountId;

    @SerializedName("companyRef")
    private String transRef;

    @SerializedName("amount")
    private String amount;

    @SerializedName("currency")
    private String currency;

    @SerializedName("paymentType")
    private String paymentType;

    @SerializedName("country")
    private String country;

    @SerializedName("method")
    private String method;

    @SerializedName("redirect_url")
    private String redirectUrl;

    @SerializedName("back_url")
    private String backUrl;

    @SerializedName("serviceType")
    private String serviceType;

    @SerializedName("serviceDate")
    private String serviceDate;

    @SerializedName("description")
    private String description;

    @SerializedName("paymentSource")
    private String paymentSource;

    @SerializedName("organisationId")
    private String referenceId;

    @SerializedName("billNumber")
    private String billNumber;

    @SerializedName("phoneNumber")
    private String phoneNumber;

    @SerializedName("TransactionDescription")
    private String transactionDescription;

    @SerializedName("transactionRef")
    private String transactionRef;

    @SerializedName("email")
    private String email;

    @SerializedName("direct")
    private boolean direct;

    @SerializedName("bill_reference")
    private String billReference;

}
