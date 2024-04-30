package com.staffs.backend.role.service;

import com.staffs.backend.entity.role.UserRole;
import com.staffs.backend.general.dto.RequestExtraInfo;
import com.staffs.backend.role.dto.CreateUpdateUserRoleDTO;
import com.staffs.backend.role.dto.UserRoleDTO;

import java.util.List;

public interface UserRoleService {

    UserRoleDTO create(CreateUpdateUserRoleDTO dto , RequestExtraInfo extraInfo , String performedBy);

    UserRoleDTO update(CreateUpdateUserRoleDTO dto , RequestExtraInfo extraInfo , Long roleId , String performedBy);

    UserRoleDTO getRoleDTO(Long roleId);

    List<UserRoleDTO> getAllRoles();

    UserRole getRoleById(Long roleId);

    void disableRole(Long roleId);

    void enableRole(Long roleId);

    UserRole createSystemSuperAdminRole();

    UserRoleDTO getLoggedInUserRole(String email);

}
