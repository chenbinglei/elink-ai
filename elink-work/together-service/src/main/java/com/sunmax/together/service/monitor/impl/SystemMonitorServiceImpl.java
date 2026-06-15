package com.sunmax.together.service.monitor.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.TypeReference;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.sunmax.common.config.redis.RedisGeneralUtil;
import com.sunmax.common.dto.PageDto;
import com.sunmax.common.dto.crontab.ComputeNodeListDto;
import com.sunmax.common.dto.crontab.NodeHistoryDataDto;
import com.sunmax.common.dto.data.DeviceHistoryDto;
import com.sunmax.common.dto.data.NodeDifHistoryDto;
import com.sunmax.common.dto.device.*;
import com.sunmax.common.model.RealDataModel;
import com.sunmax.common.model.general.PileRealModel;
import com.sunmax.common.util.DateUtil;
import com.sunmax.common.util.DoubleUtil;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.util.StringUtil;
import com.sunmax.common.util.oss.FileUtil;
import com.sunmax.common.vo.FunctionLogoParamVo;
import com.sunmax.common.vo.ReaFieldParamVo;
import com.sunmax.common.vo.SiteFieldParamVo;
import com.sunmax.common.vo.crontab.DeviceNodeValueVo;
import com.sunmax.common.vo.data.DeviceCountQueryVo;
import com.sunmax.common.vo.data.DeviceHistoryQueryVo;
import com.sunmax.common.vo.device.DeviceAlarmEventQueryVo;
import com.sunmax.together.dao.order.OrderRecordDao;
import com.sunmax.together.dto.monitor.centralMonitorOld.PvSiteMonitorDto;
import com.sunmax.together.dto.monitor.systemMonitor.*;
import com.sunmax.together.dto.monitor.systemMonitor.DeviceFieldDto;
import com.sunmax.together.entity.order.OrderRecordEntity;
import com.sunmax.together.enums.SystemCurveEnum;
import com.sunmax.together.service.feign.CrontabService;
import com.sunmax.together.service.feign.DataService;
import com.sunmax.together.service.feign.DeviceService;
import com.sunmax.together.service.monitor.SystemMonitorService;
import com.sunmax.together.util.ExtraValueUtil;
import com.sunmax.together.vo.monitor.systemMonitor.FaultAlarmQueryVo;
import com.sunmax.together.vo.monitor.systemMonitor.HistoryDataQueryVo;
import com.sunmax.together.vo.monitor.systemMonitor.SystemQueryVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.compress.utils.Lists;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.sunmax.common.util.DateUtil.*;
import static com.sunmax.common.util.DoubleUtil.getAbsDouble;
import static com.sunmax.common.util.DoubleUtil.getToDouble;

@Service
@Slf4j
public class SystemMonitorServiceImpl implements SystemMonitorService {

    @Autowired
    private DeviceService deviceService;

    @Autowired
    private CrontabService crontabService;

    @Autowired
    private DataService dataService;

    @Autowired
    private OrderRecordDao orderRecordDao;

    @Override
    public ResponseResult<List<SystemTreeDto>> getSystemTreeList(String siteId, Integer type) {
        try {
            return ResponseResult.ok(this.getSystemMonitorData(siteId, siteId, type, null));
        } catch (RuntimeException e) {
            log.error("获取系统树形结构报错", e);
            return ResponseResult.paramError("获取系统树形结构报错");
        }
    }

    @Override
    public ResponseResult<SystemPvDto> findSystemPvData(String dataId) {
        try {

            //返回的对象
            SystemPvDto result = new SystemPvDto();

            String siteId = deviceService.findSiteIdBySubId(dataId).getData();
            if (StringUtil.isEmpty(siteId)) {
                return ResponseResult.paramError(ResponseResult.PARAM_ERROR);
            }
            //根据站点id查询站点信息
            SiteInfoDto siteInfo = deviceService.findSiteBasicInfoByIds(Collections.singletonList(siteId)).getData().get(siteId);
            //获取站点系统静态数据
            Map<String, Double> deviceReaMap = Maps.newHashMap();
            Map<String, String> reaMap = Stream.of(SiteFieldParamVo.PV_CAPACITY, SiteFieldParamVo.ARRAY_AREA, SiteFieldParamVo.ARRAY_INCLINATION)
                    .collect(Collectors.toMap(a -> a, a -> a));
            if (Objects.equals(siteId, dataId)) {
                deviceReaMap = siteInfo.getSiteScenarioTypeDtos().stream().filter(s -> StringUtil.isNotEmpty(s.getScenarioType())
                                && Objects.equals(s.getScenarioType(), 1)).flatMap(s -> s.getReaList().stream()
                                .filter(r -> StringUtil.isNotEmpty(r.getFieldName()) && StringUtil.isNotEmpty(r.getValue()) && reaMap.containsKey(r.getFieldName())))
                        .collect(Collectors.groupingBy(DeviceReaDto::getFieldName, Collectors.summingDouble(d -> Double.parseDouble(String
                                .valueOf(d.getValue())))));
            } else {
                //获取站点子系统静态数据
                Optional<SiteScenarioTypeDto> optional = siteInfo.getSiteScenarioTypeDtos().stream().filter(s -> Objects.equals(s.getId(), dataId)).findFirst();
                if (optional.isPresent()) {
                    deviceReaMap = optional.get().getReaList().stream().filter(d -> StringUtil.isNotEmpty(d.getFieldName()) && StringUtil.isNotEmpty(d.getValue())
                            && reaMap.containsKey(d.getFieldName())).collect(Collectors.toMap(DeviceReaDto::getFieldName, d -> Double.parseDouble(String.valueOf(d.getValue())), (k1, k2) -> k1));
                }
            }
            if (MapUtils.isNotEmpty(deviceReaMap)) {
                //装机容量
                if (deviceReaMap.containsKey(SiteFieldParamVo.PV_CAPACITY) && StringUtil.isNotEmpty(deviceReaMap.get(SiteFieldParamVo.PV_CAPACITY))) {
                    result.setCapacity(Double.valueOf(String.valueOf(deviceReaMap.get(SiteFieldParamVo.PV_CAPACITY))));
                }
                //阵列面积
                if (deviceReaMap.containsKey(SiteFieldParamVo.ARRAY_AREA) && StringUtil.isNotEmpty(deviceReaMap.get(SiteFieldParamVo.ARRAY_AREA))) {
                    result.setArrayArea(Double.valueOf(String.valueOf(deviceReaMap.get(SiteFieldParamVo.ARRAY_AREA))));
                }
                //阵列倾角
                if (deviceReaMap.containsKey(SiteFieldParamVo.ARRAY_INCLINATION) && StringUtil.isNotEmpty(deviceReaMap.get(SiteFieldParamVo.ARRAY_INCLINATION))) {
                    result.setArrayInclination(Double.valueOf(String.valueOf(deviceReaMap.get(SiteFieldParamVo.ARRAY_INCLINATION))));
                }
            }
            List<SystemTreeDto> systemTreeList = this.getSystemMonitorData(siteId, dataId, 1, siteInfo);
            if (CollectionUtils.isNotEmpty(systemTreeList)) {
                //查询逆变器设备
                List<SystemTreeDto> pvInverterList = systemTreeList.stream().filter(s -> StringUtil.isNotEmpty(s.getTypeId()) && ("20".equals(s.getTypeId()) || "77".equals(s.getTypeId()))).collect(Collectors.toList());
                if (CollectionUtils.isNotEmpty(pvInverterList)) {
                    result.setInverterNum(pvInverterList.size());
                    Set<String> deviceIds = pvInverterList.stream().map(SystemTreeDto::getId).collect(Collectors.toSet());
                    //根据多个设备id和功能点标识查询设备功能点实时数据
                    Map<String, Map<String, RealDataModel>> realDataMap = Maps.newHashMap();
                    Stream.of(deviceService.getDeviceFunctionsRealDataByIds(deviceIds, FunctionLogoParamVo.ACTIVE_POWER, 2).getData(),
                                    deviceService.getDeviceFunctionsRealDataByIds(deviceIds, FunctionLogoParamVo.TOTAL_POWER_GENERATION, 1).getData())
                            .filter(Objects::nonNull) // 过滤掉 null
                            .flatMap(map -> map.entrySet().stream())
                            .forEach(entry -> realDataMap.computeIfAbsent(entry.getKey(), k -> new HashMap<>()).putAll(entry.getValue()));
                    //获取设备功能点实时数据
                    Map<String, List<PvSiteMonitorDto.FunctionRalData>> functionRalDataMap = realDataMap.entrySet().stream()
                            .flatMap(entry -> entry.getValue().entrySet().stream().map(subEntry -> {
                                PvSiteMonitorDto.FunctionRalData functionRalData = new PvSiteMonitorDto.FunctionRalData();
                                functionRalData.setFunctionLogo(subEntry.getKey());
                                functionRalData.setRealData(subEntry.getValue().getDataValue());
                                return functionRalData;
                            })).collect(Collectors.groupingBy(PvSiteMonitorDto.FunctionRalData::getFunctionLogo));

                    //统计总发电量
                    if (functionRalDataMap.containsKey(FunctionLogoParamVo.TOTAL_POWER_GENERATION) && CollectionUtils.isNotEmpty(functionRalDataMap.get(FunctionLogoParamVo.TOTAL_POWER_GENERATION))) {
                        result.setAccTotalQt(getToDouble(functionRalDataMap.get(FunctionLogoParamVo.TOTAL_POWER_GENERATION).stream().filter(f -> StringUtil.isNotEmpty(f.getRealData())).mapToDouble(f -> Double.parseDouble(String.valueOf(f.getRealData()))).sum()));
                    }

                    //查询今日发电量和昨日发电量
                    String lastDayDate = localDateToStr(LocalDate.now().minusDays(1));
                    String todayDate = localDateToStr(LocalDate.now());
                    DeviceHistoryQueryVo dayQueryVo = new DeviceHistoryQueryVo();
                    dayQueryVo.setDeviceIds(deviceIds);
                    dayQueryVo.setFunctionLogos(Collections.singleton(FunctionLogoParamVo.TOTAL_POWER_GENERATION));
                    dayQueryVo.setStartTime(DateUtil.getDayStart(lastDayDate));
                    dayQueryVo.setEndTime(DateUtil.getDayEnd(todayDate));
                    dayQueryVo.setTimeInterval("1d");
                    Map<String, Map<String, List<NodeDifHistoryDto>>> deviceDifMap = dataService.findNodeDifHistoryListFeign(dayQueryVo).getData();
                    if (MapUtils.isNotEmpty(deviceDifMap)) {
                        Map<String, Double> dataMap = deviceDifMap.values().stream().flatMap(d -> d.values().stream().flatMap(Collection::stream))
                                .filter(d -> StringUtil.isNotEmpty(d.getFirstDateTime()))
                                .collect(Collectors.groupingBy(d -> d.getFirstDateTime().substring(0, 10),
                                        Collectors.summingDouble(d -> DoubleUtil.getObjSub(d.getFirstDataValue(), d.getLastDataValue()))));
                        //统计昨日发电量
                        if (dataMap.containsKey(lastDayDate)) {
                            result.setLastDayQt(getToDouble(dataMap.get(lastDayDate)));
                        }
                        //统计今日发电量
                        if (dataMap.containsKey(todayDate)) {
                            result.setDayQt(getToDouble(dataMap.get(todayDate)));
                        }
                    }

                    //统计总功率
                    if (functionRalDataMap.containsKey(FunctionLogoParamVo.ACTIVE_POWER) && CollectionUtils.isNotEmpty(functionRalDataMap.get(FunctionLogoParamVo.ACTIVE_POWER))) {
                        result.setTotalPower(getToDouble(functionRalDataMap.get(FunctionLogoParamVo.ACTIVE_POWER).stream().filter(f -> StringUtil.isNotEmpty(f.getRealData())).mapToDouble(f -> Double.parseDouble(String.valueOf(f.getRealData()))).sum()));
                    }

                    //实时功率归一化 总功率/光伏装机容量
                    if (StringUtil.isNotEmpty(result.getTotalPower()) && StringUtil.isNotEmpty(result.getCapacity())) {
                        result.setRealPowerNorm(getToDouble(result.getTotalPower() / result.getCapacity() * 100));
                    }

                    //计算今日等效利用小时数(今日发电量 / 站点装机容量)
                    if (StringUtil.isNotEmpty(result.getDayQt()) && StringUtil.isNotEmpty(result.getCapacity()) && result.getCapacity() > 0.0) {
                        result.setDayHours(getToDouble(result.getDayQt() / result.getCapacity()));
                    }

                    //查询光伏气象站昨日理论发电量
                    Set<String> pvTyGnIds = systemTreeList.stream().filter(s -> StringUtil.isNotEmpty(s.getTypeId()) && "66".equals(s.getTypeId())).map(SystemTreeDto::getId).collect(Collectors.toSet());
                    DeviceHistoryQueryVo deviceHistoryQueryVo = new DeviceHistoryQueryVo();
                    deviceHistoryQueryVo.setDeviceIds(pvTyGnIds);
                    deviceHistoryQueryVo.setFunctionLogos(Collections.singleton(FunctionLogoParamVo.PV_THEORY_CAPACITY));
                    deviceHistoryQueryVo.setStartTime(getDayStart(localDateToStr(LocalDate.now().minusDays(1))));
                    deviceHistoryQueryVo.setEndTime(getDayEnd(localDateToStr(LocalDate.now().minusDays(1))));
                    deviceHistoryQueryVo.setTimeInterval("1d");
                    //获取昨日光伏理论发电量 设备id -> (功能点标识 -> 功能点历史数据)
                    Map<String, Map<String, List<NodeDifHistoryDto>>> data = dataService.findNodeDifHistoryListFeign(deviceHistoryQueryVo).getData();
                    double lastDayTheoryQt = data.entrySet().stream().flatMap(s -> s.getValue().entrySet().stream().flatMap(n -> n.getValue().stream().map(NodeDifHistoryDto::getLastDataValue)).filter(StringUtil::isNotEmpty).map(d -> Double.parseDouble(String.valueOf(d))).collect(Collectors.toList()).stream()).collect(Collectors.toList()).stream().mapToDouble(s -> s).sum();

                    //计算昨日系统效率(昨日发电量 / 昨日理论发电量 * 100)
                    if (StringUtil.isNotEmpty(lastDayTheoryQt) && StringUtil.isNotEmpty(result.getLastDayQt()) && lastDayTheoryQt > 0.0) {
                        result.setLastDayEff(getToDouble(result.getLastDayQt() / lastDayTheoryQt * 100));
                    }

                    //计算昨日等效利用小时数(昨日发电量 / 站点装机容量)
                    if (StringUtil.isNotEmpty(result.getLastDayQt()) && StringUtil.isNotEmpty(result.getCapacity()) && result.getCapacity() > 0.0) {
                        result.setLastDayHours(getToDouble(result.getLastDayQt() / result.getCapacity()));
                    }

                    //计算昨日损失电量(昨日理论发电量 * 昨日系统效率- 昨日发电量)
                    if (StringUtil.isNotEmpty(result.getLastDayQt()) && StringUtil.isNotEmpty(result.getLastDayEff())) {
                        result.setLastDayLossDayQt((lastDayTheoryQt * result.getLastDayEff() / 100) - result.getLastDayQt());
                    }

                    //获取昨日峰值发电功率
                    DeviceCountQueryVo deviceCountQueryVo = new DeviceCountQueryVo();
                    deviceCountQueryVo.setDeviceIds(pvInverterList.stream().map(SystemTreeDto::getId).collect(Collectors.toSet()));
                    deviceCountQueryVo.setStartTime(getDayStart(localDateToStr(LocalDate.now().minusDays(1))));
                    deviceCountQueryVo.setEndTime(getDayEnd(localDateToStr(LocalDate.now().minusDays(1))));
                    deviceCountQueryVo.setCuntFun("MAX");
                    deviceCountQueryVo.setTimeInterval("1d");
                    deviceCountQueryVo.setFunctionLogos(new HashSet<>(Collections.singletonList(FunctionLogoParamVo.ACTIVE_POWER)));
                    Map<String, Map<String, List<DeviceHistoryDto>>> deviceMaxPowerMap = dataService.findDeviceCountFunListFeign(deviceCountQueryVo).getData();
                    if (MapUtils.isNotEmpty(deviceMaxPowerMap)) {
                        double lastDayMaxPower = deviceMaxPowerMap.entrySet().stream().flatMap(s -> s.getValue().entrySet().stream()
                                        .flatMap(n -> n.getValue().stream().map(DeviceHistoryDto::getDataValue)).filter(StringUtil::isNotEmpty)
                                        .map(d -> Double.parseDouble(String.valueOf(d))).collect(Collectors.toList()).stream())
                                .mapToDouble(s -> s).sum();
                        result.setLastDayMaxPower(lastDayMaxPower);
                    }

                    //计算二氧化碳减排量,节约标煤量,等效植树量
                    //根据多个站点id查询站点设置数据
                    SiteSetUpDto siteSetUp = deviceService.findSiteSetUpBySiteIds(Collections.singletonList(siteId)).getData().get(siteId);
                    if (siteSetUp != null && StringUtil.isNotEmpty(result.getAccTotalQt())) {
                        //二氧化碳减排量(电站发电量 * CO₂减排转换系数(0.475))
                        if (StringUtil.isNotEmpty(siteSetUp.getReduceCoeff())) {
                            result.setCo2Reduction(getToDouble(result.getAccTotalQt() * siteSetUp.getReduceCoeff()));
                        }
                        //节约标煤量(电站发电量 * 节约标准煤转换系数(0.4))
                        if (StringUtil.isNotEmpty(siteSetUp.getTceCoeff())) {
                            result.setStandardCoalReduction(getToDouble(result.getAccTotalQt() * siteSetUp.getTceCoeff()));
                        }
                        //等效植树量(二氧化碳减排量 / 等效植树量转换系数（18.3）/ 40)
                        if (result.getCo2Reduction() != null && siteSetUp.getTreeCoeff() != null && siteSetUp.getTreeCoeff() != 0.0) {
                            result.setTreeReduction(getToDouble(result.getCo2Reduction() / siteSetUp.getTreeCoeff() / 40));
                        }
                    }
                }
            }
            return ResponseResult.ok(result);
        } catch (RuntimeException e) {
            log.error("查询光伏系统数据报错", e);
            return ResponseResult.paramError("查询光伏系统数据报错,请稍后重试!!");
        }
    }

    @Override
    public ResponseResult<SystemPvInverterDto> findPvInverterData(String dataId) {
        try {
            //返回的对象
            SystemPvInverterDto result = new SystemPvInverterDto();
            //根据设备id查询光伏逆变器数据
            DeviceBasicInfoDto deviceBasicInfo = deviceService.findDeviceBasicInfoByIds(Collections.singletonList(dataId)).getData().get(dataId);
            if (deviceBasicInfo != null) {
                BeanUtils.copyProperties(deviceBasicInfo, result);
                //获取设备功能点实时数据
                Map<String, RealDataModel> realDataModelMap = deviceService.getDeviceFunctionsRealDataByIds(Collections.singleton(deviceBasicInfo.getId()),
                        FunctionLogoParamVo.CURRENT_STATUS_OF_THE_INVERTER, 2).getData().get(deviceBasicInfo.getId());
                if (MapUtils.isNotEmpty(realDataModelMap)) {
                    //运行状态
                    if (realDataModelMap.containsKey(FunctionLogoParamVo.CURRENT_STATUS_OF_THE_INVERTER) && StringUtil.isNotEmpty(realDataModelMap.get(FunctionLogoParamVo.CURRENT_STATUS_OF_THE_INVERTER).getDataValue())) {
                        String dataValue = String.valueOf(realDataModelMap.get(FunctionLogoParamVo.CURRENT_STATUS_OF_THE_INVERTER).getDataValue());
                        result.setRunState((int) Double.parseDouble(dataValue));
                        //根据电池蔟id查询电池簇的运行状态枚举值数据
                        Optional<ModelFunctionListDto> optional = deviceService.findDeviceFunctionListByIds(Collections.singletonList(deviceBasicInfo.getId())).getData()
                                .values().stream().flatMap(Collection::stream).filter(m -> Objects.equals(FunctionLogoParamVo.CURRENT_STATUS_OF_THE_INVERTER, m.getFunctionLogo()))
                                .findFirst();
                        if (optional.isPresent()) {
                            ModelFunctionListDto modelFunction = optional.get();
                            if (StringUtil.isNotEmpty(modelFunction.getDataObject())) {
                                JSONArray enumArray = JSON.parseObject(modelFunction.getDataObject()).getJSONArray("enumArray");
                                if (CollectionUtils.isNotEmpty(enumArray)) {
                                    for (Object enumObj : enumArray) {
                                        JSONObject enumDto = JSON.parseObject(String.valueOf(enumObj));
                                        if (enumDto != null && StringUtil.isNotEmpty(enumDto.get("id")) && Objects.equals(enumDto.getString("id"), dataValue)) {
                                            result.setRunStateName(enumDto.getString("name"));
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                //获取设备通信状态
                result.setTxStatus(deviceBasicInfo.getTxStatus());

                Map<String, Object> reaMap = deviceBasicInfo.getReaMap();
                //设备型号
                if (!reaMap.isEmpty() && reaMap.containsKey(ReaFieldParamVo.MODEL) && StringUtil.isNotEmpty(reaMap.get(ReaFieldParamVo.MODEL))) {
                    result.setModel(String.valueOf(reaMap.get(ReaFieldParamVo.MODEL)));
                }
                //额定功率
                if (!reaMap.isEmpty() && reaMap.containsKey(ReaFieldParamVo.RATED_POWER) && StringUtil.isNotEmpty(reaMap.get(ReaFieldParamVo.RATED_POWER))) {
                    result.setRatedPower(Double.parseDouble(String.valueOf(reaMap.get(ReaFieldParamVo.RATED_POWER))));
                }
                //生产厂家
                if (!reaMap.isEmpty() && reaMap.containsKey(ReaFieldParamVo.MANUFACTURER_NAME) && StringUtil.isNotEmpty(reaMap.get(ReaFieldParamVo.MANUFACTURER_NAME))) {
                    result.setManufacturerName(String.valueOf(reaMap.get(ReaFieldParamVo.MANUFACTURER_NAME)));
                }

                //获取遥测数据
                List<FunctionDataDto> functionDataList = deviceService.findDeviceFunctionDataList(dataId).getData();
                if (CollectionUtils.isNotEmpty(functionDataList)) {
                    result.setTelemetryDataList(functionDataList.stream().map(functionDataDto -> {
                        TelemetryDataDto telemetryData = new TelemetryDataDto();
                        BeanUtils.copyProperties(functionDataDto, telemetryData);
                        return telemetryData;
                    }).collect(Collectors.toList()));
                }

                //获取遥信数据
                List<DeviceAlarmEventListDto> alarmEventList = deviceService.findDeviceNotRecoveEventList(dataId).getData();
                if (CollectionUtils.isNotEmpty(alarmEventList)) {
                    result.setTelecommuteAlarmList(alarmEventList.stream().map(alarmEvent -> {
                        TelecommuteAlarmDto telecommuteAlarm = new TelecommuteAlarmDto();
                        telecommuteAlarm.setId(alarmEvent.getId());
                        telecommuteAlarm.setEventName(alarmEvent.getEventName());
                        telecommuteAlarm.setEventLevel(alarmEvent.getEventLevel());
                        telecommuteAlarm.setType(alarmEvent.getType());
                        telecommuteAlarm.setAlarmTime(alarmEvent.getCreateTime());
                        return telecommuteAlarm;
                    }).collect(Collectors.toList()));
                }
            }
            return ResponseResult.ok(result);
        } catch (RuntimeException e) {
            log.error("查询光伏逆变器静态数据报错", e);
            return ResponseResult.paramError("查询光伏逆变器静态数据报错,请稍后重试!!");
        }
    }

    @Override
    public ResponseResult<SystemPvWeatherDto> findPvWeatherData(String dataId) {
        try {

            //返回的对象
            SystemPvWeatherDto result = new SystemPvWeatherDto();
            //根据设备id查询光伏气象站数据
            DeviceBasicInfoDto deviceBasicInfo = deviceService.findDeviceBasicInfoByIds(Collections.singletonList(dataId)).getData().get(dataId);
            if (deviceBasicInfo != null) {
                BeanUtils.copyProperties(deviceBasicInfo, result);

                Map<String, Object> reaMap = deviceBasicInfo.getReaMap();
                //获取经纬度
                Map<String, Object> localtionReadwriteMap = Maps.newHashMap();
                if (reaMap.containsKey(SiteFieldParamVo.LOCATION)) {
                    localtionReadwriteMap = JSON.parseObject(JSON.toJSONString(reaMap.get(SiteFieldParamVo.LOCATION)), new TypeReference<Map<String, Object>>() {
                    });
                }
                //经度
                if (!localtionReadwriteMap.isEmpty() && localtionReadwriteMap.containsKey(SiteFieldParamVo.LONGITUDE) && StringUtil.isNotEmpty(localtionReadwriteMap.get(SiteFieldParamVo.LONGITUDE))) {
                    result.setLongitude(Double.parseDouble(localtionReadwriteMap.get(SiteFieldParamVo.LONGITUDE).toString()));
                }
                //纬度
                if (!localtionReadwriteMap.isEmpty() && localtionReadwriteMap.containsKey(SiteFieldParamVo.LATITUDE) && StringUtil.isNotEmpty(localtionReadwriteMap.get(SiteFieldParamVo.LATITUDE))) {
                    result.setLatitude(Double.parseDouble(localtionReadwriteMap.get(SiteFieldParamVo.LATITUDE).toString()));
                }
                //生产厂家
                if (!reaMap.isEmpty() && reaMap.containsKey(ReaFieldParamVo.MANUFACTURER_NAME) && StringUtil.isNotEmpty(reaMap.get(ReaFieldParamVo.MANUFACTURER_NAME))) {
                    result.setManufacturerName(String.valueOf(reaMap.get(ReaFieldParamVo.MANUFACTURER_NAME)));
                }

                //获取设备通信状态
                result.setTxStatus(deviceBasicInfo.getTxStatus());

                //获取设备功能点实时数据
                String pvGhi = FunctionLogoParamVo.PV_GHI;
                String pvBevelGhi = FunctionLogoParamVo.PV_BEVEL_GHI;
                String pvBackPlateTemperature = FunctionLogoParamVo.PV_BACK_PLATE_TEMPERATURE;
                String pvWindDirection = FunctionLogoParamVo.PV_WIND_DIRECTION;
                String pvWindSpeed = FunctionLogoParamVo.PV_WIND_SPEED;
                String functionLogos = String.join(FileUtil.COMMA, pvGhi, pvBevelGhi, pvBackPlateTemperature, pvWindDirection, pvWindSpeed);
                Map<String, RealDataModel> realDataModelMap = deviceService.getDeviceFunctionsRealDataByIds(Collections.singleton(deviceBasicInfo.getId()),
                        functionLogos, 2).getData().get(deviceBasicInfo.getId());
                if (MapUtils.isNotEmpty(realDataModelMap)) {
                    //水平辐射值(W/m²)
                    if (realDataModelMap.containsKey(pvGhi) && StringUtil.isNotEmpty(realDataModelMap.get(pvGhi))) {
                        result.setHorizontalRadiation((Double.parseDouble(String.valueOf(realDataModelMap.get(pvGhi).getDataValue()))));
                    }
                    //倾斜辐射值(W/m²)
                    if (realDataModelMap.containsKey(pvBevelGhi) && StringUtil.isNotEmpty(realDataModelMap.get(pvBevelGhi))) {
                        result.setInclinedRadiation(Double.parseDouble(String.valueOf(realDataModelMap.get(pvBevelGhi).getDataValue())));
                    }
                    //组件背板温度(°C)
                    if (realDataModelMap.containsKey(pvBackPlateTemperature) && StringUtil.isNotEmpty(realDataModelMap.get(pvBackPlateTemperature))) {
                        result.setModuleTemperature(Double.parseDouble(String.valueOf(realDataModelMap.get(pvBackPlateTemperature).getDataValue())));
                    }
                    //风向
                    if (realDataModelMap.containsKey(pvWindDirection) && StringUtil.isNotEmpty(realDataModelMap.get(pvWindDirection))) {
                        result.setWindDirection(Double.parseDouble(String.valueOf(realDataModelMap.get(pvWindDirection).getDataValue())));
                    }
                    //风速(m/s)
                    if (realDataModelMap.containsKey(pvWindSpeed) && StringUtil.isNotEmpty(realDataModelMap.get(pvWindSpeed))) {
                        result.setWindSpeed(Double.parseDouble(String.valueOf(realDataModelMap.get(pvWindSpeed).getDataValue())));
                    }
                }
            }
            return ResponseResult.ok(result);
        } catch (RuntimeException e) {
            log.error("查询光伏气象站静态数据报错", e);
            return ResponseResult.paramError("查询光伏气象站静态数据报错,请稍后重试!!");
        }
    }

    @Override
    public ResponseResult<SystemSeDto> findSystemSeData(String dataId) {
        try {
            //返回的对象
            SystemSeDto result = new SystemSeDto();

            String siteId = deviceService.findSiteIdBySubId(dataId).getData();
            if (StringUtil.isEmpty(siteId)) {
                return ResponseResult.paramError(ResponseResult.PARAM_ERROR);
            }
            //根据站点id查询站点信息
            SiteInfoDto siteInfo = deviceService.findSiteBasicInfoByIds(Collections.singletonList(siteId)).getData().get(siteId);
            if (siteInfo == null) {
                return ResponseResult.paramError(ResponseResult.PARAM_ERROR);
            }
            List<SystemTreeDto> systemTreeList = this.getSystemMonitorData(siteId, dataId, 2, siteInfo);
            if (CollectionUtils.isNotEmpty(systemTreeList)) {
                //查询PCS设备
                List<SystemTreeDto> sePcsList = systemTreeList.stream().filter(s -> StringUtil.isNotEmpty(s.getTypeId())
                        && ("23".equals(s.getTypeId()) || "78".equals(s.getTypeId()))).collect(Collectors.toList());
                if (CollectionUtils.isNotEmpty(sePcsList)) {
                    //PCS数量
                    result.setPcsNum(sePcsList.size());

                    //统计PCS总额定功率
                    List<String> sePcsIds = sePcsList.stream().map(SystemTreeDto::getId).distinct().collect(Collectors.toList());
                    result.setRatedPower(deviceService.findDeviceBasicInfoByIds(sePcsIds).getData().values().stream().mapToDouble(d -> {
                        Map<String, Object> reaMap = d.getReaMap();
                        if (!reaMap.isEmpty() && reaMap.containsKey(ReaFieldParamVo.RATED_POWER) && StringUtil.isNotEmpty(reaMap.get(ReaFieldParamVo.RATED_POWER))) {
                            return Double.parseDouble(String.valueOf(reaMap.get(ReaFieldParamVo.RATED_POWER)));
                        }
                        return 0.0;
                    }).sum());

                    //根据多个设备id，和多个功能点标识查询设备功能点实时数据
                    Map<String, Map<String, RealDataModel>> realDataMap = Maps.newHashMap();
                    String functionLogos = String.join(FileUtil.COMMA, FunctionLogoParamVo.PCS_BATTERY_CHARGE, FunctionLogoParamVo.PCS_BATTERY_DISCHARGE);
                    Stream.of(deviceService.getDeviceFunctionsRealDataByIds(new HashSet<>(sePcsIds), FunctionLogoParamVo.PCS_ACTIVE_POWER, 2).getData(),
                                    deviceService.getDeviceFunctionsRealDataByIds(new HashSet<>(sePcsIds), functionLogos, 1).getData())
                            .filter(Objects::nonNull) // 过滤掉 null
                            .flatMap(map -> map.entrySet().stream())
                            .forEach(entry -> realDataMap.computeIfAbsent(entry.getKey(), k -> new HashMap<>()).putAll(entry.getValue()));
                    if (MapUtils.isNotEmpty(realDataMap)) {
                        List<PvSiteMonitorDto.FunctionRalData> realDataList = realDataMap.entrySet().stream().flatMap(entry -> entry.getValue().entrySet().stream().map(subEntry -> {
                            PvSiteMonitorDto.FunctionRalData functionRalData = new PvSiteMonitorDto.FunctionRalData();
                            functionRalData.setFunctionLogo(subEntry.getKey());
                            functionRalData.setRealData(subEntry.getValue().getDataValue());
                            return functionRalData;
                        })).collect(Collectors.toList());

                        //根据功能点标识分组
                        Map<String, List<PvSiteMonitorDto.FunctionRalData>> functionRalDataMap = realDataList.stream().collect(Collectors.groupingBy(PvSiteMonitorDto.FunctionRalData::getFunctionLogo));
                        //累计充电量
                        if (functionRalDataMap.containsKey(FunctionLogoParamVo.PCS_BATTERY_CHARGE) && CollectionUtils.isNotEmpty(functionRalDataMap.get(FunctionLogoParamVo.PCS_BATTERY_CHARGE))) {
                            result.setSumChargeQt(getToDouble(functionRalDataMap.get(FunctionLogoParamVo.PCS_BATTERY_CHARGE).stream().filter(f -> StringUtil.isNotEmpty(f.getRealData())).mapToDouble(f -> Double.parseDouble(String.valueOf(f.getRealData()))).sum()));
                        }
                        //累计放电量
                        if (functionRalDataMap.containsKey(FunctionLogoParamVo.PCS_BATTERY_DISCHARGE) && CollectionUtils.isNotEmpty(functionRalDataMap.get(FunctionLogoParamVo.PCS_BATTERY_DISCHARGE))) {
                            result.setSumDischargeQt(getToDouble(functionRalDataMap.get(FunctionLogoParamVo.PCS_BATTERY_DISCHARGE).stream().filter(f -> StringUtil.isNotEmpty(f.getRealData())).mapToDouble(f -> Double.parseDouble(String.valueOf(f.getRealData()))).sum()));
                        }
                        //总功率
                        if (functionRalDataMap.containsKey(FunctionLogoParamVo.PCS_ACTIVE_POWER) && CollectionUtils.isNotEmpty(functionRalDataMap.get(FunctionLogoParamVo.PCS_ACTIVE_POWER))) {
                            result.setTotalPower(getToDouble(functionRalDataMap.get(FunctionLogoParamVo.PCS_ACTIVE_POWER).stream().filter(f -> StringUtil.isNotEmpty(f.getRealData())).mapToDouble(f -> Double.parseDouble(String.valueOf(f.getRealData()))).sum()));
                        }
                        //计算昨日系统效率(昨日放电量 / 昨日充电量 * 100)
                        DeviceHistoryQueryVo deviceHistoryQueryVo = new DeviceHistoryQueryVo();
                        deviceHistoryQueryVo.setDeviceIds(new HashSet<>(sePcsIds));
                        deviceHistoryQueryVo.setFunctionLogos(new HashSet<>(Arrays.asList(FunctionLogoParamVo.PCS_BATTERY_CHARGE, FunctionLogoParamVo.PCS_BATTERY_DISCHARGE)));
                        deviceHistoryQueryVo.setStartTime(getStartTimeByQueryType(3));
                        deviceHistoryQueryVo.setEndTime(getEndTimeByQueryType(3));
                        deviceHistoryQueryVo.setTimeInterval("1d");
                        Map<String, Map<String, List<NodeDifHistoryDto>>> deviceHistoryMap = dataService.findNodeDifHistoryListFeign(deviceHistoryQueryVo).getData();
                        if (MapUtils.isNotEmpty(deviceHistoryMap)) {
                            Map<String, Map<String, Double>> chargeQtMap = Maps.newHashMap();
                            List<NodeDifHistoryDto> deviceHistoryList = deviceHistoryMap.values().stream().flatMap(entry -> entry.values().stream()
                                            .flatMap(Collection::stream)).filter(c -> StringUtil.isNotEmpty(c.getFirstDataValue()) && StringUtil.isNotEmpty(c.getLastDataValue()))
                                    .collect(Collectors.toList());
                            deviceHistoryList.stream().collect(Collectors.groupingBy(NodeDifHistoryDto::getFunctionLogo)).forEach((functionLogo, historyList) -> {
                                Map<String, Double> dateQtMap = historyList.stream().collect(Collectors.groupingBy(c -> c.getFirstDateTime().substring(0, 10), Collectors
                                        .summingDouble(c -> DoubleUtil.getObjSub(c.getFirstDataValue(), c.getLastDataValue()))));
                                chargeQtMap.put(functionLogo, dateQtMap);
                            });
                            String dayDate = DateUtil.localDateToStr(LocalDate.now());
                            String lastDayDate = DateUtil.localDateToStr(LocalDate.now());
                            double lastDayChargeQt = 0.0, lastDay30ChargeQt = 0.0, lastDayDischargeQt = 0.0, lastDay30DischargeQt = 0.0;
                            if (MapUtils.isNotEmpty(chargeQtMap) && chargeQtMap.containsKey(FunctionLogoParamVo.PCS_BATTERY_CHARGE)) {
                                Map<String, Double> batteryChargeMap = chargeQtMap.get(FunctionLogoParamVo.PCS_BATTERY_CHARGE);
                                //今日充电量
                                result.setDayChargeQt(getToDouble(batteryChargeMap.getOrDefault(dayDate, 0.0)));
                                //昨日充电量
                                lastDayChargeQt = getToDouble(batteryChargeMap.getOrDefault(lastDayDate, 0.0));
                                //近30天累计充电量
                                lastDay30ChargeQt = getToDouble(batteryChargeMap.values().stream().mapToDouble(qt -> qt).sum());
                            }
                            if (MapUtils.isNotEmpty(chargeQtMap) && chargeQtMap.containsKey(FunctionLogoParamVo.PCS_BATTERY_DISCHARGE)) {
                                Map<String, Double> batteryChargeMap = chargeQtMap.get(FunctionLogoParamVo.PCS_BATTERY_DISCHARGE);
                                //今日充电量
                                result.setDayDischargeQt(getToDouble(batteryChargeMap.getOrDefault(dayDate, 0.0)));
                                //昨日充电量
                                lastDayDischargeQt = getToDouble(batteryChargeMap.getOrDefault(lastDayDate, 0.0));
                                //近30天累计充电量
                                lastDay30DischargeQt = getToDouble(batteryChargeMap.values().stream().mapToDouble(qt -> qt).sum());
                            }
                            //计算昨日系统效率(昨日放电量 / 昨日充电量 * 100)
                            if (lastDayChargeQt != 0.0 && lastDayDischargeQt != 0.0) {
                                result.setLastDayEff(getToDouble(lastDayDischargeQt / lastDayChargeQt * 100));
                            }
                            //计算近30日综合效率(近30日累计放电量 / 近30日累计充电量 * 100)
                            if (lastDay30ChargeQt != 0.0 && lastDay30DischargeQt != 0.0) {
                                result.setLastDay30Eff(getToDouble(lastDay30DischargeQt / lastDay30ChargeQt * 100));
                            }
                        }
                    }

                }

                //获取电池簇设备数据
                List<SystemTreeDto> seBatteryList = systemTreeList.stream().filter(s -> StringUtil.isNotEmpty(s.getTypeId()) && "25".equals(s.getTypeId())).collect(Collectors.toList());
                if (CollectionUtils.isNotEmpty(seBatteryList)) {
                    //电池簇数量
                    result.setBatteryNum(seBatteryList.size());

                    //根据多个电池簇id查询电池包数量和电芯数量
                    Set<String> seBatteryIds = seBatteryList.stream().map(SystemTreeDto::getId).collect(Collectors.toSet());
                    Map<String, DeviceBasicInfoDto> seBatteryMap = deviceService.findDeviceBasicInfoByIds(new ArrayList<>(seBatteryIds)).getData();
                    //电池包数量
                    result.setBatteryPackNum(seBatteryMap.values().stream().mapToInt(d -> {
                        Map<String, Object> reaMap = d.getReaMap();
                        if (!reaMap.isEmpty() && reaMap.containsKey(ReaFieldParamVo.PACK_NUM) && StringUtil.isNotEmpty(reaMap.get(ReaFieldParamVo.PACK_NUM))) {
                            return Integer.parseInt(String.valueOf(reaMap.get(ReaFieldParamVo.PACK_NUM)));
                        }
                        return 0;
                    }).sum());
                    //电芯数量
                    result.setBatteryCellNum(seBatteryMap.values().stream().mapToInt(d -> {
                        Map<String, Object> reaMap = d.getReaMap();
                        if (!reaMap.isEmpty() && reaMap.containsKey(ReaFieldParamVo.CELL_NUM) && StringUtil.isNotEmpty(reaMap.get(ReaFieldParamVo.CELL_NUM))) {
                            return Integer.parseInt(String.valueOf(reaMap.get(ReaFieldParamVo.CELL_NUM)));
                        }
                        return 0;
                    }).sum());


                    //获取电池簇总额定容量
                    Map<String, Double> ratedCapMap = seBatteryMap.values().stream().collect(Collectors.toMap(DeviceBasicInfoDto::getId, d -> {
                        Map<String, Object> reaMap = d.getReaMap();
                        if (!reaMap.isEmpty() && reaMap.containsKey(ReaFieldParamVo.RATED_CAP) && StringUtil.isNotEmpty(reaMap.get(ReaFieldParamVo.RATED_CAP))) {
                            return Double.parseDouble(String.valueOf(reaMap.get(ReaFieldParamVo.RATED_CAP)));
                        }
                        return 0.0;
                    }, (k1, k2) -> k1));
                    result.setCapacity(ratedCapMap.values().stream().mapToDouble(r -> r).sum());

                    //计算充放电倍率(pcs总额定功率 / 电池总额定容量)
                    if (StringUtil.isNotEmpty(result.getRatedPower()) && result.getCapacity() > 0) {
                        result.setChargeMagnification(getToDouble(result.getRatedPower() / result.getCapacity()));
                    }

                    //整个系统SOC 查询储能电池簇设备的今日SOC 可放电量：电池容量 * 电池SOC / 100
                    Map<String, Map<String, RealDataModel>> deviceRealMap = deviceService.getDeviceFunctionsRealDataByIds(seBatteryIds,
                            FunctionLogoParamVo.BATTERY_TOTAL_SOC, 2).getData();
                    double chargeQt = seBatteryIds.stream().mapToDouble(seBatteryId -> {
                        //额定容量
                        Double ratedCap = ratedCapMap.get(seBatteryId);
                        RealDataModel realDataModel = deviceRealMap.get(seBatteryId).get(FunctionLogoParamVo.BATTERY_TOTAL_SOC);
                        if (realDataModel != null && StringUtil.isNotEmpty(realDataModel.getDataValue())) {
                            return ratedCap * (Double.parseDouble(String.valueOf(realDataModel.getDataValue())) / 100);
                        }
                        return 0.0;
                    }).sum();
                    if (chargeQt != 0.0 && StringUtil.isNotEmpty(result.getCapacity()) && result.getCapacity() != 0) {
                        result.setSoc(getToDouble(chargeQt / result.getCapacity() * 100));
                    }
                }
                //累计循环次数(累计放电量 / 装机容量)
                if (StringUtil.isNotEmpty(result.getSumDischargeQt()) && StringUtil.isNotEmpty(result.getCapacity()) && result.getCapacity() != 0) {
                    result.setSumChargeCycleNum(DoubleUtil.getToDouble(result.getSumDischargeQt() / result.getCapacity()));
                }
            }
            return ResponseResult.ok(result);
        } catch (RuntimeException e) {
            log.error("查询储能系统静态数据报错", e);
            return ResponseResult.paramError("查询储能系统静态数据报错,请稍后重试!!");
        }
    }

    @Override
    public ResponseResult<SystemSePcsDto> findSePcsData(String dataId) {
        try {
            //返回的对象
            SystemSePcsDto result = new SystemSePcsDto();
            //根据设备id查询储能PCS数据
            DeviceBasicInfoDto deviceBasicInfo = deviceService.findDeviceBasicInfoByIds(Collections.singletonList(dataId)).getData().get(dataId);
            if (deviceBasicInfo != null) {
                BeanUtils.copyProperties(deviceBasicInfo, result);
                //获取设备功能点实时数据
                Map<String, RealDataModel> realDataModelMap = deviceService.getDeviceFunctionsRealDataByIds(Collections.singleton(deviceBasicInfo.getId()),
                        FunctionLogoParamVo.PCS_OPERATIVE_MODE, 2).getData().get(deviceBasicInfo.getId());
                if (MapUtils.isNotEmpty(realDataModelMap)) {
                    //运行状态 0-停机 1-待机 2-运行 3-故障
                    if (realDataModelMap.containsKey(FunctionLogoParamVo.PCS_OPERATIVE_MODE) && StringUtil.isNotEmpty(realDataModelMap.get(FunctionLogoParamVo.PCS_OPERATIVE_MODE).getDataValue())) {
                        String dataValue = String.valueOf(realDataModelMap.get(FunctionLogoParamVo.PCS_OPERATIVE_MODE).getDataValue());
                        result.setRunState((int) Double.parseDouble(dataValue));
                        //根据电池蔟id查询电池簇的运行状态枚举值数据
                        Optional<ModelFunctionListDto> optional = deviceService.findDeviceFunctionListByIds(Collections.singletonList(deviceBasicInfo.getId())).getData()
                                .values().stream().flatMap(Collection::stream).filter(m -> Objects.equals(FunctionLogoParamVo.PCS_OPERATIVE_MODE, m.getFunctionLogo()))
                                .findFirst();
                        if (optional.isPresent()) {
                            ModelFunctionListDto modelFunction = optional.get();
                            if (StringUtil.isNotEmpty(modelFunction.getDataObject())) {
                                JSONArray enumArray = JSON.parseObject(modelFunction.getDataObject()).getJSONArray("enumArray");
                                if (CollectionUtils.isNotEmpty(enumArray)) {
                                    for (Object enumObj : enumArray) {
                                        JSONObject enumDto = JSON.parseObject(String.valueOf(enumObj));
                                        if (enumDto != null && StringUtil.isNotEmpty(enumDto.get("id")) && Objects.equals(enumDto.getString("id"), dataValue)) {
                                            result.setRunStateName(enumDto.getString("name"));
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                //获取设备通信状态
                result.setTxStatus(deviceBasicInfo.getTxStatus());

                Map<String, Object> reaMap = deviceBasicInfo.getReaMap();
                //设备型号
                if (!reaMap.isEmpty() && reaMap.containsKey(ReaFieldParamVo.MODEL) && StringUtil.isNotEmpty(reaMap.get(ReaFieldParamVo.MODEL))) {
                    result.setModel(String.valueOf(reaMap.get(ReaFieldParamVo.MODEL)));
                }
                //额定功率
                if (!reaMap.isEmpty() && reaMap.containsKey(ReaFieldParamVo.RATED_POWER) && StringUtil.isNotEmpty(reaMap.get(ReaFieldParamVo.RATED_POWER))) {
                    result.setRatedPower(Double.parseDouble(String.valueOf(reaMap.get(ReaFieldParamVo.RATED_POWER))));
                }
                //生产厂家
                if (!reaMap.isEmpty() && reaMap.containsKey(ReaFieldParamVo.MANUFACTURER_NAME) && StringUtil.isNotEmpty(reaMap.get(ReaFieldParamVo.MANUFACTURER_NAME))) {
                    result.setManufacturerName(String.valueOf(reaMap.get(ReaFieldParamVo.MANUFACTURER_NAME)));
                }

                //获取遥测数据
                List<FunctionDataDto> functionDataList = deviceService.findDeviceFunctionDataList(dataId).getData();
                if (CollectionUtils.isNotEmpty(functionDataList)) {
                    result.setTelemetryDataList(functionDataList.stream().map(functionDataDto -> {
                        TelemetryDataDto telemetryData = new TelemetryDataDto();
                        BeanUtils.copyProperties(functionDataDto, telemetryData);
                        return telemetryData;
                    }).collect(Collectors.toList()));
                }

                //获取遥信数据
                List<DeviceAlarmEventListDto> alarmEventList = deviceService.findDeviceNotRecoveEventList(dataId).getData();
                if (CollectionUtils.isNotEmpty(alarmEventList)) {
                    result.setTelecommuteAlarmList(alarmEventList.stream().map(alarmEvent -> {
                        TelecommuteAlarmDto telecommuteAlarm = new TelecommuteAlarmDto();
                        telecommuteAlarm.setId(alarmEvent.getId());
                        telecommuteAlarm.setEventName(alarmEvent.getEventName());
                        telecommuteAlarm.setEventLevel(alarmEvent.getEventLevel());
                        telecommuteAlarm.setType(alarmEvent.getType());
                        telecommuteAlarm.setAlarmTime(alarmEvent.getCreateTime());
                        return telecommuteAlarm;
                    }).collect(Collectors.toList()));
                }
            }
            return ResponseResult.ok(result);
        } catch (RuntimeException e) {
            log.error("查询储能PCS静态数据报错", e);
            return ResponseResult.paramError("查询储能PCS静态数据报错,请稍后重试!!");
        }
    }

    @Override
    public ResponseResult<SystemSeBatteryDto> findSeBatteryData(String dataId) {
        try {
            //返回的对象
            SystemSeBatteryDto result = new SystemSeBatteryDto();
            //根据设备id查询储能电池簇数据
            DeviceBasicInfoDto deviceBasicInfo = deviceService.findDeviceBasicInfoByIds(Collections.singletonList(dataId)).getData().get(dataId);
            if (deviceBasicInfo != null) {
                BeanUtils.copyProperties(deviceBasicInfo, result);
                //获取设备功能点实时数据
                String batteryRunState = FunctionLogoParamVo.BATTERY_RUN_STATE;
                String maxCellVoltage = FunctionLogoParamVo.MAX_CELL_VOLTAGE;
                String minCellVoltage = FunctionLogoParamVo.MIN_CELL_VOLTAGE;
                String maxCellTemperature = FunctionLogoParamVo.MAX_CELL_TEMPERATURE;
                String minCellTemperature = FunctionLogoParamVo.MIN_CELL_TEMPERATURE;
                String functionLogos = String.join(FileUtil.COMMA, batteryRunState, maxCellVoltage, minCellVoltage, maxCellTemperature, minCellTemperature);
                Map<String, RealDataModel> realDataMap = deviceService.getDeviceFunctionsRealDataByIds(Collections.singleton(deviceBasicInfo.getId()),
                        functionLogos, 2).getData().get(deviceBasicInfo.getId());
                if (MapUtils.isNotEmpty(realDataMap)) {
                    //运行状态 0-待机 1-禁充 2-禁放 3-故障 4-告警 5-充电 6-放电
                    if (realDataMap.containsKey(batteryRunState) && StringUtil.isNotEmpty(realDataMap.get(batteryRunState).getDataValue())) {
                        String dataValue = String.valueOf(realDataMap.get(batteryRunState).getDataValue());
                        result.setRunState((int) Double.parseDouble(dataValue));
                        //根据电池蔟id查询电池簇的运行状态枚举值数据
                        Optional<ModelFunctionListDto> optional = deviceService.findDeviceFunctionListByIds(Collections.singletonList(deviceBasicInfo.getId())).getData()
                                .values().stream().flatMap(Collection::stream).filter(m -> Objects.equals(FunctionLogoParamVo.BATTERY_RUN_STATE, m.getFunctionLogo()))
                                .findFirst();
                        if (optional.isPresent()) {
                            ModelFunctionListDto modelFunction = optional.get();
                            if (StringUtil.isNotEmpty(modelFunction.getDataObject())) {
                                JSONArray enumArray = JSON.parseObject(modelFunction.getDataObject()).getJSONArray("enumArray");
                                if (CollectionUtils.isNotEmpty(enumArray)) {
                                    for (Object enumObj : enumArray) {
                                        JSONObject enumDto = JSON.parseObject(String.valueOf(enumObj));
                                        if (enumDto != null && StringUtil.isNotEmpty(enumDto.get("id")) && Objects.equals(enumDto.getString("id"), dataValue)) {
                                            result.setRunStateName(enumDto.getString("name"));
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    }
                    //电芯电压一致性,电芯电压极差(mV)
                    if (realDataMap.containsKey(maxCellVoltage) && realDataMap.containsKey(minCellVoltage)) {
                        RealDataModel maxRealData = realDataMap.get(maxCellVoltage);
                        RealDataModel minRealData = realDataMap.get(minCellVoltage);
                        if (StringUtil.isNotEmpty(maxRealData.getDataValue()) && StringUtil.isNotEmpty(minRealData.getDataValue())) {
                            double maxVoltage = Double.parseDouble(String.valueOf(maxRealData.getDataValue()));
                            double minVoltage = Double.parseDouble(String.valueOf(minRealData.getDataValue()));
                            //电芯电压极差(mV)(最高单体电压-最低单体电压)
                            result.setCellVoltageDifference(getToDouble(maxVoltage - minVoltage));
                            //电芯电压一致性 电芯电压极差(mV)<=100mv ? 正常:异常
                            result.setCellVoltageConsistency(getToDouble(maxVoltage - minVoltage) <= 100 ? 1 : 2);
                        }
                    }
                    //电芯温度一致性,电芯温度极差(℃)
                    if (realDataMap.containsKey(maxCellTemperature) && realDataMap.containsKey(minCellTemperature)) {
                        RealDataModel maxRealData = realDataMap.get(maxCellTemperature);
                        RealDataModel minRealData = realDataMap.get(minCellTemperature);
                        if (StringUtil.isNotEmpty(maxRealData.getDataValue()) && StringUtil.isNotEmpty(minRealData.getDataValue())) {
                            double maxTemperature = Double.parseDouble(String.valueOf(maxRealData.getDataValue()));
                            double minTemperature = Double.parseDouble(String.valueOf(minRealData.getDataValue()));
                            //电芯温度极差(℃)(最高单体温度-最低单体温度)
                            result.setCellTemperatureDifference(getToDouble(maxTemperature - minTemperature));
                            //电芯温度一致性 温度极差<=5℃? 正常:异常
                            result.setCellTemperatureConsistency(getToDouble(maxTemperature - minTemperature) <= 5 ? 1 : 2);
                        }
                    }
                }

                //获取设备通信状态
                result.setTxStatus(deviceBasicInfo.getTxStatus());

                Map<String, Object> reaMap = deviceBasicInfo.getReaMap();
                //额定容量
                if (!reaMap.isEmpty() && reaMap.containsKey(ReaFieldParamVo.RATED_CAP) && StringUtil.isNotEmpty(reaMap.get(ReaFieldParamVo.RATED_CAP))) {
                    result.setRatedCap(Double.parseDouble(String.valueOf(reaMap.get(ReaFieldParamVo.RATED_CAP))));
                }
                //设备型号
                if (!reaMap.isEmpty() && reaMap.containsKey(ReaFieldParamVo.MODEL) && StringUtil.isNotEmpty(reaMap.get(ReaFieldParamVo.MODEL))) {
                    result.setModel(String.valueOf(reaMap.get(ReaFieldParamVo.MODEL)));
                }
                //生产厂家
                if (!reaMap.isEmpty() && reaMap.containsKey(ReaFieldParamVo.MANUFACTURER_NAME) && StringUtil.isNotEmpty(reaMap.get(ReaFieldParamVo.MANUFACTURER_NAME))) {
                    result.setManufacturerName(String.valueOf(reaMap.get(ReaFieldParamVo.MANUFACTURER_NAME)));
                }

                //获取遥测数据
                List<FunctionDataDto> functionDataList = deviceService.findDeviceFunctionDataList(dataId).getData();
                if (CollectionUtils.isNotEmpty(functionDataList)) {
                    result.setTelemetryDataList(functionDataList.stream().map(functionDataDto -> {
                        TelemetryDataDto telemetryData = new TelemetryDataDto();
                        BeanUtils.copyProperties(functionDataDto, telemetryData);
                        return telemetryData;
                    }).collect(Collectors.toList()));
                }

                //获取遥信数据
                List<DeviceAlarmEventListDto> alarmEventList = deviceService.findDeviceNotRecoveEventList(dataId).getData();
                if (CollectionUtils.isNotEmpty(alarmEventList)) {
                    result.setTelecommuteAlarmList(alarmEventList.stream().map(alarmEvent -> {
                        TelecommuteAlarmDto telecommuteAlarm = new TelecommuteAlarmDto();
                        telecommuteAlarm.setId(alarmEvent.getId());
                        telecommuteAlarm.setEventName(alarmEvent.getEventName());
                        telecommuteAlarm.setEventLevel(alarmEvent.getEventLevel());
                        telecommuteAlarm.setType(alarmEvent.getType());
                        telecommuteAlarm.setAlarmTime(alarmEvent.getCreateTime());
                        return telecommuteAlarm;
                    }).collect(Collectors.toList()));
                }
            }
            return ResponseResult.ok(result);
        } catch (RuntimeException e) {
            log.error("查询储能电池簇静态数据报错", e);
            return ResponseResult.paramError("查询储能电池簇静态数据报错,请稍后重试!!");
        }
    }

    @Override
    public ResponseResult<SystemSeAuxEquipmentDto> findSeAuxEquipmentData(String dataId) {
        try {
            //返回的对象
            SystemSeAuxEquipmentDto result = new SystemSeAuxEquipmentDto();
            //根据设备id查询储能辅助设备数据
            DeviceBasicInfoDto deviceBasicInfo = deviceService.findDeviceBasicInfoByIds(Collections.singletonList(dataId)).getData().get(dataId);
            if (deviceBasicInfo != null) {
                BeanUtils.copyProperties(deviceBasicInfo, result);

                //获取设备通信状态
                result.setTxStatus(deviceBasicInfo.getTxStatus());

                //获取设备功能点实时数据
                String coolingState = FunctionLogoParamVo.COOLING_STATE;
                String heatingState = FunctionLogoParamVo.HEATING_STATE;
                String internalFanState = FunctionLogoParamVo.INTERNALFAN_STATE;
                String externalFanState = FunctionLogoParamVo.EXTERNALFAN_STATE;
                String functionLogos = String.join(FileUtil.COMMA, coolingState, heatingState, internalFanState, externalFanState);
                Map<String, RealDataModel> realDataMap = deviceService.getDeviceFunctionsRealDataByIds(Collections.singleton(deviceBasicInfo.getId()),
                        functionLogos, 2).getData().get(deviceBasicInfo.getId());
                if (MapUtils.isNotEmpty(realDataMap)) {
                    //制冷状态 0-停止 1-开启
                    if (realDataMap.containsKey(coolingState) && StringUtil.isNotEmpty(realDataMap.get(coolingState).getDataValue())) {
                        result.setCoolingState(Integer.parseInt(String.valueOf(realDataMap.get(coolingState).getDataValue())));
                    }
                    //加热器状态 0-停止 1-开启
                    if (realDataMap.containsKey(heatingState) && StringUtil.isNotEmpty(realDataMap.get(heatingState).getDataValue())) {
                        result.setHeatingState(Integer.parseInt(String.valueOf(realDataMap.get(heatingState).getDataValue())));
                    }
                    //内风机状态 0-停止 1-开启
                    if (realDataMap.containsKey(internalFanState) && StringUtil.isNotEmpty(realDataMap.get(internalFanState).getDataValue())) {
                        result.setInternalState(Integer.parseInt(String.valueOf(realDataMap.get(internalFanState).getDataValue())));
                    }
                    //外风机状态 0-停止 1-开启
                    if (realDataMap.containsKey(externalFanState) && StringUtil.isNotEmpty(realDataMap.get(externalFanState).getDataValue())) {
                        result.setExternalState(Integer.parseInt(String.valueOf(realDataMap.get(externalFanState).getDataValue())));
                    }
                }

                Map<String, Object> reaMap = deviceBasicInfo.getReaMap();

                //设备型号
                if (!reaMap.isEmpty() && reaMap.containsKey(ReaFieldParamVo.MODEL) && StringUtil.isNotEmpty(reaMap.get(ReaFieldParamVo.MODEL))) {
                    result.setModel(String.valueOf(reaMap.get(ReaFieldParamVo.MODEL)));
                }
                //生产厂家
                if (!reaMap.isEmpty() && reaMap.containsKey(ReaFieldParamVo.MANUFACTURER_NAME) && StringUtil.isNotEmpty(reaMap.get(ReaFieldParamVo.MANUFACTURER_NAME))) {
                    result.setManufacturerName(String.valueOf(reaMap.get(ReaFieldParamVo.MANUFACTURER_NAME)));
                }

                //获取遥测数据
                List<FunctionDataDto> functionDataList = deviceService.findDeviceFunctionDataList(dataId).getData();
                if (CollectionUtils.isNotEmpty(functionDataList)) {
                    result.setTelemetryDataList(functionDataList.stream().map(functionDataDto -> {
                        TelemetryDataDto telemetryData = new TelemetryDataDto();
                        BeanUtils.copyProperties(functionDataDto, telemetryData);
                        return telemetryData;
                    }).collect(Collectors.toList()));
                }

                //获取遥信告警数据
                List<DeviceAlarmEventListDto> alarmEventList = deviceService.findDeviceNotRecoveEventList(dataId).getData();
                if (CollectionUtils.isNotEmpty(alarmEventList)) {
                    result.setTelecommuteAlarmList(alarmEventList.stream().map(alarmEvent -> {
                        TelecommuteAlarmDto telecommuteAlarm = new TelecommuteAlarmDto();
                        telecommuteAlarm.setId(alarmEvent.getId());
                        telecommuteAlarm.setEventName(alarmEvent.getEventName());
                        telecommuteAlarm.setEventLevel(alarmEvent.getEventLevel());
                        telecommuteAlarm.setType(alarmEvent.getType());
                        telecommuteAlarm.setAlarmTime(alarmEvent.getCreateTime());
                        return telecommuteAlarm;
                    }).collect(Collectors.toList()));
                }
            }
            return ResponseResult.ok(result);
        } catch (RuntimeException e) {
            log.error("查询储能辅助设备静态数据报错", e);
            return ResponseResult.paramError("查询储能辅助设备静态数据报错,请稍后重试!!");
        }
    }

    @Override
    public ResponseResult<SystemPileDto> findSystemPileData(String dataId) {
        try {
            //返回的对象
            SystemPileDto result = new SystemPileDto();
            String siteId = deviceService.findSiteIdBySubId(dataId).getData();
            if (StringUtil.isEmpty(siteId)) {
                return ResponseResult.paramError(ResponseResult.PARAM_ERROR);
            }
            //根据站点id查询站点信息
            SiteInfoDto siteInfo = deviceService.findSiteBasicInfoByIds(Collections.singletonList(siteId)).getData().get(siteId);

            //定义多个电桩类型
            List<String> typeIds = Arrays.asList("28", "29", "30", "79");
            List<SystemTreeDto> systemTreeList = this.getSystemMonitorData(siteId, dataId, 3, siteInfo).stream()
                    .filter(s -> StringUtil.isNotEmpty(s.getTypeId()) && typeIds.contains(s.getTypeId())).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(systemTreeList)) {
                List<String> pileIds = systemTreeList.stream().map(SystemTreeDto::getId).distinct().collect(Collectors.toList());
                Collection<DeviceBasicInfoDto> deviceInfoList = deviceService.findDeviceBasicInfoByIds(pileIds).getData().values();

                //电桩装机容量
                if (siteInfo != null && CollectionUtils.isNotEmpty(siteInfo.getSiteScenarioTypeDtos())) {
                    result.setCapacity(siteInfo.getSiteScenarioTypeDtos().stream().filter(s -> StringUtil.isNotEmpty(s.getScenarioType())
                            && s.getScenarioType() == 3).mapToDouble(scenarioType -> {
                        if (StringUtil.isNotEmpty(scenarioType.getReadwriteObject())) {
                            JSONObject readwriteMap = JSON.parseObject(scenarioType.getReadwriteObject());
                            if (readwriteMap.containsKey(SiteFieldParamVo.CAPACITY) && StringUtil.isNotEmpty(readwriteMap.getString(SiteFieldParamVo.CAPACITY))) {
                                return readwriteMap.getDoubleValue(SiteFieldParamVo.CAPACITY);
                            }
                        }
                        return 0.0;
                    }).sum());

                }
//                result.setCapacity(deviceInfoList.stream().mapToDouble(d -> {
//                    Map<String, Object> reaMap = d.getReaMap();
//                    if (!reaMap.isEmpty() && reaMap.containsKey(ReaFieldParamVo.RATED_POWER) && StringUtil.isNotEmpty(reaMap.get(ReaFieldParamVo.RATED_POWER))) {
//                        return Double.parseDouble(String.valueOf(reaMap.get(ReaFieldParamVo.RATED_POWER)));
//                    }
//                    return 0.0;
//                }).sum());

                result.setPileNum(deviceInfoList.size());

                //获取总功率
                result.setTotalPower(DoubleUtil.getToDouble(deviceService.getDeviceFunctionsRealDataByIds(new HashSet<>(pileIds),
                        FunctionLogoParamVo.PILE_POWER, 2).getData().values().stream().flatMap(d ->
                        new ArrayList<>(d.values()).stream()).mapToDouble(c -> DoubleUtil.objToDouble(c.getDataValue())).sum()));

                //获取充电桩电枪数量
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
                List<String> dcTypeIds = Arrays.asList("29", "30", "79");
                int superPileNum = 0;
                List<DeviceBasicInfoDto> dcPileDeviceList = deviceInfoList.stream().filter(d -> StringUtil.isNotEmpty(d.getTypeId())
                        && dcTypeIds.contains(d.getTypeId())).collect(Collectors.toList());
                if (CollectionUtils.isNotEmpty(dcPileDeviceList)) {
                    //获取直流和V2G充电桩数量
                    result.setDcPileNum(dcPileDeviceList.size());
                    //获取直流和V2G充电枪数量和超充枪数量
                    superPileNum = (int) dcPileDeviceList.stream().filter(d -> Objects.equals(d.getTypeId(), "79")).count();
                    result.setDcGunNum(dcPileDeviceList.stream().mapToInt(device -> {
                        if (deviceGunMap.containsKey(device.getId())) {
                            return deviceGunMap.get(device.getId()).size();
                        }
                        return 0;
                    }).sum() + superPileNum);
                }

                //获取昨日开始时间
                String lastDayStartTime = getStartTimeByQueryType(1);
                //获取昨日结束时间
                String lastDayEndTime = getEndTimeByQueryType(1);

                //获取今日开始时间
                String dayStartTime = getStartTimeByQueryType(0);
                //获取今日结束时间
                String dayEndTime = getEndTimeByQueryType(0);

                //根据多个电桩编码查询充放电订单信息
                List<OrderRecordEntity> orderRecordList = orderRecordDao.findAllByPileCodeIn(deviceInfoList.stream().map(DeviceBasicInfoDto::getDeviceNumber).filter(StringUtil::isNotEmpty).collect(Collectors.toList()));
                if (CollectionUtils.isNotEmpty(orderRecordList)) {

                    //计算今日时间利用率（超充枪数量不做统计）
                    int totalGunNum = 0;
                    if (StringUtil.isNotEmpty(result.getAcPileNum()) && StringUtil.isNotEmpty(result.getDcPileNum())) {
                        totalGunNum = result.getAcPileNum() + result.getDcPileNum() - superPileNum;
                    } else if (StringUtil.isNotEmpty(result.getAcPileNum())) {
                        totalGunNum = result.getAcPileNum() - superPileNum;
                    } else if (StringUtil.isNotEmpty(result.getDcPileNum())) {
                        totalGunNum = result.getDcPileNum() - superPileNum;
                    }

                    List<OrderRecordEntity> dayOrderRecordList = Lists.newArrayList();

                    //充电订单
                    List<OrderRecordEntity> chargeOrderRecordList = orderRecordList.stream().filter(o -> StringUtil.isNotEmpty(o.getRunMode()) && o.getRunMode() == 0).collect(Collectors.toList());
                    if (CollectionUtils.isNotEmpty(chargeOrderRecordList)) {
                        //累计充电量
                        result.setSumChargeQt(getToDouble(chargeOrderRecordList.stream().filter(o -> StringUtil.isNotEmpty(o.getTotalQt())).mapToDouble(OrderRecordEntity::getTotalQt).sum()));

                        //今日订单数据
                        List<OrderRecordEntity> todayChargeOrderRecordList = chargeOrderRecordList.stream().filter(o -> StringUtil.isNotEmpty(o.getEndTime()) && strToLocalDateTime(o.getEndTime()).isAfter(strToLocalDateTime(dayStartTime)) && strToLocalDateTime(o.getEndTime()).isBefore(strToLocalDateTime(dayEndTime))).collect(Collectors.toList());
                        if (CollectionUtils.isNotEmpty(todayChargeOrderRecordList)) {
                            dayOrderRecordList.addAll(todayChargeOrderRecordList);
                            //今日充电量
                            Double dayChargeQt = getToDouble(todayChargeOrderRecordList.stream().filter(o -> StringUtil.isNotEmpty(o.getTotalQt())).mapToDouble(OrderRecordEntity::getTotalQt).sum());
                            result.setDayChargeQt(dayChargeQt);
                            if (dayChargeQt != 0 && totalGunNum != 0) {
                                //今日枪均电量(度) 今日充电量 / 平均枪数
                                result.setDayAvgChargeQt(getToDouble(dayChargeQt / totalGunNum));
                            }
                        }

                        //昨日订单数据
                        List<OrderRecordEntity> lastDayChargeOrderRecordList = chargeOrderRecordList.stream().filter(o -> StringUtil.isNotEmpty(o.getEndTime()) && strToLocalDateTime(o.getEndTime()).isAfter(strToLocalDateTime(lastDayStartTime)) && strToLocalDateTime(o.getEndTime()).isBefore(strToLocalDateTime(lastDayEndTime))).collect(Collectors.toList());
                        if (CollectionUtils.isNotEmpty(lastDayChargeOrderRecordList)) {
                            //昨日充电量
                            result.setLastDayChargeQt(getToDouble(lastDayChargeOrderRecordList.stream().filter(o -> StringUtil.isNotEmpty(o.getTotalQt())).mapToDouble(OrderRecordEntity::getTotalQt).sum()));
                        }
                    }

                    //放电订单
                    List<OrderRecordEntity> dischargeOrderRecordList = orderRecordList.stream().filter(o -> StringUtil.isNotEmpty(o.getRunMode()) && (o.getRunMode() == 1 || o.getRunMode() == 2)).collect(Collectors.toList());
                    if (CollectionUtils.isNotEmpty(dischargeOrderRecordList)) {

                        //累计放电量
                        result.setSumV2gQt(getToDouble(dischargeOrderRecordList.stream().filter(o -> StringUtil.isNotEmpty(o.getTotalQt())).mapToDouble(OrderRecordEntity::getTotalQt).sum()));

                        //今日订单数据
                        List<OrderRecordEntity> todayChargeOrderRecordList = dischargeOrderRecordList.stream().filter(o -> StringUtil.isNotEmpty(o.getEndTime()) && strToLocalDateTime(o.getEndTime()).isAfter(strToLocalDateTime(dayStartTime)) && strToLocalDateTime(o.getEndTime()).isBefore(strToLocalDateTime(dayEndTime))).collect(Collectors.toList());
                        if (CollectionUtils.isNotEmpty(todayChargeOrderRecordList)) {
                            dayOrderRecordList.addAll(todayChargeOrderRecordList);
                            //今日放电量
                            result.setDayV2gQt(getToDouble(todayChargeOrderRecordList.stream().filter(o -> StringUtil.isNotEmpty(o.getTotalQt())).mapToDouble(OrderRecordEntity::getTotalQt).sum()));
                        }

                        //昨日订单数据
                        List<OrderRecordEntity> lastDayChargeOrderRecordList = dischargeOrderRecordList.stream().filter(o -> StringUtil.isNotEmpty(o.getEndTime()) && strToLocalDateTime(o.getEndTime()).isAfter(strToLocalDateTime(lastDayStartTime)) && strToLocalDateTime(o.getEndTime()).isBefore(strToLocalDateTime(lastDayEndTime))).collect(Collectors.toList());
                        if (CollectionUtils.isNotEmpty(lastDayChargeOrderRecordList)) {
                            //昨日放电量
                            result.setLastDayV2gQt(getToDouble(lastDayChargeOrderRecordList.stream().filter(o -> StringUtil.isNotEmpty(o.getTotalQt())).mapToDouble(OrderRecordEntity::getTotalQt).sum()));
                        }
                    }

                    //计算今日时间利用率
                    //今日时间利用率(%) 今日订单(充电时长+放电时长)/总枪数*24h(筛选日期内每天的数据求和)
                    double chargeTime = dayOrderRecordList.stream().filter(s -> StringUtil.isNotEmpty(s.getStartTime()) && StringUtil.isNotEmpty(s.getEndTime()))
                            .mapToDouble(c -> {
                                LocalDateTime startLocalTime = strToLocalDateTime(c.getStartTime());
                                LocalDateTime endLocalTime = strToLocalDateTime(c.getEndTime());
                                return (double) compareDiffBetweenMinutes(startLocalTime, endLocalTime) / 60;
                            }).sum();
                    if (chargeTime != 0.0 && totalGunNum != 0) {
                        result.setDayTimeRatio(getToDouble(chargeTime / (totalGunNum * 24) * 100));
                    }
                }
            }
            return ResponseResult.ok(result);
        } catch (RuntimeException e) {
            log.error("查询电桩系统静态数据报错", e);
            return ResponseResult.paramError("查询电桩系统静态数据报错,请稍后重试!!");
        }
    }

    @Override
    public ResponseResult<PileDto> findPileData(String dataId) {
        try {
            //返回的对象
            PileDto result = new PileDto();
            //根据设备id查询电桩数据
            DeviceBasicInfoDto deviceBasicInfo = deviceService.findDeviceBasicInfoByIds(Collections.singletonList(dataId)).getData().get(dataId);
            if (deviceBasicInfo != null) {
                BeanUtils.copyProperties(deviceBasicInfo, result);
                Map<String, Object> reaMap = deviceBasicInfo.getReaMap();
                //设备型号
                if (!reaMap.isEmpty() && reaMap.containsKey(ReaFieldParamVo.MODEL) && StringUtil.isNotEmpty(reaMap.get(ReaFieldParamVo.MODEL))) {
                    result.setModel(String.valueOf(reaMap.get(ReaFieldParamVo.MODEL)));
                }
                //额定功率
                if (!reaMap.isEmpty() && reaMap.containsKey(ReaFieldParamVo.RATED_POWER) && StringUtil.isNotEmpty(reaMap.get(ReaFieldParamVo.RATED_POWER))) {
                    result.setRatedPower(Double.parseDouble(String.valueOf(reaMap.get(ReaFieldParamVo.RATED_POWER))));
                }
                //生产厂家
                if (!reaMap.isEmpty() && reaMap.containsKey(ReaFieldParamVo.MANUFACTURER_NAME) && StringUtil.isNotEmpty(reaMap.get(ReaFieldParamVo.MANUFACTURER_NAME))) {
                    result.setManufacturerName(String.valueOf(reaMap.get(ReaFieldParamVo.MANUFACTURER_NAME)));
                }

                //获取设备通信状态
                result.setTxStatus(deviceBasicInfo.getTxStatus());

                //获取电桩实时状态
                PileRealModel pileRealModel = RedisGeneralUtil.getPileRealModel(deviceBasicInfo.getDeviceNumber());
                if (pileRealModel != null) {
                    //电桩工作状态 -1-未知 1-在线 2-维护 3-故障 88-离线
                    if (StringUtil.isNotEmpty(pileRealModel.getWorkStatus())) {
                        result.setWorkStatus(pileRealModel.getWorkStatus());
                    }
                    //有功功率
                    if (StringUtil.isNotEmpty(pileRealModel.getTotalPower())) {
                        result.setActivePower(getToDouble(pileRealModel.getTotalPower()));
                    }
                    //内部温度(°C)
                    if (StringUtil.isNotEmpty(pileRealModel.getInnerTemp())) {
                        result.setInnerTemperature(getToDouble(pileRealModel.getInnerTemp()));
                    }
                }

                //获取电桩今日充电量,今日放电量
                String dayStartTime = getStartTimeByQueryType(0);
                String dayEndTime = getEndTimeByQueryType(0);
                List<OrderRecordEntity> orderRecordList = orderRecordDao.findAllByPileCodeInAndEndTimeBetween(Collections.singletonList(deviceBasicInfo.getDeviceNumber()), dayStartTime, dayEndTime);
                if (CollectionUtils.isNotEmpty(orderRecordList)) {
                    //今日充电量
                    result.setDayChargeQt(getAbsDouble(orderRecordList.stream().filter(o -> StringUtil.isNotEmpty(o.getRunMode()) && o.getRunMode() == 0 && StringUtil.isNotEmpty(o.getTotalQt())).mapToDouble(OrderRecordEntity::getTotalQt).sum(), 3));
                    //今日放电量
                    result.setDayV2gQt(getAbsDouble(orderRecordList.stream().filter(o -> StringUtil.isNotEmpty(o.getRunMode()) && (o.getRunMode() == 1 || o.getRunMode() == 2) && StringUtil.isNotEmpty(o.getTotalQt())).mapToDouble(OrderRecordEntity::getTotalQt).sum(), 3));
                }
            }
            return ResponseResult.ok(result);
        } catch (RuntimeException e) {
            log.error("查询电桩静态数据报错", e);
            return ResponseResult.error("查询电桩静态数据报错,请稍后重试!!");
        }
    }

    @Override
    public ResponseResult<SuperPileDto> findSuperPileData(String dataId) {
        try {
            //返回的对象
            SuperPileDto result = new SuperPileDto();
            //根据设备id查询电桩数据
            DeviceBasicInfoDto deviceBasicInfo = deviceService.findDeviceBasicInfoByIds(Collections.singletonList(dataId)).getData().get(dataId);
            if (deviceBasicInfo != null) {
                BeanUtils.copyProperties(deviceBasicInfo, result);
                Map<String, Object> reaMap = deviceBasicInfo.getReaMap();
                //设备型号
                if (!reaMap.isEmpty() && reaMap.containsKey(ReaFieldParamVo.MODEL) && StringUtil.isNotEmpty(reaMap.get(ReaFieldParamVo.MODEL))) {
                    result.setModel(String.valueOf(reaMap.get(ReaFieldParamVo.MODEL)));
                }
                //额定功率
                if (!reaMap.isEmpty() && reaMap.containsKey(ReaFieldParamVo.RATED_POWER) && StringUtil.isNotEmpty(reaMap.get(ReaFieldParamVo.RATED_POWER))) {
                    result.setRatedPower(Double.parseDouble(String.valueOf(reaMap.get(ReaFieldParamVo.RATED_POWER))));
                }
                //生产厂家
                if (!reaMap.isEmpty() && reaMap.containsKey(ReaFieldParamVo.MANUFACTURER_NAME) && StringUtil.isNotEmpty(reaMap.get(ReaFieldParamVo.MANUFACTURER_NAME))) {
                    result.setManufacturerName(String.valueOf(reaMap.get(ReaFieldParamVo.MANUFACTURER_NAME)));
                }

                //获取设备通信状态
                result.setTxStatus(deviceBasicInfo.getTxStatus());

                //获取电桩实时状态
                String functionLogos = String.join(FileUtil.COMMA, FunctionLogoParamVo.PILE_POWER, FunctionLogoParamVo.GUN_ORIGINAL_STATUS, FunctionLogoParamVo.VEHICLE_CONN_STATE,
                        FunctionLogoParamVo.GUN_VOLTAGE, FunctionLogoParamVo.GUN_CURRENT, FunctionLogoParamVo.GUN_SOC);
                Map<String, RealDataModel> realDataModelMap = deviceService.getDeviceFunctionsRealDataByIds(Collections.singleton(dataId),
                        functionLogos, 2).getData().get(dataId);
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
                ExtraValueUtil.getSuperPileGunStatus(deviceBasicInfo.getTxStatus(), gunState, vehicleState, result);

                if (realDataModelMap.containsKey(FunctionLogoParamVo.PILE_POWER)) { //有功功率
                    Object dataValue = realDataModelMap.get(FunctionLogoParamVo.PILE_POWER).getDataValue();
                    if (StringUtil.isNotEmpty(dataValue)) {
                        result.setActivePower(DoubleUtil.getToDouble(Double.parseDouble(String.valueOf(dataValue))));
                    }
                }
                if (realDataModelMap.containsKey(FunctionLogoParamVo.GUN_VOLTAGE)) { //电压
                    Object dataValue = realDataModelMap.get(FunctionLogoParamVo.GUN_VOLTAGE).getDataValue();
                    if (StringUtil.isNotEmpty(dataValue)) {
                        result.setVoltage(DoubleUtil.getToDouble(Double.parseDouble(String.valueOf(dataValue))));
                    }
                }
                if (realDataModelMap.containsKey(FunctionLogoParamVo.GUN_CURRENT)) { //电流
                    Object dataValue = realDataModelMap.get(FunctionLogoParamVo.GUN_CURRENT).getDataValue();
                    if (StringUtil.isNotEmpty(dataValue)) {
                        result.setCurrent(DoubleUtil.getToDouble(Double.parseDouble(String.valueOf(dataValue))));
                    }
                }
                if (realDataModelMap.containsKey(FunctionLogoParamVo.GUN_SOC)) { //SOC
                    Object dataValue = realDataModelMap.get(FunctionLogoParamVo.GUN_SOC).getDataValue();
                    if (StringUtil.isNotEmpty(dataValue)) {
                        result.setSoc(Integer.parseInt(String.valueOf(dataValue)));
                    }
                }
            }
            return ResponseResult.ok(result);
        } catch (RuntimeException e) {
            log.error("查询超充桩静态数据报错", e);
            return ResponseResult.error("查询超充桩静态数据报错,请稍后重试!!");
        }
    }

    @Override
    public ResponseResult<List<PileGunPowerDto>> findPileGunPowerList(String dataId) {
        try {
            //返回的集合
            List<PileGunPowerDto> resultList = Lists.newArrayList();
            //根据设备id查询电桩数据
            DeviceBasicInfoDto deviceBasicInfo = deviceService.findDeviceBasicInfoByIds(Collections.singletonList(dataId)).getData().get(dataId);
            if (deviceBasicInfo != null) {
                //获取电桩枪的今日充电量,今日放电量
                Map<Integer, Double> pileGunChargeQtMap = Maps.newHashMap();
                Map<Integer, Double> pileGunV2gQtMap = Maps.newHashMap();
                String dayStartTime = getStartTimeByQueryType(0);
                String dayEndTime = getEndTimeByQueryType(0);
                List<OrderRecordEntity> orderRecordList = orderRecordDao.findAllByPileCodeInAndEndTimeBetween(Collections.singletonList(deviceBasicInfo.getDeviceNumber()), dayStartTime, dayEndTime);
                if (CollectionUtils.isNotEmpty(orderRecordList)) {
                    pileGunChargeQtMap = orderRecordList.stream().filter(o -> StringUtil.isNotEmpty(o.getRunMode()) && o.getRunMode() == 0 && StringUtil.isNotEmpty(o.getTotalQt())).collect(Collectors.groupingBy(OrderRecordEntity::getGunCode, Collectors.summingDouble(OrderRecordEntity::getTotalQt)));
                    pileGunV2gQtMap = orderRecordList.stream().filter(o -> StringUtil.isNotEmpty(o.getRunMode()) && (o.getRunMode() == 1 || o.getRunMode() == 2) && StringUtil.isNotEmpty(o.getTotalQt())).collect(Collectors.groupingBy(OrderRecordEntity::getGunCode, Collectors.summingDouble(OrderRecordEntity::getTotalQt)));
                }

                //获取日期列表
                LocalDateTime startTime = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
                LocalDateTime endTime = LocalDateTime.now();
                List<String> timeList = getLocalDateTimeBetween(startTime, endTime, "1m").stream().map(d -> localDateTimeToStr(d).substring(11, 16)).collect(Collectors.toList());

                //查询充电枪的需求功率,输出功率
                DeviceHistoryQueryVo deviceHistoryQueryVo = new DeviceHistoryQueryVo();
                deviceHistoryQueryVo.setDeviceIds(Collections.singleton(dataId));
                deviceHistoryQueryVo.setFunctionLogos(new HashSet<>(Arrays.asList(FunctionLogoParamVo.GUN_REQ_POWER, FunctionLogoParamVo.GUN_OUT_POWER)));
                deviceHistoryQueryVo.setStartTime(localDateTimeToStr(startTime));
                deviceHistoryQueryVo.setEndTime(localDateTimeToStr(endTime));
                deviceHistoryQueryVo.setTimeInterval("1m");
                //查询需求功率,输出功率历史数据
                Map<String, Object> gunReqPowerMap = Maps.newHashMap();
                Map<String, Object> gunOutPowerMap = Maps.newHashMap();
                Map<String, List<DeviceHistoryDto>> deviceHistoryMap = dataService.findDeviceHistoryValueList(deviceHistoryQueryVo).getData().get(dataId);
                if (deviceHistoryMap.containsKey(FunctionLogoParamVo.GUN_REQ_POWER)) {
                    gunReqPowerMap = deviceHistoryMap.get(FunctionLogoParamVo.GUN_REQ_POWER).stream().filter(c -> StringUtil.isNotEmpty(c.getDataValue()))
                            .collect(Collectors.toMap(c -> c.getDateTime().substring(11, 16), DeviceHistoryDto::getDataValue, (k1, k2) -> k1));
                }
                if (deviceHistoryMap.containsKey(FunctionLogoParamVo.GUN_OUT_POWER)) {
                    gunOutPowerMap = deviceHistoryMap.get(FunctionLogoParamVo.GUN_OUT_POWER).stream().filter(c -> StringUtil.isNotEmpty(c.getDataValue()))
                            .collect(Collectors.toMap(c -> c.getDateTime().substring(11, 16), DeviceHistoryDto::getDataValue, (k1, k2) -> k1));
                }

                //根据设备id查询充电枪数据列表
                List<DeviceGunInfoDto> deviceGunList = deviceService.findDeviceGunInfoByDeviceIds(Collections.singletonList(deviceBasicInfo.getId())).getData().get(deviceBasicInfo.getId());
                if (CollectionUtils.isNotEmpty(deviceGunList)) {
                    Map<Integer, Double> finalPileGunChargeQtMap = pileGunChargeQtMap;
                    Map<Integer, Double> finalPileGunV2gQtMap = pileGunV2gQtMap;
                    Map<String, Object> finalGunReqPowerMap = gunReqPowerMap;
                    Map<String, Object> finalGunOutPowerMap = gunOutPowerMap;
                    //对数据进行组装
                    resultList = deviceGunList.stream().filter(g -> StringUtil.isNotEmpty(g.getGunCode()) && StringUtil.isNumber(g.getGunCode())).map(deviceGun -> {
                        PileGunPowerDto result = new PileGunPowerDto();
                        result.setPileCode(deviceBasicInfo.getDeviceNumber());
                        result.setGunCode(deviceGun.getGunCode());
                        result.setGunName(deviceGun.getGunName());
                        int gunCode = Integer.parseInt(deviceGun.getGunCode());
                        //今日充电量
                        result.setDayChargeQt(DoubleUtil.getAbsDouble(finalPileGunChargeQtMap.getOrDefault(gunCode, 0.0), 3));
                        //今日放电量
                        result.setDayV2gQt(DoubleUtil.getAbsDouble(finalPileGunV2gQtMap.getOrDefault(gunCode, 0.0), 3));
                        //日期列表
                        result.setTimeList(timeList);
                        timeList.forEach(time -> {
                            //需求功率
                            if (finalGunReqPowerMap.containsKey(time) && StringUtil.isNotEmpty(finalGunReqPowerMap.get(time))) {
                                List<Double> gunReqPowerList = JSON.parseArray(String.valueOf(finalGunReqPowerMap.get(time)), Double.class);
                                if (gunReqPowerList.size() >= gunCode) {
                                    result.getReqPowerList().add(getToDouble(gunReqPowerList.get(gunCode - 1)));
                                } else {
                                    result.getReqPowerList().add(null);
                                }
                            } else {
                                result.getReqPowerList().add(null);
                            }
                            //输出功率
                            if (finalGunOutPowerMap.containsKey(time) && StringUtil.isNotEmpty(finalGunOutPowerMap.get(time))) {
                                List<Double> gunOutPowerList = JSON.parseArray(String.valueOf(finalGunOutPowerMap.get(time)), Double.class);
                                if (gunOutPowerList.size() >= gunCode) {
                                    result.getOutPowerList().add(getToDouble(gunOutPowerList.get(gunCode - 1)));
                                } else {
                                    result.getOutPowerList().add(null);
                                }
                            } else {
                                result.getOutPowerList().add(null);
                            }
                        });
                        return result;
                    }).collect(Collectors.toList());
                }
            }
            return ResponseResult.ok(resultList);
        } catch (RuntimeException e) {
            log.error("查询电枪数据报错", e);
            return ResponseResult.error("查询电枪数据报错,请稍后重试!!");
        }
    }

    @Override
    public ResponseResult<SystemCurveDto> findSystemCurve(SystemQueryVo systemQueryVo) {
        try {
            //返回的对象
            SystemCurveDto result;

            //数据id
            String dataId = systemQueryVo.getDataId();
            //开始时间
            String startTime = systemQueryVo.getStartTime();
            //结束时间
            String endTime = systemQueryVo.getEndTime();
            //数据时间间隔 s-秒 m-分钟 h-小时 d-天 n-月 y-年
            String timeInterval = systemQueryVo.getTimeInterval();
            //时间格式间隔 1-(yyyy-MM-dd HH:mm:ss) 2-(yyyy-MM-dd) 3-(yyyy-MM) 4-(yyyy) 5-(HH:mm:ss) 6-(HH:mm)
            Integer formatInterval = systemQueryVo.getFormatInterval();
            //数据下标(可选填)
            Integer dataIndex = systemQueryVo.getDataIndex();
            Set<String> dateList = getDateFormatTimeList(startTime, endTime, timeInterval, formatInterval);
            //类型 1-光伏系统功率 2-光伏系统发电量 3-光伏逆变器功率 4-光伏逆变器发电量 5-光伏气象站辐照度 6-光伏气象站温度 7-光伏气象站辐照累积量 8-储能系统功率 9-储能系统发电量 10-储能PCS功率 11-储能PCS充放电量 12-储能电池簇SOC 13-储能电池簇总电压 14-储能辅助设备温度 15-储能辅助设备湿度 16-电桩系统功率 17-电桩系统充放电量 18-电桩功率 19-电桩充放电量 20-储能电池簇电芯电压 21-储能电池簇电芯温度 22-换电系统功率 23-换电系统充电量 24-电表功率因数 25-电表有功功率 26-电表无功功率 27-电表分时电量
            Integer type = systemQueryVo.getType();
            if (StringUtil.isEmpty(type)) {
                return ResponseResult.paramError(ResponseResult.PARAM_ERROR);
            }
            switch (type) {
                case 1: //光伏系统功率
                    result = getSystemPvPowerCurve(dataId, startTime, endTime, timeInterval, formatInterval, dateList);
                    break;
                case 2: //光伏系统发电量
                    result = getSystemPvQtCurve(dataId, startTime, endTime, timeInterval, formatInterval, dateList);
                    break;
                case 3: //光伏逆变器功率
                    result = getPvInverterPowerCurve(dataId, startTime, endTime, timeInterval, formatInterval, dateList);
                    break;
                case 4: //光伏逆变器发电量
                    result = getPvInverterQtCurve(dataId, startTime, endTime, timeInterval, formatInterval, dateList);
                    break;
                case 5: //光伏气象站辐照度
                    result = getPvWeatherRadiationCurve(dataId, startTime, endTime, timeInterval, formatInterval, dateList);
                    break;
                case 6: //光伏气象站温度
                    result = getPvWeatherTemperatureCurve(dataId, startTime, endTime, timeInterval, formatInterval, dateList);
                    break;
                case 7: //光伏气象站辐照累积量
                    result = getPvWeatherIrradiationCurve(dataId, startTime, endTime, timeInterval, formatInterval, dateList);
                    break;
                case 8: //储能系统功率
                    result = getSystemSePowerCurve(dataId, startTime, endTime, timeInterval, formatInterval, dateList);
                    break;
                case 9: //储能系统发电量
                    result = getSystemSeQtCurve(dataId, startTime, endTime, timeInterval, formatInterval, dateList);
                    break;
                case 10: //储能PCS功率
                    result = getSePcsPowerCurve(dataId, startTime, endTime, timeInterval, formatInterval, dateList);
                    break;
                case 11: //储能PCS充放电量
                    result = getSePcsQtCurve(dataId, startTime, endTime, timeInterval, formatInterval, dateList);
                    break;
                case 12: //储能电池簇SOC
                    result = getSeBatterySocCurve(dataId, startTime, endTime, timeInterval, formatInterval, dateList);
                    break;
                case 13: //储能电池簇总电压
                    result = getSeBatteryVoltageCurve(dataId, startTime, endTime, timeInterval, formatInterval, dateList);
                    break;
                case 14: //储能辅助设备温度
                    result = getSeAuxEquipmentTemperatureCurve(dataId, startTime, endTime, timeInterval, formatInterval, dateList);
                    break;
                case 15: //储能辅助设备湿度
                    result = getSeAuxEquipmentHumidityCurve(dataId, startTime, endTime, timeInterval, formatInterval, dateList);
                    break;
                case 16: //电桩系统功率
                    result = getSystemPilePowerCurve(dataId, startTime, endTime, timeInterval, formatInterval, dateList);
                    break;
                case 17: //电桩系统充放电量
                    result = getSystemPileQtCurve(dataId, startTime, endTime, formatInterval, dateList);
                    break;
                case 18: //电桩功率
                    result = getPilePowerCurve(dataId, startTime, endTime, timeInterval, formatInterval, dateList);
                    break;
                case 19: //电桩充放电量
                    result = getPileQtCurve(dataId, startTime, endTime, formatInterval, dateList);
                    break;
                case 20: //储能电池簇电芯电压
                    result = getSeBatteryCellVoltageCurve(dataId, startTime, endTime, timeInterval, formatInterval, dateList, dataIndex);
                    break;
                case 21: //储能电池簇电芯温度
                    result = getSeBatteryCellTempCurve(dataId, startTime, endTime, timeInterval, formatInterval, dateList, dataIndex);
                    break;
                case 22: //换电系统功率
                    result = getSystemChangePowerCurve(dataId, startTime, endTime, timeInterval, formatInterval, dateList);
                    break;
                case 23: //换电系统充电量
                    result = getSystemChangeQtCurve(dataId, startTime, endTime, timeInterval, formatInterval, dateList);
                    break;
                case 24: //电表功率因数
                    result = getMeterPowerFactorCurve(dataId, startTime, endTime, timeInterval, formatInterval, dateList);
                    break;
                case 25: //电表有功功率
                    result = getMeterActivePowerCurve(dataId, startTime, endTime, timeInterval, formatInterval, dateList);
                    break;
                case 26: //电表无功功率
                    result = getMeterReactivePowerCurve(dataId, startTime, endTime, timeInterval, formatInterval, dateList);
                    break;
                case 27: //电表分时电量
                    result = getMeterShareQtCurve(dataId, startTime, endTime, timeInterval, formatInterval, dateList);
                    break;
                default:
                    return ResponseResult.paramError(ResponseResult.PARAM_ERROR);
            }
            return ResponseResult.ok(result);
        } catch (RuntimeException e) {
            log.error("查询系统曲线数据报错", e);
            return ResponseResult.error("查询系统曲线数据报错,请稍后重试!!");
        }

    }

    @Override
    public ResponseResult<PageDto<FaultAlarmListDto>> findAllFaultAlarmList(FaultAlarmQueryVo faultAlarmVo) {
        try {
            //返回的分页告警数据
            PageDto<FaultAlarmListDto> resultPage = new PageDto<>(Lists.newArrayList(), faultAlarmVo.getPage(), faultAlarmVo.getSize());
            //根据站点id查询下面的设备id
            List<DeviceBasicInfoDto> deviceList = deviceService.findDeviceBasicInfoBySiteIds(Collections.singletonList(faultAlarmVo.getSiteId()), null).getData().get(faultAlarmVo.getSiteId());
            if (CollectionUtils.isNotEmpty(deviceList)) {
                //设备类型过滤
                if (StringUtil.isNotEmpty(faultAlarmVo.getTypeId())) {
                    deviceList = deviceList.stream().filter(d -> Objects.equals(d.getTypeId(), faultAlarmVo.getTypeId())).collect(Collectors.toList());
                }
                if (CollectionUtils.isEmpty(deviceList)) {
                    return ResponseResult.ok(resultPage);
                }
                Map<String, DeviceBasicInfoDto> deviceInfoMap = deviceList.stream().collect(Collectors.toMap(DeviceBasicInfoDto::getId, a -> a, (k1, k2) -> k1));
                //根据查询条件查询告警数据
                DeviceAlarmEventQueryVo eventQueryVo = new DeviceAlarmEventQueryVo();
                BeanUtils.copyProperties(faultAlarmVo, eventQueryVo);
                eventQueryVo.setDeviceIds(JSON.toJSONString(deviceInfoMap.keySet()));
                eventQueryVo.setEventStatus(faultAlarmVo.getAlarmStatus());
                PageDto<DeviceAlarmEventListDto> deviceAlarmEventPage = deviceService.findAllDeviceEventList(eventQueryVo).getData();
                if (deviceAlarmEventPage == null || CollectionUtils.isEmpty(deviceAlarmEventPage.getItems())) {
                    return ResponseResult.ok(resultPage);
                }
                BeanUtils.copyProperties(deviceAlarmEventPage, resultPage);
                resultPage.setItems(deviceAlarmEventPage.getItems().stream().sorted(Comparator.comparing(DeviceAlarmEventListDto::getCreateTime).reversed()).map(alarmEvent -> {
                    FaultAlarmListDto result = new FaultAlarmListDto();
                    BeanUtils.copyProperties(alarmEvent, result);
                    result.setId(alarmEvent.getDeviceId());
                    result.setEventId(alarmEvent.getId());
                    result.setAlarmStatus(alarmEvent.getEventStatus());
                    if (StringUtil.isNotEmpty(alarmEvent.getCreateTime()) && StringUtil.isNotEmpty(alarmEvent.getUpdateTime())) {
                        result.setAlarmDuration(secToTime((int) ChronoUnit.SECONDS.between(strToLocalDateTime(alarmEvent.getCreateTime()), strToLocalDateTime(alarmEvent.getUpdateTime()))));
                    }
                    if (StringUtil.isNotEmpty(alarmEvent.getDeviceId()) && deviceInfoMap.containsKey(alarmEvent.getDeviceId())) {
                        DeviceBasicInfoDto deviceBasicInfo = deviceInfoMap.get(alarmEvent.getDeviceId());
                        result.setTypeId(deviceBasicInfo.getTypeId());
                        result.setDeviceCode(deviceBasicInfo.getDeviceNumber());
                        result.setDeviceName(deviceBasicInfo.getDeviceName());
                    }
                    return result;
                }).collect(Collectors.toList()));
            }
            return ResponseResult.ok(resultPage);
        } catch (RuntimeException e) {
            log.error("查询系统告警数据报错", e);
            return ResponseResult.paramError("查询系统告警数据报错,请稍后重试!!");
        }

    }

    @Override
    public ResponseResult<DeviceFieldDto> getDeviceFieldList(String siteId) {
        try {
            //返回的对象
            DeviceFieldDto result = new DeviceFieldDto();
            //根据站点id查询站点信息
            SiteInfoDto siteInfo = deviceService.findSiteBasicInfoByIds(Collections.singletonList(siteId)).getData().get(siteId);
            if (siteInfo != null) {
                List<String> siteDeviceIds = Lists.newArrayList();
                siteDeviceIds.add(siteId);
                //定义功能点数据和节点数据
                Map<String, List<ModelFunctionListDto>> functionDataMap = Maps.newHashMap();
                Map<String, Map<String, RealDataModel>> functionArrayDataMap = Maps.newHashMap();
                Map<String, List<ComputeNodeListDto>> nodeDataMap;
                //根据站点id查询设备数据
                List<DeviceBasicInfoDto> deviceList = deviceService.findDeviceBasicInfoBySiteIds(Collections.singletonList(siteId), null).getData().get(siteId);
                if (CollectionUtils.isNotEmpty(deviceList)) {
                    //根据多个设备id查询功能点数据
                    List<String> deviceIds = deviceList.stream().map(DeviceBasicInfoDto::getId).distinct().collect(Collectors.toList());
                    siteDeviceIds.addAll(deviceIds);
                    functionDataMap = deviceService.findDeviceFunctionListByIds(deviceIds).getData();
                    //过滤出来数组类型的功能点
                    Set<String> deviceArrayIds = Sets.newHashSet();
                    Set<String> functionArrayLogos = Sets.newHashSet();
                    functionDataMap.forEach((deviceId, value) -> {
                        List<String> functionLogos = value.stream().filter(f -> StringUtil.isNotEmpty(f.getDataType()) && f.getDataType() == 8)
                                .map(ModelFunctionListDto::getFunctionLogo).collect(Collectors.toList());
                        if (CollectionUtils.isNotEmpty(functionLogos)) {
                            deviceArrayIds.add(deviceId);
                            functionArrayLogos.addAll(functionLogos);
                        }
                    });
                    functionArrayDataMap = deviceService.getDeviceFunctionsRealDataByIds(deviceArrayIds, String.join(FileUtil.COMMA, functionArrayLogos), 2).getData();
                }
                //根据多个设备id查询计算节点数据
                nodeDataMap = crontabService.findComputeNodeListByDeviceIds(siteDeviceIds).getData();

                //站点节点数据
                DeviceFieldDto.DeviceData siteDeviceData = new DeviceFieldDto.DeviceData();
                siteDeviceData.setId(siteId);
                siteDeviceData.setDeviceName(siteInfo.getSiteName());
                siteDeviceData.setType(1);
                if (nodeDataMap.containsKey(siteId)) {
                    siteDeviceData.setFieldList(nodeDataMap.get(siteId).stream().map(nodeData -> {
                        DeviceFieldDto.DeviceField deviceField = new DeviceFieldDto.DeviceField();
                        deviceField.setFieldCode(nodeData.getNodeCode());
                        deviceField.setFieldName(nodeData.getNodeName());
                        deviceField.setTimeInterval(nodeData.getCountPeriod());
                        return deviceField;
                    }).collect(Collectors.toList()));
                }
                result.getNodeList().add(siteDeviceData);

                //对设备数据进行组装
                if (CollectionUtils.isNotEmpty(deviceList)) {
                    for (DeviceBasicInfoDto device : deviceList) {
                        //功能点设备数据
                        DeviceFieldDto.DeviceData deviceFunctionData = new DeviceFieldDto.DeviceData();
                        deviceFunctionData.setId(device.getId());
                        deviceFunctionData.setDeviceName(device.getDeviceName());
                        deviceFunctionData.setType(2);
                        if (functionDataMap.containsKey(device.getId())) {
                            Map<String, RealDataModel> functionArrayMap;
                            if (functionArrayDataMap.containsKey(device.getId())) {
                                functionArrayMap = functionArrayDataMap.get(device.getId());
                            } else {
                                functionArrayMap = Maps.newHashMap();
                            }
                            deviceFunctionData.setFieldList(functionDataMap.get(device.getId()).stream().map(functionData -> {
                                DeviceFieldDto.DeviceField deviceField = new DeviceFieldDto.DeviceField();
                                deviceField.setFieldCode(functionData.getFunctionLogo());
                                deviceField.setFieldName(functionData.getFunctionName());
                                if (StringUtil.isNotEmpty(functionData.getDataType()) && functionData.getDataType() == 8) {
                                    if (functionArrayMap.containsKey(functionData.getFunctionLogo())) {
                                        Object dataValue = functionArrayMap.get(functionData.getFunctionLogo()).getDataValue();
                                        if (StringUtil.isNotEmpty(dataValue)) {
                                            List<Integer> indexes = Lists.newArrayList();
                                            for (int i = 0; i < JSON.parseArray(String.valueOf(dataValue)).size(); i++) {
                                                indexes.add(i);
                                            }
                                            deviceField.setIndexes(JSON.toJSONString(indexes));
                                        }
                                    }
                                }
                                deviceField.setTimeInterval("1m");
                                return deviceField;
                            }).collect(Collectors.toList()));
                        }
                        result.getFunctionList().add(deviceFunctionData);

                        //节点设备数据
                        DeviceFieldDto.DeviceData deviceNodeData = new DeviceFieldDto.DeviceData();
                        deviceNodeData.setId(device.getId());
                        deviceNodeData.setDeviceName(device.getDeviceName());
                        deviceNodeData.setType(2);
                        if (nodeDataMap.containsKey(device.getId())) {
                            deviceNodeData.setFieldList(nodeDataMap.get(device.getId()).stream().map(nodeData -> {
                                DeviceFieldDto.DeviceField deviceField = new DeviceFieldDto.DeviceField();
                                deviceField.setFieldCode(nodeData.getNodeCode());
                                deviceField.setFieldName(nodeData.getNodeName());
                                deviceField.setTimeInterval(nodeData.getCountPeriod());
                                return deviceField;
                            }).collect(Collectors.toList()));
                        }
                        result.getNodeList().add(deviceNodeData);
                    }
                }

            }
            return ResponseResult.ok(result);
        } catch (RuntimeException e) {
            log.error("获取站点及站点下面的设备数据字段报错", e);
            return ResponseResult.paramError("获取站点及站点下面的设备数据字段报错,请稍后重试!!");
        }
    }

    @Override
    public ResponseResult<HistoryDataDto> findAllHistoryDataList(HistoryDataQueryVo historyDataVo) {
        try {

            //返回的对象
            HistoryDataDto result = new HistoryDataDto();

            String functions = historyDataVo.getFunctions();
            String nodes = historyDataVo.getNodes();
            String startTime = historyDataVo.getStartTime();
            String endTime = historyDataVo.getEndTime();
            String timeInterval = historyDataVo.getTimeInterval();

            //获取日期列表
            List<String> dateList = getLocalDateTimeBetween(strToLocalDateTime(startTime), strToLocalDateTime(endTime), timeInterval)
                    .stream().map(DateUtil::localDateTimeToStr).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(dateList) && dateList.size() > 5000) {
                return ResponseResult.paramError("查询数据条数太多,请修改时间范围再试!!");
            }
            result.setDateList(dateList);

            Set<String> siteIds = Sets.newHashSet();
            Set<String> deviceIds = Sets.newHashSet();
            //解析设备功能点查询条件
            List<HistoryDataQueryVo.DeviceDataVo> deviceFunctionVos = Lists.newArrayList();
            Map<String, List<ModelFunctionListDto>> deviceFunctionNameMap = Maps.newHashMap();
            if (StringUtil.isNotEmpty(functions)) {
                deviceFunctionVos = JSON.parseArray(functions, HistoryDataQueryVo.DeviceDataVo.class);
                siteIds.addAll(deviceFunctionVos.stream().filter(s -> StringUtil.isNotEmpty(s.getType()) && s.getType() == 1)
                        .map(HistoryDataQueryVo.DeviceDataVo::getId).collect(Collectors.toSet()));
                Set<String> functionDeviceIds = deviceFunctionVos.stream().filter(s -> StringUtil.isNotEmpty(s.getType()) && s.getType() == 2)
                        .map(HistoryDataQueryVo.DeviceDataVo::getId).collect(Collectors.toSet());
                deviceIds.addAll(functionDeviceIds);
                deviceFunctionNameMap = deviceService.findDeviceFunctionListByIds(new ArrayList<>(functionDeviceIds)).getData();
            }
            //解析设备节点查询条件
            List<HistoryDataQueryVo.DeviceDataVo> deviceNodeVos = Lists.newArrayList();
            Map<String, List<ComputeNodeListDto>> deviceNodeNameMap = Maps.newHashMap();
            if (StringUtil.isNotEmpty(nodes)) {
                deviceNodeVos = JSON.parseArray(nodes, HistoryDataQueryVo.DeviceDataVo.class);
                siteIds.addAll(deviceNodeVos.stream().filter(s -> StringUtil.isNotEmpty(s.getType()) && s.getType() == 1)
                        .map(HistoryDataQueryVo.DeviceDataVo::getId).collect(Collectors.toSet()));
                deviceIds.addAll(deviceNodeVos.stream().filter(s -> StringUtil.isNotEmpty(s.getType()) && s.getType() == 2)
                        .map(HistoryDataQueryVo.DeviceDataVo::getId).collect(Collectors.toSet()));
                Set<String> nodeIds = deviceNodeVos.stream().map(HistoryDataQueryVo.DeviceDataVo::getId).collect(Collectors.toSet());
                deviceNodeNameMap = crontabService.findComputeNodeListByDeviceIds(new ArrayList<>(nodeIds)).getData();
            }

            //根据多个站点id查询站点名称
            Map<String, SiteInfoDto> siteInfoMap = Maps.newHashMap();
            if (CollectionUtils.isNotEmpty(siteIds)) {
                siteInfoMap = deviceService.findSiteBasicInfoByIds(new ArrayList<>(siteIds)).getData();
            }
            //根据多个设备id查询设备名称
            Map<String, DeviceBasicInfoDto> deviceInfoMap = Maps.newHashMap();
            if (CollectionUtils.isNotEmpty(deviceIds)) {
                deviceInfoMap = deviceService.findDeviceBasicInfoByIds(new ArrayList<>(deviceIds)).getData();
            }

            //查询设备功能点历史数据
            if (CollectionUtils.isNotEmpty(deviceFunctionVos)) {
                deviceFunctionVos = deviceFunctionVos.stream().filter(s -> StringUtil.isNotEmpty(s.getType()) && s.getType() == 2).collect(Collectors.toList());
                //根据查询条件查询设备功能点历史数据
                DeviceHistoryQueryVo deviceQueryVo = new DeviceHistoryQueryVo();
                deviceQueryVo.setDeviceIds(deviceFunctionVos.stream().map(HistoryDataQueryVo.DeviceDataVo::getId).collect(Collectors.toSet()));
                deviceQueryVo.setFunctionLogos(deviceFunctionVos.stream().flatMap(c -> c.getFields().stream().map(HistoryDataQueryVo.DeviceFieldVo::getCode)).collect(Collectors.toSet()));
                deviceQueryVo.setStartTime(startTime);
                deviceQueryVo.setEndTime(endTime);
                deviceQueryVo.setLimitSize(5001);
                deviceQueryVo.setTimeInterval(timeInterval);

                Map<String, Map<String, List<DeviceHistoryDto>>> deviceHistoryMap = dataService.findDeviceHistoryValueList(deviceQueryVo).getData();

                //校验数据长度是否大于5000条
                boolean isTotalSize = false;
                for (HistoryDataQueryVo.DeviceDataVo deviceDataVo : deviceFunctionVos) {
                    //设备名称
                    String deviceName = null;
                    if (StringUtil.isNotEmpty(deviceDataVo.getType())) {
                        if (deviceDataVo.getType() == 1 && siteInfoMap.containsKey(deviceDataVo.getId())) {
                            deviceName = siteInfoMap.get(deviceDataVo.getId()).getSiteName();
                        }
                        if (deviceDataVo.getType() == 2 && deviceInfoMap.containsKey(deviceDataVo.getId())) {
                            deviceName = deviceInfoMap.get(deviceDataVo.getId()).getDeviceName();
                        }
                    }
                    //设备功能点数据
                    Map<String, List<DeviceHistoryDto>> functionDataMap = Maps.newHashMap();
                    if (deviceHistoryMap.containsKey(deviceDataVo.getId())) {
                        functionDataMap = deviceHistoryMap.get(deviceDataVo.getId());
                    }
                    //设备功能点名称数据
                    Map<String, String> functionNameMap = Maps.newHashMap();
                    if (deviceFunctionNameMap.containsKey(deviceDataVo.getId())) {
                        functionNameMap = deviceFunctionNameMap.get(deviceDataVo.getId()).stream().collect(Collectors.toMap(ModelFunctionListDto::getFunctionLogo,
                                ModelFunctionListDto::getFunctionName, (k1, k2) -> k1));
                    }

                    for (HistoryDataQueryVo.DeviceFieldVo deviceField : deviceDataVo.getFields()) {
                        String fieldName = null;
                        if (functionNameMap.containsKey(deviceField.getCode())) {
                            fieldName = functionNameMap.get(deviceField.getCode());
                        }
                        Map<String, DeviceHistoryDto> functionDataTimeMap = Maps.newHashMap();
                        if (functionDataMap.containsKey(deviceField.getCode())) {
                            functionDataTimeMap = functionDataMap.get(deviceField.getCode()).stream().collect(Collectors.toMap(DeviceHistoryDto::getDateTime,
                                    a -> a, (k1, k2) -> k1));
                            //校验数据长度是否大于限定条数
                            if (functionDataTimeMap.size() > 5000) {
                                isTotalSize = true;
                                break;
                            }
                        }
                        //正常数据 不需要索引的
                        if (StringUtil.isEmpty(deviceField.getIndexes())) {
                            //返回的数据实体类
                            HistoryDataDto.DataInfo dataInfo = new HistoryDataDto.DataInfo();
                            dataInfo.setFieldCode(deviceField.getCode());
                            dataInfo.setDeviceName(deviceName);
                            dataInfo.setFieldName(fieldName);
                            for (String date : dateList) {
                                if (functionDataTimeMap.containsKey(date)) {
                                    dataInfo.getDataList().add(functionDataTimeMap.get(date).getDataValue());
                                } else {
                                    dataInfo.getDataList().add(null);
                                }
                            }
                            result.getDataInfoList().add(dataInfo);
                        } else { //数组类型的 需要用下标处理
                            for (Integer index : JSON.parseArray(deviceField.getIndexes(), Integer.class)) {
                                //返回的数据实体类
                                HistoryDataDto.DataInfo dataInfo = new HistoryDataDto.DataInfo();
                                dataInfo.setFieldCode(deviceField.getCode() + FileUtil.LEFT_SQUARE + index + FileUtil.RIGHT_SQUARE);
                                dataInfo.setDeviceName(deviceName);
                                dataInfo.setFieldName(fieldName + FileUtil.LEFT_SQUARE + index + FileUtil.RIGHT_SQUARE);
                                for (String date : dateList) {
                                    if (functionDataTimeMap.containsKey(date)) {
                                        Object dataValue = functionDataTimeMap.get(date).getDataValue();
                                        if (StringUtil.isNotEmpty(dataValue) && index < JSON.parseArray(String.valueOf(dataValue)).size()) {
                                            dataInfo.getDataList().add(JSON.parseArray(String.valueOf(dataValue)).get(index));
                                        } else {
                                            dataInfo.getDataList().add(null);
                                        }
                                    } else {
                                        dataInfo.getDataList().add(null);
                                    }
                                }
                                result.getDataInfoList().add(dataInfo);
                            }
                        }
                    }
                }
                if (isTotalSize) {
                    return ResponseResult.paramError("查询数据条数太多,请修改时间范围再试!!");
                }
            }
            //查询设备计算节点历史数据
            if (CollectionUtils.isNotEmpty(deviceNodeVos)) {
                //根据查询条件查询计算节点历史数据
                Map<String, Set<String>> deviceNodeCodeMap = Maps.newHashMap();
                for (HistoryDataQueryVo.DeviceDataVo deviceNodeVo : deviceNodeVos) {
                    deviceNodeCodeMap.put(deviceNodeVo.getId(), deviceNodeVo.getFields().stream().map(HistoryDataQueryVo.DeviceFieldVo::getCode).collect(Collectors.toSet()));
                }
                DeviceNodeValueVo deviceNodeValueVo = new DeviceNodeValueVo();
                deviceNodeValueVo.setDeviceNodeCodeMap(deviceNodeCodeMap);
                deviceNodeValueVo.setStartTime(startTime);
                deviceNodeValueVo.setEndTime(endTime);
                deviceNodeValueVo.setLimitSize(5001);
                deviceNodeValueVo.setTimeInterval(timeInterval);
                Map<String, Map<String, List<NodeHistoryDataDto>>> deviceNodeDataMap = crontabService.findDeviceNodeValueList(deviceNodeValueVo).getData();

                //校验数据长度是否大于5000条
                boolean isTotalSize = false;
                for (HistoryDataQueryVo.DeviceDataVo deviceNodeVo : deviceNodeVos) {
                    //设备名称
                    String deviceName = null;
                    if (StringUtil.isNotEmpty(deviceNodeVo.getType())) {
                        if (deviceNodeVo.getType() == 1 && siteInfoMap.containsKey(deviceNodeVo.getId())) {
                            deviceName = siteInfoMap.get(deviceNodeVo.getId()).getSiteName();
                        }
                        if (deviceNodeVo.getType() == 2 && deviceInfoMap.containsKey(deviceNodeVo.getId())) {
                            deviceName = deviceInfoMap.get(deviceNodeVo.getId()).getDeviceName();
                        }
                    }
                    //设备节点数据
                    Map<String, List<NodeHistoryDataDto>> nodeDataMap = Maps.newHashMap();
                    if (deviceNodeDataMap.containsKey(deviceNodeVo.getId())) {
                        nodeDataMap = deviceNodeDataMap.get(deviceNodeVo.getId());
                    }
                    //设备节点名称数据
                    Map<String, String> nodeNameMap = Maps.newHashMap();
                    if (deviceNodeNameMap.containsKey(deviceNodeVo.getId())) {
                        nodeNameMap = deviceNodeNameMap.get(deviceNodeVo.getId()).stream().collect(Collectors.toMap(ComputeNodeListDto::getNodeCode,
                                ComputeNodeListDto::getNodeName, (k1, k2) -> k1));
                    }

                    for (HistoryDataQueryVo.DeviceFieldVo deviceField : deviceNodeVo.getFields()) {
                        String fieldName = null;
                        if (nodeNameMap.containsKey(deviceField.getCode())) {
                            fieldName = nodeNameMap.get(deviceField.getCode());
                        }
                        Map<String, NodeHistoryDataDto> nodeDataTimeMap = Maps.newHashMap();
                        if (nodeDataMap.containsKey(deviceField.getCode())) {
                            nodeDataTimeMap = nodeDataMap.get(deviceField.getCode()).stream().collect(Collectors.toMap(n ->
                                    n.getTs().substring(0, 19), a -> a, (k1, k2) -> k1));
                            //校验数据长度是否大于限定条数
                            if (nodeDataTimeMap.size() > 5000) {
                                isTotalSize = true;
                                break;
                            }
                        }
                        //正常数据 不需要索引的
                        if (StringUtil.isEmpty(deviceField.getIndexes())) {
                            //返回的数据实体类
                            HistoryDataDto.DataInfo dataInfo = new HistoryDataDto.DataInfo();
                            dataInfo.setFieldCode(deviceField.getCode());
                            dataInfo.setDeviceName(deviceName);
                            dataInfo.setFieldName(fieldName);
                            for (String date : dateList) {
                                if (nodeDataTimeMap.containsKey(date)) {
                                    dataInfo.getDataList().add(getToDouble(nodeDataTimeMap.get(date).getResultValue()));
                                } else {
                                    dataInfo.getDataList().add(null);
                                }
                            }
                            result.getDataInfoList().add(dataInfo);
                        } else { //数组类型的 需要用下标处理
                            for (Integer index : JSON.parseArray(deviceField.getIndexes(), Integer.class)) {
                                //返回的数据实体类
                                HistoryDataDto.DataInfo dataInfo = new HistoryDataDto.DataInfo();
                                dataInfo.setFieldCode(deviceField.getCode() + FileUtil.LEFT_SQUARE + index + FileUtil.RIGHT_SQUARE);
                                dataInfo.setDeviceName(deviceName);
                                dataInfo.setFieldName(fieldName);
                                for (String date : dateList) {
                                    if (nodeDataTimeMap.containsKey(date)) {
                                        Object dataValue = nodeDataTimeMap.get(date).getResultValue();
                                        if (StringUtil.isNotEmpty(dataValue) && index < JSON.parseArray(String.valueOf(dataValue)).size()) {
                                            dataInfo.getDataList().add(JSON.parseArray(String.valueOf(dataValue)).get(index));
                                        } else {
                                            dataInfo.getDataList().add(null);
                                        }
                                    } else {
                                        dataInfo.getDataList().add(null);
                                    }
                                }
                                result.getDataInfoList().add(dataInfo);
                            }
                        }
                    }
                }
                if (isTotalSize) {
                    return ResponseResult.paramError("查询数据条数太多,请修改时间范围再试!!");
                }
            }
            return ResponseResult.ok(result);
        } catch (RuntimeException e) {
            log.error("查询站点历史数据报错", e);
            return ResponseResult.paramError("查询站点历史数据报错,请稍后重试!!");
        }
    }

    @Override
    public ResponseResult<SiteDetailDto> findSiteDetailById(String siteId) {
        try {
            //返回的对象
            SiteDetailDto result = new SiteDetailDto();
            //根据站点id查询站点信息
            SiteInfoDto siteInfo = deviceService.findSiteBasicInfoByIds(Collections.singletonList(siteId)).getData().get(siteId);
            if (siteInfo != null) {
                result.setSiteId(siteId);
                result.setSiteName(siteInfo.getSiteName());
                SiteSetUpDto siteSetUp = deviceService.findSiteSetUpBySiteIds(Collections.singletonList(siteId)).getData().get(siteId);
                if (siteSetUp != null) {
                    //系统名称 为空则存站点名称的前10个字符
                    result.setSystemName(siteSetUp.getSystemName());
                    if (StringUtil.isEmpty(siteSetUp.getSystemName()) && StringUtil.isNotEmpty(siteInfo.getSiteName())) {
                        if (siteInfo.getSiteName().length() > 12) {
                            result.setSystemName(siteInfo.getSiteName().substring(0, 12));
                        } else {
                            result.setSystemName(siteInfo.getSiteName());
                        }
                    }
                    //站点应用配置信息数据
                    result.setReadwriteObject(siteSetUp.getReadwriteObject());
                }
            }
            return ResponseResult.ok(result);
        } catch (RuntimeException e) {
            log.error("查询站点详情数据报错: ", e);
            return ResponseResult.paramError("查询站点详情数据报错,请稍后重试!!");
        }
    }

    @Override
    public ResponseResult<PageDto<Object>> findAllCellList(String dataId, Integer queryType, Integer page, Integer size) {
        try {
            List<Object> resultList = Lists.newArrayList();
            //根据设备id，和功能点标识查询设备功能点实时数据
            String functionLogo = null;
            if (queryType == 1) {
                functionLogo = FunctionLogoParamVo.CELL_TEMP;
            }
            if (queryType == 2) {
                functionLogo = FunctionLogoParamVo.CELL_VOLTAGE;
            }

            Map<String, RealDataModel> realDataModelMap = deviceService.getDeviceFunctionsRealDataByIds(Collections.singleton(dataId),
                    functionLogo, 2).getData().get(dataId);
            if (MapUtils.isNotEmpty(realDataModelMap) && realDataModelMap.containsKey(functionLogo)) {
                RealDataModel realDataModel = realDataModelMap.get(functionLogo);
                if (StringUtil.isNotEmpty(realDataModel) && StringUtil.isNotEmpty(realDataModel.getDataValue())) {
                    // 转换为 List
                    resultList = JSON.parseArray(String.valueOf(realDataModel.getDataValue()), Object.class);
                }
            }
            return ResponseResult.ok(new PageDto<>(resultList, page, size));
        } catch (RuntimeException e) {
            log.error("查询电池簇电芯列表数据报错: ", e);
            return ResponseResult.paramError("查询电池簇电芯列表数据报错,请稍后重试!!");
        }
    }

    @Override
    public ResponseResult<SystemChangeDto> findSystemChangeData(String dataId) {
        try {
            //返回的对象
            SystemChangeDto result = new SystemChangeDto();

            String siteId = deviceService.findSiteIdBySubId(dataId).getData();
            if (StringUtil.isEmpty(siteId)) {
                return ResponseResult.paramError(ResponseResult.PARAM_ERROR);
            }
            //根据站点id查询站点信息
            SiteInfoDto siteInfo = deviceService.findSiteBasicInfoByIds(Collections.singletonList(siteId)).getData().get(siteId);
            List<SystemTreeDto> systemTreeList = this.getSystemMonitorData(siteId, dataId, 6, siteInfo);
            if (CollectionUtils.isNotEmpty(systemTreeList)) {
                //查询换电仓设备
                List<SystemTreeDto> changeGranaryList = systemTreeList.stream().filter(s -> StringUtil.isNotEmpty(s.getTypeId()) && "70".equals(s.getTypeId())).collect(Collectors.toList());
                if (CollectionUtils.isNotEmpty(changeGranaryList)) {
                    //根据多个设备id，和多个功能点标识查询设备功能点实时数据
                    String functionLogos = String.join(FileUtil.COMMA, FunctionLogoParamVo.BATTERY_GRANARY_OUT_POWER, FunctionLogoParamVo.BATTERY_GRANARY_SUP_CHARGE);
                    Set<String> deviceIds = changeGranaryList.stream().map(SystemTreeDto::getId).collect(Collectors.toSet());
                    Map<String, Map<String, RealDataModel>> realDataMap = deviceService.getDeviceFunctionsRealDataByIds(deviceIds, functionLogos, 2).getData();
                    //获取设备功能点实时数据
                    Map<String, List<PvSiteMonitorDto.FunctionRalData>> functionRalDataMap = realDataMap.entrySet().stream().flatMap(entry -> entry.getValue().entrySet().stream().map(subEntry -> {
                        PvSiteMonitorDto.FunctionRalData functionRalData = new PvSiteMonitorDto.FunctionRalData();
                        functionRalData.setFunctionLogo(subEntry.getKey());
                        functionRalData.setRealData(subEntry.getValue().getDataValue());
                        return functionRalData;
                    })).collect(Collectors.groupingBy(PvSiteMonitorDto.FunctionRalData::getFunctionLogo));

                    //统计总功率
                    if (functionRalDataMap.containsKey(FunctionLogoParamVo.BATTERY_GRANARY_OUT_POWER) && CollectionUtils.isNotEmpty(functionRalDataMap.get(FunctionLogoParamVo.BATTERY_GRANARY_OUT_POWER))) {
                        result.setTotalPower(getToDouble(functionRalDataMap.get(FunctionLogoParamVo.BATTERY_GRANARY_OUT_POWER).stream().filter(f -> StringUtil.isNotEmpty(f.getRealData())).mapToDouble(f -> Double.parseDouble(String.valueOf(f.getRealData()))).sum()));
                    }
                    //统计总充电量
                    if (functionRalDataMap.containsKey(FunctionLogoParamVo.BATTERY_GRANARY_SUP_CHARGE) && CollectionUtils.isNotEmpty(functionRalDataMap.get(FunctionLogoParamVo.BATTERY_GRANARY_SUP_CHARGE))) {
                        result.setTotalChargeQt(getToDouble(functionRalDataMap.get(FunctionLogoParamVo.BATTERY_GRANARY_SUP_CHARGE).stream().filter(f -> StringUtil.isNotEmpty(f.getRealData())).mapToDouble(f -> Double.parseDouble(String.valueOf(f.getRealData()))).sum()));
                    }
                }
            }
            return ResponseResult.ok(result);
        } catch (RuntimeException e) {
            log.error("查询换电系统数据报错", e);
            return ResponseResult.paramError("查询换电系统数据报错,请稍后重试!!");
        }
    }

    @Override
    public ResponseResult<SystemMeterDto> findSystemMeterData(String dataId) {
        try {
            //返回的对象
            SystemMeterDto result = new SystemMeterDto();
            //根据设备id查询光伏逆变器数据
            DeviceBasicInfoDto deviceBasicInfo = deviceService.findDeviceBasicInfoByIds(Collections.singletonList(dataId)).getData().get(dataId);
            if (deviceBasicInfo != null) {
                BeanUtils.copyProperties(deviceBasicInfo, result);

                //获取设备通信状态
                result.setTxStatus(deviceBasicInfo.getTxStatus());

                Map<String, Object> reaMap = deviceBasicInfo.getReaMap();
                //设备型号
                if (!reaMap.isEmpty() && reaMap.containsKey(ReaFieldParamVo.MODEL) && StringUtil.isNotEmpty(reaMap.get(ReaFieldParamVo.MODEL))) {
                    result.setModel(String.valueOf(reaMap.get(ReaFieldParamVo.MODEL)));
                }
                //生产厂家
                if (!reaMap.isEmpty() && reaMap.containsKey(ReaFieldParamVo.MANUFACTURER_NAME) && StringUtil.isNotEmpty(reaMap.get(ReaFieldParamVo.MANUFACTURER_NAME))) {
                    result.setManufacturerName(String.valueOf(reaMap.get(ReaFieldParamVo.MANUFACTURER_NAME)));
                }

                //获取遥测数据
                List<FunctionDataDto> functionDataList = deviceService.findDeviceFunctionDataList(dataId).getData();
                if (CollectionUtils.isNotEmpty(functionDataList)) {
                    result.setTelemetryDataList(functionDataList.stream().map(functionDataDto -> {
                        TelemetryDataDto telemetryData = new TelemetryDataDto();
                        BeanUtils.copyProperties(functionDataDto, telemetryData);
                        return telemetryData;
                    }).collect(Collectors.toList()));
                }

                //平均功率因数 公式为：cosΦ = W_p / √(W_p² + W_q²)。其中W_p是月有功电量(kWh)，W_q是月无功电量(kvarh)
                //定义有功电量,无功电量
                Double totalActiveQt = null;
                Double totalReactiveQt = null;
                //根据查询条件查询该设备本月的正向总有功电量和正向总无功电量
                DeviceHistoryQueryVo deviceQueryVo = new DeviceHistoryQueryVo();
                deviceQueryVo.setDeviceIds(Collections.singleton(deviceBasicInfo.getId()));
                deviceQueryVo.setFunctionLogos(new HashSet<>(Arrays.asList(FunctionLogoParamVo.TOTAL_POSITIVE_ACTIVE_ENERGY, FunctionLogoParamVo.TOTAL_POSITIVE_REACTIVE_ENERGY)));
                deviceQueryVo.setStartTime(DateUtil.localDateTimeToStr(YearMonth.now().atDay(1).atStartOfDay()));
                deviceQueryVo.setEndTime(DateUtil.localDateTimeToStr(LocalDateTime.now()));
                deviceQueryVo.setTimeInterval("1d");
                Map<String, List<NodeDifHistoryDto>> deviceDataMap = dataService.findNodeDifHistoryListFeign(deviceQueryVo).getData().get(deviceBasicInfo.getId());
                if (MapUtils.isNotEmpty(deviceDataMap)) {
                    //正向有功电能总值
                    if (deviceDataMap.containsKey(FunctionLogoParamVo.TOTAL_POSITIVE_ACTIVE_ENERGY) && CollectionUtils.isNotEmpty(deviceDataMap.get(FunctionLogoParamVo.TOTAL_POSITIVE_ACTIVE_ENERGY))) {
                        List<NodeDifHistoryDto> nodeDifHistoryList = deviceDataMap.get(FunctionLogoParamVo.TOTAL_POSITIVE_ACTIVE_ENERGY).stream().filter(d -> StringUtil.isNotEmpty(d.getFirstDataValue())
                                && StringUtil.isNotEmpty(d.getLastDataValue())).collect(Collectors.toList());
                        totalActiveQt = nodeDifHistoryList.stream().mapToDouble(n -> Double.parseDouble(String.valueOf(n.getLastDataValue())) - Double.parseDouble(String.valueOf(n.getFirstDataValue()))).sum();
                    }
                    //正向无功电能总值
                    if (deviceDataMap.containsKey(FunctionLogoParamVo.TOTAL_POSITIVE_REACTIVE_ENERGY) && CollectionUtils.isNotEmpty(deviceDataMap.get(FunctionLogoParamVo.TOTAL_POSITIVE_REACTIVE_ENERGY))) {
                        totalReactiveQt = deviceDataMap.get(FunctionLogoParamVo.TOTAL_POSITIVE_REACTIVE_ENERGY).stream()
                                .filter(d -> StringUtil.isNotEmpty(d.getFirstDataValue()) && StringUtil.isNotEmpty(d.getLastDataValue()))
                                .mapToDouble(n -> Double.parseDouble(String.valueOf(n.getLastDataValue())) - Double.parseDouble(String.valueOf(n.getFirstDataValue())))
                                .sum();
                    }
                }
                //计算平均功率因数 公式为：cosΦ = W_p / √(W_p² + W_q²)。其中W_p是月有功电量(kWh)，W_q是月无功电量(kvarh)
                if (totalActiveQt != null && totalReactiveQt != null && StringUtil.isNotEmpty(totalActiveQt) && StringUtil.isNotEmpty(totalReactiveQt)) {
                    result.setAveragePowerFactor(getToDouble(totalActiveQt / Math.sqrt(totalActiveQt * totalActiveQt + totalReactiveQt * totalReactiveQt) * 100));
                }

                //获取设备功能点实时数据(三相电压,三相电流,三相有功功率)
                Double aPhaseCurrent = null;
                Double bPhaseCurrent = null;
                Double cPhaseCurrent = null;
                Double aPhaseVoltage = null;
                Double bPhaseVoltage = null;
                Double cPhaseVoltage = null;
                Double aPhasePower = null;
                Double bPhasePower = null;
                Double cPhasePower = null;
                String functionLogos = String.join(FileUtil.COMMA, FunctionLogoParamVo.A_PHASE_CURRENT, FunctionLogoParamVo.B_PHASE_CURRENT, FunctionLogoParamVo.C_PHASE_CURRENT,
                        FunctionLogoParamVo.A_PHASE_VOLTAGE, FunctionLogoParamVo.B_PHASE_VOLTAGE, FunctionLogoParamVo.C_PHASE_VOLTAGE,
                        FunctionLogoParamVo.A_PHASE_ACTIVE_POWER, FunctionLogoParamVo.B_PHASE_ACTIVE_POWER, FunctionLogoParamVo.C_PHASE_ACTIVE_POWER);
                Map<String, RealDataModel> realDataModelMap = deviceService.getDeviceFunctionsRealDataByIds(Collections.singleton(deviceBasicInfo.getId()),
                        functionLogos, 2).getData().get(deviceBasicInfo.getId());
                if (MapUtils.isNotEmpty(realDataModelMap)) {
                    //A相电流
                    if (realDataModelMap.containsKey(FunctionLogoParamVo.A_PHASE_CURRENT) && StringUtil.isNotEmpty(realDataModelMap.get(FunctionLogoParamVo.A_PHASE_CURRENT))) {
                        aPhaseCurrent = DoubleUtil.objToDouble(realDataModelMap.get(FunctionLogoParamVo.A_PHASE_CURRENT).getDataValue());
                    }
                    //B相电流
                    if (realDataModelMap.containsKey(FunctionLogoParamVo.B_PHASE_CURRENT) && StringUtil.isNotEmpty(realDataModelMap.get(FunctionLogoParamVo.B_PHASE_CURRENT))) {
                        bPhaseCurrent = DoubleUtil.objToDouble(realDataModelMap.get(FunctionLogoParamVo.B_PHASE_CURRENT).getDataValue());
                    }
                    //C相电流
                    if (realDataModelMap.containsKey(FunctionLogoParamVo.C_PHASE_CURRENT) && StringUtil.isNotEmpty(realDataModelMap.get(FunctionLogoParamVo.C_PHASE_CURRENT))) {
                        cPhaseCurrent = DoubleUtil.objToDouble(realDataModelMap.get(FunctionLogoParamVo.C_PHASE_CURRENT).getDataValue());
                    }
                    //A相电压
                    if (realDataModelMap.containsKey(FunctionLogoParamVo.A_PHASE_VOLTAGE) && StringUtil.isNotEmpty(realDataModelMap.get(FunctionLogoParamVo.A_PHASE_VOLTAGE))) {
                        aPhaseVoltage = DoubleUtil.objToDouble(realDataModelMap.get(FunctionLogoParamVo.A_PHASE_VOLTAGE).getDataValue());
                    }
                    //B相电压
                    if (realDataModelMap.containsKey(FunctionLogoParamVo.B_PHASE_VOLTAGE) && StringUtil.isNotEmpty(realDataModelMap.get(FunctionLogoParamVo.B_PHASE_VOLTAGE))) {
                        bPhaseVoltage = DoubleUtil.objToDouble(realDataModelMap.get(FunctionLogoParamVo.B_PHASE_VOLTAGE).getDataValue());
                    }
                    //C相电压
                    if (realDataModelMap.containsKey(FunctionLogoParamVo.C_PHASE_VOLTAGE) && StringUtil.isNotEmpty(realDataModelMap.get(FunctionLogoParamVo.C_PHASE_VOLTAGE))) {
                        cPhaseVoltage = DoubleUtil.objToDouble(realDataModelMap.get(FunctionLogoParamVo.C_PHASE_VOLTAGE).getDataValue());
                    }
                    //A相功率
                    if (realDataModelMap.containsKey(FunctionLogoParamVo.A_PHASE_ACTIVE_POWER) && StringUtil.isNotEmpty(realDataModelMap.get(FunctionLogoParamVo.A_PHASE_ACTIVE_POWER))) {
                        aPhasePower = DoubleUtil.objToDouble(realDataModelMap.get(FunctionLogoParamVo.A_PHASE_ACTIVE_POWER).getDataValue());
                    }
                    //B相功率
                    if (realDataModelMap.containsKey(FunctionLogoParamVo.B_PHASE_ACTIVE_POWER) && StringUtil.isNotEmpty(realDataModelMap.get(FunctionLogoParamVo.B_PHASE_ACTIVE_POWER))) {
                        bPhasePower = DoubleUtil.objToDouble(realDataModelMap.get(FunctionLogoParamVo.B_PHASE_ACTIVE_POWER).getDataValue());
                    }
                    //C相功率
                    if (realDataModelMap.containsKey(FunctionLogoParamVo.C_PHASE_ACTIVE_POWER) && StringUtil.isNotEmpty(realDataModelMap.get(FunctionLogoParamVo.C_PHASE_ACTIVE_POWER))) {
                        cPhasePower = DoubleUtil.objToDouble(realDataModelMap.get(FunctionLogoParamVo.C_PHASE_ACTIVE_POWER).getDataValue());
                    }
                }

                //计算三相电流/电压不平衡度
                //计算步骤:
                //(1)分别读取A、B、C三相的电压(Ua、Ub、Uc)或电流(la、lb、lc);
                //(2)计算平均值: Uavg = (Ua+Ub+Uc)/3
                //(3)计算最大偏差值:Umax= MAX(|Ua-Uavg|,|Ub-Uavg|,|Uc-Uavg|)
                //(4)计算三相不平衡度 不平衡度 = 最大偏差值/平均值 * 100%
                if (aPhaseCurrent != null && bPhaseCurrent != null && cPhaseCurrent != null) {
                    Double avgValue = (aPhaseCurrent + bPhaseCurrent + cPhaseCurrent) / 3;
                    Double maxValue = Math.max(Math.abs(aPhaseCurrent - avgValue), Math.max(Math.abs(bPhaseCurrent - avgValue), Math.abs(cPhaseCurrent - avgValue)));
                    result.setThreePhaseCurImbalance(getToDouble(maxValue / avgValue * 100));
                }
                if (aPhaseVoltage != null && bPhaseVoltage != null && cPhaseVoltage != null) {
                    Double avgValue = (aPhaseVoltage + bPhaseVoltage + cPhaseVoltage) / 3;
                    Double maxValue = Math.max(Math.abs(aPhaseVoltage - avgValue), Math.max(Math.abs(bPhaseVoltage - avgValue), Math.abs(cPhaseVoltage - avgValue)));
                    result.setThreePhaseVoltImbalance(getToDouble(maxValue / avgValue * 100));
                }

                //计算功率平衡度 min(Pa, Pb, Pc)/max(Pa, Pb, Pc) * 100%
                if (aPhasePower != null && bPhasePower != null && cPhasePower != null) {
                    Double minValue = Math.min(aPhasePower, Math.min(bPhasePower, cPhasePower));
                    Double maxValue = Math.max(aPhasePower, Math.max(bPhasePower, cPhasePower));
                    result.setPowerBalance(getToDouble(minValue / maxValue * 100));
                }
            }
            return ResponseResult.ok(result);
        } catch (RuntimeException e) {
            log.error("查询电表静态数据报错", e);
            return ResponseResult.paramError("查询电表静态数据报错,请稍后重试!!");
        }
    }

    /**
     * 封装系统数据信息
     *
     * @param siteId 站点id
     * @param dataId 数据id
     * @param type   类型 1-光伏监控 2-储能监控 3-电桩监控 4-用能系统 5-配电系统 6-换电系统
     * @return 系统数据信息
     */
    private List<SystemTreeDto> getSystemMonitorData(String siteId, String dataId, Integer type, SiteInfoDto siteInfo) {
        //定义集合
        List<SystemTreeDto> systemTreeList = Lists.newArrayList();

        //根节点名称
        String parentName;
        //能源类型 1-光伏 2-储能 3-充电桩 4-用能 5-配电 6-换电
        int scenarioType;
        //类型 1-光伏监控 2-储能监控 3-电桩监控 4-变配电系统
        switch (type) {
            case 1:
                parentName = "光伏系统";
                scenarioType = 1;
                break;
            case 2:
                parentName = "储能系统";
                scenarioType = 2;
                break;
            case 3:
                parentName = "电桩系统";
                scenarioType = 3;
                break;
            case 4:
                parentName = "用能系统";
                scenarioType = 4;
                break;
            case 5:
                parentName = "配电系统";
                scenarioType = 5;
                break;
            case 6:
                parentName = "换电系统";
                scenarioType = 6;
                break;
            default:
                return systemTreeList;
        }
        if (StringUtil.isEmpty(siteInfo)) {
            //根据站点id查询站点基本信息
            siteInfo = deviceService.findSiteBasicInfoByIds(Collections.singletonList(siteId)).getData().get(siteId);
        }
        if (siteInfo != null) {
            //默认添加根节点系统
            systemTreeList.add(SystemTreeDto.builder().id(siteId).code(siteInfo.getSiteCode()).name(parentName).type(type).levelType(1).typeId("1").build());

            if (CollectionUtils.isNotEmpty(siteInfo.getSiteScenarioTypeDtos())) {
                //根据场景类型查询站点场景类型信息
                List<SiteScenarioTypeDto> scenarioTypeList = siteInfo.getSiteScenarioTypeDtos().stream().filter(siteScenarioType -> Objects.equals(siteScenarioType.getScenarioType(), scenarioType)).collect(Collectors.toList());
                if (CollectionUtils.isNotEmpty(scenarioTypeList)) {
                    //添加子节点系统
                    systemTreeList.addAll(scenarioTypeList.stream().map(siteScenarioType -> SystemTreeDto.builder().id(siteScenarioType.getId()).name(siteScenarioType.getSystemName()).type(type).levelType(2).parentId(siteId).build()).collect(Collectors.toList()));
                }

                //根据站点id和类型查询设备基本信息
                List<String> parentIds = scenarioTypeList.stream().map(SiteScenarioTypeDto::getId).collect(Collectors.toList());
                List<DeviceBasicInfoDto> deviceDataList = deviceService.findDeviceInfoByParentIds(parentIds).getData().values().stream()
                        .flatMap(Collection::stream).collect(Collectors.toList());
                //如果设备基本信息列表不为空
                if (CollectionUtils.isNotEmpty(deviceDataList)) {
                    List<SystemTreeDto> deviceList = deviceDataList.stream().map(device -> SystemTreeDto.builder().id(device.getId()).code(device.getDeviceNumber()).name(device.getDeviceName()).type(type).levelType(3).parentId(device.getParentId()).typeId(device.getTypeId()).build()).collect(Collectors.toList());
                    scenarioTypeList.forEach(siteScenarioType -> systemTreeList.addAll(this.getChildren(siteScenarioType.getId(), deviceList)));
                }
            }
        }
        List<SystemTreeDto> resultList = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(systemTreeList) && StringUtil.isNotEmpty(dataId)) {
            resultList.addAll(systemTreeList.stream().filter(s -> Objects.equals(s.getId(), dataId)).collect(Collectors.toList()));
            resultList.addAll(this.getChildren(dataId, systemTreeList));
        }
        return resultList;
    }

    private List<SystemTreeDto> getChildren(String id, List<SystemTreeDto> childrenList) {
        List<SystemTreeDto> result = Lists.newArrayList();
        for (SystemTreeDto menu : childrenList) {
            if (id.equals(menu.getParentId())) {
                result.add(menu);
                result.addAll(getChildren(menu.getId(), childrenList));
            }
        }
        return result;
    }

    //获取光伏系统功率曲线数据
    private SystemCurveDto getSystemPvPowerCurve(String dataId, String startTime, String endTime, String timeInterval, Integer formatInterval, Set<String> dateList) {
        //返回的对象
        SystemCurveDto result = new SystemCurveDto();

        result.setDateList(dateList);

        //存储数据列表
        List<SystemCurveDto.DataInfo> dataInfoList = Lists.newArrayList();

        //光伏系统-实际功率
        SystemCurveDto.DataInfo actualPower = new SystemCurveDto.DataInfo();
        actualPower.setName(SystemCurveEnum.ACTUAL_POWER.getName());
        actualPower.setCode(SystemCurveEnum.ACTUAL_POWER.getCode());
        List<Object> actualPowerList = Lists.newArrayList();

        //光伏系统-理论功率
        SystemCurveDto.DataInfo theoryPowerInfo = new SystemCurveDto.DataInfo();
        theoryPowerInfo.setName(SystemCurveEnum.THEORY_POWER.getName());
        theoryPowerInfo.setCode(SystemCurveEnum.THEORY_POWER.getCode());
        List<Object> theoryPowerList = Lists.newArrayList();

        //根据数据id查询站点id
        String siteId = deviceService.findSiteIdBySubId(dataId).getData();
        //获取光伏逆变器和光伏DC/DC设备
        List<SystemTreeDto> systemTreeList = this.getSystemMonitorData(siteId, dataId, 1, null).stream().filter(s -> StringUtil.isNotEmpty(s.getTypeId())
                && (Objects.equals(s.getTypeId(), "20") || Objects.equals(s.getTypeId(), "77"))).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(systemTreeList)) {
            Set<String> deviceIds = systemTreeList.stream().map(SystemTreeDto::getId).collect(Collectors.toSet());
            //查询功率数据
            DeviceHistoryQueryVo deviceHistoryQueryVo = new DeviceHistoryQueryVo();
            deviceHistoryQueryVo.setDeviceIds(deviceIds);
            deviceHistoryQueryVo.setFunctionLogos(Collections.singleton(FunctionLogoParamVo.ACTIVE_POWER));
            deviceHistoryQueryVo.setStartTime(startTime);
            deviceHistoryQueryVo.setEndTime(endTime);
            deviceHistoryQueryVo.setTimeInterval(timeInterval);
            //查询逆变器功能点历史数据
            Map<String, Map<String, List<DeviceHistoryDto>>> deviceHistoryMap = dataService.findDeviceHistoryValueList(deviceHistoryQueryVo).getData();
            if (MapUtils.isNotEmpty(deviceHistoryMap)) {
                // 存储所有历史数据
                List<DeviceHistoryDto> deviceHistoryList = deviceHistoryMap.values().stream().flatMap(d -> d.values().stream().flatMap(Collection::stream)).collect(Collectors.toList());

                if (CollectionUtils.isNotEmpty(deviceHistoryList)) {
                    //根据时间分组
                    Map<String, Double> actualPowerMap = deviceHistoryList.stream().filter(c -> StringUtil.isNotEmpty(c.getDateTime())).collect(Collectors.groupingBy(c -> {
                        String dateFormat = format(strToDate(c.getDateTime()), getFormatIntervalPattern(formatInterval));
                        if (StringUtil.isNotEmpty(dateFormat)) {
                            return dateFormat;
                        }
                        return c.getDateTime();
                    }, Collectors.summingDouble(d -> DoubleUtil.objToDouble(d.getDataValue()))));
                    dateList.forEach(dateTime -> {
                        //光伏系统-实际功率
                        if (actualPowerMap.containsKey(dateTime)) {
                            actualPowerList.add(getToDouble(actualPowerMap.get(dateTime)));
                        } else {
                            actualPowerList.add(null);
                        }
                    });
                    actualPower.setDataList(actualPowerList);
                    theoryPowerInfo.setDataList(theoryPowerList);
                }
            }
        }
        dataInfoList.add(actualPower);
        dataInfoList.add(theoryPowerInfo);
        result.setDataInfoList(dataInfoList);
        return result;
    }

    //获取光伏系统实际发电量曲线数据
    private SystemCurveDto getSystemPvQtCurve(String dataId, String startTime, String endTime, String timeInterval, Integer formatInterval, Set<String> dateList) {
        //返回的对象
        SystemCurveDto result = new SystemCurveDto();

        result.setDateList(dateList);

        //存储数据列表
        List<SystemCurveDto.DataInfo> dataInfoList = Lists.newArrayList();

        //实际发电量
        SystemCurveDto.DataInfo pvActualQt = new SystemCurveDto.DataInfo();
        pvActualQt.setName(SystemCurveEnum.ACTUAL_QT.getName());
        pvActualQt.setCode(SystemCurveEnum.ACTUAL_QT.getCode());
        List<Object> pvActualQtList = Lists.newArrayList();

        //根据数据id查询站点id
        String siteId = deviceService.findSiteIdBySubId(dataId).getData();
        //获取光伏逆变器和光伏DC/DC设备
        List<SystemTreeDto> systemTreeList = this.getSystemMonitorData(siteId, dataId, 1, null).stream().filter(s -> StringUtil.isNotEmpty(s.getTypeId())
                && (Objects.equals(s.getTypeId(), "20") || Objects.equals(s.getTypeId(), "77"))).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(systemTreeList)) {
            Set<String> deviceIds = systemTreeList.stream().map(SystemTreeDto::getId).collect(Collectors.toSet());
            //查询功率数据
            DeviceHistoryQueryVo deviceHistoryQueryVo = new DeviceHistoryQueryVo();
            deviceHistoryQueryVo.setDeviceIds(deviceIds);
            deviceHistoryQueryVo.setFunctionLogos(Collections.singleton(FunctionLogoParamVo.TOTAL_POWER_GENERATION));
            deviceHistoryQueryVo.setStartTime(startTime);
            deviceHistoryQueryVo.setEndTime(endTime);
            deviceHistoryQueryVo.setTimeInterval(timeInterval);
            //查询逆变器功能点历史数据
            Map<String, Map<String, List<NodeDifHistoryDto>>> deviceHistoryMap = dataService.findNodeDifHistoryListFeign(deviceHistoryQueryVo).getData();
            if (MapUtils.isNotEmpty(deviceHistoryMap)) {
                // 存储所有历史数据
                List<NodeDifHistoryDto> deviceHistoryList = deviceHistoryMap.values().stream().flatMap(d -> d.values().stream().flatMap(Collection::stream)).collect(Collectors.toList());

                if (CollectionUtils.isNotEmpty(deviceHistoryList)) {
                    //根据时间分组
                    Map<String, Double> deviceDataMap = deviceHistoryList.stream().filter(c -> StringUtil.isNotEmpty(c.getFirstDateTime())).collect(Collectors.groupingBy(c -> {
                        String dateFormat = format(strToDate(c.getFirstDateTime()), getFormatIntervalPattern(formatInterval));
                        if (StringUtil.isNotEmpty(dateFormat)) {
                            return dateFormat;
                        }
                        return c.getFirstDateTime();
                    }, Collectors.summingDouble(d -> DoubleUtil.getObjSub(d.getFirstDataValue(), d.getLastDataValue()))));
                    dateList.forEach(dateTime -> {
                        //实际发电量
                        if (deviceDataMap.containsKey(dateTime)) {
                            pvActualQtList.add(getToDouble(deviceDataMap.get(dateTime)));
                        } else {
                            pvActualQtList.add(null);
                        }
                    });
                    pvActualQt.setDataList(pvActualQtList);
                }
            }
        }
        dataInfoList.add(pvActualQt);
        result.setDataInfoList(dataInfoList);
        return result;
    }

    //获取光伏逆变器有功功率和无功功率曲线数据
    private SystemCurveDto getPvInverterPowerCurve(String dataId, String startTime, String endTime, String timeInterval, Integer formatInterval, Set<String> dateList) {
        //返回的对象
        SystemCurveDto result = new SystemCurveDto();

        result.setDateList(dateList);

        //存储数据列表
        List<SystemCurveDto.DataInfo> dataInfoList = Lists.newArrayList();

        //光伏逆变器-有功功率
        SystemCurveDto.DataInfo activePower = new SystemCurveDto.DataInfo();
        activePower.setName(SystemCurveEnum.ACTIVE_POWER.getName());
        activePower.setCode(SystemCurveEnum.ACTIVE_POWER.getCode());
        List<Object> activePowerList = Lists.newArrayList();

        //光伏逆变器-无功功率
        SystemCurveDto.DataInfo reactivePower = new SystemCurveDto.DataInfo();
        reactivePower.setName(SystemCurveEnum.REACTIVE_POWER.getName());
        reactivePower.setCode(SystemCurveEnum.REACTIVE_POWER.getCode());
        List<Object> reactivePowerList = Lists.newArrayList();

        //根据数据id查询光伏逆变器数据
        DeviceBasicInfoDto deviceBasicInfo = deviceService.findDeviceBasicInfoByIds(Collections.singletonList(dataId)).getData().get(dataId);
        if (deviceBasicInfo != null) {
            //查询功率数据
            DeviceHistoryQueryVo deviceHistoryQueryVo = new DeviceHistoryQueryVo();
            deviceHistoryQueryVo.setDeviceIds(Collections.singleton(dataId));
            deviceHistoryQueryVo.setFunctionLogos(new HashSet<>(Arrays.asList(FunctionLogoParamVo.ACTIVE_POWER, FunctionLogoParamVo.REACTIVE_POWER)));
            deviceHistoryQueryVo.setStartTime(startTime);
            deviceHistoryQueryVo.setEndTime(endTime);
            deviceHistoryQueryVo.setTimeInterval(timeInterval);
            //查询逆变器功能点历史数据
            Map<String, Map<String, List<DeviceHistoryDto>>> deviceHistoryMap = dataService.findDeviceHistoryValueList(deviceHistoryQueryVo).getData();
            if (MapUtils.isNotEmpty(deviceHistoryMap) && deviceHistoryMap.containsKey(dataId)) {
                // 存储所有历史数据
                Map<String, List<DeviceHistoryDto>> functionDataMap = deviceHistoryMap.get(dataId);

                if (MapUtils.isNotEmpty(functionDataMap)) {
                    Map<String, Double> activePowerMap = Maps.newHashMap();
                    Map<String, Double> reactivePowerMap = Maps.newHashMap();
                    //有功功率数据
                    if (functionDataMap.containsKey(FunctionLogoParamVo.ACTIVE_POWER)) {
                        //根据时间分组
                        activePowerMap = functionDataMap.get(FunctionLogoParamVo.ACTIVE_POWER).stream().filter(c -> StringUtil.isNotEmpty(c.getDateTime())).collect(Collectors.groupingBy(c -> {
                            String dateFormat = format(strToDate(c.getDateTime()), getFormatIntervalPattern(formatInterval));
                            if (StringUtil.isNotEmpty(dateFormat)) {
                                return dateFormat;
                            }
                            return c.getDateTime();
                        }, Collectors.summingDouble(d -> DoubleUtil.objToDouble(d.getDataValue()))));
                    }
                    //无功功率数据
                    if (functionDataMap.containsKey(FunctionLogoParamVo.REACTIVE_POWER)) {
                        //根据时间分组
                        reactivePowerMap = functionDataMap.get(FunctionLogoParamVo.REACTIVE_POWER).stream().filter(c -> StringUtil.isNotEmpty(c.getDateTime())).collect(Collectors.groupingBy(c -> {
                            String dateFormat = format(strToDate(c.getDateTime()), getFormatIntervalPattern(formatInterval));
                            if (StringUtil.isNotEmpty(dateFormat)) {
                                return dateFormat;
                            }
                            return c.getDateTime();
                        }, Collectors.summingDouble(d -> DoubleUtil.objToDouble(d.getDataValue()))));
                    }

                    for (String dateTime : dateList) {
                        //有功功率
                        if (activePowerMap.containsKey(dateTime)) {
                            activePowerList.add(getToDouble(activePowerMap.get(dateTime)));
                        } else {
                            activePowerList.add(null);
                        }
                        //无功功率
                        if (reactivePowerMap.containsKey(dateTime)) {
                            reactivePowerList.add(getToDouble(reactivePowerMap.get(dateTime)));
                        } else {
                            reactivePowerList.add(null);
                        }
                    }

                    activePower.setDataList(activePowerList);
                    reactivePower.setDataList(reactivePowerList);
                }
            }
        }
        dataInfoList.add(activePower);
        dataInfoList.add(reactivePower);
        result.setDataInfoList(dataInfoList);
        return result;
    }

    //获取光伏逆变器实际发电量曲线数据
    private SystemCurveDto getPvInverterQtCurve(String dataId, String startTime, String endTime, String timeInterval, Integer formatInterval, Set<String> dateList) {
        //返回的对象
        SystemCurveDto result = new SystemCurveDto();

        result.setDateList(dateList);

        //存储数据列表
        List<SystemCurveDto.DataInfo> dataInfoList = Lists.newArrayList();

        //实际发电量
        SystemCurveDto.DataInfo actualQt = new SystemCurveDto.DataInfo();
        actualQt.setName(SystemCurveEnum.ACTUAL_QT.getName());
        actualQt.setCode(SystemCurveEnum.ACTUAL_QT.getCode());
        List<Object> actualQtList = Lists.newArrayList();

        //根据数据id查询光伏逆变器数据
        DeviceBasicInfoDto deviceBasicInfo = deviceService.findDeviceBasicInfoByIds(Collections.singletonList(dataId)).getData().get(dataId);
        if (deviceBasicInfo != null) {
            //查询逆变器功能点历史数据
            DeviceHistoryQueryVo deviceHistoryQueryVo = new DeviceHistoryQueryVo();
            deviceHistoryQueryVo.setDeviceIds(Collections.singleton(dataId));
            deviceHistoryQueryVo.setFunctionLogos(Collections.singleton(FunctionLogoParamVo.TOTAL_POWER_GENERATION));
            deviceHistoryQueryVo.setStartTime(startTime);
            deviceHistoryQueryVo.setEndTime(endTime);
            deviceHistoryQueryVo.setTimeInterval(timeInterval);
            Map<String, Map<String, List<NodeDifHistoryDto>>> deviceHistoryMap = dataService.findNodeDifHistoryListFeign(deviceHistoryQueryVo).getData();
            if (MapUtils.isNotEmpty(deviceHistoryMap) && deviceHistoryMap.containsKey(dataId)) {
                // 存储所有历史数据
                Map<String, List<NodeDifHistoryDto>> functionDataMap = deviceHistoryMap.get(dataId);

                if (MapUtils.isNotEmpty(functionDataMap)) {
                    Map<String, Double> actualQtMap = Maps.newHashMap();
                    //光伏逆变器实际发电量
                    if (functionDataMap.containsKey(FunctionLogoParamVo.TOTAL_POWER_GENERATION)) {
                        //根据时间分组
                        actualQtMap = functionDataMap.get(FunctionLogoParamVo.TOTAL_POWER_GENERATION).stream().filter(c -> StringUtil.isNotEmpty(c.getFirstDateTime())).collect(Collectors.groupingBy(c -> {
                            String dateFormat = format(strToDate(c.getFirstDateTime()), getFormatIntervalPattern(formatInterval));
                            if (StringUtil.isNotEmpty(dateFormat)) {
                                return dateFormat;
                            }
                            return c.getFirstDateTime();
                        }, Collectors.summingDouble(d -> DoubleUtil.getObjSub(d.getFirstDataValue(), d.getLastDataValue()))));
                    }

                    for (String dateTime : dateList) {
                        //光伏逆变器实际发电量
                        if (actualQtMap.containsKey(dateTime)) {
                            actualQtList.add(getToDouble(actualQtMap.get(dateTime)));
                        } else {
                            actualQtList.add(null);
                        }
                    }

                    actualQt.setDataList(actualQtList);
                }
            }
        }
        dataInfoList.add(actualQt);
        result.setDataInfoList(dataInfoList);
        return result;
    }

    //获取光伏气象站辐照度曲线数据
    private SystemCurveDto getPvWeatherRadiationCurve(String dataId, String startTime, String endTime, String timeInterval, Integer formatInterval, Set<String> dateList) {
        //返回的对象
        SystemCurveDto result = new SystemCurveDto();

        result.setDateList(dateList);

        //存储数据列表
        List<SystemCurveDto.DataInfo> dataInfoList = Lists.newArrayList();

        //光伏气象站-水平辐射值
        SystemCurveDto.DataInfo horizontalRadiation = new SystemCurveDto.DataInfo();
        horizontalRadiation.setName(SystemCurveEnum.HORIZONTAL_RADIATION.getName());
        horizontalRadiation.setCode(SystemCurveEnum.HORIZONTAL_RADIATION.getCode());
        List<Object> horizontalRadiationList = Lists.newArrayList();

        //光伏气象站-倾斜辐射值
        SystemCurveDto.DataInfo inclinedRadiation = new SystemCurveDto.DataInfo();
        inclinedRadiation.setName(SystemCurveEnum.INCLINED_RADIATION.getName());
        inclinedRadiation.setCode(SystemCurveEnum.INCLINED_RADIATION.getCode());
        List<Object> inclinedRadiationList = Lists.newArrayList();

        //根据数据id查询光伏气象站数据
        DeviceBasicInfoDto deviceBasicInfo = deviceService.findDeviceBasicInfoByIds(Collections.singletonList(dataId)).getData().get(dataId);
        if (deviceBasicInfo != null) {
            //查询逆变器功能点历史数据
            DeviceHistoryQueryVo deviceHistoryQueryVo = new DeviceHistoryQueryVo();
            deviceHistoryQueryVo.setDeviceIds(Collections.singleton(dataId));
            deviceHistoryQueryVo.setFunctionLogos(new HashSet<>(Arrays.asList(FunctionLogoParamVo.PV_GHI, FunctionLogoParamVo.PV_BEVEL_GHI)));
            deviceHistoryQueryVo.setStartTime(startTime);
            deviceHistoryQueryVo.setEndTime(endTime);
            deviceHistoryQueryVo.setTimeInterval(timeInterval);
            Map<String, Map<String, List<DeviceHistoryDto>>> deviceHistoryMap = dataService.findDeviceHistoryValueList(deviceHistoryQueryVo).getData();
            if (MapUtils.isNotEmpty(deviceHistoryMap) && deviceHistoryMap.containsKey(dataId)) {
                // 存储所有历史数据
                Map<String, List<DeviceHistoryDto>> functionDataMap = deviceHistoryMap.get(dataId);

                if (MapUtils.isNotEmpty(functionDataMap)) {
                    Map<String, Double> horizontalRadiationMap = Maps.newHashMap();
                    Map<String, Double> inclinedRadiationMap = Maps.newHashMap();
                    //光伏气象站-水平总辐射
                    if (functionDataMap.containsKey(FunctionLogoParamVo.PV_GHI)) {
                        //根据时间分组
                        horizontalRadiationMap = functionDataMap.get(FunctionLogoParamVo.PV_GHI).stream().filter(c -> StringUtil.isNotEmpty(c.getDateTime())).collect(Collectors.groupingBy(c -> {
                            String dateFormat = format(strToDate(c.getDateTime()), getFormatIntervalPattern(formatInterval));
                            if (StringUtil.isNotEmpty(dateFormat)) {
                                return dateFormat;
                            }
                            return c.getDateTime();
                        }, Collectors.summingDouble(d -> DoubleUtil.objToDouble(d.getDataValue()))));
                    }
                    //光伏气象站-倾斜总辐射
                    if (functionDataMap.containsKey(FunctionLogoParamVo.PV_BEVEL_GHI)) {
                        //根据时间分组
                        inclinedRadiationMap = functionDataMap.get(FunctionLogoParamVo.PV_BEVEL_GHI).stream().filter(c -> StringUtil.isNotEmpty(c.getDateTime())).collect(Collectors.groupingBy(c -> {
                            String dateFormat = format(strToDate(c.getDateTime()), getFormatIntervalPattern(formatInterval));
                            if (StringUtil.isNotEmpty(dateFormat)) {
                                return dateFormat;
                            }
                            return c.getDateTime();
                        }, Collectors.summingDouble(d -> DoubleUtil.objToDouble(d.getDataValue()))));
                    }

                    for (String dateTime : dateList) {
                        //光伏气象站-水平总辐射
                        if (horizontalRadiationMap.containsKey(dateTime)) {
                            horizontalRadiationList.add(getToDouble(horizontalRadiationMap.get(dateTime)));
                        } else {
                            horizontalRadiationList.add(null);
                        }
                        //光伏气象站-倾斜总辐射
                        if (inclinedRadiationMap.containsKey(dateTime)) {
                            inclinedRadiationList.add(getToDouble(inclinedRadiationMap.get(dateTime)));
                        } else {
                            inclinedRadiationList.add(null);
                        }
                    }

                    horizontalRadiation.setDataList(horizontalRadiationList);
                    inclinedRadiation.setDataList(inclinedRadiationList);
                }
            }
        }
        dataInfoList.add(horizontalRadiation);
        dataInfoList.add(inclinedRadiation);
        result.setDataInfoList(dataInfoList);
        return result;
    }

    //获取光伏气象站辐照度曲线数据
    private SystemCurveDto getPvWeatherTemperatureCurve(String dataId, String startTime, String endTime, String timeInterval, Integer formatInterval, Set<String> dateList) {
        //返回的对象
        SystemCurveDto result = new SystemCurveDto();

        result.setDateList(dateList);

        //存储数据列表
        List<SystemCurveDto.DataInfo> dataInfoList = Lists.newArrayList();

        //光伏气象站-环境温度
        SystemCurveDto.DataInfo ambientTemperature = new SystemCurveDto.DataInfo();
        ambientTemperature.setName(SystemCurveEnum.AMBIENT_TEMPERATURE.getName());
        ambientTemperature.setCode(SystemCurveEnum.AMBIENT_TEMPERATURE.getCode());
        List<Object> ambientTemperatureList = Lists.newArrayList();

        //根据数据id查询光伏气象站数据
        DeviceBasicInfoDto deviceBasicInfo = deviceService.findDeviceBasicInfoByIds(Collections.singletonList(dataId)).getData().get(dataId);
        if (deviceBasicInfo != null) {
            //查询逆变器功能点历史数据
            DeviceHistoryQueryVo deviceHistoryQueryVo = new DeviceHistoryQueryVo();
            deviceHistoryQueryVo.setDeviceIds(Collections.singleton(dataId));
            deviceHistoryQueryVo.setFunctionLogos(Collections.singleton(FunctionLogoParamVo.PV_TEMPERATURE));
            deviceHistoryQueryVo.setStartTime(startTime);
            deviceHistoryQueryVo.setEndTime(endTime);
            deviceHistoryQueryVo.setTimeInterval(timeInterval);
            Map<String, Map<String, List<DeviceHistoryDto>>> deviceHistoryMap = dataService.findDeviceHistoryValueList(deviceHistoryQueryVo).getData();
            if (MapUtils.isNotEmpty(deviceHistoryMap) && deviceHistoryMap.containsKey(dataId)) {
                // 存储所有历史数据
                Map<String, List<DeviceHistoryDto>> functionDataMap = deviceHistoryMap.get(dataId);

                if (MapUtils.isNotEmpty(functionDataMap)) {
                    Map<String, Double> temperatureMap = Maps.newHashMap();
                    //光伏气象站-环境温度
                    if (functionDataMap.containsKey(FunctionLogoParamVo.PV_TEMPERATURE)) {
                        //根据时间分组
                        temperatureMap = functionDataMap.get(FunctionLogoParamVo.PV_TEMPERATURE).stream().filter(c -> StringUtil.isNotEmpty(c.getDateTime())).collect(Collectors.groupingBy(c -> {
                            String dateFormat = format(strToDate(c.getDateTime()), getFormatIntervalPattern(formatInterval));
                            if (StringUtil.isNotEmpty(dateFormat)) {
                                return dateFormat;
                            }
                            return c.getDateTime();
                        }, Collectors.summingDouble(d -> DoubleUtil.objToDouble(d.getDataValue()))));
                    }

                    for (String dateTime : dateList) {
                        //光伏气象站-环境温度
                        if (temperatureMap.containsKey(dateTime)) {
                            ambientTemperatureList.add(getToDouble(temperatureMap.get(dateTime)));
                        } else {
                            ambientTemperatureList.add(null);
                        }
                    }

                    ambientTemperature.setDataList(ambientTemperatureList);
                }
            }
        }
        dataInfoList.add(ambientTemperature);
        result.setDataInfoList(dataInfoList);
        return result;
    }

    //获取光伏气象站辐照度曲线数据
    private SystemCurveDto getPvWeatherIrradiationCurve(String dataId, String startTime, String endTime, String timeInterval, Integer formatInterval, Set<String> dateList) {
        //返回的对象
        SystemCurveDto result = new SystemCurveDto();

        result.setDateList(dateList);

        //存储数据列表
        List<SystemCurveDto.DataInfo> dataInfoList = Lists.newArrayList();

        //光伏气象站-水平总辐照量
        SystemCurveDto.DataInfo radiantExposure = new SystemCurveDto.DataInfo();
        radiantExposure.setName(SystemCurveEnum.RADIANT_EXPOSURE.getName());
        radiantExposure.setCode(SystemCurveEnum.RADIANT_EXPOSURE.getCode());
        List<Object> radiantExposureList = Lists.newArrayList();

        //光伏气象站-倾斜总辐照量
        SystemCurveDto.DataInfo obliqueIrradiation = new SystemCurveDto.DataInfo();
        obliqueIrradiation.setName(SystemCurveEnum.OBLIQUE_IRRADIATION.getName());
        obliqueIrradiation.setCode(SystemCurveEnum.OBLIQUE_IRRADIATION.getCode());
        List<Object> obliqueIrradiationList = Lists.newArrayList();

        //根据数据id查询光伏气象站数据
        DeviceBasicInfoDto deviceBasicInfo = deviceService.findDeviceBasicInfoByIds(Collections.singletonList(dataId)).getData().get(dataId);
        if (deviceBasicInfo != null) {
            //查询逆变器功能点历史数据
            DeviceHistoryQueryVo deviceHistoryQueryVo = new DeviceHistoryQueryVo();
            deviceHistoryQueryVo.setDeviceIds(Collections.singleton(dataId));
            deviceHistoryQueryVo.setFunctionLogos(new HashSet<>(Arrays.asList(FunctionLogoParamVo.PV_TOTAL_RADIANT_EXPOSURE, FunctionLogoParamVo.PV_OBLIQUE_IRRADIATION)));
            deviceHistoryQueryVo.setStartTime(startTime);
            deviceHistoryQueryVo.setEndTime(endTime);
            deviceHistoryQueryVo.setTimeInterval(timeInterval);
            Map<String, Map<String, List<DeviceHistoryDto>>> deviceHistoryMap = dataService.findDeviceHistoryValueList(deviceHistoryQueryVo).getData();
            if (MapUtils.isNotEmpty(deviceHistoryMap) && deviceHistoryMap.containsKey(dataId)) {
                // 存储所有历史数据
                Map<String, List<DeviceHistoryDto>> functionDataMap = deviceHistoryMap.get(dataId);

                if (MapUtils.isNotEmpty(functionDataMap)) {
                    Map<String, Double> radiantExposureMap = Maps.newHashMap();
                    Map<String, Double> obliqueIrradiationMap = Maps.newHashMap();
                    //光伏气象站-水平总辐照量
                    if (functionDataMap.containsKey(FunctionLogoParamVo.PV_TOTAL_RADIANT_EXPOSURE)) {
                        //根据时间分组
                        radiantExposureMap = functionDataMap.get(FunctionLogoParamVo.PV_TOTAL_RADIANT_EXPOSURE).stream().filter(c -> StringUtil.isNotEmpty(c.getDateTime())).collect(Collectors.groupingBy(c -> {
                            String dateFormat = format(strToDate(c.getDateTime()), getFormatIntervalPattern(formatInterval));
                            if (StringUtil.isNotEmpty(dateFormat)) {
                                return dateFormat;
                            }
                            return c.getDateTime();
                        }, Collectors.summingDouble(d -> DoubleUtil.objToDouble(d.getDataValue()))));
                    }
                    //光伏气象站-倾斜总辐照量
                    if (functionDataMap.containsKey(FunctionLogoParamVo.PV_OBLIQUE_IRRADIATION)) {
                        //根据时间分组
                        obliqueIrradiationMap = functionDataMap.get(FunctionLogoParamVo.PV_OBLIQUE_IRRADIATION).stream().filter(c -> StringUtil.isNotEmpty(c.getDateTime())).collect(Collectors.groupingBy(c -> {
                            String dateFormat = format(strToDate(c.getDateTime()), getFormatIntervalPattern(formatInterval));
                            if (StringUtil.isNotEmpty(dateFormat)) {
                                return dateFormat;
                            }
                            return c.getDateTime();
                        }, Collectors.summingDouble(d -> DoubleUtil.objToDouble(d.getDataValue()))));
                    }

                    for (String dateTime : dateList) {
                        //光伏气象站-水平总辐照量
                        if (radiantExposureMap.containsKey(dateTime)) {
                            radiantExposureList.add(getToDouble(radiantExposureMap.get(dateTime)));
                        } else {
                            radiantExposureList.add(null);
                        }
                        //光伏气象站-倾斜总辐照量
                        if (obliqueIrradiationMap.containsKey(dateTime)) {
                            obliqueIrradiationList.add(getToDouble(obliqueIrradiationMap.get(dateTime)));
                        } else {
                            obliqueIrradiationList.add(null);
                        }
                    }

                    radiantExposure.setDataList(radiantExposureList);
                    obliqueIrradiation.setDataList(obliqueIrradiationList);
                }
            }
        }
        dataInfoList.add(radiantExposure);
        dataInfoList.add(obliqueIrradiation);
        result.setDataInfoList(dataInfoList);
        return result;
    }

    //获取储能系统功率曲线数据
    private SystemCurveDto getSystemSePowerCurve(String dataId, String startTime, String endTime, String timeInterval, Integer formatInterval, Set<String> dateList) {
        //返回的对象
        SystemCurveDto result = new SystemCurveDto();

        result.setDateList(dateList);

        //存储数据列表
        List<SystemCurveDto.DataInfo> dataInfoList = Lists.newArrayList();

        //储能系统-有功功率
        SystemCurveDto.DataInfo activePower = new SystemCurveDto.DataInfo();
        activePower.setName(SystemCurveEnum.ACTIVE_POWER.getName());
        activePower.setCode(SystemCurveEnum.ACTIVE_POWER.getCode());
        List<Object> activePowerList = Lists.newArrayList();

        //储能系统-无功功率
        SystemCurveDto.DataInfo reactivePower = new SystemCurveDto.DataInfo();
        reactivePower.setName(SystemCurveEnum.REACTIVE_POWER.getName());
        reactivePower.setCode(SystemCurveEnum.REACTIVE_POWER.getCode());
        List<Object> reactivePowerList = Lists.newArrayList();

        //根据数据id查询站点id
        String siteId = deviceService.findSiteIdBySubId(dataId).getData();
        //获取储能系统PCS和储能DC/DC设备
        List<SystemTreeDto> systemTreeList = this.getSystemMonitorData(siteId, dataId, 2, null).stream().filter(s -> StringUtil.isNotEmpty(s.getTypeId())
                && (Objects.equals(s.getTypeId(), "23") || Objects.equals(s.getTypeId(), "78"))).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(systemTreeList)) {
            Set<String> deviceIds = systemTreeList.stream().map(SystemTreeDto::getId).collect(Collectors.toSet());
            //查询功率数据
            DeviceHistoryQueryVo deviceHistoryQueryVo = new DeviceHistoryQueryVo();
            deviceHistoryQueryVo.setDeviceIds(deviceIds);
            deviceHistoryQueryVo.setFunctionLogos(new HashSet<>(Arrays.asList(FunctionLogoParamVo.PCS_ACTIVE_POWER, FunctionLogoParamVo.PCS_REACTIVE_POWER)));
            deviceHistoryQueryVo.setStartTime(startTime);
            deviceHistoryQueryVo.setEndTime(endTime);
            deviceHistoryQueryVo.setTimeInterval(timeInterval);
            //查询设备功能点历史数据
            Map<String, Map<String, List<DeviceHistoryDto>>> deviceHistoryMap = dataService.findDeviceHistoryValueList(deviceHistoryQueryVo).getData();
            if (MapUtils.isNotEmpty(deviceHistoryMap)) {
                // 存储所有历史数据
                Map<String, List<DeviceHistoryDto>> functionDataMap = Maps.newHashMap();
                for (Map<String, List<DeviceHistoryDto>> listMap : deviceHistoryMap.values()) {
                    listMap.forEach((key, value) -> {
                        if (functionDataMap.containsKey(key)) {
                            functionDataMap.get(key).addAll(value);
                        } else {
                            functionDataMap.put(key, value);
                        }
                    });
                }

                if (MapUtils.isNotEmpty(functionDataMap)) {
                    Map<String, Double> activePowerMap = Maps.newHashMap();
                    Map<String, Double> reactivePowerMap = Maps.newHashMap();
                    //储能系统-有功功率数据
                    if (functionDataMap.containsKey(FunctionLogoParamVo.PCS_ACTIVE_POWER)) {
                        //根据时间分组
                        activePowerMap = functionDataMap.get(FunctionLogoParamVo.PCS_ACTIVE_POWER).stream().filter(c -> StringUtil.isNotEmpty(c.getDateTime())).collect(Collectors.groupingBy(c -> {
                            String dateFormat = format(strToDate(c.getDateTime()), getFormatIntervalPattern(formatInterval));
                            if (StringUtil.isNotEmpty(dateFormat)) {
                                return dateFormat;
                            }
                            return c.getDateTime();
                        }, Collectors.summingDouble(d -> DoubleUtil.objToDouble(d.getDataValue()))));
                    }
                    //储能系统-无功功率数据
                    if (functionDataMap.containsKey(FunctionLogoParamVo.PCS_REACTIVE_POWER)) {
                        //根据时间分组
                        reactivePowerMap = functionDataMap.get(FunctionLogoParamVo.PCS_REACTIVE_POWER).stream().filter(c -> StringUtil.isNotEmpty(c.getDateTime())).collect(Collectors.groupingBy(c -> {
                            String dateFormat = format(strToDate(c.getDateTime()), getFormatIntervalPattern(formatInterval));
                            if (StringUtil.isNotEmpty(dateFormat)) {
                                return dateFormat;
                            }
                            return c.getDateTime();
                        }, Collectors.summingDouble(d -> DoubleUtil.objToDouble(d.getDataValue()))));
                    }

                    for (String dateTime : dateList) {
                        //储能系统-有功功率
                        if (activePowerMap.containsKey(dateTime)) {
                            activePowerList.add(getToDouble(activePowerMap.get(dateTime)));
                        } else {
                            activePowerList.add(null);
                        }
                        //储能系统-无功功率
                        if (reactivePowerMap.containsKey(dateTime)) {
                            reactivePowerList.add(getToDouble(reactivePowerMap.get(dateTime)));
                        } else {
                            reactivePowerList.add(null);
                        }
                    }

                    activePower.setDataList(activePowerList);
                    reactivePower.setDataList(reactivePowerList);
                }
            }
        }
        dataInfoList.add(activePower);
        dataInfoList.add(reactivePower);
        result.setDataInfoList(dataInfoList);
        return result;
    }

    //获取储能系统发电量曲线数据
    private SystemCurveDto getSystemSeQtCurve(String dataId, String startTime, String endTime, String timeInterval, Integer formatInterval, Set<String> dateList) {
        //返回的对象
        SystemCurveDto result = new SystemCurveDto();

        result.setDateList(dateList);

        //存储数据列表
        List<SystemCurveDto.DataInfo> dataInfoList = Lists.newArrayList();

        //储能系统-充电量
        SystemCurveDto.DataInfo chargeQt = new SystemCurveDto.DataInfo();
        chargeQt.setName(SystemCurveEnum.CHARGE_QT.getName());
        chargeQt.setCode(SystemCurveEnum.CHARGE_QT.getCode());
        List<Object> chargeQtList = Lists.newArrayList();

        //储能系统-放电量
        SystemCurveDto.DataInfo dischargeQt = new SystemCurveDto.DataInfo();
        dischargeQt.setName(SystemCurveEnum.DISCHARGE_QT.getName());
        dischargeQt.setCode(SystemCurveEnum.DISCHARGE_QT.getCode());
        List<Object> dischargeQtList = Lists.newArrayList();

        //根据数据id查询站点id
        String siteId = deviceService.findSiteIdBySubId(dataId).getData();
        //获取储能PCS设备
        List<SystemTreeDto> systemTreeList = this.getSystemMonitorData(siteId, dataId, 2, null).stream().filter(s -> StringUtil.isNotEmpty(s.getTypeId())
                && (Objects.equals(s.getTypeId(), "23") || Objects.equals(s.getTypeId(), "78"))).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(systemTreeList)) {
            Set<String> deviceIds = systemTreeList.stream().map(SystemTreeDto::getId).collect(Collectors.toSet());
            //查询功率数据
            DeviceHistoryQueryVo deviceHistoryQueryVo = new DeviceHistoryQueryVo();
            deviceHistoryQueryVo.setDeviceIds(deviceIds);
            deviceHistoryQueryVo.setFunctionLogos(new HashSet<>(Arrays.asList(FunctionLogoParamVo.PCS_BATTERY_CHARGE, FunctionLogoParamVo.PCS_BATTERY_DISCHARGE)));
            deviceHistoryQueryVo.setStartTime(startTime);
            deviceHistoryQueryVo.setEndTime(endTime);
            deviceHistoryQueryVo.setTimeInterval(timeInterval);
            //查询储能PCS功能点历史数据
            Map<String, Map<String, List<NodeDifHistoryDto>>> deviceHistoryMap = dataService.findNodeDifHistoryListFeign(deviceHistoryQueryVo).getData();
            if (MapUtils.isNotEmpty(deviceHistoryMap)) {
                // 存储所有充电量历史数据
                List<NodeDifHistoryDto> sumChargeList = Lists.newArrayList();
                // 存储所有放电量历史数据
                List<NodeDifHistoryDto> sumDischargeList = Lists.newArrayList();
                deviceHistoryMap.forEach((deviceId, dataMap) -> {
                    if (!dataMap.isEmpty() && CollectionUtils.isNotEmpty(dataMap.get(FunctionLogoParamVo.PCS_BATTERY_CHARGE))) {
                        sumChargeList.addAll(dataMap.get(FunctionLogoParamVo.PCS_BATTERY_CHARGE));
                    }
                    if (!dataMap.isEmpty() && CollectionUtils.isNotEmpty(dataMap.get(FunctionLogoParamVo.PCS_BATTERY_DISCHARGE))) {
                        sumDischargeList.addAll(dataMap.get(FunctionLogoParamVo.PCS_BATTERY_DISCHARGE));
                    }
                });

                //储能系统-充电量
                Map<String, Double> chargeQtMap = sumChargeList.stream().filter(c -> StringUtil.isNotEmpty(c.getFirstDateTime())).collect(Collectors.groupingBy(c -> {
                    String dateFormat = format(strToDate(c.getFirstDateTime()), getFormatIntervalPattern(formatInterval));
                    if (StringUtil.isNotEmpty(dateFormat)) {
                        return dateFormat;
                    }
                    return c.getFirstDateTime();
                }, Collectors.summingDouble(d -> DoubleUtil.getObjSub(d.getFirstDataValue(), d.getLastDataValue()))));
                //储能系统-放电量
                Map<String, Double> dischargeQtMap = sumDischargeList.stream().filter(c -> StringUtil.isNotEmpty(c.getFirstDateTime())).collect(Collectors.groupingBy(c -> {
                    String dateFormat = format(strToDate(c.getFirstDateTime()), getFormatIntervalPattern(formatInterval));
                    if (StringUtil.isNotEmpty(dateFormat)) {
                        return dateFormat;
                    }
                    return c.getFirstDateTime();
                }, Collectors.summingDouble(d -> DoubleUtil.getObjSub(d.getFirstDataValue(), d.getLastDataValue()))));

                for (String dateTime : dateList) {
                    //储能系统-充电量
                    if (chargeQtMap.containsKey(dateTime)) {
                        chargeQtList.add(getToDouble(chargeQtMap.get(dateTime)));
                    } else {
                        chargeQtList.add(0.0);
                    }
                    //储能系统-放电量
                    if (dischargeQtMap.containsKey(dateTime)) {
                        dischargeQtList.add(getToDouble(dischargeQtMap.get(dateTime)));
                    } else {
                        dischargeQtList.add(0.0);
                    }
                }
                chargeQt.setDataList(chargeQtList);
                dischargeQt.setDataList(dischargeQtList);
            }
        }
        dataInfoList.add(chargeQt);
        dataInfoList.add(dischargeQt);
        result.setDataInfoList(dataInfoList);
        return result;
    }

    //获取储能系统PCS功率曲线数据
    private SystemCurveDto getSePcsPowerCurve(String dataId, String startTime, String endTime, String timeInterval, Integer formatInterval, Set<String> dateList) {
        //返回的对象
        SystemCurveDto result = new SystemCurveDto();

        result.setDateList(dateList);

        //存储数据列表
        List<SystemCurveDto.DataInfo> dataInfoList = Lists.newArrayList();

        //储能设备PCS-有功功率
        SystemCurveDto.DataInfo activePower = new SystemCurveDto.DataInfo();
        activePower.setName(SystemCurveEnum.ACTIVE_POWER.getName());
        activePower.setCode(SystemCurveEnum.ACTIVE_POWER.getCode());
        List<Object> activePowerList = Lists.newArrayList();

        //储能设备PCS-无功功率
        SystemCurveDto.DataInfo reactivePower = new SystemCurveDto.DataInfo();
        reactivePower.setName(SystemCurveEnum.REACTIVE_POWER.getName());
        reactivePower.setCode(SystemCurveEnum.REACTIVE_POWER.getCode());
        List<Object> reactivePowerList = Lists.newArrayList();

        //根据数据id查询储能pcs设备数据
        DeviceBasicInfoDto deviceInfo = deviceService.findDeviceBasicInfoByIds(Collections.singletonList(dataId)).getData().get(dataId);
        if (deviceInfo != null) {
            //查询功率数据
            DeviceHistoryQueryVo deviceHistoryQueryVo = new DeviceHistoryQueryVo();
            deviceHistoryQueryVo.setDeviceIds(Collections.singleton(dataId));
            deviceHistoryQueryVo.setFunctionLogos(new HashSet<>(Arrays.asList(FunctionLogoParamVo.PCS_ACTIVE_POWER, FunctionLogoParamVo.PCS_REACTIVE_POWER)));
            deviceHistoryQueryVo.setStartTime(startTime);
            deviceHistoryQueryVo.setEndTime(endTime);
            deviceHistoryQueryVo.setTimeInterval(timeInterval);
            //查询设备功能点历史数据
            Map<String, Map<String, List<DeviceHistoryDto>>> deviceHistoryMap = dataService.findDeviceHistoryValueList(deviceHistoryQueryVo).getData();
            if (MapUtils.isNotEmpty(deviceHistoryMap) && deviceHistoryMap.containsKey(dataId)) {
                // 存储所有历史数据
                Map<String, List<DeviceHistoryDto>> functionDataMap = deviceHistoryMap.get(dataId);
                if (MapUtils.isNotEmpty(functionDataMap)) {
                    Map<String, Double> activePowerMap = Maps.newHashMap();
                    Map<String, Double> reactivePowerMap = Maps.newHashMap();
                    //储能设备PCS-有功功率数据
                    if (functionDataMap.containsKey(FunctionLogoParamVo.PCS_ACTIVE_POWER)) {
                        //根据时间分组
                        activePowerMap = functionDataMap.get(FunctionLogoParamVo.PCS_ACTIVE_POWER).stream().filter(c -> StringUtil.isNotEmpty(c.getDateTime())).collect(Collectors.groupingBy(c -> {
                            String dateFormat = format(strToDate(c.getDateTime()), getFormatIntervalPattern(formatInterval));
                            if (StringUtil.isNotEmpty(dateFormat)) {
                                return dateFormat;
                            }
                            return c.getDateTime();
                        }, Collectors.summingDouble(d -> DoubleUtil.objToDouble(d.getDataValue()))));
                    }
                    //储能设备PCS-无功功率数据
                    if (functionDataMap.containsKey(FunctionLogoParamVo.PCS_REACTIVE_POWER)) {
                        //根据时间分组
                        reactivePowerMap = functionDataMap.get(FunctionLogoParamVo.PCS_REACTIVE_POWER).stream().filter(c -> StringUtil.isNotEmpty(c.getDateTime())).collect(Collectors.groupingBy(c -> {
                            String dateFormat = format(strToDate(c.getDateTime()), getFormatIntervalPattern(formatInterval));
                            if (StringUtil.isNotEmpty(dateFormat)) {
                                return dateFormat;
                            }
                            return c.getDateTime();
                        }, Collectors.summingDouble(d -> DoubleUtil.objToDouble(d.getDataValue()))));
                    }

                    for (String dateTime : dateList) {
                        //储能设备PCS-有功功率
                        if (activePowerMap.containsKey(dateTime)) {
                            activePowerList.add(getToDouble(activePowerMap.get(dateTime)));
                        } else {
                            activePowerList.add(null);
                        }
                        //储能设备PCS-无功功率
                        if (reactivePowerMap.containsKey(dateTime)) {
                            reactivePowerList.add(getToDouble(reactivePowerMap.get(dateTime)));
                        } else {
                            reactivePowerList.add(null);
                        }
                    }

                    activePower.setDataList(activePowerList);
                    reactivePower.setDataList(reactivePowerList);
                }
            }
        }
        dataInfoList.add(activePower);
        dataInfoList.add(reactivePower);
        result.setDataInfoList(dataInfoList);
        return result;
    }

    //获取储能设备PCS发电量曲线数据
    private SystemCurveDto getSePcsQtCurve(String dataId, String startTime, String endTime, String timeInterval, Integer formatInterval, Set<String> dateList) {
        //返回的对象
        SystemCurveDto result = new SystemCurveDto();

        result.setDateList(dateList);

        //存储数据列表
        List<SystemCurveDto.DataInfo> dataInfoList = Lists.newArrayList();

        //储能设备PCS-充电量
        SystemCurveDto.DataInfo chargeQt = new SystemCurveDto.DataInfo();
        chargeQt.setName(SystemCurveEnum.CHARGE_QT.getName());
        chargeQt.setCode(SystemCurveEnum.CHARGE_QT.getCode());
        List<Object> chargeQtList = Lists.newArrayList();

        //储能设备PCS-放电量
        SystemCurveDto.DataInfo dischargeQt = new SystemCurveDto.DataInfo();
        dischargeQt.setName(SystemCurveEnum.DISCHARGE_QT.getName());
        dischargeQt.setCode(SystemCurveEnum.DISCHARGE_QT.getCode());
        List<Object> dischargeQtList = Lists.newArrayList();

        //根据数据id查询储能pcs设备数据
        DeviceBasicInfoDto deviceInfo = deviceService.findDeviceBasicInfoByIds(Collections.singletonList(dataId)).getData().get(dataId);
        if (deviceInfo != null) {
            //查询储能PCS功能点历史数据
            DeviceHistoryQueryVo deviceHistoryQueryVo = new DeviceHistoryQueryVo();
            deviceHistoryQueryVo.setDeviceIds(Collections.singleton(dataId));
            deviceHistoryQueryVo.setFunctionLogos(new HashSet<>(Arrays.asList(FunctionLogoParamVo.PCS_BATTERY_CHARGE, FunctionLogoParamVo.PCS_BATTERY_DISCHARGE)));
            deviceHistoryQueryVo.setStartTime(startTime);
            deviceHistoryQueryVo.setEndTime(endTime);
            deviceHistoryQueryVo.setTimeInterval(timeInterval);
            Map<String, Map<String, List<NodeDifHistoryDto>>> deviceHistoryMap = dataService.findNodeDifHistoryListFeign(deviceHistoryQueryVo).getData();
            if (MapUtils.isNotEmpty(deviceHistoryMap) && deviceHistoryMap.containsKey(dataId)) {

                Map<String, List<NodeDifHistoryDto>> functionDataMap = deviceHistoryMap.get(dataId);

                Map<String, Double> chargeQtMap = Maps.newHashMap();
                Map<String, Double> dischargeQtMap = Maps.newHashMap();
                //储能设备PCS-充电量
                if (functionDataMap.containsKey(FunctionLogoParamVo.PCS_BATTERY_CHARGE)) {
                    //根据时间分组
                    chargeQtMap = functionDataMap.get(FunctionLogoParamVo.PCS_BATTERY_CHARGE).stream().filter(c -> StringUtil.isNotEmpty(c.getFirstDateTime())).collect(Collectors.groupingBy(c -> {
                        String dateFormat = format(strToDate(c.getFirstDateTime()), getFormatIntervalPattern(formatInterval));
                        if (StringUtil.isNotEmpty(dateFormat)) {
                            return dateFormat;
                        }
                        return c.getFirstDateTime();
                    }, Collectors.summingDouble(d -> DoubleUtil.getObjSub(d.getFirstDataValue(), d.getLastDataValue()))));
                }
                //储能设备PCS-放电量
                if (functionDataMap.containsKey(FunctionLogoParamVo.PCS_BATTERY_DISCHARGE)) {
                    //根据时间分组
                    dischargeQtMap = functionDataMap.get(FunctionLogoParamVo.PCS_BATTERY_DISCHARGE).stream().filter(c -> StringUtil.isNotEmpty(c.getFirstDateTime())).collect(Collectors.groupingBy(c -> {
                        String dateFormat = format(strToDate(c.getFirstDateTime()), getFormatIntervalPattern(formatInterval));
                        if (StringUtil.isNotEmpty(dateFormat)) {
                            return dateFormat;
                        }
                        return c.getFirstDateTime();
                    }, Collectors.summingDouble(d -> DoubleUtil.getObjSub(d.getFirstDataValue(), d.getLastDataValue()))));
                }

                for (String dateTime : dateList) {
                    //储能设备PCS-充电量
                    if (chargeQtMap.containsKey(dateTime)) {
                        chargeQtList.add(getToDouble(chargeQtMap.get(dateTime)));
                    } else {
                        chargeQtList.add(null);
                    }
                    //储能设备PCS-放电量
                    if (dischargeQtMap.containsKey(dateTime)) {
                        dischargeQtList.add(getToDouble(dischargeQtMap.get(dateTime)));
                    } else {
                        dischargeQtList.add(null);
                    }
                }
                chargeQt.setDataList(chargeQtList);
                dischargeQt.setDataList(dischargeQtList);
            }
        }
        dataInfoList.add(chargeQt);
        dataInfoList.add(dischargeQt);
        result.setDataInfoList(dataInfoList);
        return result;
    }

    //获取储能设备电池簇SOC曲线数据
    private SystemCurveDto getSeBatterySocCurve(String dataId, String startTime, String endTime, String timeInterval, Integer formatInterval, Set<String> dateList) {
        //返回的对象
        SystemCurveDto result = new SystemCurveDto();

        result.setDateList(dateList);

        //存储数据列表
        List<SystemCurveDto.DataInfo> dataInfoList = Lists.newArrayList();

        //储能设备电池簇-电池SOC
        SystemCurveDto.DataInfo batterySoc = new SystemCurveDto.DataInfo();
        batterySoc.setName(SystemCurveEnum.BATTERY_SOC.getName());
        batterySoc.setCode(SystemCurveEnum.BATTERY_SOC.getCode());
        List<Object> batterySocList = Lists.newArrayList();

        //储能设备电池簇-电池总电流
        SystemCurveDto.DataInfo batteryCurrent = new SystemCurveDto.DataInfo();
        batteryCurrent.setName(SystemCurveEnum.BATTERY_CURRENT.getName());
        batteryCurrent.setCode(SystemCurveEnum.BATTERY_CURRENT.getCode());
        List<Object> batteryCurrentList = Lists.newArrayList();

        //根据数据id查询储能电池簇设备数据
        DeviceBasicInfoDto deviceInfo = deviceService.findDeviceBasicInfoByIds(Collections.singletonList(dataId)).getData().get(dataId);
        if (deviceInfo != null) {
            //查询功率数据
            DeviceHistoryQueryVo deviceHistoryQueryVo = new DeviceHistoryQueryVo();
            deviceHistoryQueryVo.setDeviceIds(Collections.singleton(dataId));
            deviceHistoryQueryVo.setFunctionLogos(new HashSet<>(Arrays.asList(FunctionLogoParamVo.BATTERY_TOTAL_SOC, FunctionLogoParamVo.BATTERY_TOTAL_CURRENT)));
            deviceHistoryQueryVo.setStartTime(startTime);
            deviceHistoryQueryVo.setEndTime(endTime);
            deviceHistoryQueryVo.setTimeInterval(timeInterval);
            //查询设备功能点历史数据
            Map<String, Map<String, List<DeviceHistoryDto>>> deviceHistoryMap = dataService.findDeviceHistoryValueList(deviceHistoryQueryVo).getData();
            if (MapUtils.isNotEmpty(deviceHistoryMap) && deviceHistoryMap.containsKey(dataId)) {
                // 存储所有历史数据
                Map<String, List<DeviceHistoryDto>> functionDataMap = deviceHistoryMap.get(dataId);
                if (MapUtils.isNotEmpty(functionDataMap)) {
                    Map<String, Double> batterySocMap = Maps.newHashMap();
                    Map<String, Double> batteryCurrentMap = Maps.newHashMap();
                    //储能设备电池簇-电池SOC
                    if (functionDataMap.containsKey(FunctionLogoParamVo.BATTERY_TOTAL_SOC)) {
                        //根据时间分组
                        batterySocMap = functionDataMap.get(FunctionLogoParamVo.BATTERY_TOTAL_SOC).stream().filter(c -> StringUtil.isNotEmpty(c.getDateTime())).collect(Collectors.groupingBy(c -> {
                            String dateFormat = format(strToDate(c.getDateTime()), getFormatIntervalPattern(formatInterval));
                            if (StringUtil.isNotEmpty(dateFormat)) {
                                return dateFormat;
                            }
                            return c.getDateTime();
                        }, Collectors.summingDouble(d -> DoubleUtil.objToDouble(d.getDataValue()))));
                    }
                    //储能设备电池簇-电池总电流
                    if (functionDataMap.containsKey(FunctionLogoParamVo.BATTERY_TOTAL_CURRENT)) {
                        //根据时间分组
                        batteryCurrentMap = functionDataMap.get(FunctionLogoParamVo.BATTERY_TOTAL_CURRENT).stream().filter(c -> StringUtil.isNotEmpty(c.getDateTime())).collect(Collectors.groupingBy(c -> {
                            String dateFormat = format(strToDate(c.getDateTime()), getFormatIntervalPattern(formatInterval));
                            if (StringUtil.isNotEmpty(dateFormat)) {
                                return dateFormat;
                            }
                            return c.getDateTime();
                        }, Collectors.summingDouble(d -> DoubleUtil.objToDouble(d.getDataValue()))));
                    }

                    for (String dateTime : dateList) {
                        //储能设备电池簇-电池SOC
                        if (batterySocMap.containsKey(dateTime)) {
                            batterySocList.add(getToDouble(batterySocMap.get(dateTime)));
                        } else {
                            batterySocList.add(null);
                        }
                        //储能设备电池簇-电池总电流
                        if (batteryCurrentMap.containsKey(dateTime)) {
                            batteryCurrentList.add(getToDouble(batteryCurrentMap.get(dateTime)));
                        } else {
                            batteryCurrentList.add(null);
                        }
                    }
                    batterySoc.setDataList(batterySocList);
                    batteryCurrent.setDataList(batteryCurrentList);
                }
            }
        }
        dataInfoList.add(batterySoc);
        dataInfoList.add(batteryCurrent);
        result.setDataInfoList(dataInfoList);
        return result;
    }

    //获取储能设备电池簇总电压曲线数据
    private SystemCurveDto getSeBatteryVoltageCurve(String dataId, String startTime, String endTime, String timeInterval, Integer formatInterval, Set<String> dateList) {
        //返回的对象
        SystemCurveDto result = new SystemCurveDto();

        result.setDateList(dateList);

        //存储数据列表
        List<SystemCurveDto.DataInfo> dataInfoList = Lists.newArrayList();

        //储能设备电池簇-电池总电压
        SystemCurveDto.DataInfo batteryVoltage = new SystemCurveDto.DataInfo();
        batteryVoltage.setName(SystemCurveEnum.BATTERY_VOLTAGE.getName());
        batteryVoltage.setCode(SystemCurveEnum.BATTERY_VOLTAGE.getCode());
        List<Object> batteryVoltageList = Lists.newArrayList();

        //根据数据id查询储能电池簇设备数据
        DeviceBasicInfoDto deviceInfo = deviceService.findDeviceBasicInfoByIds(Collections.singletonList(dataId)).getData().get(dataId);
        if (deviceInfo != null) {
            //查询功率数据
            DeviceHistoryQueryVo deviceHistoryQueryVo = new DeviceHistoryQueryVo();
            deviceHistoryQueryVo.setDeviceIds(Collections.singleton(dataId));
            deviceHistoryQueryVo.setFunctionLogos(Collections.singleton(FunctionLogoParamVo.BATTERY_TOTAL_VOLTAGE));
            deviceHistoryQueryVo.setStartTime(startTime);
            deviceHistoryQueryVo.setEndTime(endTime);
            deviceHistoryQueryVo.setTimeInterval(timeInterval);
            //查询设备功能点历史数据
            Map<String, Map<String, List<DeviceHistoryDto>>> deviceHistoryMap = dataService.findDeviceHistoryValueList(deviceHistoryQueryVo).getData();
            if (MapUtils.isNotEmpty(deviceHistoryMap) && deviceHistoryMap.containsKey(dataId)) {
                // 存储所有历史数据
                Map<String, List<DeviceHistoryDto>> functionDataMap = deviceHistoryMap.get(dataId);
                if (MapUtils.isNotEmpty(functionDataMap)) {
                    //储能设备电池簇-电池总电压
                    Map<String, Double> batteryVoltageMap = Maps.newHashMap();
                    if (functionDataMap.containsKey(FunctionLogoParamVo.BATTERY_TOTAL_VOLTAGE)) {
                        //根据时间分组
                        batteryVoltageMap = functionDataMap.get(FunctionLogoParamVo.BATTERY_TOTAL_VOLTAGE).stream().filter(c -> StringUtil.isNotEmpty(c.getDateTime())).collect(Collectors.groupingBy(c -> {
                            String dateFormat = format(strToDate(c.getDateTime()), getFormatIntervalPattern(formatInterval));
                            if (StringUtil.isNotEmpty(dateFormat)) {
                                return dateFormat;
                            }
                            return c.getDateTime();
                        }, Collectors.summingDouble(d -> DoubleUtil.objToDouble(d.getDataValue()))));
                    }
                    for (String dateTime : dateList) {
                        //储能设备电池簇-电池总电压
                        if (batteryVoltageMap.containsKey(dateTime)) {
                            batteryVoltageList.add(getToDouble(batteryVoltageMap.get(dateTime)));
                        } else {
                            batteryVoltageList.add(null);
                        }
                    }
                    batteryVoltage.setDataList(batteryVoltageList);
                }
            }
        }
        dataInfoList.add(batteryVoltage);
        result.setDataInfoList(dataInfoList);
        return result;
    }

    //获取储能空调设备温度曲线数据
    private SystemCurveDto getSeAuxEquipmentTemperatureCurve(String dataId, String startTime, String endTime, String timeInterval, Integer formatInterval, Set<String> dateList) {
        //返回的对象
        SystemCurveDto result = new SystemCurveDto();

        result.setDateList(dateList);

        //存储数据列表
        List<SystemCurveDto.DataInfo> dataInfoList = Lists.newArrayList();

        //储能空调设备-柜内温度
        SystemCurveDto.DataInfo cabinetTemperature = new SystemCurveDto.DataInfo();
        cabinetTemperature.setName(SystemCurveEnum.CABINET_TEMPERATURE.getName());
        cabinetTemperature.setCode(SystemCurveEnum.CABINET_TEMPERATURE.getCode());
        List<Object> cabinetTemperatureList = Lists.newArrayList();

        //储能空调设备-环境温度
        SystemCurveDto.DataInfo ambientTemperature = new SystemCurveDto.DataInfo();
        ambientTemperature.setName(SystemCurveEnum.AMBIENT_TEMPERATURE.getName());
        ambientTemperature.setCode(SystemCurveEnum.AMBIENT_TEMPERATURE.getCode());
        List<Object> ambientTemperatureList = Lists.newArrayList();

        //根据数据id储能空调设备数据
        DeviceBasicInfoDto deviceInfo = deviceService.findDeviceBasicInfoByIds(Collections.singletonList(dataId)).getData().get(dataId);
        if (deviceInfo != null) {
            //查询功率数据
            DeviceHistoryQueryVo deviceHistoryQueryVo = new DeviceHistoryQueryVo();
            deviceHistoryQueryVo.setDeviceIds(Collections.singleton(dataId));
            deviceHistoryQueryVo.setFunctionLogos(new HashSet<>(Arrays.asList(FunctionLogoParamVo.CABINET_TEMPERATURE, FunctionLogoParamVo.EXTERNAL_TEMPERATURE)));
            deviceHistoryQueryVo.setStartTime(startTime);
            deviceHistoryQueryVo.setEndTime(endTime);
            deviceHistoryQueryVo.setTimeInterval(timeInterval);
            //查询设备功能点历史数据
            Map<String, Map<String, List<DeviceHistoryDto>>> deviceHistoryMap = dataService.findDeviceHistoryValueList(deviceHistoryQueryVo).getData();
            if (MapUtils.isNotEmpty(deviceHistoryMap) && deviceHistoryMap.containsKey(dataId)) {
                // 存储所有历史数据
                Map<String, List<DeviceHistoryDto>> functionDataMap = deviceHistoryMap.get(dataId);
                if (MapUtils.isNotEmpty(functionDataMap)) {
                    //储能空调设备-柜内温度
                    Map<String, Double> cabinetTemperatureMap = Maps.newHashMap();
                    if (functionDataMap.containsKey(FunctionLogoParamVo.CABINET_TEMPERATURE)) {
                        //根据时间分组
                        cabinetTemperatureMap = functionDataMap.get(FunctionLogoParamVo.CABINET_TEMPERATURE).stream().filter(c -> StringUtil.isNotEmpty(c.getDateTime())).collect(Collectors.groupingBy(c -> {
                            String dateFormat = format(strToDate(c.getDateTime()), getFormatIntervalPattern(formatInterval));
                            if (StringUtil.isNotEmpty(dateFormat)) {
                                return dateFormat;
                            }
                            return c.getDateTime();
                        }, Collectors.summingDouble(d -> DoubleUtil.objToDouble(d.getDataValue()))));
                    }
                    //储能空调设备-环境温度
                    Map<String, Double> ambientTemperatureMap = Maps.newHashMap();
                    if (functionDataMap.containsKey(FunctionLogoParamVo.EXTERNAL_TEMPERATURE)) {
                        //根据时间分组
                        ambientTemperatureMap = functionDataMap.get(FunctionLogoParamVo.EXTERNAL_TEMPERATURE).stream().filter(c -> StringUtil.isNotEmpty(c.getDateTime())).collect(Collectors.groupingBy(c -> {
                            String dateFormat = format(strToDate(c.getDateTime()), getFormatIntervalPattern(formatInterval));
                            if (StringUtil.isNotEmpty(dateFormat)) {
                                return dateFormat;
                            }
                            return c.getDateTime();
                        }, Collectors.summingDouble(d -> DoubleUtil.objToDouble(d.getDataValue()))));
                    }

                    for (String dateTime : dateList) {
                        //储能空调设备-柜内温度
                        if (cabinetTemperatureMap.containsKey(dateTime)) {
                            cabinetTemperatureList.add(getToDouble(cabinetTemperatureMap.get(dateTime)));
                        } else {
                            cabinetTemperatureList.add(null);
                        }
                        //储能空调设备-环境温度
                        if (ambientTemperatureMap.containsKey(dateTime)) {
                            ambientTemperatureList.add(getToDouble(ambientTemperatureMap.get(dateTime)));
                        } else {
                            ambientTemperatureList.add(null);
                        }
                    }
                    cabinetTemperature.setDataList(cabinetTemperatureList);
                    ambientTemperature.setDataList(ambientTemperatureList);
                }
            }
        }
        dataInfoList.add(cabinetTemperature);
        dataInfoList.add(ambientTemperature);
        result.setDataInfoList(dataInfoList);
        return result;
    }

    //获取储能空调设备温度曲线数据
    private SystemCurveDto getSeAuxEquipmentHumidityCurve(String dataId, String startTime, String endTime, String timeInterval, Integer formatInterval, Set<String> dateList) {
        //返回的对象
        SystemCurveDto result = new SystemCurveDto();

        result.setDateList(dateList);

        //存储数据列表
        List<SystemCurveDto.DataInfo> dataInfoList = Lists.newArrayList();

        //储能空调设备-柜内湿度
        SystemCurveDto.DataInfo cabinetHumidity = new SystemCurveDto.DataInfo();
        cabinetHumidity.setName(SystemCurveEnum.CABINET_HUMIDITY.getName());
        cabinetHumidity.setCode(SystemCurveEnum.CABINET_HUMIDITY.getCode());
        List<Object> cabinetHumidityList = Lists.newArrayList();

        //根据数据id查询储能空调设备数据
        DeviceBasicInfoDto deviceInfo = deviceService.findDeviceBasicInfoByIds(Collections.singletonList(dataId)).getData().get(dataId);
        if (deviceInfo != null) {
            //查询功率数据
            DeviceHistoryQueryVo deviceHistoryQueryVo = new DeviceHistoryQueryVo();
            deviceHistoryQueryVo.setDeviceIds(Collections.singleton(dataId));
            deviceHistoryQueryVo.setFunctionLogos(Collections.singleton(FunctionLogoParamVo.CABINET_HUMIDITY));
            deviceHistoryQueryVo.setStartTime(startTime);
            deviceHistoryQueryVo.setEndTime(endTime);
            deviceHistoryQueryVo.setTimeInterval(timeInterval);
            //查询设备功能点历史数据
            Map<String, Map<String, List<DeviceHistoryDto>>> deviceHistoryMap = dataService.findDeviceHistoryValueList(deviceHistoryQueryVo).getData();
            if (MapUtils.isNotEmpty(deviceHistoryMap) && deviceHistoryMap.containsKey(dataId)) {
                // 存储所有历史数据
                Map<String, List<DeviceHistoryDto>> functionDataMap = deviceHistoryMap.get(dataId);
                if (MapUtils.isNotEmpty(functionDataMap)) {
                    //储能空调设备-柜内湿度
                    Map<String, Double> cabinetHumidityMap = Maps.newHashMap();
                    if (functionDataMap.containsKey(FunctionLogoParamVo.CABINET_HUMIDITY)) {
                        cabinetHumidityMap = functionDataMap.get(FunctionLogoParamVo.CABINET_HUMIDITY).stream().filter(c -> StringUtil.isNotEmpty(c.getDateTime())).collect(Collectors.groupingBy(c -> {
                            String dateFormat = format(strToDate(c.getDateTime()), getFormatIntervalPattern(formatInterval));
                            if (StringUtil.isNotEmpty(dateFormat)) {
                                return dateFormat;
                            }
                            return c.getDateTime();
                        }, Collectors.summingDouble(d -> DoubleUtil.objToDouble(d.getDataValue()))));
                    }

                    for (String dateTime : dateList) {
                        //储能空调设备-柜内湿度
                        if (cabinetHumidityMap.containsKey(dateTime)) {
                            cabinetHumidityList.add(getToDouble(cabinetHumidityMap.get(dateTime)));
                        } else {
                            cabinetHumidityList.add(null);
                        }
                    }
                    cabinetHumidity.setDataList(cabinetHumidityList);
                }
            }
        }
        dataInfoList.add(cabinetHumidity);
        result.setDataInfoList(dataInfoList);
        return result;
    }

    //获取电桩系统功率曲线数据
    private SystemCurveDto getSystemPilePowerCurve(String dataId, String startTime, String endTime, String timeInterval, Integer formatInterval, Set<String> dateList) {
        //返回的对象
        SystemCurveDto result = new SystemCurveDto();

        result.setDateList(dateList);

        //存储数据列表
        List<SystemCurveDto.DataInfo> dataInfoList = Lists.newArrayList();

        //电桩系统-充电功率
        SystemCurveDto.DataInfo chargePower = new SystemCurveDto.DataInfo();
        chargePower.setName(SystemCurveEnum.CHARGE_POWER.getName());
        chargePower.setCode(SystemCurveEnum.CHARGE_POWER.getCode());
        List<Object> chargePowerList = Lists.newArrayList();

        //电桩系统-放电功率
        SystemCurveDto.DataInfo dischargePower = new SystemCurveDto.DataInfo();
        dischargePower.setName(SystemCurveEnum.DISCHARGE_POWER.getName());
        dischargePower.setCode(SystemCurveEnum.DISCHARGE_POWER.getCode());
        List<Object> dischargePowerList = Lists.newArrayList();

        //根据数据id查询站点id
        String siteId = deviceService.findSiteIdBySubId(dataId).getData();
        //获取电桩系统电桩设备
        List<String> typeIds = Arrays.asList("28", "29", "30", "79");
        List<SystemTreeDto> systemTreeList = this.getSystemMonitorData(siteId, dataId, 3, null).stream().filter(s -> StringUtil.isNotEmpty(s.getTypeId()) && typeIds.contains(s.getTypeId())).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(systemTreeList)) {
            Set<String> deviceIds = systemTreeList.stream().map(SystemTreeDto::getId).collect(Collectors.toSet());
            //查询功率数据
            DeviceHistoryQueryVo deviceHistoryQueryVo = new DeviceHistoryQueryVo();
            deviceHistoryQueryVo.setDeviceIds(deviceIds);
            deviceHistoryQueryVo.setFunctionLogos(new HashSet<>(Arrays.asList(FunctionLogoParamVo.PILE_CHARGEPOWER, FunctionLogoParamVo.PILE_DISCHARGEPOWER)));
            deviceHistoryQueryVo.setStartTime(startTime);
            deviceHistoryQueryVo.setEndTime(endTime);
            deviceHistoryQueryVo.setTimeInterval(timeInterval);
            //查询设备功能点历史数据
            Map<String, Map<String, List<DeviceHistoryDto>>> deviceHistoryMap = dataService.findDeviceHistoryValueList(deviceHistoryQueryVo).getData();
            if (MapUtils.isNotEmpty(deviceHistoryMap)) {
                // 存储所有历史数据
                Map<String, List<DeviceHistoryDto>> functionDataMap = Maps.newHashMap();
                for (Map<String, List<DeviceHistoryDto>> listMap : deviceHistoryMap.values()) {
                    listMap.forEach((key, value) -> {
                        if (functionDataMap.containsKey(key)) {
                            functionDataMap.get(key).addAll(value);
                        } else {
                            functionDataMap.put(key, value);
                        }
                    });
                }

                if (MapUtils.isNotEmpty(functionDataMap)) {
                    Map<String, Double> chargePowerMap = Maps.newHashMap();
                    Map<String, Double> dischargePowerMap = Maps.newHashMap();
                    //电桩系统-充电功率
                    if (functionDataMap.containsKey(FunctionLogoParamVo.PILE_CHARGEPOWER)) {
                        chargePowerMap = functionDataMap.get(FunctionLogoParamVo.PILE_CHARGEPOWER).stream().filter(c -> StringUtil.isNotEmpty(c.getDateTime())).collect(Collectors.groupingBy(c -> {
                            String dateFormat = format(strToDate(c.getDateTime()), getFormatIntervalPattern(formatInterval));
                            if (StringUtil.isNotEmpty(dateFormat)) {
                                return dateFormat;
                            }
                            return c.getDateTime();
                        }, Collectors.summingDouble(d -> DoubleUtil.objToDouble(d.getDataValue()))));
                    }
                    //电桩系统-放电功率
                    if (functionDataMap.containsKey(FunctionLogoParamVo.PILE_DISCHARGEPOWER)) {
                        dischargePowerMap = functionDataMap.get(FunctionLogoParamVo.PILE_DISCHARGEPOWER).stream().filter(c -> StringUtil.isNotEmpty(c.getDateTime())).collect(Collectors.groupingBy(c -> {
                            String dateFormat = format(strToDate(c.getDateTime()), getFormatIntervalPattern(formatInterval));
                            if (StringUtil.isNotEmpty(dateFormat)) {
                                return dateFormat;
                            }
                            return c.getDateTime();
                        }, Collectors.summingDouble(d -> DoubleUtil.objToDouble(d.getDataValue()))));
                    }

                    for (String dateTime : dateList) {
                        //电桩系统-充电功率
                        if (chargePowerMap.containsKey(dateTime)) {
                            chargePowerList.add(getToDouble(chargePowerMap.get(dateTime)));
                        } else {
                            chargePowerList.add(null);
                        }
                        //电桩系统-放电功率
                        if (dischargePowerMap.containsKey(dateTime)) {
                            dischargePowerList.add(getToDouble(dischargePowerMap.get(dateTime)));
                        } else {
                            dischargePowerList.add(null);
                        }
                    }

                    chargePower.setDataList(chargePowerList);
                    dischargePower.setDataList(dischargePowerList);
                }
            }
        }
        dataInfoList.add(chargePower);
        dataInfoList.add(dischargePower);
        result.setDataInfoList(dataInfoList);
        return result;
    }

    //获取电桩系统电量曲线数据
    private SystemCurveDto getSystemPileQtCurve(String dataId, String startTime, String endTime, Integer formatInterval, Set<String> dateList) {
        //返回的对象
        SystemCurveDto result = new SystemCurveDto();

        result.setDateList(dateList);

        //存储数据列表
        List<SystemCurveDto.DataInfo> dataInfoList = Lists.newArrayList();

        //电桩系统-充电电量
        SystemCurveDto.DataInfo chargeQt = new SystemCurveDto.DataInfo();
        chargeQt.setName(SystemCurveEnum.CHARGE_QT.getName());
        chargeQt.setCode(SystemCurveEnum.CHARGE_QT.getCode());
        List<Object> chargeQtList = Lists.newArrayList();

        //电桩系统-放电电量
        SystemCurveDto.DataInfo dischargeQt = new SystemCurveDto.DataInfo();
        dischargeQt.setName(SystemCurveEnum.DISCHARGE_QT.getName());
        dischargeQt.setCode(SystemCurveEnum.DISCHARGE_QT.getCode());
        List<Object> dischargeQtList = Lists.newArrayList();

        //根据数据id查询站点id
        String siteId = deviceService.findSiteIdBySubId(dataId).getData();
        //获取电桩系统电桩设备
        List<String> typeIds = Arrays.asList("28", "29", "30");
        List<String> pileCodes = this.getSystemMonitorData(siteId, dataId, 3, null).stream()
                .filter(s -> StringUtil.isNotEmpty(s.getTypeId()) && typeIds.contains(s.getTypeId()))
                .map(SystemTreeDto::getCode).filter(StringUtil::isNotEmpty).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(pileCodes)) {
            List<OrderRecordEntity> orderRecordList = orderRecordDao.findAllByPileCodeInAndEndTimeBetween(pileCodes, startTime, endTime);
            //电桩系统-充电电量
            List<OrderRecordEntity> chargeOrderList = orderRecordList.stream().filter(o -> StringUtil.isNotEmpty(o.getRunMode())
                    && o.getRunMode() == 0 && StringUtil.isNotEmpty(o.getTotalQt())).collect(Collectors.toList());
            Map<String, Double> chargeQtrMap = Maps.newHashMap();
            if (CollectionUtils.isNotEmpty(chargeOrderList)) {
                chargeQtrMap = chargeOrderList.stream().collect(Collectors.groupingBy(c -> {
                    String dateFormat = format(strToDate(c.getEndTime()), getFormatIntervalPattern(formatInterval));
                    if (StringUtil.isNotEmpty(dateFormat)) {
                        return dateFormat;
                    }
                    return c.getEndTime();
                }, Collectors.summingDouble(OrderRecordEntity::getTotalQt)));

            }

            //电桩系统-放电电量
            List<OrderRecordEntity> dischargeOrderList = orderRecordList.stream().filter(o -> StringUtil.isNotEmpty(o.getRunMode())
                    && (o.getRunMode() == 1 || o.getRunMode() == 2) && StringUtil.isNotEmpty(o.getTotalQt())).collect(Collectors.toList());
            Map<String, Double> dischargeQtMap = Maps.newHashMap();
            if (CollectionUtils.isNotEmpty(dischargeOrderList)) {
                dischargeQtMap = dischargeOrderList.stream().collect(Collectors.groupingBy(c -> {
                    String dateFormat = format(strToDate(c.getEndTime()), getFormatIntervalPattern(formatInterval));
                    if (StringUtil.isNotEmpty(dateFormat)) {
                        return dateFormat;
                    }
                    return c.getEndTime();
                }, Collectors.summingDouble(OrderRecordEntity::getTotalQt)));
            }

            for (String dateTime : dateList) {
                //电桩系统-充电电量
                if (chargeQtrMap.containsKey(dateTime)) {
                    chargeQtList.add(getToDouble(chargeQtrMap.get(dateTime)));
                } else {
                    chargeQtList.add(0.0);
                }
                //电桩系统-放电电量
                if (dischargeQtMap.containsKey(dateTime)) {
                    dischargeQtList.add(getToDouble(dischargeQtMap.get(dateTime)));
                } else {
                    dischargeQtList.add(0.0);
                }
            }

            chargeQt.setDataList(chargeQtList);
            dischargeQt.setDataList(dischargeQtList);
        }

        dataInfoList.add(chargeQt);
        dataInfoList.add(dischargeQt);
        result.setDataInfoList(dataInfoList);
        return result;
    }

    //获取电桩功率曲线数据
    private SystemCurveDto getPilePowerCurve(String dataId, String startTime, String endTime, String timeInterval, Integer formatInterval, Set<String> dateList) {
        //返回的对象
        SystemCurveDto result = new SystemCurveDto();

        result.setDateList(dateList);

        //存储数据列表
        List<SystemCurveDto.DataInfo> dataInfoList = Lists.newArrayList();

        //电桩-充电功率
        SystemCurveDto.DataInfo chargePower = new SystemCurveDto.DataInfo();
        chargePower.setName(SystemCurveEnum.CHARGE_POWER.getName());
        chargePower.setCode(SystemCurveEnum.CHARGE_POWER.getCode());
        List<Object> chargePowerList = Lists.newArrayList();

        //电桩-放电功率
        SystemCurveDto.DataInfo dischargePower = new SystemCurveDto.DataInfo();
        dischargePower.setName(SystemCurveEnum.DISCHARGE_POWER.getName());
        dischargePower.setCode(SystemCurveEnum.DISCHARGE_POWER.getCode());
        List<Object> dischargePowerList = Lists.newArrayList();

        //根据数据id查询电桩数据
        DeviceBasicInfoDto deviceInfo = deviceService.findDeviceBasicInfoByIds(Collections.singletonList(dataId)).getData().get(dataId);
        if (deviceInfo != null) {
            //查询功率数据
            DeviceHistoryQueryVo deviceHistoryQueryVo = new DeviceHistoryQueryVo();
            deviceHistoryQueryVo.setDeviceIds(Collections.singleton(dataId));
            deviceHistoryQueryVo.setFunctionLogos(new HashSet<>(Arrays.asList(FunctionLogoParamVo.PILE_CHARGEPOWER, FunctionLogoParamVo.PILE_DISCHARGEPOWER)));
            deviceHistoryQueryVo.setStartTime(startTime);
            deviceHistoryQueryVo.setEndTime(endTime);
            deviceHistoryQueryVo.setTimeInterval(timeInterval);
            //查询设备功能点历史数据
            Map<String, Map<String, List<DeviceHistoryDto>>> deviceHistoryMap = dataService.findDeviceHistoryValueList(deviceHistoryQueryVo).getData();
            if (MapUtils.isNotEmpty(deviceHistoryMap)) {
                // 存储所有历史数据
                Map<String, List<DeviceHistoryDto>> functionDataMap = deviceHistoryMap.get(dataId);

                if (MapUtils.isNotEmpty(functionDataMap)) {
                    Map<String, Double> chargePowerMap = Maps.newHashMap();
                    Map<String, Double> dischargePowerMap = Maps.newHashMap();
                    //电桩-充电功率
                    if (functionDataMap.containsKey(FunctionLogoParamVo.PILE_CHARGEPOWER)) {
                        chargePowerMap = functionDataMap.get(FunctionLogoParamVo.PILE_CHARGEPOWER).stream().filter(c -> StringUtil.isNotEmpty(c.getDateTime()))
                                .collect(Collectors.groupingBy(c -> {
                                    String dateFormat = format(strToDate(c.getDateTime()), getFormatIntervalPattern(formatInterval));
                                    if (StringUtil.isNotEmpty(dateFormat)) {
                                        return dateFormat;
                                    }
                                    return c.getDateTime();
                                }, Collectors.summingDouble(c -> DoubleUtil.objToDouble(c.getDataValue()))));
                    }
                    //电桩-放电功率
                    if (functionDataMap.containsKey(FunctionLogoParamVo.PILE_DISCHARGEPOWER)) {
                        dischargePowerMap = functionDataMap.get(FunctionLogoParamVo.PILE_DISCHARGEPOWER).stream().filter(c -> StringUtil.isNotEmpty(c.getDateTime()))
                                .collect(Collectors.groupingBy(c -> {
                                    String dateFormat = format(strToDate(c.getDateTime()), getFormatIntervalPattern(formatInterval));
                                    if (StringUtil.isNotEmpty(dateFormat)) {
                                        return dateFormat;
                                    }
                                    return c.getDateTime();
                                }, Collectors.summingDouble(c -> DoubleUtil.objToDouble(c.getDataValue()))));
                    }

                    for (String dateTime : dateList) {
                        //电桩-充电功率
                        if (chargePowerMap.containsKey(dateTime)) {
                            chargePowerList.add(getToDouble(chargePowerMap.get(dateTime)));
                        } else {
                            chargePowerList.add(null);
                        }
                        //电桩-放电功率
                        if (dischargePowerMap.containsKey(dateTime)) {
                            dischargePowerList.add(getToDouble(dischargePowerMap.get(dateTime)));
                        } else {
                            dischargePowerList.add(null);
                        }
                    }

                    chargePower.setDataList(chargePowerList);
                    dischargePower.setDataList(dischargePowerList);
                }
            }
        }
        dataInfoList.add(chargePower);
        dataInfoList.add(dischargePower);
        result.setDataInfoList(dataInfoList);
        return result;
    }

    //获取电桩电量曲线数据
    private SystemCurveDto getPileQtCurve(String dataId, String startTime, String endTime, Integer formatInterval, Set<String> dateList) {
        //返回的对象
        SystemCurveDto result = new SystemCurveDto();

        result.setDateList(dateList);

        //存储数据列表
        List<SystemCurveDto.DataInfo> dataInfoList = Lists.newArrayList();

        //电桩-充电电量
        SystemCurveDto.DataInfo chargeQt = new SystemCurveDto.DataInfo();
        chargeQt.setName(SystemCurveEnum.CHARGE_QT.getName());
        chargeQt.setCode(SystemCurveEnum.CHARGE_QT.getCode());
        List<Object> chargeQtList = Lists.newArrayList();

        //电桩-放电电量
        SystemCurveDto.DataInfo dischargeQt = new SystemCurveDto.DataInfo();
        dischargeQt.setName(SystemCurveEnum.DISCHARGE_QT.getName());
        dischargeQt.setCode(SystemCurveEnum.DISCHARGE_QT.getCode());
        List<Object> dischargeQtList = Lists.newArrayList();

        //根据数据id查询电桩数据
        DeviceBasicInfoDto deviceInfo = deviceService.findDeviceBasicInfoByIds(Collections.singletonList(dataId)).getData().get(dataId);
        if (deviceInfo != null && StringUtil.isNotEmpty(deviceInfo.getDeviceNumber())) {

            List<OrderRecordEntity> orderRecordList = orderRecordDao.findAllByPileCodeInAndEndTimeBetween(Collections.singletonList(deviceInfo.getDeviceNumber()), startTime, endTime);
            //电桩-充电电量
            List<OrderRecordEntity> chargeOrderList = orderRecordList.stream().filter(o -> StringUtil.isNotEmpty(o.getRunMode()) && o.getRunMode() == 0 && StringUtil.isNotEmpty(o.getTotalQt())).collect(Collectors.toList());
            Map<String, Double> chargeQtrMap = Maps.newHashMap();
            if (CollectionUtils.isNotEmpty(chargeOrderList)) {
                chargeQtrMap = chargeOrderList.stream().collect(Collectors.groupingBy(c -> {
                    String dateFormat = format(strToDate(c.getEndTime()), getFormatIntervalPattern(formatInterval));
                    if (StringUtil.isNotEmpty(dateFormat)) {
                        return dateFormat;
                    }
                    return c.getEndTime();
                }, Collectors.summingDouble(OrderRecordEntity::getTotalQt)));

            }

            //电桩-放电电量
            List<OrderRecordEntity> dischargeOrderList = orderRecordList.stream().filter(o -> StringUtil.isNotEmpty(o.getRunMode()) && (o.getRunMode() == 1 || o.getRunMode() == 2) && StringUtil.isNotEmpty(o.getTotalQt())).collect(Collectors.toList());
            Map<String, Double> dischargeQtMap = Maps.newHashMap();
            if (CollectionUtils.isNotEmpty(dischargeOrderList)) {
                dischargeQtMap = dischargeOrderList.stream().collect(Collectors.groupingBy(c -> {
                    String dateFormat = format(strToDate(c.getEndTime()), getFormatIntervalPattern(formatInterval));
                    if (StringUtil.isNotEmpty(dateFormat)) {
                        return dateFormat;
                    }
                    return c.getEndTime();
                }, Collectors.summingDouble(OrderRecordEntity::getTotalQt)));
            }
            for (String dateTime : dateList) {
                //电桩-充电电量
                if (chargeQtrMap.containsKey(dateTime)) {
                    chargeQtList.add(getToDouble(chargeQtrMap.get(dateTime)));
                } else {
                    chargeQtList.add(0.0);
                }
                //电桩-放电电量
                if (dischargeQtMap.containsKey(dateTime)) {
                    dischargeQtList.add(getToDouble(dischargeQtMap.get(dateTime)));
                } else {
                    dischargeQtList.add(0.0);
                }
            }
            chargeQt.setDataList(chargeQtList);
            dischargeQt.setDataList(dischargeQtList);
        }
        dataInfoList.add(chargeQt);
        dataInfoList.add(dischargeQt);
        result.setDataInfoList(dataInfoList);
        return result;
    }

    //获取储能设备电池簇电芯电压曲线数据
    private SystemCurveDto getSeBatteryCellVoltageCurve(String dataId, String startTime, String endTime, String timeInterval, Integer formatInterval,
                                                        Set<String> dateList, Integer dataIndex) {
        //返回的对象
        SystemCurveDto result = new SystemCurveDto();

        result.setDateList(dateList);

        //存储数据列表
        List<SystemCurveDto.DataInfo> dataInfoList = Lists.newArrayList();

        //储能设备电池簇-电芯电压
        SystemCurveDto.DataInfo cellVoltage = new SystemCurveDto.DataInfo();
        cellVoltage.setName(SystemCurveEnum.CELL_VOLTAGE.getName());
        cellVoltage.setCode(SystemCurveEnum.CELL_VOLTAGE.getCode());
        List<Object> cellVoltageList = Lists.newArrayList();

        //根据数据id查询储能电池簇设备数据
        DeviceBasicInfoDto deviceInfo = deviceService.findDeviceBasicInfoByIds(Collections.singletonList(dataId)).getData().get(dataId);
        if (deviceInfo != null) {
            //查询功率数据
            DeviceHistoryQueryVo deviceHistoryQueryVo = new DeviceHistoryQueryVo();
            deviceHistoryQueryVo.setDeviceIds(Collections.singleton(dataId));
            deviceHistoryQueryVo.setFunctionLogos(Collections.singleton(FunctionLogoParamVo.CELL_VOLTAGE));
            deviceHistoryQueryVo.setStartTime(startTime);
            deviceHistoryQueryVo.setEndTime(endTime);
            deviceHistoryQueryVo.setTimeInterval(timeInterval);
            //查询设备功能点历史数据
            Map<String, Map<String, List<DeviceHistoryDto>>> deviceHistoryMap = dataService.findDeviceHistoryValueList(deviceHistoryQueryVo).getData();
            if (MapUtils.isNotEmpty(deviceHistoryMap) && deviceHistoryMap.containsKey(dataId)) {
                // 存储所有历史数据
                Map<String, List<DeviceHistoryDto>> functionDataMap = deviceHistoryMap.get(dataId);
                if (MapUtils.isNotEmpty(functionDataMap)) {
                    //储能设备电池簇-电芯电压
                    Map<String, List<DeviceHistoryDto>> cellVoltageMap = Maps.newHashMap();
                    if (functionDataMap.containsKey(FunctionLogoParamVo.CELL_VOLTAGE)) {
                        //根据时间分组
                        cellVoltageMap = functionDataMap.get(FunctionLogoParamVo.CELL_VOLTAGE).stream().filter(c -> StringUtil.isNotEmpty(c.getDateTime())).collect(Collectors.groupingBy(c -> {
                            String dateFormat = format(strToDate(c.getDateTime()), getFormatIntervalPattern(formatInterval));
                            if (StringUtil.isNotEmpty(dateFormat)) {
                                return dateFormat;
                            }
                            return c.getDateTime();
                        }));
                    }
                    for (String dateTime : dateList) {
                        //储能设备电池簇-电芯电压
                        if (cellVoltageMap.containsKey(dateTime) && StringUtil.isNotEmpty(dataIndex)) {
                            cellVoltageList.add(getToDouble(cellVoltageMap.get(dateTime).stream().filter(d -> StringUtil.isNotEmpty(d.getDataValue()))
                                    .mapToDouble(d -> {
                                        List<Double> values = JSON.parseArray(String.valueOf(d.getDataValue()), Double.class);
                                        if (dataIndex < values.size()) {
                                            return getToDouble(values.get(dataIndex), 6);
                                        }
                                        return 0.0;
                                    }).sum()));
                        } else {
                            cellVoltageList.add(null);
                        }
                    }
                    cellVoltage.setDataList(cellVoltageList);
                }
            }
        }
        dataInfoList.add(cellVoltage);
        result.setDataInfoList(dataInfoList);
        return result;
    }

    //获取储能设备电池簇电芯电压曲线数据
    private SystemCurveDto getSeBatteryCellTempCurve(String dataId, String startTime, String endTime, String timeInterval,
                                                     Integer formatInterval, Set<String> dateList, Integer dataIndex) {
        //返回的对象
        SystemCurveDto result = new SystemCurveDto();

        result.setDateList(dateList);

        //存储数据列表
        List<SystemCurveDto.DataInfo> dataInfoList = Lists.newArrayList();

        //储能设备电池簇-电芯温度
        SystemCurveDto.DataInfo cellTemperature = new SystemCurveDto.DataInfo();
        cellTemperature.setName(SystemCurveEnum.CELL_TEMPERATURE.getName());
        cellTemperature.setCode(SystemCurveEnum.CELL_TEMPERATURE.getCode());
        List<Object> cellTemperatureList = Lists.newArrayList();

        //根据数据id查询储能电池簇设备数据
        DeviceBasicInfoDto deviceInfo = deviceService.findDeviceBasicInfoByIds(Collections.singletonList(dataId)).getData().get(dataId);
        if (deviceInfo != null) {
            //查询功率数据
            DeviceHistoryQueryVo deviceHistoryQueryVo = new DeviceHistoryQueryVo();
            deviceHistoryQueryVo.setDeviceIds(Collections.singleton(dataId));
            deviceHistoryQueryVo.setFunctionLogos(Collections.singleton(FunctionLogoParamVo.CELL_TEMP));
            deviceHistoryQueryVo.setStartTime(startTime);
            deviceHistoryQueryVo.setEndTime(endTime);
            deviceHistoryQueryVo.setTimeInterval(timeInterval);
            //查询设备功能点历史数据
            Map<String, Map<String, List<DeviceHistoryDto>>> deviceHistoryMap = dataService.findDeviceHistoryValueList(deviceHistoryQueryVo).getData();
            if (MapUtils.isNotEmpty(deviceHistoryMap) && deviceHistoryMap.containsKey(dataId)) {
                // 存储所有历史数据
                Map<String, List<DeviceHistoryDto>> functionDataMap = deviceHistoryMap.get(dataId);
                if (MapUtils.isNotEmpty(functionDataMap)) {
                    //储能设备电池簇-电芯温度
                    Map<String, List<DeviceHistoryDto>> cellTemperatureMap = Maps.newHashMap();
                    if (functionDataMap.containsKey(FunctionLogoParamVo.CELL_TEMP)) {
                        //根据时间分组
                        cellTemperatureMap = functionDataMap.get(FunctionLogoParamVo.CELL_TEMP).stream().filter(c -> StringUtil.isNotEmpty(c.getDateTime())).collect(Collectors.groupingBy(c -> {
                            String dateFormat = format(strToDate(c.getDateTime()), getFormatIntervalPattern(formatInterval));
                            if (StringUtil.isNotEmpty(dateFormat)) {
                                return dateFormat;
                            }
                            return c.getDateTime();
                        }));
                    }
                    for (String dateTime : dateList) {
                        //储能设备电池簇-电芯温度
                        if (cellTemperatureMap.containsKey(dateTime) && StringUtil.isNotEmpty(dataIndex)) {
                            cellTemperatureList.add(getToDouble(cellTemperatureMap.get(dateTime).stream().filter(d -> StringUtil.isNotEmpty(d.getDataValue()))
                                    .mapToDouble(d -> {
                                        List<Double> values = JSON.parseArray(String.valueOf(d.getDataValue()), Double.class);
                                        if (dataIndex < values.size()) {
                                            return getToDouble(values.get(dataIndex), 6);
                                        }
                                        return 0.0;
                                    }).sum()));
                        } else {
                            cellTemperatureList.add(null);
                        }
                    }
                    cellTemperature.setDataList(cellTemperatureList);
                }
            }
        }
        dataInfoList.add(cellTemperature);
        result.setDataInfoList(dataInfoList);
        return result;
    }

    //获取换电系统功率曲线数据
    private SystemCurveDto getSystemChangePowerCurve(String dataId, String startTime, String endTime, String timeInterval, Integer formatInterval, Set<String> dateList) {
        //返回的对象
        SystemCurveDto result = new SystemCurveDto();

        result.setDateList(dateList);

        //存储数据列表
        List<SystemCurveDto.DataInfo> dataInfoList = Lists.newArrayList();

        //换电系统-充电功率
        SystemCurveDto.DataInfo changePower = new SystemCurveDto.DataInfo();
        changePower.setName(SystemCurveEnum.CHARGE_POWER.getName());
        changePower.setCode(SystemCurveEnum.CHARGE_POWER.getCode());
        List<Object> changePowerList = Lists.newArrayList();

        //根据数据id查询站点id
        String siteId = deviceService.findSiteIdBySubId(dataId).getData();
        //获取换电仓设备
        List<SystemTreeDto> systemTreeList = this.getSystemMonitorData(siteId, dataId, 6, null).stream().filter(s -> StringUtil.isNotEmpty(s.getTypeId()) && Objects.equals(s.getTypeId(), "70")).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(systemTreeList)) {
            Set<String> deviceIds = systemTreeList.stream().map(SystemTreeDto::getId).collect(Collectors.toSet());
            //查询功率数据
            DeviceHistoryQueryVo deviceHistoryQueryVo = new DeviceHistoryQueryVo();
            deviceHistoryQueryVo.setDeviceIds(deviceIds);
            deviceHistoryQueryVo.setFunctionLogos(Collections.singleton(FunctionLogoParamVo.BATTERY_GRANARY_OUT_POWER));
            deviceHistoryQueryVo.setStartTime(startTime);
            deviceHistoryQueryVo.setEndTime(endTime);
            deviceHistoryQueryVo.setTimeInterval(timeInterval);
            //查询换电仓功能点历史数据
            Map<String, Map<String, List<DeviceHistoryDto>>> deviceHistoryMap = dataService.findDeviceHistoryValueList(deviceHistoryQueryVo).getData();
            if (MapUtils.isNotEmpty(deviceHistoryMap)) {
                // 存储所有历史数据
                List<DeviceHistoryDto> deviceHistoryList = deviceHistoryMap.values().stream().flatMap(d -> d.values().stream().flatMap(Collection::stream)).collect(Collectors.toList());

                if (CollectionUtils.isNotEmpty(deviceHistoryList)) {
                    //根据时间分组
                    Map<String, List<DeviceHistoryDto>> changePowerMap = deviceHistoryList.stream().filter(c -> StringUtil.isNotEmpty(c.getDateTime())).collect(Collectors.groupingBy(c -> {
                        String dateFormat = format(strToDate(c.getDateTime()), getFormatIntervalPattern(formatInterval));
                        if (StringUtil.isNotEmpty(dateFormat)) {
                            return dateFormat;
                        }
                        return c.getDateTime();
                    }));
                    dateList.forEach(dateTime -> {
                        //换电系统-充电功率
                        if (changePowerMap.containsKey(dateTime)) {
                            changePowerList.add(getToDouble(changePowerMap.get(dateTime).stream().filter(d -> StringUtil.isNotEmpty(d.getDataValue())).mapToDouble(d -> Double.parseDouble(String.valueOf(d.getDataValue()))).sum()));
                        } else {
                            changePowerList.add(null);
                        }
                    });
                    changePower.setDataList(changePowerList);
                }
            }
        }
        dataInfoList.add(changePower);
        result.setDataInfoList(dataInfoList);
        return result;
    }

    //获取换电系统电量曲线数据
    private SystemCurveDto getSystemChangeQtCurve(String dataId, String startTime, String endTime, String timeInterval, Integer formatInterval, Set<String> dateList) {
        //返回的对象
        SystemCurveDto result = new SystemCurveDto();

        result.setDateList(dateList);

        //存储数据列表
        List<SystemCurveDto.DataInfo> dataInfoList = Lists.newArrayList();

        //换电系统-充电量
        SystemCurveDto.DataInfo changeChargeQt = new SystemCurveDto.DataInfo();
        changeChargeQt.setName(SystemCurveEnum.CHARGE_QT.getName());
        changeChargeQt.setCode(SystemCurveEnum.CHARGE_QT.getCode());
        List<Object> changeChargeQtList = Lists.newArrayList();

        //根据数据id查询站点id
        String siteId = deviceService.findSiteIdBySubId(dataId).getData();
        //获取换电仓设备
        List<SystemTreeDto> systemTreeList = this.getSystemMonitorData(siteId, dataId, 6, null).stream().filter(s -> StringUtil.isNotEmpty(s.getTypeId()) && Objects.equals(s.getTypeId(), "70")).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(systemTreeList)) {
            Set<String> deviceIds = systemTreeList.stream().map(SystemTreeDto::getId).collect(Collectors.toSet());
            //查询电量数据
            DeviceHistoryQueryVo deviceHistoryQueryVo = new DeviceHistoryQueryVo();
            deviceHistoryQueryVo.setDeviceIds(deviceIds);
            deviceHistoryQueryVo.setFunctionLogos(Collections.singleton(FunctionLogoParamVo.BATTERY_GRANARY_SUP_CHARGE));
            deviceHistoryQueryVo.setStartTime(startTime);
            deviceHistoryQueryVo.setEndTime(endTime);
            deviceHistoryQueryVo.setTimeInterval(timeInterval);
            //查询换电仓充电量历史数据
            Map<String, Map<String, List<NodeDifHistoryDto>>> deviceHistoryMap = dataService.findNodeDifHistoryListFeign(deviceHistoryQueryVo).getData();
            if (MapUtils.isNotEmpty(deviceHistoryMap)) {

                //换电仓充电量
                Map<String, List<NodeDifHistoryDto>> functionDataMap = deviceHistoryMap.values().stream()
                        .flatMap(c -> c.values().stream().flatMap(Collection::stream)).filter(c -> StringUtil.isNotEmpty(c.getFirstDateTime()))
                        .collect(Collectors.groupingBy(c -> {
                            String dateFormat = format(strToDate(c.getFirstDateTime()), getFormatIntervalPattern(formatInterval));
                            if (StringUtil.isNotEmpty(dateFormat)) {
                                return dateFormat;
                            }
                            return c.getFirstDateTime();
                        }));

                for (String dateTime : dateList) {
                    //换电仓设备-充电量
                    if (functionDataMap.containsKey(dateTime)) {
                        changeChargeQtList.add(getToDouble(functionDataMap.get(dateTime).stream().filter(d -> StringUtil.isNotEmpty(d.getFirstDataValue()) && StringUtil.isNotEmpty(d.getLastDataValue()))
                                .mapToDouble(d -> Double.parseDouble(String.valueOf(d.getLastDataValue())) - Double.parseDouble(String.valueOf(d.getFirstDataValue()))).sum()));
                    } else {
                        changeChargeQtList.add(null);
                    }
                }
                changeChargeQt.setDataList(changeChargeQtList);
            }
        }
        dataInfoList.add(changeChargeQt);
        result.setDataInfoList(dataInfoList);
        return result;
    }

    //获取电表功率因数曲线数据
    private SystemCurveDto getMeterPowerFactorCurve(String dataId, String startTime, String endTime, String timeInterval, Integer formatInterval, Set<String> dateList) {
        //返回的对象
        SystemCurveDto result = new SystemCurveDto();

        result.setDateList(dateList);

        //存储数据列表
        List<SystemCurveDto.DataInfo> dataInfoList = Lists.newArrayList();

        //电表-功率因数
        SystemCurveDto.DataInfo powerFactor = new SystemCurveDto.DataInfo();
        powerFactor.setName(SystemCurveEnum.POWER_FACTOR.getName());
        powerFactor.setCode(SystemCurveEnum.POWER_FACTOR.getCode());
        List<Object> powerFactorList = Lists.newArrayList();
        //电表-A相功率因数
        SystemCurveDto.DataInfo aPhasePowerFactor = new SystemCurveDto.DataInfo();
        aPhasePowerFactor.setName(SystemCurveEnum.A_PHASE_POWER_FACTOR.getName());
        aPhasePowerFactor.setCode(SystemCurveEnum.A_PHASE_POWER_FACTOR.getCode());
        List<Object> aPhasePowerFactorList = Lists.newArrayList();
        //电表-B相功率因数
        SystemCurveDto.DataInfo bPhasePowerFactor = new SystemCurveDto.DataInfo();
        bPhasePowerFactor.setName(SystemCurveEnum.B_PHASE_POWER_FACTOR.getName());
        bPhasePowerFactor.setCode(SystemCurveEnum.B_PHASE_POWER_FACTOR.getCode());
        List<Object> bPhasePowerFactorList = Lists.newArrayList();
        //电表-C相功率因数
        SystemCurveDto.DataInfo cPhasePowerFactor = new SystemCurveDto.DataInfo();
        cPhasePowerFactor.setName(SystemCurveEnum.C_PHASE_POWER_FACTOR.getName());
        cPhasePowerFactor.setCode(SystemCurveEnum.C_PHASE_POWER_FACTOR.getCode());
        List<Object> cPhasePowerFactorList = Lists.newArrayList();

        //根据数据id查询电表数据
        DeviceBasicInfoDto deviceInfo = deviceService.findDeviceBasicInfoByIds(Collections.singletonList(dataId)).getData().get(dataId);
        if (deviceInfo != null) {

            //查询功率因数数据
            DeviceHistoryQueryVo deviceHistoryQueryVo = new DeviceHistoryQueryVo();
            deviceHistoryQueryVo.setDeviceIds(Collections.singleton(dataId));
            deviceHistoryQueryVo.setFunctionLogos(new HashSet<>(Arrays.asList(FunctionLogoParamVo.POWER_FACTOR, FunctionLogoParamVo.A_PHASE_POWER_FACTOR, FunctionLogoParamVo.B_PHASE_POWER_FACTOR, FunctionLogoParamVo.C_PHASE_POWER_FACTOR)));
            deviceHistoryQueryVo.setStartTime(startTime);
            deviceHistoryQueryVo.setEndTime(endTime);
            deviceHistoryQueryVo.setTimeInterval(timeInterval);
            //查询设备功能点历史数据
            Map<String, Map<String, List<DeviceHistoryDto>>> deviceHistoryMap = dataService.findDeviceHistoryValueList(deviceHistoryQueryVo).getData();
            if (MapUtils.isNotEmpty(deviceHistoryMap)) {
                // 存储所有历史数据
                Map<String, List<DeviceHistoryDto>> functionDataMap = deviceHistoryMap.get(dataId);

                if (MapUtils.isNotEmpty(functionDataMap)) {
                    Map<String, List<DeviceHistoryDto>> powerFactorMap = Maps.newHashMap();
                    Map<String, List<DeviceHistoryDto>> aPhasePowerFactorMap = Maps.newHashMap();
                    Map<String, List<DeviceHistoryDto>> bPhasePowerFactorMap = Maps.newHashMap();
                    Map<String, List<DeviceHistoryDto>> cPhasePowerFactorMap = Maps.newHashMap();
                    //电表-功率因数
                    if (functionDataMap.containsKey(FunctionLogoParamVo.POWER_FACTOR)) {
                        powerFactorMap = functionDataMap.get(FunctionLogoParamVo.POWER_FACTOR).stream().filter(c -> StringUtil.isNotEmpty(c.getDateTime())).collect(Collectors.groupingBy(c -> {
                            String dateFormat = format(strToDate(c.getDateTime()), getFormatIntervalPattern(formatInterval));
                            if (StringUtil.isNotEmpty(dateFormat)) {
                                return dateFormat;
                            }
                            return c.getDateTime();
                        }));
                    }
                    //电表-A相功率因数
                    if (functionDataMap.containsKey(FunctionLogoParamVo.A_PHASE_POWER_FACTOR)) {
                        aPhasePowerFactorMap = functionDataMap.get(FunctionLogoParamVo.A_PHASE_POWER_FACTOR).stream().filter(c -> StringUtil.isNotEmpty(c.getDateTime())).collect(Collectors.groupingBy(c -> {
                            String dateFormat = format(strToDate(c.getDateTime()), getFormatIntervalPattern(formatInterval));
                            if (StringUtil.isNotEmpty(dateFormat)) {
                                return dateFormat;
                            }
                            return c.getDateTime();
                        }));
                    }
                    //电表-B相功率因数
                    if (functionDataMap.containsKey(FunctionLogoParamVo.B_PHASE_POWER_FACTOR)) {
                        bPhasePowerFactorMap = functionDataMap.get(FunctionLogoParamVo.B_PHASE_POWER_FACTOR).stream().filter(c -> StringUtil.isNotEmpty(c.getDateTime())).collect(Collectors.groupingBy(c -> {
                            String dateFormat = format(strToDate(c.getDateTime()), getFormatIntervalPattern(formatInterval));
                            if (StringUtil.isNotEmpty(dateFormat)) {
                                return dateFormat;
                            }
                            return c.getDateTime();
                        }));
                    }
                    //电表-C相功率因数
                    if (functionDataMap.containsKey(FunctionLogoParamVo.C_PHASE_POWER_FACTOR)) {
                        cPhasePowerFactorMap = functionDataMap.get(FunctionLogoParamVo.C_PHASE_POWER_FACTOR).stream().filter(c -> StringUtil.isNotEmpty(c.getDateTime())).collect(Collectors.groupingBy(c -> {
                            String dateFormat = format(strToDate(c.getDateTime()), getFormatIntervalPattern(formatInterval));
                            if (StringUtil.isNotEmpty(dateFormat)) {
                                return dateFormat;
                            }
                            return c.getDateTime();
                        }));
                    }

                    for (String dateTime : dateList) {
                        //电表-功率因数
                        if (powerFactorMap.containsKey(dateTime)) {
                            powerFactorList.add(getToDouble(powerFactorMap.get(dateTime).stream().filter(d -> StringUtil.isNotEmpty(d.getDataValue())).mapToDouble(d -> Double.parseDouble(String.valueOf(d.getDataValue()))).sum()));
                        } else {
                            powerFactorList.add(null);
                        }
                        //电表-A相功率因数
                        if (aPhasePowerFactorMap.containsKey(dateTime)) {
                            aPhasePowerFactorList.add(getToDouble(aPhasePowerFactorMap.get(dateTime).stream().filter(d -> StringUtil.isNotEmpty(d.getDataValue())).mapToDouble(d -> Double.parseDouble(String.valueOf(d.getDataValue()))).sum()));
                        } else {
                            aPhasePowerFactorList.add(null);
                        }
                        //电表-B相功率因数
                        if (bPhasePowerFactorMap.containsKey(dateTime)) {
                            bPhasePowerFactorList.add(getToDouble(bPhasePowerFactorMap.get(dateTime).stream().filter(d -> StringUtil.isNotEmpty(d.getDataValue())).mapToDouble(d -> Double.parseDouble(String.valueOf(d.getDataValue()))).sum()));
                        } else {
                            bPhasePowerFactorList.add(null);
                        }
                        //电表-C相功率因数
                        if (cPhasePowerFactorMap.containsKey(dateTime)) {
                            cPhasePowerFactorList.add(getToDouble(cPhasePowerFactorMap.get(dateTime).stream().filter(d -> StringUtil.isNotEmpty(d.getDataValue())).mapToDouble(d -> Double.parseDouble(String.valueOf(d.getDataValue()))).sum()));
                        } else {
                            cPhasePowerFactorList.add(null);
                        }
                    }
                    powerFactor.setDataList(powerFactorList);
                    aPhasePowerFactor.setDataList(aPhasePowerFactorList);
                    bPhasePowerFactor.setDataList(bPhasePowerFactorList);
                    cPhasePowerFactor.setDataList(cPhasePowerFactorList);
                }
            }

        }
        dataInfoList.add(powerFactor);
        dataInfoList.add(aPhasePowerFactor);
        dataInfoList.add(bPhasePowerFactor);
        dataInfoList.add(cPhasePowerFactor);
        result.setDataInfoList(dataInfoList);
        return result;
    }

    //获取电表有功功率曲线数据
    private SystemCurveDto getMeterActivePowerCurve(String dataId, String startTime, String endTime, String timeInterval, Integer formatInterval, Set<String> dateList) {
        //返回的对象
        SystemCurveDto result = new SystemCurveDto();

        result.setDateList(dateList);

        //存储数据列表
        List<SystemCurveDto.DataInfo> dataInfoList = Lists.newArrayList();

        //电表-总有功
        SystemCurveDto.DataInfo totalActivePower = new SystemCurveDto.DataInfo();
        totalActivePower.setName(SystemCurveEnum.TOTAL_ACTIVE_POWER.getName());
        totalActivePower.setCode(SystemCurveEnum.TOTAL_ACTIVE_POWER.getCode());
        List<Object> totalActivePowerList = Lists.newArrayList();
        //电表-A相有功
        SystemCurveDto.DataInfo aPhaseActivePower = new SystemCurveDto.DataInfo();
        aPhaseActivePower.setName(SystemCurveEnum.A_PHASE_ACTIVE_POWER.getName());
        aPhaseActivePower.setCode(SystemCurveEnum.A_PHASE_ACTIVE_POWER.getCode());
        List<Object> aPhaseActivePowerList = Lists.newArrayList();
        //电表-B相有功
        SystemCurveDto.DataInfo bPhaseActivePower = new SystemCurveDto.DataInfo();
        bPhaseActivePower.setName(SystemCurveEnum.B_PHASE_ACTIVE_POWER.getName());
        bPhaseActivePower.setCode(SystemCurveEnum.B_PHASE_ACTIVE_POWER.getCode());
        List<Object> bPhaseActivePowerList = Lists.newArrayList();
        //电表-C相有功
        SystemCurveDto.DataInfo cPhaseActivePower = new SystemCurveDto.DataInfo();
        cPhaseActivePower.setName(SystemCurveEnum.C_PHASE_ACTIVE_POWER.getName());
        cPhaseActivePower.setCode(SystemCurveEnum.C_PHASE_ACTIVE_POWER.getCode());
        List<Object> cPhaseActivePowerList = Lists.newArrayList();

        //根据数据id查询电表数据
        DeviceBasicInfoDto deviceInfo = deviceService.findDeviceBasicInfoByIds(Collections.singletonList(dataId)).getData().get(dataId);
        if (deviceInfo != null) {

            //查询功率数据
            DeviceHistoryQueryVo deviceHistoryQueryVo = new DeviceHistoryQueryVo();
            deviceHistoryQueryVo.setDeviceIds(Collections.singleton(dataId));
            deviceHistoryQueryVo.setFunctionLogos(new HashSet<>(Arrays.asList(FunctionLogoParamVo.TOTAL_ACTIVE_POWER, FunctionLogoParamVo.A_PHASE_ACTIVE_POWER,
                    FunctionLogoParamVo.B_PHASE_ACTIVE_POWER, FunctionLogoParamVo.C_PHASE_ACTIVE_POWER)));
            deviceHistoryQueryVo.setStartTime(startTime);
            deviceHistoryQueryVo.setEndTime(endTime);
            deviceHistoryQueryVo.setTimeInterval(timeInterval);
            //查询设备功能点历史数据
            Map<String, Map<String, List<DeviceHistoryDto>>> deviceHistoryMap = dataService.findDeviceHistoryValueList(deviceHistoryQueryVo).getData();
            if (MapUtils.isNotEmpty(deviceHistoryMap)) {
                // 存储所有历史数据
                Map<String, List<DeviceHistoryDto>> functionDataMap = deviceHistoryMap.get(dataId);

                if (MapUtils.isNotEmpty(functionDataMap)) {
                    Map<String, List<DeviceHistoryDto>> totalActivePowerMap = Maps.newHashMap();
                    Map<String, List<DeviceHistoryDto>> aPhaseActivePowerMap = Maps.newHashMap();
                    Map<String, List<DeviceHistoryDto>> bPhaseActivePowerMap = Maps.newHashMap();
                    Map<String, List<DeviceHistoryDto>> cPhaseActivePowerMap = Maps.newHashMap();
                    //电表-总有功
                    if (functionDataMap.containsKey(FunctionLogoParamVo.TOTAL_ACTIVE_POWER)) {
                        totalActivePowerMap = functionDataMap.get(FunctionLogoParamVo.TOTAL_ACTIVE_POWER).stream().filter(c -> StringUtil.isNotEmpty(c.getDateTime())).collect(Collectors.groupingBy(c -> {
                            String dateFormat = format(strToDate(c.getDateTime()), getFormatIntervalPattern(formatInterval));
                            if (StringUtil.isNotEmpty(dateFormat)) {
                                return dateFormat;
                            }
                            return c.getDateTime();
                        }));
                    }
                    //电表-A相有功
                    if (functionDataMap.containsKey(FunctionLogoParamVo.A_PHASE_ACTIVE_POWER)) {
                        aPhaseActivePowerMap = functionDataMap.get(FunctionLogoParamVo.A_PHASE_ACTIVE_POWER).stream().filter(c -> StringUtil.isNotEmpty(c.getDateTime())).collect(Collectors.groupingBy(c -> {
                            String dateFormat = format(strToDate(c.getDateTime()), getFormatIntervalPattern(formatInterval));
                            if (StringUtil.isNotEmpty(dateFormat)) {
                                return dateFormat;
                            }
                            return c.getDateTime();
                        }));
                    }
                    //电表-B相有功
                    if (functionDataMap.containsKey(FunctionLogoParamVo.B_PHASE_ACTIVE_POWER)) {
                        bPhaseActivePowerMap = functionDataMap.get(FunctionLogoParamVo.B_PHASE_ACTIVE_POWER).stream().filter(c -> StringUtil.isNotEmpty(c.getDateTime())).collect(Collectors.groupingBy(c -> {
                            String dateFormat = format(strToDate(c.getDateTime()), getFormatIntervalPattern(formatInterval));
                            if (StringUtil.isNotEmpty(dateFormat)) {
                                return dateFormat;
                            }
                            return c.getDateTime();
                        }));
                    }
                    //电表-C相有功
                    if (functionDataMap.containsKey(FunctionLogoParamVo.C_PHASE_ACTIVE_POWER)) {
                        cPhaseActivePowerMap = functionDataMap.get(FunctionLogoParamVo.C_PHASE_ACTIVE_POWER).stream().filter(c -> StringUtil.isNotEmpty(c.getDateTime())).collect(Collectors.groupingBy(c -> {
                            String dateFormat = format(strToDate(c.getDateTime()), getFormatIntervalPattern(formatInterval));
                            if (StringUtil.isNotEmpty(dateFormat)) {
                                return dateFormat;
                            }
                            return c.getDateTime();
                        }));
                    }

                    for (String dateTime : dateList) {
                        //电表-总有功
                        if (totalActivePowerMap.containsKey(dateTime)) {
                            totalActivePowerList.add(getToDouble(totalActivePowerMap.get(dateTime).stream().filter(d -> StringUtil.isNotEmpty(d.getDataValue())).mapToDouble(d -> Double.parseDouble(String.valueOf(d.getDataValue()))).sum()));
                        } else {
                            totalActivePowerList.add(null);
                        }
                        //电表-A相总有功
                        if (aPhaseActivePowerMap.containsKey(dateTime)) {
                            aPhaseActivePowerList.add(getToDouble(aPhaseActivePowerMap.get(dateTime).stream().filter(d -> StringUtil.isNotEmpty(d.getDataValue())).mapToDouble(d -> Double.parseDouble(String.valueOf(d.getDataValue()))).sum()));
                        } else {
                            aPhaseActivePowerList.add(null);
                        }
                        //电表-B相总有功
                        if (bPhaseActivePowerMap.containsKey(dateTime)) {
                            bPhaseActivePowerList.add(getToDouble(bPhaseActivePowerMap.get(dateTime).stream().filter(d -> StringUtil.isNotEmpty(d.getDataValue())).mapToDouble(d -> Double.parseDouble(String.valueOf(d.getDataValue()))).sum()));
                        } else {
                            bPhaseActivePowerList.add(null);
                        }
                        //电表-C相总有功
                        if (cPhaseActivePowerMap.containsKey(dateTime)) {
                            cPhaseActivePowerList.add(getToDouble(cPhaseActivePowerMap.get(dateTime).stream().filter(d -> StringUtil.isNotEmpty(d.getDataValue())).mapToDouble(d -> Double.parseDouble(String.valueOf(d.getDataValue()))).sum()));
                        } else {
                            cPhaseActivePowerList.add(null);
                        }
                    }
                    totalActivePower.setDataList(totalActivePowerList);
                    aPhaseActivePower.setDataList(aPhaseActivePowerList);
                    bPhaseActivePower.setDataList(bPhaseActivePowerList);
                    cPhaseActivePower.setDataList(cPhaseActivePowerList);
                }
            }

        }
        dataInfoList.add(totalActivePower);
        dataInfoList.add(aPhaseActivePower);
        dataInfoList.add(bPhaseActivePower);
        dataInfoList.add(cPhaseActivePower);
        result.setDataInfoList(dataInfoList);
        return result;
    }

    //获取电表无功功率曲线数据
    private SystemCurveDto getMeterReactivePowerCurve(String dataId, String startTime, String endTime, String timeInterval, Integer formatInterval, Set<String> dateList) {
        //返回的对象
        SystemCurveDto result = new SystemCurveDto();

        result.setDateList(dateList);

        //存储数据列表
        List<SystemCurveDto.DataInfo> dataInfoList = Lists.newArrayList();

        //电表-总无功
        SystemCurveDto.DataInfo totalReactivePower = new SystemCurveDto.DataInfo();
        totalReactivePower.setName(SystemCurveEnum.TOTAL_REACTIVE_POWER.getName());
        totalReactivePower.setCode(SystemCurveEnum.TOTAL_REACTIVE_POWER.getCode());
        List<Object> totalReactivePowerList = Lists.newArrayList();
        //电表-A相无功
        SystemCurveDto.DataInfo aPhaseReactivePower = new SystemCurveDto.DataInfo();
        aPhaseReactivePower.setName(SystemCurveEnum.A_PHASE_REACTIVE_POWER.getName());
        aPhaseReactivePower.setCode(SystemCurveEnum.A_PHASE_REACTIVE_POWER.getCode());
        List<Object> aPhaseReactivePowerList = Lists.newArrayList();
        //电表-B相无功
        SystemCurveDto.DataInfo bPhaseReactivePower = new SystemCurveDto.DataInfo();
        bPhaseReactivePower.setName(SystemCurveEnum.B_PHASE_REACTIVE_POWER.getName());
        bPhaseReactivePower.setCode(SystemCurveEnum.B_PHASE_REACTIVE_POWER.getCode());
        List<Object> bPhaseReactivePowerList = Lists.newArrayList();
        //电表-C相无功
        SystemCurveDto.DataInfo cPhaseReactivePower = new SystemCurveDto.DataInfo();
        cPhaseReactivePower.setName(SystemCurveEnum.C_PHASE_REACTIVE_POWER.getName());
        cPhaseReactivePower.setCode(SystemCurveEnum.C_PHASE_REACTIVE_POWER.getCode());
        List<Object> cPhaseReactivePowerList = Lists.newArrayList();

        //根据数据id查询电表数据
        DeviceBasicInfoDto deviceInfo = deviceService.findDeviceBasicInfoByIds(Collections.singletonList(dataId)).getData().get(dataId);
        if (deviceInfo != null) {

            //查询功率数据
            DeviceHistoryQueryVo deviceHistoryQueryVo = new DeviceHistoryQueryVo();
            deviceHistoryQueryVo.setDeviceIds(Collections.singleton(dataId));
            deviceHistoryQueryVo.setFunctionLogos(new HashSet<>(Arrays.asList(FunctionLogoParamVo.TOTAL_REACTIVE_POWER, FunctionLogoParamVo.A_PHASE_REACTIVE_POWER,
                    FunctionLogoParamVo.B_PHASE_REACTIVE_POWER, FunctionLogoParamVo.C_PHASE_REACTIVE_POWER)));
            deviceHistoryQueryVo.setStartTime(startTime);
            deviceHistoryQueryVo.setEndTime(endTime);
            deviceHistoryQueryVo.setTimeInterval(timeInterval);
            //查询设备功能点历史数据
            Map<String, Map<String, List<DeviceHistoryDto>>> deviceHistoryMap = dataService.findDeviceHistoryValueList(deviceHistoryQueryVo).getData();
            if (MapUtils.isNotEmpty(deviceHistoryMap)) {
                // 存储所有历史数据
                Map<String, List<DeviceHistoryDto>> functionDataMap = deviceHistoryMap.get(dataId);

                if (MapUtils.isNotEmpty(functionDataMap)) {
                    Map<String, List<DeviceHistoryDto>> totalReactivePowerMap = Maps.newHashMap();
                    Map<String, List<DeviceHistoryDto>> aPhaseReactivePowerMap = Maps.newHashMap();
                    Map<String, List<DeviceHistoryDto>> bPhaseReactivePowerMap = Maps.newHashMap();
                    Map<String, List<DeviceHistoryDto>> cPhaseReactivePowerMap = Maps.newHashMap();
                    //电表-总无功
                    if (functionDataMap.containsKey(FunctionLogoParamVo.TOTAL_REACTIVE_POWER)) {
                        totalReactivePowerMap = functionDataMap.get(FunctionLogoParamVo.TOTAL_REACTIVE_POWER).stream().filter(c -> StringUtil.isNotEmpty(c.getDateTime())).collect(Collectors.groupingBy(c -> {
                            String dateFormat = format(strToDate(c.getDateTime()), getFormatIntervalPattern(formatInterval));
                            if (StringUtil.isNotEmpty(dateFormat)) {
                                return dateFormat;
                            }
                            return c.getDateTime();
                        }));
                    }
                    //电表-A相无功
                    if (functionDataMap.containsKey(FunctionLogoParamVo.A_PHASE_REACTIVE_POWER)) {
                        aPhaseReactivePowerMap = functionDataMap.get(FunctionLogoParamVo.A_PHASE_REACTIVE_POWER).stream().filter(c -> StringUtil.isNotEmpty(c.getDateTime())).collect(Collectors.groupingBy(c -> {
                            String dateFormat = format(strToDate(c.getDateTime()), getFormatIntervalPattern(formatInterval));
                            if (StringUtil.isNotEmpty(dateFormat)) {
                                return dateFormat;
                            }
                            return c.getDateTime();
                        }));
                    }
                    //电表-B相无功
                    if (functionDataMap.containsKey(FunctionLogoParamVo.B_PHASE_REACTIVE_POWER)) {
                        bPhaseReactivePowerMap = functionDataMap.get(FunctionLogoParamVo.B_PHASE_REACTIVE_POWER).stream().filter(c -> StringUtil.isNotEmpty(c.getDateTime())).collect(Collectors.groupingBy(c -> {
                            String dateFormat = format(strToDate(c.getDateTime()), getFormatIntervalPattern(formatInterval));
                            if (StringUtil.isNotEmpty(dateFormat)) {
                                return dateFormat;
                            }
                            return c.getDateTime();
                        }));
                    }
                    //电表-C相无功
                    if (functionDataMap.containsKey(FunctionLogoParamVo.C_PHASE_REACTIVE_POWER)) {
                        cPhaseReactivePowerMap = functionDataMap.get(FunctionLogoParamVo.C_PHASE_REACTIVE_POWER).stream().filter(c -> StringUtil.isNotEmpty(c.getDateTime())).collect(Collectors.groupingBy(c -> {
                            String dateFormat = format(strToDate(c.getDateTime()), getFormatIntervalPattern(formatInterval));
                            if (StringUtil.isNotEmpty(dateFormat)) {
                                return dateFormat;
                            }
                            return c.getDateTime();
                        }));
                    }

                    for (String dateTime : dateList) {
                        //电表-总无功
                        if (totalReactivePowerMap.containsKey(dateTime)) {
                            totalReactivePowerList.add(getToDouble(totalReactivePowerMap.get(dateTime).stream().filter(d -> StringUtil.isNotEmpty(d.getDataValue())).mapToDouble(d -> Double.parseDouble(String.valueOf(d.getDataValue()))).sum()));
                        } else {
                            totalReactivePowerList.add(null);
                        }
                        //电表-A相总无功
                        if (aPhaseReactivePowerMap.containsKey(dateTime)) {
                            aPhaseReactivePowerList.add(getToDouble(aPhaseReactivePowerMap.get(dateTime).stream().filter(d -> StringUtil.isNotEmpty(d.getDataValue())).mapToDouble(d -> Double.parseDouble(String.valueOf(d.getDataValue()))).sum()));
                        } else {
                            aPhaseReactivePowerList.add(null);
                        }
                        //电表-B相总无功
                        if (bPhaseReactivePowerMap.containsKey(dateTime)) {
                            bPhaseReactivePowerList.add(getToDouble(bPhaseReactivePowerMap.get(dateTime).stream().filter(d -> StringUtil.isNotEmpty(d.getDataValue())).mapToDouble(d -> Double.parseDouble(String.valueOf(d.getDataValue()))).sum()));
                        } else {
                            bPhaseReactivePowerList.add(null);
                        }
                        //电表-C相总无功
                        if (cPhaseReactivePowerMap.containsKey(dateTime)) {
                            cPhaseReactivePowerList.add(getToDouble(cPhaseReactivePowerMap.get(dateTime).stream().filter(d -> StringUtil.isNotEmpty(d.getDataValue())).mapToDouble(d -> Double.parseDouble(String.valueOf(d.getDataValue()))).sum()));
                        } else {
                            cPhaseReactivePowerList.add(null);
                        }
                    }
                    totalReactivePower.setDataList(totalReactivePowerList);
                    aPhaseReactivePower.setDataList(aPhaseReactivePowerList);
                    bPhaseReactivePower.setDataList(bPhaseReactivePowerList);
                    cPhaseReactivePower.setDataList(cPhaseReactivePowerList);
                }
            }

        }
        dataInfoList.add(totalReactivePower);
        dataInfoList.add(aPhaseReactivePower);
        dataInfoList.add(bPhaseReactivePower);
        dataInfoList.add(cPhaseReactivePower);
        result.setDataInfoList(dataInfoList);
        return result;
    }

    //获取电表-分时电量曲线数据
    private SystemCurveDto getMeterShareQtCurve(String dataId, String startTime, String endTime, String timeInterval, Integer formatInterval, Set<String> dateList) {
        //返回的对象
        SystemCurveDto result = new SystemCurveDto();
        result.setDateList(dateList);

        //存储数据列表
        List<SystemCurveDto.DataInfo> dataInfoList = Lists.newArrayList();

        //电表-正向有功电量(尖)
        SystemCurveDto.DataInfo topSupKwh = new SystemCurveDto.DataInfo();
        topSupKwh.setName(SystemCurveEnum.TOP_SUP_KWH.getName());
        topSupKwh.setCode(SystemCurveEnum.TOP_SUP_KWH.getCode());
        List<Object> topSupKwhList = Lists.newArrayList();

        //电表-正向有功电量(峰)
        SystemCurveDto.DataInfo peakSupKwh = new SystemCurveDto.DataInfo();
        peakSupKwh.setName(SystemCurveEnum.PEAK_SUP_KWH.getName());
        peakSupKwh.setCode(SystemCurveEnum.PEAK_SUP_KWH.getCode());
        List<Object> peakSupKwhList = Lists.newArrayList();

        //电表-正向有功电量(平)
        SystemCurveDto.DataInfo plainSupKwh = new SystemCurveDto.DataInfo();
        plainSupKwh.setName(SystemCurveEnum.PLAIN_SUP_KWH.getName());
        plainSupKwh.setCode(SystemCurveEnum.PLAIN_SUP_KWH.getCode());
        List<Object> plainSupKwhList = Lists.newArrayList();

        //电表-正向有功电量(谷)
        SystemCurveDto.DataInfo valleySupKwh = new SystemCurveDto.DataInfo();
        valleySupKwh.setName(SystemCurveEnum.VALLEY_SUP_KWH.getName());
        valleySupKwh.setCode(SystemCurveEnum.VALLEY_SUP_KWH.getCode());
        List<Object> valleySupKwhList = Lists.newArrayList();

        //电表-正向有功电量(深谷)
        SystemCurveDto.DataInfo deepSupKwh = new SystemCurveDto.DataInfo();
        deepSupKwh.setName(SystemCurveEnum.DEEP_SUP_KWH.getName());
        deepSupKwh.setCode(SystemCurveEnum.DEEP_SUP_KWH.getCode());
        List<Object> deepSupKwhList = Lists.newArrayList();

        //电表-反向有功电量(尖)
        SystemCurveDto.DataInfo topRevKwh = new SystemCurveDto.DataInfo();
        topRevKwh.setName(SystemCurveEnum.TOP_REV_KWH.getName());
        topRevKwh.setCode(SystemCurveEnum.TOP_REV_KWH.getCode());
        List<Object> topRevKwhList = Lists.newArrayList();

        //电表-反向有功电量(峰)
        SystemCurveDto.DataInfo peakRevKwh = new SystemCurveDto.DataInfo();
        peakRevKwh.setName(SystemCurveEnum.PEAK_REV_KWH.getName());
        peakRevKwh.setCode(SystemCurveEnum.PEAK_REV_KWH.getCode());
        List<Object> peakRevKwhList = Lists.newArrayList();

        //电表-反向有功电量(平)
        SystemCurveDto.DataInfo plainRevKwh = new SystemCurveDto.DataInfo();
        plainRevKwh.setName(SystemCurveEnum.PLAIN_REV_KWH.getName());
        plainRevKwh.setCode(SystemCurveEnum.PLAIN_REV_KWH.getCode());
        List<Object> plainRevKwhList = Lists.newArrayList();

        //电表-反向有功电量(谷)
        SystemCurveDto.DataInfo valleyRevKwh = new SystemCurveDto.DataInfo();
        valleyRevKwh.setName(SystemCurveEnum.VALLEY_REV_KWH.getName());
        valleyRevKwh.setCode(SystemCurveEnum.VALLEY_REV_KWH.getCode());
        List<Object> valleyRevKwhList = Lists.newArrayList();

        //电表-反向有功电量(深谷)
        SystemCurveDto.DataInfo deepRevKwh = new SystemCurveDto.DataInfo();
        deepRevKwh.setName(SystemCurveEnum.DEEP_REV_KWH.getName());
        deepRevKwh.setCode(SystemCurveEnum.DEEP_REV_KWH.getCode());
        List<Object> deepRevKwhList = Lists.newArrayList();

        //根据数据id查询电表数据
        DeviceBasicInfoDto deviceInfo = deviceService.findDeviceBasicInfoByIds(Collections.singletonList(dataId)).getData().get(dataId);
        if (deviceInfo != null) {
            //查询电表功能点历史数据
            DeviceHistoryQueryVo deviceHistoryQueryVo = new DeviceHistoryQueryVo();
            deviceHistoryQueryVo.setDeviceIds(Collections.singleton(dataId));
            deviceHistoryQueryVo.setFunctionLogos(new HashSet<>(Arrays.asList(FunctionLogoParamVo.TOP_SUP_KWH, FunctionLogoParamVo.PEAK_SUP_KWH,
                    FunctionLogoParamVo.PLAIN_SUP_KWH, FunctionLogoParamVo.VALLEY_SUP_KWH, FunctionLogoParamVo.DEEP_SUP_KWH,
                    FunctionLogoParamVo.TOP_REV_KWH, FunctionLogoParamVo.PEAK_REV_KWH, FunctionLogoParamVo.PLAIN_REV_KWH,
                    FunctionLogoParamVo.VALLEY_REV_KWH, FunctionLogoParamVo.DEEP_REV_KWH)));
            deviceHistoryQueryVo.setStartTime(startTime);
            deviceHistoryQueryVo.setEndTime(endTime);
            deviceHistoryQueryVo.setTimeInterval(timeInterval);
            Map<String, Map<String, List<NodeDifHistoryDto>>> deviceHistoryMap = dataService.findNodeDifHistoryListFeign(deviceHistoryQueryVo).getData();
            if (MapUtils.isNotEmpty(deviceHistoryMap) && deviceHistoryMap.containsKey(dataId)) {
                Map<String, List<NodeDifHistoryDto>> functionDataMap = deviceHistoryMap.get(dataId);

                Map<String, List<NodeDifHistoryDto>> topSupKwhMap = Maps.newHashMap();
                Map<String, List<NodeDifHistoryDto>> peakSupKwhMap = Maps.newHashMap();
                Map<String, List<NodeDifHistoryDto>> plainSupKwhMap = Maps.newHashMap();
                Map<String, List<NodeDifHistoryDto>> valleySupKwhMap = Maps.newHashMap();
                Map<String, List<NodeDifHistoryDto>> deepSupKwhMap = Maps.newHashMap();
                Map<String, List<NodeDifHistoryDto>> topRevKwhMap = Maps.newHashMap();
                Map<String, List<NodeDifHistoryDto>> peakRevKwhMap = Maps.newHashMap();
                Map<String, List<NodeDifHistoryDto>> plainRevKwhMap = Maps.newHashMap();
                Map<String, List<NodeDifHistoryDto>> valleyRevKwhMap = Maps.newHashMap();
                Map<String, List<NodeDifHistoryDto>> deepRevKwhMap = Maps.newHashMap();

                //电表-正向有功电量(尖)
                if (functionDataMap.containsKey(FunctionLogoParamVo.TOP_SUP_KWH)) {
                    topSupKwhMap = functionDataMap.get(FunctionLogoParamVo.TOP_SUP_KWH).stream().filter(c -> StringUtil.isNotEmpty(c.getFirstDateTime()) && StringUtil.isNotEmpty(c.getFirstDataValue()) && StringUtil.isNotEmpty(c.getLastDataValue()))
                            .collect(Collectors.groupingBy(c -> {
                                String dateFormat = format(strToDate(c.getFirstDateTime()), getFormatIntervalPattern(formatInterval));
                                if (StringUtil.isNotEmpty(dateFormat)) {
                                    return dateFormat;
                                }
                                return c.getFirstDateTime();
                            }));
                }
                //电表-正向有功电量(峰)
                if (functionDataMap.containsKey(FunctionLogoParamVo.PEAK_SUP_KWH)) {
                    peakSupKwhMap = functionDataMap.get(FunctionLogoParamVo.PEAK_SUP_KWH).stream().filter(c -> StringUtil.isNotEmpty(c.getFirstDateTime()) && StringUtil.isNotEmpty(c.getFirstDataValue()) && StringUtil.isNotEmpty(c.getLastDataValue()))
                            .collect(Collectors.groupingBy(c -> {
                                String dateFormat = format(strToDate(c.getFirstDateTime()), getFormatIntervalPattern(formatInterval));
                                if (StringUtil.isNotEmpty(dateFormat)) {
                                    return dateFormat;
                                }
                                return c.getFirstDateTime();
                            }));
                }
                //电表-正向有功电量(平)
                if (functionDataMap.containsKey(FunctionLogoParamVo.PLAIN_SUP_KWH)) {
                    plainSupKwhMap = functionDataMap.get(FunctionLogoParamVo.PLAIN_SUP_KWH).stream().filter(c -> StringUtil.isNotEmpty(c.getFirstDateTime()) && StringUtil.isNotEmpty(c.getFirstDataValue()) && StringUtil.isNotEmpty(c.getLastDataValue()))
                            .collect(Collectors.groupingBy(c -> {
                                String dateFormat = format(strToDate(c.getFirstDateTime()), getFormatIntervalPattern(formatInterval));
                                if (StringUtil.isNotEmpty(dateFormat)) {
                                    return dateFormat;
                                }
                                return c.getFirstDateTime();
                            }));
                }
                //电表-正向有功电量(谷)
                if (functionDataMap.containsKey(FunctionLogoParamVo.VALLEY_SUP_KWH)) {
                    valleySupKwhMap = functionDataMap.get(FunctionLogoParamVo.VALLEY_SUP_KWH).stream().filter(c -> StringUtil.isNotEmpty(c.getFirstDateTime()) && StringUtil.isNotEmpty(c.getFirstDataValue()) && StringUtil.isNotEmpty(c.getLastDataValue()))
                            .collect(Collectors.groupingBy(c -> {
                                String dateFormat = format(strToDate(c.getFirstDateTime()), getFormatIntervalPattern(formatInterval));
                                if (StringUtil.isNotEmpty(dateFormat)) {
                                    return dateFormat;
                                }
                                return c.getFirstDateTime();
                            }));
                }
                //电表-正向有功电量(深谷)
                if (functionDataMap.containsKey(FunctionLogoParamVo.DEEP_SUP_KWH)) {
                    deepSupKwhMap = functionDataMap.get(FunctionLogoParamVo.DEEP_SUP_KWH).stream().filter(c -> StringUtil.isNotEmpty(c.getFirstDateTime()) && StringUtil.isNotEmpty(c.getFirstDataValue()) && StringUtil.isNotEmpty(c.getLastDataValue()))
                            .collect(Collectors.groupingBy(c -> {
                                String dateFormat = format(strToDate(c.getFirstDateTime()), getFormatIntervalPattern(formatInterval));
                                if (StringUtil.isNotEmpty(dateFormat)) {
                                    return dateFormat;
                                }
                                return c.getFirstDateTime();
                            }));
                }
                //电表-反向有功电量(尖)
                if (functionDataMap.containsKey(FunctionLogoParamVo.TOP_REV_KWH)) {
                    topRevKwhMap = functionDataMap.get(FunctionLogoParamVo.TOP_REV_KWH).stream().filter(c -> StringUtil.isNotEmpty(c.getFirstDateTime()) && StringUtil.isNotEmpty(c.getFirstDataValue()) && StringUtil.isNotEmpty(c.getLastDataValue()))
                            .collect(Collectors.groupingBy(c -> {
                                String dateFormat = format(strToDate(c.getFirstDateTime()), getFormatIntervalPattern(formatInterval));
                                if (StringUtil.isNotEmpty(dateFormat)) {
                                    return dateFormat;
                                }
                                return c.getFirstDateTime();
                            }));
                }
                //电表-反向有功电量(峰)
                if (functionDataMap.containsKey(FunctionLogoParamVo.PEAK_REV_KWH)) {
                    peakRevKwhMap = functionDataMap.get(FunctionLogoParamVo.PEAK_REV_KWH).stream().filter(c -> StringUtil.isNotEmpty(c.getFirstDateTime()) && StringUtil.isNotEmpty(c.getFirstDataValue()) && StringUtil.isNotEmpty(c.getLastDataValue()))
                            .collect(Collectors.groupingBy(c -> {
                                String dateFormat = format(strToDate(c.getFirstDateTime()), getFormatIntervalPattern(formatInterval));
                                if (StringUtil.isNotEmpty(dateFormat)) {
                                    return dateFormat;
                                }
                                return c.getFirstDateTime();
                            }));
                }
                //电表-反向有功电量(平)
                if (functionDataMap.containsKey(FunctionLogoParamVo.PLAIN_REV_KWH)) {
                    plainRevKwhMap = functionDataMap.get(FunctionLogoParamVo.PLAIN_REV_KWH).stream().filter(c -> StringUtil.isNotEmpty(c.getFirstDateTime()) && StringUtil.isNotEmpty(c.getFirstDataValue()) && StringUtil.isNotEmpty(c.getLastDataValue()))
                            .collect(Collectors.groupingBy(c -> {
                                String dateFormat = format(strToDate(c.getFirstDateTime()), getFormatIntervalPattern(formatInterval));
                                if (StringUtil.isNotEmpty(dateFormat)) {
                                    return dateFormat;
                                }
                                return c.getFirstDateTime();
                            }));
                }
                //电表-反向有功电量(谷)
                if (functionDataMap.containsKey(FunctionLogoParamVo.VALLEY_REV_KWH)) {
                    valleyRevKwhMap = functionDataMap.get(FunctionLogoParamVo.VALLEY_REV_KWH).stream().filter(c -> StringUtil.isNotEmpty(c.getFirstDateTime()) && StringUtil.isNotEmpty(c.getFirstDataValue()) && StringUtil.isNotEmpty(c.getLastDataValue()))
                            .collect(Collectors.groupingBy(c -> {
                                String dateFormat = format(strToDate(c.getFirstDateTime()), getFormatIntervalPattern(formatInterval));
                                if (StringUtil.isNotEmpty(dateFormat)) {
                                    return dateFormat;
                                }
                                return c.getFirstDateTime();
                            }));
                }
                //电表-反向有功电量(深谷)
                if (functionDataMap.containsKey(FunctionLogoParamVo.DEEP_REV_KWH)) {
                    deepRevKwhMap = functionDataMap.get(FunctionLogoParamVo.DEEP_REV_KWH).stream().filter(c -> StringUtil.isNotEmpty(c.getFirstDateTime()) && StringUtil.isNotEmpty(c.getFirstDataValue()) && StringUtil.isNotEmpty(c.getLastDataValue()))
                            .collect(Collectors.groupingBy(c -> {
                                String dateFormat = format(strToDate(c.getFirstDateTime()), getFormatIntervalPattern(formatInterval));
                                if (StringUtil.isNotEmpty(dateFormat)) {
                                    return dateFormat;
                                }
                                return c.getFirstDateTime();
                            }));
                }


                for (String dateTime : dateList) {
                    //电表-正向有功电量(尖)
                    if (topSupKwhMap.containsKey(dateTime)) {
                        topSupKwhList.add(getToDouble(topSupKwhMap.get(dateTime).stream().mapToDouble(d -> Double.parseDouble(String.valueOf(d.getLastDataValue())) - Double.parseDouble(String.valueOf(d.getFirstDataValue()))).sum()));
                    } else {
                        topSupKwhList.add(null);
                    }
                    //电表-正向有功电量(峰)
                    if (peakSupKwhMap.containsKey(dateTime)) {
                        peakSupKwhList.add(getToDouble(peakSupKwhMap.get(dateTime).stream().mapToDouble(d -> Double.parseDouble(String.valueOf(d.getLastDataValue())) - Double.parseDouble(String.valueOf(d.getFirstDataValue()))).sum()));
                    } else {
                        peakSupKwhList.add(null);
                    }
                    //电表-正向有功电量(平)
                    if (plainSupKwhMap.containsKey(dateTime)) {
                        plainSupKwhList.add(getToDouble(plainSupKwhMap.get(dateTime).stream().mapToDouble(d -> Double.parseDouble(String.valueOf(d.getLastDataValue())) - Double.parseDouble(String.valueOf(d.getFirstDataValue()))).sum()));
                    } else {
                        plainSupKwhList.add(null);
                    }
                    //电表-正向有功电量(谷)
                    if (valleySupKwhMap.containsKey(dateTime)) {
                        valleySupKwhList.add(getToDouble(valleySupKwhMap.get(dateTime).stream().mapToDouble(d -> Double.parseDouble(String.valueOf(d.getLastDataValue())) - Double.parseDouble(String.valueOf(d.getFirstDataValue()))).sum()));
                    } else {
                        valleySupKwhList.add(null);
                    }
                    //电表-正向有功电量(深谷)
                    if (deepSupKwhMap.containsKey(dateTime)) {
                        deepSupKwhList.add(getToDouble(deepSupKwhMap.get(dateTime).stream().mapToDouble(d -> Double.parseDouble(String.valueOf(d.getLastDataValue())) - Double.parseDouble(String.valueOf(d.getFirstDataValue()))).sum()));
                    } else {
                        deepSupKwhList.add(null);
                    }
                    //电表-反向有功电量(尖)
                    if (topRevKwhMap.containsKey(dateTime)) {
                        topRevKwhList.add(getToDouble(topRevKwhMap.get(dateTime).stream().mapToDouble(d -> Double.parseDouble(String.valueOf(d.getLastDataValue())) - Double.parseDouble(String.valueOf(d.getFirstDataValue()))).sum()));
                    } else {
                        topRevKwhList.add(null);
                    }
                    //电表-反向有功电量(峰)
                    if (peakRevKwhMap.containsKey(dateTime)) {
                        peakRevKwhList.add(getToDouble(peakRevKwhMap.get(dateTime).stream().mapToDouble(d -> Double.parseDouble(String.valueOf(d.getLastDataValue())) - Double.parseDouble(String.valueOf(d.getFirstDataValue()))).sum()));
                    } else {
                        peakRevKwhList.add(null);
                    }
                    //电表-反向有功电量(平)
                    if (plainRevKwhMap.containsKey(dateTime)) {
                        plainRevKwhList.add(getToDouble(plainRevKwhMap.get(dateTime).stream().mapToDouble(d -> Double.parseDouble(String.valueOf(d.getLastDataValue())) - Double.parseDouble(String.valueOf(d.getFirstDataValue()))).sum()));
                    } else {
                        plainRevKwhList.add(null);
                    }
                    //电表-反向有功电量(谷)
                    if (valleyRevKwhMap.containsKey(dateTime)) {
                        valleyRevKwhList.add(getToDouble(valleyRevKwhMap.get(dateTime).stream().mapToDouble(d -> Double.parseDouble(String.valueOf(d.getLastDataValue())) - Double.parseDouble(String.valueOf(d.getFirstDataValue()))).sum()));
                    } else {
                        valleyRevKwhList.add(null);
                    }
                    //电表-反向有功电量(深谷)
                    if (deepRevKwhMap.containsKey(dateTime)) {
                        deepRevKwhList.add(getToDouble(deepRevKwhMap.get(dateTime).stream().mapToDouble(d -> Double.parseDouble(String.valueOf(d.getLastDataValue())) - Double.parseDouble(String.valueOf(d.getFirstDataValue()))).sum()));
                    } else {
                        deepRevKwhList.add(null);
                    }
                }
                topSupKwh.setDataList(topSupKwhList);
                peakSupKwh.setDataList(peakSupKwhList);
                plainSupKwh.setDataList(plainSupKwhList);
                valleySupKwh.setDataList(valleySupKwhList);
                deepSupKwh.setDataList(deepSupKwhList);
                topRevKwh.setDataList(topRevKwhList);
                peakRevKwh.setDataList(peakRevKwhList);
                plainRevKwh.setDataList(plainRevKwhList);
                valleyRevKwh.setDataList(valleyRevKwhList);
                deepRevKwh.setDataList(deepRevKwhList);
            }
        }
        dataInfoList.add(topSupKwh);
        dataInfoList.add(peakSupKwh);
        dataInfoList.add(plainSupKwh);
        dataInfoList.add(valleySupKwh);
        dataInfoList.add(deepSupKwh);
        dataInfoList.add(topRevKwh);
        dataInfoList.add(peakRevKwh);
        dataInfoList.add(plainRevKwh);
        dataInfoList.add(valleyRevKwh);
        dataInfoList.add(deepRevKwh);
        result.setDataInfoList(dataInfoList);
        return result;
    }

}
