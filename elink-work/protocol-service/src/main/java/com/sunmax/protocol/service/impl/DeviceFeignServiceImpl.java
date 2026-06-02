package com.sunmax.protocol.service.impl;

import com.google.common.collect.Lists;
import com.sunmax.common.constant.WebTopicConstant;
import com.sunmax.common.dto.protocol.AlarmNumDto;
import com.sunmax.common.dto.PageDto;
import com.sunmax.common.dto.protocol.AlarmRecordDto;
import com.sunmax.common.dto.protocol.ProtocolListDto;
import com.sunmax.common.util.DateUtil;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.util.StringUtil;
import com.sunmax.common.util.oss.FileUtil;
import com.sunmax.common.vo.protocol.AlarmRecordQueryVo;
import com.sunmax.common.vo.protocol.PileBatchUpdateVo;
import com.sunmax.common.vo.protocol.ProtocolQueryVo;
import com.sunmax.protocol.config.emqx.web.WebMqttConfig;
import com.sunmax.protocol.dao.AlarmRecordDao;
import com.sunmax.protocol.dao.MqttRecordDao;
import com.sunmax.protocol.entity.AlarmRecordEntity;
import com.sunmax.protocol.mapper.AlarmRecordMapper;
import com.sunmax.protocol.entity.MqttRecordEntity;
import com.sunmax.protocol.service.DeviceFeignService;
import com.sunmax.protocol.util.MqttTopicUtil;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.persistence.criteria.Predicate;
import java.util.List;
import java.util.Set;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DeviceFeignServiceImpl implements DeviceFeignService {

    @Resource
    private WebMqttConfig webMqttConfig;

    @Resource
    private AlarmRecordDao alarmRecordDao;

    @Resource
    private AlarmRecordMapper alarmRecordMapper;

    @Resource
    private MqttRecordDao mqttRecordDao;
    @Override
    public ResponseResult<Void> addDeviceTopic(String deviceCode) {
        webMqttConfig.addAllTopic(WebTopicConstant.getAllTopics(deviceCode));
        return ResponseResult.ok();
    }

    @Override
    public ResponseResult<Void> deleteDeviceTopic(String deviceCode) {
        webMqttConfig.deleteAllTopic(WebTopicConstant.getAllTopics(deviceCode));
        return ResponseResult.ok();
    }

    @Override
    public ResponseResult<List<AlarmRecordDto>> findAlarmRecordList(AlarmRecordQueryVo alarmQueryVo) {
        //返回的集合
        List<AlarmRecordDto> resultList = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(alarmQueryVo.getDeviceCodes())) {
            List<AlarmRecordEntity> alarmRecordList = alarmRecordDao.findAll((Specification<AlarmRecordEntity>) (root, cq, cb) -> {
                List<Predicate> predicates = Lists.newArrayList();
                predicates.add(cb.in(root.get("deviceCode")).value(alarmQueryVo.getDeviceCodes()));
                if (StringUtil.isNotEmpty(alarmQueryVo.getEventLevel())) {
                    predicates.add(cb.equal(root.get("eventLevel"), alarmQueryVo.getEventLevel()));
                }
                if (StringUtil.isNotEmpty(alarmQueryVo.getGunCode())) {
                    predicates.add(cb.equal(root.get("gunCode"), alarmQueryVo.getGunCode()));
                }
                if (StringUtil.isNotEmpty(alarmQueryVo.getFaultCode())) {
                    predicates.add(cb.like(root.get("faultCode").as(String.class), "%" + alarmQueryVo.getFaultCode() + "%"));
                }
                if (StringUtil.isNotEmpty(alarmQueryVo.getAlarmStatus())) {
                    predicates.add(cb.equal(root.get("alarmStatus"), alarmQueryVo.getAlarmStatus()));
                }
                if (StringUtil.isNotEmpty(alarmQueryVo.getStartDate()) && StringUtil.isNotEmpty(alarmQueryVo.getEndDate())) {
                    predicates.add(cb.between(root.get("createTime"), DateUtil.strToLocalDateTime(DateUtil.getDayStart(alarmQueryVo.getStartDate())),
                            DateUtil.strToLocalDateTime(DateUtil.getDayEnd(alarmQueryVo.getEndDate()))));
                }
                if (StringUtil.isNotEmpty(alarmQueryVo.getRecoverStartDate()) && StringUtil.isNotEmpty(alarmQueryVo.getRecoverEndDate())) {
                    predicates.add(cb.between(root.get("updateTime"), DateUtil.strToLocalDateTime(DateUtil.getDayStart(alarmQueryVo.getRecoverStartDate())),
                            DateUtil.strToLocalDateTime(DateUtil.getDayEnd(alarmQueryVo.getRecoverEndDate()))));
                }
                if (StringUtil.isNotEmpty(alarmQueryVo.getIgnoreStatus())) { //忽略状态 0-未忽略 1-已忽略
                    predicates.add(cb.equal(root.get("ignoreStatus"), alarmQueryVo.getIgnoreStatus()));
                }
                return cb.and(predicates.toArray(new Predicate[0]));
            });
            if (CollectionUtils.isNotEmpty(alarmRecordList)) {
                resultList = alarmRecordList.stream().map(alarmRecord -> {
                    AlarmRecordDto result = new AlarmRecordDto();
                    BeanUtils.copyProperties(alarmRecord, result);
                    if (StringUtil.isNotEmpty(alarmRecord.getCreateTime())) {
                        result.setCreateTime(DateUtil.localDateTimeToStr(alarmRecord.getCreateTime()));
                    }
                    if (StringUtil.isNotEmpty(alarmRecord.getUpdateTime())) {
                        result.setUpdateTime(DateUtil.localDateTimeToStr(alarmRecord.getUpdateTime()));
                    }
                    //事件名称拼接
                    if (StringUtil.isNotEmpty(alarmRecord.getModuleAddr())) {
                        result.setEventName(alarmRecord.getEventName() + FileUtil.BAR + alarmRecord.getModuleAddr());
                    }
                    return result;
                }).collect(Collectors.toList());
            }
        }
        return ResponseResult.ok(resultList);
    }

    @Override
    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class)
    public ResponseResult<Void> deleteAllAlarmRecord(Set<String> deviceCodes) {
        List<AlarmRecordEntity> alarmRecordList = alarmRecordDao.findAllByDeviceCodeIn(deviceCodes);
        if (CollectionUtils.isNotEmpty(alarmRecordList)) {
            alarmRecordDao.deleteAll(alarmRecordList);
        }
        return ResponseResult.ok();
    }

    @Override
    public ResponseResult<List<AlarmNumDto>> countDeviceAlarmNum(Set<String> deviceCodes, String startTime, String endTime) {
        List<AlarmNumDto> resultList = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(deviceCodes) && StringUtil.isNotEmpty(startTime) && StringUtil.isNotEmpty(endTime)) {
            resultList = alarmRecordMapper.countDeviceAlarmNum(deviceCodes, startTime, endTime);
        }
        return ResponseResult.ok(resultList);
    }

    @Override
    public ResponseResult<PageDto<ProtocolListDto>> queryProtocolList(ProtocolQueryVo protocolQueryVo) {
        //返回的集合
        List<ProtocolListDto> resultList = Lists.newArrayList();

        //根据查询条件查询mqtt记录数据
        Page<MqttRecordEntity> mqttRecordPage = mqttRecordDao.findAll((Specification<MqttRecordEntity>) (root, cq, cb) -> {
            List<Predicate> list = new ArrayList<>();
            if (StringUtil.isNotEmpty(protocolQueryVo.getPileCode())) {
                list.add(cb.equal(root.get("pileCode"), protocolQueryVo.getPileCode()));
                if (StringUtil.isNotEmpty(protocolQueryVo.getGunCode())) {
                    list.add(cb.equal(root.get("gunCode"), protocolQueryVo.getGunCode()));
                }
            }
            if (StringUtil.isNotEmpty(protocolQueryVo.getStartTime()) && StringUtil.isNotEmpty(protocolQueryVo.getEndTime())) {
                LocalDateTime startTime = DateUtil.strToLocalDateTime(protocolQueryVo.getStartTime());
                LocalDateTime endTime = DateUtil.strToLocalDateTime(protocolQueryVo.getEndTime());
                list.add(cb.between(root.get("dateTime"), startTime, endTime));
            }
            if (StringUtil.isNotEmpty(protocolQueryVo.getProtocolType())) {
                list.add(cb.equal(root.get("protocolType"), protocolQueryVo.getProtocolType()));
            }
            return cb.and(list.toArray(new Predicate[0]));
        }, PageRequest.of(protocolQueryVo.getPage() - 1, protocolQueryVo.getSize(), Sort.by("dateTime").descending()));
        if (CollectionUtils.isNotEmpty(mqttRecordPage.getContent())) {
            resultList = mqttRecordPage.getContent().stream().map(mqttRecord -> {
                ProtocolListDto result = new ProtocolListDto();
                BeanUtils.copyProperties(mqttRecord, result);
                if (StringUtil.isNotEmpty(mqttRecord.getDateTime())) {
                    result.setDateTime(DateUtil.localDateTimeToStr(mqttRecord.getDateTime()));
                }
                if (StringUtil.isNotEmpty(mqttRecord.getType()) && StringUtil.isNotEmpty(mqttRecord.getMessageType())) {
                    if (mqttRecord.getType() == 1) {
                        result.setSender("云平台");
                        if (mqttRecord.getMessageType() == 1) {
                            result.setReceiver("边缘网关");
                        }
                        if (mqttRecord.getMessageType() == 2) {
                            result.setReceiver("云网关");
                        }
                    }
                    if (mqttRecord.getType() == 2) {
                        result.setReceiver("云平台");
                        if (mqttRecord.getMessageType() == 1) {
                            result.setSender("边缘网关");
                        }
                        if (mqttRecord.getMessageType() == 2) {
                            result.setSender("云网关");
                        }
                    }
                }
                return result;
            }).collect(Collectors.toList());
        }
        return ResponseResult.ok(new PageDto<>(resultList, mqttRecordPage.getNumber() + 1, mqttRecordPage.getSize(), (int) mqttRecordPage.getTotalElements()));
    }

    @Override
    public ResponseResult<Boolean> batchPileUpdate(PileBatchUpdateVo pileBatchUpdateVo) {
        return ResponseResult.ok(MqttTopicUtil.sendBatchPileUpdateTopic(pileBatchUpdateVo));
    }

    @Override
    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class)
    public ResponseResult<Void> updateEventIgnoreStatus(String id, Integer ignoreStatus) {
        Optional<AlarmRecordEntity> optional = alarmRecordDao.findById(id);
        if (optional.isPresent()) {
            AlarmRecordEntity alarmRecord = optional.get();
            alarmRecord.setIgnoreStatus(ignoreStatus);
            alarmRecordDao.save(alarmRecord);
            return ResponseResult.ok();
        }
        return ResponseResult.paramError(ResponseResult.PARAM_ERROR);
    }

}
