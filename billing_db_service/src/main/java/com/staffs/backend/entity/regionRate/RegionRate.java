package com.staffs.backend.entity.regionRate;

import com.staffs.backend.entity.country.Country;
import com.staffs.backend.entity.packageRate.PackageRate;
import com.staffs.backend.utils.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Proxy;

@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
@Proxy(lazy = false)
public class RegionRate extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long regionRateId;

    @Column(nullable = false)
    private double rate;

    @ToString.Exclude
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Country country;

    @ToString.Exclude
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private PackageRate packageRate;

}
