package com.sunmax.common.vo.system;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel(value = "MqttClientVo", description = "mqtt客户端实体类")
public class MqttClientVo {

    /**
     * 接入协议标识
     */
    @ApiModelProperty(value = "接入协议标识", required = true)
    private String protocolCode;

    /**
     * 客户端id
     */
    @ApiModelProperty(value = "客户端id", required = true)
    private String clientId;

    /**
     * 厂商标识
     */
    @ApiModelProperty(value = "厂商标识", required = true)
    private String vendor;

    /**
     * 网关编码
     */
    @ApiModelProperty(value = "网关编码", required = true)
    private String gwSn;

    /**
     * 地址
     */
    @ApiModelProperty(value = "地址", required = true)
    private String address;

    /**
     * 用户名
     */
    @ApiModelProperty(value = "用户名", required = true)
    private String username;

    /**
     * 密码
     */
    @ApiModelProperty(value = "密码", required = true)
    private String password;

}
