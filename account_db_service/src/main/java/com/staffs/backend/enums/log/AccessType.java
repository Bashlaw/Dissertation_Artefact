package com.staffs.backend.enums.log;

public enum AccessType {

    A001("login"), A002("refresh"), A099("logout");

    public final String description;

    AccessType(String description) {
        this.description = description;
    }

}
