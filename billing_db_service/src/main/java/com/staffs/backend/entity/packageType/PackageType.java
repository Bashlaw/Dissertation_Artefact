package com.staffs.backend.entity.packageType;

import com.staffs.backend.entity.licenseType.LicenseType;
import com.staffs.backend.utils.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
public class PackageType extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long packageTypeId;

    @Column(unique = true)
    private String packageTypeName;

    private String description;

    @ToString.Exclude
    @ManyToOne(optional = false)
    private LicenseType licenseType;

    @Column(columnDefinition = "boolean default false")
    private boolean isVisit;

}
