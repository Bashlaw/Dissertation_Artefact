package com.staffs.backend.billLog.controller;

import com.staffs.backend.billLog.dto.BillLogRequestDTO;
import com.staffs.backend.billLog.service.BillLogService;
import com.staffs.backend.general.dto.PageableRequestDTO;
import com.staffs.backend.general.dto.Response;
import com.staffs.backend.general.service.GeneralService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping("/billing/api/v1/billLog")
public class BillLogController {

    private final BillLogService billLogService;
    private final GeneralService generalService;

    @PostMapping("/log")
    public Response logBillInfo(@RequestBody BillLogRequestDTO dto) {
        return generalService.prepareSuccessResponse(billLogService.logBillingDetail(dto));
    }

    @GetMapping("/all/{accountId}")
    public Response getBillLogByAccountId(@Valid @RequestBody PageableRequestDTO requestDTO , @PathVariable String accountId) {
        return generalService.prepareSuccessResponse(billLogService.getBillLogByAccountId(requestDTO , accountId));
    }

    @GetMapping("/all/getByBillSetup/{accountId}/{packageName}")
    public Response getBillLogByAccountIdAndBill(@PathVariable String accountId , @PathVariable String packageName) {
        return generalService.prepareSuccessResponse(billLogService.getBillLogByAccountIdAndBillSetup(accountId , packageName));
    }

    @GetMapping("/all/getByItem/{itemId}/{accountId}")
    public Response getBillLogByAccountIdAndItem(@Valid @RequestBody PageableRequestDTO requestDTO , @PathVariable Long itemId , @PathVariable String accountId) {
        return generalService.prepareSuccessResponse(billLogService.getBillLogByAccountIdAndItem(requestDTO , accountId , itemId));
    }

    @GetMapping("/single/{billLogId}")
    public Response getSingleBillLog(@PathVariable String billLogId) {
        return generalService.prepareSuccessResponse(billLogService.getSingleDTOBillLog(billLogId));
    }

}
