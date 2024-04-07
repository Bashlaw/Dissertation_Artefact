package com.staffs.backend.repository.packageType;

import com.staffs.backend.entity.licenseType.LicenseType;
import com.staffs.backend.entity.packageType.PackageType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PackageTypeRepository extends JpaRepository<PackageType, Long> {

    boolean existsByPackageTypeNameAndLicenseType(String packageTypeName, LicenseType licenseType);

    PackageType findByPackageTypeNameAndLicenseType(String packageTypeName, LicenseType licenseType);

    PackageType findByPackageTypeIdAndLicenseType(Long packageTypeId, LicenseType licenseType);

    List<PackageType> findByLicenseType(LicenseType licenseType);

    PackageType findByPackageTypeId(Long packageTypeId);

}
