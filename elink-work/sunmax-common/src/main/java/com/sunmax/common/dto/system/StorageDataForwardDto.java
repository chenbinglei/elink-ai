package com.sunmax.common.dto.system;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.compress.utils.Lists;

import java.util.List;

@Data
public class StorageDataForwardDto {

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
     * 应用ID
     */
    @ApiModelProperty(value = "应用ID")
    private String appId;

    /**
     * 公钥
     */
    @ApiModelProperty(value = "公钥")
    private String publicKey;

    /**
     * 私钥
     */
    @ApiModelProperty(value = "私钥")
    private String privateKey;

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
