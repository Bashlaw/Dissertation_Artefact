package com.staffs.backend.changeLog.service;

import com.staffs.backend.changeLog.dto.ChangeLogDTO;
import com.staffs.backend.changeLog.dto.ChangeLogListDTO;
import com.staffs.backend.general.dto.PageableRequestDTO;

public interface ChangeLogService {

    void logOperatorAction(String module , Long operator , String action);

    ChangeLogListDTO getChangeLogByOperator(PageableRequestDTO requestDTO , Long operatorId);

    ChangeLogListDTO getChangeLogByModule(PageableRequestDTO requestDTO , String module);

    ChangeLogListDTO getAuditLogByOperatorAndModule(PageableRequestDTO requestDTO , Long operatorId , String module);

    ChangeLogDTO getSingleLog(String logId);

}
