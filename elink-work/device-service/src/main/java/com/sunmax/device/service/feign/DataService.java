package com.sunmax.device.service.feign;

import com.sunmax.common.dto.data.DeviceHistoryDto;
import com.sunmax.common.dto.data.NodeDifHistoryDto;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.vo.data.DeviceHistoryQueryVo;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 接收数据服务提供的接口
 */
@FeignClient(value = "sunos-data-service", path = "/data/feign/device")
public interface DataService {

    @PostMapping("deleteAllDataStoreTable")
    @Operation(summary = "批量删除数据存储表")
    
    ResponseResult<Void> deleteAllDataStoreTable(@RequestBody Set<String> tableNames);

    @PostMapping("findDeviceHistoryValueList")
    @Operation(summary = "根据多个设备id和多个功能点标识和时间获取设备功能点历史列表")
    
    ResponseResult<Map<String, Map<String, List<DeviceHistoryDto>>>> findDeviceHistoryValueList(@RequestBody DeviceHistoryQueryVo deviceQueryVo);

    @PostMapping("findNodeDifHistoryListFeign")
    @Operation(summary = "查询设备功能点指定时间段内的last和first历史数据")
    
    ResponseResult<Map<String, Map<String, List<NodeDifHistoryDto>>>> findNodeDifHistoryListFeign(@RequestBody DeviceHistoryQueryVo deviceQueryVo);

}
