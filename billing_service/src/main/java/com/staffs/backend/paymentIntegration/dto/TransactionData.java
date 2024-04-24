package com.staffs.backend.paymentIntegration.dto;

import lombok.Data;

@Data
public class TransactionData {

    private String payment_status;

    private String description;

    private String mpesaRef;

    private String amount;

    private String phone_number;

    private String transactionTime;

}
