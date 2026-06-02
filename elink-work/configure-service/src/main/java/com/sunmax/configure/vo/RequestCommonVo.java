package com.sunmax.configure.vo;

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
@ApiModel(value = "RequestCommonVo", description = "数据请求参数")
public class RequestCommonVo {

    /**
     * url地址
     */
    @ApiModelProperty(value = "url地址")
    private String url;

    /**
     * 平台运营商ID
     */
    @ApiModelProperty(value = "平台运营商ID")
    private String platformId;

    /**
     * 平台运营商密钥
     */
    @ApiModelProperty(value = "平台运营商密钥")
    private String platformSecret;

    /**
     * 运营商ID
     */
    @ApiModelProperty(value = "运营商ID")
    private String operatorId;

    /**
     * 数据消息密钥
     */
    @ApiModelProperty(value = "数据消息密钥")
    private String dataSecret;

    /**
     * 消息密钥初始化向量
     */
    @ApiModelProperty(value = "消息密钥初始化向量")
    private String dataSecretIv;

    /**
     * 签名密钥
     */
    @ApiModelProperty(value = "签名密钥")
    private String sigSecret;

}
