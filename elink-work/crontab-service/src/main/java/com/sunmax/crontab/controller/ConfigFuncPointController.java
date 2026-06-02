package com.sunmax.crontab.controller;

import com.sunmax.common.dto.crontab.SystemVarInfoDto;
import com.sunmax.common.dto.operate.ConfigurationResultDto;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.vo.crontab.DeviceVarChartQueryVo;
import com.sunmax.common.vo.crontab.GunSystemVarChartQueryVo;
import com.sunmax.common.vo.crontab.SystemVarNewValueVo;
import com.sunmax.crontab.dto.SiteDeviceDataDto;
import com.sunmax.crontab.service.ConfigFuncPointService;
import com.sunmax.crontab.vo.SiteDeviceQueryVo;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("configFuncPoint")
@Api(tags = "组态功能点数据查询管理")
public class ConfigFuncPointController {

    @Autowired
    private ConfigFuncPointService configFuncPointService;

    @PostMapping("findGunSystemVarChartData")
    @ApiOperation("查询电枪系统变量图表数据")
    @ApiOperationSupport(order = 1)
    public ResponseResult<ConfigurationResultDto> findGunSystemVarChartData(@RequestBody GunSystemVarChartQueryVo chartQueryVo) {
        return configFuncPointService.findGunSystemVarChartData(chartQueryVo);
    }

    @PostMapping("findDeviceSystemVarChartData")
    @ApiOperation("查询站点/设备系统变量图表数据")
    @ApiOperationSupport(order = 2)
    public ResponseResult<ConfigurationResultDto> findDeviceSystemVarChartData(@RequestBody DeviceVarChartQueryVo deviceChartQueryVo) {
        return configFuncPointService.findDeviceSystemVarChartData(deviceChartQueryVo);
    }

    @PostMapping("findSystemVarNewValue")
    @ApiOperation("查询系统变量最新值数据")
    @ApiOperationSupport(order = 3)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "deviceIds", value = "多个设备id(以逗号分割)", dataType = "String"),
            @ApiImplicitParam(name = "siteId", value = "站点唯一id(不可传多个，和设备编码同时只能传一个)", dataType = "String"),
            @ApiImplicitParam(name = "varCodes", value = "多个系统变量标识(以逗号分割)", dataType = "String")
    })
    public ResponseResult<ConfigurationResultDto> findSystemVarNewValue(@RequestBody SystemVarNewValueVo systemVarNewValueVo) {
        return configFuncPointService.findSystemVarNewValue(systemVarNewValueVo);
    }

    @PostMapping("findSystemVarListByDeviceId")
    @ApiOperation("根据站点/设备id查询系统变量列表")
    @ApiOperationSupport(order = 4)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "deviceId", value = "站点/设备唯一id", dataType = "String", required = true),
            @ApiImplicitParam(name = "queryType", value = "查询类型 1-站点 2-设备", dataType = "Integer")
    })
    public ResponseResult<List<SystemVarInfoDto>> findSystemVarListByDeviceId(@RequestParam String deviceId, @RequestParam Integer queryType) {
        return configFuncPointService.findSystemVarListByDeviceId(deviceId, queryType);
    }

    @PostMapping("findSiteDeviceDataList")
    @ApiOperation("查询站点设备组件历史数据")
    @ApiOperationSupport(order = 5)
    public ResponseResult<SiteDeviceDataDto> findSiteDeviceDataList(@RequestBody SiteDeviceQueryVo siteDeviceQueryVo) {
        return configFuncPointService.findSiteDeviceDataList(siteDeviceQueryVo);
    }

}
