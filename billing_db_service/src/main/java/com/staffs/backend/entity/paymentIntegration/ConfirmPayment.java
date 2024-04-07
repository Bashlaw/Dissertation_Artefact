package com.staffs.backend.entity.paymentIntegration;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
public class ConfirmPayment {

    @Id
    @Column(unique = true)
    private String confirmPaymentId;

    private Long status;

    private String message;

    private String companyRef;

    @Column(unique = true)
    private String transactionRef;

    private String amount;

    private String fraudLevel;

    private String fraudExplanation;

    private String transFee;

}
