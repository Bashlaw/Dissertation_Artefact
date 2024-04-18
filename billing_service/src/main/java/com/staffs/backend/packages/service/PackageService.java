package com.staffs.backend.packages.service;

import com.staffs.backend.entity.packages.Packages;
import com.staffs.backend.packages.dto.PackageDTO;
import com.staffs.backend.packages.dto.PackageRequestDTO;

import java.util.List;
import java.util.UUID;

public interface PackageService {

    PackageDTO savePackage(PackageRequestDTO requestDTO);

    PackageDTO getPackageDTOByNameAndPackageTypeName(String packageName , Long packageTypeId);

    List<PackageDTO> getPackages(Long packageTypeId);

    void updateActivation(String packageName , Long packageTypeId , boolean activationStatus);

    PackageDTO getPackageDTOByName(String packageName);

    Packages getPackageByName(String packageName);

    PackageDTO getBillPackage(UUID billId);

    Packages getBillPackages(UUID billId);

    void updateRecurring(String packageName , boolean recurringStatus);

}
