package com.sunmax.together.dto.monitor.systemMonitor;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.compress.utils.Lists;

import java.util.List;

@Data
@ApiModel(value = "SystemPvInverterDto", description = "光伏系统逆变器数据返回实体类")
public class SystemPvInverterDto {

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
     * 运行状态 0-待机 1、2、3运行 其它的表示故障
     */
    @ApiModelProperty(value = "运行状态 0-待机 1、2、3运行 其它的表示故障")
    private Integer runState;

    /**
     * 运行状态名称
     */
    @ApiModelProperty(value = "运行状态名称")
    private String runStateName;

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
