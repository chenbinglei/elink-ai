package com.sunmax.common.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "EventModel", description = "模型事件缓存实体")
public class EventModel {

    /**
     * 模型id
     */
    @ApiModelProperty(value = "模型id")
    private String modelId;

    /**
     * 事件id
     */
    @ApiModelProperty(value = "事件id")
    private String eventId;

    /**
     * 计算类型 1-值运算 2-位运算
     */
    @ApiModelProperty(value = "计算类型 1-值运算 2-位运算")
    private Integer calculateType;

    /**
     * 多个功能点标识
     */
    @ApiModelProperty(value = "多个功能点标识")
    private String functionLogos;

    /**
     * 存储数据
     */
    @ApiModelProperty(value = "存储数据")
    private String storeData;

}
