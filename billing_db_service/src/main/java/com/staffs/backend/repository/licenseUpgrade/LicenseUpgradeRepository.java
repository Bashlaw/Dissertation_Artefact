package com.staffs.backend.repository.licenseUpgrade;

import com.staffs.backend.entity.billingSetup.model.BillingSetup;
import com.staffs.backend.entity.licenseUpgrade.LicenseUpgrade;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LicenseUpgradeRepository extends JpaRepository<LicenseUpgrade, Long> {

    boolean existsByInitialBillId(UUID initialBillId);

    List<LicenseUpgrade> findByBillingSetup(BillingSetup billingSetup);

    Optional<LicenseUpgrade> findByLicenseUpgradeId(Long licenseUpgradeId);

    List<LicenseUpgrade> findByAccountId(String accountId);

    Optional<LicenseUpgrade> findByBillingSetup_BillId(UUID billId);

    Optional<LicenseUpgrade> findByInitialBillId(UUID initialBillId);

}
