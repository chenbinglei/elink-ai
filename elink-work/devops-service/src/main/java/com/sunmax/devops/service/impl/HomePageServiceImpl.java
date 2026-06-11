package com.sunmax.devops.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.sunmax.common.dto.data.NodeDifHistoryDto;
import com.sunmax.common.dto.device.DeviceBasicInfoDto;
import com.sunmax.common.dto.device.SiteInfoDto;
import com.sunmax.common.dto.device.SiteScenarioTypeDto;
import com.sunmax.common.dto.device.SiteSetUpDto;
import com.sunmax.common.dto.operate.OrderCountDto;
import com.sunmax.common.dto.operate.OrderQtDto;
import com.sunmax.common.dto.system.OrganEmpowerListDto;
import com.sunmax.common.dto.system.UserDto;
import com.sunmax.common.model.RealDataModel;
import com.sunmax.common.util.DateUtil;
import com.sunmax.common.util.DoubleUtil;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.util.StringUtil;
import com.sunmax.common.util.oss.FileUtil;
import com.sunmax.common.vo.FunctionLogoParamVo;
import com.sunmax.common.vo.ReaFieldParamVo;
import com.sunmax.common.vo.SiteFieldParamVo;
import com.sunmax.common.vo.data.DeviceHistoryQueryVo;
import com.sunmax.devops.dto.*;
import com.sunmax.devops.service.HomePageService;
import com.sunmax.devops.service.feign.DataService;
import com.sunmax.devops.service.feign.DeviceService;
import com.sunmax.devops.service.feign.SystemService;
import com.sunmax.devops.service.feign.TogetherService;
import com.sunmax.devops.util.DeviceCommonUtil;
import com.sunmax.devops.vo.EnergyQueryVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.compress.utils.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static com.sunmax.common.util.DateUtil.*;

@Service
@Slf4j
public class HomePageServiceImpl implements HomePageService {

    @Autowired
    private SystemService systemService;

    @Autowired
    private DeviceService deviceService;

    @Autowired
    private DataService dataService;

    @Autowired
    private TogetherService togetherService;

    @Override
    public ResponseResult<List<TenantSiteDto>> getTenantSiteList(String userId) {
        //返回的集合
        List<TenantSiteDto> resultList = Lists.newArrayList();
        //根据用户id查询用户信息
        UserDto user = systemService.findUserInfoByIdsFeign(Collections.singletonList(userId)).getData().get(userId);
        if (user != null && StringUtil.isNotEmpty(user.getTenantId()) && StringUtil.isNotEmpty(user.getTenantName())) {
            TenantSiteDto parent = new TenantSiteDto();
            parent.setId(user.getTenantId());
            parent.setName(user.getTenantName());
            parent.setParentId("0");
            parent.setType(1);
            resultList.add(parent);

            //根据用户id查询关联站点数据
            List<OrganEmpowerListDto> organEmpowerList = systemService.findAllOrganEmpowerByUserId(userId).getData();
            if (CollectionUtils.isNotEmpty(organEmpowerList)) {
                //根据多个站点id查询站点位置
                List<String> siteIds = organEmpowerList.stream().map(OrganEmpowerListDto::getSiteId).distinct().collect(Collectors.toList());
                Map<String, SiteInfoDto> siteInfoMap = deviceService.findSiteBasicInfoByIds(siteIds).getData();
                resultList.addAll(siteInfoMap.values().stream().map(item -> {
                    //获取站点位置信息
                    TenantSiteDto child = new TenantSiteDto();
                    child.setId(item.getId());
                    child.setName(item.getSiteName());
                    child.setParentId(user.getTenantId());
                    child.setType(2);
                    child.setScenarioTypes(item.getScenarioTypes());
                    String siteReadwriteObject = item.getSiteReadwriteObject();
                    if (StringUtil.isNotEmpty(siteReadwriteObject)) {
                        JSONObject parseObjectMap = JSON.parseObject(siteReadwriteObject);
                        //获取站点位置信息
                        if (parseObjectMap.containsKey(SiteFieldParamVo.LOCATION)) {
                            child.setLocation(parseObjectMap.getString(SiteFieldParamVo.LOCATION));
                        }
                    }
                    child.setCreateTime(DateUtil.localDateTimeToStr(item.getCreateTime()));
                    return child;
                }).collect(Collectors.toList()));
            }
        }
        return ResponseResult.ok(resultList);
    }

    @Override
    public ResponseResult<DeviceCapDto> getDeviceCap(String siteIds) {
        DeviceCapDto result = new DeviceCapDto();
        if (StringUtil.isEmpty(siteIds)) {
            return ResponseResult.error(ResponseResult.PARAM_ERROR);
        }
        //根据多个站点id查询系统容量
        List<String> systemIds = Lists.newArrayList();
        Map<String, SiteInfoDto> siteInfoMap = deviceService.findSiteBasicInfoByIds(JSON.parseArray(siteIds, String.class)).getData();
        if (MapUtils.isEmpty(siteInfoMap)) {
            return ResponseResult.ok(result);
        }
        List<Integer> scenarioTypes = Arrays.asList(1, 2, 3);
        siteInfoMap.values().stream().filter(s -> CollectionUtils.isNotEmpty(s.getSiteScenarioTypeDtos()))
                .flatMap(s -> s.getSiteScenarioTypeDtos().stream().filter(d -> StringUtil.isNotEmpty(d.getScenarioType())
                        && scenarioTypes.contains(d.getScenarioType()))).forEach(scenarioType -> {
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
                    //获取光伏,储能,电桩系统id
                    systemIds.add(scenarioType.getId());
                });
        //站点id -> 多个逆变器id
        Map<String, Set<String>> siteInverterMap = Maps.newHashMap();
        //定义充电桩设备类型
//        List<String> pileSystemTypes = Arrays.asList("28", "29", "30", "79");
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

            //充电桩系统额定功率
//            List<DeviceBasicInfoDto> systemPileList = deviceInfoList.stream().filter(d -> pileSystemTypes.contains(d.getTypeId()))
//                    .collect(Collectors.toList());
//            result.setPileCap(result.getPileCap() + systemPileList.stream().mapToDouble(pile -> {
//                if (pile.getReaMap().containsKey(ReaFieldParamVo.RATED_POWER) && StringUtil.isNotEmpty(pile.getReaMap().get(ReaFieldParamVo.RATED_POWER))) {
//                    return Double.parseDouble(String.valueOf(pile.getReaMap().get(ReaFieldParamVo.RATED_POWER)));
//                }
//                return 0.0;
//            }).sum());

            //多个逆变器id
            deviceInfoList.stream().filter(d -> d.getTypeId().equals("20")).forEach(inverter -> {
                Set<String> inverterIds;
                if (siteInverterMap.containsKey(inverter.getSiteId())) {
                    inverterIds = siteInverterMap.get(inverter.getSiteId());
                } else {
                    inverterIds = Sets.newHashSet();
                }
                inverterIds.add(inverter.getId());
                siteInverterMap.put(inverter.getSiteId(), inverterIds);
            });
        });

        //计算光伏系统的社会贡献
        //根据多个站点id查询站点设置参数
        Map<String, SiteSetUpDto> siteSetUpMap = deviceService.findSiteSetUpBySiteIds(new ArrayList<>(siteInfoMap.keySet())).getData();
        //根据多个逆变器id查询逆变器发电量
        Map<String, Map<String, RealDataModel>> inverterDataMap = deviceService.getDeviceFunctionsRealDataByIds(siteInverterMap.values()
                .stream().flatMap(Collection::stream).collect(Collectors.toSet()), FunctionLogoParamVo.TOTAL_POWER_GENERATION).getData();
        siteInverterMap.forEach((siteId, inverterIds) -> {
            double totalQt = inverterIds.stream().mapToDouble(inverterId -> {
                if (inverterDataMap.containsKey(inverterId) && inverterDataMap.get(inverterId).containsKey(FunctionLogoParamVo.TOTAL_POWER_GENERATION)) {
                    RealDataModel realDataModel = inverterDataMap.get(inverterId).get(FunctionLogoParamVo.TOTAL_POWER_GENERATION);
                    if (realDataModel != null && StringUtil.isNotEmpty(realDataModel.getDataValue())) {
                        return Double.parseDouble(String.valueOf(realDataModel.getDataValue()));
                    }
                }
                return 0.0;
            }).sum();
            if (siteSetUpMap.containsKey(siteId)) {
                SiteSetUpDto siteSetUp = siteSetUpMap.get(siteId);
                //二氧化碳减排量(电站发电量 * CO₂减排转换系数(0.475))
                Double co2Reduction = null;
                if (StringUtil.isNotEmpty(siteSetUp.getReduceCoeff())) {
                    co2Reduction = totalQt * siteSetUp.getReduceCoeff();
                    result.setCo2Reduction(result.getCo2Reduction() + co2Reduction);
                }
                //节约标煤量(电站发电量 * 节约标准煤转换系数(0.4))
                if (StringUtil.isNotEmpty(siteSetUp.getTceCoeff())) {
                    result.setStandardCoalReduction(result.getStandardCoalReduction() + (totalQt * siteSetUp.getTceCoeff()));
                }
                //等效植树量(二氧化碳减排量 / 等效植树量转换系数（18.3）/ 40)
                if (co2Reduction != null && siteSetUp.getTreeCoeff() != null && siteSetUp.getTreeCoeff() != 0.0) {
                    result.setTreeReduction(result.getTreeReduction() + (co2Reduction / siteSetUp.getTreeCoeff() / 40));
                }
            }
        });
        result.setPileCap(DoubleUtil.getToDouble(result.getPileCap()));
        result.setPcsPower(DoubleUtil.getToDouble(result.getPcsPower()));
        result.setBatteryCap(DoubleUtil.getToDouble(result.getBatteryCap()));
        result.setPvCap(DoubleUtil.getToDouble(result.getPvCap()));
        result.setCo2Reduction(DoubleUtil.getToDouble(result.getCo2Reduction()));
        result.setStandardCoalReduction(DoubleUtil.getToDouble(result.getStandardCoalReduction()));
        result.setTreeReduction(DoubleUtil.getToDouble(result.getTreeReduction()));
        return ResponseResult.ok(result);
    }

    @Override
    public ResponseResult<EnergyPileDto> findAllEnergyPile(EnergyQueryVo energyQueryVo) {
        //返回的对象
        EnergyPileDto result = new EnergyPileDto();
        //校验请求参数
        if (validateQueryParams(energyQueryVo)) {
            return ResponseResult.error(ResponseResult.PARAM_ERROR);
        }
        //根据多个站点id查询电桩系统id
        Map<String, SiteInfoDto> siteInfoMap = deviceService.findSiteBasicInfoByIds(JSON.parseArray(energyQueryVo.getSiteIds(), String.class)).getData();
        if (MapUtils.isEmpty(siteInfoMap)) {
            return ResponseResult.ok(result);
        }
        List<String> systemIds = siteInfoMap.values().stream().filter(s -> CollectionUtils.isNotEmpty(s.getSiteScenarioTypeDtos()))
                .flatMap(s -> s.getSiteScenarioTypeDtos().stream().filter(d -> StringUtil.isNotEmpty(d.getScenarioType())
                        && Objects.equals(d.getScenarioType(), 3))).map(SiteScenarioTypeDto::getId).collect(Collectors.toList());
        //获取站点充电站系统下面的设备id和桩编号
        //电桩id -> 电桩编号
        Map<String, String> pileIdCodeMap = Maps.newHashMap();
        //定义充电桩设备类型
        List<String> pileSystemTypes = Arrays.asList("28", "29", "30", "79");
        deviceService.findDeviceInfoByParentIds(systemIds).getData().forEach((systemId, deviceInfoList) -> {
            //充电桩系统设备id -> 电桩编号
            pileIdCodeMap.putAll(deviceInfoList.stream().filter(d -> StringUtil.isNotEmpty(d.getTypeId()) && pileSystemTypes.contains(d.getTypeId())
                    && StringUtil.isNotEmpty(d.getDeviceNumber())).collect(Collectors.toMap(DeviceBasicInfoDto::getId, DeviceBasicInfoDto::getDeviceNumber, (k1, k2) -> k1)));
        });
        if (MapUtils.isEmpty(pileIdCodeMap)) {
            return ResponseResult.ok(result);
        }
        //日期类型 1-日 2-月 3-年 4-总(开始时间传最早的站点创建时间,结束时间传当前时间)
        String startTime = energyQueryVo.getStartTime();
        String endTime = energyQueryVo.getEndTime();
        Integer dateType = null;
        switch (energyQueryVo.getDateType()) {
            case 1:
                //获取电桩功率曲线数据
                DeviceCommonUtil.getDevicePowerCurve(pileIdCodeMap.keySet(), startTime, endTime, FunctionLogoParamVo.PILE_POWER, dataService, result);
                //获取电桩充电量，电桩放电量,充电金额,充电次数
                Map<String, OrderCountDto> orderCountMap = togetherService.findOrderRecordListByPileCodes(new ArrayList<>(pileIdCodeMap.values()),
                        startTime, endTime).getData();
                if (MapUtils.isNotEmpty(orderCountMap)) {
                    result.setChargeQt(DoubleUtil.getToDouble(orderCountMap.values().stream().mapToDouble(OrderCountDto::getChargeQt).sum()));
                    result.setDischargeQt(DoubleUtil.getAbsDouble(orderCountMap.values().stream().mapToDouble(OrderCountDto::getDischargeQt).sum()));
                    result.setChargeMoney(DoubleUtil.getToBigDecimal(orderCountMap.values().stream().map(OrderCountDto::getChargeMoney).reduce(BigDecimal.ZERO, BigDecimal::add)));
                    result.setChargeCount(orderCountMap.values().stream().mapToInt(OrderCountDto::getChargeCount).sum());
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
        if (dateType != null) {
            Map<String, OrderQtDto> orderQtMap = togetherService.findOrderQtListByPileCodes(new ArrayList<>(pileIdCodeMap.values()),
                    startTime, endTime, dateType).getData();
            if (MapUtils.isNotEmpty(orderQtMap)) {
                String startDate = energyQueryVo.getStartTime().substring(0, 10);
                String endDate = energyQueryVo.getEndTime().substring(0, 10);
                List<String> timeList = DateUtil.getDateBetween(dateType, startDate, endDate);
                for (String dateTime : timeList) {
                    if (orderQtMap.containsKey(dateTime)) {
                        OrderQtDto orderQtDto = orderQtMap.get(dateTime);
                        result.setChargeQt(result.getChargeQt() + orderQtDto.getChargeQt());
                        result.setDischargeQt(result.getDischargeQt() + orderQtDto.getDischargeQt());
                        result.setChargeMoney(result.getChargeMoney().add(orderQtDto.getChargeMoney()));
                        result.setChargeCount(result.getChargeCount() + orderQtDto.getChargeCount());
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
            }
        }
        return ResponseResult.ok(result);
    }


    @Override
    public ResponseResult<EnergyStorageDto> findAllEnergyStorage(EnergyQueryVo energyQueryVo) {
        //返回的对象
        EnergyStorageDto result = new EnergyStorageDto();
        //校验请求参数
        if (validateQueryParams(energyQueryVo)) {
            return ResponseResult.error(ResponseResult.PARAM_ERROR);
        }
        //根据多个站点id查询储能系统id
        Map<String, SiteInfoDto> siteInfoMap = deviceService.findSiteBasicInfoByIds(JSON.parseArray(energyQueryVo.getSiteIds(), String.class)).getData();
        if (MapUtils.isEmpty(siteInfoMap)) {
            return ResponseResult.ok(result);
        }
        List<String> systemIds = siteInfoMap.values().stream().filter(s -> CollectionUtils.isNotEmpty(s.getSiteScenarioTypeDtos()))
                .flatMap(s -> s.getSiteScenarioTypeDtos().stream().filter(d -> StringUtil.isNotEmpty(d.getScenarioType())
                        && Objects.equals(d.getScenarioType(), 2))).map(SiteScenarioTypeDto::getId).collect(Collectors.toList());
        //获取站点储能系统下面的PCS设备id
        Set<String> deviceIds = Sets.newHashSet();
        deviceService.findDeviceInfoByParentIds(systemIds).getData().forEach((systemId, deviceInfoList) -> {
            //储能系统下面的PCS设备id
            deviceIds.addAll(deviceInfoList.stream().filter(d -> StringUtil.isNotEmpty(d.getTypeId())
                            && (Objects.equals(d.getTypeId(), "23") || Objects.equals(d.getTypeId(), "78")))
                    .map(DeviceBasicInfoDto::getId).collect(Collectors.toList()));
        });
        if (CollectionUtils.isEmpty(deviceIds)) {
            return ResponseResult.ok(result);
        }
        //日期类型 1-日 2-月 3-年 4-总(可以不传时间)
        String startTime = energyQueryVo.getStartTime();
        String endTime = energyQueryVo.getEndTime();
        Integer dateType = null;
        String timeInterval;
        switch (energyQueryVo.getDateType()) {
            case 1:
                //获取储能功率曲线数据
                DeviceCommonUtil.getDevicePowerCurve(deviceIds, startTime, endTime, FunctionLogoParamVo.PCS_ACTIVE_POWER, dataService, result);
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
        DeviceHistoryQueryVo deviceQuery = new DeviceHistoryQueryVo();
        deviceQuery.setDeviceIds(deviceIds);
        deviceQuery.setFunctionLogos(new HashSet<>(Arrays.asList(FunctionLogoParamVo.PCS_BATTERY_CHARGE, FunctionLogoParamVo.PCS_BATTERY_DISCHARGE)));
        deviceQuery.setStartTime(startTime);
        deviceQuery.setEndTime(endTime);
        deviceQuery.setTimeInterval(timeInterval);
        Map<String, Map<String, List<NodeDifHistoryDto>>> historyDataMap = dataService.findNodeDifHistoryListFeign(deviceQuery).getData();
        if (MapUtils.isNotEmpty(historyDataMap)) {
            if (dateType != null && StringUtil.isNotEmpty(dateType)) {
                String startDate = energyQueryVo.getStartTime().substring(0, 10);
                String endDate = energyQueryVo.getEndTime().substring(0, 10);
                List<String> timeList = DateUtil.getDateBetween(dateType, startDate, endDate);
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
            result.setChargeQt(DoubleUtil.getAbsDouble(result.getChargeQt()));
            result.setDischargeQt(DoubleUtil.getAbsDouble(result.getDischargeQt()));
        }
        return ResponseResult.ok(result);
    }

    @Override
    public ResponseResult<EnergyPvDto> findAllEnergyPv(EnergyQueryVo energyQueryVo) {
        //返回的对象
        EnergyPvDto result = new EnergyPvDto();
        //校验请求参数
        if (validateQueryParams(energyQueryVo)) {
            return ResponseResult.error(ResponseResult.PARAM_ERROR);
        }
        //根据多个站点id查询光伏系统id,光伏系统额定容量
        List<String> siteIds = JSON.parseArray(energyQueryVo.getSiteIds(), String.class);
        Map<String, SiteInfoDto> siteInfoMap = deviceService.findSiteBasicInfoByIds(siteIds).getData();
        if (MapUtils.isEmpty(siteInfoMap)) {
            return ResponseResult.ok(result);
        }
        final double[] siteCapacity = {0.0};
        List<String> systemIds = siteInfoMap.values().stream().filter(s -> CollectionUtils.isNotEmpty(s.getSiteScenarioTypeDtos()))
                .flatMap(s -> s.getSiteScenarioTypeDtos().stream().filter(d -> StringUtil.isNotEmpty(d.getScenarioType())
                        && Objects.equals(d.getScenarioType(), 1))).map(s -> {
                    if (StringUtil.isNotEmpty(s.getReadwriteObject())) {
                        JSONObject jsonObject = JSON.parseObject(s.getReadwriteObject());
                        if (jsonObject.containsKey(SiteFieldParamVo.PV_CAPACITY) && StringUtil.isNotEmpty(jsonObject.getString(SiteFieldParamVo.PV_CAPACITY))) {
                            siteCapacity[0] += jsonObject.getDouble(SiteFieldParamVo.PV_CAPACITY);
                        }
                    }
                    return s.getId();
                }).collect(Collectors.toList());
        //获取站点光伏系统下面的逆变器设备id，关口表设备id
        Set<String> inverterIds = Sets.newHashSet();
        Set<String> gatewayIds = Sets.newHashSet();
        //根据多个站点id查询关口表id
        Map<String, List<DeviceBasicInfoDto>> deviceInfoMap = deviceService.findDeviceBasicInfoBySiteIds(siteIds, null).getData();
        deviceInfoMap.forEach((siteId, deviceInfoList) -> {
            //光伏系统下面的关口表设备id
            gatewayIds.addAll(deviceInfoList.stream().filter(d -> StringUtil.isNotEmpty(d.getTypeId()) && Objects.equals(d.getTypeId(), "39"))
                    .map(DeviceBasicInfoDto::getId).collect(Collectors.toList()));
            //光伏系统下面的逆变器设备id
            inverterIds.addAll(deviceInfoList.stream().filter(d -> StringUtil.isNotEmpty(d.getTypeId())
                    && (Objects.equals(d.getTypeId(), "20") || Objects.equals(d.getTypeId(), "77"))
                    && StringUtil.isNotEmpty(d.getParentId()) && systemIds.contains(d.getParentId())).map(DeviceBasicInfoDto::getId).collect(Collectors.toList()));
        });
        if (CollectionUtils.isEmpty(inverterIds) || CollectionUtils.isEmpty(gatewayIds)) {
            return ResponseResult.ok(result);
        }
        //日期类型 1-日 2-月 3-年 4-总(可以不传时间)
        String startTime = energyQueryVo.getStartTime();
        String endTime = energyQueryVo.getEndTime();
        Integer dateType = null;
        String timeInterval;
        switch (energyQueryVo.getDateType()) {
            case 1:
                //获取光伏功率曲线数据
                DeviceCommonUtil.getDevicePowerCurve(inverterIds, startTime, endTime, FunctionLogoParamVo.ACTIVE_POWER, dataService, result);
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
        if (CollectionUtils.isNotEmpty(gatewayIds)) {
            //查询光伏逆变器发电量
            DeviceHistoryQueryVo inverterQuery = new DeviceHistoryQueryVo();
            inverterQuery.setDeviceIds(inverterIds);
            inverterQuery.setFunctionLogos(Collections.singleton(FunctionLogoParamVo.TOTAL_POWER_GENERATION));
            inverterQuery.setStartTime(startTime);
            inverterQuery.setEndTime(endTime);
            inverterQuery.setTimeInterval(timeInterval);
            Map<String, Map<String, List<NodeDifHistoryDto>>> inverterDataMap = dataService.findNodeDifHistoryListFeign(inverterQuery).getData();
            if (MapUtils.isNotEmpty(inverterDataMap)) {
                List<NodeDifHistoryDto> inverterDataList = inverterDataMap.values().stream().flatMap(s -> s.values()
                        .stream().flatMap(Collection::stream)).collect(Collectors.toList());
                if (dateType != null && StringUtil.isNotEmpty(dateType)) {
                    String startDate = energyQueryVo.getStartTime().substring(0, 10);
                    String endDate = energyQueryVo.getEndTime().substring(0, 10);
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
                //日等效发电时长
                if (siteCapacity[0] > 0) {
                    result.setEffectiveTime(DoubleUtil.getToDouble(result.getGenerateQt() / siteCapacity[0]));
                }
            }
        }
        if (CollectionUtils.isNotEmpty(gatewayIds)) {
            //获取光伏上网电量
            //查询光伏关口表反向有功电量
            DeviceHistoryQueryVo gatewayQuery = new DeviceHistoryQueryVo();
            gatewayQuery.setDeviceIds(gatewayIds);
            gatewayQuery.setFunctionLogos(Collections.singleton(FunctionLogoParamVo.TOTAL_INVERSE_ACTIVE_ENERGY));
            gatewayQuery.setStartTime(startTime);
            gatewayQuery.setEndTime(endTime);
            gatewayQuery.setTimeInterval(timeInterval);
            Map<String, Map<String, List<NodeDifHistoryDto>>> gatewayDataMap = dataService.findNodeDifHistoryListFeign(gatewayQuery).getData();
            if (MapUtils.isNotEmpty(gatewayDataMap)) {
                result.setNetQt(DoubleUtil.getAbsDouble(gatewayDataMap.values().stream().flatMap(s -> s.values().stream().flatMap(Collection::stream))
                        .mapToDouble(c -> DoubleUtil.getObjSub(c.getFirstDataValue(), c.getLastDataValue())).sum()));
            }
        }
        //获取光伏消纳电量(光伏发电量 - 上网电量)
        result.setConsumeQt(DoubleUtil.getToDouble(result.getGenerateQt() - result.getNetQt()));
        return ResponseResult.ok(result);
    }


    /**
     * 校验能量查询参数
     *
     * @param energyQueryVo 查询参数对象
     * @return 如果参数有效返回true，否则返回false
     */
    private boolean validateQueryParams(EnergyQueryVo energyQueryVo) {
        if (energyQueryVo == null) {
            return true;
        }
        String siteIds = energyQueryVo.getSiteIds();
        Integer dateType = energyQueryVo.getDateType();
        String startTime = energyQueryVo.getStartTime();
        String endTime = energyQueryVo.getEndTime();
        // 校验必填字段是否为空
        if (StringUtil.isEmpty(siteIds) || StringUtil.isEmpty(dateType) || StringUtil.isEmpty(startTime) || StringUtil.isEmpty(endTime)) {
            return true;
        }
        if (!siteIds.contains(FileUtil.LEFT_SQUARE) || !siteIds.contains(FileUtil.RIGHT_SQUARE)) {
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
