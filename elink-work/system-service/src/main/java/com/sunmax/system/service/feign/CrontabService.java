package com.sunmax.system.service.feign;
import org.springframework.cloud.openfeign.FeignClient;
import com.sunmax.common.feign.fallback.GenericFeignFallbackFactory;


/**
 * @deprecated 此接口已迁移至 com.sunmax.common.feign.system.SystemCrontabFeignClient
 * 请直接使用 com.sunmax.common.feign.system.SystemCrontabFeignClient
 */
@FeignClient(value = "crontab-service", path = "/crontab/feign/system", fallbackFactory = GenericFeignFallbackFactory.class)
@Deprecated
public interface CrontabService extends com.sunmax.common.feign.system.SystemCrontabFeignClient {
}
