package com.sunmax.protocol.dao;

import com.sunmax.common.dao.base.BaseDao;
import com.sunmax.protocol.entity.AlarmRecordEntity;

import java.util.List;
import java.util.Set;

public interface AlarmRecordDao extends BaseDao<AlarmRecordEntity, String> {

    //根据设备编号和故障码查询告警记录
    List<AlarmRecordEntity> findAllByDeviceCodeAndFaultCodeInAndAlarmStatus(String deviceCode, Set<Integer> faultCodes, Integer alarmStatus);

    //根据多个设备编号查询告警记录数据
    List<AlarmRecordEntity> findAllByDeviceCodeIn(Set<String> deviceCodes);

    //根据多个设备编号查询告警记录数据
    List<AlarmRecordEntity> findAllByDeviceCodeInAndFaultCodeAndAlarmStatus(Set<String> deviceCodes, Integer faultCodes, Integer alarmStatus);

    //根据设备编号、故障码、模块地址和告警状态查询告警记录（用于电力模块故障）
    List<AlarmRecordEntity> findAllByDeviceCodeAndFaultCodeAndModuleAddrInAndAlarmStatus(String deviceCode, Integer faultCode, Set<Integer> moduleAddrs, Integer alarmStatus);

}
