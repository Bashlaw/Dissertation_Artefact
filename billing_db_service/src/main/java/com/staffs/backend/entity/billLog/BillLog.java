package com.staffs.backend.entity.billLog;

import com.staffs.backend.entity.billingSetup.model.BillingSetup;
import com.staffs.backend.entity.item.Item;
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
public class BillLog extends BaseEntity {

    @Id
    @Column(unique = true)
    private String billLogId;

    private String accountId;

    @ToString.Exclude
    @ManyToOne(optional = false)
    private Item item;

    private Long itemQuantity;

    private String itemRef;

    private double chargeAmount;

    private String paymentStatus;

    @ToString.Exclude
    @ManyToOne(optional = false)
    private PaymentSource paymentOrigin;

    @Column(unique = true)
    private String transRef;

    private boolean isUsed = false;

    @ToString.Exclude
    @ManyToOne(optional = false)
    private BillingSetup billingSetup;

    private boolean isEmailSent = false;

    private boolean isSMSSent = false;

    private boolean isNotificationSent = false;

    private boolean paymentConfirmEmailSent = false;

    private boolean paymentSuccess;

    private String currency;

    private Long itemQuantityLeft;

}
