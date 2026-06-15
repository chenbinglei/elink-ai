package com.sunmax.common.feign.devops;

import com.sunmax.common.dto.auth.PermissionInfoListDto;
import com.sunmax.common.util.ResponseResult;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;
import com.sunmax.common.feign.fallback.GenericFeignFallbackFactory;

/**
 * 获取认证服务提供的接口
 */
@FeignClient(value = "sauth-service", path = "/sauth", fallbackFactory = GenericFeignFallbackFactory.class)
public interface DevopsSauthFeignClient {

    @PostMapping("/feign/permission/findPermissionByUserAccount")
    @Operation(summary = "根据用户账号查询权限数据")
    
    ResponseResult<List<PermissionInfoListDto>> findPermissionByUserAccount(@RequestParam String userAccount, @RequestParam String clientId);

    @PostMapping("/oauth/token")
    @Operation(summary = "用户登录Post请求方式")
    
    ResponseResult<Map<String,Object>> postAccessToken(@RequestParam Map<String, String> parameters);

}
