package com.staffs.backend.enums.log;

public enum AccessType {

    LOGIN(1), REFRESH_TOKEN(2), LOGOUT(3);

    public final long code;

    AccessType(long code) {
        this.code = code;
    }

}
