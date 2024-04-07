package com.staffs.backend.repository.transactionLog;

import com.staffs.backend.entity.billLog.BillLog;
import com.staffs.backend.entity.transactionLog.TransactionLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionLogRepository extends JpaRepository<TransactionLog, String> {

    List<TransactionLog> findByBillLogOrderByCreatedAtDesc(BillLog billLog);

    TransactionLog findByTransRef(String transRef);

}
