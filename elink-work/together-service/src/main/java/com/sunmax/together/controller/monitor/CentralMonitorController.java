package com.sunmax.together.controller.monitor;

import com.sunmax.common.dto.PageDto;
import com.sunmax.common.dto.WeatherDayDto;
import com.sunmax.common.dto.device.SiteTopDataDto;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.together.dto.monitor.centralMonitor.*;
import com.sunmax.together.service.monitor.CentralMonitorService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping
@Api(tags = "集中监控控制层")
@CrossOrigin
public class CentralMonitorController {

    @Autowired
    private CentralMonitorService centralMonitorService;

    @PostMapping("/centralMonitor/statusTotal")
    @ApiOperation("场站状态总计")
    @ApiOperationSupport(order = 1)
    @ApiImplicitParam(name = "userId", value = "用户id", dataType = "String")
    public ResponseResult<Map<String, Integer>> statusTotal(String userId) {
        return centralMonitorService.statusTotal(userId);
    }

    @PostMapping("/centralMonitor/statistics")
    @ApiOperation("场站统计")
    @ApiOperationSupport(order = 2)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "word", value = "关键字", dataType = "String"),
            @ApiImplicitParam(name = "area", value = "区域名称", dataType = "String"),
            @ApiImplicitParam(name = "userId", value = "用户id", dataType = "String"),
            @ApiImplicitParam(name = "page", value = "当前页", dataType = "int"),
            @ApiImplicitParam(name = "size", value = "当前页条数", dataType = "int"),
    })
    public ResponseResult<PageDto<SiteStatisticsDto>> statistics(String word, String area, String userId, Integer page, Integer size) {
        return centralMonitorService.statistics(word, area, userId, page, size);
    }

    @PostMapping("/photovoltaic/page")
    @ApiOperation("光伏场站分页")
    @ApiOperationSupport(order = 3)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "word", value = "关键字", dataType = "String"),
            @ApiImplicitParam(name = "area", value = "区域名称", dataType = "String"),
            @ApiImplicitParam(name = "userId", value = "用户id", dataType = "String"),
            @ApiImplicitParam(name = "page", value = "当前页", dataType = "int"),
            @ApiImplicitParam(name = "size", value = "当前页条数", dataType = "int")
    })
    public ResponseResult<PageDto<PhotovoltaicDto>> photovoltaicPage(String word, String area, String userId, Integer page, Integer size) {
        return centralMonitorService.photovoltaicPage(word, area, userId, page, size);
    }

    @PostMapping("/energyStorage/page")
    @ApiOperation("储能场站分页")
    @ApiOperationSupport(order = 4)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "word", value = "关键字", dataType = "String"),
            @ApiImplicitParam(name = "area", value = "区域名称", dataType = "String"),
            @ApiImplicitParam(name = "userId", value = "用户id", dataType = "String"),
            @ApiImplicitParam(name = "page", value = "当前页", dataType = "int"),
            @ApiImplicitParam(name = "size", value = "当前页条数", dataType = "int")
    })
    public ResponseResult<PageDto<EnergyStorageDto>> energyStoragePage(String word, String area, String userId, Integer page, Integer size) {
        return centralMonitorService.energyStoragePage(word, area, userId, page, size);
    }

    @PostMapping("/batterySupply/page")
    @ApiOperation("充电系统分页")
    @ApiOperationSupport(order = 5)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "word", value = "关键字", dataType = "String"),
            @ApiImplicitParam(name = "area", value = "区县", dataType = "String"),
            @ApiImplicitParam(name = "userId", value = "用户id", dataType = "String"),
            @ApiImplicitParam(name = "page", value = "页面数", dataType = "int"),
            @ApiImplicitParam(name = "size", value = "页面大小", dataType = "int"),
    })
    public ResponseResult<PageDto<BatterySupplyDto>> batterySupplyPage(String word, String area, String userId, Integer page, Integer size) {
        return centralMonitorService.batterySupplyPage(word, area, userId, page, size);
    }

    @PostMapping("/batteryChange/page")
    @ApiOperation("换电系统分页")
    @ApiOperationSupport(order = 6)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "word", value = "关键字", dataType = "String"),
            @ApiImplicitParam(name = "area", value = "区县", dataType = "String"),
            @ApiImplicitParam(name = "userId", value = "用户id", dataType = "String"),
            @ApiImplicitParam(name = "page", value = "页面数", dataType = "int"),
            @ApiImplicitParam(name = "size", value = "页面大小", dataType = "int"),
    })
    public ResponseResult<PageDto<BatteryChangeDto>> batteryChangePage(String word, String area, String userId, Integer page, Integer size) {
        return centralMonitorService.batteryChangePage(word, area, userId, page, size);
    }

    @PostMapping("/centralMonitor/getDeviceList")
    @ApiOperation("设备列表概览")
    @ApiOperationSupport(order = 7)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "siteId", value = "站点id", required = true),
            @ApiImplicitParam(name = "type", value = "类型 1-光伏 2-储能 3-充电桩 4-用能 5-配电 6-换电", required = true)
    })
    public ResponseResult<List<DeviceOverviewDto>> getDeviceList(String siteId, Integer type) {
        return centralMonitorService.getDeviceList(siteId, type);
    }

    @PostMapping("/centralMonitor/findSiteTopDataListBySiteId")
    @ApiOperation("根据站点id查询拓扑节点数据")
    @ApiOperationSupport(order = 8)
    @ApiImplicitParam(name = "siteId", value = "站点id", required = true)
    public ResponseResult<List<SiteTopDataDto>> findSiteTopDataListBySiteId(String siteId) {
        return centralMonitorService.findSiteTopDataListBySiteId(siteId);
    }

    @PostMapping("/centralMonitor/findSiteGateTopBySiteId")
    @ApiOperation("根据站点id查询关口表数据")
    @ApiOperationSupport(order = 9)
    @ApiImplicitParam(name = "siteId", value = "站点id", required = true)
    public ResponseResult<List<SiteGateTopDto>> findSiteGateTopBySiteId(String siteId) {
        return centralMonitorService.findSiteGateTopBySiteId(siteId);
    }

    @PostMapping("/centralMonitor/findSiteTopCurveList")
    @ApiOperation("根据站点id和关口表节点id查询曲线数据")
    @ApiOperationSupport(order = 10)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "siteId", value = "站点id", required = true),
            @ApiImplicitParam(name = "nodeId", value = "节点id", required = true),
            @ApiImplicitParam(name = "queryDate", value = "查询日期(yyyy-MM-dd)", required = true)
    })
    public ResponseResult<SiteTopCurveDto> findSiteTopCurveList(String siteId, String nodeId, String queryDate) {
        return centralMonitorService.findSiteTopCurveList(siteId, nodeId, queryDate);
    }

    @PostMapping("/centralMonitor/getWeatherDayListBySiteId")
    @ApiOperation("根据站点id获取天气预报")
    @ApiOperationSupport(order = 11)
    @ApiImplicitParam(name = "siteId", value = "站点id", required = true)
    public ResponseResult<List<WeatherDayDto>> getWeatherDayListBySiteId(String siteId) {
        return centralMonitorService.getWeatherDayListBySiteId(siteId);
    }

}
