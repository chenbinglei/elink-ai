package com.sunmax.webapp.service.feign;
import org.springframework.cloud.openfeign.FeignClient;
import com.sunmax.common.feign.fallback.GenericFeignFallbackFactory;


/**
 * @deprecated 此接口已迁移至 com.sunmax.common.feign.webapp.WebAppDeviceFeignClient
 * 请直接使用 com.sunmax.common.feign.webapp.WebAppDeviceFeignClient
 */
@FeignClient(value = "device-service", path = "/device/feign/swebapp", fallbackFactory = GenericFeignFallbackFactory.class)
@Deprecated
public interface DeviceService extends com.sunmax.common.feign.webapp.WebAppDeviceFeignClient {
}
