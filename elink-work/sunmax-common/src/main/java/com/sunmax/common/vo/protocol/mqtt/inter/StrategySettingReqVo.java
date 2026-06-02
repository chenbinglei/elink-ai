package com.sunmax.common.vo.protocol.mqtt.inter;

import com.sunmax.common.dto.protocol.mqtt.web.model.Strategy;
import com.sunmax.common.dto.protocol.mqtt.web.model.UserAccount;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 4.20 CMD_StrategySettingRequest,//策略设置 20
 * 发送方向：前置服务--->平台服务
 */
@Data
public class StrategySettingReqVo {

    /**
     * 桩编码
     */
    @ApiModelProperty(value = "桩编码", required = true)
    private String pilesCode;

    /**
     * 枪标识 从1开始
     */
    @ApiModelProperty(value = "枪标识", required = true)
    private Integer gunCode;

    /**
     * 运行模式 0-充电模式 1-放电模式
     */
    @ApiModelProperty(value = "运行模式", required = true)
    private Integer runMode;

    /**
     * 用户账户
     */
    @ApiModelProperty(value = "用户账户", required = true)
    private UserAccount userAccount;

    /**
     * 策略
     */
    @ApiModelProperty(value = "策略", required = true)
    private Strategy strategy;

}
