package com.staffs.backend.licenseUpgrade.dto;

import com.staffs.backend.billingSetup.dto.BillingSetupDTO;
import com.staffs.backend.packages.dto.PackageDTO;
import lombok.Data;

@Data
public class LicenseUpgradeDTO {

    private Long licenseUpgradeId;

    private PackageDTO upgradedFrom;

    private PackageDTO upgradedTo;

    private BillingSetupDTO billingSetup;

}
