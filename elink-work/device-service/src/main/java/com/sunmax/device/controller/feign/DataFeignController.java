package com.sunmax.device.controller.feign;

import com.sunmax.common.dto.device.DeviceFieldDto;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.vo.device.DeviceFieldQueryVo;
import com.sunmax.device.service.DataFeignService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiOperationSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@CrossOrigin
@RequestMapping("/feign/data")
@Api(tags = "提供给协议服务调用的远程接口")
@ApiIgnore()
public class DataFeignController {

    @Autowired
    private DataFeignService dataFeignService;

    @PostMapping("queryDeviceFieldList")
    @ApiOperation("查询设备数据及标识字段名")
    @ApiOperationSupport(order = 1)
    public ResponseResult<Map<String, List<DeviceFieldDto>>> queryDeviceFieldList(@RequestBody DeviceFieldQueryVo deviceFieldQueryVo) {
        return dataFeignService.queryDeviceFieldList(deviceFieldQueryVo);
    }

    @PostMapping("queryDeviceFieldListByMap")
    @ApiOperation("根据设备id和功能点标识查询设备功能点数据")
    @ApiOperationSupport(order = 2)
    public ResponseResult<Map<String, List<DeviceFieldDto>>> queryDeviceFieldListByMap(@RequestBody Map<String, Set<String>> deviceFieldQueryMap) {
        return dataFeignService.queryDeviceFieldListByMap(deviceFieldQueryMap);
    }

}
