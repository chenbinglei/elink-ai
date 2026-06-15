package com.sunmax.common.dto.device;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "模型标准功能列表返回实体类")
public class ModelFunctionListDto {

    /**
     * 关联主键id
     */
    @Schema(description = "关联主键id")
    private String id;

    /**
     * 功能id
     */
    @Schema(description = "功能id")
    private String functionId;

    /**
     * 功能名称
     */
    @Schema(description = "功能名称")
    private String functionName;

    /**
     * 功能标识
     */
    @Schema(description = "功能标识")
    private String functionLogo;

    /**
     * 功能类型 1-遥测 2-遥信 3-遥脉 4-遥控 5-遥调
     */
    @Schema(description = "功能类型 1-遥测 2-遥信 3-遥脉 4-遥控 5-遥调")
    private Integer functionType;

    /**
     * 数据类型 1-int32(整数) 2-int64(长整数型) 3-float(单精度浮点型) 4-double(双精度浮点型) 5-enum(枚举) 6-bool(布尔) 7-string(字符串) 8-array(数组) 9-date(时间)
     */
    @Schema(description = "数据类型 1-int32(整数) 2-int64(长整数型) 3-float(单精度浮点型) 4-double(双精度浮点型) 5-enum(枚举) 6-bool(布尔) 7-string(字符串) 8-array(数组) 9-date(时间)")
    private Integer dataType;

    /**
     * 取值范围
     */
    @Schema(description = "取值范围")
    private String valueRange;

    /**
     * 精度 1-1 2-0.1 3-0.01 4-0.001 5-0.0001 6-0.00001
     */
    @Schema(description = "精度 1-1 2-0.1 3-0.01 4-0.001 5-0.0001 6-0.00001")
    private Integer accuracy;

    /**
     * 单位
     */
    @Schema(description = "单位")
    private String unit;

    /**
     * 数据对象 {key:value} 字符串直接存长度
     */
    @Schema(description = "数据对象 {key:value} 字符串直接存长度")
    private String dataObject;

    /**
     * 字段编码
     */
    @Schema(description = "字段编码")
    private String fieldCode;

    /**
     * 字段类型
     */
    @Schema(description = "字段类型 1-充电桩类型 2-充电枪类型")
    private Integer fieldType;

    /**
     * 序列号
     */
    @Schema(description = "序列号")
    private Integer serialNum;

    /**
     * 功能点描述
     */
    @Schema(description = "功能点描述")
    private String functionDesc;

}
