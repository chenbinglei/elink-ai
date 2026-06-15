package com.sunmax.system.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "用户组应用授权配置信息参数")
public class GroupApplyEmpowerVo {

    /**
     * 所属用户组id
     */
    @Schema(description = "所属用户组id")
    private String groupId;

    /**
     * 所属模块id
     */
    @Schema(description = "所属模块id")
    private String moduleId;

    /**
     * 所属权限id
     */
    @Schema(description = "所属权限id")
    private String permissionId;

    /**
     * 操作 1-开启 2-关闭
     */
    @Schema(description = "操作 1-开启 2-关闭")
    private Integer operate;
}
