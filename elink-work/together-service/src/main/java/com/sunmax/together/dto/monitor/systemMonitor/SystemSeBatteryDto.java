package com.sunmax.together.dto.monitor.systemMonitor;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.apache.commons.compress.utils.Lists;

import java.util.List;

@Data
@Schema(description = "储能系统电池簇数据返回实体类")
public class SystemSeBatteryDto {

    /**
     * 主键id
     */
    @Schema(description = "主键id")
    private String id;

    /**
     * 设备名称
     */
    @Schema(description = "设备名称")
    private String deviceName;

    /**
     * 设备序列号
     */
    @Schema(description = "设备序列号")
    private String deviceNumber;

    /**
     * 通讯状态 0-未注册 1-在线 2-维护 3-故障 88-离线
     */
    @Schema(description = "通讯状态 0-未注册 1-在线 2-维护 3-故障 88-离线")
    private Integer txStatus = 0;

    /**
     * 运行状态 0-待机 1-禁充 2-禁放 3-故障 4-告警 5-充电 6-放电
     */
    @Schema(description = "运行状态 0-待机 1-禁充 2-禁放 3-故障 4-告警 5-充电 6-放电")
    private Integer runState;

    /**
     * 运行状态名称
     */
    @Schema(description = "运行状态名称")
    private String runStateName;

    /**
     * 额定容量
     */
    @Schema(description = "额定容量")
    private Double ratedCap;

    /**
     * 设备型号
     */
    @Schema(description = "设备型号")
    private String model;

    /**
     * 设备厂家名称
     */
    @Schema(description = "设备厂家名称")
    private String manufacturerName;

    /**
     * 电芯电压一致性 1-正常 2-异常
     */
    @Schema(description = "电芯电压一致性 1-正常 2-异常")
    private Integer cellVoltageConsistency;

    /**
     * 电芯电压极差(mV)
     */
    @Schema(description = "电芯电压极差(mV)")
    private Double cellVoltageDifference;

    /**
     * 电芯温度一致性 1-正常 2-异常
     */
    @Schema(description = "电芯温度一致性 1-正常 2-异常")
    private Integer cellTemperatureConsistency;

    /**
     * 电芯温度极差(℃)
     */
    @Schema(description = "电芯温度极差(℃)")
    private Double cellTemperatureDifference;

    /**
     * 遥测数据列表
     */
    @Schema(description = "遥测数据列表")
    private List<TelemetryDataDto> telemetryDataList = Lists.newArrayList();

    /**
     * 遥信告警数据
     */
    @Schema(description = "遥信告警数据")
    private List<TelecommuteAlarmDto> telecommuteAlarmList = Lists.newArrayList();

}
