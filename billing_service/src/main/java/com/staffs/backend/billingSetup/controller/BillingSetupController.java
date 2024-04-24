package com.staffs.backend.billingSetup.controller;

import com.staffs.backend.billingSetup.dto.BillingSetupDTO;
import com.staffs.backend.billingSetup.dto.BillingSetupRequestDTO;
import com.staffs.backend.billingSetup.service.BillingSetupService;
import com.staffs.backend.general.dto.BillChargeDTO;
import com.staffs.backend.general.dto.Response;
import com.staffs.backend.general.service.GeneralService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping("/billing/api/v1/billingSetup")
public class BillingSetupController {

    private final GeneralService generalService;
    private final BillingSetupService billingSetupService;

    @PostMapping("/add")
    public Response accountBillingSetup(@Valid @RequestBody BillingSetupRequestDTO dto) {

        BillingSetupDTO billingSetupDTO = billingSetupService.setupAccountBill(dto);

        return generalService.prepareSuccessResponse(billingSetupDTO);
    }

    @GetMapping("/single/{accountId}/{packageName}")
    public Response getCurrentAccountBillingSetup(@PathVariable String accountId , @PathVariable String packageName) {

        BillingSetupDTO billingSetupDTO = billingSetupService.getAccountBillDTOInfo(accountId , packageName);

        return generalService.prepareSuccessResponse(billingSetupDTO);
    }

    @GetMapping("/all/byAccountId/{accountId}")
    public Response getAccountBillingSetups(@PathVariable String accountId) {

        List<BillingSetupDTO> billingSetupDTOs = billingSetupService.getAccountBillInfo(accountId);

        return generalService.prepareSuccessResponse(billingSetupDTOs);
    }

    @GetMapping("/all/byPackageName/{packageName}")
    public Response getBillingSetupByPackage(@PathVariable String packageName) {

        List<BillingSetupDTO> billingSetupDTOs = billingSetupService.getBillInfoByPackageName(packageName);

        return generalService.prepareSuccessResponse(billingSetupDTOs);
    }

    @GetMapping("/getTotalBillCharge/{packageName}/{country}")
    public Response getTotalBillCharge(@PathVariable String packageName , @PathVariable String country) {

        BillChargeDTO billChargeDTO = billingSetupService.getChargeAmount(packageName , country);
        return generalService.prepareSuccessResponse(billChargeDTO);
    }

    @GetMapping("/activeSubscriptions/{accountId}")
    public Response getActiveSubscriptions(@PathVariable String accountId) {

        List<BillingSetupDTO> billingSetupDTOs = billingSetupService.getActiveSubscriptions(accountId);

        return generalService.prepareSuccessResponse(billingSetupDTOs);
    }

}
