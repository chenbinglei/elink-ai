package com.sunmax.common.dto.system;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "租户详情信息返回实体类")
public class TenantDetailsDto {

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
     * 手机号
     */
    @Schema(description = "手机号")
    private String phone;

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
     * 营业执照
     */
    @Schema(description = "营业执照")
    private String businessLicense;

    /**
     * 描述
     */
    @Schema(description = "描述")
    private String refer;

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
     * 创建时间
     */
    @Schema(description = "创建时间")
    private String createTime;

    /**
     * 修改人员名称
     */
    @Schema(description = "修改人员名称")
    private String updateUserName;

    /**
     * 修改时间
     */
    @Schema(description = "修改时间")
    private String updateTime;

    /**
     * 租户所属组织架构id
     */
    @Schema(description = "租户所属组织架构id")
    private String organStructureId;
}
