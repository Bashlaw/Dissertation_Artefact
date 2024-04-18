package com.staffs.backend.regionRate.controller;

import com.staffs.backend.general.dto.Response;
import com.staffs.backend.general.service.GeneralService;
import com.staffs.backend.regionRate.dto.RegionRateRequestDTOList;
import com.staffs.backend.regionRate.service.RegionRateService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping("/billing/api/v1/regionRate")
public class RegionRateController {

    private final GeneralService generalService;
    private final RegionRateService regionRateService;

    @PostMapping("/add")
    public Response addRegionRate(@Valid @RequestBody RegionRateRequestDTOList dtoList) {

        return generalService.prepareSuccessResponse(regionRateService.add(dtoList));

    }

    @GetMapping("/single/{countryShortCode}/{packageRateVersionNo}")
    public Response getSingleRegionRate(@PathVariable String countryShortCode , @PathVariable Long packageRateVersionNo) {

        return generalService.prepareSuccessResponse(regionRateService.getSingle(countryShortCode , packageRateVersionNo));

    }

}
