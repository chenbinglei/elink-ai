package com.sunmax.device.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.TypeReference;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.sunmax.common.config.redis.KeyUtil;
import com.sunmax.common.config.redis.RedisDeviceUtil;
import com.sunmax.common.dto.PageDto;
import com.sunmax.common.dto.data.DeviceHistoryDto;
import com.sunmax.common.dto.device.*;
import com.sunmax.common.dto.device.DeviceBasicInfoDto;
import com.sunmax.common.dto.protocol.AlarmRecordDto;
import com.sunmax.common.dto.system.OrganEmpowerListDto;
import com.sunmax.common.dto.system.TenantDetailsDto;
import com.sunmax.common.dto.system.UserDto;
import com.sunmax.common.entity.BaseEntity;
import com.sunmax.common.model.DeviceModel;
import com.sunmax.common.model.FunctionModel;
import com.sunmax.common.model.RealDataModel;
import com.sunmax.common.util.CommonUtil;
import com.sunmax.common.util.DateUtil;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.util.StringUtil;
import com.sunmax.common.util.local.LocalImageUtil;
import com.sunmax.common.util.oss.FileUtil;
import com.sunmax.common.util.oss.OssImageUtil;
import com.sunmax.common.vo.LocalParamVo;
import com.sunmax.common.vo.data.DeviceHistoryQueryVo;
import com.sunmax.common.vo.device.InterflowDeviceVo;
import com.sunmax.common.vo.device.InterflowGunVo;
import com.sunmax.common.vo.protocol.AlarmRecordQueryVo;
import com.sunmax.common.vo.statics.DeviceParamVo;
import com.sunmax.device.constants.DBConstants;
import com.sunmax.device.dao.access.*;
import com.sunmax.device.dao.model.*;
import com.sunmax.device.dto.GatewaySubDeviceDto;
import com.sunmax.device.dto.ImportResultDto;
import com.sunmax.device.dto.device.*;
import com.sunmax.device.dto.model.ModelFieldUpdateDto;
import com.sunmax.device.dto.model.ModelNameDto;
import com.sunmax.device.entity.access.*;
import com.sunmax.device.entity.model.*;
import com.sunmax.device.service.DeviceService;
import com.sunmax.device.service.feign.ConfigureService;
import com.sunmax.device.service.feign.DataService;
import com.sunmax.device.service.feign.ProtocolService;
import com.sunmax.device.service.feign.SystemService;
import com.sunmax.device.util.AssetTypeUtil;
import com.sunmax.device.util.DeviceCommonUtil;
import com.sunmax.device.vo.device.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.criteria.Predicate;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.sunmax.device.util.DeviceCommonUtil.getFunctionListByModeIds;

@Service
@Slf4j
public class DeviceServiceImpl implements DeviceService {

    @Autowired
    private DeviceDao deviceDao;

    @Autowired
    private ModelDao modelDao;

    @Autowired
    private ModelReaDao modelReaDao;

    @Autowired
    private ReaDao reaDao;

    @Autowired
    private ModelFunctionDao modelFunctionDao;

    @Autowired
    private FunctionDao functionDao;

    @Autowired
    private ModelTopologyDao modelTopologyDao;

    @Autowired
    private DeviceTopologyDao deviceTopologyDao;

    @Autowired
    private SystemService systemService;

    @Autowired
    private DeviceEventDao deviceEventDao;

    @Autowired
    private ModelEventDao modelEventDao;

    @Autowired
    private SiteInfoDao siteInfoDao;

    @Autowired
    private GatewaySubDeviceDao gatewaySubDeviceDao;

    @Autowired
    private DataService dataService;

    @Autowired
    private DeviceGunDao deviceGunDao;

    @Autowired
    private AssetTypeDao assetTypeDao;

    @Autowired
    private ProtocolService protocolService;

    @Autowired
    private ScenarioTypeDao scenarioTypeDao;

    @Autowired
    private ConfigureService configureService;

    @Autowired
    private DeviceFunctionFieldDao deviceFunctionFieldDao;

    @Override
    @Transactional(transactionManager = DBConstants.JPA_TX_ACCESS, rollbackFor = Exception.class)
    public ResponseResult<Void> saveDevice(DeviceChangeVo deviceChangeVo, MultipartFile[] imageFiles) {
        //根据设备名称查询设备数据列表
//        List<DeviceEntity> deviceNameList = deviceDao.findAllByDeviceNameAndIsDelete(deviceChangeVo.getDeviceName(), 1);
        //根据设备序列号查询设备数据列表
        List<DeviceEntity> deviceNumberList = Lists.newArrayList();
        if (StringUtil.isNotEmpty(deviceChangeVo.getDeviceNumber())) {
            deviceNumberList = deviceDao.findAllByDeviceNumberAndIsDelete(deviceChangeVo.getDeviceNumber(), 1);
        }
//        //根据设备类型id查询设备类型
//        Integer type = assetTypeDao.findById(deviceChangeVo.getTypeId()).orElse(new AssetTypeEntity()).getType();
//        if (StringUtil.isEmpty(type)) {
//            return ResponseResult.paramShow(deviceChangeVo.getTypeId(), ResponseResult.PARAM_ERROR);
//        }
        DeviceEntity device;
        String oldDeviceNumber = null;
        //新增设备数据
        if (StringUtil.isEmpty(deviceChangeVo.getId())) {
            //校验设备名称和设备序列号
//            if (CollectionUtils.isNotEmpty(deviceNameList)) {
//                return ResponseResult.paramShow(deviceChangeVo.getDeviceName(), ResponseResult.PARAM_EXIST);
//            }
            if (CollectionUtils.isNotEmpty(deviceNumberList)) {
                return ResponseResult.paramShow(deviceChangeVo.getDeviceNumber(), ResponseResult.PARAM_EXIST);
            }
            DeviceEntity deviceEntity = new DeviceEntity();
            BeanUtils.copyProperties(deviceChangeVo, deviceEntity);
            deviceEntity.setCreateId(deviceChangeVo.getUserId());
            deviceEntity.setUpdateId(deviceChangeVo.getUserId());
            deviceEntity.setIsDelete(1);
//            deviceEntity.setType(type);
            device = deviceDao.save(deviceEntity);
        } else { //编辑设备数据
            //校验设备名称和设备序列号
//            deviceNameList = deviceNameList.stream().filter(d -> !Objects.equals(d.getId(), deviceChangeVo.getId())).collect(Collectors.toList());
//            if (CollectionUtils.isNotEmpty(deviceNameList)) {
//                return ResponseResult.paramShow(deviceChangeVo.getDeviceName(), ResponseResult.PARAM_EXIST);
//            }
            deviceNumberList = deviceNumberList.stream().filter(d -> !Objects.equals(d.getId(), deviceChangeVo.getId())).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(deviceNumberList)) {
                return ResponseResult.paramShow(deviceChangeVo.getDeviceNumber(), ResponseResult.PARAM_EXIST);
            }

            Optional<DeviceEntity> optional = deviceDao.findById(deviceChangeVo.getId());
            if (optional.isPresent()) {
                DeviceEntity deviceEntity = optional.get();
                //旧的设备序列号
                oldDeviceNumber = deviceEntity.getDeviceNumber();
                if (StringUtil.isNotEmpty(deviceChangeVo.getDeviceNumber())) {
                    deviceEntity.setDeviceNumber(deviceChangeVo.getDeviceNumber());//设备序列号
                }
                if (StringUtil.isNotEmpty(deviceChangeVo.getParentId())) {
                    deviceEntity.setParentId(deviceChangeVo.getParentId()); //父节点id
                }
                if (StringUtil.isNotEmpty(deviceChangeVo.getAccessType())) {
                    deviceEntity.setAccessType(deviceChangeVo.getAccessType());
                }
                if (StringUtil.isNotEmpty(deviceChangeVo.getDeviceName())) { //设备名称
                    deviceEntity.setDeviceName(deviceChangeVo.getDeviceName());
                }
                if (StringUtil.isNotEmpty(deviceChangeVo.getDeviceDesc())) { //设备描述
                    deviceEntity.setDeviceDesc(deviceChangeVo.getDeviceDesc());
                }
                if (StringUtil.isNotEmpty(deviceChangeVo.getDeleteImagePath())) { //删除图片路径
                    if (StringUtil.isEmpty(deviceEntity.getImagePaths())) {
                        return ResponseResult.paramError("未找到对应的设备图片路径");
                    }
                    List<String> imagePaths = Lists.newArrayList(deviceEntity.getImagePaths().split(FileUtil.COMMA));
                    if (imagePaths.contains(deviceChangeVo.getDeleteImagePath())) {
                        if (LocalParamVo.FILE_TYPE) { //文件类型
                            if (StringUtil.isNotEmpty(deviceChangeVo.getDeleteImagePath())) { //删除阿里云上面的图片
                                OssImageUtil.deleteImage(FileUtil.subString(deviceChangeVo.getDeleteImagePath(), FileUtil.SLASH, FileUtil.QUESTION));
                            }
                        } else {
                            if (StringUtil.isNotEmpty(deviceChangeVo.getDeleteImagePath())) { //删除本地服务器上面的图片
                                LocalImageUtil.deleteImage(deviceChangeVo.getDeleteImagePath());
                            }
                        }
                        imagePaths.removeIf(imagePath -> imagePath.equals(deviceChangeVo.getDeleteImagePath()));
                        deviceEntity.setImagePaths(String.join(FileUtil.COMMA, imagePaths));
                    } else {
                        return ResponseResult.paramError("未找到对应的设备图片路径");
                    }
                }
                if (imageFiles != null && imageFiles.length > 0) { //上传设备图片 多张
                    List<MultipartFile> multipartFiles = Arrays.asList(imageFiles);
                    if (StringUtil.isEmpty(deviceEntity.getImagePaths())) {
                        deviceEntity.setImagePaths(FileUtil.getImagePathList(multipartFiles, null));
                    } else if (StringUtil.isNotEmpty(deviceEntity.getImagePaths())) {
                        deviceEntity.setImagePaths(deviceEntity.getImagePaths() + FileUtil.COMMA + FileUtil.getImagePathList(multipartFiles, null));
                    }
                }
                if (StringUtil.isNotEmpty(deviceChangeVo.getReadwriteObject())) {
                    deviceEntity.setReadwriteObject(deviceChangeVo.getReadwriteObject());
                }
//                deviceEntity.setType(type);
                deviceEntity.setUpdateId(deviceChangeVo.getUserId());
                device = deviceDao.save(deviceEntity);
            } else {
                return ResponseResult.paramError(ResponseResult.PARAM_ERROR);
            }
        }
        //设备数据存入redis缓存
        this.saveDeviceRedis(oldDeviceNumber, device, DeviceCommonUtil.getFunctionListByModeId(device.getModelId()));
        //推送省平台充电站信息数据
        List<String> pileTypeIds = Arrays.asList("28", "29", "30");
        pileTypeIds.stream().filter(typeId -> Objects.equals(typeId, device.getTypeId())).findFirst().ifPresent(typeId -> {
            configureService.notificationStationInfo(Collections.singleton(device.getSiteId()));
        });
        return ResponseResult.ok();
    }

    @Override
    @Transactional(transactionManager = DBConstants.JPA_TX_ACCESS, rollbackFor = Exception.class)
    public ResponseResult<ImportResultDto> batchInsertDevice(String userId, String typeId, String modelId, String siteId, String parentId, MultipartFile dataFile) {
        //返回的对象
        ImportResultDto result = new ImportResultDto();

        //校验文件名是否存在
        String filename = dataFile.getOriginalFilename();
        if (StringUtil.isEmpty(filename)) {
            return ResponseResult.paramError("文件名称为空");
        }
        //根据设备类型id查询设备类型
//        Integer type = assetTypeDao.findById(typeId).orElse(new AssetTypeEntity()).getType();
//        if (StringUtil.isEmpty(type)) {
//            return ResponseResult.paramShow(typeId, ResponseResult.PARAM_ERROR);
//        }

        //校验文件名称格式
        if (!filename.endsWith("xls") && !filename.endsWith("xlsx")) {
            return ResponseResult.paramError("文件不是Excel文件");
        }

        //根据模型id获取模型字段数据
        Map<String, ModelFieldUpdateDto> modelFieldMap = this.getModelFieldUpdateListByModelId(modelId).getData().stream().collect(Collectors.toMap(ModelFieldUpdateDto::getFieldName, a -> a, (k1, k2) -> k1));

        //查询所有设备数据
        List<DeviceEntity> checkList = deviceDao.findAll(Example.of(DeviceEntity.builder().isDelete(1).build()));
//        Set<String> deviceNames = checkList.stream().map(DeviceEntity::getDeviceName).filter(StringUtil::isNotEmpty).collect(Collectors.toSet());
        Set<String> deviceNumbers = checkList.stream().map(DeviceEntity::getDeviceNumber).filter(StringUtil::isNotEmpty).collect(Collectors.toSet());

        List<DeviceEntity> deviceList = Lists.newArrayList();
        //解析文件内容
        try {
            //获取第一个shell
            Sheet sheet = WorkbookFactory.create(dataFile.getInputStream()).getSheetAt(0);
            //获取Excel的行数
            int totalRows = sheet.getPhysicalNumberOfRows();
            // 得到Excel的列数(前提是有行数)
            if (totalRows <= 2 || StringUtil.isEmpty(sheet.getRow(0)) || StringUtil.isEmpty(sheet.getRow(1))) {
                return ResponseResult.paramError("没有解析到文件中的数据");
            }
            //获取excel表头
            List<String> headers = Lists.newArrayList();
            for (Cell cell : sheet.getRow(0)) {
                headers.add(cell.getStringCellValue());
            }
            //定义错误信息列表
            List<String> errDescList = Lists.newArrayList();
            List<Map<String, String>> errDataList = Lists.newArrayList();
            for (int i = 2; i < totalRows; i++) {
                Map<String, String> dataMap = Maps.newHashMap();
                //读取每行单元格
                for (Cell cell : sheet.getRow(i)) {
                    dataMap.put(headers.get(cell.getColumnIndex()), CommonUtil.getCellValue(cell));
                }
                //校验数据map是否都为空
                if (dataMap.entrySet().stream().noneMatch(s -> StringUtil.isNotEmpty(s.getValue()))) {
                    totalRows = i - 2;
                    break;
                }

                DeviceEntity device = new DeviceEntity();
                device.setModelId(modelId);
                device.setTypeId(typeId);
                device.setSiteId(siteId);
                device.setParentId(parentId);
                device.setIsDelete(1);
//                device.setType(type);
                device.setCreateId(userId);
                device.setUpdateId(userId);
                Map<String, Object> readwriteMap = Maps.newHashMap();
                boolean isError = false;
                for (Map.Entry<String, String> data : dataMap.entrySet()) {
                    String key = data.getKey();
                    String value = data.getValue();
                    ModelFieldUpdateDto modelField = modelFieldMap.get(key);
                    //必填项校验
                    if (modelField.getRequired() && StringUtil.isEmpty(value)) {
                        errDescList.add("第" + i + "行：【" + modelField.getReaName() + "】数据是必填项, 不能为空");
                        isError = true;
                        break;
                    }
                    if (modelField.getType() == 1) { //静态字段
                        switch (key) {
                            case DeviceParamVo.DEVICE_NAME:
//                                if (deviceNames.contains(value)) {
//                                    errDescList.add("第" + i + "行：【" + modelField.getReaName() + "】数据已重复 不允许添加");
//                                    isError = true;
//                                }
                                device.setDeviceName(value);
                                break;
                            case DeviceParamVo.DEVICE_NUMBER:
                                if (StringUtil.isNotEmpty(value) && deviceNumbers.contains(value)) {
                                    errDescList.add("第" + i + "行：【" + modelField.getReaName() + "】数据已重复 不允许添加");
                                    isError = true;
                                }
                                device.setDeviceNumber(value);
                                break;
                            case DeviceParamVo.ACCESS_TYPE:
                                //如果值为空 则用默认值
                                if (StringUtil.isEmpty(value) && StringUtil.isNotEmpty(modelField.getDefaultValue())) {
                                    device.setAccessType(Integer.parseInt(modelField.getDefaultValue()));
                                } else {
                                    //扩展属性类型 1-数值 2-文字 3-选项 4-位置 5-开关 6-时间 7-文本
                                    Object reaValue = DeviceCommonUtil.getReaValue(modelField.getReaType(), modelField.getExtraValue(), modelField.getDefaultValue(), value);
                                    if (StringUtil.isEmpty(reaValue)) {
                                        errDescList.add("第" + i + "行：【" + modelField.getReaName() + "】数据格式有误 不允许添加");
                                        isError = true;
                                    } else {
                                        device.setAccessType(Integer.parseInt(String.valueOf(reaValue)));
                                    }
                                }
                                break;
                            case DeviceParamVo.DEVICE_DESC:
                                device.setDeviceDesc(value);
                                break;
                            case DeviceParamVo.OPERATE_STATUS:
                                //如果值为空 则用默认值
                                if (StringUtil.isEmpty(value) && StringUtil.isNotEmpty(modelField.getDefaultValue())) {
                                    device.setOperateStatus(Integer.parseInt(value));
                                } else {
                                    //扩展属性类型 1-数值 2-文字 3-选项 4-位置 5-开关 6-时间 7-文本
                                    Object reaValue = DeviceCommonUtil.getReaValue(modelField.getReaType(), modelField.getExtraValue(), modelField.getDefaultValue(), value);
                                    if (StringUtil.isEmpty(reaValue)) {
                                        errDescList.add("第" + i + "行：【" + modelField.getReaName() + "】数据格式有误 不允许添加");
                                        isError = true;
                                    } else {
                                        device.setOperateStatus(Integer.parseInt(String.valueOf(reaValue)));
                                    }
                                }
//                            case DeviceParamVo.FACTORY_CODE:
//                                device.setFactoryCode(value);
//                                break;
//                            case DeviceParamVo.RATED_CAP:
//                                //如果值为空 则用默认值
//                                if (StringUtil.isEmpty(value) && StringUtil.isNotEmpty(modelField.getDefaultValue())) {
//                                    device.setRatedCap(Double.parseDouble(modelField.getDefaultValue()));
//                                } else {
//                                    //扩展属性类型 1-数值 2-文字 3-选项 4-位置 5-开关 6-时间 7-文本
//                                    Object reaValue = DeviceCommonUtil.getReaValue(modelField.getReaType(), modelField.getExtraValue(), modelField.getDefaultValue(), value);
//                                    if (StringUtil.isEmpty(reaValue)) {
//                                        errDescList.add("第" + i + "行：【" + modelField.getReaName() + "】数据格式有误 不允许添加");
//                                        isError = true;
//                                    } else {
//                                        device.setRatedCap(Double.parseDouble(String.valueOf(reaValue)));
//                                    }
//                                }
//                                break;
//                            case DeviceParamVo.RATED_POWER:
//                                //如果值为空 则用默认值
//                                if (StringUtil.isEmpty(value) && StringUtil.isNotEmpty(modelField.getDefaultValue())) {
//                                    device.setRatedPower(Double.parseDouble(modelField.getDefaultValue()));
//                                } else {
//                                    //扩展属性类型 1-数值 2-文字 3-选项 4-位置 5-开关 6-时间 7-文本
//                                    Object reaValue = DeviceCommonUtil.getReaValue(modelField.getReaType(), modelField.getExtraValue(), modelField.getDefaultValue(), value);
//                                    if (StringUtil.isEmpty(reaValue)) {
//                                        errDescList.add("第" + i + "行：【" + modelField.getReaName() + "】数据格式有误 不允许添加");
//                                        isError = true;
//                                    } else {
//                                        device.setRatedPower(Double.parseDouble(String.valueOf(reaValue)));
//                                    }
//                                }
//                                break;
                        }
                    }
                    if (modelField.getType() == 2) { //动态字段
                        if (StringUtil.isNotEmpty(modelField.getReaType())) {
                            //如果值为空 则用默认值
                            if (StringUtil.isEmpty(value)) {
                                readwriteMap.put(key, modelField.getDefaultValue());
                            } else {
                                //扩展属性类型 1-数值 2-文字 3-选项 4-位置 5-开关 6-时间 7-文本
                                Object reaValue = DeviceCommonUtil.getReaValue(modelField.getReaType(), modelField.getExtraValue(), modelField.getDefaultValue(), value);
                                if (StringUtil.isEmpty(reaValue)) {
                                    errDescList.add("第" + i + "行：【" + modelField.getReaName() + "】数据格式有误 不允许添加");
                                    isError = true;
                                }
                                readwriteMap.put(key, reaValue);
                            }
                        } else {
                            errDescList.add("第" + i + "行：【" + modelField.getReaName() + "】数据的扩展属性类型格式有误 不允许添加");
                            isError = true;
                        }
                    }
                    if (isError) {
                        break;
                    }
                }
                device.setReadwriteObject(JSON.toJSONString(readwriteMap));
                if (!isError) {
                    deviceList.add(device);
                    //把添加的设备名称和设备序列号存入到校验里面，以免重复添加
//                    deviceNames.add(device.getDeviceName());
                    if (StringUtil.isNotEmpty(device.getDeviceNumber())) {
                        deviceNumbers.add(device.getDeviceNumber());
                    }
                } else {
                    errDataList.add(dataMap);
                }
            }
            //批量添加设备数据
            if (CollectionUtils.isNotEmpty(deviceList)) {
                List<ModelFunctionListDto> functionList = DeviceCommonUtil.getFunctionListByModeId(modelId);
                deviceDao.saveAll(deviceList).forEach(device -> {
                    //设备数据存入redis缓存
                    this.saveDeviceRedis(null, device, functionList);
                });
                //推送省平台充电站信息数据
                List<String> pileTypeIds = Arrays.asList("28", "29", "30");
                pileTypeIds.stream().filter(t -> Objects.equals(t, typeId)).findFirst().ifPresent(t -> {
                    configureService.notificationStationInfo(Collections.singleton(siteId));
                });
            }
            result.setNormalNum(totalRows - errDescList.size());
            result.setErrorNum(errDescList.size());
            result.setErrDataList(errDataList);
            result.setErrDescList(errDescList);
            return ResponseResult.ok(result);
        } catch (IOException e) {
            log.error("解析文件内容异常", e);
            return ResponseResult.error(ResponseResult.PARAM_PARSE_ERROR);
        }
    }

    @Override
    public ResponseResult<List<ModelNameDto>> getModelNameListByTypeId(String typeId) {
        //根据模型分类id查询模型分类数据
        return ResponseResult.ok(modelDao.findAllByTypeIdIn(Collections.singletonList(typeId))
                .stream()
                .filter(s -> Objects.equals(s.getModelStatus(), 1))
                .map(model -> {
                    ModelNameDto result = new ModelNameDto();
                    BeanUtils.copyProperties(model, result);
                    return result;
                }).collect(Collectors.toList()));
    }

    @Override
    public ResponseResult<List<ModelFieldUpdateDto>> getModelFieldUpdateListByModelId(String modelId) {
        List<ModelFieldUpdateDto> resultList = Lists.newArrayList();
        //组装静态字段数据 (设备名称,设备序列号,接入类型,设备描述,设备出厂编码)
        resultList.add(ModelFieldUpdateDto.builder().reaName("设备名称").fieldName(DeviceParamVo.DEVICE_NAME).reaType(2).required(true).type(1).build());
        resultList.add(ModelFieldUpdateDto.builder().reaName("设备序列号").fieldName(DeviceParamVo.DEVICE_NUMBER).reaType(2).required(false).type(1).build());
        //接入类型 1-直连设备 2-网关设备 3-网关子设备
        resultList.add(ModelFieldUpdateDto.builder().reaName("接入类型").fieldName(DeviceParamVo.ACCESS_TYPE).reaType(3).required(true).extraValue("{\"enumArray\":[{\"id\":1,\"name\":\"直连设备\"},{\"id\":2,\"name\":\"网关设备\"},{\"id\":3,\"name\":\"网关子设备\"}]}").type(1).build());
        resultList.add(ModelFieldUpdateDto.builder().reaName("设备描述").fieldName(DeviceParamVo.DEVICE_DESC).reaType(7).required(false).type(1).build());
        resultList.add(ModelFieldUpdateDto.builder().reaName("运营状态").fieldName(DeviceParamVo.OPERATE_STATUS).reaType(3).required(true).extraValue("{\"enumArray\":[{\"id\":0,\"name\":\"未知\"},{\"id\":1,\"name\":\"投运\"},{\"id\":2,\"name\":\"检修\"},{\"id\":3,\"name\":\"退役\"}]}").type(1).build());

        //获取模型读写字段数据
//        Set<String> reaIds = modelReaDao.findAllByModelIdAndReadWriteType(modelId, 2).stream().map(ModelReaEntity::getReaId).collect(Collectors.toSet());
        Set<String> reaIds = modelReaDao.findAllByModelId(modelId).stream().map(ModelReaEntity::getReaId).collect(Collectors.toSet());
        if (CollectionUtils.isNotEmpty(reaIds)) {
            resultList.addAll(reaDao.findAllById(reaIds).stream().map(rea -> {
                ModelFieldUpdateDto result = new ModelFieldUpdateDto();
                BeanUtils.copyProperties(rea, result);
                result.setType(2);
                return result;
            }).collect(Collectors.toSet()));
        }
        return ResponseResult.ok(resultList);
    }

    @Override
    public ResponseResult<PageDto<DeviceListDto>> queryDeviceList(DeviceQueryVo deviceQueryVo) {
        //返回的集合
        List<DeviceListDto> resultList = Lists.newArrayList();

        //根据查询条件查询设备数据
        List<DeviceEntity> deviceList = deviceDao.findAll((Specification<DeviceEntity>) (root, cq, cb) -> {
            List<Predicate> list = new ArrayList<>();
            list.add(cb.equal(root.get("isDelete"), 1));
            if (StringUtil.isNotEmpty(deviceQueryVo.getModelId())) { //模型id
                list.add(cb.equal(root.get("modelId"), deviceQueryVo.getModelId()));
            }
            //多个站点id
            list.add(cb.in(root.get("siteId")).value(JSON.parseArray(deviceQueryVo.getSiteIds())));
            if (StringUtil.isNotEmpty(deviceQueryVo.getAccessType())) { //接入类型
                list.add(cb.equal(root.get("accessType"), deviceQueryVo.getAccessType()));
            }
            if (StringUtil.isNotEmpty(deviceQueryVo.getTypeId())) { //设备类型
                list.add(cb.equal(root.get("typeId"), deviceQueryVo.getTypeId()));
            }
            if (StringUtil.isNotEmpty(deviceQueryVo.getKeyword())) { //关键字(设备ID+设备序列号+设备名称)
                list.add(cb.or(cb.like(root.get("id"), "%" + deviceQueryVo.getKeyword() + "%"),
                        cb.like(root.get("deviceNumber"), "%" + deviceQueryVo.getKeyword() + "%"),
                        cb.like(root.get("deviceName"), "%" + deviceQueryVo.getKeyword() + "%")));
            }
            if (StringUtil.isNotEmpty(deviceQueryVo.getDeviceNumber())) { //设备序列号
                list.add(cb.like(root.get("deviceNumber"), "%" + deviceQueryVo.getDeviceNumber() + "%"));
            }
            return cb.and(list.toArray(new Predicate[0]));
        }, Sort.by("createTime").descending());
        if (CollectionUtils.isNotEmpty(deviceList)) {
            //根据多个模型id获取模型名称和logo
            Map<String, ModelEntity> modelMap = modelDao.findAllById(deviceList.stream().map(DeviceEntity::getModelId).collect(Collectors.toSet())).stream().collect(Collectors.toMap(BaseEntity::getId, a -> a));
            //根据多个站点id获取站点名称
            Map<String, String> siteNameMap = siteInfoDao.findAllById(deviceList.stream().map(DeviceEntity::getSiteId).collect(Collectors.toSet())).stream().collect(Collectors.toMap(BaseEntity::getId, SiteInfoEntity::getSiteName));
            //根据多个设备类型id查询设备类型名称
            Map<String, String> deviceTypeMap = AssetTypeUtil.getTypeNameMap(deviceList.stream().map(DeviceEntity::getTypeId).filter(StringUtil::isNotEmpty).collect(Collectors.toSet()), assetTypeDao);
            //根据多个设备id查询设备事件未修复的数据
            Set<String> deviceIds = deviceList.stream().map(BaseEntity::getId).collect(Collectors.toSet());
            Map<String, Long> deviceEventMap = deviceEventDao.findAllByDeviceIdInAndEventStatus(deviceIds, 0).stream()
                    .collect(Collectors.groupingBy(DeviceEventEntity::getDeviceId, Collectors.counting()));
            //根据多个设备id查询设备告警未修复的数据
            AlarmRecordQueryVo alarmQueryVo = new AlarmRecordQueryVo();
            alarmQueryVo.setDeviceCodes(deviceList.stream().map(DeviceEntity::getDeviceNumber).filter(StringUtil::isNotEmpty).collect(Collectors.toSet()));
            alarmQueryVo.setAlarmStatus(0);
            Map<String, Long> deviceAlarmMap = protocolService.findAlarmRecordList(alarmQueryVo).getData().stream().collect(Collectors
                    .groupingBy(AlarmRecordDto::getDeviceCode, Collectors.counting()));
            resultList = deviceList.stream().map(device -> {
                DeviceListDto result = new DeviceListDto();
                BeanUtils.copyProperties(device, result);
                if (modelMap.containsKey(device.getModelId())) {
                    ModelEntity modelEntity = modelMap.get(device.getModelId());
                    result.setModelName(modelEntity.getModelName());
                    result.setLogoPath(modelEntity.getLogoPath());
                }
                result.setSiteName(siteNameMap.getOrDefault(device.getSiteId(), null));
                Integer txStatus = DeviceCommonUtil.getDeviceTxStatus(device.getId(), device.getDeviceNumber());
                if (StringUtil.isNotEmpty(txStatus)) {
                    result.setTxStatus(txStatus);
                }
                result.setTypeName(deviceTypeMap.getOrDefault(device.getTypeId(), null));
                if (deviceEventMap.containsKey(device.getId()) && deviceEventMap.get(device.getId()) > 0) {
                    result.setAlarmStatus(2);
                } else if (StringUtil.isNotEmpty(device.getDeviceNumber()) && deviceAlarmMap.containsKey(device.getDeviceNumber())
                        && deviceAlarmMap.get(device.getDeviceNumber()) > 0) {
                    result.setAlarmStatus(2);
                } else {
                    result.setAlarmStatus(1);
                }
                return result;
            }).collect(Collectors.toList());
            if (StringUtil.isNotEmpty(deviceQueryVo.getTxStatus())) {
                resultList = resultList.stream().filter(d -> StringUtil.isNotEmpty(d.getTxStatus()) && Objects.equals(d.getTxStatus(), deviceQueryVo.getTxStatus())).collect(Collectors.toList());
            }
            //告警状态查询
            if (StringUtil.isNotEmpty(deviceQueryVo.getAlarmStatus())) {
                resultList = resultList.stream().filter(d -> Objects.equals(d.getAlarmStatus(), deviceQueryVo.getAlarmStatus())).collect(Collectors.toList());
            }
        }
        return ResponseResult.ok(new PageDto<>(resultList, deviceQueryVo.getPage(), deviceQueryVo.getSize()));
    }

    @Override
    @Transactional(transactionManager = DBConstants.JPA_TX_ACCESS, rollbackFor = Exception.class)
    public ResponseResult<Void> deleteDeviceById(String id, Boolean deleteLogo) {
        Optional<DeviceEntity> optional = deviceDao.findById(id);
        if (optional.isPresent()) {
            DeviceEntity device = optional.get();
            if (deleteLogo) {
                //删除数据
                deviceDao.delete(device);
                //删除设备关联拓扑节点数据
                deviceTopologyDao.deleteAllByLeftDeviceIdOrRightDeviceId(id, id);
                //删除redis缓存数据,taos数据库
                if (StringUtil.isNotEmpty(device.getDeviceNumber())) {
                    RedisDeviceUtil.delDevice(device.getDeviceNumber());
                    dataService.deleteAllDataStoreTable(Collections.singleton(KeyUtil.DEVICE_KEY + device.getDeviceNumber()));
                }
                //删除设备告警数据
                deviceEventDao.deleteAll(deviceEventDao.findAllByDeviceIdIn(Collections.singleton(id)));
                //删除设备故障定义数据
                protocolService.deleteAllAlarmRecord(Collections.singleton(device.getDeviceNumber()));
            } else {
                //不删除关联数据
                device.setIsDelete(2);
                deviceDao.save(device);
            }
            return ResponseResult.ok();
        }
        return ResponseResult.paramError(ResponseResult.PARAM_ERROR);
    }

    @Override
    public ResponseResult<DeviceBasicInfoDto> findDeviceBasicInfoById(String deviceId) {
        //返回的对象
        DeviceBasicInfoDto result = new DeviceBasicInfoDto();
        //根据设备id查询设备数据
        Optional<DeviceEntity> optional = deviceDao.findById(deviceId);
        if (optional.isPresent()) {
            DeviceEntity device = optional.get();
            BeanUtils.copyProperties(device, result);
            //根据资产分类id获取资产分类名称
            result.setTypeName(AssetTypeUtil.getTypeNameMap(Collections.singleton(device.getTypeId()), assetTypeDao).get(device.getTypeId()));
            //根据模型id查询模型名称,模型描述
            modelDao.findById(device.getModelId()).ifPresent(model -> {
                result.setModelName(model.getModelName());
                result.setModelDesc(model.getModelDesc());
                result.setLogoPath(model.getLogoPath());
            });
            //根据创建人id和编辑人id查询用户名称
            //根据创建人id和修改人id查询创建人名称和修改人名称
            Map<String, UserDto> userMap = systemService.findUserInfoByIdsFeign(Arrays.asList(device.getCreateId(), device.getUpdateId())).getData();
            if (userMap.containsKey(device.getCreateId())) {
                result.setCreateName(userMap.get(device.getCreateId()).getFullName());
            }
            if (userMap.containsKey(device.getUpdateId())) {
                result.setUpdateName(userMap.get(device.getUpdateId()).getFullName());
            }

            //根据站点id查询站点名称
            siteInfoDao.findById(device.getSiteId()).ifPresent(site -> result.setSiteName(site.getSiteName()));

            //根据父节点id查询父节点名称
            if (StringUtil.isNotEmpty(device.getParentId())) {
                result.setParentId(device.getParentId());
                result.setParentName(DeviceCommonUtil.getParentNameByParentIds(Collections.singleton(device.getParentId())).get(device.getParentId()));
            }
            //获取设备通信状态
            result.setTxStatus(DeviceCommonUtil.getDeviceTxStatus(device.getId(), device.getDeviceNumber()));
        }
        return ResponseResult.ok(result);
    }

    @Override
    public ResponseResult<List<DeviceFunctionDto>> findDeviceFunctionListById(String deviceId) {
        //返回的功能属性数据列表
        List<DeviceFunctionDto> resultList = Lists.newArrayList();
        //根据设备id查询对应的模型标准功能点
        Optional<DeviceEntity> optional = deviceDao.findById(deviceId);
        if (optional.isPresent()) {
            DeviceEntity device = optional.get();
            //根据模型id查询所有模型标准功能id
            Map<String, ModelFunctionEntity> modelFunctionMap = modelFunctionDao.findAllByModelIdAndIsDelete(device.getModelId(), 1).stream()
                    .collect(Collectors.toMap(ModelFunctionEntity::getFunctionId, a -> a, (k1, k2) -> k1));

            //根据设备id查询设备功能点字段数据
            List<String> functionFieldIds = Lists.newArrayList();
            Optional<DeviceFunctionFieldEntity> functionFieldOptional = deviceFunctionFieldDao.findOne(Example
                    .of(DeviceFunctionFieldEntity.builder().deviceId(deviceId).build()));
            if (functionFieldOptional.isPresent() && StringUtil.isNotEmpty(functionFieldOptional.get().getFunctionFields())) {
                functionFieldIds = JSON.parseArray(functionFieldOptional.get().getFunctionFields(), String.class);
            }
            //获取设备redis里面的数据
            List<FunctionEntity> functionList = functionDao.findAllById(modelFunctionMap.keySet());
            Set<String> functionLogos = functionList.stream().map(FunctionEntity::getFunctionLogo).collect(Collectors.toSet());
            Map<String, RealDataModel> realDataModelMap = DeviceCommonUtil.getRawDeviceFunctions(Collections.singleton(deviceId), functionLogos).get(deviceId);
            //获取设备的通信状态
            Integer txStatus = DeviceCommonUtil.getDeviceTxStatus(deviceId, device.getDeviceNumber());
            List<String> finalFunctionFieldIds = functionFieldIds;
            resultList = functionList.stream().map(function -> {
                DeviceFunctionDto result = new DeviceFunctionDto();
                BeanUtils.copyProperties(function, result);
                result.setFunctionId(function.getId());
                if (modelFunctionMap.containsKey(function.getId())) {
                    ModelFunctionEntity modelFunction = modelFunctionMap.get(function.getId());
                    result.setDataType(modelFunction.getDataType());
                    result.setDataObject(modelFunction.getDataObject());
                }
                //获取量测实时值
                if (txStatus != null && MapUtils.isNotEmpty(realDataModelMap) && realDataModelMap.containsKey(function.getFunctionLogo())) {
                    RealDataModel realDataModel = realDataModelMap.get(function.getFunctionLogo());
                    if (realDataModel != null) {
                        result.setValue(realDataModel.getDataValue());
                        result.setDateTime(realDataModel.getDateTime());
                    }
                }
                //展示类型
                result.setShowType(1);
                if (finalFunctionFieldIds.contains(function.getId())) { //包含则不展示
                    result.setShowType(2);
                }
                return result;
            }).collect(Collectors.toList());
        }
        return ResponseResult.ok(resultList);
    }

    @Override
    public ResponseResult<DeviceFunctionValueDto> queryDeviceFunctionValueList(DeviceFunctionQueryVo functionQueryVo) {
        //返回的对象
        DeviceFunctionValueDto result = new DeviceFunctionValueDto();
        result.setDeviceId(functionQueryVo.getDeviceId());

        Optional<DeviceEntity> optional = deviceDao.findById(functionQueryVo.getDeviceId());
        if (optional.isPresent()) {
            //定义查询条件
            DeviceHistoryQueryVo deviceQueryVo = new DeviceHistoryQueryVo();
            deviceQueryVo.setDeviceIds(Collections.singleton(functionQueryVo.getDeviceId()));
            deviceQueryVo.setTimeInterval(functionQueryVo.getTimeInterval());
            deviceQueryVo.setStartTime(functionQueryVo.getStartTime());
            deviceQueryVo.setEndTime(functionQueryVo.getEndTime());
            functionDao.findById(functionQueryVo.getFunctionId()).ifPresent(function -> {
                deviceQueryVo.setFunctionLogos(Collections.singleton(function.getFunctionLogo()));
                result.setFunctionLogo(function.getFunctionLogo());
                result.setDataType(function.getDataType());
            });
            //根据查询条件查询设备下面的功能点数据
            Map<String, Map<String, List<DeviceHistoryDto>>> dataMap = dataService.findDeviceHistoryValueList(deviceQueryVo).getData();
            if (MapUtils.isNotEmpty(dataMap) && dataMap.containsKey(functionQueryVo.getDeviceId())) {
                Map<String, List<DeviceHistoryDto>> deviceHistoryMap = dataMap.get(functionQueryVo.getDeviceId());
                if (MapUtils.isNotEmpty(deviceHistoryMap) && deviceHistoryMap.containsKey(result.getFunctionLogo())) {
                    List<DeviceHistoryDto> deviceHistoryList = deviceHistoryMap.get(result.getFunctionLogo());
                    result.setDateList(deviceHistoryList.stream().map(DeviceHistoryDto::getDateTime).collect(Collectors.toList()));
                    result.setValueList(deviceHistoryList.stream().map(DeviceHistoryDto::getDataValue).collect(Collectors.toList()));
                }
            }
        }
        return ResponseResult.ok(result);
    }

    @Override
    public ResponseResult<Map<String, List<ModelFunctionListDto>>> findDeviceFunctionListByIds(Set<String> deviceIds) {
        Map<String, List<ModelFunctionListDto>> resultMap = Maps.newHashMap();
        List<DeviceEntity> deviceList = deviceDao.findAllByIdInAndIsDelete(deviceIds, 1);
        if (CollectionUtils.isNotEmpty(deviceList)) {
            //根据模型id查询所有模型标准功能数据
            Set<String> modelIds = deviceList.stream().map(DeviceEntity::getModelId).collect(Collectors.toSet());
            Map<String, List<ModelFunctionListDto>> functionMap = getFunctionListByModeIds(modelIds);

            deviceList.forEach(device -> resultMap.put(device.getId(), functionMap.getOrDefault(device.getModelId(), Lists.newArrayList())));
        }
        return ResponseResult.ok(resultMap);
    }

    @Override
    public ResponseResult<List<DeviceReaDto>> findDeviceReaListById(String deviceId) {
        //返回的集合
        List<DeviceReaDto> resultList = Lists.newArrayList();
        //根据设备id查询对应的模型扩展属性id
        Optional<DeviceEntity> optional = deviceDao.findById(deviceId);
        if (optional.isPresent()) {
            DeviceEntity device = optional.get();
            //读写设备值
            Map<String, Object> readwriteMap = Maps.newHashMap();
            if (StringUtil.isNotEmpty(device.getReadwriteObject())) {
                readwriteMap = JSON.parseObject(device.getReadwriteObject(), new TypeReference<Map<String, Object>>() {
                });
            }
            //根据模型id查询所有模型扩展属性id
//            Set<String> reaIds = modelReaDao.findAllByModelId(device.getModelId()).stream().map(ModelReaEntity::getReaId).collect(Collectors.toSet());
//            Map<String, Object> finalReadwriteMap = readwriteMap;
//            resultList = reaDao.findAllById(reaIds).stream().map(rea -> {
//                DeviceReaDto result = new DeviceReaDto();
//                BeanUtils.copyProperties(rea, result);
//                result.setReaId(rea.getId());
//                if (rea.getReadWriteType() == 1) { //只读 默认值
//                    result.setValue(rea.getDefaultValue());
//                } else { //设备实时值
//                    result.setValue(finalReadwriteMap.getOrDefault(rea.getFieldName(), rea.getDefaultValue()));
//                }
//                return result;
//            }).collect(Collectors.toList());
            //根据模型id查询所有模型扩展属性id
            Map<String, ModelReaEntity> modelReaMap = modelReaDao.findAllByModelId(device.getModelId()).stream().collect(Collectors.toMap(ModelReaEntity::getReaId,
                    modelReaEntity -> modelReaEntity, (k1, k2) -> k2));
            Map<String, Object> finalReadwriteMap = readwriteMap;
            resultList = reaDao.findAllById(modelReaMap.keySet()).stream().map(rea -> {
                DeviceReaDto result = new DeviceReaDto();
                BeanUtils.copyProperties(rea, result);
                result.setReaId(rea.getId());
                //设备实时值
                result.setValue(finalReadwriteMap.getOrDefault(rea.getFieldName(), modelReaMap.get(rea.getId()).getDefaultValue()));
                return result;
            }).collect(Collectors.toList());
        }
        return ResponseResult.ok(resultList);
    }

    @Override
    public ResponseResult<List<DeviceNodeListDto>> findDeviceNodeListById(String deviceId) {
        //返回的集合
        List<DeviceNodeListDto> resultList = Lists.newArrayList();
        //根据设备id查询设备模型id
        Optional<DeviceEntity> optional = deviceDao.findById(deviceId);
        if (optional.isPresent()) {
            List<ModelTopologyEntity> modelTopologyList = modelTopologyDao.findAllByModelId(optional.get().getModelId());
            Set<String> nodeIds = modelTopologyList.stream().map(ModelTopologyEntity::getId).collect(Collectors.toSet());

            //根据设备id和多个模型节点id查询设备节点关联数据
            List<DeviceTopologyEntity> leftNodeList = deviceTopologyDao.findAllByLeftDeviceIdAndLeftNodeIdIn(deviceId, nodeIds);
            Map<String, List<DeviceTopologyEntity>> leftNodeMap = leftNodeList.stream().collect(Collectors.groupingBy(d -> d.getLeftDeviceId() + FileUtil.COMMA + d.getLeftNodeId()));
            List<DeviceTopologyEntity> rightNodeList = deviceTopologyDao.findAllByRightDeviceIdAndRightNodeIdIn(deviceId, nodeIds);
            Map<String, List<DeviceTopologyEntity>> rightNodeMap = rightNodeList.stream().collect(Collectors.groupingBy(d -> d.getRightDeviceId() + FileUtil.COMMA + d.getRightNodeId()));

            //根据多个关联设备id查询关联设备名称
            Set<String> deviceIds = Sets.newHashSet();
            deviceIds.addAll(leftNodeList.stream().map(DeviceTopologyEntity::getRightDeviceId).collect(Collectors.toSet()));
            deviceIds.addAll(rightNodeList.stream().map(DeviceTopologyEntity::getLeftDeviceId).collect(Collectors.toSet()));
            Map<String, String> deviceNameMap = deviceDao.findAllById(deviceIds).stream().collect(Collectors.toMap(BaseEntity::getId, DeviceEntity::getDeviceName, (k1, k2) -> k1));

            //根据多个关联节点id查询节点名称
            Set<String> modelNodeIds = Sets.newHashSet();
            modelNodeIds.addAll(leftNodeList.stream().map(DeviceTopologyEntity::getRightNodeId).collect(Collectors.toSet()));
            modelNodeIds.addAll(rightNodeList.stream().map(DeviceTopologyEntity::getLeftNodeId).collect(Collectors.toSet()));
            Map<String, String> nodeNameMap = modelTopologyDao.findAllById(modelNodeIds).stream().collect(Collectors.toMap(ModelTopologyEntity::getId, ModelTopologyEntity::getNodeName, (k1, k2) -> k1));

            resultList = modelTopologyList.stream().map(modelTopology -> {
                DeviceNodeListDto result = new DeviceNodeListDto();
                result.setNodeId(modelTopology.getId());
                result.setNodeName(modelTopology.getNodeName());
                String keyId = deviceId + FileUtil.COMMA + modelTopology.getId();
                if (leftNodeMap.containsKey(keyId)) {
                    result.getOtherNodeList().addAll(leftNodeMap.get(keyId).stream().map(deviceNode -> {
                        DeviceNodeListDto.NodeData nodeData = new DeviceNodeListDto.NodeData();
                        nodeData.setId(deviceNode.getId());
                        nodeData.setDeviceId(deviceNode.getRightDeviceId());
                        nodeData.setDeviceName(deviceNameMap.getOrDefault(deviceNode.getRightDeviceId(), null));
                        nodeData.setNodeId(deviceNode.getRightNodeId());
                        nodeData.setNodeName(nodeNameMap.getOrDefault(deviceNode.getRightNodeId(), null));
                        return nodeData;
                    }).collect(Collectors.toSet()));
                }
                if (rightNodeMap.containsKey(keyId)) {
                    result.getOtherNodeList().addAll(rightNodeMap.get(keyId).stream().map(deviceNode -> {
                        DeviceNodeListDto.NodeData nodeData = new DeviceNodeListDto.NodeData();
                        nodeData.setId(deviceNode.getId());
                        nodeData.setDeviceId(deviceNode.getLeftDeviceId());
                        nodeData.setDeviceName(deviceNameMap.getOrDefault(deviceNode.getLeftDeviceId(), null));
                        nodeData.setNodeId(deviceNode.getLeftNodeId());
                        nodeData.setNodeName(nodeNameMap.getOrDefault(deviceNode.getLeftNodeId(), null));
                        return nodeData;
                    }).collect(Collectors.toSet()));
                }
                return result;
            }).collect(Collectors.toList());
        }
        return ResponseResult.ok(resultList);
    }

    @Override
    public ResponseResult<DeviceNodeUpdateDto> findDeviceNodeUpdateList(String deviceId, String nodeId) {
        //返回的对象
        DeviceNodeUpdateDto result = new DeviceNodeUpdateDto();
        //根据设备id查询设备模型id
        Optional<DeviceEntity> optional = deviceDao.findById(deviceId);
        if (optional.isPresent()) {
            //根据设备id查询对应站点下面所有的设备数据
            List<DeviceEntity> deviceList = deviceDao.findAllBySiteIdAndIsDelete(optional.get().getSiteId(), 1);

            //根据多个模型id查询节点数据
            Set<String> modelIds = deviceList.stream().map(DeviceEntity::getModelId).collect(Collectors.toSet());
            Map<String, List<ModelTopologyEntity>> modelNodeMap = modelTopologyDao.findAllByModelIdIn(modelIds).stream().collect(Collectors.groupingBy(ModelTopologyEntity::getModelId));

            //根据设备id和节点id查询已关联的数据
            Map<String, String> relationNodeMap = Maps.newConcurrentMap();
            relationNodeMap.putAll(deviceTopologyDao.findAllByLeftDeviceIdAndLeftNodeIdIn(deviceId, Collections.singleton(nodeId)).stream().collect(Collectors.toMap(d -> d.getRightDeviceId() + FileUtil.COMMA + d.getRightNodeId(), DeviceTopologyEntity::getRightNodeId, (k1, k2) -> k1)));
            relationNodeMap.putAll(deviceTopologyDao.findAllByRightDeviceIdAndRightNodeIdIn(deviceId, Collections.singleton(nodeId)).stream().collect(Collectors.toMap(d -> d.getLeftDeviceId() + FileUtil.COMMA + d.getLeftNodeId(), DeviceTopologyEntity::getLeftNodeId, (k1, k2) -> k1)));

            //对数据组装
            deviceList.forEach(device -> {
                if (modelNodeMap.containsKey(device.getModelId())) {
                    for (ModelTopologyEntity modelTopology : modelNodeMap.get(device.getModelId())) {
                        String keyId = device.getId() + FileUtil.COMMA + modelTopology.getId();
                        DeviceNodeUpdateDto.NodeData nodeData = new DeviceNodeUpdateDto.NodeData();
                        nodeData.setDeviceId(device.getId());
                        nodeData.setDeviceName(device.getDeviceName());
                        nodeData.setNodeId(modelTopology.getId());
                        nodeData.setNodeName(modelTopology.getNodeName());
                        nodeData.setType(1);
                        if (Objects.equals(device.getId(), deviceId) && Objects.equals(modelTopology.getId(), nodeId)) {
                            nodeData.setType(2);
                        }
                        if (relationNodeMap.containsKey(keyId)) {
                            result.getCheckList().add(nodeData);
                        } else {
                            result.getUncheckList().add(nodeData);
                        }
                    }
                }
            });
        }
        return ResponseResult.ok(result);
    }

    @Override
    @Transactional(transactionManager = DBConstants.JPA_TX_ACCESS, rollbackFor = Exception.class)
    public ResponseResult<Void> batchBindDeviceTopology(String deviceId, String nodeId, List<String> bindData) {
        //先删除之前的数据
        //根据设备id和节点id查询所有绑定的节点数据
        List<DeviceTopologyEntity> deleteList = Lists.newArrayList();
        deleteList.addAll(deviceTopologyDao.findAllByLeftDeviceIdAndLeftNodeIdIn(deviceId, Collections.singleton(nodeId)));
        deleteList.addAll(deviceTopologyDao.findAllByRightDeviceIdAndRightNodeIdIn(deviceId, Collections.singleton(nodeId)));
        deviceTopologyDao.deleteInBatch(deleteList);

        //绑定新的数据
        deviceTopologyDao.saveAll(bindData.stream().map(data -> {
            List<String> dataList = Lists.newArrayList(data.split(FileUtil.COMMA));
            DeviceTopologyEntity deviceTopology = new DeviceTopologyEntity();
            deviceTopology.setLeftDeviceId(deviceId);
            deviceTopology.setLeftNodeId(nodeId);
            deviceTopology.setRightDeviceId(dataList.get(0));
            deviceTopology.setRightNodeId(dataList.get(1));
            return deviceTopology;
        }).collect(Collectors.toSet()));
        return ResponseResult.ok();
    }

    @Override
    @Transactional(transactionManager = DBConstants.JPA_TX_ACCESS, rollbackFor = Exception.class)
    public ResponseResult<Void> deleteDeviceTopologyById(String id) {
        deviceTopologyDao.deleteById(id);
        return ResponseResult.ok();
    }

    @Override
    public ResponseResult<PageDto<DeviceEventListDto>> findDeviceEventList(DeviceEventQueryVo eventQueryVo) {
        //返回的集合
        List<DeviceEventListDto> resultList = Lists.newArrayList();

        //根据查询条件查询设备事件数据
        List<DeviceEventEntity> deviceEventList = deviceEventDao.findAll((Specification<DeviceEventEntity>) (root, cq, cb) -> {
            List<Predicate> list = new ArrayList<>();
            list.add(cb.equal(root.get("deviceId"), eventQueryVo.getDeviceId()));//设备id
            if (StringUtil.isNotEmpty(eventQueryVo.getStartDate()) && StringUtil.isNotEmpty(eventQueryVo.getEndDate())) { //创建事件查询
                LocalDateTime startTime = DateUtil.strToLocalDateTime(DateUtil.getDayStart(eventQueryVo.getStartDate()));
                LocalDateTime endTime = DateUtil.strToLocalDateTime(DateUtil.getDayEnd(eventQueryVo.getEndDate()));
                list.add(cb.between(root.get("createTime"), startTime, endTime));
            }
            return cb.and(list.toArray(new Predicate[0]));
        }, Sort.by("createTime").descending());

        if (CollectionUtils.isNotEmpty(deviceEventList)) {
            //根据多个模型事件id查询模型事件数据
            Set<String> eventIds = deviceEventList.stream().map(DeviceEventEntity::getEventId).collect(Collectors.toSet());
            Map<String, ModelEventEntity> modelEventMap = modelEventDao.findAllById(eventIds).stream().collect(Collectors.toMap(ModelEventEntity::getId, a -> a, (k1, k2) -> k1));

            //对数据组装
            resultList.addAll(deviceEventList.stream().map(deviceEvent -> {
                DeviceEventListDto result = new DeviceEventListDto();
                result.setId(deviceEvent.getId());
                result.setEventStatus(deviceEvent.getEventStatus());
                result.setFunctionNames(deviceEvent.getEventSource());
                if (modelEventMap.containsKey(deviceEvent.getEventId())) {
                    ModelEventEntity modelEvent = modelEventMap.get(deviceEvent.getEventId());
                    result.setEventName(modelEvent.getEventName());
                    result.setEventLevel(modelEvent.getEventLevel());
                    result.setEventDesc(modelEvent.getEventDesc());
                    result.setIsAllow(modelEvent.getIsAllow());
                }
                if (StringUtil.isNotEmpty(deviceEvent.getCreateTime())) {
                    result.setCreateTime(DateUtil.localDateTimeToStr(deviceEvent.getCreateTime()));
                }
                if (StringUtil.isNotEmpty(deviceEvent.getUpdateTime())) {
                    result.setUpdateTime(DateUtil.localDateTimeToStr(deviceEvent.getUpdateTime()));
                }
                result.setType(1);
                return result;
            }).collect(Collectors.toList()));

            //查询条件 事件级别
            if (StringUtil.isNotEmpty(eventQueryVo.getEventLevel())) {
                resultList = resultList.stream().filter(s -> Objects.equals(s.getEventLevel(), eventQueryVo.getEventLevel())).collect(Collectors.toList());
            }
        }

        //根据查询条件查询设备告警数据
        Optional<DeviceEntity> optional = deviceDao.findById(eventQueryVo.getDeviceId());
        if (optional.isPresent() && StringUtil.isNotEmpty(optional.get().getDeviceNumber())) {
            AlarmRecordQueryVo alarmQueryVo = new AlarmRecordQueryVo();
            alarmQueryVo.setDeviceCodes(Collections.singleton(optional.get().getDeviceNumber()));
            alarmQueryVo.setEventLevel(eventQueryVo.getEventLevel());
            alarmQueryVo.setStartDate(eventQueryVo.getStartDate());
            alarmQueryVo.setEndDate(eventQueryVo.getEndDate());
            resultList.addAll(protocolService.findAlarmRecordList(alarmQueryVo).getData()
                    .stream().filter(alarmRecord -> StringUtil.isNotEmpty(alarmRecord.getFaultCode()))
                    .map(alarmRecord -> {
                        DeviceEventListDto result = new DeviceEventListDto();
                        BeanUtils.copyProperties(alarmRecord, result);
                        result.setFunctionNames("故障码: " + alarmRecord.getFaultCode());
                        result.setEventStatus(alarmRecord.getAlarmStatus());
                        result.setIsAllow(2);
                        result.setType(2);
                        return result;
                    }).collect(Collectors.toList()));
        }
        return ResponseResult.ok(new PageDto<>(resultList.stream().filter(s -> StringUtil.isNotEmpty(s.getCreateTime()))
                .sorted(Comparator.comparing(DeviceEventListDto::getCreateTime).reversed()).collect(Collectors.toList()),
                eventQueryVo.getPage(), eventQueryVo.getSize()));

    }

    @Override
    @Transactional(transactionManager = DBConstants.JPA_TX_ACCESS, rollbackFor = Exception.class)
    public ResponseResult<Void> updateDeviceEventStatusById(String id) {
        Optional<DeviceEventEntity> optional = deviceEventDao.findById(id);
        if (optional.isPresent()) {
            DeviceEventEntity deviceEvent = optional.get();
            deviceEvent.setEventStatus(1);
            deviceEventDao.save(deviceEvent);
        }
        return ResponseResult.ok();
    }

    @Override
    public ResponseResult<List<SiteDeviceTreeDto>> getSiteDeviceTreeList(String userId, Integer type) {
        //返回的集合
        List<SiteDeviceTreeDto> resultList = Lists.newArrayList();

        //根据用户id查询所属租户id
        Map<String, UserDto> userMap = systemService.findUserInfoByIdsFeign(Collections.singletonList(userId)).getData();
        if (userMap == null || userMap.isEmpty() || !userMap.containsKey(userId)) {
            return ResponseResult.ok(resultList);
        }
        List<SiteInfoEntity> siteList;
        if (userMap.get(userId).getUserRole() != 0) {
            //根据用户id查询租户资产授权列表
            List<OrganEmpowerListDto> organEmpowerList = systemService.findAllOrganEmpowerByUserId(userId).getData();
            if (CollectionUtils.isEmpty(organEmpowerList)) {
                return ResponseResult.ok(resultList);
            }
            //根据多个站点id查询站点数据
            Set<String> siteIds = organEmpowerList.stream().map(OrganEmpowerListDto::getSiteId).distinct().filter(StringUtil::isNotEmpty).collect(Collectors.toSet());
            siteList = siteInfoDao.findAllById(siteIds).stream().filter(s -> Objects.equals(s.getIsDelete(), 1)).collect(Collectors.toList());
        } else {
            siteList = siteInfoDao.findAll(Example.of(SiteInfoEntity.builder().isDelete(1).build()));
        }
        if (CollectionUtils.isNotEmpty(siteList)) {
            //根据多个站点模型id查询资产分类id
            Set<String> modelIds = siteList.stream().map(SiteInfoEntity::getSiteModelId).filter(StringUtil::isNotEmpty).collect(Collectors.toSet());
            Map<String, String> siteModelMap = modelDao.findAllById(modelIds).stream().collect(Collectors.toMap(ModelEntity::getId, ModelEntity::getTypeId, (k1, k2) -> k1));
            //根据多个站点id查询站点下的直连设备和网关设备数据
            Set<String> siteIds = siteList.stream().map(BaseEntity::getId).collect(Collectors.toSet());
            Map<String, List<DeviceEntity>> deviceMap = deviceDao.findAllBySiteIdInAndAccessTypeNotAndIsDelete(siteIds, 3, 1).stream().collect(Collectors.groupingBy(DeviceEntity::getSiteId));
            //根据多个网关设备id查询网关子设备数据
            Set<String> gatewayIds = deviceMap.entrySet().stream().flatMap(device -> device.getValue().stream().filter(s -> StringUtil.isNotEmpty(s.getAccessType()) && Objects.equals(s.getAccessType(), 2)).map(DeviceEntity::getId)).collect(Collectors.toSet());
            List<GatewaySubDeviceEntity> gatewaySubDeviceList = gatewaySubDeviceDao.findAllByGatewayIdIn(gatewayIds);
            Map<String, Set<String>> subDeviceIdMap = gatewaySubDeviceList.stream().collect(Collectors.groupingBy(GatewaySubDeviceEntity::getGatewayId, Collectors.mapping(GatewaySubDeviceEntity::getSubDeviceId, Collectors.toSet())));
            Set<String> subDeviceIds = gatewaySubDeviceList.stream().map(GatewaySubDeviceEntity::getSubDeviceId).filter(StringUtil::isNotEmpty).collect(Collectors.toSet());
            Map<String, DeviceEntity> subDeviceMap = deviceDao.findAllByIdInAndIsDelete(subDeviceIds, 1).stream().collect(Collectors.toMap(DeviceEntity::getId, a -> a, (k1, k2) -> k1));

            List<SiteDeviceTreeDto> finalResultList = resultList;
            //对数据进行组装
            siteList.forEach(site -> {
                //站点数据
                finalResultList.add(SiteDeviceTreeDto.builder().id(site.getId()).code(site.getSiteCode()).name(site.getSiteName()).type(1).typeId(StringUtil.isNotEmpty(site.getSiteModelId()) ? siteModelMap.get(site.getSiteModelId()) : null).build());

                if (deviceMap.containsKey(site.getId())) {
                    deviceMap.get(site.getId()).forEach(device -> {
                        //直连设备和网关设备数据
                        finalResultList.add(SiteDeviceTreeDto.builder().id(device.getId()).code(device.getDeviceNumber()).name(device.getDeviceName()).type(2).parentId(site.getId()).typeDetail(device.getAccessType()).typeId(device.getTypeId()).build());

                        //网关子设备数据
                        if (subDeviceIdMap.containsKey(device.getId())) {
                            subDeviceIdMap.get(device.getId()).forEach(subDeviceId -> {
                                if (subDeviceMap.containsKey(subDeviceId)) {
                                    DeviceEntity subDevice = subDeviceMap.get(subDeviceId);
                                    finalResultList.add(SiteDeviceTreeDto.builder().id(subDevice.getId()).code(subDevice.getDeviceNumber()).name(subDevice.getDeviceName()).type(3).parentId(device.getId()).typeDetail(subDevice.getAccessType()).typeId(subDevice.getTypeId()).build());
                                }
                            });
                        }
                    });
                }
            });
        }
        //类型 0-所有数据 1-站点数据 2-设备数据 3-子设备数据 4-站点设备数据 5-站点设备子设备数据
        switch (type) {
            case 1:
                resultList = resultList.stream().filter(s -> Objects.equals(s.getType(), 1)).collect(Collectors.toList());
                break;
            case 2:
                resultList = resultList.stream().filter(s -> !Objects.equals(s.getType(), 2)).collect(Collectors.toList());
                break;
            case 3:
                resultList = resultList.stream().filter(s -> Objects.equals(s.getType(), 3)).collect(Collectors.toList());
                break;
            case 4:
                resultList = resultList.stream().filter(s -> !Objects.equals(s.getType(), 3)).collect(Collectors.toList());
                break;
            case 5:
                resultList = resultList.stream().filter(s -> StringUtil.isEmpty(s.getTypeDetail()) || Objects.equals(s.getTypeDetail(), 2) || Objects.equals(s.getTypeDetail(), 3)).collect(Collectors.toList());
                break;
        }
        return ResponseResult.ok(resultList);
    }

    @Override
    public ResponseResult<List<SiteDeviceTreeDto>> getSiteAssetsTreeList(String userId) {
        //返回的集合
        List<SiteDeviceTreeDto> resultList = Lists.newArrayList();

        //根据用户id查询所属租户id
        Map<String, UserDto> userMap = systemService.findUserInfoByIdsFeign(Collections.singletonList(userId)).getData();
        if (userMap == null || userMap.isEmpty() || !userMap.containsKey(userId)) {
            return ResponseResult.ok(resultList);
        }
        List<SiteInfoEntity> siteList;
        if (userMap.get(userId).getUserRole() != 0) {
            //根据用户id查询租户资产授权列表
            List<OrganEmpowerListDto> organEmpowerList = systemService.findAllOrganEmpowerByUserId(userId).getData();
            if (CollectionUtils.isEmpty(organEmpowerList)) {
                return ResponseResult.ok(resultList);
            }
            //根据多个站点id查询站点数据
            Set<String> siteIds = organEmpowerList.stream().map(OrganEmpowerListDto::getSiteId).distinct().filter(StringUtil::isNotEmpty).collect(Collectors.toSet());
            siteList = siteInfoDao.findAllById(siteIds).stream().filter(s -> Objects.equals(s.getIsDelete(), 1)).collect(Collectors.toList());
        } else {
            siteList = siteInfoDao.findAll(Example.of(SiteInfoEntity.builder().isDelete(1).build()));
        }
        if (CollectionUtils.isNotEmpty(siteList)) {
            Set<String> siteIds = siteList.stream().map(BaseEntity::getId).collect(Collectors.toSet());
            //根据多个站点id查询站点子系统数据
            List<ScenarioTypeEntity> scenarioTypeList = scenarioTypeDao.findAllBySiteIdIn(siteIds);
            Map<String, List<ScenarioTypeEntity>> scenarioTypeMap = scenarioTypeList.stream().collect(Collectors.groupingBy(ScenarioTypeEntity::getSiteId));
            //根据多个站点和子系统模型id查询资产分类id
            Set<String> modelIds = siteList.stream().map(SiteInfoEntity::getSiteModelId).filter(StringUtil::isNotEmpty).collect(Collectors.toSet());
            modelIds.addAll(scenarioTypeList.stream().map(ScenarioTypeEntity::getModelId).filter(StringUtil::isNotEmpty).collect(Collectors.toSet()));
            Map<String, String> modelMap = modelDao.findAllById(modelIds).stream().collect(Collectors.toMap(ModelEntity::getId, ModelEntity::getTypeId, (k1, k2) -> k1));
            //根据多个站点id查询站点下的直连设备和网关设备数据
            Map<String, List<DeviceEntity>> deviceMap = deviceDao.findAllBySiteIdInAndIsDelete(siteIds, 1).stream().collect(Collectors.groupingBy(DeviceEntity::getSiteId));

            //对数据进行组装
            siteList.forEach(site -> {
                //站点数据
                resultList.add(SiteDeviceTreeDto.builder().id(site.getId()).name(site.getSiteName()).type(1).typeId(StringUtil.isNotEmpty(site.getSiteModelId())
                        ? modelMap.get(site.getSiteModelId()) : null).build());
                //站点子系统数据
                if (scenarioTypeMap.containsKey(site.getId())) {
                    scenarioTypeMap.get(site.getId()).forEach(scenarioType ->
                            resultList.add(SiteDeviceTreeDto.builder().id(scenarioType.getId()).name(scenarioType.getSystemName()).type(4).parentId(site.getId())
                                    .typeId(StringUtil.isNotEmpty(scenarioType.getModelId()) ? modelMap.get(scenarioType.getModelId()) : null).build()));
                }
                if (deviceMap.containsKey(site.getId())) {
                    deviceMap.get(site.getId()).forEach(device -> {
                        Integer type = null;
                        //接入类型 1-直连设备 2-网关设备 3-网关子设备
                        if (StringUtil.isNotEmpty(device.getAccessType())) {
                            switch (device.getAccessType()) {
                                case 1:
                                case 2:
                                    type = 2;
                                    break;
                                case 3:
                                    type = 3;
                                    break;
                            }
                        }
                        resultList.add(SiteDeviceTreeDto.builder().id(device.getId()).name(device.getDeviceName()).type(type)
                                .parentId(StringUtil.isEmpty(device.getParentId()) ? site.getId() : device.getParentId())
                                .typeDetail(device.getAccessType()).typeId(device.getTypeId()).build());
                    });
                }
            });
        }
        return ResponseResult.ok(resultList);
    }

    @Override
    public ResponseResult<List<GatewaySubDeviceDto>> findGatewaySubDeviceList(String gatewayId) {
        //返回的集合
        List<GatewaySubDeviceDto> resultList = Lists.newArrayList();
        if (StringUtil.isNotEmpty(gatewayId)) {
            List<GatewaySubDeviceEntity> gatewaySubDeviceList = gatewaySubDeviceDao.findAllByGatewayId(gatewayId);
            if (CollectionUtils.isNotEmpty(gatewaySubDeviceList)) {
                //根据多个子设备id查询子设备数据
                Set<String> subDeviceIds = gatewaySubDeviceList.stream().map(GatewaySubDeviceEntity::getSubDeviceId).collect(Collectors.toSet());
                Map<String, DeviceEntity> deviceMap = deviceDao.findAllByIdInAndIsDelete(subDeviceIds, 1).stream()
                        .collect(Collectors.toMap(BaseEntity::getId, s -> s));
                //对数组进行组装
                resultList = gatewaySubDeviceList.stream().map(gatewaySubDevice -> {
                    GatewaySubDeviceDto result = new GatewaySubDeviceDto();
                    result.setId(gatewaySubDevice.getId());
                    result.setDeviceId(gatewaySubDevice.getSubDeviceId());
                    if (deviceMap.containsKey(gatewaySubDevice.getSubDeviceId())) {
                        DeviceEntity device = deviceMap.get(gatewaySubDevice.getSubDeviceId());
                        result.setDeviceNumber(device.getDeviceNumber());
                        result.setDeviceName(device.getDeviceName());
                        result.setTxStatus(DeviceCommonUtil.getDeviceTxStatus(gatewaySubDevice.getId(), device.getDeviceNumber()));
                    }
                    return result;
                }).collect(Collectors.toList());
            }
        }
        return ResponseResult.ok(resultList);
    }

    //设备数据存redis
    private void saveDeviceRedis(String oldDeviceNumber, DeviceEntity device, List<ModelFunctionListDto> functionList) {
        String deviceNumber = device.getDeviceNumber();

        if (StringUtil.isNotEmpty(oldDeviceNumber)) {
            boolean isUpdate = false;
            if (StringUtil.isNotEmpty(deviceNumber)) {
                isUpdate = Objects.equals(oldDeviceNumber, deviceNumber);
            }
            //旧新设备号不一致时 删除旧的设备号
            if (!isUpdate) {
                RedisDeviceUtil.delDevice(oldDeviceNumber);
            }
        }
        //设备数据存入redis缓存
        if (StringUtil.isNotEmpty(deviceNumber)) {

            DeviceModel deviceModel = RedisDeviceUtil.getDevice(deviceNumber);
            if (deviceModel == null) {
                deviceModel = new DeviceModel();
            }
            deviceModel.setDeviceId(device.getId());
            deviceModel.setDeviceNumber(device.getDeviceNumber());
            deviceModel.setModelId(device.getModelId());
            deviceModel.setAccessType(device.getAccessType());
            if (StringUtil.isEmpty(deviceModel.getTxStatus())) {
                deviceModel.setTxStatus(0);
            }
            Map<String, FunctionModel> functionMap = Maps.newConcurrentMap();
            for (ModelFunctionListDto function : functionList) {
                FunctionModel functionModel = new FunctionModel();
                functionModel.setFunctionLogo(function.getFunctionLogo());
                functionModel.setDataType(function.getDataType());
                functionModel.setFieldCode(function.getFieldCode());
                functionModel.setAccuracy(function.getAccuracy());
//                realDataModel.setValueRange(function.getValueRange());
                functionModel.setDataObject(function.getDataObject());
                if (deviceModel.getFunctionMap().containsKey(function.getFunctionLogo())) {
                    FunctionModel realData = deviceModel.getFunctionMap().get(function.getFunctionLogo());
                    functionModel.setDataValue(realData.getDataValue());
                    functionModel.setDateTime(realData.getDateTime());
                }
                functionMap.put(function.getFunctionLogo(), functionModel);
            }
            deviceModel.setFunctionMap(functionMap);
            RedisDeviceUtil.setDevice(deviceNumber, deviceModel);
        }
    }


    /**
     * 根据多个设备编码查询设备详情数据
     *
     * @param deviceCodeList 多个设备编码
     * @return
     */
    @Override
    public ResponseResult<Map<String, DeviceBasicInfoDto>> findDeviceBasicInfoByCodes(List<String> deviceCodeList) {
        Map<String, DeviceBasicInfoDto> resultMap = Maps.newHashMap();

        //根据多个设备编码查询正常状态设备数据
        List<DeviceEntity> deviceEntityList = deviceDao.findAllByDeviceNumberInAndIsDelete(deviceCodeList, 1);
        if (CollectionUtils.isNotEmpty(deviceEntityList)) {
            List<String> siteIdList = deviceEntityList.stream().map(DeviceEntity::getSiteId).filter(StringUtil::isNotEmpty).distinct().collect(Collectors.toList());
            //根据多个站点id查询站点基本信息，租户名称
            List<SiteInfoEntity> siteEntityList = siteInfoDao.findAllById(siteIdList);
            Map<String, SiteInfoEntity> siteEntityMap = Maps.newHashMap();
            Map<String, String> tenantMap = Maps.newHashMap();
            if (CollectionUtils.isNotEmpty(siteEntityList)) {
                siteEntityMap = siteEntityList.stream().collect(Collectors.toMap(SiteInfoEntity::getId, SiteInfoEntity -> SiteInfoEntity, (k1, k2) -> k1));
                //获取运营商id列表
                List<String> operateIdList = siteEntityList.stream().map(SiteInfoEntity::getOperatorId).filter(StringUtil::isNotEmpty).distinct().collect(Collectors.toList());
                //获取产权方id列表
                List<String> propertyIdList = siteEntityList.stream().map(SiteInfoEntity::getPropertyId).filter(StringUtil::isNotEmpty).distinct().collect(Collectors.toList());
                if (CollectionUtils.isNotEmpty(operateIdList) || CollectionUtils.isNotEmpty(propertyIdList)) {
                    operateIdList.addAll(propertyIdList);
                    //根据多个运营商id查询运营商基本信息
                    ResponseResult<List<TenantDetailsDto>> tenantDetailsByIds = systemService.findTenantDetailsByIds(operateIdList.stream().distinct().collect(Collectors.toList()));
                    if (tenantDetailsByIds.isSuccess() && CollectionUtils.isNotEmpty(tenantDetailsByIds.getData())) {
                        List<TenantDetailsDto> tenantDetailsDtoList = tenantDetailsByIds.getData();
                        tenantMap = tenantDetailsDtoList.stream().collect(Collectors.toMap(TenantDetailsDto::getId, TenantDetailsDto::getTenantName, (k1, k2) -> k1));
                    }
                }
            }
            //根据多个模型id查询模型相关数据
            List<String> modelIds = deviceEntityList.stream().map(DeviceEntity::getModelId).distinct().collect(Collectors.toList());
            Map<String, ModelEntity> modelMap = modelDao.findAllById(modelIds).stream().collect(Collectors.toMap(BaseEntity::getId, a -> a, (k1, k2) -> k1));
            Map<String, List<ModelReaEntity>> modelReaMap = modelReaDao.findAllByModelIdIn(modelIds).stream().filter(r -> StringUtil.isNotEmpty(r.getReaId()))
                    .collect(Collectors.groupingBy(ModelReaEntity::getModelId));
            Map<String, ReaEntity> reaEntityMap = reaDao.findAllById(modelReaMap.values().stream().flatMap(Collection::stream).map(ModelReaEntity::getReaId).filter(StringUtil::isNotEmpty)
                    .collect(Collectors.toSet())).stream().collect(Collectors.toMap(ReaEntity::getId, a -> a, (k1, k2) -> k1));

            //根据多个用户id查询用户名称
            List<String> userIds = Lists.newArrayList();
            userIds.addAll(deviceEntityList.stream().map(DeviceEntity::getCreateId).filter(StringUtil::isNotEmpty).distinct().collect(Collectors.toList()));
            userIds.addAll(deviceEntityList.stream().map(DeviceEntity::getUpdateId).filter(StringUtil::isNotEmpty).distinct().collect(Collectors.toList()));
            Map<String, UserDto> userMap = systemService.findUserInfoByIdsFeign(userIds.stream().distinct().collect(Collectors.toList())).getData();

            //根据多个类型id查询类型名称
            List<String> typeIds = deviceEntityList.stream().map(DeviceEntity::getTypeId).distinct().collect(Collectors.toList());
            Map<String, String> typeNameMap = assetTypeDao.findAllById(typeIds).stream().collect(Collectors.toMap(AssetTypeEntity::getId, AssetTypeEntity::getTypeName));

            Map<String, SiteInfoEntity> finalSiteEntityMap = siteEntityMap;
            Map<String, String> finalTenantMap = tenantMap;
            deviceEntityList.forEach(device -> {
                DeviceBasicInfoDto result = new DeviceBasicInfoDto();
                BeanUtils.copyProperties(device, result);
                //根据模型id查询模型名称,模型描述
                if (modelMap.containsKey(device.getModelId())) {
                    ModelEntity model = modelMap.get(device.getModelId());
                    result.setModelName(model.getModelName());
                    result.setModelDesc(model.getModelDesc());
                    result.setLogoPath(model.getLogoPath());
                }

                if (userMap.containsKey(device.getCreateId())) {
                    result.setCreateName(userMap.get(device.getCreateId()).getFullName());
                }
                if (userMap.containsKey(device.getUpdateId())) {
                    result.setUpdateName(userMap.get(device.getUpdateId()).getFullName());
                }

                if (StringUtil.isNotEmpty(device.getTypeId()) && typeNameMap.containsKey(device.getTypeId())) {
                    result.setTypeName(typeNameMap.get(device.getTypeId()));
                }

                if (modelReaMap.containsKey(device.getModelId())) {
                    //获取模型下的扩展数据
                    Map<String, Object> reaMap = Maps.newConcurrentMap();
                    //获取读写map数据
                    JSONObject readwriteObject = new JSONObject();
                    if (StringUtil.isNotEmpty(device.getReadwriteObject())) {
                        readwriteObject = JSONObject.parseObject(device.getReadwriteObject());
                    }
                    for (ModelReaEntity modelRea : modelReaMap.get(device.getModelId())) {
                        ReaEntity reaEntity = reaEntityMap.get(modelRea.getReaId());
                        if (StringUtil.isNotEmpty(reaEntity)) {
                            //读写类型 1-只读 2-读写
                            if (readwriteObject.containsKey(reaEntity.getFieldName()) && StringUtil.isNotEmpty(readwriteObject.get(reaEntity.getFieldName()))) {
                                reaMap.put(reaEntity.getFieldName(), readwriteObject.get(reaEntity.getFieldName()));
                            } else {
                                reaMap.put(reaEntity.getFieldName(), modelRea.getDefaultValue() == null ? "":modelRea.getDefaultValue());
                            }
                        }
                    }
                    result.setReaMap(reaMap);
                }

                //根据站点id查询站点名称和运营商名称
                SiteInfoEntity siteEntity = finalSiteEntityMap.get(device.getSiteId());
                if (StringUtil.isNotEmpty(siteEntity)) {
                    result.setSiteName(siteEntity.getSiteName());
                    //获取运营商名称
                    if (StringUtil.isNotEmpty(siteEntity.getOperatorId())) {
                        result.setOperateId(siteEntity.getOperatorId());
                        result.setOperateName(finalTenantMap.get(siteEntity.getOperatorId()));
                    }
                    //获取产权方名称
                    if (StringUtil.isNotEmpty(siteEntity.getPropertyId())) {
                        result.setPropertyId(siteEntity.getPropertyId());
                        result.setPropertyName(finalTenantMap.get(siteEntity.getPropertyId()));
                    }
                }

                //获取设备状态
                result.setTxStatus(DeviceCommonUtil.getDeviceTxStatus(device.getId(), device.getDeviceNumber()));
                resultMap.put(device.getDeviceNumber(), result);
            });
        }
        return ResponseResult.ok(resultMap);
    }

    /**
     * 根据网关id查询网关下所有子设备信息
     *
     * @param gatWayId 网关设备id
     * @return
     */
    @Override
    public ResponseResult<List<DeviceBasicInfoDto>> findGatewayChildDeviceById(String gatWayId) {
        List<DeviceBasicInfoDto> deviceBasicInfoDtoList = Lists.newArrayList();
        //根据网关id查询关联子设备id
        List<GatewaySubDeviceEntity> allByGatewayId = gatewaySubDeviceDao.findAllByGatewayId(gatWayId);
        if (CollectionUtils.isNotEmpty(allByGatewayId)) {
            deviceBasicInfoDtoList = deviceDao.findAllByIdInAndIsDelete(allByGatewayId.stream().map(GatewaySubDeviceEntity::getSubDeviceId).collect(Collectors.toSet()), 1).stream().map(deviceEntity -> {
                DeviceBasicInfoDto deviceBasicInfoDto = new DeviceBasicInfoDto();
                BeanUtils.copyProperties(deviceEntity, deviceBasicInfoDto);
                return deviceBasicInfoDto;
            }).collect(Collectors.toList());
        }
        return ResponseResult.ok(deviceBasicInfoDtoList);
    }

    /**
     * 根据多个设备id查询设备电枪数据
     *
     * @param deviceIds 多个设备id
     * @return
     */
    @Override
    public ResponseResult<Map<String, List<DeviceGunInfoDto>>> findDeviceGunInfoByDeviceIds(List<String> deviceIds) {
        Map<String, List<DeviceGunInfoDto>> resultMap = Maps.newHashMap();
        List<DeviceGunEntity> deviceGunEntityList = deviceGunDao.findAllByDeviceIdIn(deviceIds);
        if (CollectionUtils.isNotEmpty(deviceGunEntityList)) {
            deviceGunEntityList.stream().collect(Collectors.groupingBy(DeviceGunEntity::getDeviceId)).forEach((k, v) -> {
                resultMap.put(k, v.stream().map(deviceGunEntity -> {
                    DeviceGunInfoDto deviceGunInfoDto = new DeviceGunInfoDto();
                    BeanUtils.copyProperties(deviceGunEntity, deviceGunInfoDto);
                    return deviceGunInfoDto;
                }).collect(Collectors.toList()));
            });
        }
        return ResponseResult.ok(resultMap);
    }

    @Override
    public ResponseResult<List<GatewaySubDeviceDto>> findSiteSubDeviceList(String gatewayId) {
        //返回的集合列表
        List<GatewaySubDeviceDto> resultList = Lists.newArrayList();

        Optional<DeviceEntity> optional = deviceDao.findById(gatewayId);
        if (optional.isPresent()) {
            //根据网关id查询网关子设备
            Map<String, String> subDeviceMap = gatewaySubDeviceDao.findAllByGatewayId(gatewayId).stream().collect(Collectors
                    .toMap(GatewaySubDeviceEntity::getSubDeviceId, GatewaySubDeviceEntity::getGatewayId, (k1, k2) -> k1));
            //过滤掉该网关已存在的子设备
            resultList = deviceDao.findAll(Example.of(DeviceEntity.builder().siteId(optional.get().getSiteId()).accessType(3).isDelete(1).build()))
                    .stream().filter(d -> !subDeviceMap.containsKey(d.getId())).map(deviceEntity -> {
                        GatewaySubDeviceDto result = new GatewaySubDeviceDto();
                        BeanUtils.copyProperties(deviceEntity, result);
                        return result;
                    }).collect(Collectors.toList());
        }
        return ResponseResult.ok(resultList);
    }

    @Override
    @Transactional(transactionManager = DBConstants.JPA_TX_ACCESS, rollbackFor = Exception.class)
    public ResponseResult<Void> batchUpdateGatewaySubDevice(String gatewayId, List<String> subDeviceIds, Integer type) {
        if (CollectionUtils.isNotEmpty(subDeviceIds) && StringUtil.isNotEmpty(type)) {
            if (type == 1) {
                gatewaySubDeviceDao.deleteAll(gatewaySubDeviceDao.findAllBySubDeviceIdIn(subDeviceIds));
                gatewaySubDeviceDao.saveAll(subDeviceIds.stream().map(subDeviceId -> {
                    GatewaySubDeviceEntity subDevice = new GatewaySubDeviceEntity();
                    subDevice.setGatewayId(gatewayId);
                    subDevice.setSubDeviceId(subDeviceId);
                    return subDevice;
                }).collect(Collectors.toList()));
                return ResponseResult.ok();
            }
            if (type == 2) {
                gatewaySubDeviceDao.deleteAll(gatewaySubDeviceDao.findAllByGatewayIdAndSubDeviceIdIn(gatewayId, subDeviceIds));
                return ResponseResult.ok();
            }
        }
        return ResponseResult.paramError(ResponseResult.PARAM_ERROR);
    }

    /**
     * 查询所有电桩设备信息列表
     *
     * @return
     */
    @Override
    public ResponseResult<List<DeviceBasicInfoDto>> findAllPileDeviceInfoList() {
        List<DeviceBasicInfoDto> resultList = Lists.newArrayList();
        //查询所有正常状态电桩信息
        List<DeviceEntity> deviceEntityList = deviceDao.findAllByTypeIdInAndIsDelete(Arrays.asList("28", "29", "30"), 1);
        if (CollectionUtils.isNotEmpty(deviceEntityList)) {

//            //根据多个模型id查询扩展属性数据
//            List<String> modelIds = deviceEntityList.stream().map(DeviceEntity::getModelId).distinct().collect(Collectors.toList());
//            Map<String, Set<String>> modelReaMap = modelReaDao.findAllByModelIdIn(modelIds).stream().filter(r -> StringUtil.isNotEmpty(r.getReaId()))
//                    .collect(Collectors.groupingBy(ModelReaEntity::getModelId, Collectors.mapping(ModelReaEntity::getReaId, Collectors.toSet())));
//            Map<String, ReaEntity> reaEntityMap = reaDao.findAllById(modelReaMap.values().stream().flatMap(Collection::stream).filter(StringUtil::isNotEmpty)
//                    .collect(Collectors.toSet())).stream().collect(Collectors.toMap(ReaEntity::getId, a -> a, (k1, k2) -> k1));

            //根据多个模型id查询扩展属性数据
            List<String> modelIds = deviceEntityList.stream().map(DeviceEntity::getModelId).distinct().collect(Collectors.toList());
            Map<String, List<ModelReaEntity>> modelReaMap = modelReaDao.findAllByModelIdIn(modelIds).stream().filter(r -> StringUtil.isNotEmpty(r.getReaId()))
                    .collect(Collectors.groupingBy(ModelReaEntity::getModelId));
            Map<String, ReaEntity> reaEntityMap = reaDao.findAllById(modelReaMap.values().stream().flatMap(Collection::stream).map(ModelReaEntity::getReaId).filter(StringUtil::isNotEmpty)
                    .collect(Collectors.toSet())).stream().collect(Collectors.toMap(ReaEntity::getId, a -> a, (k1, k2) -> k1));

            //根据多个站点id查询站点基本信息
            List<SiteInfoEntity> siteEntityList = siteInfoDao.findAllById(deviceEntityList.stream().map(DeviceEntity::getSiteId).distinct().collect(Collectors.toList()));
            Map<String, SiteInfoEntity> siteEntityMap = Maps.newHashMap();
//            Map<String, TenantDetailsDto> tenantDetailsDtoMap = Maps.newHashMap();
            if (CollectionUtils.isNotEmpty(siteEntityList)) {
                siteEntityMap = siteEntityList.stream().collect(Collectors.toMap(SiteInfoEntity::getId, a -> a, (k1, k2) -> k1));
//                List<String> operateUnitIdList = siteEntityList.stream().map(SiteInfoEntity::getOperateUnit).filter(StringUtil::isNotEmpty).distinct().collect(Collectors.toList());
//                if (CollectionUtils.isNotEmpty(operateUnitIdList)) {
//                    //根据多个运营商id查询运营商基本信息
//                    ResponseResult<List<TenantDetailsDto>> tenantDetailsByIds = systemService.findTenantDetailsByIds(operateUnitIdList);
//                    if (tenantDetailsByIds.isSuccess() && CollectionUtils.isNotEmpty(tenantDetailsByIds.getData())) {
//                        List<TenantDetailsDto> tenantDetailsDtoList = tenantDetailsByIds.getData();
//                        tenantDetailsDtoMap = tenantDetailsDtoList.stream().collect(Collectors.toMap(TenantDetailsDto::getId, tenantDetailsDto -> tenantDetailsDto, (k1, k2) -> k1));
//                    }
//                }
            }
            Map<String, SiteInfoEntity> finalSiteEntityMap = siteEntityMap;
//            Map<String, TenantDetailsDto> finalTenantDetailsDtoMap = tenantDetailsDtoMap;
            resultList = deviceEntityList.stream().map(deviceEntity -> {
                DeviceBasicInfoDto deviceBasicInfoDto = new DeviceBasicInfoDto();
                BeanUtils.copyProperties(deviceEntity, deviceBasicInfoDto);

                if (modelReaMap.containsKey(deviceEntity.getModelId())) {
                    //获取模型下的扩展数据
                    Map<String, Object> reaMap = Maps.newConcurrentMap();
                    //获取读写map数据
                    JSONObject readwriteObject = new JSONObject();
                    if (StringUtil.isNotEmpty(deviceEntity.getReadwriteObject())) {
                        readwriteObject = JSONObject.parseObject(deviceEntity.getReadwriteObject());
                    }
//                    for (String reaId : modelReaMap.get(deviceEntity.getModelId())) {
//                        ReaEntity reaEntity = reaEntityMap.get(reaId);
//                        if (StringUtil.isNotEmpty(reaEntity)) {
//                            //读写类型 1-只读 2-读写
//                            if (reaEntity.getReadWriteType() == 1 && StringUtil.isNotEmpty(reaEntity.getDefaultValue())) {
//                                reaMap.put(reaEntity.getFieldName(), reaEntity.getDefaultValue());
//                            }
//                            if (reaEntity.getReadWriteType() == 2) {
//                                if (readwriteObject.containsKey(reaEntity.getFieldName()) && StringUtil.isNotEmpty(readwriteObject.get(reaEntity.getFieldName()))) {
//                                    reaMap.put(reaEntity.getFieldName(), readwriteObject.get(reaEntity.getFieldName()));
//                                } else {
//                                    if (StringUtil.isNotEmpty(reaEntity.getDefaultValue())) {
//                                        reaMap.put(reaEntity.getFieldName(), reaEntity.getDefaultValue());
//                                    }
//                                }
//                            }
//                        }
//                    }
                    for (ModelReaEntity modelRea : modelReaMap.get(deviceEntity.getModelId())) {
                        ReaEntity reaEntity = reaEntityMap.get(modelRea.getReaId());
                        if (StringUtil.isNotEmpty(reaEntity)) {
                            if (readwriteObject.containsKey(reaEntity.getFieldName()) && StringUtil.isNotEmpty(readwriteObject.get(reaEntity.getFieldName()))) {
                                reaMap.put(reaEntity.getFieldName(), readwriteObject.get(reaEntity.getFieldName()));
                            } else if (StringUtil.isNotEmpty(modelRea.getDefaultValue())) {
                                reaMap.put(reaEntity.getFieldName(), modelRea.getDefaultValue() == null ? "" : modelRea.getDefaultValue());
                            }
                        }
                    }
                    deviceBasicInfoDto.setReaMap(reaMap);
                }

                //获取站点信息
                SiteInfoEntity siteEntity = finalSiteEntityMap.get(deviceEntity.getSiteId());
                if (StringUtil.isNotEmpty(siteEntity)) {
                    deviceBasicInfoDto.setSiteName(siteEntity.getSiteName());
//                    //查询运营商名称
//                    String operateUnit = siteEntity.getOperateUnit();
//                    if (StringUtil.isNotEmpty(operateUnit)) {
//                        TenantDetailsDto tenantDetailsDto = finalTenantDetailsDtoMap.get(operateUnit);
//                        if (StringUtil.isNotEmpty(tenantDetailsDto)) {
//                            deviceBasicInfoDto.setOperateUnitId(tenantDetailsDto.getId());
//                            deviceBasicInfoDto.setOperateUnitName(tenantDetailsDto.getTenantName());
//                        }
//                    }
                }
                return deviceBasicInfoDto;
            }).collect(Collectors.toList());
        }
        return ResponseResult.ok(resultList);
    }

    @Override
    public ResponseResult<List<DeviceAssetDto>> getDeviceAssetList(String siteId) {
        //返回的集合
        List<DeviceAssetDto> resultList = Lists.newArrayList();
        if (StringUtil.isNotEmpty(siteId)) {
            Optional<SiteInfoEntity> optional = siteInfoDao.findById(siteId);
            if (optional.isPresent()) {
                //根据站点id获取能源信息
                List<ScenarioTypeEntity> scenarioTypeList = scenarioTypeDao.findAll(Example.of(ScenarioTypeEntity.builder().siteId(siteId).build()));
                if (CollectionUtils.isNotEmpty(scenarioTypeList)) {
                    scenarioTypeList.forEach(scenarioType -> {
                        DeviceAssetDto result = new DeviceAssetDto();
                        result.setId(scenarioType.getId());
                        result.setName(scenarioType.getSystemName());
                        result.setParentId(siteId);
                        resultList.add(result);
                    });
                }
                //根据站点id获取设备信息
                List<DeviceEntity> deviceList = deviceDao.findAll(Example.of(DeviceEntity.builder().siteId(siteId).isDelete(1).build()));
                if (CollectionUtils.isNotEmpty(deviceList)) {
                    deviceList.forEach(device -> {
                        DeviceAssetDto result = new DeviceAssetDto();
                        result.setId(device.getId());
                        result.setName(device.getDeviceName());
                        if (StringUtil.isNotEmpty(device.getParentId())) {
                            result.setParentId(device.getParentId());
                        } else {
                            result.setParentId(siteId);
                        }
                        resultList.add(result);
                    });
                }
            }
        }
        return ResponseResult.ok(resultList);
    }

    @Override
    @Transactional(transactionManager = DBConstants.JPA_TX_ACCESS, rollbackFor = Exception.class)
    public ResponseResult<Void> saveDeviceGun(DeviceGunChangeVo deviceGunChangeVo) {
        List<DeviceGunEntity> deviceGunList = deviceGunDao.findAllByDeviceIdAndGunCode(deviceGunChangeVo.getDeviceId(), deviceGunChangeVo.getGunCode());
        if (StringUtil.isEmpty(deviceGunChangeVo.getId())) {
            if (CollectionUtils.isNotEmpty(deviceGunList)) {
                return ResponseResult.paramError("枪编号已存在,不允许添加");
            }
        } else {
            deviceGunList = deviceGunList.stream().filter(d -> !Objects.equals(d.getId(), deviceGunChangeVo.getId())).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(deviceGunList)) {
                return ResponseResult.paramError("枪编号已存在多条,不允许编辑");
            }
        }
        //添加设备枪数据
        DeviceGunEntity deviceGun = new DeviceGunEntity();
        BeanUtils.copyProperties(deviceGunChangeVo, deviceGun);
        if (StringUtil.isNotEmpty(deviceGunChangeVo.getId())) {
            deviceGunDao.findById(deviceGunChangeVo.getId()).ifPresent(deviceGunEntity -> deviceGun.setCreateTime(deviceGunEntity.getCreateTime()));
        }
        deviceGunDao.save(deviceGun);

        //推送充电站信息
        deviceDao.findById(deviceGun.getDeviceId()).ifPresent(device -> configureService.notificationStationInfo(Collections.singleton(device.getSiteId())));
        return ResponseResult.ok();
    }

    @Override
    public ResponseResult<List<DeviceGunListDto>> findDeviceGunListByDeviceId(String deviceId) {
        //返回的对象列表
        List<DeviceGunListDto> resultList = Lists.newArrayList();
        //根据模型id查询模型枪数据列表
        List<DeviceGunEntity> modelGunList = deviceGunDao.findAllByDeviceId(deviceId);
        if (CollectionUtils.isNotEmpty(modelGunList)) {
            resultList = modelGunList.stream().map(modelGun -> {
                DeviceGunListDto result = new DeviceGunListDto();
                BeanUtils.copyProperties(modelGun, result);
                return result;
            }).sorted(Comparator.comparing(DeviceGunListDto::getGunCode)).collect(Collectors.toList());
        }
        return ResponseResult.ok(resultList);
    }

    /**
     * 批量新增或编辑互联互通设备数据
     *
     * @param interflowDeviceVos
     * @return
     */
    @Override
    @Transactional(transactionManager = DBConstants.JPA_TX_ACCESS, rollbackFor = Exception.class)
    public ResponseResult<Void> saveOrUpdateInterflowDevice(List<InterflowDeviceVo> interflowDeviceVos) {
        //根据多个模型id查询功能点列表
        Map<String, List<ModelFunctionListDto>> functionListByModeIds = getFunctionListByModeIds(interflowDeviceVos.stream().map(InterflowDeviceVo::getModelId).collect(Collectors.toSet()));

        //根据设备编码查询设备列表信息
        List<DeviceEntity> deviceEntityList = deviceDao.findAllByDeviceNumberInAndIsDelete(interflowDeviceVos.stream().map(InterflowDeviceVo::getDeviceNumber).collect(Collectors.toList()), 1);
        if (CollectionUtils.isNotEmpty(deviceEntityList)) {
            List<InterflowGunVo> interflowGunVoList = Lists.newArrayList();
            //把设备信息转换为map
            Map<String, InterflowDeviceVo> interflowDeviceVoMap = interflowDeviceVos.stream().collect(Collectors.toMap(InterflowDeviceVo::getDeviceNumber, interflowDeviceVo -> interflowDeviceVo, (k1, k2) -> k1));
            List<DeviceEntity> deviceEntityList1 = deviceDao.saveAll(deviceEntityList.stream().peek(deviceEntity -> {
                InterflowDeviceVo interflowDeviceVo = interflowDeviceVoMap.get(deviceEntity.getDeviceNumber());
                if (StringUtil.isNotEmpty(interflowDeviceVo)) {
                    deviceEntity.setTypeId(interflowDeviceVo.getTypeId());
                    deviceEntity.setModelId(interflowDeviceVo.getModelId());
                    deviceEntity.setSiteId(interflowDeviceVo.getSiteId());
                    deviceEntity.setDeviceName(StringUtil.isEmpty(interflowDeviceVo.getDeviceName()) ? interflowDeviceVo.getDeviceNumber() : interflowDeviceVo.getDeviceName());
                    deviceEntity.setReadwriteObject(interflowDeviceVo.getReadwriteObject());
                    deviceEntity.setOperateStatus(interflowDeviceVo.getOperateStatus());
                    deviceEntity.setUpdateTime(LocalDateTime.now());
                    //获取设备下的接口信息
                    List<InterflowGunVo> interflowGunVos = interflowDeviceVo.getInterflowGunVoList();
                    if (CollectionUtils.isNotEmpty(interflowGunVos)) {
                        interflowGunVoList.addAll(interflowGunVos.stream().peek(i -> i.setDeviceId(deviceEntity.getId())).collect(Collectors.toList()));
                    }
                }
            }).collect(Collectors.toList()));
            //设备数据存入redis缓存
            deviceEntityList1.forEach(deviceEntity -> {
                if (!functionListByModeIds.isEmpty() && functionListByModeIds.containsKey(deviceEntity.getModelId())) {
                    this.saveDeviceRedis(null, deviceEntity, functionListByModeIds.get(deviceEntity.getModelId()));
                }
            });
            //处理设备接口信息
            if (CollectionUtils.isNotEmpty(interflowGunVoList)) {
                //根据多个设备id，删除下面电枪信息
                deviceGunDao.deleteAllByDeviceIdIn(interflowGunVoList.stream().map(InterflowGunVo::getDeviceId).distinct().collect(Collectors.toList()));
                //批量插入电枪信息
                deviceGunDao.saveAll(interflowGunVoList.stream().map(interflowGunVo -> {
                    DeviceGunEntity deviceGunEntity = new DeviceGunEntity();
                    BeanUtils.copyProperties(interflowGunVo, deviceGunEntity);
                    deviceGunEntity.setCreateTime(LocalDateTime.now());
                    deviceGunEntity.setUpdateTime(LocalDateTime.now());
                    return deviceGunEntity;
                }).collect(Collectors.toList()));
            }
            //过滤掉已查询出修改的设备
            interflowDeviceVos = interflowDeviceVos.stream().filter(interflowDeviceVo -> !deviceEntityList.stream().map(DeviceEntity::getDeviceNumber).collect(Collectors.toList()).contains(interflowDeviceVo.getDeviceNumber())).collect(Collectors.toList());
        }
        //如果设备参数列表还有数据，则做批量插入操作
        if (CollectionUtils.isNotEmpty(interflowDeviceVos)) {
            List<DeviceGunEntity> deviceGunEntities = Lists.newArrayList();
            //批量插入设备信息
            List<DeviceEntity> deviceEntities = deviceDao.saveAll(interflowDeviceVos.stream().map(interflowDeviceVo -> {
                DeviceEntity deviceEntity = new DeviceEntity();
                BeanUtils.copyProperties(interflowDeviceVo, deviceEntity);
                deviceEntity.setDeviceName(StringUtil.isEmpty(interflowDeviceVo.getDeviceName()) ? interflowDeviceVo.getDeviceNumber() : interflowDeviceVo.getDeviceName());
                deviceEntity.setCreateTime(LocalDateTime.now());
                deviceEntity.setUpdateTime(LocalDateTime.now());
                return deviceEntity;
            }).collect(Collectors.toList()));

            //设备数据存入redis缓存
            deviceEntities.forEach(deviceEntity -> {
                if (!functionListByModeIds.isEmpty() && functionListByModeIds.containsKey(deviceEntity.getModelId())) {
                    this.saveDeviceRedis(null, deviceEntity, functionListByModeIds.get(deviceEntity.getModelId()));
                }
            });

            //把设备信息转换为map
            Map<String, DeviceEntity> deviceEntityMap = deviceEntities.stream().collect(Collectors.toMap(DeviceEntity::getDeviceNumber, deviceEntity -> deviceEntity, (k1, k2) -> k1));
            interflowDeviceVos.forEach(interflowDeviceVo -> {
                List<InterflowGunVo> interflowGunVos = interflowDeviceVo.getInterflowGunVoList();
                if (CollectionUtils.isNotEmpty(interflowGunVos)) {
                    DeviceEntity deviceEntity = deviceEntityMap.get(interflowDeviceVo.getDeviceNumber());
                    List<InterflowGunVo> gunVoList = interflowGunVos.stream().peek(i -> i.setDeviceId(deviceEntity.getId())).collect(Collectors.toList());
                    deviceGunEntities.addAll(gunVoList.stream().map(interflowGunVo -> {
                        DeviceGunEntity deviceGunEntity = new DeviceGunEntity();
                        BeanUtils.copyProperties(interflowGunVo, deviceGunEntity);
                        deviceGunEntity.setCreateTime(LocalDateTime.now());
                        deviceGunEntity.setUpdateTime(LocalDateTime.now());
                        return deviceGunEntity;
                    }).collect(Collectors.toList()));
                }
            });
            //批量插入电枪信息
            deviceGunDao.saveAll(deviceGunEntities);
        }
        return ResponseResult.ok();
    }

    @Override
    @Transactional(transactionManager = DBConstants.JPA_TX_ACCESS, rollbackFor = Exception.class)
    public ResponseResult<Void> saveAllDeviceGun(List<String> deviceGunIds, List<String> deviceIds) {
        //根据多个设备枪编号查询设备枪数据列表
        List<DeviceGunEntity> deviceGunList = deviceGunDao.findAllById(deviceGunIds);
        if (CollectionUtils.isNotEmpty(deviceGunList)) {
            //校验其它设备是否存在
            Map<String, String> deviceNameMap = deviceDao.findAllById(deviceIds).stream().collect(Collectors.toMap(DeviceEntity::getId,
                    DeviceEntity::getDeviceName, (k1, k2) -> k1));
            Set<String> gunCodes = deviceGunList.stream().map(DeviceGunEntity::getGunCode).collect(Collectors.toSet());
            for (DeviceGunEntity deviceGun : deviceGunDao.findAllByDeviceIdIn(deviceIds)) {
                if (gunCodes.contains(deviceGun.getGunCode())) {
                    if (deviceNameMap.containsKey(deviceGun.getDeviceId())) {
                        return ResponseResult.paramError(deviceNameMap.get(deviceGun.getDeviceId()) + "的" + deviceGun.getGunCode() + "号枪已存在,不允许复制");
                    }
                    return ResponseResult.paramError("其它设备的" + deviceGun.getGunCode() + "号枪已存在,不允许复制");
                }
            }
            List<DeviceGunEntity> addDeviceGunList = Lists.newArrayList();
            deviceIds.forEach(deviceId -> {
                for (DeviceGunEntity deviceGun : deviceGunList) {
                    DeviceGunEntity deviceGunEntity = new DeviceGunEntity();
                    BeanUtils.copyProperties(deviceGun, deviceGunEntity);
                    deviceGunEntity.setId(null);
                    deviceGunEntity.setDeviceId(deviceId);
                    addDeviceGunList.add(deviceGunEntity);
                }
            });
            deviceGunDao.saveAll(addDeviceGunList);

            //批量推送充电站信息
            List<DeviceEntity> deviceList = deviceDao.findAllById(deviceIds);
            if (CollectionUtils.isNotEmpty(deviceList)) {
                Set<String> siteIds = deviceList.stream().map(DeviceEntity::getSiteId).collect(Collectors.toSet());
                configureService.notificationStationInfo(siteIds);
            }
            return ResponseResult.ok();
        }
        return ResponseResult.paramError(ResponseResult.PARAM_ERROR);
    }

    @Override
    @Transactional(transactionManager = DBConstants.JPA_TX_ACCESS, rollbackFor = Exception.class)
    public ResponseResult<Void> deleteAllDeviceGun(List<String> deviceGunIds) {
        deviceGunDao.deleteAll(deviceGunDao.findAllById(deviceGunIds));
        return ResponseResult.ok();
    }

    @Override
    @Transactional(transactionManager = DBConstants.JPA_TX_ACCESS, rollbackFor = Exception.class)
    public ResponseResult<Void> saveDeviceFunctionField(String deviceId, String functionFields) {
        //根据设备id查询设备功能点字段数据
        DeviceFunctionFieldEntity deviceFunctionField = deviceFunctionFieldDao.findOne(Example.of(DeviceFunctionFieldEntity.builder().deviceId(deviceId).build())).orElseGet(DeviceFunctionFieldEntity::new);
        deviceFunctionField.setDeviceId(deviceId);
        deviceFunctionField.setFunctionFields(functionFields);
        deviceFunctionFieldDao.save(deviceFunctionField);
        return ResponseResult.ok();
    }

    @Override
    public ResponseResult<List<ModelFunctionListDto>> findDeviceFunctionListByDeviceId(String deviceId) {
        //返回的集合
        List<ModelFunctionListDto> resultList = Lists.newArrayList();
        //根据设备id查询模型id
        AtomicReference<String> modelId = new AtomicReference<>();
        siteInfoDao.findById(deviceId).ifPresent(s -> modelId.set(s.getSiteModelId()));
        if (StringUtil.isEmpty(modelId.get())) {
            scenarioTypeDao.findById(deviceId).ifPresent(s -> modelId.set(s.getModelId()));
        }
        if (StringUtil.isEmpty(modelId.get())) {
            deviceDao.findById(deviceId).ifPresent(d -> modelId.set(d.getModelId()));
        }
        if (StringUtil.isNotEmpty(modelId.get())) {
            resultList = DeviceCommonUtil.getFunctionListByModeId(modelId.get());
        }
        return ResponseResult.ok(resultList);
    }

    @Override
    public ResponseResult<List<SiteDeviceTreeDto>> findSiteDeviceListBySiteId(String siteId) {
        //返回的集合
        List<SiteDeviceTreeDto> resultList = Lists.newArrayList();
        Optional<SiteInfoEntity> optional = siteInfoDao.findById(siteId);
        if (optional.isPresent()) {
            SiteInfoEntity siteInfo = optional.get();
            //根据站点模型id查询资产分类id
            Map<String, String> siteModelMap = modelDao.findAllById(Collections.singleton(siteInfo.getSiteModelId())).stream().collect(Collectors.toMap(ModelEntity::getId, ModelEntity::getTypeId, (k1, k2) -> k1));
            //根据多个站点id查询站点下的直连设备和网关设备数据
            Set<String> siteIds = Collections.singleton(siteId);
            Map<String, List<DeviceEntity>> deviceMap = deviceDao.findAllBySiteIdInAndAccessTypeNotAndIsDelete(siteIds, 3, 1).stream().collect(Collectors.groupingBy(DeviceEntity::getSiteId));
            //根据多个网关设备id查询网关子设备数据
            Set<String> gatewayIds = deviceMap.entrySet().stream().flatMap(device -> device.getValue().stream().filter(s -> StringUtil.isNotEmpty(s.getAccessType()) && Objects.equals(s.getAccessType(), 2)).map(DeviceEntity::getId)).collect(Collectors.toSet());
            List<GatewaySubDeviceEntity> gatewaySubDeviceList = gatewaySubDeviceDao.findAllByGatewayIdIn(gatewayIds);
            Map<String, Set<String>> subDeviceIdMap = gatewaySubDeviceList.stream().collect(Collectors.groupingBy(GatewaySubDeviceEntity::getGatewayId, Collectors.mapping(GatewaySubDeviceEntity::getSubDeviceId, Collectors.toSet())));
            Set<String> subDeviceIds = gatewaySubDeviceList.stream().map(GatewaySubDeviceEntity::getSubDeviceId).filter(StringUtil::isNotEmpty).collect(Collectors.toSet());
            Map<String, DeviceEntity> subDeviceMap = deviceDao.findAllByIdInAndIsDelete(subDeviceIds, 1).stream().collect(Collectors.toMap(DeviceEntity::getId, a -> a, (k1, k2) -> k1));

            //站点数据
            resultList.add(SiteDeviceTreeDto.builder().id(siteInfo.getId()).code(siteInfo.getSiteCode()).name(siteInfo.getSiteName()).type(1).typeId(StringUtil.isNotEmpty(siteInfo.getSiteModelId()) ? siteModelMap.get(siteInfo.getSiteModelId()) : null).build());

            if (deviceMap.containsKey(siteInfo.getId())) {
                deviceMap.get(siteInfo.getId()).forEach(device -> {
                    //直连设备和网关设备数据
                    resultList.add(SiteDeviceTreeDto.builder().id(device.getId()).code(device.getDeviceNumber()).name(device.getDeviceName()).type(2).parentId(siteInfo.getId()).typeDetail(device.getAccessType()).typeId(device.getTypeId()).build());
                    //网关子设备数据
                    if (subDeviceIdMap.containsKey(device.getId())) {
                        subDeviceIdMap.get(device.getId()).forEach(subDeviceId -> {
                            if (subDeviceMap.containsKey(subDeviceId)) {
                                DeviceEntity subDevice = subDeviceMap.get(subDeviceId);
                                resultList.add(SiteDeviceTreeDto.builder().id(subDevice.getId()).code(subDevice.getDeviceNumber()).name(subDevice.getDeviceName()).type(3).parentId(device.getId()).typeDetail(subDevice.getAccessType()).typeId(subDevice.getTypeId()).build());
                            }
                        });
                    }
                });
            }
        }
        return ResponseResult.ok(resultList);
    }

    @Override
    public ResponseResult<List<DeviceAlarmEventListDto>> findDeviceNotRecoveEventList(String deviceId) {
        //返回的集合
        List<DeviceAlarmEventListDto> resultList = Lists.newArrayList();

        //根据查询条件查询设备事件数据
        List<DeviceEventEntity> deviceEventList = deviceEventDao.findAll((Specification<DeviceEventEntity>) (root, cq, cb) -> {
            List<Predicate> list = new ArrayList<>();
            list.add(cb.equal(root.get("deviceId"), deviceId));//设备id
            list.add(cb.equal(root.get("eventStatus"), 0));//设备id
            return cb.and(list.toArray(new Predicate[0]));
        }, Sort.by("createTime").descending());

        if (CollectionUtils.isNotEmpty(deviceEventList)) {
            //根据多个模型事件id查询模型事件数据
            Set<String> eventIds = deviceEventList.stream().map(DeviceEventEntity::getEventId).collect(Collectors.toSet());
            Map<String, ModelEventEntity> modelEventMap = modelEventDao.findAllById(eventIds).stream().collect(Collectors.toMap(ModelEventEntity::getId, a -> a, (k1, k2) -> k1));

            //根据多个功能点标识获取功能点名称
//            Set<String> functionLogos = modelEventMap.values().stream().map(s -> JSON.parseArray(s.getFunctionLogos(), String.class)).flatMap(Collection::stream).collect(Collectors.toSet());
//            Map<String, String> functionNameMap = functionDao.findAllByFunctionLogoInAndIsDelete(functionLogos, 1).stream().collect(Collectors.toMap(FunctionEntity::getFunctionLogo, FunctionEntity::getFunctionName, (k1, k2) -> k1));

            //对数据组装
            resultList.addAll(deviceEventList.stream().map(deviceEvent -> {
                DeviceAlarmEventListDto result = new DeviceAlarmEventListDto();
                result.setId(deviceEvent.getId());
                result.setEventStatus(deviceEvent.getEventStatus());
                result.setFunctionNames(deviceEvent.getEventSource());
                if (modelEventMap.containsKey(deviceEvent.getEventId())) {
                    ModelEventEntity modelEvent = modelEventMap.get(deviceEvent.getEventId());
                    result.setEventName(modelEvent.getEventName());
                    result.setEventLevel(modelEvent.getEventLevel());
                    result.setEventDesc(modelEvent.getEventDesc());
                    result.setIsAllow(modelEvent.getIsAllow());
                }
                if (StringUtil.isNotEmpty(deviceEvent.getCreateTime())) {
                    result.setCreateTime(DateUtil.localDateTimeToStr(deviceEvent.getCreateTime()));
                }
                if (StringUtil.isNotEmpty(deviceEvent.getUpdateTime())) {
                    result.setUpdateTime(DateUtil.localDateTimeToStr(deviceEvent.getUpdateTime()));
                }
                result.setType(1);
                return result;
            }).collect(Collectors.toList()));
        }

        //根据查询条件查询设备告警数据
        Optional<DeviceEntity> optional = deviceDao.findById(deviceId);
        if (optional.isPresent() && StringUtil.isNotEmpty(optional.get().getDeviceNumber())) {
            AlarmRecordQueryVo alarmQueryVo = new AlarmRecordQueryVo();
            alarmQueryVo.setDeviceCodes(Collections.singleton(optional.get().getDeviceNumber()));
            alarmQueryVo.setAlarmStatus(0);
            resultList.addAll(protocolService.findAlarmRecordList(alarmQueryVo).getData()
                    .stream().filter(alarmRecord -> StringUtil.isNotEmpty(alarmRecord.getFaultCode()))
                    .map(alarmRecord -> {
                        DeviceAlarmEventListDto result = new DeviceAlarmEventListDto();
                        BeanUtils.copyProperties(alarmRecord, result);
                        result.setFunctionNames("故障码: " + alarmRecord.getFaultCode());
                        result.setEventStatus(alarmRecord.getAlarmStatus());
                        result.setIsAllow(2);
                        result.setType(2);
                        return result;
                    }).collect(Collectors.toList()));
        }
        return ResponseResult.ok(resultList);
    }

    /**
     * 根据设备id查询设备功能属性列表数据
     *
     * @param deviceId
     * @return
     */
    @Override
    public ResponseResult<List<FunctionDataDto>> findDeviceFunctionDataList(String deviceId) {
        //返回的功能属性数据列表
        List<FunctionDataDto> resultList = Lists.newArrayList();
        //根据设备id查询对应的模型标准功能点
        Optional<DeviceEntity> optional = deviceDao.findById(deviceId);
        if (optional.isPresent()) {
            DeviceEntity device = optional.get();
            //根据模型id查询所有模型标准功能id
            Set<String> functionIds = modelFunctionDao.findAllByModelIdAndIsDelete(device.getModelId(), 1).stream().map(ModelFunctionEntity::getFunctionId).collect(Collectors.toSet());

            //获取设备redis里面的数据
            List<FunctionEntity> functionList = functionDao.findAllById(functionIds);
            Set<String> functionLogos = functionList.stream().map(FunctionEntity::getFunctionLogo).collect(Collectors.toSet());
            Map<String, RealDataModel> realDataModelMap = DeviceCommonUtil.getDeviceFunctions(Collections.singleton(deviceId), functionLogos).get(deviceId);
            //获取设备的通信状态
            Integer txStatus = DeviceCommonUtil.getDeviceTxStatus(deviceId, device.getDeviceNumber());
            resultList = functionList.stream().map(function -> {
                FunctionDataDto result = new FunctionDataDto();
                BeanUtils.copyProperties(function, result);
                result.setFunctionId(function.getId());
                //获取量测实时值
                if (txStatus != null && MapUtils.isNotEmpty(realDataModelMap) && realDataModelMap.containsKey(function.getFunctionLogo())) {
                    RealDataModel realDataModel = realDataModelMap.get(function.getFunctionLogo());
                    if (realDataModel != null) {
                        result.setValue(realDataModel.getDataValue());
                        result.setDateTime(realDataModel.getDateTime());
                    }
                }
                return result;
            }).collect(Collectors.toList());
        }
        return ResponseResult.ok(resultList);
    }

    @Override
    public ResponseResult<Map<String, Map<String, ModelFunctionListDto>>> getModelFunctionListByModelIds(Set<String> modelIds, String functionLogos) {
        //返回的对象
        Map<String, Map<String, ModelFunctionListDto>> resultMap = Maps.newHashMap();
        //根据多个模型id查询模型关联功能点数据
        Map<String, List<ModelFunctionListDto>> modelFunctionMap = getFunctionListByModeIds(modelIds);
        if (MapUtils.isNotEmpty(modelFunctionMap)) {
            //根据功能点标识过滤
            if (StringUtil.isNotEmpty(functionLogos)) {
                Set<String> functionLogoSet = Arrays.stream(functionLogos.split(FileUtil.COMMA)).collect(Collectors.toSet());
                resultMap = modelFunctionMap.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey,
                        entry -> entry.getValue().stream().filter(f -> functionLogoSet.contains(f.getFunctionLogo()))
                                .collect(Collectors.toMap(ModelFunctionListDto::getFunctionLogo,
                                Function.identity(), (k1,k2) -> k1))));
            } else {
                resultMap = modelFunctionMap.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey,
                        entry -> entry.getValue().stream().collect(Collectors.toMap(ModelFunctionListDto::getFunctionLogo,
                                Function.identity(), (k1,k2) -> k1))));
            }
        }
        return ResponseResult.ok(resultMap);
    }

}