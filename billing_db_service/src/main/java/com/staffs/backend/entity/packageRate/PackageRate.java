package com.staffs.backend.entity.packageRate;

import com.staffs.backend.entity.packages.Packages;
import com.staffs.backend.utils.BaseEntity;
import lombok.*;
import org.hibernate.annotations.Proxy;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
@Proxy(lazy=false)
public class PackageRate extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long packageRateId;

    @Column(unique = true)
    private Long versionNo;

    private LocalDateTime effectDate;

    private double rate;

    private boolean validate;

    @ToString.Exclude
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Packages packages;

}
