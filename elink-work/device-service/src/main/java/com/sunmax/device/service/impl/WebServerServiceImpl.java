package com.sunmax.device.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.sunmax.common.dto.operate.OrderCountDto;
import com.sunmax.common.dto.system.OrganEmpowerListDto;
import com.sunmax.common.dto.system.TenantDetailsDto;
import com.sunmax.common.entity.BaseEntity;
import com.sunmax.common.util.DoubleUtil;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.util.StringUtil;
import com.sunmax.common.util.oss.FileUtil;
import com.sunmax.device.dao.access.DeviceDao;
import com.sunmax.device.dao.access.DeviceGunDao;
import com.sunmax.device.dao.access.SiteInfoDao;
import com.sunmax.device.dao.model.AssetTypeDao;
import com.sunmax.device.dao.model.ModelReaDao;
import com.sunmax.device.dao.model.ReaDao;
import com.sunmax.device.dto.webserver.*;
import com.sunmax.device.entity.access.DeviceEntity;
import com.sunmax.device.entity.access.DeviceGunEntity;
import com.sunmax.device.entity.access.SiteInfoEntity;
import com.sunmax.device.entity.model.AssetTypeEntity;
import com.sunmax.device.entity.model.ModelReaEntity;
import com.sunmax.device.entity.model.ReaEntity;
import com.sunmax.device.service.WebServerService;
import com.sunmax.device.service.feign.SystemService;
import com.sunmax.device.service.feign.TogetherService;
import com.sunmax.device.vo.webserver.CountQueryVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.compress.utils.Lists;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class WebServerServiceImpl implements WebServerService {

    @Autowired
    private SiteInfoDao siteInfoDao;

    @Autowired
    private DeviceDao deviceDao;

    @Autowired
    private SystemService systemService;

    @Autowired
    private DeviceGunDao deviceGunDao;

    @Autowired
    private ModelReaDao modelReaDao;

    @Autowired
    private ReaDao reaDao;

    @Autowired
    private AssetTypeDao assetTypeDao;

    @Autowired
    private TogetherService togetherService;

    @Override
    public ResponseResult<List<DeviceServerDto>> findDeviceListByIdsAndType(String ids, Integer type) {
        if (StringUtil.isEmpty(ids) || StringUtil.isEmpty(type)) {
            return ResponseResult.paramError(ResponseResult.PARAM_ERROR);
        }
        return null;
    }

    @Override
    public ResponseResult<List<SiteLedgerDto>> findLedgerListByTenetId(String tenantId) {
        //返回的集合
        List<SiteLedgerDto> resultList = Lists.newArrayList();
        if (StringUtil.isEmpty(tenantId)) {
            return ResponseResult.paramError(ResponseResult.PARAM_ERROR);
        }
        //1.根据租户id查询多个站点id
        Set<String> siteIds = systemService.findOrganEmpowerListByTenantId(tenantId).getData().stream().map(OrganEmpowerListDto::getSiteId)
                .collect(Collectors.toSet());
        if (CollectionUtils.isEmpty(siteIds)) {
            return ResponseResult.ok(resultList);
        }

        //2.根据多个站点id查询站点信息列表
        List<SiteInfoEntity> siteList = siteInfoDao.findAllById(siteIds).stream().filter(s -> s.getIsDelete() == 1).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(siteList)) {
            return ResponseResult.ok(resultList);
        }

        //3.根据多个租户id查询租户名称
        List<String> tenetIds = siteList.stream().map(SiteInfoEntity::getTenantId).filter(StringUtil::isNotEmpty).distinct().collect(Collectors.toList());
        Map<String, String> tenantNameMap = systemService.findTenantDetailsByIds(tenetIds).getData().stream().collect(Collectors
                .toMap(TenantDetailsDto::getId, TenantDetailsDto::getTenantName, (k1, k2) -> k1));

        //4.根据多个站点id查询设备信息列表
        Map<String, List<DeviceEntity>> siteDeviceMap = deviceDao.findAllBySiteIdInAndIsDelete(siteIds, 1).stream().collect(Collectors
                .groupingBy(DeviceEntity::getSiteId));

        //5.根据多个设备id查询设备枪数据
        List<String> deviceIds = siteDeviceMap.entrySet().stream().flatMap(s -> s.getValue().stream().map(BaseEntity::getId)).collect(Collectors.toList());
        Map<String, List<DeviceGunEntity>> deviceGunMap = deviceGunDao.findAllByDeviceIdIn(deviceIds).stream().collect(Collectors
                .groupingBy(DeviceGunEntity::getDeviceId));

        //6.根据多个模型id查询扩展属性数据
        List<String> modelIds = Lists.newArrayList();
        modelIds.addAll(siteList.stream().map(SiteInfoEntity::getSiteModelId).collect(Collectors.toSet()));
        modelIds.addAll(siteDeviceMap.entrySet().stream().flatMap(s -> s.getValue().stream().map(DeviceEntity::getModelId)).collect(Collectors.toSet()));
        modelIds = modelIds.stream().distinct().collect(Collectors.toList());
        Map<String, List<ModelReaEntity>> modelReaMap = modelReaDao.findAllByModelIdIn(modelIds).stream().filter(r -> StringUtil.isNotEmpty(r.getReaId()))
                .collect(Collectors.groupingBy(ModelReaEntity::getModelId));
        Set<String> reaIds = modelReaMap.values().stream().flatMap(Collection::stream).map(ModelReaEntity::getReaId).filter(StringUtil::isNotEmpty)
                .collect(Collectors.toSet());
        Map<String, ReaEntity> reaMap = reaDao.findAllById(reaIds).stream().collect(Collectors.toMap(ReaEntity::getId, a -> a, (k1, k2) -> k1));

        //6.根据多个资产分类id查询分类名称
        Set<String> typeIds = siteDeviceMap.entrySet().stream().flatMap(s -> s.getValue().stream().map(DeviceEntity::getTypeId)).collect(Collectors.toSet());
        Map<String, String> typeNameMap = assetTypeDao.findAllById(typeIds).stream().collect(Collectors.toMap(AssetTypeEntity::getId,
                AssetTypeEntity::getTypeName, (k1, k2) -> k1));

        //7.对数据进行组装
        resultList = siteList.stream().map(site -> {
            SiteLedgerDto result = new SiteLedgerDto();
            //站点静态数据
            BeanUtils.copyProperties(site, result);
            result.setSiteId(site.getId());
            if (tenantNameMap.containsKey(site.getTenantId())) {
                result.setTenantName(tenantNameMap.get(site.getTenantId()));
            }
            //站点扩展属性数据
            if (modelReaMap.containsKey(site.getSiteModelId())) {
                //获取读写map数据
                JSONObject readwriteObject = new JSONObject();
                if (StringUtil.isNotEmpty(site.getSiteReadwriteObject())) {
                    readwriteObject = JSONObject.parseObject(site.getSiteReadwriteObject());
                }
                List<FieldDataDto> fieldDataList = Lists.newArrayList();
                for (ModelReaEntity modelRea : modelReaMap.get(site.getSiteModelId())) {
                    ReaEntity rea = reaMap.get(modelRea.getReaId());
                    if (StringUtil.isNotEmpty(rea)) {
                        FieldDataDto fieldData = new FieldDataDto();
                        fieldData.setFieldCode(rea.getFieldName());
                        fieldData.setFieldName(rea.getReaName());
                        fieldData.setFieldType(rea.getReaType());
                        fieldData.setFieldDesc(rea.getExtraValue());
                        if (readwriteObject.containsKey(rea.getFieldName()) && StringUtil.isNotEmpty(readwriteObject.get(rea.getFieldName()))) {
                            fieldData.setFieldValue(readwriteObject.get(rea.getFieldName()));
                        } else {
                            fieldData.setFieldValue(modelRea.getDefaultValue());
                        }
                        fieldDataList.add(fieldData);
                    }
                }
                result.setFieldDataList(fieldDataList);
            }
            //设备数据
            if (siteDeviceMap.containsKey(site.getId())) {
                //对设备数据进行组装
                List<DeviceLedgerDto> deviceDataList = siteDeviceMap.get(site.getId()).stream().map(device -> {
                    DeviceLedgerDto deviceLedger = new DeviceLedgerDto();
                    BeanUtils.copyProperties(device, deviceLedger);
                    deviceLedger.setDeviceId(device.getId());
                    if (typeNameMap.containsKey(device.getTypeId())) {
                        deviceLedger.setTypeName(typeNameMap.get(device.getTypeId()));
                    }
                    //设备扩展属性字段数据
                    if (modelReaMap.containsKey(device.getModelId())) {
                        //获取读写map数据
                        JSONObject readwriteObject = new JSONObject();
                        if (StringUtil.isNotEmpty(device.getReadwriteObject())) {
                            readwriteObject = JSONObject.parseObject(device.getReadwriteObject());
                        }
                        List<FieldDataDto> fieldDataList = Lists.newArrayList();
                        for (ModelReaEntity modelRea : modelReaMap.get(device.getModelId())) {
                            ReaEntity rea = reaMap.get(modelRea.getReaId());
                            if (StringUtil.isNotEmpty(rea)) {
                                FieldDataDto fieldData = new FieldDataDto();
                                fieldData.setFieldCode(rea.getFieldName());
                                fieldData.setFieldName(rea.getReaName());
                                fieldData.setFieldType(rea.getReaType());
                                fieldData.setFieldDesc(rea.getExtraValue());
                                if (readwriteObject.containsKey(rea.getFieldName()) && StringUtil.isNotEmpty(readwriteObject.get(rea.getFieldName()))) {
                                    fieldData.setFieldValue(readwriteObject.get(rea.getFieldName()));
                                } else {
                                    fieldData.setFieldValue(modelRea.getDefaultValue());
                                }
                                fieldDataList.add(fieldData);
                            }
                        }
                        deviceLedger.setFieldDataList(fieldDataList);
                    }
                    //设备枪数据
                    if (deviceGunMap.containsKey(device.getId())) {
                        List<DeviceGunEntity> deviceGunList = deviceGunMap.get(device.getId());
                        List<GunLedgerDto> gunDataList = deviceGunList.stream().map(deviceGun -> {
                            GunLedgerDto gunLedger = new GunLedgerDto();
                            BeanUtils.copyProperties(deviceGun, gunLedger);
                            return gunLedger;
                        }).collect(Collectors.toList());
                        deviceLedger.setGunDataList(gunDataList);
                    }
                    return deviceLedger;
                }).collect(Collectors.toList());
                result.setDeviceDataList(deviceDataList);
            }
            return result;
        }).collect(Collectors.toList());

        //7.返回数据
        return ResponseResult.ok(resultList);
    }

    @Override
    public ResponseResult<List<SiteCountDto>> findCountListByCondition(CountQueryVo countQueryVo) {
        //返回的集合
        List<SiteCountDto> resultList = Lists.newArrayList();

        if (StringUtil.isEmpty(countQueryVo.getSiteIds())) {
            return ResponseResult.error(ResponseResult.PARAM_ERROR, resultList);
        }
        //2.根据多个站点id查询设备信息列表
        List<String> siteIds = Arrays.stream(countQueryVo.getSiteIds().split(FileUtil.COMMA)).collect(Collectors.toList());
        Map<String, List<DeviceEntity>> siteDeviceMap = deviceDao.findAllBySiteIdInAndIsDelete(siteIds, 1).stream().collect(Collectors
                .groupingBy(DeviceEntity::getSiteId));
        Map<String, String> pileTypeMap = Maps.newHashMap();
        pileTypeMap.put("28", "交流充电桩");
        pileTypeMap.put("29", "直流充电桩");
        pileTypeMap.put("30", "V2G充电桩");

        //3.根据多个站点id查询无异常的订单数据
        Map<String, List<OrderCountDto>> siteOrderCountMap = togetherService.findOrderRecordListBySiteIds(siteIds, countQueryVo.getStartTime(), countQueryVo.getEndTime()).getData();

        //4.对数据进行组装
        resultList = siteIds.stream().map(siteId -> {
            SiteCountDto result = new SiteCountDto();
            result.setSiteId(siteId);
            if (siteOrderCountMap.containsKey(siteId)) {
                List<OrderCountDto> orderCountList = siteOrderCountMap.get(siteId);
                //站点下面的累计充放电次数,电量,金额
                result.setChargeCount(orderCountList.stream().filter(s -> StringUtil.isNotEmpty(s.getChargeCount())).mapToInt(OrderCountDto::getChargeCount).sum());
                result.setChargeQt(DoubleUtil.getToDouble(orderCountList.stream().filter(s -> StringUtil.isNotEmpty(s.getChargeQt())).mapToDouble(OrderCountDto::getChargeQt).sum()));
                result.setChargeMoney(DoubleUtil.getToBigDecimal(orderCountList.stream().map(OrderCountDto::getChargeMoney).filter(StringUtil::isNotEmpty).reduce(BigDecimal.ZERO, BigDecimal::add)));
                result.setDischargeCount(orderCountList.stream().filter(s -> StringUtil.isNotEmpty(s.getDischargeCount())).mapToInt(OrderCountDto::getDischargeCount).sum());
                result.setDischargeQt(DoubleUtil.getToDouble(orderCountList.stream().filter(s -> StringUtil.isNotEmpty(s.getDischargeQt())).mapToDouble(OrderCountDto::getDischargeQt).sum()));
                result.setDischargeMoney(DoubleUtil.getToBigDecimal(orderCountList.stream().map(OrderCountDto::getDischargeMoney).filter(StringUtil::isNotEmpty).reduce(BigDecimal.ZERO, BigDecimal::add)));

                //站点下面的设备电量数据
                if (siteDeviceMap.containsKey(siteId)) {
                    Map<String, OrderCountDto> pileOrderCountMap = orderCountList.stream().filter(s -> StringUtil.isNotEmpty(s.getPileCode())).collect(Collectors
                            .toMap(OrderCountDto::getPileCode, a -> a, (k1, k2) -> k1));
                    result.setDeviceCountList(siteDeviceMap.get(siteId).stream().filter(d -> pileTypeMap.containsKey(d.getTypeId()) && StringUtil.isNotEmpty(d.getDeviceNumber())).map(device -> {
                        DeviceCountDto deviceCount = new DeviceCountDto();
                        deviceCount.setDeviceId(device.getId());
                        if (pileOrderCountMap.containsKey(device.getDeviceNumber())) {
                            OrderCountDto orderCount = pileOrderCountMap.get(device.getDeviceNumber());
                            deviceCount.setChargeCount(orderCount.getChargeCount());
                            deviceCount.setChargeQt(orderCount.getChargeQt());
                            deviceCount.setChargeMoney(orderCount.getChargeMoney());
                            deviceCount.setDischargeCount(orderCount.getDischargeCount());
                            deviceCount.setDischargeQt(orderCount.getDischargeQt());
                            deviceCount.setDischargeMoney(orderCount.getDischargeMoney());
                        }
                        return deviceCount;
                    }).collect(Collectors.toList()));
                }
            }
            return result;
        }).collect(Collectors.toList());

        return ResponseResult.ok(resultList);
    }

}
