package com.staffs.backend.licenseType.controller;

import com.staffs.backend.general.dto.MessageConstant;
import com.staffs.backend.general.dto.Response;
import com.staffs.backend.general.service.GeneralService;
import com.staffs.backend.licenseType.dto.LicenseTypeDTORequest;
import com.staffs.backend.licenseType.service.LicenseTypeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping("/billing/api/v1/license")
public class LicenseTypeController {

    private final GeneralService generalService;
    private final LicenseTypeService licenseTypeService;

    @PostMapping("/add")
    public Response addLicenseType(@Valid @RequestBody LicenseTypeDTORequest dtoRequest) {

        return generalService.prepareSuccessResponse(licenseTypeService.saveLicenseType(dtoRequest));

    }

    @PostMapping("/delete")
    public Response deleteLicenseType(@Valid @RequestParam(name = "licenseType name") String licenseName , @Valid @RequestParam(name = "client name") String clientName) {

        licenseTypeService.deleteLicense(licenseName , clientName);

        return generalService.prepareSuccessResponse(MessageConstant.SUCCESS);

    }

    @PostMapping("/invalidate")
    public Response invalidateLicenseType(@Valid @RequestParam(name = "licenseType name") String licenseName , @Valid @RequestParam(name = "client name") String clientName) {

        licenseTypeService.invalidateLicense(licenseName , clientName);

        return generalService.prepareSuccessResponse(MessageConstant.SUCCESS);

    }

    @GetMapping("/all")
    public Response getLicenseTypes(@Valid @RequestParam(name = "client name") String clientName) {

        return generalService.prepareSuccessResponse(licenseTypeService.getLicenseTypes(clientName));

    }

    @GetMapping("/single")
    public Response getSingleLicenseType(@Valid @RequestParam(name = "licenseType name") String licenseName , @Valid @RequestParam(name = "client name") String clientName) {

        return generalService.prepareSuccessResponse(licenseTypeService.getLicenseDTOByNameAndClientName(licenseName , clientName));

    }

}
