package com.sunmax.common.dto.together;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "UserGroupInfoDto", description = "用户分组信息返回实体类")
public class UserGroupInfoDto {

    /**
     * 主键id
     */
    @ApiModelProperty(value = "主键id")
    private String id;

    /**
     * 分组名称
     */
    @ApiModelProperty(value = "分组名称")
    private String groupName;

    /**
     * 电费折扣(百分比值)
     */
    @ApiModelProperty(value = "电费折扣(百分比值)")
    private Integer elecDiscount = 100;

    /**
     * 服务费折扣(百分比值)
     */
    @ApiModelProperty(value = "服务费折扣(百分比值)")
    private Integer serviceDiscount = 100;

    /**
     * 描述
     */
    @ApiModelProperty(value = "描述")
    private String refer;
}
