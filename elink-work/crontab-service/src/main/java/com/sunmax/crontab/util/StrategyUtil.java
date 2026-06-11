package com.sunmax.crontab.util;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.google.common.collect.Maps;
import com.sunmax.common.config.redis.RedisGeneralUtil;
import com.sunmax.common.model.general.PileRealModel;
import com.sunmax.common.util.DateUtil;
import com.sunmax.common.util.DoubleUtil;
import com.sunmax.common.util.SpringBeanUtil;
import com.sunmax.common.util.StringUtil;
import com.sunmax.common.vo.crontab.PileStartControlVo;
import com.sunmax.common.vo.crontab.PowerControlParamVo;
import com.sunmax.common.vo.crontab.StrategyTaskVo;
import com.sunmax.common.vo.protocol.PilePowerCtrlVo;
import com.sunmax.crontab.dto.PileGunAdjustPDto;
import com.sunmax.crontab.dto.StationAdjustPowerDto;
import com.sunmax.crontab.service.feign.ConfigService;
import com.sunmax.crontab.service.feign.ProtocolService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.compress.utils.Lists;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 平台策略计算工具类
 */
@Slf4j
public class StrategyUtil {

    //电桩启动管理map key(电桩编号+枪编号) -> 开始时间
    public static Map<String, PileStartControlVo> pileOccupyMap = Maps.newConcurrentMap();

    private static ProtocolService protocolService;

    private static ConfigService configService;

    /**
     * 晟曼平台自动策略任务
     *
     * @param taskVo     任务参数
     * @param sourceType 来源类型 1-自建 2-城市充电接入
     * @return 是否调控 true-需要 false-不需要
     */
    public static Boolean platformAutoControlTask(StrategyTaskVo taskVo, Integer sourceType) {
//        log.info("自动策略定时任务执行入参:{}", taskVo);

        //功率控制策略计算
        List<PilePowerCtrlVo> pilePowerCtrlVos = controlPowerCalculate(taskVo);
        //对需要调的电桩功率进行调控
        if (CollectionUtils.isNotEmpty(pilePowerCtrlVos)) {
            log.info("平台自动策略需要调控的电桩功率控制参数:{}", pilePowerCtrlVos);
            //晟曼平台接入
            if (sourceType == 1) {
                protocolService.batchPilePowerCtrl(pilePowerCtrlVos);
            }
            //城市充电接入
            if (sourceType == 2) {
                configService.interflowBatchPowerControl(pilePowerCtrlVos.stream().map(pilePowerCtrlVo -> {
                    PowerControlParamVo pilePowerCtrl = new PowerControlParamVo();
                    pilePowerCtrl.setPileCode(pilePowerCtrlVo.getPileCode());
                    if (StringUtil.isNotEmpty(pilePowerCtrlVo.getGunCode())) {
                        pilePowerCtrl.setGunCode(Integer.parseInt(pilePowerCtrlVo.getGunCode()));
                    }
                    pilePowerCtrl.setOutPower(pilePowerCtrlVo.getOutPower());
                    return pilePowerCtrl;
                }).collect(Collectors.toList()));
            }
        }
        return true;
    }

    /**
     * 功率控制策略计算
     *
     * @param taskVo 任务参数
     * @return 电桩功率控制参数集合
     */
    public static List<PilePowerCtrlVo> controlPowerCalculate(StrategyTaskVo taskVo) {
        //定义功率控制参数集合
        List<PilePowerCtrlVo> resultList = Lists.newArrayList();

        if (protocolService == null) {
            protocolService = SpringBeanUtil.getBean(ProtocolService.class);
        }
        if (configService == null) {
            configService = SpringBeanUtil.getBean(ConfigService.class);
        }

        if (StringUtil.isEmpty(taskVo.getMaxP()) || StringUtil.isEmpty(taskVo.getWavePower())) {
            log.error("策略参数异常,请检查,策略参数:{}", taskVo);
            return resultList;
        }

        //1.获取策略需要的参数
        //停止日期
        String filterDates = taskVo.getFilterDates();
        //执行类型 1-全时段 2-工作日 3-周末 4-自定义时段
        Integer executeType = taskVo.getExecuteType();
        //执行时段 自定义时段{1:["00:00:00","01:00:00"],2:["00:00:00"]}
        String executeTime = taskVo.getExecuteTime();
        //策略使能 true-开启 false-关闭
        Boolean enabled = taskVo.getEnabled();
        //站点最大充电功率
        BigDecimal maxP = BigDecimal.valueOf(taskVo.getMaxP());
        //功率允许波动值
        BigDecimal wavePower = BigDecimal.valueOf(taskVo.getWavePower());
        //优先级 1-SOC优先 2-顺序优先 3-综合
        Integer priority = taskVo.getPriority();
        //控制对象(被控制电桩数据 桩编号+枪编号 -> 控制电桩数据)
        Map<String, StrategyTaskVo.StrategyPile> strategyPileMap = taskVo.getStrategyPileMap();
        //当前日期(年月日)
        String nowDateStr = DateUtil.localDateToStr(LocalDate.now());
        //当前日期(年月日时分秒)
        LocalDateTime nowDateTime = LocalDateTime.now();
        //目标值
        BigDecimal targetP = taskVo.getTargetP();
        //调控时间
        Long controlTime = taskVo.getControlTime();
        //有效时长(秒)(暂定)
        Integer validTime = taskVo.getValidTime();

        //2.校验站点开关
        if (!enabled) {
            removeAllPileOccupyMap(strategyPileMap);
            log.error("站点未开启策略使能,不执行策略");
            return resultList;
        }

        //3.校验调控时间
        if (StringUtil.isNotEmpty(controlTime) && StringUtil.isNotEmpty(targetP) && StringUtil.isNotEmpty(validTime)) {
            //如果策略执行时间未到 则执行
            long nowSecond = nowDateTime.atZone(ZoneOffset.UTC).toInstant().getEpochSecond();
            if (nowSecond - controlTime > validTime) {
                removeAllPileOccupyMap(strategyPileMap);
                log.error("有效时长过期,不允许调控");
                return resultList;
            }
        }

        //3.校验停止日期
        if (StringUtil.isNotEmpty(filterDates)) {
            //停止日期包含今天日期的话 不执行
            if (filterDates.contains(nowDateStr)) {
                removeAllPileOccupyMap(strategyPileMap);
                log.error("站点停止日期包含今天日期,不执行策略");
                return resultList;
            }
        }

        //4.校验执行类型和执行时间
        switch (executeType) {
//            case 1: //全时段
//                break;
            case 2: //工作日
                if (!DateUtil.isWorkDayOrWeek(nowDateStr, 1)) {
                    removeAllPileOccupyMap(strategyPileMap);
                    log.error("站点执行类型为工作日,今天不是工作日,不执行策略");
                    return resultList;
                }
                break;
            case 3: //周末
                if (!DateUtil.isWorkDayOrWeek(nowDateStr, 2)) {
                    removeAllPileOccupyMap(strategyPileMap);
                    log.error("站点执行类型为周末,今天不是周末,不执行策略");
                    return resultList;
                }
                break;
            case 4: //自定义时段
                if (StringUtil.isNotEmpty(executeTime)) {
                    Map<Integer, String> executeTimeMap = JSON.parseObject(executeTime, new TypeReference<Map<Integer, String>>() {
                    });
                    //获取当天是周几
                    int day = nowDateTime.getDayOfWeek().getValue();
                    if (executeTimeMap.containsKey(day)) {
                        Map<Integer, Integer> timeMap = JSON.parseArray(executeTimeMap.get(day), String.class).stream().map(s ->
                                Integer.parseInt(s.substring(0, 2))).collect(Collectors.toMap(a -> a, a -> a, (k1, k2) -> k1));
                        if (!timeMap.containsKey(nowDateTime.getHour())) {
                            removeAllPileOccupyMap(strategyPileMap);
                            log.error("站点执行类型为自定义时段,今天不是自定义时段,不执行策略");
                            return resultList;
                        }
                    } else {
                        removeAllPileOccupyMap(strategyPileMap);
                        log.error("站点执行类型为自定义时段,今天没有自定义时段,不执行策略");
                        return resultList;
                    }
                }
                break;
        }

        //5.计算站点当前运行功率，占用功率，充放电可调功率
        StationAdjustPowerDto stationAdjustPower = getStationAdjustPower(strategyPileMap, BigDecimal.ZERO);
        BigDecimal curP = DoubleUtil.getToBigDecimal(stationAdjustPower.getCurP(), 2);
        BigDecimal occupyP = DoubleUtil.getToBigDecimal(stationAdjustPower.getOccupyP(), 2);
        BigDecimal chargeUpAdjustP = DoubleUtil.getToBigDecimal(stationAdjustPower.getChargeUpAdjustP(), 2);
        BigDecimal chargeDownAdjustP = DoubleUtil.getToBigDecimal(stationAdjustPower.getChargeDownAdjustP(), 2);
        BigDecimal dischargeUpAdjustP = DoubleUtil.getToBigDecimal(stationAdjustPower.getDischargeUpAdjustP(), 2);
        BigDecimal dischargeDownAdjustP = DoubleUtil.getToBigDecimal(stationAdjustPower.getDischargeDownAdjustP(), 2);
        List<PileGunAdjustPDto> pileGunAdjustPList = stationAdjustPower.getPileGunAdjustPList();
        //判断是否根据目标值调控
        boolean isControl = StringUtil.isNotEmpty(targetP);
        if (isControl) {
            //6.判断是否调控
            //1.站点总功率(maxP)-当前总功率(curP)-占用功率(occupyP)>功率波动值(wavePower)
            //2.站点总功率(maxP)-当前总功率(curP)-占用功率(occupyP)<功率波动值(wavePower)的相反数
            //注：满足以上两个条件之一则需要调控 - true
            //不满足则不需要调控 - false
            //调控功率=站点总功率-当前功率-占用功率
            BigDecimal adjustP = targetP.subtract(curP).subtract(occupyP);
            log.info("1.站点目标值:{},当前总功率:{},占用功率:{},功率波动值:{},是否调控:{}", targetP, curP, occupyP, wavePower, isControl);
            log.info("2.充电可上调功率{},充电可下调功率:{},放电可上调功率{},放电可下调功率{}", chargeUpAdjustP, chargeDownAdjustP, dischargeUpAdjustP, dischargeDownAdjustP);
            log.info("3.电桩可调功率列表:{}", pileGunAdjustPList);
            if (adjustP.compareTo(wavePower) > 0 || adjustP.compareTo(wavePower.negate()) < 0) {
                if (adjustP.compareTo(BigDecimal.ZERO) > 0 && (chargeUpAdjustP.compareTo(BigDecimal.ZERO) > 0 || dischargeDownAdjustP.compareTo(BigDecimal.ZERO) > 0)) {
                    //1.充电上调
                    if (chargeUpAdjustP.compareTo(BigDecimal.ZERO) > 0) {
                        adjustP = pileChargeUpAdjustP(priority, adjustP, wavePower, pileGunAdjustPList, resultList);
                    }
                    //2.放电下调
                    if (adjustP.compareTo(BigDecimal.ZERO) > 0 && dischargeDownAdjustP.compareTo(BigDecimal.ZERO) > 0) {
                        adjustP = pileDischargeDownAdjustP(priority, adjustP, wavePower, pileGunAdjustPList, resultList);
                    }
                    if (adjustP.compareTo(BigDecimal.ZERO) > 0) {
                        log.error("无法完成正值调控目标, 策略id:{}, 调控功率：{}", taskVo.getId(), adjustP);
                    }
                } else if (adjustP.compareTo(BigDecimal.ZERO) < 0 && (dischargeUpAdjustP.compareTo(BigDecimal.ZERO) > 0 || chargeDownAdjustP.compareTo(BigDecimal.ZERO) > 0)) {
                    //1.放电上调
                    if (dischargeUpAdjustP.compareTo(BigDecimal.ZERO) > 0) {
                        adjustP = pileDischargeUpAdjustP(priority, adjustP, wavePower, pileGunAdjustPList, resultList);
                    }
                    //2.充电下调
                    if (adjustP.compareTo(BigDecimal.ZERO) < 0 && chargeDownAdjustP.compareTo(BigDecimal.ZERO) > 0) {
                        adjustP = pileChargeDownAdjustP(priority, adjustP, wavePower, pileGunAdjustPList, resultList);
                    }
                    if (adjustP.compareTo(BigDecimal.ZERO) < 0) {
                        log.error("无法完成负值调控目标, 策略id:{}, 调控功率：{}", taskVo.getId(), adjustP);
                    }
                }
            } else {
                //调控功率=充放电可上调功率最小值
                adjustP = chargeUpAdjustP.min(dischargeUpAdjustP);
                if (adjustP.compareTo(BigDecimal.ZERO) > 0) {
                    //均调
                    // 1.充电上调
                    BigDecimal chargeUp = pileChargeUpAdjustP(priority, adjustP, wavePower, pileGunAdjustPList, resultList);
                    // 2.放电上调
                    BigDecimal dischargeUp = pileDischargeUpAdjustP(priority, adjustP.negate(), wavePower, pileGunAdjustPList, resultList);
                    log.error("均调,策略id:{}, 充电上调剩余功率:{}, 放电上调剩余功率:{}", taskVo.getId(), chargeUp, dischargeUp);
                }
            }
        } else {
            //充放电有序控制
            //有序控制上调功率
            if (curP.add(occupyP).compareTo(maxP) < 0) {
                //获取整站剩余功率
                BigDecimal upPower = maxP.subtract(occupyP).subtract(curP);
                if (upPower.compareTo(wavePower) > 0 && upPower.compareTo(BigDecimal.ZERO) > 0) {
                    upPower = orderedPowerUp(priority, upPower, wavePower, pileGunAdjustPList, resultList);
//                    if (upPower.compareTo(BigDecimal.ZERO) > 0) {
//                        log.error("左边 往上调功率, 策略id:{}, 剩余功率:{}", taskVo.getId(), upPower);
//                    }
                } else {
                    //均调功率
                    occupyPower(occupyP, maxP, curP, wavePower, priority, pileGunAdjustPList, resultList);
                }
            } else {
                //有序控制下调
                BigDecimal downPower = curP.add(occupyP).subtract(maxP);
                if (downPower.compareTo(wavePower) > 0) { //右边 往下调功率
                    downPower = orderedPowerDown(priority, downPower, wavePower, pileGunAdjustPList, resultList);
//                    if (downPower.compareTo(BigDecimal.ZERO) > 0) {
//                        log.error("右边 往下调功率,策略id:{}, 剩余功率:{}", taskVo.getId() ,downPower);
//                    }
                } else {
                    //均调功率
                    occupyPower(occupyP, maxP, curP, wavePower, priority, pileGunAdjustPList, resultList);
                }
            }
            removeAllPileOccupyMap(strategyPileMap);
        }
        return resultList;
    }

    //批量删除工具类
    private static void removeAllPileOccupyMap(Map<String, StrategyTaskVo.StrategyPile> strategyPileMap) {
        pileOccupyMap.forEach((key, value) -> {
            if (strategyPileMap.containsKey(key)) {
                pileOccupyMap.remove(key);
            }
        });
    }

    //获取站点可调功率
    public static StationAdjustPowerDto getStationAdjustPower(Map<String, StrategyTaskVo.StrategyPile> strategyPileGunMap, BigDecimal wavePower) {
        //返回的对象
        StationAdjustPowerDto result = new StationAdjustPowerDto();

        if (MapUtils.isNotEmpty(strategyPileGunMap)) {
            //获取电桩运行中的数据条数
            Map<String, List<StrategyTaskVo.StrategyPile>> pileMap = strategyPileGunMap.values().stream().collect(Collectors
                    .groupingBy(StrategyTaskVo.StrategyPile::getCid));
            //获取电桩实时数据
            Map<String, PileRealModel> pileRealMap = Maps.newConcurrentMap();
            Map<String, Long> pileRunTotalMap = Maps.newConcurrentMap();
            List<Integer> gunStates = Arrays.asList(1, 2, 4, 5);
            pileMap.keySet().forEach(pileCode -> {
                PileRealModel pileRealModel = RedisGeneralUtil.getPileRealModel(pileCode);
                if (pileRealModel != null) {
                    pileRealMap.put(pileCode, pileRealModel);
                    pileRunTotalMap.put(pileCode, pileRealModel.getGunRealModelMap().values().stream().filter(entry -> gunStates.contains(entry.getGunStatus())).count());
                }
            });
            for (Map.Entry<String, StrategyTaskVo.StrategyPile> entry : strategyPileGunMap.entrySet()) {
                String key = entry.getKey();
                StrategyTaskVo.StrategyPile strategyPile = entry.getValue();
                if (pileRealMap.containsKey(strategyPile.getCid()) && StringUtil.isNotEmpty(strategyPile.getGid())) {
                    //获取电桩实时数据
                    PileRealModel pileRealModel = pileRealMap.get(strategyPile.getCid());

                    Long runTotals = 0L;
                    if (pileRunTotalMap.containsKey(strategyPile.getCid())) {
                        runTotals = pileRunTotalMap.get(strategyPile.getCid());
                    }

                    //获取电桩占用功率
                    if (pileOccupyMap.containsKey(key)) {
                        //1.电桩离线(资源占用至上线,最大占用时长判断)
                        boolean pileStatus;
                        if (StringUtil.isNotEmpty(pileRealModel.getWorkStatus()) && pileRealModel.getWorkStatus() == 88 && StringUtil.isNotEmpty(pileRealModel.getOfflineTime())) {
                            LocalDateTime getOfflineTime = DateUtil.strToLocalDateTime(pileRealModel.getOfflineTime());
                            pileStatus = LocalDateTime.now().isBefore(getOfflineTime.plusSeconds((long) (strategyPile.getOfflineDuration() * 3600)));
                        } else {
                            pileStatus = true;
                        }
                        PileStartControlVo pileStartVo = pileOccupyMap.get(key);
                        if (pileStatus && StringUtil.isNotEmpty(pileStartVo.getRunMode())) {
                            if (pileStartVo.getRunMode() == 0) {
                                if (runTotals > 0) {
                                    result.setOccupyP(result.getOccupyP().add(BigDecimal.valueOf(strategyPile.getRatedP() / runTotals)));
                                } else {
                                    result.setOccupyP(result.getOccupyP().add(BigDecimal.valueOf(strategyPile.getMaxP())));
                                }
                            }
                            if (pileStartVo.getRunMode() == 1) {
                                if (runTotals > 0) {
                                    result.setOccupyP(result.getOccupyP().subtract(BigDecimal.valueOf(strategyPile.getRatedP() / runTotals)));
                                } else {
                                    result.setOccupyP(result.getOccupyP().subtract(BigDecimal.valueOf(strategyPile.getMaxP())));
                                }
                            }
                            continue;
                        } else { //当电桩占用离线超过设置时间时，直接删除
                            pileOccupyMap.remove(key);
                        }
                    }
                    String gunCode = String.valueOf(strategyPile.getGid());
                    if (pileRealModel.getGunRealModelMap().containsKey(gunCode)) {
                        PileRealModel.GunRealModel gunReal = pileRealModel.getGunRealModelMap().get(gunCode);
                        BigDecimal pileMaxPower = null;
                        BigDecimal gunMaxPower = null;
                        BigDecimal gunMinPower = null;
                        //不支持调控则调过 不计算
                        if (StringUtil.isEmpty(strategyPile.getControlType()) || strategyPile.getControlType() == 2) {
                            continue;
                        }
                        if (StringUtil.isNotEmpty(strategyPile.getRatedP())) {
                            pileMaxPower = DoubleUtil.getAbsBigDecimal(BigDecimal.valueOf(strategyPile.getRatedP()), 5);
                        }
                        if (StringUtil.isNotEmpty(strategyPile.getMaxP())) {
                            gunMaxPower = DoubleUtil.getAbsBigDecimal(BigDecimal.valueOf(strategyPile.getMaxP()), 5);
                        }
                        if (StringUtil.isNotEmpty(strategyPile.getMinP())) {
                            gunMinPower = DoubleUtil.getAbsBigDecimal(BigDecimal.valueOf(strategyPile.getMinP()), 5);
                        }
                        if (pileMaxPower == null || gunMaxPower == null || gunMinPower == null || StringUtil.isEmpty(pileMaxPower) || StringUtil.isEmpty(gunMaxPower) || StringUtil.isEmpty(gunMinPower)) {
                            continue;
                        }
                        //定义功率
                        BigDecimal power;
                        //运行多个枪时，每个枪的功率为运行总功率/运行枪数
                        if (runTotals > 0) {
                            pileMaxPower = pileMaxPower.divide(BigDecimal.valueOf(runTotals), 2, RoundingMode.HALF_UP);
                        }
                        if (StringUtil.isNotEmpty(gunReal.getReqPower())) {
                            BigDecimal reqPower = DoubleUtil.getAbsBigDecimal(BigDecimal.valueOf(gunReal.getReqPower()), 5);
                            power = pileMaxPower.min(gunMaxPower).min(reqPower);
                        } else {
                            power = pileMaxPower.min(gunMaxPower);
                        }

                        //计算当前输出功率 取绝对值
                        BigDecimal outPower = StringUtil.isEmpty(gunReal.getOutPower()) ? new BigDecimal("0.0") : DoubleUtil.getAbsBigDecimal(BigDecimal.valueOf(gunReal.getOutPower()), 5);
                        if (StringUtil.isNotEmpty(gunReal.getGunStatus()) && StringUtil.isNotEmpty(gunReal.getRunMode()) && gunReal.getRunMode() != -1) {
                            //定义电枪可调实体类
                            PileGunAdjustPDto pileGunAdjustP = new PileGunAdjustPDto();
                            pileGunAdjustP.setPileCode(strategyPile.getCid());
                            pileGunAdjustP.setGunCode(gunCode);
                            pileGunAdjustP.setCurPower(DoubleUtil.getToBigDecimal(BigDecimal.valueOf(gunReal.getOutPower()), 5));
                            pileGunAdjustP.setType(strategyPile.getType());
                            pileGunAdjustP.setStartTime(gunReal.getStartTime());
                            pileGunAdjustP.setBatterySOC(gunReal.getBatterySoc());
                            pileGunAdjustP.setRunMode(gunReal.getRunMode());

                            //获取当前总功率
                            if (gunReal.getGunStatus() == 2 || gunReal.getGunStatus() == 5) {
                                //累计总功率
                                result.setCurP(result.getCurP().add(BigDecimal.valueOf(gunReal.getOutPower())));
                            }

                            //枪充电中 统计当前充电功率，最大可上调充电功率，最小可上调充电功率
                            boolean isUpPower = power.subtract(outPower).compareTo(wavePower) >= 0;
                            boolean isDownPower = outPower.subtract(gunMinPower).compareTo(wavePower) >= 0;
                            if (gunReal.getGunStatus() == 2) {
                                //最大可上调充电功率
                                if (isUpPower) {
                                    result.setChargeUpAdjustP(result.getChargeUpAdjustP().add(power.subtract(outPower)));
                                    pileGunAdjustP.setUpPower(power.subtract(outPower));
                                }
                                //最大可下调充电功率
                                if (isDownPower) {
                                    result.setChargeDownAdjustP(result.getChargeDownAdjustP().add(outPower.subtract(gunMinPower)));
                                    pileGunAdjustP.setDownPower(outPower.subtract(gunMinPower));
                                }
                            }
                            //枪放电中 统计当前放电功率，最大可上调放电功率，最小可上调放电功率
                            if (gunReal.getGunStatus() == 5) {
                                //最大可上调放电功率
                                if (isUpPower) {
                                    result.setDischargeUpAdjustP(result.getDischargeUpAdjustP().add(power.subtract(outPower)));
                                    pileGunAdjustP.setUpPower(power.subtract(outPower));
                                }
                                //最大可下调放电功率
                                if (isDownPower) {
                                    result.setDischargeDownAdjustP(result.getDischargeDownAdjustP().add(outPower.subtract(gunMinPower)));
                                    pileGunAdjustP.setDownPower(outPower.subtract(gunMinPower));
                                }
                            }
                            //电枪可调功率数据
                            if (pileGunAdjustP.getUpPower().compareTo(BigDecimal.ZERO) > 0 || pileGunAdjustP.getDownPower().compareTo(BigDecimal.ZERO) > 0) {
                                result.getPileGunAdjustPList().add(pileGunAdjustP);
                            }
                        }
                    }

                }
            }
        }
        return result;
    }

    //有序控制上调
    public static BigDecimal orderedPowerUp(Integer priority, BigDecimal upPower, BigDecimal wavePower, List<PileGunAdjustPDto> pileGunAdjustPList, List<PilePowerCtrlVo> resultList) {
        //校验当前运行可调控电桩数据是否达到满功率
        pileGunAdjustPList = pileGunAdjustPList.stream().filter(pileRunData -> pileRunData.getUpPower().compareTo(wavePower) >= 0).collect(Collectors.toList());
        //对未满功率运行的桩进行调控
        if (CollectionUtils.isNotEmpty(pileGunAdjustPList)) {
            //优先级 1-SOC优先 2-顺序优先 3-综合
            //SOC优先: 1.直流桩SOC低优先 2.交流桩先到先得
            //顺序优先: 交直流桩统一遵循先到先得(按开始时间进行调)
            //综合: 1.交流桩先到先得 2.直流桩SOC低优先
            switch (priority) {
                case 1:
                    //直流桩调控 SOC优先 从低到高排序
                    upPower = pileDCPowerUpSocSort(pileGunAdjustPList, upPower, resultList);
                    //交流桩先到先得 过滤掉运行数据里没有开始时间的
                    upPower = pileACPowerUpStartTimeSort(pileGunAdjustPList, upPower, resultList);
                    break;
                case 2:
                    //交直流桩先到先得 过滤掉运行数据里没有开始时间的
                    upPower = pileACAndDCPowerUpStartTimeSort(pileGunAdjustPList, upPower, resultList);
                    break;
                case 3:
                    //交流桩先到先得 过滤掉运行数据里没有开始时间的
                    upPower = pileACPowerUpStartTimeSort(pileGunAdjustPList, upPower, resultList);
                    //直流桩调控 SOC优先 从低到高排序
                    upPower = pileDCPowerUpSocSort(pileGunAdjustPList, upPower, resultList);
                    break;
            }
        }
        return upPower;
    }

    //有序控制下调
    public static BigDecimal orderedPowerDown(Integer priority, BigDecimal downPower, BigDecimal wavePower, List<PileGunAdjustPDto> pileGunAdjustPList, List<PilePowerCtrlVo> resultList) {
        //校验当前运行可调控电桩数据是否是最小功率 最小功率则不调控 否则则调控
        pileGunAdjustPList = pileGunAdjustPList.stream().filter(pileRunData -> pileRunData.getDownPower().compareTo(wavePower) >= 0).collect(Collectors.toList());
//                log.info("右侧可下调功率: " + reducePower);
        switch (priority) {
            //优先级 1-SOC优先 2-顺序优先 3-综合
            //SOC优先: 1.直流桩SOC高优先 2.交流桩后到后调
            //顺序优先: 交直流桩统一遵循后到后调(按开始时间降序进行调)
            //综合: 1.交流桩后到后调 2.直流桩SOC高优先
            case 1:
                //1.直流桩SOC高优先 2.交流桩后到先调
                //直流桩调控 SOC优先 从高到低排序
                downPower = pileDCPowerDownSocSort(pileGunAdjustPList, downPower, resultList);
                //交流桩后到先调 过滤掉运行数据里没有开始时间的
                downPower = pileACPowerDownStartTimeSort(pileGunAdjustPList, downPower, resultList);
                break;
            case 2:
                //交直流桩统一遵循后到后调(按开始时间降序进行调)
                downPower = pileACAndDCPowerDownStartTimeSort(pileGunAdjustPList, downPower, resultList);
                break;
            case 3:
                //1.交流桩后到先调 2.直流桩SOC高优先
                //交流桩后到先调 过滤掉运行数据里没有开始时间的
                downPower = pileACPowerDownStartTimeSort(pileGunAdjustPList, downPower, resultList);
                //直流桩调控 SOC优先 从高到低排序
                downPower = pileDCPowerDownSocSort(pileGunAdjustPList, downPower, resultList);
                break;
        }
        return downPower;
    }

    public static void occupyPower(BigDecimal occupyP, BigDecimal maxP, BigDecimal currentP, BigDecimal wavePower, Integer priority, List<PileGunAdjustPDto> pileRunDataList, List<PilePowerCtrlVo> resultList) {
        if (occupyP.compareTo(BigDecimal.ZERO) <= 0) {
            //获取当前运行可调控电桩数据未满功率的桩
            //校验当前运行可调控电桩数据是否达到满功率
            List<PileGunAdjustPDto> underPowerList = pileRunDataList.stream().filter(pileRunData -> pileRunData.getUpPower().compareTo(wavePower) >= 0).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(underPowerList) || (currentP.subtract(wavePower)).compareTo(maxP) > 0) {
                //均调功率
                //获取所有电桩可下调的功率(所有电桩当前功率 - 最小功率)
                BigDecimal downPower = pileRunDataList.stream().map(PileGunAdjustPDto::getDownPower).reduce(BigDecimal.ZERO, BigDecimal::add);
                //获取所有电桩上调功率(最大充电功率 - 当前充电功率)
                BigDecimal upPower = maxP.subtract(currentP);
//                log.info("平台自动策略,充电中可上调功率:{},充电中可下调功率: {}", upSumPower, downSumPower);
                //优先级 1-SOC优先 2-顺序优先 3-综合
                switch (priority) {
                    case 1:
                        pileRunControlSoc(pileRunDataList, downPower, upPower, resultList);
                        break;
                    case 2:
                        pileRunControlSort(pileRunDataList, downPower, upPower, wavePower, resultList);
                        break;
                    case 3:
                        pileRunControlSynthesis(pileRunDataList, downPower, upPower, resultList);
                        break;
                }
            }
        }
    }

    //直流桩调控 SOC优先 从低到高排序
    public static BigDecimal pileDCPowerUpSocSort(List<PileGunAdjustPDto> pileGunAdjustPList, BigDecimal freeSumPower, List<PilePowerCtrlVo> resultList) {
        List<PileGunAdjustPDto> pileDCList = pileGunAdjustPList.stream().filter(p -> p.getType() == 1).sorted(Comparator
                .comparing(PileGunAdjustPDto::getBatterySOC)).collect(Collectors.toList());

        return controlPowerUp(pileDCList, freeSumPower, resultList);
    }

    //交流桩先到先得 顺序优先 按起始时间调控
    public static BigDecimal pileACPowerUpStartTimeSort(List<PileGunAdjustPDto> pileGunAdjustPList, BigDecimal freeSumPower, List<PilePowerCtrlVo> pilePowerCtrlVos) {
        //交流桩先到先得 过滤掉运行数据里没有开始时间的
        List<PileGunAdjustPDto> pileACList = pileGunAdjustPList.stream().filter(p -> p.getType() == 2 && StringUtil.isNotEmpty(p.getStartTime()))
                .sorted(Comparator.comparing(PileGunAdjustPDto::getStartTime)).collect(Collectors.toList());

        return controlPowerUp(pileACList, freeSumPower, pilePowerCtrlVos);
    }

    //交直流桩先到先得 顺序优先 按起始时间调控
    public static BigDecimal pileACAndDCPowerUpStartTimeSort(List<PileGunAdjustPDto> pileGunAdjustPList, BigDecimal freeSumPower, List<PilePowerCtrlVo> pilePowerCtrlVos) {
        //交直流桩先到先得 过滤掉运行数据里没有开始时间的
        List<PileGunAdjustPDto> pileACAndDcList = pileGunAdjustPList.stream().filter(p -> StringUtil.isNotEmpty(p.getStartTime()))
                .sorted(Comparator.comparing(PileGunAdjustPDto::getStartTime)).collect(Collectors.toList());

        return controlPowerUp(pileACAndDcList, freeSumPower, pilePowerCtrlVos);
    }

    //直流桩按SOC依次下调
    public static BigDecimal pileDCPowerDownSocSort(List<PileGunAdjustPDto> pileGunAdjustPList, BigDecimal reduceSumPower, List<PilePowerCtrlVo> pilePowerCtrlVos) {
        //直流桩按照SOC降序调
        List<PileGunAdjustPDto> pileACList = pileGunAdjustPList.stream().filter(p -> p.getType() == 1 && StringUtil.isNotEmpty(p.getBatterySOC()))
                .sorted(Comparator.comparing(PileGunAdjustPDto::getBatterySOC).reversed()).collect(Collectors.toList());

        return controlPowerDown(pileACList, reduceSumPower, pilePowerCtrlVos);
    }

    //交流桩后到后得 顺序优先 按起始时间降序调控
    public static BigDecimal pileACPowerDownStartTimeSort(List<PileGunAdjustPDto> pileGunAdjustPList, BigDecimal reduceSumPower, List<PilePowerCtrlVo> pilePowerCtrlVos) {
        //交流桩先到先得 过滤掉运行数据里没有开始时间的
        List<PileGunAdjustPDto> pileACList = pileGunAdjustPList.stream().filter(p -> p.getType() == 2 && StringUtil.isNotEmpty(p.getStartTime()))
                .sorted(Comparator.comparing(PileGunAdjustPDto::getStartTime).reversed()).collect(Collectors.toList());

        return controlPowerDown(pileACList, reduceSumPower, pilePowerCtrlVos);
    }

    //交流桩后到后得 顺序优先 按起始时间降序调控
    public static BigDecimal pileACAndDCPowerDownStartTimeSort(List<PileGunAdjustPDto> pileGunAdjustPList, BigDecimal reduceSumPower, List<PilePowerCtrlVo> pilePowerCtrlVos) {
        //交流桩先到先得 过滤掉运行数据里没有开始时间的
        List<PileGunAdjustPDto> pileACAndDCList = pileGunAdjustPList.stream().filter(p -> StringUtil.isNotEmpty(p.getStartTime()))
                .sorted(Comparator.comparing(PileGunAdjustPDto::getStartTime).reversed()).collect(Collectors.toList());

        return controlPowerDown(pileACAndDCList, reduceSumPower, pilePowerCtrlVos);
    }

    //上调功率代码封装
    private static BigDecimal controlPowerUp(List<PileGunAdjustPDto> pileUpList, BigDecimal adjustP, List<PilePowerCtrlVo> pilePowerCtrlVos) {
        if (CollectionUtils.isNotEmpty(pileUpList) && adjustP.compareTo(BigDecimal.ZERO) > 0) {
            for (PileGunAdjustPDto pileRunData : pileUpList) {
                //获取最大功率,当前功率,可调功率
                BigDecimal curPower = pileRunData.getCurPower();
                BigDecimal reducePower = pileRunData.getUpPower();
                if (adjustP.compareTo(BigDecimal.ZERO) > 0) {
                    if (adjustP.compareTo(reducePower) >= 0) { //按照最大功率调整
                        curPower = curPower.add(reducePower);
                        adjustP = adjustP.subtract(reducePower);
                    } else { //按照当前功率+剩余功率进行调控
                        curPower = curPower.add(adjustP);
                        adjustP = new BigDecimal("0.0");
                    }
                    pilePowerCtrlVos.add(pilePowerCtrlVo(pileRunData.getPileCode(), pileRunData.getGunCode(), pileRunData.getRunMode(), curPower));
                    //把调控后的功率存到当前功率中
                    pileRunData.setCurPower(curPower);
                }
            }
        }
        return adjustP;
    }

    //下调功率代码封装
    private static BigDecimal controlPowerDown(List<PileGunAdjustPDto> pileDownList, BigDecimal adjustP, List<PilePowerCtrlVo> pilePowerCtrlVos) {
        if (CollectionUtils.isNotEmpty(pileDownList) && adjustP.compareTo(BigDecimal.ZERO) > 0) {
            for (PileGunAdjustPDto pileRunData : pileDownList) {
                BigDecimal curPower = pileRunData.getCurPower();
                BigDecimal downPower = pileRunData.getDownPower();
                if (adjustP.compareTo(BigDecimal.ZERO) > 0) {
                    //当前桩可调功率大于站需要调功率时 按照站需要调的功率进行下调
                    if (downPower.compareTo(adjustP) >= 0) {
                        curPower = curPower.subtract(adjustP);
                        adjustP = new BigDecimal("0.0");
                    } else {
                        curPower = curPower.subtract(downPower);
                        adjustP = adjustP.subtract(downPower);
                    }
                    pilePowerCtrlVos.add(pilePowerCtrlVo(pileRunData.getPileCode(), pileRunData.getGunCode(), pileRunData.getRunMode(), curPower));
                    //把调控后的功率存到当前功率中
                    pileRunData.setCurPower(curPower);
                }
            }
        }
        return adjustP;
    }

    //电桩运行中是否可调控(soc优先)
    private static void pileRunControlSoc(List<PileGunAdjustPDto> pileRunDataList, BigDecimal downPower, BigDecimal upPower, List<PilePowerCtrlVo> pilePowerCtrlVos) {
        boolean isControl = false;
        //直流桩调控 SOC优先 从低到高排序
        List<PileGunAdjustPDto> pileDCList = pileRunDataList.stream().filter(p -> p.getType() == 1).sorted(Comparator
                .comparing(PileGunAdjustPDto::getBatterySOC)).collect(Collectors.toList());
        BigDecimal dcPower = new BigDecimal("0.0");
        for (PileGunAdjustPDto pileRunData : pileDCList) {
            if (dcPower.compareTo(BigDecimal.ZERO) > 0 && pileRunData.getCurPower().compareTo(dcPower) > 0) {
                isControl = true;
                break;
            }
            dcPower = pileRunData.getCurPower();
        }
        //交流桩先到先得 过滤掉运行数据里没有开始时间的
        List<PileGunAdjustPDto> pileACList = pileRunDataList.stream().filter(p -> p.getType() == 2 && StringUtil.isNotEmpty(p.getStartTime()))
                .sorted(Comparator.comparing(PileGunAdjustPDto::getStartTime).reversed()).collect(Collectors.toList());
        BigDecimal acPower = new BigDecimal("0.0");
        for (PileGunAdjustPDto pileRunData : pileACList) {
            if (isControl) {
                break;
            }
            if (acPower.compareTo(BigDecimal.ZERO) > 0 && pileRunData.getCurPower().compareTo(acPower) > 0) {
                isControl = true;
                break;
            }
            acPower = pileRunData.getCurPower();
        }
//        log.info("SOC优先是否可调状态: " + isControl);
        if (isControl) {
            //下调功率
            //1.直流桩SOC高优先 2.交流桩后到先调
            //直流桩调控 SOC优先 从高到低排序
            pileDCPowerDownSocSort(pileRunDataList, downPower, pilePowerCtrlVos);
            //交流桩后到后调 过滤掉运行数据里没有开始时间的
            pileACPowerDownStartTimeSort(pileRunDataList, downPower, pilePowerCtrlVos);

            //上调功率
            //直流桩调控 SOC优先 从低到高排序
            pileDCPowerUpSocSort(pileRunDataList, upPower, pilePowerCtrlVos);
            //交流桩先到先得 过滤掉运行数据里没有开始时间的
            pileACPowerUpStartTimeSort(pileRunDataList, upPower, pilePowerCtrlVos);
        }
    }

    //电桩运行中是否可调控(顺序优先)
    private static void pileRunControlSort(List<PileGunAdjustPDto> pileRunDataList, BigDecimal downPower, BigDecimal upPower, BigDecimal wavePower, List<PilePowerCtrlVo> resultList) {
        boolean isControl = false;
        //交直流桩先到先得 过滤掉运行数据里没有开始时间的
        List<PileGunAdjustPDto> pileACAndDcList = pileRunDataList.stream().filter(p -> StringUtil.isNotEmpty(p.getStartTime())
                && p.getUpPower().compareTo(wavePower) >= 0).sorted(Comparator.comparing(PileGunAdjustPDto::getStartTime)).collect(Collectors.toList());
        BigDecimal acAndDcPower = new BigDecimal("0.0");
        for (PileGunAdjustPDto pileRunData : pileACAndDcList) {
            if (acAndDcPower.compareTo(BigDecimal.ZERO) > 0 && pileRunData.getCurPower().compareTo(acAndDcPower) > 0) {
                isControl = true;
                break;
            }
            acAndDcPower = pileRunData.getCurPower();
        }
        if (isControl) {
            //下调功率
            //交直流桩先到先得 过滤掉运行数据里没有开始时间的
            pileACAndDCPowerDownStartTimeSort(pileRunDataList, downPower, resultList);
            //上调功率
            //交直流桩后到先调 过滤掉运行数据里没有开始时间的
            pileACAndDCPowerUpStartTimeSort(pileRunDataList, upPower, resultList);
        }
    }

    //电桩运行中是否可调控(综合)
    private static void pileRunControlSynthesis(List<PileGunAdjustPDto> pileRunDataList, BigDecimal downPower, BigDecimal upPower, List<PilePowerCtrlVo> resultList) {
        boolean isControl = false;
        //交流桩后到先调 过滤掉运行数据里没有开始时间的
        List<PileGunAdjustPDto> pileACList = pileRunDataList.stream().filter(p -> p.getType() == 2 && StringUtil.isNotEmpty(p.getStartTime()))
                .sorted(Comparator.comparing(PileGunAdjustPDto::getStartTime).reversed()).collect(Collectors.toList());
        BigDecimal acPower = new BigDecimal("0.0");
        for (PileGunAdjustPDto pileRunData : pileACList) {
            if (acPower.compareTo(BigDecimal.ZERO) > 0 && pileRunData.getCurPower().compareTo(acPower) > 0) {
                isControl = true;
                break;
            }
            acPower = pileRunData.getCurPower();
        }
        //直流桩调控 SOC优先 从高到低排序
        List<PileGunAdjustPDto> pileDCList = pileRunDataList.stream().filter(p -> p.getType() == 1).sorted(Comparator
                .comparing(PileGunAdjustPDto::getBatterySOC).reversed()).collect(Collectors.toList());
        BigDecimal dcPower = new BigDecimal("0.0");
        for (PileGunAdjustPDto pileRunData : pileDCList) {
            if (isControl) {
                break;
            }
            if (dcPower.compareTo(BigDecimal.ZERO) > 0 && pileRunData.getCurPower().compareTo(dcPower) > 0) {
                isControl = true;
                break;
            }
            dcPower = pileRunData.getCurPower();
        }
        if (isControl) {
            //1.交流桩后到先调 2.直流桩SOC高优先
            //交流桩后到先调 过滤掉运行数据里没有开始时间的
            pileACPowerDownStartTimeSort(pileRunDataList, downPower, resultList);
            //直流桩调控 SOC优先 从高到低排序
            pileDCPowerDownSocSort(pileRunDataList, downPower, resultList);

            //上调功率
            //交流桩先到先得 过滤掉运行数据里没有开始时间的
            pileACPowerUpStartTimeSort(pileRunDataList, upPower, resultList);
            //直流桩调控 SOC优先 从低到高排序
            pileDCPowerUpSocSort(pileRunDataList, upPower, resultList);
        }
    }


    //充电上调功率
    private static BigDecimal pileChargeUpAdjustP(Integer priority, BigDecimal adjustP, BigDecimal wavePower, List<PileGunAdjustPDto> pileGunAdjustPList, List<PilePowerCtrlVo> resultList) {
        List<PileGunAdjustPDto> chargeUpPowerList = pileGunAdjustPList.stream().filter(p -> p.getRunMode() == 0
                && p.getUpPower().doubleValue() > 0 && p.getUpPower().compareTo(wavePower) > 0).collect(Collectors.toList());
        switch (priority) {
            //优先级 1-SOC优先 2-顺序优先 3-综合
            //SOC优先: 1.直流桩SOC低优先 2.交流桩先到先得
            //顺序优先: 交直流桩统一遵循先到先得(按开始时间进行调)
            //综合: 1.交流桩先到先得 2.直流桩SOC低优先
            case 1:
                //直流桩调控 SOC优先 从低到高排序
                List<PileGunAdjustPDto> pileStartDCList = chargeUpPowerList.stream().filter(p -> p.getType() == 1).sorted(Comparator
                        .comparing(PileGunAdjustPDto::getBatterySOC)).collect(Collectors.toList());
                adjustP = chargePowerUp(pileStartDCList, adjustP, resultList);
                //交流桩先到先得 过滤掉运行数据里没有开始时间的
                List<PileGunAdjustPDto> pileEndACList = chargeUpPowerList.stream().filter(p -> p.getType() == 2 && StringUtil.isNotEmpty(p.getStartTime()))
                        .sorted(Comparator.comparing(PileGunAdjustPDto::getStartTime)).collect(Collectors.toList());
                adjustP = chargePowerUp(pileEndACList, adjustP, resultList);
                break;
            case 2:
                //交直流桩先到先得 过滤掉运行数据里没有开始时间的
                List<PileGunAdjustPDto> pileACAndDCList = chargeUpPowerList.stream().filter(p -> StringUtil.isNotEmpty(p.getStartTime()))
                        .sorted(Comparator.comparing(PileGunAdjustPDto::getStartTime)).collect(Collectors.toList());
                adjustP = chargePowerUp(pileACAndDCList, adjustP, resultList);
                break;
            case 3:
                //交流桩先到先得 过滤掉运行数据里没有开始时间的
                List<PileGunAdjustPDto> pileStartACList = chargeUpPowerList.stream().filter(p -> p.getType() == 2 && StringUtil.isNotEmpty(p.getStartTime()))
                        .sorted(Comparator.comparing(PileGunAdjustPDto::getStartTime)).collect(Collectors.toList());
                adjustP = chargePowerUp(pileStartACList, adjustP, resultList);
                //直流桩调控 SOC优先 从低到高排序
                List<PileGunAdjustPDto> pileEndDCList = chargeUpPowerList.stream().filter(p -> p.getType() == 1).sorted(Comparator
                        .comparing(PileGunAdjustPDto::getBatterySOC)).collect(Collectors.toList());
                adjustP = chargePowerUp(pileEndDCList, adjustP, resultList);
                break;
        }
        return adjustP;
    }

    private static BigDecimal chargePowerUp(List<PileGunAdjustPDto> chargeUpList, BigDecimal adjustP, List<PilePowerCtrlVo> resultList) {
        if (CollectionUtils.isNotEmpty(chargeUpList) && adjustP.compareTo(BigDecimal.ZERO) > 0) {
            for (PileGunAdjustPDto pileRunData : chargeUpList) {
                //获取最大功率,当前功率,可调功率
                BigDecimal curPower = pileRunData.getCurPower();
                BigDecimal reducePower = pileRunData.getUpPower();
                if (adjustP.compareTo(BigDecimal.ZERO) > 0) {
                    if (adjustP.compareTo(reducePower) >= 0) { //按照最大功率调整
                        curPower = pileRunData.getCurPower().add(reducePower);
                        adjustP = adjustP.subtract(reducePower);
                    } else { //按照当前功率+剩余功率进行调控
                        curPower = curPower.add(adjustP);
                        adjustP = new BigDecimal("0.0");
                    }
                    resultList.add(pilePowerCtrlVo(pileRunData.getPileCode(), pileRunData.getGunCode(), pileRunData.getRunMode(), curPower));
                    //把调控后的功率存到当前功率中
                    pileRunData.setCurPower(curPower);
                }
            }
        }
        return adjustP;
    }

    //充电下调功率
    private static BigDecimal pileChargeDownAdjustP(Integer priority, BigDecimal adjustP, BigDecimal wavePower, List<PileGunAdjustPDto> pileGunAdjustPList, List<PilePowerCtrlVo> resultList) {
        List<PileGunAdjustPDto> chargeDownPowerList = pileGunAdjustPList.stream().filter(p -> p.getRunMode() == 0
                && p.getDownPower().compareTo(wavePower) > 0).collect(Collectors.toList());
        switch (priority) {
            //优先级 1-SOC优先 2-顺序优先 3-综合
            //SOC优先: 1.直流桩SOC高优先 2.交流桩后到先调
            //顺序优先: 交直流桩统一遵循后到先调(按开始时间降序进行调)
            //综合: 1.交流桩后到先调 2.直流桩SOC高优先
            case 1:
                //直流桩调控 直流桩SOC低优先
                List<PileGunAdjustPDto> pileStartDCList = chargeDownPowerList.stream().filter(p -> p.getType() == 1).sorted(Comparator
                        .comparing(PileGunAdjustPDto::getBatterySOC).reversed()).collect(Collectors.toList());
                adjustP = chargePowerDown(pileStartDCList, adjustP, resultList);
                //交流桩后到先调 过滤掉运行数据里没有开始时间的
                List<PileGunAdjustPDto> pileEndACList = chargeDownPowerList.stream().filter(p -> p.getType() == 2 && StringUtil.isNotEmpty(p.getStartTime()))
                        .sorted(Comparator.comparing(PileGunAdjustPDto::getStartTime).reversed()).collect(Collectors.toList());
                adjustP = chargePowerDown(pileEndACList, adjustP, resultList);
                break;
            case 2:
                //交直流桩统一遵循后到先调(按开始时间降序进行调) 过滤掉运行数据里没有开始时间的
                List<PileGunAdjustPDto> pileACAndDCList = chargeDownPowerList.stream().filter(p -> StringUtil.isNotEmpty(p.getStartTime()))
                        .sorted(Comparator.comparing(PileGunAdjustPDto::getStartTime).reversed()).collect(Collectors.toList());
                adjustP = chargePowerDown(pileACAndDCList, adjustP, resultList);
                break;
            case 3:
                //交流桩后到先调 过滤掉运行数据里没有开始时间的
                List<PileGunAdjustPDto> pileStartACList = chargeDownPowerList.stream().filter(p -> p.getType() == 2 && StringUtil.isNotEmpty(p.getStartTime()))
                        .sorted(Comparator.comparing(PileGunAdjustPDto::getStartTime).reversed()).collect(Collectors.toList());
                adjustP = chargePowerDown(pileStartACList, adjustP, resultList);
                //直流桩SOC低优先 从低到高排序
                List<PileGunAdjustPDto> pileEndDCList = chargeDownPowerList.stream().filter(p -> p.getType() == 1).sorted(Comparator
                        .comparing(PileGunAdjustPDto::getBatterySOC).reversed()).collect(Collectors.toList());
                adjustP = chargePowerDown(pileEndDCList, adjustP, resultList);
                break;
        }
        return adjustP;
    }

    private static BigDecimal chargePowerDown(List<PileGunAdjustPDto> chargeDownList, BigDecimal adjustP, List<PilePowerCtrlVo> resultList) {
        if (CollectionUtils.isNotEmpty(chargeDownList) && adjustP.compareTo(BigDecimal.ZERO) < 0) {
            for (PileGunAdjustPDto pileRunData : chargeDownList) {
                //调控功率(负值),当前功率(正值),充电可下调功率(正值)
                BigDecimal curPower = pileRunData.getCurPower();
                BigDecimal downPower = pileRunData.getDownPower();
                if (adjustP.compareTo(BigDecimal.ZERO) < 0) {
                    if (adjustP.abs().compareTo(downPower) >= 0) { //按照最大功率调整
                        curPower = pileRunData.getCurPower().subtract(downPower);
                        adjustP = adjustP.add(downPower);
                    } else { //按照当前功率+剩余功率进行调控
                        curPower = curPower.add(adjustP);
                        adjustP = new BigDecimal("0.0");
                    }
                    resultList.add(pilePowerCtrlVo(pileRunData.getPileCode(), pileRunData.getGunCode(), pileRunData.getRunMode(), curPower));
                    //把调控后的功率存到当前功率中
                    pileRunData.setCurPower(curPower);
                }
            }
        }
        return adjustP;
    }

    //放电上调功率
    private static BigDecimal pileDischargeUpAdjustP(Integer priority, BigDecimal adjustP, BigDecimal wavePower, List<PileGunAdjustPDto> pileGunAdjustPList, List<PilePowerCtrlVo> resultList) {
        List<PileGunAdjustPDto> dischargeUpPowerList = pileGunAdjustPList.stream().filter(p -> p.getRunMode() == 1 &&
                p.getUpPower().compareTo(wavePower) > 0).collect(Collectors.toList());
        switch (priority) {
            //优先级 1-SOC优先 2-顺序优先 3-综合
            //SOC优先: 1.直流桩SOC高优先 2.交流桩先到先得
            //顺序优先: 交直流桩统一遵循先到先得(按开始时间进行调)
            //综合: 1.交流桩先到先得 2.直流桩SOC高优先
            case 1:
                //直流桩调控 SOC优先 从高到低排序
                List<PileGunAdjustPDto> pileStartDCList = dischargeUpPowerList.stream().filter(p -> p.getType() == 1).sorted(Comparator
                        .comparing(PileGunAdjustPDto::getBatterySOC).reversed()).collect(Collectors.toList());
                adjustP = dischargePowerUp(pileStartDCList, adjustP, resultList);
                //交流桩先到先得 过滤掉运行数据里没有开始时间的
                List<PileGunAdjustPDto> pileEndACList = dischargeUpPowerList.stream().filter(p -> p.getType() == 2 && StringUtil.isNotEmpty(p.getStartTime()))
                        .sorted(Comparator.comparing(PileGunAdjustPDto::getStartTime)).collect(Collectors.toList());
                adjustP = dischargePowerUp(pileEndACList, adjustP, resultList);
                break;
            case 2:
                //交直流桩先到先得 过滤掉运行数据里没有开始时间的
                List<PileGunAdjustPDto> pileACAndDCList = dischargeUpPowerList.stream().filter(p -> StringUtil.isNotEmpty(p.getStartTime()))
                        .sorted(Comparator.comparing(PileGunAdjustPDto::getStartTime)).collect(Collectors.toList());
                adjustP = dischargePowerUp(pileACAndDCList, adjustP, resultList);
                break;
            case 3:
                //交流桩先到先得 过滤掉运行数据里没有开始时间的
                List<PileGunAdjustPDto> pileStartACList = dischargeUpPowerList.stream().filter(p -> p.getType() == 2 && StringUtil.isNotEmpty(p.getStartTime()))
                        .sorted(Comparator.comparing(PileGunAdjustPDto::getStartTime)).collect(Collectors.toList());
                adjustP = dischargePowerUp(pileStartACList, adjustP, resultList);
                //直流桩调控 SOC优先 从高到低排序
                List<PileGunAdjustPDto> pileEndDCList = dischargeUpPowerList.stream().filter(p -> p.getType() == 1).sorted(Comparator
                        .comparing(PileGunAdjustPDto::getBatterySOC).reversed()).collect(Collectors.toList());
                adjustP = dischargePowerUp(pileEndDCList, adjustP, resultList);
                break;
        }
        return adjustP;
    }

    private static BigDecimal dischargePowerUp(List<PileGunAdjustPDto> chargeUpList, BigDecimal adjustP, List<PilePowerCtrlVo> resultList) {
        if (CollectionUtils.isNotEmpty(chargeUpList) && adjustP.compareTo(BigDecimal.ZERO) < 0) {
            for (PileGunAdjustPDto pileRunData : chargeUpList) {
                //调控功率(负值),当前功率(负值),放电可调功率(正值)
                BigDecimal curPower = pileRunData.getCurPower();
                BigDecimal reducePower = pileRunData.getUpPower();
                if (adjustP.compareTo(BigDecimal.ZERO) < 0) {
                    if (adjustP.abs().compareTo(reducePower) >= 0) { //按照最大功率调整
                        curPower = pileRunData.getCurPower().subtract(reducePower);
                        adjustP = adjustP.add(reducePower);
                    } else { //按照当前功率+剩余功率进行调控
                        curPower = curPower.add(adjustP);
                        adjustP = new BigDecimal("0.0");
                    }
                    resultList.add(pilePowerCtrlVo(pileRunData.getPileCode(), pileRunData.getGunCode(), pileRunData.getRunMode(), curPower));
                    //把调控后的功率存到当前功率中
                    pileRunData.setCurPower(curPower);
                }
            }
        }
        return adjustP;
    }

    //放电下调功率
    private static BigDecimal pileDischargeDownAdjustP(Integer priority, BigDecimal adjustP, BigDecimal wavePower, List<PileGunAdjustPDto> pileGunAdjustPList, List<PilePowerCtrlVo> resultList) {
        List<PileGunAdjustPDto> dischargeDownPowerList = pileGunAdjustPList.stream().filter(p -> p.getRunMode() == 1 &&
                p.getDownPower().compareTo(wavePower) > 0).collect(Collectors.toList());
        switch (priority) {
            //优先级 1-SOC优先 2-顺序优先 3-综合
            //SOC优先: 1.直流桩SOC低优先 2.交流桩后到先调
            //顺序优先: 交直流桩统一遵循后到先调(按开始时间降序进行调)
            //综合: 1.交流桩后到先调 2.直流桩SOC低优先
            case 1:
                //直流桩调控 直流桩SOC低优先
                List<PileGunAdjustPDto> pileStartDCList = dischargeDownPowerList.stream().filter(p -> p.getType() == 1).sorted(Comparator
                        .comparing(PileGunAdjustPDto::getBatterySOC)).collect(Collectors.toList());
                adjustP = dischargePowerDown(pileStartDCList, adjustP, resultList);
                //交流桩后到先调 过滤掉运行数据里没有开始时间的
                List<PileGunAdjustPDto> pileEndACList = dischargeDownPowerList.stream().filter(p -> p.getType() == 2 && StringUtil.isNotEmpty(p.getStartTime()))
                        .sorted(Comparator.comparing(PileGunAdjustPDto::getStartTime).reversed()).collect(Collectors.toList());
                adjustP = dischargePowerDown(pileEndACList, adjustP, resultList);
                break;
            case 2:
                //交直流桩统一遵循后到先调(按开始时间降序进行调) 过滤掉运行数据里没有开始时间的
                List<PileGunAdjustPDto> pileACAndDCList = dischargeDownPowerList.stream().filter(p -> StringUtil.isNotEmpty(p.getStartTime()))
                        .sorted(Comparator.comparing(PileGunAdjustPDto::getStartTime).reversed()).collect(Collectors.toList());
                adjustP = dischargePowerDown(pileACAndDCList, adjustP, resultList);
                break;
            case 3:
                //交流桩后到先调 过滤掉运行数据里没有开始时间的
                List<PileGunAdjustPDto> pileStartACList = dischargeDownPowerList.stream().filter(p -> p.getType() == 2 && StringUtil.isNotEmpty(p.getStartTime()))
                        .sorted(Comparator.comparing(PileGunAdjustPDto::getStartTime).reversed()).collect(Collectors.toList());
                adjustP = dischargePowerDown(pileStartACList, adjustP, resultList);
                //直流桩SOC低优先 从低到高排序
                List<PileGunAdjustPDto> pileEndDCList = dischargeDownPowerList.stream().filter(p -> p.getType() == 1).sorted(Comparator
                        .comparing(PileGunAdjustPDto::getBatterySOC)).collect(Collectors.toList());
                adjustP = dischargePowerDown(pileEndDCList, adjustP, resultList);
                break;
        }
        return adjustP;
    }

    private static BigDecimal dischargePowerDown(List<PileGunAdjustPDto> dischargeDownList, BigDecimal adjustP, List<PilePowerCtrlVo> pilePowerCtrlVos) {
        if (CollectionUtils.isNotEmpty(dischargeDownList) && adjustP.compareTo(BigDecimal.ZERO) > 0) {
            for (PileGunAdjustPDto pileRunData : dischargeDownList) {
                //调控功率(正值),当前功率(负值),放电可下调功率(正值)
                BigDecimal curPower = pileRunData.getCurPower();
                BigDecimal downPower = pileRunData.getDownPower();
                if (adjustP.compareTo(BigDecimal.ZERO) > 0) {
                    if (adjustP.compareTo(downPower) >= 0) { //按照最大功率调整
                        curPower = pileRunData.getCurPower().add(downPower);
                        adjustP = adjustP.subtract(downPower);
                    } else { //按照当前功率+剩余功率进行调控
                        curPower = curPower.add(adjustP);
                        adjustP = new BigDecimal("0.0");
                    }
                    pilePowerCtrlVos.add(pilePowerCtrlVo(pileRunData.getPileCode(), pileRunData.getGunCode(), pileRunData.getRunMode(), curPower));
                    //把调控后的功率存到当前功率中
                    pileRunData.setCurPower(curPower);
                }
            }
        }
        return adjustP;
    }

    //下发电桩功率控制命令
    private static PilePowerCtrlVo pilePowerCtrlVo(String pileCode, String gunCode, Integer runMode, BigDecimal outPower) {
        log.info("电桩功率控制命令参数：桩编号：{},枪编号：{},运行模式：{},调控功率：{}", pileCode, gunCode, runMode, Math.abs(outPower.doubleValue()));
        return PilePowerCtrlVo.builder().pileCode(pileCode).gunCode(gunCode).runMode(runMode).ctrlType(0).outPower(Math.abs(outPower.doubleValue())).build();
    }

}
