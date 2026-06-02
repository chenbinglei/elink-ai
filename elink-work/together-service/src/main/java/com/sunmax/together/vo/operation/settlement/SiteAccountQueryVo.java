package com.sunmax.together.vo.operation.settlement;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 站点账户查询参数
 */
@Data
@ApiModel(value = "SiteAccountQueryVo", description = "站点账户查询参数")
public class SiteAccountQueryVo {

    /**
     * 用户id
     */
    @ApiModelProperty(value = "用户id", required = true)
    private String userId;

    /**
     * 租户id
     */
    @ApiModelProperty(value = "租户id")
    private String tenantId;

    /**
     * 站点id
     */
    @ApiModelProperty(value = "站点id")
    private String siteId;

    /**
     * 区域类型 1-省 2-市
     */
    @ApiModelProperty(value = "区域类型 1-省 2-市")
    private Integer areaType;

    /**
     * 区域值
     */
    @ApiModelProperty(value = "区域值")
    private String areaValue;

    /**
     * 当前页
     */
    @ApiModelProperty(value = "当前页", required = true)
    private Integer page;

    /**
     * 当前页条数
     */
    @ApiModelProperty(value = "当前页条数", required = true)
    private Integer size;

}
