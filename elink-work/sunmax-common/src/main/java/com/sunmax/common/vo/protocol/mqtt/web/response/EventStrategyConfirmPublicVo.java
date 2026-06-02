package com.sunmax.common.vo.protocol.mqtt.web.response;

import lombok.Data;

/**
 * 策略设置响应 发布实体类
 */
@Data
public class EventStrategyConfirmPublicVo {

    /**
     * 电桩编号
     */
    private String pilesCode;

    /**
     * 枪编号
     */
    private Integer gunCode;

    /**
     * 充/放电接口运行模式 0-充电模式 1-放电模式
     */
    private Integer runMode;

    /**
     * 保留
     */
    private String reserve;

    /**
     *失败原因
     */
    private Integer failReason;

    /**
     *启动失败详细原因
     */
    private Integer failDetail;

}
