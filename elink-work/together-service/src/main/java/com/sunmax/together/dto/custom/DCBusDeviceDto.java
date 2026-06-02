package com.sunmax.together.dto.custom;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "DCBusDeviceDto", description = "直流母线柜数据返回实体类")
public class DCBusDeviceDto {

    /**
     * 运行模式 0-整流 1-逆变
     */
    @ApiModelProperty(value = "运行模式 0-整流 1-逆变")
    private Integer runMode;

    /**
     * 运行模式名称
     */
    @ApiModelProperty(value = "运行模式名称")
    private String runModeName;

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
