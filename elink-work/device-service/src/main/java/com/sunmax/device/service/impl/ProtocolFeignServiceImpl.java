package com.sunmax.device.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.sunmax.common.dto.device.*;
import com.sunmax.common.dto.system.UserDto;
import com.sunmax.common.entity.BaseEntity;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.util.StringUtil;
import com.sunmax.common.vo.protocol.PileSetQrVo;
import com.sunmax.device.constants.DBConstants;
import com.sunmax.device.dao.access.*;
import com.sunmax.device.dao.model.ModelDao;
import com.sunmax.device.dao.model.PileFaultDao;
import com.sunmax.device.entity.access.*;
import com.sunmax.device.entity.model.ModelEntity;
import com.sunmax.device.service.ProtocolFeignService;
import com.sunmax.device.service.feign.SystemService;
import com.sunmax.device.util.DeviceCommonUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ProtocolFeignServiceImpl implements ProtocolFeignService {

    @Autowired
    private DeviceDao deviceDao;

    @Autowired
    private PileFaultDao pileFaultDao;

    @Autowired
    private PointTableDao pointTableDao;

    @Autowired
    private SiteInfoDao siteInfoDao;

    @Autowired
    private ModelDao modelDao;

    @Autowired
    private SystemService systemService;

    @Autowired
    private DeviceGunDao deviceGunDao;

    @Override
    public ResponseResult<Set<String>> getDeviceNumberList(Integer accessType) {
        List<DeviceEntity> deviceList;
        if (StringUtil.isNotEmpty(accessType)) {
            deviceList = deviceDao.findAllByAccessTypeAndIsDelete(accessType, 1);
        } else {
            deviceList = deviceDao.findAll(Example.of(DeviceEntity.builder().isDelete(1).build()));
        }
        return ResponseResult.ok(deviceList.stream().map(DeviceEntity::getDeviceNumber).filter(StringUtil::isNotEmpty).collect(Collectors.toSet()));
    }

    @Override
    public ResponseResult<Map<Integer, PileFaultDto>> findPileFaultList(String deviceCode, Set<Integer> faultCodes) {
        //返回的对象
        Map<Integer, PileFaultDto> resultMap = Maps.newHashMap();
        //根据设备编号和多个故障码查询故障数据
        if (StringUtil.isNotEmpty(deviceCode) && CollectionUtils.isNotEmpty(faultCodes)) {
            List<DeviceEntity> deviceList = deviceDao.findAllByDeviceNumberAndIsDelete(deviceCode, 1);
            if (CollectionUtils.isNotEmpty(deviceList)) {
                resultMap = pileFaultDao.findAllByModelIdAndFaultCodeIn(deviceList.get(0).getModelId(), faultCodes).stream().map(pileFault -> {
                    PileFaultDto result = new PileFaultDto();
                    BeanUtils.copyProperties(pileFault, result);
                    return result;
                }).collect(Collectors.toMap(PileFaultDto::getFaultCode, a -> a, (k1, k2) -> k1));
            }
        }
        return ResponseResult.ok(resultMap);
    }

    @Override
    public ResponseResult<Map<String, DevicePointDto>> findAllDevicePointByModelIds(Set<String> modelIds) {
        //返回的对象
        Map<String, DevicePointDto> resultMap = Maps.newHashMap();
        //根据多个模型id查询设备数据
        List<DeviceEntity> deviceList = deviceDao.findAllByModelIdInAndIsDelete(modelIds, 1);
        if (CollectionUtils.isNotEmpty(deviceList)) {
            //对设备数据转换
            Map<String, DeviceEntity> deviceMap = deviceList.stream().collect(Collectors.toMap(BaseEntity::getId, Function.identity()));
            Map<String, List<ModelFunctionListDto>> modelFunctionMap = DeviceCommonUtil.getFunctionListByModeIds(modelIds);

            Set<String> deviceIds = deviceList.stream().map(BaseEntity::getId).collect(Collectors.toSet());
            //根据多个设备id查询枪数据
            Map<String, List<String>> deviceGunMap = deviceGunDao.findAllByDeviceIdIn(deviceIds).stream().collect(Collectors.groupingBy(DeviceGunEntity::getDeviceId,
                    Collectors.mapping(DeviceGunEntity::getGunCode, Collectors.toList())));

            //根据多个设备id查询设备点表数据
            Map<String, List<PointTableEntity>> pointTableMap = pointTableDao.findAllByDeviceIdIn(deviceIds)
                    .stream().collect(Collectors.groupingBy(PointTableEntity::getDeviceId));
            if (MapUtils.isNotEmpty(pointTableMap)) {
                pointTableMap.forEach((deviceId, pointTableList) -> {
                    if (deviceMap.containsKey(deviceId)) {
                        DeviceEntity deviceEntity = deviceMap.get(deviceId);
                        //定义结果实体类
                        DevicePointDto result = new DevicePointDto();
                        result.setId(deviceId);
                        //设备序列号
                        result.setDeviceNumber(deviceEntity.getDeviceNumber());
                        //获取设备枪编号数据
                        if (deviceGunMap.containsKey(deviceId)) {
                            result.setGunCodeList(deviceGunMap.get(deviceId));
                        }
                        //获取模型功能点数据
                        Map<String, ModelFunctionListDto> functionMap = Maps.newHashMap();
                        if (modelFunctionMap.containsKey(deviceEntity.getModelId())) {
                            functionMap = modelFunctionMap.get(deviceEntity.getModelId()).stream().collect(Collectors.toMap(ModelFunctionListDto::getFunctionId,
                                    Function.identity(), (k1, k2) -> k1));
                        }
                        Map<Long, DevicePointDto.FunctionPointData> functionPointDataMap = Maps.newHashMap();
                        //获取设备点表数据
                        for (PointTableEntity pointTable : pointTableList) {
                            if (StringUtil.isEmpty(pointTable.getFunctionId()) || !functionMap.containsKey(pointTable.getFunctionId())) {
                                continue;
                            }
                            ModelFunctionListDto modelFunction = functionMap.get(pointTable.getFunctionId());
                            DevicePointDto.FunctionPointData functionPointData = new DevicePointDto.FunctionPointData();
                            functionPointData.setFunctionLogo(modelFunction.getFunctionLogo());
                            functionPointData.setFunctionIndex(pointTable.getFunctionIndex());
                            functionPointData.setFieldCode(modelFunction.getFieldCode());
                            functionPointData.setFieldType(modelFunction.getFieldType());
                            functionPointData.setDataId(pointTable.getDataId());
                            functionPointData.setCoefficient(pointTable.getCoefficient());
                            functionPointData.setOffset(pointTable.getOffset());
                            functionPointDataMap.put(pointTable.getDataId(), functionPointData);
                        }
                        result.setFunctionPointDataMap(functionPointDataMap);
                        resultMap.put(deviceId, result);
                    }
                });
            }
        }
        return ResponseResult.ok(resultMap);
    }

    @Override
    public ResponseResult<Map<String, DeviceBasicInfoDto>> findAllDeviceInfoByTypeIds(Set<String> typeIds) {
        Map<String, DeviceBasicInfoDto> resultMap = Maps.newHashMap();

        //根据多个设备类型查询正常状态设备数据
        List<DeviceEntity> deviceEntityList = deviceDao.findAllByTypeIdInAndIsDelete(typeIds, 1);
        if (CollectionUtils.isNotEmpty(deviceEntityList)) {
            List<String> siteIdList = deviceEntityList.stream().map(DeviceEntity::getSiteId).filter(StringUtil::isNotEmpty).distinct().collect(Collectors.toList());
            //根据多个站点id查询站点基本信息
            List<SiteInfoEntity> siteEntityList = siteInfoDao.findAllById(siteIdList);
            Map<String, SiteInfoEntity> siteEntityMap = Maps.newHashMap();
            if (CollectionUtils.isNotEmpty(siteEntityList)) {
                siteEntityMap = siteEntityList.stream().collect(Collectors.toMap(SiteInfoEntity::getId, a -> a, (k1, k2) -> k1));
            }
            //根据多个模型id查询模型数据
            List<String> modelIds = deviceEntityList.stream().map(DeviceEntity::getModelId).distinct().collect(Collectors.toList());
            Map<String, ModelEntity> modelMap = modelDao.findAllById(modelIds).stream().collect(Collectors.toMap(BaseEntity::getId, a -> a, (k1, k2) -> k1));

            //根据多个用户id查询用户名称
            List<String> userIds = Lists.newArrayList();
            userIds.addAll(deviceEntityList.stream().map(DeviceEntity::getCreateId).filter(StringUtil::isNotEmpty).distinct().collect(Collectors.toList()));
            userIds.addAll(deviceEntityList.stream().map(DeviceEntity::getUpdateId).filter(StringUtil::isNotEmpty).distinct().collect(Collectors.toList()));
            Map<String, UserDto> userMap = systemService.findUserInfoByIdsFeign(userIds.stream().distinct().collect(Collectors.toList())).getData();
            Map<String, SiteInfoEntity> finalSiteEntityMap = siteEntityMap;
            deviceEntityList.forEach(device -> {
                DeviceBasicInfoDto result = new DeviceBasicInfoDto();
                BeanUtils.copyProperties(device, result);
                //根据模型id查询模型名称,模型描述
                if (modelMap.containsKey(device.getModelId())) {
                    ModelEntity model = modelMap.get(device.getModelId());
                    result.setModelName(model.getModelName());
                    result.setModelDesc(model.getModelDesc());
                    result.setLogoPath(model.getLogoPath());
                }

                if (userMap.containsKey(device.getCreateId())) {
                    result.setCreateName(userMap.get(device.getCreateId()).getFullName());
                }
                if (userMap.containsKey(device.getUpdateId())) {
                    result.setUpdateName(userMap.get(device.getUpdateId()).getFullName());
                }

                //根据站点id查询站点名称和运营商名称
                SiteInfoEntity siteEntity = finalSiteEntityMap.get(device.getSiteId());
                if (StringUtil.isNotEmpty(siteEntity)) {
                    result.setSiteName(siteEntity.getSiteName());
                }

                //获取设备状态
                result.setTxStatus(DeviceCommonUtil.getDeviceTxStatus(device.getId(), device.getDeviceNumber()));
                resultMap.put(device.getDeviceNumber(), result);
            });
        }
        return ResponseResult.ok(resultMap);
    }

    @Override
    @Transactional(transactionManager = DBConstants.JPA_TX_ACCESS, rollbackFor = Exception.class)
    public ResponseResult<Void> updateDeviceQrStr(PileSetQrVo pileSetQrVo) {
        //根据设备编号查询设备数据
        List<DeviceEntity> deviceList = deviceDao.findAllByDeviceNumberAndIsDelete(pileSetQrVo.getPileCode(), 1);
        if (CollectionUtils.isNotEmpty(deviceList)) {
            Set<String> deviceIds = deviceList.stream().map(BaseEntity::getId).collect(Collectors.toSet());
            deviceGunDao.saveAll(deviceGunDao.findAllByDeviceIdIn(deviceIds).stream().peek(deviceGun -> {
                //存入二维码地址
                if (deviceGun.getGunCode().length() > 1) {
                    deviceGun.setQrCodes(pileSetQrVo.getQrStr() + pileSetQrVo.getPileCode() + deviceGun.getGunCode());
                } else {
                    deviceGun.setQrCodes(pileSetQrVo.getQrStr() + pileSetQrVo.getPileCode() + "0" + deviceGun.getGunCode());
                }
            }).collect(Collectors.toList()));
        }
        return ResponseResult.ok();
    }

    @Override
    public ResponseResult<Map<String, List<DeviceGunInfoDto>>> findDeviceGunInfoByDeviceCodes(List<String> deviceCodes) {
        Map<String, List<DeviceGunInfoDto>> resultMap = Maps.newHashMap();
        //根据多个设备编号查询设备id
        Map<String, String> deviceMap = deviceDao.findAllByDeviceNumberInAndIsDelete(deviceCodes, 1).stream().collect(Collectors.toMap(BaseEntity::getId,
                DeviceEntity::getDeviceNumber, (k1, k2) -> k1));
        //根据多个设备id查询设备枪数据
        List<DeviceGunEntity> deviceGunEntityList = deviceGunDao.findAllByDeviceIdIn(deviceMap.keySet());
        if (CollectionUtils.isNotEmpty(deviceGunEntityList)) {
            deviceGunEntityList.stream().collect(Collectors.groupingBy(DeviceGunEntity::getDeviceId))
                    .forEach((deviceId, deviceGunList) ->
                            resultMap.put(deviceMap.get(deviceId), deviceGunList.stream().map(deviceGun -> {
                                DeviceGunInfoDto result = new DeviceGunInfoDto();
                                BeanUtils.copyProperties(deviceGun, result);
                                return result;
                            }).collect(Collectors.toList())));
        }
        return ResponseResult.ok(resultMap);
    }

}
