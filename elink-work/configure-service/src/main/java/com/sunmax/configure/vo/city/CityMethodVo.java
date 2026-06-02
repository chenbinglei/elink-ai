package com.sunmax.configure.vo.city;

/**
 * 市区接口方法定义
 */
public class CityMethodVo {

    /**
     * 获取token
     */
    public static final String QUERY_TOKEN = "query_token";

    /**
     * 查询充电站信息
     */
    public static final String QUERY_STATIONS_INFO = "query_stations_info";

    /**
     * 设备状态变化推送
     */
    public static final String NOTIFICATION_STATION_STATUS = "notification_stationStatus";

    /**
     * 设备接口状态查询
     */
    public static final String QUERY_STATION_STATUS = "query_station_status";

    /**
     * 推送充电状态
     */
    public static final String NOTIFICATION_EQUIP_CHARGE_STATUS = "notification_equip_charge_status";

    /**
     * 查询充电状态
     */
    public static final String QUERY_EQUIP_CHARGE_STATUS = "query_equip_charge_status";

    /**
     * 查询统计信息
     */
    public static final String QUERY_STATION_STATS = "query_station_stats";

    /**
     * 推送充电订单
     */
    public static final String NOTIFICATION_CHARGE_ORDER_INFO = "notification_charge_order_info";

    /**
     * 查询功率控制站桩信息（可选）
     */
    public static final String QUERY_STATION_FHJH = "query_station_fhjh";

    /**
     * 查询直流桩负荷动态信息（可选）
     */
    public static final String QUERY_DYNAMIC_DATA_FHJH = "query_dynamic_data_fhjh";

    /**
     * 功率控制指令推送（可选）
     */
    public static final String NOTIFICATION_DISPATCH_INFO = "notification_Dispatch_Info";

}
