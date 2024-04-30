package com.staffs.backend.repository.user;

import com.staffs.backend.entity.user.Users;
import com.staffs.backend.enums.user.UserType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UsersRepository extends JpaRepository<Users, Long> {

    boolean existsByEmail(String email);

    boolean existsByEmailAndPhoneNumber(String email , String phoneNumber);

    boolean existsByPhoneNumberAndEmailNot(String email , String phoneNumber);

    Optional<Users> findByEmail(String email);

    @Query(value = "select b.firstName from Users b where b.email = ?1")
    String getFirstNameForEmail(String email);

    @Query(value = "select b.email from Users b where b.phoneNumber = ?1")
    String getEmailForPhoneNumber(String phoneNumber);

    Optional<Users> findByPhoneNumber(String phoneNumber);

    Page<Users> findAllByUserType(UserType userType, Pageable pageable);

}
