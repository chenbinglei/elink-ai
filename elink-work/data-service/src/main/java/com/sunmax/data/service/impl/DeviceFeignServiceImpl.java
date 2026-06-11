package com.sunmax.data.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.sunmax.common.dto.data.DeviceHistoryDto;
import com.sunmax.common.dto.data.NodeDifHistoryDto;
import com.sunmax.common.dto.device.DeviceFieldDto;
import com.sunmax.common.util.CommonUtil;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.util.oss.FileUtil;
import com.sunmax.common.vo.StaticParamVo;
import com.sunmax.common.vo.data.DeviceCountQueryVo;
import com.sunmax.common.vo.data.DeviceHistoryQueryVo;
import com.sunmax.common.vo.data.DeviceIndexListQueryVo;
import com.sunmax.common.vo.data.DeviceIndexQueryVo;
import com.sunmax.common.vo.device.DeviceFieldQueryVo;
import com.sunmax.data.mapper.tdengine.DataStoreMapper;
import com.sunmax.data.model.TableDataModel;
import com.sunmax.data.model.TableDifDataModel;
import com.sunmax.data.service.AccessDataService;
import com.sunmax.data.service.DeviceFeignService;
import com.sunmax.data.service.feign.DeviceService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class DeviceFeignServiceImpl implements DeviceFeignService {

    @Autowired
    private DataStoreMapper dataStoreMapper;

    @Autowired
    private DeviceService deviceService;

    @Autowired
    private AccessDataService accessDataService;

    @Override
    @Transactional(transactionManager = "tdengineTransactionManager", rollbackFor = Exception.class)
    public ResponseResult<Void> deleteAllDataStoreTable(Set<String> tableNames) {
        tableNames.forEach(tableName -> dataStoreMapper.deleteTable(tableName));
        return ResponseResult.ok();
    }

    @Override
    public ResponseResult<Map<String, Map<String, List<DeviceHistoryDto>>>> findDeviceHistoryValueList(DeviceHistoryQueryVo deviceQueryVo) {
        //返回的对象
        Map<String, Map<String, List<DeviceHistoryDto>>> resultMap = Maps.newHashMap();

        //1.获取查询条件参数
        Set<String> deviceIds = deviceQueryVo.getDeviceIds();
        Set<String> functionLogos = deviceQueryVo.getFunctionLogos();
        String startTime = deviceQueryVo.getStartTime();
        String endTime = deviceQueryVo.getEndTime();
        String timeInterval = deviceQueryVo.getTimeInterval();
        Integer limitSize = deviceQueryVo.getLimitSize();
        //根据查询条件查询设备字段数据
        Map<String, List<DeviceFieldDto>> deviceFieldMap = deviceService.queryDeviceFieldList(DeviceFieldQueryVo.builder().deviceIds(deviceIds)
                .functionLogos(functionLogos).build()).getData();
        if (MapUtils.isNotEmpty(deviceFieldMap)) {
            //按照表名进行字段分组查询
            List<DeviceFieldDto> deviceFieldList = deviceFieldMap.entrySet().stream().flatMap(s -> s.getValue().stream()).collect(Collectors.toList());
            Map<String, List<String>> tableFieldMap = deviceFieldList.stream().collect(Collectors.groupingBy(DeviceFieldDto::getTableName,
                    Collectors.mapping(DeviceFieldDto::getFieldName, Collectors.toList())));
            //根据表名和字段名查询表数据(表名称 -> (表字段名称 -> 数据))
            Map<String, Map<String, List<TableDataModel>>> tableDataMap = accessDataService.queryTableDataList(tableFieldMap, startTime, endTime, timeInterval, limitSize);
            deviceFieldMap.forEach((deviceId, deviceFields) -> {
                Map<String, List<DeviceHistoryDto>> deviceHistoryMap = Maps.newHashMap();
                for (DeviceFieldDto deviceField : deviceFields) {
                    if (tableDataMap.containsKey(deviceField.getTableName())) {
                        Map<String, List<TableDataModel>> dataMap = tableDataMap.get(deviceField.getTableName());
                        if (dataMap.containsKey(deviceField.getFieldName())) {
                            deviceHistoryMap.put(deviceField.getFunctionLogo(), dataMap.get(deviceField.getFieldName()).stream().map(data -> {
                                DeviceHistoryDto result = new DeviceHistoryDto();
                                result.setFunctionLogo(deviceField.getFunctionLogo());
                                result.setDataType(deviceField.getDataType());
                                result.setFieldName(data.getFieldName());
                                result.setDataValue(CommonUtil.getRangeValue(deviceField.getValueRange(), deviceField.getDataType(), deviceField.getDataObject(), data.getFieldValue()));
                                result.setDateTime(data.getDataTime());
                                return result;
                            }).collect(Collectors.toList()));
                            continue;
                        }
                    }
                    deviceHistoryMap.put(deviceField.getFunctionLogo(), Lists.newArrayList());
                }
                resultMap.put(deviceId, deviceHistoryMap);
            });
        }
        return ResponseResult.ok(resultMap);
    }

    /**
     * 查询设备功能点指定单个时间或时间段内的数据或临近值历史数据(可查询指定单个时间（单个值、临近值）或指定单个时间或时间段的原始数据(没加last或first函数)、数据)
     *
     * @param deviceQueryVo
     * @param isNear        是否查临近值 1-是
     * @return
     */
    @Override
    public ResponseResult<Map<String, Map<String, List<DeviceHistoryDto>>>> findDeviceDifferenceListFeign(DeviceHistoryQueryVo deviceQueryVo, Integer isNear) {
        //返回的对象
        Map<String, Map<String, List<DeviceHistoryDto>>> resultMap = Maps.newHashMap();

        //获取查询条件参数
        Set<String> deviceIds = deviceQueryVo.getDeviceIds();
        Set<String> functionLogos = deviceQueryVo.getFunctionLogos();
        String startTime = deviceQueryVo.getStartTime();
        String endTime = deviceQueryVo.getEndTime();
        String timeInterval = deviceQueryVo.getTimeInterval();
        //根据查询条件查询设备字段数据
        Map<String, List<DeviceFieldDto>> deviceFieldMap = deviceService.queryDeviceFieldList(DeviceFieldQueryVo.builder().deviceIds(deviceIds)
                .functionLogos(functionLogos).build()).getData();
        if (MapUtils.isNotEmpty(deviceFieldMap)) {
            //按照表名进行字段分组查询
            List<DeviceFieldDto> deviceFieldList = deviceFieldMap.entrySet().stream().flatMap(s -> s.getValue().stream()).collect(Collectors.toList());
            Map<String, List<String>> tableFieldMap = deviceFieldList.stream().collect(Collectors.groupingBy(DeviceFieldDto::getTableName,
                    Collectors.mapping(DeviceFieldDto::getFieldName, Collectors.toList())));
            //根据表名和字段名查询表数据(表名称 -> (表字段名称 -> 数据))
            Map<String, Map<String, List<TableDataModel>>> tableDataMap = accessDataService.queryDifferenceTableDataList(tableFieldMap, startTime, endTime, timeInterval, isNear);
            deviceFieldMap.forEach((deviceId, deviceFields) -> {
                Map<String, List<DeviceHistoryDto>> deviceHistoryMap = Maps.newHashMap();
                for (DeviceFieldDto deviceField : deviceFields) {
                    if (tableDataMap.containsKey(deviceField.getTableName())) {
                        Map<String, List<TableDataModel>> dataMap = tableDataMap.get(deviceField.getTableName());
                        if (dataMap.containsKey(deviceField.getFieldName())) {
                            deviceHistoryMap.put(deviceField.getFunctionLogo(), dataMap.get(deviceField.getFieldName()).stream().map(data -> {
                                DeviceHistoryDto result = new DeviceHistoryDto();
                                result.setFunctionLogo(deviceField.getFunctionLogo());
                                result.setDataType(deviceField.getDataType());
                                result.setFieldName(data.getFieldName());
                                result.setDataValue(CommonUtil.getRangeValue(deviceField.getValueRange(), deviceField.getDataType(), deviceField.getDataObject(), data.getFieldValue()));
                                result.setDateTime(data.getDataTime());
                                return result;
                            }).collect(Collectors.toList()));
                            continue;
                        }
                    }
                    deviceHistoryMap.put(deviceField.getFunctionLogo(), Lists.newArrayList());
                }
                resultMap.put(deviceId, deviceHistoryMap);
            });
        }
        return ResponseResult.ok(resultMap);
    }

    /**
     * 查询指定设备指定功能点和时间段内设备统计值函数历史数据
     *
     * @param deviceCountQueryVo
     * @return
     */
    @Override
    public ResponseResult<Map<String, Map<String, List<DeviceHistoryDto>>>> findDeviceCountFunListFeign(DeviceCountQueryVo deviceCountQueryVo) {
        //返回数据
        Map<String, Map<String, List<DeviceHistoryDto>>> resultMap = Maps.newHashMap();
        //获取查询条件参数
        Set<String> deviceIds = deviceCountQueryVo.getDeviceIds();
        Set<String> functionLogos = deviceCountQueryVo.getFunctionLogos();
        String startTime = deviceCountQueryVo.getStartTime();
        String endTime = deviceCountQueryVo.getEndTime();
        String timeInterval = deviceCountQueryVo.getTimeInterval();
        String cuntFun = deviceCountQueryVo.getCuntFun();
        //根据查询条件查询设备字段数据
        Map<String, List<DeviceFieldDto>> deviceFieldMap = deviceService.queryDeviceFieldList(DeviceFieldQueryVo.builder().deviceIds(deviceIds)
                .functionLogos(functionLogos).build()).getData();
        if (MapUtils.isNotEmpty(deviceFieldMap)) {
            //按照表名进行字段分组查询
            List<DeviceFieldDto> deviceFieldList = deviceFieldMap.entrySet().stream().flatMap(s -> s.getValue().stream()).collect(Collectors.toList());
            Map<String, List<String>> tableFieldMap = deviceFieldList.stream().collect(Collectors.groupingBy(DeviceFieldDto::getTableName,
                    Collectors.mapping(DeviceFieldDto::getFieldName, Collectors.toList())));
            //根据表名和字段名查询表数据(表名称 -> (表字段名称 -> 数据))
            Map<String, Map<String, List<TableDataModel>>> tableDataMap = accessDataService.queryCountTableDataList(tableFieldMap, startTime, endTime, timeInterval, cuntFun);
            deviceFieldMap.forEach((deviceId, deviceFields) -> {
                Map<String, List<DeviceHistoryDto>> deviceHistoryMap = Maps.newHashMap();
                for (DeviceFieldDto deviceField : deviceFields) {
                    if (tableDataMap.containsKey(deviceField.getTableName())) {
                        Map<String, List<TableDataModel>> dataMap = tableDataMap.get(deviceField.getTableName());
                        if (dataMap.containsKey(deviceField.getFieldName())) {
                            deviceHistoryMap.put(deviceField.getFunctionLogo(), dataMap.get(deviceField.getFieldName()).stream().map(data -> {
                                DeviceHistoryDto result = new DeviceHistoryDto();
                                result.setFunctionLogo(deviceField.getFunctionLogo());
                                result.setDataType(deviceField.getDataType());
                                result.setFieldName(data.getFieldName());
                                result.setDataValue(CommonUtil.getRangeValue(deviceField.getValueRange(), deviceField.getDataType(), deviceField.getDataObject(), data.getFieldValue()));
                                result.setDateTime(data.getDataTime());
                                return result;
                            }).collect(Collectors.toList()));
                            continue;
                        }
                    }
                    deviceHistoryMap.put(deviceField.getFunctionLogo(), Lists.newArrayList());
                }
                resultMap.put(deviceId, deviceHistoryMap);
            });
        }
        return ResponseResult.ok(resultMap);
    }

    /**
     * 查询设备功能点指定时间段内的last和first历史数据
     *
     * @param deviceQueryVo
     * @return
     */
    @Override
    public ResponseResult<Map<String, Map<String, List<NodeDifHistoryDto>>>> findNodeDifHistoryListFeign(DeviceHistoryQueryVo deviceQueryVo) {
        //返回数据
        Map<String, Map<String, List<NodeDifHistoryDto>>> resultMap = Maps.newHashMap();
        //获取查询条件参数
        Set<String> deviceIds = deviceQueryVo.getDeviceIds();
        Set<String> functionLogos = deviceQueryVo.getFunctionLogos();
        String startTime = deviceQueryVo.getStartTime();
        String endTime = deviceQueryVo.getEndTime();
        String timeInterval = deviceQueryVo.getTimeInterval();
        //根据查询条件查询设备字段数据
        Map<String, List<DeviceFieldDto>> deviceFieldMap = deviceService.queryDeviceFieldList(DeviceFieldQueryVo.builder().deviceIds(deviceIds)
                .functionLogos(functionLogos).build()).getData();
        if (MapUtils.isNotEmpty(deviceFieldMap)) {
            //按照表名进行字段分组查询
            List<DeviceFieldDto> deviceFieldList = deviceFieldMap.entrySet().stream().flatMap(s -> s.getValue().stream()).collect(Collectors.toList());
            Map<String, List<String>> tableFieldMap = deviceFieldList.stream().collect(Collectors.groupingBy(DeviceFieldDto::getTableName,
                    Collectors.mapping(DeviceFieldDto::getFieldName, Collectors.toList())));
            //根据表名和字段名查询表数据(表名称 -> (表字段名称 -> 数据))
            Map<String, Map<String, List<TableDifDataModel>>> tableDataMap = accessDataService.findNodeDifHistoryListFeign(tableFieldMap, startTime, endTime, timeInterval);
            deviceFieldMap.forEach((deviceId, deviceFields) -> {
                Map<String, List<NodeDifHistoryDto>> deviceHistoryMap = Maps.newHashMap();
                for (DeviceFieldDto deviceField : deviceFields) {
                    if (tableDataMap.containsKey(deviceField.getTableName())) {
                        Map<String, List<TableDifDataModel>> dataMap = tableDataMap.get(deviceField.getTableName());
                        if (dataMap.containsKey(deviceField.getFieldName())) {
                            deviceHistoryMap.put(deviceField.getFunctionLogo(), dataMap.get(deviceField.getFieldName()).stream().map(data -> {
                                NodeDifHistoryDto result = new NodeDifHistoryDto();
                                result.setFunctionLogo(deviceField.getFunctionLogo());
                                result.setDataType(deviceField.getDataType());
                                result.setFieldName(data.getFieldName());
                                result.setFirstDataValue(CommonUtil.getRangeValue(deviceField.getValueRange(), deviceField.getDataType(), deviceField.getDataObject(), data.getFirstFieldValue()));
                                result.setLastDataValue(CommonUtil.getRangeValue(deviceField.getValueRange(), deviceField.getDataType(), deviceField.getDataObject(), data.getLastFieldValue()));
                                result.setFirstDateTime(data.getFirstDataTime());
                                result.setLastDateTime(data.getLastDataTime());
                                return result;
                            }).collect(Collectors.toList()));
                            continue;
                        }
                    }
                    deviceHistoryMap.put(deviceField.getFunctionLogo(), Lists.newArrayList());
                }
                resultMap.put(deviceId, deviceHistoryMap);
            });
        }
        return ResponseResult.ok(resultMap);
    }

    @Override
    public ResponseResult<Map<String, Map<String, List<DeviceHistoryDto>>>> findDeviceHistoryIndexValueList(DeviceIndexQueryVo deviceQueryVo) {
        //返回的对象
        Map<String, Map<String, List<DeviceHistoryDto>>> resultMap = Maps.newHashMap();

        //1.获取查询条件参数
        Map<String, Set<String>> deviceFuctionMap = Maps.newHashMap();
        deviceQueryVo.getDeviceFuctionMap().forEach((deviceId, functionLogos) ->
                deviceFuctionMap.put(deviceId, functionLogos.stream().map(functionLogo -> {
                    if (functionLogo.contains(StaticParamVo.INDEX)) {
                        return functionLogo.substring(0, functionLogo.indexOf(StaticParamVo.INDEX));
                    }
                    return functionLogo;
                }).collect(Collectors.toSet())));
        String startTime = deviceQueryVo.getStartTime();
        String endTime = deviceQueryVo.getEndTime();
        String timeInterval = deviceQueryVo.getTimeInterval();
        Integer limitSize = deviceQueryVo.getLimitSize();
        //根据查询条件查询设备字段数据
        Map<String, List<DeviceFieldDto>> deviceFieldMap = deviceService.queryDeviceFieldListByMap(deviceFuctionMap).getData();
        if (MapUtils.isNotEmpty(deviceFieldMap)) {
            //按照表名进行字段分组查询
            List<DeviceFieldDto> deviceFieldList = deviceFieldMap.entrySet().stream().flatMap(s -> s.getValue().stream()).collect(Collectors.toList());
            Map<String, List<String>> tableFieldMap = deviceFieldList.stream().collect(Collectors.groupingBy(DeviceFieldDto::getTableName,
                    Collectors.mapping(DeviceFieldDto::getFieldName, Collectors.toList())));
            //根据表名和字段名查询表数据(表名称 -> (表字段名称 -> 数据))
             Map<String, Map<String, List<TableDataModel>>> tableDataMap = accessDataService.queryTableDataList(tableFieldMap, startTime, endTime, timeInterval, limitSize);

            deviceQueryVo.getDeviceFuctionMap().forEach((deviceId, deviceFields) -> {
                Map<String, List<DeviceHistoryDto>> deviceHistoryMap = Maps.newHashMap();
                if (deviceFieldMap.containsKey(deviceId)) {
                    Map<String, DeviceFieldDto> deviceLogoMap = deviceFieldMap.get(deviceId).stream().collect(Collectors.toMap(DeviceFieldDto::getFunctionLogo,
                            a -> a, (k1, k2) -> k1));
                    for (String queryLogo : deviceFields) {
                        //带下标 查询索引数据
                        if (queryLogo.contains(StaticParamVo.INDEX)) {
                            String functionLogo = queryLogo.substring(0, queryLogo.indexOf(StaticParamVo.INDEX));
                            int index = Integer.parseInt(queryLogo.substring(queryLogo.lastIndexOf(StaticParamVo.INDEX)).replace(StaticParamVo.INDEX, FileUtil.separator));
                            if (deviceLogoMap.containsKey(functionLogo)) {
                                DeviceFieldDto deviceField = deviceLogoMap.get(functionLogo);
                                if (deviceField.getDataType() != 8) {
                                    deviceHistoryMap.put(queryLogo, Lists.newArrayList());
                                    continue;
                                }
                                if (tableDataMap.containsKey(deviceField.getTableName())) {
                                    Map<String, List<TableDataModel>> dataMap = tableDataMap.get(deviceField.getTableName());
                                    if (dataMap.containsKey(deviceField.getFieldName())) {
                                        deviceHistoryMap.put(deviceField.getFunctionLogo(), dataMap.get(deviceField.getFieldName()).stream().map(data -> {
                                            DeviceHistoryDto result = new DeviceHistoryDto();
                                            result.setFunctionLogo(deviceField.getFunctionLogo());
                                            result.setDataType(deviceField.getDataType());
                                            result.setFieldName(data.getFieldName());
                                            //下标获取数据
                                            Object fieldValue = CommonUtil.getRangeValue(deviceField.getValueRange(), deviceField.getDataType(), deviceField.getDataObject(), data.getFieldValue());
                                            JSONArray dataValues = JSON.parseArray(String.valueOf(fieldValue));
                                            if (CollectionUtils.isNotEmpty(dataValues) && dataValues.size() > index) {
                                                result.setDataValue(dataValues.get(index));
                                            }
                                            result.setDateTime(data.getDataTime());
                                            return result;
                                        }).collect(Collectors.toList()));
                                        continue;
                                    }
                                }
                            }
                        }

                        //不带下标 正常设备数据 直接返回
                        if (deviceLogoMap.containsKey(queryLogo)) {
                            DeviceFieldDto deviceField = deviceLogoMap.get(queryLogo);
                            if (tableDataMap.containsKey(deviceField.getTableName())) {
                                Map<String, List<TableDataModel>> dataMap = tableDataMap.get(deviceField.getTableName());
                                if (dataMap.containsKey(deviceField.getFieldName())) {
                                    deviceHistoryMap.put(deviceField.getFunctionLogo(), dataMap.get(deviceField.getFieldName()).stream().map(data -> {
                                        DeviceHistoryDto result = new DeviceHistoryDto();
                                        result.setFunctionLogo(deviceField.getFunctionLogo());
                                        result.setDataType(deviceField.getDataType());
                                        result.setFieldName(data.getFieldName());
                                        result.setDataValue(CommonUtil.getRangeValue(deviceField.getValueRange(), deviceField.getDataType(), deviceField.getDataObject(), data.getFieldValue()));
                                        result.setDateTime(data.getDataTime());
                                        return result;
                                    }).collect(Collectors.toList()));
                                    continue;
                                }
                            }
                        }
                        deviceHistoryMap.put(queryLogo, Lists.newArrayList());
                    }
                    resultMap.put(deviceId, deviceHistoryMap);
                }
            });
        }
        return ResponseResult.ok(resultMap);
    }

    @Override
    public ResponseResult<Map<String, Map<String, List<DeviceHistoryDto>>>> findDeviceDiffIndexValueListFeign(DeviceIndexQueryVo deviceQueryVo) {
        //返回的对象
        Map<String, Map<String, List<DeviceHistoryDto>>> resultMap = Maps.newHashMap();

        //1.获取查询条件参数
        Map<String, Set<String>> deviceFuctionMap = Maps.newHashMap();
        deviceQueryVo.getDeviceFuctionMap().forEach((deviceId, functionLogos) ->
                deviceFuctionMap.put(deviceId, functionLogos.stream().map(functionLogo -> {
                    if (functionLogo.contains(StaticParamVo.INDEX)) {
                        return functionLogo.substring(0, functionLogo.indexOf(StaticParamVo.INDEX));
                    }
                    return functionLogo;
                }).collect(Collectors.toSet())));
        String startTime = deviceQueryVo.getStartTime();
        String endTime = deviceQueryVo.getEndTime();
        String timeInterval = deviceQueryVo.getTimeInterval();
        //根据查询条件查询设备字段数据
        Map<String, List<DeviceFieldDto>> deviceFieldMap = deviceService.queryDeviceFieldListByMap(deviceFuctionMap).getData();
        if (MapUtils.isNotEmpty(deviceFieldMap)) {
            //按照表名进行字段分组查询
            List<DeviceFieldDto> deviceFieldList = deviceFieldMap.entrySet().stream().flatMap(s -> s.getValue().stream()).collect(Collectors.toList());
            Map<String, List<String>> tableFieldMap = deviceFieldList.stream().collect(Collectors.groupingBy(DeviceFieldDto::getTableName,
                    Collectors.mapping(DeviceFieldDto::getFieldName, Collectors.toList())));
            //根据表名和字段名查询表数据(表名称 -> (表字段名称 -> 数据))
            Map<String, Map<String, List<TableDataModel>>> tableDataMap = accessDataService.queryDifferenceTableDataList(tableFieldMap, startTime, endTime, timeInterval, 1);

            deviceQueryVo.getDeviceFuctionMap().forEach((deviceId, deviceFields) -> {
                Map<String, List<DeviceHistoryDto>> deviceHistoryMap = Maps.newHashMap();
                if (deviceFieldMap.containsKey(deviceId)) {
                    Map<String, DeviceFieldDto> deviceLogoMap = deviceFieldMap.get(deviceId).stream().collect(Collectors.toMap(DeviceFieldDto::getFunctionLogo,
                            a -> a, (k1, k2) -> k1));
                    for (String queryLogo : deviceFields) {
                        //带下标 查询索引数据
                        if (queryLogo.contains(StaticParamVo.INDEX)) {
                            String functionLogo = queryLogo.substring(0, queryLogo.indexOf(StaticParamVo.INDEX));
                            int index = Integer.parseInt(queryLogo.substring(queryLogo.lastIndexOf(StaticParamVo.INDEX)).replace(StaticParamVo.INDEX, ""));
                            if (deviceLogoMap.containsKey(functionLogo)) {
                                DeviceFieldDto deviceField = deviceLogoMap.get(queryLogo);
                                if (deviceField.getDataType() != 8) {
                                    deviceHistoryMap.put(queryLogo, Lists.newArrayList());
                                    continue;
                                }
                                if (tableDataMap.containsKey(deviceField.getTableName())) {
                                    Map<String, List<TableDataModel>> dataMap = tableDataMap.get(deviceField.getTableName());
                                    if (dataMap.containsKey(deviceField.getFieldName())) {
                                        deviceHistoryMap.put(deviceField.getFunctionLogo(), dataMap.get(deviceField.getFieldName()).stream().map(data -> {
                                            DeviceHistoryDto result = new DeviceHistoryDto();
                                            result.setFunctionLogo(deviceField.getFunctionLogo());
                                            result.setDataType(deviceField.getDataType());
                                            result.setFieldName(data.getFieldName());
                                            //下标获取数据
                                            Object fieldValue = CommonUtil.getRangeValue(deviceField.getValueRange(), deviceField.getDataType(), deviceField.getDataObject(), data.getFieldValue());
                                            JSONArray dataValues = JSON.parseArray(String.valueOf(fieldValue));
                                            if (CollectionUtils.isNotEmpty(dataValues) && dataValues.size() > index) {
                                                result.setDataValue(dataValues.get(index));
                                            }
                                            result.setDateTime(data.getDataTime());
                                            return result;
                                        }).collect(Collectors.toList()));
                                        continue;
                                    }
                                }
                            }
                        }

                        //不带下标 正常设备数据 直接返回
                        if (deviceLogoMap.containsKey(queryLogo)) {
                            DeviceFieldDto deviceField = deviceLogoMap.get(queryLogo);
                            if (tableDataMap.containsKey(deviceField.getTableName())) {
                                Map<String, List<TableDataModel>> dataMap = tableDataMap.get(deviceField.getTableName());
                                if (dataMap.containsKey(deviceField.getFieldName())) {
                                    deviceHistoryMap.put(deviceField.getFunctionLogo(), dataMap.get(deviceField.getFieldName()).stream().map(data -> {
                                        DeviceHistoryDto result = new DeviceHistoryDto();
                                        result.setFunctionLogo(deviceField.getFunctionLogo());
                                        result.setDataType(deviceField.getDataType());
                                        result.setFieldName(data.getFieldName());
                                        result.setDataValue(CommonUtil.getRangeValue(deviceField.getValueRange(), deviceField.getDataType(), deviceField.getDataObject(), data.getFieldValue()));
                                        result.setDateTime(data.getDataTime());
                                        return result;
                                    }).collect(Collectors.toList()));
                                    continue;
                                }
                            }
                        }
                        deviceHistoryMap.put(queryLogo, Lists.newArrayList());
                    }
                    resultMap.put(deviceId, deviceHistoryMap);
                }
            });
        }
        return ResponseResult.ok(resultMap);
    }

    /**
     * 根据多个设备id和多个功能点标识以及多个索引查询历史数据列表
     * @param deviceQueryVo 设备历史数据查询条件
     * @return
     */
    @Override
    public ResponseResult<Map<String, Map<String, Map<String, List<DeviceHistoryDto>>>>> findIndexListHistorValueFeign(DeviceIndexListQueryVo deviceQueryVo) {
        //返回的对象
        Map<String, Map<String, Map<String, List<DeviceHistoryDto>>>> resultMap = Maps.newHashMap();

        //1.获取查询条件参数
        Map<String, Set<String>> deviceFuctionMap = Maps.newHashMap();
        deviceQueryVo.getDeviceFuctionMap().forEach((deviceId, functionLogos) ->
                deviceFuctionMap.put(deviceId, functionLogos.stream().map(functionLogo -> {
                    if (functionLogo.contains(StaticParamVo.INDEX)) {
                        return functionLogo.substring(0, functionLogo.indexOf(StaticParamVo.INDEX));
                    }
                    return functionLogo;
                }).collect(Collectors.toSet())));
        String startTime = deviceQueryVo.getStartTime();
        String endTime = deviceQueryVo.getEndTime();
        String timeInterval = deviceQueryVo.getTimeInterval();
        Integer limitSize = deviceQueryVo.getLimitSize();
        //根据查询条件查询设备字段数据
        Map<String, List<DeviceFieldDto>> deviceFieldMap = deviceService.queryDeviceFieldListByMap(deviceFuctionMap).getData();
        if (MapUtils.isNotEmpty(deviceFieldMap)) {
            //按照表名进行字段分组查询
            List<DeviceFieldDto> deviceFieldList = deviceFieldMap.entrySet().stream().flatMap(s -> s.getValue().stream()).collect(Collectors.toList());
            Map<String, List<String>> tableFieldMap = deviceFieldList.stream().collect(Collectors.groupingBy(DeviceFieldDto::getTableName,
                    Collectors.mapping(DeviceFieldDto::getFieldName, Collectors.toList())));
            //根据表名和字段名查询表数据(表名称 -> (表字段名称 -> 数据))
            Map<String, Map<String, List<TableDataModel>>> tableDataMap = accessDataService.queryFunctionPointTableDataList(tableFieldMap, startTime, endTime, timeInterval, limitSize);

            deviceQueryVo.getDeviceFuctionMap().forEach((deviceId, deviceFields) -> {
                Map<String, Map<String, List<DeviceHistoryDto>>> deviceHistoryMap = Maps.newHashMap();
                if (deviceFieldMap.containsKey(deviceId)) {
                    Map<String, DeviceFieldDto> deviceLogoMap = deviceFieldMap.get(deviceId).stream().collect(Collectors.toMap(DeviceFieldDto::getFunctionLogo,
                            a -> a, (k1, k2) -> k1));
                    for (String functionLogo : deviceFields) {
                        Map<String, List<DeviceHistoryDto>> indexHistoryMap = Maps.newHashMap();
                        if (deviceLogoMap.containsKey(functionLogo)) {
                            DeviceFieldDto deviceField = deviceLogoMap.get(functionLogo);
                            if (deviceField.getDataType() != 8) {
                                deviceHistoryMap.put(functionLogo, indexHistoryMap);
                                continue;
                            }
                            if (tableDataMap.containsKey(deviceField.getTableName())) {
                                Map<String, List<TableDataModel>> dataMap = tableDataMap.get(deviceField.getTableName());
                                if (dataMap.containsKey(deviceField.getFieldName())) {
                                    //循环电枪编码获取数据
                                    deviceQueryVo.getGunCodeList().forEach(gunCode -> {
                                        int index = Integer.parseInt(gunCode) - 1;
                                        indexHistoryMap.put(gunCode, dataMap.get(deviceField.getFieldName()).stream().map(data -> {
                                            DeviceHistoryDto result = new DeviceHistoryDto();
                                            result.setFunctionLogo(deviceField.getFunctionLogo());
                                            result.setDataType(deviceField.getDataType());
                                            result.setFieldName(data.getFieldName());
                                            //下标获取数据
                                            Object fieldValue = CommonUtil.getRangeValue(deviceField.getValueRange(), deviceField.getDataType(), deviceField.getDataObject(), data.getFieldValue());
                                            JSONArray dataValues = JSON.parseArray(String.valueOf(fieldValue));
                                            if (CollectionUtils.isNotEmpty(dataValues) && dataValues.size() > index) {
                                                result.setDataValue(dataValues.get(index));
                                            }
                                            result.setDateTime(data.getDataTime());
                                            return result;
                                        }).collect(Collectors.toList()));
                                    });
                                }
                            }
                            deviceHistoryMap.put(functionLogo, indexHistoryMap);
                        }
                        deviceHistoryMap.put(functionLogo, indexHistoryMap);
                    }
                    resultMap.put(deviceId, deviceHistoryMap);
                }
            });
        }
        return ResponseResult.ok(resultMap);
    }

    /**
     * 查询设备功能点指定时间段内的last和first历史数据(查询索引数据)
     * @param deviceQueryVo
     * @return
     */
    @Override
    public ResponseResult<Map<String, Map<String, List<NodeDifHistoryDto>>>> findNodeDifHistoryIndexListFeign(DeviceIndexQueryVo deviceQueryVo) {
        //返回的对象
        Map<String, Map<String, List<NodeDifHistoryDto>>> resultMap = Maps.newHashMap();

        //1.获取查询条件参数
        Map<String, Set<String>> deviceFuctionMap = Maps.newHashMap();
        deviceQueryVo.getDeviceFuctionMap().forEach((deviceId, functionLogos) ->
                deviceFuctionMap.put(deviceId, functionLogos.stream().map(functionLogo -> {
                    if (functionLogo.contains(StaticParamVo.INDEX)) {
                        return functionLogo.substring(0, functionLogo.indexOf(StaticParamVo.INDEX));
                    }
                    return functionLogo;
                }).collect(Collectors.toSet())));
        String startTime = deviceQueryVo.getStartTime();
        String endTime = deviceQueryVo.getEndTime();
        String timeInterval = deviceQueryVo.getTimeInterval();
        //根据查询条件查询设备字段数据
        Map<String, List<DeviceFieldDto>> deviceFieldMap = deviceService.queryDeviceFieldListByMap(deviceFuctionMap).getData();
        if (MapUtils.isNotEmpty(deviceFieldMap)) {
            //按照表名进行字段分组查询
            List<DeviceFieldDto> deviceFieldList = deviceFieldMap.entrySet().stream().flatMap(s -> s.getValue().stream()).collect(Collectors.toList());
            Map<String, List<String>> tableFieldMap = deviceFieldList.stream().collect(Collectors.groupingBy(DeviceFieldDto::getTableName,
                    Collectors.mapping(DeviceFieldDto::getFieldName, Collectors.toList())));
            //根据表名和字段名查询表数据(表名称 -> (表字段名称 -> 数据))
            Map<String, Map<String, List<TableDifDataModel>>> tableDataMap = accessDataService.findNodeDifHistoryListFeign(tableFieldMap, startTime, endTime, timeInterval);

            deviceQueryVo.getDeviceFuctionMap().forEach((deviceId, deviceFields) -> {
                Map<String, List<NodeDifHistoryDto>> deviceHistoryMap = Maps.newHashMap();
                if (deviceFieldMap.containsKey(deviceId)) {
                    Map<String, DeviceFieldDto> deviceLogoMap = deviceFieldMap.get(deviceId).stream().collect(Collectors.toMap(DeviceFieldDto::getFunctionLogo,
                            a -> a, (k1, k2) -> k1));
                    for (String queryLogo : deviceFields) {
                        //带下标 查询索引数据
                        if (queryLogo.contains(StaticParamVo.INDEX)) {
                            String functionLogo = queryLogo.substring(0, queryLogo.indexOf(StaticParamVo.INDEX));
                            int index = Integer.parseInt(queryLogo.substring(queryLogo.lastIndexOf(StaticParamVo.INDEX)).replace(StaticParamVo.INDEX, ""));
                            if (deviceLogoMap.containsKey(functionLogo)) {
                                DeviceFieldDto deviceField = deviceLogoMap.get(queryLogo);
                                if (deviceField.getDataType() != 8) {
                                    deviceHistoryMap.put(queryLogo, Lists.newArrayList());
                                    continue;
                                }
                                if (tableDataMap.containsKey(deviceField.getTableName())) {
                                    Map<String, List<TableDifDataModel>> dataMap = tableDataMap.get(deviceField.getTableName());
                                    if (dataMap.containsKey(deviceField.getFieldName())) {
                                        deviceHistoryMap.put(deviceField.getFunctionLogo(), dataMap.get(deviceField.getFieldName()).stream().map(data -> {
                                            NodeDifHistoryDto result = new NodeDifHistoryDto();
                                            result.setFunctionLogo(deviceField.getFunctionLogo());
                                            result.setDataType(deviceField.getDataType());
                                            result.setFieldName(data.getFieldName());
                                            //下标获取first数据
                                            Object firstFieldValue = CommonUtil.getRangeValue(deviceField.getValueRange(), deviceField.getDataType(), deviceField.getDataObject(), data.getFirstFieldValue());
                                            JSONArray firstDataValues = JSON.parseArray(String.valueOf(firstFieldValue));
                                            if (CollectionUtils.isNotEmpty(firstDataValues) && firstDataValues.size() > index) {
                                                result.setLastDataValue(firstDataValues.get(index));
                                            }
                                            //下标获取last数据
                                            Object lastFieldValue = CommonUtil.getRangeValue(deviceField.getValueRange(), deviceField.getDataType(), deviceField.getDataObject(), data.getLastFieldValue());
                                            JSONArray lastDataValues = JSON.parseArray(String.valueOf(lastFieldValue));
                                            if (CollectionUtils.isNotEmpty(lastDataValues) && lastDataValues.size() > index) {
                                                result.setLastDataValue(lastDataValues.get(index));
                                            }
                                            result.setFirstDateTime(data.getFirstDataTime());
                                            result.setLastDateTime(data.getLastDataTime());
                                            return result;
                                        }).collect(Collectors.toList()));
                                        continue;
                                    }
                                }
                            }
                        }

                        //不带下标 正常设备数据 直接返回
                        if (deviceLogoMap.containsKey(queryLogo)) {
                            DeviceFieldDto deviceField = deviceLogoMap.get(queryLogo);
                            if (tableDataMap.containsKey(deviceField.getTableName())) {
                                Map<String, List<TableDifDataModel>> dataMap = tableDataMap.get(deviceField.getTableName());
                                if (dataMap.containsKey(deviceField.getFieldName())) {
                                    deviceHistoryMap.put(deviceField.getFunctionLogo(), dataMap.get(deviceField.getFieldName()).stream().map(data -> {
                                        NodeDifHistoryDto result = new NodeDifHistoryDto();
                                        result.setFunctionLogo(deviceField.getFunctionLogo());
                                        result.setDataType(deviceField.getDataType());
                                        result.setFieldName(data.getFieldName());
                                        result.setFirstDataValue(CommonUtil.getRangeValue(deviceField.getValueRange(), deviceField.getDataType(), deviceField.getDataObject(), data.getFirstFieldValue()));
                                        result.setLastDataValue(CommonUtil.getRangeValue(deviceField.getValueRange(), deviceField.getDataType(), deviceField.getDataObject(), data.getLastFieldValue()));
                                        result.setLastDateTime(data.getLastDataTime());
                                        result.setFirstDateTime(data.getFirstDataTime());
                                        return result;
                                    }).collect(Collectors.toList()));
                                    continue;
                                }
                            }
                        }
                        deviceHistoryMap.put(queryLogo, Lists.newArrayList());
                    }
                    resultMap.put(deviceId, deviceHistoryMap);
                }
            });
        }
        return ResponseResult.ok(resultMap);
    }

}
