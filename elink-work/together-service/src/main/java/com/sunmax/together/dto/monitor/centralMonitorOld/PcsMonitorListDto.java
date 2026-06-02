package com.sunmax.together.dto.monitor.centralMonitorOld;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "PcsMonitorListDto", description = "pc设备监控信息返回实体类")
public class PcsMonitorListDto {

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
     * 有功功率
     */
    @ApiModelProperty(value = "有功功率")
    private Double activePower;

    /**
     * 额定功率
     */
    @ApiModelProperty(value = "额定功率")
    private Double power;

    /**
     * 设备型号
     */
    @ApiModelProperty(value = "设备型号")
    private String model;

    /**
     * 昨日充放效率
     */
    @ApiModelProperty(value = "昨日充放效率")
    private Double lastDayEff;

    /**
     * 通讯状态 0-未注册 1-在线 2-维护 3-故障 88-离线
     */
    @ApiModelProperty(value = "通讯状态 0-未注册 1-在线 2-维护 3-故障 88-离线")
    private Integer txStatus = 0;

    /**
     * pcs工作状态 0：停机，1：待机，2：运行，3：故障
     */
    @ApiModelProperty(value = "pcs工作状态 0：停机，1：待机，2：运行，3：故障")
    private Integer pcsOperativeMode;

    /**
     * 运行状态更新时间
     */
    @ApiModelProperty(value = "运行状态更新时间")
    private String runStateTime;

    /**
     * 生产厂家
     */
    @ApiModelProperty(value = "生产厂家")
    private String manufacturerName;

    /**
     * 电池簇数量
     */
    @ApiModelProperty(value = "电池簇数量")
    private Integer batteryNum;

    /**
     * 昨日充电量
     */
    @ApiModelProperty(value = "昨日充电量")
    private Double lastDayChargeQt;

    /**
     * 昨日放电量
     */
    @ApiModelProperty(value = "昨日放电量")
    private Double lastDayDischargeQt;
}
