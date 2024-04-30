package com.staffs.backend.security.controller;

import com.staffs.backend.enums.log.AccessType;
import com.staffs.backend.general.dto.Response;
import com.staffs.backend.general.service.GeneralService;
import com.staffs.backend.log.service.AccessLogService;
import com.staffs.backend.security.config.JwtTokenUtil;
import com.staffs.backend.user.service.UserService;
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
@RequestMapping("/api/v1/refresh")
public class RefreshTokenController {

    private final UserService userService;
    private final JwtTokenUtil jwtTokenUtil;
    private final GeneralService generalService;
    private final AccessLogService accessLogService;

    @PostMapping
    public Response refresh(HttpServletRequest request) {

        String token = jwtTokenUtil.resolveToken(request);
        String username = jwtTokenUtil.getUsernameFromToken(token);

        token = jwtTokenUtil.refreshToken(token);

        if (username != null) {
            //log access
            accessLogService.logUserAccess(userService.getUser(username).getId() , request.getHeader("User-Agent") , request.getRemoteAddr() , AccessType.REFRESH_TOKEN.code);
        }

        return generalService.prepareSuccessResponse(token);
    }

}
