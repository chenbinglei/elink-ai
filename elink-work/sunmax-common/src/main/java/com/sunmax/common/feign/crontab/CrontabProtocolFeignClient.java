package com.sunmax.common.feign.crontab;

import com.sunmax.common.dto.protocol.PileResultDto;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.vo.protocol.PilePowerCtrlVo;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import com.sunmax.common.feign.fallback.GenericFeignFallbackFactory;

@FeignClient(value = "sunos-protocol-service", path = "/protocol/feign/crontab", fallbackFactory = GenericFeignFallbackFactory.class)
public interface CrontabProtocolFeignClient {

    @PostMapping("batchPilePowerCtrl")
    @Operation(summary = "批量对多个充电桩功率控制")
    
    ResponseResult<Void> batchPilePowerCtrl(@RequestBody List<PilePowerCtrlVo> pilePowerCtrlVos);

    @PostMapping("batchPilePowerCtrlResult")
    @Operation(summary = "批量对多个充电桩功率控制(响应结果)")
    
    ResponseResult<List<PileResultDto>> batchPilePowerCtrlResult(@RequestBody List<PilePowerCtrlVo> pilePowerCtrlVos);

}
