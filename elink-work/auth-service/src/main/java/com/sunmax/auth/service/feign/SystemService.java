package com.sunmax.auth.service.feign;
import org.springframework.cloud.openfeign.FeignClient;
import com.sunmax.common.feign.fallback.GenericFeignFallbackFactory;


/**
 * @deprecated 此接口已迁移至 com.sunmax.common.feign.system.AuthSystemFeignClient
 * 请直接使用 com.sunmax.common.feign.system.AuthSystemFeignClient
 */
@FeignClient(value = "system-service", path = "/system/feign/sauth", fallbackFactory = GenericFeignFallbackFactory.class)
@Deprecated
public interface SystemService extends com.sunmax.common.feign.system.AuthSystemFeignClient {
}
