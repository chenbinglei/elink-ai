package com.sunmax.together.service.operation.impl;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.sunmax.common.dto.PageDto;
import com.sunmax.common.dto.device.SiteInfoDto;
import com.sunmax.common.dto.system.AccountDto;
import com.sunmax.common.dto.system.OrganEmpowerListDto;
import com.sunmax.common.dto.system.TenantDetailsDto;
import com.sunmax.common.dto.system.UserDto;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.util.StringUtil;
import com.sunmax.common.vo.SiteFieldParamVo;
import com.sunmax.together.dao.SiteAccountDao;
import com.sunmax.together.dto.operation.settlement.SiteAccountDetailDto;
import com.sunmax.together.dto.operation.settlement.SiteAccountListDto;
import com.sunmax.together.entity.SiteAccountEntity;
import com.sunmax.together.service.operation.SettlementService;
import com.sunmax.together.service.feign.DeviceService;
import com.sunmax.together.service.feign.SystemService;
import com.sunmax.together.vo.operation.settlement.SiteAccountChangeVo;
import com.sunmax.together.vo.operation.settlement.SiteAccountQueryVo;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.compress.utils.Lists;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class SettlementServiceImpl implements SettlementService {

    @Autowired
    private DeviceService deviceService;

    @Autowired
    private SystemService systemService;

    @Autowired
    private SiteAccountDao siteAccountDao;

    @Override
    public ResponseResult<PageDto<SiteAccountListDto>> querySiteAccountList(SiteAccountQueryVo siteAccountQueryVo) {
        //返回的集合
        List<SiteAccountListDto> resultList = Lists.newArrayList();
        //根据用户id查询组织机构列表
        List<OrganEmpowerListDto> organEmpowerList = systemService.findAllOrganEmpowerByUserId(siteAccountQueryVo.getUserId()).getData()
                .stream().filter(o -> StringUtil.isNotEmpty(o.getSiteId())).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(organEmpowerList)) {
            //根据多个站点id查询站点数据
            List<String> siteIds = organEmpowerList.stream().map(OrganEmpowerListDto::getSiteId).distinct().collect(Collectors.toList());
            Map<String, SiteInfoDto> siteInfoMap = deviceService.findSiteBasicInfoByIds(siteIds).getData();

            //根据多个站点id查询收款信息
            Map<String, SiteAccountEntity> recSiteAccountMap = siteAccountDao.findBySiteIdInAndType(siteIds, 1).stream().collect(Collectors
                    .toMap(SiteAccountEntity::getSiteId, item -> item, (k1, k2) -> k1));

            //根据多个站点id查询付款信息
            Map<String, SiteAccountEntity> paySiteAccountMap = siteAccountDao.findBySiteIdInAndType(siteIds, 2).stream().collect(Collectors
                    .toMap(SiteAccountEntity::getSiteId, item -> item, (k1, k2) -> k1));

            //根据多个账户id查询账号信息
            Map<String, AccountDto> accountMap = Maps.newHashMap();
            Set<String> accountIds = Sets.newHashSet();
            accountIds.addAll(recSiteAccountMap.values().stream().map(SiteAccountEntity::getAccountId).collect(Collectors.toList()));
            accountIds.addAll(paySiteAccountMap.values().stream().map(SiteAccountEntity::getAccountId).collect(Collectors.toList()));
            if (CollectionUtils.isNotEmpty(accountIds)) {
                accountMap = systemService.findAccountListByAccountIds(accountIds).getData();
            }

            //对数据进行组装
            Map<String, AccountDto> finalAccountMap = accountMap;
            resultList = organEmpowerList.stream().filter(item -> siteInfoMap.containsKey(item.getSiteId())).map(item -> {
                SiteAccountListDto result = new SiteAccountListDto();
                result.setSiteId(item.getSiteId());
                result.setAuthority(item.getAuthority());
                result.setSiteName(item.getSiteName());
                if (siteInfoMap.containsKey(item.getSiteId())) {
                    SiteInfoDto siteInfo = siteInfoMap.get(item.getSiteId());
                    result.setTenantId(siteInfo.getTenantId());
                    result.setTenantName(siteInfo.getTenantName());
                    //获取省市数据
                    if (StringUtil.isNotEmpty(siteInfo.getSiteReadwriteObject())) {
                        JSONObject jsonObject = JSONObject.parseObject(siteInfo.getSiteReadwriteObject());
                        if (jsonObject.containsKey(SiteFieldParamVo.LOCATION)) {
                            JSONObject locationMap = jsonObject.getJSONObject(SiteFieldParamVo.LOCATION);
                            if (locationMap.containsKey(SiteFieldParamVo.PROVINCE)) {
                                result.setProvince(locationMap.getString(SiteFieldParamVo.PROVINCE));
                            }
                            if (locationMap.containsKey(SiteFieldParamVo.CITY)) {
                                result.setCity(locationMap.getString(SiteFieldParamVo.CITY));
                            }
                        }
                    }
                }
                if (recSiteAccountMap.containsKey(item.getSiteId())) {
                    SiteAccountEntity siteAccount = recSiteAccountMap.get(item.getSiteId());
                    result.setRecId(siteAccount.getAccountId());
                    if (finalAccountMap.containsKey(siteAccount.getAccountId())) {
                        AccountDto account = finalAccountMap.get(siteAccount.getAccountId());
                        result.setRecMchName(account.getMchName());
                        result.setRecMchId(account.getMchId());
                    }
                }
                if (paySiteAccountMap.containsKey(item.getSiteId())) {
                    SiteAccountEntity siteAccount = paySiteAccountMap.get(item.getSiteId());
                    result.setRecId(siteAccount.getAccountId());
                    if (finalAccountMap.containsKey(siteAccount.getAccountId())) {
                        AccountDto account = finalAccountMap.get(siteAccount.getAccountId());
                        result.setPayMchName(account.getMchName());
                        result.setPayMchId(account.getMchId());
                    }
                }
                return result;
            }).collect(Collectors.toList());

            //对查询条件进行过滤
            if (StringUtil.isNotEmpty(siteAccountQueryVo.getTenantId())) { //租户id
                resultList = resultList.stream().filter(item -> Objects.equals(item.getTenantId(), siteAccountQueryVo.getTenantId())).collect(Collectors.toList());
            }
            if (StringUtil.isNotEmpty(siteAccountQueryVo.getSiteId())) { //站点id
                resultList = resultList.stream().filter(item -> Objects.equals(item.getSiteId(), siteAccountQueryVo.getSiteId())).collect(Collectors.toList());
            }
            if (StringUtil.isNotEmpty(siteAccountQueryVo.getAreaType()) && StringUtil.isNotEmpty(siteAccountQueryVo.getAreaValue())) {
                if (siteAccountQueryVo.getAreaType() == 1) { //省
                    resultList = resultList.stream().filter(item -> Objects.equals(item.getProvince(), siteAccountQueryVo.getAreaValue())).collect(Collectors.toList());
                }
                if (siteAccountQueryVo.getAreaType() == 2) { //市
                    resultList = resultList.stream().filter(item -> Objects.equals(item.getCity(), siteAccountQueryVo.getAreaValue())).collect(Collectors.toList());
                }
            }
        }
        return ResponseResult.ok(new PageDto<>(resultList, siteAccountQueryVo.getPage(), siteAccountQueryVo.getSize()));
    }

    @Override
    public ResponseResult<List<SiteAccountDetailDto>> findSiteAccountListBySiteIdAndType(String siteId, Integer type) {
        //返回的集合
        List<SiteAccountDetailDto> resultList = Lists.newArrayList();
        //根据站点id和类型查询站点账户数据
        List<SiteAccountEntity> siteAccountList = siteAccountDao.findBySiteIdAndTypeIn(siteId, Collections.singleton(type));
        if (CollectionUtils.isNotEmpty(siteAccountList)) {
            //根据多个租户id查询租户名称
            List<String> tenantIds = siteAccountList.stream().map(SiteAccountEntity::getTenantId).distinct().collect(Collectors.toList());
            Map<String, String> tenantMap = systemService.findAllTenantInfoByIds(tenantIds).getData().stream().collect(Collectors
                    .toMap(TenantDetailsDto::getId, TenantDetailsDto::getTenantName, (k1, k2) -> k1));
            //根据多个账户id查询账户数据
            Set<String> accountIds = siteAccountList.stream().map(SiteAccountEntity::getAccountId).collect(Collectors.toSet());
            Map<String, AccountDto> accountMap = systemService.findAccountListByAccountIds(accountIds).getData();
            resultList = siteAccountList.stream().map(siteAccount -> {
                SiteAccountDetailDto result = new SiteAccountDetailDto();
                BeanUtils.copyProperties(siteAccount, result);
                result.setTenantName(tenantMap.get(siteAccount.getTenantId()));
                if (accountMap.containsKey(siteAccount.getAccountId())) {
                    AccountDto account = accountMap.get(siteAccount.getAccountId());
                    result.setMchName(account.getMchName());
                    result.setMchId(account.getMchId());
                }
                return result;
            }).collect(Collectors.toList());
        }
        return ResponseResult.ok(resultList);
    }

    @Override
    public ResponseResult<AccountDto> findAccountByAccountId(String accountId) {
        //返回的对象
        AccountDto result = new AccountDto();
        Map<String, AccountDto> accountDtoMap = systemService.findAccountListByAccountIds(Collections.singleton(accountId)).getData();
        if (accountDtoMap.containsKey(accountId)) {
            BeanUtils.copyProperties(accountDtoMap.get(accountId), result);
        }
        return ResponseResult.ok(result);
    }

    @Override
    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class)
    public ResponseResult<Void> saveSiteAccount(SiteAccountChangeVo siteAccountChangeVo) {
        //根据站点id和类型查询站点账户数据
        List<SiteAccountEntity> siteAccountList = Lists.newArrayList();
        if (siteAccountChangeVo.getType() != 3) {
            siteAccountList = siteAccountDao.findBySiteIdAndTypeIn(siteAccountChangeVo.getSiteId(), Collections.singleton(siteAccountChangeVo.getType()));
        }
        //新增站点账户数据
        if (StringUtil.isEmpty(siteAccountChangeVo.getId())) {
            //校验该站点是否存在收款和付款账户
            if (CollectionUtils.isNotEmpty(siteAccountList)) {
                return ResponseResult.paramShow(siteAccountChangeVo.getSiteId(), ResponseResult.PARAM_EXIST);
            }
            SiteAccountEntity siteAccount = new SiteAccountEntity();
            BeanUtils.copyProperties(siteAccountChangeVo, siteAccount);
            siteAccountDao.save(siteAccount);
            return ResponseResult.ok();
        } else {
            //校验该站点是否存在收款和付款账户
            siteAccountList = siteAccountList.stream().filter(item -> !Objects.equals(item.getId(), siteAccountChangeVo.getId())).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(siteAccountList)) {
                return ResponseResult.paramShow(siteAccountChangeVo.getSiteId(), ResponseResult.PARAM_EXIST);
            }
            //修改站点账户数据
            Optional<SiteAccountEntity> optional = siteAccountDao.findById(siteAccountChangeVo.getId());
            if (optional.isPresent()) {
                SiteAccountEntity siteAccount = new SiteAccountEntity();
                BeanUtils.copyProperties(siteAccountChangeVo, siteAccount);
                siteAccount.setCreateTime(optional.get().getCreateTime());
                siteAccountDao.save(siteAccount);
                return ResponseResult.ok();
            }
        }
        return ResponseResult.paramError(ResponseResult.PARAM_ERROR);
    }

    @Override
    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class)
    public ResponseResult<Void> deleteSiteAccountById(String id) {
        siteAccountDao.deleteById(id);
        return ResponseResult.ok();
    }

    @Override
    public ResponseResult<List<AccountDto>> findAccountListByUserId(String userId, Integer platformType) {
        //返回的集合
        List<AccountDto> resultList = Lists.newArrayList();

        //根据用户id查询用户下面的租户id
        UserDto user = systemService.findUserInfoByIdsFeign(Collections.singletonList(userId)).getData().get(userId);
        if (user != null && StringUtil.isNotEmpty(user.getTenantId())) {
            resultList.addAll(systemService.findAccountListByTenantId(user.getTenantId(), platformType).getData());
        }

        //根据用户id查询所有站点id
        List<String> siteIds = systemService.findAllOrganEmpowerByUserId(userId).getData().stream().map(OrganEmpowerListDto::getSiteId)
                .distinct().collect(Collectors.toList());
        //根据多个站点id查询结算账户数据
        Set<String> accountIds = siteAccountDao.findAllBySiteIdInAndPayPlatform(siteIds, platformType).stream().map(SiteAccountEntity::getAccountId)
                .filter(StringUtil::isNotEmpty).collect(Collectors.toSet());
        if (CollectionUtils.isNotEmpty(accountIds)) {
            resultList.addAll(systemService.findAccountListByAccountIds(accountIds).getData().values());
        }
        return ResponseResult.ok(resultList.stream().distinct().collect(Collectors.toList()));
    }


}
