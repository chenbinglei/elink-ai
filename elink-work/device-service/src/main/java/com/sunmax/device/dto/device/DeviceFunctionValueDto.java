package com.sunmax.device.dto.device;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "设备功能值返回实体类")
public class DeviceFunctionValueDto {

    /**
     * 设备id
     */
    @Schema(description = "设备id")
    private String deviceId;

    /**
     * 功能点标识
     */
    @Schema(description = "功能点标识")
    private String functionLogo;

    /**
     * 数据类型 1-int32(整数) 2-int64(长整数型) 3-float(单精度浮点型) 4-double(双精度浮点型) 5-enum(枚举) 6-bool(布尔) 7-string(字符串) 8-array(数组) 9-date(时间)
     */
    @Schema(description = "数据类型 1-int32(整数) 2-int64(长整数型) 3-float(单精度浮点型) 4-double(双精度浮点型) 5-enum(枚举) 6-bool(布尔) 7-string(字符串) 8-array(数组) 9-date(时间)")
    private Integer dataType;

    /**
     * 日期列表
     */
    @Schema(description = "日期列表")
    private List<String> dateList;

    /**
     * 数据列表
     */
    @Schema(description = "数据列表")
    private List<Object> valueList;

}
