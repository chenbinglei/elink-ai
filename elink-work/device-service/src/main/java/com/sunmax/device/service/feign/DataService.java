package com.sunmax.device.service.feign;

import com.sunmax.common.dto.data.DeviceHistoryDto;
import com.sunmax.common.dto.data.NodeDifHistoryDto;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.vo.data.DeviceHistoryQueryVo;
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

/**
 * 接收数据服务提供的接口
 */
@FeignClient(value = "sunos-data-service")
@RestController
@RequestMapping("/data/feign/device")
public interface DataService {

    @PostMapping("deleteAllDataStoreTable")
    @ApiOperation("批量删除数据存储表")
    @ApiOperationSupport(order = 1)
    ResponseResult<Void> deleteAllDataStoreTable(@RequestBody Set<String> tableNames);

    @PostMapping("findDeviceHistoryValueList")
    @ApiOperation("根据多个设备id和多个功能点标识和时间获取设备功能点历史列表")
    @ApiOperationSupport(order = 2)
    ResponseResult<Map<String, Map<String, List<DeviceHistoryDto>>>> findDeviceHistoryValueList(@RequestBody DeviceHistoryQueryVo deviceQueryVo);

    @PostMapping("findNodeDifHistoryListFeign")
    @ApiOperation("查询设备功能点指定时间段内的last和first历史数据")
    @ApiOperationSupport(order = 3)
    ResponseResult<Map<String, Map<String, List<NodeDifHistoryDto>>>> findNodeDifHistoryListFeign(@RequestBody DeviceHistoryQueryVo deviceQueryVo);

}
