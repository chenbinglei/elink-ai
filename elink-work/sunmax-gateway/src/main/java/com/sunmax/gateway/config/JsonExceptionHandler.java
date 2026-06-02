//package com.sunmax.gateway.config;
//
//import com.sunmax.gateway.util.ResponseResult;
//import org.springframework.boot.autoconfigure.web.ErrorProperties;
//import org.springframework.boot.autoconfigure.web.ResourceProperties;
//import org.springframework.boot.autoconfigure.web.reactive.error.DefaultErrorWebExceptionHandler;
//import org.springframework.boot.web.error.ErrorAttributeOptions;
//import org.springframework.boot.web.reactive.error.ErrorAttributes;
//import org.springframework.cloud.gateway.support.NotFoundException;
//import org.springframework.context.ApplicationContext;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.MediaType;
//import org.springframework.web.reactive.function.server.*;
//
//import java.util.Map;
//
//public class JsonExceptionHandler extends DefaultErrorWebExceptionHandler {
//
//    public JsonExceptionHandler(ErrorAttributes errorAttributes,  ResourceProperties resource,
//                                ErrorProperties errorProperties, ApplicationContext applicationContext) {
//        super(errorAttributes, resource, errorProperties, applicationContext);
//    }
//
//    /**
//     * 获取异常属性
//     */
//    @Override
//    protected ErrorAttributeOptions getErrorAttributeOptions(ServerRequest request, MediaType mediaType) {
//        int code = 500;
//        Throwable error = super.getError(request);
//        if (error instanceof NotFoundException) {
//            code = 404;
//        }
//        return response(code, this.buildMessage(request, error));
//    }
//
//    /**
//     * 指定响应处理方法为JSON处理的方法
//     *
//     * @param errorAttributes
//     */
//    @Override
//    protected RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes errorAttributes) {
//        return RouterFunctions.route(RequestPredicates.all(), this::renderErrorResponse);
//    }
//
//    /**
//     * 根据code获取对应的HttpStatus
//     *
//     * @param errorAttributes
//     */
//    @Override
//    protected int getHttpStatus(Map<String, Object> errorAttributes) {
//        return  (int) errorAttributes.get("code");
//    }
//
//    /**
//     * 构建异常信息
//     *
//     * @param request
//     * @param ex
//     * @return
//     */
//    private String buildMessage(ServerRequest request, Throwable ex) {
//        StringBuilder message = new StringBuilder("Failed to handle request [");
//        message.append(request.methodName());
//        message.append(" ");
//        message.append(request.uri());
//        message.append("]");
//        if (ex != null) {
//            message.append(": ");
//            message.append(ex.getMessage());
//        }
//        return message.toString();
//    }
//
//    /**
//     * 构建返回的JSON数据格式
//     *
//     * @param status       状态码
//     * @param errorMessage 异常信息
//     * @return
//     */
//    public static ResponseResult<String> response(int status, String errorMessage) {
//        return new ResponseResult<>(status,errorMessage,"服务正忙,请稍后再试");
//    }
//}
//
