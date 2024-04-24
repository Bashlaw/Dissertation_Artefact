package com.staffs.backend.paymentIntegration.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ConfirmResponseDTO {

    private final Long status;

    private final String message;

    private final String company_ref;

    private final String transaction_ref;

    private final String amount;

    private final String fraud_level;

    private final String fraud_explanation;

    private final String trans_fee;

    private final String transactionReferenceNumber;

    private final String transactionStatus;

    private final TransactionData transactionData;

}
