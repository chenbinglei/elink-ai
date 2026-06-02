package com.sunmax.configure.dto.province;

import com.google.common.collect.Sets;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Set;

@Data
@ApiModel(value = "SubstationFieldDto", description = "充电站字段返回实体类")
public class SubstationFieldDto {

    /**
     * 数据转发id
     */
    @ApiModelProperty(value = "数据转发id")
    private String dataForwardId;

    /**
     * 站点主键id
     */
    @ApiModelProperty(value = "站点主键id")
    private String siteId;

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

    /**
     * 站点运营商Id
     */
    @ApiModelProperty(value = "站点运营商Id")
    private String operatorId;

    /**
     * 充电服务运营商 ID，
     * 所属方为个人时填写 999999999
     */
    @ApiModelProperty(value = "充电服务运营商ID")
    private String equipmentOwnerId;

    /**
     * 充电站ID
     */
    @ApiModelProperty(value = "充电站ID")
    private String stationId;

    /**
     * 多个电桩编码
     */
    @ApiModelProperty(value = "多个电桩编码")
    private Set<String> pileCodes = Sets.newHashSet();

}
