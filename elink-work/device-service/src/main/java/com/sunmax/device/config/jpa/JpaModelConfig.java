package com.sunmax.device.config.jpa;

import com.sunmax.device.constants.DBConstants;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import jakarta.annotation.Resource;
import javax.sql.DataSource;
import java.util.Map;
import java.util.Objects;

@Configuration
@EnableJpaRepositories(
        entityManagerFactoryRef = DBConstants.JPA_MF_MODEL,
        transactionManagerRef = DBConstants.JPA_TX_MODEL,
        basePackages = {"com.sunmax.device.dao.model"}) // 设置接口所在包
public class JpaModelConfig {

    @Resource(name = "hibernateVendorProperties")
    private Map<String, Object> hibernateVendorProperties;

    @Resource(name = "modelDataSource")
    private DataSource dataSource;

    /**
     * 创建 LocalContainerEntityManagerFactoryBean
     */
    @Bean(name = DBConstants.JPA_MF_MODEL)
    @Primary
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(EntityManagerFactoryBuilder builder) {
        return builder
                .dataSource(dataSource) // 数据源
                .properties(hibernateVendorProperties) // 获取并注入 Hibernate Vendor 相关配置
                .packages("com.sunmax.device.entity.model") // 数据库实体 domain 所在包
                .persistenceUnit("modelPersistenceUnit") // 设置持久单元的名字，需要唯一
                .build();
    }

    /**
     * 创建 PlatformTransactionManager
     */
    @Bean(name = DBConstants.JPA_TX_MODEL)
    @Primary
    public PlatformTransactionManager transactionManager(EntityManagerFactoryBuilder builder) {
        return new JpaTransactionManager(Objects.requireNonNull(entityManagerFactory(builder).getObject()));
    }

}
