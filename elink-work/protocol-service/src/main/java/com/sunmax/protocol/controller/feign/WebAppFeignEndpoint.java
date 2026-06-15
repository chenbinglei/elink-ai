package com.sunmax.protocol.controller.feign;

import com.sunmax.common.dto.protocol.PileResultDto;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.vo.protocol.PileStartVo;
import com.sunmax.common.vo.protocol.PileStopVo;
import com.sunmax.protocol.service.PileCtrlService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Hidden;

import jakarta.annotation.Resource;
import com.sunmax.common.feign.webapp.WebAppProtocolFeignClient;

@RestController
@CrossOrigin
@RequestMapping("/feign/swebapp")
@Tag(name = "提供给webapp服务调用的远程接口")
@Hidden()
public class WebAppFeignEndpoint implements WebAppProtocolFeignClient {

    @Resource
    private PileCtrlService pileCtrlService;

    @PostMapping("pileStart")
    @Operation(summary = "启动充电桩")
    
    @Override
    public ResponseResult<PileResultDto> pileStart(@RequestBody PileStartVo pileStartVo) {
        return pileCtrlService.pileStart(pileStartVo);
    }
    @PostMapping("pileStop")
    @Operation(summary = "停止充电桩")
    
    @Override
    public ResponseResult<PileResultDto> pileStop(@RequestBody PileStopVo pileStopVo) {
        return pileCtrlService.pileStop(pileStopVo);
    }

}
