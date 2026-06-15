package com.sunmax.together.controller.monitor;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

import com.sunmax.common.dto.PageDto;
import com.sunmax.common.dto.WeatherForecastDto;
import com.sunmax.common.dto.crontab.SystemVarDataDto;
import com.sunmax.common.dto.device.DeviceAlarmEventListDto;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.util.WeatherUtil;
import com.sunmax.common.vo.crontab.VarNodeValueVo;
import com.sunmax.common.vo.together.FunctionValueVo;
import com.sunmax.together.dto.monitor.centralMonitorOld.*;
import com.sunmax.together.dto.monitor.assetOverview.PileFunCurveDto;
import com.sunmax.together.service.monitor.CentralMonitorOldService;
import com.sunmax.together.vo.monitor.centralMonitorOld.ElecCountQueryVo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping("centralMonitor")
@Tag(name = "集中监控管理")
public class CentralMonitorOldController {

    @Autowired
    private CentralMonitorOldService centralMonitorOldService;

    @PostMapping("findPvSiteMonitorData")
    @Operation(summary = "查询光伏站点监测数据")
    
    @Parameters({
            @Parameter(name = "siteId", description = "站点id")
    })
    public ResponseResult<PvSiteMonitorDto> findPvSiteMonitorData(String siteId) {
        return centralMonitorOldService.findPvSiteMonitorData(siteId);
    }

    @PostMapping("findSystemVarCurveData")
    @Operation(summary = "查询系统变量曲线数据")
    
    @Parameters({
            @Parameter(name = "isCurrent", description = "结束时间是否为当前时间 1-是")
    })
    public ResponseResult<Map<String, Map<String, Object>>> findSystemVarCurveData(VarNodeValueVo varNodeValueVo, Integer isCurrent) {
        return centralMonitorOldService.findSystemVarCurveData(varNodeValueVo, isCurrent);
    }

    @PostMapping("findPvInverterList")
    @Operation(summary = "查询光伏逆变器列表")
    
    @Parameters({
            @Parameter(name = "siteId", description = "站点id")
    })
    public ResponseResult<List<PvInverterListDto>> findPvInverterList(String siteId) {
        return centralMonitorOldService.findPvInverterList(siteId);
    }

    @PostMapping("findNotRecoveEventList")
    @Operation(summary = "根据设备id查询设备所有未恢复事件告警数据")
    
    @Parameters({
            @Parameter(name = "deviceId", description = "设备id")
    })
    public ResponseResult<List<DeviceAlarmEventListDto>> findNotRecoveEventList(String deviceId) {
        return centralMonitorOldService.findNotRecoveEventList(deviceId);
    }

    @PostMapping("findWeatherForecast")
    @Operation(summary = "根据经纬度获取天气预报数据")
    
    @Parameters({
            @Parameter(name = "longitude", description = "经度"),
            @Parameter(name = "latitude", description = "纬度")
    })
    public ResponseResult<WeatherForecastDto> findWeatherForecast(String longitude, String latitude) {
        return WeatherUtil.getWeatherForecast(longitude, latitude);
    }

    @PostMapping("findChargeSiteMonitorData")
    @Operation(summary = "查询充电站监测数据")
    
    @Parameters({
            @Parameter(name = "siteId", description = "站点id")
    })
    public ResponseResult<ChargeSiteMonitorDto> findChargeSiteMonitorData(String siteId) {
        return centralMonitorOldService.findChargeSiteMonitorData(siteId);
    }

    @PostMapping("findElecCountCurveData")
    @Operation(summary = "查询充电站电量统计曲线数据")
    
    public ResponseResult<ElecCountCurveDto> findElecCountCurveData(ElecCountQueryVo elecCountQueryVo) {
        return centralMonitorOldService.findElecCountCurveData(elecCountQueryVo);
    }

    @PostMapping("findPileDeviceList")
    @Operation(summary = "查询电桩设备列表")
    
    @Parameters({
            @Parameter(name = "siteId", description = "站点id")
    })
    public ResponseResult<List<PileDeviceListDto>> findPileDeviceList(String siteId) {
        return centralMonitorOldService.findPileDeviceList(siteId);
    }

    @PostMapping("saveDeviceDeviceFieldSet")
    @Operation(summary = "添加设备字段设置")
    
    @Parameters({
            @Parameter(name = "deviceId", description = "设备id"),
            @Parameter(name = "functionFields", description = "多个功能点字段id 例如['1','2','3']")
    })
    public ResponseResult<Void> saveDeviceDeviceFieldSet(String deviceId, String functionFields) {
        return centralMonitorOldService.saveDeviceDeviceFieldSet(deviceId, functionFields);
    }

    @PostMapping("findDeviceTelemetryList")
    @Operation(summary = "查询设备遥测字段数据列表")
    
    @Parameters({
            @Parameter(name = "deviceId", description = "设备id")
    })
    public ResponseResult<List<DeviceTelemetryDto>> findDeviceTelemetryList(String deviceId) {
        return centralMonitorOldService.findDeviceTelemetryList(deviceId);
    }

    @PostMapping("findSystemVarDataListById")
    @Operation(summary = "根据站点/设备id查询所关联所有系统变量数据列表")
    
    @Parameters({
            @Parameter(name = "varCodes", description = "多个系统变量编码(以逗号分割，不传则查询所关联全部系统变量)"),
            @Parameter(name = "queryId", description = "设备/站点id")
    })
    public ResponseResult<List<SystemVarDataDto>> findSystemVarDataListById(String varCodes, String queryId) {
        return centralMonitorOldService.findSystemVarDataListById(varCodes, queryId);
    }

    @PostMapping("queryDeviceFunctionCurveData")
    @Operation(summary = "查询设备功能点曲线值数据")
    
    public ResponseResult<Map<String, Map<String, Object>>> queryDeviceFunctionCurveData(FunctionValueVo functionValueVo) {
        return centralMonitorOldService.queryDeviceFunctionCurveData(functionValueVo);
    }

    @PostMapping("findStorageMonitorData")
    @Operation(summary = "查询储能站点监测数据")
    
    @Parameters({
            @Parameter(name = "siteId", description = "站点id")
    })
    public ResponseResult<StorageMonitorDto> findStorageMonitorData(String siteId) {
        return centralMonitorOldService.findStorageMonitorData(siteId);
    }

    @PostMapping("findPcsMonitorList")
    @Operation(summary = "查询PCS设备监测列表数据")
    
    @Parameters({
            @Parameter(name = "siteId", description = "站点id")
    })
    public ResponseResult<List<PcsMonitorListDto>> findPcsMonitorList(String siteId) {
        return centralMonitorOldService.findPcsMonitorList(siteId);
    }

    @PostMapping("findBatteryMonitorList")
    @Operation(summary = "查询电池簇设备监测列表数据")
    
    @Parameters({
            @Parameter(name = "siteId", description = "站点id")
    })
    public ResponseResult<List<BatteryMonitorListDto>> findBatteryMonitorList(String siteId) {
        return centralMonitorOldService.findBatteryMonitorList(siteId);
    }

    @PostMapping("findCellListByPage")
    @Operation(summary = "分页查询电芯列表")
    
    @Parameters({
        @Parameter(name = "deviceId", description = "设备id"),
        @Parameter(name = "queryType", description = "查询类型 1-温度 2-电压"),
        @Parameter(name = "page", description = "页数"),
        @Parameter(name = "size", description = "条数")
    })
    public ResponseResult<PageDto<Object>> findCellListByPage(String deviceId, Integer queryType, Integer page, Integer size) {
        return centralMonitorOldService.findCellListByPage(deviceId, queryType, page, size);
    }

    @PostMapping("findAuxiliaryMonitorList")
    @Operation(summary = "查询辅助设备监测列表数据")
    
    @Parameters({
            @Parameter(name = "siteId", description = "站点id")
    })
    public ResponseResult<List<AuxiliaryMonitorListDto>> findAuxiliaryMonitorList(String siteId) {
        return centralMonitorOldService.findAuxiliaryMonitorList(siteId);
    }

    @PostMapping("findPvSitePowerCurve")
    @Operation(summary = "查询光伏站点功率曲线数据")
    
    @Parameters({
            @Parameter(name = "siteIds", description = "多个站点id"),
            @Parameter(name = "startTime", description = "开始时间(yyyy-MM-dd HH:mm:ss)"),
            @Parameter(name = "endTime", description = "结束时间(yyyy-MM-dd HH:mm:ss)")
    })
    public ResponseResult<PileFunCurveDto> findPvSitePowerCurve(String siteIds, String startTime, String endTime) {
        return centralMonitorOldService.findPvSitePowerCurve(siteIds, startTime, endTime);
    }

    @PostMapping("findPvSiteQtCurve")
    @Operation(summary = "查询光伏站点发电量曲线数据")
    
    @Parameters({
            @Parameter(name = "siteIds", description = "多个站点id"),
            @Parameter(name = "queryType", description = "查询类型(1-日 2-月 3-年)"),
            @Parameter(name = "startTime", description = "开始时间(yyyy-MM-dd HH:mm:ss)"),
            @Parameter(name = "endTime", description = "结束时间(yyyy-MM-dd HH:mm:ss)")
    })
    public ResponseResult<PileFunCurveDto> findPvSiteQtCurve(String siteIds, Integer queryType, String startTime, String endTime) {
        return centralMonitorOldService.findPvSiteQtCurve(siteIds, queryType, startTime, endTime);
    }

    @PostMapping("findChargeSitePowerCurve")
    @Operation(summary = "查询充电站点功率曲线数据")
    
    @Parameters({
            @Parameter(name = "siteIds", description = "多个站点id"),
            @Parameter(name = "startTime", description = "开始时间(yyyy-MM-dd HH:mm:ss)"),
            @Parameter(name = "endTime", description = "结束时间(yyyy-MM-dd HH:mm:ss)")
    })
    public ResponseResult<PileFunCurveDto> findChargeSitePowerCurve(String siteIds, String startTime, String endTime) {
        return centralMonitorOldService.findChargeSitePowerCurve(siteIds, startTime, endTime);
    }

    @PostMapping("findStorageSitePowerCurve")
    @Operation(summary = "查询储能站点功率曲线数据")
    
    @Parameters({
            @Parameter(name = "siteIds", description = "多个站点id"),
            @Parameter(name = "startTime", description = "开始时间(yyyy-MM-dd HH:mm:ss)"),
            @Parameter(name = "endTime", description = "结束时间(yyyy-MM-dd HH:mm:ss)")
    })
    public ResponseResult<PileFunCurveDto> findStorageSitePowerCurve(String siteIds, String startTime, String endTime) {
        return centralMonitorOldService.findStorageSitePowerCurve(siteIds, startTime, endTime);
    }

    @PostMapping("findStorageSiteQtCurve")
    @Operation(summary = "查询储能站点充放电量曲线数据")
    
    @Parameters({
            @Parameter(name = "siteIds", description = "多个站点id"),
            @Parameter(name = "queryType", description = "查询类型(1-日 2-月 3-年)"),
            @Parameter(name = "startTime", description = "开始时间(yyyy-MM-dd HH:mm:ss)"),
            @Parameter(name = "endTime", description = "结束时间(yyyy-MM-dd HH:mm:ss)")
    })
    public ResponseResult<PileFunCurveDto> findStorageSiteQtCurve(String siteIds, Integer queryType, String startTime, String endTime) {
        return centralMonitorOldService.findStorageSiteQtCurve(siteIds, queryType, startTime, endTime);
    }

    @PostMapping("findPcsChargeQtCurve")
    @Operation(summary = "查询PCS设备充放电量曲线数据")
    
    @Parameters({
            @Parameter(name = "pcsDeviceId", description = "PCS设备唯一id"),
            @Parameter(name = "queryType", description = "查询类型(1-日 2-月 3-年)"),
            @Parameter(name = "startTime", description = "开始时间(yyyy-MM-dd HH:mm:ss)"),
            @Parameter(name = "endTime", description = "结束时间(yyyy-MM-dd HH:mm:ss)")
    })
    public ResponseResult<PileFunCurveDto> findPcsChargeQtCurve(String pcsDeviceId, Integer queryType, String startTime, String endTime) {
        return centralMonitorOldService.findPcsChargeQtCurve(pcsDeviceId, queryType, startTime, endTime);
    }

}
