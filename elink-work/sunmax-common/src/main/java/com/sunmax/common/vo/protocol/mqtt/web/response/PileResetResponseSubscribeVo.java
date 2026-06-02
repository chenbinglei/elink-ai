package com.sunmax.common.vo.protocol.mqtt.web.response;

import lombok.Data;

/**
 * 电桩复位响应订阅实体类
 */
@Data
public class PileResetResponseSubscribeVo {

    /**
     * 电桩编号
     */
    private String pilesCode;

    /**
     * 固件类型
     * 1-V2G_1.0 TCP控制板
     * 2-V2G_2.0 TCP控制板
     * 3-V2G_3.0 TCP控制板
     * 4-V2G_4.0 TPU控制板
     * 5-V2G_4.0 CCU控制板
     * 6-V2G_6.0 TCP控制板
     * 7-V2G_7.0 TPU控制板
     * 8-V2G_7.0 CCU控制板
     * 9-V2G_9.0 TCP_BOOT控制板
     * 10-V2G_10.0 TPU_BOOT控制板
     * 11-V2G_11.0 CCU_BOOT控制板
     */
    private Integer type;

    /**
     * 复位命令响应参数 0-复位成功 1-当前状态不允许复位 255-其他原因
     */
    private Integer result;

}
