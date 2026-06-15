package com.sunmax.configure.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "图模变量编辑参数实体类")
public class GraphVariableChangeVo {

    /**
     * 主键id
     */
    @Schema(description = "主键id")
    private String id;

    /**
     * 关联图模id
     */
    @Schema(description = "关联图模id")
    private String graphId;

    /**
     * 变量名
     */
    @Schema(description = "变量名")
    private String name;

    /**
     * 变量类型
     */
    @Schema(description = "变量类型")
    private String type;

    /**
     * 关联图形数据源id
     */
    @Schema(description = "关联图形数据源id")
    private String graphSourceId;

    /**
     * 数据对象
     */
    @Schema(description = "数据对象")
    private String dataObject;

    /**
     * 数据点
     */
    @Schema(description = "数据点")
    private String dataPoint;

    /**
     * 数据点下标
     */
    @Schema(description = "数据点下标")
    private String dataPointIndex;

    /**
     * 描述
     */
    @Schema(description = "描述")
    private String description;

}
