package com.sunmax.common.vo.device;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "互联互通电枪编辑参数")
public class InterflowGunVo {

    /**
     * 充电枪编号
     */
    @Schema(description = "充电枪编号")
    private String gunCode;

    /**
     * 充电枪名称
     */
    @Schema(description = "充电枪名称")
    private String gunName;

    /**
     * 充电枪类型
     * 1：家用插座（模式 2）
     * 2：交流接口插座（模式3， 连接方式 B ）
     * 3：交流接口插头（带枪线，模式 3，连接方式C）
     * 4：直流接口枪头（带枪线，模式 4）
     */
    @Schema(description = "充电枪类型 1-家用插座 2-交流接口插座 3-交流接口插头 4-直流接口枪头")
    private Integer type;

    /**
     * 外观
     */
    @Schema(description = "外观")
    private String appearance;

    /**
     * 防护等级
     */
    @Schema(description = "防护等级")
    private String ipGrade;

    /**
     * 额定电流 单位A
     */
    @Schema(description = "额定电流 单位A")
    private Integer ratedCurrent;

    /**
     * 额定功率 单位kW
     */
    @Schema(description = "额定功率 单位kW")
    private Double ratedPower;

    /**
     * 额定电压上限 单位V
     */
    @Schema(description = "额定电压上限 单位V")
    private Integer voltageUpperLimits;

    /**
     * 额定电压下限 单位V
     */
    @Schema(description = "额定电压下限 单位V")
    private Integer voltageLowerLimits;

    /**
     * 车位号
     */
    @Schema(description = "车位号")
    private String parkNo;

    /**
     * 国家标准 1-2011 2-2015
     */
    @Schema(description = "国家标准 1-2011 2-2015")
    private Integer nationalStandard;

    /**
     * 关联设备id
     */
    @Schema(description = "关联设备id")
    private String deviceId;
}
