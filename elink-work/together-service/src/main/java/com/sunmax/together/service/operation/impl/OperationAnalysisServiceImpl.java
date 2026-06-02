package com.sunmax.together.service.operation.impl;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.sunmax.common.dto.data.NodeDifHistoryDto;
import com.sunmax.common.dto.device.*;
import com.sunmax.together.dto.operation.operationAnalysis.OperationCurveDto;
import com.sunmax.together.dto.operation.operationAnalysis.OperationOverviewDto;
import com.sunmax.common.dto.together.PileMonitorDataDto;
import com.sunmax.common.util.DateUtil;
import com.sunmax.common.util.DoubleUtil;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.util.StringUtil;
import com.sunmax.common.vo.DeviceTypeParamVo;
import com.sunmax.common.vo.FunctionLogoParamVo;
import com.sunmax.common.vo.ReaFieldParamVo;
import com.sunmax.common.vo.SiteFieldParamVo;
import com.sunmax.common.vo.data.DeviceHistoryQueryVo;
import com.sunmax.together.dao.order.OrderRecordDao;
import com.sunmax.together.dao.order.SettlementRecordDao;
import com.sunmax.together.dao.SiteCountRecordDao;
import com.sunmax.together.dto.operation.operationAnalysis.*;
import com.sunmax.together.entity.SiteCountRecordEntity;
import com.sunmax.together.entity.order.OrderRecordEntity;
import com.sunmax.together.entity.order.SettlementRecordEntity;
import com.sunmax.together.mapper.OrderRecordMapper;
import com.sunmax.together.model.ChargeOrderQtModel;
import com.sunmax.together.service.operation.OperationAnalysisService;
import com.sunmax.together.service.feign.DataService;
import com.sunmax.together.service.feign.DeviceService;
import com.sunmax.common.vo.together.OperationCurveVo;
import com.sunmax.common.vo.together.OperationOverviewVo;
import com.sunmax.together.vo.operation.operationAnalysis.OperationTypeVo;
import com.sunmax.together.vo.operation.operationAnalysis.PvOperationAnalysisVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.Predicate;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.sunmax.common.util.DoubleUtil.getToDouble;

@Service
@Slf4j
public class OperationAnalysisServiceImpl implements OperationAnalysisService {

    @Autowired
    private OrderRecordDao orderRecordDao;

    @Autowired
    private SettlementRecordDao settlementRecordDao;

    @Autowired
    private SiteCountRecordDao siteCountRecordDao;

    @Autowired
    private OrderRecordMapper orderRecordMapper;

    @Autowired
    private DeviceService deviceService;

    @Autowired
    private DataService dataService;

    @Override
    public ResponseResult<OperationOverviewDto> countOperationOverview(OperationOverviewVo operationOverviewVo) {
        //返回的对象
        OperationOverviewDto result = new OperationOverviewDto();
        if (StringUtil.isEmpty(operationOverviewVo.getSiteIds()) || StringUtil.isEmpty(operationOverviewVo.getStartDate())
                || StringUtil.isEmpty(operationOverviewVo.getEndDate())) {
            return ResponseResult.paramError(ResponseResult.PARAM_ERROR);
        }
        List<String> siteIds = JSON.parseArray(operationOverviewVo.getSiteIds(), String.class);
        String startTime = DateUtil.getDayStart(operationOverviewVo.getStartDate());
        String endTime = DateUtil.getDayEnd(operationOverviewVo.getEndDate());
        //获取当前统计数据
        OverviewDto thisOverview = getOverview(siteIds, startTime, endTime);
        BeanUtils.copyProperties(thisOverview, result);

        String beforeStartDate = operationOverviewVo.getBeforeStartDate();
        String beforeEndDate = operationOverviewVo.getBeforeEndDate();
        if (StringUtil.isNotEmpty(beforeStartDate) && StringUtil.isNotEmpty(beforeEndDate)) {
            String beforeStartTime = DateUtil.getDayStart(beforeStartDate);
            String beforeEndTime = DateUtil.getDayEnd(beforeEndDate);
            OverviewDto beforeOverview = getOverview(siteIds, beforeStartTime, beforeEndTime);
            result.setChargeOrderMoneyRatio(this.calculateOverviewOver(beforeOverview.getChargeOrderMoney(), thisOverview.getChargeOrderMoney()));
            result.setChargePayMoneyRatio(this.calculateOverviewOver(beforeOverview.getChargePayMoney(), thisOverview.getChargePayMoney()));
            result.setChargeOrderQtRatio(this.calculateOverviewOver(beforeOverview.getChargeOrderQt(), thisOverview.getChargeOrderQt()));
            result.setChargeOrderNumRatio(this.calculateOverviewOver(beforeOverview.getChargeOrderNum(), thisOverview.getChargeOrderNum()));
            result.setDischargeOrderMoneyRatio(this.calculateOverviewOver(beforeOverview.getDischargeOrderMoney(), thisOverview.getDischargeOrderMoney()));
            result.setDischargeOrderQtRatio(this.calculateOverviewOver(beforeOverview.getDischargeOrderQt(), thisOverview.getDischargeOrderQt()));
            result.setAvgChargeQtRatio(this.calculateOverviewOver(beforeOverview.getAvgChargeQt(), thisOverview.getAvgChargeQt()));
            result.setTimeRatioRatio(this.calculateOverviewOver(beforeOverview.getTimeRatio(), thisOverview.getTimeRatio()));
            result.setChargeDurationRatio(this.calculateOverviewOver(beforeOverview.getChargeDuration(), thisOverview.getChargeDuration()));
            result.setAvgChargeFeeRatio(this.calculateOverviewOver(beforeOverview.getAvgChargeFee(), thisOverview.getAvgChargeFee()));
            result.setPowerRatioRatio(this.calculateOverviewOver(beforeOverview.getPowerRatio(), thisOverview.getPowerRatio()));
            result.setChargeSuccessRatioRatio(this.calculateOverviewOver(beforeOverview.getChargeSuccessRatio(), thisOverview.getChargeSuccessRatio()));
        }
        return ResponseResult.ok(result);
    }

    @Override
    public ResponseResult<OperationCurveDto> countOperationCurve(OperationCurveVo operationCurveVo) {
        //返回的对象
        OperationCurveDto result = new OperationCurveDto();

        //校验入参
        if (StringUtil.isEmpty(operationCurveVo.getSiteIds()) || StringUtil.isEmpty(operationCurveVo.getStartDate())
                || StringUtil.isEmpty(operationCurveVo.getEndDate()) || StringUtil.isEmpty(operationCurveVo.getTypes())
                || StringUtil.isEmpty(operationCurveVo.getDateType())) {
            return ResponseResult.paramError(ResponseResult.PARAM_ERROR);
        }
        List<String> siteIds = JSON.parseArray(operationCurveVo.getSiteIds(), String.class);
        LocalDate startLocalDate = DateUtil.strToLocalDate(operationCurveVo.getStartDate());
        LocalDate endLocalDate = DateUtil.strToLocalDate(operationCurveVo.getEndDate());
        String startTime = DateUtil.getDayStart(operationCurveVo.getStartDate());
        String endTime = DateUtil.getDayEnd(operationCurveVo.getEndDate());
        List<Integer> types = JSON.parseArray(operationCurveVo.getTypes(), Integer.class);
        List<OrderRecordEntity> orderRecordList = orderRecordDao.findAll((Specification<OrderRecordEntity>) (root, cq, cb) -> {
            List<Predicate> predicates = Lists.newArrayList();
            predicates.add(cb.in(root.get("siteId")).value(siteIds));
            predicates.add(cb.between(root.get("endTime"), startTime, endTime));
            predicates.add(cb.isNotNull(root.get("runMode")));
            predicates.add(cb.isNull(root.get("abnormalCode")));
            predicates.add(cb.equal(root.get("orderStatus"), "2"));
            return cb.and(predicates.toArray(new Predicate[0]));
        });
        //获取日期列表
        Integer dateType = operationCurveVo.getDateType();
        result.setDateList(DateUtil.getDateBetween(dateType, operationCurveVo.getStartDate(), operationCurveVo.getEndDate()));

        Map<String, List<OrderRecordEntity>> orderRecordMap = Maps.newHashMap();
        Map<String, List<OrderRecordEntity>> chargeOrderRecordMap = Maps.newHashMap();
        Map<String, List<OrderRecordEntity>> dischargeOrderRecordMap = Maps.newHashMap();
        //日
        if (dateType == 1) {
            orderRecordMap = orderRecordList.stream().collect(Collectors.groupingBy(o -> o.getEndTime().substring(0, 10)));
            chargeOrderRecordMap = orderRecordList.stream().filter(c -> c.getRunMode() == 0).collect(Collectors.groupingBy(o -> o.getEndTime().substring(0, 10)));
            dischargeOrderRecordMap = orderRecordList.stream().filter(c -> c.getRunMode() == 1 || c.getRunMode() == 2).collect(Collectors.groupingBy(o -> o.getEndTime().substring(0, 10)));
        }
        //月
        if (dateType == 2) {
            orderRecordMap = orderRecordList.stream().collect(Collectors.groupingBy(o -> o.getEndTime().substring(0, 7)));
            chargeOrderRecordMap = orderRecordList.stream().filter(c -> c.getRunMode() == 0).collect(Collectors.groupingBy(o -> o.getEndTime().substring(0, 7)));
            dischargeOrderRecordMap = orderRecordList.stream().filter(c -> c.getRunMode() == 1 || c.getRunMode() == 2).collect(Collectors.groupingBy(o -> o.getEndTime().substring(0, 7)));
        }
        //类型 1-充电订单金额(元) 2-充电实付金额(元) 3-充电电量(度) 4-充电订单数量(笔) 5-V2G订单金额(元) 6-V2G放电电量(度) 7-枪均电量(度) 8-时间利用率(%) 9-充电时长(小时) 10-度均服务费(元) 11-功率利用率(%) 12-一次充电成功率(%)
        for (Integer type : types) {
            switch (type) {
                case 1:
                    //充电订单金额(订单总金额 充电电费 充电服务费)
                    result.getCurveDataMap().put(type, this.chargeOrderMoney(chargeOrderRecordMap, result.getDateList()));
                    break;
                case 2:
                    //充电实付金额(实付总金额,实付电费,实付服务费)
                    result.getCurveDataMap().put(type, this.chargePayMoney(chargeOrderRecordMap, result.getDateList()));
                    break;
                case 3:
                    //充电电量(充电电量,直流充电量,交流充电量)
                    result.getCurveDataMap().put(type, this.chargeOrderQt(chargeOrderRecordMap, result.getDateList()));
                    break;
                case 4:
                    //充电订单数量(正常订单数量,异常订单数量)
                    Map<String, Integer> abOrderNumMap = Maps.newHashMap();
                    List<ChargeOrderQtModel> abOrderNumList = orderRecordMapper.countChargeAbOrderNum(siteIds, startTime, endTime, dateType);
                    if (CollectionUtils.isNotEmpty(abOrderNumList)) {
                        abOrderNumMap = abOrderNumList.stream().collect(Collectors.toMap(ChargeOrderQtModel::getDataTime, ChargeOrderQtModel::getTotalCount, (k1, k2) -> k1));
                    }
                    result.getCurveDataMap().put(type, this.chargeOrderNum(chargeOrderRecordMap, abOrderNumMap, result.getDateList()));
                    break;
                case 5:
                    //V2G放电订单金额
                    result.getCurveDataMap().put(type, this.dischargeOrderMoney(dischargeOrderRecordMap, result.getDateList()));
                    break;
                case 6:
                    //V2G放电电量
                    result.getCurveDataMap().put(type, this.dischargeOrderQt(dischargeOrderRecordMap, result.getDateList()));
                    break;
                case 7:
                    //枪均电量(枪均充电量,直流枪均充电量,交流枪均充电量) 累计充电量/筛选日期内平均枪数
                    List<SiteCountRecordEntity> avgChargeQtRecordList = siteCountRecordDao.findAllBySiteIdInAndCountDateBetween(siteIds, startLocalDate, endLocalDate);
                    result.getCurveDataMap().put(type, this.avgChargeQt(dateType, chargeOrderRecordMap, avgChargeQtRecordList, result.getDateList()));
                    break;
                case 8:
                    //时间利用率 累计(充电时长+放电时长)/总枪数*24h(筛选日期内每天的数据求和)
                    List<SiteCountRecordEntity> timeRatioRecordList = siteCountRecordDao.findAllBySiteIdInAndCountDateBetween(siteIds, startLocalDate, endLocalDate);
                    result.getCurveDataMap().put(type, this.timeRatio(dateType, orderRecordMap, timeRatioRecordList, result.getDateList()));
                    break;
                case 9:
                    //充电时长(充电时长,直流充电时长,交流充电时长)
                    result.getCurveDataMap().put(type, this.chargeDuration(chargeOrderRecordMap, result.getDateList()));
                    break;
                case 10:
                    //度均服务费 累计充电服务费 / 总充电度数
                    result.getCurveDataMap().put(type, this.avgChargeFee(chargeOrderRecordMap, result.getDateList()));
                    break;
                case 11:
                    //功率利用率 累计(充电度数+放电度数) /全部充电桩额定功率之和×24h(筛选日期内每天的数据求和) *100%
                    List<SiteCountRecordEntity> powerRatioRecordList = siteCountRecordDao.findAllBySiteIdInAndCountDateBetween(siteIds, startLocalDate, endLocalDate);
                    result.getCurveDataMap().put(type, this.powerRatio(dateType, orderRecordMap, powerRatioRecordList, result.getDateList()));
                    break;
                case 12:
                    //一次充电成功率 筛选日期内创建的订单中，启动成功的订单数量(即排除启动失败、无效订单、挂起、充放电中、预约中)/总订单数量(不含进行中)*100%
                    Map<String, Integer> orderNormalNumMap = Maps.newHashMap();
                    List<ChargeOrderQtModel> orderNormalNumList = orderRecordMapper.countChargeOrderNormalNumList(siteIds, startTime, endTime, dateType);
                    if (CollectionUtils.isNotEmpty(orderNormalNumList)) {
                        orderNormalNumMap = orderNormalNumList.stream().collect(Collectors.toMap(ChargeOrderQtModel::getDataTime, ChargeOrderQtModel::getTotalCount, (k1, k2) -> k1));
                    }
                    Map<String, Integer> orderTotalNumMap = Maps.newHashMap();
                    List<ChargeOrderQtModel> orderTotalNumList = orderRecordMapper.countChargeOrderTotalNumList(siteIds, startTime, endTime, dateType);
                    if (CollectionUtils.isNotEmpty(orderTotalNumList)) {
                        orderTotalNumMap = orderTotalNumList.stream().collect(Collectors.toMap(ChargeOrderQtModel::getDataTime, ChargeOrderQtModel::getTotalCount, (k1, k2) -> k1));
                    }
                    result.getCurveDataMap().put(type, this.chargeSuccessRatio(orderNormalNumMap, orderTotalNumMap, result.getDateList()));
                    break;
            }
        }
        return ResponseResult.ok(result);
    }

    @Override
    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class)
    public ResponseResult<Void> addAllSiteCountRecord(String startDate, String endDate) {
        List<String> dateList = DateUtil.getDateBetween(1, startDate, endDate);
        //查询所有站点
        List<SiteInfoDto> siteInfoList = deviceService.findAllSiteBasicInfoList(null).getData();
        if (CollectionUtils.isNotEmpty(siteInfoList)) {
            //根据多个站点id查询设备电桩信息
            List<String> siteIds = siteInfoList.stream().map(SiteInfoDto::getId).collect(Collectors.toList());
            Map<String, List<DeviceBasicInfoDto>> devicePileMap = deviceService.findDeviceBasicInfoBySiteIds(siteIds, DeviceTypeParamVo.CDZ).getData();
            List<String> deviceIds = devicePileMap.entrySet().stream().flatMap(s -> s.getValue().stream().map(DeviceBasicInfoDto::getId))
                    .distinct().collect(Collectors.toList());
            //根据多个设备id查询电枪信息
            Map<String, List<DeviceGunInfoDto>> deviceGunMap = deviceService.findDeviceGunInfoByDeviceIds(deviceIds).getData();

            List<SiteCountRecordEntity> resultList = Lists.newArrayList();
            //对数据进行组装
            dateList.forEach(date -> resultList.addAll(siteIds.stream().map(siteId -> {
                SiteCountRecordEntity result = new SiteCountRecordEntity();
                result.setSiteId(siteId);
                if (devicePileMap.containsKey(siteId)) {
                    List<DeviceBasicInfoDto> deviceList = devicePileMap.get(siteId);
                    //电桩总额定功率
                    result.setTotalPilePower(deviceList.stream().mapToDouble(device -> {
                        Map<String, Object> reaMap = device.getReaMap();
                        //额定功率
                        if (MapUtils.isNotEmpty(reaMap) && reaMap.containsKey(ReaFieldParamVo.RATED_POWER) && StringUtil.isNotEmpty(reaMap.get(ReaFieldParamVo.RATED_POWER))) {
                            return Double.parseDouble(String.valueOf(reaMap.get(ReaFieldParamVo.RATED_POWER)));
                        }
                        return 0;
                    }).sum());
                    //总电桩枪数
                    result.setTotalGunNum(deviceList.stream().mapToInt(device -> {
                        if (deviceGunMap.containsKey(device.getId())) {
                            return deviceGunMap.get(device.getId()).size();
                        }
                        return 0;
                    }).sum());
                    //交流电桩枪数
                    result.setAcGunNum(deviceList.stream().filter(d -> Objects.equals(d.getTypeId(), "28")).mapToInt(device -> {
                        if (deviceGunMap.containsKey(device.getId())) {
                            return deviceGunMap.get(device.getId()).size();
                        }
                        return 0;
                    }).sum());
                    //直流电桩枪数
                    result.setDcGunNum(deviceList.stream().filter(d -> Objects.equals(d.getTypeId(), "29") ||
                            Objects.equals(d.getTypeId(), "30")).mapToInt(device -> {
                        if (deviceGunMap.containsKey(device.getId())) {
                            return deviceGunMap.get(device.getId()).size();
                        }
                        return 0;
                    }).sum());
                }
                result.setCountDate(DateUtil.strToLocalDate(date));
                result.setCreateTime(LocalDateTime.now());
                return result;
            }).collect(Collectors.toList())));

            siteCountRecordDao.saveAll(resultList);
            return ResponseResult.ok();
        }
        return ResponseResult.paramError(ResponseResult.PARAM_ERROR);
    }

    @Override
    public ResponseResult<PvOperationAnalysisDto> countPvOperationAnalysis(PvOperationAnalysisVo pvAnalysisVo) {
        //返回的对象
        PvOperationAnalysisDto result = new PvOperationAnalysisDto();
        //校验入参
        if (StringUtil.isEmpty(pvAnalysisVo.getSiteIds()) || StringUtil.isEmpty(pvAnalysisVo.getStartDate())
                || StringUtil.isEmpty(pvAnalysisVo.getEndDate()) || StringUtil.isEmpty(pvAnalysisVo.getDateType())) {
            return ResponseResult.paramError(ResponseResult.PARAM_ERROR);
        }

        List<String> siteIds = JSON.parseArray(pvAnalysisVo.getSiteIds(), String.class);
        String startTime = DateUtil.getDayStart(pvAnalysisVo.getStartDate());
        String endTime = DateUtil.getDayEnd(pvAnalysisVo.getEndDate());
        //时间维度 1-月 2-年 3-生命周期
        Integer dateType = pvAnalysisVo.getDateType();
        List<String> dateList = Lists.newArrayList();
        String functionLogo = null;
        String timeInterval = null;
        switch (dateType) {
            case 1: //月 按日统计
                timeInterval = "1d";
                functionLogo = FunctionLogoParamVo.TOTAL_POWER_GENERATION;
                dateList = DateUtil.getDateBetween(dateType, pvAnalysisVo.getStartDate(), pvAnalysisVo.getEndDate());
                break;
            case 2: //年 按月统计
                timeInterval = "1n";
                functionLogo = FunctionLogoParamVo.TOTAL_POWER_GENERATION;
                dateList = DateUtil.getDateBetween(dateType, pvAnalysisVo.getStartDate(), pvAnalysisVo.getEndDate());
                break;
            case 3: //生命周期 按年统计
                timeInterval = "1y";
                functionLogo = FunctionLogoParamVo.TOTAL_POWER_GENERATION;
                dateList = DateUtil.getDateBetween(dateType, pvAnalysisVo.getStartDate(), pvAnalysisVo.getEndDate());
                break;
        }
        result.setDateList(dateList);
        //根据多个站点id查询站点数据
        Map<String, SiteInfoDto> siteInfoMap = deviceService.findSiteBasicInfoByIds(siteIds).getData();
        if (MapUtils.isNotEmpty(siteInfoMap)) {
            List<SiteInfoDto> siteInfoList = Lists.newArrayList(siteInfoMap.values());
            siteIds = siteInfoList.stream().map(SiteInfoDto::getId).collect(Collectors.toList());
            //根据多个站点id查询站点设置数据
            Map<String, SiteSetUpDto> siteSetUpMap = deviceService.findSiteSetUpBySiteIds(siteIds).getData();
            //根据多个站点id查询设备数据
            Map<String, List<DeviceBasicInfoDto>> siteDeviceMap = deviceService.findDeviceBasicInfoBySiteIds(siteIds, null).getData();

            //统计数据
            //1.光伏装机容量
            Map<String, Double> sitePvCapMap = Maps.newHashMap();
            result.setCapacity(DoubleUtil.getToDouble(siteInfoList.stream().mapToDouble(siteInfo -> {
                double pvCapacity = 0.0;
                //光伏站装机量
                if (StringUtil.isNotEmpty(siteInfo.getScenarioTypes()) && siteInfo.getScenarioTypes().contains("1") && CollectionUtils.isNotEmpty(siteInfo.getSiteScenarioTypeDtos())) {
                    List<SiteScenarioTypeDto> siteScenarioTypeList = siteInfo.getSiteScenarioTypeDtos().stream().filter(s -> s.getScenarioType().equals(1))
                            .collect(Collectors.toList());
                    pvCapacity = getToDouble(siteScenarioTypeList.stream().mapToDouble(s -> {
                        if (StringUtil.isNotEmpty(s.getReadwriteObject())) {
                            return getToDouble(JSON.parseObject(s.getReadwriteObject()).getDoubleValue(SiteFieldParamVo.PV_CAPACITY), 4);
                        }
                        return 0.0;
                    }).sum());
                }
                sitePvCapMap.put(siteInfo.getId(), DoubleUtil.getToDouble(pvCapacity));
                return pvCapacity;
            }).sum()));
            //2.逆变器数量
            result.setInverterNum((int) siteDeviceMap.entrySet().stream().flatMap(s -> s.getValue().stream())
                    .filter(s -> Objects.equals(s.getTypeId(), "20") || Objects.equals(s.getTypeId(), "77")).count());

            Map<String, PvSiteGenerationDto> siteGenerationMap = Maps.newHashMap();
            for (Map.Entry<String, List<DeviceBasicInfoDto>> entry : siteDeviceMap.entrySet()) {
                PvSiteGenerationDto pvSiteGeneration = new PvSiteGenerationDto();
                String siteId = entry.getKey();
                pvSiteGeneration.setSiteId(siteId);
                //站点名称
                if (siteInfoMap.containsKey(siteId)) {
                    pvSiteGeneration.setSiteName(siteInfoMap.get(siteId).getSiteName());
                }
                //站点装机量
                if (sitePvCapMap.containsKey(siteId)) {
                    pvSiteGeneration.setPvCapacity(sitePvCapMap.get(siteId));
                }
                List<DeviceBasicInfoDto> deviceList = entry.getValue();
                if (siteSetUpMap.containsKey(siteId)) {
                    Integer pvQtSource = siteSetUpMap.get(siteId).getPvQtSource();
                    List<String> deviceIds = Lists.newArrayList();
                    if (StringUtil.isNotEmpty(pvQtSource)) {
                        pvSiteGeneration.setPvQtSource(pvQtSource);
                        if (pvQtSource == 20) {
                            deviceIds = deviceList.stream().filter(d -> Objects.equals(d.getTypeId(), String.valueOf(pvQtSource))
                                    || Objects.equals(d.getTypeId(), "77")).map(DeviceBasicInfoDto::getId).collect(Collectors.toList());
                        } else {
                            deviceIds = deviceList.stream().filter(d -> Objects.equals(d.getTypeId(), String.valueOf(pvQtSource)))
                                    .map(DeviceBasicInfoDto::getId).collect(Collectors.toList());
                        }
                    }
                    pvSiteGeneration.setDeviceIds(deviceIds);
                }
                siteGenerationMap.put(siteId, pvSiteGeneration);
            }

            //3.发电量,上网电量,自用电量,损失电量

            //获取站点逆变器发电量(站点统计方式)
            //逆变器发电量 设备id -> (功能点标识 -> 功能点历史数据)
            Map<String, Map<String, List<NodeDifHistoryDto>>> inverterMap = Maps.newHashMap();
            Set<String> inverterIds = siteGenerationMap.values().stream().filter(s -> StringUtil.isNotEmpty(s.getPvQtSource())
                    && s.getPvQtSource() == 20).flatMap(s -> s.getDeviceIds().stream()).collect(Collectors.toSet());
            if (CollectionUtils.isNotEmpty(inverterIds)) {
                DeviceHistoryQueryVo deviceHistoryQueryVo = new DeviceHistoryQueryVo();
                deviceHistoryQueryVo.setDeviceIds(inverterIds);
                deviceHistoryQueryVo.setFunctionLogos(Collections.singleton(functionLogo));
                deviceHistoryQueryVo.setStartTime(startTime);
                deviceHistoryQueryVo.setEndTime(endTime);
                deviceHistoryQueryVo.setTimeInterval(timeInterval);
                //逆变器发电量 设备id -> (功能点标识 -> 功能点历史数据)
                inverterMap = dataService.findNodeDifHistoryListFeign(deviceHistoryQueryVo).getData();
            }

            //并网点发电量
            //根据站点id，查询计量节点光伏并网点关联计量设备信息
            Map<String, List<String>> siteNodeDeviceIdMap = deviceService.findSiteMeasureIdBySiteIds(siteIds, 0, 1).getData();
            Set<String> deviceIdSet = siteNodeDeviceIdMap.values().stream().flatMap(List::stream).collect(Collectors.toSet());

            //查询光伏并网点功能点数据
            DeviceHistoryQueryVo deviceQueryVo = new DeviceHistoryQueryVo();
            deviceQueryVo.setDeviceIds(deviceIdSet);
            deviceQueryVo.setFunctionLogos(Collections.singleton(FunctionLogoParamVo.TOTAL_POSITIVE_ACTIVE_ENERGY));
            deviceQueryVo.setStartTime(startTime);
            deviceQueryVo.setEndTime(endTime);
            deviceQueryVo.setTimeInterval("1d");
            Map<String, Map<String, List<NodeDifHistoryDto>>> nodeDifHistoryMap = dataService.findNodeDifHistoryListFeign(deviceQueryVo).getData();

            //光伏上网电量
            Double netGeneration = 0.0;
            Set<String> gatewayDeviceIds = siteDeviceMap.entrySet().stream().flatMap(s -> s.getValue().stream()).filter(s -> Objects
                    .equals(s.getTypeId(), "39")).map(DeviceBasicInfoDto::getId).collect(Collectors.toSet());
            //查询光伏关口表反向有功电量数据
            if (CollectionUtils.isNotEmpty(gatewayDeviceIds)) {
                DeviceHistoryQueryVo gatewayQueryVo = new DeviceHistoryQueryVo();
                gatewayQueryVo.setDeviceIds(gatewayDeviceIds);
                gatewayQueryVo.setFunctionLogos(Collections.singleton(FunctionLogoParamVo.TOTAL_INVERSE_ACTIVE_ENERGY));
                gatewayQueryVo.setStartTime(startTime);
                gatewayQueryVo.setEndTime(endTime);
                gatewayQueryVo.setTimeInterval(timeInterval);
                netGeneration = getToDouble(dataService.findNodeDifHistoryListFeign(gatewayQueryVo).getData().entrySet().stream()
                        .flatMap(s -> s.getValue().entrySet().stream().flatMap(d -> d.getValue().stream()))
                        .filter(d -> StringUtil.isNotEmpty(d.getFirstDataValue()) && StringUtil.isNotEmpty(d.getLastDataValue()))
                        .mapToDouble(d -> DoubleUtil.objToDouble(d.getLastDataValue()) - DoubleUtil.objToDouble(d.getFirstDataValue())).sum());
            }
            result.setNetGeneration(netGeneration);

            //对发电量数据进行组装(统计发电量和等效发电时长)
            Map<String, Map<String, List<NodeDifHistoryDto>>> finalInverterMap = inverterMap;
            siteGenerationMap.forEach((siteId, siteGeneration) -> {
                if (StringUtil.isNotEmpty(siteGeneration.getPvQtSource())) {
                    //统计方式-逆变器发电量
                    if (siteGeneration.getPvQtSource() == 20) {
                        List<NodeDifHistoryDto> inverterValueList = Lists.newArrayList();
                        siteGeneration.getDeviceIds().forEach(deviceId -> {
                            if (finalInverterMap.containsKey(deviceId)) {
                                inverterValueList.addAll(finalInverterMap.get(deviceId).entrySet().stream().flatMap(s -> s.getValue().stream()
                                        .filter(i -> StringUtil.isNotEmpty(i.getFirstDateTime()))).collect(Collectors.toList()));
                            }
                        });
                        Map<String, Double> pvGenerationMap = inverterValueList.stream().collect(Collectors.groupingBy(i -> {
                            switch (dateType) {
                                case 1:
                                    return i.getFirstDateTime().substring(0, 10);
                                case 2:
                                    return i.getFirstDateTime().substring(0, 7);
                                case 3:
                                    return i.getFirstDateTime().substring(0, 4);
                                default:
                                    return i.getFirstDateTime();
                            }
                        }, Collectors.summingDouble(d -> DoubleUtil.getObjSub(d.getFirstDataValue(), d.getLastDataValue()))));
                        //发电量数据(日期 -> 发电量)
                        siteGeneration.setPvGenerationMap(pvGenerationMap);
                        pvGenerationMap.forEach((date, value) -> {
                            //光伏总发电量
                            siteGeneration.setPvGeneration(siteGeneration.getPvGeneration() + value);
                            double pvGenerationTime = 0.0;
                            if (siteGeneration.getPvCapacity() > 0.0) {
                                pvGenerationTime = value / siteGeneration.getPvCapacity();
                            }
                            siteGeneration.getPvGenerationTimeMap().put(date, DoubleUtil.getToDouble(pvGenerationTime, 4));
                            siteGeneration.setPvGenerationTime(siteGeneration.getPvGenerationTime() + DoubleUtil.getToDouble(pvGenerationTime, 4));
                        });
                    }
                    //统计方式-并网点发电量
                    if (siteGeneration.getPvQtSource() == 66 && siteNodeDeviceIdMap.containsKey(siteId) && CollectionUtils.isNotEmpty(siteNodeDeviceIdMap.get(siteId))) {
                        //查询站点光伏并网点数据
                        List<NodeDifHistoryDto> nodeDifHistoryList = siteNodeDeviceIdMap.get(siteId).stream()
                                .map(deviceId -> nodeDifHistoryMap.getOrDefault(deviceId, Maps.newHashMap()))
                                .flatMap(d -> d.getOrDefault(FunctionLogoParamVo.TOTAL_POSITIVE_ACTIVE_ENERGY, Lists.newArrayList()).stream())
                                .collect(Collectors.toList());
                        if (CollectionUtils.isNotEmpty(nodeDifHistoryList)) {
                            Map<String, Double> pvGenerationMap = nodeDifHistoryList.stream().filter(n -> StringUtil.isNotEmpty(n.getLastDataValue()) && StringUtil.isNotEmpty(n.getFirstDataValue()))
                                    .collect(Collectors.groupingBy(i -> {
                                        switch (dateType) {
                                            case 1:
                                                return i.getFirstDateTime().substring(0, 10);
                                            case 2:
                                                return i.getFirstDateTime().substring(0, 7);
                                            case 3:
                                                return i.getFirstDateTime().substring(0, 4);
                                            default:
                                                return i.getFirstDateTime();
                                        }
                                    }, Collectors.summingDouble(i -> Double.parseDouble(String.valueOf(i.getLastDataValue())) - Double.parseDouble(String.valueOf(i.getFirstDataValue())))));

                            //发电量数据(日期 -> 发电量)
                            siteGeneration.setPvGenerationMap(pvGenerationMap);
                            pvGenerationMap.forEach((date, value) -> {
                                //光伏总发电量
                                siteGeneration.setPvGeneration(siteGeneration.getPvGeneration() + value);
                                double pvGenerationTime = 0.0;
                                if (siteGeneration.getPvCapacity() > 0.0) {
                                    pvGenerationTime = value / siteGeneration.getPvCapacity();
                                }
                                siteGeneration.getPvGenerationTimeMap().put(date, DoubleUtil.getToDouble(pvGenerationTime, 4));
                                siteGeneration.setPvGenerationTime(siteGeneration.getPvGenerationTime() + DoubleUtil.getToDouble(pvGenerationTime, 4));
                            });
                        }
                    }

                    if (siteSetUpMap.containsKey(siteId)) {
                        SiteSetUpDto siteSetUp = siteSetUpMap.get(siteId);
                        //二氧化碳减排量(电站发电量 * CO₂减排转换系数(0.475))
                        siteGeneration.setCo2Reduction(siteGeneration.getPvGeneration() * siteSetUp.getReduceCoeff());
                        //节约标煤量(电站发电量 * 节约标准煤转换系数(0.4))
                        siteGeneration.setStandardCoalReduction(siteGeneration.getPvGeneration() * siteSetUp.getTceCoeff());
                        //等效植树量(二氧化碳减排量 / 等效植树量转换系数（18.3）/ 40)
                        if (siteGeneration.getCo2Reduction() != null && siteSetUp.getTreeCoeff() != null && siteSetUp.getTreeCoeff() != 0.0) {
                            siteGeneration.setTreeReduction(siteGeneration.getCo2Reduction() / siteSetUp.getTreeCoeff() / 40);
                        }
                    }
                }
            });


            //光伏理论发电量
            Map<String, Double> pvTyGnMap = Maps.newHashMap();
            Set<String> pvTyGnIds = siteGenerationMap.values().stream().filter(s -> StringUtil.isNotEmpty(s.getPvQtSource())
                    && s.getPvQtSource() == 65).flatMap(s -> s.getDeviceIds().stream()).collect(Collectors.toSet());
            if (CollectionUtils.isNotEmpty(pvTyGnIds)) {
                DeviceHistoryQueryVo deviceHistoryQueryVo = new DeviceHistoryQueryVo();
                deviceHistoryQueryVo.setDeviceIds(pvTyGnIds);
                deviceHistoryQueryVo.setFunctionLogos(Collections.singleton(FunctionLogoParamVo.PV_THEORY_CAPACITY));
                deviceHistoryQueryVo.setStartTime(startTime);
                deviceHistoryQueryVo.setEndTime(endTime);
                deviceHistoryQueryVo.setTimeInterval("1d");
                //光伏理论发电量 设备id -> (功能点标识 -> 功能点历史数据)
                pvTyGnMap = dataService.findNodeDifHistoryListFeign(deviceHistoryQueryVo).getData().entrySet().stream()
                        .flatMap(t -> t.getValue().entrySet().stream().flatMap(s -> s.getValue().stream()))
                        .collect(Collectors.groupingBy(i -> {
                            switch (dateType) {
                                case 1:
                                    return i.getFirstDateTime().substring(0, 10);
                                case 2:
                                    return i.getFirstDateTime().substring(0, 7);
                                case 3:
                                    return i.getFirstDateTime().substring(0, 4);
                                default:
                                    return i.getFirstDateTime();
                            }
                        }, Collectors.summingDouble(i -> StringUtil.isNotEmpty(i.getLastDataValue()) ? Double.parseDouble(String.valueOf(i.getLastDataValue())) : 0.0)));
            }

            for (String date : dateList) {
                //光伏发电量
                double generation = siteGenerationMap.values().stream().filter(s -> s.getPvGenerationMap().containsKey(date))
                        .mapToDouble(s -> s.getPvGenerationMap().get(date)).sum();
                result.getGenerationList().add(DoubleUtil.getToDouble(generation, 4));
                //等效发电时长
                double pvGenerationTime = siteGenerationMap.values().stream().filter(s -> s.getPvGenerationTimeMap().containsKey(date))
                        .mapToDouble(s -> s.getPvGenerationTimeMap().get(date)).sum();
                result.getGenerationTimeList().add(DoubleUtil.getToDouble(pvGenerationTime, 4));

                //系统效率PR数据和损失电量数据
                Double pr = null;
                Double lossGeneration = 0.0;
                if (pvTyGnMap.containsKey(date)) {
                    //光伏理论发电量
                    Double pvTyGn = pvTyGnMap.get(date);
                    if (pvTyGn > 0.0) {
                        //系统效率PR数据(发电量/理论发电量*100%)
                        pr = DoubleUtil.getToDouble(generation / pvTyGn * 100);
                    }
                    if (pr != null) {
                        //损失电量数据(理论发电量 * 系统效率-实际发电量)
                        lossGeneration = DoubleUtil.getToDouble(pvTyGn * pr / 100 - generation, 4);
                    }
                }
                result.getPrList().add(pr);
                result.getLossGenerationList().add(lossGeneration);
            }

            //光伏总发电量
            result.setGeneration(DoubleUtil.getToDouble(result.getGenerationList().stream().mapToDouble(g -> g).sum(), 4));

            //光伏自用电量 自用电量（消纳电量）=光伏总发电量-上网电量
            if (StringUtil.isNotEmpty(result.getGeneration()) && StringUtil.isNotEmpty(result.getNetGeneration())) {
                result.setSelfGeneration(DoubleUtil.getToDouble(result.getGeneration() - result.getNetGeneration()));
            }

            //光伏总损失电量
            result.setLossGeneration(DoubleUtil.getToDouble(result.getLossGenerationList().stream().mapToDouble(g -> g).sum(), 4));

            //电站排名(等效发电时长) 前10个
            List<PvOperationAnalysisDto.Ranking> rankingList = siteGenerationMap.values().stream().sorted(Comparator
                    .comparing(PvSiteGenerationDto::getPvGenerationTime).reversed()).map(s -> {
                PvOperationAnalysisDto.Ranking ranking = new PvOperationAnalysisDto.Ranking();
                ranking.setSiteName(s.getSiteName());
                ranking.setTotalGenerationTime(s.getPvGenerationTime());
                return ranking;
            }).collect(Collectors.toList());
            result.setRankingList(rankingList.subList(0, Math.min(rankingList.size(), 10)));

            //社会贡献
            //二氧化碳减排量(电站发电量 * CO₂减排转换系数(0.475))
            result.setCo2Reduction(DoubleUtil.getToDouble(siteGenerationMap.values().stream().filter(s -> StringUtil.isNotEmpty(s.getCo2Reduction()))
                    .mapToDouble(PvSiteGenerationDto::getCo2Reduction).sum(), 4));
            //节约标煤量(电站发电量 * 节约标准煤转换系数(0.4))
            result.setStandardCoalReduction(DoubleUtil.getToDouble(siteGenerationMap.values().stream().filter(s -> StringUtil.isNotEmpty(s.getStandardCoalReduction()))
                    .mapToDouble(PvSiteGenerationDto::getStandardCoalReduction).sum(), 4));
            //等效植树量(二氧化碳减排量 / 等效植树量转换系数（18.3）/ 40)
            result.setTreeReduction(DoubleUtil.getToDouble(siteGenerationMap.values().stream().filter(s -> StringUtil.isNotEmpty(s.getTreeReduction()))
                    .mapToDouble(PvSiteGenerationDto::getTreeReduction).sum(), 4));

        }
        return ResponseResult.ok(result);
    }

    @Override
    public ResponseResult<Map<String, OperationOverviewDto>> countOperationOverviewBySiteId(OperationOverviewVo operationOverviewVo) {
        //返回的对象
        Map<String, OperationOverviewDto> resultMap = Maps.newHashMap();

        List<String> siteIds = JSON.parseArray(operationOverviewVo.getSiteIds(), String.class);
        String startTime = DateUtil.getDayStart(operationOverviewVo.getStartDate());
        String endTime = DateUtil.getDayEnd(operationOverviewVo.getEndDate());
        //获取当前统计数据
        Map<String, OverviewDto> overviewMap = this.getSiteOverviewMap(siteIds, startTime, endTime);
        if (MapUtils.isNotEmpty(overviewMap)) {
            //查询上一个周期的数据
            Map<String, OverviewDto> beforeOverviewMap = Maps.newHashMap();
            String beforeStartDate = operationOverviewVo.getBeforeStartDate();
            String beforeEndDate = operationOverviewVo.getBeforeEndDate();
            if (StringUtil.isNotEmpty(beforeStartDate) && StringUtil.isNotEmpty(beforeEndDate)) {
                String beforeStartTime = DateUtil.getDayStart(beforeStartDate);
                String beforeEndTime = DateUtil.getDayEnd(beforeEndDate);
                beforeOverviewMap = this.getSiteOverviewMap(siteIds, beforeStartTime, beforeEndTime);
            }

            for (Map.Entry<String, OverviewDto> entry : overviewMap.entrySet()) {
                String siteId = entry.getKey();
                OverviewDto thisOverview = entry.getValue();
                OperationOverviewDto result = new OperationOverviewDto();
                BeanUtils.copyProperties(thisOverview, result);
                if (MapUtils.isNotEmpty(beforeOverviewMap) && beforeOverviewMap.containsKey(siteId)) {
                    OverviewDto beforeOverview = beforeOverviewMap.get(siteId);
                    result.setChargeOrderMoneyRatio(this.calculateOverviewOver(beforeOverview.getChargeOrderMoney(), thisOverview.getChargeOrderMoney()));
                    result.setChargePayMoneyRatio(this.calculateOverviewOver(beforeOverview.getChargePayMoney(), thisOverview.getChargePayMoney()));
                    result.setChargeOrderQtRatio(this.calculateOverviewOver(beforeOverview.getChargeOrderQt(), thisOverview.getChargeOrderQt()));
                    result.setChargeOrderNumRatio(this.calculateOverviewOver(beforeOverview.getChargeOrderNum(), thisOverview.getChargeOrderNum()));
                    result.setDischargeOrderMoneyRatio(this.calculateOverviewOver(beforeOverview.getDischargeOrderMoney(), thisOverview.getDischargeOrderMoney()));
                    result.setDischargeOrderQtRatio(this.calculateOverviewOver(beforeOverview.getDischargeOrderQt(), thisOverview.getDischargeOrderQt()));
                    result.setAvgChargeQtRatio(this.calculateOverviewOver(beforeOverview.getAvgChargeQt(), thisOverview.getAvgChargeQt()));
                    result.setTimeRatioRatio(this.calculateOverviewOver(beforeOverview.getTimeRatio(), thisOverview.getTimeRatio()));
                    result.setChargeDurationRatio(this.calculateOverviewOver(beforeOverview.getChargeDuration(), thisOverview.getChargeDuration()));
                    result.setAvgChargeFeeRatio(this.calculateOverviewOver(beforeOverview.getAvgChargeFee(), thisOverview.getAvgChargeFee()));
                    result.setPowerRatioRatio(this.calculateOverviewOver(beforeOverview.getPowerRatio(), thisOverview.getPowerRatio()));
                    result.setChargeSuccessRatioRatio(this.calculateOverviewOver(beforeOverview.getChargeSuccessRatio(), thisOverview.getChargeSuccessRatio()));
                }
                resultMap.put(siteId, result);
            }
        }
        return ResponseResult.ok(resultMap);
    }

    @Override
    public ResponseResult<Map<String, PileMonitorDataDto>> countPileMonitorData(List<String> dataIds, Integer type) {
        Map<String, PileMonitorDataDto> resultMap = Maps.newHashMap();
        String startTime = DateUtil.getDayStart(DateUtil.localDateToStr(LocalDate.now()));
        String endTime = DateUtil.localDateTimeToStr(LocalDateTime.now());
        if (type == 1) {
            //站点级
            this.getSiteOverviewMap(dataIds, startTime, endTime).forEach((siteId, overviewDto) -> {
                PileMonitorDataDto result = new PileMonitorDataDto();
                BeanUtils.copyProperties(overviewDto, result);
                resultMap.put(siteId, result);
            });
        }
        if (type == 2) {
            //系统级
            this.getSystemOverviewMap(dataIds, startTime, endTime).forEach((siteId, overviewDto) -> {
                PileMonitorDataDto result = new PileMonitorDataDto();
                BeanUtils.copyProperties(overviewDto, result);
                resultMap.put(siteId, result);
            });
        }
        return ResponseResult.ok(resultMap);
    }

    private List<OrderRecordEntity> getOrderRecordList(List<String> siteIds, String startTime, String endTime) {
        return orderRecordDao.findAll((Specification<OrderRecordEntity>) (root, cq, cb) -> {
            List<Predicate> predicates = Lists.newArrayList();
            predicates.add(cb.in(root.get("siteId")).value(siteIds));
            predicates.add(cb.between(root.get("endTime"), startTime, endTime));
            predicates.add(cb.isNotNull(root.get("runMode")));
//            predicates.add(cb.isNull(root.get("abnormalCode")));
            predicates.add(cb.or(cb.isNull(root.get("abnormalCode")), cb.equal(root.get("abnormalCode"), "[5]")));
            predicates.add(cb.equal(root.get("orderStatus"), "2"));
            return cb.and(predicates.toArray(new Predicate[0]));
        });
    }


    //封装统计数据
    private OverviewDto getOverview(List<String> siteIds, String startTime, String endTime) {
        //返回的对象
        OverviewDto result = new OverviewDto();
        List<OrderRecordEntity> orderRecordList = this.getOrderRecordList(siteIds, startTime, endTime);

        //统计数据
        //1.充电订单金额(元) 筛选日期内创建的充电订单中，累计“订单金额之和”，未扣除优惠减免等费用。
        //2.充电实付金额(元) 筛选日期内创建的充电订单中，累计“实付金额之和”。
        //3.充电电量(度) 筛选日期内创建的充电订单中，累计充电度数总和
        //4.充电订单数量(笔) 筛选日期内创建的充电订单累计数量，排除异常订单和启动失败的订单
        //5.V2G放电订单金额(元) 筛选日期内创建的放电订单中，累计“订单金额之和”。
        //6.V2G放电电量(度) 筛选日期内创建的放电订单中，累计放电度数总和
        //7.枪均电量(度) 筛选日期内创建的订单中，累计充电度数 / 筛选日期内平均枪数
        //8.时间利用率(%) 筛选日期内创建的订单中，累计(充电时长+放电时长)/总枪数*24h(筛选日期内每天的数据求和)
        //9.充电时长(小时) 筛选日期内创建的充电订单，累计充电时长
        //10.度均服务费(元) 筛选日期内创建的订单中，累计充电服务费 / 总充电度数
        //11.功率利用率(%) 筛选日期内创建的订单中，累计(充电度数+放电度数)/全部充电桩额定功率之和*24h(筛选日期内每天的数据求和)
        //12.一次充电成功率(%) 筛选日期内创建的订单中，启动成功的订单数量(即排除启动失败、无效订单、挂起、充放电中、预约中)/总订单数量(不含进行中)*100%

        //充电订单
        List<OrderRecordEntity> chargeOrderList = orderRecordList.stream().filter(s -> s.getRunMode() == 0).collect(Collectors.toList());

        //1.充电订单金额(元) 筛选日期内创建的充电订单中，累计“订单金额之和”，未扣除优惠减免等费用。
        result.setChargeOrderMoney(DoubleUtil.getToBigDecimal(chargeOrderList.stream().map(OrderRecordEntity::getTotalCost)
                .filter(StringUtil::isNotEmpty).reduce(BigDecimal.ZERO, BigDecimal::add)));

        //2.充电实付金额(元) 筛选日期内创建的充电订单中，累计“实付金额之和”。
        List<String> orderNums = chargeOrderList.stream().map(OrderRecordEntity::getOrderNum).distinct().collect(Collectors.toList());
        result.setChargePayMoney(DoubleUtil.getToBigDecimal(settlementRecordDao.findAllByOrderNumInAndPayWay(orderNums, 2).stream()
                .map(SettlementRecordEntity::getActualTotalCost).filter(StringUtil::isNotEmpty).reduce(BigDecimal.ZERO, BigDecimal::add)));

        //3.充电电量(度) 筛选日期内创建的充电订单中，累计充电度数总和
        result.setChargeOrderQt(DoubleUtil.getToDouble(chargeOrderList.stream().mapToDouble(OrderRecordEntity::getTotalQt).sum()));

        //4.充电订单数量(笔) 筛选日期内创建的充电订单累计数量，排除异常订单和启动失败的订单
        result.setChargeOrderNum(chargeOrderList.size());

        //放电订单
        List<OrderRecordEntity> dischargeOrderList = orderRecordList.stream().filter(s -> s.getRunMode() == 1 || s.getRunMode() == 2).collect(Collectors.toList());

        //5.V2G放电订单金额(元) 筛选日期内创建的放电订单中，累计“订单金额之和”。
        result.setDischargeOrderMoney(DoubleUtil.getToBigDecimal(dischargeOrderList.stream().map(OrderRecordEntity::getTotalCost)
                .filter(StringUtil::isNotEmpty).reduce(BigDecimal.ZERO, BigDecimal::add)));

        //6.V2G放电电量(度) 筛选日期内创建的放电订单中，累计放电度数总和
        result.setDischargeOrderQt(DoubleUtil.getToDouble(dischargeOrderList.stream().mapToDouble(OrderRecordEntity::getTotalQt).sum()));

        //根据多个站点id和时间查询站点充电桩额定功率和枪数量数据
        Double totalPilePower = 0.0;
        Integer totalGunNum = 0;
        Map<LocalDate, Integer> totalGunMap = Maps.newHashMap();
        double avgGunNum = 0.0;
        LocalDate startDate = DateUtil.strToLocalDateTime(startTime).toLocalDate();
        LocalDate endDate = DateUtil.strToLocalDateTime(endTime).toLocalDate();
        List<SiteCountRecordEntity> siteCountRecordList = siteCountRecordDao.findAllBySiteIdInAndCountDateBetween(siteIds, startDate, endDate);
        if (CollectionUtils.isNotEmpty(siteCountRecordList)) {
            for (SiteCountRecordEntity siteCountRecord : siteCountRecordList) {
                totalPilePower += siteCountRecord.getTotalPilePower();
                totalGunNum += siteCountRecord.getTotalGunNum();
            }
            totalGunMap = siteCountRecordList.stream().collect(Collectors.groupingBy(SiteCountRecordEntity::getCountDate,
                    Collectors.summingInt(SiteCountRecordEntity::getTotalGunNum)));
            avgGunNum = (double) totalGunNum / totalGunMap.size();
        }

        //7.枪均电量(度) 筛选日期内创建的订单中，累计充电度数 / 筛选日期内平均枪数
        if (avgGunNum != 0) {
            //过滤掉日期列表中没有枪数的订单
            Map<LocalDate, Integer> finalTotalGunMap = totalGunMap;
            double chargeOrderQt = chargeOrderList.stream().filter(o -> StringUtil.isNotEmpty(o.getTotalQt()) && StringUtil.isNotEmpty(o.getEndTime())
                            && finalTotalGunMap.containsKey(DateUtil.strToLocalDateTime(o.getEndTime()).toLocalDate()))
                    .mapToDouble(OrderRecordEntity::getTotalQt).sum();
            result.setAvgChargeQt(DoubleUtil.getToDouble(chargeOrderQt / avgGunNum));
        }

        //8.时间利用率(%) 筛选日期内创建的订单中，累计（充电时长+放电时长）／(总枪数（筛选日期内平均）*24h) * 100%
        if (totalGunNum != 0) {
            Map<LocalDate, Integer> finalTotalGunMap = totalGunMap;
            double chargeDuration = orderRecordList.stream().filter(o -> StringUtil.isNotEmpty(o.getEndTime()) &&
                    finalTotalGunMap.containsKey(DateUtil.strToLocalDateTime(o.getEndTime()).toLocalDate())).mapToDouble(c -> {
                if (StringUtil.isNotEmpty(c.getStartTime()) && StringUtil.isNotEmpty(c.getEndTime())) {
                    LocalDateTime startLocalTime = DateUtil.strToLocalDateTime(c.getStartTime());
                    LocalDateTime endLocalTime = DateUtil.strToLocalDateTime(c.getEndTime());
                    return (double) DateUtil.compareDiffBetweenMinutes(startLocalTime, endLocalTime) / 60;
                }
                return 0.0;
            }).sum();
            result.setTimeRatio(DoubleUtil.getToDouble((chargeDuration / (totalGunNum * 24) * 100)));
        }

        //9.充电时长(小时) 筛选日期内创建的充电订单，累计充电时长
        result.setChargeDuration(DoubleUtil.getToDouble(chargeOrderList.stream().mapToDouble(c -> {
            if (StringUtil.isNotEmpty(c.getStartTime()) && StringUtil.isNotEmpty(c.getEndTime())) {
                LocalDateTime startLocalTime = DateUtil.strToLocalDateTime(c.getStartTime());
                LocalDateTime endLocalTime = DateUtil.strToLocalDateTime(c.getEndTime());
                return (double) DateUtil.compareDiffBetweenMinutes(startLocalTime, endLocalTime) / 60;
            }
            return 0.0;
        }).sum()));

        //10.度均服务费(元) 筛选日期内创建的订单中，累计充电服务费 / 总充电度数
        if (result.getChargeOrderQt() != 0) {
            BigDecimal totalFee = chargeOrderList.stream().map(OrderRecordEntity::getTotalFee).filter(StringUtil::isNotEmpty).reduce(BigDecimal.ZERO, BigDecimal::add);
            result.setAvgChargeFee(DoubleUtil.getToBigDecimal(totalFee.divide(BigDecimal.valueOf(result.getChargeOrderQt()), 2, RoundingMode.HALF_UP)));
        }

        //11.功率利用率(%) 筛选日期内创建的订单中，累计（充电度数+放电度数）／全部充电桩额定功率之和（筛选日期内平均）*24h
        if (totalPilePower != 0) {
            Map<LocalDate, Integer> finalTotalGunMap = totalGunMap;
            double orderTotalQt = orderRecordList.stream().filter(o -> StringUtil.isNotEmpty(o.getTotalQt()) && StringUtil.isNotEmpty(o.getEndTime())
                            && finalTotalGunMap.containsKey(DateUtil.strToLocalDateTime(o.getEndTime()).toLocalDate()))
                    .mapToDouble(OrderRecordEntity::getTotalQt).sum();
            result.setPowerRatio(DoubleUtil.getToDouble(orderTotalQt / (totalPilePower * 24) * 100));
        }

        //12.一次充电成功率(%) 筛选日期内创建的订单中，正常订单数量/总订单数量*100%
        Integer normalNum = orderRecordMapper.countChargeOrderNormalNum(siteIds, startTime, endTime);
        Integer totalNum = orderRecordMapper.countChargeOrderTotalNum(siteIds, startTime, endTime);
        if (totalNum != 0) {
            result.setChargeSuccessRatio(DoubleUtil.getToDouble((double) normalNum / totalNum * 100));
        }
        return result;
    }

    private Map<String, OverviewDto> getSiteOverviewMap(List<String> siteIds, String startTime, String endTime) {
        //返回的对象
        Map<String, OverviewDto> resultMap = Maps.newHashMap();
        //获取订单记录数据
        List<OrderRecordEntity> orderRecordList = this.getOrderRecordList(siteIds, startTime, endTime);
        //充电订单
        Map<String, List<OrderRecordEntity>> chargeOrderMap = orderRecordList.stream().filter(s -> s.getRunMode() == 0)
                .collect(Collectors.groupingBy(OrderRecordEntity::getSiteId));
        //放电订单
        Map<String, List<OrderRecordEntity>> dischargeMap = orderRecordList.stream().filter(s -> s.getRunMode() == 1 || s.getRunMode() == 2)
                .collect(Collectors.groupingBy(OrderRecordEntity::getSiteId));
        //查询充电结算实付金额
        List<String> orderNums = chargeOrderMap.values().stream().flatMap(c -> c.stream()
                .map(OrderRecordEntity::getOrderNum)).distinct().collect(Collectors.toList());
        Map<String, BigDecimal> orderActualCostMap = settlementRecordDao.findAllByOrderNumInAndPayWay(orderNums, 2).stream().collect(Collectors
                .toMap(SettlementRecordEntity::getOrderNum, SettlementRecordEntity::getActualTotalCost, (k1, k2) -> k1));

        //根据多个站点id和时间查询站点充电桩额定功率和枪数量数据
        LocalDate startDate = DateUtil.strToLocalDateTime(startTime).toLocalDate();
        LocalDate endDate = DateUtil.strToLocalDateTime(endTime).toLocalDate();
        Map<String, List<SiteCountRecordEntity>> siteCountMap = siteCountRecordDao.findAllBySiteIdInAndCountDateBetween(siteIds, startDate, endDate).stream()
                .collect(Collectors.groupingBy(SiteCountRecordEntity::getSiteId));

        //统计充电订单正常数量(排除进行中的,启动失败,未进行，订单挂起，无效订单的订单)
        Map<Object, Object> normalNumMap = orderRecordMapper.countChargeOrderNormalNumBySiteIds(siteIds, startTime, endTime).stream()
                .collect(Collectors.toMap(c -> c.get("siteId"), c -> c.get("totalCount"), (k1, k2) -> k1));
        //统计充电订单总数量
        Map<Object, Object> totalNumMap = orderRecordMapper.countChargeOrderTotalNumBySiteIds(siteIds, startTime, endTime).stream()
                .collect(Collectors.toMap(c -> c.get("siteId"), c -> c.get("totalCount"), (k1, k2) -> k1));

        for (String siteId : siteIds) {
            OverviewDto result = new OverviewDto();
            //统计数据
            //1.充电订单金额(元) 筛选日期内创建的充电订单中，累计“订单金额之和”，未扣除优惠减免等费用。
            //2.充电实付金额(元) 筛选日期内创建的充电订单中，累计“实付金额之和”。
            //3.充电电量(度) 筛选日期内创建的充电订单中，累计充电度数总和
            //4.充电订单数量(笔) 筛选日期内创建的充电订单累计数量，排除异常订单和启动失败的订单
            //5.V2G放电订单金额(元) 筛选日期内创建的放电订单中，累计“订单金额之和”。
            //6.V2G放电电量(度) 筛选日期内创建的放电订单中，累计放电度数总和
            //7.枪均电量(度) 筛选日期内创建的订单中，累计充电度数 / 筛选日期内平均枪数
            //8.时间利用率(%) 筛选日期内创建的订单中，累计(充电时长+放电时长)/总枪数*24h(筛选日期内每天的数据求和)
            //9.充电时长(小时) 筛选日期内创建的充电订单，累计充电时长
            //10.度均服务费(元) 筛选日期内创建的订单中，累计充电服务费 / 总充电度数
            //11.功率利用率(%) 筛选日期内创建的订单中，累计(充电度数+放电度数)/全部充电桩额定功率之和*24h(筛选日期内每天的数据求和)
            //12.一次充电成功率(%) 筛选日期内创建的订单中，启动成功的订单数量(即排除启动失败、无效订单、挂起、充放电中、预约中)/总订单数量(不含进行中)*100%

            //充电订单
            List<OrderRecordEntity> chargeOrderList = Lists.newArrayList();
            if (chargeOrderMap.containsKey(siteId)) {
                chargeOrderList = chargeOrderMap.get(siteId);
            }

            //1.充电订单金额(元) 筛选日期内创建的充电订单中，累计“订单金额之和”，未扣除优惠减免等费用。
            result.setChargeOrderMoney(DoubleUtil.getToBigDecimal(chargeOrderList.stream().map(OrderRecordEntity::getTotalCost)
                    .filter(StringUtil::isNotEmpty).reduce(BigDecimal.ZERO, BigDecimal::add)));

            //2.充电实付金额(元) 筛选日期内创建的充电订单中，累计“实付金额之和”。
            result.setChargePayMoney(DoubleUtil.getToBigDecimal(chargeOrderList.stream().map(c -> {
                if (orderActualCostMap.containsKey(c.getOrderNum())) {
                    return orderActualCostMap.get(c.getOrderNum());
                }
                return BigDecimal.ZERO;
            }).filter(StringUtil::isNotEmpty).reduce(BigDecimal.ZERO, BigDecimal::add)));

            //3.充电电量(度) 筛选日期内创建的充电订单中，累计充电度数总和
            result.setChargeOrderQt(DoubleUtil.getToDouble(chargeOrderList.stream().mapToDouble(OrderRecordEntity::getTotalQt).sum()));

            //4.充电订单数量(笔) 筛选日期内创建的充电订单累计数量，排除异常订单和启动失败的订单
            result.setChargeOrderNum(chargeOrderList.size());

            //放电订单
            List<OrderRecordEntity> dischargeOrderList = Lists.newArrayList();
            if (dischargeMap.containsKey(siteId)) {
                dischargeOrderList = dischargeMap.get(siteId);
            }

            //5.V2G放电订单金额(元) 筛选日期内创建的放电订单中，累计“订单金额之和”。
            result.setDischargeOrderMoney(DoubleUtil.getToBigDecimal(dischargeOrderList.stream().map(OrderRecordEntity::getTotalCost)
                    .filter(StringUtil::isNotEmpty).reduce(BigDecimal.ZERO, BigDecimal::add)));

            //6.V2G放电电量(度) 筛选日期内创建的放电订单中，累计放电度数总和
            result.setDischargeOrderQt(DoubleUtil.getToDouble(dischargeOrderList.stream().mapToDouble(OrderRecordEntity::getTotalQt).sum()));

            //查询站点充电桩额定功率和枪数量数据
            Double totalPilePower = 0.0;
            Integer totalGunNum = 0;
            Map<LocalDate, Integer> totalGunMap = Maps.newHashMap();
            double avgGunNum = 0.0;
            if (siteCountMap.containsKey(siteId) && CollectionUtils.isNotEmpty(siteCountMap.get(siteId))) {
                List<SiteCountRecordEntity> siteCountRecordList = siteCountMap.get(siteId);
                for (SiteCountRecordEntity siteCountRecord : siteCountRecordList) {
                    totalPilePower += siteCountRecord.getTotalPilePower();
                    totalGunNum += siteCountRecord.getTotalGunNum();
                }
                totalGunMap = siteCountRecordList.stream().collect(Collectors.groupingBy(SiteCountRecordEntity::getCountDate,
                        Collectors.summingInt(SiteCountRecordEntity::getTotalGunNum)));
                avgGunNum = (double) totalGunNum / totalGunMap.size();
            }

            //7.枪均电量(度) 筛选日期内创建的订单中，累计充电度数 / 筛选日期内平均枪数
            if (avgGunNum != 0) {
                //过滤掉日期列表中没有枪数的订单
                Map<LocalDate, Integer> finalTotalGunMap = totalGunMap;
                double chargeOrderQt = chargeOrderList.stream().filter(o -> StringUtil.isNotEmpty(o.getTotalQt()) && StringUtil.isNotEmpty(o.getEndTime())
                                && finalTotalGunMap.containsKey(DateUtil.strToLocalDateTime(o.getEndTime()).toLocalDate()))
                        .mapToDouble(OrderRecordEntity::getTotalQt).sum();
                result.setAvgChargeQt(DoubleUtil.getToDouble(chargeOrderQt / avgGunNum));
            }

            //8.时间利用率(%) 筛选日期内创建的订单中，累计（充电时长+放电时长）／(总枪数（筛选日期内平均）*24h) * 100%
            if (totalGunNum != 0) {
                Map<LocalDate, Integer> finalTotalGunMap = totalGunMap;
                double chargeDuration = orderRecordList.stream().filter(o -> StringUtil.isNotEmpty(o.getEndTime()) &&
                        finalTotalGunMap.containsKey(DateUtil.strToLocalDateTime(o.getEndTime()).toLocalDate())).mapToDouble(c -> {
                    if (StringUtil.isNotEmpty(c.getStartTime()) && StringUtil.isNotEmpty(c.getEndTime())) {
                        LocalDateTime startLocalTime = DateUtil.strToLocalDateTime(c.getStartTime());
                        LocalDateTime endLocalTime = DateUtil.strToLocalDateTime(c.getEndTime());
                        return (double) DateUtil.compareDiffBetweenMinutes(startLocalTime, endLocalTime) / 60;
                    }
                    return 0.0;
                }).sum();
                result.setTimeRatio(DoubleUtil.getToDouble((chargeDuration / (totalGunNum * 24) * 100)));
            }

            //9.充电时长(小时) 筛选日期内创建的充电订单，累计充电时长
            result.setChargeDuration(DoubleUtil.getToDouble(chargeOrderList.stream().mapToDouble(c -> {
                if (StringUtil.isNotEmpty(c.getStartTime()) && StringUtil.isNotEmpty(c.getEndTime())) {
                    LocalDateTime startLocalTime = DateUtil.strToLocalDateTime(c.getStartTime());
                    LocalDateTime endLocalTime = DateUtil.strToLocalDateTime(c.getEndTime());
                    return (double) DateUtil.compareDiffBetweenMinutes(startLocalTime, endLocalTime) / 60;
                }
                return 0.0;
            }).sum()));

            //10.度均服务费(元) 筛选日期内创建的订单中，累计充电服务费 / 总充电度数
            if (result.getChargeOrderQt() != 0) {
                BigDecimal totalFee = chargeOrderList.stream().map(OrderRecordEntity::getTotalFee).filter(StringUtil::isNotEmpty).reduce(BigDecimal.ZERO, BigDecimal::add);
                result.setAvgChargeFee(DoubleUtil.getToBigDecimal(totalFee.divide(BigDecimal.valueOf(result.getChargeOrderQt()), 2, RoundingMode.HALF_UP)));
            }

            //11.功率利用率(%) 筛选日期内创建的订单中，累计（充电度数+放电度数）／全部充电桩额定功率之和（筛选日期内平均）*24h
            if (totalPilePower != 0) {
                Map<LocalDate, Integer> finalTotalGunMap = totalGunMap;
                double orderTotalQt = orderRecordList.stream().filter(o -> StringUtil.isNotEmpty(o.getTotalQt()) && StringUtil.isNotEmpty(o.getEndTime())
                                && finalTotalGunMap.containsKey(DateUtil.strToLocalDateTime(o.getEndTime()).toLocalDate()))
                        .mapToDouble(OrderRecordEntity::getTotalQt).sum();
                result.setPowerRatio(DoubleUtil.getToDouble(orderTotalQt / (totalPilePower * 24) * 100));
            }

            //12.一次充电成功率(%) 筛选日期内创建的订单中，正常订单数量/总订单数量*100%
            int normalNum = 0;
            if (normalNumMap.containsKey(siteId) && StringUtil.isNotEmpty(normalNumMap.get(siteId))) {
                normalNum = Integer.parseInt(String.valueOf(normalNumMap.get(siteId)));
            }
            int totalNum = 0;
            if (totalNumMap.containsKey(siteId) && StringUtil.isNotEmpty(totalNumMap.get(siteId))) {
                totalNum = Integer.parseInt(String.valueOf(totalNumMap.get(siteId)));
            }
            if (totalNum != 0) {
                result.setChargeSuccessRatio(DoubleUtil.getToDouble((double) normalNum / totalNum * 100));
            }
            resultMap.put(siteId, result);
        }
        return resultMap;
    }

    private Map<String, OverviewDto> getSystemOverviewMap(List<String> systemIds, String startTime, String endTime) {
        Map<String, OverviewDto> resultMap = Maps.newHashMap();
        //根据多个系统id查询设备数据
        List<String> typeIds = Arrays.asList("28", "29", "30");
        Map<String, List<DeviceBasicInfoDto>> deviceInfoMap = deviceService.findDeviceInfoByParentIds(systemIds).getData().values()
                .stream().flatMap(s -> s.stream().filter(d -> typeIds.contains(d.getTypeId())))
                .collect(Collectors.groupingBy(DeviceBasicInfoDto::getParentId));

        //获取充电桩编号
        List<String> pileCodes = deviceInfoMap.values().stream().flatMap(s -> s.stream().map(DeviceBasicInfoDto::getDeviceNumber)).collect(Collectors.toList());
        List<OrderRecordEntity> orderRecordList = orderRecordDao.findAll((Specification<OrderRecordEntity>) (root, cq, cb) -> {
            List<Predicate> predicates = Lists.newArrayList();
            predicates.add(cb.in(root.get("pileCode")).value(pileCodes));
            predicates.add(cb.between(root.get("endTime"), startTime, endTime));
            predicates.add(cb.isNotNull(root.get("runMode")));
            predicates.add(cb.or(cb.isNull(root.get("abnormalCode")), cb.equal(root.get("abnormalCode"), "[5]")));
            predicates.add(cb.equal(root.get("orderStatus"), "2"));
            return cb.and(predicates.toArray(new Predicate[0]));
        });

        //根据多个设备id查询电枪信息
        List<String> deviceIds = deviceInfoMap.values().stream().flatMap(s -> s.stream().map(DeviceBasicInfoDto::getId)).collect(Collectors.toList());
        Map<String, List<DeviceGunInfoDto>> deviceGunMap = deviceService.findDeviceGunInfoByDeviceIds(deviceIds).getData();

        //充电订单
        Map<String, List<OrderRecordEntity>> chargeOrderMap = orderRecordList.stream().filter(s -> s.getRunMode() == 0)
                .collect(Collectors.groupingBy(OrderRecordEntity::getPileCode));
        //放电订单
        Map<String, List<OrderRecordEntity>> dischargeMap = orderRecordList.stream().filter(s -> s.getRunMode() == 1 || s.getRunMode() == 2)
                .collect(Collectors.groupingBy(OrderRecordEntity::getPileCode));
        //查询充电结算实付金额
        List<String> orderNums = chargeOrderMap.values().stream().flatMap(c -> c.stream()
                .map(OrderRecordEntity::getOrderNum)).distinct().collect(Collectors.toList());
        Map<String, BigDecimal> orderActualCostMap = settlementRecordDao.findAllByOrderNumInAndPayWay(orderNums, 2).stream().collect(Collectors
                .toMap(SettlementRecordEntity::getOrderNum, SettlementRecordEntity::getActualTotalCost, (k1, k2) -> k1));


        //统计充电订单正常数量(排除进行中的,启动失败,未进行，订单挂起，无效订单的订单)
        Map<String, Object> normalNumMap = Maps.newHashMap();
        //统计充电订单总数量
        Map<String, Object> totalNumMap = Maps.newHashMap();
        if (CollectionUtils.isNotEmpty(pileCodes)) {
            normalNumMap = orderRecordMapper.countChargeOrderNormalNumByPileCodes(pileCodes, startTime, endTime).stream()
                    .collect(Collectors.toMap(c -> String.valueOf(c.get("pileCode")), c -> c.get("totalCount"),
                            (k1, k2) -> k1));
            totalNumMap = orderRecordMapper.countChargeOrderTotalNumByPileCodes(pileCodes, startTime, endTime).stream()
                    .collect(Collectors.toMap(c -> String.valueOf(c.get("pileCode")), c -> c.get("totalCount"),
                            (k1, k2) -> k1));
        }

        Map<String, Object> finalNormalNumMap = normalNumMap;
        Map<String, Object> finalTotalNumMap = totalNumMap;
        deviceInfoMap.forEach((dataId, deviceList) -> {
            OverviewDto result = new OverviewDto();
            //统计数据
            //1.充电订单金额(元) 筛选日期内创建的充电订单中，累计“订单金额之和”，未扣除优惠减免等费用。
            //2.充电实付金额(元) 筛选日期内创建的充电订单中，累计“实付金额之和”。
            //3.充电电量(度) 筛选日期内创建的充电订单中，累计充电度数总和
            //4.充电订单数量(笔) 筛选日期内创建的充电订单累计数量，排除异常订单和启动失败的订单
            //5.V2G放电订单金额(元) 筛选日期内创建的放电订单中，累计“订单金额之和”。
            //6.V2G放电电量(度) 筛选日期内创建的放电订单中，累计放电度数总和
            //7.枪均电量(度) 筛选日期内创建的订单中，累计充电度数 / 筛选日期内平均枪数
            //8.时间利用率(%) 筛选日期内创建的订单中，累计(充电时长+放电时长)/总枪数*24h(筛选日期内每天的数据求和)
            //9.充电时长(小时) 筛选日期内创建的充电订单，累计充电时长
            //10.度均服务费(元) 筛选日期内创建的订单中，累计充电服务费 / 总充电度数
            //11.功率利用率(%) 筛选日期内创建的订单中，累计(充电度数+放电度数)/全部充电桩额定功率之和*24h(筛选日期内每天的数据求和)
            //12.一次充电成功率(%) 筛选日期内创建的订单中，启动成功的订单数量(即排除启动失败、无效订单、挂起、充放电中、预约中)/总订单数量(不含进行中)*100%
            Map<String, String> pileCodeMap = deviceList.stream().filter(d -> StringUtil.isNotEmpty(d.getDeviceNumber()))
                    .collect(Collectors.toMap(DeviceBasicInfoDto::getDeviceNumber, DeviceBasicInfoDto::getDeviceNumber, (k1, k2) -> k1));
            //充电订单
            List<OrderRecordEntity> chargeOrderList = chargeOrderMap.entrySet().stream().filter(c -> pileCodeMap.containsKey(c.getKey()))
                    .flatMap(c -> c.getValue().stream()).collect(Collectors.toList());

            //1.充电订单金额(元) 筛选日期内创建的充电订单中，累计“订单金额之和”，未扣除优惠减免等费用。
            result.setChargeOrderMoney(DoubleUtil.getToBigDecimal(chargeOrderList.stream().map(OrderRecordEntity::getTotalCost)
                    .filter(StringUtil::isNotEmpty).reduce(BigDecimal.ZERO, BigDecimal::add)));

            //2.充电实付金额(元) 筛选日期内创建的充电订单中，累计“实付金额之和”。
            result.setChargePayMoney(DoubleUtil.getToBigDecimal(chargeOrderList.stream().map(c -> {
                if (orderActualCostMap.containsKey(c.getOrderNum())) {
                    return orderActualCostMap.get(c.getOrderNum());
                }
                return BigDecimal.ZERO;
            }).filter(StringUtil::isNotEmpty).reduce(BigDecimal.ZERO, BigDecimal::add)));

            //3.充电电量(度) 筛选日期内创建的充电订单中，累计充电度数总和
            result.setChargeOrderQt(DoubleUtil.getToDouble(chargeOrderList.stream().mapToDouble(OrderRecordEntity::getTotalQt).sum()));

            //4.充电订单数量(笔) 筛选日期内创建的充电订单累计数量，排除异常订单和启动失败的订单
            result.setChargeOrderNum(chargeOrderList.size());

            //放电订单
            List<OrderRecordEntity> dischargeOrderList = dischargeMap.entrySet().stream().filter(c -> pileCodeMap.containsKey(c.getKey()))
                    .flatMap(c -> c.getValue().stream()).collect(Collectors.toList());

            //5.V2G放电订单金额(元) 筛选日期内创建的放电订单中，累计“订单金额之和”。
            result.setDischargeOrderMoney(DoubleUtil.getToBigDecimal(dischargeOrderList.stream().map(OrderRecordEntity::getTotalCost)
                    .filter(StringUtil::isNotEmpty).reduce(BigDecimal.ZERO, BigDecimal::add)));

            //6.V2G放电电量(度) 筛选日期内创建的放电订单中，累计放电度数总和
            result.setDischargeOrderQt(DoubleUtil.getToDouble(dischargeOrderList.stream().mapToDouble(OrderRecordEntity::getTotalQt).sum()));

            //查询总充电桩额定功率和枪数量数据
            double totalPilePower = deviceList.stream().mapToDouble(device -> {
                Map<String, Object> reaMap = device.getReaMap();
                //额定功率
                if (MapUtils.isNotEmpty(reaMap) && reaMap.containsKey(ReaFieldParamVo.RATED_POWER) && StringUtil.isNotEmpty(reaMap.get(ReaFieldParamVo.RATED_POWER))) {
                    return Double.parseDouble(String.valueOf(reaMap.get(ReaFieldParamVo.RATED_POWER)));
                }
                return 0;
            }).sum();
            int totalGunNum = deviceList.stream().mapToInt(device -> {
                if (deviceGunMap.containsKey(device.getId())) {
                    return deviceGunMap.get(device.getId()).size();
                }
                return 0;
            }).sum();

            //7.枪均电量(度) 筛选日期内创建的订单中，累计充电度数 / 筛选日期内平均枪数
            if (totalGunNum != 0) {
                double chargeOrderQt = chargeOrderList.stream().filter(o -> StringUtil.isNotEmpty(o.getTotalQt()))
                        .mapToDouble(OrderRecordEntity::getTotalQt).sum();
                result.setAvgChargeQt(DoubleUtil.getToDouble(chargeOrderQt / totalGunNum));
            }

            //8.时间利用率(%) 筛选日期内创建的订单中，累计（充电时长+放电时长）／(总枪数（筛选日期内平均）*24h) * 100%
            if (totalGunNum != 0) {
                double chargeDuration = orderRecordList.stream().filter(o -> StringUtil.isNotEmpty(o.getStartTime()) &&
                        StringUtil.isNotEmpty(o.getEndTime())).mapToDouble(c -> {
                    LocalDateTime startLocalTime = DateUtil.strToLocalDateTime(c.getStartTime());
                    LocalDateTime endLocalTime = DateUtil.strToLocalDateTime(c.getEndTime());
                    return (double) DateUtil.compareDiffBetweenMinutes(startLocalTime, endLocalTime) / 60;
                }).sum();
                result.setTimeRatio(DoubleUtil.getToDouble((chargeDuration / (totalGunNum * 24) * 100)));
            }

            //9.充电时长(小时) 筛选日期内创建的充电订单，累计充电时长
            result.setChargeDuration(DoubleUtil.getToDouble(chargeOrderList.stream().mapToDouble(c -> {
                if (StringUtil.isNotEmpty(c.getStartTime()) && StringUtil.isNotEmpty(c.getEndTime())) {
                    LocalDateTime startLocalTime = DateUtil.strToLocalDateTime(c.getStartTime());
                    LocalDateTime endLocalTime = DateUtil.strToLocalDateTime(c.getEndTime());
                    return (double) DateUtil.compareDiffBetweenMinutes(startLocalTime, endLocalTime) / 60;
                }
                return 0.0;
            }).sum()));

            //10.度均服务费(元) 筛选日期内创建的订单中，累计充电服务费 / 总充电度数
            if (result.getChargeOrderQt() != 0) {
                BigDecimal totalFee = chargeOrderList.stream().map(OrderRecordEntity::getTotalFee).filter(StringUtil::isNotEmpty).reduce(BigDecimal.ZERO, BigDecimal::add);
                result.setAvgChargeFee(DoubleUtil.getToBigDecimal(totalFee.divide(BigDecimal.valueOf(result.getChargeOrderQt()), 2, RoundingMode.HALF_UP)));
            }

            //11.功率利用率(%) 筛选日期内创建的订单中，累计（充电度数+放电度数）／全部充电桩额定功率之和（筛选日期内平均）*24h
            if (totalPilePower != 0) {
                double orderTotalQt = orderRecordList.stream().filter(o -> StringUtil.isNotEmpty(o.getTotalQt()))
                        .mapToDouble(OrderRecordEntity::getTotalQt).sum();
                result.setPowerRatio(DoubleUtil.getToDouble(orderTotalQt / (totalPilePower * 24) * 100));
            }

            //12.一次充电成功率(%) 筛选日期内创建的订单中，正常订单数量/总订单数量*100%
            int normalNum = finalNormalNumMap.entrySet().stream().filter(c -> pileCodeMap.containsKey(c.getKey())
                    && StringUtil.isNotEmpty(c.getValue())).mapToInt(s -> Integer.parseInt(String.valueOf(s.getValue()))).sum();
            int totalNum = finalTotalNumMap.entrySet().stream().filter(c -> pileCodeMap.containsKey(c.getKey())
                    && StringUtil.isNotEmpty(c.getValue())).mapToInt(s -> Integer.parseInt(String.valueOf(s.getValue()))).sum();
            if (totalNum != 0) {
                result.setChargeSuccessRatio(DoubleUtil.getToDouble((double) normalNum / totalNum * 100));
            }
            resultMap.put(dataId, result);
        });
        return resultMap;
    }


    private Double calculateOverviewOver(Object beforeValue, Object thisValue) {
        if (StringUtil.isEmpty(beforeValue) || StringUtil.isEmpty(thisValue)) {
            return null;
        }
        BigDecimal beforeBigValue = new BigDecimal(String.valueOf(beforeValue));
        BigDecimal thisBigValue = new BigDecimal(String.valueOf(thisValue));
        if (beforeBigValue.compareTo(BigDecimal.ZERO) == 0) {
            return null;
        }
        //（当前值-上次值）/ 上次值 * 100%
        return DoubleUtil.getToBigDecimal((thisBigValue.subtract(beforeBigValue)).divide(beforeBigValue, 2, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))).doubleValue();
    }

    //充电订单金额(订单总金额,充电电费,充电服务费)
    private List<OperationCurveDto.CurveData> chargeOrderMoney(Map<String, List<OrderRecordEntity>> orderRecordMap, List<String> dateList) {
        //返回的集合
        List<OperationCurveDto.CurveData> resultList = Lists.newArrayList();
        List<Object> totalCostList = Lists.newArrayList();
        List<Object> totalElectList = Lists.newArrayList();
        List<Object> totalFeeList = Lists.newArrayList();
        for (String date : dateList) {
            if (orderRecordMap.containsKey(date)) {
                List<OrderRecordEntity> orderRecordList = orderRecordMap.get(date);
                totalCostList.add(orderRecordList.stream().map(OrderRecordEntity::getTotalCost).filter(StringUtil::isNotEmpty)
                        .reduce(BigDecimal.ZERO, BigDecimal::add));
                totalElectList.add(orderRecordList.stream().map(OrderRecordEntity::getTotalElect).filter(StringUtil::isNotEmpty)
                        .reduce(BigDecimal.ZERO, BigDecimal::add));
                totalFeeList.add(orderRecordList.stream().map(OrderRecordEntity::getTotalFee).filter(StringUtil::isNotEmpty)
                        .reduce(BigDecimal.ZERO, BigDecimal::add));
            } else {
                totalCostList.add(0);
                totalElectList.add(0);
                totalFeeList.add(0);
            }
        }
        resultList.add(OperationCurveDto.CurveData.builder().dataName(OperationTypeVo.SUM_COST).dataValueList(totalCostList).build());
        resultList.add(OperationCurveDto.CurveData.builder().dataName(OperationTypeVo.CHARGE_FEE).dataValueList(totalElectList).build());
        resultList.add(OperationCurveDto.CurveData.builder().dataName(OperationTypeVo.CHARGE_SERVICE_FEE).dataValueList(totalFeeList).build());
        return resultList;
    }

    //充电实付金额(实付总金额,实付电费,实付服务费)
    private List<OperationCurveDto.CurveData> chargePayMoney(Map<String, List<OrderRecordEntity>> orderRecordMap, List<String> dateList) {
        //返回的集合
        List<OperationCurveDto.CurveData> resultList = Lists.newArrayList();
        List<Object> actualTotalCostList = Lists.newArrayList();
        List<Object> actualTotalElectList = Lists.newArrayList();
        List<Object> actualTotalFeeList = Lists.newArrayList();
        //根据多个订单号查询订单结算记录
        List<String> orderNums = orderRecordMap.entrySet().stream().flatMap(s -> s.getValue().stream().map(OrderRecordEntity::getOrderNum))
                .distinct().collect(Collectors.toList());
        Map<String, SettlementRecordEntity> settlementMap = settlementRecordDao.findAllByOrderNumInAndPayWay(orderNums, 2).stream()
                .collect(Collectors.toMap(SettlementRecordEntity::getOrderNum, a -> a, (k1, k2) -> k1));
        for (String date : dateList) {
            if (orderRecordMap.containsKey(date)) {
                List<OrderRecordEntity> orderRecordList = orderRecordMap.get(date);
                actualTotalCostList.add(orderRecordList.stream().filter(o -> settlementMap.containsKey(o.getOrderNum()))
                        .map(o -> settlementMap.get(o.getOrderNum()).getActualTotalCost()).filter(StringUtil::isNotEmpty)
                        .reduce(BigDecimal.ZERO, BigDecimal::add));
                actualTotalElectList.add(orderRecordList.stream().filter(o -> settlementMap.containsKey(o.getOrderNum()))
                        .map(o -> settlementMap.get(o.getOrderNum()).getActualTotalElect()).filter(StringUtil::isNotEmpty)
                        .reduce(BigDecimal.ZERO, BigDecimal::add));
                actualTotalFeeList.add(orderRecordList.stream().filter(o -> settlementMap.containsKey(o.getOrderNum()))
                        .map(o -> settlementMap.get(o.getOrderNum()).getActualTotalFee()).filter(StringUtil::isNotEmpty)
                        .reduce(BigDecimal.ZERO, BigDecimal::add));
            } else {
                actualTotalCostList.add(0);
                actualTotalElectList.add(0);
                actualTotalFeeList.add(0);
            }
        }
        resultList.add(OperationCurveDto.CurveData.builder().dataName(OperationTypeVo.ACTUAL_TOTAL_COST).dataValueList(actualTotalCostList).build());
        resultList.add(OperationCurveDto.CurveData.builder().dataName(OperationTypeVo.ACTUAL_TOTAL_ELECT).dataValueList(actualTotalElectList).build());
        resultList.add(OperationCurveDto.CurveData.builder().dataName(OperationTypeVo.ACTUAL_TOTAL_FEE).dataValueList(actualTotalFeeList).build());
        return resultList;
    }

    //充电电量(充电电量,直流充电量,交流充电量,尖峰平谷充电量)
    private List<OperationCurveDto.CurveData> chargeOrderQt(Map<String, List<OrderRecordEntity>> orderRecordMap, List<String> dateList) {
        //返回的集合
        List<OperationCurveDto.CurveData> resultList = Lists.newArrayList();
        List<Object> chargeQtList = Lists.newArrayList();
        List<Object> dcChargeQtList = Lists.newArrayList();
        List<Object> acChargeQtList = Lists.newArrayList();
        List<Object> sharpQtList = Lists.newArrayList();
        List<Object> peakQtList = Lists.newArrayList();
        List<Object> flatQtList = Lists.newArrayList();
        List<Object> valleyQtList = Lists.newArrayList();
        List<Object> deepvalleyQtList = Lists.newArrayList();
        //根据多个订单号查询订单结算记录
        List<String> pileCodes = orderRecordMap.entrySet().stream().flatMap(s -> s.getValue().stream().map(OrderRecordEntity::getPileCode))
                .distinct().collect(Collectors.toList());
        Map<String, String> dcPileMap = Maps.newHashMap();
        Map<String, String> acPileMap = Maps.newHashMap();
        deviceService.findDeviceBasicInfoByCodes(pileCodes).getData().forEach((key, value) -> {
            if (Objects.equals(value.getTypeId(), "28")) {
                acPileMap.put(key, value.getTypeId());
            }
            if (Objects.equals(value.getTypeId(), "29") || Objects.equals(value.getTypeId(), "30")) {
                dcPileMap.put(key, value.getTypeId());
            }
        });
        for (String date : dateList) {
            if (orderRecordMap.containsKey(date)) {
                List<OrderRecordEntity> orderRecordList = orderRecordMap.get(date);
                chargeQtList.add(orderRecordList.stream().filter(o -> StringUtil.isNotEmpty(o.getTotalQt()))
                        .mapToDouble(OrderRecordEntity::getTotalQt).sum());
                dcChargeQtList.add(orderRecordList.stream().filter(o -> dcPileMap.containsKey(o.getPileCode())
                        && StringUtil.isNotEmpty(o.getTotalQt())).mapToDouble(OrderRecordEntity::getTotalQt).sum());
                acChargeQtList.add(orderRecordList.stream().filter(o -> acPileMap.containsKey(o.getPileCode())
                        && StringUtil.isNotEmpty(o.getTotalQt())).mapToDouble(OrderRecordEntity::getTotalQt).sum());
                sharpQtList.add(orderRecordList.stream().filter(o -> StringUtil.isNotEmpty(o.getJQt()))
                        .mapToDouble(OrderRecordEntity::getJQt).sum());
                peakQtList.add(orderRecordList.stream().filter(o -> StringUtil.isNotEmpty(o.getFQt()))
                        .mapToDouble(OrderRecordEntity::getFQt).sum());
                flatQtList.add(orderRecordList.stream().filter(o -> StringUtil.isNotEmpty(o.getPQt()))
                        .mapToDouble(OrderRecordEntity::getPQt).sum());
                valleyQtList.add(orderRecordList.stream().filter(o -> StringUtil.isNotEmpty(o.getGQt()))
                        .mapToDouble(OrderRecordEntity::getGQt).sum());
            } else {
                chargeQtList.add(0);
                dcChargeQtList.add(0);
                acChargeQtList.add(0);
                sharpQtList.add(0);
                peakQtList.add(0);
                flatQtList.add(0);
                valleyQtList.add(0);
                deepvalleyQtList.add(0);
            }
        }
        resultList.add(OperationCurveDto.CurveData.builder().dataName(OperationTypeVo.CHARGE_QT).dataValueList(chargeQtList).build());
        resultList.add(OperationCurveDto.CurveData.builder().dataName(OperationTypeVo.DC_CHARGE_QT).dataValueList(dcChargeQtList).build());
        resultList.add(OperationCurveDto.CurveData.builder().dataName(OperationTypeVo.AC_CHARGE_QT).dataValueList(acChargeQtList).build());
        resultList.add(OperationCurveDto.CurveData.builder().dataName(OperationTypeVo.SHARP_QT).dataValueList(sharpQtList).build());
        resultList.add(OperationCurveDto.CurveData.builder().dataName(OperationTypeVo.PEAK_QT).dataValueList(peakQtList).build());
        resultList.add(OperationCurveDto.CurveData.builder().dataName(OperationTypeVo.FLAT_QT).dataValueList(flatQtList).build());
        resultList.add(OperationCurveDto.CurveData.builder().dataName(OperationTypeVo.VALLEY_QT).dataValueList(valleyQtList).build());
        resultList.add(OperationCurveDto.CurveData.builder().dataName(OperationTypeVo.DEEP_VALLEY_QT).dataValueList(deepvalleyQtList).build());
        return resultList;
    }

    //充电订单数量(正常订单数量,异常订单数量)
    private List<OperationCurveDto.CurveData> chargeOrderNum(Map<String, List<OrderRecordEntity>> orderRecordMap, Map<String, Integer> abOrderNumMap,
                                                             List<String> dateList) {
        //返回的集合
        List<OperationCurveDto.CurveData> resultList = Lists.newArrayList();
        List<Object> orderNumList = Lists.newArrayList();
        List<Object> abOrderNumList = Lists.newArrayList();
        for (String date : dateList) {
            if (orderRecordMap.containsKey(date)) {
                orderNumList.add(orderRecordMap.get(date).size());
            } else {
                orderNumList.add(0);
            }
            abOrderNumList.add(abOrderNumMap.getOrDefault(date, 0));
        }
        resultList.add(OperationCurveDto.CurveData.builder().dataName(OperationTypeVo.ORDER_NUM).dataValueList(orderNumList).build());
        resultList.add(OperationCurveDto.CurveData.builder().dataName(OperationTypeVo.AB_ORDER_NUM).dataValueList(abOrderNumList).build());
        return resultList;
    }

    //V2G放电订单金额
    private List<OperationCurveDto.CurveData> dischargeOrderMoney(Map<String, List<OrderRecordEntity>> orderRecordMap, List<String> dateList) {
        //返回的集合
        List<OperationCurveDto.CurveData> resultList = Lists.newArrayList();
        List<Object> dischargeSumCostList = Lists.newArrayList();
        for (String date : dateList) {
            if (orderRecordMap.containsKey(date)) {
                dischargeSumCostList.add(orderRecordMap.get(date).stream().map(OrderRecordEntity::getTotalCost)
                        .filter(StringUtil::isNotEmpty).reduce(BigDecimal.ZERO, BigDecimal::add));
            } else {
                dischargeSumCostList.add(0);
            }
        }
        resultList.add(OperationCurveDto.CurveData.builder().dataName(OperationTypeVo.DISCHARGE_SUM_COST).dataValueList(dischargeSumCostList).build());
        return resultList;
    }

    //V2G放电订单电量
    private List<OperationCurveDto.CurveData> dischargeOrderQt(Map<String, List<OrderRecordEntity>> orderRecordMap, List<String> dateList) {
        //返回的集合
        List<OperationCurveDto.CurveData> resultList = Lists.newArrayList();
        List<Object> dischargeQtList = Lists.newArrayList();
        for (String date : dateList) {
            if (orderRecordMap.containsKey(date)) {
                dischargeQtList.add(orderRecordMap.get(date).stream().filter(o -> StringUtil.isNotEmpty(o.getTotalQt()))
                        .mapToDouble(OrderRecordEntity::getTotalQt).sum());
            } else {
                dischargeQtList.add(0);
            }
        }
        resultList.add(OperationCurveDto.CurveData.builder().dataName(OperationTypeVo.DISCHARGE_QT).dataValueList(dischargeQtList).build());
        return resultList;
    }

    //枪均电量(枪均充电量,直流枪均充电量,交流枪均充电量) 累计充电度数 / 筛选日期内平均枪数
    private List<OperationCurveDto.CurveData> avgChargeQt(Integer dateType, Map<String, List<OrderRecordEntity>> orderRecordMap,
                                                          List<SiteCountRecordEntity> countRecordList, List<String> dateList) {
        //返回的集合
        List<OperationCurveDto.CurveData> resultList = Lists.newArrayList();

        //根据多个电桩编号查询交流充电桩和直流充电桩
        List<String> pileCodes = orderRecordMap.entrySet().stream().flatMap(s -> s.getValue().stream().map(OrderRecordEntity::getPileCode)
                .filter(StringUtil::isNotEmpty)).distinct().collect(Collectors.toList());
        //定义交流充电桩和直流充电桩
        Map<String, String> acPileMap = Maps.newHashMap();
        Map<String, String> dcPileMap = Maps.newHashMap();
        deviceService.findDeviceBasicInfoByCodes(pileCodes).getData().forEach((key, value) -> {
            if (Objects.equals(value.getTypeId(), "28")) {
                acPileMap.put(key, value.getTypeId());
            }
            if (Objects.equals(value.getTypeId(), "29") || Objects.equals(value.getTypeId(), "30")) {
                dcPileMap.put(key, value.getTypeId());
            }
        });

        //定义每天日期对应的交流枪和直流枪数量
        Map<String, Integer> gunNumDateMap = Maps.newHashMap();
        Map<String, Integer> acGunNumDateMap = Maps.newHashMap();
        Map<String, Integer> dcGunNumDateMap = Maps.newHashMap();
        for (Map.Entry<String, List<SiteCountRecordEntity>> entry : countRecordList.stream().collect(Collectors
                .groupingBy(a -> DateUtil.localDateToStr(a.getCountDate()))).entrySet()) {
            gunNumDateMap.put(entry.getKey(), entry.getValue().stream().filter(s -> StringUtil.isNotEmpty(s.getTotalGunNum())).mapToInt(SiteCountRecordEntity::getTotalGunNum).sum());
            acGunNumDateMap.put(entry.getKey(), entry.getValue().stream().filter(s -> StringUtil.isNotEmpty(s.getAcGunNum())).mapToInt(SiteCountRecordEntity::getAcGunNum).sum());
            dcGunNumDateMap.put(entry.getKey(), entry.getValue().stream().filter(s -> StringUtil.isNotEmpty(s.getDcGunNum())).mapToInt(SiteCountRecordEntity::getDcGunNum).sum());
        }

        List<Object> gunAvChargeQtList = Lists.newArrayList();
        List<Object> dcGunAvChargeQtList = Lists.newArrayList();
        List<Object> acGunAvChargeQtList = Lists.newArrayList();
        for (String date : dateList) {
            if (orderRecordMap.containsKey(date)) {
                List<OrderRecordEntity> orderRecordList = orderRecordMap.get(date);
                //获取累计充电量,交流充电量,直流充电量
                double totalQt = orderRecordList.stream().filter(o -> StringUtil.isNotEmpty(o.getTotalQt()) && gunNumDateMap.containsKey(o.getEndTime().substring(0, 10)))
                        .mapToDouble(OrderRecordEntity::getTotalQt).sum();
                double acTotalQt = orderRecordList.stream().filter(o -> StringUtil.isNotEmpty(o.getTotalQt()) && acPileMap.containsKey(o.getPileCode())
                        && acGunNumDateMap.containsKey(o.getEndTime().substring(0, 10))).mapToDouble(OrderRecordEntity::getTotalQt).sum();
                double dcTotalQt = orderRecordList.stream().filter(o -> StringUtil.isNotEmpty(o.getTotalQt()) && dcPileMap.containsKey(o.getPileCode())
                        && dcGunNumDateMap.containsKey(o.getEndTime().substring(0, 10))).mapToDouble(OrderRecordEntity::getTotalQt).sum();

                //获取筛选日期内平均枪数
                double avgGunNum = 0.0;
                double avgAcGunNum = 0.0;
                double avgDcGunNum = 0.0;
                if (dateType == 1) {
                    if (gunNumDateMap.containsKey(date)) {
                        avgGunNum = gunNumDateMap.get(date).doubleValue();
                    }
                    if (acGunNumDateMap.containsKey(date)) {
                        avgAcGunNum = acGunNumDateMap.get(date).doubleValue();
                    }
                    if (dcGunNumDateMap.containsKey(date)) {
                        avgDcGunNum = dcGunNumDateMap.get(date).doubleValue();
                    }

                }
                if (dateType == 2) {
                    int gunNumSize = (int) gunNumDateMap.entrySet().stream().filter(s -> Objects.equals(s.getKey().substring(0, 7), date)).count();
                    int gunNumSum = gunNumDateMap.entrySet().stream().filter(s -> Objects.equals(s.getKey().substring(0, 7), date)).mapToInt(Map.Entry::getValue).sum();
                    if (gunNumSize != 0) {
                        avgGunNum = (double) gunNumSum / gunNumSize;
                    }
                    int acGunNumSize = (int) acGunNumDateMap.entrySet().stream().filter(s -> Objects.equals(s.getKey().substring(0, 7), date)).count();
                    int acGunNumSum = acGunNumDateMap.entrySet().stream().filter(s -> Objects.equals(s.getKey().substring(0, 7), date)).mapToInt(Map.Entry::getValue).sum();
                    if (acGunNumSize != 0) {
                        avgAcGunNum = (double) acGunNumSum / acGunNumSize;
                    }
                    int dcGunNumSize = (int) dcGunNumDateMap.entrySet().stream().filter(s -> Objects.equals(s.getKey().substring(0, 7), date)).count();
                    int dcGunNumSum = dcGunNumDateMap.entrySet().stream().filter(s -> Objects.equals(s.getKey().substring(0, 7), date)).mapToInt(Map.Entry::getValue).sum();
                    if (dcGunNumSize != 0) {
                        avgDcGunNum = (double) dcGunNumSum / dcGunNumSize;
                    }
                }
                //枪均电量 累计充电度数 / 筛选日期内平均枪数
                if (avgGunNum != 0) {
                    gunAvChargeQtList.add(totalQt / avgGunNum);
                } else {
                    gunAvChargeQtList.add(null);
                }
                if (avgAcGunNum != 0) {
                    acGunAvChargeQtList.add(acTotalQt / avgAcGunNum);
                } else {
                    acGunAvChargeQtList.add(null);
                }
                if (avgDcGunNum != 0) {
                    dcGunAvChargeQtList.add(dcTotalQt / avgDcGunNum);
                } else {
                    dcGunAvChargeQtList.add(null);
                }
            } else {
                gunAvChargeQtList.add(null);
                dcGunAvChargeQtList.add(null);
                acGunAvChargeQtList.add(null);
            }
        }
        resultList.add(OperationCurveDto.CurveData.builder().dataName(OperationTypeVo.GUN_AV_CHARGE_QT).dataValueList(gunAvChargeQtList).build());
        resultList.add(OperationCurveDto.CurveData.builder().dataName(OperationTypeVo.DC_GUN_AV_CHARGE_QT).dataValueList(dcGunAvChargeQtList).build());
        resultList.add(OperationCurveDto.CurveData.builder().dataName(OperationTypeVo.AC_GUN_AV_CHARGE_QT).dataValueList(acGunAvChargeQtList).build());
        return resultList;
    }

    //时间利用率(%) 筛选日期内创建的订单中，累计(充电时长+放电时长)/总枪数*24h(筛选日期内每天的数据求和)
    private List<OperationCurveDto.CurveData> timeRatio(Integer dateType, Map<String, List<OrderRecordEntity>> orderRecordMap,
                                                        List<SiteCountRecordEntity> countRecordList, List<String> dateList) {
        //返回的集合
        List<OperationCurveDto.CurveData> resultList = Lists.newArrayList();

        //获取每天的枪数量
        Map<String, Integer> gunNumDateMap = countRecordList.stream().filter(s -> StringUtil.isNotEmpty(s.getTotalGunNum())).collect(Collectors
                .groupingBy(a -> DateUtil.localDateToStr(a.getCountDate()), Collectors.summingInt(SiteCountRecordEntity::getTotalGunNum)));

        Map<String, Integer> gunNumMap = Maps.newHashMap();
        if (dateType == 1) {
            gunNumMap = gunNumDateMap;
        }
        if (dateType == 2) {
            gunNumMap = countRecordList.stream().filter(s -> StringUtil.isNotEmpty(s.getTotalGunNum())).collect(Collectors.groupingBy(a ->
                    DateUtil.localDateToStr(a.getCountDate()).substring(0, 7), Collectors.summingInt(SiteCountRecordEntity::getTotalGunNum)));
        }

        List<Object> timeRatioList = Lists.newArrayList();
        for (String date : dateList) {
            if (orderRecordMap.containsKey(date)) {
                List<OrderRecordEntity> orderRecordList = orderRecordMap.get(date);
                double chargeDuration = orderRecordList.stream().filter(o -> StringUtil.isNotEmpty(o.getTotalQt())
                        && gunNumDateMap.containsKey(o.getEndTime().substring(0, 10))).mapToDouble(c -> {
                    if (StringUtil.isNotEmpty(c.getStartTime()) && StringUtil.isNotEmpty(c.getEndTime())) {
                        LocalDateTime startLocalTime = DateUtil.strToLocalDateTime(c.getStartTime());
                        LocalDateTime endLocalTime = DateUtil.strToLocalDateTime(c.getEndTime());
                        return (double) DateUtil.compareDiffBetweenMinutes(startLocalTime, endLocalTime) / 60;
                    }
                    return 0.0;
                }).sum();
                if (gunNumMap.containsKey(date) && gunNumMap.get(date) != 0) {
                    //累计(充电时长+放电时长)/总枪数*24h(筛选日期内每天的数据求和)
                    timeRatioList.add((chargeDuration / (gunNumMap.get(date) * 24) * 100));
                } else {
                    timeRatioList.add(null);
                }
            } else {
                timeRatioList.add(null);
            }
        }
        resultList.add(OperationCurveDto.CurveData.builder().dataName(OperationTypeVo.TIME_RATIO).dataValueList(timeRatioList).build());
        return resultList;
    }

    //充电时长(充电时长,直流充电时长,交流充电时长)
    private List<OperationCurveDto.CurveData> chargeDuration(Map<String, List<OrderRecordEntity>> orderRecordMap, List<String> dateList) {
        //返回的集合
        List<OperationCurveDto.CurveData> resultList = Lists.newArrayList();
        List<Object> chargeDurationList = Lists.newArrayList();
        List<Object> dcChargeDurationList = Lists.newArrayList();
        List<Object> acChargeDurationList = Lists.newArrayList();
        //根据多个订单号查询订单结算记录
        List<String> pileCodes = orderRecordMap.entrySet().stream().flatMap(s -> s.getValue().stream().map(OrderRecordEntity::getPileCode))
                .distinct().collect(Collectors.toList());
        Map<String, String> dcPileMap = Maps.newHashMap();
        Map<String, String> acPileMap = Maps.newHashMap();
        deviceService.findDeviceBasicInfoByCodes(pileCodes).getData().forEach((key, value) -> {
            if (Objects.equals(value.getTypeId(), "28")) {
                acPileMap.put(key, value.getTypeId());
            }
            if (Objects.equals(value.getTypeId(), "29") || Objects.equals(value.getTypeId(), "30")) {
                dcPileMap.put(key, value.getTypeId());
            }
        });
        for (String date : dateList) {
            if (orderRecordMap.containsKey(date)) {
                List<OrderRecordEntity> orderRecordList = orderRecordMap.get(date);
                chargeDurationList.add(orderRecordList.stream().mapToDouble(c -> {
                    if (StringUtil.isNotEmpty(c.getStartTime()) && StringUtil.isNotEmpty(c.getEndTime())) {
                        LocalDateTime startLocalTime = DateUtil.strToLocalDateTime(c.getStartTime());
                        LocalDateTime endLocalTime = DateUtil.strToLocalDateTime(c.getEndTime());
                        return (double) DateUtil.compareDiffBetweenMinutes(startLocalTime, endLocalTime) / 60;
                    }
                    return 0.0;
                }).sum());
                dcChargeDurationList.add(orderRecordList.stream().filter(o -> dcPileMap.containsKey(o.getPileCode())).mapToDouble(c -> {
                    if (StringUtil.isNotEmpty(c.getStartTime()) && StringUtil.isNotEmpty(c.getEndTime())) {
                        LocalDateTime startLocalTime = DateUtil.strToLocalDateTime(c.getStartTime());
                        LocalDateTime endLocalTime = DateUtil.strToLocalDateTime(c.getEndTime());
                        return (double) DateUtil.compareDiffBetweenMinutes(startLocalTime, endLocalTime) / 60;
                    }
                    return 0.0;
                }).sum());
                acChargeDurationList.add(orderRecordList.stream().filter(o -> acPileMap.containsKey(o.getPileCode())).mapToDouble(c -> {
                    if (StringUtil.isNotEmpty(c.getStartTime()) && StringUtil.isNotEmpty(c.getEndTime())) {
                        LocalDateTime startLocalTime = DateUtil.strToLocalDateTime(c.getStartTime());
                        LocalDateTime endLocalTime = DateUtil.strToLocalDateTime(c.getEndTime());
                        return (double) DateUtil.compareDiffBetweenMinutes(startLocalTime, endLocalTime) / 60;
                    }
                    return 0.0;
                }).sum());
            } else {
                chargeDurationList.add(0);
                dcChargeDurationList.add(0);
                acChargeDurationList.add(0);
            }
        }
        resultList.add(OperationCurveDto.CurveData.builder().dataName(OperationTypeVo.CHARGE_DURATION).dataValueList(chargeDurationList).build());
        resultList.add(OperationCurveDto.CurveData.builder().dataName(OperationTypeVo.DC_CHARGE_DURATION).dataValueList(dcChargeDurationList).build());
        resultList.add(OperationCurveDto.CurveData.builder().dataName(OperationTypeVo.AC_CHARGE_DURATION).dataValueList(acChargeDurationList).build());
        return resultList;
    }

    //度均服务费 累计充电服务费 / 总充电度数
    private List<OperationCurveDto.CurveData> avgChargeFee(Map<String, List<OrderRecordEntity>> orderRecordMap, List<String> dateList) {
        //返回的集合
        List<OperationCurveDto.CurveData> resultList = Lists.newArrayList();
        List<Object> avgChargeFeeList = Lists.newArrayList();
        for (String date : dateList) {
            if (orderRecordMap.containsKey(date)) {
                List<OrderRecordEntity> orderRecordList = orderRecordMap.get(date);
                BigDecimal totalQt = BigDecimal.valueOf(orderRecordList.stream().filter(o -> StringUtil.isNotEmpty(o.getTotalQt()))
                        .mapToDouble(OrderRecordEntity::getTotalQt).sum());
                BigDecimal totalFee = orderRecordList.stream().map(OrderRecordEntity::getTotalFee).filter(StringUtil::isNotEmpty)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                avgChargeFeeList.add(totalQt.compareTo(BigDecimal.ZERO) == 0 ? null : totalFee.divide(totalQt, 2, RoundingMode.HALF_UP));
            } else {
                avgChargeFeeList.add(null);
            }
        }
        resultList.add(OperationCurveDto.CurveData.builder().dataName(OperationTypeVo.AVG_CHARGE_FEE).dataValueList(avgChargeFeeList).build());
        return resultList;
    }

    //功率利用率 累计(充电度数+放电度数) /(全部充电桩额定功率之和×24h(筛选日期内每天的数据求和)) *100%
    private List<OperationCurveDto.CurveData> powerRatio(Integer dateType, Map<String, List<OrderRecordEntity>> orderRecordMap,
                                                         List<SiteCountRecordEntity> countRecordList, List<String> dateList) {
        //返回的集合
        List<OperationCurveDto.CurveData> resultList = Lists.newArrayList();
        //获取每天的电桩总额定功率
        Map<String, Double> pilePowerDateMap = countRecordList.stream().filter(a -> StringUtil.isNotEmpty(a.getTotalPilePower())).collect(Collectors
                .groupingBy(a -> DateUtil.localDateToStr(a.getCountDate()), Collectors.summingDouble(SiteCountRecordEntity::getTotalPilePower)));

        Map<String, Double> pilePowerMap = Maps.newHashMap();
        if (dateType == 1) {
            pilePowerMap = pilePowerDateMap;
        }
        if (dateType == 2) {
            pilePowerMap = countRecordList.stream().filter(a -> StringUtil.isNotEmpty(a.getTotalPilePower())).collect(Collectors.groupingBy(a ->
                    DateUtil.localDateToStr(a.getCountDate()).substring(0, 7), Collectors.summingDouble(SiteCountRecordEntity::getTotalPilePower)));
        }
        List<Object> powerRatioList = Lists.newArrayList();
        for (String date : dateList) {
            if (orderRecordMap.containsKey(date)) {
                List<OrderRecordEntity> orderRecordList = orderRecordMap.get(date);
                double totalQt = orderRecordList.stream().filter(o -> StringUtil.isNotEmpty(o.getTotalQt())
                        && pilePowerDateMap.containsKey(o.getEndTime().substring(0, 10))).mapToDouble(OrderRecordEntity::getTotalQt).sum();
                if (pilePowerMap.containsKey(date) && pilePowerMap.get(date) != 0) {
                    //累计(充电度数+放电度数) / (全部充电桩额定功率之和×24h(筛选日期内每天的数据求和)) *100%
                    powerRatioList.add(totalQt / (pilePowerMap.get(date) * 24) * 100);
                } else {
                    powerRatioList.add(null);
                }
            } else {
                powerRatioList.add(null);
            }
        }
        resultList.add(OperationCurveDto.CurveData.builder().dataName(OperationTypeVo.POWER_RATIO).dataValueList(powerRatioList).build());
        return resultList;
    }

    //一次充电成功率 筛选日期内创建的订单中，启动成功的订单数量(即排除启动失败、无效订单、挂起、充放电中、预约中)/总订单数量(不含进行中)*100%
    private List<OperationCurveDto.CurveData> chargeSuccessRatio(Map<String, Integer> orderNormalNumMap, Map<String, Integer> orderTotalNumMap, List<String> dateList) {
        //返回的集合
        List<OperationCurveDto.CurveData> resultList = Lists.newArrayList();
        List<Object> chargeSuccessRatioList = Lists.newArrayList();
        for (String date : dateList) {
            if (orderNormalNumMap.containsKey(date) && orderTotalNumMap.containsKey(date)) {
                Integer normalNum = orderNormalNumMap.get(date);
                Integer totalNum = orderTotalNumMap.get(date);
                if (totalNum != 0) {
                    chargeSuccessRatioList.add((double) normalNum / totalNum * 100);
                } else {
                    chargeSuccessRatioList.add(0);
                }
            } else {
                chargeSuccessRatioList.add(0);
            }
        }
        resultList.add(OperationCurveDto.CurveData.builder().dataName(OperationTypeVo.CHARGE_SUCCESS_RATIO).dataValueList(chargeSuccessRatioList).build());
        return resultList;
    }

}
