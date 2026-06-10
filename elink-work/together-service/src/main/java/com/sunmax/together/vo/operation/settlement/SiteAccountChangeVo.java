package com.sunmax.together.vo.operation.settlement;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "站点账户编辑参数")
public class SiteAccountChangeVo {

    /**
     * 主键id
     */
    @Schema(description = "主键id")
    private String id;

    /**
     * 站点id
     */
    @Schema(description = "站点id")
    private String siteId;

    /**
     * 租户id
     */
    @Schema(description = "租户id")
    private String tenantId;

    /**
     * 支付平台 1-微信
     */
    @Schema(description = "支付平台 1-微信")
    private Integer payPlatform;

    /**
     * 租户账号id
     */
    @Schema(description = "租户账号id")
    private String accountId;

    /**
     * 类型 1-收款账户 2-付款账户 3-分帐账户
     */
    @Schema(description = "类型 1-收款账户 2-付款账户 3-分帐账户")
    private Integer type;

    /**
     * 比例(%)
     */
    @Schema(description = "比例(%)")
    private Double ratio;

}
