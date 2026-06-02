package com.sunmax.device.service;

import com.sunmax.common.util.ResponseResult;
import com.sunmax.device.dto.webserver.SiteCountDto;
import com.sunmax.device.dto.webserver.SiteLedgerDto;
import com.sunmax.device.dto.webserver.DeviceServerDto;
import com.sunmax.device.vo.webserver.CountQueryVo;

import java.util.List;

public interface WebServerService {

    /**
     * 根据设备id和类型查询设备信息列表
     * @param ids 多个id 例如"device1,device2"
     * @param type 类型 1-站点数据 2-设备数据
     * @return 设备信息列表数据
     */
    ResponseResult<List<DeviceServerDto>> findDeviceListByIdsAndType(String ids, Integer type);

    /**
     * 根据租户id查询站点设备台账数据
     * @param tenantId 租户id
     * @return 设备台账数据
     */
    ResponseResult<List<SiteLedgerDto>> findLedgerListByTenetId(String tenantId);

    /**
     * 根据查询条件查询站点设备统计数据
     * @param countQueryVo 站点统计查询条件
     * @return 站点统计数据
     */
    ResponseResult<List<SiteCountDto>> findCountListByCondition(CountQueryVo countQueryVo);

}
