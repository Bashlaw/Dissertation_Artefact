package com.staffs.backend.paymentIntegration.controller;

import com.staffs.backend.general.dto.Response;
import com.staffs.backend.general.service.GeneralService;
import com.staffs.backend.paymentIntegration.dto.*;
import com.staffs.backend.paymentIntegration.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping("/billing/api/v1/paymentIntegration")
public class PaymentIntegrationController {

    private final GeneralService generalService;
    private final PaymentService paymentService;

    @PostMapping("/confirm")
    private Response confirmPayment(@RequestBody ConfirmRequestDTO dto) {

        ConfirmResponseDTO response = paymentService.confirmPayment(dto);

        return generalService.prepareSuccessResponse(response);
    }

    @PostMapping("/push")
    private Response pushPayment(@RequestBody PaymentRequestDTO dto) {

        PaymentResponseDTO response = paymentService.pushPayment(dto);

        return generalService.prepareSuccessResponse(response);
    }

    @PostMapping("/cancel")
    private Response pushPayment(@RequestBody CancelRequestDTO dto) {

        TransactionResponseDTO response = paymentService.cancelTransaction(dto);

        return generalService.prepareSuccessResponse(response);
    }

    @PostMapping("/refund")
    private Response pushPayment(@RequestBody RefundRequestDTO dto) {

        TransactionResponseDTO response = paymentService.refundTransaction(dto);

        return generalService.prepareSuccessResponse(response);
    }

}
