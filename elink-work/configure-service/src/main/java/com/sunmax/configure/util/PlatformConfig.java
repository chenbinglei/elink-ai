package com.sunmax.configure.util;

import org.springframework.context.annotation.Configuration;

/**
 * 运营商常量类
 */
@Configuration
public class PlatformConfig {

    /**
     * 平台ID
     */
    public static String PLATFORM_ID;

    /**
     * 平台运营商密钥
     */
    public static String PLATFORM_SECRET;

    /**
     * 消息密钥
     */
    public static String DATA_SECRET;

    /**
     * 消息密钥初始化向量
     */
    public static String DATA_SECRET_IV;

    /**
     * 签名密钥
     */
    public static String SIG_SECRET;

}
