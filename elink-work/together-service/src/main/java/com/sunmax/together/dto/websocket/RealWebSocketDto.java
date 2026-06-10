package com.sunmax.together.dto.websocket;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.apache.commons.compress.utils.Lists;

import java.util.ArrayList;
import java.util.List;

@Data
@Schema(description = "大屏实时页面websocket返回实体类")
public class RealWebSocketDto {

    /**
     * 实时功率
     */
    @Schema(description = "实时功率")
    private RealPowerDto realPower;

    /**
     * 设备状态
     */
    @Schema(description = "设备状态")
    private List<DeviceStatusDto> deviceStatus = Lists.newArrayList();

    /**
     * 运营日期列表
     */
    @Schema(description = "运营日期列表")
    private List<String> operationDateList = Lists.newArrayList();

    /**
     * 充电桩运营
     */
    @Schema(description = "充电桩运营")
    private PileOperationDto pileOperation;

    /**
     * 换电运营
     */
    @Schema(description = "换电运营")
    private ChangeOperationDto changeOperation;

    /**
     * 光伏运营
     */
    @Schema(description = "光伏运营")
    private PvOperationDto pvOperation;

    /**
     * 储能运营
     */
    @Schema(description = "储能运营")
    private StorageOperationDto storageOperation;

    @Data
    @Schema(description = "实时功率数据实体类")
    public static class RealPowerDto {

        /**
         * 日期列表
         */
        @Schema(description = "日期列表")
        private List<String> dateList = Lists.newArrayList();

        /**
         * 光伏总功率
         */
        @Schema(description = "光伏总功率")
        private Double pvTotalPower = 0.0;

        /**
         * 光伏发电量
         */
        @Schema(description = "光伏发电量")
        private Double pvQt = 0.0;

        /**
         * 光伏上网电量
         */
        @Schema(description = "光伏上网电量")
        private Double pvNetQt = 0.0;

        /**
         * 光伏消纳电量
         */
        @Schema(description = "光伏消纳电量")
        private Double pvConsumeQt = 0.0;

        /**
         * 光伏今日功率曲线列表
         */
        @Schema(description = "光伏今日功率曲线列表")
        private List<Double> pvDayPowerList = Lists.newArrayList();

        /**
         * 光伏昨日功率曲线列表
         */
        @Schema(description = "光伏昨日功率曲线列表")
        private List<Double> pvYestdayPowerList = Lists.newArrayList();

        /**
         * 储能总功率
         */
        @Schema(description = "储能总功率")
        private Double storageTotalPower = 0.0;

        /**
         * 储能充电量
         */
        @Schema(description = "储能充电量")
        private Double storageChargeQt = 0.0;

        /**
         * 储能放电量
         */
        @Schema(description = "储能放电量")
        private Double storageDischargeQt = 0.0;

        /**
         * 储能今日功率曲线列表
         */
        @Schema(description = "储能今日功率曲线列表")
        private List<Double> storageDayPowerList = Lists.newArrayList();

        /**
         * 储能昨日功率曲线列表
         */
        @Schema(description = "储能昨日功率曲线列表")
        private List<Double> storageYestdayPowerList = Lists.newArrayList();

        /**
         * 充电桩总功率
         */
        @Schema(description = "充电桩总功率")
        private Double pileTotalPower;

        /**
         * 充电桩充电量
         */
        @Schema(description = "充电桩充电量")
        private Double pileChargeQt = 0.0;

        /**
         * 充电桩充电次数
         */
        @Schema(description = "充电桩充电次数")
        private Integer pileChargeNum = 0;

        /**
         * 充电桩今日功率曲线列表
         */
        @Schema(description = "充电桩今日功率曲线列表")
        private List<Double> pileDayPowerList = Lists.newArrayList();

        /**
         * 充电桩昨日功率曲线列表
         */
        @Schema(description = "充电桩昨日功率曲线列表")
        private List<Double> pileYestdayPowerList = Lists.newArrayList();

        /**
         * 换电总功率
         */
        @Schema(description = "换电总功率")
        private Double totalChangePower = 0.0;

        /**
         * 换电充电量
         */
        @Schema(description = "换电充电量")
        private Double changeChargeQt = 0.0;

        /**
         * 换电耗电量
         */
        @Schema(description = "换电耗电量")
        private Double changeUseQt = 0.0;

        /**
         * 换电今日功率曲线列表
         */
        @Schema(description = "换电今日功率曲线列表")
        private List<Double> changeDayPowerList = Lists.newArrayList();

        /**
         * 换电昨日功率曲线列表
         */
        @Schema(description = "换电昨日功率曲线列表")
        private List<Double> changeYestdayPowerList = Lists.newArrayList();

    }

    @Data
    @Schema(description = "设备状态数据实体类")
    public static class DeviceStatusDto {

        /**
         * 类型 1-光伏 2-储能 3-充电桩 6-换电
         */
        @Schema(description = "类型 1-光伏 2-储能 3-充电桩 6-换电")
        private Integer type;

        /**
         * 正常数量
         */
        @Schema(description = "正常数量")
        private Integer normalNum = 0;

        /**
         * 故障数量
         */
        @Schema(description = "故障数量")
        private Integer errorNum = 0;

        /**
         * 离线数量
         */
        @Schema(description = "离线数量")
        private Integer offlineNum = 0;

    }

    @Data
    @Schema(description = "充电桩运营返回实体类")
    public static class PileOperationDto {

        /**
         * 充电桩总充电量
         */
        @Schema(description = "充电桩总充电量")
        private Double totalChargeQt = 0.0;

        /**
         * 充电桩本月充电次数
         */
        @Schema(description = "充电桩本月充电次数")
        private Integer monthChargeNum = 0;

        /**
         * 较上月次数比
         */
        @Schema(description = "较上月比较次数")
        private Integer monthNumCompare = 0;

        /**
         * 充电桩日均枪效(kWh/枪/天) 充电量/枪数/本月天数
         */
        @Schema(description = "充电桩日均枪效")
        private Double dayChargeEfficiency = 0.0;

        /**
         * 较上月日均枪数比
         */
        @Schema(description = "较上月日均枪数比")
        private Double dayEfficiencyCompare = 0.0;

        /**
         * 充电桩充电量列表
         */
        @Schema(description = "充电桩充电量列表")
        private List<Double> chargeQtList = new ArrayList<>();

    }

    @Data
    @Schema(description = "换电运营返回实体类")
    public static class ChangeOperationDto {

        /**
         * 换电总充电量
         */
        @Schema(description = "换电总充电量")
        private Double totalChargeQt = 0.0;

        /**
         * 换电本月换电次数
         */
        @Schema(description = "换电本月换电次数")
        private Integer monthChargeNum = 0;

        /**
         * 较上月次数比
         */
        @Schema(description = "较上月比较次数")
        private Integer monthNumCompare = 0;

        /**
         * 换电日均换电里程(km)
         */
        @Schema(description = "换电日均换电里程(km)")
        private Double dayKm = 0.0;

        /**
         * 较上月日均换电里程比
         */
        @Schema(description = "较上月日均换电里程比")
        private Double dayKmCompare = 0.0;

        /**
         * 换电充电量列表
         */
        @Schema(description = "换电充电量列表")
        private List<Double> chargeQtList = new ArrayList<>();

    }

    @Data
    @Schema(description = "光伏运营返回实体类")
    public static class PvOperationDto {

        /**
         * 光伏总发电量
         */
        @Schema(description = "光伏总发电量")
        private Double totalQt = 0.0;

        /**
         * 光伏本月发电量
         */
        @Schema(description = "光伏本月发电量")
        private Double monthQt = 0.0;

        /**
         * 光伏本月消纳电量
         */
        @Schema(description = "光伏本月消纳电量")
        private Double monthConsumeQt = 0.0;

        /**
         * 光伏本月消纳率
         */
        @Schema(description = "光伏本月消纳率")
        private Double montConsumeRate = 0.0;

        /**
         * 光伏日均等效发电时长(小时/天)
         */
        @Schema(description = "光伏日均等效发电时长(小时/天)")
        private Double dayEffectiveTime = 0.0;

        /**
         * 较上月日均等效发电时长比
         */
        @Schema(description = "较上月日均等效发电时长比")
        private Double dayEffectiveCompare = 0.0;

        /**
         * 光伏系统效率PR
         */
        @Schema(description = "光伏系统效率PR")
        private Double systemEfficiency = 0.0;

        /**
         * 较上月系统效率比
         */
        @Schema(description = "较上月系统效率比")
        private Double systemEfficiencyCompare = 0.0;

        /**
         * 光伏实际发电量列表
         */
        @Schema(description = "光伏实际发电量列表")
        private List<Double> qtList = Lists.newArrayList();

        /**
         * 光伏实际发电量去年同期列表
         */
        @Schema(description = "光伏实际发电量去年同期列表")
        private List<Double> qtLastYearList = Lists.newArrayList();

        /**
         * 二氧化碳(CO2)减排量(kg)
         */
        @Schema(description = "二氧化碳(CO2)减排量（kg）")
        private Double co2Reduction = 0.0;

        /**
         * 节约标准煤量(kg)
         */
        @Schema(description = "节约标准煤量（kg）")
        private Double standardCoalReduction = 0.0;

        /**
         * 等效植树量(颗)
         */
        @Schema(description = "等效植树量（颗）")
        private Double treeReduction = 0.0;

    }

    @Data
    @Schema(description = "储能运营返回实体类")
    public static class StorageOperationDto {

        /**
         * 储能总充电量
         */
        @Schema(description = "储能总充电量")
        private Double chargeTotalQt = 0.0;

        /**
         * 储能总放电量
         */
        @Schema(description = "储能总放电量")
        private Double dischargeTotalQt = 0.0;

        /**
         * 储能当前可充电量
         */
        @Schema(description = "储能当前可充电量")
        private Double chargeCurrentQt = 0.0;

        /**
         * 储能当前可放电量
         */
        @Schema(description = "储能当前可放电量")
        private Double dischargeCurrentQt = 0.0;

        /**
         * 储能本月充放循环次数
         */
        @Schema(description = "储能本月充放循环次数")
        private Double monthTimes = 0.0;

        /**
         * 较上月本月充放循环次数比
         */
        @Schema(description = "较上月本月充放循环次数比")
        private Double monthTimesCompare;

        /**
         * 储能本月综合效率
         */
        @Schema(description = "储能本月综合效率")
        private Double monthEfficiency = 0.0;

        /**
         * 较上月本月综合效率比
         */
        @Schema(description = "较上月本月综合效率比")
        private Double monthEfficiencyCompare = 0.0;

        /**
         * 储能充电量列表
         */
        @Schema(description = "储能充电量列表")
        private List<Double> chargeQtList = Lists.newArrayList();

        /**
         * 储能放电量列表
         */
        @Schema(description = "储能放电量列表")
        private List<Double> dischargeQtList = Lists.newArrayList();

    }



}
