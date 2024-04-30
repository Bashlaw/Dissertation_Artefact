package com.staffs.backend.startup;

import com.staffs.backend.permission.service.UserPermissionService;
import com.staffs.backend.role.service.UserRoleService;
import com.staffs.backend.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class StartupEvent implements ApplicationListener<ContextRefreshedEvent> {

    boolean alreadySetup = false;

    private final UserService userService;
    private final UserRoleService userRoleService;
    private final UserPermissionService userPermissionService;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {

        if (alreadySetup) {
            return;
        }

        log.info("Application starting up, loading requirements ...");

        userPermissionService.saveAllPermissions();
        var role = userRoleService.createSystemSuperAdminRole();
        userService.createSuperSystemUser(role);

        log.info("Application startup completed :)");

        alreadySetup = true;
    }

}