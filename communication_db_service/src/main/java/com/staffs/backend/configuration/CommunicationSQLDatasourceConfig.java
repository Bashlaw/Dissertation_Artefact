package com.staffs.backend.configuration;

import com.staffs.backend.utils.BaseEntity;
import com.zaxxer.hikari.HikariDataSource;
import jakarta.persistence.EntityManagerFactory;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.boot.model.naming.CamelCaseToUnderscoresNamingStrategy;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.boot.orm.jpa.hibernate.SpringImplicitNamingStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.hibernate5.HibernateExceptionTranslator;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.HashMap;

@Slf4j
@Configuration
@EnableJpaRepositories(entityManagerFactoryRef = "communicationLocalContainerEntityManagerFactoryBean",
        basePackages = {"com.staffs.backend.repository.email" , "com.staffs.backend.repository.sms"
                , "com.staffs.backend.repository.notification"},
        transactionManagerRef = "communicationTransactionManager")
@EnableTransactionManagement
@EntityScan(basePackageClasses = BaseEntity.class)
public class CommunicationSQLDatasourceConfig {

    @Bean
    @ConfigurationProperties("spring.datasource.communication.sql")
    public DataSourceProperties communicationDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    @ConfigurationProperties("spring.datasource.communication.sql.configuration")
    public DataSource CommunicationDataSource() {
        log.debug("Datasource properties :urlProps ==============================> {}" , communicationDataSourceProperties().getUrl());
        log.debug("Datasource properties :username ==============================> {}" , communicationDataSourceProperties().getUsername());
        log.debug("Datasource properties :password ==============================> {}" , communicationDataSourceProperties().getPassword());
        return communicationDataSourceProperties().initializeDataSourceBuilder()
                .type(HikariDataSource.class)
                .build();
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean communicationLocalContainerEntityManagerFactoryBean(EntityManagerFactoryBuilder builder ,
                                                                                                      @Qualifier("CommunicationDataSource") DataSource dataSource) {
        HashMap<String, Object> properties = new HashMap<>();
        properties.put("hibernate.physical_naming_strategy" , CamelCaseToUnderscoresNamingStrategy.class);
        properties.put("hibernate.implicit_naming_strategy" , SpringImplicitNamingStrategy.class);

        return builder.dataSource(dataSource).properties(properties)
                .packages("com.staffs.backend.entity.email" , "com.staffs.backend.entity.sms"
                        , "com.staffs.backend.entity.notification")
                .persistenceUnit("communication").build();
    }

    @Bean
    public JdbcTemplate jdbcTemplateCommunication(@Qualifier("CommunicationDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean
    public PlatformTransactionManager communicationTransactionManager(
            @Qualifier("communicationLocalContainerEntityManagerFactoryBean") EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }

    @Bean
    public HibernateExceptionTranslator hibernateExceptionTranslatorCommunication() {
        return new HibernateExceptionTranslator();
    }

}
