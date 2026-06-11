package com.sunmax.devops.service.feign;
import org.springframework.cloud.openfeign.FeignClient;
import com.sunmax.common.feign.fallback.GenericFeignFallbackFactory;


/**
 * @deprecated 此接口已迁移至 com.sunmax.common.feign.auth.AuthSauthFeignClient
 * 请直接使用 com.sunmax.common.feign.auth.AuthSauthFeignClient
 */
@FeignClient(value = "sauth-service", path = "/sauth", fallbackFactory = GenericFeignFallbackFactory.class)
@Deprecated
public interface SauthService extends com.sunmax.common.feign.auth.AuthSauthFeignClient {
}
