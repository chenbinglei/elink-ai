package com.sunmax.protocol.service;

import com.sunmax.common.dto.protocol.AlarmNumDto;
import com.sunmax.common.dto.PageDto;
import com.sunmax.common.dto.protocol.AlarmRecordDto;
import com.sunmax.common.dto.protocol.ProtocolListDto;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.vo.protocol.AlarmRecordQueryVo;
import com.sunmax.common.vo.protocol.PileBatchUpdateVo;
import com.sunmax.common.vo.protocol.ProtocolQueryVo;

import java.util.List;
import java.util.Set;

public interface DeviceFeignService {

    /**
     * 添加设备主题
     * @param deviceCode 设备编号
     * @return 状态码
     */
    ResponseResult<Void> addDeviceTopic(String deviceCode);

    /**
     * 删除设备主题
     * @param deviceCode 设备编号
     * @return 状态码
     */
    ResponseResult<Void> deleteDeviceTopic(String deviceCode);

    /**
     * 根据查询条件查询设备告警数据
     * @param alarmQueryVo 查询条件
     * @return 设备告警数据
     */
    ResponseResult<List<AlarmRecordDto>> findAlarmRecordList(AlarmRecordQueryVo alarmQueryVo);

    /**
     * 根据多个设备编号删除告警记录数据
     * @param deviceCodes 多个设备编号
     * @return 告警记录数据
     */
    ResponseResult<Void> deleteAllAlarmRecord(Set<String> deviceCodes);

    /**
     * 统计设备告警次数
     * @param deviceCodes
     * @param startTime
     * @param endTime
     * @return
     */
    ResponseResult<List<AlarmNumDto>> countDeviceAlarmNum(Set<String> deviceCodes, String startTime, String endTime);

    /**
     * 查询协议日志列表
     * @param protocolQueryVo 协议查询参数
     * @return 协议日志列表
     */
    ResponseResult<PageDto<ProtocolListDto>> queryProtocolList(ProtocolQueryVo protocolQueryVo);

    /**
     * 批量更新充电桩数据
     * @param pileBatchUpdateVo 充电桩升级数据
     * @return 下发状态响应数据
     */
    ResponseResult<Boolean> batchPileUpdate(PileBatchUpdateVo pileBatchUpdateVo);

    /**
     * 更新设备事件忽略状态
     * @param id 设备事件ID
     * @param ignoreStatus 忽略状态 0-未忽略 1-已忽略
     * @return 状态码
     */
    ResponseResult<Void> updateEventIgnoreStatus(String id, Integer ignoreStatus);

}
