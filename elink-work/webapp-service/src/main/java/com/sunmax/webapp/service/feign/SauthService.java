package com.sunmax.webapp.service.feign;
import org.springframework.cloud.openfeign.FeignClient;

import com.sunmax.common.feign.auth.AuthPermissionFeignClient;
import com.sunmax.common.feign.fallback.GenericFeignFallbackFactory;


/**
 * @deprecated 此接口已迁移至 com.sunmax.common.feign.auth.AuthPermissionFeignClient
 * 请直接使用 com.sunmax.common.feign.auth.AuthPermissionFeignClient
 */
@FeignClient(value = "sauth-service", path = "/sauth", fallbackFactory = GenericFeignFallbackFactory.class)
@Deprecated
public interface SauthService extends AuthPermissionFeignClient {
}
