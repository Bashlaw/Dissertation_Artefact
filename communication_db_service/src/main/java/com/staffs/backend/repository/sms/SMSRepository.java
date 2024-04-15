package com.staffs.backend.repository.sms;

import com.staffs.backend.entity.sms.Sms;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SMSRepository extends JpaRepository<Sms, Long> {
}
