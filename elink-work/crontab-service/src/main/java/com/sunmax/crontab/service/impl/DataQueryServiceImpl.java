package com.sunmax.crontab.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.sunmax.common.dto.crontab.NodeHistoryDataDto;
import com.sunmax.common.dto.data.DeviceHistoryDto;
import com.sunmax.common.dto.device.ModelFunctionListDto;
import com.sunmax.common.util.DateUtil;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.util.StringUtil;
import com.sunmax.common.vo.StaticParamVo;
import com.sunmax.common.vo.crontab.NodeHistoryDataVo;
import com.sunmax.common.vo.data.DeviceHistoryQueryVo;
import com.sunmax.common.vo.data.DeviceIndexQueryVo;
import com.sunmax.crontab.dao.ComputeNodeDao;
import com.sunmax.crontab.dto.DataQueryDto;
import com.sunmax.crontab.entity.ComputeNodeEntity;
import com.sunmax.crontab.service.DataQueryService;
import com.sunmax.crontab.service.NodeTaskService;
import com.sunmax.crontab.service.feign.DataService;
import com.sunmax.crontab.service.feign.DeviceService;
import com.sunmax.crontab.vo.DataQueryVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.sunmax.common.util.DateUtil.*;
import static com.sunmax.common.util.DeviceUtil.dataTypeConvert;
import static com.sunmax.common.util.DoubleUtil.getToDouble;

@Slf4j
@Service
public class DataQueryServiceImpl implements DataQueryService {

    @Autowired
    private NodeTaskService nodeTaskService;

    @Autowired
    private DataService dataService;

    @Autowired
    private DeviceService deviceService;

    @Autowired
    private ComputeNodeDao computeNodeDao;

    /**
     * 查询图标数据
     * @param dataQueryVo
     * @return
     */
    @Override
    public ResponseResult<DataQueryDto> findDataQueryList(DataQueryVo dataQueryVo) {
        //返回数据
        DataQueryDto dataQueryDto = new DataQueryDto();
        List<DataQueryDto.DataInfo> resultList = Lists.newArrayList();
        //时间间隔
        String timeInterval = "1m";
        String deviceId = dataQueryVo.getDeviceId();
        //功能点标识数据
        String functionLogos = dataQueryVo.getFunctionLogos();
        //多个数组类型功能点标识
        String arrayFunctionLogos = dataQueryVo.getArrayFunctionLogos();
        //计算节点id数据
        String nodeIds = dataQueryVo.getNodeIds();
        List<ComputeNodeEntity> computeNodeEntityList = null;
        if (StringUtil.isNotEmpty(nodeIds)) {
            List<String> nodeIdList = Arrays.asList(nodeIds.split(","));
            computeNodeEntityList = computeNodeDao.findAllById(nodeIdList);
            List<String> periodList = computeNodeEntityList.stream().map(ComputeNodeEntity::getCountPeriod).collect(Collectors.toList());
            List<String> mList = periodList.stream().filter(s -> s.contains(StaticParamVo.M)).collect(Collectors.toList());
            List<String> hList = periodList.stream().filter(s -> s.contains(StaticParamVo.H)).collect(Collectors.toList());
            List<String> dList = periodList.stream().filter(s -> s.contains(StaticParamVo.D)).collect(Collectors.toList());
            List<String> nList = periodList.stream().filter(s -> s.contains(StaticParamVo.N)).collect(Collectors.toList());
            List<String> yList = periodList.stream().filter(s -> s.contains(StaticParamVo.Y)).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(mList)) {
                mList.sort(Comparator.comparing(String::valueOf));
                timeInterval = mList.get(0);
            } else if (CollectionUtils.isNotEmpty(hList)) {
                hList.sort(Comparator.comparing(String::valueOf));
                timeInterval = hList.get(0);
            } else if (CollectionUtils.isNotEmpty(dList)) {
                dList.sort(Comparator.comparing(String::valueOf));
                timeInterval = dList.get(0);
            } else if (CollectionUtils.isNotEmpty(nList)) {
                nList.sort(Comparator.comparing(String::valueOf));
                timeInterval = nList.get(0);
            } else if (CollectionUtils.isNotEmpty(yList)) {
                yList.sort(Comparator.comparing(String::valueOf));
                timeInterval = yList.get(0);
            }
        }
        List<LocalDateTime> dateTimeList = getLocalDateTimeBetween(strToLocalDateTime(dataQueryVo.getStartTime()), strToLocalDateTime(dataQueryVo.getEndTime()), timeInterval);
        dataQueryDto.setXAXisList(dateTimeList.stream().map(DateUtil::localDateTimeToStr).collect(Collectors.toList()));
        if (StringUtil.isNotEmpty(functionLogos) || StringUtil.isNotEmpty(arrayFunctionLogos)) {
            ResponseResult<Map<String, List<ModelFunctionListDto>>> deviceFunctionListByIds = deviceService.findDeviceFunctionListByIds(Collections.singletonList(deviceId));
            if (deviceFunctionListByIds.isSuccess() && !deviceFunctionListByIds.getData().isEmpty()) {
                List<ModelFunctionListDto> modelFunctionListDtoList = deviceFunctionListByIds.getData().get(deviceId);
                if (CollectionUtils.isNotEmpty(modelFunctionListDtoList)) {
                    Map<String, String> functionNameMap = modelFunctionListDtoList.stream().collect(Collectors.toMap(ModelFunctionListDto::getFunctionLogo, ModelFunctionListDto::getFunctionName, (k1, k2) -> k1));
                    //普通功能点数据查询
                    if (StringUtil.isNotEmpty(functionLogos)) {
                        List<String> functionLogoList = Arrays.asList(functionLogos.split(","));
                        //查询设备和功能点历史数据
                        DeviceHistoryQueryVo deviceHistoryQueryVo = new DeviceHistoryQueryVo();
                        deviceHistoryQueryVo.setDeviceIds(Collections.singleton(deviceId));
                        deviceHistoryQueryVo.setFunctionLogos(new HashSet<>(functionLogoList));
                        deviceHistoryQueryVo.setStartTime(dataQueryVo.getStartTime());
                        deviceHistoryQueryVo.setEndTime(dataQueryVo.getEndTime());
                        deviceHistoryQueryVo.setTimeInterval(timeInterval);
//                        deviceHistoryQueryVo.setLimitSize(1440);
                        ResponseResult<Map<String, Map<String, List<DeviceHistoryDto>>>> deviceHistoryValueList = dataService.findDeviceHistoryValueList(deviceHistoryQueryVo);
                        if (deviceHistoryValueList.isSuccess() && !deviceHistoryValueList.getData().isEmpty()) {
                            Map<String, List<DeviceHistoryDto>> functionHistoryDataMap = deviceHistoryValueList.getData().get(deviceId);
                            //循环功能点历史数据并组装
                            functionHistoryDataMap.forEach((functionLogo, historyDataList) -> {
                                DataQueryDto.DataInfo dataInfo = new DataQueryDto.DataInfo();
                                dataInfo.setName(functionNameMap.get(functionLogo));
                                if (CollectionUtils.isNotEmpty(historyDataList)) {
                                    Map<String, DeviceHistoryDto> deviceHistoryDtoMap = new TreeMap<>(historyDataList.stream().collect(Collectors.toMap(DeviceHistoryDto::getDateTime, DeviceHistoryDto -> DeviceHistoryDto, (k1, k2) -> k1)));
                                    dateTimeList.forEach(dateTime -> {
                                        String dateTimeToStr = localDateTimeToStr(dateTime);
                                        DeviceHistoryDto deviceHistoryDto = deviceHistoryDtoMap.get(dateTimeToStr);
                                        if (StringUtil.isNotEmpty(deviceHistoryDto)) {
                                            Integer dataType = deviceHistoryDto.getDataType();
                                            Object dataValue = deviceHistoryDto.getDataValue();
                                            dataInfo.getDataList().add(getToDouble(dataTypeConvert(dataType, dataValue)));
                                        } else {
                                            dataInfo.getDataList().add(null);
                                        }
                                    });
                                }
                                resultList.add(dataInfo);
                            });
                        }
                    }
                    //索引功能点历史数据查询
                    if (StringUtil.isNotEmpty(arrayFunctionLogos)) {
                        List<String> arrayFunctionLogoList = Arrays.asList(arrayFunctionLogos.split(","));
                        Integer index = dataQueryVo.getIndex();
                        //查询所选索引的设备历史数据
                        DeviceIndexQueryVo deviceIndexQueryVo = new DeviceIndexQueryVo();
                        Map<String, Set<String>> deviceFuctionMap = Maps.newHashMap();
                        Set<String> fuctionSet = new HashSet<>();
                        arrayFunctionLogoList.forEach(functionLogo -> fuctionSet.add(functionLogo + "index" + index));
                        deviceFuctionMap.put(deviceId, fuctionSet);
                        deviceIndexQueryVo.setDeviceFuctionMap(deviceFuctionMap);
                        deviceIndexQueryVo.setStartTime(dataQueryVo.getStartTime());
                        deviceIndexQueryVo.setEndTime(dataQueryVo.getEndTime());
                        deviceIndexQueryVo.setTimeInterval(timeInterval);
//                        deviceIndexQueryVo.setLimitSize(1440);
                        ResponseResult<Map<String, Map<String, List<DeviceHistoryDto>>>> deviceHistoryIndexValueList = dataService.findDeviceHistoryIndexValueList(deviceIndexQueryVo);
                        if (deviceHistoryIndexValueList.isSuccess() && !deviceHistoryIndexValueList.getData().isEmpty()) {
                            Map<String, List<DeviceHistoryDto>> deviceHistoryMap = deviceHistoryIndexValueList.getData().get(deviceId);
                            //循环功能点历史数据并组装
                            arrayFunctionLogoList.forEach(functionLogo -> {
                                DataQueryDto.DataInfo dataInfo = new DataQueryDto.DataInfo();
                                dataInfo.setName(functionNameMap.get(functionLogo));
                                List<DeviceHistoryDto> historyDataList = deviceHistoryMap.get(functionLogo);
                                if (CollectionUtils.isNotEmpty(historyDataList)) {
                                    Map<String, DeviceHistoryDto> deviceHistoryDtoMap = historyDataList.stream().collect(Collectors.toMap(DeviceHistoryDto::getDateTime, DeviceHistoryDto -> DeviceHistoryDto, (k1, k2) -> k1));
                                    dateTimeList.forEach(dateTime -> {
                                        DeviceHistoryDto deviceHistoryDto = deviceHistoryDtoMap.get(localDateTimeToStr(dateTime));
                                        if (StringUtil.isNotEmpty(deviceHistoryDto)) {
                                            Integer dataType = deviceHistoryDto.getDataType();
                                            Object dataValue = deviceHistoryDto.getDataValue();
                                            dataInfo.getDataList().add(getToDouble(dataTypeConvert(dataType, dataValue)));
                                        } else {
                                            dataInfo.getDataList().add(null);
                                        }
                                    });
                                }
                                resultList.add(dataInfo);
                            });
                        }
                    }
                }
            }
        }
        //处理查询计算节点数据
        if (StringUtil.isNotEmpty(nodeIds)) {
            //获取所有存储id
            NodeHistoryDataVo nodeHistoryDataVo = new NodeHistoryDataVo();
            Map<Long, String> nodeNameMap;
            if (computeNodeEntityList != null && CollectionUtils.isNotEmpty(computeNodeEntityList)) {
                nodeHistoryDataVo.setStorageIdList(computeNodeEntityList.stream().map(ComputeNodeEntity::getStorageId).collect(Collectors.toList()));
                nodeNameMap = computeNodeEntityList.stream().collect(Collectors.toMap(ComputeNodeEntity::getStorageId, ComputeNodeEntity::getNodeName, (k1, k2) -> k1));
            } else {
                nodeNameMap = Maps.newHashMap();
            }
            nodeHistoryDataVo.setStartTime(dataQueryVo.getStartTime());
            nodeHistoryDataVo.setEndTime(dataQueryVo.getEndTime());
            nodeHistoryDataVo.setTimeInterval(timeInterval);
//            nodeHistoryDataVo.setLimitSize(1440);
            Map<Long, List<NodeHistoryDataDto>> nodeHistoryDataMap = nodeTaskService.findNodeTaosDataByIds(nodeHistoryDataVo);
            nodeHistoryDataMap.forEach((storageId, nodeHistoryDataDtoList) -> {
                DataQueryDto.DataInfo dataInfo = new DataQueryDto.DataInfo();
                dataInfo.setName(nodeNameMap.get(storageId));
                if (CollectionUtils.isNotEmpty(nodeHistoryDataDtoList)) {
                    Map<String, NodeHistoryDataDto> nodeHistoryDataDtoMap = nodeHistoryDataDtoList.stream().collect(Collectors.toMap(n -> n.getTs().substring(0, 19), NodeHistoryDataDto -> NodeHistoryDataDto, (k1, k2) -> k1));
                    dateTimeList.forEach(dateTime -> {
                        NodeHistoryDataDto nodeHistoryDataDto = nodeHistoryDataDtoMap.get(localDateTimeToStr(dateTime));
                        if (StringUtil.isNotEmpty(nodeHistoryDataDto)) {
                            dataInfo.getDataList().add(nodeHistoryDataDto.getResultValue() != null ? getToDouble(nodeHistoryDataDto.getResultValue()): null);
                        } else {
                            dataInfo.getDataList().add(null);
                        }
                    });
                }
                resultList.add(dataInfo);
            });
        }
        dataQueryDto.setDataInfoList(resultList);
        return ResponseResult.ok(dataQueryDto);
    }
}
