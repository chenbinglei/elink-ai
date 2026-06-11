package com.sunmax.together.dto.operation.settlement;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "站点账户列表返回实体类")
public class SiteAccountListDto {

    /**
     * 租户id
     */
    @Schema(description = "租户id")
    private String tenantId;

    /**
     * 租户名称
     */
    @Schema(description = "租户名称")
    private String tenantName;

    /**
     * 站点id
     */
    @Schema(description = "站点id")
    private String siteId;

    /**
     * 权限 1-只读 2-读写
     */
    @Schema(description = "权限 1-只读 2-读写")
    private Integer authority;

    /**
     * 站点名称
     */
    @Schema(description = "站点名称")
    private String siteName;

    /**
     * 所在省份
     */
    @Schema(description = "所在省份")
    private String province;

    /**
     * 所在市
     */
    @Schema(description = "所在市")
    private String city;

    /**
     * 收款主键id
     */
    @Schema(description = "收款主键id")
    private String recId;

    /**
     * 收款商户名称
     */
    @Schema(description = "收款商户名称")
    private String recMchName;

    /**
     * 收款商户id
     */
    @Schema(description = "收款商户id")
    private String recMchId;

    /**
     * 付款主键id
     */
    @Schema(description = "付款主键id")
    private String payId;

    /**
     * 付款商户名称
     */
    @Schema(description = "付款商户名称")
    private String payMchName;

    /**
     * 付款商户id
     */
    @Schema(description = "付款商户id")
    private String payMchId;

}
