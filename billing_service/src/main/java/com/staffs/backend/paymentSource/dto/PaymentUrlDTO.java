package com.staffs.backend.paymentSource.dto;

import lombok.Data;

@Data
public class PaymentUrlDTO {

    private String url;

    private PaymentSourceDTO paymentSource;

}
