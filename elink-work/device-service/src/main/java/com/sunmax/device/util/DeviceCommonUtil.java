package com.sunmax.device.util;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.sunmax.common.config.redis.KeyUtil;
import com.sunmax.common.config.redis.RedisDeviceUtil;
import com.sunmax.common.config.redis.RedisUtil;
import com.sunmax.common.dto.device.ModelFunctionListDto;
import com.sunmax.common.entity.BaseEntity;
import com.sunmax.common.model.*;
import com.sunmax.common.util.*;
import com.sunmax.common.util.oss.FileUtil;
import com.sunmax.device.dao.access.DeviceDao;
import com.sunmax.device.dao.access.GatewaySubDeviceDao;
import com.sunmax.device.dao.access.ScenarioTypeDao;
import com.sunmax.device.dao.model.FunctionDao;
import com.sunmax.device.dao.model.ModelFunctionDao;
import com.sunmax.device.entity.access.DeviceEntity;
import com.sunmax.device.entity.access.GatewaySubDeviceEntity;
import com.sunmax.device.entity.access.ScenarioTypeEntity;
import com.sunmax.device.entity.model.FunctionEntity;
import com.sunmax.device.entity.model.ModelFunctionEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Slf4j
public class DeviceCommonUtil {

    private static final GatewaySubDeviceDao gatewaySubDeviceDao = SpringBeanUtil.getBean(GatewaySubDeviceDao.class);

    private static final DeviceDao deviceDao = SpringBeanUtil.getBean(DeviceDao.class);

    private static final ModelFunctionDao modelFunctionDao = SpringBeanUtil.getBean(ModelFunctionDao.class);

    private static final FunctionDao functionDao = SpringBeanUtil.getBean(FunctionDao.class);

    private static final ScenarioTypeDao scenarioTypeDao = SpringBeanUtil.getBean(ScenarioTypeDao.class);

    /**
     * 获取设备通讯状态
     *
     * @param deviceId     设备id
     * @param deviceNumber 设备编号
     * @return 设备状态
     */
    public static Integer getDeviceTxStatus(String deviceId, String deviceNumber) {
        //获取设备状态
        if (StringUtil.isEmpty(deviceNumber) && StringUtil.isNotEmpty(deviceId)) {
            AtomicReference<String> finalDeviceNumber = new AtomicReference<>();
            gatewaySubDeviceDao.findBySubDeviceId(deviceId).ifPresent(subDevice ->
                    finalDeviceNumber.set(deviceDao.findById(subDevice.getGatewayId()).orElse(new DeviceEntity()).getDeviceNumber()));
            deviceNumber = finalDeviceNumber.get();
        }
        if (StringUtil.isNotEmpty(deviceNumber)) {
            String deviceKey = KeyUtil.DEVICE_KEY + deviceNumber;
            if (RedisUtil.hasKey(deviceKey)) {
                DeviceModel deviceModel = JsonUtil.objectToEntity(RedisUtil.get(deviceKey), DeviceModel.class);
                return deviceModel.getTxStatus();
            }
        }
        return 0;
    }

    /**
     * 获取设备功能点数据
     *
     * @param modelId       模型id
     * @param functionLogos 多个功能点标识
     * @param dataType      数据类型 1-原始缓存数据 2-超过15分钟未上报的数据(置空)
     * @return 设备id -> (功能点标识 -> 功能点实时数据)
     */
    public static Map<String, Map<String, RealDataModel>> getDeviceFunctions(String modelId, Set<String> functionLogos, Integer dataType) {
        //根据模型id查询设备功能点数据
        return getDeviceFunctions(deviceDao.findAllByModelIdInAndIsDelete(Collections.singleton(modelId), 1), functionLogos, dataType);
    }

    /**
     * 获取设备功能点数据
     *
     * @param modelIds      多个模型id
     * @param functionLogos 多个功能点标识
     * @param dataType      数据类型 1-原始缓存数据 2-超过15分钟未上报的数据(置空)
     * @return 模型id -> (设备id -> (功能点标识 -> 功能点实时数据))
     */
    public static Map<String, Map<String, Map<String, RealDataModel>>> getDeviceFunctionsByModelIds(Set<String> modelIds, Set<String> functionLogos, Integer dataType) {
        Map<String, Map<String, Map<String, RealDataModel>>> resultMap = Maps.newHashMap();
        //根据模型id查询设备功能点数据
        List<DeviceEntity> deviceList = deviceDao.findAllByModelIdInAndIsDelete(modelIds, 1);
        Map<String, Map<String, RealDataModel>> deviceModelMap = getDeviceFunctions(deviceList, functionLogos, dataType);
        Map<String, Set<String>> deviceMap = deviceList.stream().collect(Collectors.groupingBy(DeviceEntity::getModelId,
                Collectors.mapping(DeviceEntity::getId, Collectors.toSet())));
        deviceMap.forEach((key, values) -> {
            Map<String, Map<String, RealDataModel>> deviceRealDataMap = Maps.newHashMap();
            values.forEach(deviceId -> {
                if (deviceModelMap.containsKey(deviceId)) {
                    deviceRealDataMap.put(deviceId, deviceModelMap.get(deviceId));
                }
            });
            resultMap.put(key, deviceRealDataMap);
        });
        return resultMap;
    }


    /**
     * 获取设备功能点数据
     * 注：超过15分钟未上报的数据 置空
     *
     * @param deviceIds     多个设备id
     * @param functionLogos 多个功能点标识
     * @return 设备id -> (功能点标识 -> 功能点实时数据)
     */
    public static Map<String, Map<String, RealDataModel>> getDeviceFunctions(Set<String> deviceIds, Set<String> functionLogos) {
        //根据多个设备id查询设备功能点数据
        return getDeviceFunctions(deviceDao.findAllByIdInAndIsDelete(deviceIds, 1), functionLogos, 2);
    }

    /**
     * 获取设备功能点数据
     * 注：缓存最后一次上报的数据
     *
     * @param deviceIds     多个设备id
     * @param functionLogos 多个功能点标识
     * @return 设备id -> (功能点标识 -> 功能点实时数据)
     */
    public static Map<String, Map<String, RealDataModel>> getRawDeviceFunctions(Set<String> deviceIds, Set<String> functionLogos) {
        //根据多个设备id查询设备功能点数据
        return getDeviceFunctions(deviceDao.findAllByIdInAndIsDelete(deviceIds, 1), functionLogos, 1);
    }

    /**
     * 获取设备功能点数据
     * 注：超过15分钟未上报的数据 置空
     *
     * @param deviceIds     多个设备id
     * @param functionLogos 多个功能点标识
     * @param dataType 数据类型 1-原始缓存数据 2-超过15分钟未上报的数据(置空)
     * @return 设备id -> (功能点标识 -> 功能点实时数据)
     */
    public static Map<String, Map<String, RealDataModel>> getDeviceFunctions(Set<String> deviceIds, Set<String> functionLogos, Integer dataType) {
        //根据多个设备id查询设备功能点数据
        return getDeviceFunctions(deviceDao.findAllByIdInAndIsDelete(deviceIds, 1), functionLogos, dataType);
    }

    /**
     * 获取设备功能点数据
     *
     * @param deviceList    多个设备数据
     * @param functionLogos 多个功能点标识
     * @param dataType      数据类型 1-原始缓存数据 2-超过15分钟未上报的数据(置空)
     * @return 设备id -> (功能点标识 -> 功能点实时数据)
     */
    public static Map<String, Map<String, RealDataModel>> getDeviceFunctions(List<DeviceEntity> deviceList, Set<String> functionLogos, Integer dataType) {
        Map<String, Map<String, RealDataModel>> resultMap = Maps.newHashMap();
        //根据多个网关子设备id查询网关数据
        Set<String> subDeviceId = deviceList.stream().filter(d -> Objects.equals(d.getAccessType(), 3)).map(DeviceEntity::getId)
                .collect(Collectors.toSet());
        Map<String, String> subDeviceMap = gatewaySubDeviceDao.findAllBySubDeviceIdIn(subDeviceId).stream().collect(Collectors
                .toMap(GatewaySubDeviceEntity::getSubDeviceId, GatewaySubDeviceEntity::getGatewayId, (k1, k2) -> k1));
        Map<String, DeviceModel> gatewayDeviceMap = Maps.newHashMap();
        deviceDao.findAllByIdInAndIsDelete(subDeviceMap.values(), 1).stream()
                .filter(d -> StringUtil.isNotEmpty(d.getDeviceNumber()))
                .forEach(device -> gatewayDeviceMap.put(device.getId(), RedisDeviceUtil.getDevice(device.getDeviceNumber())));
        deviceList.forEach(device -> {
            Map<String, RealDataModel> realDataMap = Maps.newHashMap();
            //设备缓存和子设备缓存
            DeviceModel deviceModel = new DeviceModel();
            DeviceModel gatewayDeviceModel = new DeviceModel();
            if (StringUtil.isNotEmpty(device.getDeviceNumber())) {
                deviceModel = RedisDeviceUtil.getDevice(device.getDeviceNumber());
            }
            if (subDeviceMap.containsKey(device.getId())) {
                String gatewayId = subDeviceMap.get(device.getId());
                gatewayDeviceModel = gatewayDeviceMap.getOrDefault(gatewayId, null);
            }
            for (String functionLogo : functionLogos) {
                if (deviceModel != null) {
                    Map<String, FunctionModel> functionMap = deviceModel.getFunctionMap();
                    if (functionMap != null && functionMap.containsKey(functionLogo)) {
                        FunctionModel functionModel = functionMap.get(functionLogo);
                        RealDataModel realDataModel = new RealDataModel();
                        BeanUtils.copyProperties(functionModel, realDataModel);
                        //校验是否在取值范围内 不在取值范围内 数据给空
                        realDataModel.setDataValue(CommonUtil.getRangeValue(functionModel.getValueRange(), functionModel.getDataType(),
                                functionModel.getDataObject(), realDataModel.getDataValue()));
                        //校验数据是否在15分钟之前 考虑置空
                        if (dataType == 2) {
                            realDataModel.setDataValue(CommonUtil.getDateTimeValue(functionModel.getDateTime(), functionModel.getDataType(), functionModel.getDataValue()));
                        }
                        realDataMap.put(functionLogo, realDataModel);
//                        continue;
                    }
                }
                //设备是电桩类型的话 不往下执行获取
                List<String> pileTypes = Arrays.asList("28", "29", "30");
                if (pileTypes.contains(device.getTypeId())) {
                    continue;
                }
                if (gatewayDeviceModel != null) {
                    //网关下面要过滤电桩类型的 以免获取错误数据
                    for (Map.Entry<String, ChannelModel> channelModel : gatewayDeviceModel.getChannelMap().entrySet()) {
                        Map<String, String> functionPointMap = channelModel.getValue().getFunctionPointMap();
                        String functionPointKey = device.getId() + FileUtil.COLON + functionLogo;
                        if (functionPointMap.containsKey(functionPointKey)) {
                            RealDataModel realDataModel = new RealDataModel();
                            String pointKey = functionPointMap.get(functionPointKey);
                            Map<String, PointTableModel> pointTableMap = channelModel.getValue().getPointTableMap();
                            if (pointTableMap.containsKey(pointKey)) {
                                PointTableModel pointTableModel = pointTableMap.get(pointKey);
                                BeanUtils.copyProperties(pointTableModel, realDataModel);
                                //校验是否在取值范围内 不在取值范围内 数据给空
                                realDataModel.setDataValue(CommonUtil.getRangeValue(pointTableModel.getValueRange(), pointTableModel.getDataType(), pointTableModel.getDataObject(), realDataModel.getDataValue()));
                                //校验数据是否在15分钟之前 考虑置空
                                if (dataType == 2) {
                                    realDataModel.setDataValue(CommonUtil.getDateTimeValue(pointTableModel.getDateTime(), pointTableModel.getDataType(), pointTableModel.getDataValue()));
                                }
                                realDataMap.put(functionLogo, realDataModel);
                            } else {
                                realDataMap.put(functionLogo, null);
                            }
                            break;
                        }
                    }
                }
            }
            resultMap.put(device.getId(), realDataMap);
        });

        return resultMap;
    }

    /**
     * 根据模型id获取功能点数据
     *
     * @param modelId 模型id
     * @return 设备功能点数据
     */
    public static List<ModelFunctionListDto> getFunctionListByModeId(String modelId) {
        List<ModelFunctionListDto> resultList = Lists.newArrayList();
        //根据模型id获取模型标准功能关联数据
        List<ModelFunctionEntity> modelFunctionList = modelFunctionDao.findAllByModelIdAndIsDelete(modelId, 1);
        if (CollectionUtils.isNotEmpty(modelFunctionList)) {
            //根据多个模型标准功能id查询模型标准功能数据
            Set<String> functionIds = modelFunctionList.stream().map(ModelFunctionEntity::getFunctionId).collect(Collectors.toSet());
            Map<String, FunctionEntity> functionMap = functionDao.findAllById(functionIds).stream().collect(Collectors.toMap(FunctionEntity::getId, a -> a));

            resultList = modelFunctionList.stream().map(modelFunction -> {
                ModelFunctionListDto result = new ModelFunctionListDto();
                if (functionMap.containsKey(modelFunction.getFunctionId())) {
                    FunctionEntity function = functionMap.get(modelFunction.getFunctionId());
                    BeanUtils.copyProperties(function, result);
                    result.setFunctionId(function.getId());
                }
                result.setId(modelFunction.getId());
                result.setSerialNum(modelFunction.getSerialNum());
                if (StringUtil.isNotEmpty(modelFunction.getDataType())) {
                    result.setDataType(modelFunction.getDataType());
                }
                if (StringUtil.isNotEmpty(modelFunction.getAccuracy())) {
                    result.setAccuracy(modelFunction.getAccuracy());
                }
                if (StringUtil.isNotEmpty(modelFunction.getUnit())) {
                    result.setUnit(modelFunction.getUnit());
                }
                if (StringUtil.isNotEmpty(modelFunction.getDataObject())) {
                    result.setDataObject(modelFunction.getDataObject());
                }
                if (StringUtil.isNotEmpty(modelFunction.getValueRange())) {
                    result.setValueRange(modelFunction.getValueRange());
                }
                return result;
            }).collect(Collectors.toList());
        }
        return resultList;
    }

    /**
     * 根据多个模型id获取功能点数据
     *
     * @param modelIds 多个模型id
     * @return 设备功能点数据
     */
    public static Map<String, List<ModelFunctionListDto>> getFunctionListByModeIds(Set<String> modelIds) {
        Map<String, List<ModelFunctionListDto>> resultMap = Maps.newHashMap();

        Map<String, List<ModelFunctionEntity>> modelFunctionMap = modelFunctionDao.findAllByModelIdInAndIsDelete(modelIds, 1).stream()
                .collect(Collectors.groupingBy(ModelFunctionEntity::getModelId));
        Set<String> functionIds = modelFunctionMap.entrySet().stream().flatMap(s -> s.getValue().stream()
                .map(ModelFunctionEntity::getFunctionId)).collect(Collectors.toSet());
        Map<String, FunctionEntity> functionMap = functionDao.findAllById(functionIds).stream().collect(Collectors.toMap(FunctionEntity::getId, a -> a));
        modelFunctionMap.forEach((key, values) -> {
            List<ModelFunctionListDto> functionList = Lists.newArrayList();
            values.forEach(modelFunction -> {
                ModelFunctionListDto result = new ModelFunctionListDto();
                if (functionMap.containsKey(modelFunction.getFunctionId())) {
                    FunctionEntity function = functionMap.get(modelFunction.getFunctionId());
                    BeanUtils.copyProperties(function, result);
                    result.setFunctionId(function.getId());
                }
                result.setId(modelFunction.getId());
                result.setSerialNum(modelFunction.getSerialNum());
                if (StringUtil.isNotEmpty(modelFunction.getDataType())) {
                    result.setDataType(modelFunction.getDataType());
                }
                if (StringUtil.isNotEmpty(modelFunction.getAccuracy())) {
                    result.setAccuracy(modelFunction.getAccuracy());
                }
                if (StringUtil.isNotEmpty(modelFunction.getUnit())) {
                    result.setUnit(modelFunction.getUnit());
                }
                if (StringUtil.isNotEmpty(modelFunction.getDataObject())) {
                    result.setDataObject(modelFunction.getDataObject());
                }
                functionList.add(result);
            });
            resultMap.put(key, functionList);
        });
        return resultMap;
    }

    /**
     * 获取扩展值数据(数据转换)
     *
     * @param reaType      扩展属性类型 1-数值 2-文字 3-选项 4-位置 5-开关 6-时间 7-文本
     * @param extraValue   额定值
     * @param defaultValue 默认值
     * @param value        数据
     * @return 扩展值数据
     */
    public static Object getReaValue(Integer reaType, String extraValue, String defaultValue, Object value) {
        Object result = null;
        //如果值为空 则用默认值
        if (StringUtil.isEmpty(value)) {
            result = defaultValue;
            return result;
        }
        //扩展属性类型 1-数值 2-文字 3-选项 4-位置 5-开关 6-时间 7-文本
        switch (reaType) {
            case 1:
            case 4:
            case 2:
            case 7:
                result = value;
                break;
            case 3:
                for (Object enumArray : JSON.parseObject(extraValue).getJSONArray("enumArray")) {
                    JSONObject jsonObject = JSON.parseObject(enumArray.toString());
                    if (Objects.equals(jsonObject.getString("name"), value)) {
                        result = jsonObject.get("id");
                    }
                }
                break;
            case 5:
                JSONObject jsonObject5 = JSONObject.parseObject(extraValue);
                if (StringUtil.isNotEmpty(jsonObject5.get("trueValue")) && Objects.equals(jsonObject5.get("trueValue"), value)) {
                    result = true;
                }
                if (StringUtil.isNotEmpty(jsonObject5.get("falseValue")) && Objects.equals(jsonObject5.get("falseValue"), value)) {
                    result = false;
                }
                break;
            case 6:
                String timeFormat = JSONObject.parseObject(extraValue).getString("timeFormat");
                result = DateUtil.format(DateUtil.strToDate(String.valueOf(value)), timeFormat);
                break;
        }
        return result;
    }

    /**
     * 根据多个父节点id查询父节点名称
     *
     * @param parentIds 多个父节点id
     */
    public static Map<String, String> getParentNameByParentIds(Set<String> parentIds) {
        Map<String, String> resultMap = Maps.newHashMap();
        if (CollectionUtils.isNotEmpty(parentIds)) {
            Set<String> parentIds2;
            //1.根据父节点id先查询设备表父级节点数据
            List<DeviceEntity> deviceList = deviceDao.findAllByIdInAndIsDelete(parentIds, 1);
            if (CollectionUtils.isNotEmpty(deviceList)) {
                List<String> deviceIds = deviceList.stream().map(BaseEntity::getId).collect(Collectors.toList());
                parentIds2 = parentIds.stream().filter(id -> !deviceIds.contains(id)).collect(Collectors.toSet());
                deviceList.forEach(device -> resultMap.put(device.getId(), device.getDeviceName()));
            } else {
                parentIds2 = parentIds;
            }
            //2.根据未查到的父节点id查询站点能源表数据
            if (CollectionUtils.isNotEmpty(parentIds2)) {
                List<ScenarioTypeEntity> scenarioTypeList = scenarioTypeDao.findAllById(parentIds);
                if (CollectionUtils.isNotEmpty(scenarioTypeList)) {
                    scenarioTypeList.forEach(scenarioType -> resultMap.put(scenarioType.getId(), scenarioType.getSystemName()));
                }
            }
        }
        return resultMap;
    }

}
