package com.sunmax.together.service.feign;
import org.springframework.cloud.openfeign.FeignClient;
import com.sunmax.common.feign.fallback.GenericFeignFallbackFactory;


/**
 * @deprecated 此接口已迁移至 com.sunmax.common.feign.together.TogetherDataFeignClient
 * 请直接使用 com.sunmax.common.feign.together.TogetherDataFeignClient
 */
@FeignClient(value = "sunos-data-service", path = "/data/feign/together", fallbackFactory = GenericFeignFallbackFactory.class)
@Deprecated
public interface DataService extends com.sunmax.common.feign.together.TogetherDataFeignClient {
}
