package com.staffs.backend.packageType.controller;

import com.staffs.backend.general.dto.Response;
import com.staffs.backend.general.service.GeneralService;
import com.staffs.backend.packageType.dto.PackageTypeDTORequest;
import com.staffs.backend.packageType.service.PackageTypeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping("/billing/api/v1/packageType")
public class PackageTypeController {

    private final GeneralService generalService;
    private final PackageTypeService packageTypeService;

    @PostMapping("/add")
    public Response addPackageType(@Valid @RequestBody PackageTypeDTORequest dtoRequest) {

        return generalService.prepareSuccessResponse(packageTypeService.savePackageType(dtoRequest));

    }

    @GetMapping("/all")
    public Response getPackageTypes(@Valid @RequestParam(name = "licenseType name") String licenseName) {

        return generalService.prepareSuccessResponse(packageTypeService.getPackageTypes(licenseName));

    }

    @GetMapping("/single")
    public Response getSinglePackageType(@Valid @RequestParam(name = "packageType ID") Long packageTypeId , @Valid @RequestParam(name = "licenseType name") String licenseName) {

        return generalService.prepareSuccessResponse(packageTypeService.getPackageTypeDTOByNameAndLicenseName(packageTypeId , licenseName));

    }

}
