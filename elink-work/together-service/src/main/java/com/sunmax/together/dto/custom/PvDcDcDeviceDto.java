package com.sunmax.together.dto.custom;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "PvDcDcDeviceDto", description = "光伏DC/DC数据返回实体类")
public class PvDcDcDeviceDto {

    /**
     * 母线电压(V)
     */
    @ApiModelProperty(value = "母线电压(V)")
    private Double busVoltage;

    /**
     * 母线电流(A)
     */
    @ApiModelProperty(value = "母线电流(A)")
    private Double busCurrent;

    /**
     * MPPT电压
     */
    @ApiModelProperty(value = "MPTT电压(V)")
    private Double mpptVoltage;

    /**
     * 功率(kW)
     */
    @ApiModelProperty(value = "功率(kW)")
    private Double power;

    /**
     * 当日发电量(kWh)
     */
    @ApiModelProperty(value = "当日发电量(kWh)")
    private Double dayQt;

}
