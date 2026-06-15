package com.sunmax.common.feign.auth;

import com.sunmax.common.dto.auth.PermissionInfoListDto;
import com.sunmax.common.util.ResponseResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import com.sunmax.common.feign.fallback.GenericFeignFallbackFactory;

/**
 * 认证服务 - 权限查询接口
 * 被调用方：together-service, system-service, device-service, crontab-service, protocol-service
 */
@FeignClient(value = "sauth-service", path = "/sauth/feign/permission", fallbackFactory = GenericFeignFallbackFactory.class)
@Tag(name = "认证服务-权限查询接口")
public interface AuthPermissionFeignClient {

    @PostMapping("findPermissionByUserAccount")
    @Operation(summary = "根据用户账号查询权限数据")
    ResponseResult<List<PermissionInfoListDto>> findPermissionByUserAccount(@RequestParam String userAccount, @RequestParam String clientId);
}
