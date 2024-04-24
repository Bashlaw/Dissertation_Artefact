package com.staffs.backend.billingSetup.service;

import com.staffs.backend.billingSetup.dto.BillingSetupDTO;
import com.staffs.backend.billingSetup.dto.BillingSetupRequestDTO;
import com.staffs.backend.entity.billingSetup.iModel.BillingSetupBasicInfoI;
import com.staffs.backend.entity.billingSetup.model.BillingSetup;
import com.staffs.backend.general.dto.BillChargeDTO;
import com.staffs.backend.licenseUpgrade.dto.LicenseUpgradeRequestDTO;

import java.util.List;
import java.util.UUID;

public interface BillingSetupService {

    BillingSetupDTO setupAccountBill(BillingSetupRequestDTO dto);

    BillingSetupDTO getAccountBillDTOInfo(String accountId , String packageName);

    List<BillingSetupDTO> getBillInfoByPackageName(String packageName);

    List<BillingSetupDTO> getAccountBillInfo(String accountId);

    BillingSetup getCurrentAccountBillInfo(String accountId , String packageName);

    void invalidateBill(UUID billId);

    BillingSetupDTO upgradeBill(LicenseUpgradeRequestDTO licenseUpgradeRequestDTO , double charge , String currency);

    BillChargeDTO getChargeAmount(String packageName , String country);

    List<BillingSetupDTO> getActiveSubscriptions(String accountId);

    BillingSetup getNewBillInfo(String trans_Ref);

    BillingSetupDTO getNewBillByBillId(UUID billId);

    List<BillingSetupBasicInfoI> getOutdatedBills();

    List<String> getOutdatedBilling();

    BillingSetup getOutdatedBill(String billId);

    void updateOutdatedBills(List<BillingSetup> billingSetups);

    List<BillingSetupDTO> renewBills(List<BillingSetup> billingSetups);

    BillingSetup getBillSetupByBillLogId(String billLogId);

}
