package com.staffs.backend.packages.controller;

import com.staffs.backend.general.dto.MessageConstant;
import com.staffs.backend.general.dto.Response;
import com.staffs.backend.general.service.GeneralService;
import com.staffs.backend.packages.dto.PackageRequestDTO;
import com.staffs.backend.packages.service.PackageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping("/billing/api/v1/packages")
public class PackageController {

    private final GeneralService generalService;
    private final PackageService packageService;

    @PostMapping("/add")
    public Response addPackage(@RequestBody PackageRequestDTO requestDTO) {

        return generalService.prepareSuccessResponse(packageService.savePackage(requestDTO));

    }

    @GetMapping("/all")
    public Response getPackages(@Valid @RequestParam(name = "packageType ID") Long packageTypeId) {

        return generalService.prepareSuccessResponse(packageService.getPackages(packageTypeId));

    }

    @GetMapping("/single")
    public Response getSinglePackage(@Valid @RequestParam(name = "package name") String packageName , @Valid @RequestParam(name = "packageType ID") Long packageTypeId) {

        return generalService.prepareSuccessResponse(packageService.getPackageDTOByNameAndPackageTypeName(packageName , packageTypeId));

    }

    @PostMapping("/enableDisable")
    public Response enableDisablePackage(@Valid @RequestParam(name = "package name") String packageName , @Valid @RequestParam(name = "packageType ID") Long packageTypeId , @Valid @RequestParam(name = "active status", defaultValue = "true") boolean status) {

        packageService.updateActivation(packageName , packageTypeId , status);

        return generalService.prepareSuccessResponse(MessageConstant.SUCCESS);

    }

    @PostMapping("/setPackageRecurringStatus")
    public Response setPackageRecurringStatus(@Valid @RequestParam(name = "package name") String packageName , @Valid@RequestParam(name = "recurring status", defaultValue = "true") boolean status) {

        packageService.updateRecurring(packageName , status);

        return generalService.prepareSuccessResponse(MessageConstant.SUCCESS);

    }

}
