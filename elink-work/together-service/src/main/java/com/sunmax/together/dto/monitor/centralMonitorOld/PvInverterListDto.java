package com.sunmax.together.dto.monitor.centralMonitorOld;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "光伏逆变器设备列表返回实体类")
public class PvInverterListDto {

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
     * 有功功率
     */
    @Schema(description = "有功功率")
    private Double activePower;

    /**
     * 今日发电量
     */
    @Schema(description = "今日发电量")
    private Double dayQt;

    /**
     * 等效发电小时数
     */
    @Schema(description = "等效发电小时数")
    private Double equivalentHours;

    /**
     * 运行状态 0-待机 1、2、3运行 其它的表示故障
     */
    @Schema(description = "运行状态 0-待机 1、2、3运行 其它的表示故障")
    private Integer runState;

    /**
     * 运行状态更新时间
     */
    @Schema(description = "运行状态更新时间")
    private String runStateTime;

    /**
     * 通讯状态 0-未注册 1-在线 2-维护 3-故障 88-离线
     */
    @Schema(description = "通讯状态 0-未注册 1-在线 2-维护 3-故障 88-离线")
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
    private Double power;

    /**
     * 额定电流
     */
    @Schema(description = "额定电流")
    private Double ratedCurrent;

    /**
     * 生产厂家
     */
    @Schema(description = "生产厂家")
    private String manufacturerName;
}
