package com.sunmax.system.dto;

import com.sunmax.system.entity.ProductEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.beans.BeanUtils;

/**
 * 产品列表数据返回实体类
 */
@Data
@ApiModel("productListDto")
public class ProductListDto {

    /**
     * 产品id
     */
    @ApiModelProperty("产品id")
    public String id;

    /**
     * 产品名称
     */
    @ApiModelProperty("产品名称")
    private String productName;

    /**
     * 英文名称
     */
    @ApiModelProperty("产品英文名称")
    private String englishName;

    /**
     * 客户端id
     */
    @ApiModelProperty("客户端id")
    private String clientId;

    /**
     * 目录排序
     */
    @ApiModelProperty("目录排序")
    private Integer directoryDesc;

    /**
     * 权限状态 0-显示 1-不显示
     */
    @ApiModelProperty("权限状态 0-显示 1-不显示")
    private Integer isHidden;

    /**
     * 父节点id
     */
    @ApiModelProperty("父节点id")
    private String parentId;

    public ProductListDto(ProductEntity productEntity) {
        BeanUtils.copyProperties(productEntity,this);
    }
}
