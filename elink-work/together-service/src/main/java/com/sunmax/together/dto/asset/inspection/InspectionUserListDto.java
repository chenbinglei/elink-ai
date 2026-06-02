package com.sunmax.together.dto.asset.inspection;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "InspectionUserListDto", description = "节点人员设置列表返回实体类")
public class InspectionUserListDto {

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
    @ApiModelProperty(value = "多个用户id")
    private String userIds;

    /**
     * 用户数量
     */
    @ApiModelProperty(value = "用户数量")
    private Integer userNum;

}
