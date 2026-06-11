package com.sunmax.common.dto.together.ops;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "巡检进行中的任务列表返回实体类")
public class AppInspectHandTaskListDto {

    /**
     * 主键id
     */
    @Schema(description = "主键id")
    private String id;

    /**
     * 任务名称
     */
    @Schema(description = "任务名称")
    private String taskName;

    /**
     * 任务描述
     */
    @Schema(description = "任务描述")
    private String taskDesc;

    /**
     * 任务状态 1-未分配 2-未开启 3-巡检中 4-待验收 5-完结
     */
    @Schema(description = "任务状态 1-未分配 2-未开启 3-巡检中 4-待验收 5-完结")
    private Integer taskStatus;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    private String createTime;

    /**
     * 修改时间
     */
    @Schema(description = "修改时间")
    private String updateTime;

}
