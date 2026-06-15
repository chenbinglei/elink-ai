package com.sunmax.common.dto.device;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "标准功能信息数据返回实体类")
public class FunctionDataDto {

    /**
     * 功能属性id
     */
    @Schema(description = "功能属性id")
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
     * 功能值
     */
    @Schema(description = "功能值")
    private Object value;

    /**
     * 数据对象
     */
    @Schema(description = "数据对象")
    private Object dataObject;

    /**
     * 时间
     */
    @Schema(description = "时间")
    private String dateTime;

    /**
     * 单位
     */
    @Schema(description = "单位")
    private String unit;
}
