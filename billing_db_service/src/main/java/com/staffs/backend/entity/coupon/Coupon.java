package com.staffs.backend.entity.coupon;

import com.staffs.backend.entity.billLog.BillLog;
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
public class Coupon extends BaseEntity {

    @Id
    @Column(updatable = false)
    private String couponId;

    @Column(updatable = false, unique = true)
    private String code;

    @ToString.Exclude
    @ManyToOne(optional = false)
    private BillLog billLog;

    private boolean used;

}
