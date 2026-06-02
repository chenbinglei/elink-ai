package com.sunmax.together.service.monitor.impl;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sunmax.common.dto.PageDto;
import com.sunmax.common.dto.crontab.NodeDifDataDto;
import com.sunmax.common.dto.crontab.SystemVarDataDto;
import com.sunmax.common.dto.data.DeviceHistoryDto;
import com.sunmax.common.dto.data.NodeDifHistoryDto;
import com.sunmax.common.dto.device.*;
import com.sunmax.common.enums.SystemVariableEnum;
import com.sunmax.common.model.DeviceModel;
import com.sunmax.common.model.RealDataModel;
import com.sunmax.common.model.general.PileRealModel;
import com.sunmax.common.util.DateUtil;
import com.sunmax.common.util.DoubleUtil;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.util.StringUtil;
import com.sunmax.common.util.oss.FileUtil;
import com.sunmax.common.vo.FunctionLogoParamVo;
import com.sunmax.common.vo.ReaFieldParamVo;
import com.sunmax.common.vo.SiteFieldParamVo;
import com.sunmax.common.vo.StaticParamVo;
import com.sunmax.common.vo.crontab.VarNodeValueVo;
import com.sunmax.common.vo.data.DeviceHistoryQueryVo;
import com.sunmax.common.vo.data.DeviceIndexQueryVo;
import com.sunmax.common.vo.together.FunctionValueVo;
import com.sunmax.together.dao.DeviceFieldSetDao;
import com.sunmax.together.dao.order.OrderRecordDao;
import com.sunmax.together.dto.monitor.centralMonitorOld.*;
import com.sunmax.together.dto.monitor.assetOverview.PileFunCurveDto;
import com.sunmax.together.entity.DeviceFieldSetEntity;
import com.sunmax.together.entity.order.OrderRecordEntity;
import com.sunmax.together.service.monitor.CentralMonitorOldService;
import com.sunmax.together.service.feign.CrontabService;
import com.sunmax.together.service.feign.DataService;
import com.sunmax.together.service.feign.DeviceService;
import com.sunmax.together.util.TogetherCommonUtil;
import com.sunmax.together.vo.monitor.centralMonitorOld.ElecCountQueryVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.compress.utils.Lists;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Type;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.sunmax.common.config.redis.RedisDeviceUtil.getDeviceList;
import static com.sunmax.common.config.redis.RedisGeneralUtil.getPileRealModelList;
import static com.sunmax.common.util.DateUtil.*;
import static com.sunmax.common.util.DeviceUtil.dataTypeConvert;
import static com.sunmax.common.util.DoubleUtil.getToDouble;

@Slf4j
@Service
public class CentralMonitorOldServiceImpl implements CentralMonitorOldService {

    @Autowired
    private DeviceService deviceService;

    @Autowired
    private CrontabService crontabService;

    @Autowired
    private OrderRecordDao orderRecordDao;

    @Autowired
    private DeviceFieldSetDao deviceFieldSetDao;

    @Autowired
    private DataService dataService;

    /**
     * 查询光伏站点监测数据
     *
     * @param siteId
     * @return
     */
    @Override
    public ResponseResult<PvSiteMonitorDto> findPvSiteMonitorData(String siteId) {
        PvSiteMonitorDto result = new PvSiteMonitorDto();
        //根据站点id，查询站点详细信息
        ResponseResult<Map<String, SiteInfoDto>> siteBasicInfoByIds = deviceService.findSiteBasicInfoByIds(Collections.singletonList(siteId));
        if (siteBasicInfoByIds.isSuccess() && !siteBasicInfoByIds.getData().isEmpty() && siteBasicInfoByIds.getData().containsKey(siteId)) {
            SiteInfoDto siteInfoDto = siteBasicInfoByIds.getData().get(siteId);

            //获取站点扩展属性字段数据
            List<DeviceReaDto> siteReaList = siteInfoDto.getSiteReaList();
            if (CollectionUtils.isNotEmpty(siteReaList)) {
                Map<String, DeviceReaDto> deviceReaDtoMap = siteReaList.stream().collect(Collectors.toMap(DeviceReaDto::getFieldName, deviceReaDto -> deviceReaDto, (k1, k2) -> k1));
                //装机容量
                if (deviceReaDtoMap.containsKey(SiteFieldParamVo.PV_CAPACITY) && StringUtil.isNotEmpty(deviceReaDtoMap.get(SiteFieldParamVo.PV_CAPACITY).getValue())) {
                    result.setCapacity(Double.valueOf(String.valueOf(deviceReaDtoMap.get(SiteFieldParamVo.PV_CAPACITY).getValue())));
                }
                //并网点数量
                if (deviceReaDtoMap.containsKey(SiteFieldParamVo.PARALLEL_NUM) && StringUtil.isNotEmpty(deviceReaDtoMap.get(SiteFieldParamVo.PARALLEL_NUM).getValue())) {
                    result.setParallelNum((Integer) deviceReaDtoMap.get(SiteFieldParamVo.PARALLEL_NUM).getValue());
                }
                //阵列面积
                if (deviceReaDtoMap.containsKey(SiteFieldParamVo.ARRAY_AREA) && StringUtil.isNotEmpty(deviceReaDtoMap.get(SiteFieldParamVo.ARRAY_AREA).getValue())) {
                    result.setArrayArea(Double.valueOf(String.valueOf(deviceReaDtoMap.get(SiteFieldParamVo.ARRAY_AREA).getValue())));
                }
                //阵列倾角
                if (deviceReaDtoMap.containsKey(SiteFieldParamVo.ARRAY_INCLINATION) && StringUtil.isNotEmpty(deviceReaDtoMap.get(SiteFieldParamVo.ARRAY_INCLINATION).getValue())) {
                    result.setArrayInclination(Double.valueOf(String.valueOf(deviceReaDtoMap.get(SiteFieldParamVo.ARRAY_INCLINATION).getValue())));
                }
            }

            //获取昨日开始时间
            String lastDayStartTime = getStartTimeByQueryType(1);
            //获取昨日结束时间
            String lastDayEndTime = getEndTimeByQueryType(1);
            //查询系统变量历史数据
            VarNodeValueVo VarNodeValueVo = new VarNodeValueVo();
            VarNodeValueVo.setVarCodeList(Collections.singletonList(SystemVariableEnum.PV_THEORY_KWH.getCode()));
            VarNodeValueVo.setDeviceIdList(Collections.singletonList(siteId));
            VarNodeValueVo.setStartTime(lastDayStartTime);
            VarNodeValueVo.setEndTime(lastDayEndTime);
            VarNodeValueVo.setTimeInterval("1d");
            ResponseResult<Map<String, Map<String, List<NodeDifDataDto>>>> nodeDifDataListFeign = crontabService.findNodeDifDataListFeign(VarNodeValueVo);
            if (nodeDifDataListFeign.isSuccess() && !nodeDifDataListFeign.getData().isEmpty() && nodeDifDataListFeign.getData().containsKey(siteId)) {
                Map<String, List<NodeDifDataDto>> nodeDifDataMap = nodeDifDataListFeign.getData().get(siteId);
                //理论发电量
                if (!nodeDifDataMap.isEmpty() && nodeDifDataMap.containsKey(SystemVariableEnum.PV_THEORY_KWH.getCode()) && CollectionUtils.isNotEmpty(nodeDifDataMap.get(SystemVariableEnum.PV_THEORY_KWH.getCode()))) {
                    result.setTheoryGeneration(nodeDifDataMap.get(SystemVariableEnum.PV_THEORY_KWH.getCode()).get(0).getLastDataValue());
                }
            }

            //根据站点id，查询站点下所有设备列表
            Map<String, List<DeviceBasicInfoDto>> siteDeviceMap = deviceService.findDeviceBasicInfoBySiteIds(Collections.singletonList(siteId), null).getData();
            if (MapUtils.isNotEmpty(siteDeviceMap) && siteDeviceMap.containsKey(siteId)) {
                //过滤出逆变器设备
                List<DeviceBasicInfoDto> deviceInfoList = siteDeviceMap.get(siteId).stream().filter(d -> StringUtil.isNotEmpty(d.getTypeId()) && "20".equals(d.getTypeId())).collect(Collectors.toList());
                if (CollectionUtils.isNotEmpty(deviceInfoList)) {
                    result.setInverterNum(deviceInfoList.size());

                    //根据多个设备id，和多个功能点标识查询设备功能点实时数据
                    Set<String> deviceIds = deviceInfoList.stream().map(DeviceBasicInfoDto::getId).collect(Collectors.toSet());
                    String functionLogos = String.join(FileUtil.COMMA, FunctionLogoParamVo.ACTIVE_POWER, FunctionLogoParamVo.TOTAL_POWER_GENERATION, FunctionLogoParamVo.DAILY_POWER_GENERATION/*, FunctionLogoParamVo.ELECTRICITY_GENERATED_YESTERDAY*/);
                    ResponseResult<Map<String, Map<String, RealDataModel>>> realDataMapResult = deviceService.getDeviceFunctionsRealDataByIds(deviceIds, functionLogos, 2);
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
                            result.setSumQt(getToDouble(functionRalDataMap.get(FunctionLogoParamVo.TOTAL_POWER_GENERATION).stream().filter(f -> StringUtil.isNotEmpty(f.getRealData())).mapToDouble(f -> Double.parseDouble(String.valueOf(f.getRealData()))).sum()));
                        }
                        //统计今日发电量
                        if (functionRalDataMap.containsKey(FunctionLogoParamVo.DAILY_POWER_GENERATION) && CollectionUtils.isNotEmpty(functionRalDataMap.get(FunctionLogoParamVo.DAILY_POWER_GENERATION))) {
                            result.setDayQt(getToDouble(functionRalDataMap.get(FunctionLogoParamVo.DAILY_POWER_GENERATION).stream().filter(f -> StringUtil.isNotEmpty(f.getRealData())).mapToDouble(f -> Double.parseDouble(String.valueOf(f.getRealData()))).sum()));
                        }
//                        //统计昨日发电量
//                        if (functionRalDataMap.containsKey(FunctionLogoParamVo.ELECTRICITY_GENERATED_YESTERDAY) && CollectionUtils.isNotEmpty(functionRalDataMap.get(FunctionLogoParamVo.ELECTRICITY_GENERATED_YESTERDAY))) {
//                            result.setLastDayQt(getToDouble(functionRalDataMap.get(FunctionLogoParamVo.ELECTRICITY_GENERATED_YESTERDAY).stream().filter(f -> StringUtil.isNotEmpty(f.getRealData())).mapToDouble(f -> Double.parseDouble(String.valueOf(f.getRealData()))).sum()));
//                        }
                        //统计有功功率
                        if (functionRalDataMap.containsKey(FunctionLogoParamVo.ACTIVE_POWER) && CollectionUtils.isNotEmpty(functionRalDataMap.get(FunctionLogoParamVo.ACTIVE_POWER))) {
                            result.setRealPower(getToDouble(functionRalDataMap.get(FunctionLogoParamVo.ACTIVE_POWER).stream().filter(f -> StringUtil.isNotEmpty(f.getRealData())).mapToDouble(f -> Double.parseDouble(String.valueOf(f.getRealData()))).sum()));
                        }
                    }

                    //查询昨日发电量
                    DeviceHistoryQueryVo deviceQueryVo = new DeviceHistoryQueryVo();
                    deviceQueryVo.setDeviceIds(deviceIds);
                    deviceQueryVo.setFunctionLogos(Collections.singleton(FunctionLogoParamVo.TOTAL_POWER_GENERATION));
                    deviceQueryVo.setStartTime(lastDayStartTime);
                    deviceQueryVo.setEndTime(lastDayEndTime);
                    deviceQueryVo.setTimeInterval("1d");
                    Map<String, Map<String, List<NodeDifHistoryDto>>> deviceDifMap = dataService.findNodeDifHistoryListFeign(deviceQueryVo).getData();
                    if (MapUtils.isNotEmpty(deviceDifMap)) {
                        //统计昨日发电量
                        double lastDayQt = deviceDifMap.values().stream().flatMap(d -> d.values().stream().flatMap(Collection::stream))
                                .mapToDouble(d -> DoubleUtil.getObjSub(d.getFirstDataValue(), d.getLastDataValue())).sum();
                        result.setLastDayQt(getToDouble(lastDayQt));

                    }

                    //计算昨日系统效率(昨日发电量 / 昨日理论发电量 * 100)
                    if (StringUtil.isNotEmpty(result.getTheoryGeneration()) && StringUtil.isNotEmpty(result.getLastDayQt()) && result.getLastDayQt() > 0.0) {
                        result.setLastDayEff(getToDouble(result.getLastDayQt() / result.getTheoryGeneration() * 100));
                    }

                    //计算昨日等效利用小时数(昨日发电量 / 站点装机容量)
                    if (StringUtil.isNotEmpty(result.getLastDayQt()) && StringUtil.isNotEmpty(result.getCapacity()) && result.getCapacity() > 0.0) {
                        result.setLastDayHours(getToDouble(result.getLastDayQt() / result.getCapacity()));
                    }
                }
            }
        }
        return ResponseResult.ok(result);
    }

    /**
     * 查询系统变量曲线数据
     *
     * @param varNodeValueVo
     * @param isCurrent
     * @return
     */
    @Override
    public ResponseResult<Map<String, Map<String, Object>>> findSystemVarCurveData(VarNodeValueVo varNodeValueVo, Integer isCurrent) {
        return ResponseResult.ok(TogetherCommonUtil.findVarNodeValueByIds(varNodeValueVo, isCurrent));
    }

    /**
     * 查询光伏逆变器列表
     *
     * @param siteId
     * @return
     */
    @Override
    public ResponseResult<List<PvInverterListDto>> findPvInverterList(String siteId) {
        List<PvInverterListDto> resultList = Lists.newArrayList();
        //根据站点id，查询站点下设备列表数量
        ResponseResult<Map<String, List<DeviceBasicInfoDto>>> deviceBasicInfoBySiteIds = deviceService.findDeviceBasicInfoBySiteIds(Collections.singletonList(siteId), null);
        if (deviceBasicInfoBySiteIds.isSuccess() && !deviceBasicInfoBySiteIds.getData().isEmpty() && deviceBasicInfoBySiteIds.getData().containsKey(siteId)) {

            //根据设备类型id，过滤出逆变器设备信息
            List<DeviceBasicInfoDto> deviceBasicInfoDtos = deviceBasicInfoBySiteIds.getData().get(siteId).stream().filter(d -> StringUtil.isNotEmpty(d.getTypeId()) && "20".equals(d.getTypeId())).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(deviceBasicInfoDtos)) {

                //根据多个设备id，和多个功能点标识查询设备功能点实时数据
                String functionLogos = String.join(FileUtil.COMMA, FunctionLogoParamVo.ACTIVE_POWER, FunctionLogoParamVo.DAILY_POWER_GENERATION, FunctionLogoParamVo.CURRENT_STATUS_OF_THE_INVERTER);
                Set<String> deviceIds = deviceBasicInfoDtos.stream().map(DeviceBasicInfoDto::getId).collect(Collectors.toSet());
                ResponseResult<Map<String, Map<String, RealDataModel>>> realDataMapResult = deviceService.getDeviceFunctionsRealDataByIds(deviceIds, functionLogos, 2);

                //根据设备信息，查询设备通信状态
                Map<String, Integer> txStatusMap = deviceService.findDeviceTxStatus(deviceBasicInfoDtos).getData();

                resultList = deviceBasicInfoDtos.stream().map(deviceBasicInfoDto -> {
                    PvInverterListDto pvInverterListDto = new PvInverterListDto();
                    BeanUtils.copyProperties(deviceBasicInfoDto, pvInverterListDto);
                    Map<String, Object> reaMap = deviceBasicInfoDto.getReaMap();
                    //设备型号
                    if (!reaMap.isEmpty() && reaMap.containsKey(ReaFieldParamVo.MODEL) && StringUtil.isNotEmpty(reaMap.get(ReaFieldParamVo.MODEL))) {
                        pvInverterListDto.setModel(String.valueOf(reaMap.get(ReaFieldParamVo.MODEL)));
                    }
                    //额定功率
                    if (!reaMap.isEmpty() && reaMap.containsKey(ReaFieldParamVo.RATED_POWER) && StringUtil.isNotEmpty(reaMap.get(ReaFieldParamVo.RATED_POWER))) {
                        pvInverterListDto.setPower(Double.parseDouble(String.valueOf(reaMap.get(ReaFieldParamVo.RATED_POWER))));
                    }
                    //额定电流
                    if (!reaMap.isEmpty() && reaMap.containsKey(ReaFieldParamVo.RATED_CURRENT) && StringUtil.isNotEmpty(reaMap.get(ReaFieldParamVo.RATED_CURRENT))) {
                        pvInverterListDto.setRatedCurrent(Double.parseDouble(String.valueOf(reaMap.get(ReaFieldParamVo.RATED_CURRENT))));
                    }
                    //生产厂家
                    if (!reaMap.isEmpty() && reaMap.containsKey(ReaFieldParamVo.MANUFACTURER_NAME) && StringUtil.isNotEmpty(reaMap.get(ReaFieldParamVo.MANUFACTURER_NAME))) {
                        pvInverterListDto.setManufacturerName(String.valueOf(reaMap.get(ReaFieldParamVo.MANUFACTURER_NAME)));
                    }

                    //获取设备通信状态
                    if (!txStatusMap.isEmpty() && txStatusMap.containsKey(deviceBasicInfoDto.getId())) {
                        pvInverterListDto.setTxStatus(txStatusMap.get(deviceBasicInfoDto.getId()));
                    }

                    //获取设备功能点实时数据
                    if (realDataMapResult.isSuccess() && !realDataMapResult.getData().isEmpty() && realDataMapResult.getData().containsKey(deviceBasicInfoDto.getId())) {
                        Map<String, RealDataModel> realDataModelMap = realDataMapResult.getData().get(deviceBasicInfoDto.getId());
                        //有功功率
                        if (realDataModelMap.containsKey(FunctionLogoParamVo.ACTIVE_POWER) && StringUtil.isNotEmpty(realDataModelMap.get(FunctionLogoParamVo.ACTIVE_POWER))) {
                            pvInverterListDto.setActivePower(getToDouble(Double.parseDouble(String.valueOf(realDataModelMap.get(FunctionLogoParamVo.ACTIVE_POWER).getDataValue()))));
                        }
                        //今日发电量
                        if (realDataModelMap.containsKey(FunctionLogoParamVo.DAILY_POWER_GENERATION) && StringUtil.isNotEmpty(realDataModelMap.get(FunctionLogoParamVo.DAILY_POWER_GENERATION))) {
                            pvInverterListDto.setDayQt(getToDouble(Double.parseDouble(String.valueOf(realDataModelMap.get(FunctionLogoParamVo.DAILY_POWER_GENERATION).getDataValue()))));
                        }
                        //运行状态
                        if (realDataModelMap.containsKey(FunctionLogoParamVo.CURRENT_STATUS_OF_THE_INVERTER) && StringUtil.isNotEmpty(realDataModelMap.get(FunctionLogoParamVo.CURRENT_STATUS_OF_THE_INVERTER))) {
                            pvInverterListDto.setRunState(Integer.parseInt(String.valueOf(realDataModelMap.get(FunctionLogoParamVo.CURRENT_STATUS_OF_THE_INVERTER).getDataValue())));
                            pvInverterListDto.setRunStateTime(realDataModelMap.get(FunctionLogoParamVo.CURRENT_STATUS_OF_THE_INVERTER).getDateTime());
                        }
                    }

                    //计算等效发电小时数 今日发电量 / 额定功率
                    if (StringUtil.isNotEmpty(pvInverterListDto.getDayQt()) && StringUtil.isNotEmpty(pvInverterListDto.getPower()) && pvInverterListDto.getPower() > 0) {
                        pvInverterListDto.setEquivalentHours(getToDouble(pvInverterListDto.getDayQt() / pvInverterListDto.getPower()));
                    }

                    return pvInverterListDto;
                }).collect(Collectors.toList());
            }
        }
        return ResponseResult.ok(resultList);
    }

    @Override
    public ResponseResult<List<DeviceAlarmEventListDto>> findNotRecoveEventList(String deviceId) {
        return deviceService.findDeviceNotRecoveEventList(deviceId);
    }

    /**
     * 查询充电站监测数据
     *
     * @param siteId
     * @return
     */
    @Override
    public ResponseResult<ChargeSiteMonitorDto> findChargeSiteMonitorData(String siteId) {
        ChargeSiteMonitorDto result = new ChargeSiteMonitorDto();

        //根据站点id查询所有设备信息
        ResponseResult<Map<String, List<DeviceBasicInfoDto>>> deviceBasicInfoBySiteIds = deviceService.findDeviceBasicInfoBySiteIds(Collections.singletonList(siteId), 1);
        if (deviceBasicInfoBySiteIds.isSuccess() && !deviceBasicInfoBySiteIds.getData().isEmpty() && deviceBasicInfoBySiteIds.getData().containsKey(siteId)) {
            List<DeviceBasicInfoDto> deviceBasicInfoDtoList = deviceBasicInfoBySiteIds.getData().get(siteId);

            //根据多个设备id，和多个功能点标识查询设备功能点实时数据
            Set<String> deviceIds = deviceBasicInfoDtoList.stream().map(DeviceBasicInfoDto::getId).collect(Collectors.toSet());
            ResponseResult<Map<String, Map<String, RealDataModel>>> realDataMapResult = deviceService.getDeviceFunctionsRealDataByIds(deviceIds,
                    FunctionLogoParamVo.PILE_CHARGEPOWER, 2);
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
                //充电功率
                if (functionRalDataMap.containsKey(FunctionLogoParamVo.PILE_CHARGEPOWER) && CollectionUtils.isNotEmpty(functionRalDataMap.get(FunctionLogoParamVo.PILE_CHARGEPOWER))) {
                    result.setRealPower(getToDouble(functionRalDataMap.get(FunctionLogoParamVo.PILE_CHARGEPOWER).stream().filter(f -> StringUtil.isNotEmpty(f.getRealData())).mapToDouble(f -> Double.parseDouble(String.valueOf(f.getRealData()))).sum()));
                }

            }

            //获取设备额定功率
            result.setCapacity(deviceRatedPower(deviceBasicInfoDtoList));

            result.setPileNum(deviceBasicInfoDtoList.size());
            //获取直流电桩数量
            List<DeviceBasicInfoDto> acPileDeviceList = deviceBasicInfoDtoList.stream().filter(d -> StringUtil.isNotEmpty(d.getTypeId()) && "29".equals(d.getTypeId()) || "30".equals(d.getTypeId())).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(acPileDeviceList)) {

                result.setAcPileNum(acPileDeviceList.size());

                //根据电桩id，查询电桩下充电枪信息
                ResponseResult<Map<String, List<DeviceGunInfoDto>>> deviceGunInfoByDeviceIds = deviceService.findDeviceGunInfoByDeviceIds(acPileDeviceList.stream().map(DeviceBasicInfoDto::getId).filter(StringUtil::isNotEmpty).collect(Collectors.toList()));
                if (deviceGunInfoByDeviceIds.isSuccess() && !deviceGunInfoByDeviceIds.getData().isEmpty()) {
                    //获取充电枪总数
                    result.setAcGunNum(deviceGunInfoByDeviceIds.getData().values().stream().mapToInt(List::size).sum());
                }
            }
            //获取交流充电桩数量
            List<DeviceBasicInfoDto> dcPileDeviceList = deviceBasicInfoDtoList.stream().filter(d -> StringUtil.isNotEmpty(d.getTypeId()) && "28".equals(d.getTypeId())).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(dcPileDeviceList)) {

                result.setDcPileNum(dcPileDeviceList.size());

                //根据电桩id，查询电桩下充电枪信息
                ResponseResult<Map<String, List<DeviceGunInfoDto>>> deviceGunInfoByDeviceIds = deviceService.findDeviceGunInfoByDeviceIds(dcPileDeviceList.stream().map(DeviceBasicInfoDto::getId).filter(StringUtil::isNotEmpty).collect(Collectors.toList()));
                if (deviceGunInfoByDeviceIds.isSuccess() && !deviceGunInfoByDeviceIds.getData().isEmpty()) {
                    //获取充电枪总数
                    result.setDcGunNum(deviceGunInfoByDeviceIds.getData().values().stream().mapToInt(List::size).sum());
                }
            }

            //获取昨日开始时间
            String lastDayStartTime = getStartTimeByQueryType(1);
            //获取昨日结束时间
            String lastDayEndTime = getEndTimeByQueryType(1);

            //获取今日开始时间
            String dayStartTime = getStartTimeByQueryType(0);
            //获取今日结束时间
            String dayEndTime = getEndTimeByQueryType(0);

            //根据多个电桩编码查询充放电订单信息
            List<OrderRecordEntity> orderRecordEntityList = orderRecordDao.findAllByPileCodeIn(deviceBasicInfoDtoList.stream().map(DeviceBasicInfoDto::getDeviceNumber).filter(StringUtil::isNotEmpty).collect(Collectors.toList()));
            if (CollectionUtils.isNotEmpty(orderRecordEntityList)) {

                //充电订单
                List<OrderRecordEntity> chargeOrderRecordList = orderRecordEntityList.stream().filter(o -> StringUtil.isNotEmpty(o.getRunMode()) && o.getRunMode() == 0).collect(Collectors.toList());
                if (CollectionUtils.isNotEmpty(chargeOrderRecordList)) {
                    //累计充电量
                    result.setSumChargeQt(getToDouble(chargeOrderRecordList.stream().filter(o -> StringUtil.isNotEmpty(o.getTotalQt())).mapToDouble(OrderRecordEntity::getTotalQt).sum()));

                    //今日订单数据
                    List<OrderRecordEntity> todayChargeOrderRecordList = chargeOrderRecordList.stream().filter(o -> StringUtil.isNotEmpty(o.getEndTime()) && DateUtil.strToLocalDateTime(o.getStartTime()).isAfter(strToLocalDateTime(dayStartTime)) &&
                            DateUtil.strToLocalDateTime(o.getStartTime()).isBefore(strToLocalDateTime(dayEndTime))).collect(Collectors.toList());
                    if (CollectionUtils.isNotEmpty(todayChargeOrderRecordList)) {
                        //今日充电量
                        result.setDayChargeQt(getToDouble(todayChargeOrderRecordList.stream().filter(o -> StringUtil.isNotEmpty(o.getTotalQt())).mapToDouble(OrderRecordEntity::getTotalQt).sum()));
                    }

                    //昨日订单数据
                    List<OrderRecordEntity> lastDayChargeOrderRecordList = chargeOrderRecordList.stream().filter(o -> StringUtil.isNotEmpty(o.getEndTime()) && DateUtil.strToLocalDateTime(o.getStartTime()).isAfter(strToLocalDateTime(lastDayStartTime)) &&
                            DateUtil.strToLocalDateTime(o.getStartTime()).isBefore(strToLocalDateTime(lastDayEndTime))).collect(Collectors.toList());
                    if (CollectionUtils.isNotEmpty(lastDayChargeOrderRecordList)) {
                        //昨日充电量
                        result.setLastDayChargeQt(getToDouble(lastDayChargeOrderRecordList.stream().filter(o -> StringUtil.isNotEmpty(o.getTotalQt())).mapToDouble(OrderRecordEntity::getTotalQt).sum()));
                    }
                }

                //放电订单
                List<OrderRecordEntity> dischargeOrderRecordList = orderRecordEntityList.stream().filter(o -> StringUtil.isNotEmpty(o.getRunMode()) && (o.getRunMode() == 1 || o.getRunMode() == 2)).collect(Collectors.toList());
                if (CollectionUtils.isNotEmpty(dischargeOrderRecordList)) {
                    //累计放电量
                    result.setSumV2gQt(getToDouble(dischargeOrderRecordList.stream().filter(o -> StringUtil.isNotEmpty(o.getTotalQt())).mapToDouble(OrderRecordEntity::getTotalQt).sum()));

                    //今日订单数据
                    List<OrderRecordEntity> todayChargeOrderRecordList = dischargeOrderRecordList.stream().filter(o -> StringUtil.isNotEmpty(o.getEndTime()) && DateUtil.strToLocalDateTime(o.getStartTime()).isAfter(strToLocalDateTime(dayStartTime)) &&
                            DateUtil.strToLocalDateTime(o.getStartTime()).isBefore(strToLocalDateTime(dayEndTime))).collect(Collectors.toList());
                    if (CollectionUtils.isNotEmpty(todayChargeOrderRecordList)) {
                        //今日放电量
                        result.setDayV2gQt(getToDouble(todayChargeOrderRecordList.stream().filter(o -> StringUtil.isNotEmpty(o.getTotalQt())).mapToDouble(OrderRecordEntity::getTotalQt).sum()));
                    }

                    //昨日订单数据
                    List<OrderRecordEntity> lastDayChargeOrderRecordList = dischargeOrderRecordList.stream().filter(o -> StringUtil.isNotEmpty(o.getEndTime()) && DateUtil.strToLocalDateTime(o.getStartTime()).isAfter(strToLocalDateTime(lastDayStartTime)) &&
                            DateUtil.strToLocalDateTime(o.getStartTime()).isBefore(strToLocalDateTime(lastDayEndTime))).collect(Collectors.toList());
                    if (CollectionUtils.isNotEmpty(lastDayChargeOrderRecordList)) {
                        //昨日放电量
                        result.setLastDayV2gQt(getToDouble(lastDayChargeOrderRecordList.stream().filter(o -> StringUtil.isNotEmpty(o.getTotalQt())).mapToDouble(OrderRecordEntity::getTotalQt).sum()));
                    }
                }
            }
        }
        return ResponseResult.ok(result);
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

    /**
     * 查询充电站电量统计曲线数据
     *
     * @param elecCountQueryVo
     * @return
     */
    @Override
    public ResponseResult<ElecCountCurveDto> findElecCountCurveData(ElecCountQueryVo elecCountQueryVo) {
        ElecCountCurveDto result = new ElecCountCurveDto();
        //根据开始结束时间查询时间轴列表
        List<String> dateBetween = getDateBetween(elecCountQueryVo.getDateType(), elecCountQueryVo.getStartTime().substring(0, 10), elecCountQueryVo.getEndTime().substring(0, 10));

        List<String> siteIdList = JSON.parseArray(elecCountQueryVo.getSiteIds(), String.class);
        //根据站点id和时间查询无异常订单数据
        List<OrderRecordEntity> orderRecordEntityList = orderRecordDao.findAllBySiteIdInAndEndTimeBetween(siteIdList, elecCountQueryVo.getStartTime(), elecCountQueryVo.getEndTime());
        if (CollectionUtils.isNotEmpty(orderRecordEntityList)) {

            //充电订单
            List<OrderRecordEntity> chargeOrderRecordList = orderRecordEntityList.stream().filter(o -> StringUtil.isNotEmpty(o.getRunMode()) && o.getRunMode() == 0).collect(Collectors.toList());
            Map<String, List<OrderRecordEntity>> groupByChargeOrderMap = Maps.newHashMap();
            if (CollectionUtils.isNotEmpty(chargeOrderRecordList)) {
                //根据时间类型格式，以订单结束时间分组充电订单数据
                if (elecCountQueryVo.getDateType() == 1) {
                    groupByChargeOrderMap = chargeOrderRecordList.stream().filter(o -> StringUtil.isNotEmpty(o.getEndTime())).collect(Collectors.groupingBy(o -> o.getEndTime().substring(0, 10)));
                } else if (elecCountQueryVo.getDateType() == 2) {
                    groupByChargeOrderMap = chargeOrderRecordList.stream().filter(o -> StringUtil.isNotEmpty(o.getEndTime())).collect(Collectors.groupingBy(o -> o.getEndTime().substring(0, 7)));
                } else if (elecCountQueryVo.getDateType() == 3) {
                    groupByChargeOrderMap = chargeOrderRecordList.stream().filter(o -> StringUtil.isNotEmpty(o.getEndTime())).collect(Collectors.groupingBy(o -> o.getEndTime().substring(0, 4)));
                }
            }

            //放电订单
            List<OrderRecordEntity> dischargeOrderRecordList = orderRecordEntityList.stream().filter(o -> StringUtil.isNotEmpty(o.getRunMode()) && (o.getRunMode() == 1 || o.getRunMode() == 2)).collect(Collectors.toList());
            Map<String, List<OrderRecordEntity>> groupByDischargeOrderMap = Maps.newHashMap();
            if (CollectionUtils.isNotEmpty(dischargeOrderRecordList)) {
                //根据时间类型格式，以订单结束时间分组充电订单数据
                if (elecCountQueryVo.getDateType() == 1) {
                    groupByDischargeOrderMap = dischargeOrderRecordList.stream().filter(o -> StringUtil.isNotEmpty(o.getEndTime())).collect(Collectors.groupingBy(o -> o.getEndTime().substring(0, 10)));
                } else if (elecCountQueryVo.getDateType() == 2) {
                    groupByDischargeOrderMap = dischargeOrderRecordList.stream().filter(o -> StringUtil.isNotEmpty(o.getEndTime())).collect(Collectors.groupingBy(o -> o.getEndTime().substring(0, 7)));
                } else if (elecCountQueryVo.getDateType() == 3) {
                    groupByDischargeOrderMap = dischargeOrderRecordList.stream().filter(o -> StringUtil.isNotEmpty(o.getEndTime())).collect(Collectors.groupingBy(o -> o.getEndTime().substring(0, 4)));
                }
            }

            //循环时间组装数据
            Map<String, List<OrderRecordEntity>> finalGroupByChargeOrderMap = groupByChargeOrderMap;
            Map<String, List<OrderRecordEntity>> finalGroupByDischargeOrderMap = groupByDischargeOrderMap;
            dateBetween.forEach(date -> {
                Double chargeElecCount = null;
                Double dischargeElecCount = null;
                //充电订单
                if (!finalGroupByChargeOrderMap.isEmpty() && finalGroupByChargeOrderMap.containsKey(date)) {
                    List<OrderRecordEntity> chargeOrderRecordListByDate = finalGroupByChargeOrderMap.get(date);
                    //充电订单总电量
                    chargeElecCount = getToDouble(chargeOrderRecordListByDate.stream().filter(o -> StringUtil.isNotEmpty(o.getTotalQt())).mapToDouble(OrderRecordEntity::getTotalQt).sum());
                }
                //放电订单
                if (!finalGroupByDischargeOrderMap.isEmpty() && finalGroupByDischargeOrderMap.containsKey(date)) {
                    List<OrderRecordEntity> dischargeOrderRecordListByDate = finalGroupByDischargeOrderMap.get(date);
                    //放电订单总电量
                    dischargeElecCount = getToDouble(dischargeOrderRecordListByDate.stream().filter(o -> StringUtil.isNotEmpty(o.getTotalQt())).mapToDouble(OrderRecordEntity::getTotalQt).sum());
                }
                result.getChargeQtList().add(chargeElecCount);
                result.getDischargeQtList().add(dischargeElecCount);
            });
        }
        if (elecCountQueryVo.getDateType() == 1) {
            dateBetween = dateBetween.stream().map(s -> s.substring(0, 10)).collect(Collectors.toList());
        } else if (elecCountQueryVo.getDateType() == 2) {
            dateBetween = dateBetween.stream().map(s -> s.substring(0, 7)).collect(Collectors.toList());
        } else if (elecCountQueryVo.getDateType() == 3) {
            dateBetween = dateBetween.stream().map(s -> s.substring(0, 4)).collect(Collectors.toList());
        }
        result.setXAxisList(dateBetween);
        return ResponseResult.ok(result);
    }

    /**
     * 查询电桩设备列表
     *
     * @param siteId
     * @return
     */
    @Override
    public ResponseResult<List<PileDeviceListDto>> findPileDeviceList(String siteId) {
        List<PileDeviceListDto> resultList = Lists.newArrayList();
        //根据站点id，查询站点下电桩设备列表
        ResponseResult<Map<String, List<DeviceBasicInfoDto>>> deviceBasicInfoBySiteIds = deviceService.findDeviceBasicInfoBySiteIds(Collections.singletonList(siteId), 1);
        if (deviceBasicInfoBySiteIds.isSuccess() && !deviceBasicInfoBySiteIds.getData().isEmpty() && deviceBasicInfoBySiteIds.getData().containsKey(siteId)) {
            List<DeviceBasicInfoDto> deviceBasicInfoDtos = deviceBasicInfoBySiteIds.getData().get(siteId);

            //根据多个设备id，和多个功能点标识查询设备功能点实时数据
            String funationLogos = FunctionLogoParamVo.PILE_POWER;
            ResponseResult<Map<String, Map<String, RealDataModel>>> realDataMapResult = deviceService.getDeviceFunctionsRealDataByIds(
                    deviceBasicInfoDtos.stream().map(DeviceBasicInfoDto::getId).collect(Collectors.toSet()), funationLogos, 2);

            //根据多个设备编码，查询设备实时缓存数据
            List<DeviceModel> deviceModelList = getDeviceList(deviceBasicInfoDtos.stream().map(DeviceBasicInfoDto::getDeviceNumber).filter(StringUtil::isNotEmpty).collect(Collectors.toList()));
            Map<String, DeviceModel> deviceModelMap = Maps.newHashMap();
            if (CollectionUtils.isNotEmpty(deviceModelList) && StringUtil.isNotEmpty(deviceModelList.get(0).getDeviceNumber())) {
                deviceModelMap = deviceModelList.stream().collect(Collectors.toMap(DeviceModel::getDeviceNumber, Function.identity(), (o1, o2) -> o1));
            }

            List<String> pileCodeList = deviceBasicInfoDtos.stream().map(DeviceBasicInfoDto::getDeviceNumber).filter(StringUtil::isNotEmpty).collect(Collectors.toList());
            //根据多个电桩编码，查询电桩实时数据
            List<PileRealModel> pileRealModelList = getPileRealModelList(pileCodeList);
            Map<String, PileRealModel> pileRealModelMap = Maps.newHashMap();
            if (CollectionUtils.isNotEmpty(pileRealModelList) && StringUtil.isNotEmpty(pileRealModelList.get(0).getPileCode())) {
                pileRealModelMap = pileRealModelList.stream().collect(Collectors.toMap(PileRealModel::getPileCode, pileRealModel -> pileRealModel, (k1, k2) -> k1));
            }

            //获取今日开始时间
            String dayStartTime = getStartTimeByQueryType(0);
            //获取今日结束时间
            String dayEndTime = getEndTimeByQueryType(0);

            //查询电桩今日充放电订单
            List<OrderRecordEntity> orderRecordEntityList = orderRecordDao.findAllByPileCodeInAndRunModeAndEndTimeBetween(pileCodeList, 0, dayStartTime, dayEndTime);
            Map<String, List<OrderRecordEntity>> pileChargeOrderMap = Maps.newHashMap();
            if (CollectionUtils.isNotEmpty(orderRecordEntityList)) {
                pileChargeOrderMap = orderRecordEntityList.stream().collect(Collectors.groupingBy(OrderRecordEntity::getPileCode));
            }

            Map<String, DeviceModel> finalDeviceModelMap = deviceModelMap;
            Map<String, PileRealModel> finalPileRealModelMap = pileRealModelMap;
            Map<String, List<OrderRecordEntity>> finalPileChargeOrderMap = pileChargeOrderMap;
            resultList = deviceBasicInfoDtos.stream().map(deviceBasicInfoDto -> {
                PileDeviceListDto pileDeviceListDto = new PileDeviceListDto();
                BeanUtils.copyProperties(deviceBasicInfoDto, pileDeviceListDto);
                Map<String, Object> reaMap = deviceBasicInfoDto.getReaMap();
                //设备型号
                if (!reaMap.isEmpty() && reaMap.containsKey(ReaFieldParamVo.MODEL) && StringUtil.isNotEmpty(reaMap.get(ReaFieldParamVo.MODEL))) {
                    pileDeviceListDto.setModel(String.valueOf(reaMap.get(ReaFieldParamVo.MODEL)));
                }
                //额定功率
                if (!reaMap.isEmpty() && reaMap.containsKey(ReaFieldParamVo.RATED_POWER) && StringUtil.isNotEmpty(reaMap.get(ReaFieldParamVo.RATED_POWER))) {
                    pileDeviceListDto.setPower(Double.parseDouble(String.valueOf(reaMap.get(ReaFieldParamVo.RATED_POWER))));
                }
                //生产厂家
                if (!reaMap.isEmpty() && reaMap.containsKey(ReaFieldParamVo.MANUFACTURER_NAME) && StringUtil.isNotEmpty(reaMap.get(ReaFieldParamVo.MANUFACTURER_NAME))) {
                    pileDeviceListDto.setManufacturerName(String.valueOf(reaMap.get(ReaFieldParamVo.MANUFACTURER_NAME)));
                }

                //获取设备通信状态
                if (!finalDeviceModelMap.isEmpty() && finalDeviceModelMap.containsKey(deviceBasicInfoDto.getDeviceNumber())) {
                    pileDeviceListDto.setTxStatus(finalDeviceModelMap.get(deviceBasicInfoDto.getDeviceNumber()).getTxStatus());
                }

                //获取电桩实时状态
                if (!finalPileRealModelMap.isEmpty() && finalPileRealModelMap.containsKey(deviceBasicInfoDto.getDeviceNumber()) &&
                        StringUtil.isNotEmpty(finalPileRealModelMap.get(deviceBasicInfoDto.getDeviceNumber()).getPileCode())) {
                    pileDeviceListDto.setWorkStatus(finalPileRealModelMap.get(deviceBasicInfoDto.getDeviceNumber()).getWorkStatus());
                }

                //获取设备功能点实时数据
                if (realDataMapResult.isSuccess() && !realDataMapResult.getData().isEmpty() && realDataMapResult.getData().containsKey(deviceBasicInfoDto.getId())) {
                    Map<String, RealDataModel> realDataModelMap = realDataMapResult.getData().get(deviceBasicInfoDto.getId());
                    //电桩总功率
                    if (realDataModelMap.containsKey(FunctionLogoParamVo.PILE_POWER) && StringUtil.isNotEmpty(realDataModelMap.get(FunctionLogoParamVo.PILE_POWER)) && StringUtil.isNotEmpty(realDataModelMap.get(FunctionLogoParamVo.PILE_POWER).getDataValue())) {
                        pileDeviceListDto.setRealPower(getToDouble(Double.parseDouble(String.valueOf(realDataModelMap.get(FunctionLogoParamVo.PILE_POWER).getDataValue()))));
                    }
                }

                //获取电桩今日充电量
                if (!finalPileChargeOrderMap.isEmpty() && finalPileChargeOrderMap.containsKey(deviceBasicInfoDto.getDeviceNumber())) {
                    pileDeviceListDto.setDayChargeQt(getToDouble(finalPileChargeOrderMap.get(deviceBasicInfoDto.getDeviceNumber()).stream().filter(o -> StringUtil.isNotEmpty(o.getTotalQt())).mapToDouble(OrderRecordEntity::getTotalQt).sum()));
                }
                return pileDeviceListDto;
            }).collect(Collectors.toList());
        }
        return ResponseResult.ok(resultList);
    }

    /**
     * 添加设备字段设置
     *
     * @param deviceId
     * @param functionFields 多个功能点字段id 例如['1','2','3']
     * @return
     */
    @Override
    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class)
    public ResponseResult<Void> saveDeviceDeviceFieldSet(String deviceId, String functionFields) {
        DeviceFieldSetEntity deviceFieldSet = deviceFieldSetDao.findOne(Example.of(DeviceFieldSetEntity.builder().deviceId(deviceId).build())).orElseGet(DeviceFieldSetEntity::new);
        deviceFieldSet.setDeviceId(deviceId);
        deviceFieldSet.setFunctionFields(functionFields);
        deviceFieldSetDao.save(deviceFieldSet);
        return ResponseResult.ok();
    }

    @Override
    public ResponseResult<List<DeviceTelemetryDto>> findDeviceTelemetryList(String deviceId) {
        List<DeviceTelemetryDto> resultList = Lists.newArrayList();

        //根据设备id查询标准功能列表数据
        ResponseResult<List<FunctionDataDto>> deviceFunctionDataList = deviceService.findDeviceFunctionDataList(deviceId);
        if (deviceFunctionDataList.isSuccess() && CollectionUtils.isNotEmpty(deviceFunctionDataList.getData())) {
            List<FunctionDataDto> functionDataList = deviceFunctionDataList.getData();

            //根据设备id，查询设备配置数据
            List<String> functionFieldIds = com.google.common.collect.Lists.newArrayList();
            Optional<DeviceFieldSetEntity> functionFieldOptional = deviceFieldSetDao.findOne(Example
                    .of(DeviceFieldSetEntity.builder().deviceId(deviceId).build()));
            if (functionFieldOptional.isPresent() && StringUtil.isNotEmpty(functionFieldOptional.get().getFunctionFields())) {
                functionFieldIds = JSON.parseArray(functionFieldOptional.get().getFunctionFields(), String.class);
            }

            List<String> finalFunctionFieldIds = functionFieldIds;
            resultList = functionDataList.stream().map(functionDataDto -> {
                DeviceTelemetryDto deviceTelemetryDto = new DeviceTelemetryDto();
                BeanUtils.copyProperties(functionDataDto, deviceTelemetryDto);
                //展示类型
                deviceTelemetryDto.setShowType(1);
                if (finalFunctionFieldIds.contains(functionDataDto.getFunctionId())) { //包含则不展示
                    deviceTelemetryDto.setShowType(2);
                }
                return deviceTelemetryDto;
            }).collect(Collectors.toList());
        }
        return ResponseResult.ok(resultList);
    }

    @Override
    public ResponseResult<List<SystemVarDataDto>> findSystemVarDataListById(String varCodes, String queryId) {
        return crontabService.findSystemVarDataListById(varCodes, queryId);
    }

    @Override
    public ResponseResult<Map<String, Map<String, Object>>> queryDeviceFunctionCurveData(FunctionValueVo functionValueVo) {
        Map<String, Map<String, Object>> resultMap = Maps.newHashMap();

        if (CollectionUtils.isEmpty(functionValueVo.getDeviceIdList()) || CollectionUtils.isEmpty(functionValueVo.getFunctionLogos())) {
            return ResponseResult.error("缺少参数！", resultMap);
        }
        String startTime = functionValueVo.getStartTime();
        String endTime = functionValueVo.getEndTime();
        //获取时间轴
        List<String> getDateTimeBetween = getLocalDateTimeBetween(strToLocalDateTime(startTime), strToLocalDateTime(endTime), functionValueVo.getTimeInterval()).stream().map(DateUtil::localDateTimeToStr).collect(Collectors.toList());

        //查询指定功能点历史数据
        if (StringUtil.isNotEmpty(functionValueVo.getIndex())) {//根据索引查询数据
            resultMap = findFunctionIndexValueList(functionValueVo, getDateTimeBetween);
        } else {
            resultMap = findFunctionValueList(functionValueVo, getDateTimeBetween);
        }
        return ResponseResult.ok(resultMap);
    }

    /**
     * 查询功能点索引值数据
     *
     * @param functionValueVo
     * @param getDateTimeBetween
     * @return
     */
    private Map<String, Map<String, Object>> findFunctionIndexValueList(FunctionValueVo functionValueVo, List<String> getDateTimeBetween) {
        Map<String, Map<String, Object>> resultMap = Maps.newHashMap();

        DeviceIndexQueryVo deviceIndexQueryVo = new DeviceIndexQueryVo();

        Set<String> functionIndexSet = functionValueVo.getFunctionLogos().stream().map(functionLogo -> functionLogo + "index" + (functionValueVo.getIndex() - 1)).collect(Collectors.toSet());
        Map<String, Set<String>> deviceFuctionMap = Maps.newHashMap();
        functionValueVo.getDeviceIdList().forEach(deviceId -> deviceFuctionMap.put(deviceId, functionIndexSet));
        deviceIndexQueryVo.setDeviceFuctionMap(deviceFuctionMap);

        deviceIndexQueryVo.setStartTime(functionValueVo.getStartTime());
        deviceIndexQueryVo.setEndTime(functionValueVo.getEndTime());
        deviceIndexQueryVo.setTimeInterval(functionValueVo.getTimeInterval());
        deviceIndexQueryVo.setLimitSize(functionValueVo.getLimitSize());
        //查询功能点索引历史数据
        ResponseResult<Map<String, Map<String, List<DeviceHistoryDto>>>> deviceHistoryIndexValueList = dataService.findDeviceHistoryIndexValueList(deviceIndexQueryVo);
        if (deviceHistoryIndexValueList.isSuccess() && !deviceHistoryIndexValueList.getData().isEmpty()) {
            Map<String, Map<String, List<DeviceHistoryDto>>> historyIndexValueListData = deviceHistoryIndexValueList.getData();

            //循环设备id获取数据
            functionValueVo.getDeviceIdList().forEach(deviceId -> {
                Map<String, Object> funcDataMap = Maps.newHashMap();
                if (historyIndexValueListData.containsKey(deviceId)) {
                    Map<String, List<DeviceHistoryDto>> deviceHistoryMap = historyIndexValueListData.get(deviceId);
                    //循环功能点获取数据
                    functionIndexSet.forEach(functionLogo -> {
                        //存储功能点数据
                        List<Double> dataList = Lists.newArrayList();
                        List<DeviceHistoryDto> historyDataList = deviceHistoryMap.get(functionLogo.substring(0, functionLogo.indexOf(StaticParamVo.INDEX)));
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
                        funcDataMap.put(functionLogo.substring(0, functionLogo.indexOf(StaticParamVo.INDEX)), dataList);
                    });
                    funcDataMap.put("xAxisList", getDateTimeBetween);
                }
                resultMap.put(deviceId, funcDataMap);
            });
        }
        return resultMap;
    }

    /**
     * 查询功能点历史值数据
     *
     * @param functionValueVo
     * @param getDateTimeBetween
     * @return
     */
    private Map<String, Map<String, Object>> findFunctionValueList(FunctionValueVo functionValueVo, List<String> getDateTimeBetween) {
        Map<String, Map<String, Object>> resultMap = Maps.newHashMap();

        DeviceHistoryQueryVo deviceHistoryQueryVo = new DeviceHistoryQueryVo();

        deviceHistoryQueryVo.setDeviceIds(new HashSet<>(functionValueVo.getDeviceIdList()));
        deviceHistoryQueryVo.setFunctionLogos(new HashSet<>(functionValueVo.getFunctionLogos()));
        deviceHistoryQueryVo.setStartTime(functionValueVo.getStartTime());
        deviceHistoryQueryVo.setEndTime(functionValueVo.getEndTime());
        deviceHistoryQueryVo.setTimeInterval(functionValueVo.getTimeInterval());
        deviceHistoryQueryVo.setLimitSize(functionValueVo.getLimitSize());
        //查询功能点索引历史数据
        ResponseResult<Map<String, Map<String, List<DeviceHistoryDto>>>> deviceHistoryValueList = dataService.findDeviceHistoryValueList(deviceHistoryQueryVo);
        if (deviceHistoryValueList.isSuccess() && !deviceHistoryValueList.getData().isEmpty()) {
            Map<String, Map<String, List<DeviceHistoryDto>>> historyValueListData = deviceHistoryValueList.getData();

            //循环设备id获取数据
            functionValueVo.getDeviceIdList().forEach(deviceId -> {
                Map<String, Object> funcDataMap = Maps.newHashMap();
                if (historyValueListData.containsKey(deviceId)) {
                    Map<String, List<DeviceHistoryDto>> deviceHistoryMap = historyValueListData.get(deviceId);
                    //循环功能点获取数据
                    functionValueVo.getFunctionLogos().forEach(functionLogo -> {
                        //存储功能点数据
                        List<Double> dataList = Lists.newArrayList();
                        List<DeviceHistoryDto> historyDataList = deviceHistoryMap.get(functionLogo);
                        if (CollectionUtils.isNotEmpty(historyDataList)) {
                            Map<String, DeviceHistoryDto> deviceHistoryDtoMap = new TreeMap<>(historyDataList.stream().collect(Collectors.toMap(DeviceHistoryDto::getDateTime, DeviceHistoryDto -> DeviceHistoryDto, (k1, k2) -> k1)));
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
                        funcDataMap.put(functionLogo, dataList);
                    });
                    funcDataMap.put("xAxisList", getDateTimeBetween);
                }
                resultMap.put(deviceId, funcDataMap);
            });
        }
        return resultMap;
    }


    /**
     * 查询储能站点监测数据
     *
     * @param siteId
     * @return
     */
    @Override
    public ResponseResult<StorageMonitorDto> findStorageMonitorData(String siteId) {
        StorageMonitorDto result = new StorageMonitorDto();

        //根据站点id，查询站点详细信息
        ResponseResult<Map<String, SiteInfoDto>> siteBasicInfoByIds = deviceService.findSiteBasicInfoByIds(Collections.singletonList(siteId));
        if (siteBasicInfoByIds.isSuccess() && !siteBasicInfoByIds.getData().isEmpty() && siteBasicInfoByIds.getData().containsKey(siteId)) {
            SiteInfoDto siteInfoDto = siteBasicInfoByIds.getData().get(siteId);

            //根据站点id，查询站点下所有设备列表
            ResponseResult<Map<String, List<DeviceBasicInfoDto>>> deviceBasicInfoBySiteIds = deviceService.findDeviceBasicInfoBySiteIds(Collections.singletonList(siteId), null);
            if (deviceBasicInfoBySiteIds.isSuccess() && !deviceBasicInfoBySiteIds.getData().isEmpty() && deviceBasicInfoBySiteIds.getData().containsKey(siteId)) {
                //根据设备类型id，获取pcs设备
                List<DeviceBasicInfoDto> pcsDeviceInfoDtos = deviceBasicInfoBySiteIds.getData().get(siteId).stream().filter(d -> StringUtil.isNotEmpty(d.getTypeId()) && "23".equals(d.getTypeId())).collect(Collectors.toList());
                if (CollectionUtils.isNotEmpty(pcsDeviceInfoDtos)) {
                    result.setPcsNum(pcsDeviceInfoDtos.size());
                    //统计pcs总额定功率
                    result.setPcsTotalPower(pcsDeviceInfoDtos.stream().mapToDouble(d -> {
                        Map<String, Object> reaMap = d.getReaMap();
                        if (!reaMap.isEmpty() && reaMap.containsKey(ReaFieldParamVo.RATED_POWER) && StringUtil.isNotEmpty(reaMap.get(ReaFieldParamVo.RATED_POWER))) {
                            return Double.parseDouble(String.valueOf(reaMap.get(ReaFieldParamVo.RATED_POWER)));
                        }
                        return 0.0;
                    }).sum());

                    //根据多个设备id，和多个功能点标识查询设备功能点实时数据
                    String functionLogos = FunctionLogoParamVo.PCS_ACTIVE_POWER;
                    ResponseResult<Map<String, Map<String, RealDataModel>>> realDataMapResult = deviceService.getDeviceFunctionsRealDataByIds(
                            pcsDeviceInfoDtos.stream().map(DeviceBasicInfoDto::getId).collect(Collectors.toSet()), functionLogos, 2);
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
                            result.setRealPower(getToDouble(functionRalDataMap.get(FunctionLogoParamVo.PCS_ACTIVE_POWER).stream().filter(f -> StringUtil.isNotEmpty(f.getRealData())).mapToDouble(f -> Double.parseDouble(String.valueOf(f.getRealData()))).sum()));
                        }

                    }
                }
                //根据设备类型id，获取电池簇设备
                List<DeviceBasicInfoDto> batteryDeviceInfoDtos = deviceBasicInfoBySiteIds.getData().get(siteId).stream().filter(d -> StringUtil.isNotEmpty(d.getTypeId()) && "25".equals(d.getTypeId())).collect(Collectors.toList());
                if (CollectionUtils.isNotEmpty(batteryDeviceInfoDtos)) {
                    result.setBatteryNum(batteryDeviceInfoDtos.size());

                    //统计电池簇总额定容量
                    result.setBatteryTotalCapacity(batteryDeviceInfoDtos.stream().mapToDouble(d -> {
                        Map<String, Object> reaMap = d.getReaMap();
                        if (!reaMap.isEmpty() && reaMap.containsKey(ReaFieldParamVo.RATED_CAP) && StringUtil.isNotEmpty(reaMap.get(ReaFieldParamVo.RATED_CAP))) {
                            return Double.parseDouble(String.valueOf(reaMap.get(ReaFieldParamVo.RATED_CAP)));
                        }
                        return 0.0;
                    }).sum());

                    //根据多个设备id，和多个功能点标识查询设备功能点实时数据
                    String functionLogos = String.join(FileUtil.COMMA, FunctionLogoParamVo.ACCCHARGEQ, FunctionLogoParamVo.ACCDISCHARGEQ);
                    ResponseResult<Map<String, Map<String, RealDataModel>>> realDataMapResult = deviceService.getDeviceFunctionsRealDataByIds(
                            batteryDeviceInfoDtos.stream().map(DeviceBasicInfoDto::getId).collect(Collectors.toSet()), functionLogos, 1);
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
                        //累计充电量
                        if (functionRalDataMap.containsKey(FunctionLogoParamVo.ACCCHARGEQ) && CollectionUtils.isNotEmpty(functionRalDataMap.get(FunctionLogoParamVo.ACCCHARGEQ))) {
                            result.setSumChargeQt(getToDouble(functionRalDataMap.get(FunctionLogoParamVo.ACCCHARGEQ).stream().filter(f -> StringUtil.isNotEmpty(f.getRealData())).mapToDouble(f -> Double.parseDouble(String.valueOf(f.getRealData()))).sum()));
                        }
                        //累计放电量
                        if (functionRalDataMap.containsKey(FunctionLogoParamVo.ACCDISCHARGEQ) && CollectionUtils.isNotEmpty(functionRalDataMap.get(FunctionLogoParamVo.ACCDISCHARGEQ))) {
                            result.setSumDischargeQt(getToDouble(functionRalDataMap.get(FunctionLogoParamVo.ACCDISCHARGEQ).stream().filter(f -> StringUtil.isNotEmpty(f.getRealData())).mapToDouble(f -> Double.parseDouble(String.valueOf(f.getRealData()))).sum()));
                        }
                    }

                    //获取昨日开始时间
                    String lastDayStartTime = getStartTimeByQueryType(1);
                    //获取昨日结束时间
                    String lastDayEndTime = getEndTimeByQueryType(1);
                    DeviceHistoryQueryVo deviceHistoryQueryVo = new DeviceHistoryQueryVo();
                    deviceHistoryQueryVo.setDeviceIds(batteryDeviceInfoDtos.stream().map(DeviceBasicInfoDto::getId).collect(Collectors.toSet()));
                    deviceHistoryQueryVo.setFunctionLogos(new HashSet<>(Arrays.asList(FunctionLogoParamVo.ACCCHARGEQ, FunctionLogoParamVo.ACCDISCHARGEQ)));
                    deviceHistoryQueryVo.setStartTime(lastDayStartTime);
                    deviceHistoryQueryVo.setEndTime(lastDayEndTime);
                    deviceHistoryQueryVo.setTimeInterval("1d");
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
                        //计算昨日充电量
                        if (CollectionUtils.isNotEmpty(sumChargeList)) {
                            result.setLastDayChargeQt(sumChargeList.stream().filter(n -> StringUtil.isNotEmpty(n.getLastDataValue()) && StringUtil.isNotEmpty(n.getFirstDataValue()))
                                    .mapToDouble(qtData -> Double.parseDouble(String.valueOf(qtData.getLastDataValue())) - Double.parseDouble(String.valueOf(qtData.getFirstDataValue()))).sum());
                        }
                        //计算昨日放电量
                        if (CollectionUtils.isNotEmpty(sumDischargeList)) {
                            result.setLastDayDischargeQt(sumDischargeList.stream().filter(n -> StringUtil.isNotEmpty(n.getLastDataValue()) && StringUtil.isNotEmpty(n.getFirstDataValue()))
                                    .mapToDouble(qtData -> Double.parseDouble(String.valueOf(qtData.getLastDataValue())) - Double.parseDouble(String.valueOf(qtData.getFirstDataValue()))).sum());
                        }
                    }
                }
            }
            //计算昨日系统效率(昨日放电量 / 昨日充电量 * 100)
            if (StringUtil.isNotEmpty(result.getLastDayDischargeQt()) && StringUtil.isNotEmpty(result.getLastDayChargeQt()) && result.getLastDayChargeQt() > 0) {
                result.setLastDayEff(getToDouble(result.getLastDayDischargeQt() / result.getLastDayChargeQt() * 100));
            }

            //计算充放电倍率(pcs总额定功率 / 电池总额定容量)
            if (StringUtil.isNotEmpty(result.getPcsTotalPower()) && StringUtil.isNotEmpty(result.getBatteryTotalCapacity()) && result.getBatteryTotalCapacity() > 0) {
                result.setChargeMagnification(getToDouble(result.getPcsTotalPower() / result.getBatteryTotalCapacity()));
            }

            //获取扩展属性字段数据
            List<DeviceReaDto> siteReaList = siteInfoDto.getSiteReaList();
            if (CollectionUtils.isNotEmpty(siteReaList)) {
                Map<String, DeviceReaDto> deviceReaDtoMap = siteReaList.stream().collect(Collectors.toMap(DeviceReaDto::getFieldName, deviceReaDto -> deviceReaDto, (k1, k2) -> k1));
                //电池包数量
                if (deviceReaDtoMap.containsKey(SiteFieldParamVo.BATTERY_PACK_NUM) && StringUtil.isNotEmpty(deviceReaDtoMap.get(SiteFieldParamVo.BATTERY_PACK_NUM).getValue())) {
                    result.setBatteryPackNum(Integer.valueOf(String.valueOf(deviceReaDtoMap.get(SiteFieldParamVo.BATTERY_PACK_NUM).getValue())));
                }
                //电芯数量
                if (deviceReaDtoMap.containsKey(SiteFieldParamVo.BATTERY_CELL_NUM) && StringUtil.isNotEmpty(deviceReaDtoMap.get(SiteFieldParamVo.BATTERY_CELL_NUM).getValue())) {
                    result.setBatteryCellNum(Integer.valueOf(String.valueOf(deviceReaDtoMap.get(SiteFieldParamVo.BATTERY_CELL_NUM).getValue())));
                }
            }
            //计算累计充放电循环次数(累计充电量 / 电池总额定容量)
            if (StringUtil.isNotEmpty(result.getSumChargeQt()) && StringUtil.isNotEmpty(result.getBatteryTotalCapacity()) && result.getBatteryTotalCapacity() > 0) {
                result.setSumChargeCycleNum((int) Math.floor(result.getSumChargeQt() / result.getBatteryTotalCapacity()));
            }
        }
        return ResponseResult.ok(result);
    }

    @Override
    public ResponseResult<List<PcsMonitorListDto>> findPcsMonitorList(String siteId) {
        List<PcsMonitorListDto> resultList = Lists.newArrayList();
        //根据站点id，查询站点下设备列表数量
        ResponseResult<Map<String, List<DeviceBasicInfoDto>>> deviceBasicInfoBySiteIds = deviceService.findDeviceBasicInfoBySiteIds(Collections.singletonList(siteId), null);
        if (deviceBasicInfoBySiteIds.isSuccess() && !deviceBasicInfoBySiteIds.getData().isEmpty() && deviceBasicInfoBySiteIds.getData().containsKey(siteId)) {
            List<DeviceBasicInfoDto> deviceBasicInfoDtoList = deviceBasicInfoBySiteIds.getData().get(siteId);
            //根据设备类型id，过滤出PCS设备信息
            List<DeviceBasicInfoDto> pcsDeviceInfoDtoList = deviceBasicInfoDtoList.stream().filter(d -> StringUtil.isNotEmpty(d.getTypeId()) && "23".equals(d.getTypeId())).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(pcsDeviceInfoDtoList)) {

                //过滤出电池簇设备信息，并以父级id分组
                List<DeviceBasicInfoDto> batteryDeviceInfoList = deviceBasicInfoDtoList.stream().filter(d -> StringUtil.isNotEmpty(d.getTypeId()) && "25".equals(d.getTypeId())).collect(Collectors.toList());
                Map<String, List<DeviceBasicInfoDto>> batteryDeviceMap = Maps.newHashMap();
                if (CollectionUtils.isNotEmpty(batteryDeviceInfoList)) {
                    batteryDeviceMap = batteryDeviceInfoList.stream().filter(d -> StringUtil.isNotEmpty(d.getParentId())).collect(Collectors.groupingBy(DeviceBasicInfoDto::getParentId));
                }
                //根据多个设备id，和多个功能点标识查询设备功能点实时数据
                String functionLogos = String.join(FileUtil.COMMA, FunctionLogoParamVo.PCS_ACTIVE_POWER, FunctionLogoParamVo.PCS_OPERATIVE_MODE);
                ResponseResult<Map<String, Map<String, RealDataModel>>> realDataMapResult = deviceService.getDeviceFunctionsRealDataByIds(
                        pcsDeviceInfoDtoList.stream().map(DeviceBasicInfoDto::getId).collect(Collectors.toSet()), functionLogos, 2);

                //根据设备信息，查询设备通信状态
                Map<String, Integer> txStatusMap = deviceService.findDeviceTxStatus(pcsDeviceInfoDtoList).getData();

                //获取昨日开始结束时间，并查询计算节点昨日充放电量
                String lastDayStartTime = getStartTimeByQueryType(1);
                //获取昨日结束时间
                String lastDayEndTime = getEndTimeByQueryType(1);
                DeviceHistoryQueryVo deviceHistoryQueryVo = new DeviceHistoryQueryVo();
                deviceHistoryQueryVo.setDeviceIds(batteryDeviceInfoList.stream().map(DeviceBasicInfoDto::getId).collect(Collectors.toSet()));
                deviceHistoryQueryVo.setFunctionLogos(new HashSet<>(Arrays.asList(FunctionLogoParamVo.ACCCHARGEQ, FunctionLogoParamVo.ACCDISCHARGEQ)));
                deviceHistoryQueryVo.setStartTime(lastDayStartTime);
                deviceHistoryQueryVo.setEndTime(lastDayEndTime);
                deviceHistoryQueryVo.setTimeInterval("1d");
                ResponseResult<Map<String, Map<String, List<NodeDifHistoryDto>>>> deviceHistoryResult = dataService.findNodeDifHistoryListFeign(deviceHistoryQueryVo);

                Map<String, List<DeviceBasicInfoDto>> finalBatteryDeviceMap = batteryDeviceMap;
                resultList = pcsDeviceInfoDtoList.stream().map(deviceBasicInfoDto -> {
                    PcsMonitorListDto pcsMonitorListDto = new PcsMonitorListDto();
                    BeanUtils.copyProperties(deviceBasicInfoDto, pcsMonitorListDto);
                    Map<String, Object> reaMap = deviceBasicInfoDto.getReaMap();
                    //设备型号
                    if (!reaMap.isEmpty() && reaMap.containsKey(ReaFieldParamVo.MODEL) && StringUtil.isNotEmpty(reaMap.get(ReaFieldParamVo.MODEL))) {
                        pcsMonitorListDto.setModel(String.valueOf(reaMap.get(ReaFieldParamVo.MODEL)));
                    }
                    //额定功率
                    if (!reaMap.isEmpty() && reaMap.containsKey(ReaFieldParamVo.RATED_POWER) && StringUtil.isNotEmpty(reaMap.get(ReaFieldParamVo.RATED_POWER))) {
                        pcsMonitorListDto.setPower(Double.parseDouble(String.valueOf(reaMap.get(ReaFieldParamVo.RATED_POWER))));
                    }
                    //生产厂家
                    if (!reaMap.isEmpty() && reaMap.containsKey(ReaFieldParamVo.MANUFACTURER_NAME) && StringUtil.isNotEmpty(reaMap.get(ReaFieldParamVo.MANUFACTURER_NAME))) {
                        pcsMonitorListDto.setManufacturerName(String.valueOf(reaMap.get(ReaFieldParamVo.MANUFACTURER_NAME)));
                    }

                    //获取设备通信状态
                    if (!txStatusMap.isEmpty() && txStatusMap.containsKey(deviceBasicInfoDto.getId())) {
                        pcsMonitorListDto.setTxStatus(txStatusMap.get(deviceBasicInfoDto.getId()));
                    }

                    //获取设备功能点实时数据
                    if (realDataMapResult.isSuccess() && !realDataMapResult.getData().isEmpty() && realDataMapResult.getData().containsKey(deviceBasicInfoDto.getId())) {
                        Map<String, RealDataModel> realDataModelMap = realDataMapResult.getData().get(deviceBasicInfoDto.getId());
                        //有功功率
                        if (realDataModelMap.containsKey(FunctionLogoParamVo.PCS_ACTIVE_POWER) && StringUtil.isNotEmpty(realDataModelMap.get(FunctionLogoParamVo.PCS_ACTIVE_POWER))) {
                            pcsMonitorListDto.setActivePower(getToDouble(Double.parseDouble(String.valueOf(realDataModelMap.get(FunctionLogoParamVo.PCS_ACTIVE_POWER).getDataValue()))));
                        }
                        //运行状态
                        if (realDataModelMap.containsKey(FunctionLogoParamVo.PCS_OPERATIVE_MODE) && StringUtil.isNotEmpty(realDataModelMap.get(FunctionLogoParamVo.PCS_OPERATIVE_MODE))) {
                            pcsMonitorListDto.setPcsOperativeMode(Integer.parseInt(String.valueOf(realDataModelMap.get(FunctionLogoParamVo.PCS_OPERATIVE_MODE).getDataValue())));
                            pcsMonitorListDto.setRunStateTime(realDataModelMap.get(FunctionLogoParamVo.PCS_OPERATIVE_MODE).getDateTime());
                        }
                    }

                    //获取pcs下级电池簇设备信息
                    if (!finalBatteryDeviceMap.isEmpty() && finalBatteryDeviceMap.containsKey(deviceBasicInfoDto.getId()) &&
                            CollectionUtils.isNotEmpty(finalBatteryDeviceMap.get(deviceBasicInfoDto.getId()))) {
                        //获取电池簇数量
                        pcsMonitorListDto.setBatteryNum(finalBatteryDeviceMap.get(deviceBasicInfoDto.getId()).size());

                        List<DeviceBasicInfoDto> batteryInfoDtoList = finalBatteryDeviceMap.get(deviceBasicInfoDto.getId());
                        //获取pcs昨日充电放电量，计算昨日充放电效率(昨日放电量 / 昨日充电量)
                        if (deviceHistoryResult.isSuccess() && !deviceHistoryResult.getData().isEmpty()) {
                            Map<String, Map<String, List<NodeDifHistoryDto>>> nodeDifHistoryMap = deviceHistoryResult.getData();
                            // 存储所有充电量历史数据
                            List<NodeDifHistoryDto> sumChargeList = Lists.newArrayList();
                            // 存储所有放电量历史数据
                            List<NodeDifHistoryDto> sumDischargeList = Lists.newArrayList();
                            //循环当前pcs下级所有电池簇设备，获取所有电池簇历史充电放电量数据
                            batteryInfoDtoList.forEach(deviceInfoDto -> {
                                if (nodeDifHistoryMap.containsKey(deviceInfoDto.getId()) && !nodeDifHistoryMap.get(deviceInfoDto.getId()).isEmpty()) {
                                    Map<String, List<NodeDifHistoryDto>> nodeDiffMap = nodeDifHistoryMap.get(deviceInfoDto.getId());
                                    if (!nodeDiffMap.isEmpty() && nodeDiffMap.containsKey(FunctionLogoParamVo.ACCCHARGEQ) &&
                                            CollectionUtils.isNotEmpty(nodeDiffMap.get(FunctionLogoParamVo.ACCCHARGEQ))) {
                                        sumChargeList.addAll(nodeDiffMap.get(FunctionLogoParamVo.ACCCHARGEQ));
                                    }
                                    if (!nodeDiffMap.isEmpty() && nodeDiffMap.containsKey(FunctionLogoParamVo.ACCDISCHARGEQ) &&
                                            CollectionUtils.isNotEmpty(nodeDiffMap.get(FunctionLogoParamVo.ACCDISCHARGEQ))) {
                                        sumDischargeList.addAll(nodeDiffMap.get(FunctionLogoParamVo.ACCDISCHARGEQ));
                                    }
                                }
                            });
                            //昨日充电量
                            if (CollectionUtils.isNotEmpty(sumChargeList)) {
                                pcsMonitorListDto.setLastDayChargeQt(getToDouble(sumChargeList.stream()
                                        .filter(n -> StringUtil.isNotEmpty(n.getLastDataValue()) && StringUtil.isNotEmpty(n.getFirstDataValue()))
                                        .mapToDouble(n -> Double.parseDouble(String.valueOf(n.getLastDataValue())) - Double.parseDouble(String.valueOf(n.getFirstDataValue())))
                                        .sum()));
                            }
                            //昨日放电量
                            if (CollectionUtils.isNotEmpty(sumDischargeList)) {
                                pcsMonitorListDto.setLastDayDischargeQt(getToDouble(sumDischargeList.stream()
                                        .filter(n -> StringUtil.isNotEmpty(n.getLastDataValue()) && StringUtil.isNotEmpty(n.getFirstDataValue()))
                                        .mapToDouble(n -> Double.parseDouble(String.valueOf(n.getLastDataValue())) - Double.parseDouble(String.valueOf(n.getFirstDataValue())))
                                        .sum()));
                            }
                            //计算昨日充放电效率
                            if (StringUtil.isNotEmpty(pcsMonitorListDto.getLastDayChargeQt()) && StringUtil.isNotEmpty(pcsMonitorListDto.getLastDayDischargeQt()) && pcsMonitorListDto.getLastDayChargeQt() > 0) {
                                pcsMonitorListDto.setLastDayEff(getToDouble(pcsMonitorListDto.getLastDayDischargeQt() / pcsMonitorListDto.getLastDayChargeQt() * 100));
                            }
                        }
                    }

                    return pcsMonitorListDto;
                }).collect(Collectors.toList());
            }
        }
        return ResponseResult.ok(resultList);
    }

    @Override
    public ResponseResult<List<BatteryMonitorListDto>> findBatteryMonitorList(String siteId) {
        List<BatteryMonitorListDto> resultList = Lists.newArrayList();
        //根据站点id，查询站点下设备列表数量
        ResponseResult<Map<String, List<DeviceBasicInfoDto>>> deviceBasicInfoBySiteIds = deviceService.findDeviceBasicInfoBySiteIds(Collections.singletonList(siteId), null);
        if (deviceBasicInfoBySiteIds.isSuccess() && !deviceBasicInfoBySiteIds.getData().isEmpty() && deviceBasicInfoBySiteIds.getData().containsKey(siteId)) {
            List<DeviceBasicInfoDto> deviceBasicInfoDtoList = deviceBasicInfoBySiteIds.getData().get(siteId);
            //根据设备类型id，过滤出电池簇设备信息
            List<DeviceBasicInfoDto> batteryDeviceInfoDtoList = deviceBasicInfoDtoList.stream().filter(d -> StringUtil.isNotEmpty(d.getTypeId()) && "25".equals(d.getTypeId())).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(batteryDeviceInfoDtoList)) {

                //根据多个设备id，和多个功能点标识查询设备功能点实时数据
                String functionLogos = String.join(FileUtil.COMMA, FunctionLogoParamVo.CHARGESTATE, FunctionLogoParamVo.BATTERY_TOTAL_SOC, FunctionLogoParamVo.BATTERY_TOTAL_VOLTAGE);
                ResponseResult<Map<String, Map<String, RealDataModel>>> realDataMapResult = deviceService.getDeviceFunctionsRealDataByIds(
                        batteryDeviceInfoDtoList.stream().map(DeviceBasicInfoDto::getId).collect(Collectors.toSet()), functionLogos, 2);

                //根据设备信息，查询设备通信状态
                Map<String, Integer> txStatusMap = deviceService.findDeviceTxStatus(batteryDeviceInfoDtoList).getData();

                resultList = batteryDeviceInfoDtoList.stream().map(deviceBasicInfoDto -> {
                    BatteryMonitorListDto batteryMonitorListDto = new BatteryMonitorListDto();
                    BeanUtils.copyProperties(deviceBasicInfoDto, batteryMonitorListDto);
                    Map<String, Object> reaMap = deviceBasicInfoDto.getReaMap();
                    //设备型号
                    if (!reaMap.isEmpty() && reaMap.containsKey(ReaFieldParamVo.MODEL) && StringUtil.isNotEmpty(reaMap.get(ReaFieldParamVo.MODEL))) {
                        batteryMonitorListDto.setModel(String.valueOf(reaMap.get(ReaFieldParamVo.MODEL)));
                    }
                    //额定容量
                    if (!reaMap.isEmpty() && reaMap.containsKey(ReaFieldParamVo.RATED_CAP) && StringUtil.isNotEmpty(reaMap.get(ReaFieldParamVo.RATED_CAP))) {
                        batteryMonitorListDto.setRatedCap(Double.parseDouble(String.valueOf(reaMap.get(ReaFieldParamVo.RATED_CAP))));
                    }
                    //生产厂家
                    if (!reaMap.isEmpty() && reaMap.containsKey(ReaFieldParamVo.MANUFACTURER_NAME) && StringUtil.isNotEmpty(reaMap.get(ReaFieldParamVo.MANUFACTURER_NAME))) {
                        batteryMonitorListDto.setManufacturerName(String.valueOf(reaMap.get(ReaFieldParamVo.MANUFACTURER_NAME)));
                    }
                    //电芯数量
                    if (!reaMap.isEmpty() && reaMap.containsKey(ReaFieldParamVo.CELL_NUM) && StringUtil.isNotEmpty(reaMap.get(ReaFieldParamVo.CELL_NUM))) {
                        batteryMonitorListDto.setCellNum(Integer.parseInt(String.valueOf(reaMap.get(ReaFieldParamVo.CELL_NUM))));
                    }

                    //获取设备通信状态
                    if (!txStatusMap.isEmpty() && txStatusMap.containsKey(deviceBasicInfoDto.getId())) {
                        batteryMonitorListDto.setTxStatus(txStatusMap.get(deviceBasicInfoDto.getId()));
                    }

                    //获取设备功能点实时数据
                    if (realDataMapResult.isSuccess() && !realDataMapResult.getData().isEmpty() && realDataMapResult.getData().containsKey(deviceBasicInfoDto.getId())) {
                        Map<String, RealDataModel> realDataModelMap = realDataMapResult.getData().get(deviceBasicInfoDto.getId());
                        //soc状态
                        if (realDataModelMap.containsKey(FunctionLogoParamVo.BATTERY_TOTAL_SOC) && StringUtil.isNotEmpty(realDataModelMap.get(FunctionLogoParamVo.BATTERY_TOTAL_SOC))) {
                            batteryMonitorListDto.setSoc(getToDouble(Double.parseDouble(String.valueOf(realDataModelMap.get(FunctionLogoParamVo.BATTERY_TOTAL_SOC).getDataValue()))));
                        }
                        //电池总电压
                        if (realDataModelMap.containsKey(FunctionLogoParamVo.BATTERY_TOTAL_VOLTAGE) && StringUtil.isNotEmpty(realDataModelMap.get(FunctionLogoParamVo.BATTERY_TOTAL_VOLTAGE))) {
                            batteryMonitorListDto.setBatterytotalvoltage(getToDouble(Double.parseDouble(String.valueOf(realDataModelMap.get(FunctionLogoParamVo.BATTERY_TOTAL_VOLTAGE).getDataValue()))));
                        }
                        //充放电状态
                        if (realDataModelMap.containsKey(FunctionLogoParamVo.CHARGESTATE) && StringUtil.isNotEmpty(realDataModelMap.get(FunctionLogoParamVo.CHARGESTATE))) {
                            batteryMonitorListDto.setChargestate(Integer.parseInt(String.valueOf(realDataModelMap.get(FunctionLogoParamVo.CHARGESTATE).getDataValue())));
                            batteryMonitorListDto.setChargeStateTime(realDataModelMap.get(FunctionLogoParamVo.CHARGESTATE).getDateTime());
                        }
                    }

                    return batteryMonitorListDto;
                }).collect(Collectors.toList());
            }
        }
        return ResponseResult.ok(resultList);
    }

    @Override
    public ResponseResult<PageDto<Object>> findCellListByPage(String deviceId, Integer queryType, Integer page, Integer size) {
        List<Object> resultList = Lists.newArrayList();
        //根据设备id，和功能点标识查询设备功能点实时数据
        String functionLogo;
        if (queryType == 1) {
            functionLogo = FunctionLogoParamVo.CELL_TEMP;
        } else {
            functionLogo = FunctionLogoParamVo.CELL_VOLTAGE;
        }
        ResponseResult<Map<String, Map<String, RealDataModel>>> realDataMapResult = deviceService.getDeviceFunctionsRealDataByIds(Collections.singleton(deviceId),
                functionLogo, 2);
        if (realDataMapResult.isSuccess() && !realDataMapResult.getData().isEmpty() && realDataMapResult.getData().containsKey(deviceId)) {
            Map<String, RealDataModel> realDataModelMap = realDataMapResult.getData().get(deviceId);
            if (!realDataModelMap.isEmpty() && realDataModelMap.containsKey(functionLogo)) {
                RealDataModel realDataModel = realDataModelMap.get(functionLogo);
                if (StringUtil.isNotEmpty(realDataModel.getDataValue())) {
                    // 创建 Gson 实例
                    Gson gson = new Gson();
                    // 定义类型
                    Type listType = new TypeToken<List<Double>>() {
                    }.getType();
                    // 转换为 List
                    resultList = gson.fromJson(String.valueOf(realDataModel.getDataValue()), listType);
                }
            }
        }
        return ResponseResult.ok(new PageDto<>(resultList, page, size));
    }

    @Override
    public ResponseResult<List<AuxiliaryMonitorListDto>> findAuxiliaryMonitorList(String siteId) {
        List<AuxiliaryMonitorListDto> resultList = Lists.newArrayList();
        //根据站点id，查询站点下设备列表数量
        ResponseResult<Map<String, List<DeviceBasicInfoDto>>> deviceBasicInfoBySiteIds = deviceService.findDeviceBasicInfoBySiteIds(Collections.singletonList(siteId), null);
        if (deviceBasicInfoBySiteIds.isSuccess() && !deviceBasicInfoBySiteIds.getData().isEmpty() && deviceBasicInfoBySiteIds.getData().containsKey(siteId)) {
            List<DeviceBasicInfoDto> deviceBasicInfoDtoList = deviceBasicInfoBySiteIds.getData().get(siteId);
            //根据设备类型id，过滤出辅助设备信息
            List<DeviceBasicInfoDto> deviceInfoDtoList = deviceBasicInfoDtoList.stream().filter(d -> StringUtil.isNotEmpty(d.getTypeId()) && "60".equals(d.getTypeId())).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(deviceInfoDtoList)) {

                //根据多个设备id，和多个功能点标识查询设备功能点实时数据
                String functionLogos = String.join(",", FunctionLogoParamVo.CABINET_TEMPERATURE, FunctionLogoParamVo.CABINET_HUMIDITY, FunctionLogoParamVo.COOLING_STATE,
                        FunctionLogoParamVo.HEATING_STATE, FunctionLogoParamVo.INTERNALFAN_STATE, FunctionLogoParamVo.EXTERNALFAN_STATE);
                ResponseResult<Map<String, Map<String, RealDataModel>>> realDataMapResult = deviceService.getDeviceFunctionsRealDataByIds(
                        deviceInfoDtoList.stream().map(DeviceBasicInfoDto::getId).collect(Collectors.toSet()), functionLogos, 2);

                //根据设备信息，查询设备通信状态
                Map<String, Integer> txStatusMap = deviceService.findDeviceTxStatus(deviceInfoDtoList).getData();

                resultList = deviceInfoDtoList.stream().map(deviceBasicInfoDto -> {
                    AuxiliaryMonitorListDto auxiliaryMonitorListDto = new AuxiliaryMonitorListDto();
                    BeanUtils.copyProperties(deviceBasicInfoDto, auxiliaryMonitorListDto);
                    Map<String, Object> reaMap = deviceBasicInfoDto.getReaMap();
                    //设备型号
                    if (!reaMap.isEmpty() && reaMap.containsKey(ReaFieldParamVo.MODEL) && StringUtil.isNotEmpty(reaMap.get(ReaFieldParamVo.MODEL))) {
                        auxiliaryMonitorListDto.setModel(String.valueOf(reaMap.get(ReaFieldParamVo.MODEL)));
                    }
                    //生产厂家
                    if (!reaMap.isEmpty() && reaMap.containsKey(ReaFieldParamVo.MANUFACTURER_NAME) && StringUtil.isNotEmpty(reaMap.get(ReaFieldParamVo.MANUFACTURER_NAME))) {
                        auxiliaryMonitorListDto.setManufacturerName(String.valueOf(reaMap.get(ReaFieldParamVo.MANUFACTURER_NAME)));
                    }

                    //获取设备通信状态
                    if (!txStatusMap.isEmpty() && txStatusMap.containsKey(deviceBasicInfoDto.getId())) {
                        auxiliaryMonitorListDto.setTxStatus(txStatusMap.get(deviceBasicInfoDto.getId()));
                    }

                    //获取设备功能点实时数据
                    if (realDataMapResult.isSuccess() && !realDataMapResult.getData().isEmpty() && realDataMapResult.getData().containsKey(deviceBasicInfoDto.getId())) {
                        Map<String, RealDataModel> realDataModelMap = realDataMapResult.getData().get(deviceBasicInfoDto.getId());
                        //柜内温度
                        if (realDataModelMap.containsKey(FunctionLogoParamVo.CABINET_TEMPERATURE) && StringUtil.isNotEmpty(realDataModelMap.get(FunctionLogoParamVo.CABINET_TEMPERATURE))) {
                            auxiliaryMonitorListDto.setCabinetTemp(getToDouble(Double.parseDouble(String.valueOf(realDataModelMap.get(FunctionLogoParamVo.CABINET_TEMPERATURE).getDataValue()))));
                        }
                        //柜内湿度
                        if (realDataModelMap.containsKey(FunctionLogoParamVo.CABINET_HUMIDITY) && StringUtil.isNotEmpty(realDataModelMap.get(FunctionLogoParamVo.CABINET_HUMIDITY))) {
                            auxiliaryMonitorListDto.setCabinetHumidity(getToDouble(Double.parseDouble(String.valueOf(realDataModelMap.get(FunctionLogoParamVo.CABINET_HUMIDITY).getDataValue()))));
                        }
                        //制冷状态
                        if (realDataModelMap.containsKey(FunctionLogoParamVo.COOLING_STATE) && StringUtil.isNotEmpty(realDataModelMap.get(FunctionLogoParamVo.COOLING_STATE))) {
                            auxiliaryMonitorListDto.setCoolingState(Integer.parseInt(String.valueOf(realDataModelMap.get(FunctionLogoParamVo.COOLING_STATE).getDataValue())));
                        }
                        //加热器状态
                        if (realDataModelMap.containsKey(FunctionLogoParamVo.HEATING_STATE) && StringUtil.isNotEmpty(realDataModelMap.get(FunctionLogoParamVo.HEATING_STATE))) {
                            auxiliaryMonitorListDto.setHeatingState(Integer.parseInt(String.valueOf(realDataModelMap.get(FunctionLogoParamVo.HEATING_STATE).getDataValue())));
                        }
                        //内风机状态
                        if (realDataModelMap.containsKey(FunctionLogoParamVo.INTERNALFAN_STATE) && StringUtil.isNotEmpty(realDataModelMap.get(FunctionLogoParamVo.INTERNALFAN_STATE))) {
                            auxiliaryMonitorListDto.setInternalfanState(Integer.parseInt(String.valueOf(realDataModelMap.get(FunctionLogoParamVo.INTERNALFAN_STATE).getDataValue())));
                        }
                        //外风机状态
                        if (realDataModelMap.containsKey(FunctionLogoParamVo.EXTERNALFAN_STATE) && StringUtil.isNotEmpty(realDataModelMap.get(FunctionLogoParamVo.EXTERNALFAN_STATE))) {
                            auxiliaryMonitorListDto.setExternalfanState(Integer.parseInt(String.valueOf(realDataModelMap.get(FunctionLogoParamVo.EXTERNALFAN_STATE).getDataValue())));
                        }
                    }

                    return auxiliaryMonitorListDto;
                }).collect(Collectors.toList());
            }
        }
        return ResponseResult.ok(resultList);
    }

    @Override
    public ResponseResult<PileFunCurveDto> findPvSitePowerCurve(String siteIds, String startTime, String endTime) {
        PileFunCurveDto result = new PileFunCurveDto();
        if (StringUtil.isEmpty(siteIds) || StringUtil.isEmpty(startTime) || StringUtil.isEmpty(endTime)) {
            return ResponseResult.error(ResponseResult.PARAM_ERROR);
        }
        List<String> siteIdList = JSON.parseArray(siteIds, String.class);
        //存储数据列表
        List<PileFunCurveDto.DataInfo> dataInfoList = Lists.newArrayList();
        //获取今日每分钟时间轴
        List<String> getDateTimeBetween = getLocalDateTimeBetween(strToLocalDateTime(startTime), strToLocalDateTime(endTime), "1m").stream().map(DateUtil::localDateTimeToStr).collect(Collectors.toList());
        result.setXAXisList(getDateTimeBetween.stream().map(e -> e.substring(11, 19)).collect(Collectors.toList()));

        //根据站点id，查询站点下所有设备列表
        ResponseResult<Map<String, List<DeviceBasicInfoDto>>> deviceBasicInfoBySiteIds = deviceService.findDeviceBasicInfoBySiteIds(siteIdList, null);
        if (deviceBasicInfoBySiteIds.isSuccess() && !deviceBasicInfoBySiteIds.getData().isEmpty()) {
            List<DeviceBasicInfoDto> deviceBasicInfoDtos = Lists.newArrayList();
            //过滤出逆变器设备
            deviceBasicInfoBySiteIds.getData().forEach((k, v) -> deviceBasicInfoDtos.addAll(v.stream().filter(d ->
                    StringUtil.isNotEmpty(d.getTypeId()) && "20".equals(d.getTypeId())).collect(Collectors.toList())));
            if (CollectionUtils.isNotEmpty(deviceBasicInfoDtos)) {

                DeviceHistoryQueryVo deviceHistoryQueryVo = new DeviceHistoryQueryVo();
                deviceHistoryQueryVo.setDeviceIds(deviceBasicInfoDtos.stream().map(DeviceBasicInfoDto::getId).collect(Collectors.toSet()));
                deviceHistoryQueryVo.setFunctionLogos(Collections.singleton(FunctionLogoParamVo.ACTIVE_POWER));
                deviceHistoryQueryVo.setStartTime(startTime);
                deviceHistoryQueryVo.setEndTime(endTime);
                deviceHistoryQueryVo.setTimeInterval("1m");
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
                        //实际功率
                        PileFunCurveDto.DataInfo dataInfo = new PileFunCurveDto.DataInfo();
                        dataInfo.setName("实际功率");
                        dataInfo.setEName("realPowerList");
                        List<Object> dataList = Lists.newArrayList();

                        //理论功率
                        PileFunCurveDto.DataInfo theoryPowerInfo = new PileFunCurveDto.DataInfo();
                        theoryPowerInfo.setName("理论功率");
                        theoryPowerInfo.setEName("theoryPowerList");
                        List<Object> theoryPowerList = Lists.newArrayList();

                        //短期预测功率
                        PileFunCurveDto.DataInfo shortForecastPowerInfo = new PileFunCurveDto.DataInfo();
                        shortForecastPowerInfo.setName("短期预测功率");
                        shortForecastPowerInfo.setEName("shortForecastPowerList");
                        List<Object> shortForecastPowerList = Lists.newArrayList();

                        //根据时间分组
                        Map<String, List<DeviceHistoryDto>> groupByDateMap = deviceHistoryDtoList.stream().collect(Collectors.groupingBy(DeviceHistoryDto::getDateTime));
                        getDateTimeBetween.forEach(dateTime -> {
                            if (groupByDateMap.containsKey(dateTime)) {
                                dataList.add(getToDouble(groupByDateMap.get(dateTime).stream().filter(d -> StringUtil.isNotEmpty(d.getDataValue())).mapToDouble(d -> Double.parseDouble(String.valueOf(d.getDataValue()))).sum()));
                            } else {
                                dataList.add(null);
                            }
                        });
                        dataInfo.setDataList(dataList);
                        theoryPowerInfo.setDataList(theoryPowerList);
                        shortForecastPowerInfo.setDataList(shortForecastPowerList);

                        dataInfoList.add(dataInfo);
                        dataInfoList.add(theoryPowerInfo);
                        dataInfoList.add(shortForecastPowerInfo);
                    }
                }
            }
        }
        result.setDataInfoList(dataInfoList);
        return ResponseResult.ok(result);
    }

    @Override
    public ResponseResult<PileFunCurveDto> findPvSiteQtCurve(String siteIds, Integer queryType, String startTime, String endTime) {
        PileFunCurveDto result = new PileFunCurveDto();
        if (StringUtil.isEmpty(siteIds) || StringUtil.isEmpty(startTime) || StringUtil.isEmpty(endTime) || StringUtil.isEmpty(queryType)) {
            return ResponseResult.error(ResponseResult.PARAM_ERROR);
        }
        List<String> siteIdList = JSON.parseArray(siteIds, String.class);
        //存储数据列表
        List<PileFunCurveDto.DataInfo> dataInfoList = Lists.newArrayList();
        //根据开始结束时间查询时间轴列表
        List<String> dateBetween = getDateBetween(queryType, startTime.substring(0, 10), endTime.substring(0, 10));
        String functionLogo = null;
        String timeInterval = null;
        if (queryType == 1) {
            timeInterval = "1d";
            functionLogo = FunctionLogoParamVo.TOTAL_POWER_GENERATION;
        } else if (queryType == 2) {
            timeInterval = "1n";
            functionLogo = FunctionLogoParamVo.TOTAL_POWER_GENERATION;
        } else if (queryType == 3) {
            timeInterval = "1y";
            functionLogo = FunctionLogoParamVo.TOTAL_POWER_GENERATION;
        }
        result.setXAXisList(dateBetween);

        //根据站点id，查询站点下所有设备列表
        ResponseResult<Map<String, List<DeviceBasicInfoDto>>> deviceBasicInfoBySiteIds = deviceService.findDeviceBasicInfoBySiteIds(siteIdList, null);
        if (deviceBasicInfoBySiteIds.isSuccess() && !deviceBasicInfoBySiteIds.getData().isEmpty()) {
            List<DeviceBasicInfoDto> deviceBasicInfoDtos = Lists.newArrayList();
            //过滤出逆变器设备
            deviceBasicInfoBySiteIds.getData().forEach((k, v) -> deviceBasicInfoDtos.addAll(v.stream().filter(d ->
                    StringUtil.isNotEmpty(d.getTypeId()) && "20".equals(d.getTypeId())).collect(Collectors.toList())));
            if (CollectionUtils.isNotEmpty(deviceBasicInfoDtos)) {
                DeviceHistoryQueryVo deviceHistoryQueryVo = new DeviceHistoryQueryVo();
                deviceHistoryQueryVo.setDeviceIds(deviceBasicInfoDtos.stream().map(DeviceBasicInfoDto::getId).collect(Collectors.toSet()));
                deviceHistoryQueryVo.setFunctionLogos(Collections.singleton(functionLogo));
                deviceHistoryQueryVo.setStartTime(startTime);
                deviceHistoryQueryVo.setEndTime(endTime);
                deviceHistoryQueryVo.setTimeInterval(timeInterval);
                ResponseResult<Map<String, Map<String, List<NodeDifHistoryDto>>>> deviceHistoryResult = dataService.findNodeDifHistoryListFeign(deviceHistoryQueryVo);
                if (deviceHistoryResult.isSuccess() && !deviceHistoryResult.getData().isEmpty()) {
                    // 存储所有历史数据
                    List<NodeDifHistoryDto> deviceHistoryDtoList = deviceHistoryResult.getData().values().stream()
                            .filter(deviceHistoryMap -> !deviceHistoryMap.isEmpty())
                            .flatMap(deviceHistoryMap -> deviceHistoryMap.values().stream())
                            .flatMap(List::stream)
                            .collect(Collectors.toList());

                    if (CollectionUtils.isNotEmpty(deviceHistoryDtoList)) {
                        PileFunCurveDto.DataInfo dataInfo = new PileFunCurveDto.DataInfo();
                        List<Object> dataList = Lists.newArrayList();

                        //根据时间类型格式，以first时间分组
                        Map<String, List<NodeDifHistoryDto>> groupByDateMap = groupByDateTime(deviceHistoryDtoList, queryType);

                        //循环时间获取数据
                        dateBetween.forEach(dateTime -> {
                            if (groupByDateMap.containsKey(dateTime)) {
                                dataList.add(getToDouble(groupByDateMap.get(dateTime).stream().mapToDouble(d -> DoubleUtil.getObjSub(d.getFirstDataValue(), d.getLastDataValue())).sum()));
                            } else {
                                dataList.add(null);
                            }
                        });
                        dataInfo.setName("实际发电量");
                        dataInfo.setEName("realQtList");
                        dataInfo.setDataList(dataList);
                        dataInfoList.add(dataInfo);
                    }
                }
            }
        }
        result.setDataInfoList(dataInfoList);
        return ResponseResult.ok(result);
    }

    @Override
    public ResponseResult<PileFunCurveDto> findChargeSitePowerCurve(String siteIds, String startTime, String endTime) {
        PileFunCurveDto result = new PileFunCurveDto();
        if (StringUtil.isEmpty(siteIds) || StringUtil.isEmpty(startTime) || StringUtil.isEmpty(endTime)) {
            return ResponseResult.error(ResponseResult.PARAM_ERROR);
        }
        List<String> siteIdList = JSON.parseArray(siteIds, String.class);
        //存储数据列表
        List<PileFunCurveDto.DataInfo> dataInfoList = Lists.newArrayList();
        //获取今日每分钟时间轴
        List<String> getDateTimeBetween = getLocalDateTimeBetween(strToLocalDateTime(startTime), strToLocalDateTime(endTime), "1m").stream().map(DateUtil::localDateTimeToStr).collect(Collectors.toList());
        result.setXAXisList(getDateTimeBetween.stream().map(e -> e.substring(11, 19)).collect(Collectors.toList()));

        //根据站点id，查询站点下所有设备列表
        ResponseResult<Map<String, List<DeviceBasicInfoDto>>> deviceBasicInfoBySiteIds = deviceService.findDeviceBasicInfoBySiteIds(siteIdList, 1);
        if (deviceBasicInfoBySiteIds.isSuccess() && !deviceBasicInfoBySiteIds.getData().isEmpty()) {
            //所有电桩设备
            List<DeviceBasicInfoDto> deviceBasicInfoDtos = Lists.newArrayList();
            deviceBasicInfoBySiteIds.getData().forEach((k, v) -> deviceBasicInfoDtos.addAll(v));

            DeviceHistoryQueryVo deviceHistoryQueryVo = new DeviceHistoryQueryVo();
            deviceHistoryQueryVo.setDeviceIds(deviceBasicInfoDtos.stream().map(DeviceBasicInfoDto::getId).collect(Collectors.toSet()));
            deviceHistoryQueryVo.setFunctionLogos(new HashSet<>(Arrays.asList(FunctionLogoParamVo.PILE_CHARGEPOWER, FunctionLogoParamVo.PILE_DISCHARGEPOWER)));
            deviceHistoryQueryVo.setStartTime(startTime);
            deviceHistoryQueryVo.setEndTime(endTime);
            deviceHistoryQueryVo.setTimeInterval("1m");
            //查询逆变器功能点历史数据
            ResponseResult<Map<String, Map<String, List<DeviceHistoryDto>>>> deviceHistoryResult = dataService.findDeviceHistoryValueList(deviceHistoryQueryVo);
            if (deviceHistoryResult.isSuccess() && !deviceHistoryResult.getData().isEmpty()) {
                // 存储所有充电功率历史数据
                List<DeviceHistoryDto> chargePowerDataList = Lists.newArrayList();
                // 存储所有放电功率历史数据
                List<DeviceHistoryDto> dischargePowerDataList = Lists.newArrayList();
                deviceHistoryResult.getData().forEach((deviceId, deviceHistoryMap) -> {
                    if (!deviceHistoryMap.isEmpty() && deviceHistoryMap.containsKey(FunctionLogoParamVo.PILE_CHARGEPOWER) &&
                            CollectionUtils.isNotEmpty(deviceHistoryMap.get(FunctionLogoParamVo.PILE_CHARGEPOWER))) {
                        chargePowerDataList.addAll(deviceHistoryMap.get(FunctionLogoParamVo.PILE_CHARGEPOWER));
                    }
                    if (!deviceHistoryMap.isEmpty() && deviceHistoryMap.containsKey(FunctionLogoParamVo.PILE_DISCHARGEPOWER) &&
                            CollectionUtils.isNotEmpty(deviceHistoryMap.get(FunctionLogoParamVo.PILE_DISCHARGEPOWER))) {
                        dischargePowerDataList.addAll(deviceHistoryMap.get(FunctionLogoParamVo.PILE_DISCHARGEPOWER));
                    }
                });

                //根据时间分组充电功率数据
                Map<String, List<DeviceHistoryDto>> chargePowerDataMap = Maps.newHashMap();
                if (CollectionUtils.isNotEmpty(chargePowerDataList)) {
                    chargePowerDataMap = chargePowerDataList.stream().collect(Collectors.groupingBy(DeviceHistoryDto::getDateTime));
                }

                //根据时间分组放电功率数据
                Map<String, List<DeviceHistoryDto>> dischargePowerDataMap = Maps.newHashMap();
                if (CollectionUtils.isNotEmpty(dischargePowerDataList)) {
                    dischargePowerDataMap = dischargePowerDataList.stream().collect(Collectors.groupingBy(DeviceHistoryDto::getDateTime));
                }

                //充电曲线对象
                PileFunCurveDto.DataInfo chargeDataInfo = new PileFunCurveDto.DataInfo();
                chargeDataInfo.setName("充电功率");
                chargeDataInfo.setEName("chargePowerList");
                List<Object> chargeDataList = Lists.newArrayList();

                //放电曲线对象
                PileFunCurveDto.DataInfo dischargeDataInfo = new PileFunCurveDto.DataInfo();
                dischargeDataInfo.setName("放电功率");
                dischargeDataInfo.setEName("dischargePowerList");
                List<Object> dischargeDataList = Lists.newArrayList();

                Map<String, List<DeviceHistoryDto>> finalChargePowerDataMap = chargePowerDataMap;
                Map<String, List<DeviceHistoryDto>> finalDischargePowerDataMap = dischargePowerDataMap;
                getDateTimeBetween.forEach(dateTime -> {
                    //充电功率
                    if (!finalChargePowerDataMap.isEmpty() && finalChargePowerDataMap.containsKey(dateTime)) {
                        chargeDataList.add(getToDouble(finalChargePowerDataMap.get(dateTime).stream().filter(d -> StringUtil.isNotEmpty(d.getDataValue())).mapToDouble(d -> Double.parseDouble(String.valueOf(d.getDataValue()))).sum()));
                    } else {
                        chargeDataList.add(null);
                    }
                    //放电功率
                    if (!finalDischargePowerDataMap.isEmpty() && finalDischargePowerDataMap.containsKey(dateTime)) {
                        dischargeDataList.add(getToDouble(finalDischargePowerDataMap.get(dateTime).stream().filter(d -> StringUtil.isNotEmpty(d.getDataValue())).mapToDouble(d -> Double.parseDouble(String.valueOf(d.getDataValue()))).sum()));
                    } else {
                        dischargeDataList.add(null);
                    }
                });
                chargeDataInfo.setDataList(chargeDataList);
                dischargeDataInfo.setDataList(dischargeDataList);
                dataInfoList.add(chargeDataInfo);
                dataInfoList.add(dischargeDataInfo);
            }
        }
        result.setDataInfoList(dataInfoList);
        return ResponseResult.ok(result);
    }

    @Override
    public ResponseResult<PileFunCurveDto> findStorageSitePowerCurve(String siteIds, String startTime, String endTime) {
        PileFunCurveDto result = new PileFunCurveDto();
        if (StringUtil.isEmpty(siteIds) || StringUtil.isEmpty(startTime) || StringUtil.isEmpty(endTime)) {
            return ResponseResult.error(ResponseResult.PARAM_ERROR);
        }
        List<String> siteIdList = JSON.parseArray(siteIds, String.class);
        //存储数据列表
        List<PileFunCurveDto.DataInfo> dataInfoList = Lists.newArrayList();
        //获取今日每分钟时间轴
        List<String> getDateTimeBetween = getLocalDateTimeBetween(strToLocalDateTime(startTime), strToLocalDateTime(endTime), "1m").stream().map(DateUtil::localDateTimeToStr).collect(Collectors.toList());
        result.setXAXisList(getDateTimeBetween.stream().map(e -> e.substring(11, 19)).collect(Collectors.toList()));

        //根据站点id，查询站点下所有设备列表
        ResponseResult<Map<String, List<DeviceBasicInfoDto>>> deviceBasicInfoBySiteIds = deviceService.findDeviceBasicInfoBySiteIds(siteIdList, null);
        if (deviceBasicInfoBySiteIds.isSuccess() && !deviceBasicInfoBySiteIds.getData().isEmpty()) {
            List<DeviceBasicInfoDto> deviceBasicInfoDtos = Lists.newArrayList();
            //过滤出pcs设备
            deviceBasicInfoBySiteIds.getData().forEach((k, v) -> deviceBasicInfoDtos.addAll(v.stream().filter(d ->
                    StringUtil.isNotEmpty(d.getTypeId()) && "23".equals(d.getTypeId())).collect(Collectors.toList())));
            if (CollectionUtils.isNotEmpty(deviceBasicInfoDtos)) {

                DeviceHistoryQueryVo deviceHistoryQueryVo = new DeviceHistoryQueryVo();
                deviceHistoryQueryVo.setDeviceIds(deviceBasicInfoDtos.stream().map(DeviceBasicInfoDto::getId).collect(Collectors.toSet()));
                deviceHistoryQueryVo.setFunctionLogos(Collections.singleton(FunctionLogoParamVo.PCS_ACTIVE_POWER));
                deviceHistoryQueryVo.setStartTime(startTime);
                deviceHistoryQueryVo.setEndTime(endTime);
                deviceHistoryQueryVo.setTimeInterval("1m");
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
                        PileFunCurveDto.DataInfo dataInfo = new PileFunCurveDto.DataInfo();
                        List<Object> dataList = Lists.newArrayList();
                        //根据时间分组
                        Map<String, List<DeviceHistoryDto>> groupByDateMap = deviceHistoryDtoList.stream().collect(Collectors.groupingBy(DeviceHistoryDto::getDateTime));
                        getDateTimeBetween.forEach(dateTime -> {
                            if (groupByDateMap.containsKey(dateTime)) {
                                dataList.add(getToDouble(groupByDateMap.get(dateTime).stream().filter(d -> StringUtil.isNotEmpty(d.getDataValue())).mapToDouble(d -> Double.parseDouble(String.valueOf(d.getDataValue()))).sum()));
                            } else {
                                dataList.add(null);
                            }
                        });
                        dataInfo.setName("有功功率");
                        dataInfo.setEName("activePowerList");
                        dataInfo.setDataList(dataList);
                        dataInfoList.add(dataInfo);
                    }
                }
            }
        }
        result.setDataInfoList(dataInfoList);
        return ResponseResult.ok(result);
    }

    @Override
    public ResponseResult<PileFunCurveDto> findStorageSiteQtCurve(String siteIds, Integer queryType, String startTime, String endTime) {
        PileFunCurveDto result = new PileFunCurveDto();
        if (StringUtil.isEmpty(siteIds) || StringUtil.isEmpty(startTime) || StringUtil.isEmpty(endTime) || StringUtil.isEmpty(queryType)) {
            return ResponseResult.error(ResponseResult.PARAM_ERROR);
        }
        List<String> siteIdList = JSON.parseArray(siteIds, String.class);
        //存储数据列表
        List<PileFunCurveDto.DataInfo> dataInfoList = Lists.newArrayList();
        //根据开始结束时间查询时间轴列表
        List<String> dateBetween = getDateBetween(queryType, startTime.substring(0, 10), endTime.substring(0, 10));
        String timeInterval = null;
        if (queryType == 1) {
            timeInterval = "1d";
        } else if (queryType == 2) {
            timeInterval = "1n";
        } else if (queryType == 3) {
            timeInterval = "1y";
        }
        result.setXAXisList(dateBetween);

        //根据站点id，查询站点下所有设备列表
        ResponseResult<Map<String, List<DeviceBasicInfoDto>>> deviceBasicInfoBySiteIds = deviceService.findDeviceBasicInfoBySiteIds(siteIdList, null);
        if (deviceBasicInfoBySiteIds.isSuccess() && !deviceBasicInfoBySiteIds.getData().isEmpty()) {
            List<DeviceBasicInfoDto> deviceBasicInfoDtos = Lists.newArrayList();
            //过滤出电池簇设备
            deviceBasicInfoBySiteIds.getData().forEach((k, v) -> deviceBasicInfoDtos.addAll(v.stream().filter(d ->
                    StringUtil.isNotEmpty(d.getTypeId()) && "25".equals(d.getTypeId())).collect(Collectors.toList())));
            if (CollectionUtils.isNotEmpty(deviceBasicInfoDtos)) {
                DeviceHistoryQueryVo deviceHistoryQueryVo = new DeviceHistoryQueryVo();
                deviceHistoryQueryVo.setDeviceIds(deviceBasicInfoDtos.stream().map(DeviceBasicInfoDto::getId).collect(Collectors.toSet()));
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

                    //根据时间类型格式，以first时间分组充电量历史数据
                    Map<String, List<NodeDifHistoryDto>> sumChargeMap = groupByDateTime(sumChargeList, queryType);

                    //根据时间类型格式，以first时间分组充电量历史数据
                    Map<String, List<NodeDifHistoryDto>> sumDischargeMap = groupByDateTime(sumDischargeList, queryType);

                    //充电曲线对象
                    PileFunCurveDto.DataInfo chargeDataInfo = new PileFunCurveDto.DataInfo();
                    chargeDataInfo.setName("充电电量");
                    chargeDataInfo.setEName("chargeQtList");
                    List<Object> chargeDataList = Lists.newArrayList();

                    //放电曲线对象
                    PileFunCurveDto.DataInfo dischargeDataInfo = new PileFunCurveDto.DataInfo();
                    dischargeDataInfo.setName("放电电量");
                    dischargeDataInfo.setEName("dischargeQtList");
                    List<Object> dischargeDataList = Lists.newArrayList();

                    dateBetween.forEach(dateTime -> {
                        double chargeSum = sumChargeMap.getOrDefault(dateTime, Collections.emptyList()).stream()
                                .filter(n -> StringUtil.isNotEmpty(n.getLastDataValue()) && StringUtil.isNotEmpty(n.getFirstDataValue()))
                                .mapToDouble(n -> Double.parseDouble(String.valueOf(n.getLastDataValue())) - Double.parseDouble(String.valueOf(n.getFirstDataValue())))
                                .sum();
                        chargeDataList.add(getToDouble(chargeSum));

                        double dischargeSum = sumDischargeMap.getOrDefault(dateTime, Collections.emptyList()).stream()
                                .filter(n -> StringUtil.isNotEmpty(n.getLastDataValue()) && StringUtil.isNotEmpty(n.getFirstDataValue()))
                                .mapToDouble(n -> Double.parseDouble(String.valueOf(n.getLastDataValue())) - Double.parseDouble(String.valueOf(n.getFirstDataValue())))
                                .sum();
                        dischargeDataList.add(getToDouble(dischargeSum));
                    });
                    chargeDataInfo.setDataList(chargeDataList);
                    dischargeDataInfo.setDataList(dischargeDataList);
                    dataInfoList.add(chargeDataInfo);
                    dataInfoList.add(dischargeDataInfo);
                }
            }
        }
        result.setDataInfoList(dataInfoList);
        return ResponseResult.ok(result);
    }

    private Map<String, List<NodeDifHistoryDto>> groupByDateTime(List<NodeDifHistoryDto> nodeDifHistoryDtos, int queryType) {
        if (CollectionUtils.isNotEmpty(nodeDifHistoryDtos)) {
            return nodeDifHistoryDtos.stream()
                    .filter(o -> StringUtil.isNotEmpty(o.getFirstDateTime()))
                    .collect(Collectors.groupingBy(o -> {
                        String dateTime = o.getFirstDateTime();
                        switch (queryType) {
                            case 1:
                                return dateTime.substring(0, 10);
                            case 2:
                                return dateTime.substring(0, 7);
                            case 3:
                                return dateTime.substring(0, 4);
                            default:
                                return dateTime;
                        }
                    }));
        }
        return Maps.newHashMap();
    }

    @Override
    public ResponseResult<PileFunCurveDto> findPcsChargeQtCurve(String pcsDeviceId, Integer queryType, String startTime, String endTime) {
        PileFunCurveDto result = new PileFunCurveDto();
        if (StringUtil.isEmpty(pcsDeviceId) || StringUtil.isEmpty(startTime) || StringUtil.isEmpty(endTime) || StringUtil.isEmpty(queryType)) {
            return ResponseResult.error(ResponseResult.PARAM_ERROR);
        }
        //存储数据列表
        List<PileFunCurveDto.DataInfo> dataInfoList = Lists.newArrayList();
        //根据开始结束时间查询时间轴列表
        List<String> dateBetween = getDateBetween(queryType, startTime.substring(0, 10), endTime.substring(0, 10));
        String timeInterval = null;
        if (queryType == 1) {
            timeInterval = "1d";
        } else if (queryType == 2) {
            timeInterval = "1n";
        } else if (queryType == 3) {
            timeInterval = "1y";
        }
        result.setXAXisList(dateBetween);

        //根据站点id，查询站点下所有设备列表
        ResponseResult<Map<String, List<DeviceBasicInfoDto>>> deviceBasicInfoByParentIds = deviceService.findDeviceInfoByParentIds(Collections.singletonList(pcsDeviceId));
        if (deviceBasicInfoByParentIds.isSuccess() && !deviceBasicInfoByParentIds.getData().isEmpty() && deviceBasicInfoByParentIds.getData().containsKey(pcsDeviceId)) {
            //过滤出电池簇设备
            List<DeviceBasicInfoDto> deviceBasicInfoDtos = deviceBasicInfoByParentIds.getData().get(pcsDeviceId).stream().filter(d -> StringUtil.isNotEmpty(d.getTypeId()) && "25".equals(d.getTypeId())).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(deviceBasicInfoDtos)) {
                DeviceHistoryQueryVo deviceHistoryQueryVo = new DeviceHistoryQueryVo();
                deviceHistoryQueryVo.setDeviceIds(deviceBasicInfoDtos.stream().map(DeviceBasicInfoDto::getId).collect(Collectors.toSet()));
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

                    //根据时间类型格式，以first时间分组充电量历史数据
                    Map<String, List<NodeDifHistoryDto>> sumChargeMap = groupByDateTime(sumChargeList, queryType);

                    //根据时间类型格式，以first时间分组充电量历史数据
                    Map<String, List<NodeDifHistoryDto>> sumDischargeMap = groupByDateTime(sumDischargeList, queryType);

                    //充电曲线对象
                    PileFunCurveDto.DataInfo chargeDataInfo = new PileFunCurveDto.DataInfo();
                    chargeDataInfo.setName("充电电量");
                    chargeDataInfo.setEName("chargeQtList");
                    List<Object> chargeDataList = Lists.newArrayList();

                    //放电曲线对象
                    PileFunCurveDto.DataInfo dischargeDataInfo = new PileFunCurveDto.DataInfo();
                    dischargeDataInfo.setName("放电电量");
                    dischargeDataInfo.setEName("dischargeQtList");
                    List<Object> dischargeDataList = Lists.newArrayList();

                    dateBetween.forEach(dateTime -> {
                        double chargeSum = sumChargeMap.getOrDefault(dateTime, Collections.emptyList()).stream()
                                .filter(n -> StringUtil.isNotEmpty(n.getLastDataValue()) && StringUtil.isNotEmpty(n.getFirstDataValue()))
                                .mapToDouble(n -> Double.parseDouble(String.valueOf(n.getLastDataValue())) - Double.parseDouble(String.valueOf(n.getFirstDataValue())))
                                .sum();
                        chargeDataList.add(getToDouble(chargeSum));

                        double dischargeSum = sumDischargeMap.getOrDefault(dateTime, Collections.emptyList()).stream()
                                .filter(n -> StringUtil.isNotEmpty(n.getLastDataValue()) && StringUtil.isNotEmpty(n.getFirstDataValue()))
                                .mapToDouble(n -> Double.parseDouble(String.valueOf(n.getLastDataValue())) - Double.parseDouble(String.valueOf(n.getFirstDataValue())))
                                .sum();
                        dischargeDataList.add(getToDouble(dischargeSum));
                    });
                    chargeDataInfo.setDataList(chargeDataList);
                    dischargeDataInfo.setDataList(dischargeDataList);
                    dataInfoList.add(chargeDataInfo);
                    dataInfoList.add(dischargeDataInfo);
                }
            }
        }
        result.setDataInfoList(dataInfoList);
        return ResponseResult.ok(result);
    }

}
