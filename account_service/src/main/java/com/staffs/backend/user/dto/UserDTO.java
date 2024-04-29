package com.staffs.backend.user.dto;

import com.staffs.backend.entity.user.Users;
import com.staffs.backend.enums.user.UserType;
import com.staffs.backend.role.dto.UserRoleDTO;
import lombok.Data;
import org.springframework.beans.BeanUtils;

@Data
public class UserDTO {

    private Long id;

    private String firstName;

    private String lastName;

    private String middleName;

    private String phoneNumber;

    private String email;

    private UserRoleDTO role;

    private UserType userType;


    public static UserDTO getUserDTO(Users users) {
        UserDTO userDTO = new UserDTO();
        BeanUtils.copyProperties(users, userDTO);

        //set role DTO
        userDTO.setRole(UserRoleDTO.getUserRoleDTO(users.getUserRole()));

        return userDTO;

    }

}
