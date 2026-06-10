package com.sunmax.common.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "模型事件缓存实体")
public class EventModel {

    /**
     * 模型id
     */
    @Schema(description = "模型id")
    private String modelId;

    /**
     * 事件id
     */
    @Schema(description = "事件id")
    private String eventId;

    /**
     * 计算类型 1-值运算 2-位运算
     */
    @Schema(description = "计算类型 1-值运算 2-位运算")
    private Integer calculateType;

    /**
     * 多个功能点标识
     */
    @Schema(description = "多个功能点标识")
    private String functionLogos;

    /**
     * 存储数据
     */
    @Schema(description = "存储数据")
    private String storeData;

}
