package com.sunmax.common.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "实时公共缓存实体类")
public class RealDataModel {

    /**
     * 数据类型 1-int32(整数) 2-int64(长整数型) 3-float(单精度浮点型) 4-double(双精度浮点型) 5-enum(枚举) 6-bool(布尔) 7-string(字符串) 8-array(数组) 9-date(时间)
     */
    @Schema(description = "数据类型 1-int32(整数) 2-int64(长整数型) 3-float(单精度浮点型) 4-double(双精度浮点型) 5-enum(枚举) 6-bool(布尔) 7-string(字符串) 8-array(数组) 9-date(时间)")
    private Integer dataType;

    /**
     * 数据值
     */
    @Schema(description = "数据值")
    private Object dataValue;

    /**
     * 时间
     */
    @Schema(description = "时间")
    private String dateTime;

    /**
     * 字段编码(目前只针对于充电桩和充电枪)
     */
    @Schema(description = "字段编码")
    private String fieldCode;

}
