package com.sunmax.system.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "租户新增或编辑参数")
public class TenantInfoVo {


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
     * 地址
     */
    @Schema(description = "地址")
    private String address;

    /**
     * 组织机构代码
     */
    @Schema(description = "组织机构代码")
    private String organizationCode;

    /**
     * 描述
     */
    @Schema(description = "描述")
    private String refer;

    /**
     * 营业执照
     */
    @Schema(description = "营业执照")
    private String businessLicense;

    /**
     * 标识
     */
    @Schema(description = "标识")
    private String logo;

    /**
     * 创建人员名称
     */
    @Schema(description = "创建人员名称")
    private String createUserName;

    /**
     * 修改人员名称
     */
    @Schema(description = "修改人员名称")
    private String updateUserName;
}
