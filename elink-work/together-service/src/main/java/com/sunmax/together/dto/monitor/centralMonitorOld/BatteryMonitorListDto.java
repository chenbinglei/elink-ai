package com.sunmax.together.dto.monitor.centralMonitorOld;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "电池簇设备监控信息返回实体类")
public class BatteryMonitorListDto {

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
     * 额定容量
     */
    @Schema(description = "额定容量")
    private Double ratedCap;

    /**
     * soc状态
     */
    @Schema(description = "soc状态")
    private Double soc;

    /**
     * 电池总电压
     */
    @Schema(description = "电池总电压")
    private Double batterytotalvoltage;

    /**
     * 通讯状态 0-未注册 1-在线 2-维护 3-故障 88-离线
     */
    @Schema(description = "通讯状态 0-未注册 1-在线 2-维护 3-故障 88-离线")
    private Integer txStatus = 0;

    /**
     * 充放电状态 0：静置，1：放电，2：充电
     */
    @Schema(description = "pcs工作状态 0：静置，1：放电，2：充电")
    private Integer chargestate;

    /**
     * 充放电状态更新时间
     */
    @Schema(description = "运行状态更新时间")
    private String chargeStateTime;

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
     * 电芯数量
     */
    @Schema(description = "电芯数量")
    private Integer cellNum;
}
