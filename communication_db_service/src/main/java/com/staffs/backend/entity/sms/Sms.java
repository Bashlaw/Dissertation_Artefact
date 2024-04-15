package com.staffs.backend.entity.sms;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class Sms {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) //Generates the ID for us
    private Long id;

    @Column(nullable = false)
    private String smsTo;

    @Lob
    private String smsContent;

    private boolean sent = false;

    @Lob
    private String failureReason;

    private Date createdOn = new Date();

    private Date lastSent;

}
