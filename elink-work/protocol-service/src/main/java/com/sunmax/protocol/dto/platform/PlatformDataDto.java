package com.sunmax.protocol.dto.platform;

import com.google.common.collect.Sets;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Set;

/**
 * 平台控制协议实体类
 */
@Data
@ApiModel(value = "PlatformDataDto", description = "平台控制协议实体类")
public class PlatformDataDto {

    /**
     * 数据转发id
     */
    @ApiModelProperty(value = "数据转发id")
    private String dataForwardId;

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
     * 多个电桩编码
     */
    @ApiModelProperty(value = "多个电桩编码")
    private Set<String> pileCodes = Sets.newHashSet();

}
