package com.staffs.backend.permission.controller;

import com.staffs.backend.enums.user.UserType;
import com.staffs.backend.general.dto.Response;
import com.staffs.backend.general.service.GeneralService;
import com.staffs.backend.permission.service.UserPermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/permissions")
public class UserPermissionController {

    private final GeneralService generalService;
    private final UserPermissionService userPermissionService;

    @PreAuthorize("hasAuthority('VIEW_ADMIN_PERMISSION')")
    @GetMapping("/admin")
    public Response getAllSystemAdminPermissions() {

        return generalService.prepareSuccessResponse(userPermissionService.getAllUserPermissions(UserType.ADMIN));

    }

    @PreAuthorize("hasAuthority('VIEW_CUSTOMER_PERMISSION')")
    @GetMapping("/customer")
    public Response getAllCustomerPermissions() {

        return generalService.prepareSuccessResponse(userPermissionService.getAllUserPermissions(UserType.CUSTOMER));

    }

}
