package com.sunmax.common.dto.device;

import com.google.common.collect.Lists;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "站点关联场景信息返回实体类")
public class SiteScenarioTypeDto {

    /**
     * 主键id
     */
    @Schema(description = "主键id")
    private String id;

    /**
     * 能源类型 1-光伏 2-储能 3-充电桩 4-用能 5-配电 6-换电
     */
    @Schema(description = "能源类型 1-光伏 2-储能 3-充电桩 4-用能 5-配电 6-换电")
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
     * 模型名称
     */
    @Schema(description = "模型名称")
    private String modelName;

    /**
     * 读写数据对象
     */
    @Schema(description = "读写数据对象")
    private String readwriteObject;

    /**
     * 扩展属性对象列表
     */
    @Schema(description = "扩展属性对象列表")
    private List<DeviceReaDto> reaList = Lists.newArrayList();
}
