package com.staffs.backend.paymentIntegration.service;

import com.staffs.backend.paymentIntegration.dto.*;

public interface PaymentService {

    PaymentResponseDTO pushPayment(PaymentRequestDTO requestDTO);

    ConfirmResponseDTO confirmPayment(ConfirmRequestDTO requestDTO);

    TransactionResponseDTO cancelTransaction(CancelRequestDTO requestDTO);

    TransactionResponseDTO refundTransaction(RefundRequestDTO requestDTO);

}
