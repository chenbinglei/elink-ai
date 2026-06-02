package com.sunmax.webapp.task;

import com.sunmax.common.dto.system.AccountDto;
import com.sunmax.common.dto.together.AppletUserInfoDto;
import com.sunmax.common.util.DateUtil;
import com.sunmax.common.util.StringUtil;
import com.sunmax.webapp.dao.trade.RechargeTradeDao;
import com.sunmax.webapp.entity.trade.RechargeTradeEntity;
import com.sunmax.webapp.service.TradeService;
import com.sunmax.webapp.service.feign.SystemService;
import com.sunmax.webapp.service.feign.TogetherService;
import com.sunmax.webapp.vo.wechat.WechatPayOrderVo;
import com.sunmax.webapp.vo.wechat.WechatRefundOrderVo;
import com.wechat.pay.java.service.payments.model.Transaction;
import com.wechat.pay.java.service.refund.model.Refund;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.compress.utils.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Example;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 交易明细任务
 */
@Configuration
@Slf4j
public class TradeTask {

    @Autowired
    private RechargeTradeDao rechargeTradeDao;

    @Autowired
    private SystemService systemService;

    @Autowired
    private TradeService tradeService;

    @Autowired
    private TogetherService togetherService;

    /**
     * 定时任务：微信充电交易订单状态处理
     * 执行周期：1分钟
     */
    @Scheduled(cron = "0 0/1 * * * ?")
    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class)
    public void updateOrderPayStatus() {

        //定义充电预付和退款交易实体类
        List<RechargeTradeEntity> updateRecTradeList = Lists.newArrayList();

        //1.查询微信充电交易的订单
        List<RechargeTradeEntity> rechargeTradeList = rechargeTradeDao.findAll(Example.of(RechargeTradeEntity.builder().tradeWay(1).tradeStatus(1).build()));
        if (CollectionUtils.isNotEmpty(rechargeTradeList)) {
            //根据多个账户id查询账户信息
            Set<String> accountIds = rechargeTradeList.stream().map(RechargeTradeEntity::getAccountId).collect(Collectors.toSet());
            Map<String, AccountDto> accountMap = systemService.findAccountListByAccountIds(accountIds).getData();

            //根据多个小程序主键id查询小程序标识
            Set<String> appletUserIds = rechargeTradeList.stream().map(RechargeTradeEntity::getAppletUserId).collect(Collectors.toSet());
            Map<String, AppletUserInfoDto> appletUserMap = togetherService.findAppletUserByIds(appletUserIds).getData();

            //微信充电预付订单处理
            updateRecTradeList.addAll(rechargeTradeList.stream().filter(t -> Objects.equals(t.getTradeType(), 1)).peek(trade -> {
                WechatPayOrderVo payOrderVo = new WechatPayOrderVo();
                payOrderVo.setType(2);
                payOrderVo.setOrderNum(trade.getOrderNum());
                if (appletUserMap.containsKey(trade.getAppletUserId())) {
                    payOrderVo.setAppletId(appletUserMap.get(trade.getAppletUserId()).getAppletCode());
                }
                if (accountMap.containsKey(trade.getAccountId())) {
                    AccountDto account = accountMap.get(trade.getAccountId());
                    payOrderVo.setMchId(account.getMchId());
                    payOrderVo.setApiType(account.getApiType());
                    payOrderVo.setApiV3Key(account.getApiV3Key());
                    payOrderVo.setSerialNo(account.getSerialNo());
                    payOrderVo.setKeyPemPath(account.getKeyPemPath());
                    payOrderVo.setRsaSerialNo(account.getRsaSerialNo());
                    payOrderVo.setPubKeyPath(account.getPubKeyPath());
                }
                Transaction transaction = tradeService.queryWechatPayOrder(payOrderVo).getData();
                if (transaction != null) {
                    trade.setFlowNum(transaction.getTransactionId());
                    if (StringUtil.isNotEmpty(transaction.getTradeState())) {
                        switch (transaction.getTradeState()) {
                            case SUCCESS:
                            case REFUND:
                                trade.setTradeStatus(2);
                                break;
                            case NOTPAY:
                            case CLOSED:
                                trade.setTradeStatus(3);
                                trade.setTradeStatus(3);
                                break;
                            default:
                                trade.setTradeStatus(1);
                        }
                        if (StringUtil.isNotEmpty(transaction.getSuccessTime())) {
                            trade.setUpdateTime(DateUtil.wxStrToLocalDateTime(transaction.getSuccessTime()));
                        }
                    } else {
                        trade.setTradeStatus(3);
                    }
                } else {
                    trade.setTradeStatus(3);
                }
            }).collect(Collectors.toList()));

            //微信充电退款订单处理
            updateRecTradeList.addAll(rechargeTradeList.stream().filter(t -> Objects.equals(t.getTradeType(), 2)).peek(trade -> {
                if (StringUtil.isNotEmpty(trade.getRefundNum())) {
                    WechatRefundOrderVo refundOrderVo = new WechatRefundOrderVo();
                    refundOrderVo.setRefundOrderNum(trade.getRefundNum());
                    if (accountMap.containsKey(trade.getAccountId())) {
                        AccountDto account = accountMap.get(trade.getAccountId());
                        refundOrderVo.setMchId(account.getMchId());
                        refundOrderVo.setApiType(account.getApiType());
                        refundOrderVo.setApiV3Key(account.getApiV3Key());
                        refundOrderVo.setSerialNo(account.getSerialNo());
                        refundOrderVo.setKeyPemPath(account.getKeyPemPath());
                        refundOrderVo.setRsaSerialNo(account.getRsaSerialNo());
                        refundOrderVo.setPubKeyPath(account.getPubKeyPath());
                    }
                    Refund refund = tradeService.queryWechatRefundOrder(refundOrderVo).getData();
                    if (StringUtil.isNotEmpty(refund.getStatus())) {
                        switch (refund.getStatus()) {
                            case SUCCESS:
                                trade.setTradeStatus(2);
                                if (StringUtil.isNotEmpty(refund.getSuccessTime())) {
                                    trade.setUpdateTime(DateUtil.wxStrToLocalDateTime(refund.getSuccessTime()));
                                }
                                break;
                            case PROCESSING:
                                trade.setTradeStatus(1);
                                break;
                            case CLOSED:
                            case ABNORMAL:
                                trade.setTradeStatus(3);
                                if (StringUtil.isNotEmpty(refund.getSuccessTime())) {
                                    trade.setUpdateTime(DateUtil.wxStrToLocalDateTime(refund.getSuccessTime()));
                                }
                                break;
                        }
                    } else {
                        trade.setTradeStatus(3);
                    }
                }
            }).collect(Collectors.toList()));

        }

        if (CollectionUtils.isNotEmpty(updateRecTradeList)) {
            rechargeTradeDao.batchUpdate(updateRecTradeList);
        }

    }

//    /**
//     * 定时任务：微信放电余额提现订单处理(处理1小时之内在处理的订单)
//     * 执行周期：5分钟
//     */
//    @Scheduled(cron = "0 0/5 * * * ?")
//    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class)
//    public void updateOrderTransferStatus() {
//        //根据查询条件查询放电余额提现订单
//        List<DischargeTradeEntity> dischargeTradeList = dischargeTradeDao.findAll((Specification<DischargeTradeEntity>) (root, cq, cb) -> {
//            List<Predicate> predicates = Lists.newArrayList();
//            LocalDateTime queryTime = LocalDateTime.now().minusHours(1);;
//            predicates.add(cb.lessThan(root.get("createTime"), queryTime));//1小时内未处理的订单
//            predicates.add(cb.equal(root.get("tradeWay"), 1));//交易方式 1-微信 2-支付宝 3-银联商户
//            predicates.add(cb.equal(root.get("tradeType"), 2));//交易类型 1-V2G收益存入 2-余额提现
//            predicates.add(cb.equal(root.get("tradeStatus"), 1));//交易状态 1-处理中 2-处理成功 3-处理失败
//            return cb.and(predicates.toArray(new Predicate[0]));
//        });
//
//        List<DischargeTradeEntity> updateDisTradeList = Lists.newArrayList();
//
//        if (CollectionUtils.isNotEmpty(dischargeTradeList)) {
//
//            //3.根据多个账号id查询账号数据
//            Map<String, AccountDto> accountMap = Maps.newHashMap();
//            Set<String> accountIds = dischargeTradeList.stream().map(DischargeTradeEntity::getAccountId).collect(Collectors.toSet());
//            if (CollectionUtils.isNotEmpty(accountIds)) {
//                accountMap = systemService.findAccountListByAccountIds(accountIds).getData();
//            }
//
//            //4.根据多个小程序用户id查询小程序用户数据
//            Map<String, AppletUserInfoDto> appletUserMap = Maps.newHashMap();
//            Set<String> appletUserIds = dischargeTradeList.stream().map(DischargeTradeEntity::getAppletUserId).collect(Collectors.toSet());
//            if (CollectionUtils.isNotEmpty(appletUserIds)) {
//                appletUserMap = togetherService.findAppletUserByIds(appletUserIds).getData();
//            }
//
//            //微信商户转账订单处理
//            Map<String, AppletUserInfoDto> finalAppletUserMap = appletUserMap;
//            Map<String, AccountDto> finalAccountMap = accountMap;
//            updateDisTradeList = dischargeTradeList.stream().filter(d -> StringUtil.isNotEmpty(d.getOrderNum()) && StringUtil.isNotEmpty(d.getFlowNum())).peek(dischargeTrade -> {
//
//                WechatTransferOrderVo transferOrderVo = new WechatTransferOrderVo();
//                transferOrderVo.setTransferNum(dischargeTrade.getOrderNum());
//                if (finalAppletUserMap.containsKey(dischargeTrade.getAppletUserId())) {
//                    transferOrderVo.setAppletCode(finalAppletUserMap.get(dischargeTrade.getAppletUserId()).getAppletCode());
//                }
//                if (finalAccountMap.containsKey(dischargeTrade.getAccountId())) {
//                    AccountDto account = finalAccountMap.get(dischargeTrade.getAccountId());
//                    transferOrderVo.setMchId(account.getMchId());
//                    transferOrderVo.setApiV3Key(account.getApiV3Key());
//                    transferOrderVo.setSerialNo(account.getSerialNo());
//                    transferOrderVo.setKeyPemPath(account.getKeyPemPath());
//                }
//                WechatTransferOrderDto transferOrder = tradeService.queryWechatTransferOrder(transferOrderVo).getData();
//                if (transferOrder != null && StringUtil.isNotEmpty(transferOrder.getState())) {
//                    String state = transferOrder.getState();
//                    TransferStatusEnum statusEnum = TransferStatusEnum.getByCode(state);
//                    if (statusEnum != null) {
//                        dischargeTrade.setTradeStateDesc(statusEnum.getName());
//                        switch (statusEnum) {
//                            case WAIT_USER_CONFIRM:
//                            case ACCEPTED:
//                            case PROCESSING:
//                            case TRANSFERING:
//                            case CANCELING:
//                                dischargeTrade.setTradeStatus(1);
//                                break;
//                            case SUCCESS:
//                                dischargeTrade.setTradeStatus(2); //转账成功
//                                //转账成功,把冻结金额减掉,返回用户余额
//                                ResponseResult<BigDecimal> successResult = togetherService.updateUserDisWallet(UserDisWalletChangeVo.builder()
//                                        .appletUserId(dischargeTrade.getAppletUserId())
//                                        .accountId(dischargeTrade.getAccountId())
//                                        .tradeType(3)
//                                        .tradeMoney(dischargeTrade.getTradeMoney()).build());
//                                if (!successResult.isSuccess()) {
//                                    log.error("微信商户转账成功, 用户余额返回失败, V2G放电余额提现记录:{}", dischargeTrade);
//                                } else {
//                                    dischargeTrade.setTradeBalance(successResult.getData());
//                                }
//                                break;
//                            case FAIL:
//                            case CANCELLED:
//                                dischargeTrade.setTradeStatus(3); //转账失败
//                                //转账失败,把金额返回给用户余额里面
//                                UserDisWalletChangeVo userDisWalletVo = UserDisWalletChangeVo.builder()
//                                        .appletUserId(dischargeTrade.getAppletUserId())
//                                        .accountId(dischargeTrade.getAccountId())
//                                        .tradeMoney(dischargeTrade.getTradeMoney()).build();
//                                ResponseResult<BigDecimal> failResult = togetherService.updateUserDisWallet(UserDisWalletChangeVo.builder()
//                                        .appletUserId(dischargeTrade.getAppletUserId())
//                                        .accountId(dischargeTrade.getAccountId())
//                                        .tradeType(4)
//                                        .tradeMoney(dischargeTrade.getTradeMoney()).build());
//                                if (!failResult.isSuccess()) {
//                                    log.error("微信商户转账失败, 用户余额返回失败, V2G放电余额提现记录:{}", dischargeTrade);
//                                } else {
//                                    dischargeTrade.setTradeBalance(failResult.getData());
//                                }
//                                break;
//                        }
//                    }
//                }
//            }).collect(Collectors.toList());
//        }
//        //批量更新放电收益余额提现的数据
//        if (CollectionUtils.isNotEmpty(updateDisTradeList)) {
//            dischargeTradeDao.batchUpdate(updateDisTradeList);
//        }
//
//    }

}
