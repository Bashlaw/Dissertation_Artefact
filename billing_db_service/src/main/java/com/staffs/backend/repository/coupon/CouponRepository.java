package com.staffs.backend.repository.coupon;

import com.staffs.backend.entity.coupon.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CouponRepository extends JpaRepository<Coupon, String> {

    Optional<Coupon> findByCode(String code);

    Optional<Coupon> findFirstByBillLog_BillingSetup_BillIdAndBillLog_Item_ItemIdAndUsedOrderByCreatedAtDesc(UUID billId, Long itemId, boolean used);

    List<Coupon> findByBillLog_BillingSetup_BillIdAndUsed(UUID billId, boolean used);

    List<Coupon> findByBillLog_BillingSetup_BillIdAndBillLog_Item_ItemIdAndUsedOrderByCreatedAtDesc(UUID billId, Long itemId, boolean used);

}
