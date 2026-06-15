package com.sunmax.together.dto.custom;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "直流配电柜数据返回实体类")
public class DCADDeviceDto {

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
