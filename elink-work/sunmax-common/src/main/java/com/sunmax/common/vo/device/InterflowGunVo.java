package com.sunmax.common.vo.device;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "InterflowGunVo", description = "互联互通电枪编辑参数")
public class InterflowGunVo {

    /**
     * 充电枪编号
     */
    @ApiModelProperty(value = "充电枪编号")
    private String gunCode;

    /**
     * 充电枪名称
     */
    @ApiModelProperty(value = "充电枪名称")
    private String gunName;

    /**
     * 充电枪类型
     * 1：家用插座（模式 2）
     * 2：交流接口插座（模式3， 连接方式 B ）
     * 3：交流接口插头（带枪线，模式 3，连接方式C）
     * 4：直流接口枪头（带枪线，模式 4）
     */
    @ApiModelProperty(value = "充电枪类型 1-家用插座 2-交流接口插座 3-交流接口插头 4-直流接口枪头")
    private Integer type;

    /**
     * 外观
     */
    @ApiModelProperty(value = "外观")
    private String appearance;

    /**
     * 防护等级
     */
    @ApiModelProperty(value = "防护等级")
    private String ipGrade;

    /**
     * 额定电流 单位A
     */
    @ApiModelProperty(value = "额定电流 单位A")
    private Integer ratedCurrent;

    /**
     * 额定功率 单位kW
     */
    @ApiModelProperty(value = "额定功率 单位kW")
    private Double ratedPower;

    /**
     * 额定电压上限 单位V
     */
    @ApiModelProperty(value = "额定电压上限 单位V")
    private Integer voltageUpperLimits;

    /**
     * 额定电压下限 单位V
     */
    @ApiModelProperty(value = "额定电压下限 单位V")
    private Integer voltageLowerLimits;

    /**
     * 车位号
     */
    @ApiModelProperty(value = "车位号")
    private String parkNo;

    /**
     * 国家标准 1-2011 2-2015
     */
    @ApiModelProperty(value = "国家标准 1-2011 2-2015")
    private Integer nationalStandard;

    /**
     * 关联设备id
     */
    @ApiModelProperty(value = "关联设备id")
    private String deviceId;
}
