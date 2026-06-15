package com.sunmax.webapp.service.feign;
import org.springframework.cloud.openfeign.FeignClient;
import com.sunmax.common.feign.fallback.GenericFeignFallbackFactory;


/**
 * @deprecated 此接口已迁移至 com.sunmax.common.feign.webapp.WebAppTogetherFeignClient
 * 请直接使用 com.sunmax.common.feign.webapp.WebAppTogetherFeignClient
 */
@FeignClient(value = "together-service", path = "/together/feign/swebapp", fallbackFactory = GenericFeignFallbackFactory.class)
@Deprecated
public interface TogetherService extends com.sunmax.common.feign.webapp.WebAppTogetherFeignClient {
}
