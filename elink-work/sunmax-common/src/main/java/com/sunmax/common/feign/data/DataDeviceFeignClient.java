package com.sunmax.common.feign.data;

import com.sunmax.common.dto.device.DeviceFieldDto;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.vo.device.DeviceFieldQueryVo;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;
import java.util.Set;
import com.sunmax.common.feign.fallback.GenericFeignFallbackFactory;

@FeignClient(value = "device-service", path = "/device/feign/data", fallbackFactory = GenericFeignFallbackFactory.class)
public interface DataDeviceFeignClient {

    @PostMapping("queryDeviceFieldList")
    @Operation(summary = "查询设备数据及标识字段名")
    
    ResponseResult<Map<String, List<DeviceFieldDto>>> queryDeviceFieldList(@RequestBody DeviceFieldQueryVo deviceFieldQueryVo);

    @PostMapping("queryDeviceFieldListByMap")
    @Operation(summary = "根据设备id和功能点标识查询设备功能点数据")
    
    ResponseResult<Map<String, List<DeviceFieldDto>>> queryDeviceFieldListByMap(@RequestBody Map<String, Set<String>> deviceFieldQueryMap);

}
