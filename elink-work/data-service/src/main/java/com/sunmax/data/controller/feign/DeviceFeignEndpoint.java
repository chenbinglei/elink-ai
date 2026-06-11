package com.sunmax.data.controller.feign;

import com.sunmax.common.dto.data.DeviceHistoryDto;
import com.sunmax.common.dto.data.NodeDifHistoryDto;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.vo.data.DeviceHistoryQueryVo;
import com.sunmax.data.service.DeviceFeignService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;
import com.sunmax.common.feign.device.DeviceDataFeignClient;

/**
 * 提供设备服务需要的接口
 */
@RestController
@CrossOrigin
@RequestMapping("/feign/device")
@Tag(name = "提供设备服务需要的接口")
public class DeviceFeignEndpoint implements DeviceDataFeignClient {

    @Autowired
    private DeviceFeignService deviceFeignService;

    @PostMapping("deleteAllDataStoreTable")
    @Operation(summary = "批量删除数据存储表")
    
    @Override
    public ResponseResult<Void> deleteAllDataStoreTable(@RequestBody Set<String> tableNames) {
        return deviceFeignService.deleteAllDataStoreTable(tableNames);
    }

    @PostMapping("findDeviceHistoryValueList")
    @Operation(summary = "根据多个设备id和多个功能点标识和时间获取设备功能点历史列表")
    
    public ResponseResult<Map<String, Map<String, List<DeviceHistoryDto>>>> findDeviceHistoryValueList(@RequestBody DeviceHistoryQueryVo deviceQueryVo) {
        return deviceFeignService.findDeviceHistoryValueList(deviceQueryVo);
    }

    @PostMapping("findNodeDifHistoryListFeign")
    @Operation(summary = "查询设备功能点指定时间段内的last和first历史数据")
    
    public ResponseResult<Map<String, Map<String, List<NodeDifHistoryDto>>>> findNodeDifHistoryListFeign(@RequestBody DeviceHistoryQueryVo deviceQueryVo) {
        return deviceFeignService.findNodeDifHistoryListFeign(deviceQueryVo);
    }

}
