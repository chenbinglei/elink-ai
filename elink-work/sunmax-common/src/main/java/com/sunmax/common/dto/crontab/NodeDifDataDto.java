package com.sunmax.common.dto.crontab;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 节点差值历史数据实体类
 */
@Data
@Schema(description = "节点差值历史数据实体类")
public class NodeDifDataDto {

    /**
     * 变量编码
     */
    private String varCode;

    /**
     * last数据值
     */
    @Schema(description = "last数据值")
    private Double lastDataValue;

    /**
     * first数据值
     */
    @Schema(description = "first数据值")
    private Double firstDataValue;

    /**
     * last时间
     */
    @Schema(description = "last时间")
    private String lastDateTime;

    /**
     * first时间
     */
    @Schema(description = "first时间")
    private String firstDateTime;
}
