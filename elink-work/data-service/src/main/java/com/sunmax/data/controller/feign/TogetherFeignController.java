package com.sunmax.data.controller.feign;

import com.sunmax.common.dto.data.DeviceHistoryDto;
import com.sunmax.common.dto.data.NodeDifHistoryDto;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.vo.data.DeviceCountQueryVo;
import com.sunmax.common.vo.data.DeviceHistoryQueryVo;
import com.sunmax.common.vo.data.DeviceIndexQueryVo;
import com.sunmax.data.service.DeviceFeignService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiOperationSupport;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@CrossOrigin
@RequestMapping("/feign/together")
@Api(tags = "提供给能源聚合服务调用的远程接口")
public class TogetherFeignController {

    @Autowired
    private DeviceFeignService deviceFeignService;

    @PostMapping("findDeviceHistoryValueListFeign")
    @ApiOperation("根据多个设备id和多个功能点标识和时间获取设备功能点历史列表")
    @ApiOperationSupport(order = 1)
    public ResponseResult<Map<String, Map<String, List<DeviceHistoryDto>>>> findDeviceHistoryValueList(@RequestBody DeviceHistoryQueryVo deviceQueryVo) {
        return deviceFeignService.findDeviceHistoryValueList(deviceQueryVo);
    }

    @PostMapping("findDeviceHistoryIndexValueListFeign")
    @ApiOperation("根据多个设备id和多个功能点标识和时间获取设备功能点历史列表")
    @ApiOperationSupport(order = 2)
    public ResponseResult<Map<String, Map<String, List<DeviceHistoryDto>>>> findDeviceHistoryIndexValueList(@RequestBody DeviceIndexQueryVo deviceQueryVo) {
        return deviceFeignService.findDeviceHistoryIndexValueList(deviceQueryVo);
    }

    @PostMapping("findNodeDifHistoryListFeign")
    @ApiOperation("查询设备功能点指定时间段内的last和first历史数据")
    @ApiOperationSupport(order = 3)
    public ResponseResult<Map<String, Map<String, List<NodeDifHistoryDto>>>> findNodeDifHistoryListFeign(@RequestBody DeviceHistoryQueryVo deviceQueryVo) {
        return deviceFeignService.findNodeDifHistoryListFeign(deviceQueryVo);
    }

    @PostMapping("findDeviceCountFunListFeign")
    @ApiOperation("查询指定设备指定功能点和时间段内设备统计值函数历史数据")
    @ApiOperationSupport(order = 4)
    public ResponseResult<Map<String, Map<String, List<DeviceHistoryDto>>>> findDeviceCountFunListFeign(@RequestBody DeviceCountQueryVo deviceCountQueryVo) {
        return deviceFeignService.findDeviceCountFunListFeign(deviceCountQueryVo);
    }

}
