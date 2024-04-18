package com.staffs.backend.regionRate.service.implementation;

import com.staffs.backend.country.service.CountryService;
import com.staffs.backend.entity.country.Country;
import com.staffs.backend.entity.packageRate.PackageRate;
import com.staffs.backend.entity.regionRate.RegionRate;
import com.staffs.backend.exceptions.GeneralException;
import com.staffs.backend.general.dto.MessageConstant;
import com.staffs.backend.general.enums.ResponseCodeAndMessage;
import com.staffs.backend.packageRate.service.PackageRateService;
import com.staffs.backend.regionRate.dto.RegionRateDTO;
import com.staffs.backend.regionRate.dto.RegionRateRequestDTO;
import com.staffs.backend.regionRate.dto.RegionRateRequestDTOList;
import com.staffs.backend.regionRate.service.RegionRateService;
import com.staffs.backend.repository.regionRate.RegionRateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class RegionRateServiceImpl implements RegionRateService {

    private final CountryService countryService;
    private final PackageRateService packageRateService;
    private final RegionRateRepository regionRateRepository;

    @Override
    public List<RegionRateDTO> add(RegionRateRequestDTOList dtoList) {
        log.info("add region rate!");

        //get size
        int dtoSize = dtoList.getRegionRateRequestDTOs().size();

        List<RegionRate> regionRates = new ArrayList<>();

        for (int x = 0; x < dtoSize; x++) {

            RegionRateRequestDTO dto = dtoList.getRegionRateRequestDTOs().get(x);

            //get country
            Country country = countryService.getCountryByShortCode(dto.getCountryShortCode());

            //get package rate
            PackageRate packageRate = packageRateService.getPackageRateByVersionNo(dto.getPackageRateVersionId());

            if (!regionRateRepository.existsByCountryAndPackageRate(country , packageRate)) {
                log.info("saving new region rate => {} {}" , dto.getCountryShortCode() , dto.getPackageRateVersionId());

                RegionRate regionRate = new RegionRate();
                BeanUtils.copyProperties(dto , regionRate);
                regionRate.setCountry(country);
                regionRate.setPackageRate(packageRate);
                regionRate.setCreatedAt(LocalDateTime.now());

                //save to DB
                regionRate = regionRateRepository.save(regionRate);

                regionRates.add(regionRate);

            } else {
                throw new GeneralException(ResponseCodeAndMessage.ALREADY_EXIST.responseCode , MessageConstant.RECORD_ALREADY_EXISTS);
            }
        }

        return RegionRateDTO.getRegionRateDTOs(regionRates , true);

    }

    @Override
    public RegionRateDTO getSingle(String countryShortCode , Long packageRateVersionNo) {
        log.info("getting single region rate DTO info");

        return getRegionRateDTO(getSingleRegionRate(countryShortCode , packageRateVersionNo));

    }

    @Override
    public List<RegionRateDTO> getAll(Long packageRateId) {
        log.info("getting region rate DTOs info by package rate id: {}" , packageRateId);

        List<RegionRate> regionRates = regionRateRepository.findByPackageRate_packageRateId(packageRateId);

        return RegionRateDTO.getRegionRateDTOs(regionRates , false);

    }

    @Override
    public RegionRate getSingleRegionRate(String countryName , Long packageRateVersionNo) {
        log.info("getting single region rate by country name DTO info");

        return regionRateRepository.findByCountry_CountryNameAndPackageRate_VersionNo(countryName , packageRateVersionNo)
                .orElseThrow(() -> new GeneralException(ResponseCodeAndMessage.RECORD_NOT_FOUND.responseCode , MessageConstant.RECORD_NOT_FOUND));

    }

    @Override
    public boolean isRegionRate(String countryName , Long packageRateVersionNo) {
        return Objects.nonNull(regionRateRepository.findByCountry_CountryNameAndPackageRate_VersionNo(countryName , packageRateVersionNo));
    }

    private RegionRateDTO getRegionRateDTO(RegionRate regionRate) {
        log.info("converting regionRate to regionRateDTO");

        RegionRateDTO regionRateDTO = new RegionRateDTO();
        BeanUtils.copyProperties(regionRate , regionRateDTO);

        //get countries DTO
        regionRateDTO.setCountry(countryService.getCountryDTOByShortCode(regionRate.getCountry().getShortCode()));

        //get package rate
        regionRateDTO.setPackageRate(packageRateService.getPackageRateDTOByVersionNo(regionRate.getPackageRate().getVersionNo()));


        return regionRateDTO;

    }

}
