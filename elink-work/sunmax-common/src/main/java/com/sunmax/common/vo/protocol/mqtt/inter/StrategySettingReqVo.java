package com.sunmax.common.vo.protocol.mqtt.inter;

import com.sunmax.common.dto.protocol.mqtt.web.model.Strategy;
import com.sunmax.common.dto.protocol.mqtt.web.model.UserAccount;
import io.swagger.v3.oas.annotations.media.Schema;
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
    @Schema(description = "桩编码")
    private String pilesCode;

    /**
     * 枪标识 从1开始
     */
    @Schema(description = "枪标识")
    private Integer gunCode;

    /**
     * 运行模式 0-充电模式 1-放电模式
     */
    @Schema(description = "运行模式")
    private Integer runMode;

    /**
     * 用户账户
     */
    @Schema(description = "用户账户")
    private UserAccount userAccount;

    /**
     * 策略
     */
    @Schema(description = "策略")
    private Strategy strategy;

}
