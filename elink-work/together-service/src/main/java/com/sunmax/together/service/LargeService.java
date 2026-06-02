package com.sunmax.together.service;

import com.sunmax.common.util.ResponseResult;

public interface LargeService {

    /**
     * 大屏设置
     * @param userId 用户id
     * @param settingData 设置数据
     * @return 状态码
     */
    ResponseResult<Void> largeSetting(String userId, String settingData);

    /**
     * 查询大屏设置
     * @param userId 用户id
     * @return 设置数据
     */
    ResponseResult<String> queryLargeSetting(String userId);

}
