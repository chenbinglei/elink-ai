package com.sunmax.common.feign.webapp;

import com.sunmax.common.util.ResponseResult;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;
import com.sunmax.common.feign.fallback.GenericFeignFallbackFactory;

/**
 * 获取接入服务提供的接口
 */
@FeignClient(value = "sauth-service", path = "/sauth", fallbackFactory = GenericFeignFallbackFactory.class)
public interface WebAppSauthFeignClient {

    @PostMapping("/oauth/token")
    @Operation(summary = "用户登录Post请求方式")
    
    ResponseResult<Map<String,Object>> postAccessToken(@RequestParam Map<String, String> parameters);

}
