package com.staffs.backend.paymentIntegration.dto;

import com.google.gson.annotations.SerializedName;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthRequest {

    @SerializedName("username")
    private final String email;

    @SerializedName("secret")
    private final String password;

}
