package com.staffs.backend.packageRate.service.implementation;

import com.staffs.backend.entity.packageRate.PackageRate;
import com.staffs.backend.entity.packages.Packages;
import com.staffs.backend.entity.regionRate.RegionRate;
import com.staffs.backend.exceptions.GeneralException;
import com.staffs.backend.general.dto.MessageConstant;
import com.staffs.backend.general.enums.ResponseCodeAndMessage;
import com.staffs.backend.packageRate.dto.PackageRateDTO;
import com.staffs.backend.packageRate.dto.PackageRateRequestDTO;
import com.staffs.backend.packageRate.service.PackageRateService;
import com.staffs.backend.packages.service.PackageService;
import com.staffs.backend.regionRate.dto.RegionRateDTO;
import com.staffs.backend.repository.packageRate.PackageRateRepository;
import com.staffs.backend.repository.regionRate.RegionRateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PackageRateServiceImpl implements PackageRateService {

    private final PackageService packageService;
    private final RegionRateRepository regionRateRepository;
    private final PackageRateRepository packageRateRepository;

    @Override
    public PackageRateDTO savePackageRate(PackageRateRequestDTO dto) {
        log.info("saving package rate info!");

        if (packageRateRepository.existsByVersionNo(dto.getVersionNo())) {
            throw new GeneralException(ResponseCodeAndMessage.ALREADY_EXIST.responseCode , MessageConstant.VERSION_NUMBER_ALREADY_EXISTS);
        }

        //get Package
        Packages packages = packageService.getPackageByName(dto.getPackageName());

        if (!packageRateRepository.existsByVersionNoAndPackages(dto.getVersionNo() , packages)) {
            log.info("saving new package => {} {}" , dto.getVersionNo() , dto.getPackageName());

            PackageRate packageRate = new PackageRate();
            BeanUtils.copyProperties(dto , packageRate);
            packageRate.setPackages(packages);
            packageRate.setCreatedAt(LocalDateTime.now());

            //save to DB
            packageRate = packageRateRepository.save(packageRate);

            return getPackageRateDTO(packageRate);

        } else {
            throw new GeneralException(ResponseCodeAndMessage.ALREADY_EXIST.responseCode , MessageConstant.RECORD_ALREADY_EXISTS);
        }

    }

    @Override
    public PackageRateDTO getPackageRateByVersionNoAndPackageName(Long versionNo , String packageName) {
        log.info("getting single packageRate DTO info");

        //get Package
        Packages packages = packageService.getPackageByName(packageName);

        return getPackageRateDTO(getPackageRateByVersionAndPackage(versionNo , packages));

    }

    @Override
    public List<PackageRateDTO> getPackageDTORates(String packageName) {
        log.info("getting package rate DTOs info");

        //get Package
        Packages packages = packageService.getPackageByName(packageName);

        List<PackageRate> packageRates = getPackageRates(packages);

        return packageRates.stream().map(this::getPackageRateDTO).collect(Collectors.toList());

    }

    @Override
    public void invalidatePackageRate(Long versionNo , String packageName) {
        log.info("invalidate package rate info!");

        //get Package
        Packages packages = packageService.getPackageByName(packageName);

        //get package rate info
        PackageRate packageRate = getPackageRateByVersionAndPackage(versionNo , packages);

        packageRate.setValidate(false);
        packageRate.setUpdatedAt(LocalDateTime.now());

        //save to DB
        packageRateRepository.save(packageRate);

    }

    @Override
    public List<PackageRate> getPackageRates(String packageName) {
        log.info("getting package rate info");

        //get Package
        Packages packages = packageService.getPackageByName(packageName);

        return getPackageRates(packages);

    }

    @Override
    public PackageRateDTO getPackageRateDTOByVersionNo(Long versionNo) {
        return getPackageRateDTO(getPackageRateByVersionNo(versionNo));
    }

    @Override
    public PackageRateDTO getPackageRateDTOByVersionNo4Region(Long versionNo) {
        return getPackageRateDTO4Region(getPackageRateByVersionNo(versionNo));
    }

    @Override
    public PackageRate getPackageRateByVersionNo(Long versionNo) {
        return packageRateRepository.findByVersionNoAndValidate(versionNo , true).orElseThrow(() -> new GeneralException(ResponseCodeAndMessage.RECORD_NOT_FOUND.responseCode , MessageConstant.RECORD_NOT_FOUND));
    }

    private PackageRate getPackageRateByVersionAndPackage(Long versionNo , Packages packages) {
        return packageRateRepository.findByVersionNoAndValidateAndPackages(versionNo , true , packages).orElseThrow(() -> new GeneralException(ResponseCodeAndMessage.RECORD_NOT_FOUND.responseCode , MessageConstant.RECORD_NOT_FOUND));
    }

    private List<PackageRate> getPackageRates(Packages packages) {
        return packageRateRepository.findByValidateAndPackages(true , packages);
    }

    private PackageRateDTO getPackageRateDTO(PackageRate packageRate) {
        log.info("converting package rate to package rate DTO");

        PackageRateDTO packageRateDTO = new PackageRateDTO();
        BeanUtils.copyProperties(packageRate , packageRateDTO);

        // get package info
        packageRateDTO.setPackages(packageService.getPackageDTOByName(packageRate.getPackages().getPackageName()));

        //get region rate
        List<RegionRate> regionRates = regionRateRepository.findByPackageRate_packageRateId(packageRate.getPackageRateId());
        packageRateDTO.setRegionRateDTOList(RegionRateDTO.getRegionRateDTOs(regionRates , false));

        return packageRateDTO;

    }

    private PackageRateDTO getPackageRateDTO4Region(PackageRate packageRate) {
        log.info("converting package rate to package rate DTO for region");

        PackageRateDTO packageRateDTO = new PackageRateDTO();
        BeanUtils.copyProperties(packageRate , packageRateDTO);

        // get package info
        packageRateDTO.setPackages(packageService.getPackageDTOByName(packageRate.getPackages().getPackageName()));

        return packageRateDTO;

    }

}
