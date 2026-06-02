package com.sunmax.together.dto.custom;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "SeCabinetDeviceDto", description = "储能柜数据返回实体类")
public class SeCabinetDeviceDto {

    /**
     * 储能DC/DC-母线电压(V)
     */
    @ApiModelProperty(value = "储能DC/DC-母线电压(V)")
    private Double busVoltage;

    /**
     * 储能DC/DC-母线电流(A)
     */
    @ApiModelProperty(value = "储能DC/DC-母线电流(A)")
    private Double busCurrent;

    /**
     * 储能DC/DC-功率(kW)
     */
    @ApiModelProperty(value = "储能DC/DC-功率(kW)")
    private Double power;

    /**
     * 电池蔟-运行模式 0-静置 1-放电 2-充电
     */
    @ApiModelProperty(value = "电池蔟-运行模式 0-静置 1-放电 2-充电")
    private Integer runMode;

    /**
     * 运行模式名称
     */
    @ApiModelProperty(value = "电池蔟-运行模式名称")
    private String runModeName;

    /**
     * 电池蔟-SOC(%)
     */
    @ApiModelProperty(value = "电池蔟-SOC(%)")
    private Double soc;

    /**
     * 电池蔟-电池总电压(V)
     */
    @ApiModelProperty(value = "电池蔟-电池总电压(V)")
    private Double batteryVoltage;

    /**
     * 电池蔟-电池总电流(A)
     */
    @ApiModelProperty(value = "电池蔟-电池总电流(A)")
    private Double batteryCurrent;

    /**
     * 电池蔟-最高单体电压(V)
     */
    @ApiModelProperty(value = "电池蔟-最高单体电压(V)")
    private Double maxVoltage;

    /**
     * 电池蔟-最低单体电压(V)
     */
    @ApiModelProperty(value = "电池蔟-最低单体电压(V)")
    private Double minVoltage;

    /**
     * 电池蔟-最高单体温度(℃)
     */
    @ApiModelProperty(value = "电池蔟-最高单体温度(℃)")
    private Double maxTemp;

    /**
     * 电池蔟-最低单体温度(℃)
     */
    @ApiModelProperty(value = "电池蔟-最低单体温度(℃)")
    private Double minTemp;

}
