package com.sunmax.device.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 网关子设备功能点返回实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "网关子设备功能点返回实体类")
public class SubDeviceFunctionDto {

    /**
     * 设备id
     */
    @Schema(description = "设备id")
    private String deviceId;

    /**
     * 设备名称
     */
    @Schema(description = "设备名称")
    private String deviceName;

    /**
     * 功能点id
     */
    @Schema(description = "功能点id")
    private String functionId;

    /**
     * 功能点名称
     */
    @Schema(description = "功能点名称")
    private String functionName;

    /**
     * 功能类型 1-遥测 2-遥信 3-遥脉 4-遥控 5-遥调
     */
    @Schema(description = "功能类型 1-遥测 2-遥信 3-遥脉 4-遥控 5-遥调")
    private Integer functionType;

    /**
     * 序列号
     */
    @Schema(description = "序列号")
    private Integer serialNum;

    /**
     * 数据类型 1-int32(整数) 2-int64(长整数型) 3-float(单精度浮点型) 4-double(双精度浮点型) 5-enum(枚举) 6-bool(布尔) 7-string(字符串) 8-array(数组) 9-date(时间)
     */
    @Schema(description = "数据类型 1-int32(整数) 2-int64(长整数型) 3-float(单精度浮点型) 4-double(双精度浮点型) 5-enum(枚举) 6-bool(布尔) 7-string(字符串) 8-array(数组) 9-date(时间)")
    private Integer dataType;

}
