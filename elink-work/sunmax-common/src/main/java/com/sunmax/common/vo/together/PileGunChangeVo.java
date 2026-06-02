package com.sunmax.common.vo.together;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "PileGunChangeVo", description = "电桩枪编辑参数实体类")
public class PileGunChangeVo {

    /**
     * 充电枪id
     */
    @ApiModelProperty(value = "充电枪id")
    private String id;

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
    @ApiModelProperty(value = "充电枪类型 1-家用插座（模式 2） 2-交流接口插座（模式3， 连接方式 B ） 3-交流接口插头（带枪线，模式 3，连接方式C） 4-直流接口枪头（带枪线，模式 4）")
    private Integer type;

    /**
     * 浙江省充电设备接口唯一码
     */
    @ApiModelProperty(value = "浙江省充电设备接口唯一码")
    private String connectorUniqueId;

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
     * 国家标准 1:2011 2:2015
     */
    @ApiModelProperty(value = "国家标准 1-2011 2-2015")
    private Integer nationalStandard;

    /**
     * 二维码解析地址清单
     */
    @ApiModelProperty(value = "二维码解析地址清单")
    private String qrCodes;

    /**
     * 辅助电源 1-12V 2-24V 3-兼容12V和24V
     */
    @ApiModelProperty(value = "辅助电源 1-12V 2-24V 3-兼容12V和24V")
    private Integer auxPower;

}
