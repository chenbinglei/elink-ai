package com.sunmax.system.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.sunmax.common.dto.system.OperatorInfoDto;
import com.sunmax.common.dto.system.PlatformDataForwardDto;
import com.sunmax.common.dto.system.SiteOperateDto;
import com.sunmax.common.dto.system.StorageDataForwardDto;
import com.sunmax.common.dto.system.dynamic.HttpForwardDto;
import com.sunmax.common.dto.system.dynamic.StorageHttpForwardDto;
import com.sunmax.common.entity.BaseTimeEntity;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.util.StringUtil;
import com.sunmax.system.dao.DataConfigDao;
import com.sunmax.system.dao.DataForwardDao;
import com.sunmax.system.dao.OperatorInfoDao;
import com.sunmax.system.entity.DataConfigEntity;
import com.sunmax.system.entity.DataForwardEntity;
import com.sunmax.system.service.ConfigureFeignService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.compress.utils.Lists;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ConfigureFeignServiceImpl implements ConfigureFeignService {

    @Autowired
    private DataConfigDao dataConfigDao;

    @Autowired
    private DataForwardDao dataForwardDao;

    @Autowired
    private OperatorInfoDao operatorInfoDao;

    @Override
    public ResponseResult<PlatformDataForwardDto> getPlatformDataForward(String platformId, String protocolCode) {
        //根据平台id和协议标识查询数据转发数据
        List<DataForwardEntity> dataForwardList = dataForwardDao.findAll(Example.of(DataForwardEntity.builder().protocolCode(protocolCode)
                        .protocolType(2).status(1).build())).stream().filter(d -> StringUtil.isNotEmpty(d.getDynamicFields()))
                .filter(d -> Objects.equals(JSON.parseObject(d.getDynamicFields(), HttpForwardDto.class).getPlatformId(), platformId))
                .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(dataForwardList)) {
            DataForwardEntity dataForward = dataForwardList.get(0);
            //返回的对象
            PlatformDataForwardDto result = new PlatformDataForwardDto();
            HttpForwardDto httpForward = JSON.parseObject(dataForward.getDynamicFields(), HttpForwardDto.class);
            BeanUtils.copyProperties(dataForward, result);
            //平台运营商相关信息
            result.setPlatformId(httpForward.getPlatformId());
            result.setPlatformSecret(httpForward.getPlatformSecret());
            result.setDataSecret(httpForward.getDataSecret());
            result.setDataSecretIv(httpForward.getDataSecretIv());
            result.setSigSecret(httpForward.getSigSecret());
            //站点及运营商相关信息
            List<DataConfigEntity> dataConfigList = dataConfigDao.findAllByForwardIdIn(Collections.singletonList(dataForward.getId()));
            if (CollectionUtils.isNotEmpty(dataConfigList)) {
                List<SiteOperateDto> siteOperateList = dataConfigList.stream().filter(d -> StringUtil.isNotEmpty(d.getDynamicConfigs())
                        && StringUtil.isNotEmpty(JSON.parseObject(d.getDynamicConfigs()).getString("operatorId"))).map(d -> {
                    SiteOperateDto siteOperate = new SiteOperateDto();
                    siteOperate.setSiteId(d.getSiteId());
                    siteOperate.setOperateId(JSON.parseObject(d.getDynamicConfigs()).getString("operatorId"));
                    return siteOperate;
                }).collect(Collectors.toList());
                result.setSiteOperateList(siteOperateList);
                //根据多个运营商id查询运营商信息
                List<String> operateIds = siteOperateList.stream().map(SiteOperateDto::getOperateId).distinct().collect(Collectors.toList());
                result.setOperatorInfoList(operatorInfoDao.findAllByOperatorIdIn(operateIds).stream().map(o -> {
                    OperatorInfoDto operatorInfoDto = new OperatorInfoDto();
                    BeanUtils.copyProperties(o, operatorInfoDto);
                    return operatorInfoDto;
                }).collect(Collectors.toList()));
            }
            return ResponseResult.ok(result);
        }
        return ResponseResult.paramError(ResponseResult.PARAM_ERROR);
    }

    @Override
    public ResponseResult<List<PlatformDataForwardDto>> getPlatformDataForwardList(Set<String> protocolCodes) {
        //返回的集合
        List<PlatformDataForwardDto> resultList = Lists.newArrayList();

        //定义数据转发集合列表
        List<DataForwardEntity> dataForwardList = dataForwardDao.findAll(Example.of(DataForwardEntity.builder().protocolType(2).status(1).build())).stream()
                .filter(d -> StringUtil.isNotEmpty(d.getDynamicFields()) && StringUtil.isNotEmpty(d.getProtocolCode())
                        && protocolCodes.contains(d.getProtocolCode())).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(dataForwardList)) {
            //站点及运营商相关信息
            List<String> dataForwardIds = dataForwardList.stream().map(BaseTimeEntity::getId).distinct().collect(Collectors.toList());
            List<DataConfigEntity> dataConfigList = dataConfigDao.findAllByForwardIdIn(dataForwardIds).stream()
                    .filter(d -> StringUtil.isNotEmpty(d.getDynamicConfigs())
                            && StringUtil.isNotEmpty(JSON.parseObject(d.getDynamicConfigs()).getString("operatorId")))
                    .collect(Collectors.toList());
            //数据配置数据
            Map<String, List<DataConfigEntity>> dataConfigMap = dataConfigList.stream().collect(Collectors.groupingBy(DataConfigEntity::getForwardId));
            //根据多个运营商id查询运营商信息
            List<String> operateIds = dataConfigList.stream().map(d -> JSON.parseObject(d.getDynamicConfigs()).getString("operatorId"))
                    .distinct().collect(Collectors.toList());
            Map<String, OperatorInfoDto> operatorInfoMap = operatorInfoDao.findAllByOperatorIdIn(operateIds).stream().map(o -> {
                OperatorInfoDto operatorInfoDto = new OperatorInfoDto();
                BeanUtils.copyProperties(o, operatorInfoDto);
                return operatorInfoDto;
            }).collect(Collectors.toMap(OperatorInfoDto::getOperatorId, a -> a, (k1, k2) -> k1));
            //对数据进行组装
            resultList = dataForwardList.stream().map(dataForward -> {
                //返回的对象
                PlatformDataForwardDto result = new PlatformDataForwardDto();
                HttpForwardDto httpForward = JSON.parseObject(dataForward.getDynamicFields(), HttpForwardDto.class);
                BeanUtils.copyProperties(dataForward, result);
                //平台运营商相关信息
                result.setPlatformId(httpForward.getPlatformId());
                result.setPlatformSecret(httpForward.getPlatformSecret());
                result.setDataSecret(httpForward.getDataSecret());
                result.setDataSecretIv(httpForward.getDataSecretIv());
                result.setSigSecret(httpForward.getSigSecret());
                if (dataConfigMap.containsKey(dataForward.getId())) {
                    List<SiteOperateDto> siteOperateList = dataConfigMap.get(dataForward.getId()).stream().filter(d -> StringUtil.isNotEmpty(d.getDynamicConfigs())
                            && StringUtil.isNotEmpty(JSON.parseObject(d.getDynamicConfigs()).getString("operatorId"))).map(d -> {
                        SiteOperateDto siteOperate = new SiteOperateDto();
                        siteOperate.setSiteId(d.getSiteId());
                        JSONObject dynamicConfigs = JSON.parseObject(d.getDynamicConfigs());
                        if (dynamicConfigs.containsKey("operatorId")) { //运营商ID
                            siteOperate.setOperateId(dynamicConfigs.getString("operatorId"));
                        }
                        if (dynamicConfigs.containsKey("resourceSn")) { //资源编号
                            siteOperate.setResourceSn(dynamicConfigs.getString("resourceSn"));
                        }
                        return siteOperate;
                    }).collect(Collectors.toList());
                    result.setSiteOperateList(siteOperateList);
                    if (CollectionUtils.isNotEmpty(siteOperateList)) {
                        result.setOperatorInfoList(siteOperateList.stream().map(SiteOperateDto::getOperateId)
                                .distinct().filter(operatorInfoMap::containsKey).map(o -> {
                                    OperatorInfoDto operatorInfoDto = new OperatorInfoDto();
                                    BeanUtils.copyProperties(o, operatorInfoDto);
                                    return operatorInfoDto;
                                }).collect(Collectors.toList()));
                    }
                }
                return result;
            }).collect(Collectors.toList());
        }
        return ResponseResult.ok(resultList);
    }

    @Override
    public ResponseResult<List<StorageDataForwardDto>> getStorageDataForwardList(Set<String> protocolCodes) {
        //返回的集合
        List<StorageDataForwardDto> resultList = Lists.newArrayList();

        //定义数据转发集合列表
        List<DataForwardEntity> dataForwardList = dataForwardDao.findAll(Example.of(DataForwardEntity.builder().protocolType(2).status(1).build())).stream()
                .filter(d -> StringUtil.isNotEmpty(d.getDynamicFields()) && StringUtil.isNotEmpty(d.getProtocolCode())
                        && protocolCodes.contains(d.getProtocolCode())).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(dataForwardList)) {
            //站点及运营商相关信息
            List<String> dataForwardIds = dataForwardList.stream().map(BaseTimeEntity::getId).distinct().collect(Collectors.toList());
            List<DataConfigEntity> dataConfigList = dataConfigDao.findAllByForwardIdIn(dataForwardIds).stream()
                    .filter(d -> StringUtil.isNotEmpty(d.getDynamicConfigs())
                            && StringUtil.isNotEmpty(JSON.parseObject(d.getDynamicConfigs()).getString("operatorId")))
                    .collect(Collectors.toList());
            //数据配置数据
            Map<String, List<DataConfigEntity>> dataConfigMap = dataConfigList.stream().collect(Collectors.groupingBy(DataConfigEntity::getForwardId));
            //根据多个运营商id查询运营商信息
            List<String> operateIds = dataConfigList.stream().map(d -> JSON.parseObject(d.getDynamicConfigs()).getString("operatorId"))
                    .distinct().collect(Collectors.toList());
            Map<String, OperatorInfoDto> operatorInfoMap = operatorInfoDao.findAllByOperatorIdIn(operateIds).stream().map(o -> {
                OperatorInfoDto operatorInfoDto = new OperatorInfoDto();
                BeanUtils.copyProperties(o, operatorInfoDto);
                return operatorInfoDto;
            }).collect(Collectors.toMap(OperatorInfoDto::getOperatorId, a -> a, (k1, k2) -> k1));
            //对数据进行组装
            resultList = dataForwardList.stream().map(dataForward -> {
                //返回的对象
                StorageDataForwardDto result = new StorageDataForwardDto();
                StorageHttpForwardDto httpForward = JSON.parseObject(dataForward.getDynamicFields(), StorageHttpForwardDto.class);
                BeanUtils.copyProperties(dataForward, result);
                //平台运营商相关信息
                result.setAppId(httpForward.getAppId());
                result.setPublicKey(httpForward.getPublicKey());
                result.setPrivateKey(httpForward.getPrivateKey());
                if (dataConfigMap.containsKey(dataForward.getId())) {
                    List<SiteOperateDto> siteOperateList = dataConfigMap.get(dataForward.getId()).stream().filter(d -> StringUtil.isNotEmpty(d.getDynamicConfigs())
                            && StringUtil.isNotEmpty(JSON.parseObject(d.getDynamicConfigs()).getString("operatorId"))).map(d -> {
                        SiteOperateDto siteOperate = new SiteOperateDto();
                        siteOperate.setSiteId(d.getSiteId());
                        JSONObject dynamicConfigs = JSON.parseObject(d.getDynamicConfigs());
                        if (dynamicConfigs.containsKey("operatorId")) { //运营商ID
                            siteOperate.setOperateId(dynamicConfigs.getString("operatorId"));
                        }
                        if (dynamicConfigs.containsKey("resourceSn")) { //资源编号
                            siteOperate.setResourceSn(dynamicConfigs.getString("resourceSn"));
                        }
                        return siteOperate;
                    }).collect(Collectors.toList());
                    result.setSiteOperateList(siteOperateList);
                    if (CollectionUtils.isNotEmpty(siteOperateList)) {
                        result.setOperatorInfoList(siteOperateList.stream().map(SiteOperateDto::getOperateId)
                                .distinct().filter(operatorInfoMap::containsKey).map(o -> {
                                    OperatorInfoDto operatorInfoDto = new OperatorInfoDto();
                                    BeanUtils.copyProperties(o, operatorInfoDto);
                                    return operatorInfoDto;
                                }).collect(Collectors.toList()));
                    }
                }
                return result;
            }).collect(Collectors.toList());
        }
        return ResponseResult.ok(resultList);
    }

}
