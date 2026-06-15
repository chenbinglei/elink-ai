package com.sunmax.configure.dto.province;

import com.google.common.collect.Sets;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Set;

@Data
@Schema(description = "充电站字段返回实体类")
public class SubstationFieldDto {

    /**
     * 数据转发id
     */
    @Schema(description = "数据转发id")
    private String dataForwardId;

    /**
     * 站点主键id
     */
    @Schema(description = "站点主键id")
    private String siteId;

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

    /**
     * 站点运营商Id
     */
    @Schema(description = "站点运营商Id")
    private String operatorId;

    /**
     * 充电服务运营商 ID，
     * 所属方为个人时填写 999999999
     */
    @Schema(description = "充电服务运营商ID")
    private String equipmentOwnerId;

    /**
     * 充电站ID
     */
    @Schema(description = "充电站ID")
    private String stationId;

    /**
     * 多个电桩编码
     */
    @Schema(description = "多个电桩编码")
    private Set<String> pileCodes = Sets.newHashSet();

}
