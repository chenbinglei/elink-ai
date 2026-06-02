package com.sunmax.common.dto.device;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "ModelFunctionListDto", description = "模型标准功能列表返回实体类")
public class ModelFunctionListDto {

    /**
     * 关联主键id
     */
    @ApiModelProperty(value = "关联主键id")
    private String id;

    /**
     * 功能id
     */
    @ApiModelProperty(value = "功能id")
    private String functionId;

    /**
     * 功能名称
     */
    @ApiModelProperty(value = "功能名称")
    private String functionName;

    /**
     * 功能标识
     */
    @ApiModelProperty(value = "功能标识")
    private String functionLogo;

    /**
     * 功能类型 1-遥测 2-遥信 3-遥脉 4-遥控 5-遥调
     */
    @ApiModelProperty(value = "功能类型 1-遥测 2-遥信 3-遥脉 4-遥控 5-遥调")
    private Integer functionType;

    /**
     * 数据类型 1-int32(整数) 2-int64(长整数型) 3-float(单精度浮点型) 4-double(双精度浮点型) 5-enum(枚举) 6-bool(布尔) 7-string(字符串) 8-array(数组) 9-date(时间)
     */
    @ApiModelProperty(value = "数据类型 1-int32(整数) 2-int64(长整数型) 3-float(单精度浮点型) 4-double(双精度浮点型) 5-enum(枚举) 6-bool(布尔) 7-string(字符串) 8-array(数组) 9-date(时间)", required = true)
    private Integer dataType;

    /**
     * 取值范围
     */
    @ApiModelProperty(value = "取值范围")
    private String valueRange;

    /**
     * 精度 1-1 2-0.1 3-0.01 4-0.001 5-0.0001 6-0.00001
     */
    @ApiModelProperty(value = "精度 1-1 2-0.1 3-0.01 4-0.001 5-0.0001 6-0.00001")
    private Integer accuracy;

    /**
     * 单位
     */
    @ApiModelProperty(value = "单位")
    private String unit;

    /**
     * 数据对象 {key:value} 字符串直接存长度
     */
    @ApiModelProperty(value = "数据对象 {key:value} 字符串直接存长度")
    private String dataObject;

    /**
     * 字段编码
     */
    @ApiModelProperty(value = "字段编码")
    private String fieldCode;

    /**
     * 字段类型
     */
    @ApiModelProperty(value = "字段类型 1-充电桩类型 2-充电枪类型")
    private Integer fieldType;

    /**
     * 序列号
     */
    @ApiModelProperty(value = "序列号")
    private Integer serialNum;

    /**
     * 功能点描述
     */
    @ApiModelProperty(value = "功能点描述")
    private String functionDesc;

}
