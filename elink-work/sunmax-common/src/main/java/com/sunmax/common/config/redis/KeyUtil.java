package com.sunmax.common.config.redis;

public class KeyUtil {

    /**
     * 锁的key
     */
    public static final String LOCK_KEY = "lock";

    /**
     * 锁的过期时间(秒)
     */
    public static final long EXPIRE_TIME = 5;

    /**
     * 等待获取锁的超时时间(秒)
     */
    public static final long WAIT_TIME = 3;

    /**
     * 模型事件key
     */
    public static final String MODEL_EVENT_KEY = "modelEvent";

    /**
     * 设备key
     */
    public static final String DEVICE_KEY = "device";

    /**
     * 通道相关的 key标识
     */
    public static final String MQTT = "mqtt";
    public static final String HTTP = "http";
    public static final String TCP = "tcp";

    /**
     * 点表 key
     */
    public static final String POINT_KEY = "point";

    /**
     * 通用网关key前缀
     */
    public static final String GENERAL_GW_PREFIX = "generalGateway";

    /**
     * 通用电桩Key前缀
     */
    public static final String GENERAL_PILE_PREFIX = "generalPile";

    /**
     * 通用锁的key
     */
    public static final String GENERAL_LOCK_KEY = "generalLock";

}
