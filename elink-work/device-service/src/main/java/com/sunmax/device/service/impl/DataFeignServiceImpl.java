package com.sunmax.device.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.sunmax.common.config.redis.KeyUtil;
import com.sunmax.common.config.redis.RedisDeviceUtil;
import com.sunmax.common.dto.device.DeviceFieldDto;
import com.sunmax.common.dto.device.ModelFunctionListDto;
import com.sunmax.common.entity.BaseEntity;
import com.sunmax.common.model.ChannelModel;
import com.sunmax.common.model.DeviceModel;
import com.sunmax.common.model.FunctionModel;
import com.sunmax.common.model.PointTableModel;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.util.StringUtil;
import com.sunmax.common.util.oss.FileUtil;
import com.sunmax.common.vo.device.DeviceEventChangeVo;
import com.sunmax.common.vo.device.DeviceFieldQueryVo;
import com.sunmax.device.constants.DBConstants;
import com.sunmax.device.dao.access.DeviceDao;
import com.sunmax.device.dao.access.DeviceEventDao;
import com.sunmax.device.dao.access.GatewaySubDeviceDao;
import com.sunmax.device.entity.access.DeviceEntity;
import com.sunmax.device.entity.access.DeviceEventEntity;
import com.sunmax.device.entity.access.GatewaySubDeviceEntity;
import com.sunmax.device.service.DataFeignService;
import com.sunmax.device.util.DeviceCommonUtil;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class DataFeignServiceImpl implements DataFeignService {

    @Autowired
    private DeviceDao deviceDao;

    @Autowired
    private DeviceEventDao deviceEventDao;

    @Autowired
    private GatewaySubDeviceDao gatewaySubDeviceDao;

    @Override
    public ResponseResult<Map<String, Set<String>>> findDeviceEventIds(Integer eventStatus) {
        //返回的对象
        Map<String, Set<String>> resultMap = Maps.newConcurrentMap();

        List<DeviceEntity> deviceList = deviceDao.findAll(Example.of(DeviceEntity.builder().isDelete(1).build()));
        Set<String> deviceIds = deviceList.stream().map(BaseEntity::getId).collect(Collectors.toSet());
        if (CollectionUtils.isNotEmpty(deviceIds)) {
            List<DeviceEventEntity> deviceEventList;
            if (StringUtil.isNotEmpty(eventStatus)) {
                deviceEventList = deviceEventDao.findAllByDeviceIdInAndEventStatus(deviceIds, eventStatus);
            } else {
                deviceEventList = deviceEventDao.findAllByDeviceIdIn(deviceIds);
            }
            resultMap = deviceEventList.stream().collect(Collectors.groupingBy(DeviceEventEntity::getDeviceId,
                    Collectors.mapping(DeviceEventEntity::getEventId, Collectors.toSet())));
        }
        return ResponseResult.ok(resultMap);
    }

    @Override
    @Transactional(transactionManager = DBConstants.JPA_TX_ACCESS, rollbackFor = Exception.class)
    public ResponseResult<Void> batchUpdateDeviceEvent(List<DeviceEventChangeVo> deviceEventVos) {
        List<DeviceEventEntity> resultList = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(deviceEventVos)) {
            //查询所有设备库里未修复的设备事件
            Set<String> deviceIds = deviceEventVos.stream().map(DeviceEventChangeVo::getDeviceId).collect(Collectors.toSet());
            Map<String, List<DeviceEventEntity>> unEventMap = deviceEventDao.findAllByDeviceIdInAndEventStatus(deviceIds, 0)
                    .stream().collect(Collectors.groupingBy(DeviceEventEntity::getDeviceId));
            Map<Integer, List<DeviceEventChangeVo>> deviceEventMap = deviceEventVos.stream().collect(Collectors.groupingBy(DeviceEventChangeVo::getEventStatus));
            deviceEventMap.forEach((key, value) -> {
                if (key == 0) { //未修复
                    for (DeviceEventChangeVo deviceEventVo : value) {
                        String deviceId = deviceEventVo.getDeviceId();
                        String eventId = deviceEventVo.getEventId();
                        String eventSource = deviceEventVo.getEventSource();
                        if (unEventMap.containsKey(deviceId) && unEventMap.get(deviceId).stream().map(DeviceEventEntity::getEventId).collect(Collectors.toSet()).contains(eventId)) {
                            continue;
                        }
                        resultList.add(DeviceEventEntity.builder().deviceId(deviceId).eventId(eventId).eventStatus(key).ignoreStatus(0).eventSource(eventSource).build());
                    }
                }
                if (key == 1) { //已修复
                    for (DeviceEventChangeVo deviceEventVo : value) {
                        String deviceId = deviceEventVo.getDeviceId();
                        String eventId = deviceEventVo.getEventId();
                        if (unEventMap.containsKey(deviceId)) {
                            List<DeviceEventEntity> deviceEventList = unEventMap.get(deviceId);
                            DeviceEventEntity oldDeviceEvent = deviceEventList.stream().filter(d -> Objects.equals(eventId, d.getEventId())).findFirst().orElse(new DeviceEventEntity());
                            if (StringUtil.isNotEmpty(oldDeviceEvent.getId())) {
                                DeviceEventEntity deviceEvent = DeviceEventEntity.builder().deviceId(deviceId).eventId(eventId).eventStatus(key).ignoreStatus(oldDeviceEvent.getIgnoreStatus()).eventSource(oldDeviceEvent.getEventSource()).build();
                                deviceEvent.setId(oldDeviceEvent.getId());
                                resultList.add(deviceEvent);
                            }
                        }
                    }
                }
            });
        }
        if (CollectionUtils.isNotEmpty(resultList)) {
            deviceEventDao.saveAll(resultList);
        }
        return ResponseResult.ok();
    }

    @Override
    public ResponseResult<Map<String, String>> findDeviceModelIds(String deviceId) {
        Map<String, String> resultMap = Maps.newHashMap();
        if (StringUtil.isNotEmpty(deviceId)) {
            deviceDao.findById(deviceId).ifPresent(device -> resultMap.put(device.getId(), device.getModelId()));
        } else {
            deviceDao.findAll(Example.of(DeviceEntity.builder().isDelete(1).build())).forEach(device -> resultMap.put(device.getId(), device.getModelId()));
        }
        return ResponseResult.ok(resultMap);
    }

    @Override
    public ResponseResult<Map<String, List<DeviceFieldDto>>> queryDeviceFieldList(DeviceFieldQueryVo deviceFieldQueryVo) {
        //返回的对象
        Map<String, List<DeviceFieldDto>> resultMap = Maps.newHashMap();

        //根据多个设备id查询设备数据
        List<DeviceEntity> deviceList = deviceDao.findAllByIdInAndIsDelete(deviceFieldQueryVo.getDeviceIds(), 1);

        if (CollectionUtils.isNotEmpty(deviceList)) {
            //根据多个网关子设备id查询网关数据
            Set<String> subDeviceId = deviceList.stream().filter(d -> Objects.equals(d.getAccessType(), 3)).map(BaseEntity::getId)
                    .collect(Collectors.toSet());
            Map<String, String> subDeviceMap = gatewaySubDeviceDao.findAllBySubDeviceIdIn(subDeviceId).stream().collect(Collectors
                    .toMap(GatewaySubDeviceEntity::getSubDeviceId, GatewaySubDeviceEntity::getGatewayId, (k1, k2) -> k1));
            Map<String, DeviceModel> gatewayDeviceMap = Maps.newHashMap();
            deviceDao.findAllByIdInAndIsDelete(subDeviceMap.values(), 1).stream().filter(d -> StringUtil.isNotEmpty(d.getDeviceNumber()))
                    .forEach(device -> gatewayDeviceMap.put(device.getId(), RedisDeviceUtil.getDevice(device.getDeviceNumber())));

            deviceList.forEach(device -> {
                List<DeviceFieldDto> deviceFieldList = Lists.newArrayList();
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

                for (String functionLogo : deviceFieldQueryVo.getFunctionLogos()) {
                    if (deviceModel != null) {
                        Map<String, FunctionModel> functionMap = deviceModel.getFunctionMap();
                        if (functionMap != null && functionMap.containsKey(functionLogo)) {
                            FunctionModel functionModel = functionMap.get(functionLogo);
                            String tableName = KeyUtil.DEVICE_KEY + deviceModel.getDeviceNumber().toLowerCase().replace(FileUtil.BAR, FileUtil.UNDERLINE);
                            deviceFieldList.add(DeviceFieldDto.builder()
                                    .deviceId(device.getId())
                                    .tableName(tableName.toLowerCase())
                                    .dataType(functionModel.getDataType())
                                    .dataObject(functionModel.getDataObject())
                                    .valueRange(functionModel.getValueRange())
                                    .functionLogo(functionLogo)
                                    .fieldName(functionLogo).build());
//                            continue;
                        }
                    }
                    if (gatewayDeviceModel != null) {
                        for (Map.Entry<String, ChannelModel> entry : gatewayDeviceModel.getChannelMap().entrySet()) {
                            ChannelModel channelModel = entry.getValue();
                            Map<String, String> functionPointMap = channelModel.getFunctionPointMap();
                            Map<String, PointTableModel> pointTableMap = channelModel.getPointTableMap();
                            String functionPointKey = device.getId() + FileUtil.COLON + functionLogo;
                            if (functionPointMap.containsKey(functionPointKey)) {
                                String pointKey = functionPointMap.get(functionPointKey);
                                if (pointTableMap.containsKey(pointKey)) {
                                    PointTableModel pointTable = pointTableMap.get(pointKey);
                                    deviceFieldList.add(DeviceFieldDto.builder()
                                            .deviceId(device.getId())
                                            .tableName(KeyUtil.MQTT + gatewayDeviceModel.getDeviceNumber().toLowerCase().replace(FileUtil.BAR, FileUtil.UNDERLINE))
                                            .dataType(pointTable.getDataType())
                                            .dataObject(pointTable.getDataObject())
                                            .valueRange(pointTable.getValueRange())
                                            .functionLogo(functionLogo)
                                            .fieldName(pointKey).build());
                                }
                                break;
                            }
                        }
                    }
                }
                resultMap.put(device.getId(), deviceFieldList);
            });
        }
        return ResponseResult.ok(resultMap);
    }

    @Override
    public ResponseResult<Map<String, List<DeviceFieldDto>>> queryDeviceFieldListByMap(Map<String, Set<String>> deviceFieldQueryMap) {
        //返回的对象
        Map<String, List<DeviceFieldDto>> resultMap = Maps.newHashMap();

        //根据多个设备id查询设备数据
        List<DeviceEntity> deviceList = deviceDao.findAllByIdInAndIsDelete(deviceFieldQueryMap.keySet(), 1);

        if (CollectionUtils.isNotEmpty(deviceList)) {
            //根据多个网关子设备id查询网关数据
            Set<String> subDeviceId = deviceList.stream().filter(d -> Objects.equals(d.getAccessType(), 3)).map(BaseEntity::getId)
                    .collect(Collectors.toSet());
            Map<String, String> subDeviceMap = gatewaySubDeviceDao.findAllBySubDeviceIdIn(subDeviceId).stream().collect(Collectors
                    .toMap(GatewaySubDeviceEntity::getSubDeviceId, GatewaySubDeviceEntity::getGatewayId, (k1, k2) -> k1));
            Map<String, DeviceModel> gatewayDeviceMap = Maps.newHashMap();
            deviceDao.findAllByIdInAndIsDelete(subDeviceMap.values(), 1).stream()
                    .filter(d -> StringUtil.isNotEmpty(d.getDeviceNumber())).forEach(device -> gatewayDeviceMap.put(device.getId(), RedisDeviceUtil.getDevice(device.getDeviceNumber())));

            //根据多个模型id查询字段数据类型
            Set<String> modelIds = deviceList.stream().map(DeviceEntity::getModelId).collect(Collectors.toSet());
            Map<String, Map<String, Integer>> functionTypeMap = DeviceCommonUtil.getFunctionListByModeIds(modelIds)
                    .entrySet().stream().collect(Collectors.toMap(
                            Map.Entry::getKey, entry -> entry.getValue().stream().collect(Collectors
                                    .toMap(ModelFunctionListDto::getFunctionLogo, ModelFunctionListDto::getDataType, (k1, k2) -> k1))));

            deviceList.forEach(device -> {
                List<DeviceFieldDto> deviceFieldList = Lists.newArrayList();
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
                if (deviceFieldQueryMap.containsKey(device.getId())) {
                    for (String functionLogo : deviceFieldQueryMap.get(device.getId())) {
                        if (deviceModel != null) {
                            Map<String, FunctionModel> functionMap = deviceModel.getFunctionMap();
                            if (functionMap != null && functionMap.containsKey(functionLogo)) {
                                FunctionModel functionModel = functionMap.get(functionLogo);
                                String tableName = KeyUtil.DEVICE_KEY + deviceModel.getDeviceNumber();
                                deviceFieldList.add(DeviceFieldDto.builder()
                                        .deviceId(device.getId())
                                        .tableName(tableName)
                                        .dataType(functionModel.getDataType())
                                        .functionLogo(functionLogo)
                                        .fieldName(functionLogo).build());
//                            continue;
                            }
                        }
                        if (gatewayDeviceModel != null) {
                            for (Map.Entry<String, ChannelModel> channelModel : gatewayDeviceModel.getChannelMap().entrySet()) {
                                Map<String, String> functionPointMap = channelModel.getValue().getFunctionPointMap();
                                String functionPointKey = device.getId() + FileUtil.COLON + functionLogo;
                                if (functionPointMap.containsKey(functionPointKey)) {
                                    String pointKey = functionPointMap.get(functionPointKey);
                                    deviceFieldList.removeAll(deviceFieldList.stream().filter(d -> Objects.equals(d.getDeviceId(), device.getId())
                                            && Objects.equals(d.getFunctionLogo(), functionLogo)).collect(Collectors.toList()));
                                    deviceFieldList.add(DeviceFieldDto.builder()
                                            .deviceId(device.getId())
                                            .tableName(KeyUtil.MQTT + gatewayDeviceModel.getDeviceNumber())
                                            .dataType(functionTypeMap.getOrDefault(device.getModelId(), Maps.newHashMap()).get(functionLogo))
                                            .functionLogo(functionLogo)
                                            .fieldName(pointKey).build());
                                    break;
                                }
                            }
                        }
                    }
                }
                resultMap.put(device.getId(), deviceFieldList);
            });
        }
        return ResponseResult.ok(resultMap);
    }
}
