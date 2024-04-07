package com.staffs.backend.entity.billingSetup.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.staffs.backend.entity.billLog.BillLog;
import com.staffs.backend.entity.billingMethod.BillingMethod;
import com.staffs.backend.entity.packages.Packages;
import com.staffs.backend.entity.paymentSource.PaymentSource;
import com.staffs.backend.utils.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.Proxy;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
@Proxy(lazy = false)
public class BillingSetup extends BaseEntity {

    @Id
    @Column(updatable = false)
    private UUID billId;

    private String accountId;

    @ToString.Exclude
    @ManyToOne(optional = false)
    private BillingMethod billingMethod;

    private String meansOfPayment;

    private boolean validate;

    private LocalDateTime validFrom;

    private LocalDateTime validTill;

    private boolean licenseUpgrade;

    private double chargeAmount;

    private boolean paymentSuccess;

    private String paymentRef;

    private String currency;

    private String email;

    private String phone;

    private String country;

    private String firstName;

    private boolean isEmailSent = false;

    private boolean isSMSSent = false;

    private boolean isNotificationSent = false;

    private boolean paymentConfirmEmailSent = false;

    @ToString.Exclude
    @ManyToOne()
    private PaymentSource paymentSource;

    @ToString.Exclude
    @ManyToMany(fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SUBSELECT)
    @JsonManagedReference
    private List<Packages> packages;

    @Setter
    @ToString.Exclude
    @ManyToMany(fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SUBSELECT)
    @JsonManagedReference
    private List<BillLog> billLogs;

    public void setPackageList(List<Packages> packages) {
        this.packages = packages;
    }

    public void setPackages(Packages packages) {
        List<Packages> packageList = new ArrayList<>();
        packageList.add(packages);
        setPackageList(packageList);
    }

}
