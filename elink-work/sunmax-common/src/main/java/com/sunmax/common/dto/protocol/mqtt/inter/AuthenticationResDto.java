package com.sunmax.common.dto.protocol.mqtt.inter;

import com.sunmax.common.dto.protocol.mqtt.web.model.UserAccount;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 4.19 CMD_AuthenticationResponse,//鉴权响应 19
 * 发送方向：前置服务<---平台服务
 */
@Data
public class AuthenticationResDto {

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
     * 账户余额类型 1-金额 2-电量
     */
    @Schema(description = "账户余额类型")
    private Integer balanceType;

    /**
     * 失败原因 0-成功 1-帐号非法 2-账号锁定 3-密码错误 255-其他原因
     */
    @Schema(description = "失败原因")
    private Integer failReason;

    /**
     * 余额数值 0.001元/0.001kW*h
     */
    @Schema(description = "余额数值")
    private Integer balanceNum;

    /**
     * 用户账户
     */
    @Schema(description = "用户账户")
    private UserAccount userAccount;

}
