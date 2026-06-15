package com.sunmax.common.vo.crontab;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "智慧能源综合管理平台站点列表查询条件参数")
public class SiteListQueryVo {

    /**
     * 当前用户id
     */
    @Schema(description = "当前用户id")
    private String userId;

    /**
     * 站点名称模糊查询
     */
    @Schema(description = "站点名称模糊查询")
    private String siteName;

    /**
     * 站点状态 1-正常 2-预警 3-通信异常 4-故障
     */
    @Schema(description = "站点状态 1-正常 2-预警 3-通信异常 4-故障")
    private String siteState;

    /**
     * 能源场景类型 1-光伏 2-储能 3-充电桩 4-用能 5-配电 6-换电(多个以逗号分割)
     */
    @Schema(description = "能源场景类型 1-光伏 2-储能 3-充电桩 4-用能 5-配电 6-换电(多个以逗号分割)")
    private String scenarioTypes;

    /**
     * 区域类型 1-省 2-市 3-区
     */
    @Schema(description = "区域类型 1-省 2-市 3-区")
    private Integer areaType;

    /**
     * 区域值
     */
    @Schema(description = "区域值")
    private String areaValue;
}
