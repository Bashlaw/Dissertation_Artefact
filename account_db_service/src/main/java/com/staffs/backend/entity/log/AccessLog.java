package com.staffs.backend.entity.log;

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
public class AccessLog extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private long userId;

    private String deviceInfo;

    private String ipAddress;

    private Long accessedService;

}
