package com.sunmax.together.dto.monitor.centralMonitorOld;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "PileDeviceListDto", description = "电桩设备列表返回实体类")
public class PileDeviceListDto {

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
     * 额定功率
     */
    @ApiModelProperty(value = "额定功率")
    private Double power;

    /**
     * 实时功率
     */
    @ApiModelProperty(value = "实时功率")
    private Double realPower;

    /**
     * 今日充电量
     */
    @ApiModelProperty(value = "今日充电量")
    private Double dayChargeQt;
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
     * 生产厂家
     */
    @ApiModelProperty(value = "生产厂家")
    private String manufacturerName;

    /**
     * 通讯状态 0-未注册 1-在线 2-维护 3-故障 88-离线
     */
    @ApiModelProperty(value = "通讯状态 0-未注册 1-在线 2-维护 3-故障 88-离线")
    private Integer txStatus = 0;

    /**
     * 电桩工作状态 -1-未知 1-在线 2-维护 3-故障 88-离线
     */
    @ApiModelProperty(value = "电桩工作状态 -1-未知 1-在线 2-维护 3-故障 88-离线")
    private Integer workStatus;
}
