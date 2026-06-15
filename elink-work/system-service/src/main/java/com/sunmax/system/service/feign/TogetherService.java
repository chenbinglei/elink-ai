package com.sunmax.system.service.feign;
import org.springframework.cloud.openfeign.FeignClient;
import com.sunmax.common.feign.fallback.GenericFeignFallbackFactory;


/**
 * @deprecated 此接口已迁移至 com.sunmax.common.feign.system.SystemTogetherFeignClient
 * 请直接使用 com.sunmax.common.feign.system.SystemTogetherFeignClient
 */
@FeignClient(value = "together-service", path = "/together/feign/system", fallbackFactory = GenericFeignFallbackFactory.class)
@Deprecated
public interface TogetherService extends com.sunmax.common.feign.system.SystemTogetherFeignClient {
}
