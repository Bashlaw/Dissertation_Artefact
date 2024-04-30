package com.staffs.backend.user.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class CreateUpdateUserDTO {

    @NotEmpty(message = "first name cannot be empty")
    private String firstName;

    @NotEmpty(message = "last name cannot be empty")
    private String lastName;

    private String middleName;

    @NotEmpty(message = "email cannot be empty")
    private String email;

    @NotEmpty(message = "Phone number cannot be empty")
    private String phoneNumber;

    @NotEmpty(message = "Role name cannot be empty")
    private Long roleId;

}
