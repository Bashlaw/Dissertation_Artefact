package com.staffs.backend.country.controller;

import com.staffs.backend.country.service.CountryService;
import com.staffs.backend.general.dto.Response;
import com.staffs.backend.general.service.GeneralService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping("/billing/api/v1/country")
public class CountryController {

    private final CountryService countryService;
    private final GeneralService generalService;

    @GetMapping("/{shortCode}")
    public Response getCountryByShortCode(@PathVariable String shortCode) {

        return generalService.prepareSuccessResponse(countryService.getCountryDTOByShortCode(shortCode));

    }

    @GetMapping("/all")
    public Response getAllCountries() {

        return generalService.prepareSuccessResponse(countryService.getAllCountriesDTO());

    }

}
