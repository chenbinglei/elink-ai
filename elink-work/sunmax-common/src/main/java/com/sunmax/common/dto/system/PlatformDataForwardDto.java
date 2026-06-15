package com.sunmax.common.dto.system;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.apache.commons.compress.utils.Lists;

import java.util.List;

@Data
@Schema(description = "平台数据转发返回实体类")
public class PlatformDataForwardDto {

    /**
     * 主键id
     */
    @Schema(description = "主键id")
    private String id;

    /**
     * 接入协议标识
     */
    @Schema(description = "接入协议标识")
    private String protocolCode;

    /**
     * 地址
     */
    @Schema(description = "地址")
    private String address;

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

    /**
     * 状态 1-启用 2-断开
     */
    @Schema(description = "状态 1-启用 2-断开")
    private Integer status;

    /**
     * 站点运营商配置
     */
    @Schema(description = "站点运营商配置")
    private List<OperatorInfoDto> operatorInfoList = Lists.newArrayList();

    /**
     * 站点关联运营配置
     */
    @Schema(description = "站点关联运营配置")
    private List<SiteOperateDto> siteOperateList = Lists.newArrayList();

}
