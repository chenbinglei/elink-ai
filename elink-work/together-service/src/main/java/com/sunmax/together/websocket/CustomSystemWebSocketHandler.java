package com.sunmax.together.websocket;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.sunmax.common.dto.data.DeviceHistoryDto;
import com.sunmax.common.dto.data.NodeDifHistoryDto;
import com.sunmax.common.dto.device.DeviceBasicInfoDto;
import com.sunmax.common.dto.device.ModelFunctionListDto;
import com.sunmax.common.model.RealDataModel;
import com.sunmax.common.util.DateUtil;
import com.sunmax.common.util.DoubleUtil;
import com.sunmax.common.util.StringUtil;
import com.sunmax.common.util.oss.FileUtil;
import com.sunmax.common.vo.data.DeviceHistoryQueryVo;
import com.sunmax.together.dao.strategy.StrategyDao;
import com.sunmax.together.dao.strategy.TemplateDao;
import com.sunmax.together.dto.websocket.CustomSystemWebSocketDto;
import com.sunmax.together.entity.strategy.StrategyEntity;
import com.sunmax.together.entity.strategy.TemplateEntity;
import com.sunmax.together.service.feign.DataService;
import com.sunmax.together.service.feign.DeviceService;
import com.sunmax.together.vo.custom.DeviceIdParamVo;
import com.sunmax.together.vo.custom.FunctionParamVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.compress.utils.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Configuration
@Slf4j
public class CustomSystemWebSocketHandler extends TextWebSocketHandler {

    @Autowired
    private DeviceService deviceService;

    @Autowired
    private DataService dataService;

    @Autowired
    private StrategyDao strategyDao;

    @Autowired
    private TemplateDao templateDao;

    //用户id -> session会话
    private final Map<String, WebSocketSession> sessionMap = new ConcurrentHashMap<>();

    //用户id -> 站点id
    private final Map<String, String> userSiteIdMap = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        log.info("海天定制化系统新开启了一个webSocket连接{}", session.getId());
        //从请求参数中获取 userId, 例如: ws://.../together/customSystemWebSocket/{userId}/{siteId}
        String path = Objects.requireNonNull(session.getUri()).getPath();
        String[] pathParts = path.split(FileUtil.SLASH);
        if (pathParts.length >= 5) {
            String userId = pathParts[3];
            String siteId = pathParts[4];
            sessionMap.put(userId, session);
            userSiteIdMap.put(userId, siteId);
            //连接后发送一条数据
            this.sendMessage(userId, JSON.toJSONString(getSystemWebSocket(siteId)));
            log.info("有新的连接加入！访问海天定制化系统用户id:{}", userId);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        sessionMap.values().removeIf(s -> s.equals(session));
        String path = Objects.requireNonNull(session.getUri()).getPath();
        String[] pathParts = path.split(FileUtil.SLASH);
        if (pathParts.length >= 5) {
            String userId = pathParts[3];
            userSiteIdMap.remove(userId);
        }
        log.info("海天定制化系统数据有一连接关闭，sessionId={}, status={}", session.getId(), status);
    }

    private void sendMessage(String userId, String message) {
        WebSocketSession session = sessionMap.get(userId);
        if (session != null && session.isOpen()) {
            try {
                session.sendMessage(new TextMessage(message));
            } catch (Exception e) {
                log.error("推送海天定制化系统数据失败", e);
            }
        }
    }

    /**
     * 外部调用发送消息
     */
    public void sendAllMessage() {
        sessionMap.keySet().forEach(userId -> sendMessage(userId, JSON.toJSONString(getSystemWebSocket(userSiteIdMap.get(userId)))));
    }

    private CustomSystemWebSocketDto getSystemWebSocket(String siteId) {
        //推送的对象
        CustomSystemWebSocketDto result = new CustomSystemWebSocketDto();
        result.setSiteId(siteId);

        //获取运行模式数据
        result.setRunMode(getRunMode(siteId));

        //获取功率曲线数据
        result.setPowerCurveAnalysis(getPowerCurveAnalysis());

        //获取关口数据
        result.setStageGate(getStageGate());

        //获取直流母线数据
        result.setDcBus(getDcBus());

        //获取光伏数据
        result.setPhotovoltaic(getPhotovoltaic());

        //获取储能数据
        result.setEnergyStorage(getEnergyStorage());

        //获取负载数据
        result.setLoad(getLoad());

        //对数据进行组装
        return result;
    }

    //获取运行模式数据
    private CustomSystemWebSocketDto.RunMode getRunMode(String siteId) {
        //返回的对象
        CustomSystemWebSocketDto.RunMode runMode = new CustomSystemWebSocketDto.RunMode();
        //根据站点id查询设备网关列表
        List<DeviceBasicInfoDto> deviceInfoList = deviceService.findDeviceBasicInfoBySiteIds(Collections.singletonList(siteId), 2)
                .getData().getOrDefault(siteId, Lists.newArrayList()).stream().filter(d -> StringUtil.isNotEmpty(d.getTypeId()) && Objects.equals(d.getTypeId(), "31"))
                .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(deviceInfoList)) {
            List<CustomSystemWebSocketDto.GatewayDevice> gatewayDeviceList = deviceInfoList.stream().map(d -> {
                CustomSystemWebSocketDto.GatewayDevice gatewayDevice = new CustomSystemWebSocketDto.GatewayDevice();
                gatewayDevice.setId(d.getId());
                gatewayDevice.setDeviceNumber(d.getDeviceNumber());
                gatewayDevice.setTxStatus(d.getTxStatus());
                return gatewayDevice;
            }).collect(Collectors.toList());
            runMode.setGatewayDeviceList(gatewayDeviceList);

            //根据多个设备id查询策略模板类型
            List<String> deviceIds = deviceInfoList.stream().map(DeviceBasicInfoDto::getId).collect(Collectors.toList());
            Set<String> templateIds = strategyDao.findAllByDeviceIdIn(deviceIds).stream().map(StrategyEntity::getTemplateId)
                    .filter(StringUtil::isNotEmpty).collect(Collectors.toSet());
            if (CollectionUtils.isNotEmpty(templateIds)) {
                Optional<TemplateEntity> optional = templateDao.findAllById(templateIds).stream().max(Comparator.comparing(TemplateEntity::getUpdateTime));
                if (optional.isPresent()) {
                    TemplateEntity templateEntity = optional.get();
                    runMode.setStrategyType(templateEntity.getStrategyType());
                    runMode.setRunControl(2);
                }
            }
        }
        return runMode;
    }

    //获取功率曲线数据
    private CustomSystemWebSocketDto.PowerCurveAnalysis getPowerCurveAnalysis() {
        //返回的对象
        CustomSystemWebSocketDto.PowerCurveAnalysis result = new CustomSystemWebSocketDto.PowerCurveAnalysis();

        //定义开始时间,结束时间，时间间隔
        LocalDate todayLocalDate = LocalDate.now();
        String todayDate = DateUtil.localDateToStr(todayLocalDate);
        String yestTodayDate = DateUtil.localDateToStr(todayLocalDate.minusDays(1));
        //定义实时功率查询开始时间和结束时间(今日和昨日)
        String dayStartTime = DateUtil.getDayStart(DateUtil.localDateToStr(todayLocalDate.minusDays(1)));
        String dayEndTime = DateUtil.localDateTimeToStr(LocalDateTime.now());
        String timeInterval = "1m";
        //获取时间列表
        List<String> timeList = DateUtil.getLocalDateTimeBetween(LocalDateTime.of(todayLocalDate, LocalTime.MIN), LocalDateTime.of(todayLocalDate, LocalTime.MAX), timeInterval)
                .stream().map(localDateTime -> DateUtil.localDateTimeToStr(localDateTime).substring(11, 16)).collect(Collectors.toList());

        //获取光伏功率(今日和昨日数据)
        Map<String, Double> pvPowerDataMap = getPowerDataMap(DeviceIdParamVo.PV_DC_DC_ID, dayStartTime, dayEndTime, timeInterval, FunctionParamVo.ACTIVE_POWER);
        //获取储能功率(今日和昨日数据)
        Map<String, Double> sePowerDataMap = getPowerDataMap(DeviceIdParamVo.SE_DC_DC_ID, dayStartTime, dayEndTime, timeInterval, FunctionParamVo.PCS_ACTIVE_POWER);
        //获取直流母线功率(今日和昨日数据)
        Map<String, Double> busPowerDataMap = getPowerDataMap(DeviceIdParamVo.BUS_DC_METER_ID, dayStartTime, dayEndTime, timeInterval, FunctionParamVo.TOTAL_ACTIVE_POWER);
        //获取负载功率(今日和昨日数据)
        Map<String, Double> loadPowerDataMap = getPowerDataMap(DeviceIdParamVo.DC_GGD_LOAD_ID, dayStartTime, dayEndTime, timeInterval, FunctionParamVo.TOTAL_ACTIVE_POWER);

        //对数据进行组装
        //获取实时功率系统今日和昨日功率曲线数据
        for (String time : timeList) {
            //今日时间
            String todayTime = todayDate + FileUtil.SPACE + time + ":00";
            //获取今日功率曲线数据
            if (DateUtil.strToLocalDateTime(todayTime).isBefore(LocalDateTime.now())) {
                //今日时间
                result.getTodayTimeList().add(time);

                //光伏今日功率曲线数据
                if (pvPowerDataMap.containsKey(todayTime)) {
                    result.getPvTodayPowerList().add(DoubleUtil.getToDouble(pvPowerDataMap.get(todayTime)));
                } else {
                    result.getPvTodayPowerList().add(null);
                }
                //储能今日功率曲线数据
                if (sePowerDataMap.containsKey(todayTime)) {
                    result.getSeTodayPowerList().add(DoubleUtil.getToDouble(sePowerDataMap.get(todayTime)));
                } else {
                    result.getSeTodayPowerList().add(null);
                }
                //负载今日功率曲线数据
                if (loadPowerDataMap.containsKey(todayTime)) {
                    result.getLoadTodayPowerList().add(DoubleUtil.getToDouble(loadPowerDataMap.get(todayTime)));
                } else {
                    result.getLoadTodayPowerList().add(null);
                }
                //直流母线今日功率曲线数据
                if (busPowerDataMap.containsKey(todayTime)) {
                    result.getBusTodayPowerList().add(DoubleUtil.getToDouble(busPowerDataMap.get(todayTime)));
                } else {
                    result.getBusTodayPowerList().add(null);
                }
            }

            //获取昨日功率曲线数据
            //昨日时间
            result.getYestdayTimeList().add(time);
            //昨日时间
            String yesterdayTime = yestTodayDate + FileUtil.SPACE + time + ":00";
            //光伏昨日功率曲线数据
            if (pvPowerDataMap.containsKey(yesterdayTime)) {
                result.getPvYestdayPowerList().add(DoubleUtil.getToDouble(pvPowerDataMap.get(yesterdayTime)));
            } else {
                result.getPvYestdayPowerList().add(null);
            }
            //储能昨日功率曲线数据
            if (sePowerDataMap.containsKey(yesterdayTime)) {
                result.getSeYestdayPowerList().add(DoubleUtil.getToDouble(sePowerDataMap.get(yesterdayTime)));
            } else {
                result.getSeYestdayPowerList().add(null);
            }
            //负载昨日功率曲线数据
            if (loadPowerDataMap.containsKey(yesterdayTime)) {
                result.getLoadYestdayPowerList().add(DoubleUtil.getToDouble(loadPowerDataMap.get(yesterdayTime)));
            } else {
                result.getLoadYestdayPowerList().add(null);
            }
            //直流母线昨日功率曲线数据
            if (busPowerDataMap.containsKey(yesterdayTime)) {
                result.getBusYestdayPowerList().add(DoubleUtil.getToDouble(busPowerDataMap.get(yesterdayTime)));
            } else {
                result.getBusYestdayPowerList().add(null);
            }
        }
        return result;
    }

    //获取关口对象数据
    private CustomSystemWebSocketDto.StageGate getStageGate() {
        //返回的对象
        CustomSystemWebSocketDto.StageGate result = new CustomSystemWebSocketDto.StageGate();
        //获取关口实时数据
        String functionLogos = String.join(FileUtil.COMMA, FunctionParamVo.A_PHASE_VOLTAGE, FunctionParamVo.B_PHASE_VOLTAGE, FunctionParamVo.C_PHASE_VOLTAGE,
                FunctionParamVo.A_PHASE_CURRENT, FunctionParamVo.B_PHASE_CURRENT, FunctionParamVo.C_PHASE_CURRENT, FunctionParamVo.TOTAL_ACTIVE_POWER,
                FunctionParamVo.POWER_FACTOR, FunctionParamVo.FREQUENCY);
        Map<String, RealDataModel> realDataMap = deviceService.getDeviceFunctionsRealDataByIds(Collections.singleton(DeviceIdParamVo.AC_GGD_METER_ID),
                functionLogos, 2).getData().get(DeviceIdParamVo.AC_GGD_METER_ID);
        if (MapUtils.isNotEmpty(realDataMap)) {
            if (realDataMap.containsKey(FunctionParamVo.A_PHASE_VOLTAGE)) { //A相电压
                result.setVoltageA(DoubleUtil.objToDouble(realDataMap.get(FunctionParamVo.A_PHASE_VOLTAGE).getDataValue(), 2, null));
            }
            if (realDataMap.containsKey(FunctionParamVo.B_PHASE_VOLTAGE)) { //B相电压
                result.setVoltageB(DoubleUtil.objToDouble(realDataMap.get(FunctionParamVo.B_PHASE_VOLTAGE).getDataValue(), 2, null));
            }
            if (realDataMap.containsKey(FunctionParamVo.C_PHASE_VOLTAGE)) { //C相电压
                result.setVoltageC(DoubleUtil.objToDouble(realDataMap.get(FunctionParamVo.C_PHASE_VOLTAGE).getDataValue(), 2, null));
            }
            if (realDataMap.containsKey(FunctionParamVo.A_PHASE_CURRENT)) { //A相电流
                result.setCurrentA(DoubleUtil.objToDouble(realDataMap.get(FunctionParamVo.A_PHASE_CURRENT).getDataValue(), 2, null));
            }
            if (realDataMap.containsKey(FunctionParamVo.B_PHASE_CURRENT)) { //B相电流
                result.setCurrentB(DoubleUtil.objToDouble(realDataMap.get(FunctionParamVo.B_PHASE_CURRENT).getDataValue(), 2, null));
            }
            if (realDataMap.containsKey(FunctionParamVo.C_PHASE_CURRENT)) { //C相电流
                result.setCurrentC(DoubleUtil.objToDouble(realDataMap.get(FunctionParamVo.C_PHASE_CURRENT).getDataValue(), 2, null));
            }
            if (realDataMap.containsKey(FunctionParamVo.TOTAL_ACTIVE_POWER)) { //总有功功率
                result.setPower(DoubleUtil.objToDouble(realDataMap.get(FunctionParamVo.TOTAL_ACTIVE_POWER).getDataValue(), 2, null));
            }
            if (realDataMap.containsKey(FunctionParamVo.POWER_FACTOR)) { //功率因数
                result.setPowerFactor(DoubleUtil.objToDouble(realDataMap.get(FunctionParamVo.POWER_FACTOR).getDataValue(), 2, null));
            }
            if (realDataMap.containsKey(FunctionParamVo.FREQUENCY)) { //频率
                result.setFrequency(DoubleUtil.objToDouble(realDataMap.get(FunctionParamVo.FREQUENCY).getDataValue(), 2, null));
            }
        }
        return result;
    }

    //获取直流母线对象数据
    private CustomSystemWebSocketDto.DcBus getDcBus() {
        //返回的对象
        CustomSystemWebSocketDto.DcBus result = new CustomSystemWebSocketDto.DcBus();
        //获取直流母线交流双向表实时数据
        String acFunctionLogos = String.join(FileUtil.COMMA, FunctionParamVo.A_PHASE_VOLTAGE, FunctionParamVo.B_PHASE_VOLTAGE, FunctionParamVo.C_PHASE_VOLTAGE,
                FunctionParamVo.A_PHASE_CURRENT, FunctionParamVo.B_PHASE_CURRENT, FunctionParamVo.C_PHASE_CURRENT, FunctionParamVo.TOTAL_ACTIVE_POWER);
        Map<String, RealDataModel> acRealDataMap = deviceService.getDeviceFunctionsRealDataByIds(Collections.singleton(DeviceIdParamVo.AC_GGD_METER_ID),
                acFunctionLogos, 2).getData().get(DeviceIdParamVo.AC_GGD_METER_ID);
        if (MapUtils.isNotEmpty(acRealDataMap)) {
            if (acRealDataMap.containsKey(FunctionParamVo.A_PHASE_VOLTAGE)) { //A相电压
                result.setVoltageA(DoubleUtil.objToDouble(acRealDataMap.get(FunctionParamVo.A_PHASE_VOLTAGE).getDataValue(), 2, null));
            }
            if (acRealDataMap.containsKey(FunctionParamVo.B_PHASE_VOLTAGE)) { //B相电压
                result.setVoltageB(DoubleUtil.objToDouble(acRealDataMap.get(FunctionParamVo.B_PHASE_VOLTAGE).getDataValue(), 2, null));
            }
            if (acRealDataMap.containsKey(FunctionParamVo.C_PHASE_VOLTAGE)) { //C相电压
                result.setVoltageC(DoubleUtil.objToDouble(acRealDataMap.get(FunctionParamVo.C_PHASE_VOLTAGE).getDataValue(), 2, null));
            }
            if (acRealDataMap.containsKey(FunctionParamVo.A_PHASE_CURRENT)) { //A相电流
                result.setCurrentA(DoubleUtil.objToDouble(acRealDataMap.get(FunctionParamVo.A_PHASE_CURRENT).getDataValue(), 2, null));
            }
            if (acRealDataMap.containsKey(FunctionParamVo.B_PHASE_CURRENT)) { //B相电流
                result.setCurrentB(DoubleUtil.objToDouble(acRealDataMap.get(FunctionParamVo.B_PHASE_CURRENT).getDataValue(), 2, null));
            }
            if (acRealDataMap.containsKey(FunctionParamVo.C_PHASE_CURRENT)) { //C相电流
                result.setCurrentC(DoubleUtil.objToDouble(acRealDataMap.get(FunctionParamVo.C_PHASE_CURRENT).getDataValue(), 2, null));
            }
            if (acRealDataMap.containsKey(FunctionParamVo.TOTAL_ACTIVE_POWER)) { //总有功功率
                result.setPower(DoubleUtil.objToDouble(acRealDataMap.get(FunctionParamVo.TOTAL_ACTIVE_POWER).getDataValue(), 2, null));
            }
        }
        //获取直流母线直流双向表实时数据
        String dcFunctionLogos = String.join(FileUtil.COMMA, FunctionParamVo.TOTAL_VOLTAGE, FunctionParamVo.TOTAL_CURRENT, FunctionParamVo.TOTAL_ACTIVE_POWER);
        Map<String, RealDataModel> dcRealDataMap = deviceService.getDeviceFunctionsRealDataByIds(Collections.singleton(DeviceIdParamVo.BUS_DC_METER_ID),
                dcFunctionLogos, 2).getData().get(DeviceIdParamVo.BUS_DC_METER_ID);
        if (MapUtils.isNotEmpty(dcRealDataMap)) {
            if (dcRealDataMap.containsKey(FunctionParamVo.TOTAL_VOLTAGE)) { //母线电压
                result.setBusVoltage(DoubleUtil.objToDouble(dcRealDataMap.get(FunctionParamVo.TOTAL_VOLTAGE).getDataValue(), 2, null));
            }
            if (dcRealDataMap.containsKey(FunctionParamVo.TOTAL_CURRENT)) { //母线电流
                result.setBusCurrent(DoubleUtil.objToDouble(dcRealDataMap.get(FunctionParamVo.TOTAL_CURRENT).getDataValue(), 2, null));
            }
            if (dcRealDataMap.containsKey(FunctionParamVo.TOTAL_ACTIVE_POWER)) { //母线功率
                result.setBusPower(DoubleUtil.objToDouble(dcRealDataMap.get(FunctionParamVo.TOTAL_ACTIVE_POWER).getDataValue(), 2, null));
            }
        }
        return result;
    }

    //获取光伏对象数据
    private CustomSystemWebSocketDto.Photovoltaic getPhotovoltaic() {
        //返回的对象
        CustomSystemWebSocketDto.Photovoltaic result = new CustomSystemWebSocketDto.Photovoltaic();
        //获取设备功能点数据
        Map<String, RealDataModel> realDataMap = Maps.newHashMap();

        Set<String> deviceIds = Collections.singleton(DeviceIdParamVo.PV_DC_DC_ID);
        String functionLogos = String.join(FileUtil.COMMA, FunctionParamVo.HIGH_TOTAL_VOLTAGE, FunctionParamVo.LOW_TOTAL_VOLTAGE,
                FunctionParamVo.LOW_TOTAL_CURRENT, FunctionParamVo.ACTIVE_POWER, FunctionParamVo.CURRENT_STATUS_OF_THE_INVERTER);
        realDataMap.putAll(deviceService.getDeviceFunctionsRealDataByIds(deviceIds, functionLogos, 2).getData()
                .getOrDefault(DeviceIdParamVo.PV_DC_DC_ID, Maps.newHashMap()));
        realDataMap.putAll(deviceService.getDeviceFunctionsRealDataByIds(deviceIds, FunctionParamVo.PV_TOTAL_BATTERY_GENERATION, 1)
                .getData().getOrDefault(DeviceIdParamVo.PV_DC_DC_ID, Maps.newHashMap()));
        if (MapUtils.isNotEmpty(realDataMap)) {
            if (realDataMap.containsKey(FunctionParamVo.HIGH_TOTAL_VOLTAGE)) { //母线电压
                result.setBusVoltage(DoubleUtil.objToDouble(realDataMap.get(FunctionParamVo.HIGH_TOTAL_VOLTAGE).getDataValue(), 2, null));
            }
            if (realDataMap.containsKey(FunctionParamVo.LOW_TOTAL_CURRENT)) { //母线电流
                result.setBusCurrent(DoubleUtil.objToDouble(realDataMap.get(FunctionParamVo.LOW_TOTAL_CURRENT).getDataValue(), 2, null));
            }
            if (realDataMap.containsKey(FunctionParamVo.LOW_TOTAL_VOLTAGE)) { //MPPT电压
                result.setMpptVoltage(DoubleUtil.objToDouble(realDataMap.get(FunctionParamVo.LOW_TOTAL_VOLTAGE).getDataValue(), 2, null));
            }
            if (realDataMap.containsKey(FunctionParamVo.ACTIVE_POWER)) { //功率
                result.setPower(DoubleUtil.objToDouble(realDataMap.get(FunctionParamVo.ACTIVE_POWER).getDataValue(), 2, null));
            }
            if (realDataMap.containsKey(FunctionParamVo.CURRENT_STATUS_OF_THE_INVERTER) && StringUtil.isNotEmpty(realDataMap.get(FunctionParamVo.CURRENT_STATUS_OF_THE_INVERTER).getDataValue())) { //运行状态
                String dataValue = String.valueOf(realDataMap.get(FunctionParamVo.CURRENT_STATUS_OF_THE_INVERTER).getDataValue());
                result.setRunStatus((int) Double.parseDouble(dataValue));
                //根据电池蔟id查询电池簇的运行状态枚举值数据
                Optional<ModelFunctionListDto> optional = deviceService.findDeviceFunctionListByIds(Collections
                                .singletonList(DeviceIdParamVo.PV_DC_DC_ID)).getData().values().stream().flatMap(Collection::stream)
                        .filter(m -> Objects.equals(FunctionParamVo.CURRENT_STATUS_OF_THE_INVERTER, m.getFunctionLogo()))
                        .findFirst();
                if (optional.isPresent()) {
                    ModelFunctionListDto modelFunction = optional.get();
                    if (StringUtil.isNotEmpty(modelFunction.getDataObject())) {
                        JSONArray enumArray = JSON.parseObject(modelFunction.getDataObject()).getJSONArray("enumArray");
                        if (CollectionUtils.isNotEmpty(enumArray)) {
                            for (Object enumObj : enumArray) {
                                JSONObject enumDto = JSON.parseObject(String.valueOf(enumObj));
                                if (enumDto != null && StringUtil.isNotEmpty(enumDto.get("id")) && Objects.equals(enumDto.getString("id"), dataValue)) {
                                    result.setRunStatusName(enumDto.getString("name"));
                                    break;
                                }
                            }
                        }
                    }
                }
            }
            if (realDataMap.containsKey(FunctionParamVo.PV_TOTAL_BATTERY_GENERATION)) { //光伏累计发电量
                result.setTotalQt(DoubleUtil.objToDouble(realDataMap.get(FunctionParamVo.PV_TOTAL_BATTERY_GENERATION).getDataValue(), 2));
            }
        }
        //获取光伏今日发电量
        DeviceHistoryQueryVo deviceQueryVo = new DeviceHistoryQueryVo();
        deviceQueryVo.setDeviceIds(Collections.singleton(DeviceIdParamVo.PV_DC_DC_ID));
        deviceQueryVo.setFunctionLogos(Collections.singleton(FunctionParamVo.PV_TOTAL_BATTERY_GENERATION));
        deviceQueryVo.setStartTime(DateUtil.getDayStart(DateUtil.localDateToStr(LocalDate.now())));
        deviceQueryVo.setEndTime(DateUtil.localDateTimeToStr(LocalDateTime.now()));
        deviceQueryVo.setTimeInterval("1d");
        Map<String, List<NodeDifHistoryDto>> deviceDataMap = dataService.findNodeDifHistoryListFeign(deviceQueryVo)
                .getData().get(DeviceIdParamVo.PV_DC_DC_ID);
        if (MapUtils.isNotEmpty(deviceDataMap)) { //当日发电量
            result.setDayQt(DoubleUtil.getToDouble(deviceDataMap.values().stream().flatMap(List::stream).mapToDouble(c ->
                    DoubleUtil.getObjSub(c.getFirstDataValue(), c.getLastDataValue())).sum()));
        }
        return result;
    }

    //获取储能对象数据
    private CustomSystemWebSocketDto.EnergyStorage getEnergyStorage() {
        //返回的对象
        CustomSystemWebSocketDto.EnergyStorage result = new CustomSystemWebSocketDto.EnergyStorage();
        //获取储能DC/DC的数据
        String seFunctionLogos = String.join(FileUtil.COMMA, FunctionParamVo.LOW_TOTAL_VOLTAGE, FunctionParamVo.LOW_TOTAL_CURRENT,
                FunctionParamVo.PCS_ACTIVE_POWER);
        Map<String, RealDataModel> seDcDcRealDataMap = Maps.newHashMap();
        seDcDcRealDataMap.putAll(deviceService.getDeviceFunctionsRealDataByIds(Collections.singleton(DeviceIdParamVo.SE_DC_DC_ID),
                seFunctionLogos, 2).getData().getOrDefault(DeviceIdParamVo.SE_DC_DC_ID, Maps.newHashMap()));
        seDcDcRealDataMap.putAll(deviceService.getDeviceFunctionsRealDataByIds(Collections.singleton(DeviceIdParamVo.SE_DC_DC_ID),
                        String.join(FileUtil.COMMA, FunctionParamVo.SE_TOTAL_BATTERY_CHARGE, FunctionParamVo.SE_TOTAL_BATTERY_DISCHARGE), 1)
                .getData().getOrDefault(DeviceIdParamVo.SE_DC_DC_ID, Maps.newHashMap()));
        if (MapUtils.isNotEmpty(seDcDcRealDataMap)) {
            if (seDcDcRealDataMap.containsKey(FunctionParamVo.LOW_TOTAL_VOLTAGE)) { //母线电压
                result.setBusVoltage(DoubleUtil.objToDouble(seDcDcRealDataMap.get(FunctionParamVo.LOW_TOTAL_VOLTAGE).getDataValue(), 2, null));
            }
            if (seDcDcRealDataMap.containsKey(FunctionParamVo.LOW_TOTAL_CURRENT)) { //母线电流
                result.setBusCurrent(DoubleUtil.objToDouble(seDcDcRealDataMap.get(FunctionParamVo.LOW_TOTAL_CURRENT).getDataValue(), 2, null));
            }
            if (seDcDcRealDataMap.containsKey(FunctionParamVo.PCS_ACTIVE_POWER)) { //功率
                result.setPower(DoubleUtil.objToDouble(seDcDcRealDataMap.get(FunctionParamVo.PCS_ACTIVE_POWER).getDataValue(), 2, null));
            }
            if (seDcDcRealDataMap.containsKey(FunctionParamVo.SE_TOTAL_BATTERY_CHARGE)) { //总充电量
                result.setTotalChargeQt(DoubleUtil.objToDouble(seDcDcRealDataMap.get(FunctionParamVo.SE_TOTAL_BATTERY_CHARGE).getDataValue(), 2));
            }
            if (seDcDcRealDataMap.containsKey(FunctionParamVo.SE_TOTAL_BATTERY_DISCHARGE)) { //总放电量
                result.setTotalDischargeQt(DoubleUtil.objToDouble(seDcDcRealDataMap.get(FunctionParamVo.SE_TOTAL_BATTERY_DISCHARGE).getDataValue(), 2));
            }
        }

        //获取储能电池蔟数据
        String batteryFunctionLogos = String.join(FileUtil.COMMA, FunctionParamVo.RUN_STATE, FunctionParamVo.SOC, FunctionParamVo.BATTERY_TOTAL_VOLTAGE);
        Map<String, RealDataModel> batteryRealDataMap = deviceService.getDeviceFunctionsRealDataByIds(Collections
                .singleton(DeviceIdParamVo.SE_BMS_ID), batteryFunctionLogos, 2).getData().get(DeviceIdParamVo.SE_BMS_ID);
        if (MapUtils.isNotEmpty(batteryRealDataMap)) {
            if (batteryRealDataMap.containsKey(FunctionParamVo.RUN_STATE)) { //运行模式
                String dataValue = String.valueOf(batteryRealDataMap.get(FunctionParamVo.RUN_STATE).getDataValue());
                if (StringUtil.isNotEmpty(dataValue)) {
                    result.setRunStatus((int) Double.parseDouble(dataValue));
                    //根据电池蔟id查询电池簇的运行状态枚举值数据
                    Optional<ModelFunctionListDto> optional = deviceService.findDeviceFunctionListByIds(Collections
                                    .singletonList(DeviceIdParamVo.SE_BMS_ID)).getData().values().stream().flatMap(Collection::stream)
                            .filter(m -> Objects.equals(FunctionParamVo.RUN_STATE, m.getFunctionLogo()))
                            .findFirst();
                    if (optional.isPresent()) {
                        ModelFunctionListDto modelFunction = optional.get();
                        if (StringUtil.isNotEmpty(modelFunction.getDataObject())) {
                            JSONArray enumArray = JSON.parseObject(modelFunction.getDataObject()).getJSONArray("enumArray");
                            if (CollectionUtils.isNotEmpty(enumArray)) {
                                for (Object enumObj : enumArray) {
                                    JSONObject enumDto = JSON.parseObject(String.valueOf(enumObj));
                                    if (enumDto != null && StringUtil.isNotEmpty(enumDto.get("id")) && Objects.equals(enumDto.getString("id"), dataValue)) {
                                        result.setRunStatusName(enumDto.getString("name"));
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if (batteryRealDataMap.containsKey(FunctionParamVo.SOC)) { //SOC(%)
                result.setSoc(DoubleUtil.objToDouble(batteryRealDataMap.get(FunctionParamVo.SOC).getDataValue(), 2, null));
            }
            if (batteryRealDataMap.containsKey(FunctionParamVo.BATTERY_TOTAL_VOLTAGE)) { //电池总电压(V)
                result.setBatteryVoltage(DoubleUtil.objToDouble(batteryRealDataMap.get(FunctionParamVo.BATTERY_TOTAL_VOLTAGE).getDataValue(), 2, null));
            }
        }

        //获取储能今日充放电量
        DeviceHistoryQueryVo deviceQueryVo = new DeviceHistoryQueryVo();
        deviceQueryVo.setDeviceIds(Collections.singleton(DeviceIdParamVo.SE_DC_DC_ID));
        deviceQueryVo.setFunctionLogos(new HashSet<>(Arrays.asList(FunctionParamVo.SE_TOTAL_BATTERY_CHARGE, FunctionParamVo.SE_TOTAL_BATTERY_DISCHARGE)));
        deviceQueryVo.setStartTime(DateUtil.getDayStart(DateUtil.localDateToStr(LocalDate.now())));
        deviceQueryVo.setEndTime(DateUtil.localDateTimeToStr(LocalDateTime.now()));
        deviceQueryVo.setTimeInterval("1d");
        Map<String, List<NodeDifHistoryDto>> deviceDataMap = dataService.findNodeDifHistoryListFeign(deviceQueryVo)
                .getData().get(DeviceIdParamVo.SE_DC_DC_ID);
        if (MapUtils.isNotEmpty(deviceDataMap)) { //当日发电量
            Map<String, Double> dataMap = deviceDataMap.values().stream().flatMap(List::stream).collect(Collectors
                    .groupingBy(NodeDifHistoryDto::getFunctionLogo, Collectors.summingDouble(c ->
                            DoubleUtil.getObjSub(c.getFirstDataValue(), c.getLastDataValue()))));
            if (dataMap.containsKey(FunctionParamVo.SE_TOTAL_BATTERY_CHARGE)) { //今日充电量
                result.setChargeQt(DoubleUtil.getToDouble(dataMap.get(FunctionParamVo.SE_TOTAL_BATTERY_CHARGE)));
            }
            if (dataMap.containsKey(FunctionParamVo.SE_TOTAL_BATTERY_DISCHARGE)) { //今日放电量
                result.setDischargeQt(DoubleUtil.getToDouble(dataMap.get(FunctionParamVo.SE_TOTAL_BATTERY_DISCHARGE)));
            }
        }

        return result;
    }

    //获取负载数据
    private CustomSystemWebSocketDto.Load getLoad() {
        //返回的对象
        CustomSystemWebSocketDto.Load result = new CustomSystemWebSocketDto.Load();
        //获取负载数据
        Map<String, RealDataModel> realDataMap = Maps.newHashMap();
        String functionLogos = String.join(FileUtil.COMMA, FunctionParamVo.TOTAL_VOLTAGE, FunctionParamVo.TOTAL_CURRENT,
                FunctionParamVo.TOTAL_ACTIVE_POWER);
        realDataMap.putAll(deviceService.getDeviceFunctionsRealDataByIds(Collections.singleton(DeviceIdParamVo.DC_GGD_LOAD_ID),
                functionLogos, 2).getData().getOrDefault(DeviceIdParamVo.DC_GGD_LOAD_ID, Maps.newHashMap()));
        realDataMap.putAll(deviceService.getDeviceFunctionsRealDataByIds(Collections.singleton(DeviceIdParamVo.DC_GGD_LOAD_ID),
                        FunctionParamVo.TOTAL_POSITIVE_ACTIVE_ENERGY, 1).getData()
                .getOrDefault(DeviceIdParamVo.DC_GGD_LOAD_ID, Maps.newHashMap()));
        if (MapUtils.isNotEmpty(realDataMap)) {
            if (realDataMap.containsKey(FunctionParamVo.TOTAL_VOLTAGE)) { //母线电压
                result.setBusVoltage(DoubleUtil.objToDouble(realDataMap.get(FunctionParamVo.TOTAL_VOLTAGE).getDataValue(), 2, null));
            }
            if (realDataMap.containsKey(FunctionParamVo.TOTAL_CURRENT)) { //母线电流
                result.setBusCurrent(DoubleUtil.objToDouble(realDataMap.get(FunctionParamVo.TOTAL_CURRENT).getDataValue(), 2, null));
            }
            if (realDataMap.containsKey(FunctionParamVo.TOTAL_ACTIVE_POWER)) { //功率
                result.setPower(DoubleUtil.objToDouble(realDataMap.get(FunctionParamVo.TOTAL_ACTIVE_POWER).getDataValue(), 2, null));
            }
            if (realDataMap.containsKey(FunctionParamVo.TOTAL_POSITIVE_ACTIVE_ENERGY)) { //累计总电量
                result.setTotalQt(DoubleUtil.objToDouble(realDataMap.get(FunctionParamVo.TOTAL_POSITIVE_ACTIVE_ENERGY).getDataValue(), 2));
            }
        }

        return result;
    }


    private Map<String, Double> getPowerDataMap(String deviceId, String startTime, String endTime, String timeInterval, String functionLogo) {
        //返回的map数据
        Map<String, Double> resultMap = Maps.newHashMap();

        //定义查询实体类
        DeviceHistoryQueryVo deviceQueryVo = new DeviceHistoryQueryVo();
        deviceQueryVo.setDeviceIds(Collections.singleton(deviceId));
        deviceQueryVo.setFunctionLogos(Collections.singleton(functionLogo));
        deviceQueryVo.setStartTime(startTime);
        deviceQueryVo.setEndTime(endTime);
        deviceQueryVo.setTimeInterval(timeInterval);
        //查询设备功能点数据
        Map<String, List<DeviceHistoryDto>> deviceDataMap = dataService.findDeviceHistoryValueList(deviceQueryVo)
                .getData().get(deviceId);
        if (MapUtils.isNotEmpty(deviceDataMap) && deviceDataMap.containsKey(functionLogo)) {
            resultMap = deviceDataMap.get(functionLogo).stream().filter(i -> StringUtil.isNotEmpty(i.getDataValue()))
                    .collect(Collectors.groupingBy(DeviceHistoryDto::getDateTime, Collectors.summingDouble(i ->
                            DoubleUtil.objToDouble(i.getDataValue()))));
        }
        return resultMap;
    }

}
