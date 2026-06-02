package com.sunmax.together.task;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.google.common.collect.Maps;
import com.sunmax.common.config.redis.RedisGeneralUtil;
import com.sunmax.common.dto.protocol.PileResultDto;
import com.sunmax.common.dto.system.UserDto;
import com.sunmax.common.model.general.PileRealModel;
import com.sunmax.common.util.DateUtil;
import com.sunmax.common.util.StringUtil;
import com.sunmax.common.vo.OrderSerialParamVo;
import com.sunmax.common.vo.PlatformLogoVo;
import com.sunmax.common.vo.crontab.StrategyTaskVo;
import com.sunmax.common.vo.protocol.PileStartVo;
import com.sunmax.common.vo.protocol.PileStopVo;
import com.sunmax.common.vo.protocol.mqtt.web.response.EventRateReqPublicVo;
import com.sunmax.common.vo.together.OrderChangeVo;
import com.sunmax.together.service.operation.SiteInfoService;
import com.sunmax.together.service.energy.StrategyService;
import com.sunmax.together.service.WebAppFeignService;
import com.sunmax.together.service.feign.ProtocolService;
import com.sunmax.together.service.feign.SystemService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.compress.utils.Lists;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Configuration
@Slf4j
public class StrategyRunTask {

    @Autowired
    private StrategyService strategyService;

    @Autowired
    private ProtocolService protocolService;

    @Autowired
    private SystemService systemService;

    @Autowired
    private WebAppFeignService webAppFeignService;

    @Autowired
    private SiteInfoService siteInfoService;

    //电桩运行状态Map
    public static Map<String, Integer> pileStatusMap = Maps.newHashMap();

    //电桩启动次数Map
    public static Map<String, Integer> pileStartNumMap = Maps.newHashMap();

    //电桩停止次数Map
    public static Map<String, Integer> pileStopNumMap = Maps.newHashMap();

    //电桩启动方式map
    public static Map<String, Integer> pileTypeMap = Maps.newHashMap();

    /**
     * 每30秒推送电桩数据
     */
    @Scheduled(fixedRate = 30000)
    public void strategyRun() {
//        log.info("平台自动策略运行时间: " + LocalDateTime.now());
//        log.info("电桩启动次数map:{}", pileStartNumMap);
//        log.info("电桩停止次数Map:{}", pileStopNumMap);
//        log.info("电桩状态Map:{}", pileStatusMap);
//        log.info("电桩缓存启动方式Map:{}", pileTypeMap);
        List<StrategyTaskVo> strategyTaskList = strategyService.findStrategyHandTaskList(null).getData();
        if (CollectionUtils.isEmpty(strategyTaskList)) {
            return;
        }
        //当前日期(年月日时分秒)
        LocalDateTime nowDateTime = LocalDateTime.now();
        //当前日期(年月日)
        String nowDateStr = DateUtil.localDateToStr(nowDateTime.toLocalDate());
        //当前时间(时分秒)
        LocalTime nowTime = nowDateTime.toLocalTime();

        //根据多个修改人id查询用户手机号
        List<String> userIds= strategyTaskList.stream().map(StrategyTaskVo::getUpdateId).filter(StringUtil::isNotEmpty)
                .distinct().collect(Collectors.toList());
        Map<String, UserDto> userMap = systemService.findUserInfoByIdsFeign(userIds).getData();

        //根据多个电桩编号查询费率id
        List<String> pileCodes = strategyTaskList.stream().flatMap(s -> s.getStrategyPileMap().values().stream()
                .map(StrategyTaskVo.StrategyPile::getCid).filter(StringUtil::isNotEmpty)).distinct().collect(Collectors.toList());
        Map<String, EventRateReqPublicVo> pileRateMap = siteInfoService.findSiteRateInfoByPileCodes(pileCodes).getData();

        for (StrategyTaskVo taskVo : strategyTaskList) {
            //1.获取策略需要的参数
            //停止日期
            String filterDates = taskVo.getFilterDates();
            //执行类型 1-全时段 2-工作日 3-周末 4-自定义时段
            Integer executeType = taskVo.getExecuteType();
            //执行时段 自定义时段{1:["00:00:00","01:00:00"],2:["00:00:00"]}
            String executeTime = taskVo.getExecuteTime();
            //修改人id
            String updateId = taskVo.getUpdateId();
            //策略使能 true-开启 false-关闭
            Boolean enabled = taskVo.getEnabled();
            //控制对象(被控制电桩数据 桩编号+枪编号 -> 控制电桩数据)
            Map<String, StrategyTaskVo.StrategyPile> strategyPileMap = taskVo.getStrategyPileMap();
            //调控时段数组列表
            List<StrategyTaskVo.StrategyTime> strategyTimeList = taskVo.getStrategyTimeList();

            //2.校验站点开关
            if (!enabled) {
                continue;
            }

            //3.校验停止日期
            if (StringUtil.isNotEmpty(filterDates)) {
                //停止日期包含今天日期的话 不执行
                if (filterDates.contains(nowDateStr)) {
                    continue;
                }
            }

            //4.校验执行类型和执行时间
            switch (executeType) {
//            case 1: //全时段
//                break;
                case 2: //工作日
                    if (!DateUtil.isWorkDayOrWeek(nowDateStr, 1)) {
                        continue;
                    }
                    break;
                case 3: //周末
                    if (!DateUtil.isWorkDayOrWeek(nowDateStr, 2)) {
                        continue;
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
                                continue;
                            }
                        } else {
                            continue;
                        }
                    }
                    break;
            }
            if (CollectionUtils.isNotEmpty(strategyTimeList)) {
                //根据修改人id查询手机号
                String phone;
                UserDto user = userMap.get(updateId);
                if (user != null && StringUtil.isNotEmpty(user.getPhone())) {
                    phone = user.getPhone();
                } else {
                    phone = null;
                }
                //第一步：判断电桩动作(1-充电 2-放电 3-静置)
                //第二步：判断当前时间是否在策略时间段内
                //第三步：更新是否启动标识,重复次数
                //第四步：根据启动标识获取电桩控制对象
                //第五步：检查电桩是否插枪
                //第六步：启动充电桩,增加重复次数
                //第七步： -》启动成功,检查电桩枪工作状态,重复次数清空
                //       -》启动失败,根据重复次数重新启动充电桩,重复次数增加
                int isRun = 0; //是否操作 0-无 1-启动 2-停止
                int type = 0; //类型 0-不操作 1-充电 2-放电
                int repeatNum = 1;
                strategyTimeList = strategyTimeList.stream().sorted(Comparator.comparing(StrategyTaskVo.StrategyTime::getBeginTime)).collect(Collectors.toList());
                for (int i = 0; i < strategyTimeList.size(); i++) {
                    StrategyTaskVo.StrategyTime strategyTime = strategyTimeList.get(i);
                    LocalTime beginTime = DateUtil.strToLocalTime(strategyTime.getBeginTime());
                    LocalTime endTime = DateUtil.strToLocalTime(strategyTime.getEndTime());
                    if (strategyTime.getType() == 3) { //电桩动作 静置
                        continue;
                    }
                    if (beginTime.equals(endTime)) {
                        continue;
                    }
                    type = strategyTime.getType();
                    repeatNum = repeatNum + strategyTime.getRepeatNum();
                    if (!nowTime.isBefore(beginTime) && nowTime.isBefore(endTime)) { //当前时间在策略时间段内 校验是否启动
                        isRun = 1; //启动
                        break;
                    } else if (!nowTime.isBefore(endTime) && nowTime.isBefore(i + 1 >= strategyTimeList.size() ?
                            LocalTime.MAX : DateUtil.strToLocalTime(strategyTimeList.get(i + 1).getBeginTime()).plusSeconds(1))) { //当前时间在结束时间之后 下次任务执行时间之前 停止
                        isRun = 2; // 停止
                        break;
                    }
                }

                if (isRun == 0) {
                    continue;
                }
                if (MapUtils.isNotEmpty(strategyPileMap)) {
                    List<StrategyTaskVo.StrategyPile> strategyPileList = new ArrayList<>(strategyPileMap.values());
                    int num = 0;
                    while (num < repeatNum) {
                        //启动充电桩
                        if (isRun == 1) {
                            //定义电桩启动相关参数
                            List<PileStartVo> pileStartVos = Lists.newArrayList();
                            for (StrategyTaskVo.StrategyPile strategyPile : strategyPileList) {
                                String rateId = null;
                                if (MapUtils.isNotEmpty(pileRateMap) && pileRateMap.containsKey(strategyPile.getCid())) {
                                    EventRateReqPublicVo eventRateReq = pileRateMap.get(strategyPile.getCid());
                                    if (type == 1 && StringUtil.isNotEmpty(eventRateReq.getCRateId())) {
                                        rateId = eventRateReq.getCRateId();
                                    }
                                    if (type == 2 && StringUtil.isNotEmpty(eventRateReq.getDRateId())) {
                                        rateId = eventRateReq.getDRateId();
                                    }
                                }
                                if (StringUtil.isEmpty(taskVo.getSiteId())) {
                                    log.info("未找到对应的充电站编号,电桩编号:{}", strategyPile.getCid());
                                    continue;
                                }
                                if (StringUtil.isEmpty(rateId)) {
                                    log.info("未找到对应的桩费率ID,桩编号:{}", strategyPile.getCid());
                                    continue;
                                }
                                PileStartVo pileStartVo = this.getPileStartVo(taskVo.getSiteId(), strategyPile.getCid(), String.valueOf(strategyPile.getGid()),
                                        phone, repeatNum, type, rateId);
                                if (pileStartVo != null) {
                                    pileStartVos.add(pileStartVo);
                                }
                            }
                            if (CollectionUtils.isEmpty(pileStartVos)) {
                                break; //电桩参数为空 则不需要启动充电桩
                            }
                            //电桩启动参数不为空 则有需要启动的桩
                            //下发启动充电命令
                            log.info("下发启动的电桩数据:{}", pileStartVos);
                            List<PileResultDto> pileResultList = protocolService.batchPileStart(pileStartVos).getData();
                            log.info("启动结果响应:{}", pileResultList);
                            if (CollectionUtils.isEmpty(pileResultList)) {
                                num = num + 1;
                                continue;
                            }
                            strategyPileList = Lists.newArrayList();
                            for (PileResultDto result : pileResultList) {
                                String key = result.getPileCode() + result.getGunCode();
                                if (result.getResult() == 0) { //启动成功 这次启动流程结束
                                    pileTypeMap.put(key, isRun);
                                }
                                if (result.getResult() == 1 && strategyPileMap.containsKey(key)) { //启动失败 重试启动
                                    strategyPileList.add(strategyPileMap.get(key));
                                }
                            }
                            //需要启动的电桩为空时 则不需要启动
                            if (CollectionUtils.isEmpty(strategyPileList)) {
                                break;
                            }
                            num = num + 1;

                        } else {
                            //停止充电桩
                            List<PileStopVo> pileStopVos = Lists.newArrayList();
                            for (StrategyTaskVo.StrategyPile value : strategyPileList) {
                                PileStopVo pileStopVo = this.getPileStopVo(value.getCid(), String.valueOf(value.getGid()), repeatNum);
                                String key = value.getCid() + value.getGid();
                                if (pileStopVo != null && pileTypeMap.containsKey(key)) {
                                    pileStopVos.add(pileStopVo);
                                }
                            }
                            if (CollectionUtils.isEmpty(pileStopVos)) {
                                break; //停止参数不为空时则结束
                            }
                            //需要停止的桩下发停止充电命令
                            log.info("下发停止命令的电桩数据:{}", pileStopVos);
                            List<PileResultDto> pileResultList = protocolService.batchPileStop(pileStopVos).getData();
                            log.info("停止结果响应:{}", pileResultList);
                            if (CollectionUtils.isEmpty(pileResultList)) {
                                num = num + 1;
                                continue;
                            }
                            strategyPileList = Lists.newArrayList();
                            for (PileResultDto result : pileResultList) {
                                String key = result.getPileCode() + result.getGunCode();
                                if (result.getResult() == 0) { //停止成功 这次启动流程结束
                                    pileStartNumMap.remove(key);
                                    pileStopNumMap.remove(key);
                                    pileTypeMap.remove(key);
                                }
                                if (result.getResult() == 1 && strategyPileMap.containsKey(key)) { //停止失败 重新操作停止
                                    strategyPileList.add(strategyPileMap.get(key));
                                }
                            }
                            //需要重新操作停止的桩不存在时则不需要重新停止
                            if (CollectionUtils.isEmpty(strategyPileList)) {
                                break;
                            }
                            num = num + 1;
                        }
                    }
                }
            }
        }
    }

    private PileStartVo getPileStartVo(String siteId, String pileCode, String gunCode, String phone, Integer repeatNum, Integer type, String rateId) {
        String key = pileCode + gunCode;
        PileRealModel pileRealModel = RedisGeneralUtil.getPileRealModel(pileCode);
        if (pileRealModel != null) {
            if (pileRealModel.getGunRealModelMap().containsKey(gunCode)) {
                PileRealModel.GunRealModel gunRealModel = pileRealModel.getGunRealModelMap().get(gunCode);
                //枪状态不是占用的话 不允许充放电
                Integer gunStatus = gunRealModel.getGunStatus();
                if (gunStatus != 3) {
                    return null;
                }
                Integer startNum = 0;
                if (pileStatusMap.containsKey(key) && Objects.equals(pileStatusMap.get(key), gunStatus)) {
                    if (pileStartNumMap.containsKey(key)) {
                        startNum = pileStartNumMap.get(key);
                    }
                } else {
                    pileStatusMap.put(key, gunStatus);
                }
                //启动次数小于总次数
                if (startNum < repeatNum) {
                    //创建启动命令参数
                    PileStartVo pileStartVo = new PileStartVo();
                    pileStartVo.setSiteId(siteId);
                    pileStartVo.setPileCode(pileCode);
                    pileStartVo.setGunCode(gunCode);
                    pileStartVo.setSerialNum(generateOrderNum(pileCode));
                    pileStartVo.setStarter(2);
                    pileStartVo.setType(0);
                    pileStartVo.setStrategy(0);
                    pileStartVo.setRunMode(type - 1);
                    pileStartVo.setAccountType(3);
                    pileStartVo.setAccountData(StringUtil.isNotEmpty(phone) ? phone : "18888888888");
                    pileStartVo.setPrepayMoney(new BigDecimal(1000));
                    pileStartVo.setIsStore(false);
                    //创建订单
                    createPileOrder(pileStartVo, rateId);
                    startNum = startNum + 1;
                    pileStartNumMap.put(key, startNum);
                    return pileStartVo;
                }
            }
        }
        return null;
    }

    private PileStopVo getPileStopVo(String pileCode, String gunCode, Integer repeatNum) {
        String key = pileCode + gunCode;
        PileRealModel pileRealModel = RedisGeneralUtil.getPileRealModel(pileCode);
        if (pileRealModel != null) {
            if (StringUtil.isEmpty(pileRealModel.getWorkStatus()) || pileRealModel.getWorkStatus() == 88) {
                return null;
            }
            if (pileRealModel.getGunRealModelMap().containsKey(gunCode)) {
                PileRealModel.GunRealModel gunRealModel = pileRealModel.getGunRealModelMap().get(gunCode);
                Integer gunStatus = gunRealModel.getGunStatus();
                if (StringUtil.isNotEmpty(gunStatus) && (gunStatus == 2 || gunStatus == 5 || gunStatus == 8)) {
                    Integer stopNum = 0;
                    if (pileStatusMap.containsKey(key) && Objects.equals(pileStatusMap.get(key), gunStatus)) {
                        if (pileStopNumMap.containsKey(key)) {
                            stopNum = pileStopNumMap.get(key);
                        }
                    } else {
                        pileStatusMap.put(key, gunStatus);
                    }
                    if (stopNum < repeatNum) {
                        PileStopVo pileStopVo = new PileStopVo();
                        pileStopVo.setPileCode(pileCode);
                        pileStopVo.setGunCode(gunCode);
                        pileStopVo.setSerialNum(gunRealModel.getSerialNum());
                        pileStopVo.setType(1);
                        pileStopVo.setIsStore(false);
                        stopNum = stopNum + 1;
                        pileStopNumMap.put(key, stopNum);
                        return pileStopVo;
                    }
                }
            }
        }
        return null;
    }

    public String generateOrderNum(String pileCode) {
        return webAppFeignService.generateOrderNum(pileCode, OrderSerialParamVo.PLATFORM_STRATEGY_NUMBER).getData();
    }

    public void createPileOrder(PileStartVo pileStartVo, String rateId) {
        OrderChangeVo orderChangeVo = new OrderChangeVo();
        BeanUtils.copyProperties(pileStartVo, orderChangeVo);
        orderChangeVo.setOrderNum(pileStartVo.getSerialNum());
        orderChangeVo.setIsOrderly(2);
        orderChangeVo.setOrderStatus(0);
        if (StringUtil.isNotEmpty(pileStartVo.getGunCode())) {
            orderChangeVo.setGunCode(Integer.valueOf(pileStartVo.getGunCode()));
        }
        orderChangeVo.setPlatformLogo(PlatformLogoVo.SUNMAX_LOGO);
        orderChangeVo.setStrategyType(pileStartVo.getStrategy());
        orderChangeVo.setSettlementState(0);
        orderChangeVo.setPayWay(1);
        orderChangeVo.setRateTemplateId(rateId);
        webAppFeignService.createPileOrder(orderChangeVo);
    }

}
