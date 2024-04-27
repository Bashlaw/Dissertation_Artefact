package com.staffs.backend.billingSetup.controller;

import com.staffs.backend.billingSetup.dto.BillingSetupRequestDTO;
import com.staffs.backend.billingSetup.service.BillingSetupService;
import com.staffs.backend.general.dto.Response;
import com.staffs.backend.general.service.GeneralService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping("/billing/api/v1/billingSetup")
public class BillingSetupController {

    private final GeneralService generalService;
    private final BillingSetupService billingSetupService;

    @PostMapping("/add")
    public Response accountBillingSetup(@Valid @RequestBody BillingSetupRequestDTO dto) {
        return generalService.prepareSuccessResponse(billingSetupService.setupAccountBill(dto));
    }

    @GetMapping("/single/{accountId}/{packageName}")
    public Response getCurrentAccountBillingSetup(@PathVariable String accountId , @PathVariable String packageName) {
        return generalService.prepareSuccessResponse(billingSetupService.getAccountBillDTOInfo(accountId , packageName));
    }

    @GetMapping("/all/byAccountId/{accountId}")
    public Response getAccountBillingSetups(@PathVariable String accountId) {
        return generalService.prepareSuccessResponse(billingSetupService.getAccountBillInfo(accountId));
    }

    @GetMapping("/all/byPackageName/{packageName}")
    public Response getBillingSetupByPackage(@PathVariable String packageName) {
        return generalService.prepareSuccessResponse(billingSetupService.getBillInfoByPackageName(packageName));
    }

    @GetMapping("/getTotalBillCharge/{packageName}/{country}")
    public Response getTotalBillCharge(@PathVariable String packageName , @PathVariable String country) {
        return generalService.prepareSuccessResponse(billingSetupService.getChargeAmount(packageName , country));
    }

    @GetMapping("/activeSubscriptions/{accountId}")
    public Response getActiveSubscriptions(@PathVariable String accountId) {
        return generalService.prepareSuccessResponse(billingSetupService.getActiveSubscriptions(accountId));
    }

}
