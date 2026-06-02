package com.sunmax.together.dto.operation.order;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@ApiModel(value = "OccupyPileRecordInfoDto", description = "占桩订单记录信息返回实体类")
public class OccupyPileRecordInfoDto {

    /**
     * 唯一id
     */
    @ApiModelProperty("唯一id")
    private String id;

    /**
     * 占桩订单号
     */
    @ApiModelProperty("占桩订单号")
    private String occupyNum;

    /**
     * 所属订单记录id
     */
    @ApiModelProperty("所属订单记录id")
    private String orderId;

    /**
     * 开始时间
     */
    @ApiModelProperty("开始时间")
    private String startTime;

    /**
     * 结束时间
     */
    @ApiModelProperty("结束时间")
    private String endTime;

    /**
     * 时长
     */
    @ApiModelProperty("时长")
    private String duration;

    /**
     * 占位订单金额
     */
    @ApiModelProperty("占位订单金额")
    private BigDecimal orderMoney;

    /**
     * 占位实付金额
     */
    @ApiModelProperty("占位实付金额")
    private BigDecimal paidMoney;

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
     * 订单状态 1-在途 2-待支付 3-已完成 9-异常
     */
    @ApiModelProperty("订单状态 1-在途 2-待支付 3-已完成 9-异常")
    private Integer occupyState;

    /**
     * 订单支付状态 1-已支付 2-未支付
     */
    @ApiModelProperty("订单支付状态 1-已支付 2-未支付")
    private Integer payState;

    /**
     * 支付时间
     */
    @ApiModelProperty("支付时间")
    private String payTime;

    /**
     * 车量vin码
     */
    @ApiModelProperty("车量vin码")
    private String busVin;

    /**
     * 用户手机号
     */
    @ApiModelProperty("用户手机号")
    private String phoneNum;

    /**
     * 电桩信息
     */
    @ApiModelProperty("电桩信息")
    private PileInfoData pileInfoData;

    /**
     * 用户信息
     */
    @ApiModelProperty("用户信息")
    private UserRecordDto userRecordDto;

    /**
     * 计费详情列表
     */
    @ApiModelProperty("计费详情列表")
    private List<ChargingDetails> chargingDetailsList;

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
         * 计费时段
         */
        @ApiModelProperty("计费时段")
        private String tariffPeriod;

        /**
         * 免占桩时长(分钟)
         */
        @ApiModelProperty("免占桩时长(分钟)")
        private Integer avoidDuration;

        /**
         * 收费标准
         */
        @ApiModelProperty("收费标准")
        private String tariffStandard;
    }
}
