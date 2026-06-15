package com.sunmax.device.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "站点能源场景类型编辑参数实体类")
public class ScenarioTypeChangeVo {

    /**
     * 主键id
     */
    @Schema(description = "主键id")
    private String id;

    /**
     * 能源类型 1-光伏 2-储能 3-充电桩 4-用能 5-配电
     */
    @Schema(description = "能源类型 1-光伏 2-储能 3-充电桩 4-用能 5-配电")
    private Integer scenarioType;

    /**
     * 能源系统名称
     */
    @Schema(description = "能源系统名称")
    private String systemName;

    /**
     * 模型id
     */
    @Schema(description = "模型id")
    private String modelId;

    /**
     * 所属站点id
     */
    @Schema(description = "所属站点id")
    private String siteId;

    /**
     * 读写数据对象
     */
    @Schema(description = "读写数据对象")
    private String readwriteObject;
}
