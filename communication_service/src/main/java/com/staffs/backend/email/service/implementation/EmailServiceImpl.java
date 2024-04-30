package com.staffs.backend.email.service.implementation;

import com.staffs.backend.email.service.EmailService;
import com.staffs.backend.entity.email.Mail;
import com.staffs.backend.general.config.ConfigProperty;
import com.staffs.backend.repository.email.MailRepository;
import com.staffs.backend.utils.EmailUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final ConfigProperty configProperty;
    private final MailRepository mailRepository;

    @Override
    public void sendMail(String mailTo , String mailSubject , String mailBody) {

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.submit(() -> {

            Mail email = getMail(mailSubject , mailTo , mailBody);

            try {
                EmailUtil.sendMail(configProperty , mailTo , mailBody , mailSubject);

                updateMailResponse("Mail successfully sent" , email);
            } catch (MailException e) {
                log.error("Error sending mail, reason => {}" , e.getMessage());
                updateMailResponse(e.getMessage() , email);
            }

        });

    }

    private Mail getMail(String mailSubject , String mailTo , String mailBody) {
        Mail email = new Mail();

        email.setMailHeader(mailSubject);
        email.setMailTo(mailTo);
        email.setMailContent(mailBody);

        log.info("Sending mail to -> {}" , email.getMailTo());

        return email;
    }

    private void updateMailResponse(String response , Mail email) {
        log.info("Mail Response {}" , response);

        if (Objects.equals(response , "Mail successfully sent")) {
            log.info("Mail successfully sent");
            email.setSent(true);
            email.setLastSent(new Date());
        } else {
            log.info("Mail failed to sent");
            email.setSent(false);
            email.setFailureReason(response);
        }

        mailRepository.save(email);
    }

}
