package com.sunmax.together.service.custom.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.sunmax.common.dto.data.NodeDifHistoryDto;
import com.sunmax.common.dto.device.DeviceBasicInfoDto;
import com.sunmax.common.dto.device.ModelFunctionListDto;
import com.sunmax.common.model.RealDataModel;
import com.sunmax.common.util.DateUtil;
import com.sunmax.common.util.DoubleUtil;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.util.StringUtil;
import com.sunmax.common.util.oss.FileUtil;
import com.sunmax.common.vo.data.DeviceHistoryQueryVo;
import com.sunmax.common.vo.device.DeviceAlarmEventQueryVo;
import com.sunmax.together.dao.asset.ElectConfigDao;
import com.sunmax.together.dao.asset.ElectTimeFrameDao;
import com.sunmax.together.dto.custom.*;
import com.sunmax.together.dto.operation.storageCount.ElectTypeDto;
import com.sunmax.together.entity.assets.ElectConfigEntity;
import com.sunmax.together.service.custom.CustomSystemService;
import com.sunmax.together.service.feign.DataService;
import com.sunmax.together.service.feign.DeviceService;
import com.sunmax.together.service.operation.StorageCountService;
import com.sunmax.together.util.TogetherCommonUtil;
import com.sunmax.together.vo.custom.DeviceIdParamVo;
import com.sunmax.together.vo.custom.FunctionParamVo;
import com.sunmax.together.vo.operation.storageCount.StorageCountVo;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.compress.utils.Lists;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

import static com.sunmax.common.util.DateUtil.*;

@Service
public class CustomSystemServiceImpl implements CustomSystemService {

    @Autowired
    private DataService dataService;

    @Autowired
    private DeviceService deviceService;

    @Autowired
    private StorageCountService storageCountService;

    @Autowired
    private ElectConfigDao electConfigDao;

    @Autowired
    private ElectTimeFrameDao electTimeFrameDao;

    @Override
    public ResponseResult<SiteOverviewDto> getSiteOverview(String siteId) {

        return null;
    }

    /*@Override
    public ResponseResult<SiteAcSystemDto> getSiteAcSystem(String siteId, Integer type) {
        //返回的对象
        SiteAcSystemDto result = new SiteAcSystemDto();

        //参数校验
        if (StringUtil.isEmpty(siteId) || StringUtil.isEmpty(type)) {
            return ResponseResult.paramError(ResponseResult.PARAM_ERROR);
        }

        //根据站点id查询开始日期
        SiteInfoDto siteInfo = deviceService.findSiteBasicInfoByIds(Collections.singletonList(siteId)).getData().get(siteId);
        String startDate = null;
        String endDate = DateUtil.localDateToStr(LocalDate.now());
        if (siteInfo != null && CollectionUtils.isNotEmpty(siteInfo.getSiteScenarioTypeDtos())) {
            if (StringUtil.isNotEmpty(siteInfo.getSiteReadwriteObject())) {
                JSONObject jsonObject = JSON.parseObject(siteInfo.getSiteReadwriteObject());
                if (jsonObject.containsKey(SiteFieldParamVo.OFFICIAL_RUN_TIME)) {
                    startDate = jsonObject.getString(SiteFieldParamVo.OFFICIAL_RUN_TIME);
                }
            }
            if (StringUtil.isEmpty(startDate) && StringUtil.isNotEmpty(siteInfo.getCreateTime())) {
                startDate = DateUtil.localDateToStr(siteInfo.getCreateTime().toLocalDate());
            }
        }

        //获取站点的电价配置(每天)
        //电网电价(分时电价) 日期 -> (分时段时间 -> 电价)
        Map<String, Map<String, BigDecimal>> gridPurchaseElectMap = Maps.newHashMap();
        //光伏消纳电价(分时电价) 日期 -> (分时段时间 -> 电价)
        Map<String, Map<String, BigDecimal>> pvConsumeElectMap = Maps.newHashMap();
        //储能售电电价(分时电价) 日期 -> (分时段时间 -> 电价)
        Map<String, Map<String, BigDecimal>> seSaleElectMap = Maps.newHashMap();

        //获取日期列表
        List<String> dateList = getDateBetween(1, startDate, endDate);

        //根据站点id查询站点电价
        List<ElectConfigEntity> electConfigList = electConfigDao.findElectConfigList(siteId, Arrays.asList(1, 3, 4), startDate, endDate);
        if (CollectionUtils.isNotEmpty(electConfigList)) {
            //根据多个电站配置id查询电价
            Set<String> electConfigIds = electConfigList.stream().map(ElectConfigEntity::getId).collect(Collectors.toSet());
            Map<String, Map<String, BigDecimal>> electTimeFrameMap = TogetherCommonUtil.buildHalfHourMapByConfigId(electTimeFrameDao.findAllByElectConfigIdIn(electConfigIds));
            electConfigList.stream().collect(Collectors.toMap(ElectConfigEntity::getModuleType, a -> a, (k1, k2) -> k1))
                    .forEach((moduleType, electConfig) -> {
                        //电价配置 (半小时时段 -> 电费)
                        Map<String, BigDecimal> timeFrameMap = electTimeFrameMap.get(electConfig.getId());
                        if (MapUtils.isNotEmpty(timeFrameMap)) {
                            //过滤日期范围内的日期
                            List<String> electDateList = DateUtil.getDateBetween(1, electConfig.getStartDate(), electConfig.getEndDate());
                            List<String> electFiletrDateList = dateList.stream().filter(electDateList::contains).collect(Collectors.toList());
                            for (String date : electFiletrDateList) {
                                if (moduleType == 1) { //电网购电电价
                                    gridPurchaseElectMap.put(date, timeFrameMap);
                                }
                                if (moduleType == 3) { //光伏上网电价
                                    pvConsumeElectMap.put(date, timeFrameMap);
                                }
                                if (moduleType == 4) { //储能售电电价
                                    seSaleElectMap.put(date, timeFrameMap);
                                }
                            }
                        }
                    });
        }

        //计算直流系统用电成本
        //1.获取关口表正向电量和反向电量(功能点标识 -> (日期(yy-MM-dd) -> (时间(HH:mm) -> 数据值)))
        Map<String, Map<String, Map<String, Double>>> gwDataMap = this.getNodeDifHistoryDayMap(DeviceIdParamVo.AC_GGD_METER_ID, new HashSet<>(Arrays
                .asList(FunctionParamVo.TOTAL_POSITIVE_ACTIVE_ENERGY, FunctionParamVo.TOTAL_INVERSE_ACTIVE_ENERGY)), startDate, endDate);
        //2.获取光伏DC/DC的累计发电量(功能点标识 -> (日期(yy-MM-dd) -> (时间(HH:mm) -> 数据值)))
        Map<String, Map<String, Map<String, Double>>> pvDataMap = this.getNodeDifHistoryDayMap(DeviceIdParamVo.PV_DC_DC_ID, Collections
                .singleton(FunctionParamVo.PV_TOTAL_BATTERY_GENERATION), startDate, endDate);
        //3.获取储能DC/DC的累计充电量和累计放电量(功能点标识 -> (日期(yy-MM-dd) -> (时间(HH:mm) -> 数据值)))
        Map<String, Map<String, Map<String, Double>>> seDataMap = this.getNodeDifHistoryDayMap(DeviceIdParamVo.SE_DC_DC_ID, new HashSet<>(Arrays
                .asList(FunctionParamVo.SE_TOTAL_BATTERY_CHARGE, FunctionParamVo.SE_TOTAL_BATTERY_DISCHARGE)), startDate, endDate);
        //4.获取负载总耗电量(功能点标识 -> (日期(yy-MM-dd) -> (时间(HH:mm) -> 数据值)))
        Map<String, Map<String, Map<String, Double>>> loadDataMap = this.getNodeDifHistoryDayMap(DeviceIdParamVo.DC_GGD_LOAD_ID, Collections
                .singleton(FunctionParamVo.TOTAL_POSITIVE_ACTIVE_ENERGY), startDate, endDate);

        //关口表正向电量数据(日期(yy-MM-dd) -> (时间(HH:mm) -> 数据值))
        Map<String, Map<String, Double>> gwPositiveElecMap = gwDataMap.getOrDefault(FunctionParamVo.TOTAL_POSITIVE_ACTIVE_ENERGY, Maps.newHashMap());
        //关口表反向电量数据(日期(yy-MM-dd) -> (时间(HH:mm) -> 数据值))
        Map<String, Map<String, Double>> gwInverseElecMap = gwDataMap.getOrDefault(FunctionParamVo.TOTAL_INVERSE_ACTIVE_ENERGY, Maps.newHashMap());
        //光伏DC/DC的累计发电量数据(日期(yy-MM-dd) -> (时间(HH:mm) -> 数据值))
        Map<String, Map<String, Double>> pvChargeQtMap = pvDataMap.getOrDefault(FunctionParamVo.PV_TOTAL_BATTERY_GENERATION, Maps.newHashMap());
        //储能DC/DC的累计充电量数据(日期(yy-MM-dd) -> (时间(HH:mm) -> 数据值))
        Map<String, Map<String, Double>> seChargeQtMap = seDataMap.getOrDefault(FunctionParamVo.SE_TOTAL_BATTERY_CHARGE, Maps.newHashMap());
        //储能DC/DC的累计放电量数据(日期(yy-MM-dd) -> (时间(HH:mm) -> 数据值))
        Map<String, Map<String, Double>> sedischargeQtMap = seDataMap.getOrDefault(FunctionParamVo.SE_TOTAL_BATTERY_CHARGE, Maps.newHashMap());
        //负载总耗电量数据(日期(yy-MM-dd) -> (时间(HH:mm) -> 数据值))
        Map<String, Map<String, Double>> loadQtMap = loadDataMap.getOrDefault(FunctionParamVo.TOTAL_POSITIVE_ACTIVE_ENERGY, Maps.newHashMap());

        //对交直流系统的用电成本数据组装处理
        dateList.forEach(date -> {
            //获取等效直流系统用电成本(电网用电成本+光伏用电成本+储能用电成本)
            //获取电网用电成本((关口表正向电量-储能DC/DC充电量) * 电价)
            BigDecimal gridPurchaseCost = BigDecimal.ZERO;
            if (gwPositiveElecMap.containsKey(date) && seChargeQtMap.containsKey(date) && gridPurchaseElectMap.containsKey(date)) {
                gridPurchaseCost = calculateElectricityCost(gwPositiveElecMap.get(date), seChargeQtMap.get(date), gridPurchaseElectMap.get(date));
            }
            //获取光伏用电成本((光伏累计发电量-光伏上网电量) * 电价)
            BigDecimal pvPurchaseCost = BigDecimal.ZERO;
            if (pvChargeQtMap.containsKey(date) && gwInverseElecMap.containsKey(date) && pvConsumeElectMap.containsKey(date)) {
                pvPurchaseCost = calculateElectricityCost(pvChargeQtMap.get(date), gwInverseElecMap.get(date), pvConsumeElectMap.get(date));
            }
            //获取储能用电成本(储能累计放电量 * 电价)
            BigDecimal sePurchaseCost = BigDecimal.ZERO;
            if (sedischargeQtMap.containsKey(date) && seSaleElectMap.containsKey(date)) {
                sePurchaseCost = calculateElectricityCost(sedischargeQtMap.get(date), seSaleElectMap.get(date));
            }
            result.setDcSystemCost(result.getDcSystemCost().add(gridPurchaseCost).add(pvPurchaseCost).add(sePurchaseCost));

            //获取交流系统用电成本
            //获取负载用电成本((负载总耗电量/负载侧整流器效率(0.95)) * 电价)
            if (loadQtMap.containsKey(date) && gridPurchaseElectMap.containsKey(date)) {
                result.setAcSystemCost(result.getAcSystemCost().add(calculateElectricityCost(loadQtMap.get(date), 0.95, gridPurchaseElectMap.get(date))));
            }
        });
        //计算交直流系统的系统损耗电量
        //获取交流配电柜关口表的正向有功电量和反向有功电量数据
        Map<String, RealDataModel> gateRealDataMap = deviceService.getDeviceFunctionsRealDataByIds(Collections.singleton(DeviceIdParamVo.AC_GGD_METER_ID),
                        String.join(FileUtil.COMMA, FunctionParamVo.TOTAL_POSITIVE_ACTIVE_ENERGY, FunctionParamVo.TOTAL_INVERSE_ACTIVE_ENERGY), 1).getData()
                .get(DeviceIdParamVo.AC_GGD_METER_ID);
        //获取光储一体机直流表的正向有功电量和反向有功电量数据
        Map<String, RealDataModel> gstDcRealDataMap = deviceService.getDeviceFunctionsRealDataByIds(Collections.singleton(DeviceIdParamVo.GST_DC_METER_ID),
                        String.join(FileUtil.COMMA, FunctionParamVo.TOTAL_POSITIVE_ACTIVE_ENERGY, FunctionParamVo.TOTAL_INVERSE_ACTIVE_ENERGY), 1).getData()
                .get(DeviceIdParamVo.GST_DC_METER_ID);
        //获取负载系统的总耗电量
        Map<String, RealDataModel> loadRealDataMap = deviceService.getDeviceFunctionsRealDataByIds(Collections.singleton(DeviceIdParamVo.DC_GGD_LOAD_ID),
                FunctionParamVo.TOTAL_POSITIVE_ACTIVE_ENERGY, 1).getData().get(DeviceIdParamVo.DC_GGD_LOAD_ID);

        //获取直流系统的系统损耗电量(交流配电柜关口表(正向有功电量)-光储一体机直流表(正向有功电量)+光储一体机直流表(反向有功电量)-交流配电柜关口表(反向有功电量)-直流配电柜(负载)表(正向有功电量))
        *//*if (gateRealDataMap.containsKey(FunctionParamVo.TOTAL_POSITIVE_ACTIVE_ENERGY)) { //交流配电柜关口表(正向有功电量-(934.8))
            RealDataModel realDataModel = gateRealDataMap.get(FunctionParamVo.TOTAL_POSITIVE_ACTIVE_ENERGY);
            if (realDataModel != null && StringUtil.isNotEmpty(realDataModel.getDataValue())) {
                result.setDcSystemLoss(result.getDcSystemLoss() + (DoubleUtil.objToDouble(realDataModel.getDataValue()) - 934.8));
            }
        }
        if (gstDcRealDataMap.containsKey(FunctionParamVo.TOTAL_POSITIVE_ACTIVE_ENERGY)) { //光储一体机直流表(正向有功电量-(447.01))
            RealDataModel realDataModel = gstDcRealDataMap.get(FunctionParamVo.TOTAL_POSITIVE_ACTIVE_ENERGY);
            if (realDataModel != null && StringUtil.isNotEmpty(realDataModel.getDataValue())) {
                result.setDcSystemLoss(result.getDcSystemLoss() - (DoubleUtil.objToDouble(realDataModel.getDataValue()) - 447.01));
            }
        }
        if (gstDcRealDataMap.containsKey(FunctionParamVo.TOTAL_INVERSE_ACTIVE_ENERGY)) { //光储一体机直流表(反向有功电量-(1318.24))
            RealDataModel realDataModel = gstDcRealDataMap.get(FunctionParamVo.TOTAL_INVERSE_ACTIVE_ENERGY);
            if (realDataModel != null && StringUtil.isNotEmpty(realDataModel.getDataValue())) {
                result.setDcSystemLoss(result.getDcSystemLoss() + (DoubleUtil.objToDouble(realDataModel.getDataValue()) - 1318.24));
            }
        }
        if (gateRealDataMap.containsKey(FunctionParamVo.TOTAL_INVERSE_ACTIVE_ENERGY)) { //交流配电柜关口表(反向有功电量-(1009.2))
            RealDataModel realDataModel = gateRealDataMap.get(FunctionParamVo.TOTAL_INVERSE_ACTIVE_ENERGY);
            if (realDataModel != null && StringUtil.isNotEmpty(realDataModel.getDataValue())) {
                result.setDcSystemLoss(result.getDcSystemLoss() - (DoubleUtil.objToDouble(realDataModel.getDataValue()) - 1009.2));
            }
        }
        if (loadRealDataMap.containsKey(FunctionParamVo.TOTAL_POSITIVE_ACTIVE_ENERGY)) { //直流配电柜(负载)表(正向有功电量-(20.7))
            RealDataModel realDataModel = loadRealDataMap.get(FunctionParamVo.TOTAL_POSITIVE_ACTIVE_ENERGY);
            if (realDataModel != null && StringUtil.isNotEmpty(realDataModel.getDataValue())) {
                result.setDcSystemLoss(result.getDcSystemLoss() - (DoubleUtil.objToDouble(realDataModel.getDataValue()) - 20.7));
            }
        }*//*
     *//*
          直流系统损耗
          电网侧AC/DC整流器损耗=（关口下网电量-关口上网电量）*（1-0.98）
          光储侧DC/DC变换器损耗=光储并网点反向电量*（1-0.99）
          储能充电损耗=光储并网点下网电量*（1-0.98）
          负载DC/DC损耗=负载正向电量*（1-0.99）
          直流系统总损耗=电网侧AC/DC整流器损耗+光储侧DC/DC变换器损耗+储能充电损耗+负载DC/DC损耗
          说明：不含电缆损耗
         *//*
        //电网侧AC/DC整流器损耗=（关口下网电量-关口上网电量）*（1-0.98）
        if (gateRealDataMap.containsKey(FunctionParamVo.TOTAL_POSITIVE_ACTIVE_ENERGY) && gateRealDataMap.containsKey(FunctionParamVo.TOTAL_INVERSE_ACTIVE_ENERGY)) {
            double supKwh = 0.0;
            double revKwh = 0.0;
            //交流配电柜关口表(正向有功电量-(934.8))
            RealDataModel supDataModel = gateRealDataMap.get(FunctionParamVo.TOTAL_POSITIVE_ACTIVE_ENERGY);
            if (supDataModel != null && StringUtil.isNotEmpty(supDataModel.getDataValue())) {
                supKwh = DoubleUtil.objToDouble(supDataModel.getDataValue()) - 934.8;
            }
            //交流配电柜关口表(反向有功电量-(1009.2))
            RealDataModel revDataModel = gateRealDataMap.get(FunctionParamVo.TOTAL_INVERSE_ACTIVE_ENERGY);
            if (revDataModel != null && StringUtil.isNotEmpty(revDataModel.getDataValue())) {
                revKwh = DoubleUtil.objToDouble(revDataModel.getDataValue()) - 1009.2;
            }
            result.setDcSystemLoss(result.getDcSystemLoss() + (supKwh - revKwh) * (1 - 0.98));
        }
        //光储侧DC/DC变换器损耗=光储并网点反向电量*（1-0.99）
        if (gstDcRealDataMap.containsKey(FunctionParamVo.TOTAL_INVERSE_ACTIVE_ENERGY)) { //光储一体机直流表(反向有功电量-(1318.24))
            RealDataModel realDataModel = gstDcRealDataMap.get(FunctionParamVo.TOTAL_INVERSE_ACTIVE_ENERGY);
            if (realDataModel != null && StringUtil.isNotEmpty(realDataModel.getDataValue())) {
                result.setDcSystemLoss(result.getDcSystemLoss() + (DoubleUtil.objToDouble(realDataModel.getDataValue()) - 1318.24) * (1 - 0.99));
            }
        }
        //储能充电损耗=光储并网点下网电量*（1-0.98）
        if (gstDcRealDataMap.containsKey(FunctionParamVo.TOTAL_POSITIVE_ACTIVE_ENERGY)) { //光储一体机直流表(正向有功电量-(447.01))
            RealDataModel realDataModel = gstDcRealDataMap.get(FunctionParamVo.TOTAL_POSITIVE_ACTIVE_ENERGY);
            if (realDataModel != null && StringUtil.isNotEmpty(realDataModel.getDataValue())) {
                result.setDcSystemLoss(result.getDcSystemLoss() + (DoubleUtil.objToDouble(realDataModel.getDataValue()) - 447.01) * (1 - 0.98));
            }
        }
        //负载DC/DC损耗=负载正向电量*（1-0.99）
        if (loadRealDataMap.containsKey(FunctionParamVo.TOTAL_POSITIVE_ACTIVE_ENERGY)) { //直流配电柜(负载)表(正向有功电量-(20.7))
            RealDataModel realDataModel = loadRealDataMap.get(FunctionParamVo.TOTAL_POSITIVE_ACTIVE_ENERGY);
            if (realDataModel != null && StringUtil.isNotEmpty(realDataModel.getDataValue())) {
                result.setDcSystemLoss(result.getDcSystemLoss() + (DoubleUtil.objToDouble(realDataModel.getDataValue()) - 20.7) * (1 - 0.99));
            }
        }


        //获取光伏系统的总发电量
//        Map<String, RealDataModel> pvRealDataMap = deviceService.getDeviceFunctionsRealDataByIds(Collections.singleton(DeviceIdParamVo.PV_DC_DC_ID),
//                FunctionParamVo.PV_TOTAL_BATTERY_GENERATION, 1).getData().get(DeviceIdParamVo.PV_DC_DC_ID);

        //获取交流系统的系统损耗电量(光伏发电量*(1-光伏逆变器效率(0.96)) + (负载总用电量 / 负载侧整流器效率(0.95)) * (1 - 负载侧整流器效率(0.95)))
        // + (光储一体机直流表的正向电量 * (1 - 储能系统的充放电效率(0.85)))
        *//*if (pvRealDataMap.containsKey(FunctionParamVo.PV_TOTAL_BATTERY_GENERATION)) { //光伏发电量(光伏DCDC(累计放电量)-(988.3))
            RealDataModel realDataModel = pvRealDataMap.get(FunctionParamVo.PV_TOTAL_BATTERY_GENERATION);
            if (realDataModel != null && StringUtil.isNotEmpty(realDataModel.getDataValue())) {
                result.setAcSystemLoss(result.getAcSystemLoss() + (DoubleUtil.objToDouble(realDataModel.getDataValue()) - 998.3) * (1 - 0.96));
            }
        }
        if (loadRealDataMap.containsKey(FunctionParamVo.TOTAL_POSITIVE_ACTIVE_ENERGY)) { //负载总用电量(直流配电柜(负载)表(正向有功电量-(20.7)))
            RealDataModel realDataModel = loadRealDataMap.get(FunctionParamVo.TOTAL_POSITIVE_ACTIVE_ENERGY);
            if (realDataModel != null && StringUtil.isNotEmpty(realDataModel.getDataValue())) {
                result.setAcSystemLoss(result.getAcSystemLoss() + (DoubleUtil.objToDouble(realDataModel.getDataValue()) - 20.7) / 0.95 * (1 - 0.95));
            }
        }
        if (gstDcRealDataMap.containsKey(FunctionParamVo.TOTAL_POSITIVE_ACTIVE_ENERGY)) { //光储一体机直流表(正向有功电量-(447.01))
            RealDataModel realDataModel = gstDcRealDataMap.get(FunctionParamVo.TOTAL_POSITIVE_ACTIVE_ENERGY);
            if (realDataModel != null && StringUtil.isNotEmpty(realDataModel.getDataValue())) {
                result.setAcSystemLoss(result.getAcSystemLoss() + (DoubleUtil.objToDouble(realDataModel.getDataValue()) - 447.01) * (1 - 0.85));
            }
        }*//*
     *//*
          # 交流系统损耗
          变压器损耗=（关口下网电量-关口上网电量）*（1-0.98）
          光储侧DC/DC变换器损耗=光储并网点反向电量*（1-0.96）
          储能充电损耗=光储并网点下网电量*（1-0.96）
          注塑机变频器损耗=负载用电量*（1-0.94）/0.94
          等效交流系统总损耗=变压器损耗+光储侧DC/DC变换器损耗+储能充电损耗+注塑机变频器损耗
          说明：不含谐波附加损耗、电缆损耗
         *//*
        //变压器损耗=（关口下网电量-关口上网电量）*（1-0.98）
        if (gateRealDataMap.containsKey(FunctionParamVo.TOTAL_POSITIVE_ACTIVE_ENERGY) && gateRealDataMap.containsKey(FunctionParamVo.TOTAL_INVERSE_ACTIVE_ENERGY)) {
            double supKwh = 0.0;
            double revKwh = 0.0;
            //交流配电柜关口表(正向有功电量-(934.8))
            RealDataModel supDataModel = gateRealDataMap.get(FunctionParamVo.TOTAL_POSITIVE_ACTIVE_ENERGY);
            if (supDataModel != null && StringUtil.isNotEmpty(supDataModel.getDataValue())) {
                supKwh = DoubleUtil.objToDouble(supDataModel.getDataValue()) - 934.8;
            }
            //交流配电柜关口表(反向有功电量-(1009.2))
            RealDataModel revDataModel = gateRealDataMap.get(FunctionParamVo.TOTAL_INVERSE_ACTIVE_ENERGY);
            if (revDataModel != null && StringUtil.isNotEmpty(revDataModel.getDataValue())) {
                revKwh = DoubleUtil.objToDouble(revDataModel.getDataValue()) - 1009.2;
            }
            result.setAcSystemLoss(result.getAcSystemLoss() + (supKwh - revKwh) * (1 - 0.98));
        }
        //光储侧DC/DC变换器损耗=光储并网点反向电量*（1-0.96）
        if (gstDcRealDataMap.containsKey(FunctionParamVo.TOTAL_INVERSE_ACTIVE_ENERGY)) { //光储一体机直流表(反向有功电量-(1318.24))
            RealDataModel realDataModel = gstDcRealDataMap.get(FunctionParamVo.TOTAL_INVERSE_ACTIVE_ENERGY);
            if (realDataModel != null && StringUtil.isNotEmpty(realDataModel.getDataValue())) {
                result.setAcSystemLoss(result.getAcSystemLoss() + (DoubleUtil.objToDouble(realDataModel.getDataValue()) - 1318.24) * (1 - 0.96));
            }
        }
        //储能充电损耗=光储并网点下网电量*（1-0.96）
        if (gstDcRealDataMap.containsKey(FunctionParamVo.TOTAL_POSITIVE_ACTIVE_ENERGY)) { //光储一体机直流表(正向有功电量-(447.01))
            RealDataModel realDataModel = gstDcRealDataMap.get(FunctionParamVo.TOTAL_POSITIVE_ACTIVE_ENERGY);
            if (realDataModel != null && StringUtil.isNotEmpty(realDataModel.getDataValue())) {
                result.setAcSystemLoss(result.getAcSystemLoss() + (DoubleUtil.objToDouble(realDataModel.getDataValue()) - 447.01) * (1 - 0.96));
            }
        }
        //注塑机变频器损耗=负载用电量*（1-0.94）/0.94
        if (loadRealDataMap.containsKey(FunctionParamVo.TOTAL_POSITIVE_ACTIVE_ENERGY)) { //直流配电柜(负载)表(正向有功电量-(20.7))
            RealDataModel realDataModel = loadRealDataMap.get(FunctionParamVo.TOTAL_POSITIVE_ACTIVE_ENERGY);
            if (realDataModel != null && StringUtil.isNotEmpty(realDataModel.getDataValue())) {
                result.setAcSystemLoss(result.getAcSystemLoss() + (DoubleUtil.objToDouble(realDataModel.getDataValue()) - 20.7) * (1 - 0.94) / 0.94);
            }
        }

        //系统成本
        result.setDcSystemCost(null);
        result.setAcSystemCost(null);
        //系统损耗
        result.setDcSystemLoss(DoubleUtil.getToDouble(result.getDcSystemLoss()));
        result.setAcSystemLoss(DoubleUtil.getToDouble(result.getAcSystemLoss()));
        //平均成本和损耗
        if (type == 1) {
            BigDecimal days = new BigDecimal(dateList.size());
            if (StringUtil.isNotEmpty(result.getDcSystemCost())) {
                result.setDcSystemCost(DoubleUtil.getToBigDecimal(result.getDcSystemCost().divide(days, 2, RoundingMode.HALF_UP)));
            }
            if (StringUtil.isNotEmpty(result.getAcSystemCost())) {
                result.setAcSystemCost(DoubleUtil.getToBigDecimal(result.getAcSystemCost().divide(days, 2, RoundingMode.HALF_UP)));
            }
            result.setDcSystemLoss(DoubleUtil.getToDouble(result.getDcSystemLoss() / days.doubleValue()));
            result.setAcSystemLoss(DoubleUtil.getToDouble(result.getAcSystemLoss() / days.doubleValue()));
        }
        Double systemLoss = result.getDcSystemLoss() + result.getAcSystemLoss();
        if (systemLoss > 0) {
            result.setDcSystemLossPercent(DoubleUtil.getToDouble(result.getDcSystemLoss() / systemLoss * 100));
            result.setAcSystemLossPercent(DoubleUtil.getToDouble(result.getAcSystemLoss() / systemLoss * 100));
        }
        if (StringUtil.isNotEmpty(result.getDcSystemCost()) && StringUtil.isNotEmpty(result.getAcSystemCost())) {
            BigDecimal systemCost = result.getDcSystemCost().add(result.getAcSystemCost());
            if (systemCost.compareTo(BigDecimal.ZERO) > 0) {
                result.setDcSystemCostPercent(DoubleUtil.getToDouble(result.getDcSystemCost().divide(systemCost, 2, RoundingMode.HALF_UP).doubleValue() * 100));
                result.setAcSystemCostPercent(DoubleUtil.getToDouble(result.getAcSystemCost().divide(systemCost, 2, RoundingMode.HALF_UP).doubleValue() * 100));
            }
        }
        return ResponseResult.ok(result);
    }*/

    @Override
    public ResponseResult<SiteAcSystemDto> getSiteAcSystem(String siteId, Integer type) {
        //返回的对象
        SiteAcSystemDto result = new SiteAcSystemDto();

        //参数校验
        if (StringUtil.isEmpty(siteId) || StringUtil.isEmpty(type)) {
            return ResponseResult.paramError(ResponseResult.PARAM_ERROR);
        }

        String startDate = "2026-04-18";
        String endDate = DateUtil.localDateToStr(LocalDate.now());
        //根据站点id查询开始日期
//        SiteInfoDto siteInfo = deviceService.findSiteBasicInfoByIds(Collections.singletonList(siteId)).getData().get(siteId);
//        if (siteInfo != null && CollectionUtils.isNotEmpty(siteInfo.getSiteScenarioTypeDtos())) {
//            if (StringUtil.isNotEmpty(siteInfo.getSiteReadwriteObject())) {
//                JSONObject jsonObject = JSON.parseObject(siteInfo.getSiteReadwriteObject());
//                if (jsonObject.containsKey(SiteFieldParamVo.OFFICIAL_RUN_TIME)) {
//                    startDate = jsonObject.getString(SiteFieldParamVo.OFFICIAL_RUN_TIME);
//                }
//            }
//            if (StringUtil.isEmpty(startDate) && StringUtil.isNotEmpty(siteInfo.getCreateTime())) {
//                startDate = DateUtil.localDateToStr(siteInfo.getCreateTime().toLocalDate());
//            }
//        }

        //获取站点的电价配置(每天)
        //电网电价(分时电价) 日期 -> (分时段时间 -> 电价)
        Map<String, Map<String, ElectTypeDto>> gridElectMap = Maps.newHashMap();

        //获取日期列表
        List<String> dateList = getDateBetween(1, startDate, endDate);

        //根据站点id查询站点电价
        List<ElectConfigEntity> electConfigList = electConfigDao.findElectConfigList(siteId, Collections.singletonList(1), startDate, endDate);
        if (CollectionUtils.isNotEmpty(electConfigList)) {
            //根据多个电站配置id查询电价
            Set<String> electConfigIds = electConfigList.stream().map(ElectConfigEntity::getId).collect(Collectors.toSet());
            Map<String, Map<String, ElectTypeDto>> electTimeFrameMap = TogetherCommonUtil.buildHalfHourMapByConfigId(electTimeFrameDao.findAllByElectConfigIdIn(electConfigIds));
            electConfigList.stream().collect(Collectors.groupingBy(ElectConfigEntity::getModuleType))
                    .forEach((moduleType, electConfigs) -> {
                        for (ElectConfigEntity electConfig : electConfigs) {
                            //电价配置 (半小时时段 -> 电费)
                            Map<String, ElectTypeDto> timeFrameMap = electTimeFrameMap.get(electConfig.getId());
                            if (MapUtils.isNotEmpty(timeFrameMap)) {
                                //过滤日期范围内的日期
                                List<String> electDateList = DateUtil.getDateBetween(1, electConfig.getStartDate(), electConfig.getEndDate());
                                List<String> electFiletrDateList = dateList.stream().filter(electDateList::contains).collect(Collectors.toList());
                                for (String date : electFiletrDateList) {
                                    if (moduleType == 1) { //电网购电电价
                                        gridElectMap.put(date, timeFrameMap);
                                    }
                                }
                            }
                        }
                    });
        }

        //计算直流系统用电成本和交流系统用电成本
        /*
          # 直流系统用电成本
          Edc=直流负载用电量+直流系统总损耗电量-（光储并网点反向电量-光储并网点正向电量-关口反向电量）
          C=∑（Edc*分时电价）
          说明：计算逐个半小时的Edc数值 *对应时段的电价，然后求和

          # 等效交流系统用电成本
          Eac=直流负载用电量+等效交流系统总损耗电量-（光储并网点反向电量-光储并网点正向电量-关口反向电量）
          C=∑（Eac*分时电价）
          说明：计算逐个半小时的Eac数值 *对应时段的电价，然后求和
         */
        //1.获取关口表正向电量和反向电量(功能点标识 -> (日期(yy-MM-dd) -> (时间(HH:mm) -> 数据值)))
        Map<String, Map<String, Map<String, Double>>> gwDataMap = this.getNodeDifHistoryDayMap(DeviceIdParamVo.AC_GGD_METER_ID, new HashSet<>(Arrays
                .asList(FunctionParamVo.TOTAL_POSITIVE_ACTIVE_ENERGY, FunctionParamVo.TOTAL_INVERSE_ACTIVE_ENERGY)), startDate, endDate);
        //2.获取光储一体表的正向电量和反向电量(功能点标识 -> (日期(yy-MM-dd) -> (时间(HH:mm) -> 数据值)))
        Map<String, Map<String, Map<String, Double>>> gstDataMap = this.getNodeDifHistoryDayMap(DeviceIdParamVo.GST_DC_METER_ID, new HashSet<>(Arrays
                .asList(FunctionParamVo.TOTAL_POSITIVE_ACTIVE_ENERGY, FunctionParamVo.TOTAL_INVERSE_ACTIVE_ENERGY)), startDate, endDate);
        //3.获取负载总耗电量(功能点标识 -> (日期(yy-MM-dd) -> (时间(HH:mm) -> 数据值)))
        Map<String, Map<String, Map<String, Double>>> loadDataMap = this.getNodeDifHistoryDayMap(DeviceIdParamVo.DC_GGD_LOAD_ID, Collections
                .singleton(FunctionParamVo.TOTAL_POSITIVE_ACTIVE_ENERGY), startDate, endDate);

        //关口表正向电量数据(日期(yy-MM-dd) -> (时间(HH:mm) -> 数据值))
        Map<String, Map<String, Double>> gwPositiveQtMap = gwDataMap.getOrDefault(FunctionParamVo.TOTAL_POSITIVE_ACTIVE_ENERGY, Maps.newHashMap());
        //关口表反向电量数据(日期(yy-MM-dd) -> (时间(HH:mm) -> 数据值))
        Map<String, Map<String, Double>> gwInverseQtMap = gwDataMap.getOrDefault(FunctionParamVo.TOTAL_INVERSE_ACTIVE_ENERGY, Maps.newHashMap());
        //光储一体表正向电量数据(日期(yy-MM-dd) -> (时间(HH:mm) -> 数据值))
        Map<String, Map<String, Double>> gstPositiveQtMap = gstDataMap.getOrDefault(FunctionParamVo.TOTAL_POSITIVE_ACTIVE_ENERGY, Maps.newHashMap());
        //光储一体表反向电量数据(日期(yy-MM-dd) -> (时间(HH:mm) -> 数据值))
        Map<String, Map<String, Double>> gstInverseQtMap = gstDataMap.getOrDefault(FunctionParamVo.TOTAL_INVERSE_ACTIVE_ENERGY, Maps.newHashMap());
        //负载总耗电量数据(日期(yy-MM-dd) -> (时间(HH:mm) -> 数据值))
        Map<String, Map<String, Double>> loadQtMap = loadDataMap.getOrDefault(FunctionParamVo.TOTAL_POSITIVE_ACTIVE_ENERGY, Maps.newHashMap());

        //对交直流系统的用电成本数据组装处理
        dateList.forEach(date -> {
            //获取等效直流系统用电成本((直流负载用电量+直流系统总损耗电量-(光储并网点反向电量-光储并网点正向电量-关口反向电量)) * 电网电价)
            //获取交流系统用电成本((直流负载用电量+交流系统总损耗电量-(光储并网点反向电量-光储并网点正向电量-关口反向电量)) * 电网电价)
            if (gridElectMap.containsKey(date)) {
                calculatePeriodCost(gwPositiveQtMap.getOrDefault(date, Maps.newHashMap()), gwInverseQtMap.getOrDefault(date, Maps.newHashMap()),
                        gstPositiveQtMap.getOrDefault(date, Maps.newHashMap()), gstInverseQtMap.getOrDefault(date, Maps.newHashMap()),
                        loadQtMap.getOrDefault(date, Maps.newHashMap()), gridElectMap.get(date), result);
            }
        });
        //计算交直流系统的系统损耗电量
        //获取交流配电柜关口表的正向有功电量和反向有功电量数据
        Map<String, RealDataModel> gateRealDataMap = deviceService.getDeviceFunctionsRealDataByIds(Collections.singleton(DeviceIdParamVo.AC_GGD_METER_ID),
                        String.join(FileUtil.COMMA, FunctionParamVo.TOTAL_POSITIVE_ACTIVE_ENERGY, FunctionParamVo.TOTAL_INVERSE_ACTIVE_ENERGY), 1).getData()
                .get(DeviceIdParamVo.AC_GGD_METER_ID);
        //获取光储一体机直流表的正向有功电量和反向有功电量数据
        Map<String, RealDataModel> gstDcRealDataMap = deviceService.getDeviceFunctionsRealDataByIds(Collections.singleton(DeviceIdParamVo.GST_DC_METER_ID),
                        String.join(FileUtil.COMMA, FunctionParamVo.TOTAL_POSITIVE_ACTIVE_ENERGY, FunctionParamVo.TOTAL_INVERSE_ACTIVE_ENERGY), 1).getData()
                .get(DeviceIdParamVo.GST_DC_METER_ID);
        //获取负载系统的总耗电量
        Map<String, RealDataModel> loadRealDataMap = deviceService.getDeviceFunctionsRealDataByIds(Collections.singleton(DeviceIdParamVo.DC_GGD_LOAD_ID),
                FunctionParamVo.TOTAL_POSITIVE_ACTIVE_ENERGY, 1).getData().get(DeviceIdParamVo.DC_GGD_LOAD_ID);

        /*
          获取直流系统的系统损耗电量
          电网侧AC/DC整流器损耗=（关口下网电量-关口上网电量）*（1-0.98）
          光储侧DC/DC变换器损耗=光储并网点反向电量*（1-0.99）
          储能充电损耗=光储并网点下网电量*（1-0.98）
          负载DC/DC损耗=负载正向电量*（1-0.99）
          直流系统总损耗=电网侧AC/DC整流器损耗+光储侧DC/DC变换器损耗+储能充电损耗+负载DC/DC损耗
          说明：不含电缆损耗
         */
        //电网侧AC/DC整流器损耗=（关口下网电量-关口上网电量）*（1-0.98）
        if (gateRealDataMap.containsKey(FunctionParamVo.TOTAL_POSITIVE_ACTIVE_ENERGY) && gateRealDataMap.containsKey(FunctionParamVo.TOTAL_INVERSE_ACTIVE_ENERGY)) {
            double supKwh = 0.0;
            double revKwh = 0.0;
            //交流配电柜关口表(正向有功电量-(934.8))
            RealDataModel supDataModel = gateRealDataMap.get(FunctionParamVo.TOTAL_POSITIVE_ACTIVE_ENERGY);
            if (supDataModel != null && StringUtil.isNotEmpty(supDataModel.getDataValue())) {
                supKwh = DoubleUtil.objToDouble(supDataModel.getDataValue()) - 934.8;
            }
            //交流配电柜关口表(反向有功电量-(1009.2))
            RealDataModel revDataModel = gateRealDataMap.get(FunctionParamVo.TOTAL_INVERSE_ACTIVE_ENERGY);
            if (revDataModel != null && StringUtil.isNotEmpty(revDataModel.getDataValue())) {
                revKwh = DoubleUtil.objToDouble(revDataModel.getDataValue()) - 1009.2;
            }
            result.setDcSystemLoss(result.getDcSystemLoss() + (supKwh - revKwh) * (1 - 0.98));
        }
        //光储侧DC/DC变换器损耗=光储并网点反向电量*（1-0.99）
        if (gstDcRealDataMap.containsKey(FunctionParamVo.TOTAL_INVERSE_ACTIVE_ENERGY)) { //光储一体机直流表(反向有功电量-(1318.24))
            RealDataModel realDataModel = gstDcRealDataMap.get(FunctionParamVo.TOTAL_INVERSE_ACTIVE_ENERGY);
            if (realDataModel != null && StringUtil.isNotEmpty(realDataModel.getDataValue())) {
                result.setDcSystemLoss(result.getDcSystemLoss() + (DoubleUtil.objToDouble(realDataModel.getDataValue()) - 1318.24) * (1 - 0.99));
            }
        }
        //储能充电损耗=光储并网点下网电量*（1-0.98）
        if (gstDcRealDataMap.containsKey(FunctionParamVo.TOTAL_POSITIVE_ACTIVE_ENERGY)) { //光储一体机直流表(正向有功电量-(447.01))
            RealDataModel realDataModel = gstDcRealDataMap.get(FunctionParamVo.TOTAL_POSITIVE_ACTIVE_ENERGY);
            if (realDataModel != null && StringUtil.isNotEmpty(realDataModel.getDataValue())) {
                result.setDcSystemLoss(result.getDcSystemLoss() + (DoubleUtil.objToDouble(realDataModel.getDataValue()) - 447.01) * (1 - 0.98));
            }
        }
        //负载DC/DC损耗=负载正向电量*（1-0.99）
        if (loadRealDataMap.containsKey(FunctionParamVo.TOTAL_POSITIVE_ACTIVE_ENERGY)) { //直流配电柜(负载)表(正向有功电量-(20.7))
            RealDataModel realDataModel = loadRealDataMap.get(FunctionParamVo.TOTAL_POSITIVE_ACTIVE_ENERGY);
            if (realDataModel != null && StringUtil.isNotEmpty(realDataModel.getDataValue())) {
                result.setDcSystemLoss(result.getDcSystemLoss() + (DoubleUtil.objToDouble(realDataModel.getDataValue()) - 20.7) * (1 - 0.99));
            }
        }

        /*
          获取交流系统的系统损耗电量
          变压器损耗=（关口下网电量-关口上网电量）*（1-0.98）
          光储侧DC/DC变换器损耗=光储并网点反向电量*（1-0.96）
          储能充电损耗=光储并网点下网电量*（1-0.96）
          注塑机变频器损耗=负载用电量*（1-0.94）/0.94
          等效交流系统总损耗=变压器损耗+光储侧DC/DC变换器损耗+储能充电损耗+注塑机变频器损耗
          说明：不含谐波附加损耗、电缆损耗
         */
        //变压器损耗=（关口下网电量-关口上网电量）*（1-0.98）
        if (gateRealDataMap.containsKey(FunctionParamVo.TOTAL_POSITIVE_ACTIVE_ENERGY) && gateRealDataMap.containsKey(FunctionParamVo.TOTAL_INVERSE_ACTIVE_ENERGY)) {
            double supKwh = 0.0;
            double revKwh = 0.0;
            //交流配电柜关口表(正向有功电量-(934.8))
            RealDataModel supDataModel = gateRealDataMap.get(FunctionParamVo.TOTAL_POSITIVE_ACTIVE_ENERGY);
            if (supDataModel != null && StringUtil.isNotEmpty(supDataModel.getDataValue())) {
                supKwh = DoubleUtil.objToDouble(supDataModel.getDataValue()) - 934.8;
            }
            //交流配电柜关口表(反向有功电量-(1009.2))
            RealDataModel revDataModel = gateRealDataMap.get(FunctionParamVo.TOTAL_INVERSE_ACTIVE_ENERGY);
            if (revDataModel != null && StringUtil.isNotEmpty(revDataModel.getDataValue())) {
                revKwh = DoubleUtil.objToDouble(revDataModel.getDataValue()) - 1009.2;
            }
            result.setAcSystemLoss(result.getAcSystemLoss() + (supKwh - revKwh) * (1 - 0.98));
        }
        //光储侧DC/DC变换器损耗=光储并网点反向电量*（1-0.96）
        if (gstDcRealDataMap.containsKey(FunctionParamVo.TOTAL_INVERSE_ACTIVE_ENERGY)) { //光储一体机直流表(反向有功电量-(1318.24))
            RealDataModel realDataModel = gstDcRealDataMap.get(FunctionParamVo.TOTAL_INVERSE_ACTIVE_ENERGY);
            if (realDataModel != null && StringUtil.isNotEmpty(realDataModel.getDataValue())) {
                result.setAcSystemLoss(result.getAcSystemLoss() + (DoubleUtil.objToDouble(realDataModel.getDataValue()) - 1318.24) * (1 - 0.96));
            }
        }
        //储能充电损耗=光储并网点下网电量*（1-0.96）
        if (gstDcRealDataMap.containsKey(FunctionParamVo.TOTAL_POSITIVE_ACTIVE_ENERGY)) { //光储一体机直流表(正向有功电量-(447.01))
            RealDataModel realDataModel = gstDcRealDataMap.get(FunctionParamVo.TOTAL_POSITIVE_ACTIVE_ENERGY);
            if (realDataModel != null && StringUtil.isNotEmpty(realDataModel.getDataValue())) {
                result.setAcSystemLoss(result.getAcSystemLoss() + (DoubleUtil.objToDouble(realDataModel.getDataValue()) - 447.01) * (1 - 0.96));
            }
        }
        //注塑机变频器损耗=负载用电量*（1-0.94）/0.94
        if (loadRealDataMap.containsKey(FunctionParamVo.TOTAL_POSITIVE_ACTIVE_ENERGY)) { //直流配电柜(负载)表(正向有功电量-(20.7))
            RealDataModel realDataModel = loadRealDataMap.get(FunctionParamVo.TOTAL_POSITIVE_ACTIVE_ENERGY);
            if (realDataModel != null && StringUtil.isNotEmpty(realDataModel.getDataValue())) {
                result.setAcSystemLoss(result.getAcSystemLoss() + (DoubleUtil.objToDouble(realDataModel.getDataValue()) - 20.7) * (1 - 0.94) / 0.94);
            }
        }

        //系统成本
        result.setDcSystemCost(DoubleUtil.getToBigDecimal(result.getDcSystemCost()));
        result.setAcSystemCost(DoubleUtil.getToBigDecimal(result.getAcSystemCost()));
        //系统损耗
        result.setDcSystemLoss(DoubleUtil.getToDouble(result.getDcSystemLoss()));
        result.setAcSystemLoss(DoubleUtil.getToDouble(result.getAcSystemLoss()));
        //平均成本和损耗
        if (type == 1) {
            BigDecimal days = new BigDecimal(dateList.size());
            if (StringUtil.isNotEmpty(result.getDcSystemCost())) {
                result.setDcSystemCost(DoubleUtil.getToBigDecimal(result.getDcSystemCost().divide(days, 2, RoundingMode.HALF_UP)));
            }
            if (StringUtil.isNotEmpty(result.getAcSystemCost())) {
                result.setAcSystemCost(DoubleUtil.getToBigDecimal(result.getAcSystemCost().divide(days, 2, RoundingMode.HALF_UP)));
            }
            result.setDcSystemLoss(DoubleUtil.getToDouble(result.getDcSystemLoss() / days.doubleValue()));
            result.setAcSystemLoss(DoubleUtil.getToDouble(result.getAcSystemLoss() / days.doubleValue()));
        }
        Double systemLoss = result.getDcSystemLoss() + result.getAcSystemLoss();
        if (systemLoss > 0) {
            result.setDcSystemLossPercent(DoubleUtil.getToDouble(result.getDcSystemLoss() / systemLoss * 100));
            result.setAcSystemLossPercent(DoubleUtil.getToDouble(result.getAcSystemLoss() / systemLoss * 100));
        }
        if (StringUtil.isNotEmpty(result.getDcSystemCost()) && StringUtil.isNotEmpty(result.getAcSystemCost())) {
            BigDecimal systemCost = result.getDcSystemCost().add(result.getAcSystemCost());
            if (systemCost.compareTo(BigDecimal.ZERO) > 0) {
                result.setDcSystemCostPercent(DoubleUtil.getToDouble(result.getDcSystemCost().divide(systemCost, 2, RoundingMode.HALF_UP).doubleValue() * 100));
                result.setAcSystemCostPercent(DoubleUtil.getToDouble(result.getAcSystemCost().divide(systemCost, 2, RoundingMode.HALF_UP).doubleValue() * 100));
            }
        }
        return ResponseResult.ok(result);
    }

    @Override
    public ResponseResult<SiteQtCurveDto> getSiteQtCurve(String siteId, Integer type, Integer dateType) {
        //返回的对象
        SiteQtCurveDto result = new SiteQtCurveDto();

        //日期类型 1-日(最近15天) 2-月(最近6个月) 3-年(最近6年)
        String timeInterval;
        String startTime;
        String endTime;
        List<String> dateList;
        //时间格式间隔 1-(yyyy-MM-dd HH:mm:ss) 2-(yyyy-MM-dd) 3-(yyyy-MM) 4-(yyyy) 5-(HH:mm:ss) 6-(HH:mm) 7-(MM-dd)
        String formatInterval;
        switch (dateType) {
            case 1:
                timeInterval = "1d";
                startTime = DateUtil.getDayStart(DateUtil.localDateToStr(LocalDate.now().minusDays(14)));
                endTime = DateUtil.localDateTimeToStr(LocalDateTime.now());
                dateList = DateUtil.getCustomDateBetween(dateType, startTime, endTime);
                formatInterval = "MM/dd";
                break;
            case 2:
                timeInterval = "1n";
                startTime = DateUtil.getDayStart(DateUtil.localDateToStr(LocalDate.now().minusMonths(5).with(TemporalAdjusters.firstDayOfMonth())));
                endTime = DateUtil.localDateTimeToStr(LocalDateTime.now());
                dateList = DateUtil.getCustomDateBetween(dateType, startTime, endTime);
                formatInterval = "yyyy/MM";
                break;
            case 3:
                timeInterval = "1y";
                startTime = DateUtil.getDayStart(DateUtil.localDateToStr(LocalDate.now().minusYears(5).with(TemporalAdjusters.firstDayOfYear())));
                endTime = DateUtil.localDateTimeToStr(LocalDateTime.now());
                dateList = DateUtil.getCustomDateBetween(dateType, startTime, endTime);
                formatInterval = "yyyy";
                break;
            default:
                return ResponseResult.paramError(ResponseResult.PARAM_ERROR);
        }
        //类型 1-关口 2-直流母线 3-光伏 4-储能 5-负载
        String deviceId;
        List<String> functionLogos;
        switch (type) {
            case 1:
            case 2:
                deviceId = DeviceIdParamVo.AC_GGD_METER_ID;
                functionLogos = Arrays.asList(FunctionParamVo.TOTAL_POSITIVE_ACTIVE_ENERGY, FunctionParamVo.TOTAL_INVERSE_ACTIVE_ENERGY);
                break;
//            case 2:
//                deviceId = DeviceIdParamVo.BUS_AC_METER_ID;
//                functionLogos = Arrays.asList(FunctionParamVo.TOTAL_POSITIVE_ACTIVE_ENERGY, FunctionParamVo.TOTAL_INVERSE_ACTIVE_ENERGY);
//                break;
            case 3:
                deviceId = DeviceIdParamVo.PV_DC_DC_ID;
                functionLogos = Collections.singletonList(FunctionParamVo.PV_TOTAL_BATTERY_GENERATION);
                break;
            case 4:
                deviceId = DeviceIdParamVo.SE_DC_DC_ID;
                functionLogos = Arrays.asList(FunctionParamVo.SE_TOTAL_BATTERY_CHARGE, FunctionParamVo.SE_TOTAL_BATTERY_DISCHARGE);
                break;
            case 5:
                deviceId = DeviceIdParamVo.DC_GGD_LOAD_ID;
                functionLogos = Collections.singletonList(FunctionParamVo.TOTAL_POSITIVE_ACTIVE_ENERGY);
                break;
            default:
                return ResponseResult.paramError(ResponseResult.PARAM_ERROR);
        }
        DeviceHistoryQueryVo deviceQueryVo = new DeviceHistoryQueryVo();
        deviceQueryVo.setDeviceIds(Collections.singleton(deviceId));
        deviceQueryVo.setFunctionLogos(new HashSet<>(functionLogos));
        deviceQueryVo.setStartTime(startTime);
        deviceQueryVo.setEndTime(endTime);
        deviceQueryVo.setTimeInterval(timeInterval);
        Map<String, List<NodeDifHistoryDto>> deviceDataMap = dataService.findNodeDifHistoryListFeign(deviceQueryVo).getData().get(deviceId);
        if (MapUtils.isNotEmpty(deviceDataMap)) {
            //对数据进行组装
            //日期 ->(功能点 -> 电量值)
            Map<String, Map<String, Double>> dataMap = deviceDataMap.values().stream().flatMap(Collection::stream)
                    .filter(c -> StringUtil.isNotEmpty(c.getFirstDateTime()))
                    .collect(Collectors.groupingBy(c -> {
                        String dateFormat = format(strToDate(c.getFirstDateTime()), formatInterval);
                        if (StringUtil.isNotEmpty(dateFormat)) {
                            return dateFormat;
                        }
                        return c.getFirstDateTime();
                    }, Collectors.groupingBy(NodeDifHistoryDto::getFunctionLogo, Collectors.summingDouble(
                            c -> DoubleUtil.getObjSub(c.getFirstDataValue(), c.getLastDataValue())))));

            for (String date : dateList) {
                result.getDateList().add(date);
                if (dataMap.containsKey(date)) {
                    Map<String, Double> functionMap = dataMap.get(date);
                    for (String functionLogo : functionLogos) {
                        switch (functionLogo) {
                            case FunctionParamVo.TOTAL_POSITIVE_ACTIVE_ENERGY:
                            case FunctionParamVo.SE_TOTAL_BATTERY_CHARGE:
                            case FunctionParamVo.PV_TOTAL_BATTERY_GENERATION:
                                if (functionMap.containsKey(functionLogo)) {
                                    result.getCurve1List().add(DoubleUtil.getToDouble(functionMap.get(functionLogo)));
                                } else {
                                    result.getCurve1List().add(0.0);
                                }
                                break;
                            case FunctionParamVo.TOTAL_INVERSE_ACTIVE_ENERGY:
                            case FunctionParamVo.SE_TOTAL_BATTERY_DISCHARGE:
                                if (functionMap.containsKey(functionLogo)) {
                                    result.getCurve2List().add(DoubleUtil.getToDouble(functionMap.get(functionLogo)));
                                } else {
                                    result.getCurve2List().add(0.0);
                                }
                                break;
                        }
                    }
                } else {
                    if (functionLogos.size() > 1) {
                        result.getCurve1List().add(0.0);
                        result.getCurve2List().add(0.0);
                    } else {
                        result.getCurve1List().add(0.0);
                    }
                }
            }

        }
        return ResponseResult.ok(result);
    }

    @Override
    public ResponseResult<List<SiteAlarmDto>> getSiteAlarmList(String siteId) {
        //返回的集合
        List<SiteAlarmDto> resultList = Lists.newArrayList();
        //根据站点id查询设备数据
        List<DeviceBasicInfoDto> deviceInfoList = deviceService.findDeviceBasicInfoBySiteIds(Collections.singletonList(siteId), null)
                .getData().get(siteId);
        if (CollectionUtils.isNotEmpty(deviceInfoList)) {
            Map<String, String> deviceIdNameMap = deviceInfoList.stream().collect(Collectors.toMap(DeviceBasicInfoDto::getId,
                    DeviceBasicInfoDto::getDeviceName, (k1, k2) -> k1));

            //根据查询条件查询告警信息
            DeviceAlarmEventQueryVo eventQueryVo = new DeviceAlarmEventQueryVo();
            eventQueryVo.setDeviceIds(JSON.toJSONString(deviceIdNameMap.keySet()));
            eventQueryVo.setEventStatus(0);
            eventQueryVo.setStartDate(DateUtil.localDateToStr(LocalDate.now().minusDays(6)));
            eventQueryVo.setEndDate(DateUtil.localDateToStr(LocalDate.now()));
            eventQueryVo.setPage(1);
            eventQueryVo.setSize(20);
            //对数据进行组装
            resultList = deviceService.findAllDeviceEventList(eventQueryVo).getData().getItems().stream().map(event -> {
                SiteAlarmDto result = new SiteAlarmDto();
                BeanUtils.copyProperties(event, result);
                result.setDeviceName(deviceIdNameMap.get(event.getDeviceId()));
                return result;
            }).collect(Collectors.toList());
        }
        return ResponseResult.ok(resultList);
    }

    @Override
    public ResponseResult<PvDcDcDeviceDto> getPvDcDcDeviceData(String deviceId) {
        //返回的对象
        PvDcDcDeviceDto result = new PvDcDcDeviceDto();

        deviceId = DeviceIdParamVo.PV_DC_DC_ID;
        //根据设备id查询设备名称
        DeviceBasicInfoDto deviceInfo = deviceService.findDeviceBasicInfoByIds(Collections.singletonList(deviceId)).getData().get(deviceId);
        if (deviceInfo == null) {
            return ResponseResult.paramError("设备不存在");
        }
        //获取设备功能点数据
        String functionLogos = String.join(FileUtil.COMMA, FunctionParamVo.HIGH_TOTAL_VOLTAGE, FunctionParamVo.LOW_TOTAL_VOLTAGE,
                FunctionParamVo.LOW_TOTAL_CURRENT, FunctionParamVo.ACTIVE_POWER);
        Map<String, RealDataModel> realDataMap = deviceService.getDeviceFunctionsRealDataByIds(Collections.singleton(deviceId), functionLogos, 2)
                .getData().get(deviceId);
        if (MapUtils.isNotEmpty(realDataMap)) {
            if (realDataMap.containsKey(FunctionParamVo.HIGH_TOTAL_VOLTAGE)) { //母线电压
                result.setBusVoltage(DoubleUtil.objToDouble(realDataMap.get(FunctionParamVo.HIGH_TOTAL_VOLTAGE).getDataValue(), 2, null));
            }
            if (realDataMap.containsKey(FunctionParamVo.LOW_TOTAL_CURRENT)) { //母线电流
                result.setBusCurrent(DoubleUtil.objToDouble(realDataMap.get(FunctionParamVo.LOW_TOTAL_CURRENT).getDataValue(), 2, null));
            }
            if (realDataMap.containsKey(FunctionParamVo.LOW_TOTAL_VOLTAGE)) { //MPPT电压
                result.setMpptVoltage(DoubleUtil.objToDouble(realDataMap.get(FunctionParamVo.LOW_TOTAL_VOLTAGE).getDataValue(), 2, null));
            }
            if (realDataMap.containsKey(FunctionParamVo.ACTIVE_POWER)) { //功率
                result.setPower(DoubleUtil.objToDouble(realDataMap.get(FunctionParamVo.ACTIVE_POWER).getDataValue(), 2, null));
            }
        }
        DeviceHistoryQueryVo deviceQueryVo = new DeviceHistoryQueryVo();
        deviceQueryVo.setDeviceIds(Collections.singleton(deviceId));
        deviceQueryVo.setFunctionLogos(Collections.singleton(FunctionParamVo.PV_TOTAL_BATTERY_GENERATION));
        deviceQueryVo.setStartTime(DateUtil.getDayStart(DateUtil.localDateToStr(LocalDate.now())));
        deviceQueryVo.setEndTime(DateUtil.localDateTimeToStr(LocalDateTime.now()));
        deviceQueryVo.setTimeInterval("1d");
        Map<String, List<NodeDifHistoryDto>> deviceDataMap = dataService.findNodeDifHistoryListFeign(deviceQueryVo).getData().get(deviceId);
        if (MapUtils.isNotEmpty(deviceDataMap)) { //当日发电量
            double dayQt = DoubleUtil.getToDouble(deviceDataMap.values().stream().flatMap(List::stream).mapToDouble(c ->
                    DoubleUtil.getObjSub(c.getFirstDataValue(), c.getLastDataValue())).sum());
            result.setDayQt(dayQt);
        }
        return ResponseResult.ok(result);
    }

    @Override
    public ResponseResult<SeCabinetDeviceDto> getSeCabinetDeviceData(String seDcIds, String batteryIds) {
        //返回的对象
        SeCabinetDeviceDto result = new SeCabinetDeviceDto();
        seDcIds = DeviceIdParamVo.SE_DC_DC_ID;
        batteryIds = DeviceIdParamVo.SE_BMS_ID;

        //获取储能DC/DC的数据
        String seFunctionLogos = String.join(FileUtil.COMMA, FunctionParamVo.LOW_TOTAL_VOLTAGE, FunctionParamVo.LOW_TOTAL_CURRENT, FunctionParamVo.PCS_ACTIVE_POWER);
        Map<String, RealDataModel> seDcDcRealDataMap = deviceService.getDeviceFunctionsRealDataByIds(Collections.singleton(seDcIds), seFunctionLogos, 2)
                .getData().get(seDcIds);
        if (MapUtils.isNotEmpty(seDcDcRealDataMap)) {
            if (seDcDcRealDataMap.containsKey(FunctionParamVo.LOW_TOTAL_VOLTAGE)) { //母线电压
                result.setBusVoltage(DoubleUtil.objToDouble(seDcDcRealDataMap.get(FunctionParamVo.LOW_TOTAL_VOLTAGE).getDataValue(), 2, null));
            }
            if (seDcDcRealDataMap.containsKey(FunctionParamVo.LOW_TOTAL_CURRENT)) { //母线电流
                result.setBusCurrent(DoubleUtil.objToDouble(seDcDcRealDataMap.get(FunctionParamVo.LOW_TOTAL_CURRENT).getDataValue(), 2, null));
            }
            if (seDcDcRealDataMap.containsKey(FunctionParamVo.PCS_ACTIVE_POWER)) { //功率
                result.setPower(DoubleUtil.objToDouble(seDcDcRealDataMap.get(FunctionParamVo.PCS_ACTIVE_POWER).getDataValue(), 2, null));
            }
        }

        //获取储能电池蔟数据
        String batteryFunctionLogos = String.join(FileUtil.COMMA, FunctionParamVo.BATTERY_TOTAL_VOLTAGE, FunctionParamVo.BATTERY_TOTAL_CURRENT,
                FunctionParamVo.RUN_STATE, FunctionParamVo.SOC, FunctionParamVo.MAX_CELL_VOLTAGE, FunctionParamVo.MIN_CELL_VOLTAGE,
                FunctionParamVo.MAX_CELL_TEMPERATURE, FunctionParamVo.MIN_CELL_TEMPERATURE);
        Map<String, RealDataModel> batteryRealDataMap = deviceService.getDeviceFunctionsRealDataByIds(Collections.singleton(batteryIds),
                batteryFunctionLogos, 2).getData().get(batteryIds);
        if (MapUtils.isNotEmpty(batteryRealDataMap)) {
            if (batteryRealDataMap.containsKey(FunctionParamVo.BATTERY_TOTAL_VOLTAGE)) { //电池总电压
                result.setBatteryVoltage(DoubleUtil.objToDouble(batteryRealDataMap.get(FunctionParamVo.BATTERY_TOTAL_VOLTAGE).getDataValue(), 2, null));
            }
            if (batteryRealDataMap.containsKey(FunctionParamVo.BATTERY_TOTAL_CURRENT)) { //电池总电流
                result.setBatteryCurrent(DoubleUtil.objToDouble(batteryRealDataMap.get(FunctionParamVo.BATTERY_TOTAL_CURRENT).getDataValue(), 2, null));
            }
            if (batteryRealDataMap.containsKey(FunctionParamVo.RUN_STATE) && StringUtil.isNotEmpty(batteryRealDataMap.get(FunctionParamVo.RUN_STATE).getDataValue())) { //运行模式
                String dataValue = String.valueOf(batteryRealDataMap.get(FunctionParamVo.RUN_STATE).getDataValue());
                result.setRunMode((int) Double.parseDouble(dataValue));
                //根据电池蔟id查询电池簇的运行状态枚举值数据
                Optional<ModelFunctionListDto> optional = deviceService.findDeviceFunctionListByIds(Collections.singletonList(batteryIds)).getData()
                        .values().stream().flatMap(Collection::stream).filter(m -> Objects.equals(FunctionParamVo.RUN_STATE, m.getFunctionLogo()))
                        .findFirst();
                if (optional.isPresent()) {
                    ModelFunctionListDto modelFunction = optional.get();
                    if (StringUtil.isNotEmpty(modelFunction.getDataObject())) {
                        JSONArray enumArray = JSON.parseObject(modelFunction.getDataObject()).getJSONArray("enumArray");
                        if (CollectionUtils.isNotEmpty(enumArray)) {
                            for (Object enumObj : enumArray) {
                                JSONObject enumDto = JSON.parseObject(String.valueOf(enumObj));
                                if (enumDto != null && StringUtil.isNotEmpty(enumDto.get("id")) && Objects.equals(enumDto.getString("id"), dataValue)) {
                                    result.setRunModeName(enumDto.getString("name"));
                                    break;
                                }
                            }
                        }
                    }
                }
            }
            if (batteryRealDataMap.containsKey(FunctionParamVo.SOC)) { //SOC
                result.setSoc(DoubleUtil.objToDouble(batteryRealDataMap.get(FunctionParamVo.SOC).getDataValue(), 2, null));
            }
            if (batteryRealDataMap.containsKey(FunctionParamVo.MAX_CELL_VOLTAGE)) { //最高单体电压
                result.setMaxVoltage(DoubleUtil.objToDouble(batteryRealDataMap.get(FunctionParamVo.MAX_CELL_VOLTAGE).getDataValue(), 2, null));
            }
            if (batteryRealDataMap.containsKey(FunctionParamVo.MIN_CELL_VOLTAGE)) { //最低单体电压
                result.setMinVoltage(DoubleUtil.objToDouble(batteryRealDataMap.get(FunctionParamVo.MIN_CELL_VOLTAGE).getDataValue(), 2, null));
            }
            if (batteryRealDataMap.containsKey(FunctionParamVo.MAX_CELL_TEMPERATURE)) { //最高单体温度
                result.setMaxTemp(DoubleUtil.objToDouble(batteryRealDataMap.get(FunctionParamVo.MAX_CELL_TEMPERATURE).getDataValue(), 2, null));
            }
            if (batteryRealDataMap.containsKey(FunctionParamVo.MIN_CELL_TEMPERATURE)) { //最低单体温度
                result.setMinTemp(DoubleUtil.objToDouble(batteryRealDataMap.get(FunctionParamVo.MIN_CELL_TEMPERATURE).getDataValue(), 2, null));
            }
        }
        return ResponseResult.ok(result);
    }

    @Override
    public ResponseResult<AcGGDDeviceDto> getAcGGDDeviceData(String deviceId) {
        //返回的对象
        AcGGDDeviceDto result = new AcGGDDeviceDto();
        deviceId = DeviceIdParamVo.AC_GGD_METER_ID;
        //获取关口设备实时数据
        String functionLogos = String.join(FileUtil.COMMA, FunctionParamVo.A_PHASE_CURRENT, FunctionParamVo.B_PHASE_CURRENT,
                FunctionParamVo.C_PHASE_CURRENT, FunctionParamVo.TOTAL_ACTIVE_POWER);
        Map<String, RealDataModel> realDataMap = deviceService.getDeviceFunctionsRealDataByIds(Collections.singleton(deviceId),
                functionLogos, 2).getData().get(deviceId);
        if (MapUtils.isNotEmpty(realDataMap)) {
            if (realDataMap.containsKey(FunctionParamVo.TOTAL_ACTIVE_POWER)) { //总有功功率
                result.setPower(DoubleUtil.objToDouble(realDataMap.get(FunctionParamVo.TOTAL_ACTIVE_POWER).getDataValue(), 2, null));
            }
            if (realDataMap.containsKey(FunctionParamVo.A_PHASE_CURRENT)) { //A相电流
                result.setCurrentA(DoubleUtil.objToDouble(realDataMap.get(FunctionParamVo.A_PHASE_CURRENT).getDataValue(), 2, null));
            }
            if (realDataMap.containsKey(FunctionParamVo.B_PHASE_CURRENT)) { //B相电流
                result.setCurrentB(DoubleUtil.objToDouble(realDataMap.get(FunctionParamVo.B_PHASE_CURRENT).getDataValue(), 2, null));
            }
            if (realDataMap.containsKey(FunctionParamVo.C_PHASE_CURRENT)) { //C相电流
                result.setCurrentC(DoubleUtil.objToDouble(realDataMap.get(FunctionParamVo.C_PHASE_CURRENT).getDataValue(), 2, null));
            }
        }
        return ResponseResult.ok(result);
    }

    @Override
    public ResponseResult<DCADDeviceDto> getDCADDeviceData(String deviceId) {
        //返回的对象
        DCADDeviceDto result = new DCADDeviceDto();
        deviceId = DeviceIdParamVo.DC_GGD_LOAD_ID;
        //获取直流配电柜实时数据
        String functionLogos = String.join(FileUtil.COMMA, FunctionParamVo.TOTAL_VOLTAGE, FunctionParamVo.TOTAL_CURRENT,
                FunctionParamVo.TOTAL_ACTIVE_POWER);
        Map<String, RealDataModel> realDataMap = deviceService.getDeviceFunctionsRealDataByIds(Collections.singleton(deviceId),
                functionLogos, 2).getData().get(deviceId);
        if (MapUtils.isNotEmpty(realDataMap)) {
            if (realDataMap.containsKey(FunctionParamVo.TOTAL_VOLTAGE)) { //总电压
                result.setVoltage(DoubleUtil.objToDouble(realDataMap.get(FunctionParamVo.TOTAL_VOLTAGE).getDataValue(), 2, null));
            }
            if (realDataMap.containsKey(FunctionParamVo.TOTAL_CURRENT)) { //总电流
                result.setCurrent(DoubleUtil.objToDouble(realDataMap.get(FunctionParamVo.TOTAL_CURRENT).getDataValue(), 2, null));
            }
            if (realDataMap.containsKey(FunctionParamVo.TOTAL_ACTIVE_POWER)) { //总功率
                result.setPower(DoubleUtil.objToDouble(realDataMap.get(FunctionParamVo.TOTAL_ACTIVE_POWER).getDataValue(), 2, null));
            }
        }
        return ResponseResult.ok(result);
    }

    @Override
    public ResponseResult<DCBusDeviceDto> getDCBusDeviceData(String deviceId) {
        //返回的对象
        DCBusDeviceDto result = new DCBusDeviceDto();
        deviceId = DeviceIdParamVo.DC_BUS_ID;

        //获取直流母线实时数据
        String functionLogos = String.join(FileUtil.COMMA, FunctionParamVo.DC_VOLTAGE, FunctionParamVo.DC_CURRENT,
                FunctionParamVo.ACTIVE_POWER, FunctionParamVo.RUN_MODE);
        Map<String, RealDataModel> realDataMap = deviceService.getDeviceFunctionsRealDataByIds(Collections.singleton(deviceId),
                functionLogos, 2).getData().get(deviceId);

        if (MapUtils.isNotEmpty(realDataMap)) {
            if (realDataMap.containsKey(FunctionParamVo.DC_VOLTAGE)) { //直流电压
                result.setVoltage(DoubleUtil.objToDouble(realDataMap.get(FunctionParamVo.DC_VOLTAGE).getDataValue(), 2, null));
            }
            if (realDataMap.containsKey(FunctionParamVo.DC_CURRENT)) { //直流电流
                result.setCurrent(DoubleUtil.objToDouble(realDataMap.get(FunctionParamVo.DC_CURRENT).getDataValue(), 2, null));
            }
            if (realDataMap.containsKey(FunctionParamVo.ACTIVE_POWER)) { //直流功率
                result.setPower(DoubleUtil.objToDouble(realDataMap.get(FunctionParamVo.ACTIVE_POWER).getDataValue(), 2, null));
            }
            if (realDataMap.containsKey(FunctionParamVo.RUN_MODE)) { //运行模式
                String dataValue = String.valueOf(realDataMap.get(FunctionParamVo.RUN_MODE).getDataValue());
                result.setRunMode((int) Double.parseDouble(dataValue));
                //根据电池蔟id查询电池簇的运行状态枚举值数据
                Optional<ModelFunctionListDto> optional = deviceService.findDeviceFunctionListByIds(Collections.singletonList(deviceId)).getData()
                        .values().stream().flatMap(Collection::stream).filter(m -> Objects.equals(FunctionParamVo.RUN_MODE, m.getFunctionLogo()))
                        .findFirst();
                if (optional.isPresent()) {
                    ModelFunctionListDto modelFunction = optional.get();
                    if (StringUtil.isNotEmpty(modelFunction.getDataObject())) {
                        JSONArray enumArray = JSON.parseObject(modelFunction.getDataObject()).getJSONArray("enumArray");
                        if (CollectionUtils.isNotEmpty(enumArray)) {
                            for (Object enumObj : enumArray) {
                                JSONObject enumDto = JSON.parseObject(String.valueOf(enumObj));
                                if (enumDto != null && StringUtil.isNotEmpty(enumDto.get("id")) && Objects.equals(enumDto.getString("id"), dataValue)) {
                                    result.setRunModeName(enumDto.getString("name"));
                                    break;
                                }
                            }
                        }
                    }
                }
            }

        }

        return ResponseResult.ok(result);
    }

    @Override
    public ResponseResult<DCInjectorDeviceDto> getDCInjectorDeviceData(String deviceId) {
        return null;
    }

    @Override
    public ResponseResult<SiteSystemNearbyDto> getSiteSystemNearby(StorageCountVo storageCountVo) {
        //返回的对象
        SiteSystemNearbyDto result = new SiteSystemNearbyDto();
        //参数校验
        if (isValidStorageCount(storageCountVo)) {
            return ResponseResult.paramError(ResponseResult.PARAM_ERROR);
        }

        //获取储能收益
        StorageCountVo storageCount = new StorageCountVo();
        storageCount.setQueryType(2);
        storageCount.setDataId(DeviceIdParamVo.GST_DC_METER_ID);
        storageCount.setDateType(storageCountVo.getDateType());
        storageCount.setStartDate(storageCountVo.getStartDate());
        storageCount.setEndDate(storageCountVo.getEndDate());
        BeanUtils.copyProperties(storageCountService.countStorageIncome(storageCount).getData(), result);

        //获取负载用电构成分析
        String timeInterval;
        String startTime = DateUtil.getDayStart(storageCountVo.getStartDate());
        String endTime = DateUtil.getDayEnd(storageCountVo.getEndDate());
        switch (storageCountVo.getDateType()) {
            case 1:
                timeInterval = "1d";
                break;
            case 2:
                timeInterval = "1n";
                break;
            case 3:
                timeInterval = "1y";
                break;
            default:
                return ResponseResult.paramError(ResponseResult.PARAM_ERROR);
        }
        //查询关口表的上网电量和下网电量
        Map<String, Double> gatewayMap = getNodeDifHistoryMap(DeviceIdParamVo.AC_GGD_METER_ID, new HashSet<>(Arrays.asList(FunctionParamVo
                .TOTAL_POSITIVE_ACTIVE_ENERGY, FunctionParamVo.TOTAL_INVERSE_ACTIVE_ENERGY)), startTime, endTime, timeInterval);
        //查询光储一体机的下网电量
        Map<String, Double> gstDcMeterMap = getNodeDifHistoryMap(DeviceIdParamVo.GST_DC_METER_ID, new HashSet<>(Arrays.asList(FunctionParamVo
                .TOTAL_POSITIVE_ACTIVE_ENERGY, FunctionParamVo.TOTAL_INVERSE_ACTIVE_ENERGY)), startTime, endTime, timeInterval);
        //查询直流配电柜的下网电量
        Map<String, Double> dcPcMeterMap = getNodeDifHistoryMap(DeviceIdParamVo.DC_GGD_LOAD_ID, Collections.singleton(FunctionParamVo
                .TOTAL_POSITIVE_ACTIVE_ENERGY), startTime, endTime, timeInterval);
        //1.获取电网电量(关口表下网电量(关口表的正向有功电量)-储能高压侧表正向有功电量(光储一体机表的正向有功电量))
        result.setGridQt(DoubleUtil.getToDouble(gatewayMap.getOrDefault(FunctionParamVo.TOTAL_INVERSE_ACTIVE_ENERGY, 0.0)
                - gstDcMeterMap.getOrDefault(FunctionParamVo.TOTAL_POSITIVE_ACTIVE_ENERGY, 0.0)));
        //2.获取直流配电柜电量(直流配电柜的正向有功电量)
        result.setLoadQt(DoubleUtil.getToDouble(dcPcMeterMap.getOrDefault(FunctionParamVo.TOTAL_POSITIVE_ACTIVE_ENERGY, 0.0)));
        //3.获取光储电量(光储一体机表的反向有功电量)
        result.setPvSeQt(DoubleUtil.getToDouble(gstDcMeterMap.getOrDefault(FunctionParamVo.TOTAL_INVERSE_ACTIVE_ENERGY, 0.0)));
        //电量总值
        Double totalQt = DoubleUtil.getToDouble(result.getGridQt() + result.getPvSeQt());
        //计算电网,光储的占比
        if (totalQt != 0) {
            result.setGridPercent(DoubleUtil.getToDouble(result.getGridQt() / totalQt * 100));
            result.setPvSePercent(DoubleUtil.getToDouble(result.getPvSeQt() / totalQt * 100));
        }
        return ResponseResult.ok(result);
    }

    /**
     * 获取设备历史数据(累计电量数据)
     *
     * @param deviceId      设备id
     * @param functionLogos 多个功能点标识
     * @param startTime     开始时间
     * @param endTime       结束时间
     * @param timeInterval  时间间隔
     * @return 设备历史电量数据(功能点标识 - > 电量值)
     */
    private Map<String, Double> getNodeDifHistoryMap(String deviceId, Set<String> functionLogos, String startTime, String endTime, String timeInterval) {
        DeviceHistoryQueryVo deviceQueryVo = new DeviceHistoryQueryVo();
        deviceQueryVo.setDeviceIds(Collections.singleton(deviceId));
        deviceQueryVo.setFunctionLogos(functionLogos);
        deviceQueryVo.setStartTime(startTime);
        deviceQueryVo.setEndTime(endTime);
        deviceQueryVo.setTimeInterval(timeInterval);
        return dataService.findNodeDifHistoryListFeign(deviceQueryVo).getData().get(deviceId).values().stream().flatMap(List::stream)
                .collect(Collectors.groupingBy(NodeDifHistoryDto::getFunctionLogo, Collectors.summingDouble(d ->
                        DoubleUtil.getObjSub(d.getFirstDataValue(), d.getLastDataValue()))));
    }

    /**
     * 获取设备历史数据(每个时段的电量数据)
     *
     * @param deviceId      设备id
     * @param functionLogos 多个功能点标识
     * @param startDate     开始日期
     * @param endDate       结束日期
     * @return 设备历史电量数据(功能点标识 - > ( 时间 - > 电量值))
     */
    private Map<String, Map<String, Map<String, Double>>> getNodeDifHistoryDayMap(String deviceId, Set<String> functionLogos, String startDate, String endDate) {
        DeviceHistoryQueryVo deviceQueryVo = new DeviceHistoryQueryVo();
        deviceQueryVo.setDeviceIds(Collections.singleton(deviceId));
        deviceQueryVo.setFunctionLogos(functionLogos);
        deviceQueryVo.setStartTime(DateUtil.getDayStart(startDate));
        deviceQueryVo.setEndTime(DateUtil.getDayEnd(endDate));
        deviceQueryVo.setTimeInterval("30m");
        return dataService.findNodeDifHistoryListFeign(deviceQueryVo).getData().get(deviceId).values().stream().flatMap(List::stream)
                .collect(Collectors.groupingBy(NodeDifHistoryDto::getFunctionLogo, Collectors.groupingBy(d -> d.getFirstDateTime().substring(0, 10),
                        Collectors.groupingBy(d -> d.getFirstDateTime().substring(11, 16),
                                Collectors.summingDouble(d -> DoubleUtil.getObjSub(d.getFirstDataValue(), d.getLastDataValue()))))));
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

    /**
     * 计算直流系统总损耗电量
     * 电网侧AC/DC整流器损耗=（关口下网电量-关口上网电量）*（1-0.98）
     * 光储侧DC/DC变换器损耗=光储并网点反向电量*（1-0.99）
     * 储能充电损耗=光储并网点下网电量*（1-0.98）
     * 负载DC/DC损耗=负载正向电量*（1-0.99）
     * 直流系统总损耗=电网侧AC/DC整流器损耗+光储侧DC/DC变换器损耗+储能充电损耗+负载DC/DC损耗
     * 说明：不含电缆损耗
     *
     * @param gwSupKwh   关口正向电量
     * @param gwRevKwh   关口反向电量
     * @param gstSupKwh  光储一体表正向电量
     * @param gstRevKwh  光储一体表反向电量
     * @param loadSupKwh 负载正向电量
     * @return 直流系统总损耗电量
     */
    private static Double calculateDcSystemLossQt(Double gwSupKwh, Double gwRevKwh, Double gstSupKwh, Double gstRevKwh, Double loadSupKwh) {
        double result = 0.0;
        //电网侧AC/DC整流器损耗=（关口下网电量-关口上网电量）*（1-0.98）
        if (StringUtil.isNotEmpty(gwSupKwh) && StringUtil.isNotEmpty(gwRevKwh)) {
            result += (gwSupKwh - gwRevKwh) * (1 - 0.98);
        }
        //光储侧DC/DC变换器损耗=光储并网点反向电量*（1-0.99）
        if (StringUtil.isNotEmpty(gstRevKwh)) {
            result += gstRevKwh * (1 - 0.99);
        }
        //储能充电损耗=光储并网点下网电量*（1-0.98）
        if (StringUtil.isNotEmpty(gstSupKwh)) {
            result += gstSupKwh * (1 - 0.98);
        }
        //负载DC/DC损耗=负载正向电量*（1-0.99）
        if (StringUtil.isNotEmpty(loadSupKwh)) {
            result += loadSupKwh * (1 - 0.99);
        }
        return DoubleUtil.getToDouble(result);
    }

    /**
     * 计算交流系统总损耗电量
     * 变压器损耗=（关口下网电量-关口上网电量）*（1-0.98）
     * 光储侧DC/DC变换器损耗=光储并网点反向电量*（1-0.96）
     * 储能充电损耗=光储并网点下网电量*（1-0.96）
     * 注塑机变频器损耗=负载用电量*（1-0.94）/0.94
     * 等效交流系统总损耗=变压器损耗+光储侧DC/DC变换器损耗+储能充电损耗+注塑机变频器损耗
     * 说明：不含谐波附加损耗、电缆损耗
     *
     * @param gwSupKwh   关口正向电量
     * @param gwRevKwh   关口反向电量
     * @param gstSupKwh  光储一体表正向电量
     * @param gstRevKwh  光储一体表反向电量
     * @param loadSupKwh 负载正向电量
     * @return 交流系统总损耗电量
     */
    private static Double calculateAcSystemLossQt(Double gwSupKwh, Double gwRevKwh, Double gstSupKwh, Double gstRevKwh, Double loadSupKwh) {
        double result = 0.0;
        //变压器损耗=（关口下网电量-关口上网电量）*（1-0.98）
        if (StringUtil.isNotEmpty(gwSupKwh) && StringUtil.isNotEmpty(gwRevKwh)) {
            result += (gwSupKwh - gwRevKwh) * (1 - 0.98);
        }
        //光储侧DC/DC变换器损耗=光储并网点反向电量*（1-0.96）
        if (StringUtil.isNotEmpty(gstRevKwh)) {
            result += gstRevKwh * (1 - 0.96);
        }
        //储能充电损耗=光储并网点下网电量*（1-0.96）
        if (StringUtil.isNotEmpty(gstSupKwh)) {
            result += gstSupKwh * (1 - 0.96);
        }
        //注塑机变频器损耗=负载用电量*（1-0.94）/0.94
        if (StringUtil.isNotEmpty(loadSupKwh)) {
            result += loadSupKwh * (1 - 0.94) / 0.94;
        }
        return DoubleUtil.getToDouble(result);
    }

    /**
     * 计算系统用电成本
     * # 直流系统用电成本
     * Edc=直流负载用电量+直流系统总损耗电量-（光储并网点反向电量-光储并网点正向电量-关口反向电量）
     * C=∑（Edc*分时电价）
     * 说明：计算逐个半小时的Edc数值 *对应时段的电价，然后求和
     * <p>
     * # 等效交流系统用电成本
     * Eac=直流负载用电量+等效交流系统总损耗电量-（光储并网点反向电量-光储并网点正向电量-关口反向电量）
     * C=∑（Eac*分时电价）
     * 说明：计算逐个半小时的Eac数值 *对应时段的电价，然后求和
     *
     * @param gwRevKwh   关口反向电量
     * @param gstSupKwh  光储一体表正向电量
     * @param gstRevKwh  光储一体表反向电量
     * @param loadSupKwh 负载正向电量
     * @param lossKwh    损耗电量
     * @param elect      分时电价
     * @return 系统用电成本
     */
    private static BigDecimal calculateSystemCost(Double gwRevKwh, Double gstSupKwh, Double gstRevKwh, Double loadSupKwh, Double lossKwh, BigDecimal elect) {
        //总电量(负载用电量+系统总损耗电量-（光储并网点反向电量-光储并网点正向电量-关口反向电量）)
        //负载用电量+系统总损耗电量-（光储并网点反向电量-光储并网点正向电量-关口反向电量）
        double totalQt = 0.0;
        if (StringUtil.isNotEmpty(loadSupKwh)) {
            totalQt += loadSupKwh;
        }
        if (StringUtil.isNotEmpty(lossKwh)) {
            totalQt += lossKwh;
        }
        if (StringUtil.isNotEmpty(gstRevKwh)) {
            totalQt -= gstRevKwh;
        }
        if (StringUtil.isNotEmpty(gstSupKwh)) {
            totalQt += gstSupKwh;
        }
        if (StringUtil.isNotEmpty(gwRevKwh)) {
            totalQt += gwRevKwh;
        }
        //计算系统用电成本(总电量 * 分时电价)
        return DoubleUtil.getToBigDecimal(new BigDecimal(totalQt).multiply(StringUtil.isEmpty(elect) ? BigDecimal.ZERO : elect));
    }

    /**
     * 计算用电成本
     * //获取等效直流系统用电成本((直流负载用电量+直流系统总损耗电量-(光储并网点反向电量-光储并网点正向电量-关口反向电量)) * 电网电价)
     * //获取交流系统用电成本((直流负载用电量+交流系统总损耗电量-(光储并网点反向电量-光储并网点正向电量-关口反向电量)) * 电网电价)
     *
     * @param gwPositiveQtMap  关口表正向电量数据
     * @param gwInverseQtMap   关口表反向电量数据
     * @param gstPositiveQtMap 光储一体表正向电量数据
     * @param gstInverseQtMap  光储一体表反向电量数据
     * @param loadQtMap        负载总耗电量数据
     * @param priceMap         电价数据
     * @param result           结果实体
     */
    private static void calculatePeriodCost(Map<String, Double> gwPositiveQtMap, Map<String, Double> gwInverseQtMap,
                                            Map<String, Double> gstPositiveQtMap, Map<String, Double> gstInverseQtMap,
                                            Map<String, Double> loadQtMap, Map<String, ElectTypeDto> priceMap,
                                            SiteAcSystemDto result) {
        for (Map.Entry<String, ElectTypeDto> entry : priceMap.entrySet()) {
            String time = entry.getKey(); //时分
            BigDecimal price = entry.getValue().getElectMoney(); //电价
            //关口表正向电量
            Double gwPositiveQt = 0.0;
            if (gwPositiveQtMap.containsKey(time) && StringUtil.isNotEmpty(gwPositiveQtMap.get(time))) {
                gwPositiveQt = gwPositiveQtMap.get(time);
            }
            //关口表反向电量
            Double gwInverseQt = 0.0;
            if (gwInverseQtMap.containsKey(time) && StringUtil.isNotEmpty(gwInverseQtMap.get(time))) {
                gwInverseQt = gwInverseQtMap.get(time);
            }
            //光储一体表正向电量
            Double gstPositiveQt = 0.0;
            if (gstPositiveQtMap.containsKey(time) && StringUtil.isNotEmpty(gstPositiveQtMap.get(time))) {
                gstPositiveQt = gstPositiveQtMap.get(time);
            }
            //光储一体表反向电量
            Double gstInverseQt = 0.0;
            if (gstInverseQtMap.containsKey(time) && StringUtil.isNotEmpty(gstInverseQtMap.get(time))) {
                gstInverseQt = gstInverseQtMap.get(time);
            }
            //关口表正向电量
            Double loadQt = 0.0;
            if (loadQtMap.containsKey(time) && StringUtil.isNotEmpty(loadQtMap.get(time))) {
                loadQt = loadQtMap.get(time);
            }

            //获取直流系统成本
            //获取等效直流系统用电成本((直流负载用电量+直流系统总损耗电量-(光储并网点反向电量-光储并网点正向电量-关口反向电量)) * 电网电价)
            //获取直流系统损耗电量
            Double dcSystemLossQt = calculateDcSystemLossQt(gwPositiveQt, gwInverseQt, gstPositiveQt, gstInverseQt, loadQt);
            BigDecimal dcSystemCost = calculateSystemCost(gwInverseQt, gstPositiveQt, gstInverseQt, loadQt, dcSystemLossQt, price);
            result.setDcSystemCost(result.getDcSystemCost().add(dcSystemCost));
            //获取交流系统成本
            //获取交流系统用电成本((直流负载用电量+交流系统总损耗电量-(光储并网点反向电量-光储并网点正向电量-关口反向电量)) * 电网电价)
            //获取交流系统损耗电量
            Double acSystemLossQt = calculateAcSystemLossQt(gwPositiveQt, gwInverseQt, gstPositiveQt, gstInverseQt, loadQt);
            BigDecimal acSystemCost = calculateSystemCost(gwInverseQt, gstPositiveQt, gstInverseQt, loadQt, acSystemLossQt, price);
            result.setAcSystemCost(result.getAcSystemCost().add(acSystemCost));
        }
    }

}
