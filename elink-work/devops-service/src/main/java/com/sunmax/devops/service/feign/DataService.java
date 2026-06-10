package com.sunmax.devops.service.feign;


import com.sunmax.common.dto.data.DeviceHistoryDto;
import com.sunmax.common.dto.data.NodeDifHistoryDto;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.vo.data.DeviceCountQueryVo;
import com.sunmax.common.vo.data.DeviceHistoryQueryVo;
import com.sunmax.common.vo.data.DeviceIndexQueryVo;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

@FeignClient(value = "sunos-data-service", path = "/data/feign/together")
//@FeignClient(value = "sunos-data-service-cbl",url = "http://121.41.109.130:60004")
public interface DataService {

    @PostMapping("findDeviceHistoryValueListFeign")
    @Operation(summary = "根据多个设备id和多个功能点标识和时间获取设备功能点历史列表")
    
    ResponseResult<Map<String, Map<String, List<DeviceHistoryDto>>>> findDeviceHistoryValueList(@RequestBody DeviceHistoryQueryVo deviceQueryVo);

    @PostMapping("findDeviceHistoryIndexValueListFeign")
    @Operation(summary = "根据多个设备id和多个功能点标识和时间获取设备功能点历史列表")
    
    ResponseResult<Map<String, Map<String, List<DeviceHistoryDto>>>> findDeviceHistoryIndexValueList(@RequestBody DeviceIndexQueryVo deviceQueryVo);

    @PostMapping("findNodeDifHistoryListFeign")
    @Operation(summary = "查询设备功能点指定时间段内的last和first历史数据")
    
    ResponseResult<Map<String, Map<String, List<NodeDifHistoryDto>>>> findNodeDifHistoryListFeign(@RequestBody DeviceHistoryQueryVo deviceQueryVo);

    @PostMapping("findDeviceCountFunListFeign")
    @Operation(summary = "查询指定设备指定功能点和时间段内设备统计值函数历史数据")
    
    ResponseResult<Map<String, Map<String, List<DeviceHistoryDto>>>> findDeviceCountFunListFeign(@RequestBody DeviceCountQueryVo deviceCountQueryVo);
}
