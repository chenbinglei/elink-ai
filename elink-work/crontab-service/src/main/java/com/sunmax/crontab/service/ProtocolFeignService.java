package com.sunmax.crontab.service;

import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.vo.crontab.PileStartControlVo;

public interface ProtocolFeignService {

    /**
     * 校验电桩平台控制
     * @param pileStartControlVo 电桩启动控制参数
     * @return true-需要调控 fasle-不需要调控
     */
    ResponseResult<Boolean> checkPileStartControl( PileStartControlVo pileStartControlVo);

    /**
     * 更新电桩平台控制状态
     * @param pileStartControlVo 电桩启动控制结果参数
     * @return true-成功 false-失败
     */
    ResponseResult<Boolean> updatePileStartControl( PileStartControlVo pileStartControlVo);

}
