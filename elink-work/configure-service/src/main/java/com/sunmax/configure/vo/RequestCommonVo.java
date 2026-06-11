package com.sunmax.configure.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "数据请求参数")
public class RequestCommonVo {

    /**
     * url地址
     */
    @Schema(description = "url地址")
    private String url;

    /**
     * 平台运营商ID
     */
    @Schema(description = "平台运营商ID")
    private String platformId;

    /**
     * 平台运营商密钥
     */
    @Schema(description = "平台运营商密钥")
    private String platformSecret;

    /**
     * 运营商ID
     */
    @Schema(description = "运营商ID")
    private String operatorId;

    /**
     * 数据消息密钥
     */
    @Schema(description = "数据消息密钥")
    private String dataSecret;

    /**
     * 消息密钥初始化向量
     */
    @Schema(description = "消息密钥初始化向量")
    private String dataSecretIv;

    /**
     * 签名密钥
     */
    @Schema(description = "签名密钥")
    private String sigSecret;

}
