package com.sunmax.data.controller.feign;


import com.sunmax.common.dto.data.DeviceHistoryDto;
import com.sunmax.common.dto.data.NodeDifHistoryDto;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.vo.data.DeviceCountQueryVo;
import com.sunmax.common.vo.data.DeviceHistoryQueryVo;
import com.sunmax.common.vo.data.DeviceIndexListQueryVo;
import com.sunmax.common.vo.data.DeviceIndexQueryVo;
import com.sunmax.data.service.DeviceFeignService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 提供定时服务需要的接口
 */
@RestController
@CrossOrigin
@RequestMapping("/feign/crontab")
@Tag(name = "提供定时服务需要的接口")
public class CrontabFeignController {

    @Autowired
    private DeviceFeignService deviceFeignService;

    @PostMapping("findDeviceHistoryValueListFeign")
    @Operation(summary = "根据多个设备id和多个功能点标识和时间获取设备功能点历史列表")
    
    public ResponseResult<Map<String, Map<String, List<DeviceHistoryDto>>>> findDeviceHistoryValueList(@RequestBody DeviceHistoryQueryVo deviceQueryVo) {
        return deviceFeignService.findDeviceHistoryValueList(deviceQueryVo);
    }

    @PostMapping("findDeviceDifferenceListFeign")
    @Operation(summary = "查询设备功能点指定单个时间或时间段内的数据或临近值历史数据")
    
    public ResponseResult<Map<String, Map<String, List<DeviceHistoryDto>>>> findDeviceDifferenceListFeign(@RequestBody DeviceHistoryQueryVo deviceQueryVo, @RequestParam(required = false) Integer isNear) {
        return deviceFeignService.findDeviceDifferenceListFeign(deviceQueryVo, isNear);
    }

    @PostMapping("findDeviceCountFunListFeign")
    @Operation(summary = "查询指定设备指定功能点和时间段内设备统计值函数历史数据")
    
    public ResponseResult<Map<String, Map<String, List<DeviceHistoryDto>>>> findDeviceCountFunListFeign(@RequestBody DeviceCountQueryVo deviceCountQueryVo) {
        return deviceFeignService.findDeviceCountFunListFeign(deviceCountQueryVo);
    }

    @PostMapping("findNodeDifHistoryListFeign")
    @Operation(summary = "查询设备功能点指定时间段内的last和first历史数据")
    
    public ResponseResult<Map<String, Map<String, List<NodeDifHistoryDto>>>> findNodeDifHistoryListFeign(@RequestBody DeviceHistoryQueryVo deviceQueryVo) {
        return deviceFeignService.findNodeDifHistoryListFeign(deviceQueryVo);
    }

    @PostMapping("findDeviceHistoryIndexValueListFeign")
    @Operation(summary = "根据多个设备id和多个功能点标识和时间获取设备功能点历史列表")
    
    public ResponseResult<Map<String, Map<String, List<DeviceHistoryDto>>>> findDeviceHistoryIndexValueList(@RequestBody DeviceIndexQueryVo deviceQueryVo) {
        return deviceFeignService.findDeviceHistoryIndexValueList(deviceQueryVo);
    }

    @PostMapping("findDeviceDiffIndexValueListFeign")
    @Operation(summary = "查询设备功能点指定单个时间或时间段内的数据或临近值历史数据(查询索引枪数据)")
    
    public ResponseResult<Map<String, Map<String, List<DeviceHistoryDto>>>> findDeviceDiffIndexValueListFeign(@RequestBody DeviceIndexQueryVo deviceQueryVo) {
        return deviceFeignService.findDeviceDiffIndexValueListFeign(deviceQueryVo);
    }

    @PostMapping("findIndexListHistorValueFeign")
    @Operation(summary = "根据多个设备id和多个功能点标识以及多个索引查询历史数据列表")
    
    public ResponseResult<Map<String, Map<String, Map<String, List<DeviceHistoryDto>>>>> findIndexListHistorValueFeign(@RequestBody DeviceIndexListQueryVo deviceQueryVo) {
        return deviceFeignService.findIndexListHistorValueFeign(deviceQueryVo);
    }

    @PostMapping("findNodeDifHistoryIndexListFeign")
    @Operation(summary = "查询设备功能点指定时间段内的last和first历史数据(查询索引数据)")
    
    public ResponseResult<Map<String, Map<String, List<NodeDifHistoryDto>>>> findNodeDifHistoryIndexListFeign(@RequestBody DeviceIndexQueryVo deviceQueryVo) {
        return deviceFeignService.findNodeDifHistoryIndexListFeign(deviceQueryVo);
    }
}
