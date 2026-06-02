package com.sunmax.configure.util.storage;

public class StorageConst {

    /**
     * form数据key
     */
    public static String DATA = "data";

    /**
     * 请求token
     */
    public static String AUTHORIZATION = "Authorization";

    /**
     * 应用Id
     */
    public static String X_CA_APPID = "x-ca-appId";

    /**
     * 加密key
     */
    public static String X_CA_KEY = "x-ca-key";

    /**
     * 请求唯一标识，推荐使用UUID生成唯一标识。结合时间戳防重放。
     */
    public static String X_CA_NONCE = "x-ca-nonce";

    /**
     * 请求的时间戳，值为当前时间的毫秒数，也就是从1970年1月1日起至今的时间转换为毫秒，时间戳有效时间为5分钟
     */
    public static String X_CA_TIMESTAMP = "x-ca-timestamp";

    /**
     * 签名字符串
     */
    public static String X_CA_SIGN = "x-ca-sign";

    /**
     * 验签分割符
     */
    public static String SIGN_SEGMENT = "//";

    /**
     * 成功标识
     */
    public static int SUCCESS_CODE = 0;

    /**
     * 成功标识
     */
    public static int TOKEN_EXPIRE = 4002;

    /**
     * TOKEN接口名称
     */
    public static String TOKEN_API = "token";

    /**
     * 实时数据接口名称
     */
    public static String STATION_REALTIME_API = "station-realtime";

    /**
     * 统计数据接口名称
     */
    public static String STATION_ARCHIVE_API = "station-archive";

    /**
     * 状态数据接口名称
     */
    public static String STATION_STATUS_API = "station-status";

    /**
     * 故障数据接口名称
     */
    public static String STATION_ALARM_API = "station-alarm";

}
