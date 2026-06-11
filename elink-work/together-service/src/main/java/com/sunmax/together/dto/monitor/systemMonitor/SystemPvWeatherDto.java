package com.sunmax.together.dto.monitor.systemMonitor;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "光伏系统气象站数据返回实体类")
public class SystemPvWeatherDto {

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
     * 通信状态 0-未注册 1-在线 2-维护 3-故障 88-离线
     */
    @Schema(description = "通信状态 0-未注册 1-在线 2-维护 3-故障 88-离线")
    private Integer txStatus = 0;

    /**
     * 经度
     */
    @Schema(description = "经度")
    private Double longitude;

    /**
     * 纬度
     */
    @Schema(description = "纬度")
    private Double latitude;

    /**
     * 设备厂家名称
     */
    @Schema(description = "设备厂家名称")
    private String manufacturerName;

    /**
     * 水平辐射值(W/m²)
     */
    @Schema(description = "水平辐射值(W/m²)")
    private Double horizontalRadiation;

    /**
     * 倾斜辐射值(W/m²)
     */
    @Schema(description = "倾斜辐射值(W/m²)")
    private Double inclinedRadiation;

    /**
     * 组件背板温度(°C)
     */
    @Schema(description = "组件背板温度(°C)")
    private Double moduleTemperature;

    /**
     * 风向
     */
    @Schema(description = "风向")
    private Double windDirection;

    /**
     * 风速(m/s)
     */
    @Schema(description = "风速(m/s)")
    private Double windSpeed;

}
