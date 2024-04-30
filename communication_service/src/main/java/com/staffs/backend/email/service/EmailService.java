package com.staffs.backend.email.service;

public interface EmailService {

    void sendMail(String mailTo , String mailSubject , String mailBody);

}
