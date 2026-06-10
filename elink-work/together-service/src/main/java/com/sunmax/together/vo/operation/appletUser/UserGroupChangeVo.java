package com.sunmax.together.vo.operation.appletUser;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "用户分组编辑参数")
public class UserGroupChangeVo {

    /**
     * 主键id
     */
    @Schema(description = "主键id")
    private String id;

    /**
     * 分组名称
     */
    @Schema(description = "分组名称")
    private String groupName;

    /**
     * 电费折扣
     */
    @Schema(description = "电费折扣")
    private Integer elecDiscount;

    /**
     * 服务费折扣
     */
    @Schema(description = "服务费折扣")
    private Integer serviceDiscount;

    /**
     * 描述
     */
    @Schema(description = "描述")
    private String refer;

    /**
     * 应用站点id(多个以逗号分割)
     */
    @Schema(description = "应用站点id(多个以逗号分割)")
    private String applySiteIds;
}
