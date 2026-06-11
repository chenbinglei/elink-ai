package com.sunmax.device.service.feign;
import org.springframework.cloud.openfeign.FeignClient;
import com.sunmax.common.feign.fallback.GenericFeignFallbackFactory;


/**
 * @deprecated 此接口已迁移至 com.sunmax.common.feign.device.DeviceConfigureFeignClient
 * 请直接使用 com.sunmax.common.feign.device.DeviceConfigureFeignClient
 */
@FeignClient(value = "configure-service", path = "/configure/feign/device", fallbackFactory = GenericFeignFallbackFactory.class)
@Deprecated
public interface ConfigureService extends com.sunmax.common.feign.device.DeviceConfigureFeignClient {
}
