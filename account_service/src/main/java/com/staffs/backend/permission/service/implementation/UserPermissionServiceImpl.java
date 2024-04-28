package com.staffs.backend.permission.service.implementation;

import com.staffs.backend.entity.permission.UserPermission;
import com.staffs.backend.enums.permission.PermissionName;
import com.staffs.backend.enums.user.UserType;
import com.staffs.backend.permission.dto.UserPermissionDTO;
import com.staffs.backend.permission.service.UserPermissionService;
import com.staffs.backend.repository.permission.UserPermissionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserPermissionServiceImpl implements UserPermissionService {

    private final UserPermissionRepository userPermissionRepository;

    public UserPermissionServiceImpl(UserPermissionRepository userPermissionRepository) {
        this.userPermissionRepository = userPermissionRepository;
    }

    @Override
    public List<UserPermissionDTO> getAllUserPermissions(UserType userType) {
        log.info("Getting all user permissions for {}" , userType.name());

        return userPermissionRepository.findByUserTypeListContaining(userType)
                .stream().map(UserPermissionDTO::getPermissionDTO).toList();
    }

    //Save all permissions to DB if not exist

    @Override
    public void saveAllPermissions() {
        log.info("Updating all permissions");

        //TODO implement lock mechanism here

        //retrieve all permissions on DB
        List<UserPermission> savedPermissionList = userPermissionRepository.findAll();
        if (savedPermissionList.isEmpty()) {
            log.info("Permissions does not exist, creating new permissions...");

            var toSavePermissions = Arrays.stream(PermissionName.values())
                    .map(UserPermissionServiceImpl::getPermissionEntity).toList();

            userPermissionRepository.saveAll(toSavePermissions);
            log.info("Permission creation complete.");

        } else {
            log.info("Permissions already exist, checking for updates...");

            var permissionMap = savedPermissionList.stream()
                    .collect(Collectors.toMap(UserPermission::getName , Function.identity()));

            var toUpdatePermissionList = Arrays.stream(PermissionName.values()).map(permissionName -> {

                if (!permissionMap.containsKey(permissionName.name())) {
                    return getPermissionEntity(permissionName);
                } else {
                    var permission = permissionMap.get(permissionName.name());
                    if (!permission.getDescription().equals(permissionName.getDescription())
                            || permission.getUserTypeList().size() != permissionName.getUserTypeList().size()) {

                        permission.setDescription(permissionName.getDescription());
                        permission.setUserTypeList(permissionName.getUserTypeList());
                        return permission;
                    }
                    return null;
                }
            }).filter(Objects::nonNull).toList();

            if (!toUpdatePermissionList.isEmpty()) {
                log.info("Updating {} permissions" , toUpdatePermissionList.size());
                userPermissionRepository.saveAll(toUpdatePermissionList);
            } else {
                log.info("No new permissions to update");
            }
        }
    }

    private static UserPermission getPermissionEntity(PermissionName permissionName) {
        var permission = new UserPermission();
        permission.setName(permissionName.name());
        permission.setDescription(permissionName.getDescription());
        permission.setUserTypeList(permissionName.getUserTypeList());
        return permission;
    }

}