package com.sunmax.protocol.service;

import com.sunmax.common.util.ResponseResult;
import com.sunmax.protocol.vo.PlatformRequestVo;

public interface VirtualPileCtrlService {

    /**
     * 获取电桩实时数据列表
     * @param requestVo 电桩实时数据入参
     * @return 加密后的电桩实时数据列表
     */
    ResponseResult<String> getPileRealList(PlatformRequestVo requestVo);

    /**
     * 获取电桩电量数据列表
     * @param requestVo 电桩电量数据入参
     * @return 加密后的电桩电量数据列表
     */
    ResponseResult<String> getPileQtList(PlatformRequestVo requestVo);

}
