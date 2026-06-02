package com.sunmax.common.vo.together.ops;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "InspectionTaskUpdateVo", description = "巡检任务编辑实体类")
public class InspectionTaskUpdateVo {

    /**
     * 用户id
     */
    @ApiModelProperty(value = "用户id", required = true)
    private String userId;

    /**
     * 巡检任务id
     */
    @ApiModelProperty(value = "巡检任务id", required = true)
    private String id;

    /**
     * 操作类型 0-交接分配 1-提交任务 2-交接任务 3-退回任务 4-开始巡检 5-完成巡检 6-确认验收 7-退回验收 8-交接验收
     */
    @ApiModelProperty(value = "操作类型 0-交接分配 1-提交任务 2-交接任务 3-退回任务 4-开始巡检 5-完成巡检 6-确认验收 7-退回验收 8-交接验收", required = true)
    private Integer operationType;

    /**
     * 操作用户id
     */
    @ApiModelProperty(value = "操作用户id")
    private String operationUserId;

    /**
     * 操作意见
     */
    @ApiModelProperty(value = "操作意见")
    private String operationOpinion;

}
