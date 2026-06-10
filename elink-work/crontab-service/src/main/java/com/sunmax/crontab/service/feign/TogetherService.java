package com.sunmax.crontab.service.feign;

import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.vo.crontab.StrategyTaskVo;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(value = "together-service", path = "/together/feign/crontab")
//@FeignClient(value = "together-service-cbl",url = "http://121.41.109.130:60009")
public interface TogetherService {

    @PostMapping("findStrategyHandTaskList")
    @Operation(summary = "查询平台进行中的自动策略任务列表")
    
    ResponseResult<List<StrategyTaskVo>> findStrategyHandTaskList(@RequestParam(required = false) String siteId);

}
