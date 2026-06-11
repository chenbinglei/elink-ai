package com.sunmax.webapp.controller;

import com.sunmax.common.dto.protocol.PileResultDto;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.vo.protocol.PileStartVo;
import com.sunmax.common.vo.protocol.PileStopVo;
import com.sunmax.webapp.service.ChargeService;
import com.sunmax.webapp.service.feign.ProtocolService;
import com.sunmax.webapp.vo.AppletChargeStartVo;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("charge")
@Tag(name = "充电管理")
public class ChargeController {

    @Autowired
    private ChargeService chargeService;

    @Autowired
    private ProtocolService protocolService;

    @PostMapping("appletChargeStart")
    @Operation(summary = "小程序启动充放电")
    
    public ResponseResult<?> appletChargeStart(AppletChargeStartVo appletStartVo) {
        return chargeService.appletChargeStart(appletStartVo);
    }

    @PostMapping("pileStart")
    @Operation(summary = "启动充电桩")
    
    public ResponseResult<PileResultDto> pileStart(PileStartVo pileStartVo) {
        return protocolService.pileStart(pileStartVo);
    }

    @PostMapping("pileStop")
    @Operation(summary = "停止充电桩")
    
    public ResponseResult<PileResultDto> pileStop(PileStopVo pileStopVo) {
        return protocolService.pileStop(pileStopVo);
    }

}
