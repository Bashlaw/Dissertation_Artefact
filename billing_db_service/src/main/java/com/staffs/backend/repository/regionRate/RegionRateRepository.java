package com.staffs.backend.repository.regionRate;

import com.staffs.backend.entity.country.Country;
import com.staffs.backend.entity.packageRate.PackageRate;
import com.staffs.backend.entity.regionRate.RegionRate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RegionRateRepository extends JpaRepository<RegionRate, Long> {

    boolean existsByCountryAndPackageRate(Country country, PackageRate packageRate);

    Optional<RegionRate> findByCountry_ShortCodeAndPackageRate_VersionNo(String shortCode, Long versionNo);

    List<RegionRate> findByPackageRate_packageRateId(Long PackageRateId);

    Optional<RegionRate> findByCountry_CountryNameAndPackageRate_VersionNo(String countryName, Long versionNo);

}
