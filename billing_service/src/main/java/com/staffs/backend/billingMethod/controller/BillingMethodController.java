package com.staffs.backend.billingMethod.controller;

import com.staffs.backend.billingMethod.dto.BillingMethodRequestDTO;
import com.staffs.backend.billingMethod.service.BillingMethodService;
import com.staffs.backend.general.dto.MessageConstant;
import com.staffs.backend.general.dto.Response;
import com.staffs.backend.general.service.GeneralService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/billing/api/v1/billingMethod")
public class BillingMethodController {

    private final GeneralService generalService;
    private final BillingMethodService billingMethodService;

    public BillingMethodController(GeneralService generalService , BillingMethodService billingMethodService) {
        this.generalService = generalService;
        this.billingMethodService = billingMethodService;
    }

    @PostMapping("/add")
    public Response addBillingMethod(@Valid @RequestBody BillingMethodRequestDTO dto) {

        return generalService.prepareSuccessResponse(billingMethodService.saveBillingMethod(dto));

    }

    @GetMapping("/all")
    public Response getBillingMethods() {

        return generalService.prepareSuccessResponse(billingMethodService.getBillingMethods());

    }

    @GetMapping("/single")
    public Response getSingleBillingMethod(@Valid @RequestParam(name = "billing method name") String methodName) {

        return generalService.prepareSuccessResponse(billingMethodService.getBillingMethodByName(methodName));

    }

    @PostMapping("/validate")
    public Response activate(@RequestParam(name = "billing method name") String name , @RequestParam(name = "status") boolean status) {

        billingMethodService.validateBillingMethod(name , status);

        return generalService.prepareSuccessResponse(MessageConstant.SUCCESS);

    }

}
