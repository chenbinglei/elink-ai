package com.sunmax.common.vo.protocol.mqtt.inter;

import com.sunmax.common.dto.protocol.mqtt.web.model.UserAccount;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 4.18 CMD_AuthenticationRequest,//鉴权请求 18
 * 发送方向：前置服务--->平台服务
 */
@Data
public class AuthenticationReqVo {

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
     * 用户账号
     */
    @Schema(description = "用户账号")
    private UserAccount userAccount;

    /**
     * 鉴权密码
     */
    @Schema(description = "鉴权密码")
    private Integer authPwd;


}
