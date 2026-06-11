package com.sunmax.together.dto.operation.order;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Schema(description = "占桩订单记录信息返回实体类")
public class OccupyPileRecordInfoDto {

    /**
     * 唯一id
     */
    @Schema(description = "唯一id")
    private String id;

    /**
     * 占桩订单号
     */
    @Schema(description = "占桩订单号")
    private String occupyNum;

    /**
     * 所属订单记录id
     */
    @Schema(description = "所属订单记录id")
    private String orderId;

    /**
     * 开始时间
     */
    @Schema(description = "开始时间")
    private String startTime;

    /**
     * 结束时间
     */
    @Schema(description = "结束时间")
    private String endTime;

    /**
     * 时长
     */
    @Schema(description = "时长")
    private String duration;

    /**
     * 占位订单金额
     */
    @Schema(description = "占位订单金额")
    private BigDecimal orderMoney;

    /**
     * 占位实付金额
     */
    @Schema(description = "占位实付金额")
    private BigDecimal paidMoney;

    /**
     * 充电桩编号
     */
    @Schema(description = "充电桩编号")
    private String pileCode;

    /**
     * 充电枪编号
     */
    @Schema(description = "充电枪编号")
    private Integer gunCode;

    /**
     * 订单状态 1-在途 2-待支付 3-已完成 9-异常
     */
    @Schema(description = "订单状态 1-在途 2-待支付 3-已完成 9-异常")
    private Integer occupyState;

    /**
     * 订单支付状态 1-已支付 2-未支付
     */
    @Schema(description = "订单支付状态 1-已支付 2-未支付")
    private Integer payState;

    /**
     * 支付时间
     */
    @Schema(description = "支付时间")
    private String payTime;

    /**
     * 车量vin码
     */
    @Schema(description = "车量vin码")
    private String busVin;

    /**
     * 用户手机号
     */
    @Schema(description = "用户手机号")
    private String phoneNum;

    /**
     * 电桩信息
     */
    @Schema(description = "电桩信息")
    private PileInfoData pileInfoData;

    /**
     * 用户信息
     */
    @Schema(description = "用户信息")
    private UserRecordDto userRecordDto;

    /**
     * 计费详情列表
     */
    @Schema(description = "计费详情列表")
    private List<ChargingDetails> chargingDetailsList;

    /**
     * 电桩信息
     */
    @Data
    public static class PileInfoData {

        /**
         * 电站名称
         */
        @Schema(description = "电站名称")
        private String siteName;

        /**
         * 电站id
         */
        @Schema(description = "电站id")
        private String siteId;

        /**
         * 运营商名称
         */
        @Schema(description = "运营商名称")
        private String operatorName;

        /**
         * 所在省份
         */
        @Schema(description = "所在省份")
        private String province;

        /**
         * 所在市
         */
        @Schema(description = "所在市")
        private String city;

        /**
         * 所在区县
         */
        @Schema(description = "所在区县")
        private String county;

        /**
         * 详细地址
         */
        @Schema(description = "详细地址")
        private String address;

        /**
         * 电桩类型 5-交流 6-直流 7-V2G
         */
        @Schema(description = "电桩类型 5-交流 6-直流 7-V2G")
        private Integer pileType;

        /**
         * 设备出厂编码
         */
        @Schema(description = "设备出厂编码")
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
        @Schema(description = "唯一id")
        private String id;

        /**
         * 企业账户
         */
        @Schema(description = "企业账户")
        private String enterpriseAccount;

        /**
         * 车队名称
         */
        @Schema(description = "车队名称")
        private String fleetName;

        /**
         * 车牌号
         */
        @Schema(description = "车牌号")
        private String plateNumber;

        /**
         * 开票状态 1-已开发票 2-未开发票
         */
        @Schema(description = "开票状态 1-已开发票 2-未开发票")
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
        @Schema(description = "主键id")
        private String id;

        /**
         * 计费时段
         */
        @Schema(description = "计费时段")
        private String tariffPeriod;

        /**
         * 免占桩时长(分钟)
         */
        @Schema(description = "免占桩时长(分钟)")
        private Integer avoidDuration;

        /**
         * 收费标准
         */
        @Schema(description = "收费标准")
        private String tariffStandard;
    }
}
