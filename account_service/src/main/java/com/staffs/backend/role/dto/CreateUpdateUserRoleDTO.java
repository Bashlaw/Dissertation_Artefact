package com.staffs.backend.role.dto;

import com.staffs.backend.enums.permission.PermissionName;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.Set;

@Data
public class CreateUpdateUserRoleDTO {

    @NotEmpty(message = "Role name cannot be empty")
    private String name;

    private String description;

    @NotEmpty(message = "Permissions cannot be empty")
    private Set<PermissionName> permissionNames;

}
