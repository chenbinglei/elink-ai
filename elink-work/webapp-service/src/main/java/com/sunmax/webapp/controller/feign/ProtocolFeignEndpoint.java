package com.sunmax.webapp.controller.feign;

import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.vo.webapp.AppletChargeRefundVo;
import com.sunmax.common.vo.webapp.WalletBalanceVo;
import com.sunmax.webapp.service.ChargeService;
import com.sunmax.webapp.service.ProtocolFeignService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Hidden;

@RestController
@CrossOrigin
@RequestMapping("/feign/protocol")
@Tag(name = "提供给协议服务的接口")
@Hidden()
public class ProtocolFeignEndpoint {

    @Autowired
    private ChargeService chargeService;

    @Autowired
    private ProtocolFeignService protocolFeignService;

    @PostMapping("appletChargeRefund")
    @Operation(summary = "小程序充电退款")
    
    public ResponseResult<Void> appletChargeRefund(@RequestBody AppletChargeRefundVo appletRefundVo) {
        return chargeService.appletChargeRefund(appletRefundVo);
    }

    @PostMapping("updateWalletBalance")
    @Operation(summary = "更新小程序V2G钱包")
    
    public ResponseResult<Void> updateWalletBalance(@RequestBody WalletBalanceVo walletBalanceVo) {
        return protocolFeignService.updateWalletBalance(walletBalanceVo);
    }

}
