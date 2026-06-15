package com.sunmax.common.util;

import com.alibaba.fastjson2.annotation.JSONType;
import com.alibaba.fastjson2.JSONWriter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author xiuho
 * @date 2021-3.23下午 23:01
 * @description
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "ResponseResult")
@JSONType(serializeFeatures = {JSONWriter.Feature.NotWriteRootClassName})
public class ResponseResult<T> implements Serializable {

    private static final long serialVersionUID = 3468352004150968551L;

    public static final String SUCCESS = "操作成功";

    public static final String FAIL = "操作失败";

    public static final String PARAM_ERROR = "请求参数错误";

    public static final String RESPONSE_TIMEOUT = "响应超时";

    public static final String PARAM_PARSE_ERROR = "参数解析错误";

    public static final String PARAM_EXIST = "参数已存在";

    public static final String PARAM_ISNULL = "请求参数为空";

    public static final String APPLET_ERROR = "获取当前小程序交易信息错误";

    /**
     * 状态码 20000-请求成功 20001-前端展示错误 20004-熔断请求 50000-请求失败 50001-非法请求 50015-服务器认证失败 9999-token失效
     */
    @Schema(description = "状态码 20000-请求成功 20001-前端展示错误 20004-熔断请求 50000-请求失败 50001-非法请求 50015-服务器认证失败 9999-token失效")
    private Integer code;

    /**
     * 返回信息
     */
    @Schema(description = "返回信息")
    private String message;

    /**
     * 返回对象
     */
    @Schema(description = "返回数据")
    private T data;

    /**
     * 返回状态 20000-true 其它的-false
     */
    @Schema(description = "返回状态 20000-true 其它的-false")
    private boolean success;


    public ResponseResult(Integer code) {
        this.code = code;
    }

    public ResponseResult(Integer code, String message) {
        if (code == CodeStatus.OK) {
            this.success = true;
        }
        this.code = code;
        this.message = message;
    }

    public ResponseResult(Integer code, Throwable throwable) {
        if (code == CodeStatus.OK) {
            this.success = true;
        }
        this.code = code;
        this.message = throwable.getMessage();
    }

    public ResponseResult(Integer code, T data) {
        if (code == CodeStatus.OK) {
            this.success = true;
        }
        this.code = code;
        this.data = data;
    }

    public ResponseResult(Integer code, String message, T data) {
        if (code == CodeStatus.OK) {
            this.success = true;
        }
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static <T> ResponseResult<T> ok() {
        return ok(null);
    }

    public static <T> ResponseResult<T> ok(T data) {
        return ok(data, SUCCESS);
    }

    public static <T> ResponseResult<T> ok(T data, String message) {
        return new ResponseResult<T>().data(data).message(message).success(true).code(CodeStatus.OK);
    }

    public static <T> ResponseResult<T> paramShow(String message) {
        return new ResponseResult<T>().message(message).success(false).code(CodeStatus.SHOW);
    }

    public static <T> ResponseResult<T> paramShow(String showName, String message) {
        return new ResponseResult<T>().message("(" + showName + ")" + message).success(false).code(CodeStatus.SHOW);
    }

    public static <T> ResponseResult<T> error(String message) {
        return error(message, CodeStatus.FAIL, null);
    }

    public static <T> ResponseResult<T> error(String message, T data) {
        return new ResponseResult<T>().message(message).code(CodeStatus.FAIL).success(false).data(data);
    }

    public static <T> ResponseResult<T> error(String message, int code, T data) {
        return new ResponseResult<T>().message(message).code(code).success(false).data(data);
    }

    public static <T> ResponseResult<T> paramError(String message) {
        return new ResponseResult<T>().message(message).success(false).code(CodeStatus.ILLEGAL_REQUEST);
    }

    public static <T> ResponseResult<T> paramError(String message, int code) {
        return new ResponseResult<T>().message(message).success(false).code(code);
    }

    public ResponseResult<T> data(T data) {
        this.data = data;
        return this;
    }

    public ResponseResult<T> code(int code) {
        this.code = code;
        return this;
    }

    public ResponseResult<T> message(String message) {
        this.message = message;
        return this;
    }

    public ResponseResult<T> success(boolean success) {
        this.success = success;
        return this;
    }

    /**
     * 通用状态码
     */
    public static class CodeStatus {
        /**
         * 请求成功
         */
        public static final int OK = 20000;

        /**
         * 前端展示编码
         */
        public static final int SHOW = 20001;

        /**
         * 熔断请求
         */
        public static final int BREAKING = 20004;

        /**
         * 请求失败
         */
        public static final int FAIL = 50000;

        /**
         * 非法请求
         */
        public static final int ILLEGAL_REQUEST = 50001;

        /**
         * 非法令牌
         */
        public static final int ILLEGAL_TOKEN = 50008;

        /**
         * 其他客户登录
         */
        public static final int OTHER_CLIENTS_LOGGED_IN = 50012;

        /**
         * 令牌已过期
         */
        public static final int TOKEN_EXPIRED = 50014;

        /**
         * 认证服务器认证失败
         */
        public static final int ACCESS_FAIL = 50015;

        /**
         * 响应超时
         */
        public static final int RESPONSE_TIMEOUT = 50016;
    }
}