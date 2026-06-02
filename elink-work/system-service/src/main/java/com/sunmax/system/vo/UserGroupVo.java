package com.sunmax.system.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "UserGroupVo", description = "用户组新增或编辑参数")
public class UserGroupVo {

    /**
     * 唯一id
     */
    @ApiModelProperty(value = "唯一id")
    private String id;

    /**
     * 用户组名称
     */
    @ApiModelProperty(value = "用户组名称", required = true)
    private String groupName;

    /**
     * 描述
     */
    @ApiModelProperty(value = "描述")
    private String refer;

    /**
     * 所属租户id
     */
    @ApiModelProperty(value = "所属租户id", required = true)
    private String tenantId;
}
