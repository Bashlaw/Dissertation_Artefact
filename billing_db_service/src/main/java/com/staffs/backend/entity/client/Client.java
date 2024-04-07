package com.staffs.backend.entity.client;

import com.staffs.backend.utils.BaseEntity;
import lombok.*;

import jakarta.persistence.*;

@Entity(name = "clients")
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
public class Client extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long clientId;

    @Column(unique = true)
    private String clientName;

    private String description;

    private String officeAddress;

    private String officePhoneNo;

    private String officeMail;

    private String contactPerson;

    private boolean activation;

}
