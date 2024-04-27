package com.staffs.backend.general.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
public class ConfigProperty {

    @Value("${jwt.secret:secret}")
    private String secret;

}
