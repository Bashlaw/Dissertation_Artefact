package com.staffs.backend.billLog.service;

import com.staffs.backend.billLog.dto.BillLogDTO;
import com.staffs.backend.billLog.dto.BillLogListDTO;
import com.staffs.backend.billLog.dto.BillLogRequestDTO;
import com.staffs.backend.entity.billLog.BillLog;
import com.staffs.backend.general.dto.PageableRequestDTO;

import java.util.List;

public interface BillLogService {

    BillLogDTO logBillingDetail(BillLogRequestDTO dto);

    BillLogDTO getSingleDTOBillLog(String billLogId);

    BillLogListDTO getBillLogByAccountId(PageableRequestDTO requestDTO , String accountId);

    List<BillLogDTO> getBillLogByAccountIdAndBillSetup(String accountId , String packageName);

    List<BillLogDTO> getBillLogsByAccountId4Bill(String accountId);

    BillLogListDTO getBillLogByAccountIdAndItem(PageableRequestDTO requestDTO , String accountId , Long itemId);

}
