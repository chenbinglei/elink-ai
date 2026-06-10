package com.sunmax.together.dto.custom;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "电站概览返回实体类")
public class SiteOverviewDto {

    /**
     * 直流母线功率容量(kW)
     */
    @Schema(description = "直流母线功率容量(kW)")
    private Double dcBusCap;

    /**
     * 直流母线电压等级(Vdc)
     */
    @Schema(description = "直流母线电压等级(Vdc)")
    private Double dcBusVoltage;

    /**
     * 光伏额定容量(kWp)
     */
    @Schema(description = "光伏额定容量(kWp)")
    private Double pvCap;

    /**
     * 储能PCS额定功率(kW)
     */
    @Schema(description = "储能PCS额定功率(kW)")
    private Double pcsPower;

    /**
     * 储能电池蔟额定容量(kWh)
     */
    @Schema(description = "储能电池蔟额定容量(kWh)")
    private Double batteryCap;

    /**
     * 直流注塑机额定容量(kWp)
     */
    @Schema(description = "直流注塑机额定容量(kWp)")
    private Double dcInjectorCap;

}
