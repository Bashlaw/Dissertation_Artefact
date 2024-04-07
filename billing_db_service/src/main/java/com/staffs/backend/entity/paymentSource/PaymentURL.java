package com.staffs.backend.entity.paymentSource;

import com.fasterxml.jackson.annotation.JsonManagedReference;
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
public class PaymentURL extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long urlId;

    private String url;

    @ToString.Exclude
    @ManyToMany(fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<PaymentSource> paymentSources;

}

