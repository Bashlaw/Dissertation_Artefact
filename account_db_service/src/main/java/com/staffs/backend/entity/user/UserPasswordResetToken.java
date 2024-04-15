package com.staffs.backend.entity.user;

import com.staffs.backend.utils.BaseEntity;
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
@Entity
public class UserPasswordResetToken extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String token;

    @OneToOne
    private Users users;

    private Date expiryDate;

}
