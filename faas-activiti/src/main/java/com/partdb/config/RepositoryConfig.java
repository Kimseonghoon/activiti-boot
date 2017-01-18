package com.partdb.config;

import java.util.Properties;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.activiti.engine.ProcessEngineConfiguration;
import org.activiti.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.activiti.spring.SpringProcessEngineConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@EnableAutoConfiguration
@EnableJpaRepositories(
        entityManagerFactoryRef = "entityManagerFactory",
        transactionManagerRef = "transactionManager",
        basePackages = {"com.partdb.repository.base"})
public class RepositoryConfig {
	@Autowired
    JpaVendorAdapter jpaVendorAdapter;

    @Autowired
    DataSource dataSource;

    @Bean(name = "entityManager")
    public EntityManager entityManager() {
        return entityManagerFactory().createEntityManager();
    }

    @Primary
    @Bean(name = "entityManagerFactory")
    public EntityManagerFactory entityManagerFactory() {
        LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();
        emf.setDataSource(dataSource);
        emf.setJpaVendorAdapter(jpaVendorAdapter);
        emf.setPackagesToScan("com.partdb.entity.base");
        emf.setPersistenceUnitName("default");   // <- giving 'default' as name
        Properties jpaProperties = new Properties();
        jpaProperties.put("hibernate.hbm2ddl.auto", "update");
        emf.setJpaProperties(jpaProperties);
        emf.afterPropertiesSet();
        return emf.getObject();
    }

    @Bean(name = "transactionManager")
    public PlatformTransactionManager transactionManager() {
        JpaTransactionManager tm = new JpaTransactionManager();
        tm.setEntityManagerFactory(entityManagerFactory());
        return tm;
    }
    
    
    @Bean(name="processEngineConfiguration")
    public ProcessEngineConfigurationImpl processEngineConfiguration() {
    	SpringProcessEngineConfiguration processEngineConfiguration = new SpringProcessEngineConfiguration();
      	processEngineConfiguration.setDataSource(dataSource);
      	processEngineConfiguration.setDatabaseSchemaUpdate(ProcessEngineConfiguration.DB_SCHEMA_UPDATE_TRUE);
      	processEngineConfiguration.setTransactionManager(transactionManager());
      	processEngineConfiguration.setJobExecutorActivate(false);
      	processEngineConfiguration.setAsyncExecutorEnabled(true);
      	processEngineConfiguration.setAsyncExecutorActivate(false);
      	//processEngineConfiguration.setHistoryLevel(HistoryLevel.FULL);
      	//processEngineConfiguration.setHistory(HistoryLevel.FULL.getKey());
      	return processEngineConfiguration;
    }
    
}
