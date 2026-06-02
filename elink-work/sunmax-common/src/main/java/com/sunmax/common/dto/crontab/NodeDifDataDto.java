package com.sunmax.common.dto.crontab;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 节点差值历史数据实体类
 */
@Data
@ApiModel(value = "NodeDifDataDto", description = "节点差值历史数据实体类")
public class NodeDifDataDto {

    /**
     * 变量编码
     */
    private String varCode;

    /**
     * last数据值
     */
    @ApiModelProperty(value = "last数据值")
    private Double lastDataValue;

    /**
     * first数据值
     */
    @ApiModelProperty(value = "first数据值")
    private Double firstDataValue;

    /**
     * last时间
     */
    @ApiModelProperty(value = "last时间")
    private String lastDateTime;

    /**
     * first时间
     */
    @ApiModelProperty(value = "first时间")
    private String firstDateTime;
}
