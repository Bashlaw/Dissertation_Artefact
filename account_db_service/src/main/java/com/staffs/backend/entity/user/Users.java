package com.staffs.backend.entity.user;

import com.staffs.backend.entity.role.UserRole;
import com.staffs.backend.enums.user.UserType;
import com.staffs.backend.utils.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
@Table(name = "Users", indexes = {
        @Index(name = "idx_users_email", columnList = "email") ,
        @Index(name = "idx_users_phonenumber", columnList = "phoneNumber") ,
        @Index(name = "idx_users_usertype", columnList = "userType")
})
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

    private boolean resetPassword = false;

    @Enumerated(EnumType.STRING)
    private UserType userType;

    private String accountId;

    private boolean deleted = false;

    private String dob;

    private String gender;

}
