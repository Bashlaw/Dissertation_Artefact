package com.staffs.backend.repository.permission;

import com.staffs.backend.entity.permission.UserPermission;
import com.staffs.backend.enums.user.UserType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserPermissionRepository extends JpaRepository<UserPermission, Long> {

    Optional<UserPermission> findByName(String name);

    List<UserPermission> findByUserTypeListContaining(UserType userType);

}
