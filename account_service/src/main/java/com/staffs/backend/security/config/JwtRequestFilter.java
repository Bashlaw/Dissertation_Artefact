package com.staffs.backend.security.config;

import com.staffs.backend.general.dto.MessageConstant;
import com.staffs.backend.general.dto.UserConstant;
import com.staffs.backend.general.enums.ResponseCodeAndMessage;
import com.staffs.backend.security.dto.BillingUser;
import com.staffs.backend.security.service.JwtUserDetailsService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtRequestFilter extends OncePerRequestFilter {

    private final JwtTokenUtil jwtTokenUtil;
    private final JwtUserDetailsService jwtUserDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request , HttpServletResponse response , FilterChain chain)
            throws ServletException, IOException {

        final String requestTokenHeader = request.getHeader("Authorization");

        String username = null;
        String jwtToken = null;
        // JWT Token is in the form "Bearer token". Remove Bearer word and get only the Token
        if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
            jwtToken = requestTokenHeader.substring(7);
            //check token is not blacklisted
            if (jwtUserDetailsService.isTokenBlacklisted(jwtToken)) {
                request.setAttribute("errorMessage" , MessageConstant.USER_ALREADY_LOGGED_OUT);
                request.setAttribute("errorCode" , ResponseCodeAndMessage.CLIENT_NOT_ALLOWED.responseCode);
                response.sendError(ResponseCodeAndMessage.CLIENT_NOT_ALLOWED.responseCode , MessageConstant.USER_ALREADY_LOGGED_OUT);
                chain.doFilter(request , response);
            }
            try {
                username = jwtTokenUtil.getUsernameFromToken(jwtToken);
            } catch (IllegalArgumentException e) {
                logger.warn("Unable to get JWT Token");
                logger.warn("Unable to get JWT Token");
                request.setAttribute("errorMessage" , "Unable to get JWT Token");
                request.setAttribute("errorCode" , HttpServletResponse.SC_UNAUTHORIZED);
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED , "Unable to get JWT Token");
                return;
            } catch (ExpiredJwtException e) {
                logger.warn("JWT Token has expired");
                request.setAttribute("errorMessage" , "JWT Token has expired");
                request.setAttribute("errorCode" , HttpServletResponse.SC_UNAUTHORIZED);
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED , "JWT Token has expired");
                return;
            } catch (SignatureException e) {
                logger.warn("Authentication Failed. Username or Password not valid");
                request.setAttribute("errorMessage" , "JWT signature does not match locally computed signature. JWT validity cannot be asserted and should not be trusted.");
                request.setAttribute("errorCode" , HttpServletResponse.SC_UNAUTHORIZED);
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED , "JWT signature does not match locally computed signature. JWT validity cannot be asserted and should not be trusted.");
                return;
            }
        } else {
            logger.warn("JWT Token does not begin with Bearer String.");
        }

        //Once we get the token, validate it.
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            BillingUser userDetails = this.jwtUserDetailsService.loadUserByUsername(username);

            // if token is validly configuring Spring Security to manually set authentication
            if (Boolean.TRUE.equals(jwtTokenUtil.validateToken(jwtToken , userDetails)) && !jwtUserDetailsService.isTokenBlacklisted(jwtToken)) {

                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetails , null , userDetails.getAuthorities());
                usernamePasswordAuthenticationToken
                        .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                // After setting the Authentication in the context, we specify
                // that the current user is authenticated. So it passes the Spring Security Configurations successfully.
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);

                request.setAttribute(UserConstant.USER_TYPE , userDetails.getUserType());
            }
        }
        chain.doFilter(request , response);
    }

}
