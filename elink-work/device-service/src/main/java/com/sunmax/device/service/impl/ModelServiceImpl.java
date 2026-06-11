package com.sunmax.device.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.sunmax.common.config.redis.KeyUtil;
import com.sunmax.common.config.redis.RedisDeviceUtil;
import com.sunmax.common.config.redis.RedisModelEventUtil;
import com.sunmax.common.config.redis.RedisUtil;
import com.sunmax.common.dto.PageDto;
import com.sunmax.common.dto.device.AssetTypeDto;
import com.sunmax.common.dto.device.ModelDetailDto;
import com.sunmax.common.dto.device.ModelFunctionListDto;
import com.sunmax.common.dto.system.UserDto;
import com.sunmax.common.entity.BaseEntity;
import com.sunmax.common.entity.BaseTimeEntity;
import com.sunmax.common.model.DeviceModel;
import com.sunmax.common.model.EventModel;
import com.sunmax.common.model.FunctionModel;
import com.sunmax.common.util.CommonUtil;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.util.StringUtil;
import com.sunmax.common.util.oss.FileUtil;
import com.sunmax.common.vo.statics.DeviceParamVo;
import com.sunmax.device.constants.DBConstants;
import com.sunmax.device.dao.access.DeviceDao;
import com.sunmax.device.dao.access.DeviceTopologyDao;
import com.sunmax.device.dao.access.GatewaySubDeviceDao;
import com.sunmax.device.dao.model.*;
import com.sunmax.device.dto.ImportResultDto;
import com.sunmax.device.dto.model.*;
import com.sunmax.device.entity.access.DeviceEntity;
import com.sunmax.device.entity.model.*;
import com.sunmax.device.enums.LevelTypeEnum;
import com.sunmax.device.service.ModelService;
import com.sunmax.device.service.feign.DataService;
import com.sunmax.device.service.feign.SystemService;
import com.sunmax.device.util.AssetTypeUtil;
import com.sunmax.device.util.DeviceCommonUtil;
import com.sunmax.device.vo.model.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import jakarta.persistence.criteria.Predicate;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ModelServiceImpl implements ModelService {

    @Autowired
    private ModelDao modelDao;

    @Autowired
    private ModelReaDao modelReaDao;

    @Autowired
    private ModelFunctionDao modelFunctionDao;

    @Autowired
    private FunctionDao functionDao;

    @Autowired
    private ReaDao reaDao;

    @Autowired
    private ModelTopologyDao modelTopologyDao;

    @Autowired
    private DeviceDao deviceDao;

    @Autowired
    private DeviceTopologyDao deviceTopologyDao;

    @Autowired
    private ModelShowDao modelShowDao;

    @Autowired
    private ModelEventDao modelEventDao;

    @Autowired
    private SystemService systemService;

    @Autowired
    private AssetTypeDao assetTypeDao;

    @Autowired
    private DataService dataService;

    @Autowired
    private GatewaySubDeviceDao subDeviceDao;

    @Autowired
    private PileFaultDao pileFaultDao;

    @Override
    @Transactional(transactionManager = DBConstants.JPA_TX_MODEL, rollbackFor = Exception.class)
    public ResponseResult<Void> saveModel(ModelChangeVo modelChangeVo, MultipartFile logoFile) {
        //根据模型名称查询数据库是否存在
        List<ModelEntity> checkList = modelDao.findAll(Example.of(ModelEntity.builder().modelName(modelChangeVo.getModelName()).build()));
        //新增模型数据
        if (StringUtil.isEmpty(modelChangeVo.getId())) {
            //校验模型名称是否存在
            if (CollectionUtils.isNotEmpty(checkList)) {
                return ResponseResult.paramShow(modelChangeVo.getModelName(), ResponseResult.PARAM_EXIST);
            }
            ModelEntity modelEntity = new ModelEntity();
            BeanUtils.copyProperties(modelChangeVo, modelEntity);
            modelEntity.setModelStatus(0);
            modelEntity.setCreateId(modelChangeVo.getUserId());
            modelEntity.setUpdateId(modelChangeVo.getUserId());
            if (logoFile != null && !logoFile.isEmpty()) {
                modelEntity.setLogoPath(FileUtil.getImagePath(logoFile, null));
            }
            modelDao.save(modelEntity);
            return ResponseResult.ok();
        } else { //编辑模型数据
            //校验模型名称是否存在
            checkList = checkList.stream().filter(s -> !Objects.equals(s.getId(), modelChangeVo.getId())).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(checkList)) {
                return ResponseResult.paramShow(modelChangeVo.getModelName(), ResponseResult.PARAM_EXIST);
            }
            Optional<ModelEntity> optional = modelDao.findById(modelChangeVo.getId());
            if (optional.isPresent()) {
                ModelEntity modelEntity = optional.get();
                modelEntity.setModelName(modelChangeVo.getModelName());
                modelEntity.setModelDesc(modelChangeVo.getModelDesc());
                modelEntity.setUpdateId(modelChangeVo.getUserId());
                if (logoFile != null && !logoFile.isEmpty()) {
                    modelEntity.setLogoPath(FileUtil.getImagePath(logoFile, modelEntity.getLogoPath()));
                }
                modelDao.save(modelEntity);
                return ResponseResult.ok();
            }
        }
        return ResponseResult.paramError(ResponseResult.PARAM_ERROR);
    }

    @Override
    public ResponseResult<PageDto<ModelListDto>> queryModelList(ModelQueryVo modelQueryVo) {
        //返回的集合
        List<ModelListDto> resultList = Lists.newArrayList();

        Page<ModelEntity> modelPage = modelDao.findAll((Specification<ModelEntity>) (root, cq, cb) -> {
            List<Predicate> list = new ArrayList<>();
            if (StringUtil.isNotEmpty(modelQueryVo.getTypeId())) {
                list.add(cb.equal(root.get("typeId"), modelQueryVo.getTypeId())); //设备类型id
            }
            if (StringUtil.isNotEmpty(modelQueryVo.getKeyword())) { //关键字(模型ID+模型名称)
                list.add(cb.or(cb.like(root.get("id"), "%" + modelQueryVo.getKeyword() + "%"),
                        cb.like(root.get("modelName"), "%" + modelQueryVo.getKeyword() + "%")));
            }
            if (StringUtil.isNotEmpty(modelQueryVo.getModelStatus())) { //模型状态 0-开发中 1-已发布
                list.add(cb.equal(root.get("modelStatus"), modelQueryVo.getModelStatus()));
            }
            return cb.and(list.toArray(new Predicate[0]));
        }, PageRequest.of(modelQueryVo.getPage() - 1, modelQueryVo.getSize(), Sort.by("createTime").descending()));
        if (CollectionUtils.isNotEmpty(modelPage.getContent())) {
            //获取模型多个设备类型id和多个模型id
            Set<String> typeIds = Sets.newHashSet();
            Set<String> modelIds = Sets.newHashSet();
            modelPage.getContent().forEach(model -> {
                typeIds.add(model.getTypeId());
                modelIds.add(model.getId());
            });
            //根据多个资产分类id查询资产分类名称
            Map<String, String> typeNameMap = AssetTypeUtil.getTypeNameMap(typeIds, assetTypeDao);

            //根据模型id查询设备数量
            Map<String, Long> deviceNumMap = deviceDao.findAllByModelIdInAndIsDelete(modelIds, 1).stream().collect(Collectors
                    .groupingBy(DeviceEntity::getModelId, Collectors.counting()));

            //对数据进行组装
            resultList = modelPage.getContent().stream().map(model -> {
                ModelListDto result = new ModelListDto();
                BeanUtils.copyProperties(model, result);
                result.setTypeName(typeNameMap.getOrDefault(model.getTypeId(), null));
                result.setDeviceNum(deviceNumMap.getOrDefault(model.getId(), 0L));
                return result;
            }).collect(Collectors.toList());
        }
        return ResponseResult.ok(new PageDto<>(resultList, modelPage.getNumber() + 1, modelPage.getSize(), (int) modelPage.getTotalElements()));
    }

    @Override
    @Transactional(transactionManager = DBConstants.JPA_TX_MODEL, rollbackFor = Exception.class)
    public ResponseResult<Void> updateModelStatus(String id, String userId) {
        Optional<ModelEntity> optional = modelDao.findById(id);
        if (optional.isPresent()) {
            ModelEntity modelEntity = optional.get();
            modelEntity.setModelStatus(1);
            modelEntity.setUpdateId(userId);
            modelDao.save(modelEntity);
            return ResponseResult.ok();
        }
        return ResponseResult.paramError(ResponseResult.PARAM_ERROR);
    }

    @Override
    @Transactional(transactionManager = DBConstants.JPA_TX_MODEL, rollbackFor = Exception.class)
    public ResponseResult<Void> deleteModelById(String id) {
        //根据模型id查询模型数据
        Optional<ModelEntity> optional = modelDao.findById(id);
        if (optional.isPresent()) {
            //删除模型数据
            modelDao.delete(optional.get());
            //删除模型关联扩展属性数据
            modelReaDao.deleteInBatch(modelReaDao.findAllByModelId(id));
            //删除模型关联标准功能数据
            modelFunctionDao.deleteInBatch(modelFunctionDao.findAllByModelId(id));
            //删除模型拓扑节点数据
            List<ModelTopologyEntity> modelTopologyList = modelTopologyDao.findAllByModelId(id);
            modelTopologyDao.deleteInBatch(modelTopologyList);

            //删除设备关联拓扑数据
            Set<String> nodeIds = modelTopologyList.stream().map(ModelTopologyEntity::getId).collect(Collectors.toSet());
            deviceTopologyDao.deleteAllByLeftNodeIdInOrRightNodeIdIn(nodeIds, nodeIds);

            //删除模型关联设备数据
            List<DeviceEntity> deviceList = deviceDao.findAllByModelIdInAndIsDelete(Collections.singleton(id), 1);
            List<DeviceEntity> deviceCacheList = deviceList.stream().filter(d -> StringUtil.isNotEmpty(d.getDeviceNumber())).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(deviceCacheList)) {
                Set<String> tableNames = Sets.newHashSet();
                deviceCacheList.forEach(device -> {
                    String deviceKey = KeyUtil.DEVICE_KEY + device.getDeviceNumber();
                    tableNames.add(deviceKey);
                    DeviceModel deviceModel = RedisDeviceUtil.getDevice(device.getDeviceNumber());
                    if (deviceModel != null && deviceModel.getChannelMap() != null && !deviceModel.getChannelMap().isEmpty()) {
                        tableNames.addAll(deviceModel.getChannelMap().keySet().stream().map(key -> key + device.getDeviceNumber()).collect(Collectors.toSet()));
                    }
                    RedisUtil.delete(deviceKey);
                });
                //删除设备taos数据库数据
                dataService.deleteAllDataStoreTable(tableNames);
            }
            deviceDao.deleteAllByModelIdAndIsDelete(id, 1);

            //删除网关关联子设备绑定数据
            subDeviceDao.deleteAllByGatewayIdIn(deviceList.stream().filter(d -> Objects.equals(d.getAccessType(), 2)).map(BaseEntity::getId).collect(Collectors.toSet()));
            return ResponseResult.ok();
        }
        return ResponseResult.paramError(ResponseResult.PARAM_ERROR);
    }

    @Override
    public ResponseResult<ModelDetailDto> findModelDetailById(String id) {
        //返回的对象
        ModelDetailDto result = new ModelDetailDto();
        //根据模型id查询模型数据
        Optional<ModelEntity> optional = modelDao.findById(id);
        if (optional.isPresent()) {
            ModelEntity modelEntity = optional.get();
            BeanUtils.copyProperties(modelEntity, result);
            //根据资产分类id获取资产分类名称
            result.setTypeName(AssetTypeUtil.getTypeNameMap(Collections.singleton(modelEntity.getTypeId()), assetTypeDao).get(modelEntity.getTypeId()));
            //根据模型id查询设备数量
            result.setDeviceNum(deviceDao.findAllByModelIdInAndIsDelete(Collections.singleton(id), 1).size());
            //根据创建人id和修改人id查询创建人名称和修改人名称
            Map<String, UserDto> userMap = systemService.findUserInfoByIdsFeign(Arrays.asList(modelEntity.getCreateId(), modelEntity.getUpdateId())).getData();
            if (userMap.containsKey(modelEntity.getCreateId())) {
                result.setCreateName(userMap.get(modelEntity.getCreateId()).getFullName());
            }
            if (userMap.containsKey(modelEntity.getUpdateId())) {
                result.setUpdateName(userMap.get(modelEntity.getUpdateId()).getFullName());
            }
        }
        return ResponseResult.ok(result);
    }

    @Override
    @Transactional(transactionManager = DBConstants.JPA_TX_MODEL, rollbackFor = Exception.class)
    public ResponseResult<Void> bindModelFunctionData(String modelId, String functionIds) {
        //根据模型id查询关联设备显示功能数据
        Optional<ModelShowEntity> optional = modelShowDao.findByModelId(modelId);
        ModelShowEntity modelShow = new ModelShowEntity();
        if (optional.isPresent()) {
            modelShow = optional.get();
        } else {
            modelShow.setModelId(modelId);
        }
        modelShow.setModelFunctionIds(functionIds);
        modelShowDao.save(modelShow);
        return ResponseResult.ok();
    }

    @Override
    public ResponseResult<ModelDeviceDto> findModelDeviceListByModelId(String modelId, String keyword, Integer page, Integer size) {
        //返回的对象
        ModelDeviceDto result = new ModelDeviceDto();

        //获取模型静态字段数据
        result.getFieldDataList().add(ModelDeviceDto.FieldData.builder().functionLogo(DeviceParamVo.DEVICE_ID).functionName("设备ID").dataType(7).fieldType(1).build());
        result.getFieldDataList().add(ModelDeviceDto.FieldData.builder().functionLogo(DeviceParamVo.DEVICE_NAME).functionName("设备名称").dataType(7).fieldType(1).build());
        //设备状态 0-未注册 1-在线 2-故障 88-离线
        result.getFieldDataList().add(ModelDeviceDto.FieldData.builder().functionLogo(DeviceParamVo.TX_STATUS).functionName("通信状态")
                .dataObject("[{id:0,name:\"未注册\"},{id:1,name:\"在线\"},{id:2,name:\"维护\"},{id:3,name:\"状态\"},{id:88,name:\"离线\"}]").dataType(1).fieldType(1).build());
        //根据模型id查询显示标准功能数据
        Optional<ModelShowEntity> optional = modelShowDao.findByModelId(modelId);
        if (optional.isPresent() && StringUtil.isNotEmpty(optional.get().getModelFunctionIds())) {
            //根据多个模型标准功能关联id查询标准功能字段数据
            List<String> modelFunctionIds = JSON.parseArray(optional.get().getModelFunctionIds(), String.class);
            Map<String, String> modelFunctionMap = modelFunctionDao.findAllById(modelFunctionIds).stream().collect(Collectors
                    .toMap(ModelFunctionEntity::getFunctionId, ModelFunctionEntity::getId, (k1, k2) -> k1));
            result.getFieldDataList().addAll(functionDao.findAllById(modelFunctionMap.keySet()).stream().map(function -> {
                ModelDeviceDto.FieldData fieldData = new ModelDeviceDto.FieldData();
                BeanUtils.copyProperties(function, fieldData);
                fieldData.setId(modelFunctionMap.getOrDefault(function.getId(), null));
                fieldData.setFieldType(2);
                return fieldData;
            }).collect(Collectors.toList()));
        }
        //根据模型id获取设备数据
        List<Map<String, Object>> deviceDataList = Lists.newArrayList();
        List<DeviceEntity> deviceList = deviceDao.findAllByModelIdInAndIsDelete(Collections.singleton(modelId), 1);
        if (CollectionUtils.isNotEmpty(deviceList)) {
            Map<String, Integer> functionLogoMap = result.getFieldDataList().stream().collect(Collectors.toMap(ModelDeviceDto.FieldData::getFunctionLogo,
                    ModelDeviceDto.FieldData::getDataType, (k1, k2) -> k1));
            deviceDataList = deviceList.stream().filter(device -> {
                if (StringUtil.isNotEmpty(keyword)) { //关键词查询
                    return device.getId().contains(keyword) || device.getDeviceName().contains(keyword);
                }
                return true;
            }).map(device -> {
                Map<String, Object> dataMap = Maps.newHashMap();
                if (functionLogoMap.containsKey(DeviceParamVo.DEVICE_ID)) {
                    dataMap.put(DeviceParamVo.DEVICE_ID, device.getId());
                }
                if (functionLogoMap.containsKey(DeviceParamVo.DEVICE_NAME)) {
                    dataMap.put(DeviceParamVo.DEVICE_NAME, device.getDeviceName());
                }
                //设备状态 动态字段数据
                if (StringUtil.isNotEmpty(device.getDeviceNumber())) {
                    DeviceModel deviceModel = RedisDeviceUtil.getDevice(device.getDeviceNumber());
                    if (deviceModel != null) {
                        if (functionLogoMap.containsKey(DeviceParamVo.TX_STATUS)) { //设备通信状态
                            dataMap.put(DeviceParamVo.TX_STATUS, deviceModel.getTxStatus());
                        }
                        //动态字段解析
                        functionLogoMap.keySet().stream().filter(functionLogo -> {
                            List<String> filterFunctionLogo = Arrays.asList(DeviceParamVo.DEVICE_ID, DeviceParamVo.DEVICE_NAME, DeviceParamVo.TX_STATUS);
                            return !filterFunctionLogo.contains(functionLogo);
                        }).forEach(functionLogo -> {
                            if (deviceModel.getFunctionMap().containsKey(functionLogo)) {
                                dataMap.put(functionLogo, deviceModel.getFunctionMap().get(functionLogo).getDataValue());
                            }
                        });
                    }
                }
                return dataMap;
            }).collect(Collectors.toList());
        }
        result.setDeviceDataPage(new PageDto<>(deviceDataList, page, size));
        return ResponseResult.ok(result);
    }

    @Override
    public ResponseResult<PageDto<ModelFunctionListDto>> findModelFunctionListByModelId(String modelId, String functionName, Integer page, Integer size) {
        //根据模型id获取模型标准功能关联数据
        List<ModelFunctionListDto> resultList = DeviceCommonUtil.getFunctionListByModeId(modelId);
        if (StringUtil.isNotEmpty(functionName)) {
            resultList = resultList.stream().filter(m -> m.getFunctionName().contains(functionName)).collect(Collectors.toList());
        }
        resultList = resultList.stream().sorted(Comparator.comparing(ModelFunctionListDto::getSerialNum)).collect(Collectors.toList());
        return ResponseResult.ok(new PageDto<>(resultList, page, size));
    }

    @Override
    public ResponseResult<ModelBindFunctionDto> findModelBindFunctionByModelId(String modelId) {
        //返回的对象
        ModelBindFunctionDto result = new ModelBindFunctionDto();
        //根据模型id查询模型类型id
        String typeId = modelDao.findById(modelId).orElse(new ModelEntity()).getTypeId();
        if (StringUtil.isEmpty(typeId)) {
            return ResponseResult.paramError("未获取到该模型下面的设备类型");
        }
        //查询所有正常的标准功能数据
        List<FunctionEntity> functionList = functionDao.findAllByTypeIdAndIsDelete(typeId, 1);
        //根据模型id查询所有正常的标准功能数据
        Map<String, ModelFunctionEntity> modelFunctionMap = modelFunctionDao.findAllByModelIdAndIsDelete(modelId, 1).stream().collect(Collectors
                .toMap(ModelFunctionEntity::getFunctionId, a -> a, (k1, k2) -> k1));

        //定义已选中数据列表和未选中数据列表
        List<ModelBindFunctionDto.FunctionData> checkList = Lists.newArrayList();
        List<ModelBindFunctionDto.FunctionData> uncheckList = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(functionList)) {
            functionList.forEach(function -> {
                ModelBindFunctionDto.FunctionData functionData = new ModelBindFunctionDto.FunctionData();
                BeanUtils.copyProperties(function, functionData);
                if (modelFunctionMap.containsKey(function.getId())) {
                    ModelFunctionEntity modelFunction = modelFunctionMap.get(function.getId());
                    functionData.setSerialNum(modelFunction.getSerialNum());
                    functionData.setDataType(modelFunction.getDataType());
                    functionData.setUnit(modelFunction.getUnit());
                    checkList.add(functionData);
                } else {
                    uncheckList.add(functionData);
                }
            });
        }
        result.setCheckList(checkList.stream().sorted(Comparator.comparing(ModelBindFunctionDto.FunctionData::getSerialNum)).collect(Collectors.toList()));
        result.setUncheckList(uncheckList);
        return ResponseResult.ok(result);
    }

    @Override
    @Transactional(transactionManager = DBConstants.JPA_TX_MODEL, rollbackFor = Exception.class)
    public ResponseResult<Void> modelBindFunctionData(String modelId, Map<String, Integer> functionMap, Integer type) {
        if (type == 1) {
            //先删除之前已绑定的数据
            List<ModelFunctionEntity> deleteData = modelFunctionDao.findAllByModelIdAndIsDelete(modelId, 1);
            if (CollectionUtils.isNotEmpty(deleteData)) {
                modelFunctionDao.deleteInBatch(deleteData);
            }
            //根据多个功能点id查询功能点数据
            Map<String, FunctionEntity> functionDataMap = functionDao.findAllById(functionMap.keySet()).stream().collect(Collectors
                    .toMap(FunctionEntity::getId, a -> a));
            //绑定最新的数据
            Set<ModelFunctionEntity> addData = functionMap.entrySet().stream().map(function -> {
                ModelFunctionEntity modelFunction = new ModelFunctionEntity();
                modelFunction.setModelId(modelId);
                modelFunction.setFunctionId(function.getKey());
                modelFunction.setSerialNum(function.getValue());
                if (functionDataMap.containsKey(function.getKey())) {
                    FunctionEntity functionEntity = functionDataMap.get(function.getKey());
                    modelFunction.setDataType(functionEntity.getDataType());
                    modelFunction.setAccuracy(functionEntity.getAccuracy());
                    modelFunction.setValueRange(functionEntity.getValueRange());
                    modelFunction.setUnit(functionEntity.getUnit());
                    modelFunction.setDataObject(functionEntity.getDataObject());
                }
                modelFunction.setIsDelete(1);
                return modelFunction;
            }).collect(Collectors.toSet());
            modelFunctionDao.saveAll(addData);

            //最新功能点存入到设备缓存中
            List<DeviceEntity> deviceList = deviceDao.findAllByModelIdInAndIsDelete(Collections.singleton(modelId), 1).stream()
                    .filter(s -> StringUtil.isNotEmpty(s.getDeviceNumber())).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(deviceList)) {
                //根据功能点id查询功能点名称和类型
                Map<String, FunctionEntity> functionMaps = functionDao.findAllById(functionMap.keySet()).stream().collect(Collectors.toMap(FunctionEntity::getFunctionLogo,
                        a -> a, (k1, k2) -> k1));
                deviceList.forEach(device -> {
                    try {
                        DeviceModel deviceModel = RedisDeviceUtil.getDevice(device.getDeviceNumber());
                        if (deviceModel != null) {
                            functionMaps.forEach((key, function) -> {
                                FunctionModel functionModel = new FunctionModel();
                                if (deviceModel.getFunctionMap().containsKey(key)) { //缓存不存在 则新增
                                    functionModel = deviceModel.getFunctionMap().get(key);
                                }
                                functionModel.setFunctionLogo(function.getFunctionLogo());
                                functionModel.setDataType(function.getDataType());
                                functionModel.setFieldCode(function.getFieldCode());
                                functionModel.setAccuracy(function.getAccuracy());
                                functionModel.setDataObject(function.getDataObject());
                                functionModel.setValueRange(function.getValueRange());
                                deviceModel.getFunctionMap().put(key, functionModel);
                            });
                            //删除缓存不存在的key
                            List<String> deleteKeys = Lists.newArrayList();
                            deviceModel.getFunctionMap().forEach((key, value) -> {
                                if (!functionMaps.containsKey(key)) {
                                    deleteKeys.add(key);
                                }
                            });
                            deleteKeys.forEach(key -> deviceModel.getFunctionMap().remove(key));
                            RedisDeviceUtil.setDevice(device.getDeviceNumber(), deviceModel);
                        }
                    } catch (RuntimeException e) {
                        log.error("模型编辑功能点数据存redis报错", e);
                    }
                });
            }
        }
        if (type == 2) {
            //删除之前已绑定的数据
            List<ModelFunctionEntity> deleteData = modelFunctionDao.findAllByModelIdAndFunctionIdIn(modelId, Lists.newArrayList(functionMap.keySet()));
            if (CollectionUtils.isNotEmpty(deleteData)) {
                modelFunctionDao.deleteInBatch(deleteData);
            }

            //删除设备已绑定功能点
            List<DeviceEntity> deviceList = deviceDao.findAllByModelIdInAndIsDelete(Collections.singleton(modelId), 1).stream()
                    .filter(s -> StringUtil.isNotEmpty(s.getDeviceNumber())).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(deviceList)) {
                //根据功能点id查询功能点名称和类型
                Map<String, Integer> functionMaps = functionDao.findAllById(functionMap.keySet()).stream().collect(Collectors.toMap(FunctionEntity::getFunctionLogo,
                        FunctionEntity::getFunctionType, (k1, k2) -> k1));
                deviceList.forEach(device -> {
                    DeviceModel deviceModel = RedisDeviceUtil.getDevice(device.getDeviceNumber());
                    if (deviceModel != null) {
                        //删除缓存不存在的key
                        functionMaps.forEach((key, value) -> deviceModel.getFunctionMap().remove(key));
                        RedisDeviceUtil.setDevice(device.getDeviceNumber(), deviceModel);
                    }
                });
            }
        }
        return ResponseResult.ok();
    }

    @Override
    public ResponseResult<PageDto<ModelReaListDto>> findModelReaListByModelId(String modelId, String reaName, Integer page, Integer size) {
        List<ModelReaListDto> resultList = Lists.newArrayList();
        //根据模型id获取模型扩展属性关联数据
        List<ModelReaEntity> modelReaList = modelReaDao.findAllByModelId(modelId);
        if (CollectionUtils.isNotEmpty(modelReaList)) {
            //根据多个模型扩展属性id查询模型扩展属性数据
            Set<String> reaIds = modelReaList.stream().map(ModelReaEntity::getReaId).collect(Collectors.toSet());
            Map<String, ReaEntity> reaMap = reaDao.findAllById(reaIds).stream().collect(Collectors.toMap(ReaEntity::getId, a -> a));

            resultList = modelReaList.stream().map(modelRea -> {
                ModelReaListDto result = new ModelReaListDto();
                if (reaMap.containsKey(modelRea.getReaId())) {
                    BeanUtils.copyProperties(reaMap.get(modelRea.getReaId()), result);
                }
                result.setModelReaId(modelRea.getId());
                result.setDefaultValue(modelRea.getDefaultValue());
                return result;
            }).collect(Collectors.toList());
        }
        if (StringUtil.isNotEmpty(reaName)) {
            resultList = resultList.stream().filter(m -> m.getReaName().contains(reaName)).collect(Collectors.toList());
        }
        return ResponseResult.ok(new PageDto<>(resultList, page, size));
    }

    @Override
    public ResponseResult<ModelBindReaDto> findModelBindReaByModelId(String modelId) {
        //返回的对象
        ModelBindReaDto result = new ModelBindReaDto();
        Optional<ModelEntity> optional = modelDao.findById(modelId);
        if (optional.isPresent()) {
            //查询所有正常的扩展属性数据
            List<ReaEntity> reaList = reaDao.findAll(Example.of(ReaEntity.builder().typeId(optional.get().getTypeId()).build()));
            //根据模型id查询所有正常的扩展属性数据
            Map<String, ModelReaEntity> modelReaMap = modelReaDao.findAllByModelId(modelId).stream().collect(Collectors
                    .toMap(ModelReaEntity::getReaId, a -> a, (k1, k2) -> k1));

            //定义已选中数据列表和未选中数据列表
            List<ModelBindReaDto.ReaData> checkList = Lists.newArrayList();
            List<ModelBindReaDto.ReaData> uncheckList = Lists.newArrayList();
            if (CollectionUtils.isNotEmpty(reaList)) {
                reaList.forEach(rea -> {
                    ModelBindReaDto.ReaData reaData = new ModelBindReaDto.ReaData();
                    BeanUtils.copyProperties(rea, reaData);
                    if (modelReaMap.containsKey(rea.getId())) {
                        reaData.setDefaultValue(modelReaMap.get(rea.getId()).getDefaultValue());
                        checkList.add(reaData);
                    } else {
                        uncheckList.add(reaData);
                    }
                });
            }
            result.setCheckList(checkList);
            result.setUncheckList(uncheckList);
        }
        return ResponseResult.ok(result);
    }

    @Override
    @Transactional(transactionManager = DBConstants.JPA_TX_MODEL, rollbackFor = Exception.class)
    public ResponseResult<Void> modelBindReaData(String modelId, List<String> reaIds, Integer type) {
        if (type == 1) {
            //先删除之前已绑定的数据
            List<ModelReaEntity> deleteData = modelReaDao.findAllByModelId(modelId);
            if (CollectionUtils.isNotEmpty(deleteData)) {
                modelReaDao.deleteInBatch(deleteData);
            }
            //绑定最新的数据
//            Map<String, Integer> reaMap = reaDao.findAllById(reaIds).stream().collect(Collectors.toMap(ReaEntity::getId,
//                    ReaEntity::getReadWriteType, (k1, k2) -> k1));
            Map<String, ModelReaEntity> defaultMap = deleteData.stream().collect(Collectors.toMap(ModelReaEntity::getReaId,
                    modelReaEntity -> modelReaEntity, (k1, k2) -> k1));
            Set<ModelReaEntity> addData = reaIds.stream().map(reaId -> {
                ModelReaEntity modelRea = new ModelReaEntity();
                modelRea.setModelId(modelId);
                modelRea.setReaId(reaId);
//                modelRea.setReadWriteType(reaMap.getOrDefault(reaId, null));
                if (defaultMap.containsKey(reaId)) {
                    modelRea.setDefaultValue(defaultMap.get(reaId).getDefaultValue());
                }
                return modelRea;
            }).collect(Collectors.toSet());
            modelReaDao.saveAll(addData);
        }
        if (type == 2) {
            //删除之前已绑定的数据
            List<ModelReaEntity> deleteData = modelReaDao.findAllByModelIdAndReaIdIn(modelId, reaIds);
            if (CollectionUtils.isNotEmpty(deleteData)) {
                modelReaDao.deleteInBatch(deleteData);
            }
        }
        //校验设备扩展属性数据
        List<DeviceEntity> deviceList = deviceDao.findAllByModelIdInAndIsDelete(Collections.singleton(modelId), 1);
        if (CollectionUtils.isNotEmpty(deviceList)) {
            //根据多个扩展属性id查询扩展属性数据
            Map<String, String> reaMap = reaDao.findAllById(reaIds).stream().collect(Collectors.toMap(ReaEntity::getFieldName, ReaEntity::getReaName, (k1, k2) -> k1));
            //更新设备扩展属性数据
            deviceDao.saveAll(deviceList.stream().peek(device -> {
                if (StringUtil.isNotEmpty(device.getReadwriteObject())) {
                    JSONObject readwriteObject = new JSONObject();
                    for (Map.Entry<String, Object> data : JSON.parseObject(device.getReadwriteObject()).entrySet()) {
                        if (reaMap.containsKey(data.getKey())) {
                            readwriteObject.put(data.getKey(), data.getValue());
                        }
                    }
                    device.setReadwriteObject(JSON.toJSONString(readwriteObject));
                }
            }).collect(Collectors.toList()));
        }
        return ResponseResult.ok();
    }

    @Override
    @Transactional(transactionManager = DBConstants.JPA_TX_MODEL, rollbackFor = Exception.class)
    public ResponseResult<Void> saveModelTopology(String id, String modelId, String nodeName) {
        modelTopologyDao.save(ModelTopologyEntity.builder().id(id).modelId(modelId).nodeName(nodeName).build());
        return ResponseResult.ok();
    }

    @Override
    public ResponseResult<List<ModelTopologyDto>> findModelTopologyListByModelId(String modelId) {
        //根据模型id查询模型拓扑节点数据
        return ResponseResult.ok(modelTopologyDao.findAllByModelId(modelId).stream().map(modelTopology -> {
            ModelTopologyDto result = new ModelTopologyDto();
            BeanUtils.copyProperties(modelTopology, result);
            return result;
        }).collect(Collectors.toList()));
    }

    @Override
    @Transactional(transactionManager = DBConstants.JPA_TX_MODEL, rollbackFor = Exception.class)
    public ResponseResult<Void> deleteModelTopologyById(String id) {
        modelTopologyDao.deleteById(id);
        return ResponseResult.ok();
    }

    @Override
    public ResponseResult<List<ModelEventFunctionDto>> getModelEventFunctionList(String modelId, Integer calculateType) {
        //返回的集合
        List<ModelEventFunctionDto> resultList = Lists.newArrayList();
        //根据模型id查询模型功能关联表
        List<ModelFunctionEntity> modelFunctionList = modelFunctionDao.findAllByModelIdAndIsDelete(modelId, 1);
        if (CollectionUtils.isNotEmpty(modelFunctionList)) {
            Set<String> functionIds = modelFunctionList.stream().map(ModelFunctionEntity::getFunctionId).collect(Collectors.toSet());
            resultList = functionDao.findAllById(functionIds).stream().filter(s -> {
                //过滤掉电桩自定义字段值
//                if (StringUtil.isNotEmpty(s.getFieldCode()) && GeneralFieldEnum.getByFieldCode(s.getFieldCode()) != null) {
//                    return false;
//                }
                if (calculateType == 1) { //值运算
                    return Arrays.asList(1, 2, 3, 4, 5).contains(s.getDataType());
                }
                if (calculateType == 2) { //位运算
                    return Arrays.asList(1, 2).contains(s.getDataType());
                }
                return false;
            }).map(ModelEventFunctionDto::new).collect(Collectors.toList());
        }
        return ResponseResult.ok(resultList);
    }

    @Override
    @Transactional(transactionManager = DBConstants.JPA_TX_MODEL, rollbackFor = Exception.class)
    public ResponseResult<Void> saveModelEvent(ModelEventChangeVo eventChangeVo) {
        //校验同一个模型下面的事件名称是否存在
        List<ModelEventEntity> checkList = modelEventDao.findAll(Example.of(ModelEventEntity.builder().modelId(eventChangeVo.getModelId())
                .eventName(eventChangeVo.getEventName()).isDelete(1).build()));

        ModelEventEntity modelEvent = null;
        //新增模型事件数据
        if (StringUtil.isEmpty(eventChangeVo.getId())) {
            //校验模型名称是否存在
            if (CollectionUtils.isNotEmpty(checkList)) {
                return ResponseResult.paramShow(eventChangeVo.getEventName(), ResponseResult.PARAM_EXIST);
            }
            modelEvent = new ModelEventEntity();
            BeanUtils.copyProperties(eventChangeVo, modelEvent);
            modelEvent.setIsDelete(1);
            modelEventDao.save(modelEvent);
        } else { //编辑模型事件数据
            //校验事件名称是否存在
            checkList = checkList.stream().filter(s -> !Objects.equals(s.getId(), eventChangeVo.getId())).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(checkList)) {
                return ResponseResult.paramShow(eventChangeVo.getEventName(), ResponseResult.PARAM_EXIST);
            }
            Optional<ModelEventEntity> optional = modelEventDao.findById(eventChangeVo.getId());
            if (optional.isPresent()) {
                modelEvent = optional.get();
                BeanUtils.copyProperties(eventChangeVo, modelEvent);
                modelEvent.setIsDelete(1);
                modelEventDao.save(modelEvent);
            }
        }
        if (modelEvent != null && StringUtil.isNotEmpty(modelEvent.getEventName())) {
            //校验redis是否存在 不存在则添加
            //根据模型id查询所有模型事件
            List<EventModel> eventList = modelEventDao.findAllByModelId(eventChangeVo.getModelId()).stream().map(entity -> {
                EventModel eventModel = new EventModel();
                BeanUtils.copyProperties(entity, eventModel);
                eventModel.setEventId(entity.getId());
                return eventModel;
            }).collect(Collectors.toList());
            List<EventModel> modelEventList = RedisModelEventUtil.getModelEvent(eventChangeVo.getModelId());
            if (modelEventList != null) {
                RedisModelEventUtil.setModelEvent(eventChangeVo.getModelId(), eventList);
            }
            return ResponseResult.ok();
        }
        return ResponseResult.paramError(ResponseResult.PARAM_ERROR);
    }

    @Override
    public ResponseResult<PageDto<ModelEventListDto>> findModelEventList(ModelEventQueryVo eventQueryVo) {
        //返回的集合
        List<ModelEventListDto> resultList = Lists.newArrayList();
        //根据模型id查询模型数据
        Optional<ModelEntity> optional = modelDao.findById(eventQueryVo.getModelId());
        if (optional.isPresent()) {
            //根据查询条件查询模型事件
            Page<ModelEventEntity> modelEventPage = modelEventDao.findAll((Specification<ModelEventEntity>) (root, cq, cb) -> {
                List<Predicate> list = new ArrayList<>();
                list.add(cb.equal(root.get("isDelete"), 1));
                list.add(cb.equal(root.get("modelId"), eventQueryVo.getModelId())); //模型id
                if (StringUtil.isNotEmpty(eventQueryVo.getEventName())) { //事件名称
                    list.add(cb.like(root.get("eventName"), "%" + eventQueryVo.getEventName() + "%"));
                }
                if (StringUtil.isNotEmpty(eventQueryVo.getEventLevel())) { //事件级别 1-次要告警 2-重要告警 3-紧急告警 4-提示告警 5-离线告警
                    list.add(cb.equal(root.get("eventLevel"), eventQueryVo.getEventLevel()));
                }
                return cb.and(list.toArray(new Predicate[0]));
            }, PageRequest.of(eventQueryVo.getPage() - 1, eventQueryVo.getSize(), Sort.by("createTime").descending()));
            if (CollectionUtils.isNotEmpty(modelEventPage.getContent())) {
                //根据类型id和多个功能点标识查询功能点名称
                String typeId = optional.get().getTypeId();
                Set<String> functionLogos = modelEventPage.getContent().stream().filter(s -> StringUtil.isNotEmpty(s.getFunctionLogos()))
                        .map(s -> JSON.parseArray(s.getFunctionLogos(), String.class)).flatMap(Collection::stream).collect(Collectors.toSet());
                Map<String, String> functionNameMap = functionDao.findAllByTypeIdAndFunctionLogoInAndIsDelete(typeId, functionLogos, 1).stream().collect(Collectors
                        .toMap(FunctionEntity::getFunctionLogo, FunctionEntity::getFunctionName, (k1, k2) -> k1));
                resultList = modelEventPage.getContent().stream().map(modelEvent -> {
                    ModelEventListDto result = new ModelEventListDto();
                    BeanUtils.copyProperties(modelEvent, result);
                    if (StringUtil.isNotEmpty(modelEvent.getFunctionLogos())) {
                        JSON.parseArray(modelEvent.getFunctionLogos(), String.class).forEach(functionLogo -> {
                            if (functionNameMap.containsKey(functionLogo)) {
                                if (StringUtil.isEmpty(result.getFunctionNames())) {
                                    result.setFunctionNames(functionNameMap.get(functionLogo));
                                } else {
                                    result.setFunctionNames(result.getFunctionNames() + FileUtil.COMMA + functionNameMap.get(functionLogo));
                                }
                            }
                        });
                    }
                    return result;
                }).collect(Collectors.toList());
            }
            return ResponseResult.ok(new PageDto<>(resultList, modelEventPage.getNumber() + 1, modelEventPage.getSize(), (int) modelEventPage.getTotalElements()));
        }
        return ResponseResult.ok(new PageDto<>(resultList, eventQueryVo.getPage(), eventQueryVo.getSize()));
    }

    @Override
    public ResponseResult<ModelEventDetailDto> findModelEventById(String id) {
        //返回的对象
        ModelEventDetailDto result = new ModelEventDetailDto();
        //根据模型事件id查询模型事件数据
        Optional<ModelEventEntity> optional = modelEventDao.findById(id);
        if (optional.isPresent()) {
            ModelEventEntity modelEvent = optional.get();
            BeanUtils.copyProperties(modelEvent, result);
        }
        return ResponseResult.ok(result);
    }

    @Override
    @Transactional(transactionManager = DBConstants.JPA_TX_MODEL, rollbackFor = Exception.class)
    public ResponseResult<Void> batchDeleteModelEventByIds(List<String> ids) {
        //根据多个模型事件id查询模型事件数据
        List<ModelEventEntity> modelEventList = modelEventDao.findAllById(ids);
        if (CollectionUtils.isNotEmpty(modelEventList)) {
            //删除模型事件缓存里面的数据
            modelEventList.stream().filter(me -> StringUtil.isNotEmpty(me.getModelId())).collect(Collectors.groupingBy(ModelEventEntity::getModelId))
                    .forEach((key, value) -> {
                        List<EventModel> eventList = RedisModelEventUtil.getModelEvent(key);
                        if (CollectionUtils.isNotEmpty(eventList)) {
                            Set<String> eventIds = value.stream().map(BaseTimeEntity::getId).filter(StringUtil::isNotEmpty).collect(Collectors.toSet());
                            RedisModelEventUtil.setModelEvent(key, eventList.stream().filter(e -> !eventIds.contains(e.getEventId())).collect(Collectors.toList()));
                        }
                    });
            //删除多个模型事件数据
            modelEventList = modelEventList.stream().peek(m -> m.setIsDelete(2)).collect(Collectors.toList());
            modelEventDao.saveAll(modelEventList);
            return ResponseResult.ok();
        }
        return ResponseResult.paramError(ResponseResult.PARAM_ERROR);
    }

    @Override
    @Transactional(transactionManager = DBConstants.JPA_TX_MODEL, rollbackFor = Exception.class)
    public ResponseResult<ImportResultDto> importPileFaultList(String modelId, MultipartFile file) {
        ImportResultDto result = new ImportResultDto();
        //校验文件名是否存在
        String filename = file.getOriginalFilename();
        if (StringUtil.isEmpty(filename)) {
            return ResponseResult.paramError("文件名称为空");
        }
        List<PileFaultEntity> importFaultList = Lists.newArrayList();
        //校验文件名称格式
        if (!filename.endsWith("xls") && !filename.endsWith("xlsx")) {
            return ResponseResult.paramError("文件不是Excel文件");
        }
        //解析文件内容
        try {
            //获取第一个shell
            Sheet sheet = WorkbookFactory.create(file.getInputStream()).getSheetAt(0);
            //获取Excel的行数
            int totalRows = sheet.getPhysicalNumberOfRows();
            // 得到Excel的列数(前提是有行数)
            if (totalRows <= 1 && StringUtil.isEmpty(sheet.getRow(0))) {
                return ResponseResult.paramError("没有解析到文件中的数据");
            }
            //根据模型id查询电桩故障数据
            List<PileFaultEntity> pileFaultList = pileFaultDao.findAll(Example.of(PileFaultEntity.builder()
                    .modelId(modelId).build()));
            //故障码 -> 主键id
            Map<Integer, String> troubleCodeMap = pileFaultList.stream().collect(Collectors.toMap(PileFaultEntity::getFaultCode,
                    PileFaultEntity::getId, (k1, k2) -> k1));
            //事件名称 -> 故障码
            Map<String, Integer> eventNameMap = pileFaultList.stream().collect(Collectors.toMap(PileFaultEntity::getEventName,
                    PileFaultEntity::getFaultCode, (k1, k2) -> k1));
            //定义错误信息列表
            List<String> errDescList = Lists.newArrayList();
            for (int i = 1; i < totalRows; i++) {
                //读取左上端单元格
                Row row = sheet.getRow(i);
                PileFaultEntity pileFault = new PileFaultEntity();
                //故障码
                row.getCell(0).setCellType(CellType.STRING);
                String faultCode = row.getCell(0).getStringCellValue();
                if (StringUtil.isEmpty(faultCode)) {
                    errDescList.add("第" + i + "行：【故障码】数据为空");
                    continue;
                } else if (!StringUtil.isNumber(faultCode)) {
                    errDescList.add("第" + i + "行：【故障码】数据格式有误");
                    continue;
                }
                //数据库故障码存在 则更新不添加 获取id
                int parseFaultCode = Integer.parseInt(faultCode);
                if (troubleCodeMap.containsKey(parseFaultCode)) {
                    pileFault.setId(troubleCodeMap.get(parseFaultCode));
                }
                pileFault.setFaultCode(parseFaultCode);
                //事件名称
                String eventName = CommonUtil.getCellValue(row.getCell(1));
                if (StringUtil.isEmpty(eventName)) {
                    errDescList.add("第" + i + "行：【事件名称】数据为空");
                    continue;
                }
                //告警项名称已存在 不允许更新
                if (eventNameMap.containsKey(eventName) && Objects.equals(eventNameMap.get(eventName), parseFaultCode) && StringUtil.isEmpty(pileFault.getId())) {
                    errDescList.add("第" + i + "行：【事件名称】名称已重复 不允许添加");
                    continue;
                }
                pileFault.setEventName(eventName);
                //告警等级
                String alarmLevel = CommonUtil.getCellValue(row.getCell(2));
                if (StringUtil.isEmpty(alarmLevel)) {
                    errDescList.add("第" + i + "行：【告警等级】数据为空");
                    continue;
                } else if (LevelTypeEnum.getCode(alarmLevel) == null) {
                    errDescList.add("第" + i + "行：【告警等级】数据格式有误 请按照标头模板填写");
                    continue;
                }
                pileFault.setEventLevel(Objects.requireNonNull(LevelTypeEnum.getCode(alarmLevel)));
                pileFault.setModelId(modelId);
                importFaultList.add(pileFault);
            }
            result.setNormalNum((totalRows - 1) - errDescList.size());
            result.setErrorNum(errDescList.size());
            result.setErrDescList(errDescList);
            if (CollectionUtils.isNotEmpty(importFaultList)) {
                pileFaultDao.saveAll(importFaultList);
            }
            return ResponseResult.ok(result);
        } catch (IOException e) {
            log.error("解析导入告警事件异常", e);
            return ResponseResult.error(ResponseResult.PARAM_PARSE_ERROR);
        }
    }

    @Override
    public ResponseResult<List<PileFaultListDto>> findPileFaultListByModelId(String modelId) {
        //返回的对象列表
        List<PileFaultListDto> resultList = Lists.newArrayList();
        //根据模型id查询模型故障定义数据列表
        List<PileFaultEntity> pileFaultList = pileFaultDao.findAllByModelId(modelId);
        if (CollectionUtils.isNotEmpty(pileFaultList)) {
            resultList = pileFaultList.stream().map(modelGun -> {
                PileFaultListDto result = new PileFaultListDto();
                BeanUtils.copyProperties(modelGun, result);
                return result;
            }).collect(Collectors.toList());
        }
        return ResponseResult.ok(resultList);
    }

    @Override
    public ResponseResult<List<AssetTypeDto>> getAssetTypeList(String ids) {
        List<AssetTypeDto> resultList = Lists.newArrayList();
        List<AssetTypeDto> assetTypeList = assetTypeDao.findAll().stream().map(assetType -> {
            AssetTypeDto result = new AssetTypeDto();
            BeanUtils.copyProperties(assetType, result);
            return result;
        }).collect(Collectors.toList());
        if (StringUtil.isNotEmpty(ids)) {
            List<AssetTypeDto> finalResultList = resultList;
            JSON.parseArray(ids, String.class).forEach(id -> {
                Optional<AssetTypeDto> optional = assetTypeList.stream().filter(assetType -> assetType.getId().equals(id)).findFirst();
                optional.ifPresent(assetTypeDto -> finalResultList.addAll(setChild(assetTypeDto, assetTypeList)));
            });
        } else {
            resultList = assetTypeList;
        }
        return ResponseResult.ok(resultList);
    }

    /**
     * 获取子节点数据
     */
    private List<AssetTypeDto> setChild(AssetTypeDto parent, List<AssetTypeDto> childrenList) {
        //返回的集合
        List<AssetTypeDto> children = Lists.newArrayList();
        children.add(parent);
        children.addAll(getChildren(parent.getId(), childrenList));
        return children;
    }

    private List<AssetTypeDto> getChildren(String id, List<AssetTypeDto> childrenList) {
        List<AssetTypeDto> result = Lists.newArrayList();
        for (AssetTypeDto menu : childrenList) {
            if (id.equals(menu.getParentId())) {
                result.add(menu);
                //childrenList.remove(menu);
                result.addAll(getChildren(menu.getId(), childrenList));
            }
        }
        return result;
    }

    @Override
    @Transactional(transactionManager = DBConstants.JPA_TX_MODEL, rollbackFor = Exception.class)
    public ResponseResult<Void> savePileFault(PileFaultChangeVo pileFaultVo) {
        //根据模型id和故障码查询故障定义数据
        List<PileFaultEntity> pileFaultList = pileFaultDao.findAllByModelIdAndFaultCode(pileFaultVo.getModelId(), pileFaultVo.getFaultCode());
        if (StringUtil.isNotEmpty(pileFaultVo.getId())) {
            pileFaultList = pileFaultList.stream().filter(s -> !Objects.equals(pileFaultVo.getId(), s.getId())).collect(Collectors.toList());
        }
        if (CollectionUtils.isNotEmpty(pileFaultList)) {
            return ResponseResult.paramShow(String.valueOf(pileFaultVo.getFaultCode()), ResponseResult.PARAM_EXIST);
        }
        PileFaultEntity pileFault = new PileFaultEntity();
        BeanUtils.copyProperties(pileFaultVo, pileFault);
        pileFaultDao.save(pileFault);
        return ResponseResult.ok();
    }

    @Override
    @Transactional(transactionManager = DBConstants.JPA_TX_MODEL, rollbackFor = Exception.class)
    public ResponseResult<Void> deletePileFaultById(String id) {
        pileFaultDao.deleteById(id);
        return ResponseResult.ok();
    }

    @Override
    @Transactional(transactionManager = DBConstants.JPA_TX_MODEL, rollbackFor = Exception.class)
    public ResponseResult<Void> updateModelReaValue(String id, String defaultValue) {
        Optional<ModelReaEntity> optional = modelReaDao.findById(id);
        if (optional.isPresent()) {
            ModelReaEntity modelRea = optional.get();
            modelRea.setDefaultValue(defaultValue);
            modelReaDao.save(modelRea);
            return ResponseResult.ok();
        }
        return ResponseResult.paramError(ResponseResult.PARAM_ERROR);
    }

    @Override
    @Transactional(transactionManager = DBConstants.JPA_TX_MODEL, rollbackFor = Exception.class)
    public ResponseResult<Void> updateModelFunctionValue(ModelFunctionChangeVo modelFunctionVo) {
        if (StringUtil.isNotEmpty(modelFunctionVo.getId())) {
            Optional<ModelFunctionEntity> optional = modelFunctionDao.findById(modelFunctionVo.getId());
            if (optional.isPresent()) {
                ModelFunctionEntity modelFunction = optional.get();
                modelFunction.setDataType(modelFunctionVo.getDataType());
                modelFunction.setValueRange(modelFunctionVo.getValueRange());
                modelFunction.setAccuracy(modelFunctionVo.getAccuracy());
                modelFunction.setUnit(modelFunctionVo.getUnit());
                modelFunction.setDataObject(modelFunctionVo.getDataObject());
                modelFunctionDao.save(modelFunction);

                //最新功能点存入到设备缓存中
                List<DeviceEntity> deviceList = deviceDao.findAllByModelIdInAndIsDelete(Collections.singleton(modelFunction.getModelId()), 1).stream()
                        .filter(s -> StringUtil.isNotEmpty(s.getDeviceNumber())).collect(Collectors.toList());
                if (CollectionUtils.isNotEmpty(deviceList)) {
                    //根据功能点id查询功能点名称和类型
                    FunctionEntity function = functionDao.findById(modelFunction.getFunctionId()).orElse(new FunctionEntity());
                    deviceList.forEach(device -> {
                        try {
                            DeviceModel deviceModel = RedisDeviceUtil.getDevice(device.getDeviceNumber());
                            if (deviceModel != null) {
                                if (StringUtil.isNotEmpty(function.getFunctionLogo()) && deviceModel.getFunctionMap().containsKey(function.getFunctionLogo())) {
                                    FunctionModel functionModel = deviceModel.getFunctionMap().get(function.getFunctionLogo());
                                    functionModel.setFunctionLogo(function.getFunctionLogo());
                                    functionModel.setDataType(modelFunctionVo.getDataType());
                                    functionModel.setValueRange(modelFunctionVo.getValueRange());
                                    functionModel.setAccuracy(modelFunctionVo.getAccuracy());
                                    functionModel.setDataObject(modelFunctionVo.getDataObject());
                                    deviceModel.getFunctionMap().put(function.getFunctionLogo(), functionModel);
                                    RedisDeviceUtil.setDevice(device.getDeviceNumber(), deviceModel);
                                }
                            }
                        } catch (RuntimeException e) {
                            log.error("模型编辑功能点数据存redis报错", e);
                        }
                    });
                }
                return ResponseResult.ok();
            }
        }
        return ResponseResult.paramError(ResponseResult.PARAM_ERROR);
    }


}
