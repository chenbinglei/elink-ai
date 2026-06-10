package com.sunmax.system.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "租户列表返回实体类")
public class TenantListDto {

    /**
     * 唯一id
     */
    @Schema(description = "唯一id")
    private String id;

    /**
     * 租户名称
     */
    @Schema(description = "租户名称")
    private String tenantName;

    /**
     * 超管账号
     */
    @Schema(description = "超管账号")
    private String superAccount;

    /**
     * 超管密码
     */
    @Schema(description = "超管密码")
    private String password;

    /**
     * 租户状态 0-关闭 1-开启
     */
    @Schema(description = "租户状态 0-关闭 1-开启")
    private Integer tenantState;

    /**
     * 创建人员名称
     */
    @Schema(description = "创建人员名称")
    private String createUserName;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    private String createTime;
}
