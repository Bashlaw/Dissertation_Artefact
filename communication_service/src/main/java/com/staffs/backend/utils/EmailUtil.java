package com.staffs.backend.utils;

import com.staffs.backend.general.config.ConfigProperty;
import com.sun.mail.util.MailSSLSocketFactory;
import lombok.extern.slf4j.Slf4j;

import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

@Slf4j
public class EmailUtil {

    public static void sendMail(ConfigProperty configProperty , String recipient , String content , String title) {

        Runnable emailSendTask = () -> {
            log.info("Initiating email sending task. Sending to {}" , recipient);
            Properties props = new Properties();
            try {
                MailSSLSocketFactory sf = new MailSSLSocketFactory();
                sf.setTrustAllHosts(true);
                props.put("mail.imap.ssl.trust" , "*");
                props.put("mail.imap.ssl.socketFactory" , sf);
                props.put("mail.smtp.auth" , "true");
                props.put("mail.smtp.starttls.enable" , configProperty.isMailServerSsl() ? "true" : "false");
                props.put("mail.smtp.host" , configProperty.getMailServerHost());
                props.put("mail.smtp.port" , configProperty.getMailServerPort());

                Session session = Session.getInstance(props , new javax.mail.Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(configProperty.getMailServerUsername() , configProperty.getMailServerPassword());
                    }
                });

                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress(configProperty.getMailServerUsername()));
                message.setRecipients(Message.RecipientType.TO , InternetAddress.parse(recipient));
                message.setSubject(title);
                message.setContent(content , "text/html");

                Transport.send(message);
                log.info("Everything seems fine");
            } catch (Throwable exp) {
                log.info("Error occurred during sending email" , exp);
            }
        };
        Thread mailSender = new Thread(emailSendTask , "EMAIL-SENDER");
        mailSender.start();
    }

}
