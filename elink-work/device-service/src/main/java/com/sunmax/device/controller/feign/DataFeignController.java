package com.sunmax.device.controller.feign;

import com.sunmax.common.dto.device.DeviceFieldDto;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.vo.device.DeviceFieldQueryVo;
import com.sunmax.device.service.DataFeignService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Hidden;

import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@CrossOrigin
@RequestMapping("/feign/data")
@Tag(name = "提供给协议服务调用的远程接口")
@Hidden()
public class DataFeignController {

    @Autowired
    private DataFeignService dataFeignService;

    @PostMapping("queryDeviceFieldList")
    @Operation(summary = "查询设备数据及标识字段名")
    
    public ResponseResult<Map<String, List<DeviceFieldDto>>> queryDeviceFieldList(@RequestBody DeviceFieldQueryVo deviceFieldQueryVo) {
        return dataFeignService.queryDeviceFieldList(deviceFieldQueryVo);
    }

    @PostMapping("queryDeviceFieldListByMap")
    @Operation(summary = "根据设备id和功能点标识查询设备功能点数据")
    
    public ResponseResult<Map<String, List<DeviceFieldDto>>> queryDeviceFieldListByMap(@RequestBody Map<String, Set<String>> deviceFieldQueryMap) {
        return dataFeignService.queryDeviceFieldListByMap(deviceFieldQueryMap);
    }

}
