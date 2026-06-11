package com.sunmax.devops.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.sunmax.common.config.redis.RedisGeneralUtil;
import com.sunmax.common.dto.PageDto;
import com.sunmax.common.dto.WeatherDayDto;
import com.sunmax.common.dto.data.DeviceHistoryDto;
import com.sunmax.common.dto.data.NodeDifHistoryDto;
import com.sunmax.common.dto.device.*;
import com.sunmax.common.dto.operate.OrderCountDto;
import com.sunmax.common.dto.operate.OrderQtDto;
import com.sunmax.common.dto.system.OrganEmpowerListDto;
import com.sunmax.common.dto.together.ElectConfigDto;
import com.sunmax.common.model.RealDataModel;
import com.sunmax.common.model.general.PileRealModel;
import com.sunmax.common.util.*;
import com.sunmax.common.util.oss.FileUtil;
import com.sunmax.common.vo.FunctionLogoParamVo;
import com.sunmax.common.vo.ReaFieldParamVo;
import com.sunmax.common.vo.SiteFieldParamVo;
import com.sunmax.common.vo.data.DeviceHistoryQueryVo;
import com.sunmax.common.vo.device.DeviceAlarmEventQueryVo;
import com.sunmax.devops.dto.*;
import com.sunmax.devops.service.MonitorService;
import com.sunmax.devops.service.feign.DataService;
import com.sunmax.devops.service.feign.DeviceService;
import com.sunmax.devops.service.feign.SystemService;
import com.sunmax.devops.service.feign.TogetherService;
import com.sunmax.devops.util.DeviceCommonUtil;
import com.sunmax.devops.vo.DeviceQueryVo;
import com.sunmax.devops.vo.OverviewQueryVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.compress.utils.Lists;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.sunmax.common.util.DateUtil.*;
import static com.sunmax.common.util.StringUtil.isNotEmpty;

@Slf4j
@Service
public class MonitorServiceImpl implements MonitorService {

    @Autowired
    private SystemService systemService;

    @Autowired
    private DeviceService deviceService;

    @Autowired
    private DataService dataService;

    @Autowired
    private TogetherService togetherService;

    @Override
    public ResponseResult<SiteMapDto> getSiteMapBySiteId(String siteId) {
        //返回的对象
        SiteMapDto result = new SiteMapDto();
        //根据站点id查询站点信息
        SiteInfoDto siteInfo = deviceService.findSiteBasicInfoByIds(Collections.singletonList(siteId)).getData().get(siteId);
        if (siteInfo != null) {
            result.setSiteId(siteInfo.getId());
            result.setScenarioTypes(siteInfo.getScenarioTypes());
            result.setCreateTime(DateUtil.localDateTimeToStr(siteInfo.getCreateTime()));
            //获取站点下面的储能系统id
            List<String> systemIds = Lists.newArrayList();
            if (CollectionUtils.isNotEmpty(siteInfo.getSiteScenarioTypeDtos())) {
                List<Integer> scenarioTypes = Arrays.asList(1, 2, 3);
                siteInfo.getSiteScenarioTypeDtos().stream().filter(d -> StringUtil.isNotEmpty(d.getScenarioType())
                        && scenarioTypes.contains(d.getScenarioType())).forEach(scenarioType -> {
                    //获取储能系统id
                    if (scenarioType.getScenarioType() == 2) {
                        systemIds.add(scenarioType.getId());
                    } else if (StringUtil.isNotEmpty(scenarioType.getReadwriteObject())) {
                        //光伏系统装机容量
                        JSONObject reaMap = JSONObject.parseObject(scenarioType.getReadwriteObject());
                        if (reaMap != null && reaMap.containsKey(SiteFieldParamVo.PV_CAPACITY) && StringUtil.isNotEmpty(reaMap.getString(SiteFieldParamVo.PV_CAPACITY))) {
                            result.setPvCap(result.getPvCap() + Double.parseDouble(reaMap.getString(SiteFieldParamVo.PV_CAPACITY)));
                        }
                        //充电桩装机容量
                        if (reaMap != null && reaMap.containsKey(SiteFieldParamVo.CAPACITY) && StringUtil.isNotEmpty(reaMap.getString(SiteFieldParamVo.CAPACITY))) {
                            result.setPileCap(result.getPileCap() + reaMap.getDouble(SiteFieldParamVo.CAPACITY));
                        }
                    }
                });
            }
            if (CollectionUtils.isNotEmpty(systemIds)) {
                //定义充电桩设备类型
//                List<String> pileSystemTypes = Arrays.asList("28", "29", "30", "79");
                //根据多个系统id查询储能PCS设备额定容量，储能电池簇设备额定容量，充电桩系统额定功率
                deviceService.findDeviceInfoByParentIds(systemIds).getData().forEach((systemId, deviceInfoList) -> {
                    //储能系统PCS设备额定功率
                    List<DeviceBasicInfoDto> systemPcsList = deviceInfoList.stream().filter(d -> d.getTypeId().equals("23")).collect(Collectors.toList());
                    result.setPcsPower(result.getPcsPower() + systemPcsList.stream().mapToDouble(battery -> {
                        if (battery.getReaMap().containsKey(ReaFieldParamVo.RATED_POWER) && StringUtil.isNotEmpty(battery.getReaMap().get(ReaFieldParamVo.RATED_POWER))) {
                            return Double.parseDouble(String.valueOf(battery.getReaMap().get(ReaFieldParamVo.RATED_POWER)));
                        }
                        return 0.0;
                    }).sum());

                    //储能系统电池簇设备额定容量
                    List<DeviceBasicInfoDto> systemBatteryList = deviceInfoList.stream().filter(d -> d.getTypeId().equals("25")).collect(Collectors.toList());
                    result.setBatteryCap(result.getBatteryCap() + systemBatteryList.stream().mapToDouble(battery -> {
                        if (battery.getReaMap().containsKey(ReaFieldParamVo.RATED_CAP) && StringUtil.isNotEmpty(battery.getReaMap().get(ReaFieldParamVo.RATED_CAP))) {
                            return Double.parseDouble(String.valueOf(battery.getReaMap().get(ReaFieldParamVo.RATED_CAP)));
                        }
                        return 0.0;
                    }).sum());

//                    //充电桩系统额定功率
//                    List<DeviceBasicInfoDto> systemPileList = deviceInfoList.stream().filter(d -> pileSystemTypes.contains(d.getTypeId()))
//                            .collect(Collectors.toList());
//                    result.setPileCap(result.getPileCap() + systemPileList.stream().mapToDouble(pile -> {
//                        if (pile.getReaMap().containsKey(ReaFieldParamVo.RATED_POWER) && StringUtil.isNotEmpty(pile.getReaMap().get(ReaFieldParamVo.RATED_POWER))) {
//                            return Double.parseDouble(String.valueOf(pile.getReaMap().get(ReaFieldParamVo.RATED_POWER)));
//                        }
//                        return 0.0;
//                    }).sum());
                });
            }
            result.setPileCap(DoubleUtil.getToDouble(result.getPileCap()));
            result.setPcsPower(DoubleUtil.getToDouble(result.getPcsPower()));
            result.setBatteryCap(DoubleUtil.getToDouble(result.getBatteryCap()));
            result.setPvCap(DoubleUtil.getToDouble(result.getPvCap()));
        }
        return ResponseResult.ok(result);
    }

    @Override
    public ResponseResult<PageDto<SiteListDto>> querySiteList(String userId, Integer page, Integer size, String siteName) {
        //校验请求参数
        if (StringUtil.isEmpty(userId) || StringUtil.isEmpty(page) || page < 1 || StringUtil.isEmpty(size) || size < 1) {
            return ResponseResult.paramError(ResponseResult.PARAM_ERROR);
        }
        //返回的对象
        PageDto<SiteListDto> resultPage = new PageDto<>(Lists.newArrayList(), page, size);
        //根据用户id查询多个站点id
        List<String> siteIds = systemService.findAllOrganEmpowerByUserId(userId).getData().stream().map(OrganEmpowerListDto::getSiteId)
                .filter(StringUtil::isNotEmpty).distinct().collect(Collectors.toList());
        //根据多个站点id查询站点名称和站点位置
        Map<String, SiteInfoDto> siteInfoMap = deviceService.findSiteBasicInfoByIds(siteIds).getData();
        if (MapUtils.isEmpty(siteInfoMap)) {
            return ResponseResult.ok(resultPage);
        }
        List<SiteInfoDto> siteInfoList = new ArrayList<>(siteInfoMap.values());
        if (StringUtil.isNotEmpty(siteName)) { //站点名称模糊查询
            siteInfoList = siteInfoList.stream().filter(s -> s.getSiteName().contains(siteName)).collect(Collectors.toList());
        }
        List<SiteListDto> resultList = siteInfoList.stream().map(siteInfo -> {
            SiteListDto result = new SiteListDto();
            result.setId(siteInfo.getId());
            result.setSiteName(siteInfo.getSiteName());
            result.setScenarioTypes(siteInfo.getScenarioTypes());
            if (StringUtil.isNotEmpty(siteInfo.getSiteReadwriteObject())) {
                JSONObject parseObjectMap = JSON.parseObject(siteInfo.getSiteReadwriteObject());
                //获取站点位置信息
                if (parseObjectMap.containsKey(SiteFieldParamVo.LOCATION)) {
                    result.setLocation(parseObjectMap.getString(SiteFieldParamVo.LOCATION));
                }
            }
            result.setCreateTime(DateUtil.localDateTimeToStr(siteInfo.getCreateTime()));
            return result;
        }).collect(Collectors.toList());
        resultPage = new PageDto<>(resultList, page, size);
        //根据多个站点id查询站点详情，光伏，储能，充电桩功率数据
        //站点逆变器设备 站点id -> 多个逆变器id
        Map<String, List<String>> siteInverterIdMap = Maps.newHashMap();
        //站点PCS设备 站点id -> 多个PCS设备id
        Map<String, List<String>> sitePcsIdMap = Maps.newHashMap();
        //站点充电桩设备 站点id -> 多个充电桩id
        Map<String, List<String>> sitePileIdMap = Maps.newHashMap();
        //定义充电桩设备类型
        List<String> pileSystemTypes = Arrays.asList("28", "29", "30", "79");

        siteIds = resultPage.getItems().stream().map(SiteListDto::getId).collect(Collectors.toList());
        deviceService.findDeviceBasicInfoBySiteIds(siteIds, null).getData().forEach((siteId, deviceInfo) -> {
            //站点逆变器设备
            siteInverterIdMap.put(siteId, deviceInfo.stream().filter(d -> StringUtil.isNotEmpty(d.getTypeId())
                            && (Objects.equals(d.getTypeId(), "20") || Objects.equals(d.getTypeId(), "77")))
                    .map(DeviceBasicInfoDto::getId).collect(Collectors.toList()));
            //站点PCS设备
            sitePcsIdMap.put(siteId, deviceInfo.stream().filter(d -> StringUtil.isNotEmpty(d.getTypeId())
                            && (Objects.equals(d.getTypeId(), "23") || Objects.equals(d.getTypeId(), "78")))
                    .map(DeviceBasicInfoDto::getId).collect(Collectors.toList()));
            //站点充电桩设备
            sitePileIdMap.put(siteId, deviceInfo.stream().filter(d -> pileSystemTypes.contains(d.getTypeId())).map(DeviceBasicInfoDto::getId).collect(Collectors.toList()));
        });

        //定义查询历史数据的开始时间和结束时间
        String startTime = DateUtil.getDayStart(DateUtil.localDateToStr(LocalDate.now()));
        String endTime = DateUtil.localDateTimeToStr(LocalDateTime.now());

        //查询站点下面的逆变器设备功率历史数据
        Map<String, Map<String, List<DeviceHistoryDto>>> inverterDataMap = Maps.newHashMap();
        if (MapUtils.isNotEmpty(siteInverterIdMap)) {
            DeviceHistoryQueryVo inverterQueryVo = new DeviceHistoryQueryVo();
            inverterQueryVo.setDeviceIds(siteInverterIdMap.values().stream().flatMap(List::stream).collect(Collectors.toSet()));
            inverterQueryVo.setFunctionLogos(Collections.singleton(FunctionLogoParamVo.ACTIVE_POWER));
            inverterQueryVo.setStartTime(startTime);
            inverterQueryVo.setEndTime(endTime);
            inverterQueryVo.setTimeInterval("5m");
            inverterDataMap = dataService.findDeviceHistoryValueList(inverterQueryVo).getData();
        }

        //查询站点下面的PCS设备功率历史数据
        Map<String, Map<String, List<DeviceHistoryDto>>> pcsDataMap = Maps.newHashMap();
        if (MapUtils.isNotEmpty(sitePcsIdMap)) {
            DeviceHistoryQueryVo pcsQueryVo = new DeviceHistoryQueryVo();
            pcsQueryVo.setDeviceIds(sitePcsIdMap.values().stream().flatMap(List::stream).collect(Collectors.toSet()));
            pcsQueryVo.setFunctionLogos(Collections.singleton(FunctionLogoParamVo.PCS_ACTIVE_POWER));
            pcsQueryVo.setStartTime(startTime);
            pcsQueryVo.setEndTime(endTime);
            pcsQueryVo.setTimeInterval("5m");
            pcsDataMap = dataService.findDeviceHistoryValueList(pcsQueryVo).getData();
        }

        //查询站点下面的充电桩设备历史数据
        Map<String, Map<String, List<DeviceHistoryDto>>> pileDataMap = Maps.newHashMap();
        if (MapUtils.isNotEmpty(sitePileIdMap)) {
            DeviceHistoryQueryVo pileQueryVo = new DeviceHistoryQueryVo();
            pileQueryVo.setDeviceIds(sitePileIdMap.values().stream().flatMap(List::stream).collect(Collectors.toSet()));
            pileQueryVo.setFunctionLogos(Collections.singleton(FunctionLogoParamVo.PILE_POWER));
            pileQueryVo.setStartTime(startTime);
            pileQueryVo.setEndTime(endTime);
            pileQueryVo.setTimeInterval("5m");
            pileDataMap = dataService.findDeviceHistoryValueList(pileQueryVo).getData();
        }

        //获取日期列表
        List<String> dateTimeList = DateUtil.getDateBetweenMinutes(DateUtil.strToLocalDateTime(startTime), DateUtil.strToLocalDateTime(endTime), 5);
        //对数据进行组装
        Map<String, Map<String, List<DeviceHistoryDto>>> finalInverterDataMap = inverterDataMap;
        Map<String, Map<String, List<DeviceHistoryDto>>> finalPcsDataMap = pcsDataMap;
        Map<String, Map<String, List<DeviceHistoryDto>>> finalPileDataMap = pileDataMap;
        resultPage.setItems(resultPage.getItems().stream().peek(siteInfo -> {
            //光伏数据
            //光伏逆变器功率曲线
            Map<String, Double> pvPowerMap = Maps.newHashMap();
            if (siteInverterIdMap.containsKey(siteInfo.getId())) {
                List<String> siteInverterIds = siteInverterIdMap.get(siteInfo.getId());
                pvPowerMap = finalInverterDataMap.entrySet().stream().filter(d -> siteInverterIds.contains(d.getKey()))
                        .flatMap(d -> d.getValue().entrySet().stream().flatMap(s -> s.getValue().stream()))
                        .collect(Collectors.groupingBy(DeviceHistoryDto::getDateTime, Collectors.summingDouble(i ->
                                StringUtil.isNotEmpty(i.getDataValue()) ? Double.parseDouble(String.valueOf(i.getDataValue())) : 0.0)));
            }
            //储能PCS功率曲线
            Map<String, Double> sePowerMap = Maps.newHashMap();
            if (sitePcsIdMap.containsKey(siteInfo.getId())) {
                List<String> sitePcsIds = sitePcsIdMap.get(siteInfo.getId());
                sePowerMap = finalPcsDataMap.entrySet().stream().filter(d -> sitePcsIds.contains(d.getKey()))
                        .flatMap(d -> d.getValue().entrySet().stream().flatMap(s -> s.getValue().stream()))
                        .collect(Collectors.groupingBy(DeviceHistoryDto::getDateTime, Collectors.summingDouble(i ->
                                StringUtil.isNotEmpty(i.getDataValue()) ? Double.parseDouble(String.valueOf(i.getDataValue())) : 0.0)));
            }
            //充电桩功率曲线数据
            Map<String, Double> pilePowerMap = Maps.newHashMap();
            if (sitePileIdMap.containsKey(siteInfo.getId())) {
                List<String> sitePileIds = sitePileIdMap.get(siteInfo.getId());
                pilePowerMap = finalPileDataMap.entrySet().stream().filter(d -> sitePileIds.contains(d.getKey()))
                        .flatMap(d -> d.getValue().entrySet().stream().flatMap(s -> s.getValue().stream()))
                        .collect(Collectors.groupingBy(DeviceHistoryDto::getDateTime, Collectors.summingDouble(i ->
                                StringUtil.isNotEmpty(i.getDataValue()) ? Double.parseDouble(String.valueOf(i.getDataValue())) : 0.0)));
            }
            //能源场景类型 1-光伏 2-储能 3-充电桩 4-用能 5-配电 6-换电
            if (StringUtil.isNotEmpty(siteInfo.getScenarioTypes())) {
                Set<String> scenarioTypes = Arrays.stream(siteInfo.getScenarioTypes().split(FileUtil.COMMA)).collect(Collectors.toSet());
                for (String dateTime : dateTimeList) {
                    siteInfo.getTimeList().add(dateTime.substring(11, 16));
                    //光伏逆变器功率曲线
                    if (scenarioTypes.contains("1")) {
                        siteInfo.getPvPowerList().add(pvPowerMap.containsKey(dateTime) ? DoubleUtil.getToDouble(pvPowerMap.get(dateTime)) : null);
                    }
                    //储能PCS功率曲线
                    if (scenarioTypes.contains("2")) {
                        siteInfo.getStoragePowerList().add(sePowerMap.containsKey(dateTime) ? DoubleUtil.getToDouble(sePowerMap.get(dateTime)) : null);
                    }
                    //充电桩功率曲线数据
                    if (scenarioTypes.contains("3")) {
                        siteInfo.getPilePowerList().add(pilePowerMap.containsKey(dateTime) ? DoubleUtil.getToDouble(pilePowerMap.get(dateTime)) : null);
                    }
                }
            }
        }).collect(Collectors.toList()));
        return ResponseResult.ok(resultPage);
    }

    @Override
    public ResponseResult<DeviceListDto> queryDeviceList(DeviceQueryVo deviceQueryVo) {
        //返回的对象
        DeviceListDto result = new DeviceListDto();

        //根据多个站点id查询设备信息
        List<String> siteIds = JSON.parseArray(deviceQueryVo.getSiteIds(), String.class);
        Map<String, List<DeviceBasicInfoDto>> siteDeviceMap = deviceService.findDeviceBasicInfoBySiteIds(siteIds, null).getData();
        if (MapUtils.isNotEmpty(siteDeviceMap)) {
            List<DeviceBasicInfoDto> deviceList = siteDeviceMap.values().stream().flatMap(Collection::stream).collect(Collectors.toList());
            //过滤条件查询
            deviceList = deviceList.stream().filter(device -> {
                // 关键字查询
                if (StringUtil.isNotEmpty(deviceQueryVo.getKeywordType()) && StringUtil.isNotEmpty(deviceQueryVo.getKeyword())) {
                    boolean keywordMatch = false;
                    //关键字类型 1-设备编号 2-设备名称
                    switch (deviceQueryVo.getKeywordType()) {
                        case 1:
                            keywordMatch = StringUtil.isNotEmpty(device.getDeviceNumber()) && device.getDeviceNumber().contains(deviceQueryVo.getKeyword());
                            break;
                        case 2:
                            keywordMatch = StringUtil.isNotEmpty(device.getDeviceName()) && device.getDeviceName().contains(deviceQueryVo.getKeyword());
                            break;
                        default:
                    }
                    if (!keywordMatch) {
                        return false;
                    }
                }
                // 通信状态查询
                if (StringUtil.isNotEmpty(deviceQueryVo.getTxStatus())) {
                    List<Integer> txStatus = JSON.parseArray(deviceQueryVo.getTxStatus(), Integer.class);
                    if (StringUtil.isEmpty(device.getTxStatus()) || !txStatus.contains(device.getTxStatus())) {
                        return false;
                    }
                }
                // 设备类型查询
                if (StringUtil.isNotEmpty(deviceQueryVo.getTypeId())) {
                    return !StringUtil.isEmpty(device.getTypeId()) && Objects.equals(device.getTypeId(), deviceQueryVo.getTypeId());
                }
                return true;
            }).collect(Collectors.toList());

            if (CollectionUtils.isEmpty(deviceList)) {
                return ResponseResult.ok(result);
            }

            //根据多个离线或故障的设备id查询设备未修复的告警数据
            Map<String, List<DeviceListDto.Alarm>> deviceAlarmMap = Maps.newHashMap();
            Set<String> deviceIds = deviceList.stream().filter(d -> Objects.equals(d.getTxStatus(), 2) || Objects.equals(d.getTxStatus(), 88))
                    .map(DeviceBasicInfoDto::getId).collect(Collectors.toSet());
            if (CollectionUtils.isNotEmpty(deviceIds)) {
                DeviceAlarmEventQueryVo eventQueryVo = new DeviceAlarmEventQueryVo();
                eventQueryVo.setDeviceIds(JSON.toJSONString(deviceIds));
                eventQueryVo.setEventStatus(0);
                eventQueryVo.setPage(1);
                eventQueryVo.setSize(deviceIds.size() * 20);
                List<DeviceAlarmEventListDto> deviceAlarmList = deviceService.findAllDeviceEventList(eventQueryVo).getData().getItems();
                if (CollectionUtils.isNotEmpty(deviceAlarmList)) {
                    LocalDateTime nowTime = LocalDateTime.now();
                    deviceAlarmList.stream().filter(d -> StringUtil.isNotEmpty(d.getCreateTime()))
                            .collect(Collectors.groupingBy(DeviceAlarmEventListDto::getDeviceId))
                            .forEach((deviceId, alarmList) -> {
                                // 先排序再截取前3条，提高性能
                                List<DeviceAlarmEventListDto> top3AlarmList = alarmList.stream()
                                        .sorted(Comparator.comparing(DeviceAlarmEventListDto::getCreateTime).reversed())
                                        .limit(3)
                                        .collect(Collectors.toList());

                                deviceAlarmMap.put(deviceId, top3AlarmList.stream().map(d -> {
                                    DeviceListDto.Alarm alarm = new DeviceListDto.Alarm();
                                    alarm.setAlarmName(d.getEventName());
                                    try {
                                        LocalDateTime createTime = DateUtil.strToLocalDateTime(d.getCreateTime());
                                        if (createTime.isBefore(nowTime)) {
                                            String timeAgoStr = calculateTimeAgo(createTime, nowTime);
                                            alarm.setAlarmTime(timeAgoStr);
                                        }
                                    } catch (RuntimeException e) {
                                        // 如果日期解析失败，保持alarmTime为默认值或设置错误提示
                                        alarm.setAlarmTime(null);
                                    }
                                    return alarm;
                                }).collect(Collectors.toList()));
                            });
                }
            }

            //根据通信状态进行分组
            Map<Integer, Long> deviceTxMap = deviceList.stream().collect(Collectors.groupingBy(DeviceBasicInfoDto::getTxStatus, Collectors.counting()));
            result.setTotal((long) deviceList.size());
            deviceTxMap.forEach((txStatus, count) -> {
                //通信状态 0-未注册 1-在线 2-维护 3-故障 88-离线
                switch (txStatus) {
                    case 0:
                        result.setUnregister(count);
                        break;
                    case 1:
                        result.setOnline(count);
                        break;
                    case 2:
                    case 3:
                        result.setFault(count);
                        break;
                    case 88:
                        result.setOffline(count);
                        break;
                }
            });

            result.setSiteDeviceMap(deviceList.stream().map(device -> {
                DeviceListDto.Device deviceDto = new DeviceListDto.Device();
                BeanUtils.copyProperties(device, deviceDto);
                if (deviceAlarmMap.containsKey(device.getId())) {
                    deviceDto.setAlarmList(deviceAlarmMap.get(device.getId()));
                }
                if (StringUtil.isNotEmpty(device.getCreateTime())) {
                    deviceDto.setCreateTime(DateUtil.localDateTimeToStr(device.getCreateTime()));
                }
                if (StringUtil.isNotEmpty(device.getReadwriteObject())) {
                    JSONObject jsonObject = JSON.parseObject(device.getReadwriteObject());
                    //额定功率或者额定容量
                    if (jsonObject.containsKey(ReaFieldParamVo.RATED_POWER) && StringUtil.isNotEmpty(jsonObject.getDouble(ReaFieldParamVo.RATED_POWER))) {
                        deviceDto.setRatedPower(jsonObject.getDouble(ReaFieldParamVo.RATED_POWER));
                    } else if (jsonObject.containsKey(ReaFieldParamVo.RATED_CAP) && StringUtil.isNotEmpty(jsonObject.getDouble(ReaFieldParamVo.RATED_CAP))) {
                        deviceDto.setRatedPower(jsonObject.getDouble(ReaFieldParamVo.RATED_CAP));
                    }
                    //设备型号
                    if (jsonObject.containsKey(ReaFieldParamVo.MODEL) && StringUtil.isNotEmpty(jsonObject.getString(ReaFieldParamVo.MODEL))) {
                        deviceDto.setEquipmentModel(jsonObject.getString(ReaFieldParamVo.MODEL));
                    }
                }
                return deviceDto;
            }).sorted(Comparator.comparing(DeviceListDto.Device::getCreateTime).reversed()).collect(Collectors.groupingBy(DeviceListDto.Device::getSiteName)));
        }
        return ResponseResult.ok(result);
    }

    @Override
    public ResponseResult<SiteOverviewDto> findSiteOverview(String siteId) {
        //返回的对象
        SiteOverviewDto result = new SiteOverviewDto();
        if (StringUtil.isEmpty(siteId)) {
            return ResponseResult.paramError(ResponseResult.PARAM_ISNULL);
        }
        //根据站点id查询站点信息
        SiteInfoDto siteInfo = deviceService.findSiteBasicInfoByIds(Collections.singletonList(siteId)).getData().get(siteId);
        if (siteInfo == null) {
            return ResponseResult.paramError(ResponseResult.PARAM_ERROR);
        }

        //获取站点投运天数,变压器安全容量,光伏额定容量,今日天气预报
        String siteReadwriteObject = siteInfo.getSiteReadwriteObject();
        if (StringUtil.isNotEmpty(siteReadwriteObject)) {
            JSONObject objectMap = JSON.parseObject(siteReadwriteObject);
            //获取投运天数
            LocalDate nowDate = LocalDate.now();
            LocalDate createDate = null;
            if (objectMap.containsKey(SiteFieldParamVo.OFFICIAL_RUN_TIME) && StringUtil.isNotEmpty(objectMap.get(SiteFieldParamVo.OFFICIAL_RUN_TIME))) {
                createDate = DateUtil.strToLocalDate(objectMap.getString(SiteFieldParamVo.OFFICIAL_RUN_TIME));
            } else if (StringUtil.isNotEmpty(siteInfo.getCreateTime())) {
                createDate = siteInfo.getCreateTime().toLocalDate();
            }
            if (StringUtil.isNotEmpty(createDate)) {
                long runDays = ChronoUnit.DAYS.between(createDate, nowDate);
                result.setRunDays(runDays >= 0 ? runDays : 0);
            }
            //获取变压器安全容量
            if (objectMap.containsKey(SiteFieldParamVo.TRAN_SAFE_CAP)) {
                result.setTranSafeCap(objectMap.getDouble(SiteFieldParamVo.TRAN_SAFE_CAP));
            }
            //获取站点今日天气预报
            if (objectMap.containsKey(SiteFieldParamVo.LOCATION)) {
                JSONObject location = objectMap.getJSONObject(SiteFieldParamVo.LOCATION);
                if (location.containsKey(SiteFieldParamVo.LONGITUDE) && location.containsKey(SiteFieldParamVo.LATITUDE)) {
                    Double longitude = location.getDouble(SiteFieldParamVo.LONGITUDE); //经度
                    Double latitude = location.getDouble(SiteFieldParamVo.LATITUDE);//纬度
                    //查询天气数据
                    if (StringUtil.isNotEmpty(longitude) && StringUtil.isNotEmpty(latitude)) {
                        List<WeatherDayDto> weatherDayList = WeatherHfUtil.getWeatherDay(longitude, latitude, "3d");
                        if (CollectionUtils.isNotEmpty(weatherDayList)) {
                            WeatherDayDto weatherDay = weatherDayList.stream().collect(Collectors.toMap(WeatherDayDto::getFxDate,
                                    a -> a)).get(DateUtil.localDateToStr(nowDate));
                            if (weatherDay != null) {
                                result.setTempMin(weatherDay.getTempMin());
                                result.setTempMax(weatherDay.getTempMax());
                                result.setIconDay(weatherDay.getIconDay());
                            }
                        }
                    }
                }
            }
        }

        //获取光伏系统容量
        if (CollectionUtils.isNotEmpty(siteInfo.getSiteScenarioTypeDtos())) {
            siteInfo.getSiteScenarioTypeDtos().stream().filter(d -> StringUtil.isNotEmpty(d.getScenarioType())
                    && (Objects.equals(d.getScenarioType(), 1) || Objects.equals(d.getScenarioType(), 3))).forEach(scenarioType -> {
                if (StringUtil.isNotEmpty(scenarioType.getReadwriteObject())) {
                    //光伏系统装机容量
                    JSONObject reaMap = JSONObject.parseObject(scenarioType.getReadwriteObject());
                    if (reaMap != null && reaMap.containsKey(SiteFieldParamVo.PV_CAPACITY) && StringUtil.isNotEmpty(reaMap.getString(SiteFieldParamVo.PV_CAPACITY))) {
                        result.setPvCap(result.getPvCap() + reaMap.getDouble(SiteFieldParamVo.PV_CAPACITY));
                    }
                    //充电桩装机容量
                    if (reaMap != null && reaMap.containsKey(SiteFieldParamVo.CAPACITY) && StringUtil.isNotEmpty(reaMap.getString(SiteFieldParamVo.CAPACITY))) {
                        result.setPileCap(result.getPileCap() + reaMap.getDouble(SiteFieldParamVo.CAPACITY));
                    }
                }
            });
            result.setPvCap(DoubleUtil.getToDouble(result.getPvCap()));
        }


        //根据站点id查询设备列表
        //定义关口表列表
        Set<String> gatewayIds = Sets.newHashSet();
        //定义光伏逆变器列表
        Set<String> inverterIds = Sets.newHashSet();
        //定义储能PCS列表
        Set<String> pcsIds = Sets.newHashSet();
        //定义电能表列表
        Set<String> meterIds = Sets.newHashSet();
        //定义储能电池蔟列表 设备id -> 电池簇额定容量
        Map<String, Double> batteryCapMap = Maps.newHashMap();
        //定义电桩列表
        List<String> pileCodes = Lists.newArrayList();
        deviceService.findDeviceBasicInfoBySiteIds(Collections.singletonList(siteId), null).getData().get(siteId).forEach(device -> {
            String typeId = device.getTypeId();
            Map<String, Object> reaMap = device.getReaMap();
            if (StringUtil.isNotEmpty(typeId)) {
                switch (typeId) {
                    case "39":
                        gatewayIds.add(device.getId());
                        break;
                    case "20":
                        inverterIds.add(device.getId());
                        break;
                    case "23":
                        pcsIds.add(device.getId());
                        if (MapUtils.isNotEmpty(reaMap) && reaMap.containsKey(ReaFieldParamVo.RATED_POWER) && StringUtil.isNotEmpty(reaMap.get(ReaFieldParamVo.RATED_POWER))) {
                            result.setPcsPower(result.getPcsPower() + DoubleUtil.objToDouble(reaMap.get(ReaFieldParamVo.RATED_POWER)));
                        }
                        break;
                    case "38":
                        meterIds.add(device.getId());
                        break;
                    case "25":
                        if (MapUtils.isNotEmpty(reaMap) && reaMap.containsKey(ReaFieldParamVo.RATED_CAP) && StringUtil.isNotEmpty(reaMap.get(ReaFieldParamVo.RATED_CAP))) {
                            result.setBatteryCap(result.getBatteryCap() + DoubleUtil.objToDouble(reaMap.get(ReaFieldParamVo.RATED_CAP)));
                            batteryCapMap.put(device.getId(), DoubleUtil.objToDouble(reaMap.get(ReaFieldParamVo.RATED_CAP)));
                        }
                        break;
                    case "28":
                    case "29":
                    case "30":
                    case "79":
                        if (StringUtil.isNotEmpty(device.getDeviceNumber())) {
                            pileCodes.add(device.getDeviceNumber());
                        }
//                        if (MapUtils.isNotEmpty(reaMap) && reaMap.containsKey(ReaFieldParamVo.RATED_POWER) && StringUtil.isNotEmpty(reaMap.get(ReaFieldParamVo.RATED_POWER))) {
//                            result.setPileCap(result.getPileCap() + DoubleUtil.objToDouble(reaMap.get(ReaFieldParamVo.RATED_POWER)));
//                        }
                        break;
                }
            }
        });
        result.setPcsPower(DoubleUtil.getToDouble(result.getPcsPower()));
        result.setBatteryCap(DoubleUtil.getToDouble(result.getBatteryCap()));
        result.setPileCap(DoubleUtil.getToDouble(result.getPileCap()));

        //定义今日查询开始时间和结束时间
        String nowDate = DateUtil.localDateToStr(LocalDate.now());
        String startTime = DateUtil.getDayStart(nowDate);
        String endTime = DateUtil.localDateTimeToStr(LocalDateTime.now());

        //根据站点id查询站点电价配置(统计光伏收益和储能收益使用)
        //获取站点光伏上网电价和消纳电价设置信息
        //光伏上网电价 日期 -> (时段类型 -> 电费)
        Map<String, Map<Integer, BigDecimal>> pvInternetElectMap = Maps.newHashMap();
        //光伏消纳电价 日期 -> (每半个小时时段(00:00) -> 电费)
        Map<String, Map<String, BigDecimal>> pvConsumeElectMap = Maps.newHashMap();
        //储能购电电价 日期 -> (时段类型 -> 电费)
        Map<String, Map<Integer, BigDecimal>> sePurchaseElectMap = Maps.newHashMap();
        //储能售电电价 日期 -> (时段类型 -> 电费)
        Map<String, Map<Integer, BigDecimal>> seSaleElectMap = Maps.newHashMap();

        List<ElectConfigDto> electConfigList = togetherService.findElectConfigListBySiteIds(Collections.singletonList(siteId),
                String.join(FileUtil.COMMA, "2", "3", "4", "5"), nowDate, nowDate).getData().get(siteId);
        if (CollectionUtils.isNotEmpty(electConfigList)) {
            electConfigList.stream().collect(Collectors.toMap(ElectConfigDto::getModuleType, a -> a, (k1, k2) -> k1))
                    .forEach((moduleType, electConfig) -> {
                        List<ElectConfigDto.ElectTimeFrame> electTimeFrameList = electConfig.getElectTimeFrameList();
                        //电价配置 电价配置id -> (时段类型 -> 电费)
                        Map<Integer, BigDecimal> timeFrameMap = electTimeFrameList.stream().collect(Collectors.toMap(ElectConfigDto.ElectTimeFrame::getPeriodType,
                                ElectConfigDto.ElectTimeFrame::getElectMoney, (k1, k2) -> k2));
                        //电价模块类型 1-电网电价 2-光伏上网电价 3-光伏消纳电价 4-储能售电电价 5-储能购电电价 6-电桩售电电价 7-电桩购电电价
                        switch (moduleType) {
                            case 2:
                                pvInternetElectMap.put(nowDate, timeFrameMap);
                                break;
                            case 3:
                                pvConsumeElectMap.put(nowDate, buildHalfHourMapForOneConfig(electTimeFrameList));
                                break;
                            case 4:
                                seSaleElectMap.put(nowDate, timeFrameMap);
                                break;
                            case 5:
                                sePurchaseElectMap.put(nowDate, timeFrameMap);
                                break;
                        }
                    });
        }

        //获取关口表今日上网电量和今日下网电量
        //查询光伏关口表每天的正向有功电量和反向有功电量数据和(尖峰平谷)反向有功电量数据
        Map<String, List<NodeDifHistoryDto>> gatewayDataMap = Maps.newHashMap();
        //查询关口表每半个小时的上网电量
        Map<String, Double> pvInternetQtMap = Maps.newHashMap();
        if (CollectionUtils.isNotEmpty(gatewayIds)) {
            DeviceHistoryQueryVo gatewayQueryVo = new DeviceHistoryQueryVo();
            gatewayQueryVo.setDeviceIds(gatewayIds);
            gatewayQueryVo.setFunctionLogos(new HashSet<>(Arrays.asList(FunctionLogoParamVo.TOTAL_POSITIVE_ACTIVE_ENERGY,
                    FunctionLogoParamVo.TOTAL_INVERSE_ACTIVE_ENERGY, FunctionLogoParamVo.TOP_REV_KWH, FunctionLogoParamVo.PEAK_REV_KWH,
                    FunctionLogoParamVo.PLAIN_REV_KWH, FunctionLogoParamVo.VALLEY_REV_KWH, FunctionLogoParamVo.DEEP_REV_KWH)));
            gatewayQueryVo.setStartTime(startTime);
            gatewayQueryVo.setEndTime(endTime);
            gatewayQueryVo.setTimeInterval("1d");
            List<NodeDifHistoryDto> nodeDifList = dataService.findNodeDifHistoryListFeign(gatewayQueryVo).getData().values().stream()
                    .flatMap(map -> map.values().stream().flatMap(Collection::stream)).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(nodeDifList)) {
                gatewayDataMap = nodeDifList.stream().collect(Collectors.groupingBy(NodeDifHistoryDto::getFunctionLogo));
                nodeDifList.stream().filter(d -> Objects.equals(d.getFunctionLogo(), FunctionLogoParamVo.TOTAL_POSITIVE_ACTIVE_ENERGY)
                                || Objects.equals(d.getFunctionLogo(), FunctionLogoParamVo.TOTAL_INVERSE_ACTIVE_ENERGY))
                        .collect(Collectors.groupingBy(NodeDifHistoryDto::getFunctionLogo, Collectors.summingDouble(d ->
                                DoubleUtil.getObjSub(d.getFirstDataValue(), d.getLastDataValue())))).forEach((functionLogo, value) -> {
                            switch (functionLogo) {
                                case FunctionLogoParamVo.TOTAL_POSITIVE_ACTIVE_ENERGY: //今日下网电量
                                    result.setDayLowerQt(result.getDayLowerQt() + value);
                                    break;
                                case FunctionLogoParamVo.TOTAL_INVERSE_ACTIVE_ENERGY: //今日上网电量
                                    result.setDayNetQt(result.getDayNetQt() + value);
                                    break;
                            }
                        });
                result.setDayLowerQt(DoubleUtil.getAbsDouble(result.getDayLowerQt()));
                result.setDayNetQt(DoubleUtil.getAbsDouble(result.getDayNetQt()));
            }

            DeviceHistoryQueryVo gatewayInternetQueryVo = new DeviceHistoryQueryVo();
            gatewayInternetQueryVo.setDeviceIds(gatewayIds);
            gatewayInternetQueryVo.setFunctionLogos(Collections.singleton(FunctionLogoParamVo.TOTAL_INVERSE_ACTIVE_ENERGY));
            gatewayInternetQueryVo.setStartTime(startTime);
            gatewayInternetQueryVo.setEndTime(endTime);
            gatewayInternetQueryVo.setTimeInterval("30m");
            pvInternetQtMap = dataService.findNodeDifHistoryListFeign(gatewayInternetQueryVo).getData().values().stream()
                    .flatMap(map -> map.values().stream().flatMap(Collection::stream))
                    .filter(n -> isNotEmpty(n.getFirstDateTime()))
                    .collect(Collectors.groupingBy(NodeDifHistoryDto::getFirstDateTime, Collectors.summingDouble(s ->
                            DoubleUtil.getObjSub(s.getFirstDataValue(), s.getLastDataValue()))));
        }
        //获取光伏今日发电量和今日收益
        if (CollectionUtils.isNotEmpty(inverterIds)) {
            //查询逆变器发电量
            DeviceHistoryQueryVo deviceHistoryQueryVo = new DeviceHistoryQueryVo();
            deviceHistoryQueryVo.setDeviceIds(inverterIds);
            deviceHistoryQueryVo.setFunctionLogos(Collections.singleton(FunctionLogoParamVo.TOTAL_POWER_GENERATION));
            deviceHistoryQueryVo.setStartTime(startTime);
            deviceHistoryQueryVo.setEndTime(endTime);
            deviceHistoryQueryVo.setTimeInterval("30m");
            List<NodeDifHistoryDto> inverterDataList = dataService.findNodeDifHistoryListFeign(deviceHistoryQueryVo).getData()
                    .values().stream().flatMap(map -> map.values().stream().flatMap(Collection::stream))
                    .collect(Collectors.toList());
            //获取光伏逆变器今日发电量
            double dayPvQt = inverterDataList.stream().mapToDouble(s -> DoubleUtil.getObjSub(s.getFirstDataValue(), s.getLastDataValue())).sum();
            result.setDayPvQt(DoubleUtil.getAbsDouble(dayPvQt));
            //获取光伏上网收益
            BigDecimal pvInternetMoney = this.processPvInternetIncome(Collections.singletonList(nowDate), pvInternetElectMap, gatewayDataMap);
            //获取光伏消纳收益
            //逆变器发电量(年月日时分秒) -> 发电量
            Map<String, Double> pvInverterQtMap = inverterDataList.stream().filter(d -> StringUtil.isNotEmpty(d.getFirstDateTime()))
                    .collect(Collectors.groupingBy(NodeDifHistoryDto::getFirstDateTime, Collectors.summingDouble(d ->
                            DoubleUtil.getObjSub(d.getFirstDataValue(), d.getLastDataValue()))));
            BigDecimal pvConsumeMoney = this.processPvConsumeIncome(pvConsumeElectMap, pvInternetQtMap, pvInverterQtMap);
            //光伏今日收益(光伏今日上网收益 + 光伏今日消纳收益)
            if (!Objects.equals(pvInternetMoney, BigDecimal.ZERO) || !Objects.equals(pvConsumeMoney, BigDecimal.ZERO)) {
                result.setDayPvIncome(DoubleUtil.getToBigDecimal(pvInternetMoney.add(pvConsumeMoney)));
            }
        }
        //获取储能今日充电量,储能今日放电量,储能累计循环次数
        if (CollectionUtils.isNotEmpty(pcsIds)) {
            //查询PCS设备今日充电量和今日放电量
            DeviceHistoryQueryVo devicePcsQtQueryVo = new DeviceHistoryQueryVo();
            Set<String> functionLogos = new HashSet<>(Arrays.asList(FunctionLogoParamVo.PCS_BATTERY_CHARGE, FunctionLogoParamVo.PCS_BATTERY_DISCHARGE));
            devicePcsQtQueryVo.setDeviceIds(pcsIds);
            devicePcsQtQueryVo.setFunctionLogos(functionLogos);
            devicePcsQtQueryVo.setStartTime(startTime);
            devicePcsQtQueryVo.setEndTime(endTime);
            devicePcsQtQueryVo.setTimeInterval("1d");
            Map<String, Map<String, List<NodeDifHistoryDto>>> pcsQtHistoryDataMap = dataService.findNodeDifHistoryListFeign(devicePcsQtQueryVo).getData();
            Map<String, Double> pcsQtMap = pcsQtHistoryDataMap.entrySet().stream()
                    .flatMap(d -> d.getValue().entrySet().stream().flatMap(s -> s.getValue().stream()))
                    .collect(Collectors.groupingBy(NodeDifHistoryDto::getFunctionLogo, Collectors.summingDouble(c ->
                            DoubleUtil.getObjSub(c.getFirstDataValue(), c.getLastDataValue()))));
            //储能系统今日充电量
            if (pcsQtMap.containsKey(FunctionLogoParamVo.PCS_BATTERY_CHARGE)) {
                result.setDaySeChargeQt(DoubleUtil.getAbsDouble(pcsQtMap.getOrDefault(FunctionLogoParamVo.PCS_BATTERY_CHARGE, 0.0)));
            }
            //储能系统今日放电量
            if (pcsQtMap.containsKey(FunctionLogoParamVo.PCS_BATTERY_DISCHARGE)) {
                result.setDaySeDischargeQt(DoubleUtil.getAbsDouble(pcsQtMap.getOrDefault(FunctionLogoParamVo.PCS_BATTERY_DISCHARGE, 0.0)));
            }
            //储能系统今日循环次数 今日放电量/储能电池簇额定容量
            if (StringUtil.isNotEmpty(result.getDaySeDischargeQt()) && StringUtil.isNotEmpty(result.getBatteryCap()) && result.getBatteryCap() != 0.0) {
                result.setDaySeCycleNum(DoubleUtil.getToDouble(result.getDaySeDischargeQt() / result.getBatteryCap()));
            }
        }
        //获取储能系统今日SOC
        if (MapUtils.isNotEmpty(batteryCapMap)) {
            //获取储能电池蔟可放电量
            double batteryChargeQt = deviceService.getDeviceFunctionsRealDataByIds(batteryCapMap.keySet(), FunctionLogoParamVo.BATTERY_TOTAL_SOC).getData()
                    .entrySet().stream().mapToDouble(device -> {
                        String batteryId = device.getKey();
                        Map<String, RealDataModel> realDataMap = device.getValue();
                        if (batteryCapMap.containsKey(batteryId) && realDataMap.containsKey(FunctionLogoParamVo.BATTERY_TOTAL_SOC)) {
                            Double ratedCap = batteryCapMap.get(batteryId);
                            RealDataModel realDataModel = realDataMap.get(FunctionLogoParamVo.BATTERY_TOTAL_SOC);
                            if (realDataModel != null && isNotEmpty(realDataModel.getDataValue())) {
                                return ratedCap * (Double.parseDouble(String.valueOf(realDataModel.getDataValue())) / 100);
                            }
                        }
                        return 0.0;
                    }).sum();
            //储能系统SOC 储能电池簇可放电量/储能电池簇总额定容量 * 100
            result.setSoc(DoubleUtil.getToDouble(batteryChargeQt / result.getBatteryCap() * 100));
        }
        //获取储能系统今日收益
        if (CollectionUtils.isNotEmpty(meterIds)) {
            DeviceHistoryQueryVo deviceQueryVo = new DeviceHistoryQueryVo();
            deviceQueryVo.setDeviceIds(meterIds);
            deviceQueryVo.setFunctionLogos(new HashSet<>(Arrays.asList(FunctionLogoParamVo.TOP_SUP_KWH, FunctionLogoParamVo.PEAK_SUP_KWH,
                    FunctionLogoParamVo.PLAIN_SUP_KWH, FunctionLogoParamVo.VALLEY_SUP_KWH, FunctionLogoParamVo.DEEP_SUP_KWH,
                    FunctionLogoParamVo.TOP_REV_KWH, FunctionLogoParamVo.PEAK_REV_KWH, FunctionLogoParamVo.PLAIN_REV_KWH,
                    FunctionLogoParamVo.VALLEY_REV_KWH, FunctionLogoParamVo.DEEP_REV_KWH)));
            deviceQueryVo.setStartTime(startTime);
            deviceQueryVo.setEndTime(endTime);
            deviceQueryVo.setTimeInterval("1d");
            //获取每天的电量,日期(年月日)->(功能点标识->电量)
            Map<String, Map<String, Double>> dayQtMap = dataService.findNodeDifHistoryListFeign(deviceQueryVo).getData().values()
                    .stream().flatMap(s -> s.values().stream().flatMap(Collection::stream))
                    .filter(c -> StringUtil.isNotEmpty(c.getFirstDateTime()) && StringUtil.isNotEmpty(c.getFirstDataValue())
                            && StringUtil.isNotEmpty(c.getLastDataValue()))
                    .collect(Collectors.groupingBy(c -> c.getFirstDateTime().substring(0, 10),
                            Collectors.groupingBy(NodeDifHistoryDto::getFunctionLogo, Collectors.summingDouble(d ->
                                    DoubleUtil.getObjSub(d.getFirstDataValue(), d.getLastDataValue()))
                            )));
            //获取储能系统今日收益
            result.setDaySeIncome(DoubleUtil.getToBigDecimal(getStorageIncome(Collections.singletonList(nowDate), dayQtMap, sePurchaseElectMap, seSaleElectMap)));
        }

        //获取充电桩今日充电量,今日放电量,充电订单金额,放电订单金额
        if (CollectionUtils.isNotEmpty(pileCodes)) {
            Map<String, OrderQtDto> orderQtMap = togetherService.findOrderQtListByPileCodes(pileCodes, startTime, endTime, 1).getData();
            if (MapUtils.isNotEmpty(orderQtMap)) {
                for (OrderQtDto orderQt : orderQtMap.values()) {
                    result.setDayPileChargeQt(result.getDayPileChargeQt() + orderQt.getChargeQt());
                    result.setDayPileDischargeQt(result.getDayPileDischargeQt() + orderQt.getDischargeQt());
                    result.setDayPileChargeMoney(result.getDayPileChargeMoney().add(orderQt.getChargeMoney()));
                    result.setDayPileDischargeMoney(result.getDayPileDischargeMoney().add(orderQt.getDischargeMoney()));
                }
            }
            result.setDayPileChargeQt(DoubleUtil.getAbsDouble(result.getDayPileChargeQt()));
            result.setDayPileDischargeQt(DoubleUtil.getAbsDouble(result.getDayPileDischargeQt()));
            result.setDayPileChargeMoney(DoubleUtil.getAbsBigDecimal(result.getDayPileChargeMoney()));
            result.setDayPileDischargeMoney(DoubleUtil.getAbsBigDecimal(result.getDayPileDischargeMoney()));
        }
        return ResponseResult.ok(result);
    }

    @Override
    public ResponseResult<List<SiteGwDeviceDto>> findSiteGwDeviceList(String siteId) {
        //返回的集合
        List<SiteGwDeviceDto> resultList = Lists.newArrayList();
        //根据站点id查询关口表设备
        List<DeviceBasicInfoDto> deviceInfoList = deviceService.findDeviceBasicInfoBySiteIds(Collections.singletonList(siteId), null)
                .getData().get(siteId);
        if (CollectionUtils.isNotEmpty(deviceInfoList)) {
            resultList = deviceInfoList.stream().filter(d -> isNotEmpty(d.getTypeId()) && Objects.equals(d.getTypeId(), "39"))
                    .map(device -> {
                        SiteGwDeviceDto result = new SiteGwDeviceDto();
                        BeanUtils.copyProperties(device, result);
                        return result;
                    }).collect(Collectors.toList());
        }
        return ResponseResult.ok(resultList);
    }

    @Override
    public ResponseResult<SiteGwStaticDataDto> findSiteGwStaticData(String deviceId) {
        SiteGwStaticDataDto result = new SiteGwStaticDataDto();
        //根据设备id查询总有功功率和功率因数
        String functionLogos = String.join(FileUtil.COMMA, FunctionLogoParamVo.TOTAL_ACTIVE_POWER, FunctionLogoParamVo.POWER_FACTOR);
        Map<String, RealDataModel> realMap = deviceService.getDeviceFunctionsRealDataByIds(Collections.singleton(deviceId),
                functionLogos).getData().get(deviceId);
        if (MapUtils.isNotEmpty(realMap)) {
            if (realMap.containsKey(FunctionLogoParamVo.TOTAL_ACTIVE_POWER)) {
                RealDataModel realData = realMap.get(FunctionLogoParamVo.TOTAL_ACTIVE_POWER);
                if (realData != null && StringUtil.isNotEmpty(realData.getDataValue())) {
                    result.setPower(DoubleUtil.getToDouble(DoubleUtil.objToDouble(realData.getDataValue())));
                }
            }
            if (realMap.containsKey(FunctionLogoParamVo.POWER_FACTOR)) {
                RealDataModel realData = realMap.get(FunctionLogoParamVo.POWER_FACTOR);
                if (realData != null && StringUtil.isNotEmpty(realData.getDataValue())) {
                    result.setPowerFactor(DoubleUtil.getToDouble(DoubleUtil.objToDouble(realData.getDataValue())));
                }
            }
        }
        return ResponseResult.ok(result);
    }

    @Override
    public ResponseResult<SiteGwCurveDataDto> findSiteGwCurveData(OverviewQueryVo overviewQueryVo) {
        //返回的对象
        SiteGwCurveDataDto result = new SiteGwCurveDataDto();

        //日期类型 1-日 2-月 3-年 4-总(可以不传时间)
        String startTime = overviewQueryVo.getStartTime();
        String endTime = overviewQueryVo.getEndTime();
        Integer dateType = null;
        String timeInterval;
        switch (overviewQueryVo.getDateType()) {
            case 1:
                //获取站点关口表功率曲线数据
                DeviceCommonUtil.getDevicePowerCurve(Collections.singleton(overviewQueryVo.getDeviceId()), startTime, endTime, FunctionLogoParamVo.TOTAL_ACTIVE_POWER, dataService, result);
                timeInterval = "1d";
                break;
            case 2:
                dateType = 1;
                timeInterval = "1d";
                break;
            case 3:
                dateType = 2;
                timeInterval = "1n";
                break;
            case 4:
                dateType = 3;
                timeInterval = "1y";
                break;
            default:
                return ResponseResult.paramError(ResponseResult.PARAM_ERROR);
        }
        DeviceHistoryQueryVo deviceQueryVo = new DeviceHistoryQueryVo();
        deviceQueryVo.setDeviceIds(Collections.singleton(overviewQueryVo.getDeviceId()));
        deviceQueryVo.setFunctionLogos(new HashSet<>(Arrays.asList(FunctionLogoParamVo.TOP_SUP_KWH, FunctionLogoParamVo.PEAK_SUP_KWH,
                FunctionLogoParamVo.PLAIN_SUP_KWH, FunctionLogoParamVo.VALLEY_SUP_KWH, FunctionLogoParamVo.DEEP_SUP_KWH,
                FunctionLogoParamVo.TOP_REV_KWH, FunctionLogoParamVo.PEAK_REV_KWH, FunctionLogoParamVo.PLAIN_REV_KWH,
                FunctionLogoParamVo.VALLEY_REV_KWH, FunctionLogoParamVo.DEEP_REV_KWH)));
        deviceQueryVo.setStartTime(startTime);
        deviceQueryVo.setEndTime(endTime);
        deviceQueryVo.setTimeInterval(timeInterval);
        Map<String, Map<String, List<NodeDifHistoryDto>>> deviceDataMap = dataService.findNodeDifHistoryListFeign(deviceQueryVo).getData();
        if (MapUtils.isNotEmpty(deviceDataMap)) {
            if (dateType != null && StringUtil.isNotEmpty(dateType)) {
                String startDate = overviewQueryVo.getStartTime().substring(0, 10);
                String endDate = overviewQueryVo.getEndTime().substring(0, 10);
                List<String> timeList = DateUtil.getDateBetween(dateType, startDate, endDate);
                int finalDateType = dateType;
                Map<String, Map<String, Double>> dateKwhMap = deviceDataMap.values().stream().flatMap(s -> s.values().stream().flatMap(Collection::stream))
                        .filter(d -> isNotEmpty(d.getFunctionLogo()) && isNotEmpty(d.getFirstDateTime()))
                        .collect(Collectors.groupingBy(c -> {
                            String dateFormat = format(strToDate(c.getFirstDateTime()), getFormatIntervalPattern(finalDateType + 1));
                            if (isNotEmpty(dateFormat)) {
                                return dateFormat;
                            }
                            return c.getFirstDateTime();
                        }, Collectors.groupingBy(NodeDifHistoryDto::getFunctionLogo, Collectors.summingDouble(c -> DoubleUtil.getObjSub(c.getFirstDataValue(), c.getLastDataValue())))));

                for (String dateTime : timeList) {
                    if (dateType == 1) {
                        result.getTimeList().add(dateTime.substring(8, 10));
                    } else if (dateType == 2) {
                        result.getTimeList().add(dateTime.substring(5, 7));
                    } else {
                        result.getTimeList().add(dateTime);
                    }
                    //获取上网电量(总尖峰平谷深)和下网电量(总尖峰平谷深)
                    if (dateKwhMap.containsKey(dateTime)) {
                        Map<String, Double> kwhMap = dateKwhMap.get(dateTime);
                        DeviceCommonUtil.handleMeterQt(kwhMap, result, true);
                    } else {
                        result.getCurve1List().add(0.0);
                        result.getCurve2List().add(0.0);
                    }
                }
            } else {
                Map<String, Double> kwhMap = deviceDataMap.values().stream().flatMap(s -> s.values().stream().flatMap(Collection::stream))
                        .filter(d -> isNotEmpty(d.getFunctionLogo())).collect(Collectors.groupingBy(NodeDifHistoryDto::getFunctionLogo,
                                Collectors.summingDouble(c -> DoubleUtil.getObjSub(c.getFirstDataValue(), c.getLastDataValue()))));
                DeviceCommonUtil.handleMeterQt(kwhMap, result, false);
            }
        }
        return ResponseResult.ok(result);
    }

    @Override
    public ResponseResult<SitePvStaticDataDto> findSitePvStaticData(String siteId) {
        //返回的对象
        SitePvStaticDataDto result = new SitePvStaticDataDto();
        if (StringUtil.isEmpty(siteId)) {
            return ResponseResult.paramError(ResponseResult.PARAM_ISNULL);
        }
        //根据站点id查询站点信息
        SiteInfoDto siteInfo = deviceService.findSiteBasicInfoByIds(Collections.singletonList(siteId)).getData().get(siteId);
        if (siteInfo == null) {
            return ResponseResult.paramError(ResponseResult.PARAM_ERROR);
        }

        //获取光伏系统容量
        if (CollectionUtils.isNotEmpty(siteInfo.getSiteScenarioTypeDtos())) {
            siteInfo.getSiteScenarioTypeDtos().stream().filter(d -> StringUtil.isNotEmpty(d.getScenarioType())
                    && Objects.equals(d.getScenarioType(), 1)).forEach(scenarioType -> {
                if (StringUtil.isNotEmpty(scenarioType.getReadwriteObject())) {
                    //光伏系统装机容量
                    JSONObject reaMap = JSONObject.parseObject(scenarioType.getReadwriteObject());
                    if (reaMap != null && reaMap.containsKey(SiteFieldParamVo.PV_CAPACITY) && StringUtil.isNotEmpty(reaMap.getString(SiteFieldParamVo.PV_CAPACITY))) {
                        result.setPvCap(result.getPvCap() + reaMap.getDouble(SiteFieldParamVo.PV_CAPACITY));
                    }
                }
            });
            result.setPvCap(DoubleUtil.getToDouble(result.getPvCap()));
        }
        //根据站点id查询光伏逆变器设备
        List<DeviceBasicInfoDto> deviceInfoList = deviceService.findDeviceBasicInfoBySiteIds(Collections.singletonList(siteId), 3).getData().get(siteId);
        if (CollectionUtils.isEmpty(deviceInfoList)) {
            return ResponseResult.ok(result);
        }
        Set<String> inverterIds = deviceInfoList.stream().filter(d -> StringUtil.isNotEmpty(d.getTypeId())
                        && (Objects.equals(d.getTypeId(), "20") || Objects.equals(d.getTypeId(), "77")))
                .map(DeviceBasicInfoDto::getId).collect(Collectors.toSet());
        if (CollectionUtils.isNotEmpty(inverterIds)) {
            double power = deviceService.getDeviceFunctionsRealDataByIds(inverterIds, FunctionLogoParamVo.ACTIVE_POWER).getData().values().stream()
                    .flatMap(d -> d.values().stream()).mapToDouble(realModel -> {
                        if (realModel != null) {
                            return DoubleUtil.objToDouble(realModel.getDataValue());
                        }
                        return 0.0;
                    }).sum();
            result.setPower(DoubleUtil.getToDouble(power));
        }
        return ResponseResult.ok(result);
    }

    @Override
    public ResponseResult<SitePvCurveDataDto> findSitePvCurveData(OverviewQueryVo overviewQueryVo) {
        //返回的对象
        SitePvCurveDataDto result = new SitePvCurveDataDto();

        //校验请求参数
        if (validateQueryParams(overviewQueryVo)) {
            return ResponseResult.error(ResponseResult.PARAM_ERROR);
        }
        //根据多个站点id查询光伏系统id,光伏系统额定容量
        String siteId = overviewQueryVo.getSiteId();
        SiteInfoDto siteInfo = deviceService.findSiteBasicInfoByIds(Collections.singletonList(siteId)).getData().get(siteId);
        if (siteInfo == null) {
            return ResponseResult.ok(result);
        }

        //获取站点光伏系统下面的逆变器设备id，关口表设备id
        //根据多个站点id查询关口表id,逆变器id
        List<DeviceBasicInfoDto> deviceInfoList = deviceService.findDeviceBasicInfoBySiteIds(Collections.singletonList(siteId), null)
                .getData().get(siteId);
        if (CollectionUtils.isEmpty(deviceInfoList)) {
            return ResponseResult.ok(result);
        }
        Set<String> inverterIds = deviceInfoList.stream().filter(d -> StringUtil.isNotEmpty(d.getTypeId())
                        && (Objects.equals(d.getTypeId(), "20") || Objects.equals(d.getTypeId(), "77")))
                .map(DeviceBasicInfoDto::getId).collect(Collectors.toSet());
        Set<String> gatewayIds = deviceInfoList.stream().filter(d -> StringUtil.isNotEmpty(d.getTypeId()) && Objects.equals(d.getTypeId(), "39"))
                .map(DeviceBasicInfoDto::getId).collect(Collectors.toSet());

        //日期类型 1-日 2-月 3-年 4-总(可以不传时间)
        String startTime = overviewQueryVo.getStartTime();
        String endTime = overviewQueryVo.getEndTime();
        String startDate = overviewQueryVo.getStartTime().substring(0, 10);
        String endDate = overviewQueryVo.getEndTime().substring(0, 10);
        Integer dateType = null;
        switch (overviewQueryVo.getDateType()) {
            case 1:
                //获取光伏功率曲线数据
                DeviceCommonUtil.getDevicePowerCurve(inverterIds, startTime, endTime, FunctionLogoParamVo.ACTIVE_POWER, dataService, result);
                break;
            case 2:
                dateType = 1;
                break;
            case 3:
                dateType = 2;
                break;
            case 4:
                dateType = 3;
                break;
            default:
                return ResponseResult.paramError(ResponseResult.PARAM_ERROR);
        }
        List<NodeDifHistoryDto> inverterDataList = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(inverterIds)) {
            //查询光伏逆变器发电量
            DeviceHistoryQueryVo inverterQuery = new DeviceHistoryQueryVo();
            inverterQuery.setDeviceIds(inverterIds);
            inverterQuery.setFunctionLogos(Collections.singleton(FunctionLogoParamVo.TOTAL_POWER_GENERATION));
            inverterQuery.setStartTime(startTime);
            inverterQuery.setEndTime(endTime);
            inverterQuery.setTimeInterval("1d");
            Map<String, Map<String, List<NodeDifHistoryDto>>> inverterDataMap = dataService.findNodeDifHistoryListFeign(inverterQuery).getData();
            if (MapUtils.isNotEmpty(inverterDataMap)) {
                inverterDataList = inverterDataMap.values().stream().flatMap(s -> s.values().stream().flatMap(Collection::stream))
                        .collect(Collectors.toList());
                if (dateType != null && StringUtil.isNotEmpty(dateType)) {
                    List<String> timeList = DateUtil.getDateBetween(dateType, startDate, endDate);
                    int finalDateType = dateType;
                    //光伏发电量
                    Map<String, List<NodeDifHistoryDto>> generateQtMap = inverterDataList.stream().filter(c -> StringUtil.isNotEmpty(c.getFirstDateTime())).collect(Collectors.groupingBy(c -> {
                        String dateFormat = format(strToDate(c.getFirstDateTime()), getFormatIntervalPattern(finalDateType + 1));
                        if (StringUtil.isNotEmpty(dateFormat)) {
                            return dateFormat;
                        }
                        return c.getFirstDateTime();
                    }));
                    for (String dateTime : timeList) {
                        //光伏实际发电量
                        if (generateQtMap.containsKey(dateTime)) {
                            double generateQt = generateQtMap.get(dateTime).stream().mapToDouble(d -> DoubleUtil.getObjSub(d.getFirstDataValue(), d.getLastDataValue())).sum();
                            result.getCurve1List().add(DoubleUtil.getAbsDouble(generateQt));
                            result.setGenerateQt(result.getGenerateQt() + generateQt);
                        } else {
                            result.getCurve1List().add(0.0);
                        }
                        //光伏理论发电量
                        result.getCurve2List().add(0.0);
                        if (dateType == 1) {
                            result.getTimeList().add(dateTime.substring(8, 10));
                        } else if (dateType == 2) {
                            result.getTimeList().add(dateTime.substring(5, 7));
                        } else {
                            result.getTimeList().add(dateTime);
                        }
                    }
                } else {
                    result.setGenerateQt(inverterDataList.stream().mapToDouble(d -> DoubleUtil.getObjSub(d.getFirstDataValue(), d.getLastDataValue())).sum());
                }
                //光伏实际发电量
                result.setGenerateQt(DoubleUtil.getAbsDouble(result.getGenerateQt()));
            }
        }

        //获取关口表今日上网电量和今日下网电量
        //查询光伏关口表每天的正向有功电量和反向有功电量数据和(尖峰平谷)反向有功电量数据
        Map<String, List<NodeDifHistoryDto>> gatewayDataMap = Maps.newHashMap();
        //查询关口表每半个小时的上网电量
        Map<String, Double> pvInternetQtMap = Maps.newHashMap();
        if (CollectionUtils.isNotEmpty(gatewayIds)) {
            DeviceHistoryQueryVo gatewayQueryVo = new DeviceHistoryQueryVo();
            gatewayQueryVo.setDeviceIds(gatewayIds);
            gatewayQueryVo.setFunctionLogos(new HashSet<>(Arrays.asList(FunctionLogoParamVo.TOP_REV_KWH, FunctionLogoParamVo.PEAK_REV_KWH,
                    FunctionLogoParamVo.PLAIN_REV_KWH, FunctionLogoParamVo.VALLEY_REV_KWH, FunctionLogoParamVo.DEEP_REV_KWH)));
            gatewayQueryVo.setStartTime(startTime);
            gatewayQueryVo.setEndTime(endTime);
            gatewayQueryVo.setTimeInterval("1d");
            gatewayDataMap = dataService.findNodeDifHistoryListFeign(gatewayQueryVo).getData().values().stream()
                    .flatMap(map -> map.values().stream().flatMap(Collection::stream))
                    .collect(Collectors.groupingBy(NodeDifHistoryDto::getFunctionLogo));

            DeviceHistoryQueryVo gatewayInternetQueryVo = new DeviceHistoryQueryVo();
            gatewayInternetQueryVo.setDeviceIds(gatewayIds);
            gatewayInternetQueryVo.setFunctionLogos(Collections.singleton(FunctionLogoParamVo.TOTAL_INVERSE_ACTIVE_ENERGY));
            gatewayInternetQueryVo.setStartTime(startTime);
            gatewayInternetQueryVo.setEndTime(endTime);
            gatewayInternetQueryVo.setTimeInterval("30m");
            pvInternetQtMap = dataService.findNodeDifHistoryListFeign(gatewayInternetQueryVo).getData().values().stream()
                    .flatMap(map -> map.values().stream().flatMap(Collection::stream))
                    .filter(n -> isNotEmpty(n.getFirstDateTime()))
                    .collect(Collectors.groupingBy(NodeDifHistoryDto::getFirstDateTime, Collectors.summingDouble(s ->
                            DoubleUtil.getObjSub(s.getFirstDataValue(), s.getLastDataValue()))));
            //获取光伏上网电量
            double netQt = pvInternetQtMap.values().stream().mapToDouble(d -> d).sum();
            result.setNetQt(DoubleUtil.getAbsDouble(netQt));
        }

        //获取光伏消纳电量(光伏发电量 - 上网电量)
        result.setConsumeQt(DoubleUtil.getToDouble(result.getGenerateQt() - result.getNetQt()));

        //获取光伏收益
        //获取日期列表
        List<String> dateList = getDateBetween(1, startDate, endDate);
        //获取站点光伏上网电价和消纳电价设置信息
        //光伏上网电价 日期 -> (时段类型 -> 电费)
        Map<String, Map<Integer, BigDecimal>> pvInternetElectMap = Maps.newHashMap();
        //光伏消纳电价 日期 -> (每半个小时时段(00:00) -> 电费)
        Map<String, Map<String, BigDecimal>> pvConsumeElectMap = Maps.newHashMap();
        //根据站点id查询站点电价配置(统计光伏收益)
        List<ElectConfigDto> electConfigList = togetherService.findElectConfigListBySiteIds(Collections.singletonList(siteId),
                String.join(FileUtil.COMMA, "2", "3"), startDate, endDate).getData().get(siteId);
        if (CollectionUtils.isNotEmpty(electConfigList)) {
            electConfigList.stream().collect(Collectors.toMap(ElectConfigDto::getModuleType, a -> a, (k1, k2) -> k1))
                    .forEach((moduleType, electConfig) -> {
                        List<ElectConfigDto.ElectTimeFrame> electTimeFrameList = electConfig.getElectTimeFrameList();
                        //电价配置 (时段类型(尖峰平谷深) -> 电费)
                        Map<Integer, BigDecimal> periodTypeMap = electTimeFrameList.stream().collect(Collectors.toMap(ElectConfigDto.ElectTimeFrame::getPeriodType,
                                ElectConfigDto.ElectTimeFrame::getElectMoney, (k1, k2) -> k2));
                        //电价配置 (半小时时段 -> 电费)
                        Map<String, BigDecimal> timeFrameMap = buildHalfHourMapForOneConfig(electTimeFrameList);
                        //过滤日期范围内的日期
                        List<String> electDateList = DateUtil.getDateBetween(1, electConfig.getStartDate(), electConfig.getEndDate());
                        List<String> electFiletrDateList = dateList.stream().filter(electDateList::contains).collect(Collectors.toList());
                        for (String date : electFiletrDateList) {
                            if (moduleType == 2) { //光伏上网电价
                                pvInternetElectMap.put(date, periodTypeMap);
                            }
                            if (moduleType == 3) { //光伏消纳电价
                                pvConsumeElectMap.put(date, timeFrameMap);
                            }
                        }
                    });
        }


        //计算光伏上网收益
        //获取光伏上网电价和消费电价
        BigDecimal pvInternetMoney = this.processPvInternetIncome(dateList, pvInternetElectMap, gatewayDataMap);
        //获取光伏消纳收益
        //逆变器发电量(年月日时分秒) -> 发电量
        Map<String, Double> pvInverterQtMap = inverterDataList.stream().filter(d -> StringUtil.isNotEmpty(d.getFirstDateTime()))
                .collect(Collectors.groupingBy(NodeDifHistoryDto::getFirstDateTime, Collectors.summingDouble(d ->
                        DoubleUtil.getObjSub(d.getFirstDataValue(), d.getLastDataValue()))));
        BigDecimal pvConsumeMoney = this.processPvConsumeIncome(pvConsumeElectMap, pvInternetQtMap, pvInverterQtMap);
        //光伏今日收益(光伏今日上网收益 + 光伏今日消纳收益)
        if (!Objects.equals(pvInternetMoney, BigDecimal.ZERO) || !Objects.equals(pvConsumeMoney, BigDecimal.ZERO)) {
            result.setIncome(DoubleUtil.getToBigDecimal(pvInternetMoney.add(pvConsumeMoney)));
        }

        return ResponseResult.ok(result);
    }

    @Override
    public ResponseResult<SiteSeStaticDataDto> findSiteSeStaticData(String siteId) {
        //返回的对象
        SiteSeStaticDataDto result = new SiteSeStaticDataDto();
        //根据站点id查询设备列表
        //定义储能pcs列表
        Set<String> pcsIds = Sets.newHashSet();
        //定义储能电池蔟列表 设备id -> 电池簇额定容量
        Map<String, Double> batteryCapMap = Maps.newHashMap();
        deviceService.findDeviceBasicInfoBySiteIds(Collections.singletonList(siteId), 4).getData().get(siteId).stream()
                .filter(d -> StringUtil.isNotEmpty(d.getTypeId())).forEach(device -> {
                    Map<String, Object> reaMap = device.getReaMap();
                    if (Objects.equals(device.getTypeId(), "23") || Objects.equals(device.getTypeId(), "78")) {
                        if (device.getReaMap().containsKey(ReaFieldParamVo.RATED_POWER) && StringUtil.isNotEmpty(device.getReaMap().get(ReaFieldParamVo.RATED_POWER))) {
                            pcsIds.add(device.getId());
                            result.setPcsPower(result.getPcsPower() + DoubleUtil.objToDouble(reaMap.get(ReaFieldParamVo.RATED_POWER)));
                        }
                    }
                    if (Objects.equals(device.getTypeId(), "25")) {
                        if (MapUtils.isNotEmpty(reaMap) && reaMap.containsKey(ReaFieldParamVo.RATED_CAP) && StringUtil.isNotEmpty(reaMap.get(ReaFieldParamVo.RATED_CAP))) {
                            result.setBatteryCap(result.getBatteryCap() + DoubleUtil.objToDouble(reaMap.get(ReaFieldParamVo.RATED_CAP)));
                            batteryCapMap.put(device.getId(), DoubleUtil.objToDouble(reaMap.get(ReaFieldParamVo.RATED_CAP)));
                        }
                    }
                });
        result.setPcsPower(DoubleUtil.getToDouble(result.getPcsPower()));
        result.setBatteryCap(DoubleUtil.getToDouble(result.getBatteryCap()));

        //获取储能系统有功功率
        if (CollectionUtils.isNotEmpty(pcsIds)) {
            result.setPower(DoubleUtil.getToDouble(deviceService.getDeviceFunctionsRealDataByIds(pcsIds, FunctionLogoParamVo.PCS_ACTIVE_POWER).getData()
                    .values().stream().flatMap(list -> list.values().stream().filter(d -> d != null
                            && StringUtil.isNotEmpty(d.getDataValue())).map(d -> DoubleUtil.objToDouble(d.getDataValue())))
                    .mapToDouble(d -> d).sum()));
        }
        //获取储能系统当前SOC
        if (MapUtils.isNotEmpty(batteryCapMap)) {
            //获取储能电池蔟可放电量
            AtomicReference<Double> batteryChargeQt = new AtomicReference<>(0.0);
            deviceService.getDeviceFunctionsRealDataByIds(batteryCapMap.keySet(), FunctionLogoParamVo.BATTERY_TOTAL_SOC).getData()
                    .forEach((batteryId, realDataMap) -> {
                        if (batteryCapMap.containsKey(batteryId) && realDataMap.containsKey(FunctionLogoParamVo.BATTERY_TOTAL_SOC)) {
                            Double ratedCap = batteryCapMap.get(batteryId);
                            RealDataModel realDataModel = realDataMap.get(FunctionLogoParamVo.BATTERY_TOTAL_SOC);
                            if (realDataModel != null && isNotEmpty(realDataModel.getDataValue())) {
                                batteryChargeQt.updateAndGet(v -> v + ratedCap * (DoubleUtil.objToDouble(realDataModel.getDataValue()) / 100));
                            }
                        }
                    });
            //储能系统SOC 储能电池簇可放电量/储能电池簇总额定容量 * 100
            result.setSoc(DoubleUtil.getToDouble(batteryChargeQt.get() / result.getBatteryCap() * 100));
        }
        return ResponseResult.ok(result);
    }

    @Override
    public ResponseResult<SiteSeCurveDataDto> findSiteSeCurveData(OverviewQueryVo overviewQueryVo) {
        //返回的对象
        SiteSeCurveDataDto result = new SiteSeCurveDataDto();
        //根据站点id查询设备列表
        String siteId = overviewQueryVo.getSiteId();
        //定义储能PCS列表
        Set<String> pcsIds = Sets.newHashSet();
        //定义电能表列表
        Set<String> meterIds = Sets.newHashSet();
        //定义储能电池簇额定容量
        Double batteryCap = 0.0;
        for (DeviceBasicInfoDto device : deviceService.findDeviceBasicInfoBySiteIds(Collections.singletonList(siteId), null).getData().get(siteId)) {
            String typeId = device.getTypeId();
            Map<String, Object> reaMap = device.getReaMap();
            if (StringUtil.isNotEmpty(typeId)) {
                switch (typeId) {
                    case "23":
                    case "78":
                        pcsIds.add(device.getId());
                        break;
                    case "38":
                        meterIds.add(device.getId());
                        break;
                    case "25":
                        if (MapUtils.isNotEmpty(reaMap) && reaMap.containsKey(ReaFieldParamVo.RATED_CAP) && StringUtil.isNotEmpty(reaMap.get(ReaFieldParamVo.RATED_CAP))) {
                            batteryCap += DoubleUtil.objToDouble(reaMap.get(ReaFieldParamVo.RATED_CAP));
                        }
                        break;
                }
            }
        }
        //日期类型 1-日 2-月 3-年 4-总(可以不传时间)
        String startTime = overviewQueryVo.getStartTime();
        String endTime = overviewQueryVo.getEndTime();
        String startDate = overviewQueryVo.getStartTime().substring(0, 10);
        String endDate = overviewQueryVo.getEndTime().substring(0, 10);
        Integer dateType = null;
        String timeInterval;
        switch (overviewQueryVo.getDateType()) {
            case 1:
                //获取储能功率曲线数据
                DeviceCommonUtil.getDevicePowerCurve(pcsIds, startTime, endTime, FunctionLogoParamVo.PCS_ACTIVE_POWER, dataService, result);
                timeInterval = "1d";
                break;
            case 2:
                dateType = 1;
                timeInterval = "1d";
                break;
            case 3:
                dateType = 2;
                timeInterval = "1n";
                break;
            case 4:
                dateType = 3;
                timeInterval = "1y";
                break;
            default:
                return ResponseResult.paramError(ResponseResult.PARAM_ERROR);
        }
        if (CollectionUtils.isNotEmpty(pcsIds)) {
            DeviceHistoryQueryVo deviceQuery = new DeviceHistoryQueryVo();
            deviceQuery.setDeviceIds(pcsIds);
            deviceQuery.setFunctionLogos(new HashSet<>(Arrays.asList(FunctionLogoParamVo.PCS_BATTERY_CHARGE, FunctionLogoParamVo.PCS_BATTERY_DISCHARGE)));
            deviceQuery.setStartTime(startTime);
            deviceQuery.setEndTime(endTime);
            deviceQuery.setTimeInterval(timeInterval);
            Map<String, Map<String, List<NodeDifHistoryDto>>> historyDataMap = dataService.findNodeDifHistoryListFeign(deviceQuery).getData();
            if (MapUtils.isNotEmpty(historyDataMap)) {
                if (dateType != null && StringUtil.isNotEmpty(dateType)) {
                    List<String> timeList = DateUtil.getDateBetween(dateType, startDate, endDate);
                    // 存储所有充电量历史数据
                    List<NodeDifHistoryDto> sumChargeList = Lists.newArrayList();
                    // 存储所有放电量历史数据
                    List<NodeDifHistoryDto> sumDischargeList = Lists.newArrayList();
                    historyDataMap.forEach((deviceId, dataMap) -> {
                        if (!dataMap.isEmpty() && CollectionUtils.isNotEmpty(dataMap.get(FunctionLogoParamVo.PCS_BATTERY_CHARGE))) {
                            sumChargeList.addAll(dataMap.get(FunctionLogoParamVo.PCS_BATTERY_CHARGE));
                        }
                        if (!dataMap.isEmpty() && CollectionUtils.isNotEmpty(dataMap.get(FunctionLogoParamVo.PCS_BATTERY_DISCHARGE))) {
                            sumDischargeList.addAll(dataMap.get(FunctionLogoParamVo.PCS_BATTERY_DISCHARGE));
                        }
                    });

                    int finalDateType = dateType;
                    //储能系统-充电量
                    Map<String, List<NodeDifHistoryDto>> chargeQtMap = sumChargeList.stream().filter(c -> StringUtil.isNotEmpty(c.getFirstDateTime())).collect(Collectors.groupingBy(c -> {
                        String dateFormat = format(strToDate(c.getFirstDateTime()), getFormatIntervalPattern(finalDateType + 1));
                        if (StringUtil.isNotEmpty(dateFormat)) {
                            return dateFormat;
                        }
                        return c.getFirstDateTime();
                    }));
                    //储能系统-放电量
                    Map<String, List<NodeDifHistoryDto>> dischargeQtMap = sumDischargeList.stream().filter(c -> StringUtil.isNotEmpty(c.getFirstDateTime())).collect(Collectors.groupingBy(c -> {
                        String dateFormat = format(strToDate(c.getFirstDateTime()), getFormatIntervalPattern(finalDateType + 1));
                        if (StringUtil.isNotEmpty(dateFormat)) {
                            return dateFormat;
                        }
                        return c.getFirstDateTime();
                    }));
                    for (String dateTime : timeList) {
                        //储能系统-充电量
                        if (chargeQtMap.containsKey(dateTime)) {
                            double chargeQt = chargeQtMap.get(dateTime).stream().mapToDouble(d -> DoubleUtil.getObjSub(d.getFirstDataValue(), d.getLastDataValue())).sum();
                            result.getCurve1List().add(DoubleUtil.getAbsDouble(chargeQt));
                            result.setChargeQt(result.getChargeQt() + chargeQt);
                        } else {
                            result.getCurve1List().add(0.0);
                        }
                        //储能系统-放电量
                        if (dischargeQtMap.containsKey(dateTime)) {
                            double dischargeQt = dischargeQtMap.get(dateTime).stream().mapToDouble(d -> DoubleUtil.getObjSub(d.getFirstDataValue(), d.getLastDataValue())).sum();
                            result.getCurve2List().add(DoubleUtil.getAbsDouble(dischargeQt));
                            result.setDischargeQt(result.getDischargeQt() + dischargeQt);
                        } else {
                            result.getCurve2List().add(0.0);
                        }
                        if (dateType == 1) {
                            result.getTimeList().add(dateTime.substring(8, 10));
                        } else if (dateType == 2) {
                            result.getTimeList().add(dateTime.substring(5, 7));
                        } else {
                            result.getTimeList().add(dateTime);
                        }
                    }
                } else {
                    for (Map<String, List<NodeDifHistoryDto>> listMap : historyDataMap.values()) {
                        listMap.forEach((functionLogo, list) -> {
                            if (functionLogo.equals(FunctionLogoParamVo.PCS_BATTERY_CHARGE)) {
                                result.setChargeQt(result.getChargeQt() + list.stream().mapToDouble(d -> DoubleUtil.getObjSub(d.getFirstDataValue(), d.getLastDataValue())).sum());
                            }
                            if (functionLogo.equals(FunctionLogoParamVo.PCS_BATTERY_DISCHARGE)) {
                                result.setDischargeQt(result.getDischargeQt() + list.stream().mapToDouble(d -> DoubleUtil.getObjSub(d.getFirstDataValue(), d.getLastDataValue())).sum());
                            }
                        });
                    }
                }
                //获取累计充放电量和循环次数
                result.setChargeQt(DoubleUtil.getAbsDouble(result.getChargeQt()));
                result.setDischargeQt(DoubleUtil.getAbsDouble(result.getDischargeQt()));

                //获取累计循环次数
                result.setCycleNum(DoubleUtil.getToDouble(result.getDischargeQt() / batteryCap));
            }
        }

        //获取储能分时电量(并网点电能表)和储能收益
        if (CollectionUtils.isNotEmpty(meterIds)) {
            //根据多个电能表id和日期查询并网点电能表(尖峰平谷深)的数据
            DeviceHistoryQueryVo deviceQueryVo = new DeviceHistoryQueryVo();
            deviceQueryVo.setDeviceIds(meterIds);
            deviceQueryVo.setFunctionLogos(new HashSet<>(Arrays.asList(FunctionLogoParamVo.TOP_SUP_KWH, FunctionLogoParamVo.PEAK_SUP_KWH,
                    FunctionLogoParamVo.PLAIN_SUP_KWH, FunctionLogoParamVo.VALLEY_SUP_KWH, FunctionLogoParamVo.DEEP_SUP_KWH,
                    FunctionLogoParamVo.TOP_REV_KWH, FunctionLogoParamVo.PEAK_REV_KWH, FunctionLogoParamVo.PLAIN_REV_KWH,
                    FunctionLogoParamVo.VALLEY_REV_KWH, FunctionLogoParamVo.DEEP_REV_KWH)));
            deviceQueryVo.setStartTime(startTime);
            deviceQueryVo.setEndTime(endTime);
            deviceQueryVo.setTimeInterval("1d");
            List<NodeDifHistoryDto> deviceDataList = dataService.findNodeDifHistoryListFeign(deviceQueryVo).getData().values().stream()
                    .flatMap(s -> s.values().stream().flatMap(Collection::stream)).collect(Collectors.toList());

            //获取储能系统今日收益
            //获取日期列表
            List<String> dateList = getDateBetween(1, startDate, endDate);

            //根据站点id查询站点电价配置(统计光伏收益和储能收益使用)
            //储能购电电价 日期 -> (时段类型 -> 电费)
            Map<String, Map<Integer, BigDecimal>> sePurchaseElectMap = Maps.newHashMap();
            //储能售电电价 日期 -> (时段类型 -> 电费)
            Map<String, Map<Integer, BigDecimal>> seSaleElectMap = Maps.newHashMap();

            List<ElectConfigDto> electConfigList = togetherService.findElectConfigListBySiteIds(Collections.singletonList(siteId),
                    String.join(FileUtil.COMMA, "4", "5"), startDate, endDate).getData().get(siteId);
            if (CollectionUtils.isNotEmpty(electConfigList)) {
                electConfigList.stream().collect(Collectors.toMap(ElectConfigDto::getModuleType, a -> a, (k1, k2) -> k1))
                        .forEach((moduleType, electConfig) -> {
                            List<ElectConfigDto.ElectTimeFrame> electTimeFrameList = electConfig.getElectTimeFrameList();
                            //电价配置 (时段类型(尖峰平谷深) -> 电费)
                            Map<Integer, BigDecimal> periodTypeMap = electTimeFrameList.stream().collect(Collectors.toMap(ElectConfigDto.ElectTimeFrame::getPeriodType,
                                    ElectConfigDto.ElectTimeFrame::getElectMoney, (k1, k2) -> k2));
                            //过滤日期范围内的日期
                            List<String> electDateList = DateUtil.getDateBetween(1, electConfig.getStartDate(), electConfig.getEndDate());
                            List<String> electFiletrDateList = dateList.stream().filter(electDateList::contains).collect(Collectors.toList());
                            for (String date : electFiletrDateList) {
                                if (moduleType == 4) { //储能售电电价
                                    seSaleElectMap.put(date, periodTypeMap);
                                }
                                if (moduleType == 5) { //储能购电电价
                                    sePurchaseElectMap.put(date, periodTypeMap);
                                }
                            }
                        });
            }
            //获取每天的电量,日期(年月日)->(功能点标识->电量)
            Map<String, Map<String, Double>> dayQtMap = deviceDataList.stream().filter(c -> StringUtil.isNotEmpty(c.getFirstDateTime())
                            && StringUtil.isNotEmpty(c.getFirstDataValue()) && StringUtil.isNotEmpty(c.getLastDataValue()))
                    .collect(Collectors.groupingBy(c -> c.getFirstDateTime().substring(0, 10),
                            Collectors.groupingBy(NodeDifHistoryDto::getFunctionLogo, Collectors.summingDouble(d ->
                                    DoubleUtil.getObjSub(d.getFirstDataValue(), d.getLastDataValue())))));
            //计算储能系统今日收益
            result.setIncome(DoubleUtil.getToBigDecimal(getStorageIncome(dateList, dayQtMap, sePurchaseElectMap, seSaleElectMap)));

            //获取储能分时电量(并网点)数据
            Map<String, Double> kwhMap = deviceDataList.stream().filter(d -> isNotEmpty(d.getFunctionLogo()))
                    .collect(Collectors.groupingBy(NodeDifHistoryDto::getFunctionLogo, Collectors.summingDouble(c ->
                            DoubleUtil.getObjSub(c.getFirstDataValue(), c.getLastDataValue()))));
            DeviceCommonUtil.handleMeterQt(kwhMap, result, false);
        }
        return ResponseResult.ok(result);
    }

    @Override
    public ResponseResult<SitePileStaticDataDto> findSitePileStaticData(String siteId) {
        //返回的对象
        SitePileStaticDataDto result = new SitePileStaticDataDto();

        //根据站点id查询设备数据
        List<String> pileTypes = Arrays.asList("28", "29", "30", "79");
        List<DeviceBasicInfoDto> deviceInfoList = deviceService.findDeviceBasicInfoBySiteIds(Collections.singletonList(siteId), null).getData().get(siteId)
                .stream().filter(d -> pileTypes.contains(d.getTypeId())).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(deviceInfoList)) {
            return ResponseResult.ok(result);
        }
        //获取普通充电桩电枪数量
        List<String> pileIds = deviceInfoList.stream().filter(d -> !Objects.equals(d.getTypeId(), "79")).map(DeviceBasicInfoDto::getId)
                .distinct().collect(Collectors.toList());
        Map<String, List<DeviceGunInfoDto>> deviceGunMap = deviceService.findDeviceGunInfoByDeviceIds(pileIds).getData();
        //获取交流电桩数量
        List<DeviceBasicInfoDto> acPileDeviceList = deviceInfoList.stream().filter(d -> StringUtil.isNotEmpty(d.getTypeId())
                && "28".equals(d.getTypeId())).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(acPileDeviceList)) {
            //获取交流充电桩数量
            result.setAcPileNum(acPileDeviceList.size());
            //获取交流充电枪数量
            result.setAcGunNum(acPileDeviceList.stream().mapToInt(device -> {
                if (deviceGunMap.containsKey(device.getId())) {
                    return deviceGunMap.get(device.getId()).size();
                }
                return 0;
            }).sum());

        }
        //获取直流充电桩数量
        List<DeviceBasicInfoDto> dcPileDeviceList = deviceInfoList.stream().filter(d -> StringUtil.isNotEmpty(d.getTypeId())
                && ("29".equals(d.getTypeId()) || "30".equals(d.getTypeId()) || "79".equals(d.getTypeId()))).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(dcPileDeviceList)) {
            //获取直流和V2G充电桩数量
            result.setDcPileNum(dcPileDeviceList.size());
            //获取直流和V2G充电枪数量和超充枪数量
            int superPileNum = (int) dcPileDeviceList.stream().filter(d -> Objects.equals(d.getTypeId(), "79")).count();
            result.setDcGunNum(dcPileDeviceList.stream().mapToInt(device -> {
                if (deviceGunMap.containsKey(device.getId())) {
                    return deviceGunMap.get(device.getId()).size();
                }
                return 0;
            }).sum() + superPileNum);
        }

        //统计电枪状态
        Map<String, String> pileIdCodeMap = deviceInfoList.stream().filter(d -> !Objects.equals(d.getTypeId(), "79")
                && StringUtil.isNotEmpty(d.getDeviceNumber())).collect(Collectors.toMap(DeviceBasicInfoDto::getId,
                DeviceBasicInfoDto::getDeviceNumber, (k1, k2) -> k1));
        for (Map.Entry<String, String> entry : pileIdCodeMap.entrySet()) {
            String pileId = entry.getKey();
            String pileCode = entry.getValue();
            List<DeviceGunInfoDto> gunInfoList = deviceGunMap.get(pileId);
            PileRealModel pileRealModel = RedisGeneralUtil.getPileRealModel(pileCode);
            if (CollectionUtils.isNotEmpty(gunInfoList)) {
                if (pileRealModel != null && MapUtils.isNotEmpty(pileRealModel.getGunRealModelMap())) {
                    Map<String, PileRealModel.GunRealModel> gunRealMap = pileRealModel.getGunRealModelMap();
                    for (DeviceGunInfoDto deviceGun : gunInfoList) {
                        if (gunRealMap.containsKey(deviceGun.getGunCode())) {
                            PileRealModel.GunRealModel gunRealModel = gunRealMap.get(deviceGun.getGunCode());
                            if (StringUtil.isNotEmpty(gunRealModel.getGunStatus())) {
                                //枪状态 -1-未知 0-空闲 1-充电准备 2-充电中 3-占用(已连接) 4-放电准备 5-放电中 7-预约 8-暂停 88-离线 255-故障
                                switch (gunRealModel.getGunStatus()) {
                                    case 0:
                                        result.setIdle(result.getIdle() + 1);
                                        break;
                                    case 2:
                                        result.setCharge(result.getCharge() + 1);
                                        break;
                                    case 3:
                                        result.setEmploy(result.getEmploy() + 1);
                                        break;
                                    case 5:
                                        result.setDischarge(result.getDischarge() + 1);
                                        break;
                                    default:
                                        result.setOther(result.getOther() + 1);
                                        break;
                                }
                            }
                        }
                    }
                } else {
                    //默认给其他
                    result.setOther(result.getOther() + gunInfoList.size());
                }
            }
        }
        //获取超充枪状态数量
        List<DeviceBasicInfoDto> superPileList = dcPileDeviceList.stream().filter(d -> Objects.equals(d.getTypeId(), "79")).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(superPileList)) {
            Set<String> superPileIds = superPileList.stream().map(DeviceBasicInfoDto::getId).collect(Collectors.toSet());
            Map<String, Map<String, RealDataModel>> realDataMap = deviceService.getDeviceFunctionsRealDataByIds(superPileIds,
                    String.join(FileUtil.COMMA, FunctionLogoParamVo.GUN_ORIGINAL_STATUS, FunctionLogoParamVo.VEHICLE_CONN_STATE)).getData();
            for (DeviceBasicInfoDto deviceInfo : superPileList) {
                if (realDataMap.containsKey(deviceInfo.getId())) {
                    Map<String, RealDataModel> realDataModelMap = realDataMap.get(deviceInfo.getId());
                    Integer gunState = null; //枪原始状态 0-空闲 1-准备中 2-充电中 3-充电结束 4-启动失败 5-预约中 6-故障
                    Integer vehicleState = null; //车辆连接状态 0-断开 1-半连接 2-已连接
                    if (realDataModelMap.containsKey(FunctionLogoParamVo.GUN_ORIGINAL_STATUS)) { //枪原始状态
                        Object dataValue = realDataModelMap.get(FunctionLogoParamVo.GUN_ORIGINAL_STATUS).getDataValue();
                        if (StringUtil.isNotEmpty(dataValue)) {
                            gunState = Integer.parseInt(String.valueOf(dataValue));
                        }
                    }
                    if (realDataModelMap.containsKey(FunctionLogoParamVo.VEHICLE_CONN_STATE)) { //枪车辆连接状态
                        Object dataValue = realDataModelMap.get(FunctionLogoParamVo.VEHICLE_CONN_STATE).getDataValue();
                        if (StringUtil.isNotEmpty(dataValue)) {
                            vehicleState = Integer.parseInt(String.valueOf(dataValue));
                        }
                    }
                    DeviceCommonUtil.getSuperPileGunStatus(deviceInfo.getTxStatus(), gunState, vehicleState, result);
                }
            }
        }
        //全部充电桩数量
        result.setWhole(result.getIdle() + result.getCharge() + result.getEmploy() + result.getDischarge() + result.getOther());

        return ResponseResult.ok(result);
    }

    @Override
    public ResponseResult<SitePileCurveDataDto> findSitePileCurveData(OverviewQueryVo overviewQueryVo) {
        //返回的对象
        SitePileCurveDataDto result = new SitePileCurveDataDto();
        //校验请求参数
        if (validateQueryParams(overviewQueryVo)) {
            return ResponseResult.error(ResponseResult.PARAM_ERROR);
        }
        //根据站点id查询设备数据
        String siteId = overviewQueryVo.getSiteId();
        List<String> pileTypes = Arrays.asList("28", "29", "30", "79");
        List<DeviceBasicInfoDto> deviceInfoList = deviceService.findDeviceBasicInfoBySiteIds(Collections.singletonList(siteId), null).getData().get(siteId)
                .stream().filter(d -> pileTypes.contains(d.getTypeId())).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(deviceInfoList)) {
            return ResponseResult.ok(result);
        }
        Set<String> pileIds = deviceInfoList.stream().map(DeviceBasicInfoDto::getId).filter(StringUtil::isNotEmpty).collect(Collectors.toSet());
        List<String> pileCodes = deviceInfoList.stream().map(DeviceBasicInfoDto::getDeviceNumber).filter(StringUtil::isNotEmpty).collect(Collectors.toList());
        //日期类型 1-日 2-月 3-年 4-总(开始时间传最早的站点创建时间,结束时间传当前时间)
        String startTime = overviewQueryVo.getStartTime();
        String endTime = overviewQueryVo.getEndTime();
        Integer dateType = null;
        switch (overviewQueryVo.getDateType()) {
            case 1:
                //获取电桩功率曲线数据
                DeviceCommonUtil.getDevicePowerCurve(pileIds, startTime, endTime, FunctionLogoParamVo.PILE_POWER, dataService, result);
                //获取电桩充电量，电桩放电量,充电金额,放电金额
                Map<String, OrderCountDto> orderCountMap = togetherService.findOrderRecordListByPileCodes(pileCodes, startTime, endTime).getData();
                if (MapUtils.isNotEmpty(orderCountMap)) {
                    result.setChargeQt(DoubleUtil.getToDouble(orderCountMap.values().stream().mapToDouble(OrderCountDto::getChargeQt).sum()));
                    result.setDischargeQt(DoubleUtil.getAbsDouble(orderCountMap.values().stream().mapToDouble(OrderCountDto::getDischargeQt).sum()));
                    result.setChargeMoney(DoubleUtil.getAbsBigDecimal(orderCountMap.values().stream().map(OrderCountDto::getChargeMoney).reduce(BigDecimal.ZERO, BigDecimal::add)));
                    result.setDischargeMoney(DoubleUtil.getAbsBigDecimal(orderCountMap.values().stream().map(OrderCountDto::getDischargeMoney).reduce(BigDecimal.ZERO, BigDecimal::add)));
                }
                break;
            case 2:
                dateType = 1;
                break;
            case 3:
                dateType = 2;
                break;
            case 4:
                dateType = 3;
                break;
            default:
                return ResponseResult.paramError(ResponseResult.PARAM_ERROR);
        }
        if (dateType != null && StringUtil.isNotEmpty(dateType)) {
            Map<String, OrderQtDto> orderQtMap = togetherService.findOrderQtListByPileCodes(pileCodes, startTime, endTime, dateType).getData();
            if (MapUtils.isNotEmpty(orderQtMap)) {
                String startDate = overviewQueryVo.getStartTime().substring(0, 10);
                String endDate = overviewQueryVo.getEndTime().substring(0, 10);
                List<String> timeList = DateUtil.getDateBetween(dateType, startDate, endDate);
                for (String dateTime : timeList) {
                    if (orderQtMap.containsKey(dateTime)) {
                        OrderQtDto orderQtDto = orderQtMap.get(dateTime);
                        result.setChargeQt(result.getChargeQt() + orderQtDto.getChargeQt());
                        result.setDischargeQt(result.getDischargeQt() + orderQtDto.getDischargeQt());
                        result.setChargeMoney(result.getChargeMoney().add(orderQtDto.getChargeMoney()));
                        result.setDischargeMoney(result.getDischargeMoney().add(orderQtDto.getDischargeMoney()));
                        result.getCurve1List().add(DoubleUtil.getAbsDouble(orderQtDto.getChargeQt()));
                        result.getCurve2List().add(DoubleUtil.getAbsDouble(orderQtDto.getDischargeQt()));
                    } else {
                        result.getCurve1List().add(0.0);
                        result.getCurve2List().add(0.0);
                    }
                    if (dateType == 1) {
                        result.getTimeList().add(dateTime.substring(8, 10));
                    } else if (dateType == 2) {
                        result.getTimeList().add(dateTime.substring(5, 7));
                    } else {
                        result.getTimeList().add(dateTime);
                    }
                }
                result.setChargeQt(DoubleUtil.getAbsDouble(result.getChargeQt()));
                result.setDischargeQt(DoubleUtil.getAbsDouble(result.getDischargeQt()));
                result.setChargeMoney(DoubleUtil.getAbsBigDecimal(result.getChargeMoney()));
                result.setDischargeMoney(DoubleUtil.getAbsBigDecimal(result.getDischargeMoney()));
            }
        }
        return ResponseResult.ok(result);
    }

    // 提取时间差计算方法
    private String calculateTimeAgo(LocalDateTime createTime, LocalDateTime nowTime) {
        Long days = DateUtil.compareDiffBetweenDays(createTime, nowTime);
        if (days > 0) {
            return days + "天前";
        } else {
            Long hours = DateUtil.compareDiffBetweenHours(createTime, nowTime);
            if (hours > 0) {
                return hours + "小时前";
            } else {
                Long minutes = DateUtil.compareDiffBetweenMinutes(createTime, nowTime);
                if (minutes > 0) {
                    return minutes + "分钟前";
                } else {
                    Long seconds = DateUtil.compareDiffBetweenSecond(createTime, nowTime);
                    return seconds + "秒前";
                }
            }
        }
    }

    /**
     * 处理光伏上网收益(单个站点)
     *
     * @param dateList       日期列表
     * @param electMap       电价配置 日期(年月日) -> (分时段时间(00:00) -> 电价)
     * @param gatewayDataMap 关口表尖峰平谷历史数据 日期(年月日) -> (分时段时间(00:00) -> 历史数据)
     */
    private BigDecimal processPvInternetIncome(List<String> dateList, Map<String, Map<Integer, BigDecimal>> electMap,
                                               Map<String, List<NodeDifHistoryDto>> gatewayDataMap) {
        //获取光伏上网收益
        //定义光伏上网收益
        BigDecimal internetMoney = BigDecimal.ZERO;
        if (CollectionUtils.isNotEmpty(dateList) && MapUtils.isNotEmpty(electMap) && MapUtils.isNotEmpty(gatewayDataMap)) {
            Map<String, Double> topRevKwhMap = Maps.newHashMap();
            Map<String, Double> peakRevKwhMap = Maps.newHashMap();
            Map<String, Double> plainRevKwhMap = Maps.newHashMap();
            Map<String, Double> valleyRevKwhMap = Maps.newHashMap();
            Map<String, Double> deepRevKwhMap = Maps.newHashMap();
            if (gatewayDataMap.containsKey(FunctionLogoParamVo.TOP_REV_KWH)) {
                topRevKwhMap = gatewayDataMap.get(FunctionLogoParamVo.TOP_REV_KWH).stream().collect(Collectors.groupingBy(n -> n.getFirstDateTime().substring(0, 10),
                        Collectors.summingDouble(n -> DoubleUtil.getObjSub(n.getFirstDataValue(), n.getLastDataValue()))));

            }
            if (gatewayDataMap.containsKey(FunctionLogoParamVo.PEAK_REV_KWH)) {
                peakRevKwhMap = gatewayDataMap.get(FunctionLogoParamVo.PEAK_REV_KWH).stream().collect(Collectors.groupingBy(n -> n.getFirstDateTime().substring(0, 10),
                        Collectors.summingDouble(n -> DoubleUtil.getObjSub(n.getFirstDataValue(), n.getLastDataValue()))));

            }
            if (gatewayDataMap.containsKey(FunctionLogoParamVo.PLAIN_REV_KWH)) {
                plainRevKwhMap = gatewayDataMap.get(FunctionLogoParamVo.PLAIN_REV_KWH).stream().collect(Collectors.groupingBy(n -> n.getFirstDateTime().substring(0, 10),
                        Collectors.summingDouble(n -> DoubleUtil.getObjSub(n.getFirstDataValue(), n.getLastDataValue()))));

            }
            if (gatewayDataMap.containsKey(FunctionLogoParamVo.VALLEY_REV_KWH)) {
                valleyRevKwhMap = gatewayDataMap.get(FunctionLogoParamVo.VALLEY_REV_KWH).stream().collect(Collectors.groupingBy(n -> n.getFirstDateTime().substring(0, 10),
                        Collectors.summingDouble(n -> DoubleUtil.getObjSub(n.getFirstDataValue(), n.getLastDataValue()))));

            }
            if (gatewayDataMap.containsKey(FunctionLogoParamVo.DEEP_REV_KWH)) {
                deepRevKwhMap = gatewayDataMap.get(FunctionLogoParamVo.DEEP_REV_KWH).stream().collect(Collectors.groupingBy(n -> n.getFirstDateTime().substring(0, 10),
                        Collectors.summingDouble(n -> DoubleUtil.getObjSub(n.getFirstDataValue(), n.getLastDataValue()))));

            }
            for (String date : dateList) {
                if (electMap.containsKey(date)) {
                    Map<Integer, BigDecimal> dateElectMap = electMap.get(date);
                    //尖时段收益
                    if (dateElectMap.containsKey(1) && topRevKwhMap.containsKey(date)) {
                        internetMoney = internetMoney.add(dateElectMap.get(1).multiply(BigDecimal.valueOf(topRevKwhMap.get(date))));
                    }
                    //峰时段收益
                    if (dateElectMap.containsKey(2) && peakRevKwhMap.containsKey(date)) {
                        internetMoney = internetMoney.add(dateElectMap.get(2).multiply(BigDecimal.valueOf(peakRevKwhMap.get(date))));
                    }
                    //平时段收益
                    if (dateElectMap.containsKey(3) && plainRevKwhMap.containsKey(date)) {
                        internetMoney = internetMoney.add(dateElectMap.get(3).multiply(BigDecimal.valueOf(plainRevKwhMap.get(date))));
                    }
                    //谷时段收益
                    if (dateElectMap.containsKey(4) && valleyRevKwhMap.containsKey(date)) {
                        internetMoney = internetMoney.add(dateElectMap.get(4).multiply(BigDecimal.valueOf(valleyRevKwhMap.get(date))));
                    }
                    //深谷时段收益
                    if (dateElectMap.containsKey(5) && deepRevKwhMap.containsKey(date)) {
                        internetMoney = internetMoney.add(dateElectMap.get(5).multiply(BigDecimal.valueOf(deepRevKwhMap.get(date))));
                    }
                    //全时段收益(平时)
                    if (dateElectMap.containsKey(6) && plainRevKwhMap.containsKey(date)) {
                        internetMoney = internetMoney.add(dateElectMap.get(6).multiply(BigDecimal.valueOf(plainRevKwhMap.get(date))));
                    }
                }
            }
        }
        return DoubleUtil.getToBigDecimal(internetMoney);
    }

    /**
     * 处理光伏消纳收益(单个站点)
     *
     * @param electMap      电价配置 日期(年月日) -> (分时段时间(00:00) -> 电价)
     * @param internetQtMap 光伏上网电量 日期(年月日时分秒) -> 上网电量
     * @param inverterQtMap 逆变器发电量 日期(年月日时分秒) -> 发电量
     */
    private BigDecimal processPvConsumeIncome(Map<String, Map<String, BigDecimal>> electMap, Map<String, Double> internetQtMap,
                                              Map<String, Double> inverterQtMap) {
        //获取光伏消纳收益
        //定义消纳收益
        BigDecimal consumeMoney = BigDecimal.ZERO;

        if (MapUtils.isNotEmpty(electMap) && MapUtils.isNotEmpty(internetQtMap) && MapUtils.isNotEmpty(inverterQtMap)) {
            for (Map.Entry<String, Double> entry : inverterQtMap.entrySet()) {
                String key = entry.getKey();
                Double value = entry.getValue();
                Double internetQt = 0.0;
                if (MapUtils.isNotEmpty(internetQtMap) && internetQtMap.containsKey(key)) {
                    internetQt = internetQtMap.get(key);
                }
                //获取消纳电量电价
                String date = key.substring(0, 10);
                String time = key.substring(11, 16);
                BigDecimal consumeElect = new BigDecimal("0.0");
                if (electMap.containsKey(date) && electMap.get(date).containsKey(time)) {
                    consumeElect = electMap.get(date).get(time);
                }
                //光伏消纳收益 (消纳电量 = 逆变器发电量 - 上网电量) * 消纳电量电价
                if (value != null && internetQt != null) {
                    consumeMoney = consumeMoney.add(new BigDecimal(value - internetQt).multiply(consumeElect));
                }
            }
        }
        return DoubleUtil.getToBigDecimal(consumeMoney);
    }

    /**
     * 获取储能收益(单个站点)
     *
     * @param dateList         日期列表
     * @param dayQtMap         每天的尖峰平谷深时 日期(年月日) -> (尖峰平谷深时 -> 电量)
     * @param purchaseElectMap 购电电价配置 日期(年月日) -> (分时段时间(00:00) -> 电价)
     * @param saleElectMap     售电电价配置 日期(年月日) -> (分时段时间(00:00) -> 电价)
     * @return 储能收益
     */
    private BigDecimal getStorageIncome(List<String> dateList, Map<String, Map<String, Double>> dayQtMap,
                                        Map<String, Map<Integer, BigDecimal>> purchaseElectMap, Map<String, Map<Integer, BigDecimal>> saleElectMap) {
        //购电成本
        BigDecimal purchaseMoney = BigDecimal.ZERO;
        //售电收入
        BigDecimal saleMoney = BigDecimal.ZERO;
        for (String date : dateList) {
            if (dayQtMap.containsKey(date)) {
                Map<String, Double> functionLogoMap = dayQtMap.get(date);
                //购电成本（总） = 购电成本(尖) + 购电成本(峰) + 购电成本(平) + 购电成本(谷) + 购电成本(深谷)
                //购电成本 = 分时正向有功电量 * 分时购电电价
                Map<Integer, BigDecimal> purchaseTypeMap = Maps.newHashMap();
                if (purchaseElectMap.containsKey(date)) {
                    purchaseTypeMap = purchaseElectMap.get(date);
                }
                if (functionLogoMap.containsKey(FunctionLogoParamVo.TOP_SUP_KWH) && purchaseTypeMap.containsKey(1)) {
                    purchaseMoney = purchaseMoney.add(BigDecimal.valueOf(functionLogoMap.get(FunctionLogoParamVo.TOP_SUP_KWH)).multiply(purchaseTypeMap.get(1)));
                }
                if (functionLogoMap.containsKey(FunctionLogoParamVo.PEAK_SUP_KWH) && purchaseTypeMap.containsKey(2)) {
                    purchaseMoney = purchaseMoney.add(BigDecimal.valueOf(functionLogoMap.get(FunctionLogoParamVo.PEAK_SUP_KWH)).multiply(purchaseTypeMap.get(2)));
                }
                if (functionLogoMap.containsKey(FunctionLogoParamVo.PLAIN_SUP_KWH) && purchaseTypeMap.containsKey(3)) {
                    purchaseMoney = purchaseMoney.add(BigDecimal.valueOf(functionLogoMap.get(FunctionLogoParamVo.PLAIN_SUP_KWH)).multiply(purchaseTypeMap.get(3)));
                }
                if (functionLogoMap.containsKey(FunctionLogoParamVo.VALLEY_SUP_KWH) && purchaseTypeMap.containsKey(4)) {
                    purchaseMoney = purchaseMoney.add(BigDecimal.valueOf(functionLogoMap.get(FunctionLogoParamVo.VALLEY_SUP_KWH)).multiply(purchaseTypeMap.get(4)));
                }
                if (functionLogoMap.containsKey(FunctionLogoParamVo.DEEP_SUP_KWH) && purchaseTypeMap.containsKey(5)) {
                    purchaseMoney = purchaseMoney.add(BigDecimal.valueOf(functionLogoMap.get(FunctionLogoParamVo.DEEP_SUP_KWH)).multiply(purchaseTypeMap.get(5)));
                }

                Map<Integer, BigDecimal> saleTypeMap = Maps.newHashMap();
                if (saleElectMap.containsKey(date)) {
                    saleTypeMap = saleElectMap.get(date);
                }
                if (functionLogoMap.containsKey(FunctionLogoParamVo.TOP_REV_KWH) && saleTypeMap.containsKey(1)) {
                    saleMoney = saleMoney.add(BigDecimal.valueOf(functionLogoMap.get(FunctionLogoParamVo.TOP_REV_KWH)).multiply(saleTypeMap.get(1)));
                }
                if (functionLogoMap.containsKey(FunctionLogoParamVo.PEAK_REV_KWH) && saleTypeMap.containsKey(2)) {
                    saleMoney = saleMoney.add(BigDecimal.valueOf(functionLogoMap.get(FunctionLogoParamVo.PEAK_REV_KWH)).multiply(saleTypeMap.get(2)));
                }
                if (functionLogoMap.containsKey(FunctionLogoParamVo.PLAIN_REV_KWH) && saleTypeMap.containsKey(3)) {
                    saleMoney = saleMoney.add(BigDecimal.valueOf(functionLogoMap.get(FunctionLogoParamVo.PLAIN_REV_KWH)).multiply(saleTypeMap.get(3)));
                }
                if (functionLogoMap.containsKey(FunctionLogoParamVo.VALLEY_REV_KWH) && saleTypeMap.containsKey(4)) {
                    saleMoney = saleMoney.add(BigDecimal.valueOf(functionLogoMap.get(FunctionLogoParamVo.VALLEY_REV_KWH)).multiply(saleTypeMap.get(4)));
                }
                if (functionLogoMap.containsKey(FunctionLogoParamVo.DEEP_REV_KWH) && saleTypeMap.containsKey(5)) {
                    saleMoney = saleMoney.add(BigDecimal.valueOf(functionLogoMap.get(FunctionLogoParamVo.DEEP_REV_KWH)).multiply(saleTypeMap.get(5)));
                }
            }
        }
        //储能收益 = 售电收入 - 购电成本
        return DoubleUtil.getToBigDecimal(saleMoney.subtract(purchaseMoney));
    }

    // 为单个 config 构建 48 个半小时的 Map<String, BigDecimal>
    private static Map<String, BigDecimal> buildHalfHourMapForOneConfig(List<ElectConfigDto.ElectTimeFrame> entities) {
        // 创建分钟级电价数组（0 ~ 1439）
        BigDecimal[] priceByMinute = new BigDecimal[1440];

        // 填充每个时段
        for (ElectConfigDto.ElectTimeFrame e : entities) {
            int start = toMinutes(e.getStartTime());
            int end = "23:59".equals(e.getEndTime()) ? 1439 : toMinutes(e.getEndTime());
            Arrays.fill(priceByMinute, start, end + 1, e.getElectMoney());
        }

        // 构建 48 个半小时点的 map
        return IntStream.range(0, 48).boxed().collect(
                LinkedHashMap::new,
                (map, slot) -> {
                    int totalMin = slot * 30;
                    String timeKey = String.format("%02d:%02d", totalMin / 60, totalMin % 60);
                    map.put(timeKey, priceByMinute[totalMin]);
                },
                LinkedHashMap::putAll
        );
    }

    private static int toMinutes(String time) {
        String[] parts = time.split(":");
        return Integer.parseInt(parts[0]) * 60 + Integer.parseInt(parts[1]);
    }

    /**
     * 校验电桩概览查询参数
     *
     * @param overviewQueryVo 查询参数对象
     * @return 如果参数有效返回true，否则返回false
     */
    private boolean validateQueryParams(OverviewQueryVo overviewQueryVo) {
        if (overviewQueryVo == null) {
            return true;
        }
        String siteId = overviewQueryVo.getSiteId();
        String deviceId = overviewQueryVo.getDeviceId();
        Integer dateType = overviewQueryVo.getDateType();
        String startTime = overviewQueryVo.getStartTime();
        String endTime = overviewQueryVo.getEndTime();
        // 校验必填字段是否为空
        if ((StringUtil.isEmpty(siteId) && StringUtil.isEmpty(deviceId)) || StringUtil.isEmpty(dateType) || StringUtil.isEmpty(startTime) || StringUtil.isEmpty(endTime)) {
            return true;
        }
        // 校验日期格式
        if (DeviceCommonUtil.isValidDateFormat(startTime) || DeviceCommonUtil.isValidDateFormat(endTime)) {
            return true;
        }
        // 校验开始时间不能晚于结束时间
        return startTime.compareTo(endTime) > 0;
    }

}
