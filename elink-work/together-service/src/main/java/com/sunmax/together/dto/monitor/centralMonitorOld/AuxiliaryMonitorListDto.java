package com.sunmax.together.dto.monitor.centralMonitorOld;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "辅助设备监控信息返回实体类")
public class AuxiliaryMonitorListDto {

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
     * 设备型号
     */
    @Schema(description = "设备型号")
    private String model;

    /**
     * 生产厂家
     */
    @Schema(description = "生产厂家")
    private String manufacturerName;

    /**
     * 通讯状态 0-未注册 1-在线 2-维护 3-故障 88-离线
     */
    @Schema(description = "通讯状态 0-未注册 1-在线 2-维护 3-故障 88-离线")
    private Integer txStatus = 0;

    /**
     * 柜内温度
     */
    @Schema(description = "柜内温度")
    private Double cabinetTemp;

    /**
     * 柜内湿度
     */
    @Schema(description = "柜内湿度")
    private Double cabinetHumidity;

    /**
     * 制冷状态 0：停止，1：开启
     */
    @Schema(description = "制冷状态 0：停止，1：开启")
    private Integer coolingState;

    /**
     * 加热器状态 0：停止，1：开启
     */
    @Schema(description = "加热器状态 0：停止，1：开启")
    private Integer heatingState;

    /**
     * 内风机状态 0：停止，1：开启
     */
    @Schema(description = "内风机状态 0：停止，1：开启")
    private Integer internalfanState;

    /**
     * 外风机状态 0：停止，1：开启
     */
    @Schema(description = "外风机状态 0：停止，1：开启")
    private Integer externalfanState;
}
