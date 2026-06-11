package com.sunmax.together.service.monitor.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.sunmax.common.dto.PageDto;
import com.sunmax.common.dto.WeatherDayDto;
import com.sunmax.common.dto.data.DeviceHistoryDto;
import com.sunmax.common.dto.data.NodeDifHistoryDto;
import com.sunmax.common.dto.device.*;
import com.sunmax.common.dto.system.OrganEmpowerListDto;
import com.sunmax.common.dto.together.PileGunMonitorDataDto;
import com.sunmax.common.dto.together.PileMonitorDataDto;
import com.sunmax.common.model.RealDataModel;
import com.sunmax.common.util.*;
import com.sunmax.common.util.oss.FileUtil;
import com.sunmax.common.vo.FunctionLogoParamVo;
import com.sunmax.common.vo.ReaFieldParamVo;
import com.sunmax.common.vo.SiteFieldParamVo;
import com.sunmax.common.vo.data.DeviceHistoryQueryVo;
import com.sunmax.together.dto.monitor.centralMonitor.*;
import com.sunmax.together.service.WebFeignService;
import com.sunmax.together.service.feign.DataService;
import com.sunmax.together.service.feign.DeviceService;
import com.sunmax.together.service.feign.SystemService;
import com.sunmax.together.service.monitor.CentralMonitorService;
import com.sunmax.together.service.operation.OperationAnalysisService;
import com.sunmax.together.util.ExtraValueUtil;
import com.sunmax.together.vo.monitor.centralMonitor.TopCurveParamVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.compress.utils.Lists;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@Service
public class CentralMonitorServiceImpl implements CentralMonitorService {

    @Autowired
    private SystemService systemService;

    @Autowired
    private DeviceService deviceService;

    @Autowired
    private DataService dataService;

    @Autowired
    private WebFeignService webFeignService;

    @Autowired
    private OperationAnalysisService operationAnalysisService;

    @Override
    public ResponseResult<Map<String, Integer>> statusTotal(String userId) {
        //返回的对象
        Map<String, Integer> resultMap = Maps.newHashMap();
        //定义返回数据
        int a = 0, b = 0, c = 0, d = 0, e = 0, f = 0, g = 0;
        //根据用户id查询多个站点id
        List<String> siteIds = systemService.findAllOrganEmpowerByUserId(userId).getData().stream().map(OrganEmpowerListDto::getSiteId)
                .filter(StringUtil::isNotEmpty).collect(Collectors.toList());
        //根据多个站点id查询站点信息
        Map<String, SiteInfoDto> siteInfoMap = deviceService.findSiteBasicInfoByIds(siteIds).getData();

        if (MapUtils.isNotEmpty(siteInfoMap)) {
            for (Map.Entry<String, SiteInfoDto> entry : siteInfoMap.entrySet()) {
                SiteInfoDto siteInfo = siteInfoMap.get(entry.getKey());
                if (StringUtil.isNotEmpty(siteInfo.getSiteStatus())) {
                    switch (siteInfo.getSiteStatus()) {
                        case 1:
                            a++;
                            break;
                        case 2:
                            b++;
                            break;
                        case 3:
                            c++;
                            break;
                        case 4:
                            d++;
                            break;
                    }
                }
            }
            //根据多个站点id查询设备数据
            siteIds = siteInfoMap.values().stream().map(SiteInfoDto::getId).distinct().collect(Collectors.toList());
            Map<String, List<DeviceBasicInfoDto>> deviceMap = deviceService.findDeviceBasicInfoBySiteIds(siteIds, null).getData();
            for (String key : deviceMap.keySet()) {
                List<DeviceBasicInfoDto> deviceBasicInfoDtoList = deviceMap.get(key);
                for (DeviceBasicInfoDto dto : deviceBasicInfoDtoList) {
                    switch (dto.getTxStatus()) {
                        case 0:
                            g++;
                            break;
                        case 2:
                            e++;
                            break;
                        case 88:
                            f++;
                            break;
                    }
                }
            }
        }
        resultMap.put("正常投运", a);
        resultMap.put("关闭下线", b);
        resultMap.put("维护中", c);
        resultMap.put("建设中", d);
        resultMap.put("设备故障", e);
        resultMap.put("设备离线", f);
        resultMap.put("设备未注册", g);
        return ResponseResult.ok(resultMap);
    }

    @Override
    public ResponseResult<PageDto<SiteStatisticsDto>> statistics(String word, String area, String userId, Integer page, Integer size) {
        //返回的对象
        PageDto<SiteStatisticsDto> resultPage = new PageDto<>();

        //返回的集合
        List<SiteStatisticsDto> resultList = Lists.newArrayList();

        //根据用户id查询多个站点id
        List<String> siteIds = systemService.findAllOrganEmpowerByUserId(userId).getData().stream().map(OrganEmpowerListDto::getSiteId)
                .filter(StringUtil::isNotEmpty).distinct().collect(Collectors.toList());

        //根据多个站点id查询站点信息
        List<SiteInfoDto> siteInfoList = new ArrayList<>(deviceService.findSiteBasicInfoByIds(siteIds).getData().values());

        if (CollectionUtils.isNotEmpty(siteInfoList)) {
            //根据查询条件过滤
            //关键字(站点名称)
            if (StringUtil.isNotEmpty(word)) {
                siteInfoList = siteInfoList.stream().filter(s -> StringUtil.isNotEmpty(s.getSiteName()) && s.getSiteName().contains(word)).collect(Collectors.toList());
            }
            //所属省市
            if (StringUtil.isNotEmpty(area)) {
                siteInfoList = siteInfoList.stream().filter(s -> {
                    if (StringUtil.isNotEmpty(s.getSiteReadwriteObject())) {
                        JSONObject siteReaMap = JSON.parseObject(s.getSiteReadwriteObject());
                        if (siteReaMap.containsKey(SiteFieldParamVo.LOCATION) && StringUtil.isNotEmpty(siteReaMap.get(SiteFieldParamVo.LOCATION))) {
                            JSONObject locationMap = JSON.parseObject(String.valueOf(siteReaMap.get(SiteFieldParamVo.LOCATION)));
                            if (locationMap.containsKey(SiteFieldParamVo.ADDRESS) && StringUtil.isNotEmpty(locationMap.get(SiteFieldParamVo.ADDRESS))) {
                                return String.valueOf(locationMap.get(SiteFieldParamVo.ADDRESS)).contains(area);
                            }
                        }
                    }
                    return false;
                }).collect(Collectors.toList());
            }

            if (CollectionUtils.isEmpty(siteInfoList)) {
                return ResponseResult.ok(new PageDto<>(resultList, page, size));
            }
            PageDto<SiteInfoDto> siteInfoPageDto = new PageDto<>(siteInfoList, page, size);
            BeanUtils.copyProperties(siteInfoPageDto, resultPage);
            siteInfoList = siteInfoPageDto.getItems();

            //根据多个站点id查询设备数据
            siteIds = siteInfoList.stream().map(SiteInfoDto::getId).collect(Collectors.toList());

            //站点系统状态 站点id -> (系统id -> (设备状态 -> 数量))
            Map<String, Map<String, Map<Integer, Long>>> siteSystemStatusMap = Maps.newHashMap();
            //站点逆变器设备 站点id -> 多个逆变器id
            Map<String, List<String>> siteInverterIdMap = Maps.newHashMap();
            //站点PCS设备 站点id -> 多个PCS设备id
            Map<String, List<String>> sitePcsIdMap = Maps.newHashMap();
            //站点充电桩设备 站点id -> 多个充电桩id
            Map<String, List<String>> sitePileIdMap = Maps.newHashMap();
            //站点换电仓设备 站点id -> 多个换电仓id
            Map<String, List<String>> siteGranaryIdMap = Maps.newHashMap();
            //定义光伏系统设备类型,储能设备类型,充电桩设备类型
            List<String> pvSystemTypes = Arrays.asList("20", "65", "66", "67", "77");
            List<String> seSystemTypes = Arrays.asList("23", "25", "60", "78");
            List<String> pileSystemTypes = Arrays.asList("28", "29", "30", "79");
            Map<String, List<DeviceBasicInfoDto>> data = deviceService.findDeviceBasicInfoBySiteIds(siteIds, null).getData();
            data.forEach((siteId, deviceInfo) -> {
                Map<String, Map<Integer, Long>> systemStatusMap = Maps.newHashMap();
                //站点光伏系统状态
                systemStatusMap.put("1", deviceInfo.stream().filter(d -> pvSystemTypes.contains(d.getTypeId()))
                        .collect(Collectors.groupingBy(DeviceBasicInfoDto::getTxStatus, Collectors.counting())));
                //站点储能系统状态
                systemStatusMap.put("2", deviceInfo.stream().filter(d -> seSystemTypes.contains(d.getTypeId()))
                        .collect(Collectors.groupingBy(DeviceBasicInfoDto::getTxStatus, Collectors.counting())));
                //站点充电桩系统状态
                systemStatusMap.put("3", deviceInfo.stream().filter(d -> pileSystemTypes.contains(d.getTypeId()))
                        .collect(Collectors.groupingBy(DeviceBasicInfoDto::getTxStatus, Collectors.counting())));
                //站点充电桩系统状态
                systemStatusMap.put("6", deviceInfo.stream().filter(d -> Objects.equals(d.getTypeId(), "70"))
                        .collect(Collectors.groupingBy(DeviceBasicInfoDto::getTxStatus, Collectors.counting())));
                siteSystemStatusMap.put(siteId, systemStatusMap);

                //站点逆变器设备
                siteInverterIdMap.put(siteId, deviceInfo.stream().filter(d -> d.getTypeId().equals("20") || d.getTypeId().equals("77")).map(DeviceBasicInfoDto::getId).collect(Collectors.toList()));

                //站点PCS设备
                sitePcsIdMap.put(siteId, deviceInfo.stream().filter(d -> d.getTypeId().equals("23") || d.getTypeId().equals("78")).map(DeviceBasicInfoDto::getId).collect(Collectors.toList()));

                //站点充电桩设备
                sitePileIdMap.put(siteId, deviceInfo.stream().filter(d -> pileSystemTypes.contains(d.getTypeId())).map(DeviceBasicInfoDto::getId).collect(Collectors.toList()));

                //站点换电仓设备
                siteGranaryIdMap.put(siteId, deviceInfo.stream().filter(d -> d.getTypeId().equals("70")).map(DeviceBasicInfoDto::getId).collect(Collectors.toList()));

            });

            //定义查询历史数据的开始时间和结束时间
            String startTime = DateUtil.getDayStart(DateUtil.localDateToStr(LocalDate.now()));
            String endTime = DateUtil.localDateTimeToStr(LocalDateTime.now());

            //查询站点下面的逆变器设备功率历史数据
            Map<String, Map<String, List<DeviceHistoryDto>>> deviceInverterDataMap = Maps.newHashMap();
            if (MapUtils.isNotEmpty(siteInverterIdMap)) {
                DeviceHistoryQueryVo deviceInverterQueryVo = new DeviceHistoryQueryVo();
                deviceInverterQueryVo.setDeviceIds(siteInverterIdMap.values().stream().flatMap(List::stream).collect(Collectors.toSet()));
                deviceInverterQueryVo.setFunctionLogos(Collections.singleton(FunctionLogoParamVo.ACTIVE_POWER));
                deviceInverterQueryVo.setStartTime(startTime);
                deviceInverterQueryVo.setEndTime(endTime);
                deviceInverterQueryVo.setTimeInterval("1m");
                deviceInverterDataMap = dataService.findDeviceHistoryValueList(deviceInverterQueryVo).getData();
            }

            //查询站点下面的PCS设备功率历史数据
            Map<String, Map<String, List<DeviceHistoryDto>>> devicePcsDataMap = Maps.newHashMap();
            if (MapUtils.isNotEmpty(sitePcsIdMap)) {
                DeviceHistoryQueryVo devicePcsQueryVo = new DeviceHistoryQueryVo();
                devicePcsQueryVo.setDeviceIds(sitePcsIdMap.values().stream().flatMap(List::stream).collect(Collectors.toSet()));
                devicePcsQueryVo.setFunctionLogos(Collections.singleton(FunctionLogoParamVo.PCS_ACTIVE_POWER));
                devicePcsQueryVo.setStartTime(startTime);
                devicePcsQueryVo.setEndTime(endTime);
                devicePcsQueryVo.setTimeInterval("1m");
                devicePcsDataMap = dataService.findDeviceHistoryValueList(devicePcsQueryVo).getData();
            }

            //查询站点下面的充电桩设备历史数据
            Map<String, Map<String, List<DeviceHistoryDto>>> devicePileDataMap = Maps.newHashMap();
            if (MapUtils.isNotEmpty(sitePileIdMap)) {
                DeviceHistoryQueryVo devicePileQueryVo = new DeviceHistoryQueryVo();
                devicePileQueryVo.setDeviceIds(sitePileIdMap.values().stream().flatMap(List::stream).collect(Collectors.toSet()));
                List<String> functionLogos = Arrays.asList(FunctionLogoParamVo.PILE_CHARGEPOWER, FunctionLogoParamVo.PILE_DISCHARGEPOWER);
                devicePileQueryVo.setFunctionLogos(new HashSet<>(functionLogos));
                devicePileQueryVo.setStartTime(startTime);
                devicePileQueryVo.setEndTime(endTime);
                devicePileQueryVo.setTimeInterval("1m");
                devicePileDataMap = dataService.findDeviceHistoryValueList(devicePileQueryVo).getData();
            }

            //查询站点下面的换电仓设备历史数据
            Map<String, Map<String, List<DeviceHistoryDto>>> deviceGranaryDataMap = Maps.newHashMap();
            if (MapUtils.isNotEmpty(siteGranaryIdMap)) {
                DeviceHistoryQueryVo deviceGranaryQueryVo = new DeviceHistoryQueryVo();
                deviceGranaryQueryVo.setDeviceIds(siteGranaryIdMap.values().stream().flatMap(List::stream).collect(Collectors.toSet()));
                deviceGranaryQueryVo.setFunctionLogos(Collections.singleton(FunctionLogoParamVo.BATTERY_GRANARY_OUT_POWER));
                deviceGranaryQueryVo.setStartTime(startTime);
                deviceGranaryQueryVo.setEndTime(endTime);
                deviceGranaryQueryVo.setTimeInterval("1m");
                deviceGranaryDataMap = dataService.findDeviceHistoryValueList(deviceGranaryQueryVo).getData();
            }

            Map<String, Map<String, List<DeviceHistoryDto>>> finalDeviceInverterDataMap = deviceInverterDataMap;
            Map<String, Map<String, List<DeviceHistoryDto>>> finalDevicePcsDataMap = devicePcsDataMap;
            Map<String, Map<String, List<DeviceHistoryDto>>> finalDevicePileDataMap = devicePileDataMap;
            Map<String, Map<String, List<DeviceHistoryDto>>> finalDeviceGranaryDataMap = deviceGranaryDataMap;
            resultList = siteInfoList.stream().map(siteInfo -> {
                SiteStatisticsDto result = new SiteStatisticsDto();
                result.setSiteId(siteInfo.getId());
                result.setSiteName(siteInfo.getSiteName());
                result.setSiteStatus(siteInfo.getSiteStatus());
                result.setScenarioTypes(siteInfo.getScenarioTypes());

                List<SystemStatisticsDto> deviceList = Lists.newArrayList();
                Map<String, Map<Integer, Long>> systemStatusMap;
                if (siteSystemStatusMap.containsKey(siteInfo.getId())) {
                    systemStatusMap = siteSystemStatusMap.get(siteInfo.getId());
                } else {
                    systemStatusMap = Maps.newHashMap();
                }
                //能源场景类型 1-光伏 2-储能 3-充电桩 4-用能 5-配电 6-换电
                if (StringUtil.isNotEmpty(siteInfo.getScenarioTypes())) {
                    for (String scenarioType : new HashSet<>(Arrays.asList(siteInfo.getScenarioTypes().split(FileUtil.COMMA)))) {
                        SystemStatisticsDto system = new SystemStatisticsDto();
                        //获取系统设备数量
                        if (systemStatusMap.containsKey(scenarioType)) {
                            //通信状态 0-未注册 1-在线 2-故障 88-离线
                            Map<Integer, Long> statusMap = systemStatusMap.get(scenarioType);
                            system.setError(statusMap.getOrDefault(2, 0L).intValue());
                            system.setUnregistered(statusMap.getOrDefault(0, 0L).intValue());
                            system.setOffline(statusMap.getOrDefault(88, 0L).intValue());
                            system.setNormal(statusMap.getOrDefault(1, 0L).intValue());
                        }
                        //获取系统曲线数据
                        List<CurveDto> curveList = Lists.newArrayList();
                        String scenarioName = null;
                        //能源场景类型 1-光伏 2-储能 3-充电桩 4-用能 5-配电 6-换电
                        switch (scenarioType) {
                            case "1":
                                scenarioName = "光伏系统";
                                if (siteInverterIdMap.containsKey(siteInfo.getId())) {
                                    List<String> siteInverterIds = siteInverterIdMap.get(siteInfo.getId());
                                    Map<String, Double> systemPvPowerMap = finalDeviceInverterDataMap.entrySet().stream()
                                            .filter(d -> siteInverterIds.contains(d.getKey()))
                                            .flatMap(d -> d.getValue().entrySet().stream().flatMap(s -> s.getValue().stream()))
                                            .collect(Collectors.groupingBy(DeviceHistoryDto::getDateTime, Collectors.summingDouble(i ->
                                                    StringUtil.isNotEmpty(i.getDataValue()) ? Double.parseDouble(String.valueOf(i.getDataValue())) : 0.0)));
                                    //光伏系统曲线
                                    CurveDto powerCurve = new CurveDto();
                                    powerCurve.setName("光伏功率");
                                    ExtraValueUtil.getPowerCurveList(curveList, systemPvPowerMap, powerCurve);
                                }
                                break;
                            case "2":
                                scenarioName = "储能系统";
                                //光伏系统曲线
                                if (sitePcsIdMap.containsKey(siteInfo.getId())) {
                                    List<String> sitePcsIds = sitePcsIdMap.get(siteInfo.getId());
                                    Map<String, Double> systemSePowerMap = finalDevicePcsDataMap.entrySet().stream()
                                            .filter(d -> sitePcsIds.contains(d.getKey()))
                                            .flatMap(d -> d.getValue().entrySet().stream().flatMap(s -> s.getValue().stream()))
                                            .collect(Collectors.groupingBy(DeviceHistoryDto::getDateTime, Collectors.summingDouble(i ->
                                                    StringUtil.isNotEmpty(i.getDataValue()) ? Double.parseDouble(String.valueOf(i.getDataValue())) : 0.0)));
                                    CurveDto powerCurve = new CurveDto();
                                    powerCurve.setName("储能功率");
                                    ExtraValueUtil.getPowerCurveList(curveList, systemSePowerMap, powerCurve);
                                }
                                break;
                            case "3":
                                scenarioName = "充电系统";
                                //充电桩系统曲线
                                if (sitePileIdMap.containsKey(siteInfo.getId())) {
                                    List<String> sitePileIds = sitePileIdMap.get(siteInfo.getId());
                                    List<DeviceHistoryDto> deviceHistoryList = finalDevicePileDataMap.entrySet().stream()
                                            .filter(d -> sitePileIds.contains(d.getKey()))
                                            .flatMap(d -> d.getValue().entrySet().stream().flatMap(s -> s.getValue().stream()))
                                            .collect(Collectors.toList());
                                    //功能点标识 -> 设备历史数据
                                    Map<String, List<DeviceHistoryDto>> deviceFunctionMap = deviceHistoryList.stream().collect(Collectors.groupingBy(DeviceHistoryDto::getFunctionLogo));
                                    if (deviceFunctionMap.containsKey(FunctionLogoParamVo.PILE_CHARGEPOWER)) {
                                        Map<String, Double> chargePowerMap = deviceFunctionMap.get(FunctionLogoParamVo.PILE_CHARGEPOWER)
                                                .stream().collect(Collectors.groupingBy(DeviceHistoryDto::getDateTime, Collectors.summingDouble(i ->
                                                        StringUtil.isNotEmpty(i.getDataValue()) ? Double.parseDouble(String.valueOf(i.getDataValue())) : 0.0)));
                                        CurveDto powerCurve = new CurveDto();
                                        powerCurve.setName("充电功率");
                                        ExtraValueUtil.getPowerCurveList(curveList, chargePowerMap, powerCurve);
                                    }
                                    if (deviceFunctionMap.containsKey(FunctionLogoParamVo.PILE_DISCHARGEPOWER)) {
                                        Map<String, Double> chargePowerMap = deviceFunctionMap.get(FunctionLogoParamVo.PILE_DISCHARGEPOWER)
                                                .stream().collect(Collectors.groupingBy(DeviceHistoryDto::getDateTime, Collectors.summingDouble(i ->
                                                        StringUtil.isNotEmpty(i.getDataValue()) ? Double.parseDouble(String.valueOf(i.getDataValue())) : 0.0)));
                                        CurveDto powerCurve = new CurveDto();
                                        powerCurve.setName("放电功率");
                                        ExtraValueUtil.getPowerCurveList(curveList, chargePowerMap, powerCurve);
                                    }
                                }
                                break;
                            case "6":
                                scenarioName = "换电系统";
                                if (siteGranaryIdMap.containsKey(siteInfo.getId())) {
                                    List<String> siteGranaryIds = siteGranaryIdMap.get(siteInfo.getId());
                                    Map<String, Double> systemChangePowerMap = finalDeviceGranaryDataMap.entrySet().stream()
                                            .filter(d -> siteGranaryIds.contains(d.getKey()))
                                            .flatMap(d -> d.getValue().entrySet().stream().flatMap(s -> s.getValue().stream()))
                                            .collect(Collectors.groupingBy(DeviceHistoryDto::getDateTime, Collectors.summingDouble(i ->
                                                    StringUtil.isNotEmpty(i.getDataValue()) ? Double.parseDouble(String.valueOf(i.getDataValue())) : 0.0)));
                                    //换电系统曲线
                                    CurveDto powerCurve = new CurveDto();
                                    powerCurve.setName("换电功率");
                                    ExtraValueUtil.getPowerCurveList(curveList, systemChangePowerMap, powerCurve);
                                }
                                break;
                            default:
                                break;
                        }
                        system.setFunctionLogos(scenarioName);
                        system.setHistorys(curveList);
                        deviceList.add(system);
                    }
                    result.setDevices(deviceList);
                }
                return result;
            }).collect(Collectors.toList());
            resultPage.setItems(resultList);
        }
        return ResponseResult.ok(resultPage);
    }

    @Override
    public ResponseResult<PageDto<PhotovoltaicDto>> photovoltaicPage(String word, String area, String userId, Integer page, Integer size) {
        //返回的对象
        PageDto<PhotovoltaicDto> resultPage = new PageDto<>();

        //返回的集合
        List<PhotovoltaicDto> resultList = Lists.newArrayList();

        //根据用户id查询多个站点id
        List<String> siteIds = systemService.findAllOrganEmpowerByUserId(userId).getData().stream().map(OrganEmpowerListDto::getSiteId)
                .filter(StringUtil::isNotEmpty).distinct().collect(Collectors.toList());

        //根据多个站点id查询站点光伏信息
        List<SiteInfoDto> siteInfoList = deviceService.findSiteBasicInfoByIds(siteIds).getData().values().stream()
                .filter(s -> s.getScenarioTypes().contains("1")).collect(Collectors.toList());

        if (CollectionUtils.isNotEmpty(siteInfoList)) {
            for (SiteInfoDto siteInfo : siteInfoList) {
                //获取站点位置
                String siteLocation;
                JSONObject siteReaMap = JSONObject.parseObject(siteInfo.getSiteReadwriteObject());
                JSONObject location = siteReaMap.getJSONObject(SiteFieldParamVo.LOCATION);
                if (location != null && location.containsKey(SiteFieldParamVo.PROVINCE) && location.containsKey(SiteFieldParamVo.CITY)) {
                    siteLocation = location.getString(SiteFieldParamVo.PROVINCE) + location.getString(SiteFieldParamVo.CITY);
                } else {
                    siteLocation = null;
                }
                //获取场景系统数据
                List<SiteScenarioTypeDto> scenarioTypeList = siteInfo.getSiteScenarioTypeDtos().stream().filter(s -> Objects.equals(s.getScenarioType(), 1)).collect(Collectors.toList());
                if (CollectionUtils.isNotEmpty(scenarioTypeList)) {
                    resultList.addAll(scenarioTypeList.stream().map(scenarioType -> {
                        PhotovoltaicDto result = new PhotovoltaicDto();
                        result.setSystemId(scenarioType.getId());
                        result.setSystemName(scenarioType.getSystemName());
                        result.setSiteId(siteInfo.getId());
                        result.setSiteName(siteInfo.getSiteName());
                        result.setScenarioTypes(siteInfo.getScenarioTypes());
                        result.setLocation(siteLocation);
                        //光伏装机容量
                        JSONObject reaMap = JSONObject.parseObject(scenarioType.getReadwriteObject());
                        if (reaMap != null && reaMap.containsKey(SiteFieldParamVo.PV_CAPACITY)
                                && StringUtil.isNotEmpty(reaMap.getString(SiteFieldParamVo.PV_CAPACITY))) {
                            result.setPvcapacity(Double.valueOf(reaMap.getString(SiteFieldParamVo.PV_CAPACITY)));
                        }
                        for (DeviceReaDto reaDto : scenarioType.getReaList()) {
                            if (reaDto.getFieldName().equals(SiteFieldParamVo.TIED_GRADE)) {
                                result.setTiedGrade(ExtraValueUtil.getValue(reaDto));
                            }
                            if (reaDto.getFieldName().equals(SiteFieldParamVo.PV_SYS_TYPE)) {
                                result.setPvType(ExtraValueUtil.getValue(reaDto));
                            }
                            if (reaDto.getFieldName().equals(SiteFieldParamVo.CONSUM_MODE)) {
                                result.setConsumMode(ExtraValueUtil.getValue(reaDto));
                            }
                        }
                        return result;
                    }).collect(Collectors.toList()));
                }
            }

            //关键字(站点名称和系统名称)
            if (StringUtil.isNotEmpty(word)) {
                resultList = resultList.stream().filter(s -> (StringUtil.isNotEmpty(s.getSiteName()) && s.getSiteName().contains(word))
                        || (StringUtil.isNotEmpty(s.getSystemName()) && s.getSystemName().contains(word))).collect(Collectors.toList());
            }
            //所属省市
            if (StringUtil.isNotEmpty(area)) {
                resultList = resultList.stream().filter(s -> StringUtil.isNotEmpty(s.getLocation()) && s.getLocation().contains(area))
                        .collect(Collectors.toList());
            }

            resultPage = new PageDto<>(resultList, page, size);
            if (CollectionUtils.isEmpty(resultList)) {
                return ResponseResult.ok(resultPage);
            }
            resultList = resultPage.getItems();

            //光伏系统状态 系统id -> (设备状态 -> 数量)
            Map<String, Map<Integer, Long>> systemStatusMap = Maps.newHashMap();
            //光伏系统逆变器设备
            Map<String, List<String>> systemInverterIdMap = Maps.newHashMap();

            //根据多个系统id查询设备数据
            List<String> systemIds = resultList.stream().map(PhotovoltaicDto::getSystemId).collect(Collectors.toList());
            deviceService.findDeviceInfoByParentIds(systemIds).getData().forEach((systemId, deviceList) -> {
                systemStatusMap.put(systemId, deviceList.stream().collect(Collectors.groupingBy(DeviceBasicInfoDto::getTxStatus, Collectors.counting())));
                systemInverterIdMap.put(systemId, deviceList.stream().filter(d -> d.getTypeId().equals("20") || d.getTypeId().equals("77"))
                        .map(DeviceBasicInfoDto::getId).collect(Collectors.toList()));

            });

            //查询光伏功率曲线(逆变器有功功率曲线)
            //逆变器设备id -> (功能点标识 -> 设备历史数据实体)
            Map<String, Map<String, List<DeviceHistoryDto>>> inverterPowerDataMap = Maps.newHashMap();
            Map<String, Map<String, List<NodeDifHistoryDto>>> inverterDayQtDataMap = Maps.newHashMap();
            //逆变器设备id -> (功能点标识 -> 设备实时数据实体)
            Map<String, Map<String, RealDataModel>> inverterRealDataMap = Maps.newHashMap();
            if (MapUtils.isNotEmpty(systemInverterIdMap)) {
                Set<String> inverterIds = systemInverterIdMap.values().stream().flatMap(List::stream).collect(Collectors.toSet());
                //查询站点下面的逆变器设备有功功率曲线
                DeviceHistoryQueryVo powerQueryVo = new DeviceHistoryQueryVo();
                powerQueryVo.setDeviceIds(inverterIds);
                powerQueryVo.setFunctionLogos(Collections.singleton(FunctionLogoParamVo.ACTIVE_POWER));
                powerQueryVo.setStartTime(DateUtil.getDayStart(DateUtil.localDateToStr(LocalDate.now())));
                powerQueryVo.setEndTime(DateUtil.localDateTimeToStr(LocalDateTime.now()));
                powerQueryVo.setTimeInterval("1m");
                inverterPowerDataMap = dataService.findDeviceHistoryValueList(powerQueryVo).getData();

                //查询逆变器今日发电量
                DeviceHistoryQueryVo dayQtQueryVo = new DeviceHistoryQueryVo();
                dayQtQueryVo.setDeviceIds(inverterIds);
                dayQtQueryVo.setFunctionLogos(Collections.singleton(FunctionLogoParamVo.TOTAL_POWER_GENERATION));
                dayQtQueryVo.setStartTime(DateUtil.getDayStart(DateUtil.localDateToStr(LocalDate.now())));
                dayQtQueryVo.setEndTime(DateUtil.localDateTimeToStr(LocalDateTime.now()));
                dayQtQueryVo.setTimeInterval("1d");
                inverterDayQtDataMap = dataService.findNodeDifHistoryListFeign(dayQtQueryVo).getData();

                //查询站点下面的逆变器设备实时有功功率
                inverterRealDataMap = deviceService.getDeviceFunctionsRealDataByIds(inverterIds, FunctionLogoParamVo.ACTIVE_POWER, 2).getData();
            }
            //对数据进行组装处理
            Map<String, Map<String, List<DeviceHistoryDto>>> finalInverterPowerDataMap = inverterPowerDataMap;
            Map<String, Map<String, RealDataModel>> finalInverterRealDataMap = inverterRealDataMap;
            Map<String, Map<String, List<NodeDifHistoryDto>>> finalInverterDayQtDataMap = inverterDayQtDataMap;
            resultList = resultList.stream().peek(result -> {
                //定义光伏系统设备实体类
                SystemStatisticsDto system = new SystemStatisticsDto();
                system.setFunctionLogos("光伏系统");
                //获取光伏系统设备故障和离线数量
                if (systemStatusMap.containsKey(result.getSystemId())) {
                    //通信状态 0-未注册 1-在线 2-故障 88-离线
                    Map<Integer, Long> statusMap = systemStatusMap.get(result.getSystemId());
                    system.setError(statusMap.getOrDefault(2, 0L).intValue());
                    system.setUnregistered(statusMap.getOrDefault(0, 0L).intValue());
                    system.setOffline(statusMap.getOrDefault(88, 0L).intValue());
                    system.setNormal(statusMap.getOrDefault(1, 0L).intValue());
                }
                List<CurveDto> curveList = Lists.newArrayList();
                if (systemInverterIdMap.containsKey(result.getSystemId())) {
                    List<String> systemInverterIds = systemInverterIdMap.get(result.getSystemId());
                    //获取光伏系统功率曲线
                    Map<String, Double> systemPvPowerMap = finalInverterPowerDataMap.entrySet().stream()
                            .filter(d -> systemInverterIds.contains(d.getKey()))
                            .flatMap(d -> d.getValue().entrySet().stream().flatMap(s -> s.getValue().stream()))
                            .collect(Collectors.groupingBy(DeviceHistoryDto::getDateTime, Collectors.summingDouble(i ->
                                    StringUtil.isNotEmpty(i.getDataValue()) ? Double.parseDouble(String.valueOf(i.getDataValue())) : 0.0)));
                    //光伏系统曲线
                    CurveDto powerCurve = new CurveDto();
                    powerCurve.setName("光伏功率");
                    ExtraValueUtil.getPowerCurveList(curveList, systemPvPowerMap, powerCurve);

                    //光伏系统实时功率
                    result.setActivePower(DoubleUtil.getToDouble(finalInverterRealDataMap.entrySet().stream()
                            .filter(d -> systemInverterIds.contains(d.getKey()))
                            .flatMap(d -> d.getValue().values().stream())
                            .filter(Objects::nonNull)
                            .mapToDouble(d -> DoubleUtil.objToDouble(d.getDataValue())).sum()));
                    //计算光伏系统实时功率归一化 逆变器有功功率(实时值)/光伏装机容量*100%
                    result.setPowerNormalize(DoubleUtil.getToDouble(result.getActivePower() / result.getPvcapacity() * 100, 2));
                    //光伏系统今日发电量
                    result.setDailyPowerGeneration(DoubleUtil.getToDouble(finalInverterDayQtDataMap.entrySet().stream()
                            .filter(d -> systemInverterIds.contains(d.getKey()))
                            .flatMap(d -> d.getValue().entrySet().stream().flatMap(s -> s.getValue().stream()))
                            .filter(Objects::nonNull)
                            .mapToDouble(d -> DoubleUtil.getObjSub(d.getFirstDataValue(), d.getLastDataValue())).sum()));
                    //计算光伏系统今日发电时长 光伏今日发电量/装机容量
                    result.setDailyPowerDuration(DoubleUtil.getToDouble(result.getDailyPowerGeneration() / result.getPvcapacity(), 2));

                }
                system.setHistorys(curveList);
                result.setDevices(Collections.singletonList(system));

            }).collect(Collectors.toList());
            resultPage.setItems(resultList);
        }
        return ResponseResult.ok(resultPage);
    }

    @Override
    public ResponseResult<PageDto<EnergyStorageDto>> energyStoragePage(String word, String area, String userId, Integer page, Integer size) {
        //返回的对象
        PageDto<EnergyStorageDto> resultPage = new PageDto<>();

        //返回的集合
        List<EnergyStorageDto> resultList = Lists.newArrayList();

        //根据用户id查询多个站点id
        List<String> siteIds = systemService.findAllOrganEmpowerByUserId(userId).getData().stream().map(OrganEmpowerListDto::getSiteId)
                .filter(StringUtil::isNotEmpty).distinct().collect(Collectors.toList());

        //根据多个站点id查询站点储能信息
        List<SiteInfoDto> siteInfoList = deviceService.findSiteBasicInfoByIds(siteIds).getData().values().stream()
                .filter(s -> s.getScenarioTypes().contains("2")).collect(Collectors.toList());

        if (CollectionUtils.isNotEmpty(siteInfoList)) {
            for (SiteInfoDto siteInfo : siteInfoList) {
                //获取站点位置
                String siteLocation;
                JSONObject siteReaMap = JSONObject.parseObject(siteInfo.getSiteReadwriteObject());
                JSONObject location = siteReaMap.getJSONObject(SiteFieldParamVo.LOCATION);
                if (location != null && location.containsKey(SiteFieldParamVo.PROVINCE) && location.containsKey(SiteFieldParamVo.CITY)) {
                    siteLocation = location.getString(SiteFieldParamVo.PROVINCE) + location.getString(SiteFieldParamVo.CITY);
                } else {
                    siteLocation = null;
                }
                //获取场景系统数据
                if (StringUtil.isNotEmpty(siteInfo.getScenarioTypes()) && CollectionUtils.isNotEmpty(siteInfo.getSiteScenarioTypeDtos())) {
                    List<SiteScenarioTypeDto> scenarioTypeList = siteInfo.getSiteScenarioTypeDtos().stream().filter(s -> Objects.equals(s.getScenarioType(), 2)).collect(Collectors.toList());
                    if (CollectionUtils.isNotEmpty(scenarioTypeList)) {
                        resultList.addAll(scenarioTypeList.stream().map(scenarioType -> {
                            EnergyStorageDto result = new EnergyStorageDto();
                            result.setSystemId(scenarioType.getId());
                            result.setSystemName(scenarioType.getSystemName());
                            result.setSiteId(siteInfo.getId());
                            result.setSiteName(siteInfo.getSiteName());
                            result.setScenarioTypes(siteInfo.getScenarioTypes());
                            result.setLocation(siteLocation);
                            for (DeviceReaDto reaDto : scenarioType.getReaList()) {
                                if (reaDto.getFieldName().equals(SiteFieldParamVo.TIED_GRADE)) {
                                    result.setTiedGrade(ExtraValueUtil.getValue(reaDto));
                                }
                                if (reaDto.getFieldName().equals(SiteFieldParamVo.STORAGE_TYPE)) {
                                    result.setStorageType(ExtraValueUtil.getValue(reaDto));
                                }
                            }
                            return result;
                        }).collect(Collectors.toList()));
                    }
                }
            }

            //关键字(站点名称和系统名称)
            if (StringUtil.isNotEmpty(word)) {
                resultList = resultList.stream().filter(s -> (StringUtil.isNotEmpty(s.getSiteName()) && s.getSiteName().contains(word))
                        || (StringUtil.isNotEmpty(s.getSystemName()) && s.getSystemName().contains(word))).collect(Collectors.toList());
            }
            //所属省市
            if (StringUtil.isNotEmpty(area)) {
                resultList = resultList.stream().filter(s -> StringUtil.isNotEmpty(s.getLocation()) && s.getLocation().contains(area))
                        .collect(Collectors.toList());
            }

            resultPage = new PageDto<>(resultList, page, size);
            if (CollectionUtils.isEmpty(resultList)) {
                return ResponseResult.ok(resultPage);
            }
            resultList = resultPage.getItems();

            //储能系统状态 系统id -> (设备状态 -> 数量)
            Map<String, Map<Integer, Long>> systemStatusMap = Maps.newHashMap();
            //储能系统PCS设备 系统id -> 多个PCS设备id
            Map<String, List<String>> systemPcsIdMap = Maps.newHashMap();
            //储能系统电池簇设备 系统id -> 多个电池簇设备id
            Map<String, List<String>> systemBatteryIdMap = Maps.newHashMap();
            //储能系统PCS额定功率 系统id -> PCS额定功率
            Map<String, Double> systemPcsRatedPowerMap = Maps.newHashMap();
            //储能系统电池簇额定容量 系统id -> 电池簇额定容量
            Map<String, Double> systemBatteryRatedCapMap = Maps.newHashMap();
            //储能系统电池簇设备额定容量 设备id -> 电池簇额定容量
            Map<String, Double> batteryRatedCapMap = Maps.newHashMap();
            //根据多个系统id查询设备数据
            List<String> systemIds = resultList.stream().map(EnergyStorageDto::getSystemId).collect(Collectors.toList());
            deviceService.findDeviceInfoByParentIds(systemIds).getData().forEach((systemId, deviceList) -> {
                //储能系统状态
                systemStatusMap.put(systemId, deviceList.stream().collect(Collectors.groupingBy(DeviceBasicInfoDto::getTxStatus, Collectors.counting())));
                List<DeviceBasicInfoDto> systemPcsList = deviceList.stream().filter(d -> d.getTypeId().equals("23") || d.getTypeId().equals("78")).collect(Collectors.toList());
                //储能系统PCS设备id
                systemPcsIdMap.put(systemId, systemPcsList.stream().map(DeviceBasicInfoDto::getId).collect(Collectors.toList()));
                //储能系统PCS额定功率
                systemPcsRatedPowerMap.put(systemId, systemPcsList.stream().mapToDouble(pcs -> {
                    if (pcs.getReaMap().containsKey(ReaFieldParamVo.RATED_POWER) && StringUtil.isNotEmpty(pcs.getReaMap().get(ReaFieldParamVo.RATED_POWER))) {
                        return Double.parseDouble(String.valueOf(pcs.getReaMap().get(ReaFieldParamVo.RATED_POWER)));
                    }
                    return 0.0;
                }).sum());
                List<DeviceBasicInfoDto> systemBatteryList = deviceList.stream().filter(d -> d.getTypeId().equals("25")).collect(Collectors.toList());
                //储能系统电池簇设备id
                systemBatteryIdMap.put(systemId, systemBatteryList.stream().map(DeviceBasicInfoDto::getId).collect(Collectors.toList()));
                Map<String, Double> ratedCap = systemBatteryList.stream().collect(Collectors.toMap(DeviceBasicInfoDto::getId, battery -> {
                    if (battery.getReaMap().containsKey(ReaFieldParamVo.RATED_CAP) && StringUtil.isNotEmpty(battery.getReaMap().get(ReaFieldParamVo.RATED_CAP))) {
                        return Double.parseDouble(String.valueOf(battery.getReaMap().get(ReaFieldParamVo.RATED_CAP)));
                    }
                    return 0.0;
                }, (k1, k2) -> k1));
                //储能系统电池簇额定容量
                systemBatteryRatedCapMap.put(systemId, ratedCap.values().stream().mapToDouble(i -> i).sum());
                //储能系统设备额定容量
                batteryRatedCapMap.putAll(ratedCap);
            });

            //查询储能功率曲线(PCS交流侧有功功率曲线)
            //PCS设备id -> (功能点标识 -> 设备历史数据实体)
            Map<String, Map<String, List<DeviceHistoryDto>>> pcsPowerHistoryDataMap = Maps.newHashMap();
            //PCS设备id -> (功能点标识 -> 设备历史数据实体)
            Map<String, Map<String, List<NodeDifHistoryDto>>> pcsQtHistoryDataMap = Maps.newHashMap();
            Set<String> pcsIds = new HashSet<>();
            if (MapUtils.isNotEmpty(systemPcsIdMap)) {
                //定义查询历史数据的开始时间和结束时间
                String startTime = DateUtil.getDayStart(DateUtil.localDateToStr(LocalDate.now()));
                String endTime = DateUtil.localDateTimeToStr(LocalDateTime.now());

                pcsIds = systemPcsIdMap.values().stream().flatMap(List::stream).collect(Collectors.toSet());
                //查询PCS设备交流侧有功功率
                DeviceHistoryQueryVo devicePcsPowerQueryVo = new DeviceHistoryQueryVo();
                devicePcsPowerQueryVo.setDeviceIds(pcsIds);
                devicePcsPowerQueryVo.setFunctionLogos(Collections.singleton(FunctionLogoParamVo.PCS_ACTIVE_POWER));
                devicePcsPowerQueryVo.setStartTime(startTime);
                devicePcsPowerQueryVo.setEndTime(endTime);
                devicePcsPowerQueryVo.setTimeInterval("1m");
                pcsPowerHistoryDataMap = dataService.findDeviceHistoryValueList(devicePcsPowerQueryVo).getData();

                //查询PCS设备今日充电量和今日放电量
                DeviceHistoryQueryVo devicePcsQtQueryVo = new DeviceHistoryQueryVo();
                Set<String> functionLogos = new HashSet<>(Arrays.asList(FunctionLogoParamVo.PCS_BATTERY_CHARGE, FunctionLogoParamVo.PCS_BATTERY_DISCHARGE));
                devicePcsQtQueryVo.setDeviceIds(pcsIds);
                devicePcsQtQueryVo.setFunctionLogos(functionLogos);
                devicePcsQtQueryVo.setStartTime(startTime);
                devicePcsQtQueryVo.setEndTime(endTime);
                devicePcsQtQueryVo.setTimeInterval("1d");
                pcsQtHistoryDataMap = dataService.findNodeDifHistoryListFeign(devicePcsQtQueryVo).getData();
            }
            //查询站点下面的PCS设备有功功率和电池簇设备SOC
            //设备id -> (功能点标识 -> 设备实时数据实体)
            Map<String, Map<String, RealDataModel>> realDataMap = Maps.newHashMap();
            Set<String> batteryIds = systemBatteryIdMap.values().stream().flatMap(List::stream).collect(Collectors.toSet());
            if (CollectionUtils.isNotEmpty(pcsIds) || CollectionUtils.isNotEmpty(batteryIds)) {
                Set<String> deviceIds = new HashSet<>();
                Set<String> functionLogos = new HashSet<>();
                if (CollectionUtils.isNotEmpty(pcsIds)) {
                    deviceIds.addAll(pcsIds);
                    functionLogos.add(FunctionLogoParamVo.PCS_ACTIVE_POWER);
                }
                if (CollectionUtils.isNotEmpty(batteryIds)) {
                    deviceIds.addAll(batteryIds);
                    functionLogos.add(FunctionLogoParamVo.BATTERY_TOTAL_SOC);
                }
                realDataMap = deviceService.getDeviceFunctionsRealDataByIds(deviceIds, String.join(FileUtil.COMMA, functionLogos), 2).getData();
            }

            //对数据进行组装
            Map<String, Map<String, List<DeviceHistoryDto>>> finalPcsPowerHistoryDataMap = pcsPowerHistoryDataMap;
            Map<String, Map<String, RealDataModel>> finalRealDataMap = realDataMap;
            Map<String, Map<String, List<NodeDifHistoryDto>>> finalPcsQtHistoryDataMap = pcsQtHistoryDataMap;
            resultList = resultList.stream().peek(result -> {
                //储能系统装机容量(PCS总额定功率,电池簇总额定容量)
                result.setPcsPower(systemPcsRatedPowerMap.getOrDefault(result.getSystemId(), 0.0));
                result.setPcsRatedCap(systemBatteryRatedCapMap.getOrDefault(result.getSystemId(), 0.0));

                //定义储能系统设备实体类
                SystemStatisticsDto system = new SystemStatisticsDto();
                system.setFunctionLogos("储能系统");
                //获取储能系统设备故障和离线数量
                if (systemStatusMap.containsKey(result.getSystemId())) {
                    //通信状态 0-未注册 1-在线 2-故障 88-离线
                    Map<Integer, Long> statusMap = systemStatusMap.get(result.getSystemId());
                    system.setError(statusMap.getOrDefault(2, 0L).intValue());
                    system.setUnregistered(statusMap.getOrDefault(0, 0L).intValue());
                    system.setOffline(statusMap.getOrDefault(88, 0L).intValue());
                    system.setNormal(statusMap.getOrDefault(1, 0L).intValue());
                }
                List<CurveDto> curveList = Lists.newArrayList();
                if (systemPcsIdMap.containsKey(result.getSystemId())) {
                    List<String> systemPcsIds = systemPcsIdMap.get(result.getSystemId());
                    //获取储能系统功率曲线
                    Map<String, Double> systemEnergyPowerMap = finalPcsPowerHistoryDataMap.entrySet().stream()
                            .filter(d -> systemPcsIds.contains(d.getKey()))
                            .flatMap(d -> d.getValue().entrySet().stream().flatMap(s -> s.getValue().stream()))
                            .collect(Collectors.groupingBy(DeviceHistoryDto::getDateTime, Collectors.summingDouble(i ->
                                    StringUtil.isNotEmpty(i.getDataValue()) ? Double.parseDouble(String.valueOf(i.getDataValue())) : 0.0)));
                    //储能系统曲线
                    CurveDto powerCurve = new CurveDto();
                    powerCurve.setName("储能功率");
                    ExtraValueUtil.getPowerCurveList(curveList, systemEnergyPowerMap, powerCurve);

                    //储能系统实时功率
                    result.setPcsActivepower(DoubleUtil.getToDouble(finalRealDataMap.entrySet().stream().filter(d -> systemPcsIds.contains(d.getKey()))
                            .flatMap(d -> d.getValue().values().stream().filter(c -> StringUtil.isNotEmpty(c.getDataValue())))
                            .mapToDouble(d -> Double.parseDouble(String.valueOf(d.getDataValue()))).sum()));

                    Map<String, Double> systemPcsQtMap = finalPcsQtHistoryDataMap.entrySet().stream()
                            .filter(d -> systemPcsIds.contains(d.getKey()))
                            .flatMap(d -> d.getValue().entrySet().stream().flatMap(s -> s.getValue().stream().filter(c -> StringUtil.isNotEmpty(c.getFirstDataValue()) && StringUtil.isNotEmpty(c.getLastDataValue()))))
                            .collect(Collectors.groupingBy(NodeDifHistoryDto::getFunctionLogo, Collectors.summingDouble(c -> Double.parseDouble(String.valueOf(c.getLastDataValue())) - Double.parseDouble(String.valueOf(c.getFirstDataValue())))));
                    //储能系统今日充电量
                    if (systemPcsQtMap.containsKey(FunctionLogoParamVo.PCS_BATTERY_CHARGE)) {
                        result.setTotalBatteryCharge(DoubleUtil.getAbsDouble(systemPcsQtMap.getOrDefault(FunctionLogoParamVo.PCS_BATTERY_CHARGE, 0.0)));
                    }
                    //储能系统今日放电量
                    if (systemPcsQtMap.containsKey(FunctionLogoParamVo.PCS_BATTERY_DISCHARGE)) {
                        result.setTotalBatteryDischarge(DoubleUtil.getAbsDouble(systemPcsQtMap.getOrDefault(FunctionLogoParamVo.PCS_BATTERY_DISCHARGE, 0.0)));
                    }
                }
                system.setHistorys(curveList);
                result.setDevices(Collections.singletonList(system));

                //储能系统今日循环次数 今日放电量/储能电池簇额定容量
                if (StringUtil.isNotEmpty(result.getTotalBatteryDischarge()) && StringUtil.isNotEmpty(result.getPcsRatedCap()) && result.getPcsRatedCap() != 0.0) {
                    result.setLoopTimes(DoubleUtil.getToDouble(result.getTotalBatteryDischarge() / result.getPcsRatedCap()));
                }

                //储能系统SOC 储能电池簇可放电量/储能电池簇总额定容量
                if (StringUtil.isNotEmpty(result.getPcsRatedCap()) && result.getPcsRatedCap() != 0.0 && systemBatteryIdMap.containsKey(result.getSystemId())) {
                    List<String> systemBatteryIds = systemBatteryIdMap.get(result.getSystemId());
                    //计算储能电池簇可放电量 电池簇电池容量*(SOC / 100)
                    double batteryChargeQt = systemBatteryIds.stream().mapToDouble(batteryId -> {
                        Double ratedCap = 0.0;
                        if (batteryRatedCapMap.containsKey(batteryId)) {
                            ratedCap = batteryRatedCapMap.get(batteryId);
                        }
                        if (finalRealDataMap.containsKey(batteryId) && StringUtil.isNotEmpty(finalRealDataMap.get(batteryId))) {
                            RealDataModel realDataModel = finalRealDataMap.get(batteryId).get(FunctionLogoParamVo.BATTERY_TOTAL_SOC);
                            if (realDataModel != null && StringUtil.isNotEmpty(realDataModel.getDataValue())) {
                                return ratedCap * (Double.parseDouble(String.valueOf(realDataModel.getDataValue())) / 100);
                            }
                        }
                        return 0.0;
                    }).sum();
                    result.setSoc(DoubleUtil.getToDouble(batteryChargeQt / result.getPcsRatedCap() * 100));
                }
            }).collect(Collectors.toList());
            resultPage.setItems(resultList);
        }
        return ResponseResult.ok(resultPage);
    }

    @Override
    public ResponseResult<PageDto<BatterySupplyDto>> batterySupplyPage(String word, String area, String userId, Integer page, Integer size) {
        //返回的对象
        PageDto<BatterySupplyDto> resultPage = new PageDto<>();

        //返回的集合
        List<BatterySupplyDto> resultList = Lists.newArrayList();

        //根据用户id查询多个站点id
        List<String> siteIds = systemService.findAllOrganEmpowerByUserId(userId).getData().stream().map(OrganEmpowerListDto::getSiteId)
                .filter(StringUtil::isNotEmpty).distinct().collect(Collectors.toList());

        //根据多个站点id查询站点充电桩信息
        List<SiteInfoDto> siteInfoList = deviceService.findSiteBasicInfoByIds(siteIds).getData().values().stream()
                .filter(s -> s.getScenarioTypes().contains("3")).collect(Collectors.toList());
        //充电系统装机容量 系统id -> 电桩总额定功率
        Map<String, Double> systemPileRatedPowerMap = Maps.newHashMap();
        if (CollectionUtils.isNotEmpty(siteInfoList)) {
            for (SiteInfoDto siteInfo : siteInfoList) {
                //获取站点位置
                String siteLocation;
                JSONObject siteReaMap = JSONObject.parseObject(siteInfo.getSiteReadwriteObject());
                JSONObject location = siteReaMap.getJSONObject(SiteFieldParamVo.LOCATION);
                if (location != null && location.containsKey(SiteFieldParamVo.PROVINCE) && location.containsKey(SiteFieldParamVo.CITY)) {
                    siteLocation = location.getString(SiteFieldParamVo.PROVINCE) + location.getString(SiteFieldParamVo.CITY);
                } else {
                    siteLocation = null;
                }
                //获取场景系统数据
                if (CollectionUtils.isNotEmpty(siteInfo.getSiteScenarioTypeDtos())) {
                    List<SiteScenarioTypeDto> scenarioTypeList = siteInfo.getSiteScenarioTypeDtos().stream().filter(s -> StringUtil.isNotEmpty(s.getScenarioType())
                            && Objects.equals(s.getScenarioType(), 3)).collect(Collectors.toList());
                    if (CollectionUtils.isNotEmpty(scenarioTypeList)) {
                        resultList.addAll(scenarioTypeList.stream().map(scenarioType -> {
                            BatterySupplyDto result = new BatterySupplyDto();
                            result.setSystemId(scenarioType.getId());
                            result.setSystemName(scenarioType.getSystemName());
                            result.setSiteId(siteInfo.getId());
                            result.setSiteName(siteInfo.getSiteName());
                            result.setScenarioTypes(siteInfo.getScenarioTypes());
                            result.setLocation(siteLocation);
                            if(StringUtil.isNotEmpty(scenarioType.getReadwriteObject())) {
                                JSONObject readwriteMap = JSON.parseObject(scenarioType.getReadwriteObject());
                                if (readwriteMap.containsKey(SiteFieldParamVo.CAPACITY) && StringUtil.isNotEmpty(readwriteMap.getString(SiteFieldParamVo.CAPACITY))) {
                                    systemPileRatedPowerMap.put(scenarioType.getId(), readwriteMap.getDoubleValue(SiteFieldParamVo.CAPACITY));
                                } else {
                                    systemPileRatedPowerMap.put(scenarioType.getId(), 0.0);
                                }
                            }
                            return result;
                        }).collect(Collectors.toList()));
                    }
                }
            }

            //关键字(站点名称和系统名称)
            if (StringUtil.isNotEmpty(word)) {
                resultList = resultList.stream().filter(s -> (StringUtil.isNotEmpty(s.getSiteName()) && s.getSiteName().contains(word))
                        || (StringUtil.isNotEmpty(s.getSystemName()) && s.getSystemName().contains(word))).collect(Collectors.toList());
            }
            //所属省市
            if (StringUtil.isNotEmpty(area)) {
                resultList = resultList.stream().filter(s -> StringUtil.isNotEmpty(s.getLocation()) && s.getLocation().contains(area))
                        .collect(Collectors.toList());
            }
            resultPage = new PageDto<>(resultList, page, size);
            if (CollectionUtils.isEmpty(resultList)) {
                return ResponseResult.ok(resultPage);
            }
            resultList = resultPage.getItems();

            //根据多个系统id查询充电桩设备数据
            //充电桩系统状态 系统id -> (设备状态 -> 数量)
            Map<String, Map<Integer, Long>> systemStatusMap = Maps.newHashMap();
            //充电系统充电桩设备
            Map<String, List<String>> systemPileIdMap = Maps.newHashMap();
            List<String> pileTypeIds = Arrays.asList("28", "29", "30", "79");
            List<String> systemIds = resultList.stream().map(BatterySupplyDto::getSystemId).collect(Collectors.toList());
            deviceService.findDeviceInfoByParentIds(systemIds).getData().forEach((systemId, deviceList) -> {
                List<DeviceBasicInfoDto> systemPileList = deviceList.stream().filter(d -> pileTypeIds.contains(d.getTypeId())).collect(Collectors.toList());
                systemStatusMap.put(systemId, deviceList.stream().collect(Collectors.groupingBy(DeviceBasicInfoDto::getTxStatus, Collectors.counting())));
                systemPileIdMap.put(systemId, systemPileList.stream().map(DeviceBasicInfoDto::getId).collect(Collectors.toList()));
//                systemPileRatedPowerMap.put(systemId, systemPileList.stream().mapToDouble(pile -> {
//                    if (pile.getReaMap().containsKey(ReaFieldParamVo.RATED_POWER) && StringUtil.isNotEmpty(pile.getReaMap().get(ReaFieldParamVo.RATED_POWER))) {
//                        return Double.parseDouble(String.valueOf(pile.getReaMap().get(ReaFieldParamVo.RATED_POWER)));
//                    }
//                    return 0.0;
//                }).sum());
            });

            //查询充电桩充放电功率曲线
            //充电桩设备id -> (功能点标识 -> 设备历史数据实体)
            Map<String, Map<String, List<DeviceHistoryDto>>> pileHistoryDataMap = Maps.newHashMap();
            if (MapUtils.isNotEmpty(systemPileIdMap)) {
                Set<String> pileIds = systemPileIdMap.values().stream().flatMap(List::stream).collect(Collectors.toSet());

                DeviceHistoryQueryVo deviceInverterQueryVo = new DeviceHistoryQueryVo();
                deviceInverterQueryVo.setDeviceIds(pileIds);
                Set<String> functionLogos = new HashSet<>(Arrays.asList(FunctionLogoParamVo.PILE_CHARGEPOWER, FunctionLogoParamVo.PILE_DISCHARGEPOWER));
                deviceInverterQueryVo.setFunctionLogos(functionLogos);
                deviceInverterQueryVo.setStartTime(DateUtil.getDayStart(DateUtil.localDateToStr(LocalDate.now())));
                deviceInverterQueryVo.setEndTime(DateUtil.localDateTimeToStr(LocalDateTime.now()));
                deviceInverterQueryVo.setTimeInterval("1m");
                pileHistoryDataMap = dataService.findDeviceHistoryValueList(deviceInverterQueryVo).getData();

            }

            //根据多个系统id查询系统监控数据
            Map<String, SitePileMonitorDto> systemPileMonitorMap = Maps.newHashMap();
            Map<String, PileMonitorDataDto> operationOverviewMap = operationAnalysisService.countPileMonitorData(systemIds, 2).getData();
            if (MapUtils.isNotEmpty(operationOverviewMap)) {
                operationOverviewMap.forEach((dataId, pileMonitorData) -> {
                    SitePileMonitorDto result = new SitePileMonitorDto();
                    BeanUtils.copyProperties(pileMonitorData, result);
                    result.setDataId(dataId);
                    systemPileMonitorMap.put(dataId, result);
                });
            }

            //对数据进行组装
            Map<String, Map<String, List<DeviceHistoryDto>>> finalPileHistoryDataMap = pileHistoryDataMap;
            resultList = resultList.stream().peek(result -> {
                //充电系统装机容量
                if (systemPileRatedPowerMap.containsKey(result.getSystemId())) {
                    result.setCapacity(systemPileRatedPowerMap.get(result.getSystemId()));
                }
                //获取充电系统设备数据
                SystemStatisticsDto system = new SystemStatisticsDto();
                system.setFunctionLogos("充电系统");
                //获取系统设备数量
                if (systemStatusMap.containsKey(result.getSystemId())) {
                    //通信状态 0-未注册 1-在线 2-故障 88-离线
                    Map<Integer, Long> statusMap = systemStatusMap.get(result.getSystemId());
                    system.setError(statusMap.getOrDefault(2, 0L).intValue());
                    system.setUnregistered(statusMap.getOrDefault(0, 0L).intValue());
                    system.setOffline(statusMap.getOrDefault(88, 0L).intValue());
                    system.setNormal(statusMap.getOrDefault(1, 0L).intValue());
                }
                //充电系统功率(充电桩充放电功率)
                List<CurveDto> curveList = Lists.newArrayList();
                if (systemPileIdMap.containsKey(result.getSystemId())) {
                    List<String> pileIds = systemPileIdMap.get(result.getSystemId());
                    List<DeviceHistoryDto> deviceHistoryList = finalPileHistoryDataMap.entrySet().stream()
                            .filter(d -> pileIds.contains(d.getKey()))
                            .flatMap(d -> d.getValue().entrySet().stream().flatMap(s -> s.getValue().stream()))
                            .collect(Collectors.toList());
                    //功能点标识 -> 设备历史数据
                    Map<String, List<DeviceHistoryDto>> deviceFunctionMap = deviceHistoryList.stream().collect(Collectors.groupingBy(DeviceHistoryDto::getFunctionLogo));
                    if (deviceFunctionMap.containsKey(FunctionLogoParamVo.PILE_CHARGEPOWER)) {
                        Map<String, Double> chargePowerMap = deviceFunctionMap.get(FunctionLogoParamVo.PILE_CHARGEPOWER)
                                .stream().collect(Collectors.groupingBy(DeviceHistoryDto::getDateTime, Collectors.summingDouble(i ->
                                        StringUtil.isNotEmpty(i.getDataValue()) ? Double.parseDouble(String.valueOf(i.getDataValue())) : 0.0)));
                        CurveDto powerCurve = new CurveDto();
                        powerCurve.setName("充电功率");
                        ExtraValueUtil.getPowerCurveList(curveList, chargePowerMap, powerCurve);
                    }
                    if (deviceFunctionMap.containsKey(FunctionLogoParamVo.PILE_DISCHARGEPOWER)) {
                        Map<String, Double> chargePowerMap = deviceFunctionMap.get(FunctionLogoParamVo.PILE_DISCHARGEPOWER)
                                .stream().collect(Collectors.groupingBy(DeviceHistoryDto::getDateTime, Collectors.summingDouble(i ->
                                        StringUtil.isNotEmpty(i.getDataValue()) ? Double.parseDouble(String.valueOf(i.getDataValue())) : 0.0)));
                        CurveDto powerCurve = new CurveDto();
                        powerCurve.setName("放电功率");
                        ExtraValueUtil.getPowerCurveList(curveList, chargePowerMap, powerCurve);
                    }
                }
                system.setHistorys(curveList);
                result.setDevices(Collections.singletonList(system));
                //获取电桩系统监控数据
                if (systemPileMonitorMap.containsKey(result.getSystemId())) {
                    SitePileMonitorDto sitePileMonitor = systemPileMonitorMap.get(result.getSystemId());
                    result.setChargeOrderQt(sitePileMonitor.getChargeOrderQt());
                    result.setChargeOrderNum(sitePileMonitor.getChargeOrderNum());
                    result.setDischargeOrderQt(sitePileMonitor.getDischargeOrderQt());
                    result.setAvgChargeQt(sitePileMonitor.getAvgChargeQt());
                    result.setChargeSuccessRatio(sitePileMonitor.getChargeSuccessRatio());
                }
            }).collect(Collectors.toList());
            resultPage.setItems(resultList);
        }
        return ResponseResult.ok(resultPage);
    }

    @Override
    public ResponseResult<PageDto<BatteryChangeDto>> batteryChangePage(String word, String area, String userId, Integer page, Integer size) {
        //返回的对象
        PageDto<BatteryChangeDto> resultPage = new PageDto<>();

        //返回的集合
        List<BatteryChangeDto> resultList = Lists.newArrayList();

        //根据用户id查询多个站点id
        List<String> siteIds = systemService.findAllOrganEmpowerByUserId(userId).getData().stream().map(OrganEmpowerListDto::getSiteId)
                .filter(StringUtil::isNotEmpty).distinct().collect(Collectors.toList());

        //根据多个站点id查询站点储能信息
        List<SiteInfoDto> siteInfoList = deviceService.findSiteBasicInfoByIds(siteIds).getData().values().stream()
                .filter(s -> s.getScenarioTypes().contains("6")).collect(Collectors.toList());

        if (CollectionUtils.isNotEmpty(siteInfoList)) {
            for (SiteInfoDto siteInfo : siteInfoList) {
                //获取站点位置
                String siteLocation;
                JSONObject siteReaMap = JSONObject.parseObject(siteInfo.getSiteReadwriteObject());
                JSONObject location = siteReaMap.getJSONObject(SiteFieldParamVo.LOCATION);
                if (location != null && location.containsKey(SiteFieldParamVo.PROVINCE) && location.containsKey(SiteFieldParamVo.CITY)) {
                    siteLocation = location.getString(SiteFieldParamVo.PROVINCE) + location.getString(SiteFieldParamVo.CITY);
                } else {
                    siteLocation = null;
                }
                //获取场景系统数据
                List<SiteScenarioTypeDto> scenarioTypeList = siteInfo.getSiteScenarioTypeDtos().stream().filter(s -> Objects.equals(s.getScenarioType(), 6)).collect(Collectors.toList());
                if (CollectionUtils.isNotEmpty(scenarioTypeList)) {
                    resultList.addAll(scenarioTypeList.stream().map(scenarioType -> {
                        BatteryChangeDto result = new BatteryChangeDto();
                        result.setSystemId(scenarioType.getId());
                        result.setSystemName(scenarioType.getSystemName());
                        result.setSiteId(siteInfo.getId());
                        result.setSiteName(siteInfo.getSiteName());
                        result.setScenarioTypes(siteInfo.getScenarioTypes());
                        result.setLocation(siteLocation);
                        return result;
                    }).collect(Collectors.toList()));
                }
            }

            //关键字(站点名称和系统名称)
            if (StringUtil.isNotEmpty(word)) {
                resultList = resultList.stream().filter(s -> (StringUtil.isNotEmpty(s.getSiteName()) && s.getSiteName().contains(word))
                        || (StringUtil.isNotEmpty(s.getSystemName()) && s.getSystemName().contains(word))).collect(Collectors.toList());
            }
            //所属省市
            if (StringUtil.isNotEmpty(area)) {
                resultList = resultList.stream().filter(s -> StringUtil.isNotEmpty(s.getLocation()) && s.getLocation().contains(area))
                        .collect(Collectors.toList());
            }

            resultPage = new PageDto<>(resultList, page, size);
            if (CollectionUtils.isEmpty(resultList)) {
                return ResponseResult.ok(resultPage);
            }
            resultList = resultPage.getItems();

            //换电系统状态 系统id -> (设备状态 -> 数量)
            Map<String, Map<Integer, Long>> systemStatusMap = Maps.newHashMap();
            //系统id -> 多个换电仓id
            Map<String, List<String>> systemGranaryIdMap = Maps.newHashMap();
            //根据多个系统id查询设备数据
            List<String> systemIds = resultList.stream().map(BatteryChangeDto::getSystemId).collect(Collectors.toList());
            deviceService.findDeviceInfoByParentIds(systemIds).getData().forEach((systemId, deviceList) -> {
                systemStatusMap.put(systemId, deviceList.stream().collect(Collectors.groupingBy(DeviceBasicInfoDto::getTxStatus, Collectors.counting())));

                //获取系统换电仓id
                systemGranaryIdMap.put(systemId, deviceList.stream().filter(s -> Objects.equals(s.getTypeId(), "70"))
                        .map(DeviceBasicInfoDto::getId).collect(Collectors.toList()));
            });

            //换电仓设备id -> (功能点标识 -> 设备历史数据实体)
            Map<String, Map<String, List<DeviceHistoryDto>>> granaryPowerDataMap = Maps.newHashMap();
            Map<String, Map<String, List<NodeDifHistoryDto>>> granaryChargeQtDataMap = Maps.newHashMap();
            if (MapUtils.isNotEmpty(systemGranaryIdMap)) {
                //定义查询历史数据的开始时间和结束时间
                String startTime = DateUtil.getDayStart(DateUtil.localDateToStr(LocalDate.now()));
                String endTime = DateUtil.localDateTimeToStr(LocalDateTime.now());

                Set<String> granaryIds = systemGranaryIdMap.values().stream().flatMap(List::stream).collect(Collectors.toSet());

                //根据多个换电仓id查询功率数据
                DeviceHistoryQueryVo devicePowerQueryVo = new DeviceHistoryQueryVo();
                devicePowerQueryVo.setDeviceIds(granaryIds);
                devicePowerQueryVo.setFunctionLogos(Collections.singleton(FunctionLogoParamVo.BATTERY_GRANARY_OUT_POWER));
                devicePowerQueryVo.setStartTime(startTime);
                devicePowerQueryVo.setEndTime(endTime);
                devicePowerQueryVo.setTimeInterval("1m");
                granaryPowerDataMap = dataService.findDeviceHistoryValueList(devicePowerQueryVo).getData();

                //根据多个换电仓设备id查询今日充电量
                DeviceHistoryQueryVo deviceChargeQtQueryVo = new DeviceHistoryQueryVo();
                deviceChargeQtQueryVo.setDeviceIds(granaryIds);
                deviceChargeQtQueryVo.setFunctionLogos(Collections.singleton(FunctionLogoParamVo.BATTERY_GRANARY_SUP_CHARGE));
                deviceChargeQtQueryVo.setStartTime(startTime);
                deviceChargeQtQueryVo.setEndTime(endTime);
                deviceChargeQtQueryVo.setTimeInterval("1d");
                granaryChargeQtDataMap = dataService.findNodeDifHistoryListFeign(deviceChargeQtQueryVo).getData();
            }

            //对数据进行组装
            Map<String, Map<String, List<DeviceHistoryDto>>> finalGranaryPowerDataMap = granaryPowerDataMap;
            Map<String, Map<String, List<NodeDifHistoryDto>>> finalGranaryChargeQtDataMap = granaryChargeQtDataMap;
            resultList = resultList.stream().peek(result -> {
                //定义换电系统设备实体类
                SystemStatisticsDto system = new SystemStatisticsDto();
                system.setFunctionLogos("换电系统");
                //获取换电系统设备故障和离线数量
                if (systemStatusMap.containsKey(result.getSystemId())) {
                    //通信状态 0-未注册 1-在线 2-故障 88-离线
                    Map<Integer, Long> statusMap = systemStatusMap.get(result.getSystemId());
                    system.setError(statusMap.getOrDefault(2, 0L).intValue());
                    system.setUnregistered(statusMap.getOrDefault(0, 0L).intValue());
                    system.setOffline(statusMap.getOrDefault(88, 0L).intValue());
                    system.setNormal(statusMap.getOrDefault(1, 0L).intValue());
                }
                List<CurveDto> curveList = Lists.newArrayList();
                if (systemGranaryIdMap.containsKey(result.getSystemId())) {
                    List<String> systemGranaryIds = systemGranaryIdMap.get(result.getSystemId());
                    //充电仓位数
                    result.setChargePosition(systemGranaryIds.size());

                    //获取换电系统功率曲线
                    Map<String, Double> systemPowerMap = finalGranaryPowerDataMap.entrySet().stream()
                            .filter(d -> systemGranaryIds.contains(d.getKey()))
                            .flatMap(d -> d.getValue().entrySet().stream().flatMap(s -> s.getValue().stream()))
                            .collect(Collectors.groupingBy(DeviceHistoryDto::getDateTime, Collectors.summingDouble(i ->
                                    StringUtil.isNotEmpty(i.getDataValue()) ? Double.parseDouble(String.valueOf(i.getDataValue())) : 0.0)));
                    //换电系统功率曲线
                    CurveDto powerCurve = new CurveDto();
                    powerCurve.setName("换电功率");
                    ExtraValueUtil.getPowerCurveList(curveList, systemPowerMap, powerCurve);

                    //换电系统今日充电量
                    result.setDayChargeQt(DoubleUtil.getToDouble(finalGranaryChargeQtDataMap.entrySet().stream()
                            .filter(d -> systemGranaryIds.contains(d.getKey()))
                            .flatMap(d -> d.getValue().entrySet().stream().flatMap(s -> s.getValue().stream()))
                            .mapToDouble(d -> {
                                if (StringUtil.isNotEmpty(d.getFirstDataValue()) && StringUtil.isNotEmpty(d.getLastDataValue())) {
                                    return Double.parseDouble(String.valueOf(d.getLastDataValue())) - Double.parseDouble(String.valueOf(d.getFirstDataValue()));
                                }
                                return 0.0;
                            }).sum(), 3));

                }
                system.setHistorys(curveList);
                result.setPowerList(Collections.singletonList(system));

            }).collect(Collectors.toList());
            resultPage.setItems(resultList);
        }
        return ResponseResult.ok(resultPage);
    }

    @Override
    public ResponseResult<List<DeviceOverviewDto>> getDeviceList(String siteId, Integer type) {
        //返回的集合
        List<DeviceOverviewDto> resultList = Lists.newArrayList();
        if (StringUtil.isEmpty(siteId) || StringUtil.isEmpty(type)) {
            return ResponseResult.paramError(ResponseResult.PARAM_ERROR);
        }
        //根据站点id和类型查询设备
        //设备类型 1-充电桩设备 2-通信设备 3-光伏设备 4-储能设备 5-计量设备 6-配电设备 7-感知设备 8-开关设备 9-视频监控设备 10-保护装置设备 11-负载设备 12-车位管理设备 13-换电设备
        Integer deviceType = null;
        //类型 1-光伏 2-储能 3-充电桩 4-用能 5-配电 6-换电
        switch (type) {
            case 1:
                deviceType = 3;
                break;
            case 2:
                deviceType = 4;
                break;
            case 3:
                break;
//            case 4:
//                deviceType = 5;
//                break;
//            case 5:
//                deviceType = 6;
//                break;
            case 6:
                deviceType = 13;
                break;
        }
        List<DeviceBasicInfoDto> deviceList;
        if (type != 6 ) {
            deviceList = deviceService.findDeviceBasicInfoBySiteIds(Collections.singletonList(siteId), deviceType).getData().get(siteId);
        } else {
            deviceList = deviceService.findDeviceBasicInfoBySiteIds(Collections.singletonList(siteId), null).getData().get(siteId);
            if (CollectionUtils.isNotEmpty(deviceList)) {
                List<Integer> assetTypeList = Lists.newArrayList();
                assetTypeList.addAll(StringUtil.getDeviceAssetType(13));
                assetTypeList.add(36); //传感器设备
                deviceList = deviceList.stream().filter(d -> StringUtil.isNotEmpty(d.getTypeId()) && assetTypeList.contains(Integer.parseInt(d.getTypeId()))).collect(Collectors.toList());
            }
        }
        if (CollectionUtils.isNotEmpty(deviceList)) {
            //类型 1-光伏 2-储能 3-充电桩 4-用能 5-配电 6-换电
            switch (type) {
                case 1:
                    resultList = getPvOverviewList(deviceList);
                    break;
                case 2:
                    resultList = getStorageOverviewList(deviceList);
                    break;
                case 3:
                    resultList = getPileOverviewList(deviceList);
                    break;
//                case 4:
//                    break;
//                case 5:
//                    break;
                case 6:
                    resultList = getUseOverviewList(deviceList);
                    break;
            }
        }
        return ResponseResult.ok(resultList);
    }

    @Override
    public ResponseResult<List<SiteTopDataDto>> findSiteTopDataListBySiteId(String siteId) {
        return deviceService.findSiteTopDataListBySiteId(siteId);
    }

    @Override
    public ResponseResult<List<SiteGateTopDto>> findSiteGateTopBySiteId(String siteId) {
        //返回的集合
        List<SiteGateTopDto> resultList = Lists.newArrayList();
        //根据站点id和节点类型查询关口表数据
        List<SiteTopNodeDto> siteTopNodeList = deviceService.findSiteTopNodeBySiteId(siteId, 4).getData();
        if (CollectionUtils.isNotEmpty(siteTopNodeList)) {
            resultList = siteTopNodeList.stream().map(siteTopNode -> {
                SiteGateTopDto result = new SiteGateTopDto();
                BeanUtils.copyProperties(siteTopNode, result);
                return result;
            }).collect(Collectors.toList());
        }
        return ResponseResult.ok(resultList);
    }

    @Override
    public ResponseResult<SiteTopCurveDto> findSiteTopCurveList(String siteId, String nodeId, String queryDate) {
        //返回的对象
        SiteTopCurveDto result = new SiteTopCurveDto();

        //校验入参
        if (StringUtil.isEmpty(siteId) || StringUtil.isEmpty(nodeId) || StringUtil.isEmpty(queryDate)) {
            return ResponseResult.paramError(ResponseResult.PARAM_ERROR);
        }
        //定义开始时间和结束时间
        String startTime = DateUtil.getDayStart(queryDate);
        String endTime;
        String nowDate = DateUtil.localDateToStr(LocalDate.now());
        if (Objects.equals(queryDate, nowDate)) {
            endTime = DateUtil.localDateTimeToStr(LocalDateTime.now());
        } else {
            endTime = DateUtil.getDayEnd(queryDate);
        }
        //定义曲线列表数据
        List<SiteTopCurveDto.CurveData> curveDataList = Lists.newArrayList();

        //获取日期列表
        Set<String> dateList = DateUtil.getDateFormatTimeList(startTime, endTime, "1m", 6);
        result.setDateList(dateList);
        //根据站点id查询站点拓扑节点数据
        List<SiteTopNodeDto> siteTopNodeList = deviceService.findSiteTopNodeBySiteId(siteId, null).getData();
        if (CollectionUtils.isNotEmpty(siteTopNodeList)) {
            //获取该节点下面的关口表节点,逆变器节点,储能柜节点,负荷节点,充电桩节点,换电站节点
            List<Integer> nodeTypes = Arrays.asList(4, 6, 8, 9, 10, 13);
            Map<Integer, List<SiteTopNodeDto>> topNodeTypeMap = setChild(nodeId, siteTopNodeList).stream()
                    .filter(d -> StringUtil.isNotEmpty(d.getNodeType()) && nodeTypes.contains(d.getNodeType()))
                    .collect(Collectors.groupingBy(SiteTopNodeDto::getNodeType));
            if (MapUtils.isNotEmpty(topNodeTypeMap)) {
                //定义数据对象
                double safeCap = 0.0;
                Map<String, Double> gatePowerMap = Maps.newHashMap();
                Map<String, Double> pvPowerMap = Maps.newHashMap();
                Map<String, Double> sePowerMap = Maps.newHashMap();
                Map<String, Double> pilePowerMap = Maps.newHashMap();
                Map<String, Double> changePowerMap = Maps.newHashMap();
                Map<String, Map<String, Double>> nodeBatterySocMap = Maps.newHashMap();
                //获取数据
                for (Map.Entry<Integer, List<SiteTopNodeDto>> entry : topNodeTypeMap.entrySet()) {
                    //节点类型 1-拓扑点 2-电网 3-变压器 4-关口点 5-计量点 6-逆变器 7-光伏组件 8-储能柜 9-负荷 10-充电桩 11-开关 12-车辆 13-换电
                    Integer nodeType = entry.getKey();
                    List<SiteTopNodeDto> topNodeList = entry.getValue().stream().filter(t -> StringUtil.isNotEmpty(t.getDeviceIds())).collect(Collectors.toList());
                    //多个设备id(使用场景 除储能柜以外)
                    Set<String> deviceIds = Sets.newHashSet();
                    //储能柜相关设备id
                    Map<String, Set<String>> nodePcsMap = Maps.newHashMap();
                    Map<String, Set<String>> nodeBatteryMap = Maps.newHashMap();
                    Map<String, Integer> nodeParaMap = Maps.newHashMap();

                    if (nodeType != 8) {
                        deviceIds = topNodeList.stream().map(d -> JSON.parseArray(d.getDeviceIds(), String.class))
                                .flatMap(Collection::stream).collect(Collectors.toSet());
                    } else {
                        for (SiteTopNodeDto topNode : topNodeList) {
                            Set<String> pcsIds = Sets.newHashSet();
                            Set<String> batteryIds = Sets.newHashSet();
                            List<JSONObject> jsonObjects = JSON.parseArray(topNode.getDeviceIds(), JSONObject.class);
                            jsonObjects.forEach(jsonObject -> {
                                if (jsonObject.containsKey("pcsId")) {
                                    pcsIds.add(jsonObject.getString("pcsId"));
                                }
                                if (jsonObject.containsKey("batteryId")) {
                                    batteryIds.add(jsonObject.getString("batteryId"));
                                }
                            });
                            nodePcsMap.put(topNode.getId(), pcsIds);
                            nodeBatteryMap.put(topNode.getId(), batteryIds);
                            if (StringUtil.isNotEmpty(topNode.getReaObject())) {
                                JSONObject reaObject = JSON.parseObject(topNode.getReaObject());
                                //多柜并机运行 1-是 2-否
                                if (reaObject.containsKey("operation")) {
                                    nodeParaMap.put(topNode.getId(), reaObject.getInteger("operation"));
                                }
                            }
                        }
                    }
                    switch (entry.getKey()) {
                        case 4: //关口表曲线数据
                            //变压器安全容量
                            safeCap = entry.getValue().stream().filter(d -> StringUtil.isNotEmpty(d.getReaObject())).mapToDouble(d -> {
                                JSONObject reaObject = JSON.parseObject(d.getReaObject());
                                if (reaObject.containsKey("safeByq")) {
                                    return reaObject.getDouble("safeByq");
                                }
                                return 0.0;
                            }).sum();
                            //关口表功率数据
                            gatePowerMap = getDevicePowerList(deviceIds, startTime, endTime, FunctionLogoParamVo.TOTAL_ACTIVE_POWER);
                            break;
                        case 6: //光伏曲线数据
                            pvPowerMap = getDevicePowerList(deviceIds, startTime, endTime, FunctionLogoParamVo.ACTIVE_POWER);
                            break;
                        case 8: //储能曲线数据
                            //储能功率数据
                            sePowerMap = getDevicePowerList(nodePcsMap.values().stream().flatMap(Collection::stream).collect(Collectors.toSet()),
                                    startTime, endTime, FunctionLogoParamVo.PCS_ACTIVE_POWER);
                            //计算储能SOC数据
                            nodeBatterySocMap = getSeNodeSocMap(nodeBatteryMap, nodeParaMap, startTime, endTime);
                            break;
                        case 9: //负荷曲线数据
                            break;
                        case 10: //充电桩曲线数据
                            pilePowerMap = getDevicePowerList(deviceIds, startTime, endTime, FunctionLogoParamVo.PILE_POWER);
                            break;
                        case 13: //换电曲线数据
                            changePowerMap = getDevicePowerList(deviceIds, startTime, endTime, FunctionLogoParamVo.BATTERY_GRANARY_OUT_POWER);
                            break;
                    }
                }
                //拓扑节点id -> 拓扑节点名称
                Map<String, String> siteTopNodeNameMap = topNodeTypeMap.entrySet().stream().flatMap(entry -> entry.getValue().stream())
                        .collect(Collectors.toMap(SiteTopNodeDto::getId, SiteTopNodeDto::getNodeName, (k1, k2) -> k1));
                //定义数据集合
                List<Double> safeCapList = Lists.newArrayList();
                List<Double> gatePowerList = Lists.newArrayList();
                List<Double> pvPowerList = Lists.newArrayList();
                List<Double> sePowerList = Lists.newArrayList();
                List<Double> pilePowerList = Lists.newArrayList();
                List<Double> changePowerList = Lists.newArrayList();
                Map<String, List<Double>> nodeSocMap = Maps.newHashMap();
                //对数据进行组装
                for (String date : dateList) {
                    safeCapList.add(safeCap);
                    gatePowerList.add(gatePowerMap.getOrDefault(date, null));
                    pvPowerList.add(pvPowerMap.getOrDefault(date, null));
                    sePowerList.add(sePowerMap.getOrDefault(date, null));
                    pilePowerList.add(pilePowerMap.getOrDefault(date, null));
                    changePowerList.add(changePowerMap.getOrDefault(date, null));
                    //储能柜SOC数据
                    for (Map.Entry<String, Map<String, Double>> entry : nodeBatterySocMap.entrySet()) {
                        String topNodeId = entry.getKey();
                        List<Double> socList;
                        if (nodeSocMap.containsKey(topNodeId)) {
                            socList = nodeSocMap.get(topNodeId);
                        } else {
                            socList = Lists.newArrayList();
                        }
                        socList.add(entry.getValue().getOrDefault(date, null));
                        nodeSocMap.put(topNodeId, socList);
                    }
                }

                //对数据二次组装
                curveDataList.add(getSiteTopCurve(0, gatePowerList, TopCurveParamVo.GATE_POWER_LIST, "关口表功率"));
                curveDataList.add(getSiteTopCurve(0, safeCapList, TopCurveParamVo.SAFE_CAP_LIST, "变压器安全容量"));
                curveDataList.add(getSiteTopCurve(1, pvPowerList, TopCurveParamVo.PV_POWER_LIST, "光伏功率"));
                curveDataList.add(getSiteTopCurve(2, sePowerList, TopCurveParamVo.STORAGE_POWER_LIST, "储能功率"));
                curveDataList.add(getSiteTopCurve(3, pilePowerList, TopCurveParamVo.PILE_POWER_LIST, "电桩功率"));
                curveDataList.add(getSiteTopCurve(6, changePowerList, TopCurveParamVo.CHANGE_POWER_LIST, "换电功率"));

                //对储能SOC相关特殊处理
                List<Map.Entry<String, List<Double>>> nodeSocList = new ArrayList<>(nodeSocMap.entrySet());
                IntStream.range(0, nodeSocList.size()).forEach(i -> {
                    Map.Entry<String, List<Double>> entry = nodeSocList.get(i);
                    //曲线编码
                    String curveCode = TopCurveParamVo.STORAGE_SOC_LIST;
                    //曲线名称
                    String curveName = "储能SOC";
                    if (nodeSocList.size() > 1) {
                        curveCode += FileUtil.LEFT_SQUARE + i + FileUtil.RIGHT_SQUARE;
                        if (siteTopNodeNameMap.containsKey(entry.getKey())) {
                            curveName += FileUtil.LEFT_BRACKET;
                            curveName += siteTopNodeNameMap.get(entry.getKey());
                            curveName += FileUtil.RIGHT_BRACKET;
                        }
                        curveDataList.add(getSiteTopCurve(2, entry.getValue(), curveCode, curveName));
                    } else {
                        curveDataList.add(getSiteTopCurve(2, entry.getValue(), curveCode, curveName));
                    }
                });
            }

        }

        if (CollectionUtils.isNotEmpty(curveDataList)) {
            result.setCurveDataMap(curveDataList.stream().collect(Collectors.groupingBy(SiteTopCurveDto.CurveData::getDataType)));
        }
        return ResponseResult.ok(result);
    }

    @Override
    public ResponseResult<List<WeatherDayDto>> getWeatherDayListBySiteId(String siteId) {
        //返回的集合
        List<WeatherDayDto> resultList = Lists.newArrayList();
        //根据站点id查询经纬度
        SiteInfoDto siteInfo = deviceService.findSiteBasicInfoByIds(Collections.singletonList(siteId)).getData().get(siteId);
        if (siteInfo != null) {
            //获取经纬度
            Double longitude = null;
            Double latitude = null;
            if (StringUtil.isNotEmpty(siteInfo.getSiteReadwriteObject())) {
                JSONObject jsonObject = JSON.parseObject(siteInfo.getSiteReadwriteObject());
                if (jsonObject.containsKey(SiteFieldParamVo.LOCATION)) {
                    JSONObject location = jsonObject.getJSONObject(SiteFieldParamVo.LOCATION);
                    if (location.containsKey(SiteFieldParamVo.LONGITUDE) && StringUtil.isNotEmpty(location.get(SiteFieldParamVo.LONGITUDE))
                            && location.containsKey(SiteFieldParamVo.LATITUDE) && StringUtil.isNotEmpty(location.get(SiteFieldParamVo.LATITUDE))) {
                        longitude = location.getDouble(SiteFieldParamVo.LONGITUDE); //经度
                        latitude = location.getDouble(SiteFieldParamVo.LATITUDE);//纬度
                    }
                }
            }
            //查询天气数据
            if (longitude != null && latitude != null && StringUtil.isNotEmpty(longitude) && StringUtil.isNotEmpty(latitude)) {
                resultList = WeatherHfUtil.getWeatherDay(longitude, latitude, "3d");
            }
        }
        return ResponseResult.ok(resultList);
    }


    /**
     * 获取拓扑节点曲线数据
     *
     * @param dataType  类型
     * @param dataList  数据
     * @param curveCode 曲线编码
     * @param curveName 曲线名称
     * @return 曲线数据
     */
    private SiteTopCurveDto.CurveData getSiteTopCurve(Integer dataType, List<Double> dataList, String curveCode, String curveName) {
        //返回的对象
        SiteTopCurveDto.CurveData result = new SiteTopCurveDto.CurveData();
        result.setDataType(dataType);
        result.setCurveCode(curveCode);
        result.setCurveName(curveName);
        result.setCurveList(dataList);
        return result;
    }

    //获取功率曲线数据
    private Map<String, Double> getDevicePowerList(Set<String> deviceIds, String startTime, String endTime, String functionLogo) {
        DeviceHistoryQueryVo changeQueryVo = new DeviceHistoryQueryVo();
        changeQueryVo.setDeviceIds(deviceIds);
        changeQueryVo.setFunctionLogos(Collections.singleton(functionLogo));
        changeQueryVo.setStartTime(startTime);
        changeQueryVo.setEndTime(endTime);
        changeQueryVo.setTimeInterval("1m");
        return dataService.findDeviceHistoryValueList(changeQueryVo).getData().values().stream()
                .flatMap(d -> d.values().stream().flatMap(Collection::stream))
                .filter(d -> StringUtil.isNotEmpty(d.getDateTime()) && StringUtil.isNotEmpty(d.getDataValue()))
                .collect(Collectors.groupingBy(d -> d.getDateTime().substring(11, 16),
                        Collectors.summingDouble(d -> Double.parseDouble(String.valueOf(d.getDataValue())))))
                .entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, c -> DoubleUtil.getToDouble(c.getValue())));
    }

    /**
     * 获取储能SOC数据
     *
     * @param nodeBatteryIdMap 储能设备id
     * @param nodeParaMap      储能是否并机运行参数
     * @param startTime        开始时间
     * @param endTime          结束时间
     * @return 节点id -> (日期 -> SOC值)
     */
    public Map<String, Map<String, Double>> getSeNodeSocMap(Map<String, Set<String>> nodeBatteryIdMap, Map<String, Integer> nodeParaMap, String startTime, String endTime) {
        //返回的对象
        Map<String, Map<String, Double>> resultMap = Maps.newHashMap();
        Set<String> deviceIdSet = nodeBatteryIdMap.values().stream().flatMap(Collection::stream).collect(Collectors.toSet());
        //根据多个电池簇id查询可放电量,电池SOC
        DeviceHistoryQueryVo deviceQueryVo = new DeviceHistoryQueryVo();
        deviceQueryVo.setDeviceIds(deviceIdSet);
        deviceQueryVo.setFunctionLogos(new HashSet<>(Arrays.asList(FunctionLogoParamVo.BATTERY_ENABLE_DISCHARGE_Q, FunctionLogoParamVo.BATTERY_TOTAL_SOC)));
        deviceQueryVo.setStartTime(startTime);
        deviceQueryVo.setEndTime(endTime);
        Map<String, Map<String, List<DeviceHistoryDto>>> deviceHistoryMap = dataService.findDeviceHistoryValueList(deviceQueryVo).getData();

        //根据多个设备id查询电池簇总容量
        Map<String, String> deviceObjectMap = deviceService.findDeviceBasicInfoByIds(new ArrayList<>(deviceIdSet)).getData().values().stream()
                .filter(d -> StringUtil.isNotEmpty(d.getReadwriteObject()))
                .collect(Collectors.toMap(DeviceBasicInfoDto::getId, DeviceBasicInfoDto::getReadwriteObject));

        //对数据进行组装
        for (Map.Entry<String, Set<String>> entry : nodeBatteryIdMap.entrySet()) {
            String nodeId = entry.getKey(); //节点id
            Set<String> deviceIds = entry.getValue(); //多个设备id
            Map<String, Double> dataMap = Maps.newHashMap(); //数据对象
            if (nodeParaMap.containsKey(nodeId) && StringUtil.isNotEmpty(nodeParaMap.get(nodeId))) {
                //获取该节点下面的设备数据
                Map<String, List<DeviceHistoryDto>> deviceDataMap = deviceIds.stream().filter(deviceHistoryMap::containsKey)
                        .flatMap(d -> deviceHistoryMap.get(d).values().stream().flatMap(Collection::stream))
                        .collect(Collectors.groupingBy(DeviceHistoryDto::getFunctionLogo));
                Integer nodePara = nodeParaMap.get(nodeId);
                //是否多柜并机运行 1-是 2-否
                if (nodePara == 1) {
                    if (deviceDataMap.containsKey(FunctionLogoParamVo.BATTERY_TOTAL_SOC)) {
                        dataMap = deviceDataMap.get(FunctionLogoParamVo.BATTERY_TOTAL_SOC).stream().filter(d -> StringUtil.isNotEmpty(d.getDataValue())
                                        && StringUtil.isNotEmpty(d.getDateTime())).collect(Collectors.groupingBy(d -> d.getDateTime().substring(11, 16),
                                        Collectors.minBy(Comparator.comparing(c -> Double.parseDouble(String.valueOf(c.getDataValue()))))))
                                .entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, c -> Double.parseDouble(String.valueOf(c.getValue().orElse(new DeviceHistoryDto()).getDataValue()))));
                    }
                }
                if (nodePara == 2) {
                    //总电池容量
                    double ratedCap = deviceIds.stream().filter(deviceObjectMap::containsKey).mapToDouble(deviceId -> {
                        JSONObject deviceObject = JSON.parseObject(deviceObjectMap.get(deviceId));
                        if (deviceObject.containsKey(ReaFieldParamVo.RATED_CAP)) {
                            return deviceObject.getDouble(ReaFieldParamVo.RATED_CAP);
                        }
                        return 0.0;
                    }).sum();

                    if (deviceDataMap.containsKey(FunctionLogoParamVo.BATTERY_ENABLE_DISCHARGE_Q)) {
                        dataMap = deviceDataMap.get(FunctionLogoParamVo.BATTERY_ENABLE_DISCHARGE_Q).stream().filter(d -> StringUtil.isNotEmpty(d.getDataValue())
                                        && StringUtil.isNotEmpty(d.getDateTime())).collect(Collectors.groupingBy(d -> d.getDateTime().substring(11, 16),
                                        Collectors.summingDouble(d -> Double.parseDouble(String.valueOf(d.getDataValue())))))
                                .entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, c -> DoubleUtil.getToDouble(c.getValue() / ratedCap * 100)));
                    }
                }
            }
            resultMap.put(nodeId, dataMap);
        }
        return resultMap;
    }

    //获取光伏概览数据
    private List<DeviceOverviewDto> getPvOverviewList(List<DeviceBasicInfoDto> deviceList) {
        List<DeviceOverviewDto> resultList = Lists.newArrayList();
        //获取逆变器设备数据
        List<DeviceBasicInfoDto> deviceInverterList = deviceList.stream().filter(d -> Objects.equals(d.getTypeId(), "20"))
                .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(deviceInverterList)) {
            DeviceOverviewDto device = new DeviceOverviewDto();
            device.setTypeId("20");
            device.setTypeName("逆变器");
            device.setDeviceList(this.getDeviceInverterList(deviceInverterList));
            resultList.add(device);
        }
        //获取光伏DC/DC设备数据
        List<DeviceBasicInfoDto> deviceDcDcList = deviceList.stream().filter(d -> Objects.equals(d.getTypeId(), "77"))
                .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(deviceDcDcList)) {
            DeviceOverviewDto device = new DeviceOverviewDto();
            device.setTypeId("77");
            device.setTypeName("光伏DC/DC");
            device.setDeviceList(this.getDeviceInverterList(deviceDcDcList));
            resultList.add(device);
        }
        //获取气象站设备数据
        List<DeviceBasicInfoDto> deviceStationList = deviceList.stream().filter(d -> Objects.equals(d.getTypeId(), "65"))
                .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(deviceStationList)) {
            DeviceOverviewDto device = new DeviceOverviewDto();
            device.setTypeId("65");
            device.setTypeName("气象站");
            device.setDeviceList(this.getDeviceStationList(deviceStationList));
            resultList.add(device);
        }
        return resultList;
    }

    //获取储能概览数据
    private List<DeviceOverviewDto> getStorageOverviewList(List<DeviceBasicInfoDto> deviceList) {
        List<DeviceOverviewDto> resultList = Lists.newArrayList();
        //获取PCS设备数据
        List<DeviceBasicInfoDto> devicePcsList = deviceList.stream().filter(d -> Objects.equals(d.getTypeId(), "23"))
                .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(devicePcsList)) {
            DeviceOverviewDto device = new DeviceOverviewDto();
            device.setTypeId("23");
            device.setTypeName("PCS");
            device.setDeviceList(this.getDevicePcsList(devicePcsList));
            resultList.add(device);
        }
        //获取储能DC/DC设备数据
        List<DeviceBasicInfoDto> deviceDcDcList = deviceList.stream().filter(d -> Objects.equals(d.getTypeId(), "78"))
                .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(deviceDcDcList)) {
            DeviceOverviewDto device = new DeviceOverviewDto();
            device.setTypeId("78");
            device.setTypeName("储能DC/DC");
            device.setDeviceList(this.getDevicePcsList(deviceDcDcList));
            resultList.add(device);
        }
        //获取电池簇设备数据
        List<DeviceBasicInfoDto> deviceBatteryList = deviceList.stream().filter(d -> Objects.equals(d.getTypeId(), "25"))
                .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(deviceBatteryList)) {
            DeviceOverviewDto device = new DeviceOverviewDto();
            device.setTypeId("25");
            device.setTypeName("电池簇");
            device.setDeviceList(this.getDeviceBatteryList(deviceBatteryList));
            resultList.add(device);
        }
        //获取储能辅助设备数据
        List<DeviceBasicInfoDto> deviceAuxiliaryList = deviceList.stream().filter(d -> Objects.equals(d.getTypeId(), "60"))
                .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(deviceAuxiliaryList)) {
            DeviceOverviewDto device = new DeviceOverviewDto();
            device.setTypeId("60");
            device.setTypeName("辅助设备");
            device.setDeviceList(this.getDeviceAuxiliaryList(deviceAuxiliaryList));
            resultList.add(device);
        }
        return resultList;
    }

    //获取电桩概览数据
    private List<DeviceOverviewDto> getPileOverviewList(List<DeviceBasicInfoDto> deviceList) {
        //返回的集合
        List<DeviceOverviewDto> resultList = Lists.newArrayList();
        //定义设备电桩集合
        List<DeviceBasicInfoDto> devicePileList = Lists.newArrayList();
        List<DeviceBasicInfoDto> deviceAcList = deviceList.stream().filter(d -> Objects.equals(d.getTypeId(), "28"))
                .collect(Collectors.toList());
        List<DeviceBasicInfoDto> deviceDcList = deviceList.stream().filter(d -> Objects.equals(d.getTypeId(), "29"))
                .collect(Collectors.toList());
        List<DeviceBasicInfoDto> deviceV2GList = deviceList.stream().filter(d -> Objects.equals(d.getTypeId(), "30"))
                .collect(Collectors.toList());
        Map<String, List<DeviceDto>> deviceMap = Maps.newHashMap();
        devicePileList.addAll(deviceAcList);
        devicePileList.addAll(deviceDcList);
        devicePileList.addAll(deviceV2GList);
        if (CollectionUtils.isNotEmpty(devicePileList)) {
            deviceMap = this.getDevicePileList(devicePileList).stream().collect(Collectors.groupingBy(DeviceDto::getDeviceId));
        }
        Map<String, List<DeviceDto>> finalDeviceMap = deviceMap;
        if (CollectionUtils.isNotEmpty(deviceAcList)) {
            DeviceOverviewDto device = new DeviceOverviewDto();
            device.setTypeId("28");
            device.setTypeName("交流充电枪");
            if (MapUtils.isNotEmpty(finalDeviceMap)) {
                device.setDeviceList(deviceAcList.stream().filter(d -> finalDeviceMap.containsKey(d.getId()))
                        .flatMap(d -> finalDeviceMap.get(d.getId()).stream()).collect(Collectors.toList()));
            }
            resultList.add(device);
        }
        if (CollectionUtils.isNotEmpty(deviceDcList)) {
            DeviceOverviewDto device = new DeviceOverviewDto();
            device.setTypeId("29");
            device.setTypeName("直流充电枪");
            if (MapUtils.isNotEmpty(finalDeviceMap)) {
                device.setDeviceList(deviceDcList.stream().filter(d -> finalDeviceMap.containsKey(d.getId()))
                        .flatMap(d -> finalDeviceMap.get(d.getId()).stream()).collect(Collectors.toList()));
            }
            resultList.add(device);
        }
        if (CollectionUtils.isNotEmpty(deviceV2GList)) {
            DeviceOverviewDto device = new DeviceOverviewDto();
            device.setTypeId("30");
            device.setTypeName("V2G充电枪");
            if (MapUtils.isNotEmpty(finalDeviceMap)) {
                device.setDeviceList(deviceV2GList.stream().filter(d -> finalDeviceMap.containsKey(d.getId()))
                        .flatMap(d -> finalDeviceMap.get(d.getId()).stream()).collect(Collectors.toList()));
            }
            resultList.add(device);
        }

        List<DeviceBasicInfoDto> deviceSuperList = deviceList.stream().filter(d -> Objects.equals(d.getTypeId(), "79"))
                .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(deviceSuperList)) {
            DeviceOverviewDto device = new DeviceOverviewDto();
            device.setTypeId("79");
            device.setTypeName("超充充电枪");
            device.setDeviceList(getDeviceSuperPileList(deviceSuperList));
            resultList.add(device);
        }
        return resultList;
    }

    //获取换电概览数据
    private List<DeviceOverviewDto> getUseOverviewList(List<DeviceBasicInfoDto> deviceList) {
        List<DeviceOverviewDto> resultList = Lists.newArrayList();
        //获取换电舱设备数据
        List<DeviceBasicInfoDto> deviceGranaryList = deviceList.stream().filter(d -> Objects.equals(d.getTypeId(), "70"))
                .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(deviceGranaryList)) {
            DeviceOverviewDto device = new DeviceOverviewDto();
            device.setTypeId("70");
            device.setTypeName("换电仓");
            device.setDeviceList(this.getDeviceGranaryList(deviceGranaryList));
            resultList.add(device);
        }
        //获取温度监测(传感器)设备数据
        List<DeviceBasicInfoDto> deviceTempList = deviceList.stream().filter(d -> Objects.equals(d.getTypeId(), "36"))
                .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(deviceTempList)) {
            DeviceOverviewDto device = new DeviceOverviewDto();
            device.setTypeId("36");
            device.setTypeName("温度监测");
            device.setDeviceList(this.getDeviceTempList(deviceTempList));
            resultList.add(device);
        }
        return resultList;
    }

    //获取光伏逆变器设备概览数据
    private List<DeviceDto> getDeviceInverterList(List<DeviceBasicInfoDto> deviceInfoList) {
        //获取光伏逆变器今日发电量
        Set<String> inverterIds = deviceInfoList.stream().map(DeviceBasicInfoDto::getId).collect(Collectors.toSet());
        DeviceHistoryQueryVo dayQtQueryVo = new DeviceHistoryQueryVo();
        dayQtQueryVo.setDeviceIds(inverterIds);
        dayQtQueryVo.setFunctionLogos(Collections.singleton(FunctionLogoParamVo.TOTAL_POWER_GENERATION));
        dayQtQueryVo.setStartTime(DateUtil.getDayStart(DateUtil.localDateToStr(LocalDate.now())));
        dayQtQueryVo.setEndTime(DateUtil.localDateTimeToStr(LocalDateTime.now()));
        dayQtQueryVo.setTimeInterval("1d");
        Map<String, Map<String, List<NodeDifHistoryDto>>> deviceDayQtMap = dataService.findNodeDifHistoryListFeign(dayQtQueryVo).getData();

        return deviceInfoList.stream().map(deviceInfo -> {
            DeviceDto result = new DeviceDto();
            result.setDeviceId(deviceInfo.getId());
            result.setDeviceName(deviceInfo.getDeviceName());
            result.setDeviceStatus(deviceInfo.getTxStatus());
            result.setDeviceStatusName(ExtraValueUtil.getTxStatus(deviceInfo.getTxStatus()));

            List<DeviceDto.DataListDto> dataList = Lists.newArrayList();
            //今日发电量
            DeviceDto.DataListDto data1 = new DeviceDto.DataListDto();
            data1.setName("今日发电量(kWh)");
            data1.setValue(0.0);
            if (deviceDayQtMap.containsKey(deviceInfo.getId()) && deviceDayQtMap.get(deviceInfo.getId()).containsKey(FunctionLogoParamVo.TOTAL_POWER_GENERATION)) {
                List<NodeDifHistoryDto> nodeDifList = deviceDayQtMap.get(deviceInfo.getId()).get(FunctionLogoParamVo.TOTAL_POWER_GENERATION);
                if (CollectionUtils.isNotEmpty(nodeDifList)) {
                    data1.setValue(DoubleUtil.getToDouble(nodeDifList.stream().mapToDouble(d -> DoubleUtil
                            .getObjSub(d.getFirstDataValue(), d.getLastDataValue())).sum()));
                }
            }
            //今日等效发电时长 今日发电量/额定功率
            double ratedPower = 0.0;
            if (deviceInfo.getReaMap().containsKey(ReaFieldParamVo.RATED_POWER) && StringUtil.isNotEmpty(deviceInfo.getReaMap().get(ReaFieldParamVo.RATED_POWER))) {
                ratedPower = Double.parseDouble(String.valueOf(deviceInfo.getReaMap().get(ReaFieldParamVo.RATED_POWER)));
            }
            DeviceDto.DataListDto data2 = new DeviceDto.DataListDto();
            data2.setName("等效发电时长(h)");
            data2.setValue(0.0);
            if (ratedPower != 0.0) {
                data2.setValue(DoubleUtil.getToDouble(Double.parseDouble(String.valueOf(data1.getValue())) / ratedPower));
            }
            dataList.add(data1);
            dataList.add(data2);
            result.setDataList(dataList);
            return result;
        }).collect(Collectors.toList());
    }

    //获取光伏气象站设备概览数据
    private List<DeviceDto> getDeviceStationList(List<DeviceBasicInfoDto> deviceInfoList) {
        //获取光伏气象站的设备id
        Set<String> stationIds = deviceInfoList.stream().map(DeviceBasicInfoDto::getId).collect(Collectors.toSet());
        String functionLogos = String.join(FileUtil.COMMA, FunctionLogoParamVo.PV_TEMPERATURE, FunctionLogoParamVo.PV_WIND_SPEED);
        Map<String, Map<String, RealDataModel>> deviceRealDataMap = deviceService.getDeviceFunctionsRealDataByIds(stationIds, functionLogos, 2).getData();
        return deviceInfoList.stream().map(deviceInfo -> {
            DeviceDto result = new DeviceDto();
            result.setDeviceId(deviceInfo.getId());
            result.setDeviceName(deviceInfo.getDeviceName());
            result.setDeviceStatus(deviceInfo.getTxStatus());
            result.setDeviceStatusName(ExtraValueUtil.getTxStatus(deviceInfo.getTxStatus()));

            List<DeviceDto.DataListDto> dataList = Lists.newArrayList();

            DeviceDto.DataListDto data1 = new DeviceDto.DataListDto();
            data1.setName("温度(℃)");
            DeviceDto.DataListDto data2 = new DeviceDto.DataListDto();
            data2.setName("风速(m/s)");

            if (deviceRealDataMap.containsKey(deviceInfo.getId())) {
                Map<String, RealDataModel> realDataMap = deviceRealDataMap.get(deviceInfo.getId());
                if (realDataMap.containsKey(FunctionLogoParamVo.PV_TEMPERATURE)) {
                    data1.setValue(realDataMap.get(FunctionLogoParamVo.PV_TEMPERATURE).getDataValue());
                }
                if (realDataMap.containsKey(FunctionLogoParamVo.PV_WIND_SPEED)) {
                    data2.setValue(realDataMap.get(FunctionLogoParamVo.PV_WIND_SPEED).getDataValue());
                }
            }
            dataList.add(data1);
            dataList.add(data2);
            result.setDataList(dataList);
            return result;
        }).collect(Collectors.toList());
    }

    //获取储能PCS设备概览数据
    private List<DeviceDto> getDevicePcsList(List<DeviceBasicInfoDto> deviceInfoList) {
        //获取储能PCS设备id
        List<String> pcsIds = deviceInfoList.stream().map(DeviceBasicInfoDto::getId).collect(Collectors.toList());
        Set<String> functionLogos = new HashSet<>(Arrays.asList(FunctionLogoParamVo.PCS_BATTERY_CHARGE, FunctionLogoParamVo.PCS_BATTERY_DISCHARGE));
        DeviceHistoryQueryVo deviceHistoryQueryVo = new DeviceHistoryQueryVo();
        deviceHistoryQueryVo.setDeviceIds(new HashSet<>(pcsIds));
        deviceHistoryQueryVo.setFunctionLogos(functionLogos);
        deviceHistoryQueryVo.setTimeInterval("1d");
        deviceHistoryQueryVo.setStartTime(DateUtil.getDayStart(DateUtil.localDateToStr(LocalDate.now())));
        deviceHistoryQueryVo.setEndTime(DateUtil.localDateTimeToStr(LocalDateTime.now()));
        //充电量、放电量
        Map<String, Map<String, List<NodeDifHistoryDto>>> nodeDifHistoryMap = dataService.findNodeDifHistoryListFeign(deviceHistoryQueryVo).getData();

        return deviceInfoList.stream().map(deviceInfo -> {
            DeviceDto result = new DeviceDto();
            result.setDeviceId(deviceInfo.getId());
            result.setDeviceName(deviceInfo.getDeviceName());
            result.setDeviceStatus(deviceInfo.getTxStatus());
            result.setDeviceStatusName(ExtraValueUtil.getTxStatus(deviceInfo.getTxStatus()));

            List<DeviceDto.DataListDto> dataList = Lists.newArrayList();

            DeviceDto.DataListDto data1 = new DeviceDto.DataListDto();
            data1.setName("今日充电量(kWh)");
            DeviceDto.DataListDto data2 = new DeviceDto.DataListDto();
            data2.setName("今日放电量(kWh)");

            if (nodeDifHistoryMap.containsKey(deviceInfo.getId())) {
                Map<String, List<NodeDifHistoryDto>> historyDataMap = nodeDifHistoryMap.get(deviceInfo.getId());
                if (historyDataMap.containsKey(FunctionLogoParamVo.PCS_BATTERY_CHARGE) && CollectionUtils.isNotEmpty(historyDataMap.get(FunctionLogoParamVo.PCS_BATTERY_CHARGE))) {
                    NodeDifHistoryDto nodeDifHistory = historyDataMap.get(FunctionLogoParamVo.PCS_BATTERY_CHARGE).get(0);
                    if (StringUtil.isNotEmpty(nodeDifHistory.getFirstDataValue()) && StringUtil.isNotEmpty(nodeDifHistory.getLastDataValue())) {
                        data1.setValue(DoubleUtil.getToDouble((double) nodeDifHistory.getLastDataValue() - (double) nodeDifHistory.getFirstDataValue()));
                    }
                }
                if (historyDataMap.containsKey(FunctionLogoParamVo.PCS_BATTERY_DISCHARGE) && CollectionUtils.isNotEmpty(historyDataMap.get(FunctionLogoParamVo.PCS_BATTERY_DISCHARGE))) {
                    NodeDifHistoryDto nodeDifHistory = historyDataMap.get(FunctionLogoParamVo.PCS_BATTERY_DISCHARGE).get(0);
                    if (StringUtil.isNotEmpty(nodeDifHistory.getFirstDataValue()) && StringUtil.isNotEmpty(nodeDifHistory.getLastDataValue())) {
                        data2.setValue(DoubleUtil.getToDouble((double) nodeDifHistory.getLastDataValue() - (double) nodeDifHistory.getFirstDataValue()));
                    }
                }
            }
            dataList.add(data1);
            dataList.add(data2);
            result.setDataList(dataList);
            return result;
        }).collect(Collectors.toList());
    }

    //获取储能电池簇设备概览数据
    private List<DeviceDto> getDeviceBatteryList(List<DeviceBasicInfoDto> deviceInfoList) {

        //获取储能电池簇的设备id
        Set<String> batteryIds = deviceInfoList.stream().map(DeviceBasicInfoDto::getId).collect(Collectors.toSet());
        String functionLogos = String.join(FileUtil.COMMA, FunctionLogoParamVo.BATTERY_RUN_STATE, FunctionLogoParamVo.BATTERY_TOTAL_SOC,
                FunctionLogoParamVo.BATTERY_TOTAL_SOH);
        Map<String, Map<String, RealDataModel>> deviceRealDataMap = deviceService.getDeviceFunctionsRealDataByIds(batteryIds, functionLogos, 2).getData();

        //根据多个电池蔟id查询电池簇的运行状态枚举值数据
        Map<String, Map<String, ModelFunctionListDto>> modelFunctionMap = deviceService.findDeviceFunctionListByIds(new ArrayList<>(batteryIds))
                .getData().entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().stream()
                        .filter(m -> Objects.equals(FunctionLogoParamVo.BATTERY_RUN_STATE, m.getFunctionLogo())).collect(Collectors
                                .toMap(ModelFunctionListDto::getFunctionLogo, Function.identity(), (k1, k2) -> k1))));

        return deviceInfoList.stream().map(deviceInfo -> {
            DeviceDto result = new DeviceDto();
            result.setDeviceId(deviceInfo.getId());
            result.setDeviceName(deviceInfo.getDeviceName());

            List<DeviceDto.DataListDto> dataList = Lists.newArrayList();

            DeviceDto.DataListDto data1 = new DeviceDto.DataListDto();
            data1.setName("SOC(%)");
            DeviceDto.DataListDto data2 = new DeviceDto.DataListDto();
            data2.setName("SOH(%)");

            if (deviceRealDataMap.containsKey(deviceInfo.getId())) {
                Map<String, RealDataModel> realDataMap = deviceRealDataMap.get(deviceInfo.getId());
                if (realDataMap.containsKey(FunctionLogoParamVo.BATTERY_RUN_STATE)) {
                    RealDataModel realDataModel = realDataMap.get(FunctionLogoParamVo.BATTERY_RUN_STATE);
                    if (StringUtil.isNotEmpty(realDataModel.getDataValue())) {
                        result.setDeviceStatus((Integer) realDataModel.getDataValue());
                        //获取电池簇设备状态的中文名称
                        if (modelFunctionMap.containsKey(deviceInfo.getId()) && modelFunctionMap.get(deviceInfo.getId()).containsKey(FunctionLogoParamVo.BATTERY_RUN_STATE)) {
                            ModelFunctionListDto modelFunction = modelFunctionMap.get(deviceInfo.getId()).get(FunctionLogoParamVo.BATTERY_RUN_STATE);
                            if (StringUtil.isNotEmpty(modelFunction.getDataObject())) {
                                JSONArray enumArray = JSON.parseObject(modelFunction.getDataObject()).getJSONArray("enumArray");
                                if (CollectionUtils.isNotEmpty(enumArray)) {
                                    for (Object enumObj : enumArray) {
                                        JSONObject enumDto = JSON.parseObject(String.valueOf(enumObj));
                                        if (enumDto != null && StringUtil.isNotEmpty(enumDto.get("id")) && Objects.equals(enumDto.getString("id"), String.valueOf(realDataModel.getDataValue()))) {
                                            result.setDeviceStatusName(enumDto.getString("name"));
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        result.setDeviceStatus(-1);
                    }
                    if (StringUtil.isEmpty(result.getDeviceStatusName())) {
                        result.setDeviceStatusName("未知");
                    }
                }
                if (realDataMap.containsKey(FunctionLogoParamVo.BATTERY_TOTAL_SOC)) {
                    data1.setValue(realDataMap.get(FunctionLogoParamVo.BATTERY_TOTAL_SOC).getDataValue());
                }
                if (realDataMap.containsKey(FunctionLogoParamVo.BATTERY_TOTAL_SOH)) {
                    data2.setValue(realDataMap.get(FunctionLogoParamVo.BATTERY_TOTAL_SOH).getDataValue());
                }
            }
            dataList.add(data1);
            dataList.add(data2);
            result.setDataList(dataList);
            return result;
        }).collect(Collectors.toList());
    }

    //获取储能辅助设备概览数据
    private List<DeviceDto> getDeviceAuxiliaryList(List<DeviceBasicInfoDto> deviceInfoList) {
        //获取储能电池簇的辅助设备id
        Set<String> auxiliaryIds = deviceInfoList.stream().map(DeviceBasicInfoDto::getId).collect(Collectors.toSet());
        String functionLogos = String.join(FileUtil.COMMA, FunctionLogoParamVo.CABINET_TEMPERATURE, FunctionLogoParamVo.CABINET_HUMIDITY);
        Map<String, Map<String, RealDataModel>> deviceRealDataMap = deviceService.getDeviceFunctionsRealDataByIds(auxiliaryIds, functionLogos, 2).getData();

        return deviceInfoList.stream().map(deviceInfo -> {
            DeviceDto result = new DeviceDto();
            result.setDeviceId(deviceInfo.getId());
            result.setDeviceName(deviceInfo.getDeviceName());
            result.setDeviceStatus(deviceInfo.getTxStatus());
            result.setDeviceStatusName(ExtraValueUtil.getTxStatus(deviceInfo.getTxStatus()));

            List<DeviceDto.DataListDto> dataList = Lists.newArrayList();

            DeviceDto.DataListDto data1 = new DeviceDto.DataListDto();
            data1.setName("柜内温度(°C)");
            DeviceDto.DataListDto data2 = new DeviceDto.DataListDto();
            data2.setName("柜内湿度(%)");

            if (deviceRealDataMap.containsKey(deviceInfo.getId())) {
                Map<String, RealDataModel> realDataMap = deviceRealDataMap.get(deviceInfo.getId());
                if (realDataMap.containsKey(FunctionLogoParamVo.CABINET_TEMPERATURE)) {
                    data1.setValue(realDataMap.get(FunctionLogoParamVo.CABINET_TEMPERATURE).getDataValue());
                }
                if (realDataMap.containsKey(FunctionLogoParamVo.CABINET_HUMIDITY)) {
                    data2.setValue(realDataMap.get(FunctionLogoParamVo.CABINET_HUMIDITY).getDataValue());
                }
            }
            dataList.add(data1);
            dataList.add(data2);
            result.setDataList(dataList);
            return result;
        }).collect(Collectors.toList());
    }

    //获取充电枪概览数据
    private List<DeviceDto> getDevicePileList(List<DeviceBasicInfoDto> deviceInfoList) {
        List<DeviceDto> resultList = Lists.newArrayList();
        //获取电桩设备id
        List<String> deviceIds = deviceInfoList.stream().map(DeviceBasicInfoDto::getId).collect(Collectors.toList());
        Map<String, List<PileGunMonitorDataDto>> pileGunMonitorMap = webFeignService.findAllPileGunMonitorList(deviceIds).getData();
        for (DeviceBasicInfoDto deviceInfo : deviceInfoList) {
            if (pileGunMonitorMap.containsKey(deviceInfo.getId())) {
                resultList.addAll(pileGunMonitorMap.get(deviceInfo.getId()).stream().map(pileGun -> {
                    DeviceDto result = new DeviceDto();
                    result.setDeviceId(deviceInfo.getId());
                    result.setDeviceName(pileGun.getPileCode() + FileUtil.BAR + pileGun.getGunCode());
                    result.setDeviceStatus(pileGun.getGunWorkState());
                    result.setDeviceStatusName(ExtraValueUtil.getPileGunStatus(pileGun.getGunWorkState()));

                    List<DeviceDto.DataListDto> dataList = Lists.newArrayList();
                    DeviceDto.DataListDto data1 = new DeviceDto.DataListDto();
                    data1.setName("今日充电量(kWh)");
                    data1.setValue(pileGun.getDayChargeQt());
                    DeviceDto.DataListDto data2 = new DeviceDto.DataListDto();
                    data2.setName("今日放电量(kWh)");
                    data2.setValue(pileGun.getDayV2gQt());
                    dataList.add(data1);
                    dataList.add(data2);
                    result.setDataList(dataList);
                    return result;
                }).collect(Collectors.toList()));
            }
        }
        return resultList;
    }

    //获取充电枪概览数据
    private List<DeviceDto> getDeviceSuperPileList(List<DeviceBasicInfoDto> deviceInfoList) {
        List<DeviceDto> resultList = Lists.newArrayList();
        //获取电桩设备id
        Set<String> deviceIds = deviceInfoList.stream().map(DeviceBasicInfoDto::getId).collect(Collectors.toSet());
        Map<String, Map<String, RealDataModel>> realDataMap = deviceService.getDeviceFunctionsRealDataByIds(deviceIds,
                String.join(FileUtil.COMMA, FunctionLogoParamVo.GUN_ORIGINAL_STATUS, FunctionLogoParamVo.VEHICLE_CONN_STATE), 2).getData();
        for (DeviceBasicInfoDto deviceInfo : deviceInfoList) {
            DeviceDto result = new DeviceDto();
            result.setDeviceId(deviceInfo.getId());
            result.setDeviceName(deviceInfo.getDeviceName());
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
                ExtraValueUtil.getSuperPileGunStatus(deviceInfo.getTxStatus(), gunState, vehicleState, result);
            }
            List<DeviceDto.DataListDto> dataList = Lists.newArrayList();
            DeviceDto.DataListDto data1 = new DeviceDto.DataListDto();
            data1.setName("今日充电量(kWh)");
            DeviceDto.DataListDto data2 = new DeviceDto.DataListDto();
            data2.setName("今日放电量(kWh)");
            dataList.add(data1);
            dataList.add(data2);
            result.setDataList(dataList);
            resultList.add(result);
        }
        return resultList;
    }

    //获取换电仓设备数据
    private List<DeviceDto> getDeviceGranaryList(List<DeviceBasicInfoDto> deviceInfoList) {

        //获取换电仓设备id
        Set<String> tempIds = deviceInfoList.stream().map(DeviceBasicInfoDto::getId).collect(Collectors.toSet());
        String functionLogos = String.join(FileUtil.COMMA, FunctionLogoParamVo.BATTERY_GRANARY_STATUS, FunctionLogoParamVo.BATTERY_PACK_SOC,
                FunctionLogoParamVo.BATTERY_PACK_SOH);
        Map<String, Map<String, RealDataModel>> deviceRealDataMap = deviceService.getDeviceFunctionsRealDataByIds(tempIds, functionLogos, 2).getData();

        return deviceInfoList.stream().map(deviceInfo -> {
            DeviceDto result = new DeviceDto();
            result.setDeviceId(deviceInfo.getId());
            result.setDeviceName(deviceInfo.getDeviceName());
            result.setDeviceStatus(deviceInfo.getTxStatus());
            result.setDeviceStatusName(ExtraValueUtil.getTxStatus(deviceInfo.getTxStatus()));

            List<DeviceDto.DataListDto> dataList = Lists.newArrayList();

            DeviceDto.DataListDto data1 = new DeviceDto.DataListDto();
            data1.setName("SOC(%)");
            DeviceDto.DataListDto data2 = new DeviceDto.DataListDto();
            data2.setName("SOH(%)");

            if (deviceRealDataMap.containsKey(deviceInfo.getId())) {
                Map<String, RealDataModel> realDataMap = deviceRealDataMap.get(deviceInfo.getId());
                if (realDataMap.containsKey(FunctionLogoParamVo.BATTERY_GRANARY_STATUS)) {
                    RealDataModel realDataModel = realDataMap.get(FunctionLogoParamVo.BATTERY_GRANARY_STATUS);
                    if (StringUtil.isNotEmpty(realDataModel.getDataValue())) {
                        result.setDeviceStatus(Integer.parseInt(String.valueOf(realDataModel.getDataValue())));
                    } else {
                        result.setDeviceStatus(-1);
                    }
                }
                if (realDataMap.containsKey(FunctionLogoParamVo.BATTERY_PACK_SOC)) {
                    data1.setValue(realDataMap.get(FunctionLogoParamVo.BATTERY_PACK_SOC).getDataValue());
                }
                if (realDataMap.containsKey(FunctionLogoParamVo.BATTERY_PACK_SOH)) {
                    data2.setValue(realDataMap.get(FunctionLogoParamVo.BATTERY_PACK_SOH).getDataValue());
                }
            }
            dataList.add(data1);
            dataList.add(data2);
            result.setDataList(dataList);
            return result;
        }).collect(Collectors.toList());
    }

    //获取温度监测(传感器)设备数据
    private List<DeviceDto> getDeviceTempList(List<DeviceBasicInfoDto> deviceInfoList) {
        //获取传感器设备id
        Set<String> tempIds = deviceInfoList.stream().map(DeviceBasicInfoDto::getId).collect(Collectors.toSet());
        String functionLogos = String.join(FileUtil.COMMA, FunctionLogoParamVo.SENSOR_MAX_TEMP, FunctionLogoParamVo.SENSOR_AVG_TEMP);
        Map<String, Map<String, RealDataModel>> deviceRealDataMap = deviceService.getDeviceFunctionsRealDataByIds(tempIds, functionLogos, 2).getData();

        return deviceInfoList.stream().map(deviceInfo -> {
            DeviceDto result = new DeviceDto();
            result.setDeviceId(deviceInfo.getId());
            result.setDeviceName(deviceInfo.getDeviceName());
            result.setDeviceStatus(deviceInfo.getTxStatus());
            result.setDeviceStatusName(ExtraValueUtil.getTxStatus(deviceInfo.getTxStatus()));

            List<DeviceDto.DataListDto> dataList = Lists.newArrayList();

            DeviceDto.DataListDto data1 = new DeviceDto.DataListDto();
            data1.setName("最高温度(°C)");
            DeviceDto.DataListDto data2 = new DeviceDto.DataListDto();
            data2.setName("平均温度(°C)");

            if (deviceRealDataMap.containsKey(deviceInfo.getId())) {
                Map<String, RealDataModel> realDataMap = deviceRealDataMap.get(deviceInfo.getId());
                if (realDataMap.containsKey(FunctionLogoParamVo.SENSOR_MAX_TEMP)) {
                    data1.setValue(realDataMap.get(FunctionLogoParamVo.SENSOR_MAX_TEMP).getDataValue());
                }
                if (realDataMap.containsKey(FunctionLogoParamVo.SENSOR_AVG_TEMP)) {
                    data2.setValue(realDataMap.get(FunctionLogoParamVo.SENSOR_AVG_TEMP).getDataValue());
                }
            }
            dataList.add(data1);
            dataList.add(data2);
            result.setDataList(dataList);
            return result;
        }).collect(Collectors.toList());
    }

    /**
     * 获取子节点数据
     */
    public static List<SiteTopNodeDto> setChild(String parentId, List<SiteTopNodeDto> childrenList) {
        //返回的集合
        List<SiteTopNodeDto> children = Lists.newArrayList();
        children.addAll(childrenList.stream().filter(c -> c.getId().equals(parentId)).collect(Collectors.toList()));
        children.addAll(getChildren(parentId, childrenList));
        return children;
    }

    private static List<SiteTopNodeDto> getChildren(String id, List<SiteTopNodeDto> childrenList) {
        List<SiteTopNodeDto> result = Lists.newArrayList();
        for (SiteTopNodeDto menu : childrenList) {
            if (id.equals(menu.getParentId())) {
                result.add(menu);
                //childrenList.remove(menu);
                result.addAll(getChildren(menu.getId(), childrenList));
            }
        }
        return result;
    }


}
