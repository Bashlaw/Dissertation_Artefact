package com.staffs.backend.paymentIntegration.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TransactionResponseDTO {

    private final Long status;

    private final String message;

}
