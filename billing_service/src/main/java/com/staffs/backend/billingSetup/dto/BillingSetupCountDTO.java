package com.staffs.backend.billingSetup.dto;

import lombok.Data;

@Data
public class BillingSetupCountDTO {

    private Long totalSubscription;

    private Long totalActiveSubscription;

    private Long totalExpiredSubscription;

}
