package com.jvpars.codetip.config;

import com.zaxxer.hikari.HikariDataSource;
import org.hibernate.boot.model.naming.PhysicalNamingStrategy;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.hibernate.SpringImplicitNamingStrategy;
import org.springframework.boot.orm.jpa.hibernate.SpringPhysicalNamingStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Primary;
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
configure first datasource and transaction
don't touch this if u don't know
*/

@Configuration
@Primary
@EnableJpaRepositories(basePackages="com.jvpars.codetip.repository",
        entityManagerFactoryRef = "firstEntityManagerFactory", transactionManagerRef = "codetipTransaction")
public class ADatasourceConfig {
    private Map<String, Object> jpaProperties;


    public ADatasourceConfig() {
        jpaProperties = new HashMap<>();
        jpaProperties.put("hibernate.implicit_naming_strategy", SpringImplicitNamingStrategy.class.getName());
        jpaProperties.put("hibernate.physical_naming_strategy", SpringPhysicalNamingStrategy.class.getName());
        jpaProperties.put("spring.jpa.hibernate.ddl-auto", "update");
        jpaProperties.put("spring.jpa.open-in-view", Boolean.TRUE);
    }
    //.....................
    @Bean("codetip")
    @Primary
    @ConfigurationProperties("codetip.datasource")
    public DataSourceProperties firstDataSourceProperties() {
        return new DataSourceProperties();
    }

    //..............

    @Bean("codetipDatasource")
    @Primary
    public HikariDataSource dataSource() {
        return firstDataSourceProperties()
                .initializeDataSourceBuilder()
                .type(HikariDataSource.class)
                .build();
    }

    @Bean
    @Primary
    public PhysicalNamingStrategy physicalNamingStrategy()
    {
        return new SpringPhysicalNamingStrategy();
    }

    @Bean(name = "firstEntityManagerFactory")
    @Primary
   public EntityManagerFactory  firstEntityManagerFactory(
            @Qualifier("codetipDatasource") DataSource codetipDatasource) {
        LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
        factory.setDataSource(codetipDatasource);
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        vendorAdapter.setGenerateDdl(Boolean.TRUE);
        vendorAdapter.setDatabase(Database.MYSQL);
        vendorAdapter.setShowSql(Boolean.FALSE);
        vendorAdapter.setDatabasePlatform("org.hibernate.dialect.MySQL5InnoDBDialect");
        factory.setJpaVendorAdapter(vendorAdapter);
        factory.setPackagesToScan("com.jvpars.codetip.domain");
        factory.setPersistenceUnitName("primaryPersistenceUnit");
        factory.setJpaPropertyMap(jpaProperties);
        factory.afterPropertiesSet();
       return factory.getObject();
    }

    @Bean(name = "entityManager")
    @Primary
    @DependsOn({ "codetipTransaction"})
    public EntityManager entityManager(@Qualifier("codetipDatasource") DataSource codetipDatasource) {
          return   firstEntityManagerFactory(codetipDatasource).createEntityManager();
    }

    @Bean(name = "codetipTransaction")
    @Primary
    public PlatformTransactionManager codetipTransaction
    (@Qualifier("firstEntityManagerFactory")  EntityManagerFactory firstEntityManagerFactory) {
        return new JpaTransactionManager(firstEntityManagerFactory);
    }


}