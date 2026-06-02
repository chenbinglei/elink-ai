package com.sunmax.protocol.controller.feign;

import com.sunmax.common.dto.protocol.AlarmRecordDto;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.vo.protocol.AlarmRecordQueryVo;
import com.sunmax.common.vo.protocol.PileBatchUpdateVo;
import com.sunmax.protocol.service.DeviceFeignService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;
import java.util.Set;

@RestController
@CrossOrigin
@RequestMapping("/feign/device")
@Api(tags = "提供给设备管理服务调用的远程接口")
@ApiIgnore()
public class DeviceFeignController {

    @Autowired
    private DeviceFeignService deviceFeignService;

    @PostMapping("addDeviceTopic")
    @ApiOperation("添加设备主题")
    @ApiOperationSupport(order = 1)
    public ResponseResult<Void> addDeviceTopic(@RequestParam String deviceCode) {
        return deviceFeignService.addDeviceTopic(deviceCode);
    }

    @PostMapping("deleteDeviceTopic")
    @ApiOperation("删除设备主题")
    @ApiOperationSupport(order = 2)
    public ResponseResult<Void> deleteDeviceTopic(@RequestParam String deviceCode) {
        return deviceFeignService.deleteDeviceTopic(deviceCode);
    }

    @PostMapping("findAlarmRecordList")
    @ApiOperation("根据查询条件查询设备告警数据")
    @ApiOperationSupport(order = 3)
    public ResponseResult<List<AlarmRecordDto>> findAlarmRecordList(@RequestBody AlarmRecordQueryVo alarmQueryVo) {
        return deviceFeignService.findAlarmRecordList(alarmQueryVo);
    }

    @PostMapping("deleteAllAlarmRecord")
    @ApiOperation("根据多个设备编号删除告警记录")
    @ApiOperationSupport(order = 4)
    public ResponseResult<Void> deleteAllAlarmRecord(@RequestBody Set<String> deviceCodes) {
        return deviceFeignService.deleteAllAlarmRecord(deviceCodes);
    }

    @PostMapping("pileBatchUpdate")
    @ApiOperation("批量对多个充电桩升级")
    @ApiOperationSupport(order = 5)
    public ResponseResult<Boolean> batchPileUpdate(@RequestBody PileBatchUpdateVo pileBatchUpdateVos) {
        return deviceFeignService.batchPileUpdate(pileBatchUpdateVos);
    }

    @PostMapping("updateEventIgnoreStatus")
    @ApiOperation("修改设备事件忽略状态")
    @ApiOperationSupport(order = 6)
    public ResponseResult<Void> updateEventIgnoreStatus(@RequestParam String id, @RequestParam Integer ignoreStatus) {
        return deviceFeignService.updateEventIgnoreStatus(id, ignoreStatus);
    }

}
