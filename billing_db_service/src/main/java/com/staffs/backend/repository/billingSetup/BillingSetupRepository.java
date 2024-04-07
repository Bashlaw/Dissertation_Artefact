package com.staffs.backend.repository.billingSetup;

import com.staffs.backend.entity.billingSetup.iModel.BillingSetupBasicInfoI;
import com.staffs.backend.entity.billingSetup.model.BillingSetup;
import com.staffs.backend.entity.billingSetup.model.BillingSetupID;
import com.staffs.backend.entity.licenseType.LicenseType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BillingSetupRepository extends JpaRepository<BillingSetup, BillingSetupID> {

    boolean existsByAccountIdAndPackages_packageType_licenseTypeAndValidate(String accountId, LicenseType licenseType, boolean validate);

    BillingSetup findByAccountIdAndPackages_packageNameAndPackages_packageType_licenseTypeAndValidate(String accountId, String packageName, LicenseType licenseType, boolean validate);

    boolean existsByAccountIdAndPackages_packageNameAndPackages_packageType_licenseTypeAndPaymentSuccess(String accountId, String packageName, LicenseType licenseType, boolean isPaymentSuccess);

    BillingSetup findFirstByAccountIdAndPackages_packageNameAndPackages_packageType_licenseTypeAndPaymentSuccessOrderByCreatedAtDesc(String accountId, String packageName, LicenseType licenseType, boolean isPaymentSuccess);

    BillingSetup findByBillIdAndValidate(UUID billId, boolean validate);

    List<BillingSetup> findByPackages_packageNameAndValidate(String packageName, boolean validate);

    BillingSetup findByAccountIdAndValidateAndPackages_packageName(String accountId, boolean validate, String packageName);

    List<BillingSetup> findByAccountId(String accountId);

    BillingSetup findByAccountIdAndValidateAndPaymentSuccessAndPaymentRef(String accountId, boolean validate, boolean isPaymentSuccess, String paymentRef);

    List<BillingSetup> findByAccountIdAndValidateAndPaymentSuccess(String accountId, boolean validate, boolean isPaymentSuccess);

    BillingSetup findByPaymentRef(String paymentRef);

    BillingSetup findByBillId(UUID billId);

    @Query(value = "select b.account_id, encode(bill_id, 'hex') as bill_id, b.charge_amount, b.valid_from, b.valid_till from billing_setup b where b.valid_till  <= NOW() and b.validate = true and b.payment_success = true", nativeQuery = true)
    List<BillingSetupBasicInfoI> getOutdatedBills();

    @Query(value = "select b.* from billing_setup b where b.valid_till  <= NOW() and b.validate = true and b.payment_success = true AND encode(bill_id, 'hex')  = ?1", nativeQuery = true)
    BillingSetup getOutdatedBill(String billId);

    @Query(value = "select b.account_id from billing_setup b where b.valid_till  <= NOW() and b.validate = true and b.payment_success = true", nativeQuery = true)
    List<String> getOutdatedBilling();

    Optional<BillingSetup> findByBillLogs_billLogId(String billLogId);

}
