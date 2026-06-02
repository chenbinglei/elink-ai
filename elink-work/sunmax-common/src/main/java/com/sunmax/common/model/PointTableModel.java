package com.sunmax.common.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel(value = "PointTableModel", description = "设备点表缓存实体类")
public class PointTableModel {

    /**
     * 数据类型 1-int32(整数) 2-int64(长整数型) 3-float(单精度浮点型) 4-double(双精度浮点型) 5-enum(枚举) 6-bool(布尔) 7-string(字符串) 8-array(数组) 9-date(时间)
     */
    @ApiModelProperty(value = "数据类型 1-int32(整数) 2-int64(长整数型) 3-float(单精度浮点型) 4-double(双精度浮点型) 5-enum(枚举) 6-bool(布尔) 7-string(字符串) 8-array(数组) 9-date(时间)")
    private Integer dataType;

    /**
     * 数据值
     */
    @ApiModelProperty(value = "数据值")
    private Object dataValue;

    /**
     * 时间
     */
    @ApiModelProperty(value = "时间")
    private String dateTime;

    /**
     * 数据点号
     */
    @ApiModelProperty(value = "数据点号")
    private Long dataId;

    /**
     * 系数
     */
    @ApiModelProperty(value = "系数")
    private Float coefficient;

    /**
     * 偏移量
     */
    @ApiModelProperty(value = "偏移量")
    private Integer offset;

    /**
     * 字段编码(目前只针对于充电桩和充电枪)
     */
    @ApiModelProperty(value = "字段编码")
    private String fieldCode;

    /**
     * 精度 1-1 2-0.1 3-0.01 4-0.001 5-0.0001 6-0.00001
     */
    @ApiModelProperty(value = "精度 1-1 2-0.1 3-0.01 4-0.001 5-0.0001 6-0.00001")
    private Integer accuracy;

    /**
     * 数据对象 {key:value} 字符串直接存长度
     */
    @ApiModelProperty(value = "数据对象 {key:value} 字符串直接存长度")
    private String dataObject;

    /**
     * 取值范围
     */
    @ApiModelProperty(value = "取值范围")
    private String valueRange;

}
