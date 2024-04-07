package com.staffs.backend.entity.billingSetup.iModel;

import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDateTime;
import java.util.UUID;

public interface BillingSetupBasicInfoI {

    @Value(value = "#{target.bill_id}")
    String billId();

    @Value(value = "#{target.account_id}")
    String accountId();

    @Value(value = "#{target.valid_from}")
    LocalDateTime validFrom();

    @Value(value = "#{target.valid_till}")
    LocalDateTime validTill();

    @Value(value = "#{target.charge_amount}")
    double chargeAmount();

}
