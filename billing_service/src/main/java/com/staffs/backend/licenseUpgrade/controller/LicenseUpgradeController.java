package com.staffs.backend.licenseUpgrade.controller;

import com.staffs.backend.general.dto.Response;
import com.staffs.backend.general.dto.UpgradeChargeDTO;
import com.staffs.backend.general.service.GeneralService;
import com.staffs.backend.licenseUpgrade.dto.LicenseUpgradeDTO;
import com.staffs.backend.licenseUpgrade.dto.LicenseUpgradeRequestDTO;
import com.staffs.backend.licenseUpgrade.dto.UpgradeChargeRequestDTO;
import com.staffs.backend.licenseUpgrade.service.LicenseUpgradeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping("/billing/api/v1/licenseUpgrade")
public class LicenseUpgradeController {

    private final GeneralService generalService;
    private final LicenseUpgradeService licenseUpgradeService;

    @PostMapping("/add")
    public Response licenseUpgradeRequest(@RequestBody LicenseUpgradeRequestDTO dto) {

        LicenseUpgradeDTO licenseUpgradeDTO = licenseUpgradeService.logLicenseUpgrade(dto);

        return generalService.prepareSuccessResponse(licenseUpgradeDTO);
    }

    @GetMapping("/single/{licenseUpgradeId}")
    public Response getActiveLicenseUpgradeInfo(@PathVariable Long licenseUpgradeId) {

        LicenseUpgradeDTO licenseUpgradeDTO = licenseUpgradeService.getValidLicenseUpgradeById(licenseUpgradeId);

        return generalService.prepareSuccessResponse(licenseUpgradeDTO);
    }

    @GetMapping("/all/{accountId}")
    public Response getLicenseUpgradeHistory(@PathVariable String accountId) {

        List<LicenseUpgradeDTO> licenseUpgradeDTOs = licenseUpgradeService.getLicenseUpgradeByAccountId(accountId);

        return generalService.prepareSuccessResponse(licenseUpgradeDTOs);
    }

    @GetMapping("/getTotalUpgradeCharge")
    public Response getTotalUpgradeCharge(@RequestBody UpgradeChargeRequestDTO dto) {

        UpgradeChargeDTO upgradeChargeDTO = licenseUpgradeService.getChargeAmount(dto);

        return generalService.prepareSuccessResponse(upgradeChargeDTO);
    }

}
