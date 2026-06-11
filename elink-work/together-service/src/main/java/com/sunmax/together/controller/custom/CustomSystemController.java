package com.sunmax.together.controller.custom;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

import com.sunmax.common.util.ResponseResult;
import com.sunmax.together.dto.custom.*;
import com.sunmax.together.service.custom.CustomSystemService;
import com.sunmax.together.vo.operation.storageCount.StorageCountVo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Hidden;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("customSystem")
@Tag(name = "定制系统管理控制层")
public class CustomSystemController {

    @Autowired
    private CustomSystemService customSystemService;

    @PostMapping("getSiteOverview")
    @Operation(summary = "获取电站概览")
    @Hidden
    
    @Parameter(name = "siteId", description = "站点id")
    public ResponseResult<SiteOverviewDto> getSiteOverview(String siteId) {
        return customSystemService.getSiteOverview(siteId);
    }

    @PostMapping("getSiteAcSystem")
    @Operation(summary = "获取电站VS交流系统")
    
    @Parameters({
            @Parameter(name = "siteId", description = "站点id"),
            @Parameter(name = "type", description = "类型 1-平均 2-累计")
    })
    public ResponseResult<SiteAcSystemDto> getSiteAcSystem(String siteId, Integer type) {
        return customSystemService.getSiteAcSystem(siteId, type);
    }

    @PostMapping("getSiteQtCurve")
    @Operation(summary = "电站电量统计")
    
    @Parameters({
            @Parameter(name = "siteId", description = "站点id"),
            @Parameter(name = "type", description = "类型 1-关口 2-直流母线 3-光伏 4-储能 5-负载"),
            @Parameter(name = "dateType", description = "日期类型 1-日 2-月 3-年")
    })
    public ResponseResult<SiteQtCurveDto> getSiteQtCurve(String siteId, Integer type, Integer dateType) {
        return customSystemService.getSiteQtCurve(siteId, type, dateType);
    }

    @PostMapping("getSiteAlarmList")
    @Operation(summary = "电站告警信息列表")
    
    @Parameter(name = "siteId", description = "站点id")
    public ResponseResult<List<SiteAlarmDto>> getSiteAlarmList(String siteId) {
        return customSystemService.getSiteAlarmList(siteId);
    }

    @PostMapping("getPvDcDcDeviceData")
    @Operation(summary = "获取光伏DC/DC设备数据")
    
    @Parameter(name = "deviceId", description = "设备id")
    public ResponseResult<PvDcDcDeviceDto> getPvDcDcDeviceData(String deviceId) {
        return customSystemService.getPvDcDcDeviceData(deviceId);
    }

    @PostMapping("getSeCabinetDeviceData")
    @Operation(summary = "获取储能柜(储能DC/DC和电池蔟)设备数据")
    
    @Parameters({
            @Parameter(name = "seDcIds", description = "多个储能DC/DC的id"),
            @Parameter(name = "batteryIds", description = "多个电池蔟id")
    })
    public ResponseResult<SeCabinetDeviceDto> getSeCabinetDeviceData(String seDcIds, String batteryIds) {
        return customSystemService.getSeCabinetDeviceData(seDcIds, batteryIds);
    }

    @PostMapping("getAcGGDDeviceData")
    @Operation(summary = "获取交流配电柜(关口)设备数据")
    
    @Parameter(name = "deviceId", description = "设备id")
    public ResponseResult<AcGGDDeviceDto> getAcGGDDeviceData(String deviceId) {
        return customSystemService.getAcGGDDeviceData(deviceId);
    }

    @PostMapping("getDCADDeviceData")
    @Operation(summary = "获取直流配电柜(负载)设备数据")
    
    @Parameter(name = "deviceId", description = "设备id")
    public ResponseResult<DCADDeviceDto> getDCADDeviceData(String deviceId) {
        return customSystemService.getDCADDeviceData(deviceId);
    }

    @PostMapping("getDCBusDeviceData")
    @Operation(summary = "获取直流母线柜(母线柜)设备数据")
    
    @Parameter(name = "deviceId", description = "设备id")
    public ResponseResult<DCBusDeviceDto> getDCBusDeviceData(String deviceId) {
        return customSystemService.getDCBusDeviceData(deviceId);
    }

    @PostMapping("getDCInjectorDeviceData")
    @Operation(summary = "获取直流注塑机设备数据")
    
    @Parameter(name = "deviceId", description = "设备id")
    @Hidden
    public ResponseResult<DCInjectorDeviceDto> getDCInjectorDeviceData(String deviceId) {
        return customSystemService.getDCInjectorDeviceData(deviceId);
    }

    @PostMapping("getSiteSystemNearby")
    @Operation(summary = "获取站点综合分析")
    
    public ResponseResult<SiteSystemNearbyDto> getSiteSystemNearby(StorageCountVo storageCountVo) {
        return customSystemService.getSiteSystemNearby(storageCountVo);
    }


}
