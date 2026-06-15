package com.sunmax.webapp.service.feign;
import org.springframework.cloud.openfeign.FeignClient;
import com.sunmax.common.feign.fallback.GenericFeignFallbackFactory;


/**
 * @deprecated 此接口已迁移至 com.sunmax.common.feign.webapp.WebAppProtocolFeignClient
 * 请直接使用 com.sunmax.common.feign.webapp.WebAppProtocolFeignClient
 */
@FeignClient(value = "sunos-protocol-service", path = "/protocol/feign/swebapp", fallbackFactory = GenericFeignFallbackFactory.class)
@Deprecated
public interface ProtocolService extends com.sunmax.common.feign.webapp.WebAppProtocolFeignClient {
}
