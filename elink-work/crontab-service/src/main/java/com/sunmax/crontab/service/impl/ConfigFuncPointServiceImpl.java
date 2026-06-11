package com.sunmax.crontab.service.impl;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.sunmax.common.constant.ConfigDataTypeConstant;
import com.sunmax.common.constant.ConfigurListHeaderConstant;
import com.sunmax.common.dto.crontab.*;
import com.sunmax.common.dto.data.DeviceHistoryDto;
import com.sunmax.common.dto.device.DeviceBasicInfoDto;
import com.sunmax.common.dto.device.FunctionDetailDto;
import com.sunmax.common.dto.device.SiteInfoDto;
import com.sunmax.common.dto.operate.ConfigurationResultDto;
import com.sunmax.common.entity.BaseEntity;
import com.sunmax.common.model.RealDataModel;
import com.sunmax.common.util.DateUtil;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.util.StringUtil;
import com.sunmax.common.vo.crontab.*;
import com.sunmax.common.vo.data.DeviceHistoryQueryVo;
import com.sunmax.common.vo.data.DeviceIndexListQueryVo;
import com.sunmax.crontab.dao.ComputeNodeDao;
import com.sunmax.crontab.dao.SystemVariableDao;
import com.sunmax.crontab.dao.VariableNodeDao;
import com.sunmax.crontab.dto.SiteDeviceDataDto;
import com.sunmax.crontab.entity.ComputeNodeEntity;
import com.sunmax.crontab.entity.SystemVariableEntity;
import com.sunmax.crontab.entity.VariableNodeEntity;
import com.sunmax.crontab.service.ComputeNodeService;
import com.sunmax.crontab.service.ConfigFuncPointService;
import com.sunmax.crontab.service.NodeTaskService;
import com.sunmax.crontab.service.TogetherFeignService;
import com.sunmax.crontab.service.feign.DataService;
import com.sunmax.crontab.service.feign.DeviceService;
import com.sunmax.crontab.util.CrontabCommonUtil;
import com.sunmax.crontab.vo.SiteDeviceQueryVo;
import com.sunmax.crontab.websocket.ConfigFuncPointWebSocket;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.compress.utils.Lists;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static com.sunmax.common.util.DateUtil.*;
import static com.sunmax.common.util.DeviceUtil.dataTypeConvert;

@Slf4j
@Service
public class ConfigFuncPointServiceImpl implements ConfigFuncPointService {

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
    private TogetherFeignService togetherFeignService;

    @Autowired
    private ComputeNodeService computeNodeService;

    @Autowired
    private ComputeNodeDao computeNodeDao;

    @Override
    public ResponseResult<ConfigurationResultDto> findGunSystemVarChartData(GunSystemVarChartQueryVo chartQueryVo) {
        ConfigurationResultDto result = new ConfigurationResultDto();
        result.setDesc("查询电枪系统变量图表数据");
        String siteId = chartQueryVo.getSiteId();
        String deviceIds = chartQueryVo.getDeviceIds();
        if (StringUtil.isNotEmpty(siteId) && StringUtil.isNotEmpty(deviceIds)) {
            return ResponseResult.error("查询失败，站点id和设备id参数只能同时存在一个", result);
        }
        List<String> varCodeList = Arrays.stream(chartQueryVo.getVarCodes().split(",")).map(String::trim).collect(Collectors.toList());
        Map<String, ConfigurationResultDto.FieldData> dataMap = Maps.newHashMap();
        try {
            List<SystemVariableEntity> systemVariableList = systemVariableDao.findAllByVarCodeIn(varCodeList);
            //获取节点变量数据
            List<SystemVariableEntity> nodeVariables = filterVariables(systemVariableList, 1);
            //获取模型功能点变量数据
            List<SystemVariableEntity> modelVariables = filterVariables(systemVariableList, 2);

            //判断是根据多个设备id还是站点id查询设备信息数据
            List<DeviceBasicInfoDto> deviceBasicInfoDtoList = Lists.newArrayList();
            if (StringUtil.isNotEmpty(siteId)) {
                //根据站点id查询站点下所有设备信息
                ResponseResult<Map<String, List<DeviceBasicInfoDto>>> deviceBasicInfoBySiteIds =
                        deviceService.findDeviceBasicInfoBySiteIds(Collections.singletonList(siteId), null);
                if (deviceBasicInfoBySiteIds.isSuccess() && !deviceBasicInfoBySiteIds.getData().isEmpty()) {
                    deviceBasicInfoDtoList = deviceBasicInfoBySiteIds.getData().get(siteId);
                }
            } else if (StringUtil.isNotEmpty(deviceIds)) {
                List<String> deviceIdList = Arrays.stream(chartQueryVo.getDeviceIds().split(",")).map(String::trim).collect(Collectors.toList());
                ResponseResult<Map<String, DeviceBasicInfoDto>> deviceBasicInfoByIds = deviceService.findDeviceBasicInfoByIds(deviceIdList);
                if (deviceBasicInfoByIds.isSuccess() && !deviceBasicInfoByIds.getData().isEmpty()) {
                    deviceBasicInfoDtoList = new ArrayList<>(deviceBasicInfoByIds.getData().values());
                }
            }
            if (CollectionUtils.isNotEmpty(deviceBasicInfoDtoList)) {
                //计算节点历史数据
                List<String> deviceIdList = deviceBasicInfoDtoList.stream().map(DeviceBasicInfoDto::getId).collect(Collectors.toList());
                Map<String, List<ConfigurationResultDto.FieldData>> nodeVarDataMap = processNodeVariables(nodeVariables, chartQueryVo, deviceIdList);
                //模型功能点历史数据
                Map<String, List<ConfigurationResultDto.FieldData>> modelVarDataMap = processModelVariables(modelVariables, chartQueryVo, deviceIdList);
                //循环设备数据，组装数据
                deviceBasicInfoDtoList.forEach(deviceBasicInfoDto -> {
                    ConfigurationResultDto.FieldData fieldData = new ConfigurationResultDto.FieldData();
                    fieldData.setChName(deviceBasicInfoDto.getDeviceName());
                    fieldData.setEnName(deviceBasicInfoDto.getId());
                    fieldData.setFieldType(ConfigDataTypeConstant.ARRAYLIST);
                    List<ConfigurationResultDto.FieldData> fieldDataList = Lists.newArrayList();
                    //获取计算节点历史数据
                    if (nodeVarDataMap.containsKey(deviceBasicInfoDto.getId())) {
                        fieldDataList.addAll(nodeVarDataMap.get(deviceBasicInfoDto.getId()));
                    }
                    //获取模型功能点历史数据
                    if (modelVarDataMap.containsKey(deviceBasicInfoDto.getId())) {
                        fieldDataList.addAll(modelVarDataMap.get(deviceBasicInfoDto.getId()));
                    }
                    fieldData.setFieldData(fieldDataList);
                    dataMap.put(deviceBasicInfoDto.getId(), fieldData);
                });
            }
            result.setDataMap(dataMap);
        } catch (RuntimeException e) {
            log.error("查询电枪系统变量图表数据失败", e);
            result.setDataMap(dataMap);
            return ResponseResult.error("程序出现异常", result);
        }
        return ResponseResult.ok(result);
    }

    private List<SystemVariableEntity> filterVariables(List<SystemVariableEntity> systemVariableList, Integer dataSource) {
        return systemVariableList.stream()
                .filter(c -> Objects.equals(c.getDataSource(), dataSource))
                .collect(Collectors.toList());
    }

    //获取节点变量数据
    private Map<String, List<ConfigurationResultDto.FieldData>> processNodeVariables(List<SystemVariableEntity> nodeVariables, GunSystemVarChartQueryVo chartQueryVo, List<String> deviceIdList) {
        Map<String, List<ConfigurationResultDto.FieldData>> fieldDataMap = Maps.newHashMap();
        if (CollectionUtils.isNotEmpty(nodeVariables)) {
            //根据变量id查询关联节点信息
            List<VariableNodeEntity> variableNodeEntityList = variableNodeDao.findAllByVarIdIn(nodeVariables.stream().map(SystemVariableEntity::getId).collect(Collectors.toList()));
            if (CollectionUtils.isNotEmpty(variableNodeEntityList)) {
                //过滤出需要的设备id数据
                List<VariableNodeEntity> variableNodeEntities = variableNodeEntityList.stream().filter(c -> deviceIdList.stream().anyMatch(v -> Objects.equals(c.getDeviceId(), v))).collect(Collectors.toList());
                if (CollectionUtils.isNotEmpty(variableNodeEntities)) {
                    Map<String, SystemVariableEntity> systemVariableMap = nodeVariables.stream().collect(Collectors.toMap(SystemVariableEntity::getId, SystemVariableEntity -> SystemVariableEntity));
                    String startTime = getStartTimeByQueryType(chartQueryVo.getDateType());
                    String endTime = localDateTimeToStr(LocalDateTime.now());
                    List<Long> storageIdList = variableNodeEntities.stream().map(VariableNodeEntity::getStorageId).collect(Collectors.toList());
                    //查询指定节点表历史数据
                    NodeHistoryDataVo nodeHistoryDataVo = new NodeHistoryDataVo();
                    nodeHistoryDataVo.setStorageIdList(storageIdList);
                    nodeHistoryDataVo.setStartTime(startTime);
                    nodeHistoryDataVo.setEndTime(endTime);
                    nodeHistoryDataVo.setLimitSize(chartQueryVo.getLimitSize());
                    Map<Long, List<NodeHistoryDataDto>> nodeTaosDataByIds = nodeTaskService.findNodeTaosDataByIds(nodeHistoryDataVo);
                    //根据设备id分组
                    Map<String, List<VariableNodeEntity>> groupByDeviceIdMap = variableNodeEntities.stream().collect(Collectors.groupingBy(VariableNodeEntity::getDeviceId));
                    groupByDeviceIdMap.forEach((deviceId, variableEntitieList) -> {
                        List<ConfigurationResultDto.FieldData> fieldDataList = Lists.newArrayList();
                        variableEntitieList.forEach(variableNodeEntity -> {
                            SystemVariableEntity systemVariableEntity = systemVariableMap.get(variableNodeEntity.getVarId());
                            //存储系统变量信息
                            ConfigurationResultDto.FieldData fieldData = new ConfigurationResultDto.FieldData();
                            fieldData.setChName(systemVariableEntity.getVarName());
                            fieldData.setEnName(systemVariableEntity.getVarCode());
                            fieldData.setFieldType(ConfigDataTypeConstant.CURVEMAP);
                            //存储dataList数据Map
                            Map<String, Object> dataMap = Maps.newHashMap();
                            List<String> tsList = Lists.newArrayList();
                            List<Double> valueList = Lists.newArrayList();
                            List<NodeHistoryDataDto> nodeHistoryDataDtoList = nodeTaosDataByIds.get(variableNodeEntity.getStorageId());
                            if (nodeTaosDataByIds.containsKey(variableNodeEntity.getStorageId()) && CollectionUtils.isNotEmpty(nodeTaosDataByIds.get(variableNodeEntity.getStorageId()))) {
                                //获取时间轴
                                tsList = nodeHistoryDataDtoList.stream().map(NodeHistoryDataDto::getTs).collect(Collectors.toList());
                                //获取数据轴
                                valueList = nodeHistoryDataDtoList.stream().map(NodeHistoryDataDto::getResultValue).collect(Collectors.toList());
                            }
                            dataMap.put("datelist", tsList);
                            //存储枪数据
                            Map<String, Object> gunDataMap = Maps.newHashMap();
                            gunDataMap.put("0", valueList);
                            dataMap.put("dataList", gunDataMap);
                            fieldData.setFieldData(dataMap);
                            fieldDataList.add(fieldData);
                        });
                        fieldDataMap.put(deviceId, fieldDataList);
                    });
                }
            }
        }
        return fieldDataMap;
    }

    //获取模型功能点变量数据
    private Map<String, List<ConfigurationResultDto.FieldData>> processModelVariables(List<SystemVariableEntity> modelVariables, GunSystemVarChartQueryVo chartQueryVo, List<String> deviceIdList) {
        Map<String, List<ConfigurationResultDto.FieldData>> fieldDataMap = Maps.newHashMap();
        if (CollectionUtils.isNotEmpty(modelVariables)) {
            //根据变量id转成map
            Map<String, SystemVariableEntity> systemVariableEntityMap = modelVariables.stream().collect(Collectors.toMap(SystemVariableEntity::getId, systemVariableEntity -> systemVariableEntity));
            //根据变量id查询关联节点信息
            List<VariableNodeEntity> variableNodeEntityList = variableNodeDao.findAllByVarIdIn(modelVariables.stream().map(SystemVariableEntity::getId).collect(Collectors.toList()));
            if (CollectionUtils.isNotEmpty(variableNodeEntityList)) {
                Map<String, DeviceBasicInfoDto> deviceBasicInfoDtoMap = deviceService.findDeviceBasicInfoByIds(deviceIdList).getData();
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
                        //多个功能点标识
                        Set<String> functionLogoList = new ArrayList<>(functionDetailDtoMap.values()).stream().map(FunctionDetailDto::getFunctionLogo).collect(Collectors.toSet());
                        //多个枪编码
                        List<String> gunCodes = Arrays.stream(chartQueryVo.getGunCodes().split(",")).map(String::trim).collect(Collectors.toList());
                        //开始结束时间
                        String startTime = getStartTimeByQueryType(chartQueryVo.getDateType());
                        String endTime = localDateTimeToStr(LocalDateTime.now());
                        //查询所选索引的设备历史数据
                        DeviceIndexListQueryVo deviceIndexQueryVo = new DeviceIndexListQueryVo();
                        Map<String, Set<String>> deviceFuctionMap = Maps.newHashMap();
                        deviceIdList.forEach(deviceId -> deviceFuctionMap.put(deviceId, functionLogoList));
                        deviceIndexQueryVo.setDeviceFuctionMap(deviceFuctionMap);
                        deviceIndexQueryVo.setStartTime(startTime);
                        deviceIndexQueryVo.setEndTime(endTime);
                        deviceIndexQueryVo.setLimitSize(chartQueryVo.getLimitSize());
                        deviceIndexQueryVo.setGunCodeList(gunCodes);
                        deviceIndexQueryVo.setTimeInterval("30s");
                        //根据查询条件查询多个电枪功能点数据
                        ResponseResult<Map<String, Map<String, Map<String, List<DeviceHistoryDto>>>>> gunHistorValueFeign = dataService.findIndexListHistorValueFeign(deviceIndexQueryVo);
                        if (gunHistorValueFeign.isSuccess() && !gunHistorValueFeign.getData().isEmpty()) {
                            Map<String, Map<String, Map<String, List<DeviceHistoryDto>>>> historyValueListData = gunHistorValueFeign.getData();
                            //循环设备信息组装数据
                            deviceBasicInfoDtoMap.forEach((deviceId, deviceBasicInfoDto) -> {
                                List<ConfigurationResultDto.FieldData> fieldDataList = Lists.newArrayList();
                                if (historyValueListData.containsKey(deviceId)) {
                                    Map<String, Map<String, List<DeviceHistoryDto>>> deviceHistoryMap = historyValueListData.get(deviceId);
                                    //根据变量id转成map
                                    Map<String, VariableNodeEntity> variableNodeEntityMap = groupByModelIdMap.get(deviceBasicInfoDto.getModelId()).stream().collect(Collectors.toMap(VariableNodeEntity::getVarId, variableNodeEntity -> variableNodeEntity, (k1, k2) -> k1));
                                    //循环系统变量获取数据
                                    systemVariableEntityMap.forEach((varId, systemVariableEntity) -> {
                                        //存储系统变量信息
                                        ConfigurationResultDto.FieldData fieldData = new ConfigurationResultDto.FieldData();
                                        fieldData.setChName(systemVariableEntity.getVarName());
                                        fieldData.setEnName(systemVariableEntity.getVarCode());
                                        fieldData.setFieldType(ConfigDataTypeConstant.CURVEMAP);
                                        //存储dataList数据Map
                                        Map<String, Object> dataMap = Maps.newHashMap();
                                        AtomicReference<List<String>> tsList = new AtomicReference<>(Lists.newArrayList());
                                        //存储枪数据
                                        Map<String, Object> gunDataMap = Maps.newHashMap();
                                        //循环电枪编码获取每个枪数据
                                        gunCodes.forEach(gunCode -> {
                                            List<Double> valueList = Lists.newArrayList();
                                            //获取变量关联信息
                                            VariableNodeEntity variableNodeEntity = variableNodeEntityMap.get(varId);
                                            if (StringUtil.isNotEmpty(variableNodeEntity)) {
                                                //获取功能点信息
                                                FunctionDetailDto functionDetailDto = functionDetailDtoMap.get(variableNodeEntity.getFunctionId());
                                                if (StringUtil.isNotEmpty(functionDetailDto)) {
                                                    //判断历史数据中是否包含当前功能点当前枪的数据
                                                    if (deviceHistoryMap.containsKey(functionDetailDto.getFunctionLogo()) && deviceHistoryMap.get(functionDetailDto.getFunctionLogo()).containsKey(gunCode)) {
                                                        List<DeviceHistoryDto> deviceHistoryDtoList = deviceHistoryMap.get(functionDetailDto.getFunctionLogo()).get(gunCode);
                                                        //转换list对象
                                                        List<NodeHistoryDataDto> convertedDataList = deviceHistoryDtoList.stream()
                                                                .map(this::createNodeHistoryDataDto)
                                                                .collect(Collectors.toList());
                                                        if (CollectionUtils.isNotEmpty(convertedDataList)) {
                                                            //获取时间轴
                                                            tsList.set(convertedDataList.stream().map(NodeHistoryDataDto::getTs).collect(Collectors.toList()));
                                                            //获取数据轴
                                                            valueList = convertedDataList.stream().map(NodeHistoryDataDto::getResultValue).collect(Collectors.toList());
                                                        }
                                                    }
                                                }
                                            }
                                            gunDataMap.put(gunCode, valueList);
                                        });
                                        dataMap.put("datelist", tsList.get());
                                        dataMap.put("dataList", gunDataMap);
                                        fieldData.setFieldData(dataMap);
                                        fieldDataList.add(fieldData);
                                    });
                                }
                                fieldDataMap.put(deviceId, fieldDataList);
                            });
                        }
                    }
                }
            }
        }
        return fieldDataMap;
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

    @Override
    public ResponseResult<ConfigurationResultDto> findDeviceSystemVarChartData(DeviceVarChartQueryVo deviceChartQueryVo) {
        ConfigurationResultDto result = new ConfigurationResultDto();
        result.setDesc("查询站点/设备系统变量图表数据");
        List<String> varCodeList = Arrays.stream(deviceChartQueryVo.getVarCodes().split(",")).map(String::trim).collect(Collectors.toList());
        String siteId = deviceChartQueryVo.getSiteId();
        String deviceIdsStr = deviceChartQueryVo.getDeviceIds();
        if (StringUtil.isNotEmpty(siteId) && StringUtil.isNotEmpty(deviceIdsStr)) {
            return ResponseResult.error("查询失败，站点id和设备id参数只能同时存在一个", result);
        }
        Map<String, SystemVariableEntity> systemVariableEntityMap = systemVariableDao.findAllByVarCodeIn(varCodeList).stream().collect(Collectors.toMap(SystemVariableEntity::getVarCode, systemVariableEntity -> systemVariableEntity));

        Map<String, ConfigurationResultDto.FieldData> dataMap = Maps.newHashMap();

        //开始结束时间
        String startTime = getStartTimeByQueryType(deviceChartQueryVo.getDateType());
        String endTime = getEndTimeByQueryType(deviceChartQueryVo.getDateType());
        try {
            //判断是根据多个设备id还是站点id查询设备信息数据
            if (StringUtil.isNotEmpty(siteId)) {
                //查询站点基本信息
                ResponseResult<Map<String, SiteInfoDto>> siteBasicInfoByIds = deviceService.findSiteBasicInfoByIds(Collections.singletonList(siteId));
                if (siteBasicInfoByIds.isSuccess() && !siteBasicInfoByIds.getData().isEmpty() && siteBasicInfoByIds.getData().containsKey(siteId)) {
                    SiteInfoDto siteInfoDto = siteBasicInfoByIds.getData().get(siteId);
                    //查询站点系统变量数据
                    VarNodeValueVo varNodeValueVo = new VarNodeValueVo();
                    varNodeValueVo.setDeviceIdList(Collections.singletonList(siteInfoDto.getId()));
                    varNodeValueVo.setVarCodeList(varCodeList);
                    varNodeValueVo.setLimitSize(deviceChartQueryVo.getLimitSize());
                    varNodeValueVo.setStartTime(startTime);
                    varNodeValueVo.setEndTime(endTime);
                    varNodeValueVo.setTimeInterval(deviceChartQueryVo.getTimeInterval());
                    ResponseResult<Map<String, Map<String, List<NodeHistoryDataDto>>>> deviceVarNodeValueByIds = togetherFeignService.findSiteVarNodeValueByIds(varNodeValueVo);
                    ConfigurationResultDto.FieldData fieldData = new ConfigurationResultDto.FieldData();
                    fieldData.setChName(siteInfoDto.getSiteName());
                    fieldData.setEnName(siteInfoDto.getId());
                    fieldData.setFieldType(ConfigDataTypeConstant.ARRAYLIST);
                    List<ConfigurationResultDto.FieldData> fieldDataList = Lists.newArrayList();
                    //循环系统变量获取数据
                    varCodeList.forEach(varCode -> {
                        if (systemVariableEntityMap.containsKey(varCode)) {
                            SystemVariableEntity systemVariableEntity = systemVariableEntityMap.get(varCode);
                            //存储系统变量信息
                            ConfigurationResultDto.FieldData varFieldData = new ConfigurationResultDto.FieldData();
                            varFieldData.setChName(systemVariableEntity.getVarName());
                            varFieldData.setEnName(systemVariableEntity.getVarCode());
                            varFieldData.setFieldType(ConfigDataTypeConstant.CURVEMAP);
                            //存储dataList数据Map
                            Map<String, Object> varDataMap = Maps.newHashMap();
                            List<String> tsList = Lists.newArrayList();
                            List<Double> valueList = Lists.newArrayList();
                            //存储枪数据
                            Map<String, Object> gunDataMap = Maps.newHashMap();
                            if (deviceVarNodeValueByIds.isSuccess() && !deviceVarNodeValueByIds.getData().isEmpty()
                                    && deviceVarNodeValueByIds.getData().containsKey(varCode)) {
                                Map<String, List<NodeHistoryDataDto>> varCodeNodeDataMap = deviceVarNodeValueByIds.getData().get(varCode);
                                if (!varCodeNodeDataMap.isEmpty() && varCodeNodeDataMap.containsKey(siteInfoDto.getId())) {
                                    List<NodeHistoryDataDto> nodeHistoryDataDtoList = varCodeNodeDataMap.get(siteInfoDto.getId());
                                    //获取时间轴
                                    tsList = nodeHistoryDataDtoList.stream().map(NodeHistoryDataDto::getTs).collect(Collectors.toList());
                                    //获取数据轴
                                    valueList = nodeHistoryDataDtoList.stream().map(NodeHistoryDataDto::getResultValue).collect(Collectors.toList());
                                }

                            }
                            gunDataMap.put("0", valueList);
                            varDataMap.put("datelist", tsList);
                            varDataMap.put("dataList", gunDataMap);
                            varFieldData.setFieldData(varDataMap);
                            fieldDataList.add(varFieldData);
                        }
                    });
                    fieldData.setFieldData(fieldDataList);
                    dataMap.put(siteInfoDto.getId(), fieldData);
                }
            } else if (StringUtil.isNotEmpty(deviceIdsStr)) {
                List<String> deviceIdList = Arrays.stream(deviceChartQueryVo.getDeviceIds().split(",")).map(String::trim).collect(Collectors.toList());
                ResponseResult<Map<String, DeviceBasicInfoDto>> deviceBasicInfoByIds = deviceService.findDeviceBasicInfoByIds(deviceIdList);
                if (deviceBasicInfoByIds.isSuccess() && !deviceBasicInfoByIds.getData().isEmpty()) {
                    List<DeviceBasicInfoDto> deviceBasicInfoDtoList = new ArrayList<>(deviceBasicInfoByIds.getData().values());
                    //查询设备系统变量数据
                    List<String> deviceIds = deviceBasicInfoDtoList.stream().map(DeviceBasicInfoDto::getId).collect(Collectors.toList());
                    VarNodeValueVo varNodeValueVo = new VarNodeValueVo();
                    varNodeValueVo.setDeviceIdList(deviceIds);
                    varNodeValueVo.setVarCodeList(varCodeList);
                    varNodeValueVo.setLimitSize(deviceChartQueryVo.getLimitSize());
                    varNodeValueVo.setStartTime(startTime);
                    varNodeValueVo.setEndTime(endTime);
                    varNodeValueVo.setTimeInterval(deviceChartQueryVo.getTimeInterval());
                    ResponseResult<Map<String, Map<String, List<NodeHistoryDataDto>>>> deviceVarNodeValueByIds = togetherFeignService.findDeviceVarNodeValueByIds(varNodeValueVo);
                    //循环设备数据，组装数据
                    deviceBasicInfoDtoList.forEach(deviceBasicInfoDto -> {
                        ConfigurationResultDto.FieldData fieldData = new ConfigurationResultDto.FieldData();
                        fieldData.setChName(deviceBasicInfoDto.getDeviceName());
                        fieldData.setEnName(deviceBasicInfoDto.getId());
                        fieldData.setFieldType(ConfigDataTypeConstant.ARRAYLIST);
                        List<ConfigurationResultDto.FieldData> fieldDataList = Lists.newArrayList();
                        if (deviceVarNodeValueByIds.isSuccess() && !deviceVarNodeValueByIds.getData().isEmpty()
                                && deviceVarNodeValueByIds.getData().containsKey(deviceBasicInfoDto.getId())) {
                            Map<String, List<NodeHistoryDataDto>> varCodeNodeDataMap = deviceVarNodeValueByIds.getData().get(deviceBasicInfoDto.getId());
                            //循环系统变量获取数据
                            varCodeList.forEach(varCode -> {
                                if (systemVariableEntityMap.containsKey(varCode)) {
                                    SystemVariableEntity systemVariableEntity = systemVariableEntityMap.get(varCode);
                                    //存储系统变量信息
                                    ConfigurationResultDto.FieldData varFieldData = new ConfigurationResultDto.FieldData();
                                    varFieldData.setChName(systemVariableEntity.getVarName());
                                    varFieldData.setEnName(systemVariableEntity.getVarCode());
                                    varFieldData.setFieldType(ConfigDataTypeConstant.CURVEMAP);
                                    //存储dataList数据Map
                                    Map<String, Object> varDataMap = Maps.newHashMap();
                                    List<String> tsList = Lists.newArrayList();
                                    List<Double> valueList = Lists.newArrayList();
                                    //存储枪数据
                                    Map<String, Object> gunDataMap = Maps.newHashMap();
                                    if (varCodeNodeDataMap.containsKey(varCode)) {
                                        List<NodeHistoryDataDto> nodeHistoryDataDtoList = varCodeNodeDataMap.get(varCode);
                                        //获取时间轴
                                        tsList = nodeHistoryDataDtoList.stream().map(NodeHistoryDataDto::getTs).collect(Collectors.toList());
                                        //获取数据轴
                                        valueList = nodeHistoryDataDtoList.stream().map(NodeHistoryDataDto::getResultValue).collect(Collectors.toList());
                                    }
                                    gunDataMap.put("0", valueList);
                                    varDataMap.put("datelist", tsList);
                                    varDataMap.put("dataList", gunDataMap);
                                    varFieldData.setFieldData(varDataMap);
                                    fieldDataList.add(varFieldData);
                                }
                            });
                        }
                        fieldData.setFieldData(fieldDataList);
                        dataMap.put(deviceBasicInfoDto.getId(), fieldData);
                    });
                }
            }
            result.setDataMap(dataMap);
        } catch (RuntimeException e) {
            log.error("查询站点/设备系统变量图表数据", e);
            result.setDataMap(dataMap);
            return ResponseResult.error("程序出现异常", result);
        }
        return ResponseResult.ok(result);
    }

    @Override
    public ResponseResult<ConfigurationResultDto> findSystemVarNewValue(SystemVarNewValueVo systemVarNewValueVo) {
        ConfigurationResultDto result = new ConfigurationResultDto();
        result.setDesc("查询系统变量最新值数据");
        String deviceIds = systemVarNewValueVo.getDeviceIds();
        String siteId = systemVarNewValueVo.getSiteId();
        String varCodes = systemVarNewValueVo.getVarCodes();
        if (StringUtil.isNotEmpty(siteId) && StringUtil.isNotEmpty(deviceIds)) {
            return ResponseResult.error("查询失败，站点id和设备id参数只能同时存在一个", result);
        }
        Map<String, ConfigurationResultDto.FieldData> dataMap = Maps.newHashMap();
        try {

            List<String> varCodeList = Arrays.stream(varCodes.split(",")).map(String::trim).collect(Collectors.toList());
            //根据系统变量编码查询系统变量基本信息
            List<SystemVariableEntity> systemVariableList = systemVariableDao.findAllByVarCodeIn(varCodeList);
            //获取节点变量数据
            List<SystemVariableEntity> nodeVariables = filterVariables(systemVariableList, 1);
            //获取模型功能点变量数据
            List<SystemVariableEntity> modelVariables = filterVariables(systemVariableList, 2);

            if (StringUtil.isNotEmpty(siteId)) {
                //查询站点基本信息
                ResponseResult<Map<String, SiteInfoDto>> siteBasicInfoByIds = deviceService.findSiteBasicInfoByIds(Collections.singletonList(siteId));
                if (siteBasicInfoByIds.isSuccess() && !siteBasicInfoByIds.getData().isEmpty() && siteBasicInfoByIds.getData().containsKey(siteId)) {
                    SiteInfoDto siteInfoDto = siteBasicInfoByIds.getData().get(siteId);
                    //查询站点节点数据
                    Map<String, List<ConfigurationResultDto.FieldData>> nodeVarDataMap = siteProcessNodeNewValue(nodeVariables, Collections.singletonList(siteId), varCodeList);
                    ConfigurationResultDto.FieldData fieldData = new ConfigurationResultDto.FieldData();
                    fieldData.setChName(siteInfoDto.getSiteName());
                    fieldData.setEnName(siteInfoDto.getId());
                    fieldData.setFieldType(ConfigDataTypeConstant.ARRAYLIST);
                    List<ConfigurationResultDto.FieldData> fieldDataList = Lists.newArrayList();
                    //获取计算节点数据
                    if (nodeVarDataMap.containsKey(siteInfoDto.getId())) {
                        fieldDataList.addAll(nodeVarDataMap.get(siteInfoDto.getId()));
                    }
                    fieldData.setFieldData(fieldDataList);
                    dataMap.put(siteInfoDto.getId(), fieldData);
                }
            } else if (StringUtil.isNotEmpty(deviceIds)) {
                List<String> deviceIdList = Arrays.stream(deviceIds.split(",")).map(String::trim).collect(Collectors.toList());
                ResponseResult<Map<String, DeviceBasicInfoDto>> deviceBasicInfoByIds = deviceService.findDeviceBasicInfoByIds(deviceIdList);
                if (deviceBasicInfoByIds.isSuccess() && !deviceBasicInfoByIds.getData().isEmpty()) {
                    List<DeviceBasicInfoDto> deviceBasicInfoDtoList = new ArrayList<>(deviceBasicInfoByIds.getData().values());
                    //查询设备系统变量数据
                    List<String> deviceIdLists = deviceBasicInfoDtoList.stream().map(DeviceBasicInfoDto::getId).collect(Collectors.toList());
                    //计算节点历史数据
                    Map<String, List<ConfigurationResultDto.FieldData>> nodeVarDataMap = processNodeNewValue(nodeVariables, deviceIdLists, varCodeList);
                    //模型功能点历史数据
                    Map<String, List<ConfigurationResultDto.FieldData>> modelVarDataMap = processModelNewValue(modelVariables, deviceIdLists, varCodeList);
                    //循环设备数据，组装数据
                    deviceBasicInfoDtoList.forEach(deviceBasicInfoDto -> {
                        ConfigurationResultDto.FieldData fieldData = new ConfigurationResultDto.FieldData();
                        fieldData.setChName(deviceBasicInfoDto.getDeviceName());
                        fieldData.setEnName(deviceBasicInfoDto.getId());
                        fieldData.setFieldType(ConfigDataTypeConstant.ARRAYLIST);
                        List<ConfigurationResultDto.FieldData> fieldDataList = Lists.newArrayList();
                        //获取计算节点历史数据
                        if (nodeVarDataMap.containsKey(deviceBasicInfoDto.getId())) {
                            fieldDataList.addAll(nodeVarDataMap.get(deviceBasicInfoDto.getId()));
                        }
                        //获取模型功能点历史数据
                        if (modelVarDataMap.containsKey(deviceBasicInfoDto.getId())) {
                            fieldDataList.addAll(modelVarDataMap.get(deviceBasicInfoDto.getId()));
                        }
                        fieldData.setFieldData(fieldDataList);
                        dataMap.put(deviceBasicInfoDto.getId(), fieldData);
                    });
                }
            }
            result.setDataMap(dataMap);
        } catch (RuntimeException e) {
            log.error("查询设备系统变量图表数据", e);
            result.setDataMap(dataMap);
            return ResponseResult.error("程序出现异常", result);
        }
        return ResponseResult.ok(result);
    }

    //查询模型功能点系统变量最新值
    private Map<String, List<ConfigurationResultDto.FieldData>> processModelNewValue(List<SystemVariableEntity> modelVariables, List<String> deviceIdList, List<String> varCodeList) {
        Map<String, List<ConfigurationResultDto.FieldData>> fieldDataMap = Maps.newHashMap();
        if (CollectionUtils.isNotEmpty(modelVariables)) {
            //根据变量标识转成map
            Map<String, SystemVariableEntity> systemVariableEntityMap = modelVariables.stream().collect(Collectors.toMap(SystemVariableEntity::getVarCode, systemVariableEntity -> systemVariableEntity));
            //根据设备id查询设备基本信息
            Map<String, DeviceBasicInfoDto> deviceBasicInfoDtoMap = deviceService.findDeviceBasicInfoByIds(deviceIdList).getData();
            //多个模型id
            List<String> modelIdList = new ArrayList<>(deviceBasicInfoDtoMap.values()).stream().map(DeviceBasicInfoDto::getModelId).distinct().collect(Collectors.toList());
            //根据变量id查询关联节点信息
            List<VariableNodeEntity> variableNodeEntityList = variableNodeDao.findAllByVarIdIn(modelVariables.stream().map(SystemVariableEntity::getId).collect(Collectors.toList()));

            //分组模型关联功能点数据
            Map<String, List<VariableNodeEntity>> groupByModelIdMap = Maps.newHashMap();
            //多个功能点id
            List<String> functionIdList = Lists.newArrayList();
            if (CollectionUtils.isNotEmpty(variableNodeEntityList)) {
                //过滤出需要的模型id数据
                List<VariableNodeEntity> variableNodeEntities = variableNodeEntityList.stream().filter(c -> modelIdList.stream().anyMatch(v -> Objects.equals(c.getDeviceId(), v))).collect(Collectors.toList());
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
                deviceRealDataMap = deviceService.getDeviceFunctionsRealDataByIds(new HashSet<>(deviceIdList), String.join(",", functionLogoList)).getData();
            } else {
                deviceRealDataMap = Maps.newHashMap();
            }
            //循环设备id获取数据
            Map<String, List<VariableNodeEntity>> finalGroupByModelIdMap = groupByModelIdMap;
            deviceIdList.forEach(deviceId -> {
                List<ConfigurationResultDto.FieldData> fieldDataList = Lists.newArrayList();
                DeviceBasicInfoDto deviceBasicInfoDto = deviceBasicInfoDtoMap.get(deviceId);
                //获取设备最新值
                if (!deviceRealDataMap.isEmpty() && deviceRealDataMap.containsKey(deviceId)) {
                    Map<String, RealDataModel> realDataModelMap = deviceRealDataMap.get(deviceId);
                    //获取模型关联功能点数据
                    if (!finalGroupByModelIdMap.isEmpty() && finalGroupByModelIdMap.containsKey(deviceBasicInfoDto.getModelId())) {
                        Map<String, VariableNodeEntity> variableNodeEntityMap = finalGroupByModelIdMap.get(deviceBasicInfoDto.getModelId()).stream().collect(Collectors.toMap(VariableNodeEntity::getVarId, variableNodeEntity -> variableNodeEntity, (k1, k2) -> k1));
                        //循环系统变量获取关联功能点数据
                        varCodeList.forEach(varCode -> {
                            SystemVariableEntity systemVariableEntity = systemVariableEntityMap.get(varCode);
                            //存储系统变量信息
                            ConfigurationResultDto.FieldData fieldData = new ConfigurationResultDto.FieldData();
                            fieldData.setChName(systemVariableEntity.getVarName());
                            fieldData.setEnName(systemVariableEntity.getVarCode());
                            //获取变量关联模型功能点id
                            if (variableNodeEntityMap.containsKey(systemVariableEntity.getId())) {
                                String functionId = variableNodeEntityMap.get(systemVariableEntity.getId()).getFunctionId();
                                //获取功能点标识
                                if (functionDetailByIds.isSuccess() && functionDetailByIds.getData().containsKey(functionId)) {
                                    FunctionDetailDto functionDetailDto = functionDetailByIds.getData().get(functionId);
                                    String functionLogo = functionDetailDto.getFunctionLogo();
                                    fieldData.setFieldDesc(functionDetailDto.getFunctionDesc());
                                    //根据功能点标识获取最新值数据
                                    if (realDataModelMap.containsKey(functionLogo)) {
                                        RealDataModel realDataModel = realDataModelMap.get(functionLogo);
                                        fieldData.setFieldData(realDataModel.getDataValue());
                                        fieldData.setFieldType(CrontabCommonUtil.dataTypeConvertStr(realDataModel.getDataType()));
                                    }
                                }
                            }
                            fieldDataList.add(fieldData);
                        });
                    }
                }
                fieldDataMap.put(deviceId, fieldDataList);
            });
        }
        return fieldDataMap;
    }

    //查询计算节点系统变量最新值
    private Map<String, List<ConfigurationResultDto.FieldData>> processNodeNewValue(List<SystemVariableEntity> nodeVariables, List<String> deviceIdList, List<String> varCodeList) {
        Map<String, List<ConfigurationResultDto.FieldData>> fieldDataMap = Maps.newHashMap();
        if (CollectionUtils.isNotEmpty(nodeVariables)) {
            //根据变量id查询关联节点信息
            List<VariableNodeEntity> variableNodeEntityList = variableNodeDao.findAllByVarIdIn(nodeVariables.stream().map(SystemVariableEntity::getId).collect(Collectors.toList()));
            if (CollectionUtils.isNotEmpty(variableNodeEntityList)) {
                //过滤出需要的设备id数据
                List<VariableNodeEntity> variableNodeEntities = variableNodeEntityList.stream().filter(c -> deviceIdList.stream().anyMatch(v -> Objects.equals(c.getDeviceId(), v))).collect(Collectors.toList());
                if (CollectionUtils.isNotEmpty(variableNodeEntities)) {
                    Map<String, SystemVariableEntity> systemVariableMap = nodeVariables.stream().collect(Collectors.toMap(SystemVariableEntity::getVarCode, SystemVariableEntity -> SystemVariableEntity));
                    List<String> nodeIdList = variableNodeEntities.stream().map(VariableNodeEntity::getNodeId).collect(Collectors.toList());
                    //根据多个计算节点id，查询本地缓存数据
                    ResponseResult<Map<String, LocalCacheDto>> localCacheDataByIds = computeNodeService.findLocalCacheDataByIds(String.join(",", nodeIdList));
                    //根据设备id分组
                    Map<String, List<VariableNodeEntity>> groupByDeviceIdMap = variableNodeEntities.stream().collect(Collectors.groupingBy(VariableNodeEntity::getDeviceId));
                    //根据多个节点id，查询节点信息数据
                    Map<String, ComputeNodeEntity> computeNodeEntityMap = computeNodeDao.findAllById(variableNodeEntities.stream().map(VariableNodeEntity::getNodeId).distinct().collect(Collectors.toList()))
                            .stream().collect(Collectors.toMap(ComputeNodeEntity::getId, computeNodeEntity -> computeNodeEntity, (k1, k2) -> k1));
                    //循环设备id，获取组装节点缓存数据
                    deviceIdList.forEach(deviceId -> {
                        List<ConfigurationResultDto.FieldData> fieldDataList = Lists.newArrayList();
                        //获取设备关联变量信息
                        if (groupByDeviceIdMap.containsKey(deviceId)) {
                            Map<String, VariableNodeEntity> variableNodeEntityMap = groupByDeviceIdMap.get(deviceId).stream().collect(Collectors.toMap(VariableNodeEntity::getVarId, variableNodeEntity -> variableNodeEntity, (k1, k2) -> k1));
                            //循环系统变量编码
                            varCodeList.forEach(varCode -> {
                                SystemVariableEntity systemVariableEntity = systemVariableMap.get(varCode);
                                //存储系统变量信息
                                ConfigurationResultDto.FieldData fieldData = new ConfigurationResultDto.FieldData();
                                fieldData.setChName(systemVariableEntity.getVarName());
                                fieldData.setEnName(systemVariableEntity.getVarCode());
                                fieldData.setFieldType(ConfigDataTypeConstant.DOUBLE);
                                if (variableNodeEntityMap.containsKey(systemVariableEntity.getId())) {
                                    String nodeId = variableNodeEntityMap.get(systemVariableEntity.getId()).getNodeId();
                                    fieldData.setFieldDesc(computeNodeEntityMap.get(nodeId).getRemark());
                                    //获取当前关联节点缓存数据
                                    if (localCacheDataByIds.isSuccess() && localCacheDataByIds.getData().containsKey(nodeId)) {
                                        LocalCacheDto localCacheDto = localCacheDataByIds.getData().get(nodeId);
                                        fieldData.setFieldData(localCacheDto.getResultValue());
                                    }
                                }
                                fieldDataList.add(fieldData);
                            });
                        }
                        fieldDataMap.put(deviceId, fieldDataList);
                    });
                }
            }
        }
        return fieldDataMap;
    }

    //查询计算节点站点系统变量最新值
    @Override
    public Map<String, List<ConfigurationResultDto.FieldData>> siteProcessNodeNewValue(List<SystemVariableEntity> nodeVariables, List<String> siteIdList, List<String> varCodeList) {
        Map<String, List<ConfigurationResultDto.FieldData>> fieldDataMap = Maps.newHashMap();
        if (CollectionUtils.isNotEmpty(nodeVariables)) {
            //根据变量id查询关联节点信息
            List<VariableNodeEntity> variableNodeEntityList = variableNodeDao.findAllByVarIdIn(nodeVariables.stream().map(SystemVariableEntity::getId).collect(Collectors.toList()));
            if (CollectionUtils.isNotEmpty(variableNodeEntityList)) {
                //过滤出需要的站点id数据
                List<VariableNodeEntity> variableNodeEntities = variableNodeEntityList.stream().filter(c -> siteIdList.stream().anyMatch(v -> Objects.equals(c.getDeviceId(), v))).collect(Collectors.toList());
                if (CollectionUtils.isNotEmpty(variableNodeEntities)) {
                    Map<String, SystemVariableEntity> systemVariableMap = nodeVariables.stream().collect(Collectors.toMap(SystemVariableEntity::getVarCode, SystemVariableEntity -> SystemVariableEntity));
                    List<String> nodeIdList = variableNodeEntities.stream().map(VariableNodeEntity::getNodeId).collect(Collectors.toList());
                    //根据多个计算节点id，查询本地缓存数据
                    ResponseResult<Map<String, LocalCacheDto>> localCacheDataByIds = computeNodeService.findLocalCacheDataByIds(String.join(",", nodeIdList));
                    //根据站点id分组
                    Map<String, List<VariableNodeEntity>> groupByDeviceIdMap = variableNodeEntities.stream().collect(Collectors.groupingBy(VariableNodeEntity::getDeviceId));
                    //根据多个节点id，查询节点信息数据
                    Map<String, ComputeNodeEntity> computeNodeEntityMap = computeNodeDao.findAllById(variableNodeEntities.stream().map(VariableNodeEntity::getNodeId).distinct().collect(Collectors.toList()))
                            .stream().collect(Collectors.toMap(ComputeNodeEntity::getId, computeNodeEntity -> computeNodeEntity, (k1, k2) -> k1));
                    //循环设备id，获取组装节点缓存数据
                    siteIdList.forEach(siteId -> {
                        List<ConfigurationResultDto.FieldData> fieldDataList = Lists.newArrayList();
                        //获取设备关联变量信息
                        if (groupByDeviceIdMap.containsKey(siteId)) {
                            Map<String, VariableNodeEntity> variableNodeEntityMap = groupByDeviceIdMap.get(siteId).stream().collect(Collectors.toMap(VariableNodeEntity::getVarId, variableNodeEntity -> variableNodeEntity, (k1, k2) -> k1));
                            //循环系统变量编码
                            varCodeList.forEach(varCode -> {
                                if (systemVariableMap.containsKey(varCode)) {
                                    SystemVariableEntity systemVariableEntity = systemVariableMap.get(varCode);
                                    //存储系统变量信息
                                    ConfigurationResultDto.FieldData fieldData = new ConfigurationResultDto.FieldData();
                                    fieldData.setChName(systemVariableEntity.getVarName());
                                    fieldData.setEnName(systemVariableEntity.getVarCode());
                                    fieldData.setFieldType(ConfigDataTypeConstant.DOUBLE);
                                    if (variableNodeEntityMap.containsKey(systemVariableEntity.getId())) {
                                        String nodeId = variableNodeEntityMap.get(systemVariableEntity.getId()).getNodeId();
                                        //根据节点id，获取节点描述
                                        fieldData.setFieldDesc(computeNodeEntityMap.get(nodeId).getRemark());
                                        //获取当前关联节点缓存数据
                                        if (localCacheDataByIds.isSuccess() && !localCacheDataByIds.getData().isEmpty() && localCacheDataByIds.getData().containsKey(nodeId)) {
                                            LocalCacheDto localCacheDto = localCacheDataByIds.getData().get(nodeId);
                                            if (StringUtil.isNotEmpty(localCacheDto)) {
                                                fieldData.setFieldData(localCacheDto.getResultValue());
                                            }
                                        }
                                    }
                                    fieldDataList.add(fieldData);
                                }
                            });
                        }
                        fieldDataMap.put(siteId, fieldDataList);
                    });
                }
            }
        }
        return fieldDataMap;
    }

    @Override
    public ResponseResult<SiteDeviceDataDto> findSiteDeviceDataList(SiteDeviceQueryVo siteDeviceQueryVo) {
        //返回的对象
        SiteDeviceDataDto result = new SiteDeviceDataDto();
        //开始结束时间
        String startTime = getStartTimeByQueryType(siteDeviceQueryVo.getDateType());
        String endTime = getEndTimeByQueryType(siteDeviceQueryVo.getDateType());
        String timeInterval = siteDeviceQueryVo.getTimeInterval();
        if (StringUtil.isNotEmpty(startTime) && StringUtil.isNotEmpty(endTime)) {
            Map<String, SiteDeviceQueryVo.VariableData> variableDataMap = siteDeviceQueryVo.getVariableDataMap();

            //根据多个设备id和多个变量标识查询变量数据
            Set<Map.Entry<String, SiteDeviceQueryVo.VariableData>> variableDataSet = variableDataMap.entrySet().stream()
                    .filter(s -> StringUtil.isNotEmpty(s.getValue().getNodeList())).collect(Collectors.toSet());
            Map<String, Set<String>> deviceVarCodeMap = variableDataSet.stream().collect(Collectors.toMap(Map.Entry::getKey,
                    s -> s.getValue().getNodeList()));
            List<String> varCodes = variableDataSet.stream().flatMap(s -> s.getValue().getNodeList().stream()).distinct()
                    .collect(Collectors.toList());

            //获取系统变量节点对象，系统变量模型对象
            List<SystemVariableEntity> sysVarList = systemVariableDao.findAllByVarCodeIn(varCodes);
            Map<String, String> varNameMap = sysVarList.stream().collect(Collectors.toMap(SystemVariableEntity::getVarCode,
                    SystemVariableEntity::getVarName, (k1, k2) -> k1));
            Map<String, String> systemNodeMap = sysVarList.stream().filter(s -> s.getDataSource() == 1).collect(Collectors
                    .toMap(SystemVariableEntity::getVarCode, BaseEntity::getId, (k1, k2) -> k1));
            Map<String, String> systemModeMap = sysVarList.stream().filter(s -> s.getDataSource() == 2).collect(Collectors
                    .toMap(SystemVariableEntity::getVarCode, BaseEntity::getId, (k1, k2) -> k1));
            List<String> varIds = sysVarList.stream().map(BaseEntity::getId).distinct().collect(Collectors.toList());
            Map<String, List<VariableNodeEntity>> deviceVarMap = variableNodeDao.findAllByVarIdIn(varIds).stream().collect(Collectors
                    .groupingBy(VariableNodeEntity::getDeviceId));

            Map<String, Map<String, Long>> deviceVarNodeIdMap = Maps.newHashMap();
            Map<String, Map<String, String>> deviceVarFunIdMap = Maps.newHashMap();
            Set<Long> nodeIds = Sets.newHashSet();
            Set<String> functionIds = Sets.newHashSet();
            deviceVarCodeMap.forEach((deviceId, varCodeList) -> {
                if (deviceVarMap.containsKey(deviceId)) {
                    Map<String, VariableNodeEntity> varMap = deviceVarMap.get(deviceId).stream().collect(Collectors.toMap(VariableNodeEntity::getVarId, a -> a, (k1, k2) -> k1));
                    Map<String, String> deviceNodeMap = varCodeList.stream().filter(systemNodeMap::containsKey).collect(Collectors.toMap(v -> v, systemNodeMap::get));
                    Map<String, String> deviceModeMap = varCodeList.stream().filter(systemModeMap::containsKey).collect(Collectors.toMap(v -> v, systemModeMap::get));

                    Map<String, Long> varNodeIdMap = Maps.newHashMap();
                    deviceNodeMap.forEach((varCode, varId) -> {
                        if (varMap.containsKey(varId)) {
                            Long nodeId = varMap.get(varId).getStorageId();
                            if (StringUtil.isNotEmpty(nodeId)) {
                                nodeIds.add(nodeId);
                                varNodeIdMap.put(varCode, nodeId);
                            }
                        }
                    });
                    deviceVarNodeIdMap.put(deviceId, varNodeIdMap);
                    Map<String, String> varFunIdMap = Maps.newHashMap();
                    deviceModeMap.forEach((varCode, varId) -> {
                        if (varMap.containsKey(varId)) {
                            String functionId = varMap.get(varId).getFunctionId();
                            if (StringUtil.isNotEmpty(functionId)) {
                                functionIds.add(functionId);
                                varFunIdMap.put(varCode, functionId);
                            }
                        }
                    });
                    deviceVarFunIdMap.put(deviceId, varFunIdMap);
                }
            });

            //查询设备系统变量节点历史数据
            Map<Long, List<NodeHistoryDataDto>> nodeDataMap = Maps.newHashMap();
            if (CollectionUtils.isNotEmpty(nodeIds)) {
                NodeHistoryDataVo nodeHistoryDataVo = new NodeHistoryDataVo();
                nodeHistoryDataVo.setStorageIdList(new ArrayList<>(nodeIds));
                nodeHistoryDataVo.setStartTime(startTime);
                nodeHistoryDataVo.setEndTime(endTime);
                nodeHistoryDataVo.setTimeInterval(timeInterval);
                nodeDataMap = nodeTaskService.findNodeTaosDataByIds(nodeHistoryDataVo);
            }

            //获取设备功能点数据
            Map<String, Map<String, List<DeviceHistoryDto>>> functionDataMap = Maps.newHashMap();
            Map<String, FunctionDetailDto> functionIdMap = Maps.newHashMap();
            Map<String, FunctionDetailDto> functionLogoMap = Maps.newHashMap();
            Set<String> deviceIds = new HashSet<>();
            Set<String> functionLogos = new HashSet<>();
            List<Map.Entry<String, SiteDeviceQueryVo.VariableData>> deviceVarList = variableDataMap.entrySet().stream().filter(s ->
                    StringUtil.isNotEmpty(s.getValue().getFunctionList())).collect(Collectors.toList());
            deviceVarList.forEach(variable -> {
                deviceIds.add(variable.getKey());
                functionLogos.addAll(variable.getValue().getFunctionList());
            });
            if (CollectionUtils.isNotEmpty(functionIds)) {
                //根据多个功能点id查询功能点标识
                functionIdMap = deviceService.findFunctionDetailByIds(new ArrayList<>(functionIds)).getData();
                if (MapUtils.isNotEmpty(functionIdMap)) {
                    deviceIds.addAll(deviceVarFunIdMap.keySet());
                    functionLogos.addAll(functionIdMap.values().stream().map(FunctionDetailDto::getFunctionLogo).collect(Collectors.toSet()));
                }
            }
            if (CollectionUtils.isNotEmpty(deviceIds) && CollectionUtils.isNotEmpty(functionLogos)) {
                //根据多个功能点标识查询功能点名称
                functionLogoMap = deviceService.findFunctionDetailByLogos(functionLogos).getData();
                //根据查询条件查询功能点数据
                DeviceHistoryQueryVo deviceQueryVo = new DeviceHistoryQueryVo();
                deviceQueryVo.setDeviceIds(deviceIds);
                deviceQueryVo.setFunctionLogos(functionLogos);
                deviceQueryVo.setStartTime(startTime);
                deviceQueryVo.setEndTime(endTime);
                deviceQueryVo.setTimeInterval(timeInterval);
                functionDataMap = dataService.findDeviceHistoryValueList(deviceQueryVo).getData();
            }

            //获取日期列表
            List<String> dateList = getLocalDateTimeBetween(DateUtil.strToLocalDateTime(startTime), DateUtil.strToLocalDateTime(endTime), timeInterval)
                    .stream().map(DateUtil::localDateTimeToStr).collect(Collectors.toList());
            result.setDateList(dateList);
            //对数据进行组装
            Map<String, List<SiteDeviceDataDto.DeviceData>> deviceDataMap = Maps.newHashMap();
            for (Map.Entry<String, SiteDeviceQueryVo.VariableData> entry : variableDataMap.entrySet()) {
                String deviceId = entry.getKey();
                SiteDeviceQueryVo.VariableData value = entry.getValue();
                if (value != null) {
                    List<SiteDeviceDataDto.DeviceData> resultList = Lists.newArrayList();
                    if (StringUtil.isNotEmpty(value.getNodeList())) {
                        Set<String> nodeList = value.getNodeList();
                        Map<String, Long> nodeCodeMap = Maps.newHashMap();
                        Map<String, String> funIdMap = Maps.newHashMap();
                        if (deviceVarNodeIdMap.containsKey(deviceId)) {
                            nodeCodeMap = deviceVarNodeIdMap.get(deviceId);
                        }
                        if (deviceVarFunIdMap.containsKey(deviceId)) {
                            funIdMap = deviceVarFunIdMap.get(deviceId);
                        }
                        if (MapUtils.isNotEmpty(nodeCodeMap) || MapUtils.isNotEmpty(funIdMap)) {
                            for (String nodeCode : nodeList) {
                                SiteDeviceDataDto.DeviceData deviceData = new SiteDeviceDataDto.DeviceData();
                                deviceData.setDataCode(nodeCode);
                                if (varNameMap.containsKey(nodeCode)) {
                                    deviceData.setChName(varNameMap.get(nodeCode));
                                }
                                deviceData.setDataType(1);
                                Map<String, Object> dataTimeMap = Maps.newHashMap();
                                //系统变量 计算节点数据
                                if (nodeCodeMap.containsKey(nodeCode) && nodeDataMap.containsKey(nodeCodeMap.get(nodeCode))) {
                                    dataTimeMap = nodeDataMap.get(nodeCodeMap.get(nodeCode)).stream().filter(n -> StringUtil.isNotEmpty(n.getTs())
                                            && StringUtil.isNotEmpty(n.getResultValue())).collect(Collectors.toMap(s -> s.getTs().substring(0, 19),
                                            NodeHistoryDataDto::getResultValue, (k1, k2) -> k1));
                                }
                                //系统变量 模型功能点数据
                                if (funIdMap.containsKey(nodeCode)) {
                                    if (functionIdMap.containsKey(funIdMap.get(nodeCode)) && functionDataMap.containsKey(deviceId)) {
                                        Map<String, List<DeviceHistoryDto>> functionLogoDataMap = functionDataMap.get(deviceId);
                                        String functionLogo = functionIdMap.get(funIdMap.get(nodeCode)).getFunctionLogo();
                                        if (StringUtil.isNotEmpty(functionLogo) && functionLogoDataMap.containsKey(functionLogo)) {
                                            dataTimeMap = functionLogoDataMap.get(functionLogo).stream().filter(s -> StringUtil.isNotEmpty(s.getDataValue()))
                                                    .collect(Collectors.toMap(DeviceHistoryDto::getDateTime, DeviceHistoryDto::getDataValue));
                                        }
                                    }
                                }
                                Map<String, Object> finalDataTimeMap = dataTimeMap;
                                List<Object> dataList = dateList.stream().map(date -> {
                                    if (finalDataTimeMap.containsKey(date)) {
                                        return finalDataTimeMap.get(date);
                                    }
                                    return null;
                                }).collect(Collectors.toList());
                                deviceData.setDataList(dataList);
                                resultList.add(deviceData);
                            }
                        }
                    }

                    //获取设备功能点数据
                    if (CollectionUtils.isNotEmpty(value.getFunctionList())) {
                        List<String> functionLogoList = value.getFunctionList().stream().distinct().collect(Collectors.toList());
                        Map<String, List<DeviceHistoryDto>> functionLogoDataMap = Maps.newHashMap();
                        if (functionDataMap.containsKey(deviceId)) {
                            functionLogoDataMap = functionDataMap.get(deviceId);
                        }
                        for (String functionLogo : functionLogoList) {
                            SiteDeviceDataDto.DeviceData deviceData = new SiteDeviceDataDto.DeviceData();
                            deviceData.setDataCode(functionLogo);
                            if (functionLogoMap.containsKey(functionLogo)) {
                                deviceData.setChName(functionLogoMap.get(functionLogo).getFunctionName());
                            }
                            deviceData.setDataType(2);
                            Map<String, Object> dataTimeMap = Maps.newHashMap();
                            if (functionLogoDataMap.containsKey(functionLogo)) {
                                dataTimeMap = functionLogoDataMap.get(functionLogo).stream().filter(s -> StringUtil.isNotEmpty(s.getDataValue()))
                                        .collect(Collectors.toMap(DeviceHistoryDto::getDateTime, DeviceHistoryDto::getDataValue));
                            }
                            Map<String, Object> finalDataTimeMap = dataTimeMap;
                            List<Object> dataList = dateList.stream().map(date -> {
                                if (finalDataTimeMap.containsKey(date)) {
                                    return finalDataTimeMap.get(date);
                                }
                                return null;
                            }).collect(Collectors.toList());
                            deviceData.setDataList(dataList);
                            resultList.add(deviceData);
                        }
                    }
                    deviceDataMap.put(deviceId, resultList);
                }
            }
            result.setDeviceDataMap(deviceDataMap);
        }
        return ResponseResult.ok(result);
    }

    @Override
    public ResponseResult<List<ComputeNodeListDto>> findComputeNodeListByDeviceId(String deviceId) {
        //返回的集合
        List<ComputeNodeListDto> resultList = Lists.newArrayList();
        List<ComputeNodeEntity> computeNodeList = computeNodeDao.findAllByDeviceId(deviceId);
        if (CollectionUtils.isNotEmpty(computeNodeList)) {
            resultList = computeNodeList.stream().map(computeNodeEntity -> {
                ComputeNodeListDto result = new ComputeNodeListDto();
                BeanUtils.copyProperties(computeNodeEntity, result);
                return result;
            }).collect(Collectors.toList());
        }
        return ResponseResult.ok(resultList);
    }

    /**
     * 推送组态功能点数据
     */
    @Override
    public void sendConfigFuncPointData() {
        ConfigFuncPointWebSocket.sendAllMessage();
    }

    @Override
    public ResponseResult<ConfigurationResultDto> findSiteListByUserId(SiteListQueryVo siteListQueryVo) {
        ConfigurationResultDto result = new ConfigurationResultDto();
        result.setDesc("查询站点列表数据");
        String userId = siteListQueryVo.getUserId();
        if (StringUtil.isEmpty(userId)) {
            return ResponseResult.error("查询失败，用户id为空", result);
        }
        Map<String, ConfigurationResultDto.FieldData> dataMap = Maps.newHashMap();
        try {
            ResponseResult<List<ConfigurSiteListDto>> siteListResult = deviceService.findSiteListByUserId(siteListQueryVo);
            if (siteListResult.isSuccess() && CollectionUtils.isNotEmpty(siteListResult.getData())) {
                ConfigurationResultDto.FieldData fieldData = getFieldData(siteListQueryVo, siteListResult);
                dataMap.put(siteListQueryVo.getUserId(), fieldData);
            }
            result.setDataMap(dataMap);
        } catch (RuntimeException e) {
            log.error("查询设备系统变量图表数据", e);
            result.setDataMap(dataMap);
            return ResponseResult.error("程序出现异常", result);
        }
        return ResponseResult.ok(result);
    }

    @Override
    public ResponseResult<List<SystemVarInfoDto>> findSystemVarListByDeviceId(String deviceId, Integer queryType) {
        List<SystemVarInfoDto> resultList = Lists.newArrayList();
        if (StringUtil.isNotEmpty(deviceId) && StringUtil.isNotEmpty(queryType)) {
            List<VariableNodeEntity> variableNodeEntityList = variableNodeDao.findAllByDeviceId(deviceId);
            //站点类型查询
            if (queryType == 2) {
                //查询当前设备所属模型id
                ResponseResult<Map<String, DeviceBasicInfoDto>> deviceBasicInfoByIds = deviceService.findDeviceBasicInfoByIds(Collections.singletonList(deviceId));
                if (deviceBasicInfoByIds.isSuccess() && !deviceBasicInfoByIds.getData().isEmpty() && deviceBasicInfoByIds.getData().containsKey(deviceId)) {
                    DeviceBasicInfoDto deviceBasicInfoDto = deviceBasicInfoByIds.getData().get(deviceId);
                    //根据设备所属模型id查询
                    List<VariableNodeEntity> variableNodeEntities = variableNodeDao.findAllByDeviceId(deviceBasicInfoDto.getModelId());
                    if (CollectionUtils.isNotEmpty(variableNodeEntities)) {
                        variableNodeEntityList.addAll(variableNodeEntities);
                    }
                }
            }
            if (CollectionUtils.isNotEmpty(variableNodeEntityList)) {
                //多个关联系统变量id
                List<String> varIdList = variableNodeEntityList.stream().map(VariableNodeEntity::getVarId).distinct().collect(Collectors.toList());
                //根据系统变量id查询系统变量信息
                resultList = systemVariableDao.findAllById(varIdList).stream().map(systemVariableEntity -> {
                    SystemVarInfoDto systemVarInfoDto = new SystemVarInfoDto();
                    BeanUtils.copyProperties(systemVariableEntity, systemVarInfoDto);
                    return systemVarInfoDto;
                }).collect(Collectors.toList());
            }
        }
        return ResponseResult.ok(resultList);
    }

    @NotNull
    private static ConfigurationResultDto.FieldData getFieldData(SiteListQueryVo siteListQueryVo, ResponseResult<List<ConfigurSiteListDto>> siteListResult) {
        List<ConfigurSiteListDto> siteListDtoList = siteListResult.getData();
        ConfigurationResultDto.FieldData fieldData = new ConfigurationResultDto.FieldData();
        fieldData.setChName(siteListQueryVo.getUserId());
        fieldData.setEnName(siteListQueryVo.getUserId());
        fieldData.setFieldType(ConfigDataTypeConstant.ARRAYMAP);
        Map<Object, Object> fieldDataMap = Maps.newHashMap();
        fieldDataMap.put("header", ConfigurListHeaderConstant.SITE_INFO_HEADER);
        fieldDataMap.put("data", siteListDtoList);
        fieldData.setFieldData(fieldDataMap);
        return fieldData;
    }
}
