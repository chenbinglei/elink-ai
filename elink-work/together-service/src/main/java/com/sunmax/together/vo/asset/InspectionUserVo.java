package com.sunmax.together.vo.asset;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "InspectionUserVo", description = "巡检任务节点人员设置编辑实体类")
public class InspectionUserVo {

    /**
     * 主键id
     */
    @ApiModelProperty(value = "主键id")
    private String id;

    /**
     * 租户id
     */
    @ApiModelProperty(value = "租户id")
    private String tenantId;

    /**
     * 节点类型 1-启动巡检 2-现场巡检 3-巡检结果确认
     */
    @ApiModelProperty(value = "节点类型 1-启动巡检 2-现场巡检 3-巡检结果确认")
    private Integer type;

    /**
     * 多个用户id
     */
    @ApiModelProperty(value = "多个用户id 例如['用户id1','用户id2']")
    private String userIds;

}
