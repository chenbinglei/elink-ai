package com.sunmax.webapp.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sunmax.common.config.redis.RedisDeviceUtil;
import com.sunmax.common.config.redis.RedisGeneralUtil;
import com.sunmax.common.dto.device.DeviceBasicInfoDto;
import com.sunmax.common.dto.device.SiteInfoDto;
import com.sunmax.common.dto.protocol.PileResultDto;
import com.sunmax.common.dto.system.AccountDto;
import com.sunmax.common.dto.together.AppletUserInfoDto;
import com.sunmax.common.dto.together.OrderDetailDto;
import com.sunmax.common.dto.together.SiteAccountDto;
import com.sunmax.common.dto.webapp.ChargePriceInfoDto;
import com.sunmax.common.model.DeviceModel;
import com.sunmax.common.model.general.PileRealModel;
import com.sunmax.common.util.DateUtil;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.util.StringUtil;
import com.sunmax.common.vo.OrderSerialParamVo;
import com.sunmax.common.vo.SiteFieldParamVo;
import com.sunmax.common.vo.protocol.PileStartVo;
import com.sunmax.common.vo.together.OrderChangeVo;
import com.sunmax.common.vo.together.OrderUpdateVo;
import com.sunmax.common.vo.together.RefundRecordChangeVo;
import com.sunmax.common.vo.webapp.AppletChargeRefundVo;
import com.sunmax.webapp.dao.trade.RechargeTradeDao;
import com.sunmax.webapp.entity.trade.RechargeTradeEntity;
import com.sunmax.webapp.service.ChargeService;
import com.sunmax.webapp.service.TradeService;
import com.sunmax.webapp.service.feign.DeviceService;
import com.sunmax.webapp.service.feign.ProtocolService;
import com.sunmax.webapp.service.feign.SystemService;
import com.sunmax.webapp.service.feign.TogetherService;
import com.sunmax.webapp.util.TimedCacheUtil;
import com.sunmax.webapp.util.TradeCreateUtil;
import com.sunmax.webapp.vo.AppletChargeStartVo;
import com.sunmax.webapp.vo.ChargeStartVo;
import com.sunmax.webapp.vo.wechat.WechatPayVo;
import com.sunmax.webapp.vo.wechat.WechatRefundVo;
import com.wechat.pay.java.service.payments.jsapi.model.PrepayWithRequestPaymentResponse;
import com.wechat.pay.java.service.refund.model.Refund;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.*;

@Service
@Slf4j
public class ChargeServiceImpl implements ChargeService {

    @Autowired
    private TogetherService togetherService;

    @Autowired
    private ProtocolService protocolService;

    @Autowired
    private RechargeTradeDao rechargeTradeDao;

    @Autowired
    private TradeService tradeService;

    @Autowired
    private SystemService systemService;

    @Autowired
    private DeviceService deviceService;

    public static final TimedCacheUtil<String, ChargeStartVo> appletUserStartMap = new TimedCacheUtil<>();

    //自定义线程池
    private final ThreadPoolExecutor threadPool = new ThreadPoolExecutor(
            100,//核心线程数
            200,//最大线程数
            130,//超时时间
            TimeUnit.SECONDS,//超时时间单位
            new ArrayBlockingQueue<>(500),//阻塞队列，长度为3
            Executors.defaultThreadFactory(),//默认线程工厂
            new ThreadPoolExecutor.AbortPolicy()//拒绝策略：丢弃
    );


    @Override
    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class)
    public ResponseResult<?> appletChargeStart(AppletChargeStartVo appletStartVo) {
        //1. 校验参数
        if (StringUtil.isEmpty(appletStartVo.getAppletUserId())) {
            return ResponseResult.paramError("小程序用户id为空");
        }
        if (appletUserStartMap.containsKey(appletStartVo.getAppletUserId())) {
            return ResponseResult.paramError("用户启动过程中, 不允许重复启动");
        }
        if (StringUtil.isEmpty(appletStartVo.getSiteId())) {
            return ResponseResult.paramError("站点id为空");
        }
        if (StringUtil.isEmpty(appletStartVo.getPileCode())) {
            return ResponseResult.paramError("电桩编号为空");
        }

        //校验设备是否在线
        DeviceModel deviceModel = RedisDeviceUtil.getDevice(appletStartVo.getPileCode());
        if (deviceModel == null || StringUtil.isEmpty(deviceModel.getTxStatus()) || deviceModel.getTxStatus() == 0) {
            return ResponseResult.paramError("设备未注册, 不允许启动");
        }
        PileRealModel pileRealModel = RedisGeneralUtil.getPileRealModel(appletStartVo.getPileCode());
        if (deviceModel.getTxStatus() == 88 || pileRealModel == null || StringUtil.isEmpty(pileRealModel.getWorkStatus()) || pileRealModel.getWorkStatus() == 88) {
            return ResponseResult.paramError("设备已离线, 不允许启动");
        }

        //判断该站点是否支持预约
        if (StringUtil.isNotEmpty(appletStartVo.getClockingTime())) {
            //根据站点id查询站点数据
            SiteInfoDto siteInfo = deviceService.findSiteBasicInfoByIds(Collections.singletonList(appletStartVo.getSiteId())).getData().get(appletStartVo.getSiteId());
            if (siteInfo != null && StringUtil.isNotEmpty(siteInfo.getSiteReadwriteObject())) {
                JSONObject jsonObject = JSON.parseObject(siteInfo.getSiteReadwriteObject());
                //是否支持预约 0-不支持 1-支持
                if (jsonObject.containsKey(SiteFieldParamVo.SUPPORT_ORDER) && StringUtil.isNotEmpty(jsonObject.get(SiteFieldParamVo.SUPPORT_ORDER))) {
                    Integer supportOrder = jsonObject.getInteger(SiteFieldParamVo.SUPPORT_ORDER);
                    if (supportOrder == 0) {
                        return ResponseResult.paramError("该站点不支持预约");
                    }
                }
            }
        }

        //校验该设备是否有费率
        DeviceBasicInfoDto device = deviceService.findDeviceBasicInfoByCodes(Collections.singletonList(appletStartVo.getPileCode()))
                .getData().get(appletStartVo.getPileCode());
        if (device == null || StringUtil.isEmpty(device.getId())) {
            return ResponseResult.paramError("平台上面未查询到该设备");
        }
        ChargePriceInfoDto chargePriceInfo = togetherService.findDevicePriceById(device.getId(), appletStartVo.getRunMode() + 1).getData();
        if (StringUtil.isEmpty(chargePriceInfo) || StringUtil.isEmpty(chargePriceInfo.getChargerPrice())
                || StringUtil.isEmpty(chargePriceInfo.getChargerPrice().getPeriodType())) {
            return ResponseResult.paramError("未配置该设备对应的费率");
        }
//        if (StringUtil.isEmpty(appletStartVo.getPlatform())) {
//            return ResponseResult.paramError("平台类型为空");
//        }
        if (appletStartVo.getPayWay() != 1 && StringUtil.isEmpty(appletStartVo.getPrepayMoney())) {
            return ResponseResult.paramError("预付金额为空 不允许充电");
        }

        if (appletStartVo.getPayWay() != 1 && appletStartVo.getPrepayMoney().compareTo(BigDecimal.ZERO) <= 0) {
            return ResponseResult.paramError("预付金额小于等于0 不允许充电");
        }

        //2. 查询用户小程序标识
        AppletUserInfoDto appletUser = togetherService.findAppletUserByIds(Collections.singleton(appletStartVo.getAppletUserId()))
                .getData().get(appletStartVo.getAppletUserId());
        if (appletUser != null) {
            //生成订单号
            String orderNum = togetherService.generateOrderNum(appletStartVo.getPileCode(), OrderSerialParamVo.PAY_NUMBER).getData();
            ChargeStartVo chargeStartVo = new ChargeStartVo();
            BeanUtils.copyProperties(appletStartVo, chargeStartVo);
            chargeStartVo.setOrderNum(orderNum);
            //支付方式 1-免支付 2-微信支付 3-支付宝支付
            switch (appletStartVo.getPayWay()) {
                case 1:
                    appletUserStartMap.put(appletStartVo.getAppletUserId(), chargeStartVo, 60);
                    Boolean result = this.pileStart(orderNum, appletStartVo);
                    if (result) {
                        return ResponseResult.ok();
                    }
                    return ResponseResult.paramError("充电桩启动失败");
                case 2:
                    String openid = appletUser.getOpenid();
                    String appletCode = appletUser.getAppletCode();

                    if (StringUtil.isNotEmpty(appletUser.getOpenid()) && StringUtil.isNotEmpty(appletCode)) {
                        SiteAccountDto siteAccount = new SiteAccountDto();
                        //根据站点id查询微信商户
                        String resultMsg = this.getSiteAccount(appletStartVo.getSiteId(), siteAccount);
                        if (StringUtil.isNotEmpty(resultMsg)) {
                            return ResponseResult.paramError(resultMsg);
                        }
                        WechatPayVo wechatPayVo = new WechatPayVo();
                        BeanUtils.copyProperties(siteAccount, wechatPayVo);
                        wechatPayVo.setOpenid(openid);
                        wechatPayVo.setOrderNum(orderNum);
                        wechatPayVo.setOrderMoney(appletStartVo.getPrepayMoney());
                        wechatPayVo.setDescribe("用户" + appletUser.getPhoneNum() + "预付" + appletStartVo.getPrepayMoney() + "元");
                        wechatPayVo.setAppletCode(appletCode);
                        ResponseResult<PrepayWithRequestPaymentResponse> responseResult = tradeService.wechatPayUnifiedOrder(wechatPayVo);
                        //创建微信交易支付数据
                        RechargeTradeEntity tradePay = TradeCreateUtil.createTrade(orderNum, 1, 1, appletStartVo.getPrepayMoney(),
                                appletStartVo.getAppletUserId(), appletStartVo.getSiteId(), siteAccount.getAccountId());
                        if (responseResult.isSuccess()) {
                            appletUserStartMap.put(appletStartVo.getAppletUserId(), chargeStartVo, 120);
                        } else {
                            tradePay.setTradeStatus(3);
                        }
                        log.info("创建微信交易支付数据:{}", tradePay);
                        rechargeTradeDao.save(tradePay);
                        return responseResult;
                    }
                    break;
                case 3:
                    break;
            }
        }
        return ResponseResult.paramError(ResponseResult.PARAM_ERROR);
    }

    @Override
    public ResponseResult<Void> appletChargeRefund(AppletChargeRefundVo appletRefundVo) {
        //根据订单号查询充电支付订单
        RechargeTradeEntity payTrade = rechargeTradeDao.findAllByOrderNumAndTradeTypeAndTradeStatus(appletRefundVo.getOrderNum(), 1, 2);
        if (payTrade != null && payTrade.getTradeWay() == 1) {
            String refundOrderNum = togetherService.generateOrderNum(appletRefundVo.getPileCode(), OrderSerialParamVo.REFUND_NUMBER).getData();
            //创建微信交易退款数据
            RechargeTradeEntity tradeRefund = TradeCreateUtil.createTrade(appletRefundVo.getOrderNum(), 2, 1, appletRefundVo.getRefundMoney(),
                    payTrade.getAppletUserId(), payTrade.getSiteId(), payTrade.getAccountId());
            AccountDto account = systemService.findAccountListByAccountIds(Collections.singleton(payTrade.getAccountId()))
                    .getData().get(payTrade.getAccountId());
            WechatRefundVo wechatRefundVo = new WechatRefundVo();
            wechatRefundVo.setPayOrderNum(payTrade.getOrderNum());
            wechatRefundVo.setPayMoney(payTrade.getTradeMoney());
            wechatRefundVo.setRefundOrderNum(refundOrderNum);
            wechatRefundVo.setRefundMoney(appletRefundVo.getRefundMoney());
            wechatRefundVo.setDescribe("订单号" + appletRefundVo.getOrderNum() + "结算退款");
            wechatRefundVo.setMchId(account.getMchId());
            wechatRefundVo.setApiType(account.getApiType());
            wechatRefundVo.setApiV3Key(account.getApiV3Key());
            wechatRefundVo.setSerialNo(account.getSerialNo());
            wechatRefundVo.setKeyPemPath(account.getKeyPemPath());
            wechatRefundVo.setRsaSerialNo(account.getRsaSerialNo());
            wechatRefundVo.setPubKeyPath(account.getPubKeyPath());
            try {
                ResponseResult<Refund> responseResult = tradeService.wechatRefundOrder(wechatRefundVo);
                if (!responseResult.isSuccess()) {
                    return ResponseResult.paramError(ResponseResult.FAIL);
                }
                Refund refund = responseResult.getData();
                if (refund != null) {
                    tradeRefund.setRefundNum(refund.getOutRefundNo());
                    tradeRefund.setFlowNum(refund.getRefundId());
                    tradeRefund.setCreateTime(DateUtil.wxStrToLocalDateTime(refund.getCreateTime()));
                    if (StringUtil.isNotEmpty(refund.getStatus())) {
                        switch (refund.getStatus()) {
                            case SUCCESS:
                                tradeRefund.setTradeStatus(2);
                                break;
                            case PROCESSING:
                                tradeRefund.setTradeStatus(1);
                                break;
                            case CLOSED:
                            case ABNORMAL:
                                tradeRefund.setTradeStatus(3);
                                break;
                        }
                    }
                    if (StringUtil.isNotEmpty(refund.getSuccessTime())) {
                        tradeRefund.setUpdateTime(DateUtil.wxStrToLocalDateTime(refund.getSuccessTime()));
                    }
                    //增加退款记录
                    if (tradeRefund.getTradeStatus() != 3) {
                        togetherService.saveRefundRecord(RefundRecordChangeVo.builder().orderNum(tradeRefund.getOrderNum())
                                .refundAmount(tradeRefund.getTradeMoney()).refundOperator(appletRefundVo.getRefundOperator()).build());
                    }
                    //更新订单状态及结算数据
                    if (tradeRefund.getTradeStatus() != 3 && appletRefundVo.getType() == 1) { //启动失败退款
                        OrderUpdateVo orderUpdateVo = new OrderUpdateVo();
                        orderUpdateVo.setOrderNum(payTrade.getOrderNum());
                        orderUpdateVo.setOrderStatus(3);
                        orderUpdateVo.setSettlementState(3);
                        orderUpdateVo.setRefundMoney(payTrade.getTradeMoney());
                        orderUpdateVo.setAbnormalCode(JSON.toJSONString(Arrays.asList(4, 5)));
                        togetherService.updatePileFailOrder(orderUpdateVo);
                    }
                }
                tradeRefund.setType(appletRefundVo.getType());
                rechargeTradeDao.save(tradeRefund);
                return ResponseResult.ok();
            } catch (Exception e) {
                log.error("微信退款异常:{}", e.getMessage());
                log.info("订单号:{}, 交易金额:{}, 退款金额：{}", appletRefundVo.getOrderNum(), wechatRefundVo.getPayMoney(), wechatRefundVo.getRefundMoney());
                return ResponseResult.paramError(ResponseResult.FAIL);
            }
        }
        return ResponseResult.paramError(ResponseResult.PARAM_ERROR);
    }

    @Override
    public Boolean pileStart(String orderNum, AppletChargeStartVo appletStartVo) {
        log.info("用户启动充电订单号:{}", orderNum);
        //校验订单号是否存在 存在则不执行 防止重复启动 用于检测微信支付重复回调
        OrderDetailDto order = togetherService.findOrderDetailByOrderNum(orderNum).getData();
        if (order != null && (StringUtil.isNotEmpty(order.getOrderNum()) || StringUtil.isNotEmpty(order.getOrderStatus()))) {
            return false;
        }

        //超过缓存支付启动时间 退款
        if (appletStartVo == null) {
            this.refundOrderProcess(orderNum);
            return false;
        }
        try {
            ResponseResult<Void> pileOrder = togetherService.createPileOrder(this.getOrderChangeVo(orderNum, appletStartVo));
            if (pileOrder != null && pileOrder.isSuccess()) {
                CompletableFuture.runAsync(() -> {
                    try {
                        PileStartVo pileStartVo = this.getPileStartVo(orderNum, appletStartVo);
                        ResponseResult<PileResultDto> pileStartResult = protocolService.pileStart(pileStartVo);
                        appletUserStartMap.remove(appletStartVo.getAppletUserId());
                        log.info("用户本次启动订单号:{}, 响应数据:{}", orderNum, pileStartResult);

                        //是否退款状态
                        boolean isRefund = true;
                        boolean isRebate = false;
                        if (pileStartResult != null && pileStartResult.getData() != null && StringUtil.isNotEmpty(pileStartResult.getData().getResult())) {
                            //执行结果 -1-执行超时 0-执行成功 1-执行失败 255-其他原因 500-平台处理报错
                            Integer result = pileStartResult.getData().getResult();
                            if (result == 0) {
                                isRefund = false;
                            }
                            if (result == 1) {
                                isRebate = true;
                            }
                        }
                        //校验是否在充电 不在则进行退款
                        if (isRefund) {
                            this.refundOrderProcess(orderNum, appletStartVo.getPileCode(), appletStartVo.getGunCode(), isRebate);
                        }
                    } catch (Exception e) {
                        appletUserStartMap.remove(appletStartVo.getAppletUserId());
                        //启动超时 判断是否退款
                        this.refundOrderProcess(orderNum, appletStartVo.getPileCode(), appletStartVo.getGunCode(), false);
                        log.error("微信支付用户启动桩结束订单报错", e);
                    }
                }, threadPool);
                return true;
            }
            log.error("创建订单失败: {}", pileOrder);
            appletUserStartMap.remove(appletStartVo.getAppletUserId());
        } catch (Exception e) {
            appletUserStartMap.remove(appletStartVo.getAppletUserId());
            log.error("启动充电接口报错: ", e);
            return false;
        }
        return false;
    }

    private OrderChangeVo getOrderChangeVo(String orderNum, AppletChargeStartVo appletStartVo) {
        OrderChangeVo orderChangeVo = new OrderChangeVo();
        BeanUtils.copyProperties(appletStartVo, orderChangeVo);
        if (StringUtil.isNumber(appletStartVo.getGunCode())) {
            orderChangeVo.setGunCode(Integer.parseInt(appletStartVo.getGunCode()));
        }
        orderChangeVo.setOrderNum(orderNum);
        orderChangeVo.setOrderStatus(0);
//        orderChangeVo.setStrategyTime();
        orderChangeVo.setSettlementState(0);
        return orderChangeVo;
    }

    private PileStartVo getPileStartVo(String orderNum, AppletChargeStartVo appletStartVo) {
        PileStartVo pileStartVo = new PileStartVo();
        BeanUtils.copyProperties(appletStartVo, pileStartVo);
        pileStartVo.setSerialNum(orderNum);
        pileStartVo.setStrategy(appletStartVo.getStrategyType());
        pileStartVo.setIsStore(false);
        return pileStartVo;
    }

    //获取站点收款和付款账户信息
    private String getSiteAccount(String siteId, SiteAccountDto result) {
        String resultMsg = null;
        //根据站点id查询微信商户
        List<SiteAccountDto> siteAccountList = togetherService.findSiteAccountListBySiteIds(Collections.singleton(siteId), 1).getData().get(siteId);
        if (CollectionUtils.isNotEmpty(siteAccountList)) {
            SiteAccountDto siteAccount = siteAccountList.get(0);
            if (StringUtil.isEmpty(siteAccount.getMchId())) {
                resultMsg = "未配置微信商户号";
            }
            if (StringUtil.isEmpty(siteAccount.getApiV3Key())) {
                resultMsg = "未配置微信APIv3密钥";
            }
            if (StringUtil.isEmpty(siteAccount.getSerialNo())) {
                resultMsg = "未配置微信API证书序列号";
            }
            if (StringUtil.isEmpty(siteAccount.getKeyPemPath())) {
                resultMsg = "未配置微信API证书";
            }
            if (StringUtil.isEmpty(siteAccount.getRsaSerialNo())) {
                resultMsg = "未配置微信商户平台RSA证书序列号(商户公钥id)";
            }
            BeanUtils.copyProperties(siteAccount, result);
        }
        return resultMsg;
    }

    public void refundOrderProcess(String orderNum, String pileCode, String gunCode, Boolean isRebate) {
        //根据订单号查询充电支付订单
        List<RechargeTradeEntity> rechargeTradeList = rechargeTradeDao.findAllByOrderNumAndTradeType(orderNum, 1);
        if (CollectionUtils.isNotEmpty(rechargeTradeList)) {
            RechargeTradeEntity rechargeTrade = rechargeTradeList.get(0);
            //是否直接退款
            if (isRebate) {
                //校验是否退款
                if (checkRefundOrder(orderNum)) {
                    log.info("退款订单号:{},已退款无需退款", orderNum);
                    return;
                }
                log.info("充电失败,退款订单号:{}", orderNum);
                AppletChargeRefundVo appletRefundVo = new AppletChargeRefundVo();
                appletRefundVo.setOrderNum(orderNum);
                appletRefundVo.setPileCode(pileCode);
                appletRefundVo.setRefundMoney(rechargeTrade.getTradeMoney());
                appletRefundVo.setType(1);
                ResponseResult<Void> result = this.appletChargeRefund(appletRefundVo);
                if (!result.isSuccess()) {
                    log.info("充电失败,退款失败,订单号: {}", orderNum);
                }
            } else {
                PileRealModel pileRealModel = RedisGeneralUtil.getPileRealModel(pileCode);
                if (pileRealModel != null && pileRealModel.getWorkStatus() != 88 && pileRealModel.getGunRealModelMap().containsKey(gunCode)) {
                    boolean isRefund = false;
                    //枪不在工作中进行退款
                    PileRealModel.GunRealModel gunRealModel = pileRealModel.getGunRealModelMap().get(gunCode);
                    Integer gunStatus = gunRealModel.getGunStatus();
                    if (StringUtil.isNotEmpty(gunStatus) && gunStatus != 1 && gunStatus != 2 && gunStatus != 4 && gunStatus != 5) {
                        isRefund = true;
                    }
                    if (!isRefund && StringUtil.isNotEmpty(gunRealModel.getSerialNum()) && !Objects.equals(gunRealModel.getSerialNum(), orderNum)) {
                        isRefund = true;
                    }
                    if (isRefund) {
                        //校验是否退款
                        if (checkRefundOrder(orderNum)) {
                            log.info("启动异常,退款订单号:{}, 枪状态:{},已退款无需退款", orderNum, gunStatus);
                            return;
                        }
                        log.info("启动异常,退款订单号:{}, 枪状态:{}", orderNum, gunStatus);
                        AppletChargeRefundVo appletRefundVo = new AppletChargeRefundVo();
                        appletRefundVo.setOrderNum(orderNum);
                        appletRefundVo.setPileCode(pileCode);
                        appletRefundVo.setRefundMoney(rechargeTrade.getTradeMoney());
                        appletRefundVo.setType(1);
                        ResponseResult<Void> result = this.appletChargeRefund(appletRefundVo);
                        if (!result.isSuccess()) {
                            log.info("启动异常退款失败, 订单号: {}", orderNum);
                        }
                    }
                }
            }
        } else {
            //不产生交易 更改订单状态
            OrderUpdateVo orderUpdateVo = new OrderUpdateVo();
            orderUpdateVo.setOrderNum(orderNum);
            orderUpdateVo.setOrderStatus(3);
            orderUpdateVo.setSettlementState(1);
            orderUpdateVo.setRefundMoney(new BigDecimal(0));
            orderUpdateVo.setAbnormalCode(JSON.toJSONString(Arrays.asList(4, 5)));
            togetherService.updatePileFailOrder(orderUpdateVo);
        }
    }

    public void refundOrderProcess(String orderNum) {
        //根据订单号查询充电支付订单
        RechargeTradeEntity rechargeTrade = rechargeTradeDao.findAllByOrderNumAndTradeTypeAndTradeStatus(orderNum, 1, 2);
        if (rechargeTrade != null) {
            log.info("超过规定时间支付退款，退款订单号:{}", orderNum);
            AppletChargeRefundVo appletRefundVo = new AppletChargeRefundVo();
            appletRefundVo.setOrderNum(orderNum);
            if (orderNum.length() > 16) {
                appletRefundVo.setPileCode(orderNum.substring(0, 16));
            } else {
                appletRefundVo.setPileCode(orderNum);
            }
            appletRefundVo.setRefundMoney(rechargeTrade.getTradeMoney());
            appletRefundVo.setType(1);
            ResponseResult<Void> result = this.appletChargeRefund(appletRefundVo);
            if (!result.isSuccess()) {
                log.info("超过规定时间支付退款失败，订单号: {}", orderNum);
            }
        }

    }

    private Boolean checkRefundOrder(String orderNum) {
        //检查是否退款 退款过则无需退款
        List<RechargeTradeEntity> refundTradeList = rechargeTradeDao.findAllByOrderNumAndTradeType(orderNum, 2);
        return CollectionUtils.isNotEmpty(refundTradeList);
    }
}
