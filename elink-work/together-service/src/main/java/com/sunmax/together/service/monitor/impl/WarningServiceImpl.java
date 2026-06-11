package com.sunmax.together.service.monitor.impl;

import com.alibaba.fastjson2.JSON;
import com.google.common.collect.Maps;
import com.sunmax.common.dto.PageDto;
import com.sunmax.common.dto.device.DeviceAlarmEventListDto;
import com.sunmax.common.dto.device.DeviceBasicInfoDto;
import com.sunmax.common.dto.device.SiteInfoDto;
import com.sunmax.common.dto.system.OrganEmpowerListDto;
import com.sunmax.common.util.DateUtil;
import com.sunmax.common.util.DoubleUtil;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.util.StringUtil;
import com.sunmax.common.util.oss.FileUtil;
import com.sunmax.common.vo.device.DeviceAlarmEventQueryVo;
import com.sunmax.together.dto.monitor.centralMonitor.*;
import com.sunmax.together.service.feign.DeviceService;
import com.sunmax.together.service.feign.SystemService;
import com.sunmax.together.service.monitor.WarningService;
import com.sunmax.together.vo.monitor.centralMonitor.AlarmQueryVo;
import com.sunmax.together.vo.monitor.centralMonitor.DeviceAlarmQueryVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.compress.utils.Lists;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class WarningServiceImpl implements WarningService {

    @Autowired
    private SystemService systemService;

    @Autowired
    private DeviceService deviceService;

    @Override
    public ResponseResult<List<SystemEventListDto>> getEventList(String systemId) {
        //返回的集合
        List<SystemEventListDto> resultList = Lists.newArrayList();

        //根据系统id查询设备数据
        List<DeviceBasicInfoDto> deviceInfoList = deviceService.findDeviceInfoByParentIds(Collections.singletonList(systemId)).getData().get(systemId);
        if (CollectionUtils.isNotEmpty(deviceInfoList)) {
            Map<String, String> deviceInfoMap = deviceInfoList.stream().collect(Collectors.toMap(DeviceBasicInfoDto::getId,
                    DeviceBasicInfoDto::getDeviceName, (k1, k2) -> k1));
            //根据查询条件查询设备告警数据
            DeviceAlarmEventQueryVo deviceAlarmEventQueryVo = new DeviceAlarmEventQueryVo();
            deviceAlarmEventQueryVo.setDeviceIds(JSON.toJSONString(deviceInfoMap.keySet()));
            deviceAlarmEventQueryVo.setPage(1);
            deviceAlarmEventQueryVo.setSize(99999);
            deviceAlarmEventQueryVo.setEventStatus(0);
            deviceAlarmEventQueryVo.setStartDate(DateUtil.localDateToStr(LocalDate.now()));
            deviceAlarmEventQueryVo.setEndDate(DateUtil.localDateToStr(LocalDate.now()));
            PageDto<DeviceAlarmEventListDto> resultPage = deviceService.findAllDeviceEventList(deviceAlarmEventQueryVo).getData();
            if (resultPage != null && CollectionUtils.isNotEmpty(resultPage.getItems())) {
                resultList = resultPage.getItems().stream().map(deviceAlarmEvent -> {
                    SystemEventListDto result = new SystemEventListDto();
                    BeanUtils.copyProperties(deviceAlarmEvent, result);
                    result.setDeviceName(deviceInfoMap.get(deviceAlarmEvent.getDeviceId()));
                    if (StringUtil.isNotEmpty(deviceAlarmEvent.getCreateTime())) {
                        result.setCreateTime(deviceAlarmEvent.getCreateTime().substring(11, 16));
                    }
                    if (StringUtil.isNotEmpty(deviceAlarmEvent.getUpdateTime())) {
                        result.setUpdateTime(deviceAlarmEvent.getUpdateTime().substring(11, 16));
                    }
                    return result;
                }).collect(Collectors.toList());
            }
        }
        return ResponseResult.ok(resultList);
    }

    @Override
    public ResponseResult<PageDto<AlarmListDto>> queryAlarmEventList(AlarmQueryVo alarmQueryVo) {
        //返回的结果
        PageDto<AlarmListDto> resultPage = new PageDto<>(Lists.newArrayList(), alarmQueryVo.getPage(), alarmQueryVo.getSize());

        //根据用户id查询多个站点id
        List<String> siteIds = systemService.findAllOrganEmpowerByUserId(alarmQueryVo.getUserId()).getData().stream().map(OrganEmpowerListDto::getSiteId)
                .filter(StringUtil::isNotEmpty).distinct().collect(Collectors.toList());

        //查询条件 站点id
        if (StringUtil.isNotEmpty(alarmQueryVo.getSiteId())) {
            siteIds = siteIds.stream().filter(siteId -> Objects.equals(siteId, alarmQueryVo.getSiteId())).collect(Collectors.toList());
        }

        if (CollectionUtils.isEmpty(siteIds)) {
            return ResponseResult.ok(resultPage);
        }

        //根据多个站点id查询站点充电桩信息
        List<SiteInfoDto> siteInfoList = new ArrayList<>(deviceService.findSiteBasicInfoByIds(siteIds).getData().values());

        if (CollectionUtils.isNotEmpty(siteInfoList)) {
            //根据多个站点id查询站点下的设备数据
            siteIds = siteInfoList.stream().map(SiteInfoDto::getId).collect(Collectors.toList());
            List<DeviceBasicInfoDto> deviceInfoList = deviceService.findDeviceBasicInfoBySiteIds(siteIds, null).getData().values().stream()
                    .flatMap(Collection::stream).collect(Collectors.toList());

            //查询条件 设备类型id
            if (StringUtil.isNotEmpty(alarmQueryVo.getTypeId())) {
                deviceInfoList = deviceInfoList.stream().filter(deviceInfo -> Objects.equals(deviceInfo.getTypeId(), alarmQueryVo.getTypeId())).collect(Collectors.toList());
            }

            if (CollectionUtils.isNotEmpty(deviceInfoList)) {
                Map<String, DeviceBasicInfoDto> deviceInfoMap = deviceInfoList.stream().collect(Collectors.toMap(DeviceBasicInfoDto::getId,
                        a -> a, (k1, k2) -> k1));
                //根据查询条件查询设备告警数据
                DeviceAlarmEventQueryVo alarmEventQueryVo = new DeviceAlarmEventQueryVo();
                alarmEventQueryVo.setDeviceIds(JSON.toJSONString(deviceInfoMap.keySet()));
                alarmEventQueryVo.setEventLevel(alarmQueryVo.getEventLevel());
                alarmEventQueryVo.setPage(alarmQueryVo.getPage());
                alarmEventQueryVo.setSize(alarmQueryVo.getSize());
                alarmEventQueryVo.setIgnoreStatus(alarmQueryVo.getIgnoreStatus());
                alarmEventQueryVo.setEventStatus(alarmQueryVo.getEventStatus());
                alarmEventQueryVo.setStartDate(alarmQueryVo.getStartDate());
                alarmEventQueryVo.setEndDate(alarmQueryVo.getEndDate());
                PageDto<DeviceAlarmEventListDto> responsePage = deviceService.findAllDeviceEventList(alarmEventQueryVo).getData();
                //对数据进行组装
                if (responsePage != null && CollectionUtils.isNotEmpty(responsePage.getItems())) {
                    BeanUtils.copyProperties(responsePage, resultPage);
                    resultPage.setItems(responsePage.getItems().stream().map(alarmEvent -> {
                        AlarmListDto result = new AlarmListDto();
                        BeanUtils.copyProperties(alarmEvent, result);
                        if (StringUtil.isEmpty(alarmEvent.getIgnoreStatus())) {
                            result.setIgnoreStatus(0);
                        }
                        if (deviceInfoMap.containsKey(alarmEvent.getDeviceId())) {
                            DeviceBasicInfoDto deviceInfo = deviceInfoMap.get(alarmEvent.getDeviceId());
                            result.setDeviceNumber(deviceInfo.getDeviceNumber());
                            result.setDeviceName(deviceInfo.getDeviceName());
                            result.setSiteName(deviceInfo.getSiteName());
                            result.setTypeName(deviceInfo.getTypeName());
                        }
                        if (StringUtil.isNotEmpty(result.getEventLevel())) {
                            //事件级别 1-次要告警 2-重要告警 3-紧急告警 4-提示告警 5-离线告警
                            switch (result.getEventLevel()) {
                                case 1:
                                    result.setLevelName("次要告警");
                                    break;
                                case 2:
                                    result.setLevelName("重要告警");
                                    break;
                                case 3:
                                    result.setLevelName("紧急告警");
                                    break;
                                case 4:
                                    result.setLevelName("提示告警");
                                    break;
                                case 5:
                                    result.setLevelName("离线告警");
                                    break;
                                default:
                                    result.setLevelName("未知告警");
                            }
                        }
                        if (StringUtil.isNotEmpty(result.getCreateTime()) && StringUtil.isNotEmpty(result.getUpdateTime())) {
                            LocalDateTime createTime = DateUtil.strToLocalDateTime(result.getCreateTime());
                            LocalDateTime updateTime = DateUtil.strToLocalDateTime(result.getUpdateTime());
                            result.setAlarmDuration(DateUtil.secToTime((int) ChronoUnit.SECONDS.between(createTime, updateTime)));
                        } else if (StringUtil.isNotEmpty(result.getCreateTime())) {
                            LocalDateTime createTime = DateUtil.strToLocalDateTime(result.getCreateTime());
                            LocalDateTime updateTime = LocalDateTime.now();
                            result.setAlarmDuration(DateUtil.secToTime((int) ChronoUnit.SECONDS.between(createTime, updateTime)));
                            if (StringUtil.isNotEmpty(alarmEvent.getEventStatus()) && alarmEvent.getEventStatus() == 0) { //未恢复 更新时间给空
                                result.setUpdateTime(null);
                            }
                        }
                        return result;
                    }).collect(Collectors.toList()));
                }
            }
        }
        return ResponseResult.ok(resultPage);
    }

    @Override
    public ResponseResult<Void> updateEventIgnoreStatus(String id, Integer ignoreStatus) {
        return deviceService.updateEventIgnoreStatus(id, ignoreStatus);
    }

    @Override
    public ResponseResult<AlarmSiteCountDto> alarmSiteCount(String userId, List<String> siteIds, String siteId, String startDate, String endDate) {
        //返回的对象
        AlarmSiteCountDto result = new AlarmSiteCountDto();
        if (StringUtil.isEmpty(userId) || StringUtil.isEmpty(startDate) || StringUtil.isEmpty(endDate)) {
            return ResponseResult.paramError(ResponseResult.PARAM_ERROR);
        }
        try {
            List<DeviceAlarmEventListDto> deviceAlarmEventList = Lists.newArrayList();
            if (StringUtil.isEmpty(siteId)) {
                if (CollectionUtils.isEmpty(siteIds)) {
                    //根据用户id查询多个站点id
                    siteIds = systemService.findAllOrganEmpowerByUserId(userId).getData().stream().map(OrganEmpowerListDto::getSiteId)
                            .filter(StringUtil::isNotEmpty).distinct().collect(Collectors.toList());
                }
                if (CollectionUtils.isNotEmpty(siteIds)) {
                    //根据多个站点id查询站点名称
                    Map<String, SiteInfoDto> siteInfoMap = deviceService.findSiteBasicInfoByIds(siteIds).getData();

                    //根据多个站点id查询设备数据
                    siteIds = siteInfoMap.values().stream().map(SiteInfoDto::getId).collect(Collectors.toList());
                    Map<String, List<DeviceBasicInfoDto>> siteDeviceInfoMap = deviceService.findDeviceBasicInfoBySiteIds(siteIds, null).getData();

                    if (MapUtils.isNotEmpty(siteDeviceInfoMap)) {
                        //根据多个设备id查询设备告警数据
                        Set<String> deviceIds = siteDeviceInfoMap.values().stream().flatMap(d -> d.stream().map(DeviceBasicInfoDto::getId))
                                .collect(Collectors.toSet());
                        DeviceAlarmEventQueryVo alarmEventQueryVo = new DeviceAlarmEventQueryVo();
                        alarmEventQueryVo.setDeviceIds(JSON.toJSONString(deviceIds));
                        alarmEventQueryVo.setPage(1);
                        alarmEventQueryVo.setSize(999999);
                        alarmEventQueryVo.setStartDate(startDate);
                        alarmEventQueryVo.setEndDate(endDate);
                        deviceAlarmEventList = deviceService.findAllDeviceEventList(alarmEventQueryVo).getData().getItems();

                        //获取告警列表数据
                        List<AlarmNumDto> alarmNumList = Lists.newArrayList();
                        alarmNumList.add(AlarmNumDto.builder().dataName("全部").alarmNum((long) deviceAlarmEventList.size()).build());
                        Map<String, Long> deviceAlarmMap = deviceAlarmEventList.stream().collect(Collectors.groupingBy(DeviceAlarmEventListDto::getDeviceId,
                                Collectors.counting()));
                        alarmNumList.addAll(siteInfoMap.values().stream().map(siteInfo -> {
                            AlarmNumDto alarmNumDto = new AlarmNumDto();
                            alarmNumDto.setDataId(siteInfo.getId());
                            alarmNumDto.setDataName(siteInfo.getSiteName());
                            if (siteDeviceInfoMap.containsKey(siteInfo.getId())) {
                                alarmNumDto.setAlarmNum(siteDeviceInfoMap.get(siteInfo.getId()).stream().filter(d -> deviceAlarmMap.containsKey(d.getId()))
                                        .mapToLong(d -> deviceAlarmMap.get(d.getId())).sum());
                            }
                            return alarmNumDto;
                        }).collect(Collectors.toList()));
                        result.setAlarmNumList(alarmNumList);
                    }
                }
            } else {
                //根据站点id查询设备数据
                List<DeviceBasicInfoDto> deviceInfoList = deviceService.findDeviceBasicInfoBySiteIds(Collections.singletonList(siteId), null).getData()
                        .values().stream().flatMap(Collection::stream).collect(Collectors.toList());
                if (CollectionUtils.isNotEmpty(deviceInfoList)) {
                    //根据多个设备id查询设备告警数据
                    Set<String> deviceIds = deviceInfoList.stream().map(DeviceBasicInfoDto::getId).collect(Collectors.toSet());
                    DeviceAlarmEventQueryVo alarmEventQueryVo = new DeviceAlarmEventQueryVo();
                    alarmEventQueryVo.setDeviceIds(JSON.toJSONString(deviceIds));
                    alarmEventQueryVo.setPage(1);
                    alarmEventQueryVo.setSize(999999);
                    alarmEventQueryVo.setStartDate(startDate);
                    alarmEventQueryVo.setEndDate(endDate);
                    deviceAlarmEventList = deviceService.findAllDeviceEventList(alarmEventQueryVo).getData().getItems();

                    //获取告警列表数据
                    Map<String, Long> deviceAlarmMap = deviceAlarmEventList.stream().collect(Collectors.groupingBy(DeviceAlarmEventListDto::getDeviceId,
                            Collectors.counting()));
                    result.setAlarmNumList(deviceInfoList.stream().map(deviceInfo -> {
                        AlarmNumDto alarmNumDto = new AlarmNumDto();
                        alarmNumDto.setDataId(deviceInfo.getId());
                        alarmNumDto.setDataName(deviceInfo.getDeviceName());
                        alarmNumDto.setAlarmNum(deviceAlarmMap.getOrDefault(deviceInfo.getId(), 0L));
                        return alarmNumDto;
                    }).collect(Collectors.toList()));
                }
            }
            if (CollectionUtils.isNotEmpty(deviceAlarmEventList)) {
                //日期列表
                List<String> dateList = DateUtil.getDateBetween(1, startDate, endDate);
                //获取告警趋势数据
                Map<String, Long> alarmTrendMap = deviceAlarmEventList.stream().filter(d -> StringUtil.isNotEmpty(d.getCreateTime()))
                        .collect(Collectors.groupingBy(d -> d.getCreateTime().substring(0, 10), Collectors.counting()));
                result.setAlarmTrendMap(new TreeMap<>(dateList.stream().collect(Collectors.toMap(d -> d, d -> alarmTrendMap.getOrDefault(d, 0L), (k1, k2) -> k1))));

                //获取告警等级统计
                Map<Integer, Long> alarmLevelMap = deviceAlarmEventList.stream().filter(d -> StringUtil.isNotEmpty(d.getEventLevel()))
                        .collect(Collectors.groupingBy(DeviceAlarmEventListDto::getEventLevel, Collectors.counting()));
                alarmLevelMap.put(0, deviceAlarmEventList.stream().filter(d -> StringUtil.isEmpty(d.getEventLevel())).count());
                result.setAlarmLevelMap(alarmLevelMap);

                //获取持续时间统计
                List<Integer> durationList = Arrays.asList(1, 2, 3, 4, 5, 6);
                Map<Integer, Long> durationMap = deviceAlarmEventList.stream().filter(d -> StringUtil.isNotEmpty(d.getCreateTime()) && StringUtil.isNotEmpty(d.getUpdateTime()))
                        .collect(Collectors.groupingBy(d -> {
                            LocalDateTime createTime = DateUtil.strToLocalDateTime(d.getCreateTime());
                            LocalDateTime updateTime = DateUtil.strToLocalDateTime(d.getUpdateTime());
                            long minutes = DateUtil.compareDiffBetweenMinutes(createTime, updateTime);
                            //时间类型 1-(<1h) 2-(1-3h) 3-(3-12h) 4-(12-24h) 5-(24-72h) 6-(>72h)
                            if (minutes < 60) {
                                return 1;
                            } else if (minutes < 180) {
                                return 2;
                            } else if (minutes < 720) {
                                return 3;
                            } else if (minutes < 1440) {
                                return 4;
                            } else if (minutes < 4320) {
                                return 5;
                            } else {
                                return 6;
                            }
                        }, Collectors.counting()));
                result.setDurationMap(new TreeMap<>(durationList.stream().collect(Collectors.toMap(d -> d, d -> durationMap.getOrDefault(d, 0L)))));
            }
        } catch (RuntimeException e) {
            log.error("获取站点告警分析数据异常", e);
            return ResponseResult.paramError("获取站点告警分析数据异常");
        }

        return ResponseResult.ok(result);
    }

    @Override
    public ResponseResult<AlarmDeviceCountDto> alarmDeviceCount(String deviceId, String startDate, String endDate) {
        //返回的对象
        AlarmDeviceCountDto result = new AlarmDeviceCountDto();
        try {
            if (StringUtil.isEmpty(deviceId) || StringUtil.isEmpty(startDate) || StringUtil.isEmpty(endDate)) {
                return ResponseResult.paramError(ResponseResult.PARAM_ERROR);
            }
            //根据查询条件查询设备告警数据
            DeviceAlarmEventQueryVo alarmEventQueryVo = new DeviceAlarmEventQueryVo();
            alarmEventQueryVo.setDeviceIds(JSON.toJSONString(Collections.singleton(deviceId)));
            alarmEventQueryVo.setPage(1);
            alarmEventQueryVo.setSize(999999);
            alarmEventQueryVo.setStartDate(startDate);
            alarmEventQueryVo.setEndDate(endDate);
            List<DeviceAlarmEventListDto> deviceAlarmEventList = deviceService.findAllDeviceEventList(alarmEventQueryVo).getData().getItems();
            if (CollectionUtils.isNotEmpty(deviceAlarmEventList)) {
                //日期列表
                List<String> dateList = DateUtil.getDateBetween(1, startDate, endDate);
                //获取告警趋势数据
                Map<String, Long> alarmTrendMap = deviceAlarmEventList.stream().filter(d -> StringUtil.isNotEmpty(d.getCreateTime()))
                        .collect(Collectors.groupingBy(d -> d.getCreateTime().substring(0, 10), Collectors.counting()));
                result.setAlarmTrendMap(new TreeMap<>(dateList.stream().collect(Collectors.toMap(d -> d, d -> alarmTrendMap.getOrDefault(d, 0L),
                        (k1, k2) -> k1))));
                //获取散点分析数据
                List<AlarmDeviceCountDto.ScatterDto> scatterList = Lists.newArrayList();
                deviceAlarmEventList.stream().filter(s -> StringUtil.isNotEmpty(s.getEventName()))
                        .collect(Collectors.groupingBy(DeviceAlarmEventListDto::getEventName)).forEach((eventName, eventList) -> {
                            AlarmDeviceCountDto.ScatterDto scatterDto = new AlarmDeviceCountDto.ScatterDto();
                            scatterDto.setEventName(eventList.get(0).getEventName());
                            scatterDto.setAlarmFrequency(eventList.size());
                            scatterDto.setTotalDuration(DoubleUtil.getToDouble(eventList.stream()
                                    .filter(e -> StringUtil.isNotEmpty(e.getCreateTime()) && StringUtil.isNotEmpty(e.getUpdateTime()))
                                    .mapToDouble(e -> {
                                        LocalDateTime createTime = DateUtil.strToLocalDateTime(e.getCreateTime());
                                        LocalDateTime updateTime = DateUtil.strToLocalDateTime(e.getUpdateTime());
                                        return (double) DateUtil.compareDiffBetweenMinutes(createTime, updateTime) / 60;
                                    }).sum()));
                            scatterList.add(scatterDto);
                        });
                result.setScatterMap(scatterList.stream().collect(Collectors.groupingBy(AlarmDeviceCountDto.ScatterDto::getAlarmFrequency)));

                //获取设备告警分析
                Map<Integer, Long> alarmLevelMap = deviceAlarmEventList.stream().filter(d -> StringUtil.isNotEmpty(d.getEventLevel()))
                        .collect(Collectors.groupingBy(DeviceAlarmEventListDto::getEventLevel, Collectors.counting()));
                alarmLevelMap.put(0, deviceAlarmEventList.stream().filter(d -> StringUtil.isEmpty(d.getEventLevel())).count());
                result.setAlarmLevelList(alarmLevelMap.entrySet().stream().map(d -> {
                    AlarmDeviceCountDto.AlarmLevelDto alarmLevel = new AlarmDeviceCountDto.AlarmLevelDto();
                    alarmLevel.setAlarmLevel(d.getKey());
                    alarmLevel.setAlarmCount(d.getValue());
                    return alarmLevel;
                }).collect(Collectors.toList()));

                //高频告警
                Map<String, Long> alarmEventMap = deviceAlarmEventList.stream().filter(d -> StringUtil.isNotEmpty(d.getEventName()))
                        .collect(Collectors.groupingBy(DeviceAlarmEventListDto::getEventName, Collectors.counting()));
                result.setHighFrequencyAlarmList(alarmEventMap.entrySet().stream().sorted(Map.Entry.<String, Long>comparingByValue().reversed()) // 按值降序排序
                        .limit(Math.min(3, alarmEventMap.size())) // 截取前三条
                        .map(a -> {
                            AlarmDeviceCountDto.HighFrequencyDto highFrequency = new AlarmDeviceCountDto.HighFrequencyDto();
                            highFrequency.setEventName(a.getKey());
                            highFrequency.setAlarmCount(a.getValue());
                            highFrequency.setProportion((int) (DoubleUtil.getToDouble((double) a.getValue() / deviceAlarmEventList.size()) * 100));
                            return highFrequency;
                        }).collect(Collectors.toList()));

                //累计时长较高的告警列表
                Map<String, Double> alarmTimeMap = deviceAlarmEventList.stream().filter(d -> StringUtil.isNotEmpty(d.getCreateTime())
                                && StringUtil.isNotEmpty(d.getUpdateTime()) && StringUtil.isNotEmpty(d.getEventName()))
                        .collect(Collectors.groupingBy(DeviceAlarmEventListDto::getEventName, Collectors.summingDouble(d -> {
                            LocalDateTime createTime = DateUtil.strToLocalDateTime(d.getCreateTime());
                            LocalDateTime updateTime = DateUtil.strToLocalDateTime(d.getUpdateTime());
                            return (double) DateUtil.compareDiffBetweenMinutes(createTime, updateTime) / 60;
                        })));
                result.setAccDurationAlarmList(alarmTimeMap.entrySet().stream().sorted(Map.Entry.<String, Double>comparingByValue().reversed()) // 按值降序排序
                        .limit(Math.min(3, alarmEventMap.size())) // 截取前三条
                        .map(a -> {
                            AlarmDeviceCountDto.ScatterDto scatter = new AlarmDeviceCountDto.ScatterDto();
                            scatter.setEventName(a.getKey());
                            scatter.setTotalDuration(DoubleUtil.getToDouble(a.getValue()));
                            return scatter;
                        }).collect(Collectors.toList()));

            }
        } catch (RuntimeException e) {
            log.error("获取设备告警分析数据异常", e);
            return ResponseResult.paramError("获取设备告警分析数据异常");
        }
        return ResponseResult.ok(result);
    }

    @Override
    public ResponseResult<Map<String, List<DeviceAlarmDto>>> findAlarmEventListByDeviceId(DeviceAlarmQueryVo deviceAlarmQueryVo) {
        //返回的对象
        Map<String, List<DeviceAlarmDto>> resultMap = Maps.newHashMap();

        //根据查询条件查询设备告警数据
        DeviceAlarmEventQueryVo alarmEventQueryVo = new DeviceAlarmEventQueryVo();
        alarmEventQueryVo.setDeviceIds(FileUtil.LEFT_SQUARE + JSON.toJSONString(deviceAlarmQueryVo.getDeviceId()) + FileUtil.RIGHT_SQUARE);
        alarmEventQueryVo.setPage(1);
        alarmEventQueryVo.setSize(999999);
        alarmEventQueryVo.setStartDate(deviceAlarmQueryVo.getStartDate());
        alarmEventQueryVo.setEndDate(deviceAlarmQueryVo.getEndDate());
        PageDto<DeviceAlarmEventListDto> responsePage = deviceService.findAllDeviceEventList(alarmEventQueryVo).getData();
        //对数据进行组装
        if (responsePage != null && CollectionUtils.isNotEmpty(responsePage.getItems())) {
            resultMap = responsePage.getItems().stream().filter(s -> StringUtil.isNotEmpty(s.getCreateTime())).map(alarmEvent -> {
                        DeviceAlarmDto result = new DeviceAlarmDto();
                        BeanUtils.copyProperties(alarmEvent, result);
                        if (StringUtil.isEmpty(alarmEvent.getIgnoreStatus())) {
                            result.setIgnoreStatus(0);
                        }
                        if (StringUtil.isNotEmpty(result.getCreateTime()) && StringUtil.isNotEmpty(result.getUpdateTime())) {
                            LocalDateTime createTime = DateUtil.strToLocalDateTime(result.getCreateTime());
                            LocalDateTime updateTime = DateUtil.strToLocalDateTime(result.getUpdateTime());
                            result.setAlarmDuration(DateUtil.secToTime((int) ChronoUnit.SECONDS.between(createTime, updateTime)));
                        } else if (StringUtil.isNotEmpty(result.getCreateTime())) {
                            LocalDateTime createTime = DateUtil.strToLocalDateTime(result.getCreateTime());
                            LocalDateTime updateTime = LocalDateTime.now();
                            result.setAlarmDuration(DateUtil.secToTime((int) ChronoUnit.SECONDS.between(createTime, updateTime)));
                        }
                        return result;
                    }).collect(Collectors.groupingBy(d -> d.getCreateTime().substring(0, 10)))
                    .entrySet().stream().sorted(Collections.reverseOrder(Map.Entry.comparingByKey()))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (k1, k2) -> k1, LinkedHashMap::new));
        }
        return ResponseResult.ok(resultMap);
    }

}
