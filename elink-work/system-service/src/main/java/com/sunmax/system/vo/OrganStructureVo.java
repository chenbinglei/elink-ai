package com.sunmax.system.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "OrganStructureVo", description = "组织架构新增或编辑参数")
public class OrganStructureVo {

    /**
     * 唯一id
     */
    @ApiModelProperty(value = "唯一id")
    private String id;

    /**
     * 组织名称
     */
    @ApiModelProperty(value = "组织名称", required = true)
    private String organName;

    /**
     * 父级id
     */
    @ApiModelProperty(value = "父级id", required = true)
    private String parentId;

    /**
     * 排序号
     */
    @ApiModelProperty(value = "排序号", required = true)
    private Integer sortNumber;

    /**
     * 所属租户id
     */
    @ApiModelProperty(value = "所属租户id", required = true)
    private String tenantId;
}
