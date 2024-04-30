package com.staffs.backend.role.controller;

import com.staffs.backend.general.dto.MessageConstant;
import com.staffs.backend.general.dto.Response;
import com.staffs.backend.general.service.GeneralService;
import com.staffs.backend.role.dto.CreateUpdateUserRoleDTO;
import com.staffs.backend.role.service.UserRoleService;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/role")
public class UserRoleController {

    private final HttpServletRequest request;
    private final UserRoleService roleService;
    private final GeneralService generalService;

    @GetMapping()
    public Response getLoggedInUserRole(@Parameter(hidden = true) Principal principal) {
        return generalService.prepareSuccessResponse(roleService.getLoggedInUserRole(principal.getName()));
    }

    @PreAuthorize("hasAuthority('CREATE_ROLE')")
    @PostMapping("/create")
    public Response add(@Parameter(hidden = true) Principal principal , @Valid @RequestBody CreateUpdateUserRoleDTO dto) {

        var requestExtraInfo = generalService.getRequestExtraInfo(request);

        return generalService.prepareSuccessResponse(roleService.create(dto , requestExtraInfo , principal.getName()));
    }

    @PreAuthorize("hasAuthority('UPDATE_ROLE')")
    @PutMapping("/update/{roleId}")
    public Response update(@Parameter(hidden = true) Principal principal , @Valid @RequestBody CreateUpdateUserRoleDTO dto , @PathVariable Long roleId) {

        var requestExtraInfo = generalService.getRequestExtraInfo(request);

        return generalService.prepareSuccessResponse(roleService.update(dto , requestExtraInfo , roleId , principal.getName()));
    }

    @PreAuthorize("hasAuthority('VIEW_ROLE')")
    @GetMapping("/{roleId}")
    public Response getSingle(@PathVariable Long roleId) {
        return generalService.prepareSuccessResponse(roleService.getRoleDTO(roleId));
    }

    @PreAuthorize("hasAuthority('VIEW_ROLE')")
    @GetMapping("/all")
    public Response getAll() {
        return generalService.prepareSuccessResponse(roleService.getAllRoles());
    }

    @PreAuthorize("hasAuthority('DISABLE_ENABLE_ROLE')")
    @PostMapping("/disable/{roleId}")
    public Response disableRole(@PathVariable Long roleId) {

        roleService.disableRole(roleId);

        return generalService.prepareSuccessResponse(MessageConstant.ROLE_IS_DISABLED);
    }

    @PreAuthorize("hasAuthority('DISABLE_ENABLE_ROLE')")
    @PostMapping("/enable/{roleId}")
    public Response enableRole(@PathVariable Long roleId) {

        roleService.enableRole(roleId);

        return generalService.prepareSuccessResponse(MessageConstant.ROLE_IS_ENABLED);
    }

}
