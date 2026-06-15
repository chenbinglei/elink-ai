package com.sunmax.together.dto.monitor.centralMonitorOld;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "pc设备监控信息返回实体类")
public class PcsMonitorListDto {

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
     * 额定功率
     */
    @Schema(description = "额定功率")
    private Double power;

    /**
     * 设备型号
     */
    @Schema(description = "设备型号")
    private String model;

    /**
     * 昨日充放效率
     */
    @Schema(description = "昨日充放效率")
    private Double lastDayEff;

    /**
     * 通讯状态 0-未注册 1-在线 2-维护 3-故障 88-离线
     */
    @Schema(description = "通讯状态 0-未注册 1-在线 2-维护 3-故障 88-离线")
    private Integer txStatus = 0;

    /**
     * pcs工作状态 0：停机，1：待机，2：运行，3：故障
     */
    @Schema(description = "pcs工作状态 0：停机，1：待机，2：运行，3：故障")
    private Integer pcsOperativeMode;

    /**
     * 运行状态更新时间
     */
    @Schema(description = "运行状态更新时间")
    private String runStateTime;

    /**
     * 生产厂家
     */
    @Schema(description = "生产厂家")
    private String manufacturerName;

    /**
     * 电池簇数量
     */
    @Schema(description = "电池簇数量")
    private Integer batteryNum;

    /**
     * 昨日充电量
     */
    @Schema(description = "昨日充电量")
    private Double lastDayChargeQt;

    /**
     * 昨日放电量
     */
    @Schema(description = "昨日放电量")
    private Double lastDayDischargeQt;
}
