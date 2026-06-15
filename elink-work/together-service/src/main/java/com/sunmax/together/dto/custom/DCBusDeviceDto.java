package com.sunmax.together.dto.custom;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "直流母线柜数据返回实体类")
public class DCBusDeviceDto {

    /**
     * 运行模式 0-整流 1-逆变
     */
    @Schema(description = "运行模式 0-整流 1-逆变")
    private Integer runMode;

    /**
     * 运行模式名称
     */
    @Schema(description = "运行模式名称")
    private String runModeName;

    /**
     * 电压(V)
     */
    @Schema(description = "电压(V)")
    private Double voltage;

    /**
     * 电流(A)
     */
    @Schema(description = "电流(A)")
    private Double current;

    /**
     * 功率(kW)
     */
    @Schema(description = "功率(kW)")
    private Double power;

}
