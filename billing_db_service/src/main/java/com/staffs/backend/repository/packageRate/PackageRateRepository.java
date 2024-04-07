package com.staffs.backend.repository.packageRate;

import com.staffs.backend.entity.packageRate.PackageRate;
import com.staffs.backend.entity.packages.Packages;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PackageRateRepository extends JpaRepository<PackageRate, Long> {

    boolean existsByVersionNo(Long versionNo);

    boolean existsByVersionNoAndPackages(Long versionNo, Packages packages);

    PackageRate findByVersionNoAndValidateAndPackages(Long versionNo, boolean validate, Packages packages);

    List<PackageRate> findByValidateAndPackages(boolean validate, Packages packages);

    PackageRate findByVersionNoAndValidate(Long versionNo, boolean validate);

    List<PackageRate> findByPackages(Packages packages);

    @Query(value = "CALL get_package_rate(:packageName, 0);", nativeQuery = true)
    int findSingleValidRate(@Param("packageName") String packageName);

}
