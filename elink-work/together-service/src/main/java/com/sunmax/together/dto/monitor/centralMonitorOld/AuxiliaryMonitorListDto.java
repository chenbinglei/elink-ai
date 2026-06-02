package com.sunmax.together.dto.monitor.centralMonitorOld;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "AuxiliaryMonitorListDto", description = "辅助设备监控信息返回实体类")
public class AuxiliaryMonitorListDto {

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
     * 柜内温度
     */
    @ApiModelProperty(value = "柜内温度")
    private Double cabinetTemp;

    /**
     * 柜内湿度
     */
    @ApiModelProperty(value = "柜内湿度")
    private Double cabinetHumidity;

    /**
     * 制冷状态 0：停止，1：开启
     */
    @ApiModelProperty(value = "制冷状态 0：停止，1：开启")
    private Integer coolingState;

    /**
     * 加热器状态 0：停止，1：开启
     */
    @ApiModelProperty(value = "加热器状态 0：停止，1：开启")
    private Integer heatingState;

    /**
     * 内风机状态 0：停止，1：开启
     */
    @ApiModelProperty(value = "内风机状态 0：停止，1：开启")
    private Integer internalfanState;

    /**
     * 外风机状态 0：停止，1：开启
     */
    @ApiModelProperty(value = "外风机状态 0：停止，1：开启")
    private Integer externalfanState;
}
