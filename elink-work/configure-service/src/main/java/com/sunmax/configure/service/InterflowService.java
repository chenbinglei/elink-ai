package com.sunmax.configure.service;

import com.sunmax.common.dto.configure.GunStatusInfoDto;
import com.sunmax.common.dto.configure.PowerControlResDto;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.vo.crontab.PowerControlParamVo;
import com.sunmax.configure.dto.ResponseDto;

import java.util.List;
import java.util.Map;

public interface InterflowService {
    /**
     * 查询tonken
     * @return
     */
    ResponseResult<String> queryToken(String platformId);

    /**
     * 查询充电桩信息
     */
    void queryStationsInfo(String platformId);

    /**
     * 查询互联互通接口数据
     * @param platformId 平台id
     * @param methodName 方法名
     * @param paramData 路径参数
     * @return 数据
     */
    ResponseDto queryInterflowData(String platformId, String methodName, String paramData);

    /**
     * 存储充电状态
     * @param operatorId 运营商id
     * @param data 数据
     * @return
     */
    Map<String, Object> notificationEquipChargeStatus(String operatorId, String data);

    /**
     * 接收推送充电订单信息
     * @param operatorId 运营商id
     * @param data 数据
     * @return
     */
    Map<String, Object> notificationChargeOrderInfo(String operatorId, String data);

    /**
     * 接收推送设备状态变化信息
     * @param operatorId 运营商id
     * @param data 数据
     * @return
     */
    Map<String, Object> notificationStationStatus(String operatorId, String data);

    /**
     * 城市充电功率控制
     * @param pileCode 电桩编码
     * @param gunCode  电枪编码
     * @param outPower 输出功率
     * @return
     */
    ResponseResult<String> interflowPowerControl(String pileCode, Integer gunCode, Double outPower);

    /**
     * 查询站点下设备接口状态
     * @param platformId 平台id
     * @param stationIds 多个站点id
     * @return 站点id -> 多个站点下设备接口状态
     */
    ResponseResult<Map<String, List<GunStatusInfoDto>>> queryStationStatus(String platformId, List<String> stationIds);

    /**
     * 城市充电批量功率控制
     * @param powerControlParamVos
     * @return
     */
    ResponseResult<List<PowerControlResDto>> interflowBatchPowerControl(List<PowerControlParamVo> powerControlParamVos);
}
