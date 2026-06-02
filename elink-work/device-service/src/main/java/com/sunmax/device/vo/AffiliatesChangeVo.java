package com.sunmax.device.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "AffiliatesChangeVo", description = "关联方信息编辑参数实体")
public class AffiliatesChangeVo {

    /**
     * 主键id
     */
    @ApiModelProperty(value = "主键id")
    private String id;

    /**
     * 关联方类型 1-用能单位 2-产权单位 3-运营单位 4-建设单位 5-供电单位(多个以逗号分割)
     */
    @ApiModelProperty(value = "关联方类型 1-用能单位 2-产权单位 3-运营单位 4-建设单位 5-供电单位(多个以逗号分割)", required = true)
    private String affiliateTypes;

    /**
     * 所属租户id
     */
    @ApiModelProperty(value = "所属租户id", required = true)
    private String tenantId;

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
    @ApiModelProperty(value = "所属站点id", required = true)
    private String siteId;
}
