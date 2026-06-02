package com.sunmax.together.task;


import com.google.common.collect.Lists;
import com.sunmax.common.dto.device.DeviceBasicInfoDto;
import com.sunmax.common.model.general.PileRealModel;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.util.StringUtil;
import com.sunmax.together.dao.PileStateDurationDao;
import com.sunmax.together.entity.PileStateDurationEntity;
import com.sunmax.together.service.feign.DeviceService;
import com.sunmax.together.websocket.*;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.sunmax.common.config.redis.RedisGeneralUtil.getPileRealModelList;
import static com.sunmax.common.util.DateUtil.localDateToStr;

/**
 * 充电桩实时定时任务类
 */
@Configuration
public class RealTask {

    @Autowired
    private DeviceService deviceService;

    @Autowired
    private PileStateDurationDao pileStateDurationDao;

    @Autowired
    private CustomSystemWebSocketHandler customSystemWebSocketHandler;

    @Autowired
    private LargeStaticWebSocketHandler largeStaticWebSocketHandler;

    @Autowired
    private PileRealWebSocketHandler pileRealWebSocketHandler;

    @Autowired
    private LargeRealWebSocketHandler largeRealWebSocketHandler;

    @Autowired
    private HomePageWebSocketHandler homePageWebSocketHandler;

    @Scheduled(cron = "0/15 * * * * ? ")
    public void sendPileData() {
        pileRealWebSocketHandler.sendAllMessage();
    }

    @Scheduled(cron = "0 0/15 * * * ?")
    public void sendLargeStaticData() {
        largeStaticWebSocketHandler.sendAllMessage();
    }

    @Scheduled(cron = "5 * * * * ?")
    public void sendLargeRealData() {
        largeRealWebSocketHandler.sendAllMessage();
        homePageWebSocketHandler.sendAllMessage();
        customSystemWebSocketHandler.sendAllMessage();
    }

    /**
     * 每30秒执行查询电枪状态
     */
    @Scheduled(fixedRate = 30000)
    public void findGunStatus() {
        //查询所有电桩信息数据
        ResponseResult<List<DeviceBasicInfoDto>> allPileDeviceInfoList = deviceService.findAllPileDeviceInfoList();
        if (allPileDeviceInfoList.isSuccess() && CollectionUtils.isNotEmpty(allPileDeviceInfoList.getData())) {
            List<DeviceBasicInfoDto> pileDeviceList = allPileDeviceInfoList.getData().stream().filter(p -> StringUtil.isNotEmpty(p.getDeviceNumber()))
                    .collect(Collectors.toList());

            //当日日期
            String dayDate = localDateToStr(LocalDate.now());
//            String dayDate = "2024-12-25";
            //查询本日所有电桩状态数据
            Map<String, List<PileStateDurationEntity>> groupByPileCodeMap = pileStateDurationDao.findAllByCountDate(dayDate).stream()
                    .filter(Objects::nonNull).filter(p -> StringUtil.isNotEmpty(p.getPileCode()))
                    .collect(Collectors.groupingBy(PileStateDurationEntity::getPileCode));

            //根据多个电桩编码查询实时数据
            List<String> pileCodeList = pileDeviceList.stream().map(DeviceBasicInfoDto::getDeviceNumber).filter(StringUtil::isNotEmpty).distinct().collect(Collectors.toList());
            Map<String, PileRealModel> pileRealModelMap = Objects.requireNonNull(getPileRealModelList(pileCodeList)).stream().collect(Collectors.toMap(PileRealModel::getPileCode, pileRealModel -> pileRealModel, (k1, k2) -> k1));

            //存储保存电枪状态数据
            List<PileStateDurationEntity> pileStateDurationEntityList = Lists.newArrayList();
            //循环电桩数据查询电枪状态
            pileDeviceList.forEach(pileInfo -> {

                PileStateDurationEntity stateDurationEntity = new PileStateDurationEntity();

                //当前时间
                LocalDateTime currentTime = LocalDateTime.now();
                //当前电桩状态(默认设置未注册)
                Integer workStatus = 99;

                //获取电桩实时数据
                PileRealModel pileRealModel = pileRealModelMap.get(pileInfo.getDeviceNumber());
                if (StringUtil.isNotEmpty(pileRealModel) && StringUtil.isNotEmpty(pileRealModel.getPileCode())) {
                    //电桩实时状态
                    workStatus = pileRealModel.getWorkStatus();
                }
                //本日电桩状态数据
                if (!groupByPileCodeMap.isEmpty() && groupByPileCodeMap.containsKey(pileInfo.getDeviceNumber())) {
                    //根据结束时间获取获取最新一条数据
                    stateDurationEntity = groupByPileCodeMap.get(pileInfo.getDeviceNumber()).stream().filter(p -> StringUtil.isNotEmpty(p.getEndTime())).max(Comparator.comparing(PileStateDurationEntity::getEndTime)).orElse(null);
                }
                //判断是否已有当前状态数据,并且判断是否和当前状态相同
                if (StringUtil.isNotEmpty(stateDurationEntity) && StringUtil.isNotEmpty(stateDurationEntity.getPileCode())) {
                    //获取开始时间 - 至当前时间，累加状态时长
                    LocalDateTime startTime = stateDurationEntity.getStartTime();
                    LocalDateTime endOfDay = startTime.toLocalDate().atTime(23, 59, 59);
                    LocalDateTime endTime = startTime.toLocalDate().equals(currentTime.toLocalDate()) ? currentTime : endOfDay;
                    stateDurationEntity.setEndTime(endTime);
                    //判断状态是否相同，相同则累加时长，负责创建新的状态
                    if (Objects.equals(stateDurationEntity.getWorkState(), workStatus)) {
                        stateDurationEntity.setDuration(ChronoUnit.SECONDS.between(stateDurationEntity.getStartTime(), endTime));
                        pileStateDurationEntityList.add(stateDurationEntity);
                    } else {
                        //创建新的状态对象
                        pileStateDurationEntityList.add(createNewStateDurationEntity(currentTime, workStatus, pileInfo, dayDate, pileInfo.getSiteId()));
                    }
                } else {
                    pileStateDurationEntityList.add(createNewStateDurationEntity(currentTime, workStatus, pileInfo, dayDate, pileInfo.getSiteId()));
                }
            });
            if (CollectionUtils.isNotEmpty(pileStateDurationEntityList)) {
                pileStateDurationDao.saveAll(pileStateDurationEntityList);
            }
        }
    }

    private PileStateDurationEntity createNewStateDurationEntity(LocalDateTime currentTime, Integer pileStatus, DeviceBasicInfoDto pileInfo, String dayDate, String siteId) {
        PileStateDurationEntity stateDurationEntity = new PileStateDurationEntity();
        stateDurationEntity.setStartTime(currentTime);
        stateDurationEntity.setEndTime(currentTime);
        stateDurationEntity.setPileCode(pileInfo.getDeviceNumber());
        stateDurationEntity.setWorkState(pileStatus);
        stateDurationEntity.setDuration(0L);
        stateDurationEntity.setCountDate(dayDate);
        stateDurationEntity.setSiteId(siteId);
        return stateDurationEntity;
    }
}
