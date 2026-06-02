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
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiOperationSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@CrossOrigin
@RequestMapping("/feign/together")
@Api(tags = "提供给能源聚合服务调用的远程接口")
@ApiIgnore()
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
    @ApiOperation("启动充电桩")
    @ApiOperationSupport(order = 1)
    public ResponseResult<PileResultDto> pileStart(@RequestBody PileStartVo pileStartVo) {
        return pileCtrlService.pileStart(pileStartVo);
    }
    @PostMapping("pileStop")
    @ApiOperation("停止充电桩")
    @ApiOperationSupport(order = 2)
    public ResponseResult<PileResultDto> pileStop(@RequestBody PileStopVo pileStopVo) {
        return pileCtrlService.pileStop(pileStopVo);
    }
    @PostMapping("powerCtrl")
    @ApiOperation("功率控制")
    @ApiOperationSupport(order = 3)
    public ResponseResult<PileResultDto> powerCtrl(@RequestBody PilePowerCtrlVo pilePowerCtrlVo) {
        return pileCtrlService.pilePowerCtrl(pilePowerCtrlVo);
    }

    @PostMapping("batchPileRateSet")
    @ApiOperation("批量设置费率数据")
    @ApiOperationSupport(order = 4)
    public ResponseResult<List<PileResultDto>> batchPileRateSet(@RequestBody List<PileRateSetVo> pileRateSetVos) {
        return pileBatchCtrlService.batchPileRateSet(pileRateSetVos);
    }

    @PostMapping("gatewayStatus")
    @ApiOperation("网关状态查询")
    @ApiOperationSupport(order = 5)
    public ResponseResult<GatewayStatusSubscribeVo> gatewayStatus(@RequestParam String deviceCode) {
        return gatewayCtrlService.gatewayStatus(deviceCode);
    }

    @PostMapping("reboot")
    @ApiOperation("网关重启")
    @ApiOperationSupport(order = 6)
    public ResponseResult<RebootSubscribeVo> reboot(@RequestParam String deviceCode, @RequestBody RebootPublicVo rebootPublicVo) {
        return gatewayCtrlService.reboot(deviceCode, rebootPublicVo);
    }

    @PostMapping("batchGatewayStatus")
    @ApiOperation("批量网关状态查询")
    @ApiOperationSupport(order = 7)
    public ResponseResult<List<GatewayStatusSubscribeVo>> batchGatewayStatus(@RequestBody List<String> deviceCodes) {
        return gatewayCtrlService.batchGatewayStatus(deviceCodes);
    }

    @PostMapping("platformSet")
    @ApiOperation("设置平台参数")
    @ApiOperationSupport(order = 8)
    public ResponseResult<PlatformSetDto> platformSet(@RequestParam String deviceCode, @RequestBody List<PlatformSetVo> platformSetVos) {
        return gatewayCtrlService.platformSet(deviceCode, platformSetVos);
    }

    @PostMapping("batchPlatformSet")
    @ApiOperation("批量设置平台参数")
    @ApiOperationSupport(order = 9)
    public ResponseResult<Map<String, PlatformSetDto>> batchPlatformSet(@RequestBody Map<String, List<PlatformSetVo>> platformSetVoMap) {
        return gatewayCtrlService.batchPlatformSet(platformSetVoMap);
    }

    @PostMapping("platformStatus")
    @ApiOperation("查询平台设备状态")
    @ApiOperationSupport(order = 10)
    public ResponseResult<PlatformStatusDto> platformStatus(@RequestParam String deviceCode, @RequestBody List<PlatformStatusVo> platformStatusVos) {
        return gatewayCtrlService.platformStatus(deviceCode, platformStatusVos);
    }

    @PostMapping("findAlarmRecordList")
    @ApiOperation("根据查询条件查询设备告警数据")
    @ApiOperationSupport(order = 11)
    public ResponseResult<List<AlarmRecordDto>> findAlarmRecordList(@RequestBody AlarmRecordQueryVo alarmQueryVo) {
        return deviceFeignService.findAlarmRecordList(alarmQueryVo);
    }

    @PostMapping("issuedPolicyParam")
    @ApiOperation("单次下发网关策略配置参数")
    @ApiOperationSupport(order = 12)
    public ResponseResult<GateWayPolicyDto> issuedPolicyParam(@RequestBody GateWayPolicyVo policyIssuedVo) {
        return gatewayCtrlService.issuedPolicyParam(policyIssuedVo);
    }

    @PostMapping("batchIssuedPolicyParam")
    @ApiOperation("批量下发网关策略配置参数")
    @ApiOperationSupport(order = 13)
    public ResponseResult<List<GateWayPolicyDto>> batchIssuedPolicyParam(@RequestBody List<GateWayPolicyVo> policyIssuedVos) {
        return gatewayCtrlService.batchIssuedPolicyParam(policyIssuedVos);
    }

    @PostMapping("batchPileStart")
    @ApiOperation("批量启动多个充电桩")
    @ApiOperationSupport(order = 14)
    public ResponseResult<List<PileResultDto>> batchPileStart(@RequestBody List<PileStartVo> pileStartVos) {
        return pileBatchCtrlService.batchPileStart(pileStartVos);
    }

    @PostMapping("batchPileStop")
    @ApiOperation("批量停止多个充电桩")
    @ApiOperationSupport(order = 15)
    public ResponseResult<List<PileResultDto>> batchPileStop(@RequestBody List<PileStopVo> pileStopVos) {
        return pileBatchCtrlService.batchPileStop(pileStopVos);
    }

    @PostMapping("countDeviceAlarmNum")
    @ApiOperation("统计设备告警次数")
    @ApiOperationSupport(order = 16)
    public ResponseResult<List<AlarmNumDto>> countDeviceAlarmNum(@RequestBody Set<String> deviceCodes, @RequestParam String startTime, @RequestParam String endTime) {
        return deviceFeignService.countDeviceAlarmNum(deviceCodes, startTime, endTime);
    }

    @PostMapping("queryProtocolList")
    @ApiOperation("查询协议日志列表")
    @ApiOperationSupport(order = 17)
    public ResponseResult<PageDto<ProtocolListDto>> queryProtocolList(@RequestBody ProtocolQueryVo protocolQueryVo) {
        return deviceFeignService.queryProtocolList(protocolQueryVo);
    }

    @PostMapping("queryPileLogList")
    @ApiOperation("电桩日志查询")
    @ApiOperationSupport(order = 18)
    public ResponseResult<List<PileLogResultDto>> queryPileLogList(@RequestBody PileLogQueryVo pileLogQueryVo) {
        return pileCtrlService.queryPileLogList(pileLogQueryVo);
    }

    @PostMapping("pileReset")
    @ApiOperation("充电桩复位")
    @ApiOperationSupport(order = 19)
    public ResponseResult<Void> pileReset(@RequestBody PileResetVo pileResetVo) {
        return pileCtrlService.pileReset(pileResetVo);
    }

    @PostMapping("pileSetQr")
    @ApiOperation("设置二维码")
    @ApiOperationSupport(order = 20)
    public ResponseResult<Void> pileSetQr(@RequestBody PileSetQrVo pileSetQrVo) {
        return pileCtrlService.pileSetQr(pileSetQrVo);
    }

}
