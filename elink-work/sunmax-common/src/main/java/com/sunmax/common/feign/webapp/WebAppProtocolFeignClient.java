package com.sunmax.common.feign.webapp;

import com.sunmax.common.dto.protocol.PileResultDto;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.vo.protocol.PileStartVo;
import com.sunmax.common.vo.protocol.PileStopVo;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import com.sunmax.common.feign.fallback.GenericFeignFallbackFactory;

/**
 * 接收设备服务提供的接口
 */
@FeignClient(value = "sunos-protocol-service", path = "/protocol/feign/swebapp", fallbackFactory = GenericFeignFallbackFactory.class)
public interface WebAppProtocolFeignClient {

    @PostMapping("pileStart")
    @Operation(summary = "启动充电桩")
    
    ResponseResult<PileResultDto> pileStart(@RequestBody PileStartVo pileStartVo);

    @PostMapping("pileStop")
    @Operation(summary = "停止充电桩")
    
    ResponseResult<PileResultDto> pileStop(@RequestBody PileStopVo pileStopVo);

}
