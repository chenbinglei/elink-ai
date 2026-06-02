package com.sunmax.common.enums;

import lombok.Getter;

@Getter
public enum ResponseCodeEnum {

    /**
     * 状态码 20000-请求成功 20001-前端展示错误 20004-熔断请求 50000-请求失败 50001-非法请求 50015-服务器认证失败
     */
    SUCCESS(20000, "请求成功"),
    SHOW_ERROR(20001, "前端展示错误"),
    FUSE_REQUEST(20004, "熔断请求"),
    FAIL(50000, "请求失败"),
    ILLEGAL_FAIL(50001, "非法请求"),
    AUTH_FAIL(50015, "服务器认证失败");

    private final Integer code;

    private final String message;

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    ResponseCodeEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

}
