package com.sunmax.together.service.monitor;

import com.sunmax.common.dto.PageDto;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.together.dto.monitor.centralMonitor.*;
import com.sunmax.together.vo.monitor.centralMonitor.AlarmQueryVo;
import com.sunmax.together.vo.monitor.centralMonitor.DeviceAlarmQueryVo;

import java.util.List;
import java.util.Map;

public interface WarningService {

    /**
     * 查询设备告警事件列表
     *
     * @param systemId 系统id
     * @return 设备告警事件列表
     */
    ResponseResult<List<SystemEventListDto>> getEventList(String systemId);

    /**
     * 查询设备告警列表
     *
     * @param alarmQueryVo 查询参数
     * @return 设备告警列表
     */
    ResponseResult<PageDto<AlarmListDto>> queryAlarmEventList(AlarmQueryVo alarmQueryVo);

    /**
     * 修改设备告警事件忽略状态
     *
     * @param id           设备告警事件id
     * @param ignoreStatus 忽略状态 0-未忽略 1-已忽略
     * @return 修改结果
     */
    ResponseResult<Void> updateEventIgnoreStatus(String id, Integer ignoreStatus);

    /**
     * 告警站点统计
     *
     * @param userId    用户id
     * @param siteIds   多个站点id
     * @param siteId    站点id
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @return 告警站点统计数据
     */
    ResponseResult<AlarmSiteCountDto> alarmSiteCount(String userId, List<String> siteIds, String siteId, String startDate, String endDate);

    /**
     * 告警设备统计
     *
     * @param deviceId  设备id
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @return 告警设备统计数据
     */
    ResponseResult<AlarmDeviceCountDto> alarmDeviceCount(String deviceId, String startDate, String endDate);

    /**
     * 根据查询条件查询设备历史告警数据
     *
     * @param deviceAlarmQueryVo 设备告警查询参数
     * @return 告警事件列表
     */
    ResponseResult<Map<String, List<DeviceAlarmDto>>> findAlarmEventListByDeviceId(DeviceAlarmQueryVo deviceAlarmQueryVo);

}
