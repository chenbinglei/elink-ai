package com.sunmax.protocol.controller;

import com.sunmax.common.dto.protocol.PileLogResultDto;
import com.sunmax.common.dto.protocol.PileResultDto;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.vo.protocol.*;
import com.sunmax.common.vo.protocol.mqtt.inter.VehicleInfoResVo;
import com.sunmax.protocol.service.PileCtrlService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/pileCtrl")
@Tag(name = "提供给设备管理服务调用的远程接口")
public class PileCtrlController {

    @Resource
    private PileCtrlService pileCtrlService;

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

    @PostMapping("pileRateSet")
    @Operation(summary = "费率设置")
    
    public ResponseResult<PileResultDto> pileRateSet(@RequestBody PileRateSetVo pileRateSetVo) {
        return pileCtrlService.pileRateSet(pileRateSetVo);
    }

    @PostMapping("pileUpdate")
    @Operation(summary = "单桩升级")
    
    public ResponseResult<PileResultDto> pileUpdate(@RequestBody PileUpdateVo pileUpdateVo) {
        return pileCtrlService.pileUpdate(pileUpdateVo);
    }

    @PostMapping("subModelInfoReq")
    @Operation(summary = "控制板信息请求")
    
    public ResponseResult<Void> subModelInfoReq(@RequestParam String pileCode) {
        return pileCtrlService.pileSubModelInfoReq(pileCode);
    }

    @PostMapping("vehicleInfoRequest")
    @Operation(summary = "车辆信息请求")
    
    public ResponseResult<VehicleInfoResVo> vehicleInfoRequest(@RequestParam String pileCode, @RequestParam String gunCode) {
        return pileCtrlService.vehicleInfoRequest(pileCode, gunCode);
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
