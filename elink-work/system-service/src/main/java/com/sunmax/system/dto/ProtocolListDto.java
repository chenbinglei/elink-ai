package com.sunmax.system.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "ProtocolListDto", description = "协议列表返回实体类")
public class ProtocolListDto {

    /**
     * 协议标识
     */
    @ApiModelProperty(value = "协议标识")
    private String code;

    /**
     * 协议名称
     */
    @ApiModelProperty(value = "协议名称")
    private String name;

    /**
     * 协议类型 1-Mqtt 2-Http
     */
    @ApiModelProperty(value = "协议类型 1-Mqtt 2-Http")
    private Integer type;

}
