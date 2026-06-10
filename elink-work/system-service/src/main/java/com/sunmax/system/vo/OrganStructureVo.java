package com.sunmax.system.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "组织架构新增或编辑参数")
public class OrganStructureVo {

    /**
     * 唯一id
     */
    @Schema(description = "唯一id")
    private String id;

    /**
     * 组织名称
     */
    @Schema(description = "组织名称")
    private String organName;

    /**
     * 父级id
     */
    @Schema(description = "父级id")
    private String parentId;

    /**
     * 排序号
     */
    @Schema(description = "排序号")
    private Integer sortNumber;

    /**
     * 所属租户id
     */
    @Schema(description = "所属租户id")
    private String tenantId;
}
