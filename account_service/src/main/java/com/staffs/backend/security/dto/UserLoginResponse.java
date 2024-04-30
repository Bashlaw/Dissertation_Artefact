package com.staffs.backend.security.dto;

import java.io.Serial;
import java.io.Serializable;

public class UserLoginResponse implements Serializable {

    @Serial
    private static final long serialVersionUID = -8091879091924046844L;

    private final String jwtToken;

    public UserLoginResponse(String jwtToken) {
        this.jwtToken = jwtToken;
    }

    public String getToken() {
        return this.jwtToken;
    }

}