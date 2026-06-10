package com.sunmax.protocol.service.feign;

import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.vo.crontab.PileStartControlVo;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 接收数据服务提供的接口
 */
@FeignClient(value = "crontab-service", path = "/crontab/feign/protocol")
public interface CrontabService {

    @PostMapping("checkPileStartControl")
    @Operation(summary = "校验电桩平台控制")
    ResponseResult<Boolean> checkPileStartControl(@RequestBody PileStartControlVo pileStartControlVo);

    @PostMapping("updatePileStartControl")
    @Operation(summary = "更新电桩平台控制状态")
    ResponseResult<Boolean> updatePileStartControl(@RequestBody PileStartControlVo pileStartControlVo);

}
