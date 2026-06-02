package com.sunmax.protocol.service;


import com.sunmax.common.dto.protocol.ControlResponseDto;
import com.sunmax.common.dto.protocol.StationInfoDto;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.vo.protocol.ControlResponseVo;
import com.sunmax.common.vo.protocol.ControlStopVo;

import java.util.List;

public interface DispatchService {

    /**
     * 根据场站id查询场站信息
     *
     * @param stationId 站点id
     * @return 场站信息
     */
    ResponseResult<List<StationInfoDto>> queryStationInfo(Long stationId);

    /**
     * 下发需求响应命令
     *
     * @param controlResponseVo 需求响应命令参数
     * @return 需求响应状态
     */
    ResponseResult<ControlResponseDto> demandControlResponse(ControlResponseVo controlResponseVo);

    /**
     * 下发需求响应终止命令
     *
     * @param controlStopVo 需求响应终止命令
     * @return 需求响应终止命令状态
     */
    ResponseResult<ControlResponseDto> demandControlStop(ControlStopVo controlStopVo);
}
