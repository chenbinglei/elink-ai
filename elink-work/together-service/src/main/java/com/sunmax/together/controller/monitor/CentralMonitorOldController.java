package com.sunmax.together.controller.monitor;

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
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping("centralMonitor")
@Api(tags = "集中监控管理")
public class CentralMonitorOldController {

    @Autowired
    private CentralMonitorOldService centralMonitorOldService;

    @PostMapping("findPvSiteMonitorData")
    @ApiOperation("查询光伏站点监测数据")
    @ApiOperationSupport(order = 1)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "siteId", value = "站点id", paramType = "query", required = true)
    })
    public ResponseResult<PvSiteMonitorDto> findPvSiteMonitorData(String siteId) {
        return centralMonitorOldService.findPvSiteMonitorData(siteId);
    }

    @PostMapping("findSystemVarCurveData")
    @ApiOperation("查询系统变量曲线数据")
    @ApiOperationSupport(order = 2)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "isCurrent", value = "结束时间是否为当前时间 1-是", paramType = "query", required = true)
    })
    public ResponseResult<Map<String, Map<String, Object>>> findSystemVarCurveData(VarNodeValueVo varNodeValueVo, Integer isCurrent) {
        return centralMonitorOldService.findSystemVarCurveData(varNodeValueVo, isCurrent);
    }

    @PostMapping("findPvInverterList")
    @ApiOperation("查询光伏逆变器列表")
    @ApiOperationSupport(order = 3)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "siteId", value = "站点id", paramType = "query", required = true)
    })
    public ResponseResult<List<PvInverterListDto>> findPvInverterList(String siteId) {
        return centralMonitorOldService.findPvInverterList(siteId);
    }

    @PostMapping("findNotRecoveEventList")
    @ApiOperation("根据设备id查询设备所有未恢复事件告警数据")
    @ApiOperationSupport(order = 4)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "deviceId", value = "设备id", paramType = "query", required = true)
    })
    public ResponseResult<List<DeviceAlarmEventListDto>> findNotRecoveEventList(String deviceId) {
        return centralMonitorOldService.findNotRecoveEventList(deviceId);
    }

    @PostMapping("findWeatherForecast")
    @ApiOperation("根据经纬度获取天气预报数据")
    @ApiOperationSupport(order = 5)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "longitude", value = "经度", paramType = "query", required = true),
            @ApiImplicitParam(name = "latitude", value = "纬度", paramType = "query", required = true)
    })
    public ResponseResult<WeatherForecastDto> findWeatherForecast(String longitude, String latitude) {
        return WeatherUtil.getWeatherForecast(longitude, latitude);
    }

    @PostMapping("findChargeSiteMonitorData")
    @ApiOperation("查询充电站监测数据")
    @ApiOperationSupport(order = 6)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "siteId", value = "站点id", paramType = "query", required = true)
    })
    public ResponseResult<ChargeSiteMonitorDto> findChargeSiteMonitorData(String siteId) {
        return centralMonitorOldService.findChargeSiteMonitorData(siteId);
    }

    @PostMapping("findElecCountCurveData")
    @ApiOperation("查询充电站电量统计曲线数据")
    @ApiOperationSupport(order = 7)
    public ResponseResult<ElecCountCurveDto> findElecCountCurveData(ElecCountQueryVo elecCountQueryVo) {
        return centralMonitorOldService.findElecCountCurveData(elecCountQueryVo);
    }

    @PostMapping("findPileDeviceList")
    @ApiOperation("查询电桩设备列表")
    @ApiOperationSupport(order = 8)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "siteId", value = "站点id", paramType = "query", required = true)
    })
    public ResponseResult<List<PileDeviceListDto>> findPileDeviceList(String siteId) {
        return centralMonitorOldService.findPileDeviceList(siteId);
    }

    @PostMapping("saveDeviceDeviceFieldSet")
    @ApiOperation("添加设备字段设置")
    @ApiOperationSupport(order = 9)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "deviceId", value = "设备id", dataType = "String", required = true),
            @ApiImplicitParam(name = "functionFields", value = "多个功能点字段id 例如['1','2','3']", dataType = "String", required = true)
    })
    public ResponseResult<Void> saveDeviceDeviceFieldSet(String deviceId, String functionFields) {
        return centralMonitorOldService.saveDeviceDeviceFieldSet(deviceId, functionFields);
    }

    @PostMapping("findDeviceTelemetryList")
    @ApiOperation("查询设备遥测字段数据列表")
    @ApiOperationSupport(order = 10)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "deviceId", value = "设备id", dataType = "String", required = true)
    })
    public ResponseResult<List<DeviceTelemetryDto>> findDeviceTelemetryList(String deviceId) {
        return centralMonitorOldService.findDeviceTelemetryList(deviceId);
    }

    @PostMapping("findSystemVarDataListById")
    @ApiOperation("根据站点/设备id查询所关联所有系统变量数据列表")
    @ApiOperationSupport(order = 11)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "varCodes", value = "多个系统变量编码(以逗号分割，不传则查询所关联全部系统变量)", dataType = "String"),
            @ApiImplicitParam(name = "queryId", value = "设备/站点id", dataType = "String", required = true)
    })
    public ResponseResult<List<SystemVarDataDto>> findSystemVarDataListById(String varCodes, String queryId) {
        return centralMonitorOldService.findSystemVarDataListById(varCodes, queryId);
    }

    @PostMapping("queryDeviceFunctionCurveData")
    @ApiOperation("查询设备功能点曲线值数据")
    @ApiOperationSupport(order = 12)
    public ResponseResult<Map<String, Map<String, Object>>> queryDeviceFunctionCurveData(FunctionValueVo functionValueVo) {
        return centralMonitorOldService.queryDeviceFunctionCurveData(functionValueVo);
    }

    @PostMapping("findStorageMonitorData")
    @ApiOperation("查询储能站点监测数据")
    @ApiOperationSupport(order = 13)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "siteId", value = "站点id", paramType = "query", required = true)
    })
    public ResponseResult<StorageMonitorDto> findStorageMonitorData(String siteId) {
        return centralMonitorOldService.findStorageMonitorData(siteId);
    }

    @PostMapping("findPcsMonitorList")
    @ApiOperation("查询PCS设备监测列表数据")
    @ApiOperationSupport(order = 14)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "siteId", value = "站点id", paramType = "query", required = true)
    })
    public ResponseResult<List<PcsMonitorListDto>> findPcsMonitorList(String siteId) {
        return centralMonitorOldService.findPcsMonitorList(siteId);
    }

    @PostMapping("findBatteryMonitorList")
    @ApiOperation("查询电池簇设备监测列表数据")
    @ApiOperationSupport(order = 15)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "siteId", value = "站点id", paramType = "query", required = true)
    })
    public ResponseResult<List<BatteryMonitorListDto>> findBatteryMonitorList(String siteId) {
        return centralMonitorOldService.findBatteryMonitorList(siteId);
    }

    @PostMapping("findCellListByPage")
    @ApiOperation("分页查询电芯列表")
    @ApiOperationSupport(order = 16)
    @ApiImplicitParams({
        @ApiImplicitParam(name = "deviceId", value = "设备id", paramType = "query", required = true),
        @ApiImplicitParam(name = "queryType", value = "查询类型 1-温度 2-电压", paramType = "query", required = true),
        @ApiImplicitParam(name = "page", value = "页数", paramType = "query", required = true),
        @ApiImplicitParam(name = "size", value = "条数", paramType = "query", required = true)
    })
    public ResponseResult<PageDto<Object>> findCellListByPage(String deviceId, Integer queryType, Integer page, Integer size) {
        return centralMonitorOldService.findCellListByPage(deviceId, queryType, page, size);
    }

    @PostMapping("findAuxiliaryMonitorList")
    @ApiOperation("查询辅助设备监测列表数据")
    @ApiOperationSupport(order = 17)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "siteId", value = "站点id", paramType = "query", required = true)
    })
    public ResponseResult<List<AuxiliaryMonitorListDto>> findAuxiliaryMonitorList(String siteId) {
        return centralMonitorOldService.findAuxiliaryMonitorList(siteId);
    }

    @PostMapping("findPvSitePowerCurve")
    @ApiOperation("查询光伏站点功率曲线数据")
    @ApiOperationSupport(order = 18)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "siteIds", value = "多个站点id", paramType = "query", required = true),
            @ApiImplicitParam(name = "startTime", value = "开始时间(yyyy-MM-dd HH:mm:ss)", paramType = "query", required = true),
            @ApiImplicitParam(name = "endTime", value = "结束时间(yyyy-MM-dd HH:mm:ss)", paramType = "query", required = true)
    })
    public ResponseResult<PileFunCurveDto> findPvSitePowerCurve(String siteIds, String startTime, String endTime) {
        return centralMonitorOldService.findPvSitePowerCurve(siteIds, startTime, endTime);
    }

    @PostMapping("findPvSiteQtCurve")
    @ApiOperation("查询光伏站点发电量曲线数据")
    @ApiOperationSupport(order = 19)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "siteIds", value = "多个站点id", paramType = "query", required = true),
            @ApiImplicitParam(name = "queryType", value = "查询类型(1-日 2-月 3-年)", paramType = "query", required = true),
            @ApiImplicitParam(name = "startTime", value = "开始时间(yyyy-MM-dd HH:mm:ss)", paramType = "query", required = true),
            @ApiImplicitParam(name = "endTime", value = "结束时间(yyyy-MM-dd HH:mm:ss)", paramType = "query", required = true)
    })
    public ResponseResult<PileFunCurveDto> findPvSiteQtCurve(String siteIds, Integer queryType, String startTime, String endTime) {
        return centralMonitorOldService.findPvSiteQtCurve(siteIds, queryType, startTime, endTime);
    }

    @PostMapping("findChargeSitePowerCurve")
    @ApiOperation("查询充电站点功率曲线数据")
    @ApiOperationSupport(order = 20)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "siteIds", value = "多个站点id", paramType = "query", required = true),
            @ApiImplicitParam(name = "startTime", value = "开始时间(yyyy-MM-dd HH:mm:ss)", paramType = "query", required = true),
            @ApiImplicitParam(name = "endTime", value = "结束时间(yyyy-MM-dd HH:mm:ss)", paramType = "query", required = true)
    })
    public ResponseResult<PileFunCurveDto> findChargeSitePowerCurve(String siteIds, String startTime, String endTime) {
        return centralMonitorOldService.findChargeSitePowerCurve(siteIds, startTime, endTime);
    }

    @PostMapping("findStorageSitePowerCurve")
    @ApiOperation("查询储能站点功率曲线数据")
    @ApiOperationSupport(order = 21)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "siteIds", value = "多个站点id", paramType = "query", required = true),
            @ApiImplicitParam(name = "startTime", value = "开始时间(yyyy-MM-dd HH:mm:ss)", paramType = "query", required = true),
            @ApiImplicitParam(name = "endTime", value = "结束时间(yyyy-MM-dd HH:mm:ss)", paramType = "query", required = true)
    })
    public ResponseResult<PileFunCurveDto> findStorageSitePowerCurve(String siteIds, String startTime, String endTime) {
        return centralMonitorOldService.findStorageSitePowerCurve(siteIds, startTime, endTime);
    }

    @PostMapping("findStorageSiteQtCurve")
    @ApiOperation("查询储能站点充放电量曲线数据")
    @ApiOperationSupport(order = 22)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "siteIds", value = "多个站点id", paramType = "query", required = true),
            @ApiImplicitParam(name = "queryType", value = "查询类型(1-日 2-月 3-年)", paramType = "query", required = true),
            @ApiImplicitParam(name = "startTime", value = "开始时间(yyyy-MM-dd HH:mm:ss)", paramType = "query", required = true),
            @ApiImplicitParam(name = "endTime", value = "结束时间(yyyy-MM-dd HH:mm:ss)", paramType = "query", required = true)
    })
    public ResponseResult<PileFunCurveDto> findStorageSiteQtCurve(String siteIds, Integer queryType, String startTime, String endTime) {
        return centralMonitorOldService.findStorageSiteQtCurve(siteIds, queryType, startTime, endTime);
    }

    @PostMapping("findPcsChargeQtCurve")
    @ApiOperation("查询PCS设备充放电量曲线数据")
    @ApiOperationSupport(order = 23)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pcsDeviceId", value = "PCS设备唯一id", paramType = "query", required = true),
            @ApiImplicitParam(name = "queryType", value = "查询类型(1-日 2-月 3-年)", paramType = "query", required = true),
            @ApiImplicitParam(name = "startTime", value = "开始时间(yyyy-MM-dd HH:mm:ss)", paramType = "query", required = true),
            @ApiImplicitParam(name = "endTime", value = "结束时间(yyyy-MM-dd HH:mm:ss)", paramType = "query", required = true)
    })
    public ResponseResult<PileFunCurveDto> findPcsChargeQtCurve(String pcsDeviceId, Integer queryType, String startTime, String endTime) {
        return centralMonitorOldService.findPcsChargeQtCurve(pcsDeviceId, queryType, startTime, endTime);
    }

}
