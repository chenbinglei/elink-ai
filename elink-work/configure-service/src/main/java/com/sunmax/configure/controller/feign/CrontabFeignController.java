package com.sunmax.configure.controller.feign;

import com.sunmax.common.dto.configure.DeviceVariableDto;
import com.sunmax.common.dto.configure.PowerControlResDto;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.vo.crontab.PowerControlParamVo;
import com.sunmax.configure.service.GraphService;
import com.sunmax.configure.service.InterflowService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiOperationSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

/**
 * @Author: yqz
 * @注释: 提供给定时任务服务调用的远程接口
 */
@RestController
@CrossOrigin
@RequestMapping("/feign/crontab")
@Api(tags = "提供给定时任务服务调用的远程接口")
@ApiIgnore()
public class CrontabFeignController {

    @Autowired
    private InterflowService interflowService;

    @Autowired
    private GraphService graphService;

    @PostMapping("interflowPowerControl")
    @ApiOperation("城市充电功率控制")
    @ApiOperationSupport(order = 1)
    public ResponseResult<String> interflowPowerControl(@RequestParam String pileCode, @RequestParam Integer gunCode, @RequestParam Double outPower) {
        return interflowService.interflowPowerControl(pileCode, gunCode, outPower);
    }

    @PostMapping("interflowBatchPowerControl")
    @ApiOperation("城市充电批量功率控制")
    @ApiOperationSupport(order = 2)
    public ResponseResult<List<PowerControlResDto>> interflowBatchPowerControl(@RequestBody List<PowerControlParamVo> powerControlParamVos) {
        return interflowService.interflowBatchPowerControl(powerControlParamVos);
    }

    @PostMapping("findAllVariableByDomainId")
    @ApiOperation("根据域名id查询所有变量标识")
    @ApiOperationSupport(order = 3)
    public ResponseResult<DeviceVariableDto> findAllVariableByDomainId(@RequestParam String domainId) {
        return graphService.findAllVariableByDomainId(domainId);
    }

}
