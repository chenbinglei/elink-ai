package com.sunmax.data.controller.feign;

import com.sunmax.common.dto.data.DeviceHistoryDto;
import com.sunmax.common.dto.data.NodeDifHistoryDto;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.vo.data.DeviceHistoryQueryVo;
import com.sunmax.data.service.DeviceFeignService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiOperationSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 提供设备服务需要的接口
 */
@RestController
@CrossOrigin
@RequestMapping("/feign/device")
@Api(tags = "提供设备服务需要的接口")
public class DeviceFeignController {

    @Autowired
    private DeviceFeignService deviceFeignService;

    @PostMapping("deleteAllDataStoreTable")
    @ApiOperation("批量删除数据存储表")
    @ApiOperationSupport(order = 1)
    public ResponseResult<Void> deleteAllDataStoreTable(@RequestBody Set<String> tableNames) {
        return deviceFeignService.deleteAllDataStoreTable(tableNames);
    }

    @PostMapping("findDeviceHistoryValueList")
    @ApiOperation("根据多个设备id和多个功能点标识和时间获取设备功能点历史列表")
    @ApiOperationSupport(order = 2)
    public ResponseResult<Map<String, Map<String, List<DeviceHistoryDto>>>> findDeviceHistoryValueList(@RequestBody DeviceHistoryQueryVo deviceQueryVo) {
        return deviceFeignService.findDeviceHistoryValueList(deviceQueryVo);
    }

    @PostMapping("findNodeDifHistoryListFeign")
    @ApiOperation("查询设备功能点指定时间段内的last和first历史数据")
    @ApiOperationSupport(order = 3)
    public ResponseResult<Map<String, Map<String, List<NodeDifHistoryDto>>>> findNodeDifHistoryListFeign(@RequestBody DeviceHistoryQueryVo deviceQueryVo) {
        return deviceFeignService.findNodeDifHistoryListFeign(deviceQueryVo);
    }

}
