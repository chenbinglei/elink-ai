package com.sunmax.crontab.service;

import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.vo.system.MqttClientVo;

public interface SystemFeignService {

    /**
     * 创建mqtt客户端数据
     * @param mqttClientVo mqtt客户端参数实体类
     * @return 状态码
     */
    ResponseResult<Void> createMqttClient(MqttClientVo mqttClientVo);

    /**
     * 删除mqtt客户端数据
     * @param clientId 客户端id
     * @return 状态码
     */
    ResponseResult<Void> deleteMqttClient(String clientId);

}
