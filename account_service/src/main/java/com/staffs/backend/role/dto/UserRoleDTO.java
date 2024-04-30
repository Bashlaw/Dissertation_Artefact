package com.staffs.backend.role.dto;

import com.staffs.backend.entity.role.UserRole;
import com.staffs.backend.permission.dto.UserPermissionDTO;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.util.List;
import java.util.Objects;

@Data
public class UserRoleDTO {

    private Long id;

    private String name;

    private String description;

    private List<UserPermissionDTO> userPermissionList;

    public static UserRoleDTO getUserRoleDTO(UserRole userRole) {
        UserRoleDTO userRoleDTO = new UserRoleDTO();
        BeanUtils.copyProperties(userRole , userRoleDTO);

        //add permission DTO
        if (Objects.nonNull(userRole.getPermissionList()) && !userRole.getPermissionList().isEmpty()) {
            var permissionDTOs = userRole.getPermissionList().stream().map(UserPermissionDTO::getPermissionDTO).toList();
            userRoleDTO.setUserPermissionList(permissionDTOs);
        }

        return userRoleDTO;
    }

}
