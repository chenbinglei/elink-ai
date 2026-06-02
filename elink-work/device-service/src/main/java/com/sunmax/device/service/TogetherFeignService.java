package com.sunmax.device.service;

import com.sunmax.common.dto.PageDto;
import com.sunmax.common.dto.device.*;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.vo.device.DeviceAlarmEventQueryVo;
import com.sunmax.common.vo.together.PileGunChangeVo;

import java.util.List;
import java.util.Map;

public interface TogetherFeignService {

    /**
     * 编辑电桩扩展数据
     * @param id 设备id
     * @param userId 用户id
     * @param readwriteObject 读写对象数据
     * @return 状态码
     */
    ResponseResult<Void> updatePileRea(String id, String userId, String readwriteObject);

    /**
     * 编辑电枪数据
     * @param pileGunChangeVo 电枪数据
     * @return 状态码
     */
    ResponseResult<Void> updatePileGun(PileGunChangeVo pileGunChangeVo);

    /**
     * 根据站点id修改站点状态
     * @param siteId 所属站点id
     * @param siteStatus 站点状态 1-正常投运 2-关闭下线 3-维护中 4-建设中
     * @return
     */
    ResponseResult<String> updateSiteStateById(String siteId, Integer siteStatus);

    /**
     * 根据多个站点id查询关联方信息数据
     * @param siteIdList 多个站点id
     * @return
     */
    ResponseResult<Map<String, List<AffiliatesInfoDto>>> findSiteAffiliatesInfoByIds(List<String> siteIdList);

    /**
     * 校验站点密码是否正确
     *
     * @param siteId   站点id
     * @param password 站点密码
     * @return 0-验证失败 1-验证通过
     */
    ResponseResult<Integer> checkSitePassword(String siteId, String password);

    /**
     * 修改设备运营状态
     * @param deviceId 设备唯一id
     * @param operateStatus 设备运营状态 1-投运 2-检修 3-退役
     * @return
     */
    ResponseResult<String> updateDeviceOperateStatus(String deviceId, Integer operateStatus);

    /**
     * 查询设备通信状态
     * @param deviceBasicInfoDtos
     * @return
     */
    ResponseResult<Map<String, Integer>> findDeviceTxStatus(List<DeviceBasicInfoDto> deviceBasicInfoDtos);

    /**
     * 根据多个站点id查询站点关联计量设备id
     *
     * @param siteIdList 多个站点id
     * @param nodeType   节点类型 0-计量节点 1-设备节点
     * @param deviceType 设备类型 1-光伏 2-储能 3-电桩 4-其他
     * @return 站点关联计量设备id
     */
    ResponseResult<Map<String, List<String>>> findSiteMeasureIdBySiteIds(List<String> siteIdList, Integer nodeType, Integer deviceType);

    /**
     * 根据子站id查询站点id
     * @param subId 子站id
     * @return 站点id
     */
    ResponseResult<String> findSiteIdBySubId(String subId);

    /**
     * 根据查询条件查询设备告警事件列表
     * @param eventQueryVo 查询条件
     * @return 设备告警事件列表
     */
    ResponseResult<PageDto<DeviceAlarmEventListDto>> findAllDeviceEventList(DeviceAlarmEventQueryVo eventQueryVo);

    /**
     * 修改设备事件忽略状态
     * @param id 设备事件id
     * @param ignoreStatus 忽略状态 0-不忽略 1-忽略
     * @return 状态码
     */
    ResponseResult<Void> updateEventIgnoreStatus(String id, Integer ignoreStatus);

    /**
     * 根据站点id查询拓扑节点实时数据
     * @param siteId 站点id
     * @return 站点拓扑节点数据列表
     */
    ResponseResult<List<SiteTopDataDto>> findSiteTopDataListBySiteId(String siteId);

    /**
     * 根据站点id查询拓扑节点静态数据
     * @param siteId 站点id
     * @param nodeType 节点类型 1-拓扑点 2-电网 3-变压器 4-关口点 5-计量点 6-逆变器 7-光伏组件 8-储能柜 9-负荷 10-充电桩 11-开关 12-车辆
     * @return 拓扑节点静态数据列表
     */
    ResponseResult<List<SiteTopNodeDto>> findSiteTopNodeBySiteId(String siteId, Integer nodeType);

}
