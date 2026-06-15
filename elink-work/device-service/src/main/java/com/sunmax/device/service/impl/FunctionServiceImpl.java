package com.sunmax.device.service.impl;

import com.sunmax.common.config.redis.KeyUtil;
import com.sunmax.common.config.redis.RedisLockUtil;
import com.sunmax.common.config.redis.RedisUtil;
import com.sunmax.common.dto.PageDto;
import com.sunmax.common.enums.GeneralFieldEnum;
import com.sunmax.common.model.DeviceModel;
import com.sunmax.common.util.JsonUtil;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.util.StringUtil;
import com.sunmax.device.constants.DBConstants;
import com.sunmax.device.dao.access.DeviceDao;
import com.sunmax.device.dao.model.FunctionDao;
import com.sunmax.device.dao.model.ModelFunctionDao;
import com.sunmax.common.dto.device.FunctionDetailDto;
import com.sunmax.device.dto.model.FunctionListDto;
import com.sunmax.device.dto.model.PileRealFieldDto;
import com.sunmax.device.entity.access.DeviceEntity;
import com.sunmax.device.entity.model.FunctionEntity;
import com.sunmax.device.entity.model.ModelFunctionEntity;
import com.sunmax.device.service.FunctionService;
import com.sunmax.device.vo.model.FunctionChangeVo;
import com.sunmax.device.vo.model.FunctionQueryVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.compress.utils.Lists;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.criteria.Predicate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FunctionServiceImpl implements FunctionService {

    @Autowired
    private FunctionDao functionDao;

    @Autowired
    private ModelFunctionDao modelFunctionDao;

    @Autowired
    private DeviceDao deviceDao;

    @Override
    @Transactional(transactionManager = DBConstants.JPA_TX_MODEL, rollbackFor = Exception.class)
    public ResponseResult<Void> saveFunction(FunctionChangeVo functionChangeVo) {
        //查询标准功能数据 校验
        List<FunctionEntity> functionNameList = functionDao.findAll(Example.of(FunctionEntity.builder().typeId(functionChangeVo.getTypeId()).functionName(functionChangeVo.getFunctionName()).isDelete(1).build()));
        List<FunctionEntity> functionLogoList = functionDao.findAll(Example.of(FunctionEntity.builder().typeId(functionChangeVo.getTypeId()).functionLogo(functionChangeVo.getFunctionLogo()).isDelete(1).build()));
        //新增模型标准功能
        if (StringUtil.isEmpty(functionChangeVo.getId())) {
            if (CollectionUtils.isNotEmpty(functionNameList)) {
                return ResponseResult.paramShow(functionChangeVo.getFunctionName(), ResponseResult.PARAM_EXIST);
            }
            if (CollectionUtils.isNotEmpty(functionLogoList)) {
                return ResponseResult.paramShow(functionChangeVo.getFunctionLogo(), ResponseResult.PARAM_EXIST);
            }

            FunctionEntity functionEntity = new FunctionEntity();
            BeanUtils.copyProperties(functionChangeVo, functionEntity);
            functionEntity.setIsDelete(1);
            functionDao.save(functionEntity);
            return ResponseResult.ok();
        } else { //编辑模型标准功能
            //校验编辑中的数据
            functionNameList = functionNameList.stream().filter(s -> !Objects.equals(s.getId(), functionChangeVo.getId())).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(functionNameList)) {
                return ResponseResult.paramShow(functionChangeVo.getFunctionName(), ResponseResult.PARAM_EXIST);
            }
            functionLogoList = functionLogoList.stream().filter(s -> !Objects.equals(s.getId(), functionChangeVo.getId())).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(functionLogoList)) {
                return ResponseResult.paramShow(functionChangeVo.getFunctionLogo(), ResponseResult.PARAM_EXIST);
            }

            Optional<FunctionEntity> optional = functionDao.findById(functionChangeVo.getId());
            if (optional.isPresent()) {
                FunctionEntity functionEntity = new FunctionEntity();
                BeanUtils.copyProperties(functionChangeVo, functionEntity);
                functionEntity.setIsDelete(optional.get().getIsDelete());
                functionDao.save(functionEntity);
                return ResponseResult.ok();
            }
        }
        return ResponseResult.paramError(ResponseResult.PARAM_ERROR);
    }

    @Override
    public ResponseResult<PageDto<FunctionListDto>> queryFunctionList(FunctionQueryVo functionQueryVo) {
        Page<FunctionEntity> functionPage = functionDao.findAll((Specification<FunctionEntity>) (root, cq, cb) -> {
            List<Predicate> list = new ArrayList<>();
            list.add(cb.equal(root.get("typeId"), functionQueryVo.getTypeId())); //资产分类id
            list.add(cb.equal(root.get("isDelete"), 1)); //分类id
            if (StringUtil.isNotEmpty(functionQueryVo.getKeyword())) { //关键字 功能名称+标识符
                list.add(cb.or(cb.like(root.get("functionName"), "%" + functionQueryVo.getKeyword() + "%"),
                        cb.like(root.get("functionLogo"), "%" + functionQueryVo.getKeyword() + "%")));
            }
            if (StringUtil.isNotEmpty(functionQueryVo.getFunctionType())) { //功能类型 1-遥测 2-遥信 3-遥脉 4-遥控 5-遥调
                list.add(cb.equal(root.get("functionType"), functionQueryVo.getFunctionType()));
            }
            return cb.and(list.toArray(new Predicate[0]));
        }, PageRequest.of(functionQueryVo.getPage() - 1, functionQueryVo.getSize(), Sort.by("createTime").descending()));
        List<FunctionListDto> resultList = functionPage.getContent().stream().map(r -> {
            FunctionListDto result = new FunctionListDto();
            BeanUtils.copyProperties(r, result);
            return result;
        }).collect(Collectors.toList());
        return ResponseResult.ok(new PageDto<>(resultList, functionPage.getNumber() + 1, functionPage.getSize(), (int)functionPage.getTotalElements()));
    }

    @Override
    @Transactional(transactionManager = DBConstants.JPA_TX_MODEL, rollbackFor = Exception.class)
    public ResponseResult<Void> deleteFunctionById(String id, Boolean deleteLogo) {
        Optional<FunctionEntity> optional = functionDao.findById(id);
        if (optional.isPresent()) {
            FunctionEntity function = optional.get();
            if (deleteLogo) {
                //删除功能点数据
                functionDao.delete(function);

                //删除功能点关联模型以及模型下面设备的数据
                List<ModelFunctionEntity> modelFunctionList = modelFunctionDao.findAllByFunctionIdAndIsDelete(id, 1);
                if (CollectionUtils.isNotEmpty(modelFunctionList)) {
                    modelFunctionDao.deleteInBatch(modelFunctionList);

                    //删除设备已绑定功能点
                    Set<String> modelIds = modelFunctionList.stream().map(ModelFunctionEntity::getModelId).filter(StringUtil::isNotEmpty).collect(Collectors.toSet());
                    List<DeviceEntity> deviceList = deviceDao.findAllByModelIdInAndIsDelete(modelIds, 1).stream()
                            .filter(s -> StringUtil.isNotEmpty(s.getDeviceNumber())).collect(Collectors.toList());
                    if (CollectionUtils.isNotEmpty(deviceList)) {
                        deviceList.forEach(device -> {
                            //校验redis是否存在 不存在则添加
                            String lockKey = KeyUtil.LOCK_KEY + device.getDeviceNumber();
                            try {
                                boolean lock = RedisLockUtil.lock(lockKey, KeyUtil.WAIT_TIME);
                                if (lock) {
                                    String deviceKey = KeyUtil.DEVICE_KEY + device.getDeviceNumber();
                                    if (RedisUtil.hasKey(deviceKey)) {
                                        DeviceModel deviceModel = JsonUtil.objectToEntity(RedisUtil.get(deviceKey), DeviceModel.class);
                                        //删除缓存不存在的key
                                        if (MapUtils.isNotEmpty(deviceModel.getFunctionMap())) {
                                            deviceModel.getFunctionMap().forEach((key, value) -> {
                                                if (Objects.equals(key, function.getFunctionLogo())) {
                                                    deviceModel.getFunctionMap().remove(key);
                                                }
                                            });
                                        }
                                        RedisUtil.set(deviceKey, deviceModel);
                                    }
                                }
                            } catch (Exception e) {
                                log.error("更新redis里设备功能点数据报错", e);
                            } finally {
                                RedisLockUtil.unlock(lockKey);
                            }
                        });
                    }
                }
            } else {
                function.setIsDelete(2);
                functionDao.save(function);
                modelFunctionDao.saveAll(modelFunctionDao.findAllByFunctionIdAndIsDelete(id, 1).stream().peek(s -> s.setIsDelete(2)).collect(Collectors.toList()));
            }
            return ResponseResult.ok();
        }
        return ResponseResult.paramError(ResponseResult.PARAM_ERROR);
    }

    @Override
    public ResponseResult<FunctionDetailDto> findFunctionDetailById(String id) {
        //返回的对象
        FunctionDetailDto result = new FunctionDetailDto();
        functionDao.findById(id).ifPresent(function -> BeanUtils.copyProperties(function, result));
        return ResponseResult.ok(result);
    }

    @Override
    public ResponseResult<List<PileRealFieldDto>> getPileRealFieldList(Integer dataType) {
        //返回的集合
        List<PileRealFieldDto> resultList = Lists.newArrayList();
        //充电桩协议字段数据
        List<Integer> deviceTypes = Arrays.asList(1, 2, 3, 4, 5);
        if (deviceTypes.contains(dataType)) {
            switch (dataType) {
                case 1:
                case 2:
                case 5:
                    //充电桩工作状态
                    GeneralFieldEnum workStatus = GeneralFieldEnum.WORK_STATUS;
                    resultList.add(PileRealFieldDto.builder().fieldCode(workStatus.getFieldCode()).fieldName(workStatus.getFieldName())
                            .fieldType(workStatus.getFieldType()).build());
                    //充电桩原始状态
                    GeneralFieldEnum originalStatus = GeneralFieldEnum.ORIGINAL_STATUS;
                    resultList.add(PileRealFieldDto.builder().fieldCode(originalStatus.getFieldCode()).fieldName(originalStatus.getFieldName())
                            .fieldType(originalStatus.getFieldType()).build());
                    //充电桩复位次数
                    GeneralFieldEnum resetTimes = GeneralFieldEnum.RESET_TIMES;
                    resultList.add(PileRealFieldDto.builder().fieldCode(resetTimes.getFieldCode()).fieldName(resetTimes.getFieldName())
                            .fieldType(resetTimes.getFieldType()).build());
                    break;
                case 3:
                case 4:
                    //内部温度
                    GeneralFieldEnum innerTemp = GeneralFieldEnum.INNER_TEMP;
                    resultList.add(PileRealFieldDto.builder().fieldCode(innerTemp.getFieldCode()).fieldName(innerTemp.getFieldName())
                            .fieldType(innerTemp.getFieldType()).build());
                    //充电桩总功率(kW)
                    GeneralFieldEnum totalPower = GeneralFieldEnum.TOTAL_POWER;
                    resultList.add(PileRealFieldDto.builder().fieldCode(totalPower.getFieldCode()).fieldName(totalPower.getFieldName())
                            .fieldType(totalPower.getFieldType()).build());
                    //充电桩充电功率(kW)
                    GeneralFieldEnum recChargePower = GeneralFieldEnum.REC_CHARGE_POWER;
                    resultList.add(PileRealFieldDto.builder().fieldCode(recChargePower.getFieldCode()).fieldName(recChargePower.getFieldName())
                            .fieldType(recChargePower.getFieldType()).build());
                    //充电桩放电功率(kW)
                    GeneralFieldEnum disChargePower = GeneralFieldEnum.DIS_CHARGE_POWER;
                    resultList.add(PileRealFieldDto.builder().fieldCode(disChargePower.getFieldCode()).fieldName(disChargePower.getFieldName())
                            .fieldType(disChargePower.getFieldType()).build());
                    //充电桩电力模块最高温度
                    GeneralFieldEnum powerModMaxTemp = GeneralFieldEnum.POWER_MOD_MAX_TEMP;
                    resultList.add(PileRealFieldDto.builder().fieldCode(powerModMaxTemp.getFieldCode()).fieldName(powerModMaxTemp.getFieldName())
                            .fieldType(powerModMaxTemp.getFieldType()).build());
                    //充电桩电力模块最高温度序号
                    GeneralFieldEnum maxTempMode = GeneralFieldEnum.MAX_TEMP_MOD;
                    resultList.add(PileRealFieldDto.builder().fieldCode(maxTempMode.getFieldCode()).fieldName(maxTempMode.getFieldName())
                            .fieldType(maxTempMode.getFieldType()).build());
                    break;
            }
        }
        //充电枪协议字段数据
        if (Objects.equals(dataType, 8)) {
            resultList = Arrays.stream(GeneralFieldEnum.values()).filter(s -> s.getFieldType() == 2).map(pileRealField -> {
                PileRealFieldDto result = new PileRealFieldDto();
                result.setFieldCode(pileRealField.getFieldCode());
                result.setFieldName(pileRealField.getFieldName());
                result.setFieldType(pileRealField.getFieldType());
                return result;
            }).collect(Collectors.toList());
        }
        return ResponseResult.ok(resultList);
    }

}
