package com.staffs.backend.entity.paymentIntegration;

import com.staffs.backend.entity.paymentSource.PaymentSource;
import com.staffs.backend.utils.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.*;

@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
public class PaymentIntegration extends BaseEntity {

    @Id
    @Column(unique = true)
    private String paymentId;

    private String client;

    private String accountId;

    @Column(nullable = false)
    private String transRef;

    private double amount;

    private String currency;

    private String paymentType;

    private String country;

    private String method;

    private String redirectUrl;

    private String backUrl;

    private String serviceType;

    private String serviceDate;

    private String description;

    @ToString.Exclude
    @ManyToOne(optional = false)
    private PaymentSource paymentSource;

    private String phoneNumber;

}
