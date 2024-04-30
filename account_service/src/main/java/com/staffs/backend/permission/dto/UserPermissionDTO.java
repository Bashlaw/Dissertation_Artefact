package com.staffs.backend.permission.dto;

import com.staffs.backend.entity.permission.UserPermission;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserPermissionDTO {

    private String name;

    private String description;

    public static UserPermissionDTO getPermissionDTO(UserPermission userPermission) {
        return UserPermissionDTO.builder()
                .name(userPermission.getName())
                .description(userPermission.getDescription())
                .build();
    }

}
