package com.staffs.backend.entity.log;

import com.staffs.backend.enums.log.DataChangeType;
import com.staffs.backend.utils.BaseEntity;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import jakarta.persistence.*;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
public class ChangeLog extends BaseEntity {

    @Id
    @Column(unique = true, updatable = false, nullable = false)
    private String logId;

    @Column(updatable = false, nullable = false)
    private String module;

    @Column(updatable = false, nullable = false)
    private Long userId;

    @Column(updatable = false, nullable = false)
    private String action;

    @Column(updatable = false, nullable = false)
    private DataChangeType changeType;

}
