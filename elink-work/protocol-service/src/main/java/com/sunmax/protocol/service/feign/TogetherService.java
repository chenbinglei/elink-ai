package com.sunmax.protocol.service.feign;
import org.springframework.cloud.openfeign.FeignClient;
import com.sunmax.common.feign.fallback.GenericFeignFallbackFactory;


/**
 * @deprecated 此接口已迁移至 com.sunmax.common.feign.protocol.ProtocolTogetherFeignClient
 * 请直接使用 com.sunmax.common.feign.protocol.ProtocolTogetherFeignClient
 */
@FeignClient(value = "together-service", path = "/together/feign/protocol", fallbackFactory = GenericFeignFallbackFactory.class)
@Deprecated
public interface TogetherService extends com.sunmax.common.feign.protocol.ProtocolTogetherFeignClient {
}
