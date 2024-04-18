package com.staffs.backend.packageRate.controller;

import com.staffs.backend.general.dto.Response;
import com.staffs.backend.general.service.GeneralService;
import com.staffs.backend.packageRate.dto.PackageRateRequestDTO;
import com.staffs.backend.packageRate.service.PackageRateService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping("/billing/api/v1/packageRate")
public class PackageRateController {

    private final GeneralService generalService;
    private final PackageRateService packageRateService;

    @PostMapping("/add")
    public Response addPackageRate(@Valid @RequestBody PackageRateRequestDTO rateRequestDTO) {

        return generalService.prepareSuccessResponse(packageRateService.savePackageRate(rateRequestDTO));

    }

    @GetMapping("/all")
    public Response getPackageRates(@Valid@RequestParam(name = "package name") String packageName) {

        return generalService.prepareSuccessResponse(packageRateService.getPackageDTORates(packageName));

    }

    @GetMapping("/single")
    public Response getSinglePackageRate(@Valid @RequestParam(name = "versionNo") Long versionNo , @Valid @RequestParam(name = "package name") String packageName) {

        return generalService.prepareSuccessResponse(packageRateService.getPackageRateByVersionNoAndPackageName(versionNo , packageName));

    }

}
