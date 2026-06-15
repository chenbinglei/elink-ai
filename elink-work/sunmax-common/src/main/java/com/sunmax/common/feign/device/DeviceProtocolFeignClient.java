package com.sunmax.common.feign.device;

import com.sunmax.common.dto.protocol.AlarmRecordDto;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.vo.protocol.AlarmRecordQueryVo;
import com.sunmax.common.vo.protocol.PileBatchUpdateVo;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import com.sunmax.common.feign.fallback.GenericFeignFallbackFactory;

/**
 * 接收数据服务提供的接口
 */
@FeignClient(value = "sunos-protocol-service", path = "/protocol/feign/device", fallbackFactory = GenericFeignFallbackFactory.class)
public interface DeviceProtocolFeignClient {

    @PostMapping("addDeviceTopic")
    @Operation(summary = "添加设备主题")
    
    ResponseResult<Void> addDeviceTopic(@RequestParam String deviceCode);

    @PostMapping("deleteDeviceTopic")
    @Operation(summary = "删除设备主题")
    
    ResponseResult<Void> deleteDeviceTopic(@RequestParam String deviceCode);

    @PostMapping("findAlarmRecordList")
    @Operation(summary = "根据多个设备编号查询设备告警数据")
    
    ResponseResult<List<AlarmRecordDto>> findAlarmRecordList(@RequestBody AlarmRecordQueryVo alarmQueryVo);

    @PostMapping("deleteAllAlarmRecord")
    @Operation(summary = "根据多个设备编号删除告警记录")
    
    ResponseResult<Void> deleteAllAlarmRecord(@RequestBody Set<String> deviceCodes);

    @PostMapping("pileBatchUpdate")
    @Operation(summary = "批量对多个充电桩升级")
    
    ResponseResult<Boolean> batchPileUpdate(@RequestBody PileBatchUpdateVo pileBatchUpdateVos);

    @PostMapping("updateEventIgnoreStatus")
    @Operation(summary = "修改设备事件忽略状态")
    
    ResponseResult<Void> updateEventIgnoreStatus(@RequestParam String id, @RequestParam Integer ignoreStatus);

}
