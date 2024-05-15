package com.staffs.backend.security.config;

import com.staffs.backend.general.dto.ResponseConstants;
import jakarta.servlet.http.HttpServletResponse;
import kong.unirest.json.JSONObject;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {

    private final JwtRequestFilter jwtRequestFilter;

    private final UserDetailsService userDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManagerBean(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.cors(Customizer.withDefaults()).csrf(AbstractHttpConfigurer::disable);

        http.authorizeHttpRequests(
                requestMatcherRegistry -> requestMatcherRegistry
                        .requestMatchers(
                                "/v3/api-docs/**" , "/configuration/ui" , "/swagger-resources/**" ,
                                "/configuration/security" , "/swagger-ui/**" , "/swagger-ui.html" , "/webjars/**" ,
                                "/api/v1/authenticate/**" , "/api/v1/user/resetPassword/**" , "/api/v1/enums/**" ,
                                "/api/v1/user/forgetPassword/**" , "/api/v1/user/validateForgetPasswordToken/**" ,
                                "/api/v1/otp/generate/**" , "/api/v1/user/customer").permitAll()
                        .anyRequest()
                        .authenticated());

        // Add a filter to validate the tokens with every request
        http.sessionManagement(auth -> auth.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtRequestFilter , UsernamePasswordAuthenticationFilter.class);

        //Exception handling configuration for failed login
        http.exceptionHandling(httpSecurityExceptionHandlingConfigurer -> httpSecurityExceptionHandlingConfigurer
                .authenticationEntryPoint((request , response , e) -> {
                    response.setContentType("application/json;charset=UTF-8");
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    Object errorMessage = request.getAttribute("errorMessage");
                    Object errorCode = request.getAttribute("errorCode");
                    response.getWriter().write(new JSONObject()
                            .put("status" , ResponseConstants.ResponseMessage.FAILED)
                            .put("responseCode" , errorCode == null ? ResponseConstants.ResponseCode.FAILED : errorCode)
                            .put("failureReason" , errorMessage == null ? e.getMessage() : errorMessage)
                            .toString());

                }));

        //exception handling for access denied
        http.exceptionHandling(httpSecurityExceptionHandlingConfigurer -> httpSecurityExceptionHandlingConfigurer
                .accessDeniedHandler((request , response , e) -> {
                    response.setContentType("application/json;charset=UTF-8");
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    Object errorMessage = request.getAttribute("errorMessage");
                    Object errorCode = request.getAttribute("errorCode");
                    response.getWriter().write(new JSONObject()
                            .put("status" , ResponseConstants.ResponseMessage.FAILED)
                            .put("responseCode" , errorCode == null ? ResponseConstants.ResponseCode.FAILED : errorCode)
                            .put("failureReason" , errorMessage == null ? e.getMessage() : errorMessage)
                            .toString());
                }));


        return http.build();
    }

}
