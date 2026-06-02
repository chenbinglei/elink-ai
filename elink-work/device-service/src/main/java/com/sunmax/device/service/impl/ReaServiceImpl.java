package com.sunmax.device.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sunmax.common.dto.PageDto;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.util.StringUtil;
import com.sunmax.common.vo.statics.DeviceParamVo;
import com.sunmax.device.constants.DBConstants;
import com.sunmax.device.dao.access.DeviceDao;
import com.sunmax.device.dao.model.ModelReaDao;
import com.sunmax.device.dao.model.ReaDao;
import com.sunmax.device.dto.model.ReaListDto;
import com.sunmax.device.entity.access.DeviceEntity;
import com.sunmax.device.entity.model.ModelReaEntity;
import com.sunmax.device.entity.model.ReaEntity;
import com.sunmax.device.service.ReaService;
import com.sunmax.device.vo.model.ReaChangeVo;
import com.sunmax.device.vo.model.ReaQueryVo;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.Predicate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReaServiceImpl implements ReaService {

    @Autowired
    private ReaDao reaDao;

    @Autowired
    private ModelReaDao modelReaDao;

    @Autowired
    private DeviceDao deviceDao;

    @Override
    @Transactional(transactionManager = DBConstants.JPA_TX_MODEL, rollbackFor = Exception.class)
    public ResponseResult<Void> saveSea(ReaChangeVo reaChangeVo) {
        if (StringUtil.isEmpty(reaChangeVo.getFieldName())) {
            return ResponseResult.paramShow(reaChangeVo.getReaName(), ResponseResult.PARAM_ISNULL);
        }
        switch (reaChangeVo.getFieldName()) {
            case DeviceParamVo.DEVICE_ID:
            case DeviceParamVo.DEVICE_NAME:
            case DeviceParamVo.DEVICE_NUMBER:
            case DeviceParamVo.ACCESS_TYPE:
            case DeviceParamVo.DEVICE_DESC:
                return ResponseResult.paramShow(reaChangeVo.getReaName(), "设备静态字段已定义,不允许使用该字段");
        }
        //查询扩展属性数据 校验
        List<ReaEntity> reaNameList = reaDao.findAll(Example.of(ReaEntity.builder().typeId(reaChangeVo.getTypeId()).reaName(reaChangeVo.getReaName()).build()));
        List<ReaEntity> fieldNameList = reaDao.findAll(Example.of(ReaEntity.builder().typeId(reaChangeVo.getTypeId()).fieldName(reaChangeVo.getFieldName()).build()));
        //新增模型扩展属性
        if (StringUtil.isEmpty(reaChangeVo.getId())) {
            if (CollectionUtils.isNotEmpty(reaNameList)) {
                return ResponseResult.paramShow(reaChangeVo.getReaName(), ResponseResult.PARAM_EXIST);
            }
            if (CollectionUtils.isNotEmpty(fieldNameList)) {
                return ResponseResult.paramShow(reaChangeVo.getFieldName(), ResponseResult.PARAM_EXIST);
            }

            ReaEntity reaEntity = new ReaEntity();
            BeanUtils.copyProperties(reaChangeVo, reaEntity);
            reaDao.save(reaEntity);
            return ResponseResult.ok();
        } else { //编辑模型扩展属性
            //校验编辑中的数据
            reaNameList = reaNameList.stream().filter(s -> !Objects.equals(s.getId(), reaChangeVo.getId())).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(reaNameList)) {
                return ResponseResult.paramShow(reaChangeVo.getReaName(), ResponseResult.PARAM_EXIST);
            }
            fieldNameList = fieldNameList.stream().filter(s -> !Objects.equals(s.getId(), reaChangeVo.getId())).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(fieldNameList)) {
                return ResponseResult.paramShow(reaChangeVo.getFieldName(), ResponseResult.PARAM_EXIST);
            }
            Optional<ReaEntity> optional = reaDao.findById(reaChangeVo.getId());
            if (optional.isPresent()) {
                ReaEntity reaEntity = new ReaEntity();
                BeanUtils.copyProperties(reaChangeVo, reaEntity);
                reaDao.save(reaEntity);
                return ResponseResult.ok();
            }
        }
        return ResponseResult.paramError(ResponseResult.PARAM_ERROR);
    }

    @Override
    public ResponseResult<PageDto<ReaListDto>> querySeaList(ReaQueryVo reaQueryVo) {
        Page<ReaEntity> reaPage = reaDao.findAll((Specification<ReaEntity>) (root, cq, cb) -> {
            List<Predicate> list = new ArrayList<>();
            list.add(cb.equal(root.get("typeId"), reaQueryVo.getTypeId())); //资产分类id
            if (StringUtil.isNotEmpty(reaQueryVo.getKeyword())) { //关键字 属性名称+字段名称
                list.add(cb.or(cb.like(root.get("reaName"), "%" + reaQueryVo.getKeyword() + "%"),
                        cb.like(root.get("fieldName"), "%" + reaQueryVo.getKeyword() + "%")));
            }
            if (StringUtil.isNotEmpty(reaQueryVo.getReaType())) { //扩展属性类型 1-数值 2-文字 3-选项 4-位置 5-开关 6-时间 7-图片
                list.add(cb.equal(root.get("reaType"), reaQueryVo.getReaType()));
            }
//            if (StringUtil.isNotEmpty(reaQueryVo.getReadWriteType())) { //读写类型 1-只读 2-读写
//                list.add(cb.equal(root.get("readWriteType"), reaQueryVo.getReadWriteType()));
//            }
            return cb.and(list.toArray(new Predicate[0]));
        }, PageRequest.of(reaQueryVo.getPage() - 1, reaQueryVo.getSize(), Sort.by("createTime").descending()));
        List<ReaListDto> resultList = reaPage.getContent().stream().map(r -> {
            ReaListDto result = new ReaListDto();
            BeanUtils.copyProperties(r, result);
            return result;
        }).collect(Collectors.toList());
        return ResponseResult.ok(new PageDto<>(resultList, reaPage.getNumber() + 1, reaPage.getSize(), (int)reaPage.getTotalElements()));
    }

    @Override
    @Transactional(transactionManager = DBConstants.JPA_TX_MODEL, rollbackFor = Exception.class)
    public ResponseResult<Void> deleteSeaById(String id) {
        Optional<ReaEntity> optional = reaDao.findById(id);
        if (optional.isPresent()) {
            ReaEntity rea = optional.get();
            //根据扩展属性id查询关联的模型
            List<ModelReaEntity> modelReaList = modelReaDao.findAllByReaId(id);
            if (CollectionUtils.isNotEmpty(modelReaList)) {
                modelReaDao.deleteInBatch(modelReaList);
                //根据多个模型id查询设备该扩展属性
                Set<String> modelIds = modelReaList.stream().map(ModelReaEntity::getModelId).collect(Collectors.toSet());
                List<DeviceEntity> deviceList = deviceDao.findAllByModelIdInAndIsDelete(modelIds, 1);
                if (CollectionUtils.isNotEmpty(deviceList)) {
                    deviceList = deviceList.stream().peek(device -> {
                        if (StringUtil.isNotEmpty(device.getReadwriteObject())) {
                            JSONObject readwriteObject = JSONObject.parseObject(device.getReadwriteObject());
                            if (readwriteObject.containsKey(rea.getFieldName())) {
                                readwriteObject.remove(rea.getFieldName());
                                device.setReadwriteObject(JSON.toJSONString(readwriteObject));
                            }
                        }
                    }).collect(Collectors.toList());
                    deviceDao.saveAll(deviceList);
                }
            }
            reaDao.delete(rea);
        }
        return ResponseResult.ok();
    }

    @Override
    public ResponseResult<ReaListDto> findSeaDetailById(String id) {
        //返回的对象
        ReaListDto result = new ReaListDto();
        reaDao.findById(id).ifPresent(rea -> BeanUtils.copyProperties(rea, result));
        return ResponseResult.ok(result);
    }

}
