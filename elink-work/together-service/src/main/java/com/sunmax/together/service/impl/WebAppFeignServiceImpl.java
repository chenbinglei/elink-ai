package com.sunmax.together.service.impl;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import com.sunmax.common.dto.device.DeviceBasicInfoDto;
import com.sunmax.common.dto.operate.ChargerPriceRateDto;
import com.sunmax.common.dto.operate.OccupyPilePriceDto;
import com.sunmax.common.dto.system.AccountDto;
import com.sunmax.common.dto.system.AppletDto;
import com.sunmax.common.dto.together.AppletUserInfoDto;
import com.sunmax.common.dto.together.SiteAccountDto;
import com.sunmax.common.dto.webapp.ChargePriceInfoDto;
import com.sunmax.common.util.DateUtil;
import com.sunmax.common.util.DoubleUtil;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.util.StringUtil;
import com.sunmax.common.vo.together.OrderChangeVo;
import com.sunmax.common.vo.together.OrderUpdateVo;
import com.sunmax.common.vo.together.RefundRecordChangeVo;
import com.sunmax.common.vo.together.UserDisWalletChangeVo;
import com.sunmax.together.dao.*;
import com.sunmax.together.dao.asset.ChargerPriceDao;
import com.sunmax.together.dao.asset.ChargerPriceInfoDao;
import com.sunmax.together.dao.asset.OccupyPilePriceDao;
import com.sunmax.together.dao.order.OrderRecordDao;
import com.sunmax.together.dao.order.RefundRecordDao;
import com.sunmax.together.dao.order.SettlementRecordDao;
import com.sunmax.together.entity.AppletUserEntity;
import com.sunmax.together.entity.SiteAccountEntity;
import com.sunmax.together.entity.UserDisWalletEntity;
import com.sunmax.together.entity.assets.ChargerPriceEntity;
import com.sunmax.together.entity.assets.ChargerPriceInfoEntity;
import com.sunmax.together.entity.assets.OccupyPilePriceEntity;
import com.sunmax.together.entity.order.OrderRecordEntity;
import com.sunmax.together.entity.order.RefundRecordEntity;
import com.sunmax.together.entity.order.SettlementRecordEntity;
import com.sunmax.together.service.WebAppFeignService;
import com.sunmax.together.service.feign.DeviceService;
import com.sunmax.together.service.feign.SystemService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.sunmax.common.util.DoubleUtil.getToBigDecimal;

@Service
public class WebAppFeignServiceImpl implements WebAppFeignService {

    @Autowired
    private OrderRecordDao orderRecordDao;

    @Autowired
    private AppletUserDao appletUserDao;

    @Autowired
    private SettlementRecordDao settlementRecordDao;

    @Autowired
    private SiteAccountDao siteAccountDao;

    @Autowired
    private SystemService systemService;

    @Autowired
    private ChargerPriceInfoDao chargerPriceInfoDao;

    @Autowired
    private ChargerPriceDao chargerPriceDao;

    @Autowired
    private DeviceService deviceService;

    @Autowired
    private OccupyPilePriceDao occupyPilePriceDao;

    @Autowired
    private UserDisWalletDao userDisWalletDao;

    @Autowired
    private RefundRecordDao refundRecordDao;

    //上一次的订单号
    private String lastPayOrderNum;

    @Override
    public ResponseResult<String> generateOrderNum(String pilesCode, String serialNumber) {
        //离线标识，由平台鉴权充/放电都是1
        int offlineLogo = 1;
        //当前的订单号 电桩编号+年月日时分秒
        String currentOrderNum = StringUtils.rightPad(pilesCode, 16, "F") + DateTimeFormatter.ofPattern("yyMMddHHmmss").format(LocalDateTime.now());
        //数据库获取最新的一次
        if (StringUtil.isEmpty(lastPayOrderNum)) {
            OrderRecordEntity orderEntity = orderRecordDao.findFirstByOrderByCreateTimeDesc();
            if (orderEntity != null) {
                lastPayOrderNum = orderEntity.getOrderNum();
            }
        }
        //如果上一次的订单号包含当前的订单号 则顺序号+1 否则从001开始
        String orderNum;
        if (StringUtil.isEmpty(lastPayOrderNum)) {
            orderNum = currentOrderNum + offlineLogo + serialNumber;
            lastPayOrderNum = orderNum;
            return ResponseResult.ok(orderNum);
        }
        if (lastPayOrderNum.contains(currentOrderNum)) {
            //获取上次订单的顺序号+1,产生新的顺序号
            orderNum = currentOrderNum + offlineLogo + String.format("%03d", Integer.parseInt(lastPayOrderNum.substring(lastPayOrderNum.length() - 3)) + 1);
        } else {
            orderNum = currentOrderNum + offlineLogo + serialNumber;
        }
        lastPayOrderNum = orderNum;
        return ResponseResult.ok(orderNum);
    }

    @Override
    public ResponseResult<Map<String, AppletUserInfoDto>> findAppletUserByIds(Set<String> appletUserIds) {
        //返回的对象
        Map<String, AppletUserInfoDto> resultMap = Maps.newHashMap();
        List<AppletUserEntity> appletUserList = appletUserDao.findAllById(appletUserIds);
        if (CollectionUtils.isNotEmpty(appletUserList)) {
            //根据多个小程序主键id查询小程序标识
            Set<String> appletIds = appletUserList.stream().map(AppletUserEntity::getAppletId).collect(Collectors.toSet());
            Map<String, AppletDto> appletMap = systemService.findAppletListByIds(appletIds).getData();
            //返回的集合
            resultMap = appletUserDao.findAllById(appletUserIds).stream().map(appletUser -> {
                AppletUserInfoDto result = new AppletUserInfoDto();
                BeanUtils.copyProperties(appletUser, result);
                if (appletMap.containsKey(appletUser.getAppletId())) {
                    result.setAppletCode(appletMap.get(appletUser.getAppletId()).getAppletCode());
                }
                return result;
            }).collect(Collectors.toMap(AppletUserInfoDto::getId, a -> a, (k1, k2) -> k1));
        }
        return ResponseResult.ok(resultMap);
    }

    @Override
    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class)
    public ResponseResult<Void> createPileOrder(OrderChangeVo orderChangeVo) {
        //根据订单编号查询该订单是否存在
        if (orderRecordDao.findOne(Example.of(OrderRecordEntity.builder().orderNum(orderChangeVo.getOrderNum()).build())).isPresent()) {
            return ResponseResult.paramError(ResponseResult.PARAM_EXIST);
        }
        OrderRecordEntity orderEntity = new OrderRecordEntity();
        BeanUtils.copyProperties(orderChangeVo, orderEntity);
        orderEntity.setId(orderChangeVo.getOrderNum());
        orderEntity.setOrderStatus(0);
        orderRecordDao.save(orderEntity);
        //创建结算记录
        SettlementRecordEntity settlementRecordEntity = new SettlementRecordEntity();
        settlementRecordEntity.setOrderNum(orderChangeVo.getOrderNum());
        settlementRecordEntity.setSettlementState(orderChangeVo.getSettlementState());
        settlementRecordEntity.setPayWay(orderChangeVo.getPayWay());
        settlementRecordDao.save(settlementRecordEntity);
        return ResponseResult.ok();
    }

    @Override
    public ResponseResult<Map<String, List<SiteAccountDto>>> findSiteAccountListBySiteIds(Set<String> siteIds, Integer type) {
        //返回的集合
        Map<String, List<SiteAccountDto>> resultMap = Maps.newHashMap();
        //根据多个站点id和类型查询站点账户数据
        List<SiteAccountEntity> siteAccountList = siteAccountDao.findBySiteIdInAndType(siteIds, type);
        if (CollectionUtils.isNotEmpty(siteAccountList)) {
            //根据多个账户id查询账户数据
            Set<String> accountIds = siteAccountList.stream().map(SiteAccountEntity::getAccountId).collect(Collectors.toSet());
            Map<String, AccountDto> accountMap = systemService.findAccountListByAccountIds(accountIds).getData();
            //根据站点分组 返回站点账户数据
            resultMap = siteAccountList.stream().map(siteAccount -> {
                SiteAccountDto result = new SiteAccountDto();
                BeanUtils.copyProperties(siteAccount, result);
                if (accountMap.containsKey(siteAccount.getAccountId())) {
                    AccountDto account = accountMap.get(siteAccount.getAccountId());
                    result.setMchId(account.getMchId());
                    result.setMchName(account.getMchName());
                    result.setMchKey(account.getMchKey());
                    result.setApiType(account.getApiType());
                    result.setApiV3Key(account.getApiV3Key());
                    result.setSerialNo(account.getSerialNo());
                    result.setKeyPemPath(account.getKeyPemPath());
                    result.setRsaSerialNo(account.getRsaSerialNo());
                    result.setPubKeyPath(account.getPubKeyPath());
                }
                return result;
            }).collect(Collectors.groupingBy(SiteAccountDto::getSiteId));
        }
        return ResponseResult.ok(resultMap);
    }

    @Override
    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class)
    public ResponseResult<Void> updatePileFailOrder(OrderUpdateVo orderUpdateVo) {
        //根据订单编号查询订单记录数据
        OrderRecordEntity orderRecord = orderRecordDao.findByOrderNum(orderUpdateVo.getOrderNum());
        if (orderRecord != null) {
            orderRecord.setOrderStatus(orderUpdateVo.getOrderStatus());
            orderRecord.setTotalQt(orderUpdateVo.getTotalQt());
            orderRecord.setTotalCost(orderUpdateVo.getTotalCost());
            orderRecord.setAbnormalCode(orderUpdateVo.getAbnormalCode());
            if (StringUtil.isEmpty(orderRecord.getSiteId()) && StringUtil.isNotEmpty(orderRecord.getPileCode())) {
                DeviceBasicInfoDto device = deviceService.findDeviceBasicInfoByCodes(Collections.singletonList(orderRecord.getPileCode()))
                        .getData().get(orderRecord.getPileCode());
                if (device != null) {
                    orderRecord.setSiteId(device.getSiteId());
                }
            }
            orderRecordDao.save(orderRecord);
            //根据订单编号查询结算记录数据
            SettlementRecordEntity settlementRecord = settlementRecordDao.findByOrderNum(orderUpdateVo.getOrderNum());
            settlementRecord.setSettlementState(orderUpdateVo.getSettlementState());
            settlementRecord.setActualTotalCost(orderUpdateVo.getActualTotalCost());
            settlementRecord.setActualTotalElect(orderUpdateVo.getActualTotalElect());
            settlementRecord.setActualTotalFee(orderUpdateVo.getActualTotalFee());
            settlementRecord.setTotalElectReduction(orderUpdateVo.getTotalElectReduction());
            settlementRecord.setTotalFeeReduction(orderUpdateVo.getTotalFeeReduction());
            settlementRecord.setRefundMoney(orderUpdateVo.getRefundMoney());
            settlementRecordDao.save(settlementRecord);
            return ResponseResult.ok();
        }
        return ResponseResult.paramError(ResponseResult.PARAM_ERROR);
    }

    /**
     * 根据多个站点id查询充放电费率列表
     *
     * @param siteIds    多个站点id
     * @param priceType  价格类型 1-充电 2-放电
     * @param deviceType 设备类型 1-直流 2-交流
     * @return
     */
    @Override
    public ResponseResult<Map<String, List<ChargerPriceRateDto>>> findChargerRateListBySiteIds(List<String> siteIds, Integer priceType, Integer deviceType) {
        Map<String, List<ChargerPriceRateDto>> resultMap = Maps.newHashMap();
        //根据设备所属站点id查询站点下充放电价格信息数据
        List<ChargerPriceInfoEntity> priceInfoEntities = chargerPriceInfoDao.findAllBySiteIdInAndPriceStateAndPriceType(siteIds, 1, priceType);
        if (CollectionUtils.isNotEmpty(priceInfoEntities)) {
            //过滤出指定设备类型价格信息
            List<ChargerPriceInfoEntity> filterPriceInfoEntities = priceInfoEntities.stream().filter(priceInfoEntity -> priceInfoEntity.getDeviceType().equals(deviceType)).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(filterPriceInfoEntities)) {
                Map<String, ChargerPriceInfoEntity> chargerPriceInfoMap = filterPriceInfoEntities.stream().collect(Collectors.toMap(ChargerPriceInfoEntity::getSiteId, chargerPriceInfoEntity -> chargerPriceInfoEntity, (k1, k2) -> k1));
                //根据多个价格id查询费率配置信息
                Map<String, List<ChargerPriceEntity>> groupByPirceIdMap = chargerPriceDao.findAllByPriceIdIn(priceInfoEntities.stream().map(ChargerPriceInfoEntity::getId).collect(Collectors.toList()))
                        .stream().collect(Collectors.groupingBy(ChargerPriceEntity::getPriceId));
                chargerPriceInfoMap.forEach((siteId, priceInfoEntity) -> {
                    if (groupByPirceIdMap.containsKey(priceInfoEntity.getId())) {
                        List<ChargerPriceEntity> chargerPriceEntities = groupByPirceIdMap.get(priceInfoEntity.getId());
                        resultMap.put(siteId, chargerPriceEntities.stream().map(chargerPriceEntity -> {
                            ChargerPriceRateDto chargerPriceRateDto = new ChargerPriceRateDto();
                            BeanUtils.copyProperties(chargerPriceEntity, chargerPriceRateDto);
                            return chargerPriceRateDto;
                        }).collect(Collectors.toList()));
                    }
                });
            }
        }
        return ResponseResult.ok(resultMap);
    }

    /**
     * 根据设备id查询充放电和占桩价格信息
     *
     * @param deviceId  设备id
     * @param priceType 价格类型 1-充电 2-放电
     * @return
     */
    @Override
    public ResponseResult<ChargePriceInfoDto> findDevicePriceById(String deviceId, Integer priceType) {
        ChargePriceInfoDto result = new ChargePriceInfoDto();
        //根据设备id查询设备信息
        ResponseResult<Map<String, DeviceBasicInfoDto>> deviceBasicInfoByIds = deviceService.findDeviceBasicInfoByIds(Collections.singletonList(deviceId));
        if (deviceBasicInfoByIds.isSuccess() && !deviceBasicInfoByIds.getData().isEmpty() && deviceBasicInfoByIds.getData().containsKey(deviceId)) {
            DeviceBasicInfoDto deviceBasicInfoDto = deviceBasicInfoByIds.getData().get(deviceId);
            //查询生效中的价格信息
            List<ChargerPriceInfoEntity> priceInfoEntities = chargerPriceInfoDao.findAllBySiteIdInAndPriceStateAndPriceType(Collections.singletonList(deviceBasicInfoDto.getSiteId()), 1, priceType);
            if (CollectionUtils.isNotEmpty(priceInfoEntities)) {
                //根据设备资产类型过滤出设备类型的价格信息
                if ("29".equals(deviceBasicInfoDto.getTypeId()) || "30".equals(deviceBasicInfoDto.getTypeId())) {
                    priceInfoEntities = priceInfoEntities.stream().filter(priceInfoEntity -> priceInfoEntity.getDeviceType() == 1).collect(Collectors.toList());
                } else {
                    priceInfoEntities = priceInfoEntities.stream().filter(priceInfoEntity -> priceInfoEntity.getDeviceType() == 2).collect(Collectors.toList());
                }
                if (CollectionUtils.isNotEmpty(priceInfoEntities)) {
                    //根据价格id查询价格详情
                    Map<String, List<ChargerPriceEntity>> groupByPirceIdMap = chargerPriceDao.findAllByPriceIdIn(priceInfoEntities.stream().map(ChargerPriceInfoEntity::getId).collect(Collectors.toList()))
                            .stream().collect(Collectors.groupingBy(ChargerPriceEntity::getPriceId));
                    ChargerPriceInfoEntity priceInfoEntity = priceInfoEntities.get(0);
                    List<ChargerPriceEntity> chargerPriceEntities = groupByPirceIdMap.get(priceInfoEntity.getId());
                    if (CollectionUtils.isNotEmpty(chargerPriceEntities)) {
                        //当前充放电价格
                        ChargePriceInfoDto.ChargerPrice chargerPrice = new ChargePriceInfoDto.ChargerPrice();
                        String nowTime = DateUtil.localTimeToStr(LocalTime.now());
                        result.setChargerPriceList(chargerPriceEntities.stream().map(chargerPriceEntity -> {
                            ChargePriceInfoDto.ChargerPrice price = new ChargePriceInfoDto.ChargerPrice();
                            BeanUtils.copyProperties(chargerPriceEntity, price);
                            String startTime = chargerPriceEntity.getStartTime() + ":59";
                            String endTime = "00:00".equals(chargerPriceEntity.getEndTime()) ? "23:59:59" : chargerPriceEntity.getEndTime() + ":59";
                            //和当前时间比较，获取当前时段的电价
                            if (nowTime.compareTo(startTime) >= 0 && nowTime.compareTo(endTime) < 0) {
                                BeanUtils.copyProperties(chargerPriceEntity, chargerPrice);
                            }
                            return price;
                        }).collect(Collectors.toList()));
                        result.setChargerPrice(chargerPrice);
                    }
                }
            }
            //根据站点id查询相关占桩价格信息
            List<OccupyPilePriceEntity> occupyPilePriceEntityList = occupyPilePriceDao.findBySiteIdAndIsDelete(deviceBasicInfoDto.getSiteId(), 1);
            if (CollectionUtils.isNotEmpty(occupyPilePriceEntityList)) {
                //根据设备资产类型过滤出设备类型的价格信息
                if ("29".equals(deviceBasicInfoDto.getTypeId()) || "30".equals(deviceBasicInfoDto.getTypeId())) {
                    occupyPilePriceEntityList = occupyPilePriceEntityList.stream().filter(priceInfoEntity -> priceInfoEntity.getDeviceType() == 1).collect(Collectors.toList());
                } else {
                    occupyPilePriceEntityList = occupyPilePriceEntityList.stream().filter(priceInfoEntity -> priceInfoEntity.getDeviceType() == 2).collect(Collectors.toList());
                }
                if (CollectionUtils.isNotEmpty(occupyPilePriceEntityList)) {
                    ChargePriceInfoDto.PriceInfoData priceInfoData = new ChargePriceInfoDto.PriceInfoData();

                    OccupyPilePriceEntity occupyPilePriceEntity = occupyPilePriceEntityList.get(0);

                    BeanUtils.copyProperties(occupyPilePriceEntity, priceInfoData);
                    String configPriceInfo = occupyPilePriceEntity.getConfigPriceInfo();
                    priceInfoData.setConfigPriceInfoStr(configPriceInfo);
                    List<OccupyPilePriceDto.ConfigPriceInfo> configPriceInfoList = JSON.parseArray(configPriceInfo, OccupyPilePriceDto.ConfigPriceInfo.class);
                    priceInfoData.setConfigPriceInfoList(configPriceInfoList);
                    result.setOccupyPriceInfoData(priceInfoData);
                }
            }
        }
        return ResponseResult.ok(result);
    }

    @Override
    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class)
    public ResponseResult<BigDecimal> updateUserDisWallet(UserDisWalletChangeVo userDisWalletVo) {
        if (StringUtil.isEmpty(userDisWalletVo.getAppletUserId()) || StringUtil.isEmpty(userDisWalletVo.getAccountId())) {
            return ResponseResult.paramError(ResponseResult.PARAM_ERROR);
        }
        if (StringUtil.isEmpty(userDisWalletVo.getTradeMoney()) || userDisWalletVo.getTradeMoney().compareTo(BigDecimal.ZERO) == 0) {
            return ResponseResult.paramError(ResponseResult.PARAM_ERROR);
        }
        if (StringUtil.isEmpty(userDisWalletVo.getTradeType())) {
            return ResponseResult.paramError(ResponseResult.PARAM_ERROR);
        }
        UserDisWalletEntity userDisWallet = userDisWalletDao.findAllByAppletUserIdAndAccountId(userDisWalletVo.getAppletUserId(),
                userDisWalletVo.getAccountId());
        if (userDisWallet == null) {
            userDisWallet = new UserDisWalletEntity();
            userDisWallet.setAppletUserId(userDisWalletVo.getAppletUserId());
            userDisWallet.setAccountId(userDisWalletVo.getAccountId());
        }

        //交易类型 1-放电收益 2-V2G提现中(冻结金额) 3-V2G提现成功(解冻金额) 4-V2G提现失败(解冻金额,冻结余额返回到余额里面)
        switch (userDisWalletVo.getTradeType()) {
            case 1:
                userDisWallet.setBalance(getToBigDecimal(userDisWallet.getBalance().add(userDisWalletVo.getTradeMoney())));
                break;
            case 2:
                userDisWallet.setBalance(getToBigDecimal(userDisWallet.getBalance().subtract(userDisWalletVo.getTradeMoney())));
                userDisWallet.setFreezeBalance(getToBigDecimal(userDisWallet.getFreezeBalance().add(userDisWalletVo.getTradeMoney())));
                break;
            case 3:
                userDisWallet.setFreezeBalance(getToBigDecimal(userDisWallet.getFreezeBalance().subtract(userDisWalletVo.getTradeMoney())));
                break;
            case 4:
                userDisWallet.setBalance(getToBigDecimal(userDisWallet.getBalance().add(userDisWalletVo.getTradeMoney())));
                userDisWallet.setFreezeBalance(getToBigDecimal(userDisWallet.getFreezeBalance().subtract(userDisWalletVo.getTradeMoney())));
                break;
        }
        userDisWalletDao.save(userDisWallet);
        return ResponseResult.ok(userDisWallet.getBalance().add(userDisWallet.getFreezeBalance()));
    }

    @Override
    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class)
    public ResponseResult<String> saveRefundRecord(RefundRecordChangeVo refundRecordChangeVo) {
        if (StringUtil.isNotEmpty(refundRecordChangeVo)) {
            //根据订单编号查询预付金额
            OrderRecordEntity order = orderRecordDao.findByOrderNum(refundRecordChangeVo.getOrderNum());
            if (order != null && StringUtil.isNotEmpty(order.getPrepayMoney()) && order.getPrepayMoney().compareTo(BigDecimal.ZERO) > 0) {
                RefundRecordEntity refundRecordEntity = new RefundRecordEntity();
                BeanUtils.copyProperties(refundRecordChangeVo, refundRecordEntity);
                refundRecordEntity.setRefundStatus(2);
                //根据订单编码查询订单结算信息
                SettlementRecordEntity settlementRecord = settlementRecordDao.findByOrderNum(refundRecordChangeVo.getOrderNum());
                if (StringUtil.isNotEmpty(settlementRecord)) {
                    //退款金额 = 退款历史记录总金额 + 本次退款金额
                    //根据订单编号查询退款记录
                    List<RefundRecordEntity> refundRecordList = refundRecordDao.findAll(Example.of(RefundRecordEntity.builder()
                            .orderNum(refundRecordChangeVo.getOrderNum()).build()));
                    BigDecimal refundAmount = new BigDecimal("0.0");
                    if (CollectionUtils.isNotEmpty(refundRecordList)) {
                        refundAmount = refundRecordList.stream().map(RefundRecordEntity::getRefundAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
                    }
                    refundAmount = DoubleUtil.getToBigDecimal(refundAmount.add(refundRecordChangeVo.getRefundAmount()));
                    //计算结算记录实付金额(预付金额 - 退款金额),并修改实付金额
                    if (refundAmount.compareTo(BigDecimal.ZERO) > 0 && refundAmount.compareTo(order.getPrepayMoney()) <= 0) {
                        refundRecordEntity.setRefundStatus(1);
                        //实付金额
                        BigDecimal actualTotalCost = getToBigDecimal(order.getPrepayMoney().subtract(refundAmount));
                        settlementRecord.setActualTotalCost(actualTotalCost);
                        //计算结算记录退款金额(结算记录退款金额 + 退款记录退款金额)
                        settlementRecord.setRefundMoney(refundAmount);
                        //退款过程中 计算实付电费和实付服务费折扣
                        if (StringUtil.isEmpty(settlementRecord.getActualTotalElect()) || settlementRecord.getActualTotalElect().compareTo(BigDecimal.ZERO) <= 0) {
                            settlementRecord.setActualTotalFee(actualTotalCost);
                            settlementRecord.setActualTotalElect(new BigDecimal("0.0"));
                        } else if (StringUtil.isEmpty(settlementRecord.getActualTotalFee()) || settlementRecord.getActualTotalFee().compareTo(BigDecimal.ZERO) <= 0) {
                            settlementRecord.setActualTotalElect(actualTotalCost);
                            settlementRecord.setActualTotalFee(new BigDecimal("0.0"));
                        } else {
                            BigDecimal totalCost = settlementRecord.getActualTotalElect().add(settlementRecord.getActualTotalFee());
                            settlementRecord.setActualTotalElect(actualTotalCost.multiply(settlementRecord.getActualTotalElect().divide(totalCost, 4, RoundingMode.HALF_UP)));
                            settlementRecord.setActualTotalFee(actualTotalCost.multiply(settlementRecord.getActualTotalFee().divide(totalCost, 4, RoundingMode.HALF_UP)));
                        }
                        if (settlementRecord.getSettlementState() != 3) {
                            settlementRecord.setSettlementState(3);
                        }
                        //保存结算记录信息
                        settlementRecordDao.save(settlementRecord);
                    } else {
                        refundRecordEntity.setRefundStatus(2);
                    }
                }
                if (StringUtil.isNotEmpty(refundRecordChangeVo.getRefundOperator())) {
                    refundRecordEntity.setRefundOperator(refundRecordChangeVo.getRefundOperator());
                } else {
                    refundRecordEntity.setRefundOperator("系统自动操作管理员");
                }
                //保存退款记录
                refundRecordDao.save(refundRecordEntity);
                return ResponseResult.ok(ResponseResult.SUCCESS);
            }
        }
        return ResponseResult.error(ResponseResult.FAIL);
    }

}
