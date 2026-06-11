package com.sunmax.crontab.controller;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

import com.sunmax.common.dto.crontab.SystemVarInfoDto;
import com.sunmax.common.dto.operate.ConfigurationResultDto;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.vo.crontab.DeviceVarChartQueryVo;
import com.sunmax.common.vo.crontab.GunSystemVarChartQueryVo;
import com.sunmax.common.vo.crontab.SystemVarNewValueVo;
import com.sunmax.crontab.dto.SiteDeviceDataDto;
import com.sunmax.crontab.service.ConfigFuncPointService;
import com.sunmax.crontab.vo.SiteDeviceQueryVo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("configFuncPoint")
@Tag(name = "组态功能点数据查询管理")
public class ConfigFuncPointController {

    @Autowired
    private ConfigFuncPointService configFuncPointService;

    @PostMapping("findGunSystemVarChartData")
    @Operation(summary = "查询电枪系统变量图表数据")
    
    public ResponseResult<ConfigurationResultDto> findGunSystemVarChartData(@RequestBody GunSystemVarChartQueryVo chartQueryVo) {
        return configFuncPointService.findGunSystemVarChartData(chartQueryVo);
    }

    @PostMapping("findDeviceSystemVarChartData")
    @Operation(summary = "查询站点/设备系统变量图表数据")
    
    public ResponseResult<ConfigurationResultDto> findDeviceSystemVarChartData(@RequestBody DeviceVarChartQueryVo deviceChartQueryVo) {
        return configFuncPointService.findDeviceSystemVarChartData(deviceChartQueryVo);
    }

    @PostMapping("findSystemVarNewValue")
    @Operation(summary = "查询系统变量最新值数据")
    
    @Parameters({
            @Parameter(name = "deviceIds", description = "多个设备id(以逗号分割)"),
            @Parameter(name = "siteId", description = "站点唯一id(不可传多个，和设备编码同时只能传一个)"),
            @Parameter(name = "varCodes", description = "多个系统变量标识(以逗号分割)")
    })
    public ResponseResult<ConfigurationResultDto> findSystemVarNewValue(@RequestBody SystemVarNewValueVo systemVarNewValueVo) {
        return configFuncPointService.findSystemVarNewValue(systemVarNewValueVo);
    }

    @PostMapping("findSystemVarListByDeviceId")
    @Operation(summary = "根据站点/设备id查询系统变量列表")
    
    @Parameters({
            @Parameter(name = "deviceId", description = "站点/设备唯一id"),
            @Parameter(name = "queryType", description = "查询类型 1-站点 2-设备")
    })
    public ResponseResult<List<SystemVarInfoDto>> findSystemVarListByDeviceId(@RequestParam String deviceId, @RequestParam Integer queryType) {
        return configFuncPointService.findSystemVarListByDeviceId(deviceId, queryType);
    }

    @PostMapping("findSiteDeviceDataList")
    @Operation(summary = "查询站点设备组件历史数据")
    
    public ResponseResult<SiteDeviceDataDto> findSiteDeviceDataList(@RequestBody SiteDeviceQueryVo siteDeviceQueryVo) {
        return configFuncPointService.findSiteDeviceDataList(siteDeviceQueryVo);
    }

}
