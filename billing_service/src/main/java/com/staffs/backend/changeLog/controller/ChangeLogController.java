package com.staffs.backend.changeLog.controller;

import com.staffs.backend.changeLog.service.ChangeLogService;
import com.staffs.backend.general.dto.PageableRequestDTO;
import com.staffs.backend.general.dto.Response;
import com.staffs.backend.general.service.GeneralService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping("billing/api/v1/changeLog")
public class ChangeLogController {

    private final GeneralService generalService;
    private final ChangeLogService changeLogService;

    @GetMapping("/single/getByLogId/{logId}")
    public Response getChangeLogById(@PathVariable String logId) {

        return generalService.prepareSuccessResponse(changeLogService.getSingleLog(logId));

    }

    @GetMapping("/all/getByOperator/{operatorId}")
    public Response getChangeLogByOperatorId(@PathVariable Long operatorId , @Valid @RequestBody PageableRequestDTO requestDTO) {

        return generalService.prepareSuccessResponse(changeLogService.getChangeLogByOperator(requestDTO , operatorId));

    }

    @GetMapping("/all/getByModule/{module}")
    public Response getAuditLogByModule(@PathVariable String module , @Valid @RequestBody PageableRequestDTO requestDTO) {

        return generalService.prepareSuccessResponse(changeLogService.getChangeLogByModule(requestDTO , module));

    }

    @GetMapping("/all/{operatorId}/{module}")
    public Response getAuditLogByOperatorId(@PathVariable Long operatorId , @PathVariable String module , @Valid @RequestBody PageableRequestDTO requestDTO) {

        return generalService.prepareSuccessResponse(changeLogService.getAuditLogByOperatorAndModule(requestDTO , operatorId , module));

    }

}
