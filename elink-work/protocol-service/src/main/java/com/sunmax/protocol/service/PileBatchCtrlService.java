package com.sunmax.protocol.service;

import com.sunmax.common.dto.protocol.PileResultDto;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.vo.protocol.*;

import java.util.List;

public interface PileBatchCtrlService {

    /**
     * 批量启动多个充电桩
     * @param pileStartVos 多个充电桩启动参数
     * @return 返回执行结果
     */
    ResponseResult<List<PileResultDto>> batchPileStart(List<PileStartVo> pileStartVos);

    /**
     * 批量停止多个充电桩
     * @param pileStopVos 多个充电桩停止参数
     * @return 状态码
     */
    ResponseResult<List<PileResultDto>> batchPileStop(List<PileStopVo> pileStopVos);

    /**
     * 批量对多个充电桩功率控制
     * @param pilePowerCtrlVos 多个充电桩功率控制参数
     * @return 状态码
     */
    ResponseResult<List<PileResultDto>> batchPilePowerCtrl(List<PilePowerCtrlVo> pilePowerCtrlVos);

    /**
     * 批量设置费率数据
     * @param pileRateSetVos 设置费率数据
     * @return 下发状态数据
     */
    ResponseResult<List<PileResultDto>> batchPileRateSet(List<PileRateSetVo> pileRateSetVos);

    /**
     * 批量更新充电桩数据
     * @param pileBatchUpdateVo 充电桩升级数据
     * @return 下发状态响应数据
     */
    ResponseResult<List<PileResultDto>> batchPileUpdate(PileBatchUpdateVo pileBatchUpdateVo);

}
