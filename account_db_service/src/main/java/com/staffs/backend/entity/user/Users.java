package com.staffs.backend.entity.user;

import com.staffs.backend.entity.role.UserRole;
import com.staffs.backend.enums.user.UserType;
import com.staffs.backend.utils.BaseEntity;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import jakarta.persistence.*;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
public class Users extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;

    private String lastName;

    @Column(unique = true)
    private String email;

    @Column(unique = true)
    private String phoneNumber;

    private String password;

    @ManyToOne(optional = false)
    private UserRole userRole;

    private boolean disabled = false;

    private boolean resetPassword;

    @Enumerated(EnumType.STRING)
    private UserType userType;

    private String accountId;

}
