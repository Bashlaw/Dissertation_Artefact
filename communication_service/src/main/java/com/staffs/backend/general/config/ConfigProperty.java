package com.staffs.backend.general.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
public class ConfigProperty {

    @Value("${spring.data.redis.host}")
    private String redisHost;

    @Value("${spring.data.redis.port}")
    private int redisPort;

    @Value("${mail.server.host}")
    private String mailServerHost;

    @Value("${mail.server.port}")
    private int mailServerPort;

    @Value("${mail.server.username}")
    private String mailServerUsername;

    @Value("${mail.server.password}")
    private String mailServerPassword;

    @Value("${mail.server.ssl}")
    private boolean mailServerSsl;

}
