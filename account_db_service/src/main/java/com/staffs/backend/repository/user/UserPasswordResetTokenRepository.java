package com.staffs.backend.repository.user;

import com.staffs.backend.entity.user.UserPasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserPasswordResetTokenRepository extends JpaRepository<UserPasswordResetToken, Long> {

    UserPasswordResetToken findByToken(String token);

}
