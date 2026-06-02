package com.sunmax.device.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "AffiliatesInfoDto", description = "关联方信息返回实体")
public class AffiliatesInfoDto {

    /**
     * 主键id
     */
    @ApiModelProperty(value = "主键id")
    private String id;

    /**
     * 关联方类型 1-用能单位 2-产权单位 3-运营单位 4-建设单位 5-供电单位(多个以逗号分割)
     */
    @ApiModelProperty(value = "关联方类型 1-用能单位 2-产权单位 3-运营单位 4-建设单位 5-供电单位(多个以逗号分割)")
    private String affiliateTypes;

    /**
     * 所属租户id
     */
    @ApiModelProperty(value = "所属租户id")
    private String tenantId;

    /**
     * 所属租户名称
     */
    @ApiModelProperty(value = "所属租户名称")
    private String tenantName;

    /**
     * 联系人名
     */
    @ApiModelProperty(value = "联系人名")
    private String contactsName;

    /**
     * 联系人电话
     */
    @ApiModelProperty(value = "联系人电话")
    private String contactsPhone;

    /**
     * 所属站点id
     */
    @ApiModelProperty(value = "所属站点id")
    private String siteId;
}
