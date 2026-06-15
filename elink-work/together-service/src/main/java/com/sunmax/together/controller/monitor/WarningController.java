package com.sunmax.together.controller.monitor;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

import com.alibaba.fastjson2.JSON;
import com.sunmax.common.dto.PageDto;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.util.StringUtil;
import com.sunmax.log.config.WebLog;
import com.sunmax.together.dto.monitor.centralMonitor.*;
import com.sunmax.together.service.monitor.WarningService;
import com.sunmax.together.vo.monitor.centralMonitor.AlarmQueryVo;
import com.sunmax.together.vo.monitor.centralMonitor.DeviceAlarmQueryVo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping("/warning")
@Tag(name = "告警管理控制层")
public class WarningController {

    @Autowired
    private WarningService warningService;

    @PostMapping("getEventList")
    @Operation(summary = "根据系统id查询设备告警事件列表")
//    @WebLog("集中监控-查询设备告警数据")
    
    @Parameter(name = "systemId", description = "系统id")
    public ResponseResult<List<SystemEventListDto>> getEventList(String systemId) {
        return warningService.getEventList(systemId);
    }

    @PostMapping("queryAlarmEventList")
    @Operation(summary = "查询告警事件列表")
//    @WebLog("查询告警事件列表")
    
    public ResponseResult<PageDto<AlarmListDto>> queryAlarmEventList(AlarmQueryVo alarmQueryVo) {
        return warningService.queryAlarmEventList(alarmQueryVo);
    }

    @PostMapping("updateEventIgnoreStatus")
    @Operation(summary = "修改设备事件忽略状态")
    @WebLog("修改设备事件忽略状态")
    
    @Parameters({
            @Parameter(name = "id", description = "设备事件主键id"),
            @Parameter(name = "ignoreStatus", description = "忽略状态 0-未忽略 1-已忽略")
    })
    public ResponseResult<Void> updateEventIgnoreStatus(String id, Integer ignoreStatus) {
        return warningService.updateEventIgnoreStatus(id, ignoreStatus);
    }

    @PostMapping("alarmSiteCount")
    @Operation(summary = "告警站点统计")
//    @WebLog("告警站点统计")
    
    @Parameters({
            @Parameter(name = "userId", description = "用户id"),
            @Parameter(name = "siteIds", description = "多个站点id 字符串数组 例如['siteId1','siteId2']"),
            @Parameter(name = "siteId", description = "站点id"),
            @Parameter(name = "startDate", description = "开始日期"),
            @Parameter(name = "endDate", description = "结束日期")
    })
    public ResponseResult<AlarmSiteCountDto> alarmSiteCount(String userId, String siteIds, String siteId, String startDate, String endDate) {
        return warningService.alarmSiteCount(userId, StringUtil.isNotEmpty(siteIds) ? JSON.parseArray(siteIds, String.class) : null, siteId, startDate, endDate);
    }

    @PostMapping("alarmDeviceCount")
    @Operation(summary = "告警设备统计")
//    @WebLog("告警设备统计")
    
    @Parameters({
            @Parameter(name = "deviceId", description = "设备id"),
            @Parameter(name = "startDate", description = "开始日期"),
            @Parameter(name = "endDate", description = "结束日期")
    })
    public ResponseResult<AlarmDeviceCountDto> alarmDeviceCount(String deviceId, String startDate, String endDate) {
        return warningService.alarmDeviceCount(deviceId, startDate, endDate);
    }

    @PostMapping("findDeviceAlarmEventList")
    @Operation(summary = "根据查询条件查询设备历史告警数据")
//    @WebLog("根据查询条件查询设备历史告警数据")
    
    public ResponseResult<Map<String, List<DeviceAlarmDto>>> findDeviceAlarmEventList(DeviceAlarmQueryVo deviceAlarmQueryVo) {
        return warningService.findAlarmEventListByDeviceId(deviceAlarmQueryVo);
    }

}
