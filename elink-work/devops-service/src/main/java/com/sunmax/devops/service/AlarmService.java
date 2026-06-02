package com.sunmax.devops.service;

import com.sunmax.common.dto.device.AssetTypeDto;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.devops.dto.AlarmResultDto;
import com.sunmax.devops.vo.AlarmQueryVo;

import java.util.List;

public interface AlarmService {

    /**
     * 获取资产类型列表
     * @return 资产类型列表
     */
    ResponseResult<List<AssetTypeDto>> getAssetTypeList();

    /**
     * 查询告警列表
     * @param alarmQueryVo 告警查询参数
     * @return 告警列表
     */
    ResponseResult<AlarmResultDto> queryAlarmList(AlarmQueryVo alarmQueryVo);

}
