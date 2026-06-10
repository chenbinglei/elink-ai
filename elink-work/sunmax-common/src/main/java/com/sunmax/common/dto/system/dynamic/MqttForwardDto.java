package com.sunmax.common.dto.system.dynamic;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "mqtt数据转发类")
public class MqttForwardDto {

    /**
     * 客户端id
     */
    @Schema(description = "客户端id")
    public String clientId;

    /**
     * 厂商标识
     */
    @Schema(description = "厂商标识")
    private String vendor;

    /**
     * 网关编码
     */
    @Schema(description = "网关编码")
    private String gwSn;

    /**
     * 用户名
     */
    @Schema(description = "用户名")
    private String username;

    /**
     * 密码
     */
    @Schema(description = "密码")
    private String password;

}
