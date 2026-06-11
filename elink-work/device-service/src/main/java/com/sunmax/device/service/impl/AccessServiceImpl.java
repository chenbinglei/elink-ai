package com.sunmax.device.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.sunmax.common.config.redis.*;
import com.sunmax.common.entity.BaseEntity;
import com.sunmax.common.model.ChannelModel;
import com.sunmax.common.model.DeviceModel;
import com.sunmax.common.model.PointTableModel;
import com.sunmax.common.model.general.GatewayRealModel;
import com.sunmax.common.model.general.PileRealModel;
import com.sunmax.common.util.CommonUtil;
import com.sunmax.common.util.JsonUtil;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.util.StringUtil;
import com.sunmax.common.util.oss.FileUtil;
import com.sunmax.device.constants.DBConstants;
import com.sunmax.device.dao.access.*;
import com.sunmax.device.dao.model.FunctionDao;
import com.sunmax.device.dao.model.ModelDao;
import com.sunmax.device.dao.model.ModelFunctionDao;
import com.sunmax.device.dto.ChannelInfoDto;
import com.sunmax.device.dto.ImportResultDto;
import com.sunmax.device.dto.PointTableDto;
import com.sunmax.device.dto.SubDeviceFunctionDto;
import com.sunmax.device.dto.device.DeviceAccessDto;
import com.sunmax.device.entity.access.ChannelEntity;
import com.sunmax.device.entity.access.DeviceEntity;
import com.sunmax.device.entity.access.GatewaySubDeviceEntity;
import com.sunmax.device.entity.access.PointTableEntity;
import com.sunmax.device.entity.model.FunctionEntity;
import com.sunmax.device.entity.model.ModelFunctionEntity;
import com.sunmax.device.service.AccessService;
import com.sunmax.device.service.feign.ProtocolService;
import com.sunmax.device.util.DeviceCommonUtil;
import com.sunmax.device.vo.ChannelChangeVo;
import com.sunmax.device.vo.PointTableChangeVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AccessServiceImpl implements AccessService {

    @Autowired
    private DeviceDao deviceDao;

    @Autowired
    private ChannelDao channelDao;

    @Autowired
    private PointTableDao pointTableDao;

    @Autowired
    private ModelDao modelDao;

    @Autowired
    private FunctionDao functionDao;

    @Autowired
    private GatewaySubDeviceDao gatewaySubDeviceDao;

    @Autowired
    private ModelFunctionDao modelFunctionDao;

    @Autowired
    private SiteInfoDao siteInfoDao;

    @Autowired
    private ProtocolService protocolService;

    @Override
    public ResponseResult<DeviceAccessDto> findAccessDetailByDeviceId(String deviceId) {
        //返回的对象
        DeviceAccessDto result = new DeviceAccessDto();
        //根据设备id查询设备数据
        Optional<DeviceEntity> optional = deviceDao.findById(deviceId);
        if (optional.isPresent()) {
            DeviceEntity device = optional.get();
            BeanUtils.copyProperties(device, result);
            //根据模型id获取模型名称
            modelDao.findById(device.getModelId()).ifPresent(model -> result.setModelName(model.getModelName()));
            //根据站点id获取站点名称
            siteInfoDao.findById(device.getSiteId()).ifPresent(site -> result.setSiteName(site.getSiteName()));
            //获取设备通信状态
            result.setTxStatus(DeviceCommonUtil.getDeviceTxStatus(deviceId, device.getDeviceNumber()));
        }
        return ResponseResult.ok(result);
    }

    @Override
    @Transactional(transactionManager = DBConstants.JPA_TX_ACCESS, rollbackFor = Exception.class)
    public ResponseResult<Void> saveChannel(ChannelChangeVo channelChangeVo) {
        //根据设备id查询设备序列号 设备序列号不存在 则不允许添加
        String deviceNumber = deviceDao.findById(channelChangeVo.getDeviceId()).orElse(new DeviceEntity()).getDeviceNumber();
        if (StringUtil.isEmpty(deviceNumber)) {
            return ResponseResult.paramError("添加通道信息需要设备序列号,请先去设备管理添加该设备的序列号");
        }

        //根据设备id和通道名称查询通道数据列表
        List<ChannelEntity> channelNameList = channelDao.findAllByDeviceIdAndChannelName(channelChangeVo.getDeviceId(), channelChangeVo.getChannelName());

        //根据设备id和协议类型查询通道数据列表
        List<ChannelEntity> protocolTypeList = channelDao.findAllByDeviceIdAndProtocolType(channelChangeVo.getDeviceId(), channelChangeVo.getProtocolType());

        ChannelEntity channel = new ChannelEntity();
        //新增通道数据
        if (StringUtil.isEmpty(channelChangeVo.getId())) {
            //校验设备下的通道名称是否重复
            if (CollectionUtils.isNotEmpty(channelNameList)) {
                return ResponseResult.paramShow(channelChangeVo.getChannelName(), ResponseResult.PARAM_EXIST);
            }
            //校验设备下的协议类型是否重复
            if (CollectionUtils.isNotEmpty(protocolTypeList)) {
                return ResponseResult.paramShow(channelChangeVo.getProtocolType(), ResponseResult.PARAM_EXIST);
            }
            ChannelEntity channelEntity = new ChannelEntity();
            BeanUtils.copyProperties(channelChangeVo, channelEntity);
            channelEntity.setDeviceNumber(deviceNumber);
            channel = channelDao.save(channelEntity);
        } else {
            //校验设备下的通道名称是否重复
            channelNameList = channelNameList.stream().filter(c -> !Objects.equals(c.getId(), channelChangeVo.getId())).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(channelNameList)) {
                return ResponseResult.paramShow(channelChangeVo.getChannelName(), ResponseResult.PARAM_EXIST);
            }
            //校验设备下的协议类型是否重复
            protocolTypeList = protocolTypeList.stream().filter(c -> !Objects.equals(c.getId(), channelChangeVo.getId())).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(protocolTypeList)) {
                return ResponseResult.paramShow(channelChangeVo.getProtocolType(), ResponseResult.PARAM_EXIST);
            }
            //编辑通道数据
            Optional<ChannelEntity> optional = channelDao.findById(channelChangeVo.getId());
            if (optional.isPresent()) {
                ChannelEntity channelEntity = new ChannelEntity();
                BeanUtils.copyProperties(channelChangeVo, channelEntity);
                channelEntity.setDeviceNumber(deviceNumber);
                channel = channelDao.save(channelEntity);
            }
        }

        //校验redis是否存在 不存在则添加
        String lockKey = KeyUtil.LOCK_KEY + deviceNumber;
        try {
            boolean lock = RedisLockUtil.lock(lockKey, KeyUtil.WAIT_TIME, KeyUtil.EXPIRE_TIME);
            if (lock) {
                String deviceKey = KeyUtil.DEVICE_KEY + deviceNumber;
                if (!RedisUtil.hasKey(deviceKey)) {
                    return ResponseResult.paramError(ResponseResult.PARAM_ERROR);
                }
                DeviceModel deviceModel = JsonUtil.objectToEntity(RedisUtil.get(deviceKey), DeviceModel.class);
                String channelKey = channel.getProtocolType();
                if (deviceModel.getChannelMap().containsKey(channelKey)) {
                    ChannelModel redisChannel = deviceModel.getChannelMap().get(channelKey);
                    ChannelModel channelData = new ChannelModel();
                    BeanUtils.copyProperties(channel, channelData);
                    channelData.setTxStatus(redisChannel.getTxStatus());
                    channelData.setPointTableMap(redisChannel.getPointTableMap());
                    deviceModel.getChannelMap().put(channelKey, channelData);
                } else {
                    ChannelModel channelData = new ChannelModel();
                    BeanUtils.copyProperties(channel, channelData);
                    channelData.setTxStatus(1);
                    deviceModel.getChannelMap().put(channelKey, channelData);
                }
                RedisUtil.set(deviceKey, deviceModel);
            }
        } catch (RuntimeException e) {
            log.error("通道数据存redis报错", e);
            return ResponseResult.paramError(ResponseResult.PARAM_ERROR);
        } finally {
            RedisLockUtil.unlock(lockKey);
        }
        return ResponseResult.ok();

    }

    @Override
    @Transactional(transactionManager = DBConstants.JPA_TX_ACCESS, rollbackFor = Exception.class)
    public ResponseResult<Void> deleteChannelById(String deviceId, String channelId) {
        Optional<ChannelEntity> optional = channelDao.findById(channelId);
        if (optional.isPresent()) {
            ChannelEntity channel = optional.get();
            String deviceNumber = deviceDao.findById(deviceId).orElse(new DeviceEntity()).getDeviceNumber();
            if (StringUtil.isNotEmpty(deviceNumber)) {
                String lockKey = KeyUtil.LOCK_KEY + deviceNumber;
                try {
                    boolean lock = RedisLockUtil.lock(lockKey, KeyUtil.WAIT_TIME);
                    if (lock) {
                        String deviceKey = KeyUtil.DEVICE_KEY + deviceNumber;
                        if (RedisUtil.hasKey(deviceKey)) {
                            DeviceModel device = RedisDeviceUtil.getDevice(deviceNumber);
                            if (device != null) {
                                if (MapUtils.isNotEmpty(device.getChannelMap())) {
                                    device.getChannelMap().remove(channel.getProtocolType());
                                }
                            }
                            RedisUtil.set(deviceKey, device);
                        }
                    }
                } catch (RuntimeException e) {
                    log.error("删除设备redis里面的通道数据报错", e);
                } finally {
                    RedisLockUtil.unlock(lockKey);
                }
            }
            channelDao.delete(channel);
            //根据通道id查询点表数据
            pointTableDao.deleteAll(pointTableDao.findAllByChannelId(channelId));
            return ResponseResult.ok();
        }
        return ResponseResult.paramError(ResponseResult.PARAM_ERROR);
    }

    @Override
    public ResponseResult<List<ChannelInfoDto>> findChannelInfoListByDeviceId(String deviceId) {
        List<ChannelInfoDto> resultList = Lists.newArrayList();
        Optional<DeviceEntity> optional = deviceDao.findById(deviceId);
        if (optional.isPresent()) {
            String deviceNumber = optional.get().getDeviceNumber();

            //根据设备id查询通道信息数据
            List<ChannelEntity> channelList = channelDao.findAllByDeviceId(deviceId);
            if (CollectionUtils.isNotEmpty(channelList)) {
                resultList = channelList.stream().map(channel -> {
                    ChannelInfoDto result = new ChannelInfoDto();
                    BeanUtils.copyProperties(channel, result);
                    //获取通信状态
                    String deviceKey = KeyUtil.DEVICE_KEY + deviceNumber;
                    if (RedisUtil.hasKey(deviceKey)) {
                        DeviceModel deviceModel = JsonUtil.objectToEntity(RedisUtil.get(deviceKey), DeviceModel.class);
                        if (deviceModel.getChannelMap().containsKey(channel.getProtocolType())) {
                            result.setTxStatus(deviceModel.getChannelMap().get(channel.getProtocolType()).getTxStatus());
                        }
                    }
                    return result;
                }).collect(Collectors.toList());
            }
        }
        return ResponseResult.ok(resultList);
    }

    @Override
    @Transactional(transactionManager = DBConstants.JPA_TX_ACCESS, rollbackFor = Exception.class)
    public ResponseResult<Void> updateDeviceStatus(String deviceId, Integer updateType) {
        //根据设备id查询设备数据
        Optional<DeviceEntity> optional = deviceDao.findById(deviceId);
        if (optional.isPresent()) {
            DeviceEntity device = optional.get();
            if (StringUtil.isEmpty(device.getDeviceNumber())) {
                return ResponseResult.paramError("未获取到该设备的序列号");
            }
            //注销 校验是否存在订单,以及更新设备状态更新通用缓存设备状态
            if (checkGeneralDeviceStatus(device.getDeviceNumber(), device.getAccessType(), updateType)) {
                return ResponseResult.paramError("该设备有进行中的订单,不允许注销设备");
            }

            DeviceModel deviceModel = RedisDeviceUtil.getDevice(device.getDeviceNumber());
            if (deviceModel != null) {
                if (updateType == 1) { //注册
                    deviceModel.setTxStatus(88);
                    //云网关默认给在线
                    if (Objects.equals(device.getTypeId(), "32")) {
                        deviceModel.setTxStatus(1);
                    }
                }
                if (updateType == 2) { //注销
                    deviceModel.setTxStatus(0);
                }
                RedisDeviceUtil.setDevice(device.getDeviceNumber(), deviceModel);
            }
            if (device.getAccessType() == 2) {
                //查询该网关下面所有的设备状态，并更新状态
                Set<String> subDeviceIds = gatewaySubDeviceDao.findAllByGatewayId(deviceId).stream().map(GatewaySubDeviceEntity::getSubDeviceId)
                        .collect(Collectors.toSet());
                deviceDao.findAllByIdInAndIsDelete(subDeviceIds, 1).stream()
                        .filter(s -> StringUtil.isNotEmpty(s.getDeviceNumber()))
                        .forEach(subDevice -> {
                            DeviceModel subDeviceModel = RedisDeviceUtil.getDevice(subDevice.getDeviceNumber());
                            if (subDeviceModel != null) {
                                if (updateType == 1) { //注册
                                    subDeviceModel.setTxStatus(88);
                                    //云网关默认给在线
                                    if (Objects.equals(device.getTypeId(), "32")) {
                                        subDeviceModel.setTxStatus(1);
                                    }
                                }
                                if (updateType == 2) { //注销
                                    subDeviceModel.setTxStatus(0);
                                }
                                RedisDeviceUtil.setDevice(subDeviceModel.getDeviceNumber(), subDeviceModel);
                            }
                        });

                //该设备是网关设备的话 需要订阅或删除主题
                if (updateType == 1) { //注册 添加主题
                    protocolService.addDeviceTopic(device.getDeviceNumber());
                }
                if (updateType == 2) { //注销 删除主题
                    protocolService.deleteDeviceTopic(device.getDeviceNumber());
                }
            }
            return ResponseResult.ok();
        }
        return ResponseResult.paramError(ResponseResult.PARAM_ERROR);
    }

    @Override
    public ResponseResult<List<PointTableDto>> findPointTableListByChannelId(String channelId) {
        //返回的集合
        List<PointTableDto> resultList = Lists.newArrayList();
        //根据通道id查询点表数据
        List<PointTableEntity> pointTableList = pointTableDao.findAllByChannelId(channelId);
        if (CollectionUtils.isNotEmpty(pointTableList)) {
            //根据多个设备id获取多个设备名称
            Set<String> deviceIds = pointTableList.stream().map(PointTableEntity::getDeviceId).collect(Collectors.toSet());
            Map<String, DeviceEntity> deviceMap = deviceDao.findAllById(deviceIds).stream().collect(Collectors
                    .toMap(BaseEntity::getId, a -> a));
            //根据多个模型id查询模型功能数据
            Set<String> modelIds = deviceMap.values().stream().map(DeviceEntity::getModelId).collect(Collectors.toSet());
            Map<String, Set<ModelFunctionEntity>> modelFunctionMap = modelFunctionDao.findAllByModelIdInAndIsDelete(modelIds, 1)
                    .stream().collect(Collectors.groupingBy(ModelFunctionEntity::getModelId, Collectors
                            .mapping(a -> a, Collectors.toSet())));
            //根据多个功能点id获取多个功能点数据
            Set<String> functionIds = pointTableList.stream().map(PointTableEntity::getFunctionId).collect(Collectors.toSet());
            Map<String, FunctionEntity> functionMap = functionDao.findAllById(functionIds).stream().collect(Collectors
                    .toMap(FunctionEntity::getId, s -> s));
            resultList = pointTableList.stream().map(pointTable -> {
                PointTableDto result = new PointTableDto();
                BeanUtils.copyProperties(pointTable, result);
                if (functionMap.containsKey(pointTable.getFunctionId())) {
                    FunctionEntity function = functionMap.get(pointTable.getFunctionId());
                    result.setFunctionName(function.getFunctionName());
                    result.setFunctionType(function.getFunctionType());
                    result.setDataType(function.getDataType());
                }
                if (deviceMap.containsKey(pointTable.getDeviceId())) {
                    DeviceEntity deviceEntity = deviceMap.get(pointTable.getDeviceId());
                    result.setDeviceName(deviceEntity.getDeviceName());
                    if (modelFunctionMap.containsKey(deviceEntity.getModelId())) {
                        List<ModelFunctionEntity> modelFunctionList = modelFunctionMap.get(deviceEntity.getModelId()).stream()
                                .filter(mf -> Objects.equals(mf.getFunctionId(), pointTable.getFunctionId()))
                                .collect(Collectors.toList());
                        if (!modelFunctionList.isEmpty()) {
                            ModelFunctionEntity modelFunction = modelFunctionList.get(0);
                            if (StringUtil.isNotEmpty(modelFunction.getDataType())) {
                                result.setDataType(modelFunction.getDataType());
                            }
                        }
                    }
                }
                return result;
            }).collect(Collectors.toList());
        }
        return ResponseResult.ok(resultList);
    }

    @Override
    @Transactional(transactionManager = DBConstants.JPA_TX_ACCESS, rollbackFor = Exception.class)
    public ResponseResult<Void> savePointTable(List<PointTableChangeVo> pointTableVos) {
        if (CollectionUtils.isNotEmpty(pointTableVos)) {

            //定义新增编辑和删除点表集合
            List<PointTableEntity> updatePointList = Lists.newArrayList(), deletePointList = Lists.newArrayList();

            for (PointTableChangeVo pointTableVo : pointTableVos) {
                switch (pointTableVo.getUpdateType()) {
                    case 1: //新增点表数据
                    case 2: //编辑点表数据
                        //标记新增编辑的数据
                        updatePointList.add(new PointTableEntity(pointTableVo));
                        break;
                    case 3: //删除点表数据
                        //标记删除的数据
                        deletePointList.add(new PointTableEntity(pointTableVo));
                        break;
                }

            }

            //新增和编辑点表数据
            if (!updatePointList.isEmpty()) {
                pointTableDao.saveAll(updatePointList);
            }
            //删除点表数据
            if (!deletePointList.isEmpty()) {
                pointTableDao.deleteInBatch(deletePointList);
            }

            //更新redis里面的功能点映射数据
            String channelId = pointTableVos.get(0).getChannelId();
            //根据通道id查询通道数据
            Optional<ChannelEntity> optional = channelDao.findById(channelId);
            //根据通道id查询最新的点表数据
            List<PointTableEntity> pointTableList = pointTableDao.findAllByChannelId(channelId);
            Set<String> functionIds = pointTableList.stream().map(PointTableEntity::getFunctionId).collect(Collectors.toSet());
            Map<String, FunctionEntity> functionMap = functionDao.findAllById(functionIds).stream().collect(Collectors.toMap(FunctionEntity::getId,
                    a -> a, (k1, k2) -> k1));
            if (optional.isPresent()) {
                ChannelEntity channel = optional.get();
                //校验redis是否存在 不存在则添加
                try {
                    String deviceNumber = channel.getDeviceNumber();
                    DeviceModel deviceModel = RedisDeviceUtil.getDevice(deviceNumber);
                    String channelKey = channel.getProtocolType();
                    if (deviceModel != null && StringUtil.isNotEmpty(deviceModel.getDeviceNumber())) {
                        if (deviceModel.getChannelMap().containsKey(channelKey)) {
                            ChannelModel redisChannel = deviceModel.getChannelMap().get(channelKey);
                            Map<String, String> functionPointMap = Maps.newConcurrentMap();
                            Map<String, PointTableModel> pointTableMap = redisChannel.getPointTableMap();
                            pointTableList.forEach(pointTable -> {
                                if (functionMap.containsKey(pointTable.getFunctionId())) {
                                    FunctionEntity function = functionMap.get(pointTable.getFunctionId());
                                    String key = pointTable.getDeviceId() + FileUtil.COLON + function.getFunctionLogo();
                                    String value = KeyUtil.POINT_KEY + pointTable.getDataId();
                                    functionPointMap.put(key, value);

                                    //功能点里面增加系数，偏移量,精度，数据对象等字段
                                    PointTableModel pointTableModel;
                                    if (pointTableMap.containsKey(value)) {
                                        pointTableModel = pointTableMap.get(value);
                                        pointTableModel.setDataId(pointTable.getDataId());
                                        pointTableModel.setDataType(function.getDataType());
                                    } else {
                                        pointTableModel = new PointTableModel();
                                        pointTableModel.setDataType(function.getDataType());
                                        pointTableModel.setDataId(pointTable.getDataId());
                                    }
                                    pointTableModel.setCoefficient(pointTable.getCoefficient());
                                    pointTableModel.setOffset(pointTable.getOffset());
                                    pointTableModel.setFieldCode(function.getFunctionLogo());
                                    pointTableModel.setAccuracy(function.getAccuracy());
                                    pointTableModel.setDataObject(function.getDataObject());
                                    pointTableModel.setValueRange(function.getValueRange());
                                    pointTableMap.put(value, pointTableModel);
                                }
                            });
                            redisChannel.setFunctionPointMap(functionPointMap);
                            redisChannel.setPointTableMap(pointTableMap);
                            deviceModel.getChannelMap().put(channelKey, redisChannel);
                        }
                        RedisDeviceUtil.setDevice(deviceNumber, deviceModel);
                    } else {
                        return ResponseResult.paramError(ResponseResult.PARAM_ERROR);
                    }
                } catch (RuntimeException e) {
                    log.error("通道数据存redis报错", e);
                    return ResponseResult.paramError(ResponseResult.PARAM_ERROR);
                }
            }
        }
        return ResponseResult.ok();
    }

    @Override
    public ResponseResult<List<SubDeviceFunctionDto>> findSubDeviceFunctionListByDeviceId(String deviceId, Integer type) {
        //返回的集合
        List<SubDeviceFunctionDto> resultList = Lists.newArrayList();
        Set<String> subDeviceIds = Sets.newConcurrentHashSet();
        switch (type) { //类型 1-设备及功能点数据 2-设备数据 3-功能点数据
            case 1:
            case 2:
                //根据网关id查询关联的子设备id
                subDeviceIds = gatewaySubDeviceDao.findAllByGatewayId(deviceId).stream().map(GatewaySubDeviceEntity::getSubDeviceId).collect(Collectors.toSet());
                break;
            case 3:
                subDeviceIds.add(deviceId);
                break;
        }
        if (CollectionUtils.isNotEmpty(subDeviceIds)) {
            //根据多个子设备id查询设备名称和模型id
            List<DeviceEntity> subDeviceList = deviceDao.findAllById(subDeviceIds);
            //根据多个模型id查询模型功能数据
            Set<String> modelIds = subDeviceList.stream().map(DeviceEntity::getModelId).collect(Collectors.toSet());
            Map<String, Set<ModelFunctionEntity>> modelFunctionMap = modelFunctionDao.findAllByModelIdInAndIsDelete(modelIds, 1)
                    .stream().collect(Collectors.groupingBy(ModelFunctionEntity::getModelId, Collectors
                            .mapping(a -> a, Collectors.toSet())));
            Set<String> functionIds = modelFunctionMap.entrySet().stream().flatMap(s -> s.getValue().stream()
                    .map(ModelFunctionEntity::getFunctionId)).collect(Collectors.toSet());
            Map<String, FunctionEntity> functionMap = functionDao.findAllById(functionIds).stream().collect(Collectors
                    .toMap(FunctionEntity::getId, a -> a));
            for (DeviceEntity subDevice : subDeviceList) {
                //类型 1-设备及功能点数据 2-设备数据 3-功能点数据
                if (type == 1 || type == 3) {
                    if (modelFunctionMap.containsKey(subDevice.getModelId())) {
                        resultList.addAll(modelFunctionMap.get(subDevice.getModelId()).stream().map(function -> {
                            SubDeviceFunctionDto result = new SubDeviceFunctionDto();
                            result.setDeviceId(subDevice.getId());
                            result.setDeviceName(subDevice.getDeviceName());
                            result.setFunctionId(function.getFunctionId());
                            result.setSerialNum(function.getSerialNum());
                            result.setDataType(function.getDataType());
                            if (functionMap.containsKey(function.getFunctionId())) {
                                FunctionEntity functionEntity = functionMap.get(function.getFunctionId());
                                result.setFunctionName(functionEntity.getFunctionName());
                                result.setFunctionType(functionEntity.getFunctionType());
                                if (StringUtil.isEmpty(result.getDataType())) {
                                    result.setDataType(functionEntity.getDataType());
                                }
                            }
                            return result;
                        }).collect(Collectors.toList()));
                    }
                }
                if (type == 2) {
                    resultList.add(SubDeviceFunctionDto.builder().deviceId(subDevice.getId()).deviceName(subDevice.getDeviceName()).serialNum(0).build());
                }
            }
        }
        resultList = resultList.stream().sorted(Comparator.comparing(SubDeviceFunctionDto::getSerialNum)).collect(Collectors.toList());
        return ResponseResult.ok(resultList);
    }

    @Override
    @Transactional(transactionManager = DBConstants.JPA_TX_ACCESS, rollbackFor = Exception.class)
    public ResponseResult<ImportResultDto> importPointTableData(String channelId, MultipartFile pointTableFile) {
        //返回的对象
        ImportResultDto result = new ImportResultDto();

        //校验文件名是否存在
        String filename = pointTableFile.getOriginalFilename();
        if (StringUtil.isEmpty(filename)) {
            return ResponseResult.paramError("文件名称为空");
        }
        //校验文件名称格式
        if (!filename.endsWith("xls") && !filename.endsWith("xlsx")) {
            return ResponseResult.paramError("文件不是Excel文件");
        }

        //查询点表校验数据
        List<PointTableEntity> checkList = pointTableDao.findAllByChannelId(channelId);
        Map<String, String> deviceFunctionMap = checkList.stream().filter(p -> StringUtil.isNotEmpty(p.getDeviceId()) && StringUtil.isNotEmpty(p.getFunctionId()))
                .collect(Collectors.toMap(p -> StringUtil.isEmpty(p.getFunctionIndex()) ? p.getDeviceId() + FileUtil.COMMA + p.getFunctionId() : p.getDeviceId() + FileUtil.COMMA + p.getFunctionId() + FileUtil.COMMA + p.getFunctionIndex(),
                        PointTableEntity::getId, (k1, k2) -> k1));
        List<PointTableEntity> pointTableList = Lists.newArrayList();
        //解析文件内容
        try {
            //获取第一个shell
            Sheet sheet = WorkbookFactory.create(pointTableFile.getInputStream()).getSheetAt(0);
            //获取Excel的行数
            int totalRows = sheet.getPhysicalNumberOfRows();
            // 得到Excel的列数(前提是有行数)
            if (totalRows <= 1 || StringUtil.isEmpty(sheet.getRow(0))) {
                return ResponseResult.paramError("没有解析到文件中的数据");
            }
            //定义错误信息列表
            List<String> errDescList = Lists.newArrayList();
            for (int i = 1; i < totalRows; i++) {
                Row row = sheet.getRow(i);
                String deviceId = CommonUtil.getCellValue(row.getCell(1));
                String functionId = CommonUtil.getCellValue(row.getCell(3));
                String dataId = CommonUtil.getCellValue(row.getCell(4));
                String coefficient = CommonUtil.getCellValue(row.getCell(5));
                String offset = CommonUtil.getCellValue(row.getCell(6));
                String functionIndex = CommonUtil.getCellValue(row.getCell(7));
                if (StringUtil.isEmpty(deviceId)) {
                    errDescList.add("第" + i + "行：【设备ID】数据为空");
                    continue;
                }
                if (StringUtil.isEmpty(functionId)) {
                    errDescList.add("第" + i + "行：【功能点ID】数据为空");
                    continue;
                }
                if (StringUtil.isEmpty(dataId)) {
                    errDescList.add("第" + i + "行：【点号】数据为空");
                    continue;
                }
                if (StringUtil.isEmpty(coefficient)) {
                    errDescList.add("第" + i + "行：【系数】数据为空");
                    continue;
                }
                if (StringUtil.isEmpty(offset)) {
                    errDescList.add("第" + i + "行：【偏移量】数据为空");
                    continue;
                }
                if (!StringUtil.isNumber(dataId)) {
                    errDescList.add("第" + i + "行：【点号】数据格式错误");
                    continue;
                }
                if (!StringUtil.isNumeric(coefficient)) {
                    errDescList.add("第" + i + "行：【系数】数据格式错误");
                    continue;
                }
                if (!StringUtil.isNumber(offset)) {
                    errDescList.add("第" + i + "行：【偏移量】数据格式错误");
                    continue;
                }
//                if (dataIds.contains(Long.parseLong(dataId))) {
//                    errDescList.add("第" + i + "行：【点号】数据已重复, 请检查数据");
//                    continue;
//                }
                PointTableEntity pointTable = new PointTableEntity();
                //校验功能点是否重复 重复则修改
                String deviceFunctionId;
                if (StringUtil.isEmpty(functionIndex)) {
                    deviceFunctionId = deviceId + FileUtil.COMMA + functionId;
                } else {
                    deviceFunctionId = deviceId + FileUtil.COMMA + functionId + FileUtil.COMMA + functionIndex;
                }
                if (deviceFunctionMap.containsKey(deviceFunctionId)) {
                    pointTable.setId(deviceFunctionMap.get(deviceFunctionId));
                }
                pointTable.setChannelId(channelId);
                pointTable.setDeviceId(deviceId);
                pointTable.setFunctionId(functionId);
                pointTable.setDataId(Long.parseLong(dataId));
                pointTable.setCoefficient(Float.parseFloat(coefficient));
                pointTable.setOffset(Integer.parseInt(offset));
                pointTableList.add(pointTable);
            }
            //批量添加设备数据
            if (CollectionUtils.isNotEmpty(pointTableList)) {
                pointTableList = pointTableDao.saveAll(pointTableList);
            }

            //更新redis里面的功能点映射数据
            //根据通道id查询通道数据
            Optional<ChannelEntity> optional = channelDao.findById(channelId);
            if (optional.isPresent()) {
                ChannelEntity channel = optional.get();
                List<PointTableEntity> pointTableEntityList = Lists.newArrayList();
                //根据通道id查询最新的点表数据
                pointTableEntityList.addAll(pointTableDao.findAllByChannelId(channelId));
                pointTableEntityList.addAll(pointTableList);
                pointTableEntityList = pointTableEntityList.stream().distinct().collect(Collectors.toList());
                Set<String> functionIds = pointTableEntityList.stream().map(PointTableEntity::getFunctionId).collect(Collectors.toSet());
                Map<String, FunctionEntity> functionMap = functionDao.findAllById(functionIds).stream().collect(Collectors.toMap(FunctionEntity::getId,
                        a -> a, (k1, k2) -> k1));
                //校验redis是否存在 不存在则添加
                DeviceModel deviceModel = RedisDeviceUtil.getDevice(channel.getDeviceNumber());
                if (deviceModel != null) {
                    String channelKey = channel.getProtocolType();
                    if (deviceModel.getChannelMap().containsKey(channelKey)) {
                        ChannelModel redisChannel = deviceModel.getChannelMap().get(channelKey);

                        Map<String, String> functionPointMap = Maps.newConcurrentMap();
                        Map<String, PointTableModel> pointTableMap = redisChannel.getPointTableMap();
                        pointTableEntityList.forEach(pointTable -> {
                            if (functionMap.containsKey(pointTable.getFunctionId())) {
                                FunctionEntity function = functionMap.get(pointTable.getFunctionId());
                                String key = pointTable.getDeviceId() + FileUtil.COLON + function.getFunctionLogo();
                                String value = KeyUtil.POINT_KEY + pointTable.getDataId();
                                functionPointMap.put(key, value);

                                //功能点里面增加系数，偏移量,精度，数据对象等字段
                                PointTableModel pointTableModel;
                                if (pointTableMap.containsKey(value)) {
                                    pointTableModel = pointTableMap.get(value);
                                    pointTableModel.setDataId(pointTable.getDataId());
                                    pointTableModel.setDataType(function.getDataType());
                                } else {
                                    pointTableModel = new PointTableModel();
                                    pointTableModel.setDataType(function.getDataType());
                                    pointTableModel.setDataId(pointTable.getDataId());
                                }
                                pointTableModel.setCoefficient(pointTable.getCoefficient());
                                pointTableModel.setOffset(pointTable.getOffset());
                                pointTableModel.setFieldCode(function.getFunctionLogo());
                                pointTableModel.setAccuracy(function.getAccuracy());
                                pointTableModel.setDataObject(function.getDataObject());
                                pointTableModel.setValueRange(function.getValueRange());
                                pointTableMap.put(value, pointTableModel);
                            }
                        });
                        redisChannel.setFunctionPointMap(functionPointMap);
                        redisChannel.setPointTableMap(pointTableMap);
                        deviceModel.getChannelMap().put(channelKey, redisChannel);
                    }
                    RedisDeviceUtil.setDevice(channel.getDeviceNumber(), deviceModel);
                }
            }
            result.setNormalNum(totalRows - 1 - errDescList.size());
            result.setErrorNum(errDescList.size());
            result.setErrDescList(errDescList);
            return ResponseResult.ok(result);
        } catch (IOException e) {
            log.error("解析文件内容异常", e);
            return ResponseResult.error(ResponseResult.PARAM_PARSE_ERROR);
        }
    }

    /**
     * 更新设备通用缓存状态
     *
     * @param deviceNumber 设备编号
     * @param accessType   接入类型 1-直连设备 2-网关设备
     * @param updateType   更新类型 1-注册 2-注销
     * @return 状态码
     */
    private Boolean checkGeneralDeviceStatus(String deviceNumber, Integer accessType, Integer updateType) {
        AtomicReference<Boolean> flag = new AtomicReference<>(false);
        //定义枪状态
        List<Integer> gunStatusList = Arrays.asList(1, 2, 4, 5);
        //直连设备
        if (accessType == 1) {
            RedisGeneralUtil.executePile(deviceNumber, () -> {
                PileRealModel pileRealModel = RedisGeneralUtil.getPileRealModel(deviceNumber);
                if (pileRealModel != null && StringUtil.isNotEmpty(pileRealModel.getWorkStatus())) {
                    if (updateType == 1) { //注册 默认给离线
                        pileRealModel.setWorkStatus(88);
                        pileRealModel.getGunRealModelMap().forEach((gunCode, gunRealModel) -> gunRealModel.setGunStatus(88));
                    }
                    if (updateType == 2) { //注销 默认给未注册
                        pileRealModel.setWorkStatus(0);
                        List<PileRealModel.GunRealModel> gunRealList = pileRealModel.getGunRealModelMap().values().stream().filter(g -> g != null
                                && StringUtil.isNotEmpty(g.getGunStatus()) && gunStatusList.contains(g.getGunStatus())).collect(Collectors.toList());
                        if (!gunRealList.isEmpty()) {
                            flag.set(true);
                        }
                        pileRealModel.getGunRealModelMap().forEach((gunCode, gunRealModel) -> gunRealModel.setGunStatus(-1));
                    }
                    if (!flag.get()) {
                        RedisGeneralUtil.setPileRealModel(deviceNumber, pileRealModel);
                    }
                }
            });
        }
        //网关下的设备
        if (accessType == 2) {
            RedisGeneralUtil.executeGateway(deviceNumber, () -> {
                GatewayRealModel gatewayRealModel = RedisGeneralUtil.getGatewayRealModel(deviceNumber);
                if (gatewayRealModel != null && StringUtil.isNotEmpty(gatewayRealModel.getDeviceStatus())) {
                    if (updateType == 1) { //注册 默认给离线
                        gatewayRealModel.setDeviceStatus(88);
                        gatewayRealModel.getSubDeviceRealMap().replaceAll((k, v) -> 88);
                    }
                    if (updateType == 2) { //注销 默认给未注册
                        gatewayRealModel.setDeviceStatus(0);
                        gatewayRealModel.getSubDeviceRealMap().replaceAll((k, v) -> 0);
                    }
                    gatewayRealModel.getSubDeviceRealMap().forEach((pileCode, workStatus) ->
                            RedisGeneralUtil.executePile(pileCode, () -> {
                                PileRealModel pileRealModel = RedisGeneralUtil.getPileRealModel(pileCode);
                                if (pileRealModel != null && StringUtil.isNotEmpty(pileRealModel.getWorkStatus())) {
                                    if (updateType == 1) { //注册 默认给离线
                                        pileRealModel.setWorkStatus(88);
                                        pileRealModel.getGunRealModelMap().forEach((gunCode, gunRealModel) -> gunRealModel.setGunStatus(88));
                                    }
                                    if (updateType == 2) { //注销 默认给未注册
                                        pileRealModel.setWorkStatus(0);
                                        List<PileRealModel.GunRealModel> gunRealList = pileRealModel.getGunRealModelMap().values().stream().filter(g -> g != null
                                                && StringUtil.isNotEmpty(g.getGunStatus()) && gunStatusList.contains(g.getGunStatus())).collect(Collectors.toList());
                                        if (!gunRealList.isEmpty()) {
                                            flag.set(true);
                                        }
                                        pileRealModel.getGunRealModelMap().forEach((gunCode, gunRealModel) -> gunRealModel.setGunStatus(-1));
                                    }
                                    if (!flag.get()) {
                                        RedisGeneralUtil.setPileRealModel(pileCode, pileRealModel);
                                    }
                                }
                            }));
                    if (!flag.get()) {
                        RedisGeneralUtil.setGatewayRealModel(deviceNumber, gatewayRealModel);
                    }
                }
            });
        }
        return flag.get();
    }

}
