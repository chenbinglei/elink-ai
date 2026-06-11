package com.sunmax.webapp.service;

import com.alibaba.fastjson2.JSONObject;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.webapp.dto.WechatMchTransferDto;
import com.sunmax.webapp.dto.WechatTransferOrderDto;
import com.sunmax.webapp.vo.wechat.*;
import com.wechat.pay.java.service.payments.jsapi.model.PrepayWithRequestPaymentResponse;
import com.wechat.pay.java.service.payments.model.Transaction;
import com.wechat.pay.java.service.refund.model.Refund;

public interface TradeService {

    /**
     * 微信支付调起支付
     * @param wechatPayVo 微信支付参数
     * @return 预下单参数数据
     */
    ResponseResult<PrepayWithRequestPaymentResponse> wechatPayUnifiedOrder(WechatPayVo wechatPayVo);

    /**
     * 微信支付回调url
     * @param rsaSerialNo 微信商户RSA平台证书序列号
     * @param jsonObject 回调参数
     * @return 响应数据
     */
    JSONObject wechatPayNotifyUrl(String rsaSerialNo, JSONObject jsonObject);

    /**
     * 查询微信小程序支付订单
     * @param payOrderVo 支付订单参数
     * @return 订单数据
     */
    ResponseResult<Transaction> queryWechatPayOrder(WechatPayOrderVo payOrderVo);

    /**
     * 微信退款
     * @param wechatRefundVo 退款参数
     * @return 退款数据
     */
    ResponseResult<Refund> wechatRefundOrder(WechatRefundVo wechatRefundVo);

    /**
     * 微信退款回调url
     * @param rsaSerialNo 微信支付RSA证书序列号
     * @param jsonObject 回调参数
     * @return 响应数据
     */
    JSONObject wechatRefundNotifyUrl(String rsaSerialNo, JSONObject jsonObject);

    /**
     * 查询微信退款订单信息
     * @param refundOrderVo 微信退款订单查询参数
     *
     */
    ResponseResult<Refund> queryWechatRefundOrder(WechatRefundOrderVo refundOrderVo);

    /**
     * 微信商户转账
     * @param mchTransferVo 微信商户转账参数
     * @return 转账数据
     */
    ResponseResult<WechatMchTransferDto> wechatMchTransfer(WechatMchTransferVo mchTransferVo);

    /**
     * 微信商户转账回调url
     * @param rsaSerialNo 微信支付RSA证书序列号
     * @param transferObject 回调参数
     * @return 响应数据
     */
    JSONObject wechatTransferNotifyUrl(String rsaSerialNo, JSONObject transferObject);

    /**
     * 查询微信商户转账订单信息
     * @param transferOrderVo 微信商户转账订单查询参数
     * @return 转账订单数据
     */
    ResponseResult<WechatTransferOrderDto> queryWechatTransferOrder(WechatTransferOrderVo transferOrderVo);

}
