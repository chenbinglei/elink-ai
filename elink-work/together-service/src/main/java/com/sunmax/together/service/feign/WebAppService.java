package com.sunmax.together.service.feign;

import com.sunmax.common.dto.webapp.DischargeTradeDto;
import com.sunmax.common.dto.webapp.DischargeTradeListDto;
import com.sunmax.common.dto.webapp.RechargeTradeDto;
import com.sunmax.common.dto.webapp.RechargeTradeListDto;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.vo.webapp.AppletChargeRefundVo;
import com.sunmax.common.vo.webapp.DischargeTradeQueryVo;
import com.sunmax.common.vo.webapp.RechargeTradeQueryVo;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiOperationSupport;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * 接收系统服务提供的接口
 */
@FeignClient(value = "swebapp-service")
//@FeignClient(value = "swebapp-service-cbl",url = "http://121.41.109.130:60010")
@RestController
@RequestMapping("/swebapp/feign/together")
public interface WebAppService {

    @PostMapping("queryRechargeTradeList")
    @ApiOperation("查询充电交易列表")
    @ApiOperationSupport(order = 1)
    ResponseResult<RechargeTradeListDto> queryRechargeTradeList(@RequestBody RechargeTradeQueryVo rechargeTradeVo);

    @PostMapping("findRechargeTradeById")
    @ApiOperation("根据主键id查询充电交易详情")
    @ApiOperationSupport(order = 2)
    ResponseResult<RechargeTradeDto> findRechargeTradeById(@RequestParam String id);

    @PostMapping("queryDischargeTradeList")
    @ApiOperation("查询V2G钱包交易列表")
    @ApiOperationSupport(order = 3)
    ResponseResult<DischargeTradeListDto> queryDischargeTradeList(@RequestBody DischargeTradeQueryVo dischargeTradeVo);

    @PostMapping("findDischargeTradeById")
    @ApiOperation("根据主键id查询V2G钱包交易详情")
    @ApiOperationSupport(order = 4)
    ResponseResult<DischargeTradeDto> findDischargeTradeById(@RequestParam String id);

    @PostMapping(value = "findRefundMoneyByOrderNum")
    @ApiOperation("根据订单编号查询该笔订单可退金额")
    @ApiOperationSupport(order = 5)
    ResponseResult<BigDecimal> findRefundMoneyByOrderNum(@RequestParam String orderNum);

    @PostMapping("appletChargeRefund")
    @ApiOperation("小程序充电退款")
    @ApiOperationSupport(order = 6)
    ResponseResult<Void> appletChargeRefund(@RequestBody AppletChargeRefundVo appletRefundVo);

    @PostMapping("findRechargeTradeListByOrderNums")
    @ApiOperation("根据多个订单编号查询充电支付交易列表")
    @ApiOperationSupport(order = 7)
    ResponseResult<List<RechargeTradeDto>> findRechargeTradeListByOrderNums(@RequestBody List<String> orderNums);

}
