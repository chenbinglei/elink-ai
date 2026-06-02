package com.sunmax.system.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 产品编辑信息参数
 */
@Data
@ApiModel(value = "productChangeVo", description = "产品编辑信息参数")
public class ProductChangeVo {

    /**
     * 产品id
     */
    @ApiModelProperty(value = "产品id")
    private String id;

    /**
     * 产品名称
     */
    @ApiModelProperty(value = "产品名称", required = true)
    private String productName;

    /**
     * 客户端id
     */
    @ApiModelProperty("客户端id")
    private String clientId;
}
