package com.sunmax.device.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.sunmax.common.dto.PageDto;
import com.sunmax.common.dto.device.*;
import com.sunmax.common.dto.system.TenantDetailsDto;
import com.sunmax.common.entity.BaseEntity;
import com.sunmax.common.util.DateUtil;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.util.StringUtil;
import com.sunmax.common.vo.device.DeviceAlarmEventQueryVo;
import com.sunmax.common.vo.protocol.AlarmRecordQueryVo;
import com.sunmax.common.vo.together.PileGunChangeVo;
import com.sunmax.device.constants.DBConstants;
import com.sunmax.device.dao.access.*;
import com.sunmax.device.dao.model.ModelEventDao;
import com.sunmax.device.entity.access.*;
import com.sunmax.device.entity.model.ModelEventEntity;
import com.sunmax.device.service.TogetherFeignService;
import com.sunmax.device.service.feign.ProtocolService;
import com.sunmax.device.service.feign.SystemService;
import com.sunmax.device.util.DeviceCommonUtil;
import com.sunmax.device.util.TopNodeDataUtil;
import com.sunmax.device.vo.TopNodeParamVo;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TogetherFeignServiceImpl implements TogetherFeignService {

    @Autowired
    private DeviceDao deviceDao;

    @Autowired
    private DeviceGunDao deviceGunDao;

    @Autowired
    private SiteInfoDao siteInfoDao;

    @Autowired
    private AffiliatesInfoDao affiliatesInfoDao;

    @Autowired
    private SystemService systemService;

    @Autowired
    private SiteSetUpDao siteSetUpDao;

    @Autowired
    private SiteTopNodeDao siteTopNodeDao;

    @Autowired
    private SiteTopItemDao siteTopItemDao;

    @Autowired
    private ScenarioTypeDao scenarioTypeDao;

    @Autowired
    private DeviceEventDao deviceEventDao;

    @Autowired
    private ModelEventDao modelEventDao;

    @Autowired
    private ProtocolService protocolService;

    @Autowired
    private TopNodeDataUtil topNodeDataUtil;

    @Override
    @Transactional(transactionManager = DBConstants.JPA_TX_ACCESS, rollbackFor = Exception.class)
    public ResponseResult<Void> updatePileRea(String id, String userId, String readwriteObject) {
        Optional<DeviceEntity> optional = deviceDao.findById(id);
        if (optional.isPresent()) {
            DeviceEntity device = optional.get();
            device.setReadwriteObject(readwriteObject);
            device.setUpdateId(userId);
            deviceDao.save(device);
            return ResponseResult.ok();
        }
        return ResponseResult.paramError(ResponseResult.PARAM_ERROR);
    }

    @Override
    @Transactional(transactionManager = DBConstants.JPA_TX_ACCESS, rollbackFor = Exception.class)
    public ResponseResult<Void> updatePileGun(PileGunChangeVo pileGunChangeVo) {
        Optional<DeviceGunEntity> optional = deviceGunDao.findById(pileGunChangeVo.getId());
        if (optional.isPresent()) {
            DeviceGunEntity deviceGun = new DeviceGunEntity();
            BeanUtils.copyProperties(pileGunChangeVo, deviceGun);
            deviceGun.setDeviceId(optional.get().getDeviceId());
            deviceGun.setCreateTime(optional.get().getCreateTime());
            deviceGunDao.save(deviceGun);
            return ResponseResult.ok();
        }
        return ResponseResult.paramError(ResponseResult.PARAM_ERROR);
    }

    @Override
    @Transactional(transactionManager = DBConstants.JPA_TX_ACCESS, rollbackFor = Exception.class)
    public ResponseResult<String> updateSiteStateById(String siteId, Integer siteStatus) {
        if (StringUtil.isNotEmpty(siteId) && StringUtil.isNotEmpty(siteStatus)) {
            Optional<SiteInfoEntity> siteInfoDaoById = siteInfoDao.findById(siteId);
            if (siteInfoDaoById.isPresent()) {
                SiteInfoEntity siteInfoEntity = siteInfoDaoById.get();
                siteInfoEntity.setSiteStatus(siteStatus);
                siteInfoDao.save(siteInfoEntity);
                return ResponseResult.ok(ResponseResult.SUCCESS);
            }
        }
        return ResponseResult.error(ResponseResult.PARAM_ERROR);
    }

    @Override
    public ResponseResult<Map<String, List<AffiliatesInfoDto>>> findSiteAffiliatesInfoByIds(List<String> siteIdList) {
        Map<String, List<AffiliatesInfoDto>> resultMap = Maps.newHashMap();
        //根据多个站点id，查询关联方数据
        List<AffiliatesInfoEntity> affiliatesInfoEntityList = affiliatesInfoDao.findAllBySiteIdIn(siteIdList);
        if (CollectionUtils.isNotEmpty(affiliatesInfoEntityList)) {
            //根据多个租户id，查询租户信息
            Map<String, TenantDetailsDto> tenantDetailsDtoMap = Maps.newHashMap();
            ResponseResult<List<TenantDetailsDto>> tenantDetailsResult = systemService.findTenantDetailsByIds(affiliatesInfoEntityList.stream().map(AffiliatesInfoEntity::getTenantId).distinct().collect(Collectors.toList()));
            if (tenantDetailsResult.isSuccess() && CollectionUtils.isNotEmpty(tenantDetailsResult.getData())) {
                tenantDetailsDtoMap = tenantDetailsResult.getData().stream().collect(Collectors.toMap(TenantDetailsDto::getId, tenantDetailsDto -> tenantDetailsDto, (k1, k2) -> k1));
            }
            //根据站点id分组
            Map<String, TenantDetailsDto> finalTenantDetailsDtoMap = tenantDetailsDtoMap;
            affiliatesInfoEntityList.stream().collect(Collectors.groupingBy(AffiliatesInfoEntity::getSiteId)).forEach((k, v) ->
                    resultMap.put(k, v.stream().map(affiliatesInfoEntity -> {
                        AffiliatesInfoDto affiliatesInfoDto = new AffiliatesInfoDto();
                        BeanUtils.copyProperties(affiliatesInfoEntity, affiliatesInfoDto);
                        //获取租户信息
                        if (!finalTenantDetailsDtoMap.isEmpty() && finalTenantDetailsDtoMap.containsKey(affiliatesInfoEntity.getTenantId())) {
                            affiliatesInfoDto.setTenantName(finalTenantDetailsDtoMap.get(affiliatesInfoEntity.getTenantId()).getTenantName());
                        }
                        return affiliatesInfoDto;
                    }).collect(Collectors.toList())));
        }
        return ResponseResult.ok(resultMap);
    }

    @Override
    public ResponseResult<Integer> checkSitePassword(String siteId, String password) {
        SiteSetUpEntity setUpEntity = siteSetUpDao.findBySiteId(siteId);
        if (StringUtil.isNotEmpty(setUpEntity) && setUpEntity.getOperatePassword().equals(password)) {
            return ResponseResult.ok(1);
        }
        return ResponseResult.ok(0);
    }

    @Override
    @Transactional(transactionManager = DBConstants.JPA_TX_ACCESS, rollbackFor = Exception.class)
    public ResponseResult<String> updateDeviceOperateStatus(String deviceId, Integer operateStatus) {
        //根据设备id查询基本信息
        Optional<DeviceEntity> deviceDaoById = deviceDao.findById(deviceId);
        if (deviceDaoById.isPresent()) {
            DeviceEntity deviceEntity = deviceDaoById.get();
            deviceEntity.setOperateStatus(operateStatus);
            deviceDao.save(deviceEntity);
            return ResponseResult.ok(ResponseResult.SUCCESS);
        }
        return ResponseResult.error(ResponseResult.FAIL);
    }

    @Override
    public ResponseResult<Map<String, Integer>> findDeviceTxStatus(List<DeviceBasicInfoDto> deviceBasicInfoDtos) {
        Map<String, Integer> resultMap = Maps.newHashMap();
        //循环设备id集合
        deviceBasicInfoDtos.stream().collect(Collectors.toMap(DeviceBasicInfoDto::getId, deviceBasicInfoDto -> deviceBasicInfoDto, (k1, k2) -> k1))
                .forEach((deviceId, deviceBasicInfoDto) -> {
                    Integer deviceTxStatus = DeviceCommonUtil.getDeviceTxStatus(deviceId, deviceBasicInfoDto.getDeviceNumber());
                    resultMap.put(deviceId, deviceTxStatus);
                });
        return ResponseResult.ok(resultMap);
    }

    @Override
    public ResponseResult<Map<String, List<String>>> findSiteMeasureIdBySiteIds(List<String> siteIdList, Integer nodeType, Integer deviceType) {
        Map<String, List<String>> resultMap = Maps.newHashMap();

//        //根据站点id，查询拓扑节点信息
//        List<SiteTopNodeEntity> siteTopNodeEntityList = siteTopoNodeDao.findAllBySiteIdInAndNodeTypeAndDeviceType(siteIdList, nodeType, deviceType);
//        if (CollectionUtils.isEmpty(siteTopNodeEntityList)) {
//            return ResponseResult.ok(resultMap);
//        }
//
//        Map<String, List<TopoBeDeviceEntity>> groupByTopoIdMap = siteTopItemDao.findAllByTopoIdIn(siteTopNodeEntityList.stream().map(SiteTopNodeEntity::getId).collect(Collectors.toList()))
//                .stream().collect(Collectors.groupingBy(TopoBeDeviceEntity::getTopoId));
//
//        Map<String, List<SiteTopNodeEntity>> groupBySiteIdMap = siteTopNodeEntityList.stream().collect(Collectors.groupingBy(SiteTopNodeEntity::getSiteId));
//
//        siteIdList.forEach(siteId -> {
//            if (groupBySiteIdMap.containsKey(siteId)) {
//                List<String> deviceIdList = Lists.newArrayList();
//                groupBySiteIdMap.get(siteId).forEach(siteTopNodeEntity -> {
//                    if (groupByTopoIdMap.containsKey(siteTopNodeEntity.getId())) {
//                        deviceIdList.addAll(groupByTopoIdMap.get(siteTopNodeEntity.getId()).stream().map(TopoBeDeviceEntity::getDeviceId).collect(Collectors.toList()));
//                    }
//
//                });
//                resultMap.put(siteId, deviceIdList.stream().distinct().collect(Collectors.toList()));
//            }
//        });
        return ResponseResult.ok(resultMap);
    }

    @Override
    public ResponseResult<String> findSiteIdBySubId(String subId) {
        if (siteInfoDao.findById(subId).isPresent()) {
            return ResponseResult.ok(subId);
        }
        Optional<ScenarioTypeEntity> optional = scenarioTypeDao.findById(subId);
        return optional.map(scenarioType -> ResponseResult.ok(scenarioType.getSiteId())).orElse(null);
    }

    @Override
    public ResponseResult<PageDto<DeviceAlarmEventListDto>> findAllDeviceEventList(DeviceAlarmEventQueryVo eventQueryVo) {
        //返回的集合
        List<DeviceAlarmEventListDto> resultList = Lists.newArrayList();

        //根据查询条件查询设备事件数据
        List<DeviceEventEntity> deviceEventList = deviceEventDao.findAll((Specification<DeviceEventEntity>) (root, cq, cb) -> {
            List<Predicate> list = new ArrayList<>();
            if (StringUtil.isNotEmpty(eventQueryVo.getDeviceIds())) { //多个设备id
                list.add(cb.in(root.get("deviceId")).value(JSON.parseArray(eventQueryVo.getDeviceIds())));
            }
            if (StringUtil.isNotEmpty(eventQueryVo.getEventStatus())) { //事件状态 0-未恢复 1-已修复
                list.add(cb.equal(root.get("eventStatus"), eventQueryVo.getEventStatus()));
            }
            if (StringUtil.isNotEmpty(eventQueryVo.getIgnoreStatus())) { //忽略状态 0-未忽略 1-已忽略
                list.add(cb.equal(root.get("ignoreStatus"), eventQueryVo.getIgnoreStatus()));
            }
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
                DeviceAlarmEventListDto result = new DeviceAlarmEventListDto();
                result.setId(deviceEvent.getId());
                result.setDeviceId(deviceEvent.getDeviceId());
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
        if (StringUtil.isNotEmpty(eventQueryVo.getDeviceIds())) {
            Map<String, String> deviceCodeIdMap = deviceDao.findAllById(JSON.parseArray(eventQueryVo.getDeviceIds(), String.class)).stream()
                    .filter(c -> StringUtil.isNotEmpty(c.getDeviceNumber())).collect(Collectors.toMap(DeviceEntity::getDeviceNumber, BaseEntity::getId,
                            (k1, k2) -> k1));
            if (MapUtils.isNotEmpty(deviceCodeIdMap)) {
                AlarmRecordQueryVo alarmQueryVo = new AlarmRecordQueryVo();
                alarmQueryVo.setDeviceCodes(deviceCodeIdMap.keySet());
                alarmQueryVo.setEventLevel(eventQueryVo.getEventLevel());
                alarmQueryVo.setAlarmStatus(eventQueryVo.getEventStatus());
                alarmQueryVo.setStartDate(eventQueryVo.getStartDate());
                alarmQueryVo.setEndDate(eventQueryVo.getEndDate());
                resultList.addAll(protocolService.findAlarmRecordList(alarmQueryVo).getData()
                        .stream().filter(alarmRecord -> StringUtil.isNotEmpty(alarmRecord.getFaultCode()))
                        .map(alarmRecord -> {
                            DeviceAlarmEventListDto result = new DeviceAlarmEventListDto();
                            BeanUtils.copyProperties(alarmRecord, result);
                            if (deviceCodeIdMap.containsKey(alarmRecord.getDeviceCode())) {
                                result.setDeviceId(deviceCodeIdMap.get(alarmRecord.getDeviceCode()));
                            }
                            result.setFunctionNames("故障码: " + alarmRecord.getFaultCode());
                            result.setEventStatus(alarmRecord.getAlarmStatus());
                            result.setIsAllow(2);
                            result.setType(2);
                            return result;
                        }).collect(Collectors.toList()));
            }
        }
        return ResponseResult.ok(new PageDto<>(resultList.stream().filter(s -> StringUtil.isNotEmpty(s.getCreateTime()))
                .sorted(Comparator.comparing(DeviceAlarmEventListDto::getCreateTime).reversed()).collect(Collectors.toList()),
                eventQueryVo.getPage(), eventQueryVo.getSize()));
    }

    @Override
    @Transactional(transactionManager = DBConstants.JPA_TX_ACCESS, rollbackFor = Exception.class)
    public ResponseResult<Void> updateEventIgnoreStatus(String id, Integer ignoreStatus) {
        Optional<DeviceEventEntity> optional = deviceEventDao.findById(id);
        if (optional.isPresent()) {
            DeviceEventEntity deviceEvent = optional.get();
            deviceEvent.setIgnoreStatus(ignoreStatus);
            deviceEventDao.save(deviceEvent);
            return ResponseResult.ok();
        } else {
            return protocolService.updateEventIgnoreStatus(id, ignoreStatus);
        }
    }

    @Override
    public ResponseResult<List<SiteTopDataDto>> findSiteTopDataListBySiteId(String siteId) {
        //返回的集合
        List<SiteTopDataDto> resultList = Lists.newArrayList();
        //根据站点id查询站点拓扑节点数据
        List<SiteTopNodeEntity> siteTopNodeList = siteTopNodeDao.findAllBySiteId(siteId);
        if (CollectionUtils.isNotEmpty(siteTopNodeList)) {
            //获取拓扑节点数据
            Map<String, Map<String, Double>> gateNodeDataMap = Maps.newHashMap();
            Map<String, Map<String, Double>> measureNodeDataMap = Maps.newHashMap();
            Map<String, Map<String, Double>> inverterNodeDataMap = Maps.newHashMap();
            Map<String, Map<String, Double>> storageNodeDataMap = Maps.newHashMap();
            Map<String, Map<String, Double>> pileNodeDataMap = Maps.newHashMap();
            Map<String, Map<String, Double>> switchNodeDataMap = Maps.newHashMap();
            Map<String, Map<String, Double>> changeNodeDataMap = Maps.newHashMap();
            List<Integer> nodeTypes = TopNodeDataUtil.getNodeTypeList();
            Map<Integer, List<SiteTopNodeEntity>> siteTopNodeMap = siteTopNodeList.stream().filter(s -> StringUtil.isNotEmpty(s.getNodeType())
                    && StringUtil.isNotEmpty(s.getDeviceIds()) && nodeTypes.contains(s.getNodeType())).collect(Collectors
                    .groupingBy(SiteTopNodeEntity::getNodeType));
            for (Map.Entry<Integer, List<SiteTopNodeEntity>> entry : siteTopNodeMap.entrySet()) {
                Integer nodeType = entry.getKey(); //节点类型
                List<SiteTopNodeEntity> topNodeList = entry.getValue(); //拓扑节点数据
                Map<String, Set<String>> nodeDeviceMap = Maps.newHashMap();
                Map<String, Set<String>> nodePcsMap = Maps.newHashMap();
                Map<String, Set<String>> nodeBatteryMap = Maps.newHashMap();
                Map<String, Integer> nodeParaMap = Maps.newHashMap();
                if (nodeType != 8) {
                    nodeDeviceMap = topNodeList.stream().collect(Collectors.toMap(SiteTopNodeEntity::getId, c -> new HashSet<>(JSON.parseArray(c.getDeviceIds(), String.class))));
                } else {
                    topNodeList.forEach(topNode -> {
                        Set<String> pcsIds = Sets.newHashSet();
                        Set<String> batteryIds = Sets.newHashSet();
                        List<JSONObject> jsonObjects = JSON.parseArray(topNode.getDeviceIds(), JSONObject.class);
                        jsonObjects.forEach(jsonObject -> {
                            if (jsonObject.containsKey("pcsId")) {
                                pcsIds.add(jsonObject.getString("pcsId"));
                            }
                            if (jsonObject.containsKey("batteryId")) {
                                batteryIds.add(jsonObject.getString("batteryId"));
                            }
                        });
                        nodePcsMap.put(topNode.getId(), pcsIds);
                        nodeBatteryMap.put(topNode.getId(), batteryIds);
                    });
                    nodeParaMap = topNodeList.stream().filter(c -> StringUtil.isNotEmpty(c.getReaObject())).collect(Collectors
                            .toMap(SiteTopNodeEntity::getId, c -> JSON.parseObject(c.getReaObject()).getInteger("operation")));
                }
                //节点类型 1-拓扑点 2-电网 3-变压器 4-关口点 5-计量点 6-逆变器 7-光伏组件 8-储能柜 9-负荷 10-充电桩 11-开关 12-车辆 13-换电站
                switch (entry.getKey()) {
                    case 4:
                        gateNodeDataMap = topNodeDataUtil.getGatewayNodeData(nodeDeviceMap);
                        break;
                    case 5:
                        measureNodeDataMap = topNodeDataUtil.getGatewayNodeData(nodeDeviceMap);
                        break;
                    case 6:
                        inverterNodeDataMap = topNodeDataUtil.getInverterNodeData(nodeDeviceMap);
                        break;
                    case 8:
                        Map<String, Map<String, Double>> pcsNodeDataMap = topNodeDataUtil.getPcsNodeData(nodePcsMap);
                        Map<String, Map<String, Double>> batteryNodeDataMap = topNodeDataUtil.getBatteryNodeData(nodeBatteryMap, nodeParaMap);
                        for (SiteTopNodeEntity siteTopNode : topNodeList) {
                            Map<String, Double> nodeDataMap = Maps.newHashMap();
                            if (pcsNodeDataMap.containsKey(siteTopNode.getId())) {
                                nodeDataMap.putAll(pcsNodeDataMap.get(siteTopNode.getId()));
                            }
                            if (batteryNodeDataMap.containsKey(siteTopNode.getId())) {
                                nodeDataMap.putAll(batteryNodeDataMap.get(siteTopNode.getId()));
                            }
                            storageNodeDataMap.put(siteTopNode.getId(), nodeDataMap);
                        }
                        break;
                    case 10:
                        pileNodeDataMap = topNodeDataUtil.getPileNodeData(nodeDeviceMap);
                        break;
                    case 11:
                        switchNodeDataMap = topNodeDataUtil.getSwitchNodeData(nodeDeviceMap);
                        break;
                    case 13:
                        changeNodeDataMap = topNodeDataUtil.getChangeNodeData(nodeDeviceMap);
                        break;
                }
            }

            //根据多个拓扑节点id查询拓扑项数据
            Map<String, List<SiteTopItemEntity>> siteTopItemMap = siteTopItemDao.findAllByNodeIdIn(siteTopNodeList.stream().map(SiteTopNodeEntity::getId)
                    .collect(Collectors.toList())).stream().collect(Collectors.groupingBy(SiteTopItemEntity::getNodeId));

            //对数据进行组装
            Map<String, Map<String, Double>> finalGateNodeDataMap = gateNodeDataMap;
            Map<String, Map<String, Double>> finalMeasureNodeDataMap = measureNodeDataMap;
            Map<String, Map<String, Double>> finalInverterNodeDataMap = inverterNodeDataMap;
            Map<String, Map<String, Double>> finalPileNodeDataMap = pileNodeDataMap;
            Map<String, Map<String, Double>> finalSwitchNodeDataMap = switchNodeDataMap;
            Map<String, Map<String, Double>> finalChangeNodeDataMap = changeNodeDataMap;
            resultList = siteTopNodeList.stream().map(siteTopNode -> {
                SiteTopDataDto result = new SiteTopDataDto();
                BeanUtils.copyProperties(siteTopNode, result);
                //根据节点类型获取数据
                Map<String, Double> nodeDataMap = Maps.newHashMap();
                //节点类型 1-拓扑点 2-电网 3-变压器 4-关口点 5-计量点 6-逆变器 7-光伏组件 8-储能柜 9-负荷 10-充电桩 11-开关 12-车辆 13-换电站
                switch (siteTopNode.getNodeType()) {
                    case 4:
                        nodeDataMap = finalGateNodeDataMap.getOrDefault(siteTopNode.getId(), Maps.newHashMap());
                        break;
                    case 5:
                        nodeDataMap = finalMeasureNodeDataMap.getOrDefault(siteTopNode.getId(), Maps.newHashMap());
                        break;
                    case 6:
                        if (StringUtil.isNotEmpty(siteTopNode.getDeviceIds())) {
                            result.setDataNum1(JSON.parseArray(siteTopNode.getDeviceIds()).size());
                        }
                        nodeDataMap = finalInverterNodeDataMap.getOrDefault(siteTopNode.getId(), Maps.newHashMap());
                        break;
                    case 8:
                        if (StringUtil.isNotEmpty(siteTopNode.getDeviceIds())) {
                            result.setDataNum1(JSON.parseArray(siteTopNode.getDeviceIds()).size());
                        }
                        nodeDataMap = storageNodeDataMap.getOrDefault(siteTopNode.getId(), Maps.newHashMap());
                        break;
                    case 10:
                        if (StringUtil.isNotEmpty(siteTopNode.getDeviceIds())) {
                            result.setDataNum1(JSON.parseArray(siteTopNode.getDeviceIds()).size());
                        }
                        nodeDataMap = finalPileNodeDataMap.getOrDefault(siteTopNode.getId(), Maps.newHashMap());
                        if (nodeDataMap.containsKey(TopNodeParamVo.DATA_NUM)) {
                            result.setDataNum2(nodeDataMap.getOrDefault(TopNodeParamVo.DATA_NUM, 0.0).intValue());
                        }
                        break;
                    case 11:
                        nodeDataMap = finalSwitchNodeDataMap.getOrDefault(siteTopNode.getId(), Maps.newHashMap());
                        break;
                    case 13:
                        nodeDataMap = finalChangeNodeDataMap.getOrDefault(siteTopNode.getId(), Maps.newHashMap());
                        break;
                }
                if (siteTopItemMap.containsKey(siteTopNode.getId())) {
                    Map<String, Double> finalNodeDataMap = nodeDataMap;
                    result.setSiteTopItemList(siteTopItemMap.get(siteTopNode.getId()).stream().map(siteTopItem -> {
                        SiteTopDataDto.SiteTopItemDto topItem = new SiteTopDataDto.SiteTopItemDto();
                        BeanUtils.copyProperties(siteTopItem, topItem);
                        if (finalNodeDataMap.containsKey(siteTopItem.getDataCode())) {
                            if (!Objects.equals(siteTopItem.getDataCode(), TopNodeParamVo.SWITCH_STATUS)) {
                                topItem.setDataValue(String.valueOf(finalNodeDataMap.get(siteTopItem.getDataCode())));
                            } else {
                                topItem.setDataValue(String.valueOf(finalNodeDataMap.get(siteTopItem.getDataCode()).intValue()));
                            }
                        }
                        return topItem;
                    }).collect(Collectors.toList()));
                }
                return result;
            }).collect(Collectors.toList());
        }
        return ResponseResult.ok(resultList);
    }

    @Override
    public ResponseResult<List<SiteTopNodeDto>> findSiteTopNodeBySiteId(String siteId, Integer nodeType) {
        //返回的集合
        List<SiteTopNodeDto> resultList = Lists.newArrayList();

        //根据站点id和节点类型查询站点下面的拓扑节点数据
        List<SiteTopNodeEntity> siteTopNodeList;
        if (StringUtil.isNotEmpty(nodeType)) {
            siteTopNodeList = siteTopNodeDao.findAllBySiteIdAndNodeType(siteId, nodeType);
        } else {
            siteTopNodeList = siteTopNodeDao.findAllBySiteId(siteId);
        }

        if (CollectionUtils.isNotEmpty(siteTopNodeList)) {

            //根据多个拓扑节点id查询拓扑项数据
            Map<String, List<SiteTopItemEntity>> siteTopItemMap = siteTopItemDao.findAllByNodeIdIn(siteTopNodeList.stream().map(SiteTopNodeEntity::getId)
                    .collect(Collectors.toList())).stream().collect(Collectors.groupingBy(SiteTopItemEntity::getNodeId));

            resultList = siteTopNodeList.stream().map(siteTopNode -> {
                SiteTopNodeDto result = new SiteTopNodeDto();
                BeanUtils.copyProperties(siteTopNode, result);
                if (siteTopItemMap.containsKey(siteTopNode.getId())) {
                    result.setSiteTopItemList(siteTopItemMap.get(siteTopNode.getId()).stream().map(siteTopItem -> {
                        SiteTopNodeDto.SiteTopItemDto topItem = new SiteTopNodeDto.SiteTopItemDto();
                        BeanUtils.copyProperties(siteTopItem, topItem);
                        return topItem;
                    }).collect(Collectors.toList()));
                }
                return result;
            }).collect(Collectors.toList());
        }
        return ResponseResult.ok(resultList);
    }

}
