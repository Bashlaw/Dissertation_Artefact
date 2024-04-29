package com.staffs.backend.user.controller;

import com.staffs.backend.general.dto.ChangePasswordRequestDTO;
import com.staffs.backend.general.dto.MessageConstant;
import com.staffs.backend.general.dto.Response;
import com.staffs.backend.general.service.GeneralService;
import com.staffs.backend.user.service.UserService;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
public class UserController {

    private final UserService userService;
    private final GeneralService generalService;

    @PostMapping("/forgetPassword/{identifier}")
    public Response forgetPassword(@PathVariable String identifier) {

        userService.requestResetPassword(identifier);

        return generalService.prepareSuccessResponse(MessageConstant.FORGET_PASSWORD_REQUEST_SUCCESSFUL);

    }

    @PostMapping("/validateForgetPasswordToken")
    public Response validateForgetPasswordToken(@RequestParam("token") String token , @RequestParam("identifier") String identifier) {

        userService.validateForgetPasswordToken(token , identifier);

        return generalService.prepareSuccessResponse(MessageConstant.FORGET_PASSWORD_REQUEST_SUCCESSFUL);

    }

    @PostMapping("/resetPassword")
    public Response resetPassword(@RequestParam("password") String password , @RequestParam("confirmPassword") String confirmPassword , @RequestParam("identifier") String identifier) {

        userService.resetPassword(password , confirmPassword , identifier);

        return generalService.prepareSuccessResponse(MessageConstant.PASSWORD_RESET_SUCCESSFUL);

    }

    @PostMapping("/changePassword")
    public Response changePassword(@Parameter(hidden = true) Principal principal , @Valid @RequestBody ChangePasswordRequestDTO dto) {

        userService.changePassword(principal.getName() , dto);

        return generalService.prepareSuccessResponse(MessageConstant.PASSWORD_CHANGE_SUCCESSFUL);

    }

}
