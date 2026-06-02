package com.sunmax.together.service.operation;

import com.sunmax.common.dto.PageDto;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.vo.protocol.PileSetQrVo;
import com.sunmax.together.dto.operation.deviceManage.PileGunListDto;
import com.sunmax.together.vo.operation.deviceManage.AlarmListQueryVo;
import com.sunmax.together.vo.operation.deviceManage.PileListQueryVo;

public interface DeviceManageService {

    /**
     * 分页查询电桩列表信息
     * @param pileListQueryVo 查询参数
     * @return 电桩列表数据
     */
    ResponseResult<PageDto<PileGunListDto>> findPileListByPage(PileListQueryVo pileListQueryVo);

    /**
     * 修改设备运营状态
     * @param deviceId 设备唯一id
     * @param operateStatus 设备运营状态 1-投运 2-检修 3-退役
     * @return
     */
    ResponseResult<String> updateDeviceOperateStatus(String deviceId, Integer operateStatus);

    /**
     * 分页查询告警列表数据
     * @param alarmListQueryVo 查询参数
     * @return 告警列表数据
     */
    ResponseResult<?> findAlarmListByPage(AlarmListQueryVo alarmListQueryVo);

    /**
     * 设置二维码
     * @param pileSetQrVo 二维码地址参数
     * @return 状态码
     */
    ResponseResult<Void> pileSetQr(PileSetQrVo pileSetQrVo);
}
