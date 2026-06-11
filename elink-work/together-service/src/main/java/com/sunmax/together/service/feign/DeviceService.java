package com.sunmax.together.service.feign;
import org.springframework.cloud.openfeign.FeignClient;
import com.sunmax.common.feign.fallback.GenericFeignFallbackFactory;


/**
 * @deprecated 此接口已迁移至 com.sunmax.common.feign.together.TogetherDeviceFeignClient
 * 请直接使用 com.sunmax.common.feign.together.TogetherDeviceFeignClient
 */
@FeignClient(value = "device-service", path = "/device/feign/together", fallbackFactory = GenericFeignFallbackFactory.class)
@Deprecated
public interface DeviceService extends com.sunmax.common.feign.together.TogetherDeviceFeignClient {
}
