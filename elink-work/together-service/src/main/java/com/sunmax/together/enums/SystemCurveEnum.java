package com.sunmax.together.enums;

import lombok.Getter;

@Getter
public enum SystemCurveEnum {


    ACTUAL_POWER("actualPower", "实际功率"),
    THEORY_POWER("theoryPower", "理论功率"),
    ACTUAL_QT("actualQt", "实际发电量"),
    ACTIVE_POWER("activePower", "有功功率"),
    REACTIVE_POWER("reactivePower", "无功功率"),
    HORIZONTAL_RADIATION("horizontalRadiation", "水平辐射值"),
    INCLINED_RADIATION("inclinedRadiation","倾斜辐射值"),
    AMBIENT_TEMPERATURE("ambientTemperature","环境温度"),
    CABINET_TEMPERATURE("cabinetTemperature","柜内温度"),
    CABINET_HUMIDITY("cabinetHumidity","柜内湿度"),
    RADIANT_EXPOSURE("radiantExposure", "水平总辐照量"),
    OBLIQUE_IRRADIATION("obliqueIrradiation","倾斜总辐照量"),
    CHARGE_QT("chargeQt","充电量"),
    DISCHARGE_QT("dischargeQt","放电量"),
    BATTERY_SOC("batterySoc","电池SOC"),
    BATTERY_VOLTAGE("batteryVoltage","电池总电压"),
    BATTERY_CURRENT("batteryCurrent","电池总电流"),
    CHARGE_POWER("chargePower", "充电功率"),
    DISCHARGE_POWER("dischargePower", "放电功率"),
    CELL_VOLTAGE("cellVoltage", "电芯电压"),
    CELL_TEMPERATURE("cellTemperature", "电芯温度"),
    POWER_FACTOR("powerFactor", "功率因数"),
    A_PHASE_POWER_FACTOR("aPhasePowerFactor", "A相功率因数"),
    B_PHASE_POWER_FACTOR("bPhasePowerFactor", "B相功率因数"),
    C_PHASE_POWER_FACTOR("cPhasePowerFactor", "C相功率因数"),
    TOTAL_ACTIVE_POWER("totalActivePower", "总有功"),
    A_PHASE_ACTIVE_POWER("aPhaseActivePower", "A相有功"),
    B_PHASE_ACTIVE_POWER("bPhaseActivePower", "B相有功"),
    C_PHASE_ACTIVE_POWER("cPhaseActivePower", "C相有功"),
    TOTAL_REACTIVE_POWER("totalReactivePower", "总无功"),
    A_PHASE_REACTIVE_POWER("aPhaseReactivePower", "A相无功"),
    B_PHASE_REACTIVE_POWER("bPhaseReactivePower", "B相无功"),
    C_PHASE_REACTIVE_POWER("cPhaseReactivePower", "C相无功"),
    TOP_SUP_KWH("topSupKwh", "正向有功电量(尖)"),
    PEAK_SUP_KWH("peakSupKwh", "正向有功电量(峰)"),
    PLAIN_SUP_KWH("plainSupKwh", "正向有功电量(平)"),
    VALLEY_SUP_KWH("valleySupKwh", "正向有功电量(谷)"),
    DEEP_SUP_KWH("deepSupKwh", "正向有功电量(深谷)"),
    TOP_REV_KWH("topRevKwh", "反向有功电量(尖)"),
    PEAK_REV_KWH("peakRevKwh", "反向有功电量(峰)"),
    PLAIN_REV_KWH("plainRevKwh", "反向有功电量(平)"),
    VALLEY_REV_KWH("valleyRevKwh", "反向有功电量(谷)"),
    DEEP_REV_KWH("deepRevKwh", "反向有功电量(深谷)");

    //字段名
    private final String code;

    //字段中文名
    private final String name;
    SystemCurveEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public static SystemCurveEnum getByCode(String code) {
        for (SystemCurveEnum systemCurveEnum : SystemCurveEnum.values()) {
            if (systemCurveEnum.getCode().equals(code)) {
                return systemCurveEnum;
            }
        }
        return null;
    }

}
