package com.sunmax.common.dto.data;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "设备历史数据实体类")
public class DeviceHistoryDto {

    /**
     * 功能标识
     */
    @Schema(description = "功能标识")
    private String functionLogo;

    /**
     * 数据类型 1-int32(整数) 2-int64(长整数型) 3-float(单精度浮点型) 4-double(双精度浮点型) 5-enum(枚举) 6-bool(布尔) 7-string(字符串) 8-array(数组) 9-date(时间)
     */
    @Schema(description = "数据类型 1-int32(整数) 2-int64(长整数型) 3-float(单精度浮点型) 4-double(双精度浮点型) 5-enum(枚举) 6-bool(布尔) 7-string(字符串) 8-array(数组) 9-date(时间)")
    private Integer dataType;

    /**
     * 字段名称
     */
    @Schema(description = "字段名称")
    private String fieldName;

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

}
