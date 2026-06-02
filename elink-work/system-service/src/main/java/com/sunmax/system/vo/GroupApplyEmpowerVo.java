package com.sunmax.system.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "GroupApplyEmpowerVo", description = "用户组应用授权配置信息参数")
public class GroupApplyEmpowerVo {

    /**
     * 所属用户组id
     */
    @ApiModelProperty(value = "所属用户组id")
    private String groupId;

    /**
     * 所属模块id
     */
    @ApiModelProperty(value = "所属模块id")
    private String moduleId;

    /**
     * 所属权限id
     */
    @ApiModelProperty(value = "所属权限id")
    private String permissionId;

    /**
     * 操作 1-开启 2-关闭
     */
    @ApiModelProperty(value = "操作 1-开启 2-关闭")
    private Integer operate;
}
