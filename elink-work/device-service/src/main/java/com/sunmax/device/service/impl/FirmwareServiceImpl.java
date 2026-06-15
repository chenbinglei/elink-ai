package com.sunmax.device.service.impl;

import com.alibaba.fastjson2.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.sunmax.common.dto.PageDto;
import com.sunmax.common.dto.system.UserDto;
import com.sunmax.common.util.DateUtil;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.util.StringUtil;
import com.sunmax.common.util.SunMaxUtil;
import com.sunmax.common.util.oss.FileUtil;
import com.sunmax.common.vo.statics.DeviceParamVo;
import com.sunmax.device.constants.DBConstants;
import com.sunmax.device.dao.access.DeviceDao;
import com.sunmax.device.dao.access.FirmwareDao;
import com.sunmax.device.dao.model.AssetTypeDao;
import com.sunmax.device.dto.firmware.FirmwareDto;
import com.sunmax.device.dto.firmware.FirmwareParseDto;
import com.sunmax.device.entity.access.DeviceEntity;
import com.sunmax.device.entity.access.FirmwareEntity;
import com.sunmax.device.entity.model.AssetTypeEntity;
import com.sunmax.device.service.FirmwareService;
import com.sunmax.device.service.feign.SystemService;
import com.sunmax.device.vo.firmware.FirmwareChangeVo;
import com.sunmax.device.vo.firmware.FirmwareQueryVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import jakarta.persistence.criteria.Predicate;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FirmwareServiceImpl implements FirmwareService {

    @Autowired
    private FirmwareDao firmwareDao;

    @Autowired
    private SystemService systemService;

    @Autowired
    private DeviceDao deviceDao;

    @Autowired
    private AssetTypeDao assetTypeDao;

    @Override
    @Transactional(transactionManager = DBConstants.JPA_TX_ACCESS, rollbackFor = Exception.class)
    public ResponseResult<Void> uploadOrEditFirmware(FirmwareChangeVo firmwareVo, MultipartFile file) {
        //根据类型id和固件包类型和版本号查询数据库是否存在
        List<FirmwareEntity> firmwareList = firmwareDao.findAll(Example.of(FirmwareEntity.builder().typeId(firmwareVo.getTypeId())
                .firmwareType(firmwareVo.getFirmwareType()).firmwareVersion(firmwareVo.getFirmwareVersion()).build()));
        //新增固件包数据
        if (StringUtil.isEmpty(firmwareVo.getId())) {
            if (CollectionUtils.isNotEmpty(firmwareList)) {
                return ResponseResult.paramShow("固件包同版本号已存在,请重新上传");
            }
            FirmwareEntity firmwareEntity = FirmwareEntity.builder()
                    .typeId(firmwareVo.getTypeId())
                    .equipmentModels(firmwareVo.getEquipmentModels()) //设备模型
                    .firmwareName(firmwareVo.getFirmwareName()) //固件包名称
                    .firmwarePath(FileUtil.getFilePath(file, null)) //固件包路径
                    .firmwareType(firmwareVo.getFirmwareType()) //固件类型
                    .firmwareVersion(firmwareVo.getFirmwareVersion()) //固件版本号
                    .firmwareSize(file.getSize()) //固件包文件大小
                    .firmwareDesc(firmwareVo.getFirmwareDesc()) //固件包描述
                    .build();
            firmwareEntity.setCreateId(firmwareVo.getUserId());
            firmwareEntity.setUpdateId(firmwareVo.getUserId());
            firmwareDao.save(firmwareEntity);
            return ResponseResult.ok();
        } else {
            //2.编辑固件包数据
            firmwareList = firmwareList.stream().filter(firmware -> !firmware.getId().equals(firmwareVo.getId())).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(firmwareList)) {
                return ResponseResult.paramShow("固件包同版本号已存在,请重新上传");
            }
            //根据id查询固件包数据
            Optional<FirmwareEntity> optional = firmwareDao.findById(firmwareVo.getId());
            if (optional.isPresent()) {
                FirmwareEntity firmwareEntity = optional.get();
                //2.1 校对新的固件包版本号
                if (Objects.nonNull(file) && !file.isEmpty()) {
                    //上传新的固件包
                    firmwareEntity = FirmwareEntity.builder()
                            .typeId(firmwareVo.getTypeId())
                            .equipmentModels(firmwareVo.getEquipmentModels()) //设备型号
                            .firmwareName(firmwareVo.getFirmwareName()) //固件包名称
                            .firmwarePath(FileUtil.getFilePath(file, optional.get().getFirmwarePath())) //固件包路径
                            .firmwareType(firmwareVo.getFirmwareType()) //固件类型
                            .firmwareVersion(firmwareVo.getFirmwareVersion()) //固件版本号
                            .firmwareSize(file.getSize()) //固件数据大小
                            .firmwareDesc(firmwareVo.getFirmwareDesc()) //固件包描述
                            .build();
                    firmwareEntity.setId(firmwareVo.getId());
                    firmwareEntity.setCreateId(optional.get().getCreateId());
                    firmwareEntity.setCreateTime(optional.get().getCreateTime());//创建时间
                } else {
                    firmwareEntity.setEquipmentModels(firmwareVo.getEquipmentModels()); //设备型号
                    firmwareEntity.setFirmwareName(firmwareVo.getFirmwareName()); //固件包名称
                    firmwareEntity.setFirmwareType(firmwareVo.getFirmwareType()); //固件包类型
                    firmwareEntity.setFirmwareVersion(firmwareVo.getFirmwareVersion()); //固件包版本
                    firmwareEntity.setFirmwareDesc(firmwareVo.getFirmwareDesc()); //固件包描述
                }
                firmwareEntity.setUpdateId(firmwareVo.getUserId());
                firmwareDao.save(firmwareEntity);
                return ResponseResult.ok();
            }
        }
        return ResponseResult.error(ResponseResult.PARAM_ERROR);
    }

    @Override
    public ResponseResult<Set<String>> getEquipmentModelList(String typeId) {
        //返回的集合
        Set<String> resultSet = Sets.newHashSet();
        List<DeviceEntity> deviceList = deviceDao.findAll(Example.of(DeviceEntity.builder().typeId(typeId).isDelete(1).build()));
        if (CollectionUtils.isNotEmpty(deviceList)) {
            deviceList.forEach(deviceEntity -> {
                //获取读写map数据
                if (StringUtil.isNotEmpty(deviceEntity.getReadwriteObject())) {
                    JSONObject readwriteObject = JSONObject.parseObject(deviceEntity.getReadwriteObject());
                    if (readwriteObject.containsKey(DeviceParamVo.EQUIPMENT_MODEL)) {
                        resultSet.add(readwriteObject.getString(DeviceParamVo.EQUIPMENT_MODEL));
                    }
                }
            });
        }
        return ResponseResult.ok(resultSet);
    }

    @Override
    public ResponseResult<PageDto<FirmwareDto>> queryFirmwareList(FirmwareQueryVo firmwareVo) {
        //返回的集合
        List<FirmwareDto> resultList = new ArrayList<>();

        if (Objects.nonNull(firmwareVo)) {
            List<FirmwareEntity> firmwareList = firmwareDao.findAll(
                    (Specification<FirmwareEntity>) (root, cq, cb) -> {
                        List<Predicate> list = new ArrayList<>();
                        if (!StringUtil.isEmpty(firmwareVo.getKeyword())) { //关键词
                            list.add(cb.or(cb.like(root.get("firmwareName"), "%" + firmwareVo.getKeyword() + "%"),
                                    cb.like(root.get("firmwareVersion"), "%" + firmwareVo.getKeyword() + "%"),
                                    cb.like(root.get("equipmentModel"), "%" + firmwareVo.getKeyword() + "%")));
                        }
                        if (!StringUtil.isEmpty(firmwareVo.getTypeId())) { //类型id
                            list.add(cb.equal(root.get("typeId"), firmwareVo.getTypeId()));
                        }
                        return cb.and(list.toArray(new Predicate[0]));
                    }, Sort.by("createTime").descending());
            resultList = getFirmware(resultList, firmwareList, systemService, assetTypeDao);
        }
        return ResponseResult.ok(new PageDto<>(resultList, firmwareVo.getPage(), firmwareVo.getSize()));
    }

    public static List<FirmwareDto> getFirmware(List<FirmwareDto> resultList, List<FirmwareEntity> firmwareList, SystemService systemService, AssetTypeDao assetTypeDao) {
        if (CollectionUtils.isNotEmpty(firmwareList)) {
            //根据所有创建人id和修改人id查询用户名称
            List<String> userIds = Lists.newArrayList();
            userIds.addAll(firmwareList.stream().map(FirmwareEntity::getCreateId).filter(StringUtil::isNotEmpty).collect(Collectors.toList()));
            userIds.addAll(firmwareList.stream().map(FirmwareEntity::getUpdateId).filter(StringUtil::isNotEmpty).collect(Collectors.toList()));
            Map<String, UserDto> userNameMap = systemService.findUserInfoByIdsFeign(userIds.stream().distinct().collect(Collectors.toList())).getData();
            //根据多个设备类型id查询类型名称
            Set<String> typeIds = firmwareList.stream().map(FirmwareEntity::getTypeId).collect(Collectors.toSet());
            Map<String, String> assetTypeMap = assetTypeDao.findAllById(typeIds).stream().collect(Collectors.toMap(AssetTypeEntity::getId,
                    AssetTypeEntity::getTypeName));
            resultList = firmwareList.stream().map(f -> {
                FirmwareDto result = new FirmwareDto();
                BeanUtils.copyProperties(f, result);
                if (StringUtil.isNotEmpty(userNameMap.get(f.getCreateId()))) {
                    result.setCreateName(userNameMap.get(f.getCreateId()).getFullName());//创建人名称
                }
                if (StringUtil.isNotEmpty(userNameMap.get(f.getUpdateId()))) {
                    result.setUpdateName(userNameMap.get(f.getUpdateId()).getFullName());//修改人名称
                }
                if (f.getFirmwareSize() != null) {
                    //获取固件数据大小 转换展示
                    double firmwareSize = BigDecimal.valueOf((float) f.getFirmwareSize() / 1024).setScale(5, RoundingMode.HALF_UP).doubleValue();
                    if (firmwareSize < 1024) {
                        result.setFirmwareSizeShow(new BigDecimal(firmwareSize).setScale(2, RoundingMode.HALF_UP).doubleValue() + "KB");
                    } else {
                        result.setFirmwareSizeShow(new BigDecimal(firmwareSize / 1024).setScale(2, RoundingMode.HALF_UP).doubleValue() + "MB");
                    }
                }
                if (assetTypeMap.containsKey(f.getTypeId())) {
                    result.setTypeName(assetTypeMap.get(f.getTypeId()));
                }
                return result;
            }).collect(Collectors.toList());
        }
        return resultList;
    }

    //解析电桩固件包数据
    @Override
    public ResponseResult<FirmwareParseDto> parseFirmwareData(MultipartFile file) {
        try {
            FirmwareParseDto result = new FirmwareParseDto();
            if (Objects.nonNull(file) && !file.isEmpty()) {
                //获取概要信息
                result = parseFirmwareData(SunMaxUtil.toHexString(file.getBytes()));
            }
            return ResponseResult.ok(result);
        } catch (Exception e) {
            log.error("解析文件失败", e);
            return ResponseResult.paramError(ResponseResult.PARAM_PARSE_ERROR);
        }
    }

    public static FirmwareParseDto parseFirmwareData(String content) {
        FirmwareParseDto result = null;
        //获取概要信息
        if (StringUtil.isNotEmpty(content) && content.length() >= 64) {
            result = new FirmwareParseDto();
            String summaryStr = content.substring(0, 64);
            result.setHardwareVersion(Integer.parseInt(summaryStr.substring(4, 6), 16) + FileUtil.POINT + Integer.parseInt(summaryStr.substring(6, 8), 16));//硬件版本号
            result.setFirmwareType(Integer.parseInt(summaryStr.substring(8, 10), 16));//固件类型
            result.setFirmwareVersion(Integer.parseInt(summaryStr.substring(10, 12), 16) + FileUtil.POINT + Integer.parseInt(summaryStr.substring(12, 14), 16)); //固件版本号
            result.setFirmwareInternalVersion(Integer.parseInt(summaryStr.substring(14, 16), 16));//固件内测版本号
            //获取十六进制的固件编译时间
            String compileTime16 = summaryStr.substring(22, 24) + summaryStr.substring(20, 22) + summaryStr.substring(18, 20) + summaryStr.substring(16, 18);
            //转换成十进制的固件编译时间 再转成日期
            result.setFirmwareCompileTime(DateUtil.strToLocalDateTime(SunMaxUtil.timeStamp8Date(Integer.parseInt(compileTime16, 16))));//编译时间
            //获取十六进制的固件数据大小
            String size16 = summaryStr.substring(30, 32) + summaryStr.substring(28, 30) + summaryStr.substring(26, 28) + summaryStr.substring(24, 26);
            result.setFirmwareSize(Integer.parseInt(size16, 16));//转成十进制的 固件数据大小
            //获取十六进制的固件数据校验码
            String crc16 = summaryStr.substring(38, 40) + summaryStr.substring(36, 38) + summaryStr.substring(34, 36) + summaryStr.substring(32, 34);
            result.setCrc32(Long.parseLong(crc16, 16));//固件数据校验码 十六进制的
            //获取十六进制的保留12位字节
            result.setReserve(summaryStr.substring(40, 64));
        }
        return result;
    }

    @Override
    @Transactional(transactionManager = DBConstants.JPA_TX_ACCESS, rollbackFor = Exception.class)
    public ResponseResult<Void> deleteFirmwareById(String id) {
        //根据id查询固件包数据
        Optional<FirmwareEntity> optional = firmwareDao.findById(id);
        if (optional.isPresent()) {
            FirmwareEntity firmware = optional.get();
            if (StringUtil.isNotEmpty(firmware.getFirmwarePath())) {
                FileUtil.deleteFile(firmware.getFirmwarePath());
            }
            firmwareDao.delete(firmware);
            return ResponseResult.ok();
        }
        return ResponseResult.paramError(ResponseResult.PARAM_ERROR);
    }

}
