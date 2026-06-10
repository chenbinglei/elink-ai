package com.sunmax.together.vo.asset;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "巡检任务节点人员设置编辑实体类")
public class InspectionUserVo {

    /**
     * 主键id
     */
    @Schema(description = "主键id")
    private String id;

    /**
     * 租户id
     */
    @Schema(description = "租户id")
    private String tenantId;

    /**
     * 节点类型 1-启动巡检 2-现场巡检 3-巡检结果确认
     */
    @Schema(description = "节点类型 1-启动巡检 2-现场巡检 3-巡检结果确认")
    private Integer type;

    /**
     * 多个用户id
     */
    @Schema(description = "多个用户id 例如['用户id1','用户id2']")
    private String userIds;

}
