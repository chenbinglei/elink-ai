package com.sunmax.protocol.controller;

import com.sunmax.common.dto.protocol.PileLogResultDto;
import com.sunmax.common.dto.protocol.PileResultDto;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.vo.protocol.*;
import com.sunmax.common.vo.protocol.mqtt.inter.VehicleInfoResVo;
import com.sunmax.protocol.service.PileCtrlService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiOperationSupport;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/pileCtrl")
@Api(tags = "提供给设备管理服务调用的远程接口")
public class PileCtrlController {

    @Resource
    private PileCtrlService pileCtrlService;

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

    @PostMapping("pileRateSet")
    @ApiOperation("费率设置")
    @ApiOperationSupport(order = 4)
    public ResponseResult<PileResultDto> pileRateSet(@RequestBody PileRateSetVo pileRateSetVo) {
        return pileCtrlService.pileRateSet(pileRateSetVo);
    }

    @PostMapping("pileUpdate")
    @ApiOperation("单桩升级")
    @ApiOperationSupport(order = 5)
    public ResponseResult<PileResultDto> pileUpdate(@RequestBody PileUpdateVo pileUpdateVo) {
        return pileCtrlService.pileUpdate(pileUpdateVo);
    }

    @PostMapping("subModelInfoReq")
    @ApiOperation("控制板信息请求")
    @ApiOperationSupport(order = 6)
    public ResponseResult<Void> subModelInfoReq(@RequestParam String pileCode) {
        return pileCtrlService.pileSubModelInfoReq(pileCode);
    }

    @PostMapping("vehicleInfoRequest")
    @ApiOperation("车辆信息请求")
    @ApiOperationSupport(order = 7)
    public ResponseResult<VehicleInfoResVo> vehicleInfoRequest(@RequestParam String pileCode, @RequestParam String gunCode) {
        return pileCtrlService.vehicleInfoRequest(pileCode, gunCode);
    }

    @PostMapping("queryPileLogList")
    @ApiOperation("电桩日志查询")
    @ApiOperationSupport(order = 8)
    public ResponseResult<List<PileLogResultDto>> queryPileLogList(@RequestBody PileLogQueryVo pileLogQueryVo) {
        return pileCtrlService.queryPileLogList(pileLogQueryVo);
    }

    @PostMapping("pileReset")
    @ApiOperation("充电桩复位")
    @ApiOperationSupport(order = 9)
    public ResponseResult<Void> pileReset(@RequestBody PileResetVo pileResetVo) {
        return pileCtrlService.pileReset(pileResetVo);
    }

    @PostMapping("pileSetQr")
    @ApiOperation("设置二维码")
    @ApiOperationSupport(order = 10)
    public ResponseResult<Void> pileSetQr(@RequestBody PileSetQrVo pileSetQrVo) {
        return pileCtrlService.pileSetQr(pileSetQrVo);
    }

}
