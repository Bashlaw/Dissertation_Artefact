package com.staffs.backend.licenseType.dto;

import com.staffs.backend.client.dto.ClientDTO;
import lombok.Data;

@Data
public class LicenseTypeDTO {

    private String licenseTypeName;

    private String Description;

    private Long userCount;

    private boolean valid;

    private ClientDTO clientDTO;

}
