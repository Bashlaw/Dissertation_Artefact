package com.staffs.backend.entity.billingSetup.model;

import com.staffs.backend.entity.billingMethod.BillingMethod;
import lombok.Data;

import java.io.Serializable;

@Data
public class BillingSetupID implements Serializable {

    private Long billingSetupId;

    private String accountId;

    private BillingMethod billingMethod;

}
