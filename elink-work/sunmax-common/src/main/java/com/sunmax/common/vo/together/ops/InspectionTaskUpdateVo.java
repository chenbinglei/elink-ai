package com.sunmax.common.vo.together.ops;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "巡检任务编辑实体类")
public class InspectionTaskUpdateVo {

    /**
     * 用户id
     */
    @Schema(description = "用户id")
    private String userId;

    /**
     * 巡检任务id
     */
    @Schema(description = "巡检任务id")
    private String id;

    /**
     * 操作类型 0-交接分配 1-提交任务 2-交接任务 3-退回任务 4-开始巡检 5-完成巡检 6-确认验收 7-退回验收 8-交接验收
     */
    @Schema(description = "操作类型 0-交接分配 1-提交任务 2-交接任务 3-退回任务 4-开始巡检 5-完成巡检 6-确认验收 7-退回验收 8-交接验收")
    private Integer operationType;

    /**
     * 操作用户id
     */
    @Schema(description = "操作用户id")
    private String operationUserId;

    /**
     * 操作意见
     */
    @Schema(description = "操作意见")
    private String operationOpinion;

}
