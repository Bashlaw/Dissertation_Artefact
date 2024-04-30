package com.staffs.backend.flyway;

import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

@Configuration
public class FlywayConfig {

    @Bean
    @Primary
    public Flyway billingflyway(@Qualifier("billingDataSource") DataSource dataSource) {
        return Flyway.configure().dataSource(dataSource).load();
    }

    @Bean
    public Flyway communicationflyway(@Qualifier("CommunicationDataSource") DataSource dataSource) {
        return Flyway.configure().dataSource(dataSource).load();
    }

}
