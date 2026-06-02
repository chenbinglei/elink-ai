package com.sunmax.system.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "TenantListDto", description = "租户列表返回实体类")
public class TenantListDto {

    /**
     * 唯一id
     */
    @ApiModelProperty(value = "唯一id")
    private String id;

    /**
     * 租户名称
     */
    @ApiModelProperty(value = "租户名称")
    private String tenantName;

    /**
     * 超管账号
     */
    @ApiModelProperty(value = "超管账号")
    private String superAccount;

    /**
     * 超管密码
     */
    @ApiModelProperty(value = "超管密码")
    private String password;

    /**
     * 租户状态 0-关闭 1-开启
     */
    @ApiModelProperty(value = "租户状态 0-关闭 1-开启")
    private Integer tenantState;

    /**
     * 创建人员名称
     */
    @ApiModelProperty(value = "创建人员名称")
    private String createUserName;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    private String createTime;
}
