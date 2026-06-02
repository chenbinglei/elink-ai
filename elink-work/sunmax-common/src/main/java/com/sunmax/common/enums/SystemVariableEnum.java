package com.sunmax.common.enums;

import lombok.Getter;

/**
 * @Author: yqz
 * @Date: 2023/7/414:35
 * @version: 1.0
 * @注释: 系统变量枚举
 */
@Getter
public enum SystemVariableEnum {

    GUNOUTPUTCURRENT("gunoutputcurrent", "电枪输出电流"),
    GUNOUTPUTVOLTAGE("gunoutputvoltage", "电枪输出电压"),
    GUNPOWER("gunpower", "电枪输出功率"),
    PILE_CHARGINGPOWER("Pile_ChargingPower", "充电站_整站_充电功率"),
    PILE_DISCHARGINGPOWER("Pile_DisChargingPower", "充电站_整站_V2G功率(放电功率)"),
    CHARGINGPOWER("ChargingPower", "储能_整站_充电功率"),
    DISCHARGINGPOWER("DisChargingPower", "储能_整站_放电功率"),
    CHARGINGENERGY("ChargingEnergy", "储能_整站_充电电量"),
    DISCHARGINGENERGY("DisChargingEnergy", "储能_整站_放电电量"),
    CN_SUM_CHARGE_QT("cn_sum_charge_qt", "储能_整站_累计充电电量"),
    CN_SUM_DISCHARGE_QT("cn_sum_discharge_qt", "储能_整站_累计放电电量"),
    CN_LASTDAY_EFF("cn_lastday_eff", "储能_整站_昨日系统效率"),
    SYSTEMEFFICIENCY("SystemEfficiency", "储能_整站_充放电循环效率"),
    BATT_DAY_CHARGE_KWH("batt_day_charge_kwh", "储能_设备_电池簇日充电量"),
    BATT_DAY_DISCHARGE_KWH("batt_day_discharge_kwh", "储能_设备_电池簇日放电量"),
    PCS_DAY_CHARGE_KWH("pcs_day_charge_kwh", "储能_设备_PCS日充电量"),
    PCS_DAY_DISCHARGE_KWH("pcs_day_discharge_kwh", "储能_设备_PCS日放电量"),
    CN_ACTIVE_POWER("cn_active_power", "储能_整站_有功功率"),
    ONGRIDENERGY("OnGridEnergy", "光伏_整站_上网电量"),
    CONSUMEENERGY("ConsumeEnergy", "光伏_整站_消纳电量"),
    TOTAL_KW("pv_total_kwh", "光伏_整站_总发电量"),
    DAY_KW("pv_day_kwh", "光伏_整站_今日发电量"),
    LASTDAY_KW("pv_lastday_kwh", "光伏_整站_昨日发电量"),
    PV_LASTDAY_EFF("pv_lastday_eff", "光伏_整站_昨日发电效率"),
    LASTDAY_GC_HOUR("pv_lastday_gc_hour", "光伏_整站_昨日等效发电小时数"),
    PV_THEORY_POWER("pv_theory_kw", "光伏_整站_理论发电功率"),
    PV_SHORTTERM_FORECAST_KW("pv_shortterm_forecast_kw", "光伏_整站_短期预测功率"),
    PV_SHORTTERM_FORECAST_KWH("pv_shortterm_forecast_kwh", "光伏_整站_短期预测发电量"),
    PV_KWH("pv_kwh", "光伏_整站_实际发电量"),
    PV_THEORY_KWH("pv_theory_kwh", "光伏_整站_理论发电量"),
    PV_KW("pv_kw", "光伏_整站_实际功率");


    private final String code;
    private final String name;

    SystemVariableEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public static SystemVariableEnum getName(String code) {
        for (SystemVariableEnum value : values()) {
            if(value.getCode() == code) {
                return value;
            }
        }
        throw new UnsupportedOperationException("不支持的操作类型");
    }

    public static SystemVariableEnum getCode(String name) {
        for (SystemVariableEnum value : values()) {
            if(value.getName().equals(name)) {
                return value;
            }
        }
        throw new UnsupportedOperationException("不支持的操作类型");
    }

}
