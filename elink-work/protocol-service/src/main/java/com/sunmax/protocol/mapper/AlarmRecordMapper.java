package com.sunmax.protocol.mapper;

import com.sunmax.common.dto.protocol.AlarmNumDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

@Mapper
public interface AlarmRecordMapper {

    //统计设备告警次数
    List<AlarmNumDto> countDeviceAlarmNum(@Param("deviceCodes") Set<String> deviceCodes, @Param("startTime") String startTime, @Param("endTime") String endTime);
}
