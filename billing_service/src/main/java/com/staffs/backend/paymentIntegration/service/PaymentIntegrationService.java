package com.staffs.backend.paymentIntegration.service;

import com.staffs.backend.paymentIntegration.dto.*;

public interface PaymentIntegrationService {

    void getPaymentTokenDPO();

    PaymentRequestDTO generatePaymentRequestDPO(PaymentRequestDTO requestDTO);

    PaymentResponseDTO getPaymentResponse(PaymentRequestDTO requestDTO);

    ConfirmResponseDTO getConfirmPaymentResponse(ConfirmRequestDTO requestDTO);

    TransactionResponseDTO getCancelTransactionResponse(CancelRequestDTO requestDTO);

    TransactionResponseDTO getRefundTransactionResponse(RefundRequestDTO requestDTO);

    void getPaymentTokenMPESA();

    PaymentRequestDTO generatePaymentRequestMPESA(PaymentRequestDTO requestDTO);

    ConfirmRequestDTO generateConfirmRequestDPO(ConfirmRequestDTO dto);

    ConfirmRequestDTO generateConfirmRequestMPESA(ConfirmRequestDTO dto);

    ConfirmRequestDTO generateConfirmRequestGeneric(ConfirmRequestDTO dto);

    void getPaymentTokenPAYSTACK();

    PaymentRequestDTO generatePaymentRequestPAYSTACK(PaymentRequestDTO requestDTO);

    ConfirmRequestDTO generateConfirmRequestPAYSTACK(ConfirmRequestDTO dto);

    PaymentRequestDTO generatePaymentRequestGeneric(PaymentRequestDTO requestDTO);

}
