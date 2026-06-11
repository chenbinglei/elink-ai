package com.sunmax.together.vo.asset;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "巡检任务新增实体类")
public class InspectionTaskSaveVo {

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
     * 任务描述
     */
    @Schema(description = "任务描述")
    private String taskDesc;

    /**
     * 用户id
     */
    @Schema(description = "用户id")
    private String userId;

    /**
     * 多个站点id
     */
    @Schema(description = "多个站点id 例如['站点id1','站点id2']")
    private String siteIds;

}
