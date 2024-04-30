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
@EnableJpaRepositories(entityManagerFactoryRef = "accountLocalContainerEntityManagerFactoryBean",
        basePackages = {"com.staffs.backend.repository.log" , "com.staffs.backend.repository.user"
                , "com.staffs.backend.repository.permission" , "com.staffs.backend.repository.role"
                , "com.staffs.backend.repository.otp"},
        transactionManagerRef = "accountTransactionManager")
@EnableTransactionManagement
@EntityScan(basePackageClasses = BaseEntity.class)
public class AccountSQLDatasourceConfig {

    @Bean
    @ConfigurationProperties("spring.datasource.account.sql")
    public DataSourceProperties accountDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    @ConfigurationProperties("spring.datasource.account.sql.configuration")
    public DataSource accountDataSource() {
        log.debug("Datasource properties :urlProps ==============================> {}" , accountDataSourceProperties().getUrl());
        log.debug("Datasource properties :username ==============================> {}" , accountDataSourceProperties().getUsername());
        log.debug("Datasource properties :password ==============================> {}" , accountDataSourceProperties().getPassword());
        return accountDataSourceProperties().initializeDataSourceBuilder()
                .type(HikariDataSource.class)
                .build();
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean accountLocalContainerEntityManagerFactoryBean(EntityManagerFactoryBuilder builder ,
                                                                                                @Qualifier("accountDataSource") DataSource dataSource) {
        HashMap<String, Object> properties = new HashMap<>();
        properties.put("hibernate.physical_naming_strategy" , CamelCaseToUnderscoresNamingStrategy.class);
        properties.put("hibernate.implicit_naming_strategy" , SpringImplicitNamingStrategy.class);

        return builder.dataSource(dataSource).properties(properties)
                .packages("com.staffs.backend.entity.log" , "com.staffs.backend.entity.user"
                        , "com.staffs.backend.entity.permission" , "com.staffs.backend.entity.role"
                        , "com.staffs.backend.entity.otp")
                .persistenceUnit("account").build();
    }

    @Bean
    public JdbcTemplate jdbcTemplateAccount(@Qualifier("accountDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean
    public PlatformTransactionManager accountTransactionManager(
            @Qualifier("accountLocalContainerEntityManagerFactoryBean") EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }

    @Bean
    public HibernateExceptionTranslator hibernateExceptionTranslatorAccount() {
        return new HibernateExceptionTranslator();
    }

}
