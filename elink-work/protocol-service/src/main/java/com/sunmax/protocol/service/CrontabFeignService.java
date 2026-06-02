package com.sunmax.protocol.service;

import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.vo.protocol.PilePowerCtrlVo;

import java.util.List;

public interface CrontabFeignService {

    /**
     * 批量对多个充电桩功率控制
     * @param pilePowerCtrlVos 多个充电桩功率控制参数
     * @return 状态码
     */
    ResponseResult<Void> batchPilePowerCtrl(List<PilePowerCtrlVo> pilePowerCtrlVos);

}
