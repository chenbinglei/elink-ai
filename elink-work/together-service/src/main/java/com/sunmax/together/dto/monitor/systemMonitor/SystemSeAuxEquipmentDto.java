package com.sunmax.together.dto.monitor.systemMonitor;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.compress.utils.Lists;

import java.util.List;

@Data
@ApiModel(value = "SystemSeAuxEquipmentDto", description = "储能辅助设备数据返回实体类")
public class SystemSeAuxEquipmentDto {

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
     * 制冷状态 0-停止 1-开启
     */
    @ApiModelProperty(value = "制冷状态 0-停止 1-开启")
    private Integer coolingState;

    /**
     * 加热器状态 0-停止 1-开启
     */
    @ApiModelProperty(value = "加热器状态 0-停止 1-开启")
    private Integer heatingState;

    /**
     * 内风机状态 0-停止 1-开启
     */
    @ApiModelProperty(value = "内风机状态 0-停止 1-开启")
    private Integer internalState;

    /**
     * 外风机状态 0-停止 1-开启
     */
    @ApiModelProperty(value = "外风机状态 0-停止 1-开启")
    private Integer externalState;

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
     * 遥测数据列表
     */
    @ApiModelProperty(value = "遥测数据列表")
    private List<TelemetryDataDto> telemetryDataList = Lists.newArrayList();

    /**
     * 遥信告警数据
     */
    @ApiModelProperty(value = "遥信告警数据")
    private List<TelecommuteAlarmDto> telecommuteAlarmList = Lists.newArrayList();

}
