package com.sunmax.together.vo.asset;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "巡检任务查询条件实体类")
public class InspectionTaskQueryVo {

    /**
     * 租户id
     */
    @Schema(description = "租户id")
    private String tenantId;

    /**
     * 任务名称
     */
    @Schema(description = "任务名称")
    private String taskName;

    /**
     * 开始日期(年月日)
     */
    @Schema(description = "开始日期(年月日)")
    private String startDate;

    /**
     * 结束日期(日期)
     */
    @Schema(description = "结束日期(日期)")
    private String endDate;

    /**
     * 任务状态 1-未分配 2-未开启 3-巡检中 4-待验收 5-完结
     */
    @Schema(description = "任务状态 1-未分配 2-未开启 3-巡检中 4-待验收 5-完结")
    private Integer taskStatus;

    /**
     * 当前页
     */
    @Schema(description = "当前页")
    private Integer page;

    /**
     * 当前页条数
     */
    @Schema(description = "当前页条数")
    private Integer size;

}
