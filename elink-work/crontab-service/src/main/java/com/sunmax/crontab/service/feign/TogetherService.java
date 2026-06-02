package com.sunmax.crontab.service.feign;

import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.vo.crontab.StrategyTaskVo;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiOperationSupport;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@FeignClient(value = "together-service")
//@FeignClient(value = "together-service-cbl",url = "http://121.41.109.130:60009")
@RestController
@RequestMapping("/together/feign/crontab")
public interface TogetherService {

    @PostMapping("findStrategyHandTaskList")
    @ApiOperation("查询平台进行中的自动策略任务列表")
    @ApiOperationSupport(order = 1)
    ResponseResult<List<StrategyTaskVo>> findStrategyHandTaskList(@RequestParam(required = false) String siteId);

}
