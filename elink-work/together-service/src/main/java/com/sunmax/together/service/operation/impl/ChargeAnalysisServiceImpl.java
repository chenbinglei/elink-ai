package com.sunmax.together.service.operation.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.google.common.collect.Maps;
import com.sunmax.common.dto.data.NodeDifHistoryDto;
import com.sunmax.common.dto.device.DeviceBasicInfoDto;
import com.sunmax.common.dto.device.SiteInfoDto;
import com.sunmax.common.util.DateUtil;
import com.sunmax.common.util.DoubleUtil;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.util.StringUtil;
import com.sunmax.common.vo.FunctionLogoParamVo;
import com.sunmax.common.vo.SiteFieldParamVo;
import com.sunmax.common.vo.data.DeviceHistoryQueryVo;
import com.sunmax.together.dao.SiteIncomeDao;
import com.sunmax.together.dao.asset.ElectConfigDao;
import com.sunmax.together.dao.asset.ElectTimeFrameDao;
import com.sunmax.together.dto.operation.chargeAnalysis.SiteIncomeDto;
import com.sunmax.together.dto.operation.chargeAnalysis.SiteInvestIncomeDto;
import com.sunmax.together.dto.operation.chargeAnalysis.SiteOperateIncomeDto;
import com.sunmax.together.dto.operation.chargeAnalysis.SiteYieldStructureDto;
import com.sunmax.together.entity.SiteIncomeEntity;
import com.sunmax.together.entity.assets.ElectConfigEntity;
import com.sunmax.together.entity.assets.ElectTimeFrameEntity;
import com.sunmax.together.mapper.OrderRecordMapper;
import com.sunmax.together.model.ChargeOrderCostModel;
import com.sunmax.together.service.operation.ChargeAnalysisService;
import com.sunmax.together.service.feign.DataService;
import com.sunmax.together.service.feign.DeviceService;
import com.sunmax.together.vo.operation.chargeAnalysis.SiteIncomeVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.compress.utils.Lists;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ChargeAnalysisServiceImpl implements ChargeAnalysisService {

    @Resource
    private SiteIncomeDao siteIncomeDao;

    @Resource
    private OrderRecordMapper orderRecordMapper;

    @Resource
    private ElectConfigDao electConfigDao;

    @Resource
    private ElectTimeFrameDao electTimeFrameDao;

    @Resource
    private DeviceService deviceService;

    @Resource
    private DataService dataService;

    private Boolean isSiteIncomeValid(SiteIncomeVo siteIncomeVo) {
        if (siteIncomeVo == null) {
            return false;
        }

        // 定义需要校验的字段数组
        Object[] fieldsToValidate = {
                siteIncomeVo.getSiteId(),
                siteIncomeVo.getIncomeModelId(),
                siteIncomeVo.getDeviceCost(),
                siteIncomeVo.getConstructionCost()
        };

        // 它同时处理了空和只含空格的字符串
        return Arrays.stream(fieldsToValidate).noneMatch(StringUtil::isEmpty);
    }

    @Override
    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class)
    public ResponseResult<Void> saveSiteIncome(SiteIncomeVo siteIncomeVo) {
        //校验入参
        if (!isSiteIncomeValid(siteIncomeVo)) {
            return ResponseResult.paramError(ResponseResult.PARAM_ERROR);
        }
        //根据站点id查询站点收益测算数据
        List<SiteIncomeEntity> siteIncomeList = siteIncomeDao.findAllBySiteId(siteIncomeVo.getSiteId());

        //新增站点收益测算数据
        if (StringUtil.isEmpty(siteIncomeVo.getId())) {
            //校验该站点是否存在测算数据
            if (CollectionUtils.isNotEmpty(siteIncomeList)) {
                return ResponseResult.paramError("该站点收益测算数据已存在,不允许添加");
            }
            SiteIncomeEntity siteIncomeEntity = new SiteIncomeEntity();
            BeanUtils.copyProperties(siteIncomeVo, siteIncomeEntity);
            siteIncomeDao.save(siteIncomeEntity);
            return ResponseResult.ok();
        } else { //编辑站点收益测算数据
            //校验该站点是否存在测算数据
            siteIncomeList = siteIncomeList.stream().filter(s -> !Objects.equals(siteIncomeVo.getId(), s.getId()))
                    .collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(siteIncomeList)) {
                return ResponseResult.paramError("该站点收益测算数据已存在,不允许编辑");
            }
            Optional<SiteIncomeEntity> optional = siteIncomeDao.findById(siteIncomeVo.getId());
            if (optional.isPresent()) {
                SiteIncomeEntity siteIncomeEntity = new SiteIncomeEntity();
                BeanUtils.copyProperties(siteIncomeVo, siteIncomeEntity);
                siteIncomeEntity.setCreateTime(optional.get().getCreateTime());
                siteIncomeDao.save(siteIncomeEntity);
                return ResponseResult.ok();
            }
        }
        return null;
    }

    @Override
    public ResponseResult<SiteIncomeDto> findSiteIncomeBySiteId(String siteId) {
        //返回的对象
        SiteIncomeDto result = new SiteIncomeDto();
        //根据站点id查询站点测算数据
        List<SiteIncomeEntity> siteIncomeList = siteIncomeDao.findAllBySiteId(siteId);
        if (CollectionUtils.isNotEmpty(siteIncomeList)) {
            BeanUtils.copyProperties(siteIncomeList.get(0), result);
        }
        return ResponseResult.ok(result);
    }

    @Override
    public ResponseResult<SiteInvestIncomeDto> countSiteInvestIncome(String siteId) {
        //返回的对象
        SiteInvestIncomeDto result = new SiteInvestIncomeDto();

        //1.累计收益（元）：整站投运期间的累计收益
        //2.日均收益（元/日）：累计收益 / 投运天数
        //3.月均收益（元/月）：累计收益 / （投运天数 / 30）或 日均收益×30
        //4.年均收益（元/年）：累计收益 / （投运天数 / 365）或 日均收益×365
        //5.年化收益率（%）： 年均收益 / （设备总成本 + 施工总费用 - 建设补贴）×100%
        //6.投资回收周期（年）：（设备总成本 + 施工总费用 - 建设补贴）/  累计收益

        //1.获取开始日期(投运日期或建站创建日期)和结束日期(当天),投运天数
        String startDate = null;
        String endDate = DateUtil.getCurrentDate();
        SiteInfoDto siteInfo = deviceService.findSiteBasicInfoByIds(Collections.singletonList(siteId)).getData().get(siteId);
        if (siteInfo == null) {
            return ResponseResult.paramError("该站点不存在,不允许计算收益分析");
        }
        if (StringUtil.isNotEmpty(siteInfo.getSiteReadwriteObject())) {
            JSONObject jsonObject = JSON.parseObject(siteInfo.getSiteReadwriteObject());
            if (jsonObject.containsKey(SiteFieldParamVo.OFFICIAL_RUN_TIME)) {
                startDate = jsonObject.getString(SiteFieldParamVo.OFFICIAL_RUN_TIME);
            }
        }
        if (StringUtil.isEmpty(startDate) && StringUtil.isNotEmpty(siteInfo.getCreateTime())) {
            startDate = DateUtil.localDateToStr(siteInfo.getCreateTime().toLocalDate());
        }
        if (StringUtil.isEmpty(startDate)) {
            log.error("计算收益分析参数存在问题,请排查问题");
            return ResponseResult.paramError("计算收益分析异常,请联系工作人员!!");
        }
        long totalDays = ChronoUnit.DAYS.between(DateUtil.strToLocalDate(startDate), DateUtil.strToLocalDate(endDate)); //投运天数

        //2.根据站点id查询站点收益测算配置
        SiteIncomeEntity siteIncome = null;
        List<SiteIncomeEntity> siteIncomeList = siteIncomeDao.findAllBySiteId(siteId);
        if (CollectionUtils.isNotEmpty(siteIncomeList)) {
            siteIncome = siteIncomeList.get(0);
        }

        //3.获取站点收益结构数据
        SiteYieldStructureDto siteYieldStructure = getSiteYieldStructureSum(siteId, startDate, endDate, siteIncome);

        //4.计算收益数据
        //4.1.累计收益(元)
        result.setTotalIncome(siteYieldStructure.getTotalIncome());
        if (totalDays != 0 && siteIncome != null) {
            //4.2.日均收益(元/日)
            result.setDailyIncome(siteYieldStructure.getTotalIncome().divide(BigDecimal.valueOf((double) totalDays), 2, RoundingMode.HALF_UP));

            //4.3.月均收益(元/月)
            result.setMonthlyIncome(siteYieldStructure.getTotalIncome().divide(BigDecimal.valueOf((double) totalDays / 30), 2, RoundingMode.HALF_UP));

            //4.4.年均收益(元/年)
            result.setAnnualIncome(siteYieldStructure.getTotalIncome().divide(BigDecimal.valueOf((double) totalDays / 365), 2, RoundingMode.HALF_UP));

            //4.5.计算投资总成本
            BigDecimal deviceCost = StringUtil.isEmpty(siteIncome.getDeviceCost()) ? BigDecimal.ZERO : siteIncome.getDeviceCost(); //设备总成本
            BigDecimal constructionCost = StringUtil.isEmpty(siteIncome.getConstructionCost()) ? BigDecimal.ZERO : siteIncome.getConstructionCost(); //施工总费用
            BigDecimal constructionSubsidy = StringUtil.isEmpty(siteIncome.getConstructionSubsidy()) ? BigDecimal.ZERO : siteIncome.getConstructionSubsidy(); //建设补贴
            BigDecimal investCost = deviceCost.add(constructionCost).subtract(constructionSubsidy); //投资总成本

            if (investCost.compareTo(BigDecimal.ZERO) != 0) {
                //4.6.年化收益率(%)
                result.setAnnualYield(DoubleUtil.getToDouble(result.getAnnualIncome().divide(investCost, 4, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100)).doubleValue()));

                //4.7.投资回收周期(年)
                if (result.getTotalIncome().compareTo(BigDecimal.ZERO) != 0) {
                    result.setRecoveryPeriod(DoubleUtil.getToDouble(investCost.divide(result.getAnnualIncome(), 4, RoundingMode.HALF_UP).doubleValue()));
                }
            }
        }
        return ResponseResult.ok(result);
    }

    @Override
    public ResponseResult<SiteOperateIncomeDto> countSiteOperateIncome(String siteId, String startDate, String endDate, Integer dateType) {
        //返回的对象
        SiteOperateIncomeDto result = new SiteOperateIncomeDto();

        //1.获取日期列表
        List<String> dateList = DateUtil.getDateBetween(dateType, startDate, endDate);

        //2.获取开始日期(投运日期或建站创建日期)和结束日期(当天),投运天数
        String historyStartDate = null;
        String historyEndDate = DateUtil.localDateToStr(DateUtil.strToLocalDate(startDate).minusDays(1));
        SiteInfoDto siteInfo = deviceService.findSiteBasicInfoByIds(Collections.singletonList(siteId)).getData().get(siteId);
        if (siteInfo == null) {
            return ResponseResult.paramError("该站点不存在,不允许计算收益分析");
        }
        if (StringUtil.isNotEmpty(siteInfo.getSiteReadwriteObject())) {
            JSONObject jsonObject = JSON.parseObject(siteInfo.getSiteReadwriteObject());
            if (jsonObject.containsKey(SiteFieldParamVo.OFFICIAL_RUN_TIME)) {
                historyStartDate = jsonObject.getString(SiteFieldParamVo.OFFICIAL_RUN_TIME);
            }
        }
        if (StringUtil.isEmpty(historyStartDate) && StringUtil.isNotEmpty(siteInfo.getCreateTime())) {
            historyStartDate = DateUtil.localDateToStr(siteInfo.getCreateTime().toLocalDate());
        }
        if (StringUtil.isEmpty(historyStartDate)) {
            return ResponseResult.paramError("计算收益分析异常,请联系工作人员!!");
        }

        //3.根据站点id查询站点收益测算配置
        SiteIncomeEntity siteIncome = null;
        List<SiteIncomeEntity> siteIncomeList = siteIncomeDao.findAllBySiteId(siteId);
        if (CollectionUtils.isNotEmpty(siteIncomeList)) {
            siteIncome = siteIncomeList.get(0);
        }

        //4.获取历史总收益
        BigDecimal historyTotalIncome = BigDecimal.ZERO;
        SiteYieldStructureDto historyYieldStructure = this.getSiteYieldStructureSum(siteId, historyStartDate, historyEndDate, siteIncome);
        if (StringUtil.isNotEmpty(historyYieldStructure.getTotalIncome())) {
            historyTotalIncome = historyYieldStructure.getTotalIncome();
        }

        //5.获取时间范围内的电量统计和收益数据
        Map<String, SiteYieldStructureDto> siteYieldStructureMap = this.getSiteYieldStructureDate(siteId, startDate, endDate, historyStartDate, siteIncome);

        //6.计算投资总成本
        BigDecimal investCost = BigDecimal.ZERO;
        if (siteIncome != null) {
            BigDecimal deviceCost = StringUtil.isEmpty(siteIncome.getDeviceCost()) ? BigDecimal.ZERO : siteIncome.getDeviceCost(); //设备总成本
            BigDecimal constructionCost = StringUtil.isEmpty(siteIncome.getConstructionCost()) ? BigDecimal.ZERO : siteIncome.getConstructionCost(); //施工总费用
            BigDecimal constructionSubsidy = StringUtil.isEmpty(siteIncome.getConstructionSubsidy()) ? BigDecimal.ZERO : siteIncome.getConstructionSubsidy(); //建设补贴
            investCost = deviceCost.add(constructionCost).subtract(constructionSubsidy); //投资总成本
        }

        //7.对数据进行组装
        for (String date : dateList) {
            result.getDateList().add(date);
            List<SiteYieldStructureDto> yieldStructureList = Lists.newArrayList();
            if (dateType == 1) { //日
                if (siteYieldStructureMap.containsKey(date)) {
                    yieldStructureList.add(siteYieldStructureMap.get(date));
                }
            }
            if (dateType == 2) { //月
                yieldStructureList.addAll(siteYieldStructureMap.entrySet().stream().filter(s -> s.getKey().contains(date))
                        .map(Map.Entry::getValue).collect(Collectors.toList()));
            }

            //获取日/月收益,累计收益,累计收益率
            BigDecimal income = DoubleUtil.getToBigDecimal(yieldStructureList.stream().map(SiteYieldStructureDto::getTotalIncome).filter(StringUtil::isNotEmpty)
                    .reduce(BigDecimal.ZERO, BigDecimal::add));
            result.getIncomeList().add(income); //日/月收益
            //获取累计收益
            historyTotalIncome = historyTotalIncome.add(income);
            result.getTotalIncomeList().add(historyTotalIncome);
            //获取累计收益率
            if (investCost.compareTo(BigDecimal.ZERO) != 0) {
                result.getTotalIncomeRateList().add(DoubleUtil.getToBigDecimal(historyTotalIncome.divide(investCost, 4, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100))).doubleValue());
            } else {
                result.getTotalIncomeRateList().add(0.0);
            }
            //获取正向电量
            result.setPositiveQt(result.getPositiveQt() + yieldStructureList.stream().filter(s -> StringUtil.isNotEmpty(s.getPositiveQt()))
                    .mapToDouble(SiteYieldStructureDto::getPositiveQt).sum());
            //获取反向电量
            result.setNegativeQt(result.getNegativeQt() + yieldStructureList.stream().filter(s -> StringUtil.isNotEmpty(s.getNegativeQt()))
                    .mapToDouble(SiteYieldStructureDto::getNegativeQt).sum());
            //获取充电桩充电电量
            result.setPileChargeQt(result.getPileChargeQt() + yieldStructureList.stream().filter(s -> StringUtil.isNotEmpty(s.getPileChargeQt()))
                    .mapToDouble(SiteYieldStructureDto::getPileChargeQt).sum());
            //获取充电桩放电电量
            result.setPileDischargeQt(result.getPileDischargeQt() + yieldStructureList.stream().filter(s -> StringUtil.isNotEmpty(s.getPileDischargeQt()))
                    .mapToDouble(SiteYieldStructureDto::getPileDischargeQt).sum());
            //获取损耗电量
            result.setLossQt(result.getLossQt() + yieldStructureList.stream().filter(s -> StringUtil.isNotEmpty(s.getLossQt()))
                    .mapToDouble(SiteYieldStructureDto::getLossQt).sum());
            //获取充电收入
            result.setChargeCost(result.getChargeCost().add(yieldStructureList.stream().map(SiteYieldStructureDto::getChargeCost).filter(StringUtil::isNotEmpty)
                    .reduce(BigDecimal.ZERO, BigDecimal::add)));
            //获取充电购电成本
            result.setChargePurchaseCost(result.getChargePurchaseCost().add(yieldStructureList.stream().map(SiteYieldStructureDto::getChargePurchaseCost)
                    .filter(StringUtil::isNotEmpty).reduce(BigDecimal.ZERO, BigDecimal::add)));
            //获取V2G售电收入
            result.setDischargeSaleCost(result.getDischargeSaleCost().add(yieldStructureList.stream().map(SiteYieldStructureDto::getDischargeSaleCost)
                    .filter(StringUtil::isNotEmpty).reduce(BigDecimal.ZERO, BigDecimal::add)));
            //获取V2G购电成本
            result.setDischargePurchaseCost(result.getDischargePurchaseCost().add(yieldStructureList.stream().map(SiteYieldStructureDto::getDischargePurchaseCost)
                    .filter(StringUtil::isNotEmpty).reduce(BigDecimal.ZERO, BigDecimal::add)));
            //获取充电收益
            result.setChargeIncome(result.getChargeIncome().add(yieldStructureList.stream().map(SiteYieldStructureDto::getChargeIncome)
                    .filter(StringUtil::isNotEmpty).reduce(BigDecimal.ZERO, BigDecimal::add)));
            //获取放电收益
            result.setDischargeIncome(result.getDischargeIncome().add(yieldStructureList.stream().map(SiteYieldStructureDto::getDischargeIncome)
                    .filter(StringUtil::isNotEmpty).reduce(BigDecimal.ZERO, BigDecimal::add)));
            //获取运营补贴
            result.setOperateSubsidy(result.getOperateSubsidy().add(yieldStructureList.stream().map(SiteYieldStructureDto::getOperateSubsidy)
                    .filter(StringUtil::isNotEmpty).reduce(BigDecimal.ZERO, BigDecimal::add)));
            //获取场地租金
            result.setSiteRent(result.getSiteRent().add(yieldStructureList.stream().map(SiteYieldStructureDto::getSiteRent)
                    .filter(StringUtil::isNotEmpty).reduce(BigDecimal.ZERO, BigDecimal::add)));
            //获取运营成本
            result.setOperateCost(result.getOperateCost().add(yieldStructureList.stream().map(SiteYieldStructureDto::getOperateCost)
                    .filter(StringUtil::isNotEmpty).reduce(BigDecimal.ZERO, BigDecimal::add)));
            //获取运维成本
            result.setMaintainCost(result.getMaintainCost().add(yieldStructureList.stream().map(SiteYieldStructureDto::getMaintainCost)
                    .filter(StringUtil::isNotEmpty).reduce(BigDecimal.ZERO, BigDecimal::add)));
            //获取总收益
            result.setTotalIncome(result.getTotalIncome().add(yieldStructureList.stream().map(SiteYieldStructureDto::getTotalIncome)
                    .filter(StringUtil::isNotEmpty).reduce(BigDecimal.ZERO, BigDecimal::add)));

        }
        //对数据去除小数点
        result.setPositiveQt(DoubleUtil.getToDouble(result.getPositiveQt()));
        result.setNegativeQt(DoubleUtil.getToDouble(result.getNegativeQt()));
        result.setPileChargeQt(DoubleUtil.getToDouble(result.getPileChargeQt()));
        result.setPileDischargeQt(DoubleUtil.getToDouble(result.getPileDischargeQt()));
        result.setLossQt(DoubleUtil.getToDouble(result.getLossQt()));
        result.setChargeCost(DoubleUtil.getToBigDecimal(result.getChargeCost()));
        result.setChargePurchaseCost(DoubleUtil.getToBigDecimal(result.getChargePurchaseCost()));
        result.setDischargeSaleCost(DoubleUtil.getToBigDecimal(result.getDischargeSaleCost()));
        result.setDischargePurchaseCost(DoubleUtil.getToBigDecimal(result.getDischargePurchaseCost()));
        result.setChargeIncome(DoubleUtil.getToBigDecimal(result.getChargeIncome()));
        result.setDischargeIncome(DoubleUtil.getToBigDecimal(result.getDischargeIncome()));
        result.setOperateSubsidy(DoubleUtil.getToBigDecimal(result.getOperateSubsidy()));
        result.setSiteRent(DoubleUtil.getToBigDecimal(result.getSiteRent()));
        result.setOperateCost(DoubleUtil.getToBigDecimal(result.getOperateCost()));
        result.setMaintainCost(DoubleUtil.getToBigDecimal(result.getMaintainCost()));
        result.setTotalIncome(DoubleUtil.getToBigDecimal(result.getTotalIncome()));
        return ResponseResult.ok(result);
    }

    //封装 根据站点id获取收益结构数据
    private SiteYieldStructureDto getSiteYieldStructureSum(String siteId, String startDate, String endDate, SiteIncomeEntity siteIncome) {
        //注释 讲解
        //收益结构
        //1.充电收入（元）：订单表里面的充电金额
        //2.充电购电成本（元）：电能表的正向有功电能（尖峰平谷深） × 电桩购电电价
        //3.V2G售电收入（元）：电能表的反向有功电能（尖峰平谷深）× 电桩售电电价
        //4.V2G购电成本（元）：订单表里面的放电金额
        //5.运营补贴（元）：订单表里面的充电电量 × 运营补贴（元/度） 注：需要判断充电订单补贴的时间范围
        //6.场地租金（元）：场地租金（元/月）÷ 30 × 查询日期天数 注：需要判断场地租金生效的时间范围
        //7.运营成本（元）：运营成本（元/月）÷ 30 × 查询日期天数
        //8.运维成本（元）：运维成本（元/月）÷ 30 × 查询日期天数
        //9.充电收益（元）：充电收入（元）- 充电购电成本（元）
        //10.放电收益（元）：V2G售电收入（元）- V2G购电成本（元）
        //11.总收益（元）：充电收益（元）+ 运营补贴（元） + 放电收益（元） - 场地租金（元） - 运营成本（元） -  运维成本（元）
        //12.累计收益率（%）：累计收益 ÷ （设备总成本 + 施工总费用 - 建设补贴）* 100%

        //电量统计
        //1.正向电量（kWh）：电能表的正向有功电能总值(total_positive_active_energy)
        //2.汽车充电量（kWh）：订单表里面的充电量
        //3.汽车放电量（kWh）：订单表里面的放电量
        //4.反向电量（kWh）:  电能表的反向有功电能总值(total_inverse_active_energy)
        //5.损耗电量（kWh）：（正向电量 - 汽车充电量）+ （汽车放电量 - 反向电量）

        //返回的对象
        SiteYieldStructureDto result = new SiteYieldStructureDto();
        //判断开始时间和结束时间
        if (startDate.compareTo(endDate) > 0) {
            return result;
        }

        //1.获取带时分秒的开始时间和结束时间和日期列表
        String startTime = DateUtil.getDayStart(startDate);
        String endTime = DateUtil.getDayEnd(endDate);
        List<String> dateList = DateUtil.getDateBetween(1, startDate, endDate);

        //2.统计充电收入,汽车充电量
        Map<String, ChargeOrderCostModel> chargeCostMap = Maps.newHashMap();
        List<ChargeOrderCostModel> chargeCostList = orderRecordMapper.countOrderActualMoneyBySiteId(siteId, 0, startTime, endTime, 1)
                .stream().filter(c -> StringUtil.isNotEmpty(c.getDataTime())).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(chargeCostList)) {
            chargeCostMap = chargeCostList.stream().collect(Collectors.toMap(ChargeOrderCostModel::getDataTime, c -> c,
                    (k1, k2) -> k1));
            result.setChargeCost(DoubleUtil.getToBigDecimal(chargeCostMap.values().stream().map(ChargeOrderCostModel::getTotalCost).filter(StringUtil::isNotEmpty)
                    .reduce(BigDecimal.ZERO, BigDecimal::add))); //充电收入
            result.setPileChargeQt(DoubleUtil.getToDouble(chargeCostMap.values().stream().filter(c -> StringUtil.isNotEmpty(c.getTotalQt()))
                    .mapToDouble(ChargeOrderCostModel::getTotalQt).sum())); //汽车充电量
        }
        //3.统计V2G购电成本,汽车放电量
        List<ChargeOrderCostModel> v2gCostList = orderRecordMapper.countOrderTotalMoneyBySiteId(siteId, 1, startTime, endTime, null);
        if (CollectionUtils.isNotEmpty(v2gCostList) && StringUtil.isNotEmpty(v2gCostList.get(0))) {
            ChargeOrderCostModel orderCostModel = v2gCostList.get(0);
            if (StringUtil.isNotEmpty(orderCostModel.getTotalCost())) {
                result.setDischargePurchaseCost(DoubleUtil.getToBigDecimal(orderCostModel.getTotalCost())); //V2G购电成本
            }
            if (StringUtil.isNotEmpty(orderCostModel.getTotalQt())) {
                result.setPileDischargeQt(DoubleUtil.getToDouble(orderCostModel.getTotalQt())); //汽车放电量
            }
        }
        //4.统计充电购电成本和V2G售电收入
        //4.1.根据站点id查询电桩购电电价和电站售电电价
        //日期 -> (时段类型 -> 电费)
        Map<String, Map<Integer, BigDecimal>> purchaseElectMap = Maps.newHashMap();
        Map<String, Map<Integer, BigDecimal>> saleElectMap = Maps.newHashMap();
        List<ElectConfigEntity> electConfigEntityList = electConfigDao.findElectConfigList(siteId, Arrays.asList(6, 7), startDate, endDate);
        if (CollectionUtils.isNotEmpty(electConfigEntityList)) {
            Map<Integer, List<ElectConfigEntity>> electConfigMap = electConfigEntityList.stream().collect(Collectors.groupingBy(ElectConfigEntity::getModuleType));
            //根据多个电站配置id查询尖峰平谷电价(电价配置id -> (时段类型 -> 电费))
            Set<String> electConfigIds = electConfigMap.values().stream().flatMap(e -> e.stream().map(ElectConfigEntity::getId)).collect(Collectors.toSet());
            Map<String, Map<Integer, BigDecimal>> electTimeFrameMap = electTimeFrameDao.findAllByElectConfigIdIn(electConfigIds).stream()
                    .collect(Collectors.groupingBy(ElectTimeFrameEntity::getElectConfigId, Collectors.toMap(ElectTimeFrameEntity::getPeriodType,
                            ElectTimeFrameEntity::getElectMoney, (k1, k2) -> k2)));
            //对数据进行组装
            electConfigMap.forEach((moduleType, electConfigList) -> electConfigList.forEach(electConfig -> {
                Map<Integer, BigDecimal> periodTypeMap = Maps.newHashMap();
                if (electTimeFrameMap.containsKey(electConfig.getId())) {
                    periodTypeMap = electTimeFrameMap.get(electConfig.getId());
                }
                //过滤日期范围内的日期
                List<String> electDateList = DateUtil.getDateBetween(1, electConfig.getStartDate(), electConfig.getEndDate());
                List<String> electFiletrDateList = dateList.stream().filter(electDateList::contains).collect(Collectors.toList());
                for (String date : electFiletrDateList) {
                    if (moduleType == 6) { //购电电价
                        purchaseElectMap.put(date, periodTypeMap);
                    }
                    if (moduleType == 7) { //售电电价
                        saleElectMap.put(date, periodTypeMap);
                    }
                }
            }));
        }

        //4.2.根据站点id查询电能表正向有功电能(总尖峰平谷深)和电能表反向有功电能(总尖峰平谷深)的电量
        //日期 -> (功能点标识 -> 电量)
        Map<String, Map<String, Double>> meterQtMap = Maps.newHashMap();
        List<DeviceBasicInfoDto> deviceList = deviceService.findDeviceBasicInfoBySiteIds(Collections.singletonList(siteId), 5).getData().get(siteId);
        if (CollectionUtils.isNotEmpty(deviceList)) {
            //根据多个电能表id和查询时间查询电能表正向有功电能(总尖峰平谷深)和电能表反向有功电能(总尖峰平谷深)的电量
            Set<String> deviceIds = deviceList.stream().filter(d -> StringUtil.isNotEmpty(d.getTypeId()) && Objects.equals(d.getTypeId(), "38"))
                    .map(DeviceBasicInfoDto::getId).collect(Collectors.toSet());
            DeviceHistoryQueryVo deviceQueryVo = this.getDeviceHistoryQueryVo(startTime, endTime, deviceIds);
            Map<String, Map<String, List<NodeDifHistoryDto>>> deviceHistoryMap = dataService.findNodeDifHistoryListFeign(deviceQueryVo).getData();
            if (MapUtils.isNotEmpty(deviceHistoryMap)) {
                List<NodeDifHistoryDto> deviceHistoryList = deviceHistoryMap.values().stream().flatMap(entry -> entry.values().stream()
                                .flatMap(Collection::stream)).filter(c -> StringUtil.isNotEmpty(c.getFirstDataValue()) && StringUtil.isNotEmpty(c.getLastDataValue()))
                        .collect(Collectors.toList());
                deviceHistoryList.stream().collect(Collectors.groupingBy(c -> c.getFirstDateTime().substring(0, 10))).forEach((firstDate, historyList) -> {
                    Map<String, Double> dateQtMap = historyList.stream().collect(Collectors.groupingBy(NodeDifHistoryDto::getFunctionLogo, Collectors
                            .summingDouble(c -> Double.parseDouble(String.valueOf(c.getLastDataValue())) - Double.parseDouble(String.valueOf(c.getFirstDataValue())))));
                    meterQtMap.put(firstDate, dateQtMap);
                });
            }
        }
        //4.3.统计充电购电成本,V2G售电收入,正向电量,反向电量
        if (MapUtils.isNotEmpty(meterQtMap)) {
            for (String date : dateList) {
                if (!meterQtMap.containsKey(date)) {
                    continue;
                }
                Map<String, Double> qtMap = meterQtMap.get(date);
                if (qtMap.containsKey(FunctionLogoParamVo.TOTAL_POSITIVE_ACTIVE_ENERGY)) { //正向电量
                    result.setPositiveQt(result.getPositiveQt() + qtMap.get(FunctionLogoParamVo.TOTAL_POSITIVE_ACTIVE_ENERGY));
                }
                if (qtMap.containsKey(FunctionLogoParamVo.TOTAL_INVERSE_ACTIVE_ENERGY)) { //反向电量
                    result.setNegativeQt(result.getNegativeQt() + qtMap.get(FunctionLogoParamVo.TOTAL_INVERSE_ACTIVE_ENERGY));
                }

                if (MapUtils.isNotEmpty(purchaseElectMap) && purchaseElectMap.containsKey(date)) { //充电购电成本
                    this.getPurchaseElectMap(purchaseElectMap, date, result, qtMap);
                }

                if (MapUtils.isNotEmpty(saleElectMap) && saleElectMap.containsKey(date)) { //V2G售电收入
                    this.getSaleCostMap(saleElectMap, date, result, qtMap);
                }

            }
            result.setPositiveQt(DoubleUtil.getToDouble(result.getPositiveQt())); //正向电量
            result.setNegativeQt(DoubleUtil.getToDouble(result.getNegativeQt())); //反向电量
            result.setChargePurchaseCost(DoubleUtil.getToBigDecimal(result.getChargePurchaseCost())); //充电购电成本
            result.setDischargeSaleCost(DoubleUtil.getToBigDecimal(result.getDischargeSaleCost())); //V2G售电收入
        }

        //5.统计运营补贴,场地租金,运营成本,运维成本
        if (siteIncome != null) {
            //5.1.统计站点的运营补贴(订单表里面的充电电量 × 运营补贴（元/度）)
            BigDecimal operationSubsidy = siteIncome.getOperationSubsidy();
            String subsidyStartDate = siteIncome.getSubsidyStartDate();
            String subsidyEndDate = siteIncome.getSubsidyEndDate();
            if (StringUtil.isNotEmpty(operationSubsidy) && StringUtil.isNotEmpty(subsidyStartDate) && StringUtil.isNotEmpty(subsidyEndDate)) {
                Map<String, String> subsidyMap = DateUtil.getDateBetween(1, subsidyStartDate, subsidyEndDate).stream().collect(Collectors.toMap(c -> c, c -> c));
                List<String> dateFilterList = dateList.stream().filter(subsidyMap::containsKey).collect(Collectors.toList());//运营补贴日期列表
                Map<String, ChargeOrderCostModel> finalChargeCostMap = chargeCostMap;
                BigDecimal operateSubsidy = dateFilterList.stream().map(date -> {
                    if (finalChargeCostMap.containsKey(date)) {
                        Double totalQt = finalChargeCostMap.get(date).getTotalQt();
                        if (StringUtil.isNotEmpty(totalQt)) {
                            return new BigDecimal(totalQt).multiply(operationSubsidy); //订单表里面的充电电量 × 运营补贴（元/度）
                        }
                    }
                    return BigDecimal.ZERO;
                }).reduce(BigDecimal.ZERO, BigDecimal::add);
                result.setOperateSubsidy(DoubleUtil.getToBigDecimal(operateSubsidy)); //运营补贴
            }
            //5.2.统计场地租金(场地租金（元/月）÷ 30 × 查询日期天数)
            BigDecimal siteRent = siteIncome.getSiteRent();
            String rentStartDate = siteIncome.getRentStartDate();
            String rentEndDate = siteIncome.getRentEndDate();
            if (StringUtil.isNotEmpty(siteRent) && StringUtil.isNotEmpty(rentStartDate) && StringUtil.isNotEmpty(rentEndDate)) {
                Map<String, String> rentMap = DateUtil.getDateBetween(1, rentStartDate, rentEndDate).stream().collect(Collectors.toMap(c -> c, c -> c));
                long rentDays = dateList.stream().filter(rentMap::containsKey).count(); //场地租金天数
                result.setSiteRent(DoubleUtil.getToBigDecimal(siteRent.divide(new BigDecimal(30), 4, RoundingMode.HALF_UP).multiply(new BigDecimal(rentDays))));
            }
            //5.3.统计运营成本(运营成本（元/月）÷ 30 × 查询日期天数)
            BigDecimal operationCost = siteIncome.getOperationCost();
            if (StringUtil.isNotEmpty(operationCost)) {
                result.setOperateCost(DoubleUtil.getToBigDecimal(operationCost.divide(new BigDecimal(30), 4, RoundingMode.HALF_UP).multiply(new BigDecimal(dateList.size()))));
            }
            //5.4.统计运维成本(运维成本（元/月）÷ 30 × 查询日期天数)
            BigDecimal maintainCost = siteIncome.getMaintainCost();
            if (StringUtil.isNotEmpty(maintainCost)) {
                result.setMaintainCost(DoubleUtil.getToBigDecimal(maintainCost.divide(new BigDecimal(30), 4, RoundingMode.HALF_UP).multiply(new BigDecimal(dateList.size()))));
            }
        }
        //6.统计充电收益(充电收入（元）- 充电购电成本（元）)
        result.setChargeIncome(DoubleUtil.getToBigDecimal(result.getChargeCost().subtract(result.getChargePurchaseCost())));
        //7.统计放电收益(V2G售电收入（元）- V2G购电成本（元）)
        result.setDischargeIncome(DoubleUtil.getToBigDecimal(result.getDischargeSaleCost().subtract(result.getDischargePurchaseCost())));
        //8.统计总收益(充电收益（元）+ 运营补贴（元） + 放电收益（元） - 场地租金（元） - 运营成本（元） -  运维成本（元）)
        result.setTotalIncome(DoubleUtil.getToBigDecimal(result.getChargeIncome().add(result.getOperateSubsidy()).add(result.getDischargeIncome())
                .subtract(result.getSiteRent()).subtract(result.getOperateCost()).subtract(result.getMaintainCost())));
        //9.统计损耗电量(（正向电量 - 汽车充电量）+ （汽车放电量 - 反向电量）)
        Double positiveQt = StringUtil.isEmpty(result.getPositiveQt()) ? 0 : result.getPositiveQt(); //正向电量
        double negativeQt = StringUtil.isEmpty(result.getNegativeQt()) ? 0 : result.getNegativeQt(); //反向电量
        Double pileChargeQt = StringUtil.isEmpty(result.getPileChargeQt()) ? 0 : result.getPileChargeQt(); //电桩充电电量
        double pileDischargeQt = StringUtil.isEmpty(result.getPileDischargeQt()) ? 0 : result.getPileDischargeQt(); //电桩放电电量
        result.setLossQt(DoubleUtil.getToDouble(positiveQt - pileChargeQt + pileDischargeQt - negativeQt)); //损耗电量
        return result;
    }

    //封装 根据站点id获取收益结构数据
    private Map<String, SiteYieldStructureDto> getSiteYieldStructureDate(String siteId, String startDate, String endDate, String historyStartDate, SiteIncomeEntity siteIncome) {
        //返回的对象
        Map<String, SiteYieldStructureDto> resultMap = Maps.newHashMap();
        //注释 讲解
        //收益结构
        //1.充电收入（元）：订单表里面的充电金额
        //2.充电购电成本（元）：电能表的正向有功电能（尖峰平谷深） × 电桩购电电价
        //3.V2G售电收入（元）：电能表的反向有功电能（尖峰平谷深）× 电桩售电电价
        //4.V2G购电成本（元）：订单表里面的放电金额
        //5.运营补贴（元）：订单表里面的充电电量 × 运营补贴（元/度） 注：需要判断充电订单补贴的时间范围
        //6.场地租金（元）：场地租金（元/月）÷ 30 × 查询日期天数 注：需要判断场地租金生效的时间范围
        //7.运营成本（元）：运营成本（元/月）÷ 30 × 查询日期天数
        //8.运维成本（元）：运维成本（元/月）÷ 30 × 查询日期天数
        //9.充电收益（元）：充电收入（元）- 充电购电成本（元）
        //10.放电收益（元）：V2G售电收入（元）- V2G购电成本（元）
        //11.总收益（元）：充电收益（元）+ 运营补贴（元） + 放电收益（元） - 场地租金（元） - 运营成本（元） -  运维成本（元）
        //12.累计收益率（%）：累计收益 ÷ （设备总成本 + 施工总费用 - 建设补贴）* 100%

        //电量统计
        //1.正向电量（kWh）：电能表的正向有功电能总值(total_positive_active_energy)
        //2.汽车充电量（kWh）：订单表里面的充电量
        //3.汽车放电量（kWh）：订单表里面的放电量
        //4.反向电量（kWh）:  电能表的反向有功电能总值(total_inverse_active_energy)
        //5.损耗电量（kWh）：（正向电量 - 汽车充电量）+ （汽车放电量 - 反向电量）

        //1.获取带时分秒的开始时间和结束时间和日期列表
        String startTime = DateUtil.getDayStart(startDate);
        String endTime = DateUtil.getDayEnd(endDate);
        List<String> dateList = DateUtil.getDateBetween(1, startDate, endDate);

        //2.获取每天的充电收入,汽车充电量(日期(年月日) -> 充电收入和充电电量)
        Map<String, ChargeOrderCostModel> chargeCostMap = Maps.newHashMap();
        List<ChargeOrderCostModel> chargeCostList = orderRecordMapper.countOrderActualMoneyBySiteId(siteId, 0, startTime, endTime, 1)
                .stream().filter(c -> StringUtil.isNotEmpty(c.getDataTime())).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(chargeCostList)) {
            chargeCostMap = chargeCostList.stream().collect(Collectors.toMap(ChargeOrderCostModel::getDataTime, c -> c,
                    (k1, k2) -> k1));
        }

        //3.获取V2G购电成本,汽车放电量(日期(年月日) -> V2G购电成本和汽车放电量)
        Map<String, ChargeOrderCostModel> dischargeCostMap = Maps.newHashMap();
        List<ChargeOrderCostModel> v2gCostList = orderRecordMapper.countOrderTotalMoneyBySiteId(siteId, 1, startTime, endTime, 1);
        if (CollectionUtils.isNotEmpty(v2gCostList)) {
            dischargeCostMap = v2gCostList.stream().collect(Collectors.toMap(ChargeOrderCostModel::getDataTime, c -> c,
                    (k1, k2) -> k1));
        }

        //4.获取电价配置数据和电能表电量数据
        //4.1.根据站点id查询电桩购电电价和电站售电电价
        //日期 -> (时段类型 -> 电费)
        Map<String, Map<Integer, BigDecimal>> purchaseElectMap = Maps.newHashMap();
        Map<String, Map<Integer, BigDecimal>> saleElectMap = Maps.newHashMap();
        Map<Integer, List<ElectConfigEntity>> electConfigMap = electConfigDao.findElectConfigList(siteId, Arrays.asList(6, 7), startDate, endDate).stream()
                .collect(Collectors.groupingBy(ElectConfigEntity::getModuleType));
        if (MapUtils.isNotEmpty(electConfigMap)) {
            //根据多个电站配置id查询尖峰平谷电价(电价配置id -> (时段类型 -> 电费))
            Set<String> electConfigIds = electConfigMap.values().stream().flatMap(e -> e.stream().map(ElectConfigEntity::getId)).collect(Collectors.toSet());
            Map<String, Map<Integer, BigDecimal>> electTimeFrameMap = electTimeFrameDao.findAllByElectConfigIdIn(electConfigIds).stream()
                    .collect(Collectors.groupingBy(ElectTimeFrameEntity::getElectConfigId, Collectors.toMap(ElectTimeFrameEntity::getPeriodType,
                            ElectTimeFrameEntity::getElectMoney, (k1, k2) -> k2)));
            //对数据进行组装
            electConfigMap.forEach((moduleType, electConfigList) -> electConfigList.forEach(electConfig -> {
                Map<Integer, BigDecimal> periodTypeMap = Maps.newHashMap();
                if (electTimeFrameMap.containsKey(electConfig.getId())) {
                    periodTypeMap = electTimeFrameMap.get(electConfig.getId());
                }
                //过滤日期范围内的日期
                List<String> electDateList = DateUtil.getDateBetween(1, electConfig.getStartDate(), electConfig.getEndDate());
                List<String> electFiletrDateList = dateList.stream().filter(electDateList::contains).collect(Collectors.toList());
                for (String date : electFiletrDateList) {
                    if (moduleType == 7) { //电桩购电电价
                        purchaseElectMap.put(date, periodTypeMap);
                    }
                    if (moduleType == 6) { //电桩售电电价
                        saleElectMap.put(date, periodTypeMap);
                    }
                }
            }));
        }

        //4.2.根据站点id查询电能表正向有功电能(总尖峰平谷深)和电能表反向有功电能(总尖峰平谷深)的电量
        //日期 -> (功能点标识 -> 电量)
        Map<String, Map<String, Double>> meterQtMap = Maps.newHashMap();
        List<DeviceBasicInfoDto> deviceList = deviceService.findDeviceBasicInfoBySiteIds(Collections.singletonList(siteId), 5).getData().get(siteId);
        if (CollectionUtils.isNotEmpty(deviceList)) {
            //根据多个电能表id和查询时间查询电能表正向有功电能(总尖峰平谷深)和电能表反向有功电能(总尖峰平谷深)的电量
            Set<String> deviceIds = deviceList.stream().filter(d -> StringUtil.isNotEmpty(d.getTypeId()) && Objects.equals(d.getTypeId(), "38"))
                    .map(DeviceBasicInfoDto::getId).collect(Collectors.toSet());
            DeviceHistoryQueryVo deviceQueryVo = this.getDeviceHistoryQueryVo(startTime, endTime, deviceIds);
            Map<String, Map<String, List<NodeDifHistoryDto>>> deviceHistoryMap = dataService.findNodeDifHistoryListFeign(deviceQueryVo).getData();
            if (MapUtils.isNotEmpty(deviceHistoryMap)) {
                List<NodeDifHistoryDto> deviceHistoryList = deviceHistoryMap.values().stream().flatMap(entry -> entry.values().stream()
                                .flatMap(Collection::stream)).filter(c -> StringUtil.isNotEmpty(c.getFirstDataValue()) && StringUtil.isNotEmpty(c.getLastDataValue()))
                        .collect(Collectors.toList());
                deviceHistoryList.stream().collect(Collectors.groupingBy(c -> c.getFirstDateTime().substring(0, 10))).forEach((firstDate, historyList) -> {
                    Map<String, Double> dateQtMap = historyList.stream().collect(Collectors.groupingBy(NodeDifHistoryDto::getFunctionLogo, Collectors
                            .summingDouble(c -> Double.parseDouble(String.valueOf(c.getLastDataValue())) - Double.parseDouble(String.valueOf(c.getFirstDataValue())))));
                    meterQtMap.put(firstDate, dateQtMap);
                });
            }
        }

        //5.获取运营补贴,场地租金,运营成本,运维成本相关数据(日期 -> 金额)
        Map<String, BigDecimal> subsidyCostMap = Maps.newHashMap();
        Map<String, BigDecimal> siteRentCostMap = Maps.newHashMap();
        BigDecimal operationCost = BigDecimal.ZERO;
        BigDecimal maintainCost = BigDecimal.ZERO;
        if (siteIncome != null) {
            //5.1.获取站点的运营补贴(订单表里面的充电电量 × 运营补贴（元/度）)
            BigDecimal operationSubsidy = siteIncome.getOperationSubsidy();
            String subsidyStartDate = siteIncome.getSubsidyStartDate();
            String subsidyEndDate = siteIncome.getSubsidyEndDate();
            if (StringUtil.isNotEmpty(operationSubsidy) && StringUtil.isNotEmpty(subsidyStartDate) && StringUtil.isNotEmpty(subsidyEndDate)) {
                Map<String, String> subsidyMap = DateUtil.getDateBetween(1, subsidyStartDate, subsidyEndDate).stream().collect(Collectors.toMap(c -> c, c -> c));
                List<String> dateFilterList = dateList.stream().filter(subsidyMap::containsKey).collect(Collectors.toList());//运营补贴日期列表
                Map<String, ChargeOrderCostModel> finalChargeCostMap = chargeCostMap;
                dateFilterList.forEach(date -> {
                    if (finalChargeCostMap.containsKey(date)) {
                        Double totalQt = finalChargeCostMap.get(date).getTotalQt();
                        if (StringUtil.isNotEmpty(totalQt)) {
                            subsidyCostMap.put(date, DoubleUtil.getToBigDecimal(new BigDecimal(totalQt).multiply(operationSubsidy))); //订单表里面的充电电量 × 运营补贴（元/度）
                        }
                    }
                });
            }
            //5.2.统计场地租金(场地租金（元/月）÷ 30 × 查询日期天数)
            BigDecimal siteRent = siteIncome.getSiteRent();
            String rentStartDate = siteIncome.getRentStartDate();
            String rentEndDate = siteIncome.getRentEndDate();
            if (StringUtil.isNotEmpty(siteRent) && StringUtil.isNotEmpty(rentStartDate) && StringUtil.isNotEmpty(rentEndDate)) {
                Map<String, String> rentMap = DateUtil.getDateBetween(1, rentStartDate, rentEndDate).stream().collect(Collectors.toMap(c -> c, c -> c));
                dateList.stream().filter(rentMap::containsKey).forEach(date -> siteRentCostMap.put(date, siteRent.divide(new BigDecimal(30), 4, RoundingMode.HALF_UP)));
            }
            //5.3.获取每天的运营成本
            if (StringUtil.isNotEmpty(siteIncome.getOperationCost())) {
                operationCost = DoubleUtil.getToBigDecimal(siteIncome.getOperationCost().divide(new BigDecimal(30), 4, RoundingMode.HALF_UP));
            }
            //5.4.获取每天的运维成本
            if (StringUtil.isNotEmpty(siteIncome.getMaintainCost())) {
                maintainCost = DoubleUtil.getToBigDecimal(siteIncome.getMaintainCost().divide(new BigDecimal(30), 4, RoundingMode.HALF_UP));
            }
        }

        //6.对数据进行组装
        for (String date : dateList) {
            SiteYieldStructureDto result = new SiteYieldStructureDto();
            //日期
            result.setDate(date);
            //获取正向电量,反向电量
            if (meterQtMap.containsKey(date)) {
                Map<String, Double> qtMap = meterQtMap.get(date);
                result.setPositiveQt(DoubleUtil.getToDouble(qtMap.get(FunctionLogoParamVo.TOTAL_POSITIVE_ACTIVE_ENERGY))); //正向电量
                result.setNegativeQt(DoubleUtil.getToDouble(qtMap.get(FunctionLogoParamVo.TOTAL_INVERSE_ACTIVE_ENERGY))); //反向电量
            }
            //获取汽车充电量,充电收入
            if (chargeCostMap.containsKey(date)) {
                ChargeOrderCostModel orderCost = chargeCostMap.get(date);
                result.setPileChargeQt(DoubleUtil.getToDouble(orderCost.getTotalQt())); //汽车充电量
                result.setChargeCost(DoubleUtil.getToBigDecimal(orderCost.getTotalCost())); //充电收入
            }
            //获取汽车放电量,V2G购电成本
            if (dischargeCostMap.containsKey(date)) {
                ChargeOrderCostModel orderCost = dischargeCostMap.get(date);
                result.setPileDischargeQt(DoubleUtil.getToDouble(orderCost.getTotalQt())); //汽车放电量
                result.setDischargePurchaseCost(DoubleUtil.getToBigDecimal(orderCost.getTotalCost())); //V2G购电成本
            }

            //获取损耗电量(（正向电量 - 汽车充电量）+ （汽车放电量 - 反向电量）)
            result.setLossQt(DoubleUtil.getToDouble(result.getPositiveQt() - result.getPileChargeQt() + result.getPileDischargeQt() - result.getNegativeQt())); //损耗电量

            //获取充电购电成本,V2G售电收入
            if (meterQtMap.containsKey(date)) {
                Map<String, Double> qtMap = meterQtMap.get(date);
                if (MapUtils.isNotEmpty(purchaseElectMap) && purchaseElectMap.containsKey(date)) { //充电购电成本
                    //获取充电购电成本数据(封装)
                    this.getPurchaseElectMap(purchaseElectMap, date, result, qtMap);
                    result.setChargePurchaseCost(DoubleUtil.getBigDecimal(result.getChargePurchaseCost()));
                }
                if (MapUtils.isNotEmpty(saleElectMap) && saleElectMap.containsKey(date)) { //V2G售电收入
                    //获取售电售电收入数据(封装)
                    this.getSaleCostMap(saleElectMap, date, result, qtMap);
                    result.setDischargeSaleCost(DoubleUtil.getToBigDecimal(result.getDischargeSaleCost()));
                }
            }
            //获取充电收益,放电收益
            result.setChargeIncome(DoubleUtil.getToBigDecimal(result.getChargeCost().subtract(result.getChargePurchaseCost()))); //充电收益
            result.setDischargeIncome(DoubleUtil.getToBigDecimal(result.getDischargeSaleCost().subtract(result.getDischargePurchaseCost()))); //放电收益
            //获取运营补贴
            if (subsidyCostMap.containsKey(date)) {
                result.setOperateSubsidy(subsidyCostMap.get(date)); //运营补贴
            }
            //获取场地租金
            if (siteRentCostMap.containsKey(date)) {
                result.setSiteRent(siteRentCostMap.get(date)); //场地租金
            }
            //大于历史开始时间才计算运营成本和运维成本
            if (date.compareTo(historyStartDate) >= 0) {
                result.setOperateCost(operationCost); //运营成本
                result.setMaintainCost(maintainCost); //运维成本
            }
            //获取总收益(充电收益（元）+ 运营补贴（元） + 放电收益（元） - 场地租金（元） - 运营成本（元） -  运维成本（元）)
            result.setTotalIncome(DoubleUtil.getToBigDecimal(result.getChargeIncome().add(result.getOperateSubsidy()).add(result.getDischargeIncome())
                    .subtract(result.getSiteRent()).subtract(result.getOperateCost()).subtract(result.getMaintainCost()))); //总收益
            result.setDate(date);
            resultMap.put(date, result);
        }
        return resultMap;
    }

    private void getPurchaseElectMap(Map<String, Map<Integer, BigDecimal>> purchaseElectMap, String date, SiteYieldStructureDto result, Map<String, Double> qtMap) {
        Map<Integer, BigDecimal> periodTypeMap = purchaseElectMap.get(date);
        result.setChargePurchaseCost(getPeriodValue(qtMap, periodTypeMap, FunctionLogoParamVo.TOP_SUP_KWH, 1, result.getChargePurchaseCost()));
        result.setChargePurchaseCost(getPeriodValue(qtMap, periodTypeMap, FunctionLogoParamVo.PEAK_SUP_KWH, 2, result.getChargePurchaseCost()));
        result.setChargePurchaseCost(getPeriodValue(qtMap, periodTypeMap, FunctionLogoParamVo.PLAIN_SUP_KWH, 3, result.getChargePurchaseCost()));
        result.setChargePurchaseCost(getPeriodValue(qtMap, periodTypeMap, FunctionLogoParamVo.VALLEY_SUP_KWH, 4, result.getChargePurchaseCost()));
        result.setChargePurchaseCost(getPeriodValue(qtMap, periodTypeMap, FunctionLogoParamVo.DEEP_SUP_KWH, 5, result.getChargePurchaseCost()));
    }

    private void getSaleCostMap(Map<String, Map<Integer, BigDecimal>> saleElectMap, String date, SiteYieldStructureDto result, Map<String, Double> qtMap) {
        Map<Integer, BigDecimal> periodTypeMap = saleElectMap.get(date);
        result.setDischargeSaleCost(getPeriodValue(qtMap, periodTypeMap, FunctionLogoParamVo.TOP_REV_KWH, 1, result.getDischargeSaleCost()));
        result.setDischargeSaleCost(getPeriodValue(qtMap, periodTypeMap, FunctionLogoParamVo.PEAK_REV_KWH, 2, result.getDischargeSaleCost()));
        result.setDischargeSaleCost(getPeriodValue(qtMap, periodTypeMap, FunctionLogoParamVo.PLAIN_REV_KWH, 3, result.getDischargeSaleCost()));
        result.setDischargeSaleCost(getPeriodValue(qtMap, periodTypeMap, FunctionLogoParamVo.VALLEY_REV_KWH, 4, result.getDischargeSaleCost()));
        result.setDischargeSaleCost(getPeriodValue(qtMap, periodTypeMap, FunctionLogoParamVo.DEEP_REV_KWH, 5, result.getDischargeSaleCost()));
    }

    //封装电表查询参数
    private @NotNull DeviceHistoryQueryVo getDeviceHistoryQueryVo(String startTime, String endTime, Set<String> deviceIds) {
        List<String> functionLogos = Arrays.asList(FunctionLogoParamVo.TOTAL_POSITIVE_ACTIVE_ENERGY, FunctionLogoParamVo.TOTAL_INVERSE_ACTIVE_ENERGY,
                FunctionLogoParamVo.TOP_SUP_KWH, FunctionLogoParamVo.PEAK_SUP_KWH, FunctionLogoParamVo.PLAIN_SUP_KWH, FunctionLogoParamVo.VALLEY_SUP_KWH,
                FunctionLogoParamVo.DEEP_SUP_KWH, FunctionLogoParamVo.TOP_REV_KWH, FunctionLogoParamVo.PEAK_REV_KWH, FunctionLogoParamVo.PLAIN_REV_KWH,
                FunctionLogoParamVo.VALLEY_REV_KWH, FunctionLogoParamVo.DEEP_REV_KWH);
        DeviceHistoryQueryVo deviceQueryVo = new DeviceHistoryQueryVo();
        deviceQueryVo.setDeviceIds(deviceIds);
        deviceQueryVo.setFunctionLogos(new HashSet<>(functionLogos));
        deviceQueryVo.setStartTime(startTime);
        deviceQueryVo.setEndTime(endTime);
        deviceQueryVo.setTimeInterval("1d");
        return deviceQueryVo;
    }

    //封装电表尖峰平谷时段计算
    private BigDecimal getPeriodValue(Map<String, Double> qtMap, Map<Integer, BigDecimal> periodTypeMap, String functionLogo, Integer periodType, BigDecimal result) {
        if (qtMap.containsKey(functionLogo) && periodTypeMap.containsKey(periodType)) { //尖时正向有功电能和尖时段电价
            Double periodQt = qtMap.get(functionLogo);
            BigDecimal periodMoney = periodTypeMap.get(periodType);
            if (StringUtil.isNotEmpty(periodQt) && StringUtil.isNotEmpty(periodMoney)) {
                result = result.add(periodMoney.multiply(new BigDecimal(periodQt)));
            }
        }
        return result;
    }


}
