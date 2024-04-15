package com.staffs.backend.entity.email;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import jakarta.persistence.*;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class Mail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) //Generates the ID for us
    private Long id;

    private String mailHeader;

    @Column(nullable = false)
    private String mailTo;

    @Lob
    private String mailContent;

    @ElementCollection
    private List<String> copy;

    @ElementCollection
    private Map<String, String> attachments;

    private boolean sent = false;

    @Lob
    private String failureReason;

    private Date createdOn = new Date();

    private Date lastSent;

}
