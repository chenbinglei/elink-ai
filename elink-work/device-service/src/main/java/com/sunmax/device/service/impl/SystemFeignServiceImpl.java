package com.sunmax.device.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.sunmax.common.dto.device.DeviceReaDto;
import com.sunmax.common.dto.device.SiteInfoDto;
import com.sunmax.common.dto.device.SiteScenarioTypeDto;
import com.sunmax.common.dto.device.SiteSetUpDto;
import com.sunmax.common.dto.system.TenantDetailsDto;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.util.StringUtil;
import com.sunmax.device.dao.access.ScenarioTypeDao;
import com.sunmax.device.dao.access.SiteInfoDao;
import com.sunmax.device.dao.access.SiteSetUpDao;
import com.sunmax.device.dao.model.ModelReaDao;
import com.sunmax.device.dao.model.ReaDao;
import com.sunmax.device.entity.access.ScenarioTypeEntity;
import com.sunmax.device.entity.access.SiteInfoEntity;
import com.sunmax.device.entity.access.SiteSetUpEntity;
import com.sunmax.device.entity.model.ModelReaEntity;
import com.sunmax.device.service.SystemFeignService;
import com.sunmax.device.service.feign.SystemService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SystemFeignServiceImpl implements SystemFeignService {

    @Autowired
    private SiteInfoDao siteInfoDao;

    @Autowired
    private ScenarioTypeDao scenarioTypeDao;

    @Autowired
    private SystemService systemService;

    @Autowired
    private ModelReaDao modelReaDao;

    @Autowired
    private ReaDao reaDao;

    @Autowired
    private SiteSetUpDao siteSetUpDao;


    /**
     * 查询全部站点详情列表
     * @param siteNameLike
     * @return
     */
    @Override
    public ResponseResult<List<SiteInfoDto>> findAllSiteBasicInfoList(String siteNameLike) {
        //返回数据
        List<SiteInfoDto> siteInfoDtos = Lists.newArrayList();

        //查询站点数据信息
        List<SiteInfoEntity> siteList = siteInfoDao.findAll((Specification<SiteInfoEntity>) (root, cq, cb) -> {
            List<Predicate> list = new ArrayList<>();
            list.add(cb.equal(root.get("isDelete"), 1));
            if (StringUtil.isNotEmpty(siteNameLike)) {
                //站点名称查询
                list.add(cb.like(root.get("siteName"), "%" + siteNameLike + "%"));
            }
            return cb.and(list.toArray(new Predicate[0]));
        }, Sort.by("createTime").descending());

        if (CollectionUtils.isNotEmpty(siteList)) {
            siteInfoDtos = siteList.stream().map(siteEntity -> {
                SiteInfoDto siteInfoDto = new SiteInfoDto();
                BeanUtils.copyProperties(siteEntity, siteInfoDto);
                return siteInfoDto;
            }).collect(Collectors.toList());
        }
        return ResponseResult.ok(siteInfoDtos);
    }

    /**
     * 根据多个站点id查询站站点详情数据
     * @param siteIdList
     * @return
     */
    @Override
    public ResponseResult<Map<String, SiteInfoDto>> findSiteBasicInfoByIds(List<String> siteIdList) {
        //返回数据对象
        Map<String, SiteInfoDto> resultMap = Maps.newHashMap();
        //根据多个站点id，查询站点信息列表
        List<SiteInfoEntity> siteEntityList = siteInfoDao.findAllByIdInAndIsDelete(siteIdList, 1);
        if (CollectionUtils.isNotEmpty(siteEntityList)) {
            //获取多个租户id，查询租户信息
            Map<String, TenantDetailsDto> tenantDetailsDtoMap = Maps.newHashMap();
            List<String> tenantIdList = siteEntityList.stream().map(SiteInfoEntity::getTenantId).distinct().collect(Collectors.toList());
            //多个运营商id
            List<String> operatorIdList = siteEntityList.stream().map(SiteInfoEntity::getOperatorId).collect(Collectors.toList());
            //多个产权方id
            List<String> propertyIdList = siteEntityList.stream().map(SiteInfoEntity::getPropertyId).collect(Collectors.toList());
            tenantIdList.addAll(operatorIdList);
            tenantIdList.addAll(propertyIdList);
            ResponseResult<List<TenantDetailsDto>> tenantDetailsByIds = systemService.findTenantDetailsByIds(tenantIdList.stream().distinct().collect(Collectors.toList()));
            if (tenantDetailsByIds.isSuccess() && CollectionUtils.isNotEmpty(tenantDetailsByIds.getData())) {
                tenantDetailsDtoMap = tenantDetailsByIds.getData().stream().collect(Collectors.toMap(TenantDetailsDto::getId, tenantDetailsDto -> tenantDetailsDto));
            }
            //查询所有站点下关联能源场景信息
            List<ScenarioTypeEntity> scenarioTypeEntities = scenarioTypeDao.findAllBySiteIdIn(siteEntityList.stream().map(SiteInfoEntity::getId).collect(Collectors.toList()));
            Map<String, List<ScenarioTypeEntity>> groupBySiteIdMap = scenarioTypeEntities.stream().collect(Collectors.groupingBy(ScenarioTypeEntity::getSiteId));
            //获取多个站点模型id以及关联场景类型模型id
            List<String> siteModelIdList = siteEntityList.stream().map(SiteInfoEntity::getSiteModelId).distinct().collect(Collectors.toList());
            List<String> modeIdList = scenarioTypeEntities.stream().map(ScenarioTypeEntity::getModelId).distinct().collect(Collectors.toList());
            siteModelIdList.addAll(modeIdList);
            //查询模型扩展属性信息
            Map<String, List<ModelReaEntity>> groupByModelIdMap = modelReaDao.findAllByModelIdIn(siteModelIdList).stream().collect(Collectors.groupingBy(ModelReaEntity::getModelId));
            Map<String, TenantDetailsDto> finalTenantDetailsDtoMap = tenantDetailsDtoMap;
            siteEntityList.forEach(siteInfoEntity -> {
                SiteInfoDto siteInfoDto = new SiteInfoDto();
                BeanUtils.copyProperties(siteInfoEntity, siteInfoDto);
                //获取租户名称
                if (finalTenantDetailsDtoMap.containsKey(siteInfoEntity.getTenantId())) {
                    siteInfoDto.setTenantName(finalTenantDetailsDtoMap.get(siteInfoEntity.getTenantId()).getTenantName());
                }
                //获取产权方名称
                if (StringUtil.isNotEmpty(siteInfoEntity.getPropertyId()) && finalTenantDetailsDtoMap.containsKey(siteInfoEntity.getPropertyId())) {
                    siteInfoDto.setPropertyName(finalTenantDetailsDtoMap.get(siteInfoEntity.getPropertyId()).getTenantName());
                }
                //获取运营商名称
                if (StringUtil.isNotEmpty(siteInfoEntity.getOperatorId()) && finalTenantDetailsDtoMap.containsKey(siteInfoEntity.getOperatorId())) {
                    siteInfoDto.setOperatorName(finalTenantDetailsDtoMap.get(siteInfoEntity.getOperatorId()).getTenantName());
                }
                //站点读写对象值
                String siteReadwriteObject = siteInfoEntity.getSiteReadwriteObject();
                if (StringUtil.isNotEmpty(siteReadwriteObject)) {
                    //获取读写字段扩展属性值
                    Map<String, Object> readwriteMap = Maps.newHashMap();
                    if (StringUtil.isNotEmpty(siteReadwriteObject)) {
                        readwriteMap = JSON.parseObject(siteReadwriteObject, new TypeReference<Map<String, Object>>() {
                        });
                    }
                    //根据模型id查询所有模型扩展属性id
                    if (!groupByModelIdMap.isEmpty() && groupByModelIdMap.containsKey(siteInfoEntity.getSiteModelId())) {
                        Map<String, ModelReaEntity> modelReaMap = groupByModelIdMap.get(siteInfoEntity.getSiteModelId()).stream().collect(Collectors.toMap(ModelReaEntity::getReaId,
                                modelReaEntity -> modelReaEntity, (k1, k2) -> k2));
                        Map<String, Object> finalReadwriteMap = readwriteMap;
                        siteInfoDto.setSiteReaList(reaDao.findAllById(modelReaMap.keySet()).stream().map(rea -> {
                            DeviceReaDto deviceReaDto = new DeviceReaDto();
                            BeanUtils.copyProperties(rea, deviceReaDto);
                            deviceReaDto.setReaId(rea.getId());
                            //设备实时值
                            deviceReaDto.setValue(finalReadwriteMap.get(rea.getFieldName()));
                            return deviceReaDto;
                        }).collect(Collectors.toList()));
                    }
                }
                //获取站点下关联能源场景列表
                if (groupBySiteIdMap.containsKey(siteInfoEntity.getId())) {
                    //存储能源场景信息
                    List<SiteScenarioTypeDto> siteScenarioTypeDtos = org.apache.commons.compress.utils.Lists.newArrayList();
                    groupBySiteIdMap.get(siteInfoEntity.getId()).forEach(scenarioTypeEntity -> {
                        SiteScenarioTypeDto siteScenarioTypeDto = new SiteScenarioTypeDto();
                        BeanUtils.copyProperties(scenarioTypeEntity, siteScenarioTypeDto);
                        //读写对象值
                        String readwriteObject = scenarioTypeEntity.getReadwriteObject();
                        if (StringUtil.isNotEmpty(readwriteObject)) {
                            //获取读写字段扩展属性值
                            Map<String, Object> readwriteMap = Maps.newHashMap();
                            if (StringUtil.isNotEmpty(readwriteObject)) {
                                readwriteMap = JSON.parseObject(readwriteObject, new TypeReference<Map<String, Object>>() {
                                });
                            }
                            if (groupByModelIdMap.containsKey(scenarioTypeEntity.getModelId())) {
                                //获取模型扩展属性id
                                Map<String, ModelReaEntity> modelReaMap = groupByModelIdMap.get(scenarioTypeEntity.getModelId()).stream().collect(Collectors.toMap(ModelReaEntity::getReaId,
                                        modelReaEntity -> modelReaEntity, (k1, k2) -> k2));
                                Map<String, Object> finalReadwriteMap = readwriteMap;
                                siteScenarioTypeDto.setReaList(reaDao.findAllById(modelReaMap.keySet()).stream().map(rea -> {
                                    DeviceReaDto deviceReaDto = new DeviceReaDto();
                                    BeanUtils.copyProperties(rea, deviceReaDto);
                                    deviceReaDto.setReaId(rea.getId());
                                    //设备实时值
                                    deviceReaDto.setValue(finalReadwriteMap.get(rea.getFieldName()));
                                    return deviceReaDto;
                                }).collect(Collectors.toList()));
                            }
                        }
                        siteScenarioTypeDtos.add(siteScenarioTypeDto);
                    });
                    siteInfoDto.setSiteScenarioTypeDtos(siteScenarioTypeDtos);
                }
                resultMap.put(siteInfoEntity.getId(), siteInfoDto);
            });
        }
        return ResponseResult.ok(resultMap);
    }

    @Override
    public ResponseResult<Map<String, SiteSetUpDto>> findSiteSetUpBySiteIds(List<String> siteIdList) {
        Map<String, SiteSetUpDto> resultMap = Maps.newHashMap();

        List<SiteSetUpEntity> siteSetUpEntities = siteSetUpDao.findAllBySiteIdIn(siteIdList);
        if (CollectionUtils.isNotEmpty(siteSetUpEntities)) {
            resultMap = siteSetUpEntities.stream().collect(Collectors.toMap(SiteSetUpEntity::getSiteId, siteSetUpEntity -> {
                SiteSetUpDto siteSetUpDto = new SiteSetUpDto();
                BeanUtils.copyProperties(siteSetUpEntity, siteSetUpDto);
                return siteSetUpDto;
            }, (k1 , k2) -> k1));
        }
        return ResponseResult.ok(resultMap);
    }

    @Override
    public ResponseResult<List<SiteInfoDto>> findSiteInfoListByIds(List<String> siteIdList) {
        List<SiteInfoDto> resultList = Lists.newArrayList();
        //根据多个站点id，查询站点信息列表
        List<SiteInfoEntity> siteEntityList = siteInfoDao.findAllByIdInAndIsDelete(siteIdList, 1);
        if (CollectionUtils.isNotEmpty(siteEntityList)) {
            resultList = siteEntityList.stream().map(siteInfoEntity -> {
                SiteInfoDto siteInfoDto = new SiteInfoDto();
                BeanUtils.copyProperties(siteInfoEntity, siteInfoDto);
                return siteInfoDto;
            }).collect(Collectors.toList());
        }
        return ResponseResult.ok(resultList);
    }
}
