package com.sunmax.device.config.mybatis;

import com.sunmax.device.constants.DBConstants;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.annotation.Resource;
import javax.sql.DataSource;


/**
 * @author tanglin
 * @date 2020/8/18 14:08
 */

@Configuration
@MapperScan(basePackages = {"com.sunmax.device.mapper.model"}, sqlSessionTemplateRef  = "modelSqlSessionTemplate")
public class MybatisModelConfig {

    @Resource(name = "modelDataSource")
    private DataSource dataSource;

    @Bean(name = DBConstants.MYBATIS_SF_MODEL)
    @Primary
    public SqlSessionFactory mysqlSqlSessionFactory() throws Exception {
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(dataSource);
        bean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath:mapper/model/*.xml"));
        return bean.getObject();
    }

    @Bean(name = DBConstants.MYBATIS_TX_MODEL)
    @Primary
    public DataSourceTransactionManager mysqlTransactionManager() {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean(name = "modelSqlSessionTemplate")
    @Primary
    public SqlSessionTemplate mysqlSqlSessionTemplate(@Qualifier(DBConstants.MYBATIS_SF_MODEL) SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }

}
