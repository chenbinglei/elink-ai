package com.sunmax.configure.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "GraphVariableChangeVo", description = "图模变量编辑参数实体类")
public class GraphVariableChangeVo {

    /**
     * 主键id
     */
    @ApiModelProperty(value = "主键id")
    private String id;

    /**
     * 关联图模id
     */
    @ApiModelProperty(value = "关联图模id", required = true)
    private String graphId;

    /**
     * 变量名
     */
    @ApiModelProperty(value = "变量名", required = true)
    private String name;

    /**
     * 变量类型
     */
    @ApiModelProperty(value = "变量类型", required = true)
    private String type;

    /**
     * 关联图形数据源id
     */
    @ApiModelProperty(value = "关联图形数据源id", required = true)
    private String graphSourceId;

    /**
     * 数据对象
     */
    @ApiModelProperty(value = "数据对象")
    private String dataObject;

    /**
     * 数据点
     */
    @ApiModelProperty(value = "数据点", required = true)
    private String dataPoint;

    /**
     * 数据点下标
     */
    @ApiModelProperty(value = "数据点下标")
    private String dataPointIndex;

    /**
     * 描述
     */
    @ApiModelProperty(value = "描述")
    private String description;

}
