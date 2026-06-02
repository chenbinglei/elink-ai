package com.sunmax.log.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import javax.annotation.Resource;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.Map;

@Configuration
@EnableJpaRepositories(entityManagerFactoryRef = "logJpaManagerFactory", transactionManagerRef = "logJpaTransactionManager", basePackages = {"com.sunmax.log.dao"})
// 设置接口所在包
public class JpaLogConfig {

    @Resource(name = "hibernateVendorProperties")
    private Map<String, Object> hibernateVendorProperties;

    @Resource(name = "logDataSource")
    private DataSource dataSource;

    /**
     * 创建 LocalContainerEntityManagerFactoryBean
     */
    @Bean(name = "logJpaManagerFactory")
    public LocalContainerEntityManagerFactoryBean logJpaManagerFactory(EntityManagerFactoryBuilder builder) {
        return builder
                .dataSource(dataSource) // 数据源
                .properties(hibernateVendorProperties) // 获取并注入 Hibernate Vendor 相关配置
                .packages("com.sunmax.log.entity") // 数据库实体 domain 所在包
                .persistenceUnit("logPersistenceUnit") // 设置持久单元的名字，需要唯一
                .build();
    }

    /**
     * 创建 PlatformTransactionManager
     */
    @Bean(name = "logJpaTransactionManager")
    public PlatformTransactionManager logJpaTransactionManager(@Qualifier("logJpaManagerFactory") EntityManagerFactory logJpaManagerFactory) {
        return new JpaTransactionManager(logJpaManagerFactory);
    }

}
