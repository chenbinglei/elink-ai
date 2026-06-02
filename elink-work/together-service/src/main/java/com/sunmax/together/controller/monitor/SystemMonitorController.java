package com.sunmax.together.controller.monitor;

import com.sunmax.common.dto.PageDto;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.together.dto.monitor.systemMonitor.*;
import com.sunmax.together.service.monitor.SystemMonitorService;
import com.sunmax.together.vo.monitor.systemMonitor.FaultAlarmQueryVo;
import com.sunmax.together.vo.monitor.systemMonitor.HistoryDataQueryVo;
import com.sunmax.together.vo.monitor.systemMonitor.SystemQueryVo;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("systemMonitor")
@Api(tags = "微电网系统监控系统控制台")
public class SystemMonitorController {

    @Autowired
    private SystemMonitorService systemMonitorService;


    @PostMapping("getSystemTreeList")
    @ApiOperation("获取系统监控树形数据")
//    @WebLog("获取系统监控树形数据")
    @ApiOperationSupport(order = 1)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "siteId", value = "站点id", paramType = "query", required = true),
            @ApiImplicitParam(name = "type", value = "类型 1-光伏监控 2-储能监控 3-电桩监控 4-用能系统 5-变配电系统 6-换电系统", paramType = "query", required = true)
    })
    public ResponseResult<List<SystemTreeDto>> getSystemTreeList(String siteId, Integer type) {
        return systemMonitorService.getSystemTreeList(siteId, type);
    }

    @PostMapping("findSystemPvData")
    @ApiOperation("查询光伏系统数据")
    @ApiOperationSupport(order = 2)
    @ApiImplicitParam(name = "dataId", value = "数据id", paramType = "query", required = true)
    public ResponseResult<SystemPvDto> findSystemPvData(String dataId) {
        return systemMonitorService.findSystemPvData(dataId);
    }

    @PostMapping("findPvInverterData")
    @ApiOperation("查询光伏逆变器数据")
    @ApiOperationSupport(order = 3)
    @ApiImplicitParam(name = "dataId", value = "数据id", paramType = "query", required = true)
    public ResponseResult<SystemPvInverterDto> findPvInverterData(String dataId) {
        return systemMonitorService.findPvInverterData(dataId);
    }

    @PostMapping("findPvWeatherData")
    @ApiOperation("查询光伏气象站数据")
    @ApiOperationSupport(order = 4)
    @ApiImplicitParam(name = "dataId", value = "数据id", paramType = "query", required = true)
    public ResponseResult<SystemPvWeatherDto> findPvWeatherData(String dataId) {
        return systemMonitorService.findPvWeatherData(dataId);
    }

    @PostMapping("findSystemSeData")
    @ApiOperation("查询储能系统数据")
    @ApiOperationSupport(order = 5)
    @ApiImplicitParam(name = "dataId", value = "数据id", paramType = "query", required = true)
    public ResponseResult<SystemSeDto> findSystemSeData(String dataId) {
        return systemMonitorService.findSystemSeData(dataId);
    }

    @PostMapping("findSePcsData")
    @ApiOperation("查询储能PCS数据")
    @ApiOperationSupport(order = 6)
    @ApiImplicitParam(name = "dataId", value = "数据id", paramType = "query", required = true)
    public ResponseResult<SystemSePcsDto> findSePcsData(String dataId) {
        return systemMonitorService.findSePcsData(dataId);
    }

    @PostMapping("findSeBatteryData")
    @ApiOperation("查询储能电池簇数据")
    @ApiOperationSupport(order = 7)
    @ApiImplicitParam(name = "dataId", value = "数据id", paramType = "query", required = true)
    public ResponseResult<SystemSeBatteryDto> findSeBatteryData(String dataId) {
        return systemMonitorService.findSeBatteryData(dataId);
    }

    @PostMapping("findSeAuxEquipmentData")
    @ApiOperation("查询储能辅助设备数据")
    @ApiOperationSupport(order = 8)
    @ApiImplicitParam(name = "dataId", value = "数据id", paramType = "query", required = true)
    public ResponseResult<SystemSeAuxEquipmentDto> findSeAuxEquipmentData(String dataId) {
        return systemMonitorService.findSeAuxEquipmentData(dataId);
    }

    @PostMapping("findSystemPileData")
    @ApiOperation("查询电桩系统数据")
    @ApiOperationSupport(order = 9)
    @ApiImplicitParam(name = "dataId", value = "数据id", paramType = "query", required = true)
    public ResponseResult<SystemPileDto> findSystemPileData(String dataId) {
        return systemMonitorService.findSystemPileData(dataId);
    }

    @PostMapping("findPileData")
    @ApiOperation("查询电桩数据")
    @ApiOperationSupport(order = 10)
    @ApiImplicitParam(name = "dataId", value = "数据id", paramType = "query", required = true)
    public ResponseResult<PileDto> findPileData(String dataId) {
        return systemMonitorService.findPileData(dataId);
    }

    @PostMapping("findSuperPileData")
    @ApiOperation("查询超充桩数据")
    @ApiOperationSupport(order = 11)
    @ApiImplicitParam(name = "dataId", value = "数据id", paramType = "query", required = true)
    public ResponseResult<SuperPileDto> findSuperPileData(String dataId) {
        return systemMonitorService.findSuperPileData(dataId);
    }

    @PostMapping("findPileGunPowerList")
    @ApiOperation("查询电桩充电枪功率曲线数据")
    @ApiOperationSupport(order = 12)
    @ApiImplicitParam(name = "dataId", value = "数据id", paramType = "query", required = true)
    public ResponseResult<List<PileGunPowerDto>> findPileGunPowerList(String dataId) {
        return systemMonitorService.findPileGunPowerList(dataId);
    }

    @PostMapping("findSystemCurve")
    @ApiOperation("查询系统曲线数据")
    @ApiOperationSupport(order = 13)
    public ResponseResult<SystemCurveDto> findSystemCurve(SystemQueryVo systemQueryVo) {
        return systemMonitorService.findSystemCurve(systemQueryVo);
    }

    @PostMapping("findAllFaultAlarmList")
    @ApiOperation("查询故障告警列表数据")
    @ApiOperationSupport(order = 14)
    public ResponseResult<PageDto<FaultAlarmListDto>> findAllFaultAlarmList(FaultAlarmQueryVo faultAlarmVo) {
        return systemMonitorService.findAllFaultAlarmList(faultAlarmVo);
    }

    @PostMapping("getDeviceFieldList")
    @ApiOperation("获取站点及站点下面的设备数据字段")
    @ApiOperationSupport(order = 15)
    public ResponseResult<DeviceFieldDto> getDeviceFieldList(String siteId) {
        return systemMonitorService.getDeviceFieldList(siteId);
    }

    @PostMapping("findAllHistoryDataList")
    @ApiOperation("查询站点设备历史数据")
    @ApiOperationSupport(order = 16)
    public ResponseResult<HistoryDataDto> findAllHistoryDataList(HistoryDataQueryVo historyDataVo) {
        return systemMonitorService.findAllHistoryDataList(historyDataVo);
    }

    @PostMapping("findSiteDetailById")
    @ApiOperation("根据站点id查询站点详情数据")
    @ApiOperationSupport(order = 17)
    @ApiImplicitParam(name = "siteId", value = "站点id", paramType = "query", required = true)
    public ResponseResult<SiteDetailDto> findSiteDetailById(String siteId) {
        return systemMonitorService.findSiteDetailById(siteId);
    }

    @PostMapping("findAllCellList")
    @ApiOperation("根据数据id和类型查询电池簇电芯列表")
    @ApiOperationSupport(order = 18)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "dataId", value = "数据id", paramType = "query", required = true),
            @ApiImplicitParam(name = "queryType", value = "查询类型 1-温度 2-电压", paramType = "query", required = true),
            @ApiImplicitParam(name = "page", value = "当前页数", paramType = "query", required = true),
            @ApiImplicitParam(name = "size", value = "当前页条数", paramType = "query", required = true)
    })
    public ResponseResult<PageDto<Object>> findAllCellList(String dataId, Integer queryType, Integer page, Integer size) {
        return systemMonitorService.findAllCellList(dataId, queryType, page, size);
    }

    @PostMapping("findSystemChangeData")
    @ApiOperation("查询换电系统数据")
    @ApiOperationSupport(order = 19)
    @ApiImplicitParam(name = "dataId", value = "数据id", paramType = "query", required = true)
    public ResponseResult<SystemChangeDto> findSystemChangeData(String dataId) {
        return systemMonitorService.findSystemChangeData(dataId);
    }

    @PostMapping("findSystemMeterData")
    @ApiOperation("查询电表数据")
    @ApiOperationSupport(order = 20)
    @ApiImplicitParam(name = "dataId", value = "数据id", paramType = "query", required = true)
    public ResponseResult<SystemMeterDto> findSystemMeterData(String dataId) {
        return systemMonitorService.findSystemMeterData(dataId);
    }

}
