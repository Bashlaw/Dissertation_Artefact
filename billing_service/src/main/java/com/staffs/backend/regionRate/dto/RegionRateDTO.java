package com.staffs.backend.regionRate.dto;

import com.staffs.backend.country.dto.CountryDTO;
import com.staffs.backend.entity.regionRate.RegionRate;
import com.staffs.backend.packageRate.dto.PackageRateDTO;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;

@Data
@Slf4j
public class RegionRateDTO {

    private Long regionRateId;

    private double rate;

    private CountryDTO country;

    private PackageRateDTO packageRate;

    public static List<RegionRateDTO> getRegionRateDTOs(List<RegionRate> regionRates) {
        log.info("converting regionRate list to regionRateDTO list");

        List<RegionRateDTO> regionRateDTOs = new ArrayList<>();
        BeanUtils.copyProperties(regionRates , regionRateDTOs);

//        for (RegionRate regionRate : regionRates) {
//
//            RegionRateDTO regionRateDTO = new RegionRateDTO();
//            BeanUtils.copyProperties(regionRate , regionRateDTO);
//
//            //add country info
//            CountryDTO countryDTO = countryService.getCountryByCountryCode(regionRate.getCountry().getCountryCode());
//
//            regionRateDTO.setCountry(countryDTO);
//            if (showRate) {
//                //add package rate info
//                PackageRateDTO packageRateDTO = packageRateService.getPackageRateDTOByVersionNo(regionRate.getPackageRate().getVersionNo());
//
//                regionRateDTO.setPackageRate(packageRateDTO);
//            }
//
//            regionRateDTOs.add(regionRateDTO);
//
//        }

        return regionRateDTOs;

    }

}
