package com.sunmax.together.service.impl;

import com.google.common.collect.Maps;
import com.sunmax.common.config.redis.RedisGeneralUtil;
import com.sunmax.common.dto.device.DeviceBasicInfoDto;
import com.sunmax.common.dto.device.DeviceGunInfoDto;
import com.sunmax.common.dto.together.PileGunMonitorDataDto;
import com.sunmax.common.model.general.PileRealModel;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.util.StringUtil;
import com.sunmax.together.dao.order.OrderRecordDao;
import com.sunmax.together.entity.order.OrderRecordEntity;
import com.sunmax.together.service.WebFeignService;
import com.sunmax.together.service.feign.DeviceService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.compress.utils.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static com.sunmax.common.util.DateUtil.*;
import static com.sunmax.common.util.StringUtil.convertGunStatus;

@Service
@Slf4j
public class WebFeignServiceImpl implements WebFeignService {

    @Autowired
    private DeviceService deviceService;

    @Autowired
    private OrderRecordDao orderRecordDao;

    @Override
    public ResponseResult<Map<String, List<PileGunMonitorDataDto>>> findAllPileGunMonitorList(List<String> deviceIds) {
        Map<String, List<PileGunMonitorDataDto>> resultMap = Maps.newHashMap();
        try {
            //根据设备id查询电桩数据
            Map<String, DeviceBasicInfoDto> deviceInfoMap = deviceService.findDeviceBasicInfoByIds(deviceIds).getData();
            if (MapUtils.isNotEmpty(deviceInfoMap)) {
                //根据多个设备id查询电枪数据
                Map<String, List<DeviceGunInfoDto>> pileGunMap = deviceService.findDeviceGunInfoByDeviceIds(deviceIds).getData();
                List<String> pileCodes = deviceInfoMap.values().stream().map(DeviceBasicInfoDto::getDeviceNumber)
                        .filter(StringUtil::isNotEmpty).collect(Collectors.toList());
                //获取电桩枪的今日充电量,今日放电量
                Map<String, List<OrderRecordEntity>> chargeOrderMap = Maps.newHashMap();
                Map<String, List<OrderRecordEntity>> dischargeOrderMap = Maps.newHashMap();
                String dayStartTime = getStartTimeByQueryType(0);
                String dayEndTime = getEndTimeByQueryType(0);
                List<OrderRecordEntity> orderRecordList = orderRecordDao.findAllByPileCodeInAndEndTimeBetween(pileCodes, dayStartTime, dayEndTime);
                if (CollectionUtils.isNotEmpty(orderRecordList)) {
                    chargeOrderMap = orderRecordList.stream().filter(o -> StringUtil.isNotEmpty(o.getRunMode()) && o.getRunMode() == 0 && StringUtil.isNotEmpty(o.getTotalQt()))
                            .collect(Collectors.groupingBy(OrderRecordEntity::getPileCode));
                    dischargeOrderMap = orderRecordList.stream().filter(o -> StringUtil.isNotEmpty(o.getRunMode()) && (o.getRunMode() == 1 || o.getRunMode() == 2)
                            && StringUtil.isNotEmpty(o.getTotalQt())).collect(Collectors.groupingBy(OrderRecordEntity::getPileCode));
                }

                for (Map.Entry<String, DeviceBasicInfoDto> entry : deviceInfoMap.entrySet()) {
                    String deviceId = entry.getKey();
                    DeviceBasicInfoDto deviceInfo = entry.getValue();
                    if (StringUtil.isNotEmpty(deviceInfo.getDeviceNumber())) {
                        //获取电桩实时数据
                        Map<String, PileRealModel.GunRealModel> gunRealModelMap = Maps.newConcurrentMap();
                        PileRealModel pileRealModel = RedisGeneralUtil.getPileRealModel(deviceInfo.getDeviceNumber());
                        if (pileRealModel != null && MapUtils.isNotEmpty(pileRealModel.getGunRealModelMap())) {
                            gunRealModelMap = pileRealModel.getGunRealModelMap();
                        }
                        //获取电枪电量数据
                        Map<Integer, Double> pileGunChargeQtMap = Maps.newHashMap();
                        Map<Integer, Double> pileGunV2GQtMap = Maps.newHashMap();
                        if (chargeOrderMap.containsKey(deviceInfo.getDeviceNumber())) {
                            pileGunChargeQtMap = chargeOrderMap.get(deviceInfo.getDeviceNumber()).stream().collect(Collectors.groupingBy(OrderRecordEntity::getGunCode,
                                    Collectors.summingDouble(OrderRecordEntity::getTotalQt)));
                        }
                        if (dischargeOrderMap.containsKey(deviceInfo.getDeviceNumber())) {
                            pileGunV2GQtMap = dischargeOrderMap.get(deviceInfo.getDeviceNumber()).stream().collect(Collectors.groupingBy(OrderRecordEntity::getGunCode,
                                    Collectors.summingDouble(OrderRecordEntity::getTotalQt)));
                        }
                        List<PileGunMonitorDataDto> resultList = Lists.newArrayList();
                        if (pileGunMap.containsKey(deviceId)) {
                            for (DeviceGunInfoDto pileGunInfo : pileGunMap.get(deviceId)) {
                                PileGunMonitorDataDto result = new PileGunMonitorDataDto();
                                result.setPileCode(deviceInfo.getDeviceNumber());
                                result.setGunCode(pileGunInfo.getGunCode());
                                result.setGunName(pileGunInfo.getGunName());
                                //枪状态
                                if (gunRealModelMap.containsKey(pileGunInfo.getGunCode())) {
                                    PileRealModel.GunRealModel gunRealModel = gunRealModelMap.get(pileGunInfo.getGunCode());
                                    if (StringUtil.isNotEmpty(gunRealModel.getGunStatus())) {
                                        result.setGunStatus(gunRealModel.getGunStatus());
                                        result.setGunWorkState(convertGunStatus(pileRealModel, gunRealModel.getGunCode()));
                                    }
                                }
                                //今日充电量,今日放电量
                                if (StringUtil.isNotEmpty(pileGunInfo.getGunCode())) {
                                    int gunCode = Integer.parseInt(pileGunInfo.getGunCode());
                                    result.setDayChargeQt(pileGunChargeQtMap.getOrDefault(gunCode, 0D));
                                    result.setDayV2gQt(pileGunV2GQtMap.getOrDefault(gunCode, 0D));
                                }
                                resultList.add(result);
                            }
                        }
                        resultMap.put(deviceId, resultList);
                    }
                }
            }
            return ResponseResult.ok(resultMap);
        } catch (RuntimeException e) {
            log.error("查询电枪数据报错", e);
            return ResponseResult.error("查询电枪数据报错,请稍后重试!!");
        }
    }

}
