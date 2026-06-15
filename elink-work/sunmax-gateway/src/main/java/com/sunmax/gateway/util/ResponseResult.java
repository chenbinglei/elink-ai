package com.sunmax.gateway.util;

import lombok.Data;

import java.io.Serializable;

/**
 * @author xiuho
 * @date 2021-3.23下午 23:01
 * @description
 */
@Data
public class ResponseResult<T> implements Serializable {

    private static final long serialVersionUID = 3468352004150968551L;

    public static final String SUCCESS = "操作成功";

    public static final String FAIL = "操作失败";

    public static final String PARAM_ERROR = "参数错误";

    public static final String PARAM_PARSE_ERROR = "参数解析错误";

    public static final String PARAM_EXIST = "参数已存在";

    /**
     * 状态码
     */
    private Integer code;

    /**
     * 消息
     */
    private String message;

    /**
     * 返回对象
     */
    private T data;

    /**
     * 返回状态
     */
    boolean success;

    public ResponseResult() {
        super();
    }

    public ResponseResult(Integer code) {
        super();
        this.code = code;
    }

    public ResponseResult(Integer code, String message) {
        super();
        this.code = code;
        this.message = message;
    }

    public ResponseResult(Integer code, Throwable throwable) {
        super();
        this.code = code;
        this.message = throwable.getMessage();
    }

    public ResponseResult(Integer code, T data) {
        super();
        this.code = code;
        this.data = data;
    }

    public ResponseResult(Integer code, String message, T data) {
        super();
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static <T> ResponseResult<T> ok() {
        return ok(null);
    }

    public static <T> ResponseResult<T> ok(T data) {
        return ok(data, "执行成功");
    }

    public static <T> ResponseResult<T> ok(T data, String message) {
        return new ResponseResult<T>().data(data).message(message).success(true).code(20000);
    }

    public static <T> ResponseResult<T> paramShow(String message) {
        return new ResponseResult<T>().message(message).success(true).code(20001);
    }

    public static <T> ResponseResult<String> error(String message) {
        return error(message, 500, null);
    }

    public static <T> ResponseResult<T> error(String message, T data) {
        return new ResponseResult<T>().message(message).code(500).success(false).data(data);
    }

    public static <T> ResponseResult<T> error(String message, int code, T data) {
        return new ResponseResult<T>().message(message).code(code).success(false).data(data);
    }

    public static <T> ResponseResult<T> paramError(String message) {
        return new ResponseResult<T>().message(message).success(true).code(50001);
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

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((data == null) ? 0 : data.hashCode());
        result = prime * result + ((message == null) ? 0 : message.hashCode());
        result = prime * result + ((code == null) ? 0 : code.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        ResponseResult<?> other = (ResponseResult<?>) obj;
        if (data == null) {
            if (other.data != null) {
                return false;
            }
        } else if (!data.equals(other.data)) {
            return false;
        }
        if (message == null) {
            if (other.message != null) {
                return false;
            }
        } else if (!message.equals(other.message)) {
            return false;
        }
        if (code == null) {
            return other.code == null;
        } else return code.equals(other.code);
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
         * 请求失败
         */
        public static final int FAIL = 20002;

        /**
         * 熔断请求
         */
        public static final int BREAKING = 20004;

        /**
         * 非法请求
         */
        public static final int ILLEGAL_REQUEST = 50000;

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
    }
}