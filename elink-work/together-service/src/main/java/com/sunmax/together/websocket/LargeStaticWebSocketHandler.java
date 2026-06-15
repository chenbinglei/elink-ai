package com.sunmax.together.websocket;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.sunmax.common.dto.data.NodeDifHistoryDto;
import com.sunmax.common.dto.device.DeviceAlarmEventListDto;
import com.sunmax.common.dto.device.DeviceBasicInfoDto;
import com.sunmax.common.dto.device.SiteInfoDto;
import com.sunmax.common.dto.device.SiteScenarioTypeDto;
import com.sunmax.common.dto.system.OrganEmpowerListDto;
import com.sunmax.common.util.DateUtil;
import com.sunmax.common.util.DoubleUtil;
import com.sunmax.common.util.StringUtil;
import com.sunmax.common.util.oss.FileUtil;
import com.sunmax.common.vo.FunctionLogoParamVo;
import com.sunmax.common.vo.ReaFieldParamVo;
import com.sunmax.common.vo.SiteFieldParamVo;
import com.sunmax.common.vo.data.DeviceHistoryQueryVo;
import com.sunmax.common.vo.device.DeviceAlarmEventQueryVo;
import com.sunmax.together.dao.order.OrderRecordDao;
import com.sunmax.together.dto.websocket.StaticWebSocketDto;
import com.sunmax.together.entity.order.OrderRecordEntity;
import com.sunmax.together.service.feign.DataService;
import com.sunmax.together.service.feign.DeviceService;
import com.sunmax.together.service.feign.SystemService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import jakarta.persistence.criteria.Predicate;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Configuration
@Slf4j
public class LargeStaticWebSocketHandler extends TextWebSocketHandler {

    @Autowired
    private SystemService systemService;

    @Autowired
    private DeviceService deviceService;

    @Autowired
    private DataService dataService;

    @Autowired
    private OrderRecordDao orderRecordDao;

    private static final Map<String, WebSocketSession> sessionMap = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        log.info("大屏静态数据新开启了一个webSocket连接{}", session.getId());
        //从请求参数中获取 userId, 例如: ws://.../together/largeStaticWebSocket/{userId}
        String path = Objects.requireNonNull(session.getUri()).getPath();
        String[] pathParts = path.split(FileUtil.SLASH);
        if (pathParts.length >= 4) {
            String userId = pathParts[3];
            sessionMap.put(userId, session);
            //发送第一条数据
            this.sendMessage(userId, JSON.toJSONString(getStaticWebSocket(userId)));
            log.info("有新的连接加入！访问大屏静态数据用户id: {}", userId);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        sessionMap.values().removeIf(s -> s.equals(session));
        log.info("大屏静态数据有一连接关闭，sessionId={}, status={}", session.getId(), status);
    }

    private void sendMessage(String userId, String message) {
        WebSocketSession session = sessionMap.get(userId);
        if (session != null && session.isOpen()) {
            try {
                session.sendMessage(new TextMessage(message));
            } catch (Exception e) {
                log.error("推送大屏静态数据失败", e);
            }
        }
    }

    /**
     * 外部调用发送消息
     */
    public void sendAllMessage() {
        sessionMap.keySet().forEach(userId -> sendMessage(userId, JSON.toJSONString(getStaticWebSocket(userId))));
    }

    private StaticWebSocketDto getStaticWebSocket(String userId) {
        //返回的对象
        StaticWebSocketDto result = new StaticWebSocketDto();
        //查询用户下面所有的站点列表
        List<String> siteIds = systemService.findAllOrganEmpowerByUserId(userId).getData().stream().map(OrganEmpowerListDto::getSiteId)
                .filter(StringUtil::isNotEmpty).distinct().collect(Collectors.toList());
        if (siteIds.isEmpty()) {
            return result;
        }
        //根据多个站点id查询站点数据列表
        List<SiteInfoDto> siteInfoList = deviceService.findSiteBasicInfoByIds(siteIds).getData().values().stream()
                .filter(s -> StringUtil.isNotEmpty(s.getScenarioTypes())).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(siteInfoList)) {
            return result;
        }

        //光伏系统逆变器设备 系统id -> 多个逆变器id
        Map<String, List<String>> systemInverterIdMap = Maps.newHashMap();
        //储能系统PCS设备 系统id -> 多个PCS设备id
        Map<String, List<String>> systemPcsIdMap = Maps.newHashMap();
        //充电桩系统设备 系统id -> 多个充电桩编号
        Map<String, List<String>> systemPileCodeMap = Maps.newHashMap();
        //换电系统设备 系统id -> 多个换电仓id
        Map<String, List<String>> systemGranaryIdMap = Maps.newHashMap();
        //储能系统电池簇额定容量 系统id -> 电池簇额定容量
        Map<String, Double> systemBatteryRatedCapMap = Maps.newHashMap();
        //充电系统装机容量 系统id -> 电桩总额定功率
        Map<String, Double> systemPileRatedPowerMap = Maps.newHashMap();
        //换电系统装机容量 系统id -> 换电仓总额定功率
        Map<String, Double> systemGranaryRatedPowerMap = Maps.newHashMap();
        //定义充电桩设备类型
        List<String> pileSystemTypes = Arrays.asList("28", "29", "30", "79");
        //定义多个设备id列表
        List<String> deviceIds = Lists.newArrayList();
        //根据多个站点系统id查询站点设备列表
        List<String> systemIds = siteInfoList.stream().filter(s -> CollectionUtils.isNotEmpty(s.getSiteScenarioTypeDtos()))
                .flatMap(s -> s.getSiteScenarioTypeDtos().stream().map(scenarioType -> {
                    if (scenarioType.getScenarioType() == 3) { //充电桩额度容量
                        if(StringUtil.isNotEmpty(scenarioType.getReadwriteObject())) {
                            JSONObject readwriteMap = JSON.parseObject(scenarioType.getReadwriteObject());
                            if (readwriteMap.containsKey(SiteFieldParamVo.CAPACITY) && StringUtil.isNotEmpty(readwriteMap.getString(SiteFieldParamVo.CAPACITY))) {
                                systemPileRatedPowerMap.put(scenarioType.getId(), readwriteMap.getDoubleValue(SiteFieldParamVo.CAPACITY));
                            } else {
                                systemPileRatedPowerMap.put(scenarioType.getId(), 0.0);
                            }
                        }
                    }
                    return scenarioType.getId();
                })).collect(Collectors.toList());
        deviceService.findDeviceInfoByParentIds(systemIds).getData().forEach((systemId, deviceInfoList) -> {
            //光伏逆变器设备
            systemInverterIdMap.put(systemId, deviceInfoList.stream().filter(d -> d.getTypeId().equals("20")
                    || d.getTypeId().equals("77")).map(DeviceBasicInfoDto::getId).collect(Collectors.toList()));

            //储能PCS设备
            systemPcsIdMap.put(systemId, deviceInfoList.stream().filter(d -> d.getTypeId().equals("23")
                    || d.getTypeId().equals("78")).map(DeviceBasicInfoDto::getId).collect(Collectors.toList()));

            //储能系统电池簇额定容量
            List<DeviceBasicInfoDto> systemBatteryList = deviceInfoList.stream().filter(d -> d.getTypeId().equals("25")).collect(Collectors.toList());
            systemBatteryRatedCapMap.put(systemId, systemBatteryList.stream().mapToDouble(battery -> {
                if (battery.getReaMap().containsKey(ReaFieldParamVo.RATED_CAP) && StringUtil.isNotEmpty(battery.getReaMap().get(ReaFieldParamVo.RATED_CAP))) {
                    return Double.parseDouble(String.valueOf(battery.getReaMap().get(ReaFieldParamVo.RATED_CAP)));
                }
                return 0.0;
            }).sum());

            //充电桩设备
            List<DeviceBasicInfoDto> systemPileList = deviceInfoList.stream().filter(d -> pileSystemTypes.contains(d.getTypeId()) && StringUtil.isNotEmpty(d.getDeviceNumber()))
                    .collect(Collectors.toList());
            systemPileCodeMap.put(systemId, systemPileList.stream().map(DeviceBasicInfoDto::getDeviceNumber)
                    .filter(StringUtil::isNotEmpty).collect(Collectors.toList()));
//            systemPileRatedPowerMap.put(systemId, systemPileList.stream().mapToDouble(pile -> {
//                if (pile.getReaMap().containsKey(ReaFieldParamVo.RATED_POWER) && StringUtil.isNotEmpty(pile.getReaMap().get(ReaFieldParamVo.RATED_POWER))) {
//                    return Double.parseDouble(String.valueOf(pile.getReaMap().get(ReaFieldParamVo.RATED_POWER)));
//                }
//                return 0.0;
//            }).sum());

            //换电仓设备
            List<DeviceBasicInfoDto> systemChangeList = deviceInfoList.stream().filter(d -> d.getTypeId().equals("70")).collect(Collectors.toList());
            systemGranaryIdMap.put(systemId, systemChangeList.stream().map(DeviceBasicInfoDto::getId).collect(Collectors.toList()));
            //换电系统换电仓设备额定容量
            systemGranaryRatedPowerMap.put(systemId, systemChangeList.stream().mapToDouble(change -> {
                if (change.getReaMap().containsKey(ReaFieldParamVo.RATED_POWER) && StringUtil.isNotEmpty(change.getReaMap().get(ReaFieldParamVo.RATED_POWER))) {
                    return Double.parseDouble(String.valueOf(change.getReaMap().get(ReaFieldParamVo.RATED_POWER)));
                }
                return 0.0;
            }).sum());

            deviceIds.addAll(deviceInfoList.stream().map(DeviceBasicInfoDto::getId).collect(Collectors.toList()));

        });
        //定义查询历史数据的开始时间和结束时间
        String startTime = DateUtil.getDayStart(DateUtil.localDateToStr(LocalDate.now()));
        String endTime = DateUtil.localDateTimeToStr(LocalDateTime.now());

        //查询光伏设备的日发电量
        //逆变器设备id -> (功能点标识 -> 数据)
        Map<String, Map<String, List<NodeDifHistoryDto>>> inverterQtMap = Maps.newHashMap();
        if (MapUtils.isNotEmpty(systemInverterIdMap)) {
            Set<String> inverterIds = systemInverterIdMap.values().stream().flatMap(List::stream).collect(Collectors.toSet());
            //查询光伏逆变器的今日发电量
            DeviceHistoryQueryVo devicePcsQtQueryVo = new DeviceHistoryQueryVo();
            devicePcsQtQueryVo.setDeviceIds(inverterIds);
            devicePcsQtQueryVo.setFunctionLogos(Collections.singleton(FunctionLogoParamVo.TOTAL_POWER_GENERATION));
            devicePcsQtQueryVo.setStartTime(startTime);
            devicePcsQtQueryVo.setEndTime(endTime);
            devicePcsQtQueryVo.setTimeInterval("1d");
            inverterQtMap = dataService.findNodeDifHistoryListFeign(devicePcsQtQueryVo).getData();
        }

        //查询储能设备的日充/放电量
        //PCS设备id -> (功能点标识 -> 设备历史数据实体)
        Map<String, Map<String, List<NodeDifHistoryDto>>> pcsQtMap = Maps.newHashMap();
        if (MapUtils.isNotEmpty(systemPcsIdMap)) {

            Set<String> pcsIds = systemPcsIdMap.values().stream().flatMap(List::stream).collect(Collectors.toSet());

            //查询PCS设备今日充电量和今日放电量
            DeviceHistoryQueryVo devicePcsQtQueryVo = new DeviceHistoryQueryVo();
            Set<String> functionLogos = new HashSet<>(Arrays.asList(FunctionLogoParamVo.PCS_BATTERY_CHARGE, FunctionLogoParamVo.PCS_BATTERY_DISCHARGE));
            devicePcsQtQueryVo.setDeviceIds(pcsIds);
            devicePcsQtQueryVo.setFunctionLogos(functionLogos);
            devicePcsQtQueryVo.setStartTime(startTime);
            devicePcsQtQueryVo.setEndTime(endTime);
            devicePcsQtQueryVo.setTimeInterval("1d");
            pcsQtMap = dataService.findNodeDifHistoryListFeign(devicePcsQtQueryVo).getData();
        }

        //查询充电桩设备的日充/放电量
        Map<String, Double> pileChargeQtMap = Maps.newHashMap();
        Map<String, Double> pileDischargeQtMap = Maps.newHashMap();
        if (MapUtils.isNotEmpty(systemPileCodeMap)) {
            //获取系统电桩编号列表
            List<String> pileCodes = systemPileCodeMap.values().stream().flatMap(Collection::stream).collect(Collectors.toList());
            List<OrderRecordEntity> orderRecordList = orderRecordDao.findAll((Specification<OrderRecordEntity>) (root, cq, cb) -> {
                List<Predicate> predicates = Lists.newArrayList();
                predicates.add(cb.in(root.get("pileCode")).value(pileCodes));
                predicates.add(cb.between(root.get("endTime"), startTime, endTime));
                predicates.add(cb.isNotNull(root.get("runMode")));
                predicates.add(cb.or(cb.isNull(root.get("abnormalCode")), cb.equal(root.get("abnormalCode"), "[5]")));
//                predicates.add(cb.equal(root.get("orderStatus"), "2"));
                return cb.and(predicates.toArray(new Predicate[0]));
            });

            //充电订单
            pileChargeQtMap = orderRecordList.stream().filter(s -> s.getRunMode() == 0).collect(Collectors.groupingBy(OrderRecordEntity::getPileCode,
                    Collectors.summingDouble(c -> DoubleUtil.getAbsDouble(c.getTotalQt(), 5))));
            //放电订单
            pileDischargeQtMap = orderRecordList.stream().filter(s -> s.getRunMode() == 1 || s.getRunMode() == 2).collect(Collectors
                    .groupingBy(OrderRecordEntity::getPileCode, Collectors.summingDouble(c -> DoubleUtil.getAbsDouble(c.getTotalQt(), 5))));

        }

        //查询换电设备的充/用电量
        //TODO 用电量相关待定
        //换电仓设备id -> (功能点标识 -> 设备历史数据实体)
        Map<String, Map<String, List<NodeDifHistoryDto>>> granaryQtMap = Maps.newHashMap();
        if (MapUtils.isNotEmpty(systemGranaryIdMap)) {

            Set<String> granaryIds = systemGranaryIdMap.values().stream().flatMap(List::stream).collect(Collectors.toSet());

            //查询换电仓设备今日充电量
            DeviceHistoryQueryVo deviceGranaryQtQueryVo = new DeviceHistoryQueryVo();
            deviceGranaryQtQueryVo.setDeviceIds(granaryIds);
            deviceGranaryQtQueryVo.setFunctionLogos(Collections.singleton(FunctionLogoParamVo.BATTERY_GRANARY_SUP_CHARGE));
            deviceGranaryQtQueryVo.setStartTime(startTime);
            deviceGranaryQtQueryVo.setEndTime(endTime);
            deviceGranaryQtQueryVo.setTimeInterval("1d");
            granaryQtMap = dataService.findNodeDifHistoryListFeign(deviceGranaryQtQueryVo).getData();
        }

        //对站点数据进行组装
        List<StaticWebSocketDto.SiteDto> siteList = Lists.newArrayList();

        for (SiteInfoDto siteInfo : siteInfoList) {
            StaticWebSocketDto.SiteDto site = new StaticWebSocketDto.SiteDto();
            site.setSiteId(siteInfo.getId());
            site.setSiteName(siteInfo.getSiteName());
            if (StringUtil.isNotEmpty(siteInfo.getSiteReadwriteObject())) {
                site.setLocation(JSONObject.parseObject(siteInfo.getSiteReadwriteObject()).getString(SiteFieldParamVo.LOCATION));
            }
            if (StringUtil.isEmpty(siteInfo.getScenarioTypes())) {
                continue;
            }
            Integer siteType = getSiteType(siteInfo.getScenarioTypes());
            if (StringUtil.isEmpty(siteType)) {
                continue;
            }
            site.setSiteType(siteType);
            site.setScenarioTypes(siteInfo.getScenarioTypes());

            Map<String, SiteScenarioTypeDto> scenarioTypeMap = Maps.newHashMap();
            if (StringUtil.isNotEmpty(siteInfo.getSiteScenarioTypeDtos())) {
                scenarioTypeMap = siteInfo.getSiteScenarioTypeDtos().stream().collect(Collectors.toMap(SiteScenarioTypeDto::getId,
                        a -> a, (k1, k2) -> k1));
            }

            double storageChargeQt = 0.0;
            double storageDisChargeQt = 0.0;
            double pileChargeQt = 0.0;
            double pileDisChargeQt = 0.0;
            double changeChargeQt = 0.0;
            Double changeDisChargeQt = 0.0;
            for (Map.Entry<String, SiteScenarioTypeDto> entry : scenarioTypeMap.entrySet()) {
                String systemId = entry.getKey();
                SiteScenarioTypeDto scenarioType = entry.getValue();
                //能源类型 1-光伏 2-储能 3-充电桩 4-用能 5-配电 6-换电
                switch (scenarioType.getScenarioType()) {
                    case 1:
                        //光伏系统装机容量
                        JSONObject reaMap = JSONObject.parseObject(scenarioType.getReadwriteObject());
                        if (reaMap != null && reaMap.containsKey(SiteFieldParamVo.PV_CAPACITY) && StringUtil.isNotEmpty(reaMap.getString(SiteFieldParamVo.PV_CAPACITY))) {
                            site.setPvCapacity(site.getPvCapacity() + Double.parseDouble(reaMap.getString(SiteFieldParamVo.PV_CAPACITY)));
                        }
                        if (systemInverterIdMap.containsKey(systemId)) {
                            List<String> inverterIds = systemInverterIdMap.get(systemId);
                            //光伏今日发电量
                            double pvDayQt = inverterQtMap.entrySet().stream().filter(d -> inverterIds.contains(d.getKey()))
                                    .flatMap(d -> d.getValue().entrySet().stream().filter(s -> s.getValue() != null)
                                            .flatMap(s -> s.getValue().stream()))
                                    .mapToDouble(d -> DoubleUtil.getObjSub(d.getFirstDataValue(), d.getLastDataValue())).sum();
                            site.setPvDayQt(site.getPvDayQt() + pvDayQt);
                        }
                        break;
                    case 2:
                        //储能系统装机容量
                        site.setStorageCapacity(site.getStorageCapacity() + systemBatteryRatedCapMap.getOrDefault(systemId, 0.0));
                        if (systemPcsIdMap.containsKey(systemId)) {
                            List<String> pcdIds = systemPcsIdMap.get(systemId);
                            //储能今日充电量和今日放电量
                            Map<String, Double> systemPcsQtMap = pcsQtMap.entrySet().stream().filter(d -> pcdIds.contains(d.getKey()))
                                    .flatMap(d -> d.getValue().entrySet().stream().flatMap(s -> s.getValue().stream().filter(c -> StringUtil.isNotEmpty(c.getFirstDataValue()) && StringUtil.isNotEmpty(c.getLastDataValue()))))
                                    .collect(Collectors.groupingBy(NodeDifHistoryDto::getFunctionLogo, Collectors.summingDouble(c -> Double.parseDouble(String.valueOf(c.getLastDataValue())) - Double.parseDouble(String.valueOf(c.getFirstDataValue())))));
                            //储能系统今日充电量
                            if (systemPcsQtMap.containsKey(FunctionLogoParamVo.PCS_BATTERY_CHARGE)) {
                                storageChargeQt = storageChargeQt + DoubleUtil.getAbsDouble(systemPcsQtMap.getOrDefault(FunctionLogoParamVo.PCS_BATTERY_CHARGE, 0.0));
                            }
                            //储能系统今日放电量
                            if (systemPcsQtMap.containsKey(FunctionLogoParamVo.PCS_BATTERY_DISCHARGE)) {
                                storageDisChargeQt = storageDisChargeQt + DoubleUtil.getAbsDouble(systemPcsQtMap.getOrDefault(FunctionLogoParamVo.PCS_BATTERY_DISCHARGE, 0.0));
                            }
                        }
                        break;
                    case 3:
                        //充电系统装机容量
                        if (systemPileRatedPowerMap.containsKey(systemId)) {
                            site.setPileCapacity(site.getPileCapacity() + systemPileRatedPowerMap.getOrDefault(systemId, 0.0));
                        }
                        if (systemPileCodeMap.containsKey(systemId)) {
                            List<String> pileCodes = systemPileCodeMap.get(systemId);
                            //充电系统今日充电量
                            pileChargeQt = pileChargeQt + pileChargeQtMap.entrySet().stream().filter(d -> pileCodes.contains(d.getKey())).mapToDouble(Map.Entry::getValue).sum();
                            //充电系统今日放电量
                            pileDisChargeQt = pileDisChargeQt + pileDischargeQtMap.entrySet().stream().filter(d -> pileCodes.contains(d.getKey())).mapToDouble(Map.Entry::getValue).sum();
                        }
                        break;
                    case 6:
                        //换电系统装机容量
                        site.setChangeCapacity(site.getChangeCapacity() + systemGranaryRatedPowerMap.getOrDefault(systemId, 0.0));
                        //储能今日充电量和今日放电量
                        changeChargeQt = changeChargeQt + granaryQtMap.entrySet().stream().filter(d -> Objects.equals(d.getKey(), systemId))
                                .flatMap(d -> d.getValue().entrySet().stream().flatMap(s ->
                                        s.getValue().stream().filter(c -> StringUtil.isNotEmpty(c.getFirstDataValue()) && StringUtil.isNotEmpty(c.getLastDataValue()))))
                                .mapToDouble(c -> Double.parseDouble(String.valueOf(c.getLastDataValue())) - Double.parseDouble(String.valueOf(c.getFirstDataValue()))).sum();
                        //TODO 待定
//                        site.setChangeDischargeQt();
                        break;
                }
            }
            site.setPvDayQt(DoubleUtil.getToDouble(site.getPvDayQt()));
            site.setStorageChargeQt(DoubleUtil.getToDouble(storageChargeQt) + FileUtil.SLASH + DoubleUtil.getToDouble(storageDisChargeQt));
            site.setPileChargeQt(DoubleUtil.getToDouble(pileChargeQt) + FileUtil.SLASH + DoubleUtil.getToDouble(pileDisChargeQt));
            site.setChangeChargeQt(DoubleUtil.getToDouble(changeChargeQt) + FileUtil.SLASH + DoubleUtil.getToDouble(changeDisChargeQt));
            siteList.add(site);
        }

        //根据多个设备id查询设备今日告警数据
        DeviceAlarmEventQueryVo eventQueryVo = new DeviceAlarmEventQueryVo();
        eventQueryVo.setDeviceIds(JSON.toJSONString(deviceIds));
        eventQueryVo.setStartDate(DateUtil.localDateToStr(LocalDate.now()));
        eventQueryVo.setEndDate(DateUtil.localDateToStr(LocalDate.now()));
        eventQueryVo.setPage(1);
        eventQueryVo.setSize(999999);
        List<DeviceAlarmEventListDto> deviceAlarmEventList = deviceService.findAllDeviceEventList(eventQueryVo).getData().getItems();

        if (CollectionUtils.isNotEmpty(siteList)) {
            //资产总览数据(总装机容量,光伏装机容量,储能装机容量,充电桩装机容量,换电装机容量)
            StaticWebSocketDto.AssetOverviewDto assetOverview = new StaticWebSocketDto.AssetOverviewDto();
            assetOverview.setPvCapacity(DoubleUtil.getToDouble(siteList.stream().mapToDouble(StaticWebSocketDto.SiteDto::getPvCapacity).sum()));
            assetOverview.setStorageCapacity(DoubleUtil.getToDouble(siteList.stream().mapToDouble(StaticWebSocketDto.SiteDto::getStorageCapacity).sum()));
            assetOverview.setPileCapacity(DoubleUtil.getToDouble(siteList.stream().mapToDouble(StaticWebSocketDto.SiteDto::getPileCapacity).sum()));
            assetOverview.setChangeCapacity(DoubleUtil.getToDouble(siteList.stream().mapToDouble(StaticWebSocketDto.SiteDto::getChangeCapacity).sum()));
            assetOverview.setTotalCapacity(DoubleUtil.getToDouble(assetOverview.getPvCapacity() + assetOverview.getStorageCapacity()
                    + assetOverview.getPileCapacity() + assetOverview.getChangeCapacity()));
            result.setAssetOverview(assetOverview);

            //地图站点数据(站点数量,站点数量统计数据,)
            StaticWebSocketDto.MapSiteDto mapSite = new StaticWebSocketDto.MapSiteDto();
            mapSite.setSiteCount(siteList.size());
            mapSite.setSiteCountMap(siteList.stream().collect(Collectors.groupingBy(c -> String.valueOf(c.getSiteType()), Collectors.counting())));
            mapSite.setSiteList(siteList);
            result.setMapSite(mapSite);

            //设备告警数据
            StaticWebSocketDto.DeviceAlarmDto deviceAlarm = new StaticWebSocketDto.DeviceAlarmDto();
            deviceAlarm.setDayAlarmNum(deviceAlarmEventList.size());
            deviceAlarm.setFixAlarmNum((int) deviceAlarmEventList.stream().filter(d -> StringUtil.isNotEmpty(d.getEventStatus())
                    && d.getEventStatus() == 1).count());
            if (deviceAlarm.getDayAlarmNum() != 0) {
                deviceAlarm.setFixAlarmRate(DoubleUtil.getToDouble((double) deviceAlarm.getFixAlarmNum() / deviceAlarm.getDayAlarmNum() * 100));
            }
            result.setDeviceAlarm(deviceAlarm);
        }
        return result;
    }

    private Integer getSiteType(String scenarioTypes) {
        Set<Integer> scenarioTypeSet = Arrays.stream(scenarioTypes.split(FileUtil.COMMA)).filter(StringUtil::isNotEmpty)
                .map(Integer::parseInt).collect(Collectors.toSet());
        //站点类型 0-一体化电站 1-光伏电站 2-储能电站 3-充电桩电站 4-换电电站
        if (scenarioTypeSet.size() > 1) {
            return 0;
        } else if (scenarioTypeSet.contains(1)) {
            return 1;
        } else if (scenarioTypeSet.contains(2)) {
            return 2;
        } else if (scenarioTypeSet.contains(3)) {
            return 3;
        } else if (scenarioTypeSet.contains(6)) {
            return 4;
        }
        return null;
    }

}
