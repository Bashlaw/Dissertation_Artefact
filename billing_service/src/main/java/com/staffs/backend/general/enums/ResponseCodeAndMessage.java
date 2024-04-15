package com.staffs.backend.general.enums;

import org.springframework.http.HttpStatus;

public enum ResponseCodeAndMessage {

    SUCCESSFUL(HttpStatus.OK.value(), HttpStatus.OK.name()),
    ERROR_PROCESSING(HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.INTERNAL_SERVER_ERROR.name()),
    AUTHENTICATION_ERROR(HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED.name()),
    BAD_REQUEST(HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.name()),
    ALREADY_EXIST(HttpStatus.CONFLICT.value(), HttpStatus.CONFLICT.name()),
    RECORD_NOT_FOUND(HttpStatus.NOT_FOUND.value(), HttpStatus.NOT_FOUND.name()),
    TIMEOUT_ERROR(HttpStatus.GATEWAY_TIMEOUT.value(), HttpStatus.GATEWAY_TIMEOUT.name()),
    CLIENT_NOT_ALLOWED(HttpStatus.CONFLICT.value(), HttpStatus.CONFLICT.name()),
    ALREADY_DISABLED(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.name()),
    CHANGE_USER_PASSWORD(HttpStatus.IM_USED.value(), HttpStatus.RESET_CONTENT.name());

    public final int responseCode;
    public final String responseMessage;

    ResponseCodeAndMessage(int responseCode, String responseMessage) {
        this.responseCode = responseCode;
        this.responseMessage = responseMessage;
    }
}
