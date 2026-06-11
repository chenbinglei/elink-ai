package com.sunmax.protocol.controller.feign;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

import com.sunmax.common.dto.protocol.AlarmRecordDto;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.vo.protocol.AlarmRecordQueryVo;
import com.sunmax.common.vo.protocol.PileBatchUpdateVo;
import com.sunmax.protocol.service.DeviceFeignService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Hidden;

import java.util.List;
import java.util.Set;
import com.sunmax.common.feign.device.DeviceProtocolFeignClient;

@RestController
@CrossOrigin
@RequestMapping("/feign/device")
@Tag(name = "提供给设备管理服务调用的远程接口")
@Hidden()
public class DeviceFeignEndpoint implements DeviceProtocolFeignClient {

    @Autowired
    private DeviceFeignService deviceFeignService;

    @PostMapping("addDeviceTopic")
    @Operation(summary = "添加设备主题")
    
    @Override
    public ResponseResult<Void> addDeviceTopic(@RequestParam String deviceCode) {
        return deviceFeignService.addDeviceTopic(deviceCode);
    }

    @PostMapping("deleteDeviceTopic")
    @Operation(summary = "删除设备主题")
    
    @Override
    public ResponseResult<Void> deleteDeviceTopic(@RequestParam String deviceCode) {
        return deviceFeignService.deleteDeviceTopic(deviceCode);
    }

    @PostMapping("findAlarmRecordList")
    @Operation(summary = "根据查询条件查询设备告警数据")
    
    public ResponseResult<List<AlarmRecordDto>> findAlarmRecordList(@RequestBody AlarmRecordQueryVo alarmQueryVo) {
        return deviceFeignService.findAlarmRecordList(alarmQueryVo);
    }

    @PostMapping("deleteAllAlarmRecord")
    @Operation(summary = "根据多个设备编号删除告警记录")
    
    @Override
    public ResponseResult<Void> deleteAllAlarmRecord(@RequestBody Set<String> deviceCodes) {
        return deviceFeignService.deleteAllAlarmRecord(deviceCodes);
    }

    @PostMapping("pileBatchUpdate")
    @Operation(summary = "批量对多个充电桩升级")
    
    @Override
    public ResponseResult<Boolean> batchPileUpdate(@RequestBody PileBatchUpdateVo pileBatchUpdateVos) {
        return deviceFeignService.batchPileUpdate(pileBatchUpdateVos);
    }

    @PostMapping("updateEventIgnoreStatus")
    @Operation(summary = "修改设备事件忽略状态")
    
    @Override
    public ResponseResult<Void> updateEventIgnoreStatus(@RequestParam String id, @RequestParam Integer ignoreStatus) {
        return deviceFeignService.updateEventIgnoreStatus(id, ignoreStatus);
    }

}
