package com.staffs.backend.permission.service;

import com.staffs.backend.enums.user.UserType;
import com.staffs.backend.permission.dto.UserPermissionDTO;

import java.util.List;

public interface UserPermissionService {

    List<UserPermissionDTO> getAllUserPermissions(UserType userType);

    void saveAllPermissions();

}
