package com.sunmax.common.feign.protocol;

import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.vo.webapp.AppletChargeRefundVo;
import com.sunmax.common.vo.webapp.WalletBalanceVo;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import com.sunmax.common.feign.fallback.GenericFeignFallbackFactory;

/**
 * 接收数据服务提供的接口
 */
@FeignClient(value = "swebapp-service", path = "/swebapp/feign/protocol", fallbackFactory = GenericFeignFallbackFactory.class)
public interface ProtocolWebAppFeignClient {

    @PostMapping("appletChargeRefund")
    @Operation(summary = "小程序充电退款")
    
    ResponseResult<Void> appletChargeRefund(@RequestBody AppletChargeRefundVo appletRefundVo);

    @PostMapping("updateWalletBalance")
    @Operation(summary = "更新小程序放电钱包")
    
    ResponseResult<Void> updateWalletBalance(@RequestBody WalletBalanceVo walletBalanceVo);

}
