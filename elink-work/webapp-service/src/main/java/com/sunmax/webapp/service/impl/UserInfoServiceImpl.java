package com.sunmax.webapp.service.impl;

import com.google.common.collect.Lists;
import com.sunmax.common.dto.system.AccountDto;
import com.sunmax.common.dto.together.AppletUserInfoDto;
import com.sunmax.common.dto.together.UserDisWalletDto;
import com.sunmax.common.util.DateUtil;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.util.StringUtil;
import com.sunmax.common.vo.OrderSerialParamVo;
import com.sunmax.common.vo.together.AppletUserChangeVo;
import com.sunmax.common.vo.together.UserDisWalletChangeVo;
import com.sunmax.webapp.dao.trade.DischargeTradeDao;
import com.sunmax.webapp.dto.AppletDisWalletDto;
import com.sunmax.webapp.dto.AppletTradeListDto;
import com.sunmax.webapp.dto.WechatMchTransferDto;
import com.sunmax.webapp.entity.trade.DischargeTradeEntity;
import com.sunmax.webapp.enums.TransferStatusEnum;
import com.sunmax.webapp.service.TradeService;
import com.sunmax.webapp.service.UserInfoService;
import com.sunmax.webapp.service.feign.SauthService;
import com.sunmax.webapp.service.feign.SystemService;
import com.sunmax.webapp.service.feign.TogetherService;
import com.sunmax.webapp.vo.AppletTradeQueryVo;
import com.sunmax.webapp.vo.wechat.WechatMchTransferVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.criteria.Predicate;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserInfoServiceImpl implements UserInfoService {

    @Autowired
    private TogetherService togetherService;

    @Autowired
    private SauthService sauthService;

    @Autowired
    private TradeService tradeService;

    @Autowired
    private SystemService systemService;

    @Autowired
    private DischargeTradeDao dischargeTradeDao;

    /**
     * 根据小程序用户id查询基本信息
     * @param appletUserId 小程序用户id
     * @return
     */
    @Override
    public ResponseResult<AppletUserInfoDto> queryAppletUserInfoById(String appletUserId) {
        ResponseResult<Map<String, AppletUserInfoDto>> appletUserByIds = togetherService.findAppletUserByIds(Collections.singleton(appletUserId));
        return ResponseResult.ok(appletUserByIds.getData().get(appletUserId));
    }

    /**
     * 编辑小程序用户
     * @param appletUserChangeVo
     * @return
     */
    @Override
    public ResponseResult<String> updateAppletUser(AppletUserChangeVo appletUserChangeVo) {
        return togetherService.updateAppletUser(appletUserChangeVo);
    }

    /**
     * 微信小程序登录
     * @param parameters 登录参数
     * @return
     */
    @Override
    public ResponseResult<Map<String, Object>> postAccessToken(Map<String, String> parameters) {
        return sauthService.postAccessToken(parameters);
    }

    /**
     * 根据小程序用户id查询用户V2G钱包列表
     * @param appletUserId
     * @return
     */
    @Override
    public ResponseResult<List<AppletDisWalletDto>> findAppletDisWalletListById(String appletUserId) {
        List<AppletDisWalletDto> resultList = Lists.newArrayList();
        ResponseResult<List<UserDisWalletDto>> userDisWalletListResult = togetherService.findUserDisWalletListById(appletUserId);
        if (userDisWalletListResult.isSuccess() && CollectionUtils.isNotEmpty(userDisWalletListResult.getData())) {
            resultList = userDisWalletListResult.getData().stream().map(userDisWalletDto -> {
                AppletDisWalletDto appletDisWalletDto = new AppletDisWalletDto();
                BeanUtils.copyProperties(userDisWalletDto, appletDisWalletDto);
                return appletDisWalletDto;
            }).collect(Collectors.toList());
        }
        return ResponseResult.ok(resultList);
    }

    @Override
    public ResponseResult<String> submitAppletCancel(String appletUserId, String appletName) {
        return togetherService.submitAppletCancel(appletUserId,appletName);
    }

    @Override
    public ResponseResult<String> updateAppletUserPhoneById(String appletUserId, String phoneNum) {
        return togetherService.updateAppletUserPhoneById(appletUserId,phoneNum);
    }

    @Override
    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class)
    public ResponseResult<WechatMchTransferDto> withdrawAppletUserMoney(String appletUserId, String disWalletId, BigDecimal money) {
        try {
            if (StringUtil.isEmpty(appletUserId) || StringUtil.isEmpty(disWalletId)) {
                return ResponseResult.paramError("前端传的参数为空,不允许提现");
            }
            //根据小程序用户id查询用户V2G钱包列表
            List<UserDisWalletDto> userDisWalletList = togetherService.findUserDisWalletListById(appletUserId).getData();
            if (CollectionUtils.isNotEmpty(userDisWalletList)) {
                Optional<UserDisWalletDto> optional = userDisWalletList.stream().filter(d -> d.getId().equals(disWalletId)).findFirst();
                if (optional.isPresent() && StringUtil.isNotEmpty(optional.get().getAppletUserId())) {
                    UserDisWalletDto userDisWallet = optional.get();
                    if (StringUtil.isNotEmpty(userDisWallet.getBalance()) && userDisWallet.getBalance().compareTo(money) >= 0) {
                        //处理商家转账相关逻辑
                        //获取小程序用户微信openid
                        AppletUserInfoDto appletUser = togetherService.findAppletUserByIds(Collections.singleton(userDisWallet.getAppletUserId()))
                                .getData().get(userDisWallet.getAppletUserId());
                        //获取商户相关参数
                        AccountDto account = systemService.findAccountListByAccountIds(Collections.singleton(userDisWallet.getAccountId()))
                                .getData().get(userDisWallet.getAccountId());
                        if (appletUser != null && StringUtil.isNotEmpty(appletUser.getOpenid()) && account != null && StringUtil.isNotEmpty(account.getPlatformType())
                                && account.getPlatformType() == 1) {
                            //创建本次转账订单号
                            String transferNum = this.generateTransferNum(account.getMchId());
                            //商家转账
                            WechatMchTransferVo mchTransferVo = new WechatMchTransferVo();
                            BeanUtils.copyProperties(account, mchTransferVo);
                            mchTransferVo.setOpenid(appletUser.getOpenid());
                            mchTransferVo.setTransferNum(transferNum);
                            mchTransferVo.setTransferMoney(money);
                            mchTransferVo.setAppletCode(appletUser.getAppletCode());
                            ResponseResult<WechatMchTransferDto> mchTransferResult = tradeService.wechatMchTransfer(mchTransferVo);
                            if (mchTransferResult.isSuccess() && StringUtil.isNotEmpty(mchTransferResult.getData())) {
                                WechatMchTransferDto mchTransfer = mchTransferResult.getData();

                                //小程序余额冻结，返回余额
                                ResponseResult<BigDecimal> responseResult = togetherService.updateUserDisWallet(UserDisWalletChangeVo.builder()
                                        .appletUserId(userDisWallet.getAppletUserId())
                                        .tradeMoney(money)
                                        .tradeType(2)
                                        .accountId(userDisWallet.getAccountId())
                                        .build());
                                if (responseResult.isSuccess()) {
                                    //创建V2G余额提现明细
                                    DischargeTradeEntity dischargeTrade = new DischargeTradeEntity();
                                    dischargeTrade.setOrderNum(mchTransfer.getOutBillNo());
                                    dischargeTrade.setFlowNum(mchTransfer.getTransferBillNo());
                                    dischargeTrade.setTradeMoney(money);
                                    dischargeTrade.setTradeType(2);
                                    dischargeTrade.setTradeStatus(1); //交易状态-处理中
                                    dischargeTrade.setTradeWay(1);
                                    dischargeTrade.setTradeState(mchTransfer.getState());
                                    TransferStatusEnum statusEnum = TransferStatusEnum.getByCode(mchTransfer.getState());
                                    if (statusEnum != null) {
                                        dischargeTrade.setTradeStateDesc(statusEnum.getName());
                                    }
                                    dischargeTrade.setAccountId(userDisWallet.getAccountId());
                                    dischargeTrade.setAppletUserId(userDisWallet.getAppletUserId());
                                    dischargeTrade.setTradeBalance(responseResult.getData());
                                    dischargeTradeDao.save(dischargeTrade);
                                    return mchTransferResult;
                                } else {
                                    return ResponseResult.paramError("更新小程序用户余额失败");
                                }

                            } else {
                                return ResponseResult.paramError(mchTransferResult.getMessage());
                            }
                        }
                    } else {
                        return ResponseResult.error("放电收益提现余额不足");
                    }
                }
            }
            return ResponseResult.paramError(ResponseResult.PARAM_ERROR);
        } catch (RuntimeException e) {
            log.error("用户提现放电收益失败", e);
            return ResponseResult.error(ResponseResult.FAIL);
        }
    }

    @Override
    public ResponseResult<List<AppletTradeListDto>> queryAppletTradeList(AppletTradeQueryVo tradeQueryVo) {
        //返回的集合
        List<AppletTradeListDto> resultList = Lists.newArrayList();

        String appletUserId = tradeQueryVo.getAppletUserId();
        String disWalletId = tradeQueryVo.getDisWalletId();
        if (StringUtil.isEmpty(appletUserId) || StringUtil.isEmpty(disWalletId)) {
            return ResponseResult.paramError(ResponseResult.PARAM_ERROR);
        }
        //根据小程序用户id查询账号id
        String accountId;
        List<UserDisWalletDto> userDisWalletList = togetherService.findUserDisWalletListById(appletUserId).getData();
        if (CollectionUtils.isNotEmpty(userDisWalletList)) {
            Optional<UserDisWalletDto> optional = userDisWalletList.stream().filter(d -> d.getId().equals(disWalletId)).findFirst();
            if (optional.isPresent() && StringUtil.isNotEmpty(optional.get().getAccountId())) {
                accountId = optional.get().getAccountId();
            } else {
                accountId = null;
            }
        } else {
            accountId = null;
        }
        if (StringUtil.isEmpty(accountId)) {
            return ResponseResult.ok(resultList);
        }
        //根据账号id和小程序用户id查询放电交易数据
        List<DischargeTradeEntity> dischargeTradeList = dischargeTradeDao.findAll((Specification<DischargeTradeEntity>) (root, cq, cb) -> {
            List<Predicate> predicates = Lists.newArrayList();
            predicates.add(cb.equal(root.get("appletUserId"), appletUserId));
            predicates.add(cb.equal(root.get("accountId"), accountId));
            predicates.add(cb.equal(root.get("tradeStatus"), 2));
            if (StringUtil.isNotEmpty(tradeQueryVo.getStartDate()) && StringUtil.isNotEmpty(tradeQueryVo.getEndDate())) {
                LocalDateTime startTime = DateUtil.strToLocalDateTime(DateUtil.getDayStart(tradeQueryVo.getStartDate()));
                LocalDateTime endTime = DateUtil.strToLocalDateTime(DateUtil.getDayStart(tradeQueryVo.getStartDate()));
                predicates.add(cb.between(root.get("createTime"), startTime, endTime));
            }
            //交易类型
            if (StringUtil.isNotEmpty(tradeQueryVo.getTradeType())) {
                predicates.add(cb.equal(root.get("tradeType"), tradeQueryVo.getTradeType()));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        });
        if (CollectionUtils.isNotEmpty(dischargeTradeList)) {
            //根据日期进行分组
            Map<String, List<DischargeTradeEntity>> dischargeTradeMap = dischargeTradeList.stream().collect(Collectors.groupingBy(d ->
                    DateUtil.localDateTimeToStr(d.getCreateTime()).substring(0, 7)));
            for (Map.Entry<String, List<DischargeTradeEntity>> entry : dischargeTradeMap.entrySet()) {
                String key = entry.getKey();
                List<DischargeTradeEntity> value = entry.getValue();
                AppletTradeListDto result = new AppletTradeListDto();
                result.setDate(key);
                //交易明细数据
                List<AppletTradeListDto.TradeDetail> tradeDetailList = value.stream().map(d -> {
                    AppletTradeListDto.TradeDetail tradeDetail = new AppletTradeListDto.TradeDetail();
                    BeanUtils.copyProperties(d, tradeDetail);
                    tradeDetail.setCreateTime(DateUtil.localDateTimeToStr(d.getCreateTime()));
                    return tradeDetail;
                }).sorted(Comparator.comparing(AppletTradeListDto.TradeDetail::getCreateTime).reversed()).collect(Collectors.toList());
                result.setTradeDetailList(tradeDetailList);
                //收入金额
                result.setIncomeMoney(tradeDetailList.stream().filter(d -> StringUtil.isNotEmpty(d.getTradeType()) && d.getTradeType() == 1
                        && StringUtil.isNotEmpty(d.getTradeMoney())).map(d -> d.getTradeMoney()).reduce(BigDecimal.ZERO, BigDecimal::add));
                //支出金额
                result.setOutcomeMoney(tradeDetailList.stream().filter(d -> StringUtil.isNotEmpty(d.getTradeType()) && d.getTradeType() == 2
                        && StringUtil.isNotEmpty(d.getTradeMoney())).map(d -> d.getTradeMoney()).reduce(BigDecimal.ZERO, BigDecimal::add));
                resultList.add(result);
            }
            //根据时间进行排序
            resultList = resultList.stream().sorted(Comparator.comparing(AppletTradeListDto::getDate).reversed()).collect(Collectors.toList());
        }
        return ResponseResult.ok(resultList);
    }

    //上一次的转账订单号
    private String lastOrderNum;

    private String generateTransferNum(String mchCode) {
        //离线标识，由平台鉴权充/放电都是1
        int offlineLogo = 1;
        //当前的订单号 电桩编号+年月日时分秒
        String currentOrderNum = StringUtils.rightPad(mchCode, 16, "0") + DateTimeFormatter.ofPattern("yyMMddHHmmss").format(LocalDateTime.now());

        //如果上一次的订单号包含当前的订单号 则顺序号+1 否则从001开始
        String orderNum;
        if (StringUtil.isEmpty(lastOrderNum)) {
            orderNum = currentOrderNum + offlineLogo + OrderSerialParamVo.TRANSFER_NUMBER;
            lastOrderNum = orderNum;
            return orderNum;
        }
        if (lastOrderNum.contains(currentOrderNum)) {
            //获取上次订单的顺序号+1,产生新的顺序号
            orderNum = currentOrderNum + offlineLogo + java.lang.String.format("%03d", Integer.parseInt(lastOrderNum.substring(lastOrderNum.length() - 3)) + 1);
        } else {
            orderNum = currentOrderNum + offlineLogo + OrderSerialParamVo.TRANSFER_NUMBER;
        }
        lastOrderNum = orderNum;
        return orderNum;
    }

}
