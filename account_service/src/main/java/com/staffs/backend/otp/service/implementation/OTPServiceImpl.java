package com.staffs.backend.otp.service.implementation;

import com.staffs.backend.entity.otp.OTP;
import com.staffs.backend.enums.user.UserType;
import com.staffs.backend.exceptions.GeneralException;
import com.staffs.backend.general.dto.MessageConstant;
import com.staffs.backend.general.enums.ResponseCodeAndMessage;
import com.staffs.backend.otp.service.OTPService;
import com.staffs.backend.repository.otp.OTPRepository;
import com.staffs.backend.repository.user.UsersRepository;
import com.staffs.backend.utils.GeneralUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class OTPServiceImpl implements OTPService {

    private final OTPRepository otpRepository;
    private final UsersRepository usersRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    public void generateOTP(String recipient , UserType userType , int length) {
        log.info("Generating OTP for recipient: {}" , recipient);

        String phoneNumber;
        String identifier = recipient;

        if (GeneralUtil.checkIdentifierIfPhoneNumber(recipient)) {
            phoneNumber = recipient;

            //get recipient based on user types
            recipient = usersRepository.getEmailForPhoneNumber(phoneNumber);
        }

        //get recipient name based on user types
        String recipientName = usersRepository.getFirstNameForEmail(recipient);


        if (Objects.isNull(recipientName)) {
            throw new GeneralException(ResponseCodeAndMessage.RECORD_NOT_FOUND.responseCode , MessageConstant.USER_NOT_FOUND);
        }

        String otpCode = generateOtpCode(length);
        log.info("general OTP Code: {}" , otpCode);

        OTP otp = getOtp(identifier , userType);
        if (Objects.isNull(otp)) {
            otp = new OTP();
            otp.setRecipient(identifier);
        }

        otp.setRecipientName(recipientName);
        otp.setEncryptedCode(passwordEncoder.encode(otpCode));
        setCreationAndExpiryDate(otp);
        otp.setUserType(userType);
        otp.setUsed(false);

        //save otp
        otpRepository.save(otp);

    }

    @Override
    public boolean verifyOTP(String recipient , UserType userType , String otpCode) {
        if (GeneralUtil.stringIsNullOrEmpty(otpCode) || otpCode.length() > 6 || otpCode.length() < 4) {
            throw new GeneralException(ResponseCodeAndMessage.BAD_REQUEST.responseCode , MessageConstant.INVALID_OTP);
        }

        OTP otp = getOtp(recipient , userType);

        if (Objects.isNull(otp)) {
            throw new GeneralException(ResponseCodeAndMessage.BAD_REQUEST.responseCode , MessageConstant.INVALID_OTP);
        }

        if (otp.isUsed()) {
            throw new GeneralException(ResponseCodeAndMessage.BAD_REQUEST.responseCode , MessageConstant.OTP_ALREADY_USED);
        }

        if (otp.getExpiryDate().before(new Date())) {
            throw new GeneralException(ResponseCodeAndMessage.BAD_REQUEST.responseCode , MessageConstant.OTP_EXPIRED);
        }

        return !passwordEncoder.matches(otpCode , otp.getEncryptedCode());
    }

    @Override
    public void validate(String recipient , UserType userType) {

        OTP otp = getOtp(recipient , userType);

        if (Objects.isNull(otp)) {
            throw new GeneralException(ResponseCodeAndMessage.BAD_REQUEST.responseCode , MessageConstant.INVALID_OTP);
        }

        if (!otp.isUsed()) {
            throw new GeneralException(ResponseCodeAndMessage.BAD_REQUEST.responseCode , MessageConstant.OTP_VERIFICATION_FAILED);
        }

        if (otp.getExpiryDate().before(new Date())) {
            throw new GeneralException(ResponseCodeAndMessage.BAD_REQUEST.responseCode , MessageConstant.OTP_EXPIRED);
        }

    }

    @Override
    public OTP getOtp(String identifier , UserType userType) {
        return otpRepository.findByRecipientAndUserType(identifier , userType);
    }

    @Override
    public void setUsed(OTP otp) {
        otp.setUsed(true);

        otpRepository.save(otp);
    }

    private void setCreationAndExpiryDate(OTP otp) {
        Date creationDate = new Date();
        LocalDateTime ldt = LocalDateTime.ofInstant(creationDate.toInstant() , ZoneId.systemDefault());
        ldt = ldt.plusMinutes(5);
        Date expiryDate = Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant());

        otp.setCreatedDate(creationDate);
        otp.setExpiryDate(expiryDate);
    }

    private String generateOtpCode(int length) {
        //default length to 6;
        if (length <= 0) {
            length = 6;
        }

        return GeneralUtil.generateCode(length);
    }

}
