package com.staffs.backend.packageType.service.implementation;

import com.staffs.backend.entity.licenseType.LicenseType;
import com.staffs.backend.entity.packageType.PackageType;
import com.staffs.backend.exceptions.GeneralException;
import com.staffs.backend.general.dto.MessageConstant;
import com.staffs.backend.general.enums.ResponseCodeAndMessage;
import com.staffs.backend.licenseType.service.LicenseTypeService;
import com.staffs.backend.packageType.dto.PackageTypeDTO;
import com.staffs.backend.packageType.dto.PackageTypeDTORequest;
import com.staffs.backend.packageType.service.PackageTypeService;
import com.staffs.backend.repository.packageType.PackageTypeRepository;
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
public class PackageTypeServiceImpl implements PackageTypeService {

    private final LicenseTypeService licenseTypeService;
    private final PackageTypeRepository packageTypeRepository;

    @Override
    public PackageTypeDTO savePackageType(PackageTypeDTORequest dto) {
        log.info("saving package type info!");

        //get LicenseType
        LicenseType licenseType = licenseTypeService.getLicenseType(dto.getLicenseTypeName());

        if (!packageTypeRepository.existsByPackageTypeNameAndLicenseType(dto.getPackageTypeName() , licenseType)) {

            //confirm the license type is mapped to the same client
            log.info("saving new package type => {}" , dto.getPackageTypeName());

            PackageType packageType = new PackageType();
            BeanUtils.copyProperties(dto , packageType);
            packageType.setVisit(dto.isVisit());
            packageType.setLicenseType(licenseType);
            packageType.setCreatedAt(LocalDateTime.now());

            //save to DB
            packageType = packageTypeRepository.save(packageType);

            return getPackageTypeDTO(packageType);

        } else {
            throw new GeneralException(ResponseCodeAndMessage.ALREADY_EXIST.responseCode , MessageConstant.RECORD_ALREADY_EXISTS);
        }

    }

    @Override
    public PackageTypeDTO getPackageTypeDTOByNameAndLicenseName(Long packageTypeId , String licenseName) {
        log.info("getting single package type DTO info");

        return getPackageTypeDTO(getPackageTypeByIdAndLicenseType(packageTypeId , licenseTypeService.getLicenseType(licenseName)));

    }

    @Override
    public List<PackageTypeDTO> getPackageTypes(String licenseName) {
        log.info("getting package type DTOs info");

        List<PackageType> packageTypes = getPackageTypes(licenseTypeService.getLicenseType(licenseName));

        return packageTypes.stream().map(this::getPackageTypeDTO).collect(Collectors.toList());

    }

    @Override
    public PackageType getPackageTypeByNameAndLicense(String packageTypeName , LicenseType licenseType) {
        return getPackageTypeByNameAndLicenseType(packageTypeName , licenseType);
    }

    @Override
    public PackageType getPackageTypeById(Long packageTypeId) {
        return packageTypeRepository.findByPackageTypeId(packageTypeId).orElseThrow(() -> new GeneralException(ResponseCodeAndMessage.RECORD_NOT_FOUND.responseCode , MessageConstant.RECORD_NOT_FOUND));
    }

    @Override
    public PackageTypeDTO getPackageTypeDTOById(Long packageTypeId) {
        return getPackageTypeDTO(getPackageTypeById(packageTypeId));
    }

    private List<PackageType> getPackageTypes(LicenseType licenseType) {
        return packageTypeRepository.findByLicenseType(licenseType);
    }

    private PackageType getPackageTypeByNameAndLicenseType(String packageTypeName , LicenseType licenseType) {
        return packageTypeRepository.findByPackageTypeNameAndLicenseType(packageTypeName , licenseType).orElseThrow(() -> new GeneralException(ResponseCodeAndMessage.RECORD_NOT_FOUND.responseCode , MessageConstant.RECORD_NOT_FOUND));
    }

    private PackageType getPackageTypeByIdAndLicenseType(Long packageTypeId , LicenseType licenseType) {
        return packageTypeRepository.findByPackageTypeIdAndLicenseType(packageTypeId , licenseType).orElseThrow(() -> new GeneralException(ResponseCodeAndMessage.RECORD_NOT_FOUND.responseCode , MessageConstant.RECORD_NOT_FOUND));
    }

    private PackageTypeDTO getPackageTypeDTO(PackageType packageType) {
        log.info("converting package type to package type DTO");

        PackageTypeDTO packageTypeDTO = new PackageTypeDTO();
        BeanUtils.copyProperties(packageType , packageTypeDTO);

        //get license info
        packageTypeDTO.setLicenseTypeDTO(licenseTypeService.getLicenseDTOByNameAndClientName(packageType.getLicenseType().getLicenseTypeName() , packageType.getLicenseType().getClient().getClientName()));

        return packageTypeDTO;

    }

}
