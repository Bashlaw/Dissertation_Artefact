package com.staffs.backend.country.service;

import com.staffs.backend.country.dto.CountryDTO;
import com.staffs.backend.entity.country.Country;
import com.staffs.backend.entity.paymentSource.PaymentSource;

import java.util.List;

public interface CountryService {

    void updateCountryInfo(String code , PaymentSource paymentSource);

    CountryDTO getCountryByCountryCode(Long countryCode);

    CountryDTO getCountryDTOByShortCode(String shortCode);

    List<CountryDTO> getCountriesDTO(List<Country> countries);

    Country getCountryByShortCode(String shortCode);

    List<CountryDTO> getAllCountriesDTO();

}
