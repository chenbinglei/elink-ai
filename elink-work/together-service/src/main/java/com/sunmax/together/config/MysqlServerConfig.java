package com.sunmax.together.config;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import javax.annotation.Resource;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.Map;

/**
 * @author tanglin
 * @date 2020/8/18 14:08
 */
@Configuration
@MapperScan(basePackages = {"com.sunmax.together.mapper"}, sqlSessionTemplateRef  = "mysqlSqlSessionTemplate")
@EnableJpaRepositories(entityManagerFactoryRef = "jpaManagerFactory", basePackages = "com.sunmax.together.dao") // 设置接口所在包
public class MysqlServerConfig {

    @Bean(name = "mysqlDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.mysql-server")
    @Primary
    public DataSource mysqlDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "mysqlSqlSessionFactory")
    @Primary
    public SqlSessionFactory mysqlSqlSessionFactory() throws Exception {
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(this.mysqlDataSource());
        // ⭐ 关键：设置 MyBatis 全局配置
//        org.apache.ibatis.session.Configuration configuration = new org.apache.ibatis.session.Configuration();
//        configuration.setLogImpl(org.apache.ibatis.logging.stdout.StdOutImpl.class); // 开启控制台日志
//        configuration.setMapUnderscoreToCamelCase(true); // 可选：开启驼峰映射
//        bean.setConfiguration(configuration);

        bean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath*:mapper/**/*.xml"));
        return bean.getObject();
    }

    @Bean(name = "mysqlTransactionManager")
    @Primary
    public DataSourceTransactionManager mysqlTransactionManager() {
        return new DataSourceTransactionManager(this.mysqlDataSource());
    }

    @Bean(name = "mysqlSqlSessionTemplate")
    @Primary
    public SqlSessionTemplate mysqlSqlSessionTemplate(SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }

    @Resource
    private Map<String, Object> hibernateVendorProperties;

    @Bean(name = "jpaManagerFactory")
    @Primary
    public LocalContainerEntityManagerFactoryBean jpaManagerFactory(EntityManagerFactoryBuilder builder) {
        return builder.dataSource(this.mysqlDataSource()) // 数据源
                .properties(hibernateVendorProperties) // 获取并注入 Hibernate Vendor 相关配置
                .packages("com.sunmax.together.entity") // 数据库实体 domain 所在包
                .persistenceUnit("jpaPersistenceUnit") // 设置持久单元的名字，需要唯一
                .build();
    }

    @Bean(name = "transactionManager")
    @Primary
    public PlatformTransactionManager jpaTransactionManager(@Qualifier("jpaManagerFactory") EntityManagerFactory jpaManagerFactory) {
        return new JpaTransactionManager(jpaManagerFactory);
    }

}
