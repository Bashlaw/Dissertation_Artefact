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
import org.springframework.context.annotation.Primary;
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
@EnableJpaRepositories(entityManagerFactoryRef = "billingLocalContainerEntityManagerFactoryBean",
        basePackages = {"com.staffs.backend.repository.billLog" , "com.staffs.backend.repository.transactionLog"
                , "com.staffs.backend.repository.packages" , "com.staffs.backend.repository.item"
                , "com.staffs.backend.repository.billingSetup" , "com.staffs.backend.repository.billingMethod"
                , "com.staffs.backend.repository.client" , "com.staffs.backend.repository.country"
                , "com.staffs.backend.repository.coupon" , "com.staffs.backend.repository.packageType"
                , "com.staffs.backend.repository.licenseType" , "com.staffs.backend.repository.licenseUpgrade"
                , "com.staffs.backend.repository.paymentSource" , "com.staffs.backend.repository.paymentIntegration"
                , "com.staffs.backend.repository.log" , "com.staffs.backend.repository.packageRate"
                , "com.staffs.backend.repository.regionRate"},
        transactionManagerRef = "billingTransactionManager")
@EnableTransactionManagement
@EntityScan(basePackageClasses = BaseEntity.class)
public class BillingSQLDatasourceConfig {

    @Bean
    @Primary
    @ConfigurationProperties("spring.datasource.billing.sql")
    public DataSourceProperties billingDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    @Primary
    @ConfigurationProperties("spring.datasource.billing.sql.configuration")
    public DataSource billingDataSource() {
        log.debug("Datasource properties :urlProps ==============================> {}" , billingDataSourceProperties().getUrl());
        log.debug("Datasource properties :username ==============================> {}" , billingDataSourceProperties().getUsername());
        log.debug("Datasource properties :password ==============================> {}" , billingDataSourceProperties().getPassword());
        return billingDataSourceProperties().initializeDataSourceBuilder()
                .type(HikariDataSource.class)
                .build();
    }

    @Bean
    @Primary
    public LocalContainerEntityManagerFactoryBean billingLocalContainerEntityManagerFactoryBean(EntityManagerFactoryBuilder builder ,
                                                                                                @Qualifier("billingDataSource") DataSource dataSource) {
        HashMap<String, Object> properties = new HashMap<>();
        properties.put("hibernate.physical_naming_strategy" , CamelCaseToUnderscoresNamingStrategy.class);
        properties.put("hibernate.implicit_naming_strategy" , SpringImplicitNamingStrategy.class);

        return builder.dataSource(dataSource).properties(properties)
                .packages("com.staffs.backend.entity.billLog" , "com.staffs.backend.entity.transactionLog"
                        , "com.staffs.backend.entity.packages" , "com.staffs.backend.entity.item"
                        , "com.staffs.backend.entity.billingSetup" , "com.staffs.backend.entity.billingMethod"
                        , "com.staffs.backend.entity.client" , "com.staffs.backend.entity.country"
                        , "com.staffs.backend.entity.coupon" , "com.staffs.backend.entity.packageType"
                        , "com.staffs.backend.entity.licenseType" , "com.staffs.backend.entity.licenseUpgrade"
                        , "com.staffs.backend.entity.paymentSource" , "com.staffs.backend.entity.paymentIntegration"
                        , "com.staffs.backend.entity.log" , "com.staffs.backend.entity.packageRate"
                        , "com.staffs.backend.entity.regionRate")
                .persistenceUnit("billing").build();
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
    @Primary
    public JdbcTemplate jdbcTemplateBilling(@Qualifier("billingDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean
    public JdbcTemplate jdbcTemplateAccount(@Qualifier("accountDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean
    @Primary
    public PlatformTransactionManager billingTransactionManager(
            @Qualifier("billingLocalContainerEntityManagerFactoryBean") EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }

    @Bean
    public PlatformTransactionManager accountTransactionManager(
            @Qualifier("accountLocalContainerEntityManagerFactoryBean") EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }

    @Bean
    @Primary
    public HibernateExceptionTranslator hibernateExceptionTranslatorBilling() {
        return new HibernateExceptionTranslator();
    }

    @Bean
    public HibernateExceptionTranslator hibernateExceptionTranslatorAccount() {
        return new HibernateExceptionTranslator();
    }

}
