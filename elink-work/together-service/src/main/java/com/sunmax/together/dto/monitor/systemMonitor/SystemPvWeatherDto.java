package com.sunmax.together.dto.monitor.systemMonitor;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "SystemPvWeatherDto", description = "光伏系统气象站数据返回实体类")
public class SystemPvWeatherDto {

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
     * 通信状态 0-未注册 1-在线 2-维护 3-故障 88-离线
     */
    @ApiModelProperty(value = "通信状态 0-未注册 1-在线 2-维护 3-故障 88-离线")
    private Integer txStatus = 0;

    /**
     * 经度
     */
    @ApiModelProperty(value = "经度")
    private Double longitude;

    /**
     * 纬度
     */
    @ApiModelProperty(value = "纬度")
    private Double latitude;

    /**
     * 设备厂家名称
     */
    @ApiModelProperty(value = "设备厂家名称")
    private String manufacturerName;

    /**
     * 水平辐射值(W/m²)
     */
    @ApiModelProperty(value = "水平辐射值(W/m²)")
    private Double horizontalRadiation;

    /**
     * 倾斜辐射值(W/m²)
     */
    @ApiModelProperty(value = "倾斜辐射值(W/m²)")
    private Double inclinedRadiation;

    /**
     * 组件背板温度(°C)
     */
    @ApiModelProperty(value = "组件背板温度(°C)")
    private Double moduleTemperature;

    /**
     * 风向
     */
    @ApiModelProperty(value = "风向")
    private Double windDirection;

    /**
     * 风速(m/s)
     */
    @ApiModelProperty(value = "风速(m/s)")
    private Double windSpeed;

}
