package com.sunmax.common.enums;

import lombok.Getter;

@Getter
public enum GeneralFieldEnum {

    WORK_STATUS("workStatus", "充电桩工作状态", 1),
    ORIGINAL_STATUS("originalStatus", "充电桩原始状态", 1),
    RESET_TIMES("resetTimes", "充电桩复位次数", 1),
    TOTAL_POWER("totalPower", "充电桩总功率(kW)", 1),
    REC_CHARGE_POWER("recChargePower", "充电桩充电功率(kW)", 1),
    DIS_CHARGE_POWER("disChargePower", "充电桩放电功率(kW)", 1),
    INNER_TEMP("innerTemp", "充电桩内部温度", 1),
    POWER_MOD_MAX_TEMP("powerModMaxTemp", "充电桩电力模块最高温度", 1),
    MAX_TEMP_MOD("maxTempMod", "充电桩电力模块最高温度序号", 1),
    GUN_STATUS("gunStatus", "充电枪工作状态", 2),
    GUN_ORIGINAL_STATUS("gunOriginalStatus", "充电枪原始状态", 2),
    PARKING_LOCK_STATE("parkingLockState", "充电枪地锁状态", 2),
    VEHICLE_CONN_STATE("vehicleConnState", "充电枪车辆连接状态", 2),
    GUN_LOCK_STATE("gunLockState", "充电枪枪锁状态", 2),
    K1_K2_STATE("k1k2State", "充电枪接触器k1k2状态", 2),
    BATTERY_SOC("batterySoc", "充电枪电池SOC", 2),
    RUN_MODE("runMode", "充电枪运行模式", 2),
    GUN_TEMP1("gunTemp1", "充电枪温度1", 2),
    GUN_TEMP2("gunTemp2", "充电枪温度2", 2),
    OUT_VOLT("outVolt", "充电枪输出电压(V)", 2),
    OUT_CURRENT("outCurrent", "充电枪输出电流(A)", 2),
    OUT_POWER("outPower", "充电枪输出功率(kW)", 2),
    REQ_VOLT("reqVolt", "充电枪需求电压(V)", 2),
    REQ_CURRENT("reqCurrent", "充电枪需求电流(A)", 2),
    REQ_POWER("reqPower", "充电枪需求功率(kW)", 2),
    DIR_METER_NUM("dirMeterNum", "充电枪直流电表读数", 2),
    ALTER_METER_NUM("alterMeterNum", "充电枪交流电表读数", 2),
    RUN_TIME("runTime", "充电枪运行时间(秒)", 2),
    REMAIN_TIME("remainTime", "充电枪剩余时间(秒)", 2),
    TOTAL_QT("totalQt", "充电枪总电量(度)", 2),
    TOTAL_COST("totalCost", "充电枪总费用(元)", 2),
    BATTERY_TEMP_MAX("batteryTempMax", "最高动力蓄电池温度(℃)", 2),
    BATTERY_TEMP_MAX_NO("batteryTempMaxNo", "最高动力蓄电池温度检测点编号(1-128)", 2),
    BATTERY_TEMP_MIN("batteryTempMin", "最低动力蓄电池温度(℃)", 2),
    BATTERY_TEMP_MIN_NO("batteryTempMinNo", "最低动力蓄电池温度检测点编号(1-128)", 2),
    BATTERY_VOLTAGE_MAX_GN("batteryVoltageMaxGn", "最高单体蓄电池电压所对应组号(0-15)", 2),
    BATTERY_VOLTAGE_MAX("batteryVoltageMax", "最高单体蓄电池电压(V)", 2),
    BATTERY_VOLTAGE_MIN_GN("batteryVoltageMinGn", "最低单体蓄电池电压所对应组号(0-15)", 2),
    BATTERY_VOLTAGE_MIN("batteryVoltageMin", "最低单体蓄电池电压(V)", 2),
    BCP_ALLOW_CHARGE_CELL_V_MAX("bcpAllowChargeCellVMax", "单体最高允许充电电压(V)", 2),
    BCP_ALLOW_CHARGE_CURRENT_MAX("bcpAllowChargeCurrentMax", "最高允许充电电流(A)", 2),
    BCP_BATTERY_NOMINAL_CAP("bcpBatteryNominalCap", "动力电池标称总能量(kWh)", 2),
    BCP_ALLOW_CHARGE_V_MAX("bcpAllowChargeVMax", "最高允许电压(V)", 2),
    BCP_ALLOW_CHARGE_TEMP_MAX("bcpAllowTempMax", "最高允许充电温度(℃)", 2),
    BATTERY_TYPE("batteryType", "电池类型", 2);

    //字段名
    private final String fieldCode;

    //字段中文名
    private final String fieldName;

    //字段类型 1-充电桩级 2-充电枪级
    private final int fieldType;

    GeneralFieldEnum(String fieldCode, String fieldName, int fieldType) {
        this.fieldCode = fieldCode;
        this.fieldName = fieldName;
        this.fieldType = fieldType;
    }

    public static GeneralFieldEnum getByFieldCode(String fieldCode) {
        for (GeneralFieldEnum generalFieldEnum : GeneralFieldEnum.values()) {
            if (generalFieldEnum.getFieldCode().equals(fieldCode)) {
                return generalFieldEnum;
            }
        }
        return null;
    }

    public static GeneralFieldEnum getByFieldName(String fieldName) {
        for (GeneralFieldEnum generalFieldEnum : GeneralFieldEnum.values()) {
            if (generalFieldEnum.getFieldName().equals(fieldName)) {
                return generalFieldEnum;
            }
        }
        return null;
    }

    public static GeneralFieldEnum getByFieldType(int fieldType) {
        for (GeneralFieldEnum generalFieldEnum : GeneralFieldEnum.values()) {
            if (generalFieldEnum.getFieldType() == fieldType) {
                return generalFieldEnum;
            }
        }
        return null;
    }

    public static GeneralFieldEnum getByFieldCodeAndFieldType(String fieldCode, int fieldType) {
        for (GeneralFieldEnum generalFieldEnum : GeneralFieldEnum.values()) {
            if (generalFieldEnum.getFieldCode().equals(fieldCode) && generalFieldEnum.getFieldType() == fieldType) {
                return generalFieldEnum;
            }
        }
        return null;
    }

    public static GeneralFieldEnum getByFieldNameAndFieldType(String fieldName, int fieldType) {
        for (GeneralFieldEnum generalFieldEnum : GeneralFieldEnum.values()) {
            if (generalFieldEnum.getFieldName().equals(fieldName) && generalFieldEnum.getFieldType() == fieldType) {
                return generalFieldEnum;
            }
        }
        return null;
    }

    public static GeneralFieldEnum getByFieldCodeAndFieldName(String fieldCode, String fieldName) {
        for (GeneralFieldEnum generalFieldEnum : GeneralFieldEnum.values()) {
            if (generalFieldEnum.getFieldCode().equals(fieldCode) && generalFieldEnum.getFieldName().equals(fieldName)) {
                return generalFieldEnum;
            }
        }
        return null;
    }

    public static GeneralFieldEnum getByFieldCodeAndFieldNameAndFieldType(String fieldCode, String fieldName, int fieldType) {
        for (GeneralFieldEnum generalFieldEnum : GeneralFieldEnum.values()) {
            if (generalFieldEnum.getFieldCode().equals(fieldCode) && generalFieldEnum.getFieldName().equals(fieldName) && generalFieldEnum.getFieldType() == fieldType) {
                return generalFieldEnum;
            }
        }
        return null;
    }

}
