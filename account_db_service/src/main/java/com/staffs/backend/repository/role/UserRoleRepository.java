package com.staffs.backend.repository.role;

import com.staffs.backend.entity.role.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRoleRepository extends JpaRepository<UserRole, Long> {

    boolean existsByAlias(String alias);

    Optional<UserRole> findByIdAndDisabled(Long id , boolean disabled);

}
