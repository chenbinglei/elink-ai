package com.sunmax.configure.service.feign;
import org.springframework.cloud.openfeign.FeignClient;
import com.sunmax.common.feign.fallback.GenericFeignFallbackFactory;


/**
 * @deprecated 此接口已迁移至 com.sunmax.common.feign.configure.ConfigureTogetherFeignClient
 * 请直接使用 com.sunmax.common.feign.configure.ConfigureTogetherFeignClient
 */
@FeignClient(value = "together-service", path = "/together/feign/configure", fallbackFactory = GenericFeignFallbackFactory.class)
@Deprecated
public interface TogetherService extends com.sunmax.common.feign.configure.ConfigureTogetherFeignClient {
}
