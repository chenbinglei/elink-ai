package com.sunmax.protocol.dto.platform;

import com.google.common.collect.Sets;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Set;

/**
 * 平台控制协议实体类
 */
@Data
@Schema(description = "平台控制协议实体类")
public class PlatformDataDto {

    /**
     * 数据转发id
     */
    @Schema(description = "数据转发id")
    private String dataForwardId;

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
     * 多个电桩编码
     */
    @Schema(description = "多个电桩编码")
    private Set<String> pileCodes = Sets.newHashSet();

}
