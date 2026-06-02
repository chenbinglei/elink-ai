package com.sunmax.common.dto.data;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 节点差值历史数据实体类
 */
@Data
@ApiModel(value = "NodeDifHistoryDto", description = "节点差值历史数据实体类")
public class NodeDifHistoryDto {

    /**
     * 功能标识
     */
    @ApiModelProperty(value = "功能标识")
    private String functionLogo;

    /**
     * 数据类型 1-int32(整数) 2-int64(长整数型) 3-float(单精度浮点型) 4-double(双精度浮点型) 5-enum(枚举) 6-bool(布尔) 7-string(字符串) 8-array(数组) 9-date(时间)
     */
    @ApiModelProperty(value = "数据类型 1-int32(整数) 2-int64(长整数型) 3-float(单精度浮点型) 4-double(双精度浮点型) 5-enum(枚举) 6-bool(布尔) 7-string(字符串) 8-array(数组) 9-date(时间)")
    private Integer dataType;

    /**
     * 字段名称
     */
    @ApiModelProperty(value = "字段名称")
    private String fieldName;

    /**
     * last数据值
     */
    @ApiModelProperty(value = "last数据值")
    private Object lastDataValue;

    /**
     * first数据值
     */
    @ApiModelProperty(value = "first数据值")
    private Object firstDataValue;

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
