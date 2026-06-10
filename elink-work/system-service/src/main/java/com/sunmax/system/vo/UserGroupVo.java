package com.sunmax.system.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "用户组新增或编辑参数")
public class UserGroupVo {

    /**
     * 唯一id
     */
    @Schema(description = "唯一id")
    private String id;

    /**
     * 用户组名称
     */
    @Schema(description = "用户组名称")
    private String groupName;

    /**
     * 描述
     */
    @Schema(description = "描述")
    private String refer;

    /**
     * 所属租户id
     */
    @Schema(description = "所属租户id")
    private String tenantId;
}
