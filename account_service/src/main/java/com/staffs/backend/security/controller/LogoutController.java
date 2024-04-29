package com.staffs.backend.security.controller;

import com.staffs.backend.exceptions.GeneralException;
import com.staffs.backend.general.dto.MessageConstant;
import com.staffs.backend.general.dto.Response;
import com.staffs.backend.general.enums.ResponseCodeAndMessage;
import com.staffs.backend.general.service.GeneralService;
import com.staffs.backend.security.config.JwtTokenUtil;
import com.staffs.backend.security.service.JwtUserDetailsService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Component
@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/logout")
public class LogoutController {

    private final JwtTokenUtil jwtTokenUtil;
    private final GeneralService generalService;
    private final JwtUserDetailsService jwtUserDetailsService;

    @PostMapping
    public Response logout(HttpServletRequest request) {
        String token = jwtTokenUtil.resolveToken(request);

        if (token == null || token.isBlank())
            throw new GeneralException(ResponseCodeAndMessage.CLIENT_NOT_ALLOWED.responseCode , MessageConstant.ONLY_LOGGED_IN_USERS_CAN_PERFORM_THIS_ACTION);

        jwtUserDetailsService.blacklistToken(token);

        return generalService.prepareSuccessResponse(MessageConstant.LOGOUT_SUCCESSFULLY);

    }

}
