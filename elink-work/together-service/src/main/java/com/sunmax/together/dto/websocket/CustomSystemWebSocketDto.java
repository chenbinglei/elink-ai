package com.sunmax.together.dto.websocket;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.apache.commons.compress.utils.Lists;

import java.util.List;

@Data
@Schema(description = "系统定制页面websocket返回实体类")
public class CustomSystemWebSocketDto {

    /**
     * 站点id
     */
    @Schema(description = "站点id")
    private String siteId;

    /**
     * 运行模式对象
     */
    @Schema(description = "运行模式对象")
    private RunMode runMode;

    /**
     * 功率曲线分析对象
     */
    @Schema(description = "功率曲线分析对象")
    private PowerCurveAnalysis powerCurveAnalysis;

    /**
     * 关口对象
     */
    @Schema(description = "关口对象")
    private StageGate stageGate;

    /**
     * 直流母线对象
     */
    @Schema(description = "直流母线对象")
    private DcBus dcBus;

    /**
     * 光伏对象
     */
    @Schema(description = "光伏对象")
    private Photovoltaic photovoltaic;

    /**
     * 储能对象
     */
    @Schema(description = "储能对象")
    private EnergyStorage energyStorage;

    /**
     * 负载对象
     */
    @Schema(description = "负载对象")
    private Load load;


    @Data
    @Schema(description = "运行模式返回实体类")
    public static class RunMode {

        /**
         * 策略类型 1-综合智能策略 2-峰谷套利策略 3-削峰策略 4-定时策略 5-限电策略 6-变压器扩容 7-负载扩容策略 8-备用电源策略
         */
        @Schema(description = "策略类型 1-综合智能策略 2-峰谷套利策略 3-削峰策略 4-定时策略 5-限电策略 6-变压器扩容 7-负载扩容策略 8-备用电源策略")
        private Integer strategyType = 3;

        /**
         * 运行控制 1-手动控制 2-自动控制
         */
        @Schema(description = "运行控制 1-手动控制 2-自动控制")
        private Integer runControl = 1;

        /**
         * 网关设备列表
         */
        @Schema(description = "网关设备列表")
        private List<GatewayDevice> gatewayDeviceList = Lists.newArrayList();

    }

    @Data
    @Schema(description = "网关设备返回实体类")
    public static class GatewayDevice {

        /**
         * 主键id
         */
        @Schema(description = "主键id")
        private String id;

        /**
         * 设备序列号
         */
        @Schema(description = "设备序列号")
        private String deviceNumber;

        /**
         * 设备图片路径
         */
        @Schema(description = "设备图片路径")
        private String imagePaths;

        /**
         * 通信状态 0-未注册 1-在线 2-故障 88-离线
         */
        @Schema(description = "通信状态 0-未注册 1-在线 2-故障 88-离线")
        private Integer txStatus = 0;

    }

    @Data
    @Schema(description = "功率曲线分析返回实体类")
    public static class PowerCurveAnalysis {

        /**
         * 今日时间列表
         */
        @Schema(description = "今日时间列表")
        private List<String> todayTimeList = Lists.newArrayList();

        /**
         * 今日光伏功率列表
         */
        @Schema(description = "今日光伏功率列表")
        private List<Double> pvTodayPowerList = Lists.newArrayList();

        /**
         * 今日储能功率列表
         */
        @Schema(description = "今日储能功率列表")
        private List<Double> seTodayPowerList = Lists.newArrayList();

        /**
         * 今日负载功率列表
         */
        @Schema(description = "今日负载功率列表")
        private List<Double> loadTodayPowerList = Lists.newArrayList();

        /**
         * 今日直流母线功率列表
         */
        @Schema(description = "今日直流母线功率列表")
        private List<Double> busTodayPowerList = Lists.newArrayList();

        /**
         * 昨日时间列表
         */
        @Schema(description = "昨日时间列表")
        private List<String> yestdayTimeList = Lists.newArrayList();

        /**
         * 昨日光伏功率列表
         */
        @Schema(description = "昨日光伏功率列表")
        private List<Double> pvYestdayPowerList = Lists.newArrayList();

        /**
         * 昨日储能功率列表
         */
        @Schema(description = "昨日储能功率列表")
        private List<Double> seYestdayPowerList = Lists.newArrayList();

        /**
         * 昨日负载功率列表
         */
        @Schema(description = "昨日负载功率列表")
        private List<Double> loadYestdayPowerList = Lists.newArrayList();

        /**
         * 昨日直流母线功率列表
         */
        @Schema(description = "昨日直流母线功率列表")
        private List<Double> busYestdayPowerList = Lists.newArrayList();

    }

    @Data
    @Schema(description = "关口返回实体类")
    public static class StageGate {

        /**
         * A相电压(V)
         */
        @Schema(description = "A相电压(V)")
        private Double voltageA;

        /**
         * B相电压(V)
         */
        @Schema(description = "B相电压(V)")
        private Double voltageB;

        /**
         * C相电压(V)
         */
        @Schema(description = "C相电压(V)")
        private Double voltageC;

        /**
         * A相电流(A)
         */
        @Schema(description = "A相电流(A)")
        private Double currentA;

        /**
         * B相电流(A)
         */
        @Schema(description = "B相电流(A)")
        private Double currentB;

        /**
         * C相电流(A)
         */
        @Schema(description = "C相电流(A)")
        private Double currentC;

        /**
         * 总有功功率(kW)
         */
        @Schema(description = "总有功功率(kW)")
        private Double power;

        /**
         * 功率因数
         */
        @Schema(description = "功率因数(pf)")
        private Double powerFactor;

        /**
         * 频率(hz)
         */
        @Schema(description = "频率(hz)")
        private Double frequency;

    }

    @Data
    @Schema(description = "直流母线返回实体类")
    public static class DcBus {

        /**
         * A相电压(V)
         */
        @Schema(description = "A相电压(V)")
        private Double voltageA;

        /**
         * B相电压(V)
         */
        @Schema(description = "B相电压(V)")
        private Double voltageB;

        /**
         * C相电压(V)
         */
        @Schema(description = "C相电压(V)")
        private Double voltageC;

        /**
         * A相电流(A)
         */
        @Schema(description = "A相电流(A)")
        private Double currentA;

        /**
         * B相电流(A)
         */
        @Schema(description = "B相电流(A)")
        private Double currentB;

        /**
         * C相电流(A)
         */
        @Schema(description = "C相电流(A)")
        private Double currentC;

        /**
         * 总有功功率(kW)
         */
        @Schema(description = "总有功功率(kW)")
        private Double power;

        /**
         * 母线电压(V)
         */
        @Schema(description = "母线电压(V)")
        private Double busVoltage;

        /**
         * 母线电流(A)
         */
        @Schema(description = "母线电流(A)")
        private Double busCurrent;

        /**
         * 母线功率(kW)
         */
        @Schema(description = "母线功率(kW)")
        private Double busPower;

    }

    @Data
    @Schema(description = "光伏返回实体类")
    public static class Photovoltaic {

        /**
         * 母线电压(V)
         */
        @Schema(description = "母线电压(V)")
        private Double busVoltage;

        /**
         * 母线电流(A)
         */
        @Schema(description = "母线电流(A)")
        private Double busCurrent;

        /**
         * 光伏功率(kW)
         */
        @Schema(description = "光伏功率(kW)")
        private Double power;

        /**
         * MQTT电压(V)
         */
        @Schema(description = "MQTT电压(V)")
        private Double mpptVoltage;

        /**
         * 运行状态 0-关机 1-开机
         */
        @Schema(description = "运行状态 0-关机 1-开机")
        private Integer runStatus;

        /**
         * 运行状态名称
         */
        @Schema(description = "运行状态名称")
        private String runStatusName;

        /**
         * 今日发电量(kWh)
         */
        @Schema(description = "今日发电量(kWh)")
        private Double dayQt;

        /**
         * 累计发电量(kWh)
         */
        @Schema(description = "累计发电量(kWh)")
        private Double totalQt;

    }

    @Data
    @Schema(description = "储能返回实体类")
    public static class EnergyStorage {

        /**
         * 母线电压(V)
         */
        @Schema(description = "母线电压(V)")
        private Double busVoltage;

        /**
         * 母线电流(A)
         */
        @Schema(description = "母线电流(A)")
        private Double busCurrent;

        /**
         * 储能功率(kW)
         */
        @Schema(description = "储能功率(kW)")
        private Double power;

        /**
         * 电池总电压(V)
         */
        @Schema(description = "电池总电压(V)")
        private Double batteryVoltage;

        /**
         * 运行状态 0-待机 1-运行 32-故障
         */
        @Schema(description = "运行状态 0-待机 1-运行 32-故障")
        private Integer runStatus;

        /**
         * 运行状态名称
         */
        @Schema(description = "运行状态名称")
        private String runStatusName;

        /**
         * SOC(%)
         */
        @Schema(description = "SOC(%)")
        private Double soc;

        /**
         * 今日充电量(kWh)
         */
        @Schema(description = "今日充电量(kWh)")
        private Double chargeQt;

        /**
         * 今日放电量(kWh)
         */
        @Schema(description = "今日放电量(kWh)")
        private Double dischargeQt;

        /**
         * 累计充电量(kWh)
         */
        @Schema(description = "累计充电量(kWh)")
        private Double totalChargeQt;

        /**
         * 累计放电量(kWh)
         */
        @Schema(description = "累计放电量(kWh)")
        private Double totalDischargeQt;

    }

    @Data
    @Schema(description = "负载返回实体类")
    public static class Load {

        /**
         * 母线电压(V)
         */
        @Schema(description = "母线电压(V)")
        private Double busVoltage;

        /**
         * 母线电流(A)
         */
        @Schema(description = "母线电流(A)")
        private Double busCurrent;

        /**
         * 负载功率(kW)
         */
        @Schema(description = "负载功率(kW)")
        private Double power;

        /**
         * 累计总电量(kWh)
         */
        @Schema(description = "累计总电量(kWh)")
        private Double totalQt;

    }


}
