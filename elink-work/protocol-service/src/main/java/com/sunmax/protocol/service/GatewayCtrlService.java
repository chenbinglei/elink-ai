package com.sunmax.protocol.service;


import com.sunmax.common.dto.protocol.GateWayControlDto;
import com.sunmax.common.dto.protocol.GateWayPolicyDto;
import com.sunmax.common.dto.protocol.PlatformStatusDto;
import com.sunmax.common.dto.protocol.PlatformSetDto;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.vo.protocol.GateWayControlVo;
import com.sunmax.common.vo.protocol.GateWayPolicyVo;
import com.sunmax.common.vo.protocol.PlatformSetVo;
import com.sunmax.common.vo.protocol.PlatformStatusVo;
import com.sunmax.common.vo.protocol.mqtt.web.from.*;
import com.sunmax.common.vo.protocol.mqtt.web.platform.LicensePublishVo;
import com.sunmax.common.vo.protocol.mqtt.web.platform.RebootPublicVo;

import java.util.List;
import java.util.Map;

/**
 * 网关控制服务
 */
public interface GatewayCtrlService {

    /**
     * 网关参数控制
     * @param gateWayControlVo 网关控制参数
     * @return 插入是否成功
     */
    ResponseResult<GateWayControlDto> loadParamSet(GateWayControlVo gateWayControlVo);

    /**
     * 单次下发网关策略配置参数
     * @param policyIssuedVo 网关策略下发参数
     * @return 网关策略响应
     */
    ResponseResult<GateWayPolicyDto> issuedPolicyParam(GateWayPolicyVo policyIssuedVo);

    /**
     * 批量下发网关策略配置参数
     * @param policyIssuedVos 多个网关策略下发参数
     * @return 网关策略响应
     */
    ResponseResult<List<GateWayPolicyDto>> batchIssuedPolicyParam(List<GateWayPolicyVo> policyIssuedVos);

    /**
     * 查询网关状态数据
     * @param deviceCode 设备编号
     * @return 网关状态数据
     */
    ResponseResult<GatewayStatusSubscribeVo> gatewayStatus(String deviceCode);

    /**
     * 批量查询网关状态数据
     * @param deviceCodes 多个设备编号
     * @return 网关状态数据列表
     */
    ResponseResult<List<GatewayStatusSubscribeVo>> batchGatewayStatus(List<String> deviceCodes);

    /**
     * 网关服务列表查询
     * @param deviceCode 设备编号
     * @return 网关状态数据
     */
    ResponseResult<List<ServiceListSubscribeVo>> serviceList(String deviceCode);

    /**
     * 网关同步时钟
     * @param deviceCode 设备编号
     * @param type 类型 1-查询时钟 2-设置时钟
     * @return 网关同步时钟响应数据
     */
    ResponseResult<SyncClockSubscribeVo> syncClock(String deviceCode, Integer type);

    /**
     * 网关重启
     * @param deviceCode 设备编号
     * @param rebootPublicVo 重启参数
     * @return 网关重启响应数据
     */
    ResponseResult<RebootSubscribeVo> reboot(String deviceCode, RebootPublicVo rebootPublicVo);

    /**
     * 网关许可状态查询
     * @param deviceCode 设备编号
     * @return 许可响应数据
     */
    ResponseResult<LicenseSubscribeVo> licenseStatus(String deviceCode);

    /**
     * 获取设备key
     * @param deviceCode 设备编号
     * @return 许可响应数据
     */
    ResponseResult<LicenseSubscribeVo> licenseKey(String deviceCode);

    /**
     * 下发许可
     * @param deviceCode 设备网关编号
     * @param licensePublicVo 下发许可参数
     * @return 下发许可响应数据
     */
    ResponseResult<LicenseSubscribeVo> license(String deviceCode, LicensePublishVo licensePublicVo);

    /**
     * 网关平台设置
     * @param deviceCode 设备网关编号
     * @param platformSetVos 平台设置参数
     * @return 网关平台设置响应数据
     */
    ResponseResult<PlatformSetDto> platformSet(String deviceCode, List<PlatformSetVo> platformSetVos);

    /**
     * 批量设置平台参数
     * @param platformSetVoMap 批量设置平台参数
     * @return 网关平台设置响应数据
     */
    ResponseResult<Map<String, PlatformSetDto>> batchPlatformSet(Map<String, List<PlatformSetVo>> platformSetVoMap);

    /**
     * 查询平台设备状态
     * @param deviceCode 设备编号
     * @param platformStatusVos 平台状态返回实体类
     * @return 平台设备状态
     */
    ResponseResult<PlatformStatusDto> platformStatus(String deviceCode, List<PlatformStatusVo> platformStatusVos);

}
