package com.sunmax.common.feign.fallback;

import com.sunmax.common.util.ResponseResult;
import lombok.extern.slf4j.Slf4j;

/**
 * Feign降级基础类，提供统一的降级响应构造方法
 */
@Slf4j
public class BaseFeignFallback {

    protected <T> ResponseResult<T> fallbackResponse(String serviceName, String method, Throwable cause) {
        log.error("Feign调用降级: service={}, method={}, error={}", serviceName, method, cause.getMessage());
        return ResponseResult.error("服务[" + serviceName + "]调用失败: " + cause.getMessage());
    }

    protected <T> ResponseResult<T> fallbackResponse(String serviceName, Throwable cause) {
        return fallbackResponse(serviceName, "unknown", cause);
    }
}
