package com.sunmax.together.dto.operation.order;

import com.sunmax.common.dto.PageDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@ApiModel(value = "OccupyPileRecordListDto", description = "占桩订单记录列表返回实体类")
public class OccupyPileRecordListDto {

    /**
     * 累计时长
     */
    @ApiModelProperty("累计时长")
    private String sumDuration;

    /**
     * 累计金额
     */
    @ApiModelProperty("累计金额")
    private BigDecimal sumCost;

    /**
     * 占桩订单记录列表
     */
    @ApiModelProperty("占桩订单记录列表")
    private List<OccupyPileRecordData> occupyPileRecordDataList;

    /**
     * 分页占桩订单记录列表
     */
    @ApiModelProperty("分页占桩订单记录列表")
    private PageDto<OccupyPileRecordData> occupyPileRecordDataPage;

    /**
     * 占桩订单列表信息
     */
    @Data
    public static class OccupyPileRecordData {
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
         * 充电订单号
         */
        @ApiModelProperty("充电订单号")
        private String orderNum;

        /**
         * 用户手机号
         */
        @ApiModelProperty("用户手机号")
        private String phoneNum;

        /**
         * 所属订单记录id
         */
        @ApiModelProperty("所属订单记录id")
        private String orderId;

        /**
         * 订单状态 1-在途 2-待支付 3-已完成 9-异常
         */
        @ApiModelProperty("订单状态 1-在途 2-待支付 3-已完成 9-异常")
        private Integer occupyState;

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
         * 运营商名称
         */
        @ApiModelProperty(value = "运营商名称")
        private String operateUnitName;

        /**
         * 运营商id
         */
        @ApiModelProperty(value = "运营商id")
        private String operateUnitId;

        /**
         * 站点名称
         */
        @ApiModelProperty(value = "站点名称")
        private String siteName;

        /**
         * 站点id
         */
        @ApiModelProperty(value = "站点id")
        private String siteId;

        /**
         * 车牌号
         */
        @ApiModelProperty("车牌号")
        private String plateNumber;

        /**
         * 平台名称
         */
        @ApiModelProperty(value = "平台名称")
        private String platformName;
    }
}
