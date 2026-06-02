package com.sunmax.common.enums;

import lombok.Getter;

/**
 * 华电服务标识枚举类
 */
@Getter
public enum HdCodeEnum {

    ELEC_INFO_ACQ("elecInfoAcq", "负荷用电信息"),
    CHG_STAT_INFO_ACQ("chgStatInfoAcq", "充电站信息"),
    ES_INFO_ACQ("esInfoAcq", "储能电站信息"),
    PV_INFO_ACQ("pvInfoAcq", "光伏电站站信息"),
    SWITCH_STATE_GET("switchStateGet", "开关状态"),
    SWITCH_CTL("switchCtl","即时分合闸"),
    POWER_CTL("powerCtl", "即时功率控制");

    //字段名
    private final String code;

    //字段中文名
    private final String name;
    HdCodeEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public static HdCodeEnum getByCode(String code) {
        for (HdCodeEnum hdCodeEnum : HdCodeEnum.values()) {
            if (hdCodeEnum.getCode().equals(code)) {
                return hdCodeEnum;
            }
        }
        return null;
    }

}
