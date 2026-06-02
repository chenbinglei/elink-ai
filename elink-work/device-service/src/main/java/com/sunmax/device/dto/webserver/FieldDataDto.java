package com.sunmax.device.dto.webserver;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "FieldDataDto", description = "字段数据返回实体类")
public class FieldDataDto {

    /**
     * 字段标识
     */
    @ApiModelProperty(value = "字段标识")
    private String fieldCode;

    /**
     * 字段名称
     */
    @ApiModelProperty(value = "字段名称")
    private String fieldName;

    /**
     * 字段值
     */
    @ApiModelProperty(value = "字段值")
    private Object fieldValue;

    /**
     * 字段类型 1-int32(整数) 2-int64(长整数型) 3-float(单精度浮点型) 4-double(双精度浮点型) 5-enum(枚举) 6-bool(布尔) 7-string(字符串) 8-array(数组) 9-date(时间)
     */
    @ApiModelProperty(value = "字段类型 1-int32(整数) 2-int64(长整数型) 3-float(单精度浮点型) 4-double(双精度浮点型) 5-enum(枚举) 6-bool(布尔) 7-string(字符串) 8-array(数组) 9-date(时间)")
    private Integer fieldType;

    /**
     * 字段描述
     */
    @ApiModelProperty(value = "字段描述")
    private String fieldDesc;

}
