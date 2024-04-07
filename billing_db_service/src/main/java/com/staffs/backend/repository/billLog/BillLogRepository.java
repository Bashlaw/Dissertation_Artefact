package com.staffs.backend.repository.billLog;

import com.staffs.backend.entity.billLog.BillLog;
import com.staffs.backend.entity.billingSetup.model.BillingSetup;
import com.staffs.backend.entity.item.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BillLogRepository extends JpaRepository<BillLog, String> {

    List<BillLog> findByAccountIdOrderByCreatedAtDesc(String accountId);

    List<BillLog> findByAccountIdAndBillingSetupOrderByCreatedAtDesc(String accountId , BillingSetup billingSetup);

    List<BillLog> findByAccountIdAndItemOrderByCreatedAtDesc(String accountId , Item item);

    BillLog findByBillLogId(String billLogId);

    Boolean existsByTransRef(String transRef);

    BillLog findByAccountIdAndTransRef(String accountId , String transRef);

    @Query(value = "select SUM(b.item_quantity) from bill_log b where encode(b.billing_setup_bill_id, 'hex') = ?1 AND b.item_item_id = ?2", nativeQuery = true)
    Long getUsedItemQuantity(String billId , Long itemId);

}
