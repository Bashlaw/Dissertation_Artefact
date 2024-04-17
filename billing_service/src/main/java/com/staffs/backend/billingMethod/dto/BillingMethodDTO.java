package com.staffs.backend.billingMethod.dto;

import lombok.Data;

@Data
public class BillingMethodDTO {

    private Long billingMethodID;

    private String billingMethodName;

    private String description;

    private boolean validate;

}
