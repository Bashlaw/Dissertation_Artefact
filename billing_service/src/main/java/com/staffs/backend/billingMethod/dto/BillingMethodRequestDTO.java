package com.staffs.backend.billingMethod.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class BillingMethodRequestDTO {

    @NotEmpty(message = "billing method name cannot be empty")
    private String billingMethodName;

    private String description;

    private boolean validate;

}
