package com.sunmax.together.dto.operation.order;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@ApiModel(value = "OrderRecordInfoDto", description = "订单记录信息返回实体类")
public class OrderRecordInfoDto {

    /**
     * 唯一id
     */
    @ApiModelProperty("唯一id")
    private String id;

    /**
     * 所属站点id
     */
    @ApiModelProperty("所属站点id")
    private String siteId;

    /**
     * 是否有序 1-是 2-否
     */
    @ApiModelProperty("是否有序 1-是 2-否")
    private Integer isOrderly;

    /**
     * 订单号
     */
    @ApiModelProperty("订单号")
    private String orderNum;

    /**
     * 预付金额
     */
    @ApiModelProperty("预付金额")
    private BigDecimal prepayMoney;

    /**
     * 订单状态 0-未进行 1-充电中 2-充电完成 3-启动失败 4-异常 5-订单取消 6-预约中
     */
    @ApiModelProperty("订单状态 0-未进行 1-充电中 2-充电完成 3-启动失败 4-异常 5-订单取消 6-预约中")
    private Integer orderStatus;

    /**
     * 充放电方式 0-立即充电 1-定时充电 2-自动充电
     */
    @ApiModelProperty("充放电方式 0-立即充电 1-定时充电 2-自动充电")
    private Integer type;

    /**
     * 定时充放电时间
     */
    @ApiModelProperty("定时充放电时间")
    private LocalDateTime clockingTime;

    /**
     * 充电桩编号
     */
    @ApiModelProperty("充电桩编号")
    private String pileCode;

    /**
     * 充电枪编号
     */
    @ApiModelProperty("充电枪编号")
    private Integer gunCode;

    /**
     * 启动方式 1-App 2-第三方平台 3-电卡 4-VIN码 5-电桩屏幕强制启动 6-有序控制、7-离线卡启动
     */
    @ApiModelProperty("启动方式 1-App 2-第三方平台 3-电卡 4-VIN码 5-电桩屏幕强制启动 6-有序控制、7-离线卡启动")
    private Integer starter;

    /**
     * 平台名称
     */
    @ApiModelProperty(value = "平台名称")
    private String platformName;

    /**
     * 开始充/放电时间
     */
    @ApiModelProperty("开始充/放电时间")
    private String startTime;

    /**
     * 结束充/放电时间
     */
    @ApiModelProperty("结束充/放电时间")
    private String endTime;

    /**
     * 本次充/放电总电量
     */
    @ApiModelProperty("本次充/放电总电量")
    private Double totalQt = 0.0;

    /**
     * 本次充/放电总费用
     */
    @ApiModelProperty("本次充/放电总费用")
    private BigDecimal totalCost = new BigDecimal("0.0");

    /**
     * 本次充/放电总电费
     */
    @ApiModelProperty("本次充/放电总电费")
    private BigDecimal totalElect = new BigDecimal("0.0");

    /**
     * 本次充/放电总服务费
     */
    @ApiModelProperty("本次充/放电总服务费")
    private BigDecimal totalFee = new BigDecimal("0.0");

    /**
     * 开始 SOC
     */
    @ApiModelProperty("开始 SOC")
    private Integer startSoc;

    /**
     * 结束 SOC
     */
    @ApiModelProperty("结束 SOC")
    private Integer endSoc;

    /**
     * 停止码
     */
    @ApiModelProperty("停止码")
    private String stopReason;

    /**
     * 停止详细原因
     */
    @ApiModelProperty("停止详细原因")
    private String stopDetailReason;

    /**
     * 账号类型 1-充/放电卡ID 2-VIN码 3-手机号
     */
    @ApiModelProperty("账号类型 1-充/放电卡ID 2-VIN码 3-手机号")
    private Integer accountType;

    /**
     * 账号数据
     */
    @ApiModelProperty("账号数据")
    private String accountData;

    /**
     * 卡面号
     */
    @ApiModelProperty("卡面号")
    private String cardNumber;

    /**
     * 创建时间
     */
    @ApiModelProperty("创建时间")
    private String createTime;

    /**
     * 更新时间
     */
    @ApiModelProperty("更新时间")
    private String updateTime;

    /**
     * 充电时长
     */
    @ApiModelProperty("充电时长")
    private String chargeDuration;

    /**
     * 车量vin码
     */
    @ApiModelProperty("车量vin码")
    private String busVin;

    /**
     * 开始直流电表读数
     */
    @ApiModelProperty("开始直流电表读数")
    private Double startDirMeter;

    /**
     * 结束直流电表读数
     */
    @ApiModelProperty("结束直流电表读数")
    private Double endDirMeter;

    /**
     * 电桩信息
     */
    @ApiModelProperty("电桩信息")
    private OrderRecordInfoDto.PileInfoData pileInfoData;

    /**
     * 用户信息
     */
    @ApiModelProperty("用户信息")
    private OrderRecordInfoDto.UserRecordDto userRecordDto;

    /**
     * 补单记录信息
     */
    @ApiModelProperty("补单记录信息")
    private OrderRecordInfoDto.RepairOrderRecord repairOrderRecord;

    /**
     * 结算记录信息
     */
    @ApiModelProperty("结算记录信息")
    private OrderRecordInfoDto.SettlementRecord settlementRecord;

    /**
     * 计费详情列表
     */
    @ApiModelProperty("计费详情列表")
    private List<OrderRecordInfoDto.ChargingDetails> chargingDetailsList;

    /**
     * 退款记录列表
     */
    @ApiModelProperty("退款记录列表")
    private List<OrderRecordInfoDto.RefundRecord> refundRecordList;

    /**
     * 电桩信息
     */
    @Data
    public static class PileInfoData {

        /**
         * 电站名称
         */
        @ApiModelProperty("电站名称")
        private String siteName;

        /**
         * 电站id
         */
        @ApiModelProperty("电站id")
        private String siteId;

        /**
         * 运营商名称
         */
        @ApiModelProperty("运营商名称")
        private String operatorName;

        /**
         * 所在省份
         */
        @ApiModelProperty(value = "所在省份")
        private String province;

        /**
         * 所在市
         */
        @ApiModelProperty(value = "所在市")
        private String city;

        /**
         * 所在区县
         */
        @ApiModelProperty(value = "所在区县")
        private String county;

        /**
         * 详细地址
         */
        @ApiModelProperty(value = "详细地址")
        private String address;

        /**
         * 电桩类型 5-交流 6-直流 7-V2G
         */
        @ApiModelProperty("电桩类型 5-交流 6-直流 7-V2G")
        private Integer pileType;

        /**
         * 设备出厂编码
         */
        @ApiModelProperty("设备出厂编码")
        private String factoryCode;
    }

    /**
     * 用户记录信息
     */
    @Data
    public static class UserRecordDto {

        /**
         * 唯一id
         */
        @ApiModelProperty("唯一id")
        private String id;

        /**
         * 企业账户
         */
        @ApiModelProperty("企业账户")
        private String enterpriseAccount;

        /**
         * 车队名称
         */
        @ApiModelProperty("车队名称")
        private String fleetName;

        /**
         * 车牌号
         */
        @ApiModelProperty("车牌号")
        private String plateNumber;

        /**
         * 开票状态 1-已开发票 2-未开发票
         */
        @ApiModelProperty("开票状态 1-已开发票 2-未开发票")
        private Integer invoicingState;
    }

    /**
     * 计费详情信息
     */
    @Data
    public static class ChargingDetails {

        /**
         * 主键id
         */
        @ApiModelProperty("主键id")
        private String id;

        /**
         * 时段电价
         */
        @ApiModelProperty("时段电价")
        private BigDecimal electPrice;

        /**
         * 时段服务费价
         */
        @ApiModelProperty("时段服务费价")
        private BigDecimal servicePrice;

        /**
         * 充/放电量
         */
        @ApiModelProperty("充/放电量")
        private Double rechargeQt;

        /**
         * 电费
         */
        @ApiModelProperty("电费")
        private BigDecimal electMoney;

        /**
         * 服务费
         */
        @ApiModelProperty("服务费")
        private BigDecimal serviceMoney;

        /**
         * 充电时长（分钟）
         */
        @ApiModelProperty("充电时长（分钟）")
        private String chargeDuration;

        /**
         * 时段类型 1-尖时 2-峰时 3-平时 4-谷时 6-全天
         */
        @ApiModelProperty("时段类型 1-尖时 2-峰时 3-平时 4-谷时 6-全天")
        private Integer periodType;
    }

    /**
     * 补单记录信息
     */
    @Data
    public static class RepairOrderRecord {

        /**
         * 主键id
         */
        @ApiModelProperty("主键id")
        private String id;

        /**
         * 补单状态 0-挂单 1-自动恢复 2-人工恢复
         */
        @ApiModelProperty("补单状态 0-挂单 1-自动恢复 2-人工恢复")
        private Integer repairStatus;

        /**
         * 异常时长
         */
        @ApiModelProperty("异常时长")
        private String exceptionTime;

        /**
         * 补单操作人
         */
        @ApiModelProperty("补单操作人")
        private String repairOperator;

        /**
         * 创建时间
         */
        @ApiModelProperty("创建时间")
        private String createTime;

        /**
         * 修改时间
         */
        @ApiModelProperty("修改时间")
        private String updateTime;
    }

    /**
     * 结算记录信息
     */
    @Data
    public static class SettlementRecord {

        /**
         * 主键id
         */
        @ApiModelProperty("主键id")
        private String id;

        /**
         * 结算状态 0-未结算 1-结算关闭 2-结算失败 3-结算成功
         */
        @ApiModelProperty("结算状态 0-未结算 1-结算关闭 2-结算失败 3-结算成功")
        private Integer settlementState;

        /**
         * 支付方式 1-免支付 2-微信支付 3-支付宝支付 4-钱包余额
         */
        @ApiModelProperty("支付方式 1-免支付 2-微信支付 3-支付宝支付 4-钱包余额")
        private Integer payWay;

        /**
         * 原价总金额
         */
        @ApiModelProperty("原价总金额")
        private BigDecimal originalCost = new BigDecimal("0.0");

        /**
         * 实付金额
         */
        @ApiModelProperty("实付金额")
        private BigDecimal actualTotalCost;

        /**
         * 实付电费
         */
        @ApiModelProperty("实付电费")
        private BigDecimal actualTotalElect;

        /**
         * 实付服务费
         */
        @ApiModelProperty("实付服务费")
        private BigDecimal actualTotalFee;

        /**
         * 电费减免
         */
        @ApiModelProperty("电费减免")
        private BigDecimal totalElectReduction = new BigDecimal("0.0");

        /**
         * 服务费减免
         */
        @ApiModelProperty("服务费减免")
        private BigDecimal totalFeeReduction = new BigDecimal("0.0");

        /**
         * 退款金额
         */
        @ApiModelProperty("退款金额")
        private BigDecimal refundMoney;

        /**
         * 优惠金额
         */
        @ApiModelProperty("优惠金额")
        private BigDecimal preferentialMoney;
    }

    /**
     * 退款记录信息
     */
    @Data
    public static class RefundRecord {

        /**
         * 主键id
         */
        @ApiModelProperty("主键id")
        private String id;

        /**
         * 退款时间
         */
        @ApiModelProperty("退款时间")
        private String createTime;

        /**
         * 退款操作人
         */
        @ApiModelProperty("退款操作人")
        private String refundOperator;

        /**
         * 退款金额
         */
        @ApiModelProperty("退款金额")
        private BigDecimal refundAmount;

        /**
         * 退款状态 1-正常退款 2-退款异常(结算记录表中实付金额-退款金额 < 0)
         */
        @ApiModelProperty("退款状态 1-正常退款 2-退款异常(结算记录表中实付金额-退款金额 < 0)")
        private Integer refundStatus;
    }

}
