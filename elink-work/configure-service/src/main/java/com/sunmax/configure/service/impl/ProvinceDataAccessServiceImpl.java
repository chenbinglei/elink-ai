package com.sunmax.configure.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sunmax.common.dto.PageDto;
import com.sunmax.common.dto.device.DeviceBasicInfoDto;
import com.sunmax.common.dto.device.DeviceGunInfoDto;
import com.sunmax.common.dto.device.SiteInfoDto;
import com.sunmax.common.dto.operate.ChargerPriceRateDto;
import com.sunmax.common.dto.system.OperatorInfoDto;
import com.sunmax.common.dto.system.SiteOperateDto;
import com.sunmax.common.model.general.PileRealModel;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.util.StringUtil;
import com.sunmax.common.vo.SiteFieldParamVo;
import com.sunmax.configure.dto.province.*;
import com.sunmax.configure.service.ProvinceDataAccessService;
import com.sunmax.configure.service.feign.DeviceService;
import com.sunmax.configure.service.feign.TogetherService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.sunmax.common.config.redis.RedisGeneralUtil.getPileRealModelList;
import static com.sunmax.common.util.DateUtil.localDateTimeToStr;
import static com.sunmax.common.util.DateUtil.strToLocalDateTime;
import static com.sunmax.common.util.StringUtil.*;

@Slf4j
@Service
public class ProvinceDataAccessServiceImpl implements ProvinceDataAccessService {

    @Autowired
    private DeviceService deviceService;

    @Autowired
    private TogetherService togetherService;

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

    @Override
    public StationInfoDto getStationInfo(String stationIds, List<SiteOperateDto> siteOperateList) {
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
            getDeviceAndGunInfo(deviceBasicInfoDtoList, deviceGunInfoDtoList, deviceBasicInfoResult);
//            //如果传了上次查询时间，则按照时间过滤
//            if (StringUtil.isNotEmpty(lastQueryTime)) {
//                //先查询有没有修改时间在指定查询时间之后的电枪数据
//                if (CollectionUtils.isNotEmpty(deviceGunInfoDtoList)) {
//                    LocalDateTime queryTime = strToLocalDateTime(lastQueryTime);
//                    deviceGunInfoDtoList = deviceGunInfoDtoList.stream().filter(item -> item.getUpdateTime().isAfter(queryTime)).collect(Collectors.toList());
//                    if (CollectionUtils.isNotEmpty(deviceGunInfoDtoList)) {
//                        // 根据电枪信息中的设备id，过滤出设备信息
//                        List<DeviceGunInfoDto> finalDeviceGunInfoDtoList = deviceGunInfoDtoList;
//                        deviceBasicInfoDtoList = deviceBasicInfoDtoList.stream().filter(item -> finalDeviceGunInfoDtoList.stream().anyMatch(gunItem -> gunItem.getDeviceId().equals(item.getId()))).collect(Collectors.toList());
//                        // 根据设备信息中的站点id，过滤出站点信息
//                        List<DeviceBasicInfoDto> finalDeviceBasicInfoDtoList1 = deviceBasicInfoDtoList;
//                        siteInfoDtoList = siteInfoDtoList.stream().filter(item -> finalDeviceBasicInfoDtoList1.stream().anyMatch(basicItem -> basicItem.getSiteId().equals(item.getId()))).collect(Collectors.toList());
//                    } else {
//                        // 过滤出修改时间在查询时间之后的设备数据
//                        deviceBasicInfoDtoList = deviceBasicInfoDtoList.stream().filter(item -> item.getUpdateTime().isAfter(queryTime)).collect(Collectors.toList());
//                        // 过滤出修改时间在查询时间之后的站点数据
//                        siteInfoDtoList = siteInfoDtoList.stream().filter(item -> item.getUpdateTime().isAfter(queryTime)).collect(Collectors.toList());
//                    }
//                }
//            }
            if (CollectionUtils.isNotEmpty(siteInfoDtoList)) {

                //如果传了站点id查询，则按照站点id过滤
                if (StringUtil.isNotEmpty(stationIds)) {
                    List<String> provinceStationIdList = JSONObject.parseArray(stationIds, String.class);
                    siteInfoDtoList = siteInfoDtoList.stream().filter(item -> provinceStationIdList.stream().anyMatch(stationId -> stationId.equals(item.getId()))).collect(Collectors.toList());
                }
                if (CollectionUtils.isNotEmpty(siteInfoDtoList)) {
                    //把设备信息列表根据站点id分组
                    Map<String, List<DeviceBasicInfoDto>> deviceBasicInfoDtoMap = CollectionUtils.isNotEmpty(deviceBasicInfoDtoList) ? deviceBasicInfoDtoList.stream()
                            .collect(Collectors.groupingBy(DeviceBasicInfoDto::getSiteId)) : Collections.emptyMap();

                    //把电枪信息列表根据设备id分组
                    Map<String, List<DeviceGunInfoDto>> deviceGunInfoDtoMap = CollectionUtils.isNotEmpty(deviceGunInfoDtoList) ? deviceGunInfoDtoList.stream()
                            .collect(Collectors.groupingBy(DeviceGunInfoDto::getDeviceId)) : Collections.emptyMap();

                    //根据多个站点id查询充电计费信息
                    ResponseResult<Map<String, List<ChargerPriceRateDto>>> chargerRateListResult = togetherService.findChargerRateListBySiteIds(siteInfoDtoList.stream().map(SiteInfoDto::getId).collect(Collectors.toList()), 1);

                    //循环组装数据
                    siteInfoDtoList.forEach(siteInfoDto -> {
                        StationInfoDto stationInfoDto = new StationInfoDto();
                        //获取站点扩展属性信息
                        String siteReadwriteObject = siteInfoDto.getSiteReadwriteObject();
                        //周边配套设施
                        List<Integer> supportingFacilities = Lists.newArrayList();
                        //停车减免规则
                        List<Integer> parkReductionMode = Lists.newArrayList();
                        if (StringUtil.isNotEmpty(siteReadwriteObject)) {
                            Map<String, Object> readwriteMap = JSON.parseObject(siteReadwriteObject, new TypeReference<Map<String, Object>>() {});
                            //获取周边配套设施
                            Gson gson = new Gson();
                            if (readwriteMap.containsKey(SiteFieldParamVo.SUPPORTING_FACILITIES)) {
                                String jsonString = JSON.toJSONString(readwriteMap.get(SiteFieldParamVo.SUPPORTING_FACILITIES));
                                Type listType = new TypeToken<List<Integer>>() {}.getType();
                                supportingFacilities = gson.fromJson(jsonString, listType);
                                readwriteMap.remove(SiteFieldParamVo.SUPPORTING_FACILITIES);
                            }
                            //获取停车减免规则
                            if (readwriteMap.containsKey(SiteFieldParamVo.PARK_REDUCTION_MODE)) {
                                String jsonString = JSON.toJSONString(readwriteMap.get(SiteFieldParamVo.PARK_REDUCTION_MODE));
                                Type listType = new TypeToken<List<Integer>>() {}.getType();
                                parkReductionMode = gson.fromJson(jsonString, listType);
                                readwriteMap.remove(SiteFieldParamVo.PARK_REDUCTION_MODE);
                            }
                            StationInfoDto stationInfoJson = JSONObject.parseObject(JSON.toJSONString(readwriteMap), StationInfoDto.class);
                            BeanUtils.copyProperties(stationInfoJson, stationInfoDto);
                            //地址对象
                            if (readwriteMap.containsKey(SiteFieldParamVo.LOCATION)) {
                                Map<String, Object> localtionReadwriteMap = JSON.parseObject(JSON.toJSONString(readwriteMap.get(SiteFieldParamVo.LOCATION)), new TypeReference<Map<String, Object>>() {});
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
                                readwriteMap.remove(SiteFieldParamVo.LOCATION);
                            }
                        }
                        stationInfoDto.setSupportingFacilities(supportingFacilities);
                        stationInfoDto.setParkReductionMode(parkReductionMode);
                        //获取站点充电计费信息
                        if (chargerRateListResult.isSuccess() && !chargerRateListResult.getData().isEmpty() && chargerRateListResult.getData().containsKey(siteInfoDto.getId())) {
                            stationInfoDto.setPolicyInfos(chargerRateListResult.getData().get(siteInfoDto.getId()).stream().map(chargerPriceRateDto -> {
                                StationInfoDto.PolicyInfo policyInfo = new StationInfoDto.PolicyInfo();
                                policyInfo.setStartTime((chargerPriceRateDto.getStartTime() + ":00").replace(":",""));
                                policyInfo.setEndTime((chargerPriceRateDto.getEndTime() + ":59").replace(":",""));
                                policyInfo.setElecFee(chargerPriceRateDto.getElectMoney().doubleValue());
                                policyInfo.setServiceFee(chargerPriceRateDto.getServiceMoney().doubleValue());
                                return policyInfo;
                            }).collect(Collectors.toList()));
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
                            stationInfoDto.setPictures(Arrays.stream(siteInfoDto.getImagePath().split(",")).collect(Collectors.toList()));
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
                                        String gunCode = equipmentInfoDto.getEquipmentId() + deviceGunInfoDto.getGunCode();
                                        connectorInfoDto.setConnectorId(gunCode);
                                        connectorInfoDto.setConnectorName(deviceGunInfoDto.getGunName());
                                        connectorInfoDto.setConnectorType(deviceGunInfoDto.getType());
                                        connectorInfoDto.setCurrent(Double.valueOf(deviceGunInfoDto.getRatedCurrent()));
                                        connectorInfoDto.setPower(deviceGunInfoDto.getRatedPower());
                                        connectorInfoDto.setVoltageLowerLimits(Double.valueOf(deviceGunInfoDto.getVoltageLowerLimits()));
                                        connectorInfoDto.setVoltageUpperLimits(Double.valueOf(deviceGunInfoDto.getVoltageUpperLimits()));
                                        connectorInfoDto.setParkNo(deviceGunInfoDto.getParkNo());
                                        connectorInfoDto.setNationalStandard(deviceGunInfoDto.getNationalStandard());
                                        connectorInfoDto.setOperateStatus(getProvinceOperateStatus(deviceBasicInfoDto.getOperateStatus()));
                                        connectorInfoDto.setConnectorUniqueId(deviceGunInfoDto.getConnectorUniqueId());
                                        connectorInfoDto.getQrCodes().add(deviceGunInfoDto.getQrCodes());
                                        return connectorInfoDto;
                                    }).collect(Collectors.toList()));
                                }
                                return equipmentInfoDto;
                            }).collect(Collectors.toList()));
                        }
                        stationInfos.add(stationInfoDto);
                    });
                }
            }
        }
        if (CollectionUtils.isNotEmpty(stationInfos)) {
            return stationInfos.get(0);
        }
        return null;
    }

    @Override
    public Map<String, Object> queryOperatorInfo(Integer pageNo, Integer pageSize, List<OperatorInfoDto> operatorInfoList) {
        //返回的对象
        Map<String, Object> resultMap = Maps.newHashMap();

        //数据列表
        List<SupOperatorInfoDto> resultList = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(operatorInfoList)) {
            resultList = operatorInfoList.stream().map(operatorInfoDto -> {
                SupOperatorInfoDto supOperatorInfoDto = new SupOperatorInfoDto();
                BeanUtils.copyProperties(operatorInfoDto, supOperatorInfoDto);
                return supOperatorInfoDto;
            }).collect(Collectors.toList());
        }
        PageDto<SupOperatorInfoDto> pageDto = new PageDto<>(resultList, pageNo, pageSize);
        resultMap.put("PageNo", pageNo);
        resultMap.put("PageCount", (long) Math.ceil((double) pageDto.getTotalSize() / pageSize));
        resultMap.put("ItemSize", pageDto.getItems().size());
        resultMap.put("OperatorInfos", operatorInfoList);

        return resultMap;
    }

    @Override
    public Map<String, Object> findStationInfoListByTime(String lastQueryTime, Integer pageNo, Integer pageSize, String stationIds, List<SiteOperateDto> siteOperateList) {
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

                //如果传了站点id查询，则按照站点id过滤
                if (StringUtil.isNotEmpty(stationIds)) {
                    List<String> provinceStationIdList = JSONObject.parseArray(stationIds, String.class);
                    siteInfoDtoList = siteInfoDtoList.stream().filter(item -> provinceStationIdList.stream().anyMatch(stationId -> stationId.equals(item.getId()))).collect(Collectors.toList());
                }
                if (CollectionUtils.isNotEmpty(siteInfoDtoList)) {
                    //把设备信息列表根据站点id分组
                    Map<String, List<DeviceBasicInfoDto>> deviceBasicInfoDtoMap = CollectionUtils.isNotEmpty(deviceBasicInfoDtoList) ? deviceBasicInfoDtoList.stream()
                            .collect(Collectors.groupingBy(DeviceBasicInfoDto::getSiteId)) : Collections.emptyMap();

                    //把电枪信息列表根据设备id分组
                    Map<String, List<DeviceGunInfoDto>> deviceGunInfoDtoMap = CollectionUtils.isNotEmpty(deviceGunInfoDtoList) ? deviceGunInfoDtoList.stream()
                            .collect(Collectors.groupingBy(DeviceGunInfoDto::getDeviceId)) : Collections.emptyMap();

                    //根据多个站点id查询充电计费信息
                    ResponseResult<Map<String, List<ChargerPriceRateDto>>> chargerRateListResult = togetherService.findChargerRateListBySiteIds(siteInfoDtoList.stream().map(SiteInfoDto::getId).collect(Collectors.toList()), 1);

                    //循环组装数据
                    List<StationInfoDto> finalStationInfos = stationInfos;
                    siteInfoDtoList.forEach(siteInfoDto -> {
                        StationInfoDto stationInfoDto = new StationInfoDto();
                        //获取站点扩展属性信息
                        String siteReadwriteObject = siteInfoDto.getSiteReadwriteObject();
                        //周边配套设施
                        List<Integer> supportingFacilities = Lists.newArrayList();
                        //停车减免规则
                        List<Integer> parkReductionMode = Lists.newArrayList();
                        if (StringUtil.isNotEmpty(siteReadwriteObject)) {
                            Map<String, Object> readwriteMap = JSON.parseObject(siteReadwriteObject, new TypeReference<Map<String, Object>>() {});
                            //获取周边配套设施
                            Gson gson = new Gson();
                            if (readwriteMap.containsKey(SiteFieldParamVo.SUPPORTING_FACILITIES)) {
                                String jsonString = JSON.toJSONString(readwriteMap.get(SiteFieldParamVo.SUPPORTING_FACILITIES));
                                Type listType = new TypeToken<List<Integer>>() {}.getType();
                                supportingFacilities = gson.fromJson(jsonString, listType);
                                readwriteMap.remove(SiteFieldParamVo.SUPPORTING_FACILITIES);
                            }
                            //获取停车减免规则
                            if (readwriteMap.containsKey(SiteFieldParamVo.PARK_REDUCTION_MODE)) {
                                String jsonString = JSON.toJSONString(readwriteMap.get(SiteFieldParamVo.PARK_REDUCTION_MODE));
                                Type listType = new TypeToken<List<Integer>>() {}.getType();
                                parkReductionMode = gson.fromJson(jsonString, listType);
                                readwriteMap.remove(SiteFieldParamVo.PARK_REDUCTION_MODE);
                            }
                            StationInfoDto stationInfoJson = JSONObject.parseObject(JSON.toJSONString(readwriteMap), StationInfoDto.class);
                            BeanUtils.copyProperties(stationInfoJson, stationInfoDto);
                            //地址对象
                            if (readwriteMap.containsKey(SiteFieldParamVo.LOCATION)) {
                                Map<String, Object> localtionReadwriteMap = JSON.parseObject(JSON.toJSONString(readwriteMap.get(SiteFieldParamVo.LOCATION)), new TypeReference<Map<String, Object>>() {});
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
                                readwriteMap.remove(SiteFieldParamVo.LOCATION);
                            }
                        }
                        stationInfoDto.setSupportingFacilities(supportingFacilities);
                        stationInfoDto.setParkReductionMode(parkReductionMode);
                        //获取站点充电计费信息
                        if (chargerRateListResult.isSuccess() && !chargerRateListResult.getData().isEmpty() && chargerRateListResult.getData().containsKey(siteInfoDto.getId())) {
                            stationInfoDto.setPolicyInfos(chargerRateListResult.getData().get(siteInfoDto.getId()).stream().map(chargerPriceRateDto -> {
                                StationInfoDto.PolicyInfo policyInfo = new StationInfoDto.PolicyInfo();
                                policyInfo.setStartTime((chargerPriceRateDto.getStartTime() + ":00").replace(":",""));
                                policyInfo.setEndTime((chargerPriceRateDto.getEndTime() + ":59").replace(":",""));
                                policyInfo.setElecFee(chargerPriceRateDto.getElectMoney().doubleValue());
                                policyInfo.setServiceFee(chargerPriceRateDto.getServiceMoney().doubleValue());
                                return policyInfo;
                            }).collect(Collectors.toList()));
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
                            stationInfoDto.setPictures(Arrays.stream(siteInfoDto.getImagePath().split(",")).collect(Collectors.toList()));
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
                                        String gunCode = equipmentInfoDto.getEquipmentId() + deviceGunInfoDto.getGunCode();
                                        connectorInfoDto.setConnectorId(gunCode);
                                        connectorInfoDto.setConnectorName(deviceGunInfoDto.getGunName());
                                        connectorInfoDto.setConnectorType(deviceGunInfoDto.getType());
                                        connectorInfoDto.setCurrent(Double.valueOf(deviceGunInfoDto.getRatedCurrent()));
                                        connectorInfoDto.setPower(deviceGunInfoDto.getRatedPower());
                                        connectorInfoDto.setVoltageLowerLimits(Double.valueOf(deviceGunInfoDto.getVoltageLowerLimits()));
                                        connectorInfoDto.setVoltageUpperLimits(Double.valueOf(deviceGunInfoDto.getVoltageUpperLimits()));
                                        connectorInfoDto.setParkNo(deviceGunInfoDto.getParkNo());
                                        connectorInfoDto.setNationalStandard(deviceGunInfoDto.getNationalStandard());
                                        connectorInfoDto.setOperateStatus(getProvinceOperateStatus(deviceBasicInfoDto.getOperateStatus()));
                                        connectorInfoDto.setConnectorUniqueId(deviceGunInfoDto.getConnectorUniqueId());
                                        connectorInfoDto.getQrCodes().add(deviceGunInfoDto.getQrCodes());
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
        }
        if (CollectionUtils.isNotEmpty(stationInfos)) {
            itemSize = stationInfos.size();
            pageCount = (int) Math.ceil((double) stationInfos.size() / pageSize);
            stationInfos = stationInfos.stream().skip((long) (pageNo - 1) *pageSize).limit(pageSize).collect(Collectors.toList());
        }
        dataMap.put("PageNo", pageNo);
        dataMap.put("PageCount", pageCount);
        dataMap.put("ItemSize", itemSize);
        dataMap.put("StationInfos", stationInfos);
        return dataMap;
    }

    @Override
    public Map<String, Object> queryStationStatus(List<SiteOperateDto> siteOperateList) {
        Map<String, Object> dataMap = Maps.newHashMap();
        List<StationStatusDto> stationStatusInfos = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(siteOperateList)) {
            List<String> siteIdList = siteOperateList.stream().map(SiteOperateDto::getSiteId).distinct().collect(Collectors.toList());

            //把站点和运营商id信息转成map
            Map<String, String> siteOperateMap = siteOperateList.stream().collect(Collectors.toMap(SiteOperateDto::getSiteId, SiteOperateDto::getOperateId, (k1, k2) -> k1));
            //根据多个站点id,查询站点信息
            ResponseResult<Map<String, SiteInfoDto>> siteBasicInfoByIds = deviceService.findSiteBasicInfoByIds(siteIdList);

            //设备列表信息
            List<DeviceBasicInfoDto> deviceBasicInfoDtoList = Lists.newArrayList();
            //电枪列表信息
            List<DeviceGunInfoDto> deviceGunInfoDtoList = Lists.newArrayList();
            //根据多个站点id，查询站点下电桩设备列表信息
            ResponseResult<Map<String, List<DeviceBasicInfoDto>>> deviceBasicInfoResult = deviceService.findDeviceBasicInfoBySiteIds(siteIdList, 1);
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
                //循环站点信息获取数据
                siteBasicInfoByIds.getData().forEach((siteId, siteInfoDto) -> {
                    StationStatusDto stationStatusDto = new StationStatusDto();

                    //充电服务运营商id
                    String equipmentOwnerId = null;
                    //获取站点扩展属性信息
                    String siteReadwriteObject = siteInfoDto.getSiteReadwriteObject();
                    if (StringUtil.isNotEmpty(siteReadwriteObject)) {

                        Map<String, Object> readwriteMap = Maps.newHashMap();
                        if (StringUtil.isNotEmpty(siteReadwriteObject)) {
                            readwriteMap = JSON.parseObject(siteReadwriteObject, new TypeReference<Map<String, Object>>() {
                            });
                        }
                        //充电服务运营商id
                        if (!readwriteMap.isEmpty() && readwriteMap.containsKey(SiteFieldParamVo.EQUIPMENT_OWNER_ID) && StringUtil.isNotEmpty(readwriteMap.get(SiteFieldParamVo.EQUIPMENT_OWNER_ID))) {
                            equipmentOwnerId = readwriteMap.get(SiteFieldParamVo.EQUIPMENT_OWNER_ID).toString();
                        }
                    }
                    stationStatusDto.setEquipmentOwnerId(equipmentOwnerId);
                    //获取运营商id
                    String operateId = siteOperateMap.get(siteInfoDto.getId());
                    stationStatusDto.setOperatorId(operateId);
                    stationStatusDto.setStationId(siteId);
                    //转换站点状态
                    stationStatusDto.setStationStatus(getStationStatusOther(siteInfoDto.getSiteStatus()));

                    //获取站点下设备列表
                    if (!deviceBasicInfoDtoMap.isEmpty() && deviceBasicInfoDtoMap.containsKey(siteId)) {
                        List<ConnectorStatusDto> connectorStatusInfos = Lists.newArrayList();
                        String finalEquipmentOwnerId = equipmentOwnerId;
                        deviceBasicInfoDtoMap.get(siteId).forEach(deviceBasicInfoDto -> {
                            //获取当前电桩实时数据
                            PileRealModel pileRealModel = pileRealModelMap.get(deviceBasicInfoDto.getDeviceNumber());
                            //获取设备下电枪列表
                            if (!deviceGunInfoDtoMap.isEmpty() && deviceGunInfoDtoMap.containsKey(deviceBasicInfoDto.getId())) {
                                Map<String, PileRealModel.GunRealModel> gunRealModelMap = pileRealModel.getGunRealModelMap();
                                deviceGunInfoDtoMap.get(deviceBasicInfoDto.getId()).forEach(deviceGunInfoDto -> {
                                    ConnectorStatusDto connectorStatusDto = new ConnectorStatusDto();
                                    connectorStatusDto.setOperatorId(operateId);
                                    connectorStatusDto.setEquipmentOwnerId(finalEquipmentOwnerId);
                                    connectorStatusDto.setStationId(siteId);
                                    connectorStatusDto.setEquipmentId(deviceBasicInfoDto.getDeviceNumber());
                                    String gunCode = deviceBasicInfoDto.getDeviceNumber() + deviceGunInfoDto.getGunCode();
                                    connectorStatusDto.setConnectorId(gunCode);
                                    if (!gunRealModelMap.isEmpty() && gunRealModelMap.containsKey(deviceGunInfoDto.getGunCode())) {
                                        PileRealModel.GunRealModel gunRealModel = gunRealModelMap.get(deviceGunInfoDto.getGunCode());
                                        //因为省级不需要放电数据,所以过滤掉放电状态的数据
                                        Integer gunStatus = gunRealModel.getGunStatus();
                                        if (StringUtil.isNotEmpty(gunStatus) && gunStatus != 4 && gunStatus != 5) {
                                            connectorStatusDto.setStatus(getCityGunWorkStatus(gunStatus));
                                            //接口状态更新时间(暂时暂时给当前时间)
                                            connectorStatusDto.setUpdateTime(localDateTimeToStr(LocalDateTime.now()));
                                            connectorStatusInfos.add(connectorStatusDto);
                                        }
                                    }
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
}
