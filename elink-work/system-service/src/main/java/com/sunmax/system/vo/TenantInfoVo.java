package com.sunmax.system.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "TenantInfoVo", description = "租户新增或编辑参数")
public class TenantInfoVo {


    /**
     * 唯一id
     */
    @ApiModelProperty(value = "唯一id")
    private String id;

    /**
     * 租户名称
     */
    @ApiModelProperty(value = "租户名称", required = true)
    private String tenantName;

    /**
     * 超管账号
     */
    @ApiModelProperty(value = "超管账号", required = true)
    private String superAccount;

    /**
     * 超管密码
     */
    @ApiModelProperty(value = "超管密码", required = true)
    private String password;

    /**
     * 租户状态 0-关闭 1-开启
     */
    @ApiModelProperty(value = "租户状态 0-关闭 1-开启", required = true)
    private Integer tenantState;

    /**
     * 地址
     */
    @ApiModelProperty(value = "地址")
    private String address;

    /**
     * 组织机构代码
     */
    @ApiModelProperty(value = "组织机构代码")
    private String organizationCode;

    /**
     * 描述
     */
    @ApiModelProperty(value = "描述")
    private String refer;

    /**
     * 营业执照
     */
    @ApiModelProperty(value = "营业执照")
    private String businessLicense;

    /**
     * 标识
     */
    @ApiModelProperty(value = "标识")
    private String logo;

    /**
     * 创建人员名称
     */
    @ApiModelProperty(value = "创建人员名称")
    private String createUserName;

    /**
     * 修改人员名称
     */
    @ApiModelProperty(value = "修改人员名称")
    private String updateUserName;
}
