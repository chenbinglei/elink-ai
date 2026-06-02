package com.sunmax.webapp.controller;

import com.alibaba.fastjson.JSONObject;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.webapp.dto.WechatMchTransferDto;
import com.sunmax.webapp.dto.WechatTransferOrderDto;
import com.sunmax.webapp.service.TradeService;
import com.sunmax.webapp.vo.wechat.*;
import com.wechat.pay.java.service.payments.jsapi.model.PrepayWithRequestPaymentResponse;
import com.wechat.pay.java.service.payments.model.Transaction;
import com.wechat.pay.java.service.refund.model.Refund;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiOperationSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;

@RestController
@CrossOrigin
@RequestMapping("trade")
@Api(tags = "交易管理")
@ApiIgnore()
public class TradeController {

    @Autowired
    private TradeService tradeService;

    @PostMapping("wechatPayUnifiedOrder")
    @ApiOperation("微信支付调起支付")
    @ApiOperationSupport(order = 1)
    public ResponseResult<PrepayWithRequestPaymentResponse> wechatPayUnifiedOrder(WechatPayVo wechatPayVo) {
        return tradeService.wechatPayUnifiedOrder(wechatPayVo);
    }

    @PostMapping("wechatPayNotifyUrl")
    @ApiOperation("微信支付回调url")
    @ApiOperationSupport(order = 2)
    public JSONObject wechatPayNotifyUrl(HttpServletRequest request, @RequestBody JSONObject payObject) {
        // 验证签名
//        String nonce = request.getHeader("Wechatpay-Nonce");
//        String timestamp = request.getHeader("Wechatpay-Timestamp");
//        String signature = request.getHeader("Wechatpay-Signature");
//        String serialNo = request.getHeader("Wechatpay-Serial");
//        log.info("微信支付回调， nonce:{},timestamp:{},signature:{},serialNo:{}", nonce, timestamp, signature, serialNo);
        return tradeService.wechatPayNotifyUrl(request.getHeader("Wechatpay-Serial"), payObject);
    }

    @PostMapping("queryWechatPayOrder")
    @ApiOperation("查询微信小程序支付订单")
    @ApiOperationSupport(order = 3)
    public ResponseResult<Transaction> queryWechatPayOrder(WechatPayOrderVo payOrderVo) {
        return tradeService.queryWechatPayOrder(payOrderVo);
    }

    @PostMapping("wechatRefundOrder")
    @ApiOperation("微信退款")
    @ApiOperationSupport(order = 4)
    public ResponseResult<Refund> wechatRefundOrder(WechatRefundVo wechatRefundVo) {
        return tradeService.wechatRefundOrder(wechatRefundVo);
    }

    @PostMapping("wechatRefundNotifyUrl")
    @ApiOperation("微信退款回调url")
    @ApiOperationSupport(order = 5)
    public JSONObject wechatRefundNotifyUrl(HttpServletRequest request, @RequestBody JSONObject refundObject) {
        return tradeService.wechatRefundNotifyUrl(request.getHeader("Wechatpay-Serial"), refundObject);
    }

    @PostMapping("queryWechatRefundOrder")
    @ApiOperation("查询微信退款订单信息")
    @ApiOperationSupport(order = 6)
    public ResponseResult<Refund> queryWechatRefundOrder(WechatRefundOrderVo refundOrderVo) {
        return tradeService.queryWechatRefundOrder(refundOrderVo);
    }

    @PostMapping("wechatMchTransfer")
    @ApiOperation("微信商户转账")
    @ApiOperationSupport(order = 7)
    public ResponseResult<WechatMchTransferDto> wechatMchTransfer(WechatMchTransferVo mchTransferVo) {
        return tradeService.wechatMchTransfer(mchTransferVo);
    }

    @PostMapping("wechatTransferNotifyUrl")
    @ApiOperation("微信商户转账回调url")
    @ApiOperationSupport(order = 8)
    public JSONObject wechatTransferNotifyUrl(HttpServletRequest request, @RequestBody JSONObject transferObject) {
        return tradeService.wechatTransferNotifyUrl(request.getHeader("Wechatpay-Serial"), transferObject);
    }

    @PostMapping("queryWechatTransferOrder")
    @ApiOperation("查询微信转账订单信息")
    @ApiOperationSupport(order = 9)
    public ResponseResult<WechatTransferOrderDto> queryWechatTransferOrder(WechatTransferOrderVo transferOrderVo) {
        return tradeService.queryWechatTransferOrder(transferOrderVo);
    }

}
