package com.staffs.backend.billingSetup.dto;

import com.staffs.backend.billLog.dto.BillLogDTO;
import com.staffs.backend.billingMethod.dto.BillingMethodDTO;
import com.staffs.backend.packages.dto.PackageDTO;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
public class BillingSetupDTO {

    private UUID billId;

    private String accountId;

    private BillingMethodDTO billingMethod;

    private String meansOfPayment;

    private LocalDateTime validFrom;

    private LocalDateTime validTill;

    private boolean licenseUpgrade;

    private double chargeAmount;

    private String transactionRef;

    private String paymentUrl;

    private String currency;

    private List<BillLogDTO> billLogDTOs;

    private List<PackageDTO> packages;

    public void setPackageList(List<PackageDTO> packages) {
        this.packages = packages;
    }

    public void setPackages(PackageDTO packages) {
        List<PackageDTO> packageList = new ArrayList<>();
        packageList.add(packages);
        setPackageList(packageList);
    }

}
