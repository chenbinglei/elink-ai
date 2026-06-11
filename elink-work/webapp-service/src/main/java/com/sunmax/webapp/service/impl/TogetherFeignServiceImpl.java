package com.sunmax.webapp.service.impl;

import com.google.common.collect.Lists;
import com.sunmax.common.dto.PageDto;
import com.sunmax.common.dto.device.SiteInfoDto;
import com.sunmax.common.dto.system.AccountDto;
import com.sunmax.common.dto.system.OrganEmpowerListDto;
import com.sunmax.common.dto.together.AppletUserInfoDto;
import com.sunmax.common.dto.together.SiteAccountDto;
import com.sunmax.common.dto.webapp.DischargeTradeDto;
import com.sunmax.common.dto.webapp.DischargeTradeListDto;
import com.sunmax.common.dto.webapp.RechargeTradeDto;
import com.sunmax.common.dto.webapp.RechargeTradeListDto;
import com.sunmax.common.util.DateUtil;
import com.sunmax.common.util.DoubleUtil;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.util.StringUtil;
import com.sunmax.common.vo.webapp.DischargeTradeQueryVo;
import com.sunmax.common.vo.webapp.RechargeTradeQueryVo;
import com.sunmax.webapp.dao.trade.DischargeTradeDao;
import com.sunmax.webapp.dao.trade.RechargeTradeDao;
import com.sunmax.webapp.entity.trade.DischargeTradeEntity;
import com.sunmax.webapp.entity.trade.RechargeTradeEntity;
import com.sunmax.webapp.service.TogetherFeignService;
import com.sunmax.webapp.service.feign.DeviceService;
import com.sunmax.webapp.service.feign.SystemService;
import com.sunmax.webapp.service.feign.TogetherService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import jakarta.persistence.criteria.Predicate;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TogetherFeignServiceImpl implements TogetherFeignService {

    @Autowired
    private SystemService systemService;

    @Autowired
    private DeviceService deviceService;

    @Autowired
    private TogetherService togetherService;

    @Autowired
    private RechargeTradeDao rechargeTradeDao;

    @Autowired
    private DischargeTradeDao dischargeTradeDao;

    @Override
    public ResponseResult<RechargeTradeListDto> queryRechargeTradeList(RechargeTradeQueryVo rechargeTradeVo) {
        //返回的对象
        RechargeTradeListDto result = new RechargeTradeListDto();

        //根据用户id查询租户下面的站点id
        Set<String> siteIds = systemService.findAllOrganEmpowerByUserId(rechargeTradeVo.getUserId()).getData().stream()
                .map(OrganEmpowerListDto::getSiteId).collect(Collectors.toSet());
        if (CollectionUtils.isEmpty(siteIds)) {
            return ResponseResult.ok(result);
        }
        List<RechargeTradeEntity> rechargeTradeList = rechargeTradeDao.findAll((Specification<RechargeTradeEntity>) (root, cq, cb) -> {
            List<Predicate> predicates = Lists.newArrayList();
            predicates.add(cb.in(root.get("siteId")).value(siteIds));
            if (StringUtil.isNotEmpty(rechargeTradeVo.getKeywordType()) && StringUtil.isNotEmpty(rechargeTradeVo.getKeyword())) {
                //关键词类型 1-订单号 2-交易流水号 3-手机号 4-站点名称
                switch (rechargeTradeVo.getKeywordType()) {
                    case 1:
                        predicates.add(cb.like(root.get("orderNum"), "%" + rechargeTradeVo.getKeyword() + "%"));
                        break;
                    case 2:
                        predicates.add(cb.like(root.get("flowNum"), "%" + rechargeTradeVo.getKeyword() + "%"));
                        break;
//                    case 3:
//                        break;
//                    case 4:
//                        break;
                }
            }
            if (StringUtil.isNotEmpty(rechargeTradeVo.getTradeType())) {
                predicates.add(cb.equal(root.get("tradeType"), rechargeTradeVo.getTradeType()));
            }
            if (StringUtil.isNotEmpty(rechargeTradeVo.getTradeStatus())) {
                predicates.add(cb.equal(root.get("tradeStatus"), rechargeTradeVo.getTradeStatus()));
            }
            if (StringUtil.isNotEmpty(rechargeTradeVo.getTradeWay())) {
                predicates.add(cb.equal(root.get("tradeWay"), rechargeTradeVo.getTradeWay()));
            }
            if (StringUtil.isNotEmpty(rechargeTradeVo.getAccountId())) {
                predicates.add(cb.equal(root.get("accountId"), rechargeTradeVo.getAccountId()));
            }
            if (StringUtil.isNotEmpty(rechargeTradeVo.getStartTime()) && StringUtil.isNotEmpty(rechargeTradeVo.getEndTime())) {
                predicates.add(cb.between(root.get("createTime"), DateUtil.strToLocalDateTime(DateUtil.getDayStart(rechargeTradeVo.getStartTime())),
                        DateUtil.strToLocalDateTime(DateUtil.getDayEnd(rechargeTradeVo.getEndTime()))));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        });
        if (CollectionUtils.isNotEmpty(rechargeTradeList)) {
            //根据多个站点id查询站点名称
            Map<String, SiteInfoDto> siteInfoMap = deviceService.findSiteBasicInfoByIds(rechargeTradeList.stream()
                    .map(RechargeTradeEntity::getSiteId).distinct().collect(Collectors.toList())).getData();
            //根据多个小程序用户id查询小程序用户手机号
            Set<String> appletUserIds = rechargeTradeList.stream().map(RechargeTradeEntity::getAppletUserId).collect(Collectors.toSet());
            Map<String, AppletUserInfoDto> appletUserInfoMap = togetherService.findAppletUserByIds(appletUserIds).getData();
            //根据多个账号id查询商户名称
            Set<String> accountIds = rechargeTradeList.stream().map(RechargeTradeEntity::getAccountId).collect(Collectors.toSet());
            Map<String, AccountDto> accountMap = systemService.findAccountListByAccountIds(accountIds).getData();
            //对数据进行组装
            List<RechargeTradeListDto.RechargeTrade> resultList = rechargeTradeList.stream().map(trade -> {
                RechargeTradeListDto.RechargeTrade rechargeTrade = new RechargeTradeListDto.RechargeTrade();
                BeanUtils.copyProperties(trade, rechargeTrade);
                if (accountMap.containsKey(trade.getAccountId())) {
                    rechargeTrade.setMchId(accountMap.get(trade.getAccountId()).getMchId());
                    rechargeTrade.setMchName(accountMap.get(trade.getAccountId()).getMchName());
                }
                if (appletUserInfoMap.containsKey(trade.getAppletUserId())) {
                    rechargeTrade.setPhoneNum(appletUserInfoMap.get(trade.getAppletUserId()).getPhoneNum());
                }
                if (siteInfoMap.containsKey(trade.getSiteId())) {
                    rechargeTrade.setSiteName(siteInfoMap.get(trade.getSiteId()).getSiteName());
                }
                if (StringUtil.isNotEmpty(trade.getCreateTime())) {
                    rechargeTrade.setCreateTime(DateUtil.localDateTimeToStr(trade.getCreateTime()));
                }
                if (StringUtil.isNotEmpty(trade.getUpdateTime())) {
                    rechargeTrade.setUpdateTime(DateUtil.localDateTimeToStr(trade.getUpdateTime()));
                }
                return rechargeTrade;
            }).collect(Collectors.toList());

            if (StringUtil.isNotEmpty(rechargeTradeVo.getKeywordType()) && StringUtil.isNotEmpty(rechargeTradeVo.getKeyword())) {
                //关键词类型 1-订单号 2-交易流水号 3-手机号 4-站点名称
                switch (rechargeTradeVo.getKeywordType()) {
                    case 3:
                        resultList = resultList.stream().filter(trade -> trade.getPhoneNum().contains(rechargeTradeVo.getKeyword())).collect(Collectors.toList());
                        break;
                    case 4:
                        resultList = resultList.stream().filter(trade -> trade.getSiteName().contains(rechargeTradeVo.getKeyword())).collect(Collectors.toList());
                        break;
                }
            }
            result.setIncomeMoney(DoubleUtil.getToBigDecimal(resultList.stream().filter(r -> Objects.equals(r.getDetailType(), 1)
                            && Objects.equals(r.getTradeStatus(), 2)).map(RechargeTradeListDto.RechargeTrade::getTradeMoney)
                            .reduce(BigDecimal.ZERO, BigDecimal::add)));
            result.setOutcomeMoney(DoubleUtil.getToBigDecimal(resultList.stream().filter(r -> Objects.equals(r.getDetailType(), 2)
                            && Objects.equals(r.getTradeStatus(), 2)).map(RechargeTradeListDto.RechargeTrade::getTradeMoney)
                            .reduce(BigDecimal.ZERO, BigDecimal::add)));
            result.setNetMoney(DoubleUtil.getToBigDecimal(result.getIncomeMoney().subtract(result.getOutcomeMoney())));
            //根据创建时间进行排序
            resultList.sort(Comparator.comparing(RechargeTradeListDto.RechargeTrade::getCreateTime).reversed());
            result.setPageDto(new PageDto<>(resultList, rechargeTradeVo.getPage(), rechargeTradeVo.getSize()));
        }
        return ResponseResult.ok(result);
    }

    @Override
    public ResponseResult<RechargeTradeDto> findRechargeTradeById(String id) {
        //返回的对象
        RechargeTradeDto result = new RechargeTradeDto();
        Optional<RechargeTradeEntity> optional = rechargeTradeDao.findById(id);
        if (optional.isPresent()) {
            RechargeTradeEntity trade = optional.get();
            BeanUtils.copyProperties(trade, result);
            if (StringUtil.isNotEmpty(trade.getCreateTime())) {
                result.setCreateTime(DateUtil.localDateTimeToStr(trade.getCreateTime()));
            }
            if (StringUtil.isNotEmpty(trade.getUpdateTime())) {
                result.setUpdateTime(DateUtil.localDateTimeToStr(trade.getUpdateTime()));
            }
            //获取商户id和商户名称
            AccountDto account = systemService.findAccountListByAccountIds(Collections.singleton(trade.getAccountId()))
                    .getData().get(trade.getAccountId());
            if (account != null) {
                result.setMchId(account.getMchId());
                result.setMchName(account.getMchName());
            }
            //获取手机号
            AppletUserInfoDto appletUser = togetherService.findAppletUserByIds(Collections.singleton(trade.getAppletUserId()))
                    .getData().get(trade.getAppletUserId());
            if (appletUser != null) {
                result.setNickName(appletUser.getNickName());
                result.setPhoneNum(appletUser.getPhoneNum());
            }
            //获取站点名称和租户名称
            SiteInfoDto siteInfo = deviceService.findSiteBasicInfoByIds(Collections.singletonList(trade.getSiteId())).getData().get(trade.getSiteId());
            if (siteInfo != null) {
                result.setTenantName(siteInfo.getTenantName());
                result.setSiteName(siteInfo.getSiteName());
            }
        }
        return ResponseResult.ok(result);
    }

    @Override
    public ResponseResult<DischargeTradeListDto> queryDischargeTradeList(DischargeTradeQueryVo dischargeTradeVo) {
        //返回的对象
        DischargeTradeListDto result = new DischargeTradeListDto();

        //根据用户id查询租户下面的站点id
        Set<String> siteIds = systemService.findAllOrganEmpowerByUserId(dischargeTradeVo.getUserId()).getData().stream()
                .map(OrganEmpowerListDto::getSiteId).collect(Collectors.toSet());
        if (CollectionUtils.isEmpty(siteIds)) {
            return ResponseResult.ok(result);
        }

        //根据多个站点id查询账户id
        Map<String, SiteAccountDto> accountMap = togetherService.findSiteAccountListBySiteIds(siteIds, 1).getData().values()
                .stream().flatMap(Collection::stream).collect(Collectors.toMap(SiteAccountDto::getAccountId,
                        d -> d, (k1, k2) -> k1));
        List<DischargeTradeEntity> dischargeTradeList = dischargeTradeDao.findAll((Specification<DischargeTradeEntity>) (root, cq, cb) -> {
            List<Predicate> predicates = Lists.newArrayList();
            if (StringUtil.isNotEmpty(dischargeTradeVo.getAccountId())) {
                predicates.add(cb.equal(root.get("accountId"), dischargeTradeVo.getAccountId()));
            } else {
                predicates.add(cb.in(root.get("accountId")).value(accountMap.keySet()));
            }
            if (StringUtil.isNotEmpty(dischargeTradeVo.getKeywordType()) && StringUtil.isNotEmpty(dischargeTradeVo.getKeyword())) {
                //关键词类型 1-订单号 2-手机号 3-站点名称
                if (dischargeTradeVo.getKeywordType() == 1) {
                    predicates.add(cb.like(root.get("orderNum"), "%" + dischargeTradeVo.getKeyword() + "%"));
                }
            }
            if (StringUtil.isNotEmpty(dischargeTradeVo.getTradeType())) {
                predicates.add(cb.equal(root.get("tradeType"), dischargeTradeVo.getTradeType()));
            }
            if (StringUtil.isNotEmpty(dischargeTradeVo.getTradeStatus())) {
                predicates.add(cb.equal(root.get("tradeStatus"), dischargeTradeVo.getTradeStatus()));
            }
            if (StringUtil.isNotEmpty(dischargeTradeVo.getTradeWay())) {
                predicates.add(cb.equal(root.get("tradeWay"), dischargeTradeVo.getTradeWay()));
            }
            if (StringUtil.isNotEmpty(dischargeTradeVo.getStartTime()) && StringUtil.isNotEmpty(dischargeTradeVo.getEndTime())) {
                predicates.add(cb.between(root.get("createTime"), DateUtil.strToLocalDateTime(DateUtil.getDayStart(dischargeTradeVo.getStartTime())),
                        DateUtil.strToLocalDateTime(DateUtil.getDayEnd(dischargeTradeVo.getEndTime()))));
            }
            if (StringUtil.isNotEmpty(dischargeTradeVo.getAppletUserId())) {
                predicates.add(cb.equal(root.get("appletUserId"), dischargeTradeVo.getAppletUserId()));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        });
        if (CollectionUtils.isNotEmpty(dischargeTradeList)) {
            //根据多个站点id查询站点名称
            Map<String, SiteInfoDto> siteInfoMap = deviceService.findSiteBasicInfoByIds(dischargeTradeList.stream()
                    .map(DischargeTradeEntity::getSiteId).filter(StringUtil::isNotEmpty).distinct().collect(Collectors.toList())).getData();
            //根据多个小程序用户id查询小程序用户手机号
            Set<String> appletUserIds = dischargeTradeList.stream().map(DischargeTradeEntity::getAppletUserId).collect(Collectors.toSet());
            Map<String, AppletUserInfoDto> appletUserInfoMap = togetherService.findAppletUserByIds(appletUserIds).getData();
//            //根据多个账号id查询商户名称
//            Set<String> accountIds = dischargeTradeList.stream().map(DischargeTradeEntity::getAccountId).collect(Collectors.toSet());
//            Map<String, AccountDto> accountMap = systemService.findAccountListByAccountIds(accountIds).getData();
            //对数据进行组装
            List<DischargeTradeListDto.DischargeTrade> resultList = dischargeTradeList.stream().map(trade -> {
                DischargeTradeListDto.DischargeTrade dischargeTrade = new DischargeTradeListDto.DischargeTrade();
                BeanUtils.copyProperties(trade, dischargeTrade);
                if (accountMap.containsKey(trade.getAccountId())) {
                    dischargeTrade.setMchId(accountMap.get(trade.getAccountId()).getMchId());
                    dischargeTrade.setMchName(accountMap.get(trade.getAccountId()).getMchName());
                }
                if (appletUserInfoMap.containsKey(trade.getAppletUserId())) {
                    dischargeTrade.setPhoneNum(appletUserInfoMap.get(trade.getAppletUserId()).getPhoneNum());
                }
                if (StringUtil.isNotEmpty(trade.getSiteId()) && siteInfoMap.containsKey(trade.getSiteId())) {
                    dischargeTrade.setSiteName(siteInfoMap.get(trade.getSiteId()).getSiteName());
                }
                if (StringUtil.isNotEmpty(trade.getCreateTime())) {
                    dischargeTrade.setCreateTime(DateUtil.localDateTimeToStr(trade.getCreateTime()));
                }
                if (StringUtil.isNotEmpty(trade.getUpdateTime())) {
                    dischargeTrade.setUpdateTime(DateUtil.localDateTimeToStr(trade.getUpdateTime()));
                }
                return dischargeTrade;
            }).collect(Collectors.toList());

            if (StringUtil.isNotEmpty(dischargeTradeVo.getKeywordType()) && StringUtil.isNotEmpty(dischargeTradeVo.getKeyword())) {
                //关键词类型 1-订单号 2-手机号 3-站点名称
                switch (dischargeTradeVo.getKeywordType()) {
                    case 2:
                        resultList = resultList.stream().filter(trade -> trade.getPhoneNum().contains(dischargeTradeVo.getKeyword())).collect(Collectors.toList());
                        break;
                    case 3:
                        resultList = resultList.stream().filter(trade -> trade.getSiteName().contains(dischargeTradeVo.getKeyword())).collect(Collectors.toList());
                        break;
                }
            }
            //根据创建时间进行排序
            resultList.sort(Comparator.comparing(DischargeTradeListDto.DischargeTrade::getCreateTime).reversed());
            result.setPageDto(new PageDto<>(resultList, dischargeTradeVo.getPage(), dischargeTradeVo.getSize()));
        }
        return ResponseResult.ok(result);
    }

    @Override
    public ResponseResult<DischargeTradeDto> findDischargeTradeById(String id) {
        //返回的对象
        DischargeTradeDto result = new DischargeTradeDto();
        Optional<DischargeTradeEntity> optional = dischargeTradeDao.findById(id);
        if (optional.isPresent()) {
            DischargeTradeEntity trade = optional.get();
            BeanUtils.copyProperties(trade, result);
            if (StringUtil.isNotEmpty(trade.getCreateTime())) {
                result.setCreateTime(DateUtil.localDateTimeToStr(trade.getCreateTime()));
            }
            if (StringUtil.isNotEmpty(trade.getUpdateTime())) {
                result.setUpdateTime(DateUtil.localDateTimeToStr(trade.getUpdateTime()));
            }
            //获取商户id和商户名称
            AccountDto account = systemService.findAccountListByAccountIds(Collections.singleton(trade.getAccountId()))
                    .getData().get(trade.getAccountId());
            if (account != null) {
                result.setMchId(account.getMchId());
                result.setMchName(account.getMchName());
            }
            //获取手机号
            AppletUserInfoDto appletUser = togetherService.findAppletUserByIds(Collections.singleton(trade.getAppletUserId()))
                    .getData().get(trade.getAppletUserId());
            if (appletUser != null) {
                result.setNickName(appletUser.getNickName());
                result.setPhoneNum(appletUser.getPhoneNum());
            }
            //获取站点名称和租户名称
            if (StringUtil.isNotEmpty(trade.getSiteId())) {
                SiteInfoDto siteInfo = deviceService.findSiteBasicInfoByIds(Collections.singletonList(trade.getSiteId())).getData().get(trade.getSiteId());
                if (siteInfo != null) {
                    result.setTenantName(siteInfo.getTenantName());
                    result.setSiteName(siteInfo.getSiteName());
                }
            }
        }
        return ResponseResult.ok(result);
    }

    @Override
    public ResponseResult<BigDecimal> findRefundMoneyByOrderNum(String orderNum) {
        //返回的对象
        BigDecimal result = new BigDecimal("0.0");
        //根据订单编号查询充电支付成功的订单
        RechargeTradeEntity rechargeTrade = rechargeTradeDao.findAllByOrderNumAndTradeTypeAndTradeStatus(orderNum, 1, 2);
        if (rechargeTrade != null) {
            //获取充电支付金额
            BigDecimal payMoney = rechargeTrade.getTradeMoney();
            //查询该订单退款成功的金额
            List<RechargeTradeEntity> tradeRefundList = rechargeTradeDao.findAll(Example.of(RechargeTradeEntity.builder().orderNum(orderNum).tradeType(2).tradeStatus(2).build()));
            BigDecimal refundMoney = new BigDecimal("0.0");
            if (CollectionUtils.isNotEmpty(tradeRefundList)) {
                refundMoney = tradeRefundList.stream().map(RechargeTradeEntity::getTradeMoney).filter(StringUtil::isNotEmpty).reduce(BigDecimal.ZERO, BigDecimal::add);
            }
            result = payMoney.subtract(refundMoney);
        }
        return ResponseResult.ok(result);
    }

    @Override
    public ResponseResult<List<RechargeTradeDto>> findRechargeTradeListByOrderNums(List<String> orderNums) {
        //返回的集合
        List<RechargeTradeDto> resultList = Lists.newArrayList();
        //根据订单编号查询充电支付成功的订单
        List<RechargeTradeEntity> rechargeTradeList = rechargeTradeDao.findAllByOrderNumInAndTradeTypeAndTradeStatus(orderNums, 1, 2);
        if (CollectionUtils.isNotEmpty(rechargeTradeList)) {
            resultList = rechargeTradeList.stream().map(trade -> {
                RechargeTradeDto result = new RechargeTradeDto();
                BeanUtils.copyProperties(trade, result);
                return result;
            }).collect(Collectors.toList());
        }
        return ResponseResult.ok(resultList);
    }

}
