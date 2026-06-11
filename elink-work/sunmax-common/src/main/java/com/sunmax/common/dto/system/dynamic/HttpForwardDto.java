package com.sunmax.common.dto.system.dynamic;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "http数据转发类")
public class HttpForwardDto {

    /**
     * 运营商ID
     */
    @Schema(description = "平台运营商ID")
    private String platformId;

    /**
     * 平台运营商密钥
     */
    @Schema(description = "平台运营商密钥")
    private String platformSecret;

    /**
     * 数据消息密钥
     */
    @Schema(description = "数据消息密钥")
    private String dataSecret;

    /**
     * 数据消息密钥初始化向量
     */
    @Schema(description = "数据消息密钥初始化向量")
    private String dataSecretIv;

    /**
     * 签名密钥
     */
    @Schema(description = "签名密钥")
    private String sigSecret;

}
