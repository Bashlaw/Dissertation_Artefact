package com.staffs.backend.paymentSource.controller;

import com.staffs.backend.general.dto.Response;
import com.staffs.backend.general.service.GeneralService;
import com.staffs.backend.paymentSource.dto.PaymentSourceRequestDTO;
import com.staffs.backend.paymentSource.service.PaymentSourceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping("/billing/api/v1/paymentSource")
public class PaymentSourceController {

    private final GeneralService generalService;
    private final PaymentSourceService paymentSourceService;

    @PostMapping("/add")
    public Response addPaymentSource(@Valid @RequestBody PaymentSourceRequestDTO dto) {

        return generalService.prepareSuccessResponse(paymentSourceService.createSource(dto));

    }

    @GetMapping("/all")
    public Response getAllPaymentSource() {

        return generalService.prepareSuccessResponse(paymentSourceService.getAllSources());

    }

    @GetMapping("/{paymentSourceId}")
    public Response getPaymentSourceById(@PathVariable String paymentSourceId) {

        return generalService.prepareSuccessResponse(paymentSourceService.getSingleSourceDTO(paymentSourceId));

    }

}
