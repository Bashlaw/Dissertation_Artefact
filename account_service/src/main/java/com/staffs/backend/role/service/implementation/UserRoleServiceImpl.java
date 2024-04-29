package com.staffs.backend.role.service.implementation;

import com.staffs.backend.entity.permission.UserPermission;
import com.staffs.backend.entity.role.UserRole;
import com.staffs.backend.entity.user.Users;
import com.staffs.backend.enums.permission.PermissionName;
import com.staffs.backend.enums.user.UserType;
import com.staffs.backend.exceptions.GeneralException;
import com.staffs.backend.general.dto.MessageConstant;
import com.staffs.backend.general.dto.RequestExtraInfo;
import com.staffs.backend.general.enums.ResponseCodeAndMessage;
import com.staffs.backend.repository.permission.UserPermissionRepository;
import com.staffs.backend.repository.role.UserRoleRepository;
import com.staffs.backend.repository.user.UsersRepository;
import com.staffs.backend.role.dto.CreateUpdateUserRoleDTO;
import com.staffs.backend.role.dto.UserRoleDTO;
import com.staffs.backend.role.service.UserRoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.staffs.backend.startup.StartupConstant.DEFAULT_SYSTEM_ADMIN_ROLE;
import static com.staffs.backend.startup.StartupConstant.DEFAULT_SYSTEM_ADMIN_ROLE_DESCRIPTION;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserRoleServiceImpl implements UserRoleService {

    private final UsersRepository usersRepository;
    private final UserRoleRepository userRoleRepository;
    private final UserPermissionRepository userPermissionRepository;

    @Override
    public UserRoleDTO create(CreateUpdateUserRoleDTO dto , RequestExtraInfo extraInfo , String performedBy) {
        log.info("creating new user role {}" , dto);

        UserType userType = getUserType(extraInfo.userType());

        String alias = getRoleAlias(dto.getName() , userType);

        validateRoleAlias(alias);

        UserRole userRole = new UserRole();

        userRole.setAlias(alias);

        var permissionList = getPermissionList(dto);

        userRole.setPermissionList(permissionList);

        userRole = userRoleRepository.save(userRole);

        return UserRoleDTO.getUserRoleDTO(userRole);

    }

    @Override
    public UserRoleDTO update(CreateUpdateUserRoleDTO dto , RequestExtraInfo extraInfo , Long roleId , String performedBy) {
        log.info("Updating user role..");

        UserRole userRole = getRoleById(roleId);

        UserType userType = getUserType(extraInfo.userType());

        //check if the name has changed
        if (!userRole.getName().equals(dto.getName())) {
            String alias = getRoleAlias(dto.getName() , userType);
            validateRoleAlias(alias);
            userRole.setName(dto.getName());
            userRole.setAlias(alias);
        }

        //check if the description has changed
        if (userRole.getDescription() != null && !userRole.getDescription().equals(dto.getDescription())) {
            userRole.setDescription(dto.getDescription());
        } else if (userRole.getDescription() == null && dto.getDescription() != null) {
            userRole.setDescription(dto.getDescription());
        }

        //set permission
        var permissionList = getPermissionList(dto);

        List<UserPermission> permissions = new ArrayList<>();

        for (UserPermission permissionName : permissionList) {
            userPermissionRepository.findByName(permissionName.getName()).ifPresent(permissions::add);
        }

        userRole.setPermissionList(permissions);

        userRole = userRoleRepository.save(userRole);

        return UserRoleDTO.getUserRoleDTO(userRole);
    }


    @Override
    public UserRoleDTO getRoleDTO(Long roleId) {
        log.info("Getting one role by id {}" , roleId);

        return UserRoleDTO.getUserRoleDTO(getRoleById(roleId));
    }

    @Override
    public List<UserRoleDTO> getAllRoles() {
        return userRoleRepository.findAll().stream().map(UserRoleDTO::getUserRoleDTO).toList();
    }

    @Override
    public UserRole getRoleById(Long roleId) {
        log.info("Getting role by id {}" , roleId);
        return userRoleRepository.findByIdAndDisabled(roleId , false).orElseThrow(() -> new GeneralException(ResponseCodeAndMessage.RECORD_NOT_FOUND.responseCode , MessageConstant.ROLE_DOES_NOT_EXIST));
    }

    @Override
    public void disableRole(Long roleId) {
        log.info("Disabling role with id {}" , roleId);

        UserRole userRole = getRoleById(roleId);

        if (userRole.isDisabled()) {
            throw new GeneralException(ResponseCodeAndMessage.ALREADY_DISABLED.responseCode , MessageConstant.ROLE_ALREADY_DISABLED);
        }

        userRole.setDisabled(true);
        userRole.setUpdatedAt(LocalDateTime.now());
        userRoleRepository.save(userRole);

    }

    @Override
    public void enableRole(Long roleId) {
        log.info("Enabling role with id {}" , roleId);

        UserRole userRole = getRoleById(roleId);

        if (!userRole.isDisabled()) {
            throw new GeneralException(ResponseCodeAndMessage.ALREADY_DISABLED.responseCode , MessageConstant.ROLE_ALREADY_DISABLED);
        }

        userRole.setDisabled(false);
        userRole.setUpdatedAt(LocalDateTime.now());
        userRoleRepository.save(userRole);

    }

    @Override
    public UserRole createSystemSuperAdminRole() {
        log.info("creating system super admin role...");

        UserRole userRole;
        if (!userRoleRepository.existsByAlias(DEFAULT_SYSTEM_ADMIN_ROLE)) {
            log.info("creating super system role if not exist");

            userRole = new UserRole();
            userRole.setName(DEFAULT_SYSTEM_ADMIN_ROLE);
            userRole.setAlias(DEFAULT_SYSTEM_ADMIN_ROLE);
            userRole.setDescription(DEFAULT_SYSTEM_ADMIN_ROLE_DESCRIPTION);
            List<UserPermission> allSystemAdminPermissions = userPermissionRepository.findByUserTypeListContaining(UserType.ADMIN);
            userRole.setPermissionList(allSystemAdminPermissions);

            log.info("System created Super Admin Role successfully");

        } else {
            //get role
            Optional<UserRole> roleOptional = userRoleRepository.findByName(DEFAULT_SYSTEM_ADMIN_ROLE);
            if (roleOptional.isEmpty()) {
                throw new GeneralException(ResponseCodeAndMessage.RECORD_NOT_FOUND.responseCode , MessageConstant.NO_VALID_ROLE);
            }

            userRole = roleOptional.get();
            List<UserPermission> permissionList = userPermissionRepository.findAll();
            userRole.setPermissionList(permissionList);

            log.info("Updated Super Admin Role successfully");

        }

        userRole = userRoleRepository.save(userRole);
        return userRole;

    }

    @Override
    public UserRoleDTO getLoggedInUserRole(String email) {

        //get logged in user
        Users users = usersRepository.findByEmail(email).orElseThrow(() -> new GeneralException(ResponseCodeAndMessage.RECORD_NOT_FOUND.responseCode , "User (" + email + ") does not exist"));

        //check first time login using change password
        if (!users.isResetPassword()) {
            throw new GeneralException(ResponseCodeAndMessage.CHANGE_USER_PASSWORD.responseCode , MessageConstant.NEED_TO_CHANGE_PASSWORD_TO_THEIR_DESIRED_PASSWORD_TO_CONTINUE);
        }

        //get role
        UserRole usersRole = users.getUserRole();
        if (usersRole == null) {
            return null;
        }

        //convert to dto
        return UserRoleDTO.getUserRoleDTO(usersRole);
    }


    private List<UserPermission> getPermissionList(CreateUpdateUserRoleDTO dto) {
        List<PermissionName> permissionNameList = new ArrayList<>(dto.getPermissionNames());
        return permissionNameList.stream().map(permissionName -> userPermissionRepository.findByName(permissionName.name()).orElseThrow(() -> new GeneralException(ResponseCodeAndMessage.RECORD_NOT_FOUND.responseCode , "Permission (" + permissionName.name() + ") does not exist"))).toList();
    }

    private String getRoleAlias(String name , UserType userType) {

        if (userType != null) {
            return name + "_" + userType;
        }

        return name;
    }

    private static UserType getUserType(String userType) {
        UserType type;
        try {
            type = UserType.valueOf(userType);
        } catch (Exception e) {
            throw new GeneralException(ResponseCodeAndMessage.BAD_REQUEST.responseCode , MessageConstant.INVALID_USER_TYPE_ON_REQUEST);
        }
        return type;
    }

    private void validateRoleAlias(String alias) {
        if (userRoleRepository.existsByAlias(alias)) {
            throw new GeneralException(ResponseCodeAndMessage.ALREADY_EXIST.responseCode , MessageConstant.ROLE_ALREADY_EXISTS);
        }
    }

}
