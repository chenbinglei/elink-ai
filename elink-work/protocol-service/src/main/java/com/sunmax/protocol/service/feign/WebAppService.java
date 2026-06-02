package com.sunmax.protocol.service.feign;

import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.vo.webapp.AppletChargeRefundVo;
import com.sunmax.common.vo.webapp.WalletBalanceVo;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiOperationSupport;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 接收数据服务提供的接口
 */
@FeignClient(value = "swebapp-service")
@RestController
@RequestMapping("/swebapp/feign/protocol")
public interface WebAppService {

    @PostMapping("appletChargeRefund")
    @ApiOperation("小程序充电退款")
    @ApiOperationSupport(order = 1)
    ResponseResult<Void> appletChargeRefund(@RequestBody AppletChargeRefundVo appletRefundVo);

    @PostMapping("updateWalletBalance")
    @ApiOperation("更新小程序放电钱包")
    @ApiOperationSupport(order = 2)
    ResponseResult<Void> updateWalletBalance(@RequestBody WalletBalanceVo walletBalanceVo);

}
