package com.staffs.backend.general.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class ChangePasswordRequestDTO {

    @NotEmpty(message = "Please provide a valid old password")
    private String oldPassword;

    @NotEmpty(message = "Please provide a valid password")
    private String password;

    private String confirmPassword;

}
