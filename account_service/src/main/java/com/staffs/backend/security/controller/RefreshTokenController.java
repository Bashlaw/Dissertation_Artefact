package com.staffs.backend.security.controller;

import com.staffs.backend.general.dto.Response;
import com.staffs.backend.general.service.GeneralService;
import com.staffs.backend.security.config.JwtTokenUtil;
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

    private final JwtTokenUtil jwtTokenUtil;
    private final GeneralService generalService;

    @PostMapping
    public Response refresh(HttpServletRequest request) {

        String token = jwtTokenUtil.resolveToken(request);

        token = jwtTokenUtil.refreshToken(token);

        return generalService.prepareSuccessResponse(token);
    }

}
