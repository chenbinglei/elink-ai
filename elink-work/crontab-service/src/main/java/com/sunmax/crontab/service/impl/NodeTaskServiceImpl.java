package com.sunmax.crontab.service.impl;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.sunmax.common.constant.FunctionqConstant;
import com.sunmax.common.dto.crontab.LocalCacheDto;
import com.sunmax.common.dto.crontab.NodeHistoryDataDto;
import com.sunmax.common.dto.data.DeviceHistoryDto;
import com.sunmax.common.model.RealDataModel;
import com.sunmax.common.util.DateUtil;
import com.sunmax.common.util.ExcelFormulaUtil;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.util.StringUtil;
import com.sunmax.common.util.oss.FileUtil;
import com.sunmax.common.vo.StaticParamVo;
import com.sunmax.common.vo.crontab.NodeHistoryDataVo;
import com.sunmax.common.vo.data.DeviceHistoryQueryVo;
import com.sunmax.common.vo.data.DeviceIndexQueryVo;
import com.sunmax.crontab.config.cache.LocalCache;
import com.sunmax.crontab.dao.ComputeNodeDao;
import com.sunmax.crontab.dao.NodeLogInfoDao;
import com.sunmax.crontab.dto.NodeParamInfoDto;
import com.sunmax.crontab.entity.NodeLogInfoEntity;
import com.sunmax.crontab.mapper.tdengine.ComputeNodeMapper;
import com.sunmax.crontab.service.NodeTaskService;
import com.sunmax.crontab.service.feign.DataService;
import com.sunmax.crontab.service.feign.DeviceService;
import com.sunmax.crontab.util.ScheduleTaskUtil;
import com.sunmax.crontab.vo.ComputeNodeTaskVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static com.sunmax.common.util.ConversionExpressionUtil.getInstanceExpressionParamList;
import static com.sunmax.common.util.DateUtil.*;
import static com.sunmax.common.util.DeviceUtil.dataTypeConvert;
import static com.sunmax.common.util.StringUtil.isNumericAll;
import static com.sunmax.crontab.util.ScheduleTaskUtil.*;

/**
 * 计算节点定时执行业务类
 *
 * @author yqz
 */
@Slf4j
@Service
public class NodeTaskServiceImpl implements NodeTaskService {

    @Autowired
    private ComputeNodeMapper computeNodeMapper;

    @Autowired
    private ScheduleTaskUtil scheduleTask;

    @Autowired
    private DataService dataService;

    @Autowired
    private DeviceService deviceService;

    @Autowired
    private LocalCache localCache;

    @Autowired
    private NodeLogInfoDao nodeLogInfoDao;

    @Autowired
    private ComputeNodeDao computeNodeDao;

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

    /**
     * 添加所有节点至定时任务中
     *
     * @param computeNodeTaskVoList
     */
    @Override
    public void addAllComputeNodeTask(List<ComputeNodeTaskVo> computeNodeTaskVoList) {
        //执行该节点任务 并添加到队列中
        computeNodeTaskVoList.forEach(taskVo -> {
            scheduleTask.addTask(this.computeNodeRunnable(taskVo), taskVo.getCronExpression(), NODE_TASK + taskVo.getStorageId());
        });
    }

    /**
     * 删除该节点定时任务以及节点表
     *
     * @param storageId
     */
    @Override
    @Transactional(transactionManager = "tdengineTransactionManager", rollbackFor = Exception.class)
    public ResponseResult<Boolean> deleteComputeNodeTaskAndTaosData(Long storageId) {
        //删除taos表
        computeNodeMapper.dropTable(NODE_TABLE + storageId);
        //删除任务
        return ResponseResult.ok(scheduleTask.removeTask(NODE_TASK + storageId));
    }

    /**
     * 批量修改模型节点定时任务
     *
     * @param computeNodeTaskVoList
     */
    @Override
    public void updateComputeNodeListTask(List<ComputeNodeTaskVo> computeNodeTaskVoList) {
        computeNodeTaskVoList.forEach(taskVo -> {
            scheduleTask.updateTask(this.computeNodeRunnable(taskVo), taskVo.getCronExpression(), NODE_TASK + taskVo.getStorageId());
        });
    }

    //封装实例节点定时任务
    private Runnable computeNodeRunnable(ComputeNodeTaskVo taskVo) {
        return () -> {
            try {
                //获取当前时间和周期执行起始时间比较
                LocalDateTime currentTime = LocalDateTime.now();
                if (currentTime.isAfter(strToLocalDateTime(taskVo.getStartTime()))) {
                    //记录节点日志
                    NodeLogInfoEntity nodeLogInfoEntity = new NodeLogInfoEntity();
                    nodeLogInfoEntity.setLogType(1);
                    nodeLogInfoEntity.setNodeId(taskVo.getId());
                    nodeLogInfoEntity.setStorageId(taskVo.getStorageId());
                    LocalDateTime localDateTime = LocalDateTime.now();
                    nodeLogInfoEntity.setLogTime(localDateTime);
                    //节点计算数据值
                    Double taosNodeValue = null;
                    //标记当前节点公式是否支持计算，如果其中函数有一个值为空，则本次节点不计算，存储数据为空
                    AtomicBoolean flag = new AtomicBoolean(true);
                    //获取计算公式，过滤出每个函数参数
                    String formulaAfter = taskVo.getFormulaAfter();
                    //统计周期(用户统计数据范围查询时间)
                    String countPeriod = taskVo.getCountPeriod();
                    //统一查询时间
                    String endTime = localDateTimeToStr(LocalDateTime.now().withSecond(0).minusSeconds(1));
                    //存储taos节点表时间
                    String startTime = getNearTime(countPeriod, endTime);
                    //计算公式套入参数值
                    AtomicReference<String> expressionValue = new AtomicReference<>(FileUtil.separator);
                    //日志内容
                    AtomicReference<String> logInfoValue = new AtomicReference<>(FileUtil.separator);
                    //标记当前日志是否需要存储
                    AtomicBoolean logIsStorage = new AtomicBoolean(false);
                    //记录有几个参数返回缺省值
                    List<Integer> isDefaultValueList = Lists.newArrayList();
                    List<String> expressionParamList = getInstanceExpressionParamList(formulaAfter);
                    if (CollectionUtils.isNotEmpty(expressionParamList)) {
                        //节点参数列表
                        List<NodeParamInfoDto> nodeParamInfoList = taskVo.getNodeParamInfoList();
                        //循环函数，获取需要的值
                        expressionParamList.forEach(expression -> {
                            String logInfoValueStr = logInfoValue.get();
                            String logValue;
                            String expressionStr = expressionValue.get();
                            String replace;
                            //当前标识不为数字才去计算
                            Map<String, Object> resultMap = Maps.newHashMap();
                            if (!isNumericAll(expression)) {
                                //先判断是否带括号，如果带则是函数，如果不带则是映射值
                                if (expression.contains("(")) {
                                    String function = expression.substring(0, expression.indexOf("("));
                                    switch (function) {
                                        case FunctionqConstant.SUM:
                                        case FunctionqConstant.MAX:
                                        case FunctionqConstant.MIN:
                                        case FunctionqConstant.AVG: //求和、最大值、最小值、平均值
                                            resultMap = nodeCountFun(expression, startTime, endTime, countPeriod, getCountType(expression), nodeParamInfoList);
                                            break;
                                        case FunctionqConstant.DIF: //差值
                                            resultMap = nodeDifFun(expression, startTime, nodeParamInfoList);
                                            break;
                                        case FunctionqConstant.DIFF: //临近值差值
                                            resultMap = nodeDiffFun(expression, startTime, nodeParamInfoList, countPeriod, endTime);
                                            break;
                                        case FunctionqConstant.SQRT: //平方根
                                            resultMap = nodeSqrtFun(expression, nodeParamInfoList);
                                            expression = (String) resultMap.get("nodeParam");
                                            break;
                                        case FunctionqConstant.SQUARE:
                                        case FunctionqConstant.REVERSE:
                                        case FunctionqConstant.INTPART:
                                        case FunctionqConstant.ABS: //平方、相反值、取整、绝对值
                                            resultMap = nodeCalculateFun(expression, nodeParamInfoList);
                                            break;
                                        case FunctionqConstant.POW: //求X的Y次幂
                                            resultMap = nodePowFun(expression, nodeParamInfoList);
                                            break;
                                    }
                                } else {//没有函数就代表为映射值
                                    resultMap = nodeMappedValueFun(expression, startTime, nodeParamInfoList);
                                }
                            } else {
                                resultMap.put("resultValue", expression);
                            }
                            Object resultValue = resultMap.get("resultValue");
                            if (StringUtil.isEmpty(resultValue)) {
                                flag.set(false);
                                return;
                            }
                            Object isDefaultValue = resultMap.get("isDefaultValue");
                            if (StringUtil.isNotEmpty(isDefaultValue)) {
                                isDefaultValueList.add(Integer.parseInt(String.valueOf(isDefaultValue)));
                            }
                            //处理日志信息
                            Object logInfoObj = resultMap.get("logInfo");
                            if (StringUtil.isNotEmpty(logInfoObj)) {
                                logIsStorage.set(true);
                                String logInfo = String.valueOf(logInfoObj);
                                if (StringUtil.isEmpty(logInfoValueStr)) {
                                    logValue = logInfo;
                                } else {
                                    logValue = logInfoValueStr + "," + logInfo;
                                }
                                logInfoValue.set(logValue);
                            }
                            //处理公式
                            if (StringUtil.isEmpty(expressionStr)) {
                                replace = formulaAfter.replace(expression, String.valueOf(resultValue));
                            } else {
                                replace = expressionStr.replace(expression, String.valueOf(resultValue));
                            }
                            expressionValue.set(replace);
                        });
                    }
                    //判断是否需要计算
                    if (flag.get()) {
                        //通过Excel计算公式值
                        taosNodeValue = ExcelFormulaUtil.calculateFormula(expressionValue.get());
                    }
                    //处理日志
                    if (logIsStorage.get()) {
                        if (CollectionUtils.isNotEmpty(isDefaultValueList) && StringUtil.isEmpty(taosNodeValue)) {
                            nodeLogInfoEntity.setLogLevel(1);
                        } else if (CollectionUtils.isNotEmpty(isDefaultValueList) && StringUtil.isNotEmpty(taosNodeValue)) {
                            nodeLogInfoEntity.setLogLevel(2);
                        } else if (StringUtil.isEmpty(taosNodeValue)) {
                            nodeLogInfoEntity.setLogLevel(1);
                        }
                        nodeLogInfoEntity.setTsTime(startTime);
                        nodeLogInfoEntity.setLogInfo(logInfoValue.get());
                        nodeLogInfoDao.save(nodeLogInfoEntity);
                    }
                    //存储数据至taos数据库
                    Integer strategyType = taskVo.getStrategyType();
                    //存入缓存对象
                    LocalCacheDto localCacheDto;

                    //计算本次存储数据库时间
                    String storageTime = getNextStorageTime(countPeriod, taskVo.getStartTime());
                    //如果能从缓存中获取到数据，说明在正常执行，则不是新建第一次执行
                    localCacheDto = localCache.getValue(String.valueOf(taskVo.getId()));
                    if (StringUtil.isNotEmpty(localCacheDto)) {
                        //获取缓存中存储的下次存储时间
                        String nextStorageTime = localCacheDto.getNextStorageTime();
                        if (localDateTimeToStr(currentTime.withSecond(0)).compareTo(nextStorageTime) == 0) {
                            String nearTime = getNearTime(countPeriod, localDateTimeToStr(LocalDateTime.now().withSecond(0).minusSeconds(1)));
                            //添加数据至taos
                            if (strategyType == 1) {//每次存储
                                computeNodeMapper.insertTableData(NODE_TABLE + taskVo.getStorageId(), STABLE_NAME, taskVo.getStorageId(), taosNodeValue, taskVo.getSiteId(), nearTime);
                            } else if (strategyType == 2) {//变化存储
                                //根据表名称查询当前节点最新一条数据
                                NodeHistoryDataDto nodeHistoryDataDto = computeNodeMapper.findNodeLastDataByTableName(NODE_TABLE + taskVo.getStorageId());
                                if (StringUtil.isNotEmpty(nodeHistoryDataDto)) {
                                    Double resultValueTaos = nodeHistoryDataDto.getResultValue();
                                    if ((taosNodeValue == null && resultValueTaos != null) ||
                                            (resultValueTaos == null && taosNodeValue != null) ||
                                            (taosNodeValue != null && resultValueTaos != null && !taosNodeValue.equals(resultValueTaos))) {
                                        computeNodeMapper.insertTableData(NODE_TABLE + taskVo.getStorageId(), STABLE_NAME, taskVo.getStorageId(), taosNodeValue, taskVo.getSiteId(), nearTime);
                                    }
                                } else {
                                    computeNodeMapper.insertTableData(NODE_TABLE + taskVo.getStorageId(), STABLE_NAME, taskVo.getStorageId(), taosNodeValue, taskVo.getSiteId(), nearTime);
                                }
                            }
                        }
                    } else {
                        localCacheDto = new LocalCacheDto();
                    }
                    localCacheDto.setNextStorageTime(storageTime);
                    localCacheDto.setResultValue(taosNodeValue);
                    localCacheDto.setCacheTime(localDateTimeToStr(LocalDateTime.now()));

                    LocalCacheDto localCacheValue = localCache.getValue(taskVo.getId());
                    if (StringUtil.isNotEmpty(localCacheValue)) {
                        //把原来的缓存删了
                        localCache.removeCache(taskVo.getId());
                    }
                    //把数据存入缓存
                    boolean putValue = localCache.putValue(taskVo.getId(), localCacheDto, -1);
                    if (!putValue) {
                        System.out.println("添加缓存失败，key为：" + taskVo.getStorageId());
                    }
                }
            } catch (Exception e) {
                log.error("节点定时任务执行异常", e);
            }
        };
    }

    /**
     * 计算下次存储时间
     *
     * @param period
     * @param dateTime
     * @return
     */
    public static String getNextStorageTime(String period, String dateTime) {

        //把传过来的时间秒数全部置零
        dateTime = dateTime.substring(0, 16) + ":00";

        String storageTime = null;
        String currentTime = DateUtil.localDateTimeToStr(LocalDateTime.now().withSecond(0).withNano(0));
        long timeNum = Long.parseLong(period.substring(0, period.length() - 1));

        //分
        if (period.contains(StaticParamVo.M)) {
            //计算两个时间间隔了多少分钟
            long dateSubMinutes = (long) (getDateSubMinutes(dateTime, currentTime) <= 0.0 ? 1 : getDateSubMinutes(dateTime, currentTime)) + timeNum;
            storageTime = DateUtil.localDateTimeToStr(DateUtil.strToLocalDateTime(dateTime).plusMinutes(dateSubMinutes));
            if (Objects.equals(storageTime, currentTime)) {
                storageTime = DateUtil.localDateTimeToStr(DateUtil.strToLocalDateTime(storageTime).plusMinutes(timeNum).withSecond(0));
            }
            //时
        } else if (period.contains(StaticParamVo.H)) {
            //计算两个时间间隔了多少小时
            long getDateSubHours = (long) (getDateSubHours(dateTime, currentTime) <= 0.0 ? 1 : getDateSubHours(dateTime, currentTime)) + timeNum;
            storageTime = DateUtil.localDateTimeToStr(DateUtil.strToLocalDateTime(dateTime).withMinute(0).plusHours(getDateSubHours));
            if (Objects.equals(storageTime, currentTime)) {
                storageTime = DateUtil.localDateTimeToStr(DateUtil.strToLocalDateTime(storageTime).plusHours(timeNum).withMinute(0).withSecond(0));
            }
            //日
        } else if (period.contains(StaticParamVo.D)) {
            //计算两个时间间隔了多少天
            long getDateSubDays = (long) (getDateSubDays(dateTime, currentTime) <= 0.0 ? 1 : getDateSubDays(dateTime, currentTime)) + timeNum;
            storageTime = DateUtil.localDateTimeToStr(DateUtil.strToLocalDateTime(dateTime).withHour(0).withMinute(0).withSecond(0).plusDays(getDateSubDays));
            if (Objects.equals(storageTime, currentTime)) {
                storageTime = DateUtil.localDateTimeToStr(DateUtil.strToLocalDateTime(storageTime).plusDays(timeNum).withHour(0).withMinute(0).withSecond(0));
            }
            //自然月
        } else if (period.contains(StaticParamVo.N)) {
            storageTime = getDayStart(getPerFirstDayOfMonth(currentTime.substring(0, 10)));
            //自然年
        } else if (period.contains(StaticParamVo.Y)) {
            storageTime = getStartOrEndDayOfYear(currentTime.substring(0, 10), 1, true);
        }
        return storageTime;
    }

    /**
     * 节点统计函数
     *
     * @param expression        函数参数
     * @param startTime         开始时间
     * @param endTime           结束时间
     * @param countPeriod       统计周期
     * @param nodeParamInfoList 节点参数列表
     */
    private Map<String, Object> nodeCountFun(String expression, String startTime, String endTime, String countPeriod, String countFun, List<NodeParamInfoDto> nodeParamInfoList) {
        Map<String, Object> hashMap = Maps.newHashMap();
        //日志信息
        String logInfo = null;
        //返回值是否是缺省值 1-是
        Integer isDefaultValue = null;
        //获取出函数中查询参数
        String nodeParam = expression.substring(expression.indexOf("(") + 1, expression.indexOf(")")).replace(" ", "");
        //根据参数标识转成map
        NodeParamInfoDto nodeParamInfoDto = nodeParamInfoList.stream().collect(Collectors.toMap(NodeParamInfoDto::getSourceCode, NodeParamInfoDto -> NodeParamInfoDto, (k1, k2) -> k1)).get(nodeParam);
        if (StringUtil.isEmpty(nodeParamInfoDto)) {
            log.error("节点统计函数参数错误，参数为：" + expression);
            hashMap.put("resultValue", null);
            hashMap.put("isDefaultValue", isDefaultValue);
            hashMap.put("logInfo", logInfo);
            return hashMap;
        }
        //返回数据
        Double resultValue = null;
        String[] paramArray = nodeParam.split("@");
        if ("gnd".equals(paramArray[0])) {
            //设备id
            String deviceId = paramArray[1];
            //功能点标识
            String functionLogo = paramArray[2];
            //如果索引号不为空，则说明要查询枪历史数据
            Integer indexNum = nodeParamInfoDto.getIndexNum();
            List<DeviceHistoryDto> deviceHistoryDtos;
            if (StringUtil.isNotEmpty(indexNum)) {
                deviceHistoryDtos = findDeviceHistoryIndexValueList(deviceId, functionLogo, startTime, endTime, null, indexNum, null);
            } else {
                deviceHistoryDtos = findDeviceHistoryValueList(deviceId, functionLogo, startTime, endTime, null);
            }
            //根据节点参数最大最小值过滤一遍数据
            deviceHistoryDtos = judgeFunPiontDataList(deviceHistoryDtos, nodeParamInfoDto);
            if (CollectionUtils.isNotEmpty(deviceHistoryDtos)) {
                switch (countFun) {
                    case FunctionqConstant.SUM: //求和
                        resultValue = deviceHistoryDtos.stream().mapToDouble(s -> Double.parseDouble(String.valueOf(s.getDataValue()))).filter(StringUtil::isNotEmpty).sum();
                        break;
                    case FunctionqConstant.MAX: //最大值
                        resultValue = (Double) Objects.requireNonNull(deviceHistoryDtos.stream().max(Comparator.comparing(s -> Double.parseDouble(String.valueOf(s.getDataValue())))).filter(StringUtil::isNotEmpty).orElse(null)).getDataValue();
                        break;
                    case FunctionqConstant.MIN: //最小值
                        resultValue = (Double) Objects.requireNonNull(deviceHistoryDtos.stream().min(Comparator.comparing(s -> Double.parseDouble(String.valueOf(s.getDataValue())))).filter(StringUtil::isNotEmpty).orElse(null)).getDataValue();
                        break;
                    case FunctionqConstant.AVG: //平均值
                        resultValue = deviceHistoryDtos.stream().collect(Collectors.averagingDouble(s -> Double.parseDouble(String.valueOf(s.getDataValue()))));
                        break;
                }
            } else {
                isDefaultValue = 1;
                logInfo = "参数（" + expression + "）,未查询到数据或判断最大最小值之后无符合的数据，返回空值：" + resultValue;
            }
        } else {
            long storageId = Long.parseLong(paramArray[1]);
            //查询节点统计数据
            List<NodeHistoryDataDto> nodeHistoryDataDtoList = judgeNodeDataList(computeNodeMapper.findNodeTaosDataList(NODE_TABLE + storageId, getStartMill(startTime), getEndMill(endTime), null, null), nodeParamInfoDto);
            if (CollectionUtils.isNotEmpty(nodeHistoryDataDtoList)) {
                switch (countFun) {
                    case FunctionqConstant.SUM: //求和
                        resultValue = nodeHistoryDataDtoList.stream().filter(s -> StringUtil.isNotEmpty(s.getResultValue())).mapToDouble(NodeHistoryDataDto::getResultValue).filter(StringUtil::isNotEmpty).sum();
                        break;
                    case FunctionqConstant.MAX: //最大值
                        resultValue = Objects.requireNonNull(nodeHistoryDataDtoList.stream().filter(s -> StringUtil.isNotEmpty(s.getResultValue())).max(Comparator.comparing(NodeHistoryDataDto::getResultValue)).filter(StringUtil::isNotEmpty).orElse(null)).getResultValue();
                        break;
                    case FunctionqConstant.MIN: //最小值
                        resultValue = Objects.requireNonNull(nodeHistoryDataDtoList.stream().filter(s -> StringUtil.isNotEmpty(s.getResultValue())).min(Comparator.comparing(NodeHistoryDataDto::getResultValue)).filter(StringUtil::isNotEmpty).orElse(null)).getResultValue();
                        break;
                    case FunctionqConstant.AVG: //平均值
                        resultValue = nodeHistoryDataDtoList.stream().filter(s -> StringUtil.isNotEmpty(s.getResultValue())).collect(Collectors.averagingDouble(NodeHistoryDataDto::getResultValue));
                        break;
                }
            } else {
                isDefaultValue = 1;
                logInfo = "参数（" + expression + "）,未查询到数据或判断最大最小值之后无符合的数据，返回空值：" + resultValue;
            }
        }
        hashMap.put("resultValue", resultValue);
        hashMap.put("isDefaultValue", isDefaultValue);
        hashMap.put("logInfo", logInfo);
        return hashMap;
    }

    /**
     * 判断功能点原始数据是否在参数设定范围内
     *
     * @param deviceHistoryDtos
     * @param nodeParamInfoDto
     * @return
     */
    private List<DeviceHistoryDto> judgeFunPiontDataList(List<DeviceHistoryDto> deviceHistoryDtos, NodeParamInfoDto nodeParamInfoDto) {
        if (CollectionUtils.isEmpty(deviceHistoryDtos)) {
            return Lists.newArrayList();
        }
        return deviceHistoryDtos.stream()
                .map(deviceHistoryDto -> {
                    DeviceHistoryDto deviceHistory = new DeviceHistoryDto();
                    BeanUtils.copyProperties(deviceHistoryDto, deviceHistory);
                    deviceHistory.setDataValue(
                            isValidDataValue(deviceHistoryDto.getDataValue(), nodeParamInfoDto) ?
                                    deviceHistoryDto.getDataValue() :
                                    nodeParamInfoDto.getDefaultValue()
                    );
                    return deviceHistory;
                })
                .collect(Collectors.toList());
    }

    /**
     * 判断计算节点数据是否在参数设定范围内
     *
     * @param nodeHistoryDataDtoList
     * @param nodeParamInfoDto
     * @return
     */
    private List<NodeHistoryDataDto> judgeNodeDataList(List<NodeHistoryDataDto> nodeHistoryDataDtoList, NodeParamInfoDto nodeParamInfoDto) {
        if (CollectionUtils.isEmpty(nodeHistoryDataDtoList)) {
            return Lists.newArrayList();
        }
        return nodeHistoryDataDtoList.stream()
                .map(nodeHistoryDataDto -> {
                    NodeHistoryDataDto nodeHistoryData = new NodeHistoryDataDto();
                    BeanUtils.copyProperties(nodeHistoryDataDto, nodeHistoryData);
                    nodeHistoryData.setResultValue(
                            isValidDataValue(nodeHistoryDataDto.getResultValue(), nodeParamInfoDto) ?
                                    nodeHistoryDataDto.getResultValue() :
                                    nodeParamInfoDto.getDefaultValue()
                    );
                    return nodeHistoryData;
                })
                .collect(Collectors.toList());
    }

    private boolean isValidDataValue(Object dataValue, NodeParamInfoDto nodeParamInfoDto) {
        return StringUtil.isNotEmpty(dataValue) &&
                StringUtil.isNotEmpty(nodeParamInfoDto.getMaxValue()) &&
                StringUtil.isNotEmpty(nodeParamInfoDto.getMinValue()) &&
                Double.parseDouble(String.valueOf(dataValue)) <= nodeParamInfoDto.getMaxValue() &&
                Double.parseDouble(String.valueOf(dataValue)) >= nodeParamInfoDto.getMinValue();
    }

    /**
     * 判断节点参数
     *
     * @param aDouble
     * @param nodeParamInfoDto
     * @return
     */
    private Double judgeParam(Object aDouble, NodeParamInfoDto nodeParamInfoDto) {
        Double result = null;
        if (StringUtil.isEmpty(aDouble) && StringUtil.isEmpty(nodeParamInfoDto.getDefaultValue())) {
            return null;
        } else if (StringUtil.isEmpty(aDouble)) {
            result = nodeParamInfoDto.getDefaultValue();
        } else {
            result = Double.parseDouble(String.valueOf(aDouble));
        }

        if (StringUtil.isNotEmpty(nodeParamInfoDto.getMaxValue()) && StringUtil.isNotEmpty(nodeParamInfoDto.getMinValue())) {
            double max = nodeParamInfoDto.getMaxValue();
            double min = nodeParamInfoDto.getMinValue();
            if (result <= max && result >= min) {
                return result;
            } else {
                return null;
            }
        }
        return result;
    }

    /**
     * 节点差值函数
     *
     * @param expression        函数参数
     * @param startTime         开始时间
     * @param nodeParamInfoList 节点参数列表
     */
    private Map<String, Object> nodeDifFun(String expression, String startTime, List<NodeParamInfoDto> nodeParamInfoList) {
        Map<String, Object> hashMap = Maps.newHashMap();
        //日志信息
        String logInfo = null;
        //返回值是否是缺省值 1-是
        Integer isDefaultValue = null;
        //获取出函数中查询参数
        String nodeParam = expression.substring(expression.indexOf("(") + 1, expression.indexOf(")")).replace(" ", "");
        //根据参数标识转成map
        NodeParamInfoDto nodeParamInfoDto = nodeParamInfoList.stream().collect(Collectors.toMap(NodeParamInfoDto::getSourceCode, NodeParamInfoDto -> NodeParamInfoDto, (k1, k2) -> k1)).get(nodeParam);
        if (StringUtil.isEmpty(nodeParamInfoDto)) {
            log.error("节点差值函数参数错误，参数为：" + expression);
            hashMap.put("resultValue", null);
            hashMap.put("isDefaultValue", isDefaultValue);
            hashMap.put("logInfo", logInfo);
            return hashMap;
        }
        //返回数据
        Double resultValue = null;
        String[] paramArray = nodeParam.split("@");
        if ("gnd".equals(paramArray[0])) {
            //设备id
            String deviceId = paramArray[1];
            //功能点标识
            String functionLogo = paramArray[2];
            //如果索引号不为空，则说明要查询枪历史数据
            Integer indexNum = nodeParamInfoDto.getIndexNum();
            //查询最新值
            Double aDouble = judgeParam(getDeviceFunctionsRealDataByIds(deviceId, functionLogo, indexNum), nodeParamInfoDto);
            if (StringUtil.isNotEmpty(aDouble)) {
                List<DeviceHistoryDto> deviceHistoryDtos;
                if (StringUtil.isNotEmpty(indexNum)) {
                    deviceHistoryDtos = findDeviceHistoryIndexValueList(deviceId, functionLogo, getStartMill(startTime), getEndMill(startTime), null, indexNum, null);
                } else {
                    //获取功能点历史数据
                    deviceHistoryDtos = findDeviceDifferenceListFeign(deviceId, functionLogo, getStartMill(startTime), getEndMill(startTime), null, null);
                }
                //如果第二个值没有数据，则返回当前值,所以默认结果为当前值
                resultValue = aDouble;
                if (CollectionUtils.isNotEmpty(deviceHistoryDtos) && StringUtil.isNotEmpty(judgeParam(deviceHistoryDtos.get(0).getDataValue(), nodeParamInfoDto))) {
                    Double dataValue = judgeParam(deviceHistoryDtos.get(0).getDataValue(), nodeParamInfoDto);
                    resultValue = aDouble - dataValue;
                } else {
                    isDefaultValue = 1;
                    logInfo = "参数（" + expression + "）,第二个值数据为null或未在参数设定范围之内，返回空值";
                }
            } else {
                isDefaultValue = 1;
                logInfo = "参数（" + expression + "）,第一个值数据为null或未在参数设定范围之内，返回空值";
            }
        } else {
            long storageId = Long.parseLong(paramArray[1]);
            //查询最新一条节点数据
            NodeHistoryDataDto nodeHistoryDataDto = computeNodeMapper.findNodeLastDataByTableName(NODE_TABLE + storageId);
            if (StringUtil.isNotEmpty(nodeHistoryDataDto) && StringUtil.isNotEmpty(judgeParam(nodeHistoryDataDto.getResultValue(), nodeParamInfoDto))) {
                Double lastNodeValue = judgeParam(nodeHistoryDataDto.getResultValue(), nodeParamInfoDto);
                //获取周期内的第一个值
                List<NodeHistoryDataDto> nodeHistoryDataDtoList = computeNodeMapper.findNodeDataByTime(NODE_TABLE + storageId, getStartMill(startTime), getEndMill(startTime));
                if (CollectionUtils.isNotEmpty(nodeHistoryDataDtoList) && StringUtil.isNotEmpty(judgeParam(nodeHistoryDataDtoList.get(0).getResultValue(), nodeParamInfoDto))) {
                    Double firstNodeValue = judgeParam(nodeHistoryDataDtoList.get(0).getResultValue(), nodeParamInfoDto);
                    resultValue = lastNodeValue - firstNodeValue;
                } else {
                    isDefaultValue = 1;
                    logInfo = "参数（" + expression + "）,第二个值数据为null或未在参数设定范围之内，返回空值";
                }
            } else {
                isDefaultValue = 1;
                logInfo = "参数（" + expression + "）,第一个值数据为null或未在参数设定范围之内，返回空值";
            }
        }
        hashMap.put("resultValue", resultValue);
        hashMap.put("isDefaultValue", isDefaultValue);
        hashMap.put("logInfo", logInfo);
        return hashMap;
    }

    /**
     * 节点临近值差值函数
     *
     * @param expression        函数参数
     * @param startTime         开始时间
     * @param nodeParamInfoList 节点参数列表
     * @param countPeriod
     */
    private Map<String, Object> nodeDiffFun(String expression, String startTime, List<NodeParamInfoDto> nodeParamInfoList, String countPeriod, String endTime) {
        Map<String, Object> hashMap = Maps.newHashMap();
        //日志信息
        String logInfo = null;
        //返回值是否是缺省值 1-是
        Integer isDefaultValue = null;
        //获取出函数中查询参数
        String nodeParam = expression.substring(expression.indexOf("(") + 1, expression.indexOf(")")).replace(" ", "");
        //根据参数标识转成map
        NodeParamInfoDto nodeParamInfoDto = nodeParamInfoList.stream().collect(Collectors.toMap(NodeParamInfoDto::getSourceCode, NodeParamInfoDto -> NodeParamInfoDto, (k1, k2) -> k1)).get(nodeParam);
        if (StringUtil.isEmpty(nodeParamInfoDto)) {
            log.error("节点临近值差值函数参数错误，参数为：" + expression);
            hashMap.put("resultValue", null);
            hashMap.put("isDefaultValue", isDefaultValue);
            hashMap.put("logInfo", logInfo);
            return hashMap;
        }
        //返回数据
        Double resultValue = null;
        String[] paramArray = nodeParam.split("@");
        if ("gnd".equals(paramArray[0])) {
            //设备id
            String deviceId = paramArray[1];
            //功能点标识
            String functionLogo = paramArray[2];
            //如果索引号不为空，则说明要查询枪历史数据
            Integer indexNum = nodeParamInfoDto.getIndexNum();
            //查询最新值
            Double aDouble = judgeParam(getDeviceFunctionsRealDataByIds(deviceId, functionLogo, indexNum), nodeParamInfoDto);
            if (StringUtil.isNotEmpty(aDouble)) {
                //获取功能点历史数据
                List<DeviceHistoryDto> deviceHistoryDtos;
                if (StringUtil.isNotEmpty(indexNum)) {
                    deviceHistoryDtos = findDeviceHistoryIndexValueList(deviceId, functionLogo, getStartMill(startTime), getEndMill(startTime), countPeriod, indexNum, 1);
                } else {
                    //获取功能点历史数据
                    deviceHistoryDtos = findDeviceDifferenceListFeign(deviceId, functionLogo, getStartMill(startTime), getEndMill(endTime), countPeriod, 1);
                }

                //如果第二个值没有数据，则返回当前值,所以默认结果为当前值
                resultValue = aDouble;
                if (CollectionUtils.isNotEmpty(deviceHistoryDtos) && StringUtil.isNotEmpty(judgeParam(deviceHistoryDtos.get(0).getDataValue(), nodeParamInfoDto))) {
                    Double dataValue = judgeParam(deviceHistoryDtos.get(0).getDataValue(), nodeParamInfoDto);
                    resultValue = aDouble - dataValue;
                } else {
                    isDefaultValue = 1;
                    logInfo = "参数（" + expression + "）,第二个值数据为null或未在参数设定范围之内，返回空值";
                }
            } else {
                isDefaultValue = 1;
                logInfo = "参数（" + expression + "）,第一个值数据为null或未在参数设定范围之内，返回空值";
            }
        } else {
            long storageId = Long.parseLong(paramArray[1]);
            //查询最新一条节点临近值数据
            NodeHistoryDataDto nodeHistoryDataDto = computeNodeMapper.findNodeFirstDataByTableName(NODE_TABLE + storageId);
            if (StringUtil.isNotEmpty(nodeHistoryDataDto) && StringUtil.isNotEmpty(judgeParam(nodeHistoryDataDto.getResultValue(), nodeParamInfoDto))) {
                Double lastNodeValue = judgeParam(nodeHistoryDataDto.getResultValue(), nodeParamInfoDto);
                //获取周期内的第一个临近值
                List<NodeHistoryDataDto> nodeHistoryDataDtoList = computeNodeMapper.findNodeFirstDataByTime(NODE_TABLE + storageId, getStartMill(startTime), getEndMill(endTime));
                if (CollectionUtils.isNotEmpty(nodeHistoryDataDtoList) && StringUtil.isNotEmpty(judgeParam(nodeHistoryDataDtoList.get(0).getResultValue(), nodeParamInfoDto))) {
                    Double firstNodeValue = judgeParam(nodeHistoryDataDtoList.get(0).getResultValue(), nodeParamInfoDto);
                    resultValue = lastNodeValue - firstNodeValue;
                } else {
                    isDefaultValue = 1;
                    logInfo = "参数（" + expression + "）,第二个值数据为null或未在参数设定范围之内，返回空值";
                }
            } else {
                isDefaultValue = 1;
                logInfo = "参数（" + expression + "）,第一个值数据为null或未在参数设定范围之内，返回空值";
            }
        }
        hashMap.put("resultValue", resultValue);
        hashMap.put("isDefaultValue", isDefaultValue);
        hashMap.put("logInfo", logInfo);
        return hashMap;
    }


    /**
     * 节点平方根函数
     *
     * @param expression        函数参数
     * @param nodeParamInfoList 节点参数列表
     */
    private Map<String, Object> nodeSqrtFun(String expression, List<NodeParamInfoDto> nodeParamInfoList) {
        Map<String, Object> resultMap = Maps.newHashMap();
        //日志信息
        String logInfo = null;
        //返回值是否是缺省值 1-是
        Integer isDefaultValue = null;
        //获取出函数中查询参数
        String nodeParam = expression.substring(expression.indexOf("(") + 1, expression.indexOf(")")).replace(" ", "");
        //根据参数标识转成map
        NodeParamInfoDto nodeParamInfoDto = nodeParamInfoList.stream().collect(Collectors.toMap(NodeParamInfoDto::getSourceCode, NodeParamInfoDto -> NodeParamInfoDto, (k1, k2) -> k1)).get(nodeParam);
        if (StringUtil.isEmpty(nodeParamInfoDto)) {
            log.error("节点平方根函数参数错误，参数为：" + expression);
            resultMap.put("resultValue", null);
            resultMap.put("isDefaultValue", isDefaultValue);
            resultMap.put("logInfo", logInfo);
            return resultMap;
        }
        //返回数据
        Double resultValue = null;
        String[] paramArray = nodeParam.split("@");
        if ("gnd".equals(paramArray[0])) {
            //设备id
            String deviceId = paramArray[1];
            //功能点标识
            String functionLogo = paramArray[2];
            //如果索引号不为空，则说明要查询枪历史数据
            Integer indexNum = nodeParamInfoDto.getIndexNum();
            //查询设备功能点实时数据
            Double aDouble = judgeParam(getDeviceFunctionsRealDataByIds(deviceId, functionLogo, indexNum), nodeParamInfoDto);
            if (StringUtil.isNotEmpty(aDouble) && aDouble > 0.0) {
                resultValue = aDouble;
            } else {
                isDefaultValue = 1;
                logInfo = "参数（" + expression + "）,实时数据为null或为负数，无法计算，原始数据为：" + aDouble;
            }
        } else {
            long storageId = Long.parseLong(paramArray[1]);
            //查询当前节点最新一条数据
            NodeHistoryDataDto nodeHistoryDataDto = computeNodeMapper.findNodeLastDataByTableName(NODE_TABLE + storageId);
            if (StringUtil.isNotEmpty(nodeHistoryDataDto) && StringUtil.isNotEmpty(judgeParam(nodeHistoryDataDto.getResultValue(), nodeParamInfoDto))) {
                Double nodeValue = judgeParam(nodeHistoryDataDto.getResultValue(), nodeParamInfoDto);
                if (StringUtil.isNotEmpty(nodeValue) && nodeValue > 0.0) {
                    resultValue = nodeValue;
                } else {
                    isDefaultValue = 1;
                    logInfo = "参数（" + expression + "）,实时数据为null或为负数，无法计算，原始数据为：" + nodeValue;
                }
            } else {
                isDefaultValue = 1;
                logInfo = "参数（" + expression + "）,未查询到最新节点数据，无法计算";
            }
        }
        resultMap.put("nodeParam", nodeParam);
        resultMap.put("resultValue", resultValue);
        resultMap.put("isDefaultValue", isDefaultValue);
        resultMap.put("logInfo", logInfo);
        return resultMap;
    }

    /**
     * 节点计算值函数
     *
     * @param expression        函数参数
     * @param nodeParamInfoList 节点参数列表
     */
    private Map<String, Object> nodeCalculateFun(String expression, List<NodeParamInfoDto> nodeParamInfoList) {
        Map<String, Object> hashMap = Maps.newHashMap();
        //日志信息
        String logInfo = null;
        //返回值是否是缺省值 1-是
        Integer isDefaultValue = null;
        //获取出函数中查询参数
        String nodeParam = expression.substring(expression.indexOf("(") + 1, expression.indexOf(")")).replace(" ", "");
        //根据参数标识转成map
        NodeParamInfoDto nodeParamInfoDto = nodeParamInfoList.stream().collect(Collectors.toMap(NodeParamInfoDto::getSourceCode, NodeParamInfoDto -> NodeParamInfoDto, (k1, k2) -> k1)).get(nodeParam);
        if (StringUtil.isEmpty(nodeParamInfoDto)) {
            log.error("节点计算值函数参数错误，参数为：" + expression);
            hashMap.put("resultValue", null);
            hashMap.put("isDefaultValue", isDefaultValue);
            hashMap.put("logInfo", logInfo);
            return hashMap;
        }
        //返回数据
        Double resultValue = null;
        //源数据值
        Double sourceValue = null;
        String[] paramArray = nodeParam.split("@");
        if ("gnd".equals(paramArray[0])) {
            //设备id
            String deviceId = paramArray[1];
            //功能点标识
            String functionLogo = paramArray[2];
            //如果索引号不为空，则说明要查询枪历史数据
            Integer indexNum = nodeParamInfoDto.getIndexNum();
            //查询设备功能点实时数据
            Double aDouble = judgeParam(getDeviceFunctionsRealDataByIds(deviceId, functionLogo, indexNum), nodeParamInfoDto);
            if (StringUtil.isNotEmpty(aDouble)) {
                sourceValue = aDouble;
            } else {
                isDefaultValue = 1;
                logInfo = "参数（" + expression + "）,数据为null或未在参数设定范围之内，原始数据为：" + aDouble;
            }
        } else {
            long storageId = Long.parseLong(paramArray[1]);
            //查询当前节点最新一条数据
            NodeHistoryDataDto nodeHistoryDataDto = computeNodeMapper.findNodeLastDataByTableName(NODE_TABLE + storageId);
            if (StringUtil.isNotEmpty(nodeHistoryDataDto) && StringUtil.isNotEmpty(judgeParam(nodeHistoryDataDto.getResultValue(), nodeParamInfoDto))) {
                Double nodeValue = judgeParam(nodeHistoryDataDto.getResultValue(), nodeParamInfoDto);
                if (StringUtil.isNotEmpty(nodeValue)) {
                    sourceValue = nodeValue;
                } else {
                    isDefaultValue = 1;
                    logInfo = "参数（" + expression + "）,数据为null或未在参数设定范围之内，原始数据为：" + nodeValue;
                }
            } else {
                isDefaultValue = 1;
                logInfo = "参数（" + expression + "）,未查询到最新值，无法计算";
            }
        }
        if (sourceValue != null && StringUtil.isNotEmpty(sourceValue)) {
            if (expression.contains(FunctionqConstant.SQUARE)) {
                resultValue = Math.pow(sourceValue, 2);
            } else if (expression.contains(FunctionqConstant.REVERSE)) {
                resultValue = (-sourceValue);
            } else if (expression.contains(FunctionqConstant.INTPART)) {
                resultValue = Math.floor(sourceValue);
            } else if (expression.contains(FunctionqConstant.ABS)) {
                resultValue = Math.abs(sourceValue);
            }
        }
        hashMap.put("resultValue", resultValue);
        hashMap.put("isDefaultValue", isDefaultValue);
        hashMap.put("logInfo", logInfo);
        return hashMap;
    }

    /**
     * 节点求X的Y次幂函数
     *
     * @param expression        函数参数
     * @param nodeParamInfoList 节点参数列表
     */
    private Map<String, Object> nodePowFun(String expression, List<NodeParamInfoDto> nodeParamInfoList) {
        Map<String, Object> hashMap = Maps.newHashMap();
        //日志信息
        String logInfo = null;
        //返回值是否是缺省值 1-是
        Integer isDefaultValue = null;
        //获取出函数中查询参数
        String nodeParam = expression.substring(expression.indexOf("(") + 1, expression.indexOf(")")).replace(" ", "");
        //先获取函数中设定的幂数系数，判断是否是整数，如果不是整数直接返回空，并结束这次计算
        String[] nodeParamStr = nodeParam.split(",");
        //系数
        String coefficient = nodeParamStr[1];
        if (!coefficient.matches("-?\\d+")) {
            logInfo = "参数（" + expression + "）,系数不为整数，无法计算，返回null,系数为：" + coefficient;
            hashMap.put("resultValue", null);
            hashMap.put("isDefaultValue", isDefaultValue);
            hashMap.put("logInfo", logInfo);
            return hashMap;
        }
        //节点参数
        String param = nodeParamStr[0];
        //根据参数标识转成map
        NodeParamInfoDto nodeParamInfoDto = nodeParamInfoList.stream().collect(Collectors.toMap(NodeParamInfoDto::getSourceCode, NodeParamInfoDto -> NodeParamInfoDto, (k1, k2) -> k1)).get(nodeParam);
        if (StringUtil.isEmpty(nodeParamInfoDto)) {
            log.error("节点求X的Y次幂函数参数错误，参数为：" + expression);
            hashMap.put("resultValue", null);
            hashMap.put("isDefaultValue", isDefaultValue);
            hashMap.put("logInfo", logInfo);
            return hashMap;
        }
        //返回数据
        Double resultValue = null;
        String[] paramArray = param.split("@");
        if ("gnd".equals(paramArray[0])) {
            //设备id
            String deviceId = paramArray[1];
            //功能点标识
            String functionLogo = paramArray[2];
            //如果索引号不为空，则说明要查询枪历史数据
            Integer indexNum = nodeParamInfoDto.getIndexNum();
            //查询设备功能点实时数据
            Double aDouble = judgeParam(getDeviceFunctionsRealDataByIds(deviceId, functionLogo, indexNum), nodeParamInfoDto);
            //源数据为负数时不进行运算，返回空值
            if (StringUtil.isNotEmpty(aDouble) && aDouble > 0.0) {
                resultValue = Math.pow(judgeParam(aDouble, nodeParamInfoDto), Double.parseDouble(coefficient));
            } else {
                isDefaultValue = 1;
                logInfo = "参数（" + expression + "）,数据为null或数据为负数，原始数据为：" + aDouble;
            }
        } else {
            long storageId = Long.parseLong(paramArray[1]);
            //查询当前节点最新一条数据
            NodeHistoryDataDto nodeHistoryDataDto = computeNodeMapper.findNodeLastDataByTableName(NODE_TABLE + storageId);
            if (StringUtil.isNotEmpty(nodeHistoryDataDto) && StringUtil.isNotEmpty(judgeParam(nodeHistoryDataDto.getResultValue(), nodeParamInfoDto))) {
                Double nodeValue = judgeParam(nodeHistoryDataDto.getResultValue(), nodeParamInfoDto);
                //源数据为负数时不进行运算，返回空值
                if (StringUtil.isNotEmpty(nodeValue) && nodeValue > 0.0) {
                    resultValue = Math.pow(judgeParam(nodeValue, nodeParamInfoDto), Double.parseDouble(coefficient));
                } else {
                    isDefaultValue = 1;
                    logInfo = "参数（" + expression + "）,数据为null或数据为负数，原始数据为：" + nodeValue;
                }
            } else {
                isDefaultValue = 1;
                logInfo = "参数（" + expression + "）,未查询到最新值，无法计算";
            }
        }
        hashMap.put("resultValue", resultValue);
        hashMap.put("isDefaultValue", isDefaultValue);
        hashMap.put("logInfo", logInfo);
        return hashMap;
    }

    /**
     * 节点映射值函数
     *
     * @param expression        函数参数
     * @param startTime         开始时间
     * @param nodeParamInfoList 节点参数列表
     */
    private Map<String, Object> nodeMappedValueFun(String expression, String startTime, List<NodeParamInfoDto> nodeParamInfoList) {
        Map<String, Object> hashMap = Maps.newHashMap();
        //日志信息
        String logInfo = null;
        //返回值是否是缺省值 1-是
        Integer isDefaultValue = null;
        //根据参数标识转成map
        NodeParamInfoDto nodeParamInfoDto = nodeParamInfoList.stream().collect(Collectors.toMap(NodeParamInfoDto::getSourceCode, NodeParamInfoDto -> NodeParamInfoDto, (k1, k2) -> k1)).get(expression);
        if (StringUtil.isEmpty(nodeParamInfoDto)) {
            log.error("节点映射值函数参数错误，参数为：" + expression);
            hashMap.put("resultValue", null);
            hashMap.put("isDefaultValue", isDefaultValue);
            hashMap.put("logInfo", logInfo);
            return hashMap;
        }
        //返回数据
        Double resultValue = null;
        String[] paramArray = expression.split("@");
        if ("gnd".equals(paramArray[0])) {
            //设备id
            String deviceId = paramArray[1];
            //功能点标识
            String functionLogo = paramArray[2];
            //如果索引号不为空，则说明要查询枪历史数据
            Integer indexNum = nodeParamInfoDto.getIndexNum();
            List<DeviceHistoryDto> deviceHistoryDtos = StringUtil.isNotEmpty(indexNum)
                    ? findDeviceHistoryIndexValueList(deviceId, functionLogo, getStartMill(startTime), getEndMill(startTime), null, indexNum, null)
                    : findDeviceDifferenceListFeign(deviceId, functionLogo, getStartMill(startTime), getEndMill(startTime), null, null);
            //查询最新值
            if (CollectionUtils.isNotEmpty(deviceHistoryDtos) && StringUtil.isNotEmpty(judgeParam(deviceHistoryDtos.get(0).getDataValue(), nodeParamInfoDto))) {
                resultValue = judgeParam(deviceHistoryDtos.get(0).getDataValue(), nodeParamInfoDto);
            } else {
                isDefaultValue = 1;
                logInfo = "参数（" + expression + "）,未查询到最新值，返回空值";
            }
        } else {
            long storageId = Long.parseLong(paramArray[1]);
            //查询指定时间的计算节点值
            NodeHistoryDataDto nodeHistoryDataDto = computeNodeMapper.findNodeAppointTimeDataByTableName(NODE_TABLE + storageId, startTime);
            if (StringUtil.isNotEmpty(nodeHistoryDataDto) && StringUtil.isNotEmpty(judgeParam(nodeHistoryDataDto.getResultValue(), nodeParamInfoDto))) {
                resultValue = judgeParam(nodeHistoryDataDto.getResultValue(), nodeParamInfoDto);
            } else {
                //从缓存里面获取
                //根据储存id查询节点id
                String taskId = computeNodeDao.findIdByStorageId(storageId);
                if (StringUtil.isNotEmpty(taskId) && localCache.getValue(taskId) != null && StringUtil.isNotEmpty(localCache.getValue(taskId).getResultValue())) {
                    resultValue = judgeParam(localCache.getValue(taskId).getResultValue(), nodeParamInfoDto);
                } else {
                    isDefaultValue = 1;
                    logInfo = "参数（" + expression + "）,未查询到指定时间点数据，返回空值";
                }
            }
        }
        hashMap.put("resultValue", resultValue);
        hashMap.put("isDefaultValue", isDefaultValue);
        hashMap.put("logInfo", logInfo);
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
     * 查询设备功能点指定单个时间或时间段内的数据或临近值历史数据
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
     * 查询设备功能点实时数据
     */
    private Double getDeviceFunctionsRealDataByIds(String deviceId, String functionLogo, Integer indexNum) {
        //返回数据
        Double resultValue = null;
        //获取功能点实时数据
        Map<String, Map<String, RealDataModel>> deviceFunctionsRealDataMap = deviceService.getDeviceFunctionsRealDataByIds(Collections.singleton(deviceId), functionLogo).getData();
        if (deviceFunctionsRealDataMap != null && !deviceFunctionsRealDataMap.isEmpty()) {
            Map<String, RealDataModel> functionsRealDataMap = deviceFunctionsRealDataMap.get(deviceId);
            if (functionsRealDataMap != null && !functionsRealDataMap.isEmpty()) {
                RealDataModel realDataModel = functionsRealDataMap.get(functionLogo);
                if (StringUtil.isNotEmpty(realDataModel)) {
                    Integer dataType = realDataModel.getDataType();
                    Object dataValue = realDataModel.getDataValue();
                    //如果索引不为空，则获取数据数组中指定下标数据
                    if (StringUtil.isNotEmpty(indexNum) && StringUtil.isNotEmpty(dataValue)) {
                        Object object = JSON.parseArray(JSON.toJSONString(dataValue)).get(indexNum);
                        if (StringUtil.isNotEmpty(object)) {
                            resultValue = (Double) object;
                        }
                    } else {
                        resultValue = dataTypeConvert(dataType, dataValue);
                    }

                }
            }
        }
        return resultValue;
    }

    /**
     * 根据当前时间，获取周期开始时间
     *
     * @param period
     * @param hourTime
     * @return
     * @throws ParseException
     */
    private static String getNearTime(String period, String hourTime) throws ParseException {
        long timeNum = Long.parseLong(period.substring(0, period.length() - 1));
        String queryTime = null;
        //分
        if (period.contains(StaticParamVo.M)) {
            //获取设定的几分钟值，然后计算成时间
            if (timeNum > 1) {
                queryTime = localDateTimeToStr(strToLocalDateTime(hourTime).plusSeconds(1).minusMinutes(timeNum));
            } else {
                queryTime = hourTime.substring(0, 16) + ":00";
            }
            //时
        } else if (period.contains(StaticParamVo.H)) {
            String currentTime = hourTime.substring(0, 14) + "00:00";
            //获取设定的几个小时值，然后计算成时间 m
            if (timeNum > 1) {
                queryTime = localDateTimeToStr(strToLocalDateTime(currentTime).minusHours(timeNum - 1));
            } else {
                queryTime = currentTime;
            }
            //日
        } else if (period.contains(StaticParamVo.D)) {
            if (timeNum > 1) {
                if (isDay(hourTime)) {
                    queryTime = localDateTimeToStr(strToLocalDateTime(hourTime).plusSeconds(1).minusDays(timeNum - 1).withHour(0).withMinute(0));
                } else {
                    queryTime = localDateTimeToStr(strToLocalDateTime(hourTime).plusSeconds(1).minusDays(timeNum).withHour(0).withMinute(0));
                }
            } else {
                queryTime = getDayStart(hourTime.substring(0, 10));
            }
            //月
        } else if (period.contains(StaticParamVo.N)) {
            //获取自然月的开始日期
            queryTime = getDayStart(getStartTimeOfCurrentMonth(hourTime));
            //年
        } else if (period.contains(StaticParamVo.Y)) {
            //获取自然年的开始时间
            queryTime = getDayStart(getYearStartTime(hourTime));
        }
        return queryTime;
    }

    /**
     * 根据多个节点存储id查询节点数据
     *
     * @return
     */
    @Override
    public Map<Long, List<NodeHistoryDataDto>> findNodeTaosDataByIds(NodeHistoryDataVo nodeHistoryDataVo) {
        Map<Long, List<NodeHistoryDataDto>> resultMap = Maps.newHashMap();

        if (StringUtil.isNotEmpty(nodeHistoryDataVo)) {
            nodeHistoryDataVo.getStorageIdList().forEach(storageId -> {
                resultMap.put(storageId, computeNodeMapper.findNodeTaosDataList(NODE_TABLE + storageId, getStartMill(nodeHistoryDataVo.getStartTime()), getEndMill(nodeHistoryDataVo.getEndTime()), nodeHistoryDataVo.getTimeInterval(), nodeHistoryDataVo.getLimitSize()));
            });
        }
        return resultMap;
    }

    /**
     * 根据多个节点存储id查询节点差值数据
     *
     * @return
     */
    @Override
    public Map<Long, List<NodeHistoryDataDto>> findNodeTaosDiffDataByIds(NodeHistoryDataVo nodeHistoryDataVo) {
        Map<Long, List<NodeHistoryDataDto>> resultMap = Maps.newHashMap();

        if (StringUtil.isNotEmpty(nodeHistoryDataVo)) {
            nodeHistoryDataVo.getStorageIdList().forEach(storageId -> {
                resultMap.put(storageId, computeNodeMapper.findNodeDataListByTimeInterval(NODE_TABLE + storageId, getStartMill(nodeHistoryDataVo.getStartTime()), getEndMill(nodeHistoryDataVo.getEndTime()), nodeHistoryDataVo.getTimeInterval()));
            });
        }
        return resultMap;
    }

    /**
     * 根据多个节点存储id和统计函数查询节点统计值数据
     *
     * @param storageIdList 多个节点存储id
     * @param startTime     开始时间-可为空
     * @param endTime       结束时间-可为空
     * @param countFun      统计函数 求和-SUM;最大值-MAX;最小值-MIN;平均值-AVG
     * @param timeInterval  时间间隔 s-秒;m-分钟;h-小时;d-天;n-月;y-年(可为空)
     * @return
     */
    @Override
    public Map<Long, List<NodeHistoryDataDto>> findNodeTaosCountFunDataByIds(List<Long> storageIdList, String startTime, String endTime, String countFun, String timeInterval) {
        Map<Long, List<NodeHistoryDataDto>> resultMap = Maps.newHashMap();

        if (CollectionUtils.isNotEmpty(storageIdList) && StringUtil.isNotEmpty(countFun)) {
            storageIdList.forEach(storageId -> {
                String startMillTime;
                if (StringUtil.isNotEmpty(startTime)) {
                    startMillTime = getStartMill(startTime);
                } else {
                    startMillTime = startTime;
                }
                String endMillTime;
                if (StringUtil.isNotEmpty(endTime)) {
                    endMillTime = getStartMill(endTime);
                } else {
                    endMillTime = endTime;
                }
                resultMap.put(storageId, computeNodeMapper.findNodeTaosCountFunDataByIds(NODE_TABLE + storageId, startMillTime, endMillTime, countFun, timeInterval));
            });
        }
        return resultMap;
    }

}
