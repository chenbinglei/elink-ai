package com.sunmax.crontab.service.feign;
import org.springframework.cloud.openfeign.FeignClient;
import com.sunmax.common.feign.fallback.GenericFeignFallbackFactory;


/**
 * @deprecated 此接口已迁移至 com.sunmax.common.feign.crontab.CrontabProtocolFeignClient
 * 请直接使用 com.sunmax.common.feign.crontab.CrontabProtocolFeignClient
 */
@FeignClient(value = "sunos-protocol-service", path = "/protocol/feign/crontab", fallbackFactory = GenericFeignFallbackFactory.class)
@Deprecated
public interface ProtocolService extends com.sunmax.common.feign.crontab.CrontabProtocolFeignClient {
}
