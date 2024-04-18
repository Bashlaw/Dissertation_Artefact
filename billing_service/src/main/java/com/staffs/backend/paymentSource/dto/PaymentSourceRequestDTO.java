package com.staffs.backend.paymentSource.dto;

import lombok.Data;

import java.util.List;

@Data
public class PaymentSourceRequestDTO {

    private String paymentSourceCode;

    private List<String> countryCodeList;

    private List<String> urls;

}
