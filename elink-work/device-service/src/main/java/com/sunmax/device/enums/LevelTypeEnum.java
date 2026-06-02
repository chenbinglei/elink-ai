package com.sunmax.device.enums;

import lombok.Getter;

/**
 * 级别类型 (id -> 中文类名)
 */
@Getter
public enum LevelTypeEnum {

    A(1, "次要告警"),
    B(2, "重要告警"),
    C(3, "紧急告警"),
    D(4, "提示告警"),
    E(5, "离线告警");

    private final int code;
    private final String name;

    LevelTypeEnum(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public static String getName(int code) {
        for (LevelTypeEnum value : values()) {
            if(value.getCode() == code) {
                return value.getName();
            }
        }
        return null;
    }

    public static Integer getCode(String name) {
        for (LevelTypeEnum value : values()) {
            if(value.getName().equals(name)) {
                return value.getCode();
            }
        }
        return null;
    }

}
