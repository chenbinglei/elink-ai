package com.sunmax.protocol.controller.feign;

import com.sunmax.common.dto.protocol.PileResultDto;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.vo.protocol.PilePowerCtrlVo;
import com.sunmax.protocol.service.CrontabFeignService;
import com.sunmax.protocol.service.PileBatchCtrlService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Hidden;

import java.util.List;
import com.sunmax.common.feign.crontab.CrontabProtocolFeignClient;

@RestController
@CrossOrigin
@RequestMapping("/feign/crontab")
@Tag(name = "提供给定时任务管理服务调用的远程接口")
@Hidden()
public class CrontabFeignEndpoint implements CrontabProtocolFeignClient {

    @Autowired
    private CrontabFeignService crontabFeignService;

    @Autowired
    private PileBatchCtrlService pileBatchCtrlService;

    @PostMapping("batchPilePowerCtrl")
    @Operation(summary = "批量对多个充电桩功率控制")
    
    @Override
    public ResponseResult<Void> batchPilePowerCtrl(@RequestBody List<PilePowerCtrlVo> pilePowerCtrlVos) {
        return crontabFeignService.batchPilePowerCtrl(pilePowerCtrlVos);
    }

    @PostMapping("batchPilePowerCtrlResult")
    @Operation(summary = "批量对多个充电桩功率控制(响应结果)")
    
    public ResponseResult<List<PileResultDto>> batchPilePowerCtrlResult(@RequestBody List<PilePowerCtrlVo> pilePowerCtrlVos) {
        return pileBatchCtrlService.batchPilePowerCtrl(pilePowerCtrlVos);
    }

}
