package com.sunmax.configure.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "GraphSourceDto", description = "图形数据源返回实体类")
public class GraphSourceDto {

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
     * 数据源名称
     */
    @ApiModelProperty(value = "数据源名称")
    private String dataSourceName;

    /**
     * 通信方式 1-websocket 2-http 3-mqtt
     */
    @ApiModelProperty(value = "通信方式 1-websocket 2-http 3-mqtt", required = true)
    private Integer type;

    /**
     * url地址
     */
    @ApiModelProperty(value = "url地址", required = true)
    private String url;

    /**
     * 请求参数Value
     */
    @ApiModelProperty(value = "请求参数Value")
    private String requestValue;

    /**
     * 响应参数Value
     */
    @ApiModelProperty(value = "响应参数Value")
    private String responseValue;

}
