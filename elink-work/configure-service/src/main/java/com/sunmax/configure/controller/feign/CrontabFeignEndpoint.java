package com.sunmax.configure.controller.feign;

import com.sunmax.common.dto.configure.DeviceVariableDto;
import com.sunmax.common.dto.configure.PowerControlResDto;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.vo.crontab.PowerControlParamVo;
import com.sunmax.configure.service.GraphService;
import com.sunmax.configure.service.InterflowService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Hidden;

import java.util.List;

/**
 * @Author: yqz
 * @注释: 提供给定时任务服务调用的远程接口
 */
@RestController
@CrossOrigin
@RequestMapping("/feign/crontab")
@Tag(name = "提供给定时任务服务调用的远程接口")
@Hidden()
public class CrontabFeignEndpoint {

    @Autowired
    private InterflowService interflowService;

    @Autowired
    private GraphService graphService;

    @PostMapping("interflowPowerControl")
    @Operation(summary = "城市充电功率控制")
    
    public ResponseResult<String> interflowPowerControl(@RequestParam String pileCode, @RequestParam Integer gunCode, @RequestParam Double outPower) {
        return interflowService.interflowPowerControl(pileCode, gunCode, outPower);
    }

    @PostMapping("interflowBatchPowerControl")
    @Operation(summary = "城市充电批量功率控制")
    
    public ResponseResult<List<PowerControlResDto>> interflowBatchPowerControl(@RequestBody List<PowerControlParamVo> powerControlParamVos) {
        return interflowService.interflowBatchPowerControl(powerControlParamVos);
    }

    @PostMapping("findAllVariableByDomainId")
    @Operation(summary = "根据域名id查询所有变量标识")
    
    public ResponseResult<DeviceVariableDto> findAllVariableByDomainId(@RequestParam String domainId) {
        return graphService.findAllVariableByDomainId(domainId);
    }

}
