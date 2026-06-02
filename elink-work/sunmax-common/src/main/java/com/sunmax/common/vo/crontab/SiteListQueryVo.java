package com.sunmax.common.vo.crontab;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "SiteListQueryVo", description = "智慧能源综合管理平台站点列表查询条件参数")
public class SiteListQueryVo {

    /**
     * 当前用户id
     */
    @ApiModelProperty(value = "当前用户id", required = true)
    private String userId;

    /**
     * 站点名称模糊查询
     */
    @ApiModelProperty(value = "站点名称模糊查询")
    private String siteName;

    /**
     * 站点状态 1-正常 2-预警 3-通信异常 4-故障
     */
    @ApiModelProperty(value = "站点状态 1-正常 2-预警 3-通信异常 4-故障")
    private String siteState;

    /**
     * 能源场景类型 1-光伏 2-储能 3-充电桩 4-用能 5-配电 6-换电(多个以逗号分割)
     */
    @ApiModelProperty(value = "能源场景类型 1-光伏 2-储能 3-充电桩 4-用能 5-配电 6-换电(多个以逗号分割)")
    private String scenarioTypes;

    /**
     * 区域类型 1-省 2-市 3-区
     */
    @ApiModelProperty(value = "区域类型 1-省 2-市 3-区")
    private Integer areaType;

    /**
     * 区域值
     */
    @ApiModelProperty(value = "区域值")
    private String areaValue;
}
