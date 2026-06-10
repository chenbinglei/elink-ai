package com.sunmax.together.dto.asset.inspection;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "新增任务站点列表返回实体类")
public class SiteSaveTaskListDto {

    /**
     * 主键id
     */
    @Schema(description = "主键id")
    private String id;

    /**
     * 站点名称
     */
    @Schema(description = "站点名称")
    private String siteName;

    /**
     * 位置
     */
    @Schema(description = "位置")
    private String location;

    /**
     * 租户名称
     */
    @Schema(description = "租户名称")
    private String tenantName;

    /**
     * 上次巡检时间
     */
    @Schema(description = "上次巡检时间")
    private String lastInspectionTime;

    /**
     * 是否巡检中 1-是 2-否
     */
    @Schema(description = "是否巡检中 1-是 2-否")
    private Integer isInspection = 2;

}
