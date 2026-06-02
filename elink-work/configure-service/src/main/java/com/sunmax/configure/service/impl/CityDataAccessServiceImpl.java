package com.sunmax.configure.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.sunmax.common.dto.device.DeviceBasicInfoDto;
import com.sunmax.common.dto.device.DeviceGunInfoDto;
import com.sunmax.common.dto.device.SiteInfoDto;
import com.sunmax.common.dto.operate.InterflowOrderRecordDto;
import com.sunmax.common.dto.operate.OrderRecordDto;
import com.sunmax.common.dto.system.SiteOperateDto;
import com.sunmax.common.model.general.PileRealModel;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.util.StringUtil;
import com.sunmax.common.vo.SiteFieldParamVo;
import com.sunmax.configure.dto.city.*;
import com.sunmax.configure.service.CityDataAccessService;
import com.sunmax.configure.service.feign.DeviceService;
import com.sunmax.configure.service.feign.TogetherService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.sunmax.common.config.redis.RedisGeneralUtil.getPileRealModel;
import static com.sunmax.common.config.redis.RedisGeneralUtil.getPileRealModelList;
import static com.sunmax.common.util.DateUtil.strToLocalDateTime;
import static com.sunmax.common.util.DoubleUtil.getToDouble;
import static com.sunmax.common.util.StringUtil.*;

@Slf4j
@Service
public class CityDataAccessServiceImpl implements CityDataAccessService {

    @Autowired
    private DeviceService deviceService;

    @Autowired
    private TogetherService togetherService;

    /**
     * 查询充电站信息
     * @param lastQueryTime 上次查询时间
     * @param pageNo        查询页码
     * @param pageSize      每页数量
     * @param siteOperateList    数据转发配置中站点和运营商id列表
     * @return
     */
    @Override
    public Map<String, Object> queryStationsInfo(String lastQueryTime, Integer pageNo, Integer pageSize, List<SiteOperateDto> siteOperateList) {
        Map<String, Object> dataMap = Maps.newHashMap();
        if (StringUtil.isEmpty(pageNo)) {
            pageNo = 1;
        }
        if (StringUtil.isEmpty(pageSize)) {
            pageSize = 10;
        }
        //页码总数
        int pageCount = 0;
        //总记录条数
        int itemSize = 0;
        List<StationInfoDto> stationInfos = Lists.newArrayList();
        //判断站点id列表有没有值，如果有值则先根据站点id查询全部数据，如果没值则说明当前运营商没在平台数据转发中配置
        if (CollectionUtils.isNotEmpty(siteOperateList)) {
            List<String> siteIdList = siteOperateList.stream().map(SiteOperateDto::getSiteId).distinct().collect(Collectors.toList());

            //把站点和运营商id信息转成map
            Map<String, String> siteOperateMap = siteOperateList.stream().collect(Collectors.toMap(SiteOperateDto::getSiteId, SiteOperateDto::getOperateId, (k1, k2) -> k1));
            //站点列表信息
            List<SiteInfoDto> siteInfoDtoList = Lists.newArrayList();
            ResponseResult<Map<String, SiteInfoDto>> siteBasicInfoByIds = deviceService.findSiteBasicInfoByIds(siteIdList);
            if (siteBasicInfoByIds.isSuccess() && !siteBasicInfoByIds.getData().isEmpty()) {
                siteInfoDtoList = Lists.newArrayList(siteBasicInfoByIds.getData().values());
            }
            //设备列表信息
            List<DeviceBasicInfoDto> deviceBasicInfoDtoList = Lists.newArrayList();
            //电枪列表信息
            List<DeviceGunInfoDto> deviceGunInfoDtoList = Lists.newArrayList();
            //根据多个站点id，查询站点下电桩设备列表信息
            ResponseResult<Map<String, List<DeviceBasicInfoDto>>> deviceBasicInfoResult = deviceService.findDeviceBasicInfoBySiteIds(siteIdList, 1);
            /*if (deviceBasicInfoResult.isSuccess() && !deviceBasicInfoResult.getData().isEmpty()) {
                List<DeviceBasicInfoDto> finalDeviceBasicInfoDtoList = deviceBasicInfoDtoList;
                deviceBasicInfoResult.getData().forEach((k, v) -> finalDeviceBasicInfoDtoList.addAll(v));
                //根据多个设备id，查询设备关联电枪数据
                List<String> deviceIdList = deviceBasicInfoDtoList.stream().map(DeviceBasicInfoDto::getId).distinct().collect(Collectors.toList());
                ResponseResult<Map<String, List<DeviceGunInfoDto>>> deviceGunInfoResult = deviceService.findDeviceGunInfoByDeviceIds(deviceIdList);
                if (deviceGunInfoResult.isSuccess() && !deviceGunInfoResult.getData().isEmpty()) {
                    List<DeviceGunInfoDto> finalDeviceGunInfoDtoList1 = deviceGunInfoDtoList;
                    deviceGunInfoResult.getData().forEach((k, v) -> finalDeviceGunInfoDtoList1.addAll(v));
                }
            }*/
            getDeviceAndGunInfo(deviceBasicInfoDtoList, deviceGunInfoDtoList, deviceBasicInfoResult);
            //如果传了上次查询时间，则按照时间过滤
            if (StringUtil.isNotEmpty(lastQueryTime)) {
                //先查询有没有修改时间在指定查询时间之后的电枪数据
                if (CollectionUtils.isNotEmpty(deviceGunInfoDtoList)) {
                    LocalDateTime queryTime = strToLocalDateTime(lastQueryTime);
                    deviceGunInfoDtoList = deviceGunInfoDtoList.stream().filter(item -> item.getUpdateTime().isAfter(queryTime)).collect(Collectors.toList());
                    if (CollectionUtils.isNotEmpty(deviceGunInfoDtoList)) {
                        // 根据电枪信息中的设备id，过滤出设备信息
                        List<DeviceGunInfoDto> finalDeviceGunInfoDtoList = deviceGunInfoDtoList;
                        deviceBasicInfoDtoList = deviceBasicInfoDtoList.stream().filter(item -> finalDeviceGunInfoDtoList.stream().anyMatch(gunItem -> gunItem.getDeviceId().equals(item.getId()))).collect(Collectors.toList());
                        // 根据设备信息中的站点id，过滤出站点信息
                        List<DeviceBasicInfoDto> finalDeviceBasicInfoDtoList1 = deviceBasicInfoDtoList;
                        siteInfoDtoList = siteInfoDtoList.stream().filter(item -> finalDeviceBasicInfoDtoList1.stream().anyMatch(basicItem -> basicItem.getSiteId().equals(item.getId()))).collect(Collectors.toList());
                    } else {
                        // 过滤出修改时间在查询时间之后的设备数据
                        deviceBasicInfoDtoList = deviceBasicInfoDtoList.stream().filter(item -> item.getUpdateTime().isAfter(queryTime)).collect(Collectors.toList());
                        // 过滤出修改时间在查询时间之后的站点数据
                        siteInfoDtoList = siteInfoDtoList.stream().filter(item -> item.getUpdateTime().isAfter(queryTime)).collect(Collectors.toList());
                    }
                }
            }
            if (CollectionUtils.isNotEmpty(siteInfoDtoList)) {
                //把设备信息列表根据站点id分组
                Map<String, List<DeviceBasicInfoDto>> deviceBasicInfoDtoMap = CollectionUtils.isNotEmpty(deviceBasicInfoDtoList) ? deviceBasicInfoDtoList.stream()
                                .collect(Collectors.groupingBy(DeviceBasicInfoDto::getSiteId)) : Collections.emptyMap();
                //把电枪信息列表根据设备id分组
                Map<String, List<DeviceGunInfoDto>> deviceGunInfoDtoMap = CollectionUtils.isNotEmpty(deviceGunInfoDtoList) ? deviceGunInfoDtoList.stream()
                                .collect(Collectors.groupingBy(DeviceGunInfoDto::getDeviceId)) : Collections.emptyMap();
                //循环组装数据
                List<StationInfoDto> finalStationInfos = stationInfos;
                siteInfoDtoList.forEach(siteInfoDto -> {
                    StationInfoDto stationInfoDto = new StationInfoDto();
                    //获取站点扩展属性信息
                    String siteReadwriteObject = siteInfoDto.getSiteReadwriteObject();
                    if (StringUtil.isNotEmpty(siteReadwriteObject)) {
                        StationInfoDto stationInfoJson = JSONObject.parseObject(siteReadwriteObject, StationInfoDto.class);
                        BeanUtils.copyProperties(stationInfoJson, stationInfoDto);

                        Map<String, Object> readwriteMap = Maps.newHashMap();
                        if (StringUtil.isNotEmpty(siteReadwriteObject)) {
                            readwriteMap = JSON.parseObject(siteReadwriteObject, new TypeReference<Map<String, Object>>() {});
                        }
                        //地址对象
                        Map<String, Object> localtionReadwriteMap = Maps.newHashMap();
                        if (readwriteMap.containsKey(SiteFieldParamVo.LOCATION)) {
                            localtionReadwriteMap = JSON.parseObject(JSON.toJSONString(readwriteMap.get(SiteFieldParamVo.LOCATION)), new TypeReference<Map<String, Object>>() {});
                        }
                        //经度
                        if (!localtionReadwriteMap.isEmpty() && localtionReadwriteMap.containsKey(SiteFieldParamVo.LONGITUDE) && StringUtil.isNotEmpty(localtionReadwriteMap.get(SiteFieldParamVo.LONGITUDE))) {
                            stationInfoDto.setStationLng(Double.parseDouble(localtionReadwriteMap.get(SiteFieldParamVo.LONGITUDE).toString()));
                        }
                        //纬度
                        if (!localtionReadwriteMap.isEmpty() && localtionReadwriteMap.containsKey(SiteFieldParamVo.LATITUDE) && StringUtil.isNotEmpty(localtionReadwriteMap.get(SiteFieldParamVo.LATITUDE))) {
                            stationInfoDto.setStationLat(Double.parseDouble(localtionReadwriteMap.get(SiteFieldParamVo.LATITUDE).toString()));
                        }
                        //详细地址
                        if (!localtionReadwriteMap.isEmpty() && localtionReadwriteMap.containsKey(SiteFieldParamVo.ADDRESS) && StringUtil.isNotEmpty(localtionReadwriteMap.get(SiteFieldParamVo.ADDRESS))) {
                            stationInfoDto.setAddress(localtionReadwriteMap.get(SiteFieldParamVo.ADDRESS).toString());
                        }
                    }
                    //获取运营商id
                    stationInfoDto.setOperatorId(siteOperateMap.get(siteInfoDto.getId()));
                    stationInfoDto.setStationId(siteInfoDto.getId());
                    stationInfoDto.setStationName(siteInfoDto.getSiteName());
                    stationInfoDto.setRemark(siteInfoDto.getSiteDescribe());
                    //转换站点状态
                    stationInfoDto.setStationStatus(getStationStatusOther(siteInfoDto.getSiteStatus()));
                    //转换站点照片
                    if (StringUtil.isNotEmpty(siteInfoDto.getImagePath())) {
                        stationInfoDto.setPictures(JSON.toJSONString(Arrays.stream(siteInfoDto.getImagePath().split(",")).collect(Collectors.toList())));
                    }
                    //获取站点下设备列表
                    if (!deviceBasicInfoDtoMap.isEmpty() && deviceBasicInfoDtoMap.containsKey(siteInfoDto.getId())) {
                        stationInfoDto.setEquipmentInfos(deviceBasicInfoDtoMap.get(siteInfoDto.getId()).stream().map(deviceBasicInfoDto -> {
                            EquipmentInfoDto equipmentInfoDto = new EquipmentInfoDto();
                            //获取设备扩展属性字段
                            String readwriteObject = deviceBasicInfoDto.getReadwriteObject();
                            if (StringUtil.isNotEmpty(readwriteObject)) {
                                EquipmentInfoDto equipmentInfoJson = JSONObject.parseObject(readwriteObject, EquipmentInfoDto.class);
                                BeanUtils.copyProperties(equipmentInfoJson, equipmentInfoDto);
                            }
                            equipmentInfoDto.setEquipmentId(deviceBasicInfoDto.getDeviceNumber());
                            equipmentInfoDto.setEquipmentName(deviceBasicInfoDto.getDeviceName());
                            equipmentInfoDto.setEquipmentType(getDeviceType(deviceBasicInfoDto.getTypeId(), 2));
                            equipmentInfoDto.setEquipmentLat(stationInfoDto.getStationLat());
                            equipmentInfoDto.setEquipmentLng(stationInfoDto.getStationLng());
                            //获取设备下电枪列表
                            if (!deviceGunInfoDtoMap.isEmpty() && deviceGunInfoDtoMap.containsKey(deviceBasicInfoDto.getId())) {
                                equipmentInfoDto.setConnectorInfos(deviceGunInfoDtoMap.get(deviceBasicInfoDto.getId()).stream().map(deviceGunInfoDto -> {
                                    ConnectorInfoDto connectorInfoDto = new ConnectorInfoDto();
                                    String equipmentId = equipmentInfoDto.getEquipmentId();
                                    String gunCode = equipmentId.substring(equipmentId.length() - 12) + deviceGunInfoDto.getGunCode();
                                    connectorInfoDto.setConnectorId(gunCode);
                                    connectorInfoDto.setConnectorName(deviceGunInfoDto.getGunName());
                                    connectorInfoDto.setConnectorType(deviceGunInfoDto.getType());
                                    connectorInfoDto.setCurrent(Long.valueOf(deviceGunInfoDto.getRatedCurrent()));
                                    connectorInfoDto.setPower(deviceGunInfoDto.getRatedPower());
                                    connectorInfoDto.setVoltageLowerLimits(Long.valueOf(deviceGunInfoDto.getVoltageLowerLimits()));
                                    connectorInfoDto.setVoltageUpperLimits(Long.valueOf(deviceGunInfoDto.getVoltageUpperLimits()));
                                    connectorInfoDto.setParkNo(deviceGunInfoDto.getParkNo());
                                    connectorInfoDto.setNationalStandard(deviceGunInfoDto.getNationalStandard());
                                    return connectorInfoDto;
                                }).collect(Collectors.toList()));
                            }
                            return equipmentInfoDto;
                        }).collect(Collectors.toList()));
                    }
                    finalStationInfos.add(stationInfoDto);
                });
            }
        }
        if (CollectionUtils.isNotEmpty(stationInfos)) {
            itemSize = stationInfos.size();
            pageCount = Math.round((float) stationInfos.size() / pageSize) + 1;
            stationInfos = stationInfos.stream().skip((long) (pageNo - 1) *pageSize).limit(pageSize).collect(Collectors.toList());
        }
        dataMap.put("PageNo", pageNo);
        dataMap.put("PageCount", pageCount);
        dataMap.put("ItemSize", itemSize);
        dataMap.put("StationInfos", stationInfos);
        return dataMap;
    }

    @Override
    public Map<String, Object> queryStationStatus(List<String> stationIdList) {
        Map<String, Object> dataMap = Maps.newHashMap();
        List<StationStatusDto> stationStatusInfos = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(stationIdList)) {
            //根据多个站点id,查询站点信息
            ResponseResult<Map<String, SiteInfoDto>> siteBasicInfoByIds = deviceService.findSiteBasicInfoByIds(stationIdList);

            //设备列表信息
            List<DeviceBasicInfoDto> deviceBasicInfoDtoList = Lists.newArrayList();
            //电枪列表信息
            List<DeviceGunInfoDto> deviceGunInfoDtoList = Lists.newArrayList();
            //根据多个站点id，查询站点下电桩设备列表信息
            ResponseResult<Map<String, List<DeviceBasicInfoDto>>> deviceBasicInfoResult = deviceService.findDeviceBasicInfoBySiteIds(stationIdList, 1);
            getDeviceAndGunInfo(deviceBasicInfoDtoList, deviceGunInfoDtoList, deviceBasicInfoResult);
            if (siteBasicInfoByIds.isSuccess() && !siteBasicInfoByIds.getData().isEmpty()) {
                //把设备信息列表根据站点id分组
                Map<String, List<DeviceBasicInfoDto>> deviceBasicInfoDtoMap = CollectionUtils.isNotEmpty(deviceBasicInfoDtoList) ? deviceBasicInfoDtoList.stream()
                        .collect(Collectors.groupingBy(DeviceBasicInfoDto::getSiteId)) : Collections.emptyMap();

                //把电枪信息列表根据设备id分组
                Map<String, List<DeviceGunInfoDto>> deviceGunInfoDtoMap = CollectionUtils.isNotEmpty(deviceGunInfoDtoList) ? deviceGunInfoDtoList.stream()
                        .collect(Collectors.groupingBy(DeviceGunInfoDto::getDeviceId)) : Collections.emptyMap();

                //根据多个电桩编码获取实时数据
                List<String> deviceCodeList = deviceBasicInfoDtoList.stream().map(DeviceBasicInfoDto::getDeviceNumber).collect(Collectors.toList());
                List<PileRealModel> pileRealModelList = getPileRealModelList(deviceCodeList);
                Map<String, PileRealModel> pileRealModelMap = CollectionUtils.isNotEmpty(pileRealModelList) ? pileRealModelList.stream()
                        .collect(Collectors.toMap(PileRealModel::getPileCode, pileRealModel -> pileRealModel, (k1, k2) -> k1)) : Collections.emptyMap();

                //循环站点组装数据
                siteBasicInfoByIds.getData().forEach((k, v) -> {
                    StationStatusDto stationStatusDto = new StationStatusDto();
                    stationStatusDto.setStationId(v.getId());
                    //获取站点下设备列表
                    if (!deviceBasicInfoDtoMap.isEmpty() && deviceBasicInfoDtoMap.containsKey(k)) {
                        List<ConnectorStatusDto> connectorStatusInfos = Lists.newArrayList();
                        deviceBasicInfoDtoMap.get(k).forEach(deviceBasicInfoDto -> {
                            //获取设备下电枪列表
                            if (!deviceGunInfoDtoMap.isEmpty() && deviceGunInfoDtoMap.containsKey(deviceBasicInfoDto.getId())) {
                                deviceGunInfoDtoMap.get(deviceBasicInfoDto.getId()).forEach(deviceGunInfoDto -> {
                                    ConnectorStatusDto connectorStatusDto = new ConnectorStatusDto();
                                    String deviceNumber = deviceBasicInfoDto.getDeviceNumber();
                                    String gunCode = deviceNumber.substring(deviceNumber.length() - 12) + deviceGunInfoDto.getGunCode();
                                    connectorStatusDto.setConnectorId(gunCode);
                                    if (pileRealModelMap.containsKey(deviceNumber)) {
                                        Map<String, PileRealModel.GunRealModel> gunRealModelMap = pileRealModelMap.get(deviceNumber).getGunRealModelMap();
                                        if (!gunRealModelMap.isEmpty() && gunRealModelMap.containsKey(deviceGunInfoDto.getGunCode())) {
                                            PileRealModel.GunRealModel gunRealModel = gunRealModelMap.get(deviceGunInfoDto.getGunCode());
                                            //因为市级不需要放电数据,所以过滤掉放电状态的数据
                                            Integer gunStatus = gunRealModel.getGunStatus();
                                            if (StringUtil.isNotEmpty(gunStatus) && gunStatus != 4 && gunStatus != 5) {
                                                connectorStatusDto.setStatus(getCityGunWorkStatus(gunStatus));
                                                connectorStatusDto.setSoc(gunRealModel.getBatterySoc() != null ? Double.valueOf(gunRealModel.getBatterySoc()) : 0);
                                                //如果当前枪状态在充电中,则计算已充时长.已充电量
                                                if (gunStatus == 2) {
                                                    //已充时长
                                                    connectorStatusDto.setEdtime(gunRealModel.getRunTime() / 60);
                                                    //已充电量
                                                    connectorStatusDto.setEdpq(getToDouble(gunRealModel.getTotalQt(), 3));
                                                }
                                            }
                                        }
                                    }
                                    connectorStatusInfos.add(connectorStatusDto);
                                });
                            }

                        });
                        stationStatusDto.setConnectorStatusInfos(connectorStatusInfos);
                    }
                    stationStatusInfos.add(stationStatusDto);
                });
            }
        }
        dataMap.put("StationStatusInfos", stationStatusInfos);
        return dataMap;
    }

    @Override
    public Map<String, Object> queryEquipChargeStatus(String startChargeSeq, List<String> siteIdList) {
        Map<String, Object> dataMap = Maps.newHashMap();
        ChargeStateDto chargeStateDto = new ChargeStateDto();
        chargeStateDto.setStartChargeSeq(startChargeSeq);
        if (CollectionUtils.isNotEmpty(siteIdList)) {
            //根据订单编码查询订单详情
            ResponseResult<InterflowOrderRecordDto> orderInfoResult = togetherService.queryOrderRecordByOrderCode(startChargeSeq);
            if (orderInfoResult.isSuccess() && StringUtil.isNotEmpty(orderInfoResult.getData())) {
                InterflowOrderRecordDto interflowOrderRecordDto = orderInfoResult.getData();
                String pileCode = interflowOrderRecordDto.getPileCode();
                //根据多个站点id，查询站点下电桩设备数据
                ResponseResult<Map<String, List<DeviceBasicInfoDto>>> deviceBasicInfoResult = deviceService.findDeviceBasicInfoBySiteIds(siteIdList, 1);
                if (deviceBasicInfoResult.isSuccess() && !deviceBasicInfoResult.getData().isEmpty()) {
                    //设备列表信息
                    List<DeviceBasicInfoDto> deviceBasicInfoDtoList = Lists.newArrayList();
                    deviceBasicInfoResult.getData().forEach((k, v) -> deviceBasicInfoDtoList.addAll(v));
                    //检查当前订单的电桩编码是否在电桩设备列表数据中
                    if (deviceBasicInfoDtoList.stream().anyMatch(deviceBasicInfoDto -> deviceBasicInfoDto.getDeviceNumber().endsWith(pileCode))) {
                        setChargeStateDtoFromOrderRecord(chargeStateDto, interflowOrderRecordDto);
                        if (interflowOrderRecordDto.getOrderStatus() == 1) {
                            //查询电桩实时数据
                            PileRealModel pileRealModel = getPileRealModel(pileCode);
                            if (pileRealModel != null && StringUtil.isNotEmpty(pileRealModel.getPileCode())
                                    && !pileRealModel.getGunRealModelMap().isEmpty()
                                    && pileRealModel.getGunRealModelMap().containsKey(String.valueOf(interflowOrderRecordDto.getGunCode()))) {
                                PileRealModel.GunRealModel gunRealModel = pileRealModel.getGunRealModelMap().get(String.valueOf(interflowOrderRecordDto.getGunCode()));
                                chargeStateDto.setConnectorStatus(getCityGunWorkStatus(gunRealModel.getGunStatus()));
                                if (gunRealModel.getGunStatus() == 2) {
                                    setChargeStateDtoFromPileRealModel(chargeStateDto, gunRealModel);
                                } else {
                                    setChargeStateDtoFromOrderRecord(chargeStateDto, interflowOrderRecordDto);
                                }
                            }
                        } else {
                            setChargeStateDtoFromOrderRecord(chargeStateDto, interflowOrderRecordDto);
                        }
                    }
                }
            }
        }
        dataMap.put("Ret", 0);
        dataMap.put("Msg", "");
        dataMap.put("Data", chargeStateDto);
        return dataMap;
    }

    //辅助方法
    private void setChargeStateDtoFromOrderRecord(ChargeStateDto chargeStateDto, InterflowOrderRecordDto interflowOrderRecordDto) {
        chargeStateDto.setStartTime(interflowOrderRecordDto.getStartTime());
        chargeStateDto.setEndTime(interflowOrderRecordDto.getEndTime());
        chargeStateDto.setStartChargeSeqStat(getCityOrderStatus(interflowOrderRecordDto.getOrderStatus()));
        chargeStateDto.setConnectorId(interflowOrderRecordDto.getPileCode().substring(interflowOrderRecordDto.getPileCode().length() - 12) + interflowOrderRecordDto.getGunCode());
        chargeStateDto.setSoc((double) (interflowOrderRecordDto.getEndSoc() != null ? interflowOrderRecordDto.getEndSoc() : 0));
        chargeStateDto.setTotalPower(getToDouble(interflowOrderRecordDto.getTotalQt() != null ? interflowOrderRecordDto.getTotalQt() : 0.0));
        chargeStateDto.setTotalMoney(getToDouble(interflowOrderRecordDto.getTotalCost() != null ? interflowOrderRecordDto.getTotalCost().doubleValue() : 0.0));
        chargeStateDto.setSumperiod(interflowOrderRecordDto.getTimeFrameNum());
    }

    private void setChargeStateDtoFromPileRealModel(ChargeStateDto chargeStateDto, PileRealModel.GunRealModel gunRealModel) {
        chargeStateDto.setCurrentA(gunRealModel.getOutCurrent());
        chargeStateDto.setVoltageA(gunRealModel.getOutVolt());
        chargeStateDto.setSoc(Double.valueOf(gunRealModel.getBatterySoc()));
        chargeStateDto.setTotalPower(getToDouble(gunRealModel.getTotalQt()));
        chargeStateDto.setTotalMoney(getToDouble(gunRealModel.getTotalCost().doubleValue()));
    }

    @Override
    public Map<String, Object> queryStationStats(String stationId, String startTime, String endTime) {
        Map<String, Object> dataMap = Maps.newHashMap();
        StationStatsDto stationStatsDto = new StationStatsDto();
        stationStatsDto.setStationId(stationId);
        stationStatsDto.setStartTime(startTime);
        stationStatsDto.setEndTime(endTime);
        if (StringUtil.isNotEmpty(stationId)) {
            //根据多个站点id查询站点信息
            ResponseResult<Map<String, SiteInfoDto>> siteBasicInfoByIds = deviceService.findSiteBasicInfoByIds(Collections.singletonList(stationId));

            //设备列表信息
            List<DeviceBasicInfoDto> deviceBasicInfoDtoList = Lists.newArrayList();
            //电枪列表信息
            List<DeviceGunInfoDto> deviceGunInfoDtoList = Lists.newArrayList();
            //根据多个站点id，查询站点下电桩设备列表信息
            ResponseResult<Map<String, List<DeviceBasicInfoDto>>> deviceBasicInfoResult = deviceService.findDeviceBasicInfoBySiteIds(Collections.singletonList(stationId), 1);
            getDeviceAndGunInfo(deviceBasicInfoDtoList, deviceGunInfoDtoList, deviceBasicInfoResult);
            if (siteBasicInfoByIds.isSuccess() && !siteBasicInfoByIds.getData().isEmpty()) {
                //把设备信息列表根据站点id分组
                Map<String, List<DeviceBasicInfoDto>> deviceBasicInfoDtoMap = CollectionUtils.isNotEmpty(deviceBasicInfoDtoList) ? deviceBasicInfoDtoList.stream()
                        .collect(Collectors.groupingBy(DeviceBasicInfoDto::getSiteId)) : Collections.emptyMap();

                //把电枪信息列表根据设备id分组
                Map<String, List<DeviceGunInfoDto>> deviceGunInfoDtoMap = CollectionUtils.isNotEmpty(deviceGunInfoDtoList) ? deviceGunInfoDtoList.stream()
                        .collect(Collectors.groupingBy(DeviceGunInfoDto::getDeviceId)) : Collections.emptyMap();

                //根据多个电桩编码查询订单记录数据
                List<String> deviceCodeList = deviceBasicInfoDtoList.stream().map(DeviceBasicInfoDto::getDeviceNumber).collect(Collectors.toList());
                ResponseResult<Map<String, List<OrderRecordDto>>> orderRecordMapResult = togetherService.queryOrderRecordByPileCodes(deviceCodeList, startTime, startTime);

                //获取站点下设备信息
                if (!deviceBasicInfoDtoMap.isEmpty() && deviceBasicInfoDtoMap.containsKey(stationId)) {
                    //充电设备统计信息列表
                    List<EquipmentStatsDto> equipmentStatsInfos = Lists.newArrayList();

                    deviceBasicInfoDtoMap.get(stationId).forEach(deviceBasicInfoDto -> {

                        EquipmentStatsDto equipmentStatsDto = new EquipmentStatsDto();
                        equipmentStatsDto.setEquipmentId(deviceBasicInfoDto.getDeviceNumber());
                        //电枪订单记录对象
                        Map<Integer, List<OrderRecordDto>> gunOrderRecordMap;
                        //获取设备下电枪列表
                        if (!deviceGunInfoDtoMap.isEmpty() && deviceGunInfoDtoMap.containsKey(deviceBasicInfoDto.getId())) {
                            //获取当前电桩订单记录数据
                            if (orderRecordMapResult.isSuccess() && !orderRecordMapResult.getData().isEmpty()
                                    && orderRecordMapResult.getData().containsKey(deviceBasicInfoDto.getDeviceNumber())) {
                                gunOrderRecordMap = orderRecordMapResult.getData().get(deviceBasicInfoDto.getDeviceNumber()).stream().collect(Collectors.groupingBy(OrderRecordDto::getGunCode));
                            } else {
                                gunOrderRecordMap = Maps.newHashMap();
                            }

                            List<ConnectorStatsDto> connectorStatsDtoList = Lists.newArrayList();
                            deviceGunInfoDtoMap.get(deviceBasicInfoDto.getId()).forEach(deviceGunInfoDto -> {

                                ConnectorStatsDto connectorStatsDto = new ConnectorStatsDto();
                                String deviceNumber = deviceBasicInfoDto.getDeviceNumber();
                                String gunCode = deviceNumber.substring(deviceNumber.length() - 12) + deviceGunInfoDto.getGunCode();
                                connectorStatsDto.setConnectorId(gunCode);

                                if (!gunOrderRecordMap.isEmpty() && gunOrderRecordMap.containsKey(Integer.parseInt(deviceGunInfoDto.getGunCode()))) {
                                    List<OrderRecordDto> orderRecordDtoList = gunOrderRecordMap.get(Integer.parseInt(deviceGunInfoDto.getGunCode()));
                                    //过滤出充电订单记录数据
                                    List<OrderRecordDto> chargeOrderRecordList = orderRecordDtoList.stream().filter(orderRecordDto -> orderRecordDto.getRunMode() == 0).collect(Collectors.toList());
                                    if (CollectionUtils.isNotEmpty(chargeOrderRecordList)) {
                                        double sum = chargeOrderRecordList.stream().filter(c -> StringUtil.isNotEmpty(c.getTotalQt())).mapToDouble(OrderRecordDto::getTotalQt).sum();
                                        if (StringUtil.isNotEmpty(sum)) {
                                            connectorStatsDto.setConnectorElectricity(getToDouble(sum, 1));
                                        }
                                    }
                                }
                                connectorStatsDtoList.add(connectorStatsDto);
                            });

                            //计算电桩电量
                            double pileCurQt = connectorStatsDtoList.stream().filter(c -> StringUtil.isNotEmpty(c.getConnectorElectricity())).mapToDouble(ConnectorStatsDto::getConnectorElectricity).sum();
                            if (StringUtil.isNotEmpty(pileCurQt)) {
                                equipmentStatsDto.setEquipmentElectricity(getToDouble(pileCurQt, 1));
                            }
                            equipmentStatsDto.setConnectorStatsInfos(connectorStatsDtoList);
                        }
                        equipmentStatsInfos.add(equipmentStatsDto);
                    });
                    //计算站点电量
                    double siteCurQt = equipmentStatsInfos.stream().filter(c -> StringUtil.isNotEmpty(c.getEquipmentElectricity())).mapToDouble(EquipmentStatsDto::getEquipmentElectricity).sum();
                    if (StringUtil.isNotEmpty(siteCurQt)) {
                        stationStatsDto.setStationElectricity(getToDouble(siteCurQt, 1));
                    }
                    stationStatsDto.setEquipmentStatsInfos(equipmentStatsInfos);
                }
            }
        }
        dataMap.put("StationStats", stationStatsDto);
        return dataMap;
    }

    private void getDeviceAndGunInfo(List<DeviceBasicInfoDto> deviceBasicInfoDtoList, List<DeviceGunInfoDto> deviceGunInfoDtoList, ResponseResult<Map<String, List<DeviceBasicInfoDto>>> deviceBasicInfoResult) {
        if (deviceBasicInfoResult.isSuccess() && !deviceBasicInfoResult.getData().isEmpty()) {
            deviceBasicInfoResult.getData().forEach((k, v) -> deviceBasicInfoDtoList.addAll(v));
            //根据多个设备id，查询设备关联电枪数据
            List<String> deviceIdList = deviceBasicInfoDtoList.stream().map(DeviceBasicInfoDto::getId).distinct().collect(Collectors.toList());
            ResponseResult<Map<String, List<DeviceGunInfoDto>>> deviceGunInfoResult = deviceService.findDeviceGunInfoByDeviceIds(deviceIdList);
            if (deviceGunInfoResult.isSuccess() && !deviceGunInfoResult.getData().isEmpty()) {
                deviceGunInfoResult.getData().forEach((k, v) -> deviceGunInfoDtoList.addAll(v));
            }
        }
    }
}
