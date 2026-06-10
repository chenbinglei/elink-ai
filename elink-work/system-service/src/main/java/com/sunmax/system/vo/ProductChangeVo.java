package com.sunmax.system.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 产品编辑信息参数
 */
@Data
@Schema(description = "产品编辑信息参数")
public class ProductChangeVo {

    /**
     * 产品id
     */
    @Schema(description = "产品id")
    private String id;

    /**
     * 产品名称
     */
    @Schema(description = "产品名称")
    private String productName;

    /**
     * 客户端id
     */
    @Schema(description = "客户端id")
    private String clientId;
}
