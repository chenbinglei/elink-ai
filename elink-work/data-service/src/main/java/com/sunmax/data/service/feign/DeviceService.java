package com.sunmax.data.service.feign;

import com.sunmax.common.dto.device.DeviceFieldDto;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.vo.device.DeviceFieldQueryVo;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiOperationSupport;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Set;

@FeignClient(value = "device-service")
//@FeignClient(value = "device-service-cbl",url = "http://121.41.109.130:5000")
@RestController
@RequestMapping("/device/feign/data")
public interface DeviceService {

    @PostMapping("queryDeviceFieldList")
    @ApiOperation("查询设备数据及标识字段名")
    @ApiOperationSupport(order = 1)
    ResponseResult<Map<String, List<DeviceFieldDto>>> queryDeviceFieldList(@RequestBody DeviceFieldQueryVo deviceFieldQueryVo);

    @PostMapping("queryDeviceFieldListByMap")
    @ApiOperation("根据设备id和功能点标识查询设备功能点数据")
    @ApiOperationSupport(order = 2)
    ResponseResult<Map<String, List<DeviceFieldDto>>> queryDeviceFieldListByMap(@RequestBody Map<String, Set<String>> deviceFieldQueryMap);

}
