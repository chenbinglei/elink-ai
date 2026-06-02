package com.sunmax.device.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "ScenarioTypeChangeVo", description = "站点能源场景类型编辑参数实体类")
public class ScenarioTypeChangeVo {

    /**
     * 主键id
     */
    @ApiModelProperty(value = "主键id")
    private String id;

    /**
     * 能源类型 1-光伏 2-储能 3-充电桩 4-用能 5-配电
     */
    @ApiModelProperty(value = "能源类型 1-光伏 2-储能 3-充电桩 4-用能 5-配电", required = true)
    private Integer scenarioType;

    /**
     * 能源系统名称
     */
    @ApiModelProperty(value = "能源系统名称", required = true)
    private String systemName;

    /**
     * 模型id
     */
    @ApiModelProperty(value = "模型id", required = true)
    private String modelId;

    /**
     * 所属站点id
     */
    @ApiModelProperty(value = "所属站点id", required = true)
    private String siteId;

    /**
     * 读写数据对象
     */
    @ApiModelProperty(value = "读写数据对象")
    private String readwriteObject;
}
