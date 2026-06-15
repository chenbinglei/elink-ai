package com.sunmax.webapp.service.impl;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson2.JSONObject;
import com.google.common.collect.Maps;
import com.sunmax.common.dto.system.AccountDto;
import com.sunmax.common.util.DateUtil;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.util.StringUtil;
import com.sunmax.common.util.local.LocalFileUtil;
import com.sunmax.common.util.oss.FileUtil;
import com.sunmax.common.util.oss.OssFileUtil;
import com.sunmax.common.vo.LocalParamVo;
import com.sunmax.common.vo.together.UserDisWalletChangeVo;
import com.sunmax.webapp.dao.trade.DischargeTradeDao;
import com.sunmax.webapp.dao.trade.RechargeTradeDao;
import com.sunmax.webapp.dto.WechatMchTransferDto;
import com.sunmax.webapp.dto.WechatTransferOrderDto;
import com.sunmax.webapp.entity.trade.DischargeTradeEntity;
import com.sunmax.webapp.entity.trade.RechargeTradeEntity;
import com.sunmax.webapp.enums.TransferStatusEnum;
import com.sunmax.webapp.service.ChargeService;
import com.sunmax.webapp.service.TradeService;
import com.sunmax.webapp.service.feign.SystemService;
import com.sunmax.webapp.service.feign.TogetherService;
import com.sunmax.webapp.util.AesUtil;
import com.sunmax.webapp.util.WxPayUtil;
import com.sunmax.webapp.vo.ChargeStartVo;
import com.sunmax.webapp.vo.wechat.*;
import com.wechat.pay.java.core.Config;
import com.wechat.pay.java.core.RSAAutoCertificateConfig;
import com.wechat.pay.java.core.RSAPublicKeyConfig;
import com.wechat.pay.java.core.util.GsonUtil;
import com.wechat.pay.java.core.util.PemUtil;
import com.wechat.pay.java.service.payments.jsapi.JsapiServiceExtension;
import com.wechat.pay.java.service.payments.jsapi.model.*;
import com.wechat.pay.java.service.payments.model.Transaction;
import com.wechat.pay.java.service.refund.RefundService;
import com.wechat.pay.java.service.refund.model.AmountReq;
import com.wechat.pay.java.service.refund.model.CreateRequest;
import com.wechat.pay.java.service.refund.model.QueryByOutRefundNoRequest;
import com.wechat.pay.java.service.refund.model.Refund;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@Slf4j
public class TradeServiceImpl implements TradeService {

    @Autowired
    private SystemService systemService;

    @Autowired
    private RechargeTradeDao rechargeTradeDao;

    @Autowired
    private DischargeTradeDao dischargeTradeDao;

    @Autowired
    private ChargeService chargeService;

    @Autowired
    private TogetherService togetherService;

    //支付成功回调地址
    @Value("${wechatpay.payUrl}")
    private String payUrl;

    //退款成功回调地址
    @Value("${wechatpay.refundUrl}")
    private String refundUrl;

    //商家转账回调地址
    @Value("${wechatpay.transferUrl}")
    private String transferUrl;

    private static final Map<String, JsapiServiceExtension> payServiceMap = Maps.newConcurrentMap();

    private static final Map<String, RefundService> refundServiceMap = Maps.newConcurrentMap();

    public static final String CNY = "CNY";

    private static String parseKey(String path) {
        if (LocalParamVo.FILE_TYPE) {
            return new String(OssFileUtil.readFile(FileUtil.subString(path, FileUtil.SLASH, FileUtil.QUESTION)), StandardCharsets.UTF_8);
        } else {
            return new String(LocalFileUtil.readFile(path), StandardCharsets.UTF_8);
        }
    }

    private static void jsapiService(String mchId, String apiV3Key, String serialNo, String keyPemPath, Integer apiType, String rsaSerialNo, String pubKeyPath) {
        Config config = null;
        if (StringUtil.isNotEmpty(apiType)) {
            if (apiType == 1) {
                // 初始化商户配置
                config = new RSAAutoCertificateConfig.Builder()
                        .merchantId(mchId)
                        .privateKey(parseKey(keyPemPath))
                        .merchantSerialNumber(serialNo)
                        .apiV3Key(apiV3Key)
                        .build();
            }
            if (apiType == 2) {
                // 初始化商户配置
                config = new RSAPublicKeyConfig.Builder()
                        .merchantId(mchId)
                        .merchantSerialNumber(serialNo)
                        .apiV3Key(apiV3Key)
                        .privateKey(parseKey(keyPemPath))
                        .publicKeyId(rsaSerialNo)
                        .publicKey(parseKey(pubKeyPath))
                        .build();
            }
        }
        // 初始化服务
        payServiceMap.put(mchId, new JsapiServiceExtension.Builder().config(config).signType("RSA").build());// 不填默认为RSA
        refundServiceMap.put(mchId, new RefundService.Builder().config(config).build());// 不填默认为RSA
    }

    @Override
    public ResponseResult<PrepayWithRequestPaymentResponse> wechatPayUnifiedOrder(WechatPayVo wechatPayVo) {
        PrepayRequest request = new PrepayRequest();
        Amount amount = new Amount();
        //支付金额
        amount.setTotal((wechatPayVo.getOrderMoney().multiply(new BigDecimal(100))).intValue());
        amount.setCurrency(CNY);
        // 设置支付成功后的回调
        request.setNotifyUrl(payUrl);
        request.setAmount(amount);
        //支付项目的名称
//        request.setAttach();
        request.setAppid(wechatPayVo.getAppletCode());
        request.setMchid(wechatPayVo.getMchId());
        request.setOutTradeNo(wechatPayVo.getOrderNum());
        //订单描述
        request.setDescription(wechatPayVo.getDescribe());
        request.setSupportFapiao(true);
        Payer payer = new Payer();
        //前端传递的openId
        payer.setOpenid(wechatPayVo.getOpenid());
        request.setPayer(payer);

        try {
            if (!payServiceMap.containsKey(wechatPayVo.getMchId())) {
                jsapiService(wechatPayVo.getMchId(), wechatPayVo.getApiV3Key(), wechatPayVo.getSerialNo(), wechatPayVo.getKeyPemPath(),
                        wechatPayVo.getApiType(), wechatPayVo.getRsaSerialNo(), wechatPayVo.getPubKeyPath());
            }
        } catch (Exception e) {
            log.error("微信支付初始化失败", e);
            return ResponseResult.error("微信支付初始化失败");
        }
        try {
            // 调用接口
            return ResponseResult.ok(payServiceMap.get(wechatPayVo.getMchId()).prepayWithRequestPayment(request));
        } catch (Exception e) {
            log.error("微信支付统一下单失败", e);
            return ResponseResult.error("微信支付统一下单失败");
        }
    }

    @Override
    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class)
    public JSONObject wechatPayNotifyUrl(String rsaSerialNo, JSONObject jsonObject) {
        //根据商户证书序列号查询账户信息
        AccountDto account = systemService.findAccountByRsaSerialNo(rsaSerialNo).getData();
        if (account == null || StringUtil.isEmpty(account.getApiV3Key())) {
            log.error("商户RSA证书序列号:{}, 未查询到商户信息:{}", rsaSerialNo, account);
            return null;
        }
        //处理回调之后的数据
        Transaction transaction = payDecryptData(account.getApiV3Key(), jsonObject);
        log.info("微信支付回调解密后的数据：{}", transaction);
        if (transaction != null && StringUtil.isNotEmpty(transaction.getOutTradeNo())) {
            String orderNum = transaction.getOutTradeNo();
            List<RechargeTradeEntity> payTradeList = rechargeTradeDao.findAllByOrderNumAndTradeType(orderNum, 1);
            if (CollectionUtils.isNotEmpty(payTradeList)) {
                RechargeTradeEntity rechargeTrade = payTradeList.get(0);
                rechargeTrade.setFlowNum(transaction.getTransactionId());
                rechargeTrade.setTradeState(transaction.getTradeState().name());
                rechargeTrade.setTradeStateDesc(transaction.getTradeStateDesc());
                if (StringUtil.isNotEmpty(transaction.getTradeState())) {
                    switch (transaction.getTradeState()) {
                        case SUCCESS:
                        case REFUND:
                            rechargeTrade.setTradeStatus(2);
                            break;
                        case NOTPAY:
                        case CLOSED:
                            rechargeTrade.setTradeStatus(3);
                            rechargeTrade.setTradeStatus(3);
                            break;
                        default:
                            rechargeTrade.setTradeStatus(3);
                    }
                } else {
                    rechargeTrade.setTradeStatus(3);
                }
                if (StringUtil.isNotEmpty(transaction.getSuccessTime())) {
                    rechargeTrade.setUpdateTime(DateUtil.wxStrToLocalDateTime(transaction.getSuccessTime()));
                }
                rechargeTradeDao.save(rechargeTrade);
                //支付成功 进行充电
                if (rechargeTrade.getTradeStatus() == 2) {
                    if (ChargeServiceImpl.appletUserStartMap.containsKey(rechargeTrade.getAppletUserId())) {
                        ChargeStartVo chargeStartVo = ChargeServiceImpl.appletUserStartMap.get(rechargeTrade.getAppletUserId());
                        if (Objects.equals(chargeStartVo.getOrderNum(), orderNum)) {
                            chargeService.pileStart(orderNum, chargeStartVo);
                        } else {
                            chargeService.pileStart(orderNum, null);
                        }
                    } else {
                        chargeService.pileStart(orderNum, null);
                    }
                }
            }
        }
        JSONObject result = new JSONObject();
        result.put("code", "SUCCESS");
        result.put("message", "成功");
        return result;
    }

    @Override
    public ResponseResult<Transaction> queryWechatPayOrder(WechatPayOrderVo payOrderVo) {
        try {
            if (!payServiceMap.containsKey(payOrderVo.getMchId())) {
                jsapiService(payOrderVo.getMchId(), payOrderVo.getApiV3Key(), payOrderVo.getSerialNo(), payOrderVo.getKeyPemPath(),
                        payOrderVo.getApiType(), payOrderVo.getRsaSerialNo(), payOrderVo.getPubKeyPath());
            }
        } catch (Exception e) {
            log.error("微信支付初始化失败", e);
            return ResponseResult.error("微信支付初始化失败");
        }
        try {
            // 调用接口
            if (payOrderVo.getType() == 1) {
                QueryOrderByIdRequest queryRequest = new QueryOrderByIdRequest();
                queryRequest.setMchid(payOrderVo.getMchId());
                queryRequest.setTransactionId(payOrderVo.getOrderNum());
                //根据商户自定义订单号查询queryOrderByOutTradeNo
                return ResponseResult.ok(payServiceMap.get(payOrderVo.getMchId()).queryOrderById(queryRequest));
            } else {
                QueryOrderByOutTradeNoRequest queryOrderByOutTradeNoRequest = new QueryOrderByOutTradeNoRequest();
                queryOrderByOutTradeNoRequest.setMchid(payOrderVo.getMchId());
                queryOrderByOutTradeNoRequest.setOutTradeNo(payOrderVo.getOrderNum());
                return ResponseResult.ok(payServiceMap.get(payOrderVo.getMchId()).queryOrderByOutTradeNo(queryOrderByOutTradeNoRequest));
            }
        } catch (Exception e) {
            log.error("微信支付查询订单失败", e);
            return ResponseResult.error("微信支付查询订单失败");
        }
    }

    @Override
    public ResponseResult<Refund> wechatRefundOrder(WechatRefundVo wechatRefundVo) {
        CreateRequest createRequest = new CreateRequest();
        createRequest.setOutTradeNo(wechatRefundVo.getPayOrderNum());
        createRequest.setOutRefundNo(wechatRefundVo.getRefundOrderNum());
        createRequest.setReason(wechatRefundVo.getDescribe());
        createRequest.setNotifyUrl(refundUrl);
        AmountReq amountReq = new AmountReq();
        //原订单金额
        amountReq.setTotal((wechatRefundVo.getPayMoney().multiply(new BigDecimal(100))).longValue());
        //退款金额
        amountReq.setRefund((wechatRefundVo.getRefundMoney().multiply(new BigDecimal(100))).longValue());
        amountReq.setCurrency(CNY);
        createRequest.setAmount(amountReq);

        try {
            if (!refundServiceMap.containsKey(wechatRefundVo.getMchId())) {
                jsapiService(wechatRefundVo.getMchId(), wechatRefundVo.getApiV3Key(), wechatRefundVo.getSerialNo(), wechatRefundVo.getKeyPemPath(),
                        wechatRefundVo.getApiType(), wechatRefundVo.getRsaSerialNo(), wechatRefundVo.getPubKeyPath());
            }
        } catch (Exception e) {
            log.error("微信退款初始化失败", e);
            return ResponseResult.error("微信退款初始化失败");
        }
        try {
            // 调用接口
            return ResponseResult.ok(refundServiceMap.get(wechatRefundVo.getMchId()).create(createRequest));
        } catch (Exception e) {
            log.error("微信退款失败", e);
            log.error("订单号:{}, 交易金额:{}, 退款金额：{}", wechatRefundVo.getPayOrderNum(), wechatRefundVo.getPayMoney(), wechatRefundVo.getRefundMoney());
            return ResponseResult.error("微信退款失败");
        }
    }

    @Override
    public JSONObject wechatRefundNotifyUrl(String rsaSerialNo, JSONObject refundObject) {
        //根据商户证书序列号查询账户信息
        AccountDto account = systemService.findAccountByRsaSerialNo(rsaSerialNo).getData();
        if (account == null || StringUtil.isEmpty(account.getApiV3Key())) {
            log.error("微信退款回调，商户平台RSA证书序列号:{}, 未查询到商户信息:{}", rsaSerialNo, account);
            return null;
        }
        JSONObject refundMap = refundDecryptData(account.getApiV3Key(), refundObject);
        log.info("微信退款回调解密后数据：{}", refundMap);
        if (MapUtils.isNotEmpty(refundMap) && StringUtil.isNotEmpty(refundMap.getString("refund_id"))) {
            String flowNum = refundMap.getString("refund_id");
            RechargeTradeEntity rechargeTrade = rechargeTradeDao.findAllByFlowNum(flowNum);
            if (rechargeTrade != null) {
                rechargeTrade.setTradeStatus(3); //默认给交易失败的状态
                String refundStatus = refundMap.getString("refund_status");
                rechargeTrade.setTradeState(refundStatus);
                if (StringUtil.isNotEmpty(refundStatus)) {
                    switch (refundStatus) {
                        case "SUCCESS": //退款成功
                            rechargeTrade.setTradeStatus(2);
                            rechargeTrade.setTradeStateDesc("退款成功");

                            //增加退款记录
//                            togetherService.saveRefundRecord(RefundRecordChangeVo.builder().orderNum(rechargeTrade.getOrderNum())
//                                    .refundAmount(rechargeTrade.getTradeMoney()).build());
                            break;
                        case "CLOSED": //退款关闭
                            rechargeTrade.setTradeStatus(3);
                            rechargeTrade.setTradeStateDesc("退款关闭");
                            break;
                        case "ABNORMAL": //退款异常
                            rechargeTrade.setTradeStatus(3);
                            rechargeTrade.setTradeStateDesc("退款异常");
                            break;
                    }
                }
                if (StringUtil.isNotEmpty(refundMap.getString("success_time"))) {
                    rechargeTrade.setUpdateTime(DateUtil.wxStrToLocalDateTime(refundMap.getString("success_time")));
                }
                rechargeTradeDao.save(rechargeTrade);

            }
        }
        JSONObject result = new JSONObject();
        result.put("code", "SUCCESS");
        result.put("message", "成功");
        return result;
    }

    @Override
    public ResponseResult<Refund> queryWechatRefundOrder(WechatRefundOrderVo refundOrderVo) {
        QueryByOutRefundNoRequest request = new QueryByOutRefundNoRequest();
        request.setOutRefundNo(refundOrderVo.getRefundOrderNum());
        try {
            if (!refundServiceMap.containsKey(refundOrderVo.getMchId())) {
                jsapiService(refundOrderVo.getMchId(), refundOrderVo.getApiV3Key(), refundOrderVo.getSerialNo(), refundOrderVo.getKeyPemPath(),
                        refundOrderVo.getApiType(), refundOrderVo.getRsaSerialNo(), refundOrderVo.getPubKeyPath());
            }
        } catch (Exception e) {
            log.error("微信退款初始化失败", e);
            return ResponseResult.error("微信退款初始化失败");
        }
        try {
            // 调用接口
            return ResponseResult.ok(refundServiceMap.get(refundOrderVo.getMchId()).queryByOutRefundNo(request));
        } catch (Exception e) {
            log.error("查询微信退款订单失败", e);
            return ResponseResult.error("查询微信退款订单失败");
        }
    }

    @Override
    public ResponseResult<WechatMchTransferDto> wechatMchTransfer(WechatMchTransferVo mchTransferVo) {
        try {
            TransferRequestVo transferRequest = new TransferRequestVo();
            transferRequest.setAppid(mchTransferVo.getAppletCode());
            transferRequest.setOutBillNo(mchTransferVo.getTransferNum());
            transferRequest.setTransferSceneId("1009");
            transferRequest.setOpenid(mchTransferVo.getOpenid());
//        transferRequest.setUserName();
            transferRequest.setTransferAmount(mchTransferVo.getTransferMoney().multiply(new BigDecimal(100)).longValue());
            transferRequest.setTransferRemark("V2G放电收入提现");
            transferRequest.setNotifyUrl(transferUrl);
//        transferRequest.setUserRecvPerception();
            transferRequest.setSceneReportInfos(Collections.singletonList(TransferRequestVo.SceneReportInfo.builder()
                    .infoType("采购商品名称")
                    .infoContent("供电-电费")
                    .build()));

            //微信商家转账 调用微信API
            String requestBody = WxPayUtil.toJson(transferRequest); //请求数据
            String host = "https://api.mch.weixin.qq.com";
            String path = "/v3/fund-app/mch-transfer/transfer-bills"; //请求url
            HttpResponse httpResponse = HttpRequest.post(host + path)
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")
                    .header("Wechatpay-Serial", mchTransferVo.getSerialNo())
                    .header("Authorization", WxPayUtil.buildAuthorization(mchTransferVo.getMchId(), mchTransferVo.getSerialNo(),
                            PemUtil.loadPrivateKeyFromString(parseKey(mchTransferVo.getKeyPemPath())),
                            "POST", path, requestBody))
                    .body(requestBody)
                    .timeout(3000).execute();
            log.info("微信商户转账状态:{}, 微信商户转账响应数据:{}", httpResponse.getStatus(), httpResponse.body());
            //响应成功返回的数据
            if (httpResponse.getStatus() == 200) {
                return ResponseResult.ok(WxPayUtil.fromJson(httpResponse.body(), WechatMchTransferDto.class));
            }
            //响应失败返回的信息
            return ResponseResult.paramError(JSONObject.parseObject(httpResponse.body()).getString("message"));
        } catch (Exception e) {
            log.error("微信商户转账失败", e);
            return ResponseResult.error("微信商户转账失败");
        }
    }

    @Override
    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class)
    public JSONObject wechatTransferNotifyUrl(String rsaSerialNo, JSONObject transferObject) {
        //根据商户证书序列号查询账户信息
        AccountDto account = systemService.findAccountByRsaSerialNo(rsaSerialNo).getData();
        if (account == null || StringUtil.isEmpty(account.getApiV3Key())) {
            log.error("微信商户转账回调，商户平台RSA证书序列号:{}, 未查询到商户信息:{}", rsaSerialNo, account);
            return null;
        }
        WechatTransferOrderDto transferOrder = transferDecryptData(account.getApiV3Key(), transferObject);
        log.info("微信商户转账回调解密后数据：{}", transferOrder);
        if (transferOrder != null && StringUtil.isNotEmpty(transferOrder.getTransferBillNo())) {
            String flowNum = transferOrder.getTransferBillNo(); //微信商家转账单号
            DischargeTradeEntity dischargeTrade = dischargeTradeDao.findAllByFlowNum(flowNum);
            if (dischargeTrade != null && StringUtil.isNotEmpty(dischargeTrade.getTradeStatus()) && dischargeTrade.getTradeStatus() == 1) {
                String state = transferOrder.getState();
                dischargeTrade.setTradeState(state);
                if (StringUtil.isNotEmpty(state)) {
                    TransferStatusEnum statusEnum = TransferStatusEnum.getByCode(state);
                    if(statusEnum != null) {
                        dischargeTrade.setTradeStateDesc(statusEnum.getName());
                        switch (statusEnum) {
                            case ACCEPTED:
                            case PROCESSING:
                            case WAIT_USER_CONFIRM:
                            case TRANSFERING:
                            case CANCELING:
                                dischargeTrade.setTradeStatus(1);
                                break;
                            case SUCCESS:
                                dischargeTrade.setTradeStatus(2); //转账成功
                                //转账成功,把冻结金额减掉,返回用户余额
                                ResponseResult<BigDecimal> successResult = togetherService.updateUserDisWallet(UserDisWalletChangeVo.builder()
                                        .appletUserId(dischargeTrade.getAppletUserId())
                                        .accountId(account.getId())
                                        .tradeType(3)
                                        .tradeMoney(dischargeTrade.getTradeMoney()).build());
                                if (!successResult.isSuccess()) {
                                    log.error("微信商户转账成功, 用户余额返回失败, V2G放电余额提现记录:{}", dischargeTrade);
                                } else {
                                    dischargeTrade.setTradeBalance(successResult.getData());
                                }
                                break;
                            case FAIL:
                            case CANCELLED:
                                dischargeTrade.setTradeStatus(3); //转账失败
                                //转账失败,把金额返回给用户余额里面
                                ResponseResult<BigDecimal> failResult = togetherService.updateUserDisWallet(UserDisWalletChangeVo.builder()
                                        .appletUserId(dischargeTrade.getAppletUserId())
                                        .accountId(account.getId())
                                        .tradeType(4)
                                        .tradeMoney(dischargeTrade.getTradeMoney()).build());
                                if (!failResult.isSuccess()) {
                                    log.error("微信商户转账失败, 用户余额返回失败, V2G放电余额提现记录:{}", dischargeTrade);
                                } else {
                                    dischargeTrade.setTradeBalance(failResult.getData());
                                }
                                break;
                        }
                    }
                }
                if (StringUtil.isNotEmpty(transferOrder.getCreateTime())) {
                    dischargeTrade.setCreateTime(DateUtil.wxStrToLocalDateTime(transferOrder.getCreateTime()));
                }
                if (StringUtil.isNotEmpty(transferOrder.getUpdateTime())) {
                    dischargeTrade.setUpdateTime(DateUtil.wxStrToLocalDateTime(transferOrder.getUpdateTime()));
                }
                dischargeTradeDao.save(dischargeTrade);

            }
        }
        JSONObject result = new JSONObject();
        result.put("code", "SUCCESS");
        result.put("message", "成功");
        return result;
    }

    @Override
    public ResponseResult<WechatTransferOrderDto> queryWechatTransferOrder(WechatTransferOrderVo transferOrderVo) {
        try {
            //查询微信商户转账订单信息
            String host = "https://api.mch.weixin.qq.com";
            String path = "/v3/fund-app/mch-transfer/transfer-bills/out-bill-no/{out_bill_no}"; //请求url
            path = path.replace("{out_bill_no}", transferOrderVo.getTransferNum());
            HttpResponse httpResponse = HttpRequest.get(host + path)
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")
                    .header("Wechatpay-Serial", transferOrderVo.getSerialNo())
                    .header("Authorization", WxPayUtil.buildAuthorization(transferOrderVo.getMchId(), transferOrderVo.getSerialNo(),
                            PemUtil.loadPrivateKeyFromString(parseKey(transferOrderVo.getKeyPemPath())),
                            "GET", path, null))
                    .timeout(3000).execute();
            log.info("微信商户转账订单查询状态:{}, 查询响应数据:{}", httpResponse.getStatus(), httpResponse.body());
            //响应成功返回的数据
            if (httpResponse.getStatus() == 200) {
                return ResponseResult.ok(WxPayUtil.fromJson(httpResponse.body(), WechatTransferOrderDto.class));
            }
            //响应失败返回的信息
            return ResponseResult.paramError(JSONObject.parseObject(httpResponse.body()).getString("message"));
        } catch (Exception e) {
            log.error("微信商户转账失败", e);
            return ResponseResult.error("微信商户转账失败");
        }
    }

    public static Transaction payDecryptData(String apiV3Key, JSONObject payObject) {
        //解密 jsonObject 对象
        String associatedData = (String) JSONUtil.getByPath(JSONUtil.parse(payObject), "resource.associated_data");
        String ciphertext = (String) JSONUtil.getByPath(JSONUtil.parse(payObject), "resource.ciphertext");
        String nonce = (String) JSONUtil.getByPath(JSONUtil.parse(payObject), "resource.nonce");
        String decryptData;
        try {
            decryptData = new AesUtil(apiV3Key.getBytes(StandardCharsets.UTF_8)).decryptToString(associatedData.getBytes(StandardCharsets.UTF_8),
                    nonce.getBytes(StandardCharsets.UTF_8), ciphertext);
        } catch (GeneralSecurityException | IOException e) {
            throw new RuntimeException(e);
        }
        return GsonUtil.getGson().fromJson(decryptData, Transaction.class);
    }

    public static JSONObject refundDecryptData(String apiV3Key, JSONObject payObject) {
        //解密 jsonObject 对象
        String associatedData = (String) JSONUtil.getByPath(JSONUtil.parse(payObject), "resource.associated_data");
        String ciphertext = (String) JSONUtil.getByPath(JSONUtil.parse(payObject), "resource.ciphertext");
        String nonce = (String) JSONUtil.getByPath(JSONUtil.parse(payObject), "resource.nonce");
        String decryptData;
        try {
            decryptData = new AesUtil(apiV3Key.getBytes(StandardCharsets.UTF_8)).decryptToString(associatedData.getBytes(StandardCharsets.UTF_8),
                    nonce.getBytes(StandardCharsets.UTF_8), ciphertext);
        } catch (GeneralSecurityException | IOException e) {
            throw new RuntimeException(e);
        }
        return JSONObject.parseObject(decryptData);
    }

    public static WechatTransferOrderDto transferDecryptData(String apiV3Key, JSONObject payObject) {
        //解密 jsonObject 对象
        String associatedData = (String) JSONUtil.getByPath(JSONUtil.parse(payObject), "resource.associated_data");
        String ciphertext = (String) JSONUtil.getByPath(JSONUtil.parse(payObject), "resource.ciphertext");
        String nonce = (String) JSONUtil.getByPath(JSONUtil.parse(payObject), "resource.nonce");
        String decryptData;
        try {
            decryptData = new AesUtil(apiV3Key.getBytes(StandardCharsets.UTF_8)).decryptToString(associatedData.getBytes(StandardCharsets.UTF_8),
                    nonce.getBytes(StandardCharsets.UTF_8), ciphertext);
        } catch (GeneralSecurityException | IOException e) {
            throw new RuntimeException(e);
        }
        return GsonUtil.getGson().fromJson(decryptData, WechatTransferOrderDto.class);
    }


}
