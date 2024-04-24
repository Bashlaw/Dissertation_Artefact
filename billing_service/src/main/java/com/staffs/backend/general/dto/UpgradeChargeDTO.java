package com.staffs.backend.general.dto;

import com.staffs.backend.paymentSource.dto.PaymentSourceDTO;
import lombok.Data;

import java.util.List;

@Data
public class UpgradeChargeDTO {

    private String clientName;

    private double totalChargeAmount;

    private String packageNameFrom;

    private String packageNameTo;

    private String currency;

    private String period;

    private List<PaymentSourceDTO> paymentSourceDTO;

}
