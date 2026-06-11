package com.sunmax.together.service.operation.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.sunmax.common.dto.PageDto;
import com.sunmax.common.dto.data.DeviceHistoryDto;
import com.sunmax.common.dto.data.NodeDifHistoryDto;
import com.sunmax.common.dto.device.*;
import com.sunmax.common.dto.protocol.AlarmNumDto;
import com.sunmax.common.dto.system.AccountDto;
import com.sunmax.common.dto.system.ChargePlatformInfoDto;
import com.sunmax.common.dto.system.OrganEmpowerListDto;
import com.sunmax.common.model.RealDataModel;
import com.sunmax.common.util.DateUtil;
import com.sunmax.common.util.DoubleUtil;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.util.StringUtil;
import com.sunmax.common.vo.DeviceTypeParamVo;
import com.sunmax.common.vo.FunctionLogoParamVo;
import com.sunmax.common.vo.ReaFieldParamVo;
import com.sunmax.common.vo.SiteFieldParamVo;
import com.sunmax.common.vo.data.DeviceCountQueryVo;
import com.sunmax.common.vo.data.DeviceHistoryQueryVo;
import com.sunmax.together.dao.PileStateDurationDao;
import com.sunmax.together.dao.SiteAccountDao;
import com.sunmax.together.dao.SiteCountRecordDao;
import com.sunmax.together.dao.asset.ElectConfigDao;
import com.sunmax.together.dao.asset.ElectTimeFrameDao;
import com.sunmax.together.dao.asset.SeriesConfigDao;
import com.sunmax.together.dao.order.OrderRecordDao;
import com.sunmax.together.dto.operation.dataReport.*;
import com.sunmax.together.entity.PileStateDurationEntity;
import com.sunmax.together.entity.SiteAccountEntity;
import com.sunmax.together.entity.SiteCountRecordEntity;
import com.sunmax.together.entity.assets.ElectConfigEntity;
import com.sunmax.together.entity.assets.ElectTimeFrameEntity;
import com.sunmax.together.entity.assets.SeriesConfigEntity;
import com.sunmax.together.entity.order.OrderRecordEntity;
import com.sunmax.together.mapper.OrderRecordMapper;
import com.sunmax.together.model.OrderSumDataModel;
import com.sunmax.together.service.feign.DataService;
import com.sunmax.together.service.feign.DeviceService;
import com.sunmax.together.service.feign.ProtocolService;
import com.sunmax.together.service.feign.SystemService;
import com.sunmax.together.service.operation.DataReportService;
import com.sunmax.together.vo.operation.dataReport.DataReportQueryVo;
import com.sunmax.together.vo.operation.dataReport.InverterReportQueryVo;
import com.sunmax.together.vo.operation.dataReport.PlatformDetailsVo;
import com.sunmax.together.vo.operation.dataReport.PvSiteReportQueryVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.compress.utils.Lists;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import jakarta.persistence.criteria.Predicate;
import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.sunmax.common.util.DateUtil.*;
import static com.sunmax.common.util.DoubleUtil.getToBigDecimal;
import static com.sunmax.common.util.DoubleUtil.getToDouble;
import static com.sunmax.common.util.StringUtil.isNotEmpty;

@Slf4j
@Service
public class DataReportServiceImpl implements DataReportService {

    @Autowired
    private SystemService systemService;

    @Autowired
    private DeviceService deviceService;

    @Autowired
    private SiteAccountDao siteAccountDao;

    @Autowired
    private OrderRecordDao orderRecordDao;

    @Autowired
    private SiteCountRecordDao siteCountRecordDao;

    @Autowired
    private PileStateDurationDao pileStateDurationDao;

    @Autowired
    private ProtocolService protocolService;

    @Autowired
    private OrderRecordMapper orderRecordMapper;

    @Autowired
    private DataService dataService;

    @Autowired
    private SeriesConfigDao seriesConfigDao;

    @Autowired
    private ElectConfigDao electConfigDao;

    @Autowired
    private ElectTimeFrameDao electTimeFrameDao;

    @Override
    public ResponseResult<SiteReportDto> findSiteChargeReport(DataReportQueryVo reportQueryVo, String userId) {
        SiteReportDto result = new SiteReportDto();

        //站点id列表
        List<String> siteIds = JSON.parseArray(reportQueryVo.getSiteIds(), String.class);

        if (StringUtil.isNotEmpty(reportQueryVo.getAccountId())) {
            //查询指定商户下的站点id
            List<SiteAccountEntity> siteAccountEntities = siteAccountDao.findAllByAccountIdAndTypeAndPayPlatform(
                    reportQueryVo.getAccountId(), reportQueryVo.getRunMode() == 0 ? 1 : 2, 1);
            if (CollectionUtils.isNotEmpty(siteAccountEntities)) {
                siteIds = siteAccountEntities.stream()
                        .map(SiteAccountEntity::getSiteId)
                        .filter(StringUtil::isNotEmpty)
                        .distinct()
                        .collect(Collectors.toList());
            }
        }

        if (CollectionUtils.isNotEmpty(siteIds)) {
            List<SiteReportDto.DataReportInfo> dataReportInfoList = Lists.newArrayList();
            //根据站点id查询站点数据
            List<SiteInfoDto> siteInfoDtoList = deviceService.findSiteInfoListByIds(siteIds).getData();

            //查询站点下订单列表
            Map<String, List<OrderSumDataModel>> orderSumDataMap = orderRecordMapper.findSiteOrderSumData(siteIds, reportQueryVo.getRunMode(), reportQueryVo.getPlatformLogo()
                            , reportQueryVo.getStartTime(), reportQueryVo.getEndTime()).stream()
                    .filter(o -> StringUtil.isNotEmpty(o.getSiteId()))
                    .collect(Collectors.groupingBy(OrderSumDataModel::getSiteId));

            //查询站点下电桩设备列表
            Map<String, List<DeviceBasicInfoDto>> sitePileDeviceMap = deviceService.findDeviceBasicInfoBySiteIds(siteIds, 1).getData();

            //根据电桩设备id，查询电枪列表
            List<DeviceBasicInfoDto> deviceBasicInfoDtoList = Lists.newArrayList();
            sitePileDeviceMap.forEach((k, v) -> deviceBasicInfoDtoList.addAll(v));
            Map<String, List<DeviceGunInfoDto>> deviceGunInfoMap = deviceService.findDeviceGunInfoByDeviceIds(deviceBasicInfoDtoList.stream().map(DeviceBasicInfoDto::getId).collect(Collectors.toList())).getData();
            //根据站点id，查询商户id列表，并查询商户信息
            List<SiteAccountEntity> siteAccountEntities = siteAccountDao.findAllBySiteIdInAndTypeAndPayPlatform(siteIds, reportQueryVo.getRunMode() == 0 ? 1 : 2, 1);
            Map<String, AccountDto> accountDtoMap = Maps.newHashMap();
            Map<String, String> siteAccountMap = Maps.newHashMap();
            if (CollectionUtils.isNotEmpty(siteAccountEntities)) {
                //根据多个商户id查询商户信息
                accountDtoMap = systemService.findAccountListByAccountIds(siteAccountEntities.stream().map(SiteAccountEntity::getAccountId).collect(Collectors.toSet())).getData();
                siteAccountMap = siteAccountEntities.stream().collect(Collectors.toMap(SiteAccountEntity::getSiteId, SiteAccountEntity::getAccountId, (v1, v2) -> v1));
            }

            Map<String, String> finalSiteAccountMap = siteAccountMap;
            Map<String, AccountDto> finalAccountDtoMap = accountDtoMap;

            //组装站点报表数据
            List<SiteReportDto.DataReportInfo> finalDataReportInfoList = dataReportInfoList;
            siteInfoDtoList.forEach(siteInfoDto -> {
                //根据站点场景类型判断，包含电桩类型的才计算
                if (StringUtil.isNotEmpty(siteInfoDto.getScenarioTypes()) && siteInfoDto.getScenarioTypes().contains("3")) {
                    finalDataReportInfoList.add(buildDataReportInfo(siteInfoDto, orderSumDataMap, sitePileDeviceMap, finalSiteAccountMap, finalAccountDtoMap, deviceGunInfoMap));
                }
            });

            //根据区域查询
            if (StringUtil.isNotEmpty(reportQueryVo.getAreaType()) && StringUtil.isNotEmpty(reportQueryVo.getArea())) {
                if (reportQueryVo.getAreaType() == 1) {//省级
                    dataReportInfoList = dataReportInfoList.stream().filter(d -> StringUtil.isNotEmpty(d.getProvince()) && d.getProvince().contains(reportQueryVo.getArea())).collect(Collectors.toList());
                } else {//市级
                    dataReportInfoList = dataReportInfoList.stream().filter(d -> StringUtil.isNotEmpty(d.getCity()) && d.getCity().contains(reportQueryVo.getArea())).collect(Collectors.toList());
                }
            }

            if (CollectionUtils.isNotEmpty(dataReportInfoList)) {
                //统计总订单数量
                result.setOrderTotalCount(dataReportInfoList.stream().filter(d -> StringUtil.isNotEmpty(d.getOrderCount())).mapToInt(SiteReportDto.DataReportInfo::getOrderCount).sum());
                //统计总电量
                result.setTotalQt(getToDouble(dataReportInfoList.stream().filter(d -> StringUtil.isNotEmpty(d.getTotalQt())).mapToDouble(SiteReportDto.DataReportInfo::getTotalQt).sum()));
                //统计尖时总电量
                result.setJTotalQt(getToDouble(dataReportInfoList.stream().filter(d -> StringUtil.isNotEmpty(d.getJQt())).mapToDouble(SiteReportDto.DataReportInfo::getJQt).sum()));
                //统计峰时总电量
                result.setFTotalQt(getToDouble(dataReportInfoList.stream().filter(d -> StringUtil.isNotEmpty(d.getFQt())).mapToDouble(SiteReportDto.DataReportInfo::getFQt).sum()));
                //统计平时总电量
                result.setPTotalQt(getToDouble(dataReportInfoList.stream().filter(d -> StringUtil.isNotEmpty(d.getPQt())).mapToDouble(SiteReportDto.DataReportInfo::getPQt).sum()));
                //统计谷时总电量
                result.setGTotalQt(getToDouble(dataReportInfoList.stream().filter(d -> StringUtil.isNotEmpty(d.getGQt())).mapToDouble(SiteReportDto.DataReportInfo::getGQt).sum()));
            }

            //根据条数判断是否返回分页
            if (StringUtil.isNotEmpty(reportQueryVo.getSize()) && reportQueryVo.getSize() > 0) {
                result.setDataReportInfoPage(new PageDto<>(dataReportInfoList, reportQueryVo.getPage(), reportQueryVo.getSize()));
            } else {
                result.setDataReportInfoList(dataReportInfoList);
            }

        }
        return ResponseResult.ok(result);
    }

    //查询站点下订单列表
    private List<OrderRecordEntity> queryOrderRecords(List<String> pileCodeList, List<String> siteIds, DataReportQueryVo reportQueryVo) {
        return orderRecordDao.findAll((Specification<OrderRecordEntity>) (root, cq, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(pileCodeList)) {
                predicates.add(cb.in(root.get("pileCode")).value(pileCodeList));
            }
            if (CollectionUtils.isNotEmpty(siteIds)) {
                predicates.add(cb.in(root.get("siteId")).value(siteIds));
            }
            if (StringUtil.isNotEmpty(reportQueryVo.getRunMode())) {
                predicates.add(cb.equal(root.get("runMode"), reportQueryVo.getRunMode()));
            }
            if (StringUtil.isNotEmpty(reportQueryVo.getPlatformLogo())) {
                predicates.add(cb.equal(root.get("platformLogo"), reportQueryVo.getPlatformLogo()));
            }
            if (StringUtil.isNotEmpty(reportQueryVo.getPileCode())) {
                predicates.add(cb.like(root.get("pileCode"), "%" + reportQueryVo.getPileCode() + "%"));
            }
            if (StringUtil.isNotEmpty(reportQueryVo.getStartTime()) && StringUtil.isNotEmpty(reportQueryVo.getEndTime())) {
                predicates.add(cb.between(root.get("endTime"), reportQueryVo.getStartTime(),
                        reportQueryVo.getEndTime()));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        }, Sort.by("createTime").descending());
    }

    //创建获取报表数据
    private SiteReportDto.DataReportInfo buildDataReportInfo(SiteInfoDto siteInfoDto, Map<String, List<OrderSumDataModel>> orderSumDataMap,
                                                             Map<String, List<DeviceBasicInfoDto>> sitePileDeviceMap, Map<String, String> siteAccountMap,
                                                             Map<String, AccountDto> accountDtoMap, Map<String, List<DeviceGunInfoDto>> deviceGunInfoMap) {
        SiteReportDto.DataReportInfo dataReportInfo = new SiteReportDto.DataReportInfo();
        dataReportInfo.setSiteId(siteInfoDto.getId());
        dataReportInfo.setSiteName(siteInfoDto.getSiteName());

        //获取站点位置信息
        parseSiteLocation(siteInfoDto, dataReportInfo);

        //获取站点商户信息
        if (!siteAccountMap.isEmpty() && siteAccountMap.containsKey(siteInfoDto.getId()) && !accountDtoMap.isEmpty() &&
                accountDtoMap.containsKey(siteAccountMap.get(siteInfoDto.getId()))) {
            dataReportInfo.setAccountId(siteAccountMap.get(siteInfoDto.getId()));
            dataReportInfo.setAccountName(accountDtoMap.get(siteAccountMap.get(siteInfoDto.getId())).getMchName());
        }

        //获取站点下订单信息
        List<OrderSumDataModel> sumDataModelList = orderSumDataMap.getOrDefault(siteInfoDto.getId(), new ArrayList<>());
        if (CollectionUtils.isNotEmpty(sumDataModelList)) {

            dataReportInfo.setOrderCount(sumDataModelList.stream().filter(o -> StringUtil.isNotEmpty(o.getTotalCount())).mapToInt(OrderSumDataModel::getTotalCount).sum());

            //订单总时长(因为sql中有根据充电时长分组，所以统计准确时长这里需要用订单条数 * 订单时长(单位秒) / 3600)
            dataReportInfo.setTotalDuration(getToDouble(sumDataModelList.stream().mapToDouble(o -> (double) (o.getTotalCount() * o.getChargeDuration()) / 3600).sum()));
            //订单总电量
            dataReportInfo.setTotalQt(getToDouble(sumDataModelList.stream()
                    .filter(o -> StringUtil.isNotEmpty(o.getTotalQt()))
                    .mapToDouble(OrderSumDataModel::getTotalQt)
                    .sum()));
            //总尖时电量
            dataReportInfo.setJQt(getToDouble(sumDataModelList.stream()
                    .filter(o -> StringUtil.isNotEmpty(o.getJQt()))
                    .mapToDouble(OrderSumDataModel::getJQt)
                    .sum()));
            //总峰时电量
            dataReportInfo.setFQt(getToDouble(sumDataModelList.stream()
                    .filter(o -> StringUtil.isNotEmpty(o.getFQt()))
                    .mapToDouble(OrderSumDataModel::getFQt)
                    .sum()));
            //总平时电量
            dataReportInfo.setPQt(getToDouble(sumDataModelList.stream()
                    .filter(o -> StringUtil.isNotEmpty(o.getPQt()))
                    .mapToDouble(OrderSumDataModel::getPQt)
                    .sum()));
            //总谷时电量
            dataReportInfo.setGQt(getToDouble(sumDataModelList.stream()
                    .filter(o -> StringUtil.isNotEmpty(o.getGQt()))
                    .mapToDouble(OrderSumDataModel::getGQt)
                    .sum()));
        }
        //获取站点下充电桩信息
        List<DeviceBasicInfoDto> deviceBasicInfoDtos = sitePileDeviceMap.getOrDefault(siteInfoDto.getId(), new ArrayList<>());
        if (CollectionUtils.isNotEmpty(deviceBasicInfoDtos)) {
            //获取站点下电桩设备信息，并以类型id分组
            Map<String, List<DeviceBasicInfoDto>> groupByTypeMap = deviceBasicInfoDtos.stream()
                    .filter(o -> StringUtil.isNotEmpty(o.getTypeId()))
                    .collect(Collectors.groupingBy(DeviceBasicInfoDto::getTypeId));

            //直流
            processPileInfo(dataReportInfo, sumDataModelList, groupByTypeMap, DeviceTypeParamVo.AC_PILE_TYPE, "Dc", deviceGunInfoMap);
            //交流
            processPileInfo(dataReportInfo, sumDataModelList, groupByTypeMap, DeviceTypeParamVo.DC_PILE_TYPE, "Ac", deviceGunInfoMap);
            //V2G
            processPileInfo(dataReportInfo, sumDataModelList, groupByTypeMap, DeviceTypeParamVo.V2G_PILE_TYPE, "V2g", deviceGunInfoMap);
        }

        return dataReportInfo;
    }

    //获取站点位置信息
    private void parseSiteLocation(SiteInfoDto siteInfoDto, SiteReportDto.DataReportInfo dataReportInfo) {
        String siteReadwriteObject = siteInfoDto.getSiteReadwriteObject();
        if (StringUtil.isNotEmpty(siteReadwriteObject)) {
            Map<String, Object> locationMap = JSON.parseObject(siteReadwriteObject).getJSONObject(SiteFieldParamVo.LOCATION);
            if (locationMap != null) {
                dataReportInfo.setProvince(String.valueOf(locationMap.getOrDefault(SiteFieldParamVo.PROVINCE, "")));
                dataReportInfo.setCity(String.valueOf(locationMap.getOrDefault(SiteFieldParamVo.CITY, "")));
                dataReportInfo.setCounty(String.valueOf(locationMap.getOrDefault(SiteFieldParamVo.COUNTY, "")));
            }
        }
    }

    //获取站点位置信息
    private void parseSiteLocation2(SiteInfoDto siteInfoDto, SiteRunReportDto siteRunReportDto) {
        String siteReadwriteObject = siteInfoDto.getSiteReadwriteObject();
        if (StringUtil.isNotEmpty(siteReadwriteObject)) {
            Map<String, Object> locationMap = JSON.parseObject(siteReadwriteObject).getJSONObject(SiteFieldParamVo.LOCATION);
            if (locationMap != null) {
                siteRunReportDto.setProvince(String.valueOf(locationMap.getOrDefault(SiteFieldParamVo.PROVINCE, "")));
                siteRunReportDto.setCity(String.valueOf(locationMap.getOrDefault(SiteFieldParamVo.CITY, "")));
                siteRunReportDto.setCounty(String.valueOf(locationMap.getOrDefault(SiteFieldParamVo.COUNTY, "")));
            }
        }
    }

    //
    private void processPileInfo(SiteReportDto.DataReportInfo dataReportInfo, List<OrderSumDataModel> sumDataModelList,
                                 Map<String, List<DeviceBasicInfoDto>> groupByTypeMap, String pileType, String prefix,
                                 Map<String, List<DeviceGunInfoDto>> deviceGunInfoMap) {
        List<DeviceBasicInfoDto> deviceBasicInfoDtoList = groupByTypeMap.get(pileType);
        if (CollectionUtils.isNotEmpty(deviceBasicInfoDtoList)) {
            //过滤出包含该类型电桩的订单
            List<OrderSumDataModel> filterOrderSumDataList = sumDataModelList.stream()
                    .filter(o -> deviceBasicInfoDtoList.stream().anyMatch(p -> StringUtil.isNotEmpty(p.getDeviceNumber())
                            && p.getDeviceNumber().equals(o.getPileCode())))
                    .collect(Collectors.toList());
            //设备额定功率
            double ratedPower = deviceRatedPower(deviceBasicInfoDtoList);
            //电桩数量
            int pileNum = deviceBasicInfoDtoList.size();
            //电枪数量
            int gunNum = deviceBasicInfoDtoList.stream().mapToInt(d -> Optional.ofNullable(deviceGunInfoMap).map(m -> m.get(d.getId())).orElse(new ArrayList<>()).size()).sum();
            //总电量
            double totalQt = getToDouble(filterOrderSumDataList.stream().filter(o -> StringUtil.isNotEmpty(o.getTotalQt())).mapToDouble(OrderSumDataModel::getTotalQt).sum());
            //设置信息
            setPileInfo(dataReportInfo, prefix, ratedPower, pileNum, gunNum, totalQt);
        }
    }

    private void setPileInfo(SiteReportDto.DataReportInfo dataReportInfo, String prefix, double ratedPower, int pileNum, int gunNum, double totalQt) {
        switch (prefix) {
            case "Ac":
                dataReportInfo.setAcRatedPower(ratedPower);
                dataReportInfo.setAcPileNum(pileNum);
                dataReportInfo.setAcGunNum(gunNum);
                dataReportInfo.setAcTotalQt(totalQt);
                break;
            case "Dc":
                dataReportInfo.setDcRatedPower(ratedPower);
                dataReportInfo.setDcPileNum(pileNum);
                dataReportInfo.setDcGunNum(gunNum);
                dataReportInfo.setDcTotalQt(totalQt);
                break;
            case "V2g":
                dataReportInfo.setV2gRatedPower(ratedPower);
                dataReportInfo.setV2gPileNum(pileNum);
                dataReportInfo.setV2gGunNum(gunNum);
                dataReportInfo.setV2gTotalQt(totalQt);
                break;
        }
    }

    //获取设备总额定功率
    public static Double deviceRatedPower(List<DeviceBasicInfoDto> deviceBasicInfoDtos) {
        return deviceBasicInfoDtos.stream()
                .map(deviceBasicInfoDto -> {
                    Map<String, Object> reaMap = deviceBasicInfoDto.getReaMap();
                    if (!reaMap.isEmpty() && reaMap.containsKey(ReaFieldParamVo.RATED_POWER) && StringUtil.isNotEmpty(reaMap.get(ReaFieldParamVo.RATED_POWER))) {
                        return Double.parseDouble(reaMap.get(ReaFieldParamVo.RATED_POWER).toString());
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .mapToDouble(Double::doubleValue)
                .sum();
    }

    //获取订单时长
    public static Double orderDuration(List<OrderRecordEntity> orderRecordEntityList) {
        if (CollectionUtils.isEmpty(orderRecordEntityList)) {
            return 0.0;
        }

        // 过滤掉异常订单，并计算充电总时长
        return orderRecordEntityList.stream()
                .filter(o -> StringUtil.isEmpty(o.getAbnormalCode()))
                .filter(orderRecord -> StringUtil.isNotEmpty(orderRecord.getStartTime()) && StringUtil.isNotEmpty(orderRecord.getEndTime()))
                .mapToDouble(orderRecord -> DateUtil.compareDiffBetweenMinutes(DateUtil.strToLocalDateTime(orderRecord.getStartTime()), DateUtil.strToLocalDateTime(orderRecord.getEndTime())) / 60.0)
                .sum();
    }

    @Override
    public ResponseResult<?> findSiteRunReport(DataReportQueryVo reportQueryVo, String userId) {
        List<SiteRunReportDto> resultList = Lists.newArrayList();
        //站点id列表
        List<String> siteIds = JSON.parseArray(reportQueryVo.getSiteIds(), String.class);
        if (StringUtil.isNotEmpty(reportQueryVo.getAccountId())) {
            //查询指定商户下的站点id
            List<SiteAccountEntity> siteAccountEntities = siteAccountDao.findAllByAccountIdAndTypeAndPayPlatform(reportQueryVo.getAccountId(), reportQueryVo.getRunMode() == 0 ? 1 : 2, 1);
            if (CollectionUtils.isNotEmpty(siteAccountEntities)) {
                siteIds = siteAccountEntities.stream().map(SiteAccountEntity::getSiteId).filter(StringUtil::isNotEmpty).distinct().collect(Collectors.toList());
            }
        }

        if (CollectionUtils.isNotEmpty(siteIds)) {

            ResponseResult<Map<String, List<DeviceBasicInfoDto>>> deviceBasicInfoBySiteIds = deviceService.findDeviceBasicInfoBySiteIds(siteIds, 1);
            List<DeviceBasicInfoDto> deviceBasicInfoDtoList = Lists.newArrayList();
            deviceBasicInfoBySiteIds.getData().values().forEach(deviceBasicInfoDtoList::addAll);

            //根据站点id查询站点数据
            List<SiteInfoDto> siteInfoDtoList = deviceService.findSiteInfoListByIds(siteIds).getData();

            //查询站点下订单列表
            Map<String, List<OrderRecordEntity>> groupBySiteIdMap = queryOrderRecords(null, siteIds, reportQueryVo).stream()
                    .filter(o -> StringUtil.isNotEmpty(o.getSiteId()))
                    .collect(Collectors.groupingBy(OrderRecordEntity::getSiteId));

            //根据站点id，查询商户id列表，并查询商户信息
            List<SiteAccountEntity> siteAccountEntities = siteAccountDao.findAllBySiteIdInAndTypeAndPayPlatform(siteIds, 1, 1);
            Map<String, AccountDto> accountDtoMap = Maps.newHashMap();
            Map<String, String> siteAccountMap = Maps.newHashMap();
            if (CollectionUtils.isNotEmpty(siteAccountEntities)) {
                //根据多个商户id查询商户信息
                accountDtoMap = systemService.findAccountListByAccountIds(siteAccountEntities.stream().map(SiteAccountEntity::getAccountId).collect(Collectors.toSet())).getData();
                siteAccountMap = siteAccountEntities.stream().collect(Collectors.toMap(SiteAccountEntity::getSiteId, SiteAccountEntity::getAccountId, (v1, v2) -> v1));
            }

            //查询站点统计记录数据
            Map<String, List<SiteCountRecordEntity>> siteCountRecordMap = siteCountRecordDao.findAllBySiteIdInAndCountDateBetween(siteIds, strToLocalDate(reportQueryVo.getStartTime().substring(0, 10)),
                    strToLocalDate(reportQueryVo.getEndTime().substring(0, 10))).stream().collect(Collectors.groupingBy(SiteCountRecordEntity::getSiteId));

            //查询站点下电桩状态在线时长数据
            Map<String, List<PileStateDurationEntity>> sitePileDurationMap = pileStateDurationDao.findAllBySiteIdInAndCountDateBetween(siteIds,
                    reportQueryVo.getStartTime().substring(0, 10), reportQueryVo.getEndTime().substring(0, 10)).stream().collect(Collectors.groupingBy(PileStateDurationEntity::getSiteId));

            //查询电桩告警次数
            Map<String, Integer> pileAlarmNumMap = protocolService.countDeviceAlarmNum(deviceBasicInfoDtoList.stream().map(DeviceBasicInfoDto::getDeviceNumber).collect(Collectors.toSet()),
                    reportQueryVo.getStartTime(), reportQueryVo.getEndTime()).getData().stream().collect(Collectors.toMap(AlarmNumDto::getDeviceCode, AlarmNumDto::getAlarmNum, (v1, v2) -> v1));

            //获取查询时间范围间隔了多少天
            long daysBetween = ChronoUnit.DAYS.between(strToLocalDate(reportQueryVo.getStartTime().substring(0, 10)), strToLocalDate(reportQueryVo.getEndTime().substring(0, 10)));
            //组装站点报表数据
            Map<String, AccountDto> finalAccountDtoMap = accountDtoMap;
            Map<String, String> finalSiteAccountMap = siteAccountMap;
            List<SiteRunReportDto> finalResultList = resultList;
            siteInfoDtoList.forEach(siteInfoDto -> {
                //根据站点场景类型判断，包含电桩类型的才计算
                if (StringUtil.isNotEmpty(siteInfoDto.getScenarioTypes()) && siteInfoDto.getScenarioTypes().contains("3")) {
                    finalResultList.add(buildSiteRunReportInfo(siteInfoDto, groupBySiteIdMap, siteCountRecordMap, finalAccountDtoMap, finalSiteAccountMap, sitePileDurationMap, pileAlarmNumMap, daysBetween, deviceBasicInfoBySiteIds.getData()));
                }
            });

            //根据区域查询
            if (StringUtil.isNotEmpty(reportQueryVo.getAreaType()) && StringUtil.isNotEmpty(reportQueryVo.getArea())) {
                if (reportQueryVo.getAreaType() == 1) {//省级
                    resultList = resultList.stream().filter(d -> StringUtil.isNotEmpty(d.getProvince()) && d.getProvince().contains(reportQueryVo.getArea())).collect(Collectors.toList());
                } else {//市级
                    resultList = resultList.stream().filter(d -> StringUtil.isNotEmpty(d.getCity()) && d.getCity().contains(reportQueryVo.getArea())).collect(Collectors.toList());
                }
            }

            //时间利用率排序
            Integer timeUtilizeSort = reportQueryVo.getTimeUtilizeSort();
            if (StringUtil.isNotEmpty(timeUtilizeSort)) {
                if (timeUtilizeSort == 0) {
                    resultList = resultList.stream().filter(s -> StringUtil.isNotEmpty(s.getTimeUtilize())).sorted(Comparator.comparing(SiteRunReportDto::getTimeUtilize)).collect(Collectors.toList());
                } else {
                    resultList = resultList.stream().filter(s -> StringUtil.isNotEmpty(s.getTimeUtilize())).sorted(Comparator.comparing(SiteRunReportDto::getTimeUtilize).reversed()).collect(Collectors.toList());
                }
            }

            //功率利用率排序
            Integer powerUtilizeSort = reportQueryVo.getPowerUtilizeSort();
            if (StringUtil.isNotEmpty(powerUtilizeSort)) {
                if (powerUtilizeSort == 0) {
                    resultList = resultList.stream().filter(s -> StringUtil.isNotEmpty(s.getPowerUtilize())).sorted(Comparator.comparing(SiteRunReportDto::getPowerUtilize)).collect(Collectors.toList());
                } else {
                    resultList = resultList.stream().filter(s -> StringUtil.isNotEmpty(s.getPowerUtilize())).sorted(Comparator.comparing(SiteRunReportDto::getPowerUtilize).reversed()).collect(Collectors.toList());
                }
            }

            //枪均电量排序
            Integer gunAvgQtSort = reportQueryVo.getGunAvgQtSort();
            if (StringUtil.isNotEmpty(gunAvgQtSort)) {
                if (gunAvgQtSort == 0) {
                    resultList = resultList.stream().filter(s -> StringUtil.isNotEmpty(s.getGunAvgQt())).sorted(Comparator.comparing(SiteRunReportDto::getGunAvgQt)).collect(Collectors.toList());
                } else {
                    resultList = resultList.stream().filter(s -> StringUtil.isNotEmpty(s.getGunAvgQt())).sorted(Comparator.comparing(SiteRunReportDto::getGunAvgQt).reversed()).collect(Collectors.toList());
                }
            }
        }

        //根据条数判断是否返回分页
        if (StringUtil.isNotEmpty(reportQueryVo.getSize()) && reportQueryVo.getSize() > 0) {
            return ResponseResult.ok(new PageDto<>(resultList, reportQueryVo.getPage(), reportQueryVo.getSize()));
        } else {
            return ResponseResult.ok(resultList);
        }
    }

    /**
     * 构建站点运行报表数据
     *
     * @param siteInfoDto
     * @param groupBySiteIdMap
     * @param siteCountRecordMap
     * @param accountDtoMap
     * @param siteAccountMap
     * @param sitePileDurationMap
     * @param pileAlarmNumMap
     * @param daysBetween
     * @return
     */
    private SiteRunReportDto buildSiteRunReportInfo(SiteInfoDto siteInfoDto, Map<String, List<OrderRecordEntity>> groupBySiteIdMap,
                                                    Map<String, List<SiteCountRecordEntity>> siteCountRecordMap, Map<String, AccountDto> accountDtoMap,
                                                    Map<String, String> siteAccountMap, Map<String, List<PileStateDurationEntity>> sitePileDurationMap,
                                                    Map<String, Integer> pileAlarmNumMap, long daysBetween, Map<String, List<DeviceBasicInfoDto>> pileDeviceBasicInfoMap) {
        SiteRunReportDto siteRunReportDto = new SiteRunReportDto();
        siteRunReportDto.setSiteId(siteInfoDto.getId());
        siteRunReportDto.setSiteName(siteInfoDto.getSiteName());
        //获取站点位置信息
        parseSiteLocation2(siteInfoDto, siteRunReportDto);

        //获取站点商户信息
        if (!siteAccountMap.isEmpty() && siteAccountMap.containsKey(siteInfoDto.getId()) && !accountDtoMap.isEmpty() &&
                accountDtoMap.containsKey(siteAccountMap.get(siteInfoDto.getId()))) {
            siteRunReportDto.setAccountId(siteAccountMap.get(siteInfoDto.getId()));
            siteRunReportDto.setAccountName(accountDtoMap.get(siteAccountMap.get(siteInfoDto.getId())).getMchName());
        }

        //获取站点下电桩设备列表，并统计告警次数
        if (!pileAlarmNumMap.isEmpty() && pileDeviceBasicInfoMap.containsKey(siteInfoDto.getId())) {
            AtomicInteger alarmNum = new AtomicInteger(0);
            pileDeviceBasicInfoMap.get(siteInfoDto.getId()).forEach(deviceBasicInfoDto ->
                    alarmNum.addAndGet(pileAlarmNumMap.getOrDefault(deviceBasicInfoDto.getDeviceNumber(), 0))
            );
            siteRunReportDto.setAlarmNum(alarmNum.get());
        }
        //获取站点下订单信息
        if (!groupBySiteIdMap.isEmpty() && groupBySiteIdMap.containsKey(siteInfoDto.getId()) && CollectionUtils.isNotEmpty(groupBySiteIdMap.get(siteInfoDto.getId()))) {
            List<OrderRecordEntity> orderRecordEntityList = groupBySiteIdMap.get(siteInfoDto.getId());
            siteRunReportDto.setOrderCount(orderRecordEntityList.size());
            //异常订单数量
            siteRunReportDto.setAbnormalOrderNum((int) orderRecordEntityList.stream().filter(o -> StringUtil.isNotEmpty(o.getAbnormalCode())).count());
            //启动失败订单数量
            siteRunReportDto.setStartFailNum((int) orderRecordEntityList.stream().filter(o -> StringUtil.isNotEmpty(o.getOrderStatus()) && o.getOrderStatus() == 3).count());
            //计算异常订单率((异常订单数量 + 启动失败订单数) / 订单总数 * 100)
            if (siteRunReportDto.getOrderCount() > 0) {
                siteRunReportDto.setAbnormalOrderRate(getToDouble((double) (siteRunReportDto.getAbnormalOrderNum() + siteRunReportDto.getStartFailNum()) / siteRunReportDto.getOrderCount() * 100));
            }

            //获取站点下查询日期范围内统计记录数据
            if (!siteCountRecordMap.isEmpty() && siteCountRecordMap.containsKey(siteInfoDto.getId())) {

                //站点统计记录数据
                List<SiteCountRecordEntity> siteCountRecordEntities = siteCountRecordMap.get(siteInfoDto.getId());

                //无异常订单,并根据站点统计记录过滤出相同日期的订单
                List<OrderRecordEntity> orderRecordEntities = orderRecordEntityList.stream().
                        filter(o -> StringUtil.isEmpty(o.getAbnormalCode()) && o.getOrderStatus() == 2 && siteCountRecordEntities.stream().
                                anyMatch(p -> localDateToStr(p.getCountDate()).equals(o.getEndTime().substring(0, 10)))).collect(Collectors.toList());
                //订单总时长
                Double totalDuration = orderDuration(orderRecordEntities);
                siteRunReportDto.setTotalDuration(totalDuration);

                double gunData = siteCountRecordEntities.stream().filter(s -> StringUtil.isNotEmpty(s.getTotalGunNum())).mapToDouble(s -> s.getTotalGunNum() * 24).sum();
                siteRunReportDto.setGunTotalDuration(gunData);
                //计算时间利用率(累计(充电时长 + 放电时长) / 总枪数 * 24h)
                if (StringUtil.isNotEmpty(totalDuration) && StringUtil.isNotEmpty(gunData) && gunData > 0) {
                    siteRunReportDto.setTimeUtilize(getToDouble(totalDuration / gunData * 100));
                }

                //获取站点下电桩在线时长
                if (!sitePileDurationMap.isEmpty() && sitePileDurationMap.containsKey(siteInfoDto.getId())) {
                    double onlineDuration = sitePileDurationMap.get(siteInfoDto.getId()).stream().filter(p -> StringUtil.isNotEmpty(p.getWorkState()) && p.getWorkState() == 1).mapToDouble(p -> (double) p.getDuration() / 3600).sum();
                    siteRunReportDto.setPileOnlineDuration(onlineDuration);
                    //计算设备在线率(电桩在线总时长 / gunData(总枪数 * 24h) * 100)
                    if (StringUtil.isNotEmpty(onlineDuration) && StringUtil.isNotEmpty(gunData) && gunData > 0) {
                        siteRunReportDto.setOnlineRate(getToDouble(onlineDuration / gunData * 100));
                    }

                    //计算桩平均在线时长(电桩在线总时长 / 查询范围天数)
                    if (StringUtil.isNotEmpty(onlineDuration) && StringUtil.isNotEmpty(daysBetween) && daysBetween > 0) {
                        siteRunReportDto.setPileAvgDuration(getToDouble(onlineDuration / daysBetween));
                    }
                }

                //累计电量
                double totalQt = orderRecordEntities.stream().filter(o -> StringUtil.isNotEmpty(o.getTotalQt())).mapToDouble(OrderRecordEntity::getTotalQt).sum();
                siteRunReportDto.setSiteSumQt(totalQt);
                //累计额定功率之和*24
                double pilePower = siteCountRecordEntities.stream().filter(s -> StringUtil.isNotEmpty(s.getTotalPilePower())).mapToDouble(s -> s.getTotalPilePower() * 24).sum();
                siteRunReportDto.setSumPwr(pilePower);
                //计算功率利用率(累计(充电电量 + 放电电量) / 总电桩额定功率之和 * 24h)
                if (StringUtil.isNotEmpty(totalQt) && StringUtil.isNotEmpty(pilePower) && pilePower > 0) {
                    siteRunReportDto.setPowerUtilize(getToDouble(totalQt / pilePower * 100));
                }
                //枪均电量
                siteRunReportDto.setGunAvgQt(gunAvgQt(orderRecordEntities, siteCountRecordEntities));
            }
        }
        return siteRunReportDto;
    }

    //计算枪均电量
    public static Double gunAvgQt(List<OrderRecordEntity> orderRecordEntities, List<SiteCountRecordEntity> siteCountRecordEntities) {
        //筛选出充电订单
        List<OrderRecordEntity> chargeOrderList = orderRecordEntities.stream().filter(o -> o.getRunMode() == 0).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(chargeOrderList)) {
            //过滤出站点统计记录数据中有数据的日期的订单(因为站点统计记录中有可能缺少某天的数据，缺少的日期的订单不参与计算)
            List<OrderRecordEntity> recordEntities = chargeOrderList.stream().filter(o -> siteCountRecordEntities.stream().anyMatch(p -> localDateToStr(p.getCountDate()).equals(o.getEndTime().substring(0, 10)))).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(recordEntities)) {
                //累计电量
                double totalQt = recordEntities.stream().filter(o -> StringUtil.isNotEmpty(o.getTotalQt())).mapToDouble(OrderRecordEntity::getTotalQt).sum();
                //筛选日期内总枪数
                double gunNum = siteCountRecordEntities.stream().filter(s -> StringUtil.isNotEmpty(s.getTotalGunNum())).mapToInt(SiteCountRecordEntity::getTotalGunNum).sum();
                //计算枪均电量(累计充电量 / (筛选日期内总枪数 / 查询天数))
                if (StringUtil.isNotEmpty(totalQt) && StringUtil.isNotEmpty(gunNum) && (gunNum / siteCountRecordEntities.size()) > 0) {
                    return getToDouble(totalQt / (gunNum / siteCountRecordEntities.size()));
                }
            }

        }
        return null;
    }

    @Override
    public ResponseResult<PileReportDto> findPileChargeReport(DataReportQueryVo reportQueryVo, String userId) {
        PileReportDto result = new PileReportDto();

        //站点id列表
        List<String> siteIds = JSON.parseArray(reportQueryVo.getSiteIds(), String.class);

        if (CollectionUtils.isNotEmpty(siteIds)) {
            List<PileReportDto.DataReportInfo> dataReportInfoList = Lists.newArrayList();

            //查询站点下电桩设备列表
            List<DeviceBasicInfoDto> deviceBasicInfoDtos = deviceService.findDeviceBasicInfoBySiteIds(siteIds, 1).getData().values().stream()
                    .flatMap(List::stream)
                    .collect(Collectors.toList());

            //根据电桩类型查询
            if (StringUtil.isNotEmpty(reportQueryVo.getPileType())) {
                deviceBasicInfoDtos = deviceBasicInfoDtos.stream().filter(d -> StringUtil.isNotEmpty(d.getTypeId()) && d.getTypeId().equals(reportQueryVo.getPileType())).collect(Collectors.toList());
            }

            //根据电桩编码查询
            if (StringUtil.isNotEmpty(reportQueryVo.getPileCode())) {
                deviceBasicInfoDtos = deviceBasicInfoDtos.stream().filter(d -> StringUtil.isNotEmpty(d.getDeviceNumber()) && d.getDeviceNumber().contains(reportQueryVo.getPileCode())).collect(Collectors.toList());
            }

            if (CollectionUtils.isNotEmpty(deviceBasicInfoDtos)) {
                //根据多个电桩设备id，查询充电枪信息
                Map<String, List<DeviceGunInfoDto>> deviceGunInfoMap = deviceService.findDeviceGunInfoByDeviceIds(deviceBasicInfoDtos.stream().map(DeviceBasicInfoDto::getId).collect(Collectors.toList())).getData();

                //查询电桩下订单列表
                Map<String, List<OrderSumDataModel>> orderSumDataMap = orderRecordMapper.findSiteOrderSumData(deviceBasicInfoDtos.stream().map(DeviceBasicInfoDto::getSiteId).
                                        filter(StringUtil::isNotEmpty).distinct().collect(Collectors.toList()), reportQueryVo.getRunMode(), reportQueryVo.getPlatformLogo()
                                , reportQueryVo.getStartTime(), reportQueryVo.getEndTime()).stream()
                        .filter(o -> StringUtil.isNotEmpty(o.getPileCode())).collect(Collectors.groupingBy(OrderSumDataModel::getPileCode));

                deviceBasicInfoDtos.forEach(deviceBasicInfoDto -> {
                    //电桩额定功率
                    Double ratedPower = null;
                    Map<String, Object> reaMap = deviceBasicInfoDto.getReaMap();
                    //额定功率
                    if (!reaMap.isEmpty() && reaMap.containsKey(ReaFieldParamVo.RATED_POWER) && StringUtil.isNotEmpty(reaMap.get(ReaFieldParamVo.RATED_POWER))) {
                        ratedPower = Double.parseDouble(String.valueOf(reaMap.get(ReaFieldParamVo.RATED_POWER)));
                    }
                    //获取当前电桩下的充电枪信息
                    if (!deviceGunInfoMap.isEmpty() && deviceGunInfoMap.containsKey(deviceBasicInfoDto.getId())) {

                        //获取当前电桩订单信息，并以电枪编码分组
                        Map<Integer, List<OrderSumDataModel>> orderRecordMap = orderSumDataMap.getOrDefault(deviceBasicInfoDto.getDeviceNumber(), Lists.newArrayList()).stream().collect(Collectors.groupingBy(OrderSumDataModel::getGunCode));
                        //循环电枪信息获取数据
                        Double finalRatedPower = ratedPower;
                        deviceGunInfoMap.get(deviceBasicInfoDto.getId()).forEach(deviceGunInfoDto -> {
                            PileReportDto.DataReportInfo dataReportInfo = new PileReportDto.DataReportInfo();
                            dataReportInfo.setSiteId(deviceBasicInfoDto.getSiteId());
                            dataReportInfo.setSiteName(deviceBasicInfoDto.getSiteName());
                            dataReportInfo.setRatedPower(finalRatedPower);
                            dataReportInfo.setTypeTd(deviceBasicInfoDto.getTypeId());
                            dataReportInfo.setPileCode(deviceBasicInfoDto.getDeviceNumber());
                            dataReportInfo.setGunCode(Integer.valueOf(deviceGunInfoDto.getGunCode()));
                            //获取当前电枪订单数据
                            if (!orderRecordMap.isEmpty() && orderRecordMap.containsKey(Integer.parseInt(deviceGunInfoDto.getGunCode()))) {
                                List<OrderSumDataModel> sumDataModelList = orderRecordMap.get(Integer.parseInt(deviceGunInfoDto.getGunCode()));
                                //订单数
                                dataReportInfo.setOrderCount(sumDataModelList.stream().mapToInt(OrderSumDataModel::getTotalCount).sum());
                                //订单总时长(因为sql中有根据充电时长分组，所以统计准确时长这里需要用订单条数 * 订单时长(单位秒) / 3600)
                                dataReportInfo.setTotalDuration(getToDouble(sumDataModelList.stream().mapToDouble(o -> (double) (o.getTotalCount() * o.getChargeDuration()) / 3600).sum()));
                                //订单总电量
                                dataReportInfo.setTotalQt(getToDouble(sumDataModelList.stream()
                                        .filter(o -> StringUtil.isNotEmpty(o.getTotalQt()))
                                        .mapToDouble(OrderSumDataModel::getTotalQt)
                                        .sum()));
                                //总尖时电量
                                dataReportInfo.setJQt(getToDouble(sumDataModelList.stream()
                                        .filter(o -> StringUtil.isNotEmpty(o.getJQt()))
                                        .mapToDouble(OrderSumDataModel::getJQt)
                                        .sum()));
                                //总峰时电量
                                dataReportInfo.setFQt(getToDouble(sumDataModelList.stream()
                                        .filter(o -> StringUtil.isNotEmpty(o.getFQt()))
                                        .mapToDouble(OrderSumDataModel::getFQt)
                                        .sum()));
                                //总平时电量
                                dataReportInfo.setPQt(getToDouble(sumDataModelList.stream()
                                        .filter(o -> StringUtil.isNotEmpty(o.getPQt()))
                                        .mapToDouble(OrderSumDataModel::getPQt)
                                        .sum()));
                                //总谷时电量
                                dataReportInfo.setGQt(getToDouble(sumDataModelList.stream()
                                        .filter(o -> StringUtil.isNotEmpty(o.getGQt()))
                                        .mapToDouble(OrderSumDataModel::getGQt)
                                        .sum()));
                            }
                            dataReportInfoList.add(dataReportInfo);
                        });
                    }
                });
            }
            if (CollectionUtils.isNotEmpty(dataReportInfoList)) {
                //统计总订单数量
                result.setOrderTotalCount(dataReportInfoList.stream().filter(d -> StringUtil.isNotEmpty(d.getOrderCount())).mapToInt(PileReportDto.DataReportInfo::getOrderCount).sum());
                //统计总电量
                result.setTotalQt(getToDouble(dataReportInfoList.stream().filter(d -> StringUtil.isNotEmpty(d.getTotalQt())).mapToDouble(PileReportDto.DataReportInfo::getTotalQt).sum()));
                //统计尖时总电量
                result.setJTotalQt(getToDouble(dataReportInfoList.stream().filter(d -> StringUtil.isNotEmpty(d.getJQt())).mapToDouble(PileReportDto.DataReportInfo::getJQt).sum()));
                //统计峰时总电量
                result.setFTotalQt(getToDouble(dataReportInfoList.stream().filter(d -> StringUtil.isNotEmpty(d.getFQt())).mapToDouble(PileReportDto.DataReportInfo::getFQt).sum()));
                //统计平时总电量
                result.setPTotalQt(getToDouble(dataReportInfoList.stream().filter(d -> StringUtil.isNotEmpty(d.getPQt())).mapToDouble(PileReportDto.DataReportInfo::getPQt).sum()));
                //统计谷时总电量
                result.setGTotalQt(getToDouble(dataReportInfoList.stream().filter(d -> StringUtil.isNotEmpty(d.getGQt())).mapToDouble(PileReportDto.DataReportInfo::getGQt).sum()));
            }

            //根据条数判断是否返回分页
            if (StringUtil.isNotEmpty(reportQueryVo.getSize()) && reportQueryVo.getSize() > 0) {
                result.setDataReportInfoPage(new PageDto<>(dataReportInfoList, reportQueryVo.getPage(), reportQueryVo.getSize()));
            } else {
                result.setDataReportInfoList(dataReportInfoList);
            }
        }
        return ResponseResult.ok(result);
    }

    @Override
    public ResponseResult<?> findPileRunReport(DataReportQueryVo reportQueryVo, String userId) {
        List<PileRunReportDto> resultList = Lists.newArrayList();

        //站点id列表
        List<String> siteIds = JSON.parseArray(reportQueryVo.getSiteIds(), String.class);
        if (CollectionUtils.isNotEmpty(siteIds)) {

            //查询站点下电桩设备列表
            List<DeviceBasicInfoDto> deviceBasicInfoDtos = deviceService.findDeviceBasicInfoBySiteIds(siteIds, 1).getData().values().stream()
                    .flatMap(List::stream)
                    .collect(Collectors.toList());

            //根据电桩类型查询
            if (StringUtil.isNotEmpty(reportQueryVo.getPileType())) {
                deviceBasicInfoDtos = deviceBasicInfoDtos.stream().filter(d -> StringUtil.isNotEmpty(d.getTypeId()) && d.getTypeId().equals(reportQueryVo.getPileType())).collect(Collectors.toList());
            }

            //根据电桩编码查询
            if (StringUtil.isNotEmpty(reportQueryVo.getPileCode())) {
                deviceBasicInfoDtos = deviceBasicInfoDtos.stream().filter(d -> StringUtil.isNotEmpty(d.getDeviceNumber()) && d.getDeviceNumber().contains(reportQueryVo.getPileCode())).collect(Collectors.toList());
            }

            if (CollectionUtils.isNotEmpty(deviceBasicInfoDtos)) {
                //根据多个电桩设备id，查询充电枪信息
                Map<String, List<DeviceGunInfoDto>> deviceGunInfoMap = deviceService.findDeviceGunInfoByDeviceIds(deviceBasicInfoDtos.stream().map(DeviceBasicInfoDto::getId).collect(Collectors.toList())).getData();

                //查询电桩下订单列表
                Map<String, List<OrderRecordEntity>> groupByPileCodeMap = queryOrderRecords(deviceBasicInfoDtos.stream().map(DeviceBasicInfoDto::getDeviceNumber).
                        filter(StringUtil::isNotEmpty).collect(Collectors.toList()), null, reportQueryVo).stream()
                        .filter(o -> StringUtil.isNotEmpty(o.getSiteId())).collect(Collectors.groupingBy(OrderRecordEntity::getPileCode));

                //查询电桩状态在线时长数据
                Map<String, List<PileStateDurationEntity>> pileDurationMap = pileStateDurationDao.findAllByPileCodeInAndCountDateBetweenAndWorkState(deviceBasicInfoDtos.stream().map(DeviceBasicInfoDto::getDeviceNumber).collect(Collectors.toList()),
                        reportQueryVo.getStartTime().substring(0, 10), reportQueryVo.getEndTime().substring(0, 10), 1).stream().collect(Collectors.groupingBy(PileStateDurationEntity::getPileCode));

                //获取查询时间范围间隔了多少天
                long daysBetween = ChronoUnit.DAYS.between(strToLocalDate(reportQueryVo.getStartTime().substring(0, 10)), strToLocalDate(reportQueryVo.getEndTime().substring(0, 10)));

                //查询电桩告警次数
                Map<String, Integer> pileAlarmNumMap = protocolService.countDeviceAlarmNum(deviceBasicInfoDtos.stream().map(DeviceBasicInfoDto::getDeviceNumber).collect(Collectors.toSet()),
                        reportQueryVo.getStartTime(), reportQueryVo.getEndTime()).getData().stream().collect(Collectors.toMap(AlarmNumDto::getDeviceCode, AlarmNumDto::getAlarmNum, (v1, v2) -> v1));

                List<PileRunReportDto> finalResultList = resultList;
                deviceBasicInfoDtos.forEach(deviceBasicInfoDto -> {
                    //电桩额定功率
                    Double ratedPower = null;
                    Map<String, Object> reaMap = deviceBasicInfoDto.getReaMap();
                    //额定功率
                    if (!reaMap.isEmpty() && reaMap.containsKey(ReaFieldParamVo.RATED_POWER) && StringUtil.isNotEmpty(reaMap.get(ReaFieldParamVo.RATED_POWER))) {
                        ratedPower = Double.parseDouble(String.valueOf(reaMap.get(ReaFieldParamVo.RATED_POWER)));
                    }
                    //获取当前电桩下的充电枪信息
                    if (!deviceGunInfoMap.isEmpty() && deviceGunInfoMap.containsKey(deviceBasicInfoDto.getId())) {

                        //获取当前电桩订单信息，并以电枪编码分组
                        Map<Integer, List<OrderRecordEntity>> orderRecordMap = groupByPileCodeMap.getOrDefault(deviceBasicInfoDto.getDeviceNumber(), Lists.newArrayList()).stream().collect(Collectors.groupingBy(OrderRecordEntity::getGunCode));
                        //获取当前电桩状态在线信息
                        Map<String, List<PileStateDurationEntity>> pileStateDurationMap = pileDurationMap.getOrDefault(deviceBasicInfoDto.getDeviceNumber(), Lists.newArrayList()).stream().collect(Collectors.groupingBy(PileStateDurationEntity::getPileCode));
                        //循环电枪信息获取数据
                        Double finalRatedPower = ratedPower;
                        deviceGunInfoMap.get(deviceBasicInfoDto.getId()).forEach(deviceGunInfoDto -> {
                            PileRunReportDto dataReportInfo = new PileRunReportDto();
                            dataReportInfo.setSiteId(deviceBasicInfoDto.getSiteId());
                            dataReportInfo.setSiteName(deviceBasicInfoDto.getSiteName());
                            dataReportInfo.setRatedPower(finalRatedPower);
                            dataReportInfo.setTypeTd(deviceBasicInfoDto.getTypeId());
                            dataReportInfo.setPileCode(deviceBasicInfoDto.getDeviceNumber());
                            dataReportInfo.setGunCode(Integer.valueOf(deviceGunInfoDto.getGunCode()));
                            //获取当前电枪订单数据
                            if (!orderRecordMap.isEmpty() && orderRecordMap.containsKey(Integer.parseInt(deviceGunInfoDto.getGunCode()))) {
                                List<OrderRecordEntity> orderRecordEntityList = orderRecordMap.get(Integer.parseInt(deviceGunInfoDto.getGunCode()));
                                //订单数
                                dataReportInfo.setOrderCount(orderRecordEntityList.size());
                                //充电订单总时长
                                List<OrderRecordEntity> orderRecordEntities = orderRecordEntityList.stream().filter(o -> o.getRunMode() == 0).collect(Collectors.toList());
                                dataReportInfo.setChargeDuration(getToDouble(orderDuration(orderRecordEntities)));
                                //放电订单总时长
                                dataReportInfo.setDischargeDuration(getToDouble(orderDuration(orderRecordEntityList.stream().filter(o -> o.getRunMode() == 1 || o.getRunMode() == 2).collect(Collectors.toList()))));
                                //计算时间利用率(累计(充电时长 + 放电时长) / 查询范围天数 * 24h * 100)(计算方式需求提供)
                                if (StringUtil.isNotEmpty(dataReportInfo.getChargeDuration()) && StringUtil.isNotEmpty(dataReportInfo.getDischargeDuration()) && StringUtil.isNotEmpty(daysBetween)) {
                                    dataReportInfo.setTimeUtilize(getToDouble((dataReportInfo.getChargeDuration() + dataReportInfo.getDischargeDuration()) / (daysBetween * 24) * 100));
                                }

                                //计算日均充电量(电枪累计充电量 / 查询范围天数)
                                double chargeQt = orderRecordEntities.stream().filter(o -> StringUtil.isNotEmpty(o.getTotalQt())).mapToDouble(OrderRecordEntity::getTotalQt).sum();
                                if (StringUtil.isNotEmpty(chargeQt) && StringUtil.isNotEmpty(daysBetween)) {
                                    dataReportInfo.setDayAvgQt(getToDouble(chargeQt / daysBetween));
                                }
                                //获取电桩在线时长
                                if (!pileStateDurationMap.isEmpty() && pileStateDurationMap.containsKey(deviceBasicInfoDto.getDeviceNumber())) {
                                    double onlineDuration = pileStateDurationMap.get(deviceBasicInfoDto.getDeviceNumber()).stream().mapToDouble(p -> (double) p.getDuration() / 3600).sum();
                                    dataReportInfo.setOnlineDuration(getToDouble(onlineDuration));
                                    //计算设备在线率(电桩在线总时长 / 查询范围天数 * 24h * 100)
                                    if (StringUtil.isNotEmpty(onlineDuration) && StringUtil.isNotEmpty(daysBetween)) {
                                        dataReportInfo.setOnlineRate(getToDouble(onlineDuration / (daysBetween * 24) * 100));
                                    }
                                }

                                //获取电桩告警次数
                                if (MapUtils.isNotEmpty(pileAlarmNumMap) && pileAlarmNumMap.containsKey(deviceBasicInfoDto.getDeviceNumber())) {
                                    dataReportInfo.setAlarmNum(pileAlarmNumMap.get(deviceBasicInfoDto.getDeviceNumber()));
                                }

                                //异常订单数量
                                dataReportInfo.setAbnormalOrderNum((int) orderRecordEntityList.stream().filter(o -> StringUtil.isNotEmpty(o.getAbnormalCode())).count());
                                //启动失败订单数量
                                dataReportInfo.setStartFailNum((int) orderRecordEntityList.stream().filter(o -> StringUtil.isNotEmpty(o.getOrderStatus()) && o.getOrderStatus() == 3).count());
                                //计算异常订单率((异常订单数量 + 启动失败订单数) / 订单总数 * 100)
                                dataReportInfo.setAbnormalOrderRate(getToDouble(
                                        (dataReportInfo.getAbnormalOrderNum() + dataReportInfo.getStartFailNum()) /
                                                (double) dataReportInfo.getOrderCount() * 100
                                ));
                            }
                            finalResultList.add(dataReportInfo);
                        });
                    }
                });
                //时间利用率排序
                Integer timeUtilizeSort = reportQueryVo.getTimeUtilizeSort();
                if (StringUtil.isNotEmpty(timeUtilizeSort)) {
                    if (timeUtilizeSort == 0) {
                        resultList = resultList.stream().filter(s -> StringUtil.isNotEmpty(s.getTimeUtilize())).sorted(Comparator.comparing(PileRunReportDto::getTimeUtilize)).collect(Collectors.toList());
                    } else {
                        resultList = resultList.stream().filter(s -> StringUtil.isNotEmpty(s.getTimeUtilize())).sorted(Comparator.comparing(PileRunReportDto::getTimeUtilize).reversed()).collect(Collectors.toList());
                    }
                }
                //充电时长排序
                Integer chargeDurationSort = reportQueryVo.getChargeDurationSort();
                if (StringUtil.isNotEmpty(chargeDurationSort)) {
                    if (chargeDurationSort == 0) {
                        resultList = resultList.stream().filter(s -> StringUtil.isNotEmpty(s.getChargeDuration())).sorted(Comparator.comparing(PileRunReportDto::getChargeDuration)).collect(Collectors.toList());
                    } else {
                        resultList = resultList.stream().filter(s -> StringUtil.isNotEmpty(s.getChargeDuration())).sorted(Comparator.comparing(PileRunReportDto::getChargeDuration).reversed()).collect(Collectors.toList());
                    }
                }
                //放电时长排序
                Integer dischargeDurationSort = reportQueryVo.getDischargeDurationSort();
                if (StringUtil.isNotEmpty(dischargeDurationSort)) {
                    if (dischargeDurationSort == 0) {
                        resultList = resultList.stream().filter(s -> StringUtil.isNotEmpty(s.getDischargeDuration())).sorted(Comparator.comparing(PileRunReportDto::getDischargeDuration)).collect(Collectors.toList());
                    } else {
                        resultList = resultList.stream().filter(s -> StringUtil.isNotEmpty(s.getDischargeDuration())).sorted(Comparator.comparing(PileRunReportDto::getDischargeDuration).reversed()).collect(Collectors.toList());
                    }
                }
                //日均充电量排序
                Integer dayAvgQtSort = reportQueryVo.getDayAvgQtSort();
                if (StringUtil.isNotEmpty(dayAvgQtSort)) {
                    if (dayAvgQtSort == 0) {
                        resultList = resultList.stream().filter(s -> StringUtil.isNotEmpty(s.getDayAvgQt())).sorted(Comparator.comparing(PileRunReportDto::getDayAvgQt)).collect(Collectors.toList());
                    } else {
                        resultList = resultList.stream().filter(s -> StringUtil.isNotEmpty(s.getDayAvgQt())).sorted(Comparator.comparing(PileRunReportDto::getDayAvgQt).reversed()).collect(Collectors.toList());
                    }
                }
            }
        }
        //根据条数判断是否返回分页
        if (StringUtil.isNotEmpty(reportQueryVo.getSize()) && reportQueryVo.getSize() > 0) {
            return ResponseResult.ok(new PageDto<>(resultList, reportQueryVo.getPage(), reportQueryVo.getSize()));
        } else {
            return ResponseResult.ok(resultList);
        }
    }

    @Override
    public ResponseResult<List<AccountDto>> findAccountList(String userId, Integer queryMode) {
        List<AccountDto> resultList = Lists.newArrayList();

        //根据用户id查询资产授权
        ResponseResult<List<OrganEmpowerListDto>> allOrganEmpowerByTenantId = systemService.findAllOrganEmpowerByUserId(userId);
        if (allOrganEmpowerByTenantId.isSuccess() && CollectionUtils.isNotEmpty(allOrganEmpowerByTenantId.getData())) {
            List<String> siteIdList = allOrganEmpowerByTenantId.getData().stream()
                    .map(OrganEmpowerListDto::getSiteId)
                    .filter(StringUtil::isNotEmpty)
                    .distinct()
                    .collect(Collectors.toList());

            //根据站点id，查询商户id列表，并查询商户信息
            List<SiteAccountEntity> siteAccountEntities = siteAccountDao.findAllBySiteIdInAndTypeAndPayPlatform(siteIdList, queryMode, 1);
            if (CollectionUtils.isNotEmpty(siteAccountEntities)) {
                //根据多个商户id查询商户信息
                ResponseResult<Map<String, AccountDto>> accountListByAccountIds = systemService.findAccountListByAccountIds(siteAccountEntities.stream().map(SiteAccountEntity::getAccountId).collect(Collectors.toSet()));
                if (accountListByAccountIds.isSuccess() && MapUtils.isNotEmpty(accountListByAccountIds.getData())) {
                    resultList = new ArrayList<>(accountListByAccountIds.getData().values());
                }
            }
        }
        return ResponseResult.ok(resultList);
    }

    @Override
    public ResponseResult<SummaryCountDto> findSummaryCountData(DataReportQueryVo reportQueryVo, String userId) {
        SummaryCountDto result = new SummaryCountDto();

        //获取xXAis轴列表
        List<String> dateBetween = getDateBetween(1, reportQueryVo.getStartTime().substring(0, 10), reportQueryVo.getEndTime().substring(0, 10));
        result.setXAXisList(dateBetween.stream().map(c -> c.substring(5, 10)).collect(Collectors.toList()));

        //站点id列表
        List<String> siteIds = JSON.parseArray(reportQueryVo.getSiteIds(), String.class);

        if (CollectionUtils.isNotEmpty(siteIds)) {

            List<SummaryCountDto.ChargeSummary> dataReportInfoList = Lists.newArrayList();

            //根据站点id，查询站点详情数据
            Map<String, SiteInfoDto> siteInfoDtoMap = deviceService.findSiteInfoListByIds(siteIds).getData().stream().collect(Collectors.toMap(SiteInfoDto::getId, Function.identity()));
            //查询站点下订单数据
            List<OrderRecordEntity> orderRecordEntityList = orderRecordDao.findAllBySiteIdInAndEndTimeBetween(siteIds, reportQueryVo.getStartTime(), reportQueryVo.getEndTime());

            //根据平台标识分组
            Map<String, List<OrderRecordEntity>> groupByPlatformLogoMap = orderRecordEntityList.stream().collect(Collectors.groupingBy(o -> {
                String platformLogo = o.getPlatformLogo();
                return StringUtil.isNotEmpty(platformLogo) ? platformLogo : "其他";
            }));
            //获取多个平台标识，并查询平台基本信息
            List<String> platformLogoList = com.google.common.collect.Lists.newArrayList(groupByPlatformLogoMap.keySet());
            Map<String, ChargePlatformInfoDto> platformInfoDtoMap = systemService.findChargePlatformInfoByLogos(platformLogoList).getData();
            //循环分组订单，获取多平台数据
            List<SummaryCountDto.ChargeSummary> finalDataReportInfoList = dataReportInfoList;
            groupByPlatformLogoMap.forEach((k, v) -> {

                SummaryCountDto.PlatformQtInfo platformQtInfo = new SummaryCountDto.PlatformQtInfo();

                //判断是否有平台标识
                if (platformInfoDtoMap.containsKey(k)) {
                    //平台名称xAXis轴
                    result.getPlatformXaxisList().add(platformInfoDtoMap.get(k).getPlatformName());
                    platformQtInfo.setPlatformName(platformInfoDtoMap.get(k).getPlatformName());
                } else {
                    result.getPlatformXaxisList().add(k);
                    platformQtInfo.setPlatformName(k);
                }
                //根据日期分组
                Map<String, List<OrderRecordEntity>> groupByEndTimeMap = v.stream().filter(o -> StringUtil.isNotEmpty(o.getEndTime())).collect(Collectors.groupingBy(o -> o.getEndTime().substring(0, 10)));
                //循环日期列表获取平台电量数据
                dateBetween.forEach(dayDate -> {
                    //平台电量
                    Double platformQt = 0.0;
                    if (groupByEndTimeMap.containsKey(dayDate)) {
                        platformQt = getToDouble(groupByEndTimeMap.get(dayDate).stream().filter(o -> StringUtil.isNotEmpty(o.getTotalQt())).mapToDouble(OrderRecordEntity::getTotalQt).sum());
                    }
                    platformQtInfo.getPlatformQtList().add(platformQt);
                });

                //平台订单根据站点分组
                v.stream().filter(o -> StringUtil.isNotEmpty(o.getSiteId())).collect(Collectors.groupingBy(OrderRecordEntity::getSiteId))
                        .forEach((siteId, siteOrderList) -> {
                            SummaryCountDto.ChargeSummary chargeSummary = new SummaryCountDto.ChargeSummary();
                            //判断是否有平台标识
                            if (platformInfoDtoMap.containsKey(k)) {
                                chargeSummary.setPlatformName(platformInfoDtoMap.get(k).getPlatformName());
                            } else {
                                chargeSummary.setPlatformName(k);
                            }
                            //获取站点名称
                            chargeSummary.setSiteName(siteInfoDtoMap.get(siteId).getSiteName());
                            //充电订单笔数
                            List<OrderRecordEntity> orderRecordEntities = siteOrderList.stream().filter(o -> o.getRunMode() == 0).collect(Collectors.toList());
                            chargeSummary.setChargeOrderNum(orderRecordEntities.size());
                            //充电量
                            chargeSummary.setChargeQt(getToDouble(orderRecordEntities.stream().filter(o -> StringUtil.isNotEmpty(o.getTotalQt())).mapToDouble(OrderRecordEntity::getTotalQt).sum()));
                            //充电时长
                            chargeSummary.setChargeDuration(getToDouble(orderDuration(orderRecordEntities)));
                            //订单量占比(充电订单数量 / 总订单量 * 100)
                            if (StringUtil.isNotEmpty(chargeSummary.getChargeOrderNum())) {
                                chargeSummary.setOrderNumRatio(getToDouble(chargeSummary.getChargeOrderNum() / siteOrderList.size() * 100.0));
                            }
                            //充电订单总金额
                            chargeSummary.setOrderAmount(getToBigDecimal(orderRecordEntities.stream().map(OrderRecordEntity::getTotalCost).filter(StringUtil::isNotEmpty).reduce(BigDecimal.ZERO, BigDecimal::add)));
                            //充电订单电费
                            chargeSummary.setChargeElecMony(getToBigDecimal(orderRecordEntities.stream().map(OrderRecordEntity::getTotalElect).filter(StringUtil::isNotEmpty).reduce(BigDecimal.ZERO, BigDecimal::add)));
                            //充电订单服务费
                            chargeSummary.setChargeServiceMony(getToBigDecimal(orderRecordEntities.stream().map(OrderRecordEntity::getTotalFee).filter(StringUtil::isNotEmpty).reduce(BigDecimal.ZERO, BigDecimal::add)));
                            finalDataReportInfoList.add(chargeSummary);
                        });
                //平台电量
                result.getPlatformQtInfoList().add(platformQtInfo);

                //平台充电量
                result.getEachPlatformChargeQtList().add(getToDouble(v.stream().filter(o -> StringUtil.isNotEmpty(o.getTotalQt())).mapToDouble(OrderRecordEntity::getTotalQt).sum()));
                //平台订单金额
                result.getEachPlatformChargeMoneyList().add(getToBigDecimal(v.stream().map(OrderRecordEntity::getTotalCost).filter(StringUtil::isNotEmpty).reduce(BigDecimal.ZERO, BigDecimal::add)));
                //平台充电次数
                result.getEachPlatformChargeNumList().add(v.size());
            });
            //根据站点名称排序数据
            dataReportInfoList = dataReportInfoList.stream().sorted(Comparator.comparing(SummaryCountDto.ChargeSummary::getSiteName)).collect(Collectors.toList());
            //根据条数判断是否返回分页
            if (StringUtil.isNotEmpty(reportQueryVo.getSize()) && reportQueryVo.getSize() > 0) {
                result.setDataReportInfoPage(new PageDto<>(dataReportInfoList, reportQueryVo.getPage(), reportQueryVo.getSize()));
            } else {
                result.setDataReportInfoList(dataReportInfoList);
            }
        }
        return ResponseResult.ok(result);
    }

    @Override
    public ResponseResult<?> findPlatformDetailsList(PlatformDetailsVo platformDetailsVo, String userId) {
        List<PlatformDetailsDto> resultList = Lists.newArrayList();

        //根据开始结束时间查询时间轴列表
        List<String> dateBetween = getDateBetween(platformDetailsVo.getTimeType(), platformDetailsVo.getStartTime().substring(0, 10), platformDetailsVo.getEndTime().substring(0, 10));
        //站点id列表
        List<String> siteIds = JSON.parseArray(platformDetailsVo.getSiteIds(), String.class);

        if (CollectionUtils.isNotEmpty(siteIds)) {

            //根据站点id，查询站点详情数据
            Map<String, SiteInfoDto> siteInfoDtoMap = deviceService.findSiteInfoListByIds(siteIds).getData().stream().collect(Collectors.toMap(SiteInfoDto::getId, Function.identity()));

            //查询站点下订单数据
            List<OrderRecordEntity> orderRecordEntityList = orderRecordDao.findAllBySiteIdInAndEndTimeBetweenAndRunMode(siteIds, platformDetailsVo.getStartTime(), platformDetailsVo.getEndTime(), 0);

            //根据站点id分组
            Map<String, List<OrderRecordEntity>> groupBySiteIdMap = orderRecordEntityList.stream().filter(o -> StringUtil.isNotEmpty(o.getSiteId())).collect(Collectors.groupingBy(OrderRecordEntity::getSiteId));
            //获取多个平台标识，并查询平台基本信息
            List<String> platformLogoList = orderRecordEntityList.stream().map(OrderRecordEntity::getPlatformLogo).distinct().collect(Collectors.toList());
            Map<String, ChargePlatformInfoDto> platformInfoDtoMap = systemService.findChargePlatformInfoByLogos(platformLogoList).getData();

            List<PlatformDetailsDto> finalResultList = resultList;
            groupBySiteIdMap.forEach((siteId, orderRecordEntities) -> {
                Map<String, List<OrderRecordEntity>> siteOrderRecordMap = Maps.newHashMap();
                //逐日
                if (platformDetailsVo.getTimeType() == 1) {
                    siteOrderRecordMap = orderRecordEntities.stream().collect(Collectors.groupingBy(o -> o.getEndTime().substring(0, 10)));
                }
                //逐月
                if (platformDetailsVo.getTimeType() == 2) {
                    siteOrderRecordMap = orderRecordEntities.stream().collect(Collectors.groupingBy(o -> o.getEndTime().substring(0, 7)));
                }
                Map<String, List<OrderRecordEntity>> finalSiteOrderRecordMap = siteOrderRecordMap;
                //根据平台标识分组并循环
                orderRecordEntities.stream().
                        collect(Collectors.groupingBy(o -> {
                            String platformLogo = o.getPlatformLogo();
                            return StringUtil.isNotEmpty(platformLogo) ? platformLogo : "其他";
                        })).forEach((platformLogo, v) -> {
                            Map<String, List<OrderRecordEntity>> orderRecordMap = Maps.newHashMap();
                            //逐日
                            if (platformDetailsVo.getTimeType() == 1) {
                                orderRecordMap = v.stream().collect(Collectors.groupingBy(o -> o.getEndTime().substring(0, 10)));
                            }
                            //逐月
                            if (platformDetailsVo.getTimeType() == 2) {
                                orderRecordMap = v.stream().collect(Collectors.groupingBy(o -> o.getEndTime().substring(0, 7)));
                            }
                            //循环日期，获取数据
                            Map<String, List<OrderRecordEntity>> finalOrderRecordMap = orderRecordMap;
                            dateBetween.forEach(date -> {
                                PlatformDetailsDto platformDetailsDto = new PlatformDetailsDto();
                                platformDetailsDto.setSiteId(siteId);
                                platformDetailsDto.setCountDate(date);
                                //获取站点名称
                                platformDetailsDto.setSiteName(siteInfoDtoMap.get(siteId).getSiteName());
                                //判断是否有平台标识
                                if (platformInfoDtoMap.containsKey(platformLogo)) {
                                    platformDetailsDto.setPlatformName(platformInfoDtoMap.get(platformLogo).getPlatformName());
                                } else {
                                    platformDetailsDto.setPlatformName(platformLogo);
                                }
                                //获取当前平台在站点下当前日期的订单数据
                                if (MapUtils.isNotEmpty(finalOrderRecordMap) && finalOrderRecordMap.containsKey(date) && CollectionUtils.isNotEmpty(finalOrderRecordMap.get(date))) {
                                    //平台下当前日期的订单数据
                                    List<OrderRecordEntity> platformDateOrderList = finalOrderRecordMap.get(date);
                                    //充电订单数量
                                    platformDetailsDto.setChargeOrderNum(platformDateOrderList.size());
                                    //充电量
                                    platformDetailsDto.setChargeQt(getToDouble(platformDateOrderList.stream().filter(o -> StringUtil.isNotEmpty(o.getTotalQt())).mapToDouble(OrderRecordEntity::getTotalQt).sum()));
                                    //充电时长
                                    platformDetailsDto.setChargeDuration(getToDouble(orderDuration(platformDateOrderList)));
                                    //订单总金额
                                    platformDetailsDto.setOrderAmount(getToBigDecimal(platformDateOrderList.stream().map(OrderRecordEntity::getTotalCost).filter(StringUtil::isNotEmpty).reduce(BigDecimal.ZERO, BigDecimal::add)));
                                    //充电电费
                                    platformDetailsDto.setChargeElecMony(getToBigDecimal(platformDateOrderList.stream().map(OrderRecordEntity::getTotalElect).filter(StringUtil::isNotEmpty).reduce(BigDecimal.ZERO, BigDecimal::add)));
                                    //充电服务费
                                    platformDetailsDto.setChargeElecMony(getToBigDecimal(platformDateOrderList.stream().map(OrderRecordEntity::getTotalFee).filter(StringUtil::isNotEmpty).reduce(BigDecimal.ZERO, BigDecimal::add)));
                                    //计算订单量占比(当前平台当前日期订单数量 / 当前站点当前日期总订单数量 * 100)
                                    if (finalSiteOrderRecordMap.containsKey(date) && CollectionUtils.isNotEmpty(finalSiteOrderRecordMap.get(date))) {
                                        platformDetailsDto.setOrderNumRatio(getToDouble((double) platformDetailsDto.getChargeOrderNum() / finalSiteOrderRecordMap.get(date).size() * 100));
                                    }
                                }
                                finalResultList.add(platformDetailsDto);
                            });
                        });
            });
            //把resultList排序，把相同站点名称相同日期的数据排列在一起
            resultList = finalResultList.stream().sorted(Comparator.comparing(PlatformDetailsDto::getSiteName).thenComparing(PlatformDetailsDto::getCountDate).reversed()).collect(Collectors.toList());
//            resultList.sort(Comparator.comparing(PlatformDetailsDto::getCountDate).reversed());
/*            resultList = resultList.stream().collect(Collectors.collectingAndThen(Collectors.groupingBy(PlatformDetailsDto::getSiteName),map -> map.entrySet().stream()
                                    .flatMap(e -> e.getValue().stream())
                                    .collect(Collectors.toList())
                    ));*/
        }
        //根据条数判断是否返回分页
        if (StringUtil.isNotEmpty(platformDetailsVo.getSize()) && platformDetailsVo.getSize() > 0) {
            return ResponseResult.ok(new PageDto<>(resultList, platformDetailsVo.getPage(), platformDetailsVo.getSize()));
        } else {
            return ResponseResult.ok(resultList);
        }
    }

    @Override
    public ResponseResult<?> findPvSiteReportList(PvSiteReportQueryVo pvSiteReportVo) {
        List<PvSiteReportListDto> resultList = Lists.newArrayList();

        if (StringUtil.isEmpty(pvSiteReportVo.getSiteIds()) || StringUtil.isEmpty(pvSiteReportVo.getStartTime()) || StringUtil.isEmpty(pvSiteReportVo.getEndTime())) {
            return ResponseResult.paramError(ResponseResult.PARAM_ERROR);
        }

        //站点id列表
        List<String> siteIds = JSON.parseArray(pvSiteReportVo.getSiteIds(), String.class);
        //开始日期(年月日)
        String startDate = pvSiteReportVo.getStartTime().substring(0, 10);
        //结束日期(年月日)
        String endDate = pvSiteReportVo.getEndTime().substring(0, 10);

        //根据站点id，查询站点详情数据
        Map<String, SiteInfoDto> siteInfoMap = deviceService.findSiteBasicInfoByIds(siteIds).getData();

        //根据多个站点id，查询站点下设备数据
        siteIds = siteInfoMap.values().stream().map(SiteInfoDto::getId).collect(Collectors.toList());
        Map<String, List<DeviceBasicInfoDto>> siteDeviceInfoMap = deviceService.findDeviceBasicInfoBySiteIds(siteIds, null).getData();
        //存储光伏逆变器和并网点设备数据
        List<DeviceBasicInfoDto> deviceInfoList = Lists.newArrayList();
        List<String> typeIds = Arrays.asList("20", "38", "39", "65", "66", "77");
        siteDeviceInfoMap.forEach((k, v) -> deviceInfoList.addAll(v.stream().filter(d -> {
            if (StringUtil.isNotEmpty(d.getTypeId()) && typeIds.contains(d.getTypeId()) && StringUtil.isNotEmpty(d.getSiteId())) {
                if (!Objects.equals(d.getTypeId(), "38") && !Objects.equals(d.getTypeId(), "39")) {
                    return true;
                }
                if (siteInfoMap.containsKey(d.getSiteId()) && StringUtil.isNotEmpty(d.getParentId())) {
                    List<SiteScenarioTypeDto> scenarioTypeList = siteInfoMap.get(d.getSiteId()).getSiteScenarioTypeDtos();
                    if (CollectionUtils.isNotEmpty(scenarioTypeList)) {
                        List<String> scenarioTypeIds = scenarioTypeList.stream().filter(s -> isNotEmpty(s.getScenarioType()) && s.getScenarioType() == 1)
                                .map(SiteScenarioTypeDto::getId).collect(Collectors.toList());
                        return scenarioTypeIds.contains(d.getParentId());
                    }
                }
                return false;
            }
            return false;
        }).collect(Collectors.toList())));

        //根据站点id，查询计量节点光伏并网点关联计量设备信息
        Map<String, List<String>> siteNodeDeviceIdMap = deviceService.findSiteMeasureIdBySiteIds(siteIds, 0, 1).getData();
        Set<String> deviceIdSet = siteNodeDeviceIdMap.values().stream().flatMap(List::stream).collect(Collectors.toSet());

        //查询光伏并网点功能点数据
        DeviceHistoryQueryVo deviceHistoryQueryVo = new DeviceHistoryQueryVo();
        deviceHistoryQueryVo.setDeviceIds(deviceIdSet);
        deviceHistoryQueryVo.setFunctionLogos(Collections.singleton(FunctionLogoParamVo.TOTAL_POSITIVE_ACTIVE_ENERGY));
        deviceHistoryQueryVo.setStartTime(pvSiteReportVo.getStartTime());
        deviceHistoryQueryVo.setEndTime(pvSiteReportVo.getEndTime());
        deviceHistoryQueryVo.setTimeInterval("1d");
        Map<String, Map<String, List<NodeDifHistoryDto>>> nodeDifHistoryMap = dataService.findNodeDifHistoryListFeign(deviceHistoryQueryVo).getData();

        //根据时间维度，查询两个时间之中间隔了多少日/月
        long dayNum = 0L;
        switch (pvSiteReportVo.getDateType()) {
            case 2://月
                //获取间隔了多少日
                dayNum = ChronoUnit.DAYS.between(strToLocalDate(startDate), strToLocalDate(endDate));
                break;
            case 3://年
                //获取间隔了多少月
                dayNum = ChronoUnit.MONTHS.between(strToLocalDate(startDate), strToLocalDate(endDate));
                break;
        }

        //获取光伏相关数据
        Map<String, Map<String, Double>> pvSiteQtDataMap = findPvSiteQtData(siteIds, pvSiteReportVo.getStartTime(), pvSiteReportVo.getEndTime(),
                pvSiteReportVo.getDateType(), deviceInfoList);

        //获取站点设置信息
        Map<String, SiteSetUpDto> siteSetUpMap = deviceService.findSiteSetUpBySiteIds(siteIds).getData();


        //循环站点组装数据
        long finalDayNum = dayNum;
        siteInfoMap.values().forEach(site -> {
            PvSiteReportListDto result = new PvSiteReportListDto();
            BeanUtils.copyProperties(site, result);
            //获取站点位置信息
            String siteReadwriteObject = site.getSiteReadwriteObject();
            if (StringUtil.isNotEmpty(siteReadwriteObject)) {
                JSONObject parseObjectMap = JSON.parseObject(siteReadwriteObject);
                //站点位置信息对象
                if (parseObjectMap.containsKey(SiteFieldParamVo.LOCATION)) {
                    JSONObject readwriteMap = parseObjectMap.getJSONObject(SiteFieldParamVo.LOCATION);
                    if (!readwriteMap.isEmpty()) {
                        //省份
                        if (readwriteMap.containsKey(SiteFieldParamVo.PROVINCE)) {
                            result.setProvince(readwriteMap.getString(SiteFieldParamVo.PROVINCE));
                        }
                        //市级
                        if (readwriteMap.containsKey(SiteFieldParamVo.CITY)) {
                            result.setCity(readwriteMap.getString(SiteFieldParamVo.CITY));
                        }
                        //所在区县
                        if (readwriteMap.containsKey(SiteFieldParamVo.COUNTY)) {
                            result.setCounty(readwriteMap.getString(SiteFieldParamVo.COUNTY));
                        }
                        //详细地址
                        if (readwriteMap.containsKey(SiteFieldParamVo.ADDRESS)) {
                            result.setAddress(readwriteMap.getString(SiteFieldParamVo.ADDRESS));
                        }
                    }
                }
            }
            //光伏站装机量
            if (StringUtil.isNotEmpty(site.getScenarioTypes()) && site.getScenarioTypes().contains("1") && CollectionUtils.isNotEmpty(site.getSiteScenarioTypeDtos())) {
                List<SiteScenarioTypeDto> siteScenarioTypeList = site.getSiteScenarioTypeDtos().stream().filter(s -> s.getScenarioType().equals(1))
                        .collect(Collectors.toList());
                result.setPvCapacity(getToDouble(siteScenarioTypeList.stream().mapToDouble(s -> {
                    if (StringUtil.isNotEmpty(s.getReadwriteObject())) {
                        return getToDouble(JSON.parseObject(s.getReadwriteObject()).getDoubleValue(SiteFieldParamVo.PV_CAPACITY));
                    }
                    return 0.0;
                }).sum()));
            }
            //查询站点光伏并网点数据
            if (MapUtils.isNotEmpty(siteNodeDeviceIdMap) && siteNodeDeviceIdMap.containsKey(site.getId()) && CollectionUtils.isNotEmpty(siteNodeDeviceIdMap.get(site.getId()))) {

                List<NodeDifHistoryDto> nodeDifHistoryDtoList = siteNodeDeviceIdMap.get(site.getId()).stream()
                        .map(deviceId -> nodeDifHistoryMap.getOrDefault(deviceId, Maps.newHashMap()))
                        .flatMap(deviceHistoryMap -> deviceHistoryMap.getOrDefault(FunctionLogoParamVo.TOTAL_POSITIVE_ACTIVE_ENERGY, Lists.newArrayList()).stream())
                        .collect(Collectors.toList());
                if (CollectionUtils.isNotEmpty(nodeDifHistoryDtoList)) {
                    double sum = nodeDifHistoryDtoList.stream()
                            .filter(n -> StringUtil.isNotEmpty(n.getLastDataValue()) && StringUtil.isNotEmpty(n.getFirstDataValue()))
                            .mapToDouble(n -> Double.parseDouble(String.valueOf(n.getLastDataValue())) - Double.parseDouble(String.valueOf(n.getFirstDataValue())))
                            .sum();
                    //并网点发电量
                    result.setParallelQt(getToDouble(sum));
                }
            }

            //获取当前站点历史数据
            if (MapUtils.isNotEmpty(pvSiteQtDataMap) && pvSiteQtDataMap.containsKey(site.getId()) && MapUtils.isNotEmpty(pvSiteQtDataMap.get(site.getId()))) {
                Map<String, Double> dataMap = pvSiteQtDataMap.get(site.getId());

                //获取逆变器发电量数据
                if (dataMap.containsKey(FunctionLogoParamVo.TOTAL_POWER_GENERATION) && StringUtil.isNotEmpty(dataMap.get(FunctionLogoParamVo.TOTAL_POWER_GENERATION))) {
                    result.setInverterQt(getToDouble(dataMap.get(FunctionLogoParamVo.TOTAL_POWER_GENERATION)));
                }
                //获取逆变器峰值发电功率
                if (dataMap.containsKey(FunctionLogoParamVo.ACTIVE_POWER) && StringUtil.isNotEmpty(dataMap.get(FunctionLogoParamVo.ACTIVE_POWER))) {
                    result.setFValuePower(getToDouble(dataMap.get(FunctionLogoParamVo.ACTIVE_POWER)));
                }

                //获取气象站平均温度数据
                if (dataMap.containsKey(FunctionLogoParamVo.PV_TEMPERATURE) && StringUtil.isNotEmpty(dataMap.get(FunctionLogoParamVo.PV_TEMPERATURE))) {
                    Double avgTemp = dataMap.get(FunctionLogoParamVo.PV_TEMPERATURE);
                    if (pvSiteReportVo.getDateType() != 1) {
                        avgTemp = avgTemp / finalDayNum;
                    }
                    result.setAvgTemp(getToDouble(avgTemp));
                }

                //获取气象站理论发电量数据
                if (dataMap.containsKey(FunctionLogoParamVo.PV_THEORY_CAPACITY) && StringUtil.isNotEmpty(dataMap.get(FunctionLogoParamVo.PV_THEORY_CAPACITY))) {
                    result.setTheoryQt(getToDouble(dataMap.get(FunctionLogoParamVo.PV_THEORY_CAPACITY)));
                }
                //获取气象站总辐照量数据
                if (dataMap.containsKey(FunctionLogoParamVo.TOTAL_RADIANT_EXPOSURE) && StringUtil.isNotEmpty(dataMap.get(FunctionLogoParamVo.TOTAL_RADIANT_EXPOSURE))) {
                    result.setTotalIrradiation(getToDouble(dataMap.get(FunctionLogoParamVo.TOTAL_RADIANT_EXPOSURE)));
                }

                //获取光伏上网电量
                if (dataMap.containsKey(FunctionLogoParamVo.TOTAL_INVERSE_ACTIVE_ENERGY) && StringUtil.isNotEmpty(dataMap.get(FunctionLogoParamVo.TOTAL_INVERSE_ACTIVE_ENERGY))) {
                    result.setInternetQt(getToDouble(dataMap.get(FunctionLogoParamVo.TOTAL_INVERSE_ACTIVE_ENERGY)));
                }

                //获取光伏上网收益
                if (dataMap.containsKey("internetMoney") && StringUtil.isNotEmpty(dataMap.get("internetMoney"))) {
                    result.setInternetMoney(BigDecimal.valueOf(getToDouble(dataMap.get("internetMoney"))));
                }

                //获取光伏消纳收益
                if (dataMap.containsKey("consumeMoney") && StringUtil.isNotEmpty(dataMap.get("consumeMoney"))) {
                    result.setConsumMoney(BigDecimal.valueOf(getToDouble(dataMap.get("consumeMoney"))));
                }

                //获取光伏收益
                if (StringUtil.isNotEmpty(result.getInternetMoney()) && StringUtil.isNotEmpty(result.getConsumMoney())) {
                    result.setPvMoney(DoubleUtil.getToBigDecimal(result.getInternetMoney().add(result.getConsumMoney())));
                } else if (StringUtil.isNotEmpty(result.getInternetMoney())) {
                    result.setPvMoney(result.getInternetMoney());
                } else if (StringUtil.isNotEmpty(result.getConsumMoney())) {
                    result.setPvMoney(result.getConsumMoney());
                }

            }
            //获取站点设置数据
            if (MapUtils.isNotEmpty(siteSetUpMap) && siteSetUpMap.containsKey(site.getId())) {
                SiteSetUpDto siteSetUpDto = siteSetUpMap.get(site.getId());

                //电站发电量
                Double pvQt;
                if (siteSetUpDto.getPvQtSource() == 20) {
                    pvQt = result.getInverterQt();
                } else {
                    pvQt = result.getParallelQt();
                }

                //获取自用电量(消纳电量) 自用电量（消纳电量）=光伏总发电量(电站发电量)-上网电量
                if (StringUtil.isNotEmpty(pvQt) && StringUtil.isNotEmpty(result.getInternetQt())) {
                    Double occupiedQt = getToDouble(pvQt - result.getInternetQt());
                    if (occupiedQt >= 0) {
                        result.setOccupiedQt(occupiedQt);
                    }
                }

                //自发自用比例 自发自用比例=自用电量/总发电量*100%
                if (StringUtil.isNotEmpty(pvQt) && StringUtil.isNotEmpty(result.getOccupiedQt()) && pvQt > 0.0 && result.getOccupiedQt() > 0.0) {
                    result.setOccupiedRatio(getToDouble(result.getOccupiedQt() / pvQt * 100));
                }

                //计算二氧化碳减排量(电站发电量 * 减排转换系数)
                if (StringUtil.isNotEmpty(siteSetUpDto.getReduceCoeff()) && siteSetUpDto.getReduceCoeff() > 0.0) {
                    result.setDioxideReduce(getToDouble(pvQt * siteSetUpDto.getReduceCoeff()));
                }
                //计算节约标煤量(电站发电量 * 节约标煤系数)
                if (StringUtil.isNotEmpty(siteSetUpDto.getTceCoeff()) && siteSetUpDto.getTceCoeff() > 0.0) {
                    result.setThriftTce(getToDouble(pvQt * siteSetUpDto.getTceCoeff()));
                }
                //计算等效植树(二氧化碳减排量 / 等效植树系数)
                if (StringUtil.isNotEmpty(siteSetUpDto.getTreeCoeff()) && siteSetUpDto.getTreeCoeff() > 0.0) {
                    result.setEquivalentTree(getToDouble(result.getDioxideReduce() / siteSetUpDto.getTreeCoeff() / 40));
                }

                //计算系统效率(电站发电量 / 理论发电量 * 100(这里需要用系统效率计算损失电量，所以不用*100))
                if (StringUtil.isNotEmpty(pvQt) && StringUtil.isNotEmpty(result.getTheoryQt()) && result.getTheoryQt() > 0.0) {
                    result.setSystemEffi(pvQt / result.getTheoryQt());
                }

                //计算损失电量(理论发电量 * 系统效率 - 电站发电量)
                if (StringUtil.isNotEmpty(result.getTheoryQt()) && StringUtil.isNotEmpty(result.getSystemEffi())) {
                    result.setLossQt(result.getTheoryQt() * result.getSystemEffi() - pvQt);
                }

                //计算负荷率(峰值功率 / 装机容量 * 100)
                if (StringUtil.isNotEmpty(result.getFValuePower()) && result.getPvCapacity() > 0.0) {
                    result.setLoadRatio(getToDouble(result.getFValuePower() / result.getPvCapacity() * 100));
                }
            }
            resultList.add(result);
        });
        //根据条数判断是否返回分页
        if (StringUtil.isNotEmpty(pvSiteReportVo.getSize()) && pvSiteReportVo.getSize() > 0) {
            return ResponseResult.ok(new PageDto<>(resultList, pvSiteReportVo.getPage(), pvSiteReportVo.getSize()));
        } else {
            return ResponseResult.ok(resultList);
        }
    }

    @Override
    public ResponseResult<List<DeviceTreeListDto>> findInverterListByUserId(String userId) {
        List<DeviceTreeListDto> resultList = Lists.newArrayList();
        //根据当前登录所属租户id，查询相关配置资产授权站点数据
        ResponseResult<List<OrganEmpowerListDto>> allOrganEmpowerByTenantId = systemService.findAllOrganEmpowerByUserId(userId);
        if (!allOrganEmpowerByTenantId.isSuccess() || CollectionUtils.isEmpty(allOrganEmpowerByTenantId.getData())) {
            return ResponseResult.ok(resultList);
        }

        List<OrganEmpowerListDto> organEmpowerListDtos = allOrganEmpowerByTenantId.getData();

        //根据站点id查询站点本体表结构信息
        ResponseResult<List<SiteInfoDto>> siteInfoListByIds = deviceService.findSiteInfoListByIds(organEmpowerListDtos.stream()
                .map(OrganEmpowerListDto::getSiteId)
                .filter(StringUtil::isNotEmpty)
                .distinct()
                .collect(Collectors.toList()));

        if (!siteInfoListByIds.isSuccess() || CollectionUtils.isEmpty(siteInfoListByIds.getData())) {
            return ResponseResult.ok(resultList);
        }

        //根据能源场景类型过滤出包含光伏类型站点
        List<SiteInfoDto> siteInfoDtoList = siteInfoListByIds.getData().stream()
                .filter(s -> isNotEmpty(s.getScenarioTypes()) && s.getScenarioTypes().contains("1")).collect(Collectors.toList());

        if (CollectionUtils.isEmpty(siteInfoDtoList)) {
            return ResponseResult.ok(resultList);
        }

        //根据多个站点id，查询出站点列表数据

        Map<String, List<DeviceBasicInfoDto>> deviceInfoMap = deviceService.findDeviceBasicInfoBySiteIds(siteInfoDtoList.stream().map(SiteInfoDto::getId).collect(Collectors.toList()), null).getData();

        siteInfoDtoList.forEach(siteInfoDto -> {
            DeviceTreeListDto siteTreeListDto = new DeviceTreeListDto();
            siteTreeListDto.setId(siteInfoDto.getId());
            siteTreeListDto.setCode(siteInfoDto.getSiteCode());
            siteTreeListDto.setName(siteInfoDto.getSiteName());
            resultList.add(siteTreeListDto);

            //获取站点下逆变器和光伏DC/DC设备列表
            if (MapUtils.isNotEmpty(deviceInfoMap) && deviceInfoMap.containsKey(siteInfoDto.getId()) && CollectionUtils.isNotEmpty(deviceInfoMap.get(siteInfoDto.getId()))) {
                List<DeviceBasicInfoDto> deviceBasicInfoDtos = deviceInfoMap.get(siteInfoDto.getId()).stream().filter(d -> isNotEmpty(d.getTypeId())
                        && ("20".equals(d.getTypeId()) || "77".equals(d.getTypeId()))).collect(Collectors.toList());
                if (CollectionUtils.isNotEmpty(deviceBasicInfoDtos)) {
                    deviceBasicInfoDtos.forEach(deviceBasicInfoDto -> {
                        DeviceTreeListDto deviceTreeListDto = new DeviceTreeListDto();
                        deviceTreeListDto.setId(deviceBasicInfoDto.getId());
                        deviceTreeListDto.setCode(deviceBasicInfoDto.getDeviceNumber());
                        deviceTreeListDto.setName(deviceBasicInfoDto.getDeviceName());
                        deviceTreeListDto.setTypeId(deviceBasicInfoDto.getTypeId());
                        deviceTreeListDto.setParentId(siteInfoDto.getId());
                        resultList.add(deviceTreeListDto);
                    });
                }
            }
        });
        return ResponseResult.ok(resultList);
    }

    @Override
    public ResponseResult<?> findPvInverterReportList(InverterReportQueryVo inverterReportQueryVo) {
        //返回的集合
        List<InverterReportInfoDto> resultList = Lists.newArrayList();

        if (StringUtil.isEmpty(inverterReportQueryVo.getDeviceIds())) {
            return ResponseResult.paramError(inverterReportQueryVo.getDeviceIds());
        }

        //多个设备id
        List<String> deviceIdList = JSON.parseArray(inverterReportQueryVo.getDeviceIds(), String.class);

        //根据多个设备id，查询出多个设备数据
        Map<String, DeviceBasicInfoDto> deviceBasicInfoDtoMap = deviceService.findDeviceBasicInfoByIds(deviceIdList).getData();

        List<DeviceBasicInfoDto> deviceBasicInfoDtoList = new ArrayList<>(deviceBasicInfoDtoMap.values());

        //根据多个设备id查询组串配置列表
        Map<String, List<SeriesConfigEntity>> seriesConfigMap = seriesConfigDao.findAllByDeviceIdIn(deviceIdList).stream().collect(Collectors.groupingBy(SeriesConfigEntity::getDeviceId));

        String startTime = inverterReportQueryVo.getStartTime();
        String endTime = inverterReportQueryVo.getEndTime();

        String functionLogo;
        String timeInterval = null;
        if (inverterReportQueryVo.getDateType() == 1) {
            timeInterval = "1d";
            functionLogo = FunctionLogoParamVo.TOTAL_POWER_GENERATION;
        } else if (inverterReportQueryVo.getDateType() == 2) {
            timeInterval = "1n";
            functionLogo = FunctionLogoParamVo.TOTAL_POWER_GENERATION;
        } else if (inverterReportQueryVo.getDateType() == 3) {
            timeInterval = "1y";
            functionLogo = FunctionLogoParamVo.TOTAL_POWER_GENERATION;
        } else {
            functionLogo = null;
        }

        Set<String> deviceIds = deviceBasicInfoDtoList.stream().map(DeviceBasicInfoDto::getId).collect(Collectors.toSet());

        //获取设备周期内有功功率最大值
        DeviceCountQueryVo deviceCountQueryVo = new DeviceCountQueryVo();
        deviceCountQueryVo.setDeviceIds(deviceIds);
        deviceCountQueryVo.setStartTime(startTime);
        deviceCountQueryVo.setEndTime(endTime);
        deviceCountQueryVo.setCuntFun("MAX");
        deviceCountQueryVo.setTimeInterval(timeInterval);
        deviceCountQueryVo.setFunctionLogos(new HashSet<>(Collections.singletonList(FunctionLogoParamVo.ACTIVE_POWER)));
        Map<String, Map<String, List<DeviceHistoryDto>>> deviceMaxPowerMap = dataService.findDeviceCountFunListFeign(deviceCountQueryVo).getData();

        //查询逆变器日月年电量
        DeviceHistoryQueryVo deviceHistoryQueryVo = new DeviceHistoryQueryVo();
        deviceHistoryQueryVo.setDeviceIds(deviceIds);
        deviceHistoryQueryVo.setFunctionLogos(Collections.singleton(functionLogo));
        deviceHistoryQueryVo.setStartTime(startTime);
        deviceHistoryQueryVo.setEndTime(endTime);
        deviceHistoryQueryVo.setTimeInterval(timeInterval);
        Map<String, Map<String, List<NodeDifHistoryDto>>> inverterHistoryDataMap = dataService.findNodeDifHistoryListFeign(deviceHistoryQueryVo).getData();

        //根据多个设备id和功能点标识查询光伏累计发电量
        Map<String, Map<String, RealDataModel>> realDataMap = deviceService.getDeviceFunctionsRealDataByIds(deviceIds,
                FunctionLogoParamVo.TOTAL_POWER_GENERATION, 1).getData();

        //循环设备信息组装数据
        resultList = deviceBasicInfoDtoList.stream().map(deviceBasicInfoDto -> {
            InverterReportInfoDto inverterReportInfoDto = new InverterReportInfoDto();
            BeanUtils.copyProperties(deviceBasicInfoDto, inverterReportInfoDto);
            //获取组串配置信息，并获取组串容量
            if (MapUtils.isNotEmpty(seriesConfigMap) && seriesConfigMap.containsKey(deviceBasicInfoDto.getId())) {
                inverterReportInfoDto.setSeriesCapacity(getToDouble(seriesConfigMap.get(deviceBasicInfoDto.getId()).stream().mapToDouble(SeriesConfigEntity::getSeriesCapacity).sum() / 1000.0));
            }
            //获取发电量
            Optional.<Map<String, Map<String, List<NodeDifHistoryDto>>>>ofNullable(inverterHistoryDataMap)
                    .filter(MapUtils::isNotEmpty).map(map -> map.get(deviceBasicInfoDto.getId()))
                    .filter(m -> MapUtils.isNotEmpty(m)).map(nodeDifHistoryMap -> nodeDifHistoryMap.get(functionLogo))
                    .ifPresent(nodeDifList -> inverterReportInfoDto.setGenerateQt(getToDouble(nodeDifList.stream()
                            .mapToDouble(d -> DoubleUtil.getObjSub(d.getFirstDataValue(), d.getLastDataValue()))
                            .sum())));
            //获取峰值交流功率
            Optional.<Map<String, Map<String, List<DeviceHistoryDto>>>>ofNullable(deviceMaxPowerMap)
                    .filter(MapUtils::isNotEmpty).map(map -> map.get(deviceBasicInfoDto.getId()))
                    .filter(m -> MapUtils.isNotEmpty(m)).map(deviceHistoryMap -> deviceHistoryMap.get(FunctionLogoParamVo.ACTIVE_POWER))
                    .ifPresent(deviceList -> inverterReportInfoDto.setPeakAcPower(getToDouble(deviceList.stream()
                            .mapToDouble(d -> DoubleUtil.objToDouble(d.getDataValue()))
                            .sum())));
            //获取累计发电量
            Optional.<Map<String, Map<String, RealDataModel>>>ofNullable(realDataMap)
                    .filter(MapUtils::isNotEmpty).map(map -> map.get(deviceBasicInfoDto.getId()))
                    .filter(m -> MapUtils.isNotEmpty(m)).map(deviceHistoryMap -> deviceHistoryMap.get(FunctionLogoParamVo.TOTAL_POWER_GENERATION))
                    .ifPresent(realModel -> inverterReportInfoDto.setSumGenerateQt(getToDouble(DoubleUtil.objToDouble(realModel.getDataValue()))));
            //计算等价发电时(逆变器统计周期内的发电量/组串容量，若组串容量为空，则该值为空)
            if (StringUtil.isNotEmpty(inverterReportInfoDto.getSeriesCapacity()) && inverterReportInfoDto.getSeriesCapacity() > 0.0) {
                inverterReportInfoDto.setEquivGeneHour(getToDouble(inverterReportInfoDto.getGenerateQt() / inverterReportInfoDto.getSeriesCapacity()));
            }
            return inverterReportInfoDto;
        }).collect(Collectors.toList());
        //根据站点名称排序，把相同站点的数据放一起
        resultList = resultList.stream().sorted(Comparator.comparing(InverterReportInfoDto::getSiteName)).collect(Collectors.toList());

        //组串容量排序
        Integer seriesCapacitySort = inverterReportQueryVo.getSeriesCapacitySort();
        sortResultListByField(resultList, seriesCapacitySort, InverterReportInfoDto::getSeriesCapacity);

        //发电量排序
        Integer generateQtSort = inverterReportQueryVo.getGenerateQtSort();
        sortResultListByField(resultList, generateQtSort, InverterReportInfoDto::getGenerateQt);

        //累计发电量排序
        Integer sumGenerateQtSort = inverterReportQueryVo.getSumGenerateQtSort();
        sortResultListByField(resultList, sumGenerateQtSort, InverterReportInfoDto::getSumGenerateQt);

        //等价发电小时排序
        Integer equivGeneHourSort = inverterReportQueryVo.getEquivGeneHourSort();
        sortResultListByField(resultList, equivGeneHourSort, InverterReportInfoDto::getEquivGeneHour);

        //峰值交流功率排序
        Integer peakAcPowerSort = inverterReportQueryVo.getPeakAcPowerSort();
        sortResultListByField(resultList, peakAcPowerSort, InverterReportInfoDto::getPeakAcPower);

        //并网时长排序
        Integer gridHourSort = inverterReportQueryVo.getGridHourSort();
        sortResultListByField(resultList, gridHourSort, InverterReportInfoDto::getGridHour);

        //限电损失电量排序
        Integer rationLossQtSort = inverterReportQueryVo.getRationLossQtSort();
        sortResultListByField(resultList, rationLossQtSort, InverterReportInfoDto::getRationLossQt);

        //离散率排序
        Integer discRateSort = inverterReportQueryVo.getDiscRateSort();
        sortResultListByField(resultList, discRateSort, InverterReportInfoDto::getDiscRate);
        //根据条数判断是否返回分页
        if (StringUtil.isNotEmpty(inverterReportQueryVo.getSize()) && inverterReportQueryVo.getSize() > 0) {
            return ResponseResult.ok(new PageDto<>(resultList, inverterReportQueryVo.getPage(), inverterReportQueryVo.getSize()));
        } else {
            return ResponseResult.ok(resultList);
        }
    }

    private void sortResultListByField(List<InverterReportInfoDto> resultList, Integer sortValue, Function<InverterReportInfoDto, Double> fieldExtractor) {
        if (sortValue != null) {
            Comparator<InverterReportInfoDto> comparator = Comparator.comparing(fieldExtractor);
            if (sortValue != 0) {
                comparator = comparator.reversed();
            }
            resultList = resultList.stream()
                    .filter(s -> fieldExtractor.apply(s) != null)
                    .sorted(comparator)
                    .collect(Collectors.toList());
        }
    }

    /**
     * 根据多个站点id查询站点光伏历史发电量数据(根据单个站点返回)
     *
     * @param siteIds             多个站点id
     * @param startTime           开始时间(yyyy-MM-dd HH:mm:ss)
     * @param endTime             结束时间(yyyy-MM-dd HH:mm:ss)
     * @param dateType            时间类型 1-日 2-月 3-年
     * @param deviceBasicInfoDtos 逆变器、并网点、气象站设备列表
     * @return 站点id -> 功能点标识 -> 数据
     */
    public Map<String, Map<String, Double>> findPvSiteQtData(List<String> siteIds, String startTime, String endTime, Integer dateType, List<DeviceBasicInfoDto> deviceBasicInfoDtos) {
        Map<String, Map<String, Double>> resultMap = Maps.newHashMap();
        if (CollectionUtils.isEmpty(siteIds) || StringUtil.isEmpty(startTime) || StringUtil.isEmpty(endTime) || CollectionUtils.isEmpty(deviceBasicInfoDtos)) {
            return resultMap;
        }
        String timeInterval = null;
        if (dateType == 1) {
            timeInterval = "1d";
        } else if (dateType == 2) {
            timeInterval = "1n";
        } else if (dateType == 3) {
            timeInterval = "1y";
        }
        //逆变器设备数据
        List<DeviceBasicInfoDto> inverterDeviceList = deviceBasicInfoDtos.stream().filter(d -> StringUtil.isNotEmpty(d.getTypeId())
                && ("20".equals(d.getTypeId()) || "77".equals(d.getTypeId()))).collect(Collectors.toList());

        //光伏气象站数据
        List<DeviceBasicInfoDto> weatherDeviceList = deviceBasicInfoDtos.stream().filter(d -> StringUtil.isNotEmpty(d.getTypeId()) && "65".equals(d.getTypeId())).collect(Collectors.toList());

        //光伏关口表数据
        List<DeviceBasicInfoDto> gatewayDeviceList = deviceBasicInfoDtos.stream().filter(d -> StringUtil.isNotEmpty(d.getTypeId()) && "39".equals(d.getTypeId())).collect(Collectors.toList());

        //查询逆变器设备所有实时功率功能点历史数据
        Map<String, Map<String, List<NodeDifHistoryDto>>> inverterDataMap = Maps.newHashMap();
        Map<String, Map<String, List<DeviceHistoryDto>>> siteDeviceMaxPowerMap = Maps.newHashMap();
        if (CollectionUtils.isNotEmpty(inverterDeviceList)) {

            //获取设备周期内有功功率最大值
            DeviceCountQueryVo deviceCountQueryVo = new DeviceCountQueryVo();
            deviceCountQueryVo.setDeviceIds(inverterDeviceList.stream().map(DeviceBasicInfoDto::getId).collect(Collectors.toSet()));
            deviceCountQueryVo.setStartTime(startTime);
            deviceCountQueryVo.setEndTime(endTime);
            deviceCountQueryVo.setCuntFun("MAX");
            deviceCountQueryVo.setTimeInterval(timeInterval);
            deviceCountQueryVo.setFunctionLogos(new HashSet<>(Collections.singletonList(FunctionLogoParamVo.ACTIVE_POWER)));
            siteDeviceMaxPowerMap = dataService.findDeviceCountFunListFeign(deviceCountQueryVo).getData();

            //查询逆变器发电量
            DeviceHistoryQueryVo deviceHistoryQueryVo = new DeviceHistoryQueryVo();
            deviceHistoryQueryVo.setDeviceIds(inverterDeviceList.stream().map(DeviceBasicInfoDto::getId).collect(Collectors.toSet()));
            deviceHistoryQueryVo.setFunctionLogos(Collections.singleton(FunctionLogoParamVo.TOTAL_POWER_GENERATION));
            deviceHistoryQueryVo.setStartTime(startTime);
            deviceHistoryQueryVo.setEndTime(endTime);
            deviceHistoryQueryVo.setTimeInterval("30m");
            inverterDataMap = dataService.findNodeDifHistoryListFeign(deviceHistoryQueryVo).getData();

        }

        //查询光伏气象站平均温度功能点数据
        Map<String, Map<String, List<NodeDifHistoryDto>>> weatherDataMap = Maps.newHashMap();
        Map<String, Map<String, List<DeviceHistoryDto>>> siteDeviceAvgTempMap = Maps.newHashMap();
        if (CollectionUtils.isNotEmpty(weatherDeviceList)) {
            DeviceCountQueryVo deviceCountQueryVo = new DeviceCountQueryVo();
            deviceCountQueryVo.setDeviceIds(weatherDeviceList.stream().map(DeviceBasicInfoDto::getId).collect(Collectors.toSet()));
            deviceCountQueryVo.setStartTime(startTime);
            deviceCountQueryVo.setEndTime(endTime);
            deviceCountQueryVo.setCuntFun("AVG");
            if (dateType == 2) {
                deviceCountQueryVo.setTimeInterval("1d");
            } else if (dateType == 3) {
                deviceCountQueryVo.setTimeInterval("1n");
            }
            deviceCountQueryVo.setFunctionLogos(new HashSet<>(Collections.singletonList(FunctionLogoParamVo.PV_TEMPERATURE)));
            siteDeviceAvgTempMap = dataService.findDeviceCountFunListFeign(deviceCountQueryVo).getData();

            //查询气象站理论发电量、总辐照量数据
            DeviceHistoryQueryVo deviceHistoryQueryVo = new DeviceHistoryQueryVo();
            deviceHistoryQueryVo.setDeviceIds(weatherDeviceList.stream().map(DeviceBasicInfoDto::getId).collect(Collectors.toSet()));
            deviceHistoryQueryVo.setFunctionLogos(new HashSet<>(Arrays.asList(FunctionLogoParamVo.PV_THEORY_CAPACITY, FunctionLogoParamVo.TOTAL_RADIANT_EXPOSURE)));
            deviceHistoryQueryVo.setStartTime(startTime);
            deviceHistoryQueryVo.setEndTime(endTime);
            deviceHistoryQueryVo.setTimeInterval("1d");
            weatherDataMap = dataService.findNodeDifHistoryListFeign(deviceHistoryQueryVo).getData();
        }

        //查询光伏关口表每天的正向有功电量和反向有功电量数据和(尖峰平谷)反向有功电量数据
        Map<String, Map<String, List<NodeDifHistoryDto>>> gatewayDataMap = Maps.newHashMap();
        //查询关口表每半个小时的上网电量
        Map<String, Map<String, Double>> gatewayInternetQtMap = Maps.newHashMap();
        if (CollectionUtils.isNotEmpty(gatewayDeviceList)) {
            Set<String> gatewayIds = gatewayDeviceList.stream().map(DeviceBasicInfoDto::getId).collect(Collectors.toSet());
            DeviceHistoryQueryVo gatewayQueryVo = new DeviceHistoryQueryVo();
            gatewayQueryVo.setDeviceIds(gatewayIds);
            gatewayQueryVo.setFunctionLogos(new HashSet<>(Arrays.asList(FunctionLogoParamVo.TOTAL_POSITIVE_ACTIVE_ENERGY,
                    FunctionLogoParamVo.TOTAL_INVERSE_ACTIVE_ENERGY, FunctionLogoParamVo.TOP_REV_KWH, FunctionLogoParamVo.PEAK_REV_KWH,
                    FunctionLogoParamVo.PLAIN_REV_KWH, FunctionLogoParamVo.VALLEY_REV_KWH, FunctionLogoParamVo.DEEP_REV_KWH)));
            gatewayQueryVo.setStartTime(startTime);
            gatewayQueryVo.setEndTime(endTime);
            gatewayQueryVo.setTimeInterval("1d");
            gatewayDataMap = dataService.findNodeDifHistoryListFeign(gatewayQueryVo).getData();

            DeviceHistoryQueryVo gatewayInternetQueryVo = new DeviceHistoryQueryVo();
            gatewayInternetQueryVo.setDeviceIds(gatewayIds);
            gatewayInternetQueryVo.setFunctionLogos(Collections.singleton(FunctionLogoParamVo.TOTAL_INVERSE_ACTIVE_ENERGY));
            gatewayInternetQueryVo.setStartTime(startTime);
            gatewayInternetQueryVo.setEndTime(endTime);
            gatewayInternetQueryVo.setTimeInterval("30m");
            dataService.findNodeDifHistoryListFeign(gatewayInternetQueryVo).getData().forEach((deviceId, d) -> {
                gatewayInternetQtMap.put(deviceId, d.values().stream().flatMap(s -> s.stream()
                        .filter(n -> isNotEmpty(n.getFirstDateTime()))).collect(Collectors.groupingBy(NodeDifHistoryDto::getFirstDateTime,
                        Collectors.summingDouble(s -> DoubleUtil.getObjSub(s.getFirstDataValue(), s.getLastDataValue())))));
            });
        }

        //获取站点光伏上网电价和消纳电价设置信息
        //站点id -> (日期 -> (时段类型 -> 电费))
        Map<String, Map<String, Map<Integer, BigDecimal>>> siteInternetElectMap = Maps.newHashMap();
        //站点id -> (日期 -> (时段 -> 电费))
        Map<String, Map<String, Map<String, BigDecimal>>> siteConsumeElectMap = Maps.newHashMap();
        //开始日期(年月日)
        String startDate = startTime.substring(0, 10);
        //结束日期(年月日)
        String endDate = endTime.substring(0, 10);
        List<String> dateList = DateUtil.getDateBetween(1, startDate, endDate);
        Map<String, List<ElectConfigEntity>> electConfigMap = electConfigDao.findElectConfigListBySiteIds(siteIds, Arrays.asList(2, 3), startDate, endDate)
                .stream().collect(Collectors.groupingBy(ElectConfigEntity::getSiteId));
        if (MapUtils.isNotEmpty(electConfigMap)) {
            //获取电价配置id
            Set<String> electConfigIds = Sets.newHashSet();
            Set<String> internetConfigIds = electConfigMap.values().stream().flatMap(e -> e.stream().filter(c ->
                    c.getModuleType() == 2).map(ElectConfigEntity::getId)).collect(Collectors.toSet());
            Set<String> consumeConfigIds = electConfigMap.values().stream().flatMap(e -> e.stream().filter(c ->
                    c.getModuleType() == 3).map(ElectConfigEntity::getId)).collect(Collectors.toSet());
            electConfigIds.addAll(internetConfigIds);
            electConfigIds.addAll(consumeConfigIds);

            //根据多个电站配置id查询尖峰平谷电价
            List<ElectTimeFrameEntity> electTimeFrameList = electTimeFrameDao.findAllByElectConfigIdIn(electConfigIds);
            //光伏上网电价配置 电价配置id -> (时段类型 -> 电费)
            Map<String, Map<Integer, BigDecimal>> internetTimeFrameMap = electTimeFrameList.stream().filter(e ->
                    internetConfigIds.contains(e.getElectConfigId())).collect(Collectors.groupingBy(ElectTimeFrameEntity::getElectConfigId,
                    Collectors.toMap(ElectTimeFrameEntity::getPeriodType, ElectTimeFrameEntity::getElectMoney, (k1, k2) -> k2)));
            //光伏消纳电价配置 电价配置id -> (半小时时段 -> 电费)
            Map<String, Map<String, BigDecimal>> consumeTimeFrameMap = buildHalfHourMapByConfigId(electTimeFrameList.stream().filter(e -> consumeConfigIds.contains(e.getElectConfigId()))
                    .collect(Collectors.toList()));
            //对数据进行组装
            electConfigMap.forEach((siteId, electConfigList) -> {
                Map<String, Map<Integer, BigDecimal>> internetElectMap = Maps.newHashMap();
                Map<String, Map<String, BigDecimal>> consumeElectMap = Maps.newHashMap();
                electConfigList.stream().collect(Collectors.groupingBy(ElectConfigEntity::getModuleType)).forEach((moduleType, electList) ->
                        electList.forEach(electConfig -> {
                            Map<Integer, BigDecimal> periodTypeMap = Maps.newHashMap();
                            Map<String, BigDecimal> timeFrameMap = Maps.newHashMap();
                            if (moduleType == 2 && internetTimeFrameMap.containsKey(electConfig.getId())) {
                                periodTypeMap = internetTimeFrameMap.get(electConfig.getId());
                            }
                            if (moduleType == 3 && consumeTimeFrameMap.containsKey(electConfig.getId())) {
                                timeFrameMap = consumeTimeFrameMap.get(electConfig.getId());
                            }
                            //过滤日期范围内的日期
                            List<String> electDateList = DateUtil.getDateBetween(1, electConfig.getStartDate(), electConfig.getEndDate());
                            List<String> electFiletrDateList = dateList.stream().filter(electDateList::contains).collect(Collectors.toList());
                            for (String date : electFiletrDateList) {
                                if (moduleType == 2) { //光伏上网电价
                                    internetElectMap.put(date, periodTypeMap);
                                }
                                if (moduleType == 3) { //光伏消纳电价
                                    consumeElectMap.put(date, timeFrameMap);
                                }
                            }
                        }));
                siteInternetElectMap.put(siteId, internetElectMap);
                siteConsumeElectMap.put(siteId, consumeElectMap);
            });
        }

        //根据站点id分组并循环
        Map<String, Map<String, List<NodeDifHistoryDto>>> finalInverterDataMap = inverterDataMap;
        Map<String, Map<String, List<DeviceHistoryDto>>> finalSiteDeviceMaxPowerMap = siteDeviceMaxPowerMap;
        Map<String, Map<String, List<DeviceHistoryDto>>> finalSiteDeviceAvgTempMap = siteDeviceAvgTempMap;
        Map<String, Map<String, List<NodeDifHistoryDto>>> finalWeatherDataMap = weatherDataMap;
        Map<String, Map<String, List<NodeDifHistoryDto>>> finalGatewayDataMap = gatewayDataMap;
        deviceBasicInfoDtos.stream().collect(Collectors.groupingBy(DeviceBasicInfoDto::getSiteId)).forEach((siteId, deviceList) -> {
            Map<String, Double> dataMap = Maps.newHashMap();
            //逆变器和光伏DC/DC发电量
            processDeviceDifHistory(dataMap, deviceList, Arrays.asList("20", "77"), finalInverterDataMap, FunctionLogoParamVo.TOTAL_POWER_GENERATION);

            //逆变器峰值最大功率
            processDeviceRealHistory(dataMap, deviceList, Arrays.asList("20", "77"), finalSiteDeviceMaxPowerMap, FunctionLogoParamVo.ACTIVE_POWER);

            //气象站理论发电量
            processDeviceLastHistory(dataMap, deviceList, "65", finalWeatherDataMap, FunctionLogoParamVo.PV_THEORY_CAPACITY);
            //气象站总辐照量
            processDeviceLastHistory(dataMap, deviceList, "65", finalWeatherDataMap, FunctionLogoParamVo.TOTAL_RADIANT_EXPOSURE);
            //气象站平均温度
            processDeviceRealHistory(dataMap, deviceList, Collections.singletonList("65"), finalSiteDeviceAvgTempMap, FunctionLogoParamVo.PV_TEMPERATURE);

            //关口表正向有功电量
            processDeviceDifHistory(dataMap, deviceList, Collections.singletonList("39"), finalGatewayDataMap, FunctionLogoParamVo.TOTAL_POSITIVE_ACTIVE_ENERGY);
            //关口表反向有功电量
            processDeviceDifHistory(dataMap, deviceList, Collections.singletonList("39"), finalGatewayDataMap, FunctionLogoParamVo.TOTAL_INVERSE_ACTIVE_ENERGY);

            //光伏上网收益
            processPvInternetIncome(dataMap, siteId, deviceList, dateList, siteInternetElectMap, finalGatewayDataMap);
            //光伏消纳收益
            processPvConsumeIncome(dataMap, siteId, deviceList, siteConsumeElectMap, gatewayInternetQtMap, finalInverterDataMap);
            resultMap.put(siteId, dataMap);
        });
        return resultMap;
    }

    /**
     * 处理设备历史数据
     *
     * @param dataMap
     * @param deviceBasicInfoDtoList
     * @param typeIds
     * @param historyMap
     * @param functionLogo
     */
    private <T extends DeviceHistoryDto> void processDeviceRealHistory(Map<String, Double> dataMap, List<DeviceBasicInfoDto> deviceBasicInfoDtoList,
                                                                       List<String> typeIds, Map<String, Map<String, List<T>>> historyMap,
                                                                       String functionLogo) {

        List<T> deviceHistoryDtoList = deviceBasicInfoDtoList.stream()
                .filter(d -> StringUtil.isNotEmpty(d.getTypeId()) && typeIds.contains(d.getTypeId()))
                .map(deviceBasicInfoDto -> historyMap.getOrDefault(deviceBasicInfoDto.getId(), Maps.newHashMap()))
                .flatMap(deviceHistoryMap -> deviceHistoryMap.getOrDefault(functionLogo, Lists.newArrayList()).stream())
                .collect(Collectors.toList());

        if (CollectionUtils.isNotEmpty(deviceHistoryDtoList)) {
            double sum = deviceHistoryDtoList.stream()
                    .mapToDouble(d -> StringUtil.isNotEmpty(d.getDataValue()) ? Double.parseDouble(String.valueOf(d.getDataValue())) : 0.0)
                    .sum();
            dataMap.put(functionLogo, getToDouble(sum));
        }
    }

    /**
     * 处理设备first和last历史数据
     *
     * @param dataMap
     * @param deviceBasicInfoDtoList
     * @param typeId
     * @param historyMap
     * @param functionLogo
     */
    private <T extends NodeDifHistoryDto> void processDeviceLastHistory(Map<String, Double> dataMap, List<DeviceBasicInfoDto> deviceBasicInfoDtoList,
                                                                        String typeId, Map<String, Map<String, List<T>>> historyMap, String functionLogo) {

        List<T> deviceHistoryDtoList = deviceBasicInfoDtoList.stream()
                .filter(d -> StringUtil.isNotEmpty(d.getTypeId()) && typeId.equals(d.getTypeId()))
                .map(deviceBasicInfoDto -> historyMap.getOrDefault(deviceBasicInfoDto.getId(), Maps.newHashMap()))
                .flatMap(deviceHistoryMap -> deviceHistoryMap.getOrDefault(functionLogo, Lists.newArrayList()).stream())
                .collect(Collectors.toList());

        if (CollectionUtils.isNotEmpty(deviceHistoryDtoList)) {
            double sum = deviceHistoryDtoList.stream()
                    .mapToDouble(d -> StringUtil.isNotEmpty(d.getLastDataValue()) ? Double.parseDouble(String.valueOf(d.getLastDataValue())) : 0.0)
                    .sum();
            dataMap.put(functionLogo, getToDouble(sum));
        }
    }

    /**
     * 处理设备first和last历史数据
     *
     * @param dataMap
     * @param deviceBasicInfoDtoList
     * @param typeIds
     * @param historyMap
     * @param functionLogo
     */
    private <T extends NodeDifHistoryDto> void processDeviceDifHistory(Map<String, Double> dataMap, List<DeviceBasicInfoDto> deviceBasicInfoDtoList,
                                                                       List<String> typeIds, Map<String, Map<String, List<T>>> historyMap, String functionLogo) {

        List<T> deviceHistoryDtoList = deviceBasicInfoDtoList.stream()
                .filter(d -> StringUtil.isNotEmpty(d.getTypeId()) && typeIds.contains(d.getTypeId()))
                .map(deviceBasicInfoDto -> historyMap.getOrDefault(deviceBasicInfoDto.getId(), Maps.newHashMap()))
                .flatMap(deviceHistoryMap -> deviceHistoryMap.getOrDefault(functionLogo, Lists.newArrayList()).stream())
                .collect(Collectors.toList());

        if (CollectionUtils.isNotEmpty(deviceHistoryDtoList)) {
            double sum = deviceHistoryDtoList.stream()
                    .mapToDouble(d -> DoubleUtil.getObjSub(d.getFirstDataValue(), d.getLastDataValue()))
                    .sum();
            dataMap.put(functionLogo, getToDouble(sum));
        }
    }

    /**
     * 处理光伏上网收益
     */
    private void processPvInternetIncome(Map<String, Double> dataMap, String siteId, List<DeviceBasicInfoDto> deviceList, List<String> dateList,
                                         Map<String, Map<String, Map<Integer, BigDecimal>>> siteElectMap, Map<String, Map<String, List<NodeDifHistoryDto>>> gatewayDataMap) {
        //获取光伏上网收益
        //定义光伏上网收益
        BigDecimal internetMoney = new BigDecimal("0.0");
        List<String> gatewayIds = deviceList.stream().filter(d -> isNotEmpty(d.getTypeId()) && "39".equals(d.getTypeId()))
                .map(DeviceBasicInfoDto::getId).distinct().collect(Collectors.toList());
        if (siteElectMap.containsKey(siteId) && CollectionUtils.isNotEmpty(gatewayIds)) {
            Map<String, Map<Integer, BigDecimal>> electMap = siteElectMap.get(siteId);

            for (String gatewayId : gatewayIds) {
                if (gatewayDataMap.containsKey(gatewayId)) {
                    Map<String, List<NodeDifHistoryDto>> difHistoryMap = gatewayDataMap.get(gatewayId);
                    Map<String, Double> topRevKwhMap = Maps.newHashMap();
                    Map<String, Double> peakRevKwhMap = Maps.newHashMap();
                    Map<String, Double> plainRevKwhMap = Maps.newHashMap();
                    Map<String, Double> valleyRevKwhMap = Maps.newHashMap();
                    Map<String, Double> deepRevKwhMap = Maps.newHashMap();
                    if (difHistoryMap.containsKey(FunctionLogoParamVo.TOP_REV_KWH)) {
                        topRevKwhMap = difHistoryMap.get(FunctionLogoParamVo.TOP_REV_KWH).stream().collect(Collectors.toMap(n -> n.getFirstDateTime().substring(0, 10),
                                n -> DoubleUtil.getObjSub(n.getFirstDataValue(), n.getLastDataValue())));

                    }
                    if (difHistoryMap.containsKey(FunctionLogoParamVo.PEAK_REV_KWH)) {
                        peakRevKwhMap = difHistoryMap.get(FunctionLogoParamVo.PEAK_REV_KWH).stream().collect(Collectors.toMap(n -> n.getFirstDateTime().substring(0, 10),
                                n -> DoubleUtil.getObjSub(n.getFirstDataValue(), n.getLastDataValue())));

                    }
                    if (difHistoryMap.containsKey(FunctionLogoParamVo.PLAIN_REV_KWH)) {
                        plainRevKwhMap = difHistoryMap.get(FunctionLogoParamVo.PLAIN_REV_KWH).stream().collect(Collectors.toMap(n -> n.getFirstDateTime().substring(0, 10),
                                n -> DoubleUtil.getObjSub(n.getFirstDataValue(), n.getLastDataValue())));

                    }
                    if (difHistoryMap.containsKey(FunctionLogoParamVo.VALLEY_REV_KWH)) {
                        valleyRevKwhMap = difHistoryMap.get(FunctionLogoParamVo.VALLEY_REV_KWH).stream().collect(Collectors.toMap(n -> n.getFirstDateTime().substring(0, 10),
                                n -> DoubleUtil.getObjSub(n.getFirstDataValue(), n.getLastDataValue())));

                    }
                    if (difHistoryMap.containsKey(FunctionLogoParamVo.DEEP_REV_KWH)) {
                        deepRevKwhMap = difHistoryMap.get(FunctionLogoParamVo.DEEP_REV_KWH).stream().collect(Collectors.toMap(n -> n.getFirstDateTime().substring(0, 10),
                                n -> DoubleUtil.getObjSub(n.getFirstDataValue(), n.getLastDataValue())));

                    }
                    for (String date : dateList) {
                        if (electMap.containsKey(date)) {
                            Map<Integer, BigDecimal> dateElectMap = electMap.get(date);
                            //尖时段收益
                            if (dateElectMap.containsKey(1) && topRevKwhMap.containsKey(date)) {
                                internetMoney = internetMoney.add(dateElectMap.get(1).multiply(BigDecimal.valueOf(topRevKwhMap.get(date))));
                            }
                            //峰时段收益
                            if (dateElectMap.containsKey(2) && peakRevKwhMap.containsKey(date)) {
                                internetMoney = internetMoney.add(dateElectMap.get(2).multiply(BigDecimal.valueOf(peakRevKwhMap.get(date))));
                            }
                            //平时段收益
                            if (dateElectMap.containsKey(3) && plainRevKwhMap.containsKey(date)) {
                                internetMoney = internetMoney.add(dateElectMap.get(3).multiply(BigDecimal.valueOf(plainRevKwhMap.get(date))));
                            }
                            //谷时段收益
                            if (dateElectMap.containsKey(4) && valleyRevKwhMap.containsKey(date)) {
                                internetMoney = internetMoney.add(dateElectMap.get(4).multiply(BigDecimal.valueOf(valleyRevKwhMap.get(date))));
                            }
                            //深谷时段收益
                            if (dateElectMap.containsKey(5) && deepRevKwhMap.containsKey(date)) {
                                internetMoney = internetMoney.add(dateElectMap.get(5).multiply(BigDecimal.valueOf(deepRevKwhMap.get(date))));
                            }
                            //全时段收益(平时)
                            if (dateElectMap.containsKey(6) && plainRevKwhMap.containsKey(date)) {
                                internetMoney = internetMoney.add(dateElectMap.get(6).multiply(BigDecimal.valueOf(plainRevKwhMap.get(date))));
                            }
                        }
                    }
                }
            }
        }
        dataMap.put("internetMoney", DoubleUtil.getToDouble(internetMoney.doubleValue()));
    }

    /**
     * 处理光伏消纳收益
     */
    private void processPvConsumeIncome(Map<String, Double> resultMap, String siteId, List<DeviceBasicInfoDto> deviceList,
                                        Map<String, Map<String, Map<String, BigDecimal>>> siteElectMap, Map<String, Map<String, Double>> siteInternetQtMap,
                                        Map<String, Map<String, List<NodeDifHistoryDto>>> inverterDataMap) {
        //获取光伏消纳收益
        //定义消纳收益
        BigDecimal consumeMoney = BigDecimal.ZERO;
        List<String> inverterIds = deviceList.stream().filter(d -> isNotEmpty(d.getTypeId()) && ("20".equals(d.getTypeId())
                        || "77".equals(d.getTypeId()))).map(DeviceBasicInfoDto::getId).distinct().collect(Collectors.toList());
        if (siteElectMap.containsKey(siteId) && CollectionUtils.isNotEmpty(inverterIds) && MapUtils.isNotEmpty(resultMap)) {
            //日期(年月日) -> (分时段时间(00:00) -> 电价)
            Map<String, Map<String, BigDecimal>> electMap = siteElectMap.get(siteId);
            //上网电量(年月日时分秒) -> 上网电量
            Map<String, Double> internetQtMap = siteInternetQtMap.get(siteId);
            //逆变器发电量(年月日时分秒) -> 发电量
            Map<String, Double> dataQtMap = inverterIds.stream().filter(inverterDataMap::containsKey).map(inverterDataMap::get)
                    .flatMap(map -> map.entrySet().stream().flatMap(entry -> entry.getValue().stream()))
                    .collect(Collectors.groupingBy(NodeDifHistoryDto::getFirstDateTime, Collectors.summingDouble(d ->
                            DoubleUtil.getObjSub(d.getFirstDataValue(), d.getLastDataValue()))));
            for (Map.Entry<String, Double> entry : dataQtMap.entrySet()) {
                String key = entry.getKey();
                Double value = entry.getValue();
                Double internetQt = 0.0;
                if (MapUtils.isNotEmpty(internetQtMap) && internetQtMap.containsKey(key)) {
                    internetQt = internetQtMap.get(key);
                }
                //获取消纳电量电价
                String date = key.substring(0, 10);
                String time = key.substring(11, 16);
                BigDecimal consumeElect = new BigDecimal("0.0");
                if (electMap.containsKey(date) && electMap.get(date).containsKey(time)) {
                    consumeElect = electMap.get(date).get(time);
                }
                //光伏消纳收益 (消纳电量 = 逆变器发电量 - 上网电量) * 消纳电量电价
                if (value != null && internetQt != null) {
                    consumeMoney = consumeMoney.add(new BigDecimal(value - internetQt).multiply(consumeElect));
                }
            }
        }
        resultMap.put("consumeMoney", DoubleUtil.getToDouble(consumeMoney.doubleValue()));
    }


    private static Map<String, Map<String, BigDecimal>> buildHalfHourMapByConfigId(List<ElectTimeFrameEntity> list) {
        // Step 1: 按 electConfigId 分组
        Map<String, List<ElectTimeFrameEntity>> grouped = list.stream().collect(Collectors.groupingBy(ElectTimeFrameEntity::getElectConfigId));
        // Step 2: 对每个 configId 构建其半小时电价 map
        return grouped.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> buildHalfHourMapForOneConfig(entry.getValue()),
                        (v1, v2) -> v1, // 合并函数（不会冲突）
                        LinkedHashMap::new // 保持 configId 插入顺序（可选）
                ));
    }

    // 为单个 config 构建 48 个半小时的 Map<String, BigDecimal>
    private static Map<String, BigDecimal> buildHalfHourMapForOneConfig(List<ElectTimeFrameEntity> entities) {
        // 创建分钟级电价数组（0 ~ 1439）
        BigDecimal[] priceByMinute = new BigDecimal[1440];

        // 填充每个时段
        for (ElectTimeFrameEntity e : entities) {
            int start = toMinutes(e.getStartTime());
            int end = "23:59".equals(e.getEndTime()) ? 1439 : toMinutes(e.getEndTime());
            Arrays.fill(priceByMinute, start, end + 1, e.getElectMoney());
        }

        // 构建 48 个半小时点的 map
        return IntStream.range(0, 48).boxed().collect(
                LinkedHashMap::new,
                (map, slot) -> {
                    int totalMin = slot * 30;
                    String timeKey = String.format("%02d:%02d", totalMin / 60, totalMin % 60);
                    map.put(timeKey, priceByMinute[totalMin]);
                },
                LinkedHashMap::putAll
        );
    }

    private static int toMinutes(String time) {
        String[] parts = time.split(":");
        return Integer.parseInt(parts[0]) * 60 + Integer.parseInt(parts[1]);
    }


/*    public Map<String, Map<String, Double>> findPvSiteQtData(List<String> siteIdList, String startTime, String endTime, Integer dateType, List<DeviceBasicInfoDto> deviceBasicInfoDtos) {
        Map<String, Map<String, Double>> resultMap = Maps.newHashMap();
        if (CollectionUtils.isEmpty(siteIdList) || StringUtil.isEmpty(startTime) || StringUtil.isEmpty(endTime) || CollectionUtils.isEmpty(deviceBasicInfoDtos)) {
            return resultMap;
        }
        String functionLogo;
        String timeInterval = null;
        if (dateType == 1) {
            timeInterval = "1d";
            functionLogo = FunctionLogoParamVo.DAILY_POWER_GENERATION;
        } else if (dateType == 2) {
            timeInterval = "1n";
            functionLogo = FunctionLogoParamVo.MONTHLY_POWER_GENERATION;
        } else if (dateType == 3) {
            timeInterval = "1y";
            functionLogo = FunctionLogoParamVo.POWER_GENERATION_THIS_YEAR;
        } else {
            functionLogo = null;
        }
        //逆变器设备数据
        List<DeviceBasicInfoDto> inverterDeviceList = deviceBasicInfoDtos.stream().filter(d -> StringUtil.isNotEmpty(d.getTypeId()) && "20".equals(d.getTypeId())).collect(Collectors.toList());
        //光伏气象站数据
        List<DeviceBasicInfoDto> weatherDeviceList = deviceBasicInfoDtos.stream().filter(d -> StringUtil.isNotEmpty(d.getTypeId()) && "65".equals(d.getTypeId())).collect(Collectors.toList());
        //查询逆变器设备所有实时功率功能点历史数据
        Map<String, Map<String, List<NodeDifHistoryDto>>> inverterHistoryDataMap = Maps.newHashMap();
        Map<String, Map<String, List<DeviceHistoryDto>>> siteDeviceMaxPowerMap = Maps.newHashMap();
        if (CollectionUtils.isNotEmpty(inverterDeviceList)) {

            //获取设备周期内有功功率最大值
            DeviceCountQueryVo deviceCountQueryVo = new DeviceCountQueryVo();
            deviceCountQueryVo.setDeviceIds(inverterDeviceList.stream().map(DeviceBasicInfoDto::getId).collect(Collectors.toSet()));
            deviceCountQueryVo.setStartTime(startTime);
            deviceCountQueryVo.setEndTime(endTime);
            deviceCountQueryVo.setCuntFun("MAX");
            deviceCountQueryVo.setTimeInterval(timeInterval);
            deviceCountQueryVo.setFunctionLogos(new HashSet<>(Collections.singletonList(FunctionLogoParamVo.ACTIVE_POWER)));
            siteDeviceMaxPowerMap = dataService.findDeviceCountFunListFeign(deviceCountQueryVo).getData();

            inverterDeviceList.addAll(weatherDeviceList);
            DeviceHistoryQueryVo deviceHistoryQueryVo = new DeviceHistoryQueryVo();
            deviceHistoryQueryVo.setDeviceIds(inverterDeviceList.stream().map(DeviceBasicInfoDto::getId).collect(Collectors.toSet()));
            deviceHistoryQueryVo.setFunctionLogos(new HashSet<>(Arrays.asList(functionLogo, FunctionLogoParamVo.PV_THEORY_CAPACITY)));
            deviceHistoryQueryVo.setStartTime(startTime);
            deviceHistoryQueryVo.setEndTime(endTime);
            deviceHistoryQueryVo.setTimeInterval(timeInterval);
            inverterHistoryDataMap = dataService.findNodeDifHistoryListFeign(deviceHistoryQueryVo).getData();
        }

        //查询光伏气象站平均温度功能点数据
        Map<String, Map<String, List<DeviceHistoryDto>>> siteDeviceAvgTempMap = Maps.newHashMap();
        if (CollectionUtils.isNotEmpty(weatherDeviceList)) {
            DeviceCountQueryVo deviceCountQueryVo = new DeviceCountQueryVo();
            deviceCountQueryVo.setDeviceIds(weatherDeviceList.stream().map(DeviceBasicInfoDto::getId).collect(Collectors.toSet()));
            deviceCountQueryVo.setStartTime(startTime);
            deviceCountQueryVo.setEndTime(endTime);
            deviceCountQueryVo.setCuntFun("AVG");
            if (dateType == 2) {
                deviceCountQueryVo.setTimeInterval("1d");
            } else if (dateType == 3) {
                deviceCountQueryVo.setTimeInterval("1n");
            }
            deviceCountQueryVo.setFunctionLogos(new HashSet<>(Collections.singletonList(FunctionLogoParamVo.TEMPERATURE)));
            siteDeviceAvgTempMap = dataService.findDeviceCountFunListFeign(deviceCountQueryVo).getData();
        }

        //根据站点id分组并循环
        Map<String, Map<String, List<NodeDifHistoryDto>>> finalInverterHistoryDataMap = inverterHistoryDataMap;
        Map<String, Map<String, List<DeviceHistoryDto>>> finalSiteDeviceMaxPowerMap = siteDeviceMaxPowerMap;
        Map<String, Map<String, List<DeviceHistoryDto>>> finalSiteDeviceAvgTempMap = siteDeviceAvgTempMap;
        deviceBasicInfoDtos.stream().collect(Collectors.groupingBy(DeviceBasicInfoDto::getSiteId))
                .forEach((siteId, deviceBasicInfoDtoList) -> {
                    Map<String, Double> dataMap = Maps.newHashMap();

                    //过滤出逆变器数据
                    List<DeviceBasicInfoDto> inverterDevices = deviceBasicInfoDtoList.stream().filter(d -> StringUtil.isNotEmpty(d.getTypeId()) && "20".equals(d.getTypeId())).collect(Collectors.toList());
                    if (CollectionUtils.isNotEmpty(inverterDevices)) {

                        //过滤出当前站点下所有逆变器设备的发电量历史数据
                        List<Map<String, List<NodeDifHistoryDto>>> deviceQtHistoryList = inverterDevices.stream()
                                .map(deviceBasicInfoDto -> finalInverterHistoryDataMap.getOrDefault(deviceBasicInfoDto.getId(), Maps.newHashMap()))
                                .collect(Collectors.toList());
                        if (CollectionUtils.isNotEmpty(deviceQtHistoryList)) {
                            // 存储所有历史数据
                            List<NodeDifHistoryDto> deviceHistoryDtoList = deviceQtHistoryList.stream()
                                    .flatMap(deviceHistoryMap -> deviceHistoryMap.getOrDefault(functionLogo, Lists.newArrayList()).stream())
                                    .collect(Collectors.toList());

                            dataMap.put("daily_power_generation", getToDouble(deviceHistoryDtoList.stream().filter(d -> StringUtil.isNotEmpty(d.getLastDataValue())).mapToDouble(d -> Double.parseDouble(String.valueOf(d.getLastDataValue()))).sum()));
                        }

                        //过滤出当前站点下所有逆变器设备的最大功率历史数据
                        List<Map<String, List<DeviceHistoryDto>>> devicePowerHistoryList = inverterDevices.stream()
                                .map(deviceBasicInfoDto -> finalSiteDeviceMaxPowerMap.getOrDefault(deviceBasicInfoDto.getId(), Maps.newHashMap()))
                                .collect(Collectors.toList());
                        if (CollectionUtils.isNotEmpty(devicePowerHistoryList)) {
                            // 存储所有历史数据
                            List<DeviceHistoryDto> deviceHistoryDtoList = devicePowerHistoryList.stream()
                                    .flatMap(deviceHistoryMap -> deviceHistoryMap.getOrDefault(functionLogo, Lists.newArrayList()).stream())
                                    .collect(Collectors.toList());

                            dataMap.put("max_power", getToDouble(deviceHistoryDtoList.stream().filter(d -> StringUtil.isNotEmpty(d.getDataValue())).mapToDouble(d -> Double.parseDouble(String.valueOf(d.getDataValue()))).sum()));
                        }

                    }

                    //过滤出光伏气象站数据
                    List<DeviceBasicInfoDto> weatherDevices = deviceBasicInfoDtoList.stream().filter(d -> StringUtil.isNotEmpty(d.getTypeId()) && "65".equals(d.getTypeId())).collect(Collectors.toList());
                    if (CollectionUtils.isNotEmpty(weatherDevices)) {

                        //过滤出当前站点下所有气象站设备的理论发电量历史数据
                        List<Map<String, List<NodeDifHistoryDto>>> deviceQtHistoryList = weatherDevices.stream()
                                .map(deviceBasicInfoDto -> finalInverterHistoryDataMap.getOrDefault(deviceBasicInfoDto.getId(), Maps.newHashMap()))
                                .collect(Collectors.toList());
                        if (CollectionUtils.isNotEmpty(deviceQtHistoryList)) {
                            // 存储所有历史数据
                            List<NodeDifHistoryDto> deviceHistoryDtoList = deviceQtHistoryList.stream()
                                    .flatMap(deviceHistoryMap -> deviceHistoryMap.getOrDefault(FunctionLogoParamVo.PV_THEORY_CAPACITY, Lists.newArrayList()).stream())
                                    .collect(Collectors.toList());

                            dataMap.put(FunctionLogoParamVo.PV_THEORY_CAPACITY, getToDouble(deviceHistoryDtoList.stream().mapToDouble(d -> StringUtil.isNotEmpty(d.getLastDataValue()) ? Double.parseDouble(String.valueOf(d.getLastDataValue())) : 0.0).sum()));
                        }

                        //过滤出当前站点下所有气象站设备的平均温度历史数据
                        List<Map<String, List<DeviceHistoryDto>>> deviceAvgTempHistoryList = weatherDevices.stream()
                                .map(deviceBasicInfoDto -> finalSiteDeviceAvgTempMap.getOrDefault(deviceBasicInfoDto.getId(), Maps.newHashMap()))
                                .collect(Collectors.toList());
                        if (CollectionUtils.isNotEmpty(deviceAvgTempHistoryList)) {
                            // 存储所有历史数据
                            List<DeviceHistoryDto> deviceHistoryDtoList = deviceAvgTempHistoryList.stream()
                                    .flatMap(deviceHistoryMap -> deviceHistoryMap.getOrDefault(FunctionLogoParamVo.TEMPERATURE, Lists.newArrayList()).stream())
                                    .collect(Collectors.toList());

                            dataMap.put(FunctionLogoParamVo.TEMPERATURE, getToDouble(deviceHistoryDtoList.stream().mapToDouble(d -> StringUtil.isNotEmpty(d.getDataValue()) ? Double.parseDouble(String.valueOf(d.getDataValue())) : 0.0).sum()));
                        }
                    }
                    resultMap.put(siteId, dataMap);
                });
        return resultMap;
    }*/

}
