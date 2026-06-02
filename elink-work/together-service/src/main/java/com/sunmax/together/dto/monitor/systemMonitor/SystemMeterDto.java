package com.sunmax.together.dto.monitor.systemMonitor;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.compress.utils.Lists;

import java.util.List;

@Data
@ApiModel(value = "SystemMeterDto", description = "系统电表返回实体类")
public class SystemMeterDto {

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
     * 通信状态 0-未注册 1-在线 2-维护 3-故障 88-离线
     */
    @ApiModelProperty(value = "通信状态 0-未注册 1-在线 2-维护 3-故障 88-离线")
    private Integer txStatus = 0;

    /**
     * 设备型号
     */
    @ApiModelProperty(value = "设备型号")
    private String model;

    /**
     * 设备厂家名称
     */
    @ApiModelProperty(value = "设备厂家名称")
    private String manufacturerName;

    /**
     * 本月平均功率因数
     */
    @ApiModelProperty(value = "本月平均功率因数")
    private Double averagePowerFactor;

    /**
     * 三相电压不平衡度
     */
    @ApiModelProperty(value = "三相电压不平衡度")
    private Double threePhaseVoltImbalance;

    /**
     * 三相电流不平衡度
     */
    @ApiModelProperty(value = "三相电流不平衡度")
    private Double threePhaseCurImbalance;

    /**
     * 功率平衡度
     */
    @ApiModelProperty(value = "功率平衡度")
    private Double powerBalance;

    /**
     * 遥测数据列表
     */
    @ApiModelProperty(value = "遥测数据列表")
    private List<TelemetryDataDto> telemetryDataList = Lists.newArrayList();

}
