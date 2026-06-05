package com.sunmax.crontab.config.mqtt;

import com.alibaba.fastjson2.JSON;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.util.SpringBeanUtil;
import com.sunmax.common.util.StringUtil;
import com.sunmax.common.util.oss.FileUtil;
import com.sunmax.common.vo.crontab.StrategyTaskVo;
import com.sunmax.common.vo.system.MqttClientVo;
import com.sunmax.crontab.dto.HdPowerCtrlDto;
import com.sunmax.crontab.entity.MqttClientLogEntity;
import com.sunmax.crontab.service.HdDataService;
import com.sunmax.crontab.util.MiUtil;
import com.sunmax.crontab.util.ScheduleTaskUtil;
import com.sunmax.crontab.util.StrategyUtil;
import com.sunmax.crontab.vo.mqtt.HDReplyVo;
import com.sunmax.crontab.vo.mqtt.HDSetReplyVo;
import com.sunmax.crontab.vo.mqtt.HDSetVo;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MqttClientDataHandler {

    private static HdDataService hdDataService;

    private static ScheduleTaskUtil scheduleTask;

    //指令接收响应处理
    public static void hdSetReply(MqttClientVo mqttClientVo, HDSetVo hdTopicVo) {
        //指令接收响应
        String setReplyTopic = FileUtil.SLASH + mqttClientVo.getVendor() + FileUtil.SLASH + mqttClientVo.getGwSn() + "/service/setReply";
        HDSetReplyVo hdSetReplyVo = new HDSetReplyVo();
        hdSetReplyVo.setSn(hdTopicVo.getSn());
        hdSetReplyVo.setMi(String.valueOf(MiUtil.getUniqueTimestamp()));
        hdSetReplyVo.setGatewayReceivedTime(System.currentTimeMillis());
        try {
            if (MqttClientManager.clients.containsKey(mqttClientVo.getClientId())) {
                MqttClientLogEntity mqttClientLog = MqttClientLogEntity.builder().clientId(mqttClientVo.getClientId()).topic(setReplyTopic)
                        .message(JSON.toJSONString(hdSetReplyVo)).type(1).build();
                MqttClientManager.publish(mqttClientVo.getClientId(), setReplyTopic, hdSetReplyVo, mqttClientLog);
            } else {
                log.error("未找到对应的mqtt客户端, clientId: " + mqttClientVo.getClientId());
            }
        } catch (Exception e) {
            log.error("华电协议指令接收响应处理mqtt消息发送失败, 错误信息: ", e);
        }
    }

    //指令返回响应处理
    public static void hdReply(MqttClientVo mqttClientVo, HDSetVo hdTopicVo) {
        if (hdDataService == null) {
            hdDataService = SpringBeanUtil.getBean(HdDataService.class);
        }
        if (scheduleTask == null) {
            scheduleTask = SpringBeanUtil.getBean(ScheduleTaskUtil.class);
        }
        //指令返回响应
        String replyTopic = FileUtil.SLASH + mqttClientVo.getVendor() + FileUtil.SLASH + mqttClientVo.getGwSn() + "/service/reply";
        HDReplyVo hdReplyVo = new HDReplyVo();
        hdReplyVo.setSn(hdTopicVo.getSn());
        hdReplyVo.setMi(String.valueOf(MiUtil.getUniqueTimestamp()));
        hdReplyVo.setSuccessBit(0);
        //响应成功则调用设备控制接口
        ResponseResult<HdPowerCtrlDto> responseResult = hdDataService.hdPowerCtrl(mqttClientVo.getClientId(), hdTopicVo);
        if (responseResult.isSuccess()) {
            hdReplyVo.setSuccessBit(1);
            hdReplyVo.setDeviceExecutedTime(System.currentTimeMillis());
            hdReplyVo.setExcept(HDReplyVo.Except.builder().msg("调控成功").build());
        } else {
            hdReplyVo.setExcept(HDReplyVo.Except.builder().msg(responseResult.getMessage()).build());
        }
        try {
            if (MqttClientManager.clients.containsKey(mqttClientVo.getClientId())) {
                String params = null;
                String result = null;
                if (responseResult.isSuccess() && responseResult.getData() != null) {
                    HdPowerCtrlDto hdPowerCtrl = responseResult.getData();
                    params = hdPowerCtrl.getParams();
                    result = hdPowerCtrl.getResult();
                }
                MqttClientLogEntity mqttClientLog = MqttClientLogEntity.builder().clientId(mqttClientVo.getClientId()).topic(replyTopic)
                        .message(JSON.toJSONString(hdReplyVo)).type(1).params(params).result(result).build();
                MqttClientManager.publish(mqttClientVo.getClientId(), replyTopic, hdReplyVo, mqttClientLog);
            } else {
                log.error("未找到对应的mqtt客户端, clientId: " + mqttClientVo.getClientId());
            }
        } catch (Exception e) {
            log.error("华电协议指令返回响应处理mqtt消息发送失败, 错误信息: ", e);
        }
        //修改定时任务
        if (responseResult.isSuccess()) {
            HdPowerCtrlDto hdPowerCtrl = responseResult.getData();
            Integer sourceType = hdPowerCtrl.getSourceType();
            StrategyTaskVo strategyTask = hdPowerCtrl.getStrategyTask();
            //修改策略任务
            if (strategyTask.getEnabled() && StringUtil.isNotEmpty(strategyTask.getPeriod())) {
                scheduleTask.updateTask(() -> StrategyUtil.platformAutoControlTask(strategyTask, sourceType), strategyTask.getPeriod() * 1000,
                        ScheduleTaskUtil.STRATEGY_TASK + strategyTask.getId());
            }
        }
    }

}
