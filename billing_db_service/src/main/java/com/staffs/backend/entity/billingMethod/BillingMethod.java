package com.staffs.backend.entity.billingMethod;

import com.staffs.backend.utils.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
public class BillingMethod extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long billingMethodID;

    @Column(unique = true)
    private String billingMethodName;

    private String description;

    private boolean validate;

}
