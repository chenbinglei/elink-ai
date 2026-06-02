package com.sunmax.device.service.feign;

import com.sunmax.common.dto.protocol.AlarmRecordDto;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.vo.protocol.AlarmRecordQueryVo;
import com.sunmax.common.vo.protocol.PileBatchUpdateVo;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiOperationSupport;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

/**
 * 接收数据服务提供的接口
 */
@FeignClient(value = "sunos-protocol-service")
@RestController
@RequestMapping("/protocol/feign/device")
public interface ProtocolService {

    @PostMapping("addDeviceTopic")
    @ApiOperation("添加设备主题")
    @ApiOperationSupport(order = 1)
    ResponseResult<Void> addDeviceTopic(@RequestParam String deviceCode);

    @PostMapping("deleteDeviceTopic")
    @ApiOperation("删除设备主题")
    @ApiOperationSupport(order = 2)
    ResponseResult<Void> deleteDeviceTopic(@RequestParam String deviceCode);

    @PostMapping("findAlarmRecordList")
    @ApiOperation("根据多个设备编号查询设备告警数据")
    @ApiOperationSupport(order = 3)
    ResponseResult<List<AlarmRecordDto>> findAlarmRecordList(@RequestBody AlarmRecordQueryVo alarmQueryVo);

    @PostMapping("deleteAllAlarmRecord")
    @ApiOperation("根据多个设备编号删除告警记录")
    @ApiOperationSupport(order = 4)
    ResponseResult<Void> deleteAllAlarmRecord(@RequestBody Set<String> deviceCodes);

    @PostMapping("pileBatchUpdate")
    @ApiOperation("批量对多个充电桩升级")
    @ApiOperationSupport(order = 5)
    ResponseResult<Void> batchPileUpdate(@RequestBody PileBatchUpdateVo pileBatchUpdateVos);

    @PostMapping("updateEventIgnoreStatus")
    @ApiOperation("修改设备事件忽略状态")
    @ApiOperationSupport(order = 6)
    ResponseResult<Void> updateEventIgnoreStatus(@RequestParam String id, @RequestParam Integer ignoreStatus);

}
