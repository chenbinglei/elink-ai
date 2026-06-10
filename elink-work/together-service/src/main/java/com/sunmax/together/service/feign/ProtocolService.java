package com.sunmax.together.service.feign;

import com.sunmax.common.dto.PageDto;
import com.sunmax.common.dto.protocol.*;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.vo.protocol.*;
import com.sunmax.common.vo.protocol.mqtt.web.from.GatewayStatusSubscribeVo;
import com.sunmax.common.vo.protocol.mqtt.web.from.RebootSubscribeVo;
import com.sunmax.common.vo.protocol.mqtt.web.platform.RebootPublicVo;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 接收设备服务提供的接口
 */
@FeignClient(value = "sunos-protocol-service", path = "/protocol/feign/together")
//@FeignClient(value = "sunos-protocol-service-cbl",url = "http://121.41.109.130:60005")
public interface ProtocolService {

    @PostMapping("pileStart")
    @Operation(summary = "启动充电桩")
    
    ResponseResult<PileResultDto> pileStart(@RequestBody PileStartVo pileStartVo);

    @PostMapping("pileStop")
    @Operation(summary = "停止充电桩")
    
    ResponseResult<PileResultDto> pileStop(@RequestBody PileStopVo pileStopVo);

    @PostMapping("powerCtrl")
    @Operation(summary = "功率控制")
    
    ResponseResult<PileResultDto> powerCtrl(@RequestBody PilePowerCtrlVo pilePowerCtrlVo);

    @PostMapping("batchPileRateSet")
    @Operation(summary = "批量设置费率数据")
    
    ResponseResult<List<PileResultDto>> batchPileRateSet(@RequestBody List<PileRateSetVo> pileRateSetVos);

    @PostMapping("gatewayStatus")
    @Operation(summary = "网关状态查询")
    
    ResponseResult<GatewayStatusSubscribeVo> gatewayStatus(@RequestParam String deviceCode);

    @PostMapping("reboot")
    @Operation(summary = "网关重启")
    
    ResponseResult<RebootSubscribeVo> reboot(@RequestParam String deviceCode, @RequestBody RebootPublicVo rebootPublicVo);

    @PostMapping("batchGatewayStatus")
    @Operation(summary = "批量网关状态查询")
    
    ResponseResult<List<GatewayStatusSubscribeVo>> batchGatewayStatus(@RequestBody List<String> deviceCodes);

    @PostMapping("platformSet")
    @Operation(summary = "设置平台参数")
    
    ResponseResult<PlatformSetDto> platformSet(@RequestParam String deviceCode, @RequestBody List<PlatformSetVo> platformSetVos);

    @PostMapping("batchPlatformSet")
    @Operation(summary = "批量设置平台参数")
    
    ResponseResult<Map<String, PlatformSetDto>> batchPlatformSet(@RequestBody Map<String, List<PlatformSetVo>> platformSetVoMap);

    @PostMapping("platformStatus")
    @Operation(summary = "查询平台设备状态")
    
    ResponseResult<PlatformStatusDto> platformStatus(@RequestParam String deviceCode, @RequestBody List<PlatformStatusVo> platformStatusVos);

    @PostMapping("findAlarmRecordList")
    @Operation(summary = "根据查询条件查询设备告警数据")
    
    ResponseResult<List<AlarmRecordDto>> findAlarmRecordList(@RequestBody AlarmRecordQueryVo alarmQueryVo);

    @PostMapping("issuedPolicyParam")
    @Operation(summary = "单次下发网关策略配置参数")
    
    ResponseResult<GateWayPolicyDto> issuedPolicyParam(@RequestBody GateWayPolicyVo policyIssuedVo);

    @PostMapping("batchIssuedPolicyParam")
    @Operation(summary = "批量下发网关策略配置参数")
    
    ResponseResult<List<GateWayPolicyDto>> batchIssuedPolicyParam(@RequestBody List<GateWayPolicyVo> policyIssuedVos);

    @PostMapping("batchPileStart")
    @Operation(summary = "批量启动多个充电桩")
    
    ResponseResult<List<PileResultDto>> batchPileStart(@RequestBody List<PileStartVo> pileStartVos);

    @PostMapping("batchPileStop")
    @Operation(summary = "批量停止多个充电桩")
    
    ResponseResult<List<PileResultDto>> batchPileStop(@RequestBody List<PileStopVo> pileStopVos);

    @PostMapping("countDeviceAlarmNum")
    @Operation(summary = "统计设备告警次数")
    
    ResponseResult<List<AlarmNumDto>> countDeviceAlarmNum(@RequestBody Set<String> deviceCodes, @RequestParam String startTime, @RequestParam String endTime);

    @PostMapping("queryProtocolList")
    @Operation(summary = "查询协议日志列表")
    
    ResponseResult<PageDto<ProtocolListDto>> queryProtocolList(@RequestBody ProtocolQueryVo protocolQueryVo);

    @PostMapping("queryPileLogList")
    @Operation(summary = "电桩日志查询")
    
    ResponseResult<List<PileLogResultDto>> queryPileLogList(@RequestBody PileLogQueryVo pileLogQueryVo);

    @PostMapping("pileReset")
    @Operation(summary = "充电桩复位")
    
    ResponseResult<Void> pileReset(@RequestBody PileResetVo pileResetVo);

    @PostMapping("pileSetQr")
    @Operation(summary = "设置二维码")
    
    ResponseResult<Void> pileSetQr(@RequestBody PileSetQrVo pileSetQrVo);


}
