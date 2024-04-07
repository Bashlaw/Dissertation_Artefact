package com.staffs.backend.entity.country;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.staffs.backend.entity.paymentSource.PaymentSource;
import com.staffs.backend.utils.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Proxy;

import java.util.List;

@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
@Proxy(lazy = false)
public class Country extends BaseEntity {

    @Id
    @Column(unique = true, nullable = false)
    private Long countryCode;

    @Column(unique = true, nullable = false)
    private String shortCode;

    @Column(unique = true, nullable = false)
    private String countryName;

    private String phoneFormat;

    @ToString.Exclude
    @ManyToMany(fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<PaymentSource> paymentSources;

}
