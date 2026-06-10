package com.sunmax.protocol.controller;

import com.sunmax.common.dto.protocol.PileResultDto;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.vo.protocol.*;
import com.sunmax.protocol.service.PileBatchCtrlService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Hidden;

import java.util.List;

/**
 * 批量对充电桩控制层
 */
@RestController
@CrossOrigin
@RequestMapping("pileBatchCtrl")
@Tag(name = "批量对充电桩控制层")
@Hidden
public class PileBatchCtrlController {

    @Autowired
    private PileBatchCtrlService pileBatchCtrlService;

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

    @PostMapping("batchPilePowerCtrl")
    @Operation(summary = "批量对多个充电桩功率控制")
    
    public ResponseResult<List<PileResultDto>> batchPilePowerCtrl(@RequestBody List<PilePowerCtrlVo> pilePowerCtrlVos) {
        return pileBatchCtrlService.batchPilePowerCtrl(pilePowerCtrlVos);
    }

    @PostMapping("batchPileRateSet")
    @Operation(summary = "批量设置费率数据")
    
    public ResponseResult<List<PileResultDto>> batchPileRateSet(@RequestBody List<PileRateSetVo> pileRateSetVos) {
        return pileBatchCtrlService.batchPileRateSet(pileRateSetVos);
    }

    @PostMapping("pileBatchUpdate")
    @Operation(summary = "批量对多个充电桩升级")
    
    public ResponseResult<List<PileResultDto>> batchPileUpdate(@RequestBody PileBatchUpdateVo pileBatchUpdateVos) {
        return pileBatchCtrlService.batchPileUpdate(pileBatchUpdateVos);
    }

}
