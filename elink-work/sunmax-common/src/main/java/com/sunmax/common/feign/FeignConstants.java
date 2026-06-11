package com.sunmax.common.feign;

/**
 * Feign客户端统一常量定义
 * 集中管理所有服务的Nacos服务名和上下文路径
 */
public final class FeignConstants {

    private FeignConstants() {}

    // ===== 服务Nacos名称 =====
    public static final String AUTH_SERVICE = "sauth-service";
    public static final String SYSTEM_SERVICE = "system-service";
    public static final String DEVICE_SERVICE = "device-service";
    public static final String DATA_SERVICE = "sunos-data-service";
    public static final String PROTOCOL_SERVICE = "sunos-protocol-service";
    public static final String CRONTAB_SERVICE = "crontab-service";
    public static final String CONFIGURE_SERVICE = "configure-service";
    public static final String TOGETHER_SERVICE = "together-service";
    public static final String WEBAPP_SERVICE = "swebapp-service";
    public static final String GATEWAY_SERVICE = "sunmax-gateway";

    // ===== 服务上下文路径 =====
    public static final String AUTH_CONTEXT = "/sauth";
    public static final String SYSTEM_CONTEXT = "/system";
    public static final String DEVICE_CONTEXT = "/device";
    public static final String DATA_CONTEXT = "/data";
    public static final String PROTOCOL_CONTEXT = "/protocol";
    public static final String CRONTAB_CONTEXT = "/crontab";
    public static final String CONFIGURE_CONTEXT = "/configure";
    public static final String TOGETHER_CONTEXT = "/together";
    public static final String WEBAPP_CONTEXT = "/swebapp";
}
