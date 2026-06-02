package com.sunmax.together.dto.custom;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "DCADDeviceDto", description = "直流配电柜数据返回实体类")
public class DCADDeviceDto {

    /**
     * 电压(V)
     */
    @ApiModelProperty(value = "电压(V)")
    private Double voltage;

    /**
     * 电流(A)
     */
    @ApiModelProperty(value = "电流(A)")
    private Double current;

    /**
     * 功率(kW)
     */
    @ApiModelProperty(value = "功率(kW)")
    private Double power;

}
