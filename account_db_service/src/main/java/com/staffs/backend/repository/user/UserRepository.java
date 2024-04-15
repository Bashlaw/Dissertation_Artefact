package com.staffs.backend.repository.user;

import com.staffs.backend.entity.user.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<Users, Long> {

    boolean existsByEmail(String email);

    boolean existsByEmailAndDisabled(String email, boolean disabled);

    Optional<Users> findByEmail(String email);

    @Query(value = "select b.firstName from Users b where b.email = ?1")
    String getFirstNameForEmail(String email);

}
