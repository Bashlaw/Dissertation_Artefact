package com.staffs.backend.general.dto;

import com.staffs.backend.paymentSource.dto.PaymentSourceDTO;
import lombok.Data;

import java.util.List;

@Data
public class BillChargeDTO {

    private String clientName;

    private double totalChargeAmount;

    private String billMethod;

    private String packageName;

    private String currency;

    private String period;

    private List<PaymentSourceDTO> paymentSourceDTO;

}
