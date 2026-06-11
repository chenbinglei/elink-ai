package com.sunmax.together.service.feign;
import org.springframework.cloud.openfeign.FeignClient;
import com.sunmax.common.feign.fallback.GenericFeignFallbackFactory;


/**
 * @deprecated 此接口已迁移至 com.sunmax.common.feign.together.TogetherCrontabFeignClient
 * 请直接使用 com.sunmax.common.feign.together.TogetherCrontabFeignClient
 */
@FeignClient(value = "crontab-service", path = "/crontab/feign/together", fallbackFactory = GenericFeignFallbackFactory.class)
@Deprecated
public interface CrontabService extends com.sunmax.common.feign.together.TogetherCrontabFeignClient {
}
