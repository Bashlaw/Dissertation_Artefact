package com.staffs.backend.entity.transactionLog;

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
public class TransactionLog extends BaseEntity {

    @Id
    @Column(unique = true)
    private String transLogId;

    @ToString.Exclude
    @ManyToOne(optional = false)
    private BillLog billLog;

    private boolean isCredit;

    @Column(unique = true)
    private String transRef;

    private String reason;

}
