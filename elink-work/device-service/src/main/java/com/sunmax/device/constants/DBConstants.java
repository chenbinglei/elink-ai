package com.sunmax.device.constants;

public class DBConstants {

    /**
     * 工厂 - JPA - 模型库
     */
    public static final String JPA_MF_MODEL = "modelJpaManagerFactory";
    /**
     * 工厂 - JPA - 接入库
     */
    public static final String JPA_MF_ACCESS = "accessJpaManagerFactory";

    /**
     * 工厂 - MYBATIS - 模型库
     */
    public static final String MYBATIS_SF_MODEL = "modelMybatisSqlSessionFactory";
    /**
     * 工厂 - MYBATIS - 接入库
     */
    public static final String MYBATIS_SF_ACCESS = "accessMybatisSqlSessionFactory";

    /**
     * 事务管理器 - JPA - 模型库
     */
    public static final String JPA_TX_MODEL = "modelJpaTransactionManager";
    /**
     * 事务管理器 - JPA - 接入库
     */
    public static final String JPA_TX_ACCESS = "accessJpaTransactionManager";

    /**
     * 事务管理器 - MYBATIS - 模型库
     */
    public static final String MYBATIS_TX_MODEL = "modelMybatisTransactionManager";
    /**
     * 事务管理器 - MYBATIS - 接入库
     */
    public static final String MYBATIS_TX_ACCESS = "accessMybatisTransactionManager";

}
