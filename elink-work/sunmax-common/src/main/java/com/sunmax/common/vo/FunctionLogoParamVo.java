package com.sunmax.common.vo;

/**
 * 功能点标识静态实体类
 */
public class FunctionLogoParamVo {

    /**
     * 电桩总功率
     */
    public static final String PILE_POWER = "pilepower";

    /**
     * 逆变器-今日发电量
     */
    public static final String DAILY_POWER_GENERATION = "daily_power_generation";

    /**
     * 逆变器-昨日发电量
     */
    public static final String ELECTRICITY_GENERATED_YESTERDAY = "electricity_generated_yesterday";

    /**
     * 逆变器-当月发电量
     */
    public static final String MONTHLY_POWER_GENERATION = "monthly_power_generation";

    /**
     * 逆变器-总发电量
     */
    public static final String TOTAL_POWER_GENERATION = "total_power_generation";

    /**
     * 逆变器当前运行状态
     */
    public static final String CURRENT_STATUS_OF_THE_INVERTER = "current_status_of_the_inverter";

    /**
     * 逆变器-有功功率
     */
    public static final String ACTIVE_POWER = "active_power";

    /**
     * 逆变器-无功功率
     */
    public static final String REACTIVE_POWER = "reactive_power_value";

    /**
     * pcs-交流总有功功率
     */
    public static final String PCS_ACTIVE_POWER = "pcs_activepower";

    /**
     * pcs-交流总无功功率
     */
    public static final String PCS_REACTIVE_POWER = "pcs_reactivepower";

    /**
     * pcs-交流总充电量
     */
    public static final String PCS_BATTERY_CHARGE = "total_battery_charge";

    /**
     * pcs-交流总放电量
     */
    public static final String PCS_BATTERY_DISCHARGE = "total_battery_discharge";

    /**
     * pcs-工作状态
     */
    public static final String PCS_OPERATIVE_MODE = "pcs_operative_mode";

    /**
     * 电池簇-运行状态 0-待机 1-禁充 2-禁放 3-故障 4-告警 5-充电 6-放电
     */
    public static final String BATTERY_RUN_STATE = "runstate";

    /**
     * 电池簇-充放电状态
     */
    public static final String CHARGESTATE = "chargestate";

    /**
     * 电池簇-SOC状态
     */
    public static final String BATTERY_TOTAL_SOC = "soc";

    /**
     * 电池簇-SOH
     */
    public static final String BATTERY_TOTAL_SOH = "soh";

    /**
     * 电池簇-电池总电压
     */
    public static final String BATTERY_TOTAL_VOLTAGE = "batterytotalvoltage";

    /**
     * 电池簇-电池总电流
     */
    public static final String BATTERY_TOTAL_CURRENT = "batterycurrent";

    /**
     * 电池簇-单体温度
     */
    public static final String CELL_TEMP = "cell_temp";

    /**
     * 电池簇-单体电压
     */
    public static final String CELL_VOLTAGE = "cell_voltage";

    /**
     * 电池簇-累计充电电量
     */
    public static final String ACCCHARGEQ = "accchargeq";

    /**
     * 电池簇-累计放电电量
     */
    public static final String ACCDISCHARGEQ = "accdischargeq";

    /**
     * 电池簇-可充电量
     */
    public static final String BATTERY_ENABLE_CHARGE_Q = "systemenablecharge";

    /**
     * 电池簇-可放电量
     */
    public static final String BATTERY_ENABLE_DISCHARGE_Q = "systemenabledischarge";

    /**
     * 电池簇-最高单体电压
     */
    public static final String MAX_CELL_VOLTAGE = "maxcellvoltage";

    /**
     * 电池簇-最低单体电压
     */
    public static final String MIN_CELL_VOLTAGE = "mincellvoltage";

    /**
     * 电池簇-最高单体温度
     */
    public static final String MAX_CELL_TEMPERATURE = "maxcelltemperature";

    /**
     * 电池簇-最低单体温度
     */
    public static final String MIN_CELL_TEMPERATURE = "mincelltemperature";

    /**
     * 储能辅助监控-柜内温度
     */
    public static final String CABINET_TEMPERATURE = "cabinettemperature";

    /**
     * 储能辅助监控-柜外温度
     */
    public static final String EXTERNAL_TEMPERATURE = "externaltemperature";

    /**
     * 储能辅助监控-湿度值
     */
    public static final String CABINET_HUMIDITY = "humidity";

    /**
     * 储能辅助监控-制冷状态
     */
    public static final String COOLING_STATE = "coolingstate";

    /**
     * 储能辅助监控-加热器状态
     */
    public static final String HEATING_STATE = "heatingstate";

    /**
     * 储能辅助监控-内风机状态
     */
    public static final String INTERNALFAN_STATE = "internalfanstate";

    /**
     * 储能辅助监控-外风机状态
     */
    public static final String EXTERNALFAN_STATE = "externalfanstate";

    /**
     * 电桩-充电功率
     */
    public static final String PILE_CHARGEPOWER = "pilechargepower";

    /**
     * 电桩-放电功率
     */
    public static final String PILE_DISCHARGEPOWER = "piledischargepower";

    /**
     * 电枪-需求功率
     */
    public static final String GUN_REQ_POWER = "gunrepower";

    /**
     * 电桩-输出功率
     */
    public static final String GUN_OUT_POWER = "gunpower";

    /**
     * 电桩-枪原始状态
     */
    public static final String GUN_ORIGINAL_STATUS = "gunoriginalstatus";

    /**
     * 电桩-枪车辆连接状态
     */
    public static final String VEHICLE_CONN_STATE = "vehicleconnstate";

    /**
     * 电桩-枪输出电压
     */
    public static final String GUN_VOLTAGE = "gunvoltage";

    /**
     * 电桩-枪输出电流
     */
    public static final String GUN_CURRENT = "guncurrent";

    /**
     * 电桩-枪SOC
     */
    public static final String GUN_SOC = "gunsoc";

    /**
     * 光伏气象站-当日总辐照量
     */
    public static final String TOTAL_RADIANT_EXPOSURE = "total_radiant_exposure";

    /**
     * 光伏气象站-温度
     */
    public static final String PV_TEMPERATURE = "temperature";

    /**
     * 光伏气象站-湿度
     */
    public static final String PV_HUMIDITY = "humidity";

    /**
     * 光伏气象站理论发电量
     */
    public static final String PV_THEORY_CAPACITY = "theorycapacity";

    /**
     * 光伏气象站-风速
     */
    public static final String PV_WIND_SPEED = "windspeed";

    /**
     * 光伏气象站-风向
     */
    public static final String PV_WIND_DIRECTION = "winddirection";

    /**
     * 光伏气象站-水平总辐射
     */
    public static final String PV_GHI = "ghi";

    /**
     * 光伏气象站-倾斜总辐射
     */
    public static final String PV_BEVEL_GHI = "bevel_ghi";

    /**
     * 光伏气象站-水平总辐照量
     */
    public static final String PV_TOTAL_RADIANT_EXPOSURE = "total_radiant_exposure";

    /**
     * 光伏气象站-倾斜总辐照量
     */
    public static final String PV_OBLIQUE_IRRADIATION = "oblique_irradiation";

    /**
     * 光伏气象站-背板温度
     */
    public static final String PV_BACK_PLATE_TEMPERATURE = "backplatetemperature";

    /**
     * 电表总有功功率
     */
    public static final String TOTAL_ACTIVE_POWER = "total_active_power";

    /**
     * 电表总无功功率
     */
    public static final String TOTAL_REACTIVE_POWER = "total_reactive_power";

    /**
     * 电能表-正向有功电能总值
     */
    public static final String TOTAL_POSITIVE_ACTIVE_ENERGY = "total_positive_active_energy";

    /**
     * 电能表-反向有功电能总值
     */
    public static final String TOTAL_INVERSE_ACTIVE_ENERGY = "total_inverse_active_energy";

    /**
     * 电能表-正向无功电能总值
     */
    public static final String TOTAL_POSITIVE_REACTIVE_ENERGY = "total_positive_reactive_energy";

    /**
     * 电能表-反向无功电能总值
     */
    public static final String TOTAL_INVERSE_REACTIVE_ENERGY = "total_inverse_reactive_energy";

    /**
     * 电能表-尖时反向有功电能
     */
    public static final String TOP_REV_KWH = "top_rev_kwh";

    /**
     * 电能表-峰时反向有功电能
     */
    public static final String PEAK_REV_KWH = "peak_rev_kwh";

    /**
     * 电能表-平时反向有功电能
     */
    public static final String PLAIN_REV_KWH = "plain_rev_kwh";

    /**
     * 电能表-谷时反向有功电能
     */
    public static final String VALLEY_REV_KWH = "valley_rev_kwh";

    /**
     * 电能表-深谷时反向有功电能
     */
    public static final String DEEP_REV_KWH = "deep_rev_kwh";

    /**
     * 电能表-尖时正向有功电能
     */
    public static final String TOP_SUP_KWH = "top_sup_kwh";

    /**
     * 电能表-峰时正向有功电能
     */
    public static final String PEAK_SUP_KWH = "peak_sup_kwh";

    /**
     * 电能表-平时正向有功电能
     */
    public static final String PLAIN_SUP_KWH = "plain_sup_kwh";

    /**
     * 电能表-谷时正向有功电能
     */
    public static final String VALLEY_SUP_KWH = "valley_sup_kwh";

    /**
     * 电能表-深谷时正向有功电能
     */
    public static final String DEEP_SUP_KWH = "deep_sup_kwh";

    /**
     * 电能表-功率因数
     */
    public static final String POWER_FACTOR = "power_factor";

    /**
     * 电能表-A相功率因数
     */
    public static final String A_PHASE_POWER_FACTOR = "phase_a_power_factor";

    /**
     * 电能表-B相功率因数
     */
    public static final String B_PHASE_POWER_FACTOR = "phase_b_power_factor";

    /**
     * 电能表-C相功率因数
     */
    public static final String C_PHASE_POWER_FACTOR = "phase_c_power_factor";

    /**
     * 电能表-A相电流
     */
    public static final String A_PHASE_CURRENT = "a_phase_current";

    /**
     * 电能表-B相电流
     */
    public static final String B_PHASE_CURRENT = "b_phase_current";

    /**
     * 电能表-C相电流
     */
    public static final String C_PHASE_CURRENT = "c_phase_current";

    /**
     * 电能表-A相电压
     */
    public static final String A_PHASE_VOLTAGE = "a_phase_voltage";

    /**
     * 电能表-B相电压
     */
    public static final String B_PHASE_VOLTAGE = "b_phase_voltage";

    /**
     * 电能表-C相电压
     */
    public static final String C_PHASE_VOLTAGE = "c_phase_voltage";

    /**
     * 电能表-A相有功功率
     */
    public static final String A_PHASE_ACTIVE_POWER = "phase_a_is_active";

    /**
     * 电能表-B相有功功率
     */
    public static final String B_PHASE_ACTIVE_POWER = "phase_b_is_active";

    /**
     * 电能表-C相有功功率
     */
    public static final String C_PHASE_ACTIVE_POWER = "phase_c_is_active";

    /**
     * 电能表-A相无功功率
     */
    public static final String A_PHASE_REACTIVE_POWER = "phase_a_reactive_work";

    /**
     * 电能表-B相无功功率
     */
    public static final String B_PHASE_REACTIVE_POWER = "b_phase_reactive_power";

    /**
     * 电能表-C相无功功率
     */
    public static final String C_PHASE_REACTIVE_POWER = "c_phase_reactive_power";

    /**
     * 换电-换电仓运行状态 0-待机 1-充电 2-停机
     */
    public static final String BATTERY_GRANARY_STATUS = "running_status";

    /**
     * 换电-换电仓输出功率
     */
    public static final String BATTERY_GRANARY_OUT_POWER = "output_power";

    /**
     * 换电-换电仓正向有功总电量
     */
    public static final String BATTERY_GRANARY_SUP_CHARGE = "total_forward_active_power";

    /**
     * 换电-电池包当前剩余电量
     */
    public static final String BATTERY_PACK_REMAINING_POWER = "remaining_power";

    /**
     * 换电仓-电池包编号
     */
    public static final String BATTERY_PACK_NUMBER = "pack_number";

    /**
     * 换电-电池包工作模式 1-静置 2-充电 3-放电
     */
    public static final String BATTERY_PACK_MODE = "battery_working_mode";

    /**
     * 换电-电池包SOC
     */
    public static final String BATTERY_PACK_SOC = "battery_soc";

    /**
     * 换电-电池包SOH
     */
    public static final String BATTERY_PACK_SOH = "battery_soh";

    /**
     * 传感器-最高温度
     */
    public static final String SENSOR_MAX_TEMP = "maxtemp";

    /**
     * 传感器-平均温度
     */
    public static final String SENSOR_AVG_TEMP = "avgtemp";

}
