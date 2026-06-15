package com.sunmax.together.websocket;

import com.alibaba.fastjson2.JSON;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.sunmax.common.dto.data.DeviceHistoryDto;
import com.sunmax.common.dto.data.NodeDifHistoryDto;
import com.sunmax.common.dto.device.DeviceBasicInfoDto;
import com.sunmax.common.dto.device.SiteInfoDto;
import com.sunmax.common.dto.device.SiteScenarioTypeDto;
import com.sunmax.common.dto.device.SiteSetUpDto;
import com.sunmax.common.dto.system.OrganEmpowerListDto;
import com.sunmax.common.model.RealDataModel;
import com.sunmax.common.util.DateUtil;
import com.sunmax.common.util.DoubleUtil;
import com.sunmax.common.util.StringUtil;
import com.sunmax.common.util.oss.FileUtil;
import com.sunmax.common.vo.FunctionLogoParamVo;
import com.sunmax.common.vo.ReaFieldParamVo;
import com.sunmax.common.vo.data.DeviceHistoryQueryVo;
import com.sunmax.together.dto.websocket.RealWebSocketDto;
import com.sunmax.together.mapper.OrderRecordMapper;
import com.sunmax.together.model.ChargeOrderQtModel;
import com.sunmax.together.service.feign.DataService;
import com.sunmax.together.service.feign.DeviceService;
import com.sunmax.together.service.feign.SystemService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Configuration
@Slf4j
public class LargeRealWebSocketHandler extends TextWebSocketHandler {

    @Autowired
    private SystemService systemService;

    @Autowired
    private DeviceService deviceService;

    @Autowired
    private DataService dataService;

    @Autowired
    private OrderRecordMapper orderRecordMapper;

    private static final Map<String, WebSocketSession> sessionMap = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        log.info("大屏实时数据新开启了一个webSocket连接{}", session.getId());
        //从请求参数中获取 userId, 例如: ws://.../together/largeRealWebSocket/{userId}
        String path = Objects.requireNonNull(session.getUri()).getPath();
        String[] pathParts = path.split(FileUtil.SLASH);
        if (pathParts.length >= 4) {
            String userId = pathParts[3];
            sessionMap.put(userId, session);
            //发送第一条消息
            this.sendMessage(userId, JSON.toJSONString(getRealWebSocket(userId)));
            log.info("有新的连接加入！访问大屏实时数据用户id:{}", userId);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        sessionMap.values().removeIf(s -> s.equals(session));
        log.info("大屏实时数据有一连接关闭，sessionId={}, status={}", session.getId(), status);
    }

    private void sendMessage(String userId, String message) {
        WebSocketSession session = sessionMap.get(userId);
        if (session != null && session.isOpen()) {
            try {
                session.sendMessage(new TextMessage(message));
            } catch (Exception e) {
                log.error("推送大屏实时数据失败", e);
            }
        }
    }

    /**
     * 外部调用发送消息
     */
    public void sendAllMessage() {
        sessionMap.keySet().forEach(userId -> sendMessage(userId, JSON.toJSONString(getRealWebSocket(userId))));
    }

    private RealWebSocketDto getRealWebSocket(String userId) {
        //返回的对象
        RealWebSocketDto result = new RealWebSocketDto();
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

        //站点逆变器id 站点id -> 多个逆变器id
        Map<String, Set<String>> siteInverterIdMap = Maps.newHashMap();
        //多个逆变器id
        Set<String> inverterIds = Sets.newHashSet();
        //多个关口表设备id
        Set<String> meterIds = Sets.newHashSet();
        //多个PCS设备id
        Set<String> pcsIds = Sets.newHashSet();
        //多个电池簇设备id->电池簇额定容量
        Map<String, Double> batteryRatedCapMap = Maps.newHashMap();
        //多个充电桩id
        Set<String> pileIds = Sets.newHashSet();
        //多个充电桩编号
        Set<String> pileCodes = Sets.newHashSet();
        //多个换电仓id
        Set<String> granaryIds = Sets.newHashSet();
        //系统类型设备数据 系统类型 -> 设备数据
        Map<Integer, List<DeviceBasicInfoDto>> systemDeviceStatusMap = Maps.newHashMap();

        //额定容量
        AtomicReference<Double> ratedCapacity = new AtomicReference<>(0.0);

        //定义充电桩设备类型
        List<String> pileSystemTypes = Arrays.asList("28", "29", "30", "79");

        //根据多个站点系统id查询站点设备列表
        Map<String, Integer> systemIdTypeMap = siteInfoList.stream().filter(s -> CollectionUtils.isNotEmpty(s.getSiteScenarioTypeDtos()))
                .flatMap(s -> s.getSiteScenarioTypeDtos().stream()).collect(Collectors
                        .toMap(SiteScenarioTypeDto::getId, SiteScenarioTypeDto::getScenarioType, (k1, k2) -> k1));
        deviceService.findDeviceInfoByParentIds(new ArrayList<>(systemIdTypeMap.keySet())).getData().forEach((systemId, deviceInfoList) -> {
            //光伏逆变器设备
            List<DeviceBasicInfoDto> inverterList = deviceInfoList.stream().filter(d -> d.getTypeId().equals("20")
                    || d.getTypeId().equals("77")).collect(Collectors.toList());
            inverterIds.addAll(inverterList.stream().map(DeviceBasicInfoDto::getId).collect(Collectors.toList()));
            for (DeviceBasicInfoDto device : inverterList) {
                Set<String> inverters;
                if (siteInverterIdMap.containsKey(device.getSiteId())) {
                    inverters = siteInverterIdMap.get(device.getSiteId());
                } else {
                    inverters = Sets.newHashSet();
                }
                inverters.add(device.getId());
                siteInverterIdMap.put(device.getSiteId(), inverters);
            }

            //关口表设备
            meterIds.addAll(deviceInfoList.stream().filter(d -> d.getTypeId().equals("39")).map(DeviceBasicInfoDto::getId).collect(Collectors.toList()));

            //储能PCS设备
            pcsIds.addAll(deviceInfoList.stream().filter(d -> d.getTypeId().equals("23") || d.getTypeId().equals("78")).map(DeviceBasicInfoDto::getId).collect(Collectors.toList()));

            //储能电池簇设备
            List<DeviceBasicInfoDto> batteryList = deviceInfoList.stream().filter(d -> d.getTypeId().equals("25")).collect(Collectors.toList());
            //电池簇额定容量
            ratedCapacity.updateAndGet(v -> v + batteryList.stream().mapToDouble(battery -> {
                if (battery.getReaMap().containsKey(ReaFieldParamVo.RATED_CAP)) {
                    return DoubleUtil.objToDouble(battery.getReaMap().get(ReaFieldParamVo.RATED_CAP));
                }
                return 0.0;
            }).sum());
            batteryRatedCapMap.putAll(batteryList.stream().collect(Collectors.toMap(DeviceBasicInfoDto::getId, battery -> {
                if (battery.getReaMap().containsKey(ReaFieldParamVo.RATED_CAP)) {
                    return DoubleUtil.objToDouble(battery.getReaMap().get(ReaFieldParamVo.RATED_CAP));
                }
                return 0.0;
            }, (k1, k2) -> k1)));
            //充电桩设备
            List<DeviceBasicInfoDto> pileList = deviceInfoList.stream().filter(d -> pileSystemTypes.contains(d.getTypeId())).collect(Collectors.toList());
            pileIds.addAll(pileList.stream().map(DeviceBasicInfoDto::getId).filter(StringUtil::isNotEmpty).collect(Collectors.toList()));
            pileCodes.addAll(pileList.stream().map(DeviceBasicInfoDto::getDeviceNumber).filter(StringUtil::isNotEmpty).collect(Collectors.toList()));

            if (systemIdTypeMap.containsKey(systemId)) {
                Integer systemType = systemIdTypeMap.get(systemId);
                if (systemDeviceStatusMap.containsKey(systemType)) {
                    List<DeviceBasicInfoDto> systemDeviceList = systemDeviceStatusMap.get(systemType);
                    systemDeviceList.addAll(deviceInfoList);
                    systemDeviceStatusMap.put(systemType, systemDeviceList);
                } else {
                    systemDeviceStatusMap.put(systemType, deviceInfoList);
                }
            }

            //换电仓设备
            granaryIds.addAll(deviceInfoList.stream().filter(d -> d.getTypeId().equals("70")).map(DeviceBasicInfoDto::getId).collect(Collectors.toList()));
        });

        //定义今天日期(年月日)
        LocalDate todayLocalDate = LocalDate.now();
        String todayDate = DateUtil.localDateToStr(todayLocalDate);
        String yestTodayDate = DateUtil.localDateToStr(todayLocalDate.minusDays(1));
        //定义实时功率查询开始时间和结束时间(今日和昨日)
        String dayStartTime = DateUtil.getDayStart(DateUtil.localDateToStr(todayLocalDate.minusDays(1)));
        String dayEndTime = DateUtil.localDateTimeToStr(LocalDateTime.now());
        //定义运营的开始时间和结束时间(本月)
        String monthStartTime = DateUtil.getDayStart(DateUtil.localDateToStr(todayLocalDate.withDayOfMonth(1)));
        String monthEndTime = DateUtil.localDateTimeToStr(LocalDateTime.now());
        //定义运营的开始时间和结束时间(上月)
        YearMonth previousMonth = YearMonth.from(todayLocalDate).minusMonths(1);
        String lastMonthStartTime = DateUtil.getDayStart(DateUtil.localDateToStr(previousMonth.atDay(1)));
        String lastMonthEndTime = DateUtil.getDayEnd(DateUtil.localDateToStr(previousMonth.atEndOfMonth()));


        //查询光伏相关数据(当前功率,今日发电量,今日上网电量,今日消纳电量,今日和昨日功率曲线,总发电量,本月发电量,本月消纳发电量,本月发电量曲线,去年同期发电量曲线,
        // 日均等效发电时长,较上月日均等效发电时长,本月系统效率,较上月系统效率PR,节约标煤,CO2减排,等效植树)
        //光伏逆变器设备id -> (功能点标识 -> 实时数据)
        Map<String, Map<String, RealDataModel>> inverterRealDataMap = Maps.newHashMap();
        //光伏逆变器今日功率和昨日功率(分钟级)
        Map<String, Double> inverterPowerDataMap = Maps.newHashMap();
        //光伏逆变器设备id -> (功能点标识 -> 本月每天发电量历史数据)
        Map<String, Map<String, List<NodeDifHistoryDto>>> inverterMonthQtDataMap = Maps.newHashMap();
        //光伏逆变器设备id -> (功能点标识 -> 去年本月每天发电量历史数据)
        Map<String, Map<String, List<NodeDifHistoryDto>>> inverterYestQtDataMap = Maps.newHashMap();
        //光伏上网电量 关口表数据 日期(每天) -> 上网电量
        Map<String, Double> meterQtDataMap = Maps.newHashMap();
        //站点id -> 站点光伏设置信息
        Map<String, SiteSetUpDto> siteSetUpMap = Maps.newHashMap();
        if (CollectionUtils.isNotEmpty(inverterIds)) {

            //逆变器当前功率,总发电量,本月发电量,今日发电量(实时缓存里面获取)
//            inverterRealDataMap = deviceService.getDeviceFunctionsRealDataByIds(inverterIds, String.join(FileUtil.COMMA, Arrays.asList(FunctionLogoParamVo.ACTIVE_POWER,
//                    FunctionLogoParamVo.TOTAL_POWER_GENERATION, FunctionLogoParamVo.MONTHLY_POWER_GENERATION, FunctionLogoParamVo.DAILY_POWER_GENERATION))).getData();
            //逆变器当前功率,总发电量(实时缓存里面获取)
            Stream.of(deviceService.getDeviceFunctionsRealDataByIds(inverterIds, FunctionLogoParamVo.ACTIVE_POWER, 2).getData(),
                            deviceService.getDeviceFunctionsRealDataByIds(inverterIds, FunctionLogoParamVo.TOTAL_POWER_GENERATION, 1).getData())
                    .filter(Objects::nonNull) // 过滤掉 null
                    .flatMap(map -> map.entrySet().stream())
                    .forEach(entry -> inverterRealDataMap.computeIfAbsent(entry.getKey(), k -> new HashMap<>()).putAll(entry.getValue()));

            //逆变器今日和昨日功率(分钟级)
            DeviceHistoryQueryVo inverterPowerQueryVo = new DeviceHistoryQueryVo();
            inverterPowerQueryVo.setDeviceIds(inverterIds);
            inverterPowerQueryVo.setFunctionLogos(Collections.singleton(FunctionLogoParamVo.ACTIVE_POWER));
            inverterPowerQueryVo.setStartTime(dayStartTime);
            inverterPowerQueryVo.setEndTime(dayEndTime);
            inverterPowerQueryVo.setTimeInterval("1m");
            inverterPowerDataMap = dataService.findDeviceHistoryValueList(inverterPowerQueryVo).getData().values().stream()
                    .flatMap(i -> i.values().stream().flatMap(Collection::stream))
                    .filter(i -> StringUtil.isNotEmpty(i.getDataValue())).collect(Collectors
                            .groupingBy(DeviceHistoryDto::getDateTime, Collectors.summingDouble(i ->
                                    DoubleUtil.objToDouble(i.getDataValue()))));

            //逆变器本月发电量(天级)
            DeviceHistoryQueryVo inverterMonthQtQueryVo = new DeviceHistoryQueryVo();
            inverterMonthQtQueryVo.setDeviceIds(inverterIds);
            inverterMonthQtQueryVo.setFunctionLogos(Collections.singleton(FunctionLogoParamVo.TOTAL_POWER_GENERATION));
            inverterMonthQtQueryVo.setStartTime(monthStartTime);
            inverterMonthQtQueryVo.setEndTime(monthEndTime);
            inverterMonthQtQueryVo.setTimeInterval("1d");
            inverterMonthQtDataMap = dataService.findNodeDifHistoryListFeign(inverterMonthQtQueryVo).getData();

            //逆变器去年同期数据(天级)
            DeviceHistoryQueryVo inverterYestQtQueryVo = new DeviceHistoryQueryVo();
            inverterYestQtQueryVo.setDeviceIds(inverterIds);
            inverterYestQtQueryVo.setFunctionLogos(Collections.singleton(FunctionLogoParamVo.TOTAL_POWER_GENERATION));
            inverterYestQtQueryVo.setStartTime(DateUtil.getDayStart(DateUtil.localDateToStr(LocalDate.now().minusYears(1).withDayOfMonth(1))));
            inverterYestQtQueryVo.setEndTime(DateUtil.getDayEnd(DateUtil.localDateToStr(LocalDate.now().minusYears(1))));
            inverterYestQtQueryVo.setTimeInterval("1d");
            inverterYestQtDataMap = dataService.findNodeDifHistoryListFeign(inverterYestQtQueryVo).getData();

            //计算光伏的今日上网电量和本月的上网电量(关口表的反向有功电量)
            if (CollectionUtils.isNotEmpty(meterIds)) {
                DeviceHistoryQueryVo meterQtQueryVo = new DeviceHistoryQueryVo();
                meterQtQueryVo.setDeviceIds(meterIds);
                meterQtQueryVo.setFunctionLogos(Collections.singleton(FunctionLogoParamVo.TOTAL_INVERSE_ACTIVE_ENERGY));
                meterQtQueryVo.setStartTime(monthStartTime);
                meterQtQueryVo.setEndTime(monthEndTime);
                meterQtQueryVo.setTimeInterval("1d");
                meterQtDataMap = dataService.findNodeDifHistoryListFeign(meterQtQueryVo).getData().values().stream()
                        .flatMap(s -> s.values().stream().flatMap(Collection::stream))
                        .filter(d -> StringUtil.isNotEmpty(d.getFirstDateTime()) && StringUtil.isNotEmpty(d.getFirstDataValue())
                                && StringUtil.isNotEmpty(d.getLastDataValue())).collect(Collectors.groupingBy(c -> c.getFirstDateTime().substring(0, 10),
                                Collectors.summingDouble(i -> DoubleUtil.objToDouble(i.getLastDataValue()) - DoubleUtil.objToDouble(i.getFirstDataValue()))));
            }

            //根据多个站点id查询光伏设置数据
            siteSetUpMap = deviceService.findSiteSetUpBySiteIds(siteIds).getData();
        }

        //查询储能相关数据(当前功率,今日充电量,今日放电量,今日和昨日功率曲线,储能总充/放电量,当前可充电量,当前可放电量,本月充放电量曲线数据,
        // 本月充放电循环次数,较上月充放电循环次数,本月综合效率,较上月综合效率)
        //储能PCS设备id -> (功能点标识 -> 实时数据)
        Map<String, Map<String, RealDataModel>> pcsRealDataMap = Maps.newHashMap();
        //储能PCS设备今日功率和昨日功率(分钟级) 日期-> 功率
        Map<String, Double> pcsPowerDataMap = Maps.newHashMap();
        //储能PCS设备本月和今日充电量数据 日期-> 充放电量
        Map<String, Double> pcsChargeQtDataMap = Maps.newHashMap();
        //储能PCS设备本月和今日放电量数据 日期-> 充放电量
        Map<String, Double> pcsDisChargeQtDataMap = Maps.newHashMap();
        //储能PCS设备上月充放电量数据 功能点标识 -> 充放电量
        Map<String, Double> pcsLastQtDataMap = Maps.newHashMap();
        //电池簇设备id -> (功能点标识 -> 当前SOC数据)
        Map<String, Map<String, RealDataModel>> batteryRealDataMap = Maps.newHashMap();
        if (CollectionUtils.isNotEmpty(pcsIds)) {
            //获取PCS设备当前功率和总充电量和充放电量(实时缓存里面获取)
            Stream.of(deviceService.getDeviceFunctionsRealDataByIds(pcsIds, FunctionLogoParamVo.PCS_ACTIVE_POWER, 2).getData(),
                            deviceService.getDeviceFunctionsRealDataByIds(pcsIds, String.join(FileUtil.COMMA, FunctionLogoParamVo.PCS_BATTERY_CHARGE,
                                    FunctionLogoParamVo.PCS_BATTERY_DISCHARGE), 1).getData())
                    .filter(Objects::nonNull) // 过滤掉 null
                    .flatMap(map -> map.entrySet().stream())
                    .forEach(entry -> pcsRealDataMap.computeIfAbsent(entry.getKey(), k -> new HashMap<>()).putAll(entry.getValue()));

            //PCS设备今日和昨日功率(分钟级)
            DeviceHistoryQueryVo pcsPowerQueryVo = new DeviceHistoryQueryVo();
            pcsPowerQueryVo.setDeviceIds(pcsIds);
            pcsPowerQueryVo.setFunctionLogos(Collections.singleton(FunctionLogoParamVo.PCS_ACTIVE_POWER));
            pcsPowerQueryVo.setStartTime(dayStartTime);
            pcsPowerQueryVo.setEndTime(dayEndTime);
            pcsPowerQueryVo.setTimeInterval("1m");
            pcsPowerDataMap = dataService.findDeviceHistoryValueList(pcsPowerQueryVo).getData().values().stream()
                    .flatMap(i -> i.values().stream().flatMap(Collection::stream))
                    .filter(i -> StringUtil.isNotEmpty(i.getDataValue())).collect(Collectors.groupingBy(DeviceHistoryDto::getDateTime,
                            Collectors.summingDouble(i -> DoubleUtil.objToDouble(i.getDataValue()))));

            //PCS设备本月充/放电量和今日充/放电量(天级)
            DeviceHistoryQueryVo pcsTotalQtQueryVo = new DeviceHistoryQueryVo();
            pcsTotalQtQueryVo.setDeviceIds(pcsIds);
            pcsTotalQtQueryVo.setFunctionLogos(new HashSet<>(Arrays.asList(FunctionLogoParamVo.PCS_BATTERY_CHARGE, FunctionLogoParamVo.PCS_BATTERY_DISCHARGE)));
            pcsTotalQtQueryVo.setStartTime(monthStartTime);
            pcsTotalQtQueryVo.setEndTime(monthEndTime);
            pcsTotalQtQueryVo.setTimeInterval("1d");
            Map<String, Map<String, Double>> pcsQtDataMap = dataService.findNodeDifHistoryListFeign(pcsTotalQtQueryVo).getData().values().stream()
                    .flatMap(s -> s.values().stream().flatMap(Collection::stream))
                    .filter(d -> StringUtil.isNotEmpty(d.getFunctionLogo()) && StringUtil.isNotEmpty(d.getFirstDateTime())
                            && StringUtil.isNotEmpty(d.getFirstDataValue()) && StringUtil.isNotEmpty(d.getLastDataValue()))
                    .collect(Collectors.groupingBy(NodeDifHistoryDto::getFunctionLogo, Collectors
                            .groupingBy(c -> c.getFirstDateTime().substring(0, 10), Collectors.summingDouble(i ->
                                    DoubleUtil.objToDouble(i.getLastDataValue()) - DoubleUtil.objToDouble(i.getFirstDataValue())))));
            if (MapUtils.isNotEmpty(pcsQtDataMap)) {
                if (pcsQtDataMap.containsKey(FunctionLogoParamVo.PCS_BATTERY_CHARGE)) {
                    pcsChargeQtDataMap = pcsQtDataMap.get(FunctionLogoParamVo.PCS_BATTERY_CHARGE);
                }
                if (pcsQtDataMap.containsKey(FunctionLogoParamVo.PCS_BATTERY_DISCHARGE)) {
                    pcsDisChargeQtDataMap = pcsQtDataMap.get(FunctionLogoParamVo.PCS_BATTERY_DISCHARGE);
                }
            }

            //PCS设备上月充/放电量数据(月级)
            DeviceHistoryQueryVo pcsLastQtQueryVo = new DeviceHistoryQueryVo();
            pcsLastQtQueryVo.setDeviceIds(pcsIds);
            pcsLastQtQueryVo.setFunctionLogos(new HashSet<>(Arrays.asList(FunctionLogoParamVo.PCS_BATTERY_CHARGE, FunctionLogoParamVo.PCS_BATTERY_DISCHARGE)));
            pcsLastQtQueryVo.setStartTime(lastMonthStartTime);
            pcsLastQtQueryVo.setEndTime(lastMonthEndTime);
            pcsLastQtQueryVo.setTimeInterval("1n");
            pcsLastQtDataMap = dataService.findNodeDifHistoryListFeign(pcsLastQtQueryVo).getData().values().stream()
                    .flatMap(s -> s.values().stream().flatMap(Collection::stream))
                    .filter(d -> StringUtil.isNotEmpty(d.getFunctionLogo()) && StringUtil.isNotEmpty(d.getFirstDateTime())
                            && StringUtil.isNotEmpty(d.getFirstDataValue()) && StringUtil.isNotEmpty(d.getLastDataValue()))
                    .collect(Collectors.groupingBy(NodeDifHistoryDto::getFunctionLogo, Collectors.summingDouble(i ->
                            DoubleUtil.objToDouble(i.getLastDataValue()) - DoubleUtil.objToDouble(i.getFirstDataValue()))));
        }
        if (MapUtils.isNotEmpty(batteryRatedCapMap)) {
            //电池簇设备当前SOC
            batteryRealDataMap = deviceService.getDeviceFunctionsRealDataByIds(batteryRatedCapMap.keySet(), FunctionLogoParamVo.BATTERY_TOTAL_SOC, 2).getData();
        }

        //查询充电桩相关数据(当前功率,今日充电量,今日充电次数,今日和昨日功率曲线,充电桩总充电量,本月充电次数,较上个月充电次数,日均枪效,较上月日均枪效,本月充电量曲线)
        //电桩设备id -> (功能点标识 -> 总功率数据)
        Map<String, Map<String, RealDataModel>> pileRealDataMap = Maps.newHashMap();
        //电桩设备今日功率和昨日功率(分钟级)
        Map<String, Double> pilePowerDataMap = Maps.newHashMap();
        //电桩设备 日期-> 本月充电数据
        Map<String, ChargeOrderQtModel> pileChargeOrderMap = Maps.newHashMap();
        //电桩总充电量
        double pileTotalQt = 0.0;
        //电桩上月总充电量
        double pileLastTotalQt = 0.0;
        //电桩上月充电次数
        int pileLastTotalCount = 0;
        //电桩枪数量
        long pileGunNum = 0L;
        if (CollectionUtils.isNotEmpty(pileIds)) {
            //电桩总功率(实时缓存里面获取)
            pileRealDataMap = deviceService.getDeviceFunctionsRealDataByIds(pileIds, FunctionLogoParamVo.PILE_POWER, 2).getData();

            //电桩设备今日和昨日功率(分钟级)
            DeviceHistoryQueryVo pilePowerQueryVo = new DeviceHistoryQueryVo();
            pilePowerQueryVo.setDeviceIds(pileIds);
            pilePowerQueryVo.setFunctionLogos(Collections.singleton(FunctionLogoParamVo.PILE_POWER));
            pilePowerQueryVo.setStartTime(dayStartTime);
            pilePowerQueryVo.setEndTime(dayEndTime);
            pilePowerQueryVo.setTimeInterval("1m");
            pilePowerDataMap = dataService.findDeviceHistoryValueList(pilePowerQueryVo).getData().values().stream()
                    .flatMap(i -> i.values().stream().flatMap(Collection::stream))
                    .filter(i -> StringUtil.isNotEmpty(i.getDataValue())).collect(Collectors.groupingBy(DeviceHistoryDto::getDateTime,
                            Collectors.summingDouble(i -> DoubleUtil.objToDouble(i.getDataValue()))));

            //电枪数量
            pileGunNum = deviceService.findDeviceGunInfoByDeviceIds(new ArrayList<>(pileIds)).getData().values().stream().mapToLong(Collection::size).sum();
        }
        if (CollectionUtils.isNotEmpty(pileCodes)) {
            //电桩本月充电量,本月充电次数(天级)
            List<ChargeOrderQtModel> orderTotalList = orderRecordMapper.countOrderDataByPileCodes(pileCodes, 0, monthStartTime, monthEndTime, 1);
            if (CollectionUtils.isNotEmpty(orderTotalList)) {
                pileChargeOrderMap = orderTotalList.stream().collect(Collectors.toMap(ChargeOrderQtModel::getDataTime, c -> c, (k1, k2) -> k1));
            }
            //电桩总充电量
            ChargeOrderQtModel orderTotal = orderRecordMapper.countOrderTotalQtByPileCodes(pileCodes, 0);
            if (orderTotal != null) {
                pileTotalQt = orderTotal.getTotalQt();
            }

            //电桩上月充电量和上月充电次数(月级)
            List<ChargeOrderQtModel> lastOrderTotalList = orderRecordMapper.countOrderDataByPileCodes(pileCodes, 0, lastMonthStartTime, lastMonthEndTime, 2);
            if (CollectionUtils.isNotEmpty(lastOrderTotalList)) {
                pileLastTotalQt = lastOrderTotalList.stream().filter(o -> StringUtil.isNotEmpty(o.getTotalQt())).mapToDouble(ChargeOrderQtModel::getTotalQt).sum();
                pileLastTotalCount = lastOrderTotalList.stream().filter(o -> StringUtil.isNotEmpty(o.getTotalCount())).mapToInt(ChargeOrderQtModel::getTotalCount).sum();
            }
        }

        //查询换电相关数据(当前功率,今日充电量,今日耗电量,今日和昨日功率曲线,换电站总充电量,本月换电次数,较上月换电次数,日均换电里程,较上月换电里程,本月充电量曲线)
        //TODO 关于换电站用电量 换电次数 换电里程 目前还未实现
        //换电仓设备id -> (功能点标识 -> 实时数据)
        Map<String, Map<String, RealDataModel>> granaryRealDataMap = Maps.newHashMap();
        //换电仓设备今日功率和昨日功率(分钟级) 日期-> 功率
        Map<String, Double> granaryPowerDataMap = Maps.newHashMap();
        //换电仓设备本月充电量 日期-> 充电量
        Map<String, Double> granaryChargeQtMap = Maps.newHashMap();
        if (CollectionUtils.isNotEmpty(granaryIds)) {
            //获取换电仓设备当前功率和总充电量(实时缓存里面获取)
            Stream.of(deviceService.getDeviceFunctionsRealDataByIds(granaryIds, FunctionLogoParamVo.BATTERY_GRANARY_OUT_POWER, 2).getData(),
                            deviceService.getDeviceFunctionsRealDataByIds(granaryIds, FunctionLogoParamVo.BATTERY_GRANARY_SUP_CHARGE, 1).getData())
                    .filter(Objects::nonNull) // 过滤掉 null
                    .flatMap(map -> map.entrySet().stream())
                    .forEach(entry -> granaryRealDataMap.computeIfAbsent(entry.getKey(), k -> new HashMap<>()).putAll(entry.getValue()));

            //换电仓设备今日和昨日功率(分钟级)
            DeviceHistoryQueryVo granaryPowerQueryVo = new DeviceHistoryQueryVo();
            granaryPowerQueryVo.setDeviceIds(granaryIds);
            granaryPowerQueryVo.setFunctionLogos(Collections.singleton(FunctionLogoParamVo.BATTERY_GRANARY_OUT_POWER));
            granaryPowerQueryVo.setStartTime(dayStartTime);
            granaryPowerQueryVo.setEndTime(dayEndTime);
            granaryPowerQueryVo.setTimeInterval("1m");
            granaryPowerDataMap = dataService.findDeviceHistoryValueList(granaryPowerQueryVo).getData().values().stream()
                    .flatMap(i -> i.values().stream().flatMap(Collection::stream))
                    .filter(i -> StringUtil.isNotEmpty(i.getDataValue())).collect(Collectors.groupingBy(DeviceHistoryDto::getDateTime,
                            Collectors.summingDouble(i -> DoubleUtil.objToDouble(i.getDataValue()))));

            //换电仓设备本月充电量和今日充电量(天级)
            DeviceHistoryQueryVo granaryTotalQtQueryVo = new DeviceHistoryQueryVo();
            granaryTotalQtQueryVo.setDeviceIds(granaryIds);
            granaryTotalQtQueryVo.setFunctionLogos(Collections.singleton(FunctionLogoParamVo.BATTERY_GRANARY_SUP_CHARGE));
            granaryTotalQtQueryVo.setStartTime(monthStartTime);
            granaryTotalQtQueryVo.setEndTime(monthEndTime);
            granaryTotalQtQueryVo.setTimeInterval("1d");
            granaryChargeQtMap = dataService.findNodeDifHistoryListFeign(granaryTotalQtQueryVo).getData().values().stream()
                    .flatMap(s -> s.values().stream().flatMap(Collection::stream))
                    .filter(d -> StringUtil.isNotEmpty(d.getFunctionLogo()) && StringUtil.isNotEmpty(d.getFirstDateTime())
                            && StringUtil.isNotEmpty(d.getFirstDataValue()) && StringUtil.isNotEmpty(d.getLastDataValue()))
                    .collect(Collectors.groupingBy(c -> c.getFirstDateTime().substring(0, 10), Collectors.summingDouble(i ->
                            DoubleUtil.objToDouble(i.getLastDataValue()) - DoubleUtil.objToDouble(i.getFirstDataValue()))));
        }

        //实时功率
        RealWebSocketDto.RealPowerDto realPower = new RealWebSocketDto.RealPowerDto();
        //光伏运营
        RealWebSocketDto.PvOperationDto pvOperation = new RealWebSocketDto.PvOperationDto();
        //储能运营
        RealWebSocketDto.StorageOperationDto storageOperation = new RealWebSocketDto.StorageOperationDto();
        //充电桩运营
        RealWebSocketDto.PileOperationDto pileOperation = new RealWebSocketDto.PileOperationDto();
        //换电运营
        RealWebSocketDto.ChangeOperationDto changeOperation = new RealWebSocketDto.ChangeOperationDto();

        realPower.setDateList(DateUtil.getLocalDateTimeBetween(LocalDateTime.of(todayLocalDate, LocalTime.MIN), LocalDateTime.of(todayLocalDate, LocalTime.MAX), "1m")
                .stream().map(localDateTime -> DateUtil.localDateTimeToStr(localDateTime).substring(11, 16)).collect(Collectors.toList()));
        //光伏当前功率,总发电量,本月发电量,今日发电量
        for (Map<String, RealDataModel> entity : inverterRealDataMap.values()) {
            entity.forEach((key, value) -> {
                //光伏当前功率
                if (Objects.equals(key, FunctionLogoParamVo.ACTIVE_POWER) && StringUtil.isNotEmpty(value.getDataValue())) {
                    realPower.setPvTotalPower(realPower.getPvTotalPower() + DoubleUtil.objToDouble(value.getDataValue()));
                }
                //光伏总发电量
                if (Objects.equals(key, FunctionLogoParamVo.TOTAL_POWER_GENERATION) && StringUtil.isNotEmpty(value.getDataValue())) {
                    pvOperation.setTotalQt(pvOperation.getTotalQt() + DoubleUtil.objToDouble(value.getDataValue()));
                }
                //光伏本月发电量
//                if (Objects.equals(key, FunctionLogoParamVo.MONTHLY_POWER_GENERATION) && StringUtil.isNotEmpty(value.getDataValue())) {
//                    pvOperation.setMonthQt(pvOperation.getMonthQt() + DoubleUtil.objToDouble(value.getDataValue()));
//                }
                //光伏今日发电量
//                if (Objects.equals(key, FunctionLogoParamVo.DAILY_POWER_GENERATION) && StringUtil.isNotEmpty(value.getDataValue())) {
//                    realPower.setPvQt(realPower.getPvQt() + DoubleUtil.objToDouble(value.getDataValue()));
//                }
            });
        }

        //光伏本月发电量
        pvOperation.setMonthQt(inverterMonthQtDataMap.values().stream().flatMap(i -> i.values().stream().flatMap(Collection::stream))
                .mapToDouble(i -> DoubleUtil.getObjSub(i.getFirstDataValue(), i.getLastDataValue())).sum());
        //储能当前功率,储能总充电量,储能总放电量
        for (Map<String, RealDataModel> entity : pcsRealDataMap.values()) {
            entity.forEach((key, value) -> {
                //储能当前功率
                if (Objects.equals(key, FunctionLogoParamVo.PCS_ACTIVE_POWER) && StringUtil.isNotEmpty(value.getDataValue())) {
                    realPower.setStorageTotalPower(realPower.getStorageTotalPower() + DoubleUtil.objToDouble(value.getDataValue()));
                }
                //储能总充电量
                if (Objects.equals(key, FunctionLogoParamVo.PCS_BATTERY_CHARGE) && StringUtil.isNotEmpty(value.getDataValue())) {
                    storageOperation.setChargeTotalQt(storageOperation.getChargeTotalQt() + DoubleUtil.objToDouble(value.getDataValue()));
                }
                //储能总放电量
                if (Objects.equals(key, FunctionLogoParamVo.PCS_BATTERY_DISCHARGE) && StringUtil.isNotEmpty(value.getDataValue())) {
                    storageOperation.setDischargeTotalQt(storageOperation.getDischargeTotalQt() + DoubleUtil.objToDouble(value.getDataValue()));
                }
            });
        }

        //充电桩总功率
        realPower.setPileTotalPower(DoubleUtil.getToDouble(pileRealDataMap.values().stream()
                .flatMap(value -> value.values().stream().map(RealDataModel::getDataValue).filter(StringUtil::isNotEmpty))
                .mapToDouble(DoubleUtil::objToDouble).sum()));

        //换电当前功率,换电总充电量
        for (Map<String, RealDataModel> entity : granaryRealDataMap.values()) {
            entity.forEach((key, value) -> {
                //换电当前功率
                if (Objects.equals(key, FunctionLogoParamVo.BATTERY_GRANARY_OUT_POWER) && StringUtil.isNotEmpty(value.getDataValue())) {
                    realPower.setTotalChangePower(realPower.getTotalChangePower() + DoubleUtil.objToDouble(value.getDataValue()));
                }
                //换电总充电量
                if (Objects.equals(key, FunctionLogoParamVo.BATTERY_GRANARY_SUP_CHARGE) && StringUtil.isNotEmpty(value.getDataValue())) {
                    changeOperation.setTotalChargeQt(changeOperation.getTotalChargeQt() + DoubleUtil.objToDouble(value.getDataValue()));
                }
            });
        }

        //获取实时功率系统今日和昨日功率曲线数据
        for (String dateTime : realPower.getDateList()) {
            //今日时间
            String todayTime = todayDate + FileUtil.SPACE + dateTime + ":00";
            //昨日时间
            String yesterdayTime = yestTodayDate + FileUtil.SPACE + dateTime + ":00";
            //光伏今日功率曲线数据
            if (inverterPowerDataMap.containsKey(todayTime)) {
                realPower.getPvDayPowerList().add(DoubleUtil.getToDouble(inverterPowerDataMap.get(todayTime)));
            } else {
                realPower.getPvDayPowerList().add(null);
            }
            //光伏昨日功率曲线数据
            if (inverterPowerDataMap.containsKey(yesterdayTime)) {
                realPower.getPvYestdayPowerList().add(DoubleUtil.getToDouble(inverterPowerDataMap.get(yesterdayTime)));
            } else {
                realPower.getPvYestdayPowerList().add(null);
            }
            //储能今日功率曲线数据
            if (pcsPowerDataMap.containsKey(todayTime)) {
                realPower.getStorageDayPowerList().add(DoubleUtil.getToDouble(pcsPowerDataMap.get(todayTime)));
            } else {
                realPower.getStorageDayPowerList().add(null);
            }
            //储能昨日功率曲线数据
            if (pcsPowerDataMap.containsKey(yesterdayTime)) {
                realPower.getStorageYestdayPowerList().add(DoubleUtil.getToDouble(pcsPowerDataMap.get(yesterdayTime)));
            } else {
                realPower.getStorageYestdayPowerList().add(null);
            }
            //充电桩今日功率曲线数据
            if (pilePowerDataMap.containsKey(todayTime)) {
                realPower.getPileDayPowerList().add(DoubleUtil.getToDouble(pilePowerDataMap.get(todayTime)));
            } else {
                realPower.getPileDayPowerList().add(null);
            }
            //充电桩昨日功率曲线数据
            if (pilePowerDataMap.containsKey(yesterdayTime)) {
                realPower.getPileYestdayPowerList().add(DoubleUtil.getToDouble(pilePowerDataMap.get(yesterdayTime)));
            } else {
                realPower.getPileYestdayPowerList().add(null);
            }
            //换电今日功率曲线数据
            if (granaryPowerDataMap.containsKey(todayTime)) {
                realPower.getChangeDayPowerList().add(DoubleUtil.getToDouble(granaryPowerDataMap.get(todayTime)));
            } else {
                realPower.getChangeDayPowerList().add(null);
            }
            //换电昨日功率曲线数据
            if (granaryPowerDataMap.containsKey(yesterdayTime)) {
                realPower.getChangeYestdayPowerList().add(DoubleUtil.getToDouble(granaryPowerDataMap.get(yesterdayTime)));
            } else {
                realPower.getChangeYestdayPowerList().add(null);
            }
        }

        //定义运营日期列表(本月天级)
        result.setOperationDateList(DateUtil.getDateBetween(1, DateUtil.localDateToStr(todayLocalDate.withDayOfMonth(1)),
                todayDate).stream().map(c -> c.substring(8, 10)).collect(Collectors.toList()));

        //组装本月每天的电量数据
        //光伏本月每天发电量
        Map<String, Double> pvMonthQtDataMap = inverterMonthQtDataMap.values().stream()
                .flatMap(i -> i.values().stream().flatMap(Collection::stream))
                .filter(i -> StringUtil.isNotEmpty(i.getFirstDateTime()))
                .collect(Collectors.groupingBy(c -> c.getFirstDateTime().substring(0, 10),
                        Collectors.summingDouble(i -> DoubleUtil.getObjSub(i.getFirstDataValue(), i.getLastDataValue()))));
        //光伏去年本月每天发电量
        Map<String, Double> pvYestMonthQtDataMap = inverterYestQtDataMap.values().stream()
                .flatMap(i -> i.values().stream().flatMap(Collection::stream))
                .filter(i -> StringUtil.isNotEmpty(i.getFirstDateTime()))
                .collect(Collectors.groupingBy(c -> c.getFirstDateTime().substring(0, 10),
                        Collectors.summingDouble(i -> DoubleUtil.getObjSub(i.getFirstDataValue(), i.getLastDataValue()))));
        //光伏今日发电量
        realPower.setPvQt(pvMonthQtDataMap.getOrDefault(todayDate, 0.0));
        //光伏本月发电量
        pvOperation.setMonthQt(inverterMonthQtDataMap.values().stream().flatMap(i -> i.values().stream().flatMap(Collection::stream))
                .mapToDouble(i -> DoubleUtil.getObjSub(i.getFirstDataValue(), i.getLastDataValue())).sum());
        for (String date : result.getOperationDateList()) {
            //本月日期
            String monthDate = todayDate.substring(0, 7) + FileUtil.BAR + date;
            //去年当月日期
            String yestMonthDate = DateUtil.localDateToStr(todayLocalDate.minusYears(1)).substring(0, 7) + FileUtil.BAR + date;

            //光伏发电量曲线
            pvOperation.getQtList().add(DoubleUtil.getToDouble(pvMonthQtDataMap.getOrDefault(monthDate, 0.0)));

            //光伏去年同期发电量曲线
            pvOperation.getQtLastYearList().add(DoubleUtil.getToDouble(pvYestMonthQtDataMap.getOrDefault(yestMonthDate, 0.0)));

            //储能的充电量曲线
            storageOperation.getChargeQtList().add(DoubleUtil.getToDouble(pcsChargeQtDataMap.getOrDefault(monthDate, 0.0)));

            //储能的放电量曲线
            storageOperation.getDischargeQtList().add(DoubleUtil.getToDouble(pcsDisChargeQtDataMap.getOrDefault(monthDate, 0.0)));

            //充电桩的充电量曲线
            if (pileChargeOrderMap.containsKey(monthDate) && StringUtil.isNotEmpty(pileChargeOrderMap.get(monthDate))) {
                pileOperation.getChargeQtList().add(DoubleUtil.getToDouble(pileChargeOrderMap.get(monthDate).getTotalQt()));
            } else {
                pileOperation.getChargeQtList().add(0.0);
            }

            //换电的充电量曲线
            changeOperation.getChargeQtList().add(DoubleUtil.getToDouble(granaryChargeQtMap.getOrDefault(monthDate, 0.0)));
        }

        //计算光伏今日上网电量,今日消纳电量,本月消纳电量,本月消纳率,光伏二氧化碳(CO2)减排量(kg),光伏节约标准煤量(kg),光伏等效植树量(颗)
        //光伏今日上网电量
        if (meterQtDataMap.containsKey(todayDate)) {
            realPower.setPvNetQt(DoubleUtil.getToDouble(meterQtDataMap.get(todayDate)));
        }
        //光伏今日消纳电量
        realPower.setPvConsumeQt(DoubleUtil.getToDouble(realPower.getPvQt() - realPower.getPvConsumeQt()));
        //光伏本月消纳电量
        double pvMonthNetQt = meterQtDataMap.values().stream().filter(StringUtil::isNotEmpty).mapToDouble(i -> i).sum();
        pvOperation.setMonthConsumeQt(DoubleUtil.getToDouble(pvOperation.getMonthQt() - pvMonthNetQt));
        //光伏本月消纳率
        pvOperation.setMontConsumeRate(DoubleUtil.getToDouble(pvOperation.getMonthConsumeQt() / pvOperation.getMonthQt() * 100));
        //光伏二氧化碳减排量,节约标煤量,等效植树量
        for (Map.Entry<String, Set<String>> entry : siteInverterIdMap.entrySet()) {
            String siteId = entry.getKey();
            //站点下的光伏总发电量
            double totalQt = entry.getValue().stream().filter(inverterRealDataMap::containsKey).mapToDouble(inverterId -> {
                RealDataModel realDataModel = inverterRealDataMap.get(inverterId).get(FunctionLogoParamVo.TOTAL_POWER_GENERATION);
                if (realDataModel != null && StringUtil.isNotEmpty(realDataModel.getDataValue())) {
                    return DoubleUtil.objToDouble(realDataModel.getDataValue());
                }
                return 0.0;
            }).sum();
            if (siteSetUpMap.containsKey(siteId)) {
                SiteSetUpDto siteSetUp = siteSetUpMap.get(siteId);
                //二氧化碳减排量(电站发电量 * CO₂减排转换系数(0.475))
                Double co2Reduction = null;
                if (StringUtil.isNotEmpty(siteSetUp.getReduceCoeff())) {
                    co2Reduction = totalQt * siteSetUp.getReduceCoeff();
                    pvOperation.setCo2Reduction(pvOperation.getCo2Reduction() + co2Reduction);
                }
                //节约标煤量(电站发电量 * 节约标准煤转换系数(0.4))
                if (StringUtil.isNotEmpty(siteSetUp.getTceCoeff())) {
                    pvOperation.setStandardCoalReduction(pvOperation.getStandardCoalReduction() + (totalQt * siteSetUp.getTceCoeff()));
                }
                //等效植树量(二氧化碳减排量 / 等效植树量转换系数（18.3）/ 40)
                if (co2Reduction != null && siteSetUp.getTreeCoeff() != null && siteSetUp.getTreeCoeff() != 0.0) {
                    pvOperation.setTreeReduction(pvOperation.getTreeReduction() + (co2Reduction / siteSetUp.getTreeCoeff() / 40));
                }
            }
        }

        //储能今日充/放电量,本月充放循环次数,上月充放循环次数,本月综合效率,上月综合效率,当前可充电量,当前可放电量
        //储能今日充电量
        if (pcsChargeQtDataMap.containsKey(todayDate)) {
            realPower.setStorageChargeQt(DoubleUtil.getAbsDouble(pcsChargeQtDataMap.get(todayDate)));
        }
        //储能今日放电量
        if (pcsDisChargeQtDataMap.containsKey(todayDate)) {
            realPower.setStorageDischargeQt(DoubleUtil.getAbsDouble(pcsDisChargeQtDataMap.get(todayDate)));
        }
        //本月充电量
        double pcsMonthChargeQt = DoubleUtil.getAbsDouble(pcsChargeQtDataMap.values().stream().filter(StringUtil::isNotEmpty).mapToDouble(i -> i).sum());
        //上月充电量
        Double pcsLastMonthChargeQt = 0.0;
        if (pcsLastQtDataMap.containsKey(FunctionLogoParamVo.PCS_BATTERY_CHARGE) && StringUtil.isNotEmpty(pcsLastQtDataMap.get(FunctionLogoParamVo.PCS_BATTERY_CHARGE))) {
            pcsLastMonthChargeQt = pcsLastQtDataMap.get(FunctionLogoParamVo.PCS_BATTERY_CHARGE);
        }
        //本月放电量
        double pcsMonthDischargeQt = DoubleUtil.getAbsDouble(pcsDisChargeQtDataMap.values().stream().filter(StringUtil::isNotEmpty).mapToDouble(i -> i).sum());
        //上月放电量
        Double pcsLastMonthDischargeQt = 0.0;
        if (pcsLastQtDataMap.containsKey(FunctionLogoParamVo.PCS_BATTERY_DISCHARGE) && StringUtil.isNotEmpty(pcsLastQtDataMap.get(FunctionLogoParamVo.PCS_BATTERY_DISCHARGE))) {
            pcsLastMonthDischargeQt = pcsLastQtDataMap.get(FunctionLogoParamVo.PCS_BATTERY_DISCHARGE);
        }
        //储能本月充放循环次数,上月充放循环次数
        if (ratedCapacity.get() != 0.0) {
            //本月充放循环次数(本月放电量/电池簇总额定容量)
            storageOperation.setMonthTimes(DoubleUtil.getToDouble(pcsMonthDischargeQt / ratedCapacity.get()));
            //上月充放循环次数(上月放电量/电池簇总额定容量)
            storageOperation.setMonthTimesCompare(DoubleUtil.getToDouble(storageOperation.getMonthTimes() - (pcsLastMonthDischargeQt / ratedCapacity.get())));
        }
        //本月综合效率(本月放电量/本月充电量)
        if (pcsMonthChargeQt != 0.0) {
            storageOperation.setMonthEfficiency(DoubleUtil.getToDouble(pcsMonthDischargeQt / pcsMonthChargeQt * 100));
        }
        //较上月本月综合效率比
        if (pcsLastMonthChargeQt != 0.0) {
            //上月综合效率(上月放电量/上月充电量)
            Double lastMonthEfficiency = DoubleUtil.getToDouble(pcsLastMonthDischargeQt / pcsLastMonthChargeQt * 100);
            storageOperation.setMonthEfficiencyCompare(storageOperation.getMonthEfficiency() - lastMonthEfficiency);
        }
        //储能当前可充电量,储能当前可放电量
        for (Map.Entry<String, Map<String, RealDataModel>> entry : batteryRealDataMap.entrySet()) {
            String batteryId = entry.getKey();
            Map<String, RealDataModel> realDataMap = entry.getValue();
            if (batteryRatedCapMap.containsKey(batteryId) && realDataMap.containsKey(FunctionLogoParamVo.BATTERY_TOTAL_SOC)) {
                Double ratedCap = batteryRatedCapMap.get(batteryId); //额定容量
                RealDataModel realDataModel = realDataMap.get(FunctionLogoParamVo.BATTERY_TOTAL_SOC);
                if (realDataModel != null && StringUtil.isNotEmpty(realDataModel.getDataValue())) {
                    double soc = DoubleUtil.objToDouble(realDataModel.getDataValue());
                    //计算储能当前可充电量(储能额定容量 × (100-SOC) / 100) 注意：SOC为整数
                    storageOperation.setChargeCurrentQt(storageOperation.getChargeCurrentQt() + DoubleUtil.getToDouble(ratedCap * ((100 - soc) / 100)));
                    //计算储能当前可放电量(储能额定容量 × SOC / 100) 注意：SOC为整数
                    storageOperation.setDischargeCurrentQt(storageOperation.getDischargeCurrentQt() + DoubleUtil.getToDouble(ratedCap * (soc / 100)));
                }
            }
        }

        //计算充电桩 今日充电量,今日充电次数,电桩总充电量,本月充电次数,上月充电次数,本月日均枪效，上月日均枪效
        if (pileChargeOrderMap.containsKey(todayDate) && pileChargeOrderMap.get(todayDate) != null) {
            ChargeOrderQtModel chargeOrderQtModel = pileChargeOrderMap.get(todayDate);
            //今日充电量
            if (StringUtil.isNotEmpty(chargeOrderQtModel.getTotalQt())) {
                realPower.setPileChargeQt(chargeOrderQtModel.getTotalQt());
            }
            //今日充电次数
            if (StringUtil.isNotEmpty(chargeOrderQtModel.getTotalCount())) {
                realPower.setPileChargeNum(chargeOrderQtModel.getTotalCount());
            }
        }
        //电桩总充电量
        pileOperation.setTotalChargeQt(pileTotalQt);
        //本月充电次数
        int pileMonthChargeNum = pileChargeOrderMap.values().stream().filter(c -> StringUtil.isNotEmpty(c.getTotalCount()))
                .mapToInt(ChargeOrderQtModel::getTotalCount).sum();
        pileOperation.setMonthChargeNum(pileMonthChargeNum);
        //较上月充电次数
        pileOperation.setMonthNumCompare(pileMonthChargeNum - pileLastTotalCount);
        //计算本月日均枪效(kWh/枪/天)和上月日均枪效(kWh/枪/天)
        //本月充电电量
        double pileMonthChargeQt = pileChargeOrderMap.values().stream().filter(c -> StringUtil.isNotEmpty(c.getTotalQt()))
                .mapToDouble(ChargeOrderQtModel::getTotalQt).sum();
        //本月日均枪效(本月充电量/电枪数量/本月天数)
        Long monthDays = DateUtil.compareDiffBetweenDays(LocalDateTime.of(todayLocalDate.withDayOfMonth(1), LocalTime.MIN), LocalDateTime.of(todayLocalDate, LocalTime.MAX));
        pileOperation.setDayChargeEfficiency(DoubleUtil.getToDouble(pileMonthChargeQt / pileGunNum / monthDays));
        //上月充电量/电枪数量/上月天数
        int lastMonthDays = YearMonth.from(todayLocalDate).minusMonths(1).lengthOfMonth();
        Double lastDayChargeEfficiency = DoubleUtil.getToDouble(pileLastTotalQt / pileGunNum / lastMonthDays);
        pileOperation.setDayEfficiencyCompare(DoubleUtil.getToDouble(pileOperation.getDayChargeEfficiency() - lastDayChargeEfficiency));

        //设备状态
        result.setDeviceStatus(systemDeviceStatusMap.entrySet().stream().map(entity -> {
            RealWebSocketDto.DeviceStatusDto deviceStatus = new RealWebSocketDto.DeviceStatusDto();
            deviceStatus.setType(entity.getKey());
            List<DeviceBasicInfoDto> deviceList = entity.getValue();
            //通信状态 0-未注册 1-在线 2-故障 88-离线
            deviceStatus.setNormalNum((int) deviceList.stream().filter(d -> Objects.equals(1, d.getTxStatus())).count());
            deviceStatus.setErrorNum((int) deviceList.stream().filter(d -> Objects.equals(2, d.getTxStatus())).count());
            deviceStatus.setOfflineNum((int) deviceList.stream().filter(d -> Objects.equals(88, d.getTxStatus())).count());
            return deviceStatus;
        }).collect(Collectors.toList()));

        //换电今日充电量
        if (granaryChargeQtMap.containsKey(todayDate)) {
            realPower.setChangeChargeQt(DoubleUtil.getToDouble(granaryChargeQtMap.getOrDefault(todayDate, 0.0)));
        }

        //实时功率相关数据 处理小数点位
        realPower.setPvTotalPower(DoubleUtil.getToDouble(realPower.getPvTotalPower()));
        realPower.setPvQt(DoubleUtil.getToDouble(realPower.getPvQt()));
        realPower.setPvNetQt(DoubleUtil.getToDouble(realPower.getPvNetQt()));
        realPower.setPvConsumeQt(DoubleUtil.getToDouble(realPower.getPvConsumeQt()));
        realPower.setStorageTotalPower(DoubleUtil.getToDouble(realPower.getStorageTotalPower()));
        realPower.setStorageChargeQt(DoubleUtil.getToDouble(realPower.getStorageChargeQt()));
        realPower.setStorageDischargeQt(DoubleUtil.getToDouble(realPower.getStorageDischargeQt()));
        realPower.setPileTotalPower(DoubleUtil.getToDouble(realPower.getPileTotalPower()));
        realPower.setPileChargeQt(DoubleUtil.getToDouble(realPower.getPileChargeQt()));
        realPower.setTotalChangePower(DoubleUtil.getToDouble(realPower.getTotalChangePower()));
        realPower.setChangeChargeQt(DoubleUtil.getToDouble(realPower.getChangeChargeQt()));
        realPower.setChangeUseQt(DoubleUtil.getToDouble(realPower.getChangeUseQt()));

        //光伏运营相关数据 处理小数点位
        pvOperation.setTotalQt(DoubleUtil.getToDouble(pvOperation.getTotalQt()));
        pvOperation.setMonthQt(DoubleUtil.getToDouble(pvOperation.getMonthQt()));
        pvOperation.setMonthConsumeQt(DoubleUtil.getToDouble(pvOperation.getMonthConsumeQt()));
        pvOperation.setDayEffectiveTime(DoubleUtil.getToDouble(pvOperation.getDayEffectiveTime()));
        pvOperation.setDayEffectiveCompare(DoubleUtil.getToDouble(pvOperation.getDayEffectiveCompare()));
        pvOperation.setSystemEfficiency(DoubleUtil.getToDouble(pvOperation.getSystemEfficiency()));
        pvOperation.setSystemEfficiencyCompare(DoubleUtil.getToDouble(pvOperation.getSystemEfficiencyCompare()));
        pvOperation.setCo2Reduction(DoubleUtil.getToDouble(pvOperation.getCo2Reduction()));
        pvOperation.setStandardCoalReduction(DoubleUtil.getToDouble(pvOperation.getStandardCoalReduction()));
        pvOperation.setTreeReduction(DoubleUtil.getToDouble(pvOperation.getTreeReduction()));

        //储能运营相关数据 处理小数点位
        storageOperation.setChargeTotalQt(DoubleUtil.getToDouble(storageOperation.getChargeTotalQt()));
        storageOperation.setDischargeTotalQt(DoubleUtil.getToDouble(storageOperation.getDischargeTotalQt()));
        storageOperation.setChargeCurrentQt(DoubleUtil.getToDouble(storageOperation.getChargeCurrentQt()));
        storageOperation.setDischargeCurrentQt(DoubleUtil.getToDouble(storageOperation.getDischargeCurrentQt()));
        storageOperation.setMonthTimes(DoubleUtil.getToDouble(storageOperation.getMonthTimes()));
        storageOperation.setMonthTimesCompare(DoubleUtil.getToDouble(storageOperation.getMonthTimesCompare()));
        storageOperation.setMonthEfficiency(DoubleUtil.getToDouble(storageOperation.getMonthEfficiency()));
        storageOperation.setMonthEfficiencyCompare(DoubleUtil.getToDouble(storageOperation.getMonthEfficiencyCompare()));

        //充电桩运营数据 处理小数点位
        pileOperation.setTotalChargeQt(DoubleUtil.getToDouble(pileOperation.getTotalChargeQt()));
        pileOperation.setDayChargeEfficiency(DoubleUtil.getToDouble(pileOperation.getDayChargeEfficiency()));
        pileOperation.setDayEfficiencyCompare(DoubleUtil.getToDouble(pileOperation.getDayEfficiencyCompare()));

        //换电运营相关数据 处理小数点位
        changeOperation.setTotalChargeQt(DoubleUtil.getToDouble(changeOperation.getTotalChargeQt()));
        changeOperation.setDayKm(DoubleUtil.getToDouble(changeOperation.getDayKm()));
        changeOperation.setDayKmCompare(DoubleUtil.getToDouble(changeOperation.getDayKmCompare()));

        result.setRealPower(realPower);
        result.setPvOperation(pvOperation);
        result.setStorageOperation(storageOperation);
        result.setPileOperation(pileOperation);
        result.setChangeOperation(changeOperation);
        return result;

    }

}
