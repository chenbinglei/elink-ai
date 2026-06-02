package com.sunmax.configure.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@ApiModel(value = "DataSourceListDto", description = "数据源列表返回实体类")
public class DataSourceListDto {

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
     * 创建时间
     */
    @ApiModelProperty("创建时间")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    /**
     * 修改时间
     */
    @ApiModelProperty("修改时间")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

}
