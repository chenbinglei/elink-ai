package com.sunmax.crontab.service.impl;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.sunmax.common.constant.FunctionqConstant;
import com.sunmax.common.dto.crontab.NodeHistoryDataDto;
import com.sunmax.common.dto.data.DeviceHistoryDto;
import com.sunmax.common.dto.data.NodeDifHistoryDto;
import com.sunmax.common.util.DateUtil;
import com.sunmax.common.util.ExcelFormulaUtil;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.util.StringUtil;
import com.sunmax.common.util.oss.FileUtil;
import com.sunmax.common.vo.StaticParamVo;
import com.sunmax.common.vo.data.DeviceHistoryQueryVo;
import com.sunmax.common.vo.data.DeviceIndexQueryVo;
import com.sunmax.crontab.dao.ComputeNodeDao;
import com.sunmax.crontab.dao.NodeAddRecordDao;
import com.sunmax.crontab.dao.NodeLogInfoDao;
import com.sunmax.crontab.dao.NodeParamDao;
import com.sunmax.crontab.dto.NodeLogInfoDto;
import com.sunmax.crontab.entity.ComputeNodeEntity;
import com.sunmax.crontab.entity.NodeAddRecordEntity;
import com.sunmax.crontab.entity.NodeLogInfoEntity;
import com.sunmax.crontab.entity.NodeParamEntity;
import com.sunmax.crontab.mapper.tdengine.ComputeNodeMapper;
import com.sunmax.crontab.service.AddRecordTaskService;
import com.sunmax.crontab.service.feign.DataService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static com.sunmax.common.util.ConversionExpressionUtil.getInstanceExpressionParamList;
import static com.sunmax.common.util.DateUtil.*;
import static com.sunmax.common.util.DeviceUtil.dataTypeConvert;
import static com.sunmax.common.util.StringUtil.isNumeric;
import static com.sunmax.crontab.util.ScheduleTaskUtil.NODE_TABLE;
import static com.sunmax.crontab.util.ScheduleTaskUtil.STABLE_NAME;

@Slf4j
@Service
public class AddRecordTaskServiceImpl implements AddRecordTaskService {

    @Autowired
    private ComputeNodeMapper computeNodeMapper;

    @Autowired
    private ComputeNodeDao computeNodeDao;

    @Autowired
    private DataService dataService;

    @Autowired
    private NodeParamDao nodeParamDao;

    @Autowired
    private NodeAddRecordDao nodeAddRecordDao;

    @Autowired
    private NodeLogInfoDao nodeLogInfoDao;

    /**
     * 根据函数获取统计类型
     */
    private String getCountType(String expression) {
        String countFun = null;
        if (expression.contains(FunctionqConstant.SUM)) {//求和
            countFun = FunctionqConstant.SUM;
        } else if (expression.contains(FunctionqConstant.MAX)) {//最大值
            countFun = FunctionqConstant.MAX;
        } else if (expression.contains(FunctionqConstant.MIN)) {//最小值
            countFun = FunctionqConstant.MIN;
        } else if (expression.contains(FunctionqConstant.AVG)) {//平均值
            countFun = FunctionqConstant.AVG;
        }
        return countFun;
    }

    public static void main(String[] args) {
        List<String> instanceExpressionParamList = getInstanceExpressionParamList("jd@11-jd@93");
        System.out.println(instanceExpressionParamList);
    }

    /**
     * 根据节点id和时间补录taos数据
     * @param nodeId
     * @param startTime
     * @param endTime
     * @param addRecordId
     * @return
     */
    @Override
    public ResponseResult<String> addRecordNodeDataById(String nodeId, String startTime, String endTime, String addRecordId) {
        //存储日志数据
        List<NodeLogInfoDto> nodeLogInfoDtos = Lists.newArrayList();
        //存储数据
        List<NodeHistoryDataDto> nodeHistoryDataDtos = Lists.newArrayList();
        //定义循环函数返回日志信息
        Map<String, List<NodeLogInfoDto>> nodeLogInfoDtoMap = Maps.newHashMap();
        //定义循环函数返回值
        Map<String, List<NodeHistoryDataDto>> resultMap = Maps.newHashMap();
        //查询节点基本数据
        ComputeNodeEntity computeNodeEntity = computeNodeDao.findById(nodeId).get();
        String formulaAfter = computeNodeEntity.getFormulaAfter();
        List<String> expressionParamList = getInstanceExpressionParamList(formulaAfter);
        //统计周期
        String countPeriod = computeNodeEntity.getCountPeriod();
        if (CollectionUtils.isNotEmpty(expressionParamList)) {
            //查询节点参数数据
            List<NodeParamEntity> nodeParamEntityList = nodeParamDao.findAllByNodeId(nodeId);
            Map<String, NodeParamEntity> nodeParamEntityMap = nodeParamEntityList.stream().collect(Collectors.toMap(NodeParamEntity::getSourceCode, NodeParamEntity -> NodeParamEntity, (k1, k2) -> k1));
            expressionParamList.forEach(expression -> {
                Map<String, Object> addRecordResultMap = Maps.newHashMap();
                //先判断是否带括号，如果带则是函数，如果不带则是映射值
                if (expression.contains("(")) {
                    String function = expression.substring(0, expression.indexOf("("));
                    switch (function) {
                        case FunctionqConstant.SUM:
                        case FunctionqConstant.MAX:
                        case FunctionqConstant.MIN:
                        case FunctionqConstant.AVG: //求和、最大值、最小值、平均值
                            addRecordResultMap = addRecordCountFun(expression, getStartTime(countPeriod, startTime), endTime, countPeriod, getCountType(expression), nodeParamEntityMap);
                            break;
                        case FunctionqConstant.DIF: //差值
                            addRecordResultMap = addRecordDifFun(expression, getStartTime(countPeriod, startTime), getNextEndTime(countPeriod, endTime), countPeriod, nodeParamEntityMap);
                            break;
                        case FunctionqConstant.DIFF: //临近值差值
                            addRecordResultMap = addRecordDiffFun(expression, getStartTime(countPeriod, startTime), endTime, countPeriod, nodeParamEntityMap);
                            break;
                        case FunctionqConstant.SQRT: //平方根
                            addRecordResultMap = addRecordSqrtFun(expression, getStartTime(countPeriod, startTime), endTime, countPeriod, nodeParamEntityMap);
                            break;
                        case FunctionqConstant.POW: //求X的Y次幂
                            addRecordResultMap = addRecordPowFun(expression, getStartTime(countPeriod, startTime), endTime, countPeriod, nodeParamEntityMap);
                            break;
                        case FunctionqConstant.SQUARE:
                        case FunctionqConstant.REVERSE:
                        case FunctionqConstant.INTPART:
                        case FunctionqConstant.ABS: //平方、相反值、取整、绝对值
                            addRecordResultMap = addRecordCalculateFun(expression, getStartTime(countPeriod, startTime), endTime, countPeriod, nodeParamEntityMap);
                            break;
                    }
                }else {//没有函数就代表为映射值
                    addRecordResultMap = addRecordMappedValueFun(expression, getStartTime(countPeriod, startTime), endTime, countPeriod, nodeParamEntityMap);
                }
                if (MapUtils.isNotEmpty(addRecordResultMap)) {
                    if (addRecordResultMap.containsKey("resultList")) {
                        resultMap.put(expression, objToNodeHistoryList(addRecordResultMap.get("resultList")));
                    }
                    if (addRecordResultMap.containsKey("nodeLogInfoDtoList")) {
                        nodeLogInfoDtoMap.put(expression, objToNodeLogList(addRecordResultMap.get("nodeLogInfoDtoList")));
                    }
                }
            });
        }
        //处理函数数据
        List<LocalDateTime> dateTimeList = getLocalDateTimeBetween(strToLocalDateTime(startTime), strToLocalDateTime(endTime), countPeriod);
        dateTimeList.forEach(dateTime -> {
            //节点日志数据
            NodeLogInfoDto nodeLogInfoDto = new NodeLogInfoDto();
            nodeLogInfoDto.setLogType(2);
            nodeLogInfoDto.setNodeId(nodeId);
            nodeLogInfoDto.setStorageId(computeNodeEntity.getStorageId());
            nodeLogInfoDto.setLogTime(LocalDateTime.now());
            //节点历史数据
            NodeHistoryDataDto nodeHistoryDataDto = new NodeHistoryDataDto();
            String date = localDateTimeToStr(dateTime);
            nodeHistoryDataDto.setTs(date);
            nodeLogInfoDto.setTsTime(date);
            String formula = computeNodeEntity.getFormulaAfter();
            //标记当前节点公式是否支持计算，如果其中函数有一个值为空，则本次节点不计算，存储数据为空
            AtomicBoolean flag = new AtomicBoolean(true);
            //计算公式套入参数值
            AtomicReference<String> expressionValue = new AtomicReference<>(FileUtil.separator);

            //日志内容
            AtomicReference<String> logInfoValue = new AtomicReference<>(FileUtil.separator);
            //记录有几个参数返回缺省值
            List<Integer> isDefaultValueList = Lists.newArrayList();

            //循环公式函数，获取每个函数指定时间的值
            expressionParamList.forEach(expression -> {
                String logInfoValueStr = logInfoValue.get();
                String logValue;
                String expressionStr = expressionValue.get();
                String replace;
                //函数历史数据
                List<NodeHistoryDataDto> nodeHistoryDataDtoList = resultMap.get(expression);
                if (CollectionUtils.isNotEmpty(nodeHistoryDataDtoList)) {
                    Map<String, NodeHistoryDataDto> nodeHistoryValueMap = nodeHistoryDataDtoList.stream().collect(Collectors.toMap(NodeHistoryDataDto::getTs, nodeHistoryData -> nodeHistoryData, (k1, k2) -> k1));
                    NodeHistoryDataDto historyDataDto = nodeHistoryValueMap.get(date);
                    if (StringUtil.isEmpty(historyDataDto) || StringUtil.isEmpty(historyDataDto.getResultValue())) {
                        flag.set(false);
                        return;
                    }
                    if (expression.contains(FunctionqConstant.SQRT)) {//平方根
                        expression = expression.substring(expression.indexOf("("), expression.indexOf(")")).replace(" ","");
                    }
                    if (StringUtil.isEmpty(expressionStr)) {
                        replace = formula.replace(expression, String.valueOf(historyDataDto.getResultValue()));
                    } else {
                        replace = expressionStr.replace(expression, String.valueOf(historyDataDto.getResultValue()));
                    }
                    expressionValue.set(replace);
                }
                //函数日志数据
                List<NodeLogInfoDto> nodeLogInfoDtoList = nodeLogInfoDtoMap.get(expression);
                String logInfo = null;
                if (CollectionUtils.isNotEmpty(nodeLogInfoDtoList)) {
                    Map<String, NodeLogInfoDto> nodeLogInfoValueMap = nodeLogInfoDtoList.stream().collect(Collectors.toMap(NodeLogInfoDto::getTsTime, NodeLogInfoDto -> NodeLogInfoDto, (k1, k2) -> k1));
                    NodeLogInfoDto logInfoDto = nodeLogInfoValueMap.get(date);
                    if (StringUtil.isNotEmpty(logInfoDto)) {
                        Integer isDefaultValue = logInfoDto.getIsDefaultValue();
                        if (StringUtil.isNotEmpty(isDefaultValue)) {
                            isDefaultValueList.add(isDefaultValue);
                        }
                        //处理日志信息
                        logInfo = logInfoDto.getLogInfo();
                    }
                } else {
                    logInfo = "当前时间没记录日志信息，函数为：" + expression;
                }
                if (StringUtil.isEmpty(logInfoValueStr)) {
                    logValue = logInfo;
                } else {
                    logValue = logInfoValueStr + "," + logInfo;
                }
                logInfoValue.set(logValue);
            });
            Double resultValue = null;
            //判断是否需要计算
            if (flag.get()) {
                //通过Excel计算公式值
                resultValue = ExcelFormulaUtil.calculateFormula(expressionValue.get());
            }
            if (CollectionUtils.isNotEmpty(isDefaultValueList) && StringUtil.isEmpty(resultValue)) {
                nodeLogInfoDto.setLogLevel(1);
            } else if (CollectionUtils.isNotEmpty(isDefaultValueList) && StringUtil.isNotEmpty(resultValue)) {
                nodeLogInfoDto.setLogLevel(2);
            } else if (StringUtil.isEmpty(resultValue)){
                nodeLogInfoDto.setLogLevel(1);
            }
            nodeLogInfoDto.setLogInfo(logInfoValue.get());
            nodeHistoryDataDto.setResultValue(resultValue);
            nodeHistoryDataDtos.add(nodeHistoryDataDto);
            nodeLogInfoDtos.add(nodeLogInfoDto);
        });
        //筛选出最大和最小时间，并且删除数据库中该时间段数据最后插入数据
        String startDateTime = Collections.min(nodeHistoryDataDtos.stream().map(NodeHistoryDataDto::getTs).collect(Collectors.toList()));
        String endDateTime = Collections.max(nodeHistoryDataDtos.stream().map(NodeHistoryDataDto::getTs).collect(Collectors.toList()));
        //删除表中时间段数据
        computeNodeMapper.deleteDataByTimeBetween(NODE_TABLE + computeNodeEntity.getStorageId(), startDateTime, endDateTime);
        //批量插入数据
        batchInsertData(NODE_TABLE + computeNodeEntity.getStorageId(), STABLE_NAME, computeNodeEntity.getStorageId(), computeNodeEntity.getSiteId(), nodeHistoryDataDtos);
        //修改数据补录信息表中状态为已完成
        Optional<NodeAddRecordEntity> addRecordDaoById = nodeAddRecordDao.findById(addRecordId);
        if (addRecordDaoById.isPresent()) {
            NodeAddRecordEntity nodeAddRecordEntity = addRecordDaoById.get();
            nodeAddRecordEntity.setAddRecordState(2);
            nodeAddRecordDao.save(nodeAddRecordEntity);
        }
        //存储节点日志
        if (CollectionUtils.isNotEmpty(nodeLogInfoDtos)) {
            nodeLogInfoDao.saveAll(nodeLogInfoDtos.stream().map(nodeLogInfoDto -> {
                NodeLogInfoEntity nodeLogInfoEntity = new NodeLogInfoEntity();
                BeanUtils.copyProperties(nodeLogInfoDto, nodeLogInfoEntity);
                return nodeLogInfoEntity;
            }).collect(Collectors.toList()));
        }
        return ResponseResult.ok(ResponseResult.SUCCESS);
    }

    public static String getStartTime(String countPeriod, String startTime) {
        //分
        if (countPeriod.contains(StaticParamVo.M)) {
            long timeNum = Long.parseLong(countPeriod.substring(0, countPeriod.length() - 1));
            if (timeNum > 1) {
                startTime = getDateByType(startTime, (int) timeNum, 1, 1);
            }
        }
        return startTime;
    }

    /**
     * 节点历史数据 Object转list校验
     * @param obj
     * @return
     */
    private List<NodeHistoryDataDto> objToNodeHistoryList(Object obj) {
        List<NodeHistoryDataDto> nodeHistoryDataDtoList = Lists.newArrayList();
        if (obj instanceof ArrayList<?>) {
            for (Object o : (List<?>) obj) {
                nodeHistoryDataDtoList.add((NodeHistoryDataDto) o);
            }
        }
        return nodeHistoryDataDtoList.stream().peek(c -> {
            if (StringUtil.isNotEmpty(c.getFirstTs()) && c.getFirstTs().length() > 19) {
                c.setFirstTs(c.getFirstTs().substring(0, 19));
            }
            if (StringUtil.isNotEmpty(c.getTs()) && c.getTs().length() > 19) {
                c.setTs(c.getTs().substring(0, 19));
            }
            if (StringUtil.isNotEmpty(c.getUpdateTime()) && c.getUpdateTime().length() > 19) {
                c.setUpdateTime(c.getUpdateTime().substring(0, 19));
            }
        }).collect(Collectors.toList());
    }

    /**
     * 节点日志数据 Object转list校验
     * @param obj
     * @return
     */
    private List<NodeLogInfoDto> objToNodeLogList(Object obj) {
        List<NodeLogInfoDto> nodeLogInfoDtoList = Lists.newArrayList();
        if (obj instanceof ArrayList<?>) {
            for (Object o : (List<?>) obj) {
                nodeLogInfoDtoList.add((NodeLogInfoDto) o);
            }
        }
        return nodeLogInfoDtoList;
    }

    /**
     * 批量插入，解决sql语句过长问题
     *
     * @param tableName
     * @param stableName
     * @param storageId
     * @param siteId
     * @param nodeHistoryDataDtos
     */
    public void batchInsertData(String tableName, String stableName, Long storageId, String siteId, List<NodeHistoryDataDto> nodeHistoryDataDtos) {
        int batchSize = 1000;
        for (int i = 0; i < nodeHistoryDataDtos.size(); i += batchSize) {
            int endIndex = Math.min(i + batchSize, nodeHistoryDataDtos.size());
            List<NodeHistoryDataDto> batchList = nodeHistoryDataDtos.subList(i, endIndex);

            computeNodeMapper.batchInsertTableData(tableName, stableName, storageId, siteId, batchList);
        }
    }

    /**
     * 数据补录统计值函数
     * @param expression           函数参数
     * @param startTime            开始时间
     * @param endTime              结束时间
     * @param countPeriod          统计周期
     * @param countFun             统计函数:求和-SUM;最大值-MAX;最小值-MIN;平均值-AVG
     * @param nodeParamEntityMap   节点参数
     * @return
     */
    private Map<String, Object> addRecordCountFun(String expression, String startTime, String endTime, String countPeriod, String countFun, Map<String, NodeParamEntity> nodeParamEntityMap) {
        //返回日志数据
        List<NodeLogInfoDto> nodeLogInfoDtoList = Lists.newArrayList();
        //返回数据
        List<NodeHistoryDataDto> resultList = Lists.newArrayList();
        //获取出函数中查询参数
        String nodeParam = expression.substring(expression.indexOf("(") + 1, expression.indexOf(")")).replace(" ","");
        NodeParamEntity nodeParamEntity = nodeParamEntityMap.get(nodeParam);
        String[] paramArray = nodeParam.split("@");

        List<LocalDateTime> dateTimeList = getLocalDateTimeBetween(strToLocalDateTime(startTime), strToLocalDateTime(endTime), countPeriod);
        if ("gnd".equals(paramArray[0])) {
            //设备id
            String deviceId = paramArray[1];
            //功能点标识
            String functionLogo = paramArray[2];
            //如果索引号不为空，则说明要查询枪历史数据
            Integer indexNum = nodeParamEntity.getIndexNum();
            //获取功能点统计历史数据
            List<DeviceHistoryDto> deviceHistoryDtos;
            if (StringUtil.isNotEmpty(indexNum)) {
                deviceHistoryDtos = findDeviceHistoryIndexValueList(deviceId, functionLogo, getStartMill(startTime), getEndMill(endTime), null, indexNum, null);
            } else {
                deviceHistoryDtos = findDeviceHistoryValueList(deviceId, functionLogo, getStartMill(startTime), getEndMill(endTime), null);
            }
            //根据节点参数最大最小值过滤一遍数据
            deviceHistoryDtos = judgeFunPiontDataList(deviceHistoryDtos, nodeParamEntity);
            if (CollectionUtils.isNotEmpty(deviceHistoryDtos)) {
                for (int i = 0; i < dateTimeList.size() - 1; i++) {
                    //日志信息
                    String logInfo = null;
                    //节点日志数据
                    NodeLogInfoDto nodeLogInfoDto = new NodeLogInfoDto();
                    nodeLogInfoDto.setLogTime(LocalDateTime.now());
                    //节点历史数据
                    NodeHistoryDataDto nodeHistoryDataDto = new NodeHistoryDataDto();
                    //开始时间
                    LocalDateTime firstDateTime = dateTimeList.get(i);
                    //结束时间
                    LocalDateTime lastDateTime = dateTimeList.get(i + 1);
                    //过滤出指定时间段数据
                    List<DeviceHistoryDto> deviceHistoryDtoList = deviceHistoryDtos.stream().filter(d -> strToLocalDateTime(d.getDateTime()).isAfter(firstDateTime) && strToLocalDateTime(d.getDateTime()).isBefore(lastDateTime)).collect(Collectors.toList());
                    Double resultValue = null;
                    if (CollectionUtils.isNotEmpty(deviceHistoryDtoList)) {
                        switch (countFun) {
                            case FunctionqConstant.SUM: //求和
                                resultValue = deviceHistoryDtoList.stream().mapToDouble(s -> Double.parseDouble(String.valueOf(s.getDataValue()))).filter(StringUtil::isNotEmpty).sum();
                                break;
                            case FunctionqConstant.MAX: //最大值
                                resultValue = (Double) Objects.requireNonNull(deviceHistoryDtoList.stream().max(Comparator.comparing(s -> Double.parseDouble(String.valueOf(s.getDataValue())))).filter(StringUtil::isNotEmpty).orElse(null)).getDataValue();
                                break;
                            case FunctionqConstant.MIN: //最小值
                                resultValue = (Double) Objects.requireNonNull(deviceHistoryDtoList.stream().min(Comparator.comparing(s -> Double.parseDouble(String.valueOf(s.getDataValue())))).filter(StringUtil::isNotEmpty).orElse(null)).getDataValue();
                                break;
                            case FunctionqConstant.AVG: //平均值
                                resultValue = deviceHistoryDtoList.stream().collect(Collectors.averagingDouble(s -> Double.parseDouble(String.valueOf(s.getDataValue()))));
                                break;
                        }
                    }
                    if (StringUtil.isNotEmpty(resultValue)) {
                        logInfo = "参数（" + expression + "）,数据为null或未在参数设定范围之内，返回空值，原始数据为：" + resultValue;
                    }
                    nodeHistoryDataDto.setTs(localDateTimeToStr(firstDateTime));
                    nodeLogInfoDto.setTsTime(localDateTimeToStr(firstDateTime));
                    nodeHistoryDataDto.setResultValue(resultValue);
                    nodeLogInfoDto.setLogInfo(logInfo);
                    resultList.add(nodeHistoryDataDto);
                    nodeLogInfoDtoList.add(nodeLogInfoDto);
                }
            }
        } else if ("jd".equals(paramArray[0])){
            long storageId = Long.parseLong(paramArray[1]);
            //查询节点统计数据
            List<NodeHistoryDataDto> nodeHistoryDataDtoList = judgeNodeDataList(computeNodeMapper.findNodeTaosDataList(NODE_TABLE + storageId, getStartMill(startTime), getEndMill(endTime), null, null), nodeParamEntity);
            if (CollectionUtils.isNotEmpty(nodeHistoryDataDtoList)) {
                for (int i = 0; i < dateTimeList.size() - 1; i++) {
                    //日志信息
                    String logInfo = null;
                    //节点日志数据
                    NodeLogInfoDto nodeLogInfoDto = new NodeLogInfoDto();
                    nodeLogInfoDto.setLogTime(LocalDateTime.now());
                    //节点历史数据
                    NodeHistoryDataDto nodeHistoryDataDto = new NodeHistoryDataDto();
                    //开始时间
                    LocalDateTime firstDateTime = dateTimeList.get(i);
                    //结束时间
                    LocalDateTime lastDateTime = dateTimeList.get(i + 1);
                    //过滤出指定时间段数据
                    List<NodeHistoryDataDto> nodeHistoryDataDtos = nodeHistoryDataDtoList.stream().filter(d -> strToLocalDateTime(d.getTs()).isAfter(firstDateTime) && strToLocalDateTime(d.getTs()).isBefore(lastDateTime)).collect(Collectors.toList());
                    Double resultValue = null;
                    if (CollectionUtils.isNotEmpty(nodeHistoryDataDtos)) {
                        switch (countFun) {
                            case FunctionqConstant.SUM: //求和
                                resultValue = nodeHistoryDataDtos.stream().mapToDouble(s -> Double.parseDouble(String.valueOf(s.getResultValue()))).filter(StringUtil::isNotEmpty).sum();
                                break;
                            case FunctionqConstant.MAX: //最大值
                                resultValue = Objects.requireNonNull(nodeHistoryDataDtos.stream().max(Comparator.comparing(s -> Double.parseDouble(String.valueOf(s.getResultValue())))).filter(StringUtil::isNotEmpty).orElse(null)).getResultValue();
                                break;
                            case FunctionqConstant.MIN: //最小值
                                resultValue = Objects.requireNonNull(nodeHistoryDataDtos.stream().min(Comparator.comparing(s -> Double.parseDouble(String.valueOf(s.getResultValue())))).filter(StringUtil::isNotEmpty).orElse(null)).getResultValue();
                                break;
                            case FunctionqConstant.AVG: //平均值
                                resultValue = nodeHistoryDataDtos.stream().collect(Collectors.averagingDouble(s -> Double.parseDouble(String.valueOf(s.getResultValue()))));
                                break;
                        }
                    }
                    if (StringUtil.isNotEmpty(resultValue)) {
                        logInfo = "参数（" + expression + "）,数据为null或未在参数设定范围之内，返回空值，原始数据为：" + resultValue;
                    }
                    nodeHistoryDataDto.setTs(localDateTimeToStr(firstDateTime));
                    nodeLogInfoDto.setTsTime(localDateTimeToStr(firstDateTime));
                    nodeHistoryDataDto.setResultValue(resultValue);
                    nodeLogInfoDto.setLogInfo(logInfo);
                    resultList.add(nodeHistoryDataDto);
                    nodeLogInfoDtoList.add(nodeLogInfoDto);
                }
            }
        } else if (isNumeric(expression)){
            //如果都不是上面两种，则认为是公式中填的静态数值，则根据开始结束时间和周期获取时间，返回数据
            getLocalDateTimeBetween(strToLocalDateTime(startTime), strToLocalDateTime(endTime), countPeriod)
                    .stream().map(DateUtil::localDateTimeToStr).collect(Collectors.toList()).forEach(dateTime -> {
                        NodeHistoryDataDto nodeHistoryDataDto = new NodeHistoryDataDto();
                        nodeHistoryDataDto.setTs(dateTime);
                        nodeHistoryDataDto.setResultValue(Double.valueOf(expression));
                        resultList.add(nodeHistoryDataDto);
                    });

        }
        Map<String, Object> hashMap = Maps.newHashMap();
        hashMap.put("resultList", resultList);
        hashMap.put("nodeLogInfoDtoList", nodeLogInfoDtoList);
        return hashMap;
    }

    /**
     * 判断功能点原始数据是否在参数设定范围内
     * @param deviceHistoryDtos
     * @param nodeParamEntity
     * @return
     */
    private List<DeviceHistoryDto> judgeFunPiontDataList(List<DeviceHistoryDto> deviceHistoryDtos, NodeParamEntity nodeParamEntity) {
        if (CollectionUtils.isEmpty(deviceHistoryDtos)) {
            return Lists.newArrayList();
        }
        return deviceHistoryDtos.stream()
                .map(deviceHistoryDto -> {
                    DeviceHistoryDto deviceHistory = new DeviceHistoryDto();
                    BeanUtils.copyProperties(deviceHistoryDto, deviceHistory);
                    Object dataValue = deviceHistoryDto.getDataValue();
                    deviceHistory.setDataValue(dataValue);
                    if (StringUtil.isNotEmpty(dataValue) && StringUtil.isNotEmpty(nodeParamEntity.getMaxValue())
                            && StringUtil.isNotEmpty(nodeParamEntity.getMinValue()) &&
                            Double.parseDouble(String.valueOf(dataValue)) <= nodeParamEntity.getMaxValue() &&
                            Double.parseDouble(String.valueOf(dataValue)) >= nodeParamEntity.getMinValue()) {
                        deviceHistory.setDataValue(dataValue);
                    } else if (StringUtil.isNotEmpty(dataValue) && StringUtil.isNotEmpty(nodeParamEntity.getMaxValue())
                            && StringUtil.isNotEmpty(nodeParamEntity.getMinValue()) &&
                            (Double.parseDouble(String.valueOf(dataValue)) >= nodeParamEntity.getMaxValue() ||
                            Double.parseDouble(String.valueOf(dataValue)) <= nodeParamEntity.getMinValue())){
                        deviceHistory.setDataValue(nodeParamEntity.getDefaultValue());
                    }
                    return deviceHistory;
                })
                .collect(Collectors.toList());
    }

    /**
     * 判断功能点原始数据是否在参数设定范围内
     * @param deviceHistoryDtos
     * @param nodeParamEntity
     * @return
     */
    private List<NodeDifHistoryDto> judgeDifFunPiontDataList(List<NodeDifHistoryDto> deviceHistoryDtos, NodeParamEntity nodeParamEntity) {
        if (CollectionUtils.isEmpty(deviceHistoryDtos)) {
            return Lists.newArrayList();
        }
        return deviceHistoryDtos.stream()
                .map(deviceHistoryDto -> {
                    NodeDifHistoryDto deviceHistory = new NodeDifHistoryDto();
                    BeanUtils.copyProperties(deviceHistoryDto, deviceHistory);
                    //过滤last值
                    Object lastDataValue = deviceHistoryDto.getLastDataValue();
                    deviceHistory.setLastDataValue(lastDataValue);
                    if (StringUtil.isNotEmpty(lastDataValue) && StringUtil.isNotEmpty(nodeParamEntity.getMaxValue())
                            && StringUtil.isNotEmpty(nodeParamEntity.getMinValue()) &&
                            Double.parseDouble(String.valueOf(lastDataValue)) <= nodeParamEntity.getMaxValue() &&
                            Double.parseDouble(String.valueOf(lastDataValue)) >= nodeParamEntity.getMinValue()) {
                        deviceHistory.setLastDataValue(lastDataValue);
                    } else if (StringUtil.isNotEmpty(lastDataValue) && StringUtil.isNotEmpty(nodeParamEntity.getMaxValue())
                            && StringUtil.isNotEmpty(nodeParamEntity.getMinValue()) &&
                            (Double.parseDouble(String.valueOf(lastDataValue)) >= nodeParamEntity.getMaxValue() ||
                            Double.parseDouble(String.valueOf(lastDataValue)) <= nodeParamEntity.getMinValue())){
                        deviceHistory.setLastDataValue(nodeParamEntity.getDefaultValue());
                    }
                    //过滤first值
                    Object firstDataValue = deviceHistoryDto.getFirstDataValue();
                    deviceHistory.setFirstDataValue(firstDataValue);
                    if (StringUtil.isNotEmpty(firstDataValue) && StringUtil.isNotEmpty(nodeParamEntity.getMaxValue())
                            && StringUtil.isNotEmpty(nodeParamEntity.getMinValue()) &&
                            Double.parseDouble(String.valueOf(firstDataValue)) <= nodeParamEntity.getMaxValue() &&
                            Double.parseDouble(String.valueOf(firstDataValue)) >= nodeParamEntity.getMinValue()) {
                        deviceHistory.setFirstDataValue(firstDataValue);
                    } else if (StringUtil.isNotEmpty(firstDataValue) && StringUtil.isNotEmpty(nodeParamEntity.getMaxValue())
                            && StringUtil.isNotEmpty(nodeParamEntity.getMinValue()) &&
                            (Double.parseDouble(String.valueOf(firstDataValue)) >= nodeParamEntity.getMaxValue() ||
                            Double.parseDouble(String.valueOf(firstDataValue)) <= nodeParamEntity.getMinValue())){
                        deviceHistory.setFirstDataValue(nodeParamEntity.getDefaultValue());
                    }
                    return deviceHistory;
                })
                .collect(Collectors.toList());
    }

    /**
     * 判断计算节点数据是否在参数设定范围内
     * @param nodeHistoryDataDtoList
     * @param nodeParamEntity
     * @return
     */
    private List<NodeHistoryDataDto> judgeNodeDataList(List<NodeHistoryDataDto> nodeHistoryDataDtoList, NodeParamEntity nodeParamEntity) {
        if (CollectionUtils.isEmpty(nodeHistoryDataDtoList)) {
            return Lists.newArrayList();
        }
        return nodeHistoryDataDtoList.stream()
                .map(nodeHistoryDataDto -> {
                    NodeHistoryDataDto nodeHistoryData = new NodeHistoryDataDto();
                    BeanUtils.copyProperties(nodeHistoryDataDto, nodeHistoryData);

                    Double resultValue = nodeHistoryDataDto.getResultValue();
                    nodeHistoryData.setResultValue(resultValue);
                    if (StringUtil.isNotEmpty(resultValue) && StringUtil.isNotEmpty(nodeParamEntity.getMaxValue())
                            && StringUtil.isNotEmpty(nodeParamEntity.getMinValue()) &&
                            Double.parseDouble(String.valueOf(resultValue)) <= nodeParamEntity.getMaxValue() &&
                            Double.parseDouble(String.valueOf(resultValue)) >= nodeParamEntity.getMinValue()) {
                        nodeHistoryData.setResultValue(resultValue);
                    } else if (StringUtil.isNotEmpty(resultValue) && StringUtil.isNotEmpty(nodeParamEntity.getMaxValue())
                            && StringUtil.isNotEmpty(nodeParamEntity.getMinValue()) &&
                            (Double.parseDouble(String.valueOf(resultValue)) >= nodeParamEntity.getMaxValue() ||
                                    Double.parseDouble(String.valueOf(resultValue)) <= nodeParamEntity.getMinValue())){
                        nodeHistoryData.setResultValue(nodeParamEntity.getDefaultValue());
                    }


                    /*nodeHistoryData.setResultValue(
                            isValidDataValue(nodeHistoryDataDto.getResultValue(), nodeParamEntity) ?
                                    nodeHistoryDataDto.getResultValue() :
                                    nodeParamEntity.getDefaultValue()
                    );*/
                    return nodeHistoryData;
                })
                .collect(Collectors.toList());
    }

    /**
     * 数据补录差值函数
     * @param expression           函数参数
     * @param startTime            开始时间
     * @param endTime              结束时间
     * @param countPeriod          统计周期
     * @param nodeParamEntityMap   节点参数
     * @return
     */
    private Map<String, Object> addRecordDifFun(String expression, String startTime, String endTime, String countPeriod, Map<String, NodeParamEntity> nodeParamEntityMap) {
        //返回日志数据
        List<NodeLogInfoDto> nodeLogInfoDtoList = Lists.newArrayList();
        //返回数据
        List<NodeHistoryDataDto> resultList = Lists.newArrayList();
        //源数据
        List<NodeHistoryDataDto> nodeHistoryDataDtoList = Lists.newArrayList();
        //获取出函数中查询参数
        String nodeParam = expression.substring(expression.indexOf("(") + 1, expression.indexOf(")")).replace(" ","");
        NodeParamEntity nodeParamEntity = nodeParamEntityMap.get(nodeParam);
        String[] paramArray = nodeParam.split("@");
        if ("gnd".equals(paramArray[0])) {
            //设备id
            String deviceId = paramArray[1];
            //功能点标识
            String functionLogo = paramArray[2];
            //如果索引号不为空，则说明要查询枪历史数据
            Integer indexNum = nodeParamEntity.getIndexNum();
            List<DeviceHistoryDto> deviceHistoryDtos;
            if (StringUtil.isNotEmpty(indexNum)) {
                deviceHistoryDtos = findDeviceHistoryIndexValueList(deviceId, functionLogo, getStartMill(startTime), getEndMill(endTime), countPeriod, indexNum, null);
            } else {
                //获取功能点历史数据
                deviceHistoryDtos = findDeviceDifferenceListFeign(deviceId, functionLogo, getStartMill(startTime), getEndMill(endTime), countPeriod, null);
            }
            //根据节点参数最大最小值过滤一遍数据
            deviceHistoryDtos = judgeFunPiontDataList(deviceHistoryDtos, nodeParamEntity);
            //查询历史数据
            if (CollectionUtils.isNotEmpty(deviceHistoryDtos)) {
                nodeHistoryDataDtoList = deviceHistoryDtos.stream().map(deviceHistoryDto -> {
                    NodeHistoryDataDto nodeHistoryDataDto = new NodeHistoryDataDto();
                    nodeHistoryDataDto.setTs(deviceHistoryDto.getDateTime());
                    Integer dataType = deviceHistoryDto.getDataType();
                    Object dataValue = deviceHistoryDto.getDataValue();
                    Double aDouble = dataTypeConvert(dataType, dataValue);
                    nodeHistoryDataDto.setResultValue(aDouble);
                    return nodeHistoryDataDto;
                }).collect(Collectors.toList());
            }
        } else if ("jd".equals(paramArray[0])){
            long storageId = Long.parseLong(paramArray[1]);
            //查询节点历史数据
            List<NodeHistoryDataDto> nodeTaosDataList = judgeNodeDataList(computeNodeMapper.findNodeTaosDataList(NODE_TABLE + storageId, getStartMill(startTime), getEndMill(endTime), countPeriod, null), nodeParamEntity);
            if (CollectionUtils.isNotEmpty(nodeTaosDataList)) {
                nodeHistoryDataDtoList = nodeTaosDataList.stream().peek(nodeHistoryDataDto -> {
                    Double resultValue = nodeHistoryDataDto.getResultValue();
                    nodeHistoryDataDto.setResultValue(resultValue != null ? judgeParam(resultValue, nodeParamEntity):nodeParamEntity.getDefaultValue());
                    nodeHistoryDataDto.setTs(nodeHistoryDataDto.getTs());
                }).collect(Collectors.toList());
            }
        } else if (isNumeric(expression)){
            //如果都不是上面两种，则认为是公式中填的静态数值，则根据开始结束时间和周期获取时间，返回数据
            getLocalDateTimeBetween(strToLocalDateTime(startTime), strToLocalDateTime(endTime), countPeriod)
                    .stream().map(DateUtil::localDateTimeToStr).collect(Collectors.toList()).forEach(dateTime -> {
                        NodeHistoryDataDto nodeHistoryDataDto = new NodeHistoryDataDto();
                        nodeHistoryDataDto.setTs(dateTime);
                        nodeHistoryDataDto.setResultValue(Double.valueOf(expression));
                        resultList.add(nodeHistoryDataDto);
                    });

        }
        if (CollectionUtils.isNotEmpty(nodeHistoryDataDtoList)) {
            Map<String, NodeHistoryDataDto> nodeHistoryDataDtoMap = nodeHistoryDataDtoList.stream().collect(Collectors.toMap(NodeHistoryDataDto::getTs, NodeHistoryDataDto -> NodeHistoryDataDto, (k1, k2) -> k1));
            List<LocalDateTime> dateTimeList = getLocalDateTimeBetween(strToLocalDateTime(startTime), strToLocalDateTime(endTime), countPeriod);
            for (int i = 0; i < dateTimeList.size() - 1; i++) {
                //日志信息
                String logInfo;
                //节点日志数据
                NodeLogInfoDto nodeLogInfoDto = new NodeLogInfoDto();
                nodeLogInfoDto.setLogTime(LocalDateTime.now());
                //节点历史数据
                NodeHistoryDataDto nodeHistoryDataDto = new NodeHistoryDataDto();
                //last数据
                Double lastValue = null;
                String lastDateTime = localDateTimeToStr(dateTimeList.get(i + 1));
                //first数据
                Double firstValue = null;
                String firstDateTime = localDateTimeToStr(dateTimeList.get(i));
                nodeHistoryDataDto.setTs(firstDateTime);
                nodeLogInfoDto.setTsTime(firstDateTime);
                //获取last数据
                NodeHistoryDataDto lastNodeHistoryDataDto = nodeHistoryDataDtoMap.get(lastDateTime);
                if (StringUtil.isNotEmpty(lastNodeHistoryDataDto)) {
                    lastValue = lastNodeHistoryDataDto.getResultValue();
                }
                //获取first数据
                NodeHistoryDataDto firstNodeHistoryDataDto = nodeHistoryDataDtoMap.get(firstDateTime);
                if (StringUtil.isNotEmpty(firstNodeHistoryDataDto)) {
                    firstValue = firstNodeHistoryDataDto.getResultValue();
                }
                //计算差值
                if (lastValue != null && firstValue != null && StringUtil.isNotEmpty(lastValue) && StringUtil.isNotEmpty(firstValue)) {
                    nodeHistoryDataDto.setResultValue(lastValue - firstValue);
                    logInfo = "参数（" + expression + "）,数据正常";
                } else {
                    logInfo = "参数（" + expression + "）,last或first值为空，last值为：" + lastValue + "first值为：" + firstValue;
                }
                nodeLogInfoDto.setLogInfo(logInfo);
                resultList.add(nodeHistoryDataDto);
                nodeLogInfoDtoList.add(nodeLogInfoDto);
            }
        }
        Map<String, Object> hashMap = Maps.newHashMap();
        hashMap.put("resultList", resultList);
        hashMap.put("nodeLogInfoDtoList", nodeLogInfoDtoList);
        return hashMap;
    }

    /**
     * 数据补录临近值差值函数
     * @param expression           函数参数
     * @param startTime            开始时间
     * @param endTime              结束时间
     * @param countPeriod          统计周期
     * @param nodeParamEntityMap   节点参数
     * @return
     */
    private Map<String, Object> addRecordDiffFun(String expression, String startTime, String endTime, String countPeriod, Map<String, NodeParamEntity> nodeParamEntityMap) {
        //返回日志数据
        List<NodeLogInfoDto> nodeLogInfoDtoList = Lists.newArrayList();
        //返回数据
        List<NodeHistoryDataDto> resultList = Lists.newArrayList();
        //源数据
        List<NodeHistoryDataDto> nodeHistoryDataDtoList = Lists.newArrayList();
        //获取出函数中查询参数
        String nodeParam = expression.substring(expression.indexOf("(") + 1, expression.indexOf(")")).replace(" ","");
        NodeParamEntity nodeParamEntity = nodeParamEntityMap.get(nodeParam);
        String[] paramArray = nodeParam.split("@");
        if ("gnd".equals(paramArray[0])) {
            //设备id
            String deviceId = paramArray[1];
            //功能点标识
            String functionLogo = paramArray[2];
            //如果索引号不为空，则说明要查询枪历史数据
            Integer indexNum = nodeParamEntity.getIndexNum();
            List<NodeDifHistoryDto> nodeDifHistoryDtos;
            if (StringUtil.isNotEmpty(indexNum)) {
                nodeDifHistoryDtos = findNodeDifHistoryIndexListFeign(deviceId, functionLogo, getStartMill(startTime), getEndMill(endTime), countPeriod, indexNum);
            } else {
                nodeDifHistoryDtos = findNodeDifHistoryList(deviceId, functionLogo, getStartMill(startTime), getEndMill(endTime), countPeriod);
            }
            log.info("原始历史数据：{}", JSON.toJSONString(nodeDifHistoryDtos));
            //根据节点参数最大最小值过滤一遍
            nodeDifHistoryDtos = judgeDifFunPiontDataList(nodeDifHistoryDtos, nodeParamEntity);
            log.info("过滤后历史数据：{}", JSON.toJSONString(nodeDifHistoryDtos));
            if (CollectionUtils.isNotEmpty(nodeDifHistoryDtos)) {
                nodeHistoryDataDtoList = nodeDifHistoryDtos.stream().map(deviceHistoryDto -> {
                    NodeHistoryDataDto nodeHistoryDataDto = new NodeHistoryDataDto();
                    Integer dataType = deviceHistoryDto.getDataType();
                    //last数据
                    nodeHistoryDataDto.setTs(deviceHistoryDto.getLastDateTime());
                    Object lastDataValue = deviceHistoryDto.getLastDataValue();
                    Double lastDouble = dataTypeConvert(dataType, lastDataValue);
                    nodeHistoryDataDto.setResultValue(lastDouble);
                    //first数据
                    Object firstDataValue = deviceHistoryDto.getFirstDataValue();
                    Double firstDouble = dataTypeConvert(dataType, firstDataValue);
                    nodeHistoryDataDto.setFirstTs(deviceHistoryDto.getFirstDateTime());
                    nodeHistoryDataDto.setFirstResultValue(firstDouble);
                    return nodeHistoryDataDto;
                }).collect(Collectors.toList());
            }
        } else if ("jd".equals(paramArray[0])){
            long storageId = Long.parseLong(paramArray[1]);
            //查询节点历史数据
            List<NodeHistoryDataDto> nodeTaosDataList = judgeNodeDataList(computeNodeMapper.findNodeDataListByTimeInterval(NODE_TABLE + storageId, getStartMill(startTime), getEndMill(endTime), countPeriod), nodeParamEntity);
            if (CollectionUtils.isNotEmpty(nodeTaosDataList)) {
                nodeHistoryDataDtoList = nodeTaosDataList.stream().peek(nodeHistoryDataDto -> {
                    Double resultValue = nodeHistoryDataDto.getResultValue();
                    nodeHistoryDataDto.setResultValue(resultValue != null ? judgeParam(resultValue, nodeParamEntity):nodeParamEntity.getDefaultValue());
                    nodeHistoryDataDto.setTs(nodeHistoryDataDto.getTs());
                    Double firstResultValue = nodeHistoryDataDto.getFirstResultValue();
                    nodeHistoryDataDto.setFirstResultValue(firstResultValue != null ? judgeParam(firstResultValue, nodeParamEntity):nodeParamEntity.getDefaultValue());
                }).collect(Collectors.toList());
            }
        } else if (isNumeric(expression)){
            //如果都不是上面两种，则认为是公式中填的静态数值，则根据开始结束时间和周期获取时间，返回数据
            getLocalDateTimeBetween(strToLocalDateTime(startTime), strToLocalDateTime(endTime), countPeriod)
                    .stream().map(DateUtil::localDateTimeToStr).collect(Collectors.toList()).forEach(dateTime -> {
                        NodeHistoryDataDto nodeHistoryDataDto = new NodeHistoryDataDto();
                        nodeHistoryDataDto.setTs(dateTime);
                        nodeHistoryDataDto.setResultValue(Double.valueOf(expression));
                        resultList.add(nodeHistoryDataDto);
                    });

        }
        if (CollectionUtils.isNotEmpty(nodeHistoryDataDtoList)) {
            Map<String, NodeHistoryDataDto> firstQueryDateMap = nodeHistoryDataDtoList.stream().filter(n -> StringUtil.isNotEmpty(n.getFirstTs())).collect(Collectors.toMap(NodeHistoryDataDto::getFirstTs, NodeValueDto -> NodeValueDto, (k1, k2) -> k1));
            List<LocalDateTime> dateTimeList = getLocalDateTimeBetween(strToLocalDateTime(startTime), strToLocalDateTime(endTime), countPeriod);
            dateTimeList.forEach(dateTime -> {
                //节点历史数据
                NodeHistoryDataDto nodeHistoryDataDto = new NodeHistoryDataDto();
                nodeHistoryDataDto.setTs(localDateTimeToStr(dateTime));
                //日志信息
                String logInfo = null;
                //节点日志数据
                NodeLogInfoDto nodeLogInfoDto = new NodeLogInfoDto();
                nodeLogInfoDto.setLogTime(LocalDateTime.now());
                nodeLogInfoDto.setTsTime(localDateTimeToStr(dateTime));
                NodeHistoryDataDto nodeHistory = firstQueryDateMap.get(localDateTimeToStr(dateTime));
                Double resultValue = null;
                if (StringUtil.isNotEmpty(nodeHistory)) {
                    Double lastResultValue = nodeHistory.getResultValue();
                    Double firstResultValue = nodeHistory.getFirstResultValue();
                    nodeLogInfoDto.setLogLevel(1);
                    if (StringUtil.isNotEmpty(lastResultValue) && StringUtil.isNotEmpty(firstResultValue)) {
                        resultValue = lastResultValue - firstResultValue;
                    } else if (StringUtil.isEmpty(lastResultValue) && StringUtil.isEmpty(firstResultValue)) {
                        logInfo = "DIFF(" + nodeParam + "),last和first数据缺失";
                    } else if (StringUtil.isNotEmpty(lastResultValue) && StringUtil.isEmpty(firstResultValue)) {
                        logInfo = "DIFF(" + nodeParam + "),first数据缺失";
                    } else if (StringUtil.isEmpty(lastResultValue) && StringUtil.isNotEmpty(firstResultValue)) {
                        logInfo = "DIFF(" + nodeParam + "),last数据缺失";
                    }
                } else {
                    nodeLogInfoDto.setLogLevel(1);
                    nodeLogInfoDto.setLogInfo("DIF(" + nodeParam + "),first数据缺失");
                }
                nodeHistoryDataDto.setResultValue(resultValue);
                nodeLogInfoDto.setLogInfo(logInfo);
                resultList.add(nodeHistoryDataDto);
                nodeLogInfoDtoList.add(nodeLogInfoDto);
            });
        }
        Map<String, Object> hashMap = Maps.newHashMap();
        hashMap.put("resultList", resultList);
        hashMap.put("nodeLogInfoDtoList", nodeLogInfoDtoList);
        return hashMap;
    }

    /**
     * 数据补录平方根函数
     * @param expression           函数参数
     * @param startTime            开始时间
     * @param endTime              结束时间
     * @param countPeriod          统计周期
     * @param nodeParamEntityMap   节点参数
     * @return
     */
    private Map<String, Object> addRecordSqrtFun(String expression, String startTime, String endTime, String countPeriod, Map<String, NodeParamEntity> nodeParamEntityMap) {
        //返回日志数据
        List<NodeLogInfoDto> nodeLogInfoDtoList = Lists.newArrayList();
        //返回数据
        List<NodeHistoryDataDto> resultList = Lists.newArrayList();
        //获取出函数中查询参数
        String nodeParam = expression.substring(expression.indexOf("(") + 1, expression.indexOf(")")).replace(" ","");
        NodeParamEntity nodeParamEntity = nodeParamEntityMap.get(nodeParam);
        String[] paramArray = nodeParam.split("@");
        if ("gnd".equals(paramArray[0])) {
            //设备id
            String deviceId = paramArray[1];
            //功能点标识
            String functionLogo = paramArray[2];
            //如果索引号不为空，则说明要查询枪历史数据
            Integer indexNum = nodeParamEntity.getIndexNum();
            List<DeviceHistoryDto> deviceHistoryDtoList;
            if (StringUtil.isNotEmpty(indexNum)) {
                deviceHistoryDtoList = findDeviceHistoryIndexValueList(deviceId, functionLogo, getStartMill(startTime), getEndMill(endTime), countPeriod, indexNum, null);
            } else {
                deviceHistoryDtoList = findDeviceHistoryValueList(deviceId, functionLogo, getStartMill(startTime), getEndMill(endTime), countPeriod);
            }
            if (CollectionUtils.isNotEmpty(deviceHistoryDtoList)) {
                deviceHistoryDtoList.forEach(deviceHistoryDto -> {
                    Double resultValue = nodeParamEntity.getDefaultValue();
                    //节点历史数据
                    NodeHistoryDataDto nodeHistoryDataDto = new NodeHistoryDataDto();
                    Integer dataType = deviceHistoryDto.getDataType();
                    Object dataValue = deviceHistoryDto.getDataValue();
                    //日志信息
                    String logInfo;
                    //返回值是否是缺省值
                    Integer isDefaultValue = null;
                    //节点日志
                    NodeLogInfoDto nodeLogInfoDto = new NodeLogInfoDto();
                    nodeLogInfoDto.setLogTime(LocalDateTime.now());
                    nodeLogInfoDto.setTsTime(deviceHistoryDto.getDateTime());
                    Double aDouble = dataTypeConvert(dataType, dataValue);
                    if (StringUtil.isNotEmpty(aDouble)) {
                        resultValue = judgeParam(aDouble, nodeParamEntity);
                        logInfo = "参数（" + expression + "）,数据正常";
                    } else {
                        isDefaultValue = 1;
                        logInfo = "参数（" + expression + "）,数据为null或未在参数设定范围之内，返回缺省值，原始数据为：" + aDouble;
                    }
                    nodeLogInfoDto.setIsDefaultValue(isDefaultValue);
                    nodeLogInfoDto.setLogInfo(logInfo);
                    nodeHistoryDataDto.setResultValue(resultValue);
                    nodeHistoryDataDto.setTs(deviceHistoryDto.getDateTime());
                    resultList.add(nodeHistoryDataDto);
                    nodeLogInfoDtoList.add(nodeLogInfoDto);
                });
            }
        } else if ("jd".equals(paramArray[0])){
            long storageId = Long.parseLong(paramArray[1]);
            //查询节点历史数据
            List<NodeHistoryDataDto> nodeTaosDataList = computeNodeMapper.findNodeTaosDataList(NODE_TABLE + storageId, getStartMill(startTime), getEndMill(endTime), countPeriod, null);
            if (CollectionUtils.isNotEmpty(nodeTaosDataList)) {
                nodeTaosDataList.forEach(nodeHistoryDataDto -> {
                    Double nodeValue = nodeHistoryDataDto.getResultValue();
                    Double resultValue = nodeParamEntity.getDefaultValue();
                    //日志信息
                    String logInfo;
                    //返回值是否是缺省值
                    Integer isDefaultValue = null;
                    //节点日志
                    NodeLogInfoDto nodeLogInfoDto = new NodeLogInfoDto();
                    nodeLogInfoDto.setLogTime(LocalDateTime.now());
                    nodeLogInfoDto.setTsTime(nodeHistoryDataDto.getTs());
                    if (StringUtil.isNotEmpty(nodeValue)) {
                        resultValue = judgeParam(nodeValue, nodeParamEntity);
                        logInfo = "参数（" + expression + "）,数据正常";
                    } else {
                        isDefaultValue = 1;
                        logInfo = "参数（" + expression + "）,数据为null或未在参数设定范围之内，返回缺省值，原始数据为：" + nodeValue;
                    }
                    nodeLogInfoDto.setIsDefaultValue(isDefaultValue);
                    nodeLogInfoDto.setLogInfo(logInfo);
                    nodeHistoryDataDto.setResultValue(resultValue);
                    resultList.add(nodeHistoryDataDto);
                    nodeLogInfoDtoList.add(nodeLogInfoDto);
                });
            }
        } else if (isNumeric(expression)){
            //如果都不是上面两种，则认为是公式中填的静态数值，则根据开始结束时间和周期获取时间，返回数据
            getLocalDateTimeBetween(strToLocalDateTime(startTime), strToLocalDateTime(endTime), countPeriod)
                    .stream().map(DateUtil::localDateTimeToStr).collect(Collectors.toList()).forEach(dateTime -> {
                        NodeHistoryDataDto nodeHistoryDataDto = new NodeHistoryDataDto();
                        nodeHistoryDataDto.setTs(dateTime);
                        nodeHistoryDataDto.setResultValue(Double.valueOf(expression));
                        resultList.add(nodeHistoryDataDto);
                    });

        }
        Map<String, Object> hashMap = Maps.newHashMap();
        hashMap.put("resultList", resultList);
        hashMap.put("nodeLogInfoDtoList", nodeLogInfoDtoList);
        return hashMap;
    }

    /**
     * 数据补录求X的Y次幂函数
     * @param expression           函数参数
     * @param startTime            开始时间
     * @param endTime              结束时间
     * @param countPeriod          统计周期
     * @param nodeParamEntityMap   节点参数
     * @return
     */
    private Map<String, Object> addRecordPowFun(String expression, String startTime, String endTime, String countPeriod, Map<String, NodeParamEntity> nodeParamEntityMap) {
        Map<String, Object> hashMap = Maps.newHashMap();
        //返回日志数据
        List<NodeLogInfoDto> nodeLogInfoDtoList = Lists.newArrayList();
        //返回数据
        List<NodeHistoryDataDto> resultList = Lists.newArrayList();
        //获取出函数中查询参数
        String nodeParam = expression.substring(expression.indexOf("("), expression.indexOf(")")).replace(" ","");
        //先获取函数中设定的幂数系数，判断是否是整数，如果不是整数直接返回空，并结束这次计算
        String[] nodeParamStr = nodeParam.split(",");
        //系数
        String coefficient = nodeParamStr[1];
        if (!coefficient.matches("-?\\d+")) {
            hashMap.put("resultList", resultList);
            hashMap.put("nodeLogInfoDtoList", nodeLogInfoDtoList);
            return hashMap;
        }
        //节点参数
        String param = nodeParamStr[0];
        NodeParamEntity nodeParamEntity = nodeParamEntityMap.get(nodeParam);
        String[] paramArray = param.split("@");
        if ("gnd".equals(paramArray[0])) {
            //设备id
            String deviceId = paramArray[1];
            //功能点标识
            String functionLogo = paramArray[2];
            //如果索引号不为空，则说明要查询枪历史数据
            Integer indexNum = nodeParamEntity.getIndexNum();
            List<DeviceHistoryDto> deviceHistoryDtoList;
            if (StringUtil.isNotEmpty(indexNum)) {
                deviceHistoryDtoList = findDeviceHistoryIndexValueList(deviceId, functionLogo, getStartMill(startTime), getEndMill(endTime), countPeriod, indexNum, null);
            } else {
                deviceHistoryDtoList = findDeviceHistoryValueList(deviceId, functionLogo, getStartMill(startTime), getEndMill(endTime), countPeriod);
            }
            if (CollectionUtils.isNotEmpty(deviceHistoryDtoList)) {
                deviceHistoryDtoList.forEach(deviceHistoryDto -> {
                    Double resultValue = nodeParamEntity.getDefaultValue();
                    //节点历史数据
                    NodeHistoryDataDto nodeHistoryDataDto = new NodeHistoryDataDto();
                    Integer dataType = deviceHistoryDto.getDataType();
                    Object dataValue = deviceHistoryDto.getDataValue();
                    //日志信息
                    String logInfo;
                    //返回值是否是缺省值
                    Integer isDefaultValue = null;
                    //节点日志
                    NodeLogInfoDto nodeLogInfoDto = new NodeLogInfoDto();
                    nodeLogInfoDto.setLogTime(LocalDateTime.now());
                    nodeLogInfoDto.setTsTime(deviceHistoryDto.getDateTime());
                    Double aDouble = dataTypeConvert(dataType, dataValue);
                    //源数据为负数时不进行运算，返回空值
                    if (StringUtil.isNotEmpty(aDouble) && aDouble > 0.0) {
                        resultValue = Math.pow(judgeParam(aDouble, nodeParamEntity), Double.parseDouble(coefficient));
                        logInfo = "参数（" + expression + "）,数据正常";
                    } else {
                        isDefaultValue = 1;
                        logInfo = "参数（" + expression + "）,数据为null或未在参数设定范围之内，返回缺省值，原始数据为：" + aDouble;
                    }
                    nodeLogInfoDto.setIsDefaultValue(isDefaultValue);
                    nodeLogInfoDto.setLogInfo(logInfo);
                    nodeHistoryDataDto.setResultValue(resultValue);
                    nodeHistoryDataDto.setTs(deviceHistoryDto.getDateTime());
                    resultList.add(nodeHistoryDataDto);
                    nodeLogInfoDtoList.add(nodeLogInfoDto);
                });
            }
        } else if ("jd".equals(paramArray[0])){
            long storageId = Long.parseLong(paramArray[1]);
            //查询节点历史数据
            List<NodeHistoryDataDto> nodeTaosDataList = computeNodeMapper.findNodeTaosDataList(NODE_TABLE + storageId, getStartMill(startTime), getEndMill(endTime), countPeriod, null);
            if (CollectionUtils.isNotEmpty(nodeTaosDataList)) {
                nodeTaosDataList.forEach(nodeHistoryDataDto -> {
                    Double nodeValue = nodeHistoryDataDto.getResultValue();
                    Double resultValue = nodeParamEntity.getDefaultValue();
                    //日志信息
                    String logInfo;
                    //返回值是否是缺省值
                    Integer isDefaultValue = null;
                    //节点日志
                    NodeLogInfoDto nodeLogInfoDto = new NodeLogInfoDto();
                    nodeLogInfoDto.setLogTime(LocalDateTime.now());
                    nodeLogInfoDto.setTsTime(nodeHistoryDataDto.getTs());
                    //源数据为负数时不进行 运算，返回空值
                    if (StringUtil.isNotEmpty(nodeValue) && nodeValue > 0.0) {
                        resultValue = Math.pow(judgeParam(nodeValue, nodeParamEntity), Double.parseDouble(coefficient));
                        logInfo = "参数（" + expression + "）,数据正常";
                    } else {
                        isDefaultValue = 1;
                        logInfo = "参数（" + expression + "）,数据为null或未在参数设定范围之内，返回缺省值，原始数据为：" + nodeValue;
                    }
                    nodeLogInfoDto.setIsDefaultValue(isDefaultValue);
                    nodeLogInfoDto.setLogInfo(logInfo);
                    nodeHistoryDataDto.setResultValue(resultValue);
                    resultList.add(nodeHistoryDataDto);
                    nodeLogInfoDtoList.add(nodeLogInfoDto);
                });
            }
        } else if (isNumeric(expression)){
            //如果都不是上面两种，则认为是公式中填的静态数值，则根据开始结束时间和周期获取时间，返回数据
            getLocalDateTimeBetween(strToLocalDateTime(startTime), strToLocalDateTime(endTime), countPeriod)
                    .stream().map(DateUtil::localDateTimeToStr).collect(Collectors.toList()).forEach(dateTime -> {
                        NodeHistoryDataDto nodeHistoryDataDto = new NodeHistoryDataDto();
                        nodeHistoryDataDto.setTs(dateTime);
                        nodeHistoryDataDto.setResultValue(Double.valueOf(expression));
                        resultList.add(nodeHistoryDataDto);
                    });

        }
        hashMap.put("resultList", resultList);
        hashMap.put("nodeLogInfoDtoList", nodeLogInfoDtoList);
        return hashMap;
    }

    /**
     * 数据补录映射值函数
     * @param expression           函数参数
     * @param startTime            开始时间
     * @param endTime              结束时间
     * @param countPeriod          统计周期
     * @param nodeParamEntityMap   节点参数
     * @return
     */
    private Map<String, Object> addRecordMappedValueFun(String expression, String startTime, String endTime, String countPeriod, Map<String, NodeParamEntity> nodeParamEntityMap) {
        //返回日志数据
        List<NodeLogInfoDto> nodeLogInfoDtoList = Lists.newArrayList();
        //返回数据
        List<NodeHistoryDataDto> resultList = Lists.newArrayList();
        NodeParamEntity nodeParamEntity = nodeParamEntityMap.get(expression);
        String[] paramArray = expression.split("@");
        if ("gnd".equals(paramArray[0])) {
            //设备id
            String deviceId = paramArray[1];
            //功能点标识
            String functionLogo = paramArray[2];
            //如果索引号不为空，则说明要查询枪历史数据
            Integer indexNum = nodeParamEntity.getIndexNum();
            //查询历史数据
            List<DeviceHistoryDto> deviceHistoryDtoList;
            if (StringUtil.isNotEmpty(indexNum)) {
                deviceHistoryDtoList = findDeviceHistoryIndexValueList(deviceId, functionLogo, getStartMill(startTime), getEndMill(endTime), countPeriod, indexNum, null);
            } else {
                deviceHistoryDtoList = findDeviceHistoryValueList(deviceId, functionLogo, getStartMill(startTime), getEndMill(endTime), countPeriod);
            }
            if (CollectionUtils.isNotEmpty(deviceHistoryDtoList)) {
                deviceHistoryDtoList.forEach(deviceHistoryDto -> {
                    Double resultValue = nodeParamEntity.getDefaultValue();
                    //节点历史数据
                    NodeHistoryDataDto nodeHistoryDataDto = new NodeHistoryDataDto();
                    Integer dataType = deviceHistoryDto.getDataType();
                    Object dataValue = deviceHistoryDto.getDataValue();
                    //日志信息
                    String logInfo;
                    //返回值是否是缺省值
                    Integer isDefaultValue = null;
                    //节点日志
                    NodeLogInfoDto nodeLogInfoDto = new NodeLogInfoDto();
                    nodeLogInfoDto.setLogTime(LocalDateTime.now());
                    nodeLogInfoDto.setTsTime(deviceHistoryDto.getDateTime());
                    Double aDouble = dataTypeConvert(dataType, dataValue);
                    if (StringUtil.isNotEmpty(aDouble)) {
                        resultValue = judgeParam(aDouble, nodeParamEntity);
                        logInfo = "参数（" + expression + "）,数据正常";
                    } else {
                        isDefaultValue = 1;
                        logInfo = "参数（" + expression + "）,数据为null或未在参数设定范围之内，返回缺省值，原始数据为：" + aDouble;
                    }
                    nodeLogInfoDto.setIsDefaultValue(isDefaultValue);
                    nodeLogInfoDto.setLogInfo(logInfo);
                    nodeHistoryDataDto.setResultValue(resultValue);
                    nodeHistoryDataDto.setTs(deviceHistoryDto.getDateTime());
                    resultList.add(nodeHistoryDataDto);
                    nodeLogInfoDtoList.add(nodeLogInfoDto);
                });
            }
        } else if ("jd".equals(paramArray[0])){
            long storageId = Long.parseLong(paramArray[1]);
            //查询节点历史数据
            List<NodeHistoryDataDto> nodeTaosDataList = computeNodeMapper.findNodeTaosDataList(NODE_TABLE + storageId, getStartMill(startTime), getEndMill(endTime), countPeriod, null);
            if (CollectionUtils.isNotEmpty(nodeTaosDataList)) {
                nodeTaosDataList.forEach(nodeHistoryDataDto -> {
                    Double nodeValue = nodeHistoryDataDto.getResultValue();
                    Double resultValue = nodeParamEntity.getDefaultValue();
                    //日志信息
                    String logInfo;
                    //返回值是否是缺省值
                    Integer isDefaultValue = null;
                    //节点日志
                    NodeLogInfoDto nodeLogInfoDto = new NodeLogInfoDto();
                    nodeLogInfoDto.setLogTime(LocalDateTime.now());
                    nodeLogInfoDto.setTsTime(nodeHistoryDataDto.getTs());
                    if (StringUtil.isNotEmpty(nodeValue)) {
                        resultValue = judgeParam(nodeValue, nodeParamEntity);
                        logInfo = "参数（" + expression + "）,数据正常";
                    } else {
                        isDefaultValue = 1;
                        logInfo = "参数（" + expression + "）,数据为null或未在参数设定范围之内，返回缺省值，原始数据为：" + nodeValue;
                    }
                    nodeLogInfoDto.setIsDefaultValue(isDefaultValue);
                    nodeLogInfoDto.setLogInfo(logInfo);
                    nodeHistoryDataDto.setResultValue(resultValue);
                    resultList.add(nodeHistoryDataDto);
                    nodeLogInfoDtoList.add(nodeLogInfoDto);
                });
            }
        } else if (isNumeric(expression)){
            //如果都不是上面两种，则认为是公式中填的静态数值，则根据开始结束时间和周期获取时间，返回数据
            getLocalDateTimeBetween(strToLocalDateTime(startTime), strToLocalDateTime(endTime), countPeriod)
                    .stream().map(DateUtil::localDateTimeToStr).collect(Collectors.toList()).forEach(dateTime -> {
                        NodeHistoryDataDto nodeHistoryDataDto = new NodeHistoryDataDto();
                        nodeHistoryDataDto.setTs(dateTime);
                        nodeHistoryDataDto.setResultValue(Double.valueOf(expression));
                        resultList.add(nodeHistoryDataDto);
                    });

        }
        Map<String, Object> hashMap = Maps.newHashMap();
        hashMap.put("resultList", resultList);
        hashMap.put("nodeLogInfoDtoList", nodeLogInfoDtoList);
        return hashMap;
    }

    /**
     * 判断节点参数
     * @param aDouble
     * @param nodeParamEntity
     * @return
     */
    private Double judgeParam(Double aDouble, NodeParamEntity nodeParamEntity) {
        Double defaultValue;
        if (StringUtil.isNotEmpty(nodeParamEntity.getMaxValue()) && StringUtil.isNotEmpty(nodeParamEntity.getMinValue())
                && aDouble <= nodeParamEntity.getMaxValue() && aDouble >= nodeParamEntity.getMinValue()) {
            defaultValue = aDouble;
        } else {
            defaultValue = aDouble;
        }
        return defaultValue;
    }

    /**
     * 数据补录计算值函数
     * @param expression           函数参数
     * @param startTime            开始时间
     * @param endTime              结束时间
     * @param countPeriod          统计周期
     * @param nodeParamEntityMap   节点参数
     * @return
     */
    private Map<String, Object> addRecordCalculateFun(String expression, String startTime, String endTime, String countPeriod, Map<String, NodeParamEntity> nodeParamEntityMap) {
        //返回日志数据
        List<NodeLogInfoDto> nodeLogInfoDtoList = Lists.newArrayList();
        //返回数据
        List<NodeHistoryDataDto> resultList = Lists.newArrayList();
        //获取出函数中查询参数
        String nodeParam = expression.substring(expression.indexOf("(") + 1, expression.indexOf(")")).replace(" ","");
        //如果是求幂数函数，参数单独处理
        if (expression.contains(FunctionqConstant.POW)) {
            //先获取函数中设定的幂数系数，判断是否是整数，如果不是整数直接返回空，并结束这次计算
            String[] nodeParamStr = nodeParam.split(",");
            //系数
            String coefficient = nodeParamStr[1];
            if (!coefficient.matches("-?\\d+")) {
                return null;
            }
            nodeParam = nodeParamStr[0];
        }
        NodeParamEntity nodeParamEntity = nodeParamEntityMap.get(nodeParam);
        String[] paramArray = nodeParam.split("@");
        if ("gnd".equals(paramArray[0])) {
            //设备id
            String deviceId = paramArray[1];
            //功能点标识
            String functionLogo = paramArray[2];
            //如果索引号不为空，则说明要查询枪历史数据
            Integer indexNum = nodeParamEntity.getIndexNum();
            //查询历史数据
            List<DeviceHistoryDto> deviceHistoryDtoList;
            if (StringUtil.isNotEmpty(indexNum)) {
                deviceHistoryDtoList = findDeviceHistoryIndexValueList(deviceId, functionLogo, getStartMill(startTime), getEndMill(endTime), countPeriod, indexNum, null);
            } else {
                deviceHistoryDtoList = findDeviceHistoryValueList(deviceId, functionLogo, getStartMill(startTime), getEndMill(endTime), countPeriod);
            }
            if (CollectionUtils.isNotEmpty(deviceHistoryDtoList)) {
                deviceHistoryDtoList.forEach(deviceHistoryDto -> {
                    //日志信息
                    String logInfo;
                    //返回值是否是缺省值
                    Integer isDefaultValue = null;
                    //节点日志
                    NodeLogInfoDto nodeLogInfoDto = new NodeLogInfoDto();
                    nodeLogInfoDto.setLogTime(LocalDateTime.now());
                    nodeLogInfoDto.setTsTime(deviceHistoryDto.getDateTime());
                    //节点历史数据
                    NodeHistoryDataDto nodeHistoryDataDto = new NodeHistoryDataDto();
                    nodeHistoryDataDto.setTs(deviceHistoryDto.getDateTime());
                    Integer dataType = deviceHistoryDto.getDataType();
                    Object dataValue = deviceHistoryDto.getDataValue();
                    Double sourceValue = dataTypeConvert(dataType, dataValue);
                    Double resultValue = nodeParamEntity.getDefaultValue();
                    if (StringUtil.isNotEmpty(sourceValue)) {
                        sourceValue = judgeParam(sourceValue, nodeParamEntity);
                        if (expression.contains(FunctionqConstant.SQUARE)) {//求平方
                            resultValue = Math.pow(sourceValue, 2);
                        } else if (expression.contains(FunctionqConstant.REVERSE)) {//相反值
                            resultValue = (-sourceValue);
                        } else if (expression.contains(FunctionqConstant.INTPART)) {//取整
                            resultValue = Math.floor(sourceValue);
                        } else if (expression.contains(FunctionqConstant.ABS)) {//绝对值
                            resultValue = Math.abs(sourceValue);
                        }
                        logInfo = "参数（" + expression + "）,数据正常";
                    } else {
                        isDefaultValue = 1;
                        logInfo = "参数（" + expression + "）,数据为null或未在参数设定范围之内，返回缺省值，原始数据为：" + sourceValue;
                    }
                    nodeLogInfoDto.setIsDefaultValue(isDefaultValue);
                    nodeLogInfoDto.setLogInfo(logInfo);
                    nodeHistoryDataDto.setResultValue(resultValue);
                    resultList.add(nodeHistoryDataDto);
                    nodeLogInfoDtoList.add(nodeLogInfoDto);
                });
            }
        } else if ("jd".equals(paramArray[0])){
            long storageId = Long.parseLong(paramArray[1]);
            //查询节点历史数据
            List<NodeHistoryDataDto> nodeTaosDataList = computeNodeMapper.findNodeTaosDataList(NODE_TABLE + storageId, getStartMill(startTime), getEndMill(endTime), countPeriod, null);
            if (CollectionUtils.isNotEmpty(nodeTaosDataList)) {
                nodeTaosDataList.forEach(nodeHistoryDataDto -> {
                    //日志信息
                    String logInfo;
                    //返回值是否是缺省值
                    Integer isDefaultValue = null;
                    //节点日志
                    NodeLogInfoDto nodeLogInfoDto = new NodeLogInfoDto();
                    nodeLogInfoDto.setLogTime(LocalDateTime.now());
                    nodeLogInfoDto.setTsTime(nodeHistoryDataDto.getTs());

                    Double sourceValue = nodeHistoryDataDto.getResultValue();
                    Double resultValue = nodeParamEntity.getDefaultValue();
                    if (StringUtil.isNotEmpty(sourceValue)) {
                        sourceValue = judgeParam(sourceValue, nodeParamEntity);
                        if (expression.contains(FunctionqConstant.SQUARE)) {//求平方
                            resultValue = Math.pow(sourceValue, 2);
                        } else if (expression.contains(FunctionqConstant.REVERSE)) {//相反值
                            resultValue = (-sourceValue);
                        } else if (expression.contains(FunctionqConstant.INTPART)) {//取整
                            resultValue = Math.floor(sourceValue);
                        } else if (expression.contains(FunctionqConstant.ABS)) {//绝对值
                            resultValue = Math.abs(sourceValue);
                        }
                        logInfo = "参数（" + expression + "）,数据正常";
                    } else {
                        isDefaultValue = 1;
                        logInfo = "参数（" + expression + "）,数据为null或未在参数设定范围之内，返回缺省值，原始数据为：" + sourceValue;
                    }
                    nodeLogInfoDto.setIsDefaultValue(isDefaultValue);
                    nodeLogInfoDto.setLogInfo(logInfo);
                    nodeHistoryDataDto.setResultValue(resultValue);
                    nodeHistoryDataDto.setTs(nodeHistoryDataDto.getTs());
                    resultList.add(nodeHistoryDataDto);
                    nodeLogInfoDtoList.add(nodeLogInfoDto);
                });
            }
        } else if (isNumeric(expression)){
            //如果都不是上面两种，则认为是公式中填的静态数值，则根据开始结束时间和周期获取时间，返回数据
            getLocalDateTimeBetween(strToLocalDateTime(startTime), strToLocalDateTime(endTime), countPeriod)
                    .stream().map(DateUtil::localDateTimeToStr).collect(Collectors.toList()).forEach(dateTime -> {
                        NodeHistoryDataDto nodeHistoryDataDto = new NodeHistoryDataDto();
                        nodeHistoryDataDto.setTs(dateTime);
                        nodeHistoryDataDto.setResultValue(Double.valueOf(expression));
                        resultList.add(nodeHistoryDataDto);
                    });

        }
        Map<String, Object> hashMap = Maps.newHashMap();
        hashMap.put("resultList", resultList);
        hashMap.put("nodeLogInfoDtoList", nodeLogInfoDtoList);
        return hashMap;
    }

    /**
     * 查询设备功能点历史数据
     */
    private List<DeviceHistoryDto> findDeviceHistoryValueList(String deviceId, String functionLogo, String startTime, String endTime, String countPeriod) {
        //返回数据
        List<DeviceHistoryDto> realDataModelList = Lists.newArrayList();
        DeviceHistoryQueryVo deviceHistoryQueryVo = new DeviceHistoryQueryVo();
        deviceHistoryQueryVo.setDeviceIds(Collections.singleton(deviceId));
        deviceHistoryQueryVo.setFunctionLogos(Collections.singleton(functionLogo));
        deviceHistoryQueryVo.setStartTime(startTime);
        deviceHistoryQueryVo.setEndTime(endTime);
        deviceHistoryQueryVo.setTimeInterval(countPeriod);
        ResponseResult<Map<String, Map<String, List<DeviceHistoryDto>>>> deviceHistoryValueList =
                dataService.findDeviceHistoryValueList(deviceHistoryQueryVo);
        if (deviceHistoryValueList.isSuccess() && !deviceHistoryValueList.getData().isEmpty()) {
            Map<String, List<DeviceHistoryDto>> functionDataMap = deviceHistoryValueList.getData().get(deviceId);
            if (functionDataMap != null && !functionDataMap.isEmpty()) {
                realDataModelList = functionDataMap.get(functionLogo);
            }
        }
        return realDataModelList;
    }

    /**
     * 查询电枪历史数据
     */
    private List<DeviceHistoryDto> findDeviceHistoryIndexValueList(String deviceId, String functionLogo, String startTime, String endTime, String countPeriod, Integer indexNum, Integer isNear) {
        //返回数据
        List<DeviceHistoryDto> realDataModelList = Lists.newArrayList();
        DeviceIndexQueryVo deviceQueryVo = new DeviceIndexQueryVo();
        Map<String, Set<String>> deviceFuctionMap = Maps.newHashMap();
        String functionIndex = functionLogo + "index" + indexNum;
        deviceFuctionMap.put(deviceId, Collections.singleton(functionIndex));
        deviceQueryVo.setDeviceFuctionMap(deviceFuctionMap);
        deviceQueryVo.setStartTime(startTime);
        deviceQueryVo.setEndTime(endTime);
        deviceQueryVo.setTimeInterval(countPeriod);
        ResponseResult<Map<String, Map<String, List<DeviceHistoryDto>>>> deviceHistoryValueList;
        if (StringUtil.isNotEmpty(isNear)) {
            deviceHistoryValueList = dataService.findDeviceDiffIndexValueListFeign(deviceQueryVo);
        } else {
            deviceHistoryValueList = dataService.findDeviceHistoryIndexValueList(deviceQueryVo);
        }
        if (deviceHistoryValueList.isSuccess() && !deviceHistoryValueList.getData().isEmpty()) {
            Map<String, List<DeviceHistoryDto>> functionDataMap = deviceHistoryValueList.getData().get(deviceId);
            if (functionDataMap != null && !functionDataMap.isEmpty()) {
                realDataModelList = functionDataMap.get(functionLogo);
            }
        }
        return realDataModelList;
    }

    /**
     * 查询设备功能点指定单个时间或时间段内的数据
     */
    private List<DeviceHistoryDto> findDeviceDifferenceListFeign(String deviceId, String functionLogo, String startTime, String endTime, String countPeriod, Integer isNear) {
        //返回数据
        List<DeviceHistoryDto> realDataModelList = Lists.newArrayList();
        DeviceHistoryQueryVo deviceHistoryQueryVo = new DeviceHistoryQueryVo();
        deviceHistoryQueryVo.setDeviceIds(Collections.singleton(deviceId));
        deviceHistoryQueryVo.setFunctionLogos(Collections.singleton(functionLogo));
        deviceHistoryQueryVo.setStartTime(startTime);
        deviceHistoryQueryVo.setEndTime(endTime);
        deviceHistoryQueryVo.setTimeInterval(countPeriod);
        ResponseResult<Map<String, Map<String, List<DeviceHistoryDto>>>> deviceHistoryValueList = dataService.findDeviceDifferenceListFeign(deviceHistoryQueryVo, isNear);
        if (deviceHistoryValueList.isSuccess() && !deviceHistoryValueList.getData().isEmpty()) {
            Map<String, List<DeviceHistoryDto>> functionDataMap = deviceHistoryValueList.getData().get(deviceId);
            if (functionDataMap != null && !functionDataMap.isEmpty()) {
                realDataModelList = functionDataMap.get(functionLogo);
            }
        }
        return realDataModelList;
    }

    /**
     * 查询设备功能点指定时间段内的last和first历史数据
     */
    private List<NodeDifHistoryDto> findNodeDifHistoryList(String deviceId, String functionLogo, String startTime, String endTime, String countPeriod) {
        //返回数据
        List<NodeDifHistoryDto> realDataModelList = Lists.newArrayList();
        DeviceHistoryQueryVo deviceHistoryQueryVo = new DeviceHistoryQueryVo();
        deviceHistoryQueryVo.setDeviceIds(Collections.singleton(deviceId));
        deviceHistoryQueryVo.setFunctionLogos(Collections.singleton(functionLogo));
        deviceHistoryQueryVo.setStartTime(startTime);
        deviceHistoryQueryVo.setEndTime(endTime);
        deviceHistoryQueryVo.setTimeInterval(countPeriod);
        ResponseResult<Map<String, Map<String, List<NodeDifHistoryDto>>>> deviceHistoryValueList = dataService.findNodeDifHistoryListFeign(deviceHistoryQueryVo);
        if (deviceHistoryValueList.isSuccess() && !deviceHistoryValueList.getData().isEmpty()) {
            Map<String, List<NodeDifHistoryDto>> functionDataMap = deviceHistoryValueList.getData().get(deviceId);
            if (functionDataMap != null && !functionDataMap.isEmpty()) {
                realDataModelList = functionDataMap.get(functionLogo);
            }
        }
        return realDataModelList;
    }

    /**
     * 查询索引指定时间段内的last和first历史数据
     */
    private List<NodeDifHistoryDto> findNodeDifHistoryIndexListFeign(String deviceId, String functionLogo, String startTime, String endTime, String countPeriod, Integer indexNum) {
        //返回数据
        List<NodeDifHistoryDto> realDataModelList = Lists.newArrayList();
        DeviceIndexQueryVo deviceQueryVo = new DeviceIndexQueryVo();
        Map<String, Set<String>> deviceFuctionMap = Maps.newHashMap();
        String functionIndex = functionLogo + "index" + indexNum;
        deviceFuctionMap.put(deviceId, Collections.singleton(functionIndex));
        deviceQueryVo.setDeviceFuctionMap(deviceFuctionMap);
        deviceQueryVo.setStartTime(startTime);
        deviceQueryVo.setEndTime(endTime);
        deviceQueryVo.setTimeInterval(countPeriod);
        ResponseResult<Map<String, Map<String, List<NodeDifHistoryDto>>>> deviceHistoryValueList = dataService.findNodeDifHistoryIndexListFeign(deviceQueryVo);
        if (deviceHistoryValueList.isSuccess() && !deviceHistoryValueList.getData().isEmpty()) {
            Map<String, List<NodeDifHistoryDto>> functionDataMap = deviceHistoryValueList.getData().get(deviceId);
            if (functionDataMap != null && !functionDataMap.isEmpty()) {
                realDataModelList = functionDataMap.get(functionLogo);
            }
        }
        return realDataModelList;
    }

    /**
     * 获取差值下一个结束时间(因为差值不取临近值，使用前一位减前一位计算，所以要多查询一个周期)
     * @param period
     * @param dateTime
     * @return
     */
    public static String getNextEndTime(String period, String dateTime) {

        String storageTime = null;
        long timeNum = Long.parseLong(period.substring(0, period.length() - 1));

        //分
        if (period.contains(StaticParamVo.M)) {
            storageTime = DateUtil.localDateTimeToStr(DateUtil.strToLocalDateTime(dateTime).plusMinutes(timeNum).withSecond(59));
            //时
        } else if (period.contains(StaticParamVo.H)) {
            storageTime = DateUtil.localDateTimeToStr(DateUtil.strToLocalDateTime(dateTime).plusHours(timeNum).withMinute(59).withSecond(59));
            //日
        } else if (period.contains(StaticParamVo.D)){
            storageTime = DateUtil.localDateTimeToStr(DateUtil.strToLocalDateTime(dateTime).plusDays(timeNum).withHour(23).withMinute(59).withSecond(59));
            //月
        } else if (period.contains(StaticParamVo.N)) {
            LocalDate localDate = strToLocalDate(getDateByType(dateTime, (int) timeNum, 5, 1).substring(0,10));
            storageTime = getDayEnd(localDateToStr(localDate.withDayOfMonth(localDate.getMonth().length(localDate.isLeapYear()))));
            //年
        } else if (period.contains(StaticParamVo.Y)) {
            LocalDate localDate = strToLocalDate(getDateByType(dateTime, (int) timeNum, 6, 1).substring(0,10));
            storageTime = getDayEnd(localDateToStr(LocalDate.ofYearDay(localDate.getYear(), 365)));
        }
        return storageTime;
    }

}
