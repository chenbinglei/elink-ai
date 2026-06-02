package com.sunmax.together.vo.operation.appletUser;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "UserGroupChangeVo", description = "用户分组编辑参数")
public class UserGroupChangeVo {

    /**
     * 主键id
     */
    @ApiModelProperty(value = "主键id")
    private String id;

    /**
     * 分组名称
     */
    @ApiModelProperty(value = "分组名称", required = true)
    private String groupName;

    /**
     * 电费折扣
     */
    @ApiModelProperty(value = "电费折扣", required = true)
    private Integer elecDiscount;

    /**
     * 服务费折扣
     */
    @ApiModelProperty(value = "服务费折扣", required = true)
    private Integer serviceDiscount;

    /**
     * 描述
     */
    @ApiModelProperty(value = "描述")
    private String refer;

    /**
     * 应用站点id(多个以逗号分割)
     */
    @ApiModelProperty(value = "应用站点id(多个以逗号分割)", required = true)
    private String applySiteIds;
}
