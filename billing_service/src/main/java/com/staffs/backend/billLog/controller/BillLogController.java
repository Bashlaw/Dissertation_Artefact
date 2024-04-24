package com.staffs.backend.billLog.controller;

import com.staffs.backend.billLog.dto.BillLogDTO;
import com.staffs.backend.billLog.dto.BillLogListDTO;
import com.staffs.backend.billLog.dto.BillLogRequestDTO;
import com.staffs.backend.billLog.service.BillLogService;
import com.staffs.backend.general.dto.PageableRequestDTO;
import com.staffs.backend.general.dto.Response;
import com.staffs.backend.general.service.GeneralService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping("/billing/api/v1/billLog")
public class BillLogController {

    private final BillLogService billLogService;
    private final GeneralService generalService;

    @PostMapping("/log")
    public Response logBillInfo(@RequestBody BillLogRequestDTO dto) {

        BillLogDTO billLogDTO = billLogService.logBillingDetail(dto);

        return generalService.prepareSuccessResponse(billLogDTO);
    }

    @GetMapping("/all/{accountId}")
    public Response getBillLogByAccountId(@Valid @RequestBody PageableRequestDTO requestDTO , @PathVariable String accountId) {

        BillLogListDTO billLogDTOs = billLogService.getBillLogByAccountId(requestDTO , accountId);

        return generalService.prepareSuccessResponse(billLogDTOs);
    }

    @GetMapping("/all/getByBillSetup/{accountId}/{packageName}")
    public Response getBillLogByAccountIdAndBill(@PathVariable String accountId , @PathVariable String packageName) {

        List<BillLogDTO> billLogDTOs = billLogService.getBillLogByAccountIdAndBillSetup(accountId , packageName);

        return generalService.prepareSuccessResponse(billLogDTOs);
    }

    @GetMapping("/all/getByItem/{itemId}/{accountId}")
    public Response getBillLogByAccountIdAndItem(@Valid @RequestBody PageableRequestDTO requestDTO , @PathVariable Long itemId , @PathVariable String accountId) {

        BillLogListDTO billLogDTOs = billLogService.getBillLogByAccountIdAndItem(requestDTO , accountId , itemId);

        return generalService.prepareSuccessResponse(billLogDTOs);
    }

    @GetMapping("/single/{billLogId}")
    public Response getSingleBillLog(@PathVariable String billLogId) {

        BillLogDTO billLogDTO = billLogService.getSingleDTOBillLog(billLogId);

        return generalService.prepareSuccessResponse(billLogDTO);
    }

}
