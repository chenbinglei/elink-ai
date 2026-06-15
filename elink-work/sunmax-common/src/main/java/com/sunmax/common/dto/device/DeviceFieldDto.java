package com.sunmax.common.dto.device;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 设备字段返回实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "设备字段返回实体类")
public class DeviceFieldDto {

    /**
     * 设备id
     */
    @Schema(description = "设备id")
    private String deviceId;

    /**
     * 数据类型 1-int32(整数) 2-int64(长整数型) 3-float(单精度浮点型) 4-double(双精度浮点型) 5-enum(枚举) 6-bool(布尔) 7-string(字符串) 8-array(数组) 9-date(时间)
     */
    @Schema(description = "数据类型 1-int32(整数) 2-int64(长整数型) 3-float(单精度浮点型) 4-double(双精度浮点型) 5-enum(枚举) 6-bool(布尔) 7-string(字符串) 8-array(数组) 9-date(时间)")
    private Integer dataType;

    /**
     * 功能点标识
     */
    @Schema(description = "功能点标识")
    private String functionLogo;

    /**
     * 数据对象 {key:value} 字符串直接存长度
     */
    @Schema(description = "数据对象 {key:value} 字符串直接存长度")
    private String dataObject;

    /**
     * 取值范围
     */
    @Schema(description = "取值范围")
    private String valueRange;

    /**
     * 表名称
     */
    @Schema(description = "表名称")
    private String tableName;

    /**
     * 字段名称
     */
    @Schema(description = "字段名称")
    private String fieldName;

}
