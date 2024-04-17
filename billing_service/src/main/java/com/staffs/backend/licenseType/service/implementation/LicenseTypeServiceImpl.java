package com.staffs.backend.licenseType.service.implementation;

import com.staffs.backend.client.service.ClientService;
import com.staffs.backend.entity.client.Client;
import com.staffs.backend.entity.licenseType.LicenseType;
import com.staffs.backend.exceptions.GeneralException;
import com.staffs.backend.general.dto.MessageConstant;
import com.staffs.backend.general.enums.ResponseCodeAndMessage;
import com.staffs.backend.licenseType.dto.LicenseTypeDTO;
import com.staffs.backend.licenseType.dto.LicenseTypeDTORequest;
import com.staffs.backend.licenseType.service.LicenseTypeService;
import com.staffs.backend.repository.licenseType.LicenseTypeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class LicenseTypeServiceImpl implements LicenseTypeService {

    private final ClientService clientService;
    private final LicenseTypeRepository licenseTypeRepository;

    public LicenseTypeServiceImpl(ClientService clientService , LicenseTypeRepository licenseTypeRepository) {
        this.clientService = clientService;
        this.licenseTypeRepository = licenseTypeRepository;
    }

    @Override
    public LicenseTypeDTO saveLicenseType(LicenseTypeDTORequest dto) {
        log.info("saving license info!");

        if (!licenseTypeRepository.existsByLicenseTypeNameAndClient(dto.getLicenseTypeName() , clientService.getClientByName(dto.getClientName()))) {
            log.info("saving new license => {}" , dto.getLicenseTypeName());

            LicenseType licenseType = new LicenseType();
            BeanUtils.copyProperties(dto , licenseType);
            licenseType.setValid(true);
            licenseType.setDelFlag(false);
            licenseType.setClient(clientService.getClientByName(dto.getClientName()));
            licenseType.setCreatedAt(LocalDateTime.now());

            //save to DB
            licenseType = licenseTypeRepository.save(licenseType);

            return getLicenseTypeDTO(licenseType);
        } else {
            throw new GeneralException(ResponseCodeAndMessage.ALREADY_EXIST.responseCode , MessageConstant.RECORD_ALREADY_EXISTS);
        }

    }

    @Override
    public LicenseTypeDTO getLicenseDTOByNameAndClientName(String licenseName , String clientName) {
        log.info("getting single licenseDTO info");

        return getLicenseTypeDTO(getLicenseByNameAndClient(licenseName , clientService.getClientByName(clientName)));
    }

    @Override
    public void deleteLicense(String licenseName , String clientName) {
        log.info("delete license info!");

        LicenseType licenseType = getLicenseByNameAndClient(licenseName , clientService.getClientByName(clientName));

        licenseType.setDelFlag(true);
        licenseType.setUpdatedAt(LocalDateTime.now());

        licenseTypeRepository.save(licenseType);

    }

    @Override
    public void invalidateLicense(String licenseName , String clientName) {
        log.info("invalidate license info!");

        //get license info
        LicenseType licenseType = getLicenseByNameAndClient(licenseName , clientService.getClientByName(clientName));

        licenseType.setValid(false);
        licenseType.setUpdatedAt(LocalDateTime.now());

        //save to DB
        licenseTypeRepository.save(licenseType);

    }

    @Override
    public List<LicenseTypeDTO> getLicenseTypes(String clientName) {
        log.info("getting licenses info");

        List<LicenseType> licenseTypes = getLicenses(clientService.getClientByName(clientName));

        return licenseTypes.stream().map(this::getLicenseTypeDTO).collect(Collectors.toList());
    }

    private List<LicenseType> getLicenses(Client client) {
        return licenseTypeRepository.findByDelFlagAndClient(false , client);
    }

    @Override
    public LicenseType getLicenseTypeByNameAndClient(String licenseName , Client client) {
        return licenseTypeRepository.findByLicenseTypeNameAndDelFlagAndClient(licenseName , false , client)
                .orElseThrow(() -> new GeneralException(ResponseCodeAndMessage.RECORD_NOT_FOUND.responseCode , MessageConstant.RECORD_NOT_FOUND));
    }

    private LicenseType getLicenseByNameAndClient(String licenseName , Client client) {
        return licenseTypeRepository.findByLicenseTypeNameAndDelFlagAndClient(licenseName , false , client)
                .orElseThrow(() -> new GeneralException(ResponseCodeAndMessage.RECORD_NOT_FOUND.responseCode , MessageConstant.RECORD_NOT_FOUND));
    }

    @Override
    public LicenseType getLicenseType(String name) {
        return licenseTypeRepository.findByLicenseTypeName(name).orElseThrow(() -> new GeneralException(ResponseCodeAndMessage.RECORD_NOT_FOUND.responseCode , MessageConstant.RECORD_NOT_FOUND));
    }

    private LicenseTypeDTO getLicenseTypeDTO(LicenseType licenseType) {
        log.info("converting license type to license type DTO!");

        LicenseTypeDTO licenseTypeDTO = new LicenseTypeDTO();
        BeanUtils.copyProperties(licenseType , licenseTypeDTO);

        //get client info
        licenseTypeDTO.setClientDTO(clientService.getClientDTOByName(licenseType.getClient().getClientName()));

        return licenseTypeDTO;

    }

}
