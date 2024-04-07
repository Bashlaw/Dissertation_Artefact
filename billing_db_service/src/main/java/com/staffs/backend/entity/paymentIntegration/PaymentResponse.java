package com.staffs.backend.entity.paymentIntegration;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.*;

@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
public class PaymentResponse {

    @Id
    @Column(unique = true)
    private String paymentResponseId;

    private int status;

    private String message;

    private String error;

    private String paymentUrl;

    @ToString.Exclude
    @OneToOne
    private PaymentIntegration paymentIntegration;

}
