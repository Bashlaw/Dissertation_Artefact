package com.staffs.backend.packageRate.service;

import com.staffs.backend.entity.packageRate.PackageRate;
import com.staffs.backend.packageRate.dto.PackageRateDTO;
import com.staffs.backend.packageRate.dto.PackageRateRequestDTO;

import java.util.List;

public interface PackageRateService {

    PackageRateDTO savePackageRate(PackageRateRequestDTO dto);

    PackageRateDTO getPackageRateByVersionNoAndPackageName(Long versionNo , String packageName);

    List<PackageRateDTO> getPackageDTORates(String packageName);

    PackageRate getPackageRateByVersionNo(Long versionNo);

    void invalidatePackageRate(Long versionNo , String packageName);

    List<PackageRate> getPackageRates(String packageName);

    PackageRateDTO getPackageRateDTOByVersionNo(Long versionNo);

    PackageRateDTO getPackageRateDTOByVersionNo4Region(Long versionNo);

}
