package com.sunmax.device.config;

import org.springframework.boot.autoconfigure.orm.jpa.HibernateProperties;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateSettings;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import jakarta.annotation.Resource;
import javax.sql.DataSource;
import java.util.Map;

@Configuration
public class DataSourceConfig {

    @Resource
    private JpaProperties jpaProperties;

    @Resource
    private HibernateProperties hibernateProperties;

    /**
     * 获取 Hibernate Vendor 相关配置
     */
    @Bean(name = "hibernateVendorProperties")
    public Map<String, Object> hibernateVendorProperties() {
        return hibernateProperties.determineHibernateProperties(jpaProperties.getProperties(), new HibernateSettings());
    }

    /**
     * 创建 model 数据源
     */
    @Bean(name = "modelDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.model")
    @Primary // 需要特殊添加，否则初始化会有问题
    public DataSource modelDataSource() {
        return DataSourceBuilder.create().build();
    }

    /**
     * 创建 access 数据源
     */
    @Bean(name = "accessDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.access")
//    @Primary // 需要特殊添加，否则初始化会有问题
    public DataSource accessDataSource() {
        return DataSourceBuilder.create().build();
    }

}


