package com.sunmax.together.service.feign;

import com.sunmax.common.dto.PageDto;
import com.sunmax.common.dto.protocol.*;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.vo.protocol.*;
import com.sunmax.common.vo.protocol.mqtt.web.from.GatewayStatusSubscribeVo;
import com.sunmax.common.vo.protocol.mqtt.web.from.RebootSubscribeVo;
import com.sunmax.common.vo.protocol.mqtt.web.platform.RebootPublicVo;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiOperationSupport;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 接收设备服务提供的接口
 */
@FeignClient(value = "sunos-protocol-service")
//@FeignClient(value = "sunos-protocol-service-cbl",url = "http://121.41.109.130:60005")
@RestController
@RequestMapping("/protocol/feign/together")
public interface ProtocolService {

    @PostMapping("pileStart")
    @ApiOperation("启动充电桩")
    @ApiOperationSupport(order = 1)
    ResponseResult<PileResultDto> pileStart(@RequestBody PileStartVo pileStartVo);

    @PostMapping("pileStop")
    @ApiOperation("停止充电桩")
    @ApiOperationSupport(order = 2)
    ResponseResult<PileResultDto> pileStop(@RequestBody PileStopVo pileStopVo);

    @PostMapping("powerCtrl")
    @ApiOperation("功率控制")
    @ApiOperationSupport(order = 3)
    ResponseResult<PileResultDto> powerCtrl(@RequestBody PilePowerCtrlVo pilePowerCtrlVo);

    @PostMapping("batchPileRateSet")
    @ApiOperation("批量设置费率数据")
    @ApiOperationSupport(order = 4)
    ResponseResult<List<PileResultDto>> batchPileRateSet(@RequestBody List<PileRateSetVo> pileRateSetVos);

    @PostMapping("gatewayStatus")
    @ApiOperation("网关状态查询")
    @ApiOperationSupport(order = 5)
    ResponseResult<GatewayStatusSubscribeVo> gatewayStatus(@RequestParam String deviceCode);

    @PostMapping("reboot")
    @ApiOperation("网关重启")
    @ApiOperationSupport(order = 6)
    ResponseResult<RebootSubscribeVo> reboot(@RequestParam String deviceCode, @RequestBody RebootPublicVo rebootPublicVo);

    @PostMapping("batchGatewayStatus")
    @ApiOperation("批量网关状态查询")
    @ApiOperationSupport(order = 7)
    ResponseResult<List<GatewayStatusSubscribeVo>> batchGatewayStatus(@RequestBody List<String> deviceCodes);

    @PostMapping("platformSet")
    @ApiOperation("设置平台参数")
    @ApiOperationSupport(order = 8)
    ResponseResult<PlatformSetDto> platformSet(@RequestParam String deviceCode, @RequestBody List<PlatformSetVo> platformSetVos);

    @PostMapping("batchPlatformSet")
    @ApiOperation("批量设置平台参数")
    @ApiOperationSupport(order = 9)
    ResponseResult<Map<String, PlatformSetDto>> batchPlatformSet(@RequestBody Map<String, List<PlatformSetVo>> platformSetVoMap);

    @PostMapping("platformStatus")
    @ApiOperation("查询平台设备状态")
    @ApiOperationSupport(order = 10)
    ResponseResult<PlatformStatusDto> platformStatus(@RequestParam String deviceCode, @RequestBody List<PlatformStatusVo> platformStatusVos);

    @PostMapping("findAlarmRecordList")
    @ApiOperation("根据查询条件查询设备告警数据")
    @ApiOperationSupport(order = 11)
    ResponseResult<List<AlarmRecordDto>> findAlarmRecordList(@RequestBody AlarmRecordQueryVo alarmQueryVo);

    @PostMapping("issuedPolicyParam")
    @ApiOperation("单次下发网关策略配置参数")
    @ApiOperationSupport(order = 12)
    ResponseResult<GateWayPolicyDto> issuedPolicyParam(@RequestBody GateWayPolicyVo policyIssuedVo);

    @PostMapping("batchIssuedPolicyParam")
    @ApiOperation("批量下发网关策略配置参数")
    @ApiOperationSupport(order = 13)
    ResponseResult<List<GateWayPolicyDto>> batchIssuedPolicyParam(@RequestBody List<GateWayPolicyVo> policyIssuedVos);

    @PostMapping("batchPileStart")
    @ApiOperation("批量启动多个充电桩")
    @ApiOperationSupport(order = 14)
    ResponseResult<List<PileResultDto>> batchPileStart(@RequestBody List<PileStartVo> pileStartVos);

    @PostMapping("batchPileStop")
    @ApiOperation("批量停止多个充电桩")
    @ApiOperationSupport(order = 15)
    ResponseResult<List<PileResultDto>> batchPileStop(@RequestBody List<PileStopVo> pileStopVos);

    @PostMapping("countDeviceAlarmNum")
    @ApiOperation("统计设备告警次数")
    @ApiOperationSupport(order = 16)
    ResponseResult<List<AlarmNumDto>> countDeviceAlarmNum(@RequestBody Set<String> deviceCodes, @RequestParam String startTime, @RequestParam String endTime);

    @PostMapping("queryProtocolList")
    @ApiOperation("查询协议日志列表")
    @ApiOperationSupport(order = 17)
    ResponseResult<PageDto<ProtocolListDto>> queryProtocolList(@RequestBody ProtocolQueryVo protocolQueryVo);

    @PostMapping("queryPileLogList")
    @ApiOperation("电桩日志查询")
    @ApiOperationSupport(order = 18)
    ResponseResult<List<PileLogResultDto>> queryPileLogList(@RequestBody PileLogQueryVo pileLogQueryVo);

    @PostMapping("pileReset")
    @ApiOperation("充电桩复位")
    @ApiOperationSupport(order = 19)
    ResponseResult<Void> pileReset(@RequestBody PileResetVo pileResetVo);

    @PostMapping("pileSetQr")
    @ApiOperation("设置二维码")
    @ApiOperationSupport(order = 20)
    ResponseResult<Void> pileSetQr(@RequestBody PileSetQrVo pileSetQrVo);


}
