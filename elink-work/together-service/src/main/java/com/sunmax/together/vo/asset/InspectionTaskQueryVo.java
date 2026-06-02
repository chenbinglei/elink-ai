package com.sunmax.together.vo.asset;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "InspectionTaskQueryVo", description = "巡检任务查询条件实体类")
public class InspectionTaskQueryVo {

    /**
     * 租户id
     */
    @ApiModelProperty(value = "租户id", required = true)
    private String tenantId;

    /**
     * 任务名称
     */
    @ApiModelProperty(value = "任务名称")
    private String taskName;

    /**
     * 开始日期(年月日)
     */
    @ApiModelProperty(value = "开始日期(年月日)")
    private String startDate;

    /**
     * 结束日期(日期)
     */
    @ApiModelProperty(value = "结束日期(日期)")
    private String endDate;

    /**
     * 任务状态 1-未分配 2-未开启 3-巡检中 4-待验收 5-完结
     */
    @ApiModelProperty(value = "任务状态 1-未分配 2-未开启 3-巡检中 4-待验收 5-完结")
    private Integer taskStatus;

    /**
     * 当前页
     */
    @ApiModelProperty(value = "当前页", required = true)
    private Integer page;

    /**
     * 当前页条数
     */
    @ApiModelProperty(value = "当前页条数", required = true)
    private Integer size;

}
