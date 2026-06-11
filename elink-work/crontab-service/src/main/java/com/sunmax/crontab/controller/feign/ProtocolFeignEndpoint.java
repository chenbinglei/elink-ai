package com.sunmax.crontab.controller.feign;

import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.vo.crontab.PileStartControlVo;
import com.sunmax.crontab.service.ProtocolFeignService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Hidden;
import com.sunmax.common.feign.protocol.ProtocolCrontabFeignClient;

/**
 * 提供控制服务调用的接口
 */
@CrossOrigin
@RestController
@RequestMapping("/feign/protocol")
@Tag(name = "提供控制服务调用的接口")
@Hidden()
public class ProtocolFeignEndpoint implements ProtocolCrontabFeignClient {

    @Autowired
    private ProtocolFeignService protocolFeignService;

    @PostMapping("checkPileStartControl")
    @Operation(summary = "校验电桩平台控制")
    
    @Override
    public ResponseResult<Boolean> checkPileStartControl(@RequestBody PileStartControlVo pileStartControlVo) {
        return protocolFeignService.checkPileStartControl(pileStartControlVo);
    }

    @PostMapping("updatePileStartControl")
    @Operation(summary = "更新电桩平台控制状态")
    
    @Override
    public ResponseResult<Boolean> updatePileStartControl(@RequestBody PileStartControlVo pileStartControlVo) {
        return protocolFeignService.updatePileStartControl(pileStartControlVo);
    }

}
