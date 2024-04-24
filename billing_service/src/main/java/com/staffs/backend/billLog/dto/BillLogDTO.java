package com.staffs.backend.billLog.dto;

import com.staffs.backend.item.dto.ItemDTO;
import com.staffs.backend.paymentSource.dto.PaymentSourceDTO;
import lombok.Data;

@Data
public class BillLogDTO {

    private String billLogId;

    private String accountId;

    private ItemDTO itemDTO;

    private Long itemQuantity;

    private String itemRef;

    private double chargeAmount;

    private String paymentStatus;

    private PaymentSourceDTO paymentSource;

    private String transRef;

    private String paymentURL;

}
