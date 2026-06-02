package com.sunmax.together.service;

import com.sunmax.common.util.ResponseResult;

public interface SystemFeignService {

    /**
     * 根据删除平台id删除网关关联关系并重新下发
     * @param platformId
     * @return
     */
    ResponseResult<String> deleteAndGatewayPlatformSet(String platformId);
}
