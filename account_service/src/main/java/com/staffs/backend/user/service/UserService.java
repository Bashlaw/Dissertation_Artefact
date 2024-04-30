package com.staffs.backend.user.service;

import com.staffs.backend.entity.role.UserRole;
import com.staffs.backend.entity.user.Users;
import com.staffs.backend.general.dto.ChangePasswordRequestDTO;
import com.staffs.backend.general.dto.PageableRequestDTO;
import com.staffs.backend.user.dto.CreateUpdateUserDTO;
import com.staffs.backend.user.dto.UserDTO;
import com.staffs.backend.user.dto.UserListDTO;

public interface UserService {

    void createUser(CreateUpdateUserDTO dto , String performedBy);

    UserDTO updateUser(CreateUpdateUserDTO dto , String performedBy);

    UserDTO getUserDTO(String email);

    UserListDTO getUserDTOs(PageableRequestDTO dto);

    void requestResetPassword(String identifier);

    void changePassword(String email , ChangePasswordRequestDTO dto);

    void disableUser(String email);

    void enableUser(String email);

    void createSuperSystemUser(UserRole userRole);

    void validateForgetPasswordToken(String token , String identifier);

    void resetPassword(String password , String confirmPassword , String identifier);

    void deleteUser(String email);

    Users getUser(String email);

    Users getUserById(Long id);

    UserListDTO getAllCustomer(PageableRequestDTO dto);

    UserListDTO getAllAdmin(PageableRequestDTO dto);
}
