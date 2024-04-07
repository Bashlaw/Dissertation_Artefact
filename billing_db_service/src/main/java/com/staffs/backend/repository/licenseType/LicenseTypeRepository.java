package com.staffs.backend.repository.licenseType;

import com.staffs.backend.entity.client.Client;
import com.staffs.backend.entity.licenseType.LicenseType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LicenseTypeRepository extends JpaRepository<LicenseType, Long> {

    boolean existsByLicenseTypeNameAndClient(String licenseTypeName, Client client);

    LicenseType findByLicenseTypeNameAndDelFlagAndClient(String licenseTypeName, boolean delFlag, Client client);

    LicenseType findByLicenseTypeNameAndDelFlag(String licenseTypeName, boolean delFlag);

    LicenseType findByLicenseTypeNameAndClient(String licenseTypeName, Client client);

    LicenseType findByLicenseTypeName(String licenseTypeName);

    List<LicenseType> findByDelFlagAndClient(boolean delFlag, Client client);

    List<LicenseType> findByDelFlagAndClientAndValid(boolean delFlag, Client client, boolean valid);

    LicenseType findByLicenseTypeNameAndDelFlagAndClientAndValid(String licenseTypeName, boolean delFlag, Client client, boolean valid);

}
