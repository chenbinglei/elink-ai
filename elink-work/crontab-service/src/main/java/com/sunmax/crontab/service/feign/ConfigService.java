package com.sunmax.crontab.service.feign;
import org.springframework.cloud.openfeign.FeignClient;
import com.sunmax.common.feign.fallback.GenericFeignFallbackFactory;


/**
 * @deprecated 此接口已迁移至 com.sunmax.common.feign.crontab.CrontabConfigFeignClient
 * 请直接使用 com.sunmax.common.feign.crontab.CrontabConfigFeignClient
 */
@FeignClient(value = "configure-service", path = "/configure/feign/crontab", fallbackFactory = GenericFeignFallbackFactory.class)
@Deprecated
public interface ConfigService extends com.sunmax.common.feign.crontab.CrontabConfigFeignClient {
}
