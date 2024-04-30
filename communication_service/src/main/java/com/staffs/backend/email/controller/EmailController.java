package com.staffs.backend.email.controller;

import com.staffs.backend.email.dto.EmailRequestDTO;
import com.staffs.backend.email.service.EmailService;
import com.staffs.backend.general.dto.Response;
import com.staffs.backend.general.service.GeneralService;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/email")
public class EmailController {

    private final EmailService emailService;
    private final GeneralService generalService;

    @PostMapping
    public Response sendEmail(@RequestBody EmailRequestDTO requestDTO) {
        emailService.sendMail(
                requestDTO.getTo() ,
                requestDTO.getSubject() ,
                requestDTO.getBody());

        return generalService.prepareSuccessResponse("Email sent successfully");
    }

}
