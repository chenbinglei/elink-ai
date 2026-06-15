package com.sunmax.protocol.service.feign;
import org.springframework.cloud.openfeign.FeignClient;
import com.sunmax.common.feign.fallback.GenericFeignFallbackFactory;


/**
 * @deprecated 此接口已迁移至 com.sunmax.common.feign.protocol.ProtocolOperateFeignClient
 * 请直接使用 com.sunmax.common.feign.protocol.ProtocolOperateFeignClient
 */
@FeignClient(value = "operate-service", path = "/operate/feign/protocol", fallbackFactory = GenericFeignFallbackFactory.class)
@Deprecated
public interface OperateService extends com.sunmax.common.feign.protocol.ProtocolOperateFeignClient {
}
