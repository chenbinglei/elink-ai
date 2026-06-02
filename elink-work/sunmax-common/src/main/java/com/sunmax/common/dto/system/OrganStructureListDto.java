package com.sunmax.common.dto.system;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "OrganStructureListDto", description = "组织结构信息列表返回实体类")
public class OrganStructureListDto {

    /**
     * 唯一id
     */
    @ApiModelProperty(value = "唯一id")
    private String id;

    /**
     * 组织名称
     */
    @ApiModelProperty(value = "组织名称")
    private String organName;

    /**
     * 父级id
     */
    @ApiModelProperty(value = "父级id")
    private String parentId;

    /**
     * 排序号
     */
    @ApiModelProperty(value = "排序号")
    private Integer sortNumber;

    /**
     * 所属租户id
     */
    @ApiModelProperty(value = "所属租户id")
    private String tenantId;

}
