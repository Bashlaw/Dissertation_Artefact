package com.staffs.backend.client.dto;

import lombok.Data;

@Data
public class ClientDTO {

    private String clientName;

    private String description;

    private String officeAddress;

    private String officePhoneNo;

    private String officeMail;

    private String contactPerson;

    private boolean activation;

}
