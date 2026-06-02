package com.sunmax.protocol.service;

import com.sunmax.common.util.ResponseResult;
import com.sunmax.protocol.vo.PlatformRequestVo;

public interface PlatformPileCtrlService {

    /**
     * 根据充电桩编码查询计费数据
     * @param requestVo 充电桩编码数据加密数据
     * @return  电桩计费数据
     */
    ResponseResult<String> getPileRateTemplate(PlatformRequestVo requestVo);

    /**
     * 启动充电桩
     * @param requestVo 电桩启动数据加密数据
     * @return 电桩启动结果
     */
    ResponseResult<String> pileStart(PlatformRequestVo requestVo);

    /**
     * 停止充电桩
     * @param requestVo 电桩停止数据加密数据
     * @return 电桩停止结果
     */
    ResponseResult<String> pileStop(PlatformRequestVo requestVo);

    /**
     * 功率控制
     * @param requestVo 功率控制参数加密数据
     * @return 电桩功率控制结果
     */
    ResponseResult<String> powerCtrl(PlatformRequestVo requestVo);

}
