package com.sunmax.data.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "设备数据存储实体类")
public class DeviceDataVo {

    /**
     * 字段名称
     */
    @Schema(description = "字段名称")
    private String fieldName;

    /**
     * 字段类型
     */
    @Schema(description = "字段类型")
    private String fieldType;

    /**
     * 时间
     */
    @Schema(description = "时间")
    private String dateTime;

    /**
     * 数据值
     */
    @Schema(description = "数据值")
    private Object dataValue;

}
