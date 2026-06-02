package com.sunmax.protocol.controller.feign;

import com.sunmax.common.dto.protocol.PileResultDto;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.vo.protocol.PilePowerCtrlVo;
import com.sunmax.protocol.service.CrontabFeignService;
import com.sunmax.protocol.service.PileBatchCtrlService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiOperationSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/feign/crontab")
@Api(tags = "提供给定时任务管理服务调用的远程接口")
@ApiIgnore()
public class CrontabFeignController {

    @Autowired
    private CrontabFeignService crontabFeignService;

    @Autowired
    private PileBatchCtrlService pileBatchCtrlService;

    @PostMapping("batchPilePowerCtrl")
    @ApiOperation("批量对多个充电桩功率控制")
    @ApiOperationSupport(order = 1)
    public ResponseResult<Void> batchPilePowerCtrl(@RequestBody List<PilePowerCtrlVo> pilePowerCtrlVos) {
        return crontabFeignService.batchPilePowerCtrl(pilePowerCtrlVos);
    }

    @PostMapping("batchPilePowerCtrlResult")
    @ApiOperation("批量对多个充电桩功率控制(响应结果)")
    @ApiOperationSupport(order = 2)
    public ResponseResult<List<PileResultDto>> batchPilePowerCtrlResult(@RequestBody List<PilePowerCtrlVo> pilePowerCtrlVos) {
        return pileBatchCtrlService.batchPilePowerCtrl(pilePowerCtrlVos);
    }

}
