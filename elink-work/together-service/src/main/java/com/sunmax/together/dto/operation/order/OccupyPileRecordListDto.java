package com.sunmax.together.dto.operation.order;

import com.sunmax.common.dto.PageDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Schema(description = "占桩订单记录列表返回实体类")
public class OccupyPileRecordListDto {

    /**
     * 累计时长
     */
    @Schema(description = "累计时长")
    private String sumDuration;

    /**
     * 累计金额
     */
    @Schema(description = "累计金额")
    private BigDecimal sumCost;

    /**
     * 占桩订单记录列表
     */
    @Schema(description = "占桩订单记录列表")
    private List<OccupyPileRecordData> occupyPileRecordDataList;

    /**
     * 分页占桩订单记录列表
     */
    @Schema(description = "分页占桩订单记录列表")
    private PageDto<OccupyPileRecordData> occupyPileRecordDataPage;

    /**
     * 占桩订单列表信息
     */
    @Data
    public static class OccupyPileRecordData {
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
         * 充电订单号
         */
        @Schema(description = "充电订单号")
        private String orderNum;

        /**
         * 用户手机号
         */
        @Schema(description = "用户手机号")
        private String phoneNum;

        /**
         * 所属订单记录id
         */
        @Schema(description = "所属订单记录id")
        private String orderId;

        /**
         * 订单状态 1-在途 2-待支付 3-已完成 9-异常
         */
        @Schema(description = "订单状态 1-在途 2-待支付 3-已完成 9-异常")
        private Integer occupyState;

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
         * 车牌号
         */
        @Schema(description = "车牌号")
        private String plateNumber;

        /**
         * 平台名称
         */
        @Schema(description = "平台名称")
        private String platformName;
    }
}
