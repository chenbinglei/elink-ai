//package com.sunmax.crontab.util;
//
//import com.alibaba.fastjson.JSON;
//import com.alibaba.fastjson.TypeReference;
//import com.google.common.collect.Maps;
//import com.sunmax.common.config.redis.RedisGeneralUtil;
//import com.sunmax.common.dto.configure.PowerControlResDto;
//import com.sunmax.common.dto.protocol.PileResultDto;
//import com.sunmax.common.model.general.PileRealModel;
//import com.sunmax.common.util.DateUtil;
//import com.sunmax.common.util.SpringBeanUtil;
//import com.sunmax.common.util.StringUtil;
//import com.sunmax.common.vo.crontab.PileStartControlVo;
//import com.sunmax.common.vo.crontab.PowerControlParamVo;
//import com.sunmax.common.vo.crontab.StrategyTaskVo;
//import com.sunmax.common.vo.protocol.PilePowerCtrlVo;
//import com.sunmax.crontab.service.feign.ConfigService;
//import com.sunmax.crontab.service.feign.ProtocolService;
//import com.sunmax.crontab.vo.PileRunDataVo;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.collections.CollectionUtils;
//import org.apache.commons.compress.utils.Lists;
//import org.springframework.beans.BeanUtils;
//
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//import java.util.*;
//import java.util.concurrent.atomic.AtomicReference;
//import java.util.stream.Collectors;
//
///**
// * 平台策略计算工具类
// */
//@Slf4j
//public class StrategyCalculateUtil {
//
//    //电桩启动管理map key(电桩编号+枪编号) -> 开始时间
//    public static Map<String, PileStartControlVo> pileOccupyMap = Maps.newConcurrentMap();
//
//    private static ProtocolService protocolService;
//
//    private static ConfigService configService;
//
//    /**
//     * 晟曼平台自动策略任务
//     *
//     * @param taskVo 任务参数
//     * @param sourceType 来源类型 1-自建 2-城市充电接入
//     * @return 是否调控 true-需要 false-不需要
//     */
//    public static Boolean platformAutoControlTask(StrategyTaskVo taskVo, Integer sourceType) {
////        log.info("自动策略定时任务执行入参:{}", taskVo);
//
//        //功率控制策略计算
//        List<PilePowerCtrlVo> pilePowerCtrlVos = controlPowerCalculate(taskVo);
//        //对需要调的电桩功率进行调控
//        if (CollectionUtils.isNotEmpty(pilePowerCtrlVos)) {
//            log.info("平台自动策略需要调控的电桩功率控制参数:{}", pilePowerCtrlVos);
//            //晟曼平台接入
//            if (sourceType == 1) {
//                protocolService.batchPilePowerCtrl(pilePowerCtrlVos);
//            }
//            //城市充电接入
//            if (sourceType == 2) {
//                configService.interflowBatchPowerControl(pilePowerCtrlVos.stream().map(pilePowerCtrlVo -> {
//                    PowerControlParamVo pilePowerCtrl = new PowerControlParamVo();
//                    pilePowerCtrl.setPileCode(pilePowerCtrlVo.getPileCode());
//                    if (StringUtil.isNotEmpty(pilePowerCtrlVo.getGunCode())) {
//                        pilePowerCtrl.setGunCode(Integer.parseInt(pilePowerCtrlVo.getGunCode()));
//                    }
//                    pilePowerCtrl.setOutPower(pilePowerCtrlVo.getOutPower());
//                    return pilePowerCtrl;
//                }).collect(Collectors.toList()));
//            }
//            return true;
//        }
//        return false;
//    }
//
//    /**
//     * 功率控制策略计算
//     * @param taskVo 任务参数
//     * @return 电桩功率控制参数集合
//     */
//    public static List<PilePowerCtrlVo> controlPowerCalculate(StrategyTaskVo taskVo) {
//        //定义功率控制参数集合
//        List<PilePowerCtrlVo> resultList = Lists.newArrayList();
//
//        if (protocolService == null) {
//            protocolService = SpringBeanUtil.getBean(ProtocolService.class);
//        }
//        if (configService == null) {
//            configService = SpringBeanUtil.getBean(ConfigService.class);
//        }
//
//        //1.获取策略需要的参数
//        //停止日期
//        String filterDates = taskVo.getFilterDates();
//        //执行类型 1-全时段 2-工作日 3-周末 4-自定义时段
//        Integer executeType = taskVo.getExecuteType();
//        //执行时段 自定义时段{1:["00:00:00","01:00:00"],2:["00:00:00"]}
//        String executeTime = taskVo.getExecuteTime();
//        //策略使能 true-开启 false-关闭
//        Boolean enabled = taskVo.getEnabled();
//        //站点最大充电功率
//        Double maxChargeP = taskVo.getMaxP();
//        //功率允许波动值
//        Double wavePower = taskVo.getWavePower();
//        //优先级 1-SOC优先 2-顺序优先 3-综合
//        Integer priority = taskVo.getPriority();
//        //控制对象(被控制电桩数据 桩编号+枪编号 -> 控制电桩数据)
//        Map<String, StrategyTaskVo.StrategyPile> strategyPileMap = taskVo.getStrategyPileMap();
//        //当前日期(年月日)
//        String nowDateStr = DateUtil.localDateToStr(LocalDate.now());
//        //当前日期(年月日时分秒)
//        LocalDateTime nowDateTime = LocalDateTime.now();
//
//
//        //2.校验站点开关
//        if (!enabled) {
//            removeAllPileOccupyMap(strategyPileMap);
//            log.error("站点未开启策略使能,不执行策略");
//            return resultList;
//        }
//
//        //3.校验停止日期
//        if (StringUtil.isNotEmpty(filterDates)) {
//            //停止日期包含今天日期的话 不执行
//            if (filterDates.contains(nowDateStr)) {
//                removeAllPileOccupyMap(strategyPileMap);
//                log.error("站点停止日期包含今天日期,不执行策略");
//                return resultList;
//            }
//        }
//
//        //4.校验执行类型和执行时间
//        switch (executeType) {
////            case 1: //全时段
////                break;
//            case 2: //工作日
//                if (!DateUtil.isWorkDayOrWeek(nowDateStr, 1)) {
//                    removeAllPileOccupyMap(strategyPileMap);
//                    log.error("站点执行类型为工作日,今天不是工作日,不执行策略");
//                    return resultList;
//                }
//                break;
//            case 3: //周末
//                if (!DateUtil.isWorkDayOrWeek(nowDateStr, 2)) {
//                    removeAllPileOccupyMap(strategyPileMap);
//                    log.error("站点执行类型为周末,今天不是周末,不执行策略");
//                    return resultList;
//                }
//                break;
//            case 4: //自定义时段
//                if (StringUtil.isNotEmpty(executeTime)) {
//                    Map<Integer, String> executeTimeMap = JSON.parseObject(executeTime, new TypeReference<Map<Integer, String>>() {
//                    });
//                    //获取当天是周几
//                    int day = nowDateTime.getDayOfWeek().getValue();
//                    if (executeTimeMap.containsKey(day)) {
//                        Map<Integer, Integer> timeMap = JSON.parseArray(executeTimeMap.get(day), String.class).stream().map(s ->
//                                Integer.parseInt(s.substring(0, 2))).collect(Collectors.toMap(a -> a, a -> a, (k1, k2) -> k1));
//                        if (!timeMap.containsKey(nowDateTime.getHour())) {
//                            removeAllPileOccupyMap(strategyPileMap);
//                            log.error("站点执行类型为自定义时段,今天不是自定义时段,不执行策略");
//                            return resultList;
//                        }
//                    } else {
//                        removeAllPileOccupyMap(strategyPileMap);
//                        log.error("站点执行类型为自定义时段,今天没有自定义时段,不执行策略");
//                        return resultList;
//                    }
//                }
//                break;
//        }
//
//        //5.定义平台策略数据和调控参数
//        List<PileRunDataVo> pileRunDataList = Lists.newArrayList();
//        //5.获取平台策略任务调控参数(占用功率, 当前实时功率)
//        AtomicReference<Double> accOccupyPAtomic = new AtomicReference<>(0.0);
//        AtomicReference<Double> accCurrentPAtomic = new AtomicReference<>(0.0);
//        if (strategyPileMap != null && !strategyPileMap.isEmpty()) {
//            //获取电桩实时数据
//            Map<String, PileRealModel> pileRealMap = Maps.newConcurrentMap();
//            Set<String> pileCodes = strategyPileMap.values().stream().map(StrategyTaskVo.StrategyPile::getCid).filter(StringUtil::isNotEmpty)
//                    .collect(Collectors.toSet());
//            pileCodes.forEach(pileCode -> {
//                PileRealModel pileRealModel = RedisGeneralUtil.getPileRealModel(pileCode);
//                if (pileRealModel != null) {
//                    pileRealMap.put(pileCode, pileRealModel);
//                }
//            });
//
//            //电桩可调控制列表
//            List<PileRunDataVo> finalPileRunDataList = pileRunDataList;
//            strategyPileMap.forEach((key, strategyPile) -> {
//                //获取电桩(当前)实时功率
//                if (pileRealMap.containsKey(strategyPile.getCid())) {
//                    PileRealModel pileRealModel = pileRealMap.get(strategyPile.getCid());
//                    //获取电桩占用功率
//                    //1.电桩离线(资源占用至上线,最大占用时长判断)
//                    boolean pileStatus;
//                    if (StringUtil.isNotEmpty(pileRealModel.getWorkStatus()) && pileRealModel.getWorkStatus() == 88
//                            && StringUtil.isNotEmpty(pileRealModel.getOfflineTime())) {
//                        LocalDateTime getOfflineTime = DateUtil.strToLocalDateTime(pileRealModel.getOfflineTime());
//                        pileStatus = nowDateTime.isBefore(getOfflineTime.plusSeconds((long) (strategyPile.getOfflineDuration() * 3600)));
//                    } else {
//                        pileStatus = true;
//                    }
//                    if (pileOccupyMap.containsKey(key)) {
//                        if (pileStatus) {
//                            accOccupyPAtomic.set(accOccupyPAtomic.get() + strategyPile.getMaxP());
//                        } else { //当电桩占用离线超过设置时间时，直接删除
//                            pileOccupyMap.remove(key);
//                        }
//                    }
//                    String gid = String.valueOf(strategyPile.getGid());
//                    if (pileStatus && pileRealModel.getGunRealModelMap() != null && pileRealModel.getGunRealModelMap().containsKey(gid)) {
//                        PileRealModel.GunRealModel gunRealModel = pileRealModel.getGunRealModelMap().get(gid);
//                        if (gunRealModel.getGunStatus() == 1 || gunRealModel.getGunStatus() == 2 || gunRealModel.getGunStatus() == 4 || gunRealModel.getGunStatus() == 5) {
//                            //添加当前设备运行数据
//                            PileRunDataVo pileRunData = new PileRunDataVo();
//                            BeanUtils.copyProperties(strategyPile, pileRunData);
////                            log.info("电桩" + strategyPile.getCid() + "当前运行功率: " + power);
//                            pileRunData.setPileCode(strategyPile.getCid());
//                            if (StringUtil.isNotEmpty(strategyPile.getGid())) {
//                                pileRunData.setGunCode(String.valueOf(strategyPile.getGid()));
//                            }
//                            //获取电桩当前功率
//                            if (StringUtil.isNotEmpty(gunRealModel.getOutPower())) {
//                                accCurrentPAtomic.set(accCurrentPAtomic.get() + gunRealModel.getOutPower());
//                                pileRunData.setCurPower(gunRealModel.getOutPower());
//                            }
//                            //需求电压和电流为空时 默认最大功率为需求功率
//                            if (StringUtil.isNotEmpty(gunRealModel.getReqPower())) {
//                                pileRunData.setReqPower(gunRealModel.getReqPower());
//                            } else {
//                                pileRunData.setReqPower(strategyPile.getMaxP());
//                            }
//                            pileRunData.setStartTime(gunRealModel.getStartTime());
//                            if (StringUtil.isNotEmpty(gunRealModel.getBatterySoc())) {
//                                pileRunData.setBatterySOC(gunRealModel.getBatterySoc());
//                            }
//                            pileRunData.setRunMode(gunRealModel.getRunMode());
//                            //可以调控的桩存进去
//                            if (pileRunData.getControlType() == 1 && gunRealModel.getRunMode() != -1) {
//                                finalPileRunDataList.add(pileRunData);
//                            }
//                        }
//                    }
//                }
//            });
//        }
//
//        //当前功率小于最大功率
//        Double currentSumPower = accCurrentPAtomic.get();
//        Double occupySumPower = accOccupyPAtomic.get();
////        log.info("最大功率: " + maxChargeP + ", 当前功率: " + currentSumPower + ", 当前占用功率: " + occupySumPower + ", 波动值: " + wavePower);
//        //左边 往上调功率
//        if ((currentSumPower + occupySumPower) < maxChargeP) {
//            Double freeSumPower = maxChargeP - occupySumPower - currentSumPower;
//            if (freeSumPower > wavePower) {
////                log.info("左侧可上调功率: " + freeSumPower);
//                //获取整站剩余功率
//                if (freeSumPower > 0.0) {
//                    //获取当前运行可调控电桩数据未满功率的桩
//                    //校验当前运行可调控电桩数据是否达到满功率
//                    pileRunDataList = pileRunDataList.stream().filter(pileRunData -> {
//                        //需求功率取最小的功率去计算
//                        double maxPower = pileRunData.getMaxP() >= pileRunData.getReqPower() ? pileRunData.getReqPower() : pileRunData.getMaxP();
//                        return (pileRunData.getCurPower() + wavePower) < maxPower;
//                    }).collect(Collectors.toList());
//                    //对未满功率运行的桩进行调控
//                    if (CollectionUtils.isNotEmpty(pileRunDataList)) {
//                        //优先级 1-SOC优先 2-顺序优先 3-综合
//                        //SOC优先: 1.直流桩SOC低优先 2.交流桩先到先得
//                        //顺序优先: 交直流桩统一遵循先到先得(按开始时间进行调)
//                        //综合: 1.交流桩先到先得 2.直流桩SOC低优先
//                        switch (priority) {
//                            case 1:
//                                //直流桩调控 SOC优先 从低到高排序
//                                pileDCPowerUpSocSort(pileRunDataList, freeSumPower, resultList);
//                                //交流桩先到先得 过滤掉运行数据里没有开始时间的
//                                pileACPowerUpStartTimeSort(pileRunDataList, freeSumPower, resultList);
//                                break;
//                            case 2:
//                                //交直流桩先到先得 过滤掉运行数据里没有开始时间的
//                                pileACAndDCPowerUpStartTimeSort(pileRunDataList, freeSumPower, resultList);
//                                break;
//                            case 3:
//                                //交流桩先到先得 过滤掉运行数据里没有开始时间的
//                                pileACPowerUpStartTimeSort(pileRunDataList, freeSumPower, resultList);
//                                //直流桩调控 SOC优先 从低到高排序
//                                pileDCPowerUpSocSort(pileRunDataList, freeSumPower, resultList);
//                                break;
//                        }
//                    }
//                }
//            } else {
//                //均调
//                occupyPower(occupySumPower, maxChargeP, currentSumPower, wavePower, priority, pileRunDataList, resultList);
//            }
//        } else {
//            if (currentSumPower + occupySumPower - maxChargeP > wavePower) { //右边 往下调功率
//
//                //校验当前运行可调控电桩数据是否是最小功率 最小功率则不调控 否则则调控
//                pileRunDataList = pileRunDataList.stream().filter(pileRunData -> {
//                    //最低功率去最小的功率去计算
//                    return pileRunData.getCurPower() > pileRunData.getMinP();
//                }).collect(Collectors.toList());
//                //需要下调的功率
//                Double reducePower = currentSumPower + occupySumPower - maxChargeP;
////                log.info("右侧可下调功率: " + reducePower);
//                switch (priority) {
//                    //优先级 1-SOC优先 2-顺序优先 3-综合
//                    //SOC优先: 1.直流桩SOC高优先 2.交流桩后到后调
//                    //顺序优先: 交直流桩统一遵循后到后调(按开始时间降序进行调)
//                    //综合: 1.交流桩后到后调 2.直流桩SOC高优先
//                    case 1:
//                        //1.直流桩SOC高优先 2.交流桩后到先调
//                        //直流桩调控 SOC优先 从高到低排序
//                        pileDCPowerDownSocSort(pileRunDataList, reducePower, resultList);
//                        //交流桩后到先调 过滤掉运行数据里没有开始时间的
//                        pileACPowerDownStartTimeSort(pileRunDataList, reducePower, resultList);
//                        break;
//                    case 2:
//                        //交直流桩统一遵循后到后调(按开始时间降序进行调)
//                        pileACAndDCPowerDownStartTimeSort(pileRunDataList, reducePower, resultList);
//                        break;
//                    case 3:
//                        //1.交流桩后到先调 2.直流桩SOC高优先
//                        //交流桩后到先调 过滤掉运行数据里没有开始时间的
//                        pileACPowerDownStartTimeSort(pileRunDataList, reducePower, resultList);
//                        //直流桩调控 SOC优先 从高到低排序
//                        pileDCPowerDownSocSort(pileRunDataList, reducePower, resultList);
//                        break;
//                }
//            } else {
//                //均调
//                occupyPower(occupySumPower, maxChargeP, currentSumPower, wavePower, priority, pileRunDataList, resultList);
//            }
//        }
//        removeAllPileOccupyMap(strategyPileMap);
//        return resultList;
//    }
//
//    public static void occupyPower(Double occupySumPower, Double maxChargeP, Double currentSumPower, Double wavePower, Integer priority, List<PileRunDataVo> pileRunDataList, List<PilePowerCtrlVo> pilePowerCtrlVos) {
//        if (occupySumPower <= 0.0) {
//            //获取当前运行可调控电桩数据未满功率的桩
//            //校验当前运行可调控电桩数据是否达到满功率
//            List<PileRunDataVo> underPowerList = pileRunDataList.stream().filter(pileRunData -> {
//                //需求功率取最小的功率去计算
//                double maxPower = pileRunData.getMaxP() >= pileRunData.getReqPower() ? pileRunData.getReqPower() : pileRunData.getMaxP();
//                return (pileRunData.getCurPower() + wavePower) < maxPower;
//            }).collect(Collectors.toList());
//            if (CollectionUtils.isNotEmpty(underPowerList) || (currentSumPower - wavePower) > maxChargeP) {
//                //均调功率
//                //获取所有电桩可下调的功率(所有电桩当前功率 - 最小功率)
//                double downSumPower = pileRunDataList.stream().mapToDouble(d -> d.getCurPower() - d.getMinP() >= d.getReqPower() ? d.getReqPower() : d.getMinP()).sum();
//                //获取所有电桩上调功率(最大充电功率 - 当前充电功率)
//                double upSumPower = maxChargeP - currentSumPower;
////                log.info("平台自动策略,充电中可上调功率:{},充电中可下调功率: {}", upSumPower, downSumPower);
//                //优先级 1-SOC优先 2-顺序优先 3-综合
//                switch (priority) {
//                    case 1:
//                        pileRunControlSoc(pileRunDataList, downSumPower, upSumPower, pilePowerCtrlVos);
//                        break;
//                    case 2:
//                        pileRunControlSort(pileRunDataList, downSumPower, upSumPower, wavePower, pilePowerCtrlVos);
//                        break;
//                    case 3:
//                        pileRunControlSynthesis(pileRunDataList, downSumPower, upSumPower, pilePowerCtrlVos);
//                        break;
//                }
//            }
//        }
//    }
//
//    //直流桩调控 SOC优先 从低到高排序
//    public static void pileDCPowerUpSocSort(List<PileRunDataVo> pileRunDataList, Double freeSumPower, List<PilePowerCtrlVo> pilePowerCtrlVos) {
//        List<PileRunDataVo> pileDCList = pileRunDataList.stream().filter(p -> p.getType() == 1).sorted(Comparator
//                .comparing(PileRunDataVo::getBatterySOC)).collect(Collectors.toList());
//
//        controlPowerUp(pileDCList, freeSumPower, pilePowerCtrlVos);
//    }
//
//    //交流桩先到先得 顺序优先 按起始时间调控
//    public static void pileACPowerUpStartTimeSort(List<PileRunDataVo> pileRunDataList, Double freeSumPower, List<PilePowerCtrlVo> pilePowerCtrlVos) {
//        //交流桩先到先得 过滤掉运行数据里没有开始时间的
//        List<PileRunDataVo> pileACList = pileRunDataList.stream().filter(p -> p.getType() == 2 && StringUtil.isNotEmpty(p.getStartTime()))
//                .sorted(Comparator.comparing(PileRunDataVo::getStartTime)).collect(Collectors.toList());
//
//        controlPowerUp(pileACList, freeSumPower, pilePowerCtrlVos);
//    }
//
//    //交直流桩先到先得 顺序优先 按起始时间调控
//    public static void pileACAndDCPowerUpStartTimeSort(List<PileRunDataVo> pileRunDataList, Double freeSumPower, List<PilePowerCtrlVo> pilePowerCtrlVos) {
//        //交直流桩先到先得 过滤掉运行数据里没有开始时间的
//        List<PileRunDataVo> pileACAndDcList = pileRunDataList.stream().filter(p -> StringUtil.isNotEmpty(p.getStartTime()))
//                .sorted(Comparator.comparing(PileRunDataVo::getStartTime)).collect(Collectors.toList());
//
//        controlPowerUp(pileACAndDcList, freeSumPower, pilePowerCtrlVos);
//    }
//
//    //直流桩按SOC依次下调
//    public static void pileDCPowerDownSocSort(List<PileRunDataVo> pileRunDataList, Double reduceSumPower, List<PilePowerCtrlVo> pilePowerCtrlVos) {
//        //直流桩按照SOC降序调
//        List<PileRunDataVo> pileACList = pileRunDataList.stream().filter(p -> p.getType() == 1 && StringUtil.isNotEmpty(p.getBatterySOC()))
//                .sorted(Comparator.comparing(PileRunDataVo::getBatterySOC).reversed()).collect(Collectors.toList());
//
//        controlPowerDown(pileACList, reduceSumPower, pilePowerCtrlVos);
//    }
//
//    //交流桩后到后得 顺序优先 按起始时间降序调控
//    public static void pileACPowerDownStartTimeSort(List<PileRunDataVo> pileRunDataList, Double reduceSumPower, List<PilePowerCtrlVo> pilePowerCtrlVos) {
//        //交流桩先到先得 过滤掉运行数据里没有开始时间的
//        List<PileRunDataVo> pileACList = pileRunDataList.stream().filter(p -> p.getType() == 2 && StringUtil.isNotEmpty(p.getStartTime()))
//                .sorted(Comparator.comparing(PileRunDataVo::getStartTime).reversed()).collect(Collectors.toList());
//
//        controlPowerDown(pileACList, reduceSumPower, pilePowerCtrlVos);
//    }
//
//    //交流桩后到后得 顺序优先 按起始时间降序调控
//    public static void pileACAndDCPowerDownStartTimeSort(List<PileRunDataVo> pileRunDataList, Double reduceSumPower, List<PilePowerCtrlVo> pilePowerCtrlVos) {
//        //交流桩先到先得 过滤掉运行数据里没有开始时间的
//        List<PileRunDataVo> pileACAndDCList = pileRunDataList.stream().filter(p -> StringUtil.isNotEmpty(p.getStartTime()))
//                .sorted(Comparator.comparing(PileRunDataVo::getStartTime).reversed()).collect(Collectors.toList());
//
//        controlPowerDown(pileACAndDCList, reduceSumPower, pilePowerCtrlVos);
//    }
//
//    //上调功率代码封装
//    private static void controlPowerUp(List<PileRunDataVo> pileUpList, Double upPower, List<PilePowerCtrlVo> pilePowerCtrlVos) {
//        if (CollectionUtils.isNotEmpty(pileUpList) && upPower > 0.0) {
//            for (PileRunDataVo pileRunData : pileUpList) {
//                //获取最大功率,当前功率,可调功率
//                double maxPower = pileRunData.getMaxP() >= pileRunData.getReqPower() ? pileRunData.getReqPower() : pileRunData.getMaxP();
//                Double curPower = pileRunData.getCurPower();
//                double reducePower = maxPower - curPower;
//                if (upPower > 0.0) {
//                    if (upPower >= reducePower) { //按照最大功率调整
//                        curPower = maxPower;
//                        upPower = upPower - reducePower;
//                    } else { //按照当前功率+剩余功率进行调控
//                        curPower = curPower + upPower;
//                        upPower = 0.0;
//                    }
//                    pilePowerCtrlVos.add(pilePowerCtrlVo(pileRunData.getPileCode(), pileRunData.getGunCode(), pileRunData.getRunMode(), curPower));
//                    //把调控后的功率存到当前功率中
//                    pileRunData.setCurPower(curPower);
//                }
//            }
//        }
//    }
//
//    //下调功率代码封装
//    private static void controlPowerDown(List<PileRunDataVo> pileDownList, Double downPower, List<PilePowerCtrlVo> pilePowerCtrlVos) {
//        if (CollectionUtils.isNotEmpty(pileDownList) && downPower > 0.0) {
//            for (PileRunDataVo pileRunData : pileDownList) {
//                //获取最小功率,当前功率,可调功率
//                double minPower = pileRunData.getMinP() >= pileRunData.getReqPower() ? pileRunData.getReqPower() : pileRunData.getMinP();
//                Double curPower = pileRunData.getCurPower();
//                double reducePower = curPower - minPower;
//                if (downPower > 0.0) {
//                    //当前桩可调功率大于站需要调功率时 按照站需要调的功率进行下调
//                    if (reducePower >= downPower) {
//                        curPower = curPower - downPower;
//                        downPower = 0.0;
//                    } else {
//                        curPower = minPower;
//                        downPower = downPower - reducePower;
//                    }
//                    pilePowerCtrlVos.add(pilePowerCtrlVo(pileRunData.getPileCode(), pileRunData.getGunCode(), pileRunData.getRunMode(), curPower));
//                    //把调控后的功率存到当前功率中
//                    pileRunData.setCurPower(curPower);
//                }
//            }
//        }
//    }
//
//    //电桩运行中是否可调控(soc优先)
//    private static void pileRunControlSoc(List<PileRunDataVo> pileRunDataList, Double downSumPower, Double upSumPower, List<PilePowerCtrlVo> pilePowerCtrlVos) {
//        boolean isControl = false;
//        //直流桩调控 SOC优先 从低到高排序
//        List<PileRunDataVo> pileDCList = pileRunDataList.stream().filter(p -> p.getType() == 1).sorted(Comparator
//                .comparing(PileRunDataVo::getBatterySOC)).collect(Collectors.toList());
//        Double dcPower = 0.0;
//        for (PileRunDataVo pileRunData : pileDCList) {
//            if (dcPower > 0.0 && pileRunData.getCurPower() > dcPower) {
//                isControl = true;
//                break;
//            }
//            dcPower = pileRunData.getCurPower();
//        }
//        //交流桩先到先得 过滤掉运行数据里没有开始时间的
//        List<PileRunDataVo> pileACList = pileRunDataList.stream().filter(p -> p.getType() == 2 && StringUtil.isNotEmpty(p.getStartTime()))
//                .sorted(Comparator.comparing(PileRunDataVo::getStartTime).reversed()).collect(Collectors.toList());
//        Double acPower = 0.0;
//        for (PileRunDataVo pileRunData : pileACList) {
//            if (isControl) {
//                break;
//            }
//            if (acPower > 0.0 && pileRunData.getCurPower() > acPower) {
//                isControl = true;
//                break;
//            }
//            acPower = pileRunData.getCurPower();
//        }
////        log.info("SOC优先是否可调状态: " + isControl);
//        if (isControl) {
//            //下调功率
//            //1.直流桩SOC高优先 2.交流桩后到先调
//            //直流桩调控 SOC优先 从高到低排序
//            pileDCPowerDownSocSort(pileRunDataList, downSumPower, pilePowerCtrlVos);
//            //交流桩后到后调 过滤掉运行数据里没有开始时间的
//            pileACPowerDownStartTimeSort(pileRunDataList, downSumPower, pilePowerCtrlVos);
//
//            //上调功率
//            //直流桩调控 SOC优先 从低到高排序
//            pileDCPowerUpSocSort(pileRunDataList, upSumPower, pilePowerCtrlVos);
//            //交流桩先到先得 过滤掉运行数据里没有开始时间的
//            pileACPowerUpStartTimeSort(pileRunDataList, upSumPower, pilePowerCtrlVos);
//        }
//    }
//
//    //电桩运行中是否可调控(顺序优先)
//    private static void pileRunControlSort(List<PileRunDataVo> pileRunDataList, Double downSumPower, Double upSumPower, Double wavePower, List<PilePowerCtrlVo> pilePowerCtrlVos) {
//        boolean isControl = false;
//        //交直流桩先到先得 过滤掉运行数据里没有开始时间的
//        List<PileRunDataVo> pileACAndDcList = pileRunDataList.stream().filter(p -> {
//            //需求功率取最小的功率去计算
//            double maxPower = p.getMaxP() >= p.getReqPower() ? p.getReqPower() : p.getMaxP();
//            return StringUtil.isNotEmpty(p.getStartTime()) && Objects.equals(p.getType(), 1) && (p.getCurPower() + wavePower) < maxPower;
//        }).sorted(Comparator.comparing(PileRunDataVo::getStartTime)).collect(Collectors.toList());
//        Double acAndDcPower = 0.0;
//        for (PileRunDataVo pileRunData : pileACAndDcList) {
//            if (acAndDcPower > 0.0 && pileRunData.getCurPower() > acAndDcPower) {
//                isControl = true;
//                break;
//            }
//            acAndDcPower = pileRunData.getCurPower();
//        }
//        if (isControl) {
//            //下调功率
//            //交直流桩先到先得 过滤掉运行数据里没有开始时间的
//            pileACAndDCPowerDownStartTimeSort(pileRunDataList, downSumPower, pilePowerCtrlVos);
//            //上调功率
//            //交直流桩后到先调 过滤掉运行数据里没有开始时间的
//            pileACAndDCPowerUpStartTimeSort(pileRunDataList, upSumPower, pilePowerCtrlVos);
//        }
//    }
//
//    //电桩运行中是否可调控(综合)
//    private static void pileRunControlSynthesis(List<PileRunDataVo> pileRunDataList, Double downSumPower, Double upSumPower, List<PilePowerCtrlVo> pilePowerCtrlVos) {
//        boolean isControl = false;
//        //交流桩后到先调 过滤掉运行数据里没有开始时间的
//        List<PileRunDataVo> pileACList = pileRunDataList.stream().filter(p -> p.getType() == 2 && StringUtil.isNotEmpty(p.getStartTime()))
//                .sorted(Comparator.comparing(PileRunDataVo::getStartTime).reversed()).collect(Collectors.toList());
//        Double acPower = 0.0;
//        for (PileRunDataVo pileRunData : pileACList) {
//            if (acPower > 0.0 && pileRunData.getCurPower() > acPower) {
//                isControl = true;
//                break;
//            }
//            acPower = pileRunData.getCurPower();
//        }
//        //直流桩调控 SOC优先 从高到低排序
//        List<PileRunDataVo> pileDCList = pileRunDataList.stream().filter(p -> p.getType() == 1).sorted(Comparator
//                .comparing(PileRunDataVo::getBatterySOC).reversed()).collect(Collectors.toList());
//        Double dcPower = 0.0;
//        for (PileRunDataVo pileRunData : pileDCList) {
//            if (isControl) {
//                break;
//            }
//            if (dcPower > 0.0 && pileRunData.getCurPower() > dcPower) {
//                isControl = true;
//                break;
//            }
//            dcPower = pileRunData.getCurPower();
//        }
//        if (isControl) {
//            //1.交流桩后到先调 2.直流桩SOC高优先
//            //交流桩后到先调 过滤掉运行数据里没有开始时间的
//            pileACPowerDownStartTimeSort(pileRunDataList, downSumPower, pilePowerCtrlVos);
//            //直流桩调控 SOC优先 从高到低排序
//            pileDCPowerDownSocSort(pileRunDataList, downSumPower, pilePowerCtrlVos);
//
//            //上调功率
//            //交流桩先到先得 过滤掉运行数据里没有开始时间的
//            pileACPowerUpStartTimeSort(pileRunDataList, upSumPower, pilePowerCtrlVos);
//            //直流桩调控 SOC优先 从低到高排序
//            pileDCPowerUpSocSort(pileRunDataList, upSumPower, pilePowerCtrlVos);
//        }
//    }
//
//    //下发电桩功率控制命令
//    private static PilePowerCtrlVo pilePowerCtrlVo(String pileCode, String gunCode, Integer runMode, Double outPower) {
//        log.info("电桩功率控制命令参数：桩编号：{},枪编号：{},运行模式：{},调控功率：{}", pileCode, gunCode, runMode, outPower);
//        return PilePowerCtrlVo.builder().pileCode(pileCode).gunCode(gunCode).runMode(runMode).ctrlType(0).outPower(outPower).build();
//    }
//
//
//    //批量删除工具类
//    private static void removeAllPileOccupyMap(Map<String, StrategyTaskVo.StrategyPile> strategyPileMap) {
//        pileOccupyMap.forEach((key, value) -> {
//            if (strategyPileMap.containsKey(key)) {
//                pileOccupyMap.remove(key);
//            }
//        });
//    }
//
//}
