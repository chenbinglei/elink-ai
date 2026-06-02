package com.sunmax.together.vo.operation.runScene;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "SitePileMonitorQueryVo", description = "站点电桩查询条件参数实体类")
public class SitePileMonitorQueryVo {

    /**
     * 用户id
     */
    @ApiModelProperty(value = "用户id", required = true)
    private String userId;

    /**
     * 能源场景类型 1-光伏 2-储能 3-充电桩 4-用能 5-配电
     */
    @ApiModelProperty(value = "能源场景类型 1-光伏 2-储能 3-充电桩 4-用能 5-配电", required = true)
    private Integer scenarioTypes;

    /**
     * 多个站点id
     */
    @ApiModelProperty(value = "多个站点id")
    private String siteIds;

    /**
     * -1-未知 1-充电 2-放电 3-空闲 4-占用 5-故障 6-离线 7-未注册 8-预约中
     */
    @ApiModelProperty(value = "状态 -1-未知 1-充电 2-放电 3-空闲 4-占用 5-故障 6-离线 7-未注册 8-预约中")
    private Integer status;

}
