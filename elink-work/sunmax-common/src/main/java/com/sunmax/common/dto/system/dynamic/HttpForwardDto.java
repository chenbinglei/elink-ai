package com.sunmax.common.dto.system.dynamic;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "HttpForwardDto", description = "http数据转发类")
public class HttpForwardDto {

    /**
     * 运营商ID
     */
    @ApiModelProperty(value = "平台运营商ID")
    private String platformId;

    /**
     * 平台运营商密钥
     */
    @ApiModelProperty(value = "平台运营商密钥")
    private String platformSecret;

    /**
     * 数据消息密钥
     */
    @ApiModelProperty(value = "数据消息密钥")
    private String dataSecret;

    /**
     * 数据消息密钥初始化向量
     */
    @ApiModelProperty(value = "数据消息密钥初始化向量")
    private String dataSecretIv;

    /**
     * 签名密钥
     */
    @ApiModelProperty(value = "签名密钥")
    private String sigSecret;

}
