package com.staffs.backend.entity.licenseUpgrade;

import com.staffs.backend.entity.billingSetup.model.BillingSetup;
import com.staffs.backend.entity.packages.Packages;
import com.staffs.backend.utils.BaseEntity;
import lombok.*;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
public class LicenseUpgrade extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long licenseUpgradeId;

    @ToString.Exclude
    @ManyToOne(optional = false)
    private Packages upgradedFrom;

    @ToString.Exclude
    @ManyToOne(optional = false)
    private Packages upgradedTo;

    @ToString.Exclude
    @ManyToOne(optional = false)
    private BillingSetup billingSetup;

    @Column(unique = true, updatable = false, nullable = false)
    private UUID licenseUpgradeBillId;

    @Column(updatable = false, nullable = false)
    private String accountId;

    @Column(unique = true, updatable = false, nullable = false)
    private UUID initialBillId;

}
