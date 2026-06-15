package com.sunmax.together.dto.custom;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "光伏DC/DC数据返回实体类")
public class PvDcDcDeviceDto {

    /**
     * 母线电压(V)
     */
    @Schema(description = "母线电压(V)")
    private Double busVoltage;

    /**
     * 母线电流(A)
     */
    @Schema(description = "母线电流(A)")
    private Double busCurrent;

    /**
     * MPPT电压
     */
    @Schema(description = "MPTT电压(V)")
    private Double mpptVoltage;

    /**
     * 功率(kW)
     */
    @Schema(description = "功率(kW)")
    private Double power;

    /**
     * 当日发电量(kWh)
     */
    @Schema(description = "当日发电量(kWh)")
    private Double dayQt;

}
