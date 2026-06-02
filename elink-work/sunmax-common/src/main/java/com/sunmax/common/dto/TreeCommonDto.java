package com.sunmax.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Transient;
import java.util.List;

/**
 * 树形交互类
 */
@Data
@ApiModel("TreeCommon")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TreeCommonDto {

    /**
     * id
     */
    @ApiModelProperty("子节点id")
    private String id;

    /**
     * 名称
     */
    @ApiModelProperty("名称")
    private String name;

    /**
     * 父id
     */
    @ApiModelProperty("父节点id")
    private String parentId;

    /**
     * 企业id
     */
    @ApiModelProperty("企业id")
    private String companyId;

    /**
     * 类型
     */
    @ApiModelProperty("类型")
    private Integer type;

//
//    /**
//     * 子节点条数
//     */
//    @ApiModelProperty("子节点条数")
//    private Long total;

    @Transient
    private List<TreeCommonDto> children;

}
