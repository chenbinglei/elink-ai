package com.sunmax.together.service.operation.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.google.common.collect.Maps;
import com.sunmax.common.dto.PageDto;
import com.sunmax.common.dto.device.DeviceBasicInfoDto;
import com.sunmax.common.dto.device.DeviceGunInfoDto;
import com.sunmax.common.dto.device.SiteInfoDto;
import com.sunmax.common.dto.protocol.AlarmRecordDto;
import com.sunmax.common.dto.system.OrganEmpowerListDto;
import com.sunmax.common.model.general.PileRealModel;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.util.StringUtil;
import com.sunmax.common.util.oss.FileUtil;
import com.sunmax.common.vo.DeviceTypeParamVo;
import com.sunmax.common.vo.ReaFieldParamVo;
import com.sunmax.common.vo.SiteFieldParamVo;
import com.sunmax.common.vo.protocol.AlarmRecordQueryVo;
import com.sunmax.common.vo.protocol.PileSetQrVo;
import com.sunmax.together.dto.operation.deviceManage.AlarmListDto;
import com.sunmax.together.dto.operation.deviceManage.PileGunListDto;
import com.sunmax.together.service.operation.DeviceManageService;
import com.sunmax.together.service.feign.DeviceService;
import com.sunmax.together.service.feign.ProtocolService;
import com.sunmax.together.service.feign.SystemService;
import com.sunmax.together.vo.operation.deviceManage.AlarmListQueryVo;
import com.sunmax.together.vo.operation.deviceManage.PileListQueryVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.compress.utils.Lists;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

import static com.sunmax.common.config.redis.RedisGeneralUtil.getPileRealModelList;
import static com.sunmax.common.util.DateUtil.secToTime;
import static com.sunmax.common.util.DateUtil.strToLocalDateTime;
import static com.sunmax.common.util.StringUtil.isNumeric;

@Slf4j
@Service
public class DeviceManageServiceImpl implements DeviceManageService {

    @Autowired
    private SystemService systemService;

    @Autowired
    private DeviceService deviceService;

    @Autowired
    private ProtocolService protocolService;

    @Override
    public ResponseResult<PageDto<PileGunListDto>> findPileListByPage(PileListQueryVo pileListQueryVo) {
        List<PileGunListDto> resultList = Lists.newArrayList();
        //根据当前登录用户id查询资产授权
        ResponseResult<List<OrganEmpowerListDto>> allOrganEmpowerByTenantId = systemService.findAllOrganEmpowerByUserId(pileListQueryVo.getUserId());
        if (allOrganEmpowerByTenantId.isSuccess() && CollectionUtils.isNotEmpty(allOrganEmpowerByTenantId.getData())) {
            List<OrganEmpowerListDto> organEmpowerList = allOrganEmpowerByTenantId.getData();
            //根据站点id查询数据
            if (StringUtil.isNotEmpty(pileListQueryVo.getSiteId())) {
                List<String> siteIds = Arrays.stream(pileListQueryVo.getSiteId().split(FileUtil.COMMA)).collect(Collectors.toList());
                organEmpowerList = organEmpowerList.stream().filter(o -> siteIds
                        .contains(o.getSiteId())).collect(Collectors.toList());
            }
            ResponseResult<Map<String, SiteInfoDto>> siteBasicInfoByIds = deviceService.findSiteBasicInfoByIds(
                    organEmpowerList.stream().map(OrganEmpowerListDto::getSiteId).distinct().collect(Collectors.toList()));
            if (siteBasicInfoByIds.isSuccess() && !siteBasicInfoByIds.getData().isEmpty()) {
                Map<String, SiteInfoDto> siteBasicInfoDtoMap = siteBasicInfoByIds.getData();
                List<SiteInfoDto> siteBasicInfoDtoList = com.google.common.collect.Lists.newArrayList(siteBasicInfoDtoMap.values());
                //根据区域查询
                if (StringUtil.isNotEmpty(pileListQueryVo.getAreaType()) && StringUtil.isNotEmpty(pileListQueryVo.getArea())) {
                    siteBasicInfoDtoList = siteBasicInfoDtoList.stream()
                            .filter(siteInfoDto -> {
                                Map<String, Object> readwriteMap = Maps.newHashMap();
                                String siteReadwriteObject = siteInfoDto.getSiteReadwriteObject();
                                if (StringUtil.isNotEmpty(siteReadwriteObject)) {
                                    Map<String, Object> parseObject = JSON.parseObject(siteReadwriteObject, new TypeReference<Map<String, Object>>() {});
                                    //站点位置信息对象
                                    if (parseObject.containsKey(SiteFieldParamVo.LOCATION)) {
                                        readwriteMap = JSON.parseObject(JSON.toJSONString(parseObject.get(SiteFieldParamVo.LOCATION)), new TypeReference<Map<String, Object>>() {
                                        });
                                    }
                                }
                                return matchesAreaType(readwriteMap, pileListQueryVo);
                            })
                            .collect(Collectors.toList());

                }
                if (CollectionUtils.isNotEmpty(siteBasicInfoDtoList)) {
                    //根据站点id查询电桩信息
                    List<String> siteIdList = siteBasicInfoDtoList.stream().map(SiteInfoDto::getId).collect(Collectors.toList());
                    ResponseResult<Map<String, List<DeviceBasicInfoDto>>> deviceBasicInfoBySiteIds = deviceService.findDeviceBasicInfoBySiteIds(siteIdList, DeviceTypeParamVo.CDZ);
                    if (deviceBasicInfoBySiteIds.isSuccess() && !deviceBasicInfoBySiteIds.getData().isEmpty()) {
                        List<DeviceBasicInfoDto> deviceBasicInfoDtoList = com.google.common.collect.Lists.newArrayList();
                        List<DeviceBasicInfoDto> finalDeviceBasicInfoDtoList = deviceBasicInfoDtoList;
                        deviceBasicInfoBySiteIds.getData().forEach((k, v) -> finalDeviceBasicInfoDtoList.addAll(v));
                        //根据桩名称/编码查询
                        if (StringUtil.isNotEmpty(pileListQueryVo.getPileName())) {
                            deviceBasicInfoDtoList = deviceBasicInfoDtoList.stream().filter(d -> (StringUtil.isNotEmpty(d.getDeviceName()) && d.getDeviceName().contains(pileListQueryVo.getPileName())) ||
                                    (StringUtil.isNotEmpty(d.getDeviceNumber()) && d.getDeviceNumber().contains(pileListQueryVo.getPileName()))).collect(Collectors.toList());
                        }
                        //根据电桩类型查询
                        if (StringUtil.isNotEmpty(pileListQueryVo.getTypeId())) {
                            deviceBasicInfoDtoList = deviceBasicInfoDtoList.stream().filter(d -> d.getTypeId().equals(pileListQueryVo.getTypeId())).collect(Collectors.toList());
                        }
                        if (!deviceBasicInfoDtoList.isEmpty()) {
                            //根据多个设备id查询电枪数据
                            List<String> deviceIdList = deviceBasicInfoDtoList.stream().map(DeviceBasicInfoDto::getId).filter(StringUtil::isNotEmpty).distinct().collect(Collectors.toList());
                            Map<String, List<DeviceGunInfoDto>> deviceGunInfoMap = deviceService.findDeviceGunInfoByDeviceIds(deviceIdList).getData();
                            //根据多个电桩编码查询实时数据
                            List<String> pileCodeList = deviceBasicInfoDtoList.stream().map(DeviceBasicInfoDto::getDeviceNumber).filter(StringUtil::isNotEmpty).distinct().collect(Collectors.toList());
                            Map<String, PileRealModel> pileRealModelMap = Objects.requireNonNull(getPileRealModelList(pileCodeList)).stream().collect(Collectors.toMap(PileRealModel::getPileCode, pileRealModel -> pileRealModel, (k1, k2) -> k1));

                            List<PileGunListDto> finalResultList = resultList;
                            deviceBasicInfoDtoList.forEach(deviceBasicInfoDto -> {
                                PileGunListDto pileGunListDto = new PileGunListDto();
                                BeanUtils.copyProperties(deviceBasicInfoDto, pileGunListDto);
                                Map<String, Object> reaMap = deviceBasicInfoDto.getReaMap();
                                //额定功率
                                if (reaMap.containsKey(ReaFieldParamVo.RATED_POWER)) {
                                    Object ratedPowerObj = reaMap.get(ReaFieldParamVo.RATED_POWER);
                                    if (StringUtil.isNotEmpty(ratedPowerObj) && isNumeric(String.valueOf(ratedPowerObj))) {
                                        pileGunListDto.setRatedPower(Double.parseDouble(String.valueOf(ratedPowerObj)));
                                    }
                                }
                                //生产厂商
                                if (reaMap.containsKey(ReaFieldParamVo.MANUFACTURER_NAME)) {
                                    Object manufacturerObj = reaMap.get(ReaFieldParamVo.MANUFACTURER_NAME);
                                    if (StringUtil.isNotEmpty(manufacturerObj) && !isNumeric(String.valueOf(manufacturerObj))) {
                                        pileGunListDto.setManufacturer(String.valueOf(manufacturerObj));
                                    }
                                }
                                pileGunListDto.setWorkStatus(88);
                                //查询电桩实时数据
                                if (pileRealModelMap.containsKey(deviceBasicInfoDto.getDeviceNumber())) {
                                    //获取电桩实时数据
                                    PileRealModel pileRealModel = pileRealModelMap.get(deviceBasicInfoDto.getDeviceNumber());
                                    pileGunListDto.setWorkStatus(pileRealModel.getWorkStatus());
                                    //获取模型电枪数据
                                    List<DeviceGunInfoDto> deviceGunInfoDtoList = deviceGunInfoMap.get(deviceBasicInfoDto.getId());
                                    if (CollectionUtils.isNotEmpty(deviceGunInfoDtoList)) {
                                        List<PileGunListDto.GunStateInfo> gunStateInfoList = Lists.newArrayList();
                                        deviceGunInfoDtoList.forEach(modelGunInfoDto -> {
                                            PileGunListDto.GunStateInfo gunStateInfo = new PileGunListDto.GunStateInfo();
                                            BeanUtils.copyProperties(modelGunInfoDto, gunStateInfo);
                                            Integer gunWorkState = StringUtil.convertGunStatus(pileRealModel, modelGunInfoDto.getGunCode());
                                            gunStateInfo.setGunWorkState(gunWorkState);
                                            gunStateInfoList.add(gunStateInfo);
                                        });
                                        pileGunListDto.setGunStateInfoList(gunStateInfoList);
                                    }
                                }
                                finalResultList.add(pileGunListDto);
                            });
                            //根据电桩状态查询
                            if (StringUtil.isNotEmpty(pileListQueryVo.getWorkStatus())) {
                                resultList = resultList.stream().filter(pileInfo -> StringUtil.isNotEmpty(pileInfo.getWorkStatus()) && pileInfo.getWorkStatus().equals(pileListQueryVo.getWorkStatus())).collect(Collectors.toList());
                            }
                            //根据电枪状态查询
                            if (StringUtil.isNotEmpty(pileListQueryVo.getGunWorkState())) {
                                resultList = resultList.stream().filter(pileInfo -> CollectionUtils.isNotEmpty(pileInfo.getGunStateInfoList()) && pileInfo.getGunStateInfoList().stream().anyMatch(gunStateInfo -> StringUtil.isNotEmpty(gunStateInfo.getGunWorkState()) && gunStateInfo.getGunWorkState().equals(pileListQueryVo.getGunWorkState()))).collect(Collectors.toList());
                            }
                            //根据厂商查询
                            if (StringUtil.isNotEmpty(pileListQueryVo.getManufacturersName())) {
                                resultList = resultList.stream().filter(pileInfo -> StringUtil.isNotEmpty(pileInfo.getManufacturer()) && pileInfo.getManufacturer().contains(pileListQueryVo.getManufacturersName())).collect(Collectors.toList());
                            }
                        }
                    }
                }
            }
        }
        return ResponseResult.ok(new PageDto<>(resultList, pileListQueryVo.getPage(), pileListQueryVo.getSize()));
    }

    @Override
    public ResponseResult<String> updateDeviceOperateStatus(String deviceId, Integer operateStatus) {
        return deviceService.updateDeviceOperateStatus(deviceId, operateStatus);
    }

    boolean matchesAreaType(Map<String, Object> readwriteMap, PileListQueryVo pileListQueryVo) {
        String areaKey = pileListQueryVo.getAreaType() == 1 ? SiteFieldParamVo.PROVINCE : SiteFieldParamVo.CITY;
        Object areaValue = readwriteMap.get(areaKey);

        return areaValue != null && areaValue.equals(pileListQueryVo.getArea());
    }

    @Override
    public ResponseResult<?> findAlarmListByPage(AlarmListQueryVo alarmListQueryVo) {
        List<AlarmListDto> resultList = Lists.newArrayList();
        //根据当前登录用户id查询资产授权
        try{
            ResponseResult<List<OrganEmpowerListDto>> allOrganEmpowerByTenantId = systemService.findAllOrganEmpowerByUserId(alarmListQueryVo.getUserId());
            if (allOrganEmpowerByTenantId.isSuccess() && CollectionUtils.isNotEmpty(allOrganEmpowerByTenantId.getData())) {
                List<OrganEmpowerListDto> organEmpowerList = allOrganEmpowerByTenantId.getData();
                //根据站点id查询数据
                if (StringUtil.isNotEmpty(alarmListQueryVo.getSiteId())) {
                    organEmpowerList = organEmpowerList.stream().filter(o -> o.getSiteId().equals(alarmListQueryVo.getSiteId())).collect(Collectors.toList());
                }
                ResponseResult<Map<String, SiteInfoDto>> siteBasicInfoByIds = deviceService.findSiteBasicInfoByIds(
                        organEmpowerList.stream().map(OrganEmpowerListDto::getSiteId).distinct().collect(Collectors.toList()));
                if (siteBasicInfoByIds.isSuccess() && !siteBasicInfoByIds.getData().isEmpty()) {
                    List<SiteInfoDto> siteBasicInfoDtoList = new ArrayList<>(siteBasicInfoByIds.getData().values());

                    //根据站点id查询电桩信息
                    List<String> siteIdList = siteBasicInfoDtoList.stream().map(SiteInfoDto::getId).collect(Collectors.toList());
                    Map<String, List<DeviceBasicInfoDto>> deviceMap = deviceService.findDeviceBasicInfoBySiteIds(siteIdList, DeviceTypeParamVo.CDZ).getData();
                    if (MapUtils.isNotEmpty(deviceMap)) {
                        List<DeviceBasicInfoDto> deviceBasicInfoDtoList = deviceMap.values().stream().flatMap(Collection::stream).collect(Collectors.toList());
                        //根据桩编码查询
                        if (StringUtil.isNotEmpty(alarmListQueryVo.getPileCode())) {
                            deviceBasicInfoDtoList = deviceBasicInfoDtoList.stream().filter(d -> StringUtil.isNotEmpty(d.getDeviceNumber()) &&
                                    d.getDeviceNumber().contains(alarmListQueryVo.getPileCode())).collect(Collectors.toList());
                        }
                        if (CollectionUtils.isNotEmpty(deviceBasicInfoDtoList)) {
                            //获取告警列表
                            AlarmRecordQueryVo alarmQueryVo = new AlarmRecordQueryVo();
                            alarmQueryVo.setDeviceCodes(deviceBasicInfoDtoList.stream().map(DeviceBasicInfoDto::getDeviceNumber).collect(Collectors.toSet()));
                            alarmQueryVo.setStartDate(alarmListQueryVo.getAlarmStartDate());
                            alarmQueryVo.setEndDate(alarmListQueryVo.getAlarmEndDate());
                            alarmQueryVo.setEventLevel(alarmListQueryVo.getEventLevel());
                            alarmQueryVo.setAlarmStatus(alarmListQueryVo.getAlarmStatus());
                            alarmQueryVo.setFaultCode(alarmListQueryVo.getFaultCode());
                            ResponseResult<List<AlarmRecordDto>> serviceAlarmRecordList = protocolService.findAlarmRecordList(alarmQueryVo);
                            if (serviceAlarmRecordList.isSuccess() && CollectionUtils.isNotEmpty(serviceAlarmRecordList.getData())) {
                                //电桩设备信息根据编码转换成map
                                Map<String, DeviceBasicInfoDto> deviceBasicInfoDtoMap = deviceBasicInfoDtoList.stream().collect(Collectors.toMap(DeviceBasicInfoDto::getDeviceNumber, deviceBasicInfoDto -> deviceBasicInfoDto, (k1, k2) -> k1));
                                resultList = serviceAlarmRecordList.getData().stream().map(alarmRecordDto -> {
                                    AlarmListDto alarmListDto = new AlarmListDto();
                                    BeanUtils.copyProperties(alarmRecordDto, alarmListDto);
                                    DeviceBasicInfoDto deviceBasicInfoDto = deviceBasicInfoDtoMap.get(alarmRecordDto.getDeviceCode());
                                    alarmListDto.setSiteId(deviceBasicInfoDto.getSiteId());
                                    alarmListDto.setSiteName(deviceBasicInfoDto.getSiteName());
                                    //计算故障时长
                                    if (StringUtil.isNotEmpty(alarmRecordDto.getCreateTime()) && StringUtil.isNotEmpty(alarmRecordDto.getUpdateTime())) {
                                        alarmListDto.setAlarmDuration(secToTime((int) ChronoUnit.SECONDS.between(strToLocalDateTime(alarmRecordDto.getCreateTime()), strToLocalDateTime(alarmRecordDto.getUpdateTime()))));
                                    }
                                    return alarmListDto;
                                }).collect(Collectors.toList());
                                //根据创建时间排序
                                resultList.sort(Comparator.comparing(AlarmListDto::getCreateTime).reversed());
                            }
                        }
                    }
                }
            }
            //如果查询条数小于或等于0，则返回查询到的所有告警列表数据
            if (alarmListQueryVo.getSize() <= 0) {
                return ResponseResult.ok(resultList);
            }
            return ResponseResult.ok(new PageDto<>(resultList, alarmListQueryVo.getPage(), alarmListQueryVo.getSize()));
        } catch (Exception e) {
            log.error("查询告警列表失败", e);
            return ResponseResult.error("查询告警列表异常");
        }
    }

    @Override
    public ResponseResult<Void> pileSetQr(PileSetQrVo pileSetQrVo) {
        return protocolService.pileSetQr(pileSetQrVo);
    }
}
