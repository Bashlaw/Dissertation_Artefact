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
@EnableJpaRepositories(entityManagerFactoryRef = "localContainerEntityManagerFactoryBean",
        basePackages = {"com.staffs.backend.repository.log" , "com.staffs.backend.repository.user"
                , "com.staffs.backend.repository.permission" , "com.staffs.backend.repository.role"},
        transactionManagerRef = "accountTransactionManager")
@EnableTransactionManagement
@EntityScan(basePackageClasses = BaseEntity.class)
public class AccountSQLDatasourceConfig {

    @Bean
    @ConfigurationProperties("spring.datasource.account.sql")
    public DataSourceProperties dataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    @ConfigurationProperties("spring.datasource.account.sql.configuration")
    public DataSource dataSource() {
        log.debug("Datasource properties :urlProps ==============================> {}" , dataSourceProperties().getUrl());
        log.debug("Datasource properties :username ==============================> {}" , dataSourceProperties().getUsername());
        log.debug("Datasource properties :password ==============================> {}" , dataSourceProperties().getPassword());
        return dataSourceProperties().initializeDataSourceBuilder()
                .type(HikariDataSource.class)
                .build();
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean localContainerEntityManagerFactoryBean(EntityManagerFactoryBuilder builder ,
                                                                                         @Qualifier("dataSource") DataSource dataSource) {
        HashMap<String, Object> properties = new HashMap<>();
        properties.put("hibernate.physical_naming_strategy" , CamelCaseToUnderscoresNamingStrategy.class);
        properties.put("hibernate.implicit_naming_strategy" , SpringImplicitNamingStrategy.class);
        properties.put("hibernate.show_sql" , true);
        properties.put("generate-ddl" , true);
        properties.put("hibernate.hbm2ddl.auto" , "update");
        properties.put("hibernate.dialect" , "org.hibernate.dialect.PostgreSQL81Dialect");

        return builder.dataSource(dataSource).properties(properties)
                .packages("com.staffs.backend.entity.log" , "com.staffs.backend.entity.user"
                        , "com.staffs.backend.entity.permission" , "com.staffs.backend.entity.role")
                .persistenceUnit("account").build();
    }

    @Bean
    public JdbcTemplate jdbcTemplateAccount(@Qualifier("dataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean
    public PlatformTransactionManager accountTransactionManager(
            @Qualifier("localContainerEntityManagerFactoryBean") EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }

    @Bean
    public HibernateExceptionTranslator hibernateExceptionTranslatorAccount() {
        return new HibernateExceptionTranslator();
    }

}
