package com.sunmax.common.dto.system.dynamic;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "HttpForwardDto", description = "储能http数据转发类")
public class StorageHttpForwardDto {

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

}
