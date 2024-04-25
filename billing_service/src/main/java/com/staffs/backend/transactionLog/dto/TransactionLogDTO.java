package com.staffs.backend.transactionLog.dto;

import com.staffs.backend.billLog.dto.BillLogDTO;
import lombok.Data;

@Data
public class TransactionLogDTO {

    private BillLogDTO billLogDTO;

    private String bookStatus;

    private String transRef;

    private String reason;

}
