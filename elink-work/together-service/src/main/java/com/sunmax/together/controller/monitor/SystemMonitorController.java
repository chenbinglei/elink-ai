package com.sunmax.together.controller.monitor;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

import com.sunmax.common.dto.PageDto;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.together.dto.monitor.systemMonitor.*;
import com.sunmax.together.service.monitor.SystemMonitorService;
import com.sunmax.together.vo.monitor.systemMonitor.FaultAlarmQueryVo;
import com.sunmax.together.vo.monitor.systemMonitor.HistoryDataQueryVo;
import com.sunmax.together.vo.monitor.systemMonitor.SystemQueryVo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("systemMonitor")
@Tag(name = "微电网系统监控系统控制台")
public class SystemMonitorController {

    @Autowired
    private SystemMonitorService systemMonitorService;


    @PostMapping("getSystemTreeList")
    @Operation(summary = "获取系统监控树形数据")
//    @WebLog("获取系统监控树形数据")
    
    @Parameters({
            @Parameter(name = "siteId", description = "站点id"),
            @Parameter(name = "type", description = "类型 1-光伏监控 2-储能监控 3-电桩监控 4-用能系统 5-变配电系统 6-换电系统")
    })
    public ResponseResult<List<SystemTreeDto>> getSystemTreeList(String siteId, Integer type) {
        return systemMonitorService.getSystemTreeList(siteId, type);
    }

    @PostMapping("findSystemPvData")
    @Operation(summary = "查询光伏系统数据")
    
    @Parameter(name = "dataId", description = "数据id")
    public ResponseResult<SystemPvDto> findSystemPvData(String dataId) {
        return systemMonitorService.findSystemPvData(dataId);
    }

    @PostMapping("findPvInverterData")
    @Operation(summary = "查询光伏逆变器数据")
    
    @Parameter(name = "dataId", description = "数据id")
    public ResponseResult<SystemPvInverterDto> findPvInverterData(String dataId) {
        return systemMonitorService.findPvInverterData(dataId);
    }

    @PostMapping("findPvWeatherData")
    @Operation(summary = "查询光伏气象站数据")
    
    @Parameter(name = "dataId", description = "数据id")
    public ResponseResult<SystemPvWeatherDto> findPvWeatherData(String dataId) {
        return systemMonitorService.findPvWeatherData(dataId);
    }

    @PostMapping("findSystemSeData")
    @Operation(summary = "查询储能系统数据")
    
    @Parameter(name = "dataId", description = "数据id")
    public ResponseResult<SystemSeDto> findSystemSeData(String dataId) {
        return systemMonitorService.findSystemSeData(dataId);
    }

    @PostMapping("findSePcsData")
    @Operation(summary = "查询储能PCS数据")
    
    @Parameter(name = "dataId", description = "数据id")
    public ResponseResult<SystemSePcsDto> findSePcsData(String dataId) {
        return systemMonitorService.findSePcsData(dataId);
    }

    @PostMapping("findSeBatteryData")
    @Operation(summary = "查询储能电池簇数据")
    
    @Parameter(name = "dataId", description = "数据id")
    public ResponseResult<SystemSeBatteryDto> findSeBatteryData(String dataId) {
        return systemMonitorService.findSeBatteryData(dataId);
    }

    @PostMapping("findSeAuxEquipmentData")
    @Operation(summary = "查询储能辅助设备数据")
    
    @Parameter(name = "dataId", description = "数据id")
    public ResponseResult<SystemSeAuxEquipmentDto> findSeAuxEquipmentData(String dataId) {
        return systemMonitorService.findSeAuxEquipmentData(dataId);
    }

    @PostMapping("findSystemPileData")
    @Operation(summary = "查询电桩系统数据")
    
    @Parameter(name = "dataId", description = "数据id")
    public ResponseResult<SystemPileDto> findSystemPileData(String dataId) {
        return systemMonitorService.findSystemPileData(dataId);
    }

    @PostMapping("findPileData")
    @Operation(summary = "查询电桩数据")
    
    @Parameter(name = "dataId", description = "数据id")
    public ResponseResult<PileDto> findPileData(String dataId) {
        return systemMonitorService.findPileData(dataId);
    }

    @PostMapping("findSuperPileData")
    @Operation(summary = "查询超充桩数据")
    
    @Parameter(name = "dataId", description = "数据id")
    public ResponseResult<SuperPileDto> findSuperPileData(String dataId) {
        return systemMonitorService.findSuperPileData(dataId);
    }

    @PostMapping("findPileGunPowerList")
    @Operation(summary = "查询电桩充电枪功率曲线数据")
    
    @Parameter(name = "dataId", description = "数据id")
    public ResponseResult<List<PileGunPowerDto>> findPileGunPowerList(String dataId) {
        return systemMonitorService.findPileGunPowerList(dataId);
    }

    @PostMapping("findSystemCurve")
    @Operation(summary = "查询系统曲线数据")
    
    public ResponseResult<SystemCurveDto> findSystemCurve(SystemQueryVo systemQueryVo) {
        return systemMonitorService.findSystemCurve(systemQueryVo);
    }

    @PostMapping("findAllFaultAlarmList")
    @Operation(summary = "查询故障告警列表数据")
    
    public ResponseResult<PageDto<FaultAlarmListDto>> findAllFaultAlarmList(FaultAlarmQueryVo faultAlarmVo) {
        return systemMonitorService.findAllFaultAlarmList(faultAlarmVo);
    }

    @PostMapping("getDeviceFieldList")
    @Operation(summary = "获取站点及站点下面的设备数据字段")
    
    public ResponseResult<DeviceFieldDto> getDeviceFieldList(String siteId) {
        return systemMonitorService.getDeviceFieldList(siteId);
    }

    @PostMapping("findAllHistoryDataList")
    @Operation(summary = "查询站点设备历史数据")
    
    public ResponseResult<HistoryDataDto> findAllHistoryDataList(HistoryDataQueryVo historyDataVo) {
        return systemMonitorService.findAllHistoryDataList(historyDataVo);
    }

    @PostMapping("findSiteDetailById")
    @Operation(summary = "根据站点id查询站点详情数据")
    
    @Parameter(name = "siteId", description = "站点id")
    public ResponseResult<SiteDetailDto> findSiteDetailById(String siteId) {
        return systemMonitorService.findSiteDetailById(siteId);
    }

    @PostMapping("findAllCellList")
    @Operation(summary = "根据数据id和类型查询电池簇电芯列表")
    
    @Parameters({
            @Parameter(name = "dataId", description = "数据id"),
            @Parameter(name = "queryType", description = "查询类型 1-温度 2-电压"),
            @Parameter(name = "page", description = "当前页数"),
            @Parameter(name = "size", description = "当前页条数")
    })
    public ResponseResult<PageDto<Object>> findAllCellList(String dataId, Integer queryType, Integer page, Integer size) {
        return systemMonitorService.findAllCellList(dataId, queryType, page, size);
    }

    @PostMapping("findSystemChangeData")
    @Operation(summary = "查询换电系统数据")
    
    @Parameter(name = "dataId", description = "数据id")
    public ResponseResult<SystemChangeDto> findSystemChangeData(String dataId) {
        return systemMonitorService.findSystemChangeData(dataId);
    }

    @PostMapping("findSystemMeterData")
    @Operation(summary = "查询电表数据")
    
    @Parameter(name = "dataId", description = "数据id")
    public ResponseResult<SystemMeterDto> findSystemMeterData(String dataId) {
        return systemMonitorService.findSystemMeterData(dataId);
    }

}
