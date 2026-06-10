package com.sunmax.together.dto.monitor.systemMonitor;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.apache.commons.compress.utils.Lists;

import java.util.List;

@Data
@Schema(description = "光伏系统逆变器数据返回实体类")
public class SystemPvInverterDto {

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
     * 运行状态 0-待机 1、2、3运行 其它的表示故障
     */
    @Schema(description = "运行状态 0-待机 1、2、3运行 其它的表示故障")
    private Integer runState;

    /**
     * 运行状态名称
     */
    @Schema(description = "运行状态名称")
    private String runStateName;

    /**
     * 通信状态 0-未注册 1-在线 2-维护 3-故障 88-离线
     */
    @Schema(description = "通信状态 0-未注册 1-在线 2-维护 3-故障 88-离线")
    private Integer txStatus = 0;

    /**
     * 设备型号
     */
    @Schema(description = "设备型号")
    private String model;

    /**
     * 额定功率
     */
    @Schema(description = "额定功率")
    private Double ratedPower;

    /**
     * 设备厂家名称
     */
    @Schema(description = "设备厂家名称")
    private String manufacturerName;

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
