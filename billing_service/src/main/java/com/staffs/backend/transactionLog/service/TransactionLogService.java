package com.staffs.backend.transactionLog.service;

import com.staffs.backend.entity.transactionLog.TransactionLog;
import com.staffs.backend.transactionLog.dto.TransactionLogDTO;

import java.util.List;

public interface TransactionLogService {

    TransactionLogDTO logTransaction(TransactionLog dto);

    TransactionLogDTO getSingleTransaction(String transRef);

    List<TransactionLogDTO> getTransactions(String billLogId);

}
