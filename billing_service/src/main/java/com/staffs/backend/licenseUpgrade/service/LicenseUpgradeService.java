package com.staffs.backend.licenseUpgrade.service;

import com.staffs.backend.entity.licenseUpgrade.LicenseUpgrade;
import com.staffs.backend.general.dto.UpgradeChargeDTO;
import com.staffs.backend.licenseUpgrade.dto.LicenseUpgradeDTO;
import com.staffs.backend.licenseUpgrade.dto.LicenseUpgradeRequestDTO;
import com.staffs.backend.licenseUpgrade.dto.UpgradeChargeRequestDTO;

import java.util.List;
import java.util.UUID;

public interface LicenseUpgradeService {

    LicenseUpgradeDTO logLicenseUpgrade(LicenseUpgradeRequestDTO dto);

    LicenseUpgradeDTO getValidLicenseUpgradeById(Long licenseUpgradeId);

    List<LicenseUpgradeDTO> getLicenseUpgradeByAccountId(String accountId);

    UpgradeChargeDTO getChargeAmount(UpgradeChargeRequestDTO dto);

}
