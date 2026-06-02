package com.sunmax.protocol.controller;

import com.sunmax.common.dto.protocol.PileResultDto;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.vo.protocol.*;
import com.sunmax.protocol.service.PileBatchCtrlService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiOperationSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

/**
 * 批量对充电桩控制层
 */
@RestController
@CrossOrigin
@RequestMapping("pileBatchCtrl")
@Api(tags = "批量对充电桩控制层")
@ApiIgnore
public class PileBatchCtrlController {

    @Autowired
    private PileBatchCtrlService pileBatchCtrlService;

    @PostMapping("batchPileStart")
    @ApiOperation("批量启动多个充电桩")
    @ApiOperationSupport(order = 1)
    public ResponseResult<List<PileResultDto>> batchPileStart(@RequestBody List<PileStartVo> pileStartVos) {
        return pileBatchCtrlService.batchPileStart(pileStartVos);
    }

    @PostMapping("batchPileStop")
    @ApiOperation("批量停止多个充电桩")
    @ApiOperationSupport(order = 2)
    public ResponseResult<List<PileResultDto>> batchPileStop(@RequestBody List<PileStopVo> pileStopVos) {
        return pileBatchCtrlService.batchPileStop(pileStopVos);
    }

    @PostMapping("batchPilePowerCtrl")
    @ApiOperation("批量对多个充电桩功率控制")
    @ApiOperationSupport(order = 3)
    public ResponseResult<List<PileResultDto>> batchPilePowerCtrl(@RequestBody List<PilePowerCtrlVo> pilePowerCtrlVos) {
        return pileBatchCtrlService.batchPilePowerCtrl(pilePowerCtrlVos);
    }

    @PostMapping("batchPileRateSet")
    @ApiOperation("批量设置费率数据")
    @ApiOperationSupport(order = 4)
    public ResponseResult<List<PileResultDto>> batchPileRateSet(@RequestBody List<PileRateSetVo> pileRateSetVos) {
        return pileBatchCtrlService.batchPileRateSet(pileRateSetVos);
    }

    @PostMapping("pileBatchUpdate")
    @ApiOperation("批量对多个充电桩升级")
    @ApiOperationSupport(order = 5)
    public ResponseResult<List<PileResultDto>> batchPileUpdate(@RequestBody PileBatchUpdateVo pileBatchUpdateVos) {
        return pileBatchCtrlService.batchPileUpdate(pileBatchUpdateVos);
    }

}
