package com.staffs.backend.packages.service.implementation;

import com.staffs.backend.entity.item.Item;
import com.staffs.backend.entity.packageRate.PackageRate;
import com.staffs.backend.entity.packageType.PackageType;
import com.staffs.backend.entity.packages.Packages;
import com.staffs.backend.exceptions.GeneralException;
import com.staffs.backend.general.dto.MessageConstant;
import com.staffs.backend.general.enums.ResponseCodeAndMessage;
import com.staffs.backend.item.dto.ItemDTO;
import com.staffs.backend.packageRate.dto.PackageRateDTO;
import com.staffs.backend.packageType.service.PackageTypeService;
import com.staffs.backend.packages.dto.PackageDTO;
import com.staffs.backend.packages.dto.PackageListDTO;
import com.staffs.backend.packages.dto.PackageRequestDTO;
import com.staffs.backend.packages.service.PackageService;
import com.staffs.backend.repository.item.ItemRepository;
import com.staffs.backend.repository.packageRate.PackageRateRepository;
import com.staffs.backend.repository.packages.PackageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PackageServiceImpl implements PackageService {

    private final ItemRepository itemRepository;
    private final PackageRepository packageRepository;
    private final PackageTypeService packageTypeService;
    private final PackageRateRepository packageRateRepository;

    @Override
    public PackageDTO savePackage(PackageRequestDTO requestDTO) {
        log.info("saving package info!");

        //get the package type
        PackageType packageType = packageTypeService.getPackageTypeById(requestDTO.getPackageTypeId());

        if (!packageRepository.existsByPackageNameAndPackageType(requestDTO.getPackageName() , packageType)) {
            log.info("saving new package => {}" , requestDTO.getPackageName());

            Packages packages = new Packages();
            BeanUtils.copyProperties(requestDTO , packages);
            packages.setPackageType(packageType);
            packages.setCreatedAt(LocalDateTime.now());

            //save to DB
            packages = packageRepository.save(packages);

            return getPackageDTO(packages);

        } else {
            throw new GeneralException(ResponseCodeAndMessage.ALREADY_EXIST.responseCode , MessageConstant.RECORD_ALREADY_EXISTS);
        }

    }

    @Override
    public PackageDTO getPackageDTOByNameAndPackageTypeName(String packageName , Long packageTypeId) {
        log.info("getting single package DTO info");

        //get the package type
        PackageType packageType = packageTypeService.getPackageTypeById(packageTypeId);

        return getPackageDTO(getPackageByNameAndPackageType(packageName , packageType));

    }

    @Override
    public List<PackageDTO> getPackages(Long packageTypeId) {
        log.info("getting package DTOs info");

        //get the package type
        PackageType packageType = packageTypeService.getPackageTypeById(packageTypeId);

        List<Packages> packages = getPackages(packageType);

        return packages.stream().map(this::getPackageDTO).collect(Collectors.toList());

    }

    @Override
    public void updateActivation(String packageName , Long packageTypeId , boolean activationStatus) {
        log.info("enable/disable package!");

        //get package info
        Packages packages = getPackage(packageName , packageTypeService.getPackageTypeById(packageTypeId));

        packages.setActivation(activationStatus);
        packages.setUpdatedAt(LocalDateTime.now());

        //save to DB
        packageRepository.save(packages);

    }

    @Override
    public PackageDTO getPackageDTOByName(String packageName) {
        return getPackageDTO(getPackageByName(packageName));
    }

    @Override
    public Packages getPackageByName(String packageName) {
        return packageRepository.findByPackageNameAndActivation(packageName , true).orElseThrow(() -> new GeneralException(ResponseCodeAndMessage.RECORD_NOT_FOUND.responseCode , MessageConstant.RECORD_NOT_FOUND));
    }

    @Override
    public PackageDTO getBillPackage(UUID billId) {
        log.info("getting package dto for  a particular bill");

        //get package id
        Long packageId = packageRepository.getPackageIdForBill(billId);

        //get package
        Packages packages = getPackageById(packageId);

        return getPackageDTO(packages);

    }

    @Override
    public Packages getBillPackages(UUID billId) {
        log.info("getting package for  a particular bill");

        //get package id
        Long packageId = packageRepository.getPackageIdForBill(billId);

        //get package
        return getPackageById(packageId);

    }

    @Override
    public void updateRecurring(String packageName , boolean recurringStatus) {
        log.info("set package recurring status");

        //get package info
        Packages packages = getPackageByName(packageName);

        packages.setRecurring(recurringStatus);
        packages.setUpdatedAt(LocalDateTime.now());

        //save to DB
        packageRepository.save(packages);

    }

    private List<Packages> getPackages(PackageType packageType) {
        return packageRepository.findByPackageTypeAndActivation(packageType , true);
    }

    private Packages getPackageByNameAndPackageType(String packageName , PackageType packageType) {
        return packageRepository.findByPackageNameAndPackageTypeAndActivation(packageName , packageType , true).orElseThrow(() -> new GeneralException(ResponseCodeAndMessage.RECORD_NOT_FOUND.responseCode , MessageConstant.RECORD_NOT_FOUND));
    }

    private Packages getPackage(String packageName , PackageType packageType) {
        return packageRepository.findByPackageNameAndPackageType(packageName , packageType).orElseThrow(() -> new GeneralException(ResponseCodeAndMessage.RECORD_NOT_FOUND.responseCode , MessageConstant.RECORD_NOT_FOUND));
    }

    private PackageDTO getPackageDTO(Packages packages) {
        log.info("converting packages to package DTO");

        PackageDTO packageDTO = new PackageDTO();
        BeanUtils.copyProperties(packages , packageDTO);

        //get package type info
        packageDTO.setPackageType(packageTypeService.getPackageTypeDTOById(packages.getPackageType().getPackageTypeId()));

        //get package items
        List<Item> items = itemRepository.findByPackages_packageNameAndDelFlag(packages.getPackageName() , false);
        List<ItemDTO> itemDTO = items.stream().map(item -> ItemDTO.getItemDTO4Package(item , packages.getPackageName())).toList();
        packageDTO.setItemList(itemDTO);

        //package rates DTO
        List<PackageRateDTO> packageRateDTOs = packageRateRepository.findByPackages(packages).stream().map(PackageRateDTO::getPackageRateDTOForPackages).toList();

        if (!packageRateDTOs.contains(null)) {
            packageDTO.setPackageRateDTOList(packageRateDTOs);
        } else {
            packageDTO.setPackageRateDTOList(null);
        }

        //get package rate
        int versionNo = packageRateRepository.findSingleValidRate(packageDTO.getPackageName());
        if (versionNo > 0) {

            //current bill package rate DTO
            PackageRate packageRate = packageRateRepository.findByVersionNoAndValidateAndPackages((long) versionNo , true , packages).orElseThrow(() -> new GeneralException(ResponseCodeAndMessage.RECORD_NOT_FOUND.responseCode , MessageConstant.RECORD_NOT_FOUND));
            PackageRateDTO packageRateDTO = PackageRateDTO.getPackageRateDTOForPackages(packageRate);

            if (Objects.nonNull(packageRateDTO)) {
                packageDTO.setCurrentBillRates(packageRateDTO);
            } else {
                packageDTO.setCurrentBillRates(null);
            }

        } else {
            packageDTO.setCurrentBillRates(null);
        }

        return packageDTO;
    }

    private Packages getPackageById(Long packageId) {
        return packageRepository.findById(packageId).orElseThrow(() -> new GeneralException(ResponseCodeAndMessage.RECORD_NOT_FOUND.responseCode , MessageConstant.RECORD_NOT_FOUND));
    }

    private PackageListDTO getPackageListDTO(Page<Packages> packagesPage) {
        log.info("Converting package page to package list dto");

        PackageListDTO packageListDTO = new PackageListDTO();

        List<Packages> packages = packagesPage.getContent();
        if (!packages.isEmpty()) {
            packageListDTO.setHasNextRecord(packagesPage.hasNext());
            packageListDTO.setTotalCount((int) packagesPage.getTotalElements());
        }

        List<PackageDTO> packageDTOs = packages.stream().map(this::getPackageDTO).collect(Collectors.toList());
        packageListDTO.setPackageDTOs(packageDTOs);

        return packageListDTO;

    }

}
