package com.sunmax.together.task;

import com.sunmax.common.dto.device.DeviceBasicInfoDto;
import com.sunmax.common.dto.device.DeviceGunInfoDto;
import com.sunmax.common.dto.device.SiteInfoDto;
import com.sunmax.common.util.StringUtil;
import com.sunmax.common.vo.DeviceTypeParamVo;
import com.sunmax.common.vo.ReaFieldParamVo;
import com.sunmax.together.dao.SiteCountRecordDao;
import com.sunmax.together.entity.SiteCountRecordEntity;
import com.sunmax.together.service.feign.DeviceService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Configuration
public class SiteCountRecordTask {

    @Autowired
    private DeviceService deviceService;

    @Autowired
    private SiteCountRecordDao siteCountRecordDao;

    /**
     * 每天凌晨零点1分定时补录昨天订单数据
     */
    @Scheduled(cron = "0 1 0 * * ?")
    public void day1Task() {
        LocalDate countDate = LocalDate.now().minusDays(1);
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

            //对数据进行组装
            List<SiteCountRecordEntity> resultList = siteIds.stream().map(siteId -> {
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
                result.setCountDate(countDate);
                result.setCreateTime(LocalDateTime.now());
                return result;
            }).collect(Collectors.toList());

            siteCountRecordDao.saveAll(resultList);
        }

    }

}
