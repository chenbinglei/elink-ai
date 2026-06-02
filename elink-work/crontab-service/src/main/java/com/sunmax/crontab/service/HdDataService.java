package com.sunmax.crontab.service;

import com.sunmax.common.util.ResponseResult;
import com.sunmax.crontab.dto.HdPowerCtrlDto;
import com.sunmax.crontab.vo.mqtt.HDSetVo;

public interface HdDataService {

    /**
     * 推送站点信息
     */
    void stationTask();

    /**
     * 修改华电功率控制策略任务
     *
     * @param clientId 客户端id
     * @param hdSetVo 华电功率控制策略任务参数
     * @return 状态码
     */
    ResponseResult<HdPowerCtrlDto> hdPowerCtrl(String clientId, HDSetVo hdSetVo);

}
