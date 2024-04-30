package com.staffs.backend.otp.controller;

import com.staffs.backend.enums.user.UserType;
import com.staffs.backend.general.dto.MessageConstant;
import com.staffs.backend.general.dto.Response;
import com.staffs.backend.general.service.GeneralService;
import com.staffs.backend.otp.service.OTPService;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/otp")
public class OTPController {

    private final OTPService otpService;
    private final GeneralService generalService;

    @GetMapping("/generate/admin")
    public Response generateOTPForAgent(@Parameter String recipient) {
        otpService.generateOTP(recipient , UserType.ADMIN , 0);
        return generalService.prepareSuccessResponse(MessageConstant.OTP_GENERATED_SUCCESSFUL);
    }

    @GetMapping("/generate/customer")
    public Response generateOTPForBeneficiary(@Parameter String recipient) {
        otpService.generateOTP(recipient , UserType.CUSTOMER , 0);
        return generalService.prepareSuccessResponse(MessageConstant.OTP_GENERATED_SUCCESSFUL);
    }

}
