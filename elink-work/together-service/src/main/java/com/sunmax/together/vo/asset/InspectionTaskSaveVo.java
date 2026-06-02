package com.sunmax.together.vo.asset;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "InspectionTaskSaveVo", description = "巡检任务新增实体类")
public class InspectionTaskSaveVo {

    /**
     * 租户id
     */
    @ApiModelProperty(value = "租户id", required = true)
    private String tenantId;

    /**
     * 任务名称
     */
    @ApiModelProperty(value = "任务名称", required = true)
    private String taskName;

    /**
     * 任务描述
     */
    @ApiModelProperty(value = "任务描述")
    private String taskDesc;

    /**
     * 用户id
     */
    @ApiModelProperty(value = "用户id", required = true)
    private String userId;

    /**
     * 多个站点id
     */
    @ApiModelProperty(value = "多个站点id 例如['站点id1','站点id2']", required = true)
    private String siteIds;

}
