package com.sunmax.device.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "关联方信息返回实体")
public class AffiliatesInfoDto {

    /**
     * 主键id
     */
    @Schema(description = "主键id")
    private String id;

    /**
     * 关联方类型 1-用能单位 2-产权单位 3-运营单位 4-建设单位 5-供电单位(多个以逗号分割)
     */
    @Schema(description = "关联方类型 1-用能单位 2-产权单位 3-运营单位 4-建设单位 5-供电单位(多个以逗号分割)")
    private String affiliateTypes;

    /**
     * 所属租户id
     */
    @Schema(description = "所属租户id")
    private String tenantId;

    /**
     * 所属租户名称
     */
    @Schema(description = "所属租户名称")
    private String tenantName;

    /**
     * 联系人名
     */
    @Schema(description = "联系人名")
    private String contactsName;

    /**
     * 联系人电话
     */
    @Schema(description = "联系人电话")
    private String contactsPhone;

    /**
     * 所属站点id
     */
    @Schema(description = "所属站点id")
    private String siteId;
}
