package com.sunmax.together.vo.operation.runScene;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "站点电桩查询条件参数实体类")
public class SitePileMonitorQueryVo {

    /**
     * 用户id
     */
    @Schema(description = "用户id")
    private String userId;

    /**
     * 能源场景类型 1-光伏 2-储能 3-充电桩 4-用能 5-配电
     */
    @Schema(description = "能源场景类型 1-光伏 2-储能 3-充电桩 4-用能 5-配电")
    private Integer scenarioTypes;

    /**
     * 多个站点id
     */
    @Schema(description = "多个站点id")
    private String siteIds;

    /**
     * -1-未知 1-充电 2-放电 3-空闲 4-占用 5-故障 6-离线 7-未注册 8-预约中
     */
    @Schema(description = "状态 -1-未知 1-充电 2-放电 3-空闲 4-占用 5-故障 6-离线 7-未注册 8-预约中")
    private Integer status;

}
