package com.sunmax.protocol.util;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.sunmax.common.config.redis.RedisGeneralUtil;
import com.sunmax.common.constant.CmdConstant;
import com.sunmax.common.dto.device.DeviceBasicInfoDto;
import com.sunmax.common.dto.device.DeviceGunInfoDto;
import com.sunmax.common.dto.device.PileFaultDto;
import com.sunmax.common.dto.operate.ChargerPriceRateDto;
import com.sunmax.common.dto.protocol.mqtt.inter.PileSetQrCmdDto;
import com.sunmax.common.dto.together.UserGroupInfoDto;
import com.sunmax.common.model.general.PileRealModel;
import com.sunmax.common.util.*;
import com.sunmax.common.util.oss.FileUtil;
import com.sunmax.common.vo.PlatformLogoVo;
import com.sunmax.common.vo.protocol.mqtt.web.response.EventRateReqPublicVo;
import com.sunmax.common.vo.webapp.AppletChargeRefundVo;
import com.sunmax.common.vo.webapp.WalletBalanceVo;
import com.sunmax.protocol.config.emqx.inter.InterMqttConfig;
import com.sunmax.protocol.dao.*;
import com.sunmax.protocol.entity.*;
import com.sunmax.protocol.service.feign.DeviceService;
import com.sunmax.protocol.service.feign.TogetherService;
import com.sunmax.protocol.service.feign.WebAppService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Configuration
@Slf4j
public class PileRecordUtil {

    private static OrderRecordDao orderRecordDao;

    private static SettlementRecordDao settlementRecordDao;

    private static WebAppService webAppService;

    private static TogetherService togetherService;

    private static RepairOrderRecordDao repairOrderRecordDao;

    private static DeviceService deviceService;

    private static MqttRecordDao mqttRecordDao;

    @PostConstruct
    public void init() {
        PileRecordUtil.orderRecordDao = SpringBeanUtil.getBean(OrderRecordDao.class);
        PileRecordUtil.settlementRecordDao = SpringBeanUtil.getBean(SettlementRecordDao.class);
        PileRecordUtil.webAppService = SpringBeanUtil.getBean(WebAppService.class);
        PileRecordUtil.togetherService = SpringBeanUtil.getBean(TogetherService.class);
        PileRecordUtil.repairOrderRecordDao = SpringBeanUtil.getBean(RepairOrderRecordDao.class);
        PileRecordUtil.deviceService = SpringBeanUtil.getBean(DeviceService.class);
        PileRecordUtil.mqttRecordDao = SpringBeanUtil.getBean(MqttRecordDao.class);
    }

    public static void getPileChargeElect(OrderRecordEntity orderRecord, int startTime, List<Double> timeFrameQts) {
        if (StringUtil.isEmpty(startTime) || CollectionUtils.isEmpty(timeFrameQts)) {
            return;
        }
        //根据电桩编号查询电桩计费相关数据
        EventRateReqPublicVo eventRateVo = togetherService.findSiteRateInfoByPileCodes(Collections.singletonList(orderRecord.getPileCode())).getData().get(orderRecord.getPileCode());
        List<EventRateReqPublicVo.TimeFrameRate> timeFrameRateList = Lists.newArrayList();
        if (orderRecord.getRunMode() == 0 && eventRateVo != null) { //充电订单
            timeFrameRateList = eventRateVo.getCTimeFrameRate();
        }
        if ((orderRecord.getRunMode() == 1 || orderRecord.getRunMode() == 2) && eventRateVo != null) { //放电订单
            timeFrameRateList = eventRateVo.getDTimeFrameRate();
        }
        if (CollectionUtils.isEmpty(timeFrameRateList)) {
            return;
        }

        //初始化尖峰平谷电量和金额
        initElectFee(orderRecord);

        //获取充电起始时间段
        int index = SunMaxUtil.getTimeFrameStart(startTime);
        for (int i = 0; i < timeFrameQts.size(); i++) {
            if (index + i == 48) {
                index = 0;
            }
            Double qt = timeFrameQts.get(i);
            EventRateReqPublicVo.TimeFrameRate timeFrameRate = timeFrameRateList.get(index + i);
            if (timeFrameRate != null) {
                BigDecimal elect = new BigDecimal("0.0");
                if (StringUtil.isNotEmpty(timeFrameRate.getPrice())) {
                    elect = new BigDecimal(qt).multiply(BigDecimal.valueOf(timeFrameRate.getPrice().doubleValue() / 1000));
                }
                BigDecimal fee = new BigDecimal("0.0");
                if (StringUtil.isNotEmpty(timeFrameRate.getServiceCharger())) {
                    fee = new BigDecimal(qt).multiply(BigDecimal.valueOf(timeFrameRate.getServiceCharger().doubleValue() / 1000));
                }
                switch (timeFrameRate.getType()) {
                    case 1: //尖时
                        orderRecord.setJQt(orderRecord.getJQt() + qt);
                        orderRecord.setJElect(orderRecord.getJElect().add(elect));
                        orderRecord.setJFee(orderRecord.getJFee().add(fee));
                        break;
                    case 2: //峰时
                        orderRecord.setFQt(orderRecord.getFQt() + qt);
                        orderRecord.setFElect(orderRecord.getFElect().add(elect));
                        orderRecord.setFFee(orderRecord.getFFee().add(fee));
                        break;
                    case 3: //平时
                        orderRecord.setPQt(orderRecord.getPQt() + qt);
                        orderRecord.setPElect(orderRecord.getPElect().add(elect));
                        orderRecord.setPFee(orderRecord.getPFee().add(fee));
                        break;
                    case 4: //谷时
                        orderRecord.setGQt(orderRecord.getGQt() + qt);
                        orderRecord.setGElect(orderRecord.getGElect().add(elect));
                        orderRecord.setGFee(orderRecord.getGFee().add(fee));
                        break;
                }
            }
        }
        orderRecord.setTotalElect(DoubleUtil.getAbsBigDecimal(orderRecord.getJElect().add(orderRecord.getFElect()).add(orderRecord.getPElect()).add(orderRecord.getGElect()), 3));
        orderRecord.setTotalFee(DoubleUtil.getAbsBigDecimal(orderRecord.getJFee().add(orderRecord.getFFee()).add(orderRecord.getPFee()).add(orderRecord.getGFee()), 3));
        orderRecord.setJQt(DoubleUtil.getAbsDouble(orderRecord.getJQt(), 3));
        orderRecord.setJElect(DoubleUtil.getAbsBigDecimal(orderRecord.getJElect(), 3));
        orderRecord.setJFee(DoubleUtil.getAbsBigDecimal(orderRecord.getJFee(), 3));
        orderRecord.setFQt(DoubleUtil.getAbsDouble(orderRecord.getFQt(), 3));
        orderRecord.setFElect(DoubleUtil.getAbsBigDecimal(orderRecord.getFElect(), 3));
        orderRecord.setFFee(DoubleUtil.getAbsBigDecimal(orderRecord.getFFee(), 3));
        orderRecord.setPQt(DoubleUtil.getAbsDouble(orderRecord.getPQt(), 3));
        orderRecord.setPElect(DoubleUtil.getAbsBigDecimal(orderRecord.getPElect(), 3));
        orderRecord.setPFee(DoubleUtil.getAbsBigDecimal(orderRecord.getPFee(), 3));
        orderRecord.setGQt(DoubleUtil.getAbsDouble(orderRecord.getGQt(), 3));
        orderRecord.setGElect(DoubleUtil.getAbsBigDecimal(orderRecord.getGElect(), 3));
        orderRecord.setGFee(DoubleUtil.getAbsBigDecimal(orderRecord.getGFee(), 3));
    }

    public static TimeFrameQEntity getTimeFrameQtData(String timeFrameQtId, String orderNum, Integer startTime, List<Double> timeFrameQts) {
        if (StringUtil.isNotEmpty(startTime)) {
            TimeFrameQEntity timeFrameQEntity = new TimeFrameQEntity();
            String key = "timeFrame";
            //获取充电起始时间段
            int index = SunMaxUtil.getTimeFrameStart(startTime);
            timeFrameQEntity.setId(timeFrameQtId);
            timeFrameQEntity.setOrderNum(orderNum);
            for (int i = 0; i < timeFrameQts.size(); i++) {
                if (index + i == 48) {
                    index = 0;
                }
                String fieldName = key + (index + i + 1);
                Field field; //利用反射实体类设值
                try {
                    field = timeFrameQEntity.getClass().getDeclaredField(fieldName);//获取私有变量
                    field.setAccessible(true);//强行授权访问私有变量
                    field.set(timeFrameQEntity, timeFrameQts.get(i));
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    log.error("外网MQTT更新充放电记录时段失败", e);
                }
            }
            return timeFrameQEntity;
        }
        return null;
    }

    public static void initElectFee(OrderRecordEntity orderRecord) {
        orderRecord.setTotalElect(new BigDecimal("0.0"));
        orderRecord.setTotalFee(new BigDecimal("0.0"));
        orderRecord.setJQt(0.0);
        orderRecord.setJElect(new BigDecimal("0.0"));
        orderRecord.setJFee(new BigDecimal("0.0"));
        orderRecord.setFQt(0.0);
        orderRecord.setFElect(new BigDecimal("0.0"));
        orderRecord.setFFee(new BigDecimal("0.0"));
        orderRecord.setPQt(0.0);
        orderRecord.setPElect(new BigDecimal("0.0"));
        orderRecord.setPFee(new BigDecimal("0.0"));
        orderRecord.setGQt(0.0);
        orderRecord.setGElect(new BigDecimal("0.0"));
        orderRecord.setGFee(new BigDecimal("0.0"));
    }

    public static Map<String, Double> getTimeFrame(TimeFrameQEntity timeframe) {
        //返回的数据
        Map<String, Double> resultMap = new HashMap<>();
        //以结束时间点为key 结束时间点 -> 电量值
        resultMap.put("00:00:00", StringUtil.isNotEmpty(timeframe.getTimeFrame1()) ? timeframe.getTimeFrame1() : 0.0);
        resultMap.put("00:30:00", StringUtil.isNotEmpty(timeframe.getTimeFrame2()) ? timeframe.getTimeFrame2() : 0.0);
        resultMap.put("01:00:00", StringUtil.isNotEmpty(timeframe.getTimeFrame3()) ? timeframe.getTimeFrame3() : 0.0);
        resultMap.put("01:30:00", StringUtil.isNotEmpty(timeframe.getTimeFrame4()) ? timeframe.getTimeFrame4() : 0.0);
        resultMap.put("02:00:00", StringUtil.isNotEmpty(timeframe.getTimeFrame5()) ? timeframe.getTimeFrame5() : 0.0);
        resultMap.put("02:30:00", StringUtil.isNotEmpty(timeframe.getTimeFrame6()) ? timeframe.getTimeFrame6() : 0.0);
        resultMap.put("03:00:00", StringUtil.isNotEmpty(timeframe.getTimeFrame7()) ? timeframe.getTimeFrame7() : 0.0);
        resultMap.put("03:30:00", StringUtil.isNotEmpty(timeframe.getTimeFrame8()) ? timeframe.getTimeFrame8() : 0.0);
        resultMap.put("04:00:00", StringUtil.isNotEmpty(timeframe.getTimeFrame9()) ? timeframe.getTimeFrame9() : 0.0);
        resultMap.put("04:30:00", StringUtil.isNotEmpty(timeframe.getTimeFrame10()) ? timeframe.getTimeFrame10() : 0.0);
        resultMap.put("05:00:00", StringUtil.isNotEmpty(timeframe.getTimeFrame11()) ? timeframe.getTimeFrame11() : 0.0);
        resultMap.put("05:30:00", StringUtil.isNotEmpty(timeframe.getTimeFrame12()) ? timeframe.getTimeFrame12() : 0.0);
        resultMap.put("06:00:00", StringUtil.isNotEmpty(timeframe.getTimeFrame13()) ? timeframe.getTimeFrame13() : 0.0);
        resultMap.put("06:30:00", StringUtil.isNotEmpty(timeframe.getTimeFrame14()) ? timeframe.getTimeFrame14() : 0.0);
        resultMap.put("07:00:00", StringUtil.isNotEmpty(timeframe.getTimeFrame15()) ? timeframe.getTimeFrame15() : 0.0);
        resultMap.put("07:30:00", StringUtil.isNotEmpty(timeframe.getTimeFrame16()) ? timeframe.getTimeFrame16() : 0.0);
        resultMap.put("08:00:00", StringUtil.isNotEmpty(timeframe.getTimeFrame17()) ? timeframe.getTimeFrame17() : 0.0);
        resultMap.put("08:30:00", StringUtil.isNotEmpty(timeframe.getTimeFrame18()) ? timeframe.getTimeFrame18() : 0.0);
        resultMap.put("09:00:00", StringUtil.isNotEmpty(timeframe.getTimeFrame19()) ? timeframe.getTimeFrame19() : 0.0);
        resultMap.put("09:30:00", StringUtil.isNotEmpty(timeframe.getTimeFrame20()) ? timeframe.getTimeFrame20() : 0.0);
        resultMap.put("10:00:00", StringUtil.isNotEmpty(timeframe.getTimeFrame21()) ? timeframe.getTimeFrame21() : 0.0);
        resultMap.put("10:30:00", StringUtil.isNotEmpty(timeframe.getTimeFrame22()) ? timeframe.getTimeFrame22() : 0.0);
        resultMap.put("11:00:00", StringUtil.isNotEmpty(timeframe.getTimeFrame23()) ? timeframe.getTimeFrame23() : 0.0);
        resultMap.put("11:30:00", StringUtil.isNotEmpty(timeframe.getTimeFrame24()) ? timeframe.getTimeFrame24() : 0.0);
        resultMap.put("12:00:00", StringUtil.isNotEmpty(timeframe.getTimeFrame25()) ? timeframe.getTimeFrame25() : 0.0);
        resultMap.put("12:30:00", StringUtil.isNotEmpty(timeframe.getTimeFrame26()) ? timeframe.getTimeFrame26() : 0.0);
        resultMap.put("13:00:00", StringUtil.isNotEmpty(timeframe.getTimeFrame27()) ? timeframe.getTimeFrame27() : 0.0);
        resultMap.put("13:30:00", StringUtil.isNotEmpty(timeframe.getTimeFrame28()) ? timeframe.getTimeFrame28() : 0.0);
        resultMap.put("14:00:00", StringUtil.isNotEmpty(timeframe.getTimeFrame29()) ? timeframe.getTimeFrame29() : 0.0);
        resultMap.put("14:30:00", StringUtil.isNotEmpty(timeframe.getTimeFrame30()) ? timeframe.getTimeFrame30() : 0.0);
        resultMap.put("15:00:00", StringUtil.isNotEmpty(timeframe.getTimeFrame31()) ? timeframe.getTimeFrame31() : 0.0);
        resultMap.put("15:30:00", StringUtil.isNotEmpty(timeframe.getTimeFrame32()) ? timeframe.getTimeFrame32() : 0.0);
        resultMap.put("16:00:00", StringUtil.isNotEmpty(timeframe.getTimeFrame33()) ? timeframe.getTimeFrame33() : 0.0);
        resultMap.put("16:30:00", StringUtil.isNotEmpty(timeframe.getTimeFrame34()) ? timeframe.getTimeFrame34() : 0.0);
        resultMap.put("17:00:00", StringUtil.isNotEmpty(timeframe.getTimeFrame35()) ? timeframe.getTimeFrame35() : 0.0);
        resultMap.put("17:30:00", StringUtil.isNotEmpty(timeframe.getTimeFrame36()) ? timeframe.getTimeFrame36() : 0.0);
        resultMap.put("18:00:00", StringUtil.isNotEmpty(timeframe.getTimeFrame37()) ? timeframe.getTimeFrame37() : 0.0);
        resultMap.put("18:30:00", StringUtil.isNotEmpty(timeframe.getTimeFrame38()) ? timeframe.getTimeFrame38() : 0.0);
        resultMap.put("19:00:00", StringUtil.isNotEmpty(timeframe.getTimeFrame39()) ? timeframe.getTimeFrame39() : 0.0);
        resultMap.put("19:30:00", StringUtil.isNotEmpty(timeframe.getTimeFrame40()) ? timeframe.getTimeFrame40() : 0.0);
        resultMap.put("20:00:00", StringUtil.isNotEmpty(timeframe.getTimeFrame41()) ? timeframe.getTimeFrame41() : 0.0);
        resultMap.put("20:30:00", StringUtil.isNotEmpty(timeframe.getTimeFrame42()) ? timeframe.getTimeFrame42() : 0.0);
        resultMap.put("21:00:00", StringUtil.isNotEmpty(timeframe.getTimeFrame43()) ? timeframe.getTimeFrame43() : 0.0);
        resultMap.put("21:30:00", StringUtil.isNotEmpty(timeframe.getTimeFrame44()) ? timeframe.getTimeFrame44() : 0.0);
        resultMap.put("22:00:00", StringUtil.isNotEmpty(timeframe.getTimeFrame45()) ? timeframe.getTimeFrame45() : 0.0);
        resultMap.put("22:30:00", StringUtil.isNotEmpty(timeframe.getTimeFrame46()) ? timeframe.getTimeFrame46() : 0.0);
        resultMap.put("23:00:00", StringUtil.isNotEmpty(timeframe.getTimeFrame47()) ? timeframe.getTimeFrame47() : 0.0);
        resultMap.put("23:30:00", StringUtil.isNotEmpty(timeframe.getTimeFrame48()) ? timeframe.getTimeFrame48() : 0.0);
        return resultMap;
    }

//    public static List<ChargeTariffRecordEntity> getChargeTariffRecordList(OrderRecordEntity orderRecord, TimeFrameQEntity timeFrame) {
//        //定义返回数据列表
//        List<ChargeTariffRecordEntity> resultList = Lists.newArrayList();
//        Map<String, List<ChargerPriceRateDto>> chargerRateMap = operateService.findChargerRateListByPileCodes(Collections.singletonList(orderRecord.getPileCode()),
//                orderRecord.getRunMode() + 1).getData();
//        if (chargerRateMap.containsKey(orderRecord.getPileCode())) {
//            //定义费用列表
//            List<CostDto> orderCostList = Lists.newArrayList();
//            //获取电桩的计费信息
//            List<ChargerPriceRateDto> chargerPriceRateList = chargerRateMap.get(orderRecord.getPileCode());
//            //获取时段电量数据
//            Map<String, Double> timeFrameQtMap = PileRecordUtil.getTimeFrame(timeFrame);
//            //获取日期列表
//            List<String> dateList = getMinuteTime30();
//            //定义充电费用和服务费用map
//            Map<String, BigDecimal> chargeCostMap = Maps.newHashMap();
//            Map<String, BigDecimal> serviceCostMap = Maps.newHashMap();
//            for (String date : dateList) {
//                AtomicReference<BigDecimal> chargeCost = new AtomicReference<>(new BigDecimal("0.0"));
//                AtomicReference<BigDecimal> serviceCost = new AtomicReference<>(new BigDecimal("0.0"));
//                chargerPriceRateList.forEach(priceRate -> {
//                    String startTime = priceRate.getStartTime();
//                    String endTime = Objects.equals(priceRate.getEndTime(), "00:00") ? "23:59" : priceRate.getEndTime();
//                    BigDecimal electMoney = priceRate.getElectMoney();
//                    BigDecimal serviceMoney = priceRate.getServiceMoney();
//                    if (date.compareTo(startTime) >= 0 && date.compareTo(endTime) < 0) {
//                        if (StringUtil.isNotEmpty(electMoney)) {
//                            chargeCost.set(electMoney);
//                        }
//                        if (StringUtil.isNotEmpty(serviceMoney)) {
//                            serviceCost.set(serviceMoney);
//                        }
//                    }
//                });
//                chargeCostMap.put(date, chargeCost.get());
//                serviceCostMap.put(date, serviceCost.get());
//            }
//            List<CostDto> costList = Lists.newArrayList();
//            if (StringUtil.isNotEmpty(orderRecord.getStartTime()) && StringUtil.isNotEmpty(orderRecord.getEndTime())) {
//                //获取整点开始时间和整点结束时间
//                LocalDateTime startPointTime = strToLocalDateTime(getLastIntervalTime(strToLocalDateTime(orderRecord.getStartTime()), 30));
//                LocalDateTime endPointTime = strToLocalDateTime(getNextIntervalTime(strToLocalDateTime(orderRecord.getEndTime()), 30));
//                //定义上一时段的总金额
//                BigDecimal totalCost = new BigDecimal("0.0");
//                //定义上一时段下标
//                int index = 1;
//                for (LocalDateTime dateTime = startPointTime; dateTime.isBefore(endPointTime); dateTime = dateTime.plusMinutes(30)) {
//                    CostDto orderCost = new CostDto();
//                    String time = localTimeToStr(dateTime.toLocalTime());
//                    orderCost.setStartTime(time);
//                    orderCost.setEndTime(localTimeToStr(dateTime.plusMinutes(30).toLocalTime()));
//                    //获取当前时段电量
//                    Double timeFrameQt = 0.0;
//                    if (timeFrameQtMap.containsKey(time)) {
//                        timeFrameQt = timeFrameQtMap.get(time);
//                        orderCost.setChargeQt(timeFrameQt);
//                    }
//                    //当前总费用
//                    BigDecimal nowCost = new BigDecimal("0.0");
//                    //当前时段的电费
//                    if (chargeCostMap.containsKey(time)) {
//                        BigDecimal chargeCost = chargeCostMap.get(time);
//                        nowCost = nowCost.add(chargeCost);
//                        orderCost.setChargePrice(chargeCost);
//                        orderCost.setChargeMoney(chargeCost.multiply(new BigDecimal(timeFrameQt)));
//                    }
//                    //当前时段的服务费
//                    if (serviceCostMap.containsKey(time)) {
//                        BigDecimal serviceCost = serviceCostMap.get(time);
//                        orderCost.setChargeFeePrice(serviceCost);
//                        nowCost = nowCost.add(serviceCost);
//                        orderCost.setChargeFeeMoney(serviceCost.multiply(new BigDecimal(timeFrameQt)));
//                    }
//                    //计算当前时段总费用
//                    orderCost.setMoney(orderCost.getChargeMoney().add(orderCost.getChargeFeeMoney()));
//                    //当前时段和上一时段不一致 则把当前的赋值到上一时段
//                    if (!nowCost.equals(totalCost)) {
//                        index = index + 1;
//                        totalCost = nowCost;
//                    }
//                    orderCost.setIndex(index);
//                    costList.add(orderCost);
//                }
//            }
//
//            if (!costList.isEmpty()) {
//                Integer minIndex = costList.stream().map(CostDto::getIndex).min(Comparator.comparing(c -> c)).orElse(0);
//                Integer maxIndex = costList.stream().map(CostDto::getIndex).max(Comparator.comparing(c -> c)).orElse(0);
//                //对数据进行合并
//                costList.stream().collect(Collectors.groupingBy(CostDto::getIndex)).forEach((key, values) -> {
//                    CostDto cost = new CostDto();
//                    //当前时段开始日期
//                    String minStartTime = values.stream().map(CostDto::getStartTime).min(Comparator.comparing(c -> c)).orElse(null);
//                    String maxEndTime = values.stream().map(CostDto::getEndTime).max(Comparator.comparing(c -> c)).orElse(null);
//                    //当前时段结束日期
//                    //当前时段总电量
//                    Double chargerQt = DoubleUtil.getAbsDouble(values.stream().mapToDouble(CostDto::getChargeQt).sum());
//                    //当前时段总金额
//                    BigDecimal totalMoney = DoubleUtil.getAbsBigDecimal(values.stream().map(CostDto::getMoney).reduce(BigDecimal.ZERO, BigDecimal::add));
//                    //当前时段充电总金额
//                    BigDecimal chargeMoney = DoubleUtil.getAbsBigDecimal(values.stream().map(CostDto::getChargeMoney).reduce(BigDecimal.ZERO, BigDecimal::add));
//                    //当前时段服务费总金额
//                    BigDecimal serviceCostMoney = DoubleUtil.getAbsBigDecimal(values.stream().map(CostDto::getChargeFeeMoney).reduce(BigDecimal.ZERO, BigDecimal::add));
//                    cost.setIndex(key);
//                    cost.setChargeQt(chargerQt);
//                    cost.setChargeMoney(chargeMoney);
//                    cost.setChargeFeeMoney(serviceCostMoney);
//                    cost.setMoney(totalMoney);
//                    if (CollectionUtils.isNotEmpty(values)) {
//                        cost.setChargePrice(values.get(0).getChargePrice());
//                        cost.setChargeFeePrice(values.get(0).getChargeFeePrice());
//                    }
//                    if (key.equals(minIndex) && key.equals(maxIndex)) { //只有一个时段
//                        cost.setStartTime(StringUtil.isNotEmpty(orderRecord.getStartTime()) ? orderRecord.getStartTime() : null);
//                        cost.setEndTime(StringUtil.isNotEmpty(orderRecord.getEndTime()) ? orderRecord.getEndTime() : null);
//                    } else if (key.equals(minIndex)) { //当前时段是最小时段
//                        cost.setStartTime(StringUtil.isNotEmpty(orderRecord.getStartTime()) ? orderRecord.getStartTime() : null);
//                        cost.setEndTime(StringUtil.isNotEmpty(maxEndTime) ? orderRecord.getEndTime().substring(0, 10) + maxEndTime : null);
//                    } else if (key.equals(maxIndex)) { //当前时段是最大时段
//                        cost.setStartTime(StringUtil.isNotEmpty(minStartTime) ? orderRecord.getStartTime().substring(0, 10) + minStartTime : null);
//                        cost.setEndTime(StringUtil.isNotEmpty(orderRecord.getEndTime()) ? orderRecord.getEndTime() : null);
//                    } else { //其它时段
//                        cost.setStartTime(StringUtil.isNotEmpty(minStartTime) ? orderRecord.getStartTime().substring(0, 10) + minStartTime : null);
//                        cost.setEndTime(StringUtil.isNotEmpty(maxEndTime) ? orderRecord.getEndTime().substring(0, 10) + maxEndTime : null);
//                    }
//                    orderCostList.add(cost);
//                });
//            }
//            //对数据进行组装
//            if (CollectionUtils.isNotEmpty(orderCostList)) {
//                resultList = orderCostList.stream().map(cost -> {
//                    ChargeTariffRecordEntity result = new ChargeTariffRecordEntity();
//                    result.setTariffType(orderRecord.getRunMode() + 1);
//                    result.setOrderNum(orderRecord.getOrderNum());
//                    chargerPriceRateList.stream().filter(priceRate -> {
//                        if (StringUtil.isEmpty(priceRate.getStartTime()) || StringUtil.isEmpty(priceRate.getEndTime())) {
//                            return false;
//                        }
//                        String startTime = priceRate.getStartTime();
//                        String endTime = Objects.equals(priceRate.getEndTime(), "00:00") ? "23:59" : priceRate.getEndTime();
//                        if (StringUtil.isNotEmpty(cost.getStartTime()) && StringUtil.isNotEmpty(cost.getEndTime())) {
//                            String costStartTime = cost.getStartTime().substring(11, 15);
//                            String costEndTime = cost.getEndTime().substring(11, 15);
//                            return costStartTime.compareTo(startTime) >= 0 && costEndTime.compareTo(endTime) <= 0;
//                        }
//                        return false;
//                    }).findFirst().ifPresent(priceRate -> result.setTariffPeriod(priceRate.getStartTime() + FileUtil.BAR + priceRate.getEndTime()));
//                    result.setElectPrice(cost.getChargePrice());
//                    result.setServicePrice(cost.getChargeFeePrice());
//                    result.setChargeStartTime(cost.getStartTime());
//                    result.setChargeEndTime(cost.getEndTime());
//                    result.setRechargeQt(DoubleUtil.getAbsDouble(cost.getChargeQt(), 3));
//                    result.setElectMoney(DoubleUtil.getAbsBigDecimal(cost.getChargeMoney(), 3));
//                    result.setServiceMoney(DoubleUtil.getAbsBigDecimal(cost.getChargeFeeMoney(), 3));
//                    return result;
//                }).collect(Collectors.toList());
//            }
//        }
//        return resultList;
//    }

    public static List<ChargeTariffRecordEntity> saveChargeTariffRecordList(OrderRecordEntity orderRecord, ChargeTariffRecordDao chargeTariffRecordDao) {
        //返回的集合
        List<ChargeTariffRecordEntity> resultList = Lists.newArrayList();

        //获取充放电电价数据
//        List<ChargerPriceRateDto> priceRateList = Lists.newArrayList();
//        if (StringUtil.isNotEmpty(orderRecord.getRateTemplateId())) {
//            priceRateList = togetherService.findChargerRateListByPriceInfoIds(Collections.singletonList(orderRecord
//                    .getRateTemplateId())).getData().get(orderRecord.getRateTemplateId());
//        }
        List<ChargerPriceRateDto> priceRateList = togetherService.findChargerRateListByPileCodes(Collections.singletonList(orderRecord.getPileCode()), orderRecord.getRunMode() + 1)
                .getData().get(orderRecord.getPileCode());
        if (CollectionUtils.isNotEmpty(priceRateList)) {
            //获取全时段相关的电费,服务费数据
            Optional<ChargerPriceRateDto> optional = priceRateList.stream().filter(c -> Objects.equals(c.getPeriodType(), 6)).findFirst();
            if (optional.isPresent()) { //全时段计算数据
                optional.ifPresent(priceRate -> resultList.add(getChargeTariffRecord(orderRecord.getOrderNum(), orderRecord.getStartTime(), orderRecord.getEndTime(),
                        6, orderRecord.getTotalQt(), Collections.singletonList(priceRate))));
            } else { //分时段计算尖峰平谷数据
                //获取尖时段相关数据
                if (StringUtil.isNotEmpty(orderRecord.getJQt()) && orderRecord.getJQt() > 0) {
                    resultList.add(getChargeTariffRecord(orderRecord.getOrderNum(), orderRecord.getStartTime(), orderRecord.getEndTime(),
                            1, orderRecord.getJQt(), priceRateList));
                }
                //获取峰时段相关数据
                if (StringUtil.isNotEmpty(orderRecord.getFQt()) && orderRecord.getFQt() > 0) {
                    resultList.add(getChargeTariffRecord(orderRecord.getOrderNum(), orderRecord.getStartTime(), orderRecord.getEndTime(),
                            2, orderRecord.getFQt(), priceRateList));
                }
                //获取平时段相关数据
                if (StringUtil.isNotEmpty(orderRecord.getPQt()) && orderRecord.getPQt() > 0) {
                    resultList.add(getChargeTariffRecord(orderRecord.getOrderNum(), orderRecord.getStartTime(), orderRecord.getEndTime(),
                            3, orderRecord.getPQt(), priceRateList));
                }
                //获取谷时段相关数据
                if (StringUtil.isNotEmpty(orderRecord.getGQt()) && orderRecord.getGQt() > 0) {
                    resultList.add(getChargeTariffRecord(orderRecord.getOrderNum(), orderRecord.getStartTime(), orderRecord.getEndTime(),
                            4, orderRecord.getGQt(), priceRateList));
                }
            }
            if (CollectionUtils.isNotEmpty(resultList)) {
                chargeTariffRecordDao.batchUpdate(resultList);
            }
        }
        //获取不到计费详情 订单就挂起
        if (CollectionUtils.isEmpty(resultList)) {
            orderRecord.setOrderStatus(4);
            //创建补单数据
            batchAddRepairOrderRecord(Collections.singletonList(orderRecord));
        }
        return resultList;
    }

    public static ChargeTariffRecordEntity getChargeTariffRecord(String orderNum, String startTime, String endTime, Integer periodType,
                                                                 Double qt, List<ChargerPriceRateDto> priceRateList) {
        ChargeTariffRecordEntity chargeTariffRecord = new ChargeTariffRecordEntity();
        chargeTariffRecord.setOrderNum(orderNum);
        chargeTariffRecord.setRechargeQt(qt);
        chargeTariffRecord.setPeriodType(periodType);
        if (CollectionUtils.isNotEmpty(priceRateList)) {
            priceRateList.stream().filter(c -> Objects.equals(c.getPeriodType(), periodType))
                    .findFirst().ifPresent(priceRate -> {
                        chargeTariffRecord.setElectPrice(priceRate.getElectMoney());
                        chargeTariffRecord.setServicePrice(priceRate.getServiceMoney());
                        chargeTariffRecord.setElectMoney(priceRate.getElectMoney().multiply(BigDecimal.valueOf(qt)));
                        chargeTariffRecord.setServiceMoney(priceRate.getServiceMoney().multiply(BigDecimal.valueOf(qt)));
                    });
            //判断该订单是否跨天 跨天计算还待调整
            LocalDateTime orderStartTime = DateUtil.strToLocalDateTime(startTime);
            LocalDateTime orderEndTime = DateUtil.strToLocalDateTime(endTime);
            boolean isDay = Objects.equals(orderStartTime.toLocalDate(), orderEndTime.toLocalDate());
//            log.info("订单编号{}是否跨天:{},订单开始时间:{},订单结束时间:{}", orderNum, isDay, orderStartTime, orderEndTime);
            chargeTariffRecord.setChargeDuration(DateUtil.secToTime(priceRateList.stream().filter(c -> Objects.equals(c.getPeriodType(), periodType))
                    .mapToLong(priceRate -> {
                        //全时段 通过订单开始时间和结束时间算时间差
                        if (periodType == 6) {
                            return DateUtil.compareDiffBetweenSecond(orderStartTime, orderEndTime);
                        }
                        //分时段算时间差
                        if (isDay) {
                            //不跨天
                            LocalDateTime priceStartTime = LocalDateTime.of(orderStartTime.toLocalDate(), DateUtil.strToLocalTime(priceRate.getStartTime() + ":00"));
                            LocalDateTime priceEndTime = LocalDateTime.of(orderEndTime.toLocalDate(), DateUtil.strToLocalTime(Objects.equals(priceRate.getEndTime(), "00:00")
                                    ? "23:59:59" : priceRate.getEndTime() + ":59"));
                            LocalDateTime beforeTime = orderStartTime.isBefore(priceStartTime) ? priceStartTime : orderStartTime;
                            LocalDateTime finishTime = orderEndTime.isBefore(priceEndTime) ? orderEndTime : priceEndTime;
                            if (beforeTime.isBefore(finishTime)) {
//                                log.info("订单号{}, 计算开始时间{}, 计算结束时间{}", orderNum, beforeTime, finishTime);
                                return DateUtil.compareDiffBetweenSecond(beforeTime, finishTime);
                            }
                        } else {
                            //跨天 分时段计算
                            long time = 0L;
                            //按起始时间计算
                            LocalDateTime startPriceStartTime = LocalDateTime.of(orderStartTime.toLocalDate(), DateUtil.strToLocalTime(priceRate.getStartTime() + ":00"));
                            LocalDateTime startPriceEndTime = LocalDateTime.of(orderStartTime.toLocalDate(), DateUtil.strToLocalTime(Objects.equals(priceRate.getEndTime(), "00:00")
                                    ? "23:59:59" : priceRate.getEndTime() + ":59"));
                            if (startPriceStartTime.isBefore(orderStartTime) && startPriceEndTime.isBefore(orderEndTime) && orderStartTime.isBefore(startPriceEndTime)) {
                                time = time + DateUtil.compareDiffBetweenSecond(orderStartTime, startPriceEndTime);
                            }
                            //按结束时间计算
                            LocalDateTime endPriceStartTime = LocalDateTime.of(orderEndTime.toLocalDate(), DateUtil.strToLocalTime(priceRate.getStartTime() + ":00"));
                            LocalDateTime endPriceEndTime = LocalDateTime.of(orderEndTime.toLocalDate(), DateUtil.strToLocalTime(Objects.equals(priceRate.getEndTime(), "00:00")
                                    ? "23:59:59" : priceRate.getEndTime() + ":59"));
                            if (orderStartTime.isBefore(endPriceStartTime)) {
                                //订单结束时间在时段结束时间之前计算
                                if (endPriceEndTime.isBefore(orderEndTime) && endPriceStartTime.isBefore(endPriceEndTime)) {
//                                    log.info("订单号{}, 计算结束时间1{}, 计算结束时间1{}", orderNum, endPriceStartTime, endPriceEndTime);
                                    time = time + DateUtil.compareDiffBetweenSecond(endPriceStartTime, endPriceEndTime);
                                }
                                //订单结束时间在时段结束时间之后计算
                                if (orderEndTime.isBefore(endPriceEndTime) && endPriceStartTime.isBefore(orderEndTime)) {
//                                    log.info("订单号{}, 计算结束时间2{}, 计算结束时间2{}", orderNum, endPriceStartTime, orderEndTime);
                                    time = time + DateUtil.compareDiffBetweenSecond(endPriceStartTime, orderEndTime);
                                }
                            }
                            return time;
                        }
                        return 0;
                    }).sum()));
        }
        return chargeTariffRecord;
    }

    // 添加告警记录
    public static AlarmRecordEntity addAlarmRecord(String pileCode, String gunCode, Integer faultCode, Long featureCode, Integer moduleAddr, Map<Integer, PileFaultDto> faultEventMap) {
        AlarmRecordEntity alarmRecord = new AlarmRecordEntity();
        alarmRecord.setDeviceCode(pileCode);
        alarmRecord.setGunCode(gunCode);
        alarmRecord.setFaultCode(faultCode);
        alarmRecord.setFeatureCode(featureCode);
        alarmRecord.setModuleAddr(moduleAddr);
        if (faultEventMap.containsKey(faultCode)) {
            PileFaultDto pileFault = faultEventMap.get(faultCode);
            alarmRecord.setEventId(pileFault.getId());
            alarmRecord.setEventName(pileFault.getEventName());
            alarmRecord.setEventLevel(pileFault.getEventLevel());
        }
        alarmRecord.setAlarmStatus(0);
        alarmRecord.setIgnoreStatus(0);
        alarmRecord.setAlarmType(2);
        alarmRecord.setCreateTime(LocalDateTime.now());
        return alarmRecord;
    }

    //更新告警记录数据
    public static void updateAlarmStatus(List<AlarmRecordEntity> alarmRecordList, AlarmRecordDao alarmRecordDao) {
        alarmRecordDao.batchUpdate(alarmRecordList.stream().peek(a -> {
            a.setAlarmStatus(1);
            a.setUpdateTime(LocalDateTime.now());
        }).collect(Collectors.toList()));
    }

    public static Double getStrategyCfg(Integer runMode, Integer strategyType, Double strategyCfg) {
        double result = 0.0;
        if (StringUtil.isNotEmpty(strategyType)) {
            if (strategyType == 0) {
                if (runMode == 0) { //充电模式
                    result = 100.0;
                } else {
                    result = 0.0;
                }
            }
            if (StringUtil.isNotEmpty(strategyCfg)) {
                switch (strategyType) {
                    case 1:
                        result = strategyCfg;
                        break;
                    case 2:
                    case 3:
                        result = strategyCfg * 0.001;
                        break;
                }
            }
        }
        return result;
    }

    private static SettlementRecordEntity initSettlementRecord(String orderNum, BigDecimal totalCost) {
        SettlementRecordEntity settlementRecord = settlementRecordDao.findByOrderNum(orderNum);
        if (settlementRecord != null) {
            settlementRecord.setSettlementState(1);
            settlementRecord.setOriginalCost(totalCost);
            settlementRecord.setActualTotalCost(new BigDecimal("0"));
            settlementRecord.setActualTotalElect(new BigDecimal("0"));
            settlementRecord.setActualTotalFee(new BigDecimal("0"));
            settlementRecord.setTotalElectReduction(new BigDecimal("0"));
            settlementRecord.setTotalFeeReduction(new BigDecimal("0"));
            settlementRecord.setRefundMoney(new BigDecimal("0"));
            return settlementRecord;
        } else {
            settlementRecord = new SettlementRecordEntity();
            settlementRecord.setOrderNum(orderNum);
            settlementRecord.setPayWay(1);
            settlementRecord.setSettlementState(1);
            settlementRecord.setOriginalCost(totalCost);
            settlementRecord.setActualTotalCost(new BigDecimal("0"));
            settlementRecord.setActualTotalElect(new BigDecimal("0"));
            settlementRecord.setActualTotalFee(new BigDecimal("0"));
            settlementRecord.setTotalElectReduction(new BigDecimal("0"));
            settlementRecord.setTotalFeeReduction(new BigDecimal("0"));
            settlementRecord.setRefundMoney(new BigDecimal("0"));
            return settlementRecord;
        }
    }

    private static void updateOrderSettleMoney(OrderRecordEntity orderRecord, BigDecimal electMoney, BigDecimal serviceMoney, SettlementRecordEntity settlementRecord) {
        //获取该会员对应的折扣信息
        if (orderRecord.getRunMode() == 0) {
            //实付金额
            BigDecimal actualTotalCost = orderRecord.getTotalCost();
            BigDecimal actualTotalElect = new BigDecimal("0.0");
            BigDecimal actualTotalFee = new BigDecimal("0.0");
            BigDecimal totalElectReduction = new BigDecimal("0.0");
            BigDecimal totalFeeReduction = new BigDecimal("0.0");
            UserGroupInfoDto userGroup = togetherService.queryUserDiscountByPhoneNum(orderRecord.getPileCode(), orderRecord.getAccountData()).getData();
            if (userGroup != null) {
                //电费,服务费为0时 则存0
                if (electMoney.compareTo(BigDecimal.ZERO) > 0 || serviceMoney.compareTo(BigDecimal.ZERO) > 0) {
                    actualTotalCost = electMoney.add(serviceMoney);
                    if (StringUtil.isNotEmpty(userGroup.getElecDiscount()) && electMoney.compareTo(BigDecimal.ZERO) > 0) {
                        BigDecimal electDiscount = new BigDecimal(userGroup.getElecDiscount()).divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP);
                        actualTotalElect = electMoney.multiply(electDiscount); //折扣后的电费
                        totalElectReduction = (electMoney.subtract(actualTotalElect)).compareTo(BigDecimal.ZERO) == 0 ?
                                new BigDecimal("0.0") : electMoney.subtract(actualTotalElect); //电费减免
                        actualTotalCost = actualTotalElect;
                        if (StringUtil.isNotEmpty(userGroup.getServiceDiscount()) && serviceMoney.compareTo(BigDecimal.ZERO) > 0) {
                            BigDecimal serviceDiscount = new BigDecimal(userGroup.getServiceDiscount()).divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP);
                            actualTotalFee = serviceMoney.multiply(serviceDiscount); //折扣后的服务费
                            totalFeeReduction = serviceMoney.subtract(actualTotalFee); //服务费减免
                            actualTotalCost = actualTotalCost.add(actualTotalFee);
                        }
                    } else {
                        if (StringUtil.isNotEmpty(userGroup.getServiceDiscount()) && serviceMoney.compareTo(BigDecimal.ZERO) > 0) {
                            BigDecimal serviceDiscount = new BigDecimal(userGroup.getServiceDiscount()).divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP);
                            actualTotalFee = serviceMoney.multiply(serviceDiscount); //折扣后的服务费
                            totalFeeReduction = serviceMoney.subtract(actualTotalFee); //服务费减免
                            actualTotalCost = actualTotalFee;
                        }
                    }
                }
            }
            settlementRecord.setOriginalCost(DoubleUtil.getToBigDecimal(electMoney.add(serviceMoney).compareTo(orderRecord.getPrepayMoney()) > 0 ?
                    orderRecord.getPrepayMoney() : electMoney.add(serviceMoney)));
            settlementRecord.setActualTotalCost(DoubleUtil.getToBigDecimal(actualTotalCost));
            settlementRecord.setActualTotalElect(DoubleUtil.getToBigDecimal(actualTotalElect));
            settlementRecord.setActualTotalFee(DoubleUtil.getToBigDecimal(actualTotalFee));
            settlementRecord.setTotalElectReduction(DoubleUtil.getToBigDecimal(totalElectReduction));
            settlementRecord.setTotalFeeReduction(DoubleUtil.getToBigDecimal(totalFeeReduction));
        } else { //放电钱包 原价总金额存记录上报的总金额
            settlementRecord.setOriginalCost(orderRecord.getTotalCost());
        }
    }

    //更新订单结算记录
    public static void updateOrderSettleRecord(OrderRecordEntity orderRecord, Integer originalState, BigDecimal electMoney, BigDecimal serviceMoney) {
        if (orderRecord != null) {
            if (StringUtil.isNotEmpty(originalState) && (originalState != 2 && originalState != 3 && originalState != 5)) {
                //初始化结算记录数据
                SettlementRecordEntity settlementRecord = initSettlementRecord(orderRecord.getOrderNum(), orderRecord.getTotalCost());

                //小程序交易结算 (充电完成,启动失败,订单取消) 小程序平台进行结算
                if (Objects.equals(orderRecord.getPlatformLogo(), PlatformLogoVo.SUNMAX_LOGO) && orderRecord.getStarter() == 1
                        && orderRecord.getAccountType() == 3 && (orderRecord.getOrderStatus() == 2 || orderRecord.getOrderStatus() == 3
                        || orderRecord.getOrderStatus() == 5)) { //小程序启动结算

                    //更新订单结算记录数据
                    if (settlementRecord.getPayWay() != 1) {
                        updateOrderSettleMoney(orderRecord, electMoney, serviceMoney, settlementRecord);
                    }

                    //当账号类型为小程序充放电时,订单为已完成或者取消预约时或启动失败时进行结算
                    BigDecimal prepayMoney = orderRecord.getPrepayMoney();
                    //支付方式 1-免支付 2-微信支付 3-支付宝支付
                    switch (settlementRecord.getPayWay()) {
                        case 1:
                            settlementRecord.setSettlementState(1);
                            //放电订单 放电金额更新到会员钱包里面
                            if ((orderRecord.getRunMode() == 1 || orderRecord.getRunMode() == 2) && orderRecord.getTotalCost().compareTo(BigDecimal.ZERO) > 0) {
                                settlementRecord.setOriginalCost(orderRecord.getTotalCost());
                                try {
                                    ResponseResult<Void> result = webAppService.updateWalletBalance(WalletBalanceVo.builder()
                                            .orderNum(orderRecord.getOrderNum())
                                            .tradeMoney(orderRecord.getTotalCost())
                                            .tradeType(1)
                                            .tradeWay(1)
                                            .phoneNum(orderRecord.getAccountData())
                                            .siteId(orderRecord.getSiteId())
                                            .build());
                                    if (result.isSuccess()) {
                                        settlementRecord.setSettlementState(3);
                                    } else {
                                        settlementRecord.setSettlementState(2);
                                    }
                                } catch (Exception e) {
                                    log.error("更新会员钱包失败", e);
                                    settlementRecord.setSettlementState(2);
                                }
                            }
                            break;
                        //微信支付 支付宝支付只用于充电用户
                        case 2:
                        case 3:
                            try {
                                if (orderRecord.getRunMode() == 0 && prepayMoney != null && prepayMoney.compareTo(BigDecimal.ZERO) > 0) {
                                    //1.1 当退费余额大于0时 进行退费(原路退回)
                                    BigDecimal refundMoney = prepayMoney.subtract(settlementRecord.getActualTotalCost());
                                    if (refundMoney.compareTo(BigDecimal.ZERO) > 0) {
                                        ResponseResult<Void> result = webAppService.appletChargeRefund(AppletChargeRefundVo.builder()
                                                .orderNum(orderRecord.getOrderNum())
                                                .pileCode(orderRecord.getPileCode())
                                                .refundMoney(refundMoney)
                                                .platformType(1)
                                                .type(2).build());
                                        //结算成功 存入退款金额和结算状态
                                        if (result.isSuccess()) {
                                            settlementRecord.setRefundMoney(refundMoney);
                                            settlementRecord.setSettlementState(3);
                                        } else {
                                            settlementRecord.setSettlementState(2);
                                        }
                                    } else {
                                        settlementRecord.setSettlementState(3);
                                    }
                                }
                            } catch (Exception e) {
                                log.error("微信退款失败", e);
                            }
                            break;
                    }
                }
                settlementRecordDao.save(settlementRecord);
            }
            //清除当前枪的缓存
            if (StringUtil.isNotEmpty(orderRecord.getPileCode()) && StringUtil.isNotEmpty(orderRecord.getGunCode())) {
                String pileCode = orderRecord.getPileCode();
                String gunCode = String.valueOf(orderRecord.getGunCode());
                RedisGeneralUtil.executePile(pileCode, () -> {
                    PileRealModel pileRealModel = RedisGeneralUtil.getPileRealModel(pileCode);
                    if (pileRealModel != null && pileRealModel.getGunRealModelMap().containsKey(gunCode)) {
                        PileRealModel.GunRealModel gunRealModel = new PileRealModel.GunRealModel();
                        gunRealModel.setGunCode(gunCode);
                        gunRealModel.setGunStatus(pileRealModel.getGunRealModelMap().get(gunCode).getGunStatus());
                        gunRealModel.setGunOriginalStatus(pileRealModel.getGunRealModelMap().get(gunCode).getGunOriginalStatus());
                        gunRealModel.setVehicleConnState(pileRealModel.getGunRealModelMap().get(gunCode).getVehicleConnState());
                        gunRealModel.setFaultCodes(pileRealModel.getGunRealModelMap().get(gunCode).getFaultCodes());
                        pileRealModel.getGunRealModelMap().put(gunCode, gunRealModel);
                        if (MapUtils.isNotEmpty(pileRealModel.getGunRealModelMap())) {
                            double rechargePower = pileRealModel.getGunRealModelMap().values().stream().filter(gun ->
                                            (StringUtil.isNotEmpty(pileRealModel.getWorkStatus()) && pileRealModel.getWorkStatus() != 88) &&
                                                    StringUtil.isNotEmpty(gun.getGunStatus()) && StringUtil.isNotEmpty(gun.getOutPower()) &&
                                                    (Objects.equals(gun.getGunStatus(), 1) || Objects.equals(gun.getGunStatus(), 2)))
                                    .mapToDouble(PileRealModel.GunRealModel::getOutPower).sum();
                            double dischargePower = pileRealModel.getGunRealModelMap().values().stream().filter(gun ->
                                            (StringUtil.isNotEmpty(pileRealModel.getWorkStatus()) && pileRealModel.getWorkStatus() != 88) &&
                                                    StringUtil.isNotEmpty(gun.getGunStatus()) && StringUtil.isNotEmpty(gun.getOutPower()) &&
                                                    (Objects.equals(gun.getGunStatus(), 4) || Objects.equals(gun.getGunStatus(), 5)))
                                    .mapToDouble(PileRealModel.GunRealModel::getOutPower).sum();
                            pileRealModel.setTotalPower(rechargePower + dischargePower);
                            pileRealModel.setRecChargePower(rechargePower);
                            pileRealModel.setDisChargePower(dischargePower);
                        }
                        RedisGeneralUtil.setPileRealModel(pileCode, pileRealModel);
                    }
                });
            }
        }
    }

    //更新充电桩充电中的订单状态
    public static void updateOrderChargingStatus(String pileCode, Integer orderStatus) {
        //把充电中的订单状态改为订单挂起
        List<OrderRecordEntity> orderRecordList = orderRecordDao.findAllByPileCodeAndOrderStatus(pileCode, 1);
        if (CollectionUtils.isNotEmpty(orderRecordList)) {
            //修改订单状态
            orderRecordDao.batchUpdate(orderRecordList.stream().peek(orderRecord -> orderRecord.setOrderStatus(orderStatus))
                    .collect(Collectors.toList()));
            //创建补单记录
            if (orderStatus == 4) {
                batchAddRepairOrderRecord(orderRecordList);
            }

        }
    }

    //批量创单补单记录数据
    public static void batchAddRepairOrderRecord(List<OrderRecordEntity> orderRecordList) {
        Set<String> orderNums = orderRecordList.stream().map(OrderRecordEntity::getOrderNum).collect(Collectors.toSet());
        Map<String, RepairOrderRecordEntity> historyMap = repairOrderRecordDao.findByOrderNumIn(orderNums).stream().collect(Collectors
                .toMap(RepairOrderRecordEntity::getOrderNum, Function.identity(), (k1, k2) -> k1));
        repairOrderRecordDao.batchUpdate(orderRecordList.stream().map(orderRecord -> {
            RepairOrderRecordEntity result = new RepairOrderRecordEntity();
            if (historyMap.containsKey(orderRecord.getOrderNum())) {
                result.setId(historyMap.get(orderRecord.getOrderNum()).getId());
            }
            result.setOrderNum(orderRecord.getOrderNum());
            result.setRepairStatus(0);
            return result;
        }).collect(Collectors.toSet()));
    }

    //批量更新补单记录数据
    public static void batchUpdateRepairOrderRecord(List<OrderRecordEntity> orderRecordList) {
        //根据多个订单号查询补单的数据
        Set<String> orderNums = orderRecordList.stream().map(OrderRecordEntity::getOrderNum).collect(Collectors.toSet());
        List<RepairOrderRecordEntity> repairOrderRecordList = repairOrderRecordDao.findByOrderNumIn(orderNums);
        if (CollectionUtils.isNotEmpty(repairOrderRecordList)) {
            repairOrderRecordDao.batchUpdate(repairOrderRecordList.stream().peek(repairOrder -> {
                repairOrder.setRepairStatus(1);
                LocalDateTime updateTime = LocalDateTime.now();
                repairOrder.setExceptionTime(DateUtil.secToTime(DateUtil.compareDiffBetweenSecond(repairOrder.getCreateTime(), updateTime)));
                repairOrder.setRepairOperator("系统后台操作员");
                repairOrder.setUpdateTime(updateTime);
            }).collect(Collectors.toList()));
        }
    }

    public static String getSiteId(String pileCode) {
        if (StringUtil.isNotEmpty(pileCode)) {
            DeviceBasicInfoDto device = deviceService.findDeviceBasicInfoByCodes(Collections.singletonList(pileCode)).getData().get(pileCode);
            if (device != null) {
                return device.getSiteId();
            }
        }
        return null;
    }

    /**
     * 添加mqtt记录
     *
     * @param pileCode     桩编号
     * @param gunCode      枪编号
     * @param type         报文类型 1-发送 2-接收
     * @param protocolType 协议类型 1-启动命令 2-启动响应 3-启动事件 4-停止命令 5-停止响应 6-停止事件 7-记录上报 8-日志数据上报 9-复位命令 10-复位响应 11-设置二维码命令 12-设置二维码响应
     * @param messageType  消息类型 1-外网 2-内网
     * @param cmd          报文命令
     * @param content      报文内容
     */
    public static void saveMqttRecord(String pileCode, String gunCode, Integer type, Integer protocolType, Integer messageType, String cmd, Object content) {
        MqttRecordEntity mqttRecordEntity = new MqttRecordEntity();
        mqttRecordEntity.setPileCode(pileCode);
        mqttRecordEntity.setGunCode(gunCode);
        mqttRecordEntity.setType(type);
        mqttRecordEntity.setProtocolType(protocolType);
        mqttRecordEntity.setMessageType(messageType);
        mqttRecordEntity.setCmd(cmd);
        mqttRecordEntity.setContent(JSON.toJSONString(content));
        mqttRecordEntity.setDateTime(LocalDateTime.now());
        mqttRecordDao.save(mqttRecordEntity);
    }

    public static void pileSetQr(String terminalCode, String pileCode) {
        if (StringUtil.isEmpty(pileCode)) {
            return;
        }
        //根据电桩编号查询电枪二维码数据
        List<DeviceGunInfoDto> deviceGunList = deviceService.findDeviceGunInfoByDeviceCodes(Collections.singletonList(pileCode)).getData().get(pileCode);
        if (CollectionUtils.isNotEmpty(deviceGunList)) {
            //获取二维码字符串
            String qrCodes = null;
            Optional<DeviceGunInfoDto> optional = deviceGunList.stream().filter(g -> StringUtil.isNotEmpty(g.getQrCodes())).findFirst();
            if (optional.isPresent()) {
                Map<String, String> paramMap = StringUtil.parseQueryString(optional.get().getQrCodes());
                if (MapUtils.isNotEmpty(paramMap)) {
                    if (paramMap.containsKey("prot") && Objects.equals(paramMap.get("prot"), "ykc")) {
                        qrCodes = optional.get().getQrCodes();
                        if (paramMap.containsKey("No")) {
                            qrCodes = qrCodes.replace(paramMap.get("No"), FileUtil.separator);
                        }
                    }
                }
            }
            if (qrCodes != null && StringUtil.isNotEmpty(qrCodes) && StringUtil.isEmpty(terminalCode)) {
                //下发内网设置二维码
                //下发二维码
                PileSetQrCmdDto resetCmd = new PileSetQrCmdDto();
                resetCmd.setPilesCode(pileCode);
                resetCmd.setQrFormat(1); //默认传1 前缀+桩编号+枪编号
                resetCmd.setQrLen(qrCodes.length());
                resetCmd.setQrStr(qrCodes);

                InterMqttConfig.sendToMqtt(pileCode, CmdConstant.CMD_PILE_SETQR, resetCmd);
                //保存外网设置二维码数据
                PileRecordUtil.saveMqttRecord(pileCode, null, 1, 11, 2, CmdConstant.CMD_PILE_SETQR, resetCmd);
            }

        }
    }

}
