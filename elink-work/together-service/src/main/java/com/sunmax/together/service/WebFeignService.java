package com.sunmax.together.service;

import com.sunmax.common.dto.together.PileGunMonitorDataDto;
import com.sunmax.common.util.ResponseResult;

import java.util.List;
import java.util.Map;

public interface WebFeignService {

    /**
     * 根据多个设备id查询电枪监控数据
     * @param deviceIds 多个设备id
     * @return 电枪监控数据(电桩id -> 电枪监控数据)
     */
    ResponseResult<Map<String, List<PileGunMonitorDataDto>>> findAllPileGunMonitorList(List<String> deviceIds);

}
