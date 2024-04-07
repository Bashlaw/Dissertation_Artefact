package com.staffs.backend.repository.country;

import com.staffs.backend.entity.country.Country;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CountryRepository extends JpaRepository<Country, Long> {

    Country findByCountryCode(Long countryCode);

    Country findByShortCode(String shortCode);

}
