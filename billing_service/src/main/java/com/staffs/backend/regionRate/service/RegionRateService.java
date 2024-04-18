package com.staffs.backend.regionRate.service;

import com.staffs.backend.regionRate.dto.RegionRateDTO;
import com.staffs.backend.regionRate.dto.RegionRateRequestDTOList;
import com.staffs.backend.entity.regionRate.RegionRate;

import java.util.List;

public interface RegionRateService {

    List<RegionRateDTO> add(RegionRateRequestDTOList dtoList);

    RegionRateDTO getSingle(String countryShortCode, Long packageRateVersionNo);

    List<RegionRateDTO> getAll(Long packageRateVersionNo);

    RegionRate getSingleRegionRate(String countryName, Long packageRateVersionNo);

    boolean isRegionRate(String countryName, Long packageRateVersionNo);

}
