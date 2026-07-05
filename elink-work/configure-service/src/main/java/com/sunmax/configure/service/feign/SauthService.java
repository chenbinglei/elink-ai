package com.sunmax.configure.service.feign;
import org.springframework.cloud.openfeign.FeignClient;
import com.sunmax.common.feign.fallback.GenericFeignFallbackFactory;


/**
 * 修复：auth-service context-path 为 /sauth，Feign 路径需带 /sauth 前缀
 * 原 AuthPermissionNoContextFeignClient path 缺失 /sauth 前缀导致 404
 */
@FeignClient(value = "sauth-service", path = "/sauth", fallbackFactory = GenericFeignFallbackFactory.class)
public interface SauthService extends com.sunmax.common.feign.auth.AuthPermissionFeignClient {
}
