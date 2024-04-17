package com.staffs.backend.licenseType.service;

import com.staffs.backend.entity.client.Client;
import com.staffs.backend.entity.licenseType.LicenseType;
import com.staffs.backend.licenseType.dto.LicenseTypeDTO;
import com.staffs.backend.licenseType.dto.LicenseTypeDTORequest;

import java.util.List;

public interface LicenseTypeService {

    LicenseTypeDTO saveLicenseType(LicenseTypeDTORequest dto);

    LicenseTypeDTO getLicenseDTOByNameAndClientName(String licenseName , String clientName);

    void deleteLicense(String licenseName , String clientName);

    void invalidateLicense(String licenseName , String clientName);

    List<LicenseTypeDTO> getLicenseTypes(String clientName);

    LicenseType getLicenseTypeByNameAndClient(String licenseName , Client client);

    LicenseType getLicenseType(String name);

}
