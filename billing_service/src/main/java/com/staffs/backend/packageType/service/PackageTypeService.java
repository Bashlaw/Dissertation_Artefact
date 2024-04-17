package com.staffs.backend.packageType.service;

import com.staffs.backend.entity.licenseType.LicenseType;
import com.staffs.backend.packageType.dto.PackageTypeDTO;
import com.staffs.backend.packageType.dto.PackageTypeDTORequest;
import com.staffs.backend.entity.packageType.PackageType;

import java.util.List;

public interface PackageTypeService {

    PackageTypeDTO savePackageType(PackageTypeDTORequest dto);

    PackageTypeDTO getPackageTypeDTOByNameAndLicenseName(Long packageTypeId, String licenseName);

    List<PackageTypeDTO> getPackageTypes(String licenseName);

    PackageType getPackageTypeByNameAndLicense(String packageTypeName, LicenseType licenseType);

    PackageType getPackageTypeById(Long packageTypeId);

    PackageTypeDTO getPackageTypeDTOById(Long packageTypeId);

}
