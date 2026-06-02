package com.sunmax.together.dto.asset.inspection;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "SiteSaveTaskListDto", description = "新增任务站点列表返回实体类")
public class SiteSaveTaskListDto {

    /**
     * 主键id
     */
    @ApiModelProperty(value = "主键id")
    private String id;

    /**
     * 站点名称
     */
    @ApiModelProperty(value = "站点名称")
    private String siteName;

    /**
     * 位置
     */
    @ApiModelProperty(value = "位置")
    private String location;

    /**
     * 租户名称
     */
    @ApiModelProperty(value = "租户名称")
    private String tenantName;

    /**
     * 上次巡检时间
     */
    @ApiModelProperty(value = "上次巡检时间")
    private String lastInspectionTime;

    /**
     * 是否巡检中 1-是 2-否
     */
    @ApiModelProperty(value = "是否巡检中 1-是 2-否")
    private Integer isInspection = 2;

}
