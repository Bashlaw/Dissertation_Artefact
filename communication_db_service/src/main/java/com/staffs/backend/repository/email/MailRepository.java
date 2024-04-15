package com.staffs.backend.repository.email;

import com.staffs.backend.entity.email.Mail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MailRepository extends JpaRepository<Mail, Long> {
}
