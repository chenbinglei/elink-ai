package com.sunmax.common.dto.system.dynamic;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "MqttForwardDto", description = "mqtt数据转发类")
public class MqttForwardDto {

    /**
     * 客户端id
     */
    @ApiModelProperty(value = "客户端id")
    public String clientId;

    /**
     * 厂商标识
     */
    @ApiModelProperty(value = "厂商标识")
    private String vendor;

    /**
     * 网关编码
     */
    @ApiModelProperty(value = "网关编码")
    private String gwSn;

    /**
     * 用户名
     */
    @ApiModelProperty(value = "用户名")
    private String username;

    /**
     * 密码
     */
    @ApiModelProperty(value = "密码")
    private String password;

}
