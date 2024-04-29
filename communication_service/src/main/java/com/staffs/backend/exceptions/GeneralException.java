package com.staffs.backend.exceptions;

import com.staffs.backend.general.enums.ResponseCodeAndMessage;

public class GeneralException extends RuntimeException {

    public GeneralException(int responseCode, String responseMessage) {
        super(String.valueOf(responseCode), new Throwable(responseMessage));
    }

    public GeneralException(ResponseCodeAndMessage codeAndMessage) {
        super(String.valueOf(codeAndMessage.responseCode), new Throwable(codeAndMessage.responseMessage));
    }

}
