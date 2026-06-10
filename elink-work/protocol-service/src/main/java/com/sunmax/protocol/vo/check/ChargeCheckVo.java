package com.sunmax.protocol.vo.check;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
public class ChargeCheckVo {

    /**
     * 用车场景 01-私家车 02-运营小客车 03-运营大巴 04-运营货车 05-其它
     */
    @Schema(description = "用车场景 01-私家车 02-运营小客车 03-运营大巴 04-运营货车 05-其它")
    private String scene;

    /**
     * 车辆的类型 01-纯电 02-混动 03-增程
     */
    @Schema(description = "车辆的类型 01-纯电 02-混动 03-增程")
    private String vehicleType;

    /**
     * 车辆使用年限 以月为单位。车主填写,有利于提高SOH指标的准确性
     */
    @Schema(description = "车辆使用年限")
    private String serviceLife;

    /**
     * 车辆累计里程 以km为单位。车主填写,有利于提高SOH与SOE指标的准确性
     */
    @Schema(description = "车辆累计里程")
    private String odometer;

    /**
     * 车牌号 部分业务需要,如政府机构业务需要
     */
    @Schema(description = "车牌号")
    private String plateNumber;

    /**
     * 厂商ID 数据合作方的唯一识别号,一个客户平台一个ID,由我方平台统一给出,在 API请求过程中,务必包含。格式为：mfr+16位纯数字随 机数
     */
    @Schema(description = "厂商ID")
    private String mfrID;

    /**
     * 站点名称 与检测结果订单唯一绑定需要,合作方提供。如,上海闵行区春申路 xx 加油站
     */
    @Schema(description = "站点名称")
    private String siteName;

    /**
     * 站点详细地址 与检测结果订单唯一绑定需要,合作方提 供。如,上海市闵行区春申路 xx 号
     */
    @Schema(description = "站点详细地址")
    private String siteAddress;

    /**
     * 站点经纬度
     */
    @Schema(description = "站点经纬度")
    private List<Double> coordinate;

    /**
     * 充电桩编号
     */
    @Schema(description = "充电桩编号")
    private String pileCode;

    /**
     * 充电枪编号 与检测结果订单唯一绑定需要。格式为：01、02、03...。一般一桩双枪的 ，A 枪（左 枪）为01 ，B 枪（右枪）为 02；对于充电堆式 的 ，按照 01、02...或厂家自定义
     */
    @Schema(description = "充电枪编号")
    private String gunNo;

    /**
     * 充电前读表值 （非国标数据）桩自带得电表监测数据
     */
    @Schema(description = "充电前读表值")
    private String readBeforeCharge;


    private String bmsVersion;

    private String alarmCode;

    private String nominalEnergy;

    private String maxAllowTemp;

    private String ratedCapacity;

    private String totalCharge;

    private String ratedVoltage;

    private String singleMaxVoltage;

    private String orderCode;

    private String singleMaxTemp;

    private String vin;

    private String beginTime;

    private String singleMinTemp;

    private String batteryType;

    private String totalChargeTime;

    private String remainChargeTime;

    private String initSoc;

    private List<DataDto> data;

    @Data
    @Schema(description = "DataDto")
    public static class DataDto {

        /**
         * 直流充电电压 BMS监控桩实时输出电压,单位:V
         */
        @Schema(description = "直流充电电压")
        private String dcv;

        /**
         * 直流充电电流 BMS监控桩实时输出电流,单位:A
         */
        @Schema(description = "直流充电电流")
        private String dca;

        /**
         * 充电功率 充电桩充电实时输出的功率,单位:W
         */
        @Schema(description = "充电功率")
        private String chargePower;

        /**
         * 当前电池剩余容量 车辆充电过程中当前的剩余电量 ，格式：整 数，赋值不需要带"%
         */
        private Integer currentSoc;

        private String measuringChargeVoltage;

        private String gunTemp;

        private String singleMaxAllowVoltage;

        private String beforeChargeTotalVoltage;

        private String readCurrentCharge;

        private String singleMinVoltage;

        private String bmsDemandElectricity;

        private String maxAllowTotalVoltage;

        private String bmsDemandVoltage;

        private String maxAllowElectricity;

        private String environmentTemp;

        private String measuringChargeElectricity;

        private String singleMaxVoltage;

        private String singleMaxTemp;

        private String singleMinTemp;

        private String ventTemp;

        private String reportTime;

        private Integer socStatus;

        private Integer maxTempPointNum;

        private Integer minTempPointNum;

        private Integer maxSingleVoltageNum;

        private Integer maxSingleVoltageGroupNum;

        private Integer singleVoltageStatus;

        private Integer electricityStatus;

        private Integer batteryInsulation;

        private Integer outputConnectStatus;

        private Integer tempStatus;

        private Integer insulationStatus;

        private Integer bmsConnectError;

        private Integer bmsVoltageError;

        private Integer bmsInsulationError;

        private Integer bmsOverTempError;

        private Integer bmsVoltageFault;

        private Integer bmsHighVoltageError;

        private Integer bmsElectricityOverFault;

        private Integer bmsBatteryOverTempError;

    }

}
