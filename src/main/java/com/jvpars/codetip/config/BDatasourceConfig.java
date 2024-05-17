package com.jvpars.codetip.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.hibernate.SpringImplicitNamingStrategy;
import org.springframework.boot.orm.jpa.hibernate.SpringPhysicalNamingStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/*
configure second datasource and transaction
don't touch this if u don't know
*/


@Configuration
@EnableJpaRepositories
        (entityManagerFactoryRef = "secondEntityManagerFactory",
                transactionManagerRef = "h2Transaction",
                basePackages = {"com.jvpars.codetip.cache"})


public class BDatasourceConfig {
    private Map<String, Object> jpaProperties;

    public BDatasourceConfig() {
        jpaProperties = new HashMap<>();
/*        jpaProperties.put("hibernate.implicit_naming_strategy", SpringImplicitNamingStrategy.class.getName());
        jpaProperties.put("hibernate.physical_naming_strategy", SpringPhysicalNamingStrategy.class.getName());*/
        jpaProperties.put("spring.jpa.hibernate.ddl-auto", "update");
        jpaProperties.put("spring.jpa.open-in-view", Boolean.TRUE);
    }
    //...................
    @Bean("cachemem")
    @ConfigurationProperties(prefix = "cachemem.datasource")
    public DataSourceProperties secondDataSourceProperties() {
        return new DataSourceProperties();
    }
    //...................
    @Bean("h2Datasource")
    public HikariDataSource identityDatasource() {
        return secondDataSourceProperties()
                .initializeDataSourceBuilder()
                .type(HikariDataSource.class)
             //   .driverClassName(org.h2.Driver.class.getName())
                .build();
    }
    //...................

    @Bean(name = "secondEntityManagerFactory")
    public EntityManagerFactory  secondEntityManagerFactory(
        @Qualifier("h2Datasource") DataSource identityDatasource ) {
        LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
        factory.setDataSource(identityDatasource);
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        vendorAdapter.setGenerateDdl(Boolean.TRUE);
        vendorAdapter.setDatabase(Database.H2);
        vendorAdapter.setShowSql(Boolean.FALSE);
        vendorAdapter.setDatabasePlatform(org.hibernate.dialect.H2Dialect.class.getName());
        factory.setJpaVendorAdapter(vendorAdapter);
        factory.setPackagesToScan("com.jvpars.codetip.cache");
        factory.setPersistenceUnitName("secondaryPersistenceUnit");
        factory.setJpaPropertyMap(jpaProperties);
        factory.afterPropertiesSet();
        return factory.getObject();
    }

    @Bean(name = "h2EntityManager")
    @DependsOn({ "h2Transaction"})
    public EntityManager entityManager(@Qualifier("h2Datasource") DataSource h2Datasource) {
        return   secondEntityManagerFactory(h2Datasource).createEntityManager();
    }
    //.....................

    @Bean(name = "h2Transaction")
    public PlatformTransactionManager identityTransaction(@Qualifier("secondEntityManagerFactory") EntityManagerFactory secondEntityManagerFactory) {
        return new JpaTransactionManager(secondEntityManagerFactory);
    }

}