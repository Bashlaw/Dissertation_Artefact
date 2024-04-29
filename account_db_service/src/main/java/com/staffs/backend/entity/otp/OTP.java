package com.staffs.backend.entity.otp;

import com.staffs.backend.enums.user.UserType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity(name = "otp")
@Table(uniqueConstraints = {
        @UniqueConstraint(name = "uc_otp_recipient_user_type", columnNames = {"recipient", "user_type"})
})
public class OTP {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String encryptedCode;

    private String recipient;

    private String recipientName;

    private boolean used = false;

    private Date createdDate = new Date();

    private Date expiryDate = new Date();

    private UserType userType;

}
