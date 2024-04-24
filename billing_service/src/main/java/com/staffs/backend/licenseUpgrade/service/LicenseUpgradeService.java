package com.staffs.backend.licenseUpgrade.service;

import com.staffs.backend.general.dto.UpgradeChargeDTO;
import com.staffs.backend.licenseUpgrade.dto.LicenseUpgradeDTO;
import com.staffs.backend.licenseUpgrade.dto.LicenseUpgradeRequestDTO;
import com.staffs.backend.licenseUpgrade.dto.UpgradeChargeRequestDTO;

import java.util.List;

public interface LicenseUpgradeService {

    LicenseUpgradeDTO logLicenseUpgrade(LicenseUpgradeRequestDTO dto);

    LicenseUpgradeDTO getValidLicenseUpgradeById(Long licenseUpgradeId);

    List<LicenseUpgradeDTO> getLicenseUpgradeByAccountId(String accountId);

    UpgradeChargeDTO getChargeAmount(UpgradeChargeRequestDTO dto);

}
