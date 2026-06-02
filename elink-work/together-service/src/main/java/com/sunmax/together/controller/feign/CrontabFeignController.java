package com.sunmax.together.controller.feign;

import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.vo.crontab.StrategyTaskVo;
import com.sunmax.together.service.energy.StrategyService;
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
@Api(tags = "提供给配置服务调用的远程接口")
@ApiIgnore()
public class CrontabFeignController {

    @Autowired
    private StrategyService strategyService;

    @PostMapping("findStrategyHandTaskList")
    @ApiOperation("查询平台进行中的自动策略任务列表")
    @ApiOperationSupport(order = 1)
    public ResponseResult<List<StrategyTaskVo>> findStrategyHandTaskList(@RequestParam(required = false) String siteId) {
        return strategyService.findStrategyHandTaskList(siteId);
    }

}
