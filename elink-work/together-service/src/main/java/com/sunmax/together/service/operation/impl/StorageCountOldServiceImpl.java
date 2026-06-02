//package com.sunmax.together.service.operation.impl;
//
//import com.alibaba.fastjson.JSON;
//import com.alibaba.fastjson.JSONObject;
//import com.google.common.collect.Maps;
//import com.google.common.collect.Sets;
//import com.sunmax.common.dto.data.NodeDifHistoryDto;
//import com.sunmax.common.dto.device.DeviceBasicInfoDto;
//import com.sunmax.common.dto.device.SiteInfoDto;
//import com.sunmax.common.dto.device.SiteScenarioTypeDto;
//import com.sunmax.common.util.DateUtil;
//import com.sunmax.common.util.DoubleUtil;
//import com.sunmax.common.util.ResponseResult;
//import com.sunmax.common.util.StringUtil;
//import com.sunmax.common.vo.FunctionLogoParamVo;
//import com.sunmax.common.vo.SiteFieldParamVo;
//import com.sunmax.common.vo.data.DeviceHistoryQueryVo;
//import com.sunmax.together.dao.asset.ElectConfigDao;
//import com.sunmax.together.dao.asset.ElectTimeFrameDao;
//import com.sunmax.together.dto.operation.storageCount.MeterListDto;
//import com.sunmax.together.dto.operation.storageCount.StorageIncomeCountDto;
//import com.sunmax.together.dto.operation.storageCount.StorageKwhIncomeDto;
//import com.sunmax.together.dto.operation.storageCount.StorageQtCountDto;
//import com.sunmax.together.entity.assets.ElectConfigEntity;
//import com.sunmax.together.entity.assets.ElectTimeFrameEntity;
//import com.sunmax.together.service.feign.DataService;
//import com.sunmax.together.service.feign.DeviceService;
//import com.sunmax.together.service.operation.StorageCountService;
//import com.sunmax.together.vo.operation.storageCount.StorageCountVo;
//import org.apache.commons.collections.CollectionUtils;
//import org.apache.commons.collections.MapUtils;
//import org.apache.commons.compress.utils.Lists;
//import org.jetbrains.annotations.NotNull;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.math.BigDecimal;
//import java.math.RoundingMode;
//import java.util.*;
//import java.util.stream.Collectors;
//
//import static com.sunmax.common.util.DateUtil.*;
//
//@Service
//public class StorageCountOldServiceImpl implements StorageCountService {
//
//    @Autowired
//    private DeviceService deviceService;
//
//    @Autowired
//    private DataService dataService;
//
//    @Autowired
//    private ElectConfigDao electConfigDao;
//
//    @Autowired
//    private ElectTimeFrameDao electTimeFrameDao;
//
//    private static @NotNull DeviceHistoryQueryVo getDeviceHistoryQueryVo(Set<String> deviceIds, String historyStartDate, String endDate) {
//        DeviceHistoryQueryVo deviceHistoryQueryVo = new DeviceHistoryQueryVo();
//        deviceHistoryQueryVo.setDeviceIds(deviceIds);
//        deviceHistoryQueryVo.setFunctionLogos(new HashSet<>(Arrays.asList(FunctionLogoParamVo.TOP_SUP_KWH,
//                FunctionLogoParamVo.PEAK_SUP_KWH, FunctionLogoParamVo.PLAIN_SUP_KWH, FunctionLogoParamVo.VALLEY_SUP_KWH,
//                FunctionLogoParamVo.DEEP_SUP_KWH, FunctionLogoParamVo.TOP_REV_KWH, FunctionLogoParamVo.PEAK_REV_KWH,
//                FunctionLogoParamVo.PLAIN_REV_KWH, FunctionLogoParamVo.VALLEY_REV_KWH, FunctionLogoParamVo.DEEP_REV_KWH)));
//        deviceHistoryQueryVo.setStartTime(DateUtil.getDayStart(historyStartDate));
//        deviceHistoryQueryVo.setEndTime(DateUtil.getDayEnd(endDate));
//        deviceHistoryQueryVo.setTimeInterval("1d");
//        return deviceHistoryQueryVo;
//    }
//
//    //校验入参参数
//    private static boolean isValidStorageCount(StorageCountVo storageCountVo) {
//        // 检查输入对象是否为null
//        if (storageCountVo == null) {
//            // 可以考虑添加日志记录这里的失败原因
//            return true;
//        }
//
//        // 定义需要校验的字段数组
//        Object[] fieldsToValidate = {
//                storageCountVo.getQueryType(),
//                storageCountVo.getDataId(),
//                storageCountVo.getDateType(),
//                storageCountVo.getStartDate(),
//                storageCountVo.getEndDate()
//        };
//
//        // 使用Apache Commons Lang3的StringUtils.isEmpty()进行联合检查
//        // 它同时处理了空和只含空格的字符串
//        return Arrays.stream(fieldsToValidate).anyMatch(StringUtil::isEmpty);
//    }
//
//    @Override
//    public ResponseResult<List<MeterListDto>> getMeterListBySiteId(String siteId) {
//        //返回的集合
//        List<MeterListDto> resultList = Lists.newArrayList();
//        //根据站点id查询储能系统
//        SiteInfoDto siteInfo = deviceService.findSiteBasicInfoByIds(Collections.singletonList(siteId))
//                .getData().get(siteId);
//        if (siteInfo != null && CollectionUtils.isNotEmpty(siteInfo.getSiteScenarioTypeDtos())) {
//            //根据多个储能系统id查询并网表数据
//            List<String> systemIds = siteInfo.getSiteScenarioTypeDtos().stream()
//                    .filter(s -> Objects.equals(s.getScenarioType(), 2))
//                    .map(SiteScenarioTypeDto::getId).collect(Collectors.toList());
//            resultList = deviceService.findDeviceInfoByParentIds(systemIds).getData().values().stream()
//                    .flatMap(s -> s.stream().filter(d ->
//                            Objects.equals(d.getTypeId(), "38"))).map(d -> {
//                        MeterListDto result = new MeterListDto();
//                        result.setId(d.getId());
//                        result.setDeviceName(d.getDeviceName());
//                        return result;
//                    }).collect(Collectors.toList());
//        }
//        return ResponseResult.ok(resultList);
//    }
//
//    @Override
//    public ResponseResult<StorageQtCountDto> countStorageQt(StorageCountVo storageCountVo) {
//
//        //返回的对象
//        StorageQtCountDto result = new StorageQtCountDto();
//
//        //校验入参
//        if (isValidStorageCount(storageCountVo)) {
//            return ResponseResult.paramError(ResponseResult.PARAM_ERROR);
//        }
//
//        //获取多个查询设备id
//        Set<String> deviceIds = Sets.newHashSet();
//        if (storageCountVo.getQueryType() == 1) { //站点id
//            //根据站点id查询储能系统
//            SiteInfoDto siteInfo = deviceService.findSiteBasicInfoByIds(Collections.singletonList(storageCountVo.getDataId()))
//                    .getData().get(storageCountVo.getDataId());
//            if (siteInfo != null && CollectionUtils.isNotEmpty(siteInfo.getSiteScenarioTypeDtos())) {
//                //根据多个储能系统id查询并网表数据
//                List<String> systemIds = siteInfo.getSiteScenarioTypeDtos().stream()
//                        .filter(s -> Objects.equals(s.getScenarioType(), 2))
//                        .map(SiteScenarioTypeDto::getId).collect(Collectors.toList());
//                deviceIds = deviceService.findDeviceInfoByParentIds(systemIds).getData().values().stream()
//                        .flatMap(s -> s.stream().filter(d ->
//                                Objects.equals(d.getTypeId(), "38"))).map(DeviceBasicInfoDto::getId)
//                        .collect(Collectors.toSet());
//            }
//        }
//        if (storageCountVo.getQueryType() == 2) { //设备id
//            deviceIds.add(storageCountVo.getDataId());
//        }
//        //校验是否存在设备
//        if (CollectionUtils.isEmpty(deviceIds)) {
//            return ResponseResult.ok(result);
//        }
//
//        //开始时间
//        String startTime = DateUtil.getDayStart(storageCountVo.getStartDate());
//        //结束时间
//        String endTime = DateUtil.getDayEnd(storageCountVo.getEndDate());
//        //数据时间间隔 s-秒 m-分钟 h-小时 d-天 n-月 y-年
//        String timeInterval = null;
//        //时间格式间隔 1-(yyyy-MM-dd HH:mm:ss) 2-(yyyy-MM-dd) 3-(yyyy-MM) 4-(yyyy) 5-(HH:mm:ss) 6-(HH:mm)
//        Integer formatInterval = null;
//        //日期类型 1-日 2-月 3-年
//        switch (storageCountVo.getDateType()) {
//            case 1:
//                timeInterval = "1d";
//                formatInterval = 2;
//                break;
//            case 2:
//                timeInterval = "1n";
//                formatInterval = 3;
//                break;
//            case 3:
//                timeInterval = "1y";
//                formatInterval = 4;
//                break;
//        }
//        Set<String> dateList = getDateFormatTimeList(startTime, endTime, timeInterval, formatInterval);
//        //根据多个设备id和日期查询电表功能点历史数据
//        DeviceHistoryQueryVo deviceHistoryQueryVo = new DeviceHistoryQueryVo();
//        deviceHistoryQueryVo.setDeviceIds(deviceIds);
//        deviceHistoryQueryVo.setFunctionLogos(new HashSet<>(Arrays.asList(FunctionLogoParamVo.TOTAL_POSITIVE_ACTIVE_ENERGY,
//                FunctionLogoParamVo.TOTAL_INVERSE_ACTIVE_ENERGY, FunctionLogoParamVo.TOP_SUP_KWH, FunctionLogoParamVo.PEAK_SUP_KWH,
//                FunctionLogoParamVo.PLAIN_SUP_KWH, FunctionLogoParamVo.VALLEY_SUP_KWH, FunctionLogoParamVo.DEEP_SUP_KWH,
//                FunctionLogoParamVo.TOP_REV_KWH, FunctionLogoParamVo.PEAK_REV_KWH, FunctionLogoParamVo.PLAIN_REV_KWH,
//                FunctionLogoParamVo.VALLEY_REV_KWH, FunctionLogoParamVo.DEEP_REV_KWH)));
//        deviceHistoryQueryVo.setStartTime(startTime);
//        deviceHistoryQueryVo.setEndTime(endTime);
//        deviceHistoryQueryVo.setTimeInterval(timeInterval);
//        Integer finalFormatInterval = formatInterval;
//        Map<String, List<NodeDifHistoryDto>> deviceHistoryMap = dataService.findNodeDifHistoryListFeign(deviceHistoryQueryVo).getData().values().stream()
//                .flatMap(s -> s.values().stream().flatMap(Collection::stream))
//                .filter(c -> StringUtil.isNotEmpty(c.getFirstDateTime()) && StringUtil.isNotEmpty(c.getFirstDataValue()) && StringUtil.isNotEmpty(c.getLastDataValue()))
//                .collect(Collectors.groupingBy(c -> {
//                    String dateFormat = format(strToDate(c.getFirstDateTime()), getFormatIntervalPattern(finalFormatInterval));
//                    if (StringUtil.isNotEmpty(dateFormat)) {
//                        return dateFormat;
//                    }
//                    return c.getFirstDateTime();
//                }));
//        //对数据进行组装
//        for (String date : dateList) {
//            result.getDateList().add(date);
//            if (deviceHistoryMap.containsKey(date)) {
//                Map<String, List<NodeDifHistoryDto>> functionDataMap = deviceHistoryMap.get(date).stream().collect(Collectors
//                        .groupingBy(NodeDifHistoryDto::getFunctionLogo));
//                if (functionDataMap.containsKey(FunctionLogoParamVo.TOTAL_POSITIVE_ACTIVE_ENERGY)) { //正向有功电量(总)
//                    double functionKwh = functionDataMap.get(FunctionLogoParamVo.TOTAL_POSITIVE_ACTIVE_ENERGY).stream().mapToDouble(d -> Double.parseDouble(String.valueOf(d.getLastDataValue()))
//                            - Double.parseDouble(String.valueOf(d.getFirstDataValue()))).sum();
//                    result.setChargeQt(DoubleUtil.getToDouble(result.getChargeQt() + functionKwh));
//                }
//                if (functionDataMap.containsKey(FunctionLogoParamVo.TOTAL_INVERSE_ACTIVE_ENERGY)) { //反向有功电量(总)
//                    double functionKwh = functionDataMap.get(FunctionLogoParamVo.TOTAL_INVERSE_ACTIVE_ENERGY).stream().mapToDouble(d -> Double.parseDouble(String.valueOf(d.getLastDataValue()))
//                            - Double.parseDouble(String.valueOf(d.getFirstDataValue()))).sum();
//                    result.setDischargeQt(DoubleUtil.getToDouble(result.getDischargeQt() + functionKwh));
//                }
//                if (functionDataMap.containsKey(FunctionLogoParamVo.TOP_SUP_KWH)) { //正向有功电量(尖)
//                    double functionKwh = functionDataMap.get(FunctionLogoParamVo.TOP_SUP_KWH).stream().mapToDouble(d -> Double.parseDouble(String.valueOf(d.getLastDataValue()))
//                            - Double.parseDouble(String.valueOf(d.getFirstDataValue()))).sum();
//                    result.getTopChargeQtList().add(DoubleUtil.getToDouble(functionKwh));
//                    result.setTopChargeQt(DoubleUtil.getToDouble(result.getTopChargeQt() + functionKwh));
//                } else {
//                    result.getTopChargeQtList().add(0.0);
//                }
//                if (functionDataMap.containsKey(FunctionLogoParamVo.PEAK_SUP_KWH)) { //正向有功电量(峰)
//                    double functionKwh = functionDataMap.get(FunctionLogoParamVo.PEAK_SUP_KWH).stream().mapToDouble(d -> Double.parseDouble(String.valueOf(d.getLastDataValue()))
//                            - Double.parseDouble(String.valueOf(d.getFirstDataValue()))).sum();
//                    result.getPeakChargeQtList().add(DoubleUtil.getToDouble(functionKwh));
//                    result.setPeakChargeQt(DoubleUtil.getToDouble(result.getPeakChargeQt() + functionKwh));
//                } else {
//                    result.getPeakChargeQtList().add(0.0);
//                }
//                if (functionDataMap.containsKey(FunctionLogoParamVo.PLAIN_SUP_KWH)) { //正向有功电量(平)
//                    double functionKwh = functionDataMap.get(FunctionLogoParamVo.PLAIN_SUP_KWH).stream().mapToDouble(d -> Double.parseDouble(String.valueOf(d.getLastDataValue()))
//                            - Double.parseDouble(String.valueOf(d.getFirstDataValue()))).sum();
//                    result.getPlainChargeQtList().add(DoubleUtil.getToDouble(functionKwh));
//                    result.setPlainChargeQt(DoubleUtil.getToDouble(result.getPlainChargeQt() + functionKwh));
//                } else {
//                    result.getPlainChargeQtList().add(0.0);
//                }
//                if (functionDataMap.containsKey(FunctionLogoParamVo.VALLEY_SUP_KWH)) { //正向有功电量(谷)
//                    double functionKwh = functionDataMap.get(FunctionLogoParamVo.VALLEY_SUP_KWH).stream().mapToDouble(d -> Double.parseDouble(String.valueOf(d.getLastDataValue()))
//                            - Double.parseDouble(String.valueOf(d.getFirstDataValue()))).sum();
//                    result.getValleyChargeQtList().add(DoubleUtil.getToDouble(functionKwh));
//                    result.setValleyChargeQt(DoubleUtil.getToDouble(result.getValleyChargeQt() + functionKwh));
//                } else {
//                    result.getValleyChargeQtList().add(0.0);
//                }
//                if (functionDataMap.containsKey(FunctionLogoParamVo.DEEP_SUP_KWH)) { //正向有功电量(深谷)
//                    double functionKwh = functionDataMap.get(FunctionLogoParamVo.DEEP_SUP_KWH).stream().mapToDouble(d -> Double.parseDouble(String.valueOf(d.getLastDataValue()))
//                            - Double.parseDouble(String.valueOf(d.getFirstDataValue()))).sum();
//                    result.getDeepChargeQtList().add(DoubleUtil.getToDouble(functionKwh));
//                    result.setDeepChargeQt(DoubleUtil.getToDouble(result.getDeepChargeQt() + functionKwh));
//                } else {
//                    result.getDeepChargeQtList().add(0.0);
//                }
//                if (functionDataMap.containsKey(FunctionLogoParamVo.TOP_REV_KWH)) { //反向有功电量(尖)
//                    double functionKwh = functionDataMap.get(FunctionLogoParamVo.TOP_REV_KWH).stream().mapToDouble(d -> Double.parseDouble(String.valueOf(d.getLastDataValue()))
//                            - Double.parseDouble(String.valueOf(d.getFirstDataValue()))).sum();
//                    result.getTopDischargeQtList().add(DoubleUtil.getToDouble(functionKwh));
//                    result.setTopDischargeQt(DoubleUtil.getToDouble(result.getTopDischargeQt() + functionKwh));
//                } else {
//                    result.getTopDischargeQtList().add(0.0);
//                }
//                if (functionDataMap.containsKey(FunctionLogoParamVo.PEAK_REV_KWH)) { //反向有功电量(峰)
//                    double functionKwh = functionDataMap.get(FunctionLogoParamVo.PEAK_REV_KWH).stream().mapToDouble(d -> Double.parseDouble(String.valueOf(d.getLastDataValue()))
//                            - Double.parseDouble(String.valueOf(d.getFirstDataValue()))).sum();
//                    result.getPeakDischargeQtList().add(DoubleUtil.getToDouble(functionKwh));
//                    result.setPeakDischargeQt(DoubleUtil.getToDouble(result.getPeakDischargeQt() + functionKwh));
//                } else {
//                    result.getPeakDischargeQtList().add(0.0);
//                }
//                if (functionDataMap.containsKey(FunctionLogoParamVo.PLAIN_REV_KWH)) { //反向有功电量(平)
//                    double functionKwh = functionDataMap.get(FunctionLogoParamVo.PLAIN_REV_KWH).stream().mapToDouble(d -> Double.parseDouble(String.valueOf(d.getLastDataValue()))
//                            - Double.parseDouble(String.valueOf(d.getFirstDataValue()))).sum();
//                    result.getPlainDischargeQtList().add(DoubleUtil.getToDouble(functionKwh));
//                    result.setPlainDischargeQt(DoubleUtil.getToDouble(result.getPlainDischargeQt() + functionKwh));
//                } else {
//                    result.getPlainDischargeQtList().add(0.0);
//                }
//                if (functionDataMap.containsKey(FunctionLogoParamVo.VALLEY_REV_KWH)) { //反向有功电量(谷)
//                    double functionKwh = functionDataMap.get(FunctionLogoParamVo.VALLEY_REV_KWH).stream().mapToDouble(d -> Double.parseDouble(String.valueOf(d.getLastDataValue()))
//                            - Double.parseDouble(String.valueOf(d.getFirstDataValue()))).sum();
//                    result.getValleyDischargeQtList().add(DoubleUtil.getToDouble(functionKwh));
//                    result.setValleyDischargeQt(DoubleUtil.getToDouble(result.getValleyDischargeQt() + functionKwh));
//                } else {
//                    result.getValleyDischargeQtList().add(0.0);
//                }
//                if (functionDataMap.containsKey(FunctionLogoParamVo.DEEP_REV_KWH)) { //反向有功电量(深谷)
//                    double functionKwh = functionDataMap.get(FunctionLogoParamVo.DEEP_REV_KWH).stream().mapToDouble(d -> Double.parseDouble(String.valueOf(d.getLastDataValue()))
//                            - Double.parseDouble(String.valueOf(d.getFirstDataValue()))).sum();
//                    result.getDeepDischargeQtList().add(DoubleUtil.getToDouble(functionKwh));
//                    result.setDeepDischargeQt(DoubleUtil.getToDouble(result.getDeepDischargeQt() + functionKwh));
//                } else {
//                    result.getDeepDischargeQtList().add(0.0);
//                }
//            } else {
//                result.getTopChargeQtList().add(0.0);
//                result.getPeakChargeQtList().add(0.0);
//                result.getPlainChargeQtList().add(0.0);
//                result.getValleyChargeQtList().add(0.0);
//                result.getDeepChargeQtList().add(0.0);
//                result.getTopDischargeQtList().add(0.0);
//                result.getPeakDischargeQtList().add(0.0);
//                result.getPlainDischargeQtList().add(0.0);
//                result.getValleyDischargeQtList().add(0.0);
//                result.getDeepDischargeQtList().add(0.0);
//            }
//        }
//        return ResponseResult.ok(result);
//    }
//
//    @Override
//    public ResponseResult<StorageIncomeCountDto> countStorageIncome(StorageCountVo storageCountVo) {
//
//        //返回的对象
//        StorageIncomeCountDto result = new StorageIncomeCountDto();
//
//        //校验入参
//        if (isValidStorageCount(storageCountVo)) {
//            return ResponseResult.paramError(ResponseResult.PARAM_ERROR);
//        }
//
//        String dataId = storageCountVo.getDataId();
//        Integer dateType = storageCountVo.getDateType();
//        String startDate = storageCountVo.getStartDate();
//        String endDate = storageCountVo.getEndDate();
//
//
//        //1.获取日期列表
//        List<String> dateList = DateUtil.getDateBetween(dateType, startDate, endDate);
//
//        //2.获取站点id,多个设备id,开始日期(投运日期或建站创建日期)和结束日期(当天)
//        String siteId = null;
//        Set<String> deviceIds = Sets.newHashSet();
//        String historyStartDate = null;
//        String historyEndDate = DateUtil.localDateToStr(DateUtil.strToLocalDate(startDate).minusDays(1));
//        if (storageCountVo.getQueryType() == 1) { //站点id
//            siteId = dataId;
//            //根据站点id查询站点数据
//            SiteInfoDto siteInfo = deviceService.findSiteBasicInfoByIds(Collections.singletonList(dataId)).getData().get(dataId);
//            if (siteInfo != null && CollectionUtils.isNotEmpty(siteInfo.getSiteScenarioTypeDtos())) {
//                //根据多个储能系统id查询并网表数据
//                List<String> systemIds = siteInfo.getSiteScenarioTypeDtos().stream()
//                        .filter(s -> Objects.equals(s.getScenarioType(), 2))
//                        .map(SiteScenarioTypeDto::getId).collect(Collectors.toList());
//                deviceIds = deviceService.findDeviceInfoByParentIds(systemIds).getData().values().stream()
//                        .flatMap(s -> s.stream().filter(d ->
//                                Objects.equals(d.getTypeId(), "38"))).map(DeviceBasicInfoDto::getId)
//                        .collect(Collectors.toSet());
//                if (StringUtil.isNotEmpty(siteInfo.getSiteReadwriteObject())) {
//                    JSONObject jsonObject = JSON.parseObject(siteInfo.getSiteReadwriteObject());
//                    if (jsonObject.containsKey(SiteFieldParamVo.OFFICIAL_RUN_TIME)) {
//                        historyStartDate = jsonObject.getString(SiteFieldParamVo.OFFICIAL_RUN_TIME);
//                    }
//                }
//                if (StringUtil.isEmpty(historyStartDate) && StringUtil.isNotEmpty(siteInfo.getCreateTime())) {
//                    historyStartDate = DateUtil.localDateToStr(siteInfo.getCreateTime().toLocalDate());
//                }
//            }
//        }
//        if (storageCountVo.getQueryType() == 2) { //设备id
//            deviceIds.add(dataId);
//            DeviceBasicInfoDto deviceInfo = deviceService.findDeviceBasicInfoByIds(Collections.singletonList(dataId))
//                    .getData().get(dataId);
//            if (deviceInfo != null) {
//                siteId = deviceInfo.getSiteId();
//                if (StringUtil.isEmpty(historyStartDate) && StringUtil.isNotEmpty(deviceInfo.getCreateTime())) {
//                    historyStartDate = DateUtil.localDateToStr(deviceInfo.getCreateTime().toLocalDate());
//                }
//            }
//        }
//
//        if (StringUtil.isEmpty(siteId) || CollectionUtils.isEmpty(deviceIds) || StringUtil.isEmpty(historyStartDate)) {
//            return ResponseResult.ok(result);
//        }
//
//        //根据站点id查询储能购电电价和售电电价
//        //日期 -> (时段类型 -> 电费)
//        Map<String, Map<Integer, BigDecimal>> purchaseElectMap = Maps.newHashMap();//购电电价
//        Map<String, Map<Integer, BigDecimal>> saleElectMap = Maps.newHashMap();//售电电价
//        Map<Integer, List<ElectConfigEntity>> electConfigMap = electConfigDao.findElectConfigList(siteId, Arrays.asList(4, 5), historyStartDate, endDate).stream()
//                .collect(Collectors.groupingBy(ElectConfigEntity::getModuleType));
//        if (MapUtils.isEmpty(electConfigMap)) {
//            return ResponseResult.ok(result);
//        }
//        //根据多个电站配置id查询尖峰平谷电价(电价配置id -> (时段类型 -> 电费))
//        Set<String> electConfigIds = electConfigMap.values().stream().flatMap(e -> e.stream().map(ElectConfigEntity::getId)).collect(Collectors.toSet());
//        Map<String, Map<Integer, BigDecimal>> electTimeFrameMap = electTimeFrameDao.findAllByElectConfigIdIn(electConfigIds).stream()
//                .collect(Collectors.groupingBy(ElectTimeFrameEntity::getElectConfigId, Collectors.toMap(ElectTimeFrameEntity::getPeriodType,
//                        ElectTimeFrameEntity::getElectMoney, (k1, k2) -> k2)));
//        //对数据进行组装
//        List<String> dateConfigList = getDateBetween(1, historyStartDate, endDate);
//        electConfigMap.forEach((moduleType, electConfigList) -> electConfigList.forEach(electConfig -> {
//            Map<Integer, BigDecimal> periodTypeMap = Maps.newHashMap();
//            if (electTimeFrameMap.containsKey(electConfig.getId())) {
//                periodTypeMap = electTimeFrameMap.get(electConfig.getId());
//            }
//            //过滤日期范围内的日期
//            List<String> electDateList = DateUtil.getDateBetween(1, electConfig.getStartDate(), electConfig.getEndDate());
//            List<String> electFiletrDateList = dateConfigList.stream().filter(electDateList::contains).collect(Collectors.toList());
//            for (String date : electFiletrDateList) {
//                if (moduleType == 5) { //储能购电电价
//                    purchaseElectMap.put(date, periodTypeMap);
//                }
//                if (moduleType == 4) { //储能售电电价
//                    saleElectMap.put(date, periodTypeMap);
//                }
//            }
//        }));
//
//        //根据多个设备id和日期查询储能系统下面的电表功能点历史数据
//        DeviceHistoryQueryVo deviceQueryVo = getDeviceHistoryQueryVo(deviceIds, historyStartDate, endDate);
//        //获取每天的电量,日期(年月日)->(功能点标识->电量)
//        Map<String, Map<String, Double>> dayQtMap = dataService.findNodeDifHistoryListFeign(deviceQueryVo).getData().values()
//                .stream().flatMap(s -> s.values().stream().flatMap(Collection::stream))
//                .filter(c -> StringUtil.isNotEmpty(c.getFirstDateTime()) && StringUtil.isNotEmpty(c.getFirstDataValue())
//                        && StringUtil.isNotEmpty(c.getLastDataValue()))
//                .collect(Collectors.groupingBy(c -> c.getFirstDateTime().substring(0, 10),
//                        Collectors.groupingBy(NodeDifHistoryDto::getFunctionLogo, Collectors.summingDouble(d ->
//                                DoubleUtil.getObjSub(d.getFirstDataValue(), d.getLastDataValue())))));
//
//        //获取历史累计收益(放电收入-充电成本)
//        BigDecimal historyIncome = getStorageHistoryCost(historyStartDate, historyEndDate, dayQtMap, purchaseElectMap, saleElectMap);
//
//        //获取每天的尖峰平谷成本和收入
//        Map<String, Map<String, BigDecimal>> dayCostMap = getStorageCostMap(startDate, endDate, dayQtMap, purchaseElectMap, saleElectMap);
//
//        //对数据进行组装
//        for (String date : dateList) {
//            result.getDateList().add(date);
//            List<Map<String, BigDecimal>> costList = Lists.newArrayList();
//            if (dateType == 1) { //日
//                if (dayCostMap.containsKey(date)) {
//                    costList.add(dayCostMap.get(date));
//                }
//            } else {
//                costList.addAll(dayCostMap.entrySet().stream().filter(s -> s.getKey().contains(date))
//                        .map(Map.Entry::getValue).collect(Collectors.toList()));
//            }
//            if (CollectionUtils.isEmpty(costList)) {
//                result.getChargeMoneyList().add(BigDecimal.ZERO);
//                result.getDischargeMoneyList().add(BigDecimal.ZERO);
//                result.getIncomeList().add(BigDecimal.ZERO);
//                result.getTotalIncomeList().add(historyIncome);
//                continue;
//            }
//
//            //充电成本(总)
//            BigDecimal chargeMoney = BigDecimal.ZERO;
//            //售电收入(总)
//            BigDecimal dischargeMoney = BigDecimal.ZERO;
//            for (Map<String, BigDecimal> costMap : costList) {
//                //获取充电成本(尖峰平谷深)
//                if (costMap.containsKey(FunctionLogoParamVo.TOP_SUP_KWH)) { //尖
//                    BigDecimal cost = costMap.get(FunctionLogoParamVo.TOP_SUP_KWH);
//                    result.setTopChargeMoney(DoubleUtil.getToBigDecimal(result.getTopChargeMoney().add(cost)));
//                    chargeMoney = chargeMoney.add(cost);
//                }
//                if (costMap.containsKey(FunctionLogoParamVo.PEAK_SUP_KWH)) { //峰
//                    BigDecimal cost = costMap.get(FunctionLogoParamVo.PEAK_SUP_KWH);
//                    result.setPeakChargeMoney(DoubleUtil.getToBigDecimal(result.getPeakChargeMoney().add(cost)));
//                    chargeMoney = chargeMoney.add(cost);
//                }
//                if (costMap.containsKey(FunctionLogoParamVo.PLAIN_SUP_KWH)) { //平
//                    BigDecimal cost = costMap.get(FunctionLogoParamVo.PLAIN_SUP_KWH);
//                    result.setPlainChargeMoney(DoubleUtil.getToBigDecimal(result.getPlainChargeMoney().add(cost)));
//                    chargeMoney = chargeMoney.add(cost);
//                }
//                if (costMap.containsKey(FunctionLogoParamVo.VALLEY_SUP_KWH)) { //谷
//                    BigDecimal cost = costMap.get(FunctionLogoParamVo.VALLEY_SUP_KWH);
//                    result.setValleyChargeMoney(DoubleUtil.getToBigDecimal(result.getValleyChargeMoney().add(cost)));
//                    chargeMoney = chargeMoney.add(cost);
//                }
//                if (costMap.containsKey(FunctionLogoParamVo.DEEP_SUP_KWH)) { //深谷
//                    BigDecimal cost = costMap.get(FunctionLogoParamVo.DEEP_SUP_KWH);
//                    result.setDeepChargeMoney(DoubleUtil.getToBigDecimal(result.getDeepChargeMoney().add(cost)));
//                    chargeMoney = chargeMoney.add(cost);
//                }
//                //获取售电收入(尖峰平谷深)
//                if (costMap.containsKey(FunctionLogoParamVo.TOP_REV_KWH)) { //尖
//                    BigDecimal cost = costMap.get(FunctionLogoParamVo.TOP_REV_KWH);
//                    result.setTopDischargeMoney(DoubleUtil.getToBigDecimal(result.getTopDischargeMoney().add(cost)));
//                    dischargeMoney = dischargeMoney.add(cost);
//                }
//                if (costMap.containsKey(FunctionLogoParamVo.PEAK_REV_KWH)) { //峰
//                    BigDecimal cost = costMap.get(FunctionLogoParamVo.PEAK_REV_KWH);
//                    result.setPeakDischargeMoney(DoubleUtil.getToBigDecimal(result.getPeakDischargeMoney().add(cost)));
//                    dischargeMoney = dischargeMoney.add(cost);
//                }
//                if (costMap.containsKey(FunctionLogoParamVo.PLAIN_REV_KWH)) { //平
//                    BigDecimal cost = costMap.get(FunctionLogoParamVo.PLAIN_REV_KWH);
//                    result.setPlainDischargeMoney(DoubleUtil.getToBigDecimal(result.getPlainDischargeMoney().add(cost)));
//                    dischargeMoney = dischargeMoney.add(cost);
//                }
//                if (costMap.containsKey(FunctionLogoParamVo.VALLEY_REV_KWH)) { //谷
//                    BigDecimal cost = costMap.get(FunctionLogoParamVo.VALLEY_REV_KWH);
//                    result.setValleyDischargeMoney(DoubleUtil.getToBigDecimal(result.getValleyDischargeMoney().add(cost)));
//                    dischargeMoney = dischargeMoney.add(cost);
//                }
//                if (costMap.containsKey(FunctionLogoParamVo.DEEP_REV_KWH)) { //深谷
//                    BigDecimal cost = costMap.get(FunctionLogoParamVo.DEEP_REV_KWH);
//                    result.setDeepDischargeMoney(DoubleUtil.getToBigDecimal(result.getDeepDischargeMoney().add(cost)));
//                    dischargeMoney = dischargeMoney.add(cost);
//                }
//            }
//            //充电成本
//            result.setChargeMoney(DoubleUtil.getToBigDecimal(result.getChargeMoney().add(chargeMoney)));
//            result.getChargeMoneyList().add(DoubleUtil.getToBigDecimal(chargeMoney));
//            //售电收入
//            result.setDischargeMoney(DoubleUtil.getToBigDecimal(result.getDischargeMoney().add(dischargeMoney)));
//            result.getDischargeMoneyList().add(DoubleUtil.getToBigDecimal(dischargeMoney));
//            //收益
//            BigDecimal income = DoubleUtil.getToBigDecimal(dischargeMoney.subtract(chargeMoney));
//            result.getIncomeList().add(income);
//            historyIncome = historyIncome.add(income);
//            result.getTotalIncomeList().add(DoubleUtil.getToBigDecimal(historyIncome));
//        }
//        return ResponseResult.ok(result);
//    }
//
//    @Override
//    public ResponseResult<StorageKwhIncomeDto> countStorageKwhIncome(StorageCountVo storageCountVo) {
//        //返回的对象
//        StorageKwhIncomeDto result = new StorageKwhIncomeDto();
//
//        //校验入参
//        if (isValidStorageCount(storageCountVo)) {
//            return ResponseResult.paramError(ResponseResult.PARAM_ERROR);
//        }
//
//        String dataId = storageCountVo.getDataId();
//        Integer dateType = storageCountVo.getDateType();
//        String startDate = storageCountVo.getStartDate();
//        String endDate = storageCountVo.getEndDate();
//
//        //获取多个查询设备id
//        String siteId = null;
//        Set<String> deviceIds = Sets.newHashSet();
//        if (storageCountVo.getQueryType() == 1) { //站点id
//            siteId = dataId;
//            //根据站点id查询储能系统
//            SiteInfoDto siteInfo = deviceService.findSiteBasicInfoByIds(Collections.singletonList(dataId))
//                    .getData().get(dataId);
//            if (siteInfo != null && CollectionUtils.isNotEmpty(siteInfo.getSiteScenarioTypeDtos())) {
//                //根据多个储能系统id查询并网表数据
//                List<String> systemIds = siteInfo.getSiteScenarioTypeDtos().stream()
//                        .filter(s -> Objects.equals(s.getScenarioType(), 2))
//                        .map(SiteScenarioTypeDto::getId).collect(Collectors.toList());
//                deviceIds = deviceService.findDeviceInfoByParentIds(systemIds).getData().values().stream()
//                        .flatMap(s -> s.stream().filter(d ->
//                                Objects.equals(d.getTypeId(), "38"))).map(DeviceBasicInfoDto::getId)
//                        .collect(Collectors.toSet());
//            }
//        }
//        if (storageCountVo.getQueryType() == 2) { //设备id
//            deviceIds.add(storageCountVo.getDataId());
//            DeviceBasicInfoDto deviceInfo = deviceService.findDeviceBasicInfoByIds(Collections.singletonList(dataId))
//                    .getData().get(dataId);
//            if (deviceInfo != null && StringUtil.isNotEmpty(deviceInfo.getSiteId())) {
//                siteId = deviceInfo.getSiteId();
//            }
//        }
//
//        if (StringUtil.isEmpty(siteId) || CollectionUtils.isEmpty(deviceIds)) {
//            return ResponseResult.ok(result);
//        }
//
//        //1.获取日期列表
//        List<String> dateList = DateUtil.getDateBetween(dateType, startDate, endDate);
//
//        //根据站点id查询储能购电电价和售电电价
//        //日期 -> (时段类型 -> 电费)
//        Map<String, Map<Integer, BigDecimal>> purchaseElectMap = Maps.newHashMap();//购电电价
//        Map<String, Map<Integer, BigDecimal>> saleElectMap = Maps.newHashMap();//售电电价
//        Map<Integer, List<ElectConfigEntity>> electConfigMap = electConfigDao.findElectConfigList(siteId, Arrays.asList(4, 5), startDate, endDate).stream()
//                .collect(Collectors.groupingBy(ElectConfigEntity::getModuleType));
//        if (MapUtils.isEmpty(electConfigMap)) {
//            return ResponseResult.ok(result);
//        }
//        //根据多个电站配置id查询尖峰平谷电价(电价配置id -> (时段类型 -> 电费))
//        Set<String> electConfigIds = electConfigMap.values().stream().flatMap(e -> e.stream().map(ElectConfigEntity::getId)).collect(Collectors.toSet());
//        Map<String, Map<Integer, BigDecimal>> electTimeFrameMap = electTimeFrameDao.findAllByElectConfigIdIn(electConfigIds).stream()
//                .collect(Collectors.groupingBy(ElectTimeFrameEntity::getElectConfigId, Collectors.toMap(ElectTimeFrameEntity::getPeriodType,
//                        ElectTimeFrameEntity::getElectMoney, (k1, k2) -> k2)));
//        //对数据进行组装
//        List<String> dateConfigList = getDateBetween(1, startDate, endDate);
//        electConfigMap.forEach((moduleType, electConfigList) -> electConfigList.forEach(electConfig -> {
//            Map<Integer, BigDecimal> periodTypeMap = Maps.newHashMap();
//            if (electTimeFrameMap.containsKey(electConfig.getId())) {
//                periodTypeMap = electTimeFrameMap.get(electConfig.getId());
//            }
//            //过滤日期范围内的日期
//            List<String> electDateList = DateUtil.getDateBetween(1, electConfig.getStartDate(), electConfig.getEndDate());
//            List<String> electFiletrDateList = dateConfigList.stream().filter(electDateList::contains).collect(Collectors.toList());
//            for (String date : electFiletrDateList) {
//                if (moduleType == 5) { //储能购电电价
//                    purchaseElectMap.put(date, periodTypeMap);
//                }
//                if (moduleType == 4) { //储能售电电价
//                    saleElectMap.put(date, periodTypeMap);
//                }
//            }
//        }));
//
//        //根据多个设备id和日期查询储能系统下面的电表功能点历史数据
//        DeviceHistoryQueryVo deviceQueryVo = getDeviceHistoryQueryVo(deviceIds, startDate, endDate);
//        //获取每天的电量,日期(年月日)->(功能点标识->电量)
//        Map<String, Map<String, Double>> dayQtMap = dataService.findNodeDifHistoryListFeign(deviceQueryVo).getData().values()
//                .stream().flatMap(s -> s.values().stream().flatMap(Collection::stream))
//                .filter(c -> StringUtil.isNotEmpty(c.getFirstDateTime()) && StringUtil.isNotEmpty(c.getFirstDataValue())
//                        && StringUtil.isNotEmpty(c.getLastDataValue()))
//                .collect(Collectors.groupingBy(c -> c.getFirstDateTime().substring(0, 10),
//                        Collectors.groupingBy(NodeDifHistoryDto::getFunctionLogo, Collectors.summingDouble(d ->
//                                DoubleUtil.objToDouble(d.getLastDataValue()) - DoubleUtil.objToDouble(d.getFirstDataValue()))
//                        )));
//
//        //获取每天的尖峰平谷成本和收入
//        Map<String, Map<String, BigDecimal>> dayCostMap = getStorageCostMap(startDate, endDate, dayQtMap, purchaseElectMap, saleElectMap);
//
//        //对数据进行组装
//        for (String date : dateList) {
//            result.getDateList().add(date);
//            List<Map<String, Double>> qtList = Lists.newArrayList();
//            List<Map<String, BigDecimal>> costList = Lists.newArrayList();
//            if (dateType == 1) { //日
//                if (dayQtMap.containsKey(date)) {
//                    qtList.add(dayQtMap.get(date));
//                }
//                if (dayCostMap.containsKey(date)) {
//                    costList.add(dayCostMap.get(date));
//                }
//            } else {
//                qtList.addAll(dayQtMap.entrySet().stream().filter(s -> s.getKey().contains(date))
//                        .map(Map.Entry::getValue).collect(Collectors.toList()));
//                costList.addAll(dayCostMap.entrySet().stream().filter(s -> s.getKey().contains(date))
//                        .map(Map.Entry::getValue).collect(Collectors.toList()));
//            }
//            if (CollectionUtils.isEmpty(qtList) || CollectionUtils.isEmpty(costList)) {
//                result.getKwhIncomeList().add(BigDecimal.ZERO);
//                continue;
//            }
//
//            //充电电量(总)
//            Double chargeQt = 0.0;
//            //放电电量(总)
//            Double dischargeQt = 0.0;
//            for (Map<String, Double> qtMap : qtList) {
//                //获取充电电量(尖峰平谷深)
//                if (qtMap.containsKey(FunctionLogoParamVo.TOP_SUP_KWH)) { //尖】
//                    chargeQt = chargeQt + qtMap.get(FunctionLogoParamVo.TOP_SUP_KWH);
//                }
//                if (qtMap.containsKey(FunctionLogoParamVo.PEAK_SUP_KWH)) { //峰
//                    chargeQt = chargeQt + qtMap.get(FunctionLogoParamVo.PEAK_SUP_KWH);
//                }
//                if (qtMap.containsKey(FunctionLogoParamVo.PLAIN_SUP_KWH)) { //平
//                    chargeQt = chargeQt + qtMap.get(FunctionLogoParamVo.PLAIN_SUP_KWH);
//                }
//                if (qtMap.containsKey(FunctionLogoParamVo.VALLEY_SUP_KWH)) { //谷
//                    chargeQt = chargeQt + qtMap.get(FunctionLogoParamVo.VALLEY_SUP_KWH);
//                }
//                if (qtMap.containsKey(FunctionLogoParamVo.DEEP_SUP_KWH)) { //深谷
//                    chargeQt = chargeQt + qtMap.get(FunctionLogoParamVo.DEEP_SUP_KWH);
//                }
//                //获取放电电量(尖峰平谷深)
//                if (qtMap.containsKey(FunctionLogoParamVo.TOP_REV_KWH)) { //尖
//                    dischargeQt = dischargeQt + qtMap.get(FunctionLogoParamVo.TOP_REV_KWH);
//                }
//                if (qtMap.containsKey(FunctionLogoParamVo.PEAK_REV_KWH)) { //峰
//                    dischargeQt = dischargeQt + qtMap.get(FunctionLogoParamVo.PEAK_REV_KWH);
//                }
//                if (qtMap.containsKey(FunctionLogoParamVo.PLAIN_REV_KWH)) { //平
//                    dischargeQt = dischargeQt + qtMap.get(FunctionLogoParamVo.PLAIN_REV_KWH);
//                }
//                if (qtMap.containsKey(FunctionLogoParamVo.VALLEY_REV_KWH)) { //谷
//                    dischargeQt = dischargeQt + qtMap.get(FunctionLogoParamVo.VALLEY_REV_KWH);
//                }
//                if (qtMap.containsKey(FunctionLogoParamVo.DEEP_REV_KWH)) { //深谷
//                    dischargeQt = dischargeQt + qtMap.get(FunctionLogoParamVo.DEEP_REV_KWH);
//                }
//            }
//
//            //充电成本(总)
//            BigDecimal chargeMoney = BigDecimal.ZERO;
//            //售电收入(总)
//            BigDecimal dischargeMoney = BigDecimal.ZERO;
//            for (Map<String, BigDecimal> costMap : costList) {
//                //获取充电成本(尖峰平谷深)
//                if (costMap.containsKey(FunctionLogoParamVo.TOP_SUP_KWH)) { //尖
//                    BigDecimal cost = costMap.get(FunctionLogoParamVo.TOP_SUP_KWH);
//                    chargeMoney = chargeMoney.add(cost);
//                }
//                if (costMap.containsKey(FunctionLogoParamVo.PEAK_SUP_KWH)) { //峰
//                    BigDecimal cost = costMap.get(FunctionLogoParamVo.PEAK_SUP_KWH);
//                    chargeMoney = chargeMoney.add(cost);
//                }
//                if (costMap.containsKey(FunctionLogoParamVo.PLAIN_SUP_KWH)) { //平
//                    BigDecimal cost = costMap.get(FunctionLogoParamVo.PLAIN_SUP_KWH);
//                    chargeMoney = chargeMoney.add(cost);
//                }
//                if (costMap.containsKey(FunctionLogoParamVo.VALLEY_SUP_KWH)) { //谷
//                    BigDecimal cost = costMap.get(FunctionLogoParamVo.VALLEY_SUP_KWH);
//                    chargeMoney = chargeMoney.add(cost);
//                }
//                if (costMap.containsKey(FunctionLogoParamVo.DEEP_SUP_KWH)) { //深谷
//                    BigDecimal cost = costMap.get(FunctionLogoParamVo.DEEP_SUP_KWH);
//                    chargeMoney = chargeMoney.add(cost);
//                }
//                //获取售电收入(尖峰平谷深)
//                if (costMap.containsKey(FunctionLogoParamVo.TOP_REV_KWH)) { //尖
//                    BigDecimal cost = costMap.get(FunctionLogoParamVo.TOP_REV_KWH);
//                    dischargeMoney = dischargeMoney.add(cost);
//                }
//                if (costMap.containsKey(FunctionLogoParamVo.PEAK_REV_KWH)) { //峰
//                    BigDecimal cost = costMap.get(FunctionLogoParamVo.PEAK_REV_KWH);
//                    dischargeMoney = dischargeMoney.add(cost);
//                }
//                if (costMap.containsKey(FunctionLogoParamVo.PLAIN_REV_KWH)) { //平
//                    BigDecimal cost = costMap.get(FunctionLogoParamVo.PLAIN_REV_KWH);
//                    dischargeMoney = dischargeMoney.add(cost);
//                }
//                if (costMap.containsKey(FunctionLogoParamVo.VALLEY_REV_KWH)) { //谷
//                    BigDecimal cost = costMap.get(FunctionLogoParamVo.VALLEY_REV_KWH);
//                    dischargeMoney = dischargeMoney.add(cost);
//                }
//                if (costMap.containsKey(FunctionLogoParamVo.DEEP_REV_KWH)) { //深谷
//                    BigDecimal cost = costMap.get(FunctionLogoParamVo.DEEP_REV_KWH);
//                    dischargeMoney = dischargeMoney.add(cost);
//                }
//            }
//            //充电量(总)
//            result.setChargeQt(result.getChargeQt() + chargeQt);
//            //充电成本(总)
//            result.setChargeMoney(result.getChargeMoney().add(chargeMoney));
//            //放电量(总)
//            result.setDischargeQt(result.getDischargeQt() + dischargeQt);
//            //放电收入(总)
//            result.setDischargeMoney(result.getDischargeMoney().add(dischargeMoney));
//            //合计收益 放电收入-充电成本
//            BigDecimal income = DoubleUtil.getToBigDecimal(dischargeMoney.subtract(chargeMoney));
//            result.setTotalIncome(result.getTotalIncome().add(income));
//
//            //度电收益曲线
//            if (dischargeQt != 0.0) {
//                result.getKwhIncomeList().add(income.divide(BigDecimal.valueOf(dischargeQt), 2, RoundingMode.HALF_UP));
//            } else {
//                result.getKwhIncomeList().add(BigDecimal.ZERO);
//            }
//        }
//
//        //合计度电收益（合计收益 / 合计放电量）
//        if (result.getDischargeQt() != 0) {
//            result.setTotalKwhIncome(result.getTotalIncome().divide(BigDecimal.valueOf(result.getDischargeQt()), 2, RoundingMode.HALF_UP));
//        }
//        return ResponseResult.ok(result);
//    }
//
//    //封装统计储能充电成本,放电收入,收益数据()
//    private BigDecimal getStorageHistoryCost(String startDate, String endDate, Map<String, Map<String, Double>> dayQtMap,
//                                             Map<String, Map<Integer, BigDecimal>> purchaseElectMap, Map<String, Map<Integer, BigDecimal>> saleElectMap) {
//        return DoubleUtil.getToBigDecimal(getDateBetween(1, startDate, endDate).stream().map(date -> {
//            if (dayQtMap.containsKey(date)) {
//                Map<String, Double> functionLogoMap = dayQtMap.get(date);
//                //购电成本（总） = 购电成本(总) + 购电成本(尖) + 购电成本(峰) + 购电成本(平) + 购电成本(谷) + 购电成本(深谷)
//                //购电成本 = 分时正向有功电量 * 分时购电电价
//                BigDecimal chargeMoney = BigDecimal.ZERO;
//                Map<Integer, BigDecimal> purchaseTypeMap = Maps.newHashMap();
//                if (purchaseElectMap.containsKey(date)) {
//                    purchaseTypeMap = purchaseElectMap.get(date);
//                }
//                if (functionLogoMap.containsKey(FunctionLogoParamVo.TOP_SUP_KWH) && purchaseTypeMap.containsKey(1)) {
//                    chargeMoney = chargeMoney.add(BigDecimal.valueOf(functionLogoMap.get(FunctionLogoParamVo.TOP_SUP_KWH))
//                            .multiply(purchaseTypeMap.get(1)));
//                }
//                if (functionLogoMap.containsKey(FunctionLogoParamVo.PEAK_SUP_KWH) && purchaseTypeMap.containsKey(2)) {
//                    chargeMoney = chargeMoney.add(BigDecimal.valueOf(functionLogoMap.get(FunctionLogoParamVo.PEAK_SUP_KWH))
//                            .multiply(purchaseTypeMap.get(2)));
//                }
//                if (functionLogoMap.containsKey(FunctionLogoParamVo.PLAIN_SUP_KWH) && purchaseTypeMap.containsKey(3)) {
//                    chargeMoney = chargeMoney.add(BigDecimal.valueOf(functionLogoMap.get(FunctionLogoParamVo.PLAIN_SUP_KWH))
//                            .multiply(purchaseTypeMap.get(3)));
//                }
//                if (functionLogoMap.containsKey(FunctionLogoParamVo.VALLEY_SUP_KWH) && purchaseTypeMap.containsKey(4)) {
//                    chargeMoney = chargeMoney.add(BigDecimal.valueOf(functionLogoMap.get(FunctionLogoParamVo.VALLEY_SUP_KWH))
//                            .multiply(purchaseTypeMap.get(4)));
//                }
//                if (functionLogoMap.containsKey(FunctionLogoParamVo.DEEP_SUP_KWH) && purchaseTypeMap.containsKey(5)) {
//                    chargeMoney = chargeMoney.add(BigDecimal.valueOf(functionLogoMap.get(FunctionLogoParamVo.DEEP_SUP_KWH))
//                            .multiply(purchaseTypeMap.get(5)));
//                }
//
//                //售电收入（总） = 售电收入(总) + 售电收入(尖) + 售电收入(峰) + 售电收入(平) + 售电收入(谷) + 售电收入(深谷)
//                BigDecimal dischargeMoney = BigDecimal.ZERO;
//                Map<Integer, BigDecimal> saleTypeMap = Maps.newHashMap();
//                if (saleElectMap.containsKey(date)) {
//                    saleTypeMap = saleElectMap.get(date);
//                }
//                if (functionLogoMap.containsKey(FunctionLogoParamVo.TOP_REV_KWH) && saleTypeMap.containsKey(1)) {
//                    dischargeMoney = dischargeMoney.add(BigDecimal.valueOf(functionLogoMap.get(FunctionLogoParamVo.TOP_REV_KWH))
//                            .multiply(saleTypeMap.get(1)));
//                }
//                if (functionLogoMap.containsKey(FunctionLogoParamVo.PEAK_REV_KWH) && saleTypeMap.containsKey(2)) {
//                    dischargeMoney = dischargeMoney.add(BigDecimal.valueOf(functionLogoMap.get(FunctionLogoParamVo.PEAK_REV_KWH))
//                            .multiply(saleTypeMap.get(2)));
//                }
//                if (functionLogoMap.containsKey(FunctionLogoParamVo.PLAIN_REV_KWH) && saleTypeMap.containsKey(3)) {
//                    dischargeMoney = dischargeMoney.add(BigDecimal.valueOf(functionLogoMap.get(FunctionLogoParamVo.PLAIN_REV_KWH))
//                            .multiply(saleTypeMap.get(3)));
//                }
//                if (functionLogoMap.containsKey(FunctionLogoParamVo.VALLEY_REV_KWH) && saleTypeMap.containsKey(4)) {
//                    dischargeMoney = dischargeMoney.add(BigDecimal.valueOf(functionLogoMap.get(FunctionLogoParamVo.VALLEY_REV_KWH))
//                            .multiply(saleTypeMap.get(4)));
//                }
//                if (functionLogoMap.containsKey(FunctionLogoParamVo.DEEP_REV_KWH) && saleTypeMap.containsKey(5)) {
//                    dischargeMoney = dischargeMoney.add(BigDecimal.valueOf(functionLogoMap.get(FunctionLogoParamVo.DEEP_REV_KWH))
//                            .multiply(saleTypeMap.get(5)));
//                }
//                return dischargeMoney.subtract(chargeMoney);
//            }
//            return BigDecimal.ZERO;
//        }).reduce(BigDecimal.ZERO, BigDecimal::add));
//    }
//
//
//    //封装统计储能充电成本,放电收入,收益数据(日期 -> (标识 -> 数据))
//    private Map<String, Map<String, BigDecimal>> getStorageCostMap(String startDate, String endDate, Map<String, Map<String, Double>> dayQtMap,
//                                                                   Map<String, Map<Integer, BigDecimal>> purchaseElectMap, Map<String, Map<Integer, BigDecimal>> saleElectMap) {
//        //返回的对象
//        Map<String, Map<String, BigDecimal>> resultMap = Maps.newHashMap();
//        getDateBetween(1, startDate, endDate).forEach(date -> {
//            if (dayQtMap.containsKey(date)) {
//                Map<String, BigDecimal> moneyLogoMap = Maps.newHashMap();
//                Map<String, Double> functionLogoMap = dayQtMap.get(date);
//                //购电成本（总） = 购电成本(总) + 购电成本(尖) + 购电成本(峰) + 购电成本(平) + 购电成本(谷) + 购电成本(深谷)
//                //购电成本 = 分时正向有功电量 * 分时购电电价
//                Map<Integer, BigDecimal> purchaseTypeMap = Maps.newHashMap();
//                if (purchaseElectMap.containsKey(date)) {
//                    purchaseTypeMap = purchaseElectMap.get(date);
//                }
//                if (functionLogoMap.containsKey(FunctionLogoParamVo.TOP_SUP_KWH) && purchaseTypeMap.containsKey(1)) {
//                    moneyLogoMap.put(FunctionLogoParamVo.TOP_SUP_KWH, BigDecimal.valueOf(functionLogoMap
//                            .get(FunctionLogoParamVo.TOP_SUP_KWH)).multiply(purchaseTypeMap.get(1)));
//                }
//                if (functionLogoMap.containsKey(FunctionLogoParamVo.PEAK_SUP_KWH) && purchaseTypeMap.containsKey(2)) {
//                    moneyLogoMap.put(FunctionLogoParamVo.PEAK_SUP_KWH, BigDecimal.valueOf(functionLogoMap
//                            .get(FunctionLogoParamVo.PEAK_SUP_KWH)).multiply(purchaseTypeMap.get(2)));
//                }
//                if (functionLogoMap.containsKey(FunctionLogoParamVo.PLAIN_SUP_KWH) && purchaseTypeMap.containsKey(3)) {
//                    moneyLogoMap.put(FunctionLogoParamVo.PLAIN_SUP_KWH, BigDecimal.valueOf(functionLogoMap
//                            .get(FunctionLogoParamVo.PLAIN_SUP_KWH)).multiply(purchaseTypeMap.get(3)));
//                }
//                if (functionLogoMap.containsKey(FunctionLogoParamVo.VALLEY_SUP_KWH) && purchaseTypeMap.containsKey(4)) {
//                    moneyLogoMap.put(FunctionLogoParamVo.VALLEY_SUP_KWH, BigDecimal.valueOf(functionLogoMap
//                            .get(FunctionLogoParamVo.VALLEY_SUP_KWH)).multiply(purchaseTypeMap.get(4)));
//                }
//                if (functionLogoMap.containsKey(FunctionLogoParamVo.DEEP_SUP_KWH) && purchaseTypeMap.containsKey(5)) {
//                    moneyLogoMap.put(FunctionLogoParamVo.DEEP_SUP_KWH, BigDecimal.valueOf(functionLogoMap
//                            .get(FunctionLogoParamVo.DEEP_SUP_KWH)).multiply(purchaseTypeMap.get(5)));
//                }
//
//                //售电收入
//                Map<Integer, BigDecimal> saleTypeMap = Maps.newHashMap();
//                if (saleElectMap.containsKey(date)) {
//                    saleTypeMap = saleElectMap.get(date);
//                }
//                if (functionLogoMap.containsKey(FunctionLogoParamVo.TOP_REV_KWH) && saleTypeMap.containsKey(1)) {
//                    moneyLogoMap.put(FunctionLogoParamVo.TOP_REV_KWH, BigDecimal.valueOf(functionLogoMap
//                            .get(FunctionLogoParamVo.TOP_REV_KWH)).multiply(saleTypeMap.get(1)));
//                }
//                if (functionLogoMap.containsKey(FunctionLogoParamVo.PEAK_REV_KWH) && saleTypeMap.containsKey(2)) {
//                    moneyLogoMap.put(FunctionLogoParamVo.PEAK_REV_KWH, BigDecimal.valueOf(functionLogoMap
//                            .get(FunctionLogoParamVo.PEAK_REV_KWH)).multiply(saleTypeMap.get(2)));
//                }
//                if (functionLogoMap.containsKey(FunctionLogoParamVo.PLAIN_REV_KWH) && saleTypeMap.containsKey(3)) {
//                    moneyLogoMap.put(FunctionLogoParamVo.PLAIN_REV_KWH, BigDecimal.valueOf(functionLogoMap
//                            .get(FunctionLogoParamVo.PLAIN_REV_KWH)).multiply(saleTypeMap.get(3)));
//                }
//                if (functionLogoMap.containsKey(FunctionLogoParamVo.VALLEY_REV_KWH) && saleTypeMap.containsKey(4)) {
//                    moneyLogoMap.put(FunctionLogoParamVo.VALLEY_REV_KWH, BigDecimal.valueOf(functionLogoMap
//                            .get(FunctionLogoParamVo.VALLEY_REV_KWH)).multiply(saleTypeMap.get(4)));
//                }
//                if (functionLogoMap.containsKey(FunctionLogoParamVo.DEEP_REV_KWH) && saleTypeMap.containsKey(5)) {
//                    moneyLogoMap.put(FunctionLogoParamVo.DEEP_REV_KWH, BigDecimal.valueOf(functionLogoMap
//                            .get(FunctionLogoParamVo.DEEP_REV_KWH)).multiply(saleTypeMap.get(5)));
//                }
//                resultMap.put(date, moneyLogoMap);
//            }
//        });
//        return resultMap;
//    }
//
//
//}
