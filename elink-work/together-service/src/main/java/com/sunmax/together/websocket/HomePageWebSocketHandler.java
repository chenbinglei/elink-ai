package com.sunmax.together.websocket;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.google.common.collect.Maps;
import com.sunmax.common.dto.device.DeviceBasicInfoDto;
import com.sunmax.common.dto.device.SiteInfoDto;
import com.sunmax.common.dto.device.SiteScenarioTypeDto;
import com.sunmax.common.dto.system.OrganEmpowerListDto;
import com.sunmax.common.model.RealDataModel;
import com.sunmax.common.util.DoubleUtil;
import com.sunmax.common.util.StringUtil;
import com.sunmax.common.util.oss.FileUtil;
import com.sunmax.common.vo.FunctionLogoParamVo;
import com.sunmax.common.vo.ReaFieldParamVo;
import com.sunmax.common.vo.SiteFieldParamVo;
import com.sunmax.together.dto.websocket.HomePageWebSocketDto;
import com.sunmax.together.mapper.OrderRecordMapper;
import com.sunmax.together.model.ChargeOrderQtModel;
import com.sunmax.together.service.feign.DeviceService;
import com.sunmax.together.service.feign.SystemService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.compress.utils.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Configuration
@Slf4j
public class HomePageWebSocketHandler extends TextWebSocketHandler {

    @Autowired
    private SystemService systemService;

    @Autowired
    private DeviceService deviceService;

    @Autowired
    private OrderRecordMapper orderRecordMapper;

    private static final Map<String, WebSocketSession> sessionMap = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        log.info("首页数据新开启了一个webSocket连接{}", session.getId());
        //从请求参数中获取 userId, 例如: ws://.../homePageWebSocket/{userId}
        String path = Objects.requireNonNull(session.getUri()).getPath();
        String[] pathParts = path.split(FileUtil.SLASH);
        if (pathParts.length >= 4) {
            String userId = pathParts[3];
            sessionMap.put(userId, session);
            //连接后发送一条数据
            this.sendMessage(userId, JSON.toJSONString(getHomePageWebSocket(userId)));
            log.info("有新的连接加入！访问首页数据用户id:{}", userId);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        sessionMap.values().removeIf(s -> s.equals(session));
        log.info("首页数据有一连接关闭，sessionId={}, status={}", session.getId(), status);
    }

    private void sendMessage(String userId, String message) {
        WebSocketSession session = sessionMap.get(userId);
        if (session != null && session.isOpen()) {
            try {
                session.sendMessage(new TextMessage(message));
            } catch (Exception e) {
                log.error("推送首页数据失败", e);
            }
        }
    }

    /**
     * 外部调用发送消息
     */
    public void sendAllMessage() {
        sessionMap.keySet().forEach(userId -> sendMessage(userId, JSON.toJSONString(getHomePageWebSocket(userId))));
    }

    //获取首页数据
    private HomePageWebSocketDto getHomePageWebSocket(String userId) {
        //返回的对象
        HomePageWebSocketDto result = new HomePageWebSocketDto();

        //查询用户下面所有的站点列表
        List<String> siteIds = systemService.findAllOrganEmpowerByUserId(userId).getData().stream().map(OrganEmpowerListDto::getSiteId)
                .filter(StringUtil::isNotEmpty).distinct().collect(Collectors.toList());
        if (siteIds.isEmpty()) {
            return result;
        }
        //根据多个站点id查询站点数据列表
        List<SiteInfoDto> siteInfoList = deviceService.findSiteBasicInfoByIds(siteIds).getData().values().stream()
                .filter(s -> StringUtil.isNotEmpty(s.getScenarioTypes())).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(siteInfoList)) {
            return result;
        }

        if (CollectionUtils.isNotEmpty(siteInfoList)) {

            //0.定义系统类型对象map(key(1-光伏场站 2-储能场站 3-充电场站 4-V2G场站 5-超充场站 6-换电场站) -> value(多个系统id))
            Map<Integer, List<String>> systemIdMap = Maps.newHashMap();
            //定义光伏数据实体对象
            HomePageWebSocketDto.PvDataDto pvData = new HomePageWebSocketDto.PvDataDto();
            //定义储能数据实体对象
            HomePageWebSocketDto.StorageDataDto storageData = new HomePageWebSocketDto.StorageDataDto();
            //定义充电桩实体类对象
            HomePageWebSocketDto.PileDataDto pileData = new HomePageWebSocketDto.PileDataDto();
            //定义换电实体类对象
            HomePageWebSocketDto.ChangeDataDto changeData = new HomePageWebSocketDto.ChangeDataDto();

            //1.获取光伏数据
            List<SiteInfoDto> pvSiteList = siteInfoList.stream().filter(s -> s.getScenarioTypes().contains("1")).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(pvSiteList)) {

                pvData.setSiteNum(pvSiteList.size());//站点数量
                //定义光伏系统id列表
                List<String> pvSystemIds = Lists.newArrayList();
                pvData.setSiteList(pvSiteList.stream().map(site -> {
                    HomePageWebSocketDto.SiteInfoDto siteInfoDto = new HomePageWebSocketDto.SiteInfoDto();
                    siteInfoDto.setId(site.getId());
                    siteInfoDto.setSiteName(site.getSiteName());
                    siteInfoDto.setScenarioTypes(site.getScenarioTypes());
                    //获取站点类型 以及光伏装机容量
                    //站点类型(默认给0)
                    siteInfoDto.setSiteType(0);
                    if (CollectionUtils.isNotEmpty(site.getSiteScenarioTypeDtos())) {
                        List<SiteScenarioTypeDto> scenarioTypeList = site.getSiteScenarioTypeDtos().stream().filter(s -> StringUtil.isNotEmpty(s.getScenarioType())
                                && s.getScenarioType() == 1).collect(Collectors.toList());
                        if (CollectionUtils.isNotEmpty(scenarioTypeList)) {
                            pvSystemIds.addAll(scenarioTypeList.stream().map(SiteScenarioTypeDto::getId).collect(Collectors.toList()));
                            //站点类型
                            SiteScenarioTypeDto scenarioType = scenarioTypeList.get(0);
                            if (StringUtil.isNotEmpty(scenarioType) && StringUtil.isNotEmpty(scenarioType.getReadwriteObject())) {
                                JSONObject jsonObject = JSON.parseObject(scenarioType.getReadwriteObject());
                                if (jsonObject.containsKey(SiteFieldParamVo.PV_SYS_TYPE)) {
                                    siteInfoDto.setSiteType(jsonObject.getInteger(SiteFieldParamVo.PV_SYS_TYPE)); //光伏站点类型
                                }
                            }
                            //光伏装机容量
                            pvData.setPvCapacity(pvData.getPvCapacity() + scenarioTypeList.stream().filter(s -> StringUtil.isNotEmpty(s.getReadwriteObject())).mapToDouble(s -> {
                                JSONObject jsonObject = JSON.parseObject(s.getReadwriteObject());
                                if (jsonObject.containsKey(SiteFieldParamVo.PV_CAPACITY) && StringUtil.isNotEmpty(jsonObject.getString(SiteFieldParamVo.PV_CAPACITY))) {
                                    return jsonObject.getDouble(SiteFieldParamVo.PV_CAPACITY);
                                }
                                return 0.0;
                            }).sum());
                        }
                    }
                    //获取经纬度
                    if (StringUtil.isNotEmpty(site.getSiteReadwriteObject())) {
                        JSONObject jsonObject = JSON.parseObject(site.getSiteReadwriteObject());
                        if (jsonObject.containsKey(SiteFieldParamVo.LOCATION)) {
                            JSONObject location = jsonObject.getJSONObject(SiteFieldParamVo.LOCATION);
                            if (location.containsKey(SiteFieldParamVo.LONGITUDE) && location.containsKey(SiteFieldParamVo.LATITUDE)) {
                                siteInfoDto.setCoordinate(location.getString(SiteFieldParamVo.LONGITUDE) + FileUtil.COMMA + location.getString(SiteFieldParamVo.LATITUDE));
                            }
                            if (location.containsKey(SiteFieldParamVo.ADDRESS)) {
                                siteInfoDto.setAddress(location.getString(SiteFieldParamVo.ADDRESS));
                            }
                        }
                    }
                    return siteInfoDto;
                }).collect(Collectors.toList())); //光伏站点信息列表

                systemIdMap.put(1, pvSystemIds); //光伏系统id列表

            }
            //2.获取储能数据
            List<SiteInfoDto> storageSiteList = siteInfoList.stream().filter(s -> s.getScenarioTypes().contains("2")).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(storageSiteList)) {

                storageData.setSiteNum(storageSiteList.size()); //储能站点数量
                //定义储能系统id
                List<String> seSystemIds = Lists.newArrayList();
                storageData.setSiteList(storageSiteList.stream().map(site -> {
                    HomePageWebSocketDto.SiteInfoDto siteInfoDto = new HomePageWebSocketDto.SiteInfoDto();
                    siteInfoDto.setId(site.getId());
                    siteInfoDto.setSiteName(site.getSiteName());
                    siteInfoDto.setScenarioTypes(site.getScenarioTypes());
                    //获取站点类型
                    //站点类型(默认给0)
                    siteInfoDto.setSiteType(0);
                    if (CollectionUtils.isNotEmpty(site.getSiteScenarioTypeDtos())) {
                        List<SiteScenarioTypeDto> scenarioTypeList = site.getSiteScenarioTypeDtos().stream().filter(s -> StringUtil.isNotEmpty(s.getScenarioType())
                                && s.getScenarioType() == 2).collect(Collectors.toList());
                        if (CollectionUtils.isNotEmpty(scenarioTypeList)) {
                            seSystemIds.addAll(scenarioTypeList.stream().map(SiteScenarioTypeDto::getId).collect(Collectors.toList()));
                            //站点类型
                            SiteScenarioTypeDto scenarioType = scenarioTypeList.get(0);
                            if (StringUtil.isNotEmpty(scenarioType) && StringUtil.isNotEmpty(scenarioType.getReadwriteObject())) {
                                JSONObject jsonObject = JSON.parseObject(scenarioType.getReadwriteObject());
                                if (jsonObject.containsKey(SiteFieldParamVo.STORAGE_TYPE)) {
                                    siteInfoDto.setSiteType(jsonObject.getInteger(SiteFieldParamVo.STORAGE_TYPE)); //储能站点类型
                                }
                            }
                        }
                    }
                    //获取经纬度
                    if (StringUtil.isNotEmpty(site.getSiteReadwriteObject())) {
                        JSONObject jsonObject = JSON.parseObject(site.getSiteReadwriteObject());
                        if (jsonObject.containsKey(SiteFieldParamVo.LOCATION)) {
                            JSONObject location = jsonObject.getJSONObject(SiteFieldParamVo.LOCATION);
                            if (location.containsKey(SiteFieldParamVo.LONGITUDE) && location.containsKey(SiteFieldParamVo.LATITUDE)) {
                                siteInfoDto.setCoordinate(location.getString(SiteFieldParamVo.LONGITUDE) + FileUtil.COMMA + location.getString(SiteFieldParamVo.LATITUDE));
                            }
                            if (location.containsKey(SiteFieldParamVo.ADDRESS)) {
                                siteInfoDto.setAddress(location.getString(SiteFieldParamVo.ADDRESS));
                            }
                        }
                    }
                    return siteInfoDto;
                }).collect(Collectors.toList())); //储能站点信息列表

                systemIdMap.put(2, seSystemIds); //储能系统id
            }
            //3.获取充电桩数据
            List<SiteInfoDto> pileSiteList = siteInfoList.stream().filter(s -> s.getScenarioTypes().contains("3")).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(pileSiteList)) {

                pileData.setSiteNum(pileSiteList.size()); //充电桩站点数量
                //定义充电场站系统id列表
                List<String> chargeSystemIds = Lists.newArrayList();
                pileData.setSiteList(pileSiteList.stream().map(site -> {
                    HomePageWebSocketDto.SiteInfoDto siteInfoDto = new HomePageWebSocketDto.SiteInfoDto();
                    siteInfoDto.setId(site.getId());
                    siteInfoDto.setSiteName(site.getSiteName());
                    siteInfoDto.setScenarioTypes(site.getScenarioTypes());
                    //获取站点类型
                    //站点类型(默认给0)
                    siteInfoDto.setSiteType(0);
                    if (CollectionUtils.isNotEmpty(site.getSiteScenarioTypeDtos())) {
                        List<SiteScenarioTypeDto> scenarioTypeList = site.getSiteScenarioTypeDtos().stream().filter(s -> StringUtil.isNotEmpty(s.getScenarioType())
                                && s.getScenarioType() == 3).collect(Collectors.toList());
                        if (CollectionUtils.isNotEmpty(scenarioTypeList)) {
                            chargeSystemIds.addAll(scenarioTypeList.stream().map(SiteScenarioTypeDto::getId).collect(Collectors.toList()));
                            //站点类型
                            SiteScenarioTypeDto scenarioType = scenarioTypeList.get(0);
                            if (StringUtil.isNotEmpty(scenarioType) && StringUtil.isNotEmpty(scenarioType.getReadwriteObject())) {
                                JSONObject jsonObject = JSON.parseObject(scenarioType.getReadwriteObject());
                                if (jsonObject.containsKey(SiteFieldParamVo.CHARGE_TYPE)) {
                                    siteInfoDto.setSiteType(jsonObject.getInteger(SiteFieldParamVo.CHARGE_TYPE)); //充电桩站点类型
                                }
                            }
                        }
                    }
                    //获取经纬度
                    if (StringUtil.isNotEmpty(site.getSiteReadwriteObject())) {
                        JSONObject jsonObject = JSON.parseObject(site.getSiteReadwriteObject());
                        if (jsonObject.containsKey(SiteFieldParamVo.LOCATION)) {
                            JSONObject location = jsonObject.getJSONObject(SiteFieldParamVo.LOCATION);
                            if (location.containsKey(SiteFieldParamVo.LONGITUDE) && location.containsKey(SiteFieldParamVo.LATITUDE)) {
                                siteInfoDto.setCoordinate(location.getString(SiteFieldParamVo.LONGITUDE) + FileUtil.COMMA + location.getString(SiteFieldParamVo.LATITUDE));
                            }
                            if (location.containsKey(SiteFieldParamVo.ADDRESS)) {
                                siteInfoDto.setAddress(location.getString(SiteFieldParamVo.ADDRESS));
                            }
                        }
                    }
                    return siteInfoDto;
                }).collect(Collectors.toList())); //充电站点信息列表

                systemIdMap.put(3, chargeSystemIds); //充电场站系统id列表
            }

            //4.获取换电数据
            List<SiteInfoDto> changeSiteList = siteInfoList.stream().filter(s -> s.getScenarioTypes().contains("6")).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(changeSiteList)) {

                changeData.setChangeSiteNum(changeSiteList.size());
//                changeData.setTotalChangeQt();
//                changeData.setTotalChangeNum();
                //定义换电系统id列表
                List<String> changeSystemIds = Lists.newArrayList();
                changeData.setSiteList(changeSiteList.stream().map(site -> {
                    HomePageWebSocketDto.SiteInfoDto siteInfoDto = new HomePageWebSocketDto.SiteInfoDto();
                    siteInfoDto.setId(site.getId());
                    siteInfoDto.setSiteName(site.getSiteName());
                    siteInfoDto.setScenarioTypes(site.getScenarioTypes());
                    siteInfoDto.setSiteType(0); //换电系统站点类型(默认给0)
                    //获取换电站系统id
                    if (CollectionUtils.isNotEmpty(site.getSiteScenarioTypeDtos())) {
                        List<SiteScenarioTypeDto> scenarioTypeList = site.getSiteScenarioTypeDtos().stream().filter(s -> StringUtil.isNotEmpty(s.getScenarioType())
                                && s.getScenarioType() == 6).collect(Collectors.toList());
                        if (CollectionUtils.isNotEmpty(scenarioTypeList)) {
                            changeSystemIds.addAll(scenarioTypeList.stream().map(SiteScenarioTypeDto::getId).collect(Collectors.toList()));
                        }
                    }
                    //获取经纬度
                    if (StringUtil.isNotEmpty(site.getSiteReadwriteObject())) {
                        JSONObject jsonObject = JSON.parseObject(site.getSiteReadwriteObject());
                        if (jsonObject.containsKey(SiteFieldParamVo.LOCATION)) {
                            JSONObject location = jsonObject.getJSONObject(SiteFieldParamVo.LOCATION);
                            if (location.containsKey(SiteFieldParamVo.LONGITUDE) && location.containsKey(SiteFieldParamVo.LATITUDE)) {
                                siteInfoDto.setCoordinate(location.getString(SiteFieldParamVo.LONGITUDE) + FileUtil.COMMA + location.getString(SiteFieldParamVo.LATITUDE));
                            }
                            if (location.containsKey(SiteFieldParamVo.ADDRESS)) {
                                siteInfoDto.setAddress(location.getString(SiteFieldParamVo.ADDRESS));
                            }
                        }
                    }
                    return siteInfoDto;
                }).collect(Collectors.toList()));

                systemIdMap.put(6, changeSystemIds); //换电站系统id列表

            }

            //对系统类型不同的设备处理
            List<String> systemIds = systemIdMap.values().stream().flatMap(Collection::stream).collect(Collectors.toList());
            Map<String, List<DeviceBasicInfoDto>> deviceMap = deviceService.findDeviceInfoByParentIds(systemIds).getData();

            //获取光伏逆变器数量和光伏累计发电量
            if (systemIdMap.containsKey(1) && CollectionUtils.isNotEmpty(systemIdMap.get(1))) {
                Set<String> inverterIds = systemIdMap.get(1).stream().filter(deviceMap::containsKey).map(deviceMap::get)
                        .flatMap(s -> s.stream().filter(d -> StringUtil.isNotEmpty(d.getTypeId())
                                && (Objects.equals(d.getTypeId(), "20") || Objects.equals(d.getTypeId(), "77"))))
                                .map(DeviceBasicInfoDto::getId).collect(Collectors.toSet());
                pvData.setInverterNum(inverterIds.size()); //逆变器数量

                if (CollectionUtils.isNotEmpty(inverterIds)) {
                    pvData.setTotalQt(DoubleUtil.getToDouble(deviceService.getDeviceFunctionsRealDataByIds(inverterIds, FunctionLogoParamVo.TOTAL_POWER_GENERATION,
                                    1).getData().values().stream().flatMap(i -> i.values()
                            .stream()).mapToDouble(d -> DoubleUtil.objToDouble(d.getDataValue())).sum())); //总发电量
                }
            }

            //获取储能数量,PCS额定功率,PCS额定容量,电池簇额定容量,电池簇额定功率,电池簇累计充电量,电池簇累计放电量
            if (systemIdMap.containsKey(2) && CollectionUtils.isNotEmpty(systemIdMap.get(2))) {

                //根据多个储能系统id查询储能PCS数据和电池簇数据
                Set<DeviceBasicInfoDto> deviceList = systemIdMap.get(2).stream().filter(deviceMap::containsKey).map(deviceMap::get)
                        .flatMap(s -> s.stream().filter(d -> StringUtil.isNotEmpty(d.getTypeId())))
                        .collect(Collectors.toSet());
                List<DeviceBasicInfoDto> devicePcsList = deviceList.stream().filter(d -> StringUtil.isNotEmpty(d.getTypeId())
                        && (Objects.equals(d.getTypeId(), "23") || Objects.equals(d.getTypeId(), "78"))).collect(Collectors.toList());
                List<DeviceBasicInfoDto> deviceBatteryList = deviceList.stream().filter(d -> StringUtil.isNotEmpty(d.getTypeId())
                        && Objects.equals(d.getTypeId(), "25")).collect(Collectors.toList());
                if (CollectionUtils.isNotEmpty(devicePcsList)) {
                    storageData.setStorageNum(devicePcsList.size()); //储能柜数量
                    storageData.setPcsRatedPower(DoubleUtil.getToDouble(devicePcsList.stream().filter(d -> StringUtil.isNotEmpty(d.getReadwriteObject())).mapToDouble(d -> {
                        JSONObject jsonObject = JSON.parseObject(d.getReadwriteObject());
                        if (jsonObject.containsKey(ReaFieldParamVo.RATED_POWER)) {
                            return DoubleUtil.objToDouble(jsonObject.get(ReaFieldParamVo.RATED_POWER));
                        }
                        return 0;
                    }).sum())); //储能PCS额定功率
                    //根据多个PCS设备的id查询PCS累计充电量和累计放电量
                    Set<String> pcsIds = devicePcsList.stream().map(DeviceBasicInfoDto::getId).collect(Collectors.toSet());
                    String functionLogos = String.join(FileUtil.COMMA, FunctionLogoParamVo.PCS_BATTERY_CHARGE, FunctionLogoParamVo.PCS_BATTERY_DISCHARGE);
                    Map<String, Map<String, RealDataModel>> pcsRealDataMap = deviceService.getDeviceFunctionsRealDataByIds(pcsIds, functionLogos, 1).getData();
                    //储能当前功率,储能总充电量,储能总放电量
                    for (Map<String, RealDataModel> entity : pcsRealDataMap.values()) {
                        entity.forEach((key, value) -> {
                            //储能总充电量
                            if (Objects.equals(key, FunctionLogoParamVo.PCS_BATTERY_CHARGE) && StringUtil.isNotEmpty(value.getDataValue())) {
                                storageData.setTotalChargeQt(storageData.getTotalChargeQt() + DoubleUtil.objToDouble(value.getDataValue()));
                            }
                            //储能总放电量
                            if (Objects.equals(key, FunctionLogoParamVo.PCS_BATTERY_DISCHARGE) && StringUtil.isNotEmpty(value.getDataValue())) {
                                storageData.setTotalDisChargeQt(storageData.getTotalDisChargeQt() + DoubleUtil.objToDouble(value.getDataValue()));
                            }
                        });
                    }
                    storageData.setTotalChargeQt(DoubleUtil.getToDouble(storageData.getTotalChargeQt())); //储能累计充电量
                    storageData.setTotalDisChargeQt(DoubleUtil.getToDouble(storageData.getTotalDisChargeQt())); //储能累计放电量
                }
                if (CollectionUtils.isNotEmpty(deviceBatteryList)) {
                    storageData.setBatteryRatedCapacity(DoubleUtil.getToDouble(deviceBatteryList.stream().filter(d -> StringUtil.isNotEmpty(d.getReadwriteObject())).mapToDouble(d -> {
                        if (d.getReaMap().containsKey(ReaFieldParamVo.RATED_CAP)) {
                            return DoubleUtil.objToDouble(d.getReaMap().get(ReaFieldParamVo.RATED_CAP));
                        }
                        return 0.0;
                    }).sum())); //储能电池簇额定容量
                }
            }

            //充电桩设备相关数据
            List<String> pileTypeIds = Arrays.asList("28", "29", "30", "79");
            //获取充电场站设备数量
            if (systemIdMap.containsKey(3) && CollectionUtils.isNotEmpty(systemIdMap.get(3))) {
                //根据多个充电场站系统id查询充电桩设备数据
                Set<DeviceBasicInfoDto> chargePileList = systemIdMap.get(3).stream().filter(deviceMap::containsKey).map(deviceMap::get)
                        .flatMap(s -> s.stream().filter(d -> StringUtil.isNotEmpty(d.getTypeId())
                                && pileTypeIds.contains(d.getTypeId()))).collect(Collectors.toSet());
                pileData.setPileNum(chargePileList.size()); //充电场站桩数量
                Set<String> pileCodes = chargePileList.stream().map(DeviceBasicInfoDto::getDeviceNumber).filter(StringUtil::isNotEmpty).collect(Collectors.toSet());
                if (CollectionUtils.isNotEmpty(pileCodes)) {
                    //电桩总充电量,总充电次数
                    ChargeOrderQtModel chargeOrder = orderRecordMapper.countOrderTotalQtByPileCodes(pileCodes, 0);
                    if (chargeOrder != null) {
                        pileData.setTotalChargeQt(DoubleUtil.getToDouble(chargeOrder.getTotalQt())); //电桩累计充电量
                        pileData.setTotalChargeNum(chargeOrder.getTotalCount()); //电桩累计充电次数
                    }
                    //电桩总放电量,总放电次数
                    ChargeOrderQtModel dischargeOrder = orderRecordMapper.countOrderTotalQtByPileCodes(pileCodes, 1);
                    if (dischargeOrder != null) {
                        pileData.setTotalDisChargeQt(DoubleUtil.getToDouble(dischargeOrder.getTotalQt())); //电桩累计放电量
                        pileData.setTotalDisChargeNum(dischargeOrder.getTotalCount()); //电桩累计放电次数
                    }
                }
            }

            //获取换电站总装机容量
            if (systemIdMap.containsKey(6) && CollectionUtils.isNotEmpty(systemIdMap.get(6))) {
                //根据多个换电系统id查询换电仓设备
                Set<DeviceBasicInfoDto> deviceGranaryList = systemIdMap.get(6).stream().filter(deviceMap::containsKey).map(deviceMap::get)
                        .flatMap(s -> s.stream().filter(d -> StringUtil.isNotEmpty(d.getTypeId())
                                && Objects.equals(d.getTypeId(), "70"))).collect(Collectors.toSet());
                if (CollectionUtils.isNotEmpty(deviceGranaryList)) {
                    changeData.setChangeCapacity(DoubleUtil.getToDouble(deviceGranaryList.stream().mapToDouble(d -> {
                        if (d.getReaMap().containsKey(ReaFieldParamVo.RATED_POWER)) {
                            return DoubleUtil.objToDouble(d.getReaMap().get(ReaFieldParamVo.RATED_POWER));
                        }
                        return 0.0;
                    }).sum())); //换电站总装机容量
                }
            }
            result.setPvData(pvData);
            result.setStorageData(storageData);
            result.setPileData(pileData);
            result.setChangeData(changeData);

        }
        return result;
    }

}
