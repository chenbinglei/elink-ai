package com.sunmax.configure.service.feign;
import org.springframework.cloud.openfeign.FeignClient;
import com.sunmax.common.feign.fallback.GenericFeignFallbackFactory;


/**
 * @deprecated 此接口已迁移至 com.sunmax.common.feign.auth.AuthPermissionNoContextFeignClient
 * 请直接使用 com.sunmax.common.feign.auth.AuthPermissionNoContextFeignClient
 */
@FeignClient(value = "sauth-service", path = "/feign/permission", fallbackFactory = GenericFeignFallbackFactory.class)
@Deprecated
public interface SauthService extends com.sunmax.common.feign.auth.AuthPermissionNoContextFeignClient {
}
