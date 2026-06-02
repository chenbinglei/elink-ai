package com.sunmax.protocol.service.impl;

import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.vo.protocol.PilePowerCtrlVo;
import com.sunmax.protocol.service.CrontabFeignService;
import com.sunmax.protocol.util.MqttTopicUtil;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CrontabFeignServiceImpl implements CrontabFeignService {

    @Override
    public ResponseResult<Void> batchPilePowerCtrl(List<PilePowerCtrlVo> pilePowerCtrlVos) {
        pilePowerCtrlVos.forEach(MqttTopicUtil::sendPowerCtrlTopic);
        return ResponseResult.ok();
    }

}
