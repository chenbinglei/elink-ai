package com.sunmax.device.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.sunmax.common.config.redis.RedisDeviceUtil;
import com.sunmax.common.config.redis.RedisGeneralUtil;
import com.sunmax.common.dto.PageDto;
import com.sunmax.common.dto.protocol.mqtt.web.CtrlBoardInfo;
import com.sunmax.common.dto.system.UserDto;
import com.sunmax.common.entity.BaseEntity;
import com.sunmax.common.model.DeviceModel;
import com.sunmax.common.model.general.GatewayRealModel;
import com.sunmax.common.model.general.PileRealModel;
import com.sunmax.common.util.DateUtil;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.util.StringUtil;
import com.sunmax.common.util.SunMaxUtil;
import com.sunmax.common.util.oss.FileUtil;
import com.sunmax.common.vo.device.DeviceBatchUpdateVo;
import com.sunmax.common.vo.protocol.PileBatchUpdateVo;
import com.sunmax.common.vo.statics.DeviceParamVo;
import com.sunmax.device.constants.DBConstants;
import com.sunmax.device.dao.access.*;
import com.sunmax.device.dao.model.AssetTypeDao;
import com.sunmax.device.dto.firmware.FirmwareDto;
import com.sunmax.device.dto.firmware.FirmwareParseDto;
import com.sunmax.device.dto.task.DeviceTaskListDto;
import com.sunmax.device.dto.task.DeviceTaskRecordListDto;
import com.sunmax.device.dto.task.DeviceUpdateDto;
import com.sunmax.device.entity.access.*;
import com.sunmax.device.entity.model.AssetTypeEntity;
import com.sunmax.device.service.DeviceTaskService;
import com.sunmax.device.service.feign.ProtocolService;
import com.sunmax.device.service.feign.SystemService;
import com.sunmax.device.vo.task.DeviceTaskChangeVo;
import com.sunmax.device.vo.task.DeviceTaskQueryVo;
import com.sunmax.device.vo.task.DeviceTaskRecordVo;
import com.sunmax.device.vo.task.DeviceUpdateQueryVo;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DeviceTaskServiceImpl implements DeviceTaskService {

    @Autowired
    private FirmwareDao firmwareDao;

    @Autowired
    private SystemService systemService;

    @Autowired
    private DeviceDao deviceDao;

    @Autowired
    private SiteInfoDao siteInfoDao;

    @Autowired
    private DeviceTaskDao deviceTaskDao;

    @Autowired
    private DeviceTaskRecordDao deviceTaskRecordDao;

    @Autowired
    private AssetTypeDao assetTypeDao;

    @Autowired
    private ProtocolService protocolService;

    @Override
    public ResponseResult<List<FirmwareDto>> getFirmwareListByTypeId(String typeId) {
        //返回的集合
        List<FirmwareDto> resultList = new ArrayList<>();
        List<FirmwareEntity> firmwareList = firmwareDao.findAll(Example.of(FirmwareEntity.builder().typeId(typeId).build()));
        return ResponseResult.ok(FirmwareServiceImpl.getFirmware(resultList, firmwareList, systemService, assetTypeDao));
    }

    @Override
    public ResponseResult<PageDto<DeviceUpdateDto>> queryDeviceUpdateList(DeviceUpdateQueryVo deviceQueryVo) {
        //返回的集合
        List<DeviceUpdateDto> resultList = Lists.newArrayList();
        //根据条件查询出设备数据
        List<DeviceEntity> deviceList = deviceDao.findAll((Specification<DeviceEntity>) (root, cq, cb) -> {
            List<Predicate> predicates = Lists.newArrayList();
            predicates.add(cb.equal(root.get("typeId"), deviceQueryVo.getTypeId()));
            predicates.add(cb.equal(root.get("isDelete"), 1));
            predicates.add(cb.isNotNull(root.get("deviceNumber")));
            if (StringUtil.isNotEmpty(deviceQueryVo.getDeviceName())) {
                predicates.add(cb.like(root.get("deviceName"), "%" + deviceQueryVo.getDeviceName() + "%"));
            }
            if (StringUtil.isNotEmpty(deviceQueryVo.getDeviceNumber())) {
                predicates.add(cb.like(root.get("deviceNumber"), "%" + deviceQueryVo.getDeviceNumber() + "%"));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        });
        if (CollectionUtils.isNotEmpty(deviceList)) {
            //根据设备型号过滤设备数据
            deviceList = deviceList.stream().filter(device -> {
                //过滤设备序列号
                if (StringUtil.isEmpty(device.getDeviceNumber())) {
                    return false;
                }
                //获取读写map数据
                if (StringUtil.isNotEmpty(device.getReadwriteObject())) {
                    JSONObject readwriteObject = JSONObject.parseObject(device.getReadwriteObject());
                    if (readwriteObject.containsKey(DeviceParamVo.EQUIPMENT_MODEL)) {
                        return readwriteObject.getString(DeviceParamVo.EQUIPMENT_MODEL).contains(deviceQueryVo.getEquipmentModel());
                    }
                }
                return false;
            }).collect(Collectors.toList());
            //根据多个站点id查询站点名称
            Set<String> siteIds = deviceList.stream().map(DeviceEntity::getSiteId).collect(Collectors.toSet());
            Map<String, String> siteNameMap = siteInfoDao.findAllById(siteIds).stream().collect(Collectors.toMap(BaseEntity::getId, SiteInfoEntity::getSiteName));

            //对数据进行组装
            List<Integer> gunStatusList = Arrays.asList(1, 2, 4, 5, 8);
            for (DeviceEntity device : deviceList) {
                DeviceUpdateDto result = new DeviceUpdateDto();
                BeanUtils.copyProperties(device, result);
                if (siteNameMap.containsKey(device.getSiteId())) {
                    result.setSiteName(siteNameMap.get(device.getSiteId()));
                }
                //获取设备状态
                result.setStatus(2); //默认给不可用
                DeviceModel deviceModel = RedisDeviceUtil.getDevice(device.getDeviceNumber());
                if (deviceModel != null /*&& deviceModel.getTxStatus() != 0 && deviceModel.getTxStatus() != 88*/) {
                    boolean isAdd = true;
                    //充电桩类型
                    List<String> pileTypeIds = Arrays.asList("29", "30");
                    if (pileTypeIds.contains(deviceQueryVo.getTypeId())) {
                        PileRealModel pileRealModel = RedisGeneralUtil.getPileRealModel(device.getDeviceNumber());
                        if (pileRealModel != null) {
                            //校验枪状态是否在充放电中
                            if (StringUtil.isNotEmpty(pileRealModel.getWorkStatus()) && pileRealModel.getWorkStatus() != -1 && pileRealModel.getWorkStatus() != 88) {
                                List<PileRealModel.GunRealModel> gunRealModelList = pileRealModel.getGunRealModelMap().values().stream()
                                        .filter(gunRealModel -> StringUtil.isNotEmpty(gunRealModel.getGunStatus()) && gunStatusList.contains(gunRealModel.getGunStatus()))
                                        .collect(Collectors.toList());
                                if (CollectionUtils.isEmpty(gunRealModelList)) {
                                    result.setStatus(1);
                                }
                            }
                            if (MapUtils.isNotEmpty(pileRealModel.getCtrlBoardInfoMap()) && pileRealModel.getCtrlBoardInfoMap().containsKey(deviceQueryVo.getFirmwareType())) {
                                CtrlBoardInfo ctrlBoardInfo = pileRealModel.getCtrlBoardInfoMap().get(deviceQueryVo.getFirmwareType());
                                result.setCurrentVersion(ctrlBoardInfo.getFirmMainVCode() + FileUtil.POINT + ctrlBoardInfo.getFirmSecVCode());
                            } else {
                                isAdd = false;
                            }
                        }
                    }
                    //TODO 边缘网关待定
                    if (Objects.equals(deviceQueryVo.getTypeId(), "31")) {
                        GatewayRealModel gatewayRealModel = RedisGeneralUtil.getGatewayRealModel(device.getDeviceNumber());
                        if (gatewayRealModel != null) {
                            isAdd = false;
                        }
                    }
                    if (isAdd) {
                        resultList.add(result);
                    }
                }
            }

            //根据站点名称过滤
            if (StringUtil.isNotEmpty(deviceQueryVo.getSiteName())) {
                resultList = resultList.stream().filter(device -> device.getSiteName().contains(deviceQueryVo.getSiteName())).collect(Collectors.toList());
            }
            //根据设备状态过滤
            if (StringUtil.isNotEmpty(deviceQueryVo.getStatus())) {
                resultList = resultList.stream().filter(device -> Objects.equals(device.getStatus(), deviceQueryVo.getStatus())).collect(Collectors.toList());
            }
        }
        return ResponseResult.ok(new PageDto<>(resultList, deviceQueryVo.getPage(), deviceQueryVo.getSize()));
    }

    @Override
    public ResponseResult<Set<String>> getDeviceVersionList(String typeId, String equipmentModel, Integer firmwareType) {
        //返回的集合
        Set<String> resultSet = Sets.newHashSet();
        List<DeviceEntity> deviceList = deviceDao.findAll(Example.of(DeviceEntity.builder().typeId(typeId).isDelete(1).build()));
        if (CollectionUtils.isNotEmpty(deviceList)) {
            deviceList.stream().filter(device -> {
                if (StringUtil.isEmpty(device.getDeviceNumber())) {
                    return false;
                }
                //获取读写map数据
                if (StringUtil.isNotEmpty(device.getReadwriteObject())) {
                    JSONObject readwriteObject = JSONObject.parseObject(device.getReadwriteObject());
                    if (readwriteObject.containsKey(DeviceParamVo.EQUIPMENT_MODEL)) {
                        return Objects.equals(readwriteObject.getString(DeviceParamVo.EQUIPMENT_MODEL), equipmentModel);
                    }
                }
                return false;
            }).forEach(device -> {
                DeviceModel deviceModel = RedisDeviceUtil.getDevice(device.getDeviceNumber());
                if (deviceModel != null && deviceModel.getTxStatus() != 0 && deviceModel.getTxStatus() != 88) {
                    //充电桩类型
                    List<String> pileTypeIds = Arrays.asList("29", "30");
                    if (pileTypeIds.contains(typeId)) {
                        PileRealModel pileRealModel = RedisGeneralUtil.getPileRealModel(device.getDeviceNumber());
                        if (pileRealModel != null && MapUtils.isNotEmpty(pileRealModel.getCtrlBoardInfoMap())) {
                            if (pileRealModel.getCtrlBoardInfoMap().containsKey(firmwareType)) {
                                //获取固件版本号
                                CtrlBoardInfo ctrlBoardInfo = pileRealModel.getCtrlBoardInfoMap().get(firmwareType);
                                resultSet.add(ctrlBoardInfo.getFirmMainVCode() + FileUtil.POINT + ctrlBoardInfo.getFirmSecVCode());
                            }
                        }
                    }
                    //TODO 边缘网关待定
                }
            });
        }
        return ResponseResult.ok(resultSet);
    }

    @Override
    @Transactional(transactionManager = DBConstants.JPA_TX_ACCESS, rollbackFor = Exception.class)
    public ResponseResult<Void> createDeviceTask(DeviceTaskChangeVo taskChangeVo) {
        //根据固件包id查询固件包数据
        Optional<FirmwareEntity> optional = firmwareDao.findById(taskChangeVo.getFirmwareId());
        if (optional.isPresent()) {
            FirmwareEntity firmware = optional.get();

            //校验文件路径
            if (StringUtil.isEmpty(firmware.getFirmwarePath())) {
                return ResponseResult.paramError("固件包路径为空 不允许升级");
            }
            FirmwareParseDto firmwareParse = FirmwareServiceImpl.parseFirmwareData(SunMaxUtil.toHexString(FileUtil.readFileByte(firmware.getFirmwarePath())));
            if (firmwareParse == null) {
                return ResponseResult.paramError("解析固件包数据错误 不允许升级");
            }

            //添加设备升级任务
            DeviceTaskEntity deviceTask = new DeviceTaskEntity();
            deviceTask.setTaskName(taskChangeVo.getTaskName());
            deviceTask.setTaskStatus(1);
            deviceTask.setTaskDesc(taskChangeVo.getTaskDesc());
            deviceTask.setTypeId(taskChangeVo.getTypeId());
            deviceTask.setEquipmentModel(taskChangeVo.getEquipmentModel());
            deviceTask.setFirmwareType(taskChangeVo.getFirmwareType());
            deviceTask.setTargetVersion(firmware.getFirmwareVersion());
            deviceTask.setCreateId(taskChangeVo.getUserId());
            DeviceTaskEntity save = deviceTaskDao.save(deviceTask);

            //对电桩升级做处理
            List<String> pileTypeIds = Arrays.asList("29", "30");
            if (pileTypeIds.contains(taskChangeVo.getTypeId())) {
                //添加设备升级任务记录
                List<String> deviceIds = JSON.parseArray(taskChangeVo.getDeviceIds(), String.class);
                List<DeviceEntity> deviceList = deviceDao.findAllById(deviceIds).stream().filter(d -> StringUtil.isNotEmpty(d.getDeviceNumber()))
                        .collect(Collectors.toList());
                //根据多个站点id查询站点名称
                Set<String> siteIds = deviceList.stream().map(DeviceEntity::getSiteId).collect(Collectors.toSet());
                Map<String, String> siteNameMap = siteInfoDao.findAllById(siteIds).stream().collect(Collectors.toMap(SiteInfoEntity::getId, SiteInfoEntity::getSiteName));
                List<DeviceTaskRecordEntity> deviceTaskRecordList = deviceDao.findAllById(deviceIds).stream().filter(d -> StringUtil.isNotEmpty(d.getDeviceNumber())).map(device -> {
                    DeviceTaskRecordEntity taskRecord = new DeviceTaskRecordEntity();
                    taskRecord.setTaskId(save.getId());
                    if (siteNameMap.containsKey(device.getSiteId())) {
                        taskRecord.setSiteName(siteNameMap.get(device.getSiteId()));
                    }
                    taskRecord.setDeviceName(device.getDeviceName());
                    taskRecord.setDeviceNumber(device.getDeviceNumber());
                    PileRealModel pileRealModel = RedisGeneralUtil.getPileRealModel(device.getDeviceNumber());
                    if (pileRealModel != null && pileRealModel.getCtrlBoardInfoMap().containsKey(taskChangeVo.getFirmwareType())) {
                        CtrlBoardInfo ctrlBoardInfo = pileRealModel.getCtrlBoardInfoMap().get(taskChangeVo.getFirmwareType());
                        taskRecord.setSourceVersion(ctrlBoardInfo.getFirmMainVCode() + FileUtil.POINT + ctrlBoardInfo.getFirmSecVCode());
                    }
                    taskRecord.setTargetVersion(firmware.getFirmwareVersion());
                    taskRecord.setStatus(1);
                    //校验是否是同版本
                    if (StringUtil.isNotEmpty(taskRecord.getSourceVersion()) && Objects.equals(taskRecord.getSourceVersion(), taskRecord.getTargetVersion())) {
                        taskRecord.setStatus(0);
                        taskRecord.setReason("同版本不需要升级");
                    }
                    taskRecord.setCreateTime(LocalDateTime.now());
                    return taskRecord;
                }).collect(Collectors.toList());
                deviceTaskRecordDao.saveAll(deviceTaskRecordList);

                //批量对多个充电桩升级
                Set<String> deviceNumbers = deviceTaskRecordList.stream().filter(d -> d.getStatus() == 1).map(DeviceTaskRecordEntity::getDeviceNumber).collect(Collectors.toSet());
                if (CollectionUtils.isNotEmpty(deviceNumbers)) {
                    PileBatchUpdateVo pileBatchUpdateVos = new PileBatchUpdateVo();
                    pileBatchUpdateVos.setTaskId(save.getId());
                    pileBatchUpdateVos.setPileCodes(deviceNumbers);
                    pileBatchUpdateVos.setUpgradeType(1);
                    pileBatchUpdateVos.setFirmwarePath(firmware.getFirmwarePath());

                    if (StringUtil.isNotEmpty(firmwareParse.getHardwareVersion())) {
                        String[] split = firmwareParse.getHardwareVersion().split(FileUtil.SLASH_POINT);
                        pileBatchUpdateVos.setHardwareMajorVersion(Integer.parseInt(split[0]));
                        pileBatchUpdateVos.setHardwareMinorVersion(Integer.parseInt(split[1]));
                    }
                    pileBatchUpdateVos.setFirmwareType(firmware.getFirmwareType());
                    if (StringUtil.isNotEmpty(firmwareParse.getFirmwareVersion())) {
                        String[] split = firmwareParse.getFirmwareVersion().split(FileUtil.SLASH_POINT);
                        pileBatchUpdateVos.setFirmwareMajorVersion(Integer.parseInt(split[0]));
                        pileBatchUpdateVos.setFirmwareMinorVersion(Integer.parseInt(split[1]));
                    }
                    pileBatchUpdateVos.setFirmwareInternalVersion(firmwareParse.getFirmwareInternalVersion());
                    pileBatchUpdateVos.setFirmwareCompileTime(firmwareParse.getFirmwareCompileTime());
                    pileBatchUpdateVos.setFirmwareSize(firmwareParse.getFirmwareSize());
                    pileBatchUpdateVos.setCrc32(firmwareParse.getCrc32());
                    protocolService.batchPileUpdate(pileBatchUpdateVos);
                    return ResponseResult.ok();
                } else {
                    save.setTaskStatus(3);
                    deviceTaskDao.save(save);
                    return ResponseResult.ok();
                }
            }
            //TODO 网关暂时不开发
        }
        return ResponseResult.paramError(ResponseResult.PARAM_ERROR);
    }

    @Override
    public ResponseResult<PageDto<DeviceTaskListDto>> queryDeviceTaskList(DeviceTaskQueryVo taskQueryVo) {
        //返回的集合
        List<DeviceTaskListDto> resultList = Lists.newArrayList();
        //根据条件查询出设备数据
        List<DeviceTaskEntity> deviceTaskList = deviceTaskDao.findAll((Specification<DeviceTaskEntity>) (root, cq, cb) -> {
            List<Predicate> predicates = Lists.newArrayList();
            if (StringUtil.isNotEmpty(taskQueryVo.getKeyword())) {
                predicates.add(cb.or(cb.like(root.get("taskName"), "%" + taskQueryVo.getKeyword() + "%"),
                        cb.like(root.get("targetVersion"), "%" + taskQueryVo.getKeyword() + "%")));
            }
            if (StringUtil.isNotEmpty(taskQueryVo.getStartTime()) && StringUtil.isNotEmpty(taskQueryVo.getEndTime())) {
                LocalDateTime startTime = DateUtil.strToLocalDateTime(taskQueryVo.getStartTime());
                LocalDateTime endTime = DateUtil.strToLocalDateTime(taskQueryVo.getEndTime());
                predicates.add(cb.between(root.get("createTime"), startTime, endTime));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        }, Sort.by("createTime").descending());
        if (CollectionUtils.isNotEmpty(deviceTaskList)) {
            //根据查询条件查询任务记录数据
            List<String> ids = deviceTaskList.stream().map(DeviceTaskEntity::getId).collect(Collectors.toList());
            List<DeviceTaskRecordEntity> deviceTaskRecordList = deviceTaskRecordDao.findAll((Specification<DeviceTaskRecordEntity>) (root, cq, cb) -> {
                List<Predicate> predicates = Lists.newArrayList();
                predicates.add(cb.in(root.get("taskId")).value(ids));
                if (StringUtil.isNotEmpty(taskQueryVo.getSiteName())) { //站点名称
                    predicates.add(cb.like(root.get("siteName"), "%" + taskQueryVo.getSiteName() + "%"));
                }
                if (StringUtil.isNotEmpty(taskQueryVo.getDeviceNumber())) { //设备序列号
                    predicates.add(cb.like(root.get("deviceNumber"), "%" + taskQueryVo.getDeviceNumber() + "%"));
                }
                return cb.and(predicates.toArray(new Predicate[0]));
            });
            Set<String> taskIds = deviceTaskRecordList.stream().map(DeviceTaskRecordEntity::getTaskId).collect(Collectors.toSet());
            if (CollectionUtils.isNotEmpty(taskIds)) {
                deviceTaskList = deviceTaskList.stream().filter(deviceTask -> taskIds.contains(deviceTask.getId())).collect(Collectors.toList());

                //根据多个创建人id查询创建人名称
                List<String> createIds = deviceTaskList.stream().map(DeviceTaskEntity::getCreateId).distinct().collect(Collectors.toList());
                Map<String, UserDto> userNameMap = systemService.findUserInfoByIdsFeign(createIds).getData();

                //根据多个设备类型id查询设备类型名称
                List<String> typeIds = deviceTaskList.stream().map(DeviceTaskEntity::getTypeId).distinct().collect(Collectors.toList());
                Map<String, String> typeNameMap = assetTypeDao.findAllById(typeIds).stream().collect(Collectors.toMap(AssetTypeEntity::getId, AssetTypeEntity::getTypeName));

                resultList = deviceTaskList.stream().filter(deviceTask -> taskIds.contains(deviceTask.getId())).map(deviceTask -> {
                    DeviceTaskListDto result = new DeviceTaskListDto();
                    BeanUtils.copyProperties(deviceTask, result);
                    if (userNameMap.containsKey(deviceTask.getCreateId())) {
                        result.setCreateName(userNameMap.get(deviceTask.getCreateId()).getFullName());
                    }
                    if (typeNameMap.containsKey(deviceTask.getTypeId())) {
                        result.setTypeName(typeNameMap.get(deviceTask.getTypeId()));
                    }
                    return result;
                }).collect(Collectors.toList());
            }
        }
        return ResponseResult.ok(new PageDto<>(resultList, taskQueryVo.getPage(), taskQueryVo.getSize()));
    }

    @Override
    public ResponseResult<List<DeviceTaskRecordListDto>> queryDeviceTaskRecordList(DeviceTaskRecordVo taskRecordVo) {
        //返回的集合
        List<DeviceTaskRecordListDto> resultList = Lists.newArrayList();

        List<DeviceTaskRecordEntity> deviceTaskRecordList = deviceTaskRecordDao.findAll((Specification<DeviceTaskRecordEntity>) (root, cq, cb) -> {
            List<Predicate> predicates = Lists.newArrayList();
            predicates.add(cb.equal(root.get("taskId"), taskRecordVo.getTaskId()));
            if (StringUtil.isNotEmpty(taskRecordVo.getSiteName())) { //站点名称
                predicates.add(cb.like(root.get("siteName"), "%" + taskRecordVo.getSiteName() + "%"));
            }
            if (StringUtil.isNotEmpty(taskRecordVo.getDeviceName())) { //设备名称
                predicates.add(cb.like(root.get("deviceName"), "%" + taskRecordVo.getDeviceName() + "%"));
            }
            if (StringUtil.isNotEmpty(taskRecordVo.getDeviceNumber())) { //设备序列号
                predicates.add(cb.like(root.get("deviceNumber"), "%" + taskRecordVo.getDeviceNumber() + "%"));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        });
        if (CollectionUtils.isNotEmpty(deviceTaskRecordList)) {
            resultList = deviceTaskRecordList.stream().map(deviceTaskRecord -> {
                DeviceTaskRecordListDto result = new DeviceTaskRecordListDto();
                BeanUtils.copyProperties(deviceTaskRecord, result);
                return result;
            }).collect(Collectors.toList());
        }
        return ResponseResult.ok(resultList);
    }

    @Override
    @Transactional(transactionManager = DBConstants.JPA_TX_ACCESS, rollbackFor = Exception.class)
    public ResponseResult<Void> deleteDeviceTaskById(String id, Integer type) {
        //校验参数
        if (StringUtil.isEmpty(id) || StringUtil.isEmpty(type)) {
            return ResponseResult.paramError(ResponseResult.PARAM_ERROR);
        }
        //任务类型 1-设备升级任务 2-设备升级记录
        if (type == 1) {
            Optional<DeviceTaskEntity> optional = deviceTaskDao.findById(id);
            if (optional.isPresent()) {
                DeviceTaskEntity deviceTask = optional.get();
                deviceTaskDao.delete(deviceTask);
                List<DeviceTaskRecordEntity> deviceTaskRecordList = deviceTaskRecordDao.findAll(Example.of(DeviceTaskRecordEntity.builder().taskId(id).build()));
                if (CollectionUtils.isNotEmpty(deviceTaskRecordList)) {
                    deviceTaskRecordDao.deleteAll(deviceTaskRecordList);
                }
            }
        }
        if (type == 2) {
            Optional<DeviceTaskRecordEntity> optional = deviceTaskRecordDao.findById(id);
            if (optional.isPresent()) {
                DeviceTaskRecordEntity deviceTaskRecord = optional.get();
                deviceTaskRecordDao.delete(deviceTaskRecord);
            }
        }
        return ResponseResult.ok();
    }

    @Override
    public ResponseResult<Void> batchUpdateDeviceTask(DeviceBatchUpdateVo deviceUpdateVo) {
        if (MapUtils.isNotEmpty(deviceUpdateVo.getDeviceUpdateInfoMap())) {
            List<Integer> statusList = Arrays.asList(0, 3, 5);
            //批量更新设备任务数据
            if (StringUtil.isNotEmpty(deviceUpdateVo.getTaskStatus())) {
                Set<String> taskIds = deviceUpdateVo.getDeviceUpdateInfoMap().keySet();
                //查询进行种的设备升级任务数据
                List<DeviceTaskEntity> deviceTaskList = deviceTaskDao.findAllById(taskIds).stream().filter(d -> StringUtil.isEmpty(d.getTaskStatus())
                        || d.getTaskStatus() != 3).collect(Collectors.toList());
                if (CollectionUtils.isNotEmpty(deviceTaskList)) {
                    deviceTaskDao.saveAll(deviceTaskList.stream().peek(deviceTask -> deviceTask.setTaskStatus(deviceUpdateVo.getTaskStatus())).collect(Collectors.toList()));
                }
            }
            //批量更新设备任务记录数据
            deviceUpdateVo.getDeviceUpdateInfoMap().forEach((key, value) -> {
                Map<String, DeviceBatchUpdateVo.DeviceUpdateInfo> deviceUpdateInfoMap = value.stream().collect(Collectors.toMap(DeviceBatchUpdateVo
                        .DeviceUpdateInfo::getDeviceCode, a -> a, (v1, v2) -> v1));
                List<DeviceTaskRecordEntity> deviceTaskRecordList = deviceTaskRecordDao.findAllByTaskIdAndDeviceNumberIn(key, deviceUpdateInfoMap.keySet());
                if (CollectionUtils.isNotEmpty(deviceTaskRecordList)) {
                    deviceTaskRecordDao.saveAll(deviceTaskRecordList.stream().peek(deviceTaskRecord -> {
                        if (StringUtil.isNotEmpty(deviceUpdateVo.getUpgradeTime())) {
                            deviceTaskRecord.setUpgradeTime(DateUtil.strToLocalDateTime(deviceUpdateVo.getUpgradeTime()));
                        }
                        //校验升级状态是否已结束
                        if (StringUtil.isEmpty(deviceTaskRecord.getStatus()) || !statusList.contains(deviceTaskRecord.getStatus())) {
                            if (StringUtil.isNotEmpty(deviceUpdateVo.getEndTime())) {
                                deviceTaskRecord.setEndTime(DateUtil.strToLocalDateTime(deviceUpdateVo.getEndTime()));
                            }
                            if (deviceUpdateInfoMap.containsKey(deviceTaskRecord.getDeviceNumber())) {
                                DeviceBatchUpdateVo.DeviceUpdateInfo deviceUpdateInfo = deviceUpdateInfoMap.get(deviceTaskRecord.getDeviceNumber());
                                if (StringUtil.isNotEmpty(deviceUpdateInfo.getStatus())) {
                                    deviceTaskRecord.setStatus(deviceUpdateInfo.getStatus());
                                }
                                if (StringUtil.isEmpty(deviceTaskRecord.getReason()) && StringUtil.isNotEmpty(deviceUpdateInfo.getFailReason()) && deviceUpdateInfo.getFailReason() != 0) {
                                    //失败原因 0-成功 1-数据校验失败 2-应答超时 3-flash擦除失败 4-flash写入失败 5-同版本不升级 255-其他原因 256-控制板信息上报不一致
                                    switch (deviceUpdateInfo.getFailReason()) {
                                        case 1:
                                            deviceTaskRecord.setReason("数据校验失败");
                                            break;
                                        case 2:
                                            deviceTaskRecord.setReason("应答超时");
                                            break;
                                        case 3:
                                            deviceTaskRecord.setReason("flash擦除失败");
                                            break;
                                        case 4:
                                            deviceTaskRecord.setReason("flash写入失败");
                                            break;
                                        case 5:
                                            deviceTaskRecord.setReason("同版本不升级");
                                            break;
                                        case 255:
                                            deviceTaskRecord.setReason("其他原因");
                                            break;
                                        case 256:
                                            deviceTaskRecord.setReason("控制板信息上报不一致");
                                            break;
                                    }
                                }
                            }
                        }
                    }).collect(Collectors.toList()));
                }
            });
        }
        return ResponseResult.ok();
    }

}
