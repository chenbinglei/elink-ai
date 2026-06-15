package com.sunmax.webapp.controller.feign;

import com.sunmax.common.dto.webapp.DischargeTradeDto;
import com.sunmax.common.dto.webapp.DischargeTradeListDto;
import com.sunmax.common.dto.webapp.RechargeTradeDto;
import com.sunmax.common.dto.webapp.RechargeTradeListDto;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.vo.webapp.AppletChargeRefundVo;
import com.sunmax.common.vo.webapp.DischargeTradeQueryVo;
import com.sunmax.common.vo.webapp.RechargeTradeQueryVo;
import com.sunmax.webapp.service.ChargeService;
import com.sunmax.webapp.service.TogetherFeignService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Hidden;

import java.math.BigDecimal;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/feign/together")
@Tag(name = "提供给协议服务的接口")
@Hidden()
public class TogetherFeignController {

    @Autowired
    private TogetherFeignService togetherFeignService;

    @Autowired
    private ChargeService chargeService;

    @PostMapping("queryRechargeTradeList")
    @Operation(summary = "查询充电交易列表")
    
    public ResponseResult<RechargeTradeListDto> queryRechargeTradeList(@RequestBody RechargeTradeQueryVo rechargeTradeVo) {
        return togetherFeignService.queryRechargeTradeList(rechargeTradeVo);
    }

    @PostMapping("findRechargeTradeById")
    @Operation(summary = "根据主键id查询充电交易详情")
    
    public ResponseResult<RechargeTradeDto> findRechargeTradeById(@RequestParam String id) {
        return togetherFeignService.findRechargeTradeById(id);
    }

    @PostMapping("queryDischargeTradeList")
    @Operation(summary = "查询V2G钱包交易列表")
    
    public ResponseResult<DischargeTradeListDto> queryDischargeTradeList(@RequestBody DischargeTradeQueryVo dischargeTradeVo) {
        return togetherFeignService.queryDischargeTradeList(dischargeTradeVo);
    }

    @PostMapping("findDischargeTradeById")
    @Operation(summary = "根据主键id查询V2G钱包交易详情")
    
    public ResponseResult<DischargeTradeDto> findDischargeTradeById(@RequestParam String id) {
        return togetherFeignService.findDischargeTradeById(id);
    }

    @PostMapping(value = "findRefundMoneyByOrderNum")
    @Operation(summary = "根据订单编号查询该笔订单可退金额")
    
    public ResponseResult<BigDecimal> findRefundMoneyByOrderNum(@RequestParam String orderNum) {
        return togetherFeignService.findRefundMoneyByOrderNum(orderNum);
    }

    @PostMapping("appletChargeRefund")
    @Operation(summary = "小程序充电退款")
    
    public ResponseResult<Void> appletChargeRefund(@RequestBody AppletChargeRefundVo appletRefundVo) {
        return chargeService.appletChargeRefund(appletRefundVo);
    }

    @PostMapping("findRechargeTradeListByOrderNums")
    @Operation(summary = "根据多个订单编号查询充电支付交易列表")
    
    public ResponseResult<List<RechargeTradeDto>> findRechargeTradeListByOrderNums(@RequestBody List<String> orderNums) {
        return togetherFeignService.findRechargeTradeListByOrderNums(orderNums);
    }

}
