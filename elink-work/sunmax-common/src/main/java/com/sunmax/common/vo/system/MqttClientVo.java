package com.sunmax.common.vo.system;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "mqtt客户端实体类")
public class MqttClientVo {

    /**
     * 接入协议标识
     */
    @Schema(description = "接入协议标识")
    private String protocolCode;

    /**
     * 客户端id
     */
    @Schema(description = "客户端id")
    private String clientId;

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
     * 地址
     */
    @Schema(description = "地址")
    private String address;

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
