package com.sunmax.together.service.operation.impl;

import com.alibaba.fastjson.JSON;
import com.sunmax.common.config.redis.RedisDeviceUtil;
import com.sunmax.common.config.redis.RedisGeneralUtil;
import com.sunmax.common.dto.device.DeviceBasicInfoDto;
import com.sunmax.common.dto.device.DeviceGunInfoDto;
import com.sunmax.common.dto.device.SiteInfoDto;
import com.sunmax.common.dto.operate.OrderRecordDto;
import com.sunmax.common.dto.system.OrganEmpowerListDto;
import com.sunmax.common.model.general.PileRealModel;
import com.sunmax.common.util.DateUtil;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.util.StringUtil;
import com.sunmax.common.vo.together.PileGunChangeVo;
import com.sunmax.together.dto.operation.runScene.PileDetailDto;
import com.sunmax.together.dto.operation.runScene.SitePileMonitorDto;
import com.sunmax.together.service.operation.OrderRecordService;
import com.sunmax.together.service.operation.RunSceneService;
import com.sunmax.together.service.feign.DeviceService;
import com.sunmax.together.service.feign.SystemService;
import com.sunmax.together.vo.operation.runScene.SitePileMonitorQueryVo;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static com.sunmax.common.util.DateUtil.*;

@Service
public class RunSceneServiceImpl implements RunSceneService {

    @Autowired
    private SystemService systemService;

    @Autowired
    private DeviceService deviceService;

    @Autowired
    private OrderRecordService orderRecordService;

    @Override
    public ResponseResult<SitePileMonitorDto> countSitePileMonitor(SitePileMonitorQueryVo sitePileQueryVo) {
        //返回的对象
        SitePileMonitorDto result = new SitePileMonitorDto();
        //获取多个站点id
        List<String> siteIds;
        if (StringUtil.isEmpty(sitePileQueryVo.getSiteIds())) {
            siteIds = systemService.findAllOrganEmpowerByUserId(sitePileQueryVo.getUserId()).getData()
                    .stream().map(OrganEmpowerListDto::getSiteId).distinct().collect(Collectors.toList());
        } else {
            siteIds = JSON.parseArray(sitePileQueryVo.getSiteIds(), String.class);
        }
        if (CollectionUtils.isNotEmpty(siteIds)) {
            //根据多个站点id查询站点基本信息数据列表
            List<SiteInfoDto> siteInfoDtoList = new ArrayList<>(deviceService.findSiteBasicInfoByIds(siteIds).getData().values()).stream().filter(s -> StringUtil.isNotEmpty(s.getScenarioTypes()) && s.getScenarioTypes().contains(String.valueOf(sitePileQueryVo.getScenarioTypes()))).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(siteInfoDtoList)) {
                Map<String, SiteInfoDto> siteInfoMap = siteInfoDtoList.stream().collect(Collectors.toMap(SiteInfoDto::getId, s -> s, (o, n) -> o));
                //根据多个站点id查询设备数据列表
                Map<String, List<DeviceBasicInfoDto>> sitePileMap = deviceService.findDeviceBasicInfoBySiteIds(siteIds, 1).getData();
                //根据多个设备id查询设备枪数据列表
                List<String> pileIds = sitePileMap.entrySet().stream().flatMap(s -> s.getValue().stream().map(DeviceBasicInfoDto::getId))
                        .distinct().collect(Collectors.toList());
                Map<String, List<DeviceGunInfoDto>> pileGunMap = deviceService.findDeviceGunInfoByDeviceIds(pileIds).getData();

                //根据多个电桩编号查询充电订单数据
                List<String> pileCodes = sitePileMap.entrySet().stream().flatMap(s -> s.getValue().stream().map(DeviceBasicInfoDto::getDeviceNumber)
                        .filter(StringUtil::isNotEmpty)).distinct().collect(Collectors.toList());
                String dateToStr = localDateToStr(LocalDate.now());
                Map<String, List<OrderRecordDto>> pileOrderMap = orderRecordService.findOrderRecordListByPileCodes(pileCodes,
                                getDayStart(dateToStr), getDayEnd(dateToStr)).getData().stream()
                        .filter(o -> StringUtil.isNotEmpty(o.getRunMode()) && o.getRunMode() == 0)
                        .collect(Collectors.groupingBy(OrderRecordDto::getPileCode));

                //对数据进行组装
                siteInfoMap.forEach((siteId, siteInfo) -> {
                    SitePileMonitorDto.SitePile sitePile = new SitePileMonitorDto.SitePile();
                    sitePile.setSiteId(siteId);
                    sitePile.setSiteName(siteInfo.getSiteName());
                    //对站点下面的电桩枪的数据进行组装
                    if (sitePileMap.containsKey(siteId)) {
                        sitePileMap.get(siteId).forEach(pileInfo -> {
                            if (StringUtil.isNotEmpty(pileInfo.getDeviceNumber())) {
                                Integer txStatus = Objects.requireNonNull(RedisDeviceUtil.getDevice(pileInfo.getDeviceNumber())).getTxStatus();
                                PileRealModel pileRealModel = RedisGeneralUtil.getPileRealModel(pileInfo.getDeviceNumber());
                                if (pileGunMap.containsKey(pileInfo.getId())) {
                                    pileGunMap.get(pileInfo.getId()).forEach(gunInfo -> {
                                        SitePileMonitorDto.PileGun pileGun = new SitePileMonitorDto.PileGun();
                                        pileGun.setPileId(pileInfo.getId());
                                        pileGun.setPileCode(pileInfo.getDeviceNumber());
                                        pileGun.setGunCode(gunInfo.getGunCode());
                                        pileGun.setPileType(pileInfo.getTypeId());
                                        pileGun.setRatedPower(gunInfo.getRatedPower());
                                        pileGun.setGunWorkState(StringUtil.convertGunStatus(pileRealModel, txStatus, gunInfo.getGunCode()));
                                        if (pileRealModel != null && pileRealModel.getGunRealModelMap().containsKey(gunInfo.getGunCode())) {
                                            PileRealModel.GunRealModel gunRealModel = pileRealModel.getGunRealModelMap().get(gunInfo.getGunCode());
                                            pileGun.setBatterySOC(gunRealModel.getBatterySoc());
                                            pileGun.setOriginalGunWorkState(gunRealModel.getGunStatus());
                                        }
                                        sitePile.getPileGunList().add(pileGun);
                                    });
                                }
                            }
                        });
                    }
                    result.getSitePileList().add(sitePile);
                });
                //查询条件
                result.setSitePileList(result.getSitePileList().stream().peek(sitePile -> {
                    //统计状态 全部 -1-未知 1-充电 2-放电 3-空闲 4-占用 5-故障 6-离线 7-未注册 8-预约中
                    result.setWhole(result.getWhole() + sitePile.getPileGunList().size());
                    result.setUnknown(result.getUnknown() + sitePile.getPileGunList().stream().filter(pileGun -> Objects.equals(pileGun.getGunWorkState(), -1)).count());
                    result.setCharge(result.getCharge() + sitePile.getPileGunList().stream().filter(pileGun -> Objects.equals(pileGun.getGunWorkState(), 1)).count());
                    result.setDischarge(result.getDischarge() + sitePile.getPileGunList().stream().filter(pileGun -> Objects.equals(pileGun.getGunWorkState(), 2)).count());
                    result.setIdle(result.getIdle() + sitePile.getPileGunList().stream().filter(pileGun -> Objects.equals(pileGun.getGunWorkState(), 3)).count());
                    result.setEmploy(result.getEmploy() + sitePile.getPileGunList().stream().filter(pileGun -> Objects.equals(pileGun.getGunWorkState(), 4)).count());
                    result.setFault(result.getFault() + sitePile.getPileGunList().stream().filter(pileGun -> Objects.equals(pileGun.getGunWorkState(), 5)).count());
                    result.setOffline(result.getOffline() + sitePile.getPileGunList().stream().filter(pileGun -> Objects.equals(pileGun.getGunWorkState(), 6)).count());
                    result.setUnregistered(result.getUnregistered() + sitePile.getPileGunList().stream().filter(pileGun -> Objects.equals(pileGun.getGunWorkState(), 7)).count());
                    result.setReservation(result.getReservation() + sitePile.getPileGunList().stream().filter(pileGun -> Objects.equals(pileGun.getGunWorkState(), 8)).count());

                    List<SitePileMonitorDto.PileGun> pileGunList = sitePile.getPileGunList();
                    if (StringUtil.isNotEmpty(sitePileQueryVo.getStatus())) {
                        pileGunList = pileGunList.stream().filter(pileGun -> Objects.equals(pileGun.getGunWorkState(), sitePileQueryVo.getStatus())).collect(Collectors.toList());
                    }
                    sitePile.setPileGunList(pileGunList);
                    //统计站点下面的充电量和充电金额
                    pileGunList.forEach(pileGun -> {
                        if (pileOrderMap.containsKey(pileGun.getPileCode())) {
                            List<OrderRecordDto> orderRecordList = pileOrderMap.get(pileGun.getPileCode()).stream().filter(o ->
                                    Objects.equals(String.valueOf(o.getGunCode()), pileGun.getGunCode())).collect(Collectors.toList());
                            double todayCharge = orderRecordList.stream().filter(o -> StringUtil.isNotEmpty(o.getTotalQt()))
                                    .mapToDouble(OrderRecordDto::getTotalQt).sum();
                            sitePile.setTodayCharge(sitePile.getTodayCharge() + todayCharge);
                            BigDecimal todayChargeMoney = orderRecordList.stream().map(OrderRecordDto::getTotalCost)
                                    .filter(StringUtil::isNotEmpty).reduce(BigDecimal.ZERO, BigDecimal::add);
                            sitePile.setTodayChargeMoney(sitePile.getTodayChargeMoney().add(todayChargeMoney));
                        }
                    });
                    //保留两位小数
                    sitePile.setTodayCharge(BigDecimal.valueOf(sitePile.getTodayCharge()).setScale(2, RoundingMode.HALF_UP).doubleValue());
                    sitePile.setTodayChargeMoney(sitePile.getTodayChargeMoney().setScale(2, RoundingMode.HALF_UP));
                }).collect(Collectors.toList()));
            }
        }
        return ResponseResult.ok(result);
    }

    @Override
    public ResponseResult<PileDetailDto> findPileDetailById(String id) {
        //返回的对象
        PileDetailDto result = new PileDetailDto();

        //根据电桩id查询电桩详情数据
        Map<String, DeviceBasicInfoDto> deviceBasicInfoMap = deviceService.findDeviceBasicInfoByIds(Collections.singletonList(id)).getData();
        if (MapUtils.isNotEmpty(deviceBasicInfoMap) && deviceBasicInfoMap.containsKey(id)) {
            DeviceBasicInfoDto deviceInfo = deviceBasicInfoMap.get(id);
            BeanUtils.copyProperties(deviceInfo, result);
            if (StringUtil.isNotEmpty(deviceInfo.getCreateTime())) {
                result.setCreateTime(DateUtil.localDateTimeToStr(deviceInfo.getCreateTime()));
            }
            if (StringUtil.isNotEmpty(deviceInfo.getUpdateTime())) {
                result.setUpdateTime(DateUtil.localDateTimeToStr(deviceInfo.getUpdateTime()));
            }
            //获取扩展属性数据
            result.setDeviceReaList(deviceService.findDeviceReaListById(id).getData());

            //获取电桩枪的数据
            Map<String, List<DeviceGunInfoDto>> deviceGunMap = deviceService.findDeviceGunInfoByDeviceIds(Collections.singletonList(id)).getData();
            if (MapUtils.isNotEmpty(deviceGunMap) && deviceGunMap.containsKey(id)) {
                result.setGunDetailList(deviceGunMap.get(id).stream().map(gunInfo -> {
                    PileDetailDto.GunDetail gunDetail = new PileDetailDto.GunDetail();
                    BeanUtils.copyProperties(gunInfo, gunDetail);
                    return gunDetail;
                }).collect(Collectors.toList()));
            }
        }
        return ResponseResult.ok(result);
    }

    @Override
    public ResponseResult<Void> updatePileRea(String id, String userId, String readwriteObject) {
        return deviceService.updatePileRea(id, userId, readwriteObject);
    }

    @Override
    public ResponseResult<Void> updatePileGun(PileGunChangeVo pileGunChangeVo) {
        return deviceService.updatePileGun(pileGunChangeVo);
    }


}
