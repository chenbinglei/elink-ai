package com.sunmax.together.service.monitor.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.AtomicDouble;
import com.sunmax.common.constant.FunctionqConstant;
import com.sunmax.common.dto.crontab.LocalCacheDto;
import com.sunmax.common.dto.crontab.NodeHistoryDataDto;
import com.sunmax.common.dto.data.DeviceHistoryDto;
import com.sunmax.common.dto.data.NodeDifHistoryDto;
import com.sunmax.common.dto.device.DeviceBasicInfoDto;
import com.sunmax.common.dto.device.DeviceGunInfoDto;
import com.sunmax.common.dto.device.ModelFunctionListDto;
import com.sunmax.common.dto.device.SiteInfoDto;
import com.sunmax.common.dto.system.OrganEmpowerListDto;
import com.sunmax.common.dto.together.SystemDeviceListDto;
import com.sunmax.common.enums.SystemVariableEnum;
import com.sunmax.common.model.RealDataModel;
import com.sunmax.common.model.general.PileRealModel;
import com.sunmax.common.util.DateUtil;
import com.sunmax.common.util.DoubleUtil;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.util.StringUtil;
import com.sunmax.common.vo.FunctionLogoParamVo;
import com.sunmax.common.vo.ReaFieldParamVo;
import com.sunmax.common.vo.SiteFieldParamVo;
import com.sunmax.common.vo.crontab.VarNodeCuntFunQueryVo;
import com.sunmax.common.vo.crontab.VarNodeValueVo;
import com.sunmax.common.vo.data.DeviceHistoryQueryVo;
import com.sunmax.common.vo.data.DeviceIndexQueryVo;
import com.sunmax.together.dao.SiteCountRecordDao;
import com.sunmax.together.dao.order.OrderRecordDao;
import com.sunmax.together.dto.monitor.assetOverview.*;
import com.sunmax.together.dto.monitor.centralMonitorOld.PvSiteMonitorDto;
import com.sunmax.together.entity.SiteCountRecordEntity;
import com.sunmax.together.entity.order.OrderRecordEntity;
import com.sunmax.together.mapper.OrderRecordMapper;
import com.sunmax.together.service.feign.CrontabService;
import com.sunmax.together.service.feign.DataService;
import com.sunmax.together.service.feign.DeviceService;
import com.sunmax.together.service.feign.SystemService;
import com.sunmax.together.service.monitor.AssetOverviewService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.sunmax.common.config.redis.RedisGeneralUtil.getPileRealModelList;
import static com.sunmax.common.util.DateUtil.*;
import static com.sunmax.common.util.DoubleUtil.getToDouble;

@Slf4j
@Service
public class AssetOverviewServiceImpl implements AssetOverviewService {

    @Autowired
    private SystemService systemService;

    @Autowired
    private DeviceService deviceService;

    @Autowired
    private CrontabService crontabService;

    @Autowired
    private OrderRecordDao orderRecordDao;

    @Autowired
    private DataService dataService;

    @Autowired
    private OrderRecordMapper orderRecordMapper;

    @Autowired
    private SiteCountRecordDao siteCountRecordDao;

    /**
     * 查询资产站点列表
     *
     * @param userId       当前登录用户id
     * @param scenarioType 场景类型 1-光伏 2-储能 3-充电桩（不传默认全部）
     * @param areaType     区域类型 1-省级 2-市级（不传默认全部）
     * @param areaName     区域名称
     * @param siteName     站点名称(用于模糊查询)
     * @return
     */
    @Override
    public ResponseResult<List<AssetSiteListDto>> findAssetSiteList(String userId, Integer scenarioType, Integer areaType, String areaName, String siteName) {
        List<AssetSiteListDto> resultList = Lists.newArrayList();
        //根据当前登录用户id，查询相关配置资产授权站点数据
        ResponseResult<List<OrganEmpowerListDto>> allOrganEmpowerByTenantId = systemService.findAllOrganEmpowerByUserId(userId);
        if (allOrganEmpowerByTenantId.isSuccess() && CollectionUtils.isNotEmpty(allOrganEmpowerByTenantId.getData())) {
            List<OrganEmpowerListDto> organEmpowerListDtos = allOrganEmpowerByTenantId.getData();
            //根据多个站点id查询站点信息
            ResponseResult<List<SiteInfoDto>> siteInfoListByIds = deviceService.findSiteInfoListByIds(organEmpowerListDtos.stream().map(OrganEmpowerListDto::getSiteId).filter(StringUtil::isNotEmpty).distinct().collect(Collectors.toList()));
            if (siteInfoListByIds.isSuccess() && CollectionUtils.isNotEmpty(siteInfoListByIds.getData())) {
                List<SiteInfoDto> siteInfoDtoList = siteInfoListByIds.getData();
                //根据能源类型查询
                if (StringUtil.isNotEmpty(scenarioType)) {
                    siteInfoDtoList = siteInfoDtoList.stream().filter(s -> StringUtil.isNotEmpty(s.getScenarioTypes()) && s.getScenarioTypes().contains(String.valueOf(scenarioType))).collect(Collectors.toList());
                }
                //判断是否根据区域类型查询
                if (StringUtil.isNotEmpty(areaType) && StringUtil.isNotEmpty(areaName)) {
                    siteInfoDtoList = siteInfoDtoList.stream()
                            .filter(siteInfoDto -> {
                                Map<String, Object> readwriteMap = Maps.newHashMap();
                                String siteReadwriteObject = siteInfoDto.getSiteReadwriteObject();
                                if (StringUtil.isNotEmpty(siteReadwriteObject)) {
                                    Map<String, Object> parseObjectMap = JSON.parseObject(siteReadwriteObject, new TypeReference<Map<String, Object>>() {
                                    });
                                    if (parseObjectMap.containsKey(SiteFieldParamVo.LOCATION)) {
                                        readwriteMap = JSON.parseObject(JSON.toJSONString(parseObjectMap.get(SiteFieldParamVo.LOCATION)), new TypeReference<Map<String, Object>>() {
                                        });
                                    }
                                }
                                return matchesAreaType(readwriteMap, areaType, areaName);
                            })
                            .collect(Collectors.toList());
                    //根据站点名称模糊查询
                    if (StringUtil.isNotEmpty(siteName)) {
                        siteInfoDtoList = siteInfoDtoList.stream().filter(siteInfoDto -> siteInfoDto.getSiteName().contains(siteName)).collect(Collectors.toList());
                    }
                }
                if (CollectionUtils.isNotEmpty(siteInfoDtoList)) {
                    resultList = siteInfoDtoList.stream().map(siteInfoDto -> {
                        AssetSiteListDto assetSiteListDto = new AssetSiteListDto();
                        BeanUtils.copyProperties(siteInfoDto, assetSiteListDto);
                        //获取站点扩展属性信息
                        String siteReadwriteObject = siteInfoDto.getSiteReadwriteObject();
                        if (StringUtil.isNotEmpty(siteReadwriteObject)) {
                            Map<String, Object> readwriteMap = JSON.parseObject(siteReadwriteObject, new TypeReference<Map<String, Object>>() {
                            });
                            //地址对象
                            if (readwriteMap.containsKey(SiteFieldParamVo.LOCATION)) {
                                Map<String, Object> localtionReadwriteMap = JSON.parseObject(JSON.toJSONString(readwriteMap.get(SiteFieldParamVo.LOCATION)), new TypeReference<Map<String, Object>>() {
                                });
                                //经度
                                if (!localtionReadwriteMap.isEmpty() && localtionReadwriteMap.containsKey(SiteFieldParamVo.LONGITUDE) && StringUtil.isNotEmpty(localtionReadwriteMap.get(SiteFieldParamVo.LONGITUDE))) {
                                    assetSiteListDto.setLongitude(localtionReadwriteMap.get(SiteFieldParamVo.LONGITUDE).toString());
                                }
                                //纬度
                                if (!localtionReadwriteMap.isEmpty() && localtionReadwriteMap.containsKey(SiteFieldParamVo.LATITUDE) && StringUtil.isNotEmpty(localtionReadwriteMap.get(SiteFieldParamVo.LATITUDE))) {
                                    assetSiteListDto.setLatitude(localtionReadwriteMap.get(SiteFieldParamVo.LATITUDE).toString());
                                }
                            }
                        }
                        return assetSiteListDto;
                    }).collect(Collectors.toList());
                }
            }
        }
        return ResponseResult.ok(resultList);
    }

    boolean matchesAreaType(Map<String, Object> readwriteMap, Integer areaType, String areaName) {
        String areaKey = areaType == 1 ? SiteFieldParamVo.PROVINCE : SiteFieldParamVo.CITY;
        Object areaValue = readwriteMap.get(areaKey);

        return areaValue != null && areaValue.equals(areaName);
    }

    /**
     * 查询光伏资产统计数据
     *
     * @param siteIds 多个站点id
     * @return
     */
    @Override
    public ResponseResult<PvAssetCountDto> findPvAssetCountData(String siteIds) {
        PvAssetCountDto pvAssetCountDto = new PvAssetCountDto();
        if (StringUtil.isEmpty(siteIds)) {
            return ResponseResult.error(ResponseResult.PARAM_ERROR);
        }
        List<String> siteIdList = JSON.parseArray(siteIds, String.class);

        // 根据多个站点ID查询站点信息
        ResponseResult<List<SiteInfoDto>> siteInfoListByIds = deviceService.findSiteInfoListByIds(siteIdList);
        if (siteInfoListByIds.isSuccess() && CollectionUtils.isNotEmpty(siteInfoListByIds.getData())) {
            //过滤出光伏站点数据
            List<SiteInfoDto> siteInfoDtoList = siteInfoListByIds.getData()
                    .stream().filter(s -> StringUtil.isNotEmpty(s.getScenarioTypes()) && s.getScenarioTypes().contains("1")).collect(Collectors.toList());

            if (CollectionUtils.isNotEmpty(siteInfoDtoList)) {
                pvAssetCountDto.setSiteNum(siteInfoDtoList.size());

                // 获取光伏装机量
                double pvCapacity = siteInfoDtoList.stream().mapToDouble(s -> {
                    String siteReadwriteObject = s.getSiteReadwriteObject();
                    if (StringUtil.isNotEmpty(siteReadwriteObject)) {
                        Map<String, Object> parseObjectMap = JSON.parseObject(siteReadwriteObject, new TypeReference<Map<String, Object>>() {});
                        //光伏电站装机量
                        if (parseObjectMap.containsKey(SiteFieldParamVo.PV_CAPACITY) && StringUtil.isNotEmpty(parseObjectMap.get(SiteFieldParamVo.PV_CAPACITY))) {
                            return Double.parseDouble(String.valueOf(parseObjectMap.get(SiteFieldParamVo.PV_CAPACITY)));
                        }
                    }
                    return 0.0;
                }).sum();

                pvAssetCountDto.setCapacity(pvCapacity);

                // 获取站点下逆变器设备数量
                ResponseResult<Map<String, List<DeviceBasicInfoDto>>> deviceBasicInfoBySiteIds = deviceService.findDeviceBasicInfoBySiteIds(siteIdList, null);
                if (deviceBasicInfoBySiteIds.isSuccess() && !deviceBasicInfoBySiteIds.getData().isEmpty()) {
                    List<DeviceBasicInfoDto> deviceBasicInfoDtoList = Lists.newArrayList();
                    deviceBasicInfoBySiteIds.getData().values().forEach(deviceBasicInfoDtoList::addAll);
                    List<DeviceBasicInfoDto> deviceBasicInfoDtos = deviceBasicInfoDtoList.stream().filter(d -> "20".equals(d.getTypeId())).collect(Collectors.toList());
                    if (CollectionUtils.isNotEmpty(deviceBasicInfoDtos)) {
                        pvAssetCountDto.setInverterNum(deviceBasicInfoDtos.size());

                        //根据多个设备id，和多个功能点标识查询设备功能点实时数据
                        ResponseResult<Map<String, Map<String, RealDataModel>>> realDataMapResult = deviceService.getDeviceFunctionsRealDataByIds(deviceBasicInfoDtos.stream()
                                .map(DeviceBasicInfoDto::getId).collect(Collectors.toSet()), FunctionLogoParamVo.ACTIVE_POWER, 2);
                        //获取设备功能点实时数据
                        if (realDataMapResult.isSuccess() && !realDataMapResult.getData().isEmpty()) {
                            List<PvSiteMonitorDto.FunctionRalData> realDataList = realDataMapResult.getData().entrySet().stream()
                                    .flatMap(entry -> entry.getValue().entrySet().stream()
                                            .map(subEntry -> {
                                                PvSiteMonitorDto.FunctionRalData functionRalData = new PvSiteMonitorDto.FunctionRalData();
                                                functionRalData.setFunctionLogo(subEntry.getKey());
                                                functionRalData.setRealData(subEntry.getValue().getDataValue());
                                                return functionRalData;
                                            }))
                                    .collect(Collectors.toList());

                            //根据功能点标识分组
                            Map<String, List<PvSiteMonitorDto.FunctionRalData>> functionRalDataMap = realDataList.stream().collect(Collectors.groupingBy(PvSiteMonitorDto.FunctionRalData::getFunctionLogo));

                            //统计有功功率
                            if (functionRalDataMap.containsKey(FunctionLogoParamVo.ACTIVE_POWER) && CollectionUtils.isNotEmpty(functionRalDataMap.get(FunctionLogoParamVo.ACTIVE_POWER))) {
                                pvAssetCountDto.setRealOutput(getToDouble(functionRalDataMap.get(FunctionLogoParamVo.ACTIVE_POWER).stream().filter(f -> StringUtil.isNotEmpty(f.getRealData())).mapToDouble(f -> Double.parseDouble(String.valueOf(f.getRealData()))).sum()));
                            }
                            //计算运行效率 = 实时出力/装机容量 * 100
                            if (StringUtil.isNotEmpty(pvAssetCountDto.getRealOutput()) && StringUtil.isNotEmpty(pvCapacity) && pvCapacity > 0.0 ) {
                                pvAssetCountDto.setRunEfficiency(getToDouble(pvAssetCountDto.getRealOutput() / pvCapacity * 100));
                            }
                        }

                        //查询累计统计数据
                        pvAssetCountDto.setSumCountData(getPvCountData(siteIdList, null, null, pvCapacity, 4, deviceBasicInfoDtos));

                        //获取本月开始时间
                        String monthStartTime = getStartTimeByQueryType(4);
                        //获取本月结束时间
                        String monthEndTime = getEndTimeByQueryType(4);
                        //查询本月统计数据
                        pvAssetCountDto.setMonthCountData(getPvCountData(siteIdList, monthStartTime, monthEndTime, pvCapacity, 3, deviceBasicInfoDtos));

                        //获取昨日开始时间
                        String lastDayStartTime = getStartTimeByQueryType(1);
                        //获取昨日结束时间
                        String lastDayEndTime = getEndTimeByQueryType(1);
                        //查询昨日统计数据
                        pvAssetCountDto.setLastDayCountData(getPvCountData(siteIdList, lastDayStartTime, lastDayEndTime, pvCapacity, 2, deviceBasicInfoDtos));

                        //获取今日开始时间
                        String dayStartTime = getStartTimeByQueryType(0);
                        //获取今日结束时间
                        String dayEndTime = getEndTimeByQueryType(0);
                        //查询今日统计数据
                        pvAssetCountDto.setDayCountData(getPvCountData(siteIdList, dayStartTime, dayEndTime, pvCapacity, 1, deviceBasicInfoDtos));
                    }
                }
            }
        }
        return ResponseResult.ok(pvAssetCountDto);
    }

    private PvAssetCountDto.CountData getPvCountData(List<String> queryIdList, String startTime, String endTime, Double pvCapacity, int queryType, List<DeviceBasicInfoDto> deviceBasicInfoDtos) {
        PvAssetCountDto.CountData countData = new PvAssetCountDto.CountData();

        //如果查询类型为1，则查询今日实时节点数据
        if (queryType == 1) {
            queryAssetCountRealData(queryIdList, countData);
        } else {
            queryAssetCountHistoryData(queryIdList, startTime, endTime, countData);
        }

        String functionLogo = null;
        if (queryType == 1) {
            functionLogo = FunctionLogoParamVo.DAILY_POWER_GENERATION;
        } else if (queryType == 2) {
            functionLogo = FunctionLogoParamVo.DAILY_POWER_GENERATION + "," + FunctionLogoParamVo.ELECTRICITY_GENERATED_YESTERDAY;
        } else if (queryType == 3) {
            functionLogo = FunctionLogoParamVo.DAILY_POWER_GENERATION + "," + FunctionLogoParamVo.MONTHLY_POWER_GENERATION;
        } else if (queryType == 4) {
            functionLogo = FunctionLogoParamVo.DAILY_POWER_GENERATION + "," + FunctionLogoParamVo.TOTAL_POWER_GENERATION;
        }

        //根据多个设备id，和多个功能点标识查询设备功能点实时数据
        ResponseResult<Map<String, Map<String, RealDataModel>>> realDataMapResult = deviceService.getDeviceFunctionsRealDataByIds(deviceBasicInfoDtos.stream()
                .map(DeviceBasicInfoDto::getId).collect(Collectors.toSet()), functionLogo, 1);
        //获取设备功能点实时数据
        if (realDataMapResult.isSuccess() && !realDataMapResult.getData().isEmpty()) {
            List<PvSiteMonitorDto.FunctionRalData> realDataList = realDataMapResult.getData().entrySet().stream()
                    .flatMap(entry -> entry.getValue().entrySet().stream()
                            .map(subEntry -> {
                                PvSiteMonitorDto.FunctionRalData functionRalData = new PvSiteMonitorDto.FunctionRalData();
                                functionRalData.setFunctionLogo(subEntry.getKey());
                                functionRalData.setRealData(subEntry.getValue().getDataValue());
                                return functionRalData;
                            }))
                    .collect(Collectors.toList());

            //根据功能点标识分组
            Map<String, List<PvSiteMonitorDto.FunctionRalData>> functionRalDataMap = realDataList.stream().collect(Collectors.groupingBy(PvSiteMonitorDto.FunctionRalData::getFunctionLogo));
            //统计总发电量
            if (functionRalDataMap.containsKey(FunctionLogoParamVo.TOTAL_POWER_GENERATION) && CollectionUtils.isNotEmpty(functionRalDataMap.get(FunctionLogoParamVo.TOTAL_POWER_GENERATION))) {
                countData.setTotalQt(getToDouble(functionRalDataMap.get(FunctionLogoParamVo.TOTAL_POWER_GENERATION).stream().filter(f -> StringUtil.isNotEmpty(f.getRealData())).mapToDouble(f -> Double.parseDouble(String.valueOf(f.getRealData()))).sum()));
            }
            //统计今日发电量
            if (functionRalDataMap.containsKey(FunctionLogoParamVo.DAILY_POWER_GENERATION) && CollectionUtils.isNotEmpty(functionRalDataMap.get(FunctionLogoParamVo.DAILY_POWER_GENERATION))) {
                if (queryType == 1) {
                    countData.setTotalQt(getToDouble(functionRalDataMap.get(FunctionLogoParamVo.DAILY_POWER_GENERATION).stream().filter(f -> StringUtil.isNotEmpty(f.getRealData())).mapToDouble(f -> Double.parseDouble(String.valueOf(f.getRealData()))).sum()));
                }
                countData.setDayQt(getToDouble(functionRalDataMap.get(FunctionLogoParamVo.DAILY_POWER_GENERATION).stream().filter(f -> StringUtil.isNotEmpty(f.getRealData())).mapToDouble(f -> Double.parseDouble(String.valueOf(f.getRealData()))).sum()));
            }
            //统计昨日发电量
            if (functionRalDataMap.containsKey(FunctionLogoParamVo.ELECTRICITY_GENERATED_YESTERDAY) && CollectionUtils.isNotEmpty(functionRalDataMap.get(FunctionLogoParamVo.ELECTRICITY_GENERATED_YESTERDAY))) {
                countData.setTotalQt(getToDouble(functionRalDataMap.get(FunctionLogoParamVo.ELECTRICITY_GENERATED_YESTERDAY).stream().filter(f -> StringUtil.isNotEmpty(f.getRealData())).mapToDouble(f -> Double.parseDouble(String.valueOf(f.getRealData()))).sum()));
            }
            //统计当月发电量
            if (functionRalDataMap.containsKey(FunctionLogoParamVo.MONTHLY_POWER_GENERATION) && CollectionUtils.isNotEmpty(functionRalDataMap.get(FunctionLogoParamVo.MONTHLY_POWER_GENERATION))) {
                countData.setTotalQt(getToDouble(functionRalDataMap.get(FunctionLogoParamVo.MONTHLY_POWER_GENERATION).stream().filter(f -> StringUtil.isNotEmpty(f.getRealData())).mapToDouble(f -> Double.parseDouble(String.valueOf(f.getRealData()))).sum()));
            }
        }

        //计算消纳电量(总发电量 - 上网电量)
        countData.setAbsorptiveQt(getToDouble(countData.getTotalQt() - countData.getInternetQt()));

        //计算消纳率 消纳率=消纳电量/总发电量*100%
        if (StringUtil.isNotEmpty(countData.getAbsorptiveQt()) && StringUtil.isNotEmpty(countData.getTotalQt()) && countData.getTotalQt() > 0.0) {
            countData.setAbsorptiveRate(getToDouble(countData.getAbsorptiveQt() / countData.getTotalQt() * 100));
        }
        //计算今日等效发电小时数 今日等效发电小时数=今日发电量/装机量
        if (StringUtil.isNotEmpty(countData.getDayQt()) && StringUtil.isNotEmpty(pvCapacity) && pvCapacity > 0.0) {
            countData.setWaitOutHour(getToDouble(countData.getDayQt() / pvCapacity, 2));
        }
        return countData;
    }

    //查询历史数据
    private void queryAssetCountHistoryData(List<String> queryIdList, String startTime, String endTime, PvAssetCountDto.CountData countData) {
        VarNodeCuntFunQueryVo varNodeCuntFunQueryVo = new VarNodeCuntFunQueryVo();
        varNodeCuntFunQueryVo.setVarCodeList(Collections.singletonList(SystemVariableEnum.ONGRIDENERGY.getCode()));
        varNodeCuntFunQueryVo.setQueryIdList(queryIdList);
        varNodeCuntFunQueryVo.setStartTime(startTime);
        varNodeCuntFunQueryVo.setEndTime(endTime);
        varNodeCuntFunQueryVo.setCuntFun(FunctionqConstant.SUM);
        ResponseResult<Map<String, Map<String, List<NodeHistoryDataDto>>>> varNodeDataByCountFun = crontabService.findVarNodeDataByCountFun(varNodeCuntFunQueryVo);
        if (varNodeDataByCountFun.isSuccess() && !varNodeDataByCountFun.getData().isEmpty()) {
            Map<String, Map<String, List<NodeHistoryDataDto>>> nodeDataByCountFunMap = varNodeDataByCountFun.getData();
            //上网电量
            if (nodeDataByCountFunMap.containsKey(SystemVariableEnum.ONGRIDENERGY.getCode())) {
                Map<String, List<NodeHistoryDataDto>> nodeDataMap = nodeDataByCountFunMap.get(SystemVariableEnum.ONGRIDENERGY.getCode());
                List<NodeHistoryDataDto> nodeHistoryDataDtos = nodeDataMap.values().stream().flatMap(Collection::stream).collect(Collectors.toList());
                if (CollectionUtils.isNotEmpty(nodeHistoryDataDtos) && StringUtil.isNotEmpty(nodeHistoryDataDtos.get(0))) {
                    double sum = nodeHistoryDataDtos.stream().filter(n -> StringUtil.isNotEmpty(n.getResultValue())).mapToDouble(NodeHistoryDataDto::getResultValue).sum();
                    if (StringUtil.isNotEmpty(sum)) {
                        countData.setInternetQt(getToDouble(sum));
                    }
                }
            }
        }
    }

    //查询实时数据
    private void queryAssetCountRealData(List<String> queryIdList, PvAssetCountDto.CountData countData) {
        ResponseResult<Map<String, Map<String, LocalCacheDto>>> nodeCacheByVarCodes = crontabService.findNodeCacheByVarCodes(
                Collections.singletonList(SystemVariableEnum.ONGRIDENERGY.getCode()), String.join(",", queryIdList));
        if (nodeCacheByVarCodes.isSuccess() && !nodeCacheByVarCodes.getData().isEmpty() && nodeCacheByVarCodes.getData().containsKey(SystemVariableEnum.ONGRIDENERGY.getCode())) {
            Map<String, LocalCacheDto> localCacheDtoMap = nodeCacheByVarCodes.getData().get(SystemVariableEnum.ONGRIDENERGY.getCode());
            if (MapUtils.isNotEmpty(localCacheDtoMap) && CollectionUtils.isNotEmpty(localCacheDtoMap.values())) {
                countData.setInternetQt(getToDouble(localCacheDtoMap.values().stream().filter(l -> StringUtil.isNotEmpty(l) && StringUtil.isNotEmpty(l.getResultValue())).mapToDouble(LocalCacheDto::getResultValue).sum()));
            }
        }
    }

    /**
     * 查询光伏发电量分析曲线数据
     *
     * @param userId 当前登录用户id
     * @param siteId 站点id（不传默认查询全部光伏站点）
     * @return
     */
    @Override
    public ResponseResult<Map<String, Object>> findPvGenerationCurveData(String userId, String siteId) {
        return findEnergyCurveData(userId, siteId, "1", Arrays.asList(SystemVariableEnum.ONGRIDENERGY.getCode(), SystemVariableEnum.CONSUMEENERGY.getCode()));
    }

    /**
     * 查询储能资产统计数据
     *
     * @param siteIds 多个站点id
     * @return
     */
    @Override
    public ResponseResult<StorageAssetCountDto> findStorageAssetCountData(String siteIds) {
        StorageAssetCountDto storageAssetCountDto = new StorageAssetCountDto();
        //多个站点id
        if (StringUtil.isEmpty(siteIds)) {
            return ResponseResult.error(ResponseResult.PARAM_ERROR);
        }
        List<String> siteIdList = JSON.parseArray(siteIds, String.class);

        // 根据多个站点ID查询站点信息
        ResponseResult<List<SiteInfoDto>> siteInfoListByIds = deviceService.findSiteInfoListByIds(siteIdList);
        if (siteInfoListByIds.isSuccess() && CollectionUtils.isNotEmpty(siteInfoListByIds.getData())) {
            //过滤出储能站点数据
            List<SiteInfoDto> siteInfoDtoList = siteInfoListByIds.getData()
                    .stream().filter(s -> StringUtil.isNotEmpty(s.getScenarioTypes()) && s.getScenarioTypes().contains("2")).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(siteInfoDtoList)) {
                storageAssetCountDto.setSiteNum(siteInfoDtoList.size());

                // 获取站点下pcs设备数量,光伏设备数量
                Map<String, List<DeviceBasicInfoDto>> siteDeviceMap = deviceService.findDeviceBasicInfoBySiteIds(siteIdList, null).getData();
                if (MapUtils.isNotEmpty(siteDeviceMap)) {
                    List<DeviceBasicInfoDto> deviceList = siteDeviceMap.values().stream().flatMap(Collection::stream).filter(s ->
                            StringUtil.isNotEmpty(s.getTypeId())).collect(Collectors.toList());

                    //根据设备类型id，获取pcs设备
                    List<DeviceBasicInfoDto> pcsDeviceInfoDtos = deviceList.stream().filter(d -> StringUtil.isNotEmpty(d.getTypeId()) && "23".equals(d.getTypeId())).collect(Collectors.toList());
                    if (CollectionUtils.isNotEmpty(pcsDeviceInfoDtos)) {
                        storageAssetCountDto.setPcsDeviceNum(pcsDeviceInfoDtos.size());
                        //统计pcs总额定功率
                        storageAssetCountDto.setStationRatedpower(pcsDeviceInfoDtos.stream().mapToDouble(d -> {
                            Map<String, Object> reaMap = d.getReaMap();
                            if (!reaMap.isEmpty() && reaMap.containsKey(ReaFieldParamVo.RATED_POWER) && StringUtil.isNotEmpty(reaMap.get(ReaFieldParamVo.RATED_POWER))) {
                                return Double.parseDouble(String.valueOf(reaMap.get(ReaFieldParamVo.RATED_POWER)));
                            }
                            return 0.0;
                        }).sum());

                        //根据多个设备id，和多个功能点标识查询设备功能点实时数据
                        ResponseResult<Map<String, Map<String, RealDataModel>>> realDataMapResult = deviceService.getDeviceFunctionsRealDataByIds(
                                pcsDeviceInfoDtos.stream().map(DeviceBasicInfoDto::getId).collect(Collectors.toSet()),
                                FunctionLogoParamVo.PCS_ACTIVE_POWER, 2);
                        //获取设备功能点实时数据
                        if (realDataMapResult.isSuccess() && !realDataMapResult.getData().isEmpty()) {
                            List<PvSiteMonitorDto.FunctionRalData> realDataList = realDataMapResult.getData().entrySet().stream()
                                    .flatMap(entry -> entry.getValue().entrySet().stream()
                                            .map(subEntry -> {
                                                PvSiteMonitorDto.FunctionRalData functionRalData = new PvSiteMonitorDto.FunctionRalData();
                                                functionRalData.setFunctionLogo(subEntry.getKey());
                                                functionRalData.setRealData(subEntry.getValue().getDataValue());
                                                return functionRalData;
                                            }))
                                    .collect(Collectors.toList());

                            //根据功能点标识分组
                            Map<String, List<PvSiteMonitorDto.FunctionRalData>> functionRalDataMap = realDataList.stream().collect(Collectors.groupingBy(PvSiteMonitorDto.FunctionRalData::getFunctionLogo));
                            //有功功率
                            if (functionRalDataMap.containsKey(FunctionLogoParamVo.PCS_ACTIVE_POWER) && CollectionUtils.isNotEmpty(functionRalDataMap.get(FunctionLogoParamVo.PCS_ACTIVE_POWER))) {
                                AtomicDouble chargePower = new AtomicDouble(0.0);
                                AtomicDouble dischargePower = new AtomicDouble(0.0);

                                functionRalDataMap.get(FunctionLogoParamVo.PCS_ACTIVE_POWER).forEach(functionRalData -> {
                                    Object realData = functionRalData.getRealData();
                                    if (StringUtil.isNotEmpty(realData)) {
                                        double value = Double.parseDouble(String.valueOf(realData));
                                        if (value >= 0) {
                                            dischargePower.addAndGet(value);
                                        } else {
                                            chargePower.addAndGet(value);
                                        }
                                    }
                                });
                                storageAssetCountDto.setChargePower(getToDouble(Math.abs(chargePower.get())));
                                storageAssetCountDto.setDischargePower(getToDouble( -dischargePower.get()));
                            }
                        }
                    }

                    //根据设备类型id，获取电池簇设备
                    List<DeviceBasicInfoDto> batteryDeviceInfoDtos = deviceList.stream().filter(d -> StringUtil.isNotEmpty(d.getTypeId()) && "25".equals(d.getTypeId())).collect(Collectors.toList());
                    if (CollectionUtils.isNotEmpty(batteryDeviceInfoDtos)) {
                        //统计电池簇总额定容量
                        storageAssetCountDto.setCapacity(batteryDeviceInfoDtos.stream().mapToDouble(d -> {
                            Map<String, Object> reaMap = d.getReaMap();
                            if (!reaMap.isEmpty() && reaMap.containsKey(ReaFieldParamVo.RATED_CAP) && StringUtil.isNotEmpty(reaMap.get(ReaFieldParamVo.RATED_CAP))) {
                                return Double.parseDouble(String.valueOf(reaMap.get(ReaFieldParamVo.RATED_CAP)));
                            }
                            return 0.0;
                        }).sum());

                        //多个电池簇设备id
                        List<String> batteryIdList = batteryDeviceInfoDtos.stream().map(DeviceBasicInfoDto::getId).collect(Collectors.toList());
                        //查询累计统计数据
                        storageAssetCountDto.setSumCountData(getStorageRealCountData(batteryIdList));

                        //获取本月开始时间
                        String monthStartTime = getStartTimeByQueryType(4);
                        //获取本月结束时间
                        String monthEndTime = getEndTimeByQueryType(4);
                        //查询本月统计数据
                        storageAssetCountDto.setMonthCountData(getStorageCountData(batteryIdList, monthStartTime, monthEndTime, 3));

                        //获取昨日开始时间
                        String lastDayStartTime = getStartTimeByQueryType(1);
                        //获取昨日结束时间
                        String lastDayEndTime = getEndTimeByQueryType(1);
                        //查询昨日统计数据
                        storageAssetCountDto.setLastDayCountData(getStorageCountData(batteryIdList, lastDayStartTime, lastDayEndTime, 2));

                        //获取今日开始时间
                        String dayStartTime = getStartTimeByQueryType(0);
                        //获取今日结束时间
                        String dayEndTime = getEndTimeByQueryType(0);
                        //查询今日统计数据
                        storageAssetCountDto.setDayCountData(getStorageCountData(batteryIdList, dayStartTime, dayEndTime, 1));
                    }
                }
            }
        }
        return ResponseResult.ok(storageAssetCountDto);
    }

    /**
     * 查询储能充放电量分析曲线数据
     *
     * @param userId 当前登录用户id
     * @param siteId 站点id（不传默认查询全部储能站点）
     * @return
     */
    @Override
    public ResponseResult<Map<String, Object>> findStorageQtCurveData(String userId, String siteId) {
        return findEnergyCurveData(userId, siteId, "2", Arrays.asList(SystemVariableEnum.CHARGINGENERGY.getCode(), SystemVariableEnum.DISCHARGINGENERGY.getCode()));
    }

    private StorageAssetCountDto.CountData getStorageCountData(List<String> batteryIdList, String startTime, String endTime, Integer queryType) {
        StorageAssetCountDto.CountData countData = new StorageAssetCountDto.CountData();

        String timeInterval = null;
        if (queryType == 1 || queryType == 2) {//今日/昨日
            timeInterval = "1d";
        } else if (queryType == 3) {//本月
            timeInterval = "1n";
        }

        DeviceHistoryQueryVo deviceHistoryQueryVo = new DeviceHistoryQueryVo();
        deviceHistoryQueryVo.setDeviceIds(new HashSet<>(batteryIdList));
        deviceHistoryQueryVo.setFunctionLogos(new HashSet<>(Arrays.asList(FunctionLogoParamVo.ACCCHARGEQ, FunctionLogoParamVo.ACCDISCHARGEQ)));
        deviceHistoryQueryVo.setStartTime(startTime);
        deviceHistoryQueryVo.setEndTime(endTime);
        deviceHistoryQueryVo.setTimeInterval(timeInterval);
        ResponseResult<Map<String, Map<String, List<NodeDifHistoryDto>>>> deviceHistoryResult = dataService.findNodeDifHistoryListFeign(deviceHistoryQueryVo);
        if (deviceHistoryResult.isSuccess() && !deviceHistoryResult.getData().isEmpty()) {
            // 存储所有充电量历史数据
            List<NodeDifHistoryDto> sumChargeList = Lists.newArrayList();
            // 存储所有放电量历史数据
            List<NodeDifHistoryDto> sumDischargeList = Lists.newArrayList();
            deviceHistoryResult.getData().forEach((deviceId, deviceHistoryMap) -> {
                if (!deviceHistoryMap.isEmpty() && deviceHistoryMap.containsKey(FunctionLogoParamVo.ACCCHARGEQ) &&
                        CollectionUtils.isNotEmpty(deviceHistoryMap.get(FunctionLogoParamVo.ACCCHARGEQ))) {
                    sumChargeList.addAll(deviceHistoryMap.get(FunctionLogoParamVo.ACCCHARGEQ));
                }
                if (!deviceHistoryMap.isEmpty() && deviceHistoryMap.containsKey(FunctionLogoParamVo.ACCDISCHARGEQ) &&
                        CollectionUtils.isNotEmpty(deviceHistoryMap.get(FunctionLogoParamVo.ACCDISCHARGEQ))) {
                    sumDischargeList.addAll(deviceHistoryMap.get(FunctionLogoParamVo.ACCDISCHARGEQ));
                }
            });
            //储能充电量
            countData.setStorageChargeQt(getToDouble(sumChargeList.stream()
                    .filter(n -> StringUtil.isNotEmpty(n.getLastDataValue()) && StringUtil.isNotEmpty(n.getFirstDataValue()))
                    .mapToDouble(n -> Double.parseDouble(String.valueOf(n.getLastDataValue())) - Double.parseDouble(String.valueOf(n.getFirstDataValue())))
                    .sum()));
            //储能放电量
            countData.setStorageDischargeQt(getToDouble(sumDischargeList.stream()
                    .filter(n -> StringUtil.isNotEmpty(n.getLastDataValue()) && StringUtil.isNotEmpty(n.getFirstDataValue()))
                    .mapToDouble(n -> Double.parseDouble(String.valueOf(n.getLastDataValue())) - Double.parseDouble(String.valueOf(n.getFirstDataValue())))
                    .sum()));
            //计算系统充放电循环效率(储能放电量 / 储能充电量 * 100)
            if (countData.getStorageChargeQt() > 0.0) {
                countData.setSystemEfficiency(getToDouble(countData.getStorageDischargeQt() / countData.getStorageChargeQt() * 100));
            }
        }
        return countData;
    }

    private StorageAssetCountDto.CountData getStorageRealCountData(List<String> batteryIdList) {
        StorageAssetCountDto.CountData countData = new StorageAssetCountDto.CountData();

        //根据多个设备id，和多个功能点标识查询设备功能点实时数据
        String funationLogos = String.join(",", FunctionLogoParamVo.ACCCHARGEQ, FunctionLogoParamVo.ACCDISCHARGEQ);
        ResponseResult<Map<String, Map<String, RealDataModel>>> realDataMapResult = deviceService.getDeviceFunctionsRealDataByIds(new HashSet<>(batteryIdList),
                funationLogos, 1);
        //获取设备功能点实时数据
        if (realDataMapResult.isSuccess() && !realDataMapResult.getData().isEmpty()) {

            List<PvSiteMonitorDto.FunctionRalData> realDataList = realDataMapResult.getData().entrySet().stream()
                    .flatMap(entry -> entry.getValue().entrySet().stream()
                            .map(subEntry -> {
                                PvSiteMonitorDto.FunctionRalData functionRalData = new PvSiteMonitorDto.FunctionRalData();
                                functionRalData.setFunctionLogo(subEntry.getKey());
                                functionRalData.setRealData(subEntry.getValue().getDataValue());
                                return functionRalData;
                            }))
                    .collect(Collectors.toList());

            //根据功能点标识分组
            Map<String, List<PvSiteMonitorDto.FunctionRalData>> functionRalDataMap = realDataList.stream().collect(Collectors.groupingBy(PvSiteMonitorDto.FunctionRalData::getFunctionLogo));
            //累计储能充电量
            if (functionRalDataMap.containsKey(FunctionLogoParamVo.ACCCHARGEQ) && CollectionUtils.isNotEmpty(functionRalDataMap.get(FunctionLogoParamVo.ACCCHARGEQ))) {
                countData.setStorageChargeQt(getToDouble(functionRalDataMap.get(FunctionLogoParamVo.ACCCHARGEQ).
                        stream().filter(p -> StringUtil.isNotEmpty(p.getRealData())).mapToDouble(p -> Double.parseDouble(String.valueOf(p.getRealData()))).sum()));
            }
            //累计储能放电量
            if (functionRalDataMap.containsKey(FunctionLogoParamVo.ACCDISCHARGEQ) && CollectionUtils.isNotEmpty(functionRalDataMap.get(FunctionLogoParamVo.ACCDISCHARGEQ))) {
                countData.setStorageDischargeQt(getToDouble(functionRalDataMap.get(FunctionLogoParamVo.ACCDISCHARGEQ).
                        stream().filter(p -> StringUtil.isNotEmpty(p.getRealData())).mapToDouble(p -> Double.parseDouble(String.valueOf(p.getRealData()))).sum()));
            }
        }
        //计算系统充放电循环效率(储能放电量 / 储能充电量 * 100)
        if (countData.getStorageChargeQt() > 0.0) {
            countData.setSystemEfficiency(getToDouble(countData.getStorageDischargeQt() / countData.getStorageChargeQt() * 100));
        }
        return countData;
    }

    /**
     * 查询能源近十二个月分析柱状图计算节点数据
     *
     * @param userId      当前登录用户id
     * @param siteId      站点id（不传默认查询全部站点）
     * @param energyType  1-光伏 2-储能
     * @param varCodeList 多个系统变量编码
     * @return
     */
    public ResponseResult<Map<String, Object>> findEnergyCurveData(String userId, String siteId, String energyType, List<String> varCodeList) {
        Map<String, Object> resultMap = Maps.newHashMap();

        //根据能源类型判断取哪两个数据
        String varCode1 = null;
        String varCode2 = null;
        if ("1".equals(energyType)) { //光伏
            varCode1 = SystemVariableEnum.ONGRIDENERGY.getCode();
            varCode2 = SystemVariableEnum.CONSUMEENERGY.getCode();
        } else if ("2".equals(energyType)) { //储能
            varCode1 = SystemVariableEnum.CHARGINGENERGY.getCode();
            varCode2 = SystemVariableEnum.DISCHARGINGENERGY.getCode();
        }
        //存储数据1
        List<Double> dataList1 = Lists.newArrayList();
        //存储数据2
        List<Double> dataList2 = Lists.newArrayList();
        //获取近十二个月的开始时间
        String monthStartTime = getDateByType(getStartTimeByQueryType(0), 11, 5, 2);
        //当前时间
        String dayEndTime = localDateTimeToStr(LocalDateTime.now());
        //获取近十二个月的日期列表 yyyy-MM
        List<String> dayXAxisList = getLocalDateTimeBetweenAuf(strToLocalDateTime(monthStartTime), strToLocalDateTime(dayEndTime), "1n").stream().map(d -> localDateTimeToStr(d).substring(0, 7)).collect(Collectors.toList());

        //多个站点id
        List<String> siteIdList = Lists.newArrayList();
        //如果站点id为空，则查询当前用户有权限的站点
        if (StringUtil.isEmpty(siteId)) {
            ResponseResult<List<OrganEmpowerListDto>> allOrganEmpowerByTenantId = systemService.findAllOrganEmpowerByUserId(userId);
            if (allOrganEmpowerByTenantId.isSuccess() && CollectionUtils.isNotEmpty(allOrganEmpowerByTenantId.getData())) {
                List<OrganEmpowerListDto> organEmpowerListDtos = allOrganEmpowerByTenantId.getData();
                siteIdList = organEmpowerListDtos.stream().map(OrganEmpowerListDto::getSiteId).filter(StringUtil::isNotEmpty).distinct().collect(Collectors.toList());
            } else {
                siteIdList.add(siteId);
            }
        }
        if (CollectionUtils.isNotEmpty(siteIdList)) {
            // 根据多个站点ID查询站点信息
            ResponseResult<Map<String, SiteInfoDto>> siteBasicInfoDtoResult = deviceService.findSiteBasicInfoByIds(siteIdList);
            if (siteBasicInfoDtoResult.isSuccess() && !siteBasicInfoDtoResult.getData().isEmpty()) {
                //过滤出相应能源站点数据
                List<SiteInfoDto> siteInfoDtoList = Lists.newArrayList(siteBasicInfoDtoResult.getData().values()).
                        stream().filter(s -> energyType.equals(s.getScenarioTypes())).collect(Collectors.toList());

                if (CollectionUtils.isNotEmpty(siteInfoDtoList)) {
                    VarNodeValueVo varNodeValueVo = new VarNodeValueVo();
                    varNodeValueVo.setVarCodeList(varCodeList);
                    varNodeValueVo.setDeviceIdList(siteIdList);
                    varNodeValueVo.setStartTime(monthStartTime);
                    varNodeValueVo.setEndTime(dayEndTime);
                    ResponseResult<Map<String, Map<String, List<NodeHistoryDataDto>>>> siteVarNodeValueByIds = crontabService.findSiteVarNodeValueByIds(varNodeValueVo);
                    if (siteVarNodeValueByIds.isSuccess() && !siteVarNodeValueByIds.getData().isEmpty()) {
                        Map<String, Map<String, List<NodeHistoryDataDto>>> nodeValueByIdsData = siteVarNodeValueByIds.getData();
                        //月份分组数据1
                        Map<String, List<NodeHistoryDataDto>> monthGroupDataMap1 = Maps.newHashMap();
                        //月份分组数据2
                        Map<String, List<NodeHistoryDataDto>> monthGroupDataMap2 = Maps.newHashMap();
                        //获取数据1
                        if (nodeValueByIdsData.containsKey(varCode1)) {
                            Map<String, List<NodeHistoryDataDto>> realPowerDataMap = nodeValueByIdsData.get(varCode1);
                            List<NodeHistoryDataDto> realPowerDataList = realPowerDataMap.values().stream().flatMap(Collection::stream).collect(Collectors.toList());
                            //根据时间分组
                            monthGroupDataMap1 = realPowerDataList.stream().collect(Collectors.groupingBy(n -> n.getTs().substring(0, 7)));
                        }
                        //获取数据2
                        if (nodeValueByIdsData.containsKey(varCode2)) {
                            Map<String, List<NodeHistoryDataDto>> realPowerDataMap = nodeValueByIdsData.get(varCode2);
                            List<NodeHistoryDataDto> realPowerDataList = realPowerDataMap.values().stream().flatMap(Collection::stream).collect(Collectors.toList());
                            //根据时间分组
                            monthGroupDataMap2 = realPowerDataList.stream().collect(Collectors.groupingBy(n -> n.getTs().substring(0, 7)));
                        }
                        //循环时间轴获取数据
                        Map<String, List<NodeHistoryDataDto>> finalMonthGroupDataMap1 = monthGroupDataMap1;
                        Map<String, List<NodeHistoryDataDto>> finalMonthGroupDataMap2 = monthGroupDataMap2;
                        dayXAxisList.forEach(dateTime -> {
                            Double data1 = null;
                            Double data2 = null;
                            //获取数据1
                            if (!finalMonthGroupDataMap1.isEmpty() && finalMonthGroupDataMap1.containsKey(dateTime)) {
                                data1 = finalMonthGroupDataMap1.get(dateTime).stream().filter(n -> StringUtil.isNotEmpty(n.getResultValue())).mapToDouble(NodeHistoryDataDto::getResultValue).sum();
                            }
                            //获取数据2
                            if (!finalMonthGroupDataMap2.isEmpty() && finalMonthGroupDataMap2.containsKey(dateTime)) {
                                data2 = finalMonthGroupDataMap2.get(dateTime).stream().filter(n -> StringUtil.isNotEmpty(n.getResultValue())).mapToDouble(NodeHistoryDataDto::getResultValue).sum();
                            }
                            dataList1.add(data1 != null ? getToDouble(data1) : data1);
                            dataList2.add(data2 != null ? getToDouble(data2) : data2);
                        });
                    }
                }
            }
        }
        resultMap.put(varCode1, dataList1);
        if (StringUtil.isNotEmpty(varCode2)) {
            resultMap.put(varCode2, dataList2);
        }
        resultMap.put("xAxisList", dayXAxisList);
        return ResponseResult.ok(resultMap);
    }


    /**
     * 查询充放电资产统计数据
     * @param siteIds 多个站点id
     * @return
     */
    @Override
    public ResponseResult<ChargeAssetCountDto> findChargeAssetCountData(String siteIds) {
        ChargeAssetCountDto chargeAssetCountDto = new ChargeAssetCountDto();
        if (StringUtil.isEmpty(siteIds)) {
            return ResponseResult.error(ResponseResult.PARAM_ERROR);
        }
        //多个站点id
        List<String> siteIdList = JSON.parseArray(siteIds, String.class);

        // 根据多个站点ID查询站点信息
        ResponseResult<List<SiteInfoDto>> siteInfoListByIds = deviceService.findSiteInfoListByIds(siteIdList);
        if (siteInfoListByIds.isSuccess() && CollectionUtils.isNotEmpty(siteInfoListByIds.getData())) {
            //过滤出充放电站点数据
            List<SiteInfoDto> siteInfoDtoList = siteInfoListByIds.getData().
                    stream().filter(s -> StringUtil.isNotEmpty(s.getScenarioTypes()) && s.getScenarioTypes().contains("3")).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(siteInfoDtoList)) {
                chargeAssetCountDto.setSiteNum(siteInfoDtoList.size());

                // 获取站点下充电桩设备数量
                ResponseResult<Map<String, List<DeviceBasicInfoDto>>> deviceBasicInfoBySiteIds = deviceService.findDeviceBasicInfoBySiteIds(siteIdList, 1);
                if (deviceBasicInfoBySiteIds.isSuccess() && !deviceBasicInfoBySiteIds.getData().isEmpty()) {
                    List<DeviceBasicInfoDto> deviceBasicInfoDtoList = Lists.newArrayList();
                    deviceBasicInfoBySiteIds.getData().values().forEach(deviceBasicInfoDtoList::addAll);

                    //获取设备额定功率
                    chargeAssetCountDto.setCapacity(deviceRatedPower(deviceBasicInfoDtoList));

                    //电桩设备数量
                    chargeAssetCountDto.setPileNum(deviceBasicInfoDtoList.size());

                    //根据多个电桩id获取充电枪信息
                    ResponseResult<Map<String, List<DeviceGunInfoDto>>> deviceGunInfoByDeviceIds = deviceService.findDeviceGunInfoByDeviceIds(deviceBasicInfoDtoList.stream().map(DeviceBasicInfoDto::getId).collect(Collectors.toList()));
                    if (deviceGunInfoByDeviceIds.isSuccess() && !deviceGunInfoByDeviceIds.getData().isEmpty()) {
                        //电枪数量
                        chargeAssetCountDto.setGunNum(deviceGunInfoByDeviceIds.getData().values().stream().mapToInt(List::size).sum());
                    }

                    //根据多个电桩编码查询电桩实时数据
                    List<PileRealModel> pileRealModelList = getPileRealModelList(deviceBasicInfoDtoList.stream().map(DeviceBasicInfoDto::getDeviceNumber).collect(Collectors.toList()));
                    if (CollectionUtils.isNotEmpty(pileRealModelList)) {
                        //获取总实时充电功率
                        chargeAssetCountDto.setChargePower(getToDouble(Optional.of(pileRealModelList.stream().filter(p -> StringUtil.isNotEmpty(p.getRecChargePower())).mapToDouble(PileRealModel::getRecChargePower).sum()).orElse(0.0)));
                        //获取总实时放电功率
                        chargeAssetCountDto.setV2gPower(getToDouble(Optional.of(pileRealModelList.stream().filter(p -> StringUtil.isNotEmpty(p.getDisChargePower())).mapToDouble(PileRealModel::getDisChargePower).sum()).orElse(0.0)));
                    }
                }
                //查询站点下所有订单数据
                List<OrderRecordEntity> orderRecordEntityList = orderRecordDao.findAllBySiteIdInAndEndTimeIsNotNullAndAbnormalCodeIsNull(siteIdList);
                if (CollectionUtils.isNotEmpty(orderRecordEntityList)) {
                    //查询累计统计数据
                    String startDateTime = Collections.min(orderRecordEntityList.stream().map(OrderRecordEntity::getEndTime).collect(Collectors.toList()));
                    String endDateTime = Collections.max(orderRecordEntityList.stream().map(OrderRecordEntity::getEndTime).collect(Collectors.toList()));
                    //查询站点下所有统计数据
                    List<SiteCountRecordEntity> siteCountRecordEntityList = siteCountRecordDao.findAllBySiteIdInAndCountDateBetween(siteIdList, strToLocalDate(startDateTime.substring(0, 10)),
                            strToLocalDate(endDateTime.substring(0, 10)));
                    chargeAssetCountDto.setSumCountData(getChargeCountData(orderRecordEntityList, null, null, siteCountRecordEntityList, siteIdList, 4, null));

                    //获取本月开始时间
                    String monthStartTime = getStartTimeByQueryType(4);
                    //获取本月结束时间
                    String monthEndTime = getEndTimeByQueryType(4);
                    //查询本月统计数据
                    chargeAssetCountDto.setMonthCountData(getChargeCountData(orderRecordEntityList, monthStartTime, monthEndTime, siteCountRecordEntityList, siteIdList, 3, null));

                    //获取昨日开始时间
                    String lastDayStartTime = getStartTimeByQueryType(1);
                    //获取昨日结束时间
                    String lastDayEndTime = getEndTimeByQueryType(1);
                    //查询昨日统计数据
                    chargeAssetCountDto.setLastDayCountData(getChargeCountData(orderRecordEntityList, lastDayStartTime, lastDayEndTime, siteCountRecordEntityList, siteIdList, 2, null));

                    //获取今日开始时间
                    String dayStartTime = getStartTimeByQueryType(0);
                    //获取今日结束时间
                    String dayEndTime = getEndTimeByQueryType(0);
                    chargeAssetCountDto.setDayCountData(getChargeCountData(orderRecordEntityList, dayStartTime, dayEndTime, siteCountRecordEntityList, siteIdList, 1, chargeAssetCountDto.getGunNum()));
                }
            }
        }
        return ResponseResult.ok(chargeAssetCountDto);
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

    @Override
    public ResponseResult<Map<String, Object>> findChargePowerCurveData(String userId, String siteId) {
        Map<String, Object> resultMap = Maps.newHashMap();

        //存储充电功率数据
        List<Double> chargePowerDataList = Lists.newArrayList();
        //存储放电功率数据
        List<Double> dischargePowerDataList = Lists.newArrayList();
        //获取今日开始时间
        String dayStartTime = getStartTimeByQueryType(0);
        //获取今日结束时间
        String dayEndTime = getEndTimeByQueryType(0);

        //查询今日每五分钟一个点的时间轴
        List<String> dayXAxisList = getLocalDateTimeBetweenAuf(strToLocalDateTime(dayStartTime), strToLocalDateTime(dayEndTime), "5m").stream().map(DateUtil::localDateTimeToStr).collect(Collectors.toList());

        //多个站点id
        List<String> siteIdList = Lists.newArrayList();
        //如果站点id为空，则查询当前用户有权限的储能站点
        if (StringUtil.isEmpty(siteId)) {
            ResponseResult<List<OrganEmpowerListDto>> allOrganEmpowerByTenantId = systemService.findAllOrganEmpowerByUserId(userId);
            if (allOrganEmpowerByTenantId.isSuccess() && CollectionUtils.isNotEmpty(allOrganEmpowerByTenantId.getData())) {
                List<OrganEmpowerListDto> organEmpowerListDtos = allOrganEmpowerByTenantId.getData();
                siteIdList = organEmpowerListDtos.stream().map(OrganEmpowerListDto::getSiteId).filter(StringUtil::isNotEmpty).distinct().collect(Collectors.toList());
            } else {
                siteIdList.add(siteId);
            }
        }
        if (CollectionUtils.isNotEmpty(siteIdList)) {
            // 根据多个站点ID查询站点信息
            ResponseResult<Map<String, SiteInfoDto>> siteBasicInfoDtoResult = deviceService.findSiteBasicInfoByIds(siteIdList);
            if (siteBasicInfoDtoResult.isSuccess() && !siteBasicInfoDtoResult.getData().isEmpty()) {
                //过滤出充放电站点数据
                List<SiteInfoDto> siteInfoDtoList = Lists.newArrayList(siteBasicInfoDtoResult.getData().values()).
                        stream().filter(s -> StringUtil.isNotEmpty(s.getScenarioTypes()) && s.getScenarioTypes().contains("3")).collect(Collectors.toList());

                if (CollectionUtils.isNotEmpty(siteInfoDtoList)) {
                    VarNodeValueVo varNodeValueVo = new VarNodeValueVo();
                    varNodeValueVo.setVarCodeList(Arrays.asList(SystemVariableEnum.PILE_CHARGINGPOWER.getCode(), SystemVariableEnum.PILE_DISCHARGINGPOWER.getCode()));
                    varNodeValueVo.setDeviceIdList(siteIdList);
                    varNodeValueVo.setStartTime(dayStartTime);
                    varNodeValueVo.setEndTime(dayEndTime);
                    varNodeValueVo.setTimeInterval("5m");
                    ResponseResult<Map<String, Map<String, List<NodeHistoryDataDto>>>> siteVarNodeValueByIds = crontabService.findSiteVarNodeValueByIds(varNodeValueVo);
                    if (siteVarNodeValueByIds.isSuccess() && !siteVarNodeValueByIds.getData().isEmpty()) {
                        Map<String, Map<String, List<NodeHistoryDataDto>>> nodeValueByIdsData = siteVarNodeValueByIds.getData();
                        //充电功率数据
                        Map<String, List<NodeHistoryDataDto>> chargePowerDataMap = Maps.newHashMap();
                        //放电功率数据
                        Map<String, List<NodeHistoryDataDto>> dischargePowerDataMap = Maps.newHashMap();
                        //获取充电功率数据
                        if (nodeValueByIdsData.containsKey(SystemVariableEnum.PILE_CHARGINGPOWER.getCode())) {
                            Map<String, List<NodeHistoryDataDto>> realPowerDataMap = nodeValueByIdsData.get(SystemVariableEnum.PILE_CHARGINGPOWER.getCode());
                            List<NodeHistoryDataDto> realPowerDataList = realPowerDataMap.values().stream().flatMap(Collection::stream).collect(Collectors.toList());
                            //根据时间分组
                            chargePowerDataMap = realPowerDataList.stream().collect(Collectors.groupingBy(NodeHistoryDataDto::getTs));
                        }
                        //获取放电功率数据
                        if (nodeValueByIdsData.containsKey(SystemVariableEnum.PILE_DISCHARGINGPOWER.getCode())) {
                            Map<String, List<NodeHistoryDataDto>> realPowerDataMap = nodeValueByIdsData.get(SystemVariableEnum.PILE_DISCHARGINGPOWER.getCode());
                            List<NodeHistoryDataDto> realPowerDataList = realPowerDataMap.values().stream().flatMap(Collection::stream).collect(Collectors.toList());
                            //根据时间分组
                            dischargePowerDataMap = realPowerDataList.stream().collect(Collectors.groupingBy(NodeHistoryDataDto::getTs));
                        }
                        //循环时间轴获取数据
                        Map<String, List<NodeHistoryDataDto>> finalChargePowerDataMap = chargePowerDataMap;
                        Map<String, List<NodeHistoryDataDto>> finalDischargePowerDataMap = dischargePowerDataMap;
                        dayXAxisList.forEach(dateTime -> {
                            Double chargePower = null;
                            Double dischargePower = null;
                            //获取充电功率
                            if (!finalChargePowerDataMap.isEmpty() && finalChargePowerDataMap.containsKey(dateTime)) {
                                chargePower = finalChargePowerDataMap.get(dateTime).stream().filter(n -> StringUtil.isNotEmpty(n.getResultValue())).mapToDouble(NodeHistoryDataDto::getResultValue).sum();
                            }
                            //获取放电功率
                            if (!finalDischargePowerDataMap.isEmpty() && finalDischargePowerDataMap.containsKey(dateTime)) {
                                dischargePower = finalDischargePowerDataMap.get(dateTime).stream().filter(n -> StringUtil.isNotEmpty(n.getResultValue())).mapToDouble(NodeHistoryDataDto::getResultValue).sum();
                            }
                            chargePowerDataList.add(chargePower != null ? getToDouble(chargePower) : chargePower);
                            dischargePowerDataList.add(dischargePower != null ? getToDouble(dischargePower) : dischargePower);
                        });
                    }
                }
            }
        }
        resultMap.put("chargePowerDataList", chargePowerDataList);
        resultMap.put("dischargePowerDataList", dischargePowerDataList);
        resultMap.put("xAxisList", dayXAxisList.stream().map(d -> d.substring(11, 16)).collect(Collectors.toList()));
        return ResponseResult.ok(resultMap);
    }

    /**
     * 查询站点数量
     *
     * @param userId       当前登录用户id
     * @param scenarioType 场景类型 1-光伏 2-储能 3-充电桩（不传默认全部）
     * @param areaType     区域类型 1-省级 2-市级（不传默认全部）
     * @param areaName     区域名称
     * @return
     */
    @Override
    public ResponseResult<List<Map<String, Object>>> querySiteNum(String userId, Integer scenarioType, Integer areaType, String areaName) {
        List<Map<String, Object>> resultList = Lists.newArrayList();
        //根据当前登录用户id，查询相关配置资产授权站点数据
        ResponseResult<List<OrganEmpowerListDto>> allOrganEmpowerByTenantId = systemService.findAllOrganEmpowerByUserId(userId);
        if (allOrganEmpowerByTenantId.isSuccess() && CollectionUtils.isNotEmpty(allOrganEmpowerByTenantId.getData())) {
            List<OrganEmpowerListDto> organEmpowerListDtos = allOrganEmpowerByTenantId.getData();
            //根据多个站点id查询站点信息
            Map<String, SiteInfoDto> siteInfoDtoMap = deviceService.findSiteInfoListByIds(organEmpowerListDtos.stream().
                    map(OrganEmpowerListDto::getSiteId).filter(StringUtil::isNotEmpty).distinct().collect(Collectors.toList())).getData().
                    stream().collect(Collectors.toMap(SiteInfoDto::getId, Function.identity()));
            if (MapUtils.isNotEmpty(siteInfoDtoMap)) {
                List<SiteInfoDto> siteInfoDtoList = Lists.newArrayList(siteInfoDtoMap.values());
                //根据能源类型查询
                if (StringUtil.isNotEmpty(scenarioType)) {
                    siteInfoDtoList = siteInfoDtoList.stream().filter(s -> StringUtil.isNotEmpty(s.getScenarioTypes()) && s.getScenarioTypes().contains(String.valueOf(scenarioType))).collect(Collectors.toList());
                }
                //判断是否根据区域类型查询
                if (StringUtil.isNotEmpty(areaType) && StringUtil.isNotEmpty(areaName)) {
                    siteInfoDtoList = siteInfoDtoList.stream()
                            .filter(siteInfoDto -> {
                                Map<String, Object> readwriteMap = Maps.newHashMap();
                                String siteReadwriteObject = siteInfoDto.getSiteReadwriteObject();
                                if (StringUtil.isNotEmpty(siteReadwriteObject)) {
                                    Map<String, Object> parseObjectMap = JSON.parseObject(siteReadwriteObject, new TypeReference<Map<String, Object>>() {
                                    });
                                    if (parseObjectMap.containsKey(SiteFieldParamVo.LOCATION)) {
                                        readwriteMap = JSON.parseObject(JSON.toJSONString(parseObjectMap.get(SiteFieldParamVo.LOCATION)), new TypeReference<Map<String, Object>>() {
                                        });
                                    }
                                }
                                return matchesAreaType(readwriteMap, areaType, areaName);
                            })
                            .collect(Collectors.toList());
                }
                if (CollectionUtils.isNotEmpty(siteInfoDtoList)) {
                    List<AssetSiteListDto> assetSiteListDtoList = siteInfoDtoList.stream().map(siteInfoDto -> {
                        AssetSiteListDto assetSiteListDto = new AssetSiteListDto();
                        BeanUtils.copyProperties(siteInfoDto, assetSiteListDto);
                        //获取站点扩展属性信息
                        String siteReadwriteObject = siteInfoDto.getSiteReadwriteObject();
                        if (StringUtil.isNotEmpty(siteReadwriteObject)) {
                            Map<String, Object> readwriteMap = JSON.parseObject(siteReadwriteObject, new TypeReference<Map<String, Object>>() {
                            });
                            //地址对象
                            if (readwriteMap.containsKey(SiteFieldParamVo.LOCATION)) {
                                Map<String, Object> localtionReadwriteMap = JSON.parseObject(JSON.toJSONString(readwriteMap.get(SiteFieldParamVo.LOCATION)), new TypeReference<Map<String, Object>>() {
                                });
                                //经度
                                if (!localtionReadwriteMap.isEmpty() && localtionReadwriteMap.containsKey(SiteFieldParamVo.LONGITUDE) && StringUtil.isNotEmpty(localtionReadwriteMap.get(SiteFieldParamVo.LONGITUDE))) {
                                    assetSiteListDto.setLongitude(localtionReadwriteMap.get(SiteFieldParamVo.LONGITUDE).toString());
                                }
                                //纬度
                                if (!localtionReadwriteMap.isEmpty() && localtionReadwriteMap.containsKey(SiteFieldParamVo.LATITUDE) && StringUtil.isNotEmpty(localtionReadwriteMap.get(SiteFieldParamVo.LATITUDE))) {
                                    assetSiteListDto.setLatitude(localtionReadwriteMap.get(SiteFieldParamVo.LATITUDE).toString());
                                }
                                //省份
                                if (!localtionReadwriteMap.isEmpty() && localtionReadwriteMap.containsKey(SiteFieldParamVo.PROVINCE) && StringUtil.isNotEmpty(localtionReadwriteMap.get(SiteFieldParamVo.PROVINCE))) {
                                    assetSiteListDto.setProvince(localtionReadwriteMap.get(SiteFieldParamVo.PROVINCE).toString());
                                }
                                //市级
                                if (!localtionReadwriteMap.isEmpty() && localtionReadwriteMap.containsKey(SiteFieldParamVo.CITY) && StringUtil.isNotEmpty(localtionReadwriteMap.get(SiteFieldParamVo.CITY))) {
                                    assetSiteListDto.setCity(localtionReadwriteMap.get(SiteFieldParamVo.CITY).toString());
                                }
                                //所在区县
                                if (!localtionReadwriteMap.isEmpty() && localtionReadwriteMap.containsKey(SiteFieldParamVo.COUNTY) && StringUtil.isNotEmpty(localtionReadwriteMap.get(SiteFieldParamVo.COUNTY))) {
                                    assetSiteListDto.setCounty(localtionReadwriteMap.get(SiteFieldParamVo.COUNTY).toString());
                                }
                            }
                        }
                        return assetSiteListDto;
                    }).collect(Collectors.toList());
                    //过滤出省市区数为空的站点数据
                    List<AssetSiteListDto> assetSiteListDtos = assetSiteListDtoList.stream().filter(a -> StringUtil.isNotEmpty(a.getCounty())).collect(Collectors.toList());
                    if (CollectionUtils.isNotEmpty(assetSiteListDtos)) {
                        Map<String, List<AssetSiteListDto>> areaMap = Maps.newHashMap();
                        //如果区域类型为空，则根据省份分组
                        if (StringUtil.isEmpty(areaType)) {
                            areaMap = assetSiteListDtos.stream().collect(Collectors.groupingBy(AssetSiteListDto::getProvince));
                        } else if (StringUtil.isNotEmpty(areaType) && areaType == 1) {//如果是根据省份查询，则按照市级分组
                            areaMap = assetSiteListDtos.stream().collect(Collectors.groupingBy(AssetSiteListDto::getCity));
                        } else if (StringUtil.isNotEmpty(areaType) && areaType == 2) {//如果是市级，则按照区县分组
                            areaMap = assetSiteListDtos.stream().collect(Collectors.groupingBy(AssetSiteListDto::getCounty));
                        }
                        areaMap.forEach((k, v) -> {
                            Map<String, Object> resultMap = Maps.newHashMap();
                            resultMap.put("name", k);
                            resultMap.put("size", v.size());
                            resultMap.put("siteInfoList", v);
                            resultList.add(resultMap);
                        });
                    }
                }
            }
        }
        return ResponseResult.ok(resultList);
    }

    private ChargeAssetCountDto.CountData getChargeCountData(List<OrderRecordEntity> orderRecordEntityList, String startTime, String endTime,
                                                             List<SiteCountRecordEntity> siteCountRecordEntityList, List<String> siteIdList,
                                                             int isDay, Integer gunNum) {

        ChargeAssetCountDto.CountData countData = new ChargeAssetCountDto.CountData();
        if (StringUtil.isNotEmpty(startTime) && StringUtil.isNotEmpty(endTime)) {
            //根据订单结束时间过滤出数据
            LocalDateTime startDateTime = strToLocalDateTime(startTime);
            LocalDateTime endDateTime = strToLocalDateTime(endTime);
            orderRecordEntityList = orderRecordEntityList.stream().filter(s -> strToLocalDateTime(s.getEndTime()).isAfter(startDateTime) && strToLocalDateTime(s.getEndTime()).isBefore(endDateTime)).collect(Collectors.toList());
            siteCountRecordEntityList = siteCountRecordEntityList.stream().filter(s -> !s.getCountDate().isBefore(startDateTime.toLocalDate()) && !s.getCountDate().isAfter(endDateTime.toLocalDate())).collect(Collectors.toList());
        }
        if (CollectionUtils.isNotEmpty(orderRecordEntityList)) {
            countData.setV2gQt(getToDouble(orderRecordEntityList.stream().filter(o -> (o.getRunMode() == 1 || o.getRunMode() == 2) && StringUtil.isNotEmpty(o.getTotalQt())).mapToDouble(OrderRecordEntity::getTotalQt).sum()));
            countData.setChargeQt(getToDouble(orderRecordEntityList.stream().filter(o -> o.getRunMode() == 0 && StringUtil.isNotEmpty(o.getTotalQt())).mapToDouble(OrderRecordEntity::getTotalQt).sum()));

            //计算枪均电量(判断是否是今日，如果是则拿当前充电电量 / 当前实时电枪数量计算)
            if (StringUtil.isNotEmpty(isDay) && isDay == 1 && StringUtil.isNotEmpty(gunNum) && gunNum > 0) {
                countData.setGunAvgQt(getToDouble(countData.getChargeQt() / gunNum));
            } else {
                countData.setGunAvgQt(gunAvgQt(orderRecordEntityList, siteCountRecordEntityList));
            }
        }
        //一次充电成功率(%) 筛选日期内创建的订单中，正常订单数量/总订单数量*100%
        Integer normalNum = orderRecordMapper.countChargeOrderNormalNum(siteIdList, startTime, endTime);
        Integer totalNum = orderRecordMapper.countChargeOrderTotalNum(siteIdList, startTime, endTime);
        if (totalNum != 0) {
            countData.setChargeSuccessRatio(DoubleUtil.getToDouble((double) normalNum / totalNum * 100));
        }
        return countData;
    }

    public static Double gunAvgQt(List<OrderRecordEntity> orderRecordEntities, List<SiteCountRecordEntity> siteCountRecordEntities) {
        //筛选出充电订单
        List<OrderRecordEntity> chargeOrderList = orderRecordEntities.stream().filter(o -> o.getRunMode() == 0).collect(Collectors.toList());

        if (CollectionUtils.isEmpty(chargeOrderList)) {
            return 0.0;
        }
        //过滤出站点统计记录数据中有数据的日期的订单(因为站点统计记录中有可能缺少某天的数据，缺少的日期的订单不参与计算)
        List<OrderRecordEntity> recordEntities = chargeOrderList.stream().filter(o -> siteCountRecordEntities.stream()
                        .anyMatch(p -> localDateToStr(p.getCountDate()).equals(o.getEndTime().substring(0, 10))))
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(recordEntities)) {
            return 0.0;
        }
        //充电量
        double totalQt = recordEntities.stream()
                .filter(o -> StringUtil.isNotEmpty(o.getTotalQt()))
                .mapToDouble(OrderRecordEntity::getTotalQt)
                .sum();

        //枪数量
        double gunNum = siteCountRecordEntities.stream()
                .filter(s -> StringUtil.isNotEmpty(s.getTotalGunNum()))
                .mapToInt(SiteCountRecordEntity::getTotalGunNum)
                .sum();
        if (totalQt > 0 && gunNum > 0 && CollectionUtils.isNotEmpty(siteCountRecordEntities)) {
            return getToDouble(totalQt / (gunNum / siteCountRecordEntities.size()));
        }

        return 0.0;
    }

    /**
     * 查询系统设备列表
     *
     * @param siteId
     * @return
     */
    @Override
    public ResponseResult<SystemDeviceListDto> querySystemDeviceList(String siteId) {
        SystemDeviceListDto result = new SystemDeviceListDto();
        //根据站点id查询所有设备信息
        ResponseResult<Map<String, List<DeviceBasicInfoDto>>> deviceBasicInfoBySiteIds = deviceService.findDeviceBasicInfoBySiteIds(Collections.singletonList(siteId), null);
        if (deviceBasicInfoBySiteIds.isSuccess() && !deviceBasicInfoBySiteIds.getData().isEmpty() && deviceBasicInfoBySiteIds.getData().containsKey(siteId)) {
            List<DeviceBasicInfoDto> deviceBasicInfoDtoList = deviceBasicInfoBySiteIds.getData().get(siteId);
            //获取储能PCS设备数据
            List<DeviceBasicInfoDto> pcsDeviceList = deviceBasicInfoDtoList.stream().filter(d -> StringUtil.isNotEmpty(d.getTypeId()) && "23".equals(d.getTypeId())).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(pcsDeviceList)) {
                result.setStorageDeviceList(pcsDeviceList.stream().map(deviceBasicInfoDto -> {
                    SystemDeviceListDto.DeviceData deviceData = new SystemDeviceListDto.DeviceData();
                    BeanUtils.copyProperties(deviceBasicInfoDto, deviceData);
                    return deviceData;
                }).collect(Collectors.toList()));
            }
            //获取光伏逆变器设备数据
            List<DeviceBasicInfoDto> inverterDeviceList = deviceBasicInfoDtoList.stream().filter(d -> StringUtil.isNotEmpty(d.getTypeId()) && "20".equals(d.getTypeId())).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(inverterDeviceList)) {
                result.setPvDeviceList(inverterDeviceList.stream().map(deviceBasicInfoDto -> {
                    SystemDeviceListDto.DeviceData deviceData = new SystemDeviceListDto.DeviceData();
                    BeanUtils.copyProperties(deviceBasicInfoDto, deviceData);
                    return deviceData;
                }).collect(Collectors.toList()));
            }
            //获取充电桩设备数据
            List<DeviceBasicInfoDto> pileDeviceList = deviceBasicInfoDtoList.stream().filter(d -> StringUtil.isNotEmpty(d.getTypeId()) && "28".equals(d.getTypeId()) || "29".equals(d.getTypeId()) || "30".equals(d.getTypeId())).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(pileDeviceList)) {
                result.setPileDeviceList(pileDeviceList.stream().map(deviceBasicInfoDto -> {
                    SystemDeviceListDto.DeviceData deviceData = new SystemDeviceListDto.DeviceData();
                    BeanUtils.copyProperties(deviceBasicInfoDto, deviceData);
                    return deviceData;
                }).collect(Collectors.toList()));
            }
            //获取配电设备数据
            List<DeviceBasicInfoDto> powerDeviceList = deviceBasicInfoDtoList.stream().filter(d -> StringUtil.isNotEmpty(d.getTypeId()) && "38".equals(d.getTypeId())).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(powerDeviceList)) {
                result.setPowerDeviceList(powerDeviceList.stream().map(deviceBasicInfoDto -> {
                    SystemDeviceListDto.DeviceData deviceData = new SystemDeviceListDto.DeviceData();
                    BeanUtils.copyProperties(deviceBasicInfoDto, deviceData);
                    return deviceData;
                }).collect(Collectors.toList()));
            }
        }
        return ResponseResult.ok(result);
    }

    /**
     * 查询储能、光伏、配电设备功能点实时数据
     *
     * @param deviceId      设备id
     * @param functionLogos 多个功能点标识(以逗号分割)
     * @return
     */
    @Override
    public ResponseResult<Map<String, RealDataModel>> queryDeviceFunRealData(String deviceId, String functionLogos) {
        Map<String, RealDataModel> resultMap = Maps.newHashMap();
        ResponseResult<Map<String, Map<String, RealDataModel>>> realDataResult = deviceService.getDeviceFunctionsRealDataByIds(Collections.singleton(deviceId),
                functionLogos, 2);
        if (realDataResult.isSuccess() && !realDataResult.getData().isEmpty() && realDataResult.getData().containsKey(deviceId)) {
            resultMap = realDataResult.getData().get(deviceId);
        }
        return ResponseResult.ok(resultMap);
    }

    /**
     * 查询储能、光伏、配电设备功能点曲线数据数据
     *
     * @param deviceId     设备id
     * @param functionLogo 功能点标识
     * @return
     */
    @Override
    public ResponseResult<Map<String, Object>> queryDeviceFunCurveData(String deviceId, String functionLogo) {
        Map<String, Object> resultMap = Maps.newHashMap();
        //存储数据列表
        List<Object> dataList = Lists.newArrayList();
        //今日开始时间
        String startTime = getStartTimeByQueryType(0);
        //当前时间
        String endTime = localDateTimeToStr(LocalDateTime.now());
        //获取今日每分钟时间轴
        List<String> getDateTimeBetween = getLocalDateTimeBetween(strToLocalDateTime(startTime), strToLocalDateTime(endTime), "1m").stream().map(DateUtil::localDateTimeToStr).collect(Collectors.toList());
        resultMap.put("xAXisList", getDateTimeBetween.stream().map(s -> s.substring(11, 19)).collect(Collectors.toList()));
        DeviceHistoryQueryVo deviceHistoryQueryVo = new DeviceHistoryQueryVo();
        deviceHistoryQueryVo.setDeviceIds(Collections.singleton(deviceId));
        deviceHistoryQueryVo.setFunctionLogos(Collections.singleton(functionLogo));
        deviceHistoryQueryVo.setStartTime(startTime);
        deviceHistoryQueryVo.setEndTime(endTime);
        deviceHistoryQueryVo.setTimeInterval("1m");
        ResponseResult<Map<String, Map<String, List<DeviceHistoryDto>>>> deviceHistoryResult = dataService.findDeviceHistoryValueList(deviceHistoryQueryVo);
        if (deviceHistoryResult.isSuccess() && !deviceHistoryResult.getData().isEmpty() && deviceHistoryResult.getData().containsKey(deviceId)
                && deviceHistoryResult.getData().get(deviceId).containsKey(functionLogo)) {
            List<DeviceHistoryDto> deviceHistoryDtoList = deviceHistoryResult.getData().get(deviceId).get(functionLogo);
            if (CollectionUtils.isNotEmpty(deviceHistoryDtoList)) {
                //根据时间转换成map
                Map<String, DeviceHistoryDto> deviceHistoryMap = deviceHistoryDtoList.stream().collect(Collectors.toMap(DeviceHistoryDto::getDateTime, deviceHistoryDto -> deviceHistoryDto, (k1, k2) -> k1));
                getDateTimeBetween.forEach(dateTime -> {
                    if (deviceHistoryMap.containsKey(dateTime)) {
                        dataList.add(deviceHistoryMap.get(dateTime).getDataValue());
                    }
                });
            }
        }
        resultMap.put("dataList", dataList);
        return ResponseResult.ok(resultMap);
    }

    /**
     * 查询电桩设备功能点曲线数据数据
     *
     * @param deviceId     设备id
     * @param functionLogo 功能点标识
     * @return
     */
    @Override
    public ResponseResult<PileFunCurveDto> queryPileFunCurveData(String deviceId, String functionLogo) {
        PileFunCurveDto pileFunCurveDto = new PileFunCurveDto();
        //存储数据列表
        List<PileFunCurveDto.DataInfo> dataInfoList = Lists.newArrayList();
        //今日开始时间
        String startTime = getStartTimeByQueryType(0);
        //当前时间
        String endTime = localDateTimeToStr(LocalDateTime.now());
        //获取今日每分钟时间轴
        List<String> getDateTimeBetween = getLocalDateTimeBetween(strToLocalDateTime(startTime), strToLocalDateTime(endTime), "1m").stream().map(DateUtil::localDateTimeToStr).collect(Collectors.toList());
        pileFunCurveDto.setXAXisList(getDateTimeBetween.stream().map(s -> s.substring(11, 19)).collect(Collectors.toList()));
        //根据设备id查询关联功能点列表，查询出当前功能点是否是数组类型
        ResponseResult<Map<String, List<ModelFunctionListDto>>> modelFunctionResult = deviceService.findDeviceFunctionListByIds(Collections.singletonList(deviceId));
        if (modelFunctionResult.isSuccess() && !modelFunctionResult.getData().isEmpty() && modelFunctionResult.getData().containsKey(deviceId)) {
            List<ModelFunctionListDto> modelFunctionListDtoList = modelFunctionResult.getData().get(deviceId);
            Map<String, ModelFunctionListDto> modelFunctionListMap = CollectionUtils.isEmpty(modelFunctionListDtoList)
                    ? Collections.emptyMap()
                    : modelFunctionListDtoList.stream()
                    .collect(Collectors.toMap(ModelFunctionListDto::getFunctionLogo, modelFunctionListDto -> modelFunctionListDto, (k1, k2) -> k1));
            if (!modelFunctionListMap.isEmpty() && modelFunctionListMap.containsKey(functionLogo)) {
                ModelFunctionListDto modelFunctionListDto = modelFunctionListMap.get(functionLogo);
                //根据功能点类型判断是否是数组类型，如果是数组类型则查询电桩下所有电枪数据
                if (modelFunctionListDto.getDataType() != 8) {//电桩数据
                    DeviceHistoryQueryVo deviceHistoryQueryVo = new DeviceHistoryQueryVo();
                    deviceHistoryQueryVo.setDeviceIds(Collections.singleton(deviceId));
                    deviceHistoryQueryVo.setFunctionLogos(Collections.singleton(functionLogo));
                    deviceHistoryQueryVo.setStartTime(startTime);
                    deviceHistoryQueryVo.setEndTime(endTime);
                    deviceHistoryQueryVo.setTimeInterval("1m");
                    ResponseResult<Map<String, Map<String, List<DeviceHistoryDto>>>> deviceHistoryResult = dataService.findDeviceHistoryValueList(deviceHistoryQueryVo);
                    if (deviceHistoryResult.isSuccess() && !deviceHistoryResult.getData().isEmpty() && deviceHistoryResult.getData().containsKey(deviceId)
                            && deviceHistoryResult.getData().get(deviceId).containsKey(functionLogo)) {
                        List<DeviceHistoryDto> deviceHistoryDtoList = deviceHistoryResult.getData().get(deviceId).get(functionLogo).stream().filter(d -> StringUtil.isNotEmpty(d.getDateTime())).collect(Collectors.toList());
                        if (CollectionUtils.isNotEmpty(deviceHistoryDtoList)) {
                            PileFunCurveDto.DataInfo dataInfo = new PileFunCurveDto.DataInfo();
                            List<Object> dataList = Lists.newArrayList();
                            //根据时间转换成map
                            Map<String, DeviceHistoryDto> deviceHistoryMap = deviceHistoryDtoList.stream().collect(Collectors.toMap(DeviceHistoryDto::getDateTime, deviceHistoryDto -> deviceHistoryDto, (k1, k2) -> k1));
                            getDateTimeBetween.forEach(dateTime -> {
                                if (deviceHistoryMap.containsKey(dateTime)) {
                                    dataList.add(deviceHistoryMap.get(dateTime).getDataValue());
                                }
                            });
                            dataInfo.setName(modelFunctionListDto.getFunctionName());
                            dataInfo.setDataList(dataList);
                            dataInfoList.add(dataInfo);
                        }
                    }
                } else {//电枪数据
                    //根据电桩设备id，查询电桩下所有电枪信息
                    ResponseResult<Map<String, List<DeviceGunInfoDto>>> deviceGunInfoByDeviceIds = deviceService.findDeviceGunInfoByDeviceIds(Collections.singletonList(deviceId));
                    if (deviceGunInfoByDeviceIds.isSuccess() && !deviceGunInfoByDeviceIds.getData().isEmpty() && deviceGunInfoByDeviceIds.getData().containsKey(deviceId)) {
                        List<DeviceGunInfoDto> deviceGunInfoDtoList = deviceGunInfoByDeviceIds.getData().get(deviceId);
                        Set<String> functionIndexSet = deviceGunInfoDtoList.stream().map(d -> functionLogo + "index" + (Integer.parseInt(d.getGunCode()) - 1)).collect(Collectors.toSet());
                        DeviceIndexQueryVo deviceQueryVo = new DeviceIndexQueryVo();
                        Map<String, Set<String>> deviceFuctionMap = Maps.newHashMap();
                        deviceFuctionMap.put(deviceId, functionIndexSet);
                        deviceQueryVo.setDeviceFuctionMap(deviceFuctionMap);
                        deviceQueryVo.setStartTime(startTime);
                        deviceQueryVo.setEndTime(endTime);
                        deviceQueryVo.setTimeInterval("1m");
                        ResponseResult<Map<String, Map<String, List<DeviceHistoryDto>>>> deviceHistoryIndexValueList = dataService.findDeviceHistoryIndexValueList(deviceQueryVo);
                        //循环电枪信息，获取电枪信息数据
                        deviceGunInfoDtoList.forEach(deviceGunInfoDto -> {
                            PileFunCurveDto.DataInfo dataInfo = new PileFunCurveDto.DataInfo();
                            List<Object> dataList = Lists.newArrayList();
                            dataInfo.setName(deviceGunInfoDto.getGunName());
                            String functionIndex = functionLogo + "index" + (Integer.parseInt(deviceGunInfoDto.getGunCode()) - 1);
                            if (deviceHistoryIndexValueList.isSuccess() && !deviceHistoryIndexValueList.getData().isEmpty() &&
                                    deviceHistoryIndexValueList.getData().containsKey(deviceId) && deviceHistoryIndexValueList.getData().get(deviceId).containsKey(functionIndex)) {
                                List<DeviceHistoryDto> deviceHistoryDtoList = deviceHistoryIndexValueList.getData().get(deviceId).get(functionIndex);
                                //根据时间转换成map
                                Map<String, DeviceHistoryDto> deviceHistoryMap = deviceHistoryDtoList.stream().collect(Collectors.toMap(DeviceHistoryDto::getDateTime, deviceHistoryDto -> deviceHistoryDto, (k1, k2) -> k1));
                                getDateTimeBetween.forEach(dateTime -> {
                                    if (deviceHistoryMap.containsKey(dateTime)) {
                                        dataList.add(deviceHistoryMap.get(dateTime).getDataValue());
                                    }
                                });
                                dataInfo.setDataList(dataList);
                            }
                            dataInfoList.add(dataInfo);
                        });
                    }
                }
            }
        }
        pileFunCurveDto.setDataInfoList(dataInfoList);
        return ResponseResult.ok(pileFunCurveDto);
    }

    /**
     * 查询储能资产总览站点功率曲线数据
     * @param siteIds   多个站点id
     * @param startTime 开始时间(yyyy-MM-dd HH:mm:ss)
     * @param endTime   结束时间(yyyy-MM-dd HH:mm:ss)
     * @return
     */
    @Override
    public ResponseResult<PileFunCurveDto> findStorageOverviewPowerCurve(String siteIds, String startTime, String endTime) {
        PileFunCurveDto result = new PileFunCurveDto();
        if (StringUtil.isEmpty(siteIds) || StringUtil.isEmpty(startTime) || StringUtil.isEmpty(endTime)) {
            return ResponseResult.error(ResponseResult.PARAM_ERROR);
        }
        List<String> siteIdList = JSON.parseArray(siteIds, String.class);
        //存储数据列表
        List<PileFunCurveDto.DataInfo> dataInfoList = org.apache.commons.compress.utils.Lists.newArrayList();
        //获取今日每分钟时间轴
        List<String> getDateTimeBetween = getLocalDateTimeBetween(strToLocalDateTime(startTime), strToLocalDateTime(endTime), "5m").stream().map(DateUtil::localDateTimeToStr).collect(Collectors.toList());
        result.setXAXisList(getDateTimeBetween.stream().map(e -> e.substring(11, 19)).collect(Collectors.toList()));

        //根据站点id，查询站点下所有设备列表
        ResponseResult<Map<String, List<DeviceBasicInfoDto>>> deviceBasicInfoBySiteIds = deviceService.findDeviceBasicInfoBySiteIds(siteIdList, null);
        if (deviceBasicInfoBySiteIds.isSuccess() && !deviceBasicInfoBySiteIds.getData().isEmpty()) {
            List<DeviceBasicInfoDto> deviceBasicInfoDtos = org.apache.commons.compress.utils.Lists.newArrayList();
            //过滤出pcs设备
            deviceBasicInfoBySiteIds.getData().forEach((k,v) -> deviceBasicInfoDtos.addAll(v.stream().filter(d ->
                    StringUtil.isNotEmpty(d.getTypeId()) && "23".equals(d.getTypeId())).collect(Collectors.toList())));
            if (CollectionUtils.isNotEmpty(deviceBasicInfoDtos)) {

                DeviceHistoryQueryVo deviceHistoryQueryVo = new DeviceHistoryQueryVo();
                deviceHistoryQueryVo.setDeviceIds(deviceBasicInfoDtos.stream().map(DeviceBasicInfoDto::getId).collect(Collectors.toSet()));
                deviceHistoryQueryVo.setFunctionLogos(Collections.singleton(FunctionLogoParamVo.PCS_ACTIVE_POWER));
                deviceHistoryQueryVo.setStartTime(startTime);
                deviceHistoryQueryVo.setEndTime(endTime);
                deviceHistoryQueryVo.setTimeInterval("5m");
                //查询逆变器功能点历史数据
                ResponseResult<Map<String, Map<String, List<DeviceHistoryDto>>>> deviceHistoryResult = dataService.findDeviceHistoryValueList(deviceHistoryQueryVo);
                if (deviceHistoryResult.isSuccess() && !deviceHistoryResult.getData().isEmpty()) {
                    // 存储所有历史数据
                    List<DeviceHistoryDto> deviceHistoryDtoList = deviceHistoryResult.getData().values().stream()
                            .filter(deviceHistoryMap -> !deviceHistoryMap.isEmpty())
                            .flatMap(deviceHistoryMap -> deviceHistoryMap.values().stream())
                            .flatMap(List::stream)
                            .collect(Collectors.toList());

                    if (CollectionUtils.isNotEmpty(deviceHistoryDtoList)) {
                        //充电曲线对象
                        PileFunCurveDto.DataInfo chargeDataInfo = new PileFunCurveDto.DataInfo();
                        chargeDataInfo.setName("储能充电功率");
                        chargeDataInfo.setEName("chargePowerList");
                        List<Object> chargeDataList = Lists.newArrayList();

                        //放电曲线对象
                        PileFunCurveDto.DataInfo dischargeDataInfo = new PileFunCurveDto.DataInfo();
                        dischargeDataInfo.setName("储能放电功率");
                        dischargeDataInfo.setEName("dischargePowerList");
                        List<Object> dischargeDataList = Lists.newArrayList();

                        //根据时间分组
                        Map<String, List<DeviceHistoryDto>> groupByDateMap = deviceHistoryDtoList.stream().collect(Collectors.groupingBy(DeviceHistoryDto::getDateTime));
                        getDateTimeBetween.forEach(dateTime -> {
                            AtomicDouble chargePower = new AtomicDouble(0.0);
                            AtomicDouble dischargePower = new AtomicDouble(0.0);
                            if (groupByDateMap.containsKey(dateTime)) {
                                //循环当前时间点数据，根据数据正负区分充电功率和放电功率
                                groupByDateMap.get(dateTime).forEach(deviceHistoryDto -> {
                                    Object realData = deviceHistoryDto.getDataValue();
                                    if (StringUtil.isNotEmpty(realData)) {
                                        double value = Double.parseDouble(String.valueOf(realData));
                                        if (value >= 0) {
                                            dischargePower.addAndGet(value);
                                        } else {
                                            chargePower.addAndGet(value);
                                        }
                                    }
                                });
                            }
                            chargeDataList.add(getToDouble(Math.abs(chargePower.get())));
                            dischargeDataList.add(getToDouble( -dischargePower.get()));
                        });
                        chargeDataInfo.setDataList(chargeDataList);
                        dischargeDataInfo.setDataList(dischargeDataList);
                        dataInfoList.add(chargeDataInfo);
                        dataInfoList.add(dischargeDataInfo);
                    }
                }
            }
        }
        result.setDataInfoList(dataInfoList);
        return ResponseResult.ok(result);
    }
}
