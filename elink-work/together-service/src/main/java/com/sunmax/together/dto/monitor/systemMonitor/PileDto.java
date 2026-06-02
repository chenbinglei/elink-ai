package com.sunmax.together.dto.monitor.systemMonitor;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "PileDto", description = "电桩数据返回实体类")
public class PileDto {

    /**
     * 主键id
     */
    @ApiModelProperty(value = "主键id")
    private String id;

    /**
     * 设备名称
     */
    @ApiModelProperty(value = "设备名称")
    private String deviceName;

    /**
     * 设备序列号
     */
    @ApiModelProperty(value = "设备序列号")
    private String deviceNumber;

    /**
     * 通讯状态 0-未注册 1-在线 2-维护 3-故障 88-离线
     */
    @ApiModelProperty(value = "通讯状态 0-未注册 1-在线 2-维护 3-故障 88-离线")
    private Integer txStatus = 0;

    /**
     * 电桩工作状态 -1-未知 1-在线 2-维护 3-故障 88-离线
     */
    @ApiModelProperty(value = "电桩工作状态 -1-未知 1-在线 2-维护 3-故障 88-离线")
    private Integer workStatus = -1;

    /**
     * 额定功率
     */
    @ApiModelProperty(value = "额定功率")
    private Double ratedPower;

    /**
     * 设备厂家名称
     */
    @ApiModelProperty(value = "设备厂家名称")
    private String manufacturerName;

    /**
     * 电桩类型 28-交流 29-直流 30-V2G
     */
    @ApiModelProperty("电桩类型 28-交流 29-直流 30-V2G")
    private String typeId;

    /**
     * 设备型号
     */
    @ApiModelProperty(value = "设备型号")
    private String model;

    /**
     * 今日充电量
     */
    @ApiModelProperty(value = "今日充电量")
    private Double dayChargeQt = 0.0;

    /**
     * 今日放电量
     */
    @ApiModelProperty(value = "今日放电量")
    private Double dayV2gQt = 0.0;

    /**
     * 有功功率
     */
    @ApiModelProperty(value = "有功功率")
    private Double activePower;

    /**
     * 内部温度(°C)
     */
    @ApiModelProperty(value = "内部温度(°C)")
    private Double innerTemperature;

}
