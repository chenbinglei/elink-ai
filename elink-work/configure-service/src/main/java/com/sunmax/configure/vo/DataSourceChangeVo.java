package com.sunmax.configure.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "DataSourceChangeVo", description = "数据源编辑参数实体类")
public class DataSourceChangeVo {

    /**
     * 主键id
     */
    @ApiModelProperty(value = "主键id")
    private String id;

    /**
     * 数据源名称
     */
    @ApiModelProperty(value = "数据源名称", required = true)
    private String name;

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
     * 描述
     */
    @ApiModelProperty(value = "描述")
    private String description;

    /**
     * 动态字段
     */
    @ApiModelProperty(value = "动态字段", required = true)
    private String dynamicField;

    /**
     * 请求参数Key
     */
    @ApiModelProperty(value = "请求参数Key")
    private String requestKey;

    /**
     * 用户id
     */
    @ApiModelProperty(value = "用户id", required = true)
    private String userId;

}
