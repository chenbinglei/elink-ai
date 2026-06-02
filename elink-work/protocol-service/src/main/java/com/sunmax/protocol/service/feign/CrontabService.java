package com.sunmax.protocol.service.feign;

import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.vo.crontab.PileStartControlVo;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiOperationSupport;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 接收数据服务提供的接口
 */
@FeignClient(value = "scrontab-service")
@RestController
@RequestMapping("/scrontab/feign/protocol")
public interface CrontabService {

    @PostMapping("checkPileStartControl")
    @ApiOperation("校验电桩平台控制")
    @ApiOperationSupport(order = 1)
    ResponseResult<Boolean> checkPileStartControl(@RequestBody PileStartControlVo pileStartControlVo);

    @PostMapping("updatePileStartControl")
    @ApiOperation("更新电桩平台控制状态")
    @ApiOperationSupport(order = 2)
    ResponseResult<Boolean> updatePileStartControl(@RequestBody PileStartControlVo pileStartControlVo);

}
