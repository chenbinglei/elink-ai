//package com.sunmax.gateway.config;
//
//import lombok.Data;
//
//@Data
//public class APIException extends RuntimeException {
//
//    private String appCode;
//    private String path;
//    private String msg;
//    private Object data;
//
//    public APIException() {
//    }
//
//    public APIException(String appCode, String path) {
//        this.appCode = appCode;
//        this.path = path;
//    }
//
//    public APIException(String appCode, String path, Object data) {
//        this.appCode = appCode;
//        this.path = path;
//        this.data = data;
//    }
//
//    public APIException(String appCode, String path, String msg, Object data) {
//        super(msg);
//        this.appCode = appCode;
//        this.path = path;
//        this.msg = msg;
//        this.data = data;
//    }
//
//    public APIException(ApiResult apiResult) {
//        this.appCode = apiResult.getAppCode();
//        this.path = apiResult.getPath();
//        this.msg = apiResult.getMsg();
//        this.data = apiResult.getData();
//    }
//}