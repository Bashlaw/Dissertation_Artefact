package com.staffs.backend.transactionLog.controller;

import com.staffs.backend.general.dto.Response;
import com.staffs.backend.general.service.GeneralService;
import com.staffs.backend.transactionLog.dto.TransactionLogDTO;
import com.staffs.backend.transactionLog.service.TransactionLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping("/billing/api/v1/transaction")
public class TransactionLogController {

    private final GeneralService generalService;
    private final TransactionLogService transactionLogService;

    @GetMapping("/all")
    public Response getTransactions(@RequestParam(name = "bill logID") String billLogID) {

        List<TransactionLogDTO> transactionLogDTOs = transactionLogService.getTransactions(billLogID);

        return generalService.prepareSuccessResponse(transactionLogDTOs);
    }

    @GetMapping("/single")
    public Response getTransaction(@RequestParam(name = "trans ref") String transRef) {

        TransactionLogDTO transactionLogDTO = transactionLogService.getSingleTransaction(transRef);

        return generalService.prepareSuccessResponse(transactionLogDTO);
    }

}
