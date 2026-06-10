package com.sunmax.together.service.operation.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.TypeReference;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.sunmax.common.dto.PageDto;
import com.sunmax.common.dto.data.DeviceHistoryDto;
import com.sunmax.common.dto.device.AffiliatesInfoDto;
import com.sunmax.common.dto.device.DeviceBasicInfoDto;
import com.sunmax.common.dto.device.SiteInfoDto;
import com.sunmax.common.dto.operate.*;
import com.sunmax.common.dto.system.ChargePlatformInfoDto;
import com.sunmax.common.dto.system.OrganEmpowerListDto;
import com.sunmax.common.dto.system.UserDto;
import com.sunmax.common.dto.together.OrderDetailDto;
import com.sunmax.common.dto.webapp.AppInHandOrderDto;
import com.sunmax.common.dto.webapp.OrderInfoDto;
import com.sunmax.common.enums.SystemVariableEnum;
import com.sunmax.common.model.general.PileRealModel;
import com.sunmax.common.util.DateUtil;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.util.StringUtil;
import com.sunmax.common.util.oss.FileUtil;
import com.sunmax.common.vo.DeviceTypeParamVo;
import com.sunmax.common.vo.ReaFieldParamVo;
import com.sunmax.common.vo.SiteFieldParamVo;
import com.sunmax.common.vo.StaticParamVo;
import com.sunmax.common.vo.crontab.VarNodeValueVo;
import com.sunmax.common.vo.data.DeviceIndexQueryVo;
import com.sunmax.common.vo.webapp.AppletChargeRefundVo;
import com.sunmax.together.dao.*;
import com.sunmax.together.dao.asset.OccupyPilePriceDao;
import com.sunmax.together.dao.order.*;
import com.sunmax.together.dto.operation.order.*;
import com.sunmax.together.entity.AppletUserEntity;
import com.sunmax.together.entity.assets.OccupyPilePriceEntity;
import com.sunmax.together.entity.order.*;
import com.sunmax.together.service.operation.OrderRecordService;
import com.sunmax.together.service.feign.DataService;
import com.sunmax.together.service.feign.DeviceService;
import com.sunmax.together.service.feign.SystemService;
import com.sunmax.together.service.feign.WebAppService;
import com.sunmax.together.util.TogetherCommonUtil;
import com.sunmax.together.vo.operation.orderRecord.OccupyPileRecordListQueryVo;
import com.sunmax.together.vo.operation.orderRecord.OrderRecordQueryVo;
import com.sunmax.together.vo.operation.orderRecord.OrderRefundVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.criteria.Predicate;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static com.sunmax.common.config.redis.RedisGeneralUtil.getPileRealModel;
import static com.sunmax.common.util.DateUtil.*;
import static com.sunmax.common.util.DeviceUtil.dataTypeConvert;
import static com.sunmax.common.util.DoubleUtil.getToBigDecimal;
import static com.sunmax.common.util.DoubleUtil.getToDouble;

@Slf4j
@Service
public class OrderRecordServiceImpl implements OrderRecordService {

    @Autowired
    private OrderRecordDao orderRecordDao;

    @Autowired
    private ChargeTariffRecordDao chargeTariffRecordDao;

    @Autowired
    private OccupyPileRecordDao occupyPileRecordDao;

    @Autowired
    private RefundRecordDao refundRecordDao;

    @Autowired
    private SettlementRecordDao settlementRecordDao;

    @Autowired
    private UserRecordDao userRecordDao;

    @Autowired
    private DeviceService deviceService;

    @Autowired
    private SystemService systemService;

    @Autowired
    private RepairOrderRecordDao repairOrderRecordDao;

    @Autowired
    private OccupyPilePriceDao occupyPilePriceDao;

    @Autowired
    private AppletUserDao appletUserDao;

    @Autowired
    private WebAppService webAppService;

    @Autowired
    private DataService dataService;

    @Override
    public ResponseResult<OrderRecordListDto> findOrderRecordListByPage(OrderRecordQueryVo orderQueryVo) {
        //返回的对象
        OrderRecordListDto resultObject = new OrderRecordListDto();
        //定义数据集合
        List<OrderRecordListDto.OrderRecordData> resultList = Lists.newArrayList();
        //默认给数据
        resultObject.setOrderRecordDataPage(new PageDto<>(resultList, orderQueryVo.getPage(), orderQueryVo.getSize()));

        //根据当前登录用户id，查询相关配置资产授权站点数据
        List<OrganEmpowerListDto> organEmpowerList = systemService.findAllOrganEmpowerByUserId(orderQueryVo.getUserId()).getData();
        if (CollectionUtils.isEmpty(organEmpowerList)) { //关联的站点数据为空 直接返回
            return ResponseResult.ok(resultObject);
        }

        //根据站点id查询
        if (StringUtil.isNotEmpty(orderQueryVo.getSiteId())) {
            List<String> siteIds = Arrays.stream(orderQueryVo.getSiteId().split(FileUtil.COMMA)).collect(Collectors.toList());
            organEmpowerList = organEmpowerList.stream().filter(o -> StringUtil.isNotEmpty(o.getSiteId()) &&
                    siteIds.contains(o.getSiteId())).collect(Collectors.toList());
            if (CollectionUtils.isEmpty(organEmpowerList)) { //关联的站点数据为空 直接返回
                return ResponseResult.ok(resultObject);
            }
        }

        //根据多个站点id查询站点信息
        List<SiteInfoDto> siteBasicInfoList = new ArrayList<>(deviceService.findSiteBasicInfoByIds(organEmpowerList.stream()
                .map(OrganEmpowerListDto::getSiteId).filter(StringUtil::isNotEmpty).distinct().collect(Collectors.toList())).getData().values());
        if (CollectionUtils.isEmpty(siteBasicInfoList)) { //站点数据为空 直接返回
            return ResponseResult.ok(resultObject);
        }

        //根据多个站点id，查询站点下所有电桩设备信息
        List<String> siteIds = siteBasicInfoList.stream().map(SiteInfoDto::getId).collect(Collectors.toList());
        Map<String, List<DeviceBasicInfoDto>> deviceInfoMap = deviceService.findDeviceBasicInfoBySiteIds(siteIds, DeviceTypeParamVo.CDZ).getData();
        if (MapUtils.isEmpty(deviceInfoMap)) { //电桩数据为空 直接返回
            return ResponseResult.ok(resultObject);
        }

        List<DeviceBasicInfoDto> deviceInfoList = deviceInfoMap.entrySet().stream().flatMap(entry -> entry.getValue().stream()).collect(Collectors.toList());
        //根据电桩编码查询
        if (StringUtil.isNotEmpty(orderQueryVo.getPileCode())) {
            deviceInfoList = deviceInfoList.stream().filter(d -> StringUtil.isNotEmpty(d.getDeviceNumber())
                    && d.getDeviceNumber().equals(orderQueryVo.getPileCode())).collect(Collectors.toList());
        }
        //根据电桩类型筛选数据
        if (StringUtil.isNotEmpty(orderQueryVo.getPileType())) {
            deviceInfoList = deviceInfoList.stream().filter(d -> d.getTypeId().equals(String.valueOf(orderQueryVo.getPileType()))).collect(Collectors.toList());
        }
        //根据多个电桩编码查询订单记录数据
        List<Integer> orderTypes = JSON.parseArray(orderQueryVo.getOrderTypes(), Integer.class);
        List<String> pileCodes = deviceInfoList.stream().map(DeviceBasicInfoDto::getDeviceNumber).filter(StringUtil::isNotEmpty).distinct().collect(Collectors.toList());
        //关键字查询电桩编号
        if (StringUtil.isNotEmpty(orderQueryVo.getKeywordType()) && StringUtil.isNotEmpty(orderQueryVo.getKeyword())) {
            if (orderQueryVo.getKeywordType() == 5) {
                pileCodes = pileCodes.stream().filter(pileCode -> pileCode.contains(orderQueryVo.getKeyword())).collect(Collectors.toList());
            }
        }
        if (CollectionUtils.isEmpty(pileCodes)) {
            return ResponseResult.ok(resultObject);
        }
        List<String> finalPileCodes = pileCodes;
        List<OrderRecordEntity> orderRecordEntityList = orderRecordDao.findAll((Specification<OrderRecordEntity>) (root, cq, cb) -> {
            List<Predicate> predicates = Lists.newArrayList();
            predicates.add(cb.in(root.get("runMode")).value(orderTypes));
            predicates.add(cb.in(root.get("pileCode")).value(finalPileCodes));
            if (StringUtil.isNotEmpty(orderQueryVo.getKeywordType()) && StringUtil.isNotEmpty(orderQueryVo.getKeyword())) { //关键字查询
                switch (orderQueryVo.getKeywordType()) {
                    case 1: //订单号
                        predicates.add(cb.like(root.get("orderNum"), "%" + orderQueryVo.getKeyword() + "%"));
                        break;
                    case 2: //手机号
                        predicates.add(cb.equal(root.get("accountType"), 3));
                        predicates.add(cb.like(root.get("accountData"), "%" + orderQueryVo.getKeyword() + "%"));
                        break;
                    case 3: //VIN码
                        predicates.add(cb.like(root.get("busVin"), "%" + orderQueryVo.getKeyword() + "%"));
                        break;
                    case 4: //电卡ID
                        predicates.add(cb.equal(root.get("accountType"), 1));
                        predicates.add(cb.like(root.get("accountData"), "%" + orderQueryVo.getKeyword() + "%"));
                        break;
                }
            }
            //根据发起者查询
            if (StringUtil.isNotEmpty(orderQueryVo.getRunMode())) {
                predicates.add(cb.equal(root.get("starter"), orderQueryVo.getRunMode()));
            }
            //根据订单状态查询
            if (StringUtil.isNotEmpty(orderQueryVo.getOrderStatus())) {
                predicates.add(cb.equal(root.get("orderStatus"), orderQueryVo.getOrderStatus()));
            }
            //根据创建时间查询
            if (StringUtil.isNotEmpty(orderQueryVo.getCreateAlsoStartDate()) && StringUtil.isNotEmpty(orderQueryVo.getCreateAlsoEndDate())) {
                LocalDateTime startTime = strToLocalDateTime(getDayStart(orderQueryVo.getCreateAlsoStartDate()));
                LocalDateTime endTime = strToLocalDateTime(getDayEnd(orderQueryVo.getCreateAlsoEndDate()));
                predicates.add(cb.between(root.get("createTime"), startTime, endTime));
            }
            //根据充电开始时间查询
            if (StringUtil.isNotEmpty(orderQueryVo.getStartAlsoStartDate()) && StringUtil.isNotEmpty(orderQueryVo.getStartAlsoEndDate())) {
                predicates.add(cb.between(root.get("startTime"), getDayStart(orderQueryVo.getStartAlsoStartDate()), getDayEnd(orderQueryVo.getStartAlsoEndDate())));
            }
            //根据充电结束时间查询
            if (StringUtil.isNotEmpty(orderQueryVo.getEndAlsoStartDate()) && StringUtil.isNotEmpty(orderQueryVo.getEndAlsoEndDate())) {
                predicates.add(cb.between(root.get("endTime"), getDayStart(orderQueryVo.getEndAlsoStartDate()), getDayEnd(orderQueryVo.getEndAlsoEndDate())));
            }
            //根据来源平台id查询
            if (StringUtil.isNotEmpty(orderQueryVo.getPlatformLogo())) {
                predicates.add(cb.equal(root.get("platformLogo"), orderQueryVo.getPlatformLogo()));
            }
            //根据电量最大最小值过滤
            if (StringUtil.isNotEmpty(orderQueryVo.getMaxQt()) && StringUtil.isNotEmpty(orderQueryVo.getMinQt())) {
                predicates.add(cb.between(root.get("totalQt"), orderQueryVo.getMinQt(), orderQueryVo.getMaxQt()));
            }
            //根据异常类型过滤
            if (StringUtil.isNotEmpty(orderQueryVo.getAbnormalType())) {
                if (orderQueryVo.getAbnormalType() != 0) {
                    predicates.add(cb.like(root.get("abnormalCode"), "%" + orderQueryVo.getAbnormalType() + "%"));
                }
                if (orderQueryVo.getAbnormalType() == 0) {
                    predicates.add(cb.isNull(root.get("abnormalCode")));
                }
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        });

        if (CollectionUtils.isEmpty(orderRecordEntityList)) { //订单数据为空 直接返回
            return ResponseResult.ok(resultObject);
        }

        //根据多个平台标识查询平台信息
        List<String> platformLogoList = orderRecordEntityList.stream().map(OrderRecordEntity::getPlatformLogo).filter(StringUtil::isNotEmpty).distinct().collect(Collectors.toList());
        Map<String, ChargePlatformInfoDto> platformInfoDtoMap = systemService.findChargePlatformInfoByLogos(platformLogoList).getData();
        //电桩基本信息
        Map<String, DeviceBasicInfoDto> deviceInfoDtoMap = deviceInfoList.stream().collect(Collectors.toMap(DeviceBasicInfoDto::getDeviceNumber, d -> d, (k1, k2) -> k1));
        //根据多个订单编码查询用户记录数据
        Map<String, UserRecordEntity> userRecordEntityMap = Maps.newHashMap();
        List<String> orderNumList = orderRecordEntityList.stream().map(OrderRecordEntity::getOrderNum).filter(StringUtil::isNotEmpty).distinct().collect(Collectors.toList());
        List<UserRecordEntity> userRecordEntityList = userRecordDao.findAllByOrderNumIn(orderNumList);
        if (CollectionUtils.isNotEmpty(userRecordEntityList)) {
            userRecordEntityMap = userRecordEntityList.stream().collect(Collectors.toMap(UserRecordEntity::getOrderNum, u -> u, (k1, k2) -> k1));
        }
        //根据多个订单编码查询补单信息
        List<RepairOrderRecordEntity> repairOrderRecordEntities = repairOrderRecordDao.findAllByOrderNumIn(orderNumList);
        Map<String, RepairOrderRecordEntity> repairOrderMap = Maps.newHashMap();
        if (CollectionUtils.isNotEmpty(repairOrderRecordEntities)) {
            repairOrderMap = repairOrderRecordEntities.stream().collect(Collectors.toMap(RepairOrderRecordEntity::getOrderNum, r -> r, (k1, k2) -> k1));
        }
        //根据多个订单编码查询结算记录信息
        Map<String, SettlementRecordEntity> settlementRecordMap = settlementRecordDao.findAllByOrderNumIn(orderNumList).stream().collect(Collectors
                .toMap(SettlementRecordEntity::getOrderNum, s -> s, (k1, k2) -> k1));

        Map<String, UserRecordEntity> finalUserRecordEntityMap = userRecordEntityMap;
        Map<String, RepairOrderRecordEntity> finalRepairOrderMap = repairOrderMap;
        resultList = orderRecordEntityList.stream().map(orderRecordEntity -> {
            OrderRecordListDto.OrderRecordData orderRecordData = new OrderRecordListDto.OrderRecordData();
            BeanUtils.copyProperties(orderRecordEntity, orderRecordData);
            orderRecordData.setCreateTime(localDateTimeToStr(orderRecordEntity.getCreateTime()));
            //获取车牌号
            UserRecordEntity userRecordEntity = finalUserRecordEntityMap.get(orderRecordEntity.getId());
            orderRecordData.setPlateNumber(userRecordEntity != null ? userRecordEntity.getPlateNumber() : null);
            //获取电桩基本详情数据
            DeviceBasicInfoDto deviceInfo = deviceInfoDtoMap.get(orderRecordEntity.getPileCode());
            if (StringUtil.isNotEmpty(deviceInfo)) {
                //获取站点名称
                orderRecordData.setSiteName(deviceInfo.getSiteName());
                orderRecordData.setSiteId(deviceInfo.getSiteId());
                //获取电桩类型
                orderRecordData.setPileType(deviceInfo.getTypeId() != null ? Integer.parseInt(deviceInfo.getTypeId()) : null);
            }
            //获取平台信息
            if (StringUtil.isNotEmpty(orderRecordEntity.getPlatformLogo()) && platformInfoDtoMap.containsKey(orderRecordEntity.getPlatformLogo())) {
                orderRecordData.setPlatformName(platformInfoDtoMap.get(orderRecordEntity.getPlatformLogo()).getPlatformName());
            }
            //获取补单信息
            orderRecordData.setRepairStatus(3);
            if (!finalRepairOrderMap.isEmpty() && finalRepairOrderMap.containsKey(orderRecordEntity.getOrderNum())) {
                orderRecordData.setRepairStatus(finalRepairOrderMap.get(orderRecordEntity.getOrderNum()).getRepairStatus());
            }
            //获取结算记录信息
            if (settlementRecordMap.containsKey(orderRecordEntity.getOrderNum())) {
                SettlementRecordEntity settlementRecord = settlementRecordMap.get(orderRecordEntity.getOrderNum());
                orderRecordData.setActualTotalCost(settlementRecord.getActualTotalCost());
                orderRecordData.setActualTotalElect(settlementRecord.getActualTotalElect());
                orderRecordData.setActualTotalFee(settlementRecord.getActualTotalFee());
            }
            //计算充放电时长
            String startTime = orderRecordEntity.getStartTime();
            String endTime = orderRecordEntity.getEndTime();
            if (StringUtil.isNotEmpty(startTime) && StringUtil.isNotEmpty(endTime)) {
                orderRecordData.setDuration(secToTime((int) ChronoUnit.SECONDS.between(strToLocalDateTime(startTime), strToLocalDateTime(endTime))));
                orderRecordData.setHourDuration(getDateSubHours(startTime, endTime));
            } else if (StringUtil.isNotEmpty(startTime) && StringUtil.isEmpty(endTime) && orderRecordEntity.getOrderStatus() == 1) {
                orderRecordData.setDuration(secToTime((int) ChronoUnit.SECONDS.between(strToLocalDateTime(startTime), LocalDateTime.now())));
                orderRecordData.setHourDuration(getDateSubHours(startTime, localDateTimeToStr(LocalDateTime.now())));
            }
            return orderRecordData;
        }).collect(Collectors.toList());
        //根据补单状态查询
        if (StringUtil.isNotEmpty(orderQueryVo.getRepairStatus())) {
            resultList = resultList.stream().filter(orderRecordData -> Objects.equals(orderRecordData.getRepairStatus(),
                    orderQueryVo.getRepairStatus())).collect(Collectors.toList());
        }
        //根据时长最大最小值过滤
        if (StringUtil.isNotEmpty(orderQueryVo.getMaxDuration()) && StringUtil.isNotEmpty(orderQueryVo.getMinDuration())) {
            resultList = resultList.stream().filter(r -> StringUtil.isNotEmpty(r.getHourDuration())
                            && r.getHourDuration() >= orderQueryVo.getMinDuration() && r.getHourDuration() <= orderQueryVo.getMaxDuration())
                    .collect(Collectors.toList());
        }
        resultList = resultList.stream().sorted(Comparator.comparing(OrderRecordListDto.OrderRecordData::getCreateTime).reversed()).collect(Collectors.toList());
        //统计累计电量
        resultObject.setSumQt(getToDouble(resultList.stream().filter(o -> StringUtil.isNotEmpty(o.getTotalQt())).mapToDouble(OrderRecordListDto.OrderRecordData::getTotalQt).sum()));
        //统计累计金额
        resultObject.setSumCost(getToBigDecimal(resultList.stream().map(OrderRecordListDto.OrderRecordData::getTotalCost).filter(StringUtil::isNotEmpty).reduce(BigDecimal.ZERO, BigDecimal::add)));
        //统计累计实付金额
        resultObject.setActualTotalCost(getToBigDecimal(resultList.stream().map(OrderRecordListDto.OrderRecordData::getActualTotalCost).filter(StringUtil::isNotEmpty).reduce(BigDecimal.ZERO, BigDecimal::add)));
        //统计累计实付服务费
        resultObject.setActualTotalFee(getToBigDecimal(resultList.stream().map(OrderRecordListDto.OrderRecordData::getActualTotalFee).filter(StringUtil::isNotEmpty).reduce(BigDecimal.ZERO, BigDecimal::add)));
        //统计累计实付电费
        resultObject.setActualTotalElect(getToBigDecimal(resultList.stream().map(OrderRecordListDto.OrderRecordData::getActualTotalElect).filter(StringUtil::isNotEmpty).reduce(BigDecimal.ZERO, BigDecimal::add)));

        //根据结束时间排序
        resultList = resultList.stream().sorted(Comparator.comparing(OrderRecordListDto.OrderRecordData::getCreateTime).reversed()).collect(Collectors.toList());
        resultObject.setOrderRecordDataPage(new PageDto<>(resultList, orderQueryVo.getPage(), orderQueryVo.getSize()));
        return ResponseResult.ok(resultObject);
    }

    @Override
    public ResponseResult<OrderRecordInfoDto> findOrderRecordInfoById(String orderId) {
        OrderRecordInfoDto result = new OrderRecordInfoDto();

        Optional<OrderRecordEntity> optional = orderRecordDao.findById(orderId);
        if (optional.isPresent()) {
            OrderRecordEntity recordEntity = optional.get();
            BeanUtils.copyProperties(recordEntity, result);
            LocalDateTime createTime = recordEntity.getCreateTime();
            if (StringUtil.isNotEmpty(createTime)) {
                result.setCreateTime(localDateTimeToStr(createTime));
            }
            LocalDateTime updateTime = recordEntity.getUpdateTime();
            if (StringUtil.isNotEmpty(updateTime)) {
                result.setUpdateTime(localDateTimeToStr(updateTime));
            }
            //计算充放电时长
            String startTime = recordEntity.getStartTime();
            String endTime = recordEntity.getEndTime();
            if (StringUtil.isNotEmpty(startTime) && StringUtil.isNotEmpty(endTime)) {
                result.setChargeDuration(secToTime((int) ChronoUnit.SECONDS.between(strToLocalDateTime(startTime), strToLocalDateTime(endTime))));
            }
            //获取电桩基本信息
            ResponseResult<Map<String, DeviceBasicInfoDto>> deviceBasicInfoByCodes = deviceService.findDeviceBasicInfoByCodes(Collections.singletonList(recordEntity.getPileCode()));
            if (deviceBasicInfoByCodes.isSuccess() && !deviceBasicInfoByCodes.getData().isEmpty()) {
                DeviceBasicInfoDto deviceBasicInfoDto = deviceBasicInfoByCodes.getData().get(recordEntity.getPileCode());
                if (StringUtil.isNotEmpty(deviceBasicInfoDto)) {
                    //电桩信息
                    OrderRecordInfoDto.PileInfoData pileInfoData = new OrderRecordInfoDto.PileInfoData();
                    pileInfoData.setSiteId(deviceBasicInfoDto.getSiteId());
                    pileInfoData.setSiteName(deviceBasicInfoDto.getSiteName());
                    pileInfoData.setOperatorName(deviceBasicInfoDto.getOperateName());

                    //获取电站信息
                    SiteInfoDto siteBasicInfoDto = deviceService.findSiteBasicInfoByIds(Collections.singletonList(deviceBasicInfoDto.getSiteId()))
                            .getData().get(deviceBasicInfoDto.getSiteId());
                    if (siteBasicInfoDto != null && StringUtil.isNotEmpty(siteBasicInfoDto.getSiteReadwriteObject())) {
                        JSONObject readwriteMap = JSON.parseObject(siteBasicInfoDto.getSiteReadwriteObject());
                        //地址对象
                        if (readwriteMap.containsKey(SiteFieldParamVo.LOCATION)) {
                            JSONObject locationMap = JSON.parseObject(JSON.toJSONString(readwriteMap.get(SiteFieldParamVo.LOCATION)));
                            if (locationMap != null && !locationMap.isEmpty()) {
                                //省份
                                if (locationMap.containsKey(SiteFieldParamVo.PROVINCE) && StringUtil.isNotEmpty(locationMap.get(SiteFieldParamVo.PROVINCE))) {
                                    pileInfoData.setProvince(locationMap.get(SiteFieldParamVo.PROVINCE).toString());
                                }
                                //市级
                                if (locationMap.containsKey(SiteFieldParamVo.CITY) && StringUtil.isNotEmpty(locationMap.get(SiteFieldParamVo.CITY))) {
                                    pileInfoData.setCity(locationMap.get(SiteFieldParamVo.CITY).toString());
                                }
                                //所在区县
                                if (locationMap.containsKey(SiteFieldParamVo.COUNTY) && StringUtil.isNotEmpty(locationMap.get(SiteFieldParamVo.COUNTY))) {
                                    pileInfoData.setCounty(locationMap.get(SiteFieldParamVo.COUNTY).toString());
                                }
                                //详细地址
                                if (locationMap.containsKey(SiteFieldParamVo.ADDRESS) && StringUtil.isNotEmpty(locationMap.get(SiteFieldParamVo.ADDRESS))) {
                                    pileInfoData.setAddress(locationMap.get(SiteFieldParamVo.ADDRESS).toString());
                                }
                            }
                        }
                    }

                    pileInfoData.setPileType(deviceBasicInfoDto.getTypeId() != null ? Integer.parseInt(deviceBasicInfoDto.getTypeId()) : null);

                    //设备出厂编码
                    Map<String, Object> reaMap = deviceBasicInfoDto.getReaMap();
                    if (reaMap.containsKey(ReaFieldParamVo.FACTORY_CODE)) {
                        Object factoryCodeObj = reaMap.get(ReaFieldParamVo.FACTORY_CODE);
                        if (StringUtil.isNotEmpty(factoryCodeObj)) {
                            pileInfoData.setFactoryCode(String.valueOf(factoryCodeObj));
                        }
                    }
                    result.setPileInfoData(pileInfoData);
                }
            }
            //获取平台信息
            ResponseResult<Map<String, ChargePlatformInfoDto>> chargePlatformInfoByLogos = systemService.findChargePlatformInfoByLogos(Collections.singletonList(recordEntity.getPlatformLogo()));
            if (chargePlatformInfoByLogos.isSuccess() && !chargePlatformInfoByLogos.getData().isEmpty()) {
                result.setPlatformName(chargePlatformInfoByLogos.getData().get(recordEntity.getPlatformLogo()).getPlatformName());
            }
            //获取用户信息
            List<UserRecordEntity> userRecordEntityList = userRecordDao.findAllByOrderNumIn(Collections.singletonList(recordEntity.getOrderNum()));
            if (CollectionUtils.isNotEmpty(userRecordEntityList)) {
                OrderRecordInfoDto.UserRecordDto userRecordDto = new OrderRecordInfoDto.UserRecordDto();
                BeanUtils.copyProperties(userRecordEntityList.get(0), userRecordDto);
                result.setUserRecordDto(userRecordDto);
            }
            //获取计费详情列表
            List<ChargeTariffRecordEntity> chargeTariffRecordEntities = chargeTariffRecordDao.findAllByOrderNum(recordEntity.getOrderNum());
            if (CollectionUtils.isNotEmpty(chargeTariffRecordEntities)) {
                result.setChargingDetailsList(chargeTariffRecordEntities.stream().map(chargeTariffRecordEntity -> {
                    OrderRecordInfoDto.ChargingDetails chargingDetails = new OrderRecordInfoDto.ChargingDetails();
                    BeanUtils.copyProperties(chargeTariffRecordEntity, chargingDetails);
                    return chargingDetails;
                }).collect(Collectors.toList()));
            }
            //获取补单记录信息
            List<RepairOrderRecordEntity> repairOrderRecordList = repairOrderRecordDao.findAllByOrderNum(recordEntity.getOrderNum());
            if (CollectionUtils.isNotEmpty(repairOrderRecordList)) {
                RepairOrderRecordEntity repairOrder = repairOrderRecordList.get(0);
                OrderRecordInfoDto.RepairOrderRecord repairOrderRecord = new OrderRecordInfoDto.RepairOrderRecord();
                BeanUtils.copyProperties(repairOrder, repairOrderRecord);
                if (StringUtil.isNotEmpty(repairOrder.getCreateTime())) {
                    repairOrderRecord.setCreateTime(DateUtil.localDateTimeToStr(repairOrder.getCreateTime()));
                }
                if (StringUtil.isNotEmpty(repairOrder.getUpdateTime())) {
                    repairOrderRecord.setUpdateTime(DateUtil.localDateTimeToStr(repairOrder.getUpdateTime()));
                }
                result.setRepairOrderRecord(repairOrderRecord);
            }
            //获取结算记录信息
            Optional.ofNullable(settlementRecordDao.findByOrderNum(recordEntity.getOrderNum()))
                    .ifPresent(settlementRecordEntity -> {
                        OrderRecordInfoDto.SettlementRecord settlementRecord = new OrderRecordInfoDto.SettlementRecord();
                        BeanUtils.copyProperties(settlementRecordEntity, settlementRecord);
                        if (StringUtil.isNotEmpty(settlementRecord.getTotalElectReduction()) && StringUtil.isNotEmpty(settlementRecord.getTotalFeeReduction())) {
                            settlementRecord.setPreferentialMoney(getToBigDecimal(settlementRecord.getTotalElectReduction().add(settlementRecord.getTotalFeeReduction())));
                        }
                        result.setSettlementRecord(settlementRecord);
                    });
            //获取退款记录信息
            List<RefundRecordEntity> refundRecordEntityList = refundRecordDao.findAllByOrderNum(recordEntity.getOrderNum());
            if (CollectionUtils.isNotEmpty(refundRecordEntityList)) {
                result.setRefundRecordList(refundRecordEntityList.stream().map(refundRecordEntity -> {
                    OrderRecordInfoDto.RefundRecord refundRecord = new OrderRecordInfoDto.RefundRecord();
                    BeanUtils.copyProperties(refundRecordEntity, refundRecord);
                    refundRecord.setCreateTime(localDateTimeToStr(refundRecordEntity.getCreateTime()));
                    return refundRecord;
                }).sorted(Comparator.comparing(OrderRecordInfoDto.RefundRecord::getCreateTime).reversed()).collect(Collectors.toList()));
            }
        }
        return ResponseResult.ok(result);
    }

    @Override
    public ResponseResult<OccupyPileRecordListDto> findOccupyPileRecordListByPage(OccupyPileRecordListQueryVo occupyPileRecordListQueryVo, String userId) {
        OccupyPileRecordListDto occupyPileRecordListDto = new OccupyPileRecordListDto();
        List<OccupyPileRecordListDto.OccupyPileRecordData> resultList = Lists.newArrayList();

        //根据当前登录用户id，查询相关配置资产授权站点数据
        ResponseResult<List<OrganEmpowerListDto>> allOrganEmpowerByTenantId = systemService.findAllOrganEmpowerByUserId(userId);
        if (allOrganEmpowerByTenantId.isSuccess() && CollectionUtils.isNotEmpty(allOrganEmpowerByTenantId.getData())) {
            List<OrganEmpowerListDto> organEmpowerListDtos = allOrganEmpowerByTenantId.getData();
            //根据站点id查询
            if (StringUtil.isNotEmpty(occupyPileRecordListQueryVo.getSiteId())) {
                organEmpowerListDtos = organEmpowerListDtos.stream().filter(o -> StringUtil.isNotEmpty(o.getSiteId()) && o.getSiteId().equals(occupyPileRecordListQueryVo.getSiteId())).collect(Collectors.toList());
            }
            if (CollectionUtils.isNotEmpty(organEmpowerListDtos)) {
                //根据多个站点id查询站点信息
                Map<String, SiteInfoDto> siteBasicInfoDtoMap = deviceService.findSiteBasicInfoByIds(
                        organEmpowerListDtos.stream().map(OrganEmpowerListDto::getSiteId).filter(StringUtil::isNotEmpty).distinct().collect(Collectors.toList())).getData();
                List<SiteInfoDto> siteBasicInfoDtoList = Lists.newArrayList(siteBasicInfoDtoMap.values());
                //根据运营商id查询
//                if (StringUtil.isNotEmpty(occupyPileRecordListQueryVo.getOperateUnitId())) {
//                    siteBasicInfoDtoList = siteBasicInfoDtoList.stream().filter(o -> StringUtil.isNotEmpty(o.getOperateUnit()) && o.getOperateUnit().equals(occupyPileRecordListQueryVo.getOperateUnitId())).collect(Collectors.toList());
//                }
                if (CollectionUtils.isNotEmpty(siteBasicInfoDtoList)) {
                    //根据多个站点id，查询站点下所有电桩设备信息
                    List<String> siteIds = siteBasicInfoDtoList.stream().map(SiteInfoDto::getId).collect(Collectors.toList());
                    ResponseResult<Map<String, List<DeviceBasicInfoDto>>> deviceBasicInfoBySiteIds = deviceService.findDeviceBasicInfoBySiteIds(siteIds, DeviceTypeParamVo.CDZ);
                    if (deviceBasicInfoBySiteIds.isSuccess() && !deviceBasicInfoBySiteIds.getData().isEmpty()) {
                        List<DeviceBasicInfoDto> deviceBasicInfoDtoList = Lists.newArrayList();
                        List<DeviceBasicInfoDto> finalDeviceBasicInfoDtoList = deviceBasicInfoDtoList;
                        deviceBasicInfoBySiteIds.getData().forEach((k, v) -> finalDeviceBasicInfoDtoList.addAll(v));
                        //根据电桩编码查询
                        if (StringUtil.isNotEmpty(occupyPileRecordListQueryVo.getPileCode())) {
                            deviceBasicInfoDtoList = deviceBasicInfoDtoList.stream().filter(d -> StringUtil.isNotEmpty(d.getDeviceNumber()) && d.getDeviceNumber().contains(occupyPileRecordListQueryVo.getPileCode())).collect(Collectors.toList());
                        }
                        //根据多个电桩编码查询占桩订单数据
                        List<OccupyPileRecordEntity> occupyPileRecordEntities = occupyPileRecordDao.findAllByPileCodeIn(deviceBasicInfoDtoList.stream().
                                map(DeviceBasicInfoDto::getDeviceNumber).filter(StringUtil::isNotEmpty).distinct().collect(Collectors.toList()));
                        if (CollectionUtils.isNotEmpty(occupyPileRecordEntities)) {
                            //根据占桩订单状态查询
                            if (StringUtil.isNotEmpty(occupyPileRecordListQueryVo.getOccupyState())) {
                                occupyPileRecordEntities = occupyPileRecordEntities.stream().filter(o -> StringUtil.isNotEmpty(o.getOccupyState()) && o.getOccupyState().equals(occupyPileRecordListQueryVo.getOccupyState())).collect(Collectors.toList());
                            }
                            //根据占桩结束时间查询
                            if (StringUtil.isNotEmpty(occupyPileRecordListQueryVo.getEndAlsoStartDate()) && StringUtil.isNotEmpty(occupyPileRecordListQueryVo.getEndAlsoEndDate())) {
                                LocalDateTime startTime = strToLocalDateTime(getDayStart(occupyPileRecordListQueryVo.getEndAlsoStartDate()));
                                LocalDateTime endTime = strToLocalDateTime(getDayEnd(occupyPileRecordListQueryVo.getEndAlsoEndDate()));
                                occupyPileRecordEntities = occupyPileRecordEntities.stream().filter(o -> StringUtil.isNotEmpty(o.getEndTime()) && strToLocalDateTime(o.getEndTime()).isAfter(startTime) &&
                                        strToLocalDateTime(o.getEndTime()).isBefore(endTime)).collect(Collectors.toList());
                            }
                            //根据占桩订单号查询
                            if (StringUtil.isNotEmpty(occupyPileRecordListQueryVo.getKeywordType()) && occupyPileRecordListQueryVo.getKeywordType() == 1 && StringUtil.isNotEmpty(occupyPileRecordListQueryVo.getKeyword())) {
                                occupyPileRecordEntities = occupyPileRecordEntities.stream().filter(o -> StringUtil.isNotEmpty(o.getOccupyNum()) && o.getOccupyNum().contains(occupyPileRecordListQueryVo.getKeyword())).collect(Collectors.toList());
                            }
                            if (CollectionUtils.isNotEmpty(occupyPileRecordEntities)) {
                                List<String> orderIdList = occupyPileRecordEntities.stream().map(OccupyPileRecordEntity::getOrderId).filter(StringUtil::isNotEmpty).distinct().collect(Collectors.toList());
                                //电桩基本信息
                                Map<String, DeviceBasicInfoDto> deviceBasicInfoDtoMap = deviceBasicInfoDtoList.stream().collect(Collectors.toMap(DeviceBasicInfoDto::getDeviceNumber, deviceBasicInfoDto -> deviceBasicInfoDto, (k1, k2) -> k1));
                                //根据多个订单id查询充放电订单信息
                                Map<String, OrderRecordEntity> orderRecordEntityMap = orderRecordDao.findAllById(orderIdList)
                                        .stream().collect(Collectors.toMap(OrderRecordEntity::getId, orderRecordEntity -> orderRecordEntity, (k1, k2) -> k1));
                                //根据多个订单id查询用户记录数据
                                List<OrderRecordEntity> orderRecordEntities = Lists.newArrayList(orderRecordEntityMap.values());
                                List<String> orderNumList = orderRecordEntities.stream().map(OrderRecordEntity::getOrderNum).filter(StringUtil::isNotEmpty).distinct().collect(Collectors.toList());
                                Map<String, UserRecordEntity> userRecordEntityMap = Maps.newHashMap();
                                List<UserRecordEntity> userRecordEntityList = userRecordDao.findAllByOrderNumIn(orderNumList);
                                if (CollectionUtils.isNotEmpty(userRecordEntityList)) {
                                    userRecordEntityMap = userRecordEntityList.stream().collect(Collectors.toMap(UserRecordEntity::getOrderNum, userRecordEntity -> userRecordEntity, (k1, k2) -> k1));
                                }
                                //统计累计秒数
                                AtomicInteger sumTime = new AtomicInteger();
                                Map<String, UserRecordEntity> finalUserRecordEntityMap = userRecordEntityMap;
                                resultList = occupyPileRecordEntities.stream().map(occupyPileRecord -> {
                                    OccupyPileRecordListDto.OccupyPileRecordData occupyPileRecordData = new OccupyPileRecordListDto.OccupyPileRecordData();
                                    BeanUtils.copyProperties(occupyPileRecord, occupyPileRecordData);
                                    //计算占用时长
                                    String startTime = occupyPileRecord.getStartTime();
                                    String endTime = occupyPileRecord.getEndTime();
                                    if (StringUtil.isNotEmpty(startTime) && StringUtil.isNotEmpty(endTime)) {
                                        int time = (int) ChronoUnit.SECONDS.between(strToLocalDateTime(startTime), strToLocalDateTime(endTime));
                                        occupyPileRecordData.setDuration(secToTime(time));
                                        sumTime.set(sumTime.get() + time);
                                    }
                                    //获取车牌号
                                    UserRecordEntity userRecordEntity = finalUserRecordEntityMap.get(occupyPileRecord.getOrderId());
                                    occupyPileRecordData.setPlateNumber(userRecordEntity != null ? userRecordEntity.getPlateNumber() : null);
                                    //获取电桩基本详情数据
                                    DeviceBasicInfoDto deviceBasicInfoDto = deviceBasicInfoDtoMap.get(occupyPileRecord.getPileCode());
                                    if (StringUtil.isNotEmpty(deviceBasicInfoDto)) {
                                        //获取站点名称
                                        occupyPileRecordData.setSiteName(deviceBasicInfoDto.getSiteName());
                                        occupyPileRecordData.setSiteId(deviceBasicInfoDto.getSiteId());
                                        //获取运营商名称
//                                        occupyPileRecordData.setOperateUnitId(deviceBasicInfoDto.getOperateUnitId());
//                                        occupyPileRecordData.setOperateUnitName(deviceBasicInfoDto.getOperateUnitName());
                                    }
                                    //获取充放电订单信息
                                    OrderRecordEntity recordEntity = orderRecordEntityMap.get(occupyPileRecord.getOrderId());
                                    if (StringUtil.isNotEmpty(recordEntity)) {
                                        occupyPileRecordData.setOrderNum(recordEntity.getOrderNum());
                                        if (StringUtil.isNotEmpty(recordEntity.getAccountType()) && recordEntity.getAccountType() == 3) {
                                            occupyPileRecordData.setPhoneNum(recordEntity.getAccountData());
                                        }
                                    }
                                    return occupyPileRecordData;
                                }).collect(Collectors.toList());
                                if (StringUtil.isNotEmpty(occupyPileRecordListQueryVo.getKeywordType()) && StringUtil.isNotEmpty(occupyPileRecordListQueryVo.getKeyword())) {
                                    if (occupyPileRecordListQueryVo.getKeywordType() == 2) {//根据充放电订单号查询
                                        resultList = resultList.stream().filter(o -> StringUtil.isNotEmpty(o.getOrderNum()) && o.getOrderNum().contains(occupyPileRecordListQueryVo.getKeyword())).collect(Collectors.toList());
                                    } else if (occupyPileRecordListQueryVo.getKeywordType() == 3) {//根据用户手机号查询
                                        resultList = resultList.stream().filter(o -> StringUtil.isNotEmpty(o.getPhoneNum()) && o.getPhoneNum().contains(occupyPileRecordListQueryVo.getKeyword())).collect(Collectors.toList());
                                    }
                                }
                                resultList = resultList.stream().sorted(Comparator.comparing(OccupyPileRecordListDto.OccupyPileRecordData::getStartTime)).collect(Collectors.toList());
                                //累计占桩时长
                                occupyPileRecordListDto.setSumDuration(secToTime(sumTime.get()));
                                //统计累计金额
                                occupyPileRecordListDto.setSumCost(getToBigDecimal(resultList.stream().map(OccupyPileRecordListDto.OccupyPileRecordData::getOrderMoney)
                                        .filter(StringUtil::isNotEmpty).reduce(BigDecimal.ZERO, BigDecimal::add)));
                            }
                        }
                    }
                }
            }
        }
        occupyPileRecordListDto.setOccupyPileRecordDataPage(new PageDto<>(resultList, occupyPileRecordListQueryVo.getPage(), occupyPileRecordListQueryVo.getSize()));
        occupyPileRecordListDto.setOccupyPileRecordDataList(resultList);
        return ResponseResult.ok(occupyPileRecordListDto);
    }

    @Override
    public ResponseResult<OccupyPileRecordInfoDto> findOccupyPileRecordInfoById(String occupyId) {
        OccupyPileRecordInfoDto result = new OccupyPileRecordInfoDto();
        Optional<OccupyPileRecordEntity> pileRecordDaoById = occupyPileRecordDao.findById(occupyId);
        if (pileRecordDaoById.isPresent()) {
            OccupyPileRecordEntity occupyPileRecord = pileRecordDaoById.get();
            BeanUtils.copyProperties(occupyPileRecord, result);
            //计算占用时长
            String startTime = occupyPileRecord.getStartTime();
            String endTime = occupyPileRecord.getEndTime();
            if (StringUtil.isNotEmpty(startTime) && StringUtil.isNotEmpty(endTime)) {
                result.setDuration(secToTime((int) ChronoUnit.SECONDS.between(strToLocalDateTime(startTime), strToLocalDateTime(endTime))));
            }
            //根据充放电订单id查询基本信息
            Optional<OrderRecordEntity> orderRecordEntity = orderRecordDao.findById(occupyPileRecord.getOrderId());
            if (orderRecordEntity.isPresent()) {
                OrderRecordEntity recordEntity = orderRecordEntity.get();
                result.setBusVin(recordEntity.getBusVin());
                if (StringUtil.isNotEmpty(recordEntity.getAccountType()) && recordEntity.getAccountType() == 3) {
                    result.setPhoneNum(recordEntity.getAccountData());
                }
                result.setPileCode(recordEntity.getPileCode());
                result.setGunCode(recordEntity.getGunCode());
            }

            //获取电桩基本信息
            ResponseResult<Map<String, DeviceBasicInfoDto>> deviceBasicInfoByCodes = deviceService.findDeviceBasicInfoByCodes(Collections.singletonList(occupyPileRecord.getPileCode()));
            if (deviceBasicInfoByCodes.isSuccess() && !deviceBasicInfoByCodes.getData().isEmpty()) {
                DeviceBasicInfoDto deviceBasicInfoDto = deviceBasicInfoByCodes.getData().get(occupyPileRecord.getPileCode());
                if (StringUtil.isNotEmpty(deviceBasicInfoDto)) {
                    //电桩信息
                    OccupyPileRecordInfoDto.PileInfoData pileInfoData = new OccupyPileRecordInfoDto.PileInfoData();
                    pileInfoData.setSiteId(deviceBasicInfoDto.getSiteId());
                    pileInfoData.setSiteName(deviceBasicInfoDto.getSiteName());

                    //获取电站信息
                    ResponseResult<Map<String, SiteInfoDto>> siteBasicInfoByIds = deviceService.findSiteBasicInfoByIds(Collections.singletonList(deviceBasicInfoDto.getSiteId()));
                    if (siteBasicInfoByIds.isSuccess() && !siteBasicInfoByIds.getData().isEmpty()) {
                        SiteInfoDto siteBasicInfoDto = siteBasicInfoByIds.getData().get(deviceBasicInfoDto.getSiteId());
                        String siteReadwriteObject = siteBasicInfoDto.getSiteReadwriteObject();
                        if (StringUtil.isNotEmpty(siteReadwriteObject)) {
                            JSONObject readwriteMap = JSON.parseObject(siteBasicInfoDto.getSiteReadwriteObject());
                            //地址对象
                            if (readwriteMap.containsKey(SiteFieldParamVo.LOCATION)) {
                                JSONObject locationMap = JSON.parseObject(JSON.toJSONString(readwriteMap.get(SiteFieldParamVo.LOCATION)));
                                if (locationMap != null && !locationMap.isEmpty()) {
                                    //省份
                                    if (locationMap.containsKey(SiteFieldParamVo.PROVINCE) && StringUtil.isNotEmpty(locationMap.get(SiteFieldParamVo.PROVINCE))) {
                                        pileInfoData.setProvince(locationMap.get(SiteFieldParamVo.PROVINCE).toString());
                                    }
                                    //市级
                                    if (locationMap.containsKey(SiteFieldParamVo.CITY) && StringUtil.isNotEmpty(locationMap.get(SiteFieldParamVo.CITY))) {
                                        pileInfoData.setCity(locationMap.get(SiteFieldParamVo.CITY).toString());
                                    }
                                    //所在区县
                                    if (locationMap.containsKey(SiteFieldParamVo.COUNTY) && StringUtil.isNotEmpty(locationMap.get(SiteFieldParamVo.COUNTY))) {
                                        pileInfoData.setCounty(locationMap.get(SiteFieldParamVo.COUNTY).toString());
                                    }
                                    //详细地址
                                    if (locationMap.containsKey(SiteFieldParamVo.ADDRESS) && StringUtil.isNotEmpty(locationMap.get(SiteFieldParamVo.ADDRESS))) {
                                        pileInfoData.setAddress(locationMap.get(SiteFieldParamVo.ADDRESS).toString());
                                    }
                                }
                            }
                        }
                    }

                    pileInfoData.setPileType(deviceBasicInfoDto.getTypeId() != null ? Integer.parseInt(deviceBasicInfoDto.getTypeId()) : null);

                    //设备出厂编码
                    Map<String, Object> reaMap = deviceBasicInfoDto.getReaMap();
                    if (reaMap.containsKey(ReaFieldParamVo.FACTORY_CODE)) {
                        Object factoryCodeObj = reaMap.get(ReaFieldParamVo.FACTORY_CODE);
                        if (StringUtil.isNotEmpty(factoryCodeObj)) {
                            pileInfoData.setFactoryCode(String.valueOf(factoryCodeObj));
                        }
                    }

                    result.setPileInfoData(pileInfoData);
                }
            }
            //获取用户信息
            Optional<OrderRecordEntity> recordDaoById = orderRecordDao.findById(occupyPileRecord.getOrderId());
            if (recordDaoById.isPresent()) {
                List<UserRecordEntity> userRecordEntityList = userRecordDao.findAllByOrderNumIn(Collections.singletonList(recordDaoById.get().getOrderNum()));
                if (CollectionUtils.isNotEmpty(userRecordEntityList)) {
                    OccupyPileRecordInfoDto.UserRecordDto userRecordDto = new OccupyPileRecordInfoDto.UserRecordDto();
                    BeanUtils.copyProperties(userRecordEntityList.get(0), userRecordDto);
                    result.setUserRecordDto(userRecordDto);
                }
            }
            //查询占桩计费历史数据信息
            Optional<OccupyPilePriceEntity> pilePriceDaoById = occupyPilePriceDao.findById(occupyPileRecord.getOccupyRateId());
            if (pilePriceDaoById.isPresent()) {
                OccupyPilePriceEntity occupyPilePriceEntity = pilePriceDaoById.get();
                OccupyPileRecordInfoDto.ChargingDetails chargingDetails = new OccupyPileRecordInfoDto.ChargingDetails();
                chargingDetails.setAvoidDuration(occupyPilePriceEntity.getAvoidDuration());
                chargingDetails.setTariffPeriod(occupyPilePriceEntity.getPartPeriodInfo());
                chargingDetails.setTariffStandard(occupyPilePriceEntity.getConfigPriceInfo());
                result.setChargingDetailsList(Collections.singletonList(chargingDetails));
            }
        }
        return ResponseResult.ok(result);
    }

    @Override
    public ResponseResult<List<SiteInfoDto>> findSiteBasicInfoByTenantId(String tenantId, String userId) {
        List<SiteInfoDto> resultList = Lists.newArrayList();
        //根据当前登录所属租户id，查询相关配置资产授权站点数据
        ResponseResult<List<OrganEmpowerListDto>> allOrganEmpowerByTenantId = systemService.findAllOrganEmpowerByUserId(userId);
        if (allOrganEmpowerByTenantId.isSuccess() && CollectionUtils.isNotEmpty(allOrganEmpowerByTenantId.getData())) {
            List<OrganEmpowerListDto> organEmpowerListDtos = allOrganEmpowerByTenantId.getData();
            Map<String, OrganEmpowerListDto> organEmpowerListDtoMap = organEmpowerListDtos.stream().collect(Collectors.toMap(OrganEmpowerListDto::getSiteId, organEmpowerListDto -> organEmpowerListDto, (k1, k2) -> k1));
            //根据多个站点id查询站点信息
            Map<String, SiteInfoDto> siteBasicInfoDtoMap = deviceService.findSiteBasicInfoByIds(
                    organEmpowerListDtos.stream().map(OrganEmpowerListDto::getSiteId).filter(StringUtil::isNotEmpty).distinct().collect(Collectors.toList())).getData();
            if (!siteBasicInfoDtoMap.isEmpty()) {
                siteBasicInfoDtoMap.forEach((k, v) -> {
                    OrganEmpowerListDto organEmpowerListDto = organEmpowerListDtoMap.get(k);
                    v.setAuthority(organEmpowerListDto.getAuthority());
                    resultList.add(v);
                });
            }
        }
        return ResponseResult.ok(resultList);
    }

    @Override
    public ResponseResult<List<OrderRecordDto>> findOrderRecordListByPileCodes(List<String> pileCodeList, String startTime, String endTime) {
        List<OrderRecordDto> resultList = Lists.newArrayList();

        List<OrderRecordEntity> orderRecordEntityList;
        if (StringUtil.isNotEmpty(startTime) && StringUtil.isNotEmpty(endTime)) {
            //根据多个充电桩编号和查询时间查询充放电记录数据
            orderRecordEntityList = orderRecordDao.findAllByPileCodeInAndEndTimeBetween(pileCodeList, startTime, endTime);
        } else {
            orderRecordEntityList = orderRecordDao.findAllByPileCodeIn(pileCodeList);
        }
        if (CollectionUtils.isNotEmpty(orderRecordEntityList)) {
            //根据多个电桩编码查询电桩基本详情数据
            Map<String, DeviceBasicInfoDto> deviceBasicInfoDtoMap = deviceService.findDeviceBasicInfoByCodes(orderRecordEntityList.stream().map(OrderRecordEntity::getPileCode).filter(StringUtil::isNotEmpty).distinct().collect(Collectors.toList())).getData();
            resultList = orderRecordEntityList.stream().map(orderRecordEntity -> {
                OrderRecordDto orderRecordDto = new OrderRecordDto();
                BeanUtils.copyProperties(orderRecordEntity, orderRecordDto);
                orderRecordDto.setCreateTime(localDateTimeToStr(orderRecordEntity.getCreateTime()));
                //获取电桩所属站点信息
                DeviceBasicInfoDto deviceBasicInfoDto = deviceBasicInfoDtoMap.get(orderRecordEntity.getPileCode());
                if (StringUtil.isNotEmpty(deviceBasicInfoDto)) {
                    orderRecordDto.setSiteId(deviceBasicInfoDto.getSiteId());
                    orderRecordDto.setSiteName(deviceBasicInfoDto.getSiteName());
                }
                return orderRecordDto;
            }).collect(Collectors.toList());
        }
        return ResponseResult.ok(resultList);
    }

    @Override
    public ResponseResult<List<OccupyPileRecordDto>> findOccupyPileRecordListByOrderIds(List<String> orderIdList, String startTime, String endTime) {
        List<OccupyPileRecordDto> resultList = Lists.newArrayList();

        List<OccupyPileRecordEntity> occupyPileRecordEntityList;
        if (StringUtil.isNotEmpty(startTime) && StringUtil.isNotEmpty(endTime)) {
            //根据多个订单记录id和查询时间查询占桩记录数据
            occupyPileRecordEntityList = occupyPileRecordDao.findAllByOrderIdInAndStartTimeBetween(orderIdList, startTime, endTime);
        } else {
            occupyPileRecordEntityList = occupyPileRecordDao.findAllByOrderIdIn(orderIdList);
        }
        if (CollectionUtils.isNotEmpty(occupyPileRecordEntityList)) {
            //根据多个电桩编码查询电桩基本详情数据
            Map<String, DeviceBasicInfoDto> deviceBasicInfoDtoMap = deviceService.findDeviceBasicInfoByCodes(occupyPileRecordEntityList.stream().map(OccupyPileRecordEntity::getPileCode).filter(StringUtil::isNotEmpty).distinct().collect(Collectors.toList())).getData();
            resultList = occupyPileRecordEntityList.stream().map(occupyPileRecordEntity -> {
                OccupyPileRecordDto occupyPileRecordDto = new OccupyPileRecordDto();
                BeanUtils.copyProperties(occupyPileRecordEntity, occupyPileRecordDto);
                //获取电桩所属站点信息
                DeviceBasicInfoDto deviceBasicInfoDto = deviceBasicInfoDtoMap.get(occupyPileRecordEntity.getPileCode());
                if (StringUtil.isNotEmpty(deviceBasicInfoDto)) {
                    occupyPileRecordDto.setSiteId(deviceBasicInfoDto.getSiteId());
                    occupyPileRecordDto.setSiteName(deviceBasicInfoDto.getSiteName());
                }
                return occupyPileRecordDto;
            }).collect(Collectors.toList());
        }
        return ResponseResult.ok(resultList);
    }

    @Override
    public ResponseResult<Map<String, Object>> findProcessAnalysisByOrderId(String orderId, List<String> functionLogoList) {
        Map<String, Object> resultMap = Maps.newHashMap();

        //根据订单id查询订单信息
        Optional<OrderRecordEntity> optional = orderRecordDao.findById(orderId);
        if (optional.isPresent()) {
            OrderRecordEntity recordEntity = optional.get();
            //根据电桩编码获取设备信息
            Map<String, DeviceBasicInfoDto> deviceInfoMap = deviceService.findDeviceBasicInfoByCodes(Collections.singletonList(recordEntity.getPileCode())).getData();
            if (MapUtils.isEmpty(deviceInfoMap) || !deviceInfoMap.containsKey(recordEntity.getPileCode())) {
                return ResponseResult.ok(resultMap);
            }
            DeviceBasicInfoDto deviceBasicInfoDto = deviceInfoMap.get(recordEntity.getPileCode());
            //开始时间
            String startTime = recordEntity.getStartTime();
            String endTime;
            if (StringUtil.isNotEmpty(startTime)) {
                //把开始时间重置成后一分钟正分钟点的时间
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                // 解析时间字符串
                LocalDateTime localDateTime = LocalDateTime.parse(startTime, formatter);
                // 增加一分钟
                startTime = localDateTimeToStr(localDateTime.plusMinutes(1).withSecond(0));
                if (StringUtil.isNotEmpty(recordEntity.getEndTime())) {
                    endTime = recordEntity.getEndTime();
                } else {
                    if (recordEntity.getOrderStatus() == 1) {
                        endTime = localDateTimeToStr(LocalDateTime.now());
                    } else {
                        endTime = getDateByType(startTime, 8, 2, 1);
                    }
                }

                //获取时间轴
                List<String> getDateTimeBetween = getLocalDateTimeBetween(strToLocalDateTime(startTime), strToLocalDateTime(endTime), "1m").stream().map(DateUtil::localDateTimeToStr).collect(Collectors.toList());

                DeviceIndexQueryVo deviceIndexQueryVo = new DeviceIndexQueryVo();

                Set<String> functionIndexSet = functionLogoList.stream().map(functionLogo -> functionLogo + "index" + (recordEntity.getGunCode() - 1)).collect(Collectors.toSet());
                Map<String, Set<String>> deviceFuctionMap = Maps.newHashMap();
                deviceFuctionMap.put(deviceBasicInfoDto.getId(), functionIndexSet);
                deviceIndexQueryVo.setDeviceFuctionMap(deviceFuctionMap);

                deviceIndexQueryVo.setStartTime(startTime);
                deviceIndexQueryVo.setEndTime(endTime);
                deviceIndexQueryVo.setTimeInterval("1m");
                //查询功能点索引历史数据
                ResponseResult<Map<String, Map<String, List<DeviceHistoryDto>>>> deviceHistoryIndexValueList = dataService.findDeviceHistoryIndexValueList(deviceIndexQueryVo);
                if (deviceHistoryIndexValueList.isSuccess() && !deviceHistoryIndexValueList.getData().isEmpty()
                        && deviceHistoryIndexValueList.getData().containsKey(deviceBasicInfoDto.getId())
                        && MapUtils.isNotEmpty(deviceHistoryIndexValueList.getData().get(deviceBasicInfoDto.getId()))) {
                    Map<String, List<DeviceHistoryDto>> historyIndexValueMap = deviceHistoryIndexValueList.getData().get(deviceBasicInfoDto.getId());

                    //循环功能点获取数据
                    functionIndexSet.stream().map(functionLogo -> functionLogo.substring(0, functionLogo.indexOf(StaticParamVo.INDEX))).forEach(substring -> {
                        //存储功能点数据
                        List<Double> dataList = Lists.newArrayList();
                        List<DeviceHistoryDto> historyDataList = historyIndexValueMap.get(substring);
                        if (CollectionUtils.isNotEmpty(historyDataList)) {
                            Map<String, DeviceHistoryDto> deviceHistoryDtoMap = historyDataList.stream().collect(Collectors.toMap(DeviceHistoryDto::getDateTime, deviceHistoryDto -> deviceHistoryDto, (k1, k2) -> k1));
                            //循环时间轴组装数据
                            getDateTimeBetween.forEach(dateTime -> {
                                DeviceHistoryDto deviceHistoryDto = deviceHistoryDtoMap.get(dateTime);
                                if (StringUtil.isNotEmpty(deviceHistoryDto)) {
                                    Integer dataType = deviceHistoryDto.getDataType();
                                    Object dataValue = deviceHistoryDto.getDataValue();
                                    dataList.add(getToDouble(dataTypeConvert(dataType, dataValue)));
                                } else {
                                    dataList.add(null);
                                }
                            });
                        }
                        resultMap.put(substring, dataList);
                    });
                }
                resultMap.put("xAxisList", getDateTimeBetween);
            }
        }
        return ResponseResult.ok(resultMap);
    }

    @Override
    public ResponseResult<List<OrderRecordExportDto>> findOrderRecordList(OrderRecordQueryVo orderQueryVo) {
        List<OrderRecordExportDto> resultList = Lists.newArrayList();
        //根据当前登录用户id，查询相关配置资产授权站点数据
        List<OrganEmpowerListDto> organEmpowerListDtos = systemService.findAllOrganEmpowerByUserId(orderQueryVo.getUserId()).getData();
        if (CollectionUtils.isEmpty(organEmpowerListDtos)) {
            return ResponseResult.ok(resultList);
        }
        //根据站点id查询
        if (StringUtil.isNotEmpty(orderQueryVo.getSiteId())) {
            organEmpowerListDtos = organEmpowerListDtos.stream().filter(o -> StringUtil.isNotEmpty(o.getSiteId()) && o.getSiteId().equals(orderQueryVo.getSiteId())).collect(Collectors.toList());
        }
        if (CollectionUtils.isEmpty(organEmpowerListDtos)) {
            return ResponseResult.ok(resultList);
        }

        //根据多个站点id查询站点信息
        Map<String, SiteInfoDto> siteBasicInfoDtoMap = deviceService.findSiteBasicInfoByIds(organEmpowerListDtos.stream().map(OrganEmpowerListDto::getSiteId)
                .filter(StringUtil::isNotEmpty).distinct().collect(Collectors.toList())).getData();
        if (MapUtils.isEmpty(siteBasicInfoDtoMap)) {
            return ResponseResult.ok(resultList);
        }
        List<SiteInfoDto> siteBasicInfoDtoList = new ArrayList<>(siteBasicInfoDtoMap.values());

        //根据多个站点id，查询站点下所有电桩设备信息
        List<String> siteIds = siteBasicInfoDtoList.stream().map(SiteInfoDto::getId).collect(Collectors.toList());
        ResponseResult<Map<String, List<DeviceBasicInfoDto>>> deviceBasicInfoBySiteIds = deviceService.findDeviceBasicInfoBySiteIds(siteIds, DeviceTypeParamVo.CDZ);
        if (deviceBasicInfoBySiteIds.isSuccess() && !deviceBasicInfoBySiteIds.getData().isEmpty()) {
            List<DeviceBasicInfoDto> deviceBasicInfoDtos = Lists.newArrayList();
            List<DeviceBasicInfoDto> finalDeviceBasicInfoDtos = deviceBasicInfoDtos;
            deviceBasicInfoBySiteIds.getData().forEach((k, v) -> finalDeviceBasicInfoDtos.addAll(v));
            //根据电桩编码查询
            if (StringUtil.isNotEmpty(orderQueryVo.getPileCode())) {
                deviceBasicInfoDtos = deviceBasicInfoDtos.stream().filter(d -> StringUtil.isNotEmpty(d.getDeviceNumber())
                        && d.getDeviceNumber().contains(orderQueryVo.getPileCode())).collect(Collectors.toList());
            }
            //根据电桩类型筛选数据
            Integer pileType = orderQueryVo.getPileType();
            if (StringUtil.isNotEmpty(pileType)) {
                deviceBasicInfoDtos = deviceBasicInfoDtos.stream().filter(d -> d.getTypeId().equals(String.valueOf(pileType))).collect(Collectors.toList());
            }
            //根据多个电桩编码查询订单记录数据
            List<Integer> orderTypes = JSON.parseArray(orderQueryVo.getOrderTypes(), Integer.class);
            List<String> pileCodes = deviceBasicInfoDtos.stream().map(DeviceBasicInfoDto::getDeviceNumber).filter(StringUtil::isNotEmpty).distinct().collect(Collectors.toList());
            //关键字查询电桩编号
            if (StringUtil.isNotEmpty(orderQueryVo.getKeywordType()) && StringUtil.isNotEmpty(orderQueryVo.getKeyword())) {
                if (orderQueryVo.getKeywordType() == 5) {
                    pileCodes = pileCodes.stream().filter(pileCode -> pileCode.contains(orderQueryVo.getKeyword())).collect(Collectors.toList());
                }
            }
            if (CollectionUtils.isEmpty(pileCodes)) {
                return ResponseResult.ok(resultList);
            }
            List<String> finalPileCodes = pileCodes;
            List<OrderRecordEntity> orderRecordEntityList = orderRecordDao.findAll((Specification<OrderRecordEntity>) (root, cq, cb) -> {
                List<Predicate> predicates = Lists.newArrayList();
                predicates.add(cb.in(root.get("runMode")).value(orderTypes));
                predicates.add(cb.in(root.get("pileCode")).value(finalPileCodes));
                if (StringUtil.isNotEmpty(orderQueryVo.getKeywordType()) && StringUtil.isNotEmpty(orderQueryVo.getKeyword())) { //关键字查询
                    switch (orderQueryVo.getKeywordType()) {
                        case 1: //订单号
                            predicates.add(cb.like(root.get("orderNum"), "%" + orderQueryVo.getKeyword() + "%"));
                            break;
                        case 2: //手机号
                            predicates.add(cb.equal(root.get("accountType"), 3));
                            predicates.add(cb.like(root.get("accountData"), "%" + orderQueryVo.getKeyword() + "%"));
                            break;
                        case 3: //VIN码
                            predicates.add(cb.like(root.get("busVin"), "%" + orderQueryVo.getKeyword() + "%"));
                            break;
                        case 4: //电卡ID
                            predicates.add(cb.equal(root.get("accountType"), 1));
                            predicates.add(cb.like(root.get("accountData"), "%" + orderQueryVo.getKeyword() + "%"));
                            break;
                    }
                }
                //根据发起者查询
                if (StringUtil.isNotEmpty(orderQueryVo.getRunMode())) {
                    predicates.add(cb.equal(root.get("starter"), orderQueryVo.getRunMode()));
                }
                //根据订单状态查询
                if (StringUtil.isNotEmpty(orderQueryVo.getOrderStatus())) {
                    predicates.add(cb.equal(root.get("orderStatus"), orderQueryVo.getOrderStatus()));
                }
                //根据充电开始时间查询
                if (StringUtil.isNotEmpty(orderQueryVo.getStartAlsoStartDate()) && StringUtil.isNotEmpty(orderQueryVo.getStartAlsoEndDate())) {
                    predicates.add(cb.between(root.get("startTime"), getDayStart(orderQueryVo.getStartAlsoStartDate()), getDayEnd(orderQueryVo.getStartAlsoEndDate())));
                }
                //根据充电结束时间查询
                if (StringUtil.isNotEmpty(orderQueryVo.getEndAlsoStartDate()) && StringUtil.isNotEmpty(orderQueryVo.getEndAlsoEndDate())) {
                    predicates.add(cb.between(root.get("endTime"), getDayStart(orderQueryVo.getEndAlsoStartDate()), getDayEnd(orderQueryVo.getEndAlsoEndDate())));
                }
                //根据来源平台id查询
                if (StringUtil.isNotEmpty(orderQueryVo.getPlatformLogo())) {
                    predicates.add(cb.equal(root.get("platformLogo"), orderQueryVo.getPlatformLogo()));
                }
                //根据电量最大最小值过滤
                if (StringUtil.isNotEmpty(orderQueryVo.getMaxQt()) && StringUtil.isNotEmpty(orderQueryVo.getMinQt())) {
                    predicates.add(cb.between(root.get("totalQt"), orderQueryVo.getMinQt(), orderQueryVo.getMaxQt()));
                }
                //根据异常类型过滤
                if (StringUtil.isNotEmpty(orderQueryVo.getAbnormalType())) {
                    if (orderQueryVo.getAbnormalType() != 0) {
                        predicates.add(cb.like(root.get("abnormalCode"), "%" + orderQueryVo.getAbnormalType() + "%"));
                    }
                    if (orderQueryVo.getAbnormalType() == 0) {
                        predicates.add(cb.isNull(root.get("abnormalCode")));
                    }
                }
                return cb.and(predicates.toArray(new Predicate[0]));
            });
            if (CollectionUtils.isEmpty(orderRecordEntityList)) { //订单数据为空 直接返回
                return ResponseResult.ok(resultList);
            }

            //根据多个平台标识查询平台信息
            List<String> platformLogoList = orderRecordEntityList.stream().map(OrderRecordEntity::getPlatformLogo).filter(StringUtil::isNotEmpty).distinct().collect(Collectors.toList());
            Map<String, ChargePlatformInfoDto> platformInfoDtoMap = systemService.findChargePlatformInfoByLogos(platformLogoList).getData();
            //电桩基本信息
            Map<String, DeviceBasicInfoDto> deviceBasicInfoDtoMap = deviceBasicInfoDtos.stream().collect(Collectors.toMap(DeviceBasicInfoDto::getDeviceNumber, deviceBasicInfoDto -> deviceBasicInfoDto, (k1, k2) -> k1));
            //根据多个订单id查询用户记录数据
            Map<String, UserRecordEntity> userRecordEntityMap = Maps.newHashMap();
            List<String> orderNumList = orderRecordEntityList.stream().map(OrderRecordEntity::getOrderNum).filter(StringUtil::isNotEmpty).distinct().collect(Collectors.toList());
            List<UserRecordEntity> userRecordEntityList = userRecordDao.findAllByOrderNumIn(orderNumList);
            if (CollectionUtils.isNotEmpty(userRecordEntityList)) {
                userRecordEntityMap = userRecordEntityList.stream().collect(Collectors.toMap(UserRecordEntity::getOrderNum, userRecordEntity -> userRecordEntity, (k1, k2) -> k1));
            }
            //根据多个订单编码查询结算记录信息
            Map<String, SettlementRecordEntity> settlementRecordMap = settlementRecordDao.findAllByOrderNumIn(orderNumList).stream().collect(Collectors.toMap(SettlementRecordEntity::getOrderNum,
                    s -> s, (k1, k2) -> k1));

            Map<String, UserRecordEntity> finalUserRecordEntityMap = userRecordEntityMap;
            resultList = orderRecordEntityList.stream().map(orderRecordEntity -> {
                OrderRecordExportDto result = new OrderRecordExportDto();
                BeanUtils.copyProperties(orderRecordEntity, result);
                result.setCreateTime(localDateTimeToStr(orderRecordEntity.getCreateTime()));
                //获取车牌号
                UserRecordEntity userRecordEntity = finalUserRecordEntityMap.get(orderRecordEntity.getId());
                result.setPlateNumber(userRecordEntity != null ? userRecordEntity.getPlateNumber() : null);
                //获取电桩基本详情数据
                DeviceBasicInfoDto deviceBasicInfoDto = deviceBasicInfoDtoMap.get(orderRecordEntity.getPileCode());
                if (StringUtil.isNotEmpty(deviceBasicInfoDto)) {
                    //获取站点名称
                    result.setSiteName(deviceBasicInfoDto.getSiteName());
                    //获取电桩类型
                    result.setPileType(deviceBasicInfoDto.getTypeId() != null ? Integer.parseInt(deviceBasicInfoDto.getTypeId()) : null);
                }
                //获取平台信息
                if (StringUtil.isNotEmpty(orderRecordEntity.getPlatformLogo()) && platformInfoDtoMap.containsKey(orderRecordEntity.getPlatformLogo())) {
                    result.setPlatformName(platformInfoDtoMap.get(orderRecordEntity.getPlatformLogo()).getPlatformName());
                }
                //计算充放电时长
                String startTime = orderRecordEntity.getStartTime();
                String endTime = orderRecordEntity.getEndTime();
                if (StringUtil.isNotEmpty(startTime) && StringUtil.isNotEmpty(endTime)) {
                    result.setDuration(secToTime((int) ChronoUnit.SECONDS.between(strToLocalDateTime(startTime), strToLocalDateTime(endTime))));
                }
                //获取结算记录信息
                if (settlementRecordMap.containsKey(orderRecordEntity.getOrderNum())) {
                    SettlementRecordEntity settlementRecord = settlementRecordMap.get(orderRecordEntity.getOrderNum());
                    result.setActualTotalCost(settlementRecord.getActualTotalCost());
                    result.setActualTotalElect(settlementRecord.getActualTotalElect());
                    result.setActualTotalFee(settlementRecord.getActualTotalFee());
                }
                //账号类型 1-充/放电卡ID 2-VIN码 3-手机号
                if (StringUtil.isNotEmpty(orderRecordEntity.getAccountType())) {
                    switch (orderRecordEntity.getAccountType()) {
                        case 1:
                            result.setCardNumber(orderRecordEntity.getAccountData());
                            break;
                        case 2:
                            result.setBusVin(orderRecordEntity.getAccountData());
                            break;
                        case 3:
                            result.setPhone(orderRecordEntity.getAccountData());
                            break;
                    }
                }
                return result;
            }).collect(Collectors.toList());
            resultList = resultList.stream().sorted(Comparator.comparing(OrderRecordExportDto::getCreateTime).reversed()).collect(Collectors.toList());
        }
        return ResponseResult.ok(resultList);
    }

    @Override
    public ResponseResult<InterflowOrderRecordDto> queryOrderRecordByOrderCode(String orderNum) {
        InterflowOrderRecordDto result = new InterflowOrderRecordDto();
        //根据订单编码查询订单信息
        Optional<OrderRecordEntity> orderRecordDaoOne = orderRecordDao.findOne(Example.of(OrderRecordEntity.builder().orderNum(orderNum).build()));
        if (orderRecordDaoOne.isPresent()) {
            OrderRecordEntity recordEntity = orderRecordDaoOne.get();
            BeanUtils.copyProperties(recordEntity, result);
            //根据订单编码查询计费详情
            /*List<ChargeTariffRecordEntity> tariffRecordEntityList = chargeTariffRecordDao.findAllByOrderNum(orderNum);
            if (CollectionUtils.isNotEmpty(tariffRecordEntityList)) {
                result.setChargingDetailsList(tariffRecordEntityList.stream().map(chargeTariffRecordEntity -> {
                    InterflowOrderRecordDto.ChargingDetails chargingDetails = new InterflowOrderRecordDto.ChargingDetails();
                    BeanUtils.copyProperties(chargeTariffRecordEntity, chargingDetails);
                    return chargingDetails;
                }).collect(Collectors.toList()));
            }*/
        }
        return ResponseResult.ok(result);
    }

    @Override
    public ResponseResult<String> saveOrUpdateOrderRecord(InterflowOrderRecordDto interflowOrderRecordDto) {
        if (StringUtil.isNotEmpty(interflowOrderRecordDto)) {
            OrderRecordEntity recordEntity = new OrderRecordEntity();
            BeanUtils.copyProperties(interflowOrderRecordDto, recordEntity);
            //根据电桩编号查询站点id
            String pileCode = interflowOrderRecordDto.getPileCode();
            if (StringUtil.isNotEmpty(pileCode)) {
                Map<String, DeviceBasicInfoDto> deviceInfoMap = deviceService.findDeviceBasicInfoByCodes(Collections.singletonList(pileCode)).getData();
                if (MapUtils.isNotEmpty(deviceInfoMap) && deviceInfoMap.containsKey(pileCode)) {
                    recordEntity.setSiteId(deviceInfoMap.get(pileCode).getSiteId());
                }
            }
            orderRecordDao.save(recordEntity);
            //先删除原来的计费详情数据，再插入新的计费详情
            List<InterflowOrderRecordDto.ChargingDetails> chargingDetailsList = interflowOrderRecordDto.getChargingDetailsList();
            if (CollectionUtils.isNotEmpty(chargingDetailsList)) {
                chargeTariffRecordDao.deleteAllByOrderNum(interflowOrderRecordDto.getOrderNum());
                chargeTariffRecordDao.saveAll(chargingDetailsList.stream().map(chargingDetails -> {
                    ChargeTariffRecordEntity chargeTariffRecordEntity = new ChargeTariffRecordEntity();
                    BeanUtils.copyProperties(chargingDetails, chargeTariffRecordEntity);
                    return chargeTariffRecordEntity;
                }).collect(Collectors.toList()));
            }
            return ResponseResult.ok(ResponseResult.SUCCESS);
        }
        return ResponseResult.error(ResponseResult.FAIL);
    }

    @Override
    public ResponseResult<Map<String, List<OrderRecordDto>>> queryOrderRecordByPileCodes(List<String> pileCodeList, String startTime, String endTime) {
        //返回的对象
        Map<String, List<OrderRecordDto>> resultMap = Maps.newHashMap();

        ResponseResult<List<OrderRecordDto>> orderRecordListByPileCodes = findOrderRecordListByPileCodes(pileCodeList, startTime, endTime);
        if (orderRecordListByPileCodes.isSuccess() && CollectionUtils.isNotEmpty(orderRecordListByPileCodes.getData())) {
            resultMap = orderRecordListByPileCodes.getData().stream().collect(Collectors.groupingBy(OrderRecordDto::getPileCode));
        }
        return ResponseResult.ok(resultMap);
    }

    @Override
    public ResponseResult<List<AffiliatesInfoDto>> findOperatorListByUserId(String userId) {
        List<AffiliatesInfoDto> resultList = Lists.newArrayList();
        //根据当前登录用户id，查询相关配置资产授权站点数据
        ResponseResult<List<OrganEmpowerListDto>> allOrganEmpowerByTenantId = systemService.findAllOrganEmpowerByUserId(userId);
        if (allOrganEmpowerByTenantId.isSuccess() && CollectionUtils.isNotEmpty(allOrganEmpowerByTenantId.getData())) {
            List<OrganEmpowerListDto> organEmpowerListDtos = allOrganEmpowerByTenantId.getData();
            //根据多个站点id，查询所有关联方列表
            ResponseResult<Map<String, List<AffiliatesInfoDto>>> siteAffiliatesInfoByIds = deviceService.findSiteAffiliatesInfoByIds(organEmpowerListDtos.stream().map(OrganEmpowerListDto::getSiteId).distinct().collect(Collectors.toList()));
            if (siteAffiliatesInfoByIds.isSuccess() && MapUtils.isNotEmpty(siteAffiliatesInfoByIds.getData())) {
                //过滤出所有包含运营单位的数据，并根据租户id去除重复
                List<AffiliatesInfoDto> affiliatesInfoDtoList = siteAffiliatesInfoByIds.getData().values().stream().flatMap(Collection::stream).filter(a -> a.getAffiliateTypes().contains("3")).collect(Collectors.toList());
                if (CollectionUtils.isNotEmpty(affiliatesInfoDtoList)) {
                    resultList = Lists.newArrayList(affiliatesInfoDtoList.stream()
                            .collect(Collectors.toMap(AffiliatesInfoDto::getTenantId, affiliatesInfoDto -> affiliatesInfoDto, (k1, k2) -> k1)).values());
                }
            }
        }
        return ResponseResult.ok(resultList);
    }

    @Override
    public ResponseResult<OrderDetailDto> findOrderDetailByOrderNum(String orderNum) {
        OrderDetailDto result = new OrderDetailDto();
        //根据订单编号查询订单信息
        OrderRecordEntity recordEntity = orderRecordDao.findByOrderNum(orderNum);
        if (StringUtil.isNotEmpty(recordEntity)) {
            BeanUtils.copyProperties(recordEntity, result);
            LocalDateTime createTime = recordEntity.getCreateTime();
            if (StringUtil.isNotEmpty(createTime)) {
                result.setCreateTime(localDateTimeToStr(createTime));
            }
            LocalDateTime updateTime = recordEntity.getUpdateTime();
            if (StringUtil.isNotEmpty(updateTime)) {
                result.setUpdateTime(localDateTimeToStr(updateTime));
            }
            //计算充放电时长
            String startTime = recordEntity.getStartTime();
            String endTime = null;
            if (StringUtil.isNotEmpty(recordEntity.getEndTime())) {
                endTime = recordEntity.getEndTime();
            } else {
                if (recordEntity.getOrderStatus() == 1) {
                    endTime = localDateTimeToStr(LocalDateTime.now());
                } else if (StringUtil.isNotEmpty(startTime)) {
                    endTime = getDateByType(startTime, 8, 2, 1);
                }
            }
            //根据订单状态判断，如果是在进行充电中的订单，则取实时状态中的数据
            if (recordEntity.getOrderStatus() == 1) {
                PileRealModel pileRealModel = getPileRealModel(recordEntity.getPileCode());
                if (StringUtil.isNotEmpty(pileRealModel) && StringUtil.isNotEmpty(pileRealModel.getPileCode())) {
                    Map<String, PileRealModel.GunRealModel> gunRealModelMap = pileRealModel.getGunRealModelMap();
                    if (!gunRealModelMap.isEmpty() && gunRealModelMap.containsKey(String.valueOf(recordEntity.getGunCode()))) {
                        PileRealModel.GunRealModel gunRealModel = gunRealModelMap.get(String.valueOf(recordEntity.getGunCode()));
                        result.setEndSoc(gunRealModel.getBatterySoc());
                        result.setTotalQt(getToDouble(gunRealModel.getTotalQt()));
                        result.setTotalCost(gunRealModel.getTotalCost() != null ? getToBigDecimal(gunRealModel.getTotalCost()) : null);
                    }
                }
            }
            //获取站点信息
            if (StringUtil.isNotEmpty(recordEntity.getSiteId())) {
                ResponseResult<Map<String, SiteInfoDto>> siteBasicInfoByIds = deviceService.findSiteBasicInfoByIds(Collections.singletonList(recordEntity.getSiteId()));
                if (siteBasicInfoByIds.isSuccess() && !siteBasicInfoByIds.getData().isEmpty() && siteBasicInfoByIds.getData().containsKey(recordEntity.getSiteId())) {
                    SiteInfoDto siteInfoDto = siteBasicInfoByIds.getData().get(recordEntity.getSiteId());
                    result.setSiteName(siteInfoDto.getSiteName());
                    //获取站点位置信息
                    String siteReadwriteObject = siteInfoDto.getSiteReadwriteObject();
                    if (StringUtil.isNotEmpty(siteReadwriteObject)) {
                        Map<String, Object> parseObjectMap = JSON.parseObject(siteReadwriteObject, new TypeReference<Map<String, Object>>() {
                        });
                        //获取站点位置信息
                        if (parseObjectMap.containsKey(SiteFieldParamVo.LOCATION)) {
                            result.setSiteLocation(JSON.toJSONString(parseObjectMap.get(SiteFieldParamVo.LOCATION)));
                        }
                    }
                }
            }
            //根据开始结束时间计算订单持续了多长时间
            if (StringUtil.isNotEmpty(startTime) && StringUtil.isNotEmpty(endTime)) {
                result.setChargeDuration(secToTime((int) ChronoUnit.SECONDS.between(strToLocalDateTime(startTime), strToLocalDateTime(endTime))));
            }
            //获取平台信息
            ResponseResult<Map<String, ChargePlatformInfoDto>> chargePlatformInfoByLogos = systemService.findChargePlatformInfoByLogos(Collections.singletonList(recordEntity.getPlatformLogo()));
            if (chargePlatformInfoByLogos.isSuccess() && !chargePlatformInfoByLogos.getData().isEmpty()) {
                result.setPlatformName(chargePlatformInfoByLogos.getData().get(recordEntity.getPlatformLogo()).getPlatformName());
            }
            //获取计费详情列表
            List<ChargeTariffRecordEntity> chargeTariffRecordEntities = chargeTariffRecordDao.findAllByOrderNum(recordEntity.getOrderNum());
            if (CollectionUtils.isNotEmpty(chargeTariffRecordEntities)) {
                result.setChargingDetailsList(chargeTariffRecordEntities.stream().map(chargeTariffRecordEntity -> {
                    OrderDetailDto.ChargingDetails chargingDetails = new OrderDetailDto.ChargingDetails();
                    BeanUtils.copyProperties(chargeTariffRecordEntity, chargingDetails);
                    return chargingDetails;
                }).collect(Collectors.toList()));
            }
            //查询订单结算记录信息
            SettlementRecordEntity settlementRecordEntity = settlementRecordDao.findByOrderNum(orderNum);
            Optional.ofNullable(settlementRecordEntity)
                    .ifPresent(entity -> {
                        OrderDetailDto.SettlementRecord settlementRecord = new OrderDetailDto.SettlementRecord();
                        BeanUtils.copyProperties(entity, settlementRecord);
                        result.setSettlementRecord(settlementRecord);
                    });
            //查询订单电压、电流、功率数据
            if (StringUtil.isNotEmpty(startTime) && StringUtil.isNotEmpty(endTime)) {//有开始和结束时间才会查询
                ResponseResult<Map<String, DeviceBasicInfoDto>> deviceBasicInfoByCodes = deviceService.findDeviceBasicInfoByCodes(Collections.singletonList(recordEntity.getPileCode()));
                if (deviceBasicInfoByCodes.isSuccess() && !deviceBasicInfoByCodes.getData().isEmpty()) {
                    DeviceBasicInfoDto deviceBasicInfoDto = deviceBasicInfoByCodes.getData().get(recordEntity.getPileCode());
                    //查询指定变量节点历史数据
                    VarNodeValueVo varNodeValueVo = new VarNodeValueVo();
                    varNodeValueVo.setVarCodeList(Arrays.asList(SystemVariableEnum.GUNOUTPUTCURRENT.getCode(), SystemVariableEnum.GUNOUTPUTVOLTAGE.getCode(), SystemVariableEnum.GUNPOWER.getCode()));
                    varNodeValueVo.setDeviceIdList(Collections.singletonList(deviceBasicInfoDto.getId()));
                    varNodeValueVo.setStartTime(startTime);
                    varNodeValueVo.setEndTime(endTime);
                    varNodeValueVo.setIndex(recordEntity.getGunCode());
                    varNodeValueVo.setTimeInterval("1m");
                    Map<String, Map<String, Object>> deviceVarNodeValueByIds = TogetherCommonUtil.findDeviceVarNodeValueByIds(varNodeValueVo);
                    if (!deviceVarNodeValueByIds.isEmpty()) {
                        Map<String, Object> varNodeValueMap = deviceVarNodeValueByIds.get(deviceBasicInfoDto.getId());
                        //获取电枪电流数据
                        if (varNodeValueMap.containsKey(SystemVariableEnum.GUNOUTPUTCURRENT.getCode())) {
                            result.setCurrentList(JSON.parseArray(JSON.toJSONString(varNodeValueMap.get(SystemVariableEnum.GUNOUTPUTCURRENT.getCode())), Double.class));
                        }
                        //获取电枪电压数据
                        if (varNodeValueMap.containsKey(SystemVariableEnum.GUNOUTPUTVOLTAGE.getCode())) {
                            result.setVoltageList(JSON.parseArray(JSON.toJSONString(varNodeValueMap.get(SystemVariableEnum.GUNOUTPUTVOLTAGE.getCode())), Double.class));
                        }
                        //获取电枪功率数据
                        if (varNodeValueMap.containsKey(SystemVariableEnum.GUNPOWER.getCode())) {
                            result.setPowerList(JSON.parseArray(JSON.toJSONString(varNodeValueMap.get(SystemVariableEnum.GUNPOWER.getCode())), Double.class));
                        }
                        result.setXAxisList(JSON.parseArray(JSON.toJSONString(varNodeValueMap.get("xAxisList")), String.class));
                    }
                }
            }
        }
        return ResponseResult.ok(result);
    }

    @Override
    public ResponseResult<List<OrderInfoDto>> findOrderListByAccountData(List<String> accountDataList, Integer runMode) {
        List<OrderInfoDto> resultList = Lists.newArrayList();

        List<OrderRecordEntity> orderList;
        if (StringUtil.isEmpty(runMode)) {
            orderList = orderRecordDao.findAllByAccountDataIn(accountDataList);
        } else {
            orderList = orderRecordDao.findAllByAccountDataInAndRunMode(accountDataList, runMode);
        }
        if (CollectionUtils.isNotEmpty(orderList)) {
            //获取多个站点id，查询站点信息
            List<String> stationIds = orderList.stream().map(OrderRecordEntity::getSiteId).distinct().collect(Collectors.toList());
            Map<String, SiteInfoDto> siteInfoDtoMap = deviceService.findSiteBasicInfoByIds(stationIds).getData();
            resultList = orderList.stream().map(orderRecordEntity -> {
                OrderInfoDto orderInfoDto = new OrderInfoDto();
                BeanUtils.copyProperties(orderRecordEntity, orderInfoDto);
                //获取站点名称
                if (!siteInfoDtoMap.isEmpty() && siteInfoDtoMap.containsKey(orderRecordEntity.getSiteId())) {
                    SiteInfoDto siteInfoDto = siteInfoDtoMap.get(orderRecordEntity.getSiteId());
                    orderInfoDto.setSiteName(siteInfoDto.getSiteName());
                    orderInfoDto.setOperateUnitId(siteInfoDto.getOperatorId());
                    orderInfoDto.setOperateUnitName(siteInfoDto.getOperatorName());
                }
                return orderInfoDto;
            }).collect(Collectors.toList());
        }
        return ResponseResult.ok(resultList);
    }

    @Override
    public ResponseResult<List<AppInHandOrderDto>> findAppInHandOrderListByAppletUserId(String appletUserId, Integer orderType) {
        List<AppInHandOrderDto> resultList = Lists.newArrayList();
        List<OrderRecordEntity> orderList = Lists.newArrayList();
        //根据小程序用户id查询手机号
        Optional<AppletUserEntity> optional = appletUserDao.findById(appletUserId);
        if (optional.isPresent()) {
            String phoneNum = optional.get().getPhoneNum();
            if (StringUtil.isNotEmpty(orderType)) {
                //查询充放电订单
                if (orderType == 0 || orderType == 1) {
                    orderList = orderRecordDao.findAllByAccountTypeAndAccountDataAndOrderStatusInAndRunMode(3, phoneNum, Arrays.asList(1, 6), orderType);
                }
            } else {
                //查询小程序用户进行中的订单
                orderList = orderRecordDao.findAllByAccountTypeAndAccountDataAndOrderStatusIn(3, phoneNum, Arrays.asList(1, 6));
            }
            if (CollectionUtils.isNotEmpty(orderList)) {
                resultList.addAll(orderList.stream().map(o -> {
                    AppInHandOrderDto result = new AppInHandOrderDto();
                    BeanUtils.copyProperties(o, result);
                    result.setAppletUserId(o.getAccountData());
                    result.setOrderState(o.getOrderStatus());
                    if (StringUtil.isNotEmpty(o.getOrderStatus()) && o.getOrderStatus() == 6) {
                        result.setOrderState(2);
                    }
                    return result;
                }).collect(Collectors.toList()));
            }
        }
        return ResponseResult.ok(resultList);
    }

    @Override
    public ResponseResult<Integer> checkSitePassword(String siteId, String password) {
        return deviceService.checkSitePassword(siteId, password);
    }

    @Override
    public ResponseResult<OrderTradeMoneyDto> findOrderTradeMoneyById(String orderId) {
        //返回的对象
        OrderTradeMoneyDto result = new OrderTradeMoneyDto();

        //根据订单id查询订单信息
        Optional<OrderRecordEntity> optional = orderRecordDao.findById(orderId);
        if (optional.isPresent()) {
            OrderRecordEntity orderRecord = optional.get();
            if (StringUtil.isEmpty(orderRecord.getOrderStatus())) {
                return ResponseResult.paramError("订单状态异常");
            }
            if (orderRecord.getOrderStatus() == 1 || orderRecord.getOrderStatus() == 6) {
                return ResponseResult.paramError("订单进行中,无法退款");
            }
            if (StringUtil.isEmpty(orderRecord.getPrepayMoney()) || orderRecord.getPrepayMoney().compareTo(BigDecimal.ZERO) <= 0) {
                return ResponseResult.paramError("免支付充电的用户,不支持退款");
            }
            result.setPrepayMoney(orderRecord.getPrepayMoney());
            //查询可退款金额
            BigDecimal maxRefundMoney = webAppService.findRefundMoneyByOrderNum(orderRecord.getOrderNum()).getData();
            if (maxRefundMoney.compareTo(BigDecimal.ZERO) <= 0) {
                maxRefundMoney = new BigDecimal("0.0");
//                return ResponseResult.paramError("可退款金额为0,不支持退款");
            }
            result.setMaxRefundMoney(maxRefundMoney);
            result.setRefundMoney(orderRecord.getPrepayMoney().subtract(maxRefundMoney));
            return ResponseResult.ok(result);
        }
        return ResponseResult.paramError("未查到对应的订单信息");
    }

    @Override
    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class)
    public ResponseResult<Void> orderRefund(OrderRefundVo orderRefundVo) {
        Optional<OrderRecordEntity> optional = orderRecordDao.findById(orderRefundVo.getOrderId());
        if (optional.isPresent()) {
            OrderRecordEntity orderRecord = optional.get();
            //查询可退款金额
            BigDecimal maxRefundMoney = webAppService.findRefundMoneyByOrderNum(orderRecord.getOrderNum()).getData();
            if (maxRefundMoney.compareTo(BigDecimal.ZERO) <= 0) {
                return ResponseResult.paramError("可退款金额为0,不支持退款");
            }
            if (orderRefundVo.getRefundMoney().compareTo(maxRefundMoney) > 0) {
                return ResponseResult.paramError("退款金额超出可退款金额");
            }
            //根据用户id查询用户信息
            if (StringUtil.isEmpty(orderRefundVo.getUserId())) {
                return ResponseResult.paramError("用户id为空, 不允许退款");
            }

            String userName = null;
            UserDto user = systemService.findUserInfoByIdsFeign(Collections.singletonList(orderRefundVo.getUserId())).getData().get(orderRefundVo.getUserId());
            if (user != null) {
                userName = user.getFullName();
            }

            //平台人工退款
            AppletChargeRefundVo appletRefundVo = new AppletChargeRefundVo();
            appletRefundVo.setOrderNum(orderRecord.getOrderNum());
            appletRefundVo.setPileCode(orderRecord.getPileCode());
            appletRefundVo.setRefundMoney(orderRefundVo.getRefundMoney());
            appletRefundVo.setPlatformType(1);
            appletRefundVo.setType(3);
            appletRefundVo.setRefundOperator(userName);
            ResponseResult<Void> result = webAppService.appletChargeRefund(appletRefundVo);
            if (result.isSuccess()) {
                //订单挂起则 进行补单
                if (StringUtil.isNotEmpty(orderRecord.getOrderStatus()) && orderRecord.getOrderStatus() == 4) {
                    List<RepairOrderRecordEntity> repairOrderRecordList = repairOrderRecordDao.findAllByOrderNum(orderRecord.getOrderNum());
                    RepairOrderRecordEntity repairOrderRecord;
                    if (CollectionUtils.isNotEmpty(repairOrderRecordList)) {
                        repairOrderRecord = repairOrderRecordList.get(0);
                        repairOrderRecord.setRepairStatus(2);
                        if (StringUtil.isNotEmpty(repairOrderRecord.getCreateTime())) {
                            repairOrderRecord.setExceptionTime(DateUtil.secToTime(DateUtil.compareDiffBetweenSecond(repairOrderRecord.getCreateTime(),
                                    LocalDateTime.now())));
                        }
                        repairOrderRecord.setRepairOperator(userName);
                    } else {
                        repairOrderRecord = new RepairOrderRecordEntity();
                        repairOrderRecord.setOrderNum(orderRecord.getOrderNum());
                        repairOrderRecord.setRepairStatus(2);
//                        repairOrderRecord.setExceptionTime();
                        repairOrderRecord.setRepairOperator(userName);
                    }
                    repairOrderRecordDao.save(repairOrderRecord);
                }
                //更新订单状态(充电完成)
                orderRecord.setOrderStatus(2);
                orderRecordDao.save(orderRecord);

                return ResponseResult.ok();
            }
            return ResponseResult.paramError("退款过程中异常，请稍后查询订单信息");
        }
        return ResponseResult.paramError(ResponseResult.PARAM_ERROR);
    }

    @Override
    public ResponseResult<Void> updateOrderStatus(String orderId, String userId) {
        //根据订单id查询订单状态
        Optional<OrderRecordEntity> optional = orderRecordDao.findById(orderId);
        if (optional.isPresent()) {
            OrderRecordEntity orderRecord = optional.get();
            Integer orderStatus = orderRecord.getOrderStatus();
            if (StringUtil.isEmpty(orderStatus) || orderStatus != 4) {
                return ResponseResult.paramError("该订单不是订单挂起订单, 不允许修改状态");
            }
            BigDecimal prepayMoney = orderRecord.getPrepayMoney();
            if (StringUtil.isNotEmpty(prepayMoney) && prepayMoney.compareTo(BigDecimal.ZERO) > 0) {
                return ResponseResult.paramError("预付金额大于0, 不允许只修改状态");
            }
            //订单挂起则 进行补单
            String userName = null;
            UserDto user = systemService.findUserInfoByIdsFeign(Collections.singletonList(userId)).getData().get(userId);
            if (user != null) {
                userName = user.getFullName();
            }
            List<RepairOrderRecordEntity> repairOrderRecordList = repairOrderRecordDao.findAllByOrderNum(orderRecord.getOrderNum());
            RepairOrderRecordEntity repairOrderRecord;
            if (CollectionUtils.isNotEmpty(repairOrderRecordList)) {
                repairOrderRecord = repairOrderRecordList.get(0);
                repairOrderRecord.setRepairStatus(2);
                if (StringUtil.isNotEmpty(repairOrderRecord.getCreateTime())) {
                    repairOrderRecord.setExceptionTime(DateUtil.secToTime(DateUtil.compareDiffBetweenSecond(repairOrderRecord.getCreateTime(),
                            LocalDateTime.now())));
                }
                repairOrderRecord.setRepairOperator(userName);
            } else {
                repairOrderRecord = new RepairOrderRecordEntity();
                repairOrderRecord.setOrderNum(orderRecord.getOrderNum());
                repairOrderRecord.setRepairStatus(2);
                repairOrderRecord.setRepairOperator(userName);
            }
            repairOrderRecordDao.save(repairOrderRecord);
            //更改财务结算状态
            SettlementRecordEntity settlementRecord = settlementRecordDao.findByOrderNum(orderRecord.getOrderNum());
            if (settlementRecord != null && settlementRecord.getSettlementState() == 0) {
                settlementRecord.setSettlementState(1);
                settlementRecordDao.save(settlementRecord);
            }
            //更新订单状态(充放电完成)
            orderRecord.setOrderStatus(2);
            orderRecordDao.save(orderRecord);
            return ResponseResult.ok();
        }
        return ResponseResult.paramError(ResponseResult.PARAM_ERROR);
    }

    @Override
    public ResponseResult<List<OrderInfoDto>> findOrderListBySiteIdS(List<String> siteIdList, String accountData, Integer runMode) {
        List<OrderInfoDto> resultList = Lists.newArrayList();

        //根据多个站点id，查询订单数据
        List<OrderRecordEntity> orderRecordEntityList = orderRecordDao.findAllBySiteIdIn(siteIdList);
        if (CollectionUtils.isNotEmpty(orderRecordEntityList)) {
            //根据账号数据过滤
            if (StringUtil.isNotEmpty(accountData)) {
                orderRecordEntityList = orderRecordEntityList.stream().filter(o -> StringUtil.isNotEmpty(o.getAccountData()) && accountData.equals(o.getAccountData())).collect(Collectors.toList());
            }
            //根据订单类型过滤
            if (StringUtil.isNotEmpty(runMode)) {
                orderRecordEntityList = orderRecordEntityList.stream().filter(o -> StringUtil.isNotEmpty(o.getRunMode()) && runMode.equals(o.getRunMode())).collect(Collectors.toList());
            }
            if (CollectionUtils.isNotEmpty(orderRecordEntityList)) {
                //获取多个站点id，查询站点信息
                List<String> stationIds = orderRecordEntityList.stream().map(OrderRecordEntity::getSiteId).distinct().collect(Collectors.toList());
                Map<String, SiteInfoDto> siteInfoDtoMap = deviceService.findSiteBasicInfoByIds(stationIds).getData();
                resultList = orderRecordEntityList.stream().map(orderRecordEntity -> {
                    OrderInfoDto orderInfoDto = new OrderInfoDto();
                    BeanUtils.copyProperties(orderRecordEntity, orderInfoDto);
                    //获取站点名称
                    if (!siteInfoDtoMap.isEmpty() && siteInfoDtoMap.containsKey(orderRecordEntity.getSiteId())) {
                        SiteInfoDto siteInfoDto = siteInfoDtoMap.get(orderRecordEntity.getSiteId());
                        orderInfoDto.setSiteName(siteInfoDto.getSiteName());
                        orderInfoDto.setOperateUnitId(siteInfoDto.getOperatorId());
                        orderInfoDto.setOperateUnitName(siteInfoDto.getOperatorName());
                    }
                    return orderInfoDto;
                }).collect(Collectors.toList());
            }
        }
        return ResponseResult.ok(resultList);
    }

}
