package com.sunmax.crontab.service.feign;


import com.sunmax.common.dto.data.DeviceHistoryDto;
import com.sunmax.common.dto.data.NodeDifHistoryDto;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.vo.data.DeviceCountQueryVo;
import com.sunmax.common.vo.data.DeviceHistoryQueryVo;
import com.sunmax.common.vo.data.DeviceIndexListQueryVo;
import com.sunmax.common.vo.data.DeviceIndexQueryVo;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiOperationSupport;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@FeignClient(value = "sunos-data-service")
//@FeignClient(value = "sunos-data-service-cbl",url = "http://121.41.109.130:60004")
@RestController
@RequestMapping("/data/feign/crontab")
public interface DataService {

    @PostMapping("findDeviceHistoryValueListFeign")
    @ApiOperation("根据多个设备id和多个功能点标识和时间获取设备功能点历史列表")
    @ApiOperationSupport(order = 1)
    ResponseResult<Map<String, Map<String, List<DeviceHistoryDto>>>> findDeviceHistoryValueList(@RequestBody DeviceHistoryQueryVo deviceQueryVo);

    @PostMapping("findDeviceDifferenceListFeign")
    @ApiOperation("查询设备功能点指定单个时间或时间段内的数据或临近值历史数据")
    @ApiOperationSupport(order = 2)
    ResponseResult<Map<String, Map<String, List<DeviceHistoryDto>>>> findDeviceDifferenceListFeign(@RequestBody DeviceHistoryQueryVo deviceQueryVo, @RequestParam(required = false) Integer isNear);

    @PostMapping("findDeviceCountFunListFeign")
    @ApiOperation("查询指定设备指定功能点和时间段内设备统计值函数历史数据")
    @ApiOperationSupport(order = 3)
    ResponseResult<Map<String, Map<String, List<DeviceHistoryDto>>>> findDeviceCountFunListFeign(@RequestBody DeviceCountQueryVo deviceCountQueryVo);

    @PostMapping("findNodeDifHistoryListFeign")
    @ApiOperation("查询设备功能点指定时间段内的last和first历史数据")
    @ApiOperationSupport(order = 4)
    ResponseResult<Map<String, Map<String, List<NodeDifHistoryDto>>>> findNodeDifHistoryListFeign(@RequestBody DeviceHistoryQueryVo deviceQueryVo);

    @PostMapping("findDeviceHistoryIndexValueListFeign")
    @ApiOperation("根据多个设备id和多个功能点标识和时间获取设备功能点历史列表")
    @ApiOperationSupport(order = 5)
    ResponseResult<Map<String, Map<String, List<DeviceHistoryDto>>>> findDeviceHistoryIndexValueList(@RequestBody DeviceIndexQueryVo deviceQueryVo);

    @PostMapping("findDeviceDiffIndexValueListFeign")
    @ApiOperation("查询设备功能点指定单个时间或时间段内的数据或临近值历史数据(查询索引枪数据)")
    @ApiOperationSupport(order = 6)
    ResponseResult<Map<String, Map<String, List<DeviceHistoryDto>>>> findDeviceDiffIndexValueListFeign(@RequestBody DeviceIndexQueryVo deviceQueryVo);

    @PostMapping("findIndexListHistorValueFeign")
    @ApiOperation("根据多个设备id和多个功能点标识以及多个索引查询历史数据列表")
    @ApiOperationSupport(order = 7)
    ResponseResult<Map<String, Map<String, Map<String, List<DeviceHistoryDto>>>>> findIndexListHistorValueFeign(@RequestBody DeviceIndexListQueryVo deviceQueryVo);

    @PostMapping("findNodeDifHistoryIndexListFeign")
    @ApiOperation("查询设备功能点指定时间段内的last和first历史数据(查询索引数据)")
    @ApiOperationSupport(order = 8)
    ResponseResult<Map<String, Map<String, List<NodeDifHistoryDto>>>> findNodeDifHistoryIndexListFeign(@RequestBody DeviceIndexQueryVo deviceQueryVo);
}
