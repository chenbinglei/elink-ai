package com.sunmax.device.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.TypeReference;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.sunmax.common.dto.crontab.ConfigurSiteListDto;
import com.sunmax.common.dto.device.*;
import com.sunmax.common.dto.system.OrganEmpowerListDto;
import com.sunmax.common.dto.system.TenantDetailsDto;
import com.sunmax.common.dto.system.UserDto;
import com.sunmax.common.entity.BaseEntity;
import com.sunmax.common.util.DateUtil;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.util.StringUtil;
import com.sunmax.common.util.oss.FileUtil;
import com.sunmax.common.vo.SiteFieldParamVo;
import com.sunmax.common.vo.crontab.SiteListQueryVo;
import com.sunmax.device.dao.access.DeviceDao;
import com.sunmax.device.dao.access.DeviceGunDao;
import com.sunmax.device.dao.access.SiteInfoDao;
import com.sunmax.device.dao.access.SiteSetUpDao;
import com.sunmax.device.dao.model.*;
import com.sunmax.device.entity.access.DeviceEntity;
import com.sunmax.device.entity.access.DeviceGunEntity;
import com.sunmax.device.entity.access.SiteInfoEntity;
import com.sunmax.device.entity.access.SiteSetUpEntity;
import com.sunmax.device.entity.model.*;
import com.sunmax.device.service.CrontabFeignService;
import com.sunmax.device.service.feign.SystemService;
import com.sunmax.device.util.AssetTypeUtil;
import com.sunmax.device.util.DeviceCommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

import static com.sunmax.common.util.StringUtil.getDeviceAssetType;

@Slf4j
@Service
public class CrontabFeignServiceImpl implements CrontabFeignService {

    @Autowired
    private DeviceDao deviceDao;

    @Autowired
    private ModelDao modelDao;

    @Autowired
    private SystemService systemService;

    @Autowired
    private SiteSetUpDao siteSetUpDao;

    @Autowired
    private AssetTypeDao assetTypeDao;

    @Autowired
    private FunctionDao functionDao;

    @Autowired
    private ModelReaDao modelReaDao;

    @Autowired
    private ReaDao reaDao;

    @Autowired
    private SiteInfoDao siteInfoDao;

    @Autowired
    private DeviceGunDao deviceGunDao;

    /**
     * 根据多个设备id查询设备基本信息
     *
     * @param deviceIdList
     * @return
     */
    @Override
    public ResponseResult<Map<String, DeviceBasicInfoDto>> findDeviceBasicInfoByIds(List<String> deviceIdList) {
        //返回对象
        Map<String, DeviceBasicInfoDto> resultMap = Maps.newHashMap();

        List<DeviceEntity> deviceEntityList = deviceDao.findAllById(deviceIdList);
        if (CollectionUtils.isNotEmpty(deviceEntityList)) {
            //根据多个模型id查询扩展属性数据
            List<String> modelIds = deviceEntityList.stream().map(DeviceEntity::getModelId).distinct().collect(Collectors.toList());
            Map<String, ModelEntity> modelEntityMap = modelDao.findAllById(modelIds).stream().collect(Collectors.toMap(ModelEntity::getId, ModelEntity -> ModelEntity, (k1, k2) -> k1));
            Map<String, List<ModelReaEntity>> modelReaMap = modelReaDao.findAllByModelIdIn(modelIds).stream().filter(r -> StringUtil.isNotEmpty(r.getReaId()))
                    .collect(Collectors.groupingBy(ModelReaEntity::getModelId));
            Map<String, ReaEntity> reaEntityMap = reaDao.findAllById(modelReaMap.values().stream().flatMap(Collection::stream).map(ModelReaEntity::getReaId).filter(StringUtil::isNotEmpty)
                    .collect(Collectors.toSet())).stream().collect(Collectors.toMap(ReaEntity::getId, a -> a, (k1, k2) -> k1));

            //获取创建人以及修改人id，并查询用户名称
            List<String> userIdList = deviceEntityList.stream().map(DeviceEntity::getCreateId).filter(StringUtil::isNotEmpty).collect(Collectors.toList());
            userIdList.addAll(deviceEntityList.stream().map(DeviceEntity::getUpdateId).filter(StringUtil::isNotEmpty).collect(Collectors.toList()));
            Map<String, UserDto> userMap = systemService.findUserInfoByIdsFeign(userIdList.stream().distinct().collect(Collectors.toList())).getData();
            //根据多个站点id查询站点信息
            List<String> siteIdList = deviceEntityList.stream().map(DeviceEntity::getSiteId).distinct().collect(Collectors.toList());
            Map<String, SiteInfoEntity> siteEntityMap = siteInfoDao.findAllById(siteIdList).stream().collect(Collectors.toMap(SiteInfoEntity::getId, SiteEntity -> SiteEntity, (k1, k2) -> k1));

            Map<String, String> deviceTypeMap = AssetTypeUtil.getTypeNameMap(deviceEntityList.stream().map(DeviceEntity::getTypeId)
                    .collect(Collectors.toSet()), assetTypeDao);

            deviceEntityList.forEach(deviceEntity -> {
                DeviceBasicInfoDto deviceBasicInfoDto = new DeviceBasicInfoDto();
                BeanUtils.copyProperties(deviceEntity, deviceBasicInfoDto);
                //根据设备类型id获取设备类型名称
                if (deviceTypeMap.containsKey(deviceEntity.getTypeId())) {
                    deviceBasicInfoDto.setTypeName(deviceTypeMap.get(deviceEntity.getTypeId()));
                }
                deviceBasicInfoDto.setTxStatus(DeviceCommonUtil.getDeviceTxStatus(deviceEntity.getId(), deviceEntity.getDeviceNumber()));
                //获取模型信息
                ModelEntity modelEntity = modelEntityMap.get(deviceEntity.getModelId());
                if (StringUtil.isNotEmpty(modelEntity)) {
                    deviceBasicInfoDto.setModelName(modelEntity.getModelName());
                    deviceBasicInfoDto.setModelDesc(modelEntity.getModelDesc());
                    deviceBasicInfoDto.setLogoPath(modelEntity.getLogoPath());

                    if (modelReaMap.containsKey(deviceEntity.getModelId())) {
                        //获取模型下的扩展数据
                        Map<String, Object> reaMap = Maps.newConcurrentMap();
                        //获取读写map数据
                        JSONObject readwriteObject = new JSONObject();
                        if (StringUtil.isNotEmpty(deviceEntity.getReadwriteObject())) {
                            readwriteObject = JSONObject.parseObject(deviceEntity.getReadwriteObject());
                        }
                        for (ModelReaEntity modelRea : modelReaMap.get(deviceEntity.getModelId())) {
                            ReaEntity reaEntity = reaEntityMap.get(modelRea.getReaId());
                            if (StringUtil.isNotEmpty(reaEntity)) {
                                if (readwriteObject.containsKey(reaEntity.getFieldName()) && StringUtil.isNotEmpty(readwriteObject.get(reaEntity.getFieldName()))) {
                                    reaMap.put(reaEntity.getFieldName(), readwriteObject.get(reaEntity.getFieldName()));
                                } else {
                                    reaMap.put(reaEntity.getFieldName(), modelRea.getDefaultValue() == null ? "" : modelRea.getDefaultValue());
                                }
                            }
                        }
                        deviceBasicInfoDto.setReaMap(reaMap);
                    }
                }
                if (userMap.containsKey(deviceEntity.getCreateId())) {
                    deviceBasicInfoDto.setCreateName(userMap.get(deviceEntity.getCreateId()).getFullName());
                }
                if (userMap.containsKey(deviceEntity.getUpdateId())) {
                    deviceBasicInfoDto.setUpdateName(userMap.get(deviceEntity.getUpdateId()).getFullName());
                }
                //根据站点id查询站点名称和运营商名称
                SiteInfoEntity siteEntity = siteEntityMap.get(deviceEntity.getSiteId());
                if (StringUtil.isNotEmpty(siteEntity)) {
                    deviceBasicInfoDto.setSiteName(siteEntity.getSiteName());
                }
                resultMap.put(deviceEntity.getId(), deviceBasicInfoDto);
            });
        }
        return ResponseResult.ok(resultMap);
    }

    /**
     * 根据多个站点id查询设备列表数据
     *
     * @param siteIdList
     * @param deviceType
     * @return
     */
    @Override
    public ResponseResult<Map<String, List<DeviceBasicInfoDto>>> findDeviceBasicInfoBySiteIds(List<String> siteIdList, Integer deviceType) {
        //返回数据
        Map<String, List<DeviceBasicInfoDto>> resultMap = Maps.newHashMap();

        //根据多个站点id查询设备数据
        List<DeviceEntity> deviceEntityList = deviceDao.findAllBySiteIdInAndIsDelete(siteIdList, 1);
        if (CollectionUtils.isNotEmpty(deviceEntityList)) {

            //根据多个模型id查询扩展属性数据
            List<String> modelIds = deviceEntityList.stream().map(DeviceEntity::getModelId).distinct().collect(Collectors.toList());
            Map<String, ModelEntity> modelMap = modelDao.findAllById(modelIds).stream().collect(Collectors.toMap(BaseEntity::getId, a -> a, (k1, k2) -> k1));
            Map<String, List<ModelReaEntity>> modelReaMap = modelReaDao.findAllByModelIdIn(modelIds).stream().filter(r -> StringUtil.isNotEmpty(r.getReaId()))
                    .collect(Collectors.groupingBy(ModelReaEntity::getModelId));
            Map<String, ReaEntity> reaEntityMap = reaDao.findAllById(modelReaMap.values().stream().flatMap(Collection::stream).map(ModelReaEntity::getReaId).filter(StringUtil::isNotEmpty)
                    .collect(Collectors.toSet())).stream().collect(Collectors.toMap(ReaEntity::getId, a -> a, (k1, k2) -> k1));

            Map<String, List<DeviceEntity>> groupBySiteIdMap = deviceEntityList.stream().collect(Collectors.groupingBy(DeviceEntity::getSiteId));
            //根据多个站点id查询站点基本信息
            List<SiteInfoEntity> siteEntityList = siteInfoDao.findAllByIdInAndIsDelete(siteIdList, 1);
            Map<String, SiteInfoEntity> siteEntityMap = Maps.newHashMap();
            Map<String, TenantDetailsDto> tenantDetailsDtoMap = Maps.newHashMap();
            if (CollectionUtils.isNotEmpty(siteEntityList)) {
                siteEntityMap = siteEntityList.stream().collect(Collectors.toMap(SiteInfoEntity::getId, siteEntity -> siteEntity, (k1, k2) -> k1));
                //获取运营商id列表
                List<String> operateIdList = siteEntityList.stream().map(SiteInfoEntity::getOperatorId).filter(StringUtil::isNotEmpty).distinct().collect(Collectors.toList());
                //获取产权方id列表
                List<String> propertyIdList = siteEntityList.stream().map(SiteInfoEntity::getPropertyId).filter(StringUtil::isNotEmpty).distinct().collect(Collectors.toList());
                if (CollectionUtils.isNotEmpty(operateIdList) || CollectionUtils.isNotEmpty(propertyIdList)) {
                    operateIdList.addAll(propertyIdList);
                    //根据多个运营商id查询运营商基本信息
                    ResponseResult<List<TenantDetailsDto>> tenantDetailsByIds = systemService.findTenantDetailsByIds(operateIdList.stream().distinct().collect(Collectors.toList()));
                    if (tenantDetailsByIds.isSuccess() && CollectionUtils.isNotEmpty(tenantDetailsByIds.getData())) {
                        List<TenantDetailsDto> tenantDetailsDtoList = tenantDetailsByIds.getData();
                        tenantDetailsDtoMap = tenantDetailsDtoList.stream().collect(Collectors.toMap(TenantDetailsDto::getId, tenantDetailsDto -> tenantDetailsDto, (k1, k2) -> k1));
                    }
                }
            }
            //根据多个类型id查询类型名称
            List<String> typeIds = groupBySiteIdMap.values().stream().flatMap(c -> c.stream().map(DeviceEntity::getTypeId)).distinct().collect(Collectors.toList());
            Map<String, String> typeNameMap = assetTypeDao.findAllById(typeIds).stream().collect(Collectors.toMap(AssetTypeEntity::getId, AssetTypeEntity::getTypeName));

            Map<String, SiteInfoEntity> finalSiteEntityMap = siteEntityMap;
            Map<String, TenantDetailsDto> finalTenantDetailsDtoMap = tenantDetailsDtoMap;
            groupBySiteIdMap.forEach((siteId, deviceEntitys) -> {
                List<DeviceBasicInfoDto> deviceBasicInfoDtos = deviceEntitys.stream().map(deviceEntity -> {
                    DeviceBasicInfoDto deviceBasicInfoDto = new DeviceBasicInfoDto();
                    BeanUtils.copyProperties(deviceEntity, deviceBasicInfoDto);
                    if (StringUtil.isNotEmpty(deviceEntity.getTypeId()) && typeNameMap.containsKey(deviceEntity.getTypeId())) {
                        deviceBasicInfoDto.setTypeName(typeNameMap.get(deviceEntity.getTypeId()));
                    }
                    //根据模型id查询模型名称,模型描述
                    if (modelMap.containsKey(deviceEntity.getModelId())) {
                        ModelEntity model = modelMap.get(deviceEntity.getModelId());
                        deviceBasicInfoDto.setModelName(model.getModelName());
                        deviceBasicInfoDto.setModelDesc(model.getModelDesc());
                        deviceBasicInfoDto.setLogoPath(model.getLogoPath());
                    }
                    deviceBasicInfoDto.setTxStatus(DeviceCommonUtil.getDeviceTxStatus(deviceEntity.getId(), deviceEntity.getDeviceNumber()));
                    if (modelReaMap.containsKey(deviceEntity.getModelId())) {
                        //获取模型下的扩展数据
                        Map<String, Object> reaMap = Maps.newConcurrentMap();
                        //获取读写map数据
                        JSONObject readwriteObject = new JSONObject();
                        if (StringUtil.isNotEmpty(deviceEntity.getReadwriteObject())) {
                            readwriteObject = JSONObject.parseObject(deviceEntity.getReadwriteObject());
                        }
                        for (ModelReaEntity modelRea : modelReaMap.get(deviceEntity.getModelId())) {
                            ReaEntity reaEntity = reaEntityMap.get(modelRea.getReaId());
                            if (StringUtil.isNotEmpty(reaEntity)) {
                                //读写类型 1-只读 2-读写
                                if (readwriteObject.containsKey(reaEntity.getFieldName()) && StringUtil.isNotEmpty(readwriteObject.get(reaEntity.getFieldName()))) {
                                    reaMap.put(reaEntity.getFieldName(), readwriteObject.get(reaEntity.getFieldName()));
                                } else {
                                    reaMap.put(reaEntity.getFieldName(), modelRea.getDefaultValue() == null ? "":modelRea.getDefaultValue());
                                }
                            }
                        }
                        deviceBasicInfoDto.setReaMap(reaMap);
                    }

                    //获取站点信息
                    SiteInfoEntity siteEntity = finalSiteEntityMap.get(siteId);
                    if (StringUtil.isNotEmpty(siteEntity)) {
                        deviceBasicInfoDto.setSiteName(siteEntity.getSiteName());
                        //获取运营商名称
                        if (StringUtil.isNotEmpty(siteEntity.getOperatorId()) && !finalTenantDetailsDtoMap.isEmpty() && finalTenantDetailsDtoMap.containsKey(siteEntity.getOperatorId())) {
                            deviceBasicInfoDto.setOperateId(siteEntity.getOperatorId());
                            deviceBasicInfoDto.setOperateName(finalTenantDetailsDtoMap.get(siteEntity.getOperatorId()).getTenantName());
                        }
                        //获取产权方名称
                        if (StringUtil.isNotEmpty(siteEntity.getPropertyId()) && !finalTenantDetailsDtoMap.isEmpty() && finalTenantDetailsDtoMap.containsKey(siteEntity.getPropertyId())) {
                            deviceBasicInfoDto.setPropertyId(siteEntity.getPropertyId());
                            deviceBasicInfoDto.setPropertyName(finalTenantDetailsDtoMap.get(siteEntity.getPropertyId()).getTenantName());
                        }
                    }
                    return deviceBasicInfoDto;
                }).collect(Collectors.toList());
                //如果传了设备类型，先根据设备类型过滤一次
                if (StringUtil.isNotEmpty(deviceType) && CollectionUtils.isNotEmpty(getDeviceAssetType(deviceType))) {
                    //获取所查询设备类型下的所有资产类型
                    List<Integer> deviceAssetType = getDeviceAssetType(deviceType);
                    deviceBasicInfoDtos = deviceBasicInfoDtos.stream().filter(d -> StringUtil.isNotEmpty(d.getTypeId()) && deviceAssetType.contains(Integer.parseInt(d.getTypeId()))).collect(Collectors.toList());
                }
                if (CollectionUtils.isNotEmpty(deviceBasicInfoDtos)) {
                    resultMap.put(siteId, deviceBasicInfoDtos);
                }
            });
        }
        return ResponseResult.ok(resultMap);
    }

    /**
     * 根据多个模型id查询模型基本信息
     *
     * @param modelIdList
     * @return
     */
    @Override
    public ResponseResult<Map<String, ModelDetailDto>> findModelDetailByIds(List<String> modelIdList) {
        Map<String, ModelDetailDto> resultMap = Maps.newHashMap();
        //根据多个模型id查询模型数据
        List<ModelEntity> modelDaoAllById = modelDao.findAllById(modelIdList);
        if (CollectionUtils.isNotEmpty(modelDaoAllById)) {
            modelDaoAllById.forEach(modelEntity -> {
                ModelDetailDto modelDetailDto = new ModelDetailDto();
                BeanUtils.copyProperties(modelEntity, modelDetailDto);
                resultMap.put(modelEntity.getId(), modelDetailDto);
            });
        }
        return ResponseResult.ok(resultMap);
    }

    /**
     * 根据多个功能点id查询功能点基本信息
     *
     * @param functionIdList
     * @return
     */
    @Override
    public ResponseResult<Map<String, FunctionDetailDto>> findFunctionDetailByIds(List<String> functionIdList) {
        Map<String, FunctionDetailDto> resultMap = Maps.newHashMap();
        //根据多个功能点id查询功能点数据
        if (CollectionUtils.isNotEmpty(functionIdList)) {
            List<FunctionEntity> functionDaoAllById = functionDao.findAllById(functionIdList);
            if (CollectionUtils.isNotEmpty(functionDaoAllById)) {
                functionDaoAllById.forEach(functionEntity -> {
                    FunctionDetailDto functionDetailDto = new FunctionDetailDto();
                    BeanUtils.copyProperties(functionEntity, functionDetailDto);
                    resultMap.put(functionEntity.getId(), functionDetailDto);
                });
            }
        }
        return ResponseResult.ok(resultMap);
    }

    /**
     * 根据多个模型id查询模型功能点列表
     *
     * @param modelIdList
     * @return
     */
    @Override
    public ResponseResult<Map<String, List<ModelFunctionListDto>>> findModelFunctionListByModelIds
    (List<String> modelIdList) {
        return ResponseResult.ok(DeviceCommonUtil.getFunctionListByModeIds(new HashSet<>(modelIdList)));
    }

    /**
     * 根据设备id查询设备通讯状态
     * @param deleveIdMap 设备id -> 设备标识
     * @return
     */
    @Override
    public ResponseResult<Map<String, Integer>> findDeviceTxStatusById(Map<String, String> deleveIdMap) {
        Map<String, Integer> resultMap = Maps.newHashMap();
        //循环设备id集合
        deleveIdMap.forEach((deviceId, deviceNumber) -> {
            Integer deviceTxStatus = DeviceCommonUtil.getDeviceTxStatus(deviceId, deviceNumber);
            resultMap.put(deviceId, deviceTxStatus);
        });
        return ResponseResult.ok(resultMap);
    }

    /**
     * 查询设备通讯状态
     * @param deleveMap 设备id -> 设备信息
     * @return
     */
    public ResponseResult<Map<String, Integer>> findDeviceTxStatus(Map<String, DeviceEntity> deleveMap) {
        Map<String, Integer> resultMap = Maps.newHashMap();
        //循环设备id集合
        deleveMap.forEach((deviceId, deviceEntity) -> {
            Integer deviceTxStatus = DeviceCommonUtil.getDeviceTxStatus(deviceId, deviceEntity.getDeviceNumber());
            resultMap.put(deviceId, deviceTxStatus);
        });
        return ResponseResult.ok(resultMap);
    }

    /**
     * 根据用户id查询站点列表数据
     * @param siteListQueryVo
     * @return
     */
    @Override
    public ResponseResult<List<ConfigurSiteListDto>> findSiteListByUserId(SiteListQueryVo siteListQueryVo) {
        List<ConfigurSiteListDto> resultList = Lists.newArrayList();
        //当前用户如果是平台管理员则查询全部站点数据
        String userId = siteListQueryVo.getUserId();
        UserDto loginUserDto = systemService.findUserInfoByIdsFeign(Collections.singletonList(userId)).getData().get(userId);
        if (StringUtil.isNotEmpty(loginUserDto)) {
            List<SiteInfoEntity> siteEntityList = findSitesForUser(loginUserDto.getUserRole(), userId);
            if (CollectionUtils.isNotEmpty(siteEntityList)) {
                //根据站点名称查询
                if (StringUtil.isNotEmpty(siteListQueryVo.getSiteName())) {
                    siteEntityList = siteEntityList.stream().filter(siteInfoEntity -> siteInfoEntity.getSiteName().contains(siteListQueryVo.getSiteName())).collect(Collectors.toList());
                }
                //根据能源场景类型查询
                if (StringUtil.isNotEmpty(siteListQueryVo.getScenarioTypes())) {
                    List<Integer> scenarioTypeList = Arrays.stream(siteListQueryVo.getScenarioTypes().split(FileUtil.COMMA)).map(s -> Integer.parseInt(s.trim())).collect(Collectors.toList());
                    siteEntityList = siteEntityList.stream().filter(s -> new HashSet<>(Arrays.stream(s.getScenarioTypes().split(FileUtil.COMMA)).filter(StringUtil::isNotEmpty)
                            .map(n -> Integer.parseInt(n.trim())).collect(Collectors.toList())).containsAll(scenarioTypeList)).collect(Collectors.toList());
                }
                //根据区域查询
                if (StringUtil.isNotEmpty(siteListQueryVo.getAreaType()) && StringUtil.isNotEmpty(siteListQueryVo.getAreaValue())) {
                    siteEntityList = siteEntityList.stream()
                            .filter(siteInfoDto -> {
                                Map<String, Object> readwriteMap = Maps.newHashMap();
                                String siteReadwriteObject = siteInfoDto.getSiteReadwriteObject();
                                if (StringUtil.isNotEmpty(siteReadwriteObject)) {
                                    Map<String, Object> parseObjectMap = JSON.parseObject(siteReadwriteObject, new TypeReference<Map<String, Object>>() {});
                                    if (parseObjectMap.containsKey(SiteFieldParamVo.LOCATION)) {
                                        readwriteMap = JSON.parseObject(JSON.toJSONString(parseObjectMap.get(SiteFieldParamVo.LOCATION)), new TypeReference<Map<String, Object>>() {});
                                    }
                                }
                                return matchesAreaType(readwriteMap, siteListQueryVo);
                            })
                            .collect(Collectors.toList());

                }
                if (CollectionUtils.isNotEmpty(siteEntityList)) {
                    //根据多个站点id，查询站点下所有设备数据
                    Map<String, Integer> deviceTxStatusMap = Maps.newHashMap();
                    Map<String, List<DeviceEntity>> groupBySiteIdMap = Maps.newHashMap();
                    List<DeviceEntity> deviceEntities = deviceDao.findAllBySiteIdInAndIsDelete(siteEntityList.stream().map(SiteInfoEntity::getId).collect(Collectors.toList()), 1);
                    if (CollectionUtils.isNotEmpty(deviceEntities)) {
                        groupBySiteIdMap = deviceEntities.stream().collect(Collectors.groupingBy(DeviceEntity::getSiteId));
                        //查询所有设备通讯状态
                        deviceTxStatusMap = findDeviceTxStatus(deviceEntities.stream().collect(Collectors.toMap(DeviceEntity::getId, deviceEntity -> deviceEntity))).getData();
                    }
                    //根据多个站点id查询站点设置信息
                    List<SiteSetUpEntity> siteSetUpEntityList = siteSetUpDao.findAllBySiteIdIn(siteEntityList.stream().map(SiteInfoEntity::getId).collect(Collectors.toList()));
                    Map<String, SiteSetUpEntity> siteSetUpEntityMap = CollectionUtils.isEmpty(siteSetUpEntityList)
                            ? Collections.emptyMap()
                            : siteSetUpEntityList.stream()
                            .collect(Collectors.toMap(SiteSetUpEntity::getSiteId, siteSetUpEntity -> siteSetUpEntity, (k1, k2) -> k1));
                    Map<String, List<DeviceEntity>> finalGroupBySiteIdMap = groupBySiteIdMap;
                    Map<String, Integer> finalDeviceTxStatusMap = deviceTxStatusMap;
                    resultList = siteEntityList.stream().map(siteInfoEntity -> {
                        ConfigurSiteListDto configurSiteListDto = new ConfigurSiteListDto();
                        BeanUtils.copyProperties(siteInfoEntity, configurSiteListDto);
                        //获取站点扩展属性信息
                        String siteReadwriteObject = siteInfoEntity.getSiteReadwriteObject();
                        if (StringUtil.isNotEmpty(siteReadwriteObject)) {
                            Map<String, Object> parseObjectMap = JSON.parseObject(siteReadwriteObject, new TypeReference<Map<String, Object>>() {});
                            //获取站点位置信息
                            if (parseObjectMap.containsKey(SiteFieldParamVo.LOCATION)) {
                                String location = JSON.toJSONString(parseObjectMap.get(SiteFieldParamVo.LOCATION));
                                configurSiteListDto.setLocation(location);
                            }
                            //获取投运时间
                            if (parseObjectMap.containsKey(SiteFieldParamVo.OFFICIAL_RUN_TIME) && StringUtil.isNotEmpty(parseObjectMap.get(SiteFieldParamVo.OFFICIAL_RUN_TIME))) {
                                LocalDate officialRunTime = DateUtil.strToLocalDate(String.valueOf(parseObjectMap.get(SiteFieldParamVo.OFFICIAL_RUN_TIME)));
                                long runDays = ChronoUnit.DAYS.between(officialRunTime, LocalDate.now());
                                configurSiteListDto.setRunDays(runDays >= 0 ? runDays : 0);
                            } else if (StringUtil.isNotEmpty(siteInfoEntity.getCreateTime())){
                                LocalDate createDate = siteInfoEntity.getCreateTime().toLocalDate();
                                long runDays = ChronoUnit.DAYS.between(createDate, LocalDate.now());
                                configurSiteListDto.setRunDays(runDays >= 0 ? runDays : 0);
                            }
                        }
                        // 电站状态
                        String siteStates = "1";

                        // 获取站点下所有设备数据
                        if (!finalGroupBySiteIdMap.isEmpty() && finalGroupBySiteIdMap.containsKey(siteInfoEntity.getId()) && !finalDeviceTxStatusMap.isEmpty()) {
                            List<String> deviceIdList = finalGroupBySiteIdMap.get(siteInfoEntity.getId()).stream()
                                    .map(DeviceEntity::getId)
                                    .collect(Collectors.toList());

                            Set<Integer> deviceTxStatusSet = finalDeviceTxStatusMap.entrySet().stream()
                                    .filter(entry -> deviceIdList.contains(entry.getKey()))
                                    .map(Map.Entry::getValue)
                                    .collect(Collectors.toSet());

                            boolean hasOfflineDevice = deviceTxStatusSet.contains(88);
                            boolean hasFaultDevice = deviceTxStatusSet.contains(255);

                            if (hasOfflineDevice && hasFaultDevice) {
                                siteStates = "3,4";
                            } else if (hasOfflineDevice) {
                                siteStates = "3";
                            } else if (hasFaultDevice) {
                                siteStates = "4";
                            }
                        }
                        //获取站点设置信息
                        if (!siteSetUpEntityMap.isEmpty() && siteSetUpEntityMap.containsKey(siteInfoEntity.getId())) {
                            SiteSetUpEntity siteSetUpEntity = siteSetUpEntityMap.get(siteInfoEntity.getId());
                            SiteSetUpDto siteSetUpDto = new SiteSetUpDto();
                            BeanUtils.copyProperties(siteSetUpEntity, siteSetUpDto);
                            configurSiteListDto.setSiteSetUpDto(siteSetUpDto);
                            //系统名称 为空则存站点名称的前10个字符
                            siteSetUpDto.setSystemName(siteSetUpEntity.getSystemName());
                            if (StringUtil.isEmpty(siteSetUpEntity.getSystemName()) && StringUtil.isNotEmpty(siteInfoEntity.getSiteName())) {
                                if (siteInfoEntity.getSiteName().length() > 10) {
                                    siteSetUpDto.setSystemName(siteInfoEntity.getSiteName().substring(0, 10));
                                } else {
                                    siteSetUpDto.setSystemName(siteInfoEntity.getSiteName());
                                }
                            }
                        }
                        configurSiteListDto.setSiteStates(siteStates);
                        return configurSiteListDto;
                    }).collect(Collectors.toList());
                    //根据站点状态查询
                    if (StringUtil.isNotEmpty(siteListQueryVo.getSiteState())) {
                        resultList = resultList.stream().filter(s -> StringUtil.isNotEmpty(s.getSiteStates()) && s.getSiteStates().contains(String.valueOf(siteListQueryVo.getSiteState()))).collect(Collectors.toList());
                    }
                }
            }
        }
        return ResponseResult.ok(resultList);
    }

    @Override
    public ResponseResult<List<SiteDeviceDto>> getSiteDeviceList(Set<String> siteIds) {
        //返回的集合
        List<SiteDeviceDto> resultList = Lists.newArrayList();
        List<SiteInfoEntity> siteInfoList;
        if (CollectionUtils.isNotEmpty(siteIds)) {
            siteInfoList = siteInfoDao.findAllByIdInAndIsDelete(siteIds, 1);
        } else {
            siteInfoList = siteInfoDao.findAllByIsDelete(1);
        }
        if (CollectionUtils.isNotEmpty(siteInfoList)) {
            //根据多个站点id查询站点下面的设备数据
            siteIds = siteInfoList.stream().map(BaseEntity::getId).collect(Collectors.toSet());
            Map<String, List<DeviceEntity>> deviceMap = deviceDao.findAllBySiteIdInAndIsDelete(siteIds, 1)
                    .stream().collect(Collectors.groupingBy(DeviceEntity::getSiteId));
            //根据多个设备id查询设备枪数据
            List<String> deviceIds = deviceMap.entrySet().stream().flatMap(d -> d.getValue().stream())
                    .map(BaseEntity::getId).collect(Collectors.toList());
            Map<String, List<DeviceGunEntity>> deviceGunMap = deviceGunDao.findAllByDeviceIdIn(deviceIds).stream()
                    .collect(Collectors.groupingBy(DeviceGunEntity::getDeviceId));
            //对数据组装
            resultList = siteInfoList.stream().map(site -> {
                SiteDeviceDto result = new SiteDeviceDto();
                BeanUtils.copyProperties(site, result);
                //获取设备以及设备枪的数据
                if (deviceMap.containsKey(site.getId())) {
                    result.setDeviceList(deviceMap.get(site.getId()).stream().map(device -> {
                        SiteDeviceDto.Device deviceDto = new SiteDeviceDto.Device();
                        BeanUtils.copyProperties(device, deviceDto);
                        if (StringUtil.isNotEmpty(device.getReadwriteObject()) && device.getReadwriteObject().contains("power")) {
                            deviceDto.setRatedPower(JSON.parseObject(device.getReadwriteObject()).getDouble("power"));
                        }
                        if (deviceGunMap.containsKey(device.getId())) {
                            deviceDto.setDeviceGunList(deviceGunMap.get(device.getId()).stream().map(deviceGun -> {
                                SiteDeviceDto.DeviceGun deviceGunDto = new SiteDeviceDto.DeviceGun();
                                BeanUtils.copyProperties(deviceGun, deviceGunDto);
                                return deviceGunDto;
                            }).collect(Collectors.toList()));
                        }
                        return deviceDto;
                    }).collect(Collectors.toList()));
                }
                return result;
            }).collect(Collectors.toList());
        }
        return ResponseResult.ok(resultList);
    }

    @Override
    public ResponseResult<Map<String, FunctionDetailDto>> findFunctionDetailByLogos(Set<String> functionLogos) {
        Map<String, FunctionDetailDto> resultMap = Maps.newHashMap();
        //根据多个功能点标识查询功能点数据
        if (CollectionUtils.isNotEmpty(functionLogos)) {
            List<FunctionEntity> functionList = functionDao.findAllByFunctionLogoIn(functionLogos);
            if (CollectionUtils.isNotEmpty(functionList)) {
                resultMap = functionList.stream().map(function -> {
                    FunctionDetailDto result = new FunctionDetailDto();
                    BeanUtils.copyProperties(function, result);
                    return result;
                }).collect(Collectors.toMap(FunctionDetailDto::getFunctionLogo, a -> a, (k1,k2) -> k1));
            }
        }
        return ResponseResult.ok(resultMap);
    }

    /**
     * 根据用户角色查询站点列表
     * @param userRole
     * @param userId
     * @return
     */
    private List<SiteInfoEntity> findSitesForUser(Integer userRole, String userId) {
        if (userRole.equals(0)) {
            return siteInfoDao.findAllByIsDelete(1);
        } else {
            ResponseResult<List<OrganEmpowerListDto>> response = systemService.findAllOrganEmpowerByUserId(userId);
            if (response.isSuccess()) {
                List<OrganEmpowerListDto> organEmpowerList = response.getData();
                if (!organEmpowerList.isEmpty()) {
                    List<String> siteIds = organEmpowerList.stream()
                            .map(OrganEmpowerListDto::getSiteId)
                            .distinct()
                            .collect(Collectors.toList());
                    return siteInfoDao.findAllByIdInAndIsDelete(siteIds, 1);
                }
            }
            return Collections.emptyList();
        }
    }

    boolean matchesAreaType(Map<String, Object> readwriteMap, SiteListQueryVo siteListQueryVo) {
        String areaKey = null;
        switch (siteListQueryVo.getAreaType()) {
            case 1:
                areaKey = SiteFieldParamVo.PROVINCE;
                break;
            case 2:
                areaKey = SiteFieldParamVo.CITY;
                break;
            case 3:
                areaKey = SiteFieldParamVo.COUNTY;
                break;
            default:
                break;
        }
        Object areaValue = readwriteMap.get(areaKey);

        return areaValue != null && areaValue.equals(siteListQueryVo.getAreaValue());
    }


    /**
     * 根据多个父节点id查询下级设备列表数据
     * @param parentIdList
     * @return
     */
    @Override
    public ResponseResult<Map<String, List<DeviceBasicInfoDto>>> findDeviceInfoByParentIds(List<String> parentIdList) {
        //返回数据
        Map<String, List<DeviceBasicInfoDto>> resultMap = Maps.newHashMap();

        //根据多个父级id查询下面所有子设备数据
        List<DeviceEntity> deviceList = deviceDao.findAll(Example.of(DeviceEntity.builder().isDelete(1).build()));

        //对数据进行组装
        Map<String, List<DeviceEntity>> parentDeviceMap = parentIdList.stream().collect(Collectors.toMap(p -> p,
                p -> getChildren(p, deviceList), (k1, k2) -> k1));
        List<DeviceEntity> deviceEntityList = parentDeviceMap.values().stream().flatMap(List::stream).distinct().collect(Collectors.toList());

        if (CollectionUtils.isNotEmpty(deviceEntityList)) {

            //根据多个模型id查询扩展属性数据
            List<String> modelIds = deviceEntityList.stream().map(DeviceEntity::getModelId).distinct().collect(Collectors.toList());
            Map<String, List<ModelReaEntity>> modelReaMap = modelReaDao.findAllByModelIdIn(modelIds).stream().filter(r -> StringUtil.isNotEmpty(r.getReaId()))
                    .collect(Collectors.groupingBy(ModelReaEntity::getModelId));
            Map<String, ReaEntity> reaEntityMap = reaDao.findAllById(modelReaMap.values().stream().flatMap(Collection::stream).map(ModelReaEntity::getReaId).filter(StringUtil::isNotEmpty)
                    .collect(Collectors.toSet())).stream().collect(Collectors.toMap(ReaEntity::getId, a -> a, (k1, k2) -> k1));

            //根据多个站点id查询站点基本信息
            List<SiteInfoEntity> siteEntityList = siteInfoDao.findAllByIdInAndIsDelete(deviceEntityList.stream().map(DeviceEntity::getSiteId).collect(Collectors.toSet()), 1);
            Map<String, SiteInfoEntity> siteEntityMap = Maps.newHashMap();
            Map<String, TenantDetailsDto> tenantDetailsDtoMap = Maps.newHashMap();
            if (CollectionUtils.isNotEmpty(siteEntityList)) {
                siteEntityMap = siteEntityList.stream().collect(Collectors.toMap(SiteInfoEntity::getId, siteEntity -> siteEntity, (k1, k2) -> k1));
                //获取运营商id列表
                List<String> operateIdList = siteEntityList.stream().map(SiteInfoEntity::getOperatorId).filter(StringUtil::isNotEmpty).distinct().collect(Collectors.toList());
                //获取产权方id列表
                List<String> propertyIdList = siteEntityList.stream().map(SiteInfoEntity::getPropertyId).filter(StringUtil::isNotEmpty).distinct().collect(Collectors.toList());
                if (CollectionUtils.isNotEmpty(operateIdList) || CollectionUtils.isNotEmpty(propertyIdList)) {
                    operateIdList.addAll(propertyIdList);
                    //根据多个运营商id查询运营商基本信息
                    ResponseResult<List<TenantDetailsDto>> tenantDetailsByIds = systemService.findTenantDetailsByIds(operateIdList.stream().distinct().collect(Collectors.toList()));
                    if (tenantDetailsByIds.isSuccess() && CollectionUtils.isNotEmpty(tenantDetailsByIds.getData())) {
                        List<TenantDetailsDto> tenantDetailsDtoList = tenantDetailsByIds.getData();
                        tenantDetailsDtoMap = tenantDetailsDtoList.stream().collect(Collectors.toMap(TenantDetailsDto::getId, tenantDetailsDto -> tenantDetailsDto, (k1, k2) -> k1));
                    }
                }
            }
            Map<String, SiteInfoEntity> finalSiteEntityMap = siteEntityMap;
            Map<String, TenantDetailsDto> finalTenantDetailsDtoMap = tenantDetailsDtoMap;
            parentDeviceMap.forEach((parentId, deviceEntitys) -> {
                List<DeviceBasicInfoDto> deviceBasicInfoDtos = deviceEntitys.stream().map(deviceEntity -> {
                    DeviceBasicInfoDto deviceBasicInfoDto = new DeviceBasicInfoDto();
                    BeanUtils.copyProperties(deviceEntity, deviceBasicInfoDto);
                    Integer txStatus = DeviceCommonUtil.getDeviceTxStatus(deviceEntity.getId(), deviceEntity.getDeviceNumber());
                    if (StringUtil.isNotEmpty(txStatus)) {
                        deviceBasicInfoDto.setTxStatus(txStatus);
                    }
                    if (modelReaMap.containsKey(deviceEntity.getModelId())) {
                        //获取模型下的扩展数据
                        Map<String, Object> reaMap = Maps.newConcurrentMap();
                        //获取读写map数据
                        JSONObject readwriteObject = new JSONObject();
                        if (StringUtil.isNotEmpty(deviceEntity.getReadwriteObject())) {
                            readwriteObject = JSONObject.parseObject(deviceEntity.getReadwriteObject());
                        }
                        for (ModelReaEntity modelRea : modelReaMap.get(deviceEntity.getModelId())) {
                            ReaEntity reaEntity = reaEntityMap.get(modelRea.getReaId());
                            if (StringUtil.isNotEmpty(reaEntity)) {
                                //读写类型 1-只读 2-读写
                                if (readwriteObject.containsKey(reaEntity.getFieldName()) && StringUtil.isNotEmpty(readwriteObject.get(reaEntity.getFieldName()))) {
                                    reaMap.put(reaEntity.getFieldName(), readwriteObject.get(reaEntity.getFieldName()));
                                } else {
                                    reaMap.put(reaEntity.getFieldName(), modelRea.getDefaultValue() == null ? "":modelRea.getDefaultValue());
                                }
                            }
                        }
                        deviceBasicInfoDto.setReaMap(reaMap);
                    }

                    //获取站点信息
                    SiteInfoEntity siteEntity = finalSiteEntityMap.get(deviceEntity.getSiteId());
                    if (StringUtil.isNotEmpty(siteEntity)) {
                        deviceBasicInfoDto.setSiteName(siteEntity.getSiteName());
                        //获取运营商名称
                        if (StringUtil.isNotEmpty(siteEntity.getOperatorId()) && !finalTenantDetailsDtoMap.isEmpty() && finalTenantDetailsDtoMap.containsKey(siteEntity.getOperatorId())) {
                            deviceBasicInfoDto.setOperateId(siteEntity.getOperatorId());
                            deviceBasicInfoDto.setOperateName(finalTenantDetailsDtoMap.get(siteEntity.getOperatorId()).getTenantName());
                        }
                        //获取产权方名称
                        if (StringUtil.isNotEmpty(siteEntity.getPropertyId()) && !finalTenantDetailsDtoMap.isEmpty() && finalTenantDetailsDtoMap.containsKey(siteEntity.getPropertyId())) {
                            deviceBasicInfoDto.setPropertyId(siteEntity.getPropertyId());
                            deviceBasicInfoDto.setPropertyName(finalTenantDetailsDtoMap.get(siteEntity.getPropertyId()).getTenantName());
                        }
                    }
                    return deviceBasicInfoDto;
                }).collect(Collectors.toList());
                if (CollectionUtils.isNotEmpty(deviceBasicInfoDtos)) {
                    resultMap.put(parentId, deviceBasicInfoDtos);
                }
            });
        }
        return ResponseResult.ok(resultMap);
    }

    private static List<DeviceEntity> getChildren(String id, List<DeviceEntity> childrenList) {
        List<DeviceEntity> result = Lists.newArrayList();
        for (DeviceEntity menu : childrenList) {
            if (id.equals(menu.getParentId())) {
                result.add(menu);
                result.addAll(getChildren(menu.getId(), childrenList));
            }
        }
        return result;
    }

}
