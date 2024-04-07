package com.staffs.backend.entity.licenseType;

import com.staffs.backend.entity.client.Client;
import com.staffs.backend.utils.BaseEntity;
import lombok.*;

import jakarta.persistence.*;

@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
public class LicenseType extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long licenseTypeId;

    @Column(unique = true)
    private String licenseTypeName;

    private String Description;

    private Long userCount;

    private boolean valid;

    private boolean delFlag;

    @ToString.Exclude
    @ManyToOne(optional = false)
    private Client client;

}
