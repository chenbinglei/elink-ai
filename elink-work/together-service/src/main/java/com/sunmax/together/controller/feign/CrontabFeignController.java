package com.sunmax.together.controller.feign;

import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.vo.crontab.StrategyTaskVo;
import com.sunmax.together.service.energy.StrategyService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Hidden;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/feign/crontab")
@Tag(name = "提供给配置服务调用的远程接口")
@Hidden()
public class CrontabFeignController {

    @Autowired
    private StrategyService strategyService;

    @PostMapping("findStrategyHandTaskList")
    @Operation(summary = "查询平台进行中的自动策略任务列表")
    
    public ResponseResult<List<StrategyTaskVo>> findStrategyHandTaskList(@RequestParam(required = false) String siteId) {
        return strategyService.findStrategyHandTaskList(siteId);
    }

}
