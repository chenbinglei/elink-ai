package com.sunmax.protocol.task;

import com.alibaba.fastjson2.JSONObject;
import com.sunmax.common.config.redis.KeyUtil;
import com.sunmax.common.config.redis.RedisUtil;
import com.sunmax.common.dto.data.EventStoreDataDto;
import com.sunmax.common.model.EventModel;
import com.sunmax.common.model.FunctionModel;
import com.sunmax.common.model.PointTableModel;
import com.sunmax.common.util.CalculateUtil;
import com.sunmax.common.util.IntegerUtil;
import com.sunmax.common.util.JsonUtil;
import com.sunmax.common.util.StringUtil;
import com.sunmax.common.util.oss.FileUtil;
import com.sunmax.common.vo.device.DeviceEventChangeVo;
import com.sunmax.protocol.runner.ProtocolRunner;
import com.sunmax.protocol.service.feign.DeviceService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.compress.utils.Lists;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.sunmax.protocol.runner.ProtocolRunner.deviceEventMap;
import static com.sunmax.protocol.runner.ProtocolRunner.deviceModelMap;

public class DeviceEventDataSink {

    /**
     * 解析设备事件数据
     *
     * @param modelId     模型id
     * @param deviceId    设备id
     * @param functionMap 功能点数据(功能点标识 -> 功能点值)
     */
    public static void parseDeviceEvent(String modelId, String deviceId, Map<String, FunctionModel> functionMap, DeviceService deviceService) {

        //定义设备数据
        List<DeviceEventChangeVo> deviceEventList = Lists.newArrayList();
        if (StringUtil.isNotEmpty(deviceId)) {
            //获取设备对应的模型id
            if (StringUtil.isEmpty(modelId)) {
                if (deviceModelMap.containsKey(deviceId)) {
                    modelId = deviceModelMap.get(deviceId);
                } else {
                    Map<String, String> deviceMap = deviceService.findDeviceModelIds(deviceId).getData();
                    if (MapUtils.isNotEmpty(deviceMap)) {
                        deviceModelMap.putAll(deviceMap);
                        modelId = deviceMap.get(deviceId);
                    }
                }
            }
            if (StringUtil.isNotEmpty(modelId)) {
                String modelEventKey = KeyUtil.MODEL_EVENT_KEY + modelId;
                if (RedisUtil.hasKey(modelEventKey)) {
                    List<EventModel> eventModelList = JsonUtil.objectToList(RedisUtil.get(modelEventKey), EventModel.class);
                    for (EventModel modelEvent : eventModelList) {
                        String eventId = modelEvent.getEventId();
                        Integer calculateType = modelEvent.getCalculateType();
                        String storeData = modelEvent.getStoreData();
                        StringBuilder eventSources = new StringBuilder(FileUtil.separator);
                        if (calculateType == 1) { //值运算
                            for (Map.Entry<String, FunctionModel> realData : functionMap.entrySet()) {
                                if (storeData.contains(realData.getKey())) {
                                    String dataValue = StringUtil.isNotEmpty(realData.getValue().getDataValue()) ? String.valueOf(realData.getValue().getDataValue()) : FileUtil.separator;
                                    storeData = storeData.replace(realData.getKey(), dataValue);
                                    eventSources.append(realData.getKey()).append(FileUtil.EQUAL).append(dataValue);
                                }
                            }
                            Object eval = CalculateUtil.eval(storeData);
                            if (eval == null || (boolean) eval) { //校验为空或校验通过则产生告警
                                if (StringUtil.isNotEmpty(eventSources) && eventSources.length() > 1) {
                                    eventSources.deleteCharAt(eventSources.length() - 1);
                                }
                                deviceEventList.add(DeviceEventChangeVo.builder().deviceId(deviceId).eventId(eventId).eventStatus(0).eventSource(eventSources.toString()).build());
                                addDeviceEvent(deviceId, eventId);
                                continue;
                            }
                        }
                        if (calculateType == 2) { //位运算
                            EventStoreDataDto eventData = JSONObject.parseObject(storeData, EventStoreDataDto.class);
                            if (functionMap.containsKey(eventData.getFunctionLogo())) { //包含校验
                                Object dataValue = functionMap.get(eventData.getFunctionLogo()).getDataValue();
                                if (StringUtil.isEmpty(dataValue)) { //直接生成告警
                                    eventSources.append(eventData.getFunctionLogo()).append(FileUtil.COLON).append(dataValue);
                                    deviceEventList.add(DeviceEventChangeVo.builder().deviceId(deviceId).eventId(eventId).eventStatus(0).eventSource(eventSources.toString()).build());
                                    addDeviceEvent(deviceId, eventId);
                                    continue;
                                }
                                if (IntegerUtil.getBit((int) dataValue, eventData.getPoint()) == eventData.getResult()) { //生成告警
                                    eventSources.append(eventData.getFunctionLogo()).append(FileUtil.COLON).append(dataValue);
                                    deviceEventList.add(DeviceEventChangeVo.builder().deviceId(deviceId).eventId(eventId).eventStatus(0).eventSource(eventSources.toString()).build());
                                    addDeviceEvent(deviceId, eventId);
                                    continue;
                                }
                            }/* else { //不包含 直接生成告警
                        continue;
                    }*/
                        }
                        //走到这里 说明未产生告警 需要修复
                        if (deviceEventMap.containsKey(deviceId) && deviceEventMap.get(deviceId).contains(eventId)) {
                            deviceEventList.add(DeviceEventChangeVo.builder().deviceId(deviceId).eventId(eventId).eventSource(eventSources.toString()).eventStatus(1).build());
                            delDeviceEvent(deviceId, modelEvent.getEventId());
                        }
                    }
                }
            }
        }

        //批量添加或更新模型事件数据
        if (CollectionUtils.isNotEmpty(deviceEventList)) {
            deviceService.batchUpdateDeviceEvent(deviceEventList);
        }

    }

    /**
     * 解析子设备事件数据
     *
     * @param modelId     模型id
     * @param deviceId    设备id
     * @param pointTableMap 功能点数据(功能点标识 -> 功能点值)
     */
    public static void parseSubDeviceEvent(String modelId, String deviceId, Map<String, PointTableModel> pointTableMap, DeviceService deviceService) {

        //定义设备数据
        List<DeviceEventChangeVo> deviceEventList = Lists.newArrayList();
        if (StringUtil.isNotEmpty(deviceId)) {
            //获取设备对应的模型id
            if (StringUtil.isEmpty(modelId)) {
                if (ProtocolRunner.deviceModelMap.containsKey(deviceId)) {
                    modelId = ProtocolRunner.deviceModelMap.get(deviceId);
                } else {
                    Map<String, String> deviceMap = deviceService.findDeviceModelIds(deviceId).getData();
                    if (MapUtils.isNotEmpty(deviceMap)) {
                        ProtocolRunner.deviceModelMap.putAll(deviceMap);
                        modelId = deviceMap.get(deviceId);
                    }
                }
            }
            if (StringUtil.isNotEmpty(modelId)) {
                String modelEventKey = KeyUtil.MODEL_EVENT_KEY + modelId;
                if (RedisUtil.hasKey(modelEventKey)) {
                    List<EventModel> eventModelList = JsonUtil.objectToList(RedisUtil.get(modelEventKey), EventModel.class);
                    for (EventModel modelEvent : eventModelList) {
                        String eventId = modelEvent.getEventId();
                        Integer calculateType = modelEvent.getCalculateType();
                        String storeData = modelEvent.getStoreData();
                        StringBuilder eventSources = new StringBuilder(FileUtil.separator);
                        if (calculateType == 1) { //值运算
                            for (Map.Entry<String, PointTableModel> realData : pointTableMap.entrySet()) {
                                if (storeData.contains(realData.getKey())) {
                                    String dataValue = StringUtil.isNotEmpty(realData.getValue().getDataValue()) ? String.valueOf(realData.getValue().getDataValue()) : FileUtil.separator;
                                    storeData = storeData.replace(realData.getKey(), dataValue);
                                    eventSources.append(realData.getKey()).append(FileUtil.EQUAL).append(dataValue).append(FileUtil.SEMICOLON);
                                }
                            }
                            Object eval = CalculateUtil.eval(storeData);
                            if (eval == null || (boolean) eval) { //校验为空或校验通过则产生告警
                                if (StringUtil.isNotEmpty(eventSources) && eventSources.length() > 1) {
                                    eventSources.deleteCharAt(eventSources.length() - 1);
                                }
                                deviceEventList.add(DeviceEventChangeVo.builder().deviceId(deviceId).eventId(eventId).eventStatus(0).eventSource(eventSources.toString()).build());
                                addDeviceEvent(deviceId, eventId);
                                continue;
                            }
                        }
                        if (calculateType == 2) { //位运算
                            EventStoreDataDto eventData = JSONObject.parseObject(storeData, EventStoreDataDto.class);
                            if (pointTableMap.containsKey(eventData.getFunctionLogo())) { //包含校验
                                Object dataValue = pointTableMap.get(eventData.getFunctionLogo()).getDataValue();
                                if (StringUtil.isEmpty(dataValue) || !StringUtil.isNumber((String) dataValue)) { //直接生成告警
                                    eventSources.append(eventData.getFunctionLogo()).append(FileUtil.COLON).append(dataValue);
                                    deviceEventList.add(DeviceEventChangeVo.builder().deviceId(deviceId).eventId(eventId).eventStatus(0).eventSource(eventSources.toString()).build());
                                    addDeviceEvent(deviceId, eventId);
                                    continue;
                                }
                                if (IntegerUtil.getBit(Integer.parseInt((String) dataValue), eventData.getPoint()) == eventData.getResult()) { //生成告警
                                    eventSources.append(eventData.getFunctionLogo()).append(FileUtil.COLON).append(dataValue);
                                    deviceEventList.add(DeviceEventChangeVo.builder().deviceId(deviceId).eventId(eventId).eventStatus(0).eventSource(eventSources.toString()).build());
                                    addDeviceEvent(deviceId, eventId);
                                    continue;
                                }
                            }/* else { //不包含 直接生成告警
                        continue;
                    }*/
                        }
                        //走到这里 说明未产生告警 需要修复
                        if (deviceEventMap.containsKey(deviceId) && deviceEventMap.get(deviceId).contains(eventId)) {
                            deviceEventList.add(DeviceEventChangeVo.builder().deviceId(deviceId).eventId(eventId).eventStatus(1).build());
                            delDeviceEvent(deviceId, modelEvent.getEventId());
                        }
                    }
                }
            }
        }

        //批量添加或更新模型事件数据
        if (CollectionUtils.isNotEmpty(deviceEventList)) {
            deviceService.batchUpdateDeviceEvent(deviceEventList);
        }

    }

    //添加事件告警
    public static void addDeviceEvent(String deviceId, String eventId) {
        Set<String> eventIds = new HashSet<>();
        if (deviceEventMap.containsKey(deviceId)) {
            eventIds = deviceEventMap.get(deviceId);
            eventIds.add(eventId);
        } else {
            eventIds.add(eventId);
        }
        deviceEventMap.put(deviceId, eventIds);
    }

    public static void delDeviceEvent(String deviceId, String eventId) {
        if (deviceEventMap.containsKey(deviceId)) {
            Set<String> eventIds = deviceEventMap.get(deviceId);
            eventIds.remove(eventId);
            deviceEventMap.put(deviceId, eventIds);
        }
    }

}
