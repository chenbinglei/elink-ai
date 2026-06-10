package com.sunmax.common.dto.system;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.apache.commons.compress.utils.Lists;

import java.util.List;

@Data
public class StorageDataForwardDto {

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
     * 应用ID
     */
    @Schema(description = "应用ID")
    private String appId;

    /**
     * 公钥
     */
    @Schema(description = "公钥")
    private String publicKey;

    /**
     * 私钥
     */
    @Schema(description = "私钥")
    private String privateKey;

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
