package com.staffs.backend.repository.packageType;

import com.staffs.backend.entity.licenseType.LicenseType;
import com.staffs.backend.entity.packageType.PackageType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PackageTypeRepository extends JpaRepository<PackageType, Long> {

    boolean existsByPackageTypeNameAndLicenseType(String packageTypeName, LicenseType licenseType);

    Optional<PackageType> findByPackageTypeNameAndLicenseType(String packageTypeName, LicenseType licenseType);

    Optional<PackageType> findByPackageTypeIdAndLicenseType(Long packageTypeId, LicenseType licenseType);

    List<PackageType> findByLicenseType(LicenseType licenseType);

    Optional<PackageType> findByPackageTypeId(Long packageTypeId);

}
