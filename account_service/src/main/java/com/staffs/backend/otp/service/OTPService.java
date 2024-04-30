package com.staffs.backend.otp.service;

import com.staffs.backend.entity.otp.OTP;
import com.staffs.backend.enums.user.UserType;

public interface OTPService {

    void generateOTP(String recipient , UserType userType , int length);

    boolean verifyOTP(String recipient , UserType userType , String otpCode);

    void validate(String recipient , UserType userType);

    OTP getOtp(String identifier , UserType userType);

    void setUsed(OTP otp);

}
