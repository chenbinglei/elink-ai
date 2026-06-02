package com.sunmax.common.enums;

import lombok.Getter;

@Getter
public enum ProtocolEnum {

    HD("hd", "华电转发协议", 1),
    PROVINCE("province", "省平台转发协议", 2),
    CITY("city", "市平台转发协议", 2),
    PLATFORM("platform", "平台控制协议", 2),
    INTERFLOW("interflow", "互联互通转发协议", 2),
    STORAGE("storage", "储能平台转发协议", 2);

    //字段名
    private final String code;

    //字段中文名
    private final String name;

    //字段类型 1-Mqtt 2-http
    private final int type;

    ProtocolEnum(String code, String name, Integer type) {
        this.code = code;
        this.name = name;
        this.type = type;
    }

    public static ProtocolEnum getByCode(String code) {
        for (ProtocolEnum protocolEnum : ProtocolEnum.values()) {
            if (protocolEnum.getCode().equals(code)) {
                return protocolEnum;
            }
        }
        return null;
    }

}
