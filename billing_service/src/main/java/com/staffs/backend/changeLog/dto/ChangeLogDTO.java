package com.staffs.backend.changeLog.dto;

import lombok.Data;

@Data
public class ChangeLogDTO {

    private String logId;

    private String module;

    private Long operatorId;

    private String action;

    private String logDate;

}
