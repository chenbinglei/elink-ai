package com.sunmax.together.service.feign;

import com.sunmax.common.dto.webapp.DischargeTradeDto;
import com.sunmax.common.dto.webapp.DischargeTradeListDto;
import com.sunmax.common.dto.webapp.RechargeTradeDto;
import com.sunmax.common.dto.webapp.RechargeTradeListDto;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.vo.webapp.AppletChargeRefundVo;
import com.sunmax.common.vo.webapp.DischargeTradeQueryVo;
import com.sunmax.common.vo.webapp.RechargeTradeQueryVo;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * 接收系统服务提供的接口
 */
@FeignClient(value = "swebapp-service", path = "/swebapp/feign/together")
//@FeignClient(value = "swebapp-service-cbl",url = "http://121.41.109.130:60010")
public interface WebAppService {

    @PostMapping("queryRechargeTradeList")
    @Operation(summary = "查询充电交易列表")
    
    ResponseResult<RechargeTradeListDto> queryRechargeTradeList(@RequestBody RechargeTradeQueryVo rechargeTradeVo);

    @PostMapping("findRechargeTradeById")
    @Operation(summary = "根据主键id查询充电交易详情")
    
    ResponseResult<RechargeTradeDto> findRechargeTradeById(@RequestParam String id);

    @PostMapping("queryDischargeTradeList")
    @Operation(summary = "查询V2G钱包交易列表")
    
    ResponseResult<DischargeTradeListDto> queryDischargeTradeList(@RequestBody DischargeTradeQueryVo dischargeTradeVo);

    @PostMapping("findDischargeTradeById")
    @Operation(summary = "根据主键id查询V2G钱包交易详情")
    
    ResponseResult<DischargeTradeDto> findDischargeTradeById(@RequestParam String id);

    @PostMapping(value = "findRefundMoneyByOrderNum")
    @Operation(summary = "根据订单编号查询该笔订单可退金额")
    
    ResponseResult<BigDecimal> findRefundMoneyByOrderNum(@RequestParam String orderNum);

    @PostMapping("appletChargeRefund")
    @Operation(summary = "小程序充电退款")
    
    ResponseResult<Void> appletChargeRefund(@RequestBody AppletChargeRefundVo appletRefundVo);

    @PostMapping("findRechargeTradeListByOrderNums")
    @Operation(summary = "根据多个订单编号查询充电支付交易列表")
    
    ResponseResult<List<RechargeTradeDto>> findRechargeTradeListByOrderNums(@RequestBody List<String> orderNums);

}
