package com.sunmax.system.dto;

import com.sunmax.system.entity.ProductEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.beans.BeanUtils;

/**
 * 产品列表数据返回实体类
 */
@Data
@Schema(description = "productListDto")
public class ProductListDto {

    /**
     * 产品id
     */
    @Schema(description = "产品id")
    public String id;

    /**
     * 产品名称
     */
    @Schema(description = "产品名称")
    private String productName;

    /**
     * 英文名称
     */
    @Schema(description = "产品英文名称")
    private String englishName;

    /**
     * 客户端id
     */
    @Schema(description = "客户端id")
    private String clientId;

    /**
     * 目录排序
     */
    @Schema(description = "目录排序")
    private Integer directoryDesc;

    /**
     * 权限状态 0-显示 1-不显示
     */
    @Schema(description = "权限状态 0-显示 1-不显示")
    private Integer isHidden;

    /**
     * 父节点id
     */
    @Schema(description = "父节点id")
    private String parentId;

    public ProductListDto(ProductEntity productEntity) {
        BeanUtils.copyProperties(productEntity,this);
    }
}
