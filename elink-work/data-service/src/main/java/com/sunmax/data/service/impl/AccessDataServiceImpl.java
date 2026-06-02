package com.sunmax.data.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.sunmax.common.util.DateUtil;
import com.sunmax.common.util.StringUtil;
import com.sunmax.common.util.oss.FileUtil;
import com.sunmax.data.mapper.tdengine.DataStoreMapper;
import com.sunmax.data.model.TableDataModel;
import com.sunmax.data.model.TableDifDataModel;
import com.sunmax.data.model.TableFieldModel;
import com.sunmax.data.service.AccessDataService;
import com.sunmax.data.vo.DeviceDataVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AccessDataServiceImpl implements AccessDataService {

    @Autowired
    private DataStoreMapper dataStoreMapper;

    @Autowired
    private Executor asyncExecutor;

    //校验字段类型
    private boolean checkFieldType(String fieldType, String tableFieldType) {
        if (fieldType.contains(tableFieldType)) {
            return true;
        } else if (Objects.equals(fieldType, "DOUBLE") && Objects.equals(tableFieldType, "FLOAT")) {
            return true;
        } else return Objects.equals(fieldType, "FLOAT") && Objects.equals(tableFieldType, "DOUBLE");
    }

    @Override
    public void batchInsertTableData(String tableName, Long currentTime, List<DeviceDataVo> deviceDataVos) {
        try {
            tableName = tableName.toLowerCase().replace(FileUtil.BAR, FileUtil.UNDERLINE);
            //0.校验数据是否是实时的(一分钟之内)
            if (CollectionUtils.isNotEmpty(deviceDataVos)) {
                Optional<DeviceDataVo> optional = deviceDataVos.stream().filter(s -> StringUtil.isNotEmpty(s.getDateTime())).max(Comparator.comparing(DeviceDataVo::getDateTime));
                if (optional.isPresent()) {
                    DeviceDataVo deviceDataVo = optional.get();
                    try {
                        if (StringUtil.isNotEmpty(deviceDataVo.getDateTime()) && DateUtil.strToLocalDateTime(deviceDataVo.getDateTime()).isBefore(LocalDateTime.now().minusMinutes(1))) {
                            return;
                        }
                    } catch (Exception e) {
                        log.error("数据存储数据:{}", deviceDataVo);
                        log.error("数据校验时间异常", e);
                    }
                } else {
                    return;
                }
            }
            //1.判断表是否存在 不存在则创建表结构
            Map<String, Object> tableMap = dataStoreMapper.findTableIfExists(tableName);
            if (tableMap == null || tableMap.isEmpty()) {
                Map<String, String> fileNameTypeMap = deviceDataVos.stream().collect(Collectors.toMap(DeviceDataVo::getFieldName,
                        DeviceDataVo::getFieldType, (k1, k2) -> k1));
                //创建表结构
                dataStoreMapper.createTable(tableName, fileNameTypeMap);
            }

            //2.判断字段类型是否一样 不一样则调整
            //2.1 查询表的表结构字段数据
            Map<String, String> fieldNameTypeMap = dataStoreMapper.findFieldNameTypeList(tableName).stream().filter(f -> !Objects.equals(f.getField(), "ts"))
                    .collect(Collectors.toMap(TableFieldModel::getField, TableFieldModel::getType));
            //2.2 定义表结构字段类型数据
            Map<String, String> addFieldTypeMap = Maps.newConcurrentMap();
            Map<String, String> updateFieldTypeMap = Maps.newConcurrentMap();
            deviceDataVos.forEach(deviceData -> {
                if (fieldNameTypeMap.containsKey(deviceData.getFieldName())) { //修改字段列及类型
                    String fieldType = fieldNameTypeMap.get(deviceData.getFieldName());
                    if (!checkFieldType(deviceData.getFieldType(), fieldType)) {
                        updateFieldTypeMap.put(deviceData.getFieldName(), deviceData.getFieldType());
                    }
                    fieldNameTypeMap.remove(deviceData.getFieldName());//清除存在的列
                } else { //添加字段列及类型
                    addFieldTypeMap.put(deviceData.getFieldName(), deviceData.getFieldType());
                }
            });
            String finalTableName = tableName;
            if (!addFieldTypeMap.isEmpty()) {
                addFieldTypeMap.forEach((fieldName, fieldType) -> dataStoreMapper.addTableField(finalTableName, fieldName, fieldType));
            }
            if (!fieldNameTypeMap.isEmpty()) {
                fieldNameTypeMap.keySet().forEach(fieldName -> dataStoreMapper.deleteTableField(finalTableName, fieldName));
            }
            if (!updateFieldTypeMap.isEmpty()) {
                updateFieldTypeMap.forEach((fieldName, fieldType) -> dataStoreMapper.updateTableField(finalTableName, fieldName, fieldType));
            }
            //3.数据插入到表中
            Map<String, Object> fieldNameValueMap = deviceDataVos.stream().collect(HashMap::new, (map, item) -> map.put(item.getFieldName(), item.getDataValue()), HashMap::putAll);
            fieldNameValueMap.put("ts", currentTime);
            dataStoreMapper.batchAddTableData(tableName, fieldNameValueMap);
        } catch (Exception e) {
            log.error("数据存储异常报错", e);
        }
    }

    @Override
    public Map<String, Map<String, List<TableDataModel>>> queryTableDataList(Map<String, List<String>> pointTableQueryMap, String startTime, String endTime, String timeInterval, Integer limitSize) {
        //返回的数据
        Map<String, Map<String, List<TableDataModel>>> resultMap = Maps.newHashMap();
        // 为每个表创建一个异步任务
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        if (!pointTableQueryMap.isEmpty()) {
            //查询全部表名称
            List<String> tableNameList = dataStoreMapper.findAllTableIfExists();
            //根据表名和字段名查询字段值
            pointTableQueryMap.forEach((key, value) -> {
                CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                    try {
                        //判断表是否全在，如果存在则正常查询数据
                        if (tableNameList.contains(key)) {
                            //校验字段名是否存在
                            List<String> filedNames = Lists.newArrayList();
                            List<TableFieldModel> fieldNameList = dataStoreMapper.findFieldNameTypeList(key);
                            if (CollectionUtils.isNotEmpty(fieldNameList)) {
                                Map<String, String> fieldNameMap = fieldNameList.stream().collect(Collectors.toMap(TableFieldModel::getField, TableFieldModel::getType));
                                filedNames = value.stream().filter(fieldNameMap::containsKey).collect(Collectors.toList());
                            }
                            List<Map<String, Object>> dataList = Lists.newArrayList();
                            if (CollectionUtils.isNotEmpty(filedNames)) {
                                dataList = dataStoreMapper.queryTableDataList(key, filedNames, startTime, endTime, timeInterval, limitSize);
                            }
                            this.getTableDataMap(resultMap, key, value, dataList);
                        }
                    } catch (Exception e) {
                        // 记录错误，避免任务中断
                        log.error("查询表 {} 失败: {}", key, e.getMessage(), e);
                    }
                }, asyncExecutor);
                futures.add(future);
            });
        }
        if (futures.isEmpty()) {
            return Collections.emptyMap();
        }
        // 返回一个 CompletableFuture，当所有任务完成时完成
        CompletableFuture<Map<String, Map<String, List<TableDataModel>>>> future = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).thenApply(v -> resultMap);
        try {
            // 可设置超时
            return future.get(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("查询数据被中断", e);
            return Collections.emptyMap();
        } catch (java.util.concurrent.TimeoutException e) {
            log.error("查询数据超时", e);
            return Collections.emptyMap();
        } catch (Exception e) {
            log.error("查询数据异常报错", e);
            return Collections.emptyMap();
        }
    }

    @Override
    public Map<String, Map<String, List<TableDataModel>>> queryDifferenceTableDataList(Map<String, List<String>> pointTableQueryMap, String startTime, String endTime, String timeInterval, Integer isNear) {
        //返回的数据
        Map<String, Map<String, List<TableDataModel>>> resultMap = Maps.newHashMap();
        if (!pointTableQueryMap.isEmpty()) {
            //查询全部表名称
            List<String> tableNameList = dataStoreMapper.findAllTableIfExists();
            //根据表名和字段名查询字段值
            pointTableQueryMap.forEach((key, value) -> {
                if (tableNameList.contains(key)) {
                    //校验字段名是否存在
                    List<String> filedNames = Lists.newArrayList();
                    List<TableFieldModel> fieldNameList = dataStoreMapper.findFieldNameTypeList(key);
                    if (CollectionUtils.isNotEmpty(fieldNameList)) {
                        Map<String, String> fieldNameMap = fieldNameList.stream().collect(Collectors.toMap(TableFieldModel::getField, TableFieldModel::getType));
                        filedNames = value.stream().filter(fieldNameMap::containsKey).collect(Collectors.toList());
                    }
                    List<Map<String, Object>> dataList = Lists.newArrayList();
                    if (CollectionUtils.isNotEmpty(filedNames)) {
                        if (StringUtil.isNotEmpty(isNear) && isNear == 1) {
                            dataList = dataStoreMapper.queryfirstTableDataList(key, filedNames, startTime, endTime, timeInterval);
                        } else {
                            dataList = dataStoreMapper.querySourceTableDataList(key, filedNames, startTime, endTime);
                        }
                    }
                    this.getTableDataMap(resultMap, key, value, dataList);
                }
            });
        }
        return resultMap;
    }

    @Override
    public Map<String, Map<String, List<TableDataModel>>> queryCountTableDataList(Map<String, List<String>> pointTableQueryMap, String startTime, String endTime, String timeInterval, String cuntFun) {
        //返回的数据
        Map<String, Map<String, List<TableDataModel>>> resultMap = Maps.newHashMap();
        if (!pointTableQueryMap.isEmpty()) {
            //查询全部表名称
            List<String> tableNameList = dataStoreMapper.findAllTableIfExists();
            //根据表名和字段名查询字段值
            pointTableQueryMap.forEach((key, value) -> {
                //判断表是否全在，如果存在则正常查询数据
                if (tableNameList.contains(key)) {
                    //校验字段名是否存在
                    List<String> filedNames = Lists.newArrayList();
                    List<TableFieldModel> fieldNameList = dataStoreMapper.findFieldNameTypeList(key);
                    if (CollectionUtils.isNotEmpty(fieldNameList)) {
                        Map<String, String> fieldNameMap = fieldNameList.stream().collect(Collectors.toMap(TableFieldModel::getField, TableFieldModel::getType));
                        filedNames = value.stream().filter(fieldNameMap::containsKey).collect(Collectors.toList());
                    }
                    List<Map<String, Object>> dataList = Lists.newArrayList();
                    if (CollectionUtils.isNotEmpty(filedNames)) {
                        dataList = dataStoreMapper.queryCountFunTableDataList(key, filedNames, startTime, endTime, timeInterval, cuntFun);
                    }
                    Map<String, List<TableDataModel>> tableDataMap = Maps.newHashMap();
                    if (CollectionUtils.isNotEmpty(dataList)) {
                        for (String funcationLogo : value) {
                            List<TableDataModel> tableDataList = Lists.newArrayList();
                            for (Map<String, Object> data : dataList) {
                                TableDataModel tableDataModel = new TableDataModel();
                                String dataTime = data.get("ts").toString().substring(0, data.get("ts").toString().indexOf("."));
                                tableDataModel.setFieldName(funcationLogo);
                                tableDataModel.setDataTime(dataTime);
                                //处理值
                                if (data.containsKey(cuntFun.toLowerCase()+"("+ funcationLogo + ")")) {
                                    tableDataModel.setFieldValue(data.get(cuntFun.toLowerCase()+"("+ funcationLogo + ")"));
                                }
                                tableDataList.add(tableDataModel);
                            }
                            tableDataMap.put(funcationLogo, tableDataList);
                        }
                    }
                    resultMap.put(key, tableDataMap);
                }
            });
        }
        return resultMap;
    }

    @Override
    public Map<String, Map<String, List<TableDifDataModel>>> findNodeDifHistoryListFeign(Map<String, List<String>> pointTableQueryMap, String startTime, String endTime, String timeInterval) {
        //返回的数据
        Map<String, Map<String, List<TableDifDataModel>>> resultMap = Maps.newHashMap();
        if (!pointTableQueryMap.isEmpty()) {
            //查询全部表名称
            List<String> tableNameList = dataStoreMapper.findAllTableIfExists();
            //根据表名和字段名查询字段值
            pointTableQueryMap.forEach((key, value) -> {
                if (tableNameList.contains(key)) {
                    //校验字段名是否存在
                    List<String> filedNames = Lists.newArrayList();
                    List<TableFieldModel> fieldNameList = dataStoreMapper.findFieldNameTypeList(key);
                    if (CollectionUtils.isNotEmpty(fieldNameList)) {
                        Map<String, String> fieldNameMap = fieldNameList.stream().collect(Collectors.toMap(TableFieldModel::getField, TableFieldModel::getType));
                        filedNames = value.stream().filter(fieldNameMap::containsKey).collect(Collectors.toList());
                    }
                    List<Map<String, Object>> dataList = Lists.newArrayList();
                    if (CollectionUtils.isNotEmpty(filedNames)) {
                        dataList = dataStoreMapper.findNodeDifHistoryList(key, filedNames, startTime, endTime, timeInterval);
                    }
                    Map<String, List<TableDifDataModel>> tableDataMap = Maps.newHashMap();
                    if (CollectionUtils.isNotEmpty(dataList)) {
                        for (String functionLogo : value) {
                            List<TableDifDataModel> tableDataList = Lists.newArrayList();
                            for (Map<String, Object> data : dataList) {
                                TableDifDataModel tableDifDataModel = new TableDifDataModel();
                                String lastDataTime = data.get("lastts").toString().substring(0, data.get("lastts").toString().indexOf("."));
                                String firstDataTime = data.get("firstts").toString().substring(0, data.get("firstts").toString().indexOf("."));
//                                data.remove("lastts");
//                                data.remove("firstts");
                                tableDifDataModel.setFieldName(functionLogo);
                                tableDifDataModel.setLastDataTime(lastDataTime);
                                tableDifDataModel.setFirstDataTime(firstDataTime);
                                //处理last值
                                if (data.containsKey("last"+functionLogo)) {
                                    tableDifDataModel.setLastFieldValue(data.get("last"+functionLogo));
                                }
                                //处理first值
                                if (data.containsKey("first"+functionLogo)) {
                                    tableDifDataModel.setFirstFieldValue(data.get("first"+functionLogo));
                                }
                                tableDataList.add(tableDifDataModel);
                            }
                            tableDataMap.put(functionLogo, tableDataList);
                        }
                    }
                    resultMap.put(key, tableDataMap);
                }
            });
        }
        return resultMap;
    }

    /**
     * 查询功能点历史数据(会返回功能点数据为空的数据)
     * @param pointTableQueryMap
     * @param startTime
     * @param endTime
     * @param timeInterval
     * @param limitSize
     * @return
     */
    @Override
    public Map<String, Map<String, List<TableDataModel>>> queryFunctionPointTableDataList(Map<String, List<String>> pointTableQueryMap, String startTime, String endTime, String timeInterval, Integer limitSize) {
        //返回的数据
        Map<String, Map<String, List<TableDataModel>>> resultMap = Maps.newHashMap();
        if (!pointTableQueryMap.isEmpty()) {
            //查询全部表名称
            List<String> tableNameList = dataStoreMapper.findAllTableIfExists();
            //根据表名和字段名查询字段值
            pointTableQueryMap.forEach((key, value) -> {
                if (tableNameList.contains(key)) {
                    //校验字段名是否存在
                    List<String> filedNames = Lists.newArrayList();
                    List<TableFieldModel> fieldNameList = dataStoreMapper.findFieldNameTypeList(key);
                    if (CollectionUtils.isNotEmpty(fieldNameList)) {
                        Map<String, String> fieldNameMap = fieldNameList.stream().collect(Collectors.toMap(TableFieldModel::getField, TableFieldModel::getType));
                        filedNames = value.stream().filter(fieldNameMap::containsKey).collect(Collectors.toList());
                    }
                    List<Map<String, Object>> dataList = Lists.newArrayList();
                    if (CollectionUtils.isNotEmpty(filedNames)) {
                        dataList = dataStoreMapper.queryTableDataList(key, filedNames, startTime, endTime, timeInterval, limitSize);
                    }
                    this.getTableDataMap(resultMap, key, value, dataList);
                }
            });
        }
        return resultMap;
    }

    //组装设备功能点历史数据
    private void getTableDataMap(Map<String, Map<String, List<TableDataModel>>> resultMap, String key, List<String> value, List<Map<String, Object>> dataMap) {
        Map<String, List<TableDataModel>> tableDataMap = value.stream().collect(Collectors.toMap(a -> a, a -> Lists.newArrayList(), (k1, k2) -> k1));
        if (CollectionUtils.isNotEmpty(dataMap)) {
            for (Map<String, Object> data : dataMap) {
                String dataTime = data.get("ts").toString().substring(0, data.get("ts").toString().indexOf("."));
                tableDataMap.forEach((fieldName, tableDataList) -> {
                    Object fieldValue = null;
                    if (data.containsKey(fieldName)) {
                        fieldValue = data.get(fieldName);
                    }
                    if (StringUtil.isNotEmpty(fieldValue) && fieldValue instanceof byte[]) {
                        fieldValue = new String((byte[]) fieldValue, StandardCharsets.UTF_8);
                    }
                    tableDataList.add(TableDataModel.builder().fieldName(fieldName).fieldValue(fieldValue).dataTime(dataTime).build());
                    tableDataMap.put(fieldName, tableDataList);
                });
            }
        }
        resultMap.put(key, tableDataMap);
    }

}
