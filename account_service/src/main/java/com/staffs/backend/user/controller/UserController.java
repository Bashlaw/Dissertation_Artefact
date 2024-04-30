package com.staffs.backend.user.controller;

import com.staffs.backend.enums.user.UserType;
import com.staffs.backend.general.dto.ChangePasswordRequestDTO;
import com.staffs.backend.general.dto.MessageConstant;
import com.staffs.backend.general.dto.PageableRequestDTO;
import com.staffs.backend.general.dto.Response;
import com.staffs.backend.general.service.GeneralService;
import com.staffs.backend.user.dto.CreateUpdateUserDTO;
import com.staffs.backend.user.service.UserService;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
public class UserController {

    private final UserService userService;
    private final GeneralService generalService;


    @GetMapping()
    public Response getLoggedInAdmin(@Parameter(hidden = true) Principal principal) {
        return generalService.prepareSuccessResponse(userService.getUserDTO(principal.getName()));
    }

    @PreAuthorize("hasAuthority('CREATE_SYSTEM_ADMIN')")
    @PostMapping("/admin")
    public Response addSystemAdmin(@Parameter(hidden = true) Principal principal , @Valid @RequestBody CreateUpdateUserDTO dto) {

        userService.createUser(dto , principal.getName());

        return generalService.prepareSuccessResponse(MessageConstant.REGISTER_SUCCESSFUL);
    }

    @PreAuthorize("hasAuthority('CREATE_CUSTOMER')")
    @PostMapping("/customer")
    public Response createCustomer(@Valid @RequestBody CreateUpdateUserDTO dto) {

        userService.createUser(dto , null);

        return generalService.prepareSuccessResponse(MessageConstant.REGISTER_SUCCESSFUL);
    }

    @PreAuthorize("hasAuthority('UPDATE_SYSTEM_ADMIN')")
    @PutMapping("/admin")
    public Response updateSystemAdmin(@Parameter(hidden = true) Principal principal , @Valid @RequestBody CreateUpdateUserDTO dto) {
        return generalService.prepareSuccessResponse(userService.updateUser(dto , principal.getName()));
    }

    @PreAuthorize("hasAuthority('UPDATE_CUSTOMER')")
    @PutMapping("/customer")
    public Response updateCustomer(@Valid @RequestBody CreateUpdateUserDTO dto) {
        return generalService.prepareSuccessResponse(userService.updateUser(dto , null));
    }

    @PreAuthorize("hasAnyAuthority('VIEW_SYSTEM_ADMIN', 'VIEW_CUSTOMERS')")
    @GetMapping("/all")
    public Response getAllUser(@Valid PageableRequestDTO dto) {
        return generalService.prepareSuccessResponse(userService.getUserDTOs(dto));
    }

    @PreAuthorize("hasAuthority('VIEW_SYSTEM_ADMIN')")
    @GetMapping("/admin/all")
    public Response getAllSystemAdmin(@Valid PageableRequestDTO dto) {
        return generalService.prepareSuccessResponse(userService.getAllAdmin(dto));
    }

    @PreAuthorize("hasAuthority('VIEW_CUSTOMERS')")
    @GetMapping("/customer/all")
    public Response getAllCustomer(@Valid PageableRequestDTO dto) {
        return generalService.prepareSuccessResponse(userService.getAllCustomer(dto));
    }

    @PreAuthorize("hasAuthority('DEACTIVATE_ACTIVATE_SYSTEM_ADMIN')")
    @PostMapping("/admin/disable/{userName}")
    public Response disableSystemUser(@PathVariable String userName) {

        if (UserType.ADMIN.equals(userService.getUser(userName).getUserType())) {
            userService.disableUser(userName);

            return generalService.prepareSuccessResponse(MessageConstant.USER_IS_DISABLED);
        }
        return generalService.prepareSuccessResponse(MessageConstant.INVALID_REQUEST);
    }

    @PreAuthorize("hasAuthority('DEACTIVATE_ACTIVATE_CUSTOMER')")
    @PostMapping("/customer/disable/{userName}")
    public Response disableCustomer(@PathVariable String userName) {

        if (UserType.CUSTOMER.equals(userService.getUser(userName).getUserType())) {
            userService.disableUser(userName);

            return generalService.prepareSuccessResponse(MessageConstant.USER_IS_DISABLED);
        }
        return generalService.prepareSuccessResponse(MessageConstant.INVALID_REQUEST);
    }

    @PreAuthorize("hasAuthority('DEACTIVATE_ACTIVATE_SYSTEM_ADMIN')")
    @PostMapping("/admin/enable/{userName}")
    public Response enableSystemUser(@PathVariable String userName) {

        if (UserType.ADMIN.equals(userService.getUser(userName).getUserType())) {
            userService.enableUser(userName);

            return generalService.prepareSuccessResponse(MessageConstant.USER_IS_ENABLED);
        }
        return generalService.prepareSuccessResponse(MessageConstant.INVALID_REQUEST);
    }

    @PreAuthorize("hasAuthority('DEACTIVATE_ACTIVATE_CUSTOMER')")
    @PostMapping("/customer/enable/{userName}")
    public Response enableCustomer(@PathVariable String userName) {

        if (UserType.CUSTOMER.equals(userService.getUser(userName).getUserType())) {
            userService.enableUser(userName);

            return generalService.prepareSuccessResponse(MessageConstant.USER_IS_ENABLED);
        }
        return generalService.prepareSuccessResponse(MessageConstant.INVALID_REQUEST);
    }

    @PreAuthorize("hasAuthority('DELETE_SYSTEM_ADMIN')")
    @DeleteMapping("/admin/delete/{userName}")
    public Response deleteSystemUser(@PathVariable String userName) {

        if (UserType.ADMIN.equals(userService.getUser(userName).getUserType())) {
            userService.deleteUser(userName);

            return generalService.prepareSuccessResponse(MessageConstant.USER_IS_DELETED);
        }
        return generalService.prepareSuccessResponse(MessageConstant.INVALID_REQUEST);

    }

    @PreAuthorize("hasAuthority('DELETE_CUSTOMER')")
    @DeleteMapping("/customer/delete/{userName}")
    public Response deleteCustomer(@PathVariable String userName) {

        if (UserType.CUSTOMER.equals(userService.getUser(userName).getUserType())) {
            userService.deleteUser(userName);

            return generalService.prepareSuccessResponse(MessageConstant.USER_IS_DELETED);
        }
        return generalService.prepareSuccessResponse(MessageConstant.INVALID_REQUEST);

    }

    @PostMapping("/forgetPassword/{identifier}")
    public Response forgetPassword(@PathVariable String identifier) {

        userService.requestResetPassword(identifier);

        return generalService.prepareSuccessResponse(MessageConstant.FORGET_PASSWORD_REQUEST_SUCCESSFUL);

    }

    @PostMapping("/validateForgetPasswordToken")
    public Response validateForgetPasswordToken(@RequestParam("token") String token , @RequestParam("identifier") String identifier) {

        userService.validateForgetPasswordToken(token , identifier);

        return generalService.prepareSuccessResponse(MessageConstant.FORGET_PASSWORD_REQUEST_SUCCESSFUL);

    }

    @PostMapping("/resetPassword")
    public Response resetPassword(@RequestParam("password") String password , @RequestParam("confirmPassword") String confirmPassword , @RequestParam("identifier") String identifier) {

        userService.resetPassword(password , confirmPassword , identifier);

        return generalService.prepareSuccessResponse(MessageConstant.PASSWORD_RESET_SUCCESSFUL);

    }

    @PostMapping("/changePassword")
    public Response changePassword(@Parameter(hidden = true) Principal principal , @Valid @RequestBody ChangePasswordRequestDTO dto) {

        userService.changePassword(principal.getName() , dto);

        return generalService.prepareSuccessResponse(MessageConstant.PASSWORD_CHANGE_SUCCESSFUL);

    }

}
