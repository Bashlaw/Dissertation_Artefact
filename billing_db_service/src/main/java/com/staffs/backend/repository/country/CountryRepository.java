package com.staffs.backend.repository.country;

import com.staffs.backend.entity.country.Country;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CountryRepository extends JpaRepository<Country, Long> {

    Optional<Country> findByCountryCode(Long countryCode);

    Optional<Country> findByShortCode(String shortCode);

}
