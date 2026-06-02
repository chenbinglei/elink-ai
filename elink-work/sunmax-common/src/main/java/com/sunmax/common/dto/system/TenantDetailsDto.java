package com.sunmax.common.dto.system;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "TenantDetailsDto", description = "租户详情信息返回实体类")
public class TenantDetailsDto {

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
     * 手机号
     */
    @ApiModelProperty(value = "手机号")
    private String phone;

    /**
     * 租户状态 0-关闭 1-开启
     */
    @ApiModelProperty(value = "租户状态 0-关闭 1-开启")
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
     * 营业执照
     */
    @ApiModelProperty(value = "营业执照")
    private String businessLicense;

    /**
     * 描述
     */
    @ApiModelProperty(value = "描述")
    private String refer;

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
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    private String createTime;

    /**
     * 修改人员名称
     */
    @ApiModelProperty(value = "修改人员名称")
    private String updateUserName;

    /**
     * 修改时间
     */
    @ApiModelProperty(value = "修改时间")
    private String updateTime;

    /**
     * 租户所属组织架构id
     */
    @ApiModelProperty(value = "租户所属组织架构id")
    private String organStructureId;
}
