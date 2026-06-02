package com.sunmax.together.util;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.sunmax.common.dto.crontab.LocalCacheDto;
import com.sunmax.common.dto.crontab.NodeHistoryDataDto;
import com.sunmax.common.util.DateUtil;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.util.SpringBeanUtil;
import com.sunmax.common.util.StringUtil;
import com.sunmax.common.vo.crontab.VarNodeValueVo;
import com.sunmax.together.dto.operation.storageCount.ElectTypeDto;
import com.sunmax.together.entity.assets.ElectTimeFrameEntity;
import com.sunmax.together.service.feign.CrontabService;
import org.apache.commons.collections.CollectionUtils;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.sunmax.common.util.DateUtil.getLocalDateTimeBetween;
import static com.sunmax.common.util.DateUtil.strToLocalDateTime;
import static com.sunmax.common.util.DoubleUtil.getToDouble;

public class TogetherCommonUtil {

    private static final CrontabService crontabService = SpringBeanUtil.getBean(CrontabService.class);

    /**
     * 查询指定变量节点历史数据
     * @param varNodeValueVo
     * @return 设备id -> 变量编码(包含x时间轴xAxisList) -> 数据
     */

    public static Map<String, Map<String, Object>> findDeviceVarNodeValueByIds(VarNodeValueVo varNodeValueVo) {
        Map<String, Map<String, Object>> resultMap = Maps.newHashMap();

        if (CollectionUtils.isEmpty(varNodeValueVo.getDeviceIdList()) && CollectionUtils.isEmpty(varNodeValueVo.getVarCodeList())) {
            return resultMap;
        }
        String startTime = varNodeValueVo.getStartTime();
        String endTime = varNodeValueVo.getEndTime();
        //获取时间轴
        List<String> getDateTimeBetween = getLocalDateTimeBetween(strToLocalDateTime(startTime), strToLocalDateTime(endTime), varNodeValueVo.getTimeInterval()).stream().map(DateUtil::localDateTimeToStr).collect(Collectors.toList());
        //查询指定变量节点历史数据
        ResponseResult<Map<String, Map<String, List<NodeHistoryDataDto>>>> variableNodeValueByCode = crontabService.findDeviceVarNodeValueByIds(varNodeValueVo);
        if (variableNodeValueByCode.isSuccess() && !variableNodeValueByCode.getData().isEmpty()) {
            varNodeValueVo.getDeviceIdList().forEach(deviceId -> {
                Map<String, Object> dataMap = Maps.newHashMap();
                //获取设备历史数据
                Map<String, List<NodeHistoryDataDto>> deviceNodeHistoryMap = variableNodeValueByCode.getData().get(deviceId);
                if (!deviceNodeHistoryMap.isEmpty()) {
                    //循环获取设备下每个变量历史数据
                    varNodeValueVo.getVarCodeList().forEach(varCode -> {
                        List<NodeHistoryDataDto> nodeHistoryDataDtoList = deviceNodeHistoryMap.get(varCode);
                        if (CollectionUtils.isNotEmpty(nodeHistoryDataDtoList)) {
                            List<Double> resultList = Lists.newArrayList();
                            Map<String, NodeHistoryDataDto> nodeHistoryMap = nodeHistoryDataDtoList.stream().collect(Collectors.toMap(n -> n.getTs().substring(0, 19), nodeHistoryDataDto -> nodeHistoryDataDto, (k1, k2) -> k1));
                            //循环时间轴获取数据
                            getDateTimeBetween.forEach(dateTime -> {
                                Double resultValue = null;
                                NodeHistoryDataDto nodeHistoryDataDto = nodeHistoryMap.get(dateTime);
                                if (StringUtil.isNotEmpty(nodeHistoryDataDto)) {
                                    resultValue = nodeHistoryDataDto.getResultValue() != null ? getToDouble(nodeHistoryDataDto.getResultValue()):null;
                                }
                                resultList.add(resultValue);
                            });
                            dataMap.put(varCode, resultList);
                        }
                        dataMap.put("xAxisList", getDateTimeBetween);
                    });
                }
                resultMap.put(deviceId, dataMap);
            });
        }
        return resultMap;
    }

    /**
     * 查询指定变量节点历史数据(支持把当前节点本地实时缓存数据放在曲线数据最后一个点)
     * @param varNodeValueVo
     * @return 设备id -> 变量编码(包含x时间轴xAxisList) -> 数据
     */

    public static Map<String, Map<String, Object>> findVarNodeValueByIds(VarNodeValueVo varNodeValueVo, Integer isCurrent) {
        Map<String, Map<String, Object>> resultMap = Maps.newHashMap();

        if (CollectionUtils.isEmpty(varNodeValueVo.getDeviceIdList()) && CollectionUtils.isEmpty(varNodeValueVo.getVarCodeList())) {
            return resultMap;
        }
        String startTime = varNodeValueVo.getStartTime();
        String endTime = varNodeValueVo.getEndTime();
        //获取时间轴
        List<String> getDateTimeBetween = getLocalDateTimeBetween(strToLocalDateTime(startTime), strToLocalDateTime(endTime), varNodeValueVo.getTimeInterval()).stream().map(DateUtil::localDateTimeToStr).collect(Collectors.toList());
        //查询指定变量节点历史数据
        ResponseResult<Map<String, Map<String, List<NodeHistoryDataDto>>>> variableNodeValueByCode = crontabService.findDeviceVarNodeValueByIds(varNodeValueVo);
        //查询系统变量实时缓存数据
        ResponseResult<Map<String, Map<String, LocalCacheDto>>> nodeCacheByVarCodes = crontabService.findNodeCacheByVarCodes(varNodeValueVo.getVarCodeList(), String.join("", varNodeValueVo.getDeviceIdList()));
        //循环站点/设备id
        varNodeValueVo.getDeviceIdList().forEach(deviceId -> {
            Map<String, Object> dataMap = Maps.newHashMap();

            //获取当前id历史数据
            Map<String, List<NodeHistoryDataDto>> deviceNodeHistoryMap = Maps.newHashMap();
            if (variableNodeValueByCode.isSuccess() && !variableNodeValueByCode.getData().isEmpty() && variableNodeValueByCode.getData().containsKey(deviceId)) {
                deviceNodeHistoryMap = variableNodeValueByCode.getData().get(deviceId);
            }

            //循环获取设备下每个变量历史数据
            Map<String, List<NodeHistoryDataDto>> finalDeviceNodeHistoryMap = deviceNodeHistoryMap;
            varNodeValueVo.getVarCodeList().forEach(varCode -> {

                //变量历史数据
                Map<String, NodeHistoryDataDto> nodeHistoryMap = Maps.newHashMap();
                if (!finalDeviceNodeHistoryMap.isEmpty() && finalDeviceNodeHistoryMap.containsKey(varCode) && CollectionUtils.isNotEmpty(finalDeviceNodeHistoryMap.get(varCode))) {
                    nodeHistoryMap = finalDeviceNodeHistoryMap.get(varCode).stream().collect(Collectors.toMap(n -> n.getTs().substring(0, 19), nodeHistoryDataDto -> nodeHistoryDataDto, (k1, k2) -> k1));
                }

                //获取当前变量当前id实时缓存数据
                LocalCacheDto localCacheDto = new LocalCacheDto();
                if (nodeCacheByVarCodes.isSuccess() && !nodeCacheByVarCodes.getData().isEmpty() && nodeCacheByVarCodes.getData().containsKey(varCode)
                        && !nodeCacheByVarCodes.getData().get(varCode).isEmpty() && nodeCacheByVarCodes.getData().get(varCode).containsKey(deviceId)) {
                    localCacheDto = nodeCacheByVarCodes.getData().get(varCode).get(deviceId);
                }

                List<Double> resultList = Lists.newArrayList();
                //循环时间轴获取数据
                Map<String, NodeHistoryDataDto> finalNodeHistoryMap = nodeHistoryMap;
                getDateTimeBetween.forEach(dateTime -> {
                    Double resultValue = null;
                    if (!finalNodeHistoryMap.isEmpty() && finalNodeHistoryMap.containsKey(dateTime) && StringUtil.isNotEmpty(finalNodeHistoryMap.get(dateTime))) {
                        resultValue = finalNodeHistoryMap.get(dateTime).getResultValue() != null ? getToDouble(finalNodeHistoryMap.get(dateTime).getResultValue()):null;
                    }
                    resultList.add(resultValue);
                });

                //判断结束时间是否为当前参数，如果是，则把resultList最后一条数替换成节点实时数据
                if (StringUtil.isNotEmpty(isCurrent) && isCurrent == 1 && StringUtil.isNotEmpty(localCacheDto)) {
                    resultList.set(resultList.size() -1, localCacheDto.getResultValue() != null ? getToDouble(localCacheDto.getResultValue()): null);
                }
                dataMap.put(varCode, resultList);
                dataMap.put("xAxisList", getDateTimeBetween);
            });
            resultMap.put(deviceId, dataMap);
        });
        return resultMap;
    }

    /**
     * 按电价配置 ID 构建每半小时的电价类型 Map
     * 返回结构：configId -> 时间 (HH:mm) -> ElectTypeDto(时段类型 + 电费)
     *
     * @param list 电价时段列表
     * @return Map<String, Map<String, ElectTypeDto>>
     */
    public static Map<String, Map<String, ElectTypeDto>> buildHalfHourMapByConfigId(List<ElectTimeFrameEntity> list) {
        if (CollectionUtils.isEmpty(list)) {
            return Maps.newHashMap();
        }

        // Step 1: 按 electConfigId 分组
        Map<String, List<ElectTimeFrameEntity>> grouped = list.stream()
                .collect(Collectors.groupingBy(ElectTimeFrameEntity::getElectConfigId, LinkedHashMap::new, Collectors.toList()));

        // Step 2: 对每个 configId 构建其半小时电价类型 map
        return grouped.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> buildHalfHourMapForOneConfig(entry.getValue()),
                        (v1, v2) -> v1,
                        LinkedHashMap::new
                ));
    }

    /**
     * 为单个 config 构建 48 个半小时点的 Map<String, ElectTypeDto>
     * 优化点：
     * 1. 直接创建 48 个点的数组，避免 1440 个元素的内存开销
     * 2. 预计算 48 个时间键，避免循环中 String.format 开销
     * 3. 对每个点只查找一次匹配的时段，避免重复填充
     */
    private static Map<String, ElectTypeDto> buildHalfHourMapForOneConfig(List<ElectTimeFrameEntity> entities) {
        // 预计算 48 个时间键 (00:00, 00:30, 01:00, ..., 23:30)
        String[] timeKeys = new String[48];
        int[] minuteOfSlot = new int[48];
        for (int i = 0; i < 48; i++) {
            int totalMin = i * 30;
            int hour = totalMin / 60;
            int minute = totalMin % 60;
            // 手动拼接避免 String.format 开销
            timeKeys[i] = (hour < 10 ? "0" + hour : hour) + ":" + (minute < 10 ? "0" + minute : minute);
            minuteOfSlot[i] = totalMin;
        }

        // 直接为 48 个时间点查找对应的电价
        ElectTypeDto[] slotDtos = new ElectTypeDto[48];
        for (int i = 0; i < 48; i++) {
            int currentMin = minuteOfSlot[i];
            // 查找当前分钟所属的时段
            for (ElectTimeFrameEntity e : entities) {
                int start = toMinutes(e.getStartTime());
                int end = "23:59".equals(e.getEndTime()) ? 1439 : toMinutes(e.getEndTime());
                if (currentMin >= start && currentMin <= end) {
                    ElectTypeDto dto = new ElectTypeDto();
                    dto.setPeriodType(e.getPeriodType());
                    dto.setElectMoney(e.getElectMoney());
                    slotDtos[i] = dto;
                    break;
                }
            }
        }

        // 构建 LinkedHashMap
        Map<String, ElectTypeDto> result = Maps.newLinkedHashMapWithExpectedSize(48);
        for (int i = 0; i < 48; i++) {
            result.put(timeKeys[i], slotDtos[i]);
        }
        return result;
    }

    /**
     * 将时间字符串转换为分钟数 (格式：HH:mm)
     * 优化：使用 indexOf 替代 split，减少数组创建开销
     */
    private static int toMinutes(String time) {
        int colonIndex = time.indexOf(':');
        return Integer.parseInt(time.substring(0, colonIndex)) * 60
               + Integer.parseInt(time.substring(colonIndex + 1));
    }

}
