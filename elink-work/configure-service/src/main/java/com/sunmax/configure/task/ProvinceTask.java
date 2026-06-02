package com.sunmax.configure.task;

import com.google.common.collect.Maps;
import com.sunmax.common.config.redis.RedisGeneralUtil;
import com.sunmax.common.dto.system.PlatformDataForwardDto;
import com.sunmax.common.model.general.PileRealModel;
import com.sunmax.common.util.DateUtil;
import com.sunmax.common.util.DoubleUtil;
import com.sunmax.configure.common.push.ProvinceDataPush;
import com.sunmax.configure.dto.province.StationPowerInfoDto;
import com.sunmax.configure.dto.province.SubstationFieldDto;
import com.sunmax.configure.runner.SubstationRunner;
import com.sunmax.configure.vo.RequestCommonVo;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.compress.utils.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static com.sunmax.configure.common.push.ProvinceDataPush.*;

/**
 * 省平台定时任务
 */
@Configuration
public class ProvinceTask {

    @Autowired
    private SubstationRunner substationRunner;

    /**
     * 10分钟定时任务
     * 获取省市平台和互联互通平台的数据转发数据
     */
    @Scheduled(cron = "0 0/10 * * * ?")
    public void minute10Task() {
        //获取省市平台和互联互通平台的数据转发数据
        substationRunner.getPlatformDataList();
    }

    /**
     * 15分钟定时任务
     * 推送省平台站点实时功率数据
     */
    @Scheduled(cron = "0 0/15 * * * ?")
    public void minute15Task() {
        //定义站点实时功率集合
        Map<String, StationPowerInfoDto> sitePowerMap = Maps.newHashMap();
        SubstationRunner.privinceSitePileMap.forEach((siteId, substationField) -> {
            //获取站点下面的数据
            StationPowerInfoDto result = new StationPowerInfoDto();
            result.setOperatorId(substationField.getOperatorId());
            result.setEquipmentOwnerId(substationField.getEquipmentOwnerId());
            result.setStationId(substationField.getStationId());
            result.setDataTime(DateUtil.localDateTimeToStr(LocalDateTime.now()));
            //定义站点实时功率
            AtomicReference<Double> stationRealPower = new AtomicReference<>(0.0);
            result.setEquipmentPowerInfos(substationField.getPileCodes().stream().map(pileCode -> {
                //定义充电桩返回实体类
                StationPowerInfoDto.EquipmentPowerInfo equipmentPowerInfo = new StationPowerInfoDto.EquipmentPowerInfo();
                equipmentPowerInfo.setEquipmentId(pileCode);
                equipmentPowerInfo.setDataTime(DateUtil.localDateTimeToStr(LocalDateTime.now()));
                //定义电桩实时功率
                AtomicReference<Double> pileRealPower = new AtomicReference<>(0.0);
                PileRealModel pileRealModel = RedisGeneralUtil.getPileRealModel(pileCode);
                if (pileRealModel != null) {
                    equipmentPowerInfo.setConnectorPowerInfos(pileRealModel.getGunRealModelMap().entrySet().stream().map(entry -> {
                        String gunCode = entry.getKey();
                        PileRealModel.GunRealModel gunRealModel = entry.getValue();
                        //定义枪返回实体类
                        StationPowerInfoDto.ConnectorPowerInfo connectorPowerInfo = new StationPowerInfoDto.ConnectorPowerInfo();
                        connectorPowerInfo.setConnectorId(pileCode + gunCode);
                        connectorPowerInfo.setDataTime(DateUtil.localDateTimeToStr(LocalDateTime.now()));
                        //充电枪充放电状态
                        List<Integer> workStatus = Arrays.asList(1, 2, 4, 5);
                        if (workStatus.contains(gunRealModel.getGunStatus())) {
                            Double outPower = DoubleUtil.getAbsDouble(gunRealModel.getOutPower());
                            pileRealPower.updateAndGet(v -> v + outPower);
                            connectorPowerInfo.setConnectorRealTimePower(outPower);
                        }
                        return connectorPowerInfo;
                    }).collect(Collectors.toList()));
                }
                //电桩实时功率
                equipmentPowerInfo.setEquipRealTimePower(pileRealPower.get());
                stationRealPower.updateAndGet(v -> v + pileRealPower.get());
                return equipmentPowerInfo;
            }).collect(Collectors.toList()));
            //电站实时功率
            result.setStationRealTimePower(stationRealPower.get());
            sitePowerMap.put(siteId, result);
        });
        if (CollectionUtils.isNotEmpty(SubstationRunner.provincePlatformSet) && MapUtils.isNotEmpty(sitePowerMap)) {
            for (PlatformDataForwardDto dataForward : SubstationRunner.provincePlatformSet) {
                List<StationPowerInfoDto> stationPowerInfos = Lists.newArrayList();
                dataForward.getSiteOperateList().forEach(siteOperate -> {
                    if (sitePowerMap.containsKey(siteOperate.getSiteId())) {
                        stationPowerInfos.add(sitePowerMap.get(siteOperate.getSiteId()));
                    }
                });
                if (CollectionUtils.isNotEmpty(stationPowerInfos)) {
                    //推送省平台站点实时功率数据
                    RequestCommonVo commonVo = RequestCommonVo.builder()
                            .url(dataForward.getAddress())
                            .platformId(dataForward.getPlatformId())
                            .platformSecret(dataForward.getPlatformSecret())
                            .dataSecret(dataForward.getDataSecret())
                            .dataSecretIv(dataForward.getDataSecretIv())
                            .sigSecret(dataForward.getSigSecret()).build();
                    ProvinceDataPush.stationPowerInfos(commonVo, stationPowerInfos);
                }
            }
        }
    }

    /**
     * 5分钟定时任务
     * 推送省平台充电状态任务
     */
    @Scheduled(cron = "0 0/5 * * * ?")
    public void minute5Task() {
        equipChargeStatusMap.values().forEach(equipChargeStatus -> {
            LocalDateTime nowTime = LocalDateTime.now();
            LocalDateTime pushTime = DateUtil.strToLocalDateTime(equipChargeStatus.getEndTime());
            //推送时间和当前时间小于5分钟进行推送
            SubstationFieldDto pileOperateField = SubstationRunner.getPileOperateField(equipChargeStatus.getEquipmentId(), 1);
            if (DateUtil.compareDiffBetweenMinutes(pushTime, nowTime) <= 5 && pileOperateField != null) {
                ProvinceDataPush.pileChargeStatus(getCommonVo(pileOperateField), equipChargeStatus);
            }
        });
    }

    /**
     * 1小时定时任务
     * 推送省平台充电状态任务
     */
    @Scheduled(cron = "0 0 * * * ?")
    public void hour1Task() {
        orderInfoMap.values().forEach(orderInfo -> {
            SubstationFieldDto pileOperateField = SubstationRunner.getPileOperateField(orderInfo.getEquipmentId(), 1);
            if (pileOperateField != null) {
                ProvinceDataPush.orderInfo(getCommonVo(pileOperateField), orderInfo);
            }
        });
    }

}
