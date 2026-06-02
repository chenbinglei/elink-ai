package com.sunmax.webapp.controller.feign;

import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.vo.webapp.AppletChargeRefundVo;
import com.sunmax.common.vo.webapp.WalletBalanceVo;
import com.sunmax.webapp.service.ChargeService;
import com.sunmax.webapp.service.ProtocolFeignService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiOperationSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

@RestController
@CrossOrigin
@RequestMapping("/feign/protocol")
@Api(tags = "提供给协议服务的接口")
@ApiIgnore()
public class ProtocolFeignController {

    @Autowired
    private ChargeService chargeService;

    @Autowired
    private ProtocolFeignService protocolFeignService;

    @PostMapping("appletChargeRefund")
    @ApiOperation("小程序充电退款")
    @ApiOperationSupport(order = 1)
    public ResponseResult<Void> appletChargeRefund(@RequestBody AppletChargeRefundVo appletRefundVo) {
        return chargeService.appletChargeRefund(appletRefundVo);
    }

    @PostMapping("updateWalletBalance")
    @ApiOperation("更新小程序V2G钱包")
    @ApiOperationSupport(order = 2)
    public ResponseResult<Void> updateWalletBalance(@RequestBody WalletBalanceVo walletBalanceVo) {
        return protocolFeignService.updateWalletBalance(walletBalanceVo);
    }

}
