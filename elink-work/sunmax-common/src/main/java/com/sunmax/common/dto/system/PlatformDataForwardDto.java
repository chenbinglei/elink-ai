package com.sunmax.common.dto.system;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.compress.utils.Lists;

import java.util.List;

@Data
@ApiModel(value = "PlatformDataForwardDto", description = "平台数据转发返回实体类")
public class PlatformDataForwardDto {

    /**
     * 主键id
     */
    @ApiModelProperty(value = "主键id")
    private String id;

    /**
     * 接入协议标识
     */
    @ApiModelProperty(value = "接入协议标识")
    private String protocolCode;

    /**
     * 地址
     */
    @ApiModelProperty(value = "地址")
    private String address;

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

    /**
     * 状态 1-启用 2-断开
     */
    @ApiModelProperty(value = "状态 1-启用 2-断开")
    private Integer status;

    /**
     * 站点运营商配置
     */
    @ApiModelProperty(value = "站点运营商配置")
    private List<OperatorInfoDto> operatorInfoList = Lists.newArrayList();

    /**
     * 站点关联运营配置
     */
    @ApiModelProperty(value = "站点关联运营配置")
    private List<SiteOperateDto> siteOperateList = Lists.newArrayList();

}
