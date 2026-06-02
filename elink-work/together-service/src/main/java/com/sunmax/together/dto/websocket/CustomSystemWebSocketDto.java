package com.sunmax.together.dto.websocket;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.compress.utils.Lists;

import java.util.List;

@Data
@ApiModel(value = "CustomSystemWebSocketDto", description = "系统定制页面websocket返回实体类")
public class CustomSystemWebSocketDto {

    /**
     * 站点id
     */
    @ApiModelProperty(value = "站点id")
    private String siteId;

    /**
     * 运行模式对象
     */
    @ApiModelProperty(value = "运行模式对象")
    private RunMode runMode;

    /**
     * 功率曲线分析对象
     */
    @ApiModelProperty(value = "功率曲线分析对象")
    private PowerCurveAnalysis powerCurveAnalysis;

    /**
     * 关口对象
     */
    @ApiModelProperty(value = "关口对象")
    private StageGate stageGate;

    /**
     * 直流母线对象
     */
    @ApiModelProperty(value = "直流母线对象")
    private DcBus dcBus;

    /**
     * 光伏对象
     */
    @ApiModelProperty(value = "光伏对象")
    private Photovoltaic photovoltaic;

    /**
     * 储能对象
     */
    @ApiModelProperty(value = "储能对象")
    private EnergyStorage energyStorage;

    /**
     * 负载对象
     */
    @ApiModelProperty(value = "负载对象")
    private Load load;


    @Data
    @ApiModel(value = "RunMode", description = "运行模式返回实体类")
    public static class RunMode {

        /**
         * 策略类型 1-综合智能策略 2-峰谷套利策略 3-削峰策略 4-定时策略 5-限电策略 6-变压器扩容 7-负载扩容策略 8-备用电源策略
         */
        @ApiModelProperty(value = "策略类型 1-综合智能策略 2-峰谷套利策略 3-削峰策略 4-定时策略 5-限电策略 6-变压器扩容 7-负载扩容策略 8-备用电源策略")
        private Integer strategyType = 3;

        /**
         * 运行控制 1-手动控制 2-自动控制
         */
        @ApiModelProperty(value = "运行控制 1-手动控制 2-自动控制")
        private Integer runControl = 1;

        /**
         * 网关设备列表
         */
        @ApiModelProperty(value = "网关设备列表")
        private List<GatewayDevice> gatewayDeviceList = Lists.newArrayList();

    }

    @Data
    @ApiModel(value = "GatewayDevice", description = "网关设备返回实体类")
    public static class GatewayDevice {

        /**
         * 主键id
         */
        @ApiModelProperty(value = "主键id")
        private String id;

        /**
         * 设备序列号
         */
        @ApiModelProperty(value = "设备序列号")
        private String deviceNumber;

        /**
         * 设备图片路径
         */
        @ApiModelProperty(value = "设备图片路径")
        private String imagePaths;

        /**
         * 通信状态 0-未注册 1-在线 2-故障 88-离线
         */
        @ApiModelProperty(value = "通信状态 0-未注册 1-在线 2-故障 88-离线")
        private Integer txStatus = 0;

    }

    @Data
    @ApiModel(value = "PowerCurveAnalysis", description = "功率曲线分析返回实体类")
    public static class PowerCurveAnalysis {

        /**
         * 今日时间列表
         */
        @ApiModelProperty(value = "今日时间列表")
        private List<String> todayTimeList = Lists.newArrayList();

        /**
         * 今日光伏功率列表
         */
        @ApiModelProperty(value = "今日光伏功率列表")
        private List<Double> pvTodayPowerList = Lists.newArrayList();

        /**
         * 今日储能功率列表
         */
        @ApiModelProperty(value = "今日储能功率列表")
        private List<Double> seTodayPowerList = Lists.newArrayList();

        /**
         * 今日负载功率列表
         */
        @ApiModelProperty(value = "今日负载功率列表")
        private List<Double> loadTodayPowerList = Lists.newArrayList();

        /**
         * 今日直流母线功率列表
         */
        @ApiModelProperty(value = "今日直流母线功率列表")
        private List<Double> busTodayPowerList = Lists.newArrayList();

        /**
         * 昨日时间列表
         */
        @ApiModelProperty(value = "昨日时间列表")
        private List<String> yestdayTimeList = Lists.newArrayList();

        /**
         * 昨日光伏功率列表
         */
        @ApiModelProperty(value = "昨日光伏功率列表")
        private List<Double> pvYestdayPowerList = Lists.newArrayList();

        /**
         * 昨日储能功率列表
         */
        @ApiModelProperty(value = "昨日储能功率列表")
        private List<Double> seYestdayPowerList = Lists.newArrayList();

        /**
         * 昨日负载功率列表
         */
        @ApiModelProperty(value = "昨日负载功率列表")
        private List<Double> loadYestdayPowerList = Lists.newArrayList();

        /**
         * 昨日直流母线功率列表
         */
        @ApiModelProperty(value = "昨日直流母线功率列表")
        private List<Double> busYestdayPowerList = Lists.newArrayList();

    }

    @Data
    @ApiModel(value = "StageGate", description = "关口返回实体类")
    public static class StageGate {

        /**
         * A相电压(V)
         */
        @ApiModelProperty(value = "A相电压(V)")
        private Double voltageA;

        /**
         * B相电压(V)
         */
        @ApiModelProperty(value = "B相电压(V)")
        private Double voltageB;

        /**
         * C相电压(V)
         */
        @ApiModelProperty(value = "C相电压(V)")
        private Double voltageC;

        /**
         * A相电流(A)
         */
        @ApiModelProperty(value = "A相电流(A)")
        private Double currentA;

        /**
         * B相电流(A)
         */
        @ApiModelProperty(value = "B相电流(A)")
        private Double currentB;

        /**
         * C相电流(A)
         */
        @ApiModelProperty(value = "C相电流(A)")
        private Double currentC;

        /**
         * 总有功功率(kW)
         */
        @ApiModelProperty(value = "总有功功率(kW)")
        private Double power;

        /**
         * 功率因数
         */
        @ApiModelProperty(value = "功率因数(pf)")
        private Double powerFactor;

        /**
         * 频率(hz)
         */
        @ApiModelProperty(value = "频率(hz)")
        private Double frequency;

    }

    @Data
    @ApiModel(value = "DcBus", description = "直流母线返回实体类")
    public static class DcBus {

        /**
         * A相电压(V)
         */
        @ApiModelProperty(value = "A相电压(V)")
        private Double voltageA;

        /**
         * B相电压(V)
         */
        @ApiModelProperty(value = "B相电压(V)")
        private Double voltageB;

        /**
         * C相电压(V)
         */
        @ApiModelProperty(value = "C相电压(V)")
        private Double voltageC;

        /**
         * A相电流(A)
         */
        @ApiModelProperty(value = "A相电流(A)")
        private Double currentA;

        /**
         * B相电流(A)
         */
        @ApiModelProperty(value = "B相电流(A)")
        private Double currentB;

        /**
         * C相电流(A)
         */
        @ApiModelProperty(value = "C相电流(A)")
        private Double currentC;

        /**
         * 总有功功率(kW)
         */
        @ApiModelProperty(value = "总有功功率(kW)")
        private Double power;

        /**
         * 母线电压(V)
         */
        @ApiModelProperty(value = "母线电压(V)")
        private Double busVoltage;

        /**
         * 母线电流(A)
         */
        @ApiModelProperty(value = "母线电流(A)")
        private Double busCurrent;

        /**
         * 母线功率(kW)
         */
        @ApiModelProperty(value = "母线功率(kW)")
        private Double busPower;

    }

    @Data
    @ApiModel(value = "Photovoltaic", description = "光伏返回实体类")
    public static class Photovoltaic {

        /**
         * 母线电压(V)
         */
        @ApiModelProperty(value = "母线电压(V)")
        private Double busVoltage;

        /**
         * 母线电流(A)
         */
        @ApiModelProperty(value = "母线电流(A)")
        private Double busCurrent;

        /**
         * 光伏功率(kW)
         */
        @ApiModelProperty(value = "光伏功率(kW)")
        private Double power;

        /**
         * MQTT电压(V)
         */
        @ApiModelProperty(value = "MQTT电压(V)")
        private Double mpptVoltage;

        /**
         * 运行状态 0-关机 1-开机
         */
        @ApiModelProperty(value = "运行状态 0-关机 1-开机")
        private Integer runStatus;

        /**
         * 运行状态名称
         */
        @ApiModelProperty(value = "运行状态名称")
        private String runStatusName;

        /**
         * 今日发电量(kWh)
         */
        @ApiModelProperty(value = "今日发电量(kWh)")
        private Double dayQt;

        /**
         * 累计发电量(kWh)
         */
        @ApiModelProperty(value = "累计发电量(kWh)")
        private Double totalQt;

    }

    @Data
    @ApiModel(value = "EnergyStorage", description = "储能返回实体类")
    public static class EnergyStorage {

        /**
         * 母线电压(V)
         */
        @ApiModelProperty(value = "母线电压(V)")
        private Double busVoltage;

        /**
         * 母线电流(A)
         */
        @ApiModelProperty(value = "母线电流(A)")
        private Double busCurrent;

        /**
         * 储能功率(kW)
         */
        @ApiModelProperty(value = "储能功率(kW)")
        private Double power;

        /**
         * 电池总电压(V)
         */
        @ApiModelProperty(value = "电池总电压(V)")
        private Double batteryVoltage;

        /**
         * 运行状态 0-待机 1-运行 32-故障
         */
        @ApiModelProperty(value = "运行状态 0-待机 1-运行 32-故障")
        private Integer runStatus;

        /**
         * 运行状态名称
         */
        @ApiModelProperty(value = "运行状态名称")
        private String runStatusName;

        /**
         * SOC(%)
         */
        @ApiModelProperty(value = "SOC(%)")
        private Double soc;

        /**
         * 今日充电量(kWh)
         */
        @ApiModelProperty(value = "今日充电量(kWh)")
        private Double chargeQt;

        /**
         * 今日放电量(kWh)
         */
        @ApiModelProperty(value = "今日放电量(kWh)")
        private Double dischargeQt;

        /**
         * 累计充电量(kWh)
         */
        @ApiModelProperty(value = "累计充电量(kWh)")
        private Double totalChargeQt;

        /**
         * 累计放电量(kWh)
         */
        @ApiModelProperty(value = "累计放电量(kWh)")
        private Double totalDischargeQt;

    }

    @Data
    @ApiModel(value = "Load", description = "负载返回实体类")
    public static class Load {

        /**
         * 母线电压(V)
         */
        @ApiModelProperty(value = "母线电压(V)")
        private Double busVoltage;

        /**
         * 母线电流(A)
         */
        @ApiModelProperty(value = "母线电流(A)")
        private Double busCurrent;

        /**
         * 负载功率(kW)
         */
        @ApiModelProperty(value = "负载功率(kW)")
        private Double power;

        /**
         * 累计总电量(kWh)
         */
        @ApiModelProperty(value = "累计总电量(kWh)")
        private Double totalQt;

    }


}
