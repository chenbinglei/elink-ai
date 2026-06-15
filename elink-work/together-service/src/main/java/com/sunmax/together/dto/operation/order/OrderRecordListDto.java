package com.sunmax.together.dto.operation.order;

import com.sunmax.common.dto.PageDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "订单记录列表返回实体类")
public class OrderRecordListDto {

    /**
     * 累计电量
     */
    @Schema(description = "累计电量")
    private Double sumQt = 0.0;

    /**
     * 累计金额
     */
    @Schema(description = "累计金额")
    private BigDecimal sumCost = new BigDecimal("0.0");

    /**
     * 实付金额
     */
    @Schema(description = "实付金额")
    private BigDecimal actualTotalCost = new BigDecimal("0.0");

    /**
     * 实付电费
     */
    @Schema(description = "实付电费")
    private BigDecimal actualTotalElect = new BigDecimal("0.0");

    /**
     * 实付服务费
     */
    @Schema(description = "实付服务费")
    private BigDecimal actualTotalFee = new BigDecimal("0.0");

    /**
     * 分页订单记录列表
     */
    @Schema(description = "分页订单记录列表")
    private PageDto<OrderRecordData> orderRecordDataPage;

    /**
     * 订单列表信息
     */
    @Data
    public static class OrderRecordData {

        /**
         * 唯一id
         */
        @Schema(description = "唯一id")
        private String id;

        /**
         * 订单状态 0-未进行 1-充电中 2-充电完成 3-启动失败 4-异常 5-订单取消 6-预约中
         */
        @Schema(description = "订单状态 0-未进行 1-充电中 2-充电完成 3-启动失败 4-异常 5-订单取消 6-预约中")
        private Integer orderStatus;

        /**
         * 订单号
         */
        @Schema(description = "订单号")
        private String orderNum;

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
         * 电桩类型 5-交流 6-直流 7-V2G
         */
        @Schema(description = "电桩类型 5-交流 6-直流 7-V2G")
        private Integer pileType;

        /**
         * 运营商名称
         */
        @Schema(description = "运营商名称")
        private String operateUnitName;

        /**
         * 运营商id
         */
        @Schema(description = "运营商id")
        private String operateUnitId;

        /**
         * 站点名称
         */
        @Schema(description = "站点名称")
        private String siteName;

        /**
         * 站点id
         */
        @Schema(description = "站点id")
        private String siteId;

        /**
         * 开始充/放电时间
         */
        @Schema(description = "开始充/放电时间")
        private String startTime;

        /**
         * 结束充/放电时间
         */
        @Schema(description = "结束充/放电时间")
        private String endTime;

        /**
         * 充/放电时长
         */
        @Schema(description = "充/放电时长")
        private String duration;

        /**
         * 充/放电小时数
         */
        @Schema(description = "充/放电小时数")
        private Double hourDuration;

        /**
         * 本次充/放电总电量
         */
        @Schema(description = "本次充/放电总电量")
        private Double totalQt = 0.0;

        /**
         * 实付金额
         */
        @Schema(description = "实付金额")
        private BigDecimal actualTotalCost = new BigDecimal("0.0");

        /**
         * 实付电费
         */
        @Schema(description = "实付电费")
        private BigDecimal actualTotalElect = new BigDecimal("0.0");

        /**
         * 实付服务费
         */
        @Schema(description = "实付服务费")
        private BigDecimal actualTotalFee = new BigDecimal("0.0");

        /**
         * 本次充/放电总费用
         */
        @Schema(description = "本次充/放电总费用")
        private BigDecimal totalCost = new BigDecimal("0.0");

        /**
         * 本次充/放电总电费
         */
        @Schema(description = "本次充/放电总电费")
        private BigDecimal totalElect = new BigDecimal("0.0");

        /**
         * 本次充/放电总服务费
         */
        @Schema(description = "本次充/放电总服务费")
        private BigDecimal totalFee = new BigDecimal("0.0");

        /**
         * 车牌号
         */
        @Schema(description = "车牌号")
        private String plateNumber;

        /**
         * 补单状态 0-挂单 1-自动恢复 2-人工恢复 3-正常
         */
        @Schema(description = "补单状态 0-挂单 1-自动恢复 2-人工恢复 3-正常")
        private Integer repairStatus;

        /**
         * 平台名称
         */
        @Schema(description = "平台名称")
        private String platformName;

        /**
         * 预付金额
         */
        @Schema(description = "预付金额")
        private BigDecimal prepayMoney;

        /**
         * 创建时间
         */
        @Schema(description = "创建时间")
        private String createTime;

        /**
         * 停止码
         */
        @Schema(description = "停止码")
        private String stopReason;

        /**
         * 停止详细原因
         */
        @Schema(description = "停止详细原因")
        private String stopDetailReason;

        /**
         * 异常类型码 0-无异常 1-时间异常：订单时长大于24h 2-大额订单：上报的订单总金额大于1000元 3-电量异常：电量大于500kwh 4-无效订单：电量小于1 5-费用异常：订单金额为0
         */
        @Schema(description = "异常类型码 0-无异常 1-时间异常：订单时长大于24h 2-大额订单：上报的订单总金额大于1000元 3-电量异常：电量大于500kwh 4-无效订单：电量小于1 5-费用异常：订单金额为0")
        private String abnormalCode;
    }
}
