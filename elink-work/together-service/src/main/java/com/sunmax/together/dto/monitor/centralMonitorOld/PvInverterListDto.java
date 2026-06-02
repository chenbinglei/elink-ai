package com.sunmax.together.dto.monitor.centralMonitorOld;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "PvInverterListDto", description = "光伏逆变器设备列表返回实体类")
public class PvInverterListDto {

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
     * 今日发电量
     */
    @ApiModelProperty(value = "今日发电量")
    private Double dayQt;

    /**
     * 等效发电小时数
     */
    @ApiModelProperty(value = "等效发电小时数")
    private Double equivalentHours;

    /**
     * 运行状态 0-待机 1、2、3运行 其它的表示故障
     */
    @ApiModelProperty(value = "运行状态 0-待机 1、2、3运行 其它的表示故障")
    private Integer runState;

    /**
     * 运行状态更新时间
     */
    @ApiModelProperty(value = "运行状态更新时间")
    private String runStateTime;

    /**
     * 通讯状态 0-未注册 1-在线 2-维护 3-故障 88-离线
     */
    @ApiModelProperty(value = "通讯状态 0-未注册 1-在线 2-维护 3-故障 88-离线")
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
    private Double power;

    /**
     * 额定电流
     */
    @ApiModelProperty(value = "额定电流")
    private Double ratedCurrent;

    /**
     * 生产厂家
     */
    @ApiModelProperty(value = "生产厂家")
    private String manufacturerName;
}
