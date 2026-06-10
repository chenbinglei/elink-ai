package com.sunmax.protocol.controller.feign;

import com.sunmax.common.dto.PageDto;
import com.sunmax.common.dto.protocol.*;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.vo.protocol.*;
import com.sunmax.common.vo.protocol.mqtt.web.from.GatewayStatusSubscribeVo;
import com.sunmax.common.vo.protocol.mqtt.web.from.RebootSubscribeVo;
import com.sunmax.common.vo.protocol.mqtt.web.platform.RebootPublicVo;
import com.sunmax.protocol.service.DeviceFeignService;
import com.sunmax.protocol.service.GatewayCtrlService;
import com.sunmax.protocol.service.PileBatchCtrlService;
import com.sunmax.protocol.service.PileCtrlService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Hidden;

import jakarta.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@CrossOrigin
@RequestMapping("/feign/together")
@Tag(name = "提供给能源聚合服务调用的远程接口")
@Hidden()
public class TogetherFeignController {

    @Autowired
    private PileBatchCtrlService pileBatchCtrlService;

    @Resource
    private PileCtrlService pileCtrlService;

    @Autowired
    private GatewayCtrlService gatewayCtrlService;

    @Autowired
    private DeviceFeignService deviceFeignService;

    @PostMapping("pileStart")
    @Operation(summary = "启动充电桩")
    
    public ResponseResult<PileResultDto> pileStart(@RequestBody PileStartVo pileStartVo) {
        return pileCtrlService.pileStart(pileStartVo);
    }
    @PostMapping("pileStop")
    @Operation(summary = "停止充电桩")
    
    public ResponseResult<PileResultDto> pileStop(@RequestBody PileStopVo pileStopVo) {
        return pileCtrlService.pileStop(pileStopVo);
    }
    @PostMapping("powerCtrl")
    @Operation(summary = "功率控制")
    
    public ResponseResult<PileResultDto> powerCtrl(@RequestBody PilePowerCtrlVo pilePowerCtrlVo) {
        return pileCtrlService.pilePowerCtrl(pilePowerCtrlVo);
    }

    @PostMapping("batchPileRateSet")
    @Operation(summary = "批量设置费率数据")
    
    public ResponseResult<List<PileResultDto>> batchPileRateSet(@RequestBody List<PileRateSetVo> pileRateSetVos) {
        return pileBatchCtrlService.batchPileRateSet(pileRateSetVos);
    }

    @PostMapping("gatewayStatus")
    @Operation(summary = "网关状态查询")
    
    public ResponseResult<GatewayStatusSubscribeVo> gatewayStatus(@RequestParam String deviceCode) {
        return gatewayCtrlService.gatewayStatus(deviceCode);
    }

    @PostMapping("reboot")
    @Operation(summary = "网关重启")
    
    public ResponseResult<RebootSubscribeVo> reboot(@RequestParam String deviceCode, @RequestBody RebootPublicVo rebootPublicVo) {
        return gatewayCtrlService.reboot(deviceCode, rebootPublicVo);
    }

    @PostMapping("batchGatewayStatus")
    @Operation(summary = "批量网关状态查询")
    
    public ResponseResult<List<GatewayStatusSubscribeVo>> batchGatewayStatus(@RequestBody List<String> deviceCodes) {
        return gatewayCtrlService.batchGatewayStatus(deviceCodes);
    }

    @PostMapping("platformSet")
    @Operation(summary = "设置平台参数")
    
    public ResponseResult<PlatformSetDto> platformSet(@RequestParam String deviceCode, @RequestBody List<PlatformSetVo> platformSetVos) {
        return gatewayCtrlService.platformSet(deviceCode, platformSetVos);
    }

    @PostMapping("batchPlatformSet")
    @Operation(summary = "批量设置平台参数")
    
    public ResponseResult<Map<String, PlatformSetDto>> batchPlatformSet(@RequestBody Map<String, List<PlatformSetVo>> platformSetVoMap) {
        return gatewayCtrlService.batchPlatformSet(platformSetVoMap);
    }

    @PostMapping("platformStatus")
    @Operation(summary = "查询平台设备状态")
    
    public ResponseResult<PlatformStatusDto> platformStatus(@RequestParam String deviceCode, @RequestBody List<PlatformStatusVo> platformStatusVos) {
        return gatewayCtrlService.platformStatus(deviceCode, platformStatusVos);
    }

    @PostMapping("findAlarmRecordList")
    @Operation(summary = "根据查询条件查询设备告警数据")
    
    public ResponseResult<List<AlarmRecordDto>> findAlarmRecordList(@RequestBody AlarmRecordQueryVo alarmQueryVo) {
        return deviceFeignService.findAlarmRecordList(alarmQueryVo);
    }

    @PostMapping("issuedPolicyParam")
    @Operation(summary = "单次下发网关策略配置参数")
    
    public ResponseResult<GateWayPolicyDto> issuedPolicyParam(@RequestBody GateWayPolicyVo policyIssuedVo) {
        return gatewayCtrlService.issuedPolicyParam(policyIssuedVo);
    }

    @PostMapping("batchIssuedPolicyParam")
    @Operation(summary = "批量下发网关策略配置参数")
    
    public ResponseResult<List<GateWayPolicyDto>> batchIssuedPolicyParam(@RequestBody List<GateWayPolicyVo> policyIssuedVos) {
        return gatewayCtrlService.batchIssuedPolicyParam(policyIssuedVos);
    }

    @PostMapping("batchPileStart")
    @Operation(summary = "批量启动多个充电桩")
    
    public ResponseResult<List<PileResultDto>> batchPileStart(@RequestBody List<PileStartVo> pileStartVos) {
        return pileBatchCtrlService.batchPileStart(pileStartVos);
    }

    @PostMapping("batchPileStop")
    @Operation(summary = "批量停止多个充电桩")
    
    public ResponseResult<List<PileResultDto>> batchPileStop(@RequestBody List<PileStopVo> pileStopVos) {
        return pileBatchCtrlService.batchPileStop(pileStopVos);
    }

    @PostMapping("countDeviceAlarmNum")
    @Operation(summary = "统计设备告警次数")
    
    public ResponseResult<List<AlarmNumDto>> countDeviceAlarmNum(@RequestBody Set<String> deviceCodes, @RequestParam String startTime, @RequestParam String endTime) {
        return deviceFeignService.countDeviceAlarmNum(deviceCodes, startTime, endTime);
    }

    @PostMapping("queryProtocolList")
    @Operation(summary = "查询协议日志列表")
    
    public ResponseResult<PageDto<ProtocolListDto>> queryProtocolList(@RequestBody ProtocolQueryVo protocolQueryVo) {
        return deviceFeignService.queryProtocolList(protocolQueryVo);
    }

    @PostMapping("queryPileLogList")
    @Operation(summary = "电桩日志查询")
    
    public ResponseResult<List<PileLogResultDto>> queryPileLogList(@RequestBody PileLogQueryVo pileLogQueryVo) {
        return pileCtrlService.queryPileLogList(pileLogQueryVo);
    }

    @PostMapping("pileReset")
    @Operation(summary = "充电桩复位")
    
    public ResponseResult<Void> pileReset(@RequestBody PileResetVo pileResetVo) {
        return pileCtrlService.pileReset(pileResetVo);
    }

    @PostMapping("pileSetQr")
    @Operation(summary = "设置二维码")
    
    public ResponseResult<Void> pileSetQr(@RequestBody PileSetQrVo pileSetQrVo) {
        return pileCtrlService.pileSetQr(pileSetQrVo);
    }

}
