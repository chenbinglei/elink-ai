package com.sunmax.auth.service.feign;
import org.springframework.cloud.openfeign.FeignClient;
import com.sunmax.common.feign.fallback.GenericFeignFallbackFactory;


/**
 * @deprecated 此接口已迁移至 com.sunmax.common.feign.together.AuthTogetherFeignClient
 * 请直接使用 com.sunmax.common.feign.together.AuthTogetherFeignClient
 */
@FeignClient(value = "together-service", path = "/together/feign/sauth", fallbackFactory = GenericFeignFallbackFactory.class)
@Deprecated
public interface TogetherService extends com.sunmax.common.feign.together.AuthTogetherFeignClient {
}
