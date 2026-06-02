package com.sunmax.configure.service;

import com.sunmax.common.dto.system.PlatformDataForwardDto;
import com.sunmax.common.util.ResponseResult;

public interface SystemFeignService {

    /**
     * 更新平台数据转发
     * @param platformDataForward 平台数据转发参数
     * @return 状态码
     */
    ResponseResult<Boolean> updateHttpSiteForward(PlatformDataForwardDto platformDataForward);
}
