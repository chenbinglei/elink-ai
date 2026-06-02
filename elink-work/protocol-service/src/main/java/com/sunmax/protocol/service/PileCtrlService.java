package com.sunmax.protocol.service;

import com.sunmax.common.dto.protocol.PileLogResultDto;
import com.sunmax.common.dto.protocol.PileResultDto;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.vo.protocol.*;
import com.sunmax.common.vo.protocol.mqtt.inter.VehicleInfoResVo;

import java.util.List;
import java.util.Set;

/**
 * 充电桩控制服务
 */
public interface PileCtrlService {

    /**
     * 电桩启动命令
     *
     * @param pileStartVo 电桩启动参数
     * @return 插入是否成功
     */
    ResponseResult<PileResultDto> pileStart(PileStartVo pileStartVo);

    /**
     * 电桩启动命令
     *
     * @param pileStopVo 电桩停止参数
     * @return 插入是否成功
     */
    ResponseResult<PileResultDto> pileStop(PileStopVo pileStopVo);

    /**
     * 电桩功率控制
     *
     * @param pilePowerCtrlVo 电桩功率控制参数
     * @return 插入是否成功
     */
    ResponseResult<PileResultDto> pilePowerCtrl(PilePowerCtrlVo pilePowerCtrlVo);

    /**
     * 电桩费率下发
     *
     * @param pileRateSetVo 费率参数
     * @return 插入是否成功
     */
    ResponseResult<PileResultDto> pileRateSet(PileRateSetVo pileRateSetVo);

    /**
     * 电桩升级
     *
     * @param pileUpdateVo 升级参数
     * @return 升级命令下发
     */
    ResponseResult<PileResultDto> pileUpdate(PileUpdateVo pileUpdateVo);

    /**
     * 电桩升级
     * @param terminalCode 终端编码
     * @param pileCodes 多个充电桩编码
     * @param pileUpdateVo 升级参数
     * @return 升级命令下发
     */
    ResponseResult<List<PileResultDto>> pileUpdateByIeg(String terminalCode, Set<String> pileCodes, PileUpdateVo pileUpdateVo);

    /**
     * 控制板信息请求
     *
     * @param pileCode 电桩编码
     * @return 是否成功
     */
    ResponseResult<Void> pileSubModelInfoReq(String pileCode);

    /**
     * 车辆信息请求
     *
     * @param pileCode 电桩编码
     * @param gunCode  枪编码
     * @return 车辆信息响应相关数据
     */
    ResponseResult<VehicleInfoResVo> vehicleInfoRequest(String pileCode, String gunCode);

    /**
     * 电桩日志查询
     *
     * @param pileLogQueryVo 电桩日志查询参数
     * @return 查询结果
     */
    ResponseResult<List<PileLogResultDto>> queryPileLogList( PileLogQueryVo pileLogQueryVo);

    /**
     * 电桩复位
     *
     * @param pileResetVo 电桩复位参数
     * @return 复位状态码
     */
    ResponseResult<Void> pileReset(PileResetVo pileResetVo);

    /**
     * 电桩二维码设置
     *
     * @param pileSetQrVo 电桩二维码设置参数
     * @return 状态码
     */
    ResponseResult<Void> pileSetQr(PileSetQrVo pileSetQrVo);

}
