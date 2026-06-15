package com.sunmax.configure.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.sunmax.common.config.redis.RedisDeviceUtil;
import com.sunmax.common.config.redis.RedisGeneralUtil;
import com.sunmax.common.dto.configure.GunStatusInfoDto;
import com.sunmax.common.dto.configure.PowerControlResDto;
import com.sunmax.common.dto.device.DeviceBasicInfoDto;
import com.sunmax.common.dto.operate.InterflowOrderRecordDto;
import com.sunmax.common.model.DeviceModel;
import com.sunmax.common.model.general.PileRealModel;
import com.sunmax.common.util.DoubleUtil;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.util.StringUtil;
import com.sunmax.common.util.oss.FileUtil;
import com.sunmax.common.vo.LocalParamVo;
import com.sunmax.common.vo.crontab.PowerControlParamVo;
import com.sunmax.common.vo.device.InterflowDeviceVo;
import com.sunmax.common.vo.device.InterflowGunVo;
import com.sunmax.common.vo.device.SiteInfoChangeVo;
import com.sunmax.configure.dao.*;
import com.sunmax.configure.dto.ResponseDto;
import com.sunmax.configure.dto.interflow.*;
import com.sunmax.configure.entity.interflow.*;
import com.sunmax.configure.runner.SubstationRunner;
import com.sunmax.configure.service.InterflowService;
import com.sunmax.configure.service.feign.DeviceService;
import com.sunmax.configure.service.feign.TogetherService;
import com.sunmax.configure.util.interflow.InterflowRequestUtil;
import com.sunmax.configure.vo.RequestCommonVo;
import com.sunmax.configure.vo.interflow.InterflowMethodVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.sunmax.common.util.CommonUtil.parseAddress;
import static com.sunmax.common.util.DateUtil.getDateSubSeconds;
import static com.sunmax.common.util.DateUtil.localDateTimeToStr;
import static com.sunmax.common.util.StringUtil.*;

@Slf4j
@Service
public class InterflowServiceImpl implements InterflowService {

    @Autowired
    private InterflowStationDao interflowStationDao;

    @Autowired
    private InterflowEquipmentDao interflowEquipmentDao;

    @Autowired
    private InterflowConnectorDao interflowConnectorDao;

    @Autowired
    private DeviceService deviceService;

    @Autowired
    private ChargeStateRecordDao chargeStateRecordDao;

    @Autowired
    private ChargeOrderRecordDao chargeOrderRecordDao;

    @Autowired
    private TogetherService togetherService;

    @Autowired
    private ConnectorStatusRecordDao connectorStatusRecordDao;

    //(运营商id + "_" + 充电设备接口编码) -> 充电桩编码
    private static final Map<String, String> connectorPileCodeMap = Maps.newHashMap();

    //(运营商id + "_" + 充电设备接口编码) -> 查询状态(true-是 说明查询过,不做二次查询 false-否)
    private static final Map<String, Boolean> connectorPileStatueMap = Maps.newHashMap();

    @Override
    public ResponseResult<String> queryToken(String platformId) {
        return ResponseResult.ok(InterflowRequestUtil.queryToken(SubstationRunner.getInterFlowField(platformId)));
    }

    /**
     * 查询充电桩信息
     */
    @Override
    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class)
    public void queryStationsInfo(String platformId) {
        //查询充电站信息
        Map<String, Object> paramMap = Maps.newHashMap();
        paramMap.put("LastQueryTime", "");
        paramMap.put("StationIDs", "");
        paramMap.put("PageNo", 1);
        paramMap.put("PageSize", 9999);
        RequestCommonVo interFlowField = SubstationRunner.getInterFlowField(platformId);
        if (interFlowField == null) {
            return;
        }
        //查询充电站信息接口
        ResponseDto responseDto = InterflowRequestUtil.queryData(interFlowField, InterflowMethodVo.QUERY_STATIONS_INFO, JSON.toJSONString(paramMap));
        if (responseDto.getRet() == 0) {
            JSONObject jsonData = JSONObject.parseObject(responseDto.getData(), JSONObject.class);
            //先获取条数字段，并判断值大于0条才会走下去
            Object itemSize = jsonData.get("ItemSize");
            if (isNotEmpty(itemSize) && Integer.parseInt(String.valueOf(itemSize)) > 0) {
                //存储互联互通站点记录表数据
                List<InterflowStationEntity> interflowStationEntities = Lists.newArrayList();
                //存储互联互通设备记录表数据
                List<InterflowEquipmentEntity> interflowEquipmentEntities = Lists.newArrayList();
                //存储互联互通设备接口记录表数据
                List<InterflowConnectorEntity> interflowConnectorEntities = Lists.newArrayList();
                //获取站点列表对象信息
                JSON.parseArray(String.valueOf(jsonData.get("StationInfos")), StationInfoDto.class).forEach(stationInfoDto -> {
                    //站点记录数据
                    InterflowStationEntity interflowStationEntity = new InterflowStationEntity();
                    BeanUtils.copyProperties(stationInfoDto, interflowStationEntity);
                    interflowStationEntity.setPlatformId(platformId);
                    interflowStationEntity.setStationLng(String.valueOf(stationInfoDto.getStationLng()));
                    interflowStationEntity.setStationLat(String.valueOf(stationInfoDto.getStationLat()));
                    List<String> pictures = stationInfoDto.getPictures();
                    if (CollectionUtils.isNotEmpty(pictures)) {
                        interflowStationEntity.setPictures(String.join(", ", pictures));
                    }
                    interflowStationEntities.add(interflowStationEntity);
                    //获取站点下设备列表
                    List<EquipmentInfoDto> equipmentInfos = stationInfoDto.getEquipmentInfos();
                    if (CollectionUtils.isNotEmpty(equipmentInfos)) {
                        equipmentInfos.forEach(equipmentInfoDto -> {
                            //设备记录数据
                            InterflowEquipmentEntity interflowEquipmentEntity = new InterflowEquipmentEntity();
                            BeanUtils.copyProperties(equipmentInfoDto, interflowEquipmentEntity);
                            interflowEquipmentEntity.setStationId(stationInfoDto.getStationId());
                            interflowEquipmentEntity.setConnectorInfos(JSON.toJSONString(equipmentInfoDto.getConnectorInfos()));
                            interflowEquipmentEntities.add(interflowEquipmentEntity);
                            //获取设备下接口列表
                            List<ConnectorInfoDto> connectorInfos = equipmentInfoDto.getConnectorInfos();
                            if (CollectionUtils.isNotEmpty(connectorInfos)) {
                                interflowConnectorEntities.addAll(connectorInfos.stream().map(connectorInfoDto -> {
                                    InterflowConnectorEntity interflowConnectorEntity = new InterflowConnectorEntity();
                                    BeanUtils.copyProperties(connectorInfoDto, interflowConnectorEntity);
                                    interflowConnectorEntity.setEquipmentId(equipmentInfoDto.getEquipmentId());
                                    interflowConnectorEntity.setOperatorId(platformId);
                                    //保存充电设备接口编码，方便后续查询
                                    connectorPileCodeMap.put(platformId + FileUtil.UNDERLINE + connectorInfoDto.getConnectorId(), interflowEquipmentEntity.getEquipmentId());
                                    return interflowConnectorEntity;
                                }).collect(Collectors.toList()));
                            }
                        });
                    }
                });
                //保存站点记录数据
                interflowStationDao.saveAll(interflowStationEntities);
                //保存设备记录数据
                interflowEquipmentDao.saveAll(interflowEquipmentEntities);
                //保存接口记录数据
                if (CollectionUtils.isNotEmpty(interflowConnectorEntities)) {
                    //根据多个设备编码删除下面电枪数据，然后保存新的电枪数据
                    List<String> equipmentIds = interflowConnectorEntities.stream().map(InterflowConnectorEntity::getEquipmentId).distinct().collect(Collectors.toList());
                    List<InterflowConnectorEntity> connectorEntities = interflowConnectorDao.findAllByEquipmentIdIn(equipmentIds);
                    if (CollectionUtils.isNotEmpty(connectorEntities)) {
                        interflowConnectorDao.deleteAll(connectorEntities);
                    }
                    interflowConnectorDao.saveAll(interflowConnectorEntities);
                }
                //转换成站点管理数据，并保存入数据库
                if (CollectionUtils.isNotEmpty(interflowStationEntities)) {
                    deviceService.saveOrUpdateInterflowSite(interflowStationEntities.stream().map(this::interflowStationEntityToVo).collect(Collectors.toList()));
                }
                //转换成设备管理和电枪接口数据，并保存入数据库
                if (CollectionUtils.isNotEmpty(interflowEquipmentEntities)) {
                    deviceService.saveOrUpdateInterflowDevice(interflowEquipmentEntities.stream().map(this::interflowEquipmentEntityToVo).collect(Collectors.toList()));
                }
            }
        }
    }

    //把站点记录实体类转成站点编辑vo
    private SiteInfoChangeVo interflowStationEntityToVo(InterflowStationEntity interflowStationEntity) {
        SiteInfoChangeVo siteInfoChangeVo = new SiteInfoChangeVo();
        siteInfoChangeVo.setId(interflowStationEntity.getStationId());
        siteInfoChangeVo.setIsDelete(1);
        siteInfoChangeVo.setSourceType(2);
        siteInfoChangeVo.setSiteCode(interflowStationEntity.getStationId());
        siteInfoChangeVo.setScenarioTypes("3");

        // 设置站点模型ID和租户ID
        switch (LocalParamVo.INTERFLOW_TYPE) {
            case 1:
                siteInfoChangeVo.setSiteModelId(LocalParamVo.LOCAL_MODEL_ID);
                siteInfoChangeVo.setTenantId(LocalParamVo.LOCAL_TENANT_ID);
                siteInfoChangeVo.setOperatorId(LocalParamVo.LOCAL_TENANT_ID);
                siteInfoChangeVo.setPropertyId(LocalParamVo.LOCAL_TENANT_ID);
                break;
            case 2:
                siteInfoChangeVo.setSiteModelId(LocalParamVo.TEST_MODEL_ID);
                siteInfoChangeVo.setTenantId(LocalParamVo.TEST_TENANT_ID);
                siteInfoChangeVo.setOperatorId(LocalParamVo.TEST_TENANT_ID);
                siteInfoChangeVo.setPropertyId(LocalParamVo.TEST_TENANT_ID);
                break;
            case 3:
                siteInfoChangeVo.setSiteModelId(LocalParamVo.ALIYUN_MODEL_ID);
                siteInfoChangeVo.setTenantId(LocalParamVo.ALIYUN_TENANT_ID);
                siteInfoChangeVo.setOperatorId(LocalParamVo.ALIYUN_TENANT_ID);
                siteInfoChangeVo.setPropertyId(LocalParamVo.ALIYUN_TENANT_ID);
                break;
        }

        siteInfoChangeVo.setSiteName(interflowStationEntity.getStationName());
        siteInfoChangeVo.setSiteStatus(getStationStatus(interflowStationEntity.getStationStatus()));
        siteInfoChangeVo.setImagePath(interflowStationEntity.getPictures());
        siteInfoChangeVo.setSiteDescribe(interflowStationEntity.getRemark());

        // 创建站点位置信息的map
        Map<String, Object> locationMap = Maps.newHashMap();
        locationMap.put("longitude", interflowStationEntity.getStationLng());
        locationMap.put("latitude", interflowStationEntity.getStationLat());
        Map<String, String> addressDetails = parseAddress(interflowStationEntity.getAddress());
        locationMap.put("province", addressDetails.get("province"));
        locationMap.put("city", addressDetails.get("city"));
        locationMap.put("county", addressDetails.get("county"));
        locationMap.put("address", interflowStationEntity.getAddress());

        // 转换站点实体为map，并去除已设置的字段
        Map<String, Object> interflowStationMap = BeanUtil.beanToMap(interflowStationEntity);
        Arrays.asList(
                "stationId", "stationName", "stationStatus", "pictures",
                "stationLng", "stationLat", "address"
        ).forEach(interflowStationMap.keySet()::remove);
        interflowStationMap.put("location", locationMap);

        // 将站点扩展属性转换为JSON字符串
        siteInfoChangeVo.setSiteReadwriteObject(JSON.toJSONString(interflowStationMap));

        return siteInfoChangeVo;
    }

    //把设备记录实体类转成设备编辑vo
    private InterflowDeviceVo interflowEquipmentEntityToVo(InterflowEquipmentEntity interflowEquipmentEntity) {
        InterflowDeviceVo vo = new InterflowDeviceVo();
        vo.setAccessType(1);
        vo.setIsDelete(1);
        vo.setSiteId(interflowEquipmentEntity.getStationId());
        vo.setDeviceName(interflowEquipmentEntity.getEquipmentName());
        vo.setDeviceNumber(interflowEquipmentEntity.getEquipmentId());
        vo.setTypeId(getInterflowDeviceAssetType(interflowEquipmentEntity.getEquipmentType()));
        vo.setOperateStatus(1);
        String modelId = null;
        if (LocalParamVo.INTERFLOW_TYPE == 3) {//城市充电数据接入阿里云环境
            switch (interflowEquipmentEntity.getEquipmentType()) {
                case 1:
                    modelId = LocalParamVo.ALIYUN_DC_DEVICE_MODEL_ID;
                    break;
                case 2:
                    modelId = LocalParamVo.ALIYUN_AC_DEVICE_MODEL_ID;
                    break;
                case 3:
                    modelId = LocalParamVo.ALIYUN_V2G_DEVICE_MODEL_ID;
                    break;
            }
        } else if (LocalParamVo.INTERFLOW_TYPE == 2) {//城市充电数据接入测试环境
            switch (interflowEquipmentEntity.getEquipmentType()) {
                case 1:
                    modelId = LocalParamVo.TEST_DC_DEVICE_MODEL_ID;
                    break;
                case 2:
                    modelId = LocalParamVo.TEST_AC_DEVICE_MODEL_ID;
                    break;
                case 3:
                    modelId = LocalParamVo.TEST_V2G_DEVICE_MODEL_ID;
                    break;
            }
        } else if (LocalParamVo.INTERFLOW_TYPE == 1) {//城市充电数据接入本地环境
            switch (interflowEquipmentEntity.getEquipmentType()) {
                case 1:
                    modelId = LocalParamVo.LOCAL_DC_DEVICE_MODEL_ID;
                    break;
                case 2:
                    modelId = LocalParamVo.LOCAL_AC_DEVICE_MODEL_ID;
                    break;
                case 3:
                    modelId = LocalParamVo.LOCAL_V2G_DEVICE_MODEL_ID;
                    break;
            }
        }
        vo.setModelId(modelId);
        Map<String, Object> interflowMap = BeanUtil.beanToMap(interflowEquipmentEntity);
        Arrays.asList("equipmentId", "equipmentName", "equipmentType", "stationId").forEach(interflowMap.keySet()::remove);
        vo.setReadwriteObject(JSON.toJSONString(interflowMap));

        //处理设备接口信息
        String connectorInfos = InterflowEquipmentEntity.getConnectorInfos();
        if (isNotEmpty(connectorInfos)) {
            List<ConnectorInfoDto> connectorInfoDtoList = JSON.parseArray(connectorInfos, ConnectorInfoDto.class);
            vo.setInterflowGunVoList(connectorInfoDtoList.stream().map(connectorInfoDto -> {
                InterflowGunVo interflowGunVo = new InterflowGunVo();
                //获取电枪编码
                if (isNotEmpty(connectorInfoDto.getConnectorId())) {
                    if (connectorInfoDto.getConnectorId().contains(FileUtil.UNDERLINE)) {
                        String[] split = connectorInfoDto.getConnectorId().split(FileUtil.UNDERLINE);
                        if (split.length > 1 && isNumber(split[1])) {
                            interflowGunVo.setGunCode(String.valueOf(Integer.parseInt(split[1]) + 1));
                        }
                    } else {
                        interflowGunVo.setGunCode(connectorInfoDto.getConnectorId());
                    }
                }
                interflowGunVo.setGunName(connectorInfoDto.getConnectorName());
                interflowGunVo.setType(connectorInfoDto.getConnectorType());
                interflowGunVo.setRatedCurrent(connectorInfoDto.getCurrent());
                interflowGunVo.setRatedPower(connectorInfoDto.getPower());
                interflowGunVo.setVoltageUpperLimits(connectorInfoDto.getVoltageUpperLimits());
                interflowGunVo.setVoltageLowerLimits(connectorInfoDto.getVoltageLowerLimits());
                interflowGunVo.setParkNo(connectorInfoDto.getParkNo());
                interflowGunVo.setNationalStandard(connectorInfoDto.getNationalStandard());
                return interflowGunVo;
            }).collect(Collectors.toList()));
        }
        return vo;
    }

    @Override
    public ResponseDto queryInterflowData(String platformId, String methodName, String paramData) {
        RequestCommonVo interFlowField = SubstationRunner.getInterFlowField(platformId);
        if (interFlowField == null) {
            return ResponseDto.error(4003, "未找到对应的接入平台");
        }
        return InterflowRequestUtil.queryData(interFlowField, methodName, paramData);
    }

    @Override
    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class)
    public Map<String, Object> notificationEquipChargeStatus(String operatorId, String data) {
        Map<String, Object> resultDataMap = Maps.newHashMap();
        if (isNotEmpty(data)) {
            ChargeStateDto chargeStateDto = JSONObject.parseObject(data, ChargeStateDto.class);
            String connectorId = chargeStateDto.getConnectorId();
            //存储推送记录
            ChargeStateRecordEntity pushRecordEntity = new ChargeStateRecordEntity();
            pushRecordEntity.setConnectorId(connectorId);
            pushRecordEntity.setPushDataObject(data);
            chargeStateRecordDao.save(pushRecordEntity);
            //获取电桩编码和枪编码
            JSONObject jsonObject = this.getPileCodeAndGunCode(operatorId, connectorId);
            String pileCode = jsonObject.getString("pileCode");
            String gunCode = jsonObject.getString("gunCode");
            if (StringUtil.isNotEmpty(pileCode) && StringUtil.isNotEmpty(gunCode)) {
                RedisGeneralUtil.executePile(pileCode, () -> {
                    //查询电桩实时数据
                    PileRealModel pileRealModel = RedisGeneralUtil.getPileRealModel(pileCode);
                    //电枪实时数据对象
                    PileRealModel.GunRealModel gunRealModel = new PileRealModel.GunRealModel();
                    //电枪实时数据map
                    Map<String, PileRealModel.GunRealModel> gunRealModelMap = Maps.newHashMap();
                    if (isEmpty(pileRealModel)) {
                        pileRealModel = new PileRealModel();
                    } else {
                        gunRealModelMap = pileRealModel.getGunRealModelMap();
                        if (!gunRealModelMap.isEmpty() && gunRealModelMap.containsKey(gunCode)) {
                            gunRealModel = gunRealModelMap.get(gunCode);
                        }
                    }
                    //存入电桩数据
                    pileRealModel.setPileCode(pileCode);
                    pileRealModel.setWorkStatus(getInterflowPileWorkStatus(chargeStateDto.getConnectorStatus()));
                    pileRealModel.setOriginalStatus(getInterflowPileOriginalStatus(chargeStateDto.getConnectorStatus()));
                    pileRealModel.setMessageType(2);
                    //存入电枪数据
                    BeanUtil.copyProperties(chargeStateDto, gunRealModel);
                    gunRealModel.setGunCode(gunCode);
                    gunRealModel.setGunStatus(getInterflowGunWorkStatus(chargeStateDto.getConnectorStatus()));
                    gunRealModel.setGunOriginalStatus(getInterflowGunOriginalStatus(chargeStateDto.getConnectorStatus()));
                    gunRealModel.setSerialNum(chargeStateDto.getStartChargeSeq());
                    gunRealModel.setStarter(2);
                    gunRealModel.setStartTime(chargeStateDto.getStartTime());
                    gunRealModel.setBatterySoc(chargeStateDto.getSoc().intValue());
                    gunRealModel.setRunMode(0);
                    gunRealModel.setOutVolt(chargeStateDto.getVoltageA());
                    gunRealModel.setOutCurrent(chargeStateDto.getCurrentA());
                    if (isNotEmpty(chargeStateDto.getVoltageA()) && isNotEmpty(chargeStateDto.getCurrentA())) {
                        gunRealModel.setOutPower(DoubleUtil.getToDouble(chargeStateDto.getVoltageA() * chargeStateDto.getCurrentA() / 1000));
                    }
                    gunRealModel.setReqVolt(chargeStateDto.getBclNeedVoltage());
                    gunRealModel.setReqCurrent(chargeStateDto.getBclNeedCurrent());
                    if (isNotEmpty(chargeStateDto.getBclNeedVoltage()) && isNotEmpty(chargeStateDto.getBclNeedCurrent())) {
                        gunRealModel.setReqPower(DoubleUtil.getToDouble(chargeStateDto.getBclNeedVoltage() * chargeStateDto.getBclNeedCurrent()));
                    }
                    //计算运行时间，如果当前电枪状态为充电中，则那开始充电时间-当前时间
                    if (isNotEmpty(chargeStateDto.getConnectorStatus()) && chargeStateDto.getConnectorStatus() == 3) {
                        String startTime = chargeStateDto.getStartTime();
                        String timeToStr = localDateTimeToStr(LocalDateTime.now());
                        gunRealModel.setRunTime((int) getDateSubSeconds(startTime, timeToStr));
                    }
                    gunRealModel.setTotalQt(chargeStateDto.getTotalPower());
                    gunRealModel.setTotalCost(chargeStateDto.getTotalMoney());
                    gunRealModelMap.put(gunCode, gunRealModel);
                    //计算电桩总功率、总充电功率
                    double power = DoubleUtil.getToDouble(gunRealModelMap.values().stream().filter(p -> isNotEmpty(p.getOutPower())).mapToDouble(PileRealModel.GunRealModel::getOutPower).sum());
                    pileRealModel.setTotalPower(power);
                    pileRealModel.setRecChargePower(power);
                    pileRealModel.setGunRealModelMap(gunRealModelMap);
                    pileRealModel.setDateTime(localDateTimeToStr(LocalDateTime.now()));
                    //保存入redis缓存
                    RedisGeneralUtil.setPileRealModel(pileCode, pileRealModel);

                    //根据设备编码查询设备缓存信息
                    DeviceModel deviceModel = RedisDeviceUtil.getDevice(pileCode);
                    if (isNotEmpty(deviceModel) && isNotEmpty(deviceModel.getDeviceNumber())) {
                        //如果当前状态不等于在线，则更新当前状态
                        if (!deviceModel.getTxStatus().equals(pileRealModel.getWorkStatus()) && !(pileRealModel.getWorkStatus() == -1 && deviceModel.getTxStatus() == 0)) {
                            deviceModel.setTxStatus(pileRealModel.getWorkStatus());
                            RedisDeviceUtil.setDevice(pileCode, deviceModel);
                        }
                    }
                });
            }
            resultDataMap.put("StartChargeSeq", chargeStateDto.getStartChargeSeq());
            resultDataMap.put("SuccStat", 0);
        } else {
            resultDataMap.put("SuccStat", 1);
        }
        return resultDataMap;
    }

    public static void main(String[] args) {
        String payload1 = "{\n  \"body\": {\n    \"body\": {\n      \"chId\": \"SMEMS_GunInfoReport_t\",\n      \"comType\": 1,\n      \"driver\": \"tcpServerEMS\",\n      \"groupList\": [],\n      \"pointList\": [\n        {\n          \"appType\": 1,\n          \"pId\": 101,\n          \"val\": \"1\"\n        },\n        {\n          \"appType\": 1,\n          \"pId\": 102,\n          \"val\": \"1\"\n        },\n        {\n          \"appType\": 1,\n          \"pId\": 103,\n          \"val\": \"0\"\n        },\n        {\n          \"appType\": 1,\n          \"pId\": 104,\n          \"val\": \"0\"\n        },\n        {\n          \"appType\": 1,\n          \"pId\": 105,\n          \"val\": \"0\"\n        },\n        {\n          \"appType\": 1,\n          \"pId\": 106,\n          \"val\": \"0\"\n        },\n        {\n          \"appType\": 1,\n          \"pId\": 107,\n          \"val\": \"0\"\n        },\n        {\n          \"appType\": 1,\n          \"pId\": 108,\n          \"val\": \"0\"\n        },\n        {\n          \"appType\": 1,\n          \"pId\": 109,\n          \"val\": \"100\"\n        },\n        {\n          \"appType\": 1,\n          \"pId\": 110,\n          \"val\": \"28.434\"\n        },\n        {\n          \"appType\": 1,\n          \"pId\": 111,\n          \"val\": \"40\"\n        }\n      ]\n    },\n    \"cmd\": \"44\",\n    \"devId\": \"TcpEMSDev\",\n    \"platformId\": \"\",\n    \"point\": \"\",\n    \"protCode\": \"0\",\n    \"type\": \"\"\n  },\n  \"timestamp\": \"2025.11.20 16:14:38\",\n  \"token\": \"sendCmdUp\"\n}";
        String payload2 = "{\n  \"body\": {\n    \"body\": {\n      \"chId\": \"SMEMS_GunInfoReport_t\",\n      \"comType\": 1,\n      \"driver\": \"tcpServerEMS\",\n      \"groupList\": [],\n      \"pointList\": [\n        {\n          \"appType\": 1,\n          \"pId\": 112,\n          \"val\": \"2\"\n        },\n        {\n          \"appType\": 1,\n          \"pId\": 113,\n          \"val\": \"1\"\n        },\n        {\n          \"appType\": 1,\n          \"pId\": 114,\n          \"val\": \"0\"\n        },\n        {\n          \"appType\": 1,\n          \"pId\": 115,\n          \"val\": \"0\"\n        },\n        {\n          \"appType\": 1,\n          \"pId\": 116,\n          \"val\": \"0\"\n        },\n        {\n          \"appType\": 1,\n          \"pId\": 117,\n          \"val\": \"0\"\n        },\n        {\n          \"appType\": 1,\n          \"pId\": 118,\n          \"val\": \"0\"\n        },\n        {\n          \"appType\": 1,\n          \"pId\": 119,\n          \"val\": \"0\"\n        },\n        {\n          \"appType\": 1,\n          \"pId\": 120,\n          \"val\": \"75\"\n        },\n        {\n          \"appType\": 1,\n          \"pId\": 121,\n          \"val\": \"0.006\"\n        },\n        {\n          \"appType\": 1,\n          \"pId\": 122,\n          \"val\": \"2100\"\n        }\n      ]\n    },\n    \"cmd\": \"44\",\n    \"devId\": \"TcpEMSDev\",\n    \"platformId\": \"\",\n    \"point\": \"\",\n    \"protCode\": \"0\",\n    \"type\": \"\"\n  },\n  \"timestamp\": \"2025.11.20 16:14:38\",\n  \"token\": \"sendCmdUp\"\n}";
        String payload3 = "{\n  \"body\": {\n    \"body\": {\n      \"chId\": \"SMEMS_GunInfoReport_t\",\n      \"comType\": 1,\n      \"driver\": \"tcpServerEMS\",\n      \"groupList\": [],\n      \"pointList\": [\n        {\n          \"appType\": 1,\n          \"pId\": 123,\n          \"val\": \"3\"\n        },\n        {\n          \"appType\": 1,\n          \"pId\": 124,\n          \"val\": \"1\"\n        },\n        {\n          \"appType\": 1,\n          \"pId\": 125,\n          \"val\": \"0\"\n        },\n        {\n          \"appType\": 1,\n          \"pId\": 126,\n          \"val\": \"25\"\n        },\n        {\n          \"appType\": 1,\n          \"pId\": 127,\n          \"val\": \"0\"\n        },\n        {\n          \"appType\": 1,\n          \"pId\": 128,\n          \"val\": \"0\"\n        },\n        {\n          \"appType\": 1,\n          \"pId\": 129,\n          \"val\": \"0\"\n        },\n        {\n          \"appType\": 1,\n          \"pId\": 130,\n          \"val\": \"0\"\n        },\n        {\n          \"appType\": 1,\n          \"pId\": 131,\n          \"val\": \"100\"\n        },\n        {\n          \"appType\": 1,\n          \"pId\": 132,\n          \"val\": \"23.834\"\n        },\n        {\n          \"appType\": 1,\n          \"pId\": 133,\n          \"val\": \"50\"\n        }\n      ]\n    },\n    \"cmd\": \"44\",\n    \"devId\": \"TcpEMSDev\",\n    \"platformId\": \"\",\n    \"point\": \"\",\n    \"protCode\": \"0\",\n    \"type\": \"\"\n  },\n  \"timestamp\": \"2025.11.20 16:14:38\",\n  \"token\": \"sendCmdUp\"\n}";
        String payload4 = "{\n  \"body\": {\n    \"body\": {\n      \"chId\": \"SMEMS_GunInfoReport_t\",\n      \"comType\": 1,\n      \"driver\": \"tcpServerEMS\",\n      \"groupList\": [],\n      \"pointList\": [\n        {\n          \"appType\": 1,\n          \"pId\": 134,\n          \"val\": \"4\"\n        },\n        {\n          \"appType\": 1,\n          \"pId\": 135,\n          \"val\": \"1\"\n        },\n        {\n          \"appType\": 1,\n          \"pId\": 136,\n          \"val\": \"0\"\n        },\n        {\n          \"appType\": 1,\n          \"pId\": 137,\n          \"val\": \"25\"\n        },\n        {\n          \"appType\": 1,\n          \"pId\": 138,\n          \"val\": \"0\"\n        },\n        {\n          \"appType\": 1,\n          \"pId\": 139,\n          \"val\": \"0\"\n        },\n        {\n          \"appType\": 1,\n          \"pId\": 140,\n          \"val\": \"0\"\n        },\n        {\n          \"appType\": 1,\n          \"pId\": 141,\n          \"val\": \"0\"\n        },\n        {\n          \"appType\": 1,\n          \"pId\": 142,\n          \"val\": \"76\"\n        },\n        {\n          \"appType\": 1,\n          \"pId\": 143,\n          \"val\": \"0.003\"\n        },\n        {\n          \"appType\": 1,\n          \"pId\": 144,\n          \"val\": \"1671\"\n        }\n      ]\n    },\n    \"cmd\": \"44\",\n    \"devId\": \"TcpEMSDev\",\n    \"platformId\": \"\",\n    \"point\": \"\",\n    \"protCode\": \"0\",\n    \"type\": \"\"\n  },\n  \"timestamp\": \"2025.11.20 16:14:38\",\n  \"token\": \"sendCmdUp\"\n}";
        String payload5 = "{\n  \"body\": {\n    \"body\": {\n      \"chId\": \"SMEMS_GunInfoReport_t\",\n      \"comType\": 1,\n      \"driver\": \"tcpServerEMS\",\n      \"groupList\": [],\n      \"pointList\": [\n        {\n          \"appType\": 1,\n          \"pId\": 145,\n          \"val\": \"5\"\n        },\n        {\n          \"appType\": 1,\n          \"pId\": 146,\n          \"val\": \"2\"\n        },\n        {\n          \"appType\": 1,\n          \"pId\": 147,\n          \"val\": \"0\"\n        },\n        {\n          \"appType\": 1,\n          \"pId\": 148,\n          \"val\": \"0\"\n        },\n        {\n          \"appType\": 1,\n          \"pId\": 149,\n          \"val\": \"467.6\"\n        },\n        {\n          \"appType\": 1,\n          \"pId\": 150,\n          \"val\": \"164.70000000000002\"\n        },\n        {\n          \"appType\": 1,\n          \"pId\": 151,\n          \"val\": \"405.1\"\n        },\n        {\n          \"appType\": 1,\n          \"pId\": 152,\n          \"val\": \"169.3\"\n        },\n        {\n          \"appType\": 1,\n          \"pId\": 153,\n          \"val\": \"38\"\n        },\n        {\n          \"appType\": 1,\n          \"pId\": 154,\n          \"val\": \"1.553\"\n        },\n        {\n          \"appType\": 1,\n          \"pId\": 155,\n          \"val\": \"2\"\n        }\n      ]\n    },\n    \"cmd\": \"44\",\n    \"devId\": \"TcpEMSDev\",\n    \"platformId\": \"\",\n    \"point\": \"\",\n    \"protCode\": \"0\",\n    \"type\": \"\"\n  },\n  \"timestamp\": \"2025.11.20 16:14:38\",\n  \"token\": \"sendCmdUp\"\n}";
        String payload6 = "{\n  \"body\": {\n    \"body\": {\n      \"chId\": \"SMEMS_GunInfoReport_t\",\n      \"comType\": 1,\n      \"driver\": \"tcpServerEMS\",\n      \"groupList\": [],\n      \"pointList\": [\n        {\n          \"appType\": 1,\n          \"pId\": 156,\n          \"val\": \"6\"\n        },\n        {\n          \"appType\": 1,\n          \"pId\": 157,\n          \"val\": \"2\"\n        },\n        {\n          \"appType\": 1,\n          \"pId\": 158,\n          \"val\": \"0\"\n        },\n        {\n          \"appType\": 1,\n          \"pId\": 159,\n          \"val\": \"112\"\n        },\n        {\n          \"appType\": 1,\n          \"pId\": 160,\n          \"val\": \"0\"\n        },\n        {\n          \"appType\": 1,\n          \"pId\": 161,\n          \"val\": \"0\"\n        },\n        {\n          \"appType\": 1,\n          \"pId\": 162,\n          \"val\": \"0\"\n        },\n        {\n          \"appType\": 1,\n          \"pId\": 163,\n          \"val\": \"0\"\n        },\n        {\n          \"appType\": 1,\n          \"pId\": 164,\n          \"val\": \"27\"\n        },\n        {\n          \"appType\": 1,\n          \"pId\": 165,\n          \"val\": \"0.001\"\n        },\n        {\n          \"appType\": 1,\n          \"pId\": 166,\n          \"val\": \"0\"\n        }\n      ]\n    },\n    \"cmd\": \"44\",\n    \"devId\": \"TcpEMSDev\",\n    \"platformId\": \"\",\n    \"point\": \"\",\n    \"protCode\": \"0\",\n    \"type\": \"\"\n  },\n  \"timestamp\": \"2025.11.20 16:14:38\",\n  \"token\": \"sendCmdUp\"\n}";
        String payload7 = "{\n  \"body\": {\n    \"body\": {\n      \"chId\": \"SMEMS_GunInfoReport_t\",\n      \"comType\": 1,\n      \"driver\": \"tcpServerEMS\",\n      \"groupList\": [],\n      \"pointList\": [\n        {\n          \"appType\": 1,\n          \"pId\": 167,\n          \"val\": \"7\"\n        },\n        {\n          \"appType\": 1,\n          \"pId\": 168,\n          \"val\": \"1\"\n        },\n        {\n          \"appType\": 1,\n          \"pId\": 169,\n          \"val\": \"0\"\n        },\n        {\n          \"appType\": 1,\n          \"pId\": 170,\n          \"val\": \"0\"\n        },\n        {\n          \"appType\": 1,\n          \"pId\": 171,\n          \"val\": \"0\"\n        },\n        {\n          \"appType\": 1,\n          \"pId\": 172,\n          \"val\": \"0\"\n        },\n        {\n          \"appType\": 1,\n          \"pId\": 173,\n          \"val\": \"0\"\n        },\n        {\n          \"appType\": 1,\n          \"pId\": 174,\n          \"val\": \"0\"\n        },\n        {\n          \"appType\": 1,\n          \"pId\": 175,\n          \"val\": \"100\"\n        },\n        {\n          \"appType\": 1,\n          \"pId\": 176,\n          \"val\": \"25.184\"\n        },\n        {\n          \"appType\": 1,\n          \"pId\": 177,\n          \"val\": \"47\"\n        }\n      ]\n    },\n    \"cmd\": \"44\",\n    \"devId\": \"TcpEMSDev\",\n    \"platformId\": \"\",\n    \"point\": \"\",\n    \"protCode\": \"0\",\n    \"type\": \"\"\n  },\n  \"timestamp\": \"2025.11.20 16:14:38\",\n  \"token\": \"sendCmdUp\"\n}";
        String payload8 = "{\n  \"body\": {\n    \"body\": {\n      \"chId\": \"SMEMS_GunInfoReport_t\",\n      \"comType\": 1,\n      \"driver\": \"tcpServerEMS\",\n      \"groupList\": [],\n      \"pointList\": [\n        {\n          \"appType\": 1,\n          \"pId\": 178,\n          \"val\": \"8\"\n        },\n        {\n          \"appType\": 1,\n          \"pId\": 179,\n          \"val\": \"1\"\n        },\n        {\n          \"appType\": 1,\n          \"pId\": 180,\n          \"val\": \"0\"\n        },\n        {\n          \"appType\": 1,\n          \"pId\": 181,\n          \"val\": \"0\"\n        },\n        {\n          \"appType\": 1,\n          \"pId\": 182,\n          \"val\": \"0\"\n        },\n        {\n          \"appType\": 1,\n          \"pId\": 183,\n          \"val\": \"0\"\n        },\n        {\n          \"appType\": 1,\n          \"pId\": 184,\n          \"val\": \"0\"\n        },\n        {\n          \"appType\": 1,\n          \"pId\": 185,\n          \"val\": \"0\"\n        },\n        {\n          \"appType\": 1,\n          \"pId\": 186,\n          \"val\": \"97\"\n        },\n        {\n          \"appType\": 1,\n          \"pId\": 187,\n          \"val\": \"0.003\"\n        },\n        {\n          \"appType\": 1,\n          \"pId\": 188,\n          \"val\": \"1308\"\n        }\n      ]\n    },\n    \"cmd\": \"44\",\n    \"devId\": \"TcpEMSDev\",\n    \"platformId\": \"\",\n    \"point\": \"\",\n    \"protCode\": \"0\",\n    \"type\": \"\"\n  },\n  \"timestamp\": \"2025.11.20 16:14:38\",\n  \"token\": \"sendCmdUp\"\n}";
        String payload9 = "{\n  \"body\": {\n    \"body\": {\n      \"chId\": \"SMEMS_GunInfoReport_t\",\n      \"comType\": 1,\n      \"driver\": \"tcpServerEMS\",\n      \"groupList\": [],\n      \"pointList\": [\n        {\n          \"appType\": 1,\n          \"pId\": 189,\n          \"val\": \"9\"\n        },\n        {\n          \"appType\": 1,\n          \"pId\": 190,\n          \"val\": \"2\"\n        },\n        {\n          \"appType\": 1,\n          \"pId\": 191,\n          \"val\": \"7\"\n        },\n        {\n          \"appType\": 1,\n          \"pId\": 192,\n          \"val\": \"126\"\n        },\n        {\n          \"appType\": 1,\n          \"pId\": 193,\n          \"val\": \"0\"\n        },\n        {\n          \"appType\": 1,\n          \"pId\": 194,\n          \"val\": \"0\"\n        },\n        {\n          \"appType\": 1,\n          \"pId\": 195,\n          \"val\": \"0\"\n        },\n        {\n          \"appType\": 1,\n          \"pId\": 196,\n          \"val\": \"0\"\n        },\n        {\n          \"appType\": 1,\n          \"pId\": 197,\n          \"val\": \"100\"\n        },\n        {\n          \"appType\": 1,\n          \"pId\": 198,\n          \"val\": \"35.93\"\n        },\n        {\n          \"appType\": 1,\n          \"pId\": 199,\n          \"val\": \"47\"\n        }\n      ]\n    },\n    \"cmd\": \"44\",\n    \"devId\": \"TcpEMSDev\",\n    \"platformId\": \"\",\n    \"point\": \"\",\n    \"protCode\": \"0\",\n    \"type\": \"\"\n  },\n  \"timestamp\": \"2025.11.20 16:14:38\",\n  \"token\": \"sendCmdUp\"\n}";
        String payload10 = "{\n  \"body\": {\n    \"body\": {\n      \"chId\": \"SMEMS_GunInfoReport_t\",\n      \"comType\": 1,\n      \"driver\": \"tcpServerEMS\",\n      \"groupList\": [],\n      \"pointList\": [\n        {\n          \"appType\": 1,\n          \"pId\": 200,\n          \"val\": \"10\"\n        },\n        {\n          \"appType\": 1,\n          \"pId\": 201,\n          \"val\": \"2\"\n        },\n        {\n          \"appType\": 1,\n          \"pId\": 202,\n          \"val\": \"0\"\n        },\n        {\n          \"appType\": 1,\n          \"pId\": 203,\n          \"val\": \"0\"\n        },\n        {\n          \"appType\": 1,\n          \"pId\": 204,\n          \"val\": \"0\"\n        },\n        {\n          \"appType\": 1,\n          \"pId\": 205,\n          \"val\": \"0\"\n        },\n        {\n          \"appType\": 1,\n          \"pId\": 206,\n          \"val\": \"0\"\n        },\n        {\n          \"appType\": 1,\n          \"pId\": 207,\n          \"val\": \"0\"\n        },\n        {\n          \"appType\": 1,\n          \"pId\": 208,\n          \"val\": \"94\"\n        },\n        {\n          \"appType\": 1,\n          \"pId\": 209,\n          \"val\": \"0.003\"\n        },\n        {\n          \"appType\": 1,\n          \"pId\": 210,\n          \"val\": \"3029\"\n        }\n      ]\n    },\n    \"cmd\": \"44\",\n    \"devId\": \"TcpEMSDev\",\n    \"platformId\": \"\",\n    \"point\": \"\",\n    \"protCode\": \"0\",\n    \"type\": \"\"\n  },\n  \"timestamp\": \"2025.11.20 16:14:38\",\n  \"token\": \"sendCmdUp\"\n}";
        String payload11 = "{\n  \"body\": {\n    \"body\": {\n      \"chId\": \"SMEMS_GunInfoReport_t\",\n      \"comType\": 1,\n      \"driver\": \"tcpServerEMS\",\n      \"groupList\": [],\n      \"pointList\": [\n        {\n          \"appType\": 1,\n          \"pId\": 211,\n          \"val\": \"11\"\n        },\n        {\n          \"appType\": 1,\n          \"pId\": 212,\n          \"val\": \"1\"\n        },\n        {\n          \"appType\": 1,\n          \"pId\": 213,\n          \"val\": \"0\"\n        },\n        {\n          \"appType\": 1,\n          \"pId\": 214,\n          \"val\": \"0\"\n        },\n        {\n          \"appType\": 1,\n          \"pId\": 215,\n          \"val\": \"0\"\n        },\n        {\n          \"appType\": 1,\n          \"pId\": 216,\n          \"val\": \"0\"\n        },\n        {\n          \"appType\": 1,\n          \"pId\": 217,\n          \"val\": \"0\"\n        },\n        {\n          \"appType\": 1,\n          \"pId\": 218,\n          \"val\": \"0\"\n        },\n        {\n          \"appType\": 1,\n          \"pId\": 219,\n          \"val\": \"0\"\n        },\n        {\n          \"appType\": 1,\n          \"pId\": 220,\n          \"val\": \"5.626\"\n        },\n        {\n          \"appType\": 1,\n          \"pId\": 221,\n          \"val\": \"23\"\n        }\n      ]\n    },\n    \"cmd\": \"44\",\n    \"devId\": \"TcpEMSDev\",\n    \"platformId\": \"\",\n    \"point\": \"\",\n    \"protCode\": \"0\",\n    \"type\": \"\"\n  },\n  \"timestamp\": \"2025.11.20 16:14:38\",\n  \"token\": \"sendCmdUp\"\n}";
        String payload12 = "{\n  \"body\": {\n    \"body\": {\n      \"chId\": \"SMEMS_GunInfoReport_t\",\n      \"comType\": 1,\n      \"driver\": \"tcpServerEMS\",\n      \"groupList\": [],\n      \"pointList\": [\n        {\n          \"appType\": 1,\n          \"pId\": 222,\n          \"val\": \"12\"\n        },\n        {\n          \"appType\": 1,\n          \"pId\": 223,\n          \"val\": \"1\"\n        },\n        {\n          \"appType\": 1,\n          \"pId\": 224,\n          \"val\": \"4\"\n        },\n        {\n          \"appType\": 1,\n          \"pId\": 225,\n          \"val\": \"0\"\n        },\n        {\n          \"appType\": 1,\n          \"pId\": 226,\n          \"val\": \"413.8\"\n        },\n        {\n          \"appType\": 1,\n          \"pId\": 227,\n          \"val\": \"101.9\"\n        },\n        {\n          \"appType\": 1,\n          \"pId\": 228,\n          \"val\": \"390.8\"\n        },\n        {\n          \"appType\": 1,\n          \"pId\": 229,\n          \"val\": \"101.80000000000001\"\n        },\n        {\n          \"appType\": 1,\n          \"pId\": 230,\n          \"val\": \"79\"\n        },\n        {\n          \"appType\": 1,\n          \"pId\": 231,\n          \"val\": \"0.003\"\n        },\n        {\n          \"appType\": 1,\n          \"pId\": 232,\n          \"val\": \"791\"\n        }\n      ]\n    },\n    \"cmd\": \"44\",\n    \"devId\": \"TcpEMSDev\",\n    \"platformId\": \"\",\n    \"point\": \"\",\n    \"protCode\": \"0\",\n    \"type\": \"\"\n  },\n  \"timestamp\": \"2025.11.20 16:14:38\",\n  \"token\": \"sendCmdUp\"\n}";
        String payload13 = "{\n  \"body\": {\n    \"body\": {\n      \"chId\": \"SMEMS_GunInfoReport_t\",\n      \"comType\": 1,\n      \"driver\": \"tcpServerEMS\",\n      \"groupList\": [],\n      \"pointList\": [\n        {\n          \"appType\": 1,\n          \"pId\": 233,\n          \"val\": \"13\"\n        },\n        {\n          \"appType\": 1,\n          \"pId\": 234,\n          \"val\": \"1\"\n        },\n        {\n          \"appType\": 1,\n          \"pId\": 235,\n          \"val\": \"0\"\n        },\n        {\n          \"appType\": 1,\n          \"pId\": 236,\n          \"val\": \"0\"\n        },\n        {\n          \"appType\": 1,\n          \"pId\": 237,\n          \"val\": \"0\"\n        },\n        {\n          \"appType\": 1,\n          \"pId\": 238,\n          \"val\": \"0\"\n        },\n        {\n          \"appType\": 1,\n          \"pId\": 239,\n          \"val\": \"0\"\n        },\n        {\n          \"appType\": 1,\n          \"pId\": 240,\n          \"val\": \"0\"\n        },\n        {\n          \"appType\": 1,\n          \"pId\": 241,\n          \"val\": \"82\"\n        },\n        {\n          \"appType\": 1,\n          \"pId\": 242,\n          \"val\": \"49.371\"\n        },\n        {\n          \"appType\": 1,\n          \"pId\": 243,\n          \"val\": \"33\"\n        }\n      ]\n    },\n    \"cmd\": \"44\",\n    \"devId\": \"TcpEMSDev\",\n    \"platformId\": \"\",\n    \"point\": \"\",\n    \"protCode\": \"0\",\n    \"type\": \"\"\n  },\n  \"timestamp\": \"2025.11.20 16:14:38\",\n  \"token\": \"sendCmdUp\"\n}";
        String payload14 = "{\n  \"body\": {\n    \"body\": {\n      \"chId\": \"SMEMS_GunInfoReport_t\",\n      \"comType\": 1,\n      \"driver\": \"tcpServerEMS\",\n      \"groupList\": [],\n      \"pointList\": [\n        {\n          \"appType\": 1,\n          \"pId\": 244,\n          \"val\": \"14\"\n        },\n        {\n          \"appType\": 1,\n          \"pId\": 245,\n          \"val\": \"1\"\n        },\n        {\n          \"appType\": 1,\n          \"pId\": 246,\n          \"val\": \"0\"\n        },\n        {\n          \"appType\": 1,\n          \"pId\": 247,\n          \"val\": \"0\"\n        },\n        {\n          \"appType\": 1,\n          \"pId\": 248,\n          \"val\": \"0\"\n        },\n        {\n          \"appType\": 1,\n          \"pId\": 249,\n          \"val\": \"0\"\n        },\n        {\n          \"appType\": 1,\n          \"pId\": 250,\n          \"val\": \"0\"\n        },\n        {\n          \"appType\": 1,\n          \"pId\": 251,\n          \"val\": \"0\"\n        },\n        {\n          \"appType\": 1,\n          \"pId\": 252,\n          \"val\": \"0\"\n        },\n        {\n          \"appType\": 1,\n          \"pId\": 253,\n          \"val\": \"0\"\n        },\n        {\n          \"appType\": 1,\n          \"pId\": 254,\n          \"val\": \"2840\"\n        }\n      ]\n    },\n    \"cmd\": \"44\",\n    \"devId\": \"TcpEMSDev\",\n    \"platformId\": \"\",\n    \"point\": \"\",\n    \"protCode\": \"0\",\n    \"type\": \"\"\n  },\n  \"timestamp\": \"2025.11.20 16:14:38\",\n  \"token\": \"sendCmdUp\"\n}";
        String payload15 = "{\n  \"body\": {\n    \"body\": {\n      \"chId\": \"SMEMS_GunInfoReport_t\",\n      \"comType\": 1,\n      \"driver\": \"tcpServerEMS\",\n      \"groupList\": [],\n      \"pointList\": [\n        {\n          \"appType\": 1,\n          \"pId\": 255,\n          \"val\": \"15\"\n        },\n        {\n          \"appType\": 1,\n          \"pId\": 256,\n          \"val\": \"2\"\n        },\n        {\n          \"appType\": 1,\n          \"pId\": 257,\n          \"val\": \"0\"\n        },\n        {\n          \"appType\": 1,\n          \"pId\": 258,\n          \"val\": \"0\"\n        },\n        {\n          \"appType\": 1,\n          \"pId\": 259,\n          \"val\": \"0\"\n        },\n        {\n          \"appType\": 1,\n          \"pId\": 260,\n          \"val\": \"0\"\n        },\n        {\n          \"appType\": 1,\n          \"pId\": 261,\n          \"val\": \"0\"\n        },\n        {\n          \"appType\": 1,\n          \"pId\": 262,\n          \"val\": \"0\"\n        },\n        {\n          \"appType\": 1,\n          \"pId\": 263,\n          \"val\": \"68\"\n        },\n        {\n          \"appType\": 1,\n          \"pId\": 264,\n          \"val\": \"16.44\"\n        },\n        {\n          \"appType\": 1,\n          \"pId\": 265,\n          \"val\": \"28\"\n        }\n      ]\n    },\n    \"cmd\": \"44\",\n    \"devId\": \"TcpEMSDev\",\n    \"platformId\": \"\",\n    \"point\": \"\",\n    \"protCode\": \"0\",\n    \"type\": \"\"\n  },\n  \"timestamp\": \"2025.11.20 16:14:39\",\n  \"token\": \"sendCmdUp\"\n}";
        String payload16 = "{\n  \"body\": {\n    \"body\": {\n      \"chId\": \"SMEMS_V2GInfoReport_t\",\n      \"comType\": 1,\n      \"driver\": \"tcpServerEMS\",\n      \"groupList\": [],\n      \"pointList\": [\n        {\n          \"appType\": 1,\n          \"pId\": 551,\n          \"val\": \"2\"\n        },\n        {\n          \"appType\": 1,\n          \"pId\": 552,\n          \"val\": \"0\"\n        },\n        {\n          \"appType\": 1,\n          \"pId\": 553,\n          \"val\": \"0\"\n        },\n        {\n          \"appType\": 1,\n          \"pId\": 554,\n          \"val\": \"0\"\n        },\n        {\n          \"appType\": 1,\n          \"pId\": 555,\n          \"val\": \"0\"\n        },\n        {\n          \"appType\": 1,\n          \"pId\": 556,\n          \"val\": \"0\"\n        },\n        {\n          \"appType\": 1,\n          \"pId\": 557,\n          \"val\": \"0\"\n        },\n        {\n          \"appType\": 1,\n          \"pId\": 558,\n          \"val\": \"0\"\n        },\n        {\n          \"appType\": 1,\n          \"pId\": 559,\n          \"val\": \"0\"\n        },\n        {\n          \"appType\": 1,\n          \"pId\": 560,\n          \"val\": \"0\"\n        }\n      ]\n    },\n    \"cmd\": \"44\",\n    \"devId\": \"TcpEMSDev\",\n    \"platformId\": \"\",\n    \"point\": \"\",\n    \"protCode\": \"0\",\n    \"type\": \"\"\n  },\n  \"timestamp\": \"2025.11.20 16:14:39\",\n  \"token\": \"sendCmdUp\"\n}";
        String payload17 = "{\n  \"body\": {\n    \"body\": {\n      \"chId\": \"SMEMS_SwapStationInfoReport_t\",\n      \"comType\": 1,\n      \"driver\": \"tcpServerEMS\",\n      \"groupList\": [],\n      \"pointList\": [\n        {\n          \"appType\": 1,\n          \"pId\": 651,\n          \"val\": \"2\"\n        },\n        {\n          \"appType\": 1,\n          \"pId\": 652,\n          \"val\": \"0\"\n        },\n        {\n          \"appType\": 1,\n          \"pId\": 653,\n          \"val\": \"0\"\n        },\n        {\n          \"appType\": 1,\n          \"pId\": 654,\n          \"val\": \"0\"\n        },\n        {\n          \"appType\": 1,\n          \"pId\": 655,\n          \"val\": \"537994.528\"\n        },\n        {\n          \"appType\": 1,\n          \"pId\": 656,\n          \"val\": \"4281930.774\"\n        },\n        {\n          \"appType\": 1,\n          \"pId\": 657,\n          \"val\": \"0\"\n        },\n        {\n          \"appType\": 1,\n          \"pId\": 658,\n          \"val\": \"0\"\n        }\n      ]\n    },\n    \"cmd\": \"44\",\n    \"devId\": \"TcpEMSDev\",\n    \"platformId\": \"\",\n    \"point\": \"\",\n    \"protCode\": \"0\",\n    \"type\": \"\"\n  },\n  \"timestamp\": \"2025.11.20 16:14:39\",\n  \"token\": \"sendCmdUp\"\n}";
        String payload18 = "{\n  \"body\": {\n    \"body\": {\n      \"chId\": \"SMEMS_SCInfoReport_t\",\n      \"comType\": 1,\n      \"driver\": \"tcpServerEMS\",\n      \"groupList\": [],\n      \"pointList\": [\n        {\n          \"appType\": 1,\n          \"pId\": 511,\n          \"val\": \"2\"\n        },\n        {\n          \"appType\": 1,\n          \"pId\": 512,\n          \"val\": \"2\"\n        },\n        {\n          \"appType\": 1,\n          \"pId\": 513,\n          \"val\": \"0\"\n        },\n        {\n          \"appType\": 1,\n          \"pId\": 514,\n          \"val\": \"0\"\n        },\n        {\n          \"appType\": 1,\n          \"pId\": 515,\n          \"val\": \"0\"\n        },\n        {\n          \"appType\": 1,\n          \"pId\": 516,\n          \"val\": \"0\"\n        },\n        {\n          \"appType\": 1,\n          \"pId\": 517,\n          \"val\": \"0\"\n        },\n        {\n          \"appType\": 1,\n          \"pId\": 518,\n          \"val\": \"0\"\n        },\n        {\n          \"appType\": 1,\n          \"pId\": 519,\n          \"val\": \"0\"\n        },\n        {\n          \"appType\": 1,\n          \"pId\": 520,\n          \"val\": \"0\"\n        }\n      ]\n    },\n    \"cmd\": \"44\",\n    \"devId\": \"TcpEMSDev\",\n    \"platformId\": \"\",\n    \"point\": \"\",\n    \"protCode\": \"0\",\n    \"type\": \"\"\n  },\n  \"timestamp\": \"2025.11.20 16:14:39\",\n  \"token\": \"sendCmdUp\"\n}";
        String payload19 = "{\n  \"body\": {\n    \"body\": {\n      \"chId\": \"SMEMS_PVInfoReport_t\",\n      \"comType\": 1,\n      \"driver\": \"tcpServerEMS\",\n      \"groupList\": [],\n      \"pointList\": [\n        {\n          \"appType\": 1,\n          \"pId\": 1,\n          \"val\": \"2\"\n        },\n        {\n          \"appType\": 1,\n          \"pId\": 2,\n          \"val\": \"2\"\n        },\n        {\n          \"appType\": 1,\n          \"pId\": 3,\n          \"val\": \"0\"\n        },\n        {\n          \"appType\": 1,\n          \"pId\": 4,\n          \"val\": \"0\"\n        },\n        {\n          \"appType\": 1,\n          \"pId\": 5,\n          \"val\": \"0\"\n        },\n        {\n          \"appType\": 1,\n          \"pId\": 6,\n          \"val\": \"0\"\n        },\n        {\n          \"appType\": 1,\n          \"pId\": 7,\n          \"val\": \"0\"\n        }\n      ]\n    },\n    \"cmd\": \"44\",\n    \"devId\": \"TcpEMSDev\",\n    \"platformId\": \"\",\n    \"point\": \"\",\n    \"protCode\": \"0\",\n    \"type\": \"\"\n  },\n  \"timestamp\": \"2025.11.20 16:14:38\",\n  \"token\": \"sendCmdUp\"\n}";

        log.info("枪编号1: " + JSON.parseObject(payload1));
        log.info("枪编号2: " + JSON.parseObject(payload2));
        log.info("枪编号3: " + JSON.parseObject(payload3));
        log.info("枪编号4: " + JSON.parseObject(payload4));
        log.info("枪编号5: " + JSON.parseObject(payload5));
        log.info("枪编号6: " + JSON.parseObject(payload6));
        log.info("枪编号7: " + JSON.parseObject(payload7));
        log.info("枪编号8: " + JSON.parseObject(payload8));
        log.info("枪编号9: " + JSON.parseObject(payload9));
        log.info("枪编号10: " + JSON.parseObject(payload10));
        log.info("枪编号11: " + JSON.parseObject(payload11));
        log.info("枪编号12: " + JSON.parseObject(payload12));
        log.info("枪编号13: " + JSON.parseObject(payload13));
        log.info("枪编号14: " + JSON.parseObject(payload14));
        log.info("枪编号15: " + JSON.parseObject(payload15));
        log.info("V2G设备16: " + JSON.parseObject(payload16));
        log.info("换电站设备17: " + JSON.parseObject(payload17));
        log.info("数据18: " + JSON.parseObject(payload18));
        log.info("数据19: " + JSON.parseObject(payload19));
    }

    //接收推送充电订单信息
    @Override
    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class)
    public Map<String, Object> notificationChargeOrderInfo(String operatorId, String data) {
        Map<String, Object> resultDataMap = Maps.newHashMap();
        if (isNotEmpty(data)) {
            ChargeOrderInfoDto chargeOrderInfoDto = JSONObject.parseObject(data, ChargeOrderInfoDto.class);
            //存储订单记录
            ChargeOrderRecordEntity chargeOrderRecordEntity = new ChargeOrderRecordEntity();
            BeanUtils.copyProperties(chargeOrderInfoDto, chargeOrderRecordEntity);
            chargeOrderRecordEntity.setChargeDetailsObject(JSON.toJSONString(chargeOrderInfoDto.getChargeDetailsDtoList()));
            chargeOrderRecordDao.save(chargeOrderRecordEntity);
            //获取电桩编码和枪编码
            JSONObject jsonObject = this.getPileCodeAndGunCode(operatorId, chargeOrderInfoDto.getConnectorId());
            String pileCode = jsonObject.getString("pileCode");
            String gunCode = jsonObject.getString("gunCode");
            //获取电桩编码和枪编码失败 直接返回失败
            if (StringUtil.isEmpty(pileCode) || StringUtil.isEmpty(gunCode)) {
                resultDataMap.put("StartChargeSeq", chargeOrderInfoDto.getStartChargeSeq());
                resultDataMap.put("ConnectorID", chargeOrderInfoDto.getConnectorId());
                resultDataMap.put("ConfirmResult", 1);
                return resultDataMap;
            }
            //存储订单信息
            InterflowOrderRecordDto interflowOrderRecordDto = new InterflowOrderRecordDto();
            //根据订单编码查询订单信息
            ResponseResult<InterflowOrderRecordDto> orderRecordResult = togetherService.queryOrderRecordByOrderCode(chargeOrderInfoDto.getStartChargeSeq());
            if (orderRecordResult.isSuccess() && isNotEmpty(orderRecordResult.getData())) {
                interflowOrderRecordDto = orderRecordResult.getData();
            }
            interflowOrderRecordDto.setOrderNum(chargeOrderInfoDto.getStartChargeSeq());
            interflowOrderRecordDto.setPileCode(pileCode);
            if (StringUtil.isNotEmpty(gunCode)) {
                interflowOrderRecordDto.setGunCode(Integer.parseInt(gunCode));
            }
            interflowOrderRecordDto.setStartTime(chargeOrderInfoDto.getStartTime());
            interflowOrderRecordDto.setEndTime(chargeOrderInfoDto.getEndTime());
            interflowOrderRecordDto.setTotalQt(chargeOrderInfoDto.getTotalPower());
            interflowOrderRecordDto.setRunMode(0);
            interflowOrderRecordDto.setPlatformLogo(LocalParamVo.CITY_CHARGE_SOURCE_PLATFORM_ID);
            interflowOrderRecordDto.setOrderStatus(2);
            interflowOrderRecordDto.setTotalElect(BigDecimal.valueOf(chargeOrderInfoDto.getTotalElecMoney()));
            interflowOrderRecordDto.setTotalFee(BigDecimal.valueOf(chargeOrderInfoDto.getTotalSeviceMoney()));
            interflowOrderRecordDto.setTotalCost(BigDecimal.valueOf(chargeOrderInfoDto.getTotalMoney()));
            interflowOrderRecordDto.setBusVin(chargeOrderInfoDto.getVin());
            interflowOrderRecordDto.setStopDetailReason(getInterflowStopReason(chargeOrderInfoDto.getStopReason()));
            interflowOrderRecordDto.setTimeFrameNum(chargeOrderInfoDto.getSumPeriod());
            interflowOrderRecordDto.setStarter(2);
            //转换充电明细信息
            List<ChargeDetailsDto> chargeDetailsDtoList = chargeOrderInfoDto.getChargeDetailsDtoList();
            if (CollectionUtils.isNotEmpty(chargeDetailsDtoList)) {
                interflowOrderRecordDto.setChargingDetailsList(chargeDetailsDtoList.stream().map(chargeDetailsDto -> {
                    InterflowOrderRecordDto.ChargingDetails chargingDetails = new InterflowOrderRecordDto.ChargingDetails();
                    if (StringUtil.isNotEmpty(chargeDetailsDto.getElecPrice())) {
                        chargingDetails.setElectPrice(BigDecimal.valueOf(chargeDetailsDto.getElecPrice()));
                    }
                    if (StringUtil.isNotEmpty(chargeDetailsDto.getServicePrice())) {
                        chargingDetails.setServicePrice(BigDecimal.valueOf(chargeDetailsDto.getServicePrice()));
                    }
                    chargingDetails.setChargeStartTime(chargeDetailsDto.getDetailStartTime());
                    chargingDetails.setChargeEndTime(chargeDetailsDto.getDetailEndTime());
                    chargingDetails.setRechargeQt(chargeDetailsDto.getDetailPower());
                    if (StringUtil.isNotEmpty(chargeDetailsDto.getDetailElecMoney())) {
                        chargingDetails.setElectMoney(BigDecimal.valueOf(chargeDetailsDto.getDetailElecMoney()));
                    }
                    if (StringUtil.isNotEmpty(chargeDetailsDto.getDetailServiceMoney())) {
                        chargingDetails.setServiceMoney(BigDecimal.valueOf(chargeDetailsDto.getDetailServiceMoney()));
                    }
                    chargingDetails.setTariffType(1);
                    return chargingDetails;
                }).collect(Collectors.toList()));
            }
            //把订单信息存入订单记录表
            togetherService.saveOrUpdateOrderRecord(interflowOrderRecordDto);
            resultDataMap.put("StartChargeSeq", chargeOrderInfoDto.getStartChargeSeq());
            resultDataMap.put("ConnectorID", chargeOrderInfoDto.getConnectorId());
            resultDataMap.put("ConfirmResult", 0);
        } else {
            resultDataMap.put("ConfirmResult", 1);
        }
        return resultDataMap;
    }

    //接收推送设备状态变化信息
    @Override
    public Map<String, Object> notificationStationStatus(String operatorId, String data) {
        Map<String, Object> resultDataMap = Maps.newHashMap();
        if (isNotEmpty(data)) {
            JSONObject paramJsonData = JSONObject.parseObject(data);
            if (paramJsonData.containsKey("ConnectorStatusInfo")) {
                ConnectorStatusInfoDto connectorStatusInfo = JSONObject.parseObject(paramJsonData.getString("ConnectorStatusInfo"), ConnectorStatusInfoDto.class);
                //存储设备状态变化记录
                ConnectorStatusRecordEntity connectorStatusRecordEntity = new ConnectorStatusRecordEntity();
                BeanUtils.copyProperties(connectorStatusInfo, connectorStatusRecordEntity);
                connectorStatusRecordDao.save(connectorStatusRecordEntity);
                //获取电桩编码和枪编码
                JSONObject jsonObject = this.getPileCodeAndGunCode(operatorId, connectorStatusInfo.getConnectorId());
                String pileCode = jsonObject.getString("pileCode");
                String gunCode = jsonObject.getString("gunCode");
                if (StringUtil.isEmpty(pileCode) || StringUtil.isEmpty(gunCode)) {
                    resultDataMap.put("Status", 1);
                    return resultDataMap;
                }
                RedisGeneralUtil.executePile(pileCode, () -> {
                    //查询电桩实时数据
                    PileRealModel pileRealModel = RedisGeneralUtil.getPileRealModel(pileCode);
                    if (isNotEmpty(pileRealModel) && isNotEmpty(pileRealModel.getPileCode())) {
                        //获取电枪状态信息
                        Map<String, PileRealModel.GunRealModel> gunRealModelMap = pileRealModel.getGunRealModelMap();
                        if (!gunRealModelMap.isEmpty() && gunRealModelMap.containsKey(gunCode)) {
                            pileRealModel.setWorkStatus(getInterflowPileWorkStatus(connectorStatusInfo.getStatus()));
                            pileRealModel.setOriginalStatus(getInterflowPileOriginalStatus(connectorStatusInfo.getStatus()));
                            PileRealModel.GunRealModel gunRealModel = gunRealModelMap.get(gunCode);
                            gunRealModel.setGunStatus(getInterflowGunWorkStatus(connectorStatusInfo.getStatus()));
                            gunRealModel.setGunOriginalStatus(getInterflowGunOriginalStatus(connectorStatusInfo.getStatus()));
                            gunRealModelMap.put(gunCode, gunRealModel);
                            pileRealModel.setGunRealModelMap(gunRealModelMap);
                            //更改缓存中的数据
                            pileRealModel.setDateTime(localDateTimeToStr(LocalDateTime.now()));
                            RedisGeneralUtil.setPileRealModel(pileCode, pileRealModel);
                            resultDataMap.put("Status", 0);
                        }
                    } else {
                        pileRealModel = new PileRealModel();
                        //电枪实时数据对象
                        PileRealModel.GunRealModel gunRealModel = new PileRealModel.GunRealModel();
                        //电枪实时数据map
                        Map<String, PileRealModel.GunRealModel> gunRealModelMap = Maps.newHashMap();

                        pileRealModel.setWorkStatus(getInterflowPileWorkStatus(connectorStatusInfo.getStatus()));
                        pileRealModel.setOriginalStatus(getInterflowPileOriginalStatus(connectorStatusInfo.getStatus()));
                        pileRealModel.setPileCode(pileCode);
                        gunRealModel.setGunStatus(getInterflowGunWorkStatus(connectorStatusInfo.getStatus()));
                        gunRealModel.setGunOriginalStatus(getInterflowGunOriginalStatus(connectorStatusInfo.getStatus()));
                        gunRealModel.setGunCode(gunCode);
                        gunRealModelMap.put(gunCode, gunRealModel);
                        pileRealModel.setGunRealModelMap(gunRealModelMap);
                        //保存入redis缓存
                        pileRealModel.setDateTime(localDateTimeToStr(LocalDateTime.now()));
                        RedisGeneralUtil.setPileRealModel(pileCode, pileRealModel);
                        resultDataMap.put("Status", 0);
                    }

                    //根据设备编码查询设备缓存信息
                    DeviceModel deviceModel = RedisDeviceUtil.getDevice(pileCode);
                    if (isNotEmpty(deviceModel) && isNotEmpty(deviceModel.getDeviceNumber())) {
                        //如果当前状态不等于在线，则更新当前状态
                        if (!deviceModel.getTxStatus().equals(pileRealModel.getWorkStatus()) && !(pileRealModel.getWorkStatus() == -1 && deviceModel.getTxStatus() == 0)) {
                            deviceModel.setTxStatus(pileRealModel.getWorkStatus());
                            RedisDeviceUtil.setDevice(pileCode, deviceModel);
                        }
                    }
                });
            }

        }
        return resultDataMap;
    }

    //城市充电功率控制
    @Override
    public ResponseResult<String> interflowPowerControl(String pileCode, Integer gunCode, Double outPower) {
        //根据电桩编码查询设备数据
        ResponseResult<Map<String, DeviceBasicInfoDto>> deviceBasicInfoByCodes = deviceService.findDeviceBasicInfoByCodes(Collections.singletonList(pileCode));
        if (deviceBasicInfoByCodes.isSuccess() && !deviceBasicInfoByCodes.getData().isEmpty() && deviceBasicInfoByCodes.getData().containsKey(pileCode)) {
            DeviceBasicInfoDto deviceBasicInfoDto = deviceBasicInfoByCodes.getData().get(pileCode);

            //根据设备编号查询站点id
            RequestCommonVo interFlowField = null;
            Optional<InterflowEquipmentEntity> optional = interflowEquipmentDao.findById(pileCode);
            if (optional.isPresent()) {
                //获取站点id
                String stationId = optional.get().getStationId();
                if (StringUtil.isNotEmpty(stationId)) {
                    //根据站点id查询站点信息
                    Optional<InterflowStationEntity> siteEntityOptional = interflowStationDao.findById(stationId);
                    if (siteEntityOptional.isPresent()) {
                        String platformId = siteEntityOptional.get().getPlatformId();
                        if (isEmpty(platformId)) {
                            log.error("未获取到站点平台id, 电桩编号:{}", pileCode);
                            return ResponseResult.error("未获取到站点平台id");
                        }
                        interFlowField = SubstationRunner.getInterFlowField(platformId);
                        if (interFlowField == null) {
                            log.error("未获取到平台运营商数据, 电桩编号:{}", pileCode);
                            return ResponseResult.error("未获取到平台运营商数据");
                        }
                    }
                }
            }
            //组装下发功率控制参数
            Map<String, Object> paramMap = Maps.newHashMap();
            paramMap.put("EquipmentID", pileCode);
            paramMap.put("StationID", deviceBasicInfoDto.getSiteId());
            paramMap.put("ConnectorID", pileCode + "_" + (gunCode - 1));
            paramMap.put("ControlPowerValue", outPower);
            paramMap.put("ControlDurationValue", "");
            //调用下发功率控制接口
            String data = InterflowRequestUtil.queryData(interFlowField, InterflowMethodVo.NOTIFICATION_POWER_CONTROL, JSON.toJSONString(paramMap)).getData();
            JSONObject jsonObject = JSONObject.parseObject(data);
            if (isNotEmpty(jsonObject)) {
                //获取响应结果
                String result = jsonObject.getString("Status");
                //判断响应结果
                if ("0".equals(result)) {
                    //成功
                    return ResponseResult.ok(ResponseResult.SUCCESS);
                } else {
                    //失败
                    return ResponseResult.error(ResponseResult.FAIL);
                }
            }
        }
        return ResponseResult.error(ResponseResult.FAIL);
    }

    @Override
    public ResponseResult<Map<String, List<GunStatusInfoDto>>> queryStationStatus(String platformId, List<String> stationIds) {
        Map<String, List<GunStatusInfoDto>> resultMap = Maps.newHashMap();
        //查询城市充电站点设备状态接口
        Map<String, Object> paramMap = Maps.newHashMap();
        paramMap.put("StationIDs", stationIds);
        ResponseDto responseDto = InterflowRequestUtil.queryData(SubstationRunner.getInterFlowField(platformId), InterflowMethodVo.QUERY_STATION_STATUS,
                JSON.toJSONString(paramMap));
        if (isNotEmpty(responseDto) && responseDto.getRet() == 0) {
            JSONObject jsonObject = JSONObject.parseObject(responseDto.getData());
            //获取总数
            Integer total = jsonObject.getInteger("Total");
            if (total > 0) {
                //获取站点信息
                JSONArray stationStatusInfos = jsonObject.getJSONArray("StationStatusInfos");
                stationStatusInfos.forEach(item -> {
                    JSONObject stationStatusInfo = (JSONObject) item;
                    resultMap.put(stationStatusInfo.getString("StationID"), JSON.parseArray(stationStatusInfo.getString("ConnectorStatusInfos"), ConnectorStatusInfoDto.class)
                            .stream().map(connectorStatusInfoDto -> {
                                GunStatusInfoDto gunStatusInfoDto = new GunStatusInfoDto();
                                JSONObject jsonObject1 = this.getPileCodeAndGunCode(platformId, connectorStatusInfoDto.getConnectorId());
                                gunStatusInfoDto.setEquipmentId(jsonObject1.getString("pileCode"));
                                gunStatusInfoDto.setGunCode(jsonObject1.getString("gunCode"));
                                gunStatusInfoDto.setGunStatus(getInterflowGunWorkStatus(connectorStatusInfoDto.getStatus()));
                                gunStatusInfoDto.setLockStatus(connectorStatusInfoDto.getLockStatus());
                                gunStatusInfoDto.setParkStatus(connectorStatusInfoDto.getParkStatus());
                                return gunStatusInfoDto;
                            }).collect(Collectors.toList()));
                });

            }
            return ResponseResult.ok(resultMap);
        }
        return ResponseResult.error(responseDto.getMsg(), responseDto.getRet(), null);
    }

    @Override
    public ResponseResult<List<PowerControlResDto>> interflowBatchPowerControl(List<PowerControlParamVo> powerControlParamVos) {
        List<PowerControlResDto> resultList = Lists.newArrayList();
        //根据电桩编码查询设备数据
        List<String> pileCodes = powerControlParamVos.stream().map(PowerControlParamVo::getPileCode).filter(StringUtil::isNotEmpty).distinct().collect(Collectors.toList());
        ResponseResult<Map<String, DeviceBasicInfoDto>> deviceBasicInfoByCodes = deviceService.findDeviceBasicInfoByCodes(pileCodes);

        //根据多个电桩编号查询平台参数
        //根据设备编号查询站点id
        Map<String, RequestCommonVo> interFlowFieldMap = Maps.newHashMap();
        List<InterflowEquipmentEntity> equipmentList = interflowEquipmentDao.findAllById(pileCodes);
        if (CollectionUtils.isNotEmpty(equipmentList)) {
            Map<String, String> pileStationIdMap = equipmentList.stream().collect(Collectors.toMap(InterflowEquipmentEntity::getEquipmentId, InterflowEquipmentEntity::getStationId,
                    (k1, k2) -> k1));
            List<InterflowStationEntity> stationList = interflowStationDao.findAllById(new HashSet<>(pileStationIdMap.values()));
            if (CollectionUtils.isNotEmpty(stationList)) {
                Map<String, String> stationPlatformIdMap = stationList.stream().collect(Collectors.toMap(InterflowStationEntity::getStationId, InterflowStationEntity::getPlatformId,
                        (k1, k2) -> k1));
                pileStationIdMap.forEach((pileCode, stationId) -> {
                    if (isNotEmpty(stationPlatformIdMap.get(stationId))) {
                        interFlowFieldMap.put(pileCode, SubstationRunner.getInterFlowField(stationPlatformIdMap.get(stationId)));
                    }
                });
            }
        }

        //循环调用功率控制接口
        powerControlParamVos.forEach(powerControlParamVo -> {
            PowerControlResDto powerControlResDto = new PowerControlResDto();
            BeanUtils.copyProperties(powerControlParamVo, powerControlResDto);
            powerControlResDto.setStatus(1);
            //如果查询到当前电桩信息，并且功率控制值不为空
            if (deviceBasicInfoByCodes.isSuccess() && !deviceBasicInfoByCodes.getData().isEmpty() &&
                    deviceBasicInfoByCodes.getData().containsKey(powerControlParamVo.getPileCode()) &&
                    isNotEmpty(powerControlParamVo.getOutPower())) {
                DeviceBasicInfoDto deviceBasicInfoDto = deviceBasicInfoByCodes.getData().get(powerControlParamVo.getPileCode());
                //电桩编码
                String pileCode = powerControlParamVo.getPileCode();
                //电枪编码
                Integer gunCode = powerControlParamVo.getGunCode();
                //功率控制值
                Double outPower = powerControlParamVo.getOutPower();
                //下发控制时长
                Integer controlDurationValue = powerControlParamVo.getControlDurationValue();
                //组装下发功率控制参数
                Map<String, Object> paramMap = Maps.newHashMap();
                paramMap.put("EquipmentID", pileCode);
                paramMap.put("StationID", deviceBasicInfoDto.getSiteId());
                paramMap.put("ConnectorID", pileCode + "_" + (gunCode - 1));
                paramMap.put("ControlPowerValue", outPower);
                paramMap.put("ControlDurationValue", controlDurationValue);
                //调用下发功率控制接口
                if (interFlowFieldMap.containsKey(pileCode)) {
                    String data = InterflowRequestUtil.queryData(interFlowFieldMap.get(pileCode), InterflowMethodVo.NOTIFICATION_POWER_CONTROL, JSON.toJSONString(paramMap)).getData();
                    JSONObject jsonObject = JSONObject.parseObject(data);
                    if (isNotEmpty(jsonObject)) {
                        //获取响应结果
                        String result = jsonObject.getString("Status");
                        powerControlResDto.setStatus(Integer.parseInt(result));
                    }
                }
            }
            resultList.add(powerControlResDto);
        });
        return ResponseResult.ok(resultList);
    }

    private JSONObject getPileCodeAndGunCode(String operatorId, String connectorId) {
        JSONObject result = new JSONObject();
        String pileCode;
        String gunCode;
        if (StringUtil.isNotEmpty(connectorId)) {
            if (connectorId.contains(FileUtil.UNDERLINE)) {
                String[] split = connectorId.split(FileUtil.UNDERLINE);
                pileCode = split[0];
                if (split.length > 1 && isNumber(split[1])) {
                    gunCode = String.valueOf(Integer.parseInt(split[1]) + 1);
                } else {
                    gunCode = null;
                }
            } else {
                String key = operatorId + FileUtil.UNDERLINE + connectorId;
                if (!connectorPileStatueMap.containsKey(key) && !connectorPileCodeMap.containsKey(key)) {
                    connectorPileStatueMap.put(key, true); //标记查库 不管查没查到 都不再去查库 以免一直查库占资源
                    List<InterflowConnectorEntity> interflowConnectorList = interflowConnectorDao.findAll(Example.of(InterflowConnectorEntity
                            .builder().connectorId(operatorId).connectorId(connectorId).build()));
                    if (CollectionUtils.isNotEmpty(interflowConnectorList)) {
                        pileCode = interflowConnectorList.get(0).getEquipmentId();
                        connectorPileCodeMap.put(key, pileCode);
                        connectorPileStatueMap.remove(key);
                    }
                }
                pileCode = connectorPileCodeMap.get(key);
                gunCode = connectorId;
            }
        } else {
            gunCode = null;
            pileCode = null;
        }
        result.put("pileCode", pileCode);
        result.put("gunCode", gunCode);
        return result;
    }


//    public static void main(String[] args) {
//        String test = "{\n" +
//                "        \"StartChargeSeq\": \"12345678987654321\",\n" +
//                "        \"StartChargeSeqStat\": 2,\n" +
//                "        \"ConnectorlD\": \"10000000000000000000003_0\",\n" +
//                "        \"ConnectorStatus\": 3,\n" +
//                "        \"CurrentA\": 32.3,\n" +
//                "        \"CurrentB\": 30.2,\n" +
//                "        \"CurrentC\": 29.1,\n" +
//                "        \"VbltageA\": 25.6,\n" +
//                "        \"VbltageB\": 24.5,\n" +
//                "        \"VbltageC\": 23.1,\n" +
//                "        \"Soc\": 50,\n" +
//                "        \"StartTime\": \"2024-08-29 17:10:00\",\n" +
//                "        \"EndTime\": \"\",\n" +
//                "        \"TotalPower\": 14.3,\n" +
//                "        \"ElecMoney\": 3.2,\n" +
//                "        \"SeviceMoney\": 2.8,\n" +
//                "        \"TotalMoney\": 6.0,\n" +
//                "        \"BclNeedVoltage\": 45.9,\n" +
//                "        \"BclNeedCurrent\": 46.1,\n" +
//                "        \"SumPeriod\": 4,\n" +
//                "        \"ChargeDetails\": [\n" +
//                "            {\n" +
//                "                \"DetailStartTime\": \"2024-08-29 17:10:00\",\n" +
//                "                \"DetailEndTime\": \"2024-08-29 17:20:00\",\n" +
//                "                \"ElecPrice\": 0.5,\n" +
//                "                \"SevicePrice\": 0.3,\n" +
//                "                \"DetailPower\": 4.6,\n" +
//                "                \"DetailElecMoney\": 2.3,\n" +
//                "                \"DetailSeviceMoney\": 1.38,\n" +
//                "                \"EquipmentLat\": 31.717877,\n" +
//                "                \"Power\": 621.52,\n" +
//                "                \"EquipmentName\": \"城市充电测试接入桩\",\n" +
//                "            }\n" +
//                "        ]\n" +
//                "    }";
//
////        notificationEquipChargeStatus1(test);
//        List<StationInfoDto> stationInfoDtos = JSON.parseArray(test, StationInfoDto.class);
//        Map<String, String> parseAddress = parseAddress("浙江省绍兴市柯桥区湖塘街道湖庵村");
//        String province = parseAddress.get("province");
//        String city = parseAddress.get("city");
//        String county = parseAddress.get("county");
//    }
}
