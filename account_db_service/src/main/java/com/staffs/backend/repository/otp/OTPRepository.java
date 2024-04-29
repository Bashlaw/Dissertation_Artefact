package com.staffs.backend.repository.otp;

import com.staffs.backend.entity.otp.OTP;
import com.staffs.backend.enums.user.UserType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OTPRepository extends JpaRepository<OTP, Long> {

    OTP findByRecipientAndUserType(String recipient , UserType userType);

    OTP findByRecipientAndUserTypeAndUsed(String recipient , UserType userType , boolean used);

}
