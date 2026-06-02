package com.sunmax.webapp.service.feign;

import com.sunmax.common.dto.protocol.PileResultDto;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.vo.protocol.PileStartVo;
import com.sunmax.common.vo.protocol.PileStopVo;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiOperationSupport;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 接收设备服务提供的接口
 */
@FeignClient(value = "sunos-protocol-service")
@RestController
@RequestMapping("/protocol/feign/swebapp")
public interface ProtocolService {

    @PostMapping("pileStart")
    @ApiOperation("启动充电桩")
    @ApiOperationSupport(order = 1)
    ResponseResult<PileResultDto> pileStart(@RequestBody PileStartVo pileStartVo);

    @PostMapping("pileStop")
    @ApiOperation("停止充电桩")
    @ApiOperationSupport(order = 2)
    ResponseResult<PileResultDto> pileStop(@RequestBody PileStopVo pileStopVo);

}
