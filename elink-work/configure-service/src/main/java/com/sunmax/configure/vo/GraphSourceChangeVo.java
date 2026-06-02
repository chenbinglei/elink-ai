package com.sunmax.configure.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "GraphSourceChangeVo", description = "图源关联数据源编辑参数实体类")
public class GraphSourceChangeVo {

    /**
     * 主键id
     */
    @ApiModelProperty(value = "主键id")
    private String id;

    /**
     * 关联图模id
     */
    @ApiModelProperty(value = "关联图模id")
    private String graphId;

    /**
     * 名称
     */
    @ApiModelProperty(value = "名称")
    private String name;

    /**
     * 数据源id
     */
    @ApiModelProperty(value = "数据源id")
    private String dataSourceId;

    /**
     * 请求参数Value
     */
    @ApiModelProperty(value = "请求参数Value")
    private String requestValue;

    /**
     * 响应参数
     */
    @ApiModelProperty(value = "响应参数")
    private String responseValue;

    /**
     * 编辑类型 1-新增 2-编辑 3-删除
     */
    @ApiModelProperty(value = "编辑类型 1-新增 2-编辑 3-删除")
    private Integer updateType;

}
