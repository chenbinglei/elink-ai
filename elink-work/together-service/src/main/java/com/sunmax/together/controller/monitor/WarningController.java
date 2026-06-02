package com.sunmax.together.controller.monitor;

import com.alibaba.fastjson.JSON;
import com.sunmax.common.dto.PageDto;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.util.StringUtil;
import com.sunmax.log.config.WebLog;
import com.sunmax.together.dto.monitor.centralMonitor.*;
import com.sunmax.together.service.monitor.WarningService;
import com.sunmax.together.vo.monitor.centralMonitor.AlarmQueryVo;
import com.sunmax.together.vo.monitor.centralMonitor.DeviceAlarmQueryVo;
import io.swagger.annotations.*;
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
@Api(tags = "告警管理控制层")
public class WarningController {

    @Autowired
    private WarningService warningService;

    @PostMapping("getEventList")
    @ApiOperation("根据系统id查询设备告警事件列表")
//    @WebLog("集中监控-查询设备告警数据")
    @ApiOperationSupport(order = 1)
    @ApiImplicitParam(name = "systemId", value = "系统id", dataType = "String")
    public ResponseResult<List<SystemEventListDto>> getEventList(String systemId) {
        return warningService.getEventList(systemId);
    }

    @PostMapping("queryAlarmEventList")
    @ApiOperation("查询告警事件列表")
//    @WebLog("查询告警事件列表")
    @ApiOperationSupport(order = 2)
    public ResponseResult<PageDto<AlarmListDto>> queryAlarmEventList(AlarmQueryVo alarmQueryVo) {
        return warningService.queryAlarmEventList(alarmQueryVo);
    }

    @PostMapping("updateEventIgnoreStatus")
    @ApiOperation("修改设备事件忽略状态")
    @WebLog("修改设备事件忽略状态")
    @ApiOperationSupport(order = 3)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "设备事件主键id", dataType = "String", required = true),
            @ApiImplicitParam(name = "ignoreStatus", value = "忽略状态 0-未忽略 1-已忽略", dataType = "int", required = true)
    })
    public ResponseResult<Void> updateEventIgnoreStatus(String id, Integer ignoreStatus) {
        return warningService.updateEventIgnoreStatus(id, ignoreStatus);
    }

    @PostMapping("alarmSiteCount")
    @ApiOperation("告警站点统计")
//    @WebLog("告警站点统计")
    @ApiOperationSupport(order = 4)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户id", dataType = "String", required = true),
            @ApiImplicitParam(name = "siteIds", value = "多个站点id 字符串数组 例如['siteId1','siteId2']", dataType = "String"),
            @ApiImplicitParam(name = "siteId", value = "站点id", dataType = "String"),
            @ApiImplicitParam(name = "startDate", value = "开始日期", dataType = "String", required = true),
            @ApiImplicitParam(name = "endDate", value = "结束日期", dataType = "String", required = true)
    })
    public ResponseResult<AlarmSiteCountDto> alarmSiteCount(String userId, String siteIds, String siteId, String startDate, String endDate) {
        return warningService.alarmSiteCount(userId, StringUtil.isNotEmpty(siteIds) ? JSON.parseArray(siteIds, String.class) : null, siteId, startDate, endDate);
    }

    @PostMapping("alarmDeviceCount")
    @ApiOperation("告警设备统计")
//    @WebLog("告警设备统计")
    @ApiOperationSupport(order = 5)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "deviceId", value = "设备id", dataType = "String"),
            @ApiImplicitParam(name = "startDate", value = "开始日期", dataType = "String", required = true),
            @ApiImplicitParam(name = "endDate", value = "结束日期", dataType = "String", required = true)
    })
    public ResponseResult<AlarmDeviceCountDto> alarmDeviceCount(String deviceId, String startDate, String endDate) {
        return warningService.alarmDeviceCount(deviceId, startDate, endDate);
    }

    @PostMapping("findDeviceAlarmEventList")
    @ApiOperation("根据查询条件查询设备历史告警数据")
//    @WebLog("根据查询条件查询设备历史告警数据")
    @ApiOperationSupport(order = 6)
    public ResponseResult<Map<String, List<DeviceAlarmDto>>> findDeviceAlarmEventList(DeviceAlarmQueryVo deviceAlarmQueryVo) {
        return warningService.findAlarmEventListByDeviceId(deviceAlarmQueryVo);
    }

}
