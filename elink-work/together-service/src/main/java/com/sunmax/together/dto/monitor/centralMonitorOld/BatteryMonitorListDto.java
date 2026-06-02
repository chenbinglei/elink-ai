package com.sunmax.together.dto.monitor.centralMonitorOld;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "BatteryMonitorListDto", description = "电池簇设备监控信息返回实体类")
public class BatteryMonitorListDto {

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
     * 额定容量
     */
    @ApiModelProperty(value = "额定容量")
    private Double ratedCap;

    /**
     * soc状态
     */
    @ApiModelProperty(value = "soc状态")
    private Double soc;

    /**
     * 电池总电压
     */
    @ApiModelProperty(value = "电池总电压")
    private Double batterytotalvoltage;

    /**
     * 通讯状态 0-未注册 1-在线 2-维护 3-故障 88-离线
     */
    @ApiModelProperty(value = "通讯状态 0-未注册 1-在线 2-维护 3-故障 88-离线")
    private Integer txStatus = 0;

    /**
     * 充放电状态 0：静置，1：放电，2：充电
     */
    @ApiModelProperty(value = "pcs工作状态 0：静置，1：放电，2：充电")
    private Integer chargestate;

    /**
     * 充放电状态更新时间
     */
    @ApiModelProperty(value = "运行状态更新时间")
    private String chargeStateTime;

    /**
     * 设备型号
     */
    @ApiModelProperty(value = "设备型号")
    private String model;

    /**
     * 生产厂家
     */
    @ApiModelProperty(value = "生产厂家")
    private String manufacturerName;

    /**
     * 电芯数量
     */
    @ApiModelProperty(value = "电芯数量")
    private Integer cellNum;
}
