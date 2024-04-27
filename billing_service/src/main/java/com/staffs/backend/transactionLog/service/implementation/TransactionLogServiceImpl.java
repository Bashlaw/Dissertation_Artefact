package com.staffs.backend.transactionLog.service.implementation;

import com.staffs.backend.entity.billLog.BillLog;
import com.staffs.backend.entity.transactionLog.TransactionLog;
import com.staffs.backend.exceptions.GeneralException;
import com.staffs.backend.general.dto.MessageConstant;
import com.staffs.backend.general.enums.ResponseCodeAndMessage;
import com.staffs.backend.repository.billLog.BillLogRepository;
import com.staffs.backend.repository.transactionLog.TransactionLogRepository;
import com.staffs.backend.transactionLog.dto.TransactionLogDTO;
import com.staffs.backend.transactionLog.service.TransactionLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionLogServiceImpl implements TransactionLogService {

    private final BillLogRepository billLogRepository;
    private final TransactionLogRepository transactionLogRepository;

    @Override
    public TransactionLogDTO logTransaction(TransactionLog dto) {
        log.info("saving trans log info!");

        //save to DB
        dto = transactionLogRepository.save(dto);

        return getTransactionLogDTO(dto);
    }

    @Override
    public TransactionLogDTO getSingleTransaction(String transRef) {
        return getTransactionLogDTO(getTransactionByTransRef(transRef));
    }

    @Override
    public List<TransactionLogDTO> getTransactions(String billLogId) {
        log.info("getting TransactionLog DTOs info by BillLog");

        //get Bill log
        BillLog billLog = billLogRepository.findByBillLogId(billLogId).orElseThrow(() -> new GeneralException(ResponseCodeAndMessage.RECORD_NOT_FOUND.responseCode , MessageConstant.RECORD_NOT_FOUND));

        List<TransactionLog> transactionLogs = getTransactionsByBillLog(billLog);

        return transactionLogs.stream().map(this::getTransactionLogDTO).collect(Collectors.toList());

    }

    private TransactionLog getTransactionByTransRef(String transRef) {
        return transactionLogRepository.findByTransRef(transRef);
    }

    private List<TransactionLog> getTransactionsByBillLog(BillLog billLog) {
        return transactionLogRepository.findByBillLogOrderByCreatedAtDesc(billLog);
    }

    private TransactionLogDTO getTransactionLogDTO(TransactionLog transactionLog) {
        log.info("converting transactionLog to TransactionLogDTO");

        TransactionLogDTO transactionLogDTO = new TransactionLogDTO();
        BeanUtils.copyProperties(transactionLog , transactionLogDTO);

        //get book status
        String bookStatus;
        if (transactionLog.isCredit()) {
            bookStatus = "Credit";
        } else {
            bookStatus = "debit";
        }
        transactionLogDTO.setBookStatus(bookStatus);

        return transactionLogDTO;
    }

}
