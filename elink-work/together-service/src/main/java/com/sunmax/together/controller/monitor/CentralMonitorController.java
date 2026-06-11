package com.sunmax.together.controller.monitor;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

import com.sunmax.common.dto.PageDto;
import com.sunmax.common.dto.WeatherDayDto;
import com.sunmax.common.dto.device.SiteTopDataDto;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.together.dto.monitor.centralMonitor.*;
import com.sunmax.together.service.monitor.CentralMonitorService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping
@Tag(name = "集中监控控制层")
@CrossOrigin
public class CentralMonitorController {

    @Autowired
    private CentralMonitorService centralMonitorService;

    @PostMapping("/centralMonitor/statusTotal")
    @Operation(summary = "场站状态总计")
    
    @Parameter(name = "userId", description = "用户id")
    public ResponseResult<Map<String, Integer>> statusTotal(String userId) {
        return centralMonitorService.statusTotal(userId);
    }

    @PostMapping("/centralMonitor/statistics")
    @Operation(summary = "场站统计")
    
    @Parameters({
            @Parameter(name = "word", description = "关键字"),
            @Parameter(name = "area", description = "区域名称"),
            @Parameter(name = "userId", description = "用户id"),
            @Parameter(name = "page", description = "当前页"),
            @Parameter(name = "size", description = "当前页条数"),
    })
    public ResponseResult<PageDto<SiteStatisticsDto>> statistics(String word, String area, String userId, Integer page, Integer size) {
        return centralMonitorService.statistics(word, area, userId, page, size);
    }

    @PostMapping("/photovoltaic/page")
    @Operation(summary = "光伏场站分页")
    
    @Parameters({
            @Parameter(name = "word", description = "关键字"),
            @Parameter(name = "area", description = "区域名称"),
            @Parameter(name = "userId", description = "用户id"),
            @Parameter(name = "page", description = "当前页"),
            @Parameter(name = "size", description = "当前页条数")
    })
    public ResponseResult<PageDto<PhotovoltaicDto>> photovoltaicPage(String word, String area, String userId, Integer page, Integer size) {
        return centralMonitorService.photovoltaicPage(word, area, userId, page, size);
    }

    @PostMapping("/energyStorage/page")
    @Operation(summary = "储能场站分页")
    
    @Parameters({
            @Parameter(name = "word", description = "关键字"),
            @Parameter(name = "area", description = "区域名称"),
            @Parameter(name = "userId", description = "用户id"),
            @Parameter(name = "page", description = "当前页"),
            @Parameter(name = "size", description = "当前页条数")
    })
    public ResponseResult<PageDto<EnergyStorageDto>> energyStoragePage(String word, String area, String userId, Integer page, Integer size) {
        return centralMonitorService.energyStoragePage(word, area, userId, page, size);
    }

    @PostMapping("/batterySupply/page")
    @Operation(summary = "充电系统分页")
    
    @Parameters({
            @Parameter(name = "word", description = "关键字"),
            @Parameter(name = "area", description = "区县"),
            @Parameter(name = "userId", description = "用户id"),
            @Parameter(name = "page", description = "页面数"),
            @Parameter(name = "size", description = "页面大小"),
    })
    public ResponseResult<PageDto<BatterySupplyDto>> batterySupplyPage(String word, String area, String userId, Integer page, Integer size) {
        return centralMonitorService.batterySupplyPage(word, area, userId, page, size);
    }

    @PostMapping("/batteryChange/page")
    @Operation(summary = "换电系统分页")
    
    @Parameters({
            @Parameter(name = "word", description = "关键字"),
            @Parameter(name = "area", description = "区县"),
            @Parameter(name = "userId", description = "用户id"),
            @Parameter(name = "page", description = "页面数"),
            @Parameter(name = "size", description = "页面大小"),
    })
    public ResponseResult<PageDto<BatteryChangeDto>> batteryChangePage(String word, String area, String userId, Integer page, Integer size) {
        return centralMonitorService.batteryChangePage(word, area, userId, page, size);
    }

    @PostMapping("/centralMonitor/getDeviceList")
    @Operation(summary = "设备列表概览")
    
    @Parameters({
            @Parameter(name = "siteId", description = "站点id"),
            @Parameter(name = "type", description = "类型 1-光伏 2-储能 3-充电桩 4-用能 5-配电 6-换电")
    })
    public ResponseResult<List<DeviceOverviewDto>> getDeviceList(String siteId, Integer type) {
        return centralMonitorService.getDeviceList(siteId, type);
    }

    @PostMapping("/centralMonitor/findSiteTopDataListBySiteId")
    @Operation(summary = "根据站点id查询拓扑节点数据")
    
    @Parameter(name = "siteId", description = "站点id")
    public ResponseResult<List<SiteTopDataDto>> findSiteTopDataListBySiteId(String siteId) {
        return centralMonitorService.findSiteTopDataListBySiteId(siteId);
    }

    @PostMapping("/centralMonitor/findSiteGateTopBySiteId")
    @Operation(summary = "根据站点id查询关口表数据")
    
    @Parameter(name = "siteId", description = "站点id")
    public ResponseResult<List<SiteGateTopDto>> findSiteGateTopBySiteId(String siteId) {
        return centralMonitorService.findSiteGateTopBySiteId(siteId);
    }

    @PostMapping("/centralMonitor/findSiteTopCurveList")
    @Operation(summary = "根据站点id和关口表节点id查询曲线数据")
    
    @Parameters({
            @Parameter(name = "siteId", description = "站点id"),
            @Parameter(name = "nodeId", description = "节点id"),
            @Parameter(name = "queryDate", description = "查询日期(yyyy-MM-dd)")
    })
    public ResponseResult<SiteTopCurveDto> findSiteTopCurveList(String siteId, String nodeId, String queryDate) {
        return centralMonitorService.findSiteTopCurveList(siteId, nodeId, queryDate);
    }

    @PostMapping("/centralMonitor/getWeatherDayListBySiteId")
    @Operation(summary = "根据站点id获取天气预报")
    
    @Parameter(name = "siteId", description = "站点id")
    public ResponseResult<List<WeatherDayDto>> getWeatherDayListBySiteId(String siteId) {
        return centralMonitorService.getWeatherDayListBySiteId(siteId);
    }

}
