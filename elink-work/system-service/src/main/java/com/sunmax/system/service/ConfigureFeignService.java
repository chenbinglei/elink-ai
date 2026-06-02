package com.sunmax.system.service;

import com.sunmax.common.dto.system.PlatformDataForwardDto;
import com.sunmax.common.dto.system.StorageDataForwardDto;
import com.sunmax.common.util.ResponseResult;

import java.util.List;
import java.util.Set;

public interface ConfigureFeignService {

    /**
     * 根据平台id和协议标识查询平台数据转发
     * @param platformId 平台id
     * @param protocolCode 协议标识
     * @return 平台数据转发
     */
    ResponseResult<PlatformDataForwardDto> getPlatformDataForward(String platformId, String protocolCode);

    /**
     * 根据多个协议标识查询平台数据转发
     * @param protocolCodes 多个协议标识
     * @return 平台数据转发
     */
    ResponseResult<List<PlatformDataForwardDto>> getPlatformDataForwardList(Set<String> protocolCodes);

    /**
     * 根据多个协议标识查询储能平台数据转发
     * @param protocolCodes 多个协议标识
     * @return 储能平台数据转发
     */
    ResponseResult<List<StorageDataForwardDto>> getStorageDataForwardList(Set<String> protocolCodes);

}
