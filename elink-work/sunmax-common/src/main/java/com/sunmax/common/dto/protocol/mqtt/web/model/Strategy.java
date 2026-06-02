package com.sunmax.common.dto.protocol.mqtt.web.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class Strategy {

    /**
     * 启动方式 0-立即启动 1-延时启动。
     */
    @ApiModelProperty(value = "启动方式", required = true)
    private Integer startMode;

    /**
     * 策略类型 0-满充/放空 1-定SOC 2-定金额 3-定电量
     */
    @ApiModelProperty(value = "策略类型", required = true)
    private Integer strategyType;

    /**
     * 启动时间
     */
    @ApiModelProperty(value = "启动时间", required = true)
    private Long startTime;

    /**
     * 定量策略值
     * 策略类型 1：0～100      精度 1%；
     * 策略类型 2：0～100000   精度 0.001 元
     * 策略类型 3：0～100000   精度 0.001kW·h
     */
    @ApiModelProperty(value = "定量策略值", required = true)
    private Double strategyCfg;

}
