package com.sunmax.crontab.controller.feign;

import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.vo.crontab.PileStartControlVo;
import com.sunmax.crontab.service.ProtocolFeignService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiOperationSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

/**
 * 提供控制服务调用的接口
 */
@CrossOrigin
@RestController
@RequestMapping("/feign/protocol")
@Api(tags = "提供控制服务调用的接口")
@ApiIgnore()
public class ProtocolFeignController {

    @Autowired
    private ProtocolFeignService protocolFeignService;

    @PostMapping("checkPileStartControl")
    @ApiOperation("校验电桩平台控制")
    @ApiOperationSupport(order = 1)
    public ResponseResult<Boolean> checkPileStartControl(@RequestBody PileStartControlVo pileStartControlVo) {
        return protocolFeignService.checkPileStartControl(pileStartControlVo);
    }

    @PostMapping("updatePileStartControl")
    @ApiOperation("更新电桩平台控制状态")
    @ApiOperationSupport(order = 2)
    public ResponseResult<Boolean> updatePileStartControl(@RequestBody PileStartControlVo pileStartControlVo) {
        return protocolFeignService.updatePileStartControl(pileStartControlVo);
    }

}
