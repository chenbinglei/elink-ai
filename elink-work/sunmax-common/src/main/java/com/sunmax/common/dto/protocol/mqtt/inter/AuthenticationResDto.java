package com.sunmax.common.dto.protocol.mqtt.inter;

import com.sunmax.common.dto.protocol.mqtt.web.model.UserAccount;
import io.swagger.annotations.ApiModelProperty;
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
    @ApiModelProperty(value = "桩编码", required = true)
    private String pilesCode;

    /**
     * 枪标识 从1开始
     */
    @ApiModelProperty(value = "枪标识", required = true)
    private Integer gunCode;

    /**
     * 账户余额类型 1-金额 2-电量
     */
    @ApiModelProperty(value = "账户余额类型", required = true)
    private Integer balanceType;

    /**
     * 失败原因 0-成功 1-帐号非法 2-账号锁定 3-密码错误 255-其他原因
     */
    @ApiModelProperty(value = "失败原因", required = true)
    private Integer failReason;

    /**
     * 余额数值 0.001元/0.001kW*h
     */
    @ApiModelProperty(value = "余额数值", required = true)
    private Integer balanceNum;

    /**
     * 用户账户
     */
    @ApiModelProperty(value = "用户账户", required = true)
    private UserAccount userAccount;

}
