package com.sunmax.protocol.controller;

import com.sunmax.common.dto.protocol.GateWayControlDto;
import com.sunmax.common.dto.protocol.GateWayPolicyDto;
import com.sunmax.common.dto.protocol.PlatformStatusDto;
import com.sunmax.common.dto.protocol.PlatformSetDto;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.vo.protocol.GateWayControlVo;
import com.sunmax.common.vo.protocol.GateWayPolicyVo;
import com.sunmax.common.vo.protocol.PlatformSetVo;
import com.sunmax.common.vo.protocol.PlatformStatusVo;
import com.sunmax.common.vo.protocol.mqtt.web.from.*;
import com.sunmax.common.vo.protocol.mqtt.web.platform.LicensePublishVo;
import com.sunmax.common.vo.protocol.mqtt.web.platform.RebootPublicVo;
import com.sunmax.protocol.service.GatewayCtrlService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiOperationSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;
import java.util.Map;

/**
 * 提供外部调用 网关控制u
 */
@RestController
@CrossOrigin
@RequestMapping("/feign/gateWayCtrl")
@Api(tags = "提供外部服务控制网关接口")
@ApiIgnore
public class GatewayCtrlController {
    
    @Autowired
    private GatewayCtrlService gatewayCtrlService;

    @PostMapping("loadParamSet")
    @ApiOperation("网关参数下发")
    @ApiOperationSupport(order = 1)
    public ResponseResult<GateWayControlDto> loadParamSet(@RequestBody GateWayControlVo gateWayControlVo) {
        return gatewayCtrlService.loadParamSet(gateWayControlVo);
    }

    @PostMapping("issuedPolicyParam")
    @ApiOperation("单次下发网关策略配置参数")
    @ApiOperationSupport(order = 2)
    public ResponseResult<GateWayPolicyDto> issuedPolicyParam(@RequestBody GateWayPolicyVo policyIssuedVo) {
        return gatewayCtrlService.issuedPolicyParam(policyIssuedVo);
    }

    @PostMapping("batchIssuedPolicyParam")
    @ApiOperation("批量下发网关策略配置参数")
    @ApiOperationSupport(order = 3)
    public ResponseResult<List<GateWayPolicyDto>> batchIssuedPolicyParam(@RequestBody List<GateWayPolicyVo> policyIssuedVos) {
        return gatewayCtrlService.batchIssuedPolicyParam(policyIssuedVos);
    }

    @PostMapping("gatewayStatus")
    @ApiOperation("网关状态查询")
    @ApiOperationSupport(order = 4)
    public ResponseResult<GatewayStatusSubscribeVo> gatewayStatus(@RequestParam String deviceCode) {
        return gatewayCtrlService.gatewayStatus(deviceCode);
    }

    @PostMapping("batchGatewayStatus")
    @ApiOperation("批量网关状态查询")
    @ApiOperationSupport(order = 4)
    public ResponseResult<List<GatewayStatusSubscribeVo>> batchGatewayStatus(@RequestBody List<String> deviceCodes) {
        return gatewayCtrlService.batchGatewayStatus(deviceCodes);
    }

    @PostMapping("serviceList")
    @ApiOperation("网关服务列表查询")
    @ApiOperationSupport(order = 5)
    public ResponseResult<List<ServiceListSubscribeVo>> serviceList(@RequestParam String deviceCode) {
        return gatewayCtrlService.serviceList(deviceCode);
    }

    @PostMapping("syncClock")
    @ApiOperation("网关同步时钟")
    @ApiOperationSupport(order = 6)
    public ResponseResult<SyncClockSubscribeVo> syncClock(@RequestParam String deviceCode, @RequestParam Integer type) {
        return gatewayCtrlService.syncClock(deviceCode, type);
    }

    @PostMapping("reboot")
    @ApiOperation("网关重启")
    @ApiOperationSupport(order = 7)
    public ResponseResult<RebootSubscribeVo> reboot(@RequestParam String deviceCode, @RequestBody RebootPublicVo rebootPublicVo) {
        return gatewayCtrlService.reboot(deviceCode, rebootPublicVo);
    }

    @PostMapping("licenseStatus")
    @ApiOperation("网关许可状态查询")
    @ApiOperationSupport(order = 8)
    public ResponseResult<LicenseSubscribeVo> licenseStatus(@RequestParam String deviceCode) {
        return gatewayCtrlService.licenseStatus(deviceCode);
    }

    @PostMapping("licenseKey")
    @ApiOperation("获取设备key")
    @ApiOperationSupport(order = 9)
    public ResponseResult<LicenseSubscribeVo> licenseKey(@RequestParam String deviceCode) {
        return gatewayCtrlService.licenseKey(deviceCode);
    }

    @PostMapping("license")
    @ApiOperation("下发许可")
    @ApiOperationSupport(order = 10)
    public ResponseResult<LicenseSubscribeVo> license(@RequestParam String deviceCode, @RequestBody LicensePublishVo licensePublicVo) {
        return gatewayCtrlService.license(deviceCode, licensePublicVo);
    }

    @PostMapping("platformSet")
    @ApiOperation("设置平台参数")
    @ApiOperationSupport(order = 11)
    public ResponseResult<PlatformSetDto> platformSet(@RequestParam String deviceCode, @RequestBody List<PlatformSetVo> platformSetVos) {
        return gatewayCtrlService.platformSet(deviceCode, platformSetVos);
    }

    @PostMapping("batchPlatformSet")
    @ApiOperation("批量设置平台参数")
    @ApiOperationSupport(order = 12)
    public ResponseResult<Map<String, PlatformSetDto>> batchPlatformSet(@RequestBody Map<String, List<PlatformSetVo>> platformSetVoMap) {
        return gatewayCtrlService.batchPlatformSet(platformSetVoMap);
    }

    @PostMapping("platformStatus")
    @ApiOperation("查询平台设备状态")
    @ApiOperationSupport(order = 13)
    public ResponseResult<PlatformStatusDto> platformStatus(@RequestParam String deviceCode, @RequestBody List<PlatformStatusVo> platformStatusVos) {
        return gatewayCtrlService.platformStatus(deviceCode, platformStatusVos);
    }

}
