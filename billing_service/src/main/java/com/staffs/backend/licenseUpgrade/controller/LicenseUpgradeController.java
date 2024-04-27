package com.staffs.backend.licenseUpgrade.controller;

import com.staffs.backend.general.dto.Response;
import com.staffs.backend.general.service.GeneralService;
import com.staffs.backend.licenseUpgrade.dto.LicenseUpgradeRequestDTO;
import com.staffs.backend.licenseUpgrade.dto.UpgradeChargeRequestDTO;
import com.staffs.backend.licenseUpgrade.service.LicenseUpgradeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping("/billing/api/v1/licenseUpgrade")
public class LicenseUpgradeController {

    private final GeneralService generalService;
    private final LicenseUpgradeService licenseUpgradeService;

    @PostMapping("/add")
    public Response licenseUpgradeRequest(@RequestBody LicenseUpgradeRequestDTO dto) {
        return generalService.prepareSuccessResponse(licenseUpgradeService.logLicenseUpgrade(dto));
    }

    @GetMapping("/single/{licenseUpgradeId}")
    public Response getActiveLicenseUpgradeInfo(@PathVariable Long licenseUpgradeId) {
        return generalService.prepareSuccessResponse(licenseUpgradeService.getValidLicenseUpgradeById(licenseUpgradeId));
    }

    @GetMapping("/all/{accountId}")
    public Response getLicenseUpgradeHistory(@PathVariable String accountId) {
        return generalService.prepareSuccessResponse(licenseUpgradeService.getLicenseUpgradeByAccountId(accountId));
    }

    @GetMapping("/getTotalUpgradeCharge")
    public Response getTotalUpgradeCharge(@RequestBody UpgradeChargeRequestDTO dto) {
        return generalService.prepareSuccessResponse(licenseUpgradeService.getChargeAmount(dto));
    }

}
