package com.sunmax.device.util;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.google.common.collect.Maps;
import com.sunmax.common.dto.data.NodeDifHistoryDto;
import com.sunmax.common.dto.operate.OrderCountDto;
import com.sunmax.common.entity.BaseEntity;
import com.sunmax.common.model.RealDataModel;
import com.sunmax.common.util.DateUtil;
import com.sunmax.common.util.DoubleUtil;
import com.sunmax.common.util.StringUtil;
import com.sunmax.common.vo.FunctionLogoParamVo;
import com.sunmax.common.vo.ReaFieldParamVo;
import com.sunmax.common.vo.data.DeviceHistoryQueryVo;
import com.sunmax.device.dao.access.DeviceDao;
import com.sunmax.device.dao.access.DeviceGunDao;
import com.sunmax.device.dto.TopItemDto;
import com.sunmax.device.entity.access.DeviceEntity;
import com.sunmax.device.entity.access.DeviceGunEntity;
import com.sunmax.device.service.feign.DataService;
import com.sunmax.device.service.feign.TogetherService;
import com.sunmax.device.vo.TopNodeParamVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.compress.utils.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Configuration
@Slf4j
public class TopNodeDataUtil {

    @Autowired
    private DataService dataService;

    @Autowired
    private DeviceDao deviceDao;

    @Autowired
    private DeviceGunDao deviceGunDao;

    @Autowired
    private TogetherService togetherService;

    /**
     * 获取拓扑节点数据的节点类型
     */
    public static List<Integer> getNodeTypeList() {
        return Arrays.asList(4, 5, 6, 8, 10, 11);
    }

    /**
     * 获取拓扑节点默认数据项列表
     *
     * @param type 节点类型 1-拓扑点 2-电网 3-变压器 4-关口点 5-计量点 6-逆变器 7-光伏组件 8-储能柜 9-负荷 10-充电桩 11-开关 12-车辆 13-换电站
     * @return 默认数据项列表
     */
    public static List<TopItemDto> getTopItemList(Integer type) {
        //返回的集合
        List<TopItemDto> resultList = Lists.newArrayList();
        switch (type) {
//            case 1:
//                break;
//            case 2:
//                break;
//            case 3:
//                break;
            case 4:
                resultList.add(TopItemDto.builder().dataCode(TopNodeParamVo.TOTAL_ACTIVE_POWER).dataName("总有功功率(kW)").showName("总有功功率(kW)").showType(2).positionType(2).build());
                resultList.add(TopItemDto.builder().dataCode(TopNodeParamVo.DAY_SUP_QT).dataName("今日正向有功电量(kWh)").showName("今日正向有功电量(kWh)").showType(2).positionType(2).build());
                resultList.add(TopItemDto.builder().dataCode(TopNodeParamVo.DAY_REV_QT).dataName("今日反向有功电量(kWh)").showName("今日反向有功电量(kWh)").showType(2).positionType(2).build());
                resultList.add(TopItemDto.builder().dataCode(TopNodeParamVo.TOTAL_SUP_QT).dataName("累计正向有功电量(kWh)").showName("累计正向有功电量(kWh)").showType(2).positionType(2).build());
                resultList.add(TopItemDto.builder().dataCode(TopNodeParamVo.TOTAL_REV_QT).dataName("累计反向有功电量(kWh)").showName("累计反向有功电量(kWh)").showType(2).positionType(2).build());
                break;
            case 5:
                resultList.add(TopItemDto.builder().dataCode(TopNodeParamVo.TOTAL_ACTIVE_POWER).dataName("总有功功率(kW)").showName("总有功功率(kW)").showType(2).positionType(2).build());
                resultList.add(TopItemDto.builder().dataCode(TopNodeParamVo.DAY_SUP_QT).dataName("今日正向有功电量(kWh)").showName("今日正向有功电量(kWh)").showType(2).positionType(2).build());
                resultList.add(TopItemDto.builder().dataCode(TopNodeParamVo.DAY_REV_QT).dataName("今日反向有功电量(kWh)").showName("今日反向有功电量(kWh)").showType(2).positionType(2).build());
                resultList.add(TopItemDto.builder().dataCode(TopNodeParamVo.TOTAL_SUP_QT).dataName("累计正向有功电量(kWh)").showName("累计正向有功电量(kWh)").showType(2).positionType(2).build());
                resultList.add(TopItemDto.builder().dataCode(TopNodeParamVo.TOTAL_REV_QT).dataName("累计反向有功电量(kWh)").showName("累计反向有功电量(kWh)").showType(2).positionType(2).build());
                break;
            case 6:
                resultList.add(TopItemDto.builder().dataCode(TopNodeParamVo.TOTAL_ACTIVE_POWER).dataName("总有功功率(kW)").showName("总有功功率(kW)").showType(2).positionType(2).build());
                resultList.add(TopItemDto.builder().dataCode(TopNodeParamVo.DAY_CHARGE_QT).dataName("今日发电量(kWh)").showName("今日发电量(kWh)").showType(2).positionType(2).build());
                resultList.add(TopItemDto.builder().dataCode(TopNodeParamVo.TOTAL_CHARGE_QT).dataName("累计发电量(kWh)").showName("累计发电量(kWh)").showType(2).positionType(2).build());
                break;
//            case 7:
//                break;
            case 8:
                resultList.add(TopItemDto.builder().dataCode(TopNodeParamVo.TOTAL_ACTIVE_POWER).dataName("总有功功率(kW)").showName("总有功功率(kW)").showType(2).positionType(2).build());
                resultList.add(TopItemDto.builder().dataCode(TopNodeParamVo.DAY_ABLE_CHARGE_QT).dataName("今日可充电量(kWh)").showName("今日可充电量(kWh)").showType(2).positionType(2).build());
                resultList.add(TopItemDto.builder().dataCode(TopNodeParamVo.DAY_ABLE_DISCHARGE_QT).dataName("今日可放电量(kWh)").showName("今日可放电量(kWh)").showType(2).positionType(2).build());
                resultList.add(TopItemDto.builder().dataCode(TopNodeParamVo.DAY_CHARGE_QT).dataName("今日充电量(kWh)").showName("今日充电量(kWh)").showType(2).positionType(2).build());
                resultList.add(TopItemDto.builder().dataCode(TopNodeParamVo.DAY_DISCHARGE_QT).dataName("今日放电量(kWh)").showName("今日放电量(kWh)").showType(2).positionType(2).build());
                resultList.add(TopItemDto.builder().dataCode(TopNodeParamVo.TOTAL_CHARGE_QT).dataName("累计充电量(kWh)").showName("累计充电量(kWh)").showType(2).positionType(2).build());
                resultList.add(TopItemDto.builder().dataCode(TopNodeParamVo.TOTAL_DISCHARGE_QT).dataName("累计放电量(kWh)").showName("累计放电量(kWh)").showType(2).positionType(2).build());
                resultList.add(TopItemDto.builder().dataCode(TopNodeParamVo.SOC).dataName("SOC(%)").showName("SOC(%)").showType(2).positionType(2).build());
                break;
//            case 9:
//                break;
            case 10:
                resultList.add(TopItemDto.builder().dataCode(TopNodeParamVo.TOTAL_ACTIVE_POWER).dataName("总有功功率(kW)").showName("总有功功率(kW)").showType(2).positionType(2).build());
                resultList.add(TopItemDto.builder().dataCode(TopNodeParamVo.RECHARGE_POWER).dataName("充电功率(kW)").showName("充电功率(kW)").showType(2).positionType(2).build());
                resultList.add(TopItemDto.builder().dataCode(TopNodeParamVo.DISCHARGE_POWER).dataName("V2G功率(kW)").showName("V2G功率(kW)").showType(2).positionType(2).build());
                resultList.add(TopItemDto.builder().dataCode(TopNodeParamVo.DAY_CHARGE_QT).dataName("今日充电量(kWh)").showName("今日充电量(kWh)").showType(2).positionType(2).build());
                resultList.add(TopItemDto.builder().dataCode(TopNodeParamVo.DAY_DISCHARGE_QT).dataName("今日V2G电量(kWh)").showName("今日V2G电量(kWh)").showType(2).positionType(2).build());
                resultList.add(TopItemDto.builder().dataCode(TopNodeParamVo.TOTAL_CHARGE_QT).dataName("累计充电量(kWh)").showName("累计充电量(kWh)").showType(2).positionType(2).build());
                resultList.add(TopItemDto.builder().dataCode(TopNodeParamVo.TOTAL_DISCHARGE_QT).dataName("累计V2G电量(kWh)").showName("累计V2G电量(kWh)").showType(2).positionType(2).build());
                break;
            case 11:
                resultList.add(TopItemDto.builder().dataCode(TopNodeParamVo.SWITCH_STATUS).dataName("开关状态").showName("开关状态").showType(2).positionType(2).build());
                break;
//            case 12:
//                break;
            case 13:
                resultList.add(TopItemDto.builder().dataCode(TopNodeParamVo.TOTAL_ACTIVE_POWER).dataName("总有功功率(kW)").showName("总有功功率(kW)").showType(2).positionType(2).build());
                resultList.add(TopItemDto.builder().dataCode(TopNodeParamVo.DAY_CHARGE_QT).dataName("今日充电量(kWh)").showName("今日充电量(kWh)").showType(2).positionType(2).build());
                resultList.add(TopItemDto.builder().dataCode(TopNodeParamVo.REMAIN_QT).dataName("剩余电量(kWh)").showName("剩余电量(kWh)").showType(2).positionType(2).build());
                resultList.add(TopItemDto.builder().dataCode(TopNodeParamVo.TOTAL_CHARGE_QT).dataName("累计充电量(kWh)").showName("累计充电量(kWh)").showType(2).positionType(2).build());
                break;
        }
        return resultList;
    }

    /**
     * 获取关口节点/计量节点的数据
     *
     * @param nodeDeviceIdMap 节点设备id数据  节点id -> 多个设备id
     * @return 关口节点数据 节点id -> (数据编号 -> 数据值)
     */
    public Map<String, Map<String, Double>> getGatewayNodeData(Map<String, Set<String>> nodeDeviceIdMap) {
        //返回的对象
        Map<String, Map<String, Double>> resultMap = Maps.newHashMap();

        if (MapUtils.isNotEmpty(nodeDeviceIdMap)) {
            //根据多个设备id查询设备总有功功率,今日正向有功电量,今日反向有功电量,累计正向有功电量,累计反向有功电量
            Set<String> deviceIdSet = nodeDeviceIdMap.values().stream().flatMap(Collection::stream).collect(Collectors.toSet());
            //今日正向有功电量,今日反向有功电量
            DeviceHistoryQueryVo deviceQueryVo = new DeviceHistoryQueryVo();
            deviceQueryVo.setDeviceIds(deviceIdSet);
            deviceQueryVo.setFunctionLogos(new HashSet<>(Arrays.asList(FunctionLogoParamVo.TOTAL_POSITIVE_ACTIVE_ENERGY, FunctionLogoParamVo.TOTAL_INVERSE_ACTIVE_ENERGY)));
            deviceQueryVo.setStartTime(DateUtil.localDateTimeToStr(LocalDateTime.now().with(LocalTime.MIN)));
            deviceQueryVo.setEndTime(DateUtil.localDateTimeToStr(LocalDateTime.now()));
            deviceQueryVo.setTimeInterval("1d");
            Map<String, Map<String, List<NodeDifHistoryDto>>> deviceHistoryMap = dataService.findNodeDifHistoryListFeign(deviceQueryVo).getData();

            //设备总有功功率,累计正向有功电量,累计反向有功电量
            Set<String> functionLogos = new HashSet<>(Arrays.asList(FunctionLogoParamVo.TOTAL_ACTIVE_POWER, FunctionLogoParamVo.TOTAL_POSITIVE_ACTIVE_ENERGY,
                    FunctionLogoParamVo.TOTAL_INVERSE_ACTIVE_ENERGY));
            Map<String, Map<String, RealDataModel>> deviceRealMap = DeviceCommonUtil.getDeviceFunctions(deviceIdSet, functionLogos);

            //对设备拓扑节点数据进行组装
            for (Map.Entry<String, Set<String>> entry : nodeDeviceIdMap.entrySet()) {
                String nodeId = entry.getKey(); //节点id
                Set<String> deviceIds = entry.getValue(); //多个设备id
                Map<String, Double> dataMap = Maps.newHashMap(); //数据对象

                //今日正向有功电量,今日反向有功电量
                Map<String, Double> functionMap = deviceIds.stream().filter(deviceHistoryMap::containsKey)
                        .flatMap(d -> deviceHistoryMap.get(d).values().stream().flatMap(Collection::stream))
                        .filter(d -> StringUtil.isNotEmpty(d.getFirstDateTime()) && StringUtil.isNotEmpty(d.getFirstDataValue()) && StringUtil.isNotEmpty(d.getLastDataValue()))
                        .collect(Collectors.groupingBy(NodeDifHistoryDto::getFunctionLogo, Collectors.summingDouble(d ->
                                Double.parseDouble(String.valueOf(d.getLastDataValue())) - Double.parseDouble(String.valueOf(d.getFirstDataValue())))));
                dataMap.put(TopNodeParamVo.DAY_SUP_QT, DoubleUtil.getAbsDouble(functionMap.get(FunctionLogoParamVo.TOTAL_POSITIVE_ACTIVE_ENERGY)));
                dataMap.put(TopNodeParamVo.DAY_REV_QT, DoubleUtil.getAbsDouble(functionMap.get(FunctionLogoParamVo.TOTAL_INVERSE_ACTIVE_ENERGY)));

                //设备总有功功率,累计正向有功电量,累计反向有功电量
                double totalActivePower = 0.0;
                double totalSupQt = 0.0;
                double totalRevQt = 0.0;
                List<Map<String, RealDataModel>> realDataList = deviceIds.stream().filter(deviceRealMap::containsKey).map(deviceRealMap::get).collect(Collectors.toList());
                for (Map<String, RealDataModel> entity : realDataList) {
                    //总有功功率
                    if (entity.containsKey(FunctionLogoParamVo.TOTAL_ACTIVE_POWER) && StringUtil.isNotEmpty(entity.get(FunctionLogoParamVo.TOTAL_ACTIVE_POWER).getDataValue())) {
                        totalActivePower += Double.parseDouble(String.valueOf(entity.get(FunctionLogoParamVo.TOTAL_ACTIVE_POWER).getDataValue()));
                    }
                    //累计正向有功电量
                    if (entity.containsKey(FunctionLogoParamVo.TOTAL_POSITIVE_ACTIVE_ENERGY) && StringUtil.isNotEmpty(entity.get(FunctionLogoParamVo.TOTAL_POSITIVE_ACTIVE_ENERGY).getDataValue())) {
                        totalSupQt += Double.parseDouble(String.valueOf(entity.get(FunctionLogoParamVo.TOTAL_POSITIVE_ACTIVE_ENERGY).getDataValue()));
                    }
                    //累计反向有功电量
                    if (entity.containsKey(FunctionLogoParamVo.TOTAL_INVERSE_ACTIVE_ENERGY) && StringUtil.isNotEmpty(entity.get(FunctionLogoParamVo.TOTAL_INVERSE_ACTIVE_ENERGY).getDataValue())) {
                        totalRevQt += Double.parseDouble(String.valueOf(entity.get(FunctionLogoParamVo.TOTAL_INVERSE_ACTIVE_ENERGY).getDataValue()));
                    }
                }
                dataMap.put(TopNodeParamVo.TOTAL_ACTIVE_POWER, DoubleUtil.getToDouble(totalActivePower));
                dataMap.put(TopNodeParamVo.TOTAL_SUP_QT, DoubleUtil.getToDouble(totalSupQt));
                dataMap.put(TopNodeParamVo.TOTAL_REV_QT, DoubleUtil.getToDouble(totalRevQt));
                resultMap.put(nodeId, dataMap);
            }
        }
        return resultMap;
    }

    /**
     * 获取逆变器节点的数据
     *
     * @param nodeDeviceIdMap 节点设备id数据  节点id -> 多个设备id
     * @return 逆变器节点数据 节点id -> (数据编号 -> 数据值)
     */
    public Map<String, Map<String, Double>> getInverterNodeData(Map<String, Set<String>> nodeDeviceIdMap) {
        //返回的对象
        Map<String, Map<String, Double>> resultMap = Maps.newHashMap();
        if (MapUtils.isNotEmpty(nodeDeviceIdMap)) {
            //根据多个设备id查询逆变器总有功功率,今日发电量,累计发电量数据
            Set<String> deviceIdSet = nodeDeviceIdMap.values().stream().flatMap(Collection::stream).collect(Collectors.toSet());

            //根据多个设备id查询逆变器总有功功率,累计发电量数据
            Map<String, Map<String, RealDataModel>> realPowerMap = DeviceCommonUtil.getDeviceFunctions(deviceIdSet, Collections
                    .singleton(FunctionLogoParamVo.ACTIVE_POWER));
            Map<String, Map<String, RealDataModel>> realQtMap = DeviceCommonUtil.getRawDeviceFunctions(deviceIdSet, Collections
                    .singleton(FunctionLogoParamVo.TOTAL_POWER_GENERATION));

            //根据多个设备id查询逆变器今日发电量
            DeviceHistoryQueryVo deviceQueryVo = new DeviceHistoryQueryVo();
            deviceQueryVo.setDeviceIds(deviceIdSet);
            deviceQueryVo.setFunctionLogos(Collections.singleton(FunctionLogoParamVo.TOTAL_POWER_GENERATION));
            deviceQueryVo.setStartTime(DateUtil.localDateTimeToStr(LocalDateTime.now().with(LocalTime.MIN)));
            deviceQueryVo.setEndTime(DateUtil.localDateTimeToStr(LocalDateTime.now()));
            deviceQueryVo.setTimeInterval("1d");
            Map<String, Map<String, List<NodeDifHistoryDto>>> dayQtMap = dataService.findNodeDifHistoryListFeign(deviceQueryVo).getData();

            //对设备拓扑节点数据进行组装
            for (Map.Entry<String, Set<String>> entry : nodeDeviceIdMap.entrySet()) {
                String nodeId = entry.getKey(); //节点id
                Set<String> deviceIds = entry.getValue(); //多个设备id
                Map<String, Double> dataMap = Maps.newHashMap(); //数据对象

                //逆变器总有功功率,今日发电量,累计发电量数据
                double totalActivePower = 0.0;
                double dayChargeQt = 0.0;
                double totalChargeQt = 0.0;
                for (String deviceId : deviceIds) {
                    //实时功率
                    if (realPowerMap.containsKey(deviceId) && realPowerMap.get(deviceId).containsKey(FunctionLogoParamVo.ACTIVE_POWER)) {
                        totalActivePower += DoubleUtil.objToDouble(realPowerMap.get(deviceId).get(FunctionLogoParamVo.ACTIVE_POWER).getDataValue());
                    }
                    //累计发电量
                    if (realQtMap.containsKey(deviceId) && realQtMap.get(deviceId).containsKey(FunctionLogoParamVo.TOTAL_POWER_GENERATION)) {
                        totalChargeQt += DoubleUtil.objToDouble(realQtMap.get(deviceId).get(FunctionLogoParamVo.TOTAL_POWER_GENERATION).getDataValue());
                    }
                    //今日发电量
                    if (dayQtMap.containsKey(deviceId) && dayQtMap.get(deviceId).containsKey(FunctionLogoParamVo.TOTAL_POWER_GENERATION)) {
                        List<NodeDifHistoryDto> dataList = dayQtMap.get(deviceId).get(FunctionLogoParamVo.TOTAL_POWER_GENERATION);
                        dayChargeQt += dataList.stream().mapToDouble(d -> DoubleUtil.getObjSub(d.getFirstDataValue(), d.getLastDataValue())).sum();
                    }
                }
                dataMap.put(TopNodeParamVo.TOTAL_ACTIVE_POWER, DoubleUtil.getToDouble(totalActivePower));
                dataMap.put(TopNodeParamVo.DAY_CHARGE_QT, DoubleUtil.getToDouble(dayChargeQt));
                dataMap.put(TopNodeParamVo.TOTAL_CHARGE_QT, DoubleUtil.getToDouble(totalChargeQt));
                resultMap.put(nodeId, dataMap);
            }
        }
        return resultMap;
    }

    /**
     * 获取储能柜PCS节点的数据
     *
     * @param nodeDeviceIdMap 节点设备id数据  节点id -> 节点设备id
     * @return 设备节点数据 节点id -> (数据编号 -> 数据值)
     */
    public Map<String, Map<String, Double>> getPcsNodeData(Map<String, Set<String>> nodeDeviceIdMap) {
        //返回的对象
        Map<String, Map<String, Double>> resultMap = Maps.newHashMap();
        if (MapUtils.isNotEmpty(nodeDeviceIdMap)) {
            Set<String> deviceIdSet = nodeDeviceIdMap.values().stream().flatMap(Collection::stream).collect(Collectors.toSet());

            //PCS今日充电量,今日放电量
            DeviceHistoryQueryVo deviceQueryVo = new DeviceHistoryQueryVo();
            deviceQueryVo.setDeviceIds(deviceIdSet);
            deviceQueryVo.setFunctionLogos(new HashSet<>(Arrays.asList(FunctionLogoParamVo.PCS_BATTERY_CHARGE, FunctionLogoParamVo.PCS_BATTERY_DISCHARGE)));
            deviceQueryVo.setStartTime(DateUtil.localDateTimeToStr(LocalDateTime.now().with(LocalTime.MIN)));
            deviceQueryVo.setEndTime(DateUtil.localDateTimeToStr(LocalDateTime.now()));
            deviceQueryVo.setTimeInterval("1d");
            Map<String, Map<String, List<NodeDifHistoryDto>>> deviceHistoryMap = dataService.findNodeDifHistoryListFeign(deviceQueryVo).getData();

            //根据多个设备id查询储能PCS交流有功功率，累计充电量，累计放电量
            Map<String, Map<String, RealDataModel>> realPowerMap = DeviceCommonUtil.getDeviceFunctions(deviceIdSet, Collections
                    .singleton(FunctionLogoParamVo.PCS_ACTIVE_POWER));
            Map<String, Map<String, RealDataModel>> realQtMap = DeviceCommonUtil.getRawDeviceFunctions(deviceIdSet, new HashSet<>(Arrays
                    .asList(FunctionLogoParamVo.PCS_BATTERY_CHARGE, FunctionLogoParamVo.PCS_BATTERY_DISCHARGE)));
            //对设备拓扑节点数据进行组装
            for (Map.Entry<String, Set<String>> entry : nodeDeviceIdMap.entrySet()) {
                String nodeId = entry.getKey(); //节点id
                Set<String> deviceIds = entry.getValue(); //多个设备id
                Map<String, Double> dataMap = Maps.newHashMap(); //数据对象

                //电池簇今日充电量,今日放电量
                Map<String, Double> functionMap = deviceIds.stream().filter(deviceHistoryMap::containsKey)
                        .flatMap(d -> deviceHistoryMap.get(d).values().stream().flatMap(Collection::stream))
                        .filter(d -> StringUtil.isNotEmpty(d.getFirstDateTime()))
                        .collect(Collectors.groupingBy(NodeDifHistoryDto::getFunctionLogo,
                                Collectors.summingDouble(d -> DoubleUtil.getObjSub(d.getFirstDataValue(), d.getLastDataValue()))));
                dataMap.put(TopNodeParamVo.DAY_CHARGE_QT, DoubleUtil.getToDouble(functionMap.get(FunctionLogoParamVo.PCS_BATTERY_CHARGE)));
                dataMap.put(TopNodeParamVo.DAY_DISCHARGE_QT, DoubleUtil.getToDouble(functionMap.get(FunctionLogoParamVo.PCS_BATTERY_DISCHARGE)));

                //储能PCS交流有功功率
                double totalActivePower = 0.0;
                double totalChargeQt = 0.0;
                double totalDischargeQt = 0.0;
                for (String deviceId : deviceIds) {
                    //实时功率
                    if (realPowerMap.containsKey(deviceId) && realPowerMap.get(deviceId).containsKey(FunctionLogoParamVo.PCS_ACTIVE_POWER)) {
                        totalActivePower += DoubleUtil.objToDouble(realPowerMap.get(deviceId).get(FunctionLogoParamVo.PCS_ACTIVE_POWER).getDataValue());
                    }
                    if (realQtMap.containsKey(deviceId)) {
                        Map<String, RealDataModel> realDataMap = realQtMap.get(deviceId);
                        //累计充电量
                        if (realDataMap.containsKey(FunctionLogoParamVo.PCS_BATTERY_CHARGE)) {
                            totalChargeQt += DoubleUtil.objToDouble(realDataMap.get(FunctionLogoParamVo.PCS_BATTERY_CHARGE).getDataValue());
                        }
                        //累计放电量
                        if (realDataMap.containsKey(FunctionLogoParamVo.PCS_BATTERY_DISCHARGE)) {
                            totalDischargeQt += DoubleUtil.objToDouble(realDataMap.get(FunctionLogoParamVo.PCS_BATTERY_DISCHARGE).getDataValue());
                        }
                    }
                }
                dataMap.put(TopNodeParamVo.TOTAL_ACTIVE_POWER, DoubleUtil.getToDouble(totalActivePower));
                dataMap.put(TopNodeParamVo.TOTAL_CHARGE_QT, DoubleUtil.getToDouble(totalChargeQt));
                dataMap.put(TopNodeParamVo.TOTAL_DISCHARGE_QT, DoubleUtil.getToDouble(totalDischargeQt));
                resultMap.put(nodeId, dataMap);
            }
        }
        return resultMap;
    }

    /**
     * 获取储能柜电池簇节点的数据
     *
     * @param nodeDeviceIdMap 节点设备id数据  节点id -> 节点设备id
     * @param nodeParaMap     节点参数数据  节点id -> 多柜并机运行
     * @return 设备节点数据 节点id -> (数据编号 -> 数据值)
     */
    public Map<String, Map<String, Double>> getBatteryNodeData(Map<String, Set<String>> nodeDeviceIdMap, Map<String, Integer> nodeParaMap) {
        //返回的对象
        Map<String, Map<String, Double>> resultMap = Maps.newHashMap();
        if (MapUtils.isNotEmpty(nodeDeviceIdMap)) {
            //根据多个设备id查询电池簇可充电量,可放电量,今日充电量,今日放电量,累计充电量,累计放电量
            Set<String> deviceIdSet = nodeDeviceIdMap.values().stream().flatMap(Collection::stream).collect(Collectors.toSet());

            //电池簇可充电量,可放电量,累计充电量,累计放电量,当前SOC
            Set<String> functionLogos = new HashSet<>(Arrays.asList(FunctionLogoParamVo.ACCCHARGEQ, FunctionLogoParamVo.ACCDISCHARGEQ,
                    FunctionLogoParamVo.BATTERY_ENABLE_CHARGE_Q, FunctionLogoParamVo.BATTERY_ENABLE_DISCHARGE_Q, FunctionLogoParamVo.BATTERY_TOTAL_SOC));
            Map<String, Map<String, RealDataModel>> deviceRealMap = DeviceCommonUtil.getDeviceFunctions(deviceIdSet, functionLogos);

            //根据多个设备id查询电池簇总容量
            Map<String, String> deviceObjectMap = deviceDao.findAllByIdInAndIsDelete(deviceIdSet, 1).stream()
                    .filter(d -> StringUtil.isNotEmpty(d.getReadwriteObject()))
                    .collect(Collectors.toMap(DeviceEntity::getId, DeviceEntity::getReadwriteObject));

            for (Map.Entry<String, Set<String>> entry : nodeDeviceIdMap.entrySet()) {
                String nodeId = entry.getKey(); //节点id
                Set<String> deviceIds = entry.getValue(); //多个设备id
                Map<String, Double> dataMap = Maps.newHashMap(); //数据对象

                //电池簇可充电量,可放电量
                double dayAbleChargeQt = 0.0;
                double dayAbleDischargeQt = 0.0;
                List<Double> socList = Lists.newArrayList();
                List<Map<String, RealDataModel>> realDataList = deviceIds.stream().filter(deviceRealMap::containsKey).map(deviceRealMap::get).collect(Collectors.toList());
                for (Map<String, RealDataModel> entity : realDataList) {
                    //可充电量
                    if (entity.containsKey(FunctionLogoParamVo.BATTERY_ENABLE_CHARGE_Q) && StringUtil.isNotEmpty(entity.get(FunctionLogoParamVo.BATTERY_ENABLE_CHARGE_Q).getDataValue())) {
                        dayAbleChargeQt += Double.parseDouble(String.valueOf(entity.get(FunctionLogoParamVo.BATTERY_ENABLE_CHARGE_Q).getDataValue()));
                    }
                    //可放电量
                    if (entity.containsKey(FunctionLogoParamVo.BATTERY_ENABLE_DISCHARGE_Q) && StringUtil.isNotEmpty(entity.get(FunctionLogoParamVo.BATTERY_ENABLE_DISCHARGE_Q).getDataValue())) {
                        dayAbleDischargeQt += Double.parseDouble(String.valueOf(entity.get(FunctionLogoParamVo.BATTERY_ENABLE_DISCHARGE_Q).getDataValue()));
                    }
                    //当前SOC
                    if (entity.containsKey(FunctionLogoParamVo.BATTERY_TOTAL_SOC) && StringUtil.isNotEmpty(entity.get(FunctionLogoParamVo.BATTERY_TOTAL_SOC).getDataValue())) {
                        socList.add(Double.parseDouble(String.valueOf(entity.get(FunctionLogoParamVo.BATTERY_TOTAL_SOC).getDataValue())));
                    }
                }
                dataMap.put(TopNodeParamVo.DAY_ABLE_CHARGE_QT, DoubleUtil.getToDouble(dayAbleChargeQt));
                dataMap.put(TopNodeParamVo.DAY_ABLE_DISCHARGE_QT, DoubleUtil.getToDouble(dayAbleDischargeQt));

                //计算SOC 并机运行,取SOC最小的储能柜soc 不是并机运行(参考SOC的计算方式：（总可放电量/总电池容量）*100%)
                if (nodeParaMap.containsKey(nodeId) && StringUtil.isNotEmpty(nodeParaMap.get(nodeId))) {
                    Integer nodePara = nodeParaMap.get(nodeId);
                    //是否多柜并机运行 1-是 2-否
                    if (nodePara == 1) {
                        dataMap.put(TopNodeParamVo.SOC, DoubleUtil.getToDouble(socList.stream().min(Comparator.comparingDouble(s -> s)).orElse(0.0)));
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
                        //（总可放电量/总电池容量）*100%
                        dataMap.put(TopNodeParamVo.SOC, DoubleUtil.getToDouble(dataMap.get(TopNodeParamVo.DAY_ABLE_DISCHARGE_QT) / ratedCap * 100));
                    }
                }
                resultMap.put(nodeId, dataMap);
            }
        }
        return resultMap;
    }

    /**
     * 获取电桩节点的数据
     *
     * @param nodeDeviceIdMap 节点设备id数据  节点id -> 节点设备id
     * @return 设备节点数据 节点id -> (数据编号 -> 数据值)
     */
    public Map<String, Map<String, Double>> getPileNodeData(Map<String, Set<String>> nodeDeviceIdMap) {
        //返回的对象
        Map<String, Map<String, Double>> resultMap = Maps.newHashMap();
        if (MapUtils.isNotEmpty(nodeDeviceIdMap)) {
            //根据多个设备id查询充电功率和放电功率
            Set<String> deviceIdSet = nodeDeviceIdMap.values().stream().flatMap(Collection::stream).collect(Collectors.toSet());
            Set<String> functionLogos = new HashSet<>(Arrays.asList(FunctionLogoParamVo.PILE_POWER, FunctionLogoParamVo.PILE_CHARGEPOWER, FunctionLogoParamVo.PILE_DISCHARGEPOWER));
            Map<String, Map<String, RealDataModel>> deviceRealMap = DeviceCommonUtil.getDeviceFunctions(deviceIdSet, functionLogos);

            //根据多个设备id查询枪数量
            Map<String, Long> deviceGunNumMap = deviceGunDao.findAllByDeviceIdIn(deviceIdSet).stream().collect(Collectors.groupingBy(DeviceGunEntity::getDeviceId,
                    Collectors.counting()));

            //根据多个设备编号查询今日充电量和今日放电量
            Map<String, String> deviceIdCodeMap = deviceDao.findAllByIdInAndIsDelete(deviceIdSet, 1).stream()
                    .filter(d -> StringUtil.isNotEmpty(d.getDeviceNumber()))
                    .collect(Collectors.toMap(BaseEntity::getId, DeviceEntity::getDeviceNumber));
            Map<String, OrderCountDto> pileDayOrderMap = Maps.newHashMap();
            Map<String, OrderCountDto> pileAccOrderMap = Maps.newHashMap();
            if (MapUtils.isNotEmpty(deviceIdCodeMap)) {
                //查询今日充电量和今日放电量
                String startTime = DateUtil.localDateTimeToStr(LocalDateTime.now().with(LocalTime.MIN));
                String endTime = DateUtil.localDateTimeToStr(LocalDateTime.now());
                pileDayOrderMap = togetherService.findOrderRecordListByPileCodes(new ArrayList<>(deviceIdCodeMap.values()), startTime, endTime).getData();

                //查询累计充电量和累计放电量
                pileAccOrderMap = togetherService.findOrderRecordListByPileCodes(new ArrayList<>(deviceIdCodeMap.values()), null, null).getData();
            }
            //对设备拓扑节点数据进行组装
            for (Map.Entry<String, Set<String>> entry : nodeDeviceIdMap.entrySet()) {
                String nodeId = entry.getKey(); //节点id
                Set<String> deviceIds = entry.getValue(); //多个设备id
                Map<String, Double> dataMap = Maps.newHashMap(); //数据对象

                //电桩总功率,充电功率,放电功率
                double totalPower = 0.0;
                double rechargePower = 0.0;
                double dischargePower = 0.0;
                List<Map<String, RealDataModel>> realDataList = deviceIds.stream().filter(deviceRealMap::containsKey).map(deviceRealMap::get).collect(Collectors.toList());
                for (Map<String, RealDataModel> entity : realDataList) {
                    //电桩总功率
                    if (entity.containsKey(FunctionLogoParamVo.PILE_POWER) && StringUtil.isNotEmpty(entity.get(FunctionLogoParamVo.PILE_POWER).getDataValue())) {
                        totalPower += Double.parseDouble(String.valueOf(entity.get(FunctionLogoParamVo.PILE_POWER).getDataValue()));
                    }
                    //充电功率
                    if (entity.containsKey(FunctionLogoParamVo.PILE_CHARGEPOWER) && StringUtil.isNotEmpty(entity.get(FunctionLogoParamVo.PILE_CHARGEPOWER).getDataValue())) {
                        rechargePower += Double.parseDouble(String.valueOf(entity.get(FunctionLogoParamVo.PILE_CHARGEPOWER).getDataValue()));
                    }
                    //放电功率
                    if (entity.containsKey(FunctionLogoParamVo.PILE_DISCHARGEPOWER) && StringUtil.isNotEmpty(entity.get(FunctionLogoParamVo.PILE_DISCHARGEPOWER).getDataValue())) {
                        dischargePower += Double.parseDouble(String.valueOf(entity.get(FunctionLogoParamVo.PILE_DISCHARGEPOWER).getDataValue()));
                    }
                }
                //今日充电量,今日放电量
                AtomicReference<Double> dayChargeQt = new AtomicReference<>(0.0);
                AtomicReference<Double> dayDischargeQt = new AtomicReference<>(0.0);
                Map<String, OrderCountDto> finalPileDayOrderMap = pileDayOrderMap;
                deviceIds.stream().filter(deviceId -> {
                    if (deviceIdCodeMap.containsKey(deviceId)) {
                        String pileCode = deviceIdCodeMap.get(deviceId);
                        return finalPileDayOrderMap.containsKey(pileCode);
                    }
                    return false;
                }).forEach(deviceId -> {
                    String pileCode = deviceIdCodeMap.get(deviceId);
                    OrderCountDto orderCount = finalPileDayOrderMap.get(pileCode);
                    if (orderCount != null) {
                        dayChargeQt.updateAndGet(v -> v + orderCount.getChargeQt());
                        dayDischargeQt.updateAndGet(v -> v + orderCount.getDischargeQt());
                    }
                });
                //累计充电量,累计放电量
                AtomicReference<Double> totalChargeQt = new AtomicReference<>(0.0);
                AtomicReference<Double> totalDischargeQt = new AtomicReference<>(0.0);
                Map<String, OrderCountDto> finalPileAccOrderMap = pileAccOrderMap;
                deviceIds.stream().filter(deviceId -> {
                    if (deviceIdCodeMap.containsKey(deviceId)) {
                        String pileCode = deviceIdCodeMap.get(deviceId);
                        return finalPileAccOrderMap.containsKey(pileCode);
                    }
                    return false;
                }).forEach(deviceId -> {
                    String pileCode = deviceIdCodeMap.get(deviceId);
                    OrderCountDto orderCount = finalPileAccOrderMap.get(pileCode);
                    if (orderCount != null) {
                        if (StringUtil.isNotEmpty(orderCount.getChargeQt())) {
                            totalChargeQt.updateAndGet(v -> v + orderCount.getChargeQt());
                        }
                        if (StringUtil.isNotEmpty(orderCount.getDischargeQt())) {
                            totalChargeQt.updateAndGet(v -> v + orderCount.getDischargeQt());
                        }
                    }
                });
                dataMap.put(TopNodeParamVo.TOTAL_ACTIVE_POWER, DoubleUtil.getToDouble(totalPower));
                dataMap.put(TopNodeParamVo.RECHARGE_POWER, DoubleUtil.getToDouble(rechargePower));
                dataMap.put(TopNodeParamVo.DISCHARGE_POWER, DoubleUtil.getToDouble(dischargePower));
                dataMap.put(TopNodeParamVo.DAY_CHARGE_QT, DoubleUtil.getToDouble(dayChargeQt.get()));
                dataMap.put(TopNodeParamVo.DAY_DISCHARGE_QT, DoubleUtil.getAbsDouble(dayDischargeQt.get()));
                dataMap.put(TopNodeParamVo.TOTAL_CHARGE_QT, DoubleUtil.getAbsDouble(totalChargeQt.get()));
                dataMap.put(TopNodeParamVo.TOTAL_DISCHARGE_QT, DoubleUtil.getAbsDouble(totalDischargeQt.get()));
                //枪数量
                long gunNum = deviceIds.stream().filter(deviceGunNumMap::containsKey).mapToLong(deviceGunNumMap::get).sum();
                dataMap.put(TopNodeParamVo.DATA_NUM, gunNum > 0 ? (double) gunNum : 1);
                resultMap.put(nodeId, dataMap);
            }
        }
        return resultMap;
    }

    /**
     * 获取开关节点的数据
     *
     * @param nodeDeviceIdMap 节点设备id数据  节点id -> 节点设备id
     * @return 设备节点数据 节点id -> (数据编号 -> 数据值)
     */
    public Map<String, Map<String, Double>> getSwitchNodeData(Map<String, Set<String>> nodeDeviceIdMap) {
        //返回的对象
        Map<String, Map<String, Double>> resultMap = Maps.newHashMap();
        if (MapUtils.isNotEmpty(nodeDeviceIdMap)) {
            //对设备拓扑节点数据进行组装
            for (Map.Entry<String, Set<String>> entry : nodeDeviceIdMap.entrySet()) {
                String nodeId = entry.getKey(); //节点id
                Map<String, Double> dataMap = Maps.newHashMap(); //数据对象
                dataMap.put(TopNodeParamVo.SWITCH_STATUS, 0.0);
                resultMap.put(nodeId, dataMap);
            }
        }
        return resultMap;
    }

    /**
     * 获取换电站节点的数据
     *
     * @param nodeDeviceIdMap 节点设备id数据  节点id -> 节点设备id
     * @return 设备节点数据 节点id -> (数据编号 -> 数据值)
     */
    public Map<String, Map<String, Double>> getChangeNodeData(Map<String, Set<String>> nodeDeviceIdMap) {
        //返回的对象
        Map<String, Map<String, Double>> resultMap = Maps.newHashMap();
        if (MapUtils.isNotEmpty(nodeDeviceIdMap)) {
            //根据多个设备id查询换电站总有功功率,剩余电量,累计充电量数据
            Set<String> deviceIdSet = nodeDeviceIdMap.values().stream().flatMap(Collection::stream).collect(Collectors.toSet());
            Set<String> functionLogos = new HashSet<>(Arrays.asList(FunctionLogoParamVo.BATTERY_GRANARY_SUP_CHARGE, FunctionLogoParamVo.BATTERY_GRANARY_OUT_POWER,
                    FunctionLogoParamVo.BATTERY_PACK_REMAINING_POWER));
            Map<String, Map<String, RealDataModel>> deviceRealMap = DeviceCommonUtil.getDeviceFunctions(deviceIdSet, functionLogos);

            //换电仓电池包今日充电量
            DeviceHistoryQueryVo deviceQueryVo = new DeviceHistoryQueryVo();
            deviceQueryVo.setDeviceIds(deviceIdSet);
            deviceQueryVo.setFunctionLogos(Collections.singleton(FunctionLogoParamVo.BATTERY_GRANARY_SUP_CHARGE));
            deviceQueryVo.setStartTime(DateUtil.localDateTimeToStr(LocalDateTime.now().with(LocalTime.MIN)));
            deviceQueryVo.setEndTime(DateUtil.localDateTimeToStr(LocalDateTime.now()));
            deviceQueryVo.setTimeInterval("1d");
            Map<String, Map<String, List<NodeDifHistoryDto>>> deviceHistoryMap = dataService.findNodeDifHistoryListFeign(deviceQueryVo).getData();

            //对设备拓扑节点数据进行组装
            for (Map.Entry<String, Set<String>> entry : nodeDeviceIdMap.entrySet()) {
                String nodeId = entry.getKey(); //节点id
                Set<String> deviceIds = entry.getValue(); //多个设备id
                Map<String, Double> dataMap = Maps.newHashMap(); //数据对象

                //换电站总有功功率,剩余电量,累计充电量数据
                double totalActivePower = 0.0;
                double remainQt = 0.0;
                double totalChargeQt = 0.0;
                List<Map<String, RealDataModel>> realDataList = deviceIds.stream().filter(deviceRealMap::containsKey).map(deviceRealMap::get).collect(Collectors.toList());
                for (Map<String, RealDataModel> entity : realDataList) {
                    //总有功功率
                    if (entity.containsKey(FunctionLogoParamVo.BATTERY_GRANARY_OUT_POWER) && StringUtil.isNotEmpty(entity.get(FunctionLogoParamVo.BATTERY_GRANARY_OUT_POWER).getDataValue())) {
                        totalActivePower += Double.parseDouble(String.valueOf(entity.get(FunctionLogoParamVo.BATTERY_GRANARY_OUT_POWER).getDataValue()));
                    }
                    //剩余发电量
                    if (entity.containsKey(FunctionLogoParamVo.BATTERY_PACK_REMAINING_POWER) && StringUtil.isNotEmpty(entity.get(FunctionLogoParamVo.BATTERY_PACK_REMAINING_POWER).getDataValue())) {
                        remainQt += Double.parseDouble(String.valueOf(entity.get(FunctionLogoParamVo.BATTERY_PACK_REMAINING_POWER).getDataValue()));
                    }
                    //累计发电量
                    if (entity.containsKey(FunctionLogoParamVo.BATTERY_GRANARY_SUP_CHARGE) && StringUtil.isNotEmpty(entity.get(FunctionLogoParamVo.BATTERY_GRANARY_SUP_CHARGE).getDataValue())) {
                        totalChargeQt += Double.parseDouble(String.valueOf(entity.get(FunctionLogoParamVo.BATTERY_GRANARY_SUP_CHARGE).getDataValue()));
                    }
                }
                //换电站今日充电量
                double dayChargeQt = deviceIds.stream().filter(deviceHistoryMap::containsKey).flatMap(d -> deviceHistoryMap.get(d).values().stream().flatMap(Collection::stream))
                        .filter(d -> StringUtil.isNotEmpty(d.getFirstDataValue()) && StringUtil.isNotEmpty(d.getLastDataValue()))
                        .mapToDouble(d -> Double.parseDouble(String.valueOf(d.getLastDataValue())) - Double.parseDouble(String.valueOf(d.getFirstDataValue()))).sum();

                dataMap.put(TopNodeParamVo.TOTAL_ACTIVE_POWER, DoubleUtil.getToDouble(totalActivePower));
                dataMap.put(TopNodeParamVo.REMAIN_QT, DoubleUtil.getToDouble(remainQt));
                dataMap.put(TopNodeParamVo.TOTAL_CHARGE_QT, DoubleUtil.getToDouble(totalChargeQt));
                dataMap.put(TopNodeParamVo.DAY_CHARGE_QT, DoubleUtil.getToDouble(dayChargeQt));
                resultMap.put(nodeId, dataMap);
            }
        }
        return resultMap;
    }

}
