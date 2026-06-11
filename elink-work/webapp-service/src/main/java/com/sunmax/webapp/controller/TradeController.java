package com.sunmax.webapp.controller;
import lombok.extern.slf4j.Slf4j;

import com.alibaba.fastjson2.JSONObject;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.webapp.dto.WechatMchTransferDto;
import com.sunmax.webapp.dto.WechatTransferOrderDto;
import com.sunmax.webapp.service.TradeService;
import com.sunmax.webapp.vo.wechat.*;
import com.wechat.pay.java.service.payments.jsapi.model.PrepayWithRequestPaymentResponse;
import com.wechat.pay.java.service.payments.model.Transaction;
import com.wechat.pay.java.service.refund.model.Refund;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Hidden;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@CrossOrigin
@RequestMapping("trade")
@Tag(name = "交易管理")
@Hidden()
@Slf4j
public class TradeController {

    @Autowired
    private TradeService tradeService;

    @PostMapping("wechatPayUnifiedOrder")
    @Operation(summary = "微信支付调起支付")
    
    public ResponseResult<PrepayWithRequestPaymentResponse> wechatPayUnifiedOrder(WechatPayVo wechatPayVo) {
        return tradeService.wechatPayUnifiedOrder(wechatPayVo);
    }

    @PostMapping("wechatPayNotifyUrl")
    @Operation(summary = "微信支付回调url")
    
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
    @Operation(summary = "查询微信小程序支付订单")
    
    public ResponseResult<Transaction> queryWechatPayOrder(WechatPayOrderVo payOrderVo) {
        return tradeService.queryWechatPayOrder(payOrderVo);
    }

    @PostMapping("wechatRefundOrder")
    @Operation(summary = "微信退款")
    
    public ResponseResult<Refund> wechatRefundOrder(WechatRefundVo wechatRefundVo) {
        return tradeService.wechatRefundOrder(wechatRefundVo);
    }

    @PostMapping("wechatRefundNotifyUrl")
    @Operation(summary = "微信退款回调url")
    
    public JSONObject wechatRefundNotifyUrl(HttpServletRequest request, @RequestBody JSONObject refundObject) {
        return tradeService.wechatRefundNotifyUrl(request.getHeader("Wechatpay-Serial"), refundObject);
    }

    @PostMapping("queryWechatRefundOrder")
    @Operation(summary = "查询微信退款订单信息")
    
    public ResponseResult<Refund> queryWechatRefundOrder(WechatRefundOrderVo refundOrderVo) {
        return tradeService.queryWechatRefundOrder(refundOrderVo);
    }

    @PostMapping("wechatMchTransfer")
    @Operation(summary = "微信商户转账")
    
    public ResponseResult<WechatMchTransferDto> wechatMchTransfer(WechatMchTransferVo mchTransferVo) {
        return tradeService.wechatMchTransfer(mchTransferVo);
    }

    @PostMapping("wechatTransferNotifyUrl")
    @Operation(summary = "微信商户转账回调url")
    
    public JSONObject wechatTransferNotifyUrl(HttpServletRequest request, @RequestBody JSONObject transferObject) {
        return tradeService.wechatTransferNotifyUrl(request.getHeader("Wechatpay-Serial"), transferObject);
    }

    @PostMapping("queryWechatTransferOrder")
    @Operation(summary = "查询微信转账订单信息")
    
    public ResponseResult<WechatTransferOrderDto> queryWechatTransferOrder(WechatTransferOrderVo transferOrderVo) {
        return tradeService.queryWechatTransferOrder(transferOrderVo);
    }

}
