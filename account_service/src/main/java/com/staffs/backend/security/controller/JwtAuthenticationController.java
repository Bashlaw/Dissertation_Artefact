package com.staffs.backend.security.controller;

import com.staffs.backend.exceptions.GeneralException;
import com.staffs.backend.general.dto.MessageConstant;
import com.staffs.backend.general.enums.ResponseCodeAndMessage;
import com.staffs.backend.security.config.JwtTokenUtil;
import com.staffs.backend.security.dto.BillingUser;
import com.staffs.backend.security.dto.UserLoginRequest;
import com.staffs.backend.security.dto.UserLoginResponse;
import com.staffs.backend.security.service.JwtUserDetailsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.util.Locale;
import java.util.Objects;

@Component
@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/authenticate")
public class JwtAuthenticationController {

    private final AuthenticationManager authenticationManager;

    private final JwtTokenUtil jwtTokenUtil;

    private final JwtUserDetailsService jwtUserDetailsService;

    @PostMapping()
    public ResponseEntity<UserLoginResponse> userAuthenticationToken(@Valid @RequestBody UserLoginRequest authenticationRequest) throws Exception {

        String username = authenticationRequest.getEmail().toLowerCase(Locale.ROOT);
        authenticate(username , authenticationRequest.getPassword());
        final BillingUser billingUser = jwtUserDetailsService.loadUserByUsername(username);

        final String token = jwtTokenUtil.generateToken(billingUser.getUsername() , billingUser.getUserType());

        return ResponseEntity.ok(new UserLoginResponse(token));
    }

    private void authenticate(String username , String password) throws Exception {
        Objects.requireNonNull(username);
        Objects.requireNonNull(password);

        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username , password));
        } catch (DisabledException e) {
            throw new GeneralException(ResponseCodeAndMessage.AUTHENTICATION_ERROR.responseCode , MessageConstant.USER_IS_DISABLED);
            //} catch (InternalAuthenticationServiceException e) {
            //throw new GeneralException(ResponseCodeAndMessage.AUTHENTICATION_ERROR.responseCode,
            // MessageConstant.INVALID_CREDENTIALS);
        } catch (Exception e) {
            throw new GeneralException(ResponseCodeAndMessage.AUTHENTICATION_ERROR.responseCode , e.getMessage());
        }

    }

}
