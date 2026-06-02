package com.sunmax.webapp.service.feign;

import com.sunmax.common.util.ResponseResult;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiOperationSupport;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 获取接入服务提供的接口
 */
@FeignClient(value = "sauth-service")
@RestController
@RequestMapping("/sauth")
public interface SauthService {

    @PostMapping("/oauth/token")
    @ApiOperation("用户登录Post请求方式")
    @ApiOperationSupport(order = 1)
    ResponseResult<Map<String,Object>> postAccessToken(@RequestParam Map<String, String> parameters);

}
