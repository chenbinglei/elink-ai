package com.sunmax.crontab.vo.mqtt;

import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel(value = "华电指令接收响应返回实体类")
public class HDSetReplyVo {

    /**
     * 下挂设备的唯一标识，一般为序列号。例如负荷控制终端下挂断路器那么填写断路器的序列号，如果终端直接下挂智能设备（例如可接收modbus指令的空调），那么填写智能设备的唯一编码。
     */
    private String sn;

    /**
     * 消息id，用来唯一确认一条消息,字符串类型
     */
    private String mi;

    /**
     * 采集数据的13位时间戳，代表网关接收到指令的时间
     */
    private Long gatewayReceivedTime;

}
