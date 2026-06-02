package com.sunmax.crontab.service;

import com.sunmax.common.util.ResponseResult;

public interface AddRecordTaskService {

    /**
     * 根据节点id和时间补录taos数据
     * @param nodeId
     * @param startTime
     * @param endTime
     * @param addRecordId
     * @return
     */
    ResponseResult<String> addRecordNodeDataById(String nodeId, String startTime, String endTime, String addRecordId);
}
