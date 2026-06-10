package com.sunmax.common.dto.device;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "设备扩展属性返回实体类")
public class DeviceReaDto {

    /**
     * 扩展属性id
     */
    @Schema(description = "扩展属性id")
    private String reaId;

    /**
     * 扩展属性名称
     */
    @Schema(description = "扩展属性名称")
    private String reaName;

    /**
     * 字段名称
     */
    @Schema(description = "字段名称")
    private String fieldName;

    /**
     * 扩展属性类型 1-数值 2-文字 3-选项 4-位置 5-开关 6-时间
     */
    @Schema(description = "扩展属性类型 1-数值 2-文字 3-选项 4-位置 5-开关 6-时间")
    private Integer reaType;

    /**
     * 读写类型 1-只读 2-读写
     */
    @Schema(description = "读写类型 1-只读 2-读写")
    private Integer readWriteType;

    /**
     * 是否必填 true-是 false-否
     */
    @Schema(description = "是否必填 true-是 false-否")
    private Boolean required;

    /**
     * 数据值
     */
    @Schema(description = "数据值")
    private Object value;

    /**
     * 单位
     */
    @Schema(description = "单位")
    private String unit;

    /**
     * 额外值
     */
    @Schema(description = "额外值")
    private String extraValue;

}
