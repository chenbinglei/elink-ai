package com.sunmax.crontab.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.sunmax.common.dto.crontab.LocalCacheDto;
import com.sunmax.common.dto.crontab.NodeDifDataDto;
import com.sunmax.common.dto.crontab.NodeHistoryDataDto;
import com.sunmax.common.dto.crontab.SystemVarDataDto;
import com.sunmax.common.dto.data.DeviceHistoryDto;
import com.sunmax.common.dto.device.DeviceBasicInfoDto;
import com.sunmax.common.dto.device.FunctionDetailDto;
import com.sunmax.common.dto.device.SiteInfoDto;
import com.sunmax.common.model.RealDataModel;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.util.StringUtil;
import com.sunmax.common.vo.crontab.*;
import com.sunmax.common.vo.data.DeviceHistoryQueryVo;
import com.sunmax.common.vo.data.DeviceIndexQueryVo;
import com.sunmax.crontab.config.cache.LocalCache;
import com.sunmax.crontab.dao.ComputeNodeDao;
import com.sunmax.crontab.dao.SystemVariableDao;
import com.sunmax.crontab.dao.VariableNodeDao;
import com.sunmax.common.dto.crontab.ComputeNodeListDto;
import com.sunmax.crontab.entity.ComputeNodeEntity;
import com.sunmax.crontab.entity.SystemVariableEntity;
import com.sunmax.crontab.entity.VariableNodeEntity;
import com.sunmax.crontab.service.NodeTaskService;
import com.sunmax.crontab.service.TogetherFeignService;
import com.sunmax.crontab.service.feign.DataService;
import com.sunmax.crontab.service.feign.DeviceService;
import com.sunmax.crontab.util.ScheduleTaskUtil;
import com.sunmax.crontab.util.StrategyUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static com.sunmax.common.util.DeviceUtil.dataTypeConvert;
import static com.sunmax.common.util.DoubleUtil.getToDouble;
import static com.sunmax.crontab.config.WebSocketConfig.computeNodeService;

@Slf4j
@Service
public class TogetherFeignServiceImpl implements TogetherFeignService {

    @Autowired
    private SystemVariableDao systemVariableDao;

    @Autowired
    private VariableNodeDao variableNodeDao;

    @Autowired
    private NodeTaskService nodeTaskService;

    @Autowired
    private DeviceService deviceService;

    @Autowired
    private DataService dataService;

    @Autowired
    private LocalCache localCache;

    @Autowired
    private ScheduleTaskUtil scheduleTask;

    @Autowired
    private ComputeNodeDao computeNodeDao;

    /**
     * 根据系统变量编码查询实例关联节点数据
     *
     * @param varNodeValueVo
     * @return
     */
    @Override
    public ResponseResult<Map<String, Map<String, List<NodeHistoryDataDto>>>> findDeviceVarNodeValueByIds(VarNodeValueVo varNodeValueVo) {
        Map<String, Map<String, List<NodeHistoryDataDto>>> resultMap = new HashMap<>();

        List<SystemVariableEntity> systemVariableList = systemVariableDao.findAllByVarCodeIn(varNodeValueVo.getVarCodeList());

        //获取节点变量数据
        List<SystemVariableEntity> nodeVariables = filterVariables(systemVariableList, 1);
        processNodeVariables(nodeVariables, varNodeValueVo, resultMap);

        //获取模型功能点变量数据
        List<SystemVariableEntity> modelVariables = filterVariables(systemVariableList, 2);
        processModelVariables(modelVariables, varNodeValueVo, resultMap);

        return ResponseResult.ok(resultMap);
    }

    private List<SystemVariableEntity> filterVariables(List<SystemVariableEntity> systemVariableList, Integer dataSource) {
        return systemVariableList.stream()
                .filter(c -> Objects.equals(c.getDataSource(), dataSource))
                .collect(Collectors.toList());
    }

    /**
     * 获取节点变量数据
     *
     * @param nodeVariables
     * @param varNodeValueVo
     * @param resultMap
     */
    private void processNodeVariables(List<SystemVariableEntity> nodeVariables, VarNodeValueVo varNodeValueVo, Map<String, Map<String, List<NodeHistoryDataDto>>> resultMap) {
        if (CollectionUtils.isNotEmpty(nodeVariables)) {
            //根据变量id查询关联节点信息
            List<VariableNodeEntity> variableNodeEntityList = variableNodeDao.findAllByVarIdIn(nodeVariables.stream().map(SystemVariableEntity::getId).collect(Collectors.toList()));
            if (CollectionUtils.isNotEmpty(variableNodeEntityList)) {
                //过滤出需要的设备id数据
                List<VariableNodeEntity> variableNodeEntities = variableNodeEntityList.stream().filter(c -> varNodeValueVo.getDeviceIdList().stream().anyMatch(v -> Objects.equals(c.getDeviceId(), v))).collect(Collectors.toList());
                if (CollectionUtils.isNotEmpty(variableNodeEntities)) {
                    Map<String, SystemVariableEntity> systemVariableMap = nodeVariables.stream().collect(Collectors.toMap(SystemVariableEntity::getId, SystemVariableEntity -> SystemVariableEntity));
                    String startTime = varNodeValueVo.getStartTime();
                    String endTime = varNodeValueVo.getEndTime();
                    String timeInterval = varNodeValueVo.getTimeInterval();
                    List<Long> storageIdList = variableNodeEntities.stream().map(VariableNodeEntity::getStorageId).collect(Collectors.toList());
                    //查询指定节点表历史数据
                    NodeHistoryDataVo nodeHistoryDataVo = new NodeHistoryDataVo();
                    nodeHistoryDataVo.setStorageIdList(storageIdList);
                    nodeHistoryDataVo.setStartTime(startTime);
                    nodeHistoryDataVo.setEndTime(endTime);
                    nodeHistoryDataVo.setTimeInterval(timeInterval);
                    nodeHistoryDataVo.setLimitSize(varNodeValueVo.getLimitSize());
                    Map<Long, List<NodeHistoryDataDto>> nodeTaosDataByIds = nodeTaskService.findNodeTaosDataByIds(nodeHistoryDataVo);
                    //根据设备id分组
                    Map<String, List<VariableNodeEntity>> groupByDeviceIdMap = variableNodeEntities.stream().collect(Collectors.groupingBy(VariableNodeEntity::getDeviceId));
                    groupByDeviceIdMap.forEach((deviceId, variableEntitieList) -> {
                        Map<String, List<NodeHistoryDataDto>> nodeHistoryDataMap = Maps.newHashMap();
                        variableEntitieList.forEach(variableNodeEntity -> {
                            List<NodeHistoryDataDto> nodeHistoryDataDtoList = nodeTaosDataByIds.get(variableNodeEntity.getStorageId());
                            String varCode = systemVariableMap.get(variableNodeEntity.getVarId()).getVarCode();
                            nodeHistoryDataMap.put(varCode, nodeHistoryDataDtoList);
                        });
                        resultMap.put(deviceId, nodeHistoryDataMap);
                    });
                }
            }
        }
    }

    /**
     * 获取模型功能点变量数据
     *
     * @param modelVariables
     * @param varNodeValueVo
     * @param resultMap
     */
    private void processModelVariables(List<SystemVariableEntity> modelVariables, VarNodeValueVo varNodeValueVo, Map<String, Map<String, List<NodeHistoryDataDto>>> resultMap) {
        if (CollectionUtils.isNotEmpty(modelVariables)) {
            //根据变量id转成map
            Map<String, SystemVariableEntity> systemVariableEntityMap = modelVariables.stream().collect(Collectors.toMap(SystemVariableEntity::getId, systemVariableEntity -> systemVariableEntity));
            //根据变量id查询关联节点信息
            List<VariableNodeEntity> variableNodeEntityList = variableNodeDao.findAllByVarIdIn(modelVariables.stream().map(SystemVariableEntity::getId).collect(Collectors.toList()));
            if (CollectionUtils.isNotEmpty(variableNodeEntityList)) {
                Map<String, DeviceBasicInfoDto> deviceBasicInfoDtoMap = deviceService.findDeviceBasicInfoByIds(varNodeValueVo.getDeviceIdList()).getData();
                //根据多个设备id查询模型id
                List<String> modelIdList = deviceBasicInfoDtoMap.values().stream().map(DeviceBasicInfoDto::getModelId).distinct().collect(Collectors.toList());
                //过滤出需要的模型id数据
                List<VariableNodeEntity> variableNodeEntities = variableNodeEntityList.stream().filter(c -> modelIdList.stream().anyMatch(v -> Objects.equals(c.getDeviceId(), v))).collect(Collectors.toList());
                if (CollectionUtils.isNotEmpty(variableNodeEntities)) {
                    //根据模型id分组
                    Map<String, List<VariableNodeEntity>> groupByModelIdMap = variableNodeEntities.stream().collect(Collectors.groupingBy(VariableNodeEntity::getDeviceId));
                    //获取功能点id
                    List<String> functionIdList = variableNodeEntities.stream().map(VariableNodeEntity::getFunctionId).filter(StringUtil::isNotEmpty).distinct().collect(Collectors.toList());
                    Map<String, FunctionDetailDto> functionDetailDtoMap = deviceService.findFunctionDetailByIds(functionIdList).getData();
                    if (!functionDetailDtoMap.isEmpty()) {
                        List<String> functionLogoList = functionDetailDtoMap.values().stream().map(FunctionDetailDto::getFunctionLogo).collect(Collectors.toList());
                        Integer index = varNodeValueVo.getIndex();
                        //返回数据对象
                        ResponseResult<Map<String, Map<String, List<DeviceHistoryDto>>>> deviceHistoryValueList;
                        //普通功能点数据查询
                        if (StringUtil.isEmpty(index)) {
                            //查询设备和功能点历史数据
                            DeviceHistoryQueryVo deviceHistoryQueryVo = new DeviceHistoryQueryVo();
                            deviceHistoryQueryVo.setDeviceIds(new HashSet<>(varNodeValueVo.getDeviceIdList()));
                            deviceHistoryQueryVo.setFunctionLogos(new HashSet<>(functionLogoList));
                            deviceHistoryQueryVo.setStartTime(varNodeValueVo.getStartTime());
                            deviceHistoryQueryVo.setEndTime(varNodeValueVo.getEndTime());
                            deviceHistoryQueryVo.setTimeInterval(varNodeValueVo.getTimeInterval());
                            deviceHistoryQueryVo.setLimitSize(varNodeValueVo.getLimitSize());
                            deviceHistoryValueList = dataService.findDeviceHistoryValueList(deviceHistoryQueryVo);
                        } else {
                            //查询所选索引的设备历史数据
                            DeviceIndexQueryVo deviceIndexQueryVo = new DeviceIndexQueryVo();
                            Map<String, Set<String>> deviceFuctionMap = Maps.newHashMap();
                            Set<String> fuctionSet = new HashSet<>();
                            functionLogoList.forEach(functionLogo -> fuctionSet.add(functionLogo + "index" + (index - 1)));
                            varNodeValueVo.getDeviceIdList().forEach(deviceId -> deviceFuctionMap.put(deviceId, fuctionSet));
                            deviceIndexQueryVo.setDeviceFuctionMap(deviceFuctionMap);
                            deviceIndexQueryVo.setStartTime(varNodeValueVo.getStartTime());
                            deviceIndexQueryVo.setEndTime(varNodeValueVo.getEndTime());
                            deviceIndexQueryVo.setTimeInterval(varNodeValueVo.getTimeInterval());
                            deviceIndexQueryVo.setLimitSize(varNodeValueVo.getLimitSize());
                            deviceHistoryValueList = dataService.findDeviceHistoryIndexValueList(deviceIndexQueryVo);
                        }
                        if (deviceHistoryValueList.isSuccess() && !deviceHistoryValueList.getData().isEmpty()) {
                            Map<String, Map<String, List<DeviceHistoryDto>>> historyValueListData = deviceHistoryValueList.getData();
                            //循环设备信息组装数据
                            deviceBasicInfoDtoMap.forEach((deviceId, deviceBasicInfoDto) -> {
                                Map<String, List<DeviceHistoryDto>> deviceHistoryMap = historyValueListData.get(deviceId);
                                if (!deviceHistoryMap.isEmpty()) {
                                    //根据变量id转成map
                                    Map<String, VariableNodeEntity> variableNodeEntityMap = groupByModelIdMap.get(deviceBasicInfoDto.getModelId()).stream().collect(Collectors.toMap(VariableNodeEntity::getVarId, variableNodeEntity -> variableNodeEntity, (k1, k2) -> k1));
                                    Map<String, List<NodeHistoryDataDto>> nodeHistoryDataMap = new HashMap<>();
                                    //循环系统变量获取数据
                                    systemVariableEntityMap.forEach((varId, systemVariableEntity) -> {
                                        //获取变量关联信息
                                        VariableNodeEntity variableNodeEntity = variableNodeEntityMap.get(varId);
                                        if (StringUtil.isNotEmpty(variableNodeEntity)) {
                                            //获取功能点信息
                                            FunctionDetailDto functionDetailDto = functionDetailDtoMap.get(variableNodeEntity.getFunctionId());
                                            if (StringUtil.isNotEmpty(functionDetailDto)) {
                                                if (deviceHistoryMap.containsKey(functionDetailDto.getFunctionLogo())) {
                                                    //根据功能标识获取历史数据
                                                    List<DeviceHistoryDto> deviceHistoryDtoList = deviceHistoryMap.get(functionDetailDto.getFunctionLogo());
                                                    List<NodeHistoryDataDto> convertedDataList = deviceHistoryDtoList.stream()
                                                            .map(this::createNodeHistoryDataDto)
                                                            .collect(Collectors.toList());
                                                    nodeHistoryDataMap.put(systemVariableEntity.getVarCode(), convertedDataList);
                                                }
                                            }
                                        }
                                    });
                                    resultMap.put(deviceId, nodeHistoryDataMap);
                                }
                            });
                        }
                    }
                }
            }
        }
    }


    private NodeHistoryDataDto createNodeHistoryDataDto(DeviceHistoryDto deviceHistoryDto) {
        NodeHistoryDataDto nodeHistoryDataDto = new NodeHistoryDataDto();

        Integer dataType = deviceHistoryDto.getDataType();
        Object dataValue = deviceHistoryDto.getDataValue();
        Double aDouble = dataTypeConvert(dataType, dataValue);

        nodeHistoryDataDto.setResultValue(aDouble);
        nodeHistoryDataDto.setTs(deviceHistoryDto.getDateTime());

        return nodeHistoryDataDto;
    }

    /**
     * 根据站点id和系统变量查询关联节点数据
     *
     * @param varNodeValueVo
     * @return
     */
    @Override
    public ResponseResult<Map<String, Map<String, List<NodeHistoryDataDto>>>> findSiteVarNodeValueByIds(VarNodeValueVo varNodeValueVo) {
        Map<String, Map<String, List<NodeHistoryDataDto>>> resultMap = Maps.newHashMap();

        //根据多个变量编码查询指定实例下关联的变量
        List<SystemVariableEntity> systemVariableEntityList = systemVariableDao.findAllByVarCodeIn(varNodeValueVo.getVarCodeList());
        if (CollectionUtils.isNotEmpty(systemVariableEntityList)) {
            //根据变量id查询关联节点信息
            List<VariableNodeEntity> variableNodeEntityList = variableNodeDao.findAllByVarIdIn(systemVariableEntityList.stream().map(SystemVariableEntity::getId).collect(Collectors.toList()));
            if (CollectionUtils.isNotEmpty(variableNodeEntityList)) {
                List<String> deviceIdList = varNodeValueVo.getDeviceIdList();
                //过滤出需要的站点id数据
                List<VariableNodeEntity> variableNodeEntities = variableNodeEntityList.stream().filter(c -> deviceIdList.stream().anyMatch(v -> Objects.equals(c.getDeviceId(), v))).collect(Collectors.toList());
                if (CollectionUtils.isNotEmpty(variableNodeEntities)) {
                    String startTime = varNodeValueVo.getStartTime();
                    String endTime = varNodeValueVo.getEndTime();
                    String timeInterval = varNodeValueVo.getTimeInterval();
                    List<Long> storageIdList = variableNodeEntities.stream().map(VariableNodeEntity::getStorageId).collect(Collectors.toList());
                    //查询指定节点表历史数据
                    NodeHistoryDataVo nodeHistoryDataVo = new NodeHistoryDataVo();
                    nodeHistoryDataVo.setStorageIdList(storageIdList);
                    nodeHistoryDataVo.setStartTime(startTime);
                    nodeHistoryDataVo.setEndTime(endTime);
                    nodeHistoryDataVo.setTimeInterval(timeInterval);
                    Map<Long, List<NodeHistoryDataDto>> nodeTaosDataByIds = nodeTaskService.findNodeTaosDataByIds(nodeHistoryDataVo);
                    //根据变量id分组
                    Map<String, List<VariableNodeEntity>> groupByVarIdMap = variableNodeEntities.stream().collect(Collectors.groupingBy(VariableNodeEntity::getVarId));
                    //循环系统变量获取数据
                    systemVariableEntityList.forEach(systemVariableEntity -> {
                        if (groupByVarIdMap.containsKey(systemVariableEntity.getId())) {
                            Map<String, List<NodeHistoryDataDto>> dataMap = Maps.newHashMap();
                            groupByVarIdMap.get(systemVariableEntity.getId()).forEach(variableNodeEntity -> {
                                List<NodeHistoryDataDto> nodeHistoryDataDtoList = Lists.newArrayList();
                                if (!nodeTaosDataByIds.isEmpty() && nodeTaosDataByIds.containsKey(variableNodeEntity.getStorageId())) {
                                    nodeHistoryDataDtoList = nodeTaosDataByIds.get(variableNodeEntity.getStorageId());
                                }
                                dataMap.put(variableNodeEntity.getDeviceId(), nodeHistoryDataDtoList);
                            });
                            resultMap.put(systemVariableEntity.getVarCode(), dataMap);
                        }
                    });
                }
            }
        }
        return ResponseResult.ok(resultMap);
    }

    @Override
    public ResponseResult<Map<String, Map<String, LocalCacheDto>>> findNodeCacheByVarCodes(List<String> varCodeList, List<String> queryIdList) {
        Map<String, Map<String, LocalCacheDto>> resultMap = Maps.newHashMap();
        //根据多个变量编码查询指定实例下关联的变量
        List<SystemVariableEntity> systemVariableEntityList = systemVariableDao.findAllByVarCodeIn(varCodeList);
        if (CollectionUtils.isNotEmpty(systemVariableEntityList)) {
            //根据变量id查询关联节点信息
            List<VariableNodeEntity> variableNodeEntityList = variableNodeDao.findAllByVarIdIn(systemVariableEntityList.stream().map(SystemVariableEntity::getId).collect(Collectors.toList()));
            if (CollectionUtils.isNotEmpty(variableNodeEntityList)) {
                //过滤出需要的设备/站点id数据
                List<VariableNodeEntity> variableNodeEntities = variableNodeEntityList.stream().filter(c -> queryIdList.stream().anyMatch(v -> Objects.equals(c.getDeviceId(), v))).collect(Collectors.toList());
                if (CollectionUtils.isNotEmpty(variableNodeEntities)) {
                    //根据变量id分组
                    Map<String, List<VariableNodeEntity>> groupByVarIdMap = variableNodeEntities.stream().collect(Collectors.groupingBy(VariableNodeEntity::getVarId));
                    //循环系统变量，获取计算节点实时数据
                    systemVariableEntityList.forEach(systemVariableEntity -> {
                        if (groupByVarIdMap.containsKey(systemVariableEntity.getId())) {
                            Map<String, LocalCacheDto> localCacheDtoMap = Maps.newHashMap();
                            groupByVarIdMap.get(systemVariableEntity.getId()).forEach(variableNodeEntity -> {
                                LocalCacheDto localCacheDto = localCache.getValue(variableNodeEntity.getNodeId());
                                localCacheDtoMap.put(variableNodeEntity.getDeviceId(), localCacheDto);
                            });
                            resultMap.put(systemVariableEntity.getVarCode(), localCacheDtoMap);
                        }
                    });
                }
            }
        }
        return ResponseResult.ok(resultMap);
    }

    /**
     * 根据多个系统变量编码和多个设备/站点id查询计算节点统计值数据
     *
     * @return
     */
    @Override
    public ResponseResult<Map<String, Map<String, List<NodeHistoryDataDto>>>> findVarNodeDataByCountFun(VarNodeCuntFunQueryVo varNodeCuntFunQueryVo) {
        Map<String, Map<String, List<NodeHistoryDataDto>>> resultMap = Maps.newHashMap();
        //根据多个变量编码查询指定实例下关联的变量
        List<SystemVariableEntity> systemVariableEntityList = systemVariableDao.findAllByVarCodeIn(varNodeCuntFunQueryVo.getVarCodeList());
        if (CollectionUtils.isNotEmpty(systemVariableEntityList)) {
            //根据变量id查询关联节点信息
            List<VariableNodeEntity> variableNodeEntityList = variableNodeDao.findAllByVarIdIn(systemVariableEntityList.stream().map(SystemVariableEntity::getId).collect(Collectors.toList()));
            if (CollectionUtils.isNotEmpty(variableNodeEntityList)) {
                //过滤出需要的设备/站点id数据
                List<VariableNodeEntity> variableNodeEntities = variableNodeEntityList.stream().filter(c -> varNodeCuntFunQueryVo.getQueryIdList().stream().anyMatch(v -> Objects.equals(c.getDeviceId(), v))).collect(Collectors.toList());
                if (CollectionUtils.isNotEmpty(variableNodeEntities)) {
                    List<Long> storageIdList = variableNodeEntities.stream().map(VariableNodeEntity::getStorageId).collect(Collectors.toList());
                    //查询计算节点历史数据
                    Map<Long, List<NodeHistoryDataDto>> nodeTaosDataByIds = nodeTaskService.findNodeTaosCountFunDataByIds(storageIdList, varNodeCuntFunQueryVo.getStartTime(), varNodeCuntFunQueryVo.getEndTime(), varNodeCuntFunQueryVo.getCuntFun(), varNodeCuntFunQueryVo.getTimeInterval());
                    //根据变量id分组
                    Map<String, List<VariableNodeEntity>> groupByVarIdMap = variableNodeEntities.stream().collect(Collectors.groupingBy(VariableNodeEntity::getVarId));
                    //循环系统变量获取数据
                    systemVariableEntityList.forEach(systemVariableEntity -> {
                        if (groupByVarIdMap.containsKey(systemVariableEntity.getId())) {
                            Map<String, List<NodeHistoryDataDto>> dataMap = Maps.newHashMap();
                            groupByVarIdMap.get(systemVariableEntity.getId()).forEach(variableNodeEntity -> {
                                List<NodeHistoryDataDto> nodeHistoryDataDtoList = Lists.newArrayList();
                                if (!nodeTaosDataByIds.isEmpty() && nodeTaosDataByIds.containsKey(variableNodeEntity.getStorageId())) {
                                    nodeHistoryDataDtoList = nodeTaosDataByIds.get(variableNodeEntity.getStorageId());
                                }
                                dataMap.put(variableNodeEntity.getDeviceId(), nodeHistoryDataDtoList);
                            });
                            resultMap.put(systemVariableEntity.getVarCode(), dataMap);
                        }
                    });
                }
            }
        }
        return ResponseResult.ok(resultMap);
    }

    @Override
    public ResponseResult<Void> batchUpdateStrategyTask(List<StrategyTaskVo> strategyTaskVos) {
        log.info("批量修改自动策略任务入参:{}", strategyTaskVos);
        //根据多个站点id查询来源类型
        List<String> siteIds = strategyTaskVos.stream().map(StrategyTaskVo::getSiteId).distinct().collect(Collectors.toList());
        Map<String, SiteInfoDto> siteInfoMap = deviceService.findSiteBasicInfoByIds(siteIds).getData();
        strategyTaskVos.forEach(taskVo -> {
            Integer sourceType;
            if (siteInfoMap.containsKey(taskVo.getSiteId()) && StringUtil.isNotEmpty(siteInfoMap.get(taskVo.getSiteId()).getSourceType())) {
                sourceType = siteInfoMap.get(taskVo.getSiteId()).getSourceType();
            } else {
                sourceType = 1;
            }
            //添加或修改策略任务
            if (StringUtil.isNotEmpty(taskVo.getPeriod())) {
                scheduleTask.updateTask(() -> StrategyUtil.platformAutoControlTask(taskVo, sourceType), taskVo.getPeriod() * 1000, ScheduleTaskUtil.STRATEGY_TASK + taskVo.getId());
            }
        });
        return ResponseResult.ok();
    }

    @Override
    public ResponseResult<Void> batchDeleteStrategyTask(List<String> strategyTaskIds) {
        log.info("批量删除自动策略任务入参:{}", strategyTaskIds);
        //删除自动策略任务
        strategyTaskIds.forEach(taskId -> scheduleTask.removeTask(ScheduleTaskUtil.STRATEGY_TASK + taskId));
        return ResponseResult.ok();
    }

    /**
     * 根据站点/设备id查询所关联所有系统变量数据列表
     *
     * @param varCodes 多个系统变量编码(以逗号分割，不传则查询所关联全部系统变量)
     * @param queryId  设备/站点id
     * @return
     */
    @Override
    public ResponseResult<List<SystemVarDataDto>> findSystemVarDataListById(String varCodes, String queryId) {
        List<SystemVarDataDto> resultList = Lists.newArrayList();

        List<SystemVariableEntity> systemVariableEntityList = Lists.newArrayList();
        //判断是否传了系统变量标识，如果没有，则查询当前所传站点或多个设备下关联的所有系统变量标识
        if (StringUtil.isEmpty(varCodes)) {
            List<VariableNodeEntity> variableNodeEntityList = Lists.newArrayList();
            //查询设备基本信息，根据设备模型id查询一遍关联的系统变量信息
            ResponseResult<Map<String, DeviceBasicInfoDto>> deviceBasicInfoByIds = deviceService.findDeviceBasicInfoByIds(Collections.singletonList(queryId));
            if (deviceBasicInfoByIds.isSuccess() && !deviceBasicInfoByIds.getData().isEmpty() && deviceBasicInfoByIds.getData().containsKey(queryId)) {
                String modelId = deviceBasicInfoByIds.getData().get(queryId).getModelId();
                variableNodeEntityList = variableNodeDao.findAllByDeviceId(modelId);
            }
            //根据站点或设备id，查询系统变量标识
            variableNodeEntityList.addAll(variableNodeDao.findAllByDeviceId(queryId));
            if (CollectionUtils.isNotEmpty(variableNodeEntityList)) {
                List<String> varidList = variableNodeEntityList.stream().map(VariableNodeEntity::getVarId).distinct().collect(Collectors.toList());
                systemVariableEntityList = systemVariableDao.findAllById(varidList);
            }
        } else {
            systemVariableEntityList = systemVariableDao.findAllByVarCodeIn(Arrays.stream(varCodes.split(",")).map(String::trim).collect(Collectors.toList()));
        }
        if (CollectionUtils.isNotEmpty(systemVariableEntityList)) {
            //获取节点变量数据
            List<SystemVariableEntity> nodeVariables = filterVariables(systemVariableEntityList, 1);
            resultList.addAll(processNodeNewValue(nodeVariables, queryId));
            //获取模型功能点变量数据
            List<SystemVariableEntity> modelVariables = filterVariables(systemVariableEntityList, 2);
            resultList.addAll(processModelNewValue(modelVariables, queryId));
        }
        return ResponseResult.ok(resultList);
    }

    @Override
    public ResponseResult<Map<String, Map<String, List<NodeDifDataDto>>>> findNodeDifDataListFeign(VarNodeValueVo varNodeValueVo) {
        Map<String, Map<String, List<NodeDifDataDto>>> resultMap = Maps.newHashMap();

        List<SystemVariableEntity> systemVariableList = systemVariableDao.findAllByVarCodeIn(varNodeValueVo.getVarCodeList());

        //获取节点变量数据
        List<SystemVariableEntity> nodeVariables = filterVariables(systemVariableList, 1);

        if (CollectionUtils.isNotEmpty(nodeVariables)) {
            //根据变量id查询关联节点信息
            List<VariableNodeEntity> variableNodeEntityList = variableNodeDao.findAllByVarIdIn(nodeVariables.stream().map(SystemVariableEntity::getId).collect(Collectors.toList()));
            if (CollectionUtils.isNotEmpty(variableNodeEntityList)) {
                //过滤出需要的设备id数据
                List<VariableNodeEntity> variableNodeEntities = variableNodeEntityList.stream().filter(c -> varNodeValueVo.getDeviceIdList().stream().anyMatch(v -> Objects.equals(c.getDeviceId(), v))).collect(Collectors.toList());
                if (CollectionUtils.isNotEmpty(variableNodeEntities)) {
                    Map<String, SystemVariableEntity> systemVariableMap = nodeVariables.stream().collect(Collectors.toMap(SystemVariableEntity::getId, SystemVariableEntity -> SystemVariableEntity));
                    String startTime = varNodeValueVo.getStartTime();
                    String endTime = varNodeValueVo.getEndTime();
                    String timeInterval = varNodeValueVo.getTimeInterval();
                    List<Long> storageIdList = variableNodeEntities.stream().map(VariableNodeEntity::getStorageId).collect(Collectors.toList());
                    //查询指定节点表历史数据
                    NodeHistoryDataVo nodeHistoryDataVo = new NodeHistoryDataVo();
                    nodeHistoryDataVo.setStorageIdList(storageIdList);
                    nodeHistoryDataVo.setStartTime(startTime);
                    nodeHistoryDataVo.setEndTime(endTime);
                    nodeHistoryDataVo.setTimeInterval(timeInterval);
                    Map<Long, List<NodeHistoryDataDto>> nodeTaosDataByIds = nodeTaskService.findNodeTaosDiffDataByIds(nodeHistoryDataVo);
                    //根据设备id分组
                    Map<String, List<VariableNodeEntity>> groupByDeviceIdMap = variableNodeEntities.stream().collect(Collectors.groupingBy(VariableNodeEntity::getDeviceId));
                    groupByDeviceIdMap.forEach((deviceId, variableEntitieList) -> {
                        Map<String, List<NodeDifDataDto>> nodeHistoryDataMap = Maps.newHashMap();

                        variableEntitieList.forEach(variableNodeEntity -> {

                            String varCode = systemVariableMap.get(variableNodeEntity.getVarId()).getVarCode();

                            List<NodeDifDataDto> nodeHistoryDataDtos = nodeTaosDataByIds.get(variableNodeEntity.getStorageId()).stream().map(nodeHistoryDataDto -> {
                                NodeDifDataDto nodeDifDataDto = new NodeDifDataDto();
                                nodeDifDataDto.setVarCode(varCode);
                                nodeDifDataDto.setLastDateTime(nodeHistoryDataDto.getTs());
                                nodeDifDataDto.setLastDataValue(nodeHistoryDataDto.getResultValue());
                                nodeDifDataDto.setFirstDateTime(nodeHistoryDataDto.getFirstTs());
                                nodeDifDataDto.setFirstDataValue(nodeHistoryDataDto.getFirstResultValue());
                                return nodeDifDataDto;
                            }).collect(Collectors.toList());
                            nodeHistoryDataMap.put(varCode, nodeHistoryDataDtos);
                        });
                        resultMap.put(deviceId, nodeHistoryDataMap);
                    });
                }
            }
        }
        return ResponseResult.ok(resultMap);
    }

    @Override
    public ResponseResult<Map<String, List<ComputeNodeListDto>>> findComputeNodeListByDeviceIds(List<String> deviceIds) {
        //返回数据
        Map<String, List<ComputeNodeListDto>> resultMap = Maps.newHashMap();

        //根据多个设备id查询计算节点数据
        List<ComputeNodeEntity> computeNodeList = computeNodeDao.findAllByDeviceIdIn(deviceIds);
        if (CollectionUtils.isNotEmpty(computeNodeList)) {
            resultMap = computeNodeList.stream().map(computeNode -> {
                ComputeNodeListDto result = new ComputeNodeListDto();
                BeanUtils.copyProperties(computeNode, result);
                return result;
            }).collect(Collectors.toList()).stream().collect(Collectors.groupingBy(ComputeNodeListDto::getDeviceId));
        }
        return ResponseResult.ok(resultMap);
    }

    @Override
    public ResponseResult<Map<String, Map<String, List<NodeHistoryDataDto>>>> findDeviceNodeValueList(DeviceNodeValueVo deviceNodeValueVo) {
        Map<String, Map<String, List<NodeHistoryDataDto>>> resultMap = new HashMap<>();

        //根据查询条件查询设备节点数据
        if (MapUtils.isNotEmpty(deviceNodeValueVo.getDeviceNodeCodeMap())) {
            Map<String, List<ComputeNodeEntity>> computeNodeMap = Maps.newHashMap();
            deviceNodeValueVo.getDeviceNodeCodeMap().forEach((key, value) -> computeNodeMap.put(key, computeNodeDao.findAllByDeviceIdAndNodeCodeIn(key, value)));
            if (MapUtils.isNotEmpty(computeNodeMap)) {
                //根据查询条数查询计算节点历史数据
                NodeHistoryDataVo nodeHistoryDataVo = new NodeHistoryDataVo();
                nodeHistoryDataVo.setStorageIdList(computeNodeMap.entrySet().stream().flatMap(s -> s.getValue().stream()
                        .map(ComputeNodeEntity::getStorageId)).collect(Collectors.toList()));
                nodeHistoryDataVo.setStartTime(deviceNodeValueVo.getStartTime());
                nodeHistoryDataVo.setEndTime(deviceNodeValueVo.getEndTime());
                nodeHistoryDataVo.setTimeInterval(deviceNodeValueVo.getTimeInterval());
                nodeHistoryDataVo.setLimitSize(deviceNodeValueVo.getLimitSize());
                Map<Long, List<NodeHistoryDataDto>> nodeHistoryDataMap = nodeTaskService.findNodeTaosDataByIds(nodeHistoryDataVo);
                computeNodeMap.forEach((deviceId, computeNodeList) -> {
                    Map<String, List<NodeHistoryDataDto>> nodeCodeValueMap = Maps.newHashMap();
                    computeNodeList.forEach(node -> {
                        if (nodeHistoryDataMap.containsKey(node.getStorageId())) {
                            nodeCodeValueMap.put(node.getNodeCode(), nodeHistoryDataMap.get(node.getStorageId()));
                        }
                    });
                    resultMap.put(deviceId, nodeCodeValueMap);
                });
            }
        }
        return ResponseResult.ok(resultMap);
    }

    /**
     * 查询计算节点系统变量最新值
     *
     * @param nodeVariables
     * @param deviceId
     * @return
     */
    private List<SystemVarDataDto> processNodeNewValue(List<SystemVariableEntity> nodeVariables, String deviceId) {
        List<SystemVarDataDto> resultList = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(nodeVariables)) {
            Map<String, ComputeNodeEntity> computeNodeEntityMap = Maps.newHashMap();
            Map<String, LocalCacheDto> localCacheDtoMap = Maps.newHashMap();
            Map<String, VariableNodeEntity> variableNodeEntityMap = Maps.newHashMap();
            //根据变量id查询关联节点信息
            List<VariableNodeEntity> variableNodeEntityList = variableNodeDao.findAllByVarIdIn(nodeVariables.stream().map(SystemVariableEntity::getId).collect(Collectors.toList()));
            if (CollectionUtils.isNotEmpty(variableNodeEntityList)) {
                //过滤出需要的设备id数据
                List<VariableNodeEntity> variableNodeEntities = variableNodeEntityList.stream().filter(c -> c.getDeviceId().equals(deviceId)).collect(Collectors.toList());
                if (CollectionUtils.isNotEmpty(variableNodeEntities)) {
                    List<String> nodeIdList = variableNodeEntities.stream().map(VariableNodeEntity::getNodeId).collect(Collectors.toList());
                    //根据节点id，查询计算节点信息
                    computeNodeEntityMap = computeNodeDao.findAllById(nodeIdList).stream().collect(Collectors.toMap(ComputeNodeEntity::getId, computeNodeEntity -> computeNodeEntity, (v1, v2) -> v1));
                    //根据多个计算节点id，查询本地缓存数据
                    localCacheDtoMap = computeNodeService.findLocalCacheDataByIds(String.join(",", nodeIdList)).getData();
                    //根据变量id转成map
                    variableNodeEntityMap = variableNodeEntities.stream().collect(Collectors.toMap(VariableNodeEntity::getVarId, variableNodeEntity -> variableNodeEntity, (v1, v2) -> v1));
                }
            }

            Map<String, VariableNodeEntity> finalVariableNodeEntityMap = variableNodeEntityMap;
            Map<String, ComputeNodeEntity> finalComputeNodeEntityMap = computeNodeEntityMap;
            Map<String, LocalCacheDto> finalLocalCacheDtoMap = localCacheDtoMap;
            resultList = nodeVariables.stream().map(systemVariableEntity -> {
                SystemVarDataDto systemVarDataDto = new SystemVarDataDto();
                BeanUtils.copyProperties(systemVariableEntity, systemVarDataDto);
                //获取当前变量所关联的关联信息
                if (!finalVariableNodeEntityMap.isEmpty() && finalVariableNodeEntityMap.containsKey(systemVariableEntity.getId())) {
                    String nodeId = finalVariableNodeEntityMap.get(systemVariableEntity.getId()).getNodeId();
                    //根据节点id获取计算节点信息
                    if (StringUtil.isNotEmpty(nodeId) && !finalComputeNodeEntityMap.isEmpty() && finalComputeNodeEntityMap.containsKey(nodeId)) {
                        //获取单位
                        systemVarDataDto.setUnit(finalComputeNodeEntityMap.get(nodeId).getUnit());
                        //获取实时数据
                        if (!finalLocalCacheDtoMap.isEmpty() && finalLocalCacheDtoMap.containsKey(nodeId) && StringUtil.isNotEmpty(finalLocalCacheDtoMap.get(nodeId))) {
                            systemVarDataDto.setValue(getToDouble(finalLocalCacheDtoMap.get(nodeId).getResultValue()));
                        }
                    }
                }
                return systemVarDataDto;
            }).collect(Collectors.toList());
        }
        return resultList;
    }

    /**
     * 查询模型功能点系统变量最新值
     *
     * @param modelVariables
     * @param deviceId
     * @return
     */
    private List<SystemVarDataDto> processModelNewValue(List<SystemVariableEntity> modelVariables, String deviceId) {
        List<SystemVarDataDto> resultList = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(modelVariables)) {
            //根据设备id查询设备基本信息
            DeviceBasicInfoDto deviceBasicInfo = deviceService.findDeviceBasicInfoByIds(Collections.singletonList(deviceId)).getData().get(deviceId);
            //根据变量id查询关联节点信息
            List<VariableNodeEntity> variableNodeEntityList = variableNodeDao.findAllByVarIdIn(modelVariables.stream().map(SystemVariableEntity::getId).collect(Collectors.toList()));

            //分组模型关联功能点数据
            Map<String, List<VariableNodeEntity>> groupByModelIdMap = Maps.newHashMap();
            //多个功能点id
            List<String> functionIdList = Lists.newArrayList();
            if (CollectionUtils.isNotEmpty(variableNodeEntityList)) {
                //过滤出需要的模型id数据
                List<VariableNodeEntity> variableNodeEntities = variableNodeEntityList.stream().filter(c -> StringUtil.isNotEmpty(c.getDeviceId()) && c.getDeviceId().equals(deviceBasicInfo.getModelId())).collect(Collectors.toList());
                if (CollectionUtils.isNotEmpty(variableNodeEntities)) {
                    //根据模型id分组
                    groupByModelIdMap = variableNodeEntities.stream().collect(Collectors.groupingBy(VariableNodeEntity::getDeviceId));
                    //获取功能点id
                    functionIdList = variableNodeEntities.stream().map(VariableNodeEntity::getFunctionId).filter(StringUtil::isNotEmpty).distinct().collect(Collectors.toList());
                }
            }
            //根据多个功能点id查询功能点信息
            ResponseResult<Map<String, FunctionDetailDto>> functionDetailByIds = deviceService.findFunctionDetailByIds(functionIdList);
            Map<String, Map<String, RealDataModel>> deviceRealDataMap;
            if (functionDetailByIds.isSuccess() && !functionDetailByIds.getData().isEmpty()) {
                //多个功能点标识
                List<String> functionLogoList = new ArrayList<>(functionDetailByIds.getData().values()).stream().map(FunctionDetailDto::getFunctionLogo).collect(Collectors.toList());
                //查询功能点最新值数据
                deviceRealDataMap = deviceService.getDeviceFunctionsRealDataByIds(Collections.singleton(deviceId), String.join(",", functionLogoList)).getData();
            } else {
                deviceRealDataMap = Maps.newHashMap();
            }
            Map<String, List<VariableNodeEntity>> finalGroupByModelIdMap = groupByModelIdMap;
            resultList = modelVariables.stream().map(systemVariableEntity -> {
                SystemVarDataDto systemVarDataDto = new SystemVarDataDto();
                BeanUtils.copyProperties(systemVariableEntity, systemVarDataDto);
                //根据模型id，获取关联信息
                if (!finalGroupByModelIdMap.isEmpty() && finalGroupByModelIdMap.containsKey(deviceBasicInfo.getModelId())) {
                    Map<String, VariableNodeEntity> variableNodeEntityMap = finalGroupByModelIdMap.get(deviceBasicInfo.getModelId()).stream().collect(Collectors.toMap(VariableNodeEntity::getVarId, variableNodeEntity -> variableNodeEntity, (k1, k2) -> k1));
                    if (variableNodeEntityMap.containsKey(systemVariableEntity.getId())) {
                        //功能点id
                        String functionId = variableNodeEntityMap.get(systemVariableEntity.getId()).getFunctionId();
                        //获取功能点标识
                        if (functionDetailByIds.isSuccess() && functionDetailByIds.getData().containsKey(functionId)) {
                            //功能点基本信息
                            FunctionDetailDto functionDetailDto = functionDetailByIds.getData().get(functionId);
                            if (!deviceRealDataMap.isEmpty() && deviceRealDataMap.containsKey(deviceId)) {
                                //获取设备实时数据
                                Map<String, RealDataModel> realDataModelMap = deviceRealDataMap.get(deviceId);
                                String functionLogo = functionDetailDto.getFunctionLogo();
                                //根据功能点标识获取最新值数据
                                if (realDataModelMap.containsKey(functionLogo)) {
                                    //获取功能点实时数据
                                    RealDataModel realDataModel = realDataModelMap.get(functionLogo);
                                    systemVarDataDto.setUnit(functionDetailDto.getUnit());
                                    systemVarDataDto.setValue(realDataModel.getDataValue());
                                }
                            }
                        }
                    }
                }
                return systemVarDataDto;
            }).collect(Collectors.toList());
        }
        return resultList;
    }
}
