package com.sunmax.together.service.operation.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.sunmax.common.dto.data.NodeDifHistoryDto;
import com.sunmax.common.dto.device.DeviceBasicInfoDto;
import com.sunmax.common.dto.device.SiteInfoDto;
import com.sunmax.common.dto.device.SiteScenarioTypeDto;
import com.sunmax.common.util.DateUtil;
import com.sunmax.common.util.DoubleUtil;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.util.StringUtil;
import com.sunmax.common.vo.FunctionLogoParamVo;
import com.sunmax.common.vo.SiteFieldParamVo;
import com.sunmax.common.vo.data.DeviceHistoryQueryVo;
import com.sunmax.together.dao.asset.ElectConfigDao;
import com.sunmax.together.dao.asset.ElectTimeFrameDao;
import com.sunmax.together.dto.operation.storageCount.*;
import com.sunmax.together.entity.assets.ElectConfigEntity;
import com.sunmax.together.service.feign.DataService;
import com.sunmax.together.service.feign.DeviceService;
import com.sunmax.together.service.operation.StorageCountService;
import com.sunmax.together.util.TogetherCommonUtil;
import com.sunmax.together.vo.operation.storageCount.StorageCountVo;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.compress.utils.Lists;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

import static com.sunmax.common.util.DateUtil.*;

@Service
public class StorageCountServiceImpl implements StorageCountService {

    @Autowired
    private DeviceService deviceService;

    @Autowired
    private DataService dataService;

    @Autowired
    private ElectConfigDao electConfigDao;

    @Autowired
    private ElectTimeFrameDao electTimeFrameDao;

    private static @NotNull DeviceHistoryQueryVo getDeviceHistoryQueryVo(Set<String> deviceIds, String historyStartDate, String endDate) {
        DeviceHistoryQueryVo deviceHistoryQueryVo = new DeviceHistoryQueryVo();
        deviceHistoryQueryVo.setDeviceIds(deviceIds);
        deviceHistoryQueryVo.setFunctionLogos(new HashSet<>(Arrays.asList(FunctionLogoParamVo.TOTAL_POSITIVE_ACTIVE_ENERGY,
                FunctionLogoParamVo.TOTAL_INVERSE_ACTIVE_ENERGY)));
        deviceHistoryQueryVo.setStartTime(DateUtil.getDayStart(historyStartDate));
        deviceHistoryQueryVo.setEndTime(DateUtil.getDayEnd(endDate));
        deviceHistoryQueryVo.setTimeInterval("30m");
        return deviceHistoryQueryVo;
    }

    //校验入参参数
    private static boolean isValidStorageCount(StorageCountVo storageCountVo) {
        // 检查输入对象是否为null
        if (storageCountVo == null) {
            // 可以考虑添加日志记录这里的失败原因
            return true;
        }

        // 定义需要校验的字段数组
        Object[] fieldsToValidate = {
                storageCountVo.getQueryType(),
                storageCountVo.getDataId(),
                storageCountVo.getDateType(),
                storageCountVo.getStartDate(),
                storageCountVo.getEndDate()
        };

        // 使用Apache Commons Lang3的StringUtils.isEmpty()进行联合检查
        // 它同时处理了空和只含空格的字符串
        return Arrays.stream(fieldsToValidate).anyMatch(StringUtil::isEmpty);
    }

    @Override
    public ResponseResult<List<MeterListDto>> getMeterListBySiteId(String siteId) {
        //返回的集合
        List<MeterListDto> resultList = Lists.newArrayList();
        //根据站点id查询储能系统
        SiteInfoDto siteInfo = deviceService.findSiteBasicInfoByIds(Collections.singletonList(siteId))
                .getData().get(siteId);
        if (siteInfo != null && CollectionUtils.isNotEmpty(siteInfo.getSiteScenarioTypeDtos())) {
            //根据多个储能系统id查询并网表数据
            List<String> systemIds = siteInfo.getSiteScenarioTypeDtos().stream()
                    .filter(s -> Objects.equals(s.getScenarioType(), 2))
                    .map(SiteScenarioTypeDto::getId).collect(Collectors.toList());
            resultList = deviceService.findDeviceInfoByParentIds(systemIds).getData().values().stream()
                    .flatMap(s -> s.stream().filter(d ->
                            Objects.equals(d.getTypeId(), "38"))).map(d -> {
                        MeterListDto result = new MeterListDto();
                        result.setId(d.getId());
                        result.setDeviceName(d.getDeviceName());
                        return result;
                    }).collect(Collectors.toList());
        }
        return ResponseResult.ok(resultList);
    }

    @Override
    public ResponseResult<StorageQtCountDto> countStorageQt(StorageCountVo storageCountVo) {

        //返回的对象
        StorageQtCountDto result = new StorageQtCountDto();

        //校验入参
        if (isValidStorageCount(storageCountVo)) {
            return ResponseResult.paramError(ResponseResult.PARAM_ERROR);
        }

        //获取多个查询设备id
        Set<String> deviceIds = Sets.newHashSet();
        if (storageCountVo.getQueryType() == 1) { //站点id
            //根据站点id查询储能系统
            SiteInfoDto siteInfo = deviceService.findSiteBasicInfoByIds(Collections.singletonList(storageCountVo.getDataId()))
                    .getData().get(storageCountVo.getDataId());
            if (siteInfo != null && CollectionUtils.isNotEmpty(siteInfo.getSiteScenarioTypeDtos())) {
                //根据多个储能系统id查询并网表数据
                List<String> systemIds = siteInfo.getSiteScenarioTypeDtos().stream()
                        .filter(s -> Objects.equals(s.getScenarioType(), 2))
                        .map(SiteScenarioTypeDto::getId).collect(Collectors.toList());
                deviceIds = deviceService.findDeviceInfoByParentIds(systemIds).getData().values().stream()
                        .flatMap(s -> s.stream().filter(d ->
                                Objects.equals(d.getTypeId(), "38"))).map(DeviceBasicInfoDto::getId)
                        .collect(Collectors.toSet());
            }
        }
        if (storageCountVo.getQueryType() == 2) { //设备id
            deviceIds.add(storageCountVo.getDataId());
        }
        //校验是否存在设备
        if (CollectionUtils.isEmpty(deviceIds)) {
            return ResponseResult.ok(result);
        }

        //开始时间
        String startTime = DateUtil.getDayStart(storageCountVo.getStartDate());
        //结束时间
        String endTime = DateUtil.getDayEnd(storageCountVo.getEndDate());
        //数据时间间隔 s-秒 m-分钟 h-小时 d-天 n-月 y-年
        String timeInterval = null;
        //时间格式间隔 1-(yyyy-MM-dd HH:mm:ss) 2-(yyyy-MM-dd) 3-(yyyy-MM) 4-(yyyy) 5-(HH:mm:ss) 6-(HH:mm)
        Integer formatInterval = null;
        //日期类型 1-日 2-月 3-年
        switch (storageCountVo.getDateType()) {
            case 1:
                timeInterval = "1d";
                formatInterval = 2;
                break;
            case 2:
                timeInterval = "1n";
                formatInterval = 3;
                break;
            case 3:
                timeInterval = "1y";
                formatInterval = 4;
                break;
        }
        Set<String> dateList = getDateFormatTimeList(startTime, endTime, timeInterval, formatInterval);
        //根据多个设备id和日期查询电表功能点历史数据
        DeviceHistoryQueryVo deviceHistoryQueryVo = new DeviceHistoryQueryVo();
        deviceHistoryQueryVo.setDeviceIds(deviceIds);
        deviceHistoryQueryVo.setFunctionLogos(new HashSet<>(Arrays.asList(FunctionLogoParamVo.TOTAL_POSITIVE_ACTIVE_ENERGY,
                FunctionLogoParamVo.TOTAL_INVERSE_ACTIVE_ENERGY, FunctionLogoParamVo.TOP_SUP_KWH, FunctionLogoParamVo.PEAK_SUP_KWH,
                FunctionLogoParamVo.PLAIN_SUP_KWH, FunctionLogoParamVo.VALLEY_SUP_KWH, FunctionLogoParamVo.DEEP_SUP_KWH,
                FunctionLogoParamVo.TOP_REV_KWH, FunctionLogoParamVo.PEAK_REV_KWH, FunctionLogoParamVo.PLAIN_REV_KWH,
                FunctionLogoParamVo.VALLEY_REV_KWH, FunctionLogoParamVo.DEEP_REV_KWH)));
        deviceHistoryQueryVo.setStartTime(startTime);
        deviceHistoryQueryVo.setEndTime(endTime);
        deviceHistoryQueryVo.setTimeInterval(timeInterval);
        Integer finalFormatInterval = formatInterval;
        Map<String, List<NodeDifHistoryDto>> deviceHistoryMap = dataService.findNodeDifHistoryListFeign(deviceHistoryQueryVo).getData().values().stream()
                .flatMap(s -> s.values().stream().flatMap(Collection::stream))
                .filter(c -> StringUtil.isNotEmpty(c.getFirstDateTime()) && StringUtil.isNotEmpty(c.getFirstDataValue()) && StringUtil.isNotEmpty(c.getLastDataValue()))
                .collect(Collectors.groupingBy(c -> {
                    String dateFormat = format(strToDate(c.getFirstDateTime()), getFormatIntervalPattern(finalFormatInterval));
                    if (StringUtil.isNotEmpty(dateFormat)) {
                        return dateFormat;
                    }
                    return c.getFirstDateTime();
                }));
        //对数据进行组装
        for (String date : dateList) {
            result.getDateList().add(date);
            if (deviceHistoryMap.containsKey(date)) {
                Map<String, List<NodeDifHistoryDto>> functionDataMap = deviceHistoryMap.get(date).stream().collect(Collectors
                        .groupingBy(NodeDifHistoryDto::getFunctionLogo));
                if (functionDataMap.containsKey(FunctionLogoParamVo.TOTAL_POSITIVE_ACTIVE_ENERGY)) { //正向有功电量(总)
                    double functionKwh = functionDataMap.get(FunctionLogoParamVo.TOTAL_POSITIVE_ACTIVE_ENERGY).stream().mapToDouble(d -> Double.parseDouble(String.valueOf(d.getLastDataValue()))
                            - Double.parseDouble(String.valueOf(d.getFirstDataValue()))).sum();
                    result.setChargeQt(DoubleUtil.getToDouble(result.getChargeQt() + functionKwh));
                }
                if (functionDataMap.containsKey(FunctionLogoParamVo.TOTAL_INVERSE_ACTIVE_ENERGY)) { //反向有功电量(总)
                    double functionKwh = functionDataMap.get(FunctionLogoParamVo.TOTAL_INVERSE_ACTIVE_ENERGY).stream().mapToDouble(d -> Double.parseDouble(String.valueOf(d.getLastDataValue()))
                            - Double.parseDouble(String.valueOf(d.getFirstDataValue()))).sum();
                    result.setDischargeQt(DoubleUtil.getToDouble(result.getDischargeQt() + functionKwh));
                }
                if (functionDataMap.containsKey(FunctionLogoParamVo.TOP_SUP_KWH)) { //正向有功电量(尖)
                    double functionKwh = functionDataMap.get(FunctionLogoParamVo.TOP_SUP_KWH).stream().mapToDouble(d -> Double.parseDouble(String.valueOf(d.getLastDataValue()))
                            - Double.parseDouble(String.valueOf(d.getFirstDataValue()))).sum();
                    result.getTopChargeQtList().add(DoubleUtil.getToDouble(functionKwh));
                    result.setTopChargeQt(DoubleUtil.getToDouble(result.getTopChargeQt() + functionKwh));
                } else {
                    result.getTopChargeQtList().add(0.0);
                }
                if (functionDataMap.containsKey(FunctionLogoParamVo.PEAK_SUP_KWH)) { //正向有功电量(峰)
                    double functionKwh = functionDataMap.get(FunctionLogoParamVo.PEAK_SUP_KWH).stream().mapToDouble(d -> Double.parseDouble(String.valueOf(d.getLastDataValue()))
                            - Double.parseDouble(String.valueOf(d.getFirstDataValue()))).sum();
                    result.getPeakChargeQtList().add(DoubleUtil.getToDouble(functionKwh));
                    result.setPeakChargeQt(DoubleUtil.getToDouble(result.getPeakChargeQt() + functionKwh));
                } else {
                    result.getPeakChargeQtList().add(0.0);
                }
                if (functionDataMap.containsKey(FunctionLogoParamVo.PLAIN_SUP_KWH)) { //正向有功电量(平)
                    double functionKwh = functionDataMap.get(FunctionLogoParamVo.PLAIN_SUP_KWH).stream().mapToDouble(d -> Double.parseDouble(String.valueOf(d.getLastDataValue()))
                            - Double.parseDouble(String.valueOf(d.getFirstDataValue()))).sum();
                    result.getPlainChargeQtList().add(DoubleUtil.getToDouble(functionKwh));
                    result.setPlainChargeQt(DoubleUtil.getToDouble(result.getPlainChargeQt() + functionKwh));
                } else {
                    result.getPlainChargeQtList().add(0.0);
                }
                if (functionDataMap.containsKey(FunctionLogoParamVo.VALLEY_SUP_KWH)) { //正向有功电量(谷)
                    double functionKwh = functionDataMap.get(FunctionLogoParamVo.VALLEY_SUP_KWH).stream().mapToDouble(d -> Double.parseDouble(String.valueOf(d.getLastDataValue()))
                            - Double.parseDouble(String.valueOf(d.getFirstDataValue()))).sum();
                    result.getValleyChargeQtList().add(DoubleUtil.getToDouble(functionKwh));
                    result.setValleyChargeQt(DoubleUtil.getToDouble(result.getValleyChargeQt() + functionKwh));
                } else {
                    result.getValleyChargeQtList().add(0.0);
                }
                if (functionDataMap.containsKey(FunctionLogoParamVo.DEEP_SUP_KWH)) { //正向有功电量(深谷)
                    double functionKwh = functionDataMap.get(FunctionLogoParamVo.DEEP_SUP_KWH).stream().mapToDouble(d -> Double.parseDouble(String.valueOf(d.getLastDataValue()))
                            - Double.parseDouble(String.valueOf(d.getFirstDataValue()))).sum();
                    result.getDeepChargeQtList().add(DoubleUtil.getToDouble(functionKwh));
                    result.setDeepChargeQt(DoubleUtil.getToDouble(result.getDeepChargeQt() + functionKwh));
                } else {
                    result.getDeepChargeQtList().add(0.0);
                }
                if (functionDataMap.containsKey(FunctionLogoParamVo.TOP_REV_KWH)) { //反向有功电量(尖)
                    double functionKwh = functionDataMap.get(FunctionLogoParamVo.TOP_REV_KWH).stream().mapToDouble(d -> Double.parseDouble(String.valueOf(d.getLastDataValue()))
                            - Double.parseDouble(String.valueOf(d.getFirstDataValue()))).sum();
                    result.getTopDischargeQtList().add(DoubleUtil.getToDouble(functionKwh));
                    result.setTopDischargeQt(DoubleUtil.getToDouble(result.getTopDischargeQt() + functionKwh));
                } else {
                    result.getTopDischargeQtList().add(0.0);
                }
                if (functionDataMap.containsKey(FunctionLogoParamVo.PEAK_REV_KWH)) { //反向有功电量(峰)
                    double functionKwh = functionDataMap.get(FunctionLogoParamVo.PEAK_REV_KWH).stream().mapToDouble(d -> Double.parseDouble(String.valueOf(d.getLastDataValue()))
                            - Double.parseDouble(String.valueOf(d.getFirstDataValue()))).sum();
                    result.getPeakDischargeQtList().add(DoubleUtil.getToDouble(functionKwh));
                    result.setPeakDischargeQt(DoubleUtil.getToDouble(result.getPeakDischargeQt() + functionKwh));
                } else {
                    result.getPeakDischargeQtList().add(0.0);
                }
                if (functionDataMap.containsKey(FunctionLogoParamVo.PLAIN_REV_KWH)) { //反向有功电量(平)
                    double functionKwh = functionDataMap.get(FunctionLogoParamVo.PLAIN_REV_KWH).stream().mapToDouble(d -> Double.parseDouble(String.valueOf(d.getLastDataValue()))
                            - Double.parseDouble(String.valueOf(d.getFirstDataValue()))).sum();
                    result.getPlainDischargeQtList().add(DoubleUtil.getToDouble(functionKwh));
                    result.setPlainDischargeQt(DoubleUtil.getToDouble(result.getPlainDischargeQt() + functionKwh));
                } else {
                    result.getPlainDischargeQtList().add(0.0);
                }
                if (functionDataMap.containsKey(FunctionLogoParamVo.VALLEY_REV_KWH)) { //反向有功电量(谷)
                    double functionKwh = functionDataMap.get(FunctionLogoParamVo.VALLEY_REV_KWH).stream().mapToDouble(d -> Double.parseDouble(String.valueOf(d.getLastDataValue()))
                            - Double.parseDouble(String.valueOf(d.getFirstDataValue()))).sum();
                    result.getValleyDischargeQtList().add(DoubleUtil.getToDouble(functionKwh));
                    result.setValleyDischargeQt(DoubleUtil.getToDouble(result.getValleyDischargeQt() + functionKwh));
                } else {
                    result.getValleyDischargeQtList().add(0.0);
                }
                if (functionDataMap.containsKey(FunctionLogoParamVo.DEEP_REV_KWH)) { //反向有功电量(深谷)
                    double functionKwh = functionDataMap.get(FunctionLogoParamVo.DEEP_REV_KWH).stream().mapToDouble(d -> Double.parseDouble(String.valueOf(d.getLastDataValue()))
                            - Double.parseDouble(String.valueOf(d.getFirstDataValue()))).sum();
                    result.getDeepDischargeQtList().add(DoubleUtil.getToDouble(functionKwh));
                    result.setDeepDischargeQt(DoubleUtil.getToDouble(result.getDeepDischargeQt() + functionKwh));
                } else {
                    result.getDeepDischargeQtList().add(0.0);
                }
            } else {
                result.getTopChargeQtList().add(0.0);
                result.getPeakChargeQtList().add(0.0);
                result.getPlainChargeQtList().add(0.0);
                result.getValleyChargeQtList().add(0.0);
                result.getDeepChargeQtList().add(0.0);
                result.getTopDischargeQtList().add(0.0);
                result.getPeakDischargeQtList().add(0.0);
                result.getPlainDischargeQtList().add(0.0);
                result.getValleyDischargeQtList().add(0.0);
                result.getDeepDischargeQtList().add(0.0);
            }
        }
        return ResponseResult.ok(result);
    }

    @Override
    public ResponseResult<StorageIncomeCountDto> countStorageIncome(StorageCountVo storageCountVo) {

        //返回的对象
        StorageIncomeCountDto result = new StorageIncomeCountDto();

        //校验入参
        if (isValidStorageCount(storageCountVo)) {
            return ResponseResult.paramError(ResponseResult.PARAM_ERROR);
        }

        String dataId = storageCountVo.getDataId();
        Integer dateType = storageCountVo.getDateType();
        String startDate = storageCountVo.getStartDate();
        String endDate = storageCountVo.getEndDate();


        //1.获取日期列表
        List<String> dateList = DateUtil.getDateBetween(dateType, startDate, endDate);

        //2.获取站点id,多个设备id,开始日期(投运日期或建站创建日期)和结束日期(当天)
        String siteId = null;
        Set<String> deviceIds = Sets.newHashSet();
        String historyStartDate = null;
        String historyEndDate = DateUtil.localDateToStr(DateUtil.strToLocalDate(startDate).minusDays(1));
        if (storageCountVo.getQueryType() == 1) { //站点id
            siteId = dataId;
            //根据站点id查询站点数据
            SiteInfoDto siteInfo = deviceService.findSiteBasicInfoByIds(Collections.singletonList(dataId)).getData().get(dataId);
            if (siteInfo != null && CollectionUtils.isNotEmpty(siteInfo.getSiteScenarioTypeDtos())) {
                //根据多个储能系统id查询并网表数据
                List<String> systemIds = siteInfo.getSiteScenarioTypeDtos().stream()
                        .filter(s -> Objects.equals(s.getScenarioType(), 2))
                        .map(SiteScenarioTypeDto::getId).collect(Collectors.toList());
                deviceIds = deviceService.findDeviceInfoByParentIds(systemIds).getData().values().stream()
                        .flatMap(s -> s.stream().filter(d ->
                                Objects.equals(d.getTypeId(), "38"))).map(DeviceBasicInfoDto::getId)
                        .collect(Collectors.toSet());
                if (StringUtil.isNotEmpty(siteInfo.getSiteReadwriteObject())) {
                    JSONObject jsonObject = JSON.parseObject(siteInfo.getSiteReadwriteObject());
                    if (jsonObject.containsKey(SiteFieldParamVo.OFFICIAL_RUN_TIME)) {
                        historyStartDate = jsonObject.getString(SiteFieldParamVo.OFFICIAL_RUN_TIME);
                    }
                }
                if (StringUtil.isEmpty(historyStartDate) && StringUtil.isNotEmpty(siteInfo.getCreateTime())) {
                    historyStartDate = DateUtil.localDateToStr(siteInfo.getCreateTime().toLocalDate());
                }
            }
        }
        if (storageCountVo.getQueryType() == 2) { //设备id
            deviceIds.add(dataId);
            DeviceBasicInfoDto deviceInfo = deviceService.findDeviceBasicInfoByIds(Collections.singletonList(dataId))
                    .getData().get(dataId);
            if (deviceInfo != null) {
                siteId = deviceInfo.getSiteId();
                if (StringUtil.isEmpty(historyStartDate) && StringUtil.isNotEmpty(deviceInfo.getCreateTime())) {
                    historyStartDate = DateUtil.localDateToStr(deviceInfo.getCreateTime().toLocalDate());
                }
            }
        }

        if (StringUtil.isEmpty(siteId) || CollectionUtils.isEmpty(deviceIds) || StringUtil.isEmpty(historyStartDate)) {
            return ResponseResult.ok(result);
        }

//        long electStartTime = System.currentTimeMillis();
        //根据站点id查询储能购电电价和售电电价
        //日期 -> (分时段(每半个小时) -> 电价类型实体类)
        Map<String, Map<String, ElectTypeDto>> purchaseElectMap = Maps.newHashMap();//购电电价
        Map<String, Map<String, ElectTypeDto>> saleElectMap = Maps.newHashMap();//售电电价
        //根据站点id查询站点电价
        List<ElectConfigEntity> electConfigList = electConfigDao.findElectConfigList(siteId, Arrays.asList(4, 5), historyStartDate, endDate);
        if (CollectionUtils.isNotEmpty(electConfigList)) {
            //根据多个电站配置id查询电价
            Set<String> electConfigIds = electConfigList.stream().map(ElectConfigEntity::getId).collect(Collectors.toSet());
            Map<String, Map<String, ElectTypeDto>> electTimeFrameMap = TogetherCommonUtil.buildHalfHourMapByConfigId(electTimeFrameDao.findAllByElectConfigIdIn(electConfigIds));
            //对数据进行组装
            List<String> dateConfigList = getDateBetween(1, historyStartDate, endDate);
            electConfigList.stream().collect(Collectors.groupingBy(ElectConfigEntity::getModuleType))
                    .forEach((moduleType, electConfigs) -> {
                        for (ElectConfigEntity electConfig : electConfigs) {
                            //电价配置 (半小时时段 -> 电费)
                            Map<String, ElectTypeDto> timeFrameMap = electTimeFrameMap.get(electConfig.getId());
                            if (MapUtils.isNotEmpty(timeFrameMap)) {
                                //过滤日期范围内的日期
                                List<String> electDateList = DateUtil.getDateBetween(1, electConfig.getStartDate(), electConfig.getEndDate());
                                List<String> electFiletrDateList = dateConfigList.stream().filter(electDateList::contains).collect(Collectors.toList());
                                for (String date : electFiletrDateList) {
                                    if (moduleType == 5) { //储能购电电价
                                        purchaseElectMap.put(date, timeFrameMap);
                                    }
                                    if (moduleType == 4) { //储能售电电价
                                        saleElectMap.put(date, timeFrameMap);
                                    }
                                }
                            }
                        }
                    });
        }
//        long dataStartTime = System.currentTimeMillis();
//        System.out.println("查询电价消耗多少毫秒: " + (dataStartTime - electStartTime) + " 毫秒");

        //根据多个设备id和日期查询储能系统下面的电表功能点历史数据
        DeviceHistoryQueryVo deviceQueryVo = getDeviceHistoryQueryVo(deviceIds, historyStartDate, endDate);
        //获取每天的电量,日期(年月日)->(时分秒 ->(功能点标识 -> 电量))
        Map<String, Map<String, List<NodeDifHistoryDto>>> dataMap = dataService.findNodeDifHistoryListFeign(deviceQueryVo).getData();
//        long costHistoryTime = System.currentTimeMillis();
//        System.out.println("查询数据消耗多少毫秒: " + (costHistoryTime - dataStartTime) + " 毫秒");
        Map<String, Map<String, Map<String, Double>>> dayQtMap = dataMap.values()
                .stream().flatMap(s -> s.values().stream().flatMap(Collection::stream))
                .filter(c -> StringUtil.isNotEmpty(c.getFirstDateTime()))
                .collect(Collectors.groupingBy(
                        // 第一层分组：提取年月日
                        c -> c.getFirstDateTime().substring(0, 10),
                        Collectors.groupingBy(
                                // 第二层分组：提取时分
                                c -> c.getFirstDateTime().substring(11, 16),
                                Collectors.groupingBy(
                                        // 第三层分组：根据功能点标识 functionLogo 分组
                                        NodeDifHistoryDto::getFunctionLogo,
                                        // 终端操作：对 dataValue 进行求和
                                        Collectors.summingDouble(c -> DoubleUtil.getObjSub(c.getFirstDataValue(), c.getLastDataValue()))
                                )
                        )
                ));
//        long costHistoryTime1 = System.currentTimeMillis();
//        System.out.println("转换数据消耗多少毫秒: " + (costHistoryTime1 - costHistoryTime) + " 毫秒");

        //获取历史累计收益(放电收入-充电成本)
        BigDecimal historyIncome = getStorageHistoryCost(historyStartDate, historyEndDate, dayQtMap, purchaseElectMap, saleElectMap);

//        long costStartTime = System.currentTimeMillis();
//        System.out.println("统计历史收益消耗多少毫秒: " + (costStartTime - costHistoryTime1) + " 毫秒");

        //获取每天的尖峰平谷成本和收入
        Map<String, Map<String, BigDecimal>> dayCostMap = getStorageCostMap(startDate, endDate, dayQtMap, purchaseElectMap, saleElectMap);

//        long handleStartTime = System.currentTimeMillis();
//        System.out.println("统计当前收益消耗多少毫秒: " + (handleStartTime - costStartTime) + " 毫秒");

        //对数据进行组装
        for (String date : dateList) {
            result.getDateList().add(date);
            List<Map<String, BigDecimal>> costList = Lists.newArrayList();
            if (dateType == 1) { //日
                if (dayCostMap.containsKey(date)) {
                    costList.add(dayCostMap.get(date));
                }
            } else {
                costList.addAll(dayCostMap.entrySet().stream().filter(s -> s.getKey().contains(date))
                        .map(Map.Entry::getValue).collect(Collectors.toList()));
            }
            if (CollectionUtils.isEmpty(costList)) {
                result.getChargeMoneyList().add(BigDecimal.ZERO);
                result.getDischargeMoneyList().add(BigDecimal.ZERO);
                result.getIncomeList().add(BigDecimal.ZERO);
                result.getTotalIncomeList().add(historyIncome);
                continue;
            }

            //充电成本(总)
            BigDecimal chargeMoney = BigDecimal.ZERO;
            //售电收入(总)
            BigDecimal dischargeMoney = BigDecimal.ZERO;
            for (Map<String, BigDecimal> costMap : costList) {
                //获取充电成本(尖峰平谷深)
                if (costMap.containsKey(FunctionLogoParamVo.TOP_SUP_KWH)) { //尖
                    BigDecimal cost = costMap.get(FunctionLogoParamVo.TOP_SUP_KWH);
                    result.setTopChargeMoney(DoubleUtil.getToBigDecimal(result.getTopChargeMoney().add(cost)));
                    chargeMoney = chargeMoney.add(cost);
                }
                if (costMap.containsKey(FunctionLogoParamVo.PEAK_SUP_KWH)) { //峰
                    BigDecimal cost = costMap.get(FunctionLogoParamVo.PEAK_SUP_KWH);
                    result.setPeakChargeMoney(DoubleUtil.getToBigDecimal(result.getPeakChargeMoney().add(cost)));
                    chargeMoney = chargeMoney.add(cost);
                }
                if (costMap.containsKey(FunctionLogoParamVo.PLAIN_SUP_KWH)) { //平
                    BigDecimal cost = costMap.get(FunctionLogoParamVo.PLAIN_SUP_KWH);
                    result.setPlainChargeMoney(DoubleUtil.getToBigDecimal(result.getPlainChargeMoney().add(cost)));
                    chargeMoney = chargeMoney.add(cost);
                }
                if (costMap.containsKey(FunctionLogoParamVo.VALLEY_SUP_KWH)) { //谷
                    BigDecimal cost = costMap.get(FunctionLogoParamVo.VALLEY_SUP_KWH);
                    result.setValleyChargeMoney(DoubleUtil.getToBigDecimal(result.getValleyChargeMoney().add(cost)));
                    chargeMoney = chargeMoney.add(cost);
                }
                if (costMap.containsKey(FunctionLogoParamVo.DEEP_SUP_KWH)) { //深谷
                    BigDecimal cost = costMap.get(FunctionLogoParamVo.DEEP_SUP_KWH);
                    result.setDeepChargeMoney(DoubleUtil.getToBigDecimal(result.getDeepChargeMoney().add(cost)));
                    chargeMoney = chargeMoney.add(cost);
                }
                //获取售电收入(尖峰平谷深)
                if (costMap.containsKey(FunctionLogoParamVo.TOP_REV_KWH)) { //尖
                    BigDecimal cost = costMap.get(FunctionLogoParamVo.TOP_REV_KWH);
                    result.setTopDischargeMoney(DoubleUtil.getToBigDecimal(result.getTopDischargeMoney().add(cost)));
                    dischargeMoney = dischargeMoney.add(cost);
                }
                if (costMap.containsKey(FunctionLogoParamVo.PEAK_REV_KWH)) { //峰
                    BigDecimal cost = costMap.get(FunctionLogoParamVo.PEAK_REV_KWH);
                    result.setPeakDischargeMoney(DoubleUtil.getToBigDecimal(result.getPeakDischargeMoney().add(cost)));
                    dischargeMoney = dischargeMoney.add(cost);
                }
                if (costMap.containsKey(FunctionLogoParamVo.PLAIN_REV_KWH)) { //平
                    BigDecimal cost = costMap.get(FunctionLogoParamVo.PLAIN_REV_KWH);
                    result.setPlainDischargeMoney(DoubleUtil.getToBigDecimal(result.getPlainDischargeMoney().add(cost)));
                    dischargeMoney = dischargeMoney.add(cost);
                }
                if (costMap.containsKey(FunctionLogoParamVo.VALLEY_REV_KWH)) { //谷
                    BigDecimal cost = costMap.get(FunctionLogoParamVo.VALLEY_REV_KWH);
                    result.setValleyDischargeMoney(DoubleUtil.getToBigDecimal(result.getValleyDischargeMoney().add(cost)));
                    dischargeMoney = dischargeMoney.add(cost);
                }
                if (costMap.containsKey(FunctionLogoParamVo.DEEP_REV_KWH)) { //深谷
                    BigDecimal cost = costMap.get(FunctionLogoParamVo.DEEP_REV_KWH);
                    result.setDeepDischargeMoney(DoubleUtil.getToBigDecimal(result.getDeepDischargeMoney().add(cost)));
                    dischargeMoney = dischargeMoney.add(cost);
                }
            }
            //充电成本
            result.setChargeMoney(DoubleUtil.getToBigDecimal(result.getChargeMoney().add(chargeMoney)));
            result.getChargeMoneyList().add(DoubleUtil.getToBigDecimal(chargeMoney));
            //售电收入
            result.setDischargeMoney(DoubleUtil.getToBigDecimal(result.getDischargeMoney().add(dischargeMoney)));
            result.getDischargeMoneyList().add(DoubleUtil.getToBigDecimal(dischargeMoney));
            //收益
            BigDecimal income = DoubleUtil.getToBigDecimal(dischargeMoney.subtract(chargeMoney));
            result.getIncomeList().add(income);
            historyIncome = historyIncome.add(income);
            result.getTotalIncomeList().add(DoubleUtil.getToBigDecimal(historyIncome));
        }
//        long handleEndTime = System.currentTimeMillis();
//        System.out.println("处理数据解析多少毫秒: " + (handleEndTime - handleStartTime) + " 毫秒");
        return ResponseResult.ok(result);
    }

    @Override
    public ResponseResult<StorageKwhIncomeDto> countStorageKwhIncome(StorageCountVo storageCountVo) {
        //返回的对象
        StorageKwhIncomeDto result = new StorageKwhIncomeDto();

        //校验入参
        if (isValidStorageCount(storageCountVo)) {
            return ResponseResult.paramError(ResponseResult.PARAM_ERROR);
        }

        String dataId = storageCountVo.getDataId();
        Integer dateType = storageCountVo.getDateType();
        String startDate = storageCountVo.getStartDate();
        String endDate = storageCountVo.getEndDate();

        //获取多个查询设备id
        String siteId = null;
        Set<String> deviceIds = Sets.newHashSet();
        if (storageCountVo.getQueryType() == 1) { //站点id
            siteId = dataId;
            //根据站点id查询储能系统
            SiteInfoDto siteInfo = deviceService.findSiteBasicInfoByIds(Collections.singletonList(dataId))
                    .getData().get(dataId);
            if (siteInfo != null && CollectionUtils.isNotEmpty(siteInfo.getSiteScenarioTypeDtos())) {
                //根据多个储能系统id查询并网表数据
                List<String> systemIds = siteInfo.getSiteScenarioTypeDtos().stream()
                        .filter(s -> Objects.equals(s.getScenarioType(), 2))
                        .map(SiteScenarioTypeDto::getId).collect(Collectors.toList());
                deviceIds = deviceService.findDeviceInfoByParentIds(systemIds).getData().values().stream()
                        .flatMap(s -> s.stream().filter(d ->
                                Objects.equals(d.getTypeId(), "38"))).map(DeviceBasicInfoDto::getId)
                        .collect(Collectors.toSet());
            }
        }
        if (storageCountVo.getQueryType() == 2) { //设备id
            deviceIds.add(storageCountVo.getDataId());
            DeviceBasicInfoDto deviceInfo = deviceService.findDeviceBasicInfoByIds(Collections.singletonList(dataId))
                    .getData().get(dataId);
            if (deviceInfo != null && StringUtil.isNotEmpty(deviceInfo.getSiteId())) {
                siteId = deviceInfo.getSiteId();
            }
        }

        if (StringUtil.isEmpty(siteId) || CollectionUtils.isEmpty(deviceIds)) {
            return ResponseResult.ok(result);
        }

        //1.获取日期列表
        List<String> dateList = DateUtil.getDateBetween(dateType, startDate, endDate);

        //根据站点id查询储能购电电价和售电电价
        //日期 -> (分时段(每半个小时) -> 电价类型实体类)
        Map<String, Map<String, ElectTypeDto>> purchaseElectMap = Maps.newHashMap();//购电电价
        Map<String, Map<String, ElectTypeDto>> saleElectMap = Maps.newHashMap();//售电电价
        //根据站点id查询站点电价
        List<ElectConfigEntity> electConfigList = electConfigDao.findElectConfigList(siteId, Arrays.asList(4, 5), startDate, endDate);
        if (CollectionUtils.isNotEmpty(electConfigList)) {
            //根据多个电站配置id查询电价
            Set<String> electConfigIds = electConfigList.stream().map(ElectConfigEntity::getId).collect(Collectors.toSet());
            Map<String, Map<String, ElectTypeDto>> electTimeFrameMap = TogetherCommonUtil.buildHalfHourMapByConfigId(electTimeFrameDao.findAllByElectConfigIdIn(electConfigIds));
            //对数据进行组装
            List<String> dateConfigList = getDateBetween(1, startDate, endDate);
            electConfigList.stream().collect(Collectors.groupingBy(ElectConfigEntity::getModuleType))
                    .forEach((moduleType, electConfigs) -> {
                        for (ElectConfigEntity electConfig : electConfigs) {
                            //电价配置 (半小时时段 -> 电费)
                            Map<String, ElectTypeDto> timeFrameMap = electTimeFrameMap.get(electConfig.getId());
                            if (MapUtils.isNotEmpty(timeFrameMap)) {
                                //过滤日期范围内的日期
                                List<String> electDateList = DateUtil.getDateBetween(1, electConfig.getStartDate(), electConfig.getEndDate());
                                List<String> electFiletrDateList = dateConfigList.stream().filter(electDateList::contains).collect(Collectors.toList());
                                for (String date : electFiletrDateList) {
                                    if (moduleType == 5) { //储能购电电价
                                        purchaseElectMap.put(date, timeFrameMap);
                                    }
                                    if (moduleType == 4) { //储能售电电价
                                        saleElectMap.put(date, timeFrameMap);
                                    }
                                }
                            }
                        }
                    });
        }

        //根据多个设备id和日期查询储能系统下面的电表功能点历史数据
        DeviceHistoryQueryVo deviceQueryVo = getDeviceHistoryQueryVo(deviceIds, startDate, endDate);
        //获取每天的电量
        List<NodeDifHistoryDto> nodeDifHistoryList = dataService.findNodeDifHistoryListFeign(deviceQueryVo).getData().values()
                .stream().flatMap(s -> s.values().stream().flatMap(Collection::stream))
                .filter(c -> StringUtil.isNotEmpty(c.getFirstDateTime())).collect(Collectors.toList());
        //日期(年月日)->(时分秒 ->(功能点标识 -> 电量))
        Map<String, Map<String, Map<String, Double>>> dayTimeQtMap = Maps.newHashMap();
        if (CollectionUtils.isNotEmpty(nodeDifHistoryList)) {
            dayTimeQtMap = nodeDifHistoryList.stream()
                    .collect(Collectors.groupingBy(
                            // 第一层分组：提取年月日
                            c -> c.getFirstDateTime().substring(0, 10),
                            Collectors.groupingBy(
                                    // 第二层分组：提取时分
                                    c -> c.getFirstDateTime().substring(11, 16),
                                    Collectors.groupingBy(
                                            // 第三层分组：根据功能点标识 functionLogo 分组
                                            NodeDifHistoryDto::getFunctionLogo,
                                            // 终端操作：对 dataValue 进行求和
                                            Collectors.summingDouble(c -> DoubleUtil.getObjSub(c.getFirstDataValue(), c.getLastDataValue()))
                                    )
                            )
                    ));
        }

        //获取每天的尖峰平谷成本和收入
        Map<String, Map<String, BigDecimal>> dayCostMap = getStorageCostMap(startDate, endDate, dayTimeQtMap, purchaseElectMap, saleElectMap);

        //对数据进行组装
        for (String date : dateList) {
            result.getDateList().add(date);
            Map<String, Double> qtMap = Maps.newHashMap();
            List<Map<String, BigDecimal>> costList = Lists.newArrayList();
            if (dateType == 1) { //日
                if (dayTimeQtMap.containsKey(date)) {
                    qtMap = dayTimeQtMap.get(date).values().stream().flatMap(d -> d.entrySet().stream())
                            .collect(Collectors.groupingBy(Map.Entry::getKey, Collectors.summingDouble(Map.Entry::getValue)));
                }
                if (dayCostMap.containsKey(date)) {
                    costList.add(dayCostMap.get(date));
                }
            } else {
                qtMap = dayTimeQtMap.entrySet().stream().filter(s -> s.getKey().contains(date))
                        .flatMap(s -> s.getValue().entrySet().stream()
                                .flatMap(d -> d.getValue().entrySet().stream()))
                        .collect(Collectors.groupingBy(Map.Entry::getKey, Collectors.summingDouble(Map.Entry::getValue)));
                costList.addAll(dayCostMap.entrySet().stream().filter(s -> s.getKey().contains(date))
                        .map(Map.Entry::getValue).collect(Collectors.toList()));
            }
            if (MapUtils.isEmpty(qtMap) && CollectionUtils.isEmpty(costList)) {
                result.getKwhIncomeList().add(BigDecimal.ZERO);
                continue;
            }

            //充电电量(总)
            Double chargeQt = 0.0;
            //放电电量(总)
            Double dischargeQt = 0.0;

            if (qtMap.containsKey(FunctionLogoParamVo.TOTAL_POSITIVE_ACTIVE_ENERGY)) {
                chargeQt = qtMap.getOrDefault(FunctionLogoParamVo.TOTAL_POSITIVE_ACTIVE_ENERGY, 0.0);
            }
            if (qtMap.containsKey(FunctionLogoParamVo.TOTAL_INVERSE_ACTIVE_ENERGY)) {
                dischargeQt = qtMap.getOrDefault(FunctionLogoParamVo.TOTAL_INVERSE_ACTIVE_ENERGY, 0.0);
            }

            //充电成本(总)
            BigDecimal chargeMoney = BigDecimal.ZERO;
            //售电收入(总)
            BigDecimal dischargeMoney = BigDecimal.ZERO;
            for (Map<String, BigDecimal> costMap : costList) {
                //获取充电成本(尖峰平谷深)
                if (costMap.containsKey(FunctionLogoParamVo.TOP_SUP_KWH)) { //尖
                    BigDecimal cost = costMap.get(FunctionLogoParamVo.TOP_SUP_KWH);
                    chargeMoney = chargeMoney.add(cost);
                }
                if (costMap.containsKey(FunctionLogoParamVo.PEAK_SUP_KWH)) { //峰
                    BigDecimal cost = costMap.get(FunctionLogoParamVo.PEAK_SUP_KWH);
                    chargeMoney = chargeMoney.add(cost);
                }
                if (costMap.containsKey(FunctionLogoParamVo.PLAIN_SUP_KWH)) { //平
                    BigDecimal cost = costMap.get(FunctionLogoParamVo.PLAIN_SUP_KWH);
                    chargeMoney = chargeMoney.add(cost);
                }
                if (costMap.containsKey(FunctionLogoParamVo.VALLEY_SUP_KWH)) { //谷
                    BigDecimal cost = costMap.get(FunctionLogoParamVo.VALLEY_SUP_KWH);
                    chargeMoney = chargeMoney.add(cost);
                }
                if (costMap.containsKey(FunctionLogoParamVo.DEEP_SUP_KWH)) { //深谷
                    BigDecimal cost = costMap.get(FunctionLogoParamVo.DEEP_SUP_KWH);
                    chargeMoney = chargeMoney.add(cost);
                }
                //获取售电收入(尖峰平谷深)
                if (costMap.containsKey(FunctionLogoParamVo.TOP_REV_KWH)) { //尖
                    BigDecimal cost = costMap.get(FunctionLogoParamVo.TOP_REV_KWH);
                    dischargeMoney = dischargeMoney.add(cost);
                }
                if (costMap.containsKey(FunctionLogoParamVo.PEAK_REV_KWH)) { //峰
                    BigDecimal cost = costMap.get(FunctionLogoParamVo.PEAK_REV_KWH);
                    dischargeMoney = dischargeMoney.add(cost);
                }
                if (costMap.containsKey(FunctionLogoParamVo.PLAIN_REV_KWH)) { //平
                    BigDecimal cost = costMap.get(FunctionLogoParamVo.PLAIN_REV_KWH);
                    dischargeMoney = dischargeMoney.add(cost);
                }
                if (costMap.containsKey(FunctionLogoParamVo.VALLEY_REV_KWH)) { //谷
                    BigDecimal cost = costMap.get(FunctionLogoParamVo.VALLEY_REV_KWH);
                    dischargeMoney = dischargeMoney.add(cost);
                }
                if (costMap.containsKey(FunctionLogoParamVo.DEEP_REV_KWH)) { //深谷
                    BigDecimal cost = costMap.get(FunctionLogoParamVo.DEEP_REV_KWH);
                    dischargeMoney = dischargeMoney.add(cost);
                }
            }
            //充电量(总)
            result.setChargeQt(result.getChargeQt() + chargeQt);
            //充电成本(总)
            result.setChargeMoney(result.getChargeMoney().add(chargeMoney));
            //放电量(总)
            result.setDischargeQt(result.getDischargeQt() + dischargeQt);
            //放电收入(总)
            result.setDischargeMoney(result.getDischargeMoney().add(dischargeMoney));
            //合计收益 放电收入-充电成本
            BigDecimal income = DoubleUtil.getToBigDecimal(dischargeMoney.subtract(chargeMoney));
            result.setTotalIncome(result.getTotalIncome().add(income));

            //度电收益曲线
            if (dischargeQt != 0.0) {
                result.getKwhIncomeList().add(income.divide(BigDecimal.valueOf(dischargeQt), 2, RoundingMode.HALF_UP));
            } else {
                result.getKwhIncomeList().add(BigDecimal.ZERO);
            }
        }

        //合计度电收益（合计收益 / 合计放电量）
        if (result.getDischargeQt() != 0) {
            result.setTotalKwhIncome(result.getTotalIncome().divide(BigDecimal.valueOf(result.getDischargeQt()), 2, RoundingMode.HALF_UP));
        }
        return ResponseResult.ok(result);
    }

    /**
     * 获取储能收益数据
     *
     * @param startDate        开始日期
     * @param endDate          结束日期
     * @param dayQtMap         每天的电量,日期(年月日)->(时分 ->(功能点标识 -> 电量))
     * @param purchaseElectMap 购电电价 日期 -> (分时段(每半个小时) -> 电价类型实体类)
     * @param saleElectMap     售电电价 日期 -> (分时段(每半个小时) -> 电价类型实体类)
     * @return 收益
     */
    private BigDecimal getStorageHistoryCost(String startDate, String endDate,
                                             Map<String, Map<String, Map<String, Double>>> dayQtMap,
                                             Map<String, Map<String, ElectTypeDto>> purchaseElectMap,
                                             Map<String, Map<String, ElectTypeDto>> saleElectMap) {
        List<String> dateList = getDateBetween(1, startDate, endDate);
        BigDecimal totalProfit = BigDecimal.ZERO;

        for (String date : dateList) {
            Map<String, Map<String, Double>> timeFunctionLogoMap = dayQtMap.get(date);
            if (timeFunctionLogoMap == null) {
                continue;
            }

            Map<String, ElectTypeDto> purchasePriceMap = purchaseElectMap.getOrDefault(date, Maps.newHashMap());
            Map<String, ElectTypeDto> salePriceMap = saleElectMap.getOrDefault(date, Maps.newHashMap());

            // 按时间槽累加减购电成本和售电收入
            for (Map.Entry<String, Map<String, Double>> timeEntry : timeFunctionLogoMap.entrySet()) {
                String timeSlot = timeEntry.getKey();
                Map<String, Double> functionLogoMap = timeEntry.getValue();

                // 计算购电成本：正向有功电量 * 购电电价
                BigDecimal chargeMoney = BigDecimal.ZERO;
                Double positiveEnergy = functionLogoMap.get(FunctionLogoParamVo.TOTAL_POSITIVE_ACTIVE_ENERGY);
                if (positiveEnergy != null && positiveEnergy > 0) {
                    ElectTypeDto purchasePriceDto = purchasePriceMap.get(timeSlot);
                    if (purchasePriceDto != null && purchasePriceDto.getElectMoney() != null) {
                        chargeMoney = BigDecimal.valueOf(positiveEnergy).multiply(purchasePriceDto.getElectMoney());
                    }
                }

                // 计算售电收入：反向有功电量 * 售电电价
                BigDecimal dischargeMoney = BigDecimal.ZERO;
                Double inverseEnergy = functionLogoMap.get(FunctionLogoParamVo.TOTAL_INVERSE_ACTIVE_ENERGY);
                if (inverseEnergy != null && inverseEnergy > 0) {
                    ElectTypeDto salePriceDto = salePriceMap.get(timeSlot);
                    if (salePriceDto != null && salePriceDto.getElectMoney() != null) {
                        dischargeMoney = BigDecimal.valueOf(inverseEnergy).multiply(salePriceDto.getElectMoney());
                    }
                }

                // 收益 = 售电收入 - 购电成本
                totalProfit = totalProfit.add(dischargeMoney.subtract(chargeMoney));
            }
        }

        return totalProfit;
    }


    /**
     * 获取储能收益数据
     *
     * @param startDate        开始日期
     * @param endDate          结束日期
     * @param dayQtMap         每天的电量,日期(年月日)->(时分秒 ->(功能点标识 -> 电量))
     * @param purchaseElectMap 购电电价 日期 -> (分时段(每半个小时) -> 电价类型实体类)
     * @param saleElectMap     售电电价 日期 -> (分时段(每半个小时) -> 电价类型实体类)
     * @return 充电成本(尖峰平谷深的收益)和放电收益(尖峰平谷深的收益) 日期年月日 -> (尖峰平谷标识 -> 收益数据)
     */
    private Map<String, Map<String, BigDecimal>> getStorageCostMap(String startDate, String endDate,
                                                                   Map<String, Map<String, Map<String, Double>>> dayQtMap,
                                                                   Map<String, Map<String, ElectTypeDto>> purchaseElectMap,
                                                                   Map<String, Map<String, ElectTypeDto>> saleElectMap) {
        Map<String, Map<String, BigDecimal>> resultMap = Maps.newHashMap();
        List<String> dateList = getDateBetween(1, startDate, endDate);

        for (String date : dateList) {
            Map<String, Map<String, Double>> timeFunctionLogoMap = dayQtMap.get(date);
            if (timeFunctionLogoMap == null || timeFunctionLogoMap.isEmpty()) {
                continue;
            }

            Map<String, ElectTypeDto> purchasePriceMap = purchaseElectMap.getOrDefault(date, Maps.newHashMap());
            Map<String, ElectTypeDto> salePriceMap = saleElectMap.getOrDefault(date, Maps.newHashMap());

            Map<String, BigDecimal> moneyLogoMap = Maps.newHashMap();

            // 按时间槽遍历 (每半个小时)
            for (Map.Entry<String, Map<String, Double>> timeEntry : timeFunctionLogoMap.entrySet()) {
                String timeSlot = timeEntry.getKey();
                Map<String, Double> functionLogoMap = timeEntry.getValue();

                // 获取该时段的电价类型
                ElectTypeDto purchasePriceDto = purchasePriceMap.get(timeSlot);
                ElectTypeDto salePriceDto = salePriceMap.get(timeSlot);

                // 1. 计算购电成本：正向总有功电量 * 该时段购电电价，计入对应时段类型
                Double positiveEnergy = functionLogoMap.get(FunctionLogoParamVo.TOTAL_POSITIVE_ACTIVE_ENERGY);
                if (positiveEnergy != null && positiveEnergy > 0 && purchasePriceDto != null
                        && purchasePriceDto.getPeriodType() != null && purchasePriceDto.getElectMoney() != null) {
                    BigDecimal cost = BigDecimal.valueOf(positiveEnergy).multiply(purchasePriceDto.getElectMoney());
                    String supLogo = getPeriodTypeLogo(purchasePriceDto.getPeriodType(), true);
                    if (supLogo != null) {
                        moneyLogoMap.merge(supLogo, cost, BigDecimal::add);
                    }
                }

                // 2. 计算售电收入：反向总有功电量 * 该时段售电电价，计入对应时段类型
                Double inverseEnergy = functionLogoMap.get(FunctionLogoParamVo.TOTAL_INVERSE_ACTIVE_ENERGY);
                if (inverseEnergy != null && inverseEnergy > 0 && salePriceDto != null
                        && salePriceDto.getPeriodType() != null && salePriceDto.getElectMoney() != null) {
                    BigDecimal income = BigDecimal.valueOf(inverseEnergy).multiply(salePriceDto.getElectMoney());
                    String revLogo = getPeriodTypeLogo(salePriceDto.getPeriodType(), false);
                    if (revLogo != null) {
                        moneyLogoMap.merge(revLogo, income, BigDecimal::add);
                    }
                }
            }

            if (!moneyLogoMap.isEmpty()) {
                resultMap.put(date, moneyLogoMap);
            }
        }

        return resultMap;
    }

    /**
     * 根据时段类型获取对应的功能标识
     *
     * @param periodType 时段类型：1-尖 2-峰 3-平 4-谷 5-深谷
     * @param isCharge   true-充电 (正向), false-放电 (反向)
     * @return 功能标识
     */
    private String getPeriodTypeLogo(Integer periodType, boolean isCharge) {
        if (periodType == null) {
            return null;
        }
        if (isCharge) {
            switch (periodType) {
                case 1:
                    return FunctionLogoParamVo.TOP_SUP_KWH;
                case 2:
                    return FunctionLogoParamVo.PEAK_SUP_KWH;
                case 3:
                    return FunctionLogoParamVo.PLAIN_SUP_KWH;
                case 4:
                    return FunctionLogoParamVo.VALLEY_SUP_KWH;
                case 5:
                    return FunctionLogoParamVo.DEEP_SUP_KWH;
                default:
                    return null;
            }
        } else {
            switch (periodType) {
                case 1:
                    return FunctionLogoParamVo.TOP_REV_KWH;
                case 2:
                    return FunctionLogoParamVo.PEAK_REV_KWH;
                case 3:
                    return FunctionLogoParamVo.PLAIN_REV_KWH;
                case 4:
                    return FunctionLogoParamVo.VALLEY_REV_KWH;
                case 5:
                    return FunctionLogoParamVo.DEEP_REV_KWH;
                default:
                    return null;
            }
        }
    }


}
