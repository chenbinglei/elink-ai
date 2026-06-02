package com.sunmax.together.controller.custom;

import com.sunmax.common.util.ResponseResult;
import com.sunmax.together.dto.custom.*;
import com.sunmax.together.service.custom.CustomSystemService;
import com.sunmax.together.vo.operation.storageCount.StorageCountVo;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("customSystem")
@Api(tags = "定制系统管理控制层")
public class CustomSystemController {

    @Autowired
    private CustomSystemService customSystemService;

    @PostMapping("getSiteOverview")
    @ApiOperation("获取电站概览")
    @ApiIgnore
    @ApiOperationSupport(order = 1)
    @ApiImplicitParam(name = "siteId", value = "站点id", dataType = "String", required = true)
    public ResponseResult<SiteOverviewDto> getSiteOverview(String siteId) {
        return customSystemService.getSiteOverview(siteId);
    }

    @PostMapping("getSiteAcSystem")
    @ApiOperation("获取电站VS交流系统")
    @ApiOperationSupport(order = 2)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "siteId", value = "站点id", dataType = "String", required = true),
            @ApiImplicitParam(name = "type", value = "类型 1-平均 2-累计", dataType = "int", required = true)
    })
    public ResponseResult<SiteAcSystemDto> getSiteAcSystem(String siteId, Integer type) {
        return customSystemService.getSiteAcSystem(siteId, type);
    }

    @PostMapping("getSiteQtCurve")
    @ApiOperation("电站电量统计")
    @ApiOperationSupport(order = 3)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "siteId", value = "站点id", dataType = "String", required = true),
            @ApiImplicitParam(name = "type", value = "类型 1-关口 2-直流母线 3-光伏 4-储能 5-负载", dataType = "int", required = true),
            @ApiImplicitParam(name = "dateType", value = "日期类型 1-日 2-月 3-年", dataType = "int", required = true)
    })
    public ResponseResult<SiteQtCurveDto> getSiteQtCurve(String siteId, Integer type, Integer dateType) {
        return customSystemService.getSiteQtCurve(siteId, type, dateType);
    }

    @PostMapping("getSiteAlarmList")
    @ApiOperation("电站告警信息列表")
    @ApiOperationSupport(order = 4)
    @ApiImplicitParam(name = "siteId", value = "站点id", dataType = "String", required = true)
    public ResponseResult<List<SiteAlarmDto>> getSiteAlarmList(String siteId) {
        return customSystemService.getSiteAlarmList(siteId);
    }

    @PostMapping("getPvDcDcDeviceData")
    @ApiOperation("获取光伏DC/DC设备数据")
    @ApiOperationSupport(order = 5)
    @ApiImplicitParam(name = "deviceId", value = "设备id", dataType = "String", required = true)
    public ResponseResult<PvDcDcDeviceDto> getPvDcDcDeviceData(String deviceId) {
        return customSystemService.getPvDcDcDeviceData(deviceId);
    }

    @PostMapping("getSeCabinetDeviceData")
    @ApiOperation("获取储能柜(储能DC/DC和电池蔟)设备数据")
    @ApiOperationSupport(order = 6)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "seDcIds", value = "多个储能DC/DC的id", dataType = "String", required = true),
            @ApiImplicitParam(name = "batteryIds", value = "多个电池蔟id", dataType = "String", required = true)
    })
    public ResponseResult<SeCabinetDeviceDto> getSeCabinetDeviceData(String seDcIds, String batteryIds) {
        return customSystemService.getSeCabinetDeviceData(seDcIds, batteryIds);
    }

    @PostMapping("getAcGGDDeviceData")
    @ApiOperation("获取交流配电柜(关口)设备数据")
    @ApiOperationSupport(order = 7)
    @ApiImplicitParam(name = "deviceId", value = "设备id", dataType = "String", required = true)
    public ResponseResult<AcGGDDeviceDto> getAcGGDDeviceData(String deviceId) {
        return customSystemService.getAcGGDDeviceData(deviceId);
    }

    @PostMapping("getDCADDeviceData")
    @ApiOperation("获取直流配电柜(负载)设备数据")
    @ApiOperationSupport(order = 8)
    @ApiImplicitParam(name = "deviceId", value = "设备id", dataType = "String", required = true)
    public ResponseResult<DCADDeviceDto> getDCADDeviceData(String deviceId) {
        return customSystemService.getDCADDeviceData(deviceId);
    }

    @PostMapping("getDCBusDeviceData")
    @ApiOperation("获取直流母线柜(母线柜)设备数据")
    @ApiOperationSupport(order = 9)
    @ApiImplicitParam(name = "deviceId", value = "设备id", dataType = "String", required = true)
    public ResponseResult<DCBusDeviceDto> getDCBusDeviceData(String deviceId) {
        return customSystemService.getDCBusDeviceData(deviceId);
    }

    @PostMapping("getDCInjectorDeviceData")
    @ApiOperation("获取直流注塑机设备数据")
    @ApiOperationSupport(order = 10)
    @ApiImplicitParam(name = "deviceId", value = "设备id", dataType = "String", required = true)
    @ApiIgnore
    public ResponseResult<DCInjectorDeviceDto> getDCInjectorDeviceData(String deviceId) {
        return customSystemService.getDCInjectorDeviceData(deviceId);
    }

    @PostMapping("getSiteSystemNearby")
    @ApiOperation("获取站点综合分析")
    @ApiOperationSupport(order = 11)
    public ResponseResult<SiteSystemNearbyDto> getSiteSystemNearby(StorageCountVo storageCountVo) {
        return customSystemService.getSiteSystemNearby(storageCountVo);
    }


}
