package com.sunmax.devops.service.feign;
import org.springframework.cloud.openfeign.FeignClient;
import com.sunmax.common.feign.fallback.GenericFeignFallbackFactory;


/**
 * @deprecated 此接口已迁移至 com.sunmax.common.feign.devops.DevopsTogetherFeignClient
 * 请直接使用 com.sunmax.common.feign.devops.DevopsTogetherFeignClient
 */
@FeignClient(value = "together-service", path = "/together/feign/devops", fallbackFactory = GenericFeignFallbackFactory.class)
@Deprecated
public interface TogetherService extends com.sunmax.common.feign.devops.DevopsTogetherFeignClient {
}
