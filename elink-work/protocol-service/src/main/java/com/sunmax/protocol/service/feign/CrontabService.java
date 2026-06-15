package com.sunmax.protocol.service.feign;
import org.springframework.cloud.openfeign.FeignClient;
import com.sunmax.common.feign.fallback.GenericFeignFallbackFactory;


/**
 * @deprecated 此接口已迁移至 com.sunmax.common.feign.protocol.ProtocolCrontabFeignClient
 * 请直接使用 com.sunmax.common.feign.protocol.ProtocolCrontabFeignClient
 */
@FeignClient(value = "crontab-service", path = "/crontab/feign/protocol", fallbackFactory = GenericFeignFallbackFactory.class)
@Deprecated
public interface CrontabService extends com.sunmax.common.feign.protocol.ProtocolCrontabFeignClient {
}
