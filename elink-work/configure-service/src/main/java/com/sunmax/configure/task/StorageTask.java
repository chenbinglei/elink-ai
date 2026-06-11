package com.sunmax.configure.task;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.sunmax.common.dto.data.NodeDifHistoryDto;
import com.sunmax.common.dto.device.ModelFunctionListDto;
import com.sunmax.common.model.RealDataModel;
import com.sunmax.common.util.DateUtil;
import com.sunmax.common.util.DoubleUtil;
import com.sunmax.common.util.StringUtil;
import com.sunmax.common.util.oss.FileUtil;
import com.sunmax.common.vo.FunctionLogoParamVo;
import com.sunmax.common.vo.data.DeviceHistoryQueryVo;
import com.sunmax.configure.dto.storage.StationArchiveDto;
import com.sunmax.configure.dto.storage.StationRealtimeDto;
import com.sunmax.configure.dto.storage.StationStatusDto;
import com.sunmax.configure.runner.SubstationRunner;
import com.sunmax.configure.service.feign.DataService;
import com.sunmax.configure.service.feign.DeviceService;
import com.sunmax.configure.util.storage.StorageConst;
import com.sunmax.configure.util.storage.StorageUtil;
import com.sunmax.configure.vo.storage.StorageCommonVo;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.compress.utils.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 储能平台定时任务类
 */
@Configuration
public class StorageTask {

    @Autowired
    private DeviceService deviceService;

    @Autowired
    private DataService dataService;

    /**
     * 5分钟定时任务
     * 推送实时数据接口
     */
    @Scheduled(cron = "0 0/5 * * * ?")
    public void minute5Task() {
        long dateTime = System.currentTimeMillis();
        //根据多个设备id查询设备实时数据(实时状态,实时功率,当前SOC值)
        Map<String, Map<String, RealDataModel>> pcsDeviceMap = Maps.newHashMap();
        Map<String, Map<String, ModelFunctionListDto>> modelFunctionMap = Maps.newHashMap();
        Map<String, Map<String, RealDataModel>> batteryDeviceMap = Maps.newHashMap();
        Set<StorageCommonVo.DeviceInfo> pcsModels = Sets.newHashSet();
        Set<StorageCommonVo.DeviceInfo> batteryModels = Sets.newHashSet();
        SubstationRunner.storageMap.forEach((appId, commonVo) -> {
            pcsModels.addAll(commonVo.getSiteResourceMap().values().stream().flatMap(s -> s.getPcsModels().stream()).collect(Collectors.toSet()));
            batteryModels.addAll(commonVo.getSiteResourceMap().values().stream().flatMap(s -> s.getBatteryModels().stream()).collect(Collectors.toSet()));
        });
        if (CollectionUtils.isNotEmpty(pcsModels)) {
            //根据多个pcs设备id查询pcs实时数据(有功功率,运行状态)
            Set<String> pcsIds = pcsModels.stream().map(StorageCommonVo.DeviceInfo::getDeviceId).collect(Collectors.toSet());
            String functionLogos = String.join(FileUtil.COMMA, FunctionLogoParamVo.PCS_ACTIVE_POWER, FunctionLogoParamVo.PCS_OPERATIVE_MODE);
            pcsDeviceMap = deviceService.getDeviceFunctionsRealDataByIds(pcsIds, functionLogos, 1).getData();
            //根据多个pcs设备id查询pcs状态枚举值
            Set<String> batteryModelIds = batteryModels.stream().map(StorageCommonVo.DeviceInfo::getDeviceId).collect(Collectors.toSet());
            modelFunctionMap = deviceService.getModelFunctionListByModelIds(batteryModelIds, FunctionLogoParamVo.PCS_OPERATIVE_MODE).getData();
        }
        if (CollectionUtils.isNotEmpty(batteryModels)) {
            //根据多个电池簇id查询电池簇实时数据(当前SOC值)
            Set<String> batteryIds = batteryModels.stream().map(StorageCommonVo.DeviceInfo::getDeviceId).collect(Collectors.toSet());
            batteryDeviceMap = deviceService.getDeviceFunctionsRealDataByIds(batteryIds, FunctionLogoParamVo.BATTERY_TOTAL_SOC, 1).getData();
        }
        for (StorageCommonVo commonVo : SubstationRunner.storageMap.values()) {
            for (StorageCommonVo.SiteResource siteResource : commonVo.getSiteResourceMap().values()) {
                //定义实时数据实体类
                StationRealtimeDto stationRealtime = new StationRealtimeDto();
                stationRealtime.setStationNo(siteResource.getResourceNo());
                stationRealtime.setDataTime(dateTime);
                //获取实时状态,有功功率,SOC
                //实时状态 000-待机 101-充电 110-放电 001-故障 011-停机
                Set<String> statusList = Sets.newHashSet();
                List<Double> activePowerList = Lists.newArrayList();
                List<Float> socList = Lists.newArrayList();
                Map<String, Map<String, RealDataModel>> finalPcsDeviceMap = pcsDeviceMap;
                Map<String, Map<String, ModelFunctionListDto>> finalModelFunctionMap = modelFunctionMap;
                siteResource.getPcsModels().stream().filter(p -> finalPcsDeviceMap.containsKey(p.getDeviceId())).forEach(pcs -> {
                    Map<String, RealDataModel> realDataMap = finalPcsDeviceMap.get(pcs.getDeviceId());
                    Double activePower = null;
                    if (realDataMap.containsKey(FunctionLogoParamVo.PCS_ACTIVE_POWER)) {
                        RealDataModel realData = realDataMap.get(FunctionLogoParamVo.PCS_ACTIVE_POWER);
                        if (StringUtil.isNotEmpty(realData.getDataValue())) {
                            activePower = Double.parseDouble(String.valueOf(realData.getDataValue()));
                        }
                    }
                    activePowerList.add(activePower);
                    if (realDataMap.containsKey(FunctionLogoParamVo.PCS_OPERATIVE_MODE) && finalModelFunctionMap.containsKey(pcs.getModelId())) {
                        ModelFunctionListDto modelFunction = finalModelFunctionMap.get(pcs.getModelId()).get(FunctionLogoParamVo.PCS_OPERATIVE_MODE);
                        RealDataModel realData = realDataMap.get(FunctionLogoParamVo.PCS_OPERATIVE_MODE);
                        if (StringUtil.isNotEmpty(realData.getDataValue()) && modelFunction != null && StringUtil.isNotEmpty(modelFunction.getDataObject())) {
                            //获取数据值
                            String dataValue = String.valueOf(realData.getDataValue());
                            JSONObject dataObject = JSON.parseObject(modelFunction.getDataObject());
                            if (dataObject != null && dataObject.containsKey("enumArray")) {
                                for (Object object : dataObject.getJSONArray("enumArray")) {
                                    JSONObject jsonObject = JSON.parseObject(JSON.toJSONString(object));
                                    if (jsonObject.containsKey("id") && jsonObject.containsKey("name")) {
                                        if (Objects.equals(jsonObject.getString("id"), dataValue)) {
                                            switch (jsonObject.getString("name")) {
                                                case "停机":
                                                    statusList.add("011");
                                                    break;
                                                case "待机":
                                                    statusList.add("000");
                                                    break;
                                                case "运行":
                                                    if (activePower != null) {
                                                        if (activePower >= 0) {
                                                            statusList.add("101");
                                                        } else {
                                                            statusList.add("110");
                                                        }
                                                    }
                                                    break;
                                                case "故障":
                                                    statusList.add("001");
                                                    break;
                                                case "充电":
                                                    statusList.add("101");
                                                    break;
                                                case "放电":
                                                    statusList.add("110");
                                                    break;
                                            }
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    }
                });
                Map<String, Map<String, RealDataModel>> finalBatteryDeviceMap = batteryDeviceMap;
                siteResource.getBatteryModels().stream().filter(b -> finalBatteryDeviceMap.containsKey(b.getDeviceId())).forEach(battery -> {
                    Map<String, RealDataModel> realDataMap = finalBatteryDeviceMap.get(battery.getDeviceId());
                    //电池当前SOC
                    if (realDataMap.containsKey(FunctionLogoParamVo.BATTERY_TOTAL_SOC)) {
                        RealDataModel realData = realDataMap.get(FunctionLogoParamVo.BATTERY_TOTAL_SOC);
                        if (StringUtil.isNotEmpty(realData.getDataValue())) {
                            socList.add(DoubleUtil.getToDouble(Double.parseDouble(String.valueOf(realData.getDataValue())), 4).floatValue());
                        }
                    }
                });
                //获取电站实时状态 000-待机 101-充电 110-放电 001-故障 011-停机
                if (!statusList.stream().filter("101"::equals).collect(Collectors.toSet()).isEmpty()) {
                    stationRealtime.setStationStatus("101");
                } else if (!statusList.stream().filter("110"::equals).collect(Collectors.toSet()).isEmpty()) {
                    stationRealtime.setStationStatus("110");
                } else if (!statusList.stream().filter("000"::equals).collect(Collectors.toSet()).isEmpty()) {
                    stationRealtime.setStationStatus("000");
                } else if (!statusList.stream().filter("001"::equals).collect(Collectors.toSet()).isEmpty()) {
                    stationRealtime.setStationStatus("001");
                } else {
                    stationRealtime.setStationStatus("011");
                }
                stationRealtime.setActivePower(DoubleUtil.getToDouble(activePowerList.stream().filter(StringUtil::isNotEmpty).mapToDouble(a -> a).sum()).floatValue());
                stationRealtime.setValueSOC(socList.stream().min(Float::compareTo).orElse(0f));
                stationRealtime.setDataType("00");
                //推送储能实时数据
                StorageUtil.sendData(commonVo.getAppId(), commonVo.getPublicKey(), commonVo.getPrivateKey(), commonVo.getBaseUrl(),
                        StorageConst.STATION_REALTIME_API, siteResource.getResourceNo(), stationRealtime);
            }
        }
    }

    /**
     * 每天定时任务
     * 推送统计数据接口
     */
    @Scheduled(cron = "0 1 0 * * ?")
    public void day1Task() {
        //推送前一天的储能统计数据
        LocalDate nowDate = LocalDate.now().minusDays(1);
        String dataDate = nowDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        //定义pcs本月的充放电量,缓存中的累计充放电量
        Map<String, Map<String, List<NodeDifHistoryDto>>> deviceMonthDataMap = Maps.newHashMap();
        Map<String, Map<String, RealDataModel>> deviceRealDataMap = Maps.newHashMap();
        Set<String> pcsIds = Sets.newHashSet();
        SubstationRunner.storageMap.forEach((appId, commonVo) -> pcsIds.addAll(commonVo.getSiteResourceMap().values()
                .stream().flatMap(s -> s.getPcsModels().stream().map(StorageCommonVo.DeviceInfo::getDeviceId))
                .collect(Collectors.toSet())));
        if (CollectionUtils.isNotEmpty(pcsIds)) {
            //根据多个pcs设备id查询pcs本月的充放电量
            Set<String> functionLogos = new HashSet<>(Arrays.asList(FunctionLogoParamVo.PCS_BATTERY_CHARGE, FunctionLogoParamVo.PCS_BATTERY_DISCHARGE));
            DeviceHistoryQueryVo deviceQueryVo = new DeviceHistoryQueryVo();
            deviceQueryVo.setDeviceIds(pcsIds);
            deviceQueryVo.setFunctionLogos(functionLogos);
            deviceQueryVo.setStartTime(DateUtil.localDateTimeToStr(nowDate.withDayOfMonth(1).atStartOfDay()));
            deviceQueryVo.setEndTime(DateUtil.localDateTimeToStr(LocalDateTime.of(nowDate, LocalTime.now())));
            deviceQueryVo.setTimeInterval("1d");
            deviceMonthDataMap = dataService.findNodeDifHistoryListFeign(deviceQueryVo).getData();

            //获取缓存中的pcs设备的充放电量
            deviceRealDataMap = deviceService.getDeviceFunctionsRealDataByIds(pcsIds, String.join(FileUtil.COMMA, FunctionLogoParamVo.PCS_BATTERY_CHARGE,
                    FunctionLogoParamVo.PCS_BATTERY_DISCHARGE), 1).getData();
        }
        for (StorageCommonVo commonVo : SubstationRunner.storageMap.values()) {
            for (StorageCommonVo.SiteResource siteResource : commonVo.getSiteResourceMap().values()) {
                //定义统计数据实体类
                StationArchiveDto stationArchive = new StationArchiveDto();
                stationArchive.setStationNo(siteResource.getResourceNo());
                stationArchive.setDataDate(dataDate);
                Set<String> sitePcsIds = siteResource.getPcsModels().stream().map(StorageCommonVo.DeviceInfo::getDeviceId).collect(Collectors.toSet());
                for (String pcsId : sitePcsIds) {
                    //获取pcs设备本月的充放电量
                    if (deviceMonthDataMap.containsKey(pcsId)) {
                        Map<String, List<NodeDifHistoryDto>> functionDataMap = deviceMonthDataMap.get(pcsId);
                        if (functionDataMap.containsKey(FunctionLogoParamVo.PCS_BATTERY_CHARGE)) {
                            List<NodeDifHistoryDto> dataList = functionDataMap.get(FunctionLogoParamVo.PCS_BATTERY_CHARGE);
                            //本月的充电量
                            double monthValue = dataList.stream().mapToDouble(d -> DoubleUtil.getObjSub(d.getFirstDataValue(), d.getLastDataValue())).sum();
                            stationArchive.setChaEnergyMonth(stationArchive.getChaEnergyMonth() + (float) monthValue);
                            //当天的充电量
                            double dayValue = dataList.stream().filter(d -> Objects.equals(d.getFirstDateTime(), DateUtil.getDayStart(DateUtil.localDateToStr(nowDate))))
                                    .mapToDouble(d -> DoubleUtil.getObjSub(d.getFirstDataValue(), d.getLastDataValue())).sum();
                            stationArchive.setChaEnergyDay(stationArchive.getChaEnergyDay() + (float) dayValue);
                        }
                        if (functionDataMap.containsKey(FunctionLogoParamVo.PCS_BATTERY_DISCHARGE)) {
                            List<NodeDifHistoryDto> dataList = functionDataMap.get(FunctionLogoParamVo.PCS_BATTERY_DISCHARGE);
                            //本月的放电量
                            double monthValue = dataList.stream().mapToDouble(d -> DoubleUtil.getObjSub(d.getFirstDataValue(), d.getLastDataValue())).sum();
                            stationArchive.setDisEnergyMonth(stationArchive.getDisEnergyMonth() + (float) monthValue);
                            //当天的放电量
                            double dayValue = dataList.stream().filter(d -> Objects.equals(d.getFirstDateTime(), DateUtil.getDayStart(DateUtil.localDateToStr(nowDate))))
                                    .mapToDouble(d -> DoubleUtil.getObjSub(d.getFirstDataValue(), d.getLastDataValue())).sum();
                            stationArchive.setDisEnergyMonth(stationArchive.getDisEnergyMonth() + (float) dayValue);
                        }
                    }
                    //获取pcs设备的累计充放电量
                    if (deviceRealDataMap.containsKey(pcsId)) {
                        Map<String, RealDataModel> functionDataMap = deviceRealDataMap.get(pcsId);
                        //累计充电量
                        if (functionDataMap.containsKey(FunctionLogoParamVo.PCS_BATTERY_CHARGE)) {
                            RealDataModel realData = functionDataMap.get(FunctionLogoParamVo.PCS_BATTERY_CHARGE);
                            if (realData != null && StringUtil.isNotEmpty(realData.getDataValue())) {
                                stationArchive.setChaEnergyDay(stationArchive.getChaEnergyTotal() + DoubleUtil.objToDouble(realData.getDataValue()).floatValue());
                            }
                        }
                        //累计放电量
                        if (functionDataMap.containsKey(FunctionLogoParamVo.PCS_BATTERY_DISCHARGE)) {
                            RealDataModel realData = functionDataMap.get(FunctionLogoParamVo.PCS_BATTERY_DISCHARGE);
                            if (realData != null && StringUtil.isNotEmpty(realData.getDataValue())) {
                                stationArchive.setDisEnergyTotal(stationArchive.getDisEnergyTotal() + DoubleUtil.objToDouble(realData.getDataValue()).floatValue());
                            }
                        }
                    }
                }
                stationArchive.setDataType("00"); //数据来源 00-实时数据 01-补传数据 10-工单重传数据
                //推送储能实时数据
                StorageUtil.sendData(commonVo.getAppId(), commonVo.getPublicKey(), commonVo.getPrivateKey(), commonVo.getBaseUrl(),
                        StorageConst.STATION_ARCHIVE_API, siteResource.getResourceNo(), stationArchive);
            }
        }
    }

    /**
     * 1分钟定时任务
     * 推送状态数据接口
     */
    @Scheduled(cron = "0 0/1 * * * ?")
    public void minute1Task() {
        for (StorageCommonVo commonVo : SubstationRunner.storageMap.values()) {
            for (StorageCommonVo.SiteResource siteResource : commonVo.getSiteResourceMap().values()) {
                //定义统计数据实体类
                StationStatusDto stationStatus = new StationStatusDto();
                stationStatus.setStationNo(siteResource.getResourceNo());
                stationStatus.setDataTime(System.currentTimeMillis());
                stationStatus.setStationStatus("0");
                if (StorageUtil.failDataMap.containsKey(siteResource.getResourceNo())) {
                    stationStatus.setStationStatus("1");
                }
                StorageUtil.sendData(commonVo.getAppId(), commonVo.getPublicKey(), commonVo.getPrivateKey(), commonVo.getBaseUrl(),
                        StorageConst.STATION_STATUS_API, siteResource.getResourceNo(), stationStatus);
            }
        }
    }

}
