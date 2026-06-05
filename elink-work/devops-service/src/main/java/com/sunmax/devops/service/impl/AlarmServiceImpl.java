package com.sunmax.devops.service.impl;

import com.alibaba.fastjson2.JSON;
import com.sunmax.common.dto.PageDto;
import com.sunmax.common.dto.device.AssetTypeDto;
import com.sunmax.common.dto.device.DeviceAlarmEventListDto;
import com.sunmax.common.dto.device.DeviceBasicInfoDto;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.util.StringUtil;
import com.sunmax.common.vo.device.DeviceAlarmEventQueryVo;
import com.sunmax.devops.dto.AlarmResultDto;
import com.sunmax.devops.service.AlarmService;
import com.sunmax.devops.service.feign.DeviceService;
import com.sunmax.devops.vo.AlarmQueryVo;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class AlarmServiceImpl implements AlarmService {

    @Autowired
    private DeviceService deviceService;

    @Override
    public ResponseResult<List<AssetTypeDto>> getAssetTypeList() {
        return deviceService.getAssetTypeList(null);
    }

    @Override
    public ResponseResult<AlarmResultDto> queryAlarmList(AlarmQueryVo alarmQueryVo) {
        //返回的对象
        AlarmResultDto result = new AlarmResultDto();
        if (StringUtil.isEmpty(alarmQueryVo.getSiteIds()) || StringUtil.isEmpty(alarmQueryVo.getPage()) || StringUtil.isEmpty(alarmQueryVo.getSize())) {
            return ResponseResult.paramError(ResponseResult.PARAM_ERROR);
        }
        //根据多个站点id查询设备信息
        List<String> siteIds = JSON.parseArray(alarmQueryVo.getSiteIds(), String.class);
        Map<String, List<DeviceBasicInfoDto>> siteDeviceMap = deviceService.findDeviceBasicInfoBySiteIds(siteIds, null).getData();
        if (MapUtils.isEmpty(siteDeviceMap)) {
            return ResponseResult.ok(result);
        }
        List<DeviceBasicInfoDto> deviceInfoList = siteDeviceMap.values().stream().flatMap(Collection::stream).collect(Collectors.toList());
        if (StringUtil.isNotEmpty(alarmQueryVo.getDeviceName())) { //设备名称
            deviceInfoList = deviceInfoList.stream().filter(d -> StringUtil.isNotEmpty(d.getDeviceName())
                    && d.getDeviceName().contains(alarmQueryVo.getDeviceName())).collect(Collectors.toList());
        }
        if (StringUtil.isNotEmpty(alarmQueryVo.getDeviceNumber())) { //设备序列号
            deviceInfoList = deviceInfoList.stream().filter(d -> StringUtil.isNotEmpty(d.getDeviceNumber())
                    && d.getDeviceNumber().contains(alarmQueryVo.getDeviceNumber())).collect(Collectors.toList());
        }
        if (StringUtil.isNotEmpty(alarmQueryVo.getTypeIds())) { //多个设备类型id
            List<String> typeIds = JSON.parseArray(alarmQueryVo.getTypeIds(), String.class);
            deviceInfoList = deviceInfoList.stream().filter(d -> StringUtil.isNotEmpty(d.getTypeId())
                    && typeIds.contains(d.getTypeId())).collect(Collectors.toList());
        }
        if (CollectionUtils.isEmpty(deviceInfoList)) {
            return ResponseResult.ok(result);
        }
        Map<String, DeviceBasicInfoDto> deviceInfoMap = deviceInfoList.stream().collect(Collectors.toMap(DeviceBasicInfoDto::getId,
                d -> d, (k1, k2) -> k1));
        //查询设备告警信息列表
        DeviceAlarmEventQueryVo eventQueryVo = new DeviceAlarmEventQueryVo();
        eventQueryVo.setDeviceIds(JSON.toJSONString(deviceInfoMap.keySet()));
        eventQueryVo.setEventLevel(alarmQueryVo.getEventLevel());
        eventQueryVo.setEventStatus(alarmQueryVo.getEventStatus());
        eventQueryVo.setStartDate(alarmQueryVo.getStartDate());
        eventQueryVo.setEndDate(alarmQueryVo.getEndDate());
        eventQueryVo.setPage(1);
        eventQueryVo.setSize(9999999);
        List<DeviceAlarmEventListDto> alarmList = deviceService.findAllDeviceEventList(eventQueryVo).getData().getItems();
        if (CollectionUtils.isEmpty(alarmList)) {
            return ResponseResult.ok(result);
        }
        //获取告警级别条数
        result.setTotalSize(alarmList.size());
        result.setIndex(alarmQueryVo.getPage());
        result.setPageSize(alarmQueryVo.getSize());
        result.setUnknown(alarmList.stream().filter(a -> StringUtil.isEmpty(a.getEventLevel())).count());
        Map<Integer, Long> eventLevelMap = alarmList.stream().filter(a -> StringUtil.isNotEmpty(a.getEventLevel())).collect(Collectors
                .groupingBy(DeviceAlarmEventListDto::getEventLevel, Collectors.counting()));
        eventLevelMap.forEach((eventLevel, count) -> {
            //事件级别 1-次要告警 2-重要告警 3-紧急告警 4-提示告警 5-离线告警
            switch (eventLevel) {
                case 1:
                    result.setSecondary(count);
                    break;
                case 2:
                    result.setImportant(count);
                    break;
                case 3:
                    result.setEmergency(count);
                    break;
                case 4:
                    result.setTip(count);
                    break;
                case 5:
                    result.setOffLine(count);
                    break;
                default:
                    result.setUnknown(result.getUnknown() + count);
                    break;
            }
        });
        //分页并查询数据
        Map<String, List<AlarmResultDto.AlarmDto>> alarmMap = new PageDto<>(alarmList, alarmQueryVo.getPage(), alarmQueryVo.getSize()).getItems()
                .stream().filter(a -> StringUtil.isNotEmpty(a.getCreateTime()))
                .collect(Collectors.groupingBy(d -> d.getCreateTime().substring(0, 10),
                        // mapFactory: 使用降序 TreeMap
                        () -> new TreeMap<>(Collections.reverseOrder()),
                        Collectors.mapping(a -> {
                            AlarmResultDto.AlarmDto alarmDto = new AlarmResultDto.AlarmDto();
                            BeanUtils.copyProperties(a, alarmDto);
                            if (deviceInfoMap.containsKey(a.getDeviceId())) {
                                DeviceBasicInfoDto deviceInfo = deviceInfoMap.get(a.getDeviceId());
                                alarmDto.setDeviceName(deviceInfo.getDeviceName());
                                alarmDto.setDeviceNumber(deviceInfo.getDeviceNumber());
                                alarmDto.setTypeName(deviceInfo.getTypeName());
                                alarmDto.setSiteName(deviceInfo.getSiteName());
                            }
                            return alarmDto;
                        }, Collectors.collectingAndThen(Collectors.toList(), list -> {
                                    list.sort(Comparator.comparing(AlarmResultDto.AlarmDto::getCreateTime).reversed());
                                    return list;
                                }
                        ))));
        result.setAlarmMap(alarmMap);
        return ResponseResult.ok(result);
    }

}
